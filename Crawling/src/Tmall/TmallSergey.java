package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import database.JDBCConnection;
import Utils.WebDriverUtils;

public class TmallSergey {
	public static void main(String[] args){
		crawl();
	}
	
	
	public static void  crawl(){
		Map<String,String> map = getMap();
		Map<String,List<Map<String, String>>> maps = new HashMap<String,List<Map<String, String>>>();
		WebDriver webDriver = new WebDriverUtils().getDriver("firefox");
		for(String shopName : map.keySet()){
			String url = map.get(shopName);
			List<Map<String, String>> mapList = maps.get(shopName);
			if(mapList == null){
				mapList = new ArrayList<Map<String,String>>();
			}
			webDriver.manage().window().maximize();
			
			TmallSearchResult.closeLoginFrame(webDriver);
			
			webDriver.get(url);
			getData(webDriver,mapList,1,"","");
			maps.put(shopName, mapList);
		}
		
		saveData(maps);
	}
	
	private static void saveData(
			Map<String, List<Map<String, String>>> maps) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into yili_copy(skuId,title,category,price,itemSum,shopName) values(?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(String shopName : maps.keySet()){
				List<Map<String, String>> list = maps.get(shopName);
				for(Map<String,String> itemMap : list){
					pst.setString(1, itemMap.get("itemId"));
					pst.setString(2, itemMap.get("itemName"));
					pst.setString(3, itemMap.get("category"));
					pst.setString(4, itemMap.get("price"));
					pst.setString(5, itemMap.get("itemSum"));
					pst.setString(6, shopName);
					pst.addBatch();
				}
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**获取页数
	 * @param areaName 
	 * @param category */
	private static void getData(WebDriver webDriver, List<Map<String, String>> mapList, int pageIndex, 
			String areaName, String category) {
		// TODO Auto-generated method stub
		try {
			webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			
			
			TmallSearchResult.closeLoginFrame(webDriver);
			getItemList(webDriver,mapList,category);
			
			pageIndex++;
			Thread.sleep((new Random().nextInt(5) + 2) * 1000);
			
			List<WebElement> list = webDriver.findElement(By.className("pagination")).findElements(By.tagName("a"));
			for(WebElement web : list){
				if(web.getText().equals("下一页")){
					if(web.getAttribute("class").equals("disable")){
						System.out.println("没有下一页啦~~~~~~~");
					}else{
						System.out.println("点击下一页~~~~~~~~");
						web.click();
						getData(webDriver,mapList,pageIndex,areaName,category);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**商品列表信息
	 * @param category */
	public static void getItemList(WebDriver webDriver,
			List<Map<String, String>> mapList, String category) {
		// TODO Auto-generated method stub
		List<WebElement> list = webDriver.findElements(By.tagName("dl"));
		for(WebElement we : list){
			Map<String,String> map = new HashMap<String,String>();
			String itemId = we.getAttribute("data-id");
			map.put("itemId", itemId);
			System.out.println(itemId);
			
			map.put("category",category);
			String itemName = we.findElement(By.className("detail")).findElement(By.tagName("a")).getText();
			map.put("itemName", itemName);
			System.out.println(itemName);
			
			String href = we.findElement(By.className("detail")).findElement(By.tagName("a")).getAttribute("href");
			System.out.println(href);
			map.put("href", href);
			
			String price = we.findElement(By.className("c-price")).getText();
			System.out.println(price);
			map.put("price", price);
			
			String itemSum = "0";
			itemSum = we.findElement(By.className("sale-num")).getText();
			System.out.println(itemSum);
			map.put("itemSum", itemSum);
			mapList.add(map);
		}
	}
	public static Map<String,String> getMap(){
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("哲品家居旗舰店", "https://zens.tmall.com/search.htm?spm=a1z10.1-b.w5002-9038466396.1.1d51767b2Grlz1&search=y");
		map.put("哲品家居集市店", "https://zhepinjiaju.jiyoujia.com/search.htm?spm=a1z10.3-c.w4002-1293493068.27.3ff19e11kqwnaY"
				+ "&_ksTS=1504172890050_395&callback=jsonp396&mid=w-1293493068-0&wid=1293493068&path=%2Fsearch.htm&orderType=hotsell_desc");
		map.put("质造出品", "https://zz2013.jiyoujia.com/search.htm?spm=2013.1.w5002-16590526750.1.4b556901jMja9Z&search=y");
		return map;
	}
 }
