package com.raincc.robot.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.blade.kit.DateKit;
import com.blade.kit.http.HttpRequest;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.raincc.robot.interceptor.CommonInterceptor;
import com.raincc.robot.me.biezhi.wechat.Constant;
import com.raincc.robot.me.biezhi.wechat.WechatRobot;
import com.raincc.common.RcConstants;
import com.raincc.common.directive.DirectiveBind;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.interceptor.FreemarkerReqParamInterceptor;
import com.raincc.wx.interceptor.WeiChatInterceptor;

import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModelException;

public class TConfig extends JFinalConfig {
	
	Routes routes;

	@Override
	public void configConstant(Constants me) {
		this.loadPropertyFile("/config/config.properties");
		me.setDevMode(this.getPropertyToBoolean("DevMode"));
		me.setBaseViewPath("/WEB-INF/views");
		me.setViewType(ViewType.FREE_MARKER);
		me.setMaxPostSize(me.getMaxPostSize() * 20);
		
//		ApiConfigKit.setDevMode(me.getDevMode());
		
//		TConstants.wx_AppId 		= getProperty("wx_AppId");
//		TConstants.wx_AppSecret 	= getProperty("wx_AppSecret");
//		TConstants.wx_token 		= getProperty("wx_token");
//		TConstants.wx_oriId 		= getProperty("wx_oriId");
		
		RcConstants.admin_index_url = getProperty("admin_index_url");
		TConstants.website 		= getProperty("website");
		
		TConstants.wsq_AppID 		= getProperty("wsq_AppID");
		TConstants.wsq_AppSecret 	= getProperty("wsq_AppSecret");
		TConstants.wsq_sId		= getProperty("wsq_sId");
		
		TConstants.baiduKey		= getProperty("baiduKey");
		TConstants.qqLBSKey 		= getProperty("qqLBSKey");
		
		TConstants.JD_Appkey = getProperty("JD_Appkey");
		TConstants.JD_AppSecret = getProperty("JD_AppSecret");
		TConstants.BL_JD_UNIONID = getPropertyToLong("BL_JD_UNIONID");
		TConstants.T_LOGINNAME = getProperty("T_LOGINNAME");
		TConstants.T_PASSWORD = getProperty("T_PASSWORD");
		TConstants.T_JDLOGIN_EID = getProperty("T_JDLOGIN_EID");
		TConstants.T_LDLOGIN_FP = getProperty("T_LDLOGIN_FP");
		TConstants.PJECT_URL = getProperty("PJECT_URL");
		System.setProperty("https.protocols", "TLSv1");
		System.setProperty("jsse.enableSNIExtension", "false");
		
//		WechatRobot wechatRobot = new WechatRobot();
//		HttpRequest request = HttpRequest.get(Constant.JS_LOGIN_URL, true, "appid", "wx782c26e4c19acffb", "fun", "new",
//				"lang", "zh_CN", "_", DateKit.getCurrentUnixTime());
//		String res = request.body();
		//res=window.QRLogin.code = 200; window.QRLogin.uuid = "QdJSLdnfow==";
//		System.err.println(">>>>>>>>>>>>,res="+res);
// 		classic_compatible=true ##如果变量为null,转化为空字符串,比如做比较的时候按照空字符做比较
//		FreeMarkerRender.getConfiguration().setClassicCompatible(true);
//		FreeMarkerRender.getConfiguration().setTemplateExceptionHandler(new FreemarkerExceptionHandler());
	}

	@Override
	public void configRoute(Routes me) {
		this.routes = me;
		AutoBindRoutes routes = new AutoBindRoutes();
		routes.autoScan(false);
		me.add(routes);
		
		Set<Entry<String, Class<? extends Controller>>> rs = me.getEntrySet();
		for (Iterator<Entry<String, Class<? extends Controller>>> iter = rs.iterator(); iter.hasNext();) {
			Entry<String, Class<? extends Controller>> entry = iter.next();
			System.out.println("path:" + entry.getKey() + ",class:" + entry.getValue().getName());
		}
		
	}

	@Override
	public void configPlugin(Plugins me) {
		loadPropertyFile("/config/config.properties");
		DruidPlugin druidPlugin = new DruidPlugin(getProperty("jdbc.url"), getProperty("jdbc.username"), getProperty("jdbc.password"));
		druidPlugin.setDriverClass(getProperty("jdbc.driverClassName"));
		
		druidPlugin.setInitialSize(getPropertyToInt("jdbc.initialSize"));
		druidPlugin.setMinIdle(getPropertyToInt("jdbc.minIdle"));
		druidPlugin.setMaxActive(getPropertyToInt("jdbc.maxActive"));

		druidPlugin.setMaxWait(getPropertyToInt("jdbc.maxWait"));
		druidPlugin.setValidationQuery("select 1 FROM bl_config");
		
		me.add(druidPlugin);
		
		AutoTableBindPlugin atbp = new AutoTableBindPlugin(druidPlugin);
		atbp.autoScan(false);
		atbp.setShowSql(true);
		atbp.setDevMode(getPropertyToBoolean("DevMode"));
		
		me.add(atbp);
		me.add(new EhCachePlugin());
		me.add(new QuartzPlugin());//定时器
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new CommonInterceptor());
		me.add(new WeiChatInterceptor());
//		me.add(new WeixinInterceptor());
		me.add(new FreemarkerReqParamInterceptor());
//		me.add(new AdminInterceptor());
//		me.add(new JdLoginInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		
		DirectiveBind directiveBind = null;
		List<Class<? extends TemplateDirectiveModel>> templateDirectiveModelClasses = ClassSearcher.of(TemplateDirectiveModel.class).search();
		for (Class template : templateDirectiveModelClasses) {
			directiveBind = (DirectiveBind) template.getAnnotation(DirectiveBind.class);
			
			if (directiveBind != null) {
				String key = directiveBind.value();
				System.out.println("freemarker tag::" + key + ",class=" + template.getClass().getName());
				
				try {
					FreeMarkerRender.getConfiguration().setSharedVariable(key, template.newInstance());
				} catch (TemplateModelException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		Db.query("set names utf8mb4");
	}

	@Override
	public void beforeJFinalStop() {
		System.out.println("====》服务器已关闭");
		//监听群组设置无效
//		Db.update("update rb_wxusergroup set isValid = 0 ,isPutaway = false");
//		Db.update("update rb_wxuser set isValid = false");
		super.beforeJFinalStop();
	}


}
