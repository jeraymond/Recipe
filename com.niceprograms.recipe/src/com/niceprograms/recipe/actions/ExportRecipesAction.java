package com.niceprograms.recipe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.persistence.TextFileExporter;
import com.niceprograms.recipe.ui.IImageKeys;

/**
 * Action to export recipes.
 */
public class ExportRecipesAction extends Action implements IWorkbenchAction {

	private final String ID = "com.niceprograms.recipe.actions.exportrecipes";

	private final IWorkbenchWindow window;

	/**
	 * Creates a ExportRecipesAction for the given window.
	 * 
	 * @param window the window this action belongs to.
	 */
	public ExportRecipesAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Export Recipes");
		setToolTipText("Exports the recipes.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.EXPORT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		IRecipeBookModel recipeBook = RecipeBookRegistry.getDefault();

		if (recipeBook != null) {

			FileDialog fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
			fileDialog.setText("Choose a file to export the recipes.");
			if (System.getProperty("os.name").contains("Windows")) {
				fileDialog.setFilterPath(System.getProperty("user.home")
						+ "\\Desktop");
			} else {
				fileDialog.setFilterPath(System.getProperty("user.home"));
			}
			fileDialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
			fileDialog.setFileName("recipes.txt");

			String filePath = fileDialog.open();
			if (filePath != null) {
				TextFileExporter exporter = new TextFileExporter(true, filePath);
				exporter.saveRecipes(recipeBook);

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		// nothing to see here
	}
}
