/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.util;

import ej.annotation.Nullable;
import ej.bon.TimerTask;
import ej.bon.XMath;
import ej.observable.Observer;
import ej.observable.SimpleObservable;

import java.util.Random;

/**
 * Defines the data model for this application.
 *
 * <p>
 * This class provides the data for the pages of the application, like stub sensing data (heart rate, step count) or
 * stub power level. This stub data is generated and updated every 5 seconds to simulate the life cycle of a real
 * device.
 *
 * <p>
 * This class is a {@link SimpleObservable}, so classes can register as {@link Observer} of this class to be notified
 * when the data changes (see {@link #setObserver(Observer)}.
 *
 */
public class Model extends SimpleObservable {

	/* Step activity constants */

	private static final int INITIAL_STEP_COUNT = 1473;
	private static final int MAX_STEP_OFFEST = 2000;
	private static final int METERS_IN_KM = 1000;
	private static final float AVERAGE_METERS_BY_STEP = 0.85f;
	private static final int STEP_GOAL = 9999;

	/* Heart Rate monitoring constants */
	private static final int INITIAL_HR_SEED = 60;
	private static final int MAX_HR_AMPLITUDE = 10;
	private static final int MIN_HR = 40;
	private static final int MAX_HR = 180;

	/* Battery constants */

	private static final int INITIAL_BATTERY_LEVEL = 50;
	private static final int MAX_BATTERY_LEVEL = 100;
	private static final int BATTERY_LEVEL_INCREMENT = 5;

	/* Data update rate */
	private static final int UPDATE_PERIOD = 5000;

	private static final int INITIAL_SELECTED_APPLICATION = 0;

	private static final Model INSTANCE = new Model();

	private int stepCount;

	private int heartRate;

	private int heartRateSeed;

	private int batteryLevel;

	private int selectedApplicationIndex;

	private final Random random;

	@Nullable
	private TimerTask updateTask;

	private Model() {
		this.random = new Random();
		this.stepCount = INITIAL_STEP_COUNT;
		this.heartRateSeed = INITIAL_HR_SEED;
		updateHeartRate();
		this.selectedApplicationIndex = INITIAL_SELECTED_APPLICATION;
		this.batteryLevel = INITIAL_BATTERY_LEVEL;
	}

	/**
	 * Gets the singleton instance of the data model.
	 *
	 * @return the model instance.
	 */
	public static final Model getInstance() {
		return INSTANCE;
	}

	/**
	 * Gets the daily step goal.
	 *
	 * @return the daily step goal.
	 */
	public int getStepGoal() {
		return STEP_GOAL;
	}

	/**
	 * Gets the current step count.
	 *
	 * @return the current step count.
	 */
	public int getStepCount() {
		return this.stepCount;
	}

	/**
	 * Gets the current battery level.
	 *
	 * @return the current battery level.
	 */
	public int getBatteryLevel() {
		return this.batteryLevel;
	}

	/**
	 * Gets the last selected index in the application list.
	 *
	 * @return the selected application index.
	 */
	public int getSelectedApplicationIndex() {
		return this.selectedApplicationIndex;
	}

	/**
	 * Sets the selected index in the application list.
	 *
	 * @param index
	 *            the selected index.
	 */
	public void setSelectedApplicationIndex(int index) {
		this.selectedApplicationIndex = index;
	}

	/**
	 * Gets the daily distance walked.
	 *
	 * @return the distance walked this day.
	 */
	public float getDistance() {
		return this.stepCount * AVERAGE_METERS_BY_STEP / METERS_IN_KM;
	}

	/**
	 * Gets the current heart rate.
	 *
	 * @return the current heart rate.
	 */
	public int getHeartRate() {
		return this.heartRate;
	}

	/**
	 * Starts tracking the user and device data.
	 *
	 * <p>
	 * It starts a new {@link TimerTask} that generates periodically new stub user and device data (heart rate, step
	 * count, battery level).
	 *
	 */
	public void start() {
		stop();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				updateData();

				// the model data changed, notify the observer
				setChanged();
				notifyObserver();
			}
		};

		// starts the periodic task that updates the model data
		TimeHelper.getTimer().schedule(task, UPDATE_PERIOD, UPDATE_PERIOD);
		this.updateTask = task;
	}

	/**
	 * Stops tracking the user and device data.
	 *
	 * <p>
	 * This stops the backing {@link TimerTask}.
	 */
	public void stop() {
		TimerTask task = this.updateTask;
		if (task != null) {
			task.cancel();
		}
	}

	private void updateData() {
		updateHeartRate();
		updateSteps();
		updateBatteryLevel();
	}

	private void updateSteps() {
		if (this.stepCount < STEP_GOAL) {
			this.stepCount = XMath.limit(this.stepCount + this.random.nextInt(MAX_STEP_OFFEST), INITIAL_STEP_COUNT,
					STEP_GOAL);
		} else {
			this.stepCount = INITIAL_STEP_COUNT;
		}
	}

	private void updateHeartRate() {
		int previousValue = this.heartRate;
		int offset = (int) (this.random.nextGaussian() * MAX_HR_AMPLITUDE);
		int nextHeartRate = XMath.limit(this.heartRateSeed + offset, MIN_HR, MAX_HR);
		if (Math.abs(previousValue - nextHeartRate) > MAX_HR_AMPLITUDE / 2) {
			this.heartRateSeed = nextHeartRate;
		}

		this.heartRate = nextHeartRate;
	}

	private void updateBatteryLevel() {
		if (this.batteryLevel == MAX_BATTERY_LEVEL) {
			this.batteryLevel = 0;
			return;
		}
		this.batteryLevel = XMath.limit(this.batteryLevel + BATTERY_LEVEL_INCREMENT, 0, MAX_BATTERY_LEVEL);
	}

}
