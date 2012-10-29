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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AddFavoriteRadioAction;
import net.sourceforge.atunes.kernel.actions.AddRadioAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.EditRadioAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RefreshRadioAction;
import net.sourceforge.atunes.kernel.actions.RemoveRadioAction;
import net.sourceforge.atunes.kernel.actions.RenameRadioLabelAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IStateRadio;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Navigation view for radios
 * 
 * @author alex
 * 
 */
public final class RadioNavigationView extends AbstractNavigationView {

    /** The radio tree. */
    private NavigationTree radioTree;

    /** The radio tree menu. */
    private JPopupMenu radioTreeMenu;

    /** The radio table menu. */
    private JPopupMenu radioTableMenu;

    /** The column set */
    private IColumnSet radioNavigationColumnSet;

    private IRadioHandler radioHandler;

    private IIconFactory radioSmallIcon;

    private IStateRadio stateRadio;

    private IBeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param stateRadio
     */
    public void setStateRadio(final IStateRadio stateRadio) {
	this.stateRadio = stateRadio;
    }

    /**
     * @param radioSmallIcon
     */
    public void setRadioSmallIcon(final IIconFactory radioSmallIcon) {
	this.radioSmallIcon = radioSmallIcon;
    }

    /**
     * @param radioNavigationColumnSet
     */
    public void setRadioNavigationColumnSet(
	    final IColumnSet radioNavigationColumnSet) {
	this.radioNavigationColumnSet = radioNavigationColumnSet;
    }

    @Override
    public IColorMutableImageIcon getIcon() {
	return radioSmallIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
	return I18nUtils.getString("RADIO");
    }

    @Override
    public String getTooltip() {
	return I18nUtils.getString("RADIO_TAB_TOOLTIP");
    }

