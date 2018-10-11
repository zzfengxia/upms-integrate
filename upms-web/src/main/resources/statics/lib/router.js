(function () {
    window.Router = function (notfoundFun) {
        var self = this;

        self.hashList = {};
        /* 路由表 */
        self.index = null;
        self.key = '!';

        // function
        self.notfound = notfoundFun;
    };

    /**
     * 添加路由,如果路由已经存在则会覆盖
     * @param addr: 地址
     * @param callback: 回调函数，调用回调函数的时候同时也会传入相应参数
     */
    Router.prototype.add = function (addr, callback) {
        var self = this;

        self.hashList[addr] = callback;
    };

    /**
     * 删除路由
     * @param addr: 地址
     */
    Router.prototype.remove = function (addr) {
        var self = this;

        delete self.hashList[addr];
    };

    /**
     * 设置主页地址
     * @param index: 主页地址
     */
    Router.prototype.setIndex = function (index) {
        var self = this;

        self.index = index;
    };


    /**
     * 跳转到指定地址
     * @param addr: 地址值
     */
    Router.prototype.go = function (addr) {
        var self = this;

        window.location.hash = '#' + addr;
    };

    /**
     * 路由是否注册
     * @param addr: 地址值
     */
    Router.prototype.hasRouter = function (addr) {
        let self = this;

        return !!self.hashList[addr]
    };

    /**
     * 重载页面
     */
    Router.prototype.reload = function () {
        let self = this;
        let hash = window.location.hash;
        //var addr = hash.split('/')[0];
        let addr = hash;
        let cb = getCb(addr, self.hashList);
        if (isDef(cb)) {
            let qstr, url = addr;
            // 获取url和query str
            if(addr.indexOf("?") !== -1) {
                url = addr.substring(0, addr.indexOf("?"));
                qstr = addr.substring(addr.indexOf("?") + 1);
            }
            cb.apply(self, [url, qstr]);
        } else if (self.hashList.length > 1 && !!addr && isDef(self.notfound)) {
            self.notfound.apply(self)
        } else {
            self.index && self.go(self.index);
        }
    };

    /**
     * 开始路由，实际上只是为了当直接访问路由路由地址的时候能够及时调用回调
     */
    Router.prototype.start = function () {
        // 监听hash
        window.onhashchange = function () {
            self.reload();
        };

        const self = this;

        self.reload();
    };

    /**
     * 获取callback，支持正则匹配，诸如user/detail?id=1024的url可以匹配到user/detail。可通过url方式传递参数
     * @return false or callback
     */
    function getCb(addr, hashList) {
        if(addr.indexOf("?") !== -1) {
            let url = addr.substring(0, addr.indexOf("?"));
            return hashList[url];
        }else {
            if(!!hashList[addr]) {
                return hashList[addr];
            }
        }

        return null;
    }

    function isDef(r) {
        return r !== undefined && r !== null
    }
})();