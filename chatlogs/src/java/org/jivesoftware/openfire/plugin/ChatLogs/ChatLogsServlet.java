package org.jivesoftware.openfire.plugin.ChatLogs;

import java.io.IOException;  
import java.io.PrintWriter;  
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.jivesoftware.database.DbConnectionManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

  
public class ChatLogsServlet extends HttpServlet {  
  
    @Override  
    public void init() throws ServletException {  
        super.init();  
    }  
  
    @Override  
    protected void doGet(HttpServletRequest request,  
            HttpServletResponse response) throws ServletException, IOException {  
        // super.doGet(request, response);
  
        PrintWriter out = response.getWriter();
        Connection con = null;
        PreparedStatement result = null;
        JSONObject obj = null;
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT * from ofMessageArchive");
        
        
        try {
			con = DbConnectionManager.getConnection();
			result = DbConnectionManager.createScrollablePreparedStatement(con, query.toString());
			ResultSet rs = result.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
            while (rs.next()) {
            	int numColumns = rsmd.getColumnCount();
            	  obj = new JSONObject();

            	  for( int i=1; i<numColumns+1; i++) {
            	    String column_name = rsmd.getColumnName(i);

            	    switch( rsmd.getColumnType( i ) ) {
            	      case java.sql.Types.VARCHAR:
            	        obj.put(column_name, rs.getString(column_name));
            	        break;
            	      case java.sql.Types.DATE:
            	        obj.put(column_name, rs.getDate(column_name));      
            	        break;
            	      case java.sql.Types.TIMESTAMP:
            	        obj.put(column_name, rs.getTimestamp(column_name)); 
            	        break;
            	      default:
            	        obj.put(column_name, rs.getObject(column_name));    
            	        break;
            	    }
            	  }
            }
            rs.close();
            
            System.out.println("final obj -- " + obj);
            
            replyMessage(obj, response, out);
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("sql + whatsup -" + e.getMessage());
			// e.printStackTrace();
			replyError(e.toString(), response, out);
		} finally {
            DbConnectionManager.closeConnection(result, con);
        }
    }
    
    private void replyMessage(JSONObject json, HttpServletResponse response, PrintWriter out){
        response.setContentType("text/json; charset=utf-8");        
        out.println(json);
        out.flush();
    }

    private void replyError(String error, HttpServletResponse response, PrintWriter out){
        response.setContentType("text/xml");        
        out.println("<error>" + error + "</error>");
        out.flush();
    }
  
    @Override  
    protected void doPost(HttpServletRequest request,  
            HttpServletResponse response) throws ServletException, IOException {  
        super.doPost(request, response);  
  
        response.setContentType("text/plain");  
        PrintWriter out = response.getWriter();  
        System.out.println("请求HelloWorldServlet doPost Method");  
        out.print("请求HelloWorldServlet doPost Method");  
        out.flush();  
    }  
  
    @Override  
    public void destroy() {  
        super.destroy();  
    }  
}  