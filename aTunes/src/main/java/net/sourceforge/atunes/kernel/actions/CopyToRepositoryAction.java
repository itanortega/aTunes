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

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectTransferProcess;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class CopyToRepositoryAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 2416674807979541242L;

    private IRepositoryHandler repositoryHandler;
    
    private IErrorDialogFactory errorDialogFactory;
    
    private IProcessFactory processFactory;
    
    /**
     * @param processFactory
     */
    public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param errorDialogFactory
     */
    public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}
    
    public CopyToRepositoryAction() {
        super(I18nUtils.getString("COPY_TO_REPOSITORY"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COPY_TO_REPOSITORY"));
    }

    @Override
    protected void executeAction(List<ILocalAudioObject> objects) {
        ILocalAudioObjectTransferProcess importer = (ILocalAudioObjectTransferProcess) processFactory.getProcessByName("transferToRepositoryProcess");
        importer.setFilesToTransfer(objects);
        importer.addProcessListener(new ImportProcessListener());
        importer.execute();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }
    
    private class ImportProcessListener implements IProcessListener {
        private final class ImportProcessFinishedRunnable implements Runnable {
            private final boolean ok;

            private ImportProcessFinishedRunnable(boolean ok) {
                this.ok = ok;
            }

            @Override
            public void run() {
                if (!ok) {
                	errorDialogFactory.getDialog().showErrorDialog(I18nUtils.getString("ERRORS_IN_COPYING_PROCESS"));
                }
                // Force a refresh of repository to add new songs
                repositoryHandler.refreshRepository();
            }
        }

        @Override
        public void processCanceled() { /* Nothing to do */
        }

        @Override
        public void processFinished(final boolean ok) {
            SwingUtilities.invokeLater(new ImportProcessFinishedRunnable(ok));
        }
    }
}
