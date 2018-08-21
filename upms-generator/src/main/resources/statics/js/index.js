// 导入layer
let layer = null;
layui.use('layer', function() {
    layer = layui.layer;
});

//iframe自适应
$(window).on('resize', function () {
    let $content = $('.content');
    $content.find('iframe').each(function () {
        $(this).height(document.documentElement.clientHeight - 130);
    });
}).resize();

const vm = new Vue({
    el: '#indexApp',
    data: {
        main: "config.html",
        navTitle: "控制台"
    },
    methods: {
    },
    updated: function () {

    }
});

let menuList = ["config.html", "generator.html"];
//路由
let router = new Router();
routerList(router, menuList);
router.start();

function routerList(router, menuList) {
    for(let idx in menuList){
        router.add('#'+  menuList[idx], function () {
            let url = window.location.hash;

            //替换iframe的url
            vm.main = url.replace('#', '');

            vm.navTitle = $("a[href='" + url + "']").text();

            // 菜单添加active样式
            $("a.active.nav-link").removeClass("active");
            $("a[href='" + url + "']").addClass("active");
            $("a[href='" + url + "']").parent().parents('li').addClass('menu-open');
        });
    }
}
