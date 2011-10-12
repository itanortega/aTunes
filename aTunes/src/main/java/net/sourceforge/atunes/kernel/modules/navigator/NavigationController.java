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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IController;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.Logger;

final class NavigationController implements IAudioFilesRemovedListener, IController {

    private final class ExtendedToolTipActionListener implements ActionListener {
		private final class GetAndSetImageSwingWorker extends
				SwingWorker<ImageIcon, Void> {
			private final Object currentObject;

			private IOSManager osManager;
			
			private GetAndSetImageSwingWorker(Object currentObject, IOSManager osManager) {
				this.currentObject = currentObject;
				this.osManager = osManager;
			}

			@Override
			protected ImageIcon doInBackground() throws Exception {
			    // Get image for albums
			    return ExtendedToolTip.getImage(currentObject, osManager);
			}

			@Override
			protected void done() {
			    try {
			        // Set image in tooltip when done (tooltip can be not visible then)
			        if (currentExtendedToolTipContent != null && currentExtendedToolTipContent.equals(currentObject)) {
			            getExtendedToolTip().setImage(get());
			        }
			    } catch (InterruptedException e) {
			        Logger.error(e);
			    } catch (ExecutionException e) {
			        Logger.error(e);
			    }
			}
		}

		@Override
        public void actionPerformed(ActionEvent arg0) {
            getExtendedToolTip().setVisible(true);

            final Object currentObject = currentExtendedToolTipContent;
            SwingWorker<ImageIcon, Void> getAndSetImage = new GetAndSetImageSwingWorker(currentObject, osManager);
            getAndSetImage.execute();

        }
	}

    private INavigationTreePanel navigationTreePanel;
    private INavigationTablePanel navigationTablePanel;

    /** The current extended tool tip content. */
    private volatile Object currentExtendedToolTipContent;

    /** The album tool tip. */
    private ExtendedToolTip extendedToolTip;

    /** The popupmenu caller. */
    private JComponent popupMenuCaller;

    /** The column model */
    private AbstractCommonColumnModel columnModel;

    private ColumnSetPopupMenu columnSetPopupMenu;

    /** The timer. */
    private Timer timer = new Timer(0, new ExtendedToolTipActionListener());

    /**
     * State of app
     */
    private IState state;
    
    private IColumnSet navigatorColumnSet;
    
    private IOSManager osManager;
    
    private INavigationHandler navigationHandler;
    
    private ITaskService taskService;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IFilterHandler filterHandler;
    
    /**
     * Instantiates a new navigation controller.
     * @param treePanel
     * @param tablePanel
     * @param state
     * @param osManager
     * @param navigationHandler
     * @param taskService
     * @param lookAndFeelManager
     * @param repositoryHandler
     * @param filterHandler 
     */
    NavigationController(INavigationTreePanel treePanel, INavigationTablePanel tablePanel, IState state, IOSManager osManager, INavigationHandler navigationHandler, ITaskService taskService, ILookAndFeelManager lookAndFeelManager, IRepositoryHandler repositoryHandler, IFilterHandler filterHandler) {
        this.navigationTreePanel = treePanel;
        this.navigationTablePanel = tablePanel;
        this.state = state;
        this.osManager = osManager;
        this.navigationHandler = navigationHandler;
        this.taskService = taskService;
        this.lookAndFeelManager = lookAndFeelManager;
        this.filterHandler = filterHandler;
        addBindings();
        repositoryHandler.addAudioFilesRemovedListener(this);
        this.navigatorColumnSet = (IColumnSet) Context.getBean("navigatorColumnSet");
    }

    protected INavigationTreePanel getNavigationTreePanel() {
        return navigationTreePanel;
    }

    protected INavigationTablePanel getNavigationTablePanel() {
        return navigationTablePanel;
    }

