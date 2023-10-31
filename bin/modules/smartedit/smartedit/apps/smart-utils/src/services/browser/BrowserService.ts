/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

export enum SUPPORTED_BROWSERS {
    IE,
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI,
    UNKNOWN
}
export class BrowserService {
    getCurrentBrowser(): SUPPORTED_BROWSERS {
        /* forbiddenNameSpaces window as any:false */
        const anyWindow = window as any;
        let browser = SUPPORTED_BROWSERS.UNKNOWN;
        if (typeof anyWindow.InstallTrigger !== 'undefined') {
            browser = SUPPORTED_BROWSERS.FIREFOX;
        } else if (/* @cc_on!@*/ false || !!(document as any).documentMode) {
            browser = SUPPORTED_BROWSERS.IE;
        } else if (!!anyWindow.StyleMedia) {
            browser = SUPPORTED_BROWSERS.EDGE;
        } else if (!!anyWindow.chrome && !!anyWindow.chrome.webstore) {
            browser = SUPPORTED_BROWSERS.CHROME;
        } else if (this._isSafari()) {
            browser = SUPPORTED_BROWSERS.SAFARI;
        }

        return browser;
    }

    /*
		It is always better to detect a browser via features. Unfortunately, it's becoming really hard to identify
		Safari, since newer versions do not match the previous ones. Thus, we have to rely on User Agent as the last
		option.
	*/
    _isSafari = (): boolean => {
        const { userAgent } = window.navigator;
        const vendor = window.navigator.vendor;

        return (
            vendor && vendor.indexOf('Apple') > -1 && userAgent && userAgent.indexOf('Safari') > -1
        );
    };

    isIE = (): boolean => this.getCurrentBrowser() === SUPPORTED_BROWSERS.IE;

    isFF = (): boolean => this.getCurrentBrowser() === SUPPORTED_BROWSERS.FIREFOX;

    isSafari = (): boolean => this.getCurrentBrowser() === SUPPORTED_BROWSERS.SAFARI;

    getBrowserLocale(): string {
        const locale = window.navigator.language.split('-');
        return locale.length === 1 ? locale[0] : `${locale[0]}_${locale[1].toUpperCase()}`;
    }
}
