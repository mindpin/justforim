package org.jivesoftware.openfire.plugin.ContactList;

import java.io.IOException;  
import java.io.PrintWriter;  
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SharedGroupException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.ContactListPlugin;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;





  
public class ContactListServlet extends HttpServlet {
  

	private static final long serialVersionUID = 1L;
	
	private ContactListPlugin plugin;

	@Override  
    public void init() throws ServletException {  
        super.init();
        AuthCheckFilter.addExclude("contactlist");
        
        plugin = (ContactListPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("contactlist");
       
    }  
  
    @Override  
    protected void doGet(HttpServletRequest request,  
            HttpServletResponse response) throws ServletException, IOException {  
        // super.doGet(request, response);
    	  
        PrintWriter out = response.getWriter();
        
        String type = request.getParameter("type");
        
        String username = request.getParameter("user");
        String item_jid = request.getParameter("item_jid");
        String item_name = item_jid.split("@")[0];
        String subscription = "3";
        String groupNames = "Everyone";
        
        if (type.equals("add")) {
        
	        try {
				plugin.addRosterItem(username, item_jid, item_name, subscription, groupNames);
				
				replyMessage("ok", response, out);
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				replyError(e.toString(),response, out);
			} catch (UserAlreadyExistsException e) {
				// TODO Auto-generated catch block
				try {
					plugin.updateRosterItem(username, item_jid, item_name, subscription, groupNames);
				} catch (UserNotFoundException e1) {
					replyError(e.toString(),response, out);
				} catch (SharedGroupException e1) {
					replyError(e.toString(),response, out);
				}
				replyError(e.toString(),response, out);
			} catch (SharedGroupException e) {
				// TODO Auto-generated catch block
				replyError(e.toString(),response, out);
			}
        }
        
        if (type.equals("delete")) {
        	try {
				plugin.deleteRosterItem(username, item_jid);
				replyMessage("ok", response, out);
			} catch (UserNotFoundException e) {
				replyError(e.toString(),response, out);
			} catch (SharedGroupException e) {
				replyError(e.toString(),response, out);
			}
        }
    }
    
  
    
    private void replyMessage(String message, HttpServletResponse response, PrintWriter out){
    	response.setContentType("text/xml");        
        out.println("<result>" + message + "</result>");
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
        AuthCheckFilter.removeExclude("contactlist");
    }  
}  