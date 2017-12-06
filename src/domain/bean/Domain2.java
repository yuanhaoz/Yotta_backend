package domain.bean;

import java.util.ArrayList;
import java.util.List;

import domainTopic.bean.DomainTopic;

/**  
 * 课程对象：包含主题信息  
 *  
 * @author 郑元浩 
 * @date 2017年12月5日 下午5:38:18 
 */
public class Domain2 extends Domain {
	List<DomainTopic> domainTopics = new ArrayList<DomainTopic>();

	public List<DomainTopic> getDomainTopics() {
		return domainTopics;
	}

	public void setDomainTopics(List<DomainTopic> domainTopics) {
		this.domainTopics = domainTopics;
	}

	@Override
	public String toString() {
		return "Domain2 [domainTopics=" + domainTopics + "]";
	}

	/**
	 * @param classID
	 * @param className
	 * @param domainTopics
	 */
	public Domain2(int classID, String className, List<DomainTopic> domainTopics) {
		super(classID, className);
		this.domainTopics = domainTopics;
	}

	/**
	 * 
	 */
	public Domain2() {
		super();
	}

	/**
	 * @param classID
	 * @param className
	 */
	public Domain2(int classID, String className) {
		super(classID, className);
	}
	
	
}
