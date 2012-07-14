package com.niceprograms.recipe.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.niceprograms.recipe.views.RecipesView;

/**
 * Generates the initial pay layout.
 */
public class Perspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.addStandaloneView(RecipesView.ID, false, IPageLayout.LEFT,
				0.33f, layout.getEditorArea());
	}
}
