/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ICommand;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public abstract class CustomAbstractAction extends javax.swing.AbstractAction implements ICommand {

    private static final long serialVersionUID = 1648027023533465104L;

    /**
     * ID of the action
     */
    private String actionId;

    /**
     * Parameters of the instance
     */
    private Properties properties;
    
    private ICommandHandler commandHandler;
    
    /**
     * State of the application
     */
    private IState state;

    public CustomAbstractAction() {
        super();
    }

    public CustomAbstractAction(String name) {
        super(name);
    }

    public CustomAbstractAction(String name, Icon icon) {
        super(name, icon);
    }

    public void setCommandHandler(ICommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}
    
    protected void setState(IState state) {
    	this.state = state;
    }
    
    protected IState getState() {
		return state;
	}

    /**
     * @param actionId
     *            the actionId to set
     */
    protected void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * @param properties
     *            the properties to set
     */
    protected void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @return the properties
     */
    protected Properties getProperties(String actionId) {
        return null;
    }

    /**
     * Initializes action if needed
     * All initialization needed retrieving values from <code>getState</code> must be done here
     * Must call super.initialize() when overriding
     * @param state
     */
    protected void initialize() {   
        if (!getCommandName().isEmpty()) {
        	commandHandler.registerCommand(this);
        }
    }

    /**
     * Indicates whether this action must be enabled or disabled when used in
     * navigator tree with given selection
     * 
     * @param rootSelected
     * @param selection
     * @return
     */
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return false;
    }

    /**
     * Indicates whether this action must be enabled or disabled when used in
     * navigator table with given selection
     * 
     * @param selection
     * @return
     */
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return false;
    }

    /**
     * Indicates whether this action must be enabled or disabled when used in
     * play list with given selection
     * 
     * @param selection
     * @return
     */
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return false;
    }

    @Override
    public void runCommand() {
        actionPerformed(null);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	Logger.debug("Executing action: ", this.getClass().getName());
    	executeAction();
    }
    
    /**
     * Override this method to execute action
     */
    protected void executeAction() {
	}

	@Override
    public String getCommandName() {
        return "";
    }

    /**
     * @return the actionId
     */
    protected String getActionId() {
        return actionId;
    }

    /**
     * @return the properties
     */
    protected Properties getProperties() {
        return properties;
    }
    
    /**
     * Delegate method
     * @param <T>
     * @param beanType
     * @return
     */
    protected <T> T getBean(Class<T> beanType) {
		return Context.getBean(beanType);
	}
	
    /**
     * Delegate method
     * @param name
     * @return
     */
    protected Object getBean(String name) {
		return Context.getBean(name);
	}
}
