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

package net.sourceforge.atunes.kernel.modules.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * Class to handle column sets and plugins
 * 
 * @author fleax
 */
public class ColumnSets implements PluginListener {

    /**
     * Column sets
     */
    private static List<AbstractColumnSet> columnSets = new ArrayList<AbstractColumnSet>();

    /**
     * Singleton instance
     */
    private static ColumnSets instance = new ColumnSets();

    public static ColumnSets getInstance() {
        return instance;
    }

    /**
     * Register a new column set
     * 
     * @param columnSet
     */
    protected static void registerColumnSet(AbstractColumnSet columnSet) {
    	columnSets.add(columnSet);
    }

    @SuppressWarnings("rawtypes")
	@Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            for (AbstractColumnSet columnSet : columnSets) {
                columnSet.addNewColumn((AbstractColumn) Context.getBean(IPluginsHandler.class).getNewInstance(plugin));
            }
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        // Take class of column (just the first)
        Class<?> columnClass = null;
        for (Plugin instancedColumn : createdInstances) {
            columnClass = instancedColumn.getClass();
            break;
        }

        for (IColumnSet columnSet : columnSets) {
            columnSet.removeColumn(columnClass);
        }
    }
}
