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

package net.sourceforge.atunes.kernel.modules.audioobjetproperties;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * The audio object properties controller.
 * 
 * @author fleax
 */
final class AudioObjectPropertiesController extends AbstractSimpleController<AudioObjectPropertiesPanel> {

    private final class FillPictureSwingWorker extends
			SwingWorker<ImageIcon, Void> {
		private boolean shadowBorder;

		@Override
		protected ImageIcon doInBackground() throws Exception {
		    ImageIcon imageForAudioObject;
		    imageForAudioObject = currentAudioObject.getImage(ImageSize.SIZE_90);
		    if (imageForAudioObject == null) {
		        imageForAudioObject = currentAudioObject.getGenericImage(GenericImageSize.MEDIUM);
		    }
		    shadowBorder = currentAudioObject instanceof AudioFile;
		    return imageForAudioObject;
		}

		@Override
		protected void done() {
		    try {
		        ImageIcon imageIcon = get();
		        if (shadowBorder) {
		            getComponentControlled().getPictureLabel().setBorder(new DropShadowBorder());
		        } else {
		            getComponentControlled().getPictureLabel().setBorder(BorderFactory.createEmptyBorder());
		        }
		        if (imageIcon != null) {
		            getComponentControlled().getPictureLabel().setIcon(imageIcon);
		            getComponentControlled().getPictureLabel().setVisible(true);
		        } else {
		            getComponentControlled().getPictureLabel().setIcon(null);
		            getComponentControlled().getPictureLabel().setVisible(false);
		        }
		    } catch (InterruptedException e) {
		        getLogger().internalError(e);
		    } catch (ExecutionException e) {
		        getLogger().internalError(e);
		    }
		}
	}

	/** The current audio object. */
    private AudioObject currentAudioObject;

    /**
     * Instantiates a new audio object properties controller.
     * 
     * @param panelControlled
     *            the panel controlled
     */
    AudioObjectPropertiesController(AudioObjectPropertiesPanel pControlled) {
        super(pControlled);        
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        getComponentControlled().getPictureLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GuiHandler.getInstance().showImageDialog(currentAudioObject);
            }
        });
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Clears the main panel.
     */
    void clearPanel() {
        currentAudioObject = null;
        getComponentControlled().getPictureLabel().setIcon(null);
        getComponentControlled().getPictureLabel().setVisible(false);
        getComponentControlled().getMainPanel().setVisible(false);
    }

    /**
     * Fill file properties.
     */
    void fillFileProperties() {
        if (currentAudioObject != null) {
            getComponentControlled().getBitrateLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("BITRATE"), ":</b>    ", currentAudioObject.getBitrate(), " Kbps"));
            getComponentControlled().getFrequencyLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("FREQUENCY"), ":</b>    ", currentAudioObject.getFrequency(), " Hz"));
        } else {
            getComponentControlled().getBitrateLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("BITRATE"), ":</b>    "));
            getComponentControlled().getFrequencyLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("FREQUENCY"), ":</b>    "));
        }

    }

    /**
     * Fill picture.
     */
    void fillPicture() {
        if (currentAudioObject != null) {
            new FillPictureSwingWorker().execute();
        }

    }

    /**
     * Returns super logger
     */
    @Override
    protected Logger getLogger() {
        return super.getLogger();
    }

    /**
     * Fill song properties.
     */
    void fillSongProperties() {
        if (currentAudioObject != null) {

            // \u202D is Unicode symbol for orientation override to LTR. 
            getComponentControlled().getUrlLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("FILE"), ":</b>    ", "\u202D", currentAudioObject.getUrl(), " \u202C </html>"));

            getComponentControlled().getTitleLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("SONG"), ":</b>    ", currentAudioObject.getTitleOrFileName(), " - ", currentAudioObject.getArtist(),
                            " - ", currentAudioObject.getAlbum(), "\u202D (", StringUtils.seconds2String(currentAudioObject.getDuration()), ") \u202C </html>"));

            if (currentAudioObject.getTrackNumber() > 0) {
                getComponentControlled().getTrackLabel()
                        .setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":</b>    ", currentAudioObject.getTrackNumber()));
            } else {
                getComponentControlled().getTrackLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":"));
            }

            if (currentAudioObject.getDate() != null) {
                getComponentControlled().getYearLabel().setText(
                        StringUtils.getString("<html><b>", I18nUtils.getString("DATE"), ":</b>    ", DateFormat.getDateInstance().format(currentAudioObject.getDate())));
            } else if (!currentAudioObject.getYear().isEmpty()) {
                getComponentControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":</b>    ", currentAudioObject.getYear()));
            } else {
                getComponentControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":"));
            }

            if (currentAudioObject.getDiscNumber() > 0) {
                getComponentControlled().getDiscNumberLabel().setText(
                        StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":</b>    ", currentAudioObject.getDiscNumber()));
            } else {
                getComponentControlled().getDiscNumberLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":"));
            }

            getComponentControlled().getGenreLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("GENRE"), ":</b>    ", currentAudioObject.getGenre()));

            // Favorite icons
            refreshFavoriteIcons();
        } else {
            getComponentControlled().getUrlLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("FILE"), ":</b>    "));
            getComponentControlled().getTitleLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("SONG"), ":</b>    "));
            getComponentControlled().getTrackLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":</b>    "));
            getComponentControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":</b>    "));
            getComponentControlled().getGenreLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("GENRE"), ":</b>    "));
            getComponentControlled().getDiscNumberLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":</b>    "));
        }
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Only show properties panel.
     */
    void onlyShowPropertiesPanel() {
        currentAudioObject = null;
        // Song properties
        fillSongProperties();
        // File Properties
        fillFileProperties();
        // Picture
        fillPicture();
        getComponentControlled().getMainPanel().setVisible(true);
    }

    /**
     * Refresh favorite icons.
     */
    void refreshFavoriteIcons() {
        if (currentAudioObject != null) {
            boolean favorite = FavoritesHandler.getInstance().getFavoriteSongsInfo().containsValue(currentAudioObject)
                    || FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsKey(currentAudioObject.getArtist())
                    || FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsKey(currentAudioObject.getAlbum());

            getComponentControlled().getTitleLabel().setIcon(favorite ? Images.getImage(Images.FAVORITE) : null);
        }
    }

    /**
     * Refresh picture.
     */
    void refreshPicture() {
        fillPicture();
    }

    /**
     * Update values.
     * 
     * @param audioObject
     *            the audio object that should be shown in the audio object
     *            properties panel
     */
    void updateValues(final AudioObject audioObject) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateValuesEDT(audioObject);
                }
            });
        } else {
            updateValuesEDT(audioObject);
        }
    }

    /**
     * Update values.
     * 
     * @param audioObject
     *            the audio object that should be shown in the audio object
     *            properties panel
     */
    private void updateValuesEDT(AudioObject audioObject) {
        if (audioObject != null) {
            getLogger().debug(LogCategories.CONTROLLER, audioObject.getUrl());
            currentAudioObject = audioObject;
            // Song properties
            fillSongProperties();
            // File Properties
            fillFileProperties();
            // Picture
            fillPicture();
            getComponentControlled().getMainPanel().setVisible(true);
        } else {
            clearPanel();
        }
    }
}