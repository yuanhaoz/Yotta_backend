package statistics.bean;

import java.util.List;
import java.util.Map;

/**  
 * 主题信息：用于echarts使用   
 *  
 * @author 郑元浩 
 * @date 2017年12月6日 上午9:55:23 
 */
public class TopicDetail {
	
	private List<String> facets;
	private List<Map<String, Object>> totals;
	private List<Map<String, Object>> details;
	public List<String> getFacets() {
		return facets;
	}
	public void setFacets(List<String> facets) {
		this.facets = facets;
	}
	public List<Map<String, Object>> getTotals() {
		return totals;
	}
	public void setTotals(List<Map<String, Object>> totals) {
		this.totals = totals;
	}
	public List<Map<String, Object>> getDetails() {
		return details;
	}
	public void setDetails(List<Map<String, Object>> details) {
		this.details = details;
	}
	@Override
	public String toString() {
		return "TopicDetail [facets=" + facets + ", totals=" + totals
				+ ", details=" + details + "]";
	}
	/**
	 * @param facets
	 * @param totals
	 * @param details
	 */
	public TopicDetail(List<String> facets, List<Map<String, Object>> totals,
			List<Map<String, Object>> details) {
		super();
		this.facets = facets;
		this.totals = totals;
		this.details = details;
	}
	/**
	 * 
	 */
	public TopicDetail() {
		super();
		// TODO Auto-generated constructor stub
	}
}
