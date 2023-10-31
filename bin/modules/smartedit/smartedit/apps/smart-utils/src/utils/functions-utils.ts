/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import * as lo from 'lodash';
import { TypedMap } from '../dtos';

/**
 * @ngdoc service
 * @name @smartutils.services:FunctionsUtils
 *
 * @description
 * utility service around Functions.
 */
export class FunctionsUtils {
    /*
     * regexp matching function(a, $b){} and function MyFunction(a, $b){}
     */
    private signatureArgsRegexp = /function[\s\w]*\(([\w\s\$,]*)\)[\s]*{/;

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#isEmpty
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Will determine whether a function body is empty or should be considered empty for proxying purposes
     *
     * @param {Function} func, the function to evaluate
     * @returns {Boolean} a boolean.
     */
    isEmpty(func: (...args: any[]) => any): boolean {
        const match = func.toString().match(/\{([\s\S]*)\}/m);
        return (
            !match ||
            match[1].trim() === '' ||
            /(proxyFunction)/g.test(func.toString().replace(/\s/g, ''))
        );
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#getArguments
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Returns the array of string arguments of the given function signature
     *
     * @param {Function} func the function to analyze
     * @returns {string[]} an array of string arguments
     */
    getArguments(func: (...args: any[]) => any): string[] {
        const exec = this.signatureArgsRegexp.exec(func.toString());
        if (exec) {
            return exec[1].replace(/\s/g, '').split(',');
        } else {
            throw new Error(`failed to retrieve arguments list of ${func}`);
        }
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#hasArguments
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Determines whether a given function (anonymous or not) has arguments in it signature
     *
     * @param {Function} func the function to analyze
     * @returns {boolean} true if the function has signature arguments
     */
    hasArguments(func: (...args: any[]) => any): boolean {
        const exec = this.signatureArgsRegexp.exec(func.toString());
        if (exec) {
            return !lo.isEmpty(exec[1]);
        } else {
            throw new Error(`failed to retrieve arguments list of ${func}`);
        }
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#getConstructorName
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion
     *
     * @param {Function} func the function to analyze
     * @returns {string} the constructor name
     */
    getConstructorName(func: new (...args: any[]) => any): string {
        let name = func.name;
        if (!name) {
            // IE does not support constructor.name
            const exec = /function (\$?\w+)\s*\(/.exec(func.toString());
            if (exec) {
                name = exec[1];
            } else {
                throw new Error('[FunctionsUtils] - Cannot get name from invalid constructor.');
            }
        }
        return name;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#getInstanceConstructorName
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion of a class instance
     *
     * @param {Object} instance instance class to analyze
     * @returns {string} the constructor name of the instance
     */
    // eslint-disable-next-line @typescript-eslint/ban-types
    getInstanceConstructorName(instance: object): string {
        return this.getConstructorName(Object.getPrototypeOf(instance).constructor);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:FunctionsUtils#extendsConstructor
     * @methodOf @smartutils.services:FunctionsUtils
     *
     * @description
     * Overrides a given constructor with a new constructor body. The resulting constructor will share the same prototype as the original one.
     *
     * @param {(...args:any[]) => T} originalConstructor the original constructor to override
     * @returns {(...args:any[]) => T} newConstructorBody the new constructor body to execute in the override. It may or may not return an instance. Should it return an instance, the latter will be returned by the override.
     */
    extendsConstructor<T>(
        originalConstructor: (...args: any[]) => T,
        newConstructorBody: (...args: any[]) => T
    ): any {
        // the new constructor behaviour
        const newConstructor: any = function (...args: any[]) {
            return new (newConstructorBody as any)(...args);
        };
        // copy prototype so intanceof operator still works
        // and it allows to test annotation (by using getMethodAnnotation in annotation.service)
        newConstructor.prototype = originalConstructor.prototype;

        // copying static methods
        Object.entries(Object.getOwnPropertyDescriptors(originalConstructor))
            .filter(([, descriptor]) => descriptor.writable)
            .forEach(([propName]) => {
                newConstructor[propName] = originalConstructor[propName];
            });

        return newConstructor;
    }

    // TODO see if can be refactored
    getInstanceMethods(obj: any): string[] {
        const isGetter = (_obj: any, prop: any): boolean => !!_obj.__lookupGetter__(prop);

        let keys: string[] = [];
        const topObject: any = obj;

        const onlyOriginalMethods = (p: any, i: number, arr: string[]): boolean =>
            !isGetter(topObject, p) &&
            typeof topObject[p] === 'function' &&
            p !== 'constructor' &&
            (i === 0 || p !== arr[i - 1]) &&
            keys.indexOf(p) === -1;

        do {
            const list = Object.getOwnPropertyNames(obj).sort().filter(onlyOriginalMethods);
            keys = keys.concat(list);

            // walk-up the prototype chain
            obj = Object.getPrototypeOf(obj);
        } while (
            // not the the Object prototype methods (hasOwnProperty, etc...)
            obj &&
            Object.getPrototypeOf(obj)
        );

        return keys;
    }

    /** @internal */
    isUnitTestMode(): boolean {
        /* forbiddenNameSpaces window._:false */
        return typeof window.__karma__ !== 'undefined';
    }

    /**
     * The helper for tests which use try / catch block.
     * When `try` block contains only `await` and `catch` block contains expects then,
     * when someone changes method and and `catch` block is never entered it should fail
     */
    assertFail(): void {
        const spy = jasmine.createSpy('TestShouldNotReachThatPart-CheckYourTryCatchBlock');
        expect(spy).toHaveBeenCalled();
    }

    convertToArray<T = string>(obj: TypedMap<T>): { key: string; value: T }[] {
        return Object.keys(obj).reduce(
            (acc: { key: string; value: T }[], key: string) => [
                ...(acc || []),
                { key, value: obj[key] }
            ],
            []
        );
    }
}

export const functionsUtils = new FunctionsUtils();
