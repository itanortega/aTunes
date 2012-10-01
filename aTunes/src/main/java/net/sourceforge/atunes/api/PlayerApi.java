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

package net.sourceforge.atunes.api;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.PlaybackState;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class PlayerApi {

    private PlayerApi() {

    }

    /**
     * Stops current object being played
     */
    public static void stop() {
        Context.getBean(IPlayerHandler.class).stopCurrentAudioObject(true);
    }

    /**
     * Starts playing previous object
     */
    public static void previous() {
        Context.getBean(IPlayerHandler.class).playPreviousAudioObject();
    }

    /**
     * Starts playing next object
     */
    public static void next() {
        Context.getBean(IPlayerHandler.class).playNextAudioObject();
    }

    /**
     * Plays or pauses current object
     */
    public static void play() {
        Context.getBean(IPlayerHandler.class).resumeOrPauseCurrentAudioObject();
    }

    /**
     * Returns current audio object in active play list
     * 
     * @return
     */
    public static IAudioObject getCurrentAudioObject() {
        return Context.getBean(IPlayListHandler.class).getCurrentAudioObjectFromCurrentPlayList();
    }

    /**
     * Returns the current state of player: playing, paused...
     * 
     * @return
     */
    public static PlaybackState getCurrentPlaybackState() {
        return Context.getBean(IPlayerHandler.class).getPlaybackState();
    }

    /**
     * Returns current audio object time played in milliseconds
     * 
     * @return
     */
    public static long getCurrentAudioObjectPlayedTime() {
        return Context.getBean(IPlayerHandler.class).getCurrentAudioObjectPlayedTime();
    }

    /**
     * Returns length of current audio object in milliseconds
     * 
     * @return
     */
    public static long getCurrentAudioObjectLength() {
        return Context.getBean(IPlayerHandler.class).getCurrentAudioObjectLength();
    }

    /**
     * Raise volume
     */
    public static void volumeUp() {
        Context.getBean(IPlayerHandler.class).volumeUp();
    }

    /**
     * Lower volume
     */
    public static void volumeDown() {
        Context.getBean(IPlayerHandler.class).volumeDown();
    }
}
