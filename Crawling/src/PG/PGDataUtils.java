package PG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import database.JDBCConnection;

public class PGDataUtils {
	
	
	/**spot summary 报表主数据*/
	public static void saveMain(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		if(mapList.size() > 0){
			try {
				Connection con = JDBCConnection.connectToServer("data");
				String sql = "insert into PG_SpotSummary_main(platform,dataTime,category,budget,budgetPc,"
						+ "budgetMobile,PCCount,PCCountWeekend,PCCountWeekday,PCOnline,MBCount,MBCountWeekend,MBCountWeekday,MBOnline,"
						+ "TongLan,TongLanWeekend,TongLanWeekday)"
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = con.prepareStatement(sql);
				for(Map<String,String> map : mapList){
					pst.setString(1, map.get("platform"));
					pst.setString(2, map.get("dataTime"));
					pst.setString(3, map.get("category"));
					pst.setString(4, map.get("budget"));
					pst.setString(5, map.get("budgetPc"));
					pst.setString(6, map.get("budgetMb"));
					pst.setString(7, map.get("pccount"));
					pst.setString(8, map.get("pCCountWeekend"));
					pst.setString(9, map.get("pCCountWeekday"));
					pst.setString(10, map.get("PCOnline"));
					pst.setString(11, map.get("mbcount"));
					pst.setString(12, map.get("MBCountWeekend"));
					pst.setString(13, map.get("MBCountWeekday"));
					pst.setString(14, map.get("MBOnline"));
					pst.setString(15, map.get("TongLan"));
					pst.setString(16, map.get("TongLanWeekend"));
					pst.setString(17, map.get("TongLanWeekday"));
					pst.addBatch();
				}
				pst.executeBatch();
				System.out.println("插入主表数据成功~~~");
				pst.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**spot summary 报表详细数据*/
	public static void saveViewList(
			List<Map<String, Map<String, String>>> viewList) {
		// TODO Auto-generated method stub
		if(viewList.size() > 0){
			try {
				Connection con = JDBCConnection.connectToServer("data");
				String sql = "insert into PG_SpotSummary_detail(id,platform,dataTime,category,channel,Adposition,"
						+ "clickCout)"
						+ " values(?,?,?,?,?,?,?)";
				PreparedStatement pst = con.prepareStatement(sql);
				for(Map<String,Map<String,String>> map : viewList){
					for(String day : map.keySet()){
						Map<String,String> dataMap = map.get(day);
						pst.setString(1, dataMap.get("id"));
						pst.setString(2, dataMap.get("platform"));
						pst.setString(3, dataMap.get("dateTime") + day);
						pst.setString(4, dataMap.get("category"));
						pst.setString(5, dataMap.get("channel"));
						pst.setString(6, dataMap.get("adposition"));
						pst.setString(7, dataMap.get("clickCout"));
						pst.addBatch();
					}
				}
				pst.executeBatch();
				System.out.println("插入详细表数据成功~~~");
				pst.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	/**HA Weekly Tracking*/
	public static void saveTracking(
			Map<String, List<Map<String, String>>> platformMap) {
		// TODO Auto-generated method stub
		if(platformMap.size() > 0){
			try {
				Connection con = JDBCConnection.connectToServer("data");
				String sql = "insert into PG_HA_Weekly_Tracking(platform,dataTime,category,channel,Adposition,"
						+ "SpotType,content,KpiCtr,KpiCPC,status)"
						+ " values(?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = con.prepareStatement(sql);
				Iterator<String> it = platformMap.keySet().iterator();
				while(it.hasNext()){
					String platform = it.next();
					List<Map<String, String>> mapList = platformMap.get(platform);
					for(Map<String,String> map : mapList){
						pst.setString(1, platform);
						pst.setString(2, map.get("dataTime"));
						System.out.println(map.get("dataTime") + "=================");
						pst.setString(3, map.get("category"));
						pst.setString(4, map.get("channel"));
						pst.setString(5, map.get("adposition"));
						pst.setString(6, map.get("spotType"));
						pst.setString(7, map.get("content"));
						pst.setString(8, map.get("CTR"));
						pst.setString(9, map.get("CPC"));
						pst.setString(10, map.get("status"));
						pst.addBatch();
					}
				}
				pst.executeBatch();
				System.out.println("插入详细表数据成功~~~");
				pst.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static void saveHAExcel(List<Map<String, String>> list) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String sql = "insert into PG_HA_Tracking_excel(id,batchName,importTime) values(?,?,?)"; 
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : list){
				pst.setString(1, map.get("id"));
				pst.setString(2, map.get("batchName"));
				pst.setString(3, map.get("importTime"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**获取上次最新更新的数据id*/
	public static String getLastImportId() {
		String id = "";
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String sql = "select id from PG_HA_Tracking_excel where importTime=(select max(importTime) from PG_HA_Tracking_excel)"; 
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				id = rs.getString(1);
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	/**更新表的impression字段*/
	public static void updateImpression(List<Map<String, String>> list) {
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String sql = "update PG_HA_Weekly_Tracking set impression=?,cost=?,clickNum=? where dataTime=? and category=? and channel=? and Adposition=?"; 
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : list){
				pst.setString(1, map.get("impression"));
				pst.setString(2, map.get("cost"));
				pst.setString(3, map.get("click"));
				pst.setString(4, map.get("trackingDate"));
				pst.setString(5, map.get("categoryId"));
				pst.setString(6, map.get("channelId"));
				pst.setString(7, map.get("positionId"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
