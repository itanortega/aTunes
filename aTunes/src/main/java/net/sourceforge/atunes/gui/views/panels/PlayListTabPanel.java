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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ArrangePlayListColumnsAction;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.NewPlayListAction;
import net.sourceforge.atunes.kernel.actions.RenamePlaylistAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;

public final class PlayListTabPanel extends JPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private PopUpButton options;
	
    private JComboBox playListCombo;
    
    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListTabPanel() {
        super(new BorderLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT);
    	playListCombo = new JComboBox();

    	add(options, BorderLayout.WEST);
        add(playListCombo, BorderLayout.CENTER);

        options.add(Actions.getAction(NewPlayListAction.class));
        options.add(Actions.getAction(RenamePlaylistAction.class));
        options.add(Actions.getAction(ClosePlaylistAction.class));
        options.add(Actions.getAction(CloseOtherPlaylistsAction.class));
        options.addSeparator();
        options.add(Actions.getAction(ArrangePlayListColumnsAction.class));
        options.addSeparator();
        options.add(Actions.getAction(CopyPlayListToDeviceAction.class));
        options.add(Actions.getAction(SynchronizeDeviceWithPlayListAction.class));
    }

    /**
	 * @return the playListCombo
	 */
	public JComboBox getPlayListCombo() {
		return playListCombo;
	}
	
	/**
	 * @return the options
	 */
	public PopUpButton getOptions() {
		return options;
	}
}
