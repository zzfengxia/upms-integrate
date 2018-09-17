/**
 * AdminLTE3模板
 * @type {null}
 */
// 导入layer，这里是异步加载的。如需同步可使用all.js，直接声明layer = layui.layer;
var layer = null;
layui.use('layer', function() {
    layer = layui.layer;
});
/**
 * 使用vue-router.js构建单页面应用
 * index.html为单页面模板，其他菜单页面会被路由解析到index.html中
 */
//生成菜单
const menuItem = Vue.extend({
    name: 'menu-item',
    props: {item: {}},
    template: [
        '<li :class="item.type === 0 ? \'nav-item has-treeview\' : \'nav-item\' ">',
        '	<a v-if="item.type === 0" href="javascript:;" class="nav-link">',
        '		<i v-if="item.iconClass" :class="item.iconClass"></i>',
        '	  <i v-else class="nav-icon fa fa-dashboard"></i>',
        '		<p>',
        '   {{item.viewName}}',
        '		<i class="right fa fa-angle-left"></i>',
        '		</p>',
        '	</a>',
        '	<ul v-if="item.type === 0" class="nav nav-treeview">',
        '		<menu-item :item="item" v-for="item in item.childMenu"></menu-item>',
        '	</ul>',

        '	<a v-if="item.type === 1 && item.url != null" :href="\'#\' + item.url" class="nav-link">',
        '		<i v-if="item.iconClass" :class="[item.iconClass, \'sub-icon\']"></i>',
        '		<i v-else class="fa fa-circle-o sub-icon"></i>',
        '       <p>{{item.viewName}}</p>',
        '</a>',
        '</li>'
    ].join('')
});

//iframe自适应
$(window).on('resize', function () {
    let $content = $('.content');
    $content.find('iframe').each(function () {
        $(this).height(document.documentElement.clientHeight - 130);
    });
}).resize();

//注册菜单组件
Vue.component('menuItem', menuItem);

// 将vm设为全局变量
var vm = new Vue({
    el: '#indexApp',
    data: {
        show: false, // 显示弹窗
        user: {},
        menuList: [],
        main: "main",
        password: '',   // 原密码
        newPassword: '', // 新密码
        navTitle: "控制台",
        isUpdatePwd: false,  // 修改密码
        isUserInfo: false,   // 个人信息
        completeData: [],    // 自动补全输入框数据
        bgStyle: {
            navbar: '',                     // 导航栏
            logo: '',                       // Logo栏
            sidebar: '',                    // 菜单栏
            borderBottom: true,             // 下边框
        }
    },
    methods: {
        getMenuList: function (event) {
            $.getJSON("menu/nav?_" + $.now(), function (r) {
                vm.menuList = r.data;
                // 初始化自动补全搜索框
                vm.loadAutoComplete();
            });
        },
        getUser: function () {
            $.getJSON("admin/user/cur?_" + $.now(), function (r) {
                vm.user = r.data;
                if(!!r.data.bgStyle) {
                    vm.bgStyle = JSON.parse(vm.user.bgStyle);
                }
            });
        },
        userInfo: function() {
            this.isUserInfo = true;
            this.openWin('个人信息', '/admin/user/save/update')
        },
        updatePwd: function () {
            this.isUpdatePwd = true;
            this.openWin('重设密码', '/admin/user/updatePwd')
        },
        openWin: function(title, url) {
            // 渲染弹窗
            vm.show = true;
            // 绑定表单校验
            $('#userForm').validate();

            layer.open({
                type: 1,
                offset: '150px',
                //skin: 'layui-layer-molv',
                title: title,
                area: ['400px', '420px'],
                shade: 0,
                shadeClose: false,
                content: $('#winContent'),
                btn: ['保存', '取消'],
                btn1: function (index) {
                    // 保存
                    if($('#userForm').valid()) {
                        let data = vm.isUpdatePwd ? {pwd: vm.password, newPwd: vm.newPassword} : JSON.stringify(vm.user);
                        let contentType = vm.isUpdatePwd ? "application/x-www-form-urlencoded;" : "application/json";
                        console.log(JSON.stringify(data));
                        $.ajax({
                            type: "POST",
                            url: baseURL + url,
                            contentType: contentType,
                            data: data,
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
                    // 清空密码
                    if(vm.isUpdatePwd) {
                        vm.password = null;
                        vm.newPassword = null;
                        vm.isUpdatePwd = false;
                    } else {
                        vm.isUserInfo = false;
                    }

                    // 隐藏表单
                    vm.show = false;
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
        loadAutoComplete: function() {
            this.parseMenu(this.menuList);

            // 自动补全插件
            $('#searchMenu').autocomplete({
                // object to local or url to remote search
                lookup: vm.completeData,
                onSelect: function(suggestion) {
                    window.location.hash = '#' + suggestion.data;
                }
            });
        },
        parseMenu: function(menuList) {
            // 解析菜单
            $.map(menuList, function(menu, index) {
                if(menu.type === 0) {
                    vm.parseMenu(menu.childMenu);
                }else if(menu.type === 1 && !!menu.url) {
                    vm.completeData.push({value: menu.viewName, data: menu.url});
                }
            });
        }
    },
    created: function () {
        this.getMenuList();
        this.getUser();
    },

    updated: function () {
        //路由
        let router = new Router(function () {
            vm.main = "404"
        });
        routerList(router, vm.menuList);
        router.start();
    }
});

function routerList(router, menuList) {
    for (let key in menuList) {
        let menu = menuList[key];
        if (menu.type === 0) {
            routerList(router, menu.childMenu);
        } else if (menu.type === 1) {
            router.add('#' + menu.url, function () {
                let url = window.location.hash;

                //替换iframe的url
                vm.main = url.replace('#', '');

                vm.navTitle = $("a[href='" + url + "']").text();

                // 移除上个元素样式
                $("a.active.nav-link").removeClass("active");
                //let $old_parent_li = $("li.has-treeview.menu-open");
                //$old_parent_li.removeClass("menu-open");

                // 为新选择元素添加样式
                $("a[href='" + url + "']").addClass("active");
                let $parent_li = $("a[href='" + url + "']").parent().parents('li');
                $parent_li.children('a.nav-link').addClass("active");
                $parent_li.addClass('menu-open');
            });
        }
    }
}