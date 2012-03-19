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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;

/**
 * Abstract class for Cdda2wav and Cdparanoia
 */
public abstract class AbstractCdToWavConverter {

    private ProgressListener listener;
    private NoCdListener noCdListener;
    private Process process;
    // Cd must be initialized here, otherwise it wont work
    private CDInfo cdInfo = new CDInfo();

    /*
     * Public methods
     */

    /**
     * Sets no CD found so a dialog gets displayed.
     */
    public void notifyNoCd() {
        if (noCdListener != null) {
            noCdListener.noCd();
        }
    }

    /**
     * Destroys the ripping process.
     */
    public void stop() {
        process.destroy();
    }

    /*
     * Abstract methods
     */

    public abstract boolean cdda2wav(int track, File file);

    public abstract boolean cdda2wav(int track, File file, boolean useParanoia);

    public abstract CDInfo retrieveDiscInformation();

    /*
     * Getters and Setters
     */

    /**
     * Sets the listener.
     * 
     * @param listener
     *            the new listener
     */
    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the no cd listener.
     * 
     * @param noCdListener
     *            the new no cd listener
     */
    public void setNoCdListener(NoCdListener noCdListener) {
        this.noCdListener = noCdListener;
    }

    /**
     * @return the process
     */
    protected Process getProcess() {
        return process;
    }

    /**
     * @param process
     *            the process to set
     */
    protected void setProcess(Process process) {
        this.process = process;
    }

    /**
     * @return the listener
     */
    protected ProgressListener getListener() {
        return listener;
    }

    /**
     * @return the cd
     */
    protected CDInfo getCdInfo() {
        return cdInfo;
    }

    /**
     * @param cdInfo
     *            the cdInfo to set
     */
    protected void setCDInfo(CDInfo cdInfo) {
        this.cdInfo = cdInfo;
    }

    /**
     * @return the noCdListener
     */
    protected NoCdListener getNoCdListener() {
        return noCdListener;
    }

}
