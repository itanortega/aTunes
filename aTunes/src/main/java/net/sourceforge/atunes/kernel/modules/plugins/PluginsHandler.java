/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.plugins;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSets;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.player.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.PluginSystemLogger;
import org.commonjukebox.plugins.PluginsFactory;
import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public class PluginsHandler extends AbstractHandler implements PluginListener {

    /** Singleton instance */
    private static PluginsHandler instance;

    /**
     * Plugins factory
     */
    private PluginsFactory factory;

    private static Set<PluginType> pluginTypes;

    /**
     * Getter of singleton instance
     * 
     * @return
     */
    public static PluginsHandler getInstance() {
        if (instance == null) {
            instance = new PluginsHandler();
        }
        return instance;
    }

    /**
     * Initializes all plugins found in plugins dir
     */
    private void initPlugins() {
        try {
            Timer t = new Timer();
            t.start();
            factory = new PluginsFactory();

            PluginSystemLogger.addHandler(new PluginsLoggerHandler());
            PluginSystemLogger.setLevel(Level.ALL);

            // User plugins folder
            factory.setPluginsRepository(getUserPluginsFolder());
            
            // Temporal plugins folder
            factory.setTemporalPluginRepository(getTemporalPluginsFolder());

            addPluginListeners();
            int plugins = factory.start(getPluginClassNames(), true, "net.sourceforge.atunes");

            getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Found ", plugins, " plugins (", t.stop(), " seconds)"));
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
                getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
        } catch (IOException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
                getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
		}
    }

    @Override
    public void applicationFinish() {
        // TODO Auto-generated method stub

    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initHandler() {
        initPlugins();
    }

    @Override
    public void applicationStarted() {
        // TODO Auto-generated method stub

    }

    /**
     * A set of all plugin types accepted TODO: Add a new plugin type here
     * 
     * @return
     */
    private Set<PluginType> getPluginTypes() {
        if (pluginTypes == null) {
            pluginTypes = new HashSet<PluginType>();
            pluginTypes.add(new PluginType(PlaybackStateListener.class.getName(), PlayerHandler.getInstance(), false));
            pluginTypes.add(new PluginType(AbstractColumn.class.getName(), ColumnSets.getInstance(), false));
            pluginTypes.add(new PluginType(AbstractNavigationView.class.getName(), NavigationHandler.getInstance(), false));
            pluginTypes.add(new PluginType(AbstractContextPanel.class.getName(), ContextHandler.getInstance(), false));
            pluginTypes.add(new PluginType(AbstractLookAndFeel.class.getName(), LookAndFeelSelector.getInstance(), false));
            pluginTypes.add(new PluginType(AbstractGeneralPurposePlugin.class.getName(), GeneralPurposePluginsHandler.getInstance(), false));
        }
        return pluginTypes;
    }

    /**
     * Return class names of all plugin types
     * 
     * @return
     */
    private Set<String> getPluginClassNames() {
        Set<String> result = new HashSet<String>();
        for (PluginType pluginType : getPluginTypes()) {
            result.add(pluginType.getClassType());
        }
        return result;
    }

    /**
     * Registers plugin listeners of every plugin type This class is registered
     * for all plugin types
     */
    private void addPluginListeners() {
        for (PluginType pluginType : getPluginTypes()) {
            factory.addPluginListener(pluginType.getClassType(), this);
            factory.addPluginListener(pluginType.getClassType(), pluginType.getListener());
        }
    }

    /**
     * Return list of available plugins
     * 
     * @return
     */
    public List<PluginInfo> getAvailablePlugins() {
        return this.factory.getPlugins();
    }

    /**
     * Return path to plugins folder, which is inside user config folder.
     * 
     * @return the plugins folder
     */
    private static String getUserPluginsFolder() {
        String userConfigFolder = SystemProperties.getUserConfigFolder(Kernel.isDebug());
        String pluginsFolder = StringUtils.getString(userConfigFolder, SystemProperties.FILE_SEPARATOR, Constants.PLUGINS_DIR);
        File pluginFile = new File(pluginsFolder);
        if (!pluginFile.exists() && !pluginFile.mkdir()) {
            return userConfigFolder;
        }
        return pluginsFolder;
    }
    
    /**
     * Returns path to temporal plugins folder
     * @return path to temporal plugins folder
     * @throws IOException
     */
    private static String getTemporalPluginsFolder() throws IOException {
    	String temporalPluginsFolder = StringUtils.getString(SystemProperties.getTempFolder(), SystemProperties.FILE_SEPARATOR, Constants.PLUGINS_DIR);
    	File temporalPluginsFile = new File(temporalPluginsFolder);
    	if (!temporalPluginsFile.exists() && !temporalPluginsFile.mkdirs()) {
    		throw new IOException(StringUtils.getString("Can't create temporal plugins folder: ", temporalPluginsFolder));
    	}
    	return temporalPluginsFolder;
    }

    /**
     * Unzips a zip file in user plugins directory and updates plugins
     * 
     * @param zipFile
     * @throws PluginSystemException
     *             , IOException
     */
    public void installPlugin(File zipFile) throws IOException, PluginSystemException {
        try {
        	factory.installPlugin(zipFile);
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
                getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
            throw e;
        }
    }

    /**
     * Removes a plugin from user plugins folder and updates plugins
     * 
     * @param plugin
     * @throws IOException
     * @throws PluginSystemException
     */
    public void uninstallPlugin(PluginInfo plugin) throws IOException, PluginSystemException {
        // Only remove plugins if are contained in a separate folder under user plugins folder
        File pluginLocation = plugin.getPluginFolder();
        if (pluginLocation.getParent().equals(new File(getUserPluginsFolder()).getAbsolutePath())) {
            try {
                factory.uninstallPlugin(plugin);
            } catch (PluginSystemException e) {
                getLogger().error(LogCategories.PLUGINS, e);
                if (e.getCause() != null) {
                    getLogger().error(LogCategories.PLUGINS, e.getCause());
                }
                throw e;
            }
        }
    }

    /**
     * Activates or deactivates given plugin
     * 
     * @param plugin
     * @param active
     */
    public void setPluginActive(PluginInfo plugin, boolean active) {
        try {
            if (active) {
                PluginsHandler.getInstance().activatePlugin(plugin);
            } else {
            	PluginsHandler.getInstance().deactivatePlugin(plugin);
            }
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
                getLogger().error(LogCategories.PLUGINS, e.getCause());
            }
        }
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Plugin activated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        getLogger().info(LogCategories.PLUGINS, StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
    }

    /**
     * Returns <code>true</code> if the application must be restarted after
     * changing or activating / deactivating the given plugin
     * 
     * @param plugin
     * @return
     */
    public boolean pluginNeedsRestart(PluginInfo plugin) {
        for (PluginType pluginType : getPluginTypes()) {
            if (plugin.getPluginType().equals(pluginType.getClassType())) {
                return pluginType.isApplicationNeedsRestart();
            }
        }
        return false;
    }

    /**
     * Returns new instance of plugin
     * @param pluginInfo
     * @return
     * @throws PluginSystemException
     */
    public Plugin getNewInstance(PluginInfo pluginInfo) throws PluginSystemException {
    	return factory.getNewInstance(pluginInfo);
    }
    
    /**
     * Activates plugin
     * @param plugin
     * @throws PluginSystemException
     */
    public void activatePlugin(PluginInfo plugin) throws PluginSystemException {
    	factory.activatePlugin(plugin);
    }
    
    /**
     * Deactivates plugin
     * @param plugin
     * @throws PluginSystemException 
     */
    public void deactivatePlugin(PluginInfo plugin) throws PluginSystemException {
    	factory.deactivatePlugin(plugin);
    }

    /**
     * Validates plugin configuration
     * @param plugin
     * @param configuration
     * @throws InvalidPluginConfigurationException
     */
    public void validateConfiguration(PluginInfo plugin, PluginConfiguration configuration) throws InvalidPluginConfigurationException {
    	factory.validateConfiguration(plugin, configuration);
    }
    
    /**
     * Sets plugin configuration
     * @param plugin
     * @param configuration
     * @throws PluginSystemException
     */
    public void setConfiguration(PluginInfo plugin, PluginConfiguration configuration) throws PluginSystemException {
    	factory.setConfiguration(plugin, configuration);
    }
    
    /**
     * Returns plugin configuration
     * @param plugin
     * @return plugin configuration
     * @throws PluginSystemException
     */
    public PluginConfiguration getConfiguration(PluginInfo plugin) throws PluginSystemException {
    	return factory.getConfiguration(plugin);
    }
    
    private static class PluginsLoggerHandler extends java.util.logging.Handler {

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().equals(Level.SEVERE)) {
                getLogger().error(LogCategories.PLUGINS, record.getMessage());
            } else {
                getLogger().info(LogCategories.PLUGINS, record.getMessage());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

    }

}
