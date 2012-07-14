package com.niceprograms.recipe;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The activator class for the Recipe plugin.
 */
public class RecipeActivator extends AbstractUIPlugin {

	private static RecipeActivator plugin;

	/** This plug-in's ID. */
	public static final String ID = "com.niceprograms.recipe";

	/**
	 * The constructor.
	 */
	public RecipeActivator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static RecipeActivator getDefault() {
		return plugin;
	}

}
