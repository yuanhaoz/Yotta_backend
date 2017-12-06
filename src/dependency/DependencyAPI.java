package dependency;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.database.drivers.MySQLDriver;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import utils.Log;
import utils.mysqlUtils;
import app.Config;
import app.error;
import app.success;
import dependency.bean.Dependency;
import dependency.ranktext.RankText;
import dependency.ranktext.Term;



/**  
 * 知识关联挖掘：主题间认知关系
 * @author 郑元浩 
 */

@Path("/DependencyAPI")
@Api(value = "DependencyAPI")
public class DependencyAPI {

	public static void main(String[] args) {
//		Response response = getDependencyByDomain("数据结构");
//		Log.log(response.getEntity());
		
		Response response = getDomain();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> results = (List<Map<String, Object>>) response.getEntity();
		System.out.println(results);
		for (int i = 0; i < results.size(); i++) {
			String className = (String) results.get(i).get("ClassName");
			getGexfByClassName(className);
		}
		
//		String className = "数据结构";
//		String className = "计算机编程";
//		String className = "人工智能";
//		getGexfByClassName(className);
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
//			if (results.size() == 0) { // 没有获取到主题间的认知关系
//				generateDependenceByClassName(className); // 生成主题间的认知关系
			results = mysql.returnMultipleResult(sql, params);
//			}
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
	public static Response createDependence(
			@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,
			@ApiParam(value = "StartName", required = true) @QueryParam("StartName") String StartName,
			@ApiParam(value = "EndName", required = true) @QueryParam("EndName") String EndName) {
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
			
			// 将认知路径写到上下位关系中
			String sqlDomainTopicRelationQuery = "select * from "+Config.DOMAIN_TOPIC_RELATION_TABLE+" where ClassName=? and Child=?";
			List<Object> paramsDomainTopicRelationQuery=new ArrayList<Object>();
			paramsDomainTopicRelationQuery.add(ClassName);
			String sqlDomainTopicRelation="insert into "+Config.DOMAIN_TOPIC_RELATION_TABLE+"(Parent, Child, ClassName) values(?,?,?);";
			List<Object> paramsDomainTopicRelation=new ArrayList<Object>();
			
			try{
				List<Map<String, Object>> results_queryDepencency = mysql.returnMultipleResult(sql_queryDependency, params_queryDependency);
				if (results_queryDepencency.size() == 0) {
					try {
						List<Map<String, Object>> results_queryTermID1 = mysql.returnMultipleResult(sql_queryTermID, params_queryTermID1);
						List<Map<String, Object>> results_queryTermID2 = mysql.returnMultipleResult(sql_queryTermID, params_queryTermID2);
						if (!StartName.equals(EndName)) {
							params.add(StartName);
							params.add(results_queryTermID1.get(0).get("TermID").toString());
							params.add(EndName);
							params.add(results_queryTermID2.get(0).get("TermID").toString());
							
							// 将认知路径写到上下位关系中
							paramsDomainTopicRelation.add(StartName);
							paramsDomainTopicRelation.add(EndName);
							paramsDomainTopicRelation.add(ClassName);
							paramsDomainTopicRelationQuery.add(StartName);
							try {
								result = mysql.addDeleteModify(sql, params);
								result = mysql.addDeleteModify(sqlDomainTopicRelation, paramsDomainTopicRelation);
								List<Map<String, Object>> results_queryLayerRelation = mysql.returnMultipleResult(sqlDomainTopicRelationQuery, paramsDomainTopicRelationQuery);
								if (results_queryLayerRelation.size() == 0) {
									paramsDomainTopicRelation.clear();
									paramsDomainTopicRelation.add(ClassName);
									paramsDomainTopicRelation.add(StartName);
									paramsDomainTopicRelation.add(ClassName);
									result = mysql.addDeleteModify(sqlDomainTopicRelation, paramsDomainTopicRelation);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							return Response.status(200).entity(new success("主题不能重复~")).build();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					return Response.status(200).entity(new success("认知关系已存在")).build();
				}
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
	public static Response deleteDependence(
			@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,
			@ApiParam(value = "StartID", required = true) @QueryParam("StartID") String StartID,
			@ApiParam(value = "EndID", required = true) @QueryParam("EndID") String EndID) {
//		Response response = null;
		/**
		 * 在选定的课程下删除一个认知关系
		 */
		try{
			// 删除dependency中的一条认知关系
			boolean result = false;
			mysqlUtils mysql = new mysqlUtils();
			String sql = "delete from " + Config.DEPENDENCY + " where ClassName=? and StartID=? and EndID=?";
			List<Object> params = new ArrayList<Object>();
			params.add(ClassName);
			params.add(StartID);
			params.add(EndID);
			// 删除domain_topic_relation中的一条上下位关系
			String sqlTopic = "select * from " + Config.DEPENDENCY + " where ClassName=? and StartID=? and EndID=?";
			List<Object> paramsTopic = new ArrayList<Object>();
			paramsTopic.add(ClassName);
			paramsTopic.add(StartID);
			paramsTopic.add(EndID);
			String sqlTopicRelation = "delete from " + Config.DOMAIN_TOPIC_RELATION_TABLE + " where ClassName=? and Parent=? and Child=?";
			List<Object> paramsTopicRelation = new ArrayList<Object>();
			try{
				// 删除domain_topic_relation中的一条上下位关系
				List<Map<String, Object>> results = mysql.returnMultipleResult(sqlTopic, paramsTopic);
				paramsTopicRelation.add(ClassName);
				paramsTopicRelation.add(results.get(0).get("Start").toString());
				paramsTopicRelation.add(results.get(0).get("End").toString());
				result = mysql.addDeleteModify(sqlTopicRelation, paramsTopicRelation);
				// 删除dependency中的一条认知关系
				result = mysql.addDeleteModify(sql, params);
			} catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				mysql.closeconnection();
			}
			if (result) {
				return Response.status(200).entity(new success("认知关系删除成功~")).build();
			} else {
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
				System.out.println(dependency.toString());
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

	@GET
	@Path("/getGexfByClassName")
	@ApiOperation(value = "根据领域名返回gexf格式的社团关系数据", notes = "根据领域名返回gexf格式的社团关系数据")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "MySql数据库  查询失败"),
			@ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class) })
	@Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
	public static Response getGexfByClassName(
			@ApiParam(value = "数据结构", required = true) @QueryParam("ClassName") String ClassName
			) {
		Response response = null;
		new File(Config.GEXFPATH).mkdir();
		File gexfFile = new File(Config.GEXFPATH + "\\" + ClassName + ".gexf");
		if (gexfFile.exists()) {
			// 第二次之后直接调用本地gexf文件的内容，返回给前台
			try {
				String gexfContent = FileUtils.readFileToString(gexfFile);
				response = Response.status(200).entity(new success(gexfContent)).build();
			} catch (IOException e1) {
				response = Response.status(401).entity(new error("认知关系生成失败~" + e1)).build();
			}
		} else {
			// 第一次调用api，运行gephi java接口生成认知关系图数据。将其存储到本地，方便第二次以后调用直接读取文件内容。同时返回关系图数据。
			
			// Init a project - and therefore a workspace
	        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
	        pc.newProject();
	        Workspace workspace = pc.getCurrentWorkspace();

	        //Get models and controllers for this new workspace - will be useful later
	        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
	        PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
	        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
	        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
	        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
	        AppearanceModel appearanceModel = appearanceController.getModel();

	        //Import database
	        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
	        db.setDBName(Config.DBNAME);
	        db.setHost(Config.HOST);
	        db.setUsername(Config.USERNAME);
	        db.setPasswd(Config.PASSWD);
	        db.setSQLDriver(new MySQLDriver());
	        db.setPort(Config.PORT);
	        db.setNodeQuery("SELECT DISTINCT " +
	                "dt.TermID AS id, " +
	                "dt.TermName AS label " +
	                "FROM " +
	                "dependency AS dp , " +
	                "domain_topic AS dt " +
	                "WHERE " +
	                "dt.ClassName = '" + ClassName + "' AND " +
	                "(dt.TermName = dp.`Start` OR " +
	                "dt.TermName = dp.`End`)");
	        db.setEdgeQuery("SELECT dependency.StartID AS source, dependency.EndID AS target "
	        		+ "FROM dependency where ClassName='" + ClassName + "'");
	        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
	        Container container = importController.importDatabase(db, edgeListImporter);
	        container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
	        container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force UNDIRECTED

	        //Append imported data to GraphAPI
	        importController.process(container, new DefaultProcessor(), workspace);

	        //See if graph is well imported
	        DirectedGraph graph = graphModel.getDirectedGraph();
	        if (graph.getNodeCount() == 0 || graph.getEdgeCount() == 0) {
				return Response.status(402).entity(new error("节点数量为：" + graph.getNodeCount() + 
						"，边数量为：" + graph.getEdgeCount())).build();
			}
	        Log.log("Nodes: " + graph.getNodeCount());
	        Log.log("Edges: " + graph.getEdgeCount());

	        //Filter：对节点进行过滤操作
	        DegreeRangeFilter degreeFilter = new DegreeRangeFilter();
	        degreeFilter.init(graph);
	        degreeFilter.setRange(new Range(Config.FILTERDEGREE, Integer.MAX_VALUE));     //Remove nodes with degree < 30
	        Query query = filterController.createQuery(degreeFilter);
	        GraphView view = filterController.filter(query);
	        graphModel.setVisibleView(view);    //Set the filter result as the visible view

	        //See visible graph stats
	        UndirectedGraph graphVisible = graphModel.getUndirectedGraphVisible();
	        Log.log("Nodes: " + graphVisible.getNodeCount());
	        Log.log("Edges: " + graphVisible.getEdgeCount());

//	        //Run YifanHuLayout for 100 passes - The layout always takes the current visible view ： 运行布局算法
//	        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
//	        layout.setGraphModel(graphModel);
//	        layout.resetPropertiesValues();
//	        layout.setOptimalDistance(200f);
	//
//	        layout.initAlgo();
//	        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
//	            layout.goAlgo();
//	        }
//	        layout.endAlgo();

	        //Layout for 1 minute
	        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
	        autoLayout.setGraphModel(graphModel);
	        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
	        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
	        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
	        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 1000., 0f);//500 for the complete period
	        autoLayout.addLayout(firstLayout, 0.5f);
	        autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
	        autoLayout.execute();

	        //Append as a Directed Graph
	        DirectedGraph directedGraph = graphModel.getDirectedGraph();
	        ArrayList<Node> nodeList = new ArrayList<Node>();
	        //Iterate over nodes
	        for (Node n : directedGraph.getNodes()) {
	            Node[] neighbors = directedGraph.getNeighbors(n).toArray();
	            if (neighbors.length != 0) {
	                System.out.println(n.getLabel() + " has " + neighbors.length + " neighbors");
	            } else {
	                nodeList.add(n);
	                System.out.println(n.getLabel() + " has " + neighbors.length + " neighbors, need to delete");
	            }
	        }
	        directedGraph.removeAllNodes(nodeList);
	        System.out.println("------删除没有邻居节点的节点后的节点数量和边的数量------");
	        System.out.println("Nodes: " + graphVisible.getNodeCount());
	        System.out.println("Edges: " + graphVisible.getEdgeCount());
	        //Iterate over edges
//	        for (Edge e : directedGraph.getEdges()) {
//	        	Log.log(e.getSource().getId() + " -> " + e.getTarget().getId());
//	        }

	        //Get Centrality
	        GraphDistance distance = new GraphDistance();
	        distance.setDirected(true);
	        distance.execute(graphModel);

	        //Rank color by Degree
	        Function degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingElementColorTransformer.class);
	        RankingElementColorTransformer degreeTransformer = (RankingElementColorTransformer) degreeRanking.getTransformer();
	        degreeTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
	        degreeTransformer.setColorPositions(new float[]{0f, 1f});
	        appearanceController.transform(degreeRanking);

	        //Rank size by centrality
	        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
	        Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
	        RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking.getTransformer();
	        centralityTransformer.setMinSize(10);
	        centralityTransformer.setMaxSize(30);
	        appearanceController.transform(centralityRanking);

	        //Rank label size - set a multiplier size
	        Function centralityRanking2 = appearanceModel.getNodeFunction(graph, centralityColumn, RankingLabelSizeTransformer.class);
	        RankingLabelSizeTransformer labelSizeTransformer = (RankingLabelSizeTransformer) centralityRanking2.getTransformer();
	        labelSizeTransformer.setMinSize(0.5f);
	        labelSizeTransformer.setMaxSize(1.5f);
	        appearanceController.transform(centralityRanking2);

	        //Set 'show labels' option in Preview - and disable node size influence on text size
	        PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
	    	//previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
	        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);

	        //Preview
	        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
	        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
	        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
	        model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(15));

	        //Run modularity algorithm - community detection
	        Modularity modularity = new Modularity();
	        modularity.execute(graphModel);
	        try {
				Class.forName("org.netbeans.JarClassLoader$JarURLStreamHandler");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //Partition with 'modularity_class', just created by Modularity algorithm
	        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
	        Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
	        Partition partition2 = ((PartitionFunction) func2).getPartition();
	        Log.log(partition2.size() + " partitions found");
	        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
	        partition2.setColors(palette2.getColors());
	        appearanceController.transform(func2);

	        //Export
	        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
	        // 导出成文件
	        try {
	            ec.exportFile(new File(Config.GEXFPATH + "\\" + ClassName + ".gexf"));
	            ec.exportFile(new File(Config.GEXFPATH + "\\" + ClassName + ".pdf"));
	        } catch (IOException ex) {
	        	response = Response.status(200).entity("生成关系图数据到文件失败~" + ex).build();
	        }
	        // 导出成字符串
	        Exporter exporter = ec.getExporter("gexf");
	        CharacterExporter characterExporter = (CharacterExporter)exporter;
	        StringWriter stringWriter = new StringWriter();
	        ec.exportWriter(stringWriter, characterExporter);
	        String result = stringWriter.toString();
//	        Log.log(result);
	        if (result == null || result.equals("")) {
	        	response = Response.status(401).entity(new error("认知关系生成失败~")).build();
			} else {
				response = Response.status(200).entity(new success(result)).build();
			}
		}
		return response;
    }
	
}
