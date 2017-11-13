package JobSchedule.JdJob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import JD.JdKillSchedule;
import JobSchedule.TmallJob.MaochaoJob;



public class JDKilleJob {
	public static void main(String[] args) throws Exception{
		JDKilleJob job = new JDKilleJob();
		job.run();
	}
	//94843581
	public void run() throws Exception{
		
		SchedulerFactory factory = new StdSchedulerFactory();
		
		Scheduler scheduler = factory.getScheduler();
		
		//京东库存后台
		JobDetail job = newJob(JdKillSchedule.class).withIdentity("jdKill","jdKillGroup").build();
		//每天1点钟执行
		Trigger trigger = newTrigger().withIdentity("jdKillTrigger", "jdKillGroup")
				.withSchedule(cronSchedule("0 30 14 ? * *")).build();
		
		scheduler.scheduleJob(job, trigger);
		
		
		//SKII触发器 每天9点，18点执行
		JobDetail babelJob = newJob(BabelJob.class)
				.withIdentity("babel", "babelGroup").build();
		Trigger bebelTrigger = newTrigger().withIdentity("babelTrigger", "triggerGroup")
				.withSchedule(cronSchedule("0 02 20 ? * *")).build();
		scheduler.scheduleJob(babelJob, bebelTrigger);
		
		
		JobDetail chaoshiJob = newJob(MaochaoJob.class)
				.withIdentity("maochao", "maochaoGroup").build();
		Trigger chaoshiTrigger = newTrigger().withIdentity("choshiTrigger", "maochaoGroup")
				.withSchedule(cronSchedule("0 05 * ? * *")).build();
		scheduler.scheduleJob(chaoshiJob, chaoshiTrigger);
		
		JobDetail vipJob = newJob(VipJob.class).withIdentity("vip", "vipGroup").build();
		Trigger vipTrigger = newTrigger().withIdentity("vipTrigger","vipGroup")
				.withSchedule(cronSchedule("0 0 8 ? * *")).build();
		scheduler.scheduleJob(vipJob, vipTrigger);
		scheduler.start();
	}
}
