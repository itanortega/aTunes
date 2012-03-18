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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public final class ExtendedToolTipActionListener implements ActionListener, ApplicationContextAware {
	
    private NavigationController navigationController;
    
    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
    	this.context = applicationContext;
    }
	
	/**
	 * @param navigationController
	 */
	public void setNavigationController(NavigationController navigationController) {
		this.navigationController = navigationController;
	}
	
	@Override
    public void actionPerformed(ActionEvent arg0) {
        navigationController.getExtendedToolTip().setVisible(true);
        ExtendedToolTipGetAndSetImageSwingWorker worker = context.getBean(ExtendedToolTipGetAndSetImageSwingWorker.class);
        worker.setCurrentObject(navigationController.getCurrentExtendedToolTipContent());
        worker.execute();
    }
}