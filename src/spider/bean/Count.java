package spider.bean;
/**  
 * 文本和图片的数量统计   
 *  
 * @author 郑元浩 
 * @date 2016年12月3日
 */
public class Count {
	
	public String type;
	public int number;
	
	/**
	 * @param type
	 * @param number
	 */
	public Count(String type, int number) {
		super();
		this.type = type;
		this.number = number;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

}
