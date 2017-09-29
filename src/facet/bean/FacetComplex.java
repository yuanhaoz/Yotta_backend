package facet.bean;

import java.util.List;

/**  
 * 复杂的分面对象：用于主题分面树显示，只考虑的两级分面的情况
 * 1. 一层分面名
 * 2. 二层分面名
 * 
 * 存在三级分面（程序猿开发中...）:再一次调用该对象
 *  
 * @author 郑元浩 
 * @date 2016年12月5日
 */
public class FacetComplex {
	
	public String firstFacet;
	public List<String> secondFacet;
	
	public String getFirstFacet() {
		return firstFacet;
	}
	public void setFirstFacet(String firstFacet) {
		this.firstFacet = firstFacet;
	}
	public List<String> getSecondFacet() {
		return secondFacet;
	}
	public void setSecondFacet(List<String> secondFacet) {
		this.secondFacet = secondFacet;
	}
	
	/**
	 * @param firstFacet
	 * @param secondFacet
	 */
	public FacetComplex(String firstFacet, List<String> secondFacet) {
		super();
		this.firstFacet = firstFacet;
		this.secondFacet = secondFacet;
	}
	
	/**
	 * 
	 */
	public FacetComplex() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
