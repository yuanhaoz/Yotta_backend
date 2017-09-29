package assemble.bean;
/**  
 * 树叶节点（碎片: 文本或图片）
 * 1. url: 碎片链接
 * 2. content: 碎片内容
 * 3. fragment_id: 碎片id
 * 4. type: 碎片类型（leaf）
 * 5. flag: 判断是文本还是图片碎片的标志位
 * 
 * 多余：name属性原来的数据中是有的  
 *  
 * @author 郑元浩 
 * @date 2016年12月4日
 */
public class Leaf {
	
	public String url;
	public String content;
	public String fragment_id;
	public String scratchTime;
	public String type;
	public String flag;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFragment_id() {
		return fragment_id;
	}
	public void setFragment_id(String fragment_id) {
		this.fragment_id = fragment_id;
	}
	public String getScratchTime() {
		return scratchTime;
	}
	public void setScratchTime(String scratchTime) {
		this.scratchTime = scratchTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @param url
	 * @param content
	 * @param fragment_id
	 * @param scratchTime
	 * @param type
	 * @param flag
	 */
	public Leaf(String url, String content, String fragment_id,
			String scratchTime, String type, String flag) {
		super();
		this.url = url;
		this.content = content;
		this.fragment_id = fragment_id;
		this.scratchTime = scratchTime;
		this.type = type;
		this.flag = flag;
	}
	/**
	 * 
	 */
	public Leaf() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
