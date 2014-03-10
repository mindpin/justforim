package org.jivesoftware.openfire.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.plugin.UserServicePlugin;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.json.JSONObject;
import org.json.simple.JSONValue;



import java.io.File;  
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  
import org.xmpp.packet.JID;


public class MyAuthProvider implements AuthProvider {

	
    /**
     * Constructs a new DefaultAuthProvider.
     */
    public MyAuthProvider() {
    	System.out.println("----just a username-----1111: ");
    }

   

	@Override
	public boolean isPlainSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDigestSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void authenticate(String input_username, String password)
			throws UnauthorizedException, ConnectionException,
			InternalUnauthenticatedException {
		
		// String web_email = "";
		String username = JID.unescapeNode(input_username);
		ConfigInfo ci = get_config_info();
		
		System.out.println("current username: " + input_username);
		System.out.println("unescapeNode username: " + username);
 
        try {
        	String response = new String();
        	
        	HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(ci.auth_url);


            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair(ci.login_param, username));
            postParameters.add(new BasicNameValuePair(ci.password_param, password));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters));

            HttpResponse httpresponse = httpclient.execute(httppost);
            
            HttpEntity responseEntity = httpresponse.getEntity();
            if(responseEntity!=null) {
                response = EntityUtils.toString(responseEntity);
            }
            
            int statusCode = httpresponse.getStatusLine().getStatusCode();

            JSONObject json = new JSONObject(response);
            System.out.println("json is: " + json);
            // web_email = json.getString("email");
            
            
            System.out.println("status code: " + statusCode);
            System.out.println("response content: " + response);
            // System.out.println("web email: " + web_email);
            
            
            if (statusCode != 200) {
                // in = method.getResponseBodyAsStream();
            	throw new UnauthorizedException();
            }
            
        } catch (Exception e) {
        	throw new UnauthorizedException();
        }
        
        XMPPServer server = XMPPServer.getInstance();
        UserManager userManager = server.getUserManager();
        try {
			userManager.createUser(input_username, password, "", "");
			userManager.getUser(input_username);
		} catch (UserAlreadyExistsException e) {
			System.out.println("whatsup: " + e.getMessage());
		} catch (UserNotFoundException e) {
			System.out.println("whatsup: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("whatsup: " + e.getMessage());
		}
        
        
        _add_user_group(input_username);
        
        System.out.println("last username: " + username);
	    
	}

	@Override
	public void authenticate(String username, String token, String digest)
			throws UnauthorizedException, ConnectionException,
			InternalUnauthenticatedException {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public String getPassword(String username) throws UserNotFoundException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPassword(String username, String password)
			throws UserNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean supportsPasswordRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private void _add_user_group(String username) {
		Group group = null;
		String group_name = "default_group";
		XMPPServer server = XMPPServer.getInstance();
		
        try {
            group = GroupManager.getInstance().getGroup(group_name);
            
        } catch (GroupNotFoundException e) {
            // Create this group 
        	try {
				GroupManager.getInstance().createGroup(group_name);
			} catch (GroupAlreadyExistsException e1) {
				// TODO Auto-generated catch block
				System.out.println("whatsup GroupAlreadyExistsException: " + e1.getMessage());
			}
        	System.out.println("whatsup GroupNotFoundException: " + e.getMessage());
        	
        	try {
				group = GroupManager.getInstance().getGroup(group_name);
			} catch (GroupNotFoundException e1) {
				// TODO Auto-generated catch block
				System.out.println("whatsup GroupNotFoundException222: " + e1.getMessage());
			}
        } 
        
        group.getMembers().add(server.createJID(username, null));
	}
	
	
	private ConfigInfo get_config_info() {
		ConfigInfo ci = new ConfigInfo();
		try {  
			  
			   File xmlFile = new File("../conf/web-auth-server-conf.xml");  
			   DocumentBuilderFactory documentFactory = DocumentBuilderFactory  
			     .newInstance();  
			   DocumentBuilder documentBuilder = documentFactory  
			     .newDocumentBuilder();  
			   Document doc = documentBuilder.parse(xmlFile);  
			  
			   doc.getDocumentElement().normalize();  
			   NodeList nodeList = doc.getElementsByTagName("config");  
			  
			   System.out.println("Root element :"  
			     + doc.getDocumentElement().getNodeName());  
			  
			   for (int temp = 0; temp < nodeList.getLength(); temp++) {
			    Node node = nodeList.item(temp);  
			  
			    System.out.println("\nElement type :" + node.getNodeName());  
			  
			    if (node.getNodeType() == Node.ELEMENT_NODE) {  
			  
			     Element data = (Element) node;  
			     
			     ci.login_param = data.getElementsByTagName("login_param").item(0).getTextContent();
			     ci.password_param = data.getElementsByTagName("password_param").item(0).getTextContent();
			     ci.auth_url = data.getElementsByTagName("auth_url").item(0).getTextContent();
			     
			     System.out.println("Data url : " + ci.auth_url);
			     System.out.println("username_param : " + ci.login_param);
			     System.out.println("password_param : " + ci.password_param);
			  
			    }  
			   }  
		    } catch (Exception e) {  
			   e.printStackTrace();  
		    }
		
		
		return ci;
	}
	
	class ConfigInfo {
		String auth_url;
		String login_param;
		String password_param;
	}

    
}