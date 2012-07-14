package com.niceprograms.recipe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.editors.RecipeEditor;
import com.niceprograms.recipe.editors.RecipeEditorInput;
import com.niceprograms.recipe.editors.ViewRecipeEditor;
import com.niceprograms.recipe.ui.IImageKeys;

/**
 * An action to view a recipe. Views the currently selected recipe.
 */
public class ViewRecipeAction extends Action implements ISelectionListener, IWorkbenchAction {

	private final String ID = "com.niceprograms.recipe.actions.readonlyeditrecipe";

	private IWorkbenchWindow window;

	private IStructuredSelection theSelection;

	/**
	 * Creates a new ViewRecipeAction for the given window.
	 * 
	 * @param window the window.
	 */
	public ViewRecipeAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Open");
		setToolTipText("Opens the currently selected recipe.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.OPEN));
		window.getSelectionService().addSelectionListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.theSelection = (IStructuredSelection) selection;
			if (this.theSelection.size() == 1
					&& this.theSelection.getFirstElement() instanceof IRecipeModel) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		} else {
			setEnabled(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		IRecipeModel item = (IRecipeModel) theSelection.getFirstElement();
		if (item != null) {
			closeRelatedEditors(item);
			IWorkbenchPage page = window.getActivePage();
			RecipeEditorInput input = new RecipeEditorInput(RecipeBookRegistry
					.getDefault(), item);
			try {
				page.openEditor(input, ViewRecipeEditor.ID);
			} catch (PartInitException e) {
				RecipeLog.logError(e);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog
								.openError(new Shell(), "Error Viewing Recipe",
										"An error occurred viewing the recipe. See log for error details.");
					}
				});
			}
		}
	}

	private void closeRelatedEditors(IRecipeModel item) {
		IEditorReference[] editors = window.getActivePage()
				.getEditorReferences();
		for (IEditorReference editor : editors) {
			IEditorPart part = editor.getEditor(false);
			if (part instanceof RecipeEditor) {
				RecipeEditor recipeEditor = (RecipeEditor) part;
				if (item.equals(recipeEditor.getRecipe())) {
					window.getActivePage().closeEditor(recipeEditor, true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
}
