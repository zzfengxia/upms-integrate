/**
 * AdminLTE3 Demo Menu
 * ------------------
 * You should not use this file in production.
 * This file is for demo purposes only.
 */
(function ($) {
    'use strict';

    let $sidebar = $('.control-sidebar');
    let $container = $('<div />', {
        class: 'p-3'
    });

    $sidebar.append($container);

    const navbar_dark_skins = [
        'bg-primary',
        'bg-info',
        'bg-success',
        'bg-danger'
    ];

    const navbar_light_skins = [
        'bg-warning',
        'bg-white',
        'bg-gray-light'
    ];

    $container.append(
        '<h5>自定义管理面板配色</h5><hr class="mb-2"/>'
        + '<h6>导航栏配色</h6>'
    );

    let $navbar_variants = $('<div />', {
        'class': 'd-flex'
    });
    let navbar_all_colors = navbar_dark_skins.concat(navbar_light_skins);
    let $navbar_variants_colors = createSkinBlock(navbar_all_colors, function (e) {
        let color = $(this).data('color');
        /*let $main_header = $('.main-header');
        $main_header.removeClass('navbar-dark').removeClass('navbar-light');
        navbar_all_colors.map(function (color) {
            $main_header.removeClass(color)
        });

        if (navbar_dark_skins.indexOf(color) > -1) {
            $main_header.addClass('navbar-dark');
        } else {
            $main_header.addClass('navbar-light');
        }

        $main_header.addClass(color);*/
        if (navbar_dark_skins.indexOf(color) > -1) {
            color = color + ' navbar-dark';
        } else {
            color = color + ' navbar-light';
        }
        vm.bgStyle.navbar = color;
    });

    $navbar_variants.append($navbar_variants_colors);

    $container.append($navbar_variants);

    let $checkbox_container = $('<div />', {
        'class': 'mb-4'
    });
    let $navbar_border = $('<input />', {
        type: 'checkbox',
        value: 1,
        checked: $('.main-header').hasClass('border-bottom'),
        'class': 'mr-1'
    }).on('click', function () {
        let hasBorder = true;
        if ($(this).is(':checked')) {
            //$('.main-header').addClass('border-bottom');
            hasBorder = true;
        } else {
            //$('.main-header').removeClass('border-bottom');
            hasBorder = false;
        }
        vm.bgStyle.borderBottom = hasBorder;
    });
    $checkbox_container.append($navbar_border);
    $checkbox_container.append('<span>导航栏分界线</span>');
    $container.append($checkbox_container);

    const sidebar_colors = [
        'bg-primary',
        'bg-warning',
        'bg-info',
        'bg-danger',
        'bg-success'
    ];

    const sidebar_skins = [
        'sidebar-dark-primary',
        'sidebar-dark-warning',
        'sidebar-dark-info',
        'sidebar-dark-danger',
        'sidebar-dark-success',
        'sidebar-light-primary',
        'sidebar-light-warning',
        'sidebar-light-info',
        'sidebar-light-danger',
        'sidebar-light-success'
    ];

    $container.append('<h6>暗色菜单栏配色</h6>');
    let $sidebar_variants = $('<div />', {
        'class': 'd-flex'
    });
    $container.append($sidebar_variants);
    $container.append(createSkinBlock(sidebar_colors, function () {
        let color = $(this).data('color');
        let sidebar_class = 'sidebar-dark-' + color.replace('bg-', '');
        /*let $sidebar = $('.main-sidebar');
        sidebar_skins.map(function (skin) {
            $sidebar.removeClass(skin)
        });

        $sidebar.addClass(sidebar_class)*/
        vm.bgStyle.sidebar = sidebar_class;
    }));

    $container.append('<h6>亮色菜单栏配色</h6>');
    $sidebar_variants = $('<div />', {
        'class': 'd-flex'
    });
    $container.append($sidebar_variants);
    $container.append(createSkinBlock(sidebar_colors, function () {
        let color = $(this).data('color');
        let sidebar_class = 'sidebar-light-' + color.replace('bg-', '')
        /*let $sidebar = $('.main-sidebar');
        sidebar_skins.map(function (skin) {
            $sidebar.removeClass(skin)
        });

        $sidebar.addClass(sidebar_class);*/
        vm.bgStyle.sidebar = sidebar_class;
    }));

    let logo_skins = navbar_all_colors;
    $container.append('<h6>商标Logo配色</h6>');
    let $logo_variants = $('<div />', {
        'class': 'd-flex'
    });
    $container.append($logo_variants);

    let $clear_btn = $('<a />', {
        href: 'javascript:void(0)'
    }).text('清除').on('click', function () {
        /*let $logo = $('.brand-link');
        logo_skins.map(function (skin) {
            $logo.removeClass(skin)
        });*/
        vm.bgStyle.logo = '';
    });

    $container.append(createSkinBlock(logo_skins, function () {
        let color = $(this).data('color');
        /*let $logo = $('.brand-link');
        logo_skins.map(function (skin) {
            $logo.removeClass(skin)
        });
        $logo.addClass(color);*/
        vm.bgStyle.logo = color;
    }).append($clear_btn));

    function createSkinBlock(colors, callback) {
        let $block = $('<div />', {
            'class': 'd-flex flex-wrap mb-3'
        });

        colors.map(function (color) {
            let $color = $('<div />', {
                'class': (typeof color === 'object' ? color.join(' ') : color) + ' elevation-2'
            });

            $block.append($color);

            $color.data('color', color);

            $color.css({
                width: '40px',
                height: '20px',
                borderRadius: '25px',
                marginRight: 10,
                marginBottom: 10,
                opacity: 0.8,
                cursor: 'pointer'
            });

            $color.hover(function () {
                $(this).css({opacity: 1}).removeClass('elevation-2').addClass('elevation-4');
            }, function () {
                $(this).css({opacity: 0.8}).removeClass('elevation-4').addClass('elevation-2');
            });

            if (callback) {
                $color.on('click', callback);
            }
        });

        return $block;
    }

    let $btnDiv = $('<div />');

    let $bgSave = $('<button />', {
        class: '"btn btn-primary btn-sm'
    }).text('保存').on('click', function () {
        let data = {id: vm.user.id, bgStyle: JSON.stringify(vm.bgStyle)};
        console.log(JSON.stringify(data));
        $.ajax({
            type: "POST",
            url: baseURL + "/admin/user/saveBgStyle",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function(r) {
                if(r.status === "0") {
                    layer.msg('保存成功');
                } else {
                    layer.alert(r.message);
                }
            }
        });
    });

    let $bgReset = $('<button />', {
        class: '"btn btn-primary btn-sm'
    }).text('重置').on('click', function () {
        vm.bgStyle = {
            navbar: '',                     // 导航栏
            logo: '',                       // Logo栏
            sidebar: '',                    // 菜单栏
            borderBottom: true,             // 下边框
        };
    });

    $btnDiv.append($bgSave).append('&nbsp;&nbsp;').append($bgReset);
    $container.append($btnDiv);
})(jQuery);
