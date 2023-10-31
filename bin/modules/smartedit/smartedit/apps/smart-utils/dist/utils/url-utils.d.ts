import { TypedMap } from '../dtos';
/**
 * @ngdoc service
 * @name @smartutils.services:UrlUtils#URIBuilder
 *
 * @description
 * builder or URIs, build() method must be invoked to actually retrieve a URI
 *
 * @param {Object} modalStack, the $modalStack service of angular-ui.
 */
export declare class URIBuilder {
    private uri;
    private readonly wholeWordMatch;
    constructor(uri: string);
    build(): string;
    /**
     * @ngdoc method
     * @name  @smartutils.services:UrlUtils#URIBuilder#replaceParams
     * @methodOf  @smartutils.services:UrlUtils#URIBuilder
     *
     * @description
     * Substitute all ":" prefixed placeholders in the full url with the matching values in the given params
     * Substitute Non ":" prefixed placeholders containg "_"
     *
     * @param {Object} params a map of placeholder names / values
     */
    replaceParams(params: TypedMap<string>): URIBuilder;
    /**
     * @ngdoc method
     * @name  @smartutils.services:UrlUtils#URIBuilder#sanitize
     * @methodOf  @smartutils.services:UrlUtils#URIBuilder
     *
     * @description
     * removes unresolved ":" prefixed placeholders from absolute path
     */
    sanitize(): URIBuilder;
    private substituteKeyForValue;
}
/**
 * @ngdoc service
 * @name @smartutils.services:UrlUtils
 *
 * @description
 * A collection of utility methods for manipulating URLs
 */
export declare class UrlUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getOrigin
     * @methodOf @smartutils.services:UrlUtils
     * @description
     * returns document location origin
     * Some browsers still do not support W3C document.location.origin, this function caters for gap.
     * @param {String =} url optional any url
     */
    getOrigin(url?: string): string;
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getURI
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * Will return the URI part of a URL
     * @param {String} url the URL the URI of which is to be returned
     */
    getURI(url: string): string;
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#updateUrlParameter
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * Updates a URL to contain the query param and value provided. If already exists then it is updated,
     * if it did not previously exist, then it will be added.
     *
     * @param {String} url The url to be updated (this param will not be modified)
     * @param {String} key The query param key
     * @param {String} value The query param value
     *
     * @returns {String} The url with updated key/value
     */
    updateUrlParameter(url: string, key: string, value: string): string;
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getQueryString
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * <b>getQueryString</b> will convert a given object into a query string.
     *
     * Below is the code snippet for sample input and sample output:
     *
     * <pre>
     * var params = {
     *  key1 : 'value1',
     *  key2 : 'value2',
     *  key3 : 'value3'
     *  }
     *
     *  var output = getQueryString(params);
     *
     *  // The output is '?&key1=value1&key2=value2&key3=value3'
     *
     * </pre>
     * @param {Object} params Object containing a list of params.
     *
     * @returns {String} a query string
     */
    getQueryString(params: any): string;
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#parseQuery
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * <b>parseQuery</b> will convert a given query string to an object.
     *
     * Below is the code snippet for sample input and sample output:
     *
     * <pre>
     * var query = '?key1=value1&key2=value2&key3=value3';
     *
     * var output = parseQuery(query);
     *
     * // The output is { key1 : 'value1', key2 : 'value2', key3 : 'value3' }
     *
     * </pre>
     * @param {String} query String that needs to be parsed.
     *
     * @returns {Object} an object containing all params of the given query
     */
    parseQuery(str: any): JSON;
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getAbsoluteURL
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * Makes url absolute (with provided domain) if not yet
     *
     * @param {String} domain the domain with witch to prepend the url if it is not absolute
     * @param {String} url the url to test
     *
     * @returns {String} url
     */
    getAbsoluteURL(domain: string, url: string): string;
}
export declare const urlUtils: UrlUtils;
