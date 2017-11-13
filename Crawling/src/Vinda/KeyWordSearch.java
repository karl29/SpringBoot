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
	
	
	/**�ٶȹؼ�������*/
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
	
	/**����ҳ��*/
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

	/**��ȡ�ٶ�ҳ������
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


	/**�ؼ����б�*/
	public static List<String> keyWordList(){
		List<String> list = Arrays.asList(new String[]{"ֽƷ","ֽ��","ά��","������","ޱ��","������","ֽ���",
				"������","�ﱦ��","������","����","��è","������","����","С����","��ҵ����","��ҵ����","�о�����","�±�",
				"���汦","����","������","���׿���","����ӡ","�߽�˿","����","������","ΨƷ��","���","����","���䱦��",
				"������","����","�շ�"});
		return list;
	}
}
