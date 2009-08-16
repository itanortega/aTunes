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

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Shows or hides status bar
 * 
 * @author fleax
 * 
 */
public class ShowStatusBarAction extends Action {

    private static final long serialVersionUID = 2303076465024539635L;

    ShowStatusBarAction() {
        super(LanguageTool.getString("SHOW_STATUS_BAR"), ImageLoader.STATUS_BAR);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("SHOW_STATUS_BAR"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isShowStatusBar());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisualHandler.getInstance().showStatusBar((Boolean) getValue(SELECTED_KEY));
    }

}
