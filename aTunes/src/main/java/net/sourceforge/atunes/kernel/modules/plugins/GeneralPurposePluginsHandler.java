/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IGeneralPurposePluginsHandler;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;

public class GeneralPurposePluginsHandler extends AbstractHandler implements IGeneralPurposePluginsHandler {

    /**
     * List of plugins created
     */
    private List<AbstractGeneralPurposePlugin> plugins;

    @Override
    public void applicationFinish() {
        // Deactivate all plugins yet active
        if (plugins != null) {
            for (AbstractGeneralPurposePlugin plugin : plugins) {
                plugin.deactivate();
            }
        }
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            AbstractGeneralPurposePlugin instance = (AbstractGeneralPurposePlugin) PluginsHandler.getInstance().getNewInstance(plugin);
            if (plugins == null) {
                plugins = new ArrayList<AbstractGeneralPurposePlugin>();
            }
            plugins.add(instance);
            // Activate plugin
            instance.activate();
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        // Call to deactivate all plugins and remove from plugins list
        for (Plugin instance : createdInstances) {
            ((AbstractGeneralPurposePlugin) instance).deactivate();
            if (plugins != null) {
                plugins.remove(instance);
            }
        }
    }
}
