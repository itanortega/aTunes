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

package net.sourceforge.atunes.gui.frame;

import java.awt.Dimension;
import java.awt.Point;

import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * Calculates window location
 * @author alex
 *
 */
class WindowLocationCalculator {

	/**
	 * Returns window location based on frame state or null
	 * @param frameState
	 * @return
	 */
	public Point getWindowLocation(IFrameState frameState) {
		// Set window location
        Point windowLocation = null;
        if (frameState != null && frameState.getXPosition() >= 0 && frameState.getYPosition() >= 0) {
            windowLocation = new Point(frameState.getXPosition(), frameState.getYPosition());
        } else {
        	// Setting location centered in screen according to default size
        	Dimension defSize = new WindowSizeCalculator().getDefaultWindowSize();
            windowLocation = new Point((GuiUtils.getDeviceWidth() - defSize.width) / 2, (GuiUtils.getDeviceHeight() - defSize.height) / 2);
        }
        
        return windowLocation;
	}
}