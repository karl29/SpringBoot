package Utils;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

public class ScreenShotTest {
	public static void main(String[] args) throws Exception{
		Desktop.getDesktop().browse(new URL("https://chaoshi.m.tmall.com").toURI());
		Robot robot = new Robot();
		Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int width = (int) d.getWidth();
		int height = (int) d.getHeight();
		//最大化浏览器
		robot.keyRelease(KeyEvent.VK_F11);
		robot.delay(2000);
		for(int i = 6;i<12;i++){
			Image img = robot.createScreenCapture(new Rectangle(20, -20, width, height));
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics gr = bi.createGraphics();
			gr.drawImage(img, 0, 0, width, height, null);
			ImageIO.write(bi, "jpg", new File("D://text_"+i+".jpg"));
			System.out.println("截图成功！");
			Thread.sleep(7000);
		}
		
		robot.mouseWheel(10);
		
		Image img = robot.createScreenCapture(new Rectangle(20, -20, width, height));
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics gr = bi.createGraphics();
		gr.drawImage(img, 0, 0, width, height, null);
		ImageIO.write(bi, "jpg", new File("D://text_13.jpg"));
		System.out.println("截图成功！");
		Thread.sleep(7000);
		
		robot.mouseWheel(10);
		
		Image img2 = robot.createScreenCapture(new Rectangle(20, -20, width, height));
		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics gr2 = bi2.createGraphics();
		gr2.drawImage(img2, 0, 0, width, height, null);
		ImageIO.write(bi2, "jpg", new File("D://text_14.jpg"));
		System.out.println("截图成功！");
		Thread.sleep(7000);
		
	}
}
