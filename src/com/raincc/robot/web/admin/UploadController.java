package com.raincc.robot.web.admin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.kit.ImgKit;
import com.raincc.robot.config.TConstants;

@ControllerBind(controllerKey = "/robotAdmin/upload")
@Before(AdminInterceptor.class)
public class UploadController extends AdminBaseController {
	
	public void index() {
		UploadFile file = getFile();
		String dir = getPara("dir");
		if (StringUtils.isBlank(dir)) {
			dir = "product";
		}
		
		String type = file.getContentType();
		if ("video/mp4".equalsIgnoreCase(type)) {
			String localPath = TConstants.PJECT_URL;
			String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
			localPath = "/upload/" + dir + "/" + ImgKit.getDatePath();
			File tarFile = new File(PathKit.getWebRootPath() + localPath);
			if (!tarFile.exists()) {
				tarFile.mkdirs();
			}
			localPath = localPath + "/" + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
			tarFile = new File(PathKit.getWebRootPath() + localPath);
			
			try {
				FileUtils.copyFile(file.getFile(), tarFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			renderTemplateJson("0", localPath);
			return;
		}
		
		int maxWidth = getParaToInt("maxWidth");
		String path = ImgKit.saveImg(file, "/upload/" + dir + "/", maxWidth);
		
		renderTemplateJson("0", path);
	}
	
	public void keUpload() {
		UploadFile file = getFile();
		String type = file.getContentType();
		if ("video/mp4".equals(type)) {
			String localPath = null;
			String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
			localPath = "/upload/ke/video/" + ImgKit.getDatePath();
			File tarFile = new File(PathKit.getWebRootPath() + localPath);
			if (!tarFile.exists()) {
				tarFile.mkdirs();
			}
			localPath = localPath + "/" + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
			tarFile = new File(PathKit.getWebRootPath() + localPath);
			
			try {
				FileUtils.copyFile(file.getFile(), tarFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			renderJson(imgUploadResult(0, localPath));
			return;
		}
		
		int maxWidth = getParaToInt("maxWidth");
		try {
			String path = ImgKit.saveImg(file, "/upload/ke/", maxWidth);
			renderJson(imgUploadResult(0, path));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(imgUploadResult(1, "不支持的图片格式，请生成正确的图片格式"));
		}
		
	}
	
	public void keManager() {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
		response.setCharacterEncoding("UTF-8");
		String basePath = "/upload/ke/";
		String rootPath = request.getSession().getServletContext().getRealPath(basePath);
		String requestPath = request.getParameter("path");
		String moveup_dir_path = "";
		if (!"".equals(requestPath)) {
			String str = requestPath.substring(0, requestPath.length() - 1);
			moveup_dir_path = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
		}
		
		//排序形式，name or size or type
		String order = request.getParameter("order") != null ? request.getParameter("order").toLowerCase() : "name";
		
		File currentPathFile = new File(rootPath + "/" + requestPath);
		//图片扩展名
		String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
		if(!currentPathFile.isDirectory()){
//			renderText("Directory does not exist.");
			renderTemplateJson("0", "Directory does not exist.");
			return;
		}
		//遍历目录取的文件信息
		List<Hashtable> fileList = new ArrayList<Hashtable>();
		if(currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				String fileName = file.getName();
				if(file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if(file.isFile()){
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
			
			// 排序
			if ("size".equals(order)) {
				Collections.sort(fileList, new SizeComparator());
			} else if ("type".equals(order)) {
				Collections.sort(fileList, new TypeComparator());
			} else {
				Collections.sort(fileList, new NameComparator());
			}
			
			JSONObject result = new JSONObject();
			result.put("moveup_dir_path", moveup_dir_path);
			result.put("current_dir_path", requestPath);
			result.put("current_url", basePath + requestPath);
			result.put("total_count", fileList.size());
			result.put("file_list", fileList);
			
			renderJson(result);
		}
		return;
	}
	
	private String imgUploadResult(int code, String message) {
		JSONObject obj = new JSONObject();
		String type = "";
		if (code == 0) type = "url";
		if (code == 1) type = "message";
		obj.put("error", code);
		obj.put(type, message);
		return obj.toString();
	}

	public class NameComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable)a;
			Hashtable hashB = (Hashtable)b;
			if (((Boolean)hashA.get("is_dir")) && !((Boolean)hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)hashA.get("is_dir")) && ((Boolean)hashB.get("is_dir"))) {
				return 1;
			} else {
				return ((String)hashA.get("filename")).compareTo((String)hashB.get("filename"));
			}
		}
	}
	public class SizeComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable)a;
			Hashtable hashB = (Hashtable)b;
			if (((Boolean)hashA.get("is_dir")) && !((Boolean)hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)hashA.get("is_dir")) && ((Boolean)hashB.get("is_dir"))) {
				return 1;
			} else {
				if (((Long)hashA.get("filesize")) > ((Long)hashB.get("filesize"))) {
					return 1;
				} else if (((Long)hashA.get("filesize")) < ((Long)hashB.get("filesize"))) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
	public class TypeComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable)a;
			Hashtable hashB = (Hashtable)b;
			if (((Boolean)hashA.get("is_dir")) && !((Boolean)hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)hashA.get("is_dir")) && ((Boolean)hashB.get("is_dir"))) {
				return 1;
			} else {
				return ((String)hashA.get("filetype")).compareTo((String)hashB.get("filetype"));
			}
		}
	}
	
}

