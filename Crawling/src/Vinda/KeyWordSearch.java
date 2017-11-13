package Vinda;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import Utils.HtmlGenUtils;

public class KeyWordSearch {
	
	public static void main(String[] args){
		baiduNews();
	}
	
	
	/**百度关键词新闻*/
	public static void baiduNews(){
		List<String> keyWordList = keyWordList();
		for(String keyWord : keyWordList){
			try {
				int pn = 0;
				while(true){
					String url = "http://news.baidu.com/ns?word="+URLEncoder.encode(keyWord, "utf-8")+"&pn="+pn+"&cl=2&ct=1&tn=news&rn=20&ie=utf-8&bt=0&et=0";
					String htmlCode = getBaiduHtmlCode(url,pn);
					parseHtmlCode(htmlCode);
					pn += 20;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**解析页面*/
	private static void parseHtmlCode(String htmlCode) {
		// TODO Auto-generated method stub
		try {
			Document doc = Jsoup.parse(htmlCode);
			List<Element> elements = doc.getElementById("content_left").getElementsByAttributeValue("class", "result");
			for(Element element : elements){
				String href = element.select("h3").select("a").attr("href");
				String source = element.getElementsByAttributeValue("class", "c-author").text();
				String content = "";
				if(element.getElementsByAttributeValue("class", "c-span18 c-span-last").size() > 0){
					content = element.getElementsByAttributeValue("class", "c-span18 c-span-last").get(0).ownText();
				}else if(element.getElementsByAttributeValue("class", "c-summary c-row ").size() > 0){
					content = element.getElementsByAttributeValue("class", "c-summary c-row ").get(0).ownText();
				}
				System.out.println(href + source);
				System.out.println(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**获取百度页面数据
	 * @param pn */
	private static String getBaiduHtmlCode(String url, int pn) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Host","news.baidu.com");
			if(pn > 0){
				get.setHeader("Referer",url.replace("pn=" + pn, "pn=" + (pn-20)));
			}
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			htmlCode = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(htmlCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}


	/**关键词列表*/
	public static List<String> keyWordList(){
		List<String> list = Arrays.asList(new String[]{"纸品","纸巾","维达","卫生巾","薇尔","轻曲线","纸尿裤",
				"丽贝乐","帮宝适","包大人","添宁","天猫","永富康","政策","小红书","行业报告","行业分析","研究报告","德宝",
				"护舒宝","好奇","老来福","网易考拉","心相印","高洁丝","大王","康尔佳","唯品会","清风","花王","妈咪宝贝",
				"安而康","洁柔","苏菲"});
		return list;
	}
}
