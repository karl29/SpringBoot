package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JDBCConnection {
	public static void main(String[] args) throws Exception{
		Connection con = connectToServer("data");
		//alter table SecKill add brandTotal numeric(6)
		String sql = "insert into CsTmall_TransactionComposition(area,dataTime,brand,category,cateLeafName,"
					+ "payMoney,payMoneyPercentage,payNum,buyerNum,payMordOrderNum,paySubOrderNum,paySubOrderAvg,payMoneyPerBuyer,payMoneyPerOrder)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, "����");
		pst.setString(2, "2017-05");
		pst.setString(3, "ȫ��");
		pst.setString(4, "��ʿ���뵶/���");
		pst.setString(5, "�ֶ����뵶/��Ƭ/����");
		pst.setString(6, "178674.22");
		pst.setString(7, "100.00%");
		pst.setString(8, "3873");
		pst.setString(9, "3323");
		pst.setString(10, "3350");
		pst.setString(11, "3526");
		pst.setString(12, "50.67");
		pst.setString(13, "53.77");
		pst.setString(14, "53.34");
		pst.execute();
		pst.close();
		con.close();
		System.out.println("����ɹ�");
	}
	public static Connection connectToServer(String database){
		Connection dbConn = null;
		try {
			synchronized(JDBCConnection.class){
				String userName = "datateam";
				String password = "sdfaf34653team46kdaSFJH98349023";
				String host = "jdbc:sqlserver://rdseavrqjane2eio.sqlserver.rds.aliyuncs.com:3433;DatabaseName=" + database;
				System.out.println("��ʼ�������ݿ�~~");
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				dbConn = DriverManager.getConnection(host, userName, password);
				System.out.println("���ӳɹ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dbConn;
	}
	
	
	public static Connection connectToLocal(String database){
		Connection dbConn = null;
		try {
			synchronized(JDBCConnection.class){
				String userName = "sa";
				String password = "i2mago.com";
				String host = "jdbc:sqlserver://192.168.0.25;DatabaseName=" + database;
				System.out.println("��ʼ�������ݿ�~~");
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				dbConn = DriverManager.getConnection(host, userName, password);
				System.out.println("���ӳɹ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dbConn;
	}
}
