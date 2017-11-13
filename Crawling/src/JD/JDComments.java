package JD;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import database.JDBCConnection;
import Tmall.TmallSearchResult;
import Utils.HtmlGenUtils;
import Utils.WebDriverUtils;

public class JDComments {
	public static void main(String[] args) throws Exception{
		crawItemComments();
	}
	
	public static List<String> getList(){
		List<String> itemList = new ArrayList<String>();
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "select itemId,itemName from vinda_jdData_copy";
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				String itemId = rs.getString(1);
				System.out.println(itemId);
				itemList.add(itemId);
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return itemList;
	}
	/**
	 * 获取搜索结果页
	 * */
	public static List<String> crawItemList(){
		/*List<String> itemList = new ArrayList<String>();
		List<String> wordList = Arrays.asList(new String[]{"纸巾","卫生巾"});
		for(String word : wordList){
			try {
				WebDriver driver = new WebDriverUtils().getDriver("firefox");
				driver.manage().window().maximize();
				String url = "https://search.jd.com/Search?keyword="+URLEncoder.encode(word, "utf-8")+"&enc=utf-8"
						+ "&qrst=1&rt=1&stop=1&vt=2&wq="+URLEncoder.encode(word, "utf-8")+"&psort=3&click=0";
				driver.get(url);
				int index = 1;
				getItemList(driver,itemList,index);
				
				driver.quit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		

		List<String> itemList = Arrays.asList(new String[]{"3761608 ","2209596","10729545227","2906164"});
		return itemList;
	}
	
	public static void crawItemComments(){
		List<String> itemIdList = crawItemList();
		List<Map<String,String>> mapList = null;
		for(String itemId : itemIdList){
			System.out.println(itemId);
			int page = 0;
			boolean hasPage = true;
			mapList = new ArrayList<Map<String,String>>();
			while(hasPage){
				String url = "https://club.jd.com/comment/skuProductPageComments.action?callback=fetchJSON_comment98vv15"
						+ "&productId="+itemId+"&score=0&sortType=6&page="+page+"&pageSize=100&isShadowSku=0&fold=1";
				String htmlCode = getComments(url,itemId);
				page++;
				if(!htmlCode.equals("")){
					hasPage = parseHtmlCode(htmlCode,mapList,page);
				}else{
					hasPage = false;
				}
			}
			if(mapList.size() > 0){
				saveJdData(mapList);
			}
		}
	}
	
	/**解析评论json数据
	 * @param page */
	private static boolean parseHtmlCode(String htmlCode,List<Map<String,String>> mapList, int page) {
		// TODO Auto-generated method stub
		boolean hasComment = true;
		try {
			System.out.println(htmlCode);
			htmlCode = htmlCode.substring(htmlCode.indexOf("vv15") + 5, htmlCode.length() - 2);
			JSONArray conmmentsJson = JSONObject.fromObject(htmlCode).getJSONArray("comments");
			if(conmmentsJson.size() == 0){
				 hasComment = false;
			}
			int maxPage = JSONObject.fromObject(htmlCode).getInt("maxPage");
			if(page == maxPage){
				hasComment = false;
			}
			Map<String,String> map = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String lastThirtyDay = HtmlGenUtils.getDataTime("yyyy-MM-dd", -30);
			for(Object obj : conmmentsJson){
				JSONObject json = JSONObject.fromObject(obj);
				map = new HashMap<String, String>();
				String id = json.getString("referenceId");
				map.put("itemId", id);
				String itemName = json.getString("referenceName");
				map.put("itemName", itemName);
				String commentTime = json.getString("creationTime");
				System.out.println(itemName + "~~~~~" + commentTime);
				if((format.parse(commentTime).getTime()) < (format.parse(lastThirtyDay).getTime())){
					hasComment = false;
					break;
				}
				map.put("nickName", json.getString("nickname"));
				map.put("guid", json.getString("guid"));
				map.put("userExpValue", json.getString("userExpValue"));
				map.put("userLevelName", json.getString("userLevelName"));
				map.put("content", json.getString("content"));
				map.put("commentTime", commentTime);
				String orderTime = json.getString("referenceTime");
				map.put("orderTime", orderTime);
				mapList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasComment;
	}

	/**获取评论的
	 * @param itemId */
	private static String getComments(String url, String itemId) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","club.jd.com");
			get.setHeader("Referer","https://item.jd.com/"+itemId+".html");
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			htmlCode = EntityUtils.toString(response.getEntity(), "utf-8");
			Thread.sleep((new Random().nextInt(5) + 1) * 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}

	/**
	 * 保存到数据库先
	 * */
	private static void saveJdData(List<Map<String,String>> mapList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into vinda_jdData_copy(itemId,itemName,commentTime,orderTime,nickName,guid,userExpValue,userLevelName,content) values(?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map: mapList){
				pst.setString(1, map.get("itemId"));
				pst.setString(2, map.get("itemName"));
				pst.setString(3, map.get("commentTime"));
				pst.setString(4, map.get("orderTime"));
				pst.setString(5, map.get("nickName"));
				pst.setString(6, map.get("guid"));
				pst.setString(7, map.get("userExpValue"));
				pst.setString(8, map.get("userLevelName"));
				pst.setString(9, map.get("content"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
			System.out.println("插入数据成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取每页的商品id
	 * @param index 
	 * */
	private static void getItemList(WebDriver driver, List<String> itemList, int index) {
		// TODO Auto-generated method stub
		try {
			controScroll(driver);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			List<WebElement> list = driver.findElement(By.id("J_goodsList")).findElements(By.tagName("li"));
			for(WebElement element : list){
				String itemId = element.getAttribute("data-sku");
				System.out.println(itemId);
				itemList.add(itemId);
			}
			boolean nextPage = TmallSearchResult.hasElement(driver,By.className("pn-next"));
			if(index == 10){
				System.out.println("没有下一页啦~~~~~~~");
			}else{
				System.out.println("点击下一页~~~~~~~~");
				driver.findElement(By.className("pn-next")).click();
				index++;
				getItemList(driver,itemList,index);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 拖动滚动条
	 * */
	public static void controScroll(WebDriver driver) {
		// TODO Auto-generated method stub
		try {
			String scroll = "document.documentElement.scrollTop=500";
			JavascriptExecutor js = (JavascriptExecutor)driver;
			js.executeScript(scroll, "");
			
			Thread.sleep(500);
			String scroll2 = "document.documentElement.scrollTop=6000";
			JavascriptExecutor js2 = (JavascriptExecutor)driver;
			js2.executeScript(scroll2, "");
			Thread.sleep(1500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
