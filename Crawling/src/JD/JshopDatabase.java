package JD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import Utils.HtmlGenUtils;
import database.JDBCConnection;

public class JshopDatabase {
	
	/**��ϸ����PCȥ�����ݱ���*/
	public static void saveRedirectPc(List<Map<String, String>> dataList){
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficRedirectPC (����,ȥ����ַ,��ȥPV,��ȥUV,������,ƽ��ͣ��ʱ��,������,�µ���,�µ���,����µ���,"
					+ "���ת����,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��,�Զ���ȥ��)"
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
			System.out.println("����PCȥ�����ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**��ϸ����PCҳ�����ݱ���*/
	public static void savePagePc(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignPagePC(����,PV,UV,������,ƽ��ͣ��ʱ��,������,�µ���,�µ����,�µ���,����µ���,����µ����,"
					+ "���ת����,ƽ���������,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��)"
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
	
	/**��ϸ����PC��Դ���ݱ���*/
	public static void saveRefererPc(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficSourcePC(����,��Դ��ַ,����PV,����UV,������,ƽ��ͣ��ʱ��,������,�µ���,�µ���,����µ���,"
					+ "���ת����,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��,�Զ�����Դ)"
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
	
	/**MO���ϸ����ҳ������*/
	public static void savePageMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignPageMobile(����,PV,UV,���ʴ���,ҳ����ת��,ҳ��ƽ��ͣ��ʱ��,ҳ��ƽ������ʱ��,�·ÿ�ռ��,������,���붩����,"
					+ "���붩�����,���붩��ת����,�����µ���Ʒ��,�����µ��û���,ֱ�Ӷ�����,ֱ�Ӷ������,ֱ�Ӷ���ת����,�ӹ��ÿ���,"
					+ "�ӹ�ת����,ֱ���µ��û���,��Ч������,��Ч�������,��Ч����ת����,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��)"
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
			System.out.println("����mobileҳ�����ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**mobile���ƶ���Դ����*/
	public static void saveComeDetailMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficSourceMobile(����,��Դ����,PV,UV,���ʴ���,ҳ��ƽ��ͣ��ʱ��,������,���붩����,���붩�����,���붩��ת����,"
					+ "��Ч������,��Ч�������,��Ч����ת����,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��,�Զ�����Դ)"
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
	
	/**MO��ȥ������*/
	public static void saveGoDetailMo(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignTrafficRedirectMobile(����,ȥ������,PV,UV,���ʴ���,ҳ��ƽ��ͣ��ʱ��,������,���붩����,���붩�����,���붩��ת����,"
					+ "��Ч������,��Ч�������,��Ч����ת����,Ʒ��,Ʒ��,�����,�ID,����ʱ��,����ʱ��,�Զ���ȥ��)"
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
			String sql = "insert into JDCampaignRealTime_IndexMobile(����,Ʒ��,Ʒ��,�ID,�����,PV,"
					+ "UV,Visit,���붩����,���붩�����,���붩��ת����,ʱ������)"
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
			System.out.println("����indexInfoMO���ݳɹ�");
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
			String sql = "insert into JDCampaignRealTime_IndexPC(����,Ʒ��,Ʒ��,�ID,�����,PV,"
					+ "UV,Visit,����µ���,ֱ���µ���,��Ӷ������,ֱ�Ӷ������,��Ӷ�����,ֱ�Ӷ�����,ʱ������)"
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
			System.out.println("����indexInfoPc���ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**ʵʱ��� product������*/
	public static void saveProductData(List<Map<String, String>> productList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Product(����,Ʒ��,Ʒ��,�ID,�����,��ƷId,��Ʒ����,"
					+ "��Ʒ��ͼ,�����,������,���۶�,������,�ն�)"
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
			System.out.println("����ʵʱ���product���ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**ʵʱ���ݻ�Ա�Ա�����*/
	public static void saveRealTimeSex(List<Map<String, String>> sexList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Gender(����,Ʒ��,Ʒ��,�ID,�����,��Ա�Ա�,"
					+ "����,�ն�)"
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
						pst.setString(6, sex.equals("0")?"��":"Ů");
						pst.setString(7, map.get(sex));
						pst.setString(8, map.get("terminal"));
						pst.addBatch();
					}
				}
			}
			pst.executeBatch();
			System.out.println("����ʵʱ���Gender���ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**ʵʱ���ݻ�Ա���������*/
	public static void saveRealTimeBirth(List<Map<String, String>> birthList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Birth(����,Ʒ��,Ʒ��,�ID,�����,��Ա�����,"
					+ "����,�ն�)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			List<String> birthStr = Arrays.asList(new String[]{"60ǰ","60��","70��","80��","90��","00��"});
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
			System.out.println("����ʵʱ���birth���ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**ʵʱ���ݻ�Ա�ȼ�����*/
	public static void saveRealTimeLevel(List<Map<String, String>> levelList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into JDCampaignRealTime_Class(����,Ʒ��,Ʒ��,�ID,�����,��Ա�ȼ�,"
					+ "����,�ն�)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			List<String> levelStr = Arrays.asList(new String[]{"ע���Ա","ͭ�ƻ�Ա","���ƻ�Ա","���ƻ�Ա","��ҵ�û�","��ʯ��Ա"});
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
			System.out.println("����ʵʱ���Class���ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**װ�޷�������*/
	public static void saveDecorate(List<Map<String, String>> dataList) {
		// TODO Auto-generated method stub
		try {
			Connection con = JDBCConnection.connectToServer("data");
			String hour = HtmlGenUtils.getDataTime("HH", 0);
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", 0);
			String sql = "insert into JDCampaignDecorateMobile_copy(����,Ʒ��,Ʒ��,�ID,�����,�������,"
					+ "�����,���붩����,���붩�����,���붩��ת����,��ӦSKU����,��λ��ʶ,�����,Hour)"
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
			System.out.println("����װ�޷������ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
