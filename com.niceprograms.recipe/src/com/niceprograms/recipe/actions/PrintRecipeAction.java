package com.niceprograms.recipe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.printing.RecipePrinter;
import com.niceprograms.recipe.ui.IImageKeys;
import com.niceprograms.recipe.views.RecipesView;

/**
 * An action for printing the selected recipe.
 */
public class PrintRecipeAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	private final String ID = "com.niceprograms.recipe.actions.printrecipe";

	private final IWorkbenchWindow window;

	private IStructuredSelection theSelection;

	/**
	 * Creates a new PrintRecipeAction for the specified window.
	 * 
	 * @param window the window that the action belongs to.
	 */
	public PrintRecipeAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Print...");
		setToolTipText("Prints the currently selected recipe.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.PRINT));
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
			IRecipeModel item = (IRecipeModel) theSelection.getFirstElement();
			if (item != null) {
				RecipePrinter.printRecipe(item, window.getShell());
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
