package com.raincc.robot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.kit.PathKit;
import com.raincc.robot.entity.BaseModel;

@SuppressWarnings("rawtypes")
public class ExcelUtil<T extends BaseModel> {
	@SuppressWarnings("deprecation")
	public void createExcelSheet(List<T> list, String excelName,
			List<String> nameFeild,String[] aStrings,String downFile) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(excelName);
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//		HSSFCell cell = row.createCell((short) 0);
		
		row = sheet.createRow((int) 0);
		for (int f = 0; f < aStrings.length; f++) {
			String values = aStrings[f];
			if(StringUtils.isNotBlank(values)){
				row.createCell((short) f).setCellValue(values);
			}else {
				row.createCell((short) f).setCellValue("");
			}
		}
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i+1);
			T rowClass = (T) list.get(i);
			for (int j = 0; j < nameFeild.size(); j++) {
				if(rowClass.get(nameFeild.get(j)) != null)
				{
					String aValue = rowClass.get(nameFeild.get(j)).toString();
					System.out.println(aValue);
					row.createCell((short) j).setCellValue(aValue);
				}
				else
				{
					row.createCell((short) j).setCellValue("");
				}
			}
		}
		try {
			 File file = new File(PathKit.getWebRootPath()+downFile);
			  //判断文件夹是否存在,如果不存在则创建文件夹
			  if (!file.exists()) {
				  file.mkdir();
			  }
			//FileOutputStream fout = new FileOutputStream("/data/apache-tomcat-7.0.57/webapps/ROOT/upload/"+excelName+".xls");
			System.out.println(PathKit.getWebRootPath()+downFile+excelName+".xls");
			FileOutputStream fout = new FileOutputStream(PathKit.getWebRootPath()+downFile+excelName+".xls");
			wb.write(fout);
			fout.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
