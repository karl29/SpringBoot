package JD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;
import Utils.OSSUtils;


import database.JDBCConnection;


/**
 * 单品秒杀
 * */
public class SecKillTempo {
	private static int INDEX = 1;
	public static void main(String[] args) throws Exception{
		crawlingJd();
	}
	
	public static void crawlingJd() throws Exception{
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		int page = 1;
		boolean hasPage = true;
		while(hasPage){
			try {
				String url = "https://coupon.m.jd.com/seckill/seckillListPage.json?isPagination=true&gid=30&page=" + page;
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader(":authority","coupon.m.jd.com");
				get.setHeader(":method","GET");
				get.setHeader(":path","/seckill/seckillList.json?isPagination=true&gid=30&page=" + page);
				get.setHeader(":scheme","https");
				get.setHeader("Referer","https://coupon.m.jd.com/seckill/seckillList?utm_source=iosapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=Wxfriends");
				get.setHeader("User-Agent",HtmlGenUtils.getRandomMobileUserAgent());
				CloseableHttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				hasPage = parseHtmlCode(entity,mapList,page);
				System.out.println(page + "====");
				page++;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		INDEX = 1;//爬完初始化值
		saveData(mapList);
	}
	
	
	/**
	 * 解析获取到的数据
	 * @param mapList 
	 * @param page 
	 * @param id 
	 * @return 
	 * */
	private static boolean parseHtmlCode(HttpEntity entity, List<Map<String, String>> mapList, int page) throws Exception{
		// TODO Auto-generated method stub
		boolean hasPage = true;
		if(entity != null){
			String htmlCode = EntityUtils.toString(entity);
			if(!"".equals(htmlCode)){
				JSONObject infoJson = JSONObject.fromObject(htmlCode).getJSONObject("seckillInfo");
				if(infoJson.getInt("totalPage") == page){
					hasPage = false;
				}
				JSONArray itemList = infoJson.getJSONArray("itemList");
				for(Object obj : itemList){
					try {
						JSONObject itemJson = JSONObject.fromObject(obj);
						String name = itemJson.getString("wname");
						Map<String,String> map = new HashMap<String, String>();
						String img = itemJson.getString("imageurl");
						if(img.indexOf("http") == -1){
							img = "http:" + img; 
						}
						String newPrice = itemJson.getString("miaoShaPrice");
						String oldPrice = itemJson.getString("jdPrice");
						map.put("name", name);
						map.put("newPrice", newPrice);
						map.put("oldPrice", oldPrice);
						map.put("imgUrl",img);
						map.put("index", INDEX + "");
						mapList.add(map);
						System.out.println("坑位:" + INDEX + "\r\n图片:" + img + "\r\n名称：" + name + "\r\n新价格：" + newPrice + "\r\n旧价格:" + oldPrice);
					} catch (Exception e) {
						e.printStackTrace();
					}
					INDEX ++;
				}
			}
		}
		return hasPage;
	}
	
	


	/**
	 * 保存到数据库
	 * */
	private static void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		
		if(mapList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("braun_jd_campaign");
				String sql = "insert into SecKill_copy (name,img,miaoshaPrice,jdPrice,proIndex,insertTime) "
						+ "values (?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, map.get("name"));
					ps.setString(2, map.get("imgUrl"));
					ps.setDouble(3, Double.valueOf(map.get("newPrice")));
					ps.setDouble(4, Double.valueOf(map.get("oldPrice")));
					ps.setInt(5, Integer.valueOf(map.get("index")));
					ps.setString(6, HtmlGenUtils.getDataTime("yyyy-MM-dd", 0));
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
