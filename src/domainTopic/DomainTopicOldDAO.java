package domainTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domainTopic.bean.DomainTopic;
import domainTopic.bean.Rela;
import domainTopic.bean.Topic;
import utils.mysqlUtils;
import app.Config;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年12月5日
 */
public class DomainTopicOldDAO {

	public static void main(String[] args) {
//		Log.log(getDomainTopic("数据结构", "八叉树"));
		modifyTopic("数据结构", 1, new DomainTopic("抽象资料型别", "https://zh.wikipedia.org/wiki/%E6%8A%BD%E8%B1%A1%E8%B3%87%E6%96%99%E5%9E%8B%E5%88%A5", 1));
		modifyTopic("数据结构", 83, new DomainTopic("树 (数据结构)", "https://zh.wikipedia.org/wiki/%E6%A0%91_(%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)", 3));
	}
	
	/**
	 * 返回一个主题的termID
	 * @param className
	 * @param termName
	 * @return
	 */
	public static int getDomainTopic(String className, String termName) {
		int termID = 0;
		/**
		 * 读取domain_topic，获得每一层知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(termName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			/**
			 * 一个关键词只有一个ID，返回一条记录
			 */
			if (results.size() == 1) {
				termID = Integer.parseInt(results.get(0).get("TermID").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return termID;
	}
	
	/**
	 * 返回所有主题
	 * @param className
	 * @return
	 */
	public static List<String> getDomainTopicList(String className) {
		List<String> topicList = new ArrayList<String>();
		/**
		 * 读取domain_topic，获得每一层知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String topic = map.get("TermName").toString();
//				Log.log(topic);
				topicList.add(topic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return topicList;
	}
	
	/**
	 * 返回一门课程的所有领域主题及其层数
	 * @param className
	 * @return
	 */
	public static List<DomainTopic> getDomainTopics(String className) {
		List<DomainTopic> domainTopicList = new ArrayList<DomainTopic>();
		/**
		 * 读取domain_topic，获得每一层知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String topicName = map.get("TermName").toString();
				String topicUrl = map.get("TermUrl").toString();
				int topicLayer = Integer.parseInt(map.get("TermLayer").toString());
				DomainTopic domainTopic = new DomainTopic(topicName, topicUrl, topicLayer);
				domainTopicList.add(domainTopic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return domainTopicList;
	}
	
	/**
	 * 返回一门课程的所有主题之间的上下位关系：上位主题和下位主题
	 * @param className
	 * @return
	 */
	public static List<Rela> getRelationList(String className) {
		List<Rela> relaList = new ArrayList<Rela>();
		/**
		 * 读取domain_topic，获得每一层知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_RELATION_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String parent = map.get("Parent").toString();
				String child = map.get("Child").toString();
				Rela topic = new Rela(parent, child);
				relaList.add(topic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return relaList;
	}
	
	/**
	 * 返回一门课程的所有主题之间的上下位关系，按照一门课的主题进行关系查询
	 * @param className
	 * @return
	 */
	public static List<Rela> getRelationList(String className, String parentTopic) {
		List<Rela> relaList = new ArrayList<Rela>();
		/**
		 * 读取domain_topic，获得每一层知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_RELATION_TABLE + " where ClassName=? and Parent=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(parentTopic);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String parent = map.get("Parent").toString();
				String child = map.get("Child").toString();
				Rela topic = new Rela(parent, child);
				relaList.add(topic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return relaList;
	}
	
	/**
	 * 返回一门课程的所有领域术语及其层数
	 * @param className
	 * @return
	 */
	public static List<DomainTopic> getDomainTerms(String className) {
		List<DomainTopic> domainTermList = new ArrayList<DomainTopic>();
		/**
		 * 读取domain_term，获得每一层领域术语
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_LAYER_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				String topicName = map.get("TermName").toString();
				String topicUrl = map.get("TermUrl").toString();
				int topicLayer = Integer.parseInt(map.get("TermLayer").toString());
				DomainTopic domainTopic = new DomainTopic(topicName, topicUrl, topicLayer);
				domainTermList.add(domainTopic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return domainTermList;
	}
	
	/**
	 * 更新一门课程的某个知识主题ID对应的知识主题名
	 * @param className
	 * @param termID
	 * @param termName
	 */
	public static void modifyTopic(String className, int termID, DomainTopic topic) {
		/**
		 * 修改domain_topic
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "update " + Config.DOMAIN_TOPIC_TABLE + " set TermName=?,TermLayer=?,TermUrl=? where ClassName=? and TermID=?";
		List<Object> params = new ArrayList<Object>();
		params.add(topic.getTermName());
		params.add(topic.getTermLayer());
		params.add(topic.getTermUrl());
		params.add(className);
		params.add(termID);
		try {
			mysql.addDeleteModify(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
	}
	
	///////////////66666666666666666666666666666666//////////////////////
	private static int topicNum = 0;
	/**
	 * 递归实现获取所有主题关系数据
	 * 递归很关键，很强势
	 * @param className
	 * @param topicNeed
	 * @return
	 */
	public static Topic getRelationAll(String className, String topicNeed){
		/**
		 * 用于存储该父亲节点的所有下位主题信息
		 */
		Topic topicAll = new Topic();
		
//		String topicNeed = "数据结构";
		Object data = "";
//		int parentID = DomainTopicOldDAO.getDomainTopic(className, topicNeed); // 得到父主题ID
		int parentID = topicNum++; // 得到父主题ID
		List<Topic> childrenList = new ArrayList<Topic>();

		/**
		 * 获取该知识主题相关的上下位关系的主题信息
		 */
		List<Rela> relaList = DomainTopicOldDAO.getRelationList(className, topicNeed);
		if (relaList.size() != 0) {
			/**
			 * 存在上下位关系的节点，递归实现
			 */
			for(int i = 0; i < relaList.size(); i++){
				Rela rela = relaList.get(i);
				String child = rela.getChild();
				System.out.println(rela.getParent() + "-->" + rela.getChild());
				if (rela.getParent().equals(rela.getChild()) || rela.getChild().equals(className)) {
					continue;
				}
				Topic topicChild = getRelationAll(className, child);
				childrenList.add(topicChild);
			}
		} else {
//			childrenList.add(null);
		}
		
		/**
		 * 不存在上下位关系节点，直接保存该节点到总的信息链表中
		 * 将该上下位关系保存到结果集合中
		 */
		topicAll.setId(parentID);
		topicAll.setName(topicNeed);
		topicAll.setData(data);
		topicAll.setChildren(childrenList);

		return topicAll;
		
	}

}
