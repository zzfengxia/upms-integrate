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
        roles: [],
        dacGroup: {},
        user: {
            status: "1",
            roles: [],
            dacGroupList: []
        },
        check: {
            msgClass: '',
            resClass: '',
            msg:''
        },
        tab: null // 表格对象
    },
    mounted: function(){
        // vue对象
        let _this = this;
        this.getDacGroup();
        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/admin/user/list',
            toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器
            formatSearch: function () {
                // 搜索输入框提示语
                return '用户名或姓名搜索';
            },

            // 表格渲染完成回调
            onPostBody: function () {
                // 判断是否有修改权限
                /*if(!_this.permCheck()) {
                    $("button[name='updateBtn']").remove();
                }
                if(!_this.resetPermCheck()) {
                    $("button[name='resetBtn']").remove();
                }*/

                bindCheckBoxForTable();
            },

            idField: "username",// 指定主键列
            columns: [
                {
                    field: 'checkbox',// json数据中rows数组中的属性名
                    checkbox: true,
                },
                {
                    title: '用户名',
                    field: 'username',
                    sortable: true // 启用排序
                },
                {
                    title: '真实姓名',
                    field: 'realname',
                },
                {
                    title: '邮箱',
                    field: 'email',
                },
                {
                    title: '联系电话',
                    field: 'phone',
                },
                {
                    title: '最近上线时间',
                    field: 'lastTime',
                },
                {
                    title: '状态',
                    field: 'status',
                    // 格式化数据，参数分别是：value该行的属性，row该行记录，index该行下标
                    formatter: function (value, row, index) {
                        return value === "1" ? '<span class="badge badge-success">正常</span>' : '<span class="badge badge-danger">禁用</span>';
                    }
                },
                {
                    title: '操作',
                    field: 'id',
                    align: 'center',
                    formatter: function (value, row, index) {
                        let updateBtn = '', resetBtn = '';
                        if(_this.permCheck()) {
                            updateBtn = '<button class="btn btn-success btn-mini" name="updateBtn" onclick="update(' + value + ')"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</button>';
                        }
                        if(_this.resetPermCheck()) {
                            resetBtn = '&nbsp;&nbsp;<button class="btn btn-success btn-mini" name="resetBtn" onclick="resetPwd(\'' + row.username + '\')"><i class="fa fa-pencil-square-o"></i>&nbsp;重置密码</button>';
                        }
                        return value === "1" ? updateBtn : updateBtn + resetBtn;
                    }
                }
            ]
        });
    },
    methods: {
        create: function () {
            // 新增弹窗
            vm.isUpdate = false;
            //获取角色信息
            this.getRoleList();

            this.openWin();
        },
        getDacGroup: function () {
            /*let _this = this;
            $.get(baseURL + "/caller/dacGroup", function(res) {
                if(res.status === '0') {
                    let data = res.data;
                    _this.sourceChannelDict = data;
                }
            });*/
            let _this = this;
            _this.dacGroup = {
                "1": "普通商户",
                "2": "VIP商户"
            }
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

            layer.confirm('确定要删除选中的用户', function (index) {
                layer.close(index);

                $.ajax({
                    type: "POST",
                    url: baseURL + "/admin/user/delete",
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
        resetPermCheck: function() {
            // 重置密码权限检查
            let flag = this.$refs.resetPwdInput && this.$refs.resetPwdInput.value;

            return !!flag;
        },
        getUser: function (userId) {
            $.get(baseURL + "/admin/user/info/" + userId, function (r) {
                vm.user = r.data;
            });
        },
        getRoleList: function () {
            $.get(baseURL + "/role/all", function (r) {
                vm.roles = r.data;
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
        checkUsername: function() {
            // 校验用户名
            if(!this.user.username || this.isUpdate) {
                return;
            }
            $.get(baseURL + "/admin/user/check/" + this.user.username, function(r) {
                if(r.status === "0") {
                    vm.check.msgClass = 'valid-feedback';
                    vm.check.resClass = 'is-valid';
                    vm.check.msg = '用户名可以使用';
                } else {
                    vm.check.msgClass = 'invalid-feedback';
                    vm.check.resClass = 'is-invalid';
                    vm.check.msg = r.message;
                }
            });
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
                    // 清空user
                    if(vm.isUpdate) {
                        vm.user = {status: "1", roles: [], dacGroupList: []};
                    } else {
                        vm.user.password = null;
                    }
                    // 清空check
                    vm.check= {
                        msgClass: '',
                        resClass: '',
                        msg:''
                    };
                    // 隐藏表单
                    vm.show = false;
                }
            });
        }
    }
});

function update(uid) {
    // 修改弹窗
    vm.isUpdate = true;
    vm.getUser(uid);
    // 获取角色信息
    vm.getRoleList();

    vm.openWin();

    stopBubble();
}

function resetPwd(username) {
    layer.confirm('确定要重置用户' + username + '的密码', function (index) {
        layer.close(index);

        $.get(baseURL + "/admin/user/resetPwd/" + username, function(r) {
            if(r.status === "0") {
                layer.alert('重置成功,新密码为' + r.data);
            } else {
                layer.alert(r.message);
            }
        });
    });

    stopBubble();
}