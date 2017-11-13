package JobSchedule.TmallJob;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import JD.JShopCrawl;
import JD.JshopDecorate;
import JobSchedule.JdJob.BabelSchedule;
import Tmall.TmallTempo;
import Utils.ExcelUtils;
import Utils.HtmlGenUtils;
import Utils.WebDriverUtils;

public class MaochaoJob implements Job {
	public static void main(String[] args) throws JobExecutionException{
		new MaochaoJob().execute(null);
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//TmallSearchResult.crawlSearchList();
		/*loginJshop();//µÇÂ½Æ½Ì¨
		JShopCrawl.crawl();*/
		//TmallSearchResult.crawlSearchList();
		List<Map<String,String>> mapList = ExcelUtils.getTaskList("Jshop");
		JshopDecorate.crawl(mapList,HtmlGenUtils.getDataTime("yyyy-MM-dd", 0));
	}
	
	
	/**µÇÂ½jshop*/
	public static void loginJshop() {
		// TODO Auto-generated method stub
		try {
			WebDriver driver = new WebDriverUtils().getDriver("firefox");
			driver.manage().window().maximize();
			driver.get("https://passport.jd.com/new/login.aspx?ReturnUrl=http%3A%2F%2Fjshop.jd.com/");
			JShopCrawl.cookie = new BabelSchedule("jd_567d58332fec9","baojieaijingdong001").getLoginCookie(driver);
			System.out.println(JShopCrawl.cookie);
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
