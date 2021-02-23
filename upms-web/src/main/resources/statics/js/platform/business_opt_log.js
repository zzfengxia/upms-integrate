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
        tab: null,     // 表格对象
        cusReqParams: {
            cardCode: '',
            commandId: '',
            optCode: '',
            sourceChnl: '',
            businessType: ''
        },
        callerDict: {},
        interfaceList: {},
        dataRangeStr: '',
        refreshFrequency: {
            "OFF": 0,
            "5s": 5,
            "10s": 10,
            "30s": 10,
            "1min": 60,
            "5min": 300,
            "10min": 600
        },
        currentFrequency: 0,
        autoRefreshHandler: null,
    },
    computed: {
    },
    mounted: function() {
        // vue对象
        const _this = this;

        $('#searchTimeRange').daterangepicker_zh({
        }, function(startTime, endTime, chosenLbel) {
            _this.dataRangeStr = null;
            if(!!startTime) {
                _this.dataRangeStr = startTime.format('YYYY-MM-DD HH:mm:ss') + ' ~ ' + endTime.format('YYYY-MM-DD HH:mm:ss');
            }
            _this.cusReqParams.startTime = startTime;
            _this.cusReqParams.endTime = endTime;
        }).data('daterangepicker').invokeCb();

        this.tab = $("#bt-table").customBootstrapTable({
            url: baseURL + '/platform/businessOptLog/list',

            toolbar: '#table-toolbar', // 自定义工具栏，jquery选择器
            toolbarAlign: 'left',
            search: true,
            formatSearch: function () {
              // 搜索输入框提示语
              return '错误信息查询';
            },
            searchOnEnterKey: true,
            // 使用post请求方式拉取数据
            method: 'post',
            pageSize: 25,
            queryParams: function(params) {
                return {
                    offset: params.offset,
                    limit: params.limit,
                    sort: params.sort,
                    order: params.order,
                    search: params.search,
                    params: _this.cusReqParams
                };
            },
            // 表格渲染完成回调
            onPostBody: function () {
            },
            onClickRow: function () {
            },

            idField: "id",// 指定主键列
            columns: [
                {
                    title: '城市',
                    field: 'cardCode'
                },
                {
                    title: '渠道',
                    field: 'sourceChnl'
                },
                {
                    title: 'SEID',
                    field: 'seUid',
                },
                {
                    title: '接口',
                    field: 'commandId'
                },
                {
                    title: '系统类型',
                    field: 'businessType',
                    formatter: function (value) {
                        let text = '';
                        if (value === 0) {
                            text = '业务系统';
                        } else if(value === 1) {
                            text = '前置系统';
                        } else {
                            text = value;
                        }

                        return text;
                    }
                },
                {
                    title: '耗时(毫秒)',
                    field: 'executeTime',
                },
                {
                    title: '创建时间',
                    field: 'createTime',
                },
                {
                    title: '错误码',
                    field: 'optCode',
                    formatter: function (value) {
                        let text = '';
                        if (value === '0') {
                            text = '<span class="badge bg-success">' + value + '</span>';
                        } else {
                            text = '<span class="badge bg-danger">' + value + '</span>';
                        }

                        return text;
                    }
                },
                {
                    title: '错误信息',
                    field: 'optMsg',
                },
                {
                    title: '卡号',
                    field: 'cardNo',
                },
                {
                    title: '订单号',
                    field: 'orderNo',
                },
                {
                    title: '日志追踪号',
                    field: 'traceId',
                },
                {
                    title: '结束时间',
                    field: 'updateTime',
                },
                {
                    title: '备注',
                    field: 'remark',
                    visible: false
                },
                {
                    title: '执行步骤ID',
                    field: 'stepId',
                    visible: false
                }
            ]
        });
    },
    methods: {
        search: function() {
            // 刷新表格
            if(!!this.tab) {
                this.tab.bootstrapTable("refresh", {pageNumber: 1});
            }
        },
        getInterfaceList: function() {
            $.get(baseURL + "/spbiz/interfaceInfo/getAllInterface", function(r) {
                vm.interfaceList = r.data;
            })
        },
        getCaller: function() {
            let _this = this;
            $.get(baseURL + "/spbiz/callerDict/allCaller", function (res) {
                if(res.status === '0') {
                    let dict = res.data;
                    _this.callerDict = {};
                    each(dict, function(i, item) {
                        _this.callerDict[item.sourceChnl] = item.callerName;
                    });
                }
            });
        },
        autorefresh: function() {
            if(!!this.autoRefreshHandler) {
                clearInterval(this.autoRefreshHandler)
            }
            if(this.currentFrequency <= 0) {
                return;
            }
            this.autoRefreshHandler = setInterval(this.search, this.currentFrequency * 1000);
        }
    },
    created: function() {

    }
});