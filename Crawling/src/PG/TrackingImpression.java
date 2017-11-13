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


/**HA Weekly Tracking 报表另外2个字段爬取*/
public class TrackingImpression {
	public static void main(String[] args){
		LoginPG.login();
		crawl();
	}
	
	/**
	 * 开始爬取Tracking 列表
	 * */
	public static void crawl(){
		try {
			List<Map<String,String>> list = getIdList();
			
			getClickAmount(list);//获取另外2个字段的数值
			
			if(list.size() > 0){
				PGDataUtils.saveHAExcel(list);//excel表
				PGDataUtils.updateImpression(list);//更新表的impression字段
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static void getClickAmount(List<Map<String, String>> list) {
		// TODO Auto-generated method stub
		try {
			String url = "https://emedia.pg.com.cn/API/TrackingHA/GetPageView";
			String batchUrl = "https://emedia.pg.com.cn/API/TrackingHABatch/GetData";
			for(Map<String,String> map : list){
				try {
					int pageIndex = 1;
					boolean hasPageView = true;
					String id = map.get("id");
					HttpEntity entity = MultipartEntityBuilder.create().addTextBody("id", id, ContentType.TEXT_PLAIN).build();
					String batchCode = LoginPG.getHtmlCode(batchUrl, entity);
					JSONObject batchJson = JSONObject.fromObject(batchCode);
					String batchId = batchJson.getJSONObject("ContextData").getString("BatchId");
					while(hasPageView){
						HttpEntity bodyEntity = getBodyEntity(batchId,pageIndex);
						String htmlCode = LoginPG.getHtmlCode(url, bodyEntity);
						hasPageView = parseDetailImpression(htmlCode,pageIndex,list);
						pageIndex++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**详情报表数据*/
	private static boolean parseDetailImpression(String htmlCode,
			int pageIndex, List<Map<String, String>> list) {
		boolean hasPageView = true;
		try {
			JSONObject dataJson = JSONObject.fromObject(htmlCode);
			JSONObject contextData = dataJson.getJSONObject("ContextData");
			if(contextData.getString("PageCount").equals(pageIndex + "")){
				hasPageView = false;
			}else{
				JSONArray pageList = contextData.getJSONArray("PageList");
				Map<String,String> map = null;
				for(Object obj : pageList){
					JSONObject itemJson = JSONObject.fromObject(obj);
					map = new HashMap<String, String>();
					map.put("dataId", itemJson.getString("Id"));
					map.put("categoryId", itemJson.getString("CategoryId"));
					map.put("trackingDate", itemJson.getString("TrackingDate"));
					map.put("platformId", itemJson.getString("PlatformId"));
					map.put("channelId", itemJson.getString("ChannelId"));
					map.put("positionId", itemJson.getString("PositionId"));
					map.put("click", itemJson.getString("Click"));
					map.put("impression", itemJson.getString("Impression"));
					map.put("cost", itemJson.getString("cost"));
					System.out.println(itemJson.getString("Id") + "~~" + itemJson.getString("Click") + "~~" + itemJson.getString("Impression") + "~~" + itemJson.getString("cost"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasPageView;
	}

	/**详情页链接请求参数*/
	private static HttpEntity getBodyEntity(String batchId,int pageIndex) {
		JSONArray conditions = new JSONArray();
		JSONObject platformJson = new JSONObject();
		platformJson.put("ColumnName", "BatchId");
		platformJson.put("ConditionType", "3");
		platformJson.put("RelationType", "1");
		platformJson.put("Value", batchId);
		
		JSONObject cateGoryJson = new JSONObject();
		cateGoryJson.put("ColumnName", "IsActive");
		cateGoryJson.put("ConditionType", "3");
		cateGoryJson.put("RelationType", "1");
		cateGoryJson.put("Value", true);
		
		conditions.add(platformJson);
		conditions.add(cateGoryJson);
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("conditions", conditions.toString(), ContentType.APPLICATION_JSON)
				.addTextBody("CurPage", pageIndex + "", ContentType.TEXT_PLAIN)
				.addTextBody("PageSize", "20", ContentType.TEXT_PLAIN)
					.build();
		return entity;
	}

	/**获取excel列表id*/
	private static List<Map<String,String>> getIdList() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String lastId = PGDataUtils.getLastImportId();
		try {
			String url = "https://emedia.pg.com.cn/API/TrackingHABatch/GetPageView";
			int pageIndex = 1;
			boolean hasPageView = true;
			while(hasPageView){
				HttpEntity bodyEntity = MultipartEntityBuilder.create()
						.addTextBody("CurPage", pageIndex + "", ContentType.TEXT_PLAIN)
						.addTextBody("PageSize", "20", ContentType.TEXT_PLAIN).build();
				String htmlCode = LoginPG.getHtmlCode(url, bodyEntity);
				hasPageView = parseHtmlCode(htmlCode,pageIndex,list,lastId);
				pageIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**解析数据
	 * @param lastId */
	private static boolean parseHtmlCode(String htmlCode, int pageIndex,List<Map<String,String>> idList, String lastId) {
		boolean hasPageView = true;
		try {
			JSONObject dataJson = JSONObject.fromObject(htmlCode);
			JSONObject contextData = dataJson.getJSONObject("ContextData");
			if(contextData.getString("PageCount").equals(pageIndex + "")){
				hasPageView = false;
			}else{
				JSONArray pageList = contextData.getJSONArray("PageList");
				Map<String,String> map = null;
				for(Object obj : pageList){
					JSONObject itemJson = JSONObject.fromObject(obj);
					map = new HashMap<String, String>();
					String id = itemJson.getString("Id");
					if(id.equals(lastId)){
						hasPageView = false;
						break;
					}
					String batchName = itemJson.getString("BatchName");
					String importTime = itemJson.getString("ImportTime");
					map.put("id", id);
					map.put("batchName", batchName);
					map.put("importTime", importTime);
					System.out.println(id);
					idList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasPageView;
	}
}
