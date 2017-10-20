package spider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Date;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import org.apache.jena.sparql.function.library.e;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import domainTopic.DomainTopicOldDAO;
import spider.bean.Count;
import spider.bean.Image;
import spider.bean.ImageResult;
import spider.bean.Text;
import spider.bean.TextResult;
import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;
import app.success;



/**  
 * 获取主题碎片化知识的API
 * 1. 按照课程获取所有文本/图片
 * 2. 按照主题获取所有文本/图片（多个主题调用多次，任若清）
 * 3. 按照主题获取所有文本/图片（多个主题）
 * 
 * 4. 按照课程获取所有文本/图片数量（所有主题，任若清）
 * 5. 按照主题获取所有文本/图片数量（多个主题调用多次，任若清）
 * 6. 按照主题获取所有文本/图片数量（多个主题，任若清）
 * 
 * 7. 按照课程分页获取所有文本/图片（前端实现）
 * 8. 按照主题分页获取所有文本/图片
 *  
 * @author 郑元浩 
 * @date 2016年12月3日
 */

@Path("/SpiderAPI")
@Api(value = "SpiderAPI")
public class SpiderAPI {

	public static void main(String[] args) {
		Response response = getTextByTopic("数据结构", "抽象资料型别");
		//		Log.log(response.getEntity());

		response = getCountByDomain("数据结构");
		Log.log(response.getEntity());

		List<String> list = new ArrayList<String>();
		list.add("抽象资料型别");
		list.add("八叉树");
		list.add("队列");
		response = getTextByTopics("数据结构", list);
		//		Log.log(response.getEntity());

		List<String> listImage = new ArrayList<String>();
		listImage.add("抽象资料型别");
		listImage.add("八叉树");
		listImage.add("队列");
		response = getImageByTopics("数据结构", listImage);
		//		Log.log(response.getEntity());

		List<String> list2 = new ArrayList<String>();
		list2.add("抽象资料型别");
		list2.add("八叉树");
		list2.add("队列");
		response = getCountByTopics("数据结构", list2);
		Log.log(response.getEntity());
	}


	@GET
	@Path("/getTextByDomain")
	@ApiOperation(value = "获得某门课程的文本信息", notes = "输入领域名，获得某门课程的文本信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByDomain(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
		List<Text> textList = new ArrayList<Text>();

		/**
		 * 读取spider_text，获得知识点的文本碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				int FragmentID = Integer.parseInt(map.get("FragmentID").toString());
				String FragmentContent = map.get("FragmentContent").toString();
				String FragmentUrl = map.get("FragmentUrl").toString();
				String FragmentPostTime = map.get("FragmentPostTime").toString();
				String FragmentScratchTime = map.get("FragmentScratchTime").toString();
				int TermID = Integer.parseInt(map.get("TermID").toString());
				String TermName = map.get("TermName").toString();
				String ClassName = map.get("ClassName").toString();
				Text text = new Text(FragmentID, FragmentContent, FragmentUrl, FragmentPostTime, FragmentScratchTime, TermID, TermName, ClassName);
				textList.add(text);
			}
			response = Response.status(200).entity(textList).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}

		return response;
	}


	@GET
	@Path("/getTextByTopic")
	@ApiOperation(value = "获得知识主题的文本信息", notes = "输入领域名和知识主题，获得知识主题的文本信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByTopic(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermName") String topicName) {
		Response response = null;
		List<Text> textList = new ArrayList<Text>();

		/**
		 * 读取spider_text，获得知识点的文本碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				int FragmentID = Integer.parseInt(map.get("FragmentID").toString());
				String FragmentContent = map.get("FragmentContent").toString();
				String FragmentUrl = map.get("FragmentUrl").toString();
				String FragmentPostTime = map.get("FragmentPostTime").toString();
				String FragmentScratchTime = map.get("FragmentScratchTime").toString();
				int TermID = Integer.parseInt(map.get("TermID").toString());
				String TermName = map.get("TermName").toString();
				String ClassName = map.get("ClassName").toString();
				Text text = new Text(FragmentID, FragmentContent, FragmentUrl, FragmentPostTime, FragmentScratchTime, TermID, TermName, ClassName);
				textList.add(text);
			}
			response = Response.status(200).entity(textList).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}

	@GET
	@Path("/getTextByTopics")
	@ApiOperation(value = "获得多个知识主题的文本信息", notes = "输入领域名和多个知识主题，获得多个知识主题的文本信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByTopics(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermNameList") List<String> topicNameList) {
		Response response = null;
		List<Text> textList = new ArrayList<Text>();

		/**
		 * 循环所有主题
		 */
		for (int i = 0; i < topicNameList.size(); i++) {

			/**
			 * 读取spider_text，获得知识点的文本碎片
			 */
			String topicName = topicNameList.get(i);
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				for (int j = 0; j < results.size(); j++) {
					Map<String, Object> map = results.get(j);
					int FragmentID = Integer.parseInt(map.get("FragmentID").toString());
					String FragmentContent = map.get("FragmentContent").toString();
					String FragmentUrl = map.get("FragmentUrl").toString();
					String FragmentPostTime = map.get("FragmentPostTime").toString();
					String FragmentScratchTime = map.get("FragmentScratchTime").toString();
					int TermID = Integer.parseInt(map.get("TermID").toString());
					String TermName = map.get("TermName").toString();
					String ClassName = map.get("ClassName").toString();
					Text text = new Text(FragmentID, FragmentContent, FragmentUrl, FragmentPostTime, FragmentScratchTime, TermID, TermName, ClassName);
					textList.add(text);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysql.closeconnection();
			}
		}
		response = Response.status(200).entity(textList).build();

		return response;
	}
	
