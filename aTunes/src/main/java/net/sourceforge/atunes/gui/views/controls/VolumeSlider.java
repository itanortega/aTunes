/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.Dimension;

import javax.swing.JSlider;

/**
 * Slider to control volume
 * 
 * @author alex
 * 
 */
public final class VolumeSlider extends JSlider {

	private static final long serialVersionUID = -7802263658163323018L;

	/**
	 * @param volumeSliderSize
	 */
	public void setVolumeSliderSize(final Dimension volumeSliderSize) {
		setMinimumSize(volumeSliderSize);
		setPreferredSize(volumeSliderSize);
	}

	/**
	 * Slider to control volume
	 * 
	 * @param state
	 * @param playerHandler
	 */
	public VolumeSlider() {
		super();
		setOpaque(false);
		setMinimum(0);
		setMaximum(100);
		setValue(50);
		setFocusable(false);
	}
}
