package assemble;

import facet.FacetDAO;
import facet.bean.FacetRelation;
import facet.bean.FacetSimple;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
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

import spider.bean.Count;
import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;
import assemble.bean.Branch;
import assemble.bean.BranchComplex;
import assemble.bean.BranchSimple;
import assemble.bean.Leaf;
import assemble.bean.Tree;
import domainTopic.DomainTopicOldDAO;

/**  
 * 获取碎片装配的API
 * 1. 
 * 2. 
 * 3. 
 *  
 * @author 郑元浩 
 * @date 2016年12月3日
 */

@Path("/AssembleAPI")
@Api(value = "AssembleAPI")
public class AssembleAPI {

	public static void main(String[] args) {
		Response response = getTextByTopic("数据结构", "抽象资料型别");
//		Log.log(response.getEntity());
		
		response = getCountByDomain("数据结构");
//		Log.log(response.getEntity());

		response = getTreeByTopic("数据结构", "抽象资料型别");
		Log.log(response.getEntity());
		
	}
	
	
	@GET
	@Path("/getTextByTopic")
	@ApiOperation(value = "获得知识主题的文本信息", notes = "输入领域名和知识主题，获得知识主题的文本信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByTopic(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		Response response = null;
		
		/**
		 * 读取assemble_text，获得知识点的文本碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
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
	@Path("/getTextByFacet")
	@ApiOperation(value = "获得知识主题某分面下的文本信息", notes = "输入领域名/知识主题/分面名，获得知识主题某分面下的文本信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByFacet(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName,
			@DefaultValue("摘要") @ApiParam(value = "分面名", required = true) @QueryParam("FacetName") String facetName) {
		Response response = null;
		
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
			/**
			 * 对于没有子分面的分面返回结果不为空
			 * 对于含有子分面的分面因为在装配表中没有数据，因此数据应该是空的，此时应该去facet_relation表格确定它的子分面，显示它的子分面的碎片信息
			 */
			if (results.size() != 0) {
				/**
				 * 对于没有子分面的分面返回结果不为空，直接返回
				 */
				response = Response.status(200).entity(results).build();
			} else {
				/**
				 * 在Facet_relation表格中寻找子分面的信息
				 */
				mysqlUtils mysqlFacetRelation = new mysqlUtils();
				String sqlFacetRelation = "select * from " + Config.FACET_RELATION_TABLE 
										+ " where ClassName=? and TermName=? and ParentFacet=?";
				List<Object> paramsFacetRelation = new ArrayList<Object>();
				paramsFacetRelation.add(className);
				paramsFacetRelation.add(topicName);
				paramsFacetRelation.add(facetName);
				List<Map<String, Object>> resultsFacet = mysqlFacetRelation.returnMultipleResult(sqlFacetRelation, paramsFacetRelation);
				if (resultsFacet.size() != 0) {
					/**
					 * 找到一级分面的子分面集合，遍历子分面集合获取其装配的碎片集合并返回
					 */
					List<Map<String, Object>> resultsAllChildFacet = mysql.returnMultipleResult(sql, params);
					for (int i = 0; i < resultsFacet.size(); i++) {
						Map<String, Object> map = resultsFacet.get(i);
						String childFacet = map.get("ChildFacet").toString();
						int childLayer = Integer.parseInt(map.get("ChildLayer").toString());
						if (childLayer == 2) {
							/**
							 * 子分面是第二层的话，再次根据子分面的信息去读取Assemble_text表格，获取分面内容
							 */
							mysqlUtils mysql2 = new mysqlUtils();
							String sql2 = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=? and FacetName=?";
							List<Object> params2 = new ArrayList<Object>();
							params2.add(className);
							params2.add(topicName);
							params2.add(childFacet);
							List<Map<String, Object>> results2 = mysql2.returnMultipleResult(sql2, params2);
							resultsAllChildFacet.addAll(results2);
							mysql2.closeconnection();
						} else if (childLayer == 3) {
							/**
							 * 子分面是第三层的话，不读取，因为目前不支持第三层，后期再添加该功能
							 */
							Log.log("点击含有第三层分面的第二层分面分枝时，不会显示任何内容，程序猿最近加班严重，待开发...");
						}
					}
					/**
					 * 返回所有子分面的碎片集合
					 */
					response = Response.status(200).entity(resultsAllChildFacet).build();
				} else {
					Log.log("该一级分面没有子分面，该分面没有爬取到对应碎片");
				}
				mysqlFacetRelation.closeconnection();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}


	@GET
	@Path("/getImageByTopic")
	@ApiOperation(value = "获得知识主题的图片信息", notes = "输入领域名和知识主题，获得知识主题的图片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByTopic(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		Response response = null;
		
		/**
		 * 读取spider_text，获得知识点的图片碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
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
	@Path("/getImageByFacet")
	@ApiOperation(value = "获得知识主题某分面下的图片信息", notes = "输入领域名/知识主题/分面名，获得知识主题某分面下的图片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByFacet(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName,
			@DefaultValue("摘要") @ApiParam(value = "分面名", required = true) @QueryParam("FacetName") String facetName) {
		Response response = null;
		
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
			/**
			 * 对于没有子分面的分面返回结果不为空
			 * 对于含有子分面的分面因为在装配表中没有数据，因此数据应该是空的，此时应该去facet_relation表格确定它的子分面，显示它的子分面的碎片信息
			 */
			if (results.size() != 0) {
				/**
				 * 对于没有子分面的分面返回结果不为空，直接返回
				 */
				response = Response.status(200).entity(results).build();
			} else {
				/**
				 * 在Facet_relation表格中寻找子分面的信息
				 */
				mysqlUtils mysqlFacetRelation = new mysqlUtils();
				String sqlFacetRelation = "select * from " + Config.FACET_RELATION_TABLE 
										+ " where ClassName=? and TermName=? and ParentFacet=?";
				List<Object> paramsFacetRelation = new ArrayList<Object>();
				paramsFacetRelation.add(className);
				paramsFacetRelation.add(topicName);
				paramsFacetRelation.add(facetName);
				List<Map<String, Object>> resultsFacet = mysqlFacetRelation.returnMultipleResult(sqlFacetRelation, paramsFacetRelation);
				if (resultsFacet.size() != 0) {
					/**
					 * 找到一级分面的子分面集合，遍历子分面集合获取其装配的碎片集合并返回
					 */
					List<Map<String, Object>> resultsAllChildFacet = mysql.returnMultipleResult(sql, params);
					for (int i = 0; i < resultsFacet.size(); i++) {
						Map<String, Object> map = resultsFacet.get(i);
						String childFacet = map.get("ChildFacet").toString();
						int childLayer = Integer.parseInt(map.get("ChildLayer").toString());
						if (childLayer == 2) {
							/**
							 * 子分面是第二层的话，再次根据子分面的信息去读取Assemble_image表格，获取分面内容
							 */
							mysqlUtils mysql2 = new mysqlUtils();
							String sql2 = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=? and FacetName=?";
							List<Object> params2 = new ArrayList<Object>();
							params2.add(className);
							params2.add(topicName);
							params2.add(childFacet);
							List<Map<String, Object>> results2 = mysql2.returnMultipleResult(sql2, params2);
							resultsAllChildFacet.addAll(results2);
							mysql2.closeconnection();
						} else if (childLayer == 3) {
							/**
							 * 子分面是第三层的话，不读取，因为目前不支持第三层，后期再添加该功能
							 */
							Log.log("点击含有第三层分面的第二层分面分枝时，不会显示任何内容，程序猿最近加班严重，待开发...");
						}
					}
					/**
					 * 返回所有子分面的碎片集合
					 */
					response = Response.status(200).entity(resultsAllChildFacet).build();
				} else {
					Log.log("该一级分面没有子分面，该分面没有爬取到对应碎片");
				}
				mysqlFacetRelation.closeconnection();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	@GET
	@Path("/getCountByDomain")
	@ApiOperation(value = "获得某门课程的文本和图片数量", notes = "输入领域名，获得某门课程的文本和图片数量")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getCountByDomain(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
		List<Count> countList = new ArrayList<Count>();
		
		/**
		 * 读取spider_text，获得文本数量
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			int textSize = results.size();
			countList.add(new Count("text", textSize));
//			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		
		/**
		 * 读取spider_image，获得文本数量
		 */
		mysqlUtils mysqlImage = new mysqlUtils();
		String sqlImage = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=?";
		List<Object> paramsImage = new ArrayList<Object>();
		paramsImage.add(className);
		try {
			List<Map<String, Object>> results = mysqlImage.returnMultipleResult(sqlImage, paramsImage);
			int imageSize = results.size();
			countList.add(new Count("image", imageSize));
//			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysqlImage.closeconnection();
		}
		
		response = Response.status(200).entity(countList).build();
		
		return response;
	}
	
	
	@GET
	@Path("/getCountByTopic")
	@ApiOperation(value = "获得某门课程下主题的文本和图片数量", notes = "输入领域名和主题，获得某门课程下主题的文本和图片数量")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getCountByTopic(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		Response response = null;
		List<Count> countList = new ArrayList<Count>();
		
		/**
		 * 读取spider_text，获得文本数量
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			int textSize = results.size();
			countList.add(new Count("text", textSize));
//			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		
		/**
		 * 读取spider_image，获得文本数量
		 */
		mysqlUtils mysqlImage = new mysqlUtils();
		String sqlImage = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=?";
		List<Object> paramsImage = new ArrayList<Object>();
		paramsImage.add(className);
		paramsImage.add(topicName);
		try {
			List<Map<String, Object>> results = mysqlImage.returnMultipleResult(sqlImage, paramsImage);
			int imageSize = results.size();
			countList.add(new Count("image", imageSize));
//			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysqlImage.closeconnection();
		}
		
		response = Response.status(200).entity(countList).build();
		
		return response;
	}
	
	
	@GET
	@Path("/getCountByFacet")
	@ApiOperation(value = "获得某门课程下某个主题某个分面下的文本和图片数量", notes = "输入领域名和主题，获得某门课程下某个主题某个分面下的文本和图片数量")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getCountByFacet(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName,
			@DefaultValue("摘要") @ApiParam(value = "分面名", required = true) @QueryParam("FacetName") String facetName) {
		Response response = null;
		List<Count> countList = new ArrayList<Count>();
		
		/**
		 * 读取assemble_text，获得文本数量
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=? and FacetName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		params.add(facetName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			
			/**
			 * 对于没有子分面的分面返回结果不为空
			 * 对于含有子分面的分面因为在装配表中没有数据，因此数据应该是空的，此时应该去facet_relation表格确定它的子分面，显示它的子分面的碎片信息
			 */
			if (results.size() != 0) {
				/**
				 * 对于没有子分面的分面返回结果不为空，直接返回
				 */
				int textSize = results.size();
				countList.add(new Count("text", textSize));
			} else {
				/**
				 * 在Facet_relation表格中寻找子分面的信息
				 */
				mysqlUtils mysqlFacetRelation = new mysqlUtils();
				String sqlFacetRelation = "select * from " + Config.FACET_RELATION_TABLE 
										+ " where ClassName=? and TermName=? and ParentFacet=?";
				List<Object> paramsFacetRelation = new ArrayList<Object>();
				paramsFacetRelation.add(className);
				paramsFacetRelation.add(topicName);
				paramsFacetRelation.add(facetName);
				List<Map<String, Object>> resultsFacet = mysqlFacetRelation.returnMultipleResult(sqlFacetRelation, paramsFacetRelation);
				if (resultsFacet.size() != 0) {
					/**
					 * 找到一级分面的子分面集合，遍历子分面集合获取其装配的碎片集合并返回
					 */
					int textSize = 0;
					for (int i = 0; i < resultsFacet.size(); i++) {
						Map<String, Object> map = resultsFacet.get(i);
						String childFacet = map.get("ChildFacet").toString();
						int childLayer = Integer.parseInt(map.get("ChildLayer").toString());
						if (childLayer == 2) {
							/**
							 * 子分面是第二层的话，再次根据子分面的信息去读取Assemble_text表格，获取分面内容
							 */
							mysqlUtils mysql2 = new mysqlUtils();
							String sql2 = "select * from " + Config.ASSEMBLE_TEXT_TABLE + " where ClassName=? and TermName=? and FacetName=?";
							List<Object> params2 = new ArrayList<Object>();
							params2.add(className);
							params2.add(topicName);
							params2.add(childFacet);
							List<Map<String, Object>> results2 = mysql2.returnMultipleResult(sql2, params2);
							textSize += results2.size();
							mysql2.closeconnection();
						} else if (childLayer == 3) {
							/**
							 * 子分面是第三层的话，不读取，因为目前不支持第三层，后期再添加该功能
							 */
							Log.log("点击含有第三层分面的第二层分面分枝时，不会显示任何内容，程序猿最近加班严重，待开发...");
						}
					}
					
					/**
					 * 返回所有子分面的碎片集合
					 */
					countList.add(new Count("text", textSize));
				} else {
					countList.add(new Count("text", 0));
					Log.log("该一级分面没有子分面，该分面没有爬取到对应碎片");
				}
				mysqlFacetRelation.closeconnection();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		
		/**
		 * 读取spider_image，获得文本数量
		 */
		mysqlUtils mysqlImage = new mysqlUtils();
		String sqlImage = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=? and FacetName=?";
		List<Object> paramsImage = new ArrayList<Object>();
		paramsImage.add(className);
		paramsImage.add(topicName);
		paramsImage.add(facetName);
		try {
			List<Map<String, Object>> results = mysqlImage.returnMultipleResult(sqlImage, paramsImage);
			
			/**
			 * 对于没有子分面的分面返回结果不为空
			 * 对于含有子分面的分面因为在装配表中没有数据，因此数据应该是空的，此时应该去facet_relation表格确定它的子分面，显示它的子分面的碎片信息
			 */
			if (results.size() != 0) {
				/**
				 * 对于没有子分面的分面返回结果不为空，直接返回
				 */
				int imageSize = results.size();
				countList.add(new Count("image", imageSize));
			} else {
				/**
				 * 在Facet_relation表格中寻找子分面的信息
				 */
				mysqlUtils mysqlFacetRelation = new mysqlUtils();
				String sqlFacetRelation = "select * from " + Config.FACET_RELATION_TABLE 
										+ " where ClassName=? and TermName=? and ParentFacet=?";
				List<Object> paramsFacetRelation = new ArrayList<Object>();
				paramsFacetRelation.add(className);
				paramsFacetRelation.add(topicName);
				paramsFacetRelation.add(facetName);
				List<Map<String, Object>> resultsFacet = mysqlFacetRelation.returnMultipleResult(sqlFacetRelation, paramsFacetRelation);
				if (resultsFacet.size() != 0) {
					/**
					 * 找到一级分面的子分面集合，遍历子分面集合获取其装配的碎片集合并返回
					 */
					int imageSize = 0;
					for (int i = 0; i < resultsFacet.size(); i++) {
						Map<String, Object> map = resultsFacet.get(i);
						String childFacet = map.get("ChildFacet").toString();
						int childLayer = Integer.parseInt(map.get("ChildLayer").toString());
						if (childLayer == 2) {
							/**
							 * 子分面是第二层的话，再次根据子分面的信息去读取Assemble_image表格，获取分面内容
							 */
							mysqlUtils mysql2 = new mysqlUtils();
							String sql2 = "select * from " + Config.ASSEMBLE_IMAGE_TABLE + " where ClassName=? and TermName=? and FacetName=?";
							List<Object> params2 = new ArrayList<Object>();
							params2.add(className);
							params2.add(topicName);
							params2.add(childFacet);
							List<Map<String, Object>> results2 = mysql2.returnMultipleResult(sql2, params2);
							imageSize += results2.size();
							mysql2.closeconnection();
						} else if (childLayer == 3) {
							/**
							 * 子分面是第三层的话，不读取，因为目前不支持第三层，后期再添加该功能
							 */
							Log.log("点击含有第三层分面的第二层分面分枝时，不会显示任何内容，程序猿最近加班严重，待开发...");
						}
					}
					/**
					 * 返回所有子分面的碎片集合
					 */
					countList.add(new Count("image", imageSize));
				} else {
					countList.add(new Count("image", 0));
					Log.log("该一级分面没有子分面，该分面没有爬取到对应碎片");
				}
				mysqlFacetRelation.closeconnection();
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysqlImage.closeconnection();
		}
		
		/**
		 * 返回分面文本和图片数量的统计
		 */
		response = Response.status(200).entity(countList).build();
		
		return response;
	}
	
	
	
	
	
	
	
	
	
	
	@GET
	@Path("/getTreeByTopic")
	@ApiOperation(value = "获得实例化主题分面树的数据", notes = "输入领域名和知识主题，获得实例化主题分面树的数据")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 402, message = "MySql数据库  查询成功，不存在该实例化主题分面树"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTreeByTopic(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		
		Response response = null;
		/**
		 * 返回该主题一级/二级分面信息
		 */
		List<FacetSimple> facetSimpleList1 = FacetDAO.getFacet(className, topicName, 1);
		List<FacetRelation> facetRelationList = FacetDAO.getFacetRelation(className, topicName, 1, 2);
		List<FacetRelation> facetRelationList2 = FacetDAO.getFacetRelation(className, topicName, 2, 3);
		
		
		/**
		 * 一个主题返回一个实例化主题分面树
		 */
		Tree tree = new Tree();
		int totalbranchlevel = 2;
		int branchnum = facetSimpleList1.size();
		int term_id = DomainTopicOldDAO.getDomainTopic(className, topicName);
		String name = topicName;
		List<Branch> children = new ArrayList<Branch>();
		
		/**
		 * 得到主题分面树每个一级分枝上的数据
		 */
		for (int i = 0; i < facetSimpleList1.size(); i++) {
			/**
			 * 判断一级分面是否存在二级分面，不存在直接返回BranchSimple为树的节点，存在返回BranchComplex为树的节点
			 */
			FacetSimple facetSimple = facetSimpleList1.get(i);
			List<FacetSimple> secondFacetList = FacetDAO.getChildFacet(facetSimple, facetRelationList);
			
			/**
			 * 判断是否存在二级分面
			 */
			if(secondFacetList.size() == 0){
				
				/**
				 * 不存在二级分面，使用BranchSimple继承Branch
				 */
				int totalbranchlevel2 = 0;
				String facet_name = facetSimple.getFacetName();
				int totalbranchnum = 0;
				String type = "branch";
				/**
				 * 树叶同时包含：文本碎片 + 图片碎片
				 */
				List<Leaf> leafList = new ArrayList<Leaf>();
				List<Leaf> leafTextList = AssembleDAO.getTextByFacet(className, topicName, facet_name);
				List<Leaf> leafImageListImage = AssembleDAO.getImageByFacet(className, topicName, facet_name);
				leafList.addAll(leafTextList);
				leafList.addAll(leafImageListImage);
				int totalleafnum = leafList.size();
				BranchSimple branchSimple = new BranchSimple(totalbranchlevel2, facet_name, totalbranchnum, type, leafList, totalleafnum);
				children.add(branchSimple);
				
			} else {
				
				/**
				 * 存在二级分面，使用BranchComplex继承Branch
				 */
				int totalbranchlevel2 = 1;
				String facet_name = facetSimple.getFacetName();
				int totalbranchnum = secondFacetList.size();
				String type = "branch";
				
				/**
				 * 设置二级分枝的子分枝
				 */
				List<BranchSimple> branchSimpleList = new ArrayList<BranchSimple>();
				for (int j = 0; j < secondFacetList.size(); j++) {
					
					/**
					 * 遍历每一个二级分面，设置每个二级分面的
					 */
					FacetSimple secondFacet = secondFacetList.get(j);
					List<FacetSimple> thirdFacetList = FacetDAO.getChildFacet(secondFacet, facetRelationList2);
					
					/**
					 * 判断是否存在三级分面
					 */
					if(thirdFacetList.size() == 0){
						
						/**
						 * 不存在三级分面，将BranchSimple添加到对应的父亲branchSimpleList中，文本碎片
						 */
						int totalbranchlevel3 = 0;
						String secondFacetName = secondFacet.getFacetName();
						int totalbranchnum3 = 0;
						String type3 = "branch";
						/**
						 * 树叶同时包含：文本碎片 + 图片碎片
						 */
						List<Leaf> leafList = new ArrayList<Leaf>();
						List<Leaf> leafTextList = AssembleDAO.getTextByFacet(className, topicName, secondFacetName);
						List<Leaf> leafImageList = AssembleDAO.getImageByFacet(className, topicName, secondFacetName);
						leafList.addAll(leafTextList);
						leafList.addAll(leafImageList);
						int totalleafnum = leafList.size();
						BranchSimple branchSimple = new BranchSimple(totalbranchlevel3, secondFacetName, totalbranchnum3, type3, leafList, totalleafnum);
						branchSimpleList.add(branchSimple);
						
					} else {
						Log.log(className + "--->" + topicName + "--->" + secondFacet.getFacetName() + ", 该二级分面存在三级分面，三级分面待开发");
						/**
						 * 存在三级分面，将三级分面的碎片内容全部挂载到二级分面上去
						 */
						List<Leaf> leafAllList = new ArrayList<Leaf>();
						for (int k = 0; k < thirdFacetList.size(); k++) {
							FacetSimple thirdFacet = thirdFacetList.get(k);
							String thirdFacetName = thirdFacet.getFacetName();
							
							/**
							 * 树叶同时包含：文本碎片 + 图片碎片
							 */
							List<Leaf> leafTextList = AssembleDAO.getTextByFacet(className, topicName, thirdFacetName);
							List<Leaf> leafImageList = AssembleDAO.getImageByFacet(className, topicName, thirdFacetName);
							leafAllList.addAll(leafTextList);
							leafAllList.addAll(leafImageList);
							
						}
						int totalbranchlevel3 = 0;
						String secondFacetName = secondFacet.getFacetName();
						int totalbranchnum3 = 0;
						String type3 = "branch";
						int totalleafnum = leafAllList.size();
						BranchSimple branchSimple = new BranchSimple(totalbranchlevel3, secondFacetName, totalbranchnum3, type3, leafAllList, totalleafnum);
						branchSimpleList.add(branchSimple);
					}
				}
				int totalleafnum = secondFacetList.size();
				/**
				 * 将其添加到树的分枝中
				 */
				BranchComplex branchComplex = new BranchComplex(totalbranchlevel2, facet_name, totalbranchnum, type, branchSimpleList, totalleafnum);
				children.add(branchComplex);
			}
			
		}
		
		/**
		 * 设置实例化分面树的值
		 */
		tree.setTotalbranchlevel(totalbranchlevel);
		tree.setBranchnum(branchnum);
		tree.setTerm_id(term_id);
		tree.setName(name);
		tree.setChildren(children);
		
		/**
		 * 返回实例化分面树数据
		 */
		response = Response.status(200).entity(tree).build();
		return response;
	}
	
	
	@GET
	@Path("/getTreeByTopic2")
	@ApiOperation(value = "获得实例化主题分面树的数据", notes = "输入领域名和知识主题，获得实例化主题分面树的数据")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 402, message = "MySql数据库  查询成功，不存在该实例化主题分面树"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTreeByTopic2(
			@DefaultValue("Data structure") @ApiParam(value = "领域名", required = true) @QueryParam("className") 
			String className,
			@DefaultValue("2-3-4_tree") @ApiParam(value = "主题名", required = true) @QueryParam("topicName") String topicName) {
		
		Response response = null;
		
		/**
		 * 返回该主题一级/二级分面信息
		 */
		List<FacetSimple> facetSimpleList1 = FacetDAO.getFacet(className, topicName, 1);
		List<FacetRelation> facetRelationList = FacetDAO.getFacetRelation(className, topicName, 1, 2);
		List<FacetRelation> facetRelationList2 = FacetDAO.getFacetRelation(className, topicName, 2, 3);
		
		
		/**
		 * 一个主题返回一个实例化主题分面树
		 */
		Tree tree = new Tree();
		int totalbranchlevel = 2;
		int branchnum = facetSimpleList1.size();
		int term_id = 0;
		String name = topicName;
		List<Branch> children = new ArrayList<Branch>();
		
		/**
		 * 得到主题分面树每个一级分枝上的数据
		 */
		for (int i = 0; i < facetSimpleList1.size(); i++) {
			/**
			 * 判断一级分面是否存在二级分面，不存在直接返回BranchSimple为树的节点，存在返回BranchComplex为树的节点
			 */
			FacetSimple facetSimple = facetSimpleList1.get(i);
			List<FacetSimple> secondFacetList = FacetDAO.getChildFacet(facetSimple, facetRelationList);
			
			/**
			 * 判断是否存在二级分面
			 */
			if(secondFacetList.size() == 0){
				
				/**
				 * 不存在二级分面，使用BranchSimple继承Branch
				 */
				int totalbranchlevel2 = 0;
				String facet_name = facetSimple.getFacetName();
				int totalbranchnum = 0;
				String type = "branch";
				/**
				 * 树叶同时包含：文本碎片 + 图片碎片
				 */
				List<Leaf> leafList = new ArrayList<Leaf>();
				int totalleafnum = leafList.size();
				BranchSimple branchSimple = new BranchSimple(totalbranchlevel2, facet_name, totalbranchnum, type, leafList, totalleafnum);
				children.add(branchSimple);
				
			} else {
				
				/**
				 * 存在二级分面，使用BranchComplex继承Branch
				 */
				int totalbranchlevel2 = 1;
				String facet_name = facetSimple.getFacetName();
				int totalbranchnum = secondFacetList.size();
				String type = "branch";
				
				/**
				 * 设置二级分枝的子分枝
				 */
				List<BranchSimple> branchSimpleList = new ArrayList<BranchSimple>();
				for (int j = 0; j < secondFacetList.size(); j++) {
					
					/**
					 * 遍历每一个二级分面，设置每个二级分面的
					 */
					FacetSimple secondFacet = secondFacetList.get(j);
					List<FacetSimple> thirdFacetList = FacetDAO.getChildFacet(secondFacet, facetRelationList2);
					
					/**
					 * 判断是否存在三级分面
					 */
					if(thirdFacetList.size() == 0){
						
						/**
						 * 不存在三级分面，将BranchSimple添加到对应的父亲branchSimpleList中，文本碎片
						 */
						int totalbranchlevel3 = 0;
						String secondFacetName = secondFacet.getFacetName();
						int totalbranchnum3 = 0;
						String type3 = "branch";
						/**
						 * 树叶同时包含：文本碎片 + 图片碎片
						 */
						List<Leaf> leafList = new ArrayList<Leaf>();
						int totalleafnum = leafList.size();
						BranchSimple branchSimple = new BranchSimple(totalbranchlevel3, secondFacetName, totalbranchnum3, type3, leafList, totalleafnum);
						branchSimpleList.add(branchSimple);
						
					} else {
						Log.log(className + "--->" + topicName + "--->" + secondFacet.getFacetName() + ", 该二级分面存在三级分面，三级分面待开发");
						/**
						 * 存在三级分面，将三级分面的碎片内容全部挂载到二级分面上去
						 */
						List<Leaf> leafAllList = new ArrayList<Leaf>();
						int totalbranchlevel3 = 0;
						String secondFacetName = secondFacet.getFacetName();
						int totalbranchnum3 = 0;
						String type3 = "branch";
						int totalleafnum = leafAllList.size();
						BranchSimple branchSimple = new BranchSimple(totalbranchlevel3, secondFacetName, totalbranchnum3, type3, leafAllList, totalleafnum);
						branchSimpleList.add(branchSimple);
					}
				}
				int totalleafnum = secondFacetList.size();
				/**
				 * 将其添加到树的分枝中
				 */
				BranchComplex branchComplex = new BranchComplex(totalbranchlevel2, facet_name, totalbranchnum, type, branchSimpleList, totalleafnum);
				children.add(branchComplex);
			}
			
		}
		
		/**
		 * 设置实例化分面树的值
		 */
		tree.setTotalbranchlevel(totalbranchlevel);
		tree.setBranchnum(branchnum);
		tree.setTerm_id(term_id);
		tree.setName(name);
		tree.setChildren(children);
		
		/**
		 * 返回实例化分面树数据
		 */
		response = Response.status(200).entity(tree).build();
		return response;
	}
	
	
	

}
