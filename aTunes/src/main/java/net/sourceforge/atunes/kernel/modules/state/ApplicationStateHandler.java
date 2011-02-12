/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Repository;
import net.sourceforge.atunes.kernel.modules.repository.exception.InconsistentRepositoryException;
import net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.repository.statistics.Statistics;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

/**
 * This class is responsible of read, write and apply application state, and
 * caches.
 */
public final class ApplicationStateHandler extends AbstractHandler {

    /** The instance. */
    private static ApplicationStateHandler instance = new ApplicationStateHandler();

    /**
     * Listeners of the state of the application
     */
    private Set<ApplicationStateChangeListener> stateChangeListeners;

    @Override
    protected void initHandler() {
    }

    /**
     * Gets the single instance of ApplicationDataHandler.
     * 
     * @return single instance of ApplicationDataHandler
     */
    public static ApplicationStateHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
    }

    /**
     * Adds a new ApplicationStateChangeListener. This listener will be notified
     * when application state is changed
     * 
     * @param listener
     */
    public void addStateChangeListener(ApplicationStateChangeListener listener) {
        if (stateChangeListeners == null) {
            stateChangeListeners = new HashSet<ApplicationStateChangeListener>();
        }
        stateChangeListeners.add(listener);
    }

    /**
     * Removes an ApplicationStateChangeListener. This listener will not be
     * notified again when application state is changed
     * 
     * @param listener
     */
    public void removeStateChangeListener(ApplicationStateChangeListener listener) {
        if (stateChangeListeners == null) {
            return;
        }
        stateChangeListeners.remove(listener);
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // Nothing to do
    }

    /**
     * Notifies all listeners of an application state change
     */
    public void notifyApplicationStateChanged() {
        try {
            for (ApplicationStateChangeListener listener : stateChangeListeners) {
                getLogger().debug(LogCategories.HANDLER, "Call to ApplicationStateChangeListener: ", listener.getClass().getName());
                listener.applicationStateChanged(ApplicationState.getInstance());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void applicationFinish() {
    }

    /**
     * Stores favorites cache.
     * 
     * @param favorites
     *            Favorites that should be persisted
     */
    public void persistFavoritesCache(Favorites favorites) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_FAVORITES_NAME)));
            getLogger().info(LogCategories.HANDLER, "Storing favorites information...");
            stream.writeObject(favorites);
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not write favorites");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(stream);
        }

        if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                XMLUtils.writeObjectToFile(favorites, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
                getLogger().info(LogCategories.HANDLER, "Storing favorites information...");
            } catch (Exception e) {
                getLogger().error(LogCategories.HANDLER, "Could not write favorites");
                getLogger().debug(LogCategories.HANDLER, e);
            }
        }
    }

    /**
     * Stores statistics cache.
     * 
     * @param statistics
     *            Statistics that should be persisted
     */
    public synchronized void persistStatisticsCache(Statistics statistics) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_STATISTICS_NAME)));
            getLogger().info(LogCategories.HANDLER, "Storing statistics information...");
            stream.writeObject(statistics);
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not write statistics");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(stream);
        }

        if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                XMLUtils.writeObjectToFile(statistics, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                getLogger().info(LogCategories.HANDLER, "Storing statistics information...");
            } catch (Exception e) {
                getLogger().error(LogCategories.HANDLER, "Could not write statistics");
                getLogger().debug(LogCategories.HANDLER, e);
            }
        }
    }

    /**
     * Stores play lists definition
     */
    public void persistPlayListsDefinition(ListOfPlayLists listOfPlayLists) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(listOfPlayLists, StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
            getLogger().info(LogCategories.HANDLER, "Playlists definition saved");
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist playlists definition");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Stores play lists contents
     * 
     * @param playListsContents
     */
    public void persistPlayListsContents(List<List<AudioObject>> playListsContents) {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_CONTENTS_FILE)));
            stream.writeObject(playListsContents);
            getLogger().info(LogCategories.HANDLER, "Playlists contents saved");
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist playlists contents");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Stores podcast feeds.
     * 
     * @param podcastFeeds
     *            Podcast feeds that should be persist
     */
    public void persistPodcastFeedCache(List<PodcastFeed> podcastFeeds) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(podcastFeeds, StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist podcast feeds");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Stores radios.
     * 
     * @param radios
     *            Radios that should be persisted
     */
    public void persistRadioCache(List<Radio> radios) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist radios");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Persist preset radio cache.
     * 
     * @param radios
     *            the radios
     */
    public void persistPresetRadioCache(List<Radio> radios) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist radios");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Stores repository cache.
     * 
     * @param repository
     *            The retrieved repository
     */

    public void persistRepositoryCache(Repository repository, boolean asXmlIfEnabled) {
        getLogger().debug(LogCategories.HANDLER);

        String folder = RepositoryHandler.getInstance().getRepositoryConfigurationFolder();

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils.getString(folder, "/", Constants.CACHE_REPOSITORY_NAME));
            oos = new ObjectOutputStream(fout);
            getLogger().info(LogCategories.HANDLER, "Serialize repository information...");
            Timer timer = new Timer();
            timer.start();
            oos.writeObject(repository);
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", timer.stop(), " seconds)"));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not write serialized repository");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(oos);
        }

        if (asXmlIfEnabled && ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                getLogger().info(LogCategories.HANDLER, "Storing repository information as xml...");
                Timer timer = new Timer();
                timer.start();
                XMLUtils.writeObjectToFile(repository, StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));
                getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", timer.stop(), " seconds)"));
            } catch (IOException e) {
                getLogger().error(LogCategories.HANDLER, "Could not write repository as xml");
                getLogger().debug(LogCategories.HANDLER, e);
            }
        }
    }

    public void persistDeviceCache(String deviceId, Repository deviceRepository) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils
                    .getString(getUserConfigFolder(), SystemProperties.FILE_SEPARATOR, Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            oos = new ObjectOutputStream(fout);
            getLogger().info(LogCategories.HANDLER, "Serialize device information...");
            long t0 = System.currentTimeMillis();
            oos.writeObject(deviceRepository);
            long t1 = System.currentTimeMillis();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", (t1 - t0) / 1000.0, " seconds)"));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Could not write serialized device");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(oos);
        }
    }

    /**
     * Reads favorites cache.
     * 
     * @return The retrieved favorites
     */
    public Favorites retrieveFavoritesCache() {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_FAVORITES_NAME)));
            getLogger().info(LogCategories.HANDLER, "Reading serialized favorites cache");
            return (Favorites) stream.readObject();
        } catch (InvalidClassException e) {
            //TODO remove in next version
            getLogger().error(LogCategories.HANDLER, e);
            return new Favorites();
        } catch (IOException e) {
            getLogger().info(LogCategories.HANDLER, "No serialized favorites info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml favorites cache");
                    return (Favorites) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
                } catch (IOException e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml favorites info found");
                    return new Favorites();
                }
            }
            return new Favorites();
        } catch (ClassNotFoundException e) {
            getLogger().info(LogCategories.HANDLER, "No serialized favorites info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml favorites cache");
                    return (Favorites) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
                } catch (IOException e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml favorites info found");
                    return new Favorites();
                }
            }
            return new Favorites();
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Reads statistics cache.
     * 
     * @return The retrieved favorites
     */
    public Statistics retrieveStatisticsCache() {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_STATISTICS_NAME)));
            getLogger().info(LogCategories.HANDLER, "Reading serialized statistics cache");
            return (Statistics) stream.readObject();
        } catch (InvalidClassException e) {
            getLogger().error(LogCategories.HANDLER, e);
        } catch (ClassCastException e) {
            getLogger().error(LogCategories.HANDLER, e);
        } catch (IOException e) {
            getLogger().info(LogCategories.HANDLER, "No serialized statistics info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml statistics cache");
                    return (Statistics) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml statistics info found");
                }
            }
        } catch (ClassNotFoundException e) {
            getLogger().info(LogCategories.HANDLER, "No serialized statistics info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml statistics cache");
                    return (Statistics) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml statistics info found");
                }
            }
        } finally {
            ClosingUtils.close(stream);
        }
        // If some 
        return new Statistics();
    }

    /**
     * Reads playlists cache.
     * 
     * @return The retrieved playlists
     */

    @SuppressWarnings("unchecked")
    public ListOfPlayLists retrievePlayListsCache() {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream stream = null;
        try {
            // First get list of playlists
            ListOfPlayLists listOfPlayLists = (ListOfPlayLists) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("List of playlists loaded"));

            // Then read contents
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_CONTENTS_FILE)));
            List<List<AudioObject>> contents = (List<List<AudioObject>>) stream.readObject();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Playlists contents loaded"));
            if (contents.size() == listOfPlayLists.getPlayLists().size()) {
                listOfPlayLists.setContents(contents);
            }

            return listOfPlayLists;
        } catch (FileNotFoundException e) {
            getLogger().info(LogCategories.HANDLER, "No playlist information found");
            return ListOfPlayLists.getEmptyPlayList();
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, e);
            return ListOfPlayLists.getEmptyPlayList();
        } catch (ClassNotFoundException e) {
            getLogger().error(LogCategories.HANDLER, e);
            return ListOfPlayLists.getEmptyPlayList();
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Reads podcast feed cache.
     * 
     * @return The retrieved podcast feeds
     */
    @SuppressWarnings("unchecked")
    public List<PodcastFeed> retrievePodcastFeedCache() {
        getLogger().debug(LogCategories.HANDLER);

        try {
            return (List<PodcastFeed>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            /*
             * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
             * java.util.ArrayList to avoid ConcurrentModificationException
             */
            return new CopyOnWriteArrayList<PodcastFeed>();
        }
    }

    /**
     * Reads radio cache.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadioCache() {
        getLogger().debug(LogCategories.HANDLER);
        try {
            return (List<Radio>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            return new ArrayList<Radio>();
        }

    }

    /**
     * Reads radio cache. Preset stations. This file is not meant to be edited.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadioPreset() {
        getLogger().debug(LogCategories.HANDLER);
        try {
            // First try user settings folder
            return (List<Radio>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            try {
                // Otherwise use list in application folder
                return (List<Radio>) XMLUtils.readObjectFromFile(ApplicationStateHandler.class.getResourceAsStream("/settings/" + Constants.PRESET_RADIO_CACHE));
            } catch (IOException e2) {
                return new ArrayList<Radio>();
            }
        }
    }

    /**
     * Reads repository cache.
     * 
     * @return The retrieved repository
     */
    public Repository retrieveRepositoryCache() {
        getLogger().debug(LogCategories.HANDLER);

        String folder = RepositoryHandler.getInstance().getRepositoryConfigurationFolder();

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(folder, "/", Constants.CACHE_REPOSITORY_NAME));
            ois = new ObjectInputStream(fis);
            getLogger().info(LogCategories.HANDLER, "Reading serialized repository cache");
            Timer timer = new Timer();
            timer.start();
            Repository result = (Repository) ois.readObject();

            // Check repository integrity
            result.validateRepository();

            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", timer.stop(), " seconds)"));
            return result;
        } catch (InvalidClassException e) {
            //TODO remove in next version
            getLogger().error(LogCategories.HANDLER, e);
            return null;
        } catch (IOException e) {
        	return exceptionReadingRepository(folder);
        } catch (ClassNotFoundException e) {
        	return exceptionReadingRepository(folder);
        } catch (InconsistentRepositoryException e) {
        	return exceptionReadingRepository(folder);
        } finally {
            ClosingUtils.close(ois);
        }
    }
    
    private Repository exceptionReadingRepository(String folder) {
        getLogger().info(LogCategories.HANDLER, "No serialized repository info found");
        if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                getLogger().info(LogCategories.HANDLER, "Reading xml repository cache");
                long t0 = System.currentTimeMillis();
                Repository repository = (Repository) XMLUtils.readObjectFromFile(StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));

                // Check repository integrity
                repository.validateRepository();

                long t1 = System.currentTimeMillis();
                getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));
                
                // Save repository again to avoid reading XML in next start
                repository.setDirty(true);
                
                return repository;
            } catch (IOException e1) {
                getLogger().info(LogCategories.HANDLER, "No xml repository info found");
                return null;
            } catch (InconsistentRepositoryException e1) {
                getLogger().info(LogCategories.HANDLER, "No xml repository info found");
                return null;
            }
        }
        return null;    	
    }

    /**
     * Reads device cache.
     * 
     * @return The retrieved device
     */

    public Repository retrieveDeviceCache(String deviceId) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(getUserConfigFolder(), SystemProperties.FILE_SEPARATOR, Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            ois = new ObjectInputStream(fis);
            getLogger().info(LogCategories.HANDLER, "Reading serialized device cache");
            long t0 = System.currentTimeMillis();
            Repository result = (Repository) ois.readObject();
            long t1 = System.currentTimeMillis();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading device cache done (", (t1 - t0) / 1000.0, " seconds)"));
            return result;
        } catch (IOException e) {
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;
        } catch (ClassNotFoundException e) {
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;
        } finally {
            ClosingUtils.close(ois);
        }
    }

    private String getUserConfigFolder() {
        return SystemProperties.getUserConfigFolder(Kernel.isDebug());
    }

}
