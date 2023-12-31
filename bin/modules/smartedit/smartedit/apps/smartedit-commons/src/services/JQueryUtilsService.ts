/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';

import { nodeUtils, EXTENDED_VIEW_PORT_MARGIN_TOKEN, WindowUtils } from '../utils';
import { YJQUERY_TOKEN } from './vendors/YjqueryModule';

/**
 * Angular utility service for JQuery operations
 */
@Injectable()
export class JQueryUtilsService {
    constructor(
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        @Inject(DOCUMENT) private readonly document: Document,
        @Inject(EXTENDED_VIEW_PORT_MARGIN_TOKEN) private readonly EXTENDED_VIEW_PORT_MARGIN: number,
        private readonly windowUtils: WindowUtils
    ) {}

    /**
     * Parses a string HTML into a queriable DOM object
     * @param parent the DOM element from which we want to extract matching selectors
     * @param extractionSelector the yjQuery selector identifying the elements to be extracted
     */
    public extractFromElement(parent: any, extractionSelector: string): JQuery {
        const element = this.yjQuery(parent);

        return element.filter(extractionSelector).add(element.find(extractionSelector));
    }

    /**
     * Parses a string HTML into a queriable DOM object, preserving any JavaScript present in the HTML.
     * Note - as this preserves the JavaScript present it must only be used on HTML strings originating
     * from a known safe location. Failure to do so may result in an XSS vulnerability.
     *
     */
    public unsafeParseHTML(stringHTML: string): Node[] {
        return this.yjQuery.parseHTML(stringHTML, null, true);
    }

    /**
     * Checks whether passed HTMLElement is a part of the DOM
     */
    public isInDOM(element: HTMLElement): boolean {
        return this.document.documentElement.contains(element);
    }

    /**
     *
     * Determines whether a DOM element is partially or totally intersecting with the "extended" viewPort
     * the "extended" viewPort is the real viewPort that extends up and down by a margin, in pixels, given by the overridable constant EXTENDED_VIEW_PORT_MARGIN
     */
    public isInExtendedViewPort(element: HTMLElement): boolean {
        if (!this.document.documentElement.contains(element)) {
            return false;
        }

        const elem = this.yjQuery(element);
        const bounds = {
            ...elem.offset(),
            width: elem.outerWidth(),
            height: elem.outerHeight()
        };

        const doc = this.document.scrollingElement || this.document.documentElement;

        return nodeUtils.areIntersecting(
            {
                left: -this.EXTENDED_VIEW_PORT_MARGIN + doc.scrollLeft,
                width: this.windowUtils.getWindow().innerWidth + 2 * this.EXTENDED_VIEW_PORT_MARGIN,
                top: -this.EXTENDED_VIEW_PORT_MARGIN + doc.scrollTop,
                height:
                    this.windowUtils.getWindow().innerHeight + 2 * this.EXTENDED_VIEW_PORT_MARGIN
            } as ClientRect,
            bounds as ClientRect
        );
    }
}
