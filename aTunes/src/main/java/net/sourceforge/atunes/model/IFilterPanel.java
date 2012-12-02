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

import javax.swing.JButton;
import javax.swing.JTextField;


/**
 * A panel to let user filter objects
 * @author alex
 *
 */
public interface IFilterPanel extends IPanel {

	/**
	 * @return the filterTextField
	 */
	public JTextField getFilterTextField();

	/**
	 * @return the clearButton
	 */
	public JButton getClearButton();

	/**
	 * Called to update filter panel
	 * @param filterApplied
	 */
	public void setFilterApplied(boolean filterApplied);

}