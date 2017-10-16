package com.raincc.robot.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.raincc.robot.config.TConstants;

public class QQLBSAPIUtils {
	
	/**
	 * 结构化地址
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static AddressComponent geocoderAddressComponent(String lat, String lng) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("location", lat + "," + lng);
		params.put("key", TConstants.qqLBSKey);
		
		int times = 3;
		
		String result = null;
		while (result == null) {
			if (times < 0) {
				break;
			}
			try {
				result = HttpKit.get("http://apis.map.qq.com/ws/geocoder/v1", params);
			} catch (Exception e) {
				e.printStackTrace();
				times --;
			}
		}
		
		JSONObject jo = JSON.parseObject(result);
		if (jo.getInteger("status") == 0) {
			JSONObject _result = jo.getJSONObject("result");
			JSONObject address_component = _result.getJSONObject("address_component");
			
			AddressComponent com = new QQLBSAPIUtils().new AddressComponent();
			
			com.set_native(address_component.getString("native"));
			com.setCity(address_component.getString("city"));
			com.setProvince(address_component.getString("province"));
			com.setDistrict(address_component.getString("district"));
			com.setStreet(address_component.getString("street"));
			com.setStreet_number(address_component.getString("street_number"));
			return com;
		}
		return null;
	}
	
	public class AddressComponent {
		private String _native;
		private String province;
		private String city;
		private String district;
		private String street;
		private String street_number;
		public String get_native() {
			return _native;
		}
		public void set_native(String _native) {
			this._native = _native;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getStreet_number() {
			return street_number;
		}
		public void setStreet_number(String street_number) {
			this.street_number = street_number;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		@Override
		public String toString() {
			return "AddressComponent [_native=" + _native + ", province="
					+ province + ", city=" + city + ", district=" + district
					+ ", street=" + street + ", street_number=" + street_number
					+ "]";
		}
	}

}