    @Override
    public void addBindings() {
        NavigationTableModel model = new NavigationTableModel();
        navigationTablePanel.getNavigationTable().setModel(model);
        columnModel = new NavigationTableColumnModel(navigationTablePanel.getNavigationTable(), state, navigationHandler, taskService, lookAndFeelManager.getCurrentLookAndFeel());
        navigationTablePanel.getNavigationTable().setColumnModel(columnModel);
        ColumnRenderers.addRenderers(navigationTablePanel.getNavigationTable().getSwingComponent(), columnModel, lookAndFeelManager.getCurrentLookAndFeel());

        new ColumnSetRowSorter(navigationTablePanel.getNavigationTable().getSwingComponent(), model, columnModel);

        // Bind column set popup menu
        columnSetPopupMenu = new ColumnSetPopupMenu(navigationTablePanel.getNavigationTable().getSwingComponent(), columnModel);

        // Add tree selection listeners to all views
        for (INavigationView view : navigationHandler.getNavigationViews()) {
            view.getTree().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    updateTableContent((JTree) e.getSource());
                }
            });
        }

        // Add tree mouse listeners to all views
        // Add tree tool tip listener to all views
        NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(this, state, navigationHandler);
        NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(this, state, navigationHandler);
        for (INavigationView view : navigationHandler.getNavigationViews()) {
            view.getTree().addMouseListener(treeMouseListener);
            view.getTree().addMouseListener(tooltipListener);
            view.getTree().addMouseMotionListener(tooltipListener);
            view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
        }

        navigationTablePanel.getNavigationTable().addMouseListener(new NavigationTableMouseListener(this, navigationTablePanel, navigationHandler));
        
        // Add combo listener
        navigationTreePanel.getTreeComboBox().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				navigationHandler.setNavigationView(e.getItem().getClass().getName());
			}
		});
        
        if (lookAndFeelManager.getCurrentLookAndFeel().customComboBoxRenderersSupported()) {
        	navigationTreePanel.getTreeComboBox().setRenderer(lookAndFeelManager.getCurrentLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {

        		@Override
        		public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        			((JLabel)superComponent).setIcon(((INavigationView)value).getIcon().getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForColorMutableIcon(superComponent, isSelected)));
        			((JLabel)superComponent).setText(((INavigationView)value).getTitle());
        			return superComponent;
        		}
        	}));
        }
    }

    @Override
    public void addStateBindings() {
    }

    /**
     * @return the timer
     */
    public Timer getToolTipTimer() {
        return timer;
    }

    /**
     * Gets the album tool tip.
     * 
     * @return the album tool tip
     */
    public ExtendedToolTip getExtendedToolTip() {
        if (extendedToolTip == null) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            extendedToolTip = new ExtendedToolTip(lookAndFeelManager.getCurrentLookAndFeel());
            JDialog.setDefaultLookAndFeelDecorated(lookAndFeelManager.getCurrentLookAndFeel().isDialogUndecorated());
        }
        return extendedToolTip;
    }

    /**
     * Get files of all selected elements in navigator.
     * 
     * @return the files selected in navigator
     */
    public List<IAudioObject> getFilesSelectedInNavigator() {
        List<IAudioObject> files = new ArrayList<IAudioObject>();
        if (getPopupMenuCaller() instanceof JTable) {
            int[] rows = navigationTablePanel.getNavigationTable().getSelectedRows();
            files.addAll(((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).getAudioObjectsAt(rows));
        } else if (getPopupMenuCaller() instanceof JTree) {
            TreePath[] paths = navigationHandler.getCurrentView().getTree().getSelectionPaths();
            for (TreePath path : paths) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (treeNode.getUserObject() instanceof ITreeObject) {
                    files.addAll(getAudioObjectsForTreeNode(navigationHandler.getCurrentView().getClass(),
                            treeNode));
                }
            }
        }
        return files;
    }

    /**
     * Gets the last album tool tip content.
     * 
     * @return the last album tool tip content
     */
    public Object getCurrentExtendedToolTipContent() {
        return currentExtendedToolTipContent;
    }

    /**
     * Gets the audio object in navigation table.
     * 
     * @param row
     *            the row
     * 
     * @return the song in navigation table
     */
    public IAudioObject getAudioObjectInNavigationTable(int row) {
        return ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).getAudioObjectAt(row);
    }

    /**
     * Returns audio objects selected by the given node in the given navigation
     * view
     * 
     * @param navigationViewClass
     * @param node
     * @return
     */
    public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends INavigationView> navigationViewClass, DefaultMutableTreeNode node) {
        List<? extends IAudioObject> audioObjects = navigationHandler.getView(navigationViewClass).getAudioObjectForTreeNode(node, state.getViewMode(),
                filterHandler.isFilterSelected(navigationHandler.getTreeFilter()) ? filterHandler.getFilter() : null);

        IColumnSet columnSet = navigationHandler.getCurrentView().getCustomColumnSet();
        if (columnSet == null) {
            columnSet = navigatorColumnSet;
        }

        IColumn columnSorted = columnSet.getSortedColumn();
        if (columnSorted != null) {
            Collections.sort(audioObjects, columnSorted.getComparator(false));
        }
        return audioObjects;
    }

    @Override
    public void notifyReload() {
    }

    /**
     * Refresh table.
     */
    public void refreshTable() {
        ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).refresh(TableModelEvent.UPDATE);
    }

    /**
     * Sets the current album tool tip content.
     * 
     * @param currentAlbumToolTipContent
     *            the new current album tool tip content
     */
    @SuppressWarnings("unchecked")
	public void setCurrentExtendedToolTipContent(Object currentAlbumToolTipContent) {
        this.currentExtendedToolTipContent = currentAlbumToolTipContent;
        getExtendedToolTip().setSizeToFitImage(currentAlbumToolTipContent instanceof ITreeObject && ((ITreeObject<? extends IAudioObject>) currentAlbumToolTipContent).isExtendedToolTipImageSupported());
    }

    /**
     * Sets the navigation view and optionally saves navigation view
     * 
     * @param view
     *            the new navigation view
     *            
     * @param saveNavigationView
     */
    public void setNavigationView(String view, boolean saveNavigationView) {
        Class<? extends INavigationView> navigationView = navigationHandler.getViewByName(view);
        if (navigationView == null) {
            navigationView = RepositoryNavigationView.class;
        }
        
        if (saveNavigationView) {
        	state.setNavigationView(navigationView.getName());
        }
        
        getNavigationTreePanel().showNavigationView(navigationHandler.getView(navigationView));

        boolean viewModeSupported = navigationHandler.getView(navigationView).isViewModeSupported();
        Actions.getAction(ShowAlbumsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowArtistsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowFoldersInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowGenresInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowYearsInNavigatorAction.class).setEnabled(viewModeSupported);

        // Change column set
        boolean useDefaultNavigatorColumns = navigationHandler.getView(navigationView).isUseDefaultNavigatorColumnSet();
        IColumnSet columnSet = null;
        if (useDefaultNavigatorColumns) {
            columnSet = navigatorColumnSet;
        } else {
            columnSet = navigationHandler.getView(navigationView).getCustomColumnSet();
        }

        ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setColumnSet(columnSet);
        ((NavigationTableColumnModel) navigationTablePanel.getNavigationTable().getColumnModel()).setColumnSet(columnSet);

        navigationHandler.refreshCurrentView();

        // Allow arrange columns if view uses default column set
        columnSetPopupMenu.enableArrangeColumns(useDefaultNavigatorColumns);

        JTree tree = navigationHandler.getCurrentView().getTree();

        if (tree.getSelectionPath() != null) {
            ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setSongs(getAudioObjectsForTreeNode(navigationView, (DefaultMutableTreeNode) (tree
                    .getSelectionPath().getLastPathComponent())));
        }
    }

    /**
     * Sets the navigation view and saves state
     * @param view
     */
    public void setNavigationView(String view) {
    	setNavigationView(view, true);
    }
    
    /**
     * Updates table contents when user selects a tree node or the table filter
     * changes
     * 
     * @param tree
     *            the tree
     */
    protected void updateTableContent(JTree tree) {
        // If navigation table is not shown then don't update it
        if (!state.isShowNavigationTable()) {
            return;
        }

        // Avoid events when changes on a tree different than the one which is visible
        if (tree != navigationHandler.getCurrentView().getTree()) {
            return;
        }

        TreePath[] paths = tree.getSelectionPaths();

        if (paths != null) {
            List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
            for (TreePath element : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
                audioObjects.addAll(getAudioObjectsForTreeNode(navigationHandler.getViewByName(state.getNavigationView()), node));
            }

            // Filter objects
            audioObjects = filterNavigationTable(audioObjects);

            ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setSongs(audioObjects);
        }
    }

    /**
     * Returns a filtered list of audio objects using the current table filter
     * 
     * @param audioObjects
     * @return
     */
    private List<IAudioObject> filterNavigationTable(List<IAudioObject> audioObjects) {
        if (!filterHandler.isFilterSelected(navigationHandler.getTableFilter())) {
            return audioObjects;
        }

        if (navigationHandler.getCurrentView().isUseDefaultNavigatorColumnSet()) {
            // Use column set filtering
            return navigatorColumnSet.filterAudioObjects(audioObjects, filterHandler.getFilter());
        } else {
            // Use custom filter
            return navigationHandler.getCurrentView().getCustomColumnSet().filterAudioObjects(audioObjects, filterHandler.getFilter());
        }
    }

    /**
     * Open search dialog.
     * 
     * @param dialog
     *            the dialog
     * @param setAsDefaultVisible
     *            the set as default visible
     * 
     * @return the search
     */
    public ISearch openSearchDialog(ISearchDialog dialog, boolean setAsDefaultVisible) {
        dialog.setSetAsDefaultVisible(setAsDefaultVisible);
        dialog.showDialog();
        return dialog.getResult();
    }

    protected JComponent getPopupMenuCaller() {
        return popupMenuCaller;
    }

    /**
     * @param popupMenuCaller
     *            the popupMenuCaller to set
     */
    public void setPopupMenuCaller(JComponent popupMenuCaller) {
        this.popupMenuCaller = popupMenuCaller;
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
    	navigationHandler.refreshCurrentView();
    }

	/**
	 * Returns true if last action has been performed in tree
	 * @return
	 */
	public boolean isActionOverTree() {
		return getPopupMenuCaller() instanceof JTree;
	}
}