	@GET
	@Path("/getTextByTopicArray")
	@ApiOperation(value = "获得主题数组", notes = "输入领域名和知识主题数组，获得主题数组的文本信息集合")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败",response = String.class),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByTopicArray(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") String className,
			@DefaultValue("树状数组,图论术语") @ApiParam(value = "主题名字符串", required = true) @QueryParam("topicNames")
			String topicNames) {
		Response response = null;
		List<Text> textList = new ArrayList<Text>();
		String[] topicNameArray = topicNames.split(",");

		/**
		 * 循环所有主题
		 */
		for (int i = 0; i < topicNameArray.length; i++) {

			/**
			 * 读取spider_text，获得知识点的文本碎片
			 */
			String topicName = topicNameArray[i];
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				for (int j = 0; j < results.size(); j++) {
					Map<String, Object> map = results.get(j);
					int FragmentID = Integer.parseInt(map.get("FragmentID").toString());
					String FragmentContent = map.get("FragmentContent").toString();
					String FragmentUrl = map.get("FragmentUrl").toString();
					String FragmentPostTime = map.get("FragmentPostTime").toString();
					String FragmentScratchTime = map.get("FragmentScratchTime").toString();
					int TermID = Integer.parseInt(map.get("TermID").toString());
					String TermName = map.get("TermName").toString();
					String ClassName = map.get("ClassName").toString();
					Text text = new Text(FragmentID, FragmentContent, FragmentUrl, FragmentPostTime, FragmentScratchTime, TermID, TermName, ClassName);
					textList.add(text);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysql.closeconnection();
			}
		}
		response = Response.status(200).entity(textList).build();

		return response;
	}

	@GET
	@Path("/getTextByDomainAndPages")
	@ApiOperation(value = "分页获取领域下的文本碎片信息", notes = "输入某门课程、每页展示的碎片数目和页数，"
			+ "得到该知识点下的所有文本碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询结果为空"),
			@ApiResponse(code = 402, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "返回所有分面及碎片信息", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByDomainAndPages(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") 
			String className,
			@DefaultValue("1") @ApiParam(value = "第几页", required = false) @QueryParam("page") 
			int page,
			@DefaultValue("5") @ApiParam(value = "每页返回的数量", required = false) @QueryParam("pagesize") 
			int pagesize){

		Response response = null;

		/**
		 * 返回知识点下面的碎片信息
		 */
		TextResult fragmentResult = new TextResult();
		ArrayList<Text> fragmentList = new ArrayList<Text>();
		ArrayList<Text> fragmentListPage = new ArrayList<Text>();

		/**
		 * 读取文本碎片表格，得到分面信息集合
		 */
		String textSql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);

		mysqlUtils mysql = new mysqlUtils();
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();

		try {
			results = mysql.returnMultipleResult(textSql, params);
			for(int i = 0; i < results.size(); i++){
				Map<String, Object> map = results.get(i);
				int id = Integer.parseInt(map.get("FragmentID").toString());
				String content = map.get("FragmentContent").toString();
				String url = map.get("FragmentUrl").toString();
				String postTime = map.get("FragmentPostTime").toString();
				String scratchTime = map.get("FragmentScratchTime").toString();
				int termID = Integer.parseInt(map.get("TermID").toString());
				String termName = map.get("TermName").toString();
				Text fragment = new Text(id, content, url, postTime, scratchTime, termID, termName, className);
				fragmentList.add(fragment);
			}

			/**
			 * 按照页面大小展示第几页的所有信息
			 */
			int totalFragment = fragmentList.size(); // 碎片总数
			if(totalFragment != 0){
				/**
				 * 返回的结果不为空
				 */
				fragmentResult.setTotalFragment(totalFragment);
				fragmentResult.setPage(page); // 第几页
				fragmentResult.setPagesize(pagesize); // 页面大小
				int remainder = totalFragment % pagesize; // 用于判断
				int totalPage = totalFragment / pagesize;
				if(remainder != 0){ // 除不尽，页面总数加1
					totalPage += 1;
				}
				fragmentResult.setTotalPage(totalPage);  // 页面总数

				// 设置每页展示的碎片信息
				int begin = (page - 1) * pagesize;
				int end = page * pagesize;
				if(totalFragment < end){ // 判断最后一页的情况
					end = totalFragment;
				}
				for(int i = begin; i < end; i++){
					fragmentListPage.add(fragmentList.get(i));
				}
				// 设置返回的该页的碎片信息
				fragmentResult.setTextList(fragmentListPage);

				response = Response.status(200).entity(fragmentResult).build();
			} else {
				response = Response.status(401).entity(new error("MySql数据库  查询结果为空")).build();
			}


		} catch (Exception e) {
			response = Response.status(402).entity(new error("MySql数据库  查询失败")).build();
		} finally {
			mysql.closeconnection();
		}

		return response;

	}


	@GET
	@Path("/getTextByTopicAndPages")
	@ApiOperation(value = "分页获取知识点下的文本碎片信息", notes = "输入某门课程、知识点、每页展示的碎片数目和页数，"
			+ "得到该知识点下的所有文本碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询结果为空"),
			@ApiResponse(code = 402, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "返回所有分面及碎片信息", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getTextByTopicAndPages(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") 
			String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("termName") 
			String termName,
			@DefaultValue("1") @ApiParam(value = "第几页", required = false) @QueryParam("page") 
			int page,
			@DefaultValue("5") @ApiParam(value = "每页返回的数量", required = false) @QueryParam("pagesize") 
			int pagesize){

		Response response = null;

		/**
		 * 返回知识点下面的碎片信息
		 */
		TextResult fragmentResult = new TextResult();
		ArrayList<Text> fragmentList = new ArrayList<Text>();
		ArrayList<Text> fragmentListPage = new ArrayList<Text>();

		/**
		 * 读取文本碎片表格，得到分面信息集合
		 */
		String textSql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(termName);

		mysqlUtils mysql = new mysqlUtils();
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();

		try {
			results = mysql.returnMultipleResult(textSql, params);
			for(int i = 0; i < results.size(); i++){
				Map<String, Object> map = results.get(i);
				int id = Integer.parseInt(map.get("FragmentID").toString());
				String content = map.get("FragmentContent").toString();
				String url = map.get("FragmentUrl").toString();
				String postTime = map.get("FragmentPostTime").toString();
				String scratchTime = map.get("FragmentScratchTime").toString();
				int termID = Integer.parseInt(map.get("TermID").toString());
				Text fragment = new Text(id, content, url, postTime, scratchTime, termID, termName, className);
				fragmentList.add(fragment);
			}

			/**
			 * 按照页面大小展示第几页的所有信息
			 */
			int totalFragment = fragmentList.size(); // 碎片总数
			if(totalFragment != 0){
				/**
				 * 返回的结果不为空
				 */
				fragmentResult.setTotalFragment(totalFragment);
				fragmentResult.setPage(page); // 第几页
				fragmentResult.setPagesize(pagesize); // 页面大小
				int remainder = totalFragment % pagesize; // 用于判断
				int totalPage = totalFragment / pagesize;
				if(remainder != 0){ // 除不尽，页面总数加1
					totalPage += 1;
				}
				fragmentResult.setTotalPage(totalPage);  // 页面总数

				// 设置每页展示的碎片信息
				int begin = (page - 1) * pagesize;
				int end = page * pagesize;
				if(totalFragment < end){ // 判断最后一页的情况
					end = totalFragment;
				}
				for(int i = begin; i < end; i++){
					fragmentListPage.add(fragmentList.get(i));
				}
				// 设置返回的该页的碎片信息
				fragmentResult.setTextList(fragmentListPage);

				response = Response.status(200).entity(fragmentResult).build();
			} else {
				response = Response.status(401).entity(new error("MySql数据库  查询结果为空")).build();
			}


		} catch (Exception e) {
			response = Response.status(402).entity(new error("MySql数据库  查询失败")).build();
		} finally {
			mysql.closeconnection();
		}

		return response;

	}


	@GET
	@Path("/getImageByDomain")
	@ApiOperation(value = "获得某门课程的图片信息", notes = "输入领域名，获得某门课程的图片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByDomain(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
		List<Image> imageList = new ArrayList<Image>();

		/**
		 * 读取spider_image，获得知识点的图片碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				int ImageID = Integer.parseInt(map.get("ImageID").toString());
				String ImageUrl = map.get("ImageUrl").toString();
				int ImageWidth = Integer.parseInt(map.get("ImageWidth").toString());
				int ImageHeight = Integer.parseInt(map.get("ImageHeight").toString());
				int TermID = Integer.parseInt(map.get("TermID").toString());
				String TermName = map.get("TermName").toString();
				String TermUrl = map.get("TermUrl").toString();
				String ClassName = map.get("ClassName").toString();
				String ImageScratchTime = map.get("ImageScratchTime").toString();
				Image image = new Image(ImageID, ImageUrl, ImageWidth, ImageHeight, TermID, TermName, TermUrl, ClassName, ImageScratchTime); 
				imageList.add(image);
			}
			response = Response.status(200).entity(imageList).build();
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
		List<Image> imageList = new ArrayList<Image>();

		/**
		 * 读取spider_image，获得知识点的图片碎片
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(topicName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> map = results.get(i);
				int ImageID = Integer.parseInt(map.get("ImageID").toString());
				String ImageUrl = map.get("ImageUrl").toString();
				int ImageWidth = Integer.parseInt(map.get("ImageWidth").toString());
				int ImageHeight = Integer.parseInt(map.get("ImageHeight").toString());
				int TermID = Integer.parseInt(map.get("TermID").toString());
				String TermName = map.get("TermName").toString();
				String TermUrl = map.get("TermUrl").toString();
				String ClassName = map.get("ClassName").toString();
				String ImageScratchTime = map.get("ImageScratchTime").toString();
				Image image = new Image(ImageID, ImageUrl, ImageWidth, ImageHeight, TermID, TermName, TermUrl, ClassName, ImageScratchTime); 
				imageList.add(image);
			}
			response = Response.status(200).entity(imageList).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}


	@GET
	@Path("/getImageByTopics")
	@ApiOperation(value = "获得多个知识主题的图片信息", notes = "输入领域名和多个知识主题，获得多个知识主题的图片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByTopics(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermNameList") List<String> topicNameList) {
		Response response = null;
		List<Image> imageList = new ArrayList<Image>();

		/**
		 * 循环所有主题
		 */
		for (int i = 0; i < topicNameList.size(); i++) {

			/**
			 * 读取spider_image，获得知识点的图片碎片
			 */
			String topicName = topicNameList.get(i);
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				for (int j = 0; j < results.size(); j++) {
					Map<String, Object> map = results.get(j);
					int ImageID = Integer.parseInt(map.get("ImageID").toString());
					String ImageUrl = map.get("ImageUrl").toString();
					int ImageWidth = Integer.parseInt(map.get("ImageWidth").toString());
					int ImageHeight = Integer.parseInt(map.get("ImageHeight").toString());
					int TermID = Integer.parseInt(map.get("TermID").toString());
					String TermName = map.get("TermName").toString();
					String TermUrl = map.get("TermUrl").toString();
					String ClassName = map.get("ClassName").toString();
					String ImageScratchTime = map.get("ImageScratchTime").toString();
					Image image = new Image(ImageID, ImageUrl, ImageWidth, ImageHeight, TermID, TermName, TermUrl, ClassName, ImageScratchTime); 
					imageList.add(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysql.closeconnection();
			}
		}
		response = Response.status(200).entity(imageList).build();

		return response;
	}
	
	@GET
	@Path("/getImageByTopicArray")
	@ApiOperation(value = "获得多个知识主题的图片信息", notes = "输入领域名和多个知识主题，获得多个知识主题的图片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByTopicArray(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("树状数组,图论术语") @ApiParam(value = "主题名数组", required = true) @QueryParam("topicNames") 
			String topicNames) {
		Response response = null;
		List<Image> imageList = new ArrayList<Image>();
		String[] topicNameList = topicNames.split(",");
		
		/**
		 * 循环所有主题
		 */
		for (int i = 0; i < topicNameList.length; i++) {

			/**
			 * 读取spider_image，获得知识点的图片碎片
			 */
			String topicName = topicNameList[i];
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				for (int j = 0; j < results.size(); j++) {
					Map<String, Object> map = results.get(j);
					int ImageID = Integer.parseInt(map.get("ImageID").toString());
					String ImageUrl = map.get("ImageUrl").toString();
					int ImageWidth = Integer.parseInt(map.get("ImageWidth").toString());
					int ImageHeight = Integer.parseInt(map.get("ImageHeight").toString());
					int TermID = Integer.parseInt(map.get("TermID").toString());
					String TermName = map.get("TermName").toString();
					String TermUrl = map.get("TermUrl").toString();
					String ClassName = map.get("ClassName").toString();
					String ImageScratchTime = map.get("ImageScratchTime").toString();
					Image image = new Image(ImageID, ImageUrl, ImageWidth, ImageHeight, TermID, TermName, TermUrl, ClassName, ImageScratchTime); 
					imageList.add(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysql.closeconnection();
			}
		}
		response = Response.status(200).entity(imageList).build();

		return response;
	}


	@GET
	@Path("/getImageByDomainAndPages")
	@ApiOperation(value = "分页获取领域下的图片碎片信息", notes = "输入某门课程、每页展示的碎片数目和页数，"
			+ "得到该知识点下的所有图片碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询结果为空"),
			@ApiResponse(code = 402, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "返回所有分面及碎片信息", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByDomainAndPages(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") 
			String className,
			@DefaultValue("1") @ApiParam(value = "第几页", required = false) @QueryParam("page") 
			int page,
			@DefaultValue("5") @ApiParam(value = "每页返回的数量", required = false) @QueryParam("pagesize") 
			int pagesize){

		Response response = null;

		/**
		 * 返回知识点下面的碎片信息
		 */
		ImageResult imageResult = new ImageResult();
		ArrayList<Image> imageList = new ArrayList<Image>();
		ArrayList<Image> imageListPage = new ArrayList<Image>();

		/**
		 * 读取图片碎片表格，得到分面信息集合
		 */
		String imageSql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);

		mysqlUtils mysql = new mysqlUtils();
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();

		try {
			results = mysql.returnMultipleResult(imageSql, params);
			for(int i = 0; i < results.size(); i++){
				Map<String, Object> map = results.get(i);
				int id = Integer.parseInt(map.get("ImageID").toString());
				String url = map.get("ImageUrl").toString();
				int width = Integer.parseInt(map.get("ImageWidth").toString());
				int height = Integer.parseInt(map.get("ImageHeight").toString());
				int termID = Integer.parseInt(map.get("TermID").toString());
				String termName = map.get("TermName").toString();
				String termUrl = map.get("TermUrl").toString();
				String scratchTime = map.get("ImageScratchTime").toString();
				Image image = new Image(id, url, width, height, termID, termName, termUrl, className, scratchTime);
				imageList.add(image);
			}

			/**
			 * 按照页面大小展示第几页的所有信息
			 */
			int totalFragment = imageList.size(); // 碎片总数
			if(totalFragment != 0){
				/**
				 * 返回的结果不为空
				 */
				imageResult.setTotalFragment(totalFragment);
				imageResult.setPage(page); // 第几页
				imageResult.setPagesize(pagesize); // 页面大小
				int remainder = totalFragment % pagesize; // 用于判断
				int totalPage = totalFragment / pagesize;
				if(remainder != 0){ // 除不尽，页面总数加1
					totalPage += 1;
				}
				imageResult.setTotalPage(totalPage);  // 页面总数

				// 设置每页展示的碎片信息
				int begin = (page - 1) * pagesize;
				int end = page * pagesize;
				if(totalFragment < end){ // 判断最后一页的情况
					end = totalFragment;
				}
				for(int i = begin; i < end; i++){
					imageListPage.add(imageList.get(i));
				}
				// 设置返回的该页的碎片信息
				imageResult.setImageList(imageListPage);

				response = Response.status(200).entity(imageResult).build();
			} else {
				response = Response.status(401).entity(new error("MySql数据库  查询结果为空")).build();
			}


		} catch (Exception e) {
			response = Response.status(402).entity(new error("MySql数据库  查询失败")).build();
		} finally {
			mysql.closeconnection();
		}

		return response;

	}




	@GET
	@Path("/getImageByTopicAndPages")
	@ApiOperation(value = "分页获取知识点下的图片碎片信息", notes = "输入某门课程、知识点、每页展示的碎片数目和页数，"
			+ "得到该知识点下的所有图片碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询结果为空"),
			@ApiResponse(code = 402, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "返回所有分面及碎片信息", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getImageByTopicAndPages(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") 
			String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("termName") 
			String termName,
			@DefaultValue("1") @ApiParam(value = "第几页", required = false) @QueryParam("page") 
			int page,
			@DefaultValue("5") @ApiParam(value = "每页返回的数量", required = false) @QueryParam("pagesize") 
			int pagesize){

		Response response = null;

		/**
		 * 返回知识点下面的碎片信息
		 */
		ImageResult imageResult = new ImageResult();
		ArrayList<Image> imageList = new ArrayList<Image>();
		ArrayList<Image> imageListPage = new ArrayList<Image>();

		/**
		 * 读取文本碎片表格，得到分面信息集合
		 */
		String imageSql = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(className);
		params.add(termName);

		mysqlUtils mysql = new mysqlUtils();
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();

		try {
			results = mysql.returnMultipleResult(imageSql, params);
			for(int i = 0; i < results.size(); i++){
				Map<String, Object> map = results.get(i);
				int id = Integer.parseInt(map.get("ImageID").toString());
				String url = map.get("ImageUrl").toString();
				int width = Integer.parseInt(map.get("ImageWidth").toString());
				int height = Integer.parseInt(map.get("ImageHeight").toString());
				int termID = Integer.parseInt(map.get("TermID").toString());
				String termUrl = map.get("TermUrl").toString();
				String scratchTime = map.get("ImageScratchTime").toString();
				Image image = new Image(id, url, width, height, termID, termName, termUrl, className, scratchTime);
				imageList.add(image);
			}

			/**
			 * 按照页面大小展示第几页的所有信息
			 */
			int totalFragment = imageList.size(); // 碎片总数
			if(totalFragment != 0){
				/**
				 * 返回的结果不为空
				 */
				imageResult.setTotalFragment(totalFragment);
				imageResult.setPage(page); // 第几页
				imageResult.setPagesize(pagesize); // 页面大小
				int remainder = totalFragment % pagesize; // 用于判断
				int totalPage = totalFragment / pagesize;
				if(remainder != 0){ // 除不尽，页面总数加1
					totalPage += 1;
				}
				imageResult.setTotalPage(totalPage);  // 页面总数

				// 设置每页展示的碎片信息
				int begin = (page - 1) * pagesize;
				int end = page * pagesize;
				if(totalFragment < end){ // 判断最后一页的情况
					end = totalFragment;
				}
				for(int i = begin; i < end; i++){
					imageListPage.add(imageList.get(i));
				}
				// 设置返回的该页的碎片信息
				imageResult.setImageList(imageListPage);

				response = Response.status(200).entity(imageResult).build();
			} else {
				response = Response.status(401).entity(new error("MySql数据库  查询结果为空")).build();
			}


		} catch (Exception e) {
			response = Response.status(402).entity(new error("MySql数据库  查询失败")).build();
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
			Log.log("text: " + textSize);
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
			Log.log("image: " + imageSize);
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
	@Path("/getCountByDomain2")
	@ApiOperation(value = "获得某门课程的文本和图片数量", notes = "输入领域名，获得某门课程的文本和图片数量")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getCountByDomain2(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className) {
		Response response = null;
		List<Count> countList = new ArrayList<Count>();
		int textSize = 0;
		int imageSize = 0;
		
		/**
		 * 统计每个主题的文本碎片数量
		 */
		List<String> topicList = DomainTopicOldDAO.getDomainTopicList(className);
		for (int i = 0; i < topicList.size(); i++) {
			String topicName = topicList.get(i);
			/**
			 * 读取spider_text，获得文本数量
			 */
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				textSize += results.size();
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
			String sqlImage = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
			List<Object> paramsImage = new ArrayList<Object>();
			paramsImage.add(className);
			paramsImage.add(topicName);
			try {
				List<Map<String, Object>> results = mysqlImage.returnMultipleResult(sqlImage, paramsImage);
				imageSize += results.size();
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysqlImage.closeconnection();
			}
		}
		
		countList.add(new Count("text", textSize));
		countList.add(new Count("image", imageSize));
		Log.log("text: " + textSize);
		Log.log("image: " + imageSize);
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
		String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
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
		String sqlImage = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
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
	@Path("/getCountByTopics")
	@ApiOperation(value = "获得某门课程下多个主题的文本和图片数量", notes = "输入领域名和主题，获得某门课程下多个主题的文本和图片数量")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getCountByTopics(
			@DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("ClassName") String className,
			@DefaultValue("抽象资料型别") @ApiParam(value = "主题名", required = true) @QueryParam("TermNameList") List<String> topicNameList) {
		Response response = null;
		List<Count> countList = new ArrayList<Count>();
		int textSize = 0;
		int imageSize = 0;

		/**
		 * 循环遍历每一个元素
		 */
		for (int i = 0; i < topicNameList.size(); i++) {
			String topicName = topicNameList.get(i);
			/**
			 * 读取spider_text，获得文本数量
			 */
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=? and TermName=?";
			List<Object> params = new ArrayList<Object>();
			params.add(className);
			params.add(topicName);
			try {
				List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
				textSize += results.size();
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
			String sqlImage = "select * from " + Config.SPIDER_IMAGE_TABLE + " where ClassName=? and TermName=?";
			List<Object> paramsImage = new ArrayList<Object>();
			paramsImage.add(className);
			paramsImage.add(topicName);
			try {
				List<Map<String, Object>> results = mysqlImage.returnMultipleResult(sqlImage, paramsImage);
				imageSize += results.size();
			} catch (Exception e) {
				e.printStackTrace();
				response = Response.status(401).entity(new error(e.toString())).build();
			} finally {
				mysqlImage.closeconnection();
			}

		}

		countList.add(new Count("text", textSize));
		countList.add(new Count("image", imageSize));
		Log.log("text : " + textSize);
		Log.log("image : " + imageSize);
		response = Response.status(200).entity(countList).build();

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
	@Path("/getDomainTermFragment")
	@ApiOperation(value = "获得指定领域下指定主题的碎片信息", notes = "获得指定领域下指定主题的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下的所有碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=?";
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
	@Path("/getDomainTermFacet1Fragment")
	@ApiOperation(value = "获得指定领域下指定主题一级分面的碎片信息", notes = "获得指定领域下指定主题一级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet1Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下一级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";

		
		String sql_facet2 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params_facet2 = new ArrayList<Object>();
		params_facet2.add(ClassName);
		params_facet2.add(TermName);
		params_facet2.add(FacetName);
		try {
			List<Map<String, Object>> results=new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> results_facet2 = mysql.returnMultipleResult(sql_facet2, params_facet2);
			List<Map<String, Object>> results_finalfacet =new ArrayList<Map<String, Object>>();
			Map<String, Object> facet1=new HashMap<String, Object>();
			facet1.put("ClassName", ClassName);
			facet1.put("TermName", TermName);
			facet1.put("FacetName", FacetName);
			facet1.put("FacetLayer", 1);
			results_finalfacet.add(facet1);
			for(int i=0;i<results_facet2.size();i++){
				Map<String, Object> facet2=new HashMap<String, Object>();
				facet2.put("ClassName", ClassName);
				facet2.put("TermName", TermName);
				facet2.put("FacetName", results_facet2.get(i).get("ChildFacet"));
				facet2.put("FacetLayer", 2);
				results_finalfacet.add(facet2);
				
				List<Object> params_facet3 = new ArrayList<Object>();
				params_facet3.add(ClassName);
				params_facet3.add(TermName);
				params_facet3.add(results_facet2.get(i).get("ChildFacet"));
				try{
					List<Map<String, Object>> results_facet3=mysql.returnMultipleResult(sql_facet3, params_facet3);
					for(int j=0;j<results_facet3.size();j++){
						Map<String, Object> facet3=new HashMap<String, Object>();
						facet3.put("ClassName", ClassName);
						facet3.put("TermName", TermName);
						facet3.put("FacetName", results_facet3.get(j).get("ChildFacet"));
						facet3.put("FacetLayer", 3);
						results_finalfacet.add(facet3);
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			for(int k=0;k<results_finalfacet.size();k++){
				List<Object> params_fragment = new ArrayList<Object>();
				params_fragment.add(results_finalfacet.get(k).get("ClassName"));
				params_fragment.add(results_finalfacet.get(k).get("TermName"));
				params_fragment.add(results_finalfacet.get(k).get("FacetName"));
				params_fragment.add(results_finalfacet.get(k).get("FacetLayer"));
				try{
					results.addAll(mysql.returnMultipleResult(sql, params_fragment));
					
				}catch(Exception e){
					e.printStackTrace();
				}
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
	@Path("/getDomainTermFacet2Fragment")
	@ApiOperation(value = "获得指定领域下指定主题二级分面的碎片信息", notes = "获得指定领域下指定主题二级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet2Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下二级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
		
		String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE+" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
		List<Object> params_facet3 = new ArrayList<Object>();
		params_facet3.add(ClassName);
		params_facet3.add(TermName);
		params_facet3.add(FacetName);
		try {
			List<Map<String, Object>> results=new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> results_facet3 = mysql.returnMultipleResult(sql_facet3, params_facet3);
			List<Map<String, Object>> results_finalfacet =new ArrayList<Map<String, Object>>();
			Map<String, Object> facet2=new HashMap<String, Object>();
			facet2.put("ClassName", ClassName);
			facet2.put("TermName", TermName);
			facet2.put("FacetName", FacetName);
			facet2.put("FacetLayer", 2);
			results_finalfacet.add(facet2);
			for(int i=0;i<results_facet3.size();i++){
				Map<String, Object> facet3=new HashMap<String, Object>();
				facet3.put("ClassName", ClassName);
				facet3.put("TermName", TermName);
				facet3.put("FacetName", results_facet3.get(i).get("ChildFacet"));
				facet3.put("FacetLayer", 3);
				results_finalfacet.add(facet3);
			}
			for(int k=0;k<results_finalfacet.size();k++){
				List<Object> params_fragment = new ArrayList<Object>();
				params_fragment.add(results_finalfacet.get(k).get("ClassName"));
				params_fragment.add(results_finalfacet.get(k).get("TermName"));
				params_fragment.add(results_finalfacet.get(k).get("FacetName"));
				params_fragment.add(results_finalfacet.get(k).get("FacetLayer"));
				try{
					results.addAll(mysql.returnMultipleResult(sql, params_fragment));
				}catch(Exception e){
					e.printStackTrace();
				}
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
	@Path("/getDomainTermFacet3Fragment")
	@ApiOperation(value = "获得指定领域下指定主题三级分面的碎片信息", notes = "获得指定领域下指定主题三级分面的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getDomainTermFacet3Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
		Response response = null;
		/**
		 * 根据指定领域和指定主题，获得该主题下三级分面的碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE+" where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
		List<Object> params_fragment = new ArrayList<Object>();
		params_fragment.add(ClassName);
		params_fragment.add(TermName);
		params_fragment.add(FacetName);
		params_fragment.add(3);
		try {
					List<Map<String, Object>> results=new ArrayList<Map<String, Object>>();
					results=mysql.returnMultipleResult(sql, params_fragment);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
/*	@GET
	@Path("/getUnaddFragment")
	@ApiOperation(value = "获得未挂接的碎片信息", notes = "获得未挂接的碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getUnaddFragment() {
		Response response = null;
		
		mysqlUtils mysql = new mysqlUtils();
		String sql_text = "select * from " + Config.UNADD_TEXT;
		String sql_image = "select * from " + Config.UNADD_IMAGE;
		List<Object> params_fragment = new ArrayList<Object>();
		try {
					List<Map<String, Object>> results=new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> results_text=mysql.returnMultipleResult(sql_text, params_fragment);
					List<Map<String, Object>> results_image=mysql.returnMultipleResult(sql_image, params_fragment);
					for(Map<String, Object> a:results_text){
						a.put("url", a.get("FragmentUrl"));
						a.put("content", a.get("FragmentContent"));
						a.put("id", a.get("FragmentID"));
						a.put("type", "text");
					}
					for(Map<String, Object> a:results_image){
						a.put("url", a.get("ImageUrl"));
						a.put("iurl", a.get("ImageAPI"));
						//a.put("content", a.get("ImageUrl")+" "+a.get("ImageWidth")+"*"+a.get("ImageHeight"));
						a.put("id", a.get("ImageID"));
						a.put("type", "image");
					}
					results.addAll(results_text);
					results.addAll(results_image);
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}*/
	
	@GET
	@Path("/createFragment")
	@ApiOperation(value = "创建碎片", notes = "创建碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createFragment(@ApiParam(value = "FragmentContent", required = true) @QueryParam("FragmentContent") String FragmentContent) {
//		Response response = null;
		/**
		 * 创建碎片
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.FRAGMENT+"(FragmentContent,FragmentScratchTime) values(?,?);";
			Date d=new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentContent);
			params.add(sdf.format(d));
			try{
				result=mysql.addDeleteModify(sql, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片创建成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	@GET
	@Path("/getFragment")
	@ApiOperation(value = "获得碎片信息", notes = "获得碎片信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getFragment() {
		Response response = null;
		/**
		 * 获得碎片信息
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql= "select * from " + Config.FRAGMENT;
		List<Object> params = new ArrayList<Object>();
		try {
					List<Map<String, Object>> results=mysql.returnMultipleResult(sql, params);
					
			response = Response.status(200).entity(results).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	
	
/*	@GET
	@Path("/createImageFragment")
	@ApiOperation(value = "创建图片碎片", notes = "创建图片碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createImageFragment(@ApiParam(value = "ImageUrl", required = true) @QueryParam("ImageUrl") String ImageUrl,@ApiParam(value = "ImageWidth", required = true) @QueryParam("ImageWidth") String ImageWidth,@ApiParam(value = "ImageHeight", required = true) @QueryParam("ImageHeight") String ImageHeight) {

		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.UNADD_IMAGE+"(ImageUrl,ImageWidth,ImageHeight,ImageScratchTime) values(?,?,?,?);";
			Date d=new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Object> params=new ArrayList<Object>();
			params.add(ImageUrl);
			params.add(ImageWidth);
			params.add(ImageHeight);
			params.add(sdf.format(d));
			try{
				result=mysql.addDeleteModify(sql, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片创建成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}*/
	
	
	
	@POST
	@Path("/createImageFragment")
	@ApiOperation(value = "插入图片", notes = "插入图片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes(MediaType.MULTIPART_FORM_DATA+";charset=" + "UTF-8")
	@Produces(MediaType.TEXT_PLAIN+ ";charset=" + "UTF-8")
	public static Response createImageFragment(@FormDataParam("imageContent") FormDataContentDisposition disposition,@FormDataParam("imageContent") InputStream imageContent) {

		Response response = null;
		mysqlUtils mysql = new mysqlUtils();
		
		Date d=new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		
//		String sqlFragment="select * from "+Config.UNADD_IMAGE+" where ImageUrl=?";
		String sqlAdd = "insert into " +Config.UNADD_IMAGE+ "(ImageUrl,ImageContent,ImageAPI, ImageScratchTime) values (?, ?, ?,?)";
		String sqlImageID="select * from "+Config.UNADD_IMAGE+" where ImageUrl=?";
		String sqlApi="update "+Config.UNADD_IMAGE+" set ImageAPI=? where ImageUrl=?";
		List<Object> paramsAdd = new ArrayList<Object>();
		List<Object> paramsImageID = new ArrayList<Object>();
		List<Object> paramsApi = new ArrayList<Object>();
		paramsAdd.add("http://image.baidu.com/" + disposition.getFileName());
		paramsAdd.add(imageContent);
		paramsAdd.add("");
		paramsAdd.add(sdf.format(d));
		paramsImageID.add("http://image.baidu.com/" + disposition.getFileName());
//		paramsApi.add(e);
//		paramsApi.add("http://image.baidu.com/" + disposition.getFileName());
		List<Map<String, Object>> resultFragment = new ArrayList<Map<String, Object>>();

		try {
		resultFragment=mysql.returnMultipleResult(sqlImageID, paramsImageID);
		if(resultFragment.size()==0){
			try{
				mysql.addDeleteModify(sqlAdd, paramsAdd);
				try{
					List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
					result=mysql.returnMultipleResult(sqlImageID, paramsImageID);
					paramsApi.add(Config.IP2+"/" + Config.projectName + "/SpiderAPI/getUnaddImage?imageID="+result.get(0).get("ImageID"));
					paramsApi.add("http://image.baidu.com/" + disposition.getFileName());
					try{
						mysql.addDeleteModify(sqlApi, paramsApi);
						response = Response.status(200).entity(paramsApi.get(0)).build();
					}catch(Exception e2){
						e2.printStackTrace();
					}
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}catch(Exception e0){
				e0.printStackTrace();
			}
		}
		else{
			response = Response.status(200).entity(resultFragment.get(0).get("ImageAPI")).build();
		}
		} catch (Exception e) {
			e.printStackTrace();
		response = Response.status(402).entity(new error("MySql数据库  更新失败")).build();
		} finally {
		mysql.closeconnection();
		}

		 
		//System.out.println(response.getEntity());
		return response;
	
		
	}
	
	@GET
	@Path("/getUnaddImage")
	@ApiOperation(value = "读取图片数据表中数据到成API", notes = "输入图片ID，得到对应API")
	@ApiResponses(value = { @ApiResponse(code = 401, message = "MySql数据库  图片内容查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  图片数据表检查处理完成", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_OCTET_STREAM + ";charset=" + "UTF-8")
	public static Response getImage(@ApiParam(value = "图片ID", required = true) @QueryParam("imageID") int imageID) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			mysqlUtils mysql = new mysqlUtils();
			String sql = "select * from "+Config.UNADD_IMAGE+" where ImageID=?";
			List<Object> params = new ArrayList<Object>();
			params.add(imageID);
			try {
				result = mysql.returnMultipleResult(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mysql.closeconnection();
			}
			String imageUrl = (String) result.get(0).get("ImageUrl");
			String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
			Object imageContent = result.get(0).get("ImageContent");
			return Response.status(200).header("Content-disposition", "attachment; " + "filename=" + filename).entity(imageContent).build();
		} catch (Exception e) {
			return Response.status(402).entity(new error(e.toString())).build();
		}
	}
	
	
/*	@GET
	@Path("/createTextFragment")
	@ApiOperation(value = "创建文本碎片", notes = "创建文本碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response createTextFragment(@ApiParam(value = "FragmentContent", required = true) @QueryParam("FragmentContent") String FragmentContent,@ApiParam(value = "FragmentUrl", required = true) @QueryParam("FragmentUrl") String FragmentUrl) {
//		Response response = null;
		
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="insert into "+Config.UNADD_TEXT+"(FragmentContent,FragmentUrl,FragmentPostTime,FragmentScratchTime) values(?,?,?,?);";
			Date d=new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentContent);
			params.add(FragmentUrl);
			params.add(sdf.format(d));
			params.add(sdf.format(d));
			try{
				result=mysql.addDeleteModify(sql, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片创建成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片创建失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}*/
	
	@GET
	@Path("/addFacetFragment")
	@ApiOperation(value = "向分面添加碎片", notes = "向分面添加碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response addFacet1TextFragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "分面级数", required = true) @QueryParam("FacetLayer") String FacetLayer,@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
//		Response response = null;
		/**
		 * 向分面添加碎片
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_term="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			String sql_query="select * from "+Config.FRAGMENT+" where FragmentID=?";
			String sql_delete="delete from "+Config.FRAGMENT+" where FragmentID=?";
			String sql_add="insert into "+Config.ASSEMBLE_FRAGMENT_TABLE+"(FragmentContent,FragmentScratchTime,TermID,TermName,FacetName,FacetLayer,ClassName) values(?,?,?,?,?,?,?);";
			List<Object> params_term=new ArrayList<Object>();
			params_term.add(ClassName);
			params_term.add(TermName);
			List<Object> params_fragment=new ArrayList<Object>();
			params_fragment.add(FragmentID);
			try{
				List<Map<String, Object>> results_term=mysql.returnMultipleResult(sql_term, params_term);
				List<Map<String, Object>> fragmentinfo=mysql.returnMultipleResult(sql_query, params_fragment);
				List<Object> params_add=new ArrayList<Object>();
				params_add.add(fragmentinfo.get(0).get("FragmentContent"));
				params_add.add(fragmentinfo.get(0).get("FragmentScratchTime"));
				params_add.add(results_term.get(0).get("TermID"));
				params_add.add(TermName);
				params_add.add(FacetName);
				params_add.add(FacetLayer);
				params_add.add(ClassName);
				result=mysql.addDeleteModify(sql_add, params_add);
				if(result){
					try{
						mysql.addDeleteModify(sql_delete, params_fragment);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片添加成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片添加失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}
	
	
/*	@GET
	@Path("/addFacetImageFragment")
	@ApiOperation(value = "向分面添加图片碎片", notes = "向分面添加图片碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response addFacet1ImageFragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,@ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,@ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,@ApiParam(value = "分面级数", required = true) @QueryParam("FacetLayer") String FacetLayer,@ApiParam(value = "ImageID", required = true) @QueryParam("ImageID") String ImageID) {
//		Response response = null;
		
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql_term="select * from "+Config.DOMAIN_TOPIC_TABLE+" where ClassName=? and TermName=?";
			String sql_query="select * from "+Config.UNADD_IMAGE+" where ImageID=?";
			String sql_delete="delete from "+Config.UNADD_IMAGE+" where ImageID=?";
			String sql_add="insert into "+Config.ASSEMBLE_IMAGE_TABLE+"(ImageUrl,ImageWidth,ImageHeight,TermID,TermName,TermUrl,FacetLayer,FacetName,ClassName,ImageScratchTime) values(?,?,?,?,?,?,?,?,?,?);";
			List<Object> params_term=new ArrayList<Object>();
			params_term.add(ClassName);
			params_term.add(TermName);
			List<Object> params_image=new ArrayList<Object>();
			params_image.add(ImageID);
			try{
				List<Map<String, Object>> results_term=mysql.returnMultipleResult(sql_term, params_term);
				List<Map<String, Object>> imageinfo=mysql.returnMultipleResult(sql_query, params_image);
				List<Object> params_add=new ArrayList<Object>();
				params_add.add(imageinfo.get(0).get("ImageUrl"));
				params_add.add(imageinfo.get(0).get("ImageWidth"));
				params_add.add(imageinfo.get(0).get("ImageHeight"));
				params_add.add(results_term.get(0).get("TermID"));
				params_add.add(TermName);
				params_add.add(results_term.get(0).get("TermUrl"));
				params_add.add(FacetLayer);
				params_add.add(FacetName);
				params_add.add(ClassName);
				params_add.add(imageinfo.get(0).get("ImageScratchTime"));
				result=mysql.addDeleteModify(sql_add, params_add);
				if(result){
					try{
						mysql.addDeleteModify(sql_delete, params_image);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片添加成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片添加失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
		
	}*/
	
	
	@GET
	@Path("/deleteUnaddFragment")
	@ApiOperation(value = "删除未挂接的碎片", notes = "删除未挂接的碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteUnaddFragment(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
		/**
		 * 删除未挂接的碎片
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="delete from "+Config.FRAGMENT+" where FragmentID=?;";
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentID);
			try{
						result=mysql.addDeleteModify(sql, params);	
				}		
			catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片删除成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
	
}
	
	
/*	@GET
	@Path("/deleteUnaddText")
	@ApiOperation(value = "删除未挂接的文本", notes = "删除未挂接的文本")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteUnaddText(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
		
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="delete from "+Config.UNADD_TEXT+" where FragmentID=?;";
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentID);
			try{
						result=mysql.addDeleteModify(sql, params);	
				}		
			catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片删除成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
	
}*/
	
	
	
	@GET
	@Path("/deleteFragment")
	@ApiOperation(value = "删除碎片", notes = "删除碎片")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteFragment(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
		/**
		 * 删除碎片
		 */
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="delete from "+Config.ASSEMBLE_FRAGMENT_TABLE+" where FragmentID=?;";
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentID);
			try{
						result=mysql.addDeleteModify(sql, params);	
				}		
			catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片删除成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
	
}
	
	
/*	@GET
	@Path("/deleteText")
	@ApiOperation(value = "删除文本", notes = "删除文本")
	@ApiResponses(value = {
			@ApiResponse(code = 402, message = "数据库错误",response=error.class),
			@ApiResponse(code = 200, message = "正常返回结果", response =success.class)})
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response deleteText(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
		
		try{
			boolean result=false;
			mysqlUtils mysql=new mysqlUtils();
			String sql="delete from "+Config.ASSEMBLE_TEXT_TABLE+" where FragmentID=?;";
			List<Object> params=new ArrayList<Object>();
			params.add(FragmentID);
			try{
						result=mysql.addDeleteModify(sql, params);	
				}		
			catch(Exception e){
			e.printStackTrace();
		}
		finally {
			mysql.closeconnection();
		}
			if (result) {
				return Response.status(200).entity(new success("碎片删除成功~")).build();
			}else{
				return Response.status(401).entity(new error("碎片删除失败~")).build();
			}
	}catch(Exception e){
		return Response.status(402).entity(new error(e.toString())).build();
	}
	
}*/


	
	
}
