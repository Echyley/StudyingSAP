/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { TypedMap } from '../dtos';
/**
 * @ngdoc service
 * @name @smartutils.services:StringUtils
 *
 * @description
 * utility service around Strings.
 */
export declare class StringUtils {
    /**
     * @ngdoc service
     * @name @smartutils.services:StringUtils#isBlank
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * <b>isBlank</b> will check if a given string is undefined or null or empty.
     * - returns TRUE for undefined / null/ empty string
     * - returns FALSE otherwise
     *
     * @param {String} inputString any input string.
     *
     * @returns {boolean} true if the string is null else false
     */
    isBlank(value: any): boolean;
    /**
     * @ngdoc method
     * @name @smartutils.services:StringUtils#regExpFactory
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * <b>regExpFactory</b> will convert a given pattern into a regular expression.
     * This method will prepend and append a string with ^ and $ respectively replaces
     * and wildcards (*) by proper regex wildcards.
     *
     * @param {String} pattern any string that needs to be converted to a regular expression.
     *
     * @returns {RegExp} a regular expression generated from the given string.
     *
     */
    regExpFactory(pattern: string): RegExp;
    formatHTML(rawHTML: string): string;
    /**
     * @ngdoc service
     * @name @smartutils.services:StringUtils#sanitize
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * <b>escapes any harmful scripting from a string, leaves innocuous HTML untouched/b>
     * @param {String} a string that needs to be sanitized.
     *
     * @returns {String} the sanitized string.
     *
     */
    sanitize: (str: string) => string;
    /**
     * @ngdoc service
     * @name @smartutils.services:StringUtils#encode
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * will return a encoded value for any JSON object passed as argument
     * @param {object} JSON object to be encoded
     */
    encode: (object: any) => any;
    /**
     * @ngdoc service
     * @name @smartutils.services:StringUtils#replaceAll
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * will return a string where all matches for the string regexps keys passed in the substitutionMap will have been substituted by correspoing values in the substitutionMap
     * @param {string} string the string to substitute keys in object to be encoded
     * @param {TypedMap<string>} substitutionMap the map of string regexp to string substitution values
     * @returns {string} the substituted string
     */
    replaceAll: (str: string, substitutionMap: TypedMap<string>) => string;
}
export declare const stringUtils: StringUtils;
