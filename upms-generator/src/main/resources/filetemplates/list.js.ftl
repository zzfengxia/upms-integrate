// 导入layer
let layer = null;
layui.use('layer', function() {
    layer = layui.layer;
});

let vm = new Vue({
    el: '#app',
    data: {
        show: false,   // 是否渲染弹窗
        isUpdate: false,
        // 数据模型
        ${classInfo.className?uncap_first}: {

        },
        tab: null,     // 表格对象
    },
    mounted: function() {
        // vue对象
        const _this = this;

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/${moduleName}/${classInfo.className?uncap_first}/list',

            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // TODO 自定义搜索输入框提示语
                return '搜索';
            },

            // 表格渲染完成回调
            onPostBody: function () {
                // 绑定ICheck
                bindCheckBoxForTable();
            },

            idField: "${pkField.fieldName}",// 指定主键列
            columns: [
                {
                    // checkbox
                    field: 'checkbox',
                    checkbox: true,
                },
                <#list fields as col>
                {
                    title: '<#if (col.fieldDesc)??><#if col.fieldDesc?length gt 0>${col.fieldDesc}<#else>${col.fieldName}</#if><#else>${col.fieldName}</#if>',
                    field: '${col.fieldName}',
                    <#if col.priFalg>
                    sortable: true // 启用排序
                    </#if>
                },
                </#list>
                {
                    title: '操作',
                    field: '${pkField.fieldName}',
                    align: 'center',
                    formatter: function (value, row, index) {
                        // vue已经渲染，无法再解析vue指令
                        let updateBtn = '';
                        if(_this.uptPermCheck()) {
                            updateBtn = '<button class="btn btn-success btn-mini" name="updateBtn" onclick="update(' + value + ')"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</button>';
                        }
                        return updateBtn;
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
            // 获取${pkField.fieldName}
            const ${pkField.fieldName}s = [];
            each(rows, function(i, val) {
                ${pkField.fieldName}s.push(val.${pkField.fieldName});
            });

            layer.confirm('确定要删除选中的行', {icon: 3}, function (index) {
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/${moduleName}/${classInfo.className?uncap_first}/delete",
                    contentType: "application/json",
                    data: JSON.stringify(${pkField.fieldName}s),
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
        uptPermCheck: function() {
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
        get${classInfo.className}: function(id) {
            $.get(baseURL + "/${moduleName}/${classInfo.className?uncap_first}/info/" + id, function(r) {
                vm.${classInfo.className?uncap_first} = r.data;
            })
        },
        openWin: function() {
            // 渲染弹窗
            vm.show = true;
            // 绑定表单校验
            $('#${classInfo.className?uncap_first}Form').validate();

            layer.open({
                type: 1,
                offset: '20px',
                //skin: 'layui-layer-molv',
                title: vm.isUpdate ? "编辑" : "新增",
                area: ['500px', '650px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 保存
                    if($('#${classInfo.className?uncap_first}Form').valid()) {
                        // 加载层
                        let loadIdx = layer.load(2);

                        const opType = vm.isUpdate ? "update" : "create";
                        $.ajax({
                            type: "POST",
                            url: baseURL + "/${moduleName}/${classInfo.className?uncap_first}/save/" + opType,
                            contentType: "application/json",
                            data: JSON.stringify(vm.${classInfo.className?uncap_first}),
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
                    // 清空${classInfo.className?uncap_first}
                    if (vm.isUpdate) {
                        vm.${classInfo.className?uncap_first} = {}
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
    vm.get${classInfo.className}(id);

    vm.openWin();

    stopBubble();
}