package test;


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

import utils.mysqlUtils;
import app.error;

@Path("/test")
@Api(value = "test")
public class test {

	public static void main(String[] args) {
//		System.out.println(get("1").getEntity().toString());
//		String conAll = "hello";
//		String con = "world";
//		conAll += "\n" + con;
//		System.out.println(conAll);
		search("电脑安全");
	}

	@GET
	@Path("/ceshi")
	@ApiOperation(value = "获取一门课程所有知识点", notes = "输入课程名称，获得该课程的所有知识点")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "正常返回结果", response = HashMap.class),
			@ApiResponse(code = 401, message = "错误", response = error.class)
	})
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response get(
			@DefaultValue("高等数学") @ApiParam(value = "课程名称", required = true) @QueryParam("domain") String domain) {
		
			if (domain.equals("1")) {
				return Response.status(401).entity(new error("状态信息。不能输出1")).build();

			}
			else
			{
				HashMap<String , String > HashMap=new HashMap<>();
				HashMap.put("自己的json", "自己的json");
				return Response.status(200).entity(HashMap).build();
			}

	}
	
	public static Response search(String domain) {
		Response response = null;
		/**
		 * 读取domain，得到所有领域名
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "SELECT DISTINCT Count(spider_text.FragmentID) as text_count FROM spider_text ,"
				+ "domain_topic WHERE domain_topic.TermName = spider_text.TermName AND "
				+ "spider_text.ClassName = domain_topic.ClassName AND spider_text.ClassName = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			System.out.println(results);
			response = Response.status(200)
					.entity(results)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	

	

}