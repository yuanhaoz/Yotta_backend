package domainTopic.bean;
/**  
 * 存在上下位关系的两个主题   
 *  
 * @author 郑元浩 
 * @date 2016年12月20日
 */
public class Rela {
	
	public String parent;
	public String child;
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	/**
	 * @param parent
	 * @param child
	 */
	public Rela(String parent, String child) {
		super();
		this.parent = parent;
		this.child = child;
	}

	
	
}
