/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler.LogicalOperator;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.SearchIndexNotAvailableException;
import net.sourceforge.atunes.model.SearchQuerySyntaxException;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class CustomSearchController extends
		AbstractSimpleController<CustomSearchDialog> {

	/** List of searchable objects. */
	private List<ISearchableObject> searchableObjects;

	/** Translated attributes */
	private final Map<String, String> translatedAttributes = new HashMap<String, String>();

	private final ISearchHandler searchHandler;

	private final IStateCore stateCore;

	private final IDialogFactory dialogFactory;

	private final AttributesList attributesList;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 * @param stateCore
	 * @param searchHandler
	 * @param dialogFactory
	 * @param attributesList
	 */
	CustomSearchController(final CustomSearchDialog dialog,
			final IStateCore stateCore, final ISearchHandler searchHandler,
			final IDialogFactory dialogFactory,
			final AttributesList attributesList) {
		super(dialog);
		this.searchHandler = searchHandler;
		this.stateCore = stateCore;
		this.dialogFactory = dialogFactory;
		this.attributesList = attributesList;
		addBindings();
	}

	/**
	 * Shows dialog (previous values are cleared).
	 */
	void showSearchDialog() {
		((DefaultTreeModel) getComponentControlled().getComplexRulesTree()
				.getModel()).setRoot(null);
		getComponentControlled().getSimpleRulesComboBox().setSelectedIndex(0);
		getComponentControlled().getSimpleRulesTextField().setText("");
		getComponentControlled().getAdvancedSearchCheckBox().setSelected(
				this.stateCore.isEnableAdvancedSearch());
		enableAdvancedSearch(this.stateCore.isEnableAdvancedSearch());
		getComponentControlled().getAdvancedSearchTextField().setText("");
		getComponentControlled().setVisible(true);
	}

	/**
	 * Fills combo box with list of objects where it's possible to search.
	 * 
	 * @param sObjects
	 *            the s objects
	 */
	void setListOfSearchableObjects(final List<ISearchableObject> sObjects) {
		if (!sObjects.isEmpty()) {
			// Create list of names of searchable objects
			List<String> names = new ArrayList<String>();
			for (ISearchableObject o : sObjects) {
				names.add(o.getSearchableObjectName());
			}
			getComponentControlled().getSearchAtComboBox().setModel(
					new ListComboBoxModel<String>(names));
			this.searchableObjects = sObjects;

			// Fire select event
			getComponentControlled().getSearchAtComboBox().setSelectedIndex(0);
		}
	}

	/**
	 * Fills combo box with list of operators.
	 * 
	 * @param operators
	 *            the operators
	 */
	void setListOfOperators(final List<String> operators) {
		getComponentControlled().getSimpleRulesComboBox().setModel(
				new DefaultComboBoxModel(operators.toArray()));
	}

	/**
	 * Displays attributes of selected searchable object.
	 */
	void updateAttributesList() {
		if (this.searchableObjects != null) {
			// Get attributes
			List<String> attributes = this.attributesList
					.getSearchableAttributes();
			attributes.add(0, ISearchHandler.DEFAULT_INDEX);

			// Translate attributes to locale
			List<String> translatedAttributesList = new ArrayList<String>();
			for (String attr : attributes) {
				String translatedAttribute = I18nUtils.getString(attr
						.toUpperCase());
				translatedAttributesList.add(translatedAttribute);
				this.translatedAttributes.put(translatedAttribute, attr);
			}

			// Set attributes list
			getComponentControlled().getSimpleRulesList().setListData(
					translatedAttributesList.toArray());
		}
	}

	/**
	 * Creates a simple rule.
	 */
	void createSimpleRule() {
		// Get simple rule data: attribute, operator and value
		String attribute = (String) getComponentControlled()
				.getSimpleRulesList().getSelectedValue();
		String operator = (String) getComponentControlled()
				.getSimpleRulesComboBox().getSelectedItem();
		String value = getComponentControlled().getSimpleRulesTextField()
				.getText();

		// Create object
		SimpleRule simpleRule = new SimpleRule(attribute, operator, value);

		// Add simple rule to tree
		DefaultTreeModel model = ((DefaultTreeModel) getComponentControlled()
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getComponentControlled()
				.getComplexRulesTree().getModel().getRoot();

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(simpleRule);

		// Rule is empty, new simple rule is root
		if (rootNode == null) {
			model.setRoot(node);
		} else {
			// Rule is not empty, get selected node
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getComponentControlled()
					.getComplexRulesTree().getSelectionPath()
					.getLastPathComponent();

			// If selected node is an empty rule replace it
			if (selectedNode.getUserObject() instanceof EmptyRule) {
				selectedNode.setUserObject(simpleRule);
			} else {
				// If it's not an empty rule, then add as child
				selectedNode.add(node);
			}
		}

		// Reload model
		model.reload();

		// Update advanced search text field
		updateAdvancedSearchTextField();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(getComponentControlled().getComplexRulesTree());
	}

	/**
	 * Adds AND operator to rule.
	 */
	void addAndOperator() {
		addLogicalOperator(LogicalOperator.AND);
	}

	/**
	 * Adds OR operator to rule.
	 */
	void addOrOperator() {
		addLogicalOperator(LogicalOperator.OR);
	}

	/**
	 * Adds NOT operator to rule.
	 */
	void addNotOperator() {
		addLogicalOperator(LogicalOperator.NOT);
	}

	/**
	 * Generic method to add a logical operator to rule.
	 * 
	 * @param operator
	 *            the operator
	 */
	private void addLogicalOperator(final LogicalOperator operator) {
		// Get selected node and parent
		DefaultTreeModel model = ((DefaultTreeModel) getComponentControlled()
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getComponentControlled()
				.getComplexRulesTree().getSelectionPath()
				.getLastPathComponent();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode
				.getParent();

		// Create node
		DefaultMutableTreeNode logicalNode = new DefaultMutableTreeNode(
				operator);

		// Node is root
		if (parentNode == null) {
			// add as root
			model.setRoot(logicalNode);
		} else {
			// add as child of parent
			parentNode.add(logicalNode);
			parentNode.remove(selectedNode);

		}
		// Add previous selected node as child of new logical operator node
		logicalNode.add(selectedNode);

		// If operator is AND, OR, then add a second child (an empty rule) as
		// these operators are binary
		if (!operator.equals(LogicalOperator.NOT)) {
			DefaultMutableTreeNode secondOperand = new DefaultMutableTreeNode(
					new EmptyRule());
			logicalNode.add(secondOperand);
		}

		// Reload model
		model.reload();

		// Update advanced search text field
		updateAdvancedSearchTextField();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(getComponentControlled().getComplexRulesTree());
	}

	/**
	 * Updated advanced search text field with rule tree content.
	 */
	private void updateAdvancedSearchTextField() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getComponentControlled()
				.getComplexRulesTree().getModel().getRoot();
		String query = "";
		if (node != null) {
			query = createQuery(node);
		}
		getComponentControlled().getAdvancedSearchTextField().setText(query);
	}

	/**
	 * Recursive method to create a string representation of a complex rule.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return the string
	 */
	private String createQuery(final DefaultMutableTreeNode node) {
		// Node is leaf, return it as string
		if (node.isLeaf()) {
			return node.getUserObject().toString();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("(");
		// If it's a NOT then return (NOT xx)
		if (node.getUserObject().equals(LogicalOperator.NOT)) {
			sb.append(node.getUserObject());
			sb.append(" ");
			sb.append(createQuery((DefaultMutableTreeNode) node.getChildAt(0)));
		} else {
			// If it's not a NOT, then return (xx OP yy OP zz ...)
			for (int i = 0; i < node.getChildCount() - 1; i++) {
				sb.append(createQuery((DefaultMutableTreeNode) node
						.getChildAt(i)));
				sb.append(" ");
				sb.append(node.toString());
				sb.append(" ");
			}
			sb.append(createQuery((DefaultMutableTreeNode) node.getChildAt(node
					.getChildCount() - 1)));
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Removes a node from rule tree.
	 */
	void removeRuleNode() {
		// Get selected node and parent
		DefaultTreeModel model = ((DefaultTreeModel) getComponentControlled()
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getComponentControlled()
				.getComplexRulesTree().getSelectionPath()
				.getLastPathComponent();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode
				.getParent();

		// If it's root, remove it
		if (selectedNode.isRoot()) {
			model.setRoot(null);
			// Allow user to add more simple rules
			getComponentControlled().getSimpleRulesAddButton().setEnabled(true);
		} else {
			// If it's not root, if parent has more than 2 children, then remove
			// it (to keep binary operators with 2 childs at least)
			if (parentNode.getChildCount() > 2) {
				parentNode.remove(selectedNode);
			} else {
				// parent has 2 children, replace and if it's not a simple rule,
				// remove its children
				selectedNode.setUserObject(new EmptyRule());
				if (!(selectedNode.getUserObject() instanceof SimpleRule)) {
					selectedNode.removeAllChildren();
				}
			}
		}

		// Reload model
		model.reload();

		// Update advanced search text field
		updateAdvancedSearchTextField();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(getComponentControlled().getComplexRulesTree());
	}

	/**
	 * Replace attributes from translated string to internal string
	 * 
	 * @param query
	 * @return
	 */
	private String translateQuery(final String query) {
		List<String> translatedAttributesList = new ArrayList<String>(
				this.translatedAttributes.keySet());

		// Sort translated attributes from the largest to the smallest, to apply
		// substitution in correct order
		Collections.sort(translatedAttributesList,
				new TranslatedAttributesList());

		String translatedQuery = query;
		// Replace translated attributes to internal
		for (String translatedAttr : translatedAttributesList) {
			translatedQuery = translatedQuery.replace(translatedAttr,
					this.translatedAttributes.get(translatedAttr));
		}

		return translatedQuery;
	}

	/**
	 * Invokes a search with rule defined in dialog.
	 */
	void search() {
		if (this.searchableObjects != null) {
			// Get searchable object
			ISearchableObject selectedSearchableObject = this.searchableObjects
					.get(getComponentControlled().getSearchAtComboBox()
							.getSelectedIndex());

			// Get query: as advanced search text field is updated with tree
			// rules, we can always get query from this text field
			String query = getComponentControlled()
					.getAdvancedSearchTextField().getText();

			// Translate query to internal attribute names
			query = translateQuery(query);

			try {
				// Invoke query
				List<IAudioObject> result = this.searchHandler.search(
						selectedSearchableObject, query);

				// If no matches found show a message
				if (result.isEmpty()) {
					this.dialogFactory.newDialog(IMessageDialog.class)
							.showMessage(
									I18nUtils.getString("NO_MATCHES_FOUND"));
				} else {
					// Hide search dialog
					getComponentControlled().setVisible(false);
					// Show result
					this.searchHandler.showSearchResults(
							selectedSearchableObject, result);
				}
			} catch (SearchIndexNotAvailableException e) {
				// Thrown when an attribute does not exist on index
				this.dialogFactory.newDialog(IErrorDialog.class)
						.showErrorDialog(
								I18nUtils.getString("INVALID_SEARCH_RULE"));
			} catch (SearchQuerySyntaxException e) {
				// Thrown when query has invalid syntax
				this.dialogFactory.newDialog(IErrorDialog.class)
						.showErrorDialog(
								I18nUtils.getString("INVALID_SEARCH_RULE"));
			}
		} else {
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("NO_MATCHES_FOUND"));
		}
	}

	/**
	 * Enables or disables advanced search.
	 * 
	 * @param enable
	 *            the enable
	 */
	void enableAdvancedSearch(final boolean enable) {
		boolean attributeSelected = getComponentControlled()
				.getSimpleRulesList().getSelectedIndex() != -1;
		getComponentControlled().getSimpleRulesAddButton().setEnabled(
				!enable && attributeSelected);

		getComponentControlled().getSimpleRulesComboBox().setEnabled(!enable);
		getComponentControlled().getSimpleRulesList().setEnabled(!enable);
		getComponentControlled().getSimpleRulesTextField().setEnabled(!enable);

		getComponentControlled().enableComplexRuleButtons(!enable);

		getComponentControlled().getComplexRulesTree().setEnabled(!enable);
		getComponentControlled().getAdvancedSearchTextField()
				.setEnabled(enable);
	}

	@Override
	public void addBindings() {
		getComponentControlled().getSearchAtComboBox().addActionListener(
				new SearchAtComboBoxActionListener());

		getComponentControlled().getSimpleRulesAddButton().addActionListener(
				new SimpleRulesAddButtonActionListener());

		getComponentControlled().getComplexRulesTree().setModel(
				new DefaultTreeModel(null));
		getComponentControlled().getComplexRulesAndButton().addActionListener(
				new ComplexRulesAndButtonActionListener());

		getComponentControlled().getComplexRulesOrButton().addActionListener(
				new ComplexRulesOrButtonActionListener());

		getComponentControlled().getComplexRulesNotButton().addActionListener(
				new ComplexRulesNotButtonActionListener());

		getComponentControlled()
				.getComplexRulesRemoveButton()
				.addActionListener(new ComplexRulesRemoveButtonActionListener());

		getComponentControlled().getAdvancedSearchCheckBox().addActionListener(
				new AdvancedSearchSearchBoxActionListener());

		getComponentControlled().getSearchButton().addActionListener(
				new SearchButtonActionListener());

		getComponentControlled()
				.getComplexRulesTree()
				.addTreeSelectionListener(
						new ComplexTreeSelectionListener(
								getComponentControlled(),
								getComponentControlled().getComplexRulesTree()));
		getComponentControlled().getCancelButton().addActionListener(
				new CancelButtonActionListener());
		getComponentControlled().getAdvancedSearchTextField()
				.addActionListener(new SearchButtonActionListener());

		getComponentControlled().getSimpleRulesList().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(final ListSelectionEvent lse) {
						getComponentControlled().getSimpleRulesAddButton()
								.setEnabled(
										getComponentControlled()
												.getSimpleRulesList()
												.getSelectedIndex() != -1);
					}
				});
	}

	private final class CancelButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed cancel button
			getComponentControlled().setVisible(false);
		}
	}

	private final class SearchButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed Search button
			search();
		}
	}

	private final class AdvancedSearchSearchBoxActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed Advanced Search Check Box
			enableAdvancedSearch(getComponentControlled()
					.getAdvancedSearchCheckBox().isSelected());
			CustomSearchController.this.stateCore
					.setEnableAdvancedSearch(getComponentControlled()
							.getAdvancedSearchCheckBox().isSelected());
		}
	}

	private final class ComplexRulesRemoveButtonActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed Remove button
			removeRuleNode();
		}
	}

	private final class ComplexRulesNotButtonActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed NOT button
			addNotOperator();
		}
	}

	private final class ComplexRulesOrButtonActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed OR button
			addOrOperator();
		}
	}

	private final class ComplexRulesAndButtonActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed AND button
			addAndOperator();
		}
	}

	private final class SimpleRulesAddButtonActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			// Pressed Add button
			createSimpleRule();
		}
	}

	private final class SearchAtComboBoxActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent arg0) {
			// When changing combo box selected value change attributes list
			updateAttributesList();
		}
	}

}
