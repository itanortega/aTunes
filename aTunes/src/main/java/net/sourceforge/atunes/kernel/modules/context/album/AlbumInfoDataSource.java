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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

import org.apache.sanselan.ImageWriteException;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class AlbumInfoDataSource implements IContextInformationSource {

    private IState state;
    
    private IWebServicesHandler webServicesHandler;
    
    private IOSManager osManager;
    
    private IAudioObjectImageLocator audioObjectImageLocator;
    
    private IAlbumInfo albumInfo;
    
    private ImageIcon image;
    
    private IAudioObject audioObject;
    
    @Override
    public void getData(IAudioObject audioObject) {
    	this.audioObject = audioObject;
    	this.albumInfo = getAlbumInfoData(audioObject);
		this.image = getImageData(albumInfo, audioObject);
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
    public IAlbumInfo getAlbumInfo() {
		return albumInfo;
	}
    
    /**
     * @return
     */
    public ImageIcon getImage() {
		return image;
	}
    
    /**
     * Returns album information
     * 
     * @param audioObject
     * @return
     */
    private IAlbumInfo getAlbumInfoData(IAudioObject audioObject) {
        // Get album info
        IAlbumInfo album = webServicesHandler.getAlbum(audioObject.getAlbumArtistOrArtist(), audioObject.getAlbum());

        // If album was not found try to get an album from the same artist that match
        if (album == null) {
            album = tryToFindAnotherAlbumFromSameArtist(audioObject);
        }

        return album;
        // Get image of album or custom image for audio object
    }

	/**
	 * @param audioObject
	 * @return
	 */
	private IAlbumInfo tryToFindAnotherAlbumFromSameArtist(IAudioObject audioObject) {
		
		// Wait a second to prevent IP banning
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		List<IAlbumInfo> albums = null;
		if (!UnknownObjectCheck.isUnknownArtist(audioObject.getAlbumArtistOrArtist())) {
		    // Get album list
		    albums = getAlbumList(audioObject.getAlbumArtistOrArtist());
		}

		if (albums != null) {
		    // Try to find an album which fits 
		    IAlbumInfo matchingAlbum = analyseAlbums(audioObject, albums);
		    if (matchingAlbum != null) {
		        // Get full information for album
		        matchingAlbum = webServicesHandler.getAlbum(matchingAlbum.getArtist(), matchingAlbum.getTitle());
		        if (matchingAlbum != null) {
		            return matchingAlbum;
		        }
		    }
		}
		return null;
	}

	/**
	 * @param audioObject
	 * @param albums
	 * @return
	 */
	private IAlbumInfo analyseAlbums(IAudioObject audioObject, List<IAlbumInfo> albums) {
		IAlbumInfo auxAlbum = null;
		int i = 0;
		while (auxAlbum == null && i < albums.size()) {
		    IAlbumInfo a = albums.get(i);
		    StringTokenizer st = new StringTokenizer(a.getTitle(), " ");
		    boolean matches = true;
		    int tokensAnalyzed = 0;
		    while (st.hasMoreTokens() && matches) {
		        String t = st.nextToken();
		        if (forbiddenToken(t)) { // Ignore album if contains forbidden chars
		            matches = false;
		            break;
		        }
		        if (!validToken(t)) { // Ignore tokens without alphanumerics
		            if (tokensAnalyzed == 0 && !st.hasMoreTokens()) {
		                matches = false;
		            } else {
		                continue;
		            }
		        }
		        if (!audioObject.getAlbum().toLowerCase().contains(t.toLowerCase())) {
		            matches = false;
		        }
		        tokensAnalyzed++;
		    }
		    if (matches) {
		        auxAlbum = a;
		    }
		    i++;
		}
		return auxAlbum;
	}

	/**
	 * @param artist
	 * @return
	 */
	private List<IAlbumInfo> getAlbumList(String artist) {
		IAlbumListInfo albumList = webServicesHandler.getAlbumList(artist);
		if (albumList != null) {
		    return albumList.getAlbums();
		}
		return null;
	}

    /**
     * Returns image from lastfm or from custom image
     * 
     * @param albumInfo
     * @param audioObject
     * @return
     */
    private ImageIcon getImageData(IAlbumInfo albumInfo, IAudioObject audioObject) {
        ImageIcon image = null;
        if (albumInfo != null) {
            image = webServicesHandler.getAlbumImage(albumInfo);
            // This data source should only be used with audio files but anyway check if audioObject is an LocalAudioObject before save picture
            if (audioObject instanceof ILocalAudioObject) {
                savePicture(image, (ILocalAudioObject) audioObject);
            }
        } else {
            image = audioObjectImageLocator.getImage(audioObject, ImageSize.SIZE_MAX);
        }
        
        return image;
    }

    /**
     * Saves an image related to an audio file from a web service in the folder
     * where audio file is
     * 
     * @param img
     * @param file
     */
    private void savePicture(ImageIcon img, ILocalAudioObject file) {
        if (img != null && state.isSaveContextPicture()) { // save image in folder of file
            String imageFileName = AudioFilePictureUtils.getFileNameForCover(file, osManager);

            File imageFile = new File(imageFileName);
            if (!imageFile.exists()) {
                // Save picture
                try {
                    ImageUtils.writeImageToFile(img.getImage(), imageFileName);
                } catch (IOException e) {
                    Logger.error(e);
                } catch (ImageWriteException e) {
                    Logger.error(e);
				}
            }
        }
    }

    /**
     * Valid token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean validToken(String t) {
        return t.matches("[A-Za-z]+");
        //t.contains("(") || t.contains(")")
    }

    /**
     * Forbidden token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean forbiddenToken(String t) {
        return t.contains("/");
    }
    
    public final void setState(IState state) {
		this.state = state;
	}
    
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    /**
     * @param audioObjectImageLocator
     */
    public void setAudioObjectImageLocator(IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectImageLocator = audioObjectImageLocator;
	}
}
