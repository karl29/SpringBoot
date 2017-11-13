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
public class SecKill {
	private static Map<String,Integer> brandTotalMap = new HashMap<String,Integer>();//坑位数
	public static void main(String[] args) throws Exception{
		new SecKill().crawlingJd();
	}
	
	public void crawlingJd() throws Exception{
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String[] cateId = {"46","19"}; //46：美肤 ,19:生活电器(主要为剃须刀)
		for(String id : cateId){
			String url = "https://ai.jd.com/index_new?app=Seckill&action=pcSeckillCategoryGoods&callback=pcSeckillCategoryGoods&id="+id+"&_=1494316296846";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","ai.jd.com");
			get.setHeader("Referer","https://miaosha.jd.com/category.html?cate_id=" + id);
			get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			parseHtmlCode(entity,mapList,id);
		}
		
		saveData(mapList);
	}
	
	
	/**
	 * 解析获取到的数据
	 * @param mapList 
	 * @param id 
	 * */
	private static void parseHtmlCode(HttpEntity entity, List<Map<String, String>> mapList, String id) throws Exception{
		// TODO Auto-generated method stub
		if(entity != null){
			String htmlCode = EntityUtils.toString(entity);
			if(!"".equals(htmlCode)){
				htmlCode = htmlCode.substring(htmlCode.indexOf("goodsList") - 2, htmlCode.length() - 2);
				JSONArray goodsList = JSONObject.fromObject(htmlCode).getJSONArray("goodsList");
				int totalSize = goodsList.size();
				int index = 1;
				for(Object obj : goodsList){
					JSONObject itemJson = JSONObject.fromObject(obj);
					String name = itemJson.getString("wname");
					String brand = HtmlGenUtils.getBrandName(name);
					if(!"".equals(brand)){
						Integer brandTotal = brandTotalMap.get(brand);
						if(brandTotal == null){
							brandTotal= 0 ;
						}
						brandTotal += 1;
						brandTotalMap.put(brand, brandTotal);
						Map<String,String> map = new HashMap<String, String>();
						String img = itemJson.getString("imageurl");
						if(img.indexOf("http") == -1){
							img = "http:" + img; 
						}
						String imgPath = OSSUtils.uploadImg(img,brand,"secKill");
						String newPrice = itemJson.getString("miaoShaPrice");
						String oldPrice = itemJson.getString("jdPrice");
						map.put("brand", brand);
						map.put("name", name);
						map.put("img", imgPath);
						map.put("newPrice", newPrice);
						map.put("oldPrice", oldPrice);
						map.put("index", index + "");
						if(id.equals("46")){
							map.put("totleSize", totalSize + "");
						}else{
							map.put("totleSize", "0");
						}
						mapList.add(map);
						System.out.println("坑位:" + index + "\r\n图片:" + img + "\r\n名称：" + name + "\r\n新价格：" + newPrice + "\r\n旧价格:" + oldPrice);
						
					}
					index ++;
				}
			}
		}
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
				String sql = "insert into SecKill (brand,name, img, miaoshaPrice,jdPrice,proIndex,insertTime,skinTotal,brandTotal) "
						+ "values (?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, map.get("brand"));
					ps.setString(2, map.get("name"));
					ps.setString(3, map.get("img"));
					ps.setDouble(4, Double.valueOf(map.get("newPrice")));
					ps.setDouble(5, Double.valueOf(map.get("oldPrice")));
					ps.setInt(6, Integer.valueOf(map.get("index")));
					String date = format.format(new Date());
					ps.setString(7, date);
					ps.setInt(8, Integer.valueOf(map.get("totleSize")));
					int brandTotal = 0;
					if(brandTotalMap.get(map.get("brand")) != null){
						brandTotal = brandTotalMap.get(map.get("brand"));
					}
					ps.setInt(9,brandTotal);
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
