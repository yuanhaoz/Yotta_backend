package domainTopic;

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

import utils.mysqlUtils;
import app.Config;
import app.error;
import app.success;
import domainTopic.bean.Topic;

/**  
 * 领域术语抽取
 * @author 郑元浩 
 */
@Path("/DomainTopicAPI")
@Api(value = "DomainTopicAPI")
public class DomainTopicAPI {

	public static void main(String[] args) {
		
	}
	
	@GET
	@Path("/getDomainTopicAll")
	@ApiOperation(value = "获得所有知识主题的信息", notes = "输入领域名，获得所有知识主题的信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTopicAll(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
		/**
		 * 读取domain_topic，获得所有知识主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
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
	@Path("/getTopicRelation")
	@ApiOperation(value = "获得知识主题之间的上下位关系", notes = "输入领域名，获得所有知识主题之间的上下位关系")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTopicRelation(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("数据结构") @ApiParam(value = "术语名", required = true) @QueryParam("initTopic") String initTopic) {
		
		Topic topicAll = DomainTopicOldDAO.getRelationAll(className, initTopic);
		Response response = Response.status(200).entity(topicAll).build();
		
		return response;
	}
	
	@GET
	@Path("/createTopic")
	@ApiOperation(value = "创建一个主题", notes = "在选定的课程下添加新主题")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createClass(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TopicName") String TopicName) {
//		Response response = null;
		/**
		 * 在选定的课程下添加新的主题
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.DOMAIN_TOPIC_TABLE+"(TermName,ClassName,ClassID) values(?,?,?);";
			String sql_queryClassID="select * from "+Config.DOMAIN_TABLE+" where ClassName=?";
			List<Object> params_queryClassID=new ArrayList<Object>();
			params_queryClassID.add(ClassName);
			List<Object> params=new ArrayList<Object>();
			params.add(TopicName);
			params.add(ClassName);
			try{
				List<Map<String, Object>> results_queryClassID=mysql.returnMultipleResult(sql_queryClassID,params_queryClassID);
				if(results_queryClassID.size()!=0){
					params.add(results_queryClassID.get(0).get("ClassID").toString());
					try{
						String exist="select * from "+Config.DOMAIN_TOPIC_TABLE+" where TermName=? and ClassName=?";
						List<Object> params_exist=new ArrayList<Object>();
						params_exist.add(TopicName);
						params_exist.add(ClassName);
						try{
							List<Map<String, Object>> results = mysql.returnMultipleResult(exist, params_exist);
							if(results.size()==0){
								result=mysql.addDeleteModify(sql, params);
							}
							else{
								return Response.status(200).entity(new success(TopicName+" 已经存在！")).build();
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
					return Response.status(401).entity(new error(ClassName+" 不存在~")).build();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(TopicName+" 创建成功~")).build();
			}else{
				return Response.status(401).entity(new error(TopicName+" 创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	@GET
	@Path("/getDomain")
	@ApiOperation(value = "获得所有领域信息", notes = "获得所有领域信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomain() {
		Response response = null;
		/**
		 * 读取domain，得到所有领域名
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TABLE;
		List<Object> params = new ArrayList<Object>();
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
	@Path("/getDomainTerm")
	@ApiOperation(value = "获得指定领域下的主题", notes = "获得指定领域下所有主题")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTerm(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName) {
		Response response = null;
		/**
		 * 根据指定领域，获得领域下所有主题
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
	@Path("/getDomainTermInfo")
	@ApiOperation(value = "获得指定领域下指定主题的信息", notes = "获得指定领域下指定主题的信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermInfo(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下的所有信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
		String sql_firstFacet = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetLayer='1'";
		String sql_secondFacet = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetLayer='2'";
		String sql_thirdFacet = "select * from " + Config.FACET_TABLE+" where ClassName=? and TermName=? and FacetLayer='3'";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add(TermName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			List<Map<String, Object>> results_firstFacet = mysql.returnMultipleResult(sql_firstFacet, params);
			List<Map<String, Object>> results_secondFacet = mysql.returnMultipleResult(sql_secondFacet, params);
			List<Map<String, Object>> results_thirdFacet = mysql.returnMultipleResult(sql_thirdFacet, params);
			if (results_firstFacet == null || results_secondFacet == null || results_thirdFacet == null) { // 查询不到结果
				results.get(0).put("FacetNum", 0);
				results.get(0).put("FirstLayerFacetNum", 0);
				results.get(0).put("SecondLayerFacetNum", 0);
				results.get(0).put("ThirdLayerFacetNum", 0);
			} else {
				results.get(0).put("FacetNum", results_firstFacet.size()+results_secondFacet.size()+results_thirdFacet.size());
				results.get(0).put("FirstLayerFacetNum", results_firstFacet.size());
				results.get(0).put("SecondLayerFacetNum", results_secondFacet.size());
				results.get(0).put("ThirdLayerFacetNum", results_thirdFacet.size());
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
	@Path("/updateTermName")
	@ApiOperation(value = "修改主题名", notes = "修改主题名")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response updateTermName(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "新主题名字", required = true) @QueryParam("NewTermName") String NewTermName) {
		/**
		 * 修改数据库中一门课程下某主题的名字
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_Topic="update "+Config.DOMAIN_TOPIC_TABLE+" set TermName=? where ClassName=? and TermName=?;";
			String sql_TopicRelation="update "+Config.DOMAIN_TOPIC_RELATION_TABLE+" set Parent=? where ClassName=? and Parent=?;";
			String sql_TopicRelation1="update "+Config.DOMAIN_TOPIC_RELATION_TABLE+" set Child=? where ClassName=? and Child=?;";
			String sql_Facet="update "+Config.FACET_TABLE+" set TermName=? where ClassName=? and TermName=?;";
			String sql_FacetRelation="update "+Config.FACET_RELATION_TABLE+" set TermName=? where ClassName=? and TermName=?;";
			String sql_Dependence="update "+Config.DEPENDENCY+" set Start=? where ClassName=? and Start=?;";
			String sql_Dependence1="update "+Config.DEPENDENCY+" set End=? where ClassName=? and End=?;";
			String sql_AssmbleFragment="update "+Config.ASSEMBLE_FRAGMENT_TABLE+" set TermName=? where ClassName=? and TermName=?;";
//			String sql_AssmbleText="update "+Config.ASSEMBLE_TEXT_TABLE+" set TermName=? where ClassName=? and TermName=?;";
//			String sql_AssembleImage="update "+Config.ASSEMBLE_IMAGE_TABLE+" set TermName=? where ClassName=? and TermName=?;";
			List<Object> params=new ArrayList<Object>();
			params.add(NewTermName);
			params.add(ClassName);
			params.add(TermName);
			try{
				String exist="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
				List<Object> params_Query=new ArrayList<Object>();
				params_Query.add(ClassName);
				params_Query.add(NewTermName);
				try{
					List<Map<String, Object>> results = mysql.returnMultipleResult(exist, params_Query);
					if(results.size()==0){
						result=mysql.addDeleteModify(sql_Topic, params);
						try{
							mysql.addDeleteModify(sql_TopicRelation, params);
							mysql.addDeleteModify(sql_TopicRelation1, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							mysql.addDeleteModify(sql_Facet, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							mysql.addDeleteModify(sql_FacetRelation, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							mysql.addDeleteModify(sql_Dependence, params);
							mysql.addDeleteModify(sql_Dependence1, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							mysql.addDeleteModify(sql_AssmbleFragment, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						
/*						try{
							mysql.addDeleteModify(sql_AssmbleText, params);
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							mysql.addDeleteModify(sql_AssembleImage, params);
						}catch(Exception e){
							e.printStackTrace();
						}*/
					}
					else{
						return Response.status(200).entity(new success(NewTermName+" 已经存在！")).build();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success(TermName+"改为"+NewTermName+" 修改成功~")).build();
			}else{
				return Response.status(401).entity(new error(TermName+"改为"+NewTermName+" 修改失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
	@GET
	@Path("/deleteTermName")
	@ApiOperation(value = "删除某主题", notes = "删除某主题")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteTermName(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
		/**
		 * 删除数据库中一门课程下某个主题
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_Topic="delete from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?;";
			String sql_TopicRelation="delete from "+Config.DOMAIN_TOPIC_RELATION_TABLE+" where ClassName=? and (Parent=? or Child=?);";
			String sql_Facet="delete from "+Config.FACET_TABLE+" where ClassName=? and TermName=?;";
			String sql_FacetRelation="delete from "+Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=?;";
			String sql_Dependence="delete from "+Config.DEPENDENCY+" where ClassName=? and (Start=? or End=?);";
			String sql_AssembleFragment="delete from "+Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=?;";
//			String sql_AssembleText="delete from "+Config.ASSEMBLE_TEXT_TABLE+" where ClassName=? and TermName=?;";
//			String sql_AssembleImage="delete from "+Config.ASSEMBLE_IMAGE_TABLE+" where ClassName=? and TermName=?;";
			List<Object> params2=new ArrayList<Object>();
			List<Object> params3=new ArrayList<Object>();
			params2.add(ClassName);
			params2.add(TermName);
			params3.add(ClassName);
			params3.add(TermName);
			params3.add(TermName);
			try{
						result=mysql.addDeleteModify(sql_Topic, params2);
						if(result){
							try{
								mysql.addDeleteModify(sql_TopicRelation, params3);
								mysql.addDeleteModify(sql_Facet, params2);
								mysql.addDeleteModify(sql_FacetRelation, params2);
								mysql.addDeleteModify(sql_Dependence, params3);
								mysql.addDeleteModify(sql_AssembleFragment, params2);
//								mysql.addDeleteModify(sql_AssembleImage, params2);
//								mysql.addDeleteModify(sql_AssmbleText, params2);
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
				return Response.status(200).entity(new success(TermName+" 删除成功~")).build();
			}else{
				return Response.status(401).entity(new error(TermName+" 删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}

	


}
