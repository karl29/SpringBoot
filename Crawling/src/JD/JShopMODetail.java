package JD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.ExcelUtils;
import Utils.HtmlGenUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * JShop mobile端数据爬取类
 * */
public class JShopMODetail {
	public static void main(String[] args){
		//MaochaoJob.loginJshop();//登陆平台
		for(int i = 3;i>=1;i--){
			crawlGoDetail(ExcelUtils.getTaskList("Jshop"),HtmlGenUtils.getDataTime("yyyy-MM-dd", -i));
		}
	}
	
	/**报表详情去向数据*/
	public static void crawlGoDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/mob/detail?appId=" + projectId + "&mobType=0";
				if(map.get("terminal").equals("Mobile")){
					boolean hasPage = true;
					while(hasPage){
						String goDetailUrl = "http://data-jshop.jd.com/mob/detail/queryMobGoDetail"
								+ "?callback=jQuery111106607306304659231_1503986696714"
								+ "&appId="+projectId+"&mobType=0&queryStartTime="+dataTime+"&queryEndTime=" + dataTime
								+ "&index="+index+"&pageSize=12&_=" + System.currentTimeMillis();
						String goDetailCode = JShopCrawl.getDetailHtmlCode(goDetailUrl,refererUrl);
						hasPage = parseMOToDetail(goDetailCode,dataList,index,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.saveGoDetailMo(dataList);
		}
	}
	/**报表详情来源数据*/
	public static void crawlComeDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/mob/detail?appId=" + projectId + "&mobType=0";
				if(map.get("terminal").equals("Mobile")){
					boolean hasPage = true;
					while(hasPage){
						String comeDetailUrl = "http://data-jshop.jd.com/mob/detail/queryMobComeDetail"
								+ "?callback=jQuery111107360040955483194_1503628976622"
								+ "&appId="+projectId+"&mobType=0&queryStartTime="+dataTime+"&queryEndTime=" + dataTime
								+ "&index="+index+"&pageSize=12&_=" + System.currentTimeMillis();
						String comeDetailCode = JShopCrawl.getDetailHtmlCode(comeDetailUrl,refererUrl);
						hasPage = parseMOComeDetail(comeDetailCode,dataList,index,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.saveComeDetailMo(dataList);
		}
	}
	/**报表详情页面数据*/
	public static void crawlPageDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				System.out.println(projectId + "~~" + map.get("projectName"));
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/mob/detail?appId=" + projectId + "&mobType=0";
				if(map.get("terminal").equals("Mobile")){
					boolean hasPage = true;
					while(hasPage){
						String pageDetailUrl = "http://data-jshop.jd.com/mob/detail/queryMobSummaryDetail"
								+ "?callback=jQuery111107360040955483194_1503628976622"
								+ "&appId="+projectId+"&mobType=0&queryStartTime="+dataTime+"&queryEndTime=" + dataTime
								+ "&index="+index+"&pageSize=12&_=" + System.currentTimeMillis();
						String pageDetailCode = JShopCrawl.getDetailHtmlCode(pageDetailUrl,refererUrl);
						hasPage = parseMODetailCode(pageDetailCode,dataList,index,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.savePageMo(dataList);
		}
	}
	

	/**
	 * @param dataList 
	 * @param map2 */
	private static boolean parseMODetailCode(String pageDetailCode, List<Map<String, String>> dataList, 
			int index, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		boolean hasPage = true;
		try {
			if(!pageDetailCode.equals("")){
				pageDetailCode = pageDetailCode.substring(pageDetailCode.indexOf("(") + 1, pageDetailCode.length() - 1);
				hasPage = JShopCrawl.equalPageIndex(pageDetailCode,index);
				JSONArray json = JSONObject.fromObject(pageDetailCode).getJSONArray("data");
				String brand = taskMap.get("brand");
				String category = taskMap.get("category");
				String projectId = taskMap.get("id").trim();
				String projectName = taskMap.get("name");
				for(Object obj : json){
					Map<String,String> map = new HashMap<String, String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					map.put("pv", itemJson.getString("pv"));
					map.put("uv", itemJson.getString("uv"));
					System.out.println(itemJson.getString("pv") + "~~" + itemJson.getString("uv"));
					map.put("visits", itemJson.getString("visits"));
					//页面跳转量
					map.put("clickNum", itemJson.getString("clickNum"));
					//引入订单量
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//引入订单金额
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//引入订单商品数
					map.put("introduceOrdItemQtty", itemJson.getString("introduceOrdItemQtty"));
					//引入订单转化率
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//引入订单户数
					map.put("placeOrdUserQtty", itemJson.getString("placeOrdUserQtty"));
					//加购访客数
					map.put("addToCartUv", itemJson.getString("addToCartUv"));
					//加购转化率
					map.put("addToCartRate", itemJson.getString("addToCartRate"));
					//直接订单数
					map.put("dirOrdQtty", itemJson.getString("dirOrdQtty"));
					//直接订单金额
					map.put("dirOrdAmount", itemJson.getString("dirOrdAmount"));
					//直接订单转换率
					map.put("dirOrdRate", itemJson.getString("dirOrdRate"));
					//直接下单用户数
					map.put("dirOrdUserQtty", itemJson.getString("dirOrdUserQtty"));
					//有效订单数
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//有效订单金额
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//有效订单转化率
					map.put("valParOrdRate", itemJson.getString("valParOrdRate"));
					dataList.add(map);
				}
			}else{
				hasPage = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasPage;
	}
	
	
	/**MO来源数据
	 * @param dataList 
	 * @param map2 */
	private static boolean parseMOComeDetail(String comeDetailCode, List<Map<String, String>> dataList,
			int index, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		boolean hasPage = true;
		try {
			if(!comeDetailCode.equals("")){
				comeDetailCode = comeDetailCode.substring(comeDetailCode.indexOf("(") + 1, comeDetailCode.length() - 1);
				hasPage = JShopCrawl.equalPageIndex(comeDetailCode,index);
				JSONArray json = JSONObject.fromObject(comeDetailCode).getJSONArray("data");
				String brand = taskMap.get("brand");
				String category = taskMap.get("category");
				String projectId = taskMap.get("id").trim();
				String projectName = taskMap.get("name");
				for(Object obj : json){
					Map<String,String> map = new HashMap<String, String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					String sourceUrl = itemJson.getString("sourceUrl");
					String divSource = getMODiySource(sourceUrl);
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					map.put("pv", itemJson.getString("pv"));
					map.put("uv", itemJson.getString("uv"));
					System.out.println(itemJson.getString("pv") + "~~" + itemJson.getString("uv"));
					map.put("visits", itemJson.getString("visits"));
					map.put("sourceUrl", sourceUrl);
					map.put("divSource", divSource);
					//引入订单量
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//引入订单金额
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//引入订单转化率
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//有效订单量
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//有效订单金额
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//有效订单转化率
					map.put("valParOrdRate", itemJson.getString("valParOrdRate"));
					dataList.add(map);
				}
			}else{
				hasPage = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasPage;
	}
	
	/**根据url判断自定义来源*/
	private static String getMODiySource(String sourceUrl) {
		String source = "";
		if(sourceUrl.equals("")){
			source = "无法识别";
		}else if(sourceUrl.indexOf("h5.m.jd.com/active/") != -1 ||
				(sourceUrl.indexOf("jd.com/") != -1 && sourceUrl.indexOf("/act") != -1) ||
				sourceUrl.indexOf("m.jd.com/mall/active") != -1){
			source = "京东活动页";
		}else if(sourceUrl.indexOf("item.m.jd.com/product") != -1 || sourceUrl.indexOf("m.jd.hk/product") != -1
				|| sourceUrl.indexOf("item.m.jd.com/detail/") != -1 || sourceUrl.indexOf("Productdetail_") != -1
				|| sourceUrl.indexOf("商品详情页") != -1){
			source = "商品详情页";
		}else if(sourceUrl.indexOf("re.m.jd.com") != -1){
			source = "京东热卖";
		}else if(sourceUrl.indexOf("coupon.m.jd.com/coupons/show.action") != -1 || sourceUrl.indexOf("我的京东主页") != -1){
			source = "我的京东";
		}else if(sourceUrl.indexOf("com.jingdong.app.mall.CommonMFragment") != -1 || 
				sourceUrl.indexOf("JDMainPageViewController") != -1 || sourceUrl.indexOf("JDWebViewController") != -1 
				|| sourceUrl.indexOf("JDWebViewController") != -1 || sourceUrl.indexOf("Home_Main") != -1){
			source = "京东首页";
		}else if(sourceUrl.indexOf("Shop_") != -1){
			source = "店铺页";
		}else if(sourceUrl.indexOf("Search_") != -1){
			source = "搜索分类页";
		}else if(sourceUrl.indexOf("cart") != -1){
			source = "购物车";
		}else if(sourceUrl.indexOf("小冰介绍页") != -1){
			source = sourceUrl;
		}else if(sourceUrl.indexOf("分类页") != -1){
			source =sourceUrl;
		}else{
			source = "京东其他";
		}
		return source;
	}

	/**MO去向数据
	 * @param dataList 
	 * @param map2 */
	private static boolean parseMOToDetail(String toDetailCode, List<Map<String, String>> dataList,
			int index, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		boolean hasPage = true;
		try {
			if(!toDetailCode.equals("")){
				toDetailCode = toDetailCode.substring(toDetailCode.indexOf("(") + 1, toDetailCode.length() - 1);
				hasPage = JShopCrawl.equalPageIndex(toDetailCode,index);
				JSONArray json = JSONObject.fromObject(toDetailCode).getJSONArray("data");
				String brand = taskMap.get("brand");
				String category = taskMap.get("category");
				String projectId = taskMap.get("id").trim();
				String projectName = taskMap.get("name");
				for(Object obj : json){
					Map<String,String> map = new HashMap<String, String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					String toUrl = itemJson.getString("toUrl");
					if(toUrl.equals("")){
						toUrl = "未知";
					}
					String diySource = getMODiySource(toUrl);
					map.put("toUrl", toUrl);
					map.put("diySource", diySource);
					map.put("pv", itemJson.getString("pv"));
					map.put("uv", itemJson.getString("uv"));
					System.out.println(itemJson.getString("pv") + "~~~" + itemJson.getString("uv"));
					map.put("visits", itemJson.getString("visits"));
					//引入订单量
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//引入订单金额
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//引入订单转化率
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//有效订单量
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//有效订单金额
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//有效订单转化率
					map.put("valParOrdRate", itemJson.getString("valParOrdRate"));
					dataList.add(map);
				}
			}else{
				hasPage = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasPage;
	}

}
