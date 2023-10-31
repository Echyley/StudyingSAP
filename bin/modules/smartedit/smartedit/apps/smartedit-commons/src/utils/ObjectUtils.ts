/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable id-blacklist */
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lodash from 'lodash';
import { SeFactory } from '../di';
import { TypedMap } from '../dtos';

/**
 * Provides a list of useful methods used for object manipulation
 */
class ObjectUtils {
    /**
     * Creates a deep copy of the given input object.
     * If an object being stringified has a property named toJSON whose value is a function, then the toJSON() method customizes JSON stringification behavior: instead of the object being serialized, the value returned by the toJSON() method when called will be serialized.
     *
     * @param candidate the javaScript value that needs to be deep copied.
     *
     * @returns A deep copy of the input
     */
    copy<T>(candidate: T): T {
        return JSON.parse(JSON.stringify(candidate));
    }

    /**
     * Will check if the object is empty and will return true if each and every property of the object is empty.
     *
     * @param value the value to evaluate
     */
    isObjectEmptyDeep = (value: any): boolean => {
        if (lodash.isObject(value) as any) {
            for (const key in value) {
                if (value.hasOwnProperty(key)) {
                    if (!lodash.isEmpty(value[key])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return lodash.isString(value) ? lodash.isEmpty(value) : lodash.isNil(value);
    };

    /**
     * Resets a given object's properties' values
     *
     * @param targetObject, the object to reset
     * @param modelObject, an object that contains the structure that targetObject should have after a reset
     * @returns The object that has been reset
     */
    resetObject = (targetObject: any, modelObject: any): any => {
        if (!targetObject) {
            targetObject = this.copy(modelObject);
        } else {
            for (const i in targetObject) {
                if (targetObject.hasOwnProperty(i)) {
                    delete targetObject[i];
                }
            }
            lodash.extend(targetObject, this.copy(modelObject));
        }

        return targetObject;
    };

    /**
     * Merges the contents of two objects together into the first object.
     *
     * **Note:** This method mutates `object`.
     *
     * @returns A new object as a result of merge
     */
    merge<TTarget, TSource>(target: TTarget, source: TSource): TTarget & TSource {
        return Object.assign(target, source);
    }

    /**
     * Iterates over object and allows to modify a value using callback function.
     * @param callback Callback function to apply to each object value.
     * @returns The object with modified values.
     */
    deepIterateOverObjectWith = (obj: any, callback: any): any =>
        lodash.reduce(
            obj,
            (result: any, value: any, key: any) => {
                if (lodash.isPlainObject(value)) {
                    result[key] = this.deepIterateOverObjectWith(value, callback);
                } else {
                    result[key] = callback(value);
                }
                return result;
            },
            {}
        );

    /**
     * Returns an object that contains list of fields and for each field it has a boolean value
     * which is true when the property was modified, added or removed, false otherwise.
     * @returns The diff object.
     */
    deepObjectPropertyDiff = (firstObject: any, secondObject: any): any => {
        const CHANGED_PROPERTY = 'CHANGED_PROPERTY';
        const NON_CHANGED_PROPERTY = 'NON_CHANGED_PROPERTY';

        const mergedObj = lodash.mergeWith(
            lodash.cloneDeep(firstObject),
            secondObject,
            function (prValue: any, cpValue: any) {
                if (!lodash.isPlainObject(prValue)) {
                    return !lodash.isEqual(prValue, cpValue)
                        ? CHANGED_PROPERTY
                        : NON_CHANGED_PROPERTY;
                }

                // Note: Previous versions of lodash could work with null, but the latest version of lodash requires
                // undefined to be returned.
                return undefined;
            }
        );

        // If the field is not CHANGED_PROPERTY/NON_CHANGED_PROPERTY then it was removed or added.
        const sanitizedObj = this.deepIterateOverObjectWith(mergedObj, (value: any) => {
            if (value !== CHANGED_PROPERTY && value !== NON_CHANGED_PROPERTY) {
                return CHANGED_PROPERTY;
            } else {
                return value;
            }
        });

        // If it's CHANGED_PROPERTY return true otherwise false.
        return this.deepIterateOverObjectWith(sanitizedObj, (value: any) =>
            value === CHANGED_PROPERTY ? true : false
        );
    };

    /**
     * Converts the given object to array.
     * The output array elements are an object that has a key and value,
     * where key is the original key and value is the original object.
     */
    convertToArray(object: any): any[] {
        const configuration = [];
        for (const key in object) {
            if (!key.startsWith('$') && !key.startsWith('toJSON')) {
                configuration.push({
                    key,
                    value: object[key]
                });
            }
        }
        return configuration;
    }

    /**
     * Returns the first Array argument supplemented with new entries from the second Array argument.
     *
     * **Note:** This method mutates `array1`.
     */
    uniqueArray(array1: any[], array2: any[]): any[] {
        const set = new Set(array1);
        array2.forEach((instance: any) => {
            if (!set.has(instance)) {
                array1.push(instance);
            }
        });

        return array1;
    }

    /**
     * Checks if `value` is a function.
     */
    isFunction(value: any): boolean {
        return typeof value === 'function';
    }

    /**
     * Checks if the value is the ECMAScript language type of Object
     */
    isObject(value: any): boolean {
        const objectTypes = {
            boolean: false,
            function: true,
            object: true,
            number: false,
            string: false,
            undefined: false
        } as any;

        return !!(value && objectTypes[typeof value]);
    }

    isTypedMap<T = string>(value: any): value is TypedMap<T> {
        return value && this.isObject(value) && value.constructor === Object;
    }

    readObjectStructure = (json: any, recursiveCount = 0): any => {
        if (recursiveCount > 25) {
            return this.getClassName(json);
        }

        if (json === undefined || json === null || json.then) {
            return json;
        }

        switch (typeof json) {
            case 'function':
                return 'FUNCTION';
            case 'string':
                return 'STRING';
            case 'number':
                return 'NUMBER';
            case 'boolean':
                return 'BOOLEAN';
            default:
                return this._getOtherObjectType(json, recursiveCount);
        }
    };

    /**
     * Sorts an array of strings or objects in specified order.
     * String of numbers are treated the same way as numbers.
     * For an array of objects, `prop` argument is required.
     *
     * @param array Array to sort
     * @param prop Property on which comparision is based. Required for an array of objects.
     * @param reverse Specify ascending or descending order
     *
     * @returns The new sorted array
     */
    sortBy<T>(array: T[], prop?: string, reverse = false): T[] {
        const targetArray: T[] = [...array];

        const descending = reverse ? -1 : 1;
        targetArray.sort((a, b) => {
            const aVal = this.isTypedMap(a) ? a[prop] : a;
            const bVal = this.isTypedMap(b) ? b[prop] : b;
            const result = String(aVal).localeCompare(String(bVal), undefined, {
                numeric: true,
                sensitivity: 'base'
            });
            return result * descending;
        });

        return targetArray;
    }

    /**
     * Provides a convenience to either default a new child or "extend" an existing child with the prototype of the parent
     *
     * @param ParentClass which has a prototype you wish to extend.
     * @param ChildClass will have its prototype set.
     *
     * @returns ChildClass which has been extended
     */
    extend(ParentClass: SeFactory, ChildClass?: SeFactory): any {
        if (!ChildClass) {
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            ChildClass = function () {
                return;
            };
        }
        ChildClass.prototype = Object.create(ParentClass.prototype);
        return ChildClass;
    }

    /** @internal */
    private getClassName(instance: any): string | null {
        return instance &&
            instance.constructor &&
            instance.constructor.name &&
            instance.constructor.name !== 'Object'
            ? instance.constructor.name
            : null;
    }

    private _getOtherObjectType(json: any, recursiveCount: number): string {
        if (lodash.isElement(json)) {
            return 'ELEMENT';
        }

        if (json.hasOwnProperty && json.hasOwnProperty('length')) {
            // jquery or Array
            if (json.forEach) {
                const arr: any = [];
                json.forEach((arrayElement: any) => {
                    recursiveCount++;
                    arr.push(this.readObjectStructure(arrayElement, recursiveCount));
                });
                return arr;
            } else {
                return 'JQUERY';
            }
        }

        if (json.constructor && json.constructor.name && json.constructor.name !== 'Object') {
            return json.constructor.name;
        }

        // JSON
        const clone = {} as any;
        Object.keys(json).forEach((directKey) => {
            if (!directKey.startsWith('$')) {
                recursiveCount++;
                clone[directKey] = this.readObjectStructure(json[directKey], recursiveCount);
            }
        });
        return clone;
    }
}

const objectUtils = new ObjectUtils();

export { objectUtils, ObjectUtils };
