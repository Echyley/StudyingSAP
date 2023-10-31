/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable, NgModule, NgZone } from '@angular/core';
import {
    annotationService,
    functionsUtils,
    urlUtils,
    WindowUtils as ParentWindowUtils
} from '@smart/utils';
import * as lodash from 'lodash';
import { SeConstructor, SeFactory } from 'smarteditcommons/di/types';
import { YJQuery } from 'smarteditcommons/services';
import { TypedMap } from '../dtos';

declare global {
    /* @internal */
    interface InternalSmartedit {
        // modules loaded by extensions of smartedit or smarteditcontainer
        modules: TypedMap<NgModule>;
        // modules statically loaded for smarteditloader and smarteditcontainer
        pushedModules: NgModule[];

        /*
         * place where our custom instrumentation will save arguments of all decorators in the following format:
         * key: `{constructorName-decoratorName:definition`
         * value: the definition object
         */
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
        /* @internal */
        __smartedit__: InternalSmartedit;

        pushModules(...modules: any[]): void;
    }
}

/* forbiddenNameSpaces window._:false */
window.__smartedit__ = window.__smartedit__ || ({} as InternalSmartedit);
function getDecoratorKey(className: string, decoratorName: string): string {
    return `${className}-${decoratorName}:definition`;
}

lodash.merge(window.__smartedit__, {
    modules: {},
    pushedModules: [],
    smarteditDecoratorPayloads: {},

    addDecoratorPayload(decorator: string, className: string, payload: any) {
        const key = getDecoratorKey(decorator, className);
        if (window.__smartedit__.smarteditDecoratorPayloads[key]) {
            throw new Error('Duplicate class/decorator pair');
        }
        window.__smartedit__.smarteditDecoratorPayloads[key] = payload;
    },

    getDecoratorPayload(decorator: string, myConstructor: SeConstructor): any {
        const className = functionsUtils.getConstructorName(
            annotationService.getOriginalConstructor(myConstructor)
        );
        return window.__smartedit__.smarteditDecoratorPayloads[
            getDecoratorKey(decorator, className)
        ];
    },

    getComponentDecoratorPayload(className: string) {
        return window.__smartedit__.smarteditDecoratorPayloads[
            getDecoratorKey('Component', className)
        ];
    },

    downgradedService: {}
});

/**
 * Add here a spread of modules containing invocations of HttpBackendService to mocks some calls to the backend
 */
window.pushModules = (...modules: NgModule[]): void => {
    window.__smartedit__.pushedModules = window.__smartedit__.pushedModules.concat(modules);
};

/**
 * A collection of utility methods for windows.
 */
@Injectable()
export class WindowUtils extends ParentWindowUtils {
    static SMARTEDIT_IFRAME_ID = 'ySmartEditFrame';
    static CYPRESS_IFRAME_ID_9 = 'Your App';
    static CYPRESS_IFRAME_ID_10 = "Your project: 'Test Project'";

    private trustedIframeDomain: string;

    constructor(ngZone?: NgZone) {
        super(ngZone);
    }

    /**
     * Given the current frame, retrieves the target frame for gateway purposes
     *
     * @returns The content window or null if it does not exists.
     */
    public getGatewayTargetFrame = (): Window => {
        /*
         * For below if-else condition, original code won't work if smartedit is running
         * under environment of cypress for testing purpose, so condition to check against cypress environment is added
         * See jira ticket for details: https://jira.tools.sap/browse/CXEC-7344 & 9335
         */
        if (this._isInCypressIframe()) {
            return this._getCypressFrame();
        }

        // return the parent frame if called within the iframe
        if (this.isIframe()) {
            return this.getWindow().parent;
        }

        // return the iframe if called from the parent window
        if (this._isParentOfIframe()) {
            return this._getSmarteditIframeFromWindow();
        }

        // return null when called from the parent and no iframe exists
        return null;
    };

    public getSmarteditIframe(): HTMLElement {
        return document.querySelector('iframe#' + WindowUtils.SMARTEDIT_IFRAME_ID);
    }

    public setTrustedIframeDomain(trustedIframeSource: string): void {
        this.trustedIframeDomain = urlUtils.getOrigin(trustedIframeSource);
    }

    public getTrustedIframeDomain(): string {
        return this.trustedIframeDomain;
    }

    public isCrossOrigin(location: string): boolean {
        return urlUtils.getOrigin() !== urlUtils.getOrigin(location);
    }

    /**
     * Will call the javascrit's native setTimeout method to execute a given function after a specified period of time.
     * This method is better than using $timeout since it is difficult to assert on $timeout during end-to-end testing.
     *
     * @param func function that needs to be executed after the specified duration.
     * @param duration time in milliseconds.
     */
    public customTimeout(func: SeFactory, duration: number): void {
        setTimeout(function () {
            func();
        }, duration);
    }

    private _getCypressFrame(): Window {
        if (
            this.getWindow().parent.document.querySelector(
                'iframe#' + WindowUtils.SMARTEDIT_IFRAME_ID
            )
        ) {
            return this.getWindow().parent;
        }

        return this._getSmarteditIframeFromWindow();
    }

    private _getSmarteditIframeFromWindow(): Window {
        const smarteditIframe = this.getWindow().document.getElementById(
            WindowUtils.SMARTEDIT_IFRAME_ID
        ) as HTMLIFrameElement;
        if (!!smarteditIframe) {
            return smarteditIframe.contentWindow;
        }

        return null;
    }

    private _isInCypressIframe(): boolean {
        try {
            const iframe = this.getWindow().top.document.querySelector('iframe');
            return (
                iframe &&
                (iframe.id.includes(WindowUtils.CYPRESS_IFRAME_ID_9) ||
                    iframe.id.includes(WindowUtils.CYPRESS_IFRAME_ID_10))
            );
        } catch (e) {
            return false;
        }
    }

    private _isParentOfIframe(): boolean {
        return !!this._getSmarteditIframeFromWindow();
    }
}

export const windowUtils = new WindowUtils();
export { ParentWindowUtils };
