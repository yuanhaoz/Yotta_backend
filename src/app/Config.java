package app;

import com.spreada.utils.chinese.ZHConverter;

/**
 * 
 * @author 郑元浩
 * @date 2016年11月25日14:20:20
 * @description 这是一个全局的配置文件
 */

public class Config {
	
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
//	public static String MYSQL_URL = "jdbc:mysql://202.117.54.39:3306/yotta?user=root&password=root&characterEncoding=UTF8";
//	public static String MYSQL_URL = "jdbc:mysql://202.117.54.39:3306/knowledgedomain?user=root&password=root&characterEncoding=UTF8";
//	public static String MYSQL_URL = "jdbc:mysql://123.139.159.38:9220/yotta2?user=shilei&password=shi&characterEncoding=UTF8";
	public static String MYSQL_URL = "jdbc:mysql://localhost:3306/yotta2?user=root&password=root&characterEncoding=UTF8";

	/**
	 * Quora 爬虫参数  配置
	 * @author 郑元浩
	 */
//	public static String Expi_PATH = "F:\\02-CQA网站中问题答案质量评估";
//	public static String QUORA_PATH = "F:\\02-CQA网站中问题答案质量评估\\00-NewDataSets";
	
	/**
	 * Baidu 爬虫参数  配置
	 * @author 郑元浩
	 */
//	public static String SERVER = "localhost:8080";
//	public static String PROJECT = "KG";
//	public static String IMAGE_API_PATH = "SpiderTest";
//	public static String CATALOG_TABLE = "datainput_catalog";
//	public static String RELATION_TABLE = "datainput_relation";
//	public static String TERM_TABLE = "datainput_term"; // term的表名
//	public static String SYSTEM_TABLE = "system_step_status";
//	public static int IMAGE_COUNT = 2; // 设置每个分面下的图片数量
	
	/**
	 * 中文维基  爬虫参数  配置
	 * @author 郑元浩
	 */
	public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
	public static String DOMAIN_TABLE = "domain";
	public static String FACET_TABLE = "facet";
	public static String FACET_RELATION_TABLE = "facet_relation";
	
//	public static String FACET_TABLE = "facet_ori";
//	public static String FACET_RELATION_TABLE = "facet_relation_ori";
	
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
	
//	public static String IP1="http://123.139.159.38";
	public static String IP1="http://localhost";
//	public static String IP2="http://123.139.159.38:9218";
	public static String IP2="http://localhost:8080";
	
	
	
	
}
