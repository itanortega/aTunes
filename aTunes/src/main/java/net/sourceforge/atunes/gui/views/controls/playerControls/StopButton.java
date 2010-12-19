/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.JButton;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;

/*
 * based on code from Xtreme Media Player
 */
/**
 * The Class StopButton.
 */
public final class StopButton extends JButton {

    private static final long serialVersionUID = 6007885049773560874L;

    /**
     * Instantiates a new stop button.
     * 
     * @param size
     */
    public StopButton(Dimension size) {
        super(Actions.getAction(StopCurrentAudioObjectAction.class));
        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setIcon(Images.getImage(Images.STOP));
        setText(null);

        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
    }
}
