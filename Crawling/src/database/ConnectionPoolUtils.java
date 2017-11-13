package database;
/**
 *@author Karl.Qin
 *@version ����ʱ�䣺2017��10��9�� ����3:07:53
 *ʹ�õ���ģʽ�������ӳع�����
 */
public class ConnectionPoolUtils {
	private ConnectionPoolUtils(){}
	
	private static ConnectionPool poolServer = null;
	
	private static ConnectionPool poolLocal = null;
	
	/**
	 * �������ݿ�
	 * */
	public static ConnectionPool getPoolServerInstance(String database){
		if(poolServer == null){
			synchronized (ConnectionPoolUtils.class) {
				if(poolServer == null){
					poolServer = new ConnectionPool("com.microsoft.sqlserver.jdbc.SQLServerDriver",
							"jdbc:sqlserver://rdseavrqjane2eio.sqlserver.rds.aliyuncs.com:3433;DatabaseName=" + database,
							"datateam", "sdfaf34653team46kdaSFJH98349023");
					try {
						poolServer.createPool();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return poolServer;
	}
	
	
	/**�������ݿ�*/
	public static ConnectionPool getPoolLocalInstance(String database){
		if(poolLocal == null){
			synchronized (ConnectionPoolUtils.class) {
				if(poolLocal == null){
					poolLocal = new ConnectionPool("com.microsoft.sqlserver.jdbc.SQLServerDriver",
							"jdbc:sqlserver://rdseavrqjane2eio.sqlserver.rds.aliyuncs.com:3433;DatabaseName=" + database,
							"datateam", "sdfaf34653team46kdaSFJH98349023");
					try {
						poolLocal.createPool();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return poolLocal;
	}
}
