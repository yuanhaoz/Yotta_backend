package test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import utils.Log;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月12日
 */
@SuppressWarnings("deprecation")
public class HttpClinetTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		simpleClent();
		test();
	}
	
	
	public static void simpleClent() throws Exception{
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("httpbin.org", 80),
                new UsernamePasswordCredentials("user", "passwd"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet("http://httpbin.org/basic-auth/user/passwd");

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}
	
	public static void test() throws IOException{
		
		@SuppressWarnings("resource")
		HttpClient httpclient = new DefaultHttpClient();
//		HttpGet httpget = new HttpGet("http://localhost/");
		HttpGet httpget = new HttpGet("http://image.baidu.com/search/index?tn=baiduimage&ps=1&ct=201326592&lm=-1&cl=2&nc=1&ie=utf-8&word=%E6%97%A5%E6%9C%AC");
		HttpResponse response = httpclient.execute(httpget);
		
		Log.log(response.getProtocolVersion());
		Log.log(response.getStatusLine().toString());
		Log.log(response.getStatusLine().getStatusCode());
		Log.log(response.getStatusLine().getReasonPhrase());
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
//				 do something useful
				String htmlContent = InputStream2String(instream);
//				Log.log(htmlContent);
				Document doc = Jsoup.parse(htmlContent);
				getFacetImage(doc);
				
				long len = entity.getContentLength();
				if (len != -1 && len < 2048) {
					System.out.println(EntityUtils.toString(entity));
				} else {
					// Stream content out
				}
				
				
				
				
			} finally {
				instream.close();
			}
		}
	}
	
	/**
	 * 解析图片网页，获取网页上前5张图片链接
	 */
	public static void getFacetImage(Document doc) {
//		Elements imgitems = doc.select("li.imgitem");
		Elements imgitems = doc.select("#noPage");
		Log.log(imgitems);
	}
	
	/**
	 * InputStream转化为String
	 */
	public static String InputStream2String(InputStream instream){
		
		BufferedReader buff;
		StringBuffer res = new StringBuffer();
		String line = "";
		try {
			buff = new BufferedReader(new InputStreamReader(instream, "utf-8"));
			while((line = buff.readLine()) != null){
			    res.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        String htmlContent = res.toString();
		return htmlContent;
	}

}
