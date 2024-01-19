/*
 * Java
 *
 * Copyright 2020-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.util;

import ej.bon.Timer;

/**
 * A utility class that provides convenient methods for handling time.
 */
public class TimeHelper {

	private static final char CLOCK_SEPARATOR = ':';

	private static final char PAD_CHARACTER = '0';

	private static final int PAD_THRESHOLD = 10;

	private static final int HOURS_IN_DAY = 24;

	private static final int MINUTES_IN_HOUR = 60;

	private static final int SECONDS_IN_MINUTE = 60;

	private static final int MILLISECONDS_IN_SECOND = 1000;

	private static final int MS_IN_MINUTES = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE;

	private static final int MS_IN_HOUR = MS_IN_MINUTES * MINUTES_IN_HOUR;

	private static final int MILLISECONDS_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE
			* MILLISECONDS_IN_SECOND;

	private static final Timer TIMER_INSTANCE = new Timer();

	private TimeHelper() {
		// prevents instantiation.
	}

	/**
	 * Gets the {@link Timer} instance to use across the application.
	 *
	 * @return the sole timer instance.
	 */
	public static Timer getTimer() {
		return TIMER_INSTANCE;
	}

	/**
	 * Computes the second given a time (milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the second value for the given time.
	 */
	public static int computeSeconds(long time) {
		return (int) (time % MS_IN_MINUTES) / MILLISECONDS_IN_SECOND;
	}

	/**
	 * Computes the minute given a time (milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the minute value for the given time.
	 */
	public static float computeMinute(long time) {
		return (float) (time % MS_IN_HOUR) / MS_IN_MINUTES;
	}

	/**
	 * Computes the hour given a time (milliseconds since Epoch).
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return the hour value for the given time.
	 */
	public static float computeHour(long time) {
		return (float) (time % MILLISECONDS_IN_DAY) / MS_IN_HOUR;
	}

	/**
	 * Computes and formats the hour with the pattern <code>hh</code> from the given time.
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return a string that represents the hour for the given time.
	 */
	public static String formatHour(long time) {
		return pad((int) computeHour(time));
	}

	/**
	 * Computes and formats the minute with the pattern <code>mm</code> from the given time.
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return a string that represents the minute for the given time.
	 */
	public static String formatMinute(long time) {
		return pad((int) computeMinute(time));
	}

	/**
	 * Computes and formats the second with the pattern <code>ss</code> from the given time.
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return a string that represents the second for the given time.
	 */
	public static String formatSeconds(long time) {
		return pad(computeSeconds(time));
	}

	/**
	 * Formats the time with the pattern <code>hh:mm</code>, given the given time.
	 *
	 * @param time
	 *            a time, in milliseconds since Epoch.
	 * @return a string that represents a clock (<code>hh:mm</code> for the given time.
	 */
	public static String formatClock(long time) {
		return pad((int) computeHour(time)) + CLOCK_SEPARATOR + pad((int) computeMinute(time));
	}

	/**
	 * Pads the given value with one zero if it is lower than 10.
	 *
	 * @param value
	 *            the value to pad.
	 * @return a string that represents the value padded with one zero if it is lower than 10.
	 */
	private static String pad(int value) {
		StringBuilder builder = new StringBuilder();
		if (value < PAD_THRESHOLD) {
			builder.append(PAD_CHARACTER);
		}
		builder.append(value);
		return builder.toString();
	}

}
