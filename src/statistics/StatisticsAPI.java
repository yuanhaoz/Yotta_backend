package statistics;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import statistics.bean.DomainStatistics;
import statistics.bean.TopicDetail;
import statistics.bean.TopicStatistics;
import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;

/**  
 * 知识森林数据统计API   
 *  
 * @author 郑元浩 
 * @date 2017年11月28日 上午10:05:23 
 */
@Path("/StatisticsAPI")
@Api(value = "StatisticsAPI")
public class StatisticsAPI {

	public static void main(String[] args) {
//		getDomainInfo();
//		getDomainInfoBySubject("计算机科学");
//		getTopicInfoByDomain("Java");
		getTopicDetail("Java", "升阳专业认证");
	}
	
	@GET
	@Path("/getDomainInfo")
	@ApiOperation(value = "获得所有领域信息", notes = "根据主题、主题关系、分面、分面关系、碎片等五个维度，获得所有领域信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainInfo() {
		Response response = null;
		DomainStatistics domainStatistics = new DomainStatistics();
		List<String> domainList = new ArrayList<String>();
		List<Integer> topicList = new ArrayList<Integer>();
		List<Integer> topicRelationList = new ArrayList<Integer>();
		List<Integer> facetList = new ArrayList<Integer>();
		List<Integer> facetRelationList = new ArrayList<Integer>();
		List<Integer> fragmentList = new ArrayList<Integer>();
		List<Integer> dependencyList = new ArrayList<Integer>();
		/**
		 * 读取domain，得到所有领域名
		 */
		mysqlUtils mysql = new mysqlUtils();
		List<Object> params = new ArrayList<Object>();
		String sqlDomain = "select ClassName from " + Config.DOMAIN_TABLE;
		String sqlTopic = "SELECT d.ClassName, Count(dt.TermID) AS dtc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DOMAIN_TOPIC_TABLE + " AS dt ON d.ClassName = dt.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlTopicRelation = "SELECT d.ClassName, Count(dtr.Parent) AS dtrc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DOMAIN_TOPIC_RELATION_TABLE + " AS dtr ON d.ClassName = dtr.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFacet = "SELECT d.ClassName, Count(f.TermID) AS fc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.FACET_TABLE + " AS f ON d.ClassName = f.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFacetRelation = "SELECT d.ClassName, Count(fr.ChildFacet) AS frc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.FACET_RELATION_TABLE + " AS fr ON d.ClassName = fr.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFragment = "SELECT d.ClassName, Count(af.FragmentID) AS afc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.ASSEMBLE_FRAGMENT_TABLE + " AS af ON d.ClassName = af.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlDepenency = "SELECT d.ClassName, Count(dp.StartID) AS dpc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DEPENDENCY + " AS dp ON d.ClassName = dp.ClassName GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		try {
			// 领域
			List<Map<String, Object>> resultDomain = mysql.returnMultipleResult(sqlDomain, params);
			Log.log(resultDomain);
			for (int i = 0; i < resultDomain.size(); i++) {
				domainList.add(resultDomain.get(i).get("ClassName").toString());
			}
			// 每个领域的主题数量
			List<Map<String, Object>> resultTopic = mysql.returnMultipleResult(sqlTopic, params);
			Log.log(resultTopic);
			int topicSum = 0;
			for (int i = 0; i < resultTopic.size(); i++) {
				topicSum += Integer.parseInt(resultTopic.get(i).get("dtc").toString());
				topicList.add(Integer.parseInt(resultTopic.get(i).get("dtc").toString()));
			}
			topicList.add(0, topicSum); // 第一个数字为所有领域主题数量的和，下面的类似
			Log.log(topicList);
			// 每个领域的主题上下位关系数量
			List<Map<String, Object>> resultTopicRelation = mysql.returnMultipleResult(sqlTopicRelation, params);
			Log.log(resultTopicRelation);
			int topicRelationSum = 0;
			for (int i = 0; i < resultTopicRelation.size(); i++) {
				topicRelationSum += Integer.parseInt(resultTopicRelation.get(i).get("dtrc").toString());
				topicRelationList.add(Integer.parseInt(resultTopicRelation.get(i).get("dtrc").toString()));
			}
			topicRelationList.add(0, topicRelationSum);
			Log.log(topicRelationList);
			// 每个领域的分面数量
			List<Map<String, Object>> resultFacet = mysql.returnMultipleResult(sqlFacet, params);
			Log.log(resultFacet);
			int facetSum = 0;
			for (int i = 0; i < resultFacet.size(); i++) {
				facetSum += Integer.parseInt(resultFacet.get(i).get("fc").toString());
				facetList.add(Integer.parseInt(resultFacet.get(i).get("fc").toString()));
			}
			facetList.add(0, facetSum);
			Log.log(facetList);
			// 每个领域的分面关系数量
			List<Map<String, Object>> resultFacetRelation = mysql.returnMultipleResult(sqlFacetRelation, params);
			Log.log(resultFacetRelation);
			int facetRelationSum = 0;
			for (int i = 0; i < resultFacetRelation.size(); i++) {
				facetRelationSum += Integer.parseInt(resultFacetRelation.get(i).get("frc").toString());
				facetRelationList.add(Integer.parseInt(resultFacetRelation.get(i).get("frc").toString()));
			}
			facetRelationList.add(0, facetRelationSum);
			Log.log(facetRelationList);
			// 每个领域的碎片数量
			List<Map<String, Object>> resultFragment = mysql.returnMultipleResult(sqlFragment, params);
			Log.log(resultFragment);
			int fragmentSum = 0;
			for (int i = 0; i < resultFragment.size(); i++) {
				fragmentSum += Integer.parseInt(resultFragment.get(i).get("afc").toString());
				fragmentList.add(Integer.parseInt(resultFragment.get(i).get("afc").toString()));
			}
			fragmentList.add(0, fragmentSum);
			Log.log(fragmentList);
			// 每个领域的碎片数量
			List<Map<String, Object>> resultDependency = mysql.returnMultipleResult(sqlDepenency, params);
			Log.log(resultDependency);
			int DependencySum = 0;
			for (int i = 0; i < resultDependency.size(); i++) {
				DependencySum += Integer.parseInt(resultDependency.get(i).get("dpc").toString());
				dependencyList.add(Integer.parseInt(resultDependency.get(i).get("dpc").toString()));
			}
			dependencyList.add(0, DependencySum);
			Log.log(dependencyList);
			// 设置返回对象
			domainStatistics.setDomainList(domainList);
			domainStatistics.setTopicList(topicList);
			domainStatistics.setTopicRelationList(topicRelationList);
			domainStatistics.setFacetList(facetList);
			domainStatistics.setFacetRelationList(facetRelationList);
			domainStatistics.setFragmentList(fragmentList);
			domainStatistics.setDependencyList(dependencyList);
			response = Response.status(200).entity(domainStatistics).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getDomainInfoBySubject")
	@ApiOperation(value = "根据学科获得领域信息", notes = "根据主题、主题关系、分面、分面关系、碎片等五个维度，获得指定学科下的领域信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainInfoBySubject(
			@DefaultValue("计算机科学") @ApiParam(value = "学科", required = true) @QueryParam("SubjectName") String subject
		) {
		Response response = null;
		DomainStatistics domainStatistics = new DomainStatistics();
		List<String> domainList = new ArrayList<String>();
		List<Integer> topicList = new ArrayList<Integer>();
		List<Integer> topicRelationList = new ArrayList<Integer>();
		List<Integer> facetList = new ArrayList<Integer>();
		List<Integer> facetRelationList = new ArrayList<Integer>();
		List<Integer> fragmentList = new ArrayList<Integer>();
		List<Integer> dependencyList = new ArrayList<Integer>();
		/**
		 * 读取domain，得到所有领域名
		 */
		mysqlUtils mysql = new mysqlUtils();
		List<Object> params = new ArrayList<Object>();
		if (subject == null || subject.equals("")) {
			subject = "计算机科学";
		}
		params.add(subject);
		String sqlDomain = "select ClassName from " + Config.DOMAIN_TABLE + " where SubjectName = ?";
		String sqlTopic = "SELECT d.SubjectName, d.ClassName, Count(dt.TermID) AS dtc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DOMAIN_TOPIC_TABLE + " AS dt ON d.ClassName = dt.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlTopicRelation = "SELECT d.SubjectName, d.ClassName, Count(dtr.Parent) AS dtrc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DOMAIN_TOPIC_RELATION_TABLE + " AS dtr ON d.ClassName = dtr.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFacet = "SELECT d.SubjectName, d.ClassName, Count(f.TermID) AS fc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.FACET_TABLE + " AS f ON d.ClassName = f.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFacetRelation = "SELECT d.SubjectName, d.ClassName, Count(fr.ChildFacet) AS frc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.FACET_RELATION_TABLE + " AS fr ON d.ClassName = fr.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlFragment = "SELECT d.SubjectName, d.ClassName, Count(af.FragmentID) AS afc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.ASSEMBLE_FRAGMENT_TABLE + " AS af ON d.ClassName = af.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		String sqlDepenency = "SELECT d.SubjectName, d.ClassName, Count(dp.StartID) AS dpc FROM " + Config.DOMAIN_TABLE + " AS d LEFT JOIN " + Config.DEPENDENCY + " AS dp ON d.ClassName = dp.ClassName WHERE d.SubjectName = ? GROUP BY d.ClassName ORDER BY d.ClassID ASC";
		try {
			// 领域
			List<Map<String, Object>> resultDomain = mysql.returnMultipleResult(sqlDomain, params);
			Log.log(resultDomain);
			for (int i = 0; i < resultDomain.size(); i++) {
				domainList.add(resultDomain.get(i).get("ClassName").toString());
			}
			// 每个领域的主题数量
			List<Map<String, Object>> resultTopic = mysql.returnMultipleResult(sqlTopic, params);
			Log.log(resultTopic);
			int topicSum = 0;
			for (int i = 0; i < resultTopic.size(); i++) {
				topicSum += Integer.parseInt(resultTopic.get(i).get("dtc").toString());
				topicList.add(Integer.parseInt(resultTopic.get(i).get("dtc").toString()));
			}
			topicList.add(0, topicSum); // 第一个数字为所有领域主题数量的和，下面的类似
			Log.log(topicList);
			// 每个领域的主题上下位关系数量
			List<Map<String, Object>> resultTopicRelation = mysql.returnMultipleResult(sqlTopicRelation, params);
			Log.log(resultTopicRelation);
			int topicRelationSum = 0;
			for (int i = 0; i < resultTopicRelation.size(); i++) {
				topicRelationSum += Integer.parseInt(resultTopicRelation.get(i).get("dtrc").toString());
				topicRelationList.add(Integer.parseInt(resultTopicRelation.get(i).get("dtrc").toString()));
			}
			topicRelationList.add(0, topicRelationSum);
			Log.log(topicRelationList);
			// 每个领域的分面数量
			List<Map<String, Object>> resultFacet = mysql.returnMultipleResult(sqlFacet, params);
			Log.log(resultFacet);
			int facetSum = 0;
			for (int i = 0; i < resultFacet.size(); i++) {
				facetSum += Integer.parseInt(resultFacet.get(i).get("fc").toString());
				facetList.add(Integer.parseInt(resultFacet.get(i).get("fc").toString()));
			}
			facetList.add(0, facetSum);
			Log.log(facetList);
			// 每个领域的分面关系数量
			List<Map<String, Object>> resultFacetRelation = mysql.returnMultipleResult(sqlFacetRelation, params);
			Log.log(resultFacetRelation);
			int facetRelationSum = 0;
			for (int i = 0; i < resultFacetRelation.size(); i++) {
				facetRelationSum += Integer.parseInt(resultFacetRelation.get(i).get("frc").toString());
				facetRelationList.add(Integer.parseInt(resultFacetRelation.get(i).get("frc").toString()));
			}
			facetRelationList.add(0, facetRelationSum);
			Log.log(facetRelationList);
			// 每个领域的碎片数量
			List<Map<String, Object>> resultFragment = mysql.returnMultipleResult(sqlFragment, params);
			Log.log(resultFragment);
			int fragmentSum = 0;
			for (int i = 0; i < resultFragment.size(); i++) {
				fragmentSum += Integer.parseInt(resultFragment.get(i).get("afc").toString());
				fragmentList.add(Integer.parseInt(resultFragment.get(i).get("afc").toString()));
			}
			fragmentList.add(0, fragmentSum);
			Log.log(fragmentList);
			// 每个领域的碎片数量
			List<Map<String, Object>> resultDependency = mysql.returnMultipleResult(sqlDepenency, params);
			Log.log(resultDependency);
			int DependencySum = 0;
			for (int i = 0; i < resultDependency.size(); i++) {
				DependencySum += Integer.parseInt(resultDependency.get(i).get("dpc").toString());
				dependencyList.add(Integer.parseInt(resultDependency.get(i).get("dpc").toString()));
			}
			dependencyList.add(0, DependencySum);
			Log.log(dependencyList);
			// 设置返回对象
			domainStatistics.setDomainList(domainList);
			domainStatistics.setTopicList(topicList);
			domainStatistics.setTopicRelationList(topicRelationList);
			domainStatistics.setFacetList(facetList);
			domainStatistics.setFacetRelationList(facetRelationList);
			domainStatistics.setFragmentList(fragmentList);
			domainStatistics.setDependencyList(dependencyList);
			response = Response.status(200).entity(domainStatistics).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}

	@GET
	@Path("/getTopicInfoByDomain")
	@ApiOperation(value = "根据课程获得主题信息", notes = "根据课程，获得指定课程下的主题信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTopicInfoByDomain(
			@DefaultValue("数据结构") @ApiParam(value = "课程", required = true) @QueryParam("domainName") String domainName
		) {
		Response response = null;
		TopicStatistics topicStatistics = new TopicStatistics();
		List<String> topicList = new ArrayList<String>();
		List<Integer> facetList = new ArrayList<Integer>();
		List<Integer> facetFirstList = new ArrayList<Integer>();
		List<Integer> facetSecondList = new ArrayList<Integer>();
		List<Integer> facetThirdList = new ArrayList<Integer>();
		List<Integer> dependencyList = new ArrayList<Integer>();
		List<Integer> fragmentList = new ArrayList<Integer>();
		/**
		 * 读取domain，得到所有领域名
		 */
		mysqlUtils mysql = new mysqlUtils();
		List<Object> params = new ArrayList<Object>();
		params.add(domainName);
		String sqlTopic = "select TermID, TermName from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName = ? order by TermID";
		String sqlFacet = "select count(TermID) as fc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and TermID = ?";
		String sqlFacetFirst = "select count(TermID) as ffc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and TermID = ? and FacetLayer = 1";
		String sqlFacetSecond = "select count(TermID) as sfc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and TermID = ? and FacetLayer = 2";
		String sqlFacetThird = "select count(TermID) as tfc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and TermID = ? and FacetLayer = 3";
		String sqlDepenency = "select count(StartID) as dc from " + Config.DEPENDENCY + " where ClassName = ? and ((Start = ? and StartID = ?) or (End = ? and EndID = ?))";
		String sqlFragment = "select count(FragmentID) as fc from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName = ? and TermName = ? and TermID = ?";
		try {
			// 总数
			int facetSum = 0;
			int facetFirstSum = 0;
			int facetSecondSum = 0;
			int facetThirdSum = 0;
			int dependencySum = 0;
			int fragmentSum = 0;
			// 主题
			List<Map<String, Object>> resultTopic = mysql.returnMultipleResult(sqlTopic, params);
			Log.log(resultTopic);
			for (int i = 0; i < resultTopic.size(); i++) {
				String termName = resultTopic.get(i).get("TermName").toString();
				int termID = Integer.parseInt(resultTopic.get(i).get("TermID").toString());
				topicList.add(termName);
				
				// 每个主题的所有分面数量
				List<Object> paramsFacet = new ArrayList<Object>();
				paramsFacet.add(domainName);
				paramsFacet.add(termName);
				paramsFacet.add(termID);
				List<Map<String, Object>> resultFacet = mysql.returnMultipleResult(sqlFacet, paramsFacet);
				facetSum += Integer.parseInt(resultFacet.get(0).get("fc").toString());
				facetList.add(Integer.parseInt(resultFacet.get(0).get("fc").toString()));
				
				// 每个主题的一级分面数量
				List<Map<String, Object>> resultFacetFirst = mysql.returnMultipleResult(sqlFacetFirst, paramsFacet);
				facetFirstSum += Integer.parseInt(resultFacetFirst.get(0).get("ffc").toString());
				facetFirstList.add(Integer.parseInt(resultFacetFirst.get(0).get("ffc").toString()));
				
				// 每个主题的二级分面数量
				List<Map<String, Object>> resultFacetSecond = mysql.returnMultipleResult(sqlFacetSecond, paramsFacet);
				facetSecondSum += Integer.parseInt(resultFacetSecond.get(0).get("sfc").toString());
				facetSecondList.add(Integer.parseInt(resultFacetSecond.get(0).get("sfc").toString()));
				
				// 每个主题的三级分面数量
				List<Map<String, Object>> resultFacetThird = mysql.returnMultipleResult(sqlFacetThird, paramsFacet);
				facetThirdSum += Integer.parseInt(resultFacetThird.get(0).get("tfc").toString());
				facetThirdList.add(Integer.parseInt(resultFacetThird.get(0).get("tfc").toString()));
				
				// 每个主题的认知关系数量
				List<Object> paramsDependency = new ArrayList<Object>();
				paramsDependency.add(domainName);
				paramsDependency.add(termName);
				paramsDependency.add(termID);
				paramsDependency.add(termName);
				paramsDependency.add(termID);
				List<Map<String, Object>> resultDependency = mysql.returnMultipleResult(sqlDepenency, paramsDependency);
				dependencySum += Integer.parseInt(resultDependency.get(0).get("dc").toString());
				dependencyList.add(Integer.parseInt(resultDependency.get(0).get("dc").toString()));
				
				// 每个主题的碎片数量
				List<Map<String, Object>> resultFragment = mysql.returnMultipleResult(sqlFragment, paramsFacet);
				fragmentSum += Integer.parseInt(resultFragment.get(0).get("fc").toString());
				fragmentList.add(Integer.parseInt(resultFragment.get(0).get("fc").toString()));
			}
			facetList.add(0, facetSum); // 第一个数字为所有领域主题数量的和，下面的类似
			facetFirstList.add(0, facetFirstSum);
			facetSecondList.add(0, facetSecondSum);
			facetThirdList.add(0, facetThirdSum);
			dependencyList.add(0, dependencySum);
			fragmentList.add(0, fragmentSum);
			Log.log(facetList);
			Log.log(facetFirstList);
			Log.log(facetSecondList);
			Log.log(facetThirdList);
			Log.log(dependencyList);
			Log.log(fragmentList);
			// 设置返回对象
			topicStatistics.setTopicList(topicList);
			topicStatistics.setFacetList(facetList);
			topicStatistics.setFacetFirstList(facetFirstList);
			topicStatistics.setFacetSecondList(facetSecondList);
			topicStatistics.setFacetThirdList(facetThirdList);
			topicStatistics.setDependencyList(dependencyList);
			topicStatistics.setFragmentList(fragmentList);
			response = Response.status(200).entity(topicStatistics).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getTopicDetail")
	@ApiOperation(value = "根据课程和主题获得主题详细信息", notes = "根据课程和主题，获得该主题的详细信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTopicDetail(
			@DefaultValue("数据结构") @ApiParam(value = "课程", required = true) @QueryParam("domainName") String domainName,
			@DefaultValue("图论术语") @ApiParam(value = "主题", required = true) @QueryParam("topicName") String topicName
		) {
		Response response = null;
		TopicDetail topicDetail = new TopicDetail();
		List<String> facets = new ArrayList<String>();
		List<Map<String, Object>> totals = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> details = new ArrayList<Map<String,Object>>();
		/**
		 * 读取Facet，得到所有分面
		 */
		mysqlUtils mysql = new mysqlUtils();
		List<Object> params = new ArrayList<Object>();
		params.add(domainName);
		params.add(topicName);
		List<Object> params1 = new ArrayList<Object>();
		params1.add(domainName);
		params1.add(topicName);
		params1.add(topicName);
		// 分面
		String sqlFacet = "select FacetName, FacetLayer from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ?";
		// 总数
		String sqlFacetSum = "select count(TermID) as fc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ?";
		String sqlFacetFirst = "select count(TermID) as fc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and FacetLayer = 1";
		String sqlFacetSecond = "select count(TermID) as fc from " + Config.FACET_TABLE + " where ClassName = ? and TermName = ? and FacetLayer = 2";
		String sqlFragment = "select count(FragmentID) as fc from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName = ? and TermName = ?";
		String sqlDependency = "select count(Start) as dc from " + Config.DEPENDENCY + " where ClassName = ? and (Start = ? or End = ?)";
		// 各级分面数量统计
		String sqlFacetFragment = "select count(FragmentID) as fc from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName = ? and TermName = ? and facetName = ? and FacetLayer = ?";
//		String sqlFacetRelation = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName = ? and TermName = ? and ParentFacet = ? and ParentLayer = ?";
		
		try {
			// 分面
			facets.add("分面总数");
			facets.add("一级分面总数");
			facets.add("二级分面总数");
			facets.add("碎片总数");
			facets.add("认知关系总数");
			List<Map<String, Object>> resultFacet = mysql.returnMultipleResult(sqlFacet, params);
			for (int i = 0; i < resultFacet.size(); i++) {
				String facetName = resultFacet.get(i).get("FacetName").toString();
				int facetLayer = Integer.parseInt(resultFacet.get(i).get("FacetLayer").toString());
				if (facetLayer == 1) {
					facets.add("f1:" + facetName);
				} else if (facetLayer == 2) {
					facets.add("f2:" + facetName);
				}
				
				// 各级分面数量统计
				List<Object> paramsLayer = new ArrayList<Object>();
				paramsLayer.add(domainName);
				paramsLayer.add(topicName);
				paramsLayer.add(facetName);
				paramsLayer.add(facetLayer);
				Map<String, Object> mapFacetFragment = new HashMap<String, Object>();
				if (facetLayer == 2) { // 二级分面直接统计碎片数量
					List<Map<String, Object>> resultFacetFragment = mysql.returnMultipleResult(sqlFacetFragment, paramsLayer);
					mapFacetFragment.put("name", "f2:" + facetName);
					mapFacetFragment.put("value", Integer.parseInt(resultFacetFragment.get(0).get("fc").toString()));
				}
				// 一级分面：没有二级分面直接统计分面数量；有二级分面统计该一级分面和二级分面下的所有碎片（不用区分：因为爬虫爬的时候，将包含二级分面的一级分面的碎片都爬取了）
				if (facetLayer == 1) {
					List<Map<String, Object>> resultFacetFragment = mysql.returnMultipleResult(sqlFacetFragment, paramsLayer);
					int fraSum = Integer.parseInt(resultFacetFragment.get(0).get("fc").toString());
//					List<Map<String, Object>> resultFacetRelation = mysql.returnMultipleResult(sqlFacetRelation, paramsLayer);
//					if (resultFacetRelation.size() != 0) {
//						for (int j = 0; j < resultFacetRelation.size(); j++) {
//							String childFacetName = resultFacetRelation.get(j).get("ChildFacet").toString();
//							int childFacetLayer = Integer.parseInt(resultFacetRelation.get(j).get("ChildLayer").toString());
//							List<Object> paramsLayer2 = new ArrayList<Object>();
//							paramsLayer2.add(domainName);
//							paramsLayer2.add(topicName);
//							paramsLayer2.add(childFacetName);
//							paramsLayer2.add(childFacetLayer);
//							List<Map<String, Object>> resultFacetFragment2 = mysql.returnMultipleResult(sqlFacetFragment, paramsLayer2);
//							fraSum += Integer.parseInt(resultFacetFragment2.get(0).get("fc").toString());
//						}
//					}
					mapFacetFragment.put("name", "f1:" + facetName);
					mapFacetFragment.put("value", fraSum);
				}
				details.add(mapFacetFragment);
			}
			
			// 总数
			// 1. 分面总数
			List<Map<String, Object>> resultFacetSum = mysql.returnMultipleResult(sqlFacetSum, params);
			Map<String, Object> mapFacetSum = new HashMap<String, Object>();
			mapFacetSum.put("name", "分面总数");
			mapFacetSum.put("value", Integer.parseInt(resultFacetSum.get(0).get("fc").toString()));
			totals.add(mapFacetSum);
			// 2. 一级分面总数
			List<Map<String, Object>> resultFacetFirst = mysql.returnMultipleResult(sqlFacetFirst, params);
			Map<String, Object> mapFacetFirst = new HashMap<String, Object>();
			mapFacetFirst.put("name", "一级分面总数");
			mapFacetFirst.put("value", Integer.parseInt(resultFacetFirst.get(0).get("fc").toString()));
			totals.add(mapFacetFirst);
			// 3. 二级分面总数
			List<Map<String, Object>> resultFacetSecond = mysql.returnMultipleResult(sqlFacetSecond, params);
			Map<String, Object> mapFacetSecond = new HashMap<String, Object>();
			mapFacetSecond.put("name", "二级分面总数");
			mapFacetSecond.put("value", Integer.parseInt(resultFacetSecond.get(0).get("fc").toString()));
			totals.add(mapFacetSecond);
			// 4. 碎片总数
			List<Map<String, Object>> resultFragment = mysql.returnMultipleResult(sqlFragment, params);
			Map<String, Object> mapFragment = new HashMap<String, Object>();
			mapFragment.put("name", "碎片总数");
			mapFragment.put("value", Integer.parseInt(resultFragment.get(0).get("fc").toString()));
			totals.add(mapFragment);
			// 5. 存在认知关系数量
			List<Map<String, Object>> resultDependency = mysql.returnMultipleResult(sqlDependency, params1);
			Map<String, Object> mapDependency = new HashMap<String, Object>();
			mapDependency.put("name", "认知关系总数");
			mapDependency.put("value", Integer.parseInt(resultDependency.get(0).get("dc").toString()));
			totals.add(mapDependency);
			
			// 输出
			Log.log(facets.size() + "," + facets);
			Log.log(totals.size() + "," + totals);
			Log.log(details.size() + "," + details);
			
			// 设置返回对象
			topicDetail.setFacets(facets);
			topicDetail.setTotals(totals);
			topicDetail.setDetails(details);
			response = Response.status(200).entity(topicDetail).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
}
