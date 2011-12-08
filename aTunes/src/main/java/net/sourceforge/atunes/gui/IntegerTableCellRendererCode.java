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

package net.sourceforge.atunes.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.ILookAndFeel;

public class IntegerTableCellRendererCode extends AbstractTableCellRendererCode {

    public IntegerTableCellRendererCode(ILookAndFeel lookAndFeel) {
		super(lookAndFeel);
	}

	@Override
    public JComponent getComponent(JComponent c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String theValue = value != null ? value.toString() : "";
        ((JLabel) c).setText(theValue);
        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
        return c;
    }

}
