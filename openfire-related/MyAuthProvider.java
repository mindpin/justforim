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
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
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
	public void authenticate(String username, String password)
			throws UnauthorizedException, ConnectionException,
			InternalUnauthenticatedException {
		// TODO Auto-generated method stub
		username = username.trim().toLowerCase();
		
		String web_user_id = "";
		String web_email = JID.unescapeNode(username);

		System.out.println("----hello world username -----: " + web_email);
		
		String url = "";
		String username_param = "";
		String password_param = "";
		
		
		try {  
			  
		   File xmlFile = new File("../conf/web-server-url.xml");  
		   DocumentBuilderFactory documentFactory = DocumentBuilderFactory  
		     .newInstance();  
		   DocumentBuilder documentBuilder = documentFactory  
		     .newDocumentBuilder();  
		   Document doc = documentBuilder.parse(xmlFile);  
		  
		   doc.getDocumentElement().normalize();  
		   NodeList nodeList = doc.getElementsByTagName("server");  
		  
		   System.out.println("Root element :"  
		     + doc.getDocumentElement().getNodeName());  
		  
		   for (int temp = 0; temp < nodeList.getLength(); temp++) {  
		    Node node = nodeList.item(temp);  
		  
		    System.out.println("\nElement type :" + node.getNodeName());  
		  
		    if (node.getNodeType() == Node.ELEMENT_NODE) {  
		  
		     Element data = (Element) node;  
		     
		     url = data.getAttribute("id");
		     username_param = data.getElementsByTagName("username").item(0).getTextContent();
		     password_param = data.getElementsByTagName("password").item(0).getTextContent();
		     
		     System.out.println("Data url : " + url);
		     System.out.println("username_param : " + username_param);
		     System.out.println("password_param : " + password_param);
		  
		    }  
		   }  
	    } catch (Exception e) {  
		   e.printStackTrace();  
	    }
			 
		
        
        
 
        try {
        	String response = new String();
        	
        	HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(url);


            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair(username_param, web_email));
            postParameters.add(new BasicNameValuePair(password_param, password));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters));

            HttpResponse httpresponse = httpclient.execute(httppost);
            
            HttpEntity responseEntity = httpresponse.getEntity();
            if(responseEntity!=null) {
                response = EntityUtils.toString(responseEntity);
            }
            
            int statusCode = httpresponse.getStatusLine().getStatusCode();

            JSONObject json = new JSONObject(response);
            System.out.println("json is: " + json);
            web_user_id = Integer.toString((Integer) json.get("id"));
            web_email = json.getString("email");
            
            
            System.out.println("status code: " + statusCode);
            System.out.println("response content: " + response);
            System.out.println("user_id as username: " + web_user_id);
            System.out.println("email: " + web_email);
            
            
            if (statusCode != 200) {
                // in = method.getResponseBodyAsStream();
            	throw new UnauthorizedException();
            }
            
            username = web_user_id;

        } catch (Exception e) {
        	throw new UnauthorizedException();
        }
        
        XMPPServer server = XMPPServer.getInstance();
        UserManager userManager = server.getUserManager();
        try {
			userManager.createUser(username, password, "", web_email);
			userManager.getUser(username);
		} catch (UserAlreadyExistsException e) {
			// TODO Auto-generated catch block
			System.out.println("whatsup: " + e.getMessage());
			// e.printStackTrace();
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("whatsup: " + e.getMessage());
			// e.printStackTrace();
		}
        
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

    
}