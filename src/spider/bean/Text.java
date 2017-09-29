package spider.bean;
/**  
 * 文本碎片类   
 *  
 * @author 郑元浩 
 * @date 2016年11月6日
 */
public class Text {
	
	public int fragmentID;
	public String fragmentContent;
	public String fragmentUrl;
	public String fragmentPostTime;
	public String fragmentScratchTime;
	public int termID;
	public String termName;
	public String className;
	public int getFragmentID() {
		return fragmentID;
	}
	public void setFragmentID(int fragmentID) {
		this.fragmentID = fragmentID;
	}
	public String getFragmentContent() {
		return fragmentContent;
	}
	public void setFragmentContent(String fragmentContent) {
		this.fragmentContent = fragmentContent;
	}
	public String getFragmentUrl() {
		return fragmentUrl;
	}
	public void setFragmentUrl(String fragmentUrl) {
		this.fragmentUrl = fragmentUrl;
	}
	public String getFragmentPostTime() {
		return fragmentPostTime;
	}
	public void setFragmentPostTime(String fragmentPostTime) {
		this.fragmentPostTime = fragmentPostTime;
	}
	public String getFragmentScratchTime() {
		return fragmentScratchTime;
	}
	public void setFragmentScratchTime(String fragmentScratchTime) {
		this.fragmentScratchTime = fragmentScratchTime;
	}
	public int getTermID() {
		return termID;
	}
	public void setTermID(int termID) {
		this.termID = termID;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @param fragmentID
	 * @param fragmentContent
	 * @param fragmentUrl
	 * @param fragmentPostTime
	 * @param fragmentScratchTime
	 * @param termID
	 * @param termName
	 * @param className
	 */
	public Text(int fragmentID, String fragmentContent, String fragmentUrl,
			String fragmentPostTime, String fragmentScratchTime, int termID,
			String termName, String className) {
		super();
		this.fragmentID = fragmentID;
		this.fragmentContent = fragmentContent;
		this.fragmentUrl = fragmentUrl;
		this.fragmentPostTime = fragmentPostTime;
		this.fragmentScratchTime = fragmentScratchTime;
		this.termID = termID;
		this.termName = termName;
		this.className = className;
	}
	/**
	 * 
	 */
	public Text() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
