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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.sourceforge.atunes.gui.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * General characteristics of the preference dialog
 */
public final class EditPreferencesDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = -4759149194433605946L;

    private JButton ok;
    private JButton cancel;
    private JPanel options;
    private JList list;
    private List<AbstractPreferencesPanel> panels;
    
    private ILookAndFeelManager lookAndFeelManager;

    /**
     * Instantiates a new edits the preferences dialog.
     * @param frame
     * @param lookAndFeelManager
     */
    public EditPreferencesDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, GuiUtils.getComponentWidthForResolution(0.5f, 800),GuiUtils.getComponentHeightForResolution(0.6f, 600), true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        this.lookAndFeelManager = lookAndFeelManager;
    }
    
    /**
     * Initializes dialog
     */
    public void initialize() {
        setResizable(true);
        setTitle(I18nUtils.getString("PREFERENCES"));
        add(getContent(lookAndFeelManager.getCurrentLookAndFeel()));
    }

    /**
     * Gets the cancel.
     * 
     * @return the cancel
     */
    public JButton getCancel() {
        return cancel;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent(ILookAndFeel lookAndFeel) {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        list = new JList();
        list.setCellRenderer(lookAndFeel.getListCellRenderer(new PreferencesListCellRendererCode()));
        JScrollPane scrollPane = lookAndFeel.getListScrollPane(list);
        scrollPane.setMinimumSize(new Dimension(130, 0));
        options = new JPanel();
        ok = new JButton(I18nUtils.getString("OK"));
        cancel = new JButton(I18nUtils.getString("CANCEL"));
        JPanel auxPanel = new JPanel();
        auxPanel.add(ok);
        auxPanel.add(cancel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.3;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 0, 5);
        container.add(scrollPane, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.7;
        c.insets = new Insets(5, 5, 0, 5);
        container.add(options, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(10, 0, 10, 10);
        container.add(auxPanel, c);

        return container;
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public JList getList() {
        return list;
    }

    /**
     * Gets the ok.
     * 
     * @return the ok
     */
    public JButton getOk() {
        return ok;
    }

    /**
     * Sets the list model.
     * 
     * @param listModel
     *            the new list model
     */
    public void setListModel(ListModel listModel) {
        list.setModel(listModel);
    }

    /**
     * Sets the panels.
     * 
     * @param panels
     *            the new panels
     */
    public void setPanels(List<AbstractPreferencesPanel> panels) {
        this.panels = panels;
        options.setLayout(new CardLayout());
        for (int i = 0; i < panels.size(); i++) {
            options.add(Integer.toString(i), panels.get(i));
        }
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Show panel.
     * 
     * @param index
     *            the index
     */
    public void showPanel(int index) {
        ((CardLayout) options.getLayout()).show(options, Integer.toString(index));
        // Mark panel as dirty
        this.panels.get(index).setDirty(true);
    }
    
    /**
     * Marks panels as not dirty
     */
    public void resetPanels() {
    	for (AbstractPreferencesPanel panel : this.panels) {
    		panel.setDirty(false);
    	}
    }

    private static class PreferencesListCellRendererCode extends AbstractListCellRendererCode<JLabel, AbstractPreferencesPanel> {
        @Override
        public JComponent getComponent(JLabel label, JList list, AbstractPreferencesPanel p, int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(p.getTitle());
            label.setIcon(p.getIcon());
            return label;
        }
    }    
}