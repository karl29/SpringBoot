package Utils;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * JavaMail �汾: 1.6.0
 * JDK �汾: JDK 1.7 ���ϣ����룩
 */
public class JavaMail {
    public static String myEmailAccount = "karl.qin@i2mago.com";
    public static String myEmailPassword = "Karl.2256258";

    // ����������� SMTP ��������ַ, ����׼ȷ, ��ͬ�ʼ���������ַ��ͬ, һ��(ֻ��һ��, ���Ǿ���)��ʽΪ: smtp.xxx.com
    public static String myEmailSMTPHost = "smtp.exmail.qq.com";

    public static String[] receiveMailAccount = {"wise.he@i2mago.com","karl.qin@i2mago.com"};
    
    
    public Session getSession(){
    	 // 1. ������������, ���������ʼ��������Ĳ�������
        Properties props = new Properties();                    // ��������
        props.setProperty("mail.transport.protocol", "smtp");   // ʹ�õ�Э�飨JavaMail�淶Ҫ��
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // �����˵������ SMTP ��������ַ
        props.setProperty("mail.smtp.auth", "true");            // ��Ҫ������֤

        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        
        // 2. �������ô����Ự����, ���ں��ʼ�����������
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);// ����Ϊdebugģʽ, ���Բ鿴��ϸ�ķ��� log
        
        return session;
    }
    
    public static void sendEmail(MimeMessage message){
    	try {
    		Session session = new JavaMail().getSession();
            // 4. ���� Session ��ȡ�ʼ��������
            Transport transport = session.getTransport();

            transport.connect(myEmailAccount, myEmailPassword);

            // 6. �����ʼ�, �������е��ռ���ַ, message.getAllRecipients() ��ȡ�������ڴ����ʼ�����ʱ���ӵ������ռ���, ������, ������
            transport.sendMessage(message, message.getAllRecipients());

            // 7. �ر�����
            transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void main(String[] args) throws Exception {

    }

    /**
     * ����һ��ֻ�����ı��ļ��ʼ�
     *
     * @param session �ͷ����������ĻỰ
     * @param sendMail ����������
     * @param receiveMail �ռ�������
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. ����һ���ʼ�
        MimeMessage message = new MimeMessage(session);

        // 2. From: �����ˣ��ǳ��й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸��ǳƣ�
        message.setFrom(new InternetAddress(sendMail));

        // 3. To: �ռ��ˣ��������Ӷ���ռ��ˡ����͡����ͣ�
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "..", "UTF-8"));

        // 4. Subject: �ʼ����⣨�����й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ı��⣩
        message.setSubject("è����̨", "UTF-8");

        // 5. Content: �ʼ����ģ�����ʹ��html��ǩ���������й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ķ������ݣ�
        message.setContent("����һ������ʼ�����ֻ��һ������ʼ�", "text/html;charset=UTF-8");

        // 6. ���÷���ʱ��
        message.setSentDate(new Date());

        // 7. ��������
        message.saveChanges();

        return message;
    }

}