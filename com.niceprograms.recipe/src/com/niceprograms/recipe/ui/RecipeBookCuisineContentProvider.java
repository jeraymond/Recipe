package com.niceprograms.recipe.ui;

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;

/**
 * Content provider for cuisines.
 */
public class RecipeBookCuisineContentProvider implements
		IStructuredContentProvider, IRecipeBookModel.IListener {

	private IRecipeBookModel recipeBook;

	private ComboViewer theViewer;

	private boolean injectAllCuisine;

	/** Key for the all cuisine. */
	static String ALL_CUISINE_KEY = "!!!!####ALL####!!!!";

	/**
	 * Creates a new RecipeBookCuisineContentProvider.
	 * 
	 * @param injectAllCuisine true to inject the all cuisine.
	 */
	public RecipeBookCuisineContentProvider(boolean injectAllCuisine) {
		this.injectAllCuisine = injectAllCuisine;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement == recipeBook) {
			String[] cuisines;

			List<String> existingCuisines = recipeBook.getCuisines();
			if (injectAllCuisine) {
				cuisines = new String[existingCuisines.size() + 1];
			} else {
				cuisines = new String[existingCuisines.size()];
			}

			if (injectAllCuisine) {
				cuisines[0] = ALL_CUISINE_KEY;
				System.arraycopy(existingCuisines.toArray(), 0, cuisines, 1,
						existingCuisines.size());
			} else {
				System.arraycopy(existingCuisines.toArray(), 0, cuisines, 0,
						existingCuisines.size());
			}

			return cuisines;
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
		if (viewer instanceof ComboViewer) {
			this.theViewer = (ComboViewer) viewer;
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
	public void itemChanged(ChangeType type, IRecipeModel data) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				theViewer.refresh();
				theViewer.setSelection(new StructuredSelection(theViewer
						.getElementAt(0)));
			}
		});
	}
}
