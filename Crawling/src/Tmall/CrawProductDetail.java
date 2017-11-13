package Tmall;


import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawProductDetail {
	private static String cookie = "";
	public static void main(String[] args){
		String url = "https://chaoshi.detail.tmall.com/item.htm?id=12246857606&rewcatid=2";
		crawlingTmall(url,"12246857606",null);
	}
	
	
	/**
	 * ≈¿√®≥¨œÍ«È“≥√Ê
	 * @param tmallMap 
	 * */
	public static void crawlingTmall(String url,String itemId, Map<String, String> tmallMap) {
		try {
			HttpEntity entity = null;
			for(int i = 0;i<3;i++){
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				String path = url.substring(url.indexOf("item.htm") - 1,url.length());
				get.setHeader("cookie",cookie);
				get.setHeader(":authority","detail.m.tmall.com");
				get.setHeader(":path",path);
				get.setHeader(":scheme","https");
				get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");
				CloseableHttpResponse response = client.execute(get);
				entity = response.getEntity();
				
				Document doc = Jsoup.parse(EntityUtils.toString(entity));
				String name = doc.getElementById("s-title").select("h1").text();
				tmallMap.put("name", name);
				System.out.println(name);
				Elements element = doc.select("script");
				String dataJson = "";
				for(Element el : element){
					if(el.html().indexOf("_DATA_Mdskip") != -1){
						dataJson = el.html();
					}
				}
				if(!"".equals(dataJson) && dataJson.indexOf("window.location.href") != -1){
					cookie = TmallLogin.login();
					continue;
				}else{
					break;
				}
			}
			parseTamllHtml(entity,url,itemId,tmallMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ω‚Œˆ√®≥¨œÍ«È“≥√Ê
	 * @param tmallMap 
	 * @param itemId 
	 * */
	private static void parseTamllHtml(HttpEntity entity, String detailUrl,String itemId, Map<String, String> tmallMap) throws Exception{
		// TODO Auto-generated method stub
		String url = "https://mdskip.taobao.com";
		
		String path = "/core/initItemDetail.htm?isPurchaseMallPage=false"
				+ "&household=false&isUseInventoryCenter=true&cartEnable=true&isAreaSell=true"
				+ "&sellerPreview=false&isSecKill=false&cachedTimestamp="+System.currentTimeMillis()+"&service3C=false"
				+ "&tryBeforeBuy=false&queryMemberRight=true&itemId="+itemId+"&isForbidBuyItem=false"
				+ "&offlineShop=false&isRegionLevel=true&showShopProm=false&tmallBuySupport=true&isApparel=false"
				+ "&addressLevel=3&callback=onMdskip&ref=&brandSiteId=0"
				+ "&isg=Av7-AuknryyU0NQF%2Fg1DpSZxzh9Bz8KW&isg2=AiEhHI1x3ORlOHAU9ujXKCoPMO2gwZXATcofroP37Sio6kG8yx6lkE9qOqgX";
		if(entity != null){
			Document doc = Jsoup.parse(EntityUtils.toString(entity));
			Element detailElement = doc.getElementById("J_DetailMeta");
			if(detailElement != null && detailElement.getElementsByAttributeValue("class", "tb-detail-hd") != null){
				String name = detailElement.getElementsByAttributeValue("class", "tb-detail-hd").select("h1").get(0).ownText();
				tmallMap.put("name", name);
				System.out.println(name);
				CloseableHttpClient client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url + path);
				get.setHeader(":authority","chaoshi.detail.tmall.com");
				get.setHeader(":path",path);
				get.setHeader(":scheme","https");
				get.setHeader("referer",detailUrl);
				get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
				CloseableHttpResponse response = client.execute(get);
				String jsonData = EntityUtils.toString(response.getEntity());
				if(jsonData != null && !jsonData.equals("")){
					jsonData = jsonData.substring(jsonData.indexOf("(") + 1, jsonData.length() - 1);
					JSONObject json = JSONObject.fromObject(jsonData);
					JSONObject moduleJson = json.getJSONObject("defaultModel");
					JSONObject priceResultJson = moduleJson.getJSONObject("itemPriceResultDO");
					
					if(moduleJson.get("sellCountDO") != null){
						String sellCount = JSONObject.fromObject(moduleJson.get("sellCountDO")).getString("sellCount");
						System.out.println(sellCount);
						tmallMap.put("sellCount", sellCount);
					}
					JSONObject priceInfo = priceResultJson.getJSONObject("priceInfo");
					JSONArray shopPromArray = priceResultJson.getJSONArray("tmallShopProm");
					String key = "";
					for(Object obj : priceInfo.keySet()){
						key = obj.toString();
					}
					if(!key.equals("")){
						JSONObject keyJson = priceInfo.getJSONObject(key);
						JSONArray priceArray = keyJson.getJSONArray("promotionList");
						JSONObject arrayJson = JSONObject.fromObject(priceArray.get(0));
						String price = arrayJson.getString("price");
						System.out.println(price);
						tmallMap.put("price", price);
					}
					if(shopPromArray.size() > 0){
						JSONArray promPlan = JSONObject.fromObject(shopPromArray.get(0)).getJSONArray("promPlan");
						if(promPlan.size() > 0){
							String msg = JSONObject.fromObject(promPlan.get(0)).getString("msg");
							System.out.println(msg);
							tmallMap.put("promMsg", msg);
						}
					}
				}
			}
		}
	}

}
