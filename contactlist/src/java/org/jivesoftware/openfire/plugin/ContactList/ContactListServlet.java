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
import org.xmpp.packet.JID;





  
public class ContactListServlet extends HttpServlet {
  

	private static final long serialVersionUID = 1L;
	
	private ContactListPlugin plugin;
	private String domain;

	@Override  
    public void init() throws ServletException {  
        super.init();
        AuthCheckFilter.addExclude("contactlist");
        
        plugin = (ContactListPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("contactlist");
        domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
        
        System.out.println("hostname: " + XMPPServer.getInstance().getServerInfo().getHostname());
        System.out.println("contactlist domain: " + domain);
       
    }  
  
    @Override  
    protected void doGet(HttpServletRequest request,  
            HttpServletResponse response) throws ServletException, IOException {  
        // super.doGet(request, response);
    	  
        PrintWriter out = response.getWriter();
        
        String type = request.getParameter("type");
        String domain_part = "@" + domain;
        
        String username = request.getParameter("user");
        username = JID.escapeNode(username);
        
        String item_jid = request.getParameter("item_jid");
        System.out.println("lastIndexOf is: " + item_jid.lastIndexOf(domain_part));
        
        String item_name = item_jid.substring(0, item_jid.lastIndexOf(domain_part));
        
        System.out.println("item_name is: " + item_name);
        
//        System.out.println("param item_jid is: " + item_jid);
//        String[] item_jid_parts = item_jid.split("@" + domain);
//        System.out.println("param item_jid length is: " + item_jid_parts.length);
//        if (item_jid_parts.length == 2) {
//        	item_jid = item_jid_parts[0] + "@" + domain + item_jid_parts[1];
//        } else {
//        	item_jid = item_jid_parts[0];
//        }
        
        item_name = JID.escapeNode(item_name);
        item_jid = item_name + domain_part;
        
        System.out.println("new username: " + username);
        System.out.println("new item_jid: " + item_jid);
        
        
        String subscription = "3";
        // String groupNames = "Everyone";
        String group_names = null;
        
        if (type.equals("add")) {
        
	        try {
				plugin.addRosterItem(username, item_jid, item_name, subscription, group_names);
				
				replyMessage("ok", response, out);
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				replyError(e.toString(),response, out);
			} catch (UserAlreadyExistsException e) {
				// TODO Auto-generated catch block
				try {
					plugin.updateRosterItem(username, item_jid, item_name, subscription, group_names);
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