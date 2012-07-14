package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.niceprograms.recipe.data.IRecipeModel;

/**
 * Sorter for recipe book models.
 */
public class RecipeBookModelViewerSorter extends ViewerSorter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IRecipeModel && e2 instanceof IRecipeModel) {
			return super.compare(viewer, ((IRecipeModel) e1).getTitle()
					.toUpperCase(), ((IRecipeModel) e2).getTitle()
					.toUpperCase());
		}

		return super.compare(viewer, e1, e2);
	}
}
