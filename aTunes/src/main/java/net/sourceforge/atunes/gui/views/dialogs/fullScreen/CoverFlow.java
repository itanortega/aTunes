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

package net.sourceforge.atunes.gui.views.dialogs.fullScreen;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.views.controls.Cover3D;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.Logger;

public final class CoverFlow extends JPanel {

    private final class PaintCoversSwingWorker extends SwingWorker<Void, Void> {
		private final Cover3D cover;
		private final IAudioObject audioObject;
		private final int index;
		private IOSManager osManager;
		private ILookAndFeel lookAndFeel;

		private PaintCoversSwingWorker(Cover3D cover, IAudioObject audioObject, int index, IOSManager osManager, ILookAndFeel lookAndFeel) {
			this.cover = cover;
			this.audioObject = audioObject;
			this.index = index;
			this.osManager = osManager;
			this.lookAndFeel = lookAndFeel;
		}

		@Override
		protected Void doInBackground() throws Exception {
			Image image = null;
		    if (audioObject instanceof IRadio) {
		        image = RadioImageIcon.getBigIcon(Color.WHITE, lookAndFeel).getImage();
		    } else if (audioObject instanceof IPodcastFeedEntry) {
		        image = RssImageIcon.getBigIcon(Color.WHITE, lookAndFeel).getImage();
		    } else {
	    		image = getPicture((AudioFile) audioObject, osManager);
		    }
		    
	        if (cover != null) {
	            if (image == null) {
	                cover.setImage(null, 0, 0);
	            } else if (audioObject == null) {
	                cover.setImage(Images.getImage(Images.APP_LOGO_300).getImage(), getImageSize(covers.indexOf(cover)), getImageSize(covers.indexOf(cover)));
	            } else {
	                cover.setImage(image, getImageSize(index), getImageSize(index));
	            }
	        }
	        
	        return null;
		}

		@Override
		protected void done() {
		    try {
		        get();
		        if (cover != null) {
		        	cover.repaint();
		        }
		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
		    }
		}
	}

	private static final long serialVersionUID = -5982158797052430789L;

    private List<Cover3D> covers;
    
    private ILookAndFeel lookAndFeel;

    CoverFlow(ILookAndFeel lookAndFeel) {
        super(new GridBagLayout());
        this.lookAndFeel = lookAndFeel;
        covers = new ArrayList<Cover3D>();
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));

        setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(covers.get(0), c);
        c.gridx = 1;
        add(covers.get(1), c);
        c.gridx = 2;
        c.weightx = 0.3;
        add(covers.get(2), c);
        c.gridx = 3;
        c.weightx = 0.2;
        add(covers.get(3), c);
        c.gridx = 4;
        add(covers.get(4), c);
    }

    /**
     * Paint.
     * 
     * @param objects
     * @param osManager
     */
    void paint(final List<IAudioObject> objects, IOSManager osManager) {
        int i = 0;
        for (IAudioObject ao : objects) {
            paint(ao, i < covers.size() ? covers.get(i) : null, i == 2, i, osManager);
            i++;
        }
    }

    private void paint(final IAudioObject audioObject, final Cover3D cover, boolean current, int index, IOSManager osManager) {
        // No object
        if (audioObject == null) {
            return;
        }

        // Fetch cover
        new PaintCoversSwingWorker(cover, audioObject, index, osManager, lookAndFeel).execute();
    }

    /**
     * Returns picture for audio file
     * 
     * @param audioFile
     * @param osManager
     * @return
     */
    protected Image getPicture(AudioFile audioFile, IOSManager osManager) {
    	Image result = Context.getBean(IWebServicesHandler.class).getAlbumImage(audioFile.getArtist(), audioFile.getAlbum());
        if (result == null) {
            ImageIcon[] pictures = AudioFilePictureUtils.getPicturesForFile(audioFile, -1, -1, osManager);
            if (pictures != null && pictures.length > 0) {
                result = pictures[0].getImage();
            }
        }
        if (result == null) {
            result = Images.getImage(Images.APP_LOGO_300).getImage();
        }
        return result;
    }

    private int getImageSize(int index) {
        if (index == 2) {
            return Constants.FULL_SCREEN_COVER;
        } else if (index == 1 || index == 3) {
            return Constants.FULL_SCREEN_COVER * 3 / 4;
        } else {
            return Constants.FULL_SCREEN_COVER * 9 / 16;
        }
    }
}
