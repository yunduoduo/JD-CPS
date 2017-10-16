function xicAlert(txt, func) {
	var id = new Date().getTime();
	var html = Mustache.render($("#xicAlertTpl").html(), {txt: txt, id: id});
	$("body").append(html);
	$("#error_" + id).show();
	
	$("#error_" + id).find(".close").click(function() {
		$("#error_" + id).hide();
		$("#error_" + id).remove();
		$("#error_" + id).find(".close").unbind("click");
		if (func)
			func.apply();
	});
	$("#error_" + id).find("button").click(function() {
		$("#error_" + id).hide();
		$("#error_" + id).remove();
		$("#error_" + id).find("button").unbind("click");
		if (func)
			func.apply();
	});
}

function xicConfirm(txt, funcSure) {
	var id = new Date().getTime();
	var html = Mustache.render($("#confirmTpl").html(), {txt: txt, id: id});
	$("body").append(html);
	$("#error_" + id).show();
	
	$("#error_" + id).find(".close").click(function() {
		$("#error_" + id).hide();
		$("#error_" + id).remove();
		$("#error_" + id).find(".close").unbind("click");
	});
	$("#error_" + id).find(".closeBtn").click(function() {
		$("#error_" + id).hide();
		$("#error_" + id).remove();
		$("#error_" + id).find(".closeBtn").unbind("click");
	});
	
	$("#error_" + id).find(".sureBtn").click(function() {
		$("#error_" + id).hide();
		$("#error_" + id).remove();
		$("#error_" + id).find(".sureBtn").unbind("click");
		funcSure.apply();
	});
}