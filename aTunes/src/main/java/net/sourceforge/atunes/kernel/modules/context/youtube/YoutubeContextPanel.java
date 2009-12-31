/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Panel to show youtube videos
 * 
 * @author alex
 * 
 */
public class YoutubeContextPanel extends ContextPanel {

    private List<ContextPanelContent> contents;

    @Override
    protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
        return Images.getImage(Images.YOUTUBE);
    }

    @Override
    public String getContextPanelName() {
        return "YOUTUBE";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        return "YouTube";
    }

    @Override
    protected List<ContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<ContextPanelContent>();
            contents.add(new YoutubeContent());
        }
        return contents;
    }

    @Override
    protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
        return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable());
    }

}
