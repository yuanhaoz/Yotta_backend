package dependency;

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

import dependency.bean.Dependency;
import dependency.ranktext.RankText;
import dependency.ranktext.Term;
import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;
import app.success;

/**  
 * 获取认知关系的API
 * 1. 
 * 2. 
 * 3. 
 *  
 * @author 郑元浩 
 * @date 2016年12月5日
 */

@Path("/DependencyAPI")
@Api(value = "DependencyAPI")
public class DependencyAPI {

	public static void main(String[] args) {
		Response response = getDependencyByDomain("数据结构");
		Log.log(response.getEntity());
	}
	
	
	@GET
	@Path("/getDependencyByDomain")
	@ApiOperation(value = "获得某个领域的认知关系", notes = "输入领域名，获得某个领域的认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDependencyByDomain(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
//		List<Dependency> dependencyList = new ArrayList<Dependency>();
		
		/**
		 * 读取dependency，获得认知关系
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DEPENDENCY + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size() == 0) { // 没有获取到主题间的认知关系
				generateDependenceByClassName(className); // 生成主题间的认知关系
				results = mysql.returnMultipleResult(sql, params);
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
	@Path("/getDependencyNum")
	@ApiOperation(value = "获得某个领域的认知关系", notes = "输入领域名，获得某个领域的认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDependencyNum(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
//		List<Dependency> dependencyList = new ArrayList<Dependency>();
		
		/**
		 * 读取dependency，获得认知关系
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DEPENDENCY + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			Map<String, Object> res=new HashMap<String, Object>();
			res.put("ClassName", className);
			res.put("DependenceNum", results.size());
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
	@Path("/createDependence")
	@ApiOperation(value = "创建一个认知关系", notes = "在选定的课程下创建一个认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createDependence(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "StartName", required = true) @QueryParam("StartName") String StartName,@ApiParam(value = "EndName", required = true) @QueryParam("EndName") String EndName) {
//		Response response = null;
		/**
		 * 在选定的课程下创建一个认知关系
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.DEPENDENCY+"(ClassName,Start,StartID,End,EndID) values(?,?,?,?,?);";
			String sql_queryTermID="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			String sql_queryDependency="select * from "+Config.DEPENDENCY+" where ClassName=? and Start=? and End=?";
			List<Object> params_queryTermID1=new ArrayList<Object>();
			List<Object> params_queryTermID2=new ArrayList<Object>();
			params_queryTermID1.add(ClassName);
			params_queryTermID1.add(StartName);
			params_queryTermID2.add(ClassName);
			params_queryTermID2.add(EndName);
			List<Object> params_queryDependency=new ArrayList<Object>();
			params_queryDependency.add(ClassName);
			params_queryDependency.add(StartName);
			params_queryDependency.add(EndName);
			List<Object> params=new ArrayList<Object>();
			params.add(ClassName);
			try{
				List<Map<String, Object>> results_queryDepencency=mysql.returnMultipleResult(sql_queryDependency, params_queryDependency);
				if(results_queryDepencency.size()==0){
					try{
				List<Map<String, Object>> results_queryTermID1=mysql.returnMultipleResult(sql_queryTermID,params_queryTermID1);
				List<Map<String, Object>> results_queryTermID2=mysql.returnMultipleResult(sql_queryTermID,params_queryTermID2);
				if(!StartName.equals(EndName)){
					params.add(StartName);
					params.add(results_queryTermID1.get(0).get("TermID").toString());
					params.add(EndName);
					params.add(results_queryTermID2.get(0).get("TermID").toString());
					try{
						result=mysql.addDeleteModify(sql, params);					
					}
				catch(Exception e){
					e.printStackTrace();
				}
				}
				else{
					return Response.status(200).entity(new success("主题不能重复~")).build();
				}
				}catch(Exception e){
					e.printStackTrace();
				}
				}
				else{return Response.status(200).entity(new success("认知关系已存在")).build();}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("认知关系创建成功~")).build();
			}else{
				return Response.status(401).entity(new error("认知关系创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
	@GET
	@Path("/deleteDependence")
	@ApiOperation(value = "删除一个认知关系", notes = "在选定的课程下删除一个认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteDependence(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "StartID", required = true) @QueryParam("StartID") String StartID,@ApiParam(value = "EndID", required = true) @QueryParam("EndID") String EndID) {
//		Response response = null;
		/**
		 * 在选定的课程下删除一个认知关系
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="delete from "+Config.DEPENDENCY+" where ClassName=? and StartID=? and EndID=?";
			List<Object> params=new ArrayList<Object>();
			params.add(ClassName);
			params.add(StartID);
			params.add(EndID);
			try{
				result=mysql.addDeleteModify(sql, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("认知关系删除成功~")).build();
			}else{
				return Response.status(401).entity(new error("认知关系删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
	
	@GET
	@Path("/getDependenceByKeyword")
	@ApiOperation(value = "根据关键词查询认知关系", notes = "根据关键词查询认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDependenceByKeyword(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "关键词", required = true) @QueryParam("Keyword") String Keyword) {
		Response response = null;
		/**
		 * 指定领域，根据关键词查询认知关系
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DEPENDENCY+" where ClassName=? and (Start like ? or End like ?)";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		params.add("%"+Keyword+"%");
		params.add("%"+Keyword+"%");
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
	@Path("/generateDependenceByClassName")
	@ApiOperation(value = "根据领域名生成认知关系", notes = "根据领域名生成认知关系")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response generateDependenceByClassName(
			@ApiParam(value = "农业史", required = true) @QueryParam("ClassName") String ClassName) {
		Response response = null;
		
		List<Term> termList = new ArrayList<Term>();
		/**
		 * 根据指定领域，查询主题表，获得领域下所有主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE +" where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Term term = new Term();
				term.setTermID(Integer.parseInt(results.get(i).get("TermID").toString()));
				term.setTermName(results.get(i).get("TermName").toString());
				termList.add(term);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		/**
		 * 根据指定领域及主题，查询碎片表，获得主题的内容信息
		 */
		mysqlUtils mysqlAssemble = new mysqlUtils();
		String sqlAssemble = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE +" where TermID=? and TermName=? and ClassName=?";
		try {
			for (int i = 0; i < termList.size(); i++) {
				Term term = termList.get(i);
				List<Object> paramsAssemble = new ArrayList<Object>();
				paramsAssemble.add(term.getTermID());
				paramsAssemble.add(term.getTermName());
				paramsAssemble.add(ClassName);
				List<Map<String, Object>> results = mysqlAssemble.returnMultipleResult(sqlAssemble, paramsAssemble);
				StringBuffer termText = new StringBuffer();
				for (int j = 0; j < results.size(); j++) {
					termText.append(results.get(j).get("FragmentContent").toString());
				}
				term.setTermText(termText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysqlAssemble.closeconnection();
		}
		
		/**
		 * 根据主题内容，调用算法得到主题认知关系
		 */
		RankText rankText = new RankText();
		List<Dependency> dependencies = rankText.rankText(termList, ClassName, Config.DEPENDENCEMAX);
		/**
		 * 指定领域，存储主题间的认知关系
		 */
		mysqlUtils mysqlDependency = new mysqlUtils();
		String sqlDependency = "insert into " + Config.DEPENDENCY + "(ClassName,Start,StartID,End,EndID,Confidence) values(?,?,?,?,?,?);";
		try {
			for (int i = 0; i < dependencies.size(); i++) {
				Dependency dependency = dependencies.get(i);
				List<Object> paramsDependency = new ArrayList<Object>();
				paramsDependency.add(ClassName);
				paramsDependency.add(dependency.getStart());
				paramsDependency.add(dependency.getStartID());
				paramsDependency.add(dependency.getEnd());
				paramsDependency.add(dependency.getEndID());
				paramsDependency.add(dependency.getConfidence());
				boolean success = mysqlDependency.addDeleteModify(sqlDependency, paramsDependency);
				response = Response.status(200).entity(success).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysqlDependency.closeconnection();
		}
		return response;
	}

}
