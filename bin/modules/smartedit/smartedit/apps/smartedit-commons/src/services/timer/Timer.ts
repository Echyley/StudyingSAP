/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgZone } from '@angular/core';

/**
 * Provides a Timer object that can invoke a callback after a certain period of time.
 *
 * A `Timer` must be instanciated calling **`timerService.createTimer()`**.
 * This `Timer` service uses native setInterval function and adds additional functions to it.
 */
export class Timer {
    /**
     * Keeps the interval reference. This will only be non-null when the
     * timer is actively counting down to callback invocation
     */
    private _timer: number = null;

    /**
     * @param _callback Callback function that will be invoked upon timeout.
     * @param _duration The number of milliseconds to wait before the callback is invoked.
     */
    constructor(
        private readonly zone: NgZone,
        private _callback: () => void,
        private _duration: number = 1000
    ) {}

    /**
     * Returns true if the timer is active (counting down).
     */
    isActive(): boolean {
        return !!this._timer;
    }

    /**
     * Stops the timer, and then starts it again. If a new duration is given, the timer's duration will be set to that new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    restart(duration?: number): void {
        this._duration = duration || this._duration;
        this.stop();
        this.start();
    }

    /**
     * Start the timer, which will invoke the callback upon timeout.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    start(duration?: number): void {
        this._duration = duration || this._duration;
        this.zone.runOutsideAngular(() => {
            this._timer = setInterval(() => {
                try {
                    if (this._callback) {
                        this._callback();
                    } else {
                        this.stop();
                    }
                } catch (e) {
                    this.stop();
                }
            }, this._duration) as unknown as number;
        });
    }

    /**
     * Stop the current timer, if it is running, which will prevent the callback from being invoked.
     */
    stop(): void {
        clearInterval(this._timer);
        this._timer = null;
    }

    /**
     * Sets the duration to a new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    resetDuration(duration: number): void {
        this._duration = duration || this._duration;
    }

    /**
     * Clean up the internal object references
     */
    teardown(): void {
        this.stop();
        this._callback = null;
        this._duration = null;
        this._timer = null;
    }
}