    @Override
    public NavigationTree getTree() {
	if (radioTree == null) {
	    radioTree = new NavigationTree(I18nUtils.getString("RADIO"),
		    getTreeRenderer());
	    radioTree.setToolTipText(I18nUtils.getString("RADIO_VIEW_TOOLTIP"));
	}
	return radioTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
	if (radioTreeMenu == null) {
	    radioTreeMenu = new JPopupMenu();
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromRadioNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    radioTreeMenu.add(addToPlayListAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromRadioNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    radioTreeMenu.add(setAsPlayListAction);

	    radioTreeMenu.add(new JSeparator());
	    radioTreeMenu.add(beanFactory.getBean(AddRadioAction.class));
	    radioTreeMenu.add(beanFactory.getBean(RefreshRadioAction.class));
	    AbstractActionOverSelectedObjects<IRadio> addFavoriteRadioAction = beanFactory
		    .getBean(AddFavoriteRadioAction.class);
	    addFavoriteRadioAction.setAudioObjectsSource(this);
	    radioTreeMenu.add(addFavoriteRadioAction);

	    EditRadioAction editRadioAction = beanFactory
		    .getBean(EditRadioAction.class);
	    editRadioAction.setAudioObjectsSource(this);
	    radioTreeMenu.add(editRadioAction);

	    radioTreeMenu
		    .add(beanFactory.getBean(RenameRadioLabelAction.class));

	    RemoveRadioAction removeRadioAction = beanFactory
		    .getBean(RemoveRadioAction.class);
	    removeRadioAction.setAudioObjectsSource(this);
	    radioTreeMenu.add(removeRadioAction);
	}
	return radioTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
	if (radioTableMenu == null) {
	    radioTableMenu = new JPopupMenu();
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromRadioNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    radioTableMenu.add(addToPlayListAction);
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = beanFactory
		    .getBean(
			    "addToPlayListAfterCurrentAudioObjectFromRadioNavigationView",
			    AddToPlayListAfterCurrentAudioObjectAction.class);
	    addToPlayListAfterCurrentAudioObjectAction
		    .setAudioObjectsSource(this);
	    radioTableMenu.add(addToPlayListAfterCurrentAudioObjectAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromRadioNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    radioTableMenu.add(setAsPlayListAction);

	    radioTableMenu.add(beanFactory.getBean(PlayNowAction.class));
	    radioTableMenu.add(new JSeparator());
	    radioTableMenu.add(new JMenuItem(beanFactory
		    .getBean(ShowNavigatorTableItemInfoAction.class)));
	    radioTableMenu.add(new JSeparator());
	    AbstractActionOverSelectedObjects<IRadio> addFavoriteRadioAction = beanFactory
		    .getBean(AddFavoriteRadioAction.class);
	    addFavoriteRadioAction.setAudioObjectsSource(this);
	    radioTableMenu.add(addFavoriteRadioAction);

	    EditRadioAction editRadioAction = beanFactory
		    .getBean(EditRadioAction.class);
	    editRadioAction.setAudioObjectsSource(this);
	    radioTableMenu.add(editRadioAction);

	    RemoveRadioAction removeRadioAction = beanFactory
		    .getBean(RemoveRadioAction.class);
	    removeRadioAction.setAudioObjectsSource(this);
	    radioTableMenu.add(removeRadioAction);
	}
	return radioTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(final ViewMode viewMode) {
	Map<String, Object> data = new HashMap<String, Object>();
	data.put("SHOW_ALL_STATIONS", stateRadio.isShowAllRadioStations());
	data.put("RADIOS", radioHandler.getRadios());
	data.put("PRESET_RADIOS", radioHandler.getRadioPresets());
	return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
	Map<String, ?> data = getViewData(viewMode);

	// Get objects selected before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsSelected = getSelectedTreeObjects();
	// Get objects expanded before refreshing tree
	List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree());

	// Nodes to be selected after refresh
	List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
	// Nodes to be expanded after refresh
	List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

	getTree().setRoot(I18nUtils.getString("RADIO"));

	addRadioNodes((List<IRadio>) data.get("RADIOS"),
		(List<IRadio>) data.get("PRESET_RADIOS"), getTree(),
		treeFilter, (Boolean) data.get("SHOW_ALL_STATIONS"),
		objectsExpanded, objectsSelected, nodesToExpand, nodesToSelect);

	// Reload the tree to refresh content
	getTree().reload();

	// Expand nodes
	getTree().expandNodes(nodesToExpand);

	// Once tree has been refreshed, select previously selected nodes
	getTree().selectNodes(nodesToSelect);
    }

    @Override
    public List<IAudioObject> getAudioObjectForTreeNode(final ITreeNode node,
	    final ViewMode viewMode, final String treeFilter) {
	List<IAudioObject> songs = new ArrayList<IAudioObject>();
	if (node.isRoot()) {
	    // Add all radios in child nodes
	    for (int i = 0; i < node.getChildCount(); i++) {
		songs.addAll(getAudioObjectForTreeNode(node.getChildAt(i),
			viewMode, treeFilter));
	    }
	} else {
	    // A node in radio view can be a label or a radio
	    Object obj = node.getUserObject();
	    if (obj instanceof IRadio) {
		IRadio r = (IRadio) node.getUserObject();
		songs.add(r);
	    } else {
		// labels
		for (int i = 0; i < node.getChildCount(); i++) {
		    IRadio r = (IRadio) node.getChildAt(i).getUserObject();
		    songs.add(r);
		}
	    }
	}
	return songs;
    }

    /**
     * Adds the radio nodes.
     * 
     * @param radios
     *            the radios
     * @param presetRadios
     *            the preset radios
     * @param root
     *            the root
     * @param currentFilter
     *            the current filter
     * @param showAllStations
     *            the show all stations
     */
    private void addRadioNodes(final List<IRadio> radios,
	    final List<IRadio> presetRadios, final INavigationTree tree,
	    final String currentFilter, final boolean showAllStations,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeNode> nodesToExpand,
	    final List<ITreeNode> nodesToSelect) {
	if (radios == null) {
	    return;
	}

	// Group radios by label
	Map<String, ITreeNode> radioGroups = new HashMap<String, ITreeNode>();
	Map<String, ITreeNode> radioGroupNoLabel = new HashMap<String, ITreeNode>();

	// Add radios
	addRadioNodes(radios, presetRadios, currentFilter, objectsExpanded,
		objectsSelected, nodesToExpand, nodesToSelect, radioGroups,
		radioGroupNoLabel);

	// Add presets
	if (showAllStations) {
	    addRadioNodes(presetRadios, null, currentFilter, objectsExpanded,
		    objectsSelected, nodesToExpand, nodesToSelect, radioGroups,
		    radioGroupNoLabel);
	}

	// Sort and add labels
	List<String> radioLabels = radioHandler.sortRadioLabels();
	for (String label : radioLabels) {
	    for (ITreeNode labelNode : radioGroups.values()) {
		if (labelNode.getUserObject().equals(label)) {
		    tree.addNode(labelNode);
		    break;
		}
	    }
	}

	// Add radio nodes without labels
	for (ITreeNode radioNode : radioGroupNoLabel.values()) {
	    tree.addNode(radioNode);
	}
    }

    private void addRadioNodes(final List<IRadio> radios,
	    final List<IRadio> presetRadios, final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeNode> nodesToExpand,
	    final List<ITreeNode> nodesToSelect,
	    final Map<String, ITreeNode> radioGroups,
	    final Map<String, ITreeNode> radioGroupNoLabel) {

	for (IRadio r : radios) {
	    if (currentFilter == null
		    || r.getName().toUpperCase()
			    .contains(currentFilter.toUpperCase())) {
		createRadioNode(presetRadios, objectsExpanded, objectsSelected,
			nodesToExpand, nodesToSelect, radioGroups,
			radioGroupNoLabel, r);
	    }
	}
    }

    /**
     * @param presetRadios
     * @param objectsExpanded
     * @param objectsSelected
     * @param nodesToExpand
     * @param nodesToSelect
     * @param radioGroups
     * @param radioGroupNoLabel
     * @param r
     */
    private void createRadioNode(final List<IRadio> presetRadios,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeNode> nodesToExpand,
	    final List<ITreeNode> nodesToSelect,
	    final Map<String, ITreeNode> radioGroups,
	    final Map<String, ITreeNode> radioGroupNoLabel, final IRadio r) {

	// Create radio node
	ITreeNode radioNode = getTree().createNode(r);

	markNodeAsSelected(objectsSelected, nodesToSelect, radioNode);

	markNodeAsExpanded(objectsExpanded, nodesToExpand, radioNode);

	// Insert radio node into label node; "REMOVED" tag means we should not
	// display this station
	if (radioGroups.containsKey(r.getLabel()) && !r.isRemoved()) {
	    radioGroups.get(r.getLabel()).add(radioNode);
	} else if (r.isRemoved()) {
	    if (presetRadios != null) {
		if (!presetRadios.isEmpty()) {
		    presetRadios.remove(r);
		}
	    } else {
		radioHandler.removeRadio(r);
	    }
	} else {
	    if (r.getLabel() == null || r.getLabel().trim().equals("")) {
		radioGroupNoLabel.put(r.getName(), radioNode);
	    } else {
		if (!r.isRemoved()) {
		    ITreeNode labelNode = getTree().createNode(r.getLabel());
		    labelNode.add(radioNode);

		    markNodeAsSelected(objectsSelected, nodesToSelect,
			    labelNode);
		    markNodeAsExpanded(objectsExpanded, nodesToExpand,
			    labelNode);

		    radioGroups.put(r.getLabel(), labelNode);
		}
	    }
	}
    }

    /**
     * @param objectsExpanded
     * @param nodesToExpand
     * @param radioNode
     */
    private void markNodeAsExpanded(
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToExpand, final ITreeNode radioNode) {
	// If node was expanded before refreshing...
	if (objectsExpanded.contains(radioNode.getUserObject())) {
	    nodesToExpand.add(radioNode);
	}
    }

    /**
     * @param objectsSelected
     * @param nodesToSelect
     * @param radioNode
     */
    private void markNodeAsSelected(
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeNode> nodesToSelect, final ITreeNode radioNode) {
	// If node was selected before refreshing...

	if (objectsSelected.contains(radioNode.getUserObject())) {
	    nodesToSelect.add(radioNode);
	}
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
	return false;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
	return radioNavigationColumnSet;
    }

    @Override
    public boolean isViewModeSupported() {
	return false;
    }

    /**
     * @param radioHandler
     */
    public void setRadioHandler(final IRadioHandler radioHandler) {
	this.radioHandler = radioHandler;
    }
}
