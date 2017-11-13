package PG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import Utils.HtmlGenUtils;


/**HA Weekly Tracking ±¨±í*/
public class TrackingReport {
	public static void main(String[] args){
		LoginPG.login();
		String dateTime = HtmlGenUtils.getMonthTime("yyyyMM",-1);
		crawl(dateTime);
	}
	
	public static void crawl(String dateTime){
		try {
			List<Map<String,String>> list = LoginPG.getPlatformList();
			String url = "https://emedia.pg.com.cn/API/Report/GetTrackingReportDataRows";
			Map<String,List<Map<String,String>>> platformMap = new HashMap<String, List<Map<String,String>>>();
			for(Map<String,String> map : list){
				String platformId = map.keySet().iterator().next();
				String platform = map.get(platformId);
				List<Map<String,String>> mapList = platformMap.get(platform);
				if(mapList == null){
					mapList = new ArrayList<Map<String,String>>();
				}
				HttpPost post = new HttpPost(url);
				HttpEntity bodyEntity = getBodyEntity(platformId,dateTime);
				post.setEntity(bodyEntity);
				String htmlCode = LoginPG.getHtmlCode(url, bodyEntity);
				parseHtmlCode(htmlCode,mapList,dateTime);
				platformMap.put(platform, mapList);
			}
			PGDataUtils.saveTracking(platformMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parseHtmlCode(String htmlCode, List<Map<String, String>> mapList, String dateTime) {
		// TODO Auto-generated method stub
		try {
			JSONObject  json = JSONObject.fromObject(htmlCode);
			String dataStatus = json.getString("Status");
			if(dataStatus.equals("SUCCESS")){
				JSONArray array = json.getJSONArray("ContextData");
				for(int i = 0;i<=array.size();i++){
					if(i > 0 && i%4 == 0){
						try {
							String category = JSONObject.fromObject(array.get(i - 3)).getString("CategoryId");
							String channel = JSONObject.fromObject(array.get(i - 3)).getString("ChannelId");
							String position = JSONObject.fromObject(array.get(i - 3)).getString("PositionId");
							String spotType = JSONObject.fromObject(array.get(i - 3)).getString("SpotType");
							Map<String,String> map = null;
							for(int day = 1;day<=31;day++){
								String dataDay = "Day" + day;
								if(JSONObject.fromObject(array.get(i - 4)).containsKey(dataDay)){
									map = new HashMap<String, String>();
									String content = JSONObject.fromObject(array.get(i - 4)).getString(dataDay);
									if(content.indexOf("image:") != -1){
										content = "https://emedia.pg.com.cn/" + content.substring(content.indexOf(";") + 1, content.length());
									}else if(content.indexOf("text:") != -1){
										content = content.substring(content.indexOf("text:") + 6, content.length());
									}
									String CPC = JSONObject.fromObject(array.get(i - 3)).getString(dataDay);
									String CTR = JSONObject.fromObject(array.get(i - 2)).getString(dataDay);
									String sataus = "";
									if(JSONObject.fromObject(array.get(i - 1)).containsKey(dataDay)){
										String sta = JSONObject.fromObject(array.get(i - 1)).getString(dataDay);
										JSONObject statusJson = JSONObject.fromObject(sta);
										sataus = statusJson.getString("Status");
									}
									map.put("category", category);
									map.put("channel", channel);
									map.put("position", position);
									map.put("spotType", spotType);
									map.put("content", content);
									map.put("CPC", CPC);
									map.put("CTR", CTR);
									map.put("status", sataus);
									map.put("dataTime", dateTime + (day<10?"0" + day : day + ""));
									mapList.add(map);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpEntity getBodyEntity(String platformId, String dateTime) {
		JSONArray conditions = new JSONArray();
		JSONObject platformJson = new JSONObject();
		platformJson.put("ColumnName", "Platform");
		platformJson.put("ConditionType", "3");
		platformJson.put("RelationType", "1");
		platformJson.put("Value", platformId);
		
		JSONObject cateGoryJson = new JSONObject();
		cateGoryJson.put("ColumnName", "Query");
		cateGoryJson.put("ConditionType", "3");
		cateGoryJson.put("RelationType", "1");
		cateGoryJson.put("Value", "0");
		
		JSONObject monthStart = new JSONObject();
		monthStart.put("ColumnName", "PlanMonth");
		monthStart.put("ConditionType", "3");
		monthStart.put("RelationType", "1");
		monthStart.put("Value", dateTime);
		
		JSONObject clientType = new JSONObject();
		clientType.put("ColumnName", "ClientTypeId");
		clientType.put("ConditionType", "6");
		clientType.put("RelationType", "1");
		clientType.put("Value", "all");
		conditions.add(platformJson);
		conditions.add(cateGoryJson);
		conditions.add(monthStart);
		conditions.add(clientType);
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("conditions", conditions.toString(), ContentType.APPLICATION_JSON)
					.build();
		return entity;
	}
}
