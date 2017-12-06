package statistics.bean;

import java.util.List;

/**  
 * 主题统计信息   
 *  
 * @author 郑元浩 
 * @date 2017年12月5日 下午8:03:46 
 */
public class TopicStatistics {

	private List<String> topicList; // 所有主题列表
	private List<Integer> facetList; // 所有主题的所有分面数量列表
	private List<Integer> facetFirstList; // 所有主题的一级分面数量列表
	private List<Integer> facetSecondList; // 所有主题的二级分面数量列表
	private List<Integer> facetThirdList; // 所有主题的三级分面数量列表
	private List<Integer> dependencyList; // 所有主题的认知关系数量列表
	private List<Integer> fragmentList; // 所有主题的碎片数量列表
	public List<String> getTopicList() {
		return topicList;
	}
	public void setTopicList(List<String> topicList) {
		this.topicList = topicList;
	}
	public List<Integer> getFacetList() {
		return facetList;
	}
	public void setFacetList(List<Integer> facetList) {
		this.facetList = facetList;
	}
	public List<Integer> getFacetFirstList() {
		return facetFirstList;
	}
	public void setFacetFirstList(List<Integer> facetFirstList) {
		this.facetFirstList = facetFirstList;
	}
	public List<Integer> getFacetSecondList() {
		return facetSecondList;
	}
	public void setFacetSecondList(List<Integer> facetSecondList) {
		this.facetSecondList = facetSecondList;
	}
	public List<Integer> getFacetThirdList() {
		return facetThirdList;
	}
	public void setFacetThirdList(List<Integer> facetThirdList) {
		this.facetThirdList = facetThirdList;
	}
	public List<Integer> getDependencyList() {
		return dependencyList;
	}
	public void setDependencyList(List<Integer> dependencyList) {
		this.dependencyList = dependencyList;
	}
	public List<Integer> getFragmentList() {
		return fragmentList;
	}
	public void setFragmentList(List<Integer> fragmentList) {
		this.fragmentList = fragmentList;
	}
	@Override
	public String toString() {
		return "TopicStatistics [topicList=" + topicList + ", facetList="
				+ facetList + ", facetFirstList=" + facetFirstList
				+ ", facetSecondList=" + facetSecondList + ", facetThirdList="
				+ facetThirdList + ", dependencyList=" + dependencyList
				+ ", fragmentList=" + fragmentList + "]";
	}
	/**
	 * @param topicList
	 * @param facetList
	 * @param facetFirstList
	 * @param facetSecondList
	 * @param facetThirdList
	 * @param dependencyList
	 * @param fragmentList
	 */
	public TopicStatistics(List<String> topicList, List<Integer> facetList,
			List<Integer> facetFirstList, List<Integer> facetSecondList,
			List<Integer> facetThirdList, List<Integer> dependencyList,
			List<Integer> fragmentList) {
		super();
		this.topicList = topicList;
		this.facetList = facetList;
		this.facetFirstList = facetFirstList;
		this.facetSecondList = facetSecondList;
		this.facetThirdList = facetThirdList;
		this.dependencyList = dependencyList;
		this.fragmentList = fragmentList;
	}
	/**
	 * 
	 */
	public TopicStatistics() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
