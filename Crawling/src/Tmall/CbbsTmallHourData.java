package Tmall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import database.JDBCConnection;
import Utils.HtmlGenUtils;

public class CbbsTmallHourData implements Job {
	public static String getDateTime(String partter){
		String dateTime = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(partter);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			dateTime = format.format(cal.getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dateTime;
	}
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		Map<String,Map<String,String>> maps = CbbsTmall.brandCateGoryMap(true);
		Iterator<String> it = maps.keySet().iterator();
		Map<String,List<Map<String,String>>> mapList = new HashMap<String, List<Map<String,String>>>();
		String queryTime = getDateTime("yyy-MM-dd");
		//获取数据字段
		List<String> chartMetrics = getMetrics();
		while(it.hasNext()){
			String brandId = it.next();
			List<Map<String,String>> list = mapList.get(brandId);
			if(list == null){
				list = new ArrayList<Map<String,String>>();
			}
			Map<String,String> map = maps.get(brandId);
			String brandName = map.get(brandId);
			//品类
			for(String cateGoryId : map.keySet()){
				Map<String,String> dataMap = new HashMap<String, String>();
				dataMap.put("area", "华北");
				dataMap.put("brand", brandName);
				dataMap.put("timeSlot", queryTime);
				String category = map.get(cateGoryId);
				dataMap.put("category", category);
				for(String metric : chartMetrics){//数据字段
					//payMoney 支付金额  paySubOrderNum 支付子订单数  paySubOrderAvg 子订单均价  
					//payNum 支付商品件数 buyerNum 支付买家数 payMoneyPerOrder 客单价
					try {
						String url = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?chartMetrics=" + metric
								+ "&query.brandId="+brandId+"&query.cate3Id="+cateGoryId+"&query.logicArea=-99999"
								+ "&query.time1="+queryTime+"&serviceId=sm_rt_supp_order_hour_aggr_data";
						JSONObject dataJson = getJsonData(url);
						parseJson(dataJson,dataMap,metric);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				list.add(dataMap);
			}
			mapList.put(brandId, list);
		}
		
		if(mapList.size() > 0){
			writeDateExcel(mapList);
			saveDate(mapList);
		}
	}
	
	
	/**
	 * 获取url对应的json数据
	 * @param dataMap 
	 * */
	private JSONObject getJsonData(String url) {
		// TODO Auto-generated method stub
		JSONObject dataJson = new JSONObject();
		try {
			for(int i = 0;i<5;i++){
				try {
					CloseableHttpClient client = HttpClients.createDefault();
					HttpGet get = new HttpGet(url);
					get.setHeader("cookie",CbbsTmall.cookie);
					get.setHeader("Host","dataweb.cbbs.tmall.com");
					get.setHeader("Origin","http://web.cbbs.tmall.com");
					get.setHeader("Referer","http://web.cbbs.tmall.com/pages/chaoshi/rtoverview");
					get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
					CloseableHttpResponse response = client.execute(get);
					System.out.println(url);
					JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
					System.out.println(json);
					Thread.sleep(3000);
					if(json.getString("success").equals("true")){
						dataJson = json;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataJson;
	}
	
	/**
	 * 解析json数据写excel
	 * @param dataMap 
	 * @param metric 
	 * */
	private void parseJson(JSONObject dataJson, Map<String, String> dataMap, String metric) {
		// TODO Auto-generated method stub
		String date = getDateTime("yyyyMMdd");
		if(!dataJson.isEmpty()){
			JSONArray dataArray = dataJson.getJSONArray("data");
			for(Object obj : dataArray){
				JSONObject valueJson = JSONObject.fromObject(obj).getJSONObject("value");
				JSONArray data = valueJson.getJSONArray("data");
				for(Object jsonObject : data){
					JSONObject itemJson = JSONObject.fromObject(jsonObject);
					System.out.println(itemJson);
					String h1 = "";
					if(itemJson.containsKey("1")){
						h1 = itemJson.getString("h1");
					}else{
						h1 = itemJson.getString("hour");
					}
					String x = itemJson.getString("x");
					String y = itemJson.getString("y");
					if(h1.indexOf(date) != -1){
						//dataMap.put(metric + "_hour_" + index, x);
						dataMap.put(metric + "_" + x, y);
					}
				}
			}
		}
	}
	
	/**保存到数据库*/
	private void saveDate(Map<String, List<Map<String, String>>> mapList) {
		// TODO Auto-generated method stub
		try {
			Connection conn = JDBCConnection.connectToServer("data");
			String sql = "insert into CSTmallRealTimeTransaction(area,brand,category,dataTime,dataHour,"
					+ "payMoney,paySubOrderNum,paySubOrderAvg,payNum,buyerNum,payMoneyPerOrder,brandId) "
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			Iterator<String> it = mapList.keySet().iterator();
			int count = 0;
			int size = 1000;
			while(it.hasNext()){
				String brandId = it.next();
				List<Map<String,String>> list = mapList.get(brandId);
				for(Map<String, String> map : list){
					String category = map.get("category");
					String dataTime = map.get("timeSlot");
					String brandName = map.get("brand");
					for(int i = 0;i<24;i++){
						String dataHour = i + ":00";
						String payMoney = map.get("payMoney_" + dataHour)==null?"0":map.get("payMoney_" + dataHour);
						String paySubOrderNum = map.get("paySubOrderNum_" + dataHour)==null?"0":map.get("paySubOrderNum_" + dataHour);
						String paySubOrderAvg = map.get("paySubOrderAvg_" + dataHour)==null?"0":map.get("paySubOrderAvg_" + dataHour);
						String payNum = map.get("payNum_" + dataHour)==null?"0":map.get("payNum_" + dataHour);
						String buyerNum = map.get("buyerNum_" + dataHour)==null?"0":map.get("buyerNum_" + dataHour);
						String payMoneyPerOrder = map.get("payMoneyPerOrder_" + dataHour)==null?"0":map.get("payMoneyPerOrder_" + dataHour);
						pst.setString(1, "华北");
						pst.setString(2, brandName);
						pst.setString(3, category);
						pst.setString(4, dataTime);
						pst.setString(5, dataHour);
						pst.setString(6, payMoney);
						pst.setString(7, paySubOrderNum);
						pst.setString(8, paySubOrderAvg);
						pst.setString(9, payNum);
						pst.setString(10, buyerNum);
						pst.setString(11, payMoneyPerOrder);
						pst.setString(12, brandId);
						if(++count%size == 0){
							pst.executeBatch();
						}
					}
				}
			}
			pst.executeBatch();
			System.out.println("插入实时数据成功");
			pst.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private List<String> getMetrics() {
		return Arrays.asList(new String[]{"payMoney","paySubOrderNum","paySubOrderAvg",
				"payNum","buyerNum","payMoneyPerOrder"});
	}
	/**
	 * 写excel
	 * */
	private void writeDateExcel(Map<String, List<Map<String, String>>> mapList) {
		// TODO Auto-generated method stub
		WritableWorkbook wwb = null;
		String sheetName = "华北";
		try {
			File file = new File("C:/hourData.xlsx");
			if(!file.exists()){
				wwb = Workbook.createWorkbook(new FileOutputStream(file));
				wwb.createSheet("sheet", 0);
				wwb.write();
				wwb.close();
			}
			wwb = Workbook.createWorkbook(file, Workbook.getWorkbook(file));
			if(wwb == null){
				return;
			}
			WritableSheet sheet = wwb.getSheet(sheetName);
			if(sheet == null){
				sheet = wwb.createSheet(sheetName, 0);
				Label area = new Label(0, 0, "地区");
				Label brand = new Label(1, 0, "品牌");
				Label category = new Label(2, 0, "类目");
				Label day = new Label(3, 0, "日期");
				Label hour = new Label(4, 0, "时段");
				Label payAmount = new Label(5, 0, "支付金额");
				Label paySubOrderNum = new Label(6, 0, "支付子订单数");
				Label paySubOrderAvg = new Label(7, 0, "子订单均价");
				Label payNum = new Label(8, 0, "支付商品件数");
				Label buyerNum = new Label(9, 0, "支付买家数");
				Label payMoneyPerOrder = new Label(10, 0, "客单价");
				
				sheet.addCell(area);
				sheet.addCell(brand);
				sheet.addCell(category);
				sheet.addCell(day);
				sheet.addCell(hour);
				sheet.addCell(payAmount);
				sheet.addCell(paySubOrderNum);
				sheet.addCell(paySubOrderAvg);
				sheet.addCell(payNum);
				sheet.addCell(buyerNum);
				sheet.addCell(payMoneyPerOrder);
			}
			int currentRow = sheet.getRows();
			Iterator<String> it = mapList.keySet().iterator();
			while(it.hasNext()){
				String brandName = it.next();
				List<Map<String,String>> list = mapList.get(brandName);
				for(Map<String, String> map : list){
					for(int i = 0;i<24;i++){
						String dataHour = i + ":00";
						String payMoney = map.get("payMoney_" + dataHour)==null?"0":map.get("payMoney_" + dataHour);
						String paySubOrderNum = map.get("paySubOrderNum_" + dataHour)==null?"0":map.get("paySubOrderNum_" + dataHour);
						String paySubOrderAvg = map.get("paySubOrderAvg_" + dataHour)==null?"0":map.get("paySubOrderAvg_" + dataHour);
						String payNum = map.get("payNum_" + dataHour)==null?"0":map.get("payNum_" + dataHour);
						String buyerNum = map.get("buyerNum_" + dataHour)==null?"0":map.get("buyerNum_" + dataHour);
						String payMoneyPerOrder = map.get("payMoneyPerOrder_" + dataHour)==null?"0":map.get("payMoneyPerOrder_" + dataHour);
						Label label1 = new Label(0, currentRow, map.get("area"));
						Label label2 = new Label(1, currentRow, brandName);
						Label label3 = new Label(2, currentRow, map.get("category"));
						Label label4 = new Label(3, currentRow, map.get("timeSlot"));
						Label label5 = new Label(4, currentRow, dataHour);
						Number label6 = new Number(5, currentRow,Double.valueOf(payMoney));
						Label label7 = new Label(6, currentRow, paySubOrderNum);
						Label label8 = new Label(7, currentRow, paySubOrderAvg);
						Label label9 = new Label(8, currentRow, payNum);
						Label label10 = new Label(9, currentRow, buyerNum);
						Label label11 = new Label(10, currentRow, payMoneyPerOrder);
						sheet.addCell(label1);
						sheet.addCell(label2);
						sheet.addCell(label3);
						sheet.addCell(label4);
						sheet.addCell(label5);
						sheet.addCell(label6);
						sheet.addCell(label7);
						sheet.addCell(label8);
						sheet.addCell(label9);
						sheet.addCell(label10);
						sheet.addCell(label11);
						currentRow++;
					}
				}
			}
			wwb.write();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(wwb != null){
				try {
					wwb.close();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws JobExecutionException{
		new CbbsTmallHourData().execute(null);
		
	}
}
