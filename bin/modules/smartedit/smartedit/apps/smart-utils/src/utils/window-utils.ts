/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { NgZone } from '@angular/core';
import { commonNgZone } from './common-ng-zone';

declare global {
    interface Window {
        Zone: any;
        __karma__: any;
    }
}

/**
 * @ngdoc service
 * @name @smartutils.services:WindowUtils
 *
 * @description
 * A collection of utility methods for windows.
 */
export class WindowUtils {
    static SMARTEDIT_IFRAME_ID = 'ySmartEditFrame';

    constructor(private ngZone?: NgZone) {
        this.ngZone = this.ngZone || commonNgZone;
    }

    getWindow(): Window {
        return window;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#isIframe
     * @methodOf @smartutils.services:WindowUtils
     * @description
     * <b>isIframe</b> will check if the current document is in an iFrame.
     * @returns {boolean} true if the current document is in an iFrame.
     */
    isIframe = (): boolean => this.getWindow().top !== this.getWindow();

    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#runTimeoutOutsideAngular
     * @methodOf @smartutils.services:WindowUtils
     *
     * @description
     * Runs a given timeout outside Angular and attaches its callback to Angular
     * this is usefull in order not to be blocking from an e2e stand point
     *
     * @param {string} callback argument less callback to execute when timeout.
     * @param {number} timeout the delay in milliseconds until timeout
     */
    runTimeoutOutsideAngular(callback: () => void, timeout?: number): number {
        const ngZone = this.ngZone;
        if (ngZone !== undefined) {
            return ngZone.runOutsideAngular<number>(
                () => setTimeout(() => ngZone.run(callback), timeout) as unknown as number
            );
        } else {
            throw new Error(
                'this instance of WindowUtils has not been instantiated through Angular 7 DI'
            );
        }
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#runIntervalOutsideAngular
     * @methodOf @smartutils.services:WindowUtils
     *
     * @description
     * Runs a given interval outside Angular and attaches its callback to Angular
     * this is usefull in order not to be blocking from an e2e stand point
     *
     * @param {string} callback argument less callback to execute when timeout.
     * @param {number} timeout the delay in milliseconds until timeout
     */
    runIntervalOutsideAngular(callback: () => void, timeout?: number): number {
        const ngZone = this.ngZone;
        if (ngZone === undefined) {
            throw new Error(
                'this instance of WindowUtils has not been instantiated through Angular 7 DI'
            );
        }
        return ngZone.runOutsideAngular<number>(
            () => setInterval(() => ngZone.run(callback), timeout) as unknown as number
        );
    }
}

export const windowUtils = new WindowUtils();
