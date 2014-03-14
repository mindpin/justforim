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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

  
public class ChatLogsServlet extends HttpServlet {  
  

	private static final long serialVersionUID = 1L;

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
        JSONArray json = new JSONArray();
        
        String username = request.getParameter("username");
        int pageSize = 5;
        int page = Integer.parseInt(request.getParameter("page"));
        int startIndex = (page - 1) * pageSize;
        int numResults = pageSize;
        
        System.out.println("username is: " + username);
        
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT * from ofMessageArchive");
        query.append(" where fromJID = '");
        query.append(username);
        query.append("' or toJID = '");
        query.append(username);
        query.append("' ORDER BY  ofMessageArchive.sentDate");
        query.append(" DESC");
        query.append(" LIMIT ").append(startIndex).append(",").append(numResults);
        
        System.out.println("sql output is : " + query.toString());
        
        
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
            	  json.put(obj);
            }
            
            rs.close();
            
            System.out.println("final json -- " + json);
            
            replyMessage(json, response, out);
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("sql + whatsup -" + e.getMessage());
			// e.printStackTrace();
			replyError(e.toString(), response, out);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("json + whatsup -" + e.getMessage());
			replyError(e.toString(), response, out);
			// e.printStackTrace();
		} finally {
            DbConnectionManager.closeConnection(result, con);
        }
    }
    
    private void replyMessage(JSONArray json, HttpServletResponse response, PrintWriter out){
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
  
        // response.setContentType("text/plain");
    }  
  
    @Override  
    public void destroy() {  
        super.destroy();  
    }  
}  