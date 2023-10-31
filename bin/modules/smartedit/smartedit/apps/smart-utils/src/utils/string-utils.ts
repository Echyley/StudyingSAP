/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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
export class StringUtils {
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
    isBlank(value: any): boolean {
        return (
            typeof value === 'undefined' ||
            value === null ||
            value === 'null' ||
            value.toString().trim().length === 0
        );
    }

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

    regExpFactory(pattern: string): RegExp {
        const onlyAlphanumericsRegex = new RegExp(/^[a-zA-Z\d]+$/i);
        const antRegex = new RegExp(/^[a-zA-Z\d\*]+$/i);

        let regexpKey;
        if (onlyAlphanumericsRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern);
        } else if (antRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern.replace(/\*/g, '.*'));
        } else {
            regexpKey = pattern;
        }

        return new RegExp(regexpKey, 'g');
    }

    /*
     * formats HTML outputs typically from Node.outerHTML to easy string comparison by:
     * - remove empty lines
     * - remove spaces between tags
     * - normalize remainign spaces to a single one
     *
     */
    formatHTML(rawHTML: string): string {
        return rawHTML
            .replace(/^\s*\n/gm, '')
            .replace(/\>[\t\s]+\</g, '><')
            .replace(/[\r\n\t\s]+/g, ' ');
    }

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
    sanitize = (str: string): string =>
        /* The correct solution for this is to use Negative Lookbehind Regex expression which is available as part of ES2018. // str.replace(/(?:(?<!\\)([()]))/g, '\\$1')
        But in order to support cross browser compatibility, the string is reversed and negative lookahead is used instead. */
        !this.isBlank(str)
            ? str
                  .split('')
                  .reverse()
                  .join('')
                  .replace(/(?:(([()])(?!\\)))/g, '$1\\')
                  .split('')
                  .reverse()
                  .join('')
            : str;

    /**
     * @ngdoc service
     * @name @smartutils.services:StringUtils#encode
     * @methodOf @smartutils.services:StringUtils
     *
     * @description
     * will return a encoded value for any JSON object passed as argument
     * @param {object} JSON object to be encoded
     */
    encode = (object: any): any =>
        /* first we use encodeURIComponent to get percent-encoded UTF-8,
         * then we convert the percent encodings into raw bytes which
         * can be fed into buf.toString('base64')
         * from https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
         */
        Buffer.from(
            encodeURIComponent(JSON.stringify(object)).replace(
                /%([0-9A-F]{2})/g,
                function toSolidBytes(match: any, p1: string) {
                    return String.fromCharCode(parseInt(p1, 16));
                }
            )
        ).toString('base64');

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
    replaceAll = (str: string, substitutionMap: TypedMap<string>): string => {
        const regex = new RegExp(Object.keys(substitutionMap).join('|'), 'g');
        return str.replace(regex, function (matched) {
            return substitutionMap[matched];
        });
    };
}

export const stringUtils = new StringUtils();
