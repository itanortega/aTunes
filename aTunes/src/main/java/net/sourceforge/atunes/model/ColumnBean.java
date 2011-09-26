/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.io.Serializable;


/**
 * This class represents information about a column to be saved into application
 * settings.
 * 
 * @author alex
 */
public final class ColumnBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3224708329958757496L;

	/** The order. */
    private int order;

    /** The visible. */
    private boolean visible;

    /** The width. */
    private int width;

    /** The sort */
    private ColumnSort sort;

    /**
     * Gets the order.
     * 
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Checks if is visible.
     * 
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the order.
     * 
     * @param order
     *            the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Sets the visible.
     * 
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sets the width.
     * 
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the sort
     */
    public ColumnSort getSort() {
        return sort;
    }

    /**
     * @param sort
     *            the sort to set
     */
    public void setSort(ColumnSort sort) {
        this.sort = sort;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ColumnBean other = (ColumnBean) obj;
		if (order != other.order) {
			return false;
		}
		if (sort != other.sort) {
			return false;
		}
		if (visible != other.visible) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}
}