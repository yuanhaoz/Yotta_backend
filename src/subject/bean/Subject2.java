package subject.bean;

import java.util.List;

import domain.bean.Domain2;

/**  
 * 学科对象：包含课程信息，课程信息包含主题信息
 *  
 * @author 郑元浩 
 * @date 2017年12月5日 下午5:15:31 
 */
public class Subject2 extends Subject{

	private List<Domain2> domains;

	public List<Domain2> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain2> domains) {
		this.domains = domains;
	}

	@Override
	public String toString() {
		return "Subject2 [domains=" + domains + "]";
	}

	/**
	 * 
	 */
	public Subject2() {
		super();
	}

	/**
	 * @param subjectID
	 * @param subjectName
	 * @param note
	 */
	public Subject2(int subjectID, String subjectName, String note) {
		super(subjectID, subjectName, note);
	}

	/**
	 * @param subjectID
	 * @param subjectName
	 * @param note
	 * @param domains
	 */
	public Subject2(int subjectID, String subjectName, String note,
			List<Domain2> domains) {
		super(subjectID, subjectName, note);
		this.domains = domains;
	}
	
}
