// 导入layer
let layer = null;
layui.use(['layer'], function() {
    layer = layui.layer;
});

// ztree setting
const setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            name: "viewName"
        }
    },
    check: {
        enable: true,
        autoCheckTrigger: true,                 // 父子节点自动关联勾选
        chkboxType: { "Y": "ps", "N": "ps" }    // 父子节点关联
    }
};

const vm = new Vue({
    el: '#app',
    data: {
        show: false, // 是否渲染弹窗
        isUpdate: false,
        role: {
            rolename: null,
            permIds: []
        },
        tab: null, // 表格对象
        ztree: null, // ztree对象
        state: true    // 是否展示菜单树
    },
    mounted: function() {
        // vue对象
        let _this = this;

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/role/list',

            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return '角色名搜索';
            },

            // 表格渲染完成回调
            onPostBody: function () {
                // 判断是否有修改权限
                if(!_this.permCheck()) {
                    $("button[name='updateBtn']").remove();
                }

                bindCheckBoxForTable();
            },

            idField: "id",// 指定主键列
            columns: [
                {
                    field: 'checkbox',// json数据中rows数组中的属性名
                    checkbox: true,
                },
                {
                    title: '角色名称',
                    field: 'rolename',
                    sortable: true // 启用排序
                },
                {
                    title: '角色描述',
                    field: 'roleintro',
                },
                {
                    title: '创建时间',
                    field: 'cTime',
                },
                {
                    title: '更新时间',
                    field: 'mTime',
                },
                {
                    title: '操作',
                    field: 'id',
                    align: 'center',
                    formatter: function (value, row, index) {
                        // vue已经渲染，无法再解析vue指令
                        return '<button class="btn btn-success btn-mini" name="updateBtn" onclick="update(' + value + ')"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</button>';
                    }
                }
            ]
        });
    },
    methods: {
        create: function () {
            // 新增弹窗
            vm.isUpdate = false;

            this.openWin();
        },
        del: function () {
            let rows = selectRows();

            if (rows == null || rows.length < 1) {
                return;
            }
            // 获取id
            let ids = [];
            each(rows, function(i, val) {
                ids.push(val.id);
            });

            layer.confirm('确定要删除选中的角色', function (index) {
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/role/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r) {
                        if(r.status === "0") {
                            vm.reload();
                        } else {
                            layer.alert(r.message);
                        }
                    }
                });
            });
        },
        permCheck: function() {
            // 更新权限检查
            let flag = this.$refs.uptPermInput && this.$refs.uptPermInput.value;

            return !!flag;
        },
        getRole: function (id, callback) {
            // 使用回调解决数据加载顺序问题
            $.get(baseURL + "/role/info/" + id, function (r) {
                vm.role = r.data;
                if(callback !== undefined) {
                    callback.apply();
                }
            });
        },
        reload: function (index) {
            layer.msg('操作成功');
            // 关闭窗口
            if(!!index) {
                layer.close(index);
            }

            // 刷新表格
            if(!!this.tab) {
                this.tab.bootstrapTable("refresh");
            }
        },
        getMenus: function(menuId) {
            //加载菜单树
            $.get(baseURL + "/menu/all", function(r) {
                vm.ztree = $.fn.zTree.init($("#menuTree"), setting, r.data);
                // 展开节点
                vm.ztree.expandAll(true);
                //勾选角色所拥有的菜单
                let perms = vm.role.permIds;
                if(!!perms) {
                    for(let p in perms) {
                        let node = vm.ztree.getNodeByParam("id", perms[p]);
                        vm.ztree.checkNode(node, true, false);
                    }
                }
            })
        },
        toggle: function() {
            vm.state = $('#permToggle').bootstrapSwitch('state');
        },
        openWin: function() {
            // 渲染弹窗
            vm.show = true;
            // 绑定switch
            $('#permToggle').bootstrapSwitch({
                onSwitchChange: function() {
                    vm.toggle();
                }
            });
            // 绑定表单校验
            $('#roleForm').validate();
            // 获取菜单
            vm.getMenus();

            layer.open({
                type: 1,
                offset: '20px',
                //skin: 'layui-layer-molv',
                title: vm.isUpdate ? "修改授权" : "新增角色",
                area: ['500px', '650px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 获取选择的菜单/权限
                    let nodes = vm.ztree.getCheckedNodes(true);
                    let menuIdList = [];
                    nodes.forEach(function(v, index) {
                        menuIdList.push(v.id)
                    });

                    vm.role.permIds = menuIdList;
                    // 保存
                    if($('#roleForm').valid()) {
                        let opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/role/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.role),
                            success: function(r) {
                                if(r.status === "0") {
                                    vm.reload(index);
                                }else {
                                    if(r.status === undefined) {
                                        layer.alert("网络异常或权限不足");
                                    } else {
                                        layer.alert(r.message);
                                    }
                                }
                            }
                        });
                    }
                },
                end: function() {
                    // 清空role
                    if (vm.isUpdate) {
                        vm.role = {
                            rolename: null,
                            permIds: []
                        };
                        vm.ztree = null;
                    }
                    $('#permToggle').bootstrapSwitch("state", true);
                    vm.state = true;
                    // 隐藏表单
                    vm.show = false;
                }
            });
        }
    }
});

function update(id) {
    // 修改弹窗
    vm.isUpdate = true;
    vm.getRole(id, vm.openWin);

    stopBubble();
}