package domainTopic.bean;

import java.util.List;

/**  
 * 存储主题之间的上下位关系: 子节点是主题元素   
 *  
 * @author 郑元浩 
 * @date 2016年12月20日
 */
public class Relation {

	public int topicID;
	public String topicName;
	public List<Topic> topicList;
	
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public List<Topic> getTopicList() {
		return topicList;
	}
	public void setTopicList(List<Topic> topicList) {
		this.topicList = topicList;
	}
	/**
	 * @param topicID
	 * @param topicName
	 * @param topicList
	 */
	public Relation(int topicID, String topicName, List<Topic> topicList) {
		super();
		this.topicID = topicID;
		this.topicName = topicName;
		this.topicList = topicList;
	}
	
	
	
}
