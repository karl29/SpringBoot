package JD;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import database.JDBCConnection;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月19日 下午7:21:52
 *监测sku价格表
 */
public class JDSKIIPrice {
	
	
	public static void main(String[] args){
		//getSkuNameDetail("1335925");
		List<Map<String,String>> dataList = readExcel();
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String sql = "insert into SkiiProductPrice (skuId,skuName,price) values(?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("skuId"));
				pst.setString(2, map.get("itemName"));
				pst.setString(3, map.get("price"));
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
	 * 爬取数据并保存
	 * @param pageSkuList 
	 * */
	public static void crawl(List<Map<String, String>> pageSkuList){
		List<Map<String,String>> mapList = readExcel();
		for(Map<String,String> map : pageSkuList){
			String skuId = map.get("skuId");
			String excelPrice = "";
			//获取固定价格
			for(Map<String,String> excelMap : mapList){
				if(excelMap.get("skuId").equals(skuId)){
					excelPrice = excelMap.get("price");
				}
			}
			map.put("originalPrice", excelPrice);//固定价格(原价)
			String status = "";
			//如果标题有京东超市或者精选
			String skuTitle = getSkuNameDetail(skuId);
			if(!"".equals(skuTitle)){
				status += skuTitle;
			}
			
			//如果有秒杀、超市、plus、精选、降、减、券等关键字出现
			String saleKeyWord = getSkuSaleKeyWord(skuId);
			if(!"".equals(saleKeyWord)){
				if(status.equals("")){
					status += saleKeyWord;
				}else{
					status += "," + saleKeyWord;
				}
			}
			map.put("saleStatus", status);
			String price = getSkuPrice(skuId);
			//如果价格有变动，则通知
			if(!price.equals(excelPrice)){
				
			}
			map.put("price", price);
			map.put("priceDidcrepancy", String.valueOf((Float.valueOf(price) - Float.valueOf(excelPrice))));
		}
	}
	
	/**
	 * 判断商品标题及优惠区域是否出现秒杀、超市、plus、精选、降、减、券关键字
	 * */
	private static String getSkuSaleKeyWord(String skuId) {
		String saleKeyWord = "";
		try {
			String url = "https://cd.jd.com/promotion/v2?callback=jQuery7034736&skuId=" + skuId
					+ "&area=1_72_2839_0&shopId=1000009821&venderId=1000009821"
					+ "&cat=1316%2C1381%2C1391&isCanUseDQ=isCanUseDQ-1&isCanUseJQ=isCanUseJQ-1&_=1508403634940";
			String htmlCode = JDSKIIStock.getStockHtmlCode(url);
			if(htmlCode.indexOf("秒杀") != -1){
				saleKeyWord = "秒杀";
			}else if(htmlCode.indexOf("降") != -1){
				saleKeyWord = "降价";
			}else if(htmlCode.indexOf("减") != -1){
				saleKeyWord = "减价";
			}else if(htmlCode.indexOf("券") != -1){
				saleKeyWord = "减价";
			}else if(htmlCode.indexOf("trueDiscount") != -1 || htmlCode.indexOf("quota") != -1){
				saleKeyWord = "优惠券";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return saleKeyWord;
	}

	/**
	 * 获取sku名称标签
	 * */
	private static String getSkuNameDetail(String skuId) {
		String skuTitle = "";
		String url = "https://item.jd.com/"+skuId+".html";
		String htmlCode = JDSKIIStock.getStockHtmlCode(url);
		Document doc = Jsoup.parse(htmlCode);
		Element el = doc.getElementsByAttributeValue("class", "sku-name").get(0);
		if(el != null && el.select("img") != null && (el.select("img").attr("alt").equals("京东超市") || el.select("img").attr("alt").equals("京东精选"))){
			skuTitle = el.select("img").attr("alt");
			System.out.println("=======11========");
		}
		return skuTitle;
	}



	private static String getSkuPrice(String skuId) {
		String price = "0";
		try {
			String url = "https://p.3.cn/prices/mgets?callback=jQuery2002939"
					+ "&type=1&pdtk=&pduid=15028658449681124666752&pdbp=0"
					+ "&skuIds=J_"+skuId+"&ext=11000000&source=item-pc";
			String htmlCode = JDSKIIStock.getStockHtmlCode(url);
			if(!"".equals(htmlCode)){
				htmlCode = htmlCode.substring(htmlCode.indexOf("[") + 1, htmlCode.indexOf("]"));
				JSONObject json = JSONObject.fromObject(htmlCode);
				price = json.getString("p");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(price);
		return price;
	}

	/**读取excel获取类目列表*/
	public static List<Map<String,String>> readExcel(){
		XSSFWorkbook book = null;
		InputStream ins = null;
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		try {
			ins = new FileInputStream(new File("E:/software/sk-iiSku.xlsx"));
			book = new XSSFWorkbook(ins);
			XSSFSheet sheet = book.getSheetAt(0);
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				String skuId = String.valueOf(row.getCell(0).getNumericCellValue()).replace(".0", "");
				String itemName = row.getCell(1).getStringCellValue();
				String price = String.valueOf(row.getCell(2).getNumericCellValue());
				String href = row.getCell(3).getStringCellValue();
				Map<String,String> map = new HashMap<String, String>();
				map.put("skuId", skuId);
				map.put("itemName", itemName);
				map.put("url", href);
				map.put("price", price);
				System.out.println(skuId + "~~~~~~~" + itemName);
				mapList.add(map);
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
