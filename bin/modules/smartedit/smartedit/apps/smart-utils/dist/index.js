'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var lodash = require('lodash');
var CryptoJS = require('crypto-js');
var core = require('@angular/core');
var http = require('@angular/common/http');
var rxjs = require('rxjs');
var operators = require('rxjs/operators');
var core$1 = require('@ngx-translate/core');
var forms = require('@angular/forms');
var core$2 = require('@fundamental-ngx/core');
var common = require('@angular/common');
var list = require('@fundamental-ngx/core/list');
var dialog = require('@fundamental-ngx/core/dialog');

function _interopNamespace(e) {
    if (e && e.__esModule) return e;
    var n = Object.create(null);
    if (e) {
        Object.keys(e).forEach(function (k) {
            if (k !== 'default') {
                var d = Object.getOwnPropertyDescriptor(e, k);
                Object.defineProperty(n, k, d.get ? d : {
                    enumerable: true,
                    get: function () { return e[k]; }
                });
            }
        });
    }
    n["default"] = e;
    return Object.freeze(n);
}

var lodash__namespace = /*#__PURE__*/_interopNamespace(lodash);
var CryptoJS__namespace = /*#__PURE__*/_interopNamespace(CryptoJS);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc service
 * @name @smartutils.services:CloneableUtils
 *
 * @description
 * utility service around Cloneable objects
 */
