package com.raincc.common.page;

public class FormItemBean {
	
	private String id;
	private String type;
	/**
	 * 1.text
	 * 2.textarea
	 * 3.select
	 * 4.checkbox
	 * 5.radio
	 */
	private Integer tag;
	private String title;
	private String required;
	private Double min;
	private Double max;
	private Double step;
	private Boolean init = true;
	public FormItemBean(String id, String type, Integer tag, String title,
			String required, Double min, Double max, Double step, Boolean init) {
		super();
		this.id = id;
		this.type = type;
		this.tag = tag;
		this.title = title;
		this.required = required;
		this.min = min;
		this.max = max;
		this.step = step;
		this.init = init;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getTag() {
		return tag;
	}
	public void setTag(Integer tag) {
		this.tag = tag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getStep() {
		return step;
	}
	public void setStep(Double step) {
		this.step = step;
	}
	public Boolean getInit() {
		return init;
	}
	public void setInit(Boolean init) {
		this.init = init;
	}

}
