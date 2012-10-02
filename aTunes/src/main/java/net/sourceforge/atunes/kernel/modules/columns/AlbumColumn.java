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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.TextAndIcon;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIconFactory;

/**
 * Column to show album
 * @author alex
 *
 */
public class AlbumColumn extends AbstractColumn<TextAndIcon> {

	private static final long serialVersionUID = -6162621108007788707L;

	private IIconFactory albumFavoriteIcon;

	private transient IBeanFactory beanFactory;

	private transient IFavoritesHandler favoritesHandler;

	/**
	 * Default constructor
	 */
	public AlbumColumn() {
		super("ALBUM");
		setVisible(true);
		setUsedForFilter(true);
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		int album = ao1.getAlbum().compareTo(ao2.getAlbum());
		if (album == 0) {
			if (ao1.getDiscNumber() == ao2.getDiscNumber()) {
				return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
			}
			return Integer.valueOf(ao1.getDiscNumber()).compareTo(ao2.getDiscNumber());
		}
		return album;
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		int album = ao2.getAlbum().compareTo(ao1.getAlbum());
		if (album == 0) {
			if (ao1.getDiscNumber() == ao2.getDiscNumber()) {
				return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
			}
			return Integer.valueOf(ao1.getDiscNumber()).compareTo(ao2.getDiscNumber());
		}
		return album;
	}

	@Override
	public TextAndIcon getValueFor(final IAudioObject audioObject, final int row) {
		if (getFavoritesHandler().getFavoriteAlbumsInfo().containsKey(audioObject.getAlbum())) {
			return new TextAndIcon(audioObject.getAlbum(), albumFavoriteIcon.getColorMutableIcon(), SwingConstants.LEFT);
		} else {
			return new TextAndIcon(audioObject.getAlbum(), null, SwingConstants.LEFT);
		}
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject, final int row) {
		return audioObject.getAlbum();
	}

	/**
	 * @return favorites handler
	 */
	private IFavoritesHandler getFavoritesHandler() {
		if (favoritesHandler == null) {
			favoritesHandler = beanFactory.getBean(IFavoritesHandler.class);
		}
		return favoritesHandler;
	}

	/**
	 * @param albumFavoriteIcon
	 */
	public void setAlbumFavoriteIcon(final IIconFactory albumFavoriteIcon) {
		this.albumFavoriteIcon = albumFavoriteIcon;
	}
}
