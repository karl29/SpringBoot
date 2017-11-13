package Utils;


import net.sf.json.JSONObject;

public class ProxyIPUtils {
	public static void main(String[] args){
		getProxyIP();
	}
	
	public static String[] getProxyIP(){
		String[] ipPort = new String[2];
		try {
			Thread.sleep(15000);//隔15秒请求一次，防止接口返回错误
			String url = "http://120.24.91.156:8080/Crawling/ProxyIp";
			String htmlCode = HtmlGenUtils.getHtmlCode(url, "utf-8");
			System.out.println(htmlCode);
			JSONObject json = JSONObject.fromObject(htmlCode);
			if(json.getString("ERRORCODE").equals("0")){
				JSONObject result = json.getJSONObject("RESULT");
				String proxyIp = result.getString("wanIp");
				String proxyPort = result.getString("proxyport");
				ipPort[0] = proxyIp;
				ipPort[1] = proxyPort;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipPort;
	}
}
