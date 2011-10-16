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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import java.awt.PopupMenu;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class encapsulates com.apple.eawt.Application using reflection
 * as this class is only available in Mac OS environments
 * @author alex
 */
public class MacOSXApplication implements IMacOSXApplication {

	private Object application;

	@Override
	public boolean initialize()  {
		application = ReflectionUtils.newInstance("com.apple.eawt.Application");
		return application != null;
	}
	
	@Override
	public void registerAbout(MacOSXAboutHandler aboutHandler) {
		setHandler(aboutHandler);
		ReflectionUtils.invoke(application, ReflectionUtils.getMethod(application.getClass(), "setEnabledAboutMenu", boolean.class), true);
	}
	
	@Override
	public void registerQuit(MacOSXQuitHandler quitHandler) {
    	setHandler(quitHandler);
    }

	@Override
	public void registerPreferences(MacOSXPreferencesHandler prefsHandler) {
        setHandler(prefsHandler);
		ReflectionUtils.invoke(application, ReflectionUtils.getMethod(application.getClass(), "setEnabledPreferencesMenu", boolean.class), true);
    }
    
	@Override
	public void registerAppReOpenedListener(MacOSXAppReOpenedListener adapter) {
        Class<?> appEventListenerClass = ReflectionUtils.getClass("com.apple.eawt.AppEventListener");
        Class<?> appReOpenedListenerClass = ReflectionUtils.getClass("com.apple.eawt.AppReOpenedListener");
        if (appEventListenerClass != null && appReOpenedListenerClass != null) {
            Method addListenerMethod = ReflectionUtils.getMethod(application.getClass(), "addAppEventListener", appEventListenerClass);
            // Create a proxy object around this handler that can be reflectively added as an Apple AppReOpenedListener
            Object osxAdapterProxy = Proxy.newProxyInstance(MacOSXAppReOpenedListener.class.getClassLoader(), new Class[] { appReOpenedListenerClass }, adapter);
            ReflectionUtils.invoke(application, addListenerMethod, osxAdapterProxy);
        }
    }

	@Override
	public void addDockIconMenu() {
    	ReflectionUtils.invoke(application, ReflectionUtils.getMethod(application.getClass(), "setDockMenu", PopupMenu.class), getDockMenu());
    }
    
    /**
     * creates a Proxy object from the passed adapter and adds it to application
     * @param adapter
     */
    private void setHandler(InvocationHandler adapter) {
        Class<?> applicationListenerClass = ReflectionUtils.getClass("com.apple.eawt.ApplicationListener");
    	if (applicationListenerClass != null) {
    		Method method = ReflectionUtils.getMethod(application.getClass(), "addApplicationListener", applicationListenerClass);
            // Create a proxy object around this handler that can be reflectively added as an Apple ApplicationListener
            Object osxAdapterProxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { applicationListenerClass }, adapter);
            ReflectionUtils.invoke(application, method, osxAdapterProxy);
    	}
    }
    
    /**
     * Returns menu for dock icon
     * @return
     */
    private PopupMenu getDockMenu() {
    	PopupMenu menu = new PopupMenu();
    	menu.add(Actions.getMenuItemForAction(PlayAction.class));
    	menu.add(Actions.getMenuItemForAction(StopCurrentAudioObjectAction.class));
    	menu.add(Actions.getMenuItemForAction(PlayPreviousAudioObjectAction.class));
    	menu.add(Actions.getMenuItemForAction(PlayNextAudioObjectAction.class));
    	return menu;
    }
}