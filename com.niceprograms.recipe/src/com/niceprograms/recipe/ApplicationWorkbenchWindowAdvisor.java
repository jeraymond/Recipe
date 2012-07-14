package com.niceprograms.recipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.service.prefs.BackingStoreException;

import com.niceprograms.recipe.preferences.GeneralPreferencesPage;
import com.niceprograms.recipe.ui.LicenseDialog;
import com.niceprograms.recipe.views.RecipesView;

/**
 * Configures the workbench window.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IWorkbenchWindowConfigurer configurer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createWindowContents(Shell shell) {
		super.createWindowContents(shell);
	}

	/**
	 * Creates a new workbench window advisor for configuring a workbench window
	 * via the given workbench window configurer.
	 * 
	 * @param configurer
	 *            an object for configuring the workbench window
	 */
	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		this.configurer = configurer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setShowMenuBar(true);
		configurer.setTitle("Recipe");

		// set the initial size of the window
		Display display = Display.getCurrent();
		Rectangle bounds = display.getBounds();
		if (bounds.width >= 800 && bounds.height >= 600) {
			configurer.setInitialSize(new Point(800, 600));
		} else {
			configurer.setInitialSize(new Point(600, 400));
		}

		configurer.setShowProgressIndicator(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postWindowOpen() {
		IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(Application.PLUGIN_ID);
		boolean licenseAccepted = preferences.getBoolean(
				GeneralPreferencesPage.LICENSE_AGREEMENT_ACCEPTED, false);
		if (!licenseAccepted) {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("RecipeLicense.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder license = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					license.append(line);
					license.append(System.getProperty("line.separator"));
				}
			} catch (IOException e1) {
				RecipeLog.logError("Error retrieving license.", e1);
			}

			LicenseDialog ld = new LicenseDialog(Display.getCurrent()
					.getActiveShell(), "Recipe License Agreement", license.toString());
			licenseAccepted = ld.open() == LicenseDialog.ACCEPT;
			preferences.putBoolean(
					GeneralPreferencesPage.LICENSE_AGREEMENT_ACCEPTED,
					licenseAccepted);
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				RecipeLog.logError(e);
			}
			if (!licenseAccepted) {
				System.exit(0);
			}
		}

		if (licenseAccepted) {
			IViewReference[] views = getWindowConfigurer()
					.getWorkbenchConfigurer().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getViewReferences();

			for (IViewReference view : views) {
				IViewPart part = view.getView(false);
				if (part instanceof RecipesView) {
					Platform.endSplash();
					RecipesView recipeView = (RecipesView) part;
					recipeView.loadRecipes(true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postWindowCreate() {
		super.postWindowOpen();
	}

}
