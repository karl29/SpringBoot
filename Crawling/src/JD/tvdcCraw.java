package JD;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.HtmlGenUtils;

public class tvdcCraw {
	private static String cookie = "user-key=2acd6704-4d7d-4525-8587-75c21cceb040; pdtk=umMjgdqEYpByF1W1lQuAWNj6Y%2BLfqjS8IqcdCEZUxW4BCvBIiAJv8U%2FHR8ofiLE4; cn=0; netParam=outer; __jdv=122270672|direct|-|none|-|1500014882032; _jrda=3; wlfstk_smdl=g1n3hdjhlnqhfgqrbw6f9pycc8rihehy; __jda=122270672.1497257805477690053470.1497257805.1499824178.1500014882.34; __jdc=122270672; 3AB9D23F7A4B3C9B=SC3JVQURCKKQ6KLYBZFP5CBT2HRALT4JIIIMRRIJYT7OLMY3BOFO45M6UBTHDEVV7VRNSGWZEZLECCMK6UQXSXD5BE; TrackID=14e4fFvgfIRAOa343zJuHcGNpK70rec2oJm-AKsjKABtsw4YXE4tQf48N-l4Zd9UInCWbvGQR2_HQznpIxTT9a0dY2zChyPynF6uFExHB4kQ; pinId=Plsjho6bFmXedA6t-iNc4rV9-x-f3wj7; pin=jd_567d58332fec9; unick=JINGDONG-PG; thor=C9ACF0338827F8B4759F187C193CC14AB52A46C08BDEC52909313880D553FD7F15C4836B90EB1212B0424EE50848B6165D006915C907729BA9A1810977EEF6712792161ACBFBA1E209A18438F613F4EA8C9999612C94E4D3CF7524B34786E13548EC844D3432E117FFD40881B61F4F73F8D2C54F413A7AD461B1C3D0E211E2711D1B00BDD9EF832A956AA8BD0F224464338EAA934FDF3D05B98DA7D1F31F13F5; _tp=m2HTiU5tB0%2F7aXn7xWWAocuigrVw1EVC30IbOvO6ztg%3D; _pst=jd_567d58332fec9; ceshi3.com=000; Hm_lvt_bf04ec1837e04357661e0b72f4619981=1500014978; Hm_lpvt_bf04ec1837e04357661e0b72f4619981=1500015646; VDC_INTEGRATION_JSESSIONID=8a44ee22-9683-4ddf-98b0-f730346fc341; __jda=113613634.1497257805477690053470.1497257805.1499824178.1500014882.34; __jdb=113613634.3.1497257805477690053470|34.1500014882; __jdc=113613634; __jdu=1497257805477690053470";
	public static void main(String[] args){
		getBrandList();
	}
	
	public static void craw(){
		
	}
	
	
	public static List<String> getBrandList(){
		List<String> brandList = null;
		String url = "https://tvdc.jd.com/common/userBrandList";
		try {
			String htmlCode = getHtmlCode(url);
			if(!htmlCode.equals("")){
				JSONArray itemList = JSONArray.fromObject(htmlCode);
				brandList = new ArrayList<String>();
				for(Object obj : itemList){
					JSONObject json = JSONObject.fromObject(obj);
					String id = json.getString("id");
					String name = json.getString("name");
					brandList.add(id);
					System.out.println(id + "~~~" + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return brandList;
	}
	public static List<String> getCategoryList(){
		List<String> brandList = new ArrayList<String>();
		String url = "https://tvdc.jd.com/common/userCategoryList";
		try {
			String htmlCode = getHtmlCode(url);
			if(!htmlCode.equals("")){
				JSONArray itemList = JSONArray.fromObject(htmlCode);
				brandList = new ArrayList<String>();
				for(Object obj : itemList){
					JSONObject json = JSONObject.fromObject(obj);
					String id = json.getString("id");
					String name = json.getString("name");
					brandList.add(id);
					System.out.println(id + "~~~" + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return brandList;
	}
	
	public static String getHtmlCode(String url){
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity bodyEntity = MultipartEntityBuilder.create()
					.addPart("vendorCode", new StringBody("", ContentType.TEXT_PLAIN))
					.addPart("module2Id", new StringBody("3000052", ContentType.TEXT_PLAIN))
					.build();
			post.setEntity(bodyEntity);
			post.setHeader("Host","tvdc.jd.com");
			post.setHeader("cookie",cookie);
			post.setHeader("Origin","https://tvdc.jd.com");
			post.setHeader("Referer","https://tvdc.jd.com/index/indexPage");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(post);
			htmlCode = EntityUtils.toString(response.getEntity(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}
}
