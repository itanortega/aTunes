/*
 * aTunes 2.0.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;

public class PropertyTableCellRendererCode extends TableCellRendererCode {

    public PropertyTableCellRendererCode() {
    }

    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = superComponent;
        ImageIcon icon = Images.getImage(Images.EMPTY);
        Property val = (Property) value;
        if (val == Property.FAVORITE) {
            icon = Images.getImage(Images.FAVORITE);
        } else if (val == Property.NOT_LISTENED_ENTRY) {
            icon = Images.getImage(Images.NEW_PODCAST_ENTRY);
        } else if (val == Property.DOWNLOADED_ENTRY) {
            icon = Images.getImage(Images.DOWNLOAD_PODCAST);
        } else if (val == Property.OLD_ENTRY) {
            icon = Images.getImage(Images.REMOVE);
        }
        ((JLabel) comp).setIcon(icon);
        ((JLabel) comp).setText(null);
        return comp;
    }

}
