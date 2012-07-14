package com.niceprograms.recipe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.editors.RecipeEditor;
import com.niceprograms.recipe.editors.ViewRecipeEditor;
import com.niceprograms.recipe.ui.IImageKeys;

/**
 * An action to delete a recipe.
 */
public class DeleteRecipeAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	private final String ID = "com.niceprograms.recipe.actions.deleterecipe";

	private final IWorkbenchWindow window;

	private IStructuredSelection theSelection;

	/**
	 * Creates a new DeleteRecipeAction for the specified window.
	 * 
	 * @param window the window that the action belongs to.
	 */
	public DeleteRecipeAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Delete");
		setToolTipText("Deletes the currently selected recipe.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.DELETE));
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
		IRecipeBookModel recipeBook = RecipeBookRegistry.getDefault();
		IRecipeModel item = (IRecipeModel) theSelection.getFirstElement();
		if (item != null && recipeBook != null) {
			if (MessageDialog.openQuestion(window.getShell(),
					"Confirm Deletion",
					"Are you sure you want to delete the selected recipe?")) {
				closeRelatedEditors(item);
				recipeBook.deleteRecipe(item);
			}
		}
	}

	private void closeRelatedEditors(IRecipeModel item) {
		IEditorReference[] editors = window.getActivePage()
				.getEditorReferences();
		for (IEditorReference editor : editors) {
			IEditorPart part = editor.getEditor(false);
			if (part instanceof ViewRecipeEditor) {
				ViewRecipeEditor recipeEditor = (ViewRecipeEditor) part;
				if (item.equals(recipeEditor.getRecipe())) {
					window.getActivePage().closeEditor(recipeEditor, true);
				}
			} else if (part instanceof RecipeEditor) {
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
