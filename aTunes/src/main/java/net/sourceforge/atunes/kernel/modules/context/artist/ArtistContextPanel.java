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
package net.sourceforge.atunes.kernel.modules.context.artist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Context panel to show album information
 * 
 * @author alex
 * 
 */
public class ArtistContextPanel extends ContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;

    private List<ContextPanelContent> contents;

    @Override
    protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
        return ImageLoader.getImage(ImageLoader.ARTIST);
    }

    @Override
    public String getContextPanelName() {
        return "ARTIST";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        return LanguageTool.getString("ARTIST");
    }

    @Override
    protected List<ContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<ContextPanelContent>();
            contents.add(new ArtistBasicInfoContent());
            contents.add(new ArtistAlbumsContent());
        }
        return contents;
    }

    @Override
    protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
        return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable());
    }

}
