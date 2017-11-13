package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class CbbsTmallStoreSale {
	public static void main(String[] args){
		crawl();
	}
	
	public static void crawl(){
		String dataTime = getDateTime("yyyy-MM-dd",-1);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		try {
			String url = "http://cstrip.cbbs.tmall.com/crk/queryBiSuppReport.json?start=0&limit=50"
					+ "&pageIndex=0&startTime=&endTime=&supplierCode=100035079&cate1=&cate2=&cate3=&cate4="
					+ "&brandName=&itemId=&itemTitle=&storeCode=&_ksTS=1501221393102_56";
			try {
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader("cookie",CbbsTmall.cookie);
				get.setHeader("Host","cstrip.cbbs.tmall.com");
				get.setHeader("Referer","http://cstrip.cbbs.tmall.com/crk/biSuppReportView.htm?spm=a224m.7959549.0.0.da3dc77w7OGw1");
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				CloseableHttpResponse response = client.execute(get);
				String result = EntityUtils.toString(response.getEntity(),"utf-8");
				System.out.println(result);
				if(!result.equals("")){
					parseHtmlCode(result,mapList,dataTime);
				}
				Thread.sleep(new Random().nextInt(5) * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(mapList.size() > 0){
			saveData(mapList);
		}
	}
	
	
	/**保存数据*/
	private static void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		if(mapList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into CSTmallStoreSales2017_copy (区域,日期,责任人,商品id,商品标题,"
						+ "一级类目,二级类目,三级类目,四级类目,品牌名称,仓库编码,"
						+ "sku编码,条形码,支付金额,无线端支付金额,支付订单分账金额,提报价成交金额,支付子订单数"
						+ ",无线端支付子订单数,销量,无线端销量,所属集团) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : mapList){
					ps.setString(1, "华北");
					ps.setString(2, map.get("dataTime"));
					ps.setString(3, map.get("dutyor"));
					ps.setString(4, map.get("itemId"));
					ps.setString(5, map.get("title"));
					ps.setString(6, map.get("cate1Name"));
					ps.setString(7, map.get("cate2Name"));
					ps.setString(8, map.get("cate3Name"));
					ps.setString(9, map.get("cate4Name"));
					ps.setString(10, map.get("brandName"));
					ps.setString(11, map.get("storeCodeDesc"));
					ps.setString(12, map.get("skuId"));
					ps.setString(13, map.get("barcode"));
					ps.setString(14, map.get("payOrdAmt1d001"));
					ps.setString(15, map.get("payOrdAmt1d003"));
					ps.setString(16, map.get("shareCommissionAmt1d001"));
					ps.setString(17, map.get("shrStlPayAmt1d001"));
					ps.setString(18, map.get("payOrdCnt1d001"));
					ps.setString(19, map.get("payOrdCnt1d003"));
					ps.setString(20, map.get("payOrdItmQty1d001"));
					ps.setString(21, map.get("payOrdItmQty1d003"));
					ps.setString(22, "宝洁");
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("插入日报概况数据成功");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void parseHtmlCode(String result,
			List<Map<String, String>> mapList, String dataTime) {
		// TODO Auto-generated method stub
		try {
			JSONObject json = JSONObject.fromObject(result);
			JSONArray rows = json.getJSONArray("rows");
			for(Object obj : rows){
				JSONObject itemJson = JSONObject.fromObject(obj);
				Map<String,String> map = new HashMap<String, String>();
				map.put("dataTime", dataTime);
				String dutyor = itemJson.getString("dutyor");
				map.put("dutyor", dutyor);
				String itemId = itemJson.getString("itemId");
				map.put("itemId", itemId);
				String title = itemJson.getString("itemTitle");
				map.put("title", title);
				String cate1Name = itemJson.getString("mcasCate1Name");
				map.put("cate1Name", cate1Name);
				String cate2Name = itemJson.getString("mcasCate2Name");
				map.put("cate2Name", cate2Name);
				String cate3Name = itemJson.getString("mcasCate3Name");
				map.put("cate3Name", cate3Name);
				String cate4Name  = itemJson.getString("mcasCate4Name");
				map.put("cate4Name", cate4Name);
				String brandName = itemJson.getString("brandName");
				map.put("brandName", brandName);
				String storeCodeDesc = itemJson.getString("storeCodeDesc");
				map.put("storeCodeDesc", storeCodeDesc);
				String skuId = itemJson.getString("skuId");
				map.put("skuId", skuId);
				String barcode = itemJson.getString("barcode");
				map.put("barcode", barcode);
				String payOrdAmt1d001 = itemJson.getString("payOrdAmt1d001");
				map.put("payOrdAmt1d001", payOrdAmt1d001);
				String payOrdAmt1d003 = itemJson.getString("payOrdAmt1d003");
				map.put("payOrdAmt1d003", payOrdAmt1d003);
				String shareCommissionAmt1d001 = itemJson.getString("shareCommissionAmt1d001");
				map.put("shareCommissionAmt1d001", shareCommissionAmt1d001);
				String shrStlPayAmt1d001 = itemJson.getString("shrStlPayAmt1d001");
				map.put("shrStlPayAmt1d001", shrStlPayAmt1d001);
				String payOrdCnt1d001 = itemJson.getString("payOrdCnt1d001");
				map.put("payOrdCnt1d001", payOrdCnt1d001);
				String payOrdCnt1d003 = itemJson.getString("payOrdCnt1d003");
				map.put("payOrdCnt1d003", payOrdCnt1d003);
				String payOrdItmQty1d001 = itemJson.getString("payOrdItmQty1d001");
				map.put("payOrdItmQty1d001", payOrdItmQty1d001);
				String payOrdItmQty1d003 = itemJson.getString("payOrdItmQty1d003");
				map.put("payOrdItmQty1d003", payOrdItmQty1d003);
				System.out.println(title + "~" + payOrdAmt1d001);
				mapList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDateTime(String formatType,int day){
		SimpleDateFormat format = new SimpleDateFormat(formatType);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, day);
		
		return format.format(cal.getTime());
	}
}
