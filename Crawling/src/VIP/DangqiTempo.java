package VIP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import Utils.HtmlGenUtils;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年11月2日 下午2:18:31
 *爬取档期按日汇总维度数据
 */
public class DangqiTempo {
	public static void main(String[] args){
		List<Map<String, String>> brandList = ProductSaleDetail.getBrandList();
		crawl(brandList);
	}
	
	
	public static void crawl(List<Map<String, String>> brandList){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for(Map<String,String> map : brandList){
			String id = map.get("brandStoreSn");
			if(id.equals("10000828")){
				String name = map.get("brandStoreName");
				List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
				List<Map<String,String>> lineDetailList = ProductSaleDetail.getLineDetailList(map);
				for(Map<String,String> lineMap : lineDetailList){
					try {
						String firstSellDay = lineMap.get("firstSellDay");
						if(format.parse(firstSellDay).getTime() > format.parse("2016-10-01").getTime() 
								&& format.parse(firstSellDay).getTime() < format.parse("2017-06-30").getTime()){
							System.out.println("档期首日：" + firstSellDay);
							lineMap.put("id", id);
							lineMap.put("name", name);
							int pageNum = 1;
							boolean hasPage = true;
							while(hasPage){
								String url = "http://compass.vis.vip.com/dangqi/details/getDangqiDetails?"
										+ "callback=jQuery32105713824183496492_1509605334229"
										+ "&brandStoreName="+URLEncoder.encode(name, "utf-8")+"&brandType=" + URLEncoder.encode(lineMap.get("brandType"), "utf-8")
										+ "&brandName=" + URLEncoder.encode(lineMap.get("brandName"), "utf-8")
										+ "&pageSize=20&pageNumber=1&sortColumn=logDate&sortType=1&warehouseName=0&optGroup=0"
										+ "&goodsCnt=0&sumType=1&lv3CategoryFlag=0&optGroupFlag=0&warehouseFlag=0&analysisType=1"
										+ "&dateMode=0&dateType=&detailType=&beginDate=&endDate=&_=" + + System.currentTimeMillis();
								Map<String,String> headerMap = new HashMap<String, String>();
								headerMap.put("url", url);
								headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
								String htmlCode = VipCrawl.getHtmlCode(headerMap);
								if(!htmlCode.equals("")){
									hasPage = DangqiDetail.parseProductHtmlCode(htmlCode,lineMap,dataList,pageNum);
								}else{
									hasPage = false;
								}
								pageNum++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					writeExecel(dataList);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * 写excel
	 * @throws FileNotFoundException 
	 * */
	private static void writeExecel(List<Map<String, String>> dataList) throws FileNotFoundException {
		// TODO Auto-generated method stub
		HSSFWorkbook book = new HSSFWorkbook();
		FileOutputStream fileOut = new FileOutputStream("E:/software/Olay.xls");
		HSSFSheet sheet = book.createSheet();
		
		sheet.setDefaultColumnWidth(20);
		HSSFRow row = sheet.createRow(0);
		//文件头
		String[] headers = new String[]{"brandId","brandName","dangqiName","activeName","saleTimeFrom","saleTimeTo",
				"dataTime","optGroup","warehouseName","onlineStockAmt","onlineStockCnt","avgOrderAmount","avgGoodsAmount",
				"userCnt","orderCnt","goodsCnt","saleCntNoReject","salesAmount","salesAmountNoCutReject","goodsMoney",
				"cutGoodsMoney","uv","uvConvert","ctr","sellingRatio"};
		for(int i = 0;i<headers.length;i++){
			HSSFCell cell = row.createCell(i);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		
		for(int i = 0;i<dataList.size();i++){
			Map<String, String> map = dataList.get(i);
			row = sheet.createRow(i + 1);
			//int j = 0;
			for(int j = 0;j<headers.length;j++){
				String objValue = map.get(headers[j]);
				row.createCell(j).setCellValue(objValue);
			}
		}
		
		try {
			book.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
