package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JavaScriptUitls {
	public static void main(String[] args) throws Exception{
		System.out.println(entryPwd("baojieaijingdong001"));
	}
	public static String entryPwd(String password)throws Exception{
		ScriptEngineManager manager = new ScriptEngineManager();
        String newCode = "";
        InputStreamReader inputStreamReader = null;
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            URL url = new URL("https://passport.jd.com/new/js/jsencrypt.min.js");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/html");
            inputStreamReader = getInputContent("GET", null, conn);
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        engine.eval(inputStreamReader);
        if (engine instanceof Invocable) {
            Invocable invoke = (Invocable) engine; 
            newCode = (String) invoke.invokeFunction("md5", password);// 调用md5方法，并传入1个参数


        }
        inputStreamReader.close();
		return newCode;
	}
	
	public static InputStreamReader getInputContent(String requestMethod,
	        String outputStr, HttpURLConnection conn) throws ProtocolException,
	    IOException, UnsupportedEncodingException { // （封装的http请求方法） 需要调用的方法
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setUseCaches(false);
	        // 设置请求方式（GET/POST）
	        conn.setRequestMethod(requestMethod);
	        // 当outputStr不为null时向输出流写数据
	        if (null != outputStr) {
	            OutputStream outputStream = conn.getOutputStream();
	            // 注意编码格式
	            outputStream.write(outputStr.getBytes("UTF-8"));
	            outputStream.close();
	        }
	        // 从输入流读取返回内容
	        InputStream inputStream = conn.getInputStream();
	        InputStreamReader inputStreamReader = new InputStreamReader(
	            inputStream, "UTF-8");
	        return inputStreamReader;
	    }
}
