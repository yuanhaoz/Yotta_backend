package statistics.bean;

import java.util.List;

/**  
 * 课程信息统计   
 *  
 * @author 郑元浩 
 * @date 2017年11月28日 上午10:10:21 
 */
public class DomainStatistics {
	
	private List<String> domainList;  // 所有课程列表
	private List<Integer> topicList; // 所有课程的主题数量列表
	private List<Integer> topicRelationList; // 所有课程的主题上下位关系数量列表
	private List<Integer> facetList;  // 所有课程的分面数量列表
	private List<Integer> facetRelationList; // 所有课程的分面关系数量列表
	private List<Integer> fragmentList;  // 所有课程的碎片数量列表
	private List<Integer> dependencyList;  // 所有课程的认知关系数量列表
	public List<String> getDomainList() {
		return domainList;
	}
	public void setDomainList(List<String> domainList) {
		this.domainList = domainList;
	}
	public List<Integer> getTopicList() {
		return topicList;
	}
	public void setTopicList(List<Integer> topicList) {
		this.topicList = topicList;
	}
	public List<Integer> getTopicRelationList() {
		return topicRelationList;
	}
	public void setTopicRelationList(List<Integer> topicRelationList) {
		this.topicRelationList = topicRelationList;
	}
	public List<Integer> getFacetList() {
		return facetList;
	}
	public void setFacetList(List<Integer> facetList) {
		this.facetList = facetList;
	}
	public List<Integer> getFacetRelationList() {
		return facetRelationList;
	}
	public void setFacetRelationList(List<Integer> facetRelationList) {
		this.facetRelationList = facetRelationList;
	}
	public List<Integer> getFragmentList() {
		return fragmentList;
	}
	public void setFragmentList(List<Integer> fragmentList) {
		this.fragmentList = fragmentList;
	}
	public List<Integer> getDependencyList() {
		return dependencyList;
	}
	public void setDependencyList(List<Integer> dependencyList) {
		this.dependencyList = dependencyList;
	}
	@Override
	public String toString() {
		return "DomainStatistics [domainList=" + domainList + ", topicList="
				+ topicList + ", topicRelationList=" + topicRelationList
				+ ", facetList=" + facetList + ", facetRelationList="
				+ facetRelationList + ", fragmentList=" + fragmentList
				+ ", dependencyList=" + dependencyList + "]";
	}
	/**
	 * @param domainList
	 * @param topicList
	 * @param topicRelationList
	 * @param facetList
	 * @param facetRelationList
	 * @param fragmentList
	 * @param dependencyList
	 */
	public DomainStatistics(List<String> domainList, List<Integer> topicList,
			List<Integer> topicRelationList, List<Integer> facetList,
			List<Integer> facetRelationList, List<Integer> fragmentList,
			List<Integer> dependencyList) {
		super();
		this.domainList = domainList;
		this.topicList = topicList;
		this.topicRelationList = topicRelationList;
		this.facetList = facetList;
		this.facetRelationList = facetRelationList;
		this.fragmentList = fragmentList;
		this.dependencyList = dependencyList;
	}
	/**
	 * 
	 */
	public DomainStatistics() {
		super();
	}
	
}
