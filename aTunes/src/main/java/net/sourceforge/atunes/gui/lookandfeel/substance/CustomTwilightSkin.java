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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.skin.TwilightSkin;

public class CustomTwilightSkin extends TwilightSkin implements CustomSubstanceSkin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543287686553802647L;

	public CustomTwilightSkin() {
		super();
	}
	
	@Override
	public Paint getPaintForColorMutableIcon(Component component, boolean isSelected) {
    	if (isSelected) {
    		return component.getForeground();    		
    	} else {
    		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.HEADER).getUltraLightColor();    		
    		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
    	}
	}
	
	@Override
	public Paint getPaintForSpecialControls() {
		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.HEADER).getUltraLightColor();    		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
	}
	
}
