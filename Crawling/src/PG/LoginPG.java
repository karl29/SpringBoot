package PG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;

public class LoginPG {
	private static final String userName = "leona@i2mago.com";
	private static final String pwd = "123456";
	public static String cookie = "";
	public static void main(String[] args){
		login();
	}
	
	
	/**登陆宝洁后台*/
	public static void login(){
		try {
			String url = "https://emedia.pg.com.cn/API/Common/Login";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity formEntity = MultipartEntityBuilder.create()
					.addTextBody("userName", userName, ContentType.TEXT_PLAIN)
					.addTextBody("password", pwd, ContentType.TEXT_PLAIN)
					.build();
			post.setEntity(formEntity);
			post.setHeader("Cookie","Principal=");
			post.setHeader("Host","emedia.pg.com.cn");
			post.setHeader("origin","https://emedia.pg.com.cn");
			post.setHeader("Referer","https://emedia.pg.com.cn/");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(post);
			String htmlCode = EntityUtils.toString(response.getEntity(), "utf-8");
			JSONObject dataJson = JSONObject.fromObject(htmlCode).getJSONObject("ContextData");
			String principal = dataJson.getString("IdentityPrincipal");
			cookie = principal;
			System.out.println(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**请求路径统一方法*/
	public static String getHtmlCode(String url,HttpEntity bodyEntity){
		
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			post.setEntity(bodyEntity);
			post.setHeader("cookie","Principal=" + cookie);
			post.setHeader("emediaprincipal",cookie);
			post.setHeader("Host","emedia.pg.com.cn");
			post.setHeader("origin","https://emedia.pg.com.cn");
			post.setHeader("Referer","https://emedia.pg.com.cn/");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(post);
			htmlCode = EntityUtils.toString(response.getEntity(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}
	
	
	/**获取平台列表*/
	public static List<Map<String, String>> getPlatformList() {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		try {
			String url = "https://emedia.pg.com.cn/API/Platform/GetListView";
			JSONObject json = new JSONObject();
			json.put("ColumnName", "IsDeleted");
			json.put("ConditionType", "3");
			json.put("RelationType", "1");
			json.put("Value", "false");
			JSONArray conditions = new JSONArray();
			conditions.add(json);
			HttpEntity entity = MultipartEntityBuilder.create()
					.addTextBody("conditions", conditions.toString(), ContentType.APPLICATION_JSON)
						.build();
			String result = getHtmlCode(url, entity);
			json = JSONObject.fromObject(result);
			System.out.println(json);
			conditions = json.getJSONArray("ContextData");
			Map<String,String> map = null;
			for(Object obj : conditions){
				JSONObject itemJson = JSONObject.fromObject(obj);
				String platformId = itemJson.getString("Id");
				String platform = itemJson.getString("Code");
				map = new HashMap<String, String>();
				map.put(platformId, platform);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
