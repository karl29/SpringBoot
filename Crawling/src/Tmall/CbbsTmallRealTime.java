package Tmall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import Utils.HtmlGenUtils;

public class CbbsTmallRealTime implements Job {
	private static String cookie = "ctoken=CPlvvUiUmWdzsEYQTJBmascm-web; cookie2=1e612926a037dc45eafe7ffc9fe74241; t=c42b83b8ac1fc3f0e182470df6f21293; _tb_token_=c6db6518c967c; SCMSESSID=yGoNTZKx0yF_5n257Z5_SqXxOo4; cna=5PfAEUubAVkCAbcGrW1ZlO0o; isg=AsPDKOp4nhUkQ1Jk7AS1C8GAUoetkFBVLvgIGfWk7CKQtOzWfQzYyrxiWnIB";
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm");
		Map<String,Map<String,String>> maps = CbbsTmall.brandCateGoryMap(true);
		Iterator<String> it = maps.keySet().iterator();
		//Map<String,String> brandMap = CbbsTmall.brandMap;
		Map<String,List<Map<String,String>>> mapList = new HashMap<String, List<Map<String,String>>>();
		while(it.hasNext()){
			String brandId = it.next();
			//String brandName = brandMap.get(brandId);
			List<Map<String,String>> list = mapList.get(brandId);
			if(list == null){
				list = new ArrayList<Map<String,String>>();
			}
			Map<String,String> map = maps.get(brandId);
			for(String cateGoryId : map.keySet()){
				try {
					Map<String,String> dataMap = new HashMap<String, String>();
					dataMap.put("area", "华北");
					dataMap.put("timeSlot", format.format(Calendar.getInstance().getTime()));
					String category = map.get(cateGoryId);
					dataMap.put("category", category);
					/*String url = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?query.brandId="+brandId+"&"
							+ "query.cate3Id="+cateGoryId+"&query.logicArea=-99999&serviceId=sm_rt_supp_order_label";*/
					String url = "http://dataweb.cbbs.tmall.com/data/service/invoke.json?chartMetrics=payMoney"
							+ "&query.brandId="+brandId+"&query.cate3Id="+cateGoryId+"&query.logicArea=-99999"
							+ "&serviceId=sm_rt_supp_order_hour_aggr_data";
					getJsonData(url,dataMap);
					list.add(dataMap);
					Thread.sleep(4000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mapList.put(brandId, list);
		}
		
		if(mapList.size() > 0){
			writeDateExcel(mapList);
		}
	}
	
	/**
	 * 获取url对应的json数据
	 * @param dataMap 
	 * */
	private void getJsonData(String url, Map<String, String> dataMap) {
		// TODO Auto-generated method stub
		try {
			JSONObject dataJson = new JSONObject();
			for(int i = 0;i<5;i++){
				try {
					CloseableHttpClient client = HttpClients.createDefault();
					HttpGet get = new HttpGet(url);
					get.setHeader("cookie",cookie);
					get.setHeader("Host","dataweb.cbbs.tmall.com");
					get.setHeader("Origin","http://web.cbbs.tmall.com");
					get.setHeader("Referer","http://web.cbbs.tmall.com/pages/chaoshi/rtoverview");
					get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
					CloseableHttpResponse response = client.execute(get);
					JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
					System.out.println(json);
					if(json.getString("success").equals("true")){
						dataJson = json;
						break;
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			parseJson(dataJson,dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析json数据写excel
	 * @param dataMap 
	 * */
	private void parseJson(JSONObject dataJson, Map<String, String> dataMap) {
		// TODO Auto-generated method stub
		if(!dataJson.isEmpty()){
			JSONArray dataArray = dataJson.getJSONArray("data");
			for(Object obj : dataArray){
				JSONObject valueJson = JSONObject.fromObject(obj).getJSONObject("value");
				JSONArray data = valueJson.getJSONArray("data");
				for(Object jsonObject : data){
					JSONObject itemJson = JSONObject.fromObject(jsonObject);
					System.out.println(itemJson);
					String desc = itemJson.getString("description");
					String label = itemJson.getString("label");
					desc = desc.substring(0, desc.indexOf(" "));
					dataMap.put(label, desc);
				}
			}
		}
	}
	
	/**
	 * 写excel
	 * */
	private void writeDateExcel(Map<String, List<Map<String, String>>> mapList) {
		// TODO Auto-generated method stub
		WritableWorkbook wwb = null;
		String sheetName = "华北";
		try {
			File file = new File("C:/data.xlsx");
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
				Label timeSlot = new Label(3, 0, "时间");
				Label payAmount = new Label(4, 0, "支付金额");
				Label payCount = new Label(5, 0, "支付子订单数");
				Label subAvg = new Label(6, 0, "子订单均价");
				Label payProductTotal = new Label(7, 0, "支付商品件数");
				Label payBuyer = new Label(8, 0, "支付买家数");
				Label prices = new Label(9, 0, "客单价");
				
				sheet.addCell(area);
				sheet.addCell(brand);
				sheet.addCell(category);
				sheet.addCell(timeSlot);
				sheet.addCell(payAmount);
				sheet.addCell(payCount);
				sheet.addCell(subAvg);
				sheet.addCell(payProductTotal);
				sheet.addCell(payBuyer);
				sheet.addCell(prices);
			}
			int currentRow = sheet.getRows();
			Iterator<String> it = mapList.keySet().iterator();
			while(it.hasNext()){
				String brandName = it.next();
				List<Map<String,String>> list = mapList.get(brandName);
				for(Map<String, String> map : list){
					Label label1 = new Label(0, currentRow, map.get("area"));
					Label label2 = new Label(1, currentRow, brandName);
					Label label3 = new Label(2, currentRow, map.get("category"));
					Label label4 = new Label(3, currentRow, map.get("timeSlot"));
					Number label5 = new Number(4, currentRow, Double.valueOf(map.get("支付金额")));
					Number label6 = new Number(5, currentRow, Integer.valueOf(map.get("支付子订单数")));
					Number label7 = new Number(6, currentRow, Double.valueOf(map.get("子订单均价")));
					Number label8 = new Number(7, currentRow, Integer.valueOf(map.get("支付商品件数")));
					Number label9 = new Number(8, currentRow, Integer.valueOf(map.get("支付买家数")));
					Number label10 = new Number(9, currentRow, Double.valueOf(map.get("客单价")));
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
					currentRow++;
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
	
	public static void writeHssfExcel(){
		
	}
	public static void main(String[] args) throws JobExecutionException{
		new CbbsTmallRealTime().execute(null);
	}
}
