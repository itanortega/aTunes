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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.ProgressSlider;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsSize;
import net.sourceforge.atunes.kernel.modules.player.ProgressBarSeekListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

public final class FullScreenWindow extends AbstractCustomWindow {

    private static final long serialVersionUID = 3422799994808333945L;

    private CoverFlow covers;

    /** The text label. */
    private JLabel textLabel;

    /** The text label 2 */
    private JLabel textLabel2;

    private IProgressSlider progressSlider;
    
    /** The options. */
    private JPopupMenu options;

    /** The play button. */
    private PlayPauseButton playButton;

    private JPanel controlsPanel;

    /** The playing. */
    private boolean playing;

    /** The background. */
    private transient Image background;

    /** The key adapter. */
    private KeyAdapter keyAdapter = new FullScreenKeyAdapter(this);

    private Timer hideMouseTimer;

    private MouseListener clickListener = new FullScreenMouseListener(this);

    private MouseMotionListener moveListener = new FullScreenMouseMotionAdapter(this);

    /**
     * Audio Objects to show in full screen
     */
    private List<IAudioObject> objects;
    
    private IState state;
    
    private IFrame frame;
    
    private IOSManager osManager;
    
    private IPlayerHandler playerHandler;
    
    private Dimension screenSize;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private MuteButton volumeButton;
    
    private VolumeSlider volumeSlider;
    
    private ProgressSlider fullScreenProgressSlider;
    
    /**
     * @param fullScreenProgressSlider
     */
    public void setFullScreenProgressSlider(ProgressSlider fullScreenProgressSlider) {
		this.fullScreenProgressSlider = fullScreenProgressSlider;
	}
    
    /**
     * @param volumeSlider
     */
    public void setVolumeSlider(VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}
    
