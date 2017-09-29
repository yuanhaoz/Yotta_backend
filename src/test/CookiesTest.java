package test;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月12日
 */
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import app.Config;

public class CookiesTest {
	WebDriver driver;
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("phantomjs.binary.path", Config.phantomjsPath + "phantomjs.exe");
		driver = new PhantomJSDriver();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void test() {
		printCookie();
		//添加一个cookie
		Cookie cookie = new Cookie("s","selenium");
		driver.manage().addCookie(cookie);
		printCookie();
		//删除一个cookie
		driver.manage().deleteCookie(cookie);	//driver.manage().deleteCookieNamed("s");也可以
		printCookie();
	}
	
	public void printCookie(){
		//获取并打印所有的cookie
		Set<Cookie> allCookies=driver.manage().getCookies();
		
		System.out.println("----------Begin---------------");
		for(Cookie c:allCookies){
			System.out.println(String.format("%s->%s",c.getName(),c.getValue()));
		}
		System.out.println("----------End---------------");
	}
}