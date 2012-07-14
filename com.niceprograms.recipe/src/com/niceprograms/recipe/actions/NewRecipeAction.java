package com.niceprograms.recipe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.editors.RecipeEditor;
import com.niceprograms.recipe.editors.RecipeEditorInput;
import com.niceprograms.recipe.ui.IImageKeys;
import com.niceprograms.recipe.views.RecipesView;

/**
 * An action to create a new recipe.
 */
public class NewRecipeAction extends Action implements IWorkbenchAction {

	private final String ID = "com.niceprograms.recipe.actions.newrecipe";

	private final IWorkbenchWindow window;

	/**
	 * Creates a NewRecipeAction for the given window.
	 * 
	 * @param window the window this action belongs to.
	 */
	public NewRecipeAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&New");
		setToolTipText("Creates a new recipe.");
		setActionDefinitionId("com.niceprograms.recipe.commands.newrecipe");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.NEW));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		IWorkbenchPage page = window.getActivePage();
		IViewReference[] views = page.getViewReferences();

		RecipesView theView = null;
		for (IViewReference view : views) {
			IViewPart part = view.getView(false);
			if (part instanceof RecipesView) {
				theView = (RecipesView) part;
			}
		}

		if (theView != null) {

			RecipeEditorInput input = new RecipeEditorInput(RecipeBookRegistry
					.getDefault(), null);
			try {
				page.openEditor(input, RecipeEditor.ID);
			} catch (PartInitException e) {
				RecipeLog.logError(e);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog
								.openError(new Shell(),
										"Error Creating New Recipe",
										"An error occurred creating a new recipe. See log for error details.");
					}
				});
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		// unused
	}
}
