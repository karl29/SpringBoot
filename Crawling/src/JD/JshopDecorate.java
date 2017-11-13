package JD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Utils.ExcelUtils;
import Utils.HtmlGenUtils;


/**jshop装修分析数据*/
public class JshopDecorate {
	
	public static void main(String[] args){
		List<Map<String,String>> mapList = ExcelUtils.getTaskList("Jshop");
		crawl(mapList,HtmlGenUtils.getDataTime("yyyy-MM-dd", 0));
	}
	
	public static void crawl(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			String projectId = map.get("id").trim();
			String refererUrl = "http://data-jshop.jd.com/mob/decorate/getPageDecorateBaseInfo?appId="+projectId+"&mobType=0";
			if(map.get("terminal").equals("Mobile")){
				String url = "http://data-jshop.jd.com/mob/realTime/queryAppRealDataByDay"
						+ "?appId="+projectId+"&mobType=0";
				String htmlCode = JShopCrawl.getDetailHtmlCode(url, refererUrl);
				if(!htmlCode.equals("")){
					try {
						JSONArray viewList = JSONObject.fromObject(htmlCode).getJSONArray("mobPitStatisticViewList");
						System.out.println(viewList.size());
						for(Object obj : viewList){
							JSONObject clickJson = JSONObject.fromObject(obj);
							String clickId = clickJson.getString("clickId");
							String pitUrl = "http://data-jshop.jd.com/mobPitDataHbase/getRealPitTargetData?"
									+ "statSiteCd=0&appId="+projectId
									+ "&clickId=" + clickId;
							String pitHtmlCode = JShopCrawl.getDetailHtmlCode(pitUrl, refererUrl);
							System.out.println(dataTime + "==========");
							parseHtmlCode(pitHtmlCode,map,dataList);
							Thread.sleep(new Random().nextInt(5) * 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("==============这是一条分割线================");
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.saveDecorate(dataList);
		}
	}
	
	/**解析json数据
	 * @param dataList 
	 * @param map 
	 * @param dataTime 
	 * @param projectId */
	private static void parseHtmlCode(String htmlCode, Map<String, String> taskMap, List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		if(!htmlCode.equals("")){
			JSONArray pitDetails = JSONObject.fromObject(htmlCode).getJSONArray("mobPitTargetInfo");
			Map<String,String> map = null;
			if(pitDetails.size() > 0){
				map = new HashMap<String, String>();
				JSONObject durationSum = JSONObject.fromObject(pitDetails.get(0));
				/*String dataTime = durationSum.getString("statDate");
				map.put("dataTime", dataTime);*/
				String category = taskMap.get("category");
				map.put("category", "");
				String brand = taskMap.get("brand");
				map.put("brand", "");
				String projectId = taskMap.get("id");
				map.put("projectId", projectId);
				String projectName = taskMap.get("name");
				map.put("projectName", projectName);
				String clickId = durationSum.getString("clickId");
				map.put("clickId", clickId);
				String clickTarget = durationSum.getString("clickTarget");
				String skuUrl = "";
				if(clickTarget.indexOf("sku_") != -1){
					String id = clickTarget.substring(clickTarget.indexOf("_") + 1, clickTarget.length());
					skuUrl = "http://item.m.jd.com/product/" + id + ".html";
				}else if(clickTarget.indexOf("sale_") != -1){
					skuUrl = clickTarget.replaceAll("sale_", "http:");
				}else{
					skuUrl = clickTarget;
				}
				map.put("skuUrl", skuUrl);
				String clickNum = durationSum.getString("clickNum");//点击量
				map.put("clickNum", clickNum);
				String clickUserNum = durationSum.getString("clickUserNum");//点击人数
				System.out.println("clickNum:" + clickNum + "~~ clickUserNum:" + clickUserNum);
				map.put("clickUserNum", clickUserNum);
				String clickRate = durationSum.getString("clickRate");//点击率
				map.put("clickRate", clickRate);
				String introduceOrderQtty = durationSum.getString("introduceOrderQtty");//引入订单量
				map.put("introduceOrderQtty", introduceOrderQtty);
				String introduceOrderMoney = durationSum.getString("introduceOrderMoney");//引入订单金额
				map.put("introduceOrderMoney", introduceOrderMoney);
				String introduceOrderRate = durationSum.getString("introduceOrderRate");//引入订单转化率
				map.put("introduceOrderRate", introduceOrderRate);
				dataList.add(map);
			}
		}
	}
}
