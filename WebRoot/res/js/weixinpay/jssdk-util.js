function initjssdk(apiList, debug, ready, error) {
	var url = location.href;
	
	if (!debug) {
		debug = false;
	}

	$.get("/weixin/api/jsapi_ticket", {url: url}, function(msg) {
		wx.config({
		    debug: debug, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: msg.appId, // 必填，公众号的唯一标识
		    timestamp: msg.timestamp, // 必填，生成签名的时间戳
		    nonceStr: msg.nonceStr, // 必填，生成签名的随机串
		    signature: msg.signature,// 必填，签名，见附录1
		    jsApiList: apiList // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		wx.ready(function(){
			if (ready)
			ready.call(this);
		});
		wx.error(function(res){
			if (error)
			error.call(this);
		});
	}, "json");
}