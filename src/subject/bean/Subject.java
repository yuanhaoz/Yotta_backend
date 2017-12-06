package subject.bean;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2017年12月4日 下午8:48:02 
 */
public class Subject {

	private int subjectID;
	private String subjectName;
	private String note;
	public int getSubjectID() {
		return subjectID;
	}
	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public String toString() {
		return "Subject [subjectID=" + subjectID + ", subjectName="
				+ subjectName + ", note=" + note + "]";
	}
	/**
	 * @param subjectID
	 * @param subjectName
	 * @param note
	 */
	public Subject(int subjectID, String subjectName, String note) {
		super();
		this.subjectID = subjectID;
		this.subjectName = subjectName;
		this.note = note;
	}
	/**
	 * 
	 */
	public Subject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
