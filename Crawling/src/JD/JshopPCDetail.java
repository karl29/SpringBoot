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
 * 
 * Jshop PC��������ȡ��
 * */
public class JshopPCDetail {
	public static void main(String[] args){
		crawlPageDetail(ExcelUtils.getTaskList("Jshop"),HtmlGenUtils.getDataTime("yyyy-MM-dd", -1));
	}
	
	
	/**��ȡpc��ϸ����ȥ������*/
	public static void crawRedirectDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/act/detail?appId=" + projectId;
				if(map.get("terminal").equals("PC")){
					boolean hasPage = true;
					while(hasPage){

						String redirectDetailUrl = "http://data-jshop.jd.com/act/detail/queryAppRedirectDetail?"
								+ "callback=jQuery1111023660621064010723_1503627681011"
								+ "&appId="+projectId+"&queryStartTime="+dataTime+"&queryEndTime=" + dataTime
								+ "&index="+index+"&pageSize=10&_="  + System.currentTimeMillis(); 
						String redirectDetailCode = JShopCrawl.getDetailHtmlCode(redirectDetailUrl, refererUrl);
						hasPage = parsePCRedirectDetail(redirectDetailCode,index,dataList,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.saveRedirectPc(dataList);
		}
	}
	/**
	 * ��ȡpc��ϸ������Դ����
	 * */
	public static void crawlRefererDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/act/detail?appId=" + projectId;
				if(map.get("terminal").equals("PC")){
					boolean hasPage = true;
					while(hasPage){
						String refererDetailUrl = "http://data-jshop.jd.com/act/detail/queryAppRefererDetail?"
								+ "callback=jQuery1111023660621064010723_1503627681009"
								+ "&appId="+projectId+"&queryStartTime="+dataTime+"&queryEndTime=" + dataTime
								+ "&index="+index+"&pageSize=10&_="  + System.currentTimeMillis();
						String refererDetailCode = JShopCrawl.getDetailHtmlCode(refererDetailUrl, refererUrl);
						hasPage = paresPCRefererDetail(refererDetailCode,index,dataList,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.saveRefererPc(dataList);
		}
	}
	
	/**��ȡpc��ϸ����ҳ������*/
	public static void crawlPageDetail(List<Map<String,String>> mapList,String dataTime){
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		for(Map<String,String> map : mapList){
			try {
				String projectId = map.get("id").trim();
				int index = 1;
				String refererUrl = "http://data-jshop.jd.com/act/detail?appId=" + projectId;
				if(map.get("terminal").equals("PC")){
					boolean hasPage = true;
					while(hasPage){
						String pageDetailUrl = "http://data-jshop.jd.com/act/detail/queryAppPageDetail?"
								+ "callback=jQuery1111023660621064010723_1503627681005&"
								+ "appId="+projectId+"&queryStartTime="+dataTime+"&queryEndTime="+dataTime+"&"
								+ "index="+index+"&pageSize=10&_=" + System.currentTimeMillis();
						String pageDetailCode = JShopCrawl.getDetailHtmlCode(pageDetailUrl,refererUrl);
						hasPage = parsePCPageDetail(pageDetailCode,dataList,index,map);
						index++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(dataList.size() > 0){
			JshopDatabase.savePagePc(dataList);
		}
	}
	
	/**pcҳ������
	 * @param dataList 
	 * @param index 
	 * @param taskMap */
	private static boolean parsePCPageDetail(String pageDetailCode, List<Map<String, String>> dataList, int index, Map<String, String> taskMap) {
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
					JSONObject itemJson = JSONObject.fromObject(obj);
					Map<String,String> map = new HashMap<String, String>();
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					map.put("pv", itemJson.getString("appPv"));
					map.put("uv", itemJson.getString("appUv"));
					System.out.println(itemJson.getString("appPv") + "~~" + itemJson.getString("appUv"));
					map.put("appVisits", itemJson.getString("appVisits"));
					map.put("appPageAvgRt", itemJson.getString("appPageAvgRt"));
					map.put("appBounce", itemJson.getString("appBounce"));
					map.put("appDirectOrderNum", itemJson.getString("appDirectOrderNum"));
					map.put("appDirectOrderAccount", itemJson.getString("appDirectOrderAccount"));
					map.put("appDirectOrderRate", itemJson.getString("appDirectOrderRate"));
					map.put("appSecOrderNum", itemJson.getString("appSecOrderNum"));
					map.put("appSecOrderAccount", itemJson.getString("appSecOrderAccount"));
					map.put("appSecOrderRate", itemJson.getString("appSecOrderRate"));
					map.put("appPageDepthVisit", itemJson.getString("appPageDepthVisit"));
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

	/**PC��Դ����
	 * @param dataList 
	 * @param taskMap */
	private static boolean paresPCRefererDetail(String refererDetailCode, int index, List<Map<String, String>> dataList, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		boolean hasPage = true;
		try {
			if(!refererDetailCode.equals("")){
				refererDetailCode = refererDetailCode.substring(refererDetailCode.indexOf("(") + 1, refererDetailCode.length() - 1);
				hasPage = JShopCrawl.equalPageIndex(refererDetailCode,index);
				JSONArray json = JSONObject.fromObject(refererDetailCode).getJSONArray("data");
				String brand = taskMap.get("brand");
				String category = taskMap.get("category");
				String projectId = taskMap.get("id").trim();
				String projectName = taskMap.get("name");
				for(Object obj : json){
					Map<String,String> map = new HashMap<String, String>();
					JSONObject itemJson = JSONObject.fromObject(obj);
					String referUrl = itemJson.getString("appPageReferUrl");
					String diySource = getDiySource(referUrl);//�Զ�����Դ
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					map.put("pv", itemJson.getString("appPv"));
					map.put("uv", itemJson.getString("appUv"));
					System.out.println(itemJson.getString("appPv") + "~~" + itemJson.getString("appUv"));
					map.put("vistis", itemJson.getString("appVisits"));
					map.put("url",referUrl);
					map.put("diySource", diySource);
					map.put("appPageAvgRt", itemJson.getString("appPageAvgRt"));//ƽ��ͣ��ʱ��
					//������
					map.put("appBounce", itemJson.getString("appBounce"));
					//�µ���
					map.put("appDirectOrderNum", itemJson.getString("appDirectOrderNum"));
					//�µ���
					map.put("appDirectOrderRate", itemJson.getString("appDirectOrderRate"));
					//����µ���
					map.put("appSecOrderNum", itemJson.getString("appSecOrderNum"));
					//���ת����
					map.put("appSecOrderRate", itemJson.getString("appSecOrderRate"));
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
	
	private static String getDiySource(String referUrl) {
		String source = "";
		if(referUrl.indexOf("jd.com") == -1){
			source = "��ý";
		}else{
			if(referUrl.indexOf("jd.com/act") != -1){
				source = "�����ҳ";
			}else if(referUrl.indexOf("item.jd.com") != -1){
				source = "��Ʒ����ҳ";
			}else if(referUrl.indexOf("item.jd.com") != -1){
				source = "��Ʒ����ҳ";
			}else if(referUrl.indexOf("chaoshi.jd.com") != -1){
				source = "��������";
			}else if(referUrl.indexOf("mall.jd.com") != -1){
				source = "�콢��";
			}else if(referUrl.indexOf("union.click.jd.com/jdc") != -1){
				source = "΢��QQ";
			}else if(referUrl.indexOf("re.jd.com") != -1){
				source = "��������";
			}else if(referUrl.indexOf("jd.com/pinpai/") != -1){
				source = "��Ʒ�б�";
			}else if(referUrl.indexOf("www.jd.com") != -1 && referUrl.indexOf("www.jd.com/pinpai") == -1){
				source = "��Ʒ��ҳ";
			}else {
				source = "��������";
			}           
		}
		return source;
	}


	/**PCȥ������
	 * @param dataList 
	 * @param taskMap */
	private static boolean parsePCRedirectDetail(String redirectDetailCode,
			int index, List<Map<String, String>> dataList, Map<String, String> taskMap) {
		// TODO Auto-generated method stub
		boolean hasPage = true;
		try {
			if(!redirectDetailCode.equals("")){
				redirectDetailCode = redirectDetailCode.substring(redirectDetailCode.indexOf("(") + 1, redirectDetailCode.length() - 1);
				hasPage = JShopCrawl.equalPageIndex(redirectDetailCode,index);
				JSONArray json = JSONObject.fromObject(redirectDetailCode).getJSONArray("data");
				String brand = taskMap.get("brand");
				String category = taskMap.get("category");
				String projectId = taskMap.get("id");
				String projectName = taskMap.get("name");
				for(Object obj : json){
					JSONObject itemJson = JSONObject.fromObject(obj);
					Map<String,String> map = new HashMap<String, String>();
					String toUrl = itemJson.getString("appPageToUrl");
					String diyRedirect = getDiySource(toUrl);
					map.put("brand", brand);
					map.put("category", category);
					map.put("projectId", projectId);
					map.put("projectName", projectName);
					map.put("dataTime", itemJson.getString("statTime"));
					map.put("pv", itemJson.getString("appPv"));
					map.put("uv", itemJson.getString("appUv"));
					System.out.println(itemJson.getString("appPv") + "~~~" + itemJson.getString("appUv"));
					map.put("toUrl", toUrl);
					map.put("diyRedirect", diyRedirect);
					map.put("vistis", itemJson.getString("appVisits"));
					//ƽ��ͣ��ʱ��
					map.put("appPageAvgRt", itemJson.getString("appPageAvgRt"));
					//������
					map.put("appBounce", itemJson.getString("appBounce"));
					//�µ���
					map.put("appDirectOrderNum", itemJson.getString("appDirectOrderNum"));
					//�µ���
					map.put("appDirectOrderRate", itemJson.getString("appDirectOrderRate"));
					//����µ���
					map.put("appSecOrderNum", itemJson.getString("appSecOrderNum"));
					//���ת����
					map.put("appSecOrderRate", itemJson.getString("appSecOrderRate"));
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
