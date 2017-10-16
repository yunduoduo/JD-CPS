
~function() {
	var data = [
			{
				src : "//img10.360buyimg.com/da/jfs/t7408/48/1480172393/102597/734fe12e/599d364eN34fcf934.jpg",
				bgColor : "#ececec",
				weight : "4"
			},
			{
				src : "//img30.360buyimg.com/da/jfs/t7645/293/1674158011/150850/a08cf6fb/599ea244N8c1fc52f.jpg",
				bgColor : "#d5142f",
				weight : "4"
			} ];
	var getRandom = function(arr) {
		var _temp = 0, _random = 0, _weight, _newArr = [];
		for (var i = 0; i < arr.length; i++) {
			_weight = parseInt(arr[i].weight) ? parseInt(arr[i].weight) : 1;
			_newArr[i] = [];
			_newArr[i].push(_temp);
			_temp += _weight;
			_newArr[i].push(_temp);
		}
		_random = Math.ceil(_temp * Math.random());
		for (var i = 0; i < _newArr.length; i++) {
			if (_random > _newArr[i][0] && _random <= _newArr[i][1]) {
				return arr[i];
			}
		}
	};
	var tpl = '<div class="login-banner" style="background-color: {bgColor}">\		              <div class="w">\			         <div id="banner-bg"  clstag="pageclick|keycount|20150112ABD|46" class="i-inner" style="background: url({imgURI}) 0px 0px no-repeat;background-color: {bgColor}"></div>\		              </div>\		           </div>';
	var bgData = getRandom(data);
	var bannerHtml = tpl.replace(/{bgColor}/g, bgData.bgColor).replace(
			/{imgURI}/g, bgData.src);
	$('.login-banner').replaceWith(bannerHtml);
}();
