package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import database.JDBCConnection;



public class ExcelUtils {
	public static void main(String[] args){
		//readExcel();
		getTaskList("通天塔");
	}
	/**
	 * 列表数据改成从数据库里获取
	 * */
	public static List<Map<String,String>> getTaskList(String platform){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		/*try {
			Connection con = JDBCConnection.connectToServer("data");
			String sql = "select projectId,projectName,url,terminal,brand,category from JD_ActivityTask where platform = ?";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, platform);
			//pst.setString(2, "249596");
			ResultSet rst = pst.executeQuery();
			while(rst.next()){
				try {
					Map<String,String> map = new HashMap<String, String>();
					String projectId = rst.getString(1);
					String projectName = rst.getString(2);
					String url = rst.getString(3);
					String terminal = rst.getString(4);
					String brand = rst.getString(5);
					String category = rst.getString(6);
					System.out.println(url);
					if(url.indexOf("encryActivityId") != -1){
						String encryActivityId =  url.substring(url.indexOf("encryActivityId") + 16,url.indexOf("activityName") - 1);
						map.put("encryId", encryActivityId);
					}
					map.put("name", projectName);
					map.put("id", projectId);
					map.put("terminal", terminal);
					map.put("brand", brand);
					map.put("category", category);
					mapList.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		Map<String,String> map = new HashMap<String, String>();
		map.put("name", "SK-II改写肌肤命运");
		map.put("id", "520105");
		map.put("encryId", "");
		map.put("terminal", "Mobile");
		/*Map<String,String> map2 = new HashMap<String, String>();
		map2.put("name", "汰渍x福临门联合大促");
		map2.put("id", "00045128");
		map2.put("encryId", "2hMdtFyndfFr716AMWdBZgESQZ5t");
		
		mapList.add(map2);*/
		mapList.add(map);
		return mapList;
	}
	public static List<Map<String,String>> readExcel(){
		XSSFWorkbook book = null;
		InputStream ins = null;
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		try {
			ins = new FileInputStream(new File("D:/bebal.xlsx"));
			book = new XSSFWorkbook(ins);
			XSSFSheet sheet = book.getSheetAt(0);
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				String productId = "";
				if(row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC){
					productId = row.getCell(3).getNumericCellValue() + "";
				}else if(row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING){
					productId = row.getCell(3).getStringCellValue();
				}
				String url = row.getCell(4).getStringCellValue();
				String encryActivityId =  url.substring(url.indexOf("encryActivityId") + 16,url.indexOf("activityName") - 1);
				String productName = row.getCell(2).getStringCellValue();
				if(!productId.equals("") && !productId.equals("—")){
					Map<String,String> map = new HashMap<String, String>();
					if(productId.indexOf(".") != -1){
						productId = productId.substring(0, productId.indexOf("."));
					}
					map.put("name", productName);
					map.put("id", productId);
					map.put("encryId", encryActivityId);
					mapList.add(map);
					System.out.println(productName + "====" + productId + "==" + encryActivityId);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(ins != null){
					ins.close();
				}
				if(book != null){
					book.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		/*Map<String,String> map = new HashMap<String, String>();
		map.put("id", "00013090");
		map.put("name", "汰渍碧浪活动页面");
		map.put("encryId", "253MrZm7pNNx63EWAXj26dHUXonB");
		mapList.add(map);*/
		return mapList;
	}
	
	/**
	 * 万万账号下的活动列表
	 * */
	public static List<Map<String,String>> getWanwanList(){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("id", "00032947");
		map.put("name", "【个护会场】高端轻奢");
		map.put("encryId", "3eQ3verSMF6V1bYK1ZuuXmWB5PZF");
		mapList.add(map);
		return mapList;
	}
}
