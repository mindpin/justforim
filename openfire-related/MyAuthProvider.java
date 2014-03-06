package org.jivesoftware.openfire.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.plugin.UserServicePlugin;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;



import java.io.File;  
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  


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
		
		System.out.println("----hello world username -----: " + username);
		
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
			 
		
        username = username.trim().toLowerCase();
        
 
        InputStream in = null;

        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(url);
            

            //Add any parameter if u want to send it with Post req.
            method.addParameter(username_param, username);
            method.addParameter(password_param, password);

            int statusCode = client.executeMethod(method);
            
            System.out.println("status code: " + statusCode);
            
            
            if (statusCode == 411) {
                // in = method.getResponseBodyAsStream();
            	throw new UnauthorizedException();
            }


        } catch (Exception e) {
        	throw new UnauthorizedException();
        }
        
        XMPPServer server = XMPPServer.getInstance();
        UserManager userManager = server.getUserManager();
        try {
			userManager.createUser(username, password, "", "");
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