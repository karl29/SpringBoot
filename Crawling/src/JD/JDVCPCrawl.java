package JD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import database.JDBCConnection;
import JobSchedule.JdJob.BabelSchedule;
import Utils.HtmlGenUtils;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月18日 下午7:07:44
 */
public class JDVCPCrawl {
	private static final String LOGIN_URL = "https://passport.jd.com/new/login.aspx?ReturnUrl=https://vcp.jd.com&rs=vc";
	public static void main(String[] args) throws Exception{
		crawl();
	}
	
	public static void crawl() throws Exception{
		//登录获取cookie
		String password = updateLoginPwd();
		new BabelSchedule("一商王海骄",password).run(LOGIN_URL);
		crawlProductStock();
	}
	
	
	
	private static String updateLoginPwd() {
		Connection con = null;
		PreparedStatement pst = null;
		String pwd = "";
		try {
			con = JDBCConnection.connectToServer("braun_jd_campaign");
			String sql = "select platform,password from platformPwd where platform=jdVcp";
			pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				pwd = rs.getString(2);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return pwd;
	}
	/**
	 * 爬取库存销量
	 * */
	private static void crawlProductStock() {
		int[] days = {1,2,3};//查询时间
		Map<Integer,List<Map<String,String>>>  maps = new HashMap<Integer, List<Map<String,String>>>();
		for(int day : days){
			List<Map<String,String>> mapList = maps.get(day);
			if(mapList == null){
				mapList = new ArrayList<Map<String,String>>();
			}
			int page = 1;
			int offset = 0;
			boolean nextPage = true;
			while(nextPage){
				String url = "https://vcp.jd.com/sub_reports/other/dayReport";
				String refererUrl = "https://vcp.jd.com/sub_reports/other/dayReport?pager.offset=0&pageNo=1";
				if(page > 1){
					url += "?pager.offset="+offset+"&pageNo=" + page;
					refererUrl = "https://vcp.jd.com/sub_reports/other/dayReport?pager.offset="+(offset - 20)+"&pageNo=" + (page -1);
				}
				String htmlCode = getHtmlCode(url,refererUrl,day);
				if(!htmlCode.equals("")){
					nextPage = parseHtml(htmlCode,mapList);
				}else{
					nextPage = false;
				}
				page++;
				offset+=20;
			}
			maps.put(day, mapList);
		}
		saveData(maps,JDBCConnection.connectToLocal("skii"));
		
		saveData(maps,JDBCConnection.connectToServer("skii"));
	}
	
	/**
	 * 保存到数据库
	 * */
	private static void saveData(Map<Integer, List<Map<String, String>>> maps,Connection con) {
		try {
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
			String sql = "insert into VCPStockDayReport(dataTime,skuId,itemName,brandName,status,price,totalSell,totalStock,totalOrder,days)"
					+ " values(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(int days : maps.keySet()){
				List<Map<String, String>> mapList = maps.get(days);
				for(Map<String,String> map : mapList){
					pst.setString(1, dataTime);
					pst.setString(2, map.get("skuId"));
					pst.setString(3, map.get("itemName"));
					pst.setString(4, map.get("brandName"));
					pst.setString(5, map.get("status"));
					pst.setString(6, map.get("price"));
					pst.setString(7, map.get("totalSell"));
					pst.setString(8, map.get("totalStock"));
					pst.setString(9, map.get("totalOrder"));
					pst.setInt(10, days);
					pst.addBatch();
				}
			}
			pst.executeBatch();
			System.out.println("插入库存销量数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析获取到的页面
	 * */
	private static boolean parseHtml(String htmlCode,List<Map<String,String>> mapList) {
		// TODO Auto-generated method stub
		boolean nextPage = false;
		try {
			Document doc = Jsoup.parse(htmlCode);
			List<Element> staticRow = doc.getElementById("staticRow").getElementsByTag("tbody").select("tr");//商品名称列表
			List<Element> dynamicRow = doc.getElementById("dynamicRow").getElementsByTag("tbody").select("tr");
			List<Element> pageList = doc.getElementsByAttributeValue("class", "fl pageNums mr10").select("a");
			for(Element el : pageList){
				if(el.text().equals("下一页")){
					nextPage = true;
				}
			}
			Map<String,String> map = null;
			for(int i = 0;i<staticRow.size();i++){
				try {
					map = new HashMap<String, String>();
					String skuId = staticRow.get(i).getElementsByTag("td").get(0).text();
					map.put("skuId", skuId);
					String itemName = staticRow.get(i).getElementsByTag("td").get(1).select("div").attr("title");
					map.put("itemName", itemName);
					String brandName = dynamicRow.get(i).getElementsByTag("td").get(0).text();
					map.put("brandName", brandName);
					String status = dynamicRow.get(i).getElementsByTag("td").get(1).text();
					map.put("status", status);
					String price = dynamicRow.get(i).getElementsByTag("td").get(2).text();
					map.put("price", price);
					String totalSell = dynamicRow.get(i).getElementsByTag("td").get(3).text();
					map.put("totalSell", totalSell);
					String totalStock = dynamicRow.get(i).getElementsByTag("td").get(4).text();
					map.put("totalStock", totalStock);
					String totalOrder = dynamicRow.get(i).getElementsByTag("td").get(5).text();
					map.put("totalOrder", totalOrder);
					mapList.add(map);
					System.out.println(skuId + "\n" + itemName + "\n" 
					+ brandName + "\n" + status + "\n" + price + "\n" 
							+ totalSell + "\n" + totalStock + "\n" + totalOrder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextPage;
	}

	private static String getHtmlCode(String url, String refererUrl, int day) {
		String htmlCode = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity bodyEntity = MultipartEntityBuilder.create()
					.addPart("productName", new StringBody("", ContentType.TEXT_PLAIN))
					.addPart("_brandCodes", new StringBody("1", ContentType.TEXT_PLAIN))
					.addPart("_productCategorys", new StringBody("1", ContentType.TEXT_PLAIN))
					.addPart("productStatus", new StringBody("-1", ContentType.TEXT_PLAIN))
					.addPart("days", new StringBody(day + "", ContentType.TEXT_PLAIN))
					.addPart("productCodes", new StringBody("", ContentType.TEXT_PLAIN))
					.build();
			post.setHeader("Cookie",BabelCraw.cookie);
			post.setHeader("Host","vcp.jd.com");
			post.setHeader("Origin","https://vcp.jd.com");
			post.setHeader("Referer",refererUrl);
			post.setEntity(bodyEntity);
			CloseableHttpResponse response = client.execute(post);
			htmlCode = EntityUtils.toString(response.getEntity(), "utf-8");
			Thread.sleep(new Random().nextInt(5) * 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlCode;
	}
}
