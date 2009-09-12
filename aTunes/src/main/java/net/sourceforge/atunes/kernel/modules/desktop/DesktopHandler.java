/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.FileNameUtils;

/**
 * Handler for Desktop interaction.
 */
public final class DesktopHandler extends Handler {

    /** The instance. */
    private static DesktopHandler instance = new DesktopHandler();

    /** The is desktop supported. */
    private boolean isDesktopSupported;

    /** The desktop. */
    Desktop desktop;

    /**
     * Instantiates a new desktop handler.
     */
    private DesktopHandler() {
    }
    
    @Override
    protected void initHandler() {
        isDesktopSupported = Desktop.isDesktopSupported();
        if (isDesktopSupported) {
            desktop = Desktop.getDesktop();
        }
    }
    
    @Override
    public void applicationStarted() {
    	// TODO Auto-generated method stub
    	
    }

    @Override
    public void applicationFinish() {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public void applicationStateChanged(ApplicationState newState) {
    	// TODO Auto-generated method stub
    	
    }

    /**
     * Gets the single instance of DesktopHandler.
     * 
     * @return single instance of DesktopHandler
     */
    public static DesktopHandler getInstance() {
        return instance;
    }

    /**
     * Starts web browser.
     * 
     * @param search
     *            Search object
     * @param query
     *            query
     */
    public void openSearch(Search search, String query) {
        if (search != null && isDesktopSupported) {
            try {
                desktop.browse(search.getURL(query).toURI());
            } catch (MalformedURLException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            } catch (IOException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            } catch (URISyntaxException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            }
        }
    }

    /**
     * Starts web browser with specified URI.
     * 
     * @param uri
     *            URI
     */
    public void openURI(URI uri) {
        if (isDesktopSupported) {
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            }
        }
    }

    /**
     * Starts web browser with specified URL.
     * 
     * @param url
     *            URL
     */
    public void openURL(String url) {
        if (isDesktopSupported) {
            try {
                openURI(new URL(url).toURI());
            } catch (MalformedURLException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            } catch (URISyntaxException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            }
        }
    }

    /**
     * Starts web browser with specified URL.
     * 
     * @param url
     *            URL
     */
    public void openURL(URL url) {
        if (isDesktopSupported) {
            try {
                openURI(url.toURI());
            } catch (URISyntaxException e) {
                getLogger().error(LogCategories.DESKTOP, e);
            }
        }
    }

    /**
     * Opens a file with the associated program.
     * 
     * @param file
     *            The file that should be opened
     */
    public void openFile(File file) {
        if (isDesktopSupported) {
            final File fileToOpen;
            /*
             * Needed for UNC filenames with spaces ->
             * http://bugs.sun.com/view_bug.do?bug_id=6550588
             */
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                fileToOpen = new File(FileNameUtils.getShortPathNameW(file.getAbsolutePath()));
            } else {
                fileToOpen = file;
            }
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        desktop.open(fileToOpen);
                    } catch (IOException e) {
                        getLogger().error(LogCategories.DESKTOP, e);
                    }
                    return null;
                }
            }.execute();
        }
    }

}
