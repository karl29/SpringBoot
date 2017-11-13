package Utils;

public class Main
{


	public static void main(String[] args) throws Exception
	{
		boolean status=UUAPI.checkAPI();//校验API，必须调用一次，校验失败，识别不成功
		
		if(!status){
			System.out.print("API文件校验失败，无法使用图片识别服务");
			return;
		}
		
		String picPath	= "D:/checkCode/test.jpg";

		//识别开始
		String[] result = UUAPI.easyDecaptcha(picPath,1004);
		
		System.out.println("this img codeID:" + result[0]);
		System.out.println("return recongize Result:" + result[1]);

	}

	public static String imgCode(String picPath){
		String code = "";
		try {
			boolean status=UUAPI.checkAPI();//校验API，必须调用一次，校验失败，识别不成功
			
			if(!status){
				System.out.print("API文件校验失败，无法使用图片识别服务");
				return "";
			}
			//识别开始
			String[] result = UUAPI.easyDecaptcha(picPath,1006);
			code = result[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return code;
	}

	

}




