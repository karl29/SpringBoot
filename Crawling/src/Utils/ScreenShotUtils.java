package Utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class ScreenShotUtils {
	public static void main(String[] args){
		try {
			screenShot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void screenShot() throws AWTException, InterruptedException{
		WebDriver driver = new WebDriverUtils().getDriver("firefox");
		driver.manage().window().maximize();
		driver.get("http://pin.aliyun.com/get_img?sessionid=2ec560212c4ee6e1355436f064b44dca&identity=sm-tmallsearch&type=150_40");
		Actions action = new Actions(driver);
		action.contextClick(driver.findElement(By.tagName("img"))).build().perform();
		
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_DOWN);
		Thread.sleep(500);
		robot.keyPress(KeyEvent.VK_DOWN);
		Thread.sleep(500);
		robot.keyPress(KeyEvent.VK_DOWN);
		Thread.sleep(500);
		
		robot.keyRelease(KeyEvent.VK_DOWN);
		Thread.sleep(500);

		robot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(500);
		
		try {
			Runtime.getRuntime().exec("E:/AutoIt3/rightSave/rightSave.exe");
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		driver.quit();
	}
}
