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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

import org.apache.commons.io.FilenameUtils;

public class RenameAudioFileInNavigationTableAction extends Action {

    private static final long serialVersionUID = 5607758675193509752L;

    public RenameAudioFileInNavigationTableAction() {
        super(LanguageTool.getString("RENAME_AUDIO_FILE_NAME"), ImageLoader.FILE_NAME);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("RENAME_AUDIO_FILE_NAME"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<AudioFile> audioFiles = ControllerProxy.getInstance().getNavigationController().getFilesSelectedInNavigator();
        if (audioFiles.size() == 1) {
            String name = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("RENAME_AUDIO_FILE_NAME"),
                    FilenameUtils.getBaseName(audioFiles.get(0).getFile().getAbsolutePath()), ImageLoader.FILE_NAME.getImage());
            if (name != null && !name.isEmpty()) {
                RepositoryHandler.getInstance().rename(audioFiles.get(0), name);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1;
    }

}
