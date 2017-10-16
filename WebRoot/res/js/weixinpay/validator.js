function xic_form_validator(selector) {
	var validSuc = true;
	
	if (!selector) {
		selector = "input";
	}
	
	$(selector).each(function() {
		var result = xck_validator_one($(this).eq(0));
		if (!result) {
			validSuc = false;
			return false;
		}
	});
	
	return validSuc;
}

function xck_validator_one(obj) {
	var $this = $(obj);
	var required = $this.attr("required");
	var val = $.trim($this.val());
	$(this).val(val);
	if (required) {
		if (val == "") {
			xicAlert($this.attr("title"), function() {
				$this.focus();
			});
			//var l_index = layer.alert($this.attr("title"), 5, function() {
			//	layer.close(l_index);
			//	$this.focus();
			//});
			return false;
		}
		
	}
	var pattern = $this.attr("pattern");
	if (pattern) {
		re =new RegExp(pattern);
		if (!re.test(val)) {
			xicAlert($this.attr("title"), function() {
				$this.focus();
			});
			
			//var l_index = layer.alert($this.attr("title"), 5, function() {
			//	layer.close(l_index);
			//	$this.focus();
			//});
			return false;
		}
	}
	var eq = $this.attr("eq");
	if (eq) {
		var before = $("#" + eq).val();
		if (val != before) {
			xicAlert($this.attr("title"), function() {
				$this.focus();
			});
			//var l_index = layer.alert($this.attr("title"), 5, function() {
			//	layer.close(l_index);
			//	$this.focus();
			//});
			return false;
		}
	}
	
	var min = $this.attr("min");
	if (min) {
		var val = $.trim($this.val());
		if (val) {
			if (val < min) {
				xicAlert($this.attr("title"), function() {
					$this.focus();
				});
				//var l_index = layer.alert($this.attr("title"), 5, function() {
				//	layer.close(l_index);
				//	$this.focus();
				//});
				return false;
			}
		}
	}
	var step = $this.attr("step");
	if (step) {
		var val = $.trim($this.val());
		if (val) {
			var step_point = 0;
			if (step.split(".")[1]) {
				step_point = step.split(".")[1].length;
			}
			var val_point = 0;
			if (val.split(".")[1]) {
				val_point = val.split(".")[1].length;
			}
			
			if (val_point > step_point) {
				xicAlert($this.attr("title"), function() {
					$this.focus();
				});
				//var l_index = layer.alert($this.attr("title"), 5, function() {
				//	layer.close(l_index);
				//	$this.focus();
				//});
				return false;
			}
		}
	}
	
	return true;
}