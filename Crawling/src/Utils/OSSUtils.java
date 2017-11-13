package Utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyun.oss.OSSClient;


/**
 * oss������
 * */
public class OSSUtils {
	
	/**
	 * ��ȡoss�ͻ�����
	 * */
	public static OSSClient getOSSClient(){
		String endpoint = "oss-cn-shenzhen.aliyuncs.com";
		String accessKeyId = "ugjE1sM2j7z8f86v";
		String accessKeySecret = "XjhC94p1RRxNlN953McTRiKX0HsdSw";
		
		return new OSSClient(endpoint, accessKeyId, accessKeySecret);
	}
	
	/**
	 * ������ɱ�����ϴ�ͼƬ��oss
	 * */
	public static String uploadImg(String imgUrl,String name,String keyName) {
		String imgPath = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
		try {
			name = URLDecoder.decode(name, "utf-8");
			URL url = new URL(imgUrl);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			InputStream ins = conn.getInputStream();
			OSSClient  oss = getOSSClient();
			String bucketName = "i2mago-data";
			String key = "Karl/"+keyName+"/"+name + "_" + format.format(new Date()) + ".jpg";;
			imgPath = "http://i2mago-data.oss-cn-shenzhen.aliyuncs.com/" + key;
			oss.putObject(bucketName, key, ins);
			System.out.println("�ϴ�ͼƬoss�ɹ������ӣ�" + imgPath);
			oss.shutdown();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgPath;
	}
	public static void main(String[] args){
		try {
			URL url = new URL("http://m.360buyimg.com/mobilecms/s210x210_jfs/t4993/102/2306541065/134079/b0cdb42/58fdbb7aNac2695d3.jpg!q70.jpg");
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			InputStream ins = conn.getInputStream();
			OSSClient  oss = getOSSClient();
			String bucketName = "i2mago-data";
			String key = "Karl/secKill/test.jpg";
			oss.putObject(bucketName, key, ins);
			System.out.println("�ϴ��ɹ�");
			oss.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
