package com.niceprograms.recipe.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * The contributor for the {@link ViewRecipeEditor} class.
 */
public class ViewRecipeEditorContributor extends EditorActionBarContributor {

	/**
	 * Creates a new ViewRecipeEditorContributor.
	 */
	public ViewRecipeEditorContributor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof ViewRecipeEditor) {
			ViewRecipeEditor editor = (ViewRecipeEditor) targetEditor;
			IActionBars bars = getActionBars();
			if (bars == null) {
				return;
			}
			bars.setGlobalActionHandler(
					org.eclipse.ui.actions.ActionFactory.COPY.getId(), editor
							.getCopyAction());
			bars.updateActionBars();
		}
	}
}
