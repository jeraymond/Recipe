package com.niceprograms.recipe.intro;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IntroPart;

/**
 * The recipe introduction.
 */
public class RecipeIntro extends IntroPart {

	private Label label;

	/**
	 * Creates a new instance of RecipeIntro.
	 */
	public RecipeIntro() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite outerContainer = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		outerContainer.setLayout(gridLayout);
		outerContainer.setBackground(outerContainer.getDisplay()
				.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label = new Label(outerContainer, SWT.CENTER);
		label.setText("WELCOME TO ECLIPSE");
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		gd.horizontalAlignment = GridData.CENTER;
		gd.verticalAlignment = GridData.CENTER;
		label.setLayoutData(gd);
		label.setBackground(outerContainer.getDisplay().getSystemColor(
				SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// unused
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void standbyStateChanged(boolean standby) {
		// unused
	}
}
