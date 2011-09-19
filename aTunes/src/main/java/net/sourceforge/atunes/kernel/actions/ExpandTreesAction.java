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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class ExpandTreesAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2632581565309673327L;

    ExpandTreesAction() {
        super(I18nUtils.getString("EXPAND"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EXPAND"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (INavigationView view : getBean(INavigationHandler.class).getNavigationViews()) {
            GuiUtils.expandTree(view.getTree());
        }
    }

}
