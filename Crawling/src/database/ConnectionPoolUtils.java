package database;
/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月9日 下午3:07:53
 *使用单例模式创建连接池工具类
 */
public class ConnectionPoolUtils {
	private ConnectionPoolUtils(){}
	
	private static ConnectionPool poolServer = null;
	
	private static ConnectionPool poolLocal = null;
	
	/**
	 * 阿里数据库
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
	
	
	/**本地数据库*/
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
