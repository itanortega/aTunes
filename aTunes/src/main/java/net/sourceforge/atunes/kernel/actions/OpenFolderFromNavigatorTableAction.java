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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens OS file browser with folder of selected elements
 * 
 * @author fleax
 * 
 */
public class OpenFolderFromNavigatorTableAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

	private static final long serialVersionUID = 8251208528513562627L;

	private IDesktop desktop;

	private IOSManager osManager;

	private ILocalAudioObjectFilter localAudioObjectFilter;

	/**
	 * @param localAudioObjectFilter
	 */
	public void setLocalAudioObjectFilter(
			final ILocalAudioObjectFilter localAudioObjectFilter) {
		this.localAudioObjectFilter = localAudioObjectFilter;
	}

	/**
	 * Default constructor
	 */
	public OpenFolderFromNavigatorTableAction() {
		super(I18nUtils.getString("OPEN_FOLDER"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("OPEN_FOLDER"));
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(final List<IAudioObject> selection) {
		return sameParentFile(localAudioObjectFilter.getLocalAudioObjects(selection));
	}

	/**
	 * Checks if a collection of files have the same parent file.
	 * 
	 * @param c
	 *            collection of files
	 * @return if a collection of files have the same parent file
	 */
	private boolean sameParentFile(final Collection<? extends ILocalAudioObject> c) {
		Set<File> set = new HashSet<File>();
		for (ILocalAudioObject af : c) {
			set.add(af.getFile().getParentFile());
		}
		return set.size() == 1;
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
			desktop.openFile(folder, osManager);
		}
	}
}