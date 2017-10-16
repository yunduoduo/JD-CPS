package com.raincc.robot.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.config.CacheTimeout;

public class WeatherKit {
	
	private static Logger _log = Logger.getLogger(WeatherKit.class);
	
	private static String api = "http://api.map.baidu.com/telematics/v3/weather?location={city}&output=json&ak={key}";
	
	private static final String weather_cache_key = "weather_cache_key_";
	
	public static BDWeatherBean getWeather(String city, Double lng, Double lat) {
		if (StringUtils.isNotBlank(city)) {
			try {
				city = URLEncoder.encode(city, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		BDWeatherBean bean = CacheKit.get(CacheTimeout.LRUCache_2h, weather_cache_key + city);
		if (bean != null) {
			return bean;
		}
		
		String baiduKey = TConstants.baiduKey;//ddADSzURC58cpG5ctTKhADeU
		
		String url;
		if (lat == null || lng == null || lat == 0 || lng == 0) {
			url = api.replace("{city}", city).replace("{key}", baiduKey);
		} else {
			url = api.replace("{city}", lng + "," + lat).replace("{key}", baiduKey);
		}
		
		String result = HttpKit.get(url);
		System.out.println(result);
		
		JSONObject jo = JSONObject.parseObject(result);
		Integer error = jo.getInteger("error");
		if (error == 0) {
			// suc
			String date = jo.getString("date");
			JSONObject results = jo.getJSONArray("results").getJSONObject(0);
			String currentCity = results.getString("currentCity");
			Integer pm25 = results.getInteger("pm25");
			JSONArray indexs = results.getJSONArray("index");
			
			List<BDWeatherBean.ZhiShu> zhiShus = new LinkedList<BDWeatherBean.ZhiShu>();
			for (int i=0; i<indexs.size(); i++) {
				JSONObject index = indexs.getJSONObject(i);
				String title = index.getString("title");
				String zs = index.getString("zs");
				String tipt = index.getString("tipt");
				String des = index.getString("des");
				
				BDWeatherBean.ZhiShu shu = new BDWeatherBean.ZhiShu(title, zs, tipt, des);
				zhiShus.add(shu);
			}
			
			List<BDWeatherBean.Weather> weathers = new LinkedList<BDWeatherBean.Weather>();
			JSONArray weather_datas = results.getJSONArray("weather_data");
			for (int i=0; i<weather_datas.size(); i++) {
				JSONObject data = weather_datas.getJSONObject(i);
				String _date = data.getString("date");
				String dayPictureUrl = data.getString("dayPictureUrl");
				String nightPictureUrl = data.getString("nightPictureUrl");
				String weather = data.getString("weather");
				String wind = data.getString("wind");
				String temperature = data.getString("temperature");
				
				BDWeatherBean.Weather _weather = new BDWeatherBean.Weather(_date, dayPictureUrl, nightPictureUrl, weather, wind, temperature);
				weathers.add(_weather);
			}
			
			bean = new BDWeatherBean(currentCity, date, pm25, zhiShus, weathers);
			CacheKit.put(CacheTimeout.LRUCache_2h, weather_cache_key + city, bean);
			
			return bean;
		} else {
			_log.info("添加预报请求失败url=" + url + ",result=" + result);
			return new BDWeatherBean(false);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(WeatherKit.getWeather("北京", null, null));
	}

	public static class BDWeatherBean {
		private Boolean isSuc = true;
		private String currentCity;
		private String date;
		private Integer pm25;
		private List<ZhiShu> zhiShus;
		private List<Weather> weathers;
		
		public BDWeatherBean(String currentCity, String date, Integer pm25,
				List<ZhiShu> zhiShus, List<Weather> weathers) {
			super();
			this.currentCity = currentCity;
			this.pm25 = pm25;
			this.zhiShus = zhiShus;
			this.weathers = weathers;
			this.date = date;
		}
		
		public BDWeatherBean(Boolean isSuc) {
			this.isSuc = isSuc;
		}
		
		public static class ZhiShu {
			private String title;
			private String zs;
			private String tipt;
			private String des;
			public ZhiShu(String title, String zs, String tipt, String des) {
				super();
				this.title = title;
				this.zs = zs;
				this.tipt = tipt;
				this.des = des;
			}
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public String getZs() {
				return zs;
			}
			public void setZs(String zs) {
				this.zs = zs;
			}
			public String getTipt() {
				return tipt;
			}
			public void setTipt(String tipt) {
				this.tipt = tipt;
			}
			public String getDes() {
				return des;
			}
			public void setDes(String des) {
				this.des = des;
			}
		}
		
		public static class Weather {
			private String date;
			private String dayPictureUrl;
			private String nightPictureUrl;
			private String weather;
			private String wind;
			private String temperature;
			public Weather(String date, String dayPictureUrl,
					String nightPictureUrl, String weather, String wind,
					String temperature) {
				super();
				this.date = date;
				this.dayPictureUrl = dayPictureUrl;
				this.nightPictureUrl = nightPictureUrl;
				this.weather = weather;
				this.wind = wind;
				this.temperature = temperature;
			}
			public String getDate() {
				return date;
			}
			public void setDate(String date) {
				this.date = date;
			}
			public String getDayPictureUrl() {
				return dayPictureUrl;
			}
			public void setDayPictureUrl(String dayPictureUrl) {
				this.dayPictureUrl = dayPictureUrl;
			}
			public String getNightPictureUrl() {
				return nightPictureUrl;
			}
			public void setNightPictureUrl(String nightPictureUrl) {
				this.nightPictureUrl = nightPictureUrl;
			}
			public String getWeather() {
				return weather;
			}
			public void setWeather(String weather) {
				this.weather = weather;
			}
			public String getWind() {
				return wind;
			}
			public void setWind(String wind) {
				this.wind = wind;
			}
			public String getTemperature() {
				return temperature;
			}
			public void setTemperature(String temperature) {
				this.temperature = temperature;
			}
		}
		
		public Boolean getIsSuc() {
			return isSuc;
		}
		
		public void setIsSuc(Boolean isSuc) {
			this.isSuc = isSuc;
		}
		
		public String getCurrentCity() {
			return currentCity;
		}
		
		public void setCurrentCity(String currentCity) {
			this.currentCity = currentCity;
		}
		
		public Integer getPm25() {
			return pm25;
		}
		
		public void setPm25(Integer pm25) {
			this.pm25 = pm25;
		}
		
		public List<ZhiShu> getZhiShus() {
			return zhiShus;
		}
		
		public void setZhiShus(List<ZhiShu> zhiShus) {
			this.zhiShus = zhiShus;
		}
		
		public List<Weather> getWeathers() {
			return weathers;
		}
		
		public void setWeathers(List<Weather> weathers) {
			this.weathers = weathers;
		}
		
		public String getDate() {
			return date;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
	}
}

