package JD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.JDBCConnection;
import Utils.HtmlGenUtils;
import Utils.OSSUtils;



/**
 * 点位统计
 * */
public class ClickStatistic {
	
	public static void main(String[] args){
		new ClickStatistic().crawlingPit();
		/*List<Map<String, String>> brandList = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("brand", "玉兰油");
		map.put("pcUrl", "http://sale.jd.com/act/afnxA4hXRlCYjVog.html");
		brandList.add(map);
		new ClickStatistic().getImgMap(brandList);*/
	}
	
	
	/**
	 * 点位统计爬取
	 * 
	 * */
	public void crawlingPit(){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		List<Map<String,String>> brandList = getBrandUrls();
		Map<String,String> imgMap = getImgMap(brandList);//根据品牌链接下载图片上传oss
		
		Map<String,Map<String,String>> urlMap = getImgPageUrls();
		Iterator<String> it = urlMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			Map<String,String> keyMap = urlMap.get(key);
			for(String pageKey : keyMap.keySet()){
				String url = keyMap.get(pageKey);
				if(key.equals("PC")){
					crawlingPc(pageKey,url,imgMap,mapList);
				}else{
					crawlingApp(pageKey,url,imgMap,mapList);
				}
			}
		}
		
		if(mapList.size() > 0){
			saveData(mapList);
		}
	}
	
	/**
	 * 保存到数据库
	 * */
	private void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Connection dbConn = JDBCConnection.connectToServer("braun_jd_campaign");
			String sql = "insert into PitStatistic (brand,brandPort, pitDetail, imgUrl,insertTime) values (?,?,?,?,?)";
			PreparedStatement ps = dbConn.prepareStatement(sql);
			int count = 0;
			int size = 1000;
			for(Map<String,String> map : mapList){
				ps.setString(1, map.get("brand"));
				String brandPort = "";
				String pitDetail = "";
				if(map.get("brandPort") != null){
					brandPort = map.get("brandPort");
				}
				if(map.get("pitDetail") != null){
					pitDetail = map.get("pitDetail");
				}
				ps.setString(2, brandPort);
				ps.setString(3, pitDetail);
				ps.setString(4, map.get("imgUrl"));
				String date = format.format(new Date());
				ps.setString(5, date);
				ps.addBatch();
				if(++count%size == 0){
					ps.executeBatch();
				}
			}
			ps.executeBatch();
			System.out.println("插入数据成功");
			ps.close();
			dbConn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * app页面
	 * @param mapList 
	 * */
	private void crawlingApp(String pageKey, String url,
			Map<String, String> imgMap, List<Map<String, String>> mapList) {
		if(pageKey.equals("firstUrl")){
			 getAppFirstPage(url,imgMap,mapList);
		}else if(pageKey.equals("secondUrl")){
			getAppSencondPage(url,imgMap,mapList);
		}else if(pageKey.equals("beautyUrl")){
			getAppBeautyPage(url,imgMap,mapList);
		}
	}
	
	/**
	 * app美妆馆焦点图
	 * @param imgMap 
	 * @param mapList 
	 * */
	private void getAppBeautyPage(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority","pro.m.jd.com");
			get.setHeader(":method","GET");
			get.setHeader(":scheme","https");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements liElements = doc.getElementsByAttributeValue("class", "promo_slide_one_wrap").select("li");
			for(Element element : liElements){
				String href = element.select("a").attr("href");
				if(href.indexOf("http") == -1){
					href = "https:" + href;
				}
				htmlCode = HtmlGenUtils.getCode(href, "utf-8");
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "APP");
						brandMap.put("pitDetail", "app美妆馆焦点图");
					}
					mapList.add(brandMap);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * app爱美丽焦点图
	 * @param mapList 
	 * @param imgMap 
	 * */
	private void getAppSencondPage(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority","pro.m.jd.com");
			get.setHeader(":method","GET");
			get.setHeader(":scheme","https");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements liElements = doc.getElementsByAttributeValue("class", "img_wrap jdui_slide_list").select("li");
			for(Element element : liElements){
				String href = element.select("a").attr("href");
				if(href.indexOf("http") == -1){
					href = "https:" + href;
				}
				htmlCode = HtmlGenUtils.getCode(href, "utf-8");
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "APP");
						brandMap.put("pitDetail", "app爱美丽焦点图");
					}
					mapList.add(brandMap);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * app首页焦点图
	 * @param mapList 
	 * @param imgMap 
	 * */
	private void getAppFirstPage(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority","m.jd.com");
			get.setHeader(":method","GET");
			get.setHeader(":scheme","https");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements liElements = doc.getElementById("slider").select("li");
			for(Element element : liElements){
				String href = element.select("a").attr("href");
				htmlCode = HtmlGenUtils.getCode(href, "utf-8");
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "APP");
						brandMap.put("pitDetail", "app首页焦点图");
					}
					mapList.add(brandMap);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * pc页面
	 * @param mapList 
	 * */
	private void crawlingPc(String pageKey, String url,
			Map<String, String> imgMap, List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		if(pageKey.equals("firstUrl")){
			getFirstUrlPage(url);//
		}else if(pageKey.equals("secondUrl")){
			 getSecondUrl(url,imgMap,mapList);
		}else if(pageKey.equals("beautyUrl")){
			getBeautyUrl(url,imgMap,mapList);
		}else if(pageKey.equals("channelUrl")){
			getChannelUrl(url,imgMap,mapList);
		}
	}
	
	
	/**
	 * 个护健康焦点图
	 * @param mapList 
	 * @param imgMap 
	 * */
	private String getChannelUrl(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","channel.jd.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Element element = doc.getElementById("slide");
			String code = element.toString();
			int offset = 0;
			int len = 0;
			String word = "href:";
			String endWord = ".html";
			while((offset = code.indexOf(word,offset)) != -1){
				offset += word.length();
				String href = code.substring(offset, code.indexOf(endWord,len) + 5);
				len =  offset + href.length();
				href = href.substring(href.indexOf("url=") + 4,href.length());
				htmlCode = HtmlGenUtils.getCode(href, "sale.jd.com");
				
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "PC");
						brandMap.put("pitDetail", "PC个护健康焦点图");
					}
					mapList.add(brandMap);
				}
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}


	/**
	 * 美妆馆焦点图
	 * @param mapList 
	 * @param imgMap 
	 * */
	private String getBeautyUrl(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","beauty.jd.com");
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			String html = EntityUtils.toString(response.getEntity());
			html = html.substring(html.indexOf("mAct1") + 7, html.indexOf("couponImg") - 1).trim();
			html = html.substring(0, html.length() - 1);
			JSONArray array = JSONArray.fromObject(html);
			for(Object obj : array){
				JSONObject json = JSONObject.fromObject(obj);
				String jsonUrl = json.getString("url");
				htmlCode = HtmlGenUtils.getCode(jsonUrl, "sale.jd.com");
				System.out.println(htmlCode);
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "PC");
						brandMap.put("pitDetail", "PC美妆馆焦点图");
					}
					mapList.add(brandMap);
				}
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}


	/**
	 * 个护二级焦点图
	 * @param mapList 
	 * @param imgMap 
	 * */
	private String getSecondUrl(String url, Map<String, String> imgMap, List<Map<String, String>> mapList) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority","channel.jd.com");
			get.setHeader(":method","GET");
			get.setHeader(":path","/beauty.html");
			get.setHeader(":scheme","https");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements elements = doc.getElementsByAttributeValue("class", "bea-firstScreen").get(0).getElementsByAttributeValue("class", "bea-slider");
			for(Element element : elements){
				String href = element.select("a").attr("href");
				href = href.substring(href.indexOf("url=") + 4,href.length());
				if(href.indexOf("http") == -1){
					href = "https:" + href;
				}
				htmlCode = HtmlGenUtils.getCode(href, "sale.jd.com");
				for(String str : imgMap.keySet()){
					Map<String,String> brandMap = new HashMap<String,String>();
					brandMap.put("brand", str);
					brandMap.put("imgUrl", imgMap.get(str));
					if(htmlCode.indexOf(str) != -1){
						brandMap.put("brandPort", "PC");
						brandMap.put("pitDetail", "PC个护二级焦点图");
					}
					mapList.add(brandMap);
				}
				Thread.sleep(200);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return htmlCode;
	}


	/**
	 * 首页焦点图页面
	 * */
	private String getFirstUrlPage(String url) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority","www.jd.com");
			get.setHeader(":method","GET");
			get.setHeader(":scheme","https");
			CloseableHttpResponse response = client.execute(get);
			
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements liElements = doc.getElementsByAttributeValue("class", "J_slider_main slider_main").select("li");
			System.out.println(doc);
			for(Element element : liElements){
				String href = element.select("a").attr("href");
				System.out.println(href);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}


	/**
	 * 下载每个品牌的图片
	 * */
	private Map<String, String> getImgMap(List<Map<String, String>> brandList) {
		Map<String,String> brandImgMap = new HashMap<String,String>();
		for(Map<String,String> map : brandList){
			try {
				String htmlCode = "";
				String brand = map.get("brand");
				String pcUrl = map.get("pcUrl").trim();
				for(int i = 0;i<3;i++){
					try {
						CloseableHttpClient client = HttpClients.createDefault();
						HttpGet get = new HttpGet(pcUrl);
						if(pcUrl.indexOf("sale.jd") != -1){
							get.setHeader("Host","sale.jd.com");
						}else if(pcUrl.indexOf("mall.jd") != -1){
							get.setHeader(":authority","mall.jd.com");
							get.setHeader(":method","GET");
							get.setHeader(":scheme","https");
						}
						CloseableHttpResponse response = client.execute(get);
						htmlCode = EntityUtils.toString(response.getEntity());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(!htmlCode.equals("")){
						break;
					}
				}
				String imgUrl = downloadImg(brand,htmlCode,pcUrl);
				brandImgMap.put(brand, imgUrl);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return brandImgMap;
	}


	/**
	 * 匹配图片上传到oss
	 * @param pcUrl 
	 * */
	private String downloadImg(String brand,String htmlCode, String pcUrl) {
		String imgUrl = "";
		if(htmlCode != null){
			try {
				Document doc = Jsoup.parse(htmlCode);
				Elements elements = doc.getElementsByAttributeValue("class", "userDefinedArea");
				Elements imgElements = doc.getElementsByAttributeValue("class", "jImgNodeArea");
				String src = "";
				if(pcUrl.indexOf("sale.jd") != -1){
					for(Element element : elements){
						if(element.select("img") != null && element.select("img").size() > 0){
							src = element.select("img").get(0).attr("original");
						}
					}
				}else if(pcUrl.indexOf("mall.jd") != -1){
					src = imgElements.get(0).select("img").get(0).attr("src");
				}
				if(!src.equals("")){
					if(src.indexOf("http") == -1){
						src = "http:" + src; 
					}
					imgUrl = OSSUtils.uploadImg(src,brand,"clickStatistic");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imgUrl;
	}



	/**
	 * 露出页面
	 * */
	private Map<String,Map<String,String>> getImgPageUrls(){
		Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
		String[] keys = {"PC","APP"};
		for(String str : keys){
			Map<String,String> keyMap = map.get(str);
			if(keyMap == null){
				keyMap = new HashMap<String,String>();
			}
			if(str.equals("PC")){
				keyMap.put("firstUrl", "https://www.jd.com/");
				keyMap.put("secondUrl", "https://channel.jd.com/beauty.html");
				keyMap.put("beautyUrl", "https://beauty.jd.com/");
				keyMap.put("channelUrl", "https://channel.jd.com/737-1276.html");
			}else{
				keyMap.put("firstUrl", "https://m.jd.com");
				keyMap.put("secondUrl", "https://pro.m.jd.com/mall/active/2QYwVrMZEDdfuAjGRVDHD4fLs69A/index.html?utm_source=pdappwakeupup_20170001");
				keyMap.put("beautyUrl", "https://pro.m.jd.com/mall/active/2aEg24zZRbXDdboPE1XYHBgDKDjw/index.html?utm_source=pdappwakeupup_20170001");
			}
			map.put(str, keyMap);
		}
		return map;
	}
	/**
	 * 获取品牌列表的url
	 * */
	private List<Map<String,String>> getBrandUrls(){
		List<Map<String,String>> brandList = new ArrayList<Map<String,String>>();
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("data.txt");
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			while((line = br.readLine()) != null){
				try {
					String[] data = line.split("\t");
					Map<String,String> map = new HashMap<String,String>();
					map.put("brand", data[1]);
					map.put("pcUrl", data[2]);
					map.put("appUrl", data[3]);
					brandList.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br != null){
					br.close();
				}
				if(is != null){
					
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		return brandList;
	}
}
