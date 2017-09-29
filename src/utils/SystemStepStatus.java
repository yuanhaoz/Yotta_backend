package utils;
/**
 * @author 石磊
 * @date 2016年10月26日15:56:14
 * @description 课程的每一步的状态。步骤的状态有三个：未开始、正在执行、已完成；
 * 				其中，正在执行不能重复操作。检测完成上一步后才能开始自己的步骤。
 * 
 * @配置  在config中增加
 *   public static String MYSQL_URL="jdbc:mysql://202.117.16.39:3306/mooc?user=kg&password=kg&characterEncoding=UTF8";
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.Config;

public class SystemStepStatus {

	
	public static void main(String[] args) {
		System.out.println(getStepStatus("高等数学（一）", "DataInput"));
		System.out.println(creatStepStatus("测试课程"));
		System.out.println(setStepStatus("高等数学（一）","DataInput","已完成"));
		System.out.println(getStepStatus("高等数学（一）", "DataInput"));
	}
	
	/**
	 *  设置状态。更新每一门课程的状态
	 * @param className
	 * @param StepName
	 * @param StepStatus
	 * @return 是否更新成功
	 */
	public static boolean setStepStatus(String className,String StepName,String StepStatus) {
		Connection conn = null;
		Statement stmt = null;
		String URL = Config.MYSQL_URL;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL);
			if (conn == null) {
			}
			String sql = "update System_Step_Status set  "+StepName+"='"+StepStatus+"' where ClassName='"+className+"' ";
			int status=0;
			try {
				stmt = conn.createStatement();
				status=stmt.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
			stmt.close();
			if (status==1) {
				return true;
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false;
		
	}
	
	
	/**
	 * 创建课程，初始化课程状态
	 * @param className
	 * @return  是否创建成功
	 */
	public static boolean creatStepStatus(String className) {
		Connection conn = null;
		Statement stmt = null;
		String URL = Config.MYSQL_URL;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL);
			if (conn == null) {
			}
			String sql = "insert into System_Step_Status values('"+className+"','未开始','未开始','未开始','未开始','未开始','未开始');";
			
			int status=0;
			try {
				stmt = conn.createStatement();
				status=stmt.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
			stmt.close();
			if (status==1) {
				return true;
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false;
		
	}
	
	
	/**
	 * 获得每一步骤的状态
	 * @param ClassName
	 * @param StepName
	 * @return
	 */
	public static String  getStepStatus(String ClassName, String StepName) {
		String result=null;
		Connection conn = null;
		Statement stmt = null;
		String URL = Config.MYSQL_URL;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL);
			if (conn == null) {
			}
			String sql = "select "+StepName+" from System_Step_Status where ClassName='"+ClassName+"' ";
			try {
				stmt = conn.createStatement();
				ResultSet rs=stmt.executeQuery(sql);
				while (rs.next()) {  
						result= rs.getString(1);
	            }  
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
			stmt.close();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}

}
