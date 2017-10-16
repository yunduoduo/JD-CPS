/*$Id: interface.js 34886 2014-11-21 01:23:46Z yixizhou $*/
(function(){
    var _getCodeUrl = 'http://m.wsq.qq.com/app/authCode';
    var _wsqReady = false;
    var _wsqCallback = {
        wsqReady: function() {
            _wsqReady = true;
            window.wsqOpenApp = new wsqOpen.App();
            _wsqOpenAppReady();
        }
    };
    var wsqOpen = {};
    wsqOpen.App = {};
    wsqOpen.JSONP = (function () {
        var head, query, key, window = this;

        function load(url) {
            script = document.createElement('script'),
            done = false;
            script.src = url;
            script.charset = 'UTF-8';
            script.async = true;

            script.onload = script.onreadystatechange = function () {
                if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                    done = true;
                    script.onload = script.onreadystatechange = null;
                    if (script && script.parentNode) {
                        script.parentNode.removeChild(script);
                    }
                }
            };
            if (!head) {
                head = document.getElementsByTagName('head')[0];
            }
            head.appendChild(script);
        }

        function jsonp(url, params, callback) {
            if (url.indexOf('?') > -1) {
                query = '&';
            } else {
                query = '?';
            }

            params = params || {};
            for (key in params) {
                if (params.hasOwnProperty(key)) {
                    query += encodeURIComponent(key) + "=" + encodeURIComponent(params[key]) + "&";
                }
            }

            var jsonp = 'wsqOpenCallback';
            window[jsonp] = function (data) {
                callback(data);
                try {
                    delete window[jsonp];
                } catch (e) {}
                window[jsonp] = null;
            };

            load(url + query + "callback=" + jsonp);

            return jsonp;
        }
        return {
            get: jsonp
        };
    })();

    wsqOpen.parseStr = function(str, key) {
        var params = str.split('&');
        var query = {};
        var q = [];
        var name = '';

        for (i = 0; i < params.length; i++) {
            q = params[i].split('=');
            name = decodeURIComponent(q[0]);
            if (name.substr(-2) == '[]') {
                if (!query[name]) {
                    query[name] = [];
                }
                query[name].push(q[1]);
            } else {
                query[name] = q[1];
            }
        }

        if (key) {
            if (query[key]) {
                return query[key];
            }

            return null;
        } else {
            return query;
        }
    }


    wsqOpen.App = function() {
        function _inArray(needle, haystack) {
            if(typeof needle == 'string' || typeof needle == 'number') {
                for(var i in haystack) {
                    if(haystack[i] == needle) {
                        return true;
                    }
                }
            }
            return false;
        }
        function _postMessage(action, params) {
            window.parent.postMessage({action:action, params:params}, 'http://m.wsq.qq.com');
            return true;
        };
        function _pushCallbackQueue(cb, from) {
            var code = Math.ceil(Math.random() * (9999 - 1000) + 1000);
            var name = from + '_' + new Date().getTime() + '_' + code;
            _wsqCallback[name] = cb;
            return name;
        }

        this.getAuthCode = function(cb) {
            var params = wsqOpen.parseStr(self.location.search.substr(1)) || {};
            var sId = params['sId'] || 0;
            var appId = params['appId'] || 0;
            if (!sId || !appId) {
                return false;
            }
            var url = _getCodeUrl + '?resType=jsonp&sId=' + sId + '&appId=' + appId;
            var params = {};
            var callback = function(re) {
                if (typeof cb == 'function') {
                    cb(re);
                }
            };
            wsqOpen.JSONP.get(url, params, callback);
        };

        this.share = function(opts) {
            if (typeof opts !== 'object') {
                opts = {content:opts}
            }
            var name = _pushCallbackQueue(opts.callback, 'share');
            delete opts.callback;
            opts.cbName = name;
            _postMessage('share', opts);
            return true;
        };
        this.resizeFrame = function(opts) {
            _postMessage('resize', opts);
            return true;
        };

        this.getWin = function(opts) {
            var name = _pushCallbackQueue(opts.callback, 'getWin');
            delete opts.callback;
            opts.cbName = name;
            _postMessage('getWin', opts);
            return true;
        };
        this.initShare = function(opts) {
            var name = _pushCallbackQueue(opts.callback, 'initShare');
            delete opts.callback;
            opts.cbName = name;
            _postMessage('initShare', opts);
            return true;
        }
        this.payOrder = function(opts) {
            var name = _pushCallbackQueue(opts.callback, 'payOrder');
            delete opts.callback;
            opts.cbName = name;
            _postMessage('payOrder', opts);
            return true;
        }
        this.showUserInfoForm = function(opts) {
            var name = _pushCallbackQueue(opts.callback, 'showUserInfoForm');
            delete opts.callback;
            opts.cbName = name;
            _postMessage('showUserInfoForm', opts);
            return true;
        }
        // [{name: '发帖', callback: function(re) {re}}]
        this.initBar = function(opts) {
            var len = opts.length;
            var params = [];
            if (len > 0) {
                for (var i = 0; i < len; ++i) {
                    var cbName = _pushCallbackQueue(opts[i].callback, 'st_initBar' + i);
                    var param = {
                        name: opts[i].name,
                        cbName: cbName
                    };
                    params.push(param);
                }
            }
            _postMessage('initBar', params);
            return true;
        };

    }

    function _wsqOpenAppReady() {
        var evt = document.createEvent("HTMLEvents");
        evt.initEvent("wsqOpenAppReady", true, true);
        document.dispatchEvent(evt);
    }
    if (_wsqReady) {
        window.wsqOpenApp = new wsqOpen.App();
        _wsqOpenAppReady();
    }

    window.onmessage = function(e) {
        if (e.origin !== 'http://m.wsq.qq.com') {
            return;
        }

        if (!e.data.cbName || (typeof _wsqCallback[e.data.cbName] !== 'function')) {
            return;
        }
        _wsqCallback[e.data.cbName](e.data.params);
        if (e.data.cbName.substring(0, 3) !== 'st_') {
            delete _wsqCallback[e.data.cbName];
        }
        return;
    };
}());