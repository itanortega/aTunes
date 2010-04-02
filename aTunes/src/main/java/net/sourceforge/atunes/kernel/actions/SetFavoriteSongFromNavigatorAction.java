/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class SetFavoriteSongFromNavigatorAction extends AbstractActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = 4023700964403110853L;

    public SetFavoriteSongFromNavigatorAction() {
        super(I18nUtils.getString("SET_FAVORITE_SONG"), Images.getImage(Images.FAVORITE), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SET_FAVORITE_SONG"));
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        FavoritesHandler.getInstance().addFavoriteSongs(objects);
        ControllerProxy.getInstance().getNavigationController().refreshTable();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return true;
    }
}
