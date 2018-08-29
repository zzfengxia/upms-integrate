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

            // 移除上个元素样式
            $("a.active.nav-link").removeClass("active");

            // 为新选择元素添加样式
            $("a[href='" + url + "']").addClass("active");
            let $parent_li = $("a[href='" + url + "']").parent().parents('li');
            $parent_li.children('a.nav-link').addClass("active");
            $parent_li.addClass('menu-open');
        });
    }
}
