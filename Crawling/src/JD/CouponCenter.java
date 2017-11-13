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

import database.JDBCConnection;
import Utils.HtmlGenUtils;
import Utils.OSSUtils;


/**
 * 领卷中心
 * **/
public class CouponCenter {
	private static int totalSize = 0;
	private static Map<String,Integer> brandTotalMap = new HashMap<String,Integer>();//坑位数
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new CouponCenter().crawlingJd();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void crawlingJd() throws Exception{
		int page = 1;
		boolean hasData = true;
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		while(hasData){
			String url = "https://coupon.m.jd.com/center/toCouponList.json?page="+page+"&categoryId=12";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority", "coupon.m.jd.com");
			get.setHeader(":method", "GET");
			get.setHeader(":path", "/center/toCouponList.json?page="+page+"&categoryId=12");
			get.setHeader(":scheme", "https");
			get.setHeader("referer", "https://coupon.m.jd.com/center/getCouponCenter.action");
			get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			
			HttpEntity entity = response.getEntity();
			hasData = parseHtml(entity,page,mapList);
			page++;
		}
		saveData(mapList);
	}

	
	/**
	 * 解析获取到的页面
	 * @param mapList 
	 * */
	private static boolean parseHtml(HttpEntity entity,int page, List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		boolean hasData = true;
		if(entity != null){
			try {
				JSONObject dataJson = JSONObject.fromObject(EntityUtils.toString(entity));
				String totalPage = dataJson.getString("totalPage");
				if(totalPage.equals(page + "")){
					hasData = false;
				}
				JSONArray dataArrayJson = dataJson.getJSONArray("couponItem");
				totalSize += dataArrayJson.size();
				int index = 1;
				for(Object obj : dataArrayJson){
					Map<String,String> map = new HashMap<String,String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					String name = itemJson.getString("limitStr");
					String brand = HtmlGenUtils.getBrandName(name);
					if(!"".equals(brand)){
						Integer brandTotal = brandTotalMap.get(brand);
						if(brandTotal == null){
							brandTotal= 0 ;
						}
						brandTotal += 1;
						String img = itemJson.getString("androidImgUrl");
						String imgPath = OSSUtils.uploadImg(img,brand,"couponCenter");
						String quotaString = itemJson.getString("quotaString");
						String denoString = itemJson.getString("denoString");
						map.put("brand", brand);
						map.put("name", name);
						map.put("img", imgPath);
						map.put("quotaString", quotaString);//机制
						map.put("denoString", denoString);//减多少价格
						map.put("index", index + "");
						mapList.add(map);
						System.out.println("坑位：" + index + "\r\n图片：" + img + "\r\n名称：" + name + "\r\n机制：" + quotaString);
					}else{
						System.out.println("没有相应的品牌券可领");
					}
					index++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			hasData = false;
		}
		
		return hasData;
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
				String sql = "insert into CouponCenter (brand,name, img, quotaString,denoString,skinTotal,proIndex,insertTime,brandTotal) "
						+ "values (?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, map.get("brand"));
					ps.setString(2, map.get("name"));
					ps.setString(3, map.get("img"));
					ps.setString(4, map.get("quotaString"));
					ps.setString(5, map.get("denoString"));
					ps.setInt(6, totalSize);
					ps.setInt(7, Integer.valueOf(map.get("index")));
					String date = format.format(new Date());
					ps.setString(8, date);
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
