package org.jivesoftware.openfire.plugin;


import java.io.File;  
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jivesoftware.openfire.SharedGroupException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.roster.Roster;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.roster.RosterManager;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
  

public class ContactListPlugin implements Plugin {
	private UserManager userManager;
    private RosterManager rosterManager;
    private XMPPServer server;
	
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("ContactListPlugin start!!!"); 
		
		server = XMPPServer.getInstance();
        userManager = server.getUserManager();
        rosterManager = server.getRosterManager();
	}
	
	public void destroyPlugin() {  
        // Your code goes here  
        System.out.println("ContactListPlugin stop!!!");  
    }
	
	
	
	/**
     * Add new roster item for specified user
     *
     * @param username the username of the local user to add roster item to.
     * @param itemJID the JID of the roster item to be added.
     * @param itemName the nickname of the roster item.
     * @param subscription the type of subscription of the roster item. Possible values are: -1(remove), 0(none), 1(to), 2(from), 3(both).
     * @param groupNames the name of a group to place contact into.
     * @throws UserNotFoundException if the user does not exist in the local server.
     * @throws UserAlreadyExistsException if roster item with the same JID already exists.
     * @throws SharedGroupException if roster item cannot be added to a shared group.
     */
    public void addRosterItem(String username, String itemJID, String itemName, String subscription, String groupNames)
            throws UserNotFoundException, UserAlreadyExistsException, SharedGroupException
    {
    	userManager.getUser(username);
        Roster r = rosterManager.getRoster(username);
        JID j = new JID(itemJID);

        try {
            r.getRosterItem(j);
            throw new UserAlreadyExistsException(j.toBareJID());
        }
        catch (UserNotFoundException e) {
            //Roster item does not exist. Try to add it.
        }

        if (r != null) {
            List<String> groups = new ArrayList<String>();
            if (groupNames != null) {
                StringTokenizer tkn = new StringTokenizer(groupNames, ",");
                while (tkn.hasMoreTokens()) {
                    groups.add(tkn.nextToken());
                }
            }
            RosterItem ri = r.createRosterItem(j, itemName, groups, false, true);
            if (subscription == null) {
                subscription = "0";
            }
            ri.setSubStatus(RosterItem.SubType.getTypeFromInt(Integer.parseInt(subscription)));
            r.updateRosterItem(ri);
        }
    }
    
    
    /**
     * Update roster item for specified user
     *
     * @param username the username of the local user to update roster item for.
     * @param itemJID the JID of the roster item to be updated.
     * @param itemName the nickname of the roster item.
     * @param subscription the type of subscription of the roster item. Possible values are: -1(remove), 0(none), 1(to), 2(from), 3(both).
     * @param groupNames the name of a group.
     * @throws UserNotFoundException if the user does not exist in the local server or roster item does not exist.
     * @throws SharedGroupException if roster item cannot be added to a shared group.
     */
    public void updateRosterItem(String username, String itemJID, String itemName, String subscription, String groupNames)
            throws UserNotFoundException, SharedGroupException
    {
    	userManager.getUser(username);
        Roster r = rosterManager.getRoster(username);
        JID j = new JID(itemJID);

        RosterItem ri = r.getRosterItem(j);

        List<String> groups = new ArrayList<String>();
        if (groupNames != null) {
            StringTokenizer tkn = new StringTokenizer(groupNames, ",");
            while (tkn.hasMoreTokens()) {
                groups.add(tkn.nextToken());
            }
        }

        ri.setGroups(groups);
        ri.setNickname(itemName);

        if (subscription == null) {
            subscription = "3";
        }
        ri.setSubStatus(RosterItem.SubType.getTypeFromInt(Integer.parseInt(subscription)));
        r.updateRosterItem(ri);
    }
    
    
    /**
     * Delete roster item for specified user. No error returns if nothing to delete.
     *
     * @param username the username of the local user to add roster item to.
     * @param itemJID the JID of the roster item to be deleted.
     * @throws UserNotFoundException if the user does not exist in the local server.
     * @throws SharedGroupException if roster item cannot be deleted from a shared group.
     */
    public void deleteRosterItem(String username, String itemJID)
            throws UserNotFoundException, SharedGroupException
    {
    	userManager.getUser(username);
        Roster r = rosterManager.getRoster(username);
        JID j = new JID(itemJID);

        // No roster item is found. Uncomment the following line to throw UserNotFoundException.
        r.getRosterItem(j);

        r.deleteRosterItem(j, true);
    }
}  