class CloneableUtils {
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
    makeCloneable(_json) {
        const json = lodash__namespace.cloneDeepWith(_json, (value) => {
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
            return json.map((arrayElement) => this.makeCloneable(arrayElement));
        }
        // JSON
        return Object.keys(json).reduce((clone, directKey) => {
            if (directKey.indexOf('$') !== 0) {
                clone[directKey] = this.makeCloneable(json[directKey]);
            }
            return clone;
        }, {});
    }
    _isValueCloneable(value, _json) {
        return value !== undefined && value !== null && !this.isPrimitive(_json);
    }
    _getCloneableValue(value) {
        if (typeof value === 'function') {
            return null;
        }
        if (value.then) {
            // is a promise
            return null;
        }
        if (lodash__namespace.isElement(value)) {
            // is yjQuery
            return null;
        }
        if (typeof value !== 'string' && value.hasOwnProperty('length') && !value.forEach) {
            return null;
        }
        return value;
    }
    isPrimitive(value) {
        return typeof value === 'number' || typeof value === 'string' || typeof value === 'boolean';
    }
}
const cloneableUtils = new CloneableUtils();

/**
 * @ngdoc service
 * @name @smartutils.services:StringUtils
 *
 * @description
 * utility service around Strings.
 */
class StringUtils {
    constructor() {
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
        this.sanitize = (str) => 
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
        this.encode = (object) => 
        /* first we use encodeURIComponent to get percent-encoded UTF-8,
         * then we convert the percent encodings into raw bytes which
         * can be fed into buf.toString('base64')
         * from https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
         */
        Buffer.from(encodeURIComponent(JSON.stringify(object)).replace(/%([0-9A-F]{2})/g, function toSolidBytes(match, p1) {
            return String.fromCharCode(parseInt(p1, 16));
        })).toString('base64');
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
        this.replaceAll = (str, substitutionMap) => {
            const regex = new RegExp(Object.keys(substitutionMap).join('|'), 'g');
            return str.replace(regex, function (matched) {
                return substitutionMap[matched];
            });
        };
    }
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
    isBlank(value) {
        return (typeof value === 'undefined' ||
            value === null ||
            value === 'null' ||
            value.toString().trim().length === 0);
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
    regExpFactory(pattern) {
        const onlyAlphanumericsRegex = new RegExp(/^[a-zA-Z\d]+$/i);
        const antRegex = new RegExp(/^[a-zA-Z\d\*]+$/i);
        let regexpKey;
        if (onlyAlphanumericsRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern);
        }
        else if (antRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern.replace(/\*/g, '.*'));
        }
        else {
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
    formatHTML(rawHTML) {
        return rawHTML
            .replace(/^\s*\n/gm, '')
            .replace(/\>[\t\s]+\</g, '><')
            .replace(/[\r\n\t\s]+/g, ' ');
    }
}
const stringUtils = new StringUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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
class URIBuilder {
    constructor(uri) {
        this.uri = uri;
        this.wholeWordMatch = '[\\w]+';
    }
    build() {
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
    replaceParams(params) {
        const clone = lodash__namespace.cloneDeep(this);
        if (params) {
            // order the keys by descending length
            clone.uri = Object.keys(params)
                .sort(function (a, b) {
                return b.length - a.length;
            })
                .reduce((tempURL, key) => this.substituteKeyForValue(tempURL, key, params[key]), clone.uri);
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
    sanitize() {
        const clone = lodash__namespace.cloneDeep(this);
        const uriDomainAndPath = /(https?:\/\/[^\/]*)(\/.*)/.exec(clone.uri);
        clone.uri = uriDomainAndPath == null ? clone.uri : uriDomainAndPath[2];
        clone.uri = this.substituteKeyForValue(clone.uri, this.wholeWordMatch, '')
            .replace(/\/\//, '/') // to replace double slash (api/:identifier/data?param=true) if :identifier is removed
            .replace(/\/\?/, '?') // to replace slash question mark (api/:identifier?param=true) if :identifier is removed
            .replace(/\/$/, ''); // to remove trailing slash
        clone.uri = uriDomainAndPath == null ? clone.uri : uriDomainAndPath[1] + clone.uri;
        return clone;
    }
    substituteKeyForValue(url, key, value) {
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
class UrlUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:UrlUtils#getOrigin
     * @methodOf @smartutils.services:UrlUtils
     * @description
     * returns document location origin
     * Some browsers still do not support W3C document.location.origin, this function caters for gap.
     * @param {String =} url optional any url
     */
    getOrigin(url) {
        if (!!url) {
            let link = document.createElement('a');
            link.setAttribute('href', url);
            const origin = link.protocol + '//' + link.hostname + (link.port ? ':' + link.port : '');
            // @ts-ignore
            link = null; // GC
            return origin;
        }
        return (window.location.protocol +
            '//' +
            window.location.hostname +
            (window.location.port ? ':' + window.location.port : ''));
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
    getURI(url) {
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
    updateUrlParameter(url, key, value) {
        const i = url.indexOf('#');
        const hash = i === -1 ? '' : url.substr(i);
        url = i === -1 ? url : url.substr(0, i);
        const regex = new RegExp('([?&])' + key + '=.*?(&|$)', 'i');
        const separator = url.indexOf('?') !== -1 ? '&' : '?';
        if (url.match(regex)) {
            url = url.replace(regex, '$1' + key + '=' + value + '$2');
        }
        else {
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
    getQueryString(params) {
        let queryString = '';
        if (params) {
            for (const param in params) {
                if (params.hasOwnProperty(param)) {
                    (lodash__namespace.isArray(params[param]) ? params[param] : [params[param]]).forEach((value) => {
                        queryString +=
                            '&' + encodeURIComponent(param) + '=' + encodeURIComponent(value);
                    });
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
    parseQuery(str) {
        const objURL = {};
        str.replace(/([^?=&]+)(=([^&]*))?/g, function ($0, $1, $2, $3) {
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
    getAbsoluteURL(domain, url) {
        // url regex
        // scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
        const re = new RegExp('([a-zA-Z0-9]+://)' + // scheme
            '([a-zA-Z0-9_]+:[a-zA-Z0-9_]+@)?' + // user:password
            '([a-zA-Z0-9.-]+)' + // hostname
            '|([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)' + // or ip
            '(:[0-9]+)?' + // port
            '(/.*)?' // everything else
        );
        return re.exec(url) ? url : domain + url;
    }
}
const urlUtils = new UrlUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc service
 * @name @smartutils.services:CryptographicUtils
 *
 * @description
 * utility service around Cryptographic operations.
 */
class CryptographicUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:CryptographicUtils#sha1Hash
     * @methodOf @smartutils.services:CryptographicUtils
     *
     * @description
     * A utility function that takes an input string and provides a cryptographic SHA1 hash value.
     *
     * @param {String} data The input string to be encrypted.
     * @returns {String} the encrypted hashed result.
     */
    sha1Hash(data) {
        return CryptoJS__namespace.SHA1(data).toString();
    }
    aesBase64Encrypt(base64EncodedMessage, secretPassphrase) {
        return CryptoJS__namespace.AES.encrypt(CryptoJS__namespace.enc.Base64.parse(base64EncodedMessage), secretPassphrase).toString();
    }
    aesDecrypt(encryptedMessage, secretPassphrase) {
        return CryptoJS__namespace.AES.decrypt(encryptedMessage, secretPassphrase).toString(CryptoJS__namespace.enc.Utf8);
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc service
 * @name @smartutils.services:FunctionsUtils
 *
 * @description
 * utility service around Functions.
 */
class FunctionsUtils {
    constructor() {
        /*
         * regexp matching function(a, $b){} and function MyFunction(a, $b){}
         */
        this.signatureArgsRegexp = /function[\s\w]*\(([\w\s\$,]*)\)[\s]*{/;
    }
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
    isEmpty(func) {
        const match = func.toString().match(/\{([\s\S]*)\}/m);
        return (!match ||
            match[1].trim() === '' ||
            /(proxyFunction)/g.test(func.toString().replace(/\s/g, '')));
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
    getArguments(func) {
        const exec = this.signatureArgsRegexp.exec(func.toString());
        if (exec) {
            return exec[1].replace(/\s/g, '').split(',');
        }
        else {
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
    hasArguments(func) {
        const exec = this.signatureArgsRegexp.exec(func.toString());
        if (exec) {
            return !lodash__namespace.isEmpty(exec[1]);
        }
        else {
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
    getConstructorName(func) {
        let name = func.name;
        if (!name) {
            // IE does not support constructor.name
            const exec = /function (\$?\w+)\s*\(/.exec(func.toString());
            if (exec) {
                name = exec[1];
            }
            else {
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
    getInstanceConstructorName(instance) {
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
    extendsConstructor(originalConstructor, newConstructorBody) {
        // the new constructor behaviour
        const newConstructor = function (...args) {
            return new newConstructorBody(...args);
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
    getInstanceMethods(obj) {
        const isGetter = (_obj, prop) => !!_obj.__lookupGetter__(prop);
        let keys = [];
        const topObject = obj;
        const onlyOriginalMethods = (p, i, arr) => !isGetter(topObject, p) &&
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
            Object.getPrototypeOf(obj));
        return keys;
    }
    /** @internal */
    isUnitTestMode() {
        /* forbiddenNameSpaces window._:false */
        return typeof window.__karma__ !== 'undefined';
    }
    /**
     * The helper for tests which use try / catch block.
     * When `try` block contains only `await` and `catch` block contains expects then,
     * when someone changes method and and `catch` block is never entered it should fail
     */
    assertFail() {
        const spy = jasmine.createSpy('TestShouldNotReachThatPart-CheckYourTryCatchBlock');
        expect(spy).toHaveBeenCalled();
    }
    convertToArray(obj) {
        return Object.keys(obj).reduce((acc, key) => [
            ...(acc || []),
            { key, value: obj[key] }
        ], []);
    }
}
const functionsUtils = new FunctionsUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const commonNgZone = new core.NgZone({ enableLongStackTrace: false });

/**
 * @ngdoc service
 * @name @smartutils.services:WindowUtils
 *
 * @description
 * A collection of utility methods for windows.
 */
class WindowUtils {
    constructor(ngZone) {
        this.ngZone = ngZone;
        /**
         * @ngdoc method
         * @name @smartutils.services:WindowUtils#isIframe
         * @methodOf @smartutils.services:WindowUtils
         * @description
         * <b>isIframe</b> will check if the current document is in an iFrame.
         * @returns {boolean} true if the current document is in an iFrame.
         */
        this.isIframe = () => this.getWindow().top !== this.getWindow();
        this.ngZone = this.ngZone || commonNgZone;
    }
    getWindow() {
        return window;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#runTimeoutOutsideAngular
     * @methodOf @smartutils.services:WindowUtils
     *
     * @description
     * Runs a given timeout outside Angular and attaches its callback to Angular
     * this is usefull in order not to be blocking from an e2e stand point
     *
     * @param {string} callback argument less callback to execute when timeout.
     * @param {number} timeout the delay in milliseconds until timeout
     */
    runTimeoutOutsideAngular(callback, timeout) {
        const ngZone = this.ngZone;
        if (ngZone !== undefined) {
            return ngZone.runOutsideAngular(() => setTimeout(() => ngZone.run(callback), timeout));
        }
        else {
            throw new Error('this instance of WindowUtils has not been instantiated through Angular 7 DI');
        }
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:WindowUtils#runIntervalOutsideAngular
     * @methodOf @smartutils.services:WindowUtils
     *
     * @description
     * Runs a given interval outside Angular and attaches its callback to Angular
     * this is usefull in order not to be blocking from an e2e stand point
     *
     * @param {string} callback argument less callback to execute when timeout.
     * @param {number} timeout the delay in milliseconds until timeout
     */
    runIntervalOutsideAngular(callback, timeout) {
        const ngZone = this.ngZone;
        if (ngZone === undefined) {
            throw new Error('this instance of WindowUtils has not been instantiated through Angular 7 DI');
        }
        return ngZone.runOutsideAngular(() => setInterval(() => ngZone.run(callback), timeout));
    }
}
WindowUtils.SMARTEDIT_IFRAME_ID = 'ySmartEditFrame';
const windowUtils = new WindowUtils();

/******************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */

function __decorate(decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
}

function __param(paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
}

function __metadata(metadataKey, metadataValue) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
}

function __awaiter(thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
}

/*
 * internal utility service to handle ES6 modules
 */
/* forbiddenNameSpaces angular.module:false */
/** @internal */
exports.ModuleUtils = class ModuleUtils {
    initialize(useFactory, deps = []) {
        return {
            provide: core.APP_INITIALIZER,
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            useFactory(...args) {
                useFactory.apply(undefined, args);
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                return (component) => {
                    // an initializer useFactory must return a function
                };
            },
            deps,
            multi: true
        };
    }
    bootstrap(useFactory, deps = []) {
        return {
            provide: core.APP_BOOTSTRAP_LISTENER,
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            useFactory(...args) {
                useFactory.apply(undefined, args);
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                return (component) => {
                    // an initializer useFactory must return a function
                };
            },
            deps,
            multi: true
        };
    }
    provideValues(_constants) {
        const constants = _constants || {};
        return Object.keys(constants).map((key) => ({
            provide: key,
            useValue: constants[key]
        }));
    }
};
exports.ModuleUtils = __decorate([
    core.Injectable()
], exports.ModuleUtils);
const moduleUtils = new exports.ModuleUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
exports.LogLevel = void 0;
(function (LogLevel) {
    LogLevel[LogLevel["log"] = 0] = "log";
    LogLevel[LogLevel["debug"] = 1] = "debug";
    LogLevel[LogLevel["info"] = 2] = "info";
    LogLevel[LogLevel["warn"] = 3] = "warn";
    LogLevel[LogLevel["error"] = 4] = "error";
})(exports.LogLevel || (exports.LogLevel = {}));
class LogService {
    constructor() {
        this.logLevel = exports.LogLevel.info;
    }
    log(...msg) {
        this._log(exports.LogLevel.log, msg);
    }
    debug(...msg) {
        this._log(exports.LogLevel.debug, msg);
    }
    info(...msg) {
        this._log(exports.LogLevel.info, msg);
    }
    warn(...msg) {
        this._log(exports.LogLevel.warn, msg);
    }
    error(...msg) {
        this._log(exports.LogLevel.error, msg);
    }
    setLogLevel(logLevel) {
        this.logLevel = logLevel;
    }
    _log(requestLevel, msg) {
        if (requestLevel >= this.logLevel) {
            const method = exports.LogLevel[requestLevel];
            if (this._console() && this._console()[method]) {
                this._console()[method](...msg);
            }
        }
    }
    _console() {
        return console;
    }
}

var PromiseUtils_1;
/**
 * @ngdoc service
 * @name @smartutils.services:PromiseUtils
 *
 * @description
 * utility service around ES6 Promises.
 */
exports.PromiseUtils = PromiseUtils_1 = class PromiseUtils {
    constructor() {
        this.WAIT_TIMEOUT = 4;
        this.FAILURE_TIMEOUT = 2000;
        this.handlePromiseRejections = (promise) => {
            const oldThen = promise.then;
            const defaultFailureCallback = this.defaultFailureCallback;
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            promise.then = function (successCallback, _failureCallback) {
                const failureCallback = _failureCallback ? _failureCallback : defaultFailureCallback;
                return oldThen.call(this, successCallback, failureCallback);
            };
            return promise;
        };
        this.defaultFailureCallback = (error) => {
            if (undefined !== error && null !== error && 'canceled' !== error) {
                if (lodash__namespace.isPlainObject(error)) {
                    if (!this.isAjaxError(error)) {
                        PromiseUtils_1.logService.error(`exception caught in promise: ${JSON.stringify(error)}`);
                    }
                }
                else if (!lodash__namespace.isBoolean(error)) {
                    PromiseUtils_1.logService.error(error);
                }
            }
            PromiseUtils_1.logService.error(`defaultFailureCallback:`, error);
            return Promise.reject(error);
        };
    }
    toPromise(method, context) {
        return function (...args) {
            try {
                return Promise.resolve(method.apply(context, args));
            }
            catch (e) {
                PromiseUtils_1.logService.error('execution of a method that was turned into a promise failed');
                PromiseUtils_1.logService.error(e);
                return Promise.reject(e);
            }
        };
    }
    promise(executor) {
        return this.handlePromiseRejections(new Promise(executor));
    }
    defer() {
        let pResolve;
        let pReject;
        const deferred = {
            promise: this.promise((_resolve, _reject) => {
                pResolve = _resolve;
                pReject = _reject;
            }),
            resolve(value) {
                pResolve(value);
            },
            reject(reason, silenceError) {
                if (silenceError === undefined) {
                    pReject(reason);
                }
                else {
                    pReject('');
                }
            }
        };
        return deferred;
    }
    sleep(ms) {
        return __awaiter(this, void 0, void 0, function* () {
            return new Promise((resolve) => setTimeout(resolve, ms));
        });
    }
    isAjaxError(error) {
        return error.hasOwnProperty('headers');
    }
    waitOnCondition(condition, callback, errorMessage, elapsedTime = 0) {
        setTimeout(() => {
            if (condition()) {
                callback();
            }
            else if (elapsedTime < this.FAILURE_TIMEOUT) {
                this.waitOnCondition(condition, callback, errorMessage, elapsedTime + this.WAIT_TIMEOUT);
            }
            else {
                throw new Error(`PromiseUtils: ${errorMessage}`);
            }
        }, this.WAIT_TIMEOUT);
    }
    resolveToCallbackWhenCondition(condition, callback, errorMessage) {
        return __awaiter(this, void 0, void 0, function* () {
            return new Promise((resolve) => {
                this.waitOnCondition(condition, () => resolve(callback()), errorMessage ? errorMessage : 'condition for promise resolution was never met');
            });
        });
    }
    attempt(promise) {
        return promise
            .then((data) => ({
            error: null,
            data
        }))
            .catch((error) => ({
            error,
            data: null
        }));
    }
};
exports.PromiseUtils.logService = new LogService();
exports.PromiseUtils = PromiseUtils_1 = __decorate([
    core.Injectable()
], exports.PromiseUtils);
const promiseUtils = new exports.PromiseUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc service
 * @name @smartutils.services:BooleanUtils
 *
 * @description
 * utility service around booleans.
 */
class BooleanUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:BooleanUtils#areAllTruthy
     * @methodOf @smartutils.services:BooleanUtils
     * @description
     * Iterate on the given array of Functions, return true if each function returns true
     *
     * @param {Array} arguments the functions
     *
     * @return {Boolean} true if every function returns true
     */
    areAllTruthy(...functions) {
        return function (...argus) {
            return functions.every((f) => f && f.apply(f, argus));
        };
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:BooleanUtils#isAnyTruthy
     * @methodOf @smartutils.services:BooleanUtils
     *
     * @description
     * Iterate on the given array of Functions, return true if at least one function returns true
     *
     * @param {Array} arguments the functions
     *
     * @return {Boolean} true if at least one function returns true
     */
    isAnyTruthy(...functions) {
        return function (...argus) {
            return functions.some((f) => f && f.apply(f, argus));
        };
    }
}
const booleanUtils = new BooleanUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class HttpUtils {
    isGET(request) {
        return request.method === 'GET';
    }
    isRequestOfAccept(request, accept) {
        return (!!request.headers &&
            !!request.headers.get('Accept') &&
            (request.headers.get('Accept') || '').includes(accept));
    }
    isResponseOfContentType(response, contentType) {
        return (!!response.headers &&
            !!response.headers.get('Content-type') &&
            (response.headers.get('Content-type') || '').indexOf(contentType) === 0);
    }
    isHTMLRequest(request, response) {
        return (this.isGET(request) &&
            (this.isRequestOfAccept(request, 'text/html') ||
                /.+\.html$/.test(request.url) ||
                /.+\.html\?/.test(request.url)));
    }
    isJSONRequest(request, response) {
        return (this.isGET(request) &&
            ((response && this.isResponseOfContentType(response, 'json')) ||
                /.+\.json$/.test(request.url)));
    }
    isJSRequest(request) {
        return this.isGET(request) && /.+\.js$/.test(request.url);
    }
    isCRUDRequest(request, response) {
        return (!this.isHTMLRequest(request, response) &&
            !this.isJSONRequest(request, response) &&
            !this.isJSRequest(request));
    }
    transformHttpParams(params, substitutionMap) {
        return new http.HttpParams({
            fromObject: JSON.parse(stringUtils.replaceAll(JSON.stringify(this.copyHttpParamsOrHeaders(params)), substitutionMap))
        });
    }
    copyHttpParamsOrHeaders(params) {
        const copy = {};
        params.keys().forEach((key) => {
            const values = params.getAll(key);
            if (values !== null) {
                copy[key] = values.length > 1 ? values : values[0];
            }
        });
        return copy;
    }
    buildHttpResponse(originalRequest, _statusAndPayload) {
        const statusAndPayloadPromise = Promise.resolve(_statusAndPayload);
        return rxjs.from(statusAndPayloadPromise).pipe(operators.switchMap((statusAndPayload) => {
            const status = statusAndPayload[0];
            const body = statusAndPayload[1];
            const requestClone = originalRequest.clone({
                body
            });
            lodash__namespace.merge(requestClone, { status });
            if (200 <= status && status < 300) {
                return new rxjs.Observable((ob) => {
                    ob.next(new http.HttpResponse(requestClone));
                });
            }
            else {
                return rxjs.throwError(new http.HttpErrorResponse(lodash__namespace.merge(requestClone, { error: body })));
            }
        }));
    }
}
const httpUtils = new HttpUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc object
 * @name utils.object:BaseValueAccessor
 * @description
 *
 * Class implementing {@link https://angular.io/api/forms/ControlValueAccessor ControlValueAccessor} interface used to create custom Angular inputs that
 * can be integrated with Angular Forms and.
 */
class BaseValueAccessor {
    constructor() {
        this.disabled = false;
        this.value = null;
    }
    onChange(item) {
        // Is set by registerOnChange method
    }
    onTouched() {
        // Is set by registerOnTouched method
    }
    registerOnChange(fn) {
        this.onChange = fn;
    }
    registerOnTouched(fn) {
        this.onTouched = fn;
    }
    setDisabledState(isDisabled) {
        this.disabled = isDisabled;
    }
    writeValue(item) {
        this.value = item;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @internal */
var annotationType;
(function (annotationType) {
    annotationType["ClassAnnotation"] = "ClassAnnotation";
    annotationType["MethodAnnotation"] = "MethodAnnotation";
})(annotationType || (annotationType = {}));
/**
 * @ngdoc service
 * @name @smartutils.services:AnnotationService
 *
 * @description
 * Utility service to declare and consume method level and class level {@link https://www.typescriptlang.org/docs/handbook/decorators.html Typescript decorator factories}.
 * <br/>Since Decorator is a reserved word in Smartedit, Typescript Decorators are called as Annotations.
 */
class AnnotationService {
    constructor() {
        this.INJECTABLE_NAME_KEY = 'getInjectableName';
        this.ORIGINAL_CONSTRUCTOR_KEY = 'originalConstructor';
        /**
         * @ngdoc method
         * @name @smartutils.services:AnnotationService#getClassAnnotations
         * @methodOf @smartutils.services:AnnotationService
         *
         * @description
         * Retrieves an object with all the string-indexed annotations defined on the given class target
         * @param {any} target The typescript class on which class annotations are defined
         * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
         */
        this.getClassAnnotations = lodash__namespace.memoize(this.getClassAnnotationsLogic);
        /**
         * @ngdoc method
         * @name @smartutils.services:AnnotationService#getMethodAnnotations
         * @methodOf @smartutils.services:AnnotationService
         *
         * @description
         * Retrieves an object with all the string indexed annotations defined on the given class method
         * @param {any} target The typescript class to the inspected
         * @param {string} propertyName The name of the method on which annotations are defined
         * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
         */
        this.getMethodAnnotations = lodash__namespace.memoize(this.getMethodAnnotationsLogic, function (target, propertyName) {
            return JSON.stringify(target.prototype) + propertyName;
        });
        this.functionsUtils = new FunctionsUtils();
        this.annotationFactoryMap = {};
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getClassAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves arguments of class annotation under a given annotation name
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {any} the payload passed to the annotation
     */
    getClassAnnotation(target, annotation) {
        const annotationMap = this.getClassAnnotations(target);
        const annotationName = annotation.annotationName;
        if (annotationMap && annotationName in annotationMap) {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-return
            return annotationMap[annotationName];
        }
        else {
            return null;
        }
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getMethodAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves arguments of method annotation for a given typescript class
     * @param {any} target The typescript class
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator)} annotation The type of the method annotation
     * @returns {any} the payload passed to the annotation
     */
    getMethodAnnotation(target, propertyName, annotation) {
        const annotationMap = this.getMethodAnnotations(target, propertyName);
        const annotationName = annotation.annotationName;
        if (annotationMap && annotationName in annotationMap) {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-return
            return annotationMap[annotationName];
        }
        else {
            return null;
        }
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#hasClassAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Determines whether a given class target has given annotation name defined or not
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {boolean} true if a given target has given annotation name. Otherwise false.
     */
    hasClassAnnotation(target, annotation) {
        const annotationMap = this.getClassAnnotations(target);
        return annotation.annotationName in annotationMap ? true : false;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#hasMethodAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Determines whether a given method name has given annotation name defined or not under a given typescript class
     * @param {any} target The typescript class object
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator} annotation The type of the method annotation
     * @returns {boolean} true if a given method name has given annotation name. Otherwise false.
     */
    hasMethodAnnotation(target, propertyName, annotation) {
        const annotationMap = this.getMethodAnnotations(target, propertyName);
        return annotation.annotationName in annotationMap ? true : false;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#setClassAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Registers a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory} under a given name.
     * <br/>Typically, in order for the ClassAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name the name of the factory.
     * @returns {ClassAnnotationFactory} a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     */
    setClassAnnotationFactory(name, annotationFactory) {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getClassAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const GatewayProxied = annotationService.getClassAnnotationFactory('GatewayProxied');
     * </pre>
     *
     * @param {string} name The name of the factory
     * @returns {ClassAnnotationFactory} a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     */
    getClassAnnotationFactory(name) {
        const instance = this;
        const classAnnotationFactory = function (...factoryArgument) {
            return function (originalConstructor) {
                const newConstructor = instance.functionsUtils.extendsConstructor(originalConstructor, function (...args) {
                    const annotationFactory = instance.annotationFactoryMap[name];
                    if (annotationFactory) {
                        // Note: Before we used to bind originalConstructor.bind(this). However, it had to be left up to the caller
                        // since that causes problems in IE; when a function is bound in IE, the browser wraps it in a function with
                        // native code, making it impossible to retrieve its name.
                        const result = annotationFactory(factoryArgument)(this, originalConstructor, args);
                        if (result) {
                            // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                            return result;
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        throw new Error(`annotation '${name}' is used on '${originalConstructor.name}' but its ClassAnnotationFactory may not have been added to the dependency injection`);
                    }
                });
                /*
                 * enable Angular to inject this new constructor even though it has an empty signature
                 * by copying $inject property and DI related Angular metadata
                 * For idempotent purposes we copy all properties anyways
                 */
                lodash__namespace.merge(newConstructor, originalConstructor);
                /*
                 * some properties set by Angular are not enumerable and yet contain
                 * such information as @Inject "metadata" necessary for DI
                 */
                newConstructor.__annotations__ = originalConstructor.__annotations__;
                newConstructor.__parameters__ = originalConstructor.__parameters__;
                newConstructor.__prop__metadata__ = originalConstructor.__prop__metadata__;
                /*
                 * copying such metadata as design:paramtypes necessary for DI
                 */
                Reflect.getMetadataKeys(originalConstructor).forEach((key) => {
                    Reflect.defineMetadata(key, Reflect.getMetadata(key, originalConstructor), newConstructor);
                });
                const rootOriginalConstructor = instance.getOriginalConstructor(originalConstructor);
                Reflect.defineMetadata(instance.ORIGINAL_CONSTRUCTOR_KEY, rootOriginalConstructor, newConstructor);
                Reflect.defineMetadata(`${annotationType.ClassAnnotation}:${name}`, factoryArgument, rootOriginalConstructor);
                // override original constructor
                // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                return newConstructor;
            };
        };
        classAnnotationFactory.annotationName = name;
        return classAnnotationFactory;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#setMethodAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Registers a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory} under a given name.
     * <br/>Typically, in order for the MethodAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name The name of the factory.
     * @returns {MethodAnnotationFactory} a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}
     */
    setMethodAnnotationFactory(name, annotationFactory) {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getMethodAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves a method level {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const Cached = annotationService.getMethodAnnotationFactory('Cached');
     * </pre>
     *
     * @param {string} name the name of the factory.
     * @returns {MethodAnnotationFactory} a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}.
     */
    getMethodAnnotationFactory(name) {
        const instance = this;
        const methodAnnotationFactory = function (...factoryArgument) {
            /*
             * when decorating an abstract class, strangely enough target is an instance of the abstract class
             * we need pass "this" instead to the annotationFactory invocation
             */
            return (target, propertyName, descriptor) => {
                const originalMethod = descriptor.value;
                descriptor.value = function (...args) {
                    const annotationFactory = instance
                        .annotationFactoryMap[name];
                    if (annotationFactory) {
                        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                        return originalMethod
                            ? annotationFactory(factoryArgument)(this, propertyName, originalMethod.bind(this), args)
                            : undefined;
                    }
                    else {
                        throw new Error(`annotation '${name}' is used but its MethodAnnotationFactory may not have been added to the dependency injection`);
                    }
                };
                Reflect.defineMetadata(`${annotationType.MethodAnnotation}:${name}`, factoryArgument, target, propertyName);
            };
        };
        methodAnnotationFactory.annotationName = name;
        return methodAnnotationFactory;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getOriginalConstructor
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Given a class constructor, returns the original constructor of it prior to any class level
     * proxying by annotations declared through {@link @smartutils.services:AnnotationService AnnotationService}
     *
     * @param {Class} target the constructor
     */
    getOriginalConstructor(target) {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return Reflect.getMetadata(this.ORIGINAL_CONSTRUCTOR_KEY, target) || target;
    }
    getClassAnnotationsLogic(target) {
        const originalConstructor = this.getOriginalConstructor(target);
        const annotationMap = {};
        Reflect.getMetadataKeys(originalConstructor)
            .filter((key) => key.toString().startsWith(annotationType.ClassAnnotation))
            .forEach((key) => {
            annotationMap[key.split(':')[1]] = Reflect.getMetadata(key, originalConstructor);
        });
        return annotationMap;
    }
    getMethodAnnotationsLogic(target, propertyName) {
        const annotationMap = {};
        Reflect.getMetadataKeys(target.prototype, propertyName)
            .filter((key) => key.toString().startsWith(annotationType.MethodAnnotation))
            .forEach((key) => {
            annotationMap[key.split(':')[1]] = Reflect.getMetadata(key, target.prototype, propertyName);
        });
        return annotationMap;
    }
}
const annotationService = new AnnotationService();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/// ////////////////////////////////////////////////////////////////////////////
/// ////////////////////////////// CACHE CONFIG ////////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////
const cacheConfigAnnotationName = 'CacheConfig';
/**
 * @ngdoc object
 * @name @smartutils.object:@CacheConfig
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for setting
 *  class level cache configuration to be merged into method specific {@link @smartutils.object:@Cached @Cached} and
 *  {@link @smartutils.object:@InvalidateCache @InvalidateCache} configurations.
 * @param {object} cacheConfig the configuration fo this cache
 * @param {cacheAction} cacheConfig.actions the list of {@link @smartutils.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link @smartutils.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
const CacheConfig = annotationService.getClassAnnotationFactory(cacheConfigAnnotationName);
function CacheConfigAnnotationFactory(logService) {
    'ngInject';
    return annotationService.setClassAnnotationFactory(cacheConfigAnnotationName, (factoryArguments) => function (instance, originalConstructor, invocationArguments) {
        instance = new (originalConstructor.bind(instance))(...invocationArguments);
        instance.cacheConfig = factoryArguments[0];
        logService.debug(`adding cache config ${JSON.stringify(instance.cacheConfig)} to class ${functionsUtils.getInstanceConstructorName(instance)}`, instance);
        return instance;
    });
}
/// ////////////////////////////////////////////////////////////////////////////
/// ///////////////////////////////// CACHE ////////////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////
const CachedAnnotationName = 'Cached';
/**
 * @ngdoc object
 * @name @smartutils.object:@Cached
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for performing
 * invocation arguments sensitive method caching.
 * <br/> This annotation must only be used on methods returning promises.
 * @param {object} cacheConfig the configuration fo this cache
 * <br/> This configuration will be merged with a class level {@link @smartutils.object:@CacheConfig @acheConfig} if any.
 * @throws if no {@link @smartutils.object:CacheAction CacheAction} is found in the resulting merge
 * @param {cacheAction} cacheConfig.actions the list of {@link @smartutils.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link @smartutils.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
const Cached = annotationService.getMethodAnnotationFactory(CachedAnnotationName);
function CachedAnnotationFactory(cacheService) {
    'ngInject';
    const result = annotationService.setMethodAnnotationFactory(CachedAnnotationName, (factoryArguments) => function (target, propertyName, originalMethod, invocationArguments) {
        let actions = [];
        let tags = [];
        if (factoryArguments[0]) {
            actions = factoryArguments[0].actions;
            tags = factoryArguments[0].tags;
        }
        if (target.cacheConfig) {
            if (target.cacheConfig.actions) {
                actions = lodash__namespace.uniq(actions.concat(target.cacheConfig.actions));
            }
            if (target.cacheConfig.tags) {
                tags = lodash__namespace.uniq(tags.concat(target.cacheConfig.tags));
            }
        }
        if (!actions.length) {
            const constructorName = functionsUtils.getInstanceConstructorName(target);
            throw new Error(`method ${propertyName} of ${constructorName} is @Cached annotated but no CacheAction is specified either through @Cached or through class level @CacheConfig annotation`);
        }
        return cacheService.handle(target, propertyName, originalMethod, Array.prototype.slice.apply(invocationArguments), actions, tags);
    });
    return result;
}
/// ////////////////////////////////////////////////////////////////////////////
/// /////////////////////////// INVALIDATE CACHE ///////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////
const InvalidateCacheName = 'InvalidateCache';
/**
 * @ngdoc object
 * @name @smartutils.object:@InvalidateCache
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for
 * invalidating all caches either directly or indirectly declaring the {@link @smartutils.object:EvictionTag eviction tag} passed as argument.
 * if no eviction tag is passed as argument, defaults to the optional eviction tags passed to the class through {@link @smartutils.object:@CacheConfig @CacheConfig}.
 *
 * @param {EvictionTag} evictionTag the {@link @smartutils.object:EvictionTag eviction tag}.
 */
const InvalidateCache = function (tag) {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return annotationService.getMethodAnnotationFactory(InvalidateCacheName)(tag);
};
function InvalidateCacheAnnotationFactory(cacheService) {
    'ngInject';
    return annotationService.setMethodAnnotationFactory(InvalidateCacheName, (factoryArguments) => function (target, propertyName, originalMethod, invocationArguments) {
        let tags = [];
        const tag = factoryArguments[0];
        if (!tag) {
            if (target.cacheConfig && target.cacheConfig.tags) {
                tags = target.cacheConfig.tags;
            }
        }
        else {
            tags = [tag];
        }
        if (!tags.length) {
            throw new Error(`method ${propertyName} of ${target.constructor.name} is @InvalidateCache annotated but no EvictionTag is specified either through @InvalidateCache or through class level @CacheConfig annotation`);
        }
        // eslint-disable-next-line prefer-spread
        const returnedObject = originalMethod.apply(undefined, Array.prototype.slice.call(invocationArguments));
        if (returnedObject && returnedObject.then) {
            return returnedObject.then((value) => {
                cacheService.evict(...tags);
                return value;
            });
        }
        else {
            cacheService.evict(...tags);
            return returnedObject;
        }
    });
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc object
 * @name @smartutils.object:CacheAction
 * @description
 * A {@link @smartutils.object:@Cached @Cached} annotation is associated to a CacheAction.
 */
class CacheAction {
    constructor(name) {
        this.name = name;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const LIBRARY_NAME = '@smart/utils';
/* TOKENS */
const WHO_AM_I_RESOURCE_URI_TOKEN = `${LIBRARY_NAME}_WHO_AM_I_RESOURCE_URI`;
const I18N_RESOURCE_URI_TOKEN = `${LIBRARY_NAME}_I18N_RESOURCE_URI`;
const EVENT_SERVICE = `${LIBRARY_NAME}_EVENTSERVICE`;
/* EVENTS */
const REAUTH_STARTED = 'REAUTH_STARTED';
const DEFAULT_AUTHENTICATION_ENTRY_POINT = '/authorizationserver/oauth/token';
/**
 * Root resource URI of i18n API
 */
const I18N_ROOT_RESOURCE_URI = '/smarteditwebservices/v1/i18n';
const DEFAULT_AUTHENTICATION_CLIENT_ID = 'smartedit';
const DEFAULT_AUTH_MAP = {
    ['^(?!' + I18N_ROOT_RESOURCE_URI + '/.*$).*$']: DEFAULT_AUTHENTICATION_ENTRY_POINT
};
const DEFAULT_CREDENTIALS_MAP = {
    [DEFAULT_AUTHENTICATION_ENTRY_POINT]: {
        client_id: DEFAULT_AUTHENTICATION_CLIENT_ID
    }
};
const LANDING_PAGE_PATH = '/';
const SWITCH_LANGUAGE_EVENT = 'SWITCH_LANGUAGE_EVENT';
const SELECTED_LANGUAGE = 'SELECTED_LANGUAGE';
const EVENTS = {
    TOKEN_STORED: 'TOKEN_STORED',
    AUTHORIZATION_SUCCESS: 'AUTHORIZATION_SUCCESS',
    USER_HAS_CHANGED: 'USER_HAS_CHANGED',
    LOGOUT: 'SE_LOGOUT_EVENT',
    CLEAR_PERSPECTIVE_FEATURES: 'CLEAR_PERSPECTIVE_FEATURES',
    EXPERIENCE_UPDATE: 'experienceUpdate',
    PERMISSION_CACHE_CLEANED: 'PERMISSION_CACHE_CLEANED',
    PAGE_CHANGE: 'PAGE_CHANGE',
    PAGE_CREATED: 'PAGE_CREATED_EVENT',
    PAGE_UPDATED: 'PAGE_UPDATED_EVENT',
    PAGE_DELETED: 'PAGE_DELETED_EVENT',
    PAGE_SELECTED: 'PAGE_SELECTED_EVENT',
    PAGE_RESTORED: 'PAGE_RESTORED_EVENT',
    REAUTH_STARTED: 'REAUTH_STARTED'
};
const DEFAULT_LANGUAGE_ISO = 'en';
const LANGUAGE_SERVICE_CONSTANTS = new core.InjectionToken('LANGUAGE_SERVICE_CONSTANTS');
const LANGUAGE_SERVICE = new core.InjectionToken('LANGUAGE_SERVICE');
const YJQUERY_TOKEN = 'yjQuery';

var CacheEngine_1;
/** @internal */
exports.CacheEngine = CacheEngine_1 = class CacheEngine {
    constructor(windowUtils, promiseUtils, logService) {
        this.windowUtils = windowUtils;
        this.promiseUtils = promiseUtils;
        this.logService = logService;
        this.cachedItemsRegistry = [];
        this.startBackgroundMonitoringJob();
    }
    addItem(item, cacheTiming, refresh) {
        if (this.getItemIndex(item) === -1) {
            this.cachedItemsRegistry.push({
                item,
                cacheTiming,
                refresh,
                completed: false,
                processing: false,
                defer: this.promiseUtils.defer()
            });
        }
        else {
            this.logService.warn(`CacheEngine - item already exist for id: ${item.id}`);
        }
    }
    getItemById(id) {
        const match = this.cachedItemsRegistry.find((obj) => obj.item.id === id);
        return match ? match.item : null;
    }
    handle(item) {
        const obj = this.cachedItemsRegistry[this.getItemIndex(item)];
        if (obj.completed && !this.hasExpired(item)) {
            obj.defer.resolve(item.cache);
        }
        else if (!obj.processing) {
            obj.processing = true;
            this.refreshCache(obj);
        }
        return obj.defer.promise;
    }
    evict(...tags) {
        tags.forEach((tag) => {
            this.cachedItemsRegistry
                .filter((obj) => obj.item.evictionTags.indexOf(tag) > -1)
                .forEach((obj) => this.cachedItemsRegistry.splice(this.getItemIndex(obj.item), 1));
        });
    }
    // regularly go though cache data and call prebound methods to refresh data when needed.
    startBackgroundMonitoringJob() {
        this.windowUtils.runIntervalOutsideAngular(() => Promise.all(this.cachedItemsRegistry
            .filter((obj) => this.needRefresh(obj.item))
            .map((obj) => this.refreshCache(obj))), CacheEngine_1.BACKGROUND_REFRESH_INTERVAL);
    }
    refreshCache(obj) {
        return obj.refresh().then((value) => {
            // TODO: read value.metadata to refresh expiry/refresh ages.
            obj.cacheTiming.setAge(obj.item);
            obj.item.cache = value;
            obj.item.timestamp = new Date().getTime();
            obj.completed = true;
            obj.processing = false;
            obj.defer.resolve(value);
        }, (e) => {
            this.logService.debug(`CacheEngine - unable to refresh cache for id: ${obj.item.id}`, e);
            delete obj.item.cache;
            obj.defer.reject(e);
        });
    }
    hasExpired(item) {
        return item.timestamp + item.expirationAge <= new Date().getTime();
    }
    needRefresh(item) {
        return item.timestamp + item.refreshAge <= new Date().getTime();
    }
    getItemIndex(item) {
        return this.cachedItemsRegistry.findIndex((o) => o.item.id === item.id);
    }
};
exports.CacheEngine.BACKGROUND_REFRESH_INTERVAL = 10000;
exports.CacheEngine = CacheEngine_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [WindowUtils,
        exports.PromiseUtils,
        LogService])
], exports.CacheEngine);

class DefaultCacheTiming {
    constructor(expirationAge, refreshAge) {
        // The cached response is discarded if it is older than the expiration age.
        this.expirationAge = expirationAge;
        // maximum age for the cached response to be considered "fresh."
        this.refreshAge = refreshAge;
    }
    setAge(_item) {
        const item = Object.assign(Object.assign({}, _item), { expirationAge: this.expirationAge, refreshAge: this.refreshAge });
        return item;
    }
}

/**
 * @ngdoc service
 * @name @smartutils.services:CacheService
 * @description
 * Service to which the {@link @smartutils.object:@Cached @Cached} and {@link @smartutils.object:@InvalidateCache @InvalidateCache} annotations delegate to perform service method level caching.
 * It is not handled explicitly except for its evict method.
 */
exports.CacheService = class CacheService {
    constructor(logService, stringUtils, functionsUtils, eventService, cacheEngine) {
        this.logService = logService;
        this.stringUtils = stringUtils;
        this.functionsUtils = functionsUtils;
        this.eventService = eventService;
        this.cacheEngine = cacheEngine;
        this.predicatesRegistry = [];
        this.eventListeners = [];
        this.defaultCacheTiming = new DefaultCacheTiming(24 * 60 * 60 * 1000, 12 * 60 * 60 * 1000);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:CacheService#register
     * @methodOf @smartutils.services:CacheService
     *
     * @description
     * Register a new predicate with it's associated cacheTiming.
     * Each time the @Cache annotation is handled, the CacheService try to find a matching cacheTiming for the given cacheActions.
     *
     * @param {ICachePredicate} test This function takes the cacheActions {@link @smartutils.object:CacheAction CacheAction} argument, and must return a Boolean that is true if the given cacheActions match the predicate.
     * @param {ICacheTiming} cacheTiming This function is used to call setAge(item: ICacheItem<any>) on the cached item.
     *
     * @return {CacheService} CacheService The CacheService instance.
     *
     * @example
     * ```ts
     * export class CustomCacheTiming implements ICacheTiming {
     * 	private expirationAge: number;
     * 	private refreshAge: number;
     *  constructor(expirationAge: number, refreshAge: number) {
     * 		// The cached response is discarded if it is older than the expiration age.
     * 		this.expirationAge = expirationAge;
     * 		// maximum age for the cached response to be considered "fresh."
     * 		this.refreshAge = refreshAge;
     * 	}
     * 	setAge(item: ICacheItem<any>): void {
     * 		item.expirationAge = this.expirationAge;
     * 		item.refreshAge = this.refreshAge;
     * 	}
     * 	};
     * 	const customCacheTiming = new CustomCacheTiming(30 * 60000, 15 * 60000);
     * 	const customContentPredicate: ICachePredicate = (cacheActions: CacheAction[]) => {
     * 		return cacheActions.find((cacheAction) => cacheAction.name === 'CUSTOM_TAG') !== null;
     * 	};
     * this.register(customContentPredicate, customCacheTiming);
     * ```
     */
    register(test, cacheTiming) {
        this.predicatesRegistry.unshift({
            test,
            cacheTiming
        });
        return this;
    }
    /**
     * public method but only meant to be used by @Cache annotation
     */
    handle(service, methodName, preboundMethod, invocationArguments, cacheActions, tags) {
        const constructorName = this.functionsUtils.getInstanceConstructorName(service);
        const cachedItemId = window.btoa(constructorName + methodName) +
            this.stringUtils.encode(invocationArguments);
        const _item = this.cacheEngine.getItemById(cachedItemId);
        let item;
        if (!_item) {
            const partialItem = _item || {
                id: cachedItemId,
                timestamp: new Date().getTime(),
                evictionTags: this.collectEventNamesFromTags(tags),
                cache: null
            };
            const cacheTiming = this.findCacheTimingByCacheActions(cacheActions);
            if (!cacheTiming) {
                throw new Error('CacheService::handle - No predicate match.');
            }
            item = cacheTiming.setAge(partialItem);
            this.cacheEngine.addItem(item, cacheTiming, preboundMethod.bind(undefined, ...Array.prototype.slice.call(invocationArguments)));
            this.listenForEvictionByTags(tags);
        }
        else {
            item = _item;
        }
        return this.cacheEngine.handle(item);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:CacheService#evict
     * @methodOf  @smartutils.services:CacheService
     * @description
     * Will evict the entire cache of all methods of all services referencing either directly or indirectly the given {@link @smartutils.object:EvictionTag EvictionTags}
     * @param {...EvictionTag[]} evictionTags the {@link @smartutils.object:EvictionTag EvictionTags}
     */
    evict(...evictionTags) {
        const tags = this.collectEventNamesFromTags(evictionTags);
        this.cacheEngine.evict(...tags);
        tags.forEach((tag) => this.eventService.publish(tag));
    }
    listenForEvictionByTags(tags) {
        this.collectEventNamesFromTags(tags)
            .filter((eventId) => this.eventListeners.indexOf(eventId) === -1)
            .forEach((eventId) => {
            this.logService.debug(`registering event listener ${eventId}`);
            this.eventListeners.push(eventId);
            this.eventService.subscribe(eventId, (evt, data) => {
                this.logService.debug(`cleaning cache on event ${eventId}`);
                this.cacheEngine.evict(eventId);
                return Promise.resolve({});
            });
        });
    }
    collectEventNamesFromTags(tags) {
        if (tags && tags.length) {
            return lodash__namespace.union(...tags.map((t) => this.collectEventNamesFromTag(t)));
        }
        else {
            return [];
        }
    }
    collectEventNamesFromTag(tag) {
        return lodash__namespace.union([tag.event], ...(tag.relatedTags ? tag.relatedTags.map((t) => this.collectEventNamesFromTag(t)) : []));
    }
    findCacheTimingByCacheActions(cacheActions) {
        const predicate = this.predicatesRegistry.find((cacheTimingPredicate) => cacheTimingPredicate.test(cacheActions));
        return predicate ? predicate.cacheTiming : this.defaultCacheTiming;
    }
};
exports.CacheService = __decorate([
    core.Injectable(),
    __param(3, core.Inject(EVENT_SERVICE)),
    __metadata("design:paramtypes", [LogService,
        StringUtils,
        FunctionsUtils, Object, exports.CacheEngine])
], exports.CacheService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc object
 * @name @smartutils.object:EvictionTag
 * @description
 * A {@link @smartutils.object:@Cached @Cached} annotation is tagged with 0 to n EvictionTag, each EvictionTag possibly referencing other evictionTags.
 * <br/>An EvictionTag enables a method cache to be evicted 2 different ways:
 * <ul>
 * <li> An event with the same name as the tag is raised.</li>
 * <li> {@link @smartutils.services:CacheService#methods_evict evict} method of {@link @smartutils.services:CacheService cacheService} is invoked with the tag.</li>
 * </ul>
 */
class EvictionTag {
    constructor(args) {
        this.event = args.event;
        this.relatedTags = args.relatedTags;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const RarelyChangingContentName = 'RarelyChangingContent';
const rarelyChangingContent = new CacheAction(RarelyChangingContentName);

/**
 * @ngdoc service
 * @name @smartutils.services:BackendEntry
 * @description
 * Invocations of {@link @smartutils.services:HttpBackendService} when, whenGET, whenPOST, whenPUT, whenDELETE
 * all return an instance of {@link @smartutils.services:BackendEntry BackendEntry}
 * It is used to specify the mocked response for the given conditions.
 */
class BackendEntry {
    constructor(pattern, matchingPayload) {
        this.pattern = pattern;
        this.matchingPayload = matchingPayload;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:BackendEntry#respond
     * @methodOf @smartutils.services:BackendEntry
     * @description
     * @param {BackendRespond} mock the {@link @smartutils.object:BackendRespond} to return for the given conditions
     */
    respond(mock) {
        this.mock = mock;
        return this;
    }
    passThrough() {
        //
    }
}
/**
 * @ngdoc service
 * @name @smartutils.services:HttpBackendService
 * @description
 * Service aimed to provide mocked backend responses for given http request patterns.
 */
exports.HttpBackendService = class HttpBackendService {
    constructor() {
        this.matchLatestDefinition = false;
        /* Adding PATCH method as it will fail if original request is a patch call
         * See ticket: https://sapjira.wdf.sap.corp/browse/CXEC-6960
         */
        this.backends = {
            GET: [],
            POST: [],
            PUT: [],
            PATCH: [],
            DELETE: []
        };
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenGET
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenGET $httpBackend#whenGET}
     * but with only the url pattern as parameter
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenGET(pattern) {
        return this._whenMethod('GET', pattern);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPOST
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPOST $httpBackend#whenPOST}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPOST(pattern, matchingPayload) {
        return this._whenMethod('POST', pattern, matchingPayload);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPUT
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPUT $httpBackend#whenPUT}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPUT(pattern, matchingPayload) {
        return this._whenMethod('PUT', pattern, matchingPayload);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPATCH
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPATCH $httpBackend#whenPATCH}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPATCH(pattern, matchingPayload) {
        return this._whenMethod('PATCH', pattern, matchingPayload);
    }
    /**
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPUT
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPUT $httpBackend#whenPUT}
     * but with only the url pattern as parameter
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenDELETE(pattern) {
        return this._whenMethod('DELETE', pattern);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#when
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#when $httpBackend#when}
     * @param {string} method GET, POST, PUT, or DELETE
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    when(method, pattern, matchingPayload) {
        return this._whenMethod(method, pattern, matchingPayload);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenAsync
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to legacy $httpBackend#whenAsync, use {@link @smartutils.services:HttpBackendService#when HttpBackendService#when} instead
     * @param {string} method GET, POST, PUT, or DELETE
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenAsync(method, pattern, matchingPayload) {
        return this._whenMethod(method, pattern, matchingPayload);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#matchLatestDefinitionEnabled
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#matchLatestDefinitionEnabled $httpBackend#matchLatestDefinitionEnabled}
     * @param {boolean=false} matchLatestDefinitionEnabled if true, the last matching pattern will be picked. Otherwise the first is picked
     */
    matchLatestDefinitionEnabled(matchLatestDefinitionEnabled) {
        this.matchLatestDefinition = matchLatestDefinitionEnabled;
    }
    // whenAsync
    /// /////////////////////////////////
    findMatchingMock(request) {
        const backendEntry = (this.matchLatestDefinition
            ? this.backends[request.method].slice().reverse()
            : this.backends[request.method]).find((entry) => {
            if (typeof entry.pattern === 'string') {
                return (request.urlWithParams.endsWith(entry.pattern) &&
                    this.matchingPayloadRestriction(entry, request));
            }
            else {
                const test = entry.pattern.test(request.urlWithParams) &&
                    this.matchingPayloadRestriction(entry, request);
                entry.pattern.lastIndex = 0;
                return test;
            }
        });
        return backendEntry ? backendEntry.mock : undefined;
    }
    _whenMethod(method, pattern, matchingPayload) {
        const entry = new BackendEntry(pattern, matchingPayload);
        this.backends[method].push(entry);
        return entry;
    }
    matchingPayloadRestriction(entry, request) {
        return entry.matchingPayload ? lodash__namespace.isEqual(entry.matchingPayload, request.body) : true;
    }
};
exports.HttpBackendService = __decorate([
    core.Injectable()
], exports.HttpBackendService);

/*
 * This is the place where the entries through HttpBackenService invocations are being used.
 * All outbound http requests are funneled through here, when a match from HttpBackenService entries
 * is found, the request is intercepted and the specified mock is returned with especified status code.
 * If no match is found, the http request is effectively sent over the wire
 */
exports.BackendInterceptor = class BackendInterceptor {
    constructor(httpBackendService, httpUtils, urlUtils, logService) {
        this.httpBackendService = httpBackendService;
        this.httpUtils = httpUtils;
        this.urlUtils = urlUtils;
        this.logService = logService;
    }
    intercept(request, next) {
        const backendMockRespond = this.httpBackendService.findMatchingMock(request);
        if (!backendMockRespond) {
            return next.handle(request);
        }
        let response;
        if (typeof backendMockRespond === 'object') {
            response = [200, lodash__namespace.cloneDeep(backendMockRespond)];
        }
        else {
            // if (typeof backendMockRespond === 'function')
            let data = null;
            if (request.method === 'GET') {
                data = decodeURIComponent(this.urlUtils.getQueryString(this.httpUtils.copyHttpParamsOrHeaders(request.params)));
            }
            else if (request.headers.get('Content-Type') === 'application/x-www-form-urlencoded') {
                data = request.body; // it is a query string
            }
            else if (request.method === 'POST' || request.method === 'PUT') {
                data = JSON.stringify(request.body);
            }
            const headers = this.httpUtils.copyHttpParamsOrHeaders(request.headers);
            response = backendMockRespond(request.method, decodeURIComponent(request.urlWithParams), data, headers);
        }
        this.logService.debug(`backend ${status} response for ${request.url}: `);
        return this.httpUtils.buildHttpResponse(request, response).pipe(operators.take(1));
    }
};
exports.BackendInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.HttpBackendService,
        HttpUtils,
        UrlUtils,
        LogService])
], exports.BackendInterceptor);

var FlawInjectionInterceptor_1;
/*
 * interceptor that will inject flaw into outbound and inbound http calls.
 * It is mainly used to validate reliability and consitency of test frameworks
 */
/** @internal */
let FlawInjectionInterceptor = FlawInjectionInterceptor_1 = class FlawInjectionInterceptor {
    constructor(httpUtils, logService) {
        this.httpUtils = httpUtils;
        this.logService = logService;
        this.flawWindow = window;
        this.flawWindow.allRequests = 0;
        this.flawWindow.flawedRequests = 0;
        this.flawWindow.allResponses = 0;
        this.flawWindow.flawedResponses = 0;
    }
    static registerRequestFlaw(mutation) {
        this.requestMutations.push(mutation);
    }
    static registerResponseFlaw(mutation) {
        this.responseMutations.push(mutation);
    }
    intercept(request, next) {
        if (FlawInjectionInterceptor_1.PROBABILITY !== 0 &&
            this.httpUtils.isCRUDRequest(request) &&
            !this._isGET(request)) {
            return this._handleFlawWindow(request, next);
        }
        return next.handle(request);
    }
    _isGET(config) {
        return config.method === 'GET';
    }
    _activateWithProbability(probabilityTrue) {
        return Math.random() >= 1.0 - probabilityTrue;
    }
    _generateResultResponse(request, result) {
        return result.pipe(operators.map((event) => {
            if (event instanceof http.HttpResponse &&
                this._activateWithProbability(FlawInjectionInterceptor_1.PROBABILITY)) {
                this.flawWindow.flawedResponses++;
                const responseMutation = FlawInjectionInterceptor_1.responseMutations.find((mutation) => mutation.test(request));
                if (responseMutation && event instanceof http.HttpResponse) {
                    this.logService.error(`FLAWED RESPONSE-"${request.url}`);
                    return responseMutation.mutate(event);
                }
            }
            return event;
        }));
    }
    _handleFlawWindow(request, next) {
        let result;
        this.flawWindow.allRequests++;
        if (this._activateWithProbability(FlawInjectionInterceptor_1.PROBABILITY)) {
            this.flawWindow.flawedRequests++;
            const requestMutation = FlawInjectionInterceptor_1.requestMutations.find((mutation) => mutation.test(request));
            if (requestMutation) {
                this.logService.error(`FLAWED REQUEST-"${request.url}`);
                result = next.handle(requestMutation.mutate(request));
            }
        }
        result = next.handle(request);
        return this._generateResultResponse(request, result);
    }
};
FlawInjectionInterceptor.requestMutations = [];
FlawInjectionInterceptor.responseMutations = [];
/*
 * probability of flaw occurrence ranging from 0 to 1
 */
FlawInjectionInterceptor.PROBABILITY = 0;
FlawInjectionInterceptor = FlawInjectionInterceptor_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [HttpUtils, LogService])
], FlawInjectionInterceptor);

/** @internal */
exports.FlawInjectionInterceptorModule = class FlawInjectionInterceptorModule {
};
exports.FlawInjectionInterceptorModule = __decorate([
    core.NgModule({
        imports: [],
        providers: [
            {
                provide: http.HTTP_INTERCEPTORS,
                useClass: FlawInjectionInterceptor,
                multi: true
            },
            {
                provide: core.APP_INITIALIZER,
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                useFactory() {
                    FlawInjectionInterceptor.registerRequestFlaw({
                        test: (request) => /sites\/[\w-]+\//.test(request.url),
                        mutate: (request) => request.clone({
                            url: request.url.replace(/sites\/([\w-]+)\//, 'sites/' + Math.random() + '/')
                        })
                    });
                    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                    return (component) => {
                        // an initializer useFactory must return a function
                    };
                },
                multi: true
            }
        ]
    })
], exports.FlawInjectionInterceptorModule);

exports.IAlertServiceType = void 0;
(function (IAlertServiceType) {
    IAlertServiceType["INFO"] = "information";
    IAlertServiceType["SUCCESS"] = "success";
    IAlertServiceType["WARNING"] = "warning";
    IAlertServiceType["DANGER"] = "error";
})(exports.IAlertServiceType || (exports.IAlertServiceType = {}));
class IAlertService {
    showAlert(alertConf) {
        'proxyFunction';
        return;
    }
    showInfo(alertConf) {
        'proxyFunction';
        return;
    }
    showDanger(alertConf) {
        'proxyFunction';
        return;
    }
    showWarning(alertConf) {
        'proxyFunction';
        return;
    }
    showSuccess(alertConf) {
        'proxyFunction';
        return;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc service
 * @name @smartutils.services:authenticationService
 *
 * @description
 * The authenticationService is used to authenticate and logout from SmartEdit.
 * It also allows the management of entry points used to authenticate the different resources in the application.
 *
 */
class IAuthenticationService {
    constructor() {
        this.reauthInProgress = {};
        this.initialized = false;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#authenticate
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Authenticates the current SmartEdit user against the entry point assigned to the requested resource. If no
     * suitable entry point is found, the resource will be authenticated against the
     * {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
     *
     * @param {String} resource The URI identifying the resource to access.
     * @returns {Promise} A promise that resolves if the authentication is successful.
     */
    authenticate(resource) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#logout
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * The logout method removes all stored authentication tokens and redirects to the
     * landing page.
     *
     */
    logout() {
        'proxyFunction';
        return Promise.resolve();
    }
    // abstract onLogout(_onLogout: () => void): void;
    // abstract onUserHasChanged(_onUserHasChanged: () => void): void;
    isReAuthInProgress(entryPoint) {
        'proxyFunction';
        return Promise.resolve(false);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#setReAuthInProgress
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Used to indicate that the user is currently within a re-authentication flow for the given entry point.
     * This flow is entered by default through authentication token expiry.
     *
     * @param {String} entryPoint The entry point which the user must be re-authenticated against.
     *
     */
    setReAuthInProgress(entryPoint) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#filterEntryPoints
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Will retrieve all relevant authentication entry points for a given resource.
     * A relevant entry point is an entry value of the authenticationMap found in {@link @smartutils.sharedDataService sharedDataService}.The key used in that map is a regular expression matching the resource.
     * When no entry point is found, the method returns the {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
     * @param {string} resource The URL for which a relevant authentication entry point must be found.
     */
    filterEntryPoints(resource) {
        'proxyFunction';
        return Promise.resolve([]);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService##isAuthEntryPoint
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Indicates if the resource URI provided is one of the registered authentication entry points.
     *
     * @param {String} resource The URI to compare
     * @returns {Boolean} Flag that will be true if the resource URI provided is an authentication entry point.
     */
    isAuthEntryPoint(resource) {
        'proxyFunction';
        return Promise.resolve(false);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService##isAuthenticated
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Indicates if the resource URI provided maps to a registered authentication entry point and the associated entry point has an authentication token.
     *
     * @param {String} resource The URI to compare
     * @returns {Boolean} Flag that will be true if the resource URI provided maps to an authentication entry point which has an authentication token.
     */
    isAuthenticated(url) {
        'proxyFunction';
        return Promise.resolve(false);
    }
}

/**
 * @ngdoc interface
 * @name @smartutils.interfaces:IStorageService
 * @description
 * Interface for StorageService
 */
class IStorageService {
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#isInitialized
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to determine if the storage service has been initialized properly. It
     * makes sure that the smartedit-sessions cookie is available in the browser.
     *
     * @returns {Boolean} Indicates if the storage service was properly initialized.
     */
    isInitialized() {
        'proxyFunction';
        return Promise.resolve(false);
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#storeAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method creates and stores a new key/value entry. It associates an authentication token with a
     * URI.
     *
     * @param {String} authURI The URI that identifies the resource(s) to be authenticated with the authToken. Will be used as a key.
     * @param {String} auth The token to be used to authenticate the user in the provided URI.
     */
    storeAuthToken(authURI, auth) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to retrieve the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which the associated authToken is to be retrieved.
     * @returns {String} The authToken used to authenticate the current user in the provided URI.
     */
    getAuthToken(authURI) {
        'proxyFunction';
        return Promise.resolve(undefined);
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#removeAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Removes the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which its authToken is to be removed.
     */
    removeAuthToken(authURI) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#removeAllAuthTokens
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method removes all authURI/authToken key/pairs from the storage service.
     */
    removeAllAuthTokens() {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getValueFromLocalStorage
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Retrieves the value stored in the cookie identified by the provided name.
     */
    getValueFromLocalStorage(cookieName, isEncoded) {
        'proxyFunction';
        return Promise.resolve();
    }
    setValueInLocalStorage(cookieName, value, encode) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#setItem
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to store the item.
     *
     * @param {String} key The key of the item.
     * @param {any} value The value of the item.
     */
    setItem(key, value) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getItem
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Retrieves the value for a given key.
     *
     * @param {String} key The key of the item.
     *
     * @returns {Promise<any>} A promise that resolves to the item value.
     */
    getItem(key) {
        'proxyFunction';
        return Promise.resolve();
    }
}

exports.ModalButtonStyle = void 0;
(function (ModalButtonStyle) {
    ModalButtonStyle["Default"] = "transparent";
    ModalButtonStyle["Primary"] = "emphasized";
})(exports.ModalButtonStyle || (exports.ModalButtonStyle = {}));
exports.ModalButtonAction = void 0;
(function (ModalButtonAction) {
    ModalButtonAction["Close"] = "close";
    ModalButtonAction["Dismiss"] = "dismiss";
    ModalButtonAction["None"] = "none";
})(exports.ModalButtonAction || (exports.ModalButtonAction = {}));

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISharedDataService
 *
 * @description
 * Provides an abstract extensible shared data service. Used to store any data to be used either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
class ISharedDataService {
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#get
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Get the data for the given key.
     *
     * @param {String} key The key of the data to fetch
     */
    get(key) {
        'proxyFunction';
        return Promise.resolve({});
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#set
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Set data for the given key.
     *
     * @param {String} key The key of the data to set
     * @param {object} value The value of the data to set
     */
    set(key, value) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#update
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Convenience method to retrieve and modify on the fly the content stored under a given key
     *
     * @param {String} key The key of the data to store
     * @param {Function} modifyingCallback callback fed with the value stored under the given key. The callback must return the new value of the object to update.
     */
    update(key, modifyingCallback) {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#remove
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Remove the entry for the given key.
     *
     * @param {String} key The key of the data to remove.
     * @returns {Promise<Cloneable>} A promise which resolves to the removed data for the given key.
     */
    remove(key) {
        'proxyFunction';
        return Promise.resolve({});
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#containsKey
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Checks the given key exists or not.
     *
     * @param {String} key The key of the data to check.
     * @returns {Promise<boolean>} A promise which resolves to true if the given key is found. Otherwise false.
     */
    containsKey(key) {
        'proxyFunction';
        return Promise.resolve(true);
    }
}

class ISettingsService {
    load() {
        'proxyFunction';
        return Promise.resolve({ key: '' });
    }
    get(key) {
        'proxyFunction';
        return Promise.resolve('');
    }
    getBoolean(key) {
        'proxyFunction';
        return Promise.resolve(true);
    }
    getStringList(key) {
        'proxyFunction';
        return Promise.resolve([]);
    }
}

class IModalService {
    dismissAll() {
        'proxyFunction';
    }
    open(conf) {
        'proxyFunction';
        return {};
    }
    hasOpenModals() {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISessionService
 * @description
 * The ISessionService provides information related to the current session
 * and the authenticated user (including a user readable and writeable languages).
 */
class ISessionService {
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the username, previously mentioned as "principalUID",
     * associated to the authenticated user.
     *
     * @returns {Promise<string>} A promise resolving to the username,
     * previously mentioned as "principalUID", associated to the
     * authenticated user.
     */
    getCurrentUsername() {
        'proxyFunction';
        return Promise.resolve('');
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUserDisplayName
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the displayed name associated to the authenticated user.
     *
     * @returns {Promise<string>} A promise resolving to the displayed name
     * associated to the authenticated user.
     */
    getCurrentUserDisplayName() {
        'proxyFunction';
        return Promise.resolve('');
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUser
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the data of the current authenticated user.
     * Also note that as part of the User object returned by this method contains
     * the list of readable and writeable languages available to the user.
     *
     * @returns {Promise<User>} A promise resolving to the data of the current
     * authenticated user.
     */
    getCurrentUser() {
        'proxyFunction';
        return Promise.resolve({});
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#hasUserChanged
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns boolean indicating whether the current user is different from
     * the last authenticated one.
     *
     * @returns {Promise<boolean>} Boolean indicating whether the current user is
     * different from the last authenticated one.
     */
    hasUserChanged() {
        'proxyFunction';
        return Promise.resolve(true);
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#resetCurrentUserData
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Reset all data associated to the authenticated user.
     * to the authenticated user.
     *
     * @return {Promise<void>} returns an empty promise.
     */
    resetCurrentUserData() {
        'proxyFunction';
        return Promise.resolve();
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#setCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Set the username, previously mentioned as "principalUID", associated
     * to the authenticated user.
     *
     * @return {Promise<void>} returns an empty promise.
     */
    setCurrentUsername() {
        'proxyFunction';
        return Promise.resolve();
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
class IAuthenticationManagerService {
}

const SERVER_ERROR_PREDICATE_HTTP_STATUSES = [500, 502, 503, 504];
const CLIENT_ERROR_PREDICATE_HTTP_STATUSES = [429];
const TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES = [408];
function serverErrorPredicate(request, response) {
    return response && lodash__namespace.includes(SERVER_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
function clientErrorPredicate(request, response) {
    return response && lodash__namespace.includes(CLIENT_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
function timeoutErrorPredicate(request, response) {
    return response && lodash__namespace.includes(TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
function retriableErrorPredicate(request, response) {
    return (response &&
        booleanUtils.isAnyTruthy(serverErrorPredicate, clientErrorPredicate, timeoutErrorPredicate)(request, response));
}
function noInternetConnectionErrorPredicate(request, response) {
    return response && response.status === 0;
}

const HTTP_METHODS_UPDATE = ['PUT', 'POST', 'DELETE', 'PATCH'];
const HTTP_METHODS_READ = ['GET', 'OPTIONS', 'HEAD'];
function updatePredicate(request, response) {
    return lodash__namespace.includes(HTTP_METHODS_UPDATE, request.method);
}
function readPredicate(request, response) {
    return lodash__namespace.includes(HTTP_METHODS_READ, request.method);
}

/**
 * @ngdoc object
 * @name @smartutils.object:EXPONENTIAL_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
const EXPONENTIAL_RETRY_DEFAULT_SETTING = {
    MAX_BACKOFF: 64000,
    MAX_ATTEMPT: 5,
    MIN_BACKOFF: 0
};
/**
 * @ngdoc service
 * @name @smartutils.services:exponentialRetry
 * @description
 * When used by a retry strategy, this service could provide an exponential delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
class ExponentialRetry {
    calculateNextDelay(attemptCount, maxBackoff, minBackoff) {
        maxBackoff = maxBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
        minBackoff = minBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
        const waveShield = minBackoff + Math.random();
        return Math.min(Math.pow(2, attemptCount) * 1000 + waveShield, maxBackoff);
    }
    canRetry(attemptCount, maxAttempt) {
        maxAttempt = maxAttempt || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}

/**
 * @ngdoc object
 * @name @smartutils.object:LINEAR_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
const LINEAR_RETRY_DEFAULT_SETTING = {
    MAX_ATTEMPT: 5,
    MAX_BACKOFF: 32000,
    MIN_BACKOFF: 0,
    RETRY_INTERVAL: 500
};
/**
 * @ngdoc service
 * @name @smartutils.services:linearRetry
 * @description
 * When used by a retry strategy, this service could provide a linear delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
class LinearRetry {
    calculateNextDelay(attemptCount, retryInterval, maxBackoff, minBackoff) {
        maxBackoff = maxBackoff || LINEAR_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
        minBackoff = minBackoff || LINEAR_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
        retryInterval = retryInterval || LINEAR_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;
        const waveShield = minBackoff + Math.random();
        return Math.min(attemptCount * retryInterval + waveShield, maxBackoff);
    }
    canRetry(attemptCount, maxAttempt) {
        maxAttempt = maxAttempt || LINEAR_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}

/**
 * @ngdoc object
 * @name @smartutils.object:SIMPLE_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
const SIMPLE_RETRY_DEFAULT_SETTING = {
    MAX_ATTEMPT: 5,
    MIN_BACKOFF: 0,
    RETRY_INTERVAL: 500
};
/**
 * @ngdoc service
 * @name @smartutils.services:simpleRetry
 * @description
 * When used by a retry strategy, this service could provide a simple fixed delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
class SimpleRetry {
    calculateNextDelay(retryInterval, minBackoff) {
        minBackoff = minBackoff || SIMPLE_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
        retryInterval = retryInterval || SIMPLE_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;
        const waveShield = minBackoff + Math.random();
        return retryInterval + waveShield;
    }
    canRetry(attemptCount, _maxAttempt) {
        const maxAttempt = _maxAttempt || SIMPLE_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const DefaultRetryStrategy = new core.InjectionToken('DefaultRetryStrategy');
function defaultRetryStrategyFactory(simpleRetry) {
    return class {
        constructor() {
            this.firstFastRetry = true;
            this.attemptCount = 0;
        }
        canRetry() {
            return simpleRetry.canRetry(this.attemptCount);
        }
        calculateNextDelay() {
            return simpleRetry.calculateNextDelay();
        }
    };
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ExponentialRetryStrategy = new core.InjectionToken('ExponentialRetryStrategy');
function exponentialRetryStrategyFactory(exponentialRetry) {
    return class {
        constructor() {
            this.firstFastRetry = true;
            this.attemptCount = 0;
        }
        canRetry() {
            return exponentialRetry.canRetry(this.attemptCount);
        }
        calculateNextDelay() {
            return exponentialRetry.calculateNextDelay(this.attemptCount);
        }
    };
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const LinearRetryStrategy = new core.InjectionToken('LinearRetryStrategy');
function linearRetryStrategyFactory(linearRetry) {
    return class {
        constructor() {
            this.firstFastRetry = true;
            this.attemptCount = 0;
        }
        canRetry() {
            return linearRetry.canRetry(this.attemptCount);
        }
        calculateNextDelay() {
            return linearRetry.calculateNextDelay(this.attemptCount);
        }
    };
}

/**
 * @ngdoc service
 * @name @smartutils.services:OperationContextService
 * @description
 * This service provides the functionality to register a url with its associated operation contexts and also finds operation context given an url.
 */
exports.OperationContextService = class OperationContextService {
    constructor() {
        this.store = [];
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#register
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Register a new url with it's associated operationContext.
     *
     * @param {String} url The url that is associated to the operation context.
     * @param {String} operationContext The operation context name that is associated to the given url.
     *
     * @return {Object} operationContextService The operationContextService service
     */
    register(url, operationContext) {
        if (typeof url !== 'string' || lodash__namespace.isEmpty(url)) {
            throw new Error('operationContextService.register error: url is invalid');
        }
        if (typeof operationContext !== 'string' || lodash__namespace.isEmpty(operationContext)) {
            throw new Error('operationContextService.register error: operationContext is invalid');
        }
        const regexIndex = this.store.findIndex((store) => store.urlRegex.test(url) === true && store.operationContext === operationContext);
        if (regexIndex !== -1) {
            return this;
        }
        const urlRegex = new RegExp(url.replace(/\/:[^\/]*/g, '/.*'));
        this.store.push({
            urlRegex,
            operationContext
        });
        return this;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#findOperationContext
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Find the first matching operation context for the given url.
     *
     * @param {String} url The request url.
     *
     * @return {String} operationContext
     */
    findOperationContext(url) {
        const regexIndex = this.store.findIndex((store) => store.urlRegex.test(url) === true);
        return ~regexIndex ? this.store[regexIndex].operationContext : null;
    }
};
exports.OperationContextService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [])
], exports.OperationContextService);

const operationContextName = 'OperationContextRegistered';
/**
 * @ngdoc object
 * @name @smartutils.object:@OperationContextRegistered
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} is delegated to
 * {@link @smartutils.services:OperationContextService OperationContextService.register} and it provides the functionality
 * to register an url with  operation context(s).
 *
 * For example:
 * 1. @OperationContextRegistered('apiUrl', ['CMS', 'INTERACTIVE'])
 * 2. @OperationContextRegistered('apiUrl', 'TOOLING')
 *
 * @param {string} url
 * @param {string | string[]} operationContext
 */
const OperationContextRegistered = annotationService.getClassAnnotationFactory(operationContextName);
function OperationContextAnnotationFactory(injector, operationContextService, OPERATION_CONTEXT) {
    'ngInject';
    return annotationService.setClassAnnotationFactory(operationContextName, function (factoryArguments) {
        return function (instance, originalConstructor, invocationArguments) {
            instance = new (originalConstructor.bind(instance))(...invocationArguments);
            const url = injector.get(factoryArguments[0], factoryArguments[0]);
            if (typeof factoryArguments[1] === 'string') {
                const operationContext = OPERATION_CONTEXT[factoryArguments[1]];
                operationContextService.register(url, operationContext);
            }
            else if (Array.isArray(factoryArguments[1]) && factoryArguments[1].length > 0) {
                factoryArguments[1].forEach((element) => {
                    operationContextService.register(url, OPERATION_CONTEXT[element]);
                });
            }
            return instance;
        };
    });
}

const OPERATION_CONTEXT_TOKEN = `${LIBRARY_NAME}_OPERATION_CONTEXT`;
/**
 * @ngdoc service
 * @name @smartutils.services:retryInterceptor
 *
 * @description
 * The retryInterceptor provides the functionality to register a set of predicates with their associated retry strategies.
 * Each time an HTTP request fails, the service try to find a matching retry strategy for the given response.
 */
exports.RetryInterceptor = class RetryInterceptor {
    constructor(httpClient, translate, operationContextService, alertService, booleanUtils, defaultRetryStrategy, exponentialRetryStrategy, linearRetryStrategy, OPERATION_CONTEXT) {
        this.httpClient = httpClient;
        this.translate = translate;
        this.operationContextService = operationContextService;
        this.alertService = alertService;
        this.OPERATION_CONTEXT = OPERATION_CONTEXT;
        this.TRANSLATE_NAMESPACE = 'se.gracefuldegradation.';
        this.predicatesRegistry = [];
        this.requestToRetryTegistry = {};
        this.register(noInternetConnectionErrorPredicate, exponentialRetryStrategy)
            .register(booleanUtils.isAnyTruthy(clientErrorPredicate, timeoutErrorPredicate), defaultRetryStrategy)
            .register(booleanUtils.areAllTruthy(readPredicate, retriableErrorPredicate), defaultRetryStrategy)
            .register(serverErrorPredicate, exponentialRetryStrategy);
    }
    predicate(request, response) {
        return this.findMatchingStrategy(request, response) !== null;
    }
    responseError(request, response) {
        let retryStrategy = this.retrieveRetryStrategy(request);
        if (!retryStrategy) {
            const StrategyHolder = this.findMatchingStrategy(request, response);
            if (StrategyHolder) {
                this.alertService.showWarning({
                    message: this.translate.instant(this.TRANSLATE_NAMESPACE + 'stillworking'),
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
                retryStrategy = new StrategyHolder();
                retryStrategy.attemptCount = 0;
                this.storeRetryStrategy(request, retryStrategy);
            }
            else {
                return Promise.reject(response);
            }
        }
        return this.handleRetry(retryStrategy, request, response);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:retryInterceptor#register
     * @methodOf @smartutils.services:retryInterceptor
     *
     * @description
     * Register a new predicate with it's associated strategyHolder.
     *
     * @param {Function} predicate This function takes the 'response' {Object} argument and an (optional) operationContext {String}. This function must return a Boolean that is true if the given response match the predicate.
     * @param {Function} retryStrategy This function will be instanciated at run-time. See {@link @smartutils.services:IRetryStrategy IRetryStrategy}.
     *
     * @return {Object} retryInterceptor The retryInterceptor service.
     *
     * @example
     * ```js
     *      var customPredicate = function(request, response, operationContext) {
     *          return response.status === 500 && operationContext === OPERATION_CONTEXT.TOOLING;
     *      };
     *      var StrategyHolder = function() {
     *          // set the firstFastRetry value to true for the retry made immediately only for the very first retry (subsequent retries will remain subject to the calculateNextDelay response)
     *          this.firstFastRetry = true;
     *      };
     *      StrategyHolder.prototype.canRetry = function() {
     *          // this function must return a {Boolean} if the given request must be retried.
     *          // use this.attemptCount value to determine if the function should return true or false
     *      };
     *      StrategyHolder.prototype.calculateNextDelay = function() {
     *          // this function must return the next delay time {Number}
     *          // use this.attemptCount value to determine the next delay value
     *      };
     *      retryInterceptor.register(customPredicate, StrategyHolder);
     * ```
     */
    register(predicate, retryStrategy) {
        if (typeof predicate !== 'function') {
            throw new Error('retryInterceptor.register error: predicate must be a function');
        }
        if (typeof retryStrategy !== 'function') {
            throw new Error('retryInterceptor.register error: retryStrategy must be a function');
        }
        this.predicatesRegistry.unshift({
            predicate,
            retryStrategy
        });
        return this;
    }
    /**
     * Find a matching strategy for the given response and (optional) operationContext
     * If not provided, the default operationContext is OPERATION_CONTEXT.INTERACTIVE
     *
     * @param {Object} response The http response object
     *
     * @return {Function} The matching retryStrategy
     */
    findMatchingStrategy(request, response) {
        const operationContext = this.operationContextService.findOperationContext(request.url) ||
            this.OPERATION_CONTEXT.INTERACTIVE;
        const matchStrategy = this.predicatesRegistry.find((predicateObj) => predicateObj.predicate(request, response, operationContext));
        return matchStrategy ? matchStrategy.retryStrategy : null;
    }
    handleRetry(retryStrategy, request, response) {
        retryStrategy.attemptCount++;
        if (retryStrategy.canRetry()) {
            const delay = retryStrategy.firstFastRetry ? 0 : retryStrategy.calculateNextDelay();
            retryStrategy.firstFastRetry = false;
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    this.httpClient
                        .request(request)
                        .toPromise()
                        .then((result) => {
                        this.removeRetryStrategy(request);
                        return resolve(result);
                    }, (error) => reject(error));
                }, delay);
            });
        }
        else {
            this.alertService.showDanger({
                message: this.translate.instant(this.TRANSLATE_NAMESPACE + 'somethingwrong'),
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            return Promise.reject(response);
        }
    }
    storeRetryStrategy(request, retryStrategy) {
        this.requestToRetryTegistry[this.getRequestUUID(request)] = retryStrategy;
    }
    removeRetryStrategy(request) {
        delete this.requestToRetryTegistry[this.getRequestUUID(request)];
    }
    retrieveRetryStrategy(request) {
        return this.requestToRetryTegistry[this.getRequestUUID(request)];
    }
    getRequestUUID(request) {
        return request.clone().toString();
    }
};
exports.RetryInterceptor = __decorate([
    core.Injectable(),
    __param(5, core.Inject(DefaultRetryStrategy)),
    __param(6, core.Inject(ExponentialRetryStrategy)),
    __param(7, core.Inject(LinearRetryStrategy)),
    __param(8, core.Inject(OPERATION_CONTEXT_TOKEN)),
    __metadata("design:paramtypes", [http.HttpClient,
        core$1.TranslateService,
        exports.OperationContextService,
        IAlertService,
        BooleanUtils, Object, Object, Object, Object])
], exports.RetryInterceptor);

/**
 * @ngdoc service
 * @name permissionErrorInterceptorModule.service:permissionErrorInterceptor
 * @description
 * Used for HTTP error code 403. Displays the alert message for permission error.
 */
exports.PermissionErrorInterceptor = class PermissionErrorInterceptor {
    constructor(alertService) {
        this.alertService = alertService;
    }
    predicate(request, response) {
        return response.status === 403;
    }
    responseError(request, response) {
        if (response.error && response.error.errors) {
            response.error.errors
                .filter((error) => error.type === 'TypePermissionError')
                .forEach((error) => {
                this.alertService.showDanger({
                    message: error.message,
                    duration: 10000,
                    minWidth: '',
                    mousePersist: true,
                    dismissible: true,
                    width: '300px'
                });
            });
        }
        return Promise.reject(response);
    }
};
exports.PermissionErrorInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IAlertService])
], exports.PermissionErrorInterceptor);

// map used by HttpAuthInterceptor to avoid replay identical requests being held because of 401
const GET_REQUESTS_ON_HOLD_MAP = {};
/**
 * @ngdoc service
 * @name @smartutils.services:unauthorizedErrorInterceptor
 * @description
 * Used for HTTP error code 401 (Forbidden). It will display the login modal.
 */
exports.UnauthorizedErrorInterceptor = class UnauthorizedErrorInterceptor {
    constructor(httpClient, authenticationService, promiseUtils, httpUtils, WHO_AM_I_RESOURCE_URI, eventService, logService) {
        this.httpClient = httpClient;
        this.authenticationService = authenticationService;
        this.promiseUtils = promiseUtils;
        this.httpUtils = httpUtils;
        this.eventService = eventService;
        this.logService = logService;
        this.promisesToResolve = {}; // key: auth entry point, value: array of deferred
        this.rejectedUrls = [/authenticate/];
        this.rejectedUrls.push(WHO_AM_I_RESOURCE_URI);
    }
    predicate(request, response) {
        return (response.status === 401 &&
            (request.url
                ? this.httpUtils.isCRUDRequest(request, response) &&
                    this.isUrlNotRejected(request.url)
                : true));
    }
    responseError(request, response) {
        const deferred = this.promiseUtils.defer();
        const deferredPromise = deferred.promise.then(() => this.httpClient.request(request).toPromise());
        this.authenticationService.isAuthEntryPoint(request.url).then((isAuthEntryPoint) => {
            if (!isAuthEntryPoint) {
                this.authenticationService
                    .filterEntryPoints(request.url)
                    .then((entryPoints) => {
                    const entryPoint = entryPoints[0];
                    this.promisesToResolve[entryPoint] =
                        this.promisesToResolve[entryPoint] || [];
                    this.promisesToResolve[entryPoint].push({
                        requestIdentifier: request.url,
                        deferred
                    });
                    if (this.httpUtils.isGET(request)) {
                        GET_REQUESTS_ON_HOLD_MAP[request.url] = deferredPromise;
                    }
                    this.authenticationService
                        .isReAuthInProgress(entryPoint)
                        .then((isReAuthInProgress) => {
                        if (!isReAuthInProgress) {
                            this.authenticationService
                                .setReAuthInProgress(entryPoint)
                                .then(() => {
                                const promisesToResolve = this.promisesToResolve;
                                this.eventService
                                    .publish(REAUTH_STARTED)
                                    .catch((reason) => {
                                    this.logService.debug(`UnauthorizedErrorInterceptor - responseError: ${reason}`);
                                });
                                this.authenticationService
                                    .authenticate(request.url)
                                    .then(function () {
                                    promisesToResolve[this].forEach((record) => {
                                        delete GET_REQUESTS_ON_HOLD_MAP[record.requestIdentifier];
                                        record.deferred.resolve();
                                    });
                                    promisesToResolve[this] = [];
                                }.bind(entryPoint), function () {
                                    promisesToResolve[this].forEach((record) => {
                                        delete GET_REQUESTS_ON_HOLD_MAP[record.requestIdentifier];
                                        record.deferred.reject();
                                    });
                                    promisesToResolve[this] = [];
                                }.bind(entryPoint));
                            });
                        }
                    });
                });
            }
            else {
                deferred.reject(response);
            }
        });
        return deferredPromise;
    }
    isUrlNotRejected(url) {
        return !this.rejectedUrls.some((rejectedUrl) => typeof rejectedUrl === 'string' ? url.indexOf(rejectedUrl) === 0 : rejectedUrl.test(url));
    }
};
exports.UnauthorizedErrorInterceptor = __decorate([
    core.Injectable(),
    __param(4, core.Inject(WHO_AM_I_RESOURCE_URI_TOKEN)),
    __param(5, core.Inject(EVENT_SERVICE)),
    __metadata("design:paramtypes", [http.HttpClient,
        IAuthenticationService,
        exports.PromiseUtils,
        HttpUtils, String, Object, LogService])
], exports.UnauthorizedErrorInterceptor);

/**
 * @ngdoc service
 * @name @smartutils.httpAuthInterceptor
 *
 * @description
 * Makes it possible to perform global authentication by intercepting requests before they are forwarded to the server
 * and responses before they are forwarded to the application code.
 *
 */
let HttpAuthInterceptor = class HttpAuthInterceptor {
    constructor(authenticationService, injector, httpUtils, I18N_RESOURCE_URI) {
        this.authenticationService = authenticationService;
        this.injector = injector;
        this.httpUtils = httpUtils;
        this.I18N_RESOURCE_URI = I18N_RESOURCE_URI;
    }
    /**
     * @ngdoc method
     * @name @smartutils.httpAuthInterceptor#request
     * @methodOf @smartutils.httpAuthInterceptor
     *
     * @description
     * Interceptor method which gets called with a http config object, intercepts any request made using httpClient service.
     * A call to any REST resource will be intercepted by this method, which then adds an authentication token to the request
     * and then forwards it to the REST resource.
     *
     */
    intercept(request, next) {
        if (!this._isCRUDRequest(request)) {
            return next.handle(request);
        }
        if (this._isGetRequest(request)) {
            return this._handleGetRequest(request);
        }
        return this._handleCUDRequest(request, next);
    }
    _isCRUDRequest(request) {
        return (!request.url.includes(this.I18N_RESOURCE_URI) && this.httpUtils.isCRUDRequest(request));
    }
    _isGetRequest(request) {
        return this.httpUtils.isGET(request) && !!GET_REQUESTS_ON_HOLD_MAP[request.url];
    }
    _handleGetRequest(request) {
        return new rxjs.Observable((obj) => {
            GET_REQUESTS_ON_HOLD_MAP[request.url].then((body) => {
                obj.next(new http.HttpResponse({ status: 200, body }));
            });
        });
    }
    _addAuthToResponse(request, next, entryPoints) {
        return rxjs.from(this.injector.get(IStorageService).getAuthToken(entryPoints[0])).pipe(operators.switchMap((authToken) => {
            if (!authToken) {
                return next.handle(request);
            }
            const authReq = request.clone({
                headers: request.headers.set('Authorization', authToken.token_type + ' ' + authToken.access_token)
            });
            return next.handle(authReq);
        }));
    }
    _handleCUDRequest(request, next) {
        return rxjs.from(this.authenticationService.filterEntryPoints(request.url)).pipe(operators.switchMap((entryPoints) => {
            if (!(entryPoints && entryPoints.length)) {
                return next.handle(request);
            }
            return this._addAuthToResponse(request, next, entryPoints);
        }));
    }
};
HttpAuthInterceptor = __decorate([
    core.Injectable(),
    __param(3, core.Inject(I18N_RESOURCE_URI_TOKEN)),
    __metadata("design:paramtypes", [IAuthenticationService,
        core.Injector,
        HttpUtils, String])
], HttpAuthInterceptor);

/**
 * @ngdoc service
 * @name @smartutils.services:httpErrorInterceptorService
 *
 * @description
 * The httpErrorInterceptorService provides the functionality to add custom HTTP error interceptors.
 * An interceptor can be an {Object} or an Angular Factory and must be represented by a pair of functions:
 * - predicate(request, response) {Function} that must return true if the response is associated to the interceptor. Important: The predicate must be designed to fulfill a specific function. It must not be defined for generic use.
 * - responseError(request, response) {Function} function called if the current response error matches the predicate. It must return a {Promise} with the resolved or rejected response.
 *
 * Each time an HTTP request fails, the service iterates through all registered interceptors. It sequentially calls the responseError function for all interceptors that have a predicate returning true for the current response error. If an interceptor modifies the response, the next interceptor that is called will have the modified response.
 * The last interceptor added to the service will be the first interceptor called. This makes it possible to override default interceptors.
 * If an interceptor resolves the response, the service service stops the iteration.
 */
exports.HttpErrorInterceptorService = class HttpErrorInterceptorService {
    constructor(injector, promiseUtils) {
        this.injector = injector;
        this.promiseUtils = promiseUtils;
        this._errorInterceptors = [];
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:httpErrorInterceptorService#addInterceptor
     * @methodOf @smartutils.services:httpErrorInterceptorService
     *
     * @description
     * Add a new error interceptor
     *
     * @param {Object|String} interceptor The interceptor {Object} or angular Factory
     *
     * @returns {Function} Function to call to unregister the interceptor from the service
     *
     * @example
     * ```js
     *      // Add a new interceptor with an instance of IHttpErrorInterceptor:
     *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor({
     *          predicate: function(request, response) {
     *              return response.status === 400;
     *          },
     *          responseError: function(request, response) {
     *              alertService.showDanger({
     *                  message: response.message
     *              });
     *              return Promise.reject(response);// FIXME: update doc
     *          }
     *      });
     *
     *      // Add an interceptor with a class of IHttpErrorInterceptor:
     *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor(CustomErrorInterceptor);
     *
     *      // Unregister the interceptor:
     *      unregisterCustomInterceptor();
     * ```
     */
    addInterceptors(interceptorClasses) {
        interceptorClasses.forEach((InterceptorClass) => {
            this.addInterceptor(InterceptorClass);
        });
    }
    addInterceptor(_interceptor) {
        let interceptor;
        if (_interceptor.predicate ||
            _interceptor.responseError) {
            interceptor = _interceptor;
        }
        else {
            interceptor = this.injector.get(_interceptor);
        }
        this._validateInterceptor(interceptor);
        this._errorInterceptors.unshift(interceptor);
        return () => {
            this._errorInterceptors.splice(this._errorInterceptors.indexOf(interceptor), 1);
        };
    }
    responseError(request, response) {
        const matchingErrorInterceptors = this._errorInterceptors.filter((errorInterceptor) => errorInterceptor.predicate(request, response) === true);
        const _interceptorsDeferred = this.promiseUtils.defer();
        if (matchingErrorInterceptors.length) {
            this._iterateErrorInterceptors(request.clone(), lodash__namespace.cloneDeep(response), matchingErrorInterceptors, _interceptorsDeferred);
        }
        else {
            _interceptorsDeferred.reject(response);
        }
        return rxjs.from(_interceptorsDeferred.promise);
    }
    _iterateErrorInterceptors(request, response, interceptors, _interceptorsDeferred, idx = 0) {
        if (idx === interceptors.length) {
            _interceptorsDeferred.reject(response);
        }
        else {
            const iterateFn = this._iterateErrorInterceptors.bind(this);
            // FIXME: fully convert this part to Observable chaining
            Promise.resolve(interceptors[idx].responseError(request, response)).then((interceptedResponse) => {
                _interceptorsDeferred.resolve(interceptedResponse);
            }, (interceptedResponse) => {
                iterateFn(request, interceptedResponse, interceptors, _interceptorsDeferred, ++idx);
            });
        }
    }
    /**
     * @ignore
     * Validate if the provided interceptor respects the Interface (predicate and responseError functions are mandatory).
     * @param {Object|String} interceptor The interceptor {Object} or angular Factory
     */
    _validateInterceptor(interceptor) {
        if (!interceptor.predicate || typeof interceptor.predicate !== 'function') {
            throw new Error('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.predicate');
        }
        if (!interceptor.responseError || typeof interceptor.responseError !== 'function') {
            throw new Error('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.responseError');
        }
    }
};
exports.HttpErrorInterceptorService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core.Injector, exports.PromiseUtils])
], exports.HttpErrorInterceptorService);

/**
 * @ngdoc overview
 * @name httpErrorInterceptorServiceModule
 *
 * @description
 * This module provides the functionality to add custom HTTP error interceptors.
 * Interceptors are used to execute code each time an HTTP request fails.
 */
let HttpErrorInterceptor = class HttpErrorInterceptor {
    constructor(httpErrorInterceptorService, httpUtils) {
        this.httpErrorInterceptorService = httpErrorInterceptorService;
        this.httpUtils = httpUtils;
    }
    intercept(request, next) {
        return next.handle(request).pipe(operators.catchError((error, caught) => {
            if (!this.httpUtils.isHTMLRequest(request, error)) {
                return this.httpErrorInterceptorService.responseError(request, error);
            }
            else {
                return rxjs.throwError(error);
            }
        }));
    }
};
HttpErrorInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.HttpErrorInterceptorService,
        HttpUtils])
], HttpErrorInterceptor);

var HttpInterceptorModule_1;
/**
 * @ngdoc overview
 * @name httpInterceptorModule
 *
 * @description
 * This module provides the functionality to add custom HTTP error interceptors.
 * Interceptors are used to execute code each time an HTTP request fails.
 *
 */
exports.HttpInterceptorModule = HttpInterceptorModule_1 = class HttpInterceptorModule {
    static forRoot(...HttpErrorInterceptorClasses) {
        return {
            ngModule: HttpInterceptorModule_1,
            providers: [
                ...HttpErrorInterceptorClasses,
                {
                    provide: http.HTTP_INTERCEPTORS,
                    useClass: HttpAuthInterceptor,
                    multi: true
                },
                {
                    provide: http.HTTP_INTERCEPTORS,
                    useClass: HttpErrorInterceptor,
                    multi: true
                },
                {
                    provide: http.HTTP_INTERCEPTORS,
                    useClass: exports.BackendInterceptor,
                    multi: true
                },
                {
                    provide: core.APP_BOOTSTRAP_LISTENER,
                    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                    useFactory(httpErrorInterceptorService) {
                        httpErrorInterceptorService.addInterceptors(HttpErrorInterceptorClasses);
                        // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                        return (component) => {
                            // an initializer useFactory must return a function
                        };
                    },
                    deps: [exports.HttpErrorInterceptorService],
                    multi: true
                }
            ]
        };
    }
};
exports.HttpInterceptorModule = HttpInterceptorModule_1 = __decorate([
    core.NgModule({
        imports: [exports.FlawInjectionInterceptorModule],
        providers: [
            SimpleRetry,
            LinearRetry,
            ExponentialRetry,
            {
                provide: DefaultRetryStrategy,
                useFactory: defaultRetryStrategyFactory,
                deps: [SimpleRetry]
            },
            {
                provide: ExponentialRetryStrategy,
                useFactory: exponentialRetryStrategyFactory,
                deps: [ExponentialRetry]
            },
            {
                provide: LinearRetryStrategy,
                useFactory: linearRetryStrategyFactory,
                deps: [SimpleRetry]
            },
            exports.HttpErrorInterceptorService,
            exports.HttpBackendService
        ]
    })
], exports.HttpInterceptorModule);

exports.StatusText = void 0;
(function (StatusText) {
    StatusText["UNKNOW_ERROR"] = "Unknown Error";
})(exports.StatusText || (exports.StatusText = {}));

class HttpPaginationResponseAdapter {
    static transform(ev) {
        const event = ev;
        const isAdaptable = event && event.body && event.body.pagination;
        if (!isAdaptable) {
            return event;
        }
        const foundKey = Object.keys(event.body).find((key) => key !== 'pagination' && Array.isArray(event.body[key]));
        return foundKey
            ? event.clone({ body: Object.assign(Object.assign({}, event.body), { results: event.body[foundKey] }) })
            : event;
    }
}

/**
 * @ngdoc service
 * @name @smartutils.services:responseAdapterInterceptor
 *
 * @description
 *
 * Interceptor used to normalize the response of paginated resources. Some API consumers select data with 'result' and 'response' key.
 * This interceptor purpose is to adapt such payload.
 */
exports.ResponseAdapterInterceptor = class ResponseAdapterInterceptor {
    intercept(request, next) {
        return next.handle(request).pipe(operators.map(HttpPaginationResponseAdapter.transform));
    }
};
exports.ResponseAdapterInterceptor = __decorate([
    core.Injectable()
], exports.ResponseAdapterInterceptor);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @internal */
class RestClient {
    constructor(httpClient, url, identifierName) {
        this.httpClient = httpClient;
        this.url = url;
        this.identifierName = identifierName;
        this.DEFAULT_HEADERS = { 'x-requested-with': 'Angular' };
        this.DEFAULT_OPTIONS = { headers: {} };
        // will activate response headers appending
        this.metadataActivated = false;
        /// ////////////////////////////////////////
        /// INTERNAL METHODS NEEDED FOR GATEWAY ///
        /// ////////////////////////////////////////
        this.getMethodForSingleInstance = (name) => {
            switch (name) {
                case 'getById':
                    return (id, options = this.DEFAULT_OPTIONS) => this.getById(id, options);
                case 'get':
                    return (searchParams, options = this.DEFAULT_OPTIONS) => this.get(searchParams, options);
                case 'update':
                    return (payload, options = this.DEFAULT_OPTIONS) => this.update(payload, options);
                case 'save':
                    return (payload, options = this.DEFAULT_OPTIONS) => this.save(payload, options);
                case 'patch':
                    return (payload, options = this.DEFAULT_OPTIONS) => this.patch(payload, options);
                case 'remove':
                    return (payload, options = this.DEFAULT_OPTIONS) => this.remove(payload, options);
                default:
                    return () => Promise.reject();
            }
        };
        this.getMethodForArray = (name) => {
            switch (name) {
                case 'query':
                    return (params, options = this.DEFAULT_OPTIONS) => this.query(params, options);
                default:
                    return () => Promise.reject();
            }
        };
    }
    getById(identifier, options = this.DEFAULT_OPTIONS) {
        return this.addHeadersToBody(this.httpClient.get(`${this.url}/${identifier}`, {
            headers: this.buildRequestHeaders(options.headers || {}),
            observe: 'response'
        })).toPromise();
    }
    get(searchParams, options = this.DEFAULT_OPTIONS) {
        const params = this.convertToTypeMapOfString(searchParams);
        return this.addHeadersToBody(this.httpClient.get(this.interpolateParamsInURL(this.url, params), {
            params: this.formatQueryString(this.determineTrueQueryStringParams(this.url, searchParams)),
            headers: this.buildRequestHeaders(options.headers || {}),
            observe: 'response'
        })).toPromise();
    }
    query(searchParams, options = this.DEFAULT_OPTIONS) {
        const params = searchParams ? this.convertToTypeMapOfString(searchParams) : searchParams;
        return this.addHeadersToBody(this.httpClient.get(this.interpolateParamsInURL(this.url, params), {
            params: this.formatQueryString(this.determineTrueQueryStringParams(this.url, searchParams)),
            headers: this.buildRequestHeaders(options.headers || {}),
            observe: 'response'
        }))
            .pipe(operators.map((arr) => arr || []))
            .toPromise();
    }
    page(pageable, options = this.DEFAULT_OPTIONS) {
        return (this.addHeadersToBody(this.httpClient.get(this.interpolateParamsInURL(this.url, pageable), {
            params: this.formatQueryString(this.determineTrueQueryStringParams(this.url, pageable)),
            headers: this.buildRequestHeaders(options.headers || {}),
            observe: 'response'
        }))
            // force typing to accept the fact that a page is never null
            .pipe(operators.map((arr) => arr))
            .toPromise());
    }
    update(payload, options = this.DEFAULT_OPTIONS) {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url) => this.httpClient
            .put(url, payload, {
            headers: this.buildRequestHeaders(options.headers || {})
        })
            .toPromise());
    }
    save(payload, options = this.DEFAULT_OPTIONS) {
        return this.httpClient
            .post(this.interpolateParamsInURL(this.url, payload), payload, {
            headers: this.buildRequestHeaders(options.headers || {})
        })
            .toPromise();
    }
    patch(payload, options = this.DEFAULT_OPTIONS) {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url) => this.httpClient
            .patch(url, payload, {
            headers: this.buildRequestHeaders(options.headers || {})
        })
            .toPromise());
    }
    remove(payload, options = this.DEFAULT_OPTIONS) {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url) => this.httpClient
            .delete(url, { headers: this.buildRequestHeaders(options.headers || {}) })
            .toPromise());
    }
    queryByPost(payload, searchParams, options = this.DEFAULT_OPTIONS) {
        const params = this.convertToTypeMapOfString(searchParams);
        return this.httpClient
            .post(this.interpolateParamsInURL(this.url, params), payload, {
            params: this.formatQueryString(this.determineTrueQueryStringParams(this.url, searchParams)),
            headers: this.buildRequestHeaders(options.headers || {})
        })
            .toPromise();
    }
    activateMetadata() {
        this.metadataActivated = true;
    }
    convertToTypeMapOfString(searchParams) {
        return lodash__namespace.mapValues(searchParams, (val) => String(val));
    }
    formatQueryString(_params) {
        return this.sortByKeys(_params);
    }
    addHeadersToBody(observable) {
        return observable.pipe(operators.map((response) => {
            const data = response.body;
            if (this.metadataActivated && data) {
                // used by @Cached annotation
                data.headers = response.headers;
            }
            return data;
        }));
    }
    /*
     * interpolation URL placeholders interpolation with potential matches in queryString
     */
    interpolateParamsInURL(_url, payload) {
        // only keep params to be found in the URI or query params
        if (payload && typeof payload !== 'string') {
            return new URIBuilder(_url).replaceParams(payload).sanitize().build();
        }
        else {
            return _url;
        }
    }
    /*
     * remove from queryString any param needed for URL placeholders interpolation
     */
    determineTrueQueryStringParams(url, payload) {
        return typeof payload === 'object'
            ? Object.keys(payload).reduce((prev, next) => {
                if (!new RegExp(':' + next + '/').test(url) &&
                    !new RegExp(':' + next + '$').test(url) &&
                    !new RegExp(':' + next + '&').test(url) &&
                    !lodash__namespace.isNil(payload[next])) {
                    prev[next] = payload[next];
                }
                return prev;
            }, {})
            : {};
    }
    sortByKeys(obj) {
        const keys = lodash__namespace.sortBy(lodash__namespace.keys(obj), (key) => key);
        return lodash__namespace.zipObject(keys, lodash__namespace.map(keys, (key) => obj[key]));
    }
    performIdentifierCheck(payload) {
        const identifier = typeof payload === 'string' ? payload : payload[this.identifierName];
        if (!identifier) {
            return Promise.reject('no data was found under the ' +
                identifier +
                ' field of object ' +
                JSON.stringify(payload) +
                ', it is necessary for update and remove operations');
        }
        return Promise.resolve();
    }
    buildUrlWithIdentifier(payload) {
        const identifier = typeof payload === 'string' ? payload : payload[this.identifierName];
        let url = this.interpolateParamsInURL(`${this.url}`, payload);
        url =
            url.includes('?') || this.url.includes(':' + this.identifierName)
                ? url
                : `${url}/${identifier}`;
        return Promise.resolve(url);
    }
    buildRequestHeaders(headers) {
        return new http.HttpHeaders(Object.assign(Object.assign({}, this.DEFAULT_HEADERS), headers));
    }
}

var RestServiceFactory_1;
/** @internal */
exports.RestServiceFactory = RestServiceFactory_1 = class RestServiceFactory {
    constructor(httpClient) {
        this.httpClient = httpClient;
        this.map = new Map();
        this.basePath = null;
        this.DOMAIN = null;
        this.IDENTIFIER = 'identifier';
    }
    static setGlobalBasePath(globalBasePath) {
        if (!RestServiceFactory_1.globalBasePath) {
            RestServiceFactory_1.globalBasePath = globalBasePath;
        }
        else {
            RestServiceFactory_1.logService.warn('The value of a global base path was already set. ' +
                'Update is not possible, the value remained unchanged!');
        }
    }
    static getGlobalBasePath() {
        return RestServiceFactory_1.globalBasePath ? RestServiceFactory_1.globalBasePath : '';
    }
    setDomain(DOMAIN) {
        this.DOMAIN = DOMAIN;
    }
    setBasePath(basePath) {
        this.basePath = basePath;
    }
    get(uri, identifier = this.IDENTIFIER) {
        if (this.map.has(uri + identifier)) {
            return this.map.get(uri + identifier);
        }
        if (!/^https?\:\/\//.test(uri)) {
            const newBasePath = this.getNewBasePath();
            const basePathURI = lodash__namespace.isEmpty(newBasePath)
                ? uri
                : newBasePath + (/^\//.test(uri) ? uri : `/${uri}`);
            uri = this.shouldAppendDomain(uri) ? `${this.DOMAIN}/${uri}` : basePathURI;
        }
        const restClient = new RestClient(this.httpClient, uri, identifier);
        this.map.set(uri + identifier, restClient);
        return restClient;
    }
    shouldAppendDomain(uri) {
        return !!this.DOMAIN && !/^\//.test(uri);
    }
    getNewBasePath() {
        return this.basePath ? this.basePath : RestServiceFactory_1.globalBasePath;
    }
};
exports.RestServiceFactory.globalBasePath = null;
exports.RestServiceFactory.logService = new LogService();
exports.RestServiceFactory = RestServiceFactory_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [http.HttpClient])
], exports.RestServiceFactory);

/**
 * @ngdoc service
 * @name @smartutils.service:AbstractCachedRestService
 *
 * @description
 * Base class to implement Cache enabled {@link @smartutils.interfaces:IRestService IRestServices}.
 * <br/>Implementing classes just need declare a class level {@link @smartutils.object:@CacheConfig @CacheConfig} annotation
 * with at least one {@link @smartutils.object:CacheAction CacheAction} and one {@link @smartutils.object:EvictionTag EvictionTag}.
 * <br/>Cache policies called by the set of {@link @smartutils.object:CacheAction CacheActions} will have access to
 * REST call response headers being added to the response under "headers" property.
 * <br/>Those headers are then stripped from the response.
 *
 * <h2>Usage</h2>
 * <pre>
 * &#64;CacheConfig({actions: [rarelyChangingContent], tags: [userEvictionTag]})
 * &#64;SeInjectable()
 * export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
 * 	constructor(restServiceFactory: IRestServiceFactory) {
 * 		super(restServiceFactory, '/productcatalogs');
 * 	}
 * }
 * </pre>
 */
class AbstractCachedRestService {
    constructor(restServiceFactory, uri, identifier) {
        this.innerRestService = restServiceFactory.get(uri, identifier);
        this.innerRestService.activateMetadata && this.innerRestService.activateMetadata();
    }
    getById(identifier, options) {
        return this.innerRestService.getById(identifier, options);
    }
    get(searchParams, options) {
        return this.innerRestService.get(searchParams, options);
    }
    query(searchParams, options) {
        return this.innerRestService.query(searchParams, options);
    }
    page(searchParams, options) {
        return this.innerRestService.page(searchParams, options);
    }
    update(payload, options) {
        return this.innerRestService.update(payload, options);
    }
    patch(payload, options) {
        return this.innerRestService.patch(payload, options);
    }
    remove(payload, options) {
        return this.innerRestService.remove(payload, options);
    }
    save(payload, options) {
        return this.innerRestService.save(payload, options);
    }
    queryByPost(payload, searchParams, options) {
        return this.innerRestService.queryByPost(payload, searchParams, options);
    }
}
__decorate([
    StripResponseHeaders,
    Cached(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "getById", null);
__decorate([
    StripResponseHeaders,
    Cached(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "get", null);
__decorate([
    StripResponseHeaders,
    Cached(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "query", null);
__decorate([
    StripResponseHeaders,
    Cached(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "page", null);
__decorate([
    StripResponseHeaders,
    InvalidateCache(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "update", null);
__decorate([
    StripResponseHeaders,
    InvalidateCache(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "patch", null);
__decorate([
    InvalidateCache(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "remove", null);
__decorate([
    StripResponseHeaders,
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "save", null);
__decorate([
    StripResponseHeaders,
    Cached(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object, Object]),
    __metadata("design:returntype", Promise)
], AbstractCachedRestService.prototype, "queryByPost", null);
function StripResponseHeaders(target, propertyName, descriptor) {
    const originalMethod = descriptor.value;
    if (originalMethod) {
        descriptor.value = function (...args) {
            return originalMethod.apply(this, args).then((response) => {
                delete response.headers;
                return response;
            });
        };
    }
}

var SSOAuthenticationHelper_1;
const SSO_DIALOG_MARKER = 'sso';
var SSO_PROPERTIES;
(function (SSO_PROPERTIES) {
    SSO_PROPERTIES["SSO_CLIENT_ID"] = "SSO_CLIENT_ID";
    SSO_PROPERTIES["SSO_AUTHENTICATION_ENTRY_POINT"] = "SSO_AUTHENTICATION_ENTRY_POINT";
    SSO_PROPERTIES["SSO_LOGOUT_ENTRY_POINT"] = "SSO_LOGOUT_ENTRY_POINT";
    SSO_PROPERTIES["SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT"] = "SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT";
})(SSO_PROPERTIES || (SSO_PROPERTIES = {}));
const CHILD_SMARTEDIT_SENDING_AUTHTOKEN = 'ssoAuthenticate';
const CHILD_SMARTEDIT_SENDING_AUTH_ERROR = 'ssoAuthenticateError';
const SSODIALOG_WINDOW = 'SSODIALOG_WINDOW';
/*
 * Helper to initiate a SAML /SSO autentication sequence through a pop-up
 * (because the sequence involves auto-submiting html form at some point that causes a redirect and hence would
 * loose app context if not executed in a different window)
 * that ultimately loads the app again which in turn will detect its context and do the following:
 * - will not continue loading
 * - wil post the loginToken to the /authenticate end point to retrieve oAuth access
 * - will send back to parent (through postMessage) the retrieved oAuth access
 * - will close;
 */
exports.SSOAuthenticationHelper = SSOAuthenticationHelper_1 = class SSOAuthenticationHelper {
    constructor(windowUtils, promiseUtils, httpClient, injector) {
        this.windowUtils = windowUtils;
        this.promiseUtils = promiseUtils;
        this.httpClient = httpClient;
        this.injector = injector;
        this.logoutIframeId = 'logoutIframe';
        this.deferred = null;
        this.listenForAuthTokenBeingSentBack();
    }
    /*
     * Initiates the SSO dialog through a pop-up
     */
    launchSSODialog() {
        this.deferred = this.promiseUtils.defer();
        const ssoAuthenticationEntryPoint = this.injector.get(SSO_PROPERTIES.SSO_AUTHENTICATION_ENTRY_POINT) +
            this.getSSOContextPath();
        this.window.open(ssoAuthenticationEntryPoint, SSODIALOG_WINDOW, 'toolbar=no,scrollbars=no,resizable=no,top=200,left=200,width=1000,height=800');
        return this.deferred.promise;
    }
    /*
     * SSO happen in a popup window launched by AuthenticationHelper#launchSSODialog().
     * Once SSO is successful, a 'LoginToken' cookie is present, this is a pre-requisite for doing a POST to the /authenticate
     * endpoint that will return the Authorization bearer token.
     * This bearer is then sent with postMessage to the opener window, i.e. the SmartEdit application that will resume the pending 401 request.
     */
    completeDialog() {
        return new Promise((resolve, reject) => {
            this.httpClient
                .post(this.injector.get(SSO_PROPERTIES.SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT), { client_id: this.injector.get(SSO_PROPERTIES.SSO_CLIENT_ID) })
                .subscribe((authToken) => {
                this.window.opener.postMessage({
                    eventId: CHILD_SMARTEDIT_SENDING_AUTHTOKEN,
                    authToken
                }, this.document.location.origin);
                this.window.close();
                resolve();
            }, (httpErrorResponse) => {
                const clonableHttpErrorResponse = {
                    error: httpErrorResponse.error,
                    status: httpErrorResponse.status
                };
                this.window.opener.postMessage({
                    eventId: CHILD_SMARTEDIT_SENDING_AUTH_ERROR,
                    error: clonableHttpErrorResponse
                }, this.document.location.origin);
                this.window.close();
                reject();
            });
        });
    }
    /*
     * case of the App being a popup only meant for authentication and spun up buy the main app
     */
    isSSODialog() {
        return (this.window.name === SSODIALOG_WINDOW &&
            new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search));
    }
    /*
     * case of:
     * - the App called from another app in an SSO context and that should therefore auto-authenticate with SSO
     * - last manual authentication was with SSO
     */
    isAutoSSOMain() {
        return (SSOAuthenticationHelper_1.lastAuthenticatedWithSSO ||
            (this.window.name !== SSODIALOG_WINDOW &&
                new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search)));
    }
    logout() {
        let logoutIframe = this.logoutIframe;
        if (!logoutIframe) {
            logoutIframe = this.document.createElement('iframe');
            logoutIframe.id = this.logoutIframeId;
            logoutIframe.style.display = 'none';
            this.document.body.appendChild(logoutIframe);
        }
        logoutIframe.src = this.injector.get(SSO_PROPERTIES.SSO_LOGOUT_ENTRY_POINT);
        SSOAuthenticationHelper_1.lastAuthenticatedWithSSO = false;
        this.document.location.href = this.document.location.href.replace(this.getSSOContextPath(), this.document.location.pathname);
    }
    // context path of app in an SSO mode
    getSSOContextPath() {
        return `${this.document.location.pathname}?${SSO_DIALOG_MARKER}`;
    }
    listenForAuthTokenBeingSentBack() {
        this.window.addEventListener('message', (event) => {
            if (event.origin !== document.location.origin) {
                return;
            }
            this.logoutIframe && this.logoutIframe.remove();
            if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTHTOKEN) {
                SSOAuthenticationHelper_1.lastAuthenticatedWithSSO = true;
                this.deferred && this.deferred.resolve(event.data.authToken);
            }
            else if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTH_ERROR) {
                this.deferred && this.deferred.reject(event.data.error);
            }
        }, false);
    }
    get window() {
        return this.windowUtils.getWindow();
    }
    get document() {
        return this.window.document;
    }
    get logoutIframe() {
        return this.document.querySelector(`iframe#${this.logoutIframeId}`);
    }
};
// static in order to be shared by multiple instances
exports.SSOAuthenticationHelper.lastAuthenticatedWithSSO = false;
exports.SSOAuthenticationHelper = SSOAuthenticationHelper_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [WindowUtils,
        exports.PromiseUtils,
        http.HttpClient,
        core.Injector])
], exports.SSOAuthenticationHelper);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const LoginDialogResourceProvider = new core.InjectionToken('LoginDialogResourceProvider');

exports.LoginDialogComponent = class LoginDialogComponent {
    constructor(modalRef, logService, httpClient, sessionService, storageService, urlUtils, ssoAuthenticationHelper, resource, eventService) {
        this.modalRef = modalRef;
        this.logService = logService;
        this.httpClient = httpClient;
        this.sessionService = sessionService;
        this.storageService = storageService;
        this.urlUtils = urlUtils;
        this.ssoAuthenticationHelper = ssoAuthenticationHelper;
        this.resource = resource;
        this.eventService = eventService;
        this.hostClass = true;
        this.data = null;
        this.authURIKey = '';
        this.authURI = '';
        this.isFullScreen = false;
        this.ssoEnabled = false;
        this.form = new forms.FormGroup({
            username: new forms.FormControl('', forms.Validators.required),
            password: new forms.FormControl('', forms.Validators.required)
        });
        this.signinWithSSO = () => {
            this.form.setErrors(null);
            return this.ssoAuthenticationHelper
                .launchSSODialog()
                .then((data) => this.storeAccessToken(data), (error) => this.apiAuthenticationFailureReportFactory(error))
                .then((userHasChanged) => this.acceptUser(userHasChanged))
                .catch((reason) => {
                this.logService.debug(`LoginDialogComponent - signinWithSSO: ${JSON.stringify(reason)}`);
            });
        };
        this.sendCredentials = (payload) => this.httpClient
            .request('POST', this.authURI, {
            headers: new http.HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
            body: this.urlUtils.getQueryString(payload).replace('?', ''),
            observe: 'response'
        })
            .toPromise();
    }
    ngOnInit() {
        this.data = this.modalRef.data || {};
        this.authURI = this.data.authURI;
        this.isFullScreen = this.data.isFullScreen;
        this.ssoEnabled = this.data.ssoEnabled && this.isMainEndPoint();
        this.storageService.removeAuthToken(this.authURI);
        this.authURIKey = Buffer.from(this.authURI).toString('base64').replace(/=/g, '');
        if (this.ssoAuthenticationHelper.isAutoSSOMain()) {
            this.signinWithSSO();
        }
    }
    signinWithCredentials() {
        this.form.setErrors(null);
        if (this.hasRequiredCredentialsError()) {
            this.form.setErrors({
                credentialsRequiredError: 'se.logindialogform.username.and.password.required'
            });
            this.logService.debug(`LoginDialogComponent - signinWithCredentials: hasRequiredCredentialsError`);
            return Promise.resolve();
        }
        const payload = Object.assign(Object.assign({}, (this.data.clientCredentials || {})), { username: this.form.get('username').value, password: this.form.get('password').value, grant_type: 'password' });
        return this.sendCredentials(payload)
            .then((response) => this.storeAccessToken(response), (error) => this.apiAuthenticationFailureReportFactory(error))
            .then((hasUserChanged) => this.acceptUser(hasUserChanged))
            .catch((reason) => {
            this.logService.debug(`LoginDialogComponent - signinWithCredentials: ${JSON.stringify(reason)}`);
        });
    }
    isMainEndPoint() {
        return DEFAULT_AUTHENTICATION_ENTRY_POINT === this.authURI;
    }
    storeAccessToken(response) {
        const token = response instanceof http.HttpResponse ? response.body : response;
        this.storageService.storeAuthToken(this.authURI, token);
        this.eventService.publish(EVENTS.TOKEN_STORED);
        this.logService.debug(`API Authentication Success: ${this.authURI}`);
        return this.isMainEndPoint()
            ? this.sessionService.hasUserChanged()
            : Promise.resolve(false);
    }
    apiAuthenticationFailureReportFactory(error) {
        this.logService.debug(`API Authentication Failure: ${this.authURI} status: ${error.status}`);
        this.form.setErrors({
            asyncValidationError: (error.error && error.error.error_description) ||
                'se.logindialogform.oauth.error.default'
        });
        return Promise.reject(error);
    }
    acceptUser(userHasChanged) {
        this.modalRef.close({
            userHasChanged
        });
        if (this.isMainEndPoint()) {
            this.sessionService.setCurrentUsername();
        }
        return Promise.resolve({ userHasChanged });
    }
    hasRequiredCredentialsError() {
        const username = this.form.get('username');
        const password = this.form.get('password');
        return ((username.errors && username.errors.required) ||
            (password.errors && password.errors.required));
    }
};
__decorate([
    core.HostBinding('class.su-login-dialog'),
    __metadata("design:type", Object)
], exports.LoginDialogComponent.prototype, "hostClass", void 0);
exports.LoginDialogComponent = __decorate([
    core.Component({
        selector: 'su-login-dialog',
        styles: [`.su-login{width:500px;min-height:440px;box-shadow:0 1px 4px 0 rgba(var(--sapBlockLayer_Background) .1);background-color:var(--sapBaseColor);border-radius:4px;border:1px solid rgba(var(--sapContent_ShadowColor),.2);padding:20px;margin:120px auto 0 auto}.su-login--wrapper{padding:40px 100px;width:100%}.su-login--form-group{margin-bottom:20px}.su-login--form-sso{margin-top:20px}.su-login--logo-wrapper{display:flex;justify-content:flex-start;align-items:center}.su-login--logo-wrapper.su-login--logo-wrapper_full{padding-bottom:45px}.su-login--logo{height:44px}.su-login--logo-text{font-family:"72","72full",Arial,Helvetica,sans-serif;font-size:1.5rem;line-height:1.3333333333;font-weight:400;padding-left:16px;color:var(--sapTextColor);font-weight:700}.su-login--logo__best-run{position:absolute;bottom:30px;left:30px}.su-login--btn{font-size:.875rem;line-height:1.4285714286;font-weight:400;width:100%}.su-login--auth-message{padding-top:20px;padding-bottom:20px;font-size:.875rem;line-height:1.4285714286;font-weight:400}.su-login--alert-error{margin-bottom:0;box-shadow:none}`],
        template: `<div class="su-login-dialog-container"><div class="fd-dialog__content fd-dialog__content--overrides" role="document"><div class="su-login"><div class="su-login--wrapper"><div class="su-login--logo-wrapper" [ngClass]="{ 'su-login--logo-wrapper_full': !isFullScreen }"><img *ngIf="resource?.topLogoURL" [src]="resource?.topLogoURL" class="su-login--logo" title="{{ 'se.logo.title' | translate }}"/> <span class="su-login--logo-text" translate="se.application.name"></span></div><form autocomplete="off" novalidate [formGroup]="form" (submit)="signinWithCredentials()" class="su-login--form"><div class="su-login--auth-message" *ngIf="isFullScreen"><div translate="se.logindialogform.reauth.message1"></div><div translate="se.logindialogform.reauth.message2"></div></div><div *ngIf="form.errors" class="su-login--form-group"><fd-message-strip type="error" class="su-login--alert-error" id="invalidError" [dismissible]="false"><ng-container *ngIf="form.errors?.credentialsRequiredError">{{ form.errors.credentialsRequiredError | translate }}</ng-container><ng-container *ngIf="form.errors?.asyncValidationError">{{ form.errors.asyncValidationError | translate }}</ng-container></fd-message-strip></div><div class="su-login--form-group"><input fd-form-control type="text" id="username_{{ authURIKey }}" name="username" formControlName="username" placeholder="{{ 'se.authentication.form.input.username' | translate }}" autofocus autocomplete="off" class="fd-input"/></div><div class="su-login--form-group"><input fd-form-control type="password" id="password_{{ authURIKey }}" name="password" placeholder="{{ 'se.authentication.form.input.password' | translate }}" formControlName="password" autocomplete="off" required class="fd-input"/></div><div class="su-login--form-group"><su-language-dropdown class="su-login-language"></su-language-dropdown></div><button fd-button options="emphasized" class="fd-button--emphasized su-login--btn" id="submit_{{ authURIKey }}" name="submit" type="submit" label="{{ 'se.authentication.form.button.submit' | translate }}"></button></form><form *ngIf="ssoEnabled" class="su-login--form-sso" name="loginDialogFormSSO" novalidate (submit)="signinWithSSO()"><button fd-button options="emphasized" class="fd-button--emphasized su-login--btn" id="submitSSO_{{ authURIKey }}" name="submitSSO" type="submit" label="{{ 'se.authentication.form.button.submit.sso' | translate }}"></button></form></div></div><img *ngIf="resource?.bottomLogoURL" [src]="resource?.bottomLogoURL" class="su-login--logo__best-run" title="{{ 'se.logo.title' | translate }}"/></div></div>`
    }),
    __param(7, core.Optional()), __param(7, core.Inject(LoginDialogResourceProvider)),
    __param(8, core.Inject(EVENT_SERVICE)),
    __metadata("design:paramtypes", [core$2.DialogRef,
        LogService,
        http.HttpClient,
        ISessionService,
        IStorageService,
        UrlUtils,
        exports.SSOAuthenticationHelper, Object, Object])
], exports.LoginDialogComponent);

/**
 * @module blabla
 */
class ITranslationsFetchService {
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
exports.SUPPORTED_BROWSERS = void 0;
(function (SUPPORTED_BROWSERS) {
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["IE"] = 0] = "IE";
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["CHROME"] = 1] = "CHROME";
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["FIREFOX"] = 2] = "FIREFOX";
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["EDGE"] = 3] = "EDGE";
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["SAFARI"] = 4] = "SAFARI";
    SUPPORTED_BROWSERS[SUPPORTED_BROWSERS["UNKNOWN"] = 5] = "UNKNOWN";
})(exports.SUPPORTED_BROWSERS || (exports.SUPPORTED_BROWSERS = {}));
class BrowserService {
    constructor() {
        /*
            It is always better to detect a browser via features. Unfortunately, it's becoming really hard to identify
            Safari, since newer versions do not match the previous ones. Thus, we have to rely on User Agent as the last
            option.
        */
        this._isSafari = () => {
            const { userAgent } = window.navigator;
            const vendor = window.navigator.vendor;
            return (vendor && vendor.indexOf('Apple') > -1 && userAgent && userAgent.indexOf('Safari') > -1);
        };
        this.isIE = () => this.getCurrentBrowser() === exports.SUPPORTED_BROWSERS.IE;
        this.isFF = () => this.getCurrentBrowser() === exports.SUPPORTED_BROWSERS.FIREFOX;
        this.isSafari = () => this.getCurrentBrowser() === exports.SUPPORTED_BROWSERS.SAFARI;
    }
    getCurrentBrowser() {
        /* forbiddenNameSpaces window as any:false */
        const anyWindow = window;
        let browser = exports.SUPPORTED_BROWSERS.UNKNOWN;
        if (typeof anyWindow.InstallTrigger !== 'undefined') {
            browser = exports.SUPPORTED_BROWSERS.FIREFOX;
        }
        else if ( /* @cc_on!@*/!!document.documentMode) {
            browser = exports.SUPPORTED_BROWSERS.IE;
        }
        else if (!!anyWindow.StyleMedia) {
            browser = exports.SUPPORTED_BROWSERS.EDGE;
        }
        else if (!!anyWindow.chrome && !!anyWindow.chrome.webstore) {
            browser = exports.SUPPORTED_BROWSERS.CHROME;
        }
        else if (this._isSafari()) {
            browser = exports.SUPPORTED_BROWSERS.SAFARI;
        }
        return browser;
    }
    getBrowserLocale() {
        const locale = window.navigator.language.split('-');
        return locale.length === 1 ? locale[0] : `${locale[0]}_${locale[1].toUpperCase()}`;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @internal */
class FingerPrintingService {
    constructor() {
        this._fingerprint = stringUtils.encode(this.getUserAgent() +
            this.getPlugins() +
            this.hasJava() +
            this.hasFlash() +
            this.hasLocalStorage() +
            this.hasSessionStorage() +
            this.hasCookie() +
            this.getTimeZone() +
            this.getLanguage() +
            this.getSystemLanguage() +
            this.hasCanvas());
    }
    /**
     * Get unique browser fingerprint information encoded in Base64.
     */
    getFingerprint() {
        return this._fingerprint;
    }
    getUserAgent() {
        return navigator.userAgent;
    }
    getPlugins() {
        const plugins = [];
        for (let i = 0, l = navigator.plugins.length; i < l; i++) {
            if (navigator.plugins[i]) {
                plugins.push(navigator.plugins[i].name);
            }
        }
        return plugins.join(',');
    }
    hasJava() {
        return navigator.javaEnabled();
    }
    hasFlash() {
        return !!navigator.plugins.namedItem('Shockwave Flash');
    }
    hasLocalStorage() {
        try {
            const hasLs = 'test-has-ls';
            localStorage.setItem(hasLs, hasLs);
            localStorage.removeItem(hasLs);
            return true;
        }
        catch (exception) {
            return false;
        }
    }
    hasSessionStorage() {
        try {
            const hasSs = 'test-has-ss';
            sessionStorage.setItem(hasSs, hasSs);
            sessionStorage.removeItem(hasSs);
            return true;
        }
        catch (exception) {
            return false;
        }
    }
    hasCookie() {
        return navigator.cookieEnabled;
    }
    getTimeZone() {
        return String(String(new Date()).split('(')[1]).split(')')[0];
    }
    getLanguage() {
        return navigator.language;
    }
    getSystemLanguage() {
        return window.navigator.language;
    }
    hasCanvas() {
        try {
            const elem = document.createElement('canvas');
            return !!(elem.getContext && elem.getContext('2d'));
        }
        catch (e) {
            return false;
        }
    }
}

/* @internal */
let TranslateHttpLoader = class TranslateHttpLoader {
    constructor(translationsFetchService) {
        this.translationsFetchService = translationsFetchService;
    }
    /**
     * Gets the translations from the server
     */
    getTranslation(lang) {
        return rxjs.from(this.translationsFetchService.get(lang));
    }
};
TranslateHttpLoader = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [ITranslationsFetchService])
], TranslateHttpLoader);

var TranslationModule_1;
exports.TranslationModule = TranslationModule_1 = class TranslationModule {
    static forChild() {
        return core$1.TranslateModule.forChild({
            isolate: false,
            loader: {
                provide: core$1.TranslateLoader,
                useClass: TranslateHttpLoader
            }
        });
    }
    static forRoot(TranslationsFetchServiceClass) {
        return {
            ngModule: TranslationModule_1,
            providers: [
                {
                    provide: ITranslationsFetchService,
                    useClass: TranslationsFetchServiceClass
                },
                {
                    provide: core.APP_INITIALIZER,
                    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                    useFactory(translate, storageService, browserService) {
                        storageService
                            .getValueFromLocalStorage('SELECTED_LANGUAGE', false)
                            .then((lang) => lang ? lang.isoCode : browserService.getBrowserLocale(), () => browserService.getBrowserLocale())
                            .then((lang) => {
                            translate.setDefaultLang(lang);
                            translate.use(lang);
                        });
                        // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                        return (component) => {
                            // an initializer useFactory must return a function
                        };
                    },
                    deps: [core$1.TranslateService, IStorageService, BrowserService],
                    multi: true
                }
            ]
        };
    }
};
exports.TranslationModule = TranslationModule_1 = __decorate([
    core.NgModule({
        imports: [
            core$1.TranslateModule.forRoot({
                isolate: false,
                loader: {
                    provide: core$1.TranslateLoader,
                    useClass: TranslateHttpLoader
                }
            })
        ],
        exports: [core$1.TranslateModule]
    })
], exports.TranslationModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
class LanguageDropdownAdapter {
    static transform(item, id) {
        return {
            id,
            label: item.name,
            value: item
        };
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:LANGUAGE_RESOURCE_URI
 *
 * @description
 * Resource URI of the languages REST service.
 */
/**
 * @ngdoc service
 * @name @smartutils.services:LanguageService
 */
exports.LanguageService = class LanguageService {
    constructor(logService, translateService, promiseUtils, eventService, browserService, storageService, injector, languageServiceConstants) {
        this.logService = logService;
        this.translateService = translateService;
        this.promiseUtils = promiseUtils;
        this.eventService = eventService;
        this.browserService = browserService;
        this.storageService = storageService;
        this.injector = injector;
        this.languageServiceConstants = languageServiceConstants;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getToolingLanguages
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Retrieves a list of language descriptors using REST calls to the i18n API.
     *
     * @returns {Promise<IToolingLanguage[]>} A promise that resolves to an array of IToolingLanguage.
     */
    getToolingLanguages() {
        return this.i18nLanguageRestService
            .get({})
            .then((response) => response.languages)
            .catch((error) => {
            this.logService.error('LanguageService.getToolingLanguages() - Error loading tooling languages');
            return Promise.reject(error);
        });
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getBrowserLanguageIsoCode
     * @methodOf @smartutils.services:LanguageService
     *
     * @deprecated since 1808
     *
     * @description
     * Uses the browser's current locale to determine the selected language ISO code.
     *
     * @returns {String} The language ISO code of the browser's currently selected locale.
     */
    getBrowserLanguageIsoCode() {
        return window.navigator.language.split('-')[0];
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getBrowserLocale
     * @methodOf @smartutils.services:LanguageService
     *
     * @deprecated since 1808 - use browserService instead.
     *
     * @description
     * determines the browser locale in the format en_US
     *
     * @returns {string} the browser locale
     */
    getBrowserLocale() {
        return this.browserService.getBrowserLocale();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getResolveLocale
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale. It determines in the
     * following order:
     *
     * 1. Check if the user has previously selected the language
     * 2. Check if the user browser locale is supported in the system
     *
     * @returns {Promise<string>} the locale
     */
    getResolveLocale() {
        return this._getDefaultLanguage();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getResolveLocaleIsoCode
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale ISO code. i.e.: If the selected tooling language is 'en_US',
     * the resolved value will be 'en'.
     *
     * @returns {Promise<string>} A promise that resolves to the isocode of the tooling language.
     */
    getResolveLocaleIsoCode() {
        return this.getResolveLocale().then((resolveLocale) => this.convertBCP47TagToJavaTag(resolveLocale).split('_')[0]);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#setSelectedToolingLanguage
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Set the user preference language in the storage service
     *
     * @param {IToolingLanguage} language the language object to be saved.
     */
    setSelectedToolingLanguage(language) {
        this.storageService.setValueInLocalStorage(SELECTED_LANGUAGE, language, false);
        this.translateService.use(language.isoCode);
        this.setApplicationTitle();
        this.eventService.publish(SWITCH_LANGUAGE_EVENT, {
            isoCode: language.isoCode
        });
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#registerSwitchLanguage
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Register a callback function to the gateway in order to switch the tooling language
     */
    registerSwitchLanguage() {
        this.eventService.subscribe(SWITCH_LANGUAGE_EVENT, (eventId, language) => {
            if (this.translateService.currentLang !== language.isoCode) {
                this.translateService.use(language.isoCode);
            }
        });
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#convertBCP47TagToJavaTag
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Method converts the BCP47 language tag representing the locale to the default java representation.
     * For example, method converts "en-US" to "en_US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in java representation
     */
    convertBCP47TagToJavaTag(languageTag) {
        return !!languageTag ? languageTag.replace(/-/g, '_') : languageTag;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#convertJavaTagToBCP47Tag
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Method converts the default java language tag representing the locale to the BCP47 representation.
     * For example, method converts "en_US" to "en-US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in BCP47 representation
     */
    convertJavaTagToBCP47Tag(languageTag) {
        return !!languageTag ? languageTag.replace(/_/g, '-') : languageTag;
    }
    _getDefaultLanguage() {
        return this.storageService.getValueFromLocalStorage(SELECTED_LANGUAGE, false).then((lang) => lang ? lang.isoCode : this.browserService.getBrowserLocale(), () => this.browserService.getBrowserLocale());
    }
    setApplicationTitle() {
        this.translateService.get('se.application.name').subscribe((pageTitle) => {
            document.title = pageTitle;
        });
    }
    get i18nLanguageRestService() {
        return this.injector
            .get(exports.RestServiceFactory)
            .get(this.languageServiceConstants.I18N_LANGUAGES_RESOURCE_URI);
    }
};
__decorate([
    Cached({ actions: [rarelyChangingContent] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], exports.LanguageService.prototype, "getToolingLanguages", null);
exports.LanguageService = __decorate([
    core.Injectable(),
    __param(3, core.Inject(EVENT_SERVICE)),
    __param(7, core.Inject(LANGUAGE_SERVICE_CONSTANTS)),
    __metadata("design:paramtypes", [LogService,
        core$1.TranslateService,
        exports.PromiseUtils, Object, BrowserService,
        IStorageService,
        core.Injector, Object])
], exports.LanguageService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
var LanguageSortStrategy;
(function (LanguageSortStrategy) {
    LanguageSortStrategy["Default"] = "default";
    LanguageSortStrategy["None"] = "none";
})(LanguageSortStrategy || (LanguageSortStrategy = {}));
// @dynamic
class LanguageDropdownHelper {
    static findSelectLanguageWithIsoCodePredicate(isoCode) {
        return (item) => item.value.isoCode === isoCode;
    }
    /**
     * Finds the language with a specified isoCode.
     *
     * @param {string} isoCode
     * @param {IToolingLanguage[]} languages
     * @returns {IToolingLanguage}
     */
    static findLanguageWithIsoCode(isoCode, languages) {
        return languages.find((language) => language.isoCode === isoCode);
    }
    /**
     * Returns an ordered language array by name and sets the selected language at the beginning.
     *
     * @param {IToolingLanguage} selectedLanguage
     * @param {IToolingLanguage[]} languages
     * @param {ILanguageSortConfig} config
     * @returns {IToolingLanguage[]}
     */
    static order(selectedLanguage, languages, config = { strategy: LanguageSortStrategy.Default }) {
        switch (config.strategy) {
            case LanguageSortStrategy.Default:
                const orderedLanguages = languages
                    .filter((language) => language.isoCode !== selectedLanguage.isoCode)
                    .sort((a, b) => a.isoCode.localeCompare(b.isoCode));
                return [selectedLanguage, ...orderedLanguages];
            case LanguageSortStrategy.None:
            default:
                return languages;
        }
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class LanguageDropdown {
    constructor(languageService, eventService, logService) {
        this.languageService = languageService;
        this.eventService = eventService;
        this.logService = logService;
        this.languageSortStrategy = LanguageSortStrategy.Default;
        this.selectedLanguage = null;
        this.items = [];
        this.initialLanguage = null;
        this.languages = [];
        this.unRegisterEventService = null;
    }
    ngOnInit() {
        Promise.all([
            this.languageService.getResolveLocale(),
            this.languageService.getToolingLanguages()
        ]).then(([isoCode, languages]) => {
            this.items = languages.map(LanguageDropdownAdapter.transform);
            this.languages = languages;
            this.setSelectedLanguage(isoCode);
            this.setInitialLanguage(isoCode);
        });
        this.unRegisterEventService = this.eventService.subscribe(SWITCH_LANGUAGE_EVENT, () => this.handleLanguageChange());
    }
    ngOnDestroy() {
        this.unRegisterEventService();
    }
    /**
     * Triggered when an user selects a language.
     * @param {IToolingLanguage} language
     */
    onSelectedLanguage(item) {
        this.languageService.setSelectedToolingLanguage(item.value);
    }
    /**
     * Set initial language to be displayed in dropdown
     *
     * @param {string} isoCode
     */
    setInitialLanguage(isoCode) {
        this.initialLanguage =
            this.items.find(LanguageDropdownHelper.findSelectLanguageWithIsoCodePredicate(isoCode)) ||
                this.items.find(LanguageDropdownHelper.findSelectLanguageWithIsoCodePredicate(DEFAULT_LANGUAGE_ISO));
    }
    /**
     * Triggered onInit and when language service sets a new language.
     *
     * @param {IToolingLanguage[]} languages
     * @param {string} isoCode
     */
    setSelectedLanguage(isoCode) {
        this.selectedLanguage = LanguageDropdownHelper.findLanguageWithIsoCode(isoCode, this.languages);
        if (this.selectedLanguage) {
            const sortedLanguages = LanguageDropdownHelper.order(this.selectedLanguage, this.languages, { strategy: this.languageSortStrategy });
            this.items = sortedLanguages.map(LanguageDropdownAdapter.transform);
            return;
        }
        // In case the iso code is too specific, it will use the more generic iso code to set the language.
        this.languageService
            .getResolveLocaleIsoCode()
            .then((code) => {
            this.selectedLanguage = LanguageDropdownHelper.findLanguageWithIsoCode(code, this.languages);
            const sortedLanguages = LanguageDropdownHelper.order(this.selectedLanguage, this.languages, { strategy: this.languageSortStrategy });
            this.items = sortedLanguages.map(LanguageDropdownAdapter.transform);
        })
            .catch((error) => {
            this.logService.debug(`LanguageDropdown - can't set the language: ${error}`);
        });
    }
    /**
     * Callback for setting the selected language.
     */
    handleLanguageChange() {
        this.languageService.getResolveLocale().then((isoCode) => {
            this.setSelectedLanguage(isoCode);
        });
    }
}
__decorate([
    core.Input(),
    __metadata("design:type", String)
], LanguageDropdown.prototype, "languageSortStrategy", void 0);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc component
 * @name  @smartutils.components:LanguageDropdownComponent
 *
 * @description
 * A component responsible for displaying and selecting application language. Uses {@link @smartutils.components:SelectComponent SelectComponent} to show language items
 *
 */
exports.LanguageDropdownComponent = class LanguageDropdownComponent extends LanguageDropdown {
    constructor(languageService, eventService, yjQuery, logService) {
        super(languageService, eventService, logService);
        this.eventService = eventService;
        this.yjQuery = yjQuery;
        this.logService = logService;
        this.CDKOverlayContainer = '.cdk-overlay-container';
    }
    ngOnInit() {
        super.ngOnInit();
        // workaround, to make popover z-index higher than dialog, so that the language dropdown can be selected
        this.removeAllPopovers();
    }
    ngAfterViewChecked() {
        // workaround, to make popover z-index higher than dialog, so that the language dropdown can be selected
        this.yjQuery(this.CDKOverlayContainer).addClass('cdk-overlay-container-language');
    }
    ngOnDestroy() {
        super.ngOnDestroy();
        this.yjQuery(this.CDKOverlayContainer).removeClass('cdk-overlay-container-language');
    }
    removeAllPopovers() {
        const parentCDK = this.yjQuery(this.CDKOverlayContainer).toArray();
        if (parentCDK.length === 0) {
            return;
        }
        this.yjQuery('.cdk-overlay-connected-position-bounding-box')
            .toArray()
            .forEach((child) => {
            parentCDK[0].removeChild(child);
        });
    }
};
exports.LanguageDropdownComponent = __decorate([
    core.Component({
        selector: 'su-language-dropdown',
        template: `
        <su-select
            class="su-language-selector"
            [items]="items"
            [initialValue]="initialLanguage"
            (onItemSelected)="onSelectedLanguage($event)">
        </su-select>
    `
    }),
    __param(0, core.Inject(LANGUAGE_SERVICE)),
    __param(1, core.Inject(EVENT_SERVICE)),
    __param(2, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [exports.LanguageService, Object, Function, LogService])
], exports.LanguageDropdownComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
class ISelectAdapter {
    static transform(item, id) {
        return {};
    }
}

/**
 * Directive for marking list item for 'ListKeyboardControlDirective' to allow for navigating with keyboard.
 */
exports.ListItemKeyboardControlDirective = class ListItemKeyboardControlDirective {
    constructor(hostElement, renderer) {
        this.hostElement = hostElement;
        this.renderer = renderer;
        /** @internal */
        this.activeClassName = 'is-active';
    }
    /** @internal */
    ngOnInit() {
        this.setTabIndex();
    }
    getElement() {
        return this.hostElement.nativeElement;
    }
    setActive() {
        const elm = this.getElement();
        this.renderer.addClass(elm, this.activeClassName);
        elm.scrollIntoView({ block: 'nearest' });
    }
    setInactive() {
        this.renderer.removeClass(this.getElement(), this.activeClassName);
    }
    /** @internal */
    setTabIndex() {
        this.renderer.setAttribute(this.getElement(), 'tabindex', '-1');
    }
};
exports.ListItemKeyboardControlDirective = __decorate([
    core.Directive({
        selector: '[suListItemKeyboardControl]'
    }),
    __metadata("design:paramtypes", [core.ElementRef, core.Renderer2])
], exports.ListItemKeyboardControlDirective);

var KeyboardKey;
(function (KeyboardKey) {
    KeyboardKey["ArrowDown"] = "ArrowDown";
    KeyboardKey["ArrowUp"] = "ArrowUp";
    KeyboardKey["Enter"] = "Enter";
    KeyboardKey["Esc"] = "Escape";
})(KeyboardKey || (KeyboardKey = {}));
/**
 * Directive that manages the active option in a list of items based on keyboard interaction.
 * For disabled options, a predicate must be passed with `suListKeyboardControlDisabledPredicate`
 * which will prevent that option from navigating with arrow up / down key.
 *
 * Note: It will include only direct children having the `suListItemKeyboardControl` directive.
 *
 * @example
 * ```
 * items = [
 *   {
 *     id: 1,
 *     label: 'item 1'
 *   },
 *   {
 *     id: 2,
 *     label: 'item 2'
 *   },
 *   {
 *     id: 3,
 *     label: 'item 3'
 *   }
 * ]
 * <ul suListKeyboardControl>
 *   <li suListItemKeyboardControl *ngFor="let item of items">
 *     {{ item.label }}
 *   </li>
 * </ul>
 * ```
 */
exports.ListKeyboardControlDirective = class ListKeyboardControlDirective {
    constructor() {
        /** Whether the keyboard interaction is enabled */
        this.suListKeyboardControlEnabled = true;
        this.suListKeyboardControlEnterKeydown = new core.EventEmitter();
        this.suListKeyboardControlEscKeydown = new core.EventEmitter();
        /** @internal */
        this.didNgAfterContentInit = false;
        /** @internal */
        this.activeItem = null;
        /** @internal */
        this.activeItemIndex = null;
    }
    /** @internal */
    onKeyDown(event) {
        if (!this.suListKeyboardControlEnabled || this.items.length === 0) {
            return;
        }
        // For ArrowDown and ArrowUp prevent from scrolling the container.
        // Focus event is called when setting an active item so it will also scroll if needed.
        switch (event.key) {
            case KeyboardKey.ArrowDown:
                event.preventDefault();
                this.handleArrowDown();
                return;
            case KeyboardKey.ArrowUp:
                event.preventDefault();
                this.handleArrowUp();
                return;
            case KeyboardKey.Enter:
                event.preventDefault();
                this.handleEnter();
                return;
            case KeyboardKey.Esc:
                this.handleEsc();
                return;
        }
    }
    /** @internal */
    ngAfterContentInit() {
        this.items.changes.subscribe(() => this.onItemsChange());
        // Items may be cached by wrapper component e.g. <fd-popover> so items.changes subscription will not receive an event.
        // Ensure that whenever a dropdown is opened, subscription will receive an event
        this.items.notifyOnChanges();
        this.didNgAfterContentInit = true;
    }
    /** @internal */
    ngOnChanges(changes) {
        if (!this.didNgAfterContentInit) {
            return;
        }
        const enabledChange = changes.suListKeyboardControlEnabled;
        const predicateChange = changes.suListKeyboardControlDisabledPredicate;
        const shouldSetActive = enabledChange && enabledChange.currentValue && !enabledChange.previousValue;
        const shouldUnsetActive = enabledChange && !enabledChange.currentValue && enabledChange.previousValue;
        const shouldSetActiveForPredicate = predicateChange && predicateChange.currentValue && !!this.suListKeyboardControlEnabled;
        const shouldUnsetActiveForPredicate = predicateChange &&
            !predicateChange.currentValue &&
            predicateChange.previousValue &&
            !!this.suListKeyboardControlEnabled;
        if (this.items.length === 0) {
            return;
        }
        if (shouldSetActive || shouldSetActiveForPredicate) {
            this.setFirstItemActive();
        }
        if (shouldUnsetActive || shouldUnsetActiveForPredicate) {
            this.unsetActiveItem();
        }
    }
    /**
     * Handler for dynamic content change.
     * Sets or unsets active item.
     * @internal
     */
    onItemsChange() {
        if (this.items.length === 0) {
            this.clearActiveItem();
            return;
        }
        if (this.suListKeyboardControlEnabled) {
            if (!this.isActiveItemSet()) {
                // if no active item then set the first one as active
                this.setActiveItemByIndex(0, 1);
            }
            else if (!this.itemExistsByIndex(this.activeItemIndex)) {
                // clear active item if the active item no longer exists
                this.clearActiveItem();
            }
        }
        else {
            // keyboard control is not enabled
            this.clearActiveItem();
        }
    }
    /** @internal */
    handleArrowUp() {
        this.setPreviousItemActive();
    }
    /** @internal */
    handleArrowDown() {
        this.setNextItemActive();
    }
    /** @internal */
    handleEnter() {
        this.suListKeyboardControlEnterKeydown.emit(this.activeItemIndex);
    }
    /** @internal */
    handleEsc() {
        this.suListKeyboardControlEscKeydown.emit();
    }
    /** @internal */
    getItemByIndex(index) {
        const items = this.getItemsArray();
        return items[index];
    }
    /** @internal */
    getItemsArray() {
        return this.items.toArray();
    }
    /** @internal */
    setActiveItemByIndex(index, delta) {
        const items = this.getItemsArray();
        if (this.suListKeyboardControlDisabledPredicate) {
            while (this.suListKeyboardControlDisabledPredicate(items[index], index)) {
                index += delta;
                if (!items[index]) {
                    return;
                }
            }
        }
        const item = this.getItemByIndex(index);
        this.setActiveItem(item);
    }
    /** @internal */
    setActiveItem(item) {
        const items = this.getItemsArray();
        const index = items.indexOf(item);
        if (this.activeItem) {
            this.activeItem.setInactive();
        }
        if (items.length > 0) {
            this.activeItem = items[index];
            this.activeItem.setActive();
            this.activeItemIndex = index;
        }
    }
    /** @internal */
    unsetActiveItem() {
        if (!this.activeItem) {
            return;
        }
        this.activeItem.setInactive();
        this.activeItemIndex = null;
    }
    /** @internal */
    setFirstItemActive() {
        this.setActiveItemByIndex(0, 1);
    }
    /** @internal */
    setNextItemActive() {
        if (this.activeItemIndex === null) {
            this.setFirstItemActive();
            return;
        }
        if (this.activeItemIndex < this.items.length - 1) {
            this.setActiveItemByIndex(this.activeItemIndex + 1, 1);
        }
    }
    /** @internal */
    setPreviousItemActive() {
        if (this.activeItemIndex === null) {
            this.setFirstItemActive();
            return;
        }
        if (this.activeItemIndex > 0) {
            this.setActiveItemByIndex(this.activeItemIndex - 1, -1);
        }
    }
    /** @internal */
    itemExistsByIndex(index) {
        if (index === null) {
            return false;
        }
        return !!this.getItemByIndex(index);
    }
    /** @internal */
    clearActiveItem() {
        this.activeItem = null;
        this.activeItemIndex = null;
    }
    /** @internal */
    isActiveItemSet() {
        return !!this.activeItem;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.ListKeyboardControlDirective.prototype, "suListKeyboardControlEnabled", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.ListKeyboardControlDirective.prototype, "suListKeyboardControlDisabledPredicate", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.ListKeyboardControlDirective.prototype, "suListKeyboardControlEnterKeydown", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.ListKeyboardControlDirective.prototype, "suListKeyboardControlEscKeydown", void 0);
__decorate([
    core.ContentChildren(exports.ListItemKeyboardControlDirective),
    __metadata("design:type", core.QueryList)
], exports.ListKeyboardControlDirective.prototype, "items", void 0);
__decorate([
    core.HostListener('document:keydown', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [KeyboardEvent]),
    __metadata("design:returntype", void 0)
], exports.ListKeyboardControlDirective.prototype, "onKeyDown", null);
exports.ListKeyboardControlDirective = __decorate([
    core.Directive({
        selector: '[suListKeyboardControl]'
    })
], exports.ListKeyboardControlDirective);

exports.ListKeyboardControlModule = class ListKeyboardControlModule {
};
exports.ListKeyboardControlModule = __decorate([
    core.NgModule({
        declarations: [exports.ListKeyboardControlDirective, exports.ListItemKeyboardControlDirective],
        exports: [exports.ListKeyboardControlDirective, exports.ListItemKeyboardControlDirective]
    })
], exports.ListKeyboardControlModule);

var SelectComponent_1;
exports.SelectComponent = SelectComponent_1 = class SelectComponent extends BaseValueAccessor {
    constructor() {
        super(...arguments);
        this.items = [];
        this.initialValue = null;
        this.placeholder = '';
        this.isKeyboardControlEnabled = true;
        this.hasCustomTrigger = false;
        this.onItemSelected = new core.EventEmitter();
        this.isOpen = false;
    }
    selectItem(id) {
        const item = this.items.find((selected) => selected.id === id);
        this.setValue(item);
        this.isOpen = false;
        this.onItemSelected.emit(item);
    }
    ngOnChanges(changes) {
        if (changes.initialValue && changes.initialValue.currentValue && !this.value) {
            this.setInitialValue(changes.initialValue.currentValue);
        }
    }
    setInitialValue(value) {
        if (typeof value === 'number') {
            this.setValueById(value);
        }
        else {
            this.setValue(value);
        }
    }
    setValue(item) {
        this.writeValue(item);
        this.onChange(item);
    }
    setValueById(id) {
        this.setValue(this.items.find((item) => item.id === id));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.SelectComponent.prototype, "items", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "initialValue", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "placeholder", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "isKeyboardControlEnabled", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "hasCustomTrigger", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.SelectComponent.prototype, "onItemSelected", void 0);
exports.SelectComponent = SelectComponent_1 = __decorate([
    core.Component({
        selector: 'su-select',
        encapsulation: core.ViewEncapsulation.None,
        providers: [
            { provide: forms.NG_VALUE_ACCESSOR, useExisting: core.forwardRef(() => SelectComponent_1), multi: true }
        ],
        styles: [`.su-select,.su-select__popover-control,.su-select__popover-control .fd-dropdown{width:100%}.su-select__popover-control .fd-dropdown__control{overflow:hidden;color:var(--sapNeutralColor);text-align:left;padding:0 .625rem;justify-content:space-between}.su-select__popover-control .fd-dropdown__control:focus{outline:0!important}.su-select__popover-control .fd-dropdown__control:focus:after{border-style:none}.su-select__popover-control .fd-dropdown__control::after{position:absolute;top:0;right:0;display:flex;justify-content:center;align-items:center;padding:0;height:36px;width:36px;margin-top:0;font-size:14px}.su-select__menu .fd-list{max-height:200px;overflow:auto}.su-select__menu .fd-list .fd-list__title{padding:.5rem}.su-select__menu .fd-list__item[select-highlighted]{background:se-color-state("selected")}.su-select__menu .fd-list__item[select-highlighted]:focus{outline:0}.su-select__popover-control .se-select-arrow{position:absolute;right:0;border-left-color:inherit;border-left-style:solid;border-left-width:1px;border-top-left-radius:unset;border-bottom-left-radius:unset}`],
        template: `
        <fd-popover [(isOpen)]="isOpen" fillControlMode="equal" class="su-select">
            <fd-popover-control class="su-select__popover-control">
                <button
                    fd-button
                    aria-expanded="true"
                    class="fd-dropdown__control fd-button fd-select__control"
                    type="button"
                    *ngIf="!hasCustomTrigger; else customTrigger"
                    label="{{ (value && value.label) || placeholder | translate }}">
                    <span class="fd-button fd-button--transparent se-select-arrow">
                        <i class="sap-icon--slim-arrow-down"></i>
                    </span>
                </button>
                <ng-template #customTrigger>
                    <ng-content select="[su-select-custom-trigger]"></ng-content>
                </ng-template>
            </fd-popover-control>
            <fd-popover-body>
                <div class="su-select__menu">
                    <ul
                        fd-list
                        suListKeyboardControl
                        [suListKeyboardControlEnabled]="isKeyboardControlEnabled && isOpen"
                        (suListKeyboardControlEnterKeydown)="selectItem($event)"
                        class="fd-list--dropdown">
                        <li
                            fd-list-item
                            suListItemKeyboardControl
                            [ngClass]="item.listItemClassName"
                            *ngFor="let item of items"
                            (click)="selectItem(item.id)"
                            [attr.tabindex]="-1"
                            [attr.data-select-id]="item.id"
                            role="option">
                            <a fd-list-link>
                                <span fd-list-title> {{ item.label | translate }} </span>
                            </a>
                        </li>
                    </ul>
                </div>
            </fd-popover-body>
        </fd-popover>
    `
    })
], exports.SelectComponent);

exports.SelectModule = class SelectModule {
};
exports.SelectModule = __decorate([
    core.NgModule({
        imports: [
            core$2.FundamentalNgxCoreModule,
            common.CommonModule,
            exports.ListKeyboardControlModule,
            exports.TranslationModule.forChild(),
            list.ListModule
        ],
        declarations: [exports.SelectComponent],
        entryComponents: [exports.SelectComponent],
        exports: [exports.SelectComponent]
    })
], exports.SelectModule);

exports.LanguageDropdownModule = class LanguageDropdownModule {
};
exports.LanguageDropdownModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.SelectModule],
        declarations: [exports.LanguageDropdownComponent],
        entryComponents: [exports.LanguageDropdownComponent],
        exports: [exports.LanguageDropdownComponent]
    })
], exports.LanguageDropdownModule);

exports.LoginDialogModule = class LoginDialogModule {
};
exports.LoginDialogModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            exports.LanguageDropdownModule,
            forms.FormsModule,
            forms.ReactiveFormsModule,
            core$2.MessageStripModule,
            exports.TranslationModule.forChild(),
            core$2.ButtonModule,
            core$2.FormModule
        ],
        declarations: [exports.LoginDialogComponent],
        entryComponents: [exports.LoginDialogComponent],
        exports: [exports.LanguageDropdownModule]
    })
], exports.LoginDialogModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
exports.AuthenticationService = class AuthenticationService extends IAuthenticationService {
    constructor(translationsFetchService, modalService, sharedDataService, storageService, eventService, ssoAuthenticationHelper, settingsService, authenticationManager, logService) {
        super();
        this.translationsFetchService = translationsFetchService;
        this.modalService = modalService;
        this.sharedDataService = sharedDataService;
        this.storageService = storageService;
        this.eventService = eventService;
        this.ssoAuthenticationHelper = ssoAuthenticationHelper;
        this.settingsService = settingsService;
        this.authenticationManager = authenticationManager;
        this.logService = logService;
    }
    filterEntryPoints(resource) {
        return this.sharedDataService.get('authenticationMap').then((authenticationMap) => functionsUtils
            .convertToArray(Object.assign(Object.assign({}, (authenticationMap || {})), DEFAULT_AUTH_MAP))
            .filter((entry) => new RegExp(entry.key, 'g').test(resource))
            .map((element) => element.value));
    }
    isAuthEntryPoint(resource) {
        return this.sharedDataService.get('authenticationMap').then((authenticationMap) => {
            const authEntryPoints = functionsUtils
                .convertToArray(authenticationMap || {})
                .map((element) => element.value);
            return (authEntryPoints.indexOf(resource) > -1 ||
                resource === DEFAULT_AUTHENTICATION_ENTRY_POINT);
        });
    }
    authenticate(resource) {
        return this._findLoginData(resource).then((loginData) => this._launchAuth(loginData).then((modalFeedback) => {
            Promise.resolve(this.eventService.publish(EVENTS.AUTHORIZATION_SUCCESS, {
                userHasChanged: modalFeedback.userHasChanged
            }))
                .then(() => {
                if (modalFeedback.userHasChanged) {
                    this.eventService.publish(EVENTS.USER_HAS_CHANGED);
                }
                /**
                 * We only need to reload when the user has changed and all authentication forms were closed.
                 * There can be many authentication forms if some modules use different (from default one) end points.
                 */
                const reauthInProcess = lodash__namespace
                    .values(this.reauthInProgress)
                    .some((inProcess) => inProcess);
                if (modalFeedback.userHasChanged &&
                    !reauthInProcess &&
                    this.authenticationManager &&
                    this.authenticationManager.onUserHasChanged) {
                    this.authenticationManager.onUserHasChanged();
                }
            })
                .catch((reason) => {
                this.logService.debug(`AuthenticationService - authenticate: ${JSON.stringify(reason)}`);
            });
            this.reauthInProgress[loginData.authURI] = false;
        }));
    }
    logout() {
        // First, indicate the services that SmartEdit is logging out. This should give them the opportunity to clean up.
        // NOTE: This is not synchronous since some clean-up might be lengthy, and logging out should be fast.
        return this.eventService.publish(EVENTS.LOGOUT).then(() => {
            this.storageService.removeAllAuthTokens();
            if (this.ssoAuthenticationHelper.isAutoSSOMain()) {
                this.ssoAuthenticationHelper.logout();
            }
            else if (this.authenticationManager && this.authenticationManager.onLogout) {
                this.authenticationManager.onLogout();
            }
        });
    }
    isReAuthInProgress(entryPoint) {
        return Promise.resolve(this.reauthInProgress[entryPoint] === true);
    }
    setReAuthInProgress(entryPoint) {
        this.reauthInProgress[entryPoint] = true;
        return Promise.resolve();
    }
    isAuthenticated(url) {
        return this.filterEntryPoints(url).then((entryPoints) => {
            const authURI = entryPoints && entryPoints[0];
            return Promise.resolve(this.storageService.getAuthToken(authURI)).then((authToken) => !!authToken);
        });
    }
    /*
     * will try determine first relevant authentication entry point from authenticationMap and retrieve potential client credentials to be added on top of user credentials
     */
    _findLoginData(resource) {
        return this.filterEntryPoints(resource).then((entryPoints) => Promise.resolve(this.sharedDataService.get('credentialsMap').then((credentialsMap) => {
            const map = Object.assign(Object.assign({}, (credentialsMap || {})), DEFAULT_CREDENTIALS_MAP);
            const authURI = entryPoints[0];
            return {
                authURI,
                clientCredentials: map[authURI]
            };
        })));
    }
    _launchAuth(loginData) {
        return this.translationsFetchService
            .waitToBeReady()
            .then(() => Promise.all([
            this.storageService.isInitialized(),
            this.settingsService.getBoolean('smartedit.sso.enabled')
        ]))
            .then(([isFullScreen, ssoEnabled]) => {
            const modalRef = this.modalService.open({
                component: exports.LoginDialogComponent,
                data: Object.assign(Object.assign({}, loginData), { isFullScreen,
                    ssoEnabled }),
                config: {
                    hasBackdrop: false
                }
            });
            return new Promise((resolve, reject) => {
                modalRef.afterClosed.subscribe(resolve, reject);
            });
        });
    }
};
exports.AuthenticationService = __decorate([
    __param(4, core.Inject(EVENT_SERVICE)),
    __param(7, core.Optional()),
    __metadata("design:paramtypes", [ITranslationsFetchService,
        IModalService,
        ISharedDataService,
        IStorageService, Object, exports.SSOAuthenticationHelper,
        ISettingsService,
        IAuthenticationManagerService,
        LogService])
], exports.AuthenticationService);

/**
 * Allows to perform operations on a Modal Component such as adding the buttons or getting the modal data.
 * It must be injected into a Custom Modal Component.
 *
 * The Custom Modal Component is rendered by {@link ModalTemplateComponent}.
 * A Service or a Component that opens the Modal Component must provide
 * [component]{@link ModalOpenConfig#component} and [templateConfig]{@link ModalOpenConfig#templateConfig}.
 *
 *
 * ### Example of a Service or Component that opens the modal
 *
 *      this.modalService.open({
 *           component: YourCustomModalComponent,
 *               templateConfig: {
 *               title: 'se.cms.synchronization.pagelist.modal.title.prefix',
 *               titleSuffix: 'se.cms.pageeditormodal.editpagetab.title'
 *           },
 *           data: {
 *               propA: 'valA'
 *           }
 *      });
 *
 * ### Example of YourCustomModalComponent
 *
 *      export class YourCustomModalComponent implements OnInit {
 *          constructor(private modalManager: ModalManagerService) {}
 *
 *          ngOnInit(): void {
 *              this.modalManager.addButtons([]);
 *              this.modalManager.getModalData().pipe(take(1)).subscribe(({propA}) => { console.log(propA) });
 *          }
 *      }
 *
 */
exports.ModalManagerService = class ModalManagerService {
    constructor(modalRef) {
        this.modalRef = modalRef;
        this.title = new rxjs.BehaviorSubject('');
        this.titleSuffix = new rxjs.BehaviorSubject('');
        this.modalData = new rxjs.BehaviorSubject({});
        this.component = new rxjs.BehaviorSubject(undefined);
        this.isDismissButtonVisible = new rxjs.BehaviorSubject(false);
        this.buttons = new rxjs.BehaviorSubject([]);
    }
    init() {
        this.modalData.next(this.modalRef.data.modalData);
        this.component.next(this.modalRef.data.component);
        this.buttons.next(this.modalRef.data.templateConfig.buttons || []);
        this.title.next(this.modalRef.data.templateConfig.title || '');
        this.titleSuffix.next(this.modalRef.data.templateConfig.titleSuffix || '');
        this.isDismissButtonVisible.next(this.modalRef.data.templateConfig.isDismissButtonVisible);
    }
    // getters
    getComponent() {
        return this.component.asObservable();
    }
    getTitle() {
        return this.title.asObservable();
    }
    getTitleSuffix() {
        return this.titleSuffix.asObservable();
    }
    getButtons() {
        return this.buttons.asObservable();
    }
    getModalData() {
        return this.modalData.asObservable();
    }
    getIsDismissButtonVisible() {
        return this.isDismissButtonVisible.asObservable();
    }
    // header dismiss button
    setDismissButtonVisibility(isVisible) {
        this.isDismissButtonVisible.next(isVisible);
    }
    setTitle(title) {
        this.title.next(title);
    }
    /**
     * Use this method for adding only one button.
     *
     * NOTE: For multiple buttons use `addButtons`.
     */
    addButton(button) {
        this.getButtonsValue().subscribe((buttons) => {
            const payload = [...buttons, button];
            this.buttons.next(payload);
        });
    }
    addButtons(buttons) {
        this.getButtonsValue().subscribe((currentButtons) => {
            const payload = [...currentButtons, ...buttons];
            this.buttons.next(payload);
        });
    }
    removeButton(id) {
        this.getButtonsValue().subscribe((buttons) => {
            const payload = buttons.filter((button) => button.id !== id);
            this.buttons.next(payload);
        });
    }
    removeAllButtons() {
        this.buttons.next([]);
    }
    disableButton(id) {
        this.getButtonsValue().subscribe((buttons) => {
            const payload = buttons.map((button) => button.id === id ? Object.assign(Object.assign({}, button), { disabled: true }) : button);
            this.buttons.next(payload);
        });
    }
    setDismissCallback(callback) {
        this.dismissCallback = callback;
    }
    enableButton(id) {
        this.getButtonsValue().subscribe((buttons) => {
            const payload = buttons.map((button) => button.id === id ? Object.assign(Object.assign({}, button), { disabled: false }) : button);
            this.buttons.next(payload);
        });
    }
    onButtonClicked(button) {
        const callbackReturnedObservable = button.callback
            ? button.callback()
            : rxjs.of(null);
        if (button.action !== exports.ModalButtonAction.None) {
            callbackReturnedObservable.subscribe((data) => button.action === exports.ModalButtonAction.Close ? this.close(data) : this.dismiss(data));
        }
    }
    close(data) {
        this.modalRef.close(data);
    }
    dismiss(data) {
        this.dismissCallback()
            .then(() => this.modalRef.dismiss(data))
            .catch(lodash.noop);
    }
    getButtonsValue() {
        return this.buttons.pipe(operators.take(1));
    }
    dismissCallback() {
        return Promise.resolve();
    }
};
exports.ModalManagerService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core$2.DialogRef])
], exports.ModalManagerService);

exports.ModalTemplateComponent = class ModalTemplateComponent {
    constructor(modalManager, cdr) {
        this.modalManager = modalManager;
        this.cdr = cdr;
        this.component$ = this.modalManager.getComponent();
        this.title$ = this.modalManager.getTitle();
        this.titleSuffix$ = this.modalManager.getTitleSuffix();
        this.isDismissButtonVisible$ = this.modalManager.getIsDismissButtonVisible();
        this.buttons$ = this.modalManager.getButtons();
    }
    ngOnInit() {
        this.modalManager.init();
        this.buttonsSubscription = this.buttons$.subscribe((value) => {
            this.buttons = value;
            // For some consumere, adding buttons can result in not properly rendered view
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    ngOnDestroy() {
        if (this.buttonsSubscription) {
            this.buttonsSubscription.unsubscribe();
        }
    }
    onButtonClicked(button) {
        this.modalManager.onButtonClicked(button);
    }
    dismiss() {
        this.modalManager.dismiss();
    }
};
exports.ModalTemplateComponent = __decorate([
    core.Component({
        selector: 'fundamental-modal-template',
        encapsulation: core.ViewEncapsulation.None,
        providers: [exports.ModalManagerService],
        styles: [
            `
            .fd-dialog__title {
                min-height: 20px;
            }
        `
        ],
        template: `
        <fd-dialog>
            <fd-dialog-header>
                <h1 fd-title id="fd-title-{{ (title$ | async) || '' }}">
                    {{ (title$ | async) || '' | translate }}
                    {{ (titleSuffix$ | async) || '' | translate }}
                </h1>
                <button
                    fd-dialog-close-button
                    *ngIf="isDismissButtonVisible$ | async"
                    (click)="dismiss()"
                    class="fd-button fd-button--transparent fd-dialog__close"
                    title="{{ 'se.btn.close.title' | translate }}"></button>
            </fd-dialog-header>
            <fd-dialog-body>
                <ng-container *ngIf="component$ | async as component">
                    <ng-container *ngComponentOutlet="component"></ng-container>
                </ng-container>
            </fd-dialog-body>
            <fd-dialog-footer *ngIf="buttons && buttons.length > 0">
                <button
                    *ngFor="let button of buttons"
                    [disabled]="button.disabledFn ? button.disabledFn!() : button.disabled"
                    [fdType]="button.style"
                    [attr.id]="button.id"
                    [label]="button.label | translate"
                    (click)="onButtonClicked(button)"
                    fd-button></button>
            </fd-dialog-footer>
        </fd-dialog>
    `
    }),
    __metadata("design:paramtypes", [exports.ModalManagerService, core.ChangeDetectorRef])
], exports.ModalTemplateComponent);

exports.FundamentalModalTemplateModule = class FundamentalModalTemplateModule {
};
exports.FundamentalModalTemplateModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, dialog.DialogModule, core$2.ButtonModule, exports.TranslationModule.forChild()],
        declarations: [exports.ModalTemplateComponent],
        entryComponents: [exports.ModalTemplateComponent],
        exports: [exports.ModalTemplateComponent]
    })
], exports.FundamentalModalTemplateModule);

exports.ModalService = class ModalService {
    constructor(fundamentalModalService) {
        this.fundamentalModalService = fundamentalModalService;
    }
    open(options) {
        const { templateConfig } = options;
        return !!templateConfig
            ? this.openWithTemplate(options)
            : this.openStandalone(options);
    }
    hasOpenModals() {
        return this.fundamentalModalService.hasOpenDialogs();
    }
    dismissAll() {
        this.fundamentalModalService.dismissAll();
    }
    openStandalone(options) {
        const { config = {}, component, data } = options;
        return this.fundamentalModalService.open(component, Object.assign(Object.assign({}, config), { data }));
    }
    openWithTemplate(options) {
        const { config = {}, templateConfig = {}, component, data } = options;
        const settings = Object.assign(Object.assign({}, config), { data: {
                templateConfig,
                component,
                modalData: data
            } });
        return this.fundamentalModalService.open(exports.ModalTemplateComponent, settings);
    }
};
exports.ModalService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core$2.DialogService])
], exports.ModalService);

class Alert {
    constructor(_alertConf, ALERT_CONFIG_DEFAULTS, fundamentalAlertService, translateService) {
        this._alertConf = _alertConf;
        this.fundamentalAlertService = fundamentalAlertService;
        this.translateService = translateService;
        this._displayed = false;
        lodash__namespace.defaultsDeep(this._alertConf, lodash__namespace.cloneDeep(ALERT_CONFIG_DEFAULTS));
    }
    get alertConf() {
        return this._alertConf;
    }
    get message() {
        return this._alertConf.message;
    }
    get type() {
        return this._alertConf.type;
    }
    /**
     * Displays the alert to the user.
     */
    show() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.isDisplayed()) {
                return;
            }
            if (this._alertConf.message) {
                this._alertConf.message = yield this.translateService
                    .get(this._alertConf.message, this._alertConf.messagePlaceholders)
                    .toPromise();
            }
            const content = typeof this._alertConf.message !== 'undefined'
                ? this._alertConf.message
                : this._alertConf.component || '';
            this._alertRef = this.fundamentalAlertService.open(content, this._alertConf);
            this._alertRef.afterDismissed.subscribe(() => (this._displayed = false));
            this._displayed = true;
        });
    }
    /**
     * Hides the alert if it is currently being displayed to the user.
     */
    hide() {
        if (!this.isDisplayed()) {
            return;
        }
        this._alertRef.dismiss();
    }
    isDisplayed() {
        return this._displayed;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ALERT_CONFIG_DEFAULTS_TOKEN = new core.InjectionToken('alertConfigToken');
const ALERT_CONFIG_DEFAULTS = {
    data: {},
    type: exports.IAlertServiceType.INFO,
    dismissible: true,
    duration: 1000,
    width: '300px',
    minWidth: '',
    mousePersist: true
};

/**
 * @ngdoc service
 * @name @smartutils.services:AlertFactory
 *
 * @description
 * The alertFactory allows you to create an instances of type Alert.<br />
 * When possible, it is better to use {@link @smartutils.services:AlertService AlertService} to show alerts.<br />
 * This factory is useful when one of the Alert class methods is needed, like
 * hide() or isDisplayed(), or if you want to create a single instance and hide/show when necessary.
 */
exports.AlertFactory = class AlertFactory {
    constructor(fundamentalAlertService, translateService, ALERT_CONFIG_DEFAULTS) {
        this.fundamentalAlertService = fundamentalAlertService;
        this.translateService = translateService;
        this.ALERT_CONFIG_DEFAULTS = ALERT_CONFIG_DEFAULTS;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createAlert
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance
     */
    createAlert(alertConf) {
        const config = this.getAlertConfig(alertConf);
        return this.createAlertObject(config);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createInfo
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to INFO
     */
    createInfo(alertConf) {
        const config = this.getAlertConfig(alertConf, exports.IAlertServiceType.INFO);
        return this.createAlertObject(config);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createDanger
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to DANGER
     */
    createDanger(alertConf) {
        const config = this.getAlertConfig(alertConf, exports.IAlertServiceType.DANGER);
        return this.createAlertObject(config);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createWarning
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to WARNING
     */
    createWarning(alertConf) {
        const config = this.getAlertConfig(alertConf, exports.IAlertServiceType.WARNING);
        return this.createAlertObject(config);
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createSuccess
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to SUCCESS
     */
    createSuccess(alertConf) {
        const config = this.getAlertConfig(alertConf, exports.IAlertServiceType.SUCCESS);
        return this.createAlertObject(config);
    }
    getAlertConfig(strOrConf, type) {
        if (typeof strOrConf === 'string') {
            return {
                message: strOrConf,
                type: type || exports.IAlertServiceType.INFO,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            };
        }
        if (!strOrConf.type) {
            strOrConf.type = type || exports.IAlertServiceType.INFO;
        }
        return strOrConf;
    }
    createAlertObject(alertConf) {
        return new Alert(alertConf, this.ALERT_CONFIG_DEFAULTS, this.fundamentalAlertService, this.translateService);
    }
};
exports.AlertFactory = __decorate([
    core.Injectable(),
    __param(2, core.Inject(ALERT_CONFIG_DEFAULTS_TOKEN)),
    __metadata("design:paramtypes", [core$2.AlertService,
        core$1.TranslateService,
        core$2.AlertConfig])
], exports.AlertFactory);

/**
 * @ngdoc service
 * @name @smartutils.services:AlertService
 */
exports.AlertService = class AlertService {
    constructor(alertFactory) {
        this.alertFactory = alertFactory;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showAlert
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showAlert(alertConf) {
        const alert = this.alertFactory.createAlert(alertConf);
        alert.show();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showInfo
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showInfo(alertConf) {
        const alert = this.alertFactory.createInfo(alertConf);
        alert.show();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showDanger
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showDanger(alertConf) {
        const alert = this.alertFactory.createDanger(alertConf);
        alert.show();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showWarning
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showWarning(alertConf) {
        const alert = this.alertFactory.createWarning(alertConf);
        alert.show();
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showSuccess
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showSuccess(alertConf) {
        const alert = this.alertFactory.createSuccess(alertConf);
        alert.show();
    }
};
exports.AlertService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.AlertFactory])
], exports.AlertService);

exports.AlertModule = class AlertModule {
};
exports.AlertModule = __decorate([
    core.NgModule({
        imports: [core$2.AlertModule, exports.TranslationModule],
        providers: [
            {
                provide: ALERT_CONFIG_DEFAULTS_TOKEN,
                useValue: ALERT_CONFIG_DEFAULTS
            },
            exports.AlertService,
            exports.AlertFactory
        ]
    })
], exports.AlertModule);

exports.FileReaderService = class FileReaderService {
    read(file, config) {
        const fileReader = new FileReader();
        if (config === null || config === void 0 ? void 0 : config.onError) {
            fileReader.onerror = config.onError;
        }
        if (config === null || config === void 0 ? void 0 : config.onLoadEnd) {
            fileReader.onloadend = config.onLoadEnd;
        }
        fileReader.readAsArrayBuffer(file);
        return fileReader;
    }
};
exports.FileReaderService = __decorate([
    core.Injectable()
], exports.FileReaderService);

exports.FileMimeTypeService = class FileMimeTypeService {
    constructor(fileReaderService, settingsService) {
        this.fileReaderService = fileReaderService;
        this.settingsService = settingsService;
    }
    isFileMimeTypeValid(file) {
        return new Promise((resolve, reject) => __awaiter(this, void 0, void 0, function* () {
            const { error: mimeTypesError, data: mimeTypes } = yield promiseUtils.attempt(this.settingsService.getStringList('smartedit.validFileMimeTypeCodes'));
            if (mimeTypesError) {
                reject(false);
                return;
            }
            this.fileReaderService.read(file, {
                onLoadEnd: (ev) => {
                    if (!this.validateMimeTypeFromFile(ev.target.result, mimeTypes)) {
                        reject();
                        return;
                    }
                    resolve(true);
                },
                onError: () => {
                    reject();
                }
            });
        }));
    }
    /**
     *  Mimetype is valid when starting bytes of the file are matching the Mimetype byte pattern.
     *  For example. 89 50 4E 47 is a png file, so if the first 4 bytes are 89504E47 it is recognized as a png file.
     *
     *  Read more on File Signatures and Image Type Pattern Matching
     *  - https://en.wikipedia.org/wiki/List_of_file_signatures
     *  - https://mimesniff.spec.whatwg.org/#image-type-pattern-matching-algorithm
     */
    validateMimeTypeFromFile(loadedFile, mimeTypes) {
        const fileAsBytes = new Uint8Array(loadedFile).subarray(0, 32);
        const header = fileAsBytes.reduce((head, byte) => {
            let byteAsStr = byte.toString(16);
            if (byteAsStr.length === 1) {
                byteAsStr = '0' + byteAsStr;
            }
            head += byteAsStr;
            return head;
        }, '');
        return mimeTypes.some((mimeTypeCode) => header.toLowerCase().includes(mimeTypeCode.toLowerCase()));
    }
};
exports.FileMimeTypeService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.FileReaderService,
        ISettingsService])
], exports.FileMimeTypeService);

/** Used to build a validator for a specified list of file validator. */
class FileValidatorFactory {
    /**
     * Builds a new validator for a specified list of validator objects.
     * Each validator object must consist of a parameter to validate, a predicate function to run against the value and
     * a message to associate with this predicate function's fail case.
     *
     * For example, the resulting Object Validator has a validate method that takes two parameters:
     * an Object to validate against and an optional Contextual Error List to append errors to.
     *
     * ```
     *      const validators = [{
     *          subject: 'code',
     *          validate: function(code) {
     *              return code !== 'Invalid';
     *          },
     *          message: 'Code must not be "Invalid"'
     *      }]
     *
     *      const validator = fileValidatorFactory.build(validators);
     *      const errorsContext = []
     *      const objectUnderValidation = {
     *          code: 'Invalid'
     *      };
     *      const isValid = validator.validate(objectUnderValidation, errorsContext);
     * ```
     *
     * The result of the above code block would be that isValid is false because it failed the predicate function of
     * the single validator in the validator list and the errorsContext would be as follows:
     *
     * ```
     *      [{
     *          subject: 'code',
     *          message: 'Code must not be "Invalid"'
     *      }]
     * ```
     *
     * @param validators A list of validator objects as specified above.
     * @returns A validator that consists of a validate function.
     */
    build(validators) {
        return {
            validate: (objectUnderValidation, maxUploadFileSize, errorsContext) => this.validate(validators, objectUnderValidation, maxUploadFileSize, errorsContext)
        };
    }
    validate(validators, objectUnderValidation, objectCompareValidation, errorsContext) {
        errorsContext = errorsContext || [];
        validators.forEach((validator) => {
            const valueToValidate = objectUnderValidation[validator.subject];
            if (!validator.validate(valueToValidate, objectCompareValidation)) {
                errorsContext.push({
                    subject: validator.subject,
                    message: validator.message
                });
            }
        });
        return errorsContext.length === 0;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* tslint:disable:max-classes-per-file */
/**
 * Event payload when a property changes.
 */
class InputPropertyChange {
    constructor(key, value) {
        this.key = key;
        this.value = value;
    }
}
/**
 * Used for storing component input values for the dynamic component. The values
 * are set onto the dynamic component's properties that are decorated by the @DynamicInput()
 * decorator. Values can be retrieved or set programmatically by the form element's 'input'
 * property.
 */
class InputProperties {
    constructor(object = {}) {
        this.changes = new rxjs.Subject();
        this._map = new Map(lodash.toPairs(object));
    }
    /**
     * Get a property.
     *
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined} value
     */
    get(key) {
        return this._map.get(key);
    }
    /**
     * Setting a property.
     *
     * @param {keyof T} key
     * @param {T[keyof T]} value
     * @param {boolean} emit If emit is set to false. It will not emit changes to the
     * the component for those observing for property changes.
     */
    set(key, value, emit = true) {
        this._map.set(key, value);
        if (emit) {
            this.changes.next(new InputPropertyChange(key, value));
        }
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * ValidatorParameters holds data to the synchronous and
 * asynchronous validators configuration for a FormField.
 */
class ValidatorParameters {
    constructor(validators = {}, asyncValidators = {}) {
        this.validators = {};
        this.asyncValidators = {};
        this.validators = this._omitUndefinedValues(validators);
        this.asyncValidators = this._omitUndefinedValues(asyncValidators);
    }
    /**
     * Determines if synchronous validator exists.
     *
     * @param name The name of the synchronous validator.
     * @returns A boolean if it has that parameter.
     */
    has(name) {
        return this.validators.hasOwnProperty(name);
    }
    /**
     * Returns parameters of the synchronous validator.
     *
     * @param name The name of the synchronous validator.
     * @returns The param of the validator.
     */
    get(name) {
        if (!this.has(name)) {
            return null;
        }
        return this.validators[name];
    }
    /**
     * Determines if asynchronous validator exists.
     *
     * @param name The name of the asynchronous validator.
     * @returns A boolean if it has that parameter.
     */
    hasAsync(name) {
        return this.asyncValidators.hasOwnProperty(name);
    }
    /**
     * Returns parameters of the asynchronous validator.
     *
     * @param name The name of the asynchronous validator.
     * @returns The param of the validator.
     */
    getAsync(name) {
        if (!this.hasAsync(name)) {
            return null;
        }
        return this.asyncValidators[name];
    }
    /**
     * @internal
     * Returns a object with all those keys that have
     * undefined values.
     */
    _omitUndefinedValues(object) {
        return lodash.omitBy(object, lodash.isUndefined);
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * A leaf node of forms.
 */
class FormField extends forms.FormControl {
    constructor(value = {}, validatorOrOpts = {}, { component, inputs = new InputProperties(), validatorParams = new ValidatorParameters(), persist = true }) {
        super(value, validatorOrOpts);
        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }
    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput(key, value) {
        this.inputs.set(key, value);
    }
    /**
     * @inheritdoc
     * @param key
     */
    getInput(key) {
        return this.inputs.get(key);
    }
    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue() {
        if (!this.persist) {
            return undefined;
        }
        return this.value === undefined ? null : this.value;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * A FormGrouping is used to encapsulate form data
 * of objects.
 */
class FormGrouping extends forms.FormGroup {
    constructor(controls, validatorOrOpts = {}, { component, inputs = new InputProperties(), validatorParams = new ValidatorParameters(), persist = true }) {
        super(controls, validatorOrOpts);
        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }
    /**
     * @inheritdoc
     * @param {keyof T} key
     * @param {T[keyof T]} value
     */
    setInput(key, value) {
        this.inputs.set(key, value);
    }
    /**
     * @inheritdoc
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined}
     */
    getInput(key) {
        return this.inputs.get(key);
    }
    /**
     * Manually sets nested errors to each FormControl.
     *
     * Note: Method should be called on the next rendering cycle and not on the initialization of the form. Should be
     * used to enforce backend validation.
     *
     * @param errors
     */
    setNestedErrors(errors = []) {
        errors.forEach(([path, validationErrors]) => {
            const form = this.get(path);
            /**
             * Fail if the form does not exist.
             */
            if (!form) {
                throw new Error(`FormGrouping - Path not found when setting nested error: ${path}`);
            }
            form.setErrors(validationErrors);
        });
    }
    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue() {
        return Object.keys(this.controls).reduce((acc, key) => {
            const child = this.controls[key];
            /**
             * Look ahead and if nested does not want to be mapped, merge the nested object with
             * the current object.
             *
             * If it's a field, then it's undefined.
             *
             * If it's a group, then it will be merged.
             */
            if (!child.persist) {
                return Object.assign(Object.assign({}, acc), child.getPersistedValue());
            }
            acc[key] = child.getPersistedValue();
            return acc;
        }, {});
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class FormList extends forms.FormArray {
    constructor(controls, validatorOrOpts = {}, { component, inputs = new InputProperties(), validatorParams = new ValidatorParameters(), persist = true }) {
        super(controls, validatorOrOpts);
        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }
    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput(key, value) {
        this.inputs.set(key, value);
    }
    /**
     * @inheritdoc
     * @param key
     */
    getInput(key) {
        return this.inputs.get(key);
    }
    /**
     * @inheritdoc
     * @override
     */
    getPersistedValue() {
        return this.controls.reduce((acc, child) => {
            if (!child.persist) {
                /**
                 * Look ahead and merge the values of the
                 * nested group, array or field.
                 *
                 * If it's a field, the values of the field
                 * would be an empty array, because it's not an object.
                 */
                return acc.concat(lodash.values(child.getPersistedValue()));
            }
            acc.push(child.getPersistedValue());
            return acc;
        }, []);
    }
    /**
     * The size of the list.
     */
    size() {
        return this.controls.length;
    }
    /**
     * Swaps a form element in the array.
     *
     * @param a The index of form a.
     * @param b The index of form b.
     */
    swapFormElements(a, b) {
        if (!this._isInBounds(a) || !this._isInBounds(b) || a === b) {
            return;
        }
        /**
         * Swapping control's array.
         */
        const control = this.at(a);
        this.insert(a, this.at(b));
        this.insert(b, control);
    }
    /**
     * Moves a form element in the array to a new position.
     *
     * @param from The previous index.
     * @param to The targeted index.
     */
    moveFormElement(from, to) {
        if (!this._isInBounds(from) || !this._isInBounds(to) || from === to) {
            return;
        }
        const delta = to < from ? -1 : 1;
        const tempControl = this.at(from);
        for (let i = from; (i - to) * delta < 0; i += delta) {
            const position = i + delta;
            this.setControl(i, this.at(position));
        }
        this.setControl(to, tempControl);
    }
    /**
     * Checks of the index is in bounds.
     *
     * @internal
     * @param index
     */
    _isInBounds(index) {
        return index < this.controls.length && index >= 0;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/* tslint:disable:max-classes-per-file */
/**
 * @internal
 * Internal property on the constructor used for adding decorator metadata
 * so that it can be later picked up after component compilation.
 */
const FORM_PROP = Symbol('_form_prop_');
/**
 * Base data PropDecorator.
 * @internal
 */
class PropDecorator {
    constructor(property) {
        this.property = property;
    }
}
/**
 * @internal
 */
class InputPropDecorator extends PropDecorator {
    constructor(property, alias) {
        super(property);
        this.alias = alias ? alias : this.property;
    }
}
/**
 * @internal
 */
class FormPropDecorator extends PropDecorator {
}
/**
 * Used for tagging dynamic inputs and adding them to the FORM_PROP property
 * of the target constructor.
 */
function makePropertyDecorator(factory) {
    return (target, key) => {
        const ctor = target.constructor;
        if (!ctor[FORM_PROP]) {
            ctor[FORM_PROP] = [];
        }
        ctor[FORM_PROP].push(factory(key));
    };
}
/**
 * Injects the AbstractForm for the dynamic form component.
 */
function DynamicForm() {
    return makePropertyDecorator((key) => new FormPropDecorator(key));
}
/**
 * Injects a property of the AbstractForm for the dynamic form component.
 * Inputs are assigned from the FormSchema's 'inputs' property.
 * NOTE:
 * Property values are only available ngOnInit or onDynamicInputChange.
 * @param alias Use this alias to target a property of the AbstractForm. Defaults
 * to the assigned class property.
 * @example
 * <pre>
 *     @Component({ ... })
 *     export class DynamicFormComponent {
 *         @DynamicInput()
 *         property: string
 *     }
 * <pre>
 */
const DynamicInput = (alias = null) => makePropertyDecorator((key) => new InputPropDecorator(key, alias));

exports.FormListerComponent = class FormListerComponent {
    get forms() {
        return lodash.values(this.form.controls);
    }
};
__decorate([
    DynamicForm(),
    __metadata("design:type", FormGrouping)
], exports.FormListerComponent.prototype, "form", void 0);
exports.FormListerComponent = __decorate([
    core.Component({
        selector: 'form-lister',
        styles: [
            `
            :host {
                display: block;
            }
        `
        ],
        template: `<ng-template [formRenderer]="form" *ngFor="let form of forms"></ng-template>`
    })
], exports.FormListerComponent);

/**
 * @internal
 * Trigger property changes for the component and mark for check
 * for those components that have onPush change detection strategy.
 *
 * @param {ComponentRef<any>} componentRef
 */
const onChange = ({ changeDetectorRef, instance }) => {
    instance.onDynamicInputChange && instance.onDynamicInputChange();
    changeDetectorRef.markForCheck();
};
/**
 * @internal
 * Decorates the components with the decorators that were put into places.
 * The idea is made similar to how Angular decorates their properties with inputs.
 */
const decorate = (componentRef, form) => {
    const instance = componentRef.instance;
    const decorators = instance.constructor[FORM_PROP];
    if (!Array.isArray(decorators)) {
        return new rxjs.Subscription();
    }
    const props = new Map();
    decorators.forEach((decorator) => {
        const property = decorator.property;
        if (decorator instanceof InputPropDecorator) {
            const alias = decorator.alias;
            if (form.getInput(alias) === undefined && instance[property] !== undefined) {
                form.setInput(alias, instance[property]);
            }
            instance[property] = form.getInput(alias);
            props.set(alias, decorator);
        }
        else if (decorator instanceof FormPropDecorator) {
            instance[property] = form;
        }
    });
    onChange(componentRef);
    return form.inputChanges.subscribe(({ key, value }) => {
        const decorator = props.get(key);
        if (!decorator) {
            return;
        }
        instance[decorator.property] = value;
        onChange(componentRef);
    });
};

exports.FormRendererDirective = class FormRendererDirective {
    constructor(componentFactoryResolver, viewContainerRef) {
        this.componentFactoryResolver = componentFactoryResolver;
        this.viewContainerRef = viewContainerRef;
    }
    set form(form) {
        this._dispose();
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(form.component);
        // Create and decorate the component's inputs.
        const componentRef = this.viewContainerRef.createComponent(componentFactory);
        this._subscription = decorate(componentRef, form);
    }
    ngOnDestroy() {
        this._dispose();
    }
    /**
     * @internal
     * Clear all views and unsubscribe to streams.
     */
    _dispose() {
        this._subscription && this._subscription.unsubscribe();
        this.viewContainerRef.clear();
    }
};
__decorate([
    core.Input('formRenderer'),
    __metadata("design:type", Object),
    __metadata("design:paramtypes", [Object])
], exports.FormRendererDirective.prototype, "form", null);
exports.FormRendererDirective = __decorate([
    core.Directive({
        selector: '[formRenderer]'
    }),
    __metadata("design:paramtypes", [core.ComponentFactoryResolver,
        core.ViewContainerRef])
], exports.FormRendererDirective);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const COMPONENT_MAP = new core.InjectionToken('COMPONENT_MAP');
const ASYNC_VALIDATOR_MAP = new core.InjectionToken('ASYNC_VALIDATOR_MAP');
const VALIDATOR_MAP = new core.InjectionToken('VALIDATOR_MAP');

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
// File named form-builder.module for now cuz of build blocking me to use Module suffix.
var FormBuilderModule_1;
exports.FormBuilderModule = FormBuilderModule_1 = class FormBuilderModule {
    static forRoot(option) {
        return {
            ngModule: FormBuilderModule_1,
            providers: [
                {
                    provide: core.ANALYZE_FOR_ENTRY_COMPONENTS,
                    useValue: [option.types],
                    multi: true
                },
                {
                    provide: COMPONENT_MAP,
                    useValue: option.types
                },
                {
                    provide: VALIDATOR_MAP,
                    useValue: option.validators
                },
                {
                    provide: ASYNC_VALIDATOR_MAP,
                    useValue: option.asyncValidators
                }
            ]
        };
    }
};
exports.FormBuilderModule = FormBuilderModule_1 = __decorate([
    core.NgModule({
        imports: [common.CommonModule, forms.ReactiveFormsModule],
        declarations: [exports.FormRendererDirective, exports.FormListerComponent],
        entryComponents: [exports.FormListerComponent],
        exports: [exports.FormRendererDirective]
    })
], exports.FormBuilderModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @internal
 *
 * Generic registry for mapping keys to items.
 */
class Registry {
    constructor(items = {}) {
        this._map = new Map(lodash.toPairs(items));
    }
    /**
     * Adds a item to the registry.
     *
     * @param {string} name
     * @param {T} item
     */
    add(name, item) {
        if (this._map.has(name)) {
            throw new Error(`${this._service}: is overriding an element named '${name}' in its registry.`);
        }
        this._map.set(name, item);
    }
    /**
     * Gets an a item in the registry.
     *
     * @param {string} name
     * @returns {T}
     */
    get(name) {
        if (!this._map.has(name)) {
            throw new Error(`${this._service}: does not have '${name}' in its registry.`);
        }
        return this._map.get(name);
    }
    /**
     * @internal
     */
    get _service() {
        return this.constructor.name;
    }
}

/**
 * A registry for form components.
 */
exports.ComponentRegistryService = class ComponentRegistryService extends Registry {
    constructor(types) {
        super(types);
    }
};
exports.ComponentRegistryService = __decorate([
    core.Injectable({
        providedIn: 'root'
    }),
    __param(0, core.Optional()),
    __param(0, core.Inject(COMPONENT_MAP)),
    __metadata("design:paramtypes", [Object])
], exports.ComponentRegistryService);

/**
 * A registry for asynchronous validators.
 */
exports.AsyncValidatorRegistryService = class AsyncValidatorRegistryService extends Registry {
    constructor(asyncValidators) {
        super(asyncValidators);
    }
};
exports.AsyncValidatorRegistryService = __decorate([
    core.Injectable({
        providedIn: 'root'
    }),
    __param(0, core.Optional()),
    __param(0, core.Inject(ASYNC_VALIDATOR_MAP)),
    __metadata("design:paramtypes", [Object])
], exports.AsyncValidatorRegistryService);

/**
 * A registry for synchronous validators.
 */
exports.ValidatorRegistryService = class ValidatorRegistryService extends Registry {
    constructor(validators) {
        super(validators);
    }
};
exports.ValidatorRegistryService = __decorate([
    core.Injectable({
        providedIn: 'root'
    }),
    __param(0, core.Optional()),
    __param(0, core.Inject(VALIDATOR_MAP)),
    __metadata("design:paramtypes", [Object])
], exports.ValidatorRegistryService);

/**
 * Schema compilers service is used for compiling a schema to concrete classes for use
 * by the FormRendererDirective.
 */
exports.SchemaCompilerService = class SchemaCompilerService {
    constructor(types, validators, asyncValidators) {
        this.types = types;
        this.validators = validators;
        this.asyncValidators = asyncValidators;
    }
    /**
     * Compile a schema group.
     *
     * @param value
     * @param schema
     * @returns
     */
    compileGroup(value, schema, options = {}) {
        const abstractForms = Object.keys(schema.schemas).reduce((acc, key) => {
            acc[key] = this._toAbstractForm(value ? value[key] : null, schema.schemas[key], options);
            return acc;
        }, {});
        return new FormGrouping(abstractForms, this._getValidators(schema, options), {
            component: schema.component
                ? this._getComponent(schema.component, options.components)
                : exports.FormListerComponent,
            inputs: new InputProperties(schema.inputs),
            validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
            persist: this._toPersist(schema.persist)
        });
    }
    /**
     * Compiles a list of values with a schema.
     *
     * @param values An array of values.
     * @param listSchema
     */
    compileList(values, schema, options = {}) {
        /**
         * The schema list for each value since each value can have different
         * schemas. Or they can have the same schema for all values in the list.
         */
        const schemaList = Array.isArray(schema.schema) ? schema.schema : [schema.schema];
        if (!schemaList.length) {
            throw Error('SchemaCompilerService - One or more schemas must be provided to compile a form list.');
        }
        const list = (Array.isArray(values) ? values : []).map((value, index) => {
            const childSchema = schemaList[index]
                ? /**
                   * Get the schema one to one for the value, or get the last schema
                   * which may be repeated for all values.
                   */
                    schemaList[index]
                : schemaList[schemaList.length - 1];
            return this._toAbstractForm(value, childSchema, options);
        });
        return new FormList(list, this._getValidators(schema, options), {
            component: this._getComponent(schema.component, options.components),
            inputs: new InputProperties(schema.inputs),
            validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
            persist: this._toPersist(schema.persist)
        });
    }
    /**
     * Compiles a schema field.
     *
     * @param value
     * @param {FormFieldSchema} schema
     * @returns {FormField}
     */
    compileField(value, schema, options = {}) {
        return new FormField({ value, disabled: schema.disabled }, this._getValidators(schema, options), {
            component: this._getComponent(schema.component, options.components),
            inputs: new InputProperties(schema.inputs),
            validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
            persist: this._toPersist(schema.persist)
        });
    }
    /**
     * @internal
     * Returns form validators and ayncValidators
     * @param schema
     * @param options
     */
    _getValidators(schema, options = {}) {
        let validators = [];
        let asyncValidators = [];
        if (schema.validators) {
            validators = this._mapValidator(schema.validators, this.validators, options.validators);
        }
        if (schema.asyncValidators) {
            asyncValidators = this._mapValidator(schema.asyncValidators, this.asyncValidators, options.asyncValidators);
        }
        return {
            validators,
            asyncValidators
        };
    }
    /**
     * @internal
     * @param value
     * @param schema
     */
    _toAbstractForm(value, schema, options) {
        if (schema.type === 'field') {
            return this.compileField(value, schema, options);
        }
        if (schema.type === 'group') {
            return this.compileGroup(value, schema, options);
        }
        return this.compileList(value, schema, options);
    }
    /**
     * @internal
     *
     * Maps schema validators to actual validators in the registry and passes custom params to a validator.
     * If params are undefined then the validator isn't added to the array of validators. Validators
     * that are found the inline registry will take precedence of those in registries.
     */
    _mapValidator(validators, registry, inline = {}) {
        return Object.keys(validators).reduce((acc, name) => {
            const params = validators[name];
            if (params !== undefined) {
                const fn = inline[name] ? inline[name] : registry.get(name);
                if (!fn) {
                    throw new Error(`SchemaCompilerService - Validator not found in ${this.validators.constructor.name} for: ${name}.`);
                }
                acc.push(fn(params));
            }
            return acc;
        }, []);
    }
    /**
     * @internal
     * Sets default to true if parameter persist is undefined.
     */
    _toPersist(persist = true) {
        return persist;
    }
    /**
     * @internal
     * Decides if should get the type from the inline map or registry.
     * If no component is found, it would throw an error.
     *
     * @param name The name of the component in the registry.
     * @param components An component type name, used for inline components.
     */
    _getComponent(name, components = {}) {
        const comp = components[name] ? components[name] : this.types.get(name);
        if (!comp) {
            throw new Error(`SchemaCompilerService - Did not find component for: ${name}.`);
        }
        return comp;
    }
};
exports.SchemaCompilerService = __decorate([
    core.Injectable({
        providedIn: exports.FormBuilderModule
    }),
    __metadata("design:paramtypes", [exports.ComponentRegistryService,
        exports.ValidatorRegistryService,
        exports.AsyncValidatorRegistryService])
], exports.SchemaCompilerService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Get an address book of persisting fields to the actual form path.
 * Example:
 * {
 *   'property': ['tab', 'property']
 *   ...
 * }
 * Where tab is not a persisting property of the model.
 */
const getPersistenceMap = (form, map = {}, from = [], to = []) => {
    if (form instanceof FormField) {
        if (form.persist) {
            map[from.join('.')] = to;
        }
        return map;
    }
    if (form instanceof FormList || form instanceof FormGrouping) {
        if (form.persist && from.length) {
            map[from.join('.')] = to;
        }
        Object.keys(form.controls).forEach((current) => {
            const child = form.controls[current];
            const toActual = [...to, current];
            if (child.persist) {
                return getPersistenceMap(child, map, [...from, current], toActual);
            }
            return getPersistenceMap(child, map, from, toActual);
        });
    }
    return map;
};

/**
 * Recursively explore schema to return a form data structure
 * which is used to generated dynamic forms.
 *
 * If a schema is a field, simply return the related data
 *
 * If a schema is a group or list, explore its inner
 * schemas and return equivalent value
 *
 * @param data persisted data object
 * @param schema related schema
 */
const toFormValue = (data, schema) => {
    if (schema.type === 'field') {
        // Return related value
        return data;
    }
    if (schema.type === 'group') {
        return processGroupSchema(data, schema);
    }
    return processListSchema(data, schema);
};
/**
 * Process schemas of type FormGroupSchema.
 *
 * @param data persisted data object
 * @param schema related schema
 */
const processGroupSchema = (data, schema) => {
    const value = {};
    // Populate fields of value based on inner schemas
    Object.keys(schema.schemas).forEach((key) => {
        // Create inner data object with only data related to the inner schema
        const innerData = getInnerData(data, key, schema.schemas[key].type);
        value[key] = toFormValue(innerData, schema.schemas[key]);
    });
    return value;
};
/**
 * Process schemas of type FormListSchema.
 *
 * @param data persisted data object
 * @param schema related schema
 */
const processListSchema = (data, schema) => {
    // If schema is a list, related data must also be a list
    const listSchema = schema;
    const listValue = [];
    const listData = Array.isArray(data) ? data : [];
    // If there is only one inner schema, treat it as an array of 1
    const innerSchemas = Array.isArray(listSchema.schema) ? listSchema.schema : [listSchema.schema];
    listData.forEach((el, i) => {
        // If there is more data than schemas, use the last one.
        if (i > innerSchemas.length - 1) {
            i = innerSchemas.length - 1;
        }
        listValue.push(toFormValue(el, innerSchemas[i]));
    });
    return listValue;
};
/**
 * Return inner field of persisted data.
 *
 * If inner field is found, then it is returned
 * If the schema is a field, and the data field wasn't found, return null
 * Otherwise, the data object itself is returned.
 *
 * @param data persisted data object
 * @param key name of the value to retrieve from data
 * @param schemaType type of the related schema
 */
const getInnerData = (data, key, schemaType) => {
    // If data is null or undefined, return null
    if (data == null) {
        return null;
    }
    const value = data[key];
    if (value !== undefined) {
        return value;
    }
    else if (schemaType === 'field') {
        return null;
    }
    return data;
};

exports.ALERT_CONFIG_DEFAULTS = ALERT_CONFIG_DEFAULTS;
exports.ALERT_CONFIG_DEFAULTS_TOKEN = ALERT_CONFIG_DEFAULTS_TOKEN;
exports.AbstractCachedRestService = AbstractCachedRestService;
exports.Alert = Alert;
exports.AnnotationService = AnnotationService;
exports.BackendEntry = BackendEntry;
exports.BaseValueAccessor = BaseValueAccessor;
exports.BooleanUtils = BooleanUtils;
exports.BrowserService = BrowserService;
exports.CacheAction = CacheAction;
exports.CacheConfig = CacheConfig;
exports.CacheConfigAnnotationFactory = CacheConfigAnnotationFactory;
exports.Cached = Cached;
exports.CachedAnnotationFactory = CachedAnnotationFactory;
exports.CloneableUtils = CloneableUtils;
exports.CryptographicUtils = CryptographicUtils;
exports.DEFAULT_AUTHENTICATION_CLIENT_ID = DEFAULT_AUTHENTICATION_CLIENT_ID;
exports.DEFAULT_AUTHENTICATION_ENTRY_POINT = DEFAULT_AUTHENTICATION_ENTRY_POINT;
exports.DEFAULT_AUTH_MAP = DEFAULT_AUTH_MAP;
exports.DEFAULT_CREDENTIALS_MAP = DEFAULT_CREDENTIALS_MAP;
exports.DEFAULT_LANGUAGE_ISO = DEFAULT_LANGUAGE_ISO;
exports.DefaultCacheTiming = DefaultCacheTiming;
exports.DefaultRetryStrategy = DefaultRetryStrategy;
exports.DynamicForm = DynamicForm;
exports.DynamicInput = DynamicInput;
exports.EVENTS = EVENTS;
exports.EVENT_SERVICE = EVENT_SERVICE;
exports.EXPONENTIAL_RETRY_DEFAULT_SETTING = EXPONENTIAL_RETRY_DEFAULT_SETTING;
exports.EvictionTag = EvictionTag;
exports.ExponentialRetry = ExponentialRetry;
exports.ExponentialRetryStrategy = ExponentialRetryStrategy;
exports.FORM_PROP = FORM_PROP;
exports.FileValidatorFactory = FileValidatorFactory;
exports.FingerPrintingService = FingerPrintingService;
exports.FormField = FormField;
exports.FormGrouping = FormGrouping;
exports.FormList = FormList;
exports.FormPropDecorator = FormPropDecorator;
exports.FunctionsUtils = FunctionsUtils;
exports.GET_REQUESTS_ON_HOLD_MAP = GET_REQUESTS_ON_HOLD_MAP;
exports.HTTP_METHODS_READ = HTTP_METHODS_READ;
exports.HTTP_METHODS_UPDATE = HTTP_METHODS_UPDATE;
exports.HttpUtils = HttpUtils;
exports.I18N_RESOURCE_URI_TOKEN = I18N_RESOURCE_URI_TOKEN;
exports.I18N_ROOT_RESOURCE_URI = I18N_ROOT_RESOURCE_URI;
exports.IAlertService = IAlertService;
exports.IAuthenticationManagerService = IAuthenticationManagerService;
exports.IAuthenticationService = IAuthenticationService;
exports.IModalService = IModalService;
exports.ISelectAdapter = ISelectAdapter;
exports.ISessionService = ISessionService;
exports.ISettingsService = ISettingsService;
exports.ISharedDataService = ISharedDataService;
exports.IStorageService = IStorageService;
exports.ITranslationsFetchService = ITranslationsFetchService;
exports.InputPropDecorator = InputPropDecorator;
exports.InputProperties = InputProperties;
exports.InputPropertyChange = InputPropertyChange;
exports.InvalidateCache = InvalidateCache;
exports.InvalidateCacheAnnotationFactory = InvalidateCacheAnnotationFactory;
exports.LANDING_PAGE_PATH = LANDING_PAGE_PATH;
exports.LANGUAGE_SERVICE = LANGUAGE_SERVICE;
exports.LANGUAGE_SERVICE_CONSTANTS = LANGUAGE_SERVICE_CONSTANTS;
exports.LIBRARY_NAME = LIBRARY_NAME;
exports.LINEAR_RETRY_DEFAULT_SETTING = LINEAR_RETRY_DEFAULT_SETTING;
exports.LanguageDropdown = LanguageDropdown;
exports.LanguageDropdownAdapter = LanguageDropdownAdapter;
exports.LanguageDropdownHelper = LanguageDropdownHelper;
exports.LinearRetry = LinearRetry;
exports.LinearRetryStrategy = LinearRetryStrategy;
exports.LogService = LogService;
exports.LoginDialogResourceProvider = LoginDialogResourceProvider;
exports.OPERATION_CONTEXT_TOKEN = OPERATION_CONTEXT_TOKEN;
exports.OperationContextAnnotationFactory = OperationContextAnnotationFactory;
exports.OperationContextRegistered = OperationContextRegistered;
exports.PropDecorator = PropDecorator;
exports.REAUTH_STARTED = REAUTH_STARTED;
exports.RarelyChangingContentName = RarelyChangingContentName;
exports.RestClient = RestClient;
exports.SELECTED_LANGUAGE = SELECTED_LANGUAGE;
exports.SIMPLE_RETRY_DEFAULT_SETTING = SIMPLE_RETRY_DEFAULT_SETTING;
exports.SWITCH_LANGUAGE_EVENT = SWITCH_LANGUAGE_EVENT;
exports.SimpleRetry = SimpleRetry;
exports.StringUtils = StringUtils;
exports.StripResponseHeaders = StripResponseHeaders;
exports.URIBuilder = URIBuilder;
exports.UrlUtils = UrlUtils;
exports.ValidatorParameters = ValidatorParameters;
exports.WHO_AM_I_RESOURCE_URI_TOKEN = WHO_AM_I_RESOURCE_URI_TOKEN;
exports.WindowUtils = WindowUtils;
exports.YJQUERY_TOKEN = YJQUERY_TOKEN;
exports.annotationService = annotationService;
exports.booleanUtils = booleanUtils;
exports.clientErrorPredicate = clientErrorPredicate;
exports.cloneableUtils = cloneableUtils;
exports.commonNgZone = commonNgZone;
exports.defaultRetryStrategyFactory = defaultRetryStrategyFactory;
exports.exponentialRetryStrategyFactory = exponentialRetryStrategyFactory;
exports.functionsUtils = functionsUtils;
exports.getPersistenceMap = getPersistenceMap;
exports.httpUtils = httpUtils;
exports.linearRetryStrategyFactory = linearRetryStrategyFactory;
exports.moduleUtils = moduleUtils;
exports.noInternetConnectionErrorPredicate = noInternetConnectionErrorPredicate;
exports.promiseUtils = promiseUtils;
exports.rarelyChangingContent = rarelyChangingContent;
exports.readPredicate = readPredicate;
exports.retriableErrorPredicate = retriableErrorPredicate;
exports.serverErrorPredicate = serverErrorPredicate;
exports.stringUtils = stringUtils;
exports.timeoutErrorPredicate = timeoutErrorPredicate;
exports.toFormValue = toFormValue;
exports.updatePredicate = updatePredicate;
exports.urlUtils = urlUtils;
exports.windowUtils = windowUtils;
