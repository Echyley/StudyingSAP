/// <reference types="jquery" />
export interface YJQuery extends JQueryStatic {
    /**
     * Return the CSS path of the wrapped JQuery element.
     */
    getCssPath: () => string;
}
export declare const YJQUERY_TOKEN = "yjQuery";
/**
 * Return a jQuery wrapping factory while preserving potentially pre-existing jQuery in storefront and SmartEditContainer
 */
export declare function yjQueryServiceFactory(): YJQuery;
/**
 * This module manages the use of the jQuery library.
 * It enables to work with a "noConflict" version of jQuery in a storefront that may contain another version.
 */
export declare class YjqueryModule {
}
