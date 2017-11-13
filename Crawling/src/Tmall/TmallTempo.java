package Tmall;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.JDBCConnection;
import Utils.HtmlGenUtils;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月31日 下午3:17:40
 *猫超临时需求，爬取预订件数
 */
public class TmallTempo {
	public static void main(String[] args){
		//getSkuList();
		crawl();
	}
	
	public static void crawl(){
		List<String> skuList = getSkuList();
		Map<String,String> maps = new HashMap<String, String>();
		for(String skuId : skuList){
			try {
				String url = "https://detail.m.tmall.com/item.htm?spm=a3204.7408093.7.2.YHf3cU&id=" + skuId;
				String htmlCode = getHtmlCode(url);
				maps.put(skuId, parseTamllHtml(htmlCode));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		saveData(maps);
	}
	
	
	private static void saveData(Map<String, String> maps) {
		try {
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", 0);
			Connection con = JDBCConnection.connectToLocal("vinda");
			String sql = "insert into TmallOrderItemAmount(skuId,dataTime,orderAmount) values(?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			Iterator<Entry<String, String>> it = maps.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, String> entryMap = it.next();
				pst.setString(1, entryMap.getKey());
				pst.setString(2, dataTime);
				pst.setString(3, entryMap.getValue());
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析猫超详情页面
	 * @param tmallMap 
	 * @param itemId 
	 * */
	private static String parseTamllHtml(String htmlCode) throws Exception{
		// TODO Auto-generated method stub
		String orderItemAmount = "";
		if(!"".equals(htmlCode)){
			try {
				htmlCode = htmlCode.substring(htmlCode.indexOf("addressData") - 2,htmlCode.length());
				JSONObject json = JSONObject.fromObject(htmlCode);
				
				JSONObject keyJson = json.getJSONObject("vertical").getJSONObject("presale");
				orderItemAmount = keyJson.getString("orderedItemAmount");
				System.out.println(orderItemAmount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return orderItemAmount;
	}

	
	private static String getHtmlCode(String url) {
		String dataJson = "";
		try {
			HttpEntity entity = null;
			for(int i = 0;i<3;i++){
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				String path = url.substring(url.indexOf("item.htm") - 1,url.length());
				get.setHeader("cookie","l=AtXVF2nTqYa7YJACXa/YGxWXZcqP1oti; hng=CN%7Czh-CN%7CCNY%7C156; t=2836df35359734afbbbe3c90e2e48d6d; tracknick=%5Cu8BD7%5Cu654F%5Cu654F%5Cu654F%5Cu54C8%5Cu54C8%5Cu54C8; _tb_token_=e57fdd13b66eb; cookie2=16dbb23946996203ad1b71a982ce8019; cna=He27ETR6oBICAbcGrW1bj6EO; _m_h5_tk=bc836babf51ba74473155038e7e0c54f_1509443802013; _m_h5_tk_enc=de9ab41dafa62942367efa574b0a163a; sm4=440100; isg=AmFhXF_PnMgv-DBwzOqAPrLdcC27ptb-R-LflMM2fWjHKoH8C17l0I9o-GhX");
				get.setHeader(":authority","detail.m.tmall.com");
				get.setHeader(":path",path);
				get.setHeader(":method","GET");
				get.setHeader(":scheme","https");
				get.setHeader("user-agent", HtmlGenUtils.getRandomMobileUserAgent());
				CloseableHttpResponse response = client.execute(get);
				entity = response.getEntity();
				Thread.sleep((new Random().nextInt(5) + 3) * 1000);
				Document doc = Jsoup.parse(EntityUtils.toString(entity));
				Elements element = doc.select("script");
				for(Element el : element){
					if(el.html().indexOf("_DATA_Mdskip") != -1){
						dataJson = el.html();
					}
				}
				if(!dataJson.equals("")){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataJson;
	}
	
	private static List<String> getSkuList() {
		XSSFWorkbook book = null;
		InputStream ins = null;
		List<String> mapList = new ArrayList<String>();
		try {
			ins = new FileInputStream(new File("E:/监控ID.xlsx"));
			book = new XSSFWorkbook(ins);
			XSSFSheet sheet = book.getSheetAt(0);
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				String skuId = row.getCell(2).getStringCellValue();
				System.out.println(skuId + "~~~~~~~");
				mapList.add(skuId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(ins != null){
					ins.close();
				}
				if(book != null){
					book.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return mapList;
	}
}
