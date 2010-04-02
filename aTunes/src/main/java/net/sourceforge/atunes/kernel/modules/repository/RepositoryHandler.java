/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.ExportAction;
import net.sourceforge.atunes.kernel.actions.ImportToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RefreshRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.SelectRepositoryAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Repository;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.repository.processes.ImportFilesProcess;
import net.sourceforge.atunes.kernel.modules.repository.statistics.StatisticsHandler;
import net.sourceforge.atunes.kernel.modules.repository.tags.TagAttributesReviewed;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.RepositorySearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements LoaderListener, AudioFilesRemovedListener {

    private final class ImportFoldersSwingWorker extends
			SwingWorker<List<AudioFile>, Void> {
		private final class ImportFilesProcessListener implements
				ProcessListener {
			private final ImportFilesProcess process;

			private ImportFilesProcessListener(ImportFilesProcess process) {
				this.process = process;
			}

			@Override
			public void processCanceled() {
			    // Nothing to do, files copied will be removed before calling this method 
			}

			@Override
			public void processFinished(final boolean ok) {
			    if (!ok) {
			        try {
			            SwingUtilities.invokeAndWait(new Runnable() {
			                @Override
			                public void run() {
			                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
			                }
			            });
			        } catch (InterruptedException e) {
			            // Do nothing
			        } catch (InvocationTargetException e) {
			            // Do nothing
			        }
			    } else {
			        // If import is ok then add files to repository
			        addFilesAndRefresh(process.getFilesTransferred());
			    }
			}
		}

		private final class ImportFoldersLoaderListener implements
				LoaderListener {
			private int filesLoaded = 0;
			private int totalFiles;

			@Override
			public void notifyRemainingTime(long time) {
			}

			@Override
			public void notifyReadProgress() {
			}

			@Override
			public void notifyFinishRefresh(RepositoryLoader loader) {
			}

			@Override
			public void notifyFinishRead(RepositoryLoader loader) {
			    progressDialog.setVisible(false);
			}

			@Override
			public void notifyFilesInRepository(final int files) {
			    this.totalFiles = files;
			    SwingUtilities.invokeLater(new Runnable() {
			        @Override
			        public void run() {
			            progressDialog.setTotalProgress(files);
			        }
			    });
			}

			@Override
			public void notifyFileLoaded() {
			    this.filesLoaded++;
			    SwingUtilities.invokeLater(new Runnable() {
			        @Override
			        public void run() {
			            progressDialog.setCurrentProgress(filesLoaded);
			            progressDialog.setProgressBarValue((int) (filesLoaded * 100.0 / totalFiles));
			        }
			    });
			}

			@Override
			public void notifyCurrentPath(String path) {
			}
		}

		private final List<File> folders;
		private final String path;
		private final ProgressDialog progressDialog;

		private ImportFoldersSwingWorker(List<File> folders, String path,
				ProgressDialog progressDialog) {
			this.folders = folders;
			this.path = path;
			this.progressDialog = progressDialog;
		}

		@Override
		protected List<AudioFile> doInBackground() throws Exception {
		    return RepositoryLoader.getSongsForFolders(folders, new ImportFoldersLoaderListener());
		}

		@Override
		protected void done() {
		    super.done();
		    GuiHandler.getInstance().hideIndeterminateProgressDialog();

		    try {
		        final List<AudioFile> filesToLoad = get();

		        TagAttributesReviewed tagAttributesReviewed = null;
		        // Review tags if selected in settings
		        if (ApplicationState.getInstance().isReviewTagsBeforeImport()) {
		            ReviewImportDialog reviewImportDialog = GuiHandler.getInstance().getReviewImportDialog();
		            reviewImportDialog.show(folders, filesToLoad);
		            if (reviewImportDialog.isDialogCancelled()) {
		                return;
		            }
		            tagAttributesReviewed = reviewImportDialog.getResult();
		        }

		        final ImportFilesProcess process = new ImportFilesProcess(filesToLoad, folders, path, tagAttributesReviewed);
		        process.addProcessListener(new ImportFilesProcessListener(process));
		        process.execute();

		    } catch (InterruptedException e) {
		        getLogger().error(LogCategories.REPOSITORY, e);
		    } catch (ExecutionException e) {
		        getLogger().error(LogCategories.REPOSITORY, e);
		    }
		}
	}

	private static class ShowProgressBarRunnable implements Runnable {
        @Override
        public void run() {
            GuiHandler.getInstance().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
        }
    }

    private static class ExitRunnable implements Runnable {
        @Override
        public void run() {
            Actions.getAction(ExitAction.class).actionPerformed(null);
        }
    }

    private static RepositoryHandler instance = new RepositoryHandler();

    private Repository repository;
    private int filesLoaded;
    private RepositoryLoader currentLoader;
    private boolean backgroundLoad = false;
    private RepositoryAutoRefresher repositoryRefresher;
    private Repository repositoryRetrievedFromCache = null;
    /** Listeners notified when an audio file is removed */
    private List<AudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<AudioFilesRemovedListener>();

    private RepositoryProgressDialog progressDialog;

    private MouseListener progressBarMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            backgroundLoad = false;
            currentLoader.setPriority(Thread.MAX_PRIORITY);
            GuiHandler.getInstance().hideProgressBar();
            if (progressDialog != null) {
                progressDialog.showProgressDialog();
            }
            GuiHandler.getInstance().getProgressBar().removeMouseListener(progressBarMouseAdapter);
        };
    };

    /**
     * Instantiates a new repository handler.
     */
    private RepositoryHandler() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {

    }

    @Override
    protected void initHandler() {
        // Add itself as listener
        addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted() {
        SearchHandler.getInstance().registerSearchableObject(RepositorySearchableObject.getInstance());
        RepositoryHandler.this.repositoryRefresher = new RepositoryAutoRefresher(RepositoryHandler.this);
    }

    /**
     * Gets the single instance of RepositoryHandler.
     * 
     * @return single instance of RepositoryHandler
     */
    public static RepositoryHandler getInstance() {
        return instance;
    }

    /**
     * Adds the given files to repository and refresh.
     * 
     * @param files
     *            the files
     */
    public void addFilesAndRefresh(final List<File> files) {
        SwingUtilities.invokeLater(new ShowProgressBarRunnable());
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                RepositoryLoader.addToRepository(repository, files);
                return null;
            }

            @Override
            protected void done() {
                GuiHandler.getInstance().hideProgressBar();
                GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
                if (ControllerProxy.getInstance().getNavigationController() != null) {
                    ControllerProxy.getInstance().getNavigationController().notifyReload();
                }
                getLogger().info(LogCategories.REPOSITORY, "Repository refresh done");
            }
        };
        worker.execute();
    }

    /**
     * Adds the external picture for album.
     * 
     * @param artistName
     *            the artist name
     * @param albumName
     *            the album name
     * @param picture
     *            the picture
     */
    public void addExternalPictureForAlbum(String artistName, String albumName, File picture) {
        RepositoryLoader.addExternalPictureForAlbum(repository, artistName, albumName, picture);
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        if (repositoryRefresher != null) {
            repositoryRefresher.interrupt();
        }
        if (repository != null) {
            // Only store repository if it's dirty
            if (repository.isDirty()) {
                ApplicationStateHandler.getInstance().persistRepositoryCache(repository, true);
            } else {
                getLogger().info(LogCategories.REPOSITORY, "Repository is clean");
            }

            // Execute command after last access to repository
            String command = ApplicationState.getInstance().getCommandAfterAccessRepository();
            if (command != null && !command.trim().isEmpty()) {
                try {
                    Process p = Runtime.getRuntime().exec(command);
                    // Wait process to end
                    p.waitFor();
                    int rc = p.exitValue();
                    getLogger().info(LogCategories.END, StringUtils.getString("Command '", command, "' return code: ", rc));
                } catch (Exception e) {
                    getLogger().error(LogCategories.END, e);
                }
            }
        }
        AudioFile.getImageCache().shutdown();
    }

    public List<File> getFolders() {
        if (repository != null) {
            return repository.getFolders();
        }
        return Collections.emptyList();
    }

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    public List<Album> getAlbums() {
        List<Album> result = new ArrayList<Album>();
        if (repository != null) {
            Collection<Artist> artists = repository.getArtistStructure().values();
            for (Artist a : artists) {
                result.addAll(a.getAlbums().values());
            }
            Collections.sort(result);
        }
        return result;
    }

    /**
     * Gets the artist and album structure.
     * 
     * @return the artist and album structure
     */
    public Map<String, Artist> getArtistStructure() {
        if (repository != null) {
            return repository.getArtistStructure();
        }
        return new HashMap<String, Artist>();
    }

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<Artist> getArtists() {
        List<Artist> result = new ArrayList<Artist>();
        if (repository != null) {
            result.addAll(repository.getArtistStructure().values());
            Collections.sort(result);
        }
        return result;
    }

    /**
     * Gets the file if loaded.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file if loaded
     */
    public AudioFile getFileIfLoaded(String fileName) {
        return repository == null ? null : repository.getFile(fileName);
    }

    /**
     * Gets the folder structure.
     * 
     * @return the folder structure
     */
    public Map<String, Folder> getFolderStructure() {
        if (repository != null) {
            return repository.getFolderStructure();
        }
        return new HashMap<String, Folder>();
    }

    /**
     * Gets the genre structure.
     * 
     * @return the genre structure
     */
    public Map<String, Genre> getGenreStructure() {
        if (repository != null) {
            return repository.getGenreStructure();
        }
        return new HashMap<String, Genre>();
    }

    /**
     * Gets the year structure.
     * 
     * @return the year structure
     */
    public Map<String, Year> getYearStructure() {
        if (repository != null) {
            return repository.getYearStructure();
        }
        return new HashMap<String, Year>();
    }

    /**
     * Gets the album structure
     * 
     * @return
     */
    //TODO
    public Map<String, Album> getAlbumStructure() {
        if (repository != null) {
            Map<String, Album> albumsStructure = new HashMap<String, Album>();
            Collection<Artist> artistCollection = repository.getArtistStructure().values();
            for (Artist artist : artistCollection) {
                for (Album album : artist.getAlbums().values()) {
                    albumsStructure.put(album.getNameAndArtist(), album);
                }
            }
            return albumsStructure;
        }
        return new HashMap<String, Album>();
    }

    /**
     * Returns number of root folders of repository
     * 
     * @return
     */
    public int getFoldersCount() {
        if (repository != null) {
            return repository.getFolders().size();
        }
        return 0;
    }

    /**
     * Gets the path for new audio files ripped.
     * 
     * @return the path for new audio files ripped
     */
    public String getPathForNewAudioFilesRipped() {
        return StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), SystemProperties.FILE_SEPARATOR, Album.getUnknownAlbum(), " - ", DateUtils
                .toPathString(new Date()));
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Returns repository root folder that contains file.
     * 
     * @param file
     *            the file
     * 
     * @return the repository folder containing file
     */
    public File getRepositoryFolderContainingFile(AudioFile file) {
        if (repository == null) {
            return null;
        }
        for (File folder : repository.getFolders()) {
            if (file.getUrl().startsWith(folder.getAbsolutePath())) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Gets the repository path.
     * 
     * @return the repository path
     */
    public String getRepositoryPath() {
        // TODO: Remove this method as now more than one folder can be added to repository
        return repository != null ? repository.getFolders().get(0).getAbsolutePath() : "";
    }

    /**
     * Gets the repository total size.
     * 
     * @return the repository total size
     */
    public long getRepositoryTotalSize() {
        return repository != null ? repository.getTotalSizeInBytes() : 0;
    }

    /**
     * Gets the number of files of repository
     * 
     * @return
     */
    public int getNumberOfFiles() {
        return repository != null ? repository.countFiles() : 0;
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFilesList() {
        if (repository != null) {
            return repository.getAudioFilesList();
        }
        return new ArrayList<AudioFile>();
    }

    /**
     * Gets the audio files.
     * 
     * @return
     */
    public Map<String, AudioFile> getAudioFilesMap() {
        if (repository != null) {
            return repository.getAudioFiles();
        }
        return new HashMap<String, AudioFile>();
    }

    /**
     * Gets the audio files for albums.
     * 
     * @param albums
     *            the albums
     * 
     * @return the audio files for albums
     */
    public List<AudioFile> getAudioFilesForAlbums(Map<String, Album> albums) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (Map.Entry<String, Album> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioFiles());
        }
        return result;
    }

    /**
     * Gets the auio files for artists.
     * 
     * @param artists
     *            the artists
     * 
     * @return the audio files for artists
     */
    public List<AudioFile> getAudioFilesForArtists(Map<String, Artist> artists) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            result.addAll(entry.getValue().getAudioFiles());
        }
        return result;
    }

    /**
     * Returns true if folder is in repository.
     * 
     * @param folder
     *            the folder
     * 
     * @return true, if checks if is repository
     */
    public boolean isRepository(File folder) {
        if (repository == null) {
            return false;
        }
        String path = folder.getAbsolutePath();
        for (File folders : repository.getFolders()) {
            if (path.startsWith(folders.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notify cancel.
     */
    public void notifyCancel() {
        currentLoader.interruptLoad();
        repository = currentLoader.getOldRepository();
        notifyFinishRepositoryRead();
    }

    @Override
    public void notifyCurrentPath(final String dir) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressDialog.getFolderLabel().setText(dir);
                }
            });
        }
    }

    @Override
    public void notifyFileLoaded() {
        this.filesLoaded++;
        // Update GUI every 25 files
        if (this.filesLoaded % 25 == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.getProgressLabel().setText(Integer.toString(filesLoaded));
                        progressDialog.getProgressBar().setValue(filesLoaded);
                    }
                    GuiHandler.getInstance().getProgressBar().setValue(filesLoaded);
                }
            });
        }
    }

    @Override
    public void notifyFilesInRepository(int totalFiles) {
        getLogger().debugMethodCall(LogCategories.REPOSITORY, new String[] { Integer.toString(totalFiles) });
        // When total files has been calculated change to determinate progress bar
        if (progressDialog != null) {
            progressDialog.getProgressBar().setIndeterminate(false);
            progressDialog.getTotalFilesLabel().setText(StringUtils.getString(totalFiles));
            progressDialog.getProgressBar().setMaximum(totalFiles);
        }
        GuiHandler.getInstance().getProgressBar().setIndeterminate(false);
        GuiHandler.getInstance().getProgressBar().setMaximum(totalFiles);
    }

    @Override
    public void notifyFinishRead(RepositoryLoader loader) {
        if (progressDialog != null) {
            progressDialog.setButtonsEnabled(false);
            progressDialog.getLabel().setText(I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
            progressDialog.getProgressLabel().setText("");
            progressDialog.getTotalFilesLabel().setText("");
            progressDialog.getFolderLabel().setText(" ");
        }

        // Save folders: if repository config is lost application can reload data without asking user to select folders again
        List<String> repositoryFolders = new ArrayList<String>();
        for (File folder : repository.getFolders()) {
            repositoryFolders.add(folder.getAbsolutePath());
        }
        ApplicationState.getInstance().setLastRepositoryFolders(repositoryFolders);

        if (backgroundLoad) {
            GuiHandler.getInstance().hideProgressBar();
        }

        notifyFinishRepositoryRead();
    }

    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
        enableRepositoryActions(true);

        GuiHandler.getInstance().hideProgressBar();
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
        if (ControllerProxy.getInstance().getNavigationController() != null) {
            ControllerProxy.getInstance().getNavigationController().notifyReload();
        }
        getLogger().info(LogCategories.REPOSITORY, "Repository refresh done");

        currentLoader = null;
    }

    /**
     * Notify finish repository read.
     */
    private void notifyFinishRepositoryRead() {
        enableRepositoryActions(true);
        if (progressDialog != null) {
            progressDialog.hideProgressDialog();
            progressDialog.dispose();
            progressDialog = null;
        }
        ControllerProxy.getInstance().getNavigationController().notifyReload();
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());

        currentLoader = null;
    }

    @Override
    public void notifyRemainingTime(final long millis) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressDialog.getRemainingTimeLabel().setText(StringUtils.getString(I18nUtils.getString("REMAINING_TIME"), ":   ", StringUtils.milliseconds2String(millis)));
                }
            });
        }
    }

    @Override
    public void notifyReadProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ControllerProxy.getInstance().getNavigationController().notifyReload();
                GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
            }
        });
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                // This is the first access to repository, so execute the command defined by user
                String command = ApplicationState.getInstance().getCommandBeforeAccessRepository();
                if (command != null && !command.trim().equals("")) {
                    try {
                        Process p = Runtime.getRuntime().exec(command);
                        // Wait process to end
                        p.waitFor();
                        int rc = p.exitValue();
                        getLogger().info(LogCategories.START, StringUtils.getString("Command '", command, "' return code: ", rc));
                    } catch (Exception e) {
                        getLogger().error(LogCategories.START, e);
                    }
                }
                repositoryRetrievedFromCache = ApplicationStateHandler.getInstance().retrieveRepositoryCache();
            }
        };
    }

    /**
     * Sets the repository.
     */
    public void applyRepository() {
        // Try to read repository cache. If fails or not exists, should be selected again
        Repository rep = repositoryRetrievedFromCache;
        if (rep != null) {
            if (!rep.exists()) {
                askUserForRepository(rep);
                if (!rep.exists() && !selectRepository(true)) {
                    // select "old" repository if repository was not found and no new repository was selected
                    repository = rep;
                } else if (rep.exists()) {
                    // repository exists
                    applyExistingRepository(rep);
                }
            } else {
                // repository exists
                applyExistingRepository(rep);
            }
        } else {
            reloadExistingRepository();
        }
    }

    /**
     * Test if repository exists and show message until repository exists or
     * user doesn't press "RETRY"
     * 
     * @param rep
     */
    private void askUserForRepository(final Repository rep) {
        Object selection;
        do {
            String exitString = Actions.getAction(ExitAction.class).getValue(Action.NAME).toString();
            selection = GuiHandler.getInstance().showMessage(StringUtils.getString(I18nUtils.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getFolders().get(0)),
                    I18nUtils.getString("REPOSITORY_NOT_FOUND"), JOptionPane.WARNING_MESSAGE,
                    new String[] { I18nUtils.getString("RETRY"), I18nUtils.getString("SELECT_REPOSITORY"), exitString });

            if (selection.equals(exitString)) {
                SwingUtilities.invokeLater(new ExitRunnable());
            }
        } while (I18nUtils.getString("RETRY").equals(selection) && !rep.exists());
    }

    private void applyExistingRepository(Repository rep) {
        repository = rep;
        notifyFinishRead(null);
    }

    /**
     * If any repository was loaded previously, try to reload folders
     */
    private void reloadExistingRepository() {
        List<String> lastRepositoryFolders = ApplicationState.getInstance().getLastRepositoryFolders();
        if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
            List<File> foldersToRead = new ArrayList<File>();
            for (String f : lastRepositoryFolders) {
                foldersToRead.add(new File(f));
            }
            GuiHandler.getInstance().showMessage(I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
            retrieve(foldersToRead);
        } else {
            GuiHandler.getInstance().showRepositorySelectionInfoDialog();
            selectRepository();
        }
    }

    /**
     * Read repository.
     * 
     * @param folders
     *            the folders
     */
    private void readRepository(List<File> folders) {
        backgroundLoad = false;
        Repository oldRepository = repository;
        repository = new Repository(folders);
        currentLoader = new RepositoryLoader(folders, oldRepository, repository, false);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refresh.
     */
    private void refresh() {
        getLogger().info(LogCategories.REPOSITORY, "Refreshing repository");
        filesLoaded = 0;
        Repository oldRepository = repository;
        repository = new Repository(oldRepository.getFolders());
        currentLoader = new RepositoryLoader(oldRepository.getFolders(), oldRepository, repository, true);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refreshes a file after being modified
     * 
     * @param file
     *            the file
     */
    public void refreshFile(AudioFile file) {
        RepositoryLoader.refreshFile(repository, file);
    }

    /**
     * Refresh repository.
     */
    public void refreshRepository() {
        if (!repositoryIsNull()) {
            String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
            GuiHandler.getInstance().showProgressBar(true, text);
            enableRepositoryActions(false);
            refresh();
        }
    }

    /**
     * Removes a list of folders from repository.
     * 
     * @param foldersToRemove
     */
    public void removeFolders(List<Folder> foldersToRemove) {
        for (Folder folder : foldersToRemove) {

            // Remove content
            remove(folder.getAudioFiles());

            // Remove from model
            if (folder.getParentFolder() != null) {
                folder.getParentFolder().removeFolder(folder);
            }

            // Update navigator
            ControllerProxy.getInstance().getNavigationController().notifyReload();
        }
    }

    /**
     * Removes a list of files from repository
     * 
     * @param filesToRemove
     *            Files that should be removed
     */
    public void remove(List<AudioFile> filesToRemove) {
        if (filesToRemove == null || filesToRemove.isEmpty()) {
            return;
        }

        for (AudioFile fileToRemove : filesToRemove) {
            RepositoryLoader.deleteFile(fileToRemove);
        }

        // Notify listeners
        for (AudioFilesRemovedListener listener : audioFilesRemovedListeners) {
            listener.audioFilesRemoved(filesToRemove);
        }
    }

    /**
     * Renames an audio file
     * 
     * @param audioFile
     *            the audio file that should be renamed
     * @param name
     *            the new name of the audio file
     */
    public void rename(AudioFile audioFile, String name) {
        File file = audioFile.getFile();
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        File newFile = new File(StringUtils.getString(file.getParentFile().getAbsolutePath() + "/" + FileNameUtils.getValidFileName(name) + "." + extension));
        boolean succeeded = file.renameTo(newFile);
        if (succeeded) {
            RepositoryLoader.renameFile(audioFile, file, newFile);
            ControllerProxy.getInstance().getNavigationController().notifyReload();
            StatisticsHandler.getInstance().updateFileName(file.getAbsolutePath(), newFile.getAbsolutePath());
        }
    }

    /**
     * Repository is null.
     * 
     * @return true, if successful
     */
    public boolean repositoryIsNull() {
        return repository == null;
    }

    /**
     * Retrieve.
     * 
     * @param folders
     *            the folders
     * 
     * @return true, if successful
     */
    public boolean retrieve(List<File> folders) {
        enableRepositoryActions(false);
        progressDialog = GuiHandler.getInstance().getProgressDialog();
        // Start with indeterminate dialog
        progressDialog.showProgressDialog();
        progressDialog.getProgressBar().setIndeterminate(true);
        GuiHandler.getInstance().getProgressBar().setIndeterminate(true);
        filesLoaded = 0;
        try {
            if (folders == null || folders.isEmpty()) {
                repository = null;
                return false;
            }
            readRepository(folders);
            return true;
        } catch (Exception e) {
            repository = null;
            getLogger().error(LogCategories.REPOSITORY, e);
            return false;
        }
    }

    /**
     * Select repository.
     * 
     * @return true, if successful
     */
    public boolean selectRepository() {
        return selectRepository(false);
    }

    /**
     * Select repository.
     * 
     * @param repositoryNotFound
     *            the repository not found
     * 
     * @return true, if successful
     */
    private boolean selectRepository(boolean repositoryNotFound) {
        MultiFolderSelectionDialog dialog = GuiHandler.getInstance().getMultiFolderSelectionDialog();
        dialog.setTitle(I18nUtils.getString("SELECT_REPOSITORY"));
        dialog.setText(I18nUtils.getString("SELECT_REPOSITORY_FOLDERS"));
        dialog.startDialog((repository != null && !repositoryNotFound) ? repository.getFolders() : null);
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            if (!folders.isEmpty()) {
                this.retrieve(folders);
                return true;
            }
        }
        return false;
    }

    /**
     * Imports folders to repository
     */
    public void importFoldersToRepository() {
    }

    /**
     * Imports folders passed as argument to repository
     * 
     * @param folders
     * @param path
     */
    public void importFolders(final List<File> folders, final String path) {
        final ProgressDialog progressDialog = GuiHandler.getInstance().getNewProgressDialog(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."),
                GuiHandler.getInstance().getFrame().getFrame());
        progressDialog.disableCancelButton();
        progressDialog.setVisible(true);
        SwingWorker<List<AudioFile>, Void> worker = new ImportFoldersSwingWorker(folders, path, progressDialog);
        worker.execute();
    }

    /**
     * Adds a listener to be notified when an audio file is removed
     * 
     * @param listener
     */
    public void addAudioFilesRemovedListener(AudioFilesRemovedListener listener) {
        audioFilesRemovedListeners.add(listener);
    }

    @Override
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        // Update status bar
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

    public void doInBackground() {
        if (currentLoader != null) {
            backgroundLoad = true;
            currentLoader.setPriority(Thread.MIN_PRIORITY);
            if (progressDialog != null) {
                progressDialog.hideProgressDialog();
            }
            GuiHandler.getInstance().showProgressBar(false, StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
            GuiHandler.getInstance().getProgressBar().addMouseListener(progressBarMouseAdapter);
        }

    }

    /**
     * Enables or disables actions that can't be performed while loading
     * repository
     * 
     * @param enable
     */
    private void enableRepositoryActions(boolean enable) {
        Actions.getAction(SelectRepositoryAction.class).setEnabled(enable);
        Actions.getAction(RefreshRepositoryAction.class).setEnabled(enable);
        Actions.getAction(ImportToRepositoryAction.class).setEnabled(enable);
        Actions.getAction(ExportAction.class).setEnabled(enable);
        Actions.getAction(ConnectDeviceAction.class).setEnabled(enable);
        Actions.getAction(RipCDAction.class).setEnabled(enable);
    }

    /**
     * Returns <code>true</code>if there is a loader reading or refreshing
     * repository
     * 
     * @return
     */
    protected boolean isLoaderWorking() {
        return currentLoader != null;
    }

    /**
     * Brings access to logger to all classes of this package
     * 
     * @return
     */
    protected static Logger getRepositoryHandlerLogger() {
        return getLogger();
    }

    /**
     * Returns folder where repository configuration is stored
     * 
     * @return
     */
    public String getRepositoryConfigurationFolder() {
        String customRepositoryConfigFolder = SystemProperties.getCustomRepositoryConfigFolder();
        return customRepositoryConfigFolder != null ? customRepositoryConfigFolder : SystemProperties.getUserConfigFolder(Kernel.isDebug());
    }
}
