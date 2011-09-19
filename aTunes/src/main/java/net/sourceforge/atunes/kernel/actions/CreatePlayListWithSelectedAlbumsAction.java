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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.repository.AudioObjectComparator;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action creates a new play list with all songs of the album that is
 * currently selected in play list
 * 
 * If more than one album is selected, creates one play list for each one
 * 
 * @author fleax
 * 
 */
public class CreatePlayListWithSelectedAlbumsAction extends AbstractActionOverSelectedObjects<IAudioObject> {

    private static final long serialVersionUID = -2917908051161952409L;

    CreatePlayListWithSelectedAlbumsAction() {
        super(I18nUtils.getString("SET_ALBUM_AS_PLAYLIST"), IAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ALBUM_BUTTON_TOOLTIP"));
        setEnabled(false);
    }

    @Override
    protected void performAction(List<IAudioObject> objects) {
        // Get selected albums from play list
        List<Album> selectedAlbums = new ArrayList<Album>();
        for (IAudioObject ao : objects) {
            String artistName = ao.getArtist();
            String album = ao.getAlbum();
            Artist a = RepositoryHandler.getInstance().getArtist(artistName);
            if (a != null) {
                Album alb = a.getAlbum(album);
                if (alb != null && !selectedAlbums.contains(alb)) {
                    selectedAlbums.add(alb);
                }
            }
        }

        // Create one play list for each album
        for (Album album : selectedAlbums) {
            List<ILocalAudioObject> audioObjects = RepositoryHandler.getInstance().getAudioFilesForAlbums(Collections.singletonMap(album.getName(), album));
            AudioObjectComparator.sort(audioObjects);

            // Create a new play list with album as name and audio objects
            Context.getBean(IPlayListHandler.class).newPlayList(album.getName(), audioObjects);
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }
}
