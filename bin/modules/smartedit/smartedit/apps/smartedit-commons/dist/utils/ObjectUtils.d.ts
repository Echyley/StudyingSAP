import { SeFactory } from '../di';
import { TypedMap } from '../dtos';
/**
 * Provides a list of useful methods used for object manipulation
 */
declare class ObjectUtils {
    /**
     * Creates a deep copy of the given input object.
     * If an object being stringified has a property named toJSON whose value is a function, then the toJSON() method customizes JSON stringification behavior: instead of the object being serialized, the value returned by the toJSON() method when called will be serialized.
     *
     * @param candidate the javaScript value that needs to be deep copied.
     *
     * @returns A deep copy of the input
     */
    copy<T>(candidate: T): T;
    /**
     * Will check if the object is empty and will return true if each and every property of the object is empty.
     *
     * @param value the value to evaluate
     */
    isObjectEmptyDeep: (value: any) => boolean;
    /**
     * Resets a given object's properties' values
     *
     * @param targetObject, the object to reset
     * @param modelObject, an object that contains the structure that targetObject should have after a reset
     * @returns The object that has been reset
     */
    resetObject: (targetObject: any, modelObject: any) => any;
    /**
     * Merges the contents of two objects together into the first object.
     *
     * **Note:** This method mutates `object`.
     *
     * @returns A new object as a result of merge
     */
    merge<TTarget, TSource>(target: TTarget, source: TSource): TTarget & TSource;
    /**
     * Iterates over object and allows to modify a value using callback function.
     * @param callback Callback function to apply to each object value.
     * @returns The object with modified values.
     */
    deepIterateOverObjectWith: (obj: any, callback: any) => any;
    /**
     * Returns an object that contains list of fields and for each field it has a boolean value
     * which is true when the property was modified, added or removed, false otherwise.
     * @returns The diff object.
     */
    deepObjectPropertyDiff: (firstObject: any, secondObject: any) => any;
    /**
     * Converts the given object to array.
     * The output array elements are an object that has a key and value,
     * where key is the original key and value is the original object.
     */
    convertToArray(object: any): any[];
    /**
     * Returns the first Array argument supplemented with new entries from the second Array argument.
     *
     * **Note:** This method mutates `array1`.
     */
    uniqueArray(array1: any[], array2: any[]): any[];
    /**
     * Checks if `value` is a function.
     */
    isFunction(value: any): boolean;
    /**
     * Checks if the value is the ECMAScript language type of Object
     */
    isObject(value: any): boolean;
    isTypedMap<T = string>(value: any): value is TypedMap<T>;
    readObjectStructure: (json: any, recursiveCount?: number) => any;
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
    sortBy<T>(array: T[], prop?: string, reverse?: boolean): T[];
    /**
     * Provides a convenience to either default a new child or "extend" an existing child with the prototype of the parent
     *
     * @param ParentClass which has a prototype you wish to extend.
     * @param ChildClass will have its prototype set.
     *
     * @returns ChildClass which has been extended
     */
    extend(ParentClass: SeFactory, ChildClass?: SeFactory): any;
    /** @internal */
    private getClassName;
    private _getOtherObjectType;
}
declare const objectUtils: ObjectUtils;
export { objectUtils, ObjectUtils };
