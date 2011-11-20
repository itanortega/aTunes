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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.ArtistFavoriteImageIcon;
import net.sourceforge.atunes.gui.images.ArtistImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IFavoritesHandler;

public class ArtistTreeCellDecorator extends AbstractTreeCellDecorator {

	private IFavoritesHandler favoritesHandler;
	
	private ArtistImageIcon artistImageIcon;
	
    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject, boolean isSelected) {
        if (userObject instanceof Artist) {
        	Artist artist = (Artist) userObject;
            if (!state.isShowFavoritesInNavigator() || !favoritesHandler.getFavoriteArtistsInfo().containsKey(artist.getName())) {
          		((JLabel) component).setIcon(artistImageIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
            } else {
                ((JLabel) component).setIcon(ArtistFavoriteImageIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
            }
        }
        return component;
    }
    
    /**
     * @param favoritesHandler
     */
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
    
    /**
     * @param artistImageIcon
     */
    public void setArtistImageIcon(ArtistImageIcon artistImageIcon) {
		this.artistImageIcon = artistImageIcon;
	}
}
