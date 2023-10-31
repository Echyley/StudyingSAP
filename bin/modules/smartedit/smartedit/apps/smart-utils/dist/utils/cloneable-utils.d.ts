import { Cloneable } from '../dtos';
/**
 * @ngdoc service
 * @name @smartutils.services:CloneableUtils
 *
 * @description
 * utility service around Cloneable objects
 */
export declare class CloneableUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:CloneableUtils#makeCloneable
     * @methodOf @smartutils.services:CloneableUtils
     * @description
     * returns a "cloneable" version of an object.
     * Something is cloneable when it can be sent through W3C postMessage.
     * To this purpose, functions must be removed from the cloneable candidate.
     * @param {Object} json the object to be made cloneable
     * @returns {Cloneable} the cloneable copy of the object
     */
    makeCloneable(_json: any): Cloneable;
    private _isValueCloneable;
    private _getCloneableValue;
    private isPrimitive;
}
export declare const cloneableUtils: CloneableUtils;
