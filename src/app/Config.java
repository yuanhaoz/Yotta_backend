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
	public static String DBNAME = "yotta_create";
	public static String HOST = "localhost";
	public static String USERNAME = "root";
	public static String PASSWD = "root";
	public static int PORT = 3306;
//	public static String MYSQL_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME + 
//			"?user=" + USERNAME + "&password=" + PASSWD + "&characterEncoding=UTF8&serverTimezone=UTC"; // 阿里云服务器：域名+http端口
	public static String MYSQL_URL = "jdbc:mysql://localhost:9220/yotta?user=root&password=root&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口
	public static String IP1="http://yotta.xjtushilei.com:888"; // 跨域访问控制：域名+apache端口
	public static String IP2="http://yotta.xjtushilei.com:9218/Yotta"; // 阿里云服务器：域名+http端口
	
//	public static String MYSQL_URL = "jdbc:mysql://localhost:9220/yotta?user=root&password=root&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口
//	public static String IP1="http://yotta.xjtushilei.com:888"; // 跨域访问控制：域名+apache端口
//	public static String IP2="http://yotta.xjtushilei.com:9218/Yotta"; // 阿里云服务器：域名+http端口
	
	/**
	 * 数据库  配置
	 * @author 郑元浩
	 */
	public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
	public static String USER_INFO = "user_info";
	public static String USER_LOG = "user_log";
	public static String SUBJECT_TABLE = "subject";
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
	
	/**
	 * 认知关系算法参数：gephi-tookit
	 */
	public static int FILTERDEGREE = 2;
	public static String GEXFPATH = "F:\\gexfpath";
	
}
