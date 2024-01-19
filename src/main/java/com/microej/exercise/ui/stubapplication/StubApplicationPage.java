/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.stubapplication;

import com.microej.exercise.ui.stubapplication.widget.ApplicationPreview;
import com.microej.exercise.ui.style.Fonts;
import com.microej.exercise.ui.util.Page;
import ej.microui.display.Colors;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.TypeSelector;

/**
 * A page that represents a stub application.
 */
public class StubApplicationPage extends Page {

	private final String name;
	private final String iconPath;

	/**
	 * Creates a page that represents the application with its icon and name.
	 *
	 * @param iconPath
	 *            the path to the application icon.
	 *
	 * @param name
	 *            the name of the application.
	 *
	 */
	public StubApplicationPage(String iconPath, String name) {
		this.name = name;
		this.iconPath = iconPath;
	}

	@Override
	public Widget getWidget() {
		return new ApplicationPreview(this.iconPath, this.name);
	}

	@Override
	public void populateStylesheet(CascadingStylesheet stylesheet) {
		// defines the style of the stub application
		EditableStyle style = stylesheet.getSelectorStyle(new TypeSelector(ApplicationPreview.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));
		style.setFont(Fonts.getMediumFont());
		style.setPadding(new FlexibleOutline(0, 0, 50, 0));
	}

}
