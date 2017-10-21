package facet;

import facet.bean.FacetComplex;
import facet.bean.FacetRelation;
import facet.bean.FacetSimple;
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



import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;
import app.success;

/**  
 * 分面树构建：主题分面
 * @author 郑元浩 
 */

@Path("/FacetAPI")
@Api(value = "FacetAPI")
public class FacetAPI {

	public static void main(String[] args) {
		
	}
	
	@GET
	@Path("/getTopicFacet")
	@ApiOperation(value = "获得知识主题的所有分面信息", notes = "输入领域名/知识主题，获得知识主题的所有分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTopicFacet (
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		
		Response response = null;
		List<FacetComplex> facetComplexList = new ArrayList<FacetComplex>();
		List<FacetRelation> facetRelationList = FacetDAO.getFacetRelation(className, topicName, 1, 2);
		
		/**
		 * 读取facet，获得知识点的一级/二级分面
		 */
		List<FacetSimple> firstFacetList = FacetDAO.getFacet(className, topicName, 1);
		List<FacetSimple> secondFacetList = FacetDAO.getFacet(className, topicName, 2);
		if (firstFacetList.size() == 0) {
			/**
			 * 该主题没有一级分面，无法构建主题分面树
			 */
			Log.log(topicName + "没有一级分面，该主题无法构建主题分面树...");
		} else {
			/**
			 * 该主题有一级分面，可以构建主题分面树
			 */
			if (secondFacetList.size() == 0) {
				/**
				 * 该主题没有二级分面，只有一级分面
				 */
				for (int i = 0; i < firstFacetList.size(); i++) {
					FacetSimple firstFacet = firstFacetList.get(i);
					String firstFacetName = firstFacet.getFacetName();
					ArrayList<String> secondFacetName = new ArrayList<String>();
					FacetComplex facetComplex = new FacetComplex(firstFacetName, secondFacetName);
					facetComplexList.add(facetComplex);
				}
			} else {
				/**
				 * 该主题有二级分面，寻找到二级分面对应的一级分面，并将其信息挂载到一级分面下
				 */
				for (int i = 0; i < firstFacetList.size(); i++) {
					FacetSimple firstFacet = firstFacetList.get(i);
					String firstFacetName = firstFacet.getFacetName();
					ArrayList<String> secondFacetName = new ArrayList<String>();
					/**
					 * 根据一级分面寻找到二级分面的信息
					 */
					List<FacetSimple> secondFacetListNew = FacetDAO.getChildFacet(firstFacet, facetRelationList);
					for (int j = 0; j < secondFacetListNew.size(); j++) {
						/**
						 * 遍历所有该一级分面下的二级分面，将其添加到一级分面的信息下面
						 */
						FacetSimple secondFacet = secondFacetListNew.get(j);
						secondFacetName.add(secondFacet.getFacetName());
					}
					FacetComplex facetComplex = new FacetComplex(firstFacetName, secondFacetName);
					facetComplexList.add(facetComplex);
				}
			}
		}
		
		response = Response.status(200).entity(facetComplexList).build();
		return response;
	}
	
	
	@GET
	@Path("/getDomainTerm")
	@ApiOperation(value = "获得指定领域下主题的信息", notes = "获得指定领域下主题的信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTerm(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName) {
		Response response = null;
		/**
		 * 根据指定领域，获得该领域下的所有主题信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE+" where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getFacet1Facet2Num")
	@ApiOperation(value = "获得指定领域下指定主题的一级分面的二级分面数", notes = "获得指定领域下指定主题的一级分面的二级分面数")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getFacet1Facet2Num(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题的一级分面的二级分面数
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(Facet1Name);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			Map<String, Object> res=new HashMap<String, Object>();
			res.put("ClassName", ClassName);
			res.put("TermName",	TermName);
			res.put("Facet1Name", Facet1Name);
			res.put("Facet2Num", results.size());
			response = Response.status(200).entity(res).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getFacet2Facet3Num")
	@ApiOperation(value = "获得指定领域下指定主题的二级分面的三级分面数", notes = "获得指定领域下指定主题的二级分面的三级分面数")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getFacet2Facet3Num(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "二级分面名字", required = true) @QueryParam("Facet2Name") String Facet2Name) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题的二级分面的三级分面数
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(Facet2Name);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			Map<String, Object> res=new HashMap<String, Object>();
			res.put("ClassName", ClassName);
			res.put("TermName",	TermName);
			res.put("Facet2Name", Facet2Name);
			res.put("Facet3Num", results.size());
			response = Response.status(200).entity(res).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getDomainInfo")
	@ApiOperation(value = "获得指定领域下主题分面信息", notes = "获得指定领域下主题分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainInfo(@DefaultValue("数据结构") @ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName) {
		Response response = null;
		/**
		 * 获得指定领域下主题分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String sql_topic = "select * from " + Config.DOMAIN_TOPIC_TABLE+" where ClassName=?";
		String sql_facet1 = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetLayer='1'";
		String sql_facet2 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params_topic = new ArrayList<Object>();
		params_topic.add(ClassName);
		try {
			List<Map<String, Object>> topics =mysql.returnMultipleResult(sql_topic, params_topic);
			for(int i=0;i<topics.size();i++){
				List<Object> params_facet1 = new ArrayList<Object>();
				List<Object> res_facet1 = new ArrayList<Object>();
				params_facet1.add(ClassName);
				params_facet1.add(topics.get(i).get("TermName"));
				try{
					List<Map<String, Object>> facet1s =mysql.returnMultipleResult(sql_facet1, params_facet1);
					for(int j=0;j<facet1s.size();j++){
						
						List<Object> params_facet2 = new ArrayList<Object>();
						List<Object> res_facet2 = new ArrayList<Object>();
						params_facet2.add(ClassName);
						params_facet2.add(topics.get(i).get("TermName"));
						params_facet2.add(facet1s.get(j).get("FacetName"));
						try{
							List<Map<String, Object>> facet2s =mysql.returnMultipleResult(sql_facet2, params_facet2);
							for(int k=0;k<facet2s.size();k++){
								
								List<Object> params_facet3 = new ArrayList<Object>();
								List<Object> res_facet3 = new ArrayList<Object>();
								params_facet3.add(ClassName);
								params_facet3.add(topics.get(i).get("TermName"));
								params_facet3.add(facet2s.get(k).get("ChildFacet"));
								try{
									List<Map<String, Object>> facet3s =mysql.returnMultipleResult(sql_facet3, params_facet3);
									for(int q=0;q<facet3s.size();q++){
										Map<String, Object> fac3=new HashMap<String, Object>();
										fac3.put("Facet3Name", facet3s.get(q).get("ChildFacet"));
										res_facet3.add(fac3);
									}
								}catch(Exception e3){
									e3.printStackTrace();
								}
								
								
								Map<String, Object> fac2=new HashMap<String, Object>();
								fac2.put("Facet2Name", facet2s.get(k).get("ChildFacet"));
								fac2.put("Facet3", res_facet3);
								res_facet2.add(fac2);
							}
						}catch(Exception e2){
							e2.printStackTrace();
						}
						
					Map<String, Object> fac1=new HashMap<String, Object>();
					fac1.put("Facet1Name", facet1s.get(j).get("FacetName"));
					fac1.put("Facet2", res_facet2);
					res_facet1.add(fac1);
					}
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
				Map<String, Object> top=new HashMap<String, Object>();
				top.put("ClassName", ClassName);
				top.put("TermName", topics.get(i).get("TermName"));
				top.put("Facet1", res_facet1);
				results.add(top);
			}
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getDomainTermFacet1")
	@ApiOperation(value = "获得指定领域下指定主题的一级分面信息", notes = "获得指定领域下指定主题的一级分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下的所有一级分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetLayer='1'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getDomainTermFacet2")
	@ApiOperation(value = "获得指定领域下指定主题一级分面下的二级分面信息", notes = "获得指定领域下指定主题一级分面下的二级分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet2(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题一级分面下的二级分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(Facet1Name);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getDomainTermFacet3")
	@ApiOperation(value = "获得指定领域下指定主题二级分面下的三级分面信息", notes = "获得指定领域下指定主题二级分面下的三级分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet3(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "二级分面名字", required = true) @QueryParam("Facet2Name") String Facet2Name) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题二级分面下的三级分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(Facet2Name);
		try {
			List<Map<String, Object>> newresults = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for(Map<String, Object> a:results){
				Map<String, Object> newa=new HashMap<String, Object>();
				newa=a;
				newa.put("FacetName", a.get("ChildFacet"));
				newa.put("FacetLayer", a.get("ChildLayer"));
				newresults.add(newa);
			}
			results=newresults;
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getTermFacet")
	@ApiOperation(value = "获得指定领域下指定主题的分面信息", notes = "获得指定领域下指定主题的分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacet(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题的分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getTermFacet1")
	@ApiOperation(value = "获得指定领域下指定主题一级分面下的分面信息", notes = "获得指定领域下指定主题一级分面下的分面信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题一级分面下的分面信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		String sql1 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(Facet1Name);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			List<Map<String, Object>> results_final = results;
			List<Map<String, Object>> newresults = new ArrayList<Map<String, Object>>();
			for(int i=0;i<results.size();i++){
				List<Object> params1 = new ArrayList<Object>();
				params1.add(ClassName);
				params1.add(TermName);
				params1.add(results.get(i).get("ChildFacet"));
				try{
					List<Map<String, Object>> results1=mysql.returnMultipleResult(sql1, params1);
					for(Map<String, Object> p:results1){
						results_final.add(p);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			for(Map<String, Object> a:results_final){
				Map<String, Object> newa=new HashMap<String, Object>();
				newa.put("TermName", a.get("TermName"));
				newa.put("FacetName", a.get("ChildFacet"));
				newa.put("FacetLayer", a.get("ChildLayer"));
				newa.put("ClassName", a.get("ClassName"));
				newa.put("TermID", a.get("TermID"));
				newa.put("ParentFacet", a.get("ParentFacet"));
				newa.put("ParentLayer", a.get("ParentLayer"));
				newresults.add(newa);
			}
			results=newresults;
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getTermFacetFragment")
	@ApiOperation(value = "获得指定领域下指定主题下指定分面的碎片信息", notes = "获得指定领域下指定主题下指定分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacetFragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "分面级数", required = true) @QueryParam("FacetLayer") String FacetLayer) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题下指定分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		Map<String, Object> results=new HashMap<String, Object>();
//		String sql_facet2 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
//		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		String sql_Fragment = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Text = "select * from " + Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Image = "select * from " + Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
		
//		int TextNum=0;
//		int ImageNum=0;
		
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		params.add(FacetName);
		params.add(FacetLayer);

		try {
			List<Map<String, Object>> results1 = mysql.returnMultipleResult(sql_Fragment, params);
//			List<Map<String, Object>> results_Text = mysql.returnMultipleResult(sql_Text, params);
//			List<Map<String, Object>> results_Image = mysql.returnMultipleResult(sql_Image, params);
			results.put("FacetName", FacetName);
			results.put("FacetLayer", FacetLayer);
			results.put("FragmentNum", results1.size());
//			results.put("FragmentTextNum", results_Text.size());
//			results.put("FragmentImageNum", results_Image.size());
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getTermFacet1Fragment")
	@ApiOperation(value = "获得指定领域下指定主题下指定一级分面的碎片信息", notes = "获得指定领域下指定主题下指定一级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacet1Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题下指定一级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		Map<String, Object> results=new HashMap<String, Object>();
		String sql_facet2 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		
		String sql_Fragment = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Text = "select * from " + Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Image = "select * from " + Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
		
//		int TextNum1=0;
//		int ImageNum1=0;
//		int TextNum2=0;
//		int ImageNum2=0;
//		int TextNum3=0;
//		int ImageNum3=0;
		int FragmentNum1=0;
		int FragmentNum2=0;
		int FragmentNum3=0;
		
		List<Object> params_fragment1 = new ArrayList<Object>();
		params_fragment1.add(ClassName);
		params_fragment1.add(TermName);
		params_fragment1.add(FacetName);
		params_fragment1.add(1);
		
		List<Object> params_facet2 = new ArrayList<Object>();
		params_facet2.add(ClassName);
		params_facet2.add(TermName);
		params_facet2.add(FacetName);

		try {
			List<Map<String, Object>> results_facet2=mysql.returnMultipleResult(sql_facet2, params_facet2);
			for(int i=0;i<results_facet2.size();i++){
				List<Object> params_facet3 = new ArrayList<Object>();
				params_facet3.add(ClassName);
				params_facet3.add(TermName);
				params_facet3.add(results_facet2.get(i).get("ChildFacet"));
				try{
					List<Map<String, Object>> results_facet3=mysql.returnMultipleResult(sql_facet3, params_facet3);
					for(int j=0;j<results_facet3.size();j++){
						List<Object> params_fragment3 = new ArrayList<Object>();
						params_fragment3.add(ClassName);
						params_fragment3.add(TermName);
						params_fragment3.add(results_facet3.get(j).get("ChildFacet"));
						params_fragment3.add(3);
						try{
							List<Map<String, Object>> results_Fragment3=mysql.returnMultipleResult(sql_Fragment, params_fragment3);
							FragmentNum3=FragmentNum3+results_Fragment3.size();
//							List<Map<String, Object>> results_Text3=mysql.returnMultipleResult(sql_Text, params_fragment3);
//							List<Map<String, Object>> results_Image3=mysql.returnMultipleResult(sql_Image, params_fragment3);
//							TextNum3=TextNum3+results_Text3.size();
//							ImageNum3=ImageNum3+results_Image3.size();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				List<Object> params_fragment2 = new ArrayList<Object>();
				params_fragment2.add(ClassName);
				params_fragment2.add(TermName);
				params_fragment2.add(results_facet2.get(i).get("ChildFacet"));
				params_fragment2.add(2);
				try{
					List<Map<String, Object>> results_Fragment2=mysql.returnMultipleResult(sql_Fragment, params_fragment2);
					FragmentNum2=FragmentNum2+results_Fragment2.size();
//					List<Map<String, Object>> results_Text2=mysql.returnMultipleResult(sql_Text, params_fragment2);
//					List<Map<String, Object>> results_Image2=mysql.returnMultipleResult(sql_Image, params_fragment2);
//					TextNum2=TextNum2+results_Text2.size();
//					ImageNum2=ImageNum2+results_Image2.size();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			List<Map<String, Object>> results_Fragment1=mysql.returnMultipleResult(sql_Fragment, params_fragment1);
			FragmentNum1=FragmentNum1+results_Fragment1.size();
//			List<Map<String, Object>> results_Text1 = mysql.returnMultipleResult(sql_Text, params_fragment1);
//			List<Map<String, Object>> results_Image1 = mysql.returnMultipleResult(sql_Image, params_fragment1);
//			TextNum1=results_Text1.size();
//			ImageNum1=results_Image1.size();
			results.put("FacetName", FacetName);
			results.put("FacetLayer", 1);
			results.put("FragmentNum", FragmentNum1+FragmentNum2+FragmentNum3);
//			results.put("FragmentTextNum", TextNum1+TextNum2+TextNum3);
//			results.put("FragmentImageNum", ImageNum1+ImageNum2+ImageNum3);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getTermFacet2Fragment")
	@ApiOperation(value = "获得指定领域下指定主题下指定二级分面的碎片信息", notes = "获得指定领域下指定主题下指定二级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacet2Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题下指定二级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		Map<String, Object> results=new HashMap<String, Object>();
		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		
		String sql_Fragment = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Text = "select * from " + Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Image = "select * from " + Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";

//		int TextNum2=0;
//		int ImageNum2=0;
//		int TextNum3=0;
//		int ImageNum3=0;
		int FragmentNum2=0;
		int FragmentNum3=0;
		
		List<Object> params_fragment2 = new ArrayList<Object>();
		params_fragment2.add(ClassName);
		params_fragment2.add(TermName);
		params_fragment2.add(FacetName);
		params_fragment2.add(2);
		
		List<Object> params_facet3 = new ArrayList<Object>();
		params_facet3.add(ClassName);
		params_facet3.add(TermName);
		params_facet3.add(FacetName);

		try {
			List<Map<String, Object>> results_facet3=mysql.returnMultipleResult(sql_facet3, params_facet3);
			for(int i=0;i<results_facet3.size();i++){
				List<Object> params_fragment3 = new ArrayList<Object>();
				params_fragment3.add(ClassName);
				params_fragment3.add(TermName);
				params_fragment3.add(results_facet3.get(i).get("ChildFacet"));
				params_fragment3.add(2);
				try{
					List<Map<String, Object>> results_Fragment3=mysql.returnMultipleResult(sql_Fragment, params_fragment3);
					FragmentNum3=FragmentNum3+results_Fragment3.size();
//					List<Map<String, Object>> results_Text3=mysql.returnMultipleResult(sql_Text, params_fragment3);
//					List<Map<String, Object>> results_Image3=mysql.returnMultipleResult(sql_Image, params_fragment3);
//					TextNum3=TextNum3+results_Text3.size();
//					ImageNum3=ImageNum3+results_Image3.size();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			List<Map<String, Object>> results_Fragment2=mysql.returnMultipleResult(sql_Fragment, params_fragment2);
			FragmentNum2=FragmentNum2+results_Fragment2.size();
//			List<Map<String, Object>> results_Text2 = mysql.returnMultipleResult(sql_Text, params_fragment2);
//			List<Map<String, Object>> results_Image2 = mysql.returnMultipleResult(sql_Image, params_fragment2);
//			TextNum2=results_Text2.size();
//			ImageNum2=results_Image2.size();
			results.put("FacetName", FacetName);
			results.put("FacetLayer", 2);
			results.put("FragmentNum", FragmentNum2+FragmentNum3);
//			results.put("FragmentTextNum", TextNum2+TextNum3);
//			results.put("FragmentImageNum", ImageNum2+ImageNum3);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getTermFacet3Fragment")
	@ApiOperation(value = "获得指定领域下指定主题下指定三级分面的碎片信息", notes = "获得指定领域下指定主题下指定三级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTermFacet3Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 获得指定领域下指定主题下指定三级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		Map<String, Object> results=new HashMap<String, Object>();
		String sql_Fragment = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Text = "select * from " + Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
//		String sql_Image = "select * from " + Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";

//		int TextNum3=0;
//		int ImageNum3=0;
		int FragmentNum3=0;
		
		List<Object> params_fragment3 = new ArrayList<Object>();
		params_fragment3.add(ClassName);
		params_fragment3.add(TermName);
		params_fragment3.add(FacetName);
		params_fragment3.add(3);


		try {
			List<Map<String, Object>> results_Fragment3 = mysql.returnMultipleResult(sql_Fragment, params_fragment3);
			FragmentNum3=results_Fragment3.size();
//			List<Map<String, Object>> results_Text3 = mysql.returnMultipleResult(sql_Text, params_fragment3);
//			List<Map<String, Object>> results_Image3 = mysql.returnMultipleResult(sql_Image, params_fragment3);
//			TextNum3=results_Text3.size();
//			ImageNum3=results_Image3.size();
			results.put("FacetName", FacetName);
			results.put("FacetLayer", 3);
			results.put("FragmentNum", FragmentNum3);
//			results.put("FragmentTextNum", TextNum3);
//			results.put("FragmentImageNum", ImageNum3);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	
	@GET
	@Path("/createFacet1")
	@ApiOperation(value = "创建一个一级分面", notes = "在选定的课程和主题下添加一级分面")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
//		Response response = null;
		/**
		 * 在选定的课程和主题下添加一级分面
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.FACET_TABLE+"(TermID,TermName,FacetName,FacetLayer,ClassName) values(?,?,?,?,?);";
			String sql_queryTermID="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			List<Object> params_queryTermID=new ArrayList<Object>();
			params_queryTermID.add(ClassName);
			params_queryTermID.add(TermName);
			List<Object> params=new ArrayList<Object>();
			try{
				List<Map<String, Object>> results_queryTermID=mysql.returnMultipleResult(sql_queryTermID,params_queryTermID);
				if(results_queryTermID.size()!=0){
					params.add(results_queryTermID.get(0).get("TermID").toString());
					params.add(TermName);
					params.add(FacetName);
					params.add(1);
					params.add(ClassName);
					try{
						String exist="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='1'";
						List<Object> params_exist=new ArrayList<Object>();
						params_exist.add(ClassName);
						params_exist.add(TermName);
						params_exist.add(FacetName);
						try{
							List<Map<String, Object>> results = mysql.returnMultipleResult(exist, params_exist);
							if(results.size()==0){
								result=mysql.addDeleteModify(sql, params);
							}
							else{
								return Response.status(200).entity(new success("一级分面 "+FacetName+" 已经存在！")).build();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						
					}
				catch(Exception e){
					e.printStackTrace();
				}
				}
				else{
					return Response.status(401).entity(new error(TermName+" 不存在~")).build();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(FacetName+" 创建成功~")).build();
			}else{
				return Response.status(401).entity(new error(FacetName+" 创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	@GET
	@Path("/createFacet2")
	@ApiOperation(value = "创建一个二级分面", notes = "在选定的课程和主题和一级分面下添加二级分面")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createFacet2(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name,@ApiParam(value = "二级分面名字", required = true) @QueryParam("Facet2Name") String Facet2Name) {
//		Response response = null;
		/**
		 * 在选定的课程和主题和一级分面下添加二级分面
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_facet="insert into "+Config.FACET_TABLE+"(TermID,TermName,FacetName,FacetLayer,ClassName) values(?,?,?,?,?);";
			String sql_facetRelation="insert into "+Config.FACET_RELATION_TABLE+"(ChildFacet,ChildLayer,ParentFacet,ParentLayer,TermID,TermName,ClassName) values(?,?,?,?,?,?,?);";
			String sql_queryTermID="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			List<Object> params_queryTermID=new ArrayList<Object>();
			params_queryTermID.add(ClassName);
			params_queryTermID.add(TermName);
			List<Object> params_facet=new ArrayList<Object>();
			List<Object> params_facetRelation=new ArrayList<Object>();
			try{
				List<Map<String, Object>> results_queryTermID=mysql.returnMultipleResult(sql_queryTermID,params_queryTermID);
				if(results_queryTermID.size()!=0){
					params_facet.add(results_queryTermID.get(0).get("TermID").toString());
					params_facet.add(TermName);
					params_facet.add(Facet2Name);
					params_facet.add(2);
					params_facet.add(ClassName);
					
					params_facetRelation.add(Facet2Name);
					params_facetRelation.add(2);
					params_facetRelation.add(Facet1Name);
					params_facetRelation.add(1);
					params_facetRelation.add(results_queryTermID.get(0).get("TermID").toString());
					params_facetRelation.add(TermName);
					params_facetRelation.add(ClassName);
					try{
						String exist_facet="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='2'";
						String exist_facetRelation="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ChildFacet=? and ChildLayer='2'";
						List<Object> params_existFacet=new ArrayList<Object>();
						params_existFacet.add(ClassName);
						params_existFacet.add(TermName);
						params_existFacet.add(Facet2Name);
						List<Object> params_existFacetRelation=new ArrayList<Object>();
						params_existFacetRelation.add(ClassName);
						params_existFacetRelation.add(TermName);
						params_existFacetRelation.add(Facet1Name);
						params_existFacetRelation.add(Facet2Name);
						try{
							List<Map<String, Object>> results_facet = mysql.returnMultipleResult(exist_facet, params_existFacet);
							List<Map<String, Object>> results_facetRelation = mysql.returnMultipleResult(exist_facetRelation, params_existFacetRelation);
							if(results_facet.size()==0&&results_facetRelation.size()==0){
								result=mysql.addDeleteModify(sql_facet, params_facet)&&mysql.addDeleteModify(sql_facetRelation, params_facetRelation);
							}
							else if(results_facet.size()!=0&&results_facetRelation.size()==0){
								result=mysql.addDeleteModify(sql_facetRelation, params_facetRelation);
							}
							else{
								return Response.status(200).entity(new success("二级分面 "+Facet2Name+" 已经存在！")).build();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						
					}
				catch(Exception e){
					e.printStackTrace();
				}
				}
				else{
					return Response.status(401).entity(new error(TermName+" 不存在~")).build();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(Facet2Name+" 创建成功~")).build();
			}else{
				return Response.status(401).entity(new error(Facet2Name+" 创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
	@GET
	@Path("/createFacet3")
	@ApiOperation(value = "创建一个三级分面", notes = "在选定的课程和主题和一级分面、二级分面下添加三级分面")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createFacet3(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name,@ApiParam(value = "二级分面名字", required = true) @QueryParam("Facet2Name") String Facet2Name,@ApiParam(value = "三级分面名字", required = true) @QueryParam("Facet3Name") String Facet3Name) {
//		Response response = null;
		/**
		 * 在选定的课程和主题和一级分面、二级分面下添加三级分面（有bug，但在系统中不会出问题）
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_facet="insert into "+Config.FACET_TABLE+"(TermID,TermName,FacetName,FacetLayer,ClassName) values(?,?,?,?,?);";
			String sql_facetRelation="insert into "+Config.FACET_RELATION_TABLE+"(ChildFacet,ChildLayer,ParentFacet,ParentLayer,TermID,TermName,ClassName) values(?,?,?,?,?,?,?);";
			String sql_queryTermID="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			List<Object> params_queryTermID=new ArrayList<Object>();
			params_queryTermID.add(ClassName);
			params_queryTermID.add(TermName);
			List<Object> params_facet=new ArrayList<Object>();
			List<Object> params_facetRelation=new ArrayList<Object>();
			try{
				List<Map<String, Object>> results_queryTermID=mysql.returnMultipleResult(sql_queryTermID,params_queryTermID);
				if(results_queryTermID.size()!=0){
					params_facet.add(results_queryTermID.get(0).get("TermID").toString());
					params_facet.add(TermName);
					params_facet.add(Facet3Name);
					params_facet.add(3);
					params_facet.add(ClassName);
					
					params_facetRelation.add(Facet3Name);
					params_facetRelation.add(3);
					params_facetRelation.add(Facet2Name);
					params_facetRelation.add(2);
					params_facetRelation.add(results_queryTermID.get(0).get("TermID").toString());
					params_facetRelation.add(TermName);
					params_facetRelation.add(ClassName);
					try{
						String exist_facet="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='3'";
						String exist_facetRelation="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ChildFacet=? and ChildLayer='3'";
						List<Object> params_existFacet=new ArrayList<Object>();
						params_existFacet.add(ClassName);
						params_existFacet.add(TermName);
						params_existFacet.add(Facet3Name);
						List<Object> params_existFacetRelation=new ArrayList<Object>();
						params_existFacetRelation.add(ClassName);
						params_existFacetRelation.add(TermName);
						params_existFacetRelation.add(Facet2Name);
						params_existFacetRelation.add(Facet3Name);
						try{
							List<Map<String, Object>> results_facet = mysql.returnMultipleResult(exist_facet, params_existFacet);
							List<Map<String, Object>> results_facetRelation = mysql.returnMultipleResult(exist_facetRelation, params_existFacetRelation);
							if(results_facet.size()==0&&results_facetRelation.size()==0){
								result=mysql.addDeleteModify(sql_facet, params_facet)&&mysql.addDeleteModify(sql_facetRelation, params_facetRelation);
							}
							else if(results_facet.size()!=0&&results_facetRelation.size()==0){
								result=mysql.addDeleteModify(sql_facetRelation, params_facetRelation);
							}
							else{
								return Response.status(200).entity(new success("三级分面 "+Facet3Name+" 已经存在！")).build();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						
					}
				catch(Exception e){
					e.printStackTrace();
				}
				}
				else{
					return Response.status(401).entity(new error(TermName+" 不存在~")).build();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(Facet3Name+" 创建成功~")).build();
			}else{
				return Response.status(401).entity(new error(Facet3Name+" 创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
	@GET
	@Path("/deleteFacet1")
	@ApiOperation(value = "删除一级分面", notes = "删除一级分面")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		/**
		 * 删除指定课程及主题下的一级分面
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_Facet="delete from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
			String sql_FacetRelation="delete from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ChildFacet=? and ChildLayer=?;";
			String sql_AssembleFragment="delete from "+Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//			String sql_AssembleText="delete from "+Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//			String sql_AssembleImage="delete from "+Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
			
			String sql_queryfacet2="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1';";
			String sql_queryfacet3="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2';";
			List<Object> params_deletefacet1=new ArrayList<Object>();
			List<Object> params_queryfacet2=new ArrayList<Object>();
			params_deletefacet1.add(ClassName);
			params_deletefacet1.add(TermName);
			params_deletefacet1.add(FacetName);
			params_deletefacet1.add(1);
			params_queryfacet2.add(ClassName);
			params_queryfacet2.add(TermName);
			params_queryfacet2.add(FacetName);
			try{
				List<Map<String, Object>> res_queryfacet2=mysql.returnMultipleResult(sql_queryfacet2, params_queryfacet2);
				if(res_queryfacet2.size()!=0){
					for(int i=0;i<res_queryfacet2.size();i++){
						List<Object> params_queryfacet3=new ArrayList<Object>();
						params_queryfacet3.add(ClassName);
						params_queryfacet3.add(TermName);
						params_queryfacet3.add(res_queryfacet2.get(i).get("ChildFacet"));
						try{
						List<Map<String, Object>> res_queryfacet3=mysql.returnMultipleResult(sql_queryfacet3, params_queryfacet3);
						if(res_queryfacet3.size()!=0){
							for(int j=0;j<res_queryfacet3.size();j++){
								List<Object> params_deletefacet3=new ArrayList<Object>();
								params_deletefacet3.add(ClassName);
								params_deletefacet3.add(TermName);
								params_deletefacet3.add(res_queryfacet3.get(j).get("ChildFacet"));
								params_deletefacet3.add(3);
								try{
									mysql.addDeleteModify(sql_Facet, params_deletefacet3);
									mysql.addDeleteModify(sql_FacetRelation, params_deletefacet3);
									mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet3);
//									mysql.addDeleteModify(sql_AssembleImage, params_deletefacet3);
//									mysql.addDeleteModify(sql_AssmbleText, params_deletefacet3);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							
						}}catch(Exception e){
							e.printStackTrace();
						}
					List<Object> params_deletefacet2=new ArrayList<Object>();
					params_deletefacet2.add(ClassName);
					params_deletefacet2.add(TermName);
					params_deletefacet2.add(res_queryfacet2.get(i).get("ChildFacet"));
					params_deletefacet2.add(2);
					try{
						mysql.addDeleteModify(sql_Facet, params_deletefacet2);
						mysql.addDeleteModify(sql_FacetRelation, params_deletefacet2);
						mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet2);
//						mysql.addDeleteModify(sql_AssembleImage, params_deletefacet2);
//						mysql.addDeleteModify(sql_AssmbleText, params_deletefacet2);
					}catch(Exception e){
						e.printStackTrace();
					}
					}
				}
						result=mysql.addDeleteModify(sql_Facet, params_deletefacet1);
						if(result){
							try{
								mysql.addDeleteModify(sql_FacetRelation, params_deletefacet1);
								mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet1);
//								mysql.addDeleteModify(sql_AssembleImage, params_deletefacet1);
//								mysql.addDeleteModify(sql_AssmbleText, params_deletefacet1);
							}catch(Exception e){
								e.printStackTrace();
							}
						}	
				}		
			catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(FacetName+" 删除成功~")).build();
			}else{
				return Response.status(401).entity(new error(FacetName+" 删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
	}
		
		
		
		@GET
		@Path("/deleteFacet2")
		@ApiOperation(value = "删除二级分面", notes = "删除二级分面")
		@ApiResponses(value = {
				@ApiResponse(code = 402, message = "数据库错误",response=error.class),
				@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
		@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
		public static Response deleteFacet2(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
			/**
			 * 删除指定课程及主题下的二级分面
			 */
			try{
				boolean result=false;
				mysqlUtils mysql=new mysqlUtils();
				String sql_Facet="delete from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_FacetRelation="delete from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ChildFacet=? and ChildLayer=?;";
				String sql_AssembleFragment="delete from "+Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssmbleText="delete from "+Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssembleImage="delete from "+Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				
				String sql_queryfacet3="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2';";
				List<Object> params_deletefacet2=new ArrayList<Object>();
				List<Object> params_queryfacet3=new ArrayList<Object>();
				params_deletefacet2.add(ClassName);
				params_deletefacet2.add(TermName);
				params_deletefacet2.add(FacetName);
				params_deletefacet2.add(2);
				params_queryfacet3.add(ClassName);
				params_queryfacet3.add(TermName);
				params_queryfacet3.add(FacetName);
				try{
					List<Map<String, Object>> res_queryfacet3=mysql.returnMultipleResult(sql_queryfacet3, params_queryfacet3);
					if(res_queryfacet3.size()!=0){
						for(int i=0;i<res_queryfacet3.size();i++){
						List<Object> params_deletefacet3=new ArrayList<Object>();
						params_deletefacet3.add(ClassName);
						params_deletefacet3.add(TermName);
						params_deletefacet3.add(res_queryfacet3.get(i).get("ChildFacet"));
						params_deletefacet3.add(3);
						try{
							mysql.addDeleteModify(sql_Facet, params_deletefacet3);
							mysql.addDeleteModify(sql_FacetRelation, params_deletefacet3);
							mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet3);
//							mysql.addDeleteModify(sql_AssembleImage, params_deletefacet3);
//							mysql.addDeleteModify(sql_AssmbleText, params_deletefacet3);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
					}
							result=mysql.addDeleteModify(sql_Facet, params_deletefacet2);
							if(result){
								try{
									mysql.addDeleteModify(sql_FacetRelation, params_deletefacet2);
									mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet2);
//									mysql.addDeleteModify(sql_AssembleImage, params_deletefacet2);
//									mysql.addDeleteModify(sql_AssmbleText, params_deletefacet2);
								}catch(Exception e){
									e.printStackTrace();
								}
							}	
					}		
				catch(Exception e){
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
				if (result) {
					return Response.status(200).entity(new success(FacetName+" 删除成功~")).build();
				}else{
					return Response.status(401).entity(new error(FacetName+" 删除失败~")).build();
				}
		}catch(Exception e){
			return Response.status(402).entity(new error(e.toString())).build();
		}
		
	}
		
		
		@GET
		@Path("/deleteFacet3")
		@ApiOperation(value = "删除三级分面", notes = "删除三级分面")
		@ApiResponses(value = {
				@ApiResponse(code = 402, message = "数据库错误",response=error.class),
				@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
		@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
		public static Response deleteFacet3(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
			/**
			 * 删除指定课程及主题下的三级分面
			 */
			try{
				boolean result=false;
				mysqlUtils mysql=new mysqlUtils();
				String sql_Facet="delete from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_FacetRelation="delete from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ChildFacet=? and ChildLayer=?;";
				String sql_AssembleFragment="delete from "+Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssmbleText="delete from "+Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssembleImage="delete from "+Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				
				List<Object> params_deletefacet3=new ArrayList<Object>();
				params_deletefacet3.add(ClassName);
				params_deletefacet3.add(TermName);
				params_deletefacet3.add(FacetName);
				params_deletefacet3.add(3);
				try{
							result=mysql.addDeleteModify(sql_Facet, params_deletefacet3);
							if(result){
								try{
									mysql.addDeleteModify(sql_FacetRelation, params_deletefacet3);
									mysql.addDeleteModify(sql_AssembleFragment, params_deletefacet3);
//									mysql.addDeleteModify(sql_AssembleImage, params_deletefacet3);
//									mysql.addDeleteModify(sql_AssmbleText, params_deletefacet3);
								}catch(Exception e){
									e.printStackTrace();
								}
							}	
					}		
				catch(Exception e){
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
				if (result) {
					return Response.status(200).entity(new success(FacetName+" 删除成功~")).build();
				}else{
					return Response.status(401).entity(new error(FacetName+" 删除失败~")).build();
				}
		}catch(Exception e){
			return Response.status(402).entity(new error(e.toString())).build();
		}
		
	}
		
		
		@GET
		@Path("/updataFacet1")
		@ApiOperation(value = "修改一级分面名", notes = "修改一级分面名")
		@ApiResponses(value = {
				@ApiResponse(code = 402, message = "数据库错误",response=error.class),
				@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
		@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
		public static Response updateFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "新分面名字", required = true) @QueryParam("NewFacetName") String NewFacetName) {
			/**
			 * 修改指定课程及主题下的一级分面名
			 */
			try{
				boolean result=false;
				mysqlUtils mysql=new mysqlUtils();
				String sql_Facet="update "+Config.FACET_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_FacetRelation="update "+Config.FACET_RELATION_TABLE+" set ParentFacet=? where ClassName=? and TermName=? and ParentFacet=? and ParentLayer=?;";
				String sql_AssembleFragment="update "+Config.ASSEMBLE_FRAGMENT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssmbleText="update "+Config.ASSEMBLE_TEXT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssembleImage="update "+Config.ASSEMBLE_IMAGE_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				
				String exist_facet="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='1'";
				String sql_queryfacet2="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1';";
				List<Object> params_updatefacet1=new ArrayList<Object>();
				List<Object> params_queryfacet2=new ArrayList<Object>();
				List<Object> params_exist=new ArrayList<Object>();
				params_updatefacet1.add(NewFacetName);
				params_updatefacet1.add(ClassName);
				params_updatefacet1.add(TermName);
				params_updatefacet1.add(FacetName);
				params_updatefacet1.add(1);
				params_queryfacet2.add(ClassName);
				params_queryfacet2.add(TermName);
				params_queryfacet2.add(FacetName);
				params_exist.add(ClassName);
				params_exist.add(TermName);
				params_exist.add(NewFacetName);
				try{
					List<Map<String, Object>> res_exist=mysql.returnMultipleResult(exist_facet, params_exist);
					if(res_exist.size()==0){
					
					try{
					List<Map<String, Object>> res_queryfacet2=mysql.returnMultipleResult(sql_queryfacet2, params_queryfacet2);
					if(res_queryfacet2.size()!=0){
						for(int i=0;i<res_queryfacet2.size();i++){
						List<Object> params_updatefacet2=new ArrayList<Object>();
						params_updatefacet2.add(NewFacetName);
						params_updatefacet2.add(ClassName);
						params_updatefacet2.add(TermName);
						params_updatefacet2.add(FacetName);
						params_updatefacet2.add(1);
						try{
							mysql.addDeleteModify(sql_FacetRelation, params_updatefacet2);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
					}
							result=mysql.addDeleteModify(sql_Facet, params_updatefacet1);
							if(result){
								try{
									mysql.addDeleteModify(sql_AssembleFragment, params_updatefacet1);
//									mysql.addDeleteModify(sql_AssembleImage, params_updatefacet1);
//									mysql.addDeleteModify(sql_AssmbleText, params_updatefacet1);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
					}catch(Exception e){e.printStackTrace();}
					}
					else{
						return Response.status(200).entity(new success("一级分面 "+NewFacetName+" 已存在")).build();
					}
					
				
				}		
				catch(Exception e){
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
				if (result) {
					return Response.status(200).entity(new success(FacetName+"改为"+NewFacetName+" 修改成功~")).build();
				}else{
					return Response.status(401).entity(new error(FacetName+"改为"+NewFacetName+" 修改失败~")).build();
				}
		}catch(Exception e){
			return Response.status(402).entity(new error(e.toString())).build();
		}
		}
		
		
		@GET
		@Path("/updataFacet2")
		@ApiOperation(value = "修改二级分面名", notes = "修改二级分面名")
		@ApiResponses(value = {
				@ApiResponse(code = 402, message = "数据库错误",response=error.class),
				@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
		@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
		public static Response updateFacet2(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "新分面名字", required = true) @QueryParam("NewFacetName") String NewFacetName) {
			/**
			 * 修改指定课程及主题下的二级分面名
			 */
			try{
				boolean result=false;
				mysqlUtils mysql=new mysqlUtils();
				String sql_Facet="update "+Config.FACET_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_FacetRelationParent="update "+Config.FACET_RELATION_TABLE+" set ParentFacet=? where ClassName=? and TermName=? and ParentFacet=? and ParentLayer=?;";
				String sql_FacetRelationChild="update "+Config.FACET_RELATION_TABLE+" set ChildFacet=? where ClassName=? and TermName=? and ChildFacet=? and ChildLayer=?;";
				String sql_AssembleFragment="update "+Config.ASSEMBLE_FRAGMENT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssmbleText="update "+Config.ASSEMBLE_TEXT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssembleImage="update "+Config.ASSEMBLE_IMAGE_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				
				String exist_facet="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='2'";
				String sql_queryfacet1="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ChildFacet=? and ChildLayer='2';";
				String sql_queryfacet3="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2';";
				List<Object> params_updatefacet2=new ArrayList<Object>();
				List<Object> params_queryfacet=new ArrayList<Object>();
				List<Object> params_exist=new ArrayList<Object>();
				params_updatefacet2.add(NewFacetName);
				params_updatefacet2.add(ClassName);
				params_updatefacet2.add(TermName);
				params_updatefacet2.add(FacetName);
				params_updatefacet2.add(2);
				params_queryfacet.add(ClassName);
				params_queryfacet.add(TermName);
				params_queryfacet.add(FacetName);
				params_exist.add(ClassName);
				params_exist.add(TermName);
				params_exist.add(NewFacetName);
				try{
					List<Map<String, Object>> res_exist=mysql.returnMultipleResult(exist_facet, params_exist);
					if(res_exist.size()==0){
					
					try{
					
					
					List<Map<String, Object>> res_queryfacet3=mysql.returnMultipleResult(sql_queryfacet3, params_queryfacet);
					if(res_queryfacet3.size()!=0){
						for(int i=0;i<res_queryfacet3.size();i++){
						List<Object> params_updatefacet3=new ArrayList<Object>();
						params_updatefacet3.add(NewFacetName);
						params_updatefacet3.add(ClassName);
						params_updatefacet3.add(TermName);
						params_updatefacet3.add(FacetName);
						params_updatefacet3.add(2);
						try{
							mysql.addDeleteModify(sql_FacetRelationParent, params_updatefacet3);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
					}
					
					List<Map<String, Object>> res_queryfacet1=mysql.returnMultipleResult(sql_queryfacet1, params_queryfacet);
					if(res_queryfacet1.size()!=0){
						for(int i=0;i<res_queryfacet1.size();i++){
						List<Object> params_updatefacet1=new ArrayList<Object>();
						params_updatefacet1.add(NewFacetName);
						params_updatefacet1.add(ClassName);
						params_updatefacet1.add(TermName);
						params_updatefacet1.add(FacetName);
						params_updatefacet1.add(2);
						try{
							mysql.addDeleteModify(sql_FacetRelationChild, params_updatefacet1);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
					}
					
							result=mysql.addDeleteModify(sql_Facet, params_updatefacet2);
							if(result){
								try{
									mysql.addDeleteModify(sql_AssembleFragment, params_updatefacet2);
//									mysql.addDeleteModify(sql_AssembleImage, params_updatefacet2);
//									mysql.addDeleteModify(sql_AssmbleText, params_updatefacet2);
								}catch(Exception e){
									e.printStackTrace();
								}
							}	
					}catch(Exception e){e.printStackTrace();}
					}
					else{
						return Response.status(200).entity(new success("二级分面 "+NewFacetName+" 已存在")).build();
					}
				
				
				
				}		
				catch(Exception e){
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
				if (result) {
					return Response.status(200).entity(new success(FacetName+"改为"+NewFacetName+" 修改成功~")).build();
				}else{
					return Response.status(401).entity(new error(FacetName+"改为"+NewFacetName+" 修改失败~")).build();
				}
		}catch(Exception e){
			return Response.status(402).entity(new error(e.toString())).build();
		}
		}
		
		
		@GET
		@Path("/updataFacet3")
		@ApiOperation(value = "修改三级分面名", notes = "修改三级分面名")
		@ApiResponses(value = {
				@ApiResponse(code = 402, message = "数据库错误",response=error.class),
				@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
		@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
		public static Response updateFacet3(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "新分面名字", required = true) @QueryParam("NewFacetName") String NewFacetName) {
			/**
			 * 修改指定课程及主题下的一级分面名
			 */
			try{
				boolean result=false;
				mysqlUtils mysql=new mysqlUtils();
				String sql_Facet="update "+Config.FACET_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_FacetRelation="update "+Config.FACET_RELATION_TABLE+" set ChildFacet=? where ClassName=? and TermName=? and ChildFacet=? and ChildLayer=?;";
				String sql_AssembleFragment="update "+Config.ASSEMBLE_FRAGMENT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssmbleText="update "+Config.ASSEMBLE_TEXT_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
//				String sql_AssembleImage="update "+Config.ASSEMBLE_IMAGE_TABLE+" set FacetName=? where ClassName=? and TermName=? and FacetName=? and FacetLayer=?;";
				String sql_queryfacet3="select * from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ChildFacet=? and ChildLayer='3';";
				String exist_facet="select * from "+Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer='3'";
				
				List<Object> params_updatefacet3=new ArrayList<Object>();
				List<Object> params_queryfacet3=new ArrayList<Object>();
				List<Object> params_exist=new ArrayList<Object>();
				params_updatefacet3.add(NewFacetName);
				params_updatefacet3.add(ClassName);
				params_updatefacet3.add(TermName);
				params_updatefacet3.add(FacetName);
				params_updatefacet3.add(3);
				params_queryfacet3.add(ClassName);
				params_queryfacet3.add(TermName);
				params_queryfacet3.add(FacetName);
				params_exist.add(ClassName);
				params_exist.add(TermName);
				params_exist.add(NewFacetName);
				try{
					List<Map<String, Object>> res_exist=mysql.returnMultipleResult(exist_facet, params_exist);
					if(res_exist.size()==0){
					
					try{
					
					
					List<Map<String, Object>> res_queryfacet3=mysql.returnMultipleResult(sql_queryfacet3, params_queryfacet3);
					if(res_queryfacet3.size()!=0){
						for(int i=0;i<res_queryfacet3.size();i++){
						List<Object> params_updatefacet2=new ArrayList<Object>();
						params_updatefacet2.add(NewFacetName);
						params_updatefacet2.add(ClassName);
						params_updatefacet2.add(TermName);
						params_updatefacet2.add(FacetName);
						params_updatefacet2.add(3);
						try{
							mysql.addDeleteModify(sql_FacetRelation, params_updatefacet2);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
					}
							result=mysql.addDeleteModify(sql_Facet, params_updatefacet3);
							if(result){
								try{
									mysql.addDeleteModify(sql_AssembleFragment, params_updatefacet3);
//									mysql.addDeleteModify(sql_AssembleImage, params_updatefacet3);
//									mysql.addDeleteModify(sql_AssmbleText, params_updatefacet3);
								}catch(Exception e){
									e.printStackTrace();
								}
							}	
					
					}catch(Exception e){e.printStackTrace();}
					}
					else{
						return Response.status(200).entity(new success("三级分面 "+NewFacetName+" 已存在")).build();
					}
				
				
				}		
				catch(Exception e){
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
				if (result) {
					return Response.status(200).entity(new success(FacetName+"改为"+NewFacetName+" 修改成功~")).build();
				}else{
					return Response.status(401).entity(new error(FacetName+"改为"+NewFacetName+" 修改失败~")).build();
				}
		}catch(Exception e){
			return Response.status(402).entity(new error(e.toString())).build();
		}
		}

}
