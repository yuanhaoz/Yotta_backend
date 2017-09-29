package assemble.bean;

import java.util.List;

/**  
 * 树根（主题）
 * 1. totalbranchlevel: 分枝层次。对于树干都为2
 * 2. branchnum: 子分枝（子分面）数目
 * 3. term_id: 主题对应的ID
 * 4. name: 树干（主题）名字
 * 5. children: 子分枝元素集合（父类：Branch，子类为BranchSimple/BranchComplex）
 * 
 * 多余：
 * totalbranchnum: 之前一直设置为10，貌似是想表示子分枝的数目，重复
 * sortedbranchlist: 记录每个分面的叶子数量及其分面序号
 * totalleafnum: 记录总共碎片数量，貌似也没有什么软用
 *  
 * @author 郑元浩 
 * @date 2016年12月4日
 */
public class Tree {
	
	public int totalbranchlevel;
	public int branchnum;
	public int term_id;
	public String name;
	public List<Branch> children;
	
	
	public int getTotalbranchlevel() {
		return totalbranchlevel;
	}
	public void setTotalbranchlevel(int totalbranchlevel) {
		this.totalbranchlevel = totalbranchlevel;
	}
	public int getBranchnum() {
		return branchnum;
	}
	public void setBranchnum(int branchnum) {
		this.branchnum = branchnum;
	}
	public int getTerm_id() {
		return term_id;
	}
	public void setTerm_id(int term_id) {
		this.term_id = term_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Branch> getChildren() {
		return children;
	}
	public void setChildren(List<Branch> children) {
		this.children = children;
	}
	/**
	 * @param totalbranchlevel
	 * @param branchnum
	 * @param term_id
	 * @param name
	 * @param children
	 */
	public Tree(int totalbranchlevel, int branchnum, int term_id, String name,
			List<Branch> children) {
		super();
		this.totalbranchlevel = totalbranchlevel;
		this.branchnum = branchnum;
		this.term_id = term_id;
		this.name = name;
		this.children = children;
	}
	/**
	 * 
	 */
	public Tree() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
