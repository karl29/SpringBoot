package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HtmlGenUtils {
	
	public static final String MBuserAgent = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";
	/**
	 * 根据名称取品牌名称
	 * */
	public static String getBrandName(String name) {
		String brand = "";
		if(name.indexOf("Olay") != -1){
			brand = "Olay";
		}else if(name.indexOf("欧莱雅") != -1){
			brand = "欧莱雅";
		}else if(name.indexOf("韩束") != -1){
			brand = "韩束";
		}else if(name.indexOf("百雀羚") != -1){
			brand = "百雀羚";
		}else if(name.indexOf("sk-2") != -1 || name.indexOf("SK-II") != -1){
			brand = "SK-II";
		}else if(name.indexOf("雅诗兰黛") != -1){
			brand = "雅诗兰黛";
		}else if(name.indexOf("博朗") != -1 && name.indexOf("剃须刀") != -1){
			brand = "博朗";
		}else if(name.indexOf("飞利浦") != -1 && name.indexOf("剃须刀") != -1){
			brand = "飞利浦"; 
		}else if(name.indexOf("飞科") != -1 && name.indexOf("剃须刀") != -1){
			brand = "飞科";
		}else if(name.indexOf("松下") != -1 && name.indexOf("剃须刀") != -1){
			brand = "松下";
		}
		return brand;
	}
	
	public static String getHtmlCode(String url,String code){
		String result = "";
		BufferedReader in = null;
		try {
			code = (code != null && !code.equals(""))?code:"utf-8";
		    URL realUrl = new URL(url);
		    URLConnection connection = realUrl.openConnection();
		    connection.connect();
		    in = new BufferedReader(new InputStreamReader(
		      connection.getInputStream(),code));
		    String line;
		    while ((line = in.readLine()) != null) {
		    	result += line;
		    }
		} catch (Exception e) {
		   System.out.println("发送GET请求出现异常！"+ e);
		   e.printStackTrace();
		}
		finally {
		   try {
		    if (in != null) {
		     in.close();
		    }
		   } catch (Exception e2) {
		    e2.printStackTrace();
		   }
		}
		return result;
	}
	
	public static String getCode(String url,String host){
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader(":authority",host);
			get.setHeader(":method","GET");
			get.setHeader(":scheme","https");
			get.setHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
			CloseableHttpResponse response = client.execute(get);
			htmlCode = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return htmlCode;
	}
	
	/**获取某日日期*/
	public static String getDataTime(String formatType,int day){
		SimpleDateFormat format = new SimpleDateFormat(formatType);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, day);
		
		return format.format(cal.getTime());
	}
	
	/**获取某一月份日期*/
	public static String getMonthTime(String partter, int month) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, month);
		SimpleDateFormat format = new SimpleDateFormat(partter);
		return format.format(cal.getTime());
	}
	
	/**计算两个日期相差的天数
	 * @throws ParseException */
	public static int daysBetween(String partter,String currenDate,String maxDay) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(partter);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(currenDate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(maxDay));
		long time2 = cal.getTimeInMillis();
		long between_days = (time1-time2)/(1000*3600*24);
		return Integer.parseInt(String.valueOf(between_days));
	}
	
	/**
	 * PC随机agent
	 * */
	public static String getRandomUserAgent(){
		List<String> agentList = Arrays.asList(new String[]{
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95",
				"Safari/537.36 OPR/26.0.1656.60",
				"Opera/8.0 (Windows NT 5.1; U; en)",
				"Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
				"Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 ",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 ",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko)",
				"Chrome/10.0.648.133 Safari/534.16",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 ",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 ",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
				"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36"
		});
		return agentList.get(new Random().nextInt(16));
	}
	

	/**
	 * Mobile随机agent
	 * */
	public static String getRandomMobileUserAgent(){
		List<String> agentList = Arrays.asList(new String[]{
				"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
				"JUC (Linux; U; 2.3.7; zh-cn; MB200; 320*480) UCWEB7.9.3.103/139/999",
				"Mozilla/5.0 (Linux; U; Android 5.1; zh-cn; m1 metal Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36",
				"Mozilla/5.0 (Linux; Android 5.1.1; vivo X7 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 baiduboxapp/8.6.5 (Baidu; P1 5.1.1)",
				"Mozilla/5.0 (iPhone 6s; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 MQQBrowser/7.6.0 Mobile/14E304 Safari/8536.25 MttCustomUA/2 QBWebViewType/1 WKType/1",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 10_2 like Mac OS X) AppleWebKit/602.3.12 (KHTML, like Gecko) Mobile/14C92 MicroMessenger/6.5.9 NetType/WIFI Language/zh_CN",
				"Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; HUAWEI MT7-TL00 Build/HuaweiMT7-TL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.3.8.909 Mobile Safari/537.36",
				"Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.5.1.944 Mobile Safari/537.36",
				"Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Mobile Safari/537.36",
				"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Mobile Safari/537.36",
				"Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Mobile Safari/537.36",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
				"Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
		});
		return agentList.get(new Random().nextInt(13));
	}
	public static void main(String[] args){
		/*String htmlCode = getCode("https://sale.jd.com/act/V1OyutLB7lqwzkNC.htmlOyutLB7lqwzkNC.html","sale.jd.com");
		System.out.println(htmlCode);*/
		for(int i=0;i<17;i++){
			getRandomUserAgent();
		}
	}
}
