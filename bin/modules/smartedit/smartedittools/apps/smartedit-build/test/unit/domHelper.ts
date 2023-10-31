/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { Payload } from 'smarteditcommons';

export interface ExtentedDocument extends Document {
    mockScrollingElement(scrollingElement: Element): void;
}

export interface ElementForJQuery extends HTMLElement {
    mockedMethodsOfJQueryWrapper?: Payload;
}

class DomHelper {
    public scrollingElement: Element;

    element(
        name?: string,
        mockedMethodsOfJQueryWrapper?: Payload
    ): jasmine.SpyObj<ElementForJQuery> {
        name = name || 'element_' + Math.random();
        const prototype = jasmine.createSpyObj<ElementForJQuery>(name, [
            'dispatchEvent',
            'getBoundingClientRect'
        ]);

        /*
         * trick for lodash to be able to consider as an Element:
         * - not be a plain object (achieved by begin born off a constructor)
         * - have nodeType 1
         */

        function Clazz() {}
        Clazz.prototype = prototype;

        const mock: any = new Clazz();

        (mock as any).nodeType = 1;
        (mock as ElementForJQuery).mockedMethodsOfJQueryWrapper = mockedMethodsOfJQueryWrapper;
        return mock;
    }

    customEvent(name?: string): jasmine.SpyObj<CustomEvent> {
        name = name || 'CustomEvent_' + Math.random();
        return jasmine.createSpyObj<CustomEvent>(name, ['initCustomEvent']);
    }

    event(name?: string): jasmine.SpyObj<JQuery.Event> {
        name = name || 'JQueryEvent' + Math.random();
        return jasmine.createSpyObj<JQuery.Event>(name, ['preventDefault', 'stopPropagation']);
    }

    mockScrollingElement(scrollingElement: Element) {
        this.scrollingElement = scrollingElement;
    }

    document(): jasmine.SpyObj<Document> {
        const document = jasmine.createSpyObj('document', ['createEvent']);

        Object.assign(document, this);

        return document;
    }
}

export const domHelper = new DomHelper();
