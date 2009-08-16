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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

class PlayListAudioObject {

    private AudioObject audioObject;
    private int position;

    /**
     * @return the audioObject
     */
    protected AudioObject getAudioObject() {
        return audioObject;
    }

    /**
     * @param audioObject
     *            the audioObject to set
     */
    protected void setAudioObject(AudioObject audioObject) {
        this.audioObject = audioObject;
    }

    /**
     * @return the position
     */
    protected int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    protected void setPosition(int position) {
        this.position = position;
    }

    /**
     * Return a list of objects from a list of audio objects and an initial
     * position
     * 
     * @param startPosition
     * @param audioObjects
     * @return
     */
    protected static List<PlayListAudioObject> getList(int startPosition, List<AudioObject> audioObjects) {
        List<PlayListAudioObject> result = new ArrayList<PlayListAudioObject>();
        int positionFromStart = 0;
        for (AudioObject ao : audioObjects) {
            PlayListAudioObject plao = new PlayListAudioObject();
            plao.setAudioObject(ao);
            plao.setPosition(startPosition + positionFromStart);
            result.add(plao);
            positionFromStart++;
        }
        return result;
    }
}
