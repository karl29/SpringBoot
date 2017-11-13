package PG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import Utils.HtmlGenUtils;

/**spot summary报表数据查询*/
public class PGeMedia {
	public static void main(String[] args){
		LoginPG.login();
		String dateTime = HtmlGenUtils.getMonthTime("yyyyMM",-1);
		crawl(dateTime);
	}
	
	public static void crawl(String dateTime){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		//获取平台列表
		List<Map<String,String>> list = LoginPG.getPlatformList();
		String url = "https://emedia.pg.com.cn/API/SpotPlan/GetCount";
		for(Map<String,String> platformMap : list){
			String platformId = platformMap.keySet().iterator().next();
			String platform = platformMap.get(platformId);
			try {
				HttpEntity bodyEntity = getBodyEntity(platformId,dateTime);
				String result = LoginPG.getHtmlCode(url,bodyEntity);
				if(!result.equals("")){
					JSONObject json = JSONObject.fromObject(result);
					JSONArray contextData = json.getJSONArray("ContextData");
					Map<String,String> map = null;
					for(Object obj : contextData){
						map = new HashMap<String, String>();
						map.put("platform", platform);
						map.put("platformId", platformId);
						map.put("dataTime", dateTime);
						JSONObject data = JSONObject.fromObject(obj);
						String category = data.getString("Category");
						map.put("category", category);
						String budget = data.getString("Budget");
						map.put("budget", budget);
						String budgetMb = data.getString("BudgetMobile");
						map.put("budgetMb", budgetMb);
						String budgetPc = data.getString("BudgetPC");
						map.put("budgetPc", budgetPc);
						String pccount = data.getString("PCCount");
						map.put("pccount", pccount);
						String pCCountWeekend = data.getString("PCCountWeekend");
						map.put("pCCountWeekend", pCCountWeekend);
						String pCCountWeekday = data.getString("PCCountWeekday");
						map.put("pCCountWeekday", pCCountWeekday);
						String mbcount = data.getString("MBCount");
						map.put("mbcount", mbcount);
						String MBCountWeekend = data.getString("MBCountWeekend");
						map.put("MBCountWeekend", MBCountWeekend);
						String MBCountWeekday = data.getString("MBCountWeekday");
						map.put("MBCountWeekday", MBCountWeekday);
						String TongLan = data.getString("TongLan");
						map.put("TongLan", TongLan);
						String TongLanWeekend = data.getString("TongLanWeekend");
						map.put("TongLanWeekend", TongLanWeekend);
						String TongLanWeekday = data.getString("TongLanWeekday");
						map.put("TongLanWeekday", TongLanWeekday);
						String PCOnline = data.getString("PCOnline");
						map.put("PCOnline", PCOnline);
						String MBOnline = data.getString("MBOnline");
						map.put("MBOnline", MBOnline);
						System.out.println(category + "~~" + budget + "~~" + budgetMb + "~~" + budgetPc);
						mapList.add(map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<Map<String,Map<String,String>>> viewList = new ArrayList<Map<String,Map<String,String>>>();
		for(Map<String,String> map : mapList){
			String cateGory = map.get("category");
			String platform = map.get("platform");
			String platformId = map.get("platformId");
			String categoryId = getIdByCode(cateGory);
			crawlViewForSearch(categoryId,platform,platformId,viewList,dateTime);
		}
		
		PGDataUtils.saveMain(mapList);
		PGDataUtils.saveViewList(viewList);
	}
	
	
	/**根据品类获取id*/
	private static String getIdByCode(String cateGory) {
		// TODO Auto-generated method stub
		String categoryId = "";
		try {
			String url = "https://emedia.pg.com.cn/API/Category/GetIdByCode";
			HttpEntity entity = MultipartEntityBuilder.create()
					.addTextBody("code", cateGory, ContentType.TEXT_PLAIN)
						.build();
			String htmlCode = LoginPG.getHtmlCode(url, entity);
			if(!htmlCode.equals("")){
				JSONObject json = JSONObject.fromObject(htmlCode);
				categoryId = json.getString("ContextData");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return categoryId;
	}

	/**Spot Summary
	 * @param categoryId 
	 * @param platformId 
	 * @param dateTime 
	 * @param platform2 */
	public static void crawlViewForSearch(String categoryId, String platform,
			String platformId, List<Map<String,Map<String,String>>> mapList, String dateTime){
		String url = "https://emedia.pg.com.cn/API/SpotPlan/GetListViewForSearch";
		try {
			HttpEntity bodyEntity = getListViewBodyEntity(platformId,categoryId,dateTime);
			String result = LoginPG.getHtmlCode(url,bodyEntity);
			if(!result.equals("")){
				JSONObject json = JSONObject.fromObject(result);
				JSONArray dataArray = json.getJSONArray("ContextData");
				Map<String,Map<String,String>> map = null;
				for(Object obj : dataArray){
					JSONObject viewJson = JSONObject.fromObject(obj);
					map = new HashMap<String, Map<String,String>>();
					String id = viewJson.getString("Id");
					String channel = viewJson.getString("Channel");
					String category  = "";
					String adposition  = viewJson.getString("Position");
					for(int i = 1;i<=31;i++){
						String day = "";
						if(i < 10){
							day = "0" + i;
						}else{
							day = i + "";
						}
						if(viewJson.containsKey("Day" + day)){
							Map<String,String> dayMap = map.get(day);
							if(dayMap == null){
								dayMap = new HashMap<String, String>();
							}
							String clickCout = viewJson.getString("Day" + day);
							dayMap.put("id", id);
							dayMap.put("platform", platform);
							dayMap.put("channel", channel);
							dayMap.put("category", category);
							dayMap.put("adposition", adposition);
							dayMap.put("clickCout", clickCout);
							dayMap.put("dateTime", dateTime);
							System.out.println(channel + "~" +  category + "~" + day + "~~" + clickCout);
							map.put(day, dayMap);
						}
					}
					mapList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * spot summary详细报表链接form表单参数
	 * @param dateTime 
	 * @param dateTime */
	private static HttpEntity getListViewBodyEntity(String platfomrId, String categoryId, String dateTime) {
		JSONArray conditions = new JSONArray();
		JSONObject platformJson = new JSONObject();
		platformJson.put("ColumnName", "PlatformId");
		platformJson.put("ConditionType", "3");
		platformJson.put("RelationType", "1");
		platformJson.put("Value", platfomrId);
		
		JSONObject cateGoryJson = new JSONObject();
		cateGoryJson.put("ColumnName", "CategoryId");
		cateGoryJson.put("ConditionType", "3");
		cateGoryJson.put("RelationType", "1");
		cateGoryJson.put("Value", categoryId);
		
		JSONObject monthStart = new JSONObject();
		monthStart.put("ColumnName", "PlanMonth");
		monthStart.put("ConditionType", "2");
		monthStart.put("RelationType", "1");
		monthStart.put("Value", dateTime);
		JSONObject monthEnd = new JSONObject();
		monthEnd.put("ColumnName", "PlanMonth");
		monthEnd.put("ConditionType", "6");
		monthEnd.put("RelationType", "1");
		monthEnd.put("Value", dateTime);
		conditions.add(platformJson);
		conditions.add(cateGoryJson);
		conditions.add(monthStart);
		conditions.add(monthEnd);
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("conditions", conditions.toString(), ContentType.APPLICATION_JSON)
					.build();
		return entity;
	}

	/**拼接form表单数据
	 * @param platformId 
	 * @param dateTime */
	private static HttpEntity getBodyEntity(String platformId, String dateTime) {
		JSONArray conditions = new JSONArray();
		JSONObject platformJson = new JSONObject();
		platformJson.put("ColumnName", "Platform");
		platformJson.put("ConditionType", "3");
		platformJson.put("RelationType", "1");
		platformJson.put("Value", platformId);
		
		JSONObject monthStart = new JSONObject();
		monthStart.put("ColumnName", "MonthStart");
		monthStart.put("ConditionType", "3");
		monthStart.put("RelationType", "1");
		monthStart.put("Value", dateTime);
		JSONObject monthEnd = new JSONObject();
		monthEnd.put("ColumnName", "MonthEnd");
		monthEnd.put("ConditionType", "3");
		monthEnd.put("RelationType", "1");
		monthEnd.put("Value", dateTime);
		conditions.add(platformJson);
		conditions.add(monthStart);
		conditions.add(monthEnd);
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("conditions", conditions.toString(), ContentType.APPLICATION_JSON)
					.build();
		return entity;
	}
}
