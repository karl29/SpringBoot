package JD;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import Utils.ExcelUtils;
import Utils.HtmlGenUtils;

public class JShopCrawl implements Runnable{
	public static String cookie = "user-key=01ce6787-92c1-462f-8640-576d2d7b7249; cn=0; ipLocation=%u5E7F%u4E1C; areaId=19; ipLoc-djd=19-1601-3633-0; mobilev=html5; VC_INTEGRATION_JSESSIONID=40aad674-d61f-407f-9c55-67e84f1fdd74; __jdv=122270672|iosapp|t_335139774|appshare|Wxfriends|1510048634441; sid=f9c437542270df5565ea4c489e62968b; mba_muid=15028658449681124666752; wlfstk_smdl=34jx2lxeie5rti9r1jxabce1k37m6h1a; _jrda=2; _jrdb=1510326267355; 3AB9D23F7A4B3C9B=ZCB4RWNSQDOYUVNILXJA6QF7F5O6KT4YVR4BWKPHMCGYLOS77IVHSQ6A2GZE2EIFTBADAHJ6BB5UBLO6B5KI6HAGHU; TrackID=1RNUxOIgOCaMGHV0KAyjtc8Ryc6o1q91ZJGHGf77wZZjob630EWUR9qrhDamTv-cwx3ddw6uXUzMen1M3VOlo4G8j6ISRujETCIsw_fic3nQ; pinId=ZjvH--N0waE; pin=jdbjsk2; unick=SKII%E5%AE%98%E6%96%B9%E6%97%97%E8%88%B0%E5%BA%97; thor=F84DB8C84F43F29089544343F1CA6FB0E219720CD425D2CE2B1EB6825756AEE80DA18C280FCCD79835258AFD8E22578A9627D7DCD6C7B48067165E283D2D2B4AE77832840E4A05C329C77A23CCD9FB4520584A5D762BAA305F8521718BF0508AF9CD5047162087AA9291D87B459249AD16B8851A38F6D930CF051E215A2E8A889406005860872F71488DC03207AF2444; _tp=XLQz5I6tyz7Qh3RsAwYg2Q%3D%3D; logining=1; _pst=jdbjsk2; ceshi3.com=000; _jshop_lc_=ILVZDMOQYEYTUPZ24MW32RI32PA76XETOXMQORHHSFNHD4LDPYFO6OWNLHUBT45MVCE3MKRHQSQIM; _data_lc=ILVZDMOQYEYTUPZ24MW32RI32PA76XETOXMQORHHSFNHD4LDPYFO6OWNLHUBT45MVCE3MKRHQSQIM; __jda=105183510.15028658449681124666752.1502865844.1510303386.1510326267.56; __jdb=105183510.3.15028658449681124666752|56.1510326267; __jdc=105183510; __jdu=15028658449681124666752; JSESSIONID=EE8457D0E59FCFFCBE139A2DA39E4C2A.s1";
	private String dataTime;
	private List<Map<String,String>> mapList;
	private String name;
	public static void main(String[] args){
		crawl();
	}
	
	public JShopCrawl(String dataTime,List<Map<String,String>> mapList,String name){
		this.dataTime = dataTime;
		this.mapList = mapList;
		this.name = name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.name.equals("redirectDetail")){
			JshopPCDetail.crawRedirectDetail(mapList, dataTime);
		}else if(this.name.equals("refererDetail")){
			JshopPCDetail.crawlRefererDetail(mapList, dataTime);
		}else if(this.name.equals("pageDetail")){
			JshopPCDetail.crawlPageDetail(mapList, dataTime);
		}else if(this.name.equals("goDetail")){
			JShopMODetail.crawlGoDetail(mapList, dataTime);
		}else if(this.name.equals("comeDetail")){
			JShopMODetail.crawlComeDetail(mapList, dataTime);
		}else if(this.name.equals("pageDetail")){
			JShopMODetail.crawlPageDetail(mapList, dataTime);
		}else if(this.name.equals("decorate")){
			JshopDecorate.crawl(mapList, dataTime);
		}
	}
	
	/**开线程爬取*/
	public static void crawl(){
		List<Map<String,String>> mapList = ExcelUtils.getTaskList("Jshop");
		List<String> list = Arrays.asList(new String[]{"redirectDetail","refererDetail"
				,"pageDetail","goDetail","comeDetail","pageDetail","decorate"});
		String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
		System.out.println(dataTime + "=============================");
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for(int i = 0;i<list.size();i++){
			String strName = list.get(i);
			pool.execute(new JShopCrawl(dataTime,mapList,strName));
		}
		pool.shutdown();
		try {
			while(true){
				if(pool.isTerminated()){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	/**页面数据*/
	static String getDetailHtmlCode(String url, String refererUrl) {
		String htmlCode = "";
		try {
			for(int i = 0;i<3;i++){
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setHeader("Cookie",cookie);
				get.setHeader("Host","data-jshop.jd.com");
				get.setHeader("Referer",refererUrl);
				get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
				CloseableHttpResponse rsp = client.execute(get);
				htmlCode = EntityUtils.toString(rsp.getEntity(), "utf-8");
				if(!htmlCode.equals("")){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}
	

	/**
	 * 根据不同的json数据key获取终端
	 * */
	public static String getTerminal(String visitInfo) {
		String  terminal = "PC";
		if(visitInfo.indexOf("mo") != -1){
			terminal = "Mobile";
		}
		return terminal;
	}

	
	/**获取实时数据pc活动json数据*/
	public static String getRealTimeHtmlCode(String url){
		String htmlCode = "";
		//String refererUrl = "http://data-jshop.jd.com/pc/realTime/queryPcPageMonitorBaseInfo.html?appId=" + projectId;
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			get.setHeader("Cookie",cookie);
			get.setHeader("Host","data-jshop.jd.com");
			//get.setHeader("Referer",refererUrl);
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse rsp = client.execute(get);
			htmlCode = EntityUtils.toString(rsp.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(htmlCode);
		return htmlCode;
	}
	

	/**是否还有页数*/
	public static boolean equalPageIndex(String pageDetailCode, int index) {
		boolean hasPage = true;
		int totalPage = JSONObject.fromObject(pageDetailCode).getInt("totalPage");
		if(totalPage == index || totalPage == 0){
			hasPage = false;
		}
		return hasPage;
	}

}
