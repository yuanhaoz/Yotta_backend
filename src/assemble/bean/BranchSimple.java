package assemble.bean;

import java.util.List;

/**  
 * 叶子分枝（含有碎片的分面，没有子分面）（部分一级分枝或者二级分枝）
 * 1. totalbranchlevel: 分枝层次。对于叶子分枝都为0
 * 2. facet_name: 分枝（分面）名   
 * 3. totalbranchnum: 子分枝（子分面）数目，对于叶子分枝都为0
 * 4. type: 分枝（分面）默认都为"branch"
 * 5. children: 叶子元素集合
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

public class BranchSimple extends Branch{
	
	public List<Leaf> children;
	
	/**
	 * @param totalbranchlevel
	 * @param facet_name
	 * @param totalbranchnum
	 * @param type
	 * @param children
	 * @param totalleafnum
	 */
	public BranchSimple(int totalbranchlevel, String facet_name,
			int totalbranchnum, String type, List<Leaf> children,
			int totalleafnum) {
		super();
		this.totalbranchlevel = totalbranchlevel;
		this.facet_name = facet_name;
		this.totalbranchnum = totalbranchnum;
		this.type = type;
		this.children = children;
		this.totalleafnum = totalleafnum;
	}
	

}
