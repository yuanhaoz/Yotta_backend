package app;

import com.spreada.utils.chinese.ZHConverter;

/**
 * 全局配置文件
 * @author 郑元浩
 * @description 这是一个全局的配置文件
 */

public class Config {
	
	public static String projectName = "Yotta";
	
	/**
	 * Selenium Webdriver 配置
	 */
	public static String phantomjsPath = "D:\\";  // 无界面浏览器
//	public static String PHANTOMJS_PATH = "D:\\phantomjs.exe";  // 无界面浏览器
//	public static String IE_PATH = "D:\\IEDriverServer.exe";  // IE模拟
	public static String CHROME_PATH = "D:\\chromedriver.exe";  // Chrome模拟
	
	/**
	 * Mysql 配置
	 */
	public static String MYSQL_URL = "jdbc:mysql://localhost:3306/yotta_create?user=root&password=root&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口
	public static String IP1="http://202.117.54.39"; // 跨域访问控制：域名+apache端口
	public static String IP2="http://202.117.54.39:8080/Yotta"; // 阿里云服务器：域名+http端口
	
	/**
	 * 数据库  配置
	 * @author 郑元浩
	 */
	public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
	public static String DOMAIN_TABLE = "domain";
	public static String FACET_TABLE = "facet";
	public static String FACET_RELATION_TABLE = "facet_relation";
	public static String DOMAIN_LAYER_TABLE = "domain_layer";
	public static String DOMAIN_TOPIC_TABLE = "domain_topic";
	public static String DOMAIN_TOPIC_RELATION_TABLE = "domain_topic_relation";
	public static String SPIDER_TEXT_TABLE = "spider_text";
	public static String SPIDER_IMAGE_TABLE = "spider_image";
	public static String ASSEMBLE_TEXT_TABLE = "assemble_text";
	public static String ASSEMBLE_IMAGE_TABLE = "assemble_image";
	public static String DEPENDENCY = "dependency";
	public static String UNADD_IMAGE = "unadd_image";
	public static String UNADD_TEXT = "unadd_text";
	public static String FRAGMENT = "fragment";
	public static String ASSEMBLE_FRAGMENT_TABLE = "assemble_fragment";
	public static String RDF_TABLE = "rdf";
	
	public static int CONTENTLENGTH = 0;
	public static int DEPENDENCEMAX = 300;
	
}
