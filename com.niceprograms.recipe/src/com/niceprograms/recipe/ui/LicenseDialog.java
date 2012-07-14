package com.niceprograms.recipe.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LicenseDialog extends Dialog {

	public static final int ACCEPT = 1337;
	public static final int DECLINE = 1338;

	private Text licenseText;
	private String license;
	private String title;

	public LicenseDialog(Shell parentShell, String title, String license) {
		super(parentShell);
		this.license = license;
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Composite content = new Composite(composite, SWT.NONE);

		GridLayout layout = new GridLayout();

		content.setLayout(layout);

		licenseText = new Text(content, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.READ_ONLY | SWT.V_SCROLL);
		licenseText.setText(license != null ? license : "INVALID LICENSE");
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.minimumHeight = 400;
		layoutData.minimumWidth = 400;
		layoutData.widthHint = 400;
		layoutData.heightHint = 400;
		licenseText.setLayoutData(layoutData);
		licenseText.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ACCEPT, "Accept", true);
		createButton(parent, DECLINE, "Decline", false);
		getButton(ACCEPT).forceFocus();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (title != null) {
			newShell.setText(title);
		}
	}
}
