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

package net.sourceforge.atunes.model;

/**
 * State specific for radios
 * 
 * @author alex
 * 
 */
public interface IStateRadio extends IState {

	/**
	 * Show all radio stations or just the ones defined by user
	 * 
	 * @return
	 */
	public boolean isShowAllRadioStations();

	/**
	 * Show all radio stations or just the ones defined by user
	 * 
	 * @param showAllRadioStations
	 */
	public void setShowAllRadioStations(boolean showAllRadioStations);

	/**
	 * Read metadata information from radios
	 * 
	 * @return
	 */
	public boolean isReadInfoFromRadioStream();

	/**
	 * Read metadata information from radios
	 * 
	 * @param readInfoFromRadioStream
	 */
	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream);

}
