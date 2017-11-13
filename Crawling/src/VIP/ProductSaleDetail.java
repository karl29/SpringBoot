package VIP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Utils.HtmlGenUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**��Ʒ��������*/
public class ProductSaleDetail {
	public  static void main(String[] args){
		List<Map<String, String>> brandList = getBrandList();
		String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
		crawl(brandList,dataTime);
	}
	
	
	/**��Ʒ�����Ʒ���б�
	 * ��Ʒ��-�ۺϷ��������ݲ�һ��
	 * */
	public static List<Map<String, String>> getBrandList() {
		List<Map<String,String>> brandList = null;
		try {
			String url = "http://compass.vis.vip.com/newRealTime/comm/getBrandStore?callback=jQuery321046216827862594245_1504692645341&vendorCode=105209&_=" + System.currentTimeMillis();
			Map<String,String> headerMap = new HashMap<String, String>();
			headerMap.put("url", url);
			headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
			String htmlCode = VipCrawl.getHtmlCode(headerMap);
			if(!htmlCode.equals("")){
				brandList = parseBrandHtmlCode(htmlCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*brandList = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("brandStoreSn", "10000690");
		map.put("brandStoreName", "ɳ��Vidal Sassoon");
		brandList.add(map);*/
		return brandList;
	}
	
	/**
	 * ����Ʒ���б�����
	 * */
	private static List<Map<String, String>> parseBrandHtmlCode(String htmlCode) {
		List<Map<String,String>> brandList = new ArrayList<Map<String,String>>();
		try {
			htmlCode = htmlCode.substring(htmlCode.indexOf("(") + 1, htmlCode.indexOf(")"));
			JSONArray multipleReult = JSONObject.fromObject(htmlCode).getJSONArray("singleResult");
			Map<String,String> map = null;
			for(Object obj : multipleReult){
				JSONObject itemJson = JSONObject.fromObject(obj);
				map = new HashMap<String, String>();
				map.put("brandStoreSn", itemJson.getString("brandStoreSn"));
				map.put("brandStoreName", itemJson.getString("brandStoreName"));
				brandList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brandList;
	}


	/**����Ʒ������*/
	public static void crawl(List<Map<String, String>> brandList,
			String dataTime) {
		// TODO Auto-generated method stub
		try {
			List<Map<String,String>> dataList = null;
			List<Map<String,String>> lineDetailList = null;
			for(Map<String,String> map : brandList){
				String id = map.get("brandStoreSn");
				String name = map.get("brandStoreName");
				if(!id.equals("10022315")){
					System.out.println(name);
					lineDetailList = getLineDetailList(map);
					for(Map<String,String> lineMap : lineDetailList){
						dataList = new ArrayList<Map<String,String>>();
						lineMap.put("id", id);
						lineMap.put("name", name);
						lineMap.put("lastDay", dataTime);
						String lastSellDay = lineMap.get("lastSellDay");
						//�õ��ڵ�������ڸ���ǰʱ��Ƚϣ����С�ڵ�ǰ���ڣ���������
						//�������һ���������Ҳ��Ҫ��
						if(VipCrawl.checkIsOT(lastSellDay) <= 1){
							try {
								Map<String,String> headerMap = null;
								String url = "http://compass.vis.vip.com/newGoods/details/downloadGoodsDetails?"
										+ "brandStoreName="+URLEncoder.encode(name, "utf-8")+"&goodsCode="
										+ "&filter=onSaleStockAmt%2CuserCnt%2CgoodsCnt%2CgoodsAmt%2CsellingRatio%2Cuv"
										+ "%2Cconversion%2ConSaleStockCnt%2CgoodsCntWithoutReturn%2CgoodsMoney%2CgoodsAmtWithoutReturn"
										+ "%2CgoodsCtr%2CbrandGoodsAvgCtr&pageSize=20&pageNumber=1&sortColumn=goodsAmt&sortType=1"
										+ "&warehouseName=0&optGroup=0&goodsCnt=0&beginDate="+lineMap.get("firstSellDay")+"&endDate="+lineMap.get("lastSellDay")
										+ "&brandName="+URLEncoder.encode(lineMap.get("brandName"), "utf-8")
										+ "&sumType=1&goodsType=0&optGroupFlag=1&warehouseFlag=1&analysisType=1"
										+ "&brandType="+URLEncoder.encode(lineMap.get("brandType"), "utf-8");
								/*String url = "http://compass.vis.vip.com/newGoods/details/getDetails?callback=jQuery32109690993150970459_1504690291931"
										+ "&brandStoreName="+URLEncoder.encode(name, "utf-8")+"&goodsCode=&pageSize=200&pageNumber="+pageNum+"&sortColumn=goodsAmt"
												+ "&sortType=1&warehouseName=0&optGroup=0&goodsCnt=0"
												+ "&beginDate="+lineMap.get("firstSellDay")+"&endDate="+lineMap.get("lastSellDay")
												+ "&brandName="+URLEncoder.encode(lineMap.get("brandName"), "utf-8")+"&sumType=1&goodsType=0&optGroupFlag=1&warehouseFlag=1"
												+ "&analysisType=1&brandType="+URLEncoder.encode(lineMap.get("brandType"), "utf-8")+"&_=" + System.currentTimeMillis();*/
								headerMap = new HashMap<String, String>();
								headerMap.put("url", url);
								headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
								File file = getHtmlCode(headerMap);
								readExcelData(file,lineMap,dataList);
								if(file.exists()){
									file.delete();
								}
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(dataList.size() > 0){
								VipDataUtils.productData(dataList);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**������Ʒ����*/
	private static void readExcelData(File file,
			Map<String, String> lineMap, List<Map<String, String>> dataList) {
		Workbook book = null;
		try {
			try {
				book = new XSSFWorkbook(file);
			} catch (Exception e) {
				book = new HSSFWorkbook(new FileInputStream(file));
			}
			Sheet sheet = book.getSheetAt(0);
			String brandId = lineMap.get("id");//Ʒ��Id
			String brandName = lineMap.get("name");//Ʒ������
			String brandStoreName = lineMap.get("brandName");//��������
			System.out.println(brandStoreName + "============");
			String firstSellDay = lineMap.get("firstSellDay");//���ڿ�ʼ����
			String lastSellDay = lineMap.get("lastSellDay");//���ڽ�������
			Map<String,String> map = null;
			String lastDate = lineMap.get("lastDay");
			for(int i = 1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				String logDate = row.getCell(5).getStringCellValue();
				//ֻ����ǰһ������ݾͿ���
				if(logDate.trim().equals(lastDate.trim())){
					String uv = row.getCell(18).getStringCellValue();
					map =  new HashMap<String, String>();
					map.put("brandId", brandId);
					map.put("brandName", brandName);
					map.put("brandStoreName", brandStoreName);
					map.put("firstSellDay", firstSellDay);
					map.put("lastSellDay", lastSellDay);
					map.put("dataTime", logDate);
					map.put("productName", row.getCell(2).getStringCellValue());
					map.put("productCode", row.getCell(0).getStringCellValue());
					map.put("price", row.getCell(1).getStringCellValue());
					map.put("hotType", row.getCell(4).getStringCellValue());//������
					map.put("picUrl", row.getCell(8).getStringCellValue());
					map.put("lv3Category", row.getCell(3).getStringCellValue());//����Ʒ������
					map.put("optGroup", row.getCell(6).getStringCellValue());//��Ⱥ����
					map.put("warehouseName", row.getCell(7).getStringCellValue());//վ������
					map.put("onSaleStockAmt", row.getCell(9).getStringCellValue());//��ֵ
					map.put("onSaleStockCnt", row.getCell(10).getStringCellValue());//����
					map.put("userCnt", row.getCell(11).getStringCellValue());//��������
					map.put("goodsCnt", row.getCell(12).getStringCellValue());//������(������)
					map.put("goodsCntWithoutReturn", row.getCell(13).getStringCellValue());//������(��������)
					map.put("goodsMoney", row.getCell(14).getStringCellValue());//���۶�(������������)
					map.put("goodsAmt", row.getCell(15).getStringCellValue());//���۶�(������������)
					map.put("goodsAmtWithoutReturn", row.getCell(16).getStringCellValue());//���۶�(��������������)
					String sellingRatio = row.getCell(17).getCellType()==Cell.CELL_TYPE_NUMERIC?String.valueOf(row.getCell(17).getNumericCellValue()):row.getCell(17).getStringCellValue();
					map.put("sellingRatio", sellingRatio);//������(���۶�)
					map.put("uv", uv);
					String conversion = row.getCell(19).getCellType()==Cell.CELL_TYPE_NUMERIC?String.valueOf(row.getCell(19).getNumericCellValue()):row.getCell(19).getStringCellValue();
					map.put("conversion", conversion);//ת����
					String goodsCtr = row.getCell(20).getCellType()==Cell.CELL_TYPE_NUMERIC?String.valueOf(row.getCell(20).getNumericCellValue()):row.getCell(20).getStringCellValue();
					map.put("goodsCtr", goodsCtr);//��Ʒctr
					String brandGoodsAvgCtr = row.getCell(21).getCellType()==Cell.CELL_TYPE_NUMERIC?String.valueOf(row.getCell(21).getNumericCellValue()):row.getCell(21).getStringCellValue();
					map.put("brandGoodsAvgCtr", brandGoodsAvgCtr);//��������Ʒƽ��CTR
					System.out.println(row.getCell(2).getStringCellValue() + "uv===========" + uv);
					dataList.add(map);
				}else{
					System.out.println("��Ʒ�ѻ�ȡ������Ҫ�ڻ�ȡ~~");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**Ʒ�ƶ�Ӧ�ĵ���*/
	public static List<Map<String, String>> getLineDetailList(
			Map<String, String> map) {
		List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
		try {
			String url = "http://compass.vis.vip.com/dangqi/details/queryTimeLineDetail?callback=jQuery32109690993150970459_1504690291931"
					+ "&brandStoreName="+URLEncoder.encode(map.get("brandStoreName"), "utf-8")+"&_=" + System.currentTimeMillis();
			Map<String,String> headerMap = new HashMap<String, String>();
			headerMap.put("url", url);
			headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
			String htmlCode = VipCrawl.getHtmlCode(headerMap);
			if(!htmlCode.equals("")){
				parseHtmlCode(htmlCode,dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
	/**�������ڵ�json����*/
	private static void parseHtmlCode(String htmlCode,
			List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			htmlCode = htmlCode.substring(htmlCode.indexOf("(") + 1, htmlCode.indexOf(")"));
			JSONArray singleResult = JSONObject.fromObject(htmlCode).getJSONArray("singleResult");
			Map<String,String> map = null;
			for(Object obj : singleResult){
				JSONObject itemJson = JSONObject.fromObject(obj);
				map = new HashMap<String, String>();
				String brandName = itemJson.getString("brandName");
				String brandType = itemJson.getString("brandType");
				String firstSellDay = itemJson.getString("firstSellDay");
				String lastSellDay = itemJson.getString("lastSellDay");
				map.put("brandName", brandName);
				map.put("brandType", brandType);
				map.put("firstSellDay", firstSellDay);
				map.put("lastSellDay", lastSellDay);
				dataList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**��ȡ��Ӧ��ҳ������*/
	public static File getHtmlCode(Map<String, String> headerMap) {
		File file = null;
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(headerMap.get("url"));
			get.setHeader("cookie",VipCrawl.COOKIE);
			get.setHeader("Host","compass.vis.vip.com");
			get.setHeader("Referer",headerMap.get("refererUrl"));
			get.setHeader("User-Agent",HtmlGenUtils.getRandomUserAgent());
			CloseableHttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream ins = entity.getContent();
			file = new File("E:/software/vipProduct.xlsx");
			FileOutputStream out = new FileOutputStream(file);
			byte[] bs = new byte[10*1024];
			int ch = 0;
			while((ch = ins.read(bs)) != -1){
				out.write(bs, 0, ch);
			}
			ins.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
