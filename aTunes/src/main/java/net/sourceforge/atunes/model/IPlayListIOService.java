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

package net.sourceforge.atunes.model;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileFilter;


/**
 * Includes services to manage play list files (for example m3u files)
 * @author alex
 *
 */
public interface IPlayListIOService {

	/**
	 * Returns a list of files contained in a list of file names.
	 * @param fileNames
	 * @return
	 */
	List<IAudioObject> getAudioObjectsFromFileNamesList(List<String> fileNames);

	/**
	 * Returns an AudioObject given a resource name or instantiates it if does
	 * not exist. A resource can be a file or URL at this moment
	 * @param resourceName
	 * @return
	 */
	IAudioObject getAudioObjectOrCreate(String resourceName);

	/**
	 * Returns a list of files contained in a play list file.
	 * @param file
	 * @return
	 */
	List<IAudioObject> getFilesFromList(File file);

	/**
	 * FileFilter to be used when loading and saving a play list file.
	 * 
	 * @return the playlist file filter
	 */
	FileFilter getPlaylistFileFilter();

	/**
	 * Checks if is valid play list.
	 * 
	 * @param playListFile
	 *            the play list file
	 * 
	 * @return true, if is valid play list
	 */
	boolean isValidPlayList(String playListFile);

	/**
	 * Checks if is valid play list.
	 * 
	 * @param playListFile
	 *            the play list file
	 * 
	 * @return true, if is valid play list
	 */
	boolean isValidPlayList(File playListFile);

	/**
	 * This function reads the filenames from the playlist file (m3u). It will
	 * return all filenames with absolute path. For this playlists with relative
	 * pathname must be detected and the path must be added. Current problem of
	 * this implementation is clearly the charset used. Java reads/writes in the
	 * charset used by the OS! But for many *nixes this is UTF8, while Windows
	 * will use CP1252 or similar. So, as long as we have the same charset
	 * encoding or do not use any special character playlists will work
	 * (absolute filenames with a pathname incompatible with the current OS are
	 * not allowed), but as soon as we have say french accents in the filename a
	 * playlist created under an application using CP1252 will not import
	 * correctly on a UTF8 system (better: the files with accents in their
	 * filename will not).
	 * 
	 * Only playlist with local files have been tested! Returns a list of file
	 * names contained in a play list file
	 * 
	 * @param file
	 *            The playlist file
	 * 
	 * @return Returns an List of files of the playlist as String.
	 */
	List<String> read(File file);

	/**
	 * Writes a play list to a file.
	 * 
	 * @param playlist
	 * @param file
	 * @param osManager
	 * @return
	 */
	boolean write(IPlayList playlist, File file);

	/**
	 * Checks file name is a valid play list file (checks extension)
	 * @param file
	 * @return file checked
	 */
	File checkPlayListFileName(File file);

}