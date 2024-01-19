/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface.widget;

import com.microej.exercise.ui.util.TimeHelper;
import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.bon.Util;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that represents a digital clock.
 *
 * <p>
 * The clock format is <code>hh:mmss</code>.
 */
public class DigitalClock extends Widget {

	/** The extra style field for defining the font to use for the seconds. */
	public final static int EXTRA_FIELD_SECONDS_FONT = 0;

	/** The pattern for the hour:minute pattern (<code>hh:mm</code>) */
	private static final String CLOCK_PATTERN = "88:88"; //$NON-NLS-1$

	/** The pattern for the seconds pattern (<code>ss</code>) */
	private static final String SECONDS_PATTERN = "88"; //$NON-NLS-1$

	/** The delay between two updates of the clock. */
	private static final int UPDATE_PERIOD = 500;

	private TimerTask task;

	private final Timer timer;

	/**
	 * Creates a widget that represents a digital clock.
	 *
	 * @param timer
	 *            the timer instance to use for scheduling the internal update task.
	 */
	public DigitalClock(Timer timer) {
		this.timer = timer;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		// retrieves the style for this widget
		Style style = getStyle();
		Font font = style.getFont();
		// retrieves the font for the seconds as an extra style field
		Font secondsFont = style.getExtraObject(EXTRA_FIELD_SECONDS_FONT, Font.class, font);

		// sets the color from the style to be the color to use for the drawings
		g.setColor(style.getColor());

		// computes the hour, minute and second from the current time in milliseconds
		long currentTime = Util.currentTimeMillis();
		String clockText = TimeHelper.formatClock(currentTime);
		String secondsText = TimeHelper.formatSeconds(currentTime);

		// computes the text anchor point
		int clockWidth = font.stringWidth(CLOCK_PATTERN);
		int fullWidth = clockWidth + secondsFont.stringWidth(SECONDS_PATTERN);
		int x = Alignment.computeLeftX(fullWidth, 0, contentWidth, style.getHorizontalAlignment());
		int y = Alignment.computeTopY(font.getHeight(), 0, contentHeight, style.getVerticalAlignment());

		// draws the hh:mm string
		Painter.drawString(g, clockText, font, x, y);

		// draws the seconds on the same baseline
		int baseline = y + font.getBaselinePosition();
		x += clockWidth;
		Painter.drawString(g, secondsText, secondsFont, x, baseline - secondsFont.getBaselinePosition());
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// retrieves the style for this widget
		Style style = getStyle();
		Font font = style.getFont();
		// retrieves the font for the seconds as an extra style field
		Font secondsFont = style.getExtraObject(EXTRA_FIELD_SECONDS_FONT, Font.class, font);

		// computes the optimal size of the text from the hh:mmss pattern
		int width = font.stringWidth(CLOCK_PATTERN) + secondsFont.stringWidth(SECONDS_PATTERN);
		int height = Math.max(font.getHeight(), secondsFont.getHeight());

		size.setSize(width, height);
	}

	@Override
	protected void onShown() {
		super.onShown();
		// starts updating the clock when the widget is shown on the display
		startUpdateTask();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		// stops updating the clock when the widget is hidden
		stopUpdateTask();
	}

	private void startUpdateTask() {
		stopUpdateTask();
		this.task = new TimerTask() {

			@Override
			public void run() {
				// requests a new render at each execution, to update the clock content with current time
				requestRender();
			}
		};

		this.timer.schedule(this.task, UPDATE_PERIOD, UPDATE_PERIOD);
	}

	private void stopUpdateTask() {
		TimerTask timerTask = this.task;
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

}
