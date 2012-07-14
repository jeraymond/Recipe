package com.niceprograms.recipe.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.persistence.SimpleXMLRecipeBookPersister;

/**
 * The preferences page.
 */
public class GeneralPreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Recipe file path preference. */
	public static final String RECIPE_FILE_PATH = "prefs_recipe_file_path";
	
	/** License agreement accepted preference. */
	public static final String LICENSE_AGREEMENT_ACCEPTED = "prefs_license_agreement_accepted";

	private ScopedPreferenceStore preferences;

	private FileFieldEditor fileEditor;

	/**
	 * Creates a new GeneralPreferencesPage.
	 */
	public GeneralPreferencesPage() {
		super(GRID);
		preferences = new ScopedPreferenceStore(new ConfigurationScope(),
				Application.PLUGIN_ID);
		setPreferenceStore(preferences);
	}

	@Override
	protected void createFieldEditors() {
		fileEditor = new FileFieldEditor(RECIPE_FILE_PATH, "Recipes file",
				getFieldEditorParent());
		fileEditor.setEmptyStringAllowed(false);
		addField(fileEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		if (SimpleXMLRecipeBookPersister.recipeFileIsValid(fileEditor
				.getStringValue())) {
			try {
				preferences.save();
				return super.performOk();
			} catch (IOException e) {
				RecipeLog.logError(e);
			}
		}
		MessageDialog.openError(getShell(), "Error Loading Recipes",
				"The recipe file " + fileEditor.getStringValue()
						+ " is not valid. Please choose a different file.");

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(@SuppressWarnings("unused")
	IWorkbench workbench) {
		// unused
	}
}
