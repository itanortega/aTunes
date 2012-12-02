/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens OS file browser with folder of selected elements
 * 
 * @author fleax
 * 
 */
public class OpenFolderAction extends
	AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 1682289345922375850L;

    private IDesktop desktop;

    /**
     * @param desktop
     */
    public void setDesktop(final IDesktop desktop) {
	this.desktop = desktop;
    }

    /**
     * Default constructor
     */
    public OpenFolderAction() {
	super(I18nUtils.getString("OPEN_FOLDER"));
    }

    @Override
    protected void executeAction(final List<ILocalAudioObject> objects) {
	HashSet<File> foldersToOpen = new HashSet<File>();

	// Get folders ...
	for (ILocalAudioObject ao : objects) {
	    if (!foldersToOpen.contains(ao.getFile().getParentFile())) {
		foldersToOpen.add(ao.getFile().getParentFile());
	    }
	}

	// ... then open
	for (File folder : foldersToOpen) {
	    desktop.openFile(folder);
	}
    }
}
