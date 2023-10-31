/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lo from 'lodash';
import { domHelper, ElementForJQuery } from "./domHelper";
import { Primitive } from 'smarteditcommons';

class JQueryHelper {
    private IDEMPOTENT_METHODS = ['show', 'hide', 'on', 'off'];
    private DEFAULT_MOCKED_JQUERY_FUNCTIONS = [
        'get',
        'data',
        'css',
        'width',
        'height',
        'offset',
        'html',
        'attr',
        'getBoundingClientRect',
        'find',
        'closest',
        'removeAttr',
        'contents'
    ];

    jQuery(selectorTransform?: (selector: string) => jasmine.SpyObj<JQuery>): JQueryStatic {
        const jq = (window as any).$ || window.smarteditJQuery;
        const jqSpy = jasmine.createSpy('jQueryMock', jq);

        jqSpy.and.callFake((element: ElementForJQuery | string) => {
            if (typeof element === 'string') {
                if (selectorTransform) {
                    return selectorTransform(element);
                } else {
                    return this.wrap(
                        'jqSpyForElement',
                        domHelper.element(`Element Mock for selector ${element}`)
                    );
                }
            } else {
                return this.wrap('jqSpyForElement', element);
            }
        });

        return (jqSpy as any) as JQueryStatic;
    }

    wrap(name: string, ...elementsArray: ElementForJQuery[]): jasmine.SpyObj<JQuery> {
        const filteredElementsArray = elementsArray.filter((element) => !!element);

        const elementWithMocks = filteredElementsArray.find(
            (element) => !!element.mockedMethodsOfJQueryWrapper
        );

        const mockedMethodsOfJQueryWrapperNames = !!elementWithMocks
            ? lo.uniq(
                lo
                    .cloneDeep(this.DEFAULT_MOCKED_JQUERY_FUNCTIONS)
                    .concat(Object.keys(elementWithMocks.mockedMethodsOfJQueryWrapper))
            )
            : this.DEFAULT_MOCKED_JQUERY_FUNCTIONS;

        const elementsWrapper = jasmine.createSpyObj<any>(
            name,
            this.IDEMPOTENT_METHODS.concat(mockedMethodsOfJQueryWrapperNames)
        );

        filteredElementsArray.forEach((element, index) => {
            const wrapper = elementsWrapper as any;
            wrapper[index] = element;
        });

        (elementsWrapper as any).length = filteredElementsArray.length;

        this.DEFAULT_MOCKED_JQUERY_FUNCTIONS.forEach(
            (methodName: Extract<keyof JQuery, string>) => {
                elementsWrapper[methodName].and.returnValue({});
            }
        );

        if (elementWithMocks) {
            lo.forEach(
                elementWithMocks.mockedMethodsOfJQueryWrapper,
                (value: Primitive, methodName: Extract<keyof JQuery, string>) => {
                    elementsWrapper[methodName].and.returnValue(value);
                }
            );
        }
        elementsWrapper.get.and.callFake((index: number) => {
            return elementsWrapper[0];
        });

        this.IDEMPOTENT_METHODS.forEach((methodName: Extract<keyof JQuery, string>) => {
            elementsWrapper[methodName].and.returnValue(elementsWrapper);
        });

        return elementsWrapper;
    }
}

export const jQueryHelper = new JQueryHelper();
