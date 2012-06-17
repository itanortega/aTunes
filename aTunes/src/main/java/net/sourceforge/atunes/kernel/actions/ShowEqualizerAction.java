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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IEqualizerDialog;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action displays equalizer window
 * 
 * @author fleax
 * 
 */
public class ShowEqualizerAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = 2511199136727155747L;

    private IDialogFactory dialogFactory;
    
    private IIconFactory equalizerIcon;
    
    /**
     * @param equalizerIcon
     */
    public void setEqualizerIcon(IIconFactory equalizerIcon) {
		this.equalizerIcon = equalizerIcon;
	}

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * Default constructor
     */
    public ShowEqualizerAction() {
        super(I18nUtils.getString("EQUALIZER"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    }

    @Override
    protected void executeAction() {
    	dialogFactory.newDialog(IEqualizerDialog.class).showDialog();
    }
    
    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
    	return equalizerIcon.getColorMutableIcon();
    }
}
