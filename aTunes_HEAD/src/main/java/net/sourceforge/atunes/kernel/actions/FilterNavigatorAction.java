/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.panels.NavigationFilterPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class FilterNavigatorAction extends Action {

    private static final long serialVersionUID = 8983724480558125905L;

    FilterNavigatorAction() {
        super(LanguageTool.getString("FILTER"), ImageLoader.SEARCH);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("FILTER"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NavigationFilterPanel filterPanel = ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getTreeFilterPanel();

        if (ControllerProxy.getInstance().getNavigationController().getPopupMenuCaller() == ControllerProxy.getInstance().getNavigationController().getNavigationPanel()
                .getNavigationTable()) {
            filterPanel = ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getTableFilterPanel();
        }

        // Toggle visibility
        filterPanel.setVisible(!filterPanel.isVisible());

        // Request focus
        filterPanel.requestFocus();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return true;
    }

}
