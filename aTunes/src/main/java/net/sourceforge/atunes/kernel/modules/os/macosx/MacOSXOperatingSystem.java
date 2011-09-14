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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.macos.MacOSXLookAndFeel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdparanoia;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.os.OperatingSystemAdapter;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.StringUtils;

public class MacOSXOperatingSystem extends OperatingSystemAdapter {

	/**
     * Name of the MacOsX command
     */
    private static final String COMMAND_MACOSX = "/usr/bin/open";
    
    protected static final String MPLAYER_COMMAND = "mplayer.command";
    
    public MacOSXOperatingSystem(OperatingSystem systemType) {
		super(systemType);
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(), "/Library/Preferences/aTunes");
	}

	@Override
	public String getLaunchCommand() {
		return COMMAND_MACOSX;
	}
	
	@Override
	public String getLaunchParameters() {
		return System.getProperty("atunes.package"); // This property is set in Info.plist
	}	
	
	public void setUpFrame(IFrame frame) {
		// Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
		// use as delegates for various com.apple.eawt.ApplicationListener methods
		try {
			MacOSXAdapter.setQuitHandler(null, Kernel.class.getDeclaredMethod("finish", (Class[]) null));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		try {
			MacOSXAdapter.setAboutHandler(GuiHandler.getInstance(), GuiHandler.class.getDeclaredMethod("showAboutDialog", (Class[]) null));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		try {
			MacOSXAdapter.setPreferencesHandler(ApplicationStateHandler.getInstance(), ApplicationStateHandler.class.getDeclaredMethod("editPreferences", (Class[]) null));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		try {
			MacOSXAdapter.setListener(GuiHandler.getInstance(), GuiHandler.class.getDeclaredMethod("showFullFrame", (Class[]) null));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		MacOSXAdapter.addDockIconMenu();
	}
	
	@Override
	public AbstractCdToWavConverter getCdToWavConverter() {
		return new Cdparanoia();
	}
	
	@Override
	public boolean testCdToWavConverter() {
		return Cdparanoia.pTestTool();
	}

	@Override
	public boolean isPlayerEngineSupported(AbstractPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? true : false; // Only MPLAYER
	}
	
	@Override
	public String getPlayerEngineCommand(AbstractPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? OsManager.getOSProperty(MPLAYER_COMMAND) : null;
	}

	/**
	 * Returns list of supported look and feels
	 * @return
	 */
	public Map<String, Class<? extends AbstractLookAndFeel>> getSupportedLookAndFeels() {
	    Map<String, Class<? extends AbstractLookAndFeel>> lookAndFeels = new HashMap<String, Class<? extends AbstractLookAndFeel>>();
        lookAndFeels.put(SubstanceLookAndFeel.SUBSTANCE, SubstanceLookAndFeel.class);
        lookAndFeels.put(MacOSXLookAndFeel.SYSTEM, MacOSXLookAndFeel.class);
        return lookAndFeels;
	}

	/**
	 * Returns default look and feel class
	 * @return
	 */
	public Class<? extends AbstractLookAndFeel> getDefaultLookAndFeel() {
		return SubstanceLookAndFeel.class;
	}
	
	@Override
	public void manageNoPlayerEngine() {
		MacOSXPlayerEngineDialog dialog = new MacOSXPlayerEngineDialog(Context.getBean(IFrame.class).getFrame());
		dialog.setVisible(true);
	}
	
	@Override
	public boolean areTrayIconsSupported() {
		return false;
	}
	
	@Override
	public boolean areMenuEntriesDelegated() {
		return true;
	}
	
	@Override
	public boolean isClosingMainWindowClosesApplication() {
		return false;
	}
	
	@Override
	public boolean isRipSupported() {
		return false;
	}


}
