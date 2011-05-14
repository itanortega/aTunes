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

package net.sourceforge.atunes.gui.images;

import java.awt.Paint;
import java.awt.Shape;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.geom.Star2D;

public class NewImageIcon {

	private static final int SIZE = 18;
	private static final int STAR_SIZE = 16;
	
	public static ImageIcon getIcon() {
		return getIcon(null);
	}
	
	public static ImageIcon getIcon(Paint color) {
		Shape star = new Star2D(SIZE / 2, SIZE / 2, STAR_SIZE - 12, STAR_SIZE - 8, 9);
        return IconGenerator.generateIcon(color, SIZE, SIZE, star);
	}
}