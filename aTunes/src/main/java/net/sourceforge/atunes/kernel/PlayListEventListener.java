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

package net.sourceforge.atunes.kernel;

import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

/**
 * The listener interface for receiving playListEvent events.
 */
/**
 * @author alex
 *
 */
public interface PlayListEventListener {

    /**
     * Called when play list is cleared.
     */
    public void playListCleared();

    /**
     * Called when audio object to being played changes.
     * 
     * @param audioObject
     *            the audio object
     */
    public void selectedAudioObjectChanged(AudioObject audioObject);

	/**
	 * Called when audio objects are added to play list
	 * @param playListAudioObjects
	 */
	public void audioObjectsAdded(List<PlayListAudioObject> playListAudioObjects);

	/**
	 * Called when audio objects are removed from play list
	 * @param audioObjectList
	 */
	public void audioObjectsRemoved(List<PlayListAudioObject> audioObjectList);
}
