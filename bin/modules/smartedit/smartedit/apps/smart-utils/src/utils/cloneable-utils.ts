/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import * as lodash from 'lodash';
import { Cloneable, Payload } from '../dtos';

/**
 * @ngdoc service
 * @name @smartutils.services:CloneableUtils
 *
 * @description
 * utility service around Cloneable objects
 */
export class CloneableUtils {
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
    makeCloneable(_json: any): Cloneable {
        const json = lodash.cloneDeepWith(_json, (value: any) => {
            if (this._isValueCloneable(value, _json)) {
                // if the value could be cloned
                return this._getCloneableValue(value);
            }
            return value;
        });

        if (json === undefined || json === null || this.isPrimitive(json)) {
            return json;
        }

        if (json.hasOwnProperty('length') || json.forEach) {
            // Array, already taken care of by yjQuery
            return json.map((arrayElement: any) => this.makeCloneable(arrayElement)) as Cloneable;
        }

        // JSON
        return Object.keys(json).reduce((clone, directKey) => {
            if (directKey.indexOf('$') !== 0) {
                clone[directKey] = this.makeCloneable(json[directKey]);
            }
            return clone;
        }, {} as Payload);
    }

    private _isValueCloneable(value: any, _json: any): boolean {
        return value !== undefined && value !== null && !this.isPrimitive(_json);
    }

    private _getCloneableValue(value: any): any {
        if (typeof value === 'function') {
            return null;
        }

        if (value.then) {
            // is a promise
            return null;
        }

        if (lodash.isElement(value)) {
            // is yjQuery
            return null;
        }

        if (typeof value !== 'string' && value.hasOwnProperty('length') && !value.forEach) {
            return null;
        }

        return value;
    }

    private isPrimitive(value: any): boolean {
        return typeof value === 'number' || typeof value === 'string' || typeof value === 'boolean';
    }
}
export const cloneableUtils = new CloneableUtils();
