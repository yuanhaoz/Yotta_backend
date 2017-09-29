package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.Log;
import app.Config;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月12日
 */
@SuppressWarnings("deprecation")
public class SpiderTest {
	
	public static void main(String[] args) throws Exception{
//		test();
//		test2();
		spider_selenium();
//		jsoupTest();
//		httpClientCrawler();
	}
	
	
	
	public static void httpClientCrawler() throws Exception{
		String facetSearch = "你好";
		String url = "http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592"
				+ "&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1"
				+ "&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word="
				+ facetSearch;
		@SuppressWarnings({ "resource" })
		HttpClient hc = new DefaultHttpClient();
		
	    System.out.println(String.format("Fetching %s...", url));   	        	    
	    HttpGet hg = new HttpGet(url);     
	    hg.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	    hg.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
	    hg.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1)");
	    hg.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
	    hg.setHeader("Host", "http://image.baidu.com//");
        hg.setHeader("Connection", "Keep-Alive");
        
		try
		{
		    HttpResponse response = hc.execute(hg);
		    HttpEntity entity = response.getEntity();   	       	        
		    InputStream htmInput = null;  
		    Log.log(htmInput);
		    if(entity != null){
		        htmInput = entity.getContent();
		        BufferedReader buff = new BufferedReader(new InputStreamReader(htmInput, "utf-8"));
		        StringBuffer res = new StringBuffer();
		        String line = "";
		        while((line = buff.readLine()) != null){
		            res.append(line);
		        }
		        String htmlContent = res.toString();
		        
		        Document doc = Jsoup.parseBodyFragment(htmlContent);
		        doc.select("#imgid");
		        Log.log(htmlContent);
		    }  
		}
		catch(Exception err) {
			System.err.println("爬取失败...失败原因: " + err.getMessage()); 
		}
		finally {
	        //关闭连接，释放资源
	        hc.getConnectionManager().shutdown();
	    }
	}
	
	
	
	public static void jsoupTest(){
		String facetSearch = "你好";
		String url = "http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592"
				+ "&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1"
				+ "&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word="
				+ facetSearch;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
//			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
//					.cookie("auth", "token").timeout(3000).post();
		} catch (Exception e) {
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		Elements imgid = doc.select("#imgid");
		System.out.println(imgid);
		System.out.println(imgid.size());
		
		Elements imgitems = doc.getElementsByClass("imgitem");
		Log.log(imgitems.size());
		for (Element imgitem : imgitems) {
			String imglist = imgitem.attr("data-objurl");
			Log.log(imglist);
			// String imglist = imgitem.attr("data-thumburl"); // 403服务器禁止爬取
		}
	}
	
	/**
	 * selenium爬取图片，虚拟机将pageLoadTimeout换成implicitlyWait即可爬取
	 */
	public static void spider_selenium(){
		
		String facetSearch = "你好_定义";
		String facetImageUrl = "http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592"
				+ "&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1"
				+ "&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word="
				+ facetSearch;
		
		
		@SuppressWarnings("unused")
		Document doc = null;
		System.setProperty("phantomjs.binary.path", Config.phantomjsPath + "phantomjs.exe");
		WebDriver driver = new PhantomJSDriver();
		
		
		/**
		 * 因为Load页面需要一段时间，如果页面还没加载完就查找元素，必然是查找不到的。
		 * 最好的方式，就是设置一个默认等待时间，在查找页面元素的时候如果找不到就等待一段时间再找，直到超时。
		 * Webdriver提供两种方法，一种是显性等待，另一种是隐性等待。
		 * 
		 * 
		 * 下面的是：隐形等待只是等待一段时间，元素不一定要出现，只要时间到了就会继续执行。
		 */
		while (true) {
			try {
//				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				driver.manage().timeouts().setScriptTimeout(100,TimeUnit.SECONDS);
				driver.get(facetImageUrl);
				Log.log("success....");
			} catch (Exception e) {
				Log.log("try again...");
				driver.quit();
				driver = new PhantomJSDriver();
//				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);  
				driver.manage().timeouts().setScriptTimeout(100,TimeUnit.SECONDS);
				
				continue;
			}
			break;
		}
		
		
		/**
		 * 显性等待：显示等待就是明确的要等到某个元素的出现或者是某个元素的可点击等条件,
		 * 等不到,就一直等,除非在规定的时间之内都没找到,那么就跳出Exception.
		 */
		try{
			new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("kw")));
			Log.log("find id Element： kw");
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		/**
		 * Webdriver截图
		 */
        File sourceFile=((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);  
        try{  
            System.out.println("Begin saving screenshot to path: F:\\1.png");  
            FileUtils.copyFile(sourceFile, new File("F:\\1.png"));  
        } catch(Exception e){  
            System.out.println("Save screenshot failed!");  
            e.printStackTrace();  
        } finally{  
            System.out.println("Finish screenshot!");  
        }  
        
		
		try {
			String html = driver.getPageSource();
			doc = Jsoup.parse(html);
			driver.quit();
		} catch (Exception e) {
			Log.log("Error at loading the page ...");
			driver.quit();
		}
	}
	
	
	/**
	 * 下面的例子是：打开百度，搜索“selenium"，等待搜索结果里出现“Selenium_百度百科”的时候点击该链接。
	 * 显示等待
	 */
	public static void test() {  
		System.setProperty("phantomjs.binary.path", Config.phantomjsPath + "phantomjs.exe");
		WebDriver driver = new PhantomJSDriver();
        driver.get("http://www.baidu.com");  
        driver.findElement(By.id("kw")).sendKeys("selenium");  
        driver.findElement(By.id("su")).click();  
        //方法一  
        new WebDriverWait(driver,10).until(ExpectedConditions.presenceOfElementLocated(By.linkText("Selenium_百度百科")));  
        driver.findElement(By.linkText("Selenium_百度百科")).click();  
        //方法二  
//        WebElement e = (new WebDriverWait( driver, 10)).until(  
//                new ExpectedCondition< WebElement>(){  
//                    @Override  
//                    public WebElement apply( WebDriver d) {  
//                        return d.findElement(By.linkText("Selenium_百度百科"));  
//                    }  
//                }  
//            );  
//        e.click();
        
        
        String pathString = "F:\\1.png";
        File sourceFile = ((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);  
        try{  
            System.out.println("Begin saving screenshot to path: " + pathString);  
            FileUtils.copyFile(sourceFile, new File(pathString));  
        } catch(Exception e1){  
            System.out.println("Save screenshot failed!");  
            e1.printStackTrace();  
        } finally{  
            System.out.println("Finish screenshot!");  
        } 
          
        driver.quit();  
    } 
	
	/**
	 * 下面的例子是：打开百度，搜索“selenium"，等待搜索结果里出现“Selenium_百度百科”的时候点击该链接。
	 * 隐性等待：隐形等待只是等待一段时间，元素不一定要出现，只要时间到了就会继续执行。
	 */
	public static void test2() {
		System.setProperty("phantomjs.binary.path", Config.phantomjsPath + "phantomjs.exe");
		WebDriver driver = new PhantomJSDriver();
		driver.get("http://www.baidu.com");
		driver.findElement(By.id("kw")).sendKeys("selenium");
		driver.findElement(By.id("su")).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElement(By.linkText("Selenium_百度百科")).click();
		
		
		String pathString = "F:\\2.png";
        File sourceFile = ((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);  
        try{  
            System.out.println("Begin saving screenshot to path: " + pathString);  
            FileUtils.copyFile(sourceFile, new File(pathString));  
        } catch(Exception e1){  
            System.out.println("Save screenshot failed!");  
            e1.printStackTrace();  
        } finally{  
            System.out.println("Finish screenshot!");  
        }
		
		driver.quit();
	}

	
}
