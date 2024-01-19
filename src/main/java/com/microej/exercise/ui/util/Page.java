/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.util;

import com.microej.exercise.ui.Main;
import ej.mwt.Widget;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.observable.Observer;

/**
 * A page of the application.
 *
 * <p>
 * Pages are at the core of the navigation framework of this application, see {@link Main#showPage(Page)}.
 *
 * <p>
 * To get the widget representation of a page, users should call {@link #getWidget()}.
 */
public abstract class Page implements Observer {

	/**
	 * Gets the widget that represents the content of the page.
	 *
	 * @return the root widget for this page.
	 */
	public abstract Widget getWidget();

	/**
	 * Populates the given stylesheet with the style of the page.
	 *
	 * @param stylesheet
	 *            the stylesheet to populate.
	 */
	public abstract void populateStylesheet(CascadingStylesheet stylesheet);

	@Override
	public void update() {
		// do nothing by default.
	}
}
