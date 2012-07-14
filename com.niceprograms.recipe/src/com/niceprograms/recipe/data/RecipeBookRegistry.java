package com.niceprograms.recipe.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Registry for recipe books.
 */
public class RecipeBookRegistry {
	private static List<IRecipeBookModel> recipeBooks;

	private static final Lock lock = new ReentrantLock();

	/**
	 * Register a recipe book.
	 * 
	 * @param recipeBook the recipe book.
	 */
	public static void registerRecipeBook(IRecipeBookModel recipeBook) {
		lock.lock();
		try {
			if (recipeBooks == null) {
				recipeBooks = new ArrayList<IRecipeBookModel>();
			}
			recipeBooks.add(recipeBook);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Un-registers a recipe book.
	 * 
	 * @param recipeBook the recipe book to un-register.
	 */
	public static void unregisterRecipeBook(IRecipeBookModel recipeBook) {
		lock.lock();
		try {
			if (recipeBooks != null) {
				recipeBooks.remove(recipeBook);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the default recipe book.
	 * 
	 * @return the default recipe book.
	 */
	public static IRecipeBookModel getDefault() {
		lock.lock();
		try {
			if (recipeBooks != null) {
				return recipeBooks.get(0);
			}
			return null;
		} finally {
			lock.unlock();
		}
	}
}
