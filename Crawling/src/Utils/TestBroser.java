package Utils;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * selenium����ʵ��
 * */
public class TestBroser {
	private static final String loginUrl = "http://cbbs.tmall.com/login";
	private static final String loginName = "jd_567d58332fec9";
	private static final String password = "baojieaijingdong001";
	private static String cookie = "";
	public static void main(String[] args){
		
	}
	public static String getLoginCookie(){
		 System.setProperty("webdriver.firefox.bin", "E:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		 // System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/AppData/Local/Google/Chrome/Application/chrome.exe");
		 WebDriver driver = new FirefoxDriver();
		 //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);//10�����ӳ�ʱ
		 driver.get(loginUrl);
		 driver.manage().window().maximize();
		 List<WebElement> list = driver.findElements(By.tagName("a"));
		 for(WebElement we : list){
			 if(we.getText().equals("�˻���¼")){
				 we.click();
			 }
		 }
		 //�ҵ���½����input������
		 driver.findElement(By.id("loginname")).clear();
		 driver.findElement(By.id("loginname")).sendKeys(loginName);
		 
		 //�ҵ���½�����input������
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
		 driver.quit();
		return cookie;
	}
	// UUAPI uuapi=new UUAPI();
    //String msg=uuapi.setSoftInfo("110585", "49fe9646f0ed45bc94824741cef4eb9b");
    
    //��¼//
    //String key=uuapi.userLogin("ipmacro", "709394");
    //String codeID=uuapi.upload(storeFile.getPath(), "3007", true);
    //code=uuapi.getResult(codeID);
}
