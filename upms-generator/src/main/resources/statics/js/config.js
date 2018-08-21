let layer = null;
layui.use('layer', function () {
    layer = layui.layer;
});
const vm = new Vue({
    el: '#app',
    data: {
        config: {
            dataSource: {
                driverName: 'com.mysql.jdbc.Driver',
                dataSourceUrl: 'jdbc:mysql://192.168.1.208/sptsm-pms?useUnicode=true&characterEncoding=utf-8',
                username: 'answer',
                password: 'answer'
            },
            enableLombok: true,
            namingConversion: 'camelCase',           // 命名转换，默认驼峰式
            removePrefix: true,                      // 去除表名前缀
        }
    },
    methods: {
        test: function () {
            // 表单元素必须要有name
            if($('#dsForm').valid()) {
                // 加载层
                let loadIdx = layer.load(2);
                $.ajax({
                    type: "POST",
                    url: "generator/connectTest",
                    timeout: 5000,
                    contentType: "application/json",
                    data: JSON.stringify(vm.config.dataSource),
                    success: function (r) {
                        layer.close(loadIdx);
                        if (r.status === "0") {
                            layer.alert('连接成功');
                        } else {
                            layer.alert(r.message);
                        }
                    },
                    error: function() {
                        layer.close(loadIdx);
                        layer.alert('连接失败');
                    }
                });
            }
        },
        save: function () {
            if($('#dsForm').valid() && $('#configForm').valid()) {
                $.ajax({
                    type: "POST",
                    url: "generator/config",
                    contentType: "application/json",
                    data: JSON.stringify(vm.config),
                    success: function (r) {
                        layer.msg('参数保存成功');
                        if (r.status === "0") {
                            layer.msg('参数保存成功');
                        } else {
                            layer.alert(r.message);
                        }
                    }
                });
            }
            return false;
        },
        reset: function () {
            this.config = {
                dataSource: {
                    driverName: 'com.mysql.jdbc.Driver'
                }
            };
            return false;
        },
        getConfig: function() {
            $.get('generator/getConfig', function(r) {
                if(!!r.data) {
                    vm.config = r.data;
                }
            });
        }
    },
    mounted: function() {
        this.getConfig()
    }
});

// 绑定表单校验
$('#dsForm').validate();
$('#configForm').validate();