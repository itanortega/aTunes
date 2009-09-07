/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context.lastfm.runnables;

import java.awt.Image;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextListener;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmService;
import net.sourceforge.atunes.model.AudioObject;

public class LastFmCoversRunnable implements Runnable {

    ContextListener listener;
    private LastFmService service;
    private List<? extends AlbumInfo> albums;
    private volatile boolean interrupted;
    long id;
    AudioObject audioObject;

    /**
     * Instantiates a new audio scrobbler covers runnable.
     * 
     * @param listener
     *            the listener
     * @param service
     *            the service
     * @param albums
     *            the albums
     * @param id
     *            the id
     */
    public LastFmCoversRunnable(ContextListener listener, LastFmService service, List<? extends AlbumInfo> albums, long id, AudioObject audioObject) {
        this.listener = listener;
        this.service = service;
        this.albums = albums;
        this.id = id;
        this.audioObject = audioObject;
    }

    /**
     * Interrupt.
     */
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public void run() {
        if (albums != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyStartRetrievingCovers(id);
                }
            });
            for (int i = 0; i < albums.size(); i++) {
                final Image img;
                final AlbumInfo album = albums.get(i);
                if (!interrupted) {
                    img = service.getImage(album);
                } else {
                    img = null;
                }

                if (!interrupted) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.notifyCoverRetrieved(album, img, id);
                        }
                    });
                }
            }
        }
    }
}
