import { ComponentAttributes, SeFactory } from '../di';
/**
 * A collection of utility methods for Nodes.
 */
export declare class NodeUtils {
    /**
     * Retrieves all the attributes of an overlay Node (identified by its data-smartedit-element-uuid attribute) containing with data-smartedit- or smartedit-
     * and package them as a map of key values.
     * Keys are stripped of data- and are turned to camelcase
     * @returns Map of key values.
     */
    collectSmarteditAttributesByElementUuid(elementUuid: string): ComponentAttributes;
    /**
     * A function to sort an array containing DOM elements according to their position in the DOM
     *
     * @param key Optional key value to get the
     *
     * @returns The compare function to use with array.sort(compareFunction) to order DOM elements as they would appear in the DOM
     */
    compareHTMLElementsPosition(key?: string): (a: any, b: any) => number;
    /**
     * Method will check if the given point is over the htmlElement.
     *
     * @returns True if the given point is over the htmlElement.
     */
    isPointOverElement(point: {
        x: number;
        y: number;
    }, htmlElement: HTMLElement): boolean;
    /**
     * Determines whether 2 BoundingClientRect are intersecting even partially.
     *
     * @param boundingClientRect1 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @param boundingClientRect2 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @returns True if there is a partial or total intersection
     */
    areIntersecting(boundingClientRect1: ClientRect, boundingClientRect2: ClientRect): boolean;
    injectJS(): {
        getInjector: () => any;
        execute: (conf: {
            srcs: string[];
            callback: SeFactory;
            index?: number;
        }) => void;
    };
}
export declare const nodeUtils: NodeUtils;
