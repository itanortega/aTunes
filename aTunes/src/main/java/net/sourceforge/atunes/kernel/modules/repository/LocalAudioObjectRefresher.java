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

import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.utils.Logger;

/**
 * Refreshes local audio object of a repository
 * @author alex
 *
 */
public class LocalAudioObjectRefresher {

	private IState state;
	
	private IStatisticsHandler statisticsHandler;
	
	private ILocalAudioObjectFactory localAudioObjectFactory;
	
	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
	
	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * Refresh file
	 * @param repository
	 * @param file
	 */
	public void refreshFile(IRepository repository, ILocalAudioObject file) {
		try {
			// Get old tag
			ITag oldTag = file.getTag();
			
			// Update tag
			localAudioObjectFactory.refreshAudioObject(file);
			
			// Update repository
			new RepositoryFiller(repository, state).refreshAudioFile(file, oldTag);

			// Compare old tag with new tag
			ITag newTag = file.getTag();
			if (newTag != null) {
				compareTags(repository, oldTag, newTag);
			}

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	/**
	 * @param repository
	 * @param oldTag
	 * @param newTag
	 */
	private void compareTags(IRepository repository, ITag oldTag, ITag newTag) {
		boolean artistChanged = isArtistChanged(oldTag, newTag);
		boolean albumChanged = isAlbumChanged(oldTag, newTag);
		boolean oldArtistRemoved = false;
		
		if (artistChanged) {
			oldArtistRemoved = updateArtistStatistics(repository, oldTag, newTag, oldArtistRemoved);
		}
		if (albumChanged) {
			updateAlbumStatistics(repository, oldTag, newTag, oldArtistRemoved);
		}
	}

	/**
	 * @param repository
	 * @param oldTag
	 * @param newTag
	 * @param oldArtistRemoved
	 */
	private void updateAlbumStatistics(IRepository repository, ITag oldTag, ITag newTag, boolean oldArtistRemoved) {
		Artist artistWithOldAlbum = repository.getArtist(oldArtistRemoved ? newTag.getArtist() : oldTag.getArtist()); 
		Album oldAlbum = artistWithOldAlbum.getAlbum(oldTag.getAlbum());
		if (oldAlbum == null) {
			// Album has been renamed -> Update statistics
			statisticsHandler.replaceAlbum(artistWithOldAlbum.getName(), oldTag.getAlbum(), newTag.getAlbum());
		}
	}

	/**
	 * @param repository
	 * @param oldTag
	 * @param newTag
	 * @param oldArtistRemoved
	 * @return
	 */
	private boolean updateArtistStatistics(IRepository repository, ITag oldTag,
			ITag newTag, boolean oldArtistRemoved) {
		Artist oldArtist = repository.getArtist(oldTag.getArtist());
		if (oldArtist == null) {
			// Artist has been renamed -> Update statistics
			statisticsHandler.replaceArtist(oldTag.getArtist(), newTag.getArtist());
			oldArtistRemoved = true;
		}
		return oldArtistRemoved;
	}

	/**
	 * Returns if album changed between the two tags
	 * @param oldTag
	 * @param newTag
	 * @return
	 */
	private boolean isAlbumChanged(ITag oldTag, ITag newTag) {
		return oldTag.getAlbum() == null && newTag.getAlbum() != null ||
							   oldTag.getAlbum() != null && newTag.getAlbum() == null ||
							   !oldTag.getAlbum().equals(newTag.getAlbum());
	}

	/**
	 * Returns if artist changed between the two tags
	 * @param oldTag
	 * @param newTag
	 * @return
	 */
	private boolean isArtistChanged(ITag oldTag, ITag newTag) {
		return  oldTag.getArtist() == null && newTag.getArtist() != null ||
				oldTag.getArtist() != null && newTag.getArtist() == null || 
				!oldTag.getArtist().equals(newTag.getArtist());
	}
}