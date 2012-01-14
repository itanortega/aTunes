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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Image;

import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class ArtistInfoDataSource implements IContextInformationSource {

	private IState state;
	
    private IWebServicesHandler webServicesHandler;
    
    private IAudioObject audioObject;
    
    private String wikiText;
    
    private String wikiUrl;
    
    private String artistName;
    
    private String artistUrl;
    
    private Image artistImage;
    
    private IAlbumListInfo albumList;
    
    @Override
    public void getData(IAudioObject audioObject) {
    	this.audioObject = audioObject;
    	this.wikiText = getWikiTextData(audioObject);
        this.wikiUrl = getWikiUrlData(audioObject);
        this.artistImage = getArtistImageData(audioObject);
        this.albumList = getAlbumListData(audioObject);
        if (albumList != null && !albumList.getAlbums().isEmpty()) {
        	this.artistName = albumList.getArtist();
        	this.artistUrl = albumList.getAlbums().get(0).getArtistUrl();
        }

    }
    
    /**
     * @return
     */
    public IAlbumListInfo getAlbumList() {
		return albumList;
	}
    
    /**
     * Return album list for artist
     * 
     * @param audioObject
     * @return
     */
    private IAlbumListInfo getAlbumListData(IAudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return webServicesHandler.getAlbumList(audioObject.getArtist(), state.isHideVariousArtistsAlbums(),
                    state.getMinimumSongNumberPerAlbum());
        }
        return null;
    }

    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @return
     */
    public IAudioObject getAudioObject() {
		return audioObject;
	}
    
    /**
     * @return
     */
    public String getWikiText() {
		return wikiText;
	}
    
    /**
     * @return
     */
    public String getWikiUrl() {
		return wikiUrl;
	}
    
    /**
     * @return
     */
    public Image getArtistImage() {
		return artistImage;
	}
    
    /**
     * @return
     */
    public String getArtistName() {
		return artistName;
	}
    
    /**
     * @return
     */
    public String getArtistUrl() {
		return artistUrl;
	}
    
    /**
     * Return wiki text for artist
     * 
     * @param audioObject
     * @return
     */
    private String getWikiTextData(IAudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return webServicesHandler.getBioText(audioObject.getArtist());
        }
        return null;
    }

    /**
     * Return wiki url for artist
     * 
     * @param audioObject
     * @return
     */
    private String getWikiUrlData(IAudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return webServicesHandler.getBioURL(audioObject.getArtist());
        }
        return null;
    }

    /**
     * Returns image for artist
     * 
     * @param audioObject
     * @return
     */
    private Image getArtistImageData(IAudioObject audioObject) {
        return webServicesHandler.getArtistImage(audioObject.getArtist());
    }

    /**
     * @param webServicesHandler
     */
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

}
