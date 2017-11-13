package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import database.JDBCConnection;
import Utils.WebDriverUtils;

public class YiliSearch {
	public static void main(String[] args){
		writeExcel();
		//crawl();
	}
	
	public static void writeExcel(){
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "select skuId,title,category,price,itemSum from yili_copy where shopName='哲品家居旗舰店' and sellCount is null";
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			WebDriver webDriver = new WebDriverUtils().getDriver("firefox");
			webDriver.manage().window().maximize();
			
			while(rs.next()){
				try {
					String updateSql = "update yili_copy set sellCount=? where skuId=?";
					PreparedStatement updatePst = con.prepareStatement(updateSql);
					String itemId = rs.getString(1);
					String sellCount = getSellCount(itemId,webDriver);
					updatePst.setString(1, sellCount);
					updatePst.setString(2, itemId);
					updatePst.executeUpdate();
					System.out.println("修改成功");
					Thread.sleep((new Random().nextInt(5)) * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void crawl(){
		Map<String,String> map = getMap();
		List<String> areaList = Arrays.asList(new String[]{"上海","广州","北京"});
		WebDriver webDriver = new WebDriverUtils().getDriver("firefox");
		webDriver.manage().window().maximize();
		List<Map<String,List<Map<String,String>>>> list = new ArrayList<Map<String,List<Map<String,String>>>>();
		for(String category : map.keySet()){
			String url = map.get(category);
			Map<String,List<Map<String,String>>> areaMap = new HashMap<String, List<Map<String,String>>>();
			for(String areaName : areaList){
				List<Map<String,String>> mapList = areaMap.get(areaName);
				if(mapList == null){
					mapList = new ArrayList<Map<String,String>>();
				}
				TmallSearchResult.loadChaoshiPage(webDriver);
				
				TmallSearchResult.closeLoginFrame(webDriver);
				
				//换地区
				TmallSearchResult.changeArea(webDriver,areaName);
				
				webDriver.get(url);
				getData(webDriver, mapList,1,areaName,category);
				areaMap.put(areaName, mapList);
			}
			list.add(areaMap);
		}
		//distinctData(list,webDriver);
		saveData(list);
	}
	
	
	
	private static void saveData(
			List<Map<String, List<Map<String, String>>>> list) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into yili_copy(skuId,title,category,price,itemSum) values(?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String, List<Map<String, String>>> map : list){
				Map<String,Map<String,String>> itemList = TmallSearchResult.distinctItem(map);
				Iterator<String> it = itemList.keySet().iterator();
				while(it.hasNext()){
					String itemId = it.next();
					Map<String,String> itemMap = itemList.get(itemId);
					pst.setString(1, itemMap.get("itemId"));
					pst.setString(2, itemMap.get("itemName"));
					pst.setString(3, itemMap.get("category"));
					pst.setString(4, itemMap.get("price"));
					pst.setString(5, itemMap.get("itemSum"));
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
	
	private static String getSellCount(String itemId, WebDriver webDriver) {
		String url = "https://detail.m.tmall.com/item.htm?id=" + itemId;
		webDriver.get(url);
		String sellCount = webDriver.findElement(By.className("sales")).getText();
		sellCount = sellCount.substring(sellCount.indexOf(" ") + 1, sellCount.length() - 1);
		System.out.println(sellCount);
		return sellCount;
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
			
			boolean nextPage = TmallSearchResult.hasElement(webDriver,By.className("page-next"));
			if(nextPage){
				System.out.println("点击下一页~~~~~~~~");
				webDriver.findElement(By.className("page-next")).click();
				getData(webDriver,mapList,pageIndex,areaName,category);
			}else{
				System.out.println("没有下一页啦~~~~~~~");
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
		List<WebElement> list = webDriver.findElements(By.className("product"));
		for(WebElement we : list){
			Map<String,String> map = new HashMap<String,String>();
			String itemId = we.getAttribute("data-itemid");
			map.put("itemId", itemId);
			System.out.println(itemId);
			
			map.put("category",category);
			String itemName = we.findElement(By.tagName("h3")).getText();
			map.put("itemName", itemName);
			System.out.println(itemName);
			
			String href = we.findElement(By.className("product-img")).findElement(By.tagName("a")).getAttribute("href");
			System.out.println(href);
			map.put("href", href);
			
			String price = we.findElement(By.className("ui-price")).findElement(By.tagName("strong")).getText();
			System.out.println(price);
			map.put("price", price);
			
			String itemSum = "0";
			if(TmallSearchResult.hasElement(we.findElement(By.className("item-sum")),By.tagName("strong"))){
				itemSum = we.findElement(By.className("item-sum")).findElement(By.tagName("strong")).getText();
				System.out.println(itemSum);
			}else{
				System.out.println("没有销量~~~~~~");
			}
			map.put("itemSum", itemSum);
			mapList.add(map);
		}
	}

	public static Map<String,String> getMap(){
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("婴幼儿奶粉", "https://list.tmall.com//search_product.htm?cat=50522012&q=%C4%CC%B7%DB&sort=td"
				+ "&style=g&search_condition=23&user_id=725677994&from=chaoshi..pc_1_searchbutton"
				+ "&active=1&industryCatId=50492009");
		map.put("孕产妇奶粉", "https://list.tmall.com/search_product.htm?cat=50534014&q=%C4%CC%B7%DB"
				+ "&sort=td&style=g&search_condition=23&user_id=725677994"
				+ "&active=1&industryCatId=50546017#J_Filter");
		map.put("成人奶粉", "https://list.tmall.com/search_product.htm?cat=50492035&q=%C4%CC%B7%DB&sort=td"
				+ "&style=g&search_condition=23&user_id=725677994&active=1&industryCatId=50506028#J_Filter");
		/*map.put("乳酸菌", "https://list.tmall.com/search_product.htm?cat=57658002&q=%D2%C1%C0%FB"
				+ "&sort=s&style=g&search_condition=23&user_id=725677994&active=1&industryCatId=53152001");
		map.put("乳品饮料/酸奶", "https://list.tmall.com/search_product.htm?cat=56828014&q=%D2%C1%C0%FB"
				+ "&sort=s&style=g&search_condition=23&user_id=725677994&active=1&industryCatId=53152001");
		map.put("西式糕点", "https://list.tmall.com/search_product.htm?cat=51274008&q=%D2%C1%C0%FB"
				+ "&sort=s&style=g&search_condition=23&user_id=725677994&active=1&industryCatId=51284011");*/
		return map;
	}
}
