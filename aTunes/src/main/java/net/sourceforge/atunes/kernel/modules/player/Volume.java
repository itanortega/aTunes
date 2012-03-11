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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;

final class Volume {

    private Volume() {
    }

    /**
     * @param volume
     * @param saveVolume
     * @param state
     * @param playerHandler
     */
    public static void setVolume(int volume, boolean saveVolume, IState state, IPlayerHandler playerHandler) {
        applyVolume(saveVolume, state, playerHandler, getVolumeLevel(volume));
    }

	/**
	 * @param saveVolume
	 * @param state
	 * @param playerHandler
	 * @param finalVolume
	 */
	private static void applyVolume(boolean saveVolume, IState state,
			IPlayerHandler playerHandler, final int finalVolume) {
		if (saveVolume) {
        	state.setVolume(finalVolume);
        }
        playerHandler.setVolume(finalVolume);

        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	Context.getBean(IFullScreenHandler.class).setVolume(finalVolume);
                }
            });
        } else {
        	Context.getBean(IFullScreenHandler.class).setVolume(finalVolume);
        }
	}

	/**
	 * @param volume
	 * @return
	 */
	private static int getVolumeLevel(int volume) {
		int volumeLevel = volume;
        if (volumeLevel < 0) {
            volumeLevel = 0;
        } else if (volumeLevel > 100) {
            volumeLevel = 100;
        }
		return volumeLevel;
	}
    
    /**
     * @param volume
     * @param state
     * @param playerHandler
     */
    public static void setVolume(int volume, IState state, IPlayerHandler playerHandler) {
    	setVolume(volume, true, state, playerHandler);
    }
}
