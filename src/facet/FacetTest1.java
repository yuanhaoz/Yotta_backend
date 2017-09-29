package facet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.mysqlUtils;
import app.Config;

/**  
 * 对分面的一些处理   
 *  
 * @author 郑元浩 
 * @date 2017年3月6日 上午9:15:08 
 */
public class FacetTest1 {

	public static void main(String[] args) {
		judge();
	}
	
	/**
	 * 判断一级分面是否会在二级分面中出现（对于数据结构这个领域的所有分面信息）
	 */
	public static void judge(){
		String className = "数据结构";
		int facetLayer = 1;
		Set<String> setFirst = getFacet(className, facetLayer);
		facetLayer = 2;
		Set<String> setSecond = getFacet(className, facetLayer);
		System.out.println(setFirst.toString());
		System.out.println(setSecond.toString());
		setFirst.retainAll(setSecond); // 求两个Set集合的交集，返回boolean判断是否存在交集。并且此时setFirst的值是交集后的结果。
		System.out.println(setFirst);
	}
	
	/**
	 * 得到某个领域某个主题的某一级所有分面信息
	 * @param className
	 * @param topicName
	 * @param facetLayer
	 * @return
	 */
	public static Set<String> getFacet(String className, int facetLayer) {
		Set<String> setLayer = new HashSet<String>();
		/**
		 * 读取facet，获得知识点的多级分面
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_TABLE + " where ClassName=? and FacetLayer=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(facetLayer);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String facetName = map.get("FacetName").toString();
				setLayer.add(facetName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return setLayer;
	}

}
