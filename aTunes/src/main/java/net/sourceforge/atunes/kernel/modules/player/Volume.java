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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.fullscreen.FullScreenHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public final class Volume {

    private Volume() {

    }

    public static void setVolume(int volume, boolean saveVolume) {
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }
        final int finalVolume = volume;
        if (saveVolume) {
        	ApplicationState.getInstance().setVolume(finalVolume);
        }
        PlayerHandler.getInstance().setVolume(finalVolume);

        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FullScreenHandler.getInstance().setVolume(finalVolume);
                }
            });
        } else {
        	FullScreenHandler.getInstance().setVolume(finalVolume);
        }
    }
    
    public static void setVolume(int volume) {
    	setVolume(volume, true);
    }
}
