package com.niceprograms.recipe.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;

/**
 * Editor input for {@link RecipeEditor} and {@link ViewRecipeEditor}.
 */
public class RecipeEditorInput implements IEditorInput {

	private IRecipeBookModel recipeBook;

	private IRecipeModel recipe;

	/**
	 * Creates a new RecipeEditorInput for a recipe.
	 * 
	 * @param recipeBook the recipe book.
	 * @param recipe the recipe to edit.
	 */
	public RecipeEditorInput(IRecipeBookModel recipeBook, IRecipeModel recipe) {
		super();
		this.recipe = recipe;
		this.recipeBook = recipeBook;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean exists() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		if (recipe != null) {
			return recipe.getTitle();
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getToolTipText() {
		if (recipe != null) {
			return recipe.getTitle();
		}
		return "";
	}

	/**
	 * Gets the recipe.
	 * 
	 * @return the recipe or null if one was not set.
	 */
	public IRecipeModel getRecipe() {
		return recipe;
	}

	/**
	 * Gets the recipe book.
	 * 
	 * @return the recipe book or null if one was not set.
	 */
	public IRecipeBookModel getRecipeBook() {
		return recipeBook;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof RecipeEditorInput)) {
			return false;
		}
		RecipeEditorInput other = (RecipeEditorInput) obj;
		if (recipe == null) {
			return other.getRecipe() == null;
		}
		return recipe.equals(other.getRecipe());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if (recipe != null) {
			return recipe.hashCode();
		}

		return 1;
	}
}
