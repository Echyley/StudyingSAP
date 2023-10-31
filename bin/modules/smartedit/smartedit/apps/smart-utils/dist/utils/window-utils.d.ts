/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { NgZone } from '@angular/core';
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
export declare class WindowUtils {
    private ngZone?;
    static SMARTEDIT_IFRAME_ID: string;
    constructor(ngZone?: NgZone);
    getWindow(): Window;
    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#isIframe
     * @methodOf @smartutils.services:WindowUtils
     * @description
     * <b>isIframe</b> will check if the current document is in an iFrame.
     * @returns {boolean} true if the current document is in an iFrame.
     */
    isIframe: () => boolean;
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
    runTimeoutOutsideAngular(callback: () => void, timeout?: number): number;
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
    runIntervalOutsideAngular(callback: () => void, timeout?: number): number;
}
export declare const windowUtils: WindowUtils;
