package Utils;

/**
 * webDriver工具类
 * */
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class WebDriverUtils {
	private volatile WebDriver webDriver;
	private volatile WebDriver proxyDriver;//使用代理id的driver
	public static final String DIRVER_DIR = "E:/Mozilla Firefox/firefox.exe";
	private static final String CHROME_DIRVER_EXPLOR = "C:/Users/Data/AppData/Local/Google/Chrome/Application/chrome.exe";
	private static final String CHROME_DIRVER = "E:/software/Chrome/chromedriver_copy.exe";
	public static void main(String[] args){
		WebDriver driver = new WebDriverUtils().getDriver("firefox");
		driver.get("http://www.ip.cn/?kajsps=47nsx2");
		driver.manage().window().maximize();
	}
	
	public WebDriver getProxyDriver(){
		if(proxyDriver == null){
			synchronized (proxyDriver) {
				if(proxyDriver == null){
					try {
						System.setProperty("webdriver.firefox.bin", DIRVER_DIR);
						FirefoxProfile profile = new FirefoxProfile();
						
						String[] ipPort = ProxyIPUtils.getProxyIP();
						profile.setPreference("network.proxy.type", 1);// 默认值0，就是直接连接；1就是手工配置代理。  
						//http协议代理配置  
						profile.setPreference("network.proxy.http", ipPort[0]);  
						profile.setPreference("network.proxy.http_port", Integer.valueOf(ipPort[1]));  
						//所有协议公用一种代理配置，如果单独配置，这项设置为false，再类似于http的配置  
						profile.setPreference("network.proxy.share_proxy_settings", true);  
						//对于localhost的不用代理，这里必须要配置，否则无法和webdriver通讯  
						profile.setPreference("network.proxy.no_proxies_on", ipPort[0]);  
						profile.setPreference("network.proxy.ssl", ipPort[0]);
						profile.setPreference("network.proxy.ssl_port", Integer.valueOf(ipPort[1])); 
						proxyDriver = new FirefoxDriver(profile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return proxyDriver;
	}
	public WebDriver getDriver(String exploerType){
		if(webDriver == null){
			synchronized (WebDriverUtils.class) {
				if(webDriver == null){
					try {
						//webdriver.chrome.driver
						if("firefox".equals(exploerType)){
							System.setProperty("webdriver.firefox.bin", DIRVER_DIR);
							webDriver = new FirefoxDriver();
						}else{
							//System.setProperty("webdriver.chrome.bin", CHROME_DIRVER_EXPLOR);
							System.setProperty("webdriver.chrome.driver",CHROME_DIRVER);
							ChromeOptions co = new ChromeOptions();
							co.setBinary(CHROME_DIRVER_EXPLOR);
							webDriver = new ChromeDriver(co);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return webDriver;
	}
	
	/**等待页面加载完成*/
	public static void waitForPageLoad(final WebDriver driver){
		Function<WebDriver, Boolean> waitFn = new Function<WebDriver, Boolean>() {

			@Override
			public Boolean apply(WebDriver arg0) {
				// TODO Auto-generated method stub
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
			}
		};
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(waitFn);
	}
}
