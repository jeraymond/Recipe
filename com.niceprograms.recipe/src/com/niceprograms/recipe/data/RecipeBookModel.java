package com.niceprograms.recipe.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.niceprograms.recipe.data.IRecipeBookModel.IListener.ChangeType;
import com.niceprograms.recipe.persistence.IRecipeBookPersister;

/**
 * The recipe book model.
 */
public class RecipeBookModel implements IRecipeBookModel {
	private ArrayList<IRecipeModel> theRecipes;

	private TreeSet<String> categories;

	private TreeSet<String> cuisines;

	private ArrayList<IListener> listeners;

	private IRecipeBookPersister persister;

	private final Lock lock = new ReentrantLock();

	/**
	 * Creates a new instance of RecipeBook.
	 */
	public RecipeBookModel() {
		theRecipes = new ArrayList<IRecipeModel>();
		categories = new TreeSet<String>();
		cuisines = new TreeSet<String>();
		listeners = new ArrayList<IListener>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRecipe(IRecipeModel recipe) {
		lock.lock();
		try {
			theRecipes.add(recipe);
			notifyListeneners(IListener.ChangeType.ADD, recipe);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteRecipe(IRecipeModel recipe) {
		lock.lock();
		try {
			if (theRecipes.remove(recipe)) {
				notifyListeneners(IListener.ChangeType.REMOVE, recipe);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<IRecipeModel> getRecipes() {
		lock.lock();
		try {
			List<IRecipeModel> returnRecipes = (List<IRecipeModel>) theRecipes
					.clone();
			return returnRecipes;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		lock.lock();
		try {
			categories.clear();
			for (IRecipeModel recipe : this.theRecipes) {
				String[] cats = recipe.getCategories();
				for (String category : cats) {
					categories.add(category);
				}
			}
			return new ArrayList<String>((Set<String>) categories.clone());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCuisines() {
		lock.lock();
		try {
			cuisines.clear();
			for (IRecipeModel recipe : this.theRecipes) {
				cuisines.add(recipe.getCuisine());
			}
			return new ArrayList<String>((Set<String>) cuisines.clone());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListener(IListener listener) {
		lock.lock();
		try {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(IListener listener) {
		lock.lock();
		try {
			listeners.remove(listener);
		} finally {
			lock.unlock();
		}
	}

	private void notifyListeneners(IListener.ChangeType type, IRecipeModel data) {
		for (IListener listener : listeners) {
			listener.itemChanged(type, data);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRecipeModel createRecipe(String title, String cuisine,
			@SuppressWarnings("hiding")
			String[] categories, String ingredients, String directions, String imagePath) {
		lock.lock();
		try {
			IRecipeModel recipe = createRecipeSilent(title, cuisine,
					categories, ingredients, directions, imagePath);
			notifyListeneners(ChangeType.ADD, recipe);
			return recipe;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Creates a recipe without notifying listeners.
	 * 
	 * @param title the title.
	 * @param cuisine the cuisine.
	 * @param categories the categories.
	 * @param ingredients the ingredients.
	 * @param directions the directions.
	 * @param imagePath the image path.
	 * @return the newly created recipe.
	 */
	IRecipeModel createRecipeSilent(String title, String cuisine,
			@SuppressWarnings("hiding")
			String[] categories, String ingredients, String directions, String imagePath) {
		lock.lock();
		try {
			IRecipeModel recipe = new RecipeModel(title, cuisine, categories,
					ingredients, directions, imagePath);
			theRecipes.add(recipe);
			return recipe;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		lock.lock();
		try {
			theRecipes.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRecipes(List<IRecipeModel> recipesList) {
		lock.lock();
		try {
			theRecipes.clear();
			for (IRecipeModel recipe : recipesList) {
				theRecipes.add(recipe);
			}

			notifyListeneners(IListener.ChangeType.BULK_ADD, null);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRecipes(List<IRecipeModel> recipes) {
		lock.lock();
		try {
			for (IRecipeModel recipe : recipes) {
				theRecipes.add(recipe);
			}
			notifyListeneners(IListener.ChangeType.BULK_ADD, null);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void save() {
		lock.lock();
		try {
			if (persister != null) {
				persister.saveRecipes(this);
				notifyListeneners(ChangeType.SAVE, null);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPersister(IRecipeBookPersister persister) {
		lock.lock();
		try {
			this.persister = persister;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void load() {
		lock.lock();
		try {
			if (persister != null) {
				persister.loadRecipes(this);
				notifyListeneners(ChangeType.LOAD, null);
			}
		} finally {
			lock.unlock();
		}
	}
}
