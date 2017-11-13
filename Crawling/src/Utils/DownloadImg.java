package Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImg {
	public static String downLoadImg(String imgUrl,String path,String name){
		String imgPath = "";
		try {
			URL url = new URL(imgUrl);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			InputStream ins = conn.getInputStream();
			byte[] bs = new byte[1024];
			File imgFile = new File(path);
			if(!imgFile.exists()){
				imgFile.mkdirs();
			}
			int len;
			imgPath = imgFile.getPath() + "/" + name;
			OutputStream out = new 	FileOutputStream(imgPath);
			while((len = ins.read(bs)) != -1){
				out.write(bs, 0, len);
			}
			out.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgPath;
	}
}
