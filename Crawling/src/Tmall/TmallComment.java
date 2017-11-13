package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;
import database.JDBCConnection;

public class TmallComment {
	private static String COOKIE = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		COOKIE = TmallLogin.login();
		crawMoComment();
	}
	
	/**
	 * 爬取M端评论
	 * */
	public static void crawMoComment(){
		List<Map<String,String>> skuList = getSkuList();
		if(skuList != null){
			for(Map<String,String> map : skuList){
				List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
				int page = 1;
				boolean hasRate = true;
				while(hasRate){
					String url = "https://rate.tmall.com/list_detail_rate.htm?"
							+ "itemId="+map.get("skuId")+"&sellerId="+map.get("shopId")+"&order=3&currentPage="+page+"&pageSize=100"
							+ "&&callback=_DLP_2490_er_3_currentPage_"+page+"_pageSize_100_";
					String htmlCode = getHtmlCode(url,map);
					hasRate = parseHtmlCode(htmlCode,map,page,dataList);
					page++;
				}
				saveData(dataList);
			}
		}
	}
	
	
	/**
	 * 
	 * */
	private static void saveData(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into TmallSkuComments(skuId,itemName,rateTime,goldUser,displayUserNick,commentId,content)"
					+ " values(?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("skuId"));
				pst.setString(2, map.get("itemName"));
				pst.setString(3, map.get("rateTime"));
				pst.setString(4, map.get("goldUser"));
				pst.setString(5, map.get("displayUserNick"));
				pst.setString(6, map.get("commentId"));
				pst.setString(7, map.get("content"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析获取到的json数据
	 * @param page 
	 * @param dataList 
	 * */
	private static boolean parseHtmlCode(String htmlCode, Map<String, String> map, int page, List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		boolean hasRate = true;
		try {
			System.out.println(htmlCode);
			htmlCode = htmlCode.substring(htmlCode.indexOf("rateDetail") - 2, htmlCode.length() - 1);
			System.out.println(htmlCode);
			JSONObject rateJson = JSONObject.fromObject(htmlCode).getJSONObject("rateDetail");
			int lastPage = rateJson.getJSONObject("paginator").getInt("lastPage");
			if(page >= lastPage){
				hasRate = false;
			}
			JSONArray rateList = rateJson.getJSONArray("rateList");
			Map<String,String> rateMap = null;
			for(Object obj : rateList){
				try {
					rateMap = new HashMap<String,String>();
					JSONObject json = JSONObject.fromObject(obj);
					String rateTime = json.getString("rateDate");
					String displayUserNick = json.getString("displayUserNick");
					String commentId = json.getString("id");
					String content = json.getString("rateContent");
					int goldUser = 0;
					if(json.getString("goldUser").equals("true")){
						goldUser = 1;
					}
					rateMap.put("rateTime", rateTime);
					rateMap.put("displayUserNick", displayUserNick);
					rateMap.put("commentId", commentId);
					rateMap.put("content", content);
					rateMap.put("goldUser", goldUser + "");
					rateMap.put("skuId", map.get("skuId"));
					rateMap.put("itemName", map.get("title"));
					System.out.println(rateTime + "~~~~" + displayUserNick);
					dataList.add(rateMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasRate;
	}

	/**
	 * 获取评论json数据
	 * @param map 
	 * */
	private static String getHtmlCode(String url, Map<String, String> map) {
		String htmlCode = "";
		try {
			for(int i = 0;i<3;i++){
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader("Cookie",COOKIE);
				get.setHeader("referer","ttps://detail.m.tmall.com/item.htm?id="+map.get("skuId")+"&areaId=440100&user_id="+map.get("shopId")+"&is_b=1&rn=b6817adacc48b7744586210a3150892c");
				get.setHeader("user-agent",HtmlGenUtils.getRandomMobileUserAgent());
				htmlCode = EntityUtils.toString(client.execute(get).getEntity(), "utf-8");
				Thread.sleep(new Random().nextInt(3) * 1000);
				if(!htmlCode.equals("")){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}

	/**
	 * 获取skuid集合
	 * */
	public static List<Map<String,String>> getSkuList(){
		List<Map<String,String>> list = null;
		/*try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "select tsd.skuId,tsd.title,tsd.shopId from TmallSkuDetail tsd " + 
						 " left join TmallSkuComments tsc " +
						 " on tsd.skuId=tsc.skuId " +
						 " where tsc.skuId is null";
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			Map<String,String> map = null;
			while(rs.next()){
				if(list == null){
					list = new ArrayList<Map<String,String>>();
				}
				map = new HashMap<String, String>();
				map.put("skuId", rs.getString(1));
				map.put("title", rs.getString(2));
				map.put("shopId", rs.getString(3));
				System.out.println(rs.getString(1) + "~~" + rs.getString(3));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("skuId", "538253502715");
		map.put("title", "");
		map.put("shopId", "67597230");
		list.add(map);
		Map<String,String> map2 = new HashMap<String, String>();
		map2.put("skuId", "538769457115");
		map2.put("title", "");
		map2.put("shopId", "67597230");
		list.add(map2);
		Map<String,String> map3 = new HashMap<String, String>();
		map3.put("skuId", "521825402798");
		map3.put("title", "");
		map3.put("shopId", "67597230");
		list.add(map3);
		Map<String,String> map4 = new HashMap<String, String>();
		map4.put("skuId", "17736864358");
		map4.put("title", "");
		map4.put("shopId", "67597230");
		list.add(map4);
		Map<String,String> map5 = new HashMap<String, String>();
		map5.put("skuId", "521536887769");
		map5.put("title", "");
		map5.put("shopId", "106021903");
		list.add(map5);
		return list;
	}
}
