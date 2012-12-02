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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.gui.views.dialogs.MakeDonationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Action to make a donation
 * @author alex
 *
 */
public class MakeDonationAction extends CustomAbstractAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9125170460288897027L;
	
	private IDialogFactory dialogFactory;

	MakeDonationAction() {
		super(I18nUtils.getString("MAKE_DONATION"));
	}
	
	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
	
	@Override
	protected void executeAction() {
		dialogFactory.newDialog(MakeDonationDialog.class).showDialog();
	}
}
