/**
 * 遍历数组或者对象
 * @param obj
 * @param callback 回调函数
 */
function each(obj, callback) {
    if(typeof obj === 'object') {
        if($.isArray(obj)) {
            for(var i = 0; i < obj.length; i++) {
                if(callback) {
                    var res = callback(i, obj[i]);
                    if(res === false) {
                        break;
                    }
                }
            }
        }else if(!$.isEmptyObject(obj)){
            var i = 0;
            for(var key in obj) {
                if(callback) {
                    var res = callback(i, key, obj[key]);
                    i++;
                    if(res === false) {
                        break;
                    }
                }
            }
        }
    }
}

var baseURL = window !== top ? $('#contextPath', parent.document).val() : $('#contextPath').val();

/**
 * url跳转
 *
 * @param url 跳转url请求
 * @param navTitle 导航文本
 */
function toUrl(url, navTitle) {
    top.window.location.hash = '#' + url;
    if(!!top.vm.navContent) {
        let navContent = {'title': navTitle, 'url': '#' + url};
        top.vm.navContent.push(navContent);
    }
}

/**
 * 获取query串中指定的key信息
 *
 * @param name
 * @returns {*}
 */
function getQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

/*
 * 为表格绑定iCheck样式
 */
function bindCheckBoxForTable(boxesName, subBoxesName) {
    if (!boxesName || boxesName === '') {
        boxesName = 'btSelectAll';
    }
    if (!subBoxesName || subBoxesName === '') {
        subBoxesName = 'btSelectItem';
    }
    var boxes = $("input[name=" + boxesName + "]");
    var subBoxes = $("input[name=" + subBoxesName + "]");
    boxes.iCheck({checkboxClass: 'icheckbox_flat-green'});
    subBoxes.iCheck({checkboxClass: 'icheckbox_flat-green'});
    boxes.on("ifChecked", function (event) {
        subBoxes.iCheck('check');
    });
    boxes.on("ifUnchecked", function (event) {
        subBoxes.iCheck('uncheck');
    });
    subBoxes.on("ifChecked", function (event) {
        if ($("input[name=" + subBoxesName + "]:checked").length == subBoxes.length) {
            boxes.iCheck('check');
        }

        // 添加tr背景色
        let $tr = $(this).closest('tr');
        if (!!$tr) {
            $tr.addClass('table-success');
        }
    });
    subBoxes.on("ifUnchecked", function (event) {
        var checkList = $("input[name=" + subBoxesName + "]:checked");
        if (checkList.length != subBoxes.length) {
            boxes.iCheck('uncheck');
        }
        checkList.each(function (i, o) {
            $(o).iCheck('check');
        })

        let $tr = $(this).closest('tr');
        if (!!$tr) {
            $tr.removeClass('table-success');
        }
    });
}

function useICheckForForm(formId) {
    if(!formId) {
        return;
    }
    let $form = $("#" + formId);
    let $radios = $form.find("input[type='radio']");
    if($radios.length > 0) {
        $radios.iCheck({radioClass: 'iradio_flat-green'});
    }

    let $checkboxs = $form.find("input[type='checkbox']");
    if($checkboxs.length > 0) {
        $checkboxs.iCheck({checkboxClass: 'icheckbox_flat-green'});
    }
}

/**
 * bootstrap-table行单击操作
 */
function clickRow(row, $ele) {
    // 添加背景样式
    $ele.toggleClass('table-success');
    // 触发复选框点击
    let $box = $ele.find("input[type='checkbox']");
    if (!!$box) {
        $ele.hasClass('table-success') ? $box.iCheck('check') : $box.iCheck('uncheck');
    }
}

// bootstrap-table选择一条记录
function selectSingleRow() {
    let $checkedObj = $('input[name=btSelectItem]:checked:enabled');

    if($checkedObj.length === 0) {
        layer.msg("请选择一条记录");
        return;
    }

    if($checkedObj.length > 1) {
        layer.msg("只能选择一条记录");
        return;
    }

    let index = $checkedObj.attr('data-index');
    // 获取行数据
    let datas = $("#bt-table").bootstrapTable('getData', true);

    return datas[index];
}

// bootstrap-table选择多条记录
function selectRows() {
    let $checkedObj = $('input[name=btSelectItem]:checked:enabled');

    if($checkedObj.length === 0) {
        layer.msg("请至少选择一条记录");
        return;
    }

    let rows = [];
    $checkedObj.each(function() {
        let index = $(this).attr('data-index');
        // 获取行数据
        let datas = $("#bt-table").bootstrapTable('getData', true);

        rows.push(datas[index]);
    });
    return rows;
}

/**
 * 阻止事件冒泡
 *
 * @param e
 */
function stopBubble(e) {
    e = e ? e : window.event;
    if (window.event) { // IE
        e.cancelBubble = true;
    } else { // FF
        //e.preventDefault();
        e.stopPropagation();
    }
}

/**
 * bootstrap-table公共参数
 * @param option
 */
$.prototype.customBootstrapTable = function(option) {
    let cusOpt = {
        striped: true, // 设置为 true 会有隔行变色效果
        // undefinedText: "未定义", // 当数据为undefined 时显示的字符
        pagination: true, // 开启分页
        pageSize: 10,// 如果设置了分页，每页数据条数
        search: true, //显示搜索框
        showRefresh: true, // 显示属性按钮
        showColumns: true, // 内容字段下拉框
        // toolbar: '#my-toolbar', // 自定义工具栏，jquery选择器

        //height: 700, // 固定高度
        sidePagination: "server", // 服务端处理分页

        // 单击行事件
        onClickRow: function (row, $ele) {
            clickRow(row, $ele);
        },
    };

    let finalOpt = $.extend({}, cusOpt, option);

    return this.bootstrapTable(finalOpt);
}

// ************ 自定义jquery-validate检验规则 **************
jQuery.validator.addMethod("repeatPwd", function(value, element, params) {
    return value === $(params).val();
}, "两次输入密码不相同");