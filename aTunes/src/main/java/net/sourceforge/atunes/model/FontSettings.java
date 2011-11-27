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

package net.sourceforge.atunes.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;

import net.sourceforge.atunes.kernel.modules.state.beans.FontBean;

public class FontSettings implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8438133427543514976L;
	FontBean font;
    private boolean useFontSmoothing;
    private boolean useFontSmoothingSettingsFromOs;

    @ConstructorProperties( { "font", "useFontSmoothing", "useFontSmoothingSettingsFromOs" })
    public FontSettings(FontBean font, boolean useFontSmoothing, boolean useFontSmoothingSettingsFromOs) {
        super();
        this.font = font;
        this.useFontSmoothing = useFontSmoothing;
        this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
    }

    public FontSettings() {
    }

    public void setFont(FontBean font) {
        this.font = font;
    }

    public FontBean getFont() {
        return font;
    }

    public void setUseFontSmoothing(boolean useFontSmoothing) {
        this.useFontSmoothing = useFontSmoothing;
    }

    public boolean isUseFontSmoothing() {
        return useFontSmoothing;
    }

    public void setUseFontSmoothingSettingsFromOs(boolean useFontSmoothingSettingsFromOs) {
        this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
    }

    public boolean isUseFontSmoothingSettingsFromOs() {
        return useFontSmoothingSettingsFromOs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((font == null) ? 0 : font.hashCode());
        result = prime * result + (useFontSmoothing ? 1231 : 1237);
        result = prime * result + (useFontSmoothingSettingsFromOs ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FontSettings other = (FontSettings) obj;
        if (font == null) {
            if (other.font != null) {
                return false;
            }
        } else if (!font.equals(other.font)) {
            return false;
        }
        if (useFontSmoothing != other.useFontSmoothing) {
            return false;
        }
        if (useFontSmoothingSettingsFromOs != other.useFontSmoothingSettingsFromOs) {
            return false;
        }
        return true;
    }

}