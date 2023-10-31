import { NgZone } from '@angular/core';
/**
 * Provides a Timer object that can invoke a callback after a certain period of time.
 *
 * A `Timer` must be instanciated calling **`timerService.createTimer()`**.
 * This `Timer` service uses native setInterval function and adds additional functions to it.
 */
export declare class Timer {
    private readonly zone;
    private _callback;
    private _duration;
    /**
     * Keeps the interval reference. This will only be non-null when the
     * timer is actively counting down to callback invocation
     */
    private _timer;
    /**
     * @param _callback Callback function that will be invoked upon timeout.
     * @param _duration The number of milliseconds to wait before the callback is invoked.
     */
    constructor(zone: NgZone, _callback: () => void, _duration?: number);
    /**
     * Returns true if the timer is active (counting down).
     */
    isActive(): boolean;
    /**
     * Stops the timer, and then starts it again. If a new duration is given, the timer's duration will be set to that new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    restart(duration?: number): void;
    /**
     * Start the timer, which will invoke the callback upon timeout.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    start(duration?: number): void;
    /**
     * Stop the current timer, if it is running, which will prevent the callback from being invoked.
     */
    stop(): void;
    /**
     * Sets the duration to a new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    resetDuration(duration: number): void;
    /**
     * Clean up the internal object references
     */
    teardown(): void;
}
