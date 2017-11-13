package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import database.JDBCConnection;
import Utils.HtmlGenUtils;

public class CbbsTmallBrandTracffic {
	public static void main(String[] args){
		crawl();
	}
	
	public static void crawl(){
		Map<String,String> brandMap = getBrandList();
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String date = HtmlGenUtils.getDataTime("yyyyMMdd",-2);
		String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd",-2);
		for(String brandId : brandMap.keySet()){
			String brandName = brandMap.get(brandId);
			System.out.println(brandName);
			String url = "https://smdata.cbbs.tmall.com/index/queryItemTab.jsonp?startDate="+date+"&endDate=" + date
					+ "&areaId=-99999&brandId="+brandId+"&_ksTS=1501209006738_237&callback=jsonp238";
			try {
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader("cookie",CbbsTmall.cookie);
				get.setHeader("Host","smdata.cbbs.tmall.com");
				get.setHeader("Referer","https://smdata.cbbs.tmall.com/tm-data/pages/product/index?spm=a224m.7959549.0.0.da3dc77U34Chx");
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				CloseableHttpResponse response = client.execute(get);
				String result = EntityUtils.toString(response.getEntity(),"utf-8");
				System.out.println(result);
				if(!result.equals("")){
					parseHtmlCode(result,mapList,brandId,brandName,dataTime);
				}
				Thread.sleep(new Random().nextInt(5) * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(mapList.size() > 0){
			saveData(mapList);
		}
	}
	
	/**���浽���ݿ�*/
	private static void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		if(mapList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into CSTmallBrandTraffic_copy (����,Ʒ������,Ʒ��ID,֧�����,֧���������ռ��,"
						+ "֧���Ӷ�����,֧���Ӷ���������ռ��,֧����Ʒ����,֧����Ʒ��������ռ��,֧�������,֧�����������ռ��,"
						+ "�͵���,�͵�������ռ��,��Ʒ����ҳ�ÿ���,��Ʒ����ҳ�ÿ�������ռ��,����,��������) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, "����");
					ps.setString(2, map.get("brandName"));
					ps.setString(3, map.get("brandId"));
					ps.setString(4, map.get("payMoney"));
					ps.setString(5, map.get("wirePercent"));
					ps.setString(6, map.get("paySubOrders"));
					ps.setString(7, map.get("paySubPercent"));
					ps.setString(8, map.get("payItemNum"));
					ps.setString(9, map.get("itemNumPercent"));
					ps.setString(10, map.get("payBuyerNum"));
					ps.setString(11, map.get("burerNumPercent"));
					ps.setString(12, map.get("perOrderPrice"));
					ps.setString(13, map.get("pricePercent"));
					ps.setString(14, map.get("payTracerNum"));
					ps.setString(15, map.get("tracerNumPercent"));
					ps.setString(16, map.get("dataTime"));
					ps.setString(17, "����");
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("������Ʒ�ſ����ݳɹ�");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**����json����
	 * @param mapList 
	 * @param dataTime 
	 * @param brandName 
	 * @param brandId */
	private static void parseHtmlCode(String result, List<Map<String, String>> mapList, String brandId, String brandName, String dataTime) {
		// TODO Auto-generated method stub
		try {
			System.out.println(result);
			result = result.substring(result.indexOf("{"), result.length() - 1);
			
			JSONObject json = JSONObject.fromObject(result);
			JSONArray dataJson = json.getJSONArray("data");
			Map<String,String> map = new HashMap<String, String>();
			
			
			String payMoney = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(0)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("payMoney", payMoney);
			//map.put("dataTime", dataTime);
			String wirePercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(0)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("wirePercent", wirePercent);
			String paySubOrders = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(1)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("paySubOrders", paySubOrders);
			String paySubPercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(1)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("paySubPercent", paySubPercent);
			
			String payItemNum = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(2)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("payItemNum", payItemNum);
			String itemNumPercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(2)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("itemNumPercent", itemNumPercent);
			
			String payBuyerNum = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(3)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("payBuyerNum", payBuyerNum);
			String burerNumPercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(3)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("burerNumPercent", burerNumPercent);
			String perOrderPrice = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(4)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("perOrderPrice", perOrderPrice);
			String pricePercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(4)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("pricePercent", pricePercent);
			String payTracerNum = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(5)).getJSONArray("fields").get(0)).getString("fieldValue");
			map.put("payTracerNum", payTracerNum);
			
			String tracerNumPercent = JSONObject.fromObject(JSONObject.fromObject(dataJson.get(5)).getJSONArray("fields").get(3)).getString("fieldValue");
			map.put("tracerNumPercent", tracerNumPercent);
			map.put("brandId", brandId);
			map.put("brandName", brandName);
			map.put("dataTime", dataTime);
			System.out.println(payMoney + "~" + wirePercent + "~" + paySubOrders + "~" + paySubPercent
					+ "~" + payItemNum + "~" + itemNumPercent + "~" + payBuyerNum + "~" + burerNumPercent
					+ "~" + perOrderPrice + "~" + pricePercent + "~" + payTracerNum + "~" + tracerNumPercent);
			mapList.add(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String,String> getBrandList(){
		Map<String,String> map = new HashMap<String, String>();
		map.put("3308234", "Gillette/����");
		map.put("3665960", "Tide/̭��");
		map.put("20439", "Pampers/�ﱦ��");
		map.put("3646703", "���");
		map.put("3309017", "safeguard/�����");
		map.put("20085", "Olay/������");
		map.put("3249193", "�����");
		map.put("20090", "����");
		map.put("4200388", "����");
		map.put("94390", "whisper/���汦");
		map.put("20103", "ɳ��");
		map.put("94137", "����˿");
		map.put("94140", "REJOICE/Ʈ��");
		map.put("94519", "Crest/�ѽ�ʿ");
		map.put("102864", "Oral-B/ŷ��B");
		map.put("3385285", "Oral-B/ŷ��B");
		map.put("60736176", "TAMPAX/����˿");
		map.put("-99999", "����");
		map.put("51600372", "SARASA");
		map.put("94843581", "Febreze");
		return map;
	}
}
