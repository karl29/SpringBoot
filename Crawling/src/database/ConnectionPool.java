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
	
	private final int initialConnections = 10;//���ӳس�ʼ��С
	private final int incrementalConnection = 5;//���ӳ��Զ����ӵĴ�С
	private int maxConnection = 50;//���ӳ����������
	private Vector<Connection> connections = null;//������ӳص���������ʼֵΪnull
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
	 * �������ӳ�
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
     * ������ numConnections ָ����Ŀ�����ݿ����� , ������Щ���� ���� connections ������ 
     *  
     * @param numConnections 
     *            Ҫ���������ݿ����ӵ���Ŀ 
     */  
	private void createConnections(int numConnections) {
		// ѭ�������ƶ���Ŀ�����ݿ�����
		for(int i = 0;i<numConnections;i++){
			// �Ƿ����ӳ��е����ݿ����ӵ����������ﵽ������ֵ�����Ա maxConnections  
            // ָ������� maxConnections Ϊ 0 ��������ʾ��������û�����ơ�  
            // ��������������ﵽ��󣬼��˳��� 
			if(this.maxConnection > 0 && this.connections.size() >= this.maxConnection){
				break;
			}
			//����һ�����ӵ����ӳ���
			try {
				connections.addElement((Connection) new PooledConnection(newConnection()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ����һ���µ����ݿ�����
	 * @throws SQLException 
	 * */
	private Connection newConnection() throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		// ������ǵ�һ�δ������ݿ����ӣ���������ݿ⣬��ô����ݿ�����֧�ֵ�  
        // ���ͻ�������Ŀ  
		if(connections.size() == 0){
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			// ���ݿⷵ�ص� driverMaxConnections ��Ϊ 0 ����ʾ�����ݿ�û�����  
            // �������ƣ������ݿ������������Ʋ�֪��  
            // driverMaxConnections Ϊ���ص�һ����������ʾ�����ݿ�����ͻ����ӵ���Ŀ  
            // ������ӳ������õ�������������������ݿ������������Ŀ , �������ӳص����  
            // ������ĿΪ���ݿ�����������Ŀ  
			if(driverMaxConnections > 0 && this.maxConnection > driverMaxConnections){
				this.maxConnection = driverMaxConnections;
			}
		}
		return conn;
	}
	
	/** 
     * ͨ������ getFreeConnection() ��������һ�����õ����ݿ����� , �����ǰû�п��õ����ݿ����ӣ����Ҹ�������ݿ����Ӳ��ܴ� 
     * ���������ӳش�С�����ƣ����˺����ȴ�һ���ٳ��Ի�ȡ�� 
     *  
     * @return ����һ�����õ����ݿ����Ӷ��� 
     */ 
	public Connection getConnection(){
		if(connections == null){
			return null;
		}
		
		synchronized (ConnectionPool.class) {
			Connection con = getFreeConnection();//��ȡһ�����õ�����
			while(con == null){
				wait(250);
				con = getFreeConnection();
			}
			return con;
		}
	}
	
	/** 
     * �����������ӳ����� connections �з���һ�����õĵ����ݿ����ӣ���� ��ǰû�п��õ����ݿ����ӣ������������ 
     * incrementalConnections ���� ��ֵ�����������ݿ����ӣ����������ӳ��С� ������������е������Զ���ʹ���У��򷵻� null 
     *  
     * @return ����һ�����õ����ݿ����� 
     */  
	private Connection getFreeConnection() {
		Connection con = findFreeConnection();
		if(con == null){
			// ���Ŀǰ���ӳ���û�п��õ�����  
            // ����һЩ����  
			createConnections(incrementalConnection);
			con = findFreeConnection();
		}
		return con;
	}
	
	/** 
     * �������ӳ������е����ӣ�����һ�����õ����ݿ����ӣ� ���û�п��õ����ӣ����� null 
     *  
     * @return ����һ�����õ����ݿ����� 
     */  
	private Connection findFreeConnection() {
		Connection con = null;
		PooledConnection pConn = null;
		//������ӳص����ж���
		Enumeration<Connection> enu = connections.elements();
		while(enu.hasMoreElements()){
			pConn = (PooledConnection) enu.nextElement();
			if(!pConn.isBusy()){
				// ����˶���æ�������������ݿ����Ӳ�������Ϊæ  
				con = pConn.getConnection();
				pConn.setBusy(true);
				//���Դ������Ƿ����
				if(!testConnection(con)){
					// ��������Ӳ��������ˣ��򴴽�һ���µ����ӣ�  
                    // ���滻�˲����õ����Ӷ����������ʧ�ܣ����� null
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
	 * �������ݿ������Ƿ����
	 * */
	private boolean testConnection(Connection con) {
		try {
			//�ж����ݱ��Ƿ����
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
     * �˺�������һ�����ݿ����ӵ����ӳ��У����Ѵ�������Ϊ���С� ����ʹ�����ӳػ�õ����ݿ����Ӿ�Ӧ�ڲ�ʹ�ô�����ʱ�������� 
     *  
     * @param �践�ص����ӳ��е����Ӷ��� 
     */  
	public void returnConnection(Connection con){
		// ȷ�����ӳش��ڣ��������û�д����������ڣ���ֱ�ӷ���  
        if (connections == null) {  
            System.out.println(" ���ӳز����ڣ��޷����ش����ӵ����ӳ��� !");  
            return;  
        }  
        PooledConnection pCon = null;
        Enumeration<Connection> enu = connections.elements();
        //�ҵ���Ӧ����������Ϊ����״̬
        while(enu.hasMoreElements()){
        	pCon = (PooledConnection) enu.nextElement();
        	if(con == pCon.getConnection()){
        		pCon.setBusy(false);
        		break;
        	}
        }
	}
	
	
	/**
	 * ˢ�����ӳ��������Ӷ���
	 * @throws SQLException 
	 * */
	public void refreshConnection() throws SQLException{
		if(connections == null){
			System.out.println("���ӳ�Ϊ�գ��޷�ˢ��");
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
				//�رմ����ӣ�����һ���µ�����
				closeConnection(pCon.getConnection());
				pCon.setConnection(newConnection());
				pCon.setBusy(true);
			}
		}
	}
	/**
	 * �ر�һ�����ݿ�����
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
	 * �ر����ӳ����������ӣ���������ӳ�
	 * @throws InterruptedException 
	 * */
	private void closeConnectionPool(Connection con){
		// TODO Auto-generated method stub
		if(connections == null){
			System.out.print("���ӳز����ڣ��޷��ر�!");
			return;
		}
		synchronized (ConnectionPool.class) {
			PooledConnection pCon = null;
			Enumeration<Connection> enu = connections.elements();
			while(enu.hasMoreElements()){
				pCon = (PooledConnection) enu.nextElement();
				//���æ,��5��
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
        Connection connection = null;// ���ݿ�����  
        boolean busy = false; // �������Ƿ�����ʹ�õı�־��Ĭ��û������ʹ��  
  
        // ���캯��������һ�� Connection ����һ�� PooledConnection ����  
        public PooledConnection(Connection connection) {  
            this.connection = connection;  
        }  
  
        // ���ش˶����е�����  
        public Connection getConnection() {  
            return connection;  
        }  
  
        // ���ô˶���ģ�����  
        public void setConnection(Connection connection) {  
            this.connection = connection;  
        }  
  
        // ��ö��������Ƿ�æ  
        public boolean isBusy() {  
            return busy;  
        }  
  
        // ���ö������������æ  
        public void setBusy(boolean busy) {  
            this.busy = busy;  
        }  
    }  
}
