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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The properties dialog for podcast feed entries.
 */
final class PodcastFeedEntryPropertiesDialog extends AudioObjectPropertiesDialog {

    private static final long serialVersionUID = -2472573171771586037L;

    private JLabel pictureLabel;
    private JLabel titleLabel;
    private JLabel artistLabel;
    private JLabel urlLabel;
    private JLabel durationLabel;
    private JLabel dateLabel;
    private JLabel podcastFeedLabel;
    private JLabel downloadedLabel;
    private JLabel descriptionLabel;
    private JScrollPane descriptionScrollPane;
    private JTextArea descriptionTextArea;
    private IPodcastFeedEntry entry;
    
    private IState state;

    /**
     * Instantiates a new podcast feed entry properties dialog.
     * 
     * @param entry
     * @param frame
     * @param state
     * @param lookAndFeelManager
     */
    PodcastFeedEntryPropertiesDialog(IPodcastFeedEntry entry, IFrame frame, IState state, ILookAndFeelManager lookAndFeelManager) {
        super(getTitleText(entry), frame, lookAndFeelManager);
        this.entry = entry;
        this.state = state;
        setAudioObject(entry);
        addContent(lookAndFeelManager.getCurrentLookAndFeel());

        setContent();

        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gives a title for dialog.
     * 
     * @param entry
     *            the entry
     * 
     * @return title for dialog
     */
    private static String getTitleText(IPodcastFeedEntry entry) {
        return StringUtils.getString(I18nUtils.getString("INFO_OF_PODCAST_FEED"), " ", entry.getTitle());
    }

    /**
     * Adds the content.
     * @param iLookAndFeel 
     */
    private void addContent(ILookAndFeel iLookAndFeel) {
        JPanel panel = new JPanel(new GridBagLayout());
        
        pictureLabel = new JLabel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.VERTICAL;
        panel.add(pictureLabel, c);

        titleLabel = new JLabel();
        titleLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, c);

        artistLabel = new JLabel();
        artistLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
        c.gridx = 1;
        c.gridy = 1;
        panel.add(artistLabel, c);

        urlLabel = new JLabel();
        urlLabel.setFont(iLookAndFeel.getPropertiesDialogBigFont());
        c.gridx = 1;
        c.gridy = 2;
        panel.add(urlLabel, c);

        durationLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 3;
        panel.add(durationLabel, c);

        dateLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 4;
        panel.add(dateLabel, c);

        podcastFeedLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 5;
        panel.add(podcastFeedLabel, c);

        downloadedLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 6;
        panel.add(downloadedLabel, c);

        descriptionLabel = new JLabel();
        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 10, 0, 10);
        panel.add(descriptionLabel, c);

        descriptionScrollPane = iLookAndFeel.getScrollPane(null);
        descriptionScrollPane.setMinimumSize(new Dimension(400, 100));
        c.gridx = 1;
        c.gridy = 8;
        c.insets = new Insets(0, 10, 5, 10);
        panel.add(descriptionScrollPane, c);

        descriptionTextArea = new CustomTextArea();
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setOpaque(false);
        descriptionTextArea.setBorder(BorderFactory.createEmptyBorder());
        descriptionScrollPane.setViewportView(descriptionTextArea);

        add(panel);

    }

    /**
     * Fill picture.
     */
    private void fillPicture() {
        ImageIcon picture = RssImageIcon.getIcon(lookAndFeel);
        pictureLabel.setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
        pictureLabel.setIcon(picture);
        pictureLabel.setVisible(true);
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        fillPicture();
        titleLabel.setText(getHtmlFormatted(I18nUtils.getString("NAME"), StringUtils.isEmpty(entry.getTitle()) ? "-" : entry.getTitle()));
        artistLabel.setText(getHtmlFormatted(I18nUtils.getString("ARTIST"), StringUtils.isEmpty(entry.getArtist()) ? "-" : entry.getArtist()));
        urlLabel.setText(getHtmlFormatted(I18nUtils.getString("URL"), entry.getUrl()));
        if (entry.getDuration() > 0) {
            durationLabel.setText(getHtmlFormatted(I18nUtils.getString("DURATION"), StringUtils.seconds2String(entry.getDuration())));
        } else {
            durationLabel.setText(getHtmlFormatted(I18nUtils.getString("DURATION"), "-"));
        }
        if (entry.getDate() != null) {
            dateLabel.setText(getHtmlFormatted(I18nUtils.getString("DATE"), StringUtils.getString(DateFormat.getDateInstance(DateFormat.LONG,
                    state.getLocale().getLocale()).format(entry.getDate()), ", ", DateFormat.getTimeInstance().format(entry.getDate()))));
        } else {
            dateLabel.setText(getHtmlFormatted(I18nUtils.getString("DATE"), "-"));
        }
        podcastFeedLabel.setText(getHtmlFormatted(I18nUtils.getString("PODCAST_FEED"), entry.getPodcastFeed().getName()));
        downloadedLabel.setText(getHtmlFormatted(I18nUtils.getString("DOWNLOADED"), entry.isDownloaded() ? I18nUtils.getString("YES") : I18nUtils.getString("NO")));
        descriptionLabel.setText(getHtmlFormatted(I18nUtils.getString("DESCRIPTION"), ""));
        descriptionTextArea.setText(entry.getDescription());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                descriptionTextArea.setCaretPosition(0);
            }
        });

    }

}
