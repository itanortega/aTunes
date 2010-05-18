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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

public class ProgressBarSeekListener extends MouseAdapter {

	private JSlider progressBar;
	
	public ProgressBarSeekListener(JSlider progressBar) {
		super();
		this.progressBar = progressBar;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
        if (progressBar.isEnabled()) {
        	// Progress bar width is greater than real slider width so calculate value assuming 5 pixels in both left and right of track 
        	int value = (progressBar.getMaximum() * (e.getX() - 5)) / (progressBar.getWidth() - 10);
        	// Force new value to avoid jump to next major tick
        	progressBar.setValue(value);
        	PlayerHandler.getInstance().seekCurrentAudioObject(value);
        }
	}
}