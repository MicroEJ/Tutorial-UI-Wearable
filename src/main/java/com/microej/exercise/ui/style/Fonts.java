/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.style;

import ej.microui.display.Font;

/**
 * A class that provides convenient methods for managing the fonts used by the application.
 */
public class Fonts {

	private static final String SMALL_FONT_PATH = "/fonts/SourceSansPro_21px-600.ejf"; //$NON-NLS-1$

	private static final String MEDIUM_FONT_PATH = "/fonts/SourceSansPro_27px-500.ejf"; //$NON-NLS-1$

	private static final String LARGE_FONT_PATH = "/fonts/SourceSansPro_53px-600.ejf"; //$NON-NLS-1$

	/**
	 * Gets the small-sized font to use in the application.
	 *
	 * @return the small-sized font.
	 */
	public static Font getSmallFont() {
		return Font.getFont(SMALL_FONT_PATH);
	}

	/**
	 * Gets the medium-sized font to use in the application.
	 *
	 * @return the medium-sized font.
	 */
	public static Font getMediumFont() {
		return Font.getFont(MEDIUM_FONT_PATH);
	}

	/**
	 * Gets the large-sized font to use in the application.
	 *
	 * @return the large-sized font.
	 */
	public static Font getLargeFont() {
		return Font.getFont(LARGE_FONT_PATH);
	}

}
