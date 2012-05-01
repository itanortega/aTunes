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

package net.sourceforge.atunes.kernel.modules.playlist;

import net.sourceforge.atunes.model.IPlayList;

public interface IPlayListsContainer {

	/**
	 * Returns number of play lists
	 * @return
	 */
	int getPlayListsCount();

	/**
	 * Returns play list
	 * @param i
	 * @return
	 */
	IPlayList getPlayListAt(int i);

	/**
	 * @return active play list
	 */
	int getActivePlayListIndex();

	/**
	 * @return visible play list
	 */
	int getVisiblePlayListIndex();

	/**
	 * Move playlists 
	 * @param from
	 * @param to
	 */
	void movePlayListToPosition(int from, int to);

	/**
	 * Adds playlist
	 * @param position
	 * @param playList
	 */
	void addPlayList(int position, IPlayList playList);

	/**
	 * Clear playlist
	 */
	void clear();

	/**
	 * Returns visible or active play list
	 * @param visible
	 * @return
	 */
	IPlayList getCurrentPlayList(boolean visible);

	/**
	 * Adds play list
	 * @param newPlayList
	 */
	void addPlayList(IPlayList newPlayList);

	/**
	 * Removes play list with given index
	 * @param index
	 */
	void removePlayList(int index);

	/**
	 * Sets visible play list
	 * @param visiblePlayListIndex
	 */
	void setVisiblePlayListIndex(int visiblePlayListIndex);

	/**
	 * Sets active play list
	 * @param activePlayListIndex
	 */
	void setActivePlayListIndex(int activePlayListIndex);

	/**
	 * Sets visible play list as active
	 */
	void setVisiblePlayListActive();

	/**
	 * Play list previous to being filtered
	 * @return
	 */
	IPlayList getNonFilteredPlayList();

	/**
	 * Returns if play list is filtered
	 * @return
	 */
	boolean isFiltered();

	/**
	 * Set filter
	 * @param filter
	 */
	void setFilter(String filter);
}