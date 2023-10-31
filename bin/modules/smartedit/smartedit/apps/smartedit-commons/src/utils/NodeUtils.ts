/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { camelCase } from 'lodash';
import $script from 'scriptjs';
import { ComponentAttributes, SeFactory } from '../di';

/**
 * A collection of utility methods for Nodes.
 */
export class NodeUtils {
    /**
     * Retrieves all the attributes of an overlay Node (identified by its data-smartedit-element-uuid attribute) containing with data-smartedit- or smartedit-
     * and package them as a map of key values.
     * Keys are stripped of data- and are turned to camelcase
     * @returns Map of key values.
     */
    collectSmarteditAttributesByElementUuid(elementUuid: string): ComponentAttributes {
        const element = document.querySelector(
            `.smartEditComponent[data-smartedit-element-uuid='${elementUuid}']`
        );
        if (!element) {
            throw new Error(`could not find element with uuid ${elementUuid}`);
        }
        return Array.from(element.attributes).reduce((attributes, node: Attr) => {
            let attrName = node.name;
            if (attrName.indexOf('smartedit-') > -1) {
                attrName = camelCase(attrName.replace('data-', ''));
                attributes[attrName] = node.value;
            }
            return attributes;
        }, {} as ComponentAttributes);
    }

    /**
     * A function to sort an array containing DOM elements according to their position in the DOM
     *
     * @param key Optional key value to get the
     *
     * @returns The compare function to use with array.sort(compareFunction) to order DOM elements as they would appear in the DOM
     */
    compareHTMLElementsPosition(key?: string) {
        return function (a: any, b: any): number {
            if (key) {
                a = a[key];
                b = b[key];
            }

            if (a === b) {
                return 0;
            }
            if (!a.compareDocumentPosition) {
                // support for IE8 and below
                return a.sourceIndex - b.sourceIndex;
            }
            if (a.compareDocumentPosition(b) & 2) {
                // Note: CompareDocumentPosition returns the compared value as a bitmask that can take several values.
                // 2 represents DOCUMENT_POSITION_PRECEDING, which means that b comes before a.
                return 1;
            }

            return -1;
        };
    }

    /**
     * Method will check if the given point is over the htmlElement.
     *
     * @returns True if the given point is over the htmlElement.
     */
    isPointOverElement(point: { x: number; y: number }, htmlElement: HTMLElement): boolean {
        const domRect = htmlElement.getBoundingClientRect();
        return (
            point.x >= domRect.left &&
            point.x <= domRect.right &&
            point.y >= domRect.top &&
            point.y <= domRect.bottom
        );
    }

    /**
     * Determines whether 2 BoundingClientRect are intersecting even partially.
     *
     * @param boundingClientRect1 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @param boundingClientRect2 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @returns True if there is a partial or total intersection
     */
    areIntersecting(boundingClientRect1: ClientRect, boundingClientRect2: ClientRect): boolean {
        return !(
            boundingClientRect2.left > boundingClientRect1.left + boundingClientRect1.width ||
            boundingClientRect2.left + boundingClientRect2.width < boundingClientRect1.left ||
            boundingClientRect2.top > boundingClientRect1.top + boundingClientRect1.height ||
            boundingClientRect2.top + boundingClientRect2.height < boundingClientRect1.top
        );
    }

    injectJS(): {
        getInjector: () => any;
        execute: (conf: { srcs: string[]; callback: SeFactory; index?: number }) => void;
    } {
        function getInjector(): $script {
            return $script;
        }

        return {
            getInjector,
            execute(conf: { srcs: string[]; callback: SeFactory; index?: number }): void {
                const srcs = conf.srcs;
                let index = conf.index;
                const callback = conf.callback;
                if (!srcs.length) {
                    callback();
                    return;
                }
                if (index === undefined) {
                    index = 0;
                }
                if (srcs[index] !== undefined) {
                    this.getInjector()(srcs[index], () => {
                        if (index + 1 < srcs.length) {
                            this.execute({
                                srcs,
                                index: index + 1,
                                callback
                            });
                        } else if (typeof callback === 'function') {
                            callback();
                        }
                    });
                }
            }
        };
    }
}

export const nodeUtils = new NodeUtils();
