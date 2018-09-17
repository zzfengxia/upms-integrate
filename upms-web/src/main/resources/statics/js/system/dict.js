// 导入layer
let layer = null;
layui.use('layer', function() {
    layer = layui.layer;
});

var vm = new Vue({
    el: '#app',
    data: {
        show: false, // 是否渲染弹窗
        isUpdate: false,
        dict: {},
        tab: null, // 表格对象
    },
    mounted: function() {
        // vue对象
        const _this = this;

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/dict/list',

            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return '字典名/字典类型搜索';
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
                    title: '字典名称',
                    field: 'dictName'
                },
                {
                    title: '字典类型',
                    field: 'dictType',
                    sortable: true // 启用排序
                },
                {
                    title: '字典健',
                    field: 'dictKey'
                },
                {
                    title: '字典值',
                    field: 'dictValue',
                },
                {
                    title: '排序号',
                    field: 'dictOrd',
                },
                {
                    title: '备注',
                    field: 'remark',
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
            const rows = selectRows();

            if (rows == null || rows.length < 1) {
                return;
            }
            // 获取id
            const ids = [];
            each(rows, function(i, val) {
                ids.push(val.id);
            });

            layer.confirm('确定要删除选中的字典项', {icon: 3}, function (index) {
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/dict/delete",
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
        getDict: function(id) {
            $.get(baseURL + "/dict/info/" + id, function(r) {
                vm.dict = r.data;
            })
        },
        loadDictTypes: function() {
            $.get(baseURL + "/dict/allType", function(r) {
                if(!!r.data) {
                    let typeObj = [];
                    $.map(r.data, function(key, index) {
                        typeObj.push({value: key, data: ""});
                    });
                    // 自动补全插件
                    $('#dictType').autocomplete({
                        lookup: typeObj
                    });
                }
            });
        },
        openWin: function() {
            // 渲染弹窗
            vm.show = true;
            // 绑定表单校验
            $('#dictForm').validate();
            // 字典类型自动补全数据加载
            this.loadDictTypes();

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
                    if($('#dictForm').valid()) {
                        // 加载层
                        let loadIdx = layer.load(2);

                        const opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/dict/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.dict),
                            success: function(r) {
                                layer.close(loadIdx);
                                if(r.status === "0") {
                                    vm.reload(index);
                                }else {
                                    if(r.status === undefined) {
                                        layer.alert("网络异常或权限不足");
                                    } else {
                                        layer.alert(r.message);
                                    }
                                }
                            },
                            error: function() {
                                layer.close(loadIdx);
                                layer.alert("网络错误");
                            }
                        });
                    }
                },
                end: function() {
                    // 清空dict
                    if (vm.isUpdate) {
                        vm.dict = {}
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
    vm.getDict(id);

    vm.openWin();

    stopBubble();
}