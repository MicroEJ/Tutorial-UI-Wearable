/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface.widget;

import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that represents a text with an icon.
 */
public class IconLabel extends Widget {

	/** The extra style field for defining the icon color. */
	public static final int EXTRA_FIELD_ICON_COLOR = 0;

	private String text;

	private final Image icon;

	/**
	 * Creates the widget with a path to the icon resource to use and the text to display.
	 *
	 * @param iconPath
	 *            the path to the icon to use.
	 * @param text
	 *            the text to use.
	 */
	public IconLabel(String iconPath, String text) {
		this.text = text;
		this.icon = Image.getImage(iconPath);
	}

	/**
	 * Sets the text to display.
	 *
	 * @param text
	 *            the text to display.
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		// retrieves the style for this widget
		Style style = getStyle();
		Font font = style.getFont();
		int textColor = style.getColor();
		int iconColor = style.getExtraInt(IconLabel.EXTRA_FIELD_ICON_COLOR, textColor);

		// sets the color to use for coloring the image
		g.setColor(iconColor);

		// draws the icon
		int textWidth = font.stringWidth(this.text);
		int availableWidth = contentWidth - textWidth;
		int iconWidth = this.icon.getWidth();
		// computes the position of the anchor point of the image (top-left corner)
		int iconX = Alignment.computeLeftX(iconWidth, 0, availableWidth, style.getHorizontalAlignment());
		int iconY = Alignment.computeTopY(this.icon.getHeight(), 0, contentHeight, style.getVerticalAlignment());
		Painter.drawImage(g, this.icon, iconX, iconY);

		// set the color to use for the text
		g.setColor(textColor);

		// draws the text
		// computes the position of the anchor point of the text (top-left corner)
		int textX = iconX + iconWidth;
		int textY = Alignment.computeTopY(font.getHeight(), 0, contentHeight, style.getVerticalAlignment());
		Painter.drawString(g, this.text, font, textX, textY);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// retrieves the style for this widget
		Style style = getStyle();
		Font font = style.getFont();

		// the optimal content width is the sum of the icon width and text width
		int width = this.icon.getWidth() + font.stringWidth(this.text);
		// the optimal content height is the maximum between the icon height and text height
		int height = Math.max(this.icon.getHeight(), font.getHeight());

		size.setSize(width, height);
	}

}
