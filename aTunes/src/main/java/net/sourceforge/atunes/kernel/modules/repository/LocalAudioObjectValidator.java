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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.FileFilter;

import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;

import org.apache.commons.io.FilenameUtils;

/**
 * Validates a file is a local audio object
 * 
 * @author alex
 * 
 */
public class LocalAudioObjectValidator implements ILocalAudioObjectValidator {

    private FileFilter validLocalAudioObjectFileFilter;

    /**
     * @param validLocalAudioObjectFileFilter
     */
    public void setValidLocalAudioObjectFileFilter(
	    final FileFilter validLocalAudioObjectFileFilter) {
	this.validLocalAudioObjectFileFilter = validLocalAudioObjectFileFilter;
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    @Override
    public boolean isValidAudioFile(final String file) {
	return isValidAudioFile(new File(file));
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    @Override
    public boolean isValidAudioFile(final File file) {
	return file != null
		&& file.exists()
		&& !file.isDirectory()
		&& isOneOfTheseFormats(file.getName(),
			LocalAudioObjectFormat.values());
    }

    /**
     * Checks if a file is a valid audio file given its name
     * 
     * @param fileName
     * @param formats
     * @return if the file is a valid audio file
     */
    @Override
    public boolean isOneOfTheseFormats(final String fileName,
	    final LocalAudioObjectFormat... formats) {
	if (fileName == null) {
	    return false;
	}
	String extension = FilenameUtils.getExtension(fileName);
	for (LocalAudioObjectFormat format : formats) {
	    if (extension.equalsIgnoreCase(format.getExtension())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public FileFilter getValidLocalAudioObjectFileFilter() {
	return validLocalAudioObjectFileFilter;
    }
}
