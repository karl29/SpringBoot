package JobSchedule.JdJob;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import Utils.HtmlGenUtils;
import VIP.Analysis;
import VIP.DangqiDetail;
import VIP.ProductSaleDetail;

public class VipJob implements Job, Runnable {
	private String name;
	private List<Map<String, String>> brandList;
	public VipJob(String name,List<Map<String, String>> brandList) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.brandList = brandList;
	}
	
	public VipJob(){}
	@Override
	public void run() {
		if(this.name.equals("anlysis")){
			Analysis.crawl(brandList);
		}else if(this.name.equals("product")){
			String dataTime = HtmlGenUtils.getDataTime("yyyy-MM-dd", -1);
			ProductSaleDetail.crawl(brandList, dataTime);
		}else if(this.name.equals("dangqi")){
			DangqiDetail.crawl(brandList);
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		ExecutorService pool = Executors.newCachedThreadPool();
		List<Map<String, String>> brandList = ProductSaleDetail.getBrandList();
		List<String> list = Arrays.asList(new String[]{"anlysis","product","dangqi"});
		for(String name : list){
			pool.execute(new VipJob(name,brandList));
		}
		
		pool.shutdown();
		while(true){
			if(pool.isTerminated()){
				System.out.println("线程结束了~~");
				break;
			}
		}
	}
	
	public static void main(String[] args) throws JobExecutionException {
		// TODO Auto-generated method stub
		new VipJob().execute(null);
	}

}
