package dependency.bean;
/**  
 * 认知关系   
 *  
 * @author 郑元浩 
 * @date 2016年12月5日
 */
public class Dependency {
	
	public String className;
	public String start;
	public int startID;
	public String end;
	public int endID;
	public String confidence;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public int getStartID() {
		return startID;
	}
	public void setStartID(int startID) {
		this.startID = startID;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public int getEndID() {
		return endID;
	}
	public void setEndID(int endID) {
		this.endID = endID;
	}
	public String getConfidence() {
		return confidence;
	}
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}
	/**
	 * @param className
	 * @param start
	 * @param startID
	 * @param end
	 * @param endID
	 * @param confidence
	 */
	public Dependency(String className, String start, int startID, String end,
			int endID, String confidence) {
		super();
		this.className = className;
		this.start = start;
		this.startID = startID;
		this.end = end;
		this.endID = endID;
		this.confidence = confidence;
	}
	/**
	 * 
	 */
	public Dependency() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Dependency [className=" + className + ", start=" + start
				+ ", startID=" + startID + ", end=" + end + ", endID=" + endID
				+ ", confidence=" + confidence + "]";
	}
	
	
	
	
}
