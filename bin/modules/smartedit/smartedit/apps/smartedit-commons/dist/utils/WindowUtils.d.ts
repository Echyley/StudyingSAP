import { NgModule, NgZone } from '@angular/core';
import { WindowUtils as ParentWindowUtils } from '@smart/utils';
import * as lodash from 'lodash';
import { SeConstructor, SeFactory } from 'smarteditcommons/di/types';
import { YJQuery } from 'smarteditcommons/services';
import { TypedMap } from '../dtos';
declare global {
    interface InternalSmartedit {
        modules: TypedMap<NgModule>;
        pushedModules: NgModule[];
        smarteditDecoratorPayloads: TypedMap<any>;
        addDecoratorPayload: (className: string, decoratorName: string, payload: any) => void;
        getDecoratorPayload: (decorator: string, myConstructor: SeConstructor) => any;
        getComponentDecoratorPayload: (className: string) => {
            selector: string;
            template: string;
        };
        downgradedService: TypedMap<SeConstructor>;
        smartEditContainerAngularApps: string[];
        smartEditInnerAngularApps: string[];
    }
    interface Window {
        Zone: any;
        CKEDITOR: any;
        _paq: any;
        smarteditLodash: lodash.LoDashStatic;
        smarteditJQuery: YJQuery;
        __karma__: any;
        __smartedit__: InternalSmartedit;
        pushModules(...modules: any[]): void;
    }
}
/**
 * A collection of utility methods for windows.
 */
export declare class WindowUtils extends ParentWindowUtils {
    static SMARTEDIT_IFRAME_ID: string;
    static CYPRESS_IFRAME_ID_9: string;
    static CYPRESS_IFRAME_ID_10: string;
    private trustedIframeDomain;
    constructor(ngZone?: NgZone);
    /**
     * Given the current frame, retrieves the target frame for gateway purposes
     *
     * @returns The content window or null if it does not exists.
     */
    getGatewayTargetFrame: () => Window;
    getSmarteditIframe(): HTMLElement;
    setTrustedIframeDomain(trustedIframeSource: string): void;
    getTrustedIframeDomain(): string;
    isCrossOrigin(location: string): boolean;
    /**
     * Will call the javascrit's native setTimeout method to execute a given function after a specified period of time.
     * This method is better than using $timeout since it is difficult to assert on $timeout during end-to-end testing.
     *
     * @param func function that needs to be executed after the specified duration.
     * @param duration time in milliseconds.
     */
    customTimeout(func: SeFactory, duration: number): void;
    private _getCypressFrame;
    private _getSmarteditIframeFromWindow;
    private _isInCypressIframe;
    private _isParentOfIframe;
}
export declare const windowUtils: WindowUtils;
export { ParentWindowUtils };
