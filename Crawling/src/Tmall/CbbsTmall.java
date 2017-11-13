package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import database.JDBCConnection;
import Utils.HtmlGenUtils;


/**
 * 天猫自营平台
 * */
public class CbbsTmall{
	public static final String cookie = "locale=zh-cn; x=__ll%3D-1%26_ato%3D0; SCMLOCALE=zh-cn; hng=CN%7Czh-CN%7CCNY%7C156; _tb_token_=f518699908e31; uc3=sg2=BqfTAHKwbQ93CnDXETWBGISN1QDpvMrnAwpQnFeQFKk%3D&nk2=qTnDsZadVgqB1wFzBF8%3D&id2=W8rtrCZF0X7Q&vt3=F8dBzWRNZkgiozlE2lI%3D&lg2=UtASsssmOIJ0bQ%3D%3D; uss=BqeDsLiPVuWgzmzh1%2FCJb7ikHM3pHrFU2vE3cEKHjuz57GOcCHyNs6vn; lgc=%5Cu8BD7%5Cu654F%5Cu654F%5Cu654F%5Cu54C8%5Cu54C8%5Cu54C8; tracknick=%5Cu8BD7%5Cu654F%5Cu654F%5Cu654F%5Cu54C8%5Cu54C8%5Cu54C8; cookie2=3ca037c3a88efc571a7a0c2564b02c3d; t=c42b83b8ac1fc3f0e182470df6f21293; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; whl=-1%260%260%260; cs_gn=1; sm4=440103; _m_h5_tk=89a8a16f2ab48ccd0d6e841f42d0ec8a_1502333281952; _m_h5_tk_enc=9e115ee5c52818794fb5efb26c445d6a; l=As7Okk8pZWsaFoZ0FbCzyn8Gnq-Q5pJF; SCMSESSID=SARMJjqpf2byVg9nv2eESb0DyrQ; cna=5PfAEUubAVkCAbcGrW1ZlO0o; isg=AnBwr56l3f471IFNE2VWYgZxQT4CEVOlBvSVMGrB-UueJRPPE8ssk-VXCRu-";
	private static final String url = "http://cbbs.tmall.com/login";
	private String loginName = "ysyjtmcs@163.com";
	private String pwd = "ysyj7890";
	public static Map<String,String> brandMap = new HashMap<String, String>();
	public static void main(String[] args) throws Exception{
		new CbbsTmall().run();
		//crawTrad();
	}
	
	public void run() throws Exception{
		/*System.setProperty("webdriver.firefox.bin", "E:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		WebDriver driver = new FirefoxDriver();*/
		System.setProperty("webdriver.chrome.driver", "E:/BaiduNetdiskDownload/chromedriver.exe");
		//WebDriver driver = new ChromeDriver();
		String browserUrl = "C:/Users/Administrator/AppData/Local/360Chrome/Chrome/Application/360chrome.exe";
		ChromeOptions options = new ChromeOptions();
		options.setBinary(browserUrl);
		WebDriver driver = new ChromeDriver(options);
		driver.get(url);
		driver.manage().window().maximize();
		
		driver.findElement(By.name("loginId")).clear();
		driver.findElement(By.name("loginId")).sendKeys(loginName);
		
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(pwd);
		
		Thread.sleep(1000);
		driver.findElement(By.id("nc_1_n1z")).click();
		new Actions(driver).dragAndDropBy(driver.findElement(By.id("nc_1_n1z")), 258, 0).perform();
	}
	
	
	public static void crawTrad(){
		Map<String,Map<String,String>> maps = brandCateGoryMap(false);
		Iterator<String> it = maps.keySet().iterator();
		Map<String,List<Map<String,String>>> mapList = new HashMap<String, List<Map<String,String>>>();
		String dataTime = "2017-01";
		while(it.hasNext()){
			String brandId = it.next();
			String brandName = brandMap.get(brandId);
			List<Map<String,String>> list = mapList.get(brandName);
			if(list == null){
				list = new ArrayList<Map<String,String>>();
			}
			Map<String,String> map = maps.get(brandId);
			for(String cateGoryId : map.keySet()){
				String category = map.get(cateGoryId);
				String jsonurl = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?query.brandId="+brandId+"&query.cate3Id="+cateGoryId+"&"
						+ "query.endTime="+dataTime+"&query.logicArea=-99999&query.startTime="+dataTime+"&serviceId=sm_table_category_leaf";
				System.out.println(jsonurl);
				getJsonData(jsonurl,list,dataTime,category);
			}
			mapList.put(brandName, list);
		}
		if(mapList.size() > 0){
			saveDate(mapList);
		}
	}
	
