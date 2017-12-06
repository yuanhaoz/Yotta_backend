package subject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.bean.Domain2;
import domainTopic.bean.DomainTopic;
import subject.bean.Subject;
import subject.bean.Subject2;
import utils.mysqlUtils;
import app.Config;
import app.error;

/**  
 * 学科   
 *  
 * @author 郑元浩 
 * @date 2017年12月4日 下午8:47:45 
 */
@Path("/SubjectAPI")
@Api(value = "SubjectAPI")
public class SubjectAPI {

	public static void main(String[] args) {
		
	}
	
	@GET
	@Path("/getSubject")
	@ApiOperation(value = "获得所有学科信息", notes = "获得所有学科信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getSubject() {
		Response response = null;
		List<Subject> subjects = new ArrayList<Subject>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SUBJECT_TABLE;
		List<Object> params = new ArrayList<Object>();
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Subject subject = new Subject();
				subject.setSubjectID(Integer.parseInt(results.get(i).get("SubjectID").toString()));
				subject.setSubjectName(results.get(i).get("SubjectName").toString());
				subject.setNote(results.get(i).get("Note").toString());
				subjects.add(subject);
			}
			response = Response.status(200).entity(subjects).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}
	
	@GET
	@Path("/getSubjectWithDomain")
	@ApiOperation(value = "获得学科和课程信息", notes = "获得学科和课程信息")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getSubjectWithDomain() {
		Response response = null;
		List<Subject2> subjects = new ArrayList<Subject2>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SUBJECT_TABLE;
		List<Object> params = new ArrayList<Object>();
		String sqlDomain = "select * from " + Config.DOMAIN_TABLE + " where SubjectName = ?";
		String sqlDomainTopic = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName = ?";
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Subject2 subject = new Subject2();
				subject.setSubjectID(Integer.parseInt(results.get(i).get("SubjectID").toString()));
				subject.setSubjectName(results.get(i).get("SubjectName").toString());
				subject.setNote(results.get(i).get("Note").toString());
				// 添加课程
				List<Domain2> domains = new ArrayList<Domain2>();
				List<Object> paramsDomain = new ArrayList<Object>();
				paramsDomain.add(results.get(i).get("SubjectName").toString());
				List<Map<String, Object>> resultsDomain = mysql.returnMultipleResult(sqlDomain, paramsDomain);
				for (int j = 0; j < resultsDomain.size(); j++) {
					Domain2 domain = new Domain2();
					domain.setClassID(Integer.parseInt(resultsDomain.get(j).get("ClassID").toString()));
					domain.setClassName(resultsDomain.get(j).get("ClassName").toString());
					// 添加主题
					List<DomainTopic> domainTopics = new ArrayList<DomainTopic>();
					List<Object> paramsDomainTopic = new ArrayList<Object>();
					paramsDomainTopic.add(resultsDomain.get(j).get("ClassName").toString());
					List<Map<String, Object>> resultsDomainTopic = mysql.returnMultipleResult(sqlDomainTopic, paramsDomainTopic);
					for (int k = 0; k < resultsDomainTopic.size(); k++) {
						DomainTopic domainTopic = new DomainTopic();
						domainTopic.setTermName(resultsDomainTopic.get(k).get("TermName").toString());
						domainTopic.setTermUrl(resultsDomainTopic.get(k).get("TermUrl").toString());
//						domainTopic.setTermLayer(Integer.parseInt(resultsDomainTopic.get(k).get("TermLayer").toString()));
						domainTopics.add(domainTopic);
					}
					domain.setDomainTopics(domainTopics);
					domains.add(domain);
				}
				subject.setDomains(domains);
				subjects.add(subject);
			}
			response = Response.status(200).entity(subjects).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = Response.status(401).entity(new error(e.toString())).build();
		} finally {
			mysql.closeconnection();
		}
		return response;
	}

}
