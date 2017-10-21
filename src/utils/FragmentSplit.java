package utils;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2017年10月20日 下午7:49:09 
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentSplit {
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
	
	public static void main(String[] args) throws Exception {
		/* 读入TXT文件 */  
//        String pathname = "C:\\Users\\DELL\\Desktop\\1.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径  
//        File filename = new File(pathname); // 要读取以上路径的input。txt文件  
//        InputStreamReader reader = new InputStreamReader(  
//                new FileInputStream(filename)); // 建立一个输入流对象reader  
//        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
//        String line = "";  
//        String out = "";
//        line = br.readLine();  
//        while (line != null) {  
//            line = br.readLine(); // 一次读入一行数据  
//            out += line;
//        }  
//        System.out.println(getTextFromHtml(out));
//        br.close();
//        reader.close();
//        getImageFromHtml(out);
        
	}

	public static String delHTMLTag(String htmlStr) {
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签
		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签
		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
		return htmlStr.trim(); // 返回文本字符串
	}

	public static String getTextFromHtml(String htmlStr) {
		htmlStr = delHTMLTag(htmlStr);
		htmlStr = htmlStr.replaceAll(" ", "");
		htmlStr = htmlStr.substring(0, htmlStr.length());
		return htmlStr;
	}

	public static String getImageFromHtml(String content) {
		String ImageUrl = "";
		if (content.indexOf("src") > 0) {
			String[] s = content.split("src");
			String s1 = s[1].substring(s[1].indexOf("\""));
			String s2 = s1.substring(1);
			String s3=s2.substring(0, s2.indexOf('\"'));
			//System.out.println(s3);
			ImageUrl=s3;
		}
		return ImageUrl;
	}

}
