package JD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import Utils.HtmlGenUtils;
import database.JDBCConnection;

public class JshopDatabase {
	
	/**明细报表PC去向数据保存*/
	public static void saveRedirectPc(List<Map<String, String>> dataList){
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficRedirectPC (日期,去向网址,带去PV,带去UV,访问量,平均停留时间,跳出率,下单数,下单率,间接下单量,"
					+ "间接转化率,品类,品牌,活动名称,活动ID,上线时间,下线时间,自定义去向)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("toUrl"));
				pst.setString(3, map.get("pv"));
				pst.setString(4, map.get("uv"));
				pst.setString(5, map.get("vistis"));
				pst.setString(6, map.get("appPageAvgRt"));
				pst.setString(7, map.get("appBounce"));
				pst.setString(8, map.get("appDirectOrderNum"));
				pst.setString(9, map.get("appDirectOrderRate"));
				pst.setString(10, map.get("appSecOrderNum"));
				pst.setString(11, map.get("appSecOrderRate"));
				pst.setString(12, map.get("category"));
				pst.setString(13, map.get("brand"));
				pst.setString(14, map.get("projectName"));
				pst.setString(15, map.get("projectId"));
				pst.setString(16, map.get("dataTime"));
				pst.setString(17, map.get("dataTime"));
				pst.setString(18, map.get("diyRedirect"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入PC去向数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**明细报表PC页面数据保存*/
	public static void savePagePc(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignPagePC(日期,PV,UV,访问量,平均停留时间,跳出率,下单数,下单金额,下单率,间接下单量,间接下单金额,"
					+ "间接转化率,平均访问深度,品类,品牌,活动名称,活动ID,上线时间,下线时间)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("pv"));
				pst.setString(3, map.get("uv"));
				pst.setString(4, map.get("appVisits"));
				pst.setString(5, map.get("appPageAvgRt"));
				pst.setString(6, map.get("appBounce"));
				pst.setString(7, map.get("appDirectOrderNum"));
				pst.setString(8, map.get("appDirectOrderAccount"));
				pst.setString(9, map.get("appDirectOrderRate"));
				pst.setString(10, map.get("appSecOrderNum"));
				pst.setString(11, map.get("appSecOrderAccount"));
				pst.setString(12, map.get("appSecOrderRate"));
				pst.setString(13, map.get("appPageDepthVisit"));
				pst.setString(14, map.get("category"));
				pst.setString(15, map.get("brand"));
				pst.setString(16, map.get("projectName"));
				pst.setString(17, map.get("projectId"));
				pst.setString(18, map.get("dataTime"));
				pst.setString(19, map.get("dataTime"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**明细报表PC来源数据保存*/
	public static void saveRefererPc(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficSourcePC(日期,来源网址,带来PV,带来UV,访问量,平均停留时间,跳出率,下单数,下单率,间接下单量,"
					+ "间接转化率,品类,品牌,活动名称,活动ID,上线时间,下线时间,自定义来源)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("url"));
				pst.setString(3, map.get("pv"));
				pst.setString(4, map.get("uv"));
				pst.setString(5, map.get("vistis"));
				pst.setString(6, map.get("appPageAvgRt"));
				pst.setString(7, map.get("appBounce"));
				pst.setString(8, map.get("appDirectOrderNum"));
				pst.setString(9, map.get("appDirectOrderRate"));
				pst.setString(10, map.get("appSecOrderNum"));
				pst.setString(11, map.get("appSecOrderRate"));
				pst.setString(12, map.get("category"));
				pst.setString(13, map.get("brand"));
				pst.setString(14, map.get("projectName"));
				pst.setString(15, map.get("projectId"));
				pst.setString(16, map.get("dataTime"));
				pst.setString(17, map.get("dataTime"));
				pst.setString(18, map.get("diySource"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**MO活动明细报表页面数据*/
	public static void savePageMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignPageMobile(日期,PV,UV,访问次数,页面跳转量,页面平均停留时间,页面平均加载时间,新访客占比,跳出率,引入订单量,"
					+ "引入订单金额,引入订单转化率,引入下单商品数,引入下单用户数,直接订单数,直接订单金额,直接订单转化率,加购访客数,"
					+ "加购转化率,直接下单用户数,有效订单数,有效订单金额,有效订单转化率,品类,品牌,活动名称,活动ID,上线时间,下线时间)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("pv"));
				pst.setString(3, map.get("uv"));
				pst.setString(4, map.get("visits"));
				pst.setString(5, map.get("clickNum"));
				pst.setInt(6, 0);
				pst.setInt(7, 0);
				pst.setInt(8, 0);
				pst.setInt(9, 0);
				pst.setString(10, map.get("introduceOrdQtty"));
				pst.setString(11, map.get("introduceOrdAmount"));
				pst.setString(12, map.get("introduceOrdRate"));
				pst.setString(13, map.get("introduceOrdItemQtty"));
				pst.setString(14, map.get("placeOrdUserQtty"));
				pst.setString(15, map.get("dirOrdQtty"));
				pst.setString(16, map.get("dirOrdAmount"));
				pst.setString(17, map.get("dirOrdRate"));
				pst.setString(18, map.get("addToCartUv"));
				pst.setString(19, map.get("addToCartRate"));
				pst.setString(20, map.get("dirOrdUserQtty"));
				pst.setString(21, map.get("valOrdQtty"));
				pst.setString(22, map.get("valOrdAmount"));
				pst.setString(23,map.get("valParOrdRate"));
				pst.setString(24, map.get("category"));
				pst.setString(25, map.get("brand"));
				pst.setString(26, map.get("projectName"));
				pst.setString(27, map.get("projectId"));
				pst.setString(28, map.get("dataTime"));
				pst.setString(29, map.get("dataTime"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入mobile页面数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**mobile端移动来源数据*/
	public static void saveComeDetailMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficSourceMobile(日期,来源链接,PV,UV,访问次数,页面平均停留时间,跳出率,引入订单量,引入订单金额,引入订单转化率,"
					+ "有效订单量,有效订单金额,有效订单转化率,品类,品牌,活动名称,活动ID,上线时间,下线时间,自定义来源)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("sourceUrl"));
				pst.setString(3, map.get("pv"));
				pst.setString(4, map.get("uv"));
				pst.setString(5, map.get("visits"));
				pst.setString(6, "0");
				pst.setString(7, "0");
				pst.setString(8, map.get("introduceOrdQtty"));
				pst.setString(9, map.get("introduceOrdAmount"));
				pst.setString(10, map.get("introduceOrdRate"));
				pst.setString(11, map.get("valOrdQtty"));
				pst.setString(12, map.get("valOrdAmount"));
				pst.setString(13, map.get("valParOrdRate"));
				pst.setString(14, map.get("category"));
				pst.setString(15, map.get("brand"));
				pst.setString(16, map.get("projectName"));
				pst.setString(17, map.get("projectId"));
				pst.setString(18, map.get("dataTime"));
				pst.setString(19, map.get("dataTime"));
				pst.setString(20, map.get("divSource"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**MO端去向数据*/
	public static void saveGoDetailMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficRedirectMobile(日期,去向链接,PV,UV,访问次数,页面平均停留时间,跳出率,引入订单量,引入订单金额,引入订单转化率,"
					+ "有效订单量,有效订单金额,有效订单转化率,品类,品牌,活动名称,活动ID,上线时间,下线时间,自定义去向)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("toUrl"));
				pst.setString(3, map.get("pv"));
				pst.setString(4, map.get("uv"));
				pst.setString(5, map.get("visits"));
				pst.setString(6, "0");
				pst.setString(7, "0");
				pst.setString(8, map.get("introduceOrdQtty"));
				pst.setString(9, map.get("introduceOrdAmount"));
				pst.setString(10, map.get("introduceOrdRate"));
				pst.setString(11, map.get("valOrdQtty"));
				pst.setString(12, map.get("valOrdAmount"));
				pst.setString(13, map.get("valParOrdRate"));
				pst.setString(14, map.get("category"));
				pst.setString(15, map.get("brand"));
				pst.setString(16, map.get("projectName"));
				pst.setString(17, map.get("projectId"));
				pst.setString(18, map.get("dataTime"));
				pst.setString(19, map.get("dataTime"));
				pst.setString(20, map.get("divSource"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**indexInfo Mobile*/
	public static void saveIndexInfoMo(List<Map<String, String>> indexInfoMOList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_IndexMobile(日期,品类,品牌,活动ID,活动名称,PV,"
					+ "UV,Visit,引入订单量,引入订单金额,引入订单转化率,时间区间)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : indexInfoMOList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("category"));
				pst.setString(3, map.get("brand"));
				pst.setString(4, map.get("projectId"));
				pst.setString(5, map.get("projectName"));
				pst.setString(6, map.get("pv"));
				pst.setString(7, map.get("uv"));
				pst.setString(8, map.get("visits"));
				pst.setString(9, map.get("ordQtty"));
				pst.setString(10, map.get("ordAmount"));
				pst.setString(11, map.get("ordRate"));
				pst.setString(12, map.get("hour"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入indexInfoMO数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**indexInfo PC*/
	public static void saveIndexInfoPC(List<Map<String, String>> indexInfoPCList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_IndexPC(日期,品类,品牌,活动ID,活动名称,PV,"
					+ "UV,Visit,间接下单量,直接下单量,间接订单金额,直接订单金额,间接订单率,直接订单率,时间区间)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : indexInfoPCList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("category"));
				pst.setString(3, map.get("brand"));
				pst.setString(4, map.get("projectId"));
				pst.setString(5, map.get("projectName"));
				pst.setString(6, map.get("pv"));
				pst.setString(7, map.get("uv"));
				pst.setString(8, map.get("visits"));
				pst.setString(9, map.get("secNum"));
				pst.setString(10, map.get("directNum"));
				pst.setString(11, map.get("secAccount"));
				pst.setString(12, map.get("directAccount"));
				pst.setString(13, map.get("secRate"));
				pst.setString(14, map.get("directRate"));
				pst.setString(15, map.get("hour"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入indexInfoPc数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**实时监控 product表数据*/
	public static void saveProductData(List<Map<String, String>> productList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Product(日期,品类,品牌,活动ID,活动名称,商品Id,商品名称,"
					+ "商品主图,点击量,订单量,销售额,销售量,终端)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : productList){
				pst.setString(1, map.get("dataTime"));
				pst.setString(2, map.get("category"));
				pst.setString(3, map.get("brand"));
				pst.setString(4, map.get("projectId"));
				pst.setString(5, map.get("projectName"));
				pst.setString(6, map.get("skuId"));
				pst.setString(7, map.get("name"));
				pst.setString(8, map.get("imgSrc"));
				pst.setString(9, map.get("clickNum"));
				pst.setString(10, map.get("directQtty"));
				pst.setString(11, map.get("directAmount"));
				pst.setString(12, map.get("directProductNum"));
				pst.setString(13, map.get("terminal"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入实时监控product数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**实时数据会员性别数据*/
	public static void saveRealTimeSex(List<Map<String, String>> sexList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Gender(日期,品类,品牌,活动ID,活动名称,会员性别,"
					+ "人数,终端)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			List<String> sexStr = Arrays.asList(new String[]{"0","1"});
			for(Map<String,String> map : sexList){
				for(String sex : sexStr){
					if(map.get(sex) != null){
						pst.setString(1, map.get("dataTime"));
						pst.setString(2, map.get("category"));
						pst.setString(3, map.get("brand"));
						pst.setString(4, map.get("projectId"));
						pst.setString(5, map.get("projectName"));
						pst.setString(6, sex.equals("0")?"男":"女");
						pst.setString(7, map.get(sex));
						pst.setString(8, map.get("terminal"));
						pst.addBatch();
					}
				}
			}
			pst.executeBatch();
			System.out.println("插入实时监控Gender数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**实时数据会员年龄层数据*/
	public static void saveRealTimeBirth(List<Map<String, String>> birthList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Birth(日期,品类,品牌,活动ID,活动名称,会员年龄层,"
					+ "人数,终端)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			List<String> birthStr = Arrays.asList(new String[]{"60前","60后","70后","80后","90后","00后"});
			for(Map<String,String> map : birthList){
				for(String birth : birthStr){
					pst.setString(1, map.get("dataTime"));
					pst.setString(2, map.get("category"));
					pst.setString(3, map.get("brand"));
					pst.setString(4, map.get("projectId"));
					pst.setString(5, map.get("projectName"));
					pst.setString(6, birth);
					pst.setString(7, map.get(birth)==null?"0":map.get(birth));
					pst.setString(8, map.get("terminal"));
					pst.addBatch();
				}
			}
			pst.executeBatch();
			System.out.println("插入实时监控birth数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**实时数据会员等级数据*/
	public static void saveRealTimeLevel(List<Map<String, String>> levelList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Class(日期,品类,品牌,活动ID,活动名称,会员等级,"
					+ "人数,终端)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			List<String> levelStr = Arrays.asList(new String[]{"注册会员","铜牌会员","银牌会员","金牌会员","企业用户","钻石会员"});
			for(Map<String,String> map : levelList){
				for(String level : levelStr){
					pst.setString(1, map.get("dataTime"));
					pst.setString(2, map.get("category"));
					pst.setString(3, map.get("brand"));
					pst.setString(4, map.get("projectId"));
					pst.setString(5, map.get("projectName"));
					pst.setString(6, level);
					pst.setString(7, map.get(level)==null?"0":map.get(level));
					pst.setString(8, map.get("terminal"));
					pst.addBatch();
				}
			}
			pst.executeBatch();
			System.out.println("插入实时监控Class数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**装修分析数据*/
	public static void saveDecorate(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String hour = HtmlGenUtils.getDataTime("HH", 0);
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", 0);
			String sql = "insert into JDCampaignDecorateMobile_copy(日期,品类,品牌,活动ID,点击量,点击人数,"
					+ "点击率,引入订单量,引入订单金额,引入订单转化率,对应SKU链接,坑位标识,活动名称,Hour)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : dataList){
				pst.setString(1, dataTime);
				pst.setString(2, map.get("category"));
				pst.setString(3, map.get("brand"));
				pst.setString(4, map.get("projectId"));
				pst.setString(5, map.get("clickNum"));
				pst.setString(6, map.get("clickUserNum"));
				pst.setString(7, map.get("clickRate"));
				pst.setString(8, map.get("introduceOrderQtty"));
				pst.setString(9, map.get("introduceOrderMoney"));
				pst.setString(10, map.get("introduceOrderRate"));
				pst.setString(11, map.get("skuUrl"));
				pst.setString(12, map.get("clickId"));
				pst.setString(13, map.get("projectName"));
				pst.setString(14, hour);
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入装修分析数据成功");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
