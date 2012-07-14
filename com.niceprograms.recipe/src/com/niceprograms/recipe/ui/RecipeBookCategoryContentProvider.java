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
 * A content provider for categories.
 */
public class RecipeBookCategoryContentProvider implements
		IStructuredContentProvider, IRecipeBookModel.IListener {

	private IRecipeBookModel recipeBook;

	private ComboViewer theViewer;

	private boolean injectAllCategory;

	/** The key for the all category. */
	static String ALL_CATEGORY_KEY = "!!!!####ALL####!!!!";

	/**
	 * Creates a new RecipeBookCategoryContentProvider.
	 * 
	 * @param injectAllCategory
	 *            true to inject the all category into the category list.
	 */
	public RecipeBookCategoryContentProvider(boolean injectAllCategory) {
		this.injectAllCategory = injectAllCategory;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement == recipeBook) {
			String[] categories;

			List<String> existingCategories = recipeBook.getCategories();
			if (injectAllCategory) {
				categories = new String[existingCategories.size() + 1];
			} else {
				categories = new String[existingCategories.size()];
			}

			if (injectAllCategory) {
				categories[0] = ALL_CATEGORY_KEY;
				System.arraycopy(existingCategories.toArray(), 0, categories,
						1, existingCategories.size());
			} else {
				System.arraycopy(existingCategories.toArray(), 0, categories,
						0, existingCategories.size());
			}

			return categories;
		}
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		if (recipeBook != null) {
			recipeBook.removeListener(this);
		}
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
