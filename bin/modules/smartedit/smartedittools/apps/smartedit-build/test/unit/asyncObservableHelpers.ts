import { defer } from 'rxjs';
import { DebugElement } from '@angular/core';
import { tick, ComponentFixture } from '@angular/core/testing';

/**
 * This helper's observable emits the data value in the next turn of the JavaScript engine.
 * https://v11.angular.io/guide/testing-components-scenarios#async-observable-helpers
 */

/**
 * Create async observable that emits-once and completes
 * after a JS engine turn
 */
export function asyncData<T>(data: T) {
    return defer(() => Promise.resolve(data));
}

/**
 * Create async observable error that errors
 * after a JS engine turn
 */
export function asyncError<T>(errorObject: any) {
    return defer(() => Promise.reject(errorObject));
}

/** Short util function from Angular Official test case sample. */
/** Wait a tick, then detect changes */
export function advance(f: ComponentFixture<any>): void {
    tick();
    f.detectChanges();
}

export const ButtonClickEvents = {
    left: { button: 0 },
    right: { button: 2 }
};

/** Simulate element click. Defaults to mouse left-button click event. */
export function click(
    el: DebugElement | HTMLElement,
    eventObj: any = ButtonClickEvents.left
): void {
    if (el instanceof HTMLElement) {
        el.click();
    } else {
        el.triggerEventHandler('click', eventObj);
    }
}
