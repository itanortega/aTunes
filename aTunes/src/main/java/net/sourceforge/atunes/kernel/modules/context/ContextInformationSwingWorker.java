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

package net.sourceforge.atunes.kernel.modules.context;

import java.util.concurrent.CancellationException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.utils.Logger;

/**
 * This class implements a special SwingWorker used to retrieve data from a
 * ContextInformationDataSource and show it in a ContextPanelContent
 * 
 * @author alex
 * 
 */
class ContextInformationSwingWorker extends SwingWorker<Void, Void> {

    /**
     * The context panel content where information must be shown after
     * retrieving data
     */
    private final IContextPanelContent<IContextInformationSource> content;

    /**
     * The context information data source used to retrieve information
     */
    private final IContextInformationSource dataSource;

    /**
     * audio object
     */
    private final IAudioObject audioObject;

    /**
     * To be executed at finish
     */
    private final Runnable updateCallback;

    /**
     * Constructor used to create a new ContextInformationSwingWorker
     * 
     * @param content
     * @param dataSource
     * @param audioObject
     * @param updateCallback
     */
    ContextInformationSwingWorker(final IContextPanelContent<IContextInformationSource> content,
	    final IContextInformationSource dataSource,
	    final IAudioObject audioObject, final Runnable updateCallback) {
	this.content = content;
	this.dataSource = dataSource;
	this.audioObject = audioObject;
	this.updateCallback = updateCallback;
    }

    @Override
    protected Void doInBackground() {
	dataSource.getData(audioObject);
	return null;
    }

    @Override
    protected void done() {
	super.done();
	try {
	    content.updateContentFromDataSource(dataSource);
	    if (!isCancelled()) {
		// Enable task pane so user can expand or collapse
		content.getParentPanel().setEnabled(true);
		// After update data expand content
		content.getParentPanel().setVisible(true);

		// Callback
		updateCallback.run();
	    }
	} catch (CancellationException e) {
	    // thrown when cancelled
	    Logger.error(e);
	}
    }

    /**
     * Cancels data retrieve
     */
    void cancel() {
	cancel(true);
	dataSource.cancel();
    }
}
