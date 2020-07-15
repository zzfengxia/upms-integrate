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
        param: {
            status: true
        },
        tab: null // 表格对象
    },
    mounted: function() {
        // vue对象
        const _this = this;

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/system/config/list',

            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return 'key搜索';
            },

            // 表格渲染完成回调
            onPostBody: function () {
                bindCheckBoxForTable();
            },

            idField: "id",// 指定主键列
            columns: [
                {
                    field: 'checkbox',// json数据中rows数组中的属性名
                    checkbox: true,
                },
                {
                    title: '参数KEY',
                    field: 'paramKey',
                    sortable: true // 启用排序
                },
                {
                    title: '参数值',
                    field: 'paramValue'
                },
                {
                    title: '备注',
                    field: 'remark'
                },
                {
                    title: '状态',
                    field: 'status',
                    // 格式化数据，参数分别是：value该行的属性，row该行记录，index该行下标
                    formatter: function (value, row, index) {
                        return value === true ? '<span class="badge badge-success">正常</span>' : '<span class="badge badge-danger">禁用</span>';
                    }
                },
                {
                    title: '创建时间',
                    field: 'cTime',
                    sortable: true // 启用排序
                },
                {
                    title: '更新时间',
                    field: 'mTime',
                    sortable: true // 启用排序
                },
                {
                    title: '操作',
                    field: 'id',
                    align: 'center',
                    formatter: function (value, row, index) {
                        let btn = '';
                        // vue已经渲染，无法再解析vue指令
                        if(_this.permCheck()) {
                            btn = '<button class="btn btn-success btn-mini" name="updateBtn" onclick="update(' + value + ')"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</button>';
                        }
                        return btn;
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
                ids.push(val.paramKey);
            });

            layer.confirm('确定要删除选中的参数项', {
                icon: 3
            }, function (index) {
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/system/config/delete",
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
        getParam: function (id) {
            $.get(baseURL + "/system/config/info/" + id, function (r) {
                vm.param = r.data;
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
        openWin: function() {
            // 渲染弹窗
            vm.show = true;
            // 绑定表单校验
            $('#paramsForm').validate();

            layer.open({
                type: 1,
                offset: '20px',
                //skin: 'layui-layer-molv',
                title: vm.isUpdate ? "编辑" : "新增",
                area: ['420px', '450px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 保存
                    if($('#paramsForm').valid()) {
                        const opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/system/config/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.param),
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
                    // 清空param
                    if (vm.isUpdate) {
                        vm.param = {
                            status: true
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
    vm.getParam(id);

    vm.openWin();

    stopBubble();
}