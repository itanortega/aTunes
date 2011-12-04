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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * Data Source for artist popular tracks
 * 
 * @author alex
 * 
 */
public class ArtistPopularTracksDataSource implements IContextInformationSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_TRACKS = "TRACKS";

    private IWebServicesHandler webServicesHandler;
    
    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            IAudioObject audioObject = (IAudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            IArtistTopTracks topTracks = getTopTracks(audioObject);
            if (topTracks != null) {
            	result.put(OUTPUT_TRACKS, topTracks);
            }
        }
        return result;
    }

    private IArtistTopTracks getTopTracks(IAudioObject audioObject) {
    	if (!UnknownObjectCheck.isUnknownArtist(audioObject.getArtist())) {
    		return webServicesHandler.getTopTracks(audioObject.getArtist());
    	}
    	return null;
    }
    
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
}
