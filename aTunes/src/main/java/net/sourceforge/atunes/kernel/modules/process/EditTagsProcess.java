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

package net.sourceforge.atunes.kernel.modules.process;

import net.sourceforge.atunes.kernel.modules.repository.ImageCache;
import net.sourceforge.atunes.kernel.modules.tags.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.tags.TagFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

/**
 * The Class EditTagsProcess.
 * Process for writing tag to files. Receives AudioFiles to be written and
 * new properties (meta-information)
 */
public class EditTagsProcess extends AbstractChangeTagProcess {

    private EditTagInfo editTagInfo;
    
    /**
     * @param editTagInfo
     */
    public void setEditTagInfo(EditTagInfo editTagInfo) {
		this.editTagInfo = editTagInfo;
	}

    @Override
    protected void changeTag(ILocalAudioObject audioFile) {
        ITag newTag = TagFactory.getNewTag(audioFile, editTagInfo);
        ITag oldTag = audioFile.getTag();

        byte[] c = null;
        boolean shouldEditCover = editTagInfo.isTagEdited("COVER");
        if (shouldEditCover) {
            Object cover = editTagInfo.get("COVER");
            if (cover != null) {
                c = (byte[]) cover;
            }
            newTag.setInternalImage((oldTag != null && oldTag.hasInternalImage() && !shouldEditCover) || (shouldEditCover && cover != null));
        }
        getTagHandler().setTag(audioFile, newTag, shouldEditCover, c);
        ImageCache.getImageCache().clear(audioFile);
    }
}
