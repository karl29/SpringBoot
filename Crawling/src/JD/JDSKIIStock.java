package JD;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import Tmall.TmallSearchResult;
import Utils.WebDriverUtils;

/**
 *@author Karl.Qin
 *@version ����ʱ�䣺2017��10��19�� ����1:42:09
 *���SKII��sku���״��
 */
public class JDSKIIStock {
	private static final String URL = "https://sale.jd.com/act/SvI7VlgKcfLxCBp.html";
	public static void main(String[] args){
		//crawl();
		//checkStock("3252716");
		crawlSearchResult(null);
	}
	public static void crawl(List<Map<String, String>> mapList){
		for(Map<String,String> map : mapList){
			String skuId = map.get("skuId");
			String stockStatus = checkStock(skuId);
			map.put("status", stockStatus);
		}
	}
	/**
	 * ͨ��skuid��ȡ���Ϲ������������״��
	 * */
	private static String checkStock(String skuId) {
		String stockStatus = "";
		List<String> areaList = Arrays.asList(new String[]{"1_72_2839_0","2_78_51978_0","19_1601_3633_0"});
		for(String area : areaList){
			String url = "https://c0.3.cn/stock?skuId="+skuId+"&area=" + area
					+ "&venderId=1000009821&cat=1316,1381,1391&buyNum=1&choseSuitSkuIds="
					+ "&extraParam={%22originid%22:%221%22}&ch=1&fqsp=0&detailedAdd=null&callback=jQuery842238";
			String htmlCode = getStockHtmlCode(url);
			if(htmlCode.indexOf("�޻�") != -1 && htmlCode.indexOf("����") != -1){
				String status = "";
				if(area.equals("1_72_2839_0")){
					status = "�����޻�";
				}else if(area.equals("2_78_51978_0")){
					status = "�Ϻ��޻�";
				}else{
					status = "�㶫�޻�";
				}
				if(stockStatus.equals("")){
					stockStatus += status;
				}else{
					stockStatus += "," + status;
				}
			}
		}
		System.out.println(stockStatus + "~~~~~~~~~~~~~~");
		return stockStatus;
	}
	
	/**
	 * ��ȡurljson
	 * @param refererUrl 
	 * */
	public static String getStockHtmlCode(String toUrl) {
		String htmlCode = "";
		//url�а���{},����תuri
        try {
        	java.net.URL netUrl = new java.net.URL(toUrl);
            HttpURLConnection conn = (HttpURLConnection) netUrl.openConnection();
            conn.setRequestProperty("Content-Type", "GBK");
            conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setUseCaches(false);
	        conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
           String line;
           while((line = br.readLine()) != null){
        	   //System.out.println(new String(line.getBytes("GBK"),"UTF-8"));
        	   htmlCode += line;
           }
           System.out.println(htmlCode);
           Thread.sleep(new Random().nextInt(5) * 1000);
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return htmlCode;
	}
	/**
	 * ��ȡҳ������sku
	 * */
	public static List<Map<String,String>> getItemList(){
		WebDriver driver = new WebDriverUtils().getDriver("chrome");
		driver.get(URL);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//�϶�������
		JDComments.controScroll(driver);
		List<WebElement> elList = driver.findElement(By.cssSelector(".j-module")).findElements(By.cssSelector(".jItem"));
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		for(WebElement el : elList){
			map = new HashMap<String, String>();
			String href = el.findElement(By.cssSelector(".jPic")).findElement(By.tagName("a")).getAttribute("href");
			map.put("href", href);
			String skuId = href.substring(href.lastIndexOf("/") + 1, href.indexOf(".html"));
			map.put("skuId", skuId);
			System.out.println(href + "\n" + skuId);
			String itemName = el.findElement(By.cssSelector(".jDesc")).getAttribute("title");
			map.put("itemName", itemName);
			System.out.println(itemName);
			mapList.add(map);
		}
		driver.quit();
		
		return mapList;
	}
	
	/**
	 * ��ȡ�������ҳ
	 * */
	public static void crawlSearchResult(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		WebDriver driver = new WebDriverUtils().getDriver("chrome");
		driver.get("https://search.jd.com/Search?keyword=sk%20ii&enc=utf-8&wq=sk%20ii&pvid=94cde554687044acb54187617e551d45");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		getSearchList(driver,mapList);
		
		driver.quit();
		
	}
	/**
	 * ��ȡ�������ҳ�������б�����
	 * */
	private static void getSearchList(WebDriver driver,
			List<Map<String, String>> mapList) {
		// ˿ܽ���ٷ��콢��  SK-II�ٷ��콢��
		try {
			JDComments.controScroll(driver);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			List<WebElement> list = driver.findElement(By.id("J_goodsList")).findElements(By.tagName("li"));
			Map<String, String> map = null;
			for(WebElement element : list){
				map = new HashMap<String, String>();
				String itemId = element.getAttribute("data-sku");
				map.put("skuId", itemId);
				String itemName = element.findElement(By.cssSelector(".p-name.p-name-type-2")).findElement(By.tagName("em")).getText();
				map.put("itemName", itemName);
				String price = element.findElement(By.cssSelector(".p-price")).findElement(By.tagName("i")).getText();
				map.put("price", price);
				String shopName = element.findElement(By.cssSelector(".J_im_icon")).findElement(By.tagName("a")).getText();
				map.put("shopName", shopName);
				System.out.println(itemId + "\n" + itemName + "\n" + price + "\n" + shopName + "\n==========");
			}
			boolean nextPage = TmallSearchResult.hasElement(driver,By.cssSelector(".pn-next.disabled"));
			if(nextPage){
				System.out.println("û����һҳ��~~~~~~~");
			}else{
				System.out.println("�����һҳ~~~~~~~~");
				driver.findElement(By.cssSelector(".pn-next")).click();
				getSearchList(driver,mapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
