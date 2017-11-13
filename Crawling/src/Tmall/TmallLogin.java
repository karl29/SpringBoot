package Tmall;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import Utils.HtmlGenUtils;

public class TmallLogin {
	private static final String pwd = "5b05818c9971b960c66d1d16faaf290f5228d6980af28dd2e99f8e8774801f1861fe35d1bee88e278ecae14af141a62d48acd48dbd0ea455235b00b5927b9adecfb859df0fcc4c3e7dbac5fca254ba36cd0ef5f12e1f23a38480400536dd009362519f985ce2de4125eed53d5be001f17c01117ec511cbba1ea1e5131d550790";
	private static final String ua = "094#UVQ6Zr6C6+66Oaty666666BojFfCMoiuG9NjQVf5R+jpiUOHQsyeZhzEwOe0TmxTdj+rFPSuMQtzQ3+M7+FA0BJZ7VB6+tat66AQ9eojlcjpvwmVFBEHKJat66AQ/eoj7/gpvwmVFBEHKJat66AQ9eoj5WdpvwmVFBEHKiat6tLIegLcjjVp45hmrOQvkgFJfRg686Q6QwZfx/lOTTmJfRg6+FLWz8EXJx646M6jHThZ2k+e48EXJxdWu9m+45hmrnat6tLIegLcjj9E45hmrOQvkgFJfRg686Q6QwZfx/lOLnpJfRg6+FLWz8EXJx646M6jHThZ2k+wE/EXJxdWu9m+45hmrnat6tLIegLcjjWk45hmrOQvkgFJfRg6taQ6tojfx/7A6M6ltL3u+kUSC2wC9eoFCY8Gj3SqN+YptGmd5MRt66F+egLc86Q6dwSkx/lO8DskV9dtSnsLzWat66AQ/eojhm11cEczMdTrK96t66jCweoFtaQ6tojfx/7A6M6ltL3t+aKSC2wC9eoFCY8Gh0c9WjGHETPFy7At66lGYHbba4uA6MnuzOmZViFY28EEZ9SUuuS0pO1g04aAa1PZflD5VFLLbw0gkz1uR4pkHOStVeRbsGbPsU1BZSoO2odgqeoUR4nbHUpRfxxpJDoPsU1BSjcYb1H3a5w83APbzzsg9HM60HYva3Gfa0whA2GRczjTuxsebInB0NMzvT/Ss9ihqECWq10gkz1uR4pkHnMt6ni+egLcjLTM+gjfxplNAeo2zThS9kZv+pc+egocjLTF/gjfxplNAeo2aaQ6tgCfxD5fx6Q6nojfxH12+09IEaQ6K1M46Dl5HHCePzH6ZihK2Tc5sG0eGo/86M6Qptf8WMRt66F+egLcH6Q6tLGC+e346M6TH8SZ2k+2C7jEkNEuoR/ftaQ6tojfx/RR6M6LtLGF++P8/9J91B6ZIt3R6M6LtLHF++9Hw4PVxBqtwVR46M6jHThZ2k+fkDEXJxdWu9m+45hmr6Rt66F+egLc86Q6QwZfx/lOOO8JfRg6+FLWz8EXJx646M6jHThZ2k+wf7EXJxdWu9m+45hmrnat6tLIegLcjjiU45hmrOQvkgFJfRg686Q6QwZfx/lOLtnJfRg6+FLWz8EXJxnA6M6gdL50+/JSC2wC9eoFCY8GRuzzWknr6ypUcRqmtaQ6tojfx/R=";
	private static final String um_token = "HV01WAAZ0be3fa4bb706ad6d5940d12600986072";
	public static void main(String[] args){
		login();
	}
	public static String login(){
		String url = "https://login.m.taobao.com/login.htm";
		String cookie = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","login.m.taobao.com");
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			String _tb_token_ = doc.getElementsByAttributeValue("name", "_tb_token_").val();
			String action = doc.getElementById("J_Mparam").val();
			String event_submit_do_login = doc.getElementsByAttributeValue("name", "event_submit_do_login").val();
			String loginFrom = doc.getElementsByAttributeValue("name", "loginFrom").val();
			String otherLoginUrl = doc.getElementsByAttributeValue("name", "otherLoginUrl").val();
			String ncoToken = doc.getElementById("J_NcoToken").val();
			String TPL_username = "qy2256258";
			String nv = "false";
			
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost("https://login.m.taobao.com/login.htm?_input_charset=utf-8");
			HttpEntity bodyEntity = MultipartEntityBuilder.create().addPart("_tb_token_", new StringBody(_tb_token_, ContentType.TEXT_PLAIN))
						.addPart("action", new StringBody(action, ContentType.TEXT_PLAIN))
						.addPart("event_submit_do_login", new StringBody(event_submit_do_login, ContentType.TEXT_PLAIN))
						.addPart("TPL_redirect_url", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("loginFrom", new StringBody(loginFrom, ContentType.TEXT_PLAIN))
						.addPart("style", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("bind_token", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("assets_css", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("assets_js", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("ssottid", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("nv", new StringBody(nv, ContentType.TEXT_PLAIN))
						.addPart("otherLoginUrl", new StringBody(otherLoginUrl, ContentType.TEXT_PLAIN))
						.addPart("TPL_timestamp", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("TPL_password2", new StringBody(pwd, ContentType.TEXT_PLAIN))
						.addPart("ncoSig", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("ncoSessionid", new StringBody("", ContentType.TEXT_PLAIN))
						.addPart("ncoToken", new StringBody(ncoToken, ContentType.TEXT_PLAIN))
						.addPart("TPL_username", new StringBody(TPL_username, ContentType.TEXT_PLAIN))
						.addPart("ua", new StringBody(ua, ContentType.TEXT_PLAIN))
						.addPart("um_token", new StringBody(um_token, ContentType.TEXT_PLAIN))
						.build();
			post.setEntity(bodyEntity);
			post.setHeader("Host","login.m.taobao.com");
			post.setHeader("Origin","https://login.m.taobao.com");
			post.setHeader("Referer","https://login.m.taobao.com/login.htm");
			CloseableHttpResponse response2 = client.execute(post);
			Header[] headers = response2.getHeaders("Set-Cookie");
			for(Header header : headers){
				String headerVal = header.getValue();
				headerVal = headerVal.substring(0, headerVal.indexOf(";"));
				if(cookie.equals("")){
					cookie = headerVal;
				}else{
					cookie += ";" + headerVal;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cookie;
	}
}
