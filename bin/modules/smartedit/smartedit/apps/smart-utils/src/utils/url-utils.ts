/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import * as lodash from 'lodash';
import { TypedMap } from '../dtos';
import { stringUtils } from './string-utils';

/* tslint:disable:max-classes-per-file */

/**
 * @ngdoc service
 * @name @smartutils.services:UrlUtils#URIBuilder
 *
 * @description
 * builder or URIs, build() method must be invoked to actually retrieve a URI
 *
 * @param {Object} modalStack, the $modalStack service of angular-ui.
 */

export class URIBuilder {
    private readonly wholeWordMatch = '[\\w]+';

    constructor(private uri: string) {}

    build(): string {
        return this.uri;
    }

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
    replaceParams(params: TypedMap<string>): URIBuilder {
        const clone = lodash.cloneDeep(this);
        if (params) {
            // order the keys by descending length
            clone.uri = Object.keys(params)
                .sort(function (a, b) {
                    return b.length - a.length;
                })
                .reduce(
                    (tempURL: string, key: string) =>
                        this.substituteKeyForValue(tempURL, key, params[key]),
                    clone.uri
                );
        }
        return clone;
    }

    /**
     * @ngdoc method
     * @name  @smartutils.services:UrlUtils#URIBuilder#sanitize
     * @methodOf  @smartutils.services:UrlUtils#URIBuilder
     *
     * @description
     * removes unresolved ":" prefixed placeholders from absolute path
     */
    sanitize(): URIBuilder {
        const clone = lodash.cloneDeep(this);
        const uriDomainAndPath: string[] | null = /(https?:\/\/[^\/]*)(\/.*)/.exec(clone.uri);
        clone.uri = uriDomainAndPath == null ? clone.uri : uriDomainAndPath[2];
        clone.uri = this.substituteKeyForValue(clone.uri, this.wholeWordMatch, '')
            .replace(/\/\//, '/') // to replace double slash (api/:identifier/data?param=true) if :identifier is removed
            .replace(/\/\?/, '?') // to replace slash question mark (api/:identifier?param=true) if :identifier is removed
            .replace(/\/$/, ''); // to remove trailing slash

        clone.uri = uriDomainAndPath == null ? clone.uri : uriDomainAndPath[1] + clone.uri;
        return clone;
    }

    private substituteKeyForValue(url: string, key: string, value: string): string {
        url = url
            .replace(new RegExp(':' + key + '/'), `${value || ''}/`)
            .replace(new RegExp(':' + key + '$'), `${value || ''}`)
            .replace(new RegExp(':' + key + '\\?'), `${value || ''}?`)
            .replace(new RegExp(':' + key + '&'), `${value || ''}&`);

        /*
         * to cater for special case of smartedit
         * where some non ":" prefixed placeholders must be resolved too
         * we limit it though to keys containing "_" (case for smartedit)
         * since it would otherwise breaks most APIs patterns
         */
        if (key !== this.wholeWordMatch && key.includes('_')) {
            const _uri = url.includes('?') ? url.substr(0, url.indexOf('?')) : url;
            const uri = _uri.replace(new RegExp('\\b' + key + '\\b'), `${value || ''}`);
            url = url.includes('?') ? uri + url.substr(url.indexOf('?')) : uri;
        }
        return url;
    }
}
/**
 * @ngdoc service
 * @name @smartutils.services:UrlUtils
 *
 * @description
 * A collection of utility methods for manipulating URLs
 */
export class UrlUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getOrigin
     * @methodOf @smartutils.services:UrlUtils
     * @description
     * returns document location origin
     * Some browsers still do not support W3C document.location.origin, this function caters for gap.
     * @param {String =} url optional any url
     */
    getOrigin(url?: string): string {
        if (!!url) {
            let link = document.createElement('a');
            link.setAttribute('href', url);
            const origin =
                link.protocol + '//' + link.hostname + (link.port ? ':' + link.port : '');
            // @ts-ignore
            link = null; // GC
            return origin;
        }

        return (
            window.location.protocol +
            '//' +
            window.location.hostname +
            (window.location.port ? ':' + window.location.port : '')
        );
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getURI
     * @methodOf @smartutils.services:UrlUtils
     *
     * @description
     * Will return the URI part of a URL
     * @param {String} url the URL the URI of which is to be returned
     */
    getURI(url: string): string {
        return url && url.indexOf('?') > -1 ? url.split('?')[0] : url;
    }

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
    updateUrlParameter(url: string, key: string, value: string): string {
        const i = url.indexOf('#');
        const hash = i === -1 ? '' : url.substr(i);
        url = i === -1 ? url : url.substr(0, i);
        const regex = new RegExp('([?&])' + key + '=.*?(&|$)', 'i');
        const separator = url.indexOf('?') !== -1 ? '&' : '?';

        if (url.match(regex)) {
            url = url.replace(regex, '$1' + key + '=' + value + '$2');
        } else {
            url = url + separator + key + '=' + value;
        }
        return url + hash;
    }

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
    getQueryString(params: any): string {
        let queryString = '';
        if (params) {
            for (const param in params) {
                if (params.hasOwnProperty(param)) {
                    (lodash.isArray(params[param]) ? params[param] : [params[param]]).forEach(
                        (value: any) => {
                            queryString +=
                                '&' + encodeURIComponent(param) + '=' + encodeURIComponent(value);
                        }
                    );
                }
            }
        }
        return '?' + (!stringUtils.isBlank(queryString) ? queryString.substring(1) : queryString);
    }

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
    parseQuery(str: any): JSON {
        const objURL = {} as any;

        str.replace(/([^?=&]+)(=([^&]*))?/g, function ($0: any, $1: any, $2: any, $3: any) {
            objURL[$1] = $3;
        });
        return objURL;
    }

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
    getAbsoluteURL(domain: string, url: string): string {
        // url regex
        // scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
        const re = new RegExp(
            '([a-zA-Z0-9]+://)' + // scheme
                '([a-zA-Z0-9_]+:[a-zA-Z0-9_]+@)?' + // user:password
                '([a-zA-Z0-9.-]+)' + // hostname
                '|([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)' + // or ip
                '(:[0-9]+)?' + // port
                '(/.*)?' // everything else
        );

        return re.exec(url) ? url : domain + url;
    }
}

export const urlUtils = new UrlUtils();
