package VIP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;

public class VipCrawl {
	public static String COOKIE = "vip_cps_cid=1505881386603_d5b4ffe88a6357c6e07e6f6c883ed9de; PAPVisitorId=c88b6b395875c1fea725b9897661d15b; vip_new_old_user=1; _smt_uid=59c1ed1f.6a27248; _jzqco=%7C%7C%7C%7C%7C1.1784829161.1505881375475.1505881434988.1505881462602.1505881434988.1505881462602..0.0.7.7; vip_wh=VIP_NH; vip_province=104104; visadminvipvipcom=urqhrtudl0nc29id27lifbbvk4; _gat=1; PHPSESSID=urqhrtudl0nc29id27lifbbvk4; token=eyJ0b2tlbiI6IjQ4MzNhNDI1YzU1ZWE1MGIzZjliOWYzMGY0MTlmMTM1IiwidG9rZW4xIjoiYjgyZjVlMDk3NTU5YmQ3YTAzYjZkMjI1YzEyZTU4YjUiLCJ2ZW5kb3JJZCI6IjIyMDUiLCJ1c2VyTmFtZSI6ImxpdS56ZUBwZy5jb20iLCJ2ZW5kb3JDb2RlIjoiMTA1MjA5IiwidXNlcklkIjoiNDU0MTIiLCJ2aXNTZXNzaW9uSWQiOiJ1cnFocnR1ZGwwbmMyOWlkMjdsaWZiYnZrNCIsImFwcE5hbWUiOiJ2aXNQQyJ9; vc_token=eyJ0b2tlbiI6IjQ4MzNhNDI1YzU1ZWE1MGIzZjliOWYzMGY0MTlmMTM1IiwidG9rZW4xIjoiYjgyZjVlMDk3NTU5YmQ3YTAzYjZkMjI1YzEyZTU4YjUiLCJ2ZW5kb3JJZCI6IjIyMDUiLCJ1c2VyTmFtZSI6ImxpdS56ZUBwZy5jb20iLCJ2ZW5kb3JDb2RlIjoiMTA1MjA5IiwidXNlcklkIjoiNDU0MTIiLCJ2aXNTZXNzaW9uSWQiOiJ1cnFocnR1ZGwwbmMyOWlkMjdsaWZiYnZrNCIsImFwcE5hbWUiOiJ2aXNQQyJ9; _ga=GA1.2.1599616744.1504665015; _gid=GA1.2.1750115407.1508757241; user=liu.ze%40pg.com; jobnumber=0; nickname=Chris; shop_id=2205; vendor_code=105209; vendor_id=125499; permission=_129_130_196_197_200_201_203_206_207_208_209_210_211_212_213_214_229_242_243_244_245_259_260_261_389_394_428_429_; user_id=45412; user_type=1; axdata=ZGU3ZDBhMzcxM2E5NzZjYzk0OWExMTYzOTYxYmIxMjdiODUxMzMxNjRiMGE0MTczM2UyYTJlYTMyZTFmNGFjMg%3D%3D; shops=2205; codes=105209; expire=1508937470; mars_pid=0; mars_cid=1504665145922_f129ad9c03529c054411d879ed6547f1; mars_sid=78705638f28d365bd0528a92475cd875; visit_id=1F0EF6453C2D942AAD1BFCDFBE04033B";
	private static final String USER_NAME = "liu.ze@pg.com";
	private static final String PASSWORD = "Liu.ze@123456";
	public static void main(String[] args) {
		//getBrandList();
	}
	
	
	/**唯品会后台登录*/
	public static void login(){
		//userName=liu.ze%40pg.com&passWord=Liu.ze123456&checkWord=5YB6
		try {
			//String url = "https://vis.vip.com/login.php";
			String loginUrl = "https://vis.vip.com/login.php?v=" + System.currentTimeMillis();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(loginUrl);
			HttpEntity bodyEntity = MultipartEntityBuilder.create()
					.addPart("userName", new StringBody(USER_NAME, ContentType.TEXT_PLAIN))
					.addPart("passWord", new StringBody(PASSWORD, ContentType.TEXT_PLAIN))
					.addPart("checkWord", new StringBody("", ContentType.TEXT_PLAIN))
					.build();
			post.setEntity(bodyEntity);
			post.setHeader("Host","vis.vip.com");
			post.setHeader("Referer","https://vis.vip.com/login.php");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			client.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**下载excel*/
	public static File downLoadExcel(Map<String, String> headerMap){
		File file = null;
		InputStream ins = null;
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(headerMap.get("url"));
			get.setHeader("cookie",COOKIE);
			get.setHeader("Host","compass.vis.vip.com");
			get.setHeader("Referer",headerMap.get("refererUrl"));
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			ins = response.getEntity().getContent();
			file = new File("D://brandData.xlsx");
			FileOutputStream fos = new FileOutputStream(file);
			int len = 0;
			byte[] bs = new byte[1024];
			while((len = ins.read(bs)) != -1){
				fos.write(bs, 0, len);
			}
			ins.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	/**获取对应的页面数据*/
	public static String getHtmlCode(Map<String, String> headerMap) {
		String htmlCode = "";
		try {
			for(int i = 0;i<3;i++){
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(headerMap.get("url"));
				get.setHeader("cookie",COOKIE);
				get.setHeader("Host","compass.vis.vip.com");
				get.setHeader("Referer",headerMap.get("refererUrl"));
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				htmlCode = EntityUtils.toString(client.execute(get).getEntity(),"utf-8");
				if(htmlCode.indexOf("<html>") != -1){
					Thread.sleep(new Random().nextInt(3) * 1000);
					continue;
				}else{
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}
	
	
	/**比较当前日期是否在档期的最后日期内*/
	public static int checkIsOT(String lastSellDay) {
		int day = 0;
		String currenDate = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
		try {
			day = HtmlGenUtils.daysBetween("yyyy-MM-dd", currenDate, lastSellDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}
}
