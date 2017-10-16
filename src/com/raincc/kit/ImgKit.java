package com.raincc.kit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;

import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@SuppressWarnings("restriction")
public class ImgKit {
	
	public static String saveWebImg(String urlString, String dir, String ext) {
		String localPath = null;
		localPath = "/upload/" + dir + "/" + ImgKit.getDatePath();
		File tarFile = new File("/" + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + "/" + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
		// 构造URL  
        URL url;
        OutputStream os = null;
        InputStream is = null;
		try {
			url = new URL(urlString);
			// 打开连接  
			URLConnection con = url.openConnection();  
			//设置请求超时为5s  
			con.setConnectTimeout(5*1000);  
			// 输入流  
			is = con.getInputStream();  
			
			// 1K的数据缓冲  
			byte[] bs = new byte[1024];  
			// 读取到的数据长度  
			int len;  
			// 输出的文件流  
			if(!tarFile.getParentFile().exists()){  
				tarFile.getParentFile().mkdirs();  
			}  
			os = new FileOutputStream(tarFile);  
			// 开始读取  
			while ((len = is.read(bs)) != -1) {  
				os.write(bs, 0, len);  
			}  
			return localPath;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 完毕，关闭所有链接  
			try {
				if (os != null)
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
		}
		return null;
	}
	
	public static String saveImg(UploadFile file, String basePath, int maxWidth) {
		String localPath = null;
		String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
		localPath = basePath + ImgKit.getDatePath();
		File tarFile = new File(PathKit.getWebRootPath() + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + "/" + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
//		saveImgTo(file.getFile(), tarFile, maxWidth, 0);
		try {
			FileUtils.copyFile(file.getFile(), tarFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return localPath;
	}
	
	public static String saveProductImg(UploadFile file) {
		String localPath = null;
		String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
		localPath = "/upload/product/" + ImgKit.getDatePath();
		File tarFile = new File(PathKit.getWebRootPath() + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + File.separator + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
		saveImgTo(file.getFile(), tarFile, 0, 0);
		
		return localPath;
	}
	
	public static String saveWeixinImg(UploadFile file) {
		String localPath = null;
		String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
		localPath = "/upload/weixin/" + ImgKit.getDatePath();
		File tarFile = new File(PathKit.getWebRootPath() + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + File.separator + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
		saveImgTo(file.getFile(), tarFile, 0, 0);
		
		return localPath;
	}
	
	public static void saveImgTo(File oriFile, File toFile, int tarWidth, int tarHeight) {
		System.out.println(toFile);
		try {
			BufferedImage src = ImageIO.read(oriFile);
			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();
			System.out.println("srcSize::" + srcWidth + "," + srcHeight);
			
			if (tarWidth == 0 && tarHeight == 0) {
				tarWidth = srcWidth;
				tarHeight = srcHeight;
			} else {
				if (tarWidth == 0) {
					tarWidth = (srcWidth * tarHeight) / srcHeight;
				}
				if (tarHeight == 0) {
					tarHeight = (tarWidth * srcHeight) / srcWidth;
				}
			}
			
			System.out.println("tagSize::" + tarWidth + "," + tarHeight);
			
//		FileOutputStream fileOut = new FileOutputStream(tarPath);
			
			ImageIcon ii = new ImageIcon(oriFile.getCanonicalPath());  
			Image i = ii.getImage();  
			Image resizedImage = i.getScaledInstance(tarWidth, tarHeight, Image.SCALE_SMOOTH); 
			
			// This code ensures that all the pixels in the image are loaded.  
			Image temp = new ImageIcon(resizedImage).getImage();  
			
			// Create the buffered image.  
			BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);  
			
			// Copy image to buffered image.  
			Graphics g = bufferedImage.createGraphics();  
			
			// Clear background and paint the image.  
			g.setColor(Color.white);  
			g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));  
			g.drawImage(temp, 0, 0, null);  
			g.dispose();  
			
			// Soften.  
			float softenFactor = 0.05f;  
			float[] softenArray = { 0, softenFactor, 0, softenFactor,  
					1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };  
			Kernel kernel = new Kernel(3, 3, softenArray);  
			ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);  
			bufferedImage = cOp.filter(bufferedImage, null);
			
			// Write the jpeg to a file.  
			FileOutputStream out = new FileOutputStream(toFile);
			// Encodes image as a JPEG data stream  
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
			
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);  
			
			param.setQuality(1f, true);  
			
			encoder.setJPEGEncodeParam(param);  
			encoder.encode(bufferedImage);  
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
  
//        
        
//        FileInputStream tarImageIs = new FileInputStream(toFile);
//        OutputStream imageOut = response.getOutputStream();
//        byte[] buf = new byte[1024];
//        int count = 0;
//        while ((count = tarImageIs.read(buf)) >= 0) {
//        	imageOut.write(buf, 0, count);
//        }
//        tarImageIs.close();
//        imageOut.close();
	}
	
	/**
	 * 2014/05/06
	 * @return
	 */
	public static String getDatePath() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return year + "/" + (month<9?"0":"")+month + "/" + (day<9?"0":"")+day;
	}

	public static String saveIdCardImg(UploadFile file) {
		String localPath = null;
		String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
		localPath = "/upload/idcard/" + ImgKit.getDatePath();
		File tarFile = new File(PathKit.getWebRootPath() + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + File.separator + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
		saveImgTo(file.getFile(), tarFile, 800, 0);
		
		return localPath;
	}
	
	public static String saveArticleImg(UploadFile file) {
		String localPath = null;
		String ext = file.getFileName().substring(file.getFileName().indexOf(".")).toLowerCase();
		localPath = "/upload/arttcle/" + ImgKit.getDatePath();
		File tarFile = new File(PathKit.getWebRootPath() + localPath);
		if (!tarFile.exists()) {
			tarFile.mkdirs();
		}
		localPath = localPath + File.separator + System.currentTimeMillis() + "" + new Random().nextInt(99) + ext;
		tarFile = new File(PathKit.getWebRootPath() + localPath);
		
		saveImgTo(file.getFile(), tarFile);
		
		return localPath;
	}

	private static void saveImgTo(File oriFile, File toFile) {
		try {
			BufferedImage src = ImageIO.read(oriFile);
			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();
			System.out.println("srcSize::" + srcWidth + "," + srcHeight);

			// Write the jpeg to a file.  
			FileOutputStream out = new FileOutputStream(toFile);
			// Encodes image as a JPEG data stream  
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
			
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(src);  
			
			param.setQuality(1f, true);  
			
			encoder.setJPEGEncodeParam(param);  
			encoder.encode(src);  
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  		
	}

}
