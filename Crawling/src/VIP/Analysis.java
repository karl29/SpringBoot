package VIP;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import database.JDBCConnection;
import Utils.HtmlGenUtils;

public class Analysis {
	public static void main(String[] args){
		List<Map<String, String>> brandList = ProductSaleDetail.getBrandList();
		crawl(brandList);
	}
	
	public static void crawl(List<Map<String, String>> brandList){
		String contrastBeginDate = HtmlGenUtils.getDataTime("yyyy-MM-dd", -60);
		String startTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -30);
		String endTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
		List<Map<String,String>> dataList = null;
		for(Map<String,String> map : brandList){
			try {
				String brandName = map.get("brandStoreName");
				String brandId = map.get("brandStoreSn");
				if(!brandId.equals("10022315")){
					dataList = new ArrayList<Map<String,String>>();
					System.out.println(brandName);
					map.put("queryTime", endTime);
					String url = "http://compass.vis.vip.com/homepage/metric/queryAllMetric?"
							+ "callback=jQuery32109374578828768407_1508914715295&brandStoreName=" + URLEncoder.encode(brandName, "utf-8")
							+ "&dateType=D&detailType=D&beginDate="+startTime+"&endDate=" + endTime
							+ "&contrastBeginDate="+contrastBeginDate+"&contrastEndDate="+endTime+"&_=" + System.currentTimeMillis();
					Map<String,String> headerMap = new HashMap<String, String>();
					headerMap.put("url", url);
					headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
					String htmlCode = VipCrawl.getHtmlCode(headerMap);
					parseAnalysisHtmlCode(htmlCode,dataList,map);
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(dataList != null && dataList.size() > 0){
				saveData(dataList);
			}
		}
	}
	
	
	/**保存数据*/
	private static void saveData(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("vip");
			//                                                                      sales          
			String sql = "insert into vip_brandAnalysis(brandId,brandName,dataTime,totalAmount,uv,coverRate,buyerNum,"
					+ "avgOrderSalesAmount,avgUserSalesAmount,cutGoodsMoney,orderCnt,saleStockAmt,"
					+ "stockAmtOnline,stockCntOnline)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("brandId"));
				pst.setString(2, map.get("brandName"));
				pst.setString(3, map.get("dataTime"));
				pst.setString(4, map.get("sales"));
				pst.setString(5, map.get("flowUv"));
				pst.setString(6, map.get("flowConversion"));
				pst.setString(7, map.get("consumerCount"));
				pst.setString(8, map.get("avgOrderSalesAmount"));
				pst.setString(9, map.get("avgUserSalesAmount"));
				pst.setString(10, map.get("cutGoodsMoney"));
				pst.setString(11, map.get("orderCnt"));
				pst.setString(12, map.get("saleStockAmt"));
				pst.setString(13, map.get("stockAmtOnline"));
				pst.setString(14, map.get("stockAmtOnline"));
				System.out.println(map.get("orderCnt") + "\n" + map.get("stockAmtOnline"));
				pst.addBatch();
				
			}
			pst.executeBatch();
			System.out.println("插入数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析json数据
	 * @param queryMap 
	 * @param dataList 
	 * */
	private static void parseAnalysisHtmlCode(String htmlCode, List<Map<String, String>> dataList, 
			Map<String, String> queryMap) {
		try {
			System.out.println(htmlCode);
			htmlCode = htmlCode.substring(htmlCode.indexOf("(") + 1, htmlCode.indexOf(")"));
			JSONObject arr = JSONObject.fromObject(htmlCode).getJSONObject("singleResult")
					.getJSONObject("chart").getJSONObject("data");
			JSONArray avgOrderSalesAmountJson = arr.getJSONArray("avgOrderSalesAmount").getJSONArray(0);//客单价
			JSONArray avgUserSalesAmountjSON = arr.getJSONArray("avgUserSalesAmount").getJSONArray(0);//客均价
			JSONArray consumerCountJson = arr.getJSONArray("consumerCount").getJSONArray(0);//买家数
			JSONArray cutGoodsMoneyJson = arr.getJSONArray("cutGoodsMoney").getJSONArray(0);//满减金额
			JSONArray flowConversionJson = arr.getJSONArray("flowConversion").getJSONArray(0);//转化率
			JSONArray flowUvJson = arr.getJSONArray("flowUv").getJSONArray(0);//UV
			JSONArray orderCntJson = arr.getJSONArray("orderCnt").getJSONArray(0);//订单数
			JSONArray saleStockAmtJson = arr.getJSONArray("saleStockAmt").getJSONArray(0);//售卖比
			JSONArray salesJson = arr.getJSONArray("sales").getJSONArray(0);//销售额
			JSONArray stockAmtOnlineJson = arr.getJSONArray("stockAmtOnline").getJSONArray(0);//货值
			JSONArray stockCntOnlineJson = arr.getJSONArray("stockCntOnline").getJSONArray(0);//货量
			Map<String,String> map = null;
			for(int i = 0;i<avgOrderSalesAmountJson.size();i++){
				map = new HashMap<String, String>();
				JSONObject obj =  JSONObject.fromObject(avgOrderSalesAmountJson.get(i));
				String dataTime = obj.getString("x").trim();
				if(dataTime.equals("2017-11-01")){
					map.put("dataTime", dataTime);
					map.put("avgOrderSalesAmount", obj.getString("y"));
					map.put("avgUserSalesAmount", JSONObject.fromObject(avgUserSalesAmountjSON.get(i)).getString("y"));
					map.put("consumerCount", JSONObject.fromObject(consumerCountJson.get(i)).getString("y"));
					map.put("cutGoodsMoney", JSONObject.fromObject(cutGoodsMoneyJson.get(i)).getString("y"));
					map.put("flowConversion", JSONObject.fromObject(flowConversionJson.get(i)).getString("y"));
					map.put("flowUv", JSONObject.fromObject(flowUvJson.get(i)).getString("y"));
					map.put("orderCnt", JSONObject.fromObject(orderCntJson.get(i)).getString("y"));
					map.put("saleStockAmt", JSONObject.fromObject(saleStockAmtJson.get(i)).getString("y"));
					map.put("sales", JSONObject.fromObject(salesJson.get(i)).getString("y"));
					map.put("stockAmtOnline", JSONObject.fromObject(stockAmtOnlineJson.get(i)).getString("y"));
					map.put("stockCntOnline", JSONObject.fromObject(stockCntOnlineJson.get(i)).getString("y"));
					map.put("brandId", queryMap.get("brandStoreSn"));
					map.put("brandName", queryMap.get("brandStoreName"));
					dataList.add(map);
					System.out.println(dataTime + "\n" + obj.getString("y"));
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
