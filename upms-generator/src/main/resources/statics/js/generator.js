// 导入layer
let layer = null;
layui.use('layer', function() {
    layer = layui.layer;
});

var vm = new Vue({
    el: '#app',
    data: {
        show: false, // 是否渲染弹窗
        table: {
        },
        tab: null // 表格对象
    },
    mounted: function() {
        // vue对象
        let _this = this;

        if(!this.checkConfig()) {
            alert('请先配置并保存数据源');
            top.window.location.hash = "#config.html";
            return;
        }

        this.tab = $("#bt-table").customBootstrapTable({
            url: 'generator/tableList',
            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return '表名搜索';
            },

            // 表格渲染完成回调
            onPostBody: function () {
                bindCheckBoxForTable();
            },

            idField: "tableName",// 指定主键列
            columns: [
                {
                    field: 'checkbox',// json数据中rows数组中的属性名
                    checkbox: true,
                },
                {
                    title: '表名',
                    field: 'tableName',
                    sortable: true // 启用排序
                },
                {
                    title: 'Engine',
                    field: 'engine',
                },
                {
                    title: '表备注',
                    field: 'tableComment',
                },
                {
                    title: '表创建时间',
                    field: 'createTime',
                },
                {
                    title: '操作',
                    field: 'id',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "";
                    }
                }
            ]
        });
    },
    methods: {
        generate: function () {
            let rows = selectRows();

            if (rows == null || rows.length < 1) {
                return;
            }
            // 获取id
            let tables = [];
            each(rows, function(i, val) {
                tables.push(val.tableName);
            });

            layer.confirm('确定要为所选表生成代码', {icon: 3}, function (index) {
                layer.close(index);

                if(vm.checkConfig()) {
                    downloadWithPost("generator/generate", JSON.stringify(tables));
                    vm.reload();
                } else {
                    layer.alert('请先设置并保存相关参数');
                }
            });
        },
        checkConfig: function() {
            let flag = false;

            // 设置为同步操作
            $.ajaxSetup({
                async : false
            });

            $.get("generator/checkConfig", function(r) {
                if(!!r && r.data === true) {
                    flag = true;
                }
            });

            return flag;
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
            $('#userForm').validate();

            layer.open({
                type: 1,
                offset: '20px',
                //skin: 'layui-layer-molv',
                title: vm.isUpdate ? "编辑用户" : "新增用户",
                area: ['500px', '650px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 保存
                    if($('#userForm').valid()) {
                        let opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/admin/user/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.user),
                            success: function(r) {
                                if(r.status === "0") {
                                    vm.reload(index);
                                } else {
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
                    // 隐藏表单
                    vm.show = false;
                }
            });
        }
    }
});