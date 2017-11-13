package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utils.HtmlGenUtils;

public class ProxyIp extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public ProxyIp() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = "http://www.xdaili.cn/ipagent//privateProxy"
				+ "/getDynamicIP/DD20178181015CDGP8d/9f7ef5db160211e79ff07cd30abda612?returnType=2";
		String htmlCode = HtmlGenUtils.getHtmlCode(url, "utf-8");
		
		response.getWriter().write(htmlCode);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		doGet(request, response);
	}
}