    /**
     * @param volumeButton
     */
    public void setVolumeButton(MuteButton volumeButton) {
		this.volumeButton = volumeButton;
	}
    
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}

    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    /**
     * @param screenSize
     */
    public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * Instantiates a new full screen dialog.
     * @param frame
     */
    public FullScreenWindow(IFrame frame) {
        super(frame.getFrame(), 0, 0);
        this.frame = frame;
    }
    
    /**
     * Initializes window
     */
    public void initialize() {
        setLocation(0, 0);
        setAlwaysOnTop(true);
        setContent(lookAndFeelManager);
        addKeyListener(keyAdapter);
        File backgroundFile = null;
        if (state.getFullScreenBackground() != null) {
            backgroundFile = new File(state.getFullScreenBackground());
            if (!backgroundFile.exists()) {
                backgroundFile = null;
            }
        }
        if (backgroundFile == null) {
            background = null;
        } else {
            setBackground(backgroundFile);
        }

        addMouseMotionListener(moveListener);
        addMouseListener(clickListener);
    }

    /**
     * Activates timer to hide mouse cursor
     */
    void activateTimer() {
        setCursor(Cursor.getDefaultCursor());
        controlsPanel.setVisible(true);
        if (hideMouseTimer != null) {
            hideMouseTimer.restart();
        } else {
            hideMouseTimer = new Timer(5000, new HideMouseActionListener(this, controlsPanel, options));
        }
    }

    /**
     * Checks if is playing.
     * 
     * @return true, if is playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Sets the audio object.
     * 
     * @param audioObject
     *            the new audio object
     */
    public void setAudioObjects(List<IAudioObject> objects) {
        if (objects == null || objects.isEmpty()) {
            textLabel.setText("");
            textLabel2.setText("");
            return;
        }

        this.objects = objects;

        if (isVisible()) {
            updateWindow();
        }
    }

    private void setText(IAudioObject audioObject) {
        // No object
        if (audioObject == null) {
            textLabel.setText("");
            textLabel2.setText("");
        } else if (audioObject instanceof IRadio) {
            progressSlider.setEnabled(false);
            textLabel.setText(((IRadio) audioObject).getName());
            textLabel2.setText(((IRadio) audioObject).getUrl());
        } else if (audioObject instanceof IPodcastFeedEntry) {
        	progressSlider.setEnabled(false);
            textLabel.setText(((IPodcastFeedEntry) audioObject).getTitle());
            textLabel2.setText(((IPodcastFeedEntry) audioObject).getPodcastFeed().getName());
        } else {
            textLabel.setText(audioObject.getTitleOrFileName());
            textLabel2.setText(audioObject.getArtist());
        }
    }

    /**
     * Sets the background.
     * 
     * @param file
     *            the new background
     */
    void setBackground(File file) {
        try {
            background = ImageIO.read(file);
            state.setFullScreenBackground(file.getAbsolutePath());
        } catch (IOException e) {
        	Logger.error(e);
        }
    }

    /**
     * Sets the content.
     * @param lookAndFeelManager 
     */
    private void setContent(ILookAndFeelManager lookAndFeelManager) {
        final JPanel panel = getPanel();
        add(panel);

        options = new JPopupMenu(I18nUtils.getString("OPTIONS"));
        options.addKeyListener(keyAdapter);

        FullScreenShowMenuMouseAdapter optionsAdapter = new FullScreenShowMenuMouseAdapter(options);
        
        panel.addMouseListener(optionsAdapter);

        JMenuItem selectBackground = new JMenuItem(I18nUtils.getString("SELECT_BACKGROUND"));

        selectBackground.addActionListener(new SelectBackgroundActionListener(this));

        JMenuItem removeBackground = getRemoveBackgroundMenuItem();
        JMenuItem exitFullScreen = getExitFullScreenMenuItem();

        options.add(selectBackground);
        options.add(removeBackground);
        options.add(exitFullScreen);

        PreviousButton previousButton = new PreviousButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        playButton = new PlayPauseButton(PlayerControlsSize.PLAY_BUTTON_SIZE, lookAndFeelManager);
        StopButton stopButton = new StopButton(PlayerControlsSize.STOP_MUTE_BUTTONS_SIZE, lookAndFeelManager);
        NextButton nextButton = new NextButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        volumeButton.setText("");

        covers = new CoverFlow();
        Dimension coverSize = new Dimension(screenSize.width, screenSize.height * 5 / 7);
        covers.setMinimumSize(coverSize);
        covers.setMaximumSize(coverSize);
        covers.setPreferredSize(coverSize);

        setClickListener(previousButton, stopButton, nextButton, volumeButton);

        covers.addMouseListener(optionsAdapter);
        covers.addMouseMotionListener(moveListener);

        textLabel = getTextLabel(lookAndFeelManager);

        textLabel2 = getTextLabel2(lookAndFeelManager);

        progressSlider = getProgressSlider();

        JPanel textAndControlsPanel = new JPanel(new GridLayout(2, 1));
        textAndControlsPanel.setOpaque(false);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);

        JPanel buttonsPanel = PlayerControlsPanel.getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider, lookAndFeelManager);

        setPanels(textAndControlsPanel, textPanel, buttonsPanel);

        textAndControlsPanel.add(controlsPanel);

        panel.add(covers, BorderLayout.CENTER);
        panel.add(textAndControlsPanel, BorderLayout.SOUTH);
    }

	/**
	 * @param textAndControlsPanel
	 * @param textPanel
	 * @param buttonsPanel
	 */
	private void setPanels(JPanel textAndControlsPanel, JPanel textPanel, JPanel buttonsPanel) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 20, 0);
        c.anchor = GridBagConstraints.NORTH;
        textPanel.add(textLabel, c);
        c.gridy = 1;
        textPanel.add(textLabel2, c);

        textAndControlsPanel.add(textPanel);

        controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setOpaque(false);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(progressSlider.getSwingComponent(), c);

        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 1;
        c.insets = new Insets(20, 0, 5, 0);
        controlsPanel.add(buttonsPanel, c);
	}

	/**
	 * 
	 */
	private IProgressSlider getProgressSlider() {
		IProgressSlider slider = fullScreenProgressSlider;
        ProgressBarSeekListener seekListener = new ProgressBarSeekListener(slider, playerHandler);
        slider.addMouseListener(seekListener);        
        slider.addKeyListener(keyAdapter);
        slider.setOpaque(false);
        return slider;
	}

	/**
	 * @param lookAndFeelManager
	 */
	private JLabel getTextLabel2(ILookAndFeelManager lookAndFeelManager) {
		JLabel label = new JLabel();
        label.setFont(lookAndFeelManager.getCurrentLookAndFeel().getContextInformationBigFont());
        label.setForeground(Color.WHITE);
        return label;
	}

	/**
	 * @param lookAndFeelManager
	 */
	private JLabel getTextLabel(ILookAndFeelManager lookAndFeelManager) {
		JLabel label = new JLabel();
        label.setFont(lookAndFeelManager.getCurrentLookAndFeel().getFullScreenLine1Font());
        label.setForeground(Color.WHITE);
        return label;
	}

	/**
	 * @param previousButton
	 * @param stopButton
	 * @param nextButton
	 * @param muteButton
	 */
	private void setClickListener(PreviousButton previousButton, StopButton stopButton, NextButton nextButton, MuteButton muteButton) {
		covers.addMouseListener(clickListener);
        options.addMouseListener(clickListener);
        previousButton.addMouseListener(clickListener);
        playButton.addMouseListener(clickListener);
        stopButton.addMouseListener(clickListener);
        nextButton.addMouseListener(clickListener);
        muteButton.addMouseListener(clickListener);
        volumeSlider.addMouseListener(clickListener);
	}

	/**
	 * @return
	 */
	private JMenuItem getExitFullScreenMenuItem() {
		JMenuItem exitFullScreen = new JMenuItem(I18nUtils.getString("CLOSE"));
        exitFullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
		return exitFullScreen;
	}

	/**
	 * @return
	 */
	private JMenuItem getRemoveBackgroundMenuItem() {
		JMenuItem removeBackground = new JMenuItem(I18nUtils.getString("REMOVE_BACKGROUND"));
        removeBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                background = null;
                FullScreenWindow.this.state.setFullScreenBackground(null);
                FullScreenWindow.this.invalidate();
                FullScreenWindow.this.repaint();
            }
        });
		return removeBackground;
	}

	/**
	 * @return
	 */
	private JPanel getPanel() {
		final JPanel panel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 109708757849271173L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background == null && g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, this.getHeight(), Color.BLACK));
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                } else {
                    Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
                    g.drawImage(ImageUtils.scaleBufferedImageBicubic(background, scrSize.width, scrSize.height), 0, 0, this);
                }
            }
        };
        panel.setBackground(Color.black);
		return panel;
	}

    /**
     * Sets the full screen.
     * @param fullscreen
     * @param frame
     */
    private void setFullScreen(boolean fullscreen, IFrame frame) {
    	osManager.setFullScreen(this, fullscreen, frame);
    }

    /**
     * Sets the max duration.
     * 
     * @param maxDuration
     *            the new max duration
     */
    public void setAudioObjectLength(long maxDuration) {
        progressSlider.setMaximum((int) maxDuration);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
        playButton.setPlaying(playing);
    }

    /**
     * Sets the time.
     * 
     * @param time
     *            the time
     * @param totalTime
     *            the total time
     */
    public void setCurrentAudioObjectPlayedTime(final long time, final long totalTime) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCurrentAudioObjectPlayedTimeEDT(time, totalTime);
                }
            });
        } else {
            setCurrentAudioObjectPlayedTimeEDT(time, totalTime);
        }
    }

    private void setCurrentAudioObjectPlayedTimeEDT(long time, long totalTime) {
        long remainingTime1 = totalTime - time;
        progressSlider.setProgress(time, time == 0 ? time : remainingTime1);
        progressSlider.setValue((int) time);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateWindow();
            activateTimer();
        }
        setFullScreen(visible, frame);
    }

    /**
     * @param volume
     */
    public void setVolume(int volume) {
        volumeSlider.setValue(volume);
    }

    /**
     * Updates the window with the current objects
     */
    private void updateWindow() {
        setText(objects.get(2));
        covers.paint(objects, osManager);
    }    
}