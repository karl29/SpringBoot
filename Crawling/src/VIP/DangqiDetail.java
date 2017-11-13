package VIP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Utils.HtmlGenUtils;


/**档期详情数据爬取*/
public class DangqiDetail {
	public static void main(String[] args){
		List<Map<String, String>> brandList = ProductSaleDetail.getBrandList();
		crawl(brandList);
	}
	
	public static void crawl(List<Map<String, String>> brandList){
		String lastThirtyDay = HtmlGenUtils.getDataTime("yyyy-MM-dd", -30);
		String lastDay = HtmlGenUtils.getDataTime("yyyy-MM-dd", -7);
		try {
			for(Map<String,String> map : brandList){
				String id = map.get("brandStoreSn");
				if(!id.equals("10022315")){
					String name = map.get("brandStoreName");
					List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
					List<Map<String,String>> lineDetailList = ProductSaleDetail.getLineDetailList(map);
					for(Map<String,String> lineMap : lineDetailList){
						lineMap.put("id", id);
						lineMap.put("name", name);
						lineMap.put("lastDay", lastDay);
						String lastSellDay = lineMap.get("lastSellDay");
						if(VipCrawl.checkIsOT(lastSellDay) <= 1){
							try {
								int pageNum = 1;
								boolean hasPage = true;
								while(hasPage){
									String url = "http://compass.vis.vip.com/dangqi/details/getDangqiDetails?"
											+ "callback=jQuery321015009808780058842_1505123841555"
											+ "&brandStoreName="+URLEncoder.encode(name, "utf-8")+"&brandType=" + URLEncoder.encode(lineMap.get("brandType"), "utf-8")
											+ "&brandName=" + URLEncoder.encode(lineMap.get("brandName"), "utf-8")
											+ "&pageSize=20&pageNumber="+pageNum+"&sortColumn=logDate&sortType=1&warehouseName=0&optGroup=0"
											+ "&goodsCnt=0&sumType=1&lv3CategoryFlag=0&optGroupFlag=1&warehouseFlag=1&analysisType=2"
											+ "&dateMode=0&dateType=D&detailType=D&beginDate="+lastThirtyDay+"&endDate="+lastDay+"&_=" + System.currentTimeMillis();
									Map<String,String> headerMap = new HashMap<String, String>();
									headerMap.put("url", url);
									headerMap.put("refererUrl", "http://compass.vis.vip.com/new/dist/web/index.html");
									String htmlCode = VipCrawl.getHtmlCode(headerMap);
									if(!htmlCode.equals("")){
										hasPage = parseProductHtmlCode(htmlCode,lineMap,dataList,pageNum);
									}else{
										hasPage = false;
									}
									pageNum++;
								}
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if(dataList.size() > 0){
						VipDataUtils.saveDangqiDetail(dataList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**解析json数据*/
	public static boolean parseProductHtmlCode(String htmlCode,
			Map<String, String> lineMap, List<Map<String, String>> dataList,
			int pageNum) {
		boolean hasPage = true;
		try {
			System.out.println(htmlCode);
			htmlCode = htmlCode.substring(htmlCode.indexOf("(") + 1, htmlCode.indexOf(")"));
			JSONObject singleResult = JSONObject.fromObject(htmlCode).getJSONObject("singleResult");
			int total = singleResult.getInt("total");
			int totalPage = total/20 + (total%20==0?0:1);//计算出总共有几页数据
			System.out.println(pageNum + "~~~~" + totalPage);
			if(pageNum >= totalPage || totalPage == 0){
				hasPage = false;
			}
			System.out.println(lineMap.get("name") + "~~" + lineMap.get("brandName"));
			JSONArray listJson = singleResult.getJSONArray("list");
			Map<String,String> map = null;
			String brandId = lineMap.get("id");//品牌Id
			String brandName = lineMap.get("name");//品牌名称
			String lastDay = lineMap.get("lastDay").trim();
			for(Object obj : listJson){
				JSONObject itemJson = JSONObject.fromObject(obj);
				
				String avgGoodsAmount = itemJson.getString("avgGoodsAmount");
				String logDate = itemJson.getString("logDate");
				//只需拿前一天的数据就可以
				if(logDate.trim().equals(lastDay)){
					map = new HashMap<String, String>();
					map.put("brandId", brandId);
					map.put("brandName", brandName);
					map.put("dangqiName", itemJson.getString("dangqiName"));
					map.put("activeName", itemJson.getString("activeName"));
					map.put("saleTimeFrom", itemJson.getString("saleTimeFrom"));
					map.put("saleTimeTo", itemJson.getString("saleTimeTo"));
					map.put("dataTime", logDate);
					map.put("optGroup", itemJson.getString("optGroup"));//人群类型
					map.put("warehouseName", itemJson.getString("warehouseName"));//站点
					map.put("onlineStockAmt", itemJson.getString("onlineStockAmt"));//货值
					map.put("onlineStockCnt", itemJson.getString("onlineStockCnt"));//货量
					map.put("avgOrderAmount", itemJson.getString("avgOrderAmount"));//客单价
					map.put("avgGoodsAmount", isNumeric(avgGoodsAmount)?avgGoodsAmount:"0");//件单价
					map.put("userCnt", itemJson.getString("userCnt"));//购买人数
					map.put("orderCnt", itemJson.getString("orderCnt"));//订单数
					map.put("goodsCnt", itemJson.getString("goodsCnt"));//销售量(含拒退)
					map.put("saleCntNoReject", itemJson.getString("saleCntNoReject"));//销售量(不含拒退)
					map.put("salesAmount", itemJson.getString("salesAmount"));//销售额(扣满减含拒退)
					map.put("salesAmountNoCutReject", itemJson.getString("salesAmountNoCutReject"));//销售额(扣满减不含拒退)
					map.put("goodsMoney", itemJson.getString("goodsMoney"));//销售额(含满减含拒退）
					map.put("cutGoodsMoney", itemJson.getString("cutGoodsMoney"));//满减金额
					map.put("uv", itemJson.getString("uv"));
					map.put("uvConvert", itemJson.getString("uvConvert"));
					map.put("ctr", itemJson.getString("ctr"));//档期ctr
					map.put("sellingRatio", itemJson.getString("orderSkuCntSoldOutPercent"));//售卖比
					dataList.add(map);
				}else{
					hasPage = false;
					System.out.println("商品已获取过不需要在获取~~");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			hasPage = false;
		}
		return hasPage;
	}
	
	
	/**判断是否是字数*/
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
