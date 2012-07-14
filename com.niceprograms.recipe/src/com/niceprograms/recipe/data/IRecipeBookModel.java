package com.niceprograms.recipe.data;

import java.util.List;

import com.niceprograms.recipe.persistence.IRecipeBookPersister;

/**
 * The model interface for recipe books.
 */
public interface IRecipeBookModel {

	/**
	 * Adds a recipe to the recipe book.
	 * 
	 * @param recipe the recipe.
	 */
	void addRecipe(IRecipeModel recipe);

	/**
	 * Adds recipes to the recipe book.
	 * 
	 * @param recipes the recipes.
	 */
	void addRecipes(List<IRecipeModel> recipes);

	/**
	 * Deletes a recipe.
	 * 
	 * @param recipe the recipe to delete.
	 */
	void deleteRecipe(IRecipeModel recipe);

	/**
	 * Get a <code>List</code> for the list of recipes.
	 * 
	 * @return a <code>List</code> for the list of recipes.
	 */
	List<IRecipeModel> getRecipes();

	/**
	 * Sets the recipes.
	 * 
	 * @param recipes the recipes.
	 */
	void setRecipes(List<IRecipeModel> recipes);

	/**
	 * Get a <code>List</code> of the categories.
	 * 
	 * @return a <code>List</code> of the categories.
	 */
	List<String> getCategories();

	/**
	 * Get a <code>List</code> of cuisines.
	 * 
	 * @return a <code>List</code> of cuisines.
	 */
	List<String> getCuisines();

	/**
	 * Adds a listener.
	 * 
	 * @param listener the listener.
	 */
	void addListener(IListener listener);

	/**
	 * Removes a listener.
	 * 
	 * @param listener the listener.
	 */
	void removeListener(IListener listener);

	/**
	 * Removes all recipes from the recipe book.
	 */
	void clear();

	/**
	 * Creates a new recipe.
	 * 
	 * @param title the recipe title.
	 * @param cuisine the recipe cuisine.
	 * @param categories the recipe categories.
	 * @param ingredients the recipe ingredients.
	 * @param directions the recipe directions.
	 * @param imagePath the image path.
	 * @return the recipe.
	 */
	IRecipeModel createRecipe(String title, String cuisine,
			String[] categories, String ingredients, String directions,
			String imagePath);

	/**
	 * Sets the persister.
	 * 
	 * @param persister the persister.
	 */
	void setPersister(IRecipeBookPersister persister);

	/**
	 * Saves the recipes using the current persister.
	 * 
	 * @see #setPersister(IRecipeBookPersister)
	 */
	void save();

	/**
	 * Loads the recipes using the current persister.
	 * 
	 * @see #setPersister(IRecipeBookPersister)
	 */
	void load();

	/**
	 * Interface for IRecipeBookModel changes.
	 */
	interface IListener {

		/** The type of change in the recipe book. */
		enum ChangeType {
			/** Recipe added. */
			ADD,

			/** Recipes added in bulk. */
			BULK_ADD,

			/** Recipe removed. */
			REMOVE,

			/** Recipe updated. */
			UPDATE,

			/** All recipes cleared. */
			CLEAR,

			/** The recipes have been loaded. */
			LOAD,

			/** The recipe have been saved. */
			SAVE;
		}

		/**
		 * Invoked when the item is changed.
		 * 
		 * @param type the type of change.
		 * @param data the reference to the changed item.
		 */
		void itemChanged(ChangeType type, IRecipeModel data);
	}
}
