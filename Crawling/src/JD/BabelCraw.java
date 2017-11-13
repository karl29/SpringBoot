package JD;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import database.jd.JdUtils;
import Utils.ExcelUtils;
import Utils.HtmlGenUtils;


/**
 * 
 * 京东通天塔数据爬取
 * */
public class BabelCraw {
	public static String cookie = "cn=0; sid=b123d63c34e245275ca9db636cdd77f3; mba_muid=15028658449681124666752; __jdv=122270672|direct|-|none|-|1508135070689; isRead=read; dateExpires=1508312916440; ipLocation=%u5317%u4EAC; areaId=1; ipLoc-djd=1-72-2839-0; _rdCube=%7B%22p1268571%22%3A%22%2C4050367%22%7D; VC_INTEGRATION_JSESSIONID=bb86fb0c-d94f-458e-9b34-b09cf103dc38; __jda=122270672.15028658449681124666752.1502865844.1508322982.1508382572.27; __jdb=122270672.1.15028658449681124666752|27.1508382572; __jdc=122270672; _jrda=3; _jrdb=1508382572513; wlfstk_smdl=gvetfmx68lcuuft5jr4n89phel2m7gao; 3AB9D23F7A4B3C9B=ZCB4RWNSQDOYUVNILXJA6QF7F5O6KT4YVR4BWKPHMCGYLOS77IVHSQ6A2GZE2EIFTBADAHJ6BB5UBLO6B5KI6HAGHU; __jdu=15028658449681124666752; TrackID=1h5Grflx0xAdmE3UDfLrqhw3D0KwJKdgCLM8sCQ6WM-0ymXXRM6ETWWF8j5EelPYy442T4hq0n5cplOWh_D19j79CyQuNPzZZdoJOB5OaR24; pinId=09V92KNyF3DS2GmjAM93PQ; pin=%E4%B8%80%E5%95%86%E7%8E%8B%E6%B5%B7%E9%AA%84; unick=SK-II%E5%AE%98%E6%96%B9%E6%97%97%E8%88%B0%E5%BA%97; thor=9208878B70F31034641FE3A8F5B9140ECF2602641F0220F785FFA1BF8C0CF4DC4744CD0F8172CAB2A67D4E10B56E1D0FBC5F5BA0BC80779A822FC5033A3FE3435B2C729B7D9FF848B76D4959DFB1F08348AF9A426CF37FD3ABA1A8DF6ACB55CDCCD93C7AB940D138C7AD9623FD492CD37DC9DD6F3346749FBEDDCB748E399F73; _tp=5eISx1ZDc1NOfw%2BFW7cdJzmvd6qJQ2NJuuxliL3i9gDoxlPEOZ4AW8sU%2Bf7Z7Rk2; logining=1; _pst=%E4%B8%80%E5%95%86%E7%8E%8B%E6%B5%B7%E9%AA%84; ceshi3.com=000";
	public static void main(String[] args) throws Exception{
		//crawFlow(ExcelUtils.getTaskList("通天塔"));
		crawStall(ExcelUtils.getTaskList("通天塔"));
	}
	
