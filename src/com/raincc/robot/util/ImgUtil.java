package com.raincc.robot.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
public class ImgUtil {
	private String imageReload;

	public static Logger _log = Logger.getLogger(ImgUtil.class);
	
	public static BufferedImage resize(BufferedImage source, int targetW,
			int targetH) {
		// targetW，targetH分别表示目标长和宽
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		// 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
		// 则将下面的if else语句注释即可
		if (sx > sy) {
			sx = sy;
			targetW = (int) (sx * source.getWidth());
		} else {
			sy = sx;
			targetH = (int) (sy * source.getHeight());
		}
		if (type == BufferedImage.TYPE_CUSTOM) { 
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
					targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			target = new BufferedImage(targetW, targetH, type);
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}
	
	public static void saveImageAsJpg(String fromFileStr, String saveToFileStr,
			int width, int hight) throws Exception {
		BufferedImage srcImage;
		// String ex =
		// fromFileStr.substring(fromFileStr.indexOf("."),fromFileStr.length());
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		// System.out.println(ex);
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		srcImage = ImageIO.read(fromFile);
		if (width > 0 || hight > 0) {
			srcImage = resize(srcImage, width, hight);
		}
		ImageIO.write(srcImage, imgType, saveFile);
	}

	public void setImageReload(String imageReload) {
		this.imageReload = imageReload;
	}

	public String getImageReload() {
		return imageReload;
	}
	
	/**
	 * 创建文字图片
	 * @param allLine 所有行
	 * @param tarFile 生成对应文件
	 * @param maxWidth 图片最大宽度
	 */
	public static void createTextImg(List<String> allLine, File tarFile) {
		int maxWidth = 0;
        for (String line : allLine) {
        	if (line.length() > maxWidth) {
        		maxWidth = line.length();
        	}
        }
        
        Integer x=3,y=12;
        
		int height = 16*allLine.size();
        int width = 7 * maxWidth;
        FileOutputStream fos = null;
        BufferedImage bi = null;
		try {
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //
	        Graphics2D g = bi.createGraphics();
	        g.setBackground(Color.WHITE);
	        g.clearRect(0, 0, width, height);
	        Font mFont = new Font("arial",Font.PLAIN,14);//默认字体
	        g.setColor(new Color(Integer.parseInt("000000",16)));
	        g.setFont(mFont);
	        
	        for (String line : allLine) {
	        	addLine(line, g, x, y);
	        	y = y + 16;
	        }
	        
	        // end draw:
	        g.dispose();
	        bi.flush();
	        
	        ImageIO.write(bi, "jpg", tarFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void addLine(String line, Graphics2D g, Integer x, Integer y) {
		g.drawString(line,x,y);
	}

}
