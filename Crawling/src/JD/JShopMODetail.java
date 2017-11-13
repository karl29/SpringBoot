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
 * JShop mobile��������ȡ��
 * */
public class JShopMODetail {
	public static void main(String[] args){
		//MaochaoJob.loginJshop();//��½ƽ̨
		for(int i = 3;i>=1;i--){
			crawlGoDetail(ExcelUtils.getTaskList("Jshop"),HtmlGenUtils.getDataTime("yyyy-MM-dd", -i));
		}
	}
	
	/**��������ȥ������*/
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
	/**����������Դ����*/
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
	/**��������ҳ������*/
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
					//ҳ����ת��
					map.put("clickNum", itemJson.getString("clickNum"));
					//���붩����
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//���붩�����
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//���붩����Ʒ��
					map.put("introduceOrdItemQtty", itemJson.getString("introduceOrdItemQtty"));
					//���붩��ת����
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//���붩������
					map.put("placeOrdUserQtty", itemJson.getString("placeOrdUserQtty"));
					//�ӹ��ÿ���
					map.put("addToCartUv", itemJson.getString("addToCartUv"));
					//�ӹ�ת����
					map.put("addToCartRate", itemJson.getString("addToCartRate"));
					//ֱ�Ӷ�����
					map.put("dirOrdQtty", itemJson.getString("dirOrdQtty"));
					//ֱ�Ӷ������
					map.put("dirOrdAmount", itemJson.getString("dirOrdAmount"));
					//ֱ�Ӷ���ת����
					map.put("dirOrdRate", itemJson.getString("dirOrdRate"));
					//ֱ���µ��û���
					map.put("dirOrdUserQtty", itemJson.getString("dirOrdUserQtty"));
					//��Ч������
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//��Ч�������
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//��Ч����ת����
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
	
	
	/**MO��Դ����
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
					//���붩����
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//���붩�����
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//���붩��ת����
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//��Ч������
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//��Ч�������
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//��Ч����ת����
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
	
	/**����url�ж��Զ�����Դ*/
	private static String getMODiySource(String sourceUrl) {
		String source = "";
		if(sourceUrl.equals("")){
			source = "�޷�ʶ��";
		}else if(sourceUrl.indexOf("h5.m.jd.com/active/") != -1 ||
				(sourceUrl.indexOf("jd.com/") != -1 && sourceUrl.indexOf("/act") != -1) ||
				sourceUrl.indexOf("m.jd.com/mall/active") != -1){
			source = "�����ҳ";
		}else if(sourceUrl.indexOf("item.m.jd.com/product") != -1 || sourceUrl.indexOf("m.jd.hk/product") != -1
				|| sourceUrl.indexOf("item.m.jd.com/detail/") != -1 || sourceUrl.indexOf("Productdetail_") != -1
				|| sourceUrl.indexOf("��Ʒ����ҳ") != -1){
			source = "��Ʒ����ҳ";
		}else if(sourceUrl.indexOf("re.m.jd.com") != -1){
			source = "��������";
		}else if(sourceUrl.indexOf("coupon.m.jd.com/coupons/show.action") != -1 || sourceUrl.indexOf("�ҵľ�����ҳ") != -1){
			source = "�ҵľ���";
		}else if(sourceUrl.indexOf("com.jingdong.app.mall.CommonMFragment") != -1 || 
				sourceUrl.indexOf("JDMainPageViewController") != -1 || sourceUrl.indexOf("JDWebViewController") != -1 
				|| sourceUrl.indexOf("JDWebViewController") != -1 || sourceUrl.indexOf("Home_Main") != -1){
			source = "������ҳ";
		}else if(sourceUrl.indexOf("Shop_") != -1){
			source = "����ҳ";
		}else if(sourceUrl.indexOf("Search_") != -1){
			source = "��������ҳ";
		}else if(sourceUrl.indexOf("cart") != -1){
			source = "���ﳵ";
		}else if(sourceUrl.indexOf("С������ҳ") != -1){
			source = sourceUrl;
		}else if(sourceUrl.indexOf("����ҳ") != -1){
			source =sourceUrl;
		}else{
			source = "��������";
		}
		return source;
	}

	/**MOȥ������
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
						toUrl = "δ֪";
					}
					String diySource = getMODiySource(toUrl);
					map.put("toUrl", toUrl);
					map.put("diySource", diySource);
					map.put("pv", itemJson.getString("pv"));
					map.put("uv", itemJson.getString("uv"));
					System.out.println(itemJson.getString("pv") + "~~~" + itemJson.getString("uv"));
					map.put("visits", itemJson.getString("visits"));
					//���붩����
					map.put("introduceOrdQtty", itemJson.getString("introduceOrdQtty"));
					//���붩�����
					map.put("introduceOrdAmount", itemJson.getString("introduceOrdAmount"));
					//���붩��ת����
					map.put("introduceOrdRate", itemJson.getString("introduceOrdRate"));
					//��Ч������
					map.put("valOrdQtty", itemJson.getString("valOrdQtty"));
					//��Ч�������
					map.put("valOrdAmount", itemJson.getString("valOrdAmount"));
					//��Ч����ת����
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
