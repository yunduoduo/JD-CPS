function trimBlank() {
	$("input[type='text']").each(function(i, n) {
		$(this).val($.trim($(this).val()));
	});
}

var rcM = {
	alert_a: function(msg) {
		return layer.open({
		    content: msg,
		    shadeClose: false,
		    btn: ['确认']
		});
	},
	alert_b: function(msg,fun) {
		return layer.open({
		    content: msg,
		    shadeClose: false,
		    btn: ['确认'],
		    end: fun
		});
	},
	alert: function(msg) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			title: ['提示'],
		    content: msg,
		    shadeClose: false,
		    btn: ['好的']
		});
	},
	alert: function(msg, fun) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			title: ['提示'],
			content: msg,
		    shadeClose: false,
		    btn: ['好的'],
		    end: fun
		});
	},
	loading: function(msg) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;"> ' + msg + '</p>';
		}
		return layer.open({
			title: ['提示'],
		    type: 2,
		    content: msg,
		    shadeClose: false
		});
	},
	confirm: function(msg, yes, no) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			title: ['提示'],
			content: msg,
		    btn: ['确认', '取消'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_a: function(msg, yes, no) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}else{
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			content: msg,
		    btn: ['兑换', '取消'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_b: function(msg, yes, no) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}else{
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			content: msg,
		    btn: ['确定', '取消'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_c: function(msg, yes, no) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}else{
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			content: msg,
		    btn: ['去查看', '继续充值'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_d: function(msg, yes, no) {
		if (msg.length < 17) {
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}else{
			msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		}
		return layer.open({
			content: msg,
		    btn: ['去逛逛', '继续充值'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_e: function(msg, yes, no) {
		msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		return layer.open({
			content: msg,
		    btn: ['抽！抽！抽！', '还是算了'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_f: function(msg, yes, no) {
		msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		return layer.open({
			content: msg,
		    btn: ['填写地址', '抽点别的'],
		    shadeClose: false,
		    yes: yes, 
		    no: no
		});
	},
	confirm_g: function(msg, yes, no) {
		msg = '<p class="center" style="text-align: center;">' + msg + '</p>';
		return layer.open({
			content: msg,
		    btn: ['查看奖品', '抽点别的'],
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
	
	$(".spreadcon").each(function() {
		$(this).css("height", "auto");
		var height = $(this).height();
		if (height > 40) {
			$(this).css("height", "40px");
			$(this).parents("li").find("a.more").show();
		} else {
			$(this).parents("li").find("a.more").hide();
		}
	});
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