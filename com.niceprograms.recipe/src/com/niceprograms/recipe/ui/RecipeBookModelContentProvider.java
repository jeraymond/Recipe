package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;

/**
 * The recipe book model content provider.
 */
public class RecipeBookModelContentProvider implements
		IStructuredContentProvider, IRecipeBookModel.IListener {

	private IRecipeBookModel recipeBook;

	private ListViewer theViewer;

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement == recipeBook) {
			return recipeBook.getRecipes().toArray();
		}
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		recipeBook.removeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof ListViewer) {
			this.theViewer = (ListViewer) viewer;
		}

		if (oldInput instanceof IRecipeBookModel) {
			((IRecipeBookModel) oldInput).removeListener(this);
			recipeBook.removeListener(this);
		}

		if (newInput instanceof IRecipeBookModel) {
			recipeBook = (IRecipeBookModel) newInput;
			recipeBook.addListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void itemChanged(IRecipeBookModel.IListener.ChangeType type,
			IRecipeModel data) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				theViewer.refresh();
			}
		});
	}
}
