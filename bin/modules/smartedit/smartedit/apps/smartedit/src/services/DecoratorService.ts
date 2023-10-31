/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import {
    IDecoratorDisplayCondition,
    IDecoratorService,
    PromiseUtils,
    StringUtils,
    TypedMap
} from 'smarteditcommons';

export interface DecoratorMapping {
    [index: string]: string[];
}
/**
 * This service enables and disables decorators. It also maps decorators to SmartEdit component types–regardless if they are enabled or disabled.
 *
 */
@Injectable()
export class DecoratorService implements IDecoratorService {
    private _activeDecorators: TypedMap<{ displayCondition?: IDecoratorDisplayCondition }> = {};
    private componentDecoratorsMap: TypedMap<string[]> = {};

    constructor(
        private readonly promiseUtils: PromiseUtils,
        private readonly stringUtils: StringUtils
    ) {}

    /**
     * This method enables a list of decorators for a group of component types.
     * The list to be [enable]{@link DecoratorService#enable} is identified by a matching pattern.
     * The list is enabled when a perspective or referenced perspective that it is bound to is activated/enabled.
     *
     *
     *
     *      decoratorService.addMappings({
     *          '*Suffix': ['decorator1', 'decorator2'],
     *          '.*Suffix': ['decorator2', 'decorator3'],
     *          'MyExactType': ['decorator3', 'decorator4'],
     *          '^((?!Middle).)*$': ['decorator4', 'decorator5']
     *      });
     *
     *
     * @param  map A key-map value; the key is the matching pattern and the value is an array of decorator keys. The key can be an exact type, an ant-like wild card, or a full regular expression:
     */
    addMappings(map: DecoratorMapping): void {
        for (const regexpKey in map) {
            if (map.hasOwnProperty(regexpKey)) {
                const decoratorsArray = map[regexpKey];
                this.componentDecoratorsMap[regexpKey] = lodash.union(
                    this.componentDecoratorsMap[regexpKey] || [],
                    decoratorsArray
                );
            }
        }
    }
    /**
     * Enables a decorator
     *
     * @param decoratorKey The key that uniquely identifies the decorator.
     * @param displayCondition Returns a promise that will resolve to a boolean that determines whether the decorator will be displayed.
     */
    enable(decoratorKey: string, displayCondition?: IDecoratorDisplayCondition): void {
        if (!(decoratorKey in this._activeDecorators)) {
            this._activeDecorators[decoratorKey] = {
                displayCondition
            };
        }
    }
    /**
     * Disables a decorator
     *
     * @param decoratorKey the decorator key
     */
    disable(decoratorKey: string): void {
        if (this._activeDecorators[decoratorKey]) {
            delete this._activeDecorators[decoratorKey];
        }
    }
    /**
     * This method retrieves a list of decorator keys that is eligible for the specified component type.
     * The list retrieved depends on which perspective is active.
     *
     * This method uses the list of decorators enabled by the [addMappings]{@link DecoratorService#addMappings} method.
     *
     * @param componentType The type of the component to be decorated.
     * @param componentId The id of the component to be decorated.
     * @returns A promise that resolves to a list of decorator keys.
     *
     */
    getDecoratorsForComponent(componentType: string, componentId?: string): Promise<string[]> {
        let decoratorArray: string[] = [];
        if (this.componentDecoratorsMap) {
            for (const regexpKey in this.componentDecoratorsMap) {
                if (this.stringUtils.regExpFactory(regexpKey).test(componentType)) {
                    decoratorArray = lodash.union(
                        decoratorArray,
                        this.componentDecoratorsMap[regexpKey]
                    );
                }
            }
        }
        return this.getDecoratorPromise(decoratorArray, componentType, componentId);
    }

    private getDecoratorPromise(
        decoratorArray: string[],
        componentType: string,
        componentId?: string
    ): Promise<string[]> {
        const promisesToResolve: Promise<string>[] = [];
        const displayedDecorators: string[] = [];
        decoratorArray.forEach((dec: string) => {
            const activeDecorator = this._activeDecorators[dec];
            if (activeDecorator && activeDecorator.displayCondition) {
                if (typeof activeDecorator.displayCondition !== 'function') {
                    throw new Error(
                        "The active decorator's displayCondition property must be a function and must return a boolean"
                    );
                }
                const deferred = this.promiseUtils.defer<string>();
                activeDecorator
                    .displayCondition(componentType, componentId)
                    .then((display: boolean) => {
                        if (display) {
                            deferred.resolve(dec);
                        } else {
                            deferred.resolve(null);
                        }
                    });
                promisesToResolve.push(deferred.promise);
            } else if (activeDecorator) {
                displayedDecorators.push(dec);
            }
        });
        return Promise.all(promisesToResolve).then((decoratorsEnabled) =>
            displayedDecorators.concat(decoratorsEnabled.filter((dec) => dec))
        );
    }
}
