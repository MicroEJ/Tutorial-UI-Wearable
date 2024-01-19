/*
 * Java
 *
 * Copyright 2022-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.stubapplication.widget;

import com.microej.exercise.ui.Main;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.event.Event;
import ej.microui.event.generator.Command;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.render.ImagePainter;
import ej.widget.render.StringPainter;

/**
 * A widget that represents a stub application. It displays the icon and name of the application.
 */
public class ApplicationPreview extends Widget {

	private final String name;
	private final Image icon;

	/**
	 * Creates a widget that represents the application with its icon and name.
	 *
	 * @param iconPath
	 *            the path to the application icon.
	 *
	 * @param name
	 *            the name of the application.
	 *
	 */
	public ApplicationPreview(String iconPath, String name) {
		super(true);
		this.name = name;
		this.icon = Image.getImage(iconPath);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		Font font = style.getFont();
		// the optimal size for the content takes into account the size of the name and the size of the icon.
		int width = Math.max(font.stringWidth(this.name), this.icon.getWidth());
		int height = font.getHeight() + this.icon.getHeight();
		size.setSize(width, height);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		g.setColor(style.getColor());

		// show the icon at the center of the widget
		ImagePainter.drawImageInArea(g, this.icon, 0, 0, contentWidth, contentHeight, Alignment.HCENTER,
				Alignment.VCENTER);

		// show the name at the bottom of the widget
		StringPainter.drawStringInArea(g, this.name, style.getFont(), 0, 0, contentWidth, contentHeight,
				Alignment.HCENTER, Alignment.BOTTOM);
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Command.EVENT_TYPE) {
			// the physical button has been pressed, show the application list
			Main.showApplicationMenu();
			return true;
		}
		return super.handleEvent(event);
	}

}
