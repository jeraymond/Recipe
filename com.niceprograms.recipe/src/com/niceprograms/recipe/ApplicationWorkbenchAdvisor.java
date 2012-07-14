package com.niceprograms.recipe;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Configures the workbench.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String PERSPECTIVE_ID = "com.niceprograms.recipe.perspectives.perspective";

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        configurer.setSaveAndRestore(true);
    }

    @Override
    public boolean preShutdown() {
        try {
            // delete temporary recipe files
            File file = new File(".");
            String[] files = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("tempRecipe");
                }
            });
            
            for (String fileToDelete : files) {
                File deleteMe = new File(fileToDelete);
                deleteMe.delete();
            }
        } catch (Exception e) {
            RecipeLog.logError("Error deleting temporary files.", e);
        }

        return super.preShutdown();
    }
}
