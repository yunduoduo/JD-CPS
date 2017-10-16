function trimBlank() {
	$("input[type='text']").each(function(i, n) {
		$(this).val($.trim($(this).val()));
	});
}

var rc = {
	errorAlert: function(msg, fn) {
		return layer.alert(msg, 3, fn);
	},
	successAlert: function(msg, fn) {
		return layer.alert(msg, 1, fn);
	},
	loading: function(msg) {
		return layer.load(msg ? msg : '加载中...'); 
	}
};

var rcM = {
	alert: function(msg) {
		return layer.open({
		    content: msg,
		    shadeClose: false,
		    btn: ['好的']
		});
	},
	alert: function(msg, fun) {
		return layer.open({
		    content: msg,
		    shadeClose: false,
		    btn: ['好的'],
		    end: fun
		});
	},
	loading: function(msg) {
		return layer.open({
		    type: 2,
		    content: msg,
		    shadeClose: false
		});
	},
	confirm: function(msg, yes, no) {
		return layer.open({
		    content: msg,
		    btn: ['确认', '取消'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	}
};

$(function() {
	$(document).on("change", "#productTypeId", function() {
		var typeId = $(this).val();
		changeType(typeId);
	});
	$(document).on("change", "#channelId", function() {
		var typeId = $(this).val();
		changeChannel(typeId);
	}).on("change", "#provinceId", function() {
		var provinceId = $(this).val();
		changeCity(provinceId);
	});
	
	var typeId = $("#productTypeId").val();
	if (typeId > 0) {
		changeType(typeId);
	}
	
	//////
	$(document).on("change", "#professionId", function() {
		var pid = $(this).val();
		changeProfession(pid);
	});
	var professionId = $("#professionId").val();
	if (professionId > 0) {
		changeProfession(professionId);
	}
});

function changeProfession(professionId) {
	var firstTxt = $("#professionSubId option").eq(0).text();
	var options = '<option value="0">' + firstTxt + '</option>';
	if (professionId > 0) {
		$.get("/profession/listSub", {professionId: professionId}, function(msg) {
			$.each(msg, function(i, n) {
				options += '<option value="' + n.id +'">' + n.name + '</option>';
			});
			$("#professionSubId").html(options);
			
			var initSubProfessionId = $("#professionId").attr("initSubProfessionId");
			if (initSubProfessionId) {
				$("#professionSubId").val(initSubProfessionId);
				$("#professionId").removeAttr("initSubProfessionId");
			}
		}, "json");
	} else {
		$("#professionSubId").html(options);
	}
}

function changeType(typeId) {
	var firstTxt = $("#productSubTypeId option").eq(0).text();
	var options = '<option value="0">' + firstTxt + '</option>';
	if (typeId > 0) {
		$.get("/productType/listSubType", {typeId: typeId}, function(msg) {
			$.each(msg, function(i, n) {
				options += '<option value="' + n.id +'">' + n.name + '</option>';
			});
			$("#productSubTypeId").html(options);
			
			var initSubTypeId = $("#productTypeId").attr("initSubTypeId");
			if (initSubTypeId) {
				$("#productSubTypeId").val(initSubTypeId);
				$("#productTypeId").removeAttr("initSubTypeId");
			}
		}, "json");
	} else {
		$("#productSubTypeId").html(options);
	}
}


function changeChannel(channelId) {

	var firstTxt = $("#subChannelId option").eq(0).text();
	var options = '<option value="0">' + firstTxt + '</option>';
	if (channelId > 0) {
		$.get("/channelType/listSubChannel", { channelId: channelId}, function(msg) {
			$.each(msg, function(i, n) {
				options += '<option value="' + n.subChannelId +'">' + n.subChannelName + '</option>';
			});
			$("#subChannelId").html(options);
			
			var initSubTypeId = $("#channelId").attr("initSubTypeId");
			if (initSubTypeId) {
				$("#subChannelId").val(initSubTypeId);
				$("#channelId").removeAttr("initSubTypeId");
			}
		}, "json");
	} else {
		$("#subChannelId").html(options);
	}
}

function changeCity(provinceId) {
	var firstTxt = $("#cityId option").eq(0).text();
	var firstVal = $("#cityId option").eq(0).attr("value");
	var options = '<option value="' + firstVal + '">' + firstTxt + '</option>';
	if (provinceId > 0) {
		$.get("/province/loadCity", { pid: provinceId}, function(msg) {
			$.each(msg, function(i, n) {
				options += '<option value="' + n.id +'">' + n.name + '</option>';
			});
			$("#cityId").html(options);
		}, "json");
	} else {
		$("#cityId").html(options);
	}
}