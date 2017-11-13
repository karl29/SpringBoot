package Tmall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;


public class CrawlMBMaochaoList {
	public static void main(String[] args){
		crawlSearchList();
	}
	
	
	public static void crawlSearchList(){
		List<Map<String,String>> categoryList = TmallSearchResult.readExcel();
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>(); 
		for(Map<String,String> map : categoryList){
			String category = map.get("category");
			String refererurl = map.get("url");
			if(category.equals("洗发水") || category.equals("护发素/发膜/营养水") || 
					category.equals("染发剂/啫哩水/头发造型") || category.equals("牙膏") || 
					category.equals("牙刷") || category.equals("儿童口腔护理") ||
					category.equals("漱口水") || category.equals("牙线/牙线棒/牙粉")){
				getData(mapList,refererurl);
			}
		}
	}


	private static void getData(List<Map<String, String>> mapList, String refererurl) {
		// TODO Auto-generated method stub
		String path = refererurl.substring(refererurl.indexOf(".htm") + 4,refererurl.length());
		int page = 1;
		boolean hasData = true;
		while(hasData){
			try {
				String urlPath = path + "&p=" + page + "&unify=yes&from=chaoshi";
				String url = "https://list.tmall.com/chaoshi_data.htm" + urlPath;
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader(":authority","list.tmall.com");
				get.setHeader(":method","GET");
				get.setHeader(":path",urlPath);
				get.setHeader(":scheme","https");
				get.setHeader("referer",refererurl);
				get.setHeader("user-agent",HtmlGenUtils.MBuserAgent);
				CloseableHttpResponse response = client.execute(get);
				String result = EntityUtils.toString(response.getEntity(), "utf-8");
				if(!result.equals("")){
					hasData = parseResult(result,mapList,page);
				}
				page++;
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	/**解析json数据
	 * @param page */
	private static boolean parseResult(String result,
			List<Map<String, String>> mapList, int page) {
		boolean hasPage = true;
		try {
			JSONObject dataJson = JSONObject.fromObject(result);
			JSONObject pageJson = dataJson.getJSONObject("page");
			if(Integer.valueOf(pageJson.getString("totalPage")) == page){
				hasPage = false;
			}
			JSONArray itemList = dataJson.getJSONArray("srp");
			for(Object obj : itemList){
				JSONObject itemJson = JSONObject.fromObject(obj);
				Map<String,String> map = new HashMap<String,String>();
				String itemId = itemJson.getString("nid");
				map.put("itemId", itemId);
				System.out.println(itemId);
				
				String itemName = itemJson.getString("title");
				map.put("itemName", itemName);
				System.out.println(itemName);
				
				String href = itemJson.getString("url");
				System.out.println(href);
				map.put("href", "https:" + href);
				
				String price = itemJson.getString("reservePrice");
				System.out.println(price);
				map.put("price", price);
				map.put("pageIndex", page + "");
				mapList.add(map); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			hasPage = false;
		}
		return hasPage;
	}
}
