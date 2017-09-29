package assemble.bean;

/**  
 * 抽象类：分枝父类   
 * 
 * 1. totalbranchlevel: 分枝层次
 * 2. facet_name: 分枝（分面）名   
 * 3. totalbranchnum: 子分枝（子分面）数目
 * 4. type: 分枝（分面）默认都为"branch"
 * 5. List<***>: 每个子类自己实现
 * 6. totalleafnum: 叶子数量
 * 
 * 多余：
 * branchnum: 值为totalbranchnum相同，因此删除
 * facet_id: 没有用到，可以删除
 * 
 *  
 * @author 郑元浩 
 * @date 2016年12月4日
 */
public class Branch {
	
	public int totalbranchlevel;
	public String facet_name;
	public int totalbranchnum;
	public String type;
	public int totalleafnum;
	
	
	public int getTotalbranchlevel() {
		return totalbranchlevel;
	}
	public void setTotalbranchlevel(int totalbranchlevel) {
		this.totalbranchlevel = totalbranchlevel;
	}
	public String getFacet_name() {
		return facet_name;
	}
	public void setFacet_name(String facet_name) {
		this.facet_name = facet_name;
	}
	public int getTotalbranchnum() {
		return totalbranchnum;
	}
	public void setTotalbranchnum(int totalbranchnum) {
		this.totalbranchnum = totalbranchnum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTotalleafnum() {
		return totalleafnum;
	}
	public void setTotalleafnum(int totalleafnum) {
		this.totalleafnum = totalleafnum;
	}
	
	

}
