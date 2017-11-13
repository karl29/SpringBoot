package database.jd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import database.JDBCConnection;

public class JdUtils {
	/**
	 * 保存到流量分析数据到数据库
	 * */
	public static void saveDate(List<Map<String, String>> totalFlowList) {
		// TODO Auto-generated method stub
		if(totalFlowList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into JD_Flow_BabelData (projectId,projectName,pageType,dataTime,source,PV,UV) "
						+ "values (?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : totalFlowList){
					ps.setString(1, map.get("activityId"));
					ps.setString(2, map.get("activityName"));
					ps.setString(3, map.get("pageType"));
					ps.setString(4, map.get("dataTime"));
					ps.setString(5, map.get("referPage"));
					ps.setInt(6, Integer.valueOf(map.get("pv")));
					ps.setInt(7, Integer.valueOf(map.get("uv")));
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("插入流量分析数据成功");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 分层数据保存
	 * */
	public static void saveFloorDate(List<Map<String, String>> totalFloorList) {
		// TODO Auto-generated method stub
		if(totalFloorList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into JD_BabelData (projectId,projectName,pageType,pageTown,dataTime,clickCount,"
						+ "clickPopulation,intorducPens,intorducAmount,directOrderCount,directOrderAmount,indirectOrderCount,"
						+ "indirectOrderAmount,PV,UV) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : totalFloorList){
					ps.setString(1, map.get("activityId"));
					ps.setString(2, map.get("activityName"));
					ps.setString(3, map.get("pageType"));
					ps.setString(4, map.get("pageTown"));
					ps.setString(5, map.get("dataTime"));
					ps.setFloat(6, Float.valueOf(map.get("clickCount")));
					ps.setFloat(7, Float.valueOf(map.get("clickUv")));
					ps.setFloat(8, Float.valueOf(map.get("intorducPens")));
					ps.setFloat(9, Float.valueOf(map.get("intorducAmount")));
					ps.setFloat(10, Float.valueOf(map.get("directOrderCount")));
					ps.setFloat(11, Float.valueOf(map.get("directOrderAmount")));
					ps.setFloat(12, Float.valueOf(map.get("indirectOrderCount")));
					ps.setFloat(13, Float.valueOf(map.get("indirectOrderAmount")));
					ps.setFloat(14, Float.valueOf(map.get("pv")));
					ps.setFloat(15, Float.valueOf(map.get("uv")));
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("插入分楼层数据成功");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 分时数据保存
	 * */
	public static void saveHourDate(List<Map<String, String>> totalHourList) {
		// TODO Auto-generated method stub
		if(totalHourList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into JD_TimeSlot_BabelData (projectId,projectName,pageType,pageTown,dataTime,clickCount,"
						+ "clickPopulation,intorducPens,intorducAmount,directOrderCount,directOrderAmount,indirectOrderCount,"
						+ "indirectOrderAmount,PV,UV,timeslot) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : totalHourList){
					ps.setString(1, map.get("activityId"));
					ps.setString(2, map.get("activityName"));
					ps.setString(3, map.get("pageType"));
					ps.setString(4, map.get("pageTown"));
					ps.setString(5, map.get("dataTime"));
					ps.setFloat(6, Float.valueOf(map.get("clickCount")));
					ps.setFloat(7, Float.valueOf(map.get("clickUv")));
					ps.setFloat(8, Float.valueOf(map.get("intorducPens")));
					ps.setFloat(9, Float.valueOf(map.get("intorducAmount")));
					ps.setFloat(10, Float.valueOf(map.get("directOrderCount")));
					ps.setFloat(11, Float.valueOf(map.get("directOrderAmount")));
					ps.setFloat(12, Float.valueOf(map.get("indirectOrderCount")));
					ps.setFloat(13, Float.valueOf(map.get("indirectOrderAmount")));
					ps.setFloat(14, Float.valueOf(map.get("pv")));
					ps.setFloat(15, Float.valueOf(map.get("uv")));
					ps.setString(16, getHours(map.get("hour")));
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("插入分时数据成功");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getHours(String hour){
		for(int i = 0;i<24;i++){
			String result = "";
			if(i<10){
				result = "0" + i + ":00";
			}else{
				result = i + ":00";
			}
			if(hour.equals(i + "")){
				hour = result;
			}
		}
		return hour;
	}

	public static void saveStallDate(List<Map<String, String>> totalStallList) {
		// TODO Auto-generated method stub
		if(totalStallList.size() > 0){
			try {
				Connection dbConn = JDBCConnection.connectToServer("data");
				String sql = "insert into JD_Stall_BabelData (floorId,floorName,groupId,groupName,stallId,"
						+ "stallName,imgUrl,url,dataTime,subId,clickCount,"
						+ "clickPopulation,intorducPens,intorducAmount,directOrderCount,directOrderAmount,indirectOrderCount,"
						+ "indirectOrderAmount,groupStr,stallSc,pageType,projectId) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = dbConn.prepareStatement(sql);
				int count = 0;
				int size = 1000;
				for(Map<String,String> map : totalStallList){
					ps.setString(1, map.get("floorId"));
					ps.setString(2, map.get("pageTown"));
					ps.setString(3, map.get("groupId"));
					ps.setString(4, map.get("groupName"));
					ps.setString(5, map.get("stallId"));
					ps.setString(6, map.get("stallName"));
					ps.setString(7, map.get("imgUrl"));
					ps.setString(8, map.get("url"));
					ps.setString(9, map.get("dataTime"));
					ps.setString(10, map.get("subId"));
					ps.setFloat(11, Float.valueOf(map.get("clickCount")));
					ps.setFloat(12, Float.valueOf(map.get("clickUv")));
					ps.setFloat(13, Float.valueOf(map.get("intorducPens")));
					ps.setFloat(14, Float.valueOf(map.get("intorducAmount")));
					ps.setFloat(15, Float.valueOf(map.get("directOrderCount")));
					ps.setFloat(16, Float.valueOf(map.get("directOrderAmount")));
					ps.setFloat(17, Float.valueOf(map.get("indirectOrderCount")));
					ps.setFloat(18, Float.valueOf(map.get("indirectOrderAmount")));
					ps.setString(19, map.get("groupStr"));
					ps.setString(20, map.get("sc"));
					ps.setString(21, map.get("pageType"));
					ps.setString(22, map.get("activityId"));
					ps.addBatch();
					if(++count%size == 0){
						ps.executeBatch();
					}
				}
				ps.executeBatch();
				System.out.println("插入坑位数据成功");
				ps.close();
				dbConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
