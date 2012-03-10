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

package net.sourceforge.atunes.gui.views.dialogs.fullScreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;


final class SelectBackgroundActionListener implements ActionListener {
	
	private FullScreenWindow fullScreenWindow;
	
	/**
	 * @param fullScreenWindow
	 */
	SelectBackgroundActionListener(FullScreenWindow fullScreenWindow) {
		this.fullScreenWindow = fullScreenWindow;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		fullScreenWindow.setVisible(false);
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setFileFilter(new FullScreenBackgroundFileFilter());
	    if (fileChooser.showOpenDialog(fullScreenWindow) == JFileChooser.APPROVE_OPTION) {
	        fullScreenWindow.setBackground(fileChooser.getSelectedFile());
	        fullScreenWindow.invalidate();
	        fullScreenWindow.repaint();
	    }
	    fullScreenWindow.setVisible(true);
	}
}