package org.jivesoftware.openfire.plugin;


import java.io.File;  

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
  

public class ChatLogsPlugin implements Plugin {
	
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("ChatLogsPlugin start!!!"); 
	}
	
	public void destroyPlugin() {  
        // Your code goes here  
        System.out.println("ChatLogsPlugin stop!!!");  
    }
}  