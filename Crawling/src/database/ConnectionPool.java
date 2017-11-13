package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

public class ConnectionPool {
	private String jdbcDriver = "";
	private String dbUrl = "";
	private String dbUserName = "";
	private String dbPassword = "";
	private String table = "";
	
	private final int initialConnections = 10;//连接池初始大小
	private final int incrementalConnection = 5;//连接池自动增加的大小
	private int maxConnection = 50;//连接池最大连接数
	private Vector<Connection> connections = null;//存放连接池的向量，初始值为null
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public ConnectionPool(String jdbcDriver, String dbUrl, String dbUserName,
			String dbPassword) {
		super();
		this.jdbcDriver = jdbcDriver;
		this.dbUrl = dbUrl;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
	}
	
	/**
	 * 创建连接池
	 * */
	public void createPool(){
		if(connections == null){
			synchronized (ConnectionPool.class) {
				if(connections == null){
					try {
						Driver driver =  (Driver) Class.forName(this.jdbcDriver).newInstance();
						DriverManager.registerDriver(driver);
						connections = new Vector<Connection>();
						createConnections(this.initialConnections);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/** 
     * 创建由 numConnections 指定数目的数据库连接 , 并把这些连接 放入 connections 向量中 
     *  
     * @param numConnections 
     *            要创建的数据库连接的数目 
     */  
	private void createConnections(int numConnections) {
		// 循环创建制定数目的数据库链接
		for(int i = 0;i<numConnections;i++){
			// 是否连接池中的数据库连接的数量己经达到最大？最大值由类成员 maxConnections  
            // 指出，如果 maxConnections 为 0 或负数，表示连接数量没有限制。  
            // 如果连接数己经达到最大，即退出。 
			if(this.maxConnection > 0 && this.connections.size() >= this.maxConnection){
				break;
			}
			//增加一个连接到连接池中
			try {
				connections.addElement((Connection) new PooledConnection(newConnection()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建一个新的数据库连接
	 * @throws SQLException 
	 * */
	private Connection newConnection() throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		// 如果这是第一次创建数据库连接，即检查数据库，获得此数据库允许支持的  
        // 最大客户连接数目  
		if(connections.size() == 0){
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			// 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大  
            // 连接限制，或数据库的最大连接限制不知道  
            // driverMaxConnections 为返回的一个整数，表示此数据库允许客户连接的数目  
            // 如果连接池中设置的最大连接数量大于数据库允许的连接数目 , 则置连接池的最大  
            // 连接数目为数据库允许的最大数目  
			if(driverMaxConnections > 0 && this.maxConnection > driverMaxConnections){
				this.maxConnection = driverMaxConnections;
			}
		}
		return conn;
	}
	
	/** 
     * 通过调用 getFreeConnection() 函数返回一个可用的数据库连接 , 如果当前没有可用的数据库连接，并且更多的数据库连接不能创 
     * 建（如连接池大小的限制），此函数等待一会再尝试获取。 
     *  
     * @return 返回一个可用的数据库连接对象 
     */ 
	public Connection getConnection(){
		if(connections == null){
			return null;
		}
		
		synchronized (ConnectionPool.class) {
			Connection con = getFreeConnection();//获取一个可用的连接
			while(con == null){
				wait(250);
				con = getFreeConnection();
			}
			return con;
		}
	}
	
	/** 
     * 本函数从连接池向量 connections 中返回一个可用的的数据库连接，如果 当前没有可用的数据库连接，本函数则根据 
     * incrementalConnections 设置 的值创建几个数据库连接，并放入连接池中。 如果创建后，所有的连接仍都在使用中，则返回 null 
     *  
     * @return 返回一个可用的数据库连接 
     */  
	private Connection getFreeConnection() {
		Connection con = findFreeConnection();
		if(con == null){
			// 如果目前连接池中没有可用的连接  
            // 创建一些连接  
			createConnections(incrementalConnection);
			con = findFreeConnection();
		}
		return con;
	}
	
	/** 
     * 查找连接池中所有的连接，查找一个可用的数据库连接， 如果没有可用的连接，返回 null 
     *  
     * @return 返回一个可用的数据库连接 
     */  
	private Connection findFreeConnection() {
		Connection con = null;
		PooledConnection pConn = null;
		//获得连接池的所有对象
		Enumeration<Connection> enu = connections.elements();
		while(enu.hasMoreElements()){
			pConn = (PooledConnection) enu.nextElement();
			if(!pConn.isBusy()){
				// 如果此对象不忙，则获得它的数据库连接并把它设为忙  
				con = pConn.getConnection();
				pConn.setBusy(true);
				//测试此连接是否可用
				if(!testConnection(con)){
					// 如果此连接不可再用了，则创建一个新的连接，  
                    // 并替换此不可用的连接对象，如果创建失败，返回 null
					try {
						con = newConnection();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					pConn.setConnection(con);
				}
				break;
			}
		}
		return con;
	}
	
	
	/**
	 * 测试数据库连接是否可用
	 * */
	private boolean testConnection(Connection con) {
		try {
			//判断数据表是否存在
			if(table.equals("")){
				con.setAutoCommit(true);
			}else{
				Statement stmt = con.createStatement();
				stmt.execute("select count(*) from " + table);
			}
		} catch (SQLException e) {
			closeConnection(con);
			return false;
		}
		return true;
	}
	
	 /** 
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。 
     *  
     * @param 需返回到连接池中的连接对象 
     */  
	public void returnConnection(Connection con){
		// 确保连接池存在，如果连接没有创建（不存在），直接返回  
        if (connections == null) {  
            System.out.println(" 连接池不存在，无法返回此连接到连接池中 !");  
            return;  
        }  
        PooledConnection pCon = null;
        Enumeration<Connection> enu = connections.elements();
        //找到对应的连接设置为空闲状态
        while(enu.hasMoreElements()){
        	pCon = (PooledConnection) enu.nextElement();
        	if(con == pCon.getConnection()){
        		pCon.setBusy(false);
        		break;
        	}
        }
	}
	
	
	/**
	 * 刷新连接池所有连接对象
	 * @throws SQLException 
	 * */
	public void refreshConnection() throws SQLException{
		if(connections == null){
			System.out.println("连接池为空，无法刷新");
			return ;
		}
		synchronized (connections) {
			PooledConnection pCon = null;
			Enumeration<Connection> enu = connections.elements();
			while(enu.hasMoreElements()){
				pCon = (PooledConnection) enu.nextElement();
				if(pCon.isBusy()){
					wait(5000);
				}
				//关闭此连接，设置一个新的连接
				closeConnection(pCon.getConnection());
				pCon.setConnection(newConnection());
				pCon.setBusy(true);
			}
		}
	}
	/**
	 * 关闭一个数据库连接
	 * */
	private void closeConnection(Connection con) {
		// TODO Auto-generated method stub
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 关闭连接池中所有连接，并清空连接池
	 * @throws InterruptedException 
	 * */
	private void closeConnectionPool(Connection con){
		// TODO Auto-generated method stub
		if(connections == null){
			System.out.print("连接池不存在，无法关闭!");
			return;
		}
		synchronized (ConnectionPool.class) {
			PooledConnection pCon = null;
			Enumeration<Connection> enu = connections.elements();
			while(enu.hasMoreElements()){
				pCon = (PooledConnection) enu.nextElement();
				//如果忙,等5秒
				if(pCon.isBusy()){
					wait(5000);
				}
				closeConnection(pCon.getConnection());
				connections.removeElement(pCon);
			}
			connections = null;
		}
	}
	
	
	 private void wait(int mSeconds) {  
        try {  
            Thread.sleep(mSeconds);  
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }  
    }  

	class PooledConnection {  
        Connection connection = null;// 数据库连接  
        boolean busy = false; // 此连接是否正在使用的标志，默认没有正在使用  
  
        // 构造函数，根据一个 Connection 构告一个 PooledConnection 对象  
        public PooledConnection(Connection connection) {  
            this.connection = connection;  
        }  
  
        // 返回此对象中的连接  
        public Connection getConnection() {  
            return connection;  
        }  
  
        // 设置此对象的，连接  
        public void setConnection(Connection connection) {  
            this.connection = connection;  
        }  
  
        // 获得对象连接是否忙  
        public boolean isBusy() {  
            return busy;  
        }  
  
        // 设置对象的连接正在忙  
        public void setBusy(boolean busy) {  
            this.busy = busy;  
        }  
    }  
}
