package assemble.bean;

import java.util.List;

/**  
 * 含有子分枝的一级分枝（含有子分面的一级分面）
 * 1. totalbranchlevel: 分枝层次。对于含有子分枝的分枝都为1
 * 2. facet_name: 分枝（分面）名   
 * 3. totalbranchnum: 子分枝（子分面）数目，对于含有子分枝的分枝为该分枝对应的子分枝的数目
 * 4. type: 分枝（分面）默认都为"branch"
 * 5. children: 子分枝元素集合
 * 6. totalleafnum: 子分枝元素数量
 * 
 * 多余：
 * branchnum: 值为totalbranchnum相同，因此删除
 * facet_id: 没有用到，可以删除
 * 
 *  
 * @author 郑元浩 
 * @date 2016年12月4日
 */
public class BranchComplex extends Branch{
	
	public List<BranchSimple> children;
	
	/**
	 * @param totalbranchlevel
	 * @param facet_name
	 * @param totalbranchnum
	 * @param type
	 * @param children
	 * @param totalleafnum
	 */
	public BranchComplex(int totalbranchlevel, String facet_name,
			int totalbranchnum, String type, List<BranchSimple> children,
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
