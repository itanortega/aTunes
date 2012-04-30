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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public final class SubmitNowPlayingInfoRunnable implements Runnable {

	private ILocalAudioObject audioFile;
	
	private LastFmUserServices lastFmUserServices;
	
	private IFrame frame;
	
	private IStateContext stateContext;
	
	private IErrorDialogFactory errorDialogFactory;
	
	/**
	 * @param errorDialogFactory
	 */
	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}
	
	/**
	 * @param lastFmUserServices
	 */
	public void setLastFmUserServices(LastFmUserServices lastFmUserServices) {
		this.lastFmUserServices = lastFmUserServices;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param stateContext
	 */
	public void setStateContext(IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param audioFile
	 */
	public void setAudioFile(ILocalAudioObject audioFile) {
		this.audioFile = audioFile;
	}
	
	@Override
	public void run() {
	    try {
	        lastFmUserServices.submitNowPlayingInfo(audioFile);
	    } catch (ScrobblerException e) {
	        if (e.getStatus() == 2) {
	            Logger.error("Authentication failure on Last.fm service");
	            SwingUtilities.invokeLater(new Runnable() {

	                @Override
	                public void run() {
	                	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("LASTFM_USER_ERROR"));
	                    // Disable service by deleting password
	                    stateContext.setLastFmEnabled(false);
	                }
	            });
	        } else {
	            Logger.error(e.getMessage());
	        }
	    }
	}
}