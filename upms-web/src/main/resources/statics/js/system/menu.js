// 导入layer
let layer = null;
layui.use('layer', function() {
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
            url:"nourl",
            name: "viewName"
        }
    }
};

var vm = new Vue({
    el: '#app',
    data: {
        show: false, // 是否渲染弹窗
        isUpdate: false,
        menu: {
            viewName: null,
            type: 0,
            showFlag: true,
            parent: {
                id: 0,
                viewName: null,
            }
        },
        tab: null, // 表格对象
        ztree: null, // ztree对象
    },
    mounted: function() {
        // vue对象
        const _this = this;

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/menu/list',

            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return '菜单名搜索';
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
                    title: '菜单名称',
                    field: 'viewName',
                    sortable: true // 启用排序
                },
                {
                    title: '上级菜单',
                    field: 'parent.viewName',
                },
                {
                    title: '类型',
                    field: 'type',
                    sortable: true, // 启用排序
                    formatter: function (value, row, index) {
                        if(!row.showFlag) {
                            return '<span class="badge badge-secondary">权限</span>';
                        } else {
                            if(value === 0){
                                return '<span class="badge badge-primary">目录</span>';
                            }
                            if(value === 1){
                                return '<span class="badge badge-success">菜单</span>';
                            }
                            if(value === 2){
                                return '<span class="badge badge-warning">按钮</span>';
                            }
                        }

                    }
                },
                {
                    title: '图标',
                    field: 'iconClass',
                    align: 'center',
                    formatter: function(value, item, index) {
                        return value == null ? '' : '<i class="' + value + ' fa-lg"></i>';
                    }
                },
                {
                    title: '请求URL',
                    field: 'url',
                },
                {
                    title: '权限标识',
                    field: 'perm',
                },
                {
                    title: '排序号',
                    field: 'idx',
                },
                /*{
                    title: '状态',
                    field: 'status',
                    // 格式化数据，参数分别是：value该行的属性，row该行记录，index该行下标
                    formatter: function (value, row, index) {
                        return value === "1" ? '<span class="badge badge-success">正常</span>' : '<span class="badge badge-danger">禁用</span>';
                    }
                },*/
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
            const rows = selectRows();

            if (rows == null || rows.length < 1) {
                return;
            }
            // 获取id
            const ids = [];
            each(rows, function(i, val) {
                ids.push(val.id);
            });

            layer.confirm('确定要删除选中的菜单', {
                btn: ['级联删除', '确定', '取消'],
                icon: 3
            }, function (index) {
                // 按钮1 级联删除
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/menu/delete?cascading=true",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.status === "0") {
                            vm.reload();
                        } else {
                            layer.alert(r.message);
                        }
                    }
                });
            }, function (index) {
                // 按钮2
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/menu/delete?cascading=false",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.status === "0") {
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
        getMenu: function (id) {
            $.get(baseURL + "/menu/info/" + id, function (r) {
                vm.menu = r.data;
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
            $.get(baseURL + "/menu/allMenu", function(r) {
                vm.ztree = $.fn.zTree.init($("#menuTree"), setting, r.data);
                const node = vm.ztree.getNodeByParam("id", vm.menu.parent.id);
                vm.ztree.selectNode(node);
            })
        },
        menuTree: function() {
            layer.open({
                type: 1,
                //offset: ['100px', '50px'],
                //skin: 'layui-layer-molv',
                title: "选择菜单",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#menuLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    const node = vm.ztree.getSelectedNodes();
                    // 赋值上级菜单
                    vm.menu.parent.id = node[0].id;
                    vm.menu.parent.viewName = node[0].viewName;

                    layer.close(index);
                }
            });
        },
        openWin: function() {
            // 渲染弹窗
            vm.show = true;
            // 绑定表单校验
            $('#menuForm').validate();
            // 获取菜单
            vm.getMenus();

            layer.open({
                type: 1,
                offset: '20px',
                //skin: 'layui-layer-molv',
                title: vm.isUpdate ? "编辑菜单" : "新增菜单",
                area: ['500px', '650px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 保存
                    if($('#menuForm').valid()) {
                        const opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/menu/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.menu),
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
                    // 清空menu
                    if (vm.isUpdate) {
                        vm.menu = {
                            viewName: null,
                            type: 0,
                            showFlag: true,
                            parent: {
                                id: 0,
                                viewName: null,
                            }
                        }
                    }

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
    vm.getMenu(id);

    vm.openWin();

    stopBubble();
}