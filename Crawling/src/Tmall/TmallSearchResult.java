package Tmall;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import database.JDBCConnection;
import Utils.HtmlGenUtils;
import Utils.TmallCheckCode;
import Utils.WebDriverUtils;

public class TmallSearchResult {
	private static int page = 1;
	private static String currenPage = "";
	public static void main(String[] args){
		crawlSearchList();
		
	}
	
	public static void crawlSearchList(){
		System.out.println("竞品爬取开始啦~~~~~~~~");
		List<String> areaList = Arrays.asList(new String[]{"上海"});
		List<Map<String,String>> categoryList = readExcel();
		for(Map<String,String> categoryMap : categoryList){
			String category = categoryMap.get("category");
			String detailUrl = categoryMap.get("url");
			WebDriver webDriver = new WebDriverUtils().getDriver("firefox");
			webDriver.manage().window().maximize();
			Map<String,List<Map<String,String>>> map = new HashMap<String, List<Map<String,String>>>();
			for(String areaName : areaList){
				try {
					int pageIndex = 0;
					List<Map<String,String>> mapList = map.get(areaName);
					if(mapList == null){ 
						mapList = new ArrayList<Map<String,String>>();
					}
					
					//加载超市列表页面
					loadChaoshiPage(webDriver);
					
					//如果有弹窗，则关闭
					closeLoginFrame(webDriver);
					//换地区
					changeArea(webDriver,areaName);
					//检查是否出现验证码或者需要登陆
					//webDriver = checkProxyIp(webDriver,areaName);
					
					currenPage = detailUrl;
					webDriver.get(detailUrl);
					
					getData(webDriver, mapList,pageIndex,areaName);
					map.put(areaName, mapList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				page = 1;
			}
			webDriver.quit();
			
			saveData(map,category);
		}
	}
	
	/**加载猫超页面*/
	public static void loadChaoshiPage(WebDriver webDriver) {
		// TODO Auto-generated method stub
		webDriver.get("https://chaoshi.tmall.com/?spm=a3204.7084717.a2226n0.1.pFsO3M&notjump=true&_ig=logo");
		webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	/**检查是否出现验证码或者是否需要登陆，如果出现，换代理ip
	 * @param areaName */
	private static WebDriver checkProxyIp(WebDriver webDriver, String areaName) {
		WebDriver driver = null;
		if(webDriver.getCurrentUrl().indexOf("login.taobao") != -1 || webDriver.getCurrentUrl().indexOf("login.tmall") != -1
				|| webDriver.getCurrentUrl().indexOf("sec.taobao") != -1){
			webDriver.quit();//退出当前
			driver = new WebDriverUtils().getDriver("firefox");
			
			loadChaoshiPage(driver);
			//换地区
			changeArea(webDriver,areaName);
			webDriver.get(currenPage);
		}else{
			driver = webDriver;
		}
		return driver;
	}
	
	/**检查是否出现验证码*/
	private static void checkCode(WebDriver webDriver) {
		// TODO Auto-generated method stub
		try {
			String imgUrl = webDriver.findElement(By.id("checkcodeImg")).getAttribute("src");
			System.out.println(imgUrl);
			String code = TmallCheckCode.getImgeCode(imgUrl);
			System.out.println(code);
			webDriver.findElement(By.id("checkcodeInput")).clear();
			webDriver.findElement(By.id("checkcodeInput")).sendKeys(code);
			Thread.sleep(1000);
			webDriver.findElement(By.className("submit")).findElement(By.tagName("input")).click();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**换地区*/
	public static void changeArea(WebDriver webDriver, String areaName) {
		// TODO Auto-generated method stub
		if(hasElement(webDriver,By.id("J_AreaSelector"))){
			WebElement curArea = webDriver.findElement(By.id("J_AreaSelector")).findElement(By.tagName("h3"));
			if(curArea.getText().indexOf(areaName) == -1){//换地区
				//换地区
				new Actions(webDriver).moveToElement(webDriver.findElement(By.id("J_AreaSelector"))).perform();
				if(hasElement(webDriver,By.className("hot-cities"))){
					for(int i = 0;i<=12;i++){
						//不能提前获取到span的元素去循环，否则会报错
						WebElement hot = webDriver.findElement(By.className("hot-cities")).findElements(By.tagName("span")).get(i);
						System.out.println(hot.getText());
						System.out.println(areaName);
						if(hot.getText().equals(areaName)){
							hot.click();
							if(hasElement(webDriver,By.className("division-item"))){
								webDriver.findElement(By.className("division-item")).click();
							}
							break;
						}
					}
					//换完地区还会弹窗,再关闭
					closeLoginFrame(webDriver);
				}
			}else{
				System.out.println(curArea.getText() + "地区对啦~~~~~" + areaName);
			}
		}else{
			System.out.println("地区元素按钮未找到~~" + areaName);
		}
	}

	/**保存数据*/
	private static void saveData(Map<String, List<Map<String, String>>> mapList,String category) {
		// TODO Auto-generated method stub
		//三个地区sku去重复
		Map<String,Map<String,String>> itemList = distinctItem(mapList);
		
		try {
			Connection conn = JDBCConnection.connectToLocal("data");
			String sql = "insert into vinda_maochaoData(category,itemId,itemName,price,totalSell,dataTime)"
					+ " values (?,?,?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			Iterator<String> it = itemList.keySet().iterator();
			int count = 0;
			int size = 1000;
			while(it.hasNext()){
				String itemId = it.next();
				Map<String, String> map = itemList.get(itemId);
				pst.setString(1, category);
				pst.setString(2, itemId);
				pst.setString(3, map.get("itemName"));
				pst.setString(4, map.get("price"));
				pst.setString(5, map.get("itemSum"));
				pst.setString(6, HtmlGenUtils.getDataTime("yyyy-MM-dd", 0));
				pst.addBatch();
				if(++count%size == 0){
					pst.executeBatch();
				}
			}
			pst.executeBatch();
			System.out.println("插入sku数据成功");
			pst.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**三个地区的sku去重复，以上海地区的为主*/
	public static Map<String,Map<String,String>> distinctItem(
			Map<String, List<Map<String, String>>> mapList) {
		Map<String,Map<String,String>> itemList = new HashMap<String, Map<String,String>>();
		// TODO Auto-generated method stub
		try {
			List<Map<String,String>> shanghaiList = mapList.get("上海");
			for(Map<String,String> map : shanghaiList){
				String itemId = map.get("itemId");
				//防止执行同一链接重复插入的数据
				if(itemList.get(itemId) == null){
					itemList.put(itemId, map);
				}
			}
			Iterator<String> it = mapList.keySet().iterator();
			while(it.hasNext()){
				String areaName = it.next();
				List<Map<String,String>> areaList = mapList.get(areaName);
				if(!areaName.equals("上海")){
					for(Map<String,String> map : areaList){
						String itemId = map.get("itemId");
						if(itemList.get(itemId) == null){
							itemList.put(itemId, map);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	/**获取页数
	 * @param areaName */
	private static void getData(WebDriver webDriver, List<Map<String, String>> mapList, int pageIndex, String areaName) {
		// TODO Auto-generated method stub
		try {
			webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println(currenPage);
			
			//webDriver = checkProxyIp(webDriver,areaName);
			
			closeLoginFrame(webDriver);
			getItemList(webDriver,mapList);
			
			pageIndex++;
			Thread.sleep((new Random().nextInt(5) + 2) * 1000);
			currenPage = webDriver.getCurrentUrl();
			
			boolean nextPage = hasElement(webDriver,By.className("page-next"));
			if(nextPage){
				System.out.println("点击下一页~~~~~~~~");
				webDriver.findElement(By.className("page-next")).click();
				getData(webDriver,mapList,pageIndex,areaName);
			}else{
				System.out.println("没有下一页啦~~~~~~~");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**退出天猫*/
	private static void logOutTmall(WebDriver webDriver) {
		// TODO Auto-generated method stub
		try {
			if(hasElement(webDriver, By.id("J_Logout"))){
				webDriver.findElement(By.id("J_Logout")).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**商品列表信息*/
	public static void getItemList(WebDriver webDriver,
			List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		List<WebElement> list = webDriver.findElements(By.className("product"));
		for(WebElement we : list){
			Map<String,String> map = new HashMap<String,String>();
			String itemId = we.getAttribute("data-itemid");
			map.put("itemId", itemId);
			System.out.println(itemId);
			
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
			if(hasElement(we.findElement(By.className("item-sum")),By.tagName("strong"))){
				itemSum = we.findElement(By.className("item-sum")).findElement(By.tagName("strong")).getText();
				System.out.println(itemSum);
			}else{
				System.out.println("没有销量~~~~~~");
			}
			map.put("itemSum", itemSum);
			map.put("pageIndex", page + "");
			mapList.add(map);
			page++;
			System.out.println("~~~~~~~~~~~" + page);
		}
	}

	/**关闭登陆弹窗*/
	public static void closeLoginFrame(WebDriver webDriver) {
		// TODO Auto-generated method stub
		if(hasElement(webDriver,By.className("J_MIDDLEWARE_FRAME_WIDGET"))){
			//页面弹出登陆，直接关掉
			webDriver.findElement(By.className("J_MIDDLEWARE_FRAME_WIDGET")).findElement(By.tagName("a")).click();
		}else if(hasElement(webDriver, By.id("sufei-dialog-close"))){
			webDriver.findElement(By.id("sufei-dialog-close")).click();
		}
	}
	/**
	 * 登陆页面
	 * */
	public static void loginTmall(WebDriver webDriver) {
		try {
			webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			String href = webDriver.findElement(By.id("J_loginIframe")).getAttribute("src");
			if(href.indexOf("http") == -1){
				href = "https:" + href;
			}
			webDriver.get(href);
			if(hasElement(webDriver,By.id("J_Quick2Static"))){
				webDriver.findElement(By.id("J_Quick2Static")).click();
				Thread.sleep(300);
				webDriver.findElement(By.id("TPL_username_1")).sendKeys("诗敏敏敏哈哈哈");
				Thread.sleep(300);
				webDriver.findElement(By.id("TPL_password_1")).sendKeys("henhao1993..");
				Thread.sleep(400);
				if(hasElement(webDriver,By.id("nocaptcha"))){
					try {
						//滑块
						boolean hasSlide = webDriver.findElement(By.id("nocaptcha")).getAttribute("style").equals("display: block;");
						System.out.println("滑块~~~~~~" + hasSlide);
						if(hasSlide){
							webDriver.findElement(By.id("nc_1_n1z")).click();
							new Actions(webDriver).dragAndDropBy(webDriver.findElement(By.id("nc_1_n1z")), 125, 0);
							new Actions(webDriver).dragAndDropBy(webDriver.findElement(By.id("nc_1_n1z")), 258, 125).perform();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				webDriver.findElement(By.id("J_SubmitStatic")).click();
			}else{
				loginTmall(webDriver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasElement(WebDriver driver,By by){
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean hasElement(WebElement we,By by){
		try {
			we.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**读取excel获取类目列表*/
	public static List<Map<String,String>> readExcel(){
		XSSFWorkbook book = null;
		InputStream ins = null;
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		try {
			ins = new FileInputStream(new File("E:/category.xlsx"));
			book = new XSSFWorkbook(ins);
			XSSFSheet sheet = book.getSheetAt(0);
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				String url = row.getCell(1).getStringCellValue();
				String category = row.getCell(0).getStringCellValue();
				Map<String,String> map = new HashMap<String, String>();
				map.put("category", category);
				map.put("url", url);
				System.out.println(category + "~~~~~~~" + url);
				mapList.add(map);
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
		map.put("category", "洗发水");
		map.put("url", "https://list.tmall.com/search_product.htm?spm=a3204.7933263.0.0.d811797zDk4dt&cat=52580012&sort=s&style=g&search_condition=23&user_id=725677994&active=1&industryCatId=52536016&smAreaId=440103");
		mapList.add(map);*/
		return mapList;
	}
}
