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

package net.sourceforge.atunes.kernel.modules.ui;

import java.awt.ComponentOrientation;
import java.awt.Window;

import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.FadingPopupFactory;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAboutDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.JVMProperties;
import net.sourceforge.atunes.utils.Logger;

/**
 * Responsible of UI
 * @author alex
 */
public final class UIHandler extends AbstractHandler implements IUIHandler {

	private IStateUI stateUI;

	private IStateCore stateCore;

	private IStateContext stateContext;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	@Override
	public void applicationStarted() {
		IFrameState frameState = stateUI.getFrameState(getFrame().getClass());
		getFrame().applicationStarted(frameState);

		showStatusBar(stateUI.isShowStatusBar(), false);

		if (!stateUI.isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
			getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
	}

	@Override
	public void allHandlersInitialized() {
		getFrame().setVisible(true);
	}

	@Override
	public void finish() {
		if (!stateUI.isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
			getBean(IKernel.class).finish();
		}
	}

	@Override
	public void applicationFinish() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			window.setVisible(false);
		}
	}

	/**
	 * Repaint.
	 */
	private void repaint() {
		getFrame().getFrame().invalidate();
		getFrame().getFrame().validate();
		getFrame().getFrame().repaint();
	}

	@Override
	public void showFullFrame() {
		getFrame().setVisible(true);
	}

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	private void setPlaying(final boolean playing) {
		getBean(IPlayerHandler.class).setPlaying(playing);
		getBean(IFullScreenHandler.class).setPlaying(playing);
		getBean(ISystemTrayHandler.class).setPlaying(playing);
	}

	/**
	 * Sets title bar text, adding app name and version.
	 * 
	 * @param text
	 *            the text
	 */
	private void setTitleBar(final String text) {
		StringBuilder strBuilder = new StringBuilder();
		if (!text.equals("")) {
			strBuilder.append(text);
			strBuilder.append(" - ");
		}
		strBuilder.append(Constants.APP_NAME);
		strBuilder.append(" ");
		strBuilder.append(Constants.VERSION.toShortString());

		String result = strBuilder.toString();

		getFrame().setTitle(result);
	}

	@Override
	public void showAboutDialog() {
		dialogFactory.newDialog(IAboutDialog.class).showDialog();
	}

	@Override
	public void showStatusBar(final boolean show, final boolean save) {
		if (save) {
			stateUI.setShowStatusBar(show);
		}
		getFrame().showStatusBar(show);
		repaint();
	}

	@Override
	public void startVisualization() {
		Logger.debug("Starting visualization");

		if (new JVMProperties().isJava6Update10OrLater()) {
			FadingPopupFactory.install(getOsManager(), getBean(ILookAndFeelManager.class).getCurrentLookAndFeel());
		}

		getFrame().setStateContext(stateContext);
		getFrame().setStateUI(stateUI);

		IFrameState frameState = stateUI.getFrameState(getFrame().getClass());
		ILocaleBean locale = stateCore.getLocale();
		ILocaleBean oldLocale = stateCore.getOldLocale();
		// Reset fame state if no frame state in state or if component orientation of locale has changed
		if (frameState == null || locale == null || oldLocale != null
				&& !(ComponentOrientation.getOrientation(locale.getLocale()).equals(ComponentOrientation.getOrientation(oldLocale.getLocale())))) {
			frameState = new FrameState();
			stateUI.setFrameState(getFrame().getClass(), frameState);
		}
		getFrame().create(frameState);

		JProgressBar progressBar = getFrame().getProgressBar();
		if (progressBar != null) {
			progressBar.setVisible(false);
		}

		getFrame().showDeviceInfo(false);
		setTitleBar("");

		Logger.debug("Start visualization done");
	}

	@Override
	public void toggleWindowVisibility() {
		getFrame().setVisible(!getFrame().isVisible());
		getFrame().getFrame().toFront();
		getFrame().getFrame().setState(java.awt.Frame.NORMAL);
	}

	@Override
	public void updateTitleBar(final IAudioObject song) {
		setTitleBar(song != null ? song.getAudioObjectDescription() : "");
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				playbackStateChangedEDT(newState, currentAudioObject);
			}
		});
	}

	private void playbackStateChangedEDT(final PlaybackState newState, final IAudioObject currentAudioObject) {
		if (newState == PlaybackState.PAUSED) {
			// Pause
			setPlaying(false);
			setTitleBar("");

		} else if (newState == PlaybackState.RESUMING) {
			// Resume
			setPlaying(true);
			updateTitleBar(getBean(IPlayListHandler.class).getCurrentAudioObjectFromCurrentPlayList());

		} else if (newState == PlaybackState.PLAYING) {
			// Playing
			updateTitleBar(currentAudioObject);
			setPlaying(true);

		} else if (newState == PlaybackState.STOPPED) {
			// Stop
			setPlaying(false);
			setTitleBar("");
		}
	}

	@Override
	public void applicationStateChanged() {
		// Once done graphic changes, repaint the window
		repaint();
	}

	@Override
	public void windowIconified() {
		if (stateUI.isShowSystemTray()) {
			getFrame().setVisible(false);
		}
	}
}
