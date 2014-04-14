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

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmpp.packet.JID;

  
public class ChatLogsServlet extends HttpServlet {  
  

	private static final long serialVersionUID = 1L;
	private String domain;

	@Override  
    public void init() throws ServletException {  
        super.init();
        AuthCheckFilter.addExclude("chatlogs");
        
        domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
        System.out.println("chatlogs domain: " + domain);
    }  
  
    @Override  
    protected void doGet(HttpServletRequest request,  
            HttpServletResponse response) throws ServletException, IOException {  
        // super.doGet(request, response);
//    	PrintWriter out = response.getWriter();
//    	response.setContentType("text/plain; charset=utf-8");        
//        out.println("abc");
//        out.flush();
    	  
        PrintWriter out = response.getWriter();
        Connection con = null;
        PreparedStatement result = null;
        JSONObject obj = null;
        JSONArray json = new JSONArray();
        String domain_part = "@" + domain;
        
        String user1 = request.getParameter("user1");
        user1 = user1.substring(0, user1.lastIndexOf(domain_part));
        
        user1 = JID.escapeNode(user1);
        user1 = user1 + domain_part;
        user1 = user1.replace("\\", "\\\\");
        
        String user2 = request.getParameter("user2");
        user2 = user2.substring(0, user2.lastIndexOf(domain_part));
        
        user2 = JID.escapeNode(user2);
        user2 = user2 + domain_part;
        user2 = user2.replace("\\", "\\\\");
        
        int total_count = 0;
        int pageSize = 5;
        // int page = Integer.parseInt(request.getParameter("page"));
        
        
        System.out.println("user1 is: " + user1);
        System.out.println("user2 is: " + user2);
        
        StringBuilder query_count = new StringBuilder();
        query_count.append("SELECT count(*) as sum from ofMessageArchive");
        query_count.append(" where (fromJID = '");
        query_count.append(user1);
        query_count.append("' and toJID = '");
        query_count.append(user2);
        query_count.append("') or (fromJID = '");
        query_count.append(user2);
        query_count.append("' and toJID = '");
        query_count.append(user1);
        query_count.append("') ");
        
        System.out.println("sql query_count output is : " + query_count.toString());
        
        // Get total count
        try {
			con = DbConnectionManager.getConnection();
			PreparedStatement result_count = DbConnectionManager.createScrollablePreparedStatement(con, query_count.toString());
			ResultSet rs_count = result_count.executeQuery();
			rs_count.next();
			
			System.out.println("total count: " + rs_count.getString("sum"));
			total_count = Integer.parseInt(rs_count.getString("sum"));
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("sql + whatsup -" + e.getMessage());
			// e.printStackTrace();
			replyError(e.toString(), response, out);
        	
        }
        
        int startIndex = ((total_count - pageSize) < 0)? 0:(total_count - pageSize);
        System.out.println("start index: " + startIndex);
        
        int numResults = pageSize;
                
        
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT * from ofMessageArchive");
        query.append(" where (fromJID = '");
        query.append(user1);
        query.append("' and toJID = '");
        query.append(user2);
        query.append("') or (fromJID = '");
        query.append(user2);
        query.append("' and toJID = '");
        query.append(user1);
        query.append("') ORDER BY  ofMessageArchive.sentDate");
        query.append(" ASC");
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
        AuthCheckFilter.removeExclude("chatlogs");
    }  
}  