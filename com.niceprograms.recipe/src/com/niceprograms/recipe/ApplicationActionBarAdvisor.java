package com.niceprograms.recipe;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.niceprograms.recipe.actions.DeleteRecipeAction;
import com.niceprograms.recipe.actions.EditRecipeAction;
import com.niceprograms.recipe.actions.NewRecipeAction;
import com.niceprograms.recipe.actions.PrintRecipeAction;
import com.niceprograms.recipe.actions.RecipePrintPreviewAction;
import com.niceprograms.recipe.actions.ViewRecipeAction;

/**
 * Configures the action bars of the workspace window.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;

	private IWorkbenchAction aboutAction;

	private IWorkbenchAction editRecipeAction;

	private IWorkbenchAction viewRecipeAction;

	private IWorkbenchAction newRecipeAction;

	private IWorkbenchAction deleteRecipeAction;

	private IWorkbenchAction saveAction;

	private IWorkbenchAction closeAction;

	private IWorkbenchAction closeAllAction;

	private IWorkbenchAction printRecipeAction;

	private IWorkbenchAction recipePrintPreviewAction;

	private IWorkbenchAction preferencesAction;

	// private IWorkbenchAction exportRecipesAction;

	private IWorkbenchAction helpAction;

	private IWorkbenchAction copyAction;

	private IWorkbenchAction importAction;

	private IWorkbenchAction exportAction;

	/**
	 * Creates a new ApplicationActionBar advisor to configure a workbench
	 * window's action bars via the given action bar configurer.
	 * 
	 * @param configurer the action bar configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void makeActions(IWorkbenchWindow window) {
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

		editRecipeAction = new EditRecipeAction(window);
		register(editRecipeAction);

		viewRecipeAction = new ViewRecipeAction(window);
		register(viewRecipeAction);

		newRecipeAction = new NewRecipeAction(window);
		register(newRecipeAction);

		closeAction = ActionFactory.CLOSE.create(window);
		register(closeAction);

		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		register(closeAllAction);

		saveAction = ActionFactory.SAVE.create(window);
		saveAction.setToolTipText("Saves the recipe currently being edited.");
		register(saveAction);

		deleteRecipeAction = new DeleteRecipeAction(window);
		register(deleteRecipeAction);

		printRecipeAction = new PrintRecipeAction(window);
		register(printRecipeAction);

		recipePrintPreviewAction = new RecipePrintPreviewAction(window);
		register(recipePrintPreviewAction);

		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);

		// exportRecipesAction = new ExportRecipesAction(window);
		// register(exportRecipesAction);

		helpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(helpAction);

		copyAction = ActionFactory.COPY.create(window);
		register(copyAction);

		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);

		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File", "file");
		fileMenu.add(newRecipeAction);
		fileMenu.add(viewRecipeAction);
		fileMenu.add(new Separator());
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(new Separator());
		fileMenu.add(printRecipeAction);
		fileMenu.add(recipePrintPreviewAction);
		fileMenu.add(new Separator());
		fileMenu.add(importAction);
		// fileMenu.add(exportRecipesAction);
		fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager editMenu = new MenuManager("&Edit", "edit");
		editMenu.add(editRecipeAction);
		editMenu.add(new Separator());
		editMenu.add(deleteRecipeAction);

		MenuManager windowMenu = new MenuManager("&Window", "window");
		windowMenu.add(preferencesAction);

		MenuManager helpMenu = new MenuManager("&Help", "help");
		helpMenu.add(helpAction);
		helpMenu.add(new Separator());
		helpMenu.add(aboutAction);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolBar = new ToolBarManager(coolBar.getStyle()
				| SWT.BOTTOM);
		coolBar.add(toolBar);
		ActionContributionItem newRecipeCI = new ActionContributionItem(
				newRecipeAction);
		newRecipeCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBar.add(newRecipeCI);

		ActionContributionItem viewRecipeCI = new ActionContributionItem(
				viewRecipeAction);
		viewRecipeCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBar.add(viewRecipeCI);

		ActionContributionItem saveActionCI = new ActionContributionItem(
				saveAction);
		saveActionCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBar.add(saveActionCI);

		toolBar.add(new Separator());

		ActionContributionItem editRecipeCI = new ActionContributionItem(
				editRecipeAction);
		editRecipeCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBar.add(editRecipeCI);

		ActionContributionItem deleteRecipeCI = new ActionContributionItem(
				deleteRecipeAction);
		deleteRecipeCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBar.add(deleteRecipeCI);
	}
}
