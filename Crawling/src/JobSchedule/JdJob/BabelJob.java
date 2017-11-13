package JobSchedule.JdJob;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import database.JDBCConnection;
import JD.JDSKIIPrice;
import JD.JDSKIIStock;
import JD.SecKillTempo;
import Tmall.TmallTempo;
import Utils.HtmlGenUtils;
import Utils.JavaMail;


public class BabelJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		/*try {
			List<Map<String,String>> mapList = JDSKIIStock.getItemList();
			JDSKIIStock.crawl(mapList);
			JDSKIIPrice.crawl(mapList);
			JDSKIIStock.crawlSearchResult(mapList);
			
			if(mapList.size() > 0){
				//������ȡ��������
				saveData(mapList);
				
				//���ʼ�
				sendEmail(mapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			SecKillTempo.crawlingJd();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendEmail(List<Map<String, String>> mapList) {
		// TODO Auto-generated method stub
		StringBuffer buffer = getBufferHtml(mapList);
		if(buffer != null){
			StringBuffer subJect =new StringBuffer();
			subJect.append(getTableHead());
			subJect.append(buffer);
			subJect.append("</table>");
			try {
				MimeMessage message = new MimeMessage(new JavaMail().getSession());
		        message.setFrom(new InternetAddress(JavaMail.myEmailAccount));
		        
		        InternetAddress[] sendTo = new InternetAddress[JavaMail.receiveMailAccount.length];  
		        for (int i = 0; i < JavaMail.receiveMailAccount.length; i++) {  
		            System.out.println("���͵�:" + JavaMail.receiveMailAccount[i]);  
		            sendTo[i] = new InternetAddress(JavaMail.receiveMailAccount[i]);  
		        }  
		        message.setRecipients(MimeMessage.RecipientType.TO, sendTo);

		        message.setSubject("SKII����Ԥ��", "UTF-8");

		        message.setContent(subJect.toString(), "text/html;charset=UTF-8");

		        // 6. ���÷���ʱ��
		        message.setSentDate(new Date());

		        // 7. ��������
		        message.saveChanges();
		        
		        JavaMail.sendEmail(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ͷ
	 * */
	private static Object getTableHead() {
		StringBuffer subJect =new StringBuffer(); 
		subJect.append("<table border='1' cellpadding='0' cellspacing='0' style='width:100%;margin-top:20px;'>");
		subJect.append("<th width='10%'>����</th><th width='40%'>sku����</th><th width='5%' align='center'>ԭ��</th><th width='5%' align='center'>����۸�</th>");
		subJect.append("<th width='5%' align='center'>�۸����</th><th width='15%' align='center'>���</th><th width='20%' align='center'>����</th>");
		return subJect;
	}

	/**
	 * ƴ��html����
	 * */
	private static StringBuffer getBufferHtml(List<Map<String, String>> mapList) {
		StringBuffer buffer = null;
		String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", 0);
		for(Map<String,String> map : mapList){
			String priceDidcrepancy = map.get("priceDidcrepancy");
			if(!"".equals(map.get("status")) || !"".equals(map.get("saleStatus")) || Float.valueOf(priceDidcrepancy) != 0){
				if(buffer == null){
					buffer = new StringBuffer();
				}
				//�޻�״̬��Ԥ�����ơ����ۻ�����
				buffer.append("<tr><td align='center'>"+dataTime+"</td><td><a href="+map.get("href")+">"+map.get("itemName")+"</a></td>");
				buffer.append("<td align='right'>"+map.get("originalPrice")+"</td><td align='right'>"+map.get("price")+"</td>");
				buffer.append("<td align='right'>"+(Float.valueOf(priceDidcrepancy) == 0?"":priceDidcrepancy)+"</td><td algin='center'>"+map.get("status")+"</td>");
				buffer.append("<td>"+map.get("saleStatus")+"</td></tr>");
			}
		}
		return buffer;
	}

	/**
	 * ���浽���ݿ�
	 * */
	private void saveData(List<Map<String, String>> mapList) {
		try {
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd HH:mm:ss", 0);
			
			Connection con = JDBCConnection.connectToLocal("data");
			String sql = "insert into SKII_SkuPrice(dataTime,skuId,itemName,price,stockStatus,originalPrice,saleStatus,priceDidcrepancy)"
					+ " values(?,?,?,?,?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql);
			for(Map<String,String> map : mapList){
				pst.setString(1, dataTime);
				pst.setString(2, map.get("skuId"));
				pst.setString(3, map.get("itemName"));
				pst.setString(4, map.get("price"));
				pst.setString(5, map.get("status"));
				pst.setString(6, map.get("originalPrice"));
				pst.setString(7, map.get("saleStatus"));
				pst.setString(8, map.get("priceDidcrepancy"));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("����skiiҳ���������ݳɹ�");
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception{
		new BabelJob().execute(null);
	}
}
