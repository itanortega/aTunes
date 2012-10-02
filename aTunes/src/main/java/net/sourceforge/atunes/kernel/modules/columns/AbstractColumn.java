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

import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents a column
 * @author alex
 *
 * @param <T>
 */
public abstract class AbstractColumn<T> implements IColumn<T> {

	private static final long serialVersionUID = 7407756833207959017L;

	/** Header text of column. */
	private String columnName;

	/** Resizable. */
	private boolean resizable = true;

	/** Width of column. */
	private int width = 150;

	/** Class of data. */
	private Class<?> columnClass;

	/** Visible column. */
	private boolean visible;

	/** Order. */
	private int order;

	/** Text alignment. */
	private int alignment = GuiUtils.getComponentOrientationAsSwingConstant();

	/** Editable flag. */
	private boolean editable = false;

	/** Indicates if this column can be used to filter objects */
	private boolean usedForFilter = false;

	/**
	 * Last sort order used for this column
	 */
	private transient ColumnSort columnSort;

	/**
	 * Constructor with columnId, headerText and columnClass.
	 * 
	 * @param columnId
	 *            the column id
	 * @param name
	 *            the header text
	 * @param columnClass
	 *            the column class
	 */
	public AbstractColumn(final String name) {
		this.columnName = name;
		this.columnClass = (Class<?>) ReflectionUtils.getTypeArgumentsOfParameterizedType(this.getClass())[0];
	}

	@Override
	public void applyColumnBean(final ColumnBean bean) {
		order = bean.getOrder();
		width = bean.getWidth();
		visible = bean.isVisible();
		columnSort = bean.getSort();
	}

	@Override
	public int compareTo(final IColumn<?> o) {
		return Integer.valueOf(order).compareTo(o.getOrder());
	}

	@Override
	public int getAlignment() {
		return alignment;
	}

	@Override
	public TableCellEditor getCellEditor() {
		return null;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return null;
	}

	@Override
	public ColumnBean getColumnBean() {
		ColumnBean bean = new ColumnBean();
		bean.setOrder(order);
		bean.setWidth(width);
		bean.setVisible(visible);
		bean.setSort(columnSort);
		return bean;
	}

	@Override
	public Class<?> getColumnClass() {
		return columnClass;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String getHeaderText() {
		return columnName;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean isSortable() {
		return true;
	}

	@Override
	public boolean isResizable() {
		return resizable;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setAlignment(final int alignment) {
		this.alignment = alignment;
	}

	@Override
	public void setColumnClass(final Class<?> columnClass) {
		this.columnClass = columnClass;
	}

	@Override
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	@Override
	public void setColumnName(final String headerText) {
		this.columnName = headerText;
	}

	@Override
	public void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public void setResizable(final boolean resizable) {
		this.resizable = resizable;
	}

	@Override
	public void setValueFor(final IAudioObject audioObject, final Object value) {
		// Does nothing, should be overrided
	}

	@Override
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setWidth(final int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return columnName;
	}

	@Override
	public final Comparator<IAudioObject> getComparator(final boolean changeSort) {
		Logger.debug("Returning comparator for column: ", this.getClass().getName());
		if (columnSort == null) {
			columnSort = ColumnSort.ASCENDING;
		} else if (columnSort == ColumnSort.ASCENDING && changeSort) {
			columnSort = ColumnSort.DESCENDING;
		} else if (columnSort == ColumnSort.DESCENDING && changeSort) {
			columnSort = ColumnSort.ASCENDING;
		}

		if (columnSort.equals(ColumnSort.ASCENDING)) {
			return new AscendingColumnSortComparator(this);
		} else {
			return new DescendingColumnSortComparator(this);
		}
	}

	@Override
	public boolean isUsedForFilter() {
		return usedForFilter;
	}

	@Override
	public void setUsedForFilter(final boolean usedForFilter) {
		this.usedForFilter = usedForFilter;
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject, final int row) {
		return getValueFor(audioObject, row).toString();
	}

	@Override
	public void setColumnSort(final ColumnSort columnSort) {
		this.columnSort = columnSort;
	}

	@Override
	public ColumnSort getColumnSort() {
		return columnSort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (obj instanceof AbstractColumn<?>) {
			@SuppressWarnings("rawtypes")
			AbstractColumn other = (AbstractColumn) obj;
			if (order != other.order) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two objects in ascending order
	 * @param ao1
	 * @param ao2
	 * @return
	 */
	protected abstract int ascendingCompare(IAudioObject ao1, IAudioObject ao2);

	/**
	 * Compares two objects in descending order
	 * @param ao1
	 * @param ao2
	 * @return
	 */
	protected abstract int descendingCompare(IAudioObject ao1, IAudioObject ao2);

}
