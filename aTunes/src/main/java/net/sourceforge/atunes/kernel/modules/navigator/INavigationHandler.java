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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.ViewMode;

public interface INavigationHandler extends IHandler {

	public List<INavigationView> getNavigationViews();

	public INavigationView getCurrentView();

	public ViewMode getCurrentViewMode();

	public INavigationView getView(
			Class<? extends INavigationView> navigationViewClass);

	/**
	 * Refreshes current view to update data shown
	 */
	public void refreshCurrentView();

	/**
	 * Refreshes given view. To avoid unnecessary actions, given view is only
	 * refreshed if it's the current view
	 * 
	 * @param navigationViewClass
	 */
	public void refreshView(Class<? extends INavigationView> navigationViewClass);

	public Class<? extends INavigationView> getViewByName(String className);

	/**
	 * @return the tableFilter
	 */
	public IFilter getTableFilter();

	/**
	 * @return the treeFilter
	 */
	public IFilter getTreeFilter();

	public void refreshNavigationTable();

	/**
	 * Returns files selected both from tree and table
	 * @return
	 */
	public List<IAudioObject> getFilesSelectedInNavigator();

	public void setNavigationView(String name);

	public ISearch openSearchDialog(ISearchDialog dialog, boolean b);

	/**
	 * Updates view table, usually after apply a filter
	 */
	public void updateViewTable();

	public List<? extends IAudioObject> getAudioObjectsForTreeNode(
			Class<? extends INavigationView> class1,
			DefaultMutableTreeNode objectDragged);

	/**
	 * Returns selected audio object in navigation table
	 * @return
	 */
	public IAudioObject getSelectedAudioObjectInNavigationTable();

	/**
	 * Returns selected audio objects in navigation table
	 * @return
	 */
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable();

	public IAudioObject getAudioObjectInNavigationTable(int row);

	/**
	 * Called when repository has changed
	 */
	public void repositoryReloaded();

	/**
	 * Show navigation tree.
	 * 
	 * @param show
	 *            the show
	 */
	public void showNavigationTree(boolean show);

	/**
	 * Show navigation table.
	 * 
	 * @param show
	 *            the show
	 */
	public void showNavigationTable(boolean show);

	/**
	 * Called to select given artist in navigator
	 * @param artist
	 */
	public void selectArtist(String artist);

	/**
	 * Called to select given audio object in navigator
	 * @param audioObject
	 */
	public void selectAudioObject(IAudioObject audioObject);

	/**
	 * Returns true if last action has been performed in tree
	 * @return
	 */
	public boolean isActionOverTree();

	/**
	 * Sets navigation views
	 * @param navigationViews
	 */
	public void setNavigationViews(List<INavigationView> navigationViews);
}