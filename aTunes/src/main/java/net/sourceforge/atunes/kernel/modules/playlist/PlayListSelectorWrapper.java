/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.atunes.gui.views.panels.PlayListSelectorPanel;
import net.sourceforge.atunes.model.IButtonPanel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStatePlaylist;

/**
 * Component to select which play list to show to user
 * 
 * @author alex
 * 
 */
public class PlayListSelectorWrapper {

	private JComboBox playListCombo;

	private IButtonPanel playListButtonFlowPanel;

	private IStatePlaylist statePlaylist;

	private IPlayListHandler playListHandler;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * Creates both type of selectors
	 */
	public void initialize() {
		this.playListCombo = new JComboBox();
		this.playListCombo.setMaximumRowCount(30);

		this.playListButtonFlowPanel = this.controlsBuilder.createButtonPanel();
		this.playListButtonFlowPanel.setIconOnly(false);
	}

	/**
	 * Arrange components in play list selector panel
	 * 
	 * @param selectorPanel
	 * @param options
	 * @param playListFilterPanel
	 */
	public void arrangeComponents(final PlayListSelectorPanel selectorPanel) {
		GridBagConstraints c = new GridBagConstraints();

		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.insets = new Insets(1, 0, 1, 0);
		selectorPanel.add(selectorPanel.getOptions().getSwingComponent(), c);

		c.gridx = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		if (this.statePlaylist.isShowPlayListSelectorComboBox()) {
			c.fill = GridBagConstraints.VERTICAL;
			selectorPanel.add(this.playListCombo, c);
		} else {
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(1, 0, 1, 10);
			selectorPanel.add((JComponent) this.playListButtonFlowPanel, c);
		}

		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 5);
		selectorPanel.add(selectorPanel.getPlayListFilterPanel()
				.getSwingComponent(), c);
	}

	/**
	 * Initializes both components
	 * 
	 * @param l
	 */
	void addBindings(final PlayListTabListener l) {
		this.playListCombo.addItemListener(l);
		this.playListCombo.setModel(PlayListComboModel.getNewComboModel());

		this.playListButtonFlowPanel.addItemListener(l);
	}

	void deletePlayList(final int index) {
		((PlayListComboModel) this.playListCombo.getModel())
				.removeItemAt(index);
		this.playListButtonFlowPanel.removeButton(index);
	}

	/**
	 * @param index
	 */
	void forceSwitchTo(final int index) {
		this.playListCombo.setSelectedIndex(index);

		this.playListButtonFlowPanel.setSelectedButton(index);
	}

	void newPlayList(final String name) {
		((PlayListComboModel) this.playListCombo.getModel()).addItem(name);

		this.playListButtonFlowPanel.addButton(name, name, null,
				new AbstractAction() {

					private static final long serialVersionUID = -8487582617110724128L;

					@Override
					public void actionPerformed(final ActionEvent event) {
						forceSwitchTo(PlayListSelectorWrapper.this.playListButtonFlowPanel
								.getIndexOfButtonSelected(event));
					}
				}, name);
	}

	void renamePlayList(final int index, final String newName) {
		((PlayListComboModel) this.playListCombo.getModel()).rename(index,
				newName);
		// Forces update of combo box by selecting again current play list
		this.playListCombo.setSelectedIndex(index);

		this.playListButtonFlowPanel.renameButton(index, newName);
	}

	List<String> getNamesOfPlayLists() {
		return ((PlayListComboModel) this.playListCombo.getModel()).getItems();
	}

	int getSelectedPlayListIndex() {
		return this.playListCombo.getSelectedIndex() != -1 ? this.playListCombo
				.getSelectedIndex() : 0;
	}

	String getPlayListName(final int index) {
		return ((PlayListComboModel) this.playListCombo.getModel())
				.getElementAt(index);
	}

	/**
	 * Switches to playlist
	 * 
	 * @param selectedPlayListIndex
	 */
	public void switchToPlaylist(final int selectedPlayListIndex) {
		this.playListHandler.switchToPlaylist(getSelectedPlayListIndex());
		// This is called when selecting item in combo so set selected button
		// too
		this.playListButtonFlowPanel
				.setSelectedButton(getSelectedPlayListIndex());
	}

}
