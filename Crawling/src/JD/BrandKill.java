package JD;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
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
 * 京东品牌秒杀
 * */
public class BrandKill {
	private static Map<String,Integer> brandTotalMap = new HashMap<String,Integer>();//坑位数
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new BrandKill().crawlingJd();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean hasBrandName(String name){
		boolean  hasBrandName = false;
		String[] brandNames = {"欧莱雅","玉兰油","妮维雅 百雀羚","完美","玫琳凯","大宝",
				"相宜本草","自然堂","兰蔻","欧珀莱","雅漾","自然乐园","丝塔芙","资生堂","雅诗兰黛",
				"高夫","曼秀雷敦","韩束","珀莱雅","薇诺娜","温碧泉","理肤泉","碧欧泉","科颜氏","御泥坊",
				"欧诗漫","京润珍珠","兰芝","水密码","可莱丝美","SK-II","膜法世家",
				"博朗","飞利浦"};
		System.out.println(name);
		for(String brandName : brandNames){
			if(name.indexOf(brandName) != -1){
				hasBrandName = true;
				break;
			}
		}
		return hasBrandName;
	} 
	public void crawlingJd() throws Exception{
		String url = "https://ms.m.jd.com/seckill/seckillBrand";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(":authority", "ms.m.jd.com");
		httpGet.setHeader(":method", "GET");
		httpGet.setHeader(":path", "/seckill/seckillBrand");
		httpGet.setHeader(":scheme", "https");
		httpGet.setHeader("user-agent", HtmlGenUtils.getRandomMobileUserAgent());
		CloseableHttpResponse response = client.execute(httpGet);
		
		HttpEntity entity = response.getEntity();
		parseFirstHtml(entity);
	}

	
	/**
	 * 解析页面
	 * @throws Exception 
	 * */
	private static void parseFirstHtml(HttpEntity entity) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		if(entity != null){
			Document doc = Jsoup.parse(EntityUtils.toString(entity));
			Elements elements = doc.getElementsByAttributeValue("class", "skill-floor");
			int skinTotal = getSkinTotalSize(elements);//计算一级页面美肤坑位总数
			for(Element element : elements){
				//https://ms.m.jd.com
				String href = element.select("a").get(0).attr("href");
				Elements iteamElements = element.getElementsByAttributeValue("class", "skill-floor-iteam");
				boolean hasSkillItem = false;
				for(Element iteamEle : iteamElements){
					String name = iteamEle.getElementsByAttributeValue("class", "skill-it-tit").text();
					if((hasSkillItem = hasBrandName(name))){
						break;
					}; 
				}
				if(hasSkillItem){//如果是护肤品类的，则进到二级页面
					HttpEntity secondPageEntity = crawlingSencondPage(href);
					Document secondDoc = Jsoup.parse(EntityUtils.toString(secondPageEntity));
					Elements bomElements = secondDoc.getElementsByAttributeValue("class", "bdr-bom");
					int bomListSize = bomElements.size();
					int index = 1;
					for(Element bomElement : bomElements){
						String name = bomElement.getElementsByAttributeValue("class", "g-title").text();
						String brand = HtmlGenUtils.getBrandName(name);
						if(!"".equals(brand)){
							Integer brandTotal = brandTotalMap.get(brand);
							if(brandTotal == null){
								brandTotal= 0 ;
							}
							brandTotal += 1;
							brandTotalMap.put(brand, brandTotal);
							Map<String,String> map = new HashMap<String, String>();
							String img = bomElement.select("img").attr("_src");
							if(img.indexOf("http") == -1){
								img = "http:" + img; 
							}
							String imgPath = OSSUtils.uploadImg(img,brand,"secKillBrand");
							String newPri = bomElement.getElementsByAttributeValue("class", "g-price").get(0).ownText();
							String oldPri = bomElement.getElementsByAttributeValue("class", "g-price-odd").text().replace("￥", "");
							map.put("brand", brand);
							map.put("name", name);
							map.put("img", imgPath);
							map.put("newPrice", newPri);
							map.put("oldPrice", oldPri);
							map.put("proSelectIdx", bomListSize + "");//选品坑位总数（二级页面总数）
							map.put("skinTotal", skinTotal + "");//一级页面美肤坑位总数
							map.put("index", index + "");
							mapList.add(map);
							System.out.println("坑位" + index + "\r\n图片" + img + "\r\n名称：" + name 
									+ "\r\n新价格：" + newPri + "\r\n旧价格:" + oldPri + "\r\n美肤坑位总数：" + skinTotal
									 + "\r\n选品坑位总数：" + bomListSize);
						}
						index ++;
					}
				}
			}
		}
		
		saveData(mapList);
	}
	
	/**
	 * 计算美肤坑位总数
	 * */
	private static int getSkinTotalSize(Elements elements) {
		int totalSize = 0;
		for(Element element : elements){
			Elements iteamElements = element.getElementsByAttributeValue("class", "skill-floor-iteam");
			boolean hasSkillItem = false;
			for(Element iteamEle : iteamElements){
				String name = iteamEle.getElementsByAttributeValue("class", "skill-it-tit").text();
				if((hasSkillItem = hasBrandName(name))){
					break;
				}
			}
			if(hasSkillItem){
				totalSize++;
			}
		}
		return totalSize;
	}

	/**
	 * 解析二级页面
	 * */
	private static HttpEntity crawlingSencondPage(String href) throws Exception{
		
		String url = "https://ms.m.jd.com" + href;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(":authority", "ms.m.jd.com");
		httpGet.setHeader(":method", "GET");
		httpGet.setHeader(":path", href);
		httpGet.setHeader(":scheme", "https");
		httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
		CloseableHttpResponse response = client.execute(httpGet);
		
		return response.getEntity();
	}

	/**
	 * 保存到数据库
	 * */
	private static void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		
		if(mapList.size() > 0){
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Connection dbConn = JDBCConnection.connectToServer("braun_jd_campaign");
				String sql = "insert into SecKillBrand (brand,name, img, miaoshaPrice,jdPrice,skinTotal,proSelectIdx,proIndex,insertTime,brandTotal) "
						+ "values (?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, map.get("brand"));
					ps.setString(2, map.get("name"));
					ps.setString(3, map.get("img"));
					ps.setDouble(4, Double.valueOf(map.get("newPrice")));
					ps.setDouble(5, Double.valueOf(map.get("oldPrice")));
					ps.setInt(6, Integer.valueOf(map.get("skinTotal")));
					ps.setInt(7, Integer.valueOf(map.get("proSelectIdx")));
					ps.setInt(8, Integer.valueOf(map.get("index")));
					String date = format.format(new Date());
					ps.setString(9, date);
					int brandTotal = 0;
					if(brandTotalMap.get(map.get("brand")) != null){
						brandTotal = brandTotalMap.get(map.get("brand"));
					}
					ps.setInt(10,brandTotal);
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
		
	}
	
}
