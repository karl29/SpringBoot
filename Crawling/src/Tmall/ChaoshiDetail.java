package Tmall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.JDBCConnection;


/**
 * ��è������Ʒ������Ϣ
 * */
public class ChaoshiDetail {
	public static void main(String[] args) throws Exception{
		crawDetail();
	}
	
	
	/**
	 * ��ʼ������
	 * */
	public static void crawDetail() throws Exception{
		Map<String, List<Map<String, String>>> itemList = getItemList();
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		if(itemList.size() > 0){
			Iterator<Entry<String, List<Map<String,String>>>> it = itemList.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,List<Map<String,String>>> entryMap = it.next();
				String categroy = entryMap.getKey();
				List<Map<String,String>> list = entryMap.getValue();
				if(categroy.equals("����")){
					for(Map<String,String> map : list){
						String productId = map.get("productId");
						String url = map.get("url");
						CrawProductDetail.crawlingTmall(url,productId, map);
						mapList.add(map);
						Thread.sleep(200);
					}
				}
			}
		}
		if(mapList.size() > 0){
			saveData(mapList);
		}
	}
	
	/**
	 * ��ȡ���ݱ���
	 * */
	private static void saveData(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = JDBCConnection.connectToServer("data");
			String sql = "insert into CSTmall_SKUInfo(��ƷId,��Ʒ����,��Ʒ�����۸�,��Ʒ������,��Ʒ������Ϣ,��ȡ����,Ʒ��) values(?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(sql);
			int count = 0;
			int size = 1000;
			for(Map<String,String> map : mapList){
				pst.setString(1, map.get("productId"));
				pst.setString(2, map.get("name"));
				float price = 0f;
				if(map.get("price") != null){
					price = Float.valueOf(map.get("price"));
				}
				pst.setFloat(3, price);
				int sellCount = 0;
				if(map.get("sellCount") != null){
					sellCount = Integer.valueOf(map.get("sellCount"));
				}
				pst.setInt(4, sellCount);
				String promMsg = "";
				if(map.get("promMsg") != null){
					promMsg = map.get("promMsg");
				}
				pst.setString(5, promMsg);
				pst.setString(6, format.format(new Date()));
				pst.setString(7, map.get("categroy"));
				pst.addBatch();
				if(++count%size == 0){
					pst.executeBatch();
				}
			}
			pst.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(pst != null){
					pst.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡ����Ʒ��url�б�
	 * */
	public static Map<String,List<Map<String,String>>> getItemList() throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Connection conn = JDBCConnection.connectToServer("data");
		String sql = "select Ʒ�� as category,��ƷId as productId,��ƷUrl as url from CSTmall_SearchResult where ���� = ?";
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, format.format(new Date()));
		ResultSet ret = pst.executeQuery();
    	Map<String,List<Map<String,String>>> mapList = new HashMap<String,List<Map<String,String>>>();
    	while(ret.next()){
    		String category = ret.getString(1);
    		String productId = ret.getString(2);
    		String url = ret.getString(3);
    		List<Map<String,String>> list = mapList.get(category);
    		if(list == null){
    			list = new ArrayList<Map<String,String>>();
    		}
    		Map<String,String> map = new HashMap<String,String>();
    		System.out.println(category + "~~" + productId + "~~" + url);
    		Map<String,String> platMap = new HashMap<String,String>();
    		map.put("category", category);
    		map.put("productId", productId);
    		map.put("url", url);
    		list.add(platMap);
    		mapList.put(category, list);
    	}
		System.out.println(mapList.size());
		return mapList;
	}
}
