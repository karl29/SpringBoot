package JD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.ExcelUtils;
import Utils.HtmlGenUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**jshop实时监控数据*/
public class JshopRealTimeData {
	public static void main(String[] args){
		crawlRealTime(ExcelUtils.getTaskList("Jshop"),HtmlGenUtils.getDataTime("yyyy-MM-dd", -1));
	}
	
	/**indexInfo Mobile*/
	public static void crawlIndexInfoMobile(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> indexInfoMOList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			String projectId = map.get("id").trim();
			if(map.get("terminal").equals("Mobile")){
				String indexUrl = "http://data-jshop.jd.com/mob/queryPageSegmentData?"
						+ "appId="+projectId+"&queryStartTime="+dataTime+"&queryEndTime=" + dataTime;
				String indexInfoMoData = JShopCrawl.getRealTimeHtmlCode(indexUrl);
				parseIndexInfoMOData(indexInfoMoData,indexInfoMOList,dataTime,map);
			}
		}
		if(indexInfoMOList.size() > 0){
			JshopDatabase.saveIndexInfoMo(indexInfoMOList);
		}
	}
	
	/**indexInfo PC*/
	public static void crawlIndexInfoPc(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> indexInfoPCList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			String projectId = map.get("id").trim();
			if(map.get("terminal").equals("PC")){
				String indexUrl = "http://data-jshop.jd.com/act/queryAppPageDetailData?"
						+ "appId="+projectId+"&queryStartTime="+dataTime+"&queryEndTime=" + dataTime;
				String indexInfoData = JShopCrawl.getRealTimeHtmlCode(indexUrl);
				parseIndexInfoPCData(indexInfoData,indexInfoPCList,dataTime,map);
			}
		}
		if(indexInfoPCList.size() > 0){
			JshopDatabase.saveIndexInfoPC(indexInfoPCList);
		}
	}
	/**实时数据product表数据*/
	public static void crawlProductData(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> productList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			String projectId = map.get("id").trim();
			String productUrl = "";
			String productInfo = "pcProductRankDisplay";
			if(map.get("terminal").equals("PC")){
				productUrl = "http://data-jshop.jd.com/pc/realTime/queryRealTimeProductInfo.html?"
						+ "appId="+projectId+"&statDate=" + dataTime;
			}else{
				productInfo = "mobProductRankDisplay";
				productUrl = "http://data-jshop.jd.com/monitor/queryRealTimeProductInfo.html?"
						+ "appId="+projectId+"&mobType=0&statDate=" + dataTime;
			}
			String prouctData = JShopCrawl.getRealTimeHtmlCode(productUrl);
			
			parseProductData(prouctData,productInfo,productList,map,dataTime);
		}
		
		if(productList.size() > 0){
			JshopDatabase.saveProductData(productList);
		}
	}
	public static void crawlRealTime(List<Map<String,String>> mapList,String dataTime){
		Map<String,List<Map<String,String>>> maps = new HashMap<String, List<Map<String,String>>>();
		for(Map<String,String> map : mapList){
			String projectId = map.get("id").trim();
			String url = "";
			String visitInfo = "pcPageVisitInfo";
			if(map.get("terminal").equals("PC")){
				url = "http://data-jshop.jd.com/pc/realTime/queryRealTimeVisitsInfo.html?"
						+ "appId="+projectId+"&statDate=" + dataTime;
			}else{
				visitInfo = "mobPageVisitInfo";
				url = "http://data-jshop.jd.com/monitor/queryRealTimeVisitsInfo.html?"
						+ "appId="+projectId+"&mobType=0&statDate=" + dataTime;
			}
			String htmlCode = JShopCrawl.getRealTimeHtmlCode(url);
			parseRealTimeData(htmlCode,visitInfo,maps,map,dataTime);
		}
		for(String key : maps.keySet()){
			List<Map<String,String>> keyList = maps.get(key);
			if(keyList.size() > 0){//birth level
				if(key.equals("sex")){
					JshopDatabase.saveRealTimeSex(keyList);
				}else if(key.equals("birth")){
					JshopDatabase.saveRealTimeBirth(keyList);
				}else{
					JshopDatabase.saveRealTimeLevel(keyList);
				}
			}
		}
	}
	

	/**
	 * 解析实时数据 访客、会员构成的json数据
	 * @param maps 
	 * @param dataTime 
	 * @param map 
	 * */
	private static void parseRealTimeData(String htmlCode, String visitInfo, Map<String, List<Map<String, String>>> maps, 
			Map<String, String> taskMap, String dataTime) {
		// TODO Auto-generated method stub
		try {
			if(!htmlCode.equals("")){
				String  terminal = JShopCrawl.getTerminal(visitInfo);
				JSONObject json = JSONObject.fromObject(htmlCode);
				JSONObject infoJson = json.getJSONObject(visitInfo);
				String category = taskMap.get("category");
				String brand = taskMap.get("brand");
				String projectId = taskMap.get("id");
				String projectName = taskMap.get("name");
				if(infoJson.containsKey("quorum") && !infoJson.getString("quorum").equals("0")){
					List<Map<String,String>> birthList = maps.get("birth");
					if(birthList == null){
						birthList = new ArrayList<Map<String,String>>();
					}
					List<Map<String,String>> sexList = maps.get("sex");
					if(sexList == null){
						sexList = new ArrayList<Map<String,String>>();
					}
					List<Map<String,String>> levelList = maps.get("level");
					if(levelList == null){
						levelList = new ArrayList<Map<String,String>>();
					}
					
					JSONArray birthdayList = infoJson.getJSONArray("birthdayInfoList");//年龄构成
					Map<String,String> birthdayMap = new HashMap<String, String>();
					birthdayMap.put("category", category);
					birthdayMap.put("brand", brand);
					birthdayMap.put("projectId", projectId);
					birthdayMap.put("projectName", projectName);
					birthdayMap.put("dataTime", dataTime);
					birthdayMap.put("60前", birthdayList.get(0)==null?"0": birthdayList.get(0).toString());
					birthdayMap.put("60后", birthdayList.get(1)==null?"0": birthdayList.get(1).toString());
					birthdayMap.put("70后", birthdayList.get(2)==null?"0": birthdayList.get(2).toString());
					birthdayMap.put("80后", birthdayList.get(3)==null?"0": birthdayList.get(3).toString());
					birthdayMap.put("90后", birthdayList.get(4)==null?"0": birthdayList.get(4).toString());
					birthdayMap.put("00后", birthdayList.get(5)==null?"0": birthdayList.get(5).toString());
					birthdayMap.put("terminal", terminal);
					birthList.add(birthdayMap);
					JSONObject sexListJson = infoJson.getJSONObject("userGenderMap");//访客构成
					Map<String,String> sexMap = new HashMap<String, String>();
					sexMap.put("category", category);
					sexMap.put("brand", brand);
					sexMap.put("projectId", projectId);
					sexMap.put("projectName", projectName);
					sexMap.put("dataTime", dataTime);
					sexMap.put("0", sexListJson.get("0").toString());
					sexMap.put("1", sexListJson.get("1").toString());
					sexMap.put("terminal", terminal);
					sexList.add(sexMap);
					JSONObject levelListJson = infoJson.getJSONObject("userLevelMap");//会员构成
					Map<String,String> levelMap = new HashMap<String, String>();
					levelMap.put("category", category);
					levelMap.put("brand", brand);
					levelMap.put("projectId", projectId);
					levelMap.put("projectName", projectName);
					levelMap.put("dataTime", dataTime);
					levelMap.put("注册会员", levelListJson.get("50").toString());
					levelMap.put("铜牌会员", levelListJson.get("56").toString());
					levelMap.put("银牌会员", levelListJson.get("61").toString());
					levelMap.put("金牌会员", levelListJson.get("62").toString());
					levelMap.put("企业用户", levelListJson.get("90").toString());
					levelMap.put("钻石会员", levelListJson.get("105").toString());
					levelMap.put("terminal", terminal);
					levelList.add(levelMap);
					System.out.println("============这是一条分割线===============");
					maps.put("birth", birthList);
					maps.put("sex", sexList);
					maps.put("level", levelList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**解析实时数据product数据
	 * @param dataTime 
	 * @param map2 */
	private static void parseProductData(String prouctData, String visitInfo,List<Map<String,String>> mapList, 
			Map<String, String> taskMap, String dataTime) {
		// TODO Auto-generated method stub
		try {
			if(!prouctData.equals("")){
				String  terminal = JShopCrawl.getTerminal(visitInfo);
				JSONArray json = JSONObject.fromObject(prouctData).getJSONArray(visitInfo);
				String category = taskMap.get("category");
				String brand = taskMap.get("brand");
				String projectId = taskMap.get("id");
				String projectName = taskMap.get("name");
				for(Object obj : json){
					Map<String,String> map = new HashMap<String, String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					map.put("category", category);
					map.put("brand", brand);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", dataTime);
					String skuId = itemJson.getString("skuId");
					map.put("skuId", skuId);
					String name = itemJson.getString("name");
					map.put("name", name);
					map.put("imgSrc", itemJson.getString("imgSrc"));
					String clickNum = itemJson.getString("clickNum");
					map.put("clickNum", clickNum);
					String directAmount = "-1";
					if(itemJson.containsKey("directAmount")){
						directAmount = itemJson.getString("directAmount");
					}
					map.put("directAmount", directAmount);
					String directProductNum = "-1";
					if(itemJson.containsKey("directProductNum")){
						directProductNum = itemJson.getString("directProductNum");
					}
					map.put("directProductNum", directProductNum);
					String directQtty = "-1";
					if(itemJson.containsKey("directQtty")){
						directQtty = itemJson.getString("directQtty");
					}
					map.put("directQtty", directQtty);
					map.put("terminal", terminal);
					mapList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**pc
	 * @param dataTime 
	 * @param map2 */
	private static void parseIndexInfoPCData(String indexInfoData,List<Map<String,String>> mapList,
			String dataTime, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		try {
			JSONObject dataJson = JSONObject.fromObject(indexInfoData).getJSONObject("data");
			JSONArray visitList = dataJson.getJSONArray("appVisits"); 
			JSONArray pvList = dataJson.getJSONArray("appPv"); 
			JSONArray uvList = dataJson.getJSONArray("appUv") ;
			JSONArray appSecOrderNum = dataJson.getJSONArray("appSecOrderNum");//间接下单量
			JSONArray appDirectOrderNum = dataJson.getJSONArray("appDirectOrderNum");//直接下单量
			JSONArray appSecOrderAccount = dataJson.getJSONArray("appSecOrderAccount");//间接订单金额
			JSONArray appDirectOrderAccount = dataJson.getJSONArray("appDirectOrderAccount");//直接下单金额
			JSONArray appSecOrderRate = dataJson.getJSONArray("appSecOrderRate");//间接下单率
			JSONArray appDirectOrderRate = dataJson.getJSONArray("appDirectOrderRate");//直接下单率
			String category = taskMap.get("category");
			String brand = taskMap.get("brand");
			String projectId = taskMap.get("id");
			String projectName = taskMap.get("name");
			for(int i = 0;i<24;i++){
				Map<String,String> map = new HashMap<String, String>();
				map.put("category", category);
				map.put("brand", brand);
				map.put("projectId", projectId);
				map.put("projectName", projectName);
				map.put("dataTime", dataTime);
				int index = i + 1;
				String hour = (i<10?"0" + i:i + ":00") + "~" + (index<10?"0" + index:index + ":00");
				map.put("hour", hour);
				String visits = visitList.get(i).toString();
				map.put("visits", visits);
				String pv = pvList.get(i).toString().trim();
				map.put("pv", pv);
				System.out.println(visits + "~~" + pv);
				String uv = uvList.get(i).toString().trim();
				map.put("uv", uv);
				String secNum = appSecOrderNum.get(i).toString().trim();
				map.put("secNum", secNum);
				String directNum = appDirectOrderNum.get(i).toString().trim();
				map.put("directNum", directNum);
				String secAccount = appSecOrderAccount.get(i).toString().trim();
				map.put("secAccount", secAccount);
				String directAccount = appDirectOrderAccount.get(i).toString().trim();
				map.put("directAccount", directAccount);
				String secRate = appSecOrderRate.get(i).toString().trim();
				map.put("secRate", secRate);
				String directRate = appDirectOrderRate.get(i).toString().trim();
				map.put("directRate", directRate);
				mapList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**moblie
	 * @param dataTime 
	 * @param taskMap */
	private static void parseIndexInfoMOData(String indexInfoMoData,
			List<Map<String, String>> mapList, String dataTime, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		try {
			JSONObject dataJson = JSONObject.fromObject(indexInfoMoData).getJSONObject("data");
			JSONArray visitList = dataJson.getJSONArray("visitList"); 
			JSONArray pvList = dataJson.getJSONArray("pvList"); 
			JSONArray uvList = dataJson.getJSONArray("uvList") ;
			JSONArray introduceOrdQttyList = dataJson.getJSONArray("introduceOrdQttyList");//引入订单量
			JSONArray introduceOrdAmountList = dataJson.getJSONArray("introduceOrdAmountList");//引入订单金额
			JSONArray introduceOrdRateList = dataJson.getJSONArray("introduceOrdRateList");//引入订单转化率
			String category = taskMap.get("category");
			String brand = taskMap.get("brand");
			String projectId = taskMap.get("id");
			String projectName = taskMap.get("name");
			for(int i = 0;i<24;i++){
				Map<String,String> map = new HashMap<String, String>();
				int index = i + 1;
				String hour = (i<10?"0" + i:i + ":00") + "~" + (index<10?"0" + index:index + ":00");
				map.put("dataTime", dataTime);
				map.put("category", category);
				map.put("brand", brand);
				map.put("projectId", projectId);
				map.put("projectName", projectName);
				map.put("hour", hour);
				map.put("visits", visitList.get(i).toString());
				map.put("pv", pvList.get(i).toString().trim());
				map.put("uv", uvList.get(i).toString().trim());
				System.out.println(pvList.get(i).toString().trim() + "~~" + uvList.get(i).toString().trim());
				String ordQtty = introduceOrdQttyList.get(i).toString().trim();
				map.put("ordQtty", ordQtty);
				String ordAmount = introduceOrdAmountList.get(i).toString().trim();
				map.put("ordAmount", ordAmount);
				String ordRate = introduceOrdRateList.get(i).toString().trim();
				map.put("ordRate", ordRate);
				mapList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
