package assemble;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.mysqlUtils;
import app.Config;
import assemble.bean.Leaf;

/**  
 * 装配基本函数
 * 1. 按照主题分面返回碎片集合作为实例化主题分面树的树叶   
 * 2. 按照主题分面返回文本集合作为实例化主题分面树的树叶 （不再使用）  
 * 3. 按照主题分面返回图片集合作为实例化主题分面树的树叶 （不再使用）  
 *  
 * @author 郑元浩 
 * @date 2016年12月5日
 */
public class AssembleDAO {
	
	/**
	 * 读取指定领域名、主题名、分面名下的碎片集合作为实例化主题分面树的树叶
	 * @param className
	 * @param topicName
	 * @param facetName
	 * @return
	 */
	public static List<Leaf> getFragmentByFacet(String className, String topicName, String facetName) {
		List<Leaf> leafList = new ArrayList<Leaf>();
		
		/**
		 * 读取assemble_fragment，获得知识点某分面下的文本碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and FacetName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		params.add(facetName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String url = "";
				String content = map.get("FragmentContent").toString();
				String scratchTime = map.get("FragmentScratchTime").toString();
				String fragment_id = facetName + "_" + (i+1);
				String type = "leaf";
				String flag = "fragment";
				Leaf leaf = new Leaf(url, content, fragment_id, scratchTime, type, flag);
				leafList.add(leaf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return leafList;
	}
	
	/**
	 * 读取指定领域名、主题名、分面名下的文本碎片集合作为实例化主题分面树的树叶
	 * @param className
	 * @param topicName
	 * @param facetName
	 * @return
	 */
	public static List<Leaf> getTextByFacet(String className, String topicName, String facetName) {
		List<Leaf> leafList = new ArrayList<Leaf>();
		
		/**
		 * 读取assemble_text，获得知识点某分面下的文本碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=? and FacetName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		params.add(facetName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String url = map.get("FragmentUrl").toString();
				String content = map.get("FragmentContent").toString();
				String scratchTime = map.get("FragmentScratchTime").toString();
				String fragment_id = facetName + "_" + (i+1);
				String type = "leaf";
				String flag = "text";
				Leaf leaf = new Leaf(url, content, fragment_id, scratchTime, type, flag);
				leafList.add(leaf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return leafList;
	}
	
	/**
	 * 读取指定领域名、主题名、分面名下的图片碎片集合作为实例化主题分面树的树叶
	 * @param className
	 * @param topicName
	 * @param facetName
	 * @return
	 */
	public static List<Leaf> getImageByFacet(String className, String topicName, String facetName) {
		List<Leaf> leafList = new ArrayList<Leaf>();
		
		/**
		 * 读取assemble_image，获得知识点某分面下的图片碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=? and FacetName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		params.add(facetName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String url = map.get("TermUrl").toString();
				String content = map.get("ImageUrl").toString();
				String scratchTime = map.get("ImageScratchTime").toString();
				String fragment_id = facetName + "_" + (i+1);
				String type = "leaf";
				String flag = "image";
				Leaf leaf = new Leaf(url, content, fragment_id, scratchTime, type, flag);
				leafList.add(leaf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return leafList;
	}

}
