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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.process.ImportFilesProcess;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IReviewImportDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.utils.Logger;

/**
 * @author alex
 *
 */
public final class ImportFoldersSwingWorker extends SwingWorker<List<ILocalAudioObject>, Void> {
	
	private IRepositoryHandler repositoryHandler;
	private List<File> folders;
	private String path;
	private IProgressDialog progressDialog;
	private IFrame frame;
	private IState appState;
	private IErrorDialogFactory errorDialogFactory;
	private ILocalAudioObjectValidator localAudioObjectValidator;
	private IProcessFactory processFactory;
	private ILocalAudioObjectLocator localAudioObjectLocator;
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param folders
	 */
	public void setFolders(List<File> folders) {
		this.folders = folders;
	}
	
	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @param progressDialog
	 */
	public void setProgressDialog(IProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param state
	 */
	public void setAppState(IState state) {
		this.appState = state;
	}
	
	/**
	 * @param errorDialogFactory
	 */
	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}
	
	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(
			ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
	
	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
	
	/**
	 * @param localAudioObjectLocator
	 */
	public void setLocalAudioObjectLocator(
			ILocalAudioObjectLocator localAudioObjectLocator) {
		this.localAudioObjectLocator = localAudioObjectLocator;
	}
	
	@Override
	protected List<ILocalAudioObject> doInBackground() {
	    return getSongsForFolders(folders, new ImportFoldersLoaderListener(progressDialog), localAudioObjectValidator);
	}

	@Override
	protected void done() {
	    super.done();

	    try {
	        final List<ILocalAudioObject> filesToLoad = get();

	        ITagAttributesReviewed tagAttributesReviewed = null;
	        // Review tags if selected in settings
	        if (appState.isReviewTagsBeforeImport()) {
	            IReviewImportDialog reviewImportDialog = Context.getBean(IReviewImportDialog.class);
	            reviewImportDialog.showDialog(folders, filesToLoad);
	            if (reviewImportDialog.isDialogCancelled()) {
	                return;
	            }
	            tagAttributesReviewed = reviewImportDialog.getResult();
	        }

	        ImportFilesProcess process = (ImportFilesProcess) processFactory.getProcessByName("importFilesProcess");
	        process.setFilesToTransfer(filesToLoad);
	        process.setFolders(folders);
	        process.setDestination(path);
	        process.initialize(tagAttributesReviewed);
	        process.addProcessListener(new ImportFilesProcessListener(process, repositoryHandler, frame, errorDialogFactory));
	        process.execute();

	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
	
	/**
	 * Gets the songs of a list of folders. Used in import
	 * @param folders
	 * @param listener
	 * @param localAudioObjectValidator
	 * @return
	 */
	private List<ILocalAudioObject> getSongsForFolders(List<File> folders, IRepositoryLoaderListener listener, ILocalAudioObjectValidator localAudioObjectValidator) {
		int filesCount = 0;
		for (File folder : folders) {
			filesCount = filesCount + countFiles(folder, localAudioObjectValidator);
		}
		if (listener != null) {
			listener.notifyFilesInRepository(filesCount);
		}
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (File folder : folders) {
			result.addAll(localAudioObjectLocator.locateLocalAudioObjectsInFolder(folder, listener));
		}
		if (listener != null) {
			listener.notifyFinishRead(null);
		}
		return result;
	}
	
	/**
	 * Count files.
	 * 
	 * @param dir
	 * @param localAudioObjectValidator
	 * @return
	 */
	private int countFiles(File dir, ILocalAudioObjectValidator localAudioObjectValidator) {
		int files = 0;
		File[] list = dir.listFiles();
		if (list == null) {
			return files;
		}
		for (File element : list) {
			if (localAudioObjectValidator.isValidAudioFile(element)) {
				files++;
			} else if (element.isDirectory()) {
				files = files + countFiles(element, localAudioObjectValidator);
			}
		}
		return files;
	}
}