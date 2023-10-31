/// <reference types="jquery" />
import { WindowUtils } from '../utils';
/**
 * Angular utility service for JQuery operations
 */
export declare class JQueryUtilsService {
    private readonly yjQuery;
    private readonly document;
    private readonly EXTENDED_VIEW_PORT_MARGIN;
    private readonly windowUtils;
    constructor(yjQuery: JQueryStatic, document: Document, EXTENDED_VIEW_PORT_MARGIN: number, windowUtils: WindowUtils);
    /**
     * Parses a string HTML into a queriable DOM object
     * @param parent the DOM element from which we want to extract matching selectors
     * @param extractionSelector the yjQuery selector identifying the elements to be extracted
     */
    extractFromElement(parent: any, extractionSelector: string): JQuery;
    /**
     * Parses a string HTML into a queriable DOM object, preserving any JavaScript present in the HTML.
     * Note - as this preserves the JavaScript present it must only be used on HTML strings originating
     * from a known safe location. Failure to do so may result in an XSS vulnerability.
     *
     */
    unsafeParseHTML(stringHTML: string): Node[];
    /**
     * Checks whether passed HTMLElement is a part of the DOM
     */
    isInDOM(element: HTMLElement): boolean;
    /**
     *
     * Determines whether a DOM element is partially or totally intersecting with the "extended" viewPort
     * the "extended" viewPort is the real viewPort that extends up and down by a margin, in pixels, given by the overridable constant EXTENDED_VIEW_PORT_MARGIN
     */
    isInExtendedViewPort(element: HTMLElement): boolean;
}
