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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

public class RadioImageIcon {

	private static final int SMALL_SIZE = 16;
	private static final int MEDIUM_SIZE = 70;
	private static final int LARGE_SIZE = 150;
	
	private static ImageIcon smallImageIcon; // Cached as it's heavily used

	public static ImageIcon getSmallIcon() {
		if (smallImageIcon == null) {
			smallImageIcon = getSmallIcon(null, SMALL_SIZE, SMALL_SIZE); 
		}
		return smallImageIcon;
	}

	public static ImageIcon getSmallIcon(Paint paint) {
		if (paint == null) {
			return getSmallIcon();
		} else {
			return getSmallIcon(paint, SMALL_SIZE, SMALL_SIZE);
		}
	}

	private static ImageIcon getSmallIcon(Paint color, int w, int h) {
		int marginX = w / 10;
		int marginTopY = h / 2;
		int marginBottomY = h / 10;
		int arc = 4;
		Area a = new Area(new RoundRectangle2D.Float(marginX, marginTopY, w - 2 * marginX, h - marginTopY - marginBottomY, arc, arc));

		int antennaWidth = w / 9;
		Polygon p = new Polygon();
		p.addPoint(marginX + antennaWidth, marginBottomY);
		p.addPoint(marginX + 2 * antennaWidth, marginBottomY);
		p.addPoint(w - marginX - antennaWidth, h - marginTopY);
		p.addPoint(w - marginX - 2 * antennaWidth, h - marginTopY);
		
		a.add(new Area(p));
		
		int insideMargin = w / 8;
		marginX = marginX + insideMargin;
		marginBottomY = marginBottomY + 2 * insideMargin;
		marginTopY = marginTopY + insideMargin;
		a.subtract(new Area(new Rectangle(marginX, marginTopY, w - 2 * marginX, h - marginTopY - marginBottomY)));
		
        return IconGenerator.generateIcon(color, w, h, a);
	}

	public static ImageIcon getIcon() {
		return getIcon(null, MEDIUM_SIZE, MEDIUM_SIZE);
	}
	
	public static ImageIcon getIcon(Paint color) {
		return getIcon(color, MEDIUM_SIZE, MEDIUM_SIZE);
	}

	public static ImageIcon getBigIcon() {
		return getIcon(null, LARGE_SIZE, LARGE_SIZE);
	}
	
	public static ImageIcon getBigIcon(Paint color) {
		return getIcon(color, LARGE_SIZE, LARGE_SIZE);
	}

	private static ImageIcon getIcon(Paint color, int w, int h) {
		int marginX = w / 10;
		int marginTopY = h / 2;
		int marginBottomY = h / 10;
		int arc = 4;
		Area a = new Area(new RoundRectangle2D.Float(marginX, marginTopY, w - 2 * marginX, h - marginTopY - marginBottomY, arc, arc));

		int antennaWidth = w / 12;
		Polygon p = new Polygon();
		p.addPoint(marginX + antennaWidth, marginBottomY);
		p.addPoint(marginX + 2 * antennaWidth, marginBottomY);
		p.addPoint(w - marginX - antennaWidth, h - marginTopY);
		p.addPoint(w - marginX - 2 * antennaWidth, h - marginTopY);
		
		a.add(new Area(p));
		
		int insideMargin = w / 16;
		marginX = marginX + insideMargin;
		marginBottomY = marginBottomY + 2 * insideMargin;
		marginTopY = marginTopY + insideMargin;
		a.subtract(new Area(new Rectangle(marginX, marginTopY, w - 2 * marginX, h - marginTopY - marginBottomY)));
		
        return IconGenerator.generateIcon(color, w, h, a);
	}
}
