package VIP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import database.JDBCConnection;

public class VipDataUtils {
	
	/**品牌综合分析*/
	public static void saveAnalysis(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("vip");
			String sql = "insert into vip_brandAnalysis(brandId,brandName,dataTime,totalAmount,totalQuantity,uv,coverRate,buyerNum,MORate)"
					+ " values(?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("brandId"));
				pst.setString(2, map.get("brandName"));
				pst.setString(3, map.get("dataTime"));
				pst.setString(4, map.get("totalAmount"));
				pst.setString(5, map.get("totalQuantity"));
				pst.setString(6, map.get("uv"));
				pst.setString(7, map.get("rate"));
				pst.setString(8, map.get("buyer"));
				pst.setString(9, map.get("MORate"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入品牌综合分析数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**产品销售数据*/
	public static void productData(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("vip");
			String sql = "insert into vip_productData(brandId,brandName,brandStoreName,firstSellDay,lastSellDay,dataTime,"
					+ "productName,productCode,price,hotType,picUrl,onSaleStockAmt,onSaleStockCnt,userCnt,goodsCnt,goodsCntWithoutReturn,"
					+ "goodsMoney,goodsAmt,goodsAmtWithoutReturn,sellingRatio,uv,conversion,collectUserCnt,goodsCtr,brandGoodsAvgCtr,optGroup,warehouseName,lv3Category)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			int index = 0;
			for(Map<String,String> map : dataList){
				try {
					System.out.println("==============我只是一条分割线================");
					pst.setString(1, map.get("brandId"));
					System.out.println("brandId~~" + map.get("brandId"));
					pst.setString(2, map.get("brandName"));
					System.out.println("brandId~~" + map.get("brandName"));
					pst.setString(3, map.get("brandStoreName"));
					System.out.println("brandStoreName~~" + map.get("brandStoreName"));
					pst.setString(4, map.get("firstSellDay"));
					System.out.println("firstSellDay~~" + map.get("firstSellDay"));
					pst.setString(5, map.get("lastSellDay"));
					System.out.println("lastSellDay~~" + map.get("lastSellDay"));
					pst.setString(6, map.get("dataTime"));
					System.out.println("dataTime~~" + map.get("dataTime"));
					pst.setString(7, map.get("productName"));
					System.out.println("productName~~" + map.get("productName"));
					pst.setString(8, map.get("productCode"));
					System.out.println("productCode~~" + map.get("productCode"));
					pst.setString(9, map.get("price"));
					System.out.println(map.get("price"));
					pst.setString(10, map.get("hotType"));
					System.out.println("hotType~~" + map.get("hotType"));
					pst.setString(11, map.get("picUrl"));
					pst.setString(12, map.get("onSaleStockAmt"));
					pst.setString(13, map.get("onSaleStockCnt"));
					pst.setString(14, map.get("userCnt"));
					pst.setString(15, map.get("goodsCnt"));
					pst.setString(16, map.get("goodsCntWithoutReturn"));
					pst.setString(17, map.get("goodsMoney"));
					pst.setString(18, map.get("goodsAmt"));
					pst.setString(19, map.get("goodsAmtWithoutReturn"));
					pst.setString(20, map.get("sellingRatio").equals("null")?"0":map.get("sellingRatio"));
					pst.setString(21, map.get("uv"));
					System.out.println("uv~~" + map.get("uv"));
					pst.setString(22, map.get("conversion"));
					pst.setString(23, map.get("collectUserCnt"));
					pst.setString(24, map.get("goodsCtr"));
					pst.setString(25, map.get("brandGoodsAvgCtr"));
					pst.setString(26, map.get("optGroup"));
					pst.setString(27, map.get("warehouseName"));
					pst.setString(28, map.get("lv3Category"));
					//pst.execute();
					pst.addBatch();
					if(index%1000 == 0){
						pst.executeBatch();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				index++;
			}
			pst.executeBatch();
			System.out.println("插入商品详情数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**档期详情数据*/
	public static void saveDangqiDetail(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("vip");
			String sql = "insert into vip_DangqiDetail(brandId,brandName,dangqiName,saleTimeFrom,saleTimeTo,dataTime,"
					+ "activeName,optGroup,warehouseName,onlineStockAmt,onlineStockCnt,avgOrderAmount,avgGoodsAmount,userCnt,orderCnt,goodsCnt,"
					+ "saleCntNoReject,salesAmount,salesAmountNoCutReject,goodsMoney,cutGoodsMoney,uv,uvConvert,ctr,sellingRatio)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				try {
					System.out.println("==============我只是一条分割线================");
					pst.setString(1, map.get("brandId"));
					System.out.println("brandId~~" + map.get("brandId"));
					pst.setString(2, map.get("brandName"));
					System.out.println("brandId~~" + map.get("brandName"));
					pst.setString(3, map.get("dangqiName"));
					System.out.println("dangqiName~~" + map.get("dangqiName"));
					pst.setString(4, map.get("saleTimeFrom"));
					System.out.println("saleTimeFrom~~" + map.get("saleTimeFrom"));
					pst.setString(5, map.get("saleTimeTo"));
					System.out.println("saleTimeTo~~" + map.get("saleTimeTo"));
					pst.setString(6, map.get("dataTime"));
					System.out.println("dataTime~~" + map.get("dataTime"));
					pst.setString(7, map.get("activeName"));
					System.out.println("activeName~~" + map.get("activeName"));
					pst.setString(8, map.get("optGroup"));
					System.out.println("optGroup~~" + map.get("optGroup"));
					pst.setString(9, map.get("warehouseName"));
					System.out.println(map.get("warehouseName"));
					pst.setString(10, map.get("onlineStockAmt"));
					System.out.println("onlineStockAmt~~" + map.get("onlineStockAmt"));
					pst.setString(11, map.get("onlineStockCnt"));
					pst.setString(12, map.get("avgOrderAmount"));
					System.out.println("avgOrderAmount~~" + map.get("avgOrderAmount"));
					pst.setString(13, map.get("avgGoodsAmount"));
					System.out.println("avgGoodsAmount~~" + map.get("avgGoodsAmount"));
					pst.setString(14, map.get("userCnt"));
					pst.setString(15, map.get("orderCnt"));
					pst.setString(16, map.get("goodsCnt"));
					pst.setString(17, map.get("saleCntNoReject"));
					pst.setString(18, map.get("salesAmount"));
					System.out.println("salesAmount~~" + map.get("salesAmount"));
					pst.setString(19, map.get("salesAmountNoCutReject"));
					System.out.println("salesAmountNoCutReject~~" + map.get("salesAmountNoCutReject"));
					pst.setString(20, map.get("goodsMoney"));
					System.out.println("goodsMoney~~" + map.get("goodsMoney"));
					pst.setString(21, map.get("cutGoodsMoney"));
					pst.setString(22, map.get("uv"));
					pst.setString(23, map.get("uvConvert"));
					System.out.println("uvConvert~~" + map.get("uvConvert"));
					pst.setString(24, map.get("ctr"));
					System.out.println("ctr~~" + map.get("ctr"));
					pst.setString(25, map.get("sellingRatio"));
					System.out.println("sellingRatio~~" + map.get("sellingRatio"));
					pst.execute();
					//pst.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//pst.executeBatch();
			System.out.println("插入商品详情数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