	/**获取坑位数据*/
	private static Map<String,List<Map<String,String>>> getFloorAndStall(String sourceUrl, String dataTime,
			String activityId, String activityName, String encryId) {
		// TODO Auto-generated method stub
		Map<String,List<Map<String,String>>> groupMap = null;
		try {
			String url = "http://babelams.jd.com/service/getFloorAndStall";
			String clientType = "ANDROID,ANDROID-M,IOS,IOS-M,IPAD,IPAD-M,M-M,WEIXIN-M,OTHER,WQ";
			String result = getPageResult(sourceUrl,url,activityId,clientType,dataTime,null,encryId);
			if(result != null && !"".equals(result)){
				groupMap = new HashMap<String, List<Map<String,String>>>();
				JSONObject json = JSONObject.fromObject(result);
				JSONArray returnList = json.getJSONArray("returnList");
				System.out.println(returnList);
				for(Object obj : returnList){
					JSONObject dataJson = JSONObject.fromObject(obj);
					String floorId = dataJson.getString("floorId");
					String pageTown = dataJson.getString("ft");
					JSONArray groups = dataJson.getJSONArray("groups");
					String template = dataJson.getString("template");
					String templateName = dataJson.getString("templateName");
					List<Map<String,String>> list = groupMap.get(pageTown);
					if(list == null){
						list = new ArrayList<Map<String,String>>();
					}
					for(Object groupObj : groups){//组别
						JSONObject groupJson = JSONObject.fromObject(groupObj);
						String groupDate = groupJson.getString("date");
						String groupId = groupJson.getString("groupId");
						String groupName = groupJson.getString("groupName");
						String groupStr = groupJson.getString("groupStr");
						JSONArray stalls = groupJson.getJSONArray("stalls");//坑位
						for(Object stallsObj : stalls){
							Map<String,String> stallMap = new HashMap<String,String>();
							JSONObject stallsJson = JSONObject.fromObject(stallsObj);
							String stallsDate = stallsJson.getString("date");
							String imgUrl = stallsJson.getString("imgUrl");
							String sc = stallsJson.getString("sc");
							String subId = stallsJson.getString("subId");
							String itemUrl = stallsJson.getString("url");
							String stallId = stallsJson.getString("stallId");
							stallMap.put("stallsDate", stallsDate);
							stallMap.put("imgUrl", imgUrl);
							stallMap.put("sc", sc);
							stallMap.put("subId", subId);
							stallMap.put("itemUrl", itemUrl);
							stallMap.put("groupDate", groupDate);
							stallMap.put("groupId", groupId);
							stallMap.put("groupName", groupName);
							stallMap.put("floorId", floorId);
							stallMap.put("stallId", stallId);
							stallMap.put("template", template);
							stallMap.put("templateName", templateName);
							stallMap.put("groupStr", groupStr);
							list.add(stallMap);
						}
					}
					groupMap.put(pageTown, list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(groupMap.toString());
		return groupMap;
	}

	/**
	 * 爬取页面楼层分时段json数据
	 * @param encryId 
	 * @param floorMap 
	 * */
	private static List<Map<String, String>> getHourData(String sourceUrl, String dataTime,
			String activityId, String activityName, String encryId, Map<String, Map<String, String>> floorMap) {
		// TODO Auto-generated method stub
		String url = "http://babelams.jd.com/service/getDetailData";
		Map<String,String> pageMap = getPageType();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for(String type : pageMap.keySet()){
			String typeName = pageMap.get(type);
			for(String foorName : floorMap.keySet()){
				System.out.println("当前页面类型：" + type + "==当前楼层：" + foorName);
				Map<String,String> floorNameMap = floorMap.get(foorName);
				String result = getPageResult(sourceUrl,url,activityId,type,dataTime,floorNameMap,encryId);
				getTimeSlotData(result,activityId,activityName,typeName,dataTime,foorName,mapList);
			}
		}
		return mapList;
	}
	
	/**
	 * 爬取历史数据uv pv数据
	 * */
	private static Collection<? extends Map<String, String>> getPUVData(
			String sourceUrl, String dataTime, String activityId,
			String activityName, String encryId) {
		// TODO Auto-generated method stub
		String url = "http://babelams.jd.com/service/getTotalData2";
		Map<String,String> pageMap = getPageType();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for(String type : pageMap.keySet()){
			String typeName = pageMap.get(type);
			System.out.println("楼层数据当前页面类型：" + type);
			String result = getPageResult(sourceUrl,url,activityId,type,dataTime,null,encryId);
			getTimeSlotData(result,activityId,activityName,typeName,dataTime,"页面内容汇总",mapList);
		}
		return mapList;
	}
	/**
	 * 爬取页面楼层json数据
	 * @param encryId 
	 * @param activityName2 
	 * */
	private static List<Map<String, String>> getFloorData(String sourceUrl,String dataTime,
			String activityId, String activityName, String encryId) {
		// TODO Auto-generated method stub
		String url = "http://babelams.jd.com/service/getFloorTotalData";
		Map<String,String> pageMap = getPageType();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for(String type : pageMap.keySet()){
			String typeName = pageMap.get(type);
			System.out.println("楼层数据当前页面类型：" + type);
			String result = getPageResult(sourceUrl,url,activityId,type,dataTime,null,encryId);
			getFloorData(result,activityId,activityName,typeName,dataTime,mapList);
		}
		return mapList;
	}
	
	/**坑位数据
	 * @param floorMap */
	private static Collection<? extends Map<String, String>> getStallData(
			String sourceUrl, String dataTime, String activityId,
			String activityName, String encryId,
			Map<String, List<Map<String, String>>> stallsMap, Map<String, Map<String, String>> floorMap) {
		String url = "http://babelams.jd.com/service/getDetailData";
		Map<String,String> pageMap = getPageType();//页面
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for(String type : pageMap.keySet()){
			String typeName = pageMap.get(type);
			for(String foorName : floorMap.keySet()){//楼层
				List<Map<String,String>> list = stallsMap.get(foorName);
				for(Map<String,String> groupMap : list){//组别
					String result = getPageResult(sourceUrl,url,activityId,type,dataTime,groupMap,encryId);
					getStallData(result,activityId,activityName,typeName,dataTime,foorName,mapList,groupMap);
				}
			}
		}
		return mapList;
	}
	
	/**坑位数据*/
	private static void getStallData(String result, String activityId,
			String activityName, String typeName, String dataTime,
			String foorName, List<Map<String, String>> mapList,
			Map<String, String> groupMap) {
		// TODO Auto-generated method stub
		if(!"".equals(result)){
			JSONObject json = JSONObject.fromObject(result);
			JSONArray returnList = json.getJSONArray("returnList");
			if(returnList.size() >= 1){
				Map<String,String> map = new HashMap<String,String>();
				map.put("activityId", activityId);
				map.put("activityName", activityName);
				map.put("pageType", typeName);
				map.put("dataTime", dataTime);
				map.put("pageTown", foorName);
				Object obj = returnList.get(0);
				JSONObject floorJson = JSONObject.fromObject(obj);
				String clickCount = floorJson.getString("click");
				String clickUv = floorJson.getString("clickUv");
				String intorducPens = floorJson.getString("orderCount");
				String intorducAmount = floorJson.getString("orderAmount");
				String directOrderCount = floorJson.getString("directOrderCount");
				String directOrderAmount = floorJson.getString("directOrderAmount");
				String indirectOrderCount = floorJson.getString("indirectOrderCount");
				String indirectOrderAmount = floorJson.getString("indirectOrderAmount");
				String uv = floorJson.getString("uv");
				String pv = floorJson.getString("pv");
				map.put("clickCount", clickCount);
				map.put("clickUv", clickUv);
				map.put("intorducPens", intorducPens);
				map.put("intorducAmount", intorducAmount);
				map.put("directOrderCount", directOrderCount);
				map.put("directOrderAmount", directOrderAmount);
				map.put("indirectOrderCount", indirectOrderCount);
				map.put("indirectOrderAmount", indirectOrderAmount);
				map.put("uv", uv);
				map.put("pv", pv);
				map.put("url", groupMap.get("itemUrl"));
				map.put("imgUrl", groupMap.get("imgUrl"));
				map.put("stallName", groupMap.get("sc"));
				map.put("stallId", groupMap.get("stallId"));
				map.put("groupName", groupMap.get("groupName"));
				map.put("groupStr", groupMap.get("groupStr"));
				map.put("groupId", groupMap.get("groupId"));
				map.put("floorId", groupMap.get("floorId"));
				map.put("sc", groupMap.get("sc"));
				map.put("subId", groupMap.get("subId"));
				mapList.add(map);
			}
		}
	}

	
	/**
	 * 流量分析页面
	 * @param activityId 
	 * @param activityName 
	 * @param encryId 
	 * @return 
	 * */
	private static List<Map<String, String>> getData(String sourceUrl,String dataTime,
			String activityId, String activityName, String encryId) throws Exception{
		// TODO Auto-generated method stub 
		String url = "http://babelams.jd.com/service/getDataFlow";
		Map<String,String> pageMap = getPageType();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for(String type : pageMap.keySet()){
			String typeName = pageMap.get(type);
			System.out.println("流量分析数据当前页面类型：" + type);
			String result = getPageResult(sourceUrl,url,activityId,type,dataTime,null,encryId);
			Thread.sleep((new Random().nextInt(5) + 5) * 100);
			getFlowData(result,activityId,activityName,typeName,dataTime,mapList);
		}
		
		return mapList;
	}
	
	/**
	 * 解析页面获取结果
	 * @param floorNameMap 
	 * @param encryId 
	 * */
	public static String getPageResult(String sourceUrl,String url,
			String activityId, String type,String dataTime, Map<String, String> floorNameMap, String encryId){
		String result = "";
		for(int i = 0;i<3;i++){
			try {
				String jsonText = getBodyJson(activityId,dataTime,dataTime,type,floorNameMap,encryId);
				CloseableHttpClient client = HttpClients.createDefault();
				HttpPost post = new HttpPost(url);
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addTextBody("body", jsonText, ContentType.APPLICATION_JSON);
				//System.out.println(jsonText);
				post.setEntity(builder.build());
				post.setHeader("cookie", cookie);
				post.setHeader("Host", "babelams.jd.com");
				post.setHeader("Origin","http://babel.m.jd.com");
				post.setHeader("Referer",sourceUrl);
				post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				CloseableHttpResponse response = client.execute(post);
				result = EntityUtils.toString(response.getEntity());
				System.out.println(result);
				if(!result.equals("")){
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
	
	/**
	 * 获取页面分时数据
	 * @param foorName 
	 * @param dataTime 
	 * @param type 
	 * @param activityName 
	 * @param map 
	 * @param mapList 
	 * */
	private static void getTimeSlotData(String result, String activityId, String activityName,
			String type, String dataTime, String foorName, List<Map<String, String>> mapList){
		if(!"".equals(result)){
			JSONObject json = JSONObject.fromObject(result);
			JSONArray returnList = json.getJSONArray("returnList");
			
			for(int i = 0;i<returnList.size();i++){
				Map<String,String> map = new HashMap<String,String>();
				map.put("activityId", activityId);
				map.put("activityName", activityName);
				map.put("pageType", type);
				map.put("dataTime", dataTime);
				map.put("pageTown", foorName);
				Object obj = returnList.get(i);
				JSONObject floorJson = JSONObject.fromObject(obj);
				String clickCount = floorJson.getString("click");
				String clickUv = floorJson.getString("clickUv");
				String intorducPens = floorJson.getString("orderCount");
				String intorducAmount = floorJson.getString("orderAmount");
				String directOrderCount = floorJson.getString("directOrderCount");
				String directOrderAmount = floorJson.getString("directOrderAmount");
				String indirectOrderCount = floorJson.getString("indirectOrderCount");
				String indirectOrderAmount = floorJson.getString("indirectOrderAmount");
				String uv = floorJson.getString("uv");
				String pv = floorJson.getString("pv");
				String hour = "";
				if(i == 0){
					hour = "-1";
				}else{
					hour = floorJson.getString("hour");
				}
				map.put("clickCount", clickCount);
				map.put("clickUv", clickUv);
				map.put("intorducPens", intorducPens);
				map.put("intorducAmount", intorducAmount);
				map.put("directOrderCount", directOrderCount);
				map.put("directOrderAmount", directOrderAmount);
				map.put("indirectOrderCount", indirectOrderCount);
				map.put("indirectOrderAmount", indirectOrderAmount);
				map.put("uv", uv);
				map.put("pv", pv);
				map.put("hour", hour);
				mapList.add(map);
			}
		}
	}
	
	/**
	 * 获取页面分楼层数据
	 * @param mapList 
	 * @param dataTime 
	 * @param type 
	 * @param activityName 
	 * @param pageTown 
	 * @param map 
	 * */
	private static void getFloorData(String result, String activityId, String activityName, 
			String type, String dataTime, List<Map<String, String>> mapList){
		if(!"".equals(result)){
			JSONObject json = JSONObject.fromObject(result);
			JSONArray returnList = json.getJSONArray("returnList");
			for(Object obj : returnList){
				Map<String,String> map = new HashMap<String,String>();
				map.put("activityId", activityId);
				map.put("activityName", activityName);
				map.put("pageType", type);
				map.put("dataTime", dataTime);
				JSONObject floorJson = JSONObject.fromObject(obj);
				String pageTown = floorJson.getString("floorDes");
				String clickCount = floorJson.getString("click");
				String clickUv = floorJson.getString("clickUv");
				String intorducPens = floorJson.getString("orderCount");
				String intorducAmount = floorJson.getString("orderAmount");
				String directOrderCount = floorJson.getString("directOrderCount");
				String directOrderAmount = floorJson.getString("directOrderAmount");
				String indirectOrderCount = floorJson.getString("indirectOrderCount");
				String indirectOrderAmount = floorJson.getString("indirectOrderAmount");
				String uv = floorJson.getString("uv");
				String pv = floorJson.getString("pv");
				map.put("pageTown", pageTown);
				map.put("clickCount", clickCount);
				map.put("clickUv", clickUv);
				map.put("intorducPens", intorducPens);
				map.put("intorducAmount", intorducAmount);
				map.put("directOrderCount", directOrderCount);
				map.put("directOrderAmount", directOrderAmount);
				map.put("indirectOrderCount", indirectOrderCount);
				map.put("indirectOrderAmount", indirectOrderAmount);
				map.put("uv", uv);
				map.put("pv", pv);
				mapList.add(map);
			}
		}
	}
	/**
	 * 获取流量分析数据
	 * @param dataTime 
	 * @param type 
	 * @param activityName 
	 * @param mapList 
	 * @param map 
	 * */
	private static void getFlowData(String result, String activityId, 
			String activityName, String type, String dataTime, List<Map<String, String>> mapList) {
		if(!"".equals(result)){
			JSONObject json = JSONObject.fromObject(result);
			if(json.optJSONArray("returnList") != null){
				JSONArray returnList = json.getJSONArray("returnList");
				for(Object obj : returnList){
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityId", activityId);
					map.put("activityName", activityName);
					map.put("pageType", type);
					map.put("dataTime", dataTime);
					JSONObject flowJson = JSONObject.fromObject(obj);
					String referPage = flowJson.getString("referPage");
					String pv = flowJson.getString("pv");
					String uv = flowJson.getString("uv");
					map.put("referPage", referPage);
					map.put("pv", pv);
					map.put("uv", uv);
					mapList.add(map);
				}
			}
		}
	}
	/**
	 * 数据请求json参数
	 * @param floorNameMap 
	 * @param encryId 
	 * */
	private static String getBodyJson(String activityId,String beginDate,String endDate,
			String clientType, Map<String, String> floorNameMap, String encryId){
		JSONObject json = new JSONObject();
		try {
			json.put("activityId", activityId);
			json.put("beginDate", beginDate);
			json.put("endDate", endDate);
			json.put("clientType", clientType);
			json.put("dataType", "1");
			json.put("encryActivityId", encryId);
			if(floorNameMap != null){
				json.put("floorId", floorNameMap.get("floorId"));
				json.put("template", floorNameMap.get("template"));
				json.put("templateName", floorNameMap.get("templateName"));
				json.put("groupId", floorNameMap.get("groupId"));
				json.put("groupName", floorNameMap.get("groupName"));
				json.put("stallId", floorNameMap.get("stallId"));
				json.put("subId", floorNameMap.get("subId"));
			}
			json.put("extnet", "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(json.toString());
		return json.toString();
	}
	
	/**
	 * 页面类型
	 * */
	private static Map<String,String> getPageType(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("ANDROID", "Android页面");
		map.put("ANDROID-M", "Android-M页面");
		map.put("IOS", "iPhone页面");
		map.put("IOS-M", "iPhone-M页面");
		map.put("IPAD", "iPad-页面");
		map.put("IPAD-M", "iPad-M页面");
		map.put("M-M", "M-M页面");
		map.put("WEIXIN-M", "微信-M页面");
		map.put("OTHER", "其他类型页面");
		map.put("WQ", "WQ页面");
		map.put("ANDROID,ANDROID-M,IOS,IOS-M,IPAD,IPAD-M,M-M,WEIXIN-M,OTHER,WQ", "所有页面类型");
		return map;
	}
	
	/**
	 * 获取活动有数据的楼层
	 * */
	private static Map<String, Map<String, String>> getFloorTypeMap(
			String sourceUrl, String dataTime, String activityId,
			String activityName, String encryId) {
		String url = "http://babelams.jd.com/service/getFloorTotalData";
		Map<String, Map<String, String>> mapList = new HashMap<String, Map<String,String>>();
		String result = getPageResult(sourceUrl,url,activityId,"ANDROID,ANDROID-M,IOS,IOS-M,IPAD,IPAD-M,M-M,WEIXIN-M,OTHER,WQ",dataTime,null,encryId);
		if(result != null && !"".equals(result)){
			JSONObject json = JSONObject.fromObject(result);
			JSONArray returnList = json.getJSONArray("returnList");
			for(Object obj : returnList){
				JSONObject dataJson = JSONObject.fromObject(obj);
				String floorDes = dataJson.getString("floorDes");
				Map<String,String> map = mapList.get(floorDes);
				if(map == null){
					map = new HashMap<String, String>();
				}
				String floorId = dataJson.getString("floorId");
				String template = dataJson.getString("template");
				String templateName = dataJson.getString("templateName");
				map.put("floorId", floorId);
				map.put("template", template);
				map.put("templateName", templateName);
				mapList.put(floorDes, map);
			}
		}
		return mapList;
	}
	
	/**分时数据开始爬取接口*/
	public static void crawHour(List<Map<String, String>> mapList) {
		List<Map<String,String>> totalHourList = new ArrayList<Map<String,String>>();
		String dataTime = getDateTime(-1);
		for(Map<String,String> map : mapList){
			try {
				String activityId = map.get("id");
				String activityName = map.get("name");
				String encryId = map.get("encryId");
				String sourceUrl = "http://babel.m.jd.com/active/operation/module/data3/data.html?"
						+ "activityId="+activityId+"&&encryActivityId="+encryId+"activityName=" + URLEncoder.encode(activityName, "utf-8");
				
				executeDataPage(sourceUrl);
				
				Map<String,Map<String,String>> floorMap = getFloorTypeMap(sourceUrl,dataTime,activityId,activityName,encryId);
				//页面分楼层分时数据
				totalHourList.addAll(getHourData(sourceUrl,dataTime,activityId,activityName,encryId,floorMap));
				//页面分时PVUV数据
				totalHourList.addAll(getPUVData(sourceUrl,dataTime,activityId,activityName,encryId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JdUtils.saveHourDate(totalHourList);
	}
	
	/**流量数据开始爬取接口*/
	public static void crawFlow(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		List<Map<String,String>> totalFlowList = new ArrayList<Map<String,String>>();
		String dataTime = getDateTime(-1);
		for(Map<String,String> map : mapList){
			try {
				String activityId = map.get("id");
				String activityName = map.get("name");
				String encryId = map.get("encryId");
				String sourceUrl = "http://babel.m.jd.com/active/operation/module/data3/data.html?"
						+ "activityId="+activityId+"&&encryActivityId="+encryId+"activityName=" + URLEncoder.encode(activityName, "utf-8");
				
				executeDataPage(sourceUrl);
				//流量分析数据
				totalFlowList.addAll(getData(sourceUrl,dataTime,activityId,activityName,encryId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JdUtils.saveDate(totalFlowList);
	}
	
	/**楼层数据开始爬取接口*/
	public static void crawFloor(List<Map<String, String>> mapList) {
		List<Map<String,String>> totalFloorList = new ArrayList<Map<String,String>>();
		String dataTime = getDateTime(-1);
		for(Map<String,String> map : mapList){
			try {
				String activityId = map.get("id");
				String activityName = map.get("name");
				String encryId = map.get("encryId");
				String sourceUrl = "http://babel.m.jd.com/active/operation/module/data3/data.html?"
						+ "activityId="+activityId+"&&encryActivityId="+encryId+"activityName=" + URLEncoder.encode(activityName, "utf-8");
				
				executeDataPage(sourceUrl);
				//页面分楼层数据
				totalFloorList.addAll(getFloorData(sourceUrl,dataTime,activityId,activityName,encryId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JdUtils.saveFloorDate(totalFloorList);
	}
	
	/**坑位数据开始爬取接口*/
	public static void crawStall(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		List<Map<String,String>> totalStallList = new ArrayList<Map<String,String>>();
		String dataTime = getDateTime(-1);
		for(Map<String,String> map : mapList){
			try {
				String activityId = map.get("id");
				String activityName = map.get("name");
				String encryId = map.get("encryId");
				String sourceUrl = "http://babel.m.jd.com/active/operation/module/data3/data.html?"
						+ "activityId="+activityId+"&&encryActivityId="+encryId+"activityName=" + URLEncoder.encode(activityName, "utf-8");
				
				executeDataPage(sourceUrl);
				
				Map<String,Map<String,String>> floorMap = getFloorTypeMap(sourceUrl,dataTime,activityId,activityName,encryId);
				Map<String,List<Map<String,String>>> stallsMap = getFloorAndStall(sourceUrl,dataTime,activityId,activityName,encryId);
				
				totalStallList.addAll(getStallData(sourceUrl,dataTime,activityId,activityName,encryId,stallsMap,floorMap));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JdUtils.saveStallDate(totalStallList);
	}
	
	/**每个活动的链接执行一次*/
	private static void executeDataPage(String sourceUrl) {
		try {
			synchronized (sourceUrl) {
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(sourceUrl);
				get.setHeader("cookie", cookie);
				get.setHeader("Host", "babel.m.jd.com");
				get.setHeader("Referer","http://babel.m.jd.com/active/operation/module/data3/list.html");
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				client.execute(get);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getDateTime(int day){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, day);
		System.out.println(format.format(cal.getTime()));
		return format.format(cal.getTime());
	}
}
