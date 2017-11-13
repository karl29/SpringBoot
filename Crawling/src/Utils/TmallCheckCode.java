package Utils;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.nodes.Document;

public class TmallCheckCode {
	private static final String ua = "037UW5TcyMNYQwiAiwZTXFIdUh1SHJOe0BuOG4=|Um5Ockt/S3FKcU1zTHhFcSc=|U2xMHDJ7G2AHYg8hAS8XIw0tA18+WDRTLVd5L3k=|VGhXd1llXGhcZl1mWmRbblBoX2JAe0J5Rn5Lf0N6RnpGek56Q207|VWldfS0SMg46BSUZIQEvFzIXZQw0ZFkPP0UmGS9bdSN1|VmhIGCUFOBgkGyIWNg8xBSUZJxwnBz0GMxMvESoRMQs0AVcB|V25OHjAePgs3CSkVIRs7Az4LXQs=|WGFBET8RMQs3Di4QLhs7BDgDO207|WWBAED4QMAU5AyMcJBAwDDMMNg1bDQ==|WmJCEjxXN1oxUjleMl8cegN/AiwMXGZfZER7R3IkBDkZNxk5BTsDOARSBA==|W2BAED5VNVgzUDtcMF0eeAF9AC4OMg0zEy4OMgo2CTJkMg==|XGdHFzlSMl80VzxbN1oZfwZ6BykJNQo0FCkJNQ42CDFnMQ==|XWVFFTtQMF02VT5ZNVgbfQR4BSsLW2FZZER7RHosDDMTPRMzDDAOMgxaDA==|XmZGFjhTM141Vj1aNlsYfRI8HEx3TXVVaVJsTHdOd1VsU29aYFh4RH1DY19nWnpDfigINxc5FzcOMQ8zDFoM|X2dHFzkXN2ddZFBwRX8pCTYWOBY2Cj4GPgE5bzk=|QHhYCCZNLUArSCNEKEUGYBllGDYWRnNOclJtUWg+HiMDLQMjGiAZLRkhdyE=|QXpaCiRPL0IpSiFGKkcEYhtnGjQULxU1CCgRKxMnHiF3IQ==|QnpaCiQKKnpAe0ZmWWxQBiYbOxU7GyIXKh8lEUcR|Q3pHelpnR3hYZF1hQX9HfV1kRHhFZVFxRGRYY0N8R2dYYEB8QmJeYUF9QWFdYEB/QmJXAQ==";
	public static void checkCodeImg(Document doc) {
		// TODO Auto-generated method stub
		String url = "https://sec.taobao.com/query.htm";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity bodyEntity = setEntity(doc);
			post.setEntity(bodyEntity);
			post.setHeader("Host","sec.taobao.com");
			post.setHeader("Origin","https://sec.taobao.com");
			post.setHeader("Referer","https://sec.taobao.com/query.htm?smApp=tmallsearch&smPolicy=tmallsearch-product-anti_Spider-html-checkcode&smCharset=GBK&smTag=MTgzLjYuMTczLjEwOSw1ODU4NTQ5OTIsNzg3NGE4ODVkNGE3NDc0Mjk0M2NjYTlhZTQyYzFjMDY%3D&smReturn=https%3A%2F%2Flist.tmall.com%2Fsearch_product.htm%3Fspm%3Da3204.7933263.1996500281.152.d811797X0zUxp%26cat%3D52580012%26style%3Dg%26acm%3Dlb-zebra-26901-351047.1003.8.468125%26search_condition%3D23%26active%3D1%26user_id%3D725677994%26scm%3D1003.8.lb-zebra-26901-351047.ITEM_14440857741161_468125&smSign=iL00jr%2FbP3qAjKAmH8poow%3D%3D");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(post);
			Header[] headers = response.getHeaders("Location");
			for(Header header : headers){
				System.out.println(header.getValue());
				System.out.println("验证成功~~~~~~");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static HttpEntity setEntity(Document doc) {
		HttpEntity entity = null;
		try {
			String imgUrl = doc.getElementById("checkcodeImg").attr("src");
			String action = doc.getElementsByAttributeValue("name", "action").val();
			String event_submit_do_query = doc.getElementsByAttributeValue("name", "event_submit_do_query").val();
			String smPolicy = doc.getElementsByAttributeValue("name", "smPolicy").val();
			String smReturn = doc.getElementsByAttributeValue("name", "smReturn").val();
			String smApp = doc.getElementsByAttributeValue("name", "smApp").val();
			String smCharset = doc.getElementsByAttributeValue("name", "smCharset").val();
			String smTag = doc.getElementsByAttributeValue("name", "smTag").val();
			String smSign = doc.getElementsByAttributeValue("name", "smSign").val();
			String identity = doc.getElementsByAttributeValue("name", "identity").val();
			String captcha = doc.getElementsByAttributeValue("name", "captcha").val();
			String imgCode = getImgeCode("https:" + imgUrl);
			entity = MultipartEntityBuilder.create().addTextBody("ua", ua, ContentType.TEXT_PLAIN)
					.addTextBody("action", action, ContentType.TEXT_PLAIN)
					.addTextBody("event_submit_do_query", event_submit_do_query, ContentType.TEXT_PLAIN)
					.addTextBody("smPolicy", smPolicy, ContentType.TEXT_PLAIN)
					.addTextBody("smReturn", smReturn, ContentType.TEXT_PLAIN)
					.addTextBody("smApp", smApp, ContentType.TEXT_PLAIN)
					.addTextBody("smCharset", smCharset, ContentType.TEXT_PLAIN)
					.addTextBody("smTag", smTag, ContentType.TEXT_PLAIN)
					.addTextBody("smSign", smSign, ContentType.TEXT_PLAIN)
					.addTextBody("identity", identity, ContentType.TEXT_PLAIN)
					.addTextBody("captcha", captcha, ContentType.TEXT_PLAIN)
					.addTextBody("checkcode", imgCode, ContentType.TEXT_PLAIN)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	public static String getImgeCode(String imgUrl) {
		String code = "";
		try {
			String path = DownloadImg.downLoadImg(imgUrl, "D:/checkCode/", "test.jpg");
			code = Main.imgCode(path);
			File srcfile = new File(path);
			if(!srcfile.exists()){
				srcfile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

}
