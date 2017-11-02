package login;

import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import utils.Log;
import utils.mysqlUtils;
import app.Config;

/**
 * 类说明
 * 
 * @author 郑元浩
 * @date 2017年11月2日 下午12:48:01
 */
@Path("/login")
public class LoginAPI {

	public static void main(String[] args) {
		login("rrq", "rrq", "111", "dadas", "dasd");
	}
	
	
	@POST
	@Path("/login")
	@ApiOperation(value = "用户登陆", notes = "用户登陆")
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.TEXT_PLAIN + ";charset=" + "UTF-8")
	public static Response login(
			@FormParam("name") String name, 
			@FormParam("passwd") String passwd, 
			@FormParam("ip") String ip,
			@FormParam("place") String place, 
			@FormParam("date") String date) {
		Response response = null;
		Log.log(name + "," + passwd);
		/**
		 * 根据指定领域，获得该领域下的所有主题信息
		 */
		mysqlUtils mysql = new mysqlUtils();
//		String sql = "select * from " + Config.USER_INFO + " where name=? and passwd=?; ";
		String sql = "select * from "+Config.USER_INFO+" where name=? and passwd=?;";
		List<Object> params = new ArrayList<Object>();
		params.add(name);
		params.add(passwd);
		
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size() == 1) {
				response = Response.status(200).entity(200).build();
				String sql2 = "insert into " + Config.USER_LOG
						+ " (name, passwd, ip, ipPlace, logDate) "
						+ " values(?,?,?,?,?)";
				List<Object> params2 = new ArrayList<Object>();
				params2.add(name);
				params2.add(passwd);
				params2.add(ip);
				params2.add(place);
				params2.add(date);
				mysql.addDeleteModify(sql2, params2);
			} else {
				response = Response.status(200).entity("xxxx faied").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity("登录失败").build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}

}
