package JobSchedule.JdJob;

import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
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
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import JD.BabelCraw;
import Utils.HtmlGenUtils;
import Utils.WebDriverUtils;

public class BabelSchedule{
	public static final String loginUrl = "https://passport.jd.com/new/login.aspx?"
			+ "ReturnUrl=http%3A%2F%2Fbabel.m.jd.com%2Factive%2Foperation%2Fmodule%2Factivity%2Factivity.html";
	private static final String returnUrl = "http%3A%2F%2Fbabel.m.jd.com%2Factive%2Foperation%2Fmodule%2Factivity%2Factivity.html";
	private static final String nloginpwd = "t3ZGfUw9XpOA8mg5UUsoCunfEMuWcJpY8Le1B+PAeKcvvu0HszfjZ7g8+dH16qHnrbQEjp6p1VL3NZP1+CW7yQjPHc8MmFYbIoUpePHkrHwwpPt1O5J2xgXECC9ZbnTrH/AypYA7eRqPW/hSrxE/NoPQ+JMgzS6KukqB5MHEmPc=";
	private static final String eid = "SC3JVQURCKKQ6KLYBZFP5CBT2HRALT4JIIIMRRIJYT7OLMY3BOFO45M6UBTHDEVV7VRNSGWZEZLECCMK6UQXSXD5BE";
	private static final String fp = "8982ff21e0aff3d725d40927ed1a4f63";
	private static final String seqSid = "7388519295701304000";
	private  String loginName;
	private  String password;
	public BabelSchedule(String loginName,String password){
		this.loginName = loginName;
		this.password = password;
	}
	public void getLoginCookie(){
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(loginUrl);
			get.setHeader("Host","passport.jd.com");
			get.setHeader("Referer","http://babel.m.jd.com/active/operation/module/login/index.html");
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			String sa_token = doc.getElementById("sa_token").val();
			String uuid = doc.getElementById("uuid").val();
			String _t = doc.getElementById("token").val();
			String loginType = doc.getElementById("loginType").val();
			String loginName = this.loginName;
			String chkRememberMe = "";
			String authcode = "";
			String pubKey = doc.getElementById("pubKey").val();
			client = HttpClients.createDefault();
			String url = "https://passport.jd.com/uc/loginService?uuid=" + uuid
					+ "&ReturnUrl=" + returnUrl 
					+ "&r=0.6944410667047143&version=2015";
			HttpPost post = new HttpPost(url);
			RequestConfig defaultCofig = RequestConfig.custom().setCookieSpec(CookieSpecs.NETSCAPE).build();
			post.setConfig(defaultCofig);
			HttpEntity bodyEntity = MultipartEntityBuilder.create()
					.addPart("uuid", new StringBody(uuid, ContentType.TEXT_PLAIN))
					.addPart("eid", new StringBody(eid, ContentType.TEXT_PLAIN))
					.addPart("fp", new StringBody(fp, ContentType.TEXT_PLAIN))
					.addPart("_t", new StringBody(_t, ContentType.TEXT_PLAIN))
					.addPart("loginType", new StringBody(loginType, ContentType.TEXT_PLAIN))
					.addPart("loginName", new StringBody(loginName, ContentType.TEXT_PLAIN))
					.addPart("nloginpwd", new StringBody(nloginpwd, ContentType.TEXT_PLAIN))
					.addPart("chkRememberMe", new StringBody(chkRememberMe, ContentType.TEXT_PLAIN))
					.addPart("authcode", new StringBody(authcode, ContentType.TEXT_PLAIN))
					.addPart("pubKey", new StringBody(pubKey, ContentType.TEXT_PLAIN))
					.addPart("sa_token", new StringBody(sa_token, ContentType.TEXT_PLAIN))
					.addPart("seqSid", new StringBody(seqSid, ContentType.TEXT_PLAIN))
					.build();
			post.setEntity(bodyEntity);
			post.setHeader("X-Requested-With","XMLHttpRequest");
			post.setHeader("Host","passport.jd.com");
			//post.setHeader("cookie", cookie);
			post.setHeader("Origin","https://passport.jd.com");
			post.setHeader("Referer",loginUrl);
			post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			post.setHeader("Accept-Encoding","gzip, deflate, br");
			post.setHeader("Accept","text/plain, */*; q=0.01");
			post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			post.setHeader("Connection","keep-alive");
			post.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse postResponse = client.execute(post);
			Header[] headers = postResponse.getHeaders("Set-Cookie");
			for(Header header : headers){
				System.out.println(header.getName() + "~~~~~~~~~" + header.getValue());
			}
			System.out.println(EntityUtils.toString(postResponse.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(String url) throws Exception {
		 WebDriver driver = new WebDriverUtils().getDriver("firefox");
		 //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);//10秒连接超时
		 driver.get(url);
		 driver.manage().window().maximize();
		 BabelCraw.cookie = getLoginCookie(driver);
		 driver.quit();
		 System.out.println(BabelCraw.cookie);
	}
	
	/**京东登陆页面通用方法*/
	public String getLoginCookie(WebDriver driver) {
		 String cookie = "";
		 List<WebElement> list = driver.findElements(By.tagName("a"));
		 for(WebElement we : list){
			 if(we.getText().equals("账户登录")){
				 we.click();
			 }
		 }
		 //找到登陆名的input并输入
		 driver.findElement(By.id("loginname")).clear();
		 driver.findElement(By.id("loginname")).sendKeys(loginName);
		 
		 //找到登陆密码的input并输入
		 driver.findElement(By.id("nloginpwd")).clear();
		 driver.findElement(By.id("nloginpwd")).sendKeys(password);
		 
		 driver.findElement(By.id("loginsubmit")).click();
		 driver.switchTo().defaultContent();
		 
		 try {
			while(true){
				Thread.sleep(300);
				if(!driver.getCurrentUrl().startsWith("https://passport.jd.com/new/login.aspx")){
					Thread.sleep(2000);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 Set<Cookie> cookies = driver.manage().getCookies();
		 for(Cookie coo : cookies){
			 String cookieVal = coo.getName() + "=" + coo.getValue();
			 if(cookie.equals("")){
				 cookie = cookieVal;
			 }else{
				 cookie += ";" + cookieVal;
			 }
		 }
		return cookie;
	}
	public static void main(String[] args) throws Exception{
		new BabelSchedule("一商王海骄","H12J09W88").run(loginUrl);
	}
}
