/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export declare enum SUPPORTED_BROWSERS {
    IE = 0,
    CHROME = 1,
    FIREFOX = 2,
    EDGE = 3,
    SAFARI = 4,
    UNKNOWN = 5
}
export declare class BrowserService {
    getCurrentBrowser(): SUPPORTED_BROWSERS;
    _isSafari: () => boolean;
    isIE: () => boolean;
    isFF: () => boolean;
    isSafari: () => boolean;
    getBrowserLocale(): string;
}
