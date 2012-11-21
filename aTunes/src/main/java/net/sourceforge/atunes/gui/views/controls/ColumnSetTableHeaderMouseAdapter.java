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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.GuiUtils;

final class ColumnSetTableHeaderMouseAdapter extends MouseAdapter {
    private final JPopupMenu rightMenu;
    private final JTable table;

    ColumnSetTableHeaderMouseAdapter(JPopupMenu rightMenu, JTable table) {
        this.rightMenu = rightMenu;
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Use right button to arrange columns
        if (GuiUtils.isSecondaryMouseButton(e)) {
            rightMenu.show(table.getTableHeader(), e.getX(), e.getY());
        }
    }
}