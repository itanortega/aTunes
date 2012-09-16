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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * Extracts a picture of an audio object
 * @author alex
 *
 */
public class ExtractPictureAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = -8618297820141610193L;

    private IDialogFactory dialogFactory;
    
    private IOSManager osManager;
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * Default constructor
     */
    public ExtractPictureAction() {
        super(I18nUtils.getString("EXTRACT_PICTURE"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EXTRACT_PICTURE"));
    }

    @Override
    protected void executeAction(List<ILocalAudioObject> objects) {
    	IFileSelectorDialog dialog = dialogFactory.newDialog(IFileSelectorDialog.class);
    	FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith("PNG");
			}
			
			@Override
			public String toString() {
				return ".png";
			}
		};
		dialog.setFileFilter(filter);
		
		File selectedFile = dialog.saveFile(osManager.getUserHome());
        if (!selectedFile.getName().toUpperCase().endsWith("PNG")) {
        	selectedFile = new File(StringUtils.getString(selectedFile.getAbsolutePath(), ".png"));
        }

        // Export only first picture
        try {
			AudioFilePictureUtils.savePictureToFile(objects.get(0), selectedFile);
		} catch (ImageWriteException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return selection.size() == 1 && selection.get(0) instanceof ILocalAudioObject && ((ILocalAudioObject) selection.get(0)).hasInternalPicture();
    }
}
