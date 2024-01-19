/*
 * Copyright 2022-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface.widget;

import com.microej.exercise.ui.util.TimeHelper;
import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.bon.Util;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * A widget that represents the hands of an analog watchface.
 *
 * <p>
 * It draws three hands (hour, minute, second) and updates continuously as soon as it is shown on the display. The hands
 * are rendered with bitmap images.
 */
public class WatchHands extends Widget {

	private static final int UPDATE_PERIOD = 500;

	/** The angle between two successive hours (in degrees). Negative angle is clockwise rotation. */
	private static final int DEGREES_HOUR = -30;

	/** The angle between two successive minutes (in degrees). Negative angle is clockwise rotation. */
	private static final int DEGREES_MINUTE = -6;

	/** The angle between two successive seconds (in degrees). Negative angle is clockwise rotation. */
	private static final int DEGREES_SECOND = -6;

	/** The x-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int HOURS_RX = 9;

	/** The y-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int HOURS_RY = 121;

	/** The x-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int MINUTES_RX = 9;

	/** The y-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int MINUTES_RY = 171;

	/** The x-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int SECONDS_RX = 1;

	/** The y-coordinate of the rotation center for the hour hand, relative to the top-left of the hand image. */
	private static final int SECONDS_RY = 175;

	private static final float NOON = 12;

	private static final String IMAGE_FOLDER = "/images/watchface/"; //$NON-NLS-1$

	private final Image hoursHandImage;

	private final Image minutesHandImage;

	private final Image secondsHandImage;

	private TimerTask task;

	private final Timer timer;

	/**
	 * Creates the watchface.
	 *
	 * @param timer
	 *            the timer instance to use for scheduling the internal update task.
	 */
	public WatchHands(Timer timer) {
		this.hoursHandImage = Image.getImage(IMAGE_FOLDER + "hour.png"); //$NON-NLS-1$
		this.minutesHandImage = Image.getImage(IMAGE_FOLDER + "minute.png"); //$NON-NLS-1$
		this.secondsHandImage = Image.getImage(IMAGE_FOLDER + "second.png"); //$NON-NLS-1$
		this.timer = timer;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// the optimal size is twice the height of the largest hand (seconds)
		int dimension = this.secondsHandImage.getHeight() * 2;
		size.setSize(dimension, dimension);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {

		// compute the hands angles for the current time
		long currentTimeMillis = Util.currentTimeMillis();
		float hourAngle = computeHourAngle(currentTimeMillis);
		float minuteAngle = computeMinuteAngle(currentTimeMillis);
		float secondAngle = computeSecondAngle(currentTimeMillis);

		// compute the coordinates of the rotation center (i.e., center of the widget content area)
		int centerX = contentWidth / 2;
		int centerY = contentHeight / 2;

		// render the hands
		renderHand(g, this.hoursHandImage, hourAngle, centerX, centerY, HOURS_RX, HOURS_RY);
		renderHand(g, this.minutesHandImage, minuteAngle, centerX, centerY, MINUTES_RX, MINUTES_RY);
		renderHand(g, this.secondsHandImage, secondAngle, centerX, centerY, SECONDS_RX, SECONDS_RY);

	}

	/**
	 * Renders a hand, given the image of the hand, the hand angle and the rotation center.
	 *
	 * @param g
	 *            the graphics context.
	 * @param image
	 *            the image of the hand.
	 * @param angle
	 *            the angle of the hand.
	 * @param rx
	 *            the x coordinate of the rotation center.
	 * @param ry
	 *            the y coordinate of the rotation center.
	 * @param xOffset
	 *            the x coordinate of the hand axle, relative to the image origin.
	 * @param yOffset
	 *            the y coordinate of the hand axle, relative to the image origin.
	 */
	private void renderHand(GraphicsContext g, Image image, float angle, int rx, int ry, int xOffset, int yOffset) {
		int x = rx - xOffset;
		int y = ry - yOffset;
		TransformPainter.drawRotatedImageBilinear(g, image, x, y, rx, ry, angle);
	}

	@Override
	protected void onShown() {
		super.onShown();
		// starts the hands animation when the widget is shown on the display
		startAnimation();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		// ensures that no animation is running when the watchface is not shown on the display
		stopAnimation();
	}

	private void startAnimation() {
		stopAnimation();
		this.task = new TimerTask() {

			@Override
			public void run() {
				requestRender();
			}
		};
		this.timer.schedule(this.task, UPDATE_PERIOD, UPDATE_PERIOD);
	}

	private void stopAnimation() {
		TimerTask timerTask = this.task;
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

	/**
	 * Computes the angle of the hour hand, given a time (in milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the angle of the hour hand.
	 */
	private static float computeHourAngle(long time) {
		float hour = TimeHelper.computeHour(time);
		if (hour >= NOON) {
			hour -= NOON;
		}
		return hour * DEGREES_HOUR;
	}

	/**
	 * Computes the angle of the minute hand, given a time (in milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the angle of the minute hand.
	 */
	private static float computeMinuteAngle(long time) {
		return TimeHelper.computeMinute(time) * DEGREES_MINUTE;
	}

	/**
	 * Computes the angle of the second hand, given a time (in milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the angle of the second hand.
	 */
	private static int computeSecondAngle(long time) {
		return TimeHelper.computeSeconds(time) * DEGREES_SECOND;
	}

}
