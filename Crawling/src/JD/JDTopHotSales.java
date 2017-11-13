package JD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import database.JDBCConnection;
import Utils.HtmlGenUtils;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年11月10日 下午5:11:29
 * 京东热卖排行榜
 */
public class JDTopHotSales {
	public static void main(String[] args){
		crawl();
		//getSkuList();
	}
	
	public static void crawl(){
		List<Map<String,String>> skuList = getSkuList();
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : skuList){
			try {
				String cateId = map.get("skuId");
				String url = "https://ch.jd.com/hotsale2?cateid="+cateId+"&source=pc&callback=top_sale&_=" + System.currentTimeMillis();
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader("Host","ch.jd.com");
				get.setHeader("Referer","https://top.jd.com/sale?cateId=" + cateId);
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				String htmlCode = EntityUtils.toString(client.execute(get).getEntity());
				parseHtmlCode(htmlCode,mapList,map);
				Thread.sleep(new Random().nextInt(3) * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		saveData(mapList);
	}
	
	
	/**
	 * 保存数据
	 * */
	private static void saveData(List<Map<String, String>> mapList) {
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = JDBCConnection.connectToLocal("HC");
			String sql = "insert into JDHotSaleTop(dataTime,cateId,cateName,currentRank,skuName,skuId,hotScore,isNew,jdSale,lowInDay,venderId)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = con.prepareStatement(sql);
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd HH:mm", 0);
			for(Map<String,String> map : mapList){
				pst.setString(1, dataTime);
				pst.setString(2, map.get("cateId"));
				pst.setString(3, map.get("cateName"));
				pst.setString(4, map.get("currentRank"));
				pst.setString(5, map.get("skuName"));
				pst.setString(6, map.get("skuId"));
				pst.setString(7, map.get("hotScore"));
				pst.setString(8, map.get("isNew"));
				pst.setString(9, map.get("jdSale"));
				pst.setString(10, map.get("lowInDay"));
				pst.setString(11, map.get("venderId"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入商品热卖榜单数据成功");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解析排行榜json数据
	 * @param mapList 
	 * @param paramMap 
	 * */
	private static void parseHtmlCode(String htmlCode, List<Map<String, String>> mapList, Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		try {
			htmlCode = htmlCode.substring(htmlCode.indexOf("(") + 1, htmlCode.lastIndexOf(")"));
			System.out.println(htmlCode);
			JSONArray productList = JSONObject.fromObject(htmlCode).getJSONArray("products");
			Map<String,String> map = null;
			for(Object obj : productList){
				JSONObject productJson = JSONObject.fromObject(obj);
				map = new HashMap<String, String>();
				String currentRank = productJson.getString("currentRank");
				map.put("currentRank", currentRank);
				String skuName = productJson.getString("wareName");
				map.put("skuName", skuName);
				String skuId = productJson.getString("wareId");
				map.put("skuId", skuId);
				String hotScore = productJson.getString("hotScore");
				map.put("hotScore", hotScore);
				String isNew = productJson.getString("isNew");
				map.put("isNew", isNew);
				String jdSale = productJson.getString("jdSale");
				map.put("jdSale", jdSale);
				String lowInDay = productJson.getString("lowInDay");
				map.put("lowInDay", lowInDay);
				String venderId = productJson.getString("venderId");
				map.put("venderId", venderId);
				map.put("cateId", paramMap.get("skuId"));
				map.put("cateName", paramMap.get("category"));
				mapList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<Map<String, String>> getSkuList() {
		Workbook book = null;
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			File file = new File("E:/software/hotSaleSku.xlsx");
			try {
				book = new XSSFWorkbook(file);
			} catch (Exception e) {
				book = new HSSFWorkbook(new FileInputStream(file));
			}
			Sheet sheet = book.getSheetAt(0);
			Map<String,String> map = null;
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				map = new HashMap<String, String>();
				String skuId = row.getCell(2).getStringCellValue();
				String category = row.getCell(1).getStringCellValue();
				System.out.println(skuId);
				if(!skuId.equals("1671")){
					map.put("skuId", skuId);
					map.put("category", category);
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				book.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*Map<String,String> map = new HashMap<String, String>();
		map.put("skuId", "1671");
		map.put("category", "纸品湿巾");
		list.add(map);*/
		return list;
	}
}