	private static void getJsonData(String jsonurl, List<Map<String, String>> list, String dateMonth, String category) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(3000);
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(jsonurl);
			get.setHeader("Cookie",cookie);
			get.setHeader("Host","dataweb.cbbs.tmall.com");
			get.setHeader("Origin","http://web.cbbs.tmall.com");
			get.setHeader("Referer","http://web.cbbs.tmall.com/pages/chaoshi/tradecomposition?spm=a224m.7959549.0.0.GB6O4m");
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
			JSONArray data = json.getJSONArray("data");
			for(Object obj : data){
				JSONArray value = JSONObject.fromObject(obj).getJSONObject("value").getJSONArray("data");
				for(Object obj2 : value){
					Map<String,String> map = new HashMap<String, String>();
					map.put("area", "华北");
					map.put("month", dateMonth);
					map.put("category", category);
					JSONObject item = JSONObject.fromObject(obj2);
					String cateLeafName = item.getString("cateLeafName");//叶子类目
					map.put("cateLeafName", cateLeafName);
					String payMoney = item.getString("payMoney");//支付金额
					map.put("payMoney", payMoney);
					String payMoneyPercentage  = item.getString("payMoneyPercentage");//支付占比
					map.put("payMoneyPercentage", payMoneyPercentage);
					String payNum = item.getString("payNum");//支付商品件数
					map.put("payNum", payNum);
					String buyerNum = item.getString("buyerNum");//支付买家数
					map.put("buyerNum", buyerNum);
					String payMordOrderNum = item.getString("payMordOrderNum");//父订单数
					map.put("payMordOrderNum", payMordOrderNum);
					String paySubOrderNum = item.getString("paySubOrderNum");//子订单数
					map.put("paySubOrderNum", paySubOrderNum);
					String paySubOrderAvg = item.getString("paySubOrderAvg");//子订单单价
					map.put("paySubOrderAvg", paySubOrderAvg);
					String payMoneyPerBuyer = item.getString("payMoneyPerBuyer");//支付客单价
					map.put("payMoneyPerBuyer", payMoneyPerBuyer);
					String payMoneyPerOrder = item.getString("payMoneyPerOrder");//笔单价
					map.put("payMoneyPerOrder", payMoneyPerOrder);
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存数据到数据库
	 * */
	private static void saveDate(Map<String, List<Map<String, String>>> mapList) {
		// TODO Auto-generated method stub
		try {
			Connection  con = JDBCConnection.connectToServer("data");
			System.out.println("连接数据库成功~");
			String sql = "insert into CsTmall_TransactionComposition(area,dataTime,brand,category,cateLeafName,"
					+ "payMoney,payMoneyPercentage,payNum,buyerNum,payMordOrderNum,paySubOrderNum,paySubOrderAvg,payMoneyPerBuyer,payMoneyPerOrder)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			Iterator<String> it = mapList.keySet().iterator();
			int count = 0;
			while(it.hasNext()){
				String brand = it.next();
				List<Map<String, String>> list = mapList.get(brand);
				for(Map<String,String> map : list){
					pst.setString(1, map.get("area"));
					pst.setString(2, map.get("month"));
					pst.setString(3, brand);
					pst.setString(4, map.get("category"));
					pst.setString(5, map.get("cateLeafName"));
					pst.setString(6, map.get("payMoney"));
					pst.setString(7, map.get("payMoneyPercentage"));
					pst.setString(8, map.get("payNum"));
					pst.setString(9, map.get("buyerNum"));
					pst.setString(10, map.get("payMordOrderNum"));
					pst.setString(11, map.get("paySubOrderNum"));
					pst.setString(12, map.get("paySubOrderAvg"));
					pst.setString(13, map.get("payMoneyPerBuyer"));
					pst.setString(14, map.get("payMoneyPerOrder"));
					pst.addBatch();
					if(++count%1000 == 0){
						pst.executeBatch();
					}
				}
			}
			pst.executeBatch();
			System.out.println("插入数据成功~");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String,Map<String,String>> brandCateGoryMap(boolean optionAll){
		Map<String,Map<String,String>> maps = new HashMap<String, Map<String,String>>();
		Map<String,String> map =  getBrandMap(optionAll);
		for(String brandId : map.keySet()){
			String brandName = map.get(brandId);
			Map<String,String> brandCateMap = getCategoryMap(brandId,optionAll);
			brandCateMap.put(brandId, brandName);
			maps.put(brandId, brandCateMap);
		}
		return maps;
	}
	
	/**
	 * 获取当前用户下所有的品牌
	 * @param optionAll 
	 * */
	public static Map<String,String> getBrandMap(boolean optionAll){
		
		Map<String,String> map = new HashMap<String,String>();
		String referer = "";
		if(optionAll){
			referer = "http://web.cbbs.tmall.com/pages/chaoshi/rtoverview?spm=a224m.7959549.0.0.ilsYrZ";
		}else{
			referer = "http://web.cbbs.tmall.com/pages/chaoshi/tradecomposition?spm=a224m.7959549.0.0.GB6O4m";
		}
		String url = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?serviceId=sm_option_brand";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			System.out.println(url);
			get.setHeader("Cookie",cookie);
			get.setHeader("Host","dataweb.cbbs.tmall.com");
			get.setHeader("Origin","http://web.cbbs.tmall.com");
			get.setHeader("Referer",referer);
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			String entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);
			JSONObject json = JSONObject.fromObject(entity);
			JSONArray data = json.getJSONArray("data");
			for(Object obj : data){
				JSONObject itemJson = JSONObject.fromObject(obj);
				map.put(itemJson.getString("value"), itemJson.getString("label"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		brandMap = map;
		return map;
	}
	
	/**
	 * 获取当前用户下所有的品类
	 * */
	private static Map<String,String> getCategoryMap(String brandId, boolean optionAll){
		
		Map<String,String> categoryMap = new HashMap<String,String>();
		String referer = "";
		String url = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?serviceId=sm_option_category_3";
		if(optionAll){
			url += "&brandId=" + brandId;
			referer = "http://web.cbbs.tmall.com/pages/chaoshi/rtoverview?spm=a224m.7959549.0.0.ilsYrZ";
		}else{
			url += "&optionAll=false&brandId=" + brandId;
			referer = "http://web.cbbs.tmall.com/pages/chaoshi/tradecomposition?spm=a224m.7959549.0.0.GB6O4m";
		}
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Cookie",cookie);
			get.setHeader("Host","dataweb.cbbs.tmall.com");
			get.setHeader("Origin","http://web.cbbs.tmall.com");
			get.setHeader("Referer",referer);
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			String entity = EntityUtils.toString(response.getEntity());
			JSONObject json = JSONObject.fromObject(entity);
			JSONArray data = json.getJSONArray("data");
			for(Object obj : data){
				JSONObject itemJson = JSONObject.fromObject(obj);
				categoryMap.put(itemJson.getString("value"), itemJson.getString("label"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryMap;
	}
}
