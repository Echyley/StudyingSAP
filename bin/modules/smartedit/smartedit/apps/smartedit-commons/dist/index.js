'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@angular/core');
var lodash = require('lodash');
var $script = require('scriptjs');
var utils = require('@smart/utils');
var elements = require('@angular/elements');
var http = require('@angular/common/http');
var moment = require('moment');
var rxjs = require('rxjs');
var common = require('@angular/common');
var wizard = require('@fundamental-ngx/core/wizard');
var core$1 = require('@ngx-translate/core');
var router = require('@angular/router');
var operators = require('rxjs/operators');
var platformBrowser = require('@angular/platform-browser');
var core$2 = require('@fundamental-ngx/core');
var list = require('@fundamental-ngx/core/list');
var tabs = require('@fundamental-ngx/core/tabs');
var forms = require('@angular/forms');
var animations = require('@angular/platform-browser/animations');
var dialog = require('@fundamental-ngx/core/dialog');
var busyIndicator = require('@fundamental-ngx/core/busy-indicator');
var resizeObserver = require('@juggle/resize-observer');
var dragDrop = require('@angular/cdk/drag-drop');
var datetime = require('@fundamental-ngx/core/datetime');
var datetimePicker = require('@fundamental-ngx/core/datetime-picker');
var momentAdapter = require('@fundamental-ngx/moment-adapter');
var form = require('@fundamental-ngx/core/form');
var objectStatus = require('@fundamental-ngx/core/object-status');
var upgrade = require('@angular/common/upgrade');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

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
var $script__default = /*#__PURE__*/_interopDefaultLegacy($script);
var moment__default = /*#__PURE__*/_interopDefaultLegacy(moment);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit module as entry point.
 * @param id The module identifier used when loading it into Smartedit.
 */
const SeEntryModule = function (id) {
    return function (moduleConstructor) {
        window.__smartedit__.modules[id] = moduleConstructor;
        return moduleConstructor;
    };
};

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
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * A collection of utility methods for Nodes.
 */
class NodeUtils {
    /**
     * Retrieves all the attributes of an overlay Node (identified by its data-smartedit-element-uuid attribute) containing with data-smartedit- or smartedit-
     * and package them as a map of key values.
     * Keys are stripped of data- and are turned to camelcase
     * @returns Map of key values.
     */
    collectSmarteditAttributesByElementUuid(elementUuid) {
        const element = document.querySelector(`.smartEditComponent[data-smartedit-element-uuid='${elementUuid}']`);
        if (!element) {
            throw new Error(`could not find element with uuid ${elementUuid}`);
        }
        return Array.from(element.attributes).reduce((attributes, node) => {
            let attrName = node.name;
            if (attrName.indexOf('smartedit-') > -1) {
                attrName = lodash.camelCase(attrName.replace('data-', ''));
                attributes[attrName] = node.value;
            }
            return attributes;
        }, {});
    }
    /**
     * A function to sort an array containing DOM elements according to their position in the DOM
     *
     * @param key Optional key value to get the
     *
     * @returns The compare function to use with array.sort(compareFunction) to order DOM elements as they would appear in the DOM
     */
    compareHTMLElementsPosition(key) {
        return function (a, b) {
            if (key) {
                a = a[key];
                b = b[key];
            }
            if (a === b) {
                return 0;
            }
            if (!a.compareDocumentPosition) {
                // support for IE8 and below
                return a.sourceIndex - b.sourceIndex;
            }
            if (a.compareDocumentPosition(b) & 2) {
                // Note: CompareDocumentPosition returns the compared value as a bitmask that can take several values.
                // 2 represents DOCUMENT_POSITION_PRECEDING, which means that b comes before a.
                return 1;
            }
            return -1;
        };
    }
    /**
     * Method will check if the given point is over the htmlElement.
     *
     * @returns True if the given point is over the htmlElement.
     */
    isPointOverElement(point, htmlElement) {
        const domRect = htmlElement.getBoundingClientRect();
        return (point.x >= domRect.left &&
            point.x <= domRect.right &&
            point.y >= domRect.top &&
            point.y <= domRect.bottom);
    }
    /**
     * Determines whether 2 BoundingClientRect are intersecting even partially.
     *
     * @param boundingClientRect1 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @param boundingClientRect2 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @returns True if there is a partial or total intersection
     */
    areIntersecting(boundingClientRect1, boundingClientRect2) {
        return !(boundingClientRect2.left > boundingClientRect1.left + boundingClientRect1.width ||
            boundingClientRect2.left + boundingClientRect2.width < boundingClientRect1.left ||
            boundingClientRect2.top > boundingClientRect1.top + boundingClientRect1.height ||
            boundingClientRect2.top + boundingClientRect2.height < boundingClientRect1.top);
    }
    injectJS() {
        function getInjector() {
            return $script__default["default"];
        }
        return {
            getInjector,
            execute(conf) {
                const srcs = conf.srcs;
                let index = conf.index;
                const callback = conf.callback;
                if (!srcs.length) {
                    callback();
                    return;
                }
                if (index === undefined) {
                    index = 0;
                }
                if (srcs[index] !== undefined) {
                    this.getInjector()(srcs[index], () => {
                        if (index + 1 < srcs.length) {
                            this.execute({
                                srcs,
                                index: index + 1,
                                callback
                            });
                        }
                        else if (typeof callback === 'function') {
                            callback();
                        }
                    });
                }
            }
        };
    }
}
const nodeUtils = new NodeUtils();

const scopes = [];
class AbstractDecorator {
    constructor() {
        scopes.push(this.smarteditElementuuid);
        // only way to ensure inheritence
        const f = this.ngOnDestroy.bind(this);
        this.ngOnDestroy = () => {
            f();
            scopes.splice(scopes.indexOf(this.smarteditElementuuid), 1);
        };
    }
    set active(val) {
        this._active = val === 'true';
    }
    get active() {
        return this._active;
    }
    // for e2e purposes
    static getScopes() {
        return scopes;
    }
    get componentAttributes() {
        if (!this._cachedCcomponentAttributes) {
            try {
                this._cachedCcomponentAttributes =
                    nodeUtils.collectSmarteditAttributesByElementUuid(this.smarteditElementuuid);
            }
            catch (e) {
                // the original element may have been removed
                console.log(`AbstratcDecorator failed to find original element with uuid ${this.smarteditElementuuid}`);
            }
        }
        return this._cachedCcomponentAttributes;
    }
    ngOnDestroy() {
        // no-op
    }
}
__decorate([
    core.Input(),
    __metadata("design:type", Object),
    __metadata("design:paramtypes", [Object])
], AbstractDecorator.prototype, "active", null);
__decorate([
    core.Input('data-smartedit-element-uuid'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditElementuuid", void 0);
__decorate([
    core.Input('data-smartedit-component-id'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditComponentId", void 0);
__decorate([
    core.Input('data-smartedit-component-uuid'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditComponentUuid", void 0);
__decorate([
    core.Input('data-smartedit-component-type'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditComponentType", void 0);
__decorate([
    core.Input('data-smartedit-catalog-version-uuid'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditCatalogVersionUuid", void 0);
__decorate([
    core.Input('data-smartedit-container-id'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditContainerId", void 0);
__decorate([
    core.Input('data-smartedit-container-uuid'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditContainerUuid", void 0);
__decorate([
    core.Input('data-smartedit-container-type'),
    __metadata("design:type", String)
], AbstractDecorator.prototype, "smarteditContainerType", void 0);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @internal */
class DINameUtils {
    buildComponentName(componentConstructor) {
        return this.buildName(componentConstructor)
            .replace(/Component$/, '')
            .replace(/Directive$/, '');
    }
    buildServiceName(serviceConstructor) {
        return this.buildName(serviceConstructor);
    }
    buildModuleName(moduleConstructor) {
        return this.buildName(moduleConstructor).replace(/Module$/, '');
    }
    // builds the DI recipe name for a given construtor
    buildName(myConstructor) {
        const originalConstructor = utils.annotationService.getOriginalConstructor(myConstructor);
        const originalName = utils.functionsUtils.getConstructorName(originalConstructor);
        return this.convertNameCasing(originalName);
    }
    // converts the first character to lower case
    convertNameCasing(originalName) {
        const builtName = originalName.substring(0, 1).toLowerCase() + originalName.substring(1);
        return builtName;
    }
}
const diNameUtils = new DINameUtils();

/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit custom component from a dependency injection standpoint.
 */
const SeCustomComponent = function () {
    return function (componentConstructor) {
        const componentName = utils.functionsUtils.getConstructorName(componentConstructor);
        /* forbiddenNameSpaces window._:false */
        const definition = window.__smartedit__.getDecoratorPayload('Component', componentConstructor);
        if (!definition) {
            throw new Error(`@SeCustomComponent ${componentName} should only be used on a @Component decorated class`);
        }
        const selector = parseComponentSelector(definition.selector, componentConstructor);
        componentConstructor.selector = selector;
        seCustomComponents.unshift(componentConstructor);
        return componentConstructor;
    };
};
const parseComponentSelector = function (selector, seContructor) {
    if (!selector) {
        return window.smarteditLodash.kebabCase(diNameUtils.buildComponentName(seContructor));
    }
    else {
        return selector;
    }
};
const seCustomComponents = [];
function registerCustomComponents(injector) {
    // create a custom element for every @SeComponent flagged with custom:true
    seCustomComponents.forEach((myConstructor) => {
        if (!customElements.get(myConstructor.selector)) {
            // Convert to a custom element.
            const customComponent = elements.createCustomElement(myConstructor, { injector });
            // Register the custom element with the browser.
            customElements.define(myConstructor.selector, customComponent);
        }
    });
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const CONTEXTUAL_MENU_ITEM_DATA = new core.InjectionToken('CONTEXTUAL_MENU_ITEM_DATA');

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ignore
 * Model containing truncated text properties.
 */
class TruncatedText {
    constructor(text = '', truncatedText = '', truncated, ellipsis = '') {
        this.text = text;
        this.truncatedText = truncatedText;
        this.truncated = truncated;
        this.ellipsis = ellipsis;
        // if text/truncatedText is null, then set its value to ""
        this.text = this.text || '';
        this.truncatedText = this.truncatedText || '';
    }
    getUntruncatedText() {
        return this.text;
    }
    getTruncatedText() {
        return this.truncatedText + this.ellipsis;
    }
    isTruncated() {
        return this.truncated;
    }
}

var /* @ngInject */ WindowUtils_1;
/* forbiddenNameSpaces window._:false */
window.__smartedit__ = window.__smartedit__ || {};
function getDecoratorKey(className, decoratorName) {
    return `${className}-${decoratorName}:definition`;
}
lodash__namespace.merge(window.__smartedit__, {
    modules: {},
    pushedModules: [],
    smarteditDecoratorPayloads: {},
    addDecoratorPayload(decorator, className, payload) {
        const key = getDecoratorKey(decorator, className);
        if (window.__smartedit__.smarteditDecoratorPayloads[key]) {
            throw new Error('Duplicate class/decorator pair');
        }
        window.__smartedit__.smarteditDecoratorPayloads[key] = payload;
    },
    getDecoratorPayload(decorator, myConstructor) {
        const className = utils.functionsUtils.getConstructorName(utils.annotationService.getOriginalConstructor(myConstructor));
        return window.__smartedit__.smarteditDecoratorPayloads[getDecoratorKey(decorator, className)];
    },
    getComponentDecoratorPayload(className) {
        return window.__smartedit__.smarteditDecoratorPayloads[getDecoratorKey('Component', className)];
    },
    downgradedService: {}
});
/**
 * Add here a spread of modules containing invocations of HttpBackendService to mocks some calls to the backend
 */
window.pushModules = (...modules) => {
    window.__smartedit__.pushedModules = window.__smartedit__.pushedModules.concat(modules);
};
/**
 * A collection of utility methods for windows.
 */
/* @ngInject */ exports.WindowUtils = /* @ngInject */ WindowUtils_1 = class /* @ngInject */ WindowUtils extends utils.WindowUtils {
    constructor(ngZone) {
        super(ngZone);
        /**
         * Given the current frame, retrieves the target frame for gateway purposes
         *
         * @returns The content window or null if it does not exists.
         */
        this.getGatewayTargetFrame = () => {
            /*
             * For below if-else condition, original code won't work if smartedit is running
             * under environment of cypress for testing purpose, so condition to check against cypress environment is added
             * See jira ticket for details: https://jira.tools.sap/browse/CXEC-7344 & 9335
             */
            if (this._isInCypressIframe()) {
                return this._getCypressFrame();
            }
            // return the parent frame if called within the iframe
            if (this.isIframe()) {
                return this.getWindow().parent;
            }
            // return the iframe if called from the parent window
            if (this._isParentOfIframe()) {
                return this._getSmarteditIframeFromWindow();
            }
            // return null when called from the parent and no iframe exists
            return null;
        };
    }
    getSmarteditIframe() {
        return document.querySelector('iframe#' + /* @ngInject */ WindowUtils_1.SMARTEDIT_IFRAME_ID);
    }
    setTrustedIframeDomain(trustedIframeSource) {
        this.trustedIframeDomain = utils.urlUtils.getOrigin(trustedIframeSource);
    }
    getTrustedIframeDomain() {
        return this.trustedIframeDomain;
    }
    isCrossOrigin(location) {
        return utils.urlUtils.getOrigin() !== utils.urlUtils.getOrigin(location);
    }
    /**
     * Will call the javascrit's native setTimeout method to execute a given function after a specified period of time.
     * This method is better than using $timeout since it is difficult to assert on $timeout during end-to-end testing.
     *
     * @param func function that needs to be executed after the specified duration.
     * @param duration time in milliseconds.
     */
    customTimeout(func, duration) {
        setTimeout(function () {
            func();
        }, duration);
    }
    _getCypressFrame() {
        if (this.getWindow().parent.document.querySelector('iframe#' + /* @ngInject */ WindowUtils_1.SMARTEDIT_IFRAME_ID)) {
            return this.getWindow().parent;
        }
        return this._getSmarteditIframeFromWindow();
    }
    _getSmarteditIframeFromWindow() {
        const smarteditIframe = this.getWindow().document.getElementById(/* @ngInject */ WindowUtils_1.SMARTEDIT_IFRAME_ID);
        if (!!smarteditIframe) {
            return smarteditIframe.contentWindow;
        }
        return null;
    }
    _isInCypressIframe() {
        try {
            const iframe = this.getWindow().top.document.querySelector('iframe');
            return (iframe &&
                (iframe.id.includes(/* @ngInject */ WindowUtils_1.CYPRESS_IFRAME_ID_9) ||
                    iframe.id.includes(/* @ngInject */ WindowUtils_1.CYPRESS_IFRAME_ID_10)));
        }
        catch (e) {
            return false;
        }
    }
    _isParentOfIframe() {
        return !!this._getSmarteditIframeFromWindow();
    }
};
/* @ngInject */ exports.WindowUtils.SMARTEDIT_IFRAME_ID = 'ySmartEditFrame';
/* @ngInject */ exports.WindowUtils.CYPRESS_IFRAME_ID_9 = 'Your App';
/* @ngInject */ exports.WindowUtils.CYPRESS_IFRAME_ID_10 = "Your project: 'Test Project'";
/* @ngInject */ exports.WindowUtils = /* @ngInject */ WindowUtils_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core.NgZone])
], /* @ngInject */ exports.WindowUtils);
const windowUtils = new exports.WindowUtils();

window.__smartedit__.addDecoratorPayload("Injectable", "ModuleUtils", { providedIn: 'root' });
/**
 * Internal utility service to handle ES6 modules
 *
 * @internal
 * @ignore
 */
/* @ngInject */ exports.ModuleUtils = class /* @ngInject */ ModuleUtils extends utils.ModuleUtils {
    constructor() {
        super();
    }
    getNgModule(appName) {
        if (window.__smartedit__.modules) {
            const moduleKey = Object.keys(window.__smartedit__.modules).find((key) => key.toLowerCase().endsWith(appName.toLowerCase()) ||
                key.toLowerCase().endsWith(appName.toLowerCase() + 'module'));
            if (!moduleKey) {
                return null;
            }
            return window.__smartedit__.modules[moduleKey];
        }
        return null;
    }
};
/* @ngInject */ exports.ModuleUtils = __decorate([
    core.Injectable({ providedIn: 'root' }),
    __metadata("design:paramtypes", [])
], /* @ngInject */ exports.ModuleUtils);
const moduleUtils = new exports.ModuleUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * A collection of utility methods for windows.
 */
class StringUtils extends utils.StringUtils {
    /**
     * Remove breaks and space.
     */
    sanitizeHTML(text) {
        if (stringUtils.isBlank(text)) {
            return text;
        }
        return text
            .replace(/(\r\n|\n|\r)/gm, '')
            .replace(/>\s+</g, '><')
            .replace(/<\/br\>/g, '');
    }
    /**
     * Generates a unique string based on system time and a random generator.
     */
    generateIdentifier() {
        let d = new Date().getTime();
        if (window.performance && typeof window.performance.now === 'function') {
            d += window.performance.now(); // use high-precision timer if available
        }
        const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16);
        });
        return uuid;
    }
    /**
     * Creates a base-64 encoded ASCII string from the object or string.
     */
    getEncodedString(input) {
        return this.encode(input);
    }
    /**
     * **Deprecated since 2005, use {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim API}.**
     *
     * Removes spaces at the beginning and end of a given string.
     *
     * @returns Modified string without spaces at the beginning and the end
     *
     * @deprecated
     */
    trim(aString) {
        return aString.trim();
    }
    /**
     * Escapes &, <, >, " and ' characters.
     */
    escapeHtml(str) {
        if (typeof str === 'string') {
            return str
                .replace(/&/g, '&amp;')
                .replace(/>/g, '&gt;')
                .replace(/</g, '&lt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&apos;');
        }
        return str;
    }
    resourceLocationToRegex(str) {
        return new RegExp(str.replace(/\/:[^\/]*/g, '/.*'));
    }
}
const stringUtils = new StringUtils();

/**
 * Helper to handle competing promises
 */
/* @ngInject */ exports.DiscardablePromiseUtils = class /* @ngInject */ DiscardablePromiseUtils {
    constructor(logService) {
        this.logService = logService;
        this._map = {};
    }
    /**
     * Selects a new promise as candidate for invoking a given callback
     * each invocation of this method for a given key discards the previously selected promise
     * @param key The string key identifying the discardable promise
     * @param promise The discardable promise instance once a new candidate is called with this method
     * @param successCallback The success callback to ultimately apply on the last promise not discarded
     * @param failureCallback The failure callback to ultimately apply on the last promise not discarded. Optional.
     */
    apply(key, promise, successCallback, failureCallback) {
        if (!this._map[key]) {
            this._map[key] = {
                promise,
                successCallback,
                failureCallback
            };
        }
        else {
            this.logService.debug(`competing promise for key ${key}`);
            delete this._map[key].discardableHolder.successCallback;
            delete this._map[key].discardableHolder.failureCallback;
            this._map[key].promise = promise;
        }
        this._map[key].discardableHolder = {
            successCallback: this._map[key].successCallback,
            failureCallback: this._map[key].failureCallback
        };
        const self = this;
        const p = this._map[key].promise;
        p.then(function (response) {
            if (this.successCallback) {
                delete self._map[key];
                this.successCallback.apply(undefined, [response]);
            }
            else {
                self.logService.debug(`aborted successCallback for promise identified by ${key}`);
            }
        }.bind(this._map[key].discardableHolder), function (error) {
            if (this.failureCallback) {
                delete self._map[key];
                this.failureCallback.apply(undefined, [error]);
            }
            else {
                self.logService.debug(`aborted failureCallback for promise identified by ${key}`);
            }
        }.bind(this._map[key].discardableHolder));
    }
    /**
     * Removes callbacks of promise if exists.
     *
     * Used to remove any pending callbacks when a component is destroyed to prevent memory leaks.
     */
    clear(key) {
        if (this.exists(key)) {
            delete this._map[key].discardableHolder.successCallback;
            delete this._map[key].discardableHolder.failureCallback;
        }
    }
    exists(key) {
        return this._map[key] ? true : false;
    }
};
/* @ngInject */ exports.DiscardablePromiseUtils = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService])
], /* @ngInject */ exports.DiscardablePromiseUtils);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class SmarteditErrorHandler extends core.ErrorHandler {
    constructor() {
        super();
        this.ignorePatterns = [
            /Uncaught[\s]*\(in[\s]*promise\)/,
            /Unhandled[\s]*Promise[\s]*rejection/
        ];
    }
    handleError(error) {
        if (!error) {
            return;
        }
        if (process.env.NODE_ENV !== 'production') {
            super.handleError(error);
            return;
        }
        /*
         * original exception occuring in a promise based API won't show here
         * the catch set in ES6 promise decoration is necessary to log them
         */
        const message = error && error.message ? error.message : error;
        if (message && this.ignorePatterns.some((pattern) => pattern.test(message.toString()))) {
            return;
        }
        if (error instanceof http.HttpErrorResponse && error.status === 401) {
            return;
        }
        super.handleError(error);
    }
}

const DOMAIN_TOKEN = 'domain';
const SMARTEDITLOADER_COMPONENT_NAME = 'smarteditloader-component';
const SMARTEDITCONTAINER_COMPONENT_NAME = 'smarteditcontainer-component';
const SMARTEDIT_COMPONENT_NAME = 'smartedit-component';
const ELEMENT_UUID_ATTRIBUTE = 'data-smartedit-element-uuid';
const ID_ATTRIBUTE = 'data-smartedit-component-id';
const TYPE_ATTRIBUTE = 'data-smartedit-component-type';
const NG_ROUTE_PREFIX = 'ng';
const NG_ROUTE_WILDCARD = '**';
const LOGOUT = 'logout';
const EXTENDED_VIEW_PORT_MARGIN = 1000;
const CONTEXT_CATALOG = 'CURRENT_CONTEXT_CATALOG';
const CONTEXT_CATALOG_VERSION = 'CURRENT_CONTEXT_CATALOG_VERSION';
const CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';
const PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
const PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
/**
 * Constant containing the name of the current page site uid placeholder in URLs
 */
const PAGE_CONTEXT_SITE_ID = 'CURRENT_PAGE_CONTEXT_SITE_ID';
const SHOW_SLOT_MENU = '_SHOW_SLOT_MENU';
const HIDE_SLOT_MENU = 'HIDE_SLOT_MENU';
const OVERLAY_DISABLED_EVENT = 'OVERLAY_DISABLED';
const DEFAULT_LANGUAGE = 'en_US';
const CLOSE_CTX_MENU = 'CLOSE_CTX_MENU';
const CTX_MENU_DROPDOWN_IS_OPEN = 'CTX_MENU_DROPDOWN_IS_OPEN';
exports.MUTATION_CHILD_TYPES = void 0;
(function (MUTATION_CHILD_TYPES) {
    MUTATION_CHILD_TYPES["ADD_OPERATION"] = "addedNodes";
    MUTATION_CHILD_TYPES["REMOVE_OPERATION"] = "removedNodes";
})(exports.MUTATION_CHILD_TYPES || (exports.MUTATION_CHILD_TYPES = {}));
/*
 * Mutation object (return in a list of mutations in mutation event) can be of different types.
 * We are here only interested in type attributes (used for onPageChanged and onComponentChanged events) and childList (used for onComponentAdded events)
 */
const MUTATION_TYPES = {
    CHILD_LIST: {
        NAME: 'childList',
        ADD_OPERATION: exports.MUTATION_CHILD_TYPES.ADD_OPERATION,
        REMOVE_OPERATION: exports.MUTATION_CHILD_TYPES.REMOVE_OPERATION
    },
    ATTRIBUTES: {
        NAME: 'attributes'
    }
};
/**
 * **Deprecated since 2105.**
 *
 * Path to fetch permissions of a given catalog version.
 *
 * @deprecated
 */
const CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT = '/permissionswebservices/v1/permissions/catalogs/search';
const OPERATION_CONTEXT = {
    BACKGROUND_TASKS: 'Background Tasks',
    INTERACTIVE: 'Interactive',
    NON_INTERACTIVE: 'Non-Interactive',
    BATCH_OPERATIONS: 'Batch Operations',
    TOOLING: 'Tooling',
    CMS: 'CMS'
};
const I18N_RESOURCE_URI = '/smarteditwebservices/v1/i18n/translations';
/**
 * Resource URI of the WhoAmI REST service used to retrieve information on the
 * current logged-in user.
 */
const WHO_AM_I_RESOURCE_URI = '/authorizationserver/oauth/whoami';
/**
 * The default OAuth 2 client id to use during authentication.
 */
const DEFAULT_AUTHENTICATION_CLIENT_ID = 'smartedit';
const SSO_AUTHENTICATION_ENTRY_POINT = '/samlsinglesignon/saml';
const SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = '/smartedit/authenticate';
const SSO_LOGOUT_ENTRY_POINT = '/samlsinglesignon/saml/logout';
/**
 * Path of the preview ticket API
 */
const PREVIEW_RESOURCE_URI = '/previewwebservices/v1/preview';
/**
 * Regular expression identifying CMS related URIs
 */
const CMSWEBSERVICES_PATH = /\/cmssmarteditwebservices|\/cmswebservices/;
/**
 * To calculate platform domain URI, this regular expression will be used
 */
const SMARTEDIT_RESOURCE_URI_REGEXP = /^(.*)\/smartedit/;
/**
 * The name of the webapp root context
 */
const SMARTEDIT_ROOT = 'smartedit';
/**
 * The SmartEdit configuration API root
 */
const CONFIGURATION_URI = '/smartedit/configuration';
const SETTINGS_URI = '/smartedit/settings';
const EVENT_NOTIFICATION_CHANGED = 'EVENT_NOTIFICATION_CHANGED';
exports.SortDirections = void 0;
(function (SortDirections) {
    SortDirections["Ascending"] = "asc";
    SortDirections["Descending"] = "desc";
})(exports.SortDirections || (exports.SortDirections = {}));
const REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT = 'REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT';
const PREVIOUS_USERNAME_HASH = 'previousUsername';
const SMARTEDIT_LOGIN_DIALOG_RESOURCES = {
    topLogoURL: 'static-resources/images/SAP_R_grad.svg',
    bottomLogoURL: 'static-resources/images/best-run-sap-logo.svg'
};
const EVENT_PERSPECTIVE_CHANGED = 'EVENT_PERSPECTIVE_CHANGED';
const EVENT_PERSPECTIVE_UNLOADING = 'EVENT_PERSPECTIVE_UNLOADING';
const EVENT_PERSPECTIVE_REFRESHED = 'EVENT_PERSPECTIVE_REFRESHED';
const EVENT_PERSPECTIVE_ADDED = 'EVENT_PERSPECTIVE_ADDED';
const EVENT_PERSPECTIVE_UPDATED = 'EVENT_PERSPECTIVE_UPDATED';
const EVENT_THEME_SAVED = 'EVENT_THEME_SAVED';
const EVENT_THEME_CHANGED = 'EVENT_THEME_CHANGED';
const EVENT_STRICT_PREVIEW_MODE_REQUESTED = 'EVENT_STRICT_PREVIEW_MODE_REQUESTED';
const PERSPECTIVE_SELECTOR_WIDGET_KEY = 'perspectiveToolbar.perspectiveSelectorTemplate';
const EVENT_SMARTEDIT_COMPONENT_UPDATED = 'EVENT_SMARTEDIT_COMPONENT_UPDATED';
const OVERLAY_ID = 'smarteditoverlay';
const EVENT_OUTER_FRAME_CLICKED = 'EVENT_OUTER_FRAME_CLICKED';
const EVENT_INNER_FRAME_CLICKED = 'EVENT_INNER_FRAME_CLICKED';
const CATALOG_VERSION_UUID_ATTRIBUTE = 'data-smartedit-catalog-version-uuid';
const COMPONENT_CLASS = 'smartEditComponent';
const CONTAINER_ID_ATTRIBUTE = 'data-smartedit-container-id';
const CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS = {
    PROCESS: 'processComponent',
    REMOVE: 'removeComponent',
    KEEP_VISIBLE: 'keepComponentVisible'
};
const CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = {
    PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
    RESTART_PROCESS: 'contractChangeListenerRestartProcess'
};
const OVERLAY_RERENDERED_EVENT = 'overlayRerendered';
const SMARTEDIT_ATTRIBUTE_PREFIX = 'data-smartedit-';
const SMARTEDIT_COMPONENT_PROCESS_STATUS = 'smartEditComponentProcessStatus';
const UUID_ATTRIBUTE = 'data-smartedit-component-uuid';
const OVERLAY_COMPONENT_CLASS = 'smartEditComponentX';
const CONTENT_SLOT_TYPE = 'ContentSlot';
const CONTAINER_TYPE_ATTRIBUTE = 'data-smartedit-container-type';
const LANGUAGE_RESOURCE_URI = '/cmswebservices/v1/sites/:siteUID/languages';
const I18N_LANGUAGES_RESOURCE_URI = '/smarteditwebservices/v1/i18n/languages';
// Generic Editor
const GENERIC_EDITOR_LOADED_EVENT = 'genericEditorLoadedEvent';
const GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = 'UnrelatedValidationMessagesEvent';
const VALIDATION_MESSAGE_TYPES = {
    VALIDATION_ERROR: 'ValidationError',
    WARNING: 'Warning'
};
const ENUM_RESOURCE_URI = '/cmswebservices/v1/enums';
// Dropdown
const DROPDOWN_IMPLEMENTATION_SUFFIX = 'DROPDOWN_IMPLEMENTATION_SUFFIX';
const LINKED_DROPDOWN = 'LinkedDropdown';
const CLICK_DROPDOWN = 'ClickDropdown';
const DROPDOWN_POPULATOR = 'DropdownPopulator';
const SITES_RESOURCE_URI = '/cmswebservices/v1/sites';
const DATE_CONSTANTS = {
    ANGULAR_FORMAT: 'short',
    MOMENT_FORMAT: 'M/D/YY h:mm A',
    MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
    ISO: 'yyyy-MM-ddTHH:mm:00Z',
    ANGULAR_SHORT: 'M/d/yy h:mm a'
};
const CATALOG_DETAILS_COLUMNS = {
    LEFT: 'left',
    RIGHT: 'right'
};
const TYPES_RESOURCE_URI = '/cmswebservices/v1/types';
const STORE_FRONT_CONTEXT = '/storefront';
const PRODUCT_RESOURCE_API = '/cmssmarteditwebservices/v1/sites/:siteUID/products/:productUID';
const PRODUCT_LIST_RESOURCE_API = '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products';
const HIDE_TOOLBAR_ITEM_CONTEXT = 'HIDE_TOOLBAR_ITEM_CONTEXT';
const SHOW_TOOLBAR_ITEM_CONTEXT = 'SHOW_TOOLBAR_ITEM_CONTEXT';
const SMARTEDIT_DRAG_AND_DROP_EVENTS = {
    DRAG_DROP_CROSS_ORIGIN_START: 'DRAG_DROP_CROSS_ORIGIN_START',
    DRAG_DROP_START: 'EVENT_DRAG_DROP_START',
    DRAG_DROP_END: 'EVENT_DRAG_DROP_END',
    TRACK_MOUSE_POSITION: 'EVENT_TRACK_MOUSE_POSITION',
    DROP_ELEMENT: 'EVENT_DROP_ELEMENT'
};
const NONE_PERSPECTIVE = 'se.none';
const ALL_PERSPECTIVE = 'se.all';
const SEND_MOUSE_POSITION_THROTTLE = 100;
const THROTTLE_SCROLLING_DELAY = 70;
const SMARTEDIT_ELEMENT_HOVERED = 'smartedit-element-hovered';
const SCROLL_AREA_CLASS = 'ySECmsScrollArea';
const SMARTEDIT_IFRAME_DRAG_AREA = 'ySmartEditFrameDragArea';
const DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME = {
    START: 'START',
    END: 'END'
};
const SMARTEDIT_IFRAME_WRAPPER_ID = '#js_iFrameWrapper';
const HEART_BEAT_TIMEOUT_THRESHOLD_MS = 10000;
const EVENT_CONTENT_CATALOG_UPDATE = 'EVENT_CONTENT_CATALOG_UPDATE';
// These two constants are only used to load new files in E2E tests. In production code they
// are completely ignored.
const SMARTEDIT_INNER_FILES = [];
const SMARTEDIT_INNER_FILES_POST = [];
const MEDIA_RESOURCE_URI = `/cmswebservices/v1/catalogs/${CONTEXT_CATALOG}/versions/${CONTEXT_CATALOG_VERSION}/media`;
const MEDIA_PATH = `/cmswebservices/v1/media`;
const MEDIAS_PATH = `/cmswebservices/v1/catalogs/${CONTEXT_CATALOG}/versions/${CONTEXT_CATALOG_VERSION}/medias`;
const EXPERIENCE_STORAGE_KEY = 'experience';
const MEDIA_FOLDER_PATH = `/cmswebservices/v1/mediafolders`;
const OPEN_PAGE_WORKFLOW_MENU = 'OPEN_PAGE_WORKFLOW_MENU';
const CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU = 'CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU';
const CMSITEMS_UPDATE_EVENT = 'CMSITEMS_UPDATE';
const PAGES_CONTENT_SLOT_RESOURCE_URI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslots`;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_TEMPLATES_URI
 *
 * @description
 * Resource URI of the page templates REST service
 */
// page tree event
const EVENT_PAGE_TREE_PANEL_SWITCH = 'EVENT_PAGE_TREE_PANEL_SWITCH';
const EVENT_PAGE_TREE_SLOT_SELECTED = 'EVENT_PAGE_TREE_SLOT_SELECTED';
const EVENT_PAGE_TREE_COMPONENT_SELECTED = 'EVENT_PAGE_TREE_COMPONENT_SELECTED';
const EVENT_OPEN_IN_PAGE_TREE = 'EVENT_OPEN_IN_PAGE_TREE';
const EVENT_PART_REFRESH_TREE_NODE = 'EVENT_PART_REFRESH_TREE_NODE';
const EVENT_OVERALL_REFRESH_TREE_NODE = 'EVENT_OVERALL_REFRESH_TREE_NODE';
const EVENT_PAGE_TREE_SLOT_NEED_UPDATE = 'EVENT_PAGE_TREE_SLOT_NEED_UPDATE';
const PAGE_TREE_PANEL_WIDTH_COOKIE_NAME = 'PAGE_TREE_PANEL_WIDTH_COOKIE_NAME';
const PAGE_TREE_SLOT_EXPANDED_EVENT = 'PAGE_TREE_SLOT_EXPANDED_EVENT';
const PAGE_TREE_NODE_CLASS = 'se-page-tree-component-node';
/**
 * @description
 * Theme related APIs
 */
const CURRENT_USER_THEME_URI = '/smarteditwebservices/v1/themes/currentUser/theme';
const ALL_ACTIVE_THEMES_URI = '/smarteditwebservices/v1/themes';
// user tracking functionality area
const USER_TRACKING_FUNCTIONALITY = {
    NAVIGATION: 'Function Navigation',
    PAGE_MANAGEMENT: 'Page Management',
    NAVIGATION_MANAGEMENT: 'Navigation Management',
    HEADER_TOOL: 'Header Tool Bar',
    SELECT_PERSPECTIVE: 'Perspective Select',
    TOOL_BAR: 'Tool Bar',
    VERSION_OPERATION: 'Version Operation',
    INFLECTION: 'Inflection Select',
    ADD_COMPONENT: 'Add Component',
    VERSION_MANAGEMENT: 'Version Management',
    CONTEXT_MENU: 'Contextual Menu',
    PAGE_STRUCTURE: 'Page Structure'
};
const USER_TRACKING_KEY_MAP = new Map([
    ['se.cms.toolbaritem.navigationmenu.name', 'Navigation'],
    ['se.cms.pagelist.title', 'Pages'],
    ['se.route.storefront.title', 'Storefront'],
    ['headerToolbar.configurationTemplate', 'Configuration'],
    ['headerToolbar.languageSelectorTemplate', 'Language Select'],
    ['headerToolbar.userAccountTemplate', 'User Account'],
    ['se.cms.perspective.basic', 'Basic Edit'],
    ['se.cms.perspective.versioning', 'Versioning'],
    ['personalizationsmartedit.perspective', 'Personalization'],
    ['se.cms.perspective.advanced', 'Advanced Edit'],
    ['se.none', 'Preview'],
    ['se.cms.componentMenuTemplate', 'Component'],
    ['se.cms.pageTreeMenu', USER_TRACKING_FUNCTIONALITY.PAGE_STRUCTURE],
    [
        'personalizationsmartedit.container.pagecustomizations.toolbar',
        'Personalize - Customization'
    ],
    ['personalizationsmartedit.container.combinedview.toolbar', 'Personlization - CombinedView'],
    ['personalizationsmartedit.container.manager.toolbar', 'Personlization - Library'],
    ['se.cms.createVersionMenu', 'Add Version'],
    ['se.cms.clonePageMenu', 'Clone'],
    ['se.cms.compomentmenu.tabs.componenttypes', 'Component Types'],
    ['se.cms.compomentmenu.tabs.customizedcomp', 'Saved Components'],
    ['se.cms.contextmenu.title.dragndrop', 'Drag and Drop'],
    ['se.cms.contextmenu.title.edit', 'Edit'],
    ['se.cms.contextmenu.title.remove', 'Remove'],
    ['se.cms.contextmenu.title.clone.component', 'Clone'],
    ['se.cms.pageVersionsMenu', 'Versions'],
    ['se.cms.contextmenu.title.open.in.page.tree', 'Open In Page Tree'],
    ['se.cms.version.item.menu.view.label', 'View'],
    ['se.cms.version.item.menu.edit.label', 'Edit Details'],
    ['se.cms.version.item.menu.rollback.label', 'Rollback To This Version'],
    ['se.cms.version.item.menu.delete.label', 'Delete'],
    ['se.cms.rollbackVersionMenu', 'Version Rollback']
]);

function objectToArray(obj) {
    return Object.keys(obj).reduce((acc, key) => [...(acc || []), { key, value: obj[key] }], []);
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides a list of useful methods used for object manipulation
 */
class ObjectUtils {
    constructor() {
        /**
         * Will check if the object is empty and will return true if each and every property of the object is empty.
         *
         * @param value the value to evaluate
         */
        this.isObjectEmptyDeep = (value) => {
            if (lodash__namespace.isObject(value)) {
                for (const key in value) {
                    if (value.hasOwnProperty(key)) {
                        if (!lodash__namespace.isEmpty(value[key])) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return lodash__namespace.isString(value) ? lodash__namespace.isEmpty(value) : lodash__namespace.isNil(value);
        };
        /**
         * Resets a given object's properties' values
         *
         * @param targetObject, the object to reset
         * @param modelObject, an object that contains the structure that targetObject should have after a reset
         * @returns The object that has been reset
         */
        this.resetObject = (targetObject, modelObject) => {
            if (!targetObject) {
                targetObject = this.copy(modelObject);
            }
            else {
                for (const i in targetObject) {
                    if (targetObject.hasOwnProperty(i)) {
                        delete targetObject[i];
                    }
                }
                lodash__namespace.extend(targetObject, this.copy(modelObject));
            }
            return targetObject;
        };
        /**
         * Iterates over object and allows to modify a value using callback function.
         * @param callback Callback function to apply to each object value.
         * @returns The object with modified values.
         */
        this.deepIterateOverObjectWith = (obj, callback) => lodash__namespace.reduce(obj, (result, value, key) => {
            if (lodash__namespace.isPlainObject(value)) {
                result[key] = this.deepIterateOverObjectWith(value, callback);
            }
            else {
                result[key] = callback(value);
            }
            return result;
        }, {});
        /**
         * Returns an object that contains list of fields and for each field it has a boolean value
         * which is true when the property was modified, added or removed, false otherwise.
         * @returns The diff object.
         */
        this.deepObjectPropertyDiff = (firstObject, secondObject) => {
            const CHANGED_PROPERTY = 'CHANGED_PROPERTY';
            const NON_CHANGED_PROPERTY = 'NON_CHANGED_PROPERTY';
            const mergedObj = lodash__namespace.mergeWith(lodash__namespace.cloneDeep(firstObject), secondObject, function (prValue, cpValue) {
                if (!lodash__namespace.isPlainObject(prValue)) {
                    return !lodash__namespace.isEqual(prValue, cpValue)
                        ? CHANGED_PROPERTY
                        : NON_CHANGED_PROPERTY;
                }
                // Note: Previous versions of lodash could work with null, but the latest version of lodash requires
                // undefined to be returned.
                return undefined;
            });
            // If the field is not CHANGED_PROPERTY/NON_CHANGED_PROPERTY then it was removed or added.
            const sanitizedObj = this.deepIterateOverObjectWith(mergedObj, (value) => {
                if (value !== CHANGED_PROPERTY && value !== NON_CHANGED_PROPERTY) {
                    return CHANGED_PROPERTY;
                }
                else {
                    return value;
                }
            });
            // If it's CHANGED_PROPERTY return true otherwise false.
            return this.deepIterateOverObjectWith(sanitizedObj, (value) => value === CHANGED_PROPERTY ? true : false);
        };
        this.readObjectStructure = (json, recursiveCount = 0) => {
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
    }
    /**
     * Creates a deep copy of the given input object.
     * If an object being stringified has a property named toJSON whose value is a function, then the toJSON() method customizes JSON stringification behavior: instead of the object being serialized, the value returned by the toJSON() method when called will be serialized.
     *
     * @param candidate the javaScript value that needs to be deep copied.
     *
     * @returns A deep copy of the input
     */
    copy(candidate) {
        return JSON.parse(JSON.stringify(candidate));
    }
    /**
     * Merges the contents of two objects together into the first object.
     *
     * **Note:** This method mutates `object`.
     *
     * @returns A new object as a result of merge
     */
    merge(target, source) {
        return Object.assign(target, source);
    }
    /**
     * Converts the given object to array.
     * The output array elements are an object that has a key and value,
     * where key is the original key and value is the original object.
     */
    convertToArray(object) {
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
    uniqueArray(array1, array2) {
        const set = new Set(array1);
        array2.forEach((instance) => {
            if (!set.has(instance)) {
                array1.push(instance);
            }
        });
        return array1;
    }
    /**
     * Checks if `value` is a function.
     */
    isFunction(value) {
        return typeof value === 'function';
    }
    /**
     * Checks if the value is the ECMAScript language type of Object
     */
    isObject(value) {
        const objectTypes = {
            boolean: false,
            function: true,
            object: true,
            number: false,
            string: false,
            undefined: false
        };
        return !!(value && objectTypes[typeof value]);
    }
    isTypedMap(value) {
        return value && this.isObject(value) && value.constructor === Object;
    }
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
    sortBy(array, prop, reverse = false) {
        const targetArray = [...array];
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
    extend(ParentClass, ChildClass) {
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
    getClassName(instance) {
        return instance &&
            instance.constructor &&
            instance.constructor.name &&
            instance.constructor.name !== 'Object'
            ? instance.constructor.name
            : null;
    }
    _getOtherObjectType(json, recursiveCount) {
        if (lodash__namespace.isElement(json)) {
            return 'ELEMENT';
        }
        if (json.hasOwnProperty && json.hasOwnProperty('length')) {
            // jquery or Array
            if (json.forEach) {
                const arr = [];
                json.forEach((arrayElement) => {
                    recursiveCount++;
                    arr.push(this.readObjectStructure(arrayElement, recursiveCount));
                });
                return arr;
            }
            else {
                return 'JQUERY';
            }
        }
        if (json.constructor && json.constructor.name && json.constructor.name !== 'Object') {
            return json.constructor.name;
        }
        // JSON
        const clone = {};
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

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Collection of utility methods for handling responses from backend calls
 */
class ApiUtils {
    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the data of interest.
     *
     * @returns {Array} Returns the array from the response.
     */
    getDataFromResponse(response) {
        const dataKey = Object.keys(response).filter(function (key) {
            return response[key] instanceof Array;
        })[0];
        return response[dataKey];
    }
    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the key holding the data of interest.
     *
     * @returns Returns the name of the key holding the array from the response.
     */
    getKeyHoldingDataFromResponse(response) {
        const dataKey = Object.keys(response).filter(function (key) {
            return response[key] instanceof Array;
        })[0];
        return dataKey;
    }
}
const apiUtils = new ApiUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides a list of useful methods used for date manipulation.
 */
class DateUtils {
    /**
     * Formats provided dateTime as utc.
     *
     * @param dateTime Date Time to format in UTC.
     * @returns Formatted string.
     */
    formatDateAsUtc(dateTime) {
        return moment__default["default"](dateTime).utc().format(DATE_CONSTANTS.MOMENT_ISO);
    }
}
const dateUtils = new DateUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class ScriptUtils {
    injectJS() {
        function getInjector() {
            return $script__default["default"];
        }
        return {
            getInjector,
            execute(conf) {
                const srcs = conf.srcs;
                let index = conf.index;
                const callback = conf.callback;
                if (!srcs.length) {
                    callback();
                    return;
                }
                if (index === undefined) {
                    index = 0;
                }
                if (srcs[index] !== undefined) {
                    this.getInjector()(srcs[index], () => {
                        if (index + 1 < srcs.length) {
                            this.execute({
                                srcs,
                                index: index + 1,
                                callback
                            });
                        }
                        else if (typeof callback === 'function') {
                            callback();
                        }
                    });
                }
            }
        };
    }
}
const scriptUtils = new ScriptUtils();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
exports.Errors = void 0;
(function (Errors) {
    class ParseError {
        constructor(value) {
            this.value = value;
        }
    }
    Errors.ParseError = ParseError;
})(exports.Errors || (exports.Errors = {}));

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const EXTENDED_VIEW_PORT_MARGIN_TOKEN = new core.InjectionToken('EXTENDED_VIEW_PORT_MARGIN');
const HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN = new core.InjectionToken('HEART_BEAT_TIMEOUT_THRESHOLD_MS');

/**
 * Used to transmit events synchronously or asynchronously. It is supported by the SmartEdit [gatewayFactory]{@link GatewayFactory} to propagate events between SmartEditContainer and SmartEdit.
 * It also contains options to publish events, as well as subscribe the event handlers.
 */
/* @ngInject */ exports.SystemEventService = class /* @ngInject */ SystemEventService {
    constructor(logService, promiseUtils) {
        this.logService = logService;
        this.promiseUtils = promiseUtils;
        this._eventHandlers = {};
    }
    /**
     * Send the event with data synchronously.
     *
     * @returns A promise with resolved data of last subscriber or with the rejected error reason
     */
    publish(eventId, data) {
        if (!eventId) {
            this.logService.error('Failed to send event. No event ID provided for data: ' + data);
        }
        else {
            if (this._eventHandlers[eventId] && this._eventHandlers[eventId].length > 0) {
                return this._invokeEventHandlers(eventId, data);
            }
        }
        return Promise.resolve();
    }
    /**
     * Send the event with data asynchronously.
     */
    publishAsync(eventId, data) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                this.publish(eventId, data).then((resolvedData) => resolve(resolvedData), (reason) => reject(reason));
            }, 0);
        });
    }
    /**
     * Method to subscribe the event handler given the eventId and handler
     *
     * @param handler The event handler, a callback function which can either return a promise or directly a value.
     *
     * @returns Function to unsubscribe the event handler
     */
    subscribe(eventId, handler) {
        let unsubscribeFn;
        if (!eventId || !handler) {
            this.logService.error('Failed to subscribe event handler for event: ' + eventId);
        }
        else {
            // create handlers array for this event if not already created
            if (this._eventHandlers[eventId] === undefined) {
                this._eventHandlers[eventId] = [];
            }
            this._eventHandlers[eventId].push(handler);
            unsubscribeFn = () => {
                this._unsubscribe(eventId, handler);
            };
        }
        return unsubscribeFn;
    }
    /**
     * @internal
     */
    _unsubscribe(eventId, handler) {
        const handlersArray = this._eventHandlers[eventId];
        const index = handlersArray ? this._eventHandlers[eventId].indexOf(handler) : -1;
        if (index >= 0) {
            this._eventHandlers[eventId].splice(index, 1);
        }
        else {
            this.logService.warn(`Attempting to remove event handler for ${eventId} but handler not found.`);
        }
    }
    /**
     * @internal
     */
    _invokeEventHandlers(eventId, data) {
        return Promise.all(this._eventHandlers[eventId].map((eventHandler) => {
            const promiseClosure = this.promiseUtils.toPromise(eventHandler);
            return promiseClosure(eventId, data);
        })).then((results) => Promise.resolve(results.pop()), (reason) => Promise.reject(`eventId: ${eventId}, reason: ${reason}`));
    }
};
/* @ngInject */ exports.SystemEventService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService,
        utils.PromiseUtils])
], /* @ngInject */ exports.SystemEventService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * The Message Gateway is a private channel that is used to publish and subscribe to events across iFrame
 * boundaries. The gateway uses the W3C-compliant postMessage as its underlying technology. The benefits of
 * the postMessage are that:
 *
 *      <ul>
 *          <li>It works in cross-origin scenarios.</li>
 *          <li>The receiving end can reject messages based on their origins.</li>
 *      </ul>
 *
 * The creation of instances is controlled by the {@link GatewayFactory}.
 * Only one instance can exist for each gateway ID.
 *
 */
class MessageGateway {
    /**
     * @param gatewayId The channel identifier
     */
    constructor(logService, systemEventService, cloneableUtils, windowUtils, promiseUtils, TIMEOUT_TO_RETRY_PUBLISHING, gatewayId) {
        this.logService = logService;
        this.systemEventService = systemEventService;
        this.cloneableUtils = cloneableUtils;
        this.windowUtils = windowUtils;
        this.promiseUtils = promiseUtils;
        this.TIMEOUT_TO_RETRY_PUBLISHING = TIMEOUT_TO_RETRY_PUBLISHING;
        this.gatewayId = gatewayId;
        this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID = 'promiseAcknowledgement';
        this.PROMISE_RETURN_EVENT_ID = 'promiseReturn';
        this.SUCCESS = 'success';
        this.FAILURE = 'failure';
        this.MAX_RETRIES = 5;
        this.promisesToResolve = {};
    }
    /**
     * Publishes a message across the gateway using the postMessage.
     *
     * The gateway's publish method implements promises. To resolve a
     * publish promise, all listener promises on the side of the channel must resolve. If a failure occurs in the
     * chain, the chain is interrupted and the publish promise is rejected.
     *
     * @param data Message payload
     * @param retries The current number of attempts to publish a message. By default it is 0.
     * @param pk An optional parameter. It is a primary key for the event, which is generated after
     * the first attempt to send a message.
     */
    publish(eventId, _data, retries = 0, pk) {
        // handle for empty eventId
        if (!eventId) {
            return Promise.reject(`MessageGateway: Failed to send event. No event ID provided for _data: ${_data}`);
        }
        // handle for invalid data
        const data = this.cloneableUtils.makeCloneable(_data);
        if (!lodash__namespace.isEqual(data, _data)) {
            this.logService.debug(`MessageGateway.publish - Non cloneable payload has been sanitized for gateway ${this.gatewayId}, event ${eventId}:`, data);
        }
        // post message
        const deferred = this.promisesToResolve[pk] ||
            this.promiseUtils.defer();
        const target = this.windowUtils.getGatewayTargetFrame();
        if (!target) {
            deferred.reject('It is standalone. There is no iframe');
            return deferred.promise;
        }
        pk = pk || this._generateIdentifier();
        try {
            target.postMessage({
                pk,
                eventId,
                data,
                gatewayId: this.gatewayId
            }, '*');
        }
        catch (e) {
            this.logService.error(e);
            this.logService.error(`MessageGateway.publish - postMessage has failed for gateway ${this.gatewayId} event ${eventId} and data `, data);
        }
        this.promisesToResolve[pk] = deferred;
        // in case promise does not return because, say, a non ready frame
        this._setTimeout(() => {
            if (!deferred.acknowledged &&
                eventId !== this.PROMISE_RETURN_EVENT_ID &&
                eventId !== this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID) {
                // still pending
                if (retries < this.MAX_RETRIES) {
                    this.logService.debug(`${document.location.href} is retrying to publish event ${eventId}`);
                    ++retries;
                    this.publish(eventId, data, retries, pk).catch((reason) => {
                        //
                    });
                }
                else {
                    const error = `MessageGateway.publish - Not able to publish event ${eventId} after max retries for gateway ${this.gatewayId} and data ${JSON.stringify(data)}`;
                    deferred.reject(error);
                }
            }
        }, this.TIMEOUT_TO_RETRY_PUBLISHING);
        return deferred.promise;
    }
    /**
     * Registers a given callback function to the given event ID.
     *
     * @param callback Callback function to be invoked
     * @returns The function to call in order to unsubscribe the event listening
     */
    subscribe(eventId, callback) {
        let unsubscribeFn;
        if (!eventId) {
            this.logService.error('MessageGateway: Failed to subscribe event handler for event: ' + eventId);
        }
        else {
            const systemEventId = this._getSystemEventId(eventId);
            unsubscribeFn = this.systemEventService.subscribe(systemEventId, callback);
        }
        return unsubscribeFn;
    }
    processEvent(event) {
        // process non-return or non-ack promise
        if (event.eventId !== this.PROMISE_RETURN_EVENT_ID &&
            event.eventId !== this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID) {
            return this._processEvent(event);
        }
        const eventData = event.data;
        // process return promise
        if (event.eventId === this.PROMISE_RETURN_EVENT_ID) {
            this._processPromiseReturnEvent(eventData);
        }
        else if (event.eventId === this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID &&
            this.promisesToResolve[eventData.pk]) {
            // process acknowledgement promise
            this.logService.debug(document.location.href, 'received acknowledgement', event);
            this.promisesToResolve[eventData.pk].acknowledged = true;
        }
        return Promise.resolve();
    }
    _processEvent(event) {
        this.logService.debug(document.location.href, 'sending acknowledgement for', event);
        this.publish(this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID, {
            pk: event.pk
        });
        const systemEventId = this._getSystemEventId(event.eventId);
        return this.systemEventService.publishAsync(systemEventId, event.data).then((resolvedDataOfLastSubscriber) => {
            this.logService.debug(document.location.href, 'sending promise resolve', event);
            return this.publish(this.PROMISE_RETURN_EVENT_ID, {
                pk: event.pk,
                type: this.SUCCESS,
                resolvedDataOfLastSubscriber
            });
        }, (rejectedDataOfLastSubscriber) => {
            this.logService.debug(document.location.href, 'sending promise reject', event);
            return this.publish(this.PROMISE_RETURN_EVENT_ID, {
                pk: event.pk,
                type: this.FAILURE,
                rejectedDataOfLastSubscriber
            });
        });
    }
    _processPromiseReturnEvent(eventData) {
        if (this.promisesToResolve[eventData.pk]) {
            if (eventData.type === this.SUCCESS) {
                this.logService.debug(document.location.href, 'received promise resolve');
                this.promisesToResolve[eventData.pk].resolve(eventData.resolvedDataOfLastSubscriber);
            }
            else if (eventData.type === this.FAILURE) {
                this.logService.debug(document.location.href, 'received promise reject');
                this.promisesToResolve[eventData.pk].reject(eventData.rejectedDataOfLastSubscriber);
            }
            delete this.promisesToResolve[eventData.pk];
        }
    }
    _setTimeout(callback, timeout) {
        if (utils.functionsUtils.isUnitTestMode()) {
            setTimeout(callback, timeout);
        }
        else {
            this.windowUtils.runTimeoutOutsideAngular(callback, timeout);
        }
    }
    _generateIdentifier() {
        return new Date().getTime().toString() + Math.random().toString();
    }
    _getSystemEventId(eventId) {
        return `${this.gatewayId}:${eventId}`;
    }
}

var /* @ngInject */ GatewayFactory_1;
/**
 * The Gateway Factory controls the creation of and access to {@link MessageGateway} instances.
 *
 * To construct and access a gateway, you must use the GatewayFactory's createGateway method and provide the channel
 * ID as an argument. If you try to create the same gateway twice, the second call will return a null.
 */
/* @ngInject */ exports.GatewayFactory = /* @ngInject */ GatewayFactory_1 = class /* @ngInject */ GatewayFactory {
    constructor(logService, systemEventService, cloneableUtils, windowUtils, promiseUtils, functionsUtils) {
        this.logService = logService;
        this.systemEventService = systemEventService;
        this.cloneableUtils = cloneableUtils;
        this.windowUtils = windowUtils;
        this.promiseUtils = promiseUtils;
        this.functionsUtils = functionsUtils;
        this.messageGatewayMap = {};
    }
    /**
     * Initializes a postMessage event handler that dispatches the handling of an event to the specified gateway.
     * If the corresponding gateway does not exist, an error is logged.
     */
    initListener() {
        const processedPrimaryKeys = [];
        // Listen to message from child window
        this.windowUtils.getWindow().addEventListener('message', (e) => {
            // if it's qualtrics url, it should be allowed without error
            if (e.origin === 'https://sapinsights.eu.qualtrics.com') {
                this.logService.debug('qualtrics url ' + e.origin + ' should be allowed');
                return;
            }
            if (this._isAllowed(e.origin)) {
                // add control on e.origin
                const event = e.data;
                if (processedPrimaryKeys.indexOf(event.pk) > -1) {
                    return;
                }
                processedPrimaryKeys.push(event.pk);
                this.logService.debug('message event handler called', event.eventId);
                const gatewayId = event.gatewayId;
                const gateway = this.messageGatewayMap[gatewayId];
                if (!gateway) {
                    this.logService.debug('Incoming message on gateway ' +
                        gatewayId +
                        ', but no destination exists.');
                    return;
                }
                gateway.processEvent(event);
            }
            else {
                this.logService.error('disallowed storefront is trying to communicate with smarteditcontainer');
            }
        }, false);
    }
    /**
     * Creates a gateway for the specified gateway identifier and caches it in order to handle postMessage events
     * later in the application lifecycle. This method will fail on subsequent calls in order to prevent two
     * clients from using the same gateway.
     *
     * @returns The newly created Message Gateway or null.
     */
    createGateway(gatewayId) {
        if (this.messageGatewayMap[gatewayId] && !this.functionsUtils.isUnitTestMode()) {
            this.logService.error('Message Gateway for ' + gatewayId + ' already reserved');
            return null;
        }
        this.messageGatewayMap[gatewayId] = new MessageGateway(this.logService, this.systemEventService, this.cloneableUtils, this.windowUtils, this.promiseUtils, /* @ngInject */ GatewayFactory_1.TIMEOUT_TO_RETRY_PUBLISHING, gatewayId);
        return this.messageGatewayMap[gatewayId];
    }
    /**
     * Allowed if receiving end is frame or [container + (origin same as loaded iframe)]
     */
    _isAllowed(origin) {
        return (
        // communication from container to iframe already secured by webApplicationInjector
        this.windowUtils.isIframe() ||
            // communication from iframe to container strictly limiting to domain loaded in iframe
            this.windowUtils.getTrustedIframeDomain() === origin);
    }
};
/*
 * Period between two retries of a MessageGateway to publish an event
 * this value must be greater than the time needed by the browser to process a postMessage back and forth across two frames.
 * Internet Explorer is now known to need more than 100ms.
 */
/* @ngInject */ exports.GatewayFactory.TIMEOUT_TO_RETRY_PUBLISHING = 500;
/* @ngInject */ exports.GatewayFactory = /* @ngInject */ GatewayFactory_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService,
        exports.SystemEventService,
        utils.CloneableUtils,
        exports.WindowUtils,
        utils.PromiseUtils,
        utils.FunctionsUtils])
], /* @ngInject */ exports.GatewayFactory);

/**
 * To seamlessly integrate the gateway factory between two services on different frames, you can use a gateway
 * proxy. The gateway proxy service simplifies using the gateway module by providing an API that registers an
 * instance of a service that requires a gateway for communication.
 *
 * This registration process automatically attaches listeners to each of the service's functions (turned into promises), allowing stub
 * instances to forward calls to these functions using an instance of a gateway from {@link GatewayFactory}.
 * Any function that has an empty body declared on the service is used as a proxy function.
 * It delegates a publish call to the gateway under the same function name, and wraps the result of the call in a Promise.
 */
/* @ngInject */ exports.GatewayProxy = class /* @ngInject */ GatewayProxy {
    constructor(logService, promiseUtils, stringUtils, functionsUtils, gatewayFactory) {
        this.logService = logService;
        this.promiseUtils = promiseUtils;
        this.stringUtils = stringUtils;
        this.functionsUtils = functionsUtils;
        this.gatewayFactory = gatewayFactory;
        this.nonProxiableMethods = [
            'getMethodForVoid',
            'getMethodForSingleInstance',
            'getMethodForArray'
        ];
    }
    /**
     * Mutates the given service into a proxied service.
     * You must provide a unique string gatewayId, in one of 2 ways.
     *
     *
     * 1) Having a gatewayId property on the service provided
     *
     *
     * OR
     *
     *
     * 2) providing a gatewayId as 3rd param of this function
     *
     * @param service Service to mutate into a proxied service.
     * @param methodsSubset An explicit set of methods on which the gatewayProxy will trigger. Otherwise, by default all functions
     * will be proxied. This is particularly useful to avoid inner methods being unnecessarily turned into promises.
     * @param gatewayId The gateway ID to use internaly for the proxy. If not provided, the service <strong>must</strong> have a gatewayId property.
     */
    initForService(service, methodsSubset, gatewayId) {
        const gwId = gatewayId || service.gatewayId;
        if (!gwId) {
            this.logService.error(`initForService() - service expected to have an associated gatewayId - methodsSubset: ${methodsSubset && methodsSubset.length ? methodsSubset.join(',') : []}`);
            return null;
        }
        const gateway = this.gatewayFactory.createGateway(gwId);
        let loopedOver = methodsSubset;
        if (!loopedOver) {
            loopedOver = this.functionsUtils
                .getInstanceMethods(service)
                .filter((key) => !this._isNonProxiableMethod(key));
        }
        loopedOver.forEach((fnName) => {
            if (typeof service[fnName] === 'function') {
                if (this.functionsUtils.isEmpty(service[fnName])) {
                    this._turnToProxy(fnName, service, gateway);
                }
                else {
                    service[fnName] = this.promiseUtils.toPromise(service[fnName], service);
                    gateway.subscribe(fnName, this._onGatewayEvent.bind(null, fnName, service));
                }
            }
        });
    }
    /** @ignore */
    _isNonProxiableMethod(key) {
        return (this.nonProxiableMethods.indexOf(key) > -1 ||
            key.startsWith('$') ||
            key === 'lodash' ||
            key === 'jQuery');
    }
    /** @ignore */
    _onGatewayEvent(fnName, service, eventId, data) {
        return service[fnName].apply(service, data.arguments);
    }
    /** @ignore */
    _turnToProxy(fnName, service, gateway) {
        delete service[fnName];
        service[fnName] = ((...args) => gateway
            .publish(fnName, {
            arguments: args
        })
            .then((resolvedData) => {
            if (!this.stringUtils.isBlank(resolvedData)) {
                delete resolvedData.$resolved;
                delete resolvedData.$promise;
            }
            return resolvedData;
        }, (error) => {
            if (error) {
                this.logService.debug(`gatewayProxy - publish failed for gateway ${gateway.gatewayId} method ${fnName} and arguments ${args}`);
            }
            return Promise.reject(error);
        }));
    }
};
/* @ngInject */ exports.GatewayProxy = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService,
        utils.PromiseUtils,
        utils.StringUtils,
        utils.FunctionsUtils,
        exports.GatewayFactory])
], /* @ngInject */ exports.GatewayProxy);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ANNOUNCEMENT_DATA = new core.InjectionToken('ANNOUNCEMENT_DATA');
/**
 * Interface for Announcement service
 */
class IAnnouncementService {
    /**
     * This method creates a new announcement and displays it.
     *
     * The configuration must contain either message or component.
     *
     * @returns Returns a promise with announcement id.
     */
    showAnnouncement(announcementConfig) {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to close the announcement by given announcement id.
     */
    closeAnnouncement(announcementId) {
        'proxyFunction';
        return null;
    }
}

/**
 * The Catalog Service fetches catalogs for a specified site or for all sites registered on the hybris platform using
 * REST calls to the cmswebservices Catalog Version Details API.
 */
class ICatalogService {
    // ------------------------------------------------------------------------------------------------------------------------
    //  Active
    // ------------------------------------------------------------------------------------------------------------------------
    /**
     * Convenience method to return a full `UriContext` to the invoker through a promise.
     *
     *
     * If uriContext is provided, it will be returned as such.
     *
     *
     * If uriContext is not provided, A uriContext will be built from the experience present in {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     * If we fail to find a uriContext in sharedDataService, an exception will be thrown.
     * @returns Wrapped uriContext in a promise
     */
    retrieveUriContext(_uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a list of content catalogs for the site that corresponds to the specified site UID.
     *
     * @param siteUID The UID of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getContentCatalogsForSite(siteUID) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a list of content catalog groupings for all sites.
     *
     * @returns An array of catalog groupings sorted by catalog ID, each of which has a name, a catalog ID, and a list of
     * catalog version descriptors.
     */
    getAllContentCatalogsGroupedById() {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a list of catalogs for the given site UID and a given catalog version.
     *
     * @param siteUID The UID of the site that the catalog versions are to be fetched.
     * @param catalogVersion The version of the catalog that is to be fetched.
     *
     * @returns An array containing the catalog descriptor (if any). Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalogVersion.
     */
    // FIXME: this method does not seem to be safe for same catalogversion version name across multiple catalogs
    getCatalogByVersion(siteUID, catalogVersionName) {
        'proxyFunction';
        return null;
    }
    /**
     * Determines whether the catalog version identified by the given uriContext is a non active one
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     *
     * @returns True if the given catalog version is non active
     */
    isContentCatalogVersionNonActive(_uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Find the version that is flagged as active for the given uriContext.
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     *
     * @returns The version name
     */
    getContentCatalogActiveVersion(_uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the version name that is flagged as active for the given content catalog.
     *
     * @param contentCatalogId The UID of content catalog for which to retrieve its active catalog version name.
     * @returns The version name
     */
    getActiveContentCatalogVersionByCatalogId(contentCatalogId) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the current site ID
     * @returns The ID of the current site.
     */
    getCurrentSiteID() {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the ID of the default site configured for the provided content catalog.
     * @param contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns The ID of the default site found.
     */
    getDefaultSiteForContentCatalog(contentCatalogId) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the catalog version given an uriContext object.
     *
     * @param uriContext An object that represents the current context, containing information about the site.
     * @returns A promise that resolves to the catalog version descriptor found.
     */
    getContentCatalogVersion(uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the catalog version descriptor identified by the provided UUID.
     * An exception is thrown if no match is found.
     *
     * @param catalogVersionUuid The UID of the catalog version descriptor to find.
     * @param siteId the ID of the site where to perform the search.
     * If no ID is provided, the search will be performed on all permitted sites.
     * @returns A promise that resolves to the catalog version descriptor found.
     *
     */
    getCatalogVersionByUuid(catalogVersionUuid, siteId) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the catalog version UUID given an optional urlContext object. The current catalog version UUID from the active experience selector is returned, if the URL is not present in the call.
     *
     * @param urlContext An object that represents the current context, containing information about the site.
     * @returns A promise that resolves to the catalog version uuid.
     */
    getCatalogVersionUUid(_uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a list of product catalogs for the site that corresponds to the specified site UID key.
     *
     * @param siteUIDKey The UID of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getProductCatalogsBySiteKey(siteUIDKey) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a list of product catalogs for the site that corresponds to the specified site UID value.
     *
     * @param siteUIDValue The UID value of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getProductCatalogsForSite(siteUIDValue) {
        'proxyFunction';
        return null;
    }
    /**
     * Finds the version name that is flagged as active for the given product catalog.
     *
     * @param productCatalogId The UID of product catalog for which to retrieve its active catalog version name.
     * @returns the version name
     */
    getActiveProductCatalogVersionByCatalogId(productCatalogId) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches all the active catalog version uuid's for a provided array of catalogs.
     *
     * @returns An array of catalog version uuid's
     */
    returnActiveCatalogVersionUIDs(catalogs) {
        'proxyFunction';
        return null;
    }
    /**
     * Determines whether the current catalog from the page context of current experience is multicountry related or not.
     *
     * @returns True if current catalog is multicountry related; Otherwise false.
     */
    isCurrentCatalogMultiCountry() {
        'proxyFunction';
        return null;
    }
}

/**
 * ExperienceService deals with building experience objects given a context.
 */
class IExperienceService {
    updateExperiencePageContext(pageCatalogVersionUuid, pageId) {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the active experience.
     */
    getCurrentExperience() {
        'proxyFunction';
        return null;
    }
    /**
     * Stores a given experience as current experience.
     * Invoking this method ensures that a hard refresh of the application will preserve the experience.
     */
    setCurrentExperience(experience) {
        'proxyFunction';
        return null;
    }
    /**
     * Determines whether the catalog version has changed between the previous and current experience
     */
    hasCatalogVersionChanged() {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the active experience, creates a new preview ticket and returns a new preview url with an updated
     * previewTicketId query param
     *
     * @returns An URL containing the new `previewTicketId`
     */
    buildRefreshedPreviewUrl() {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the active experience, merges it with a new experience, creates a new preview ticket and reloads the
     * preview within the iframeManagerService
     *
     * @param newExperience The object containing new attributes to be merged with the current experience
     *
     * @returns A promise of the updated experience
     */
    updateExperience(newExperience) {
        'proxyFunction';
        return null;
    }
    loadExperience(params) {
        'proxyFunction';
        return null;
    }
    /**
     * This method compares all the properties of given experience of type IDefaultExperienceParams with the current experience.
     *
     * @param experience The object containing default experience params such as pageId, catalogId, catalogVersion and siteId
     *
     * @return True if current experience matches with the gien experience. Otherwise false.
     */
    compareWithCurrentExperience(experience) {
        'proxyFunction';
        return null;
    }
    /** @internal */
    _convertExperienceToPreviewData(experience, resourcePath) {
        const previewData = lodash__namespace.cloneDeep(experience);
        const catalogVersions = [];
        delete previewData.catalogDescriptor;
        delete previewData.siteDescriptor;
        delete previewData.languageDescriptor;
        delete previewData.pageContext;
        delete previewData.productCatalogVersions;
        if (experience.productCatalogVersions && experience.productCatalogVersions.length) {
            experience.productCatalogVersions.forEach((productCatalogVersion) => {
                catalogVersions.push({
                    catalog: productCatalogVersion.catalog,
                    catalogVersion: productCatalogVersion.catalogVersion
                });
            });
        }
        catalogVersions.push({
            catalog: experience.catalogDescriptor.catalogId,
            catalogVersion: experience.catalogDescriptor.catalogVersion
        });
        previewData.catalogVersions = catalogVersions;
        previewData.language = experience.languageDescriptor.isocode;
        previewData.resourcePath = resourcePath;
        previewData.siteId = experience.siteDescriptor.uid;
        return previewData;
    }
    /**
     * If an experience is set in the shared data service, this method will load the preview for this experience (such as Catalog, language, date and time).
     * Otherwise, the user will be redirected to the landing page to select an experience.
     * To load a preview, we need to get a preview ticket from an API.
     * Here we set current location to null initially so that the iframe manager loads the provided url and set the location.
     *
     * @returns a promise returning the experience
     */
    initializeExperience() {
        'proxyFunction';
        return null;
    }
    /**
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     */
    buildAndSetExperience(params) {
        'proxyFunction';
        return null;
    }
}

/**
 * The interface stipulates how to register features in the SmartEdit application and the SmartEdit container.
 * The SmartEdit implementation stores two instances of the interface across the {@link GatewayFactory gateway}: one for the SmartEdit application and one for the SmartEdit container.
 */
class IFeatureService {
    constructor(cloneableUtils, logService) {
        this.cloneableUtils = cloneableUtils;
        this.logService = logService;
    }
    /**
     * This method registers a feature.
     * When an end user selects a perspective, all the features that are bound to the perspective
     * will be enabled when their respective enablingCallback functions are invoked
     * and all the features that are not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     * The SmartEdit application and the SmartEdit container hold/store an instance of the implementation because callbacks cannot cross the gateway as they are functions.
     *
     * this method is meant to register a feature (identified by a key).
     * When a perspective (registered through [register]{@link IPerspectiveService#register}) is selected, all its bound features will be enabled by invocation of their respective enablingCallback functions
     * and any feature not bound to it will be disabled by invocation of its disablingCallback function.
     * Both SmartEdit and SmartEditContainer will hold a concrete implementation since Callbacks, being functions, cannot cross the gateway.
     * The function will keep a frame bound reference on a full feature in order to be able to invoke its callbacks when needed.
     *
     * @param configuration Configuration of a `IContextualMenuButton` or `IDecorator` or `IToolbarItem`.
     */
    register(configuration) {
        this._validate(configuration);
        this._featuresToAlias = this._featuresToAlias || {};
        this._featuresToAlias[configuration.key] = {
            enablingCallback: configuration.enablingCallback,
            disablingCallback: configuration.disablingCallback
        };
        delete configuration.enablingCallback;
        delete configuration.disablingCallback;
        return this._registerAliases(this.cloneableUtils.makeCloneable(configuration));
    }
    enable(key) {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].enablingCallback();
        }
        else {
            this._remoteEnablingFromInner(key);
        }
    }
    disable(key) {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].disablingCallback();
        }
        else {
            this._remoteDisablingFromInner(key).catch((reason) => {
                this.logService.debug(`IFeatureService - disable: ${JSON.stringify(reason)}`);
            });
        }
    }
    /**
     * @returns A promise of property value or null if property does not exist
     */
    getFeatureProperty(featureKey, propertyName) {
        'proxyFunction';
        return null;
    }
    /**
     * This method registers toolbar items as features. It is a wrapper around [register]{@link IFeatureService#register}.
     *
     * @param configuration Configuration that represents the toolbar action item to be registered.
     */
    addToolbarItem(toolbar) {
        'proxyFunction';
        return null;
    }
    /**
     * This method registers decorator and delegates to the
     *  {@link /smartedit/injectables/DecoratorService.html#enable enable}
     *  {@link /smartedit/injectables/DecoratorService.html#disable disable} methods.
     * This method is not a wrapper around {@link /smartedit/injectables/DecoratorService.html#addMappings addMappings}:
     * From a feature stand point, we deal with decorators, not their mappings to SmartEdit components.
     * We still need to have a separate invocation of {@link /smartedit/injectables/DecoratorService.html#addMappings addMappings}
     */
    addDecorator(decorator) {
        'proxyFunction';
        return null;
    }
    /**
     * This method registers contextual menu buttons.
     * It is a wrapper around {@link /smartedit/injectables/ContextualMenuService.html#addItems addItems}.
     */
    addContextualMenuButton(btn) {
        'proxyFunction';
        return null;
    }
    getFeatureKeys() {
        'proxyFunction';
        return null;
    }
    _remoteEnablingFromInner(key) {
        'proxyFunction';
        return null;
    }
    _remoteDisablingFromInner(key) {
        'proxyFunction';
        return null;
    }
    /**
     * This method registers a feature, identified by a unique key, across the {@link GatewayFactory}.
     * It is a simplified version of the register method, from which callbacks have been removed.
     */
    _registerAliases(configuration) {
        'proxyFunction';
        return null;
    }
    _validate(configuration) {
        if (lodash__namespace.isEmpty(configuration.key)) {
            throw new Error('featureService.configuration.key.error.required');
        }
        if (lodash__namespace.isEmpty(configuration.nameI18nKey)) {
            throw new Error('featureService.configuration.nameI18nKey.error.required');
        }
        if (!lodash__namespace.isFunction(configuration.enablingCallback)) {
            throw new Error('featureService.configuration.enablingCallback.error.not.function');
        }
        if (!lodash__namespace.isFunction(configuration.disablingCallback)) {
            throw new Error('featureService.configuration.disablingCallback.error.not.function');
        }
    }
}

/**
 * The interface defines the methods required to detect when the mouse leaves the notification panel
 * in the SmartEdit application and in the SmartEdit container.
 *
 * It is solely meant to be used with the notificationService.
 */
class INotificationMouseLeaveDetectionService {
    /**
     * This method starts tracking the movement of the mouse pointer in order to detect when it
     * leaves the notification panel.
     *
     * The innerBounds parameter is considered optional. If it is not provided, it will not be
     * validated and detection will only be started in the SmartEdit container.
     *
     * Here is an example of a bounds object:
     *
     * {
     *     x: 100,
     *     y: 100,
     *     width: 200,
     *     height: 50
     * }
     *
     * This method will throw an error if:
     *     - the bounds parameter is not provided
     *     - a bounds object does not contain the X coordinate
     *     - a bounds object does not contain the Y coordinate
     *     - a bounds object does not contain the width dimension
     *     - a bounds object does not contain the height dimension
     */
    startDetection(outerBounds, innerBounds, callback) {
        'proxyFunction';
        return null;
    }
    /**
     * This method stops tracking the movement of the mouse pointer.
     */
    stopDetection() {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to start tracking the movement of the mouse pointer within the iFrame.
     */
    _remoteStartDetection(bound) {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to stop tracking the movement of the mouse pointer within the iFrame.
     */
    _remoteStopDetection() {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to call the callback function when it is detected from within the iFrame that
     * the mouse left the notification panel
     */
    _callCallback() {
        'proxyFunction';
        return null;
    }
    /**
     * This method is called for each mouse movement. It evaluates whether or not the
     * mouse pointer is in the notification panel. If it isn't, it calls the onMouseLeave.
     */
    _onMouseMove(event) {
        this._getBounds().then((bounds) => {
            const isOutsideX = bounds &&
                event &&
                (event.clientX < bounds.x || event.clientX > bounds.x + bounds.width);
            const isOutsideY = bounds &&
                event &&
                (event.clientY < bounds.y || event.clientY > bounds.y + bounds.height);
            if (isOutsideX || isOutsideY) {
                this._onMouseLeave();
            }
        });
    }
    /**
     * This method gets bounds
     */
    _getBounds() {
        'proxyFunction';
        return null;
    }
    /**
     * This method gets callback
     */
    _getCallback() {
        'proxyFunction';
        return null;
    }
    /**
     * This method is triggered when the service has detected that the mouse left the
     * notification panel. It will execute the callback function and stop detection.
     */
    _onMouseLeave() {
        this._getCallback().then((callback) => {
            if (callback) {
                callback();
                this.stopDetection();
            }
            else {
                this._callCallback().then(() => {
                    this.stopDetection();
                });
            }
        });
    }
}

/**
 * INotificationService provides a service to display visual cues to inform
 * the user of the state of the application in the container or the iFramed application.
 * The interface defines the methods required to manage notifications that are to be displayed to the user.
 */
class INotificationService {
    /**
     * This method creates a new notification based on the given configuration and
     * adds it to the top of the list.
     *
     * The configuration must contain either one of componentName, template or templateUrl.
     *
     * ### Throws
     *
     * - Throws An error if no configuration is given.
     * - Throws An error if the configuration does not contain a unique identifier.
     * - Throws An error if the configuration's unique identifier is an empty string.
     * - Throws An error if the configuration does not contain a componenName, template or templateUrl.
     * - Throws An error if the configuration contains more than one template type.
     */
    pushNotification(configuration) {
        'proxyFunction';
        return null;
    }
    /**
     * Moves the notification with the given ID from the list.
     */
    removeNotification(notificationId) {
        'proxyFunction';
        return null;
    }
    /**
     * This method removes all notifications.
     */
    removeAllNotifications() {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * The IPageInfoService provides information about the storefront page currently loaded in the iFrame.
 */
class IPageInfoService {
    /**
     * This extracts the pageUID of the storefront page loaded in the smartedit iframe.
     */
    getPageUID() {
        'proxyFunction';
        return null;
    }
    /**
     * This extracts the pageUUID of the storefront page loaded in the smartedit iframe.
     * The UUID is different from the UID in that it is an encoding of uid and catalog version combined
     */
    getPageUUID() {
        'proxyFunction';
        return null;
    }
    /**
     * This extracts the catalogVersionUUID of the storefront page loaded in the smartedit iframe.
     * The UUID is different from the UID in that it is an encoding of uid and catalog version combined
     */
    getCatalogVersionUUIDFromPage() {
        'proxyFunction';
        return null;
    }
    /**
     * Determines whether the current page's catalog version same as page template's catalog version
     *
     * @returns True if current catalog is multicountry related; Otherwise false.
     */
    isSameCatalogVersionOfPageAndPageTemplate() {
        'proxyFunction';
        return null;
    }
}

/**
 * Interface for previewService.
 *
 * This service is for managing the storefront preview ticket and is proxied across the gateway.
 */
class IPreviewService {
    constructor(urlUtils) {
        this.urlUtils = urlUtils;
    }
    /**
     * This method will create a new previewTicket for the given experience, using the preview API
     *
     *
     * This method does *NOT* update the current experience.
     *
     * @param previewData Data representing storefront preview
     *
     * @returns An object with the ticketId
     */
    createPreview(previewData) {
        'proxyFunction';
        return null;
    }
    /**
     * This method will update a previewTicket for the given the preview data, using the preview API
     *
     * @param previewData Data representing storefront preview containing the preview ticketId
     *
     * @returns An object with the ticketId
     */
    updatePreview(previewData) {
        'proxyFunction';
        return null;
    }
    /**
     * This method will preduce a resourcePath from a given preview url
     *
     *
     * This method does *NOT* update the current experience.
     */
    getResourcePathFromPreviewUrl(previewUrl) {
        'proxyFunction';
        return null;
    }
    /**
     * This method will create a new preview ticket, and return the given url with an updated previewTicketId query param
     *
     *
     * This method does *NOT* update the current experience.
     *
     * @param storefrontUrl Existing storefront url
     * @param previewData JSON representing storefront previewData (catalog, catalog vesion, etc...)
     *
     * @returns A new string with storefrontUrl having the new ticket ID inside
     */
    updateUrlWithNewPreviewTicketId(storefrontUrl, previewData) {
        return this.createPreview(previewData).then((preview) => this.urlUtils.updateUrlParameter(storefrontUrl, 'cmsTicketId', preview.ticketId));
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IUrlService {
    /**
     * Opens a given URL in a new browser pop up without authentication.
     */
    openUrlInPopup(url) {
        'proxyFunction';
        return null;
    }
    /**
     * Navigates to the given path in the same browser tab.
     */
    path(path) {
        'proxyFunction';
        return null;
    }
    /**
     * Returns a uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildUriContext(siteId, catalogId, catalogVersion) {
        const uriContext = {};
        uriContext[CONTEXT_SITE_ID] = siteId;
        uriContext[CONTEXT_CATALOG] = catalogId;
        uriContext[CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    }
    /**
     * Returns a page uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildPageUriContext(siteId, catalogId, catalogVersion) {
        const uriContext = {};
        uriContext[PAGE_CONTEXT_SITE_ID] = siteId;
        uriContext[PAGE_CONTEXT_CATALOG] = catalogId;
        uriContext[PAGE_CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * This service be used in order to display (or hide) a 'loading' overlay. The overlay should display on top of everything, preventing
 * the user from doing any action until the overlay gets hidden.
 */
class IWaitDialogService {
    /**
     * @param customLoadingMessageLocalizedKey The i18n key that corresponds to the message to be displayed. Default value `"se.wait.dialog.message"`.
     */
    showWaitModal(customLoadingMessageLocalizedKey) {
        'proxyFunction';
        return null;
    }
    hideWaitModal() {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides the functionality to transmit a click event from anywhere within
 * the contents of the SmartEdit application iFrame to the SmartEdit container. Specifically, the module uses yjQuery to
 * bind mousedown events on the iFrame document to a proxy function, triggering the event on the SmartEdit container.
 */
class IIframeClickDetectionService {
    /**
     * Callback triggered by mousedown event of SmartEdit application
     */
    onIframeClick() {
        'proxyFunction';
        return null;
    }
    /**
     * Registers a callback registered given the ID and a callback function
     */
    registerCallback(id, callback) {
        'proxyFunction';
        return null;
    }
    /**
     * Removes a callback registered to the given ID
     */
    removeCallback(id) {
        'proxyFunction';
    }
}

const prepareRuleConfiguration = function (ruleConfiguration) {
    this.ruleVerifyFunctions = this.ruleVerifyFunctions || {};
    this.ruleVerifyFunctions[ruleConfiguration.names.join('-')] = {
        verify: ruleConfiguration.verify
    };
    delete ruleConfiguration.verify;
    return ruleConfiguration;
};
const validateRule = function (rule) {
    if (!(rule.names instanceof Array)) {
        throw Error('Rule names must be array');
    }
    if (rule.names.length < 1) {
        throw Error('Rule requires at least one name');
    }
    if (!rule.verify) {
        throw Error('Rule requires a verify function');
    }
    if (typeof rule.verify !== 'function') {
        throw Error('Rule verify must be a function');
    }
};
/**
 * The permission service is used to check if a user has been granted certain permissions.
 *
 * It is configured with rules and permissions. A rule is used to execute some logic to determine whether or not
 * the permission should be granted. A permission references a list of rules. In order for a permission to be
 * granted, each rule must be executed successfully and return true.
 */
class IPermissionService {
    /**
     * This method clears all cached results in the rules' caches.
     */
    clearCache() {
        'proxyFunction';
        return;
    }
    /**
     * This method returns the registered permission that contains the given name in its
     * array of names.
     *
     * @returns The permission with the given name, undefined otherwise.
     */
    getPermission(permission) {
        'proxyFunction';
        return null;
    }
    /**
     * This method checks if a user has been granted certain permissions.
     *
     * It takes an array of permission objects structured as follows:
     *
     * ### Example
     *
     *      {
     *          names: ["permission.aliases"],
     *          context: {
     *              data: "required to check a permission"
     *          }
     *      }
     *
     *
     * @returns A promise that resolves to true if permission is granted, rejects to false if it isn't and rejects on error.
     */
    isPermitted(permissions) {
        'proxyFunction';
        return null;
    }
    /**
     * This method registers a permission.
     *
     * A permission is defined by a set of aliases and rules. It is verified by its set of rules.
     * The set of aliases is there for convenience, as there may be different permissions
     * that use the same set of rules to be verified. The permission aliases property
     * will resolve if any one alias is in the aliases' array. Calling [isPermitted]{@link IPermissionService#isPermitted}
     * with any of these aliases will use the same permission object, therefore the same
     * combination of rules to check if the user has the appropriate clearance. This reduces the
     * number of permissions you need to register.
     *
     * ### Throws
     *
     * - Will throw an error if the permission has no aliases array
     * - Will throw an error if the permission's aliases array is empty
     * - Will throw an error if the permission has no rules array
     * - Will throw an error if the permission's rule aliases array is empty
     * - Will throw an error if a permission is already registered with a common entry in its array of aliases
     * - Will throw an error if one of the permission's aliases is not name spaced
     * - Will throw an error if no rule is registered with on of the permission's rule names
     */
    registerPermission(permission) {
        'proxyFunction';
        return;
    }
    /**
     * This method registers a rule. These rules can be used by registering permissions that
     * use them to verify if a user has the appropriate clearance.
     *
     * To avoid accidentally overriding the default rule, an error is thrown when attempting
     * to register a rule with the {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     *
     * To register the default rule, see [registerDefaultRule]{@link IPermissionService#registerDefaultRule}.
     *
     * It must return a promise that responds with true, false, or an error.
     *
     * ### Throws
     *
     * - Will throw an error if the list of rule names contains the reserved {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     * - Will throw an error if the rule has no names array.
     * - Will throw an error if the rule's names array is empty.
     * - Will throw an error if the rule has no verify function.
     * - Will throw an error if the rule's verify parameter is not a function.
     * - Will throw an error if a rule is already registered with a common entry in its names array
     */
    registerRule(ruleConfiguration) {
        validateRule(ruleConfiguration);
        ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
        this._registerRule(ruleConfiguration);
    }
    /**
     * This method registers the default rule.
     *
     * The default rule is used when no permission is found for a given permission name when
     * [isPermitted]{@link IPermissionService#isPermitted} is called.
     *
     * ### Throws
     *
     * - Will throw an error if the default rule's names does not contain {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     * - Will throw an error if the default rule has no names array.
     * - Will throw an error if the default rule's names array is empty.
     * - Will throw an error if the default rule has no verify function.
     * - Will throw an error if the default rule's verify parameter is not a function.
     * - Will throw an error if a rule is already registered with a common entry in its names array
     */
    registerDefaultRule(ruleConfiguration) {
        ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
        this._registerDefaultRule(ruleConfiguration);
    }
    unregisterDefaultRule() {
        'proxyFunction';
        return;
    }
    _registerRule(ruleConfiguration) {
        'proxyFunction';
        return;
    }
    _registerDefaultRule(ruleConfiguration) {
        'proxyFunction';
        return;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IResizeListener {
    unregister(element) {
        'proxyFunction';
    }
    fix(element) {
        'proxyFunction';
    }
    register(element, listener) {
        'proxyFunction';
    }
    init() {
        'proxyFunction';
    }
    dispose() {
        'proxyFunction';
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IPositionRegistry {
    register(element) {
        'proxyFunction';
    }
    unregister(element) {
        'proxyFunction';
    }
    getRepositionedComponents() {
        'proxyFunction';
        return null;
    }
    dispose() {
        'proxyFunction';
    }
}

class IRenderService {
    constructor(yjQuery, systemEventService, notificationService, pageInfoService, perspectiveService, crossFrameEventService, windowUtils, modalService, logService) {
        this.yjQuery = yjQuery;
        this.systemEventService = systemEventService;
        this.notificationService = notificationService;
        this.pageInfoService = pageInfoService;
        this.perspectiveService = perspectiveService;
        this.crossFrameEventService = crossFrameEventService;
        this.windowUtils = windowUtils;
        this.modalService = modalService;
        this.logService = logService;
        this.KEY_CODES = {
            ESC: 27
        };
        this.HOTKEY_NOTIFICATION_CONFIGURATION = {
            id: 'HOTKEY_NOTIFICATION_ID',
            componentName: 'PerspectiveSelectorHotkeyNotificationComponent'
        };
        this._bindEvents();
    }
    /**
     * Re-renders a slot in the page
     */
    renderSlots(_slotIds) {
        'proxyFunction';
        return null;
    }
    /**
     * Re-renders a component in the page.
     *
     * @param customContent The custom content to replace the component content with. If specified, the
     * component content will be rendered with it, instead of the accelerator's. Optional.
     *
     * @returns Promise that will resolve on render success or reject if there's an error. When rejected,
     * the promise returns an Object{message, stack}.
     */
    renderComponent(componentId, componentType) {
        'proxyFunction';
        return null;
    }
    /**
     * This method removes a component from a slot in the current page. Note that the component is only removed
     * on the frontend; the operation does not propagate to the backend.
     *
     * @param componentId The ID of the component to remove.
     *
     * @returns Object wrapping the removed component.
     */
    renderRemoval(componentId, componentType, slotId) {
        'proxyFunction';
        return null;
    }
    /**
     * Re-renders all components in the page.
     * this method first resets the HTML content all of components to the values saved by {@link /smartedit/injectables/DecoratorService.html#storePrecompiledComponent storePrecompiledComponent} at the last $compile time
     * then requires a new compilation.
     */
    renderPage(isRerender) {
        'proxyFunction';
        return null;
    }
    /**
     * Toggles on/off the visibility of the page overlay (containing the decorators).
     *
     * @param isVisible Flag that indicates if the overlay must be displayed.
     */
    toggleOverlay(isVisible) {
        'proxyFunction';
        return null;
    }
    /**
     * This method updates the position of the decorators in the overlay. Normally, this method must be executed every
     * time the original storefront content is updated to keep the decorators correctly positioned.
     */
    refreshOverlayDimensions() {
        'proxyFunction';
        return null;
    }
    /**
     * Toggles the rendering to be blocked or not which determines whether the overlay should be rendered or not.
     *
     * @param isBlocked Flag that indicates if the rendering should be blocked or not.
     */
    blockRendering(isBlocked) {
        'proxyFunction';
        return null;
    }
    /**
     * This method returns a boolean that determines whether the rendering is blocked or not.
     *
     * @returns True if the rendering is blocked. Otherwise false.
     */
    isRenderingBlocked() {
        'proxyFunction';
        return null;
    }
    createComponent(element) {
        'proxyFunction';
        return null;
    }
    destroyComponent(_component, _parent, oldAttributes) {
        'proxyFunction';
        return null;
    }
    updateComponentSizeAndPosition(element, componentOverlayElem) {
        'proxyFunction';
        return null;
    }
    _getDocument() {
        return document;
    }
    _bindEvents() {
        this._getDocument().addEventListener('keyup', (event) => this._keyUpEventHandler(event));
        this._getDocument().addEventListener('click', () => this._clickEvent());
    }
    _keyUpEventHandler(event) {
        if (!this._areAllModalWindowsClosed()) {
            return Promise.resolve();
        }
        return this._shouldEnableKeyPressEvent(event).then((enableKeyPressEvent) => {
            if (enableKeyPressEvent) {
                this._keyPressEvent();
            }
        });
    }
    _shouldEnableKeyPressEvent(event) {
        return new Promise((resolve) => this.pageInfoService
            .getPageUUID()
            .then((pageUUID) => {
            if (pageUUID) {
                return this.perspectiveService
                    .isHotkeyEnabledForActivePerspective()
                    .then((isHotkeyEnabled) => resolve(event.which === this.KEY_CODES.ESC && isHotkeyEnabled));
            }
            return resolve(false);
        })
            .catch(() => resolve(false)));
    }
    _keyPressEvent() {
        this.isRenderingBlocked().then((isBlocked) => {
            if (!isBlocked) {
                this.blockRendering(true);
                this.renderPage(false);
                this.notificationService.pushNotification(this.HOTKEY_NOTIFICATION_CONFIGURATION);
                this.systemEventService.publishAsync('OVERLAY_DISABLED');
            }
            else {
                this.blockRendering(false);
                this.renderPage(true);
                this.notificationService.removeNotification(this.HOTKEY_NOTIFICATION_CONFIGURATION.id);
            }
        });
    }
    _clickEvent() {
        if (!this.windowUtils.isIframe()) {
            this.crossFrameEventService.publish(EVENT_OUTER_FRAME_CLICKED).catch((reason) => {
                this.logService.debug(`IRenderService - _clickEvent: ${reason}`);
            });
        }
        else if (this.windowUtils.isIframe()) {
            this.crossFrameEventService.publish(EVENT_INNER_FRAME_CLICKED).catch((reason) => {
                this.logService.debug(`IRenderService - _clickEvent: ${reason}`);
            });
        }
        return this.isRenderingBlocked().then((isBlocked) => {
            if (isBlocked && !this.windowUtils.isIframe()) {
                this.blockRendering(false);
                this.renderPage(true);
                return this.notificationService.removeNotification(this.HOTKEY_NOTIFICATION_CONFIGURATION.id);
            }
            return null;
        });
    }
    _areAllModalWindowsClosed() {
        return !this.modalService.hasOpenModals();
    }
}

/**
 * The toolbar service factory generates instances of the {@link IToolbarService ToolbarService} based on
 * the gateway ID (toolbar-name) provided. Only one ToolbarService instance exists for each gateway ID, that is, the
 * instance is a singleton with respect to the gateway ID.
 */
class IToolbarServiceFactory {
    /**
     * Returns a single instance of the ToolbarService for the given gateway identifier. If one does not exist, an
     * instance is created and cached.
     *
     * @param gatewayId The toolbar name used for cross iframe communication (see {@link GatewayProxy}).
     * @returns Corresponding ToolbarService instance for given gateway ID.
     */
    getToolbarService(gatewayId) {
        'proxyFunction';
        return null;
    }
}

class IConfirmationModalService {
    confirm(conf) {
        'proxyFunction';
        return Promise.resolve(true);
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides a logic that allows to verify read and write permissions for a particular catalog version.
 */
class ICatalogVersionPermissionService {
    /**
     * Verifies whether current user has write permission for provided catalogId and catalogVersion.
     */
    hasWritePermission(catalogId, catalogVersion) {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has read permission for provided catalogId and catalogVersion.
     */
    hasReadPermission(catalogId, catalogVersion) {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has write permission for current catalog version.
     */
    hasWritePermissionOnCurrent() {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has read permission for current catalog version.
     */
    hasReadPermissionOnCurrent() {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has sync permission for provided catalogId, source and target catalog versions.
     */
    hasSyncPermission(catalogId, sourceCatalogVersion, targetCatalogVersion) {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has sync permission for current catalog version.
     */
    hasSyncPermissionFromCurrentToActiveCatalogVersion() {
        'proxyFunction';
        return null;
    }
    /**
     * Verifies whether current user has sync permission for provided catalogId and catalog version.
     */
    hasSyncPermissionToActiveCatalogVersion(catalogId, catalogVersion) {
        'proxyFunction';
        return null;
    }
}

class IDecoratorService {
    addMappings(mappings) {
        'proxyFunction';
    }
    enable(decoratorKey, displayCondition) {
        'proxyFunction';
    }
    disable(decoratorKey) {
        'proxyFunction';
    }
    getDecoratorsForComponent(componentType, componentId) {
        'proxyFunction';
        return null;
    }
}

class IRestServiceFactory {
    get(uri, identifier) {
        'proxyFunction';
        return null;
    }
    setDomain(domain) {
        'proxyFunction';
    }
    setBasePath(basePath) {
        'proxyFunction';
    }
    setGlobalBasePath(globalDomain) {
        'proxyFunction';
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IDragAndDropCrossOrigin {
    initialize() {
        'proxyFunction';
    }
}

class IContextualMenuService {
    addItems(contextualMenuItemsMap) {
        'proxyFunction';
    }
    removeItemByKey(itemKey) {
        'proxyFunction';
    }
    containsItem(itemKey) {
        'proxyFunction';
        return null;
    }
    getContextualMenuByType(componentType) {
        'proxyFunction';
        return null;
    }
    refreshMenuItems() {
        'proxyFunction';
    }
    getContextualMenuItems(configuration) {
        return null;
    }
}

class ICatalogDetailsService {
    addItems(items, column) {
        'proxyFunction';
    }
    getItems() {
        'proxyFunction';
        return null;
    }
}

class ISmartEditContractChangeListener {
    _newMutationObserver(callback) {
        return null;
    }
    _newIntersectionObserver(callback) {
        return null;
    }
    _addToComponentQueue(entry) {
        return null;
    }
    _componentsQueueLength() {
        return null;
    }
    isExtendedViewEnabled() {
        return null;
    }
    setEconomyMode(_mode) {
        return null;
    }
    initListener() {
        return null;
    }
    _processQueue() {
        return null;
    }
    isIntersecting(obj) {
        return null;
    }
    _rawProcessQueue() {
        return null;
    }
    _addComponents(componentsObj) {
        return null;
    }
    _removeComponents(componentsObj, forceRemoval = false) {
        return null;
    }
    _registerSizeAndPositionListeners(component) {
        return null;
    }
    _unregisterSizeAndPositionListeners(component) {
        return null;
    }
    stopListener() {
        return null;
    }
    _stopExpendableListeners() {
        return null;
    }
    _startExpendableListeners() {
        return null;
    }
    onComponentsAdded(callback) {
        return null;
    }
    onComponentsRemoved(callback) {
        return null;
    }
    onComponentChanged(callback) {
        return null;
    }
    onComponentResized(callback) {
        return null;
    }
    onComponentRepositioned(callback) {
        return null;
    }
    onPageChanged(callback) {
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
exports.COMPONENT_IN_SLOT_STATUS = void 0;
(function (COMPONENT_IN_SLOT_STATUS) {
    COMPONENT_IN_SLOT_STATUS["ALLOWED"] = "allowed";
    COMPONENT_IN_SLOT_STATUS["DISALLOWED"] = "disallowed";
    COMPONENT_IN_SLOT_STATUS["MAYBEALLOWED"] = "mayBeAllowed";
})(exports.COMPONENT_IN_SLOT_STATUS || (exports.COMPONENT_IN_SLOT_STATUS = {}));
/**
 * Provide methods that cache and return the restrictions of a slot in a page.
 * This restrictions determine whether a component of a certain type is allowed or forbidden in a particular slot.
 */
class ISlotRestrictionsService {
    /**
     * This methods retrieves the list of component types droppable in at least one of the slots of the current page.
     *
     * @returns Promise containing an array with the component types droppable on the current page.
     *
     * **Deprecated since 2005**
     * @deprecated
     */
    getAllComponentTypesSupportedOnPage() {
        'proxyFunction';
        return null;
    }
    /**
     * This methods retrieves the list of restrictions applied to the slot identified by the provided ID.
     *
     * @returns Promise containing an array with the restrictions applied to the slot.
     */
    getSlotRestrictions(slotId) {
        'proxyFunction';
        return null;
    }
    isSlotEditable(slotId) {
        'proxyFunction';
        return null;
    }
    isComponentAllowedInSlot(slot, dragInfo) {
        'proxyFunction';
        return null;
    }
    determineComponentStatusInSlot(slotId, dragInfo) {
        'proxyFunction';
        return null;
    }
}

/** The unique identifier for the page type. */
exports.CMSPageTypes = void 0;
(function (CMSPageTypes) {
    CMSPageTypes["ContentPage"] = "ContentPage";
    CMSPageTypes["CategoryPage"] = "CategoryPage";
    CMSPageTypes["ProductPage"] = "ProductPage";
    CMSPageTypes["EmailPage"] = "EmailPage";
})(exports.CMSPageTypes || (exports.CMSPageTypes = {}));
exports.CMSPageStatus = void 0;
(function (CMSPageStatus) {
    CMSPageStatus["ACTIVE"] = "ACTIVE";
    CMSPageStatus["DELETED"] = "DELETED";
})(exports.CMSPageStatus || (exports.CMSPageStatus = {}));
exports.CmsApprovalStatus = void 0;
(function (CmsApprovalStatus) {
    CmsApprovalStatus["APPROVED"] = "APPROVED";
    CmsApprovalStatus["CHECK"] = "CHECK";
    CmsApprovalStatus["UNAPPROVED"] = "UNAPPROVED";
})(exports.CmsApprovalStatus || (exports.CmsApprovalStatus = {}));

class IPageService {
    /**
     * Retrieves the page corresponding to the given page UID in the current contextual
     * site + catalog + catalog version.
     */
    getPageById(pageUid) {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the page information of the page identified by the given uuid.
     */
    getPageByUuid(pageUuid) {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the page information of the page that is currently loaded.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the current page
     */
    getCurrentPageInfo() {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves a version, as identified by the provided version id, of the page information that is currently loaded.
     *
     * @param versionId The ID of the page version to load.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the version selected of the current page
     */
    getCurrentPageInfoByVersion(versionId) {
        'proxyFunction';
        return null;
    }
    /**
     * Determines if a page belonging to the current contextual site+catalog+catalogversion is primary.
     */
    isPagePrimary(pageUid) {
        'proxyFunction';
        return null;
    }
    /**
     * Determines if a page belonging to the provided contextual site+catalog+catalogversion is primary.
     *
     * @param uriContext The uriContext for the pageId
     */
    isPagePrimaryWithContext(pageUid, uriContext) {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the primary page of the given variation page in the current site+catalog+catalogversion.
     *
     * @param variationPageId The UID of the variation page for which to find its primary page.
     *
     * @returns A promise that resolves to the page object or undefined if no primary page was found.
     */
    getPrimaryPage(variationPageUid) {
        'proxyFunction';
        return null;
    }
    /**
     * Returns true if primary page exists for a given page type
     */
    primaryPageForPageTypeExists(pageTypeCode, uriParams) {
        'proxyFunction';
        return null;
    }
    /**
     * Fetches a pagination page for list of pages for a given site+catalog+catalogversion and page
     * @returns A promise that resolves to pagination with array of pages
     */
    getPaginatedPrimaryPagesForPageType(pageTypeCode, uriParams, fetchPageParams) {
        'proxyFunction';
        return null;
    }
    /**
     * Retrieves the variation pages of the given primary page in the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves an array of variation pages or an empty list if none are found.
     */
    getVariationPages(primaryPageUid) {
        'proxyFunction';
        return null;
    }
    /**
     * Updates the page corresponding to the given page UID with the payload provided for the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves to the JSON page object as it now exists in the backend
     */
    updatePageById(pageUid, payload) {
        'proxyFunction';
        return null;
    }
    /**
     * This method will forcefully update the page approval status (as long as the current user has the right permissions) of the page loaded
     * in the current context to the given status.
     *
     * @returns If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    forcePageApprovalStatus(newPageStatus) {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to determine whether the given page is approved (and can be synched).
     */
    isPageApproved(pageParam) {
        'proxyFunction';
        return null;
    }
    /**
     * Returns the uriContext populated with the siteId, catalogId and catalogVersion taken from $routeParams and fallback to the currentExperience
     * Note: From the page list, $routeParams are defined. From the storefront, $routeParams are undefined.
     */
    buildUriContextForCurrentPage(siteId, catalogId, catalogVersion) {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
exports.JOB_STATUS = void 0;
(function (JOB_STATUS) {
    JOB_STATUS["RUNNING"] = "RUNNING";
    JOB_STATUS["ERROR"] = "ERROR";
    JOB_STATUS["FAILURE"] = "FAILURE";
    JOB_STATUS["FINISHED"] = "FINISHED";
    JOB_STATUS["UNKNOWN"] = "UNKNOWN";
    JOB_STATUS["ABORTED"] = "ABORTED";
    JOB_STATUS["PENDING"] = "PENDING";
})(exports.JOB_STATUS || (exports.JOB_STATUS = {}));

class ISyncPollingService {
    registerSyncPollingEvents() {
        'proxyFunction';
        return null;
    }
    changePollingSpeed(eventId, itemId) {
        'proxyFunction';
        return null;
    }
    fetchSyncStatus(_pageUUID, uriContext) {
        'proxyFunction';
        return null;
    }
    performSync(array, uriContext) {
        'proxyFunction';
        return null;
    }
    getSyncStatus(pageUUID, uriContext, forceGetSynchronization) {
        'proxyFunction';
        return null;
    }
}

/**
 * Convenience service to open an editor modal window for a given component type and component ID.
 *
 * Example:
 * We pass information about component to open method, and the component editor in form of modal appears.
 */
class IEditorModalService {
    /**
     * Proxy function which delegates opening an editor modal for a given component type and component ID to the
     * SmartEdit container.
     *
     * @param componentAttributes The details of the component to be created/edited
     * @param componentAttributes.smarteditComponentUuid An optional universally unique UUID of the component if the component is being edited.
     * @param componentAttributes.smarteditComponentId An optional universally unique ID of the component if the component is being edited.
     * @param componentAttributes.smarteditComponentType The component type
     * @param componentAttributes.smarteditCatalogVersionUuid The smartedit catalog version UUID to add the component to.
     * @param componentAttributes.catalogVersionUuid The catalog version UUID to add the component to.
     * @param componentAttributes.initialDirty Is the component dirty.
     * @param componentAttributes.content An optional content for create operation. It's ignored if componentAttributes.smarteditComponentUuid is defined.
     * @param targetSlotId The ID of the slot in which the component is placed.
     * @param position The position in a given slot where the component should be placed.
     * @param targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     * @param saveCallback The optional function that is executed if the user clicks the Save button and the modal closes successfully. The function provides one parameter: item that has been saved.
     * @param editorStackId The string that identifies the stack of editors being edited together.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    open(componentAttributes, targetSlotId, position, targetedQualifier, saveCallback, editorStackId, config) {
        'proxyFunction';
        return null;
    }
    /**
     * Proxy function which delegates opening an editor modal for a given component type and component ID to the
     * SmartEdit container.
     *
     * @param componentType The type of component as defined in the platform.
     * @param componentUuid The UUID of the component as defined in the database.
     * @param targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     * @param saveCallback The optional function that is executed if the user clicks the Save button and the modal closes successfully. The function provides one parameter: item that has been saved.
     * @param editorStackId The string that identifies the stack of editors being edited together.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    openAndRerenderSlot(componentType, componentUuid, targetedQualifier, config, saveCallback, editorStackId) {
        'proxyFunction';
        return null;
    }
    /**
     * Proxy function which delegates opening an generic editor modal for a given IGenericEditorModalServiceComponent data object
     *
     * @param componentData Object that contains all parameters for generic editor.
     * @param saveCallback the save callback that is triggered after submit.
     * @param errorCallback the error callback that is triggered after submit.
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    openGenericEditor(data, saveCallback, errorCallback, config) {
        'proxyFunction';
        return null;
    }
}

/**
 * Provides an abstract extensible pageTreeNode service. Used to build slotNodes in SmartEdit
 * application and get slotNodes in the SmartEdit container.
 * When SmartEdit bootstrap, PageTreeNodeServiceInner builds the slotNodes array from the page.
 * PageTreeNodeServiceOuter get the slotNodes when needs.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
class IPageTreeNodeService {
    constructor() {
        this.slotNodes = [];
    }
    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    buildSlotNodes() {
        'proxyFunction';
    }
    updateSlotNodes(data) {
        'proxyFunction';
    }
    getSlotNodes() {
        'proxyFunction';
        return null;
    }
    scrollToElement(elementUuid) {
        'proxyFunction';
        return null;
    }
    existedSmartEditElement(elementUuid) {
        'proxyFunction';
        return null;
    }
    handleBodyWidthChange() {
        'proxyFunction';
        return null;
    }
    _buildSlotNode(node) {
        'proxyFunction';
        return null;
    }
    _isValidElement(ele) {
        'proxyFunction';
        return true;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides an abstract extensible pageTree service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
class IPageTreeService {
    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    registerTreeComponent(item) {
        'proxyFunction';
        return null;
    }
    getTreeComponent() {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IFileValidation {
    buildAcceptedFileTypesList() {
        'proxyFunction';
        return null;
    }
    validate(file, maxUploadFileSize, errorsContext) {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides an abstract extensible user tracking service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
class IUserTrackingService {
    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    initConfiguration() {
        'proxyFunction';
        return null;
    }
    trackingUserAction(functionality, key) {
        'proxyFunction';
    }
}

/**
 * The IMediaToUpload provides abstract class to upload media content.
 */
class IMediaToUpload {
    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media) {
        return null;
    }
}

/**
 * The IMediaService provides abstract class to retrieve media content.
 */
class IMediaService {
    /**
     * Fetches paged search results by making a REST call to the appropriate item endpoint.
     *
     * @param mask for filtering the search.
     * @param pageSize number of items in the page.
     * @param currentPage current page number.
     */
    getPage(mask, pageSize, currentPage, mimeType) {
        return null;
    }
    /**
     * This method fetches a Media by its UUID.
     * @param uuid uuid of a media (contains catalog information).
     */
    getMedia(uuid) {
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const WIZARD_MANAGER = new core.InjectionToken('WIZARD_MANAGER');
const WIZARD_API = new core.InjectionToken('WIZARD_API');
/**
 * The Wizard Manager is a wizard management service that can be injected into your wizard controller.
 */
class WizardService {
    constructor(defaultWizardActionStrategy, stringUtils) {
        this.defaultWizardActionStrategy = defaultWizardActionStrategy;
        this.stringUtils = stringUtils;
        // the overridable callbacks
        this.onLoadStep = function (index, nextStep) {
            return;
        };
        this.onClose = function (result) {
            return;
        };
        this.onCancel = function () {
            return;
        };
        this.onStepsUpdated = function (steps) {
            return;
        };
    }
    /* @internal */
    initialize(conf) {
        this.validateConfig(conf);
        this._actionStrategy = conf.actionStrategy || this.defaultWizardActionStrategy;
        this._actionStrategy.applyStrategy(this, conf);
        this._currentIndex = 0;
        this._conf = Object.assign({}, conf);
        this._steps = this._conf.steps;
        this._getResult = conf.resultFn;
        this.validateStepUids(this._steps);
        this.goToStepWithIndex(0);
    }
    /* @internal */
    executeAction(action) {
        if (action.executeIfCondition) {
            const result = action.executeIfCondition();
            return (result instanceof Promise ? result : Promise.resolve(result)).then(() => action.execute(this));
        }
        return Promise.resolve(action.execute(this));
    }
    /**
     * Navigates the wizard to the given step.
     * @param index The 0-based index from the steps array returned by the wizard controllers getWizardConfig() function
     */
    goToStepWithIndex(index) {
        const nextStep = this.getStepWithIndex(index);
        if (nextStep) {
            this.onLoadStep(index, nextStep);
            this._currentIndex = index;
        }
    }
    /**
     * Navigates the wizard to the given step.
     * @param id The ID of a step returned by the wizard controllers getWizardConfig() function. Note that if
     * no id was provided for a given step, then one is automatically generated.
     */
    goToStepWithId(id) {
        this.goToStepWithIndex(this.getStepIndexFromId(id));
    }
    /**
     * Adds an additional step to the wizard at runtime
     * @param index (OPTIONAL) A 0-based index position in the steps array. Default is 0.
     */
    addStep(newStep, index) {
        if (parseInt(newStep.id, 10) !== 0 && !newStep.id) {
            newStep.id = this.stringUtils.generateIdentifier();
        }
        if (!index) {
            index = 0;
        }
        if (this._currentIndex >= index) {
            this._currentIndex++;
        }
        this._steps.splice(index, 0, newStep);
        this.validateStepUids(this._steps);
        this._actionStrategy.applyStrategy(this, this._conf);
        this.onStepsUpdated(this._steps);
    }
    /**
     * Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     */
    removeStepById(id) {
        this.removeStepByIndex(this.getStepIndexFromId(id));
    }
    /**
     * Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     * @param index The 0-based index of the step you wish to remove.
     */
    removeStepByIndex(index) {
        if (index >= 0 && index < this.getStepsCount()) {
            this._steps.splice(index, 1);
            if (index === this._currentIndex) {
                this.goToStepWithIndex(0);
            }
            this._actionStrategy.applyStrategy(this, this._conf);
            this.onStepsUpdated(this._steps);
        }
    }
    /**
     * Close the wizard. This will return a resolved promise to the creator of the wizard, and if any
     * resultFn was provided in the {@link ModalWizardConfig} the returned
     * value of this function will be passed as the result.
     */
    close() {
        let result;
        if (typeof this._getResult === 'function') {
            result = this._getResult();
        }
        this.onClose(result);
    }
    /**
     * Cancel the wizard. This will return a rejected promise to the creator of the wizard.
     */
    cancel() {
        this.onCancel();
    }
    getSteps() {
        return this._steps;
    }
    getStepIndexFromId(id) {
        const index = this._steps.findIndex((step) => step.id === id);
        return index;
    }
    /**
     * @returns True if the ID exists in one of the steps
     */
    containsStep(stepId) {
        return this.getStepIndexFromId(stepId) >= 0;
    }
    getCurrentStepId() {
        return this.getCurrentStep().id;
    }
    getCurrentStepIndex() {
        return this._currentIndex;
    }
    getCurrentStep() {
        return this.getStepWithIndex(this._currentIndex);
    }
    /**
     * @returns The number of steps in the wizard. This should always be equal to the size of the array.
     * returned by [getSteps]{@link WizardManager#getSteps}.
     */
    getStepsCount() {
        return this._steps.length;
    }
    getStepWithId(id) {
        const index = this.getStepIndexFromId(id);
        if (index >= 0) {
            return this.getStepWithIndex(index);
        }
        return null;
    }
    getStepWithIndex(index) {
        if (index >= 0 && index < this.getStepsCount()) {
            return this._steps[index];
        }
        throw new Error('wizardService.getStepForIndex - Index out of bounds: ' + index);
    }
    validateConfig(config) {
        if (!config.steps || config.steps.length <= 0) {
            throw new Error('Invalid WizardService configuration - no steps provided');
        }
    }
    validateStepUids(steps) {
        const stepIds = {};
        steps.forEach((step) => {
            if (!step.id) {
                step.id = this.stringUtils.generateIdentifier();
            }
            else if (stepIds[step.id]) {
                throw new Error(`Invalid (Duplicate) step id: ${step.id}`);
            }
            else {
                stepIds[step.id] = step.id;
            }
        });
    }
}

const DEFAULT_WIZARD_ACTION = {
    id: 'wizard_action_id',
    i18n: 'wizard_action_label',
    isMainAction: true,
    enableIfCondition() {
        return true;
    },
    executeIfCondition() {
        return true;
    },
    execute(wizardService) {
        return;
    }
};
/* @internal */
class WizardActions {
    customAction(configuration) {
        return this.createNewAction(configuration);
    }
    done(configuration) {
        const custom = {
            id: 'ACTION_SAVE',
            i18n: 'se.action.save',
            execute: (wizardService) => {
                wizardService.close();
            }
        };
        return this.createNewAction(configuration, custom);
    }
    next(configuration) {
        const custom = {
            id: 'ACTION_NEXT',
            i18n: 'se.action.next',
            execute(wizardService) {
                wizardService.goToStepWithIndex(wizardService.getCurrentStepIndex() + 1);
            }
        };
        return this.createNewAction(configuration, custom);
    }
    navBarAction(configuration) {
        if (!configuration.wizardService || configuration.destinationIndex === null) {
            throw new Error('Error initializating navBarAction, must provide the wizardService and destinationIndex fields');
        }
        const custom = {
            id: 'ACTION_GOTO',
            i18n: 'action.goto',
            enableIfCondition: () => configuration.wizardService.getCurrentStepIndex() >= configuration.destinationIndex,
            execute: (wizardService) => {
                wizardService.goToStepWithIndex(configuration.destinationIndex);
            }
        };
        return this.createNewAction(configuration, custom);
    }
    back(configuration) {
        const custom = {
            id: 'ACTION_PREVIOUS',
            i18n: 'se.action.previous',
            isMainAction: false,
            execute(wizardService) {
                const currentIndex = wizardService.getCurrentStepIndex();
                if (currentIndex <= 0) {
                    throw new Error('Failure to execute BACK action, no previous index exists!');
                }
                wizardService.goToStepWithIndex(currentIndex - 1);
            }
        };
        return this.createNewAction(configuration, custom);
    }
    cancel() {
        return this.createNewAction({
            id: 'ACTION_CANCEL',
            i18n: 'se.action.cancel',
            isMainAction: false,
            execute(wizardService) {
                wizardService.cancel();
            }
        });
    }
    createNewAction(configuration = null, customConfiguration = null) {
        return Object.assign(Object.assign(Object.assign({}, DEFAULT_WIZARD_ACTION), customConfiguration), configuration);
    }
}

/* @internal */
let /* @ngInject */ DefaultWizardActionStrategy = class /* @ngInject */ DefaultWizardActionStrategy {
    constructor(wizardActions) {
        this.wizardActions = wizardActions;
    }
    applyStrategy(wizardService, conf) {
        const nextAction = this.applyOverrides(wizardService, this.wizardActions.next(), conf.nextLabel, conf.onNext, conf.isFormValid);
        const doneAction = this.applyOverrides(wizardService, this.wizardActions.done(), conf.doneLabel, conf.onDone, conf.isFormValid);
        const backConf = conf.backLabel
            ? {
                i18n: conf.backLabel
            }
            : null;
        const backAction = this.wizardActions.back(backConf);
        conf.steps.forEach((step, index) => {
            step.actions = [];
            if (index > 0) {
                step.actions.push(backAction);
            }
            if (index === conf.steps.length - 1) {
                step.actions.push(doneAction);
            }
            else {
                step.actions.push(nextAction);
            }
        });
        conf.cancelAction = this.applyOverrides(wizardService, this.wizardActions.cancel(), conf.cancelLabel, conf.onCancel, null);
        conf.templateOverride = 'modalWizardNavBarTemplate.html';
    }
    applyOverrides(wizardService, action, label, executeCondition, enableCondition) {
        if (label) {
            action.i18n = label;
        }
        if (executeCondition) {
            action.executeIfCondition = function () {
                return executeCondition(wizardService.getCurrentStepId());
            };
        }
        if (enableCondition) {
            action.enableIfCondition = function () {
                return enableCondition(wizardService.getCurrentStepId());
            };
        }
        return action;
    }
};
/* @ngInject */ DefaultWizardActionStrategy = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [WizardActions])
], /* @ngInject */ DefaultWizardActionStrategy);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
window.__smartedit__.addDecoratorPayload("Component", "ModalWizardTemplateComponent", {
    selector: 'se-modal-wizard-template',
    template: `
        <div id="yModalWizard">
            <div class="se-modal-wizard-template">
                <se-modal-wizard-nav-bar
                    [navActions]="_wizardContext?.navActions"
                    (executeAction)="executeAction($event)"></se-modal-wizard-nav-bar>
                <se-modal-wizard-step-outlet
                    [steps]="_wizardContext?._steps"
                    [wizardService]="wizardService"
                    [wizardApiInjector]="wizardInjector"></se-modal-wizard-step-outlet>
            </div>
        </div>
    `
});
exports.ModalWizardTemplateComponent = class ModalWizardTemplateComponent {
    constructor(modalManager, wizardActions, defaultWizardActionStrategy, componentFactoryResolver, injector) {
        this.modalManager = modalManager;
        this.wizardActions = wizardActions;
        this.defaultWizardActionStrategy = defaultWizardActionStrategy;
        this.componentFactoryResolver = componentFactoryResolver;
        this.injector = injector;
    }
    ngOnInit() {
        this.modalManager.getModalData().subscribe((config) => {
            this.wizardService = new WizardService(this.defaultWizardActionStrategy, stringUtils);
            this.wizardService.properties = config.properties;
            if (config.component) {
                this.createWizardApiInjector(config);
            }
            if (typeof this.getWizardConfig !== 'function') {
                throw new Error('The provided controller must provide a getWizardConfig() function.');
            }
            const modalConfig = this.getWizardConfig();
            this._wizardContext = {
                _steps: modalConfig.steps
            };
            this.executeAction = (action) => {
                this.wizardService.executeAction(action);
            };
            this.wizardService.onLoadStep = (stepIndex, step) => {
                this.modalManager.setTitle(step.title);
                if (step.component) {
                    this._wizardContext.component = step.component;
                }
                this.modalManager.removeAllButtons();
                const buttonsConfig = (step.actions || []).map((action) => this.convertActionToButtonConf(action));
                this.modalManager.addButtons(buttonsConfig);
            };
            this.wizardService.onClose = (result) => {
                this.modalManager.close(result);
            };
            this.wizardService.onCancel = () => {
                this.modalManager.close();
            };
            this.wizardService.onStepsUpdated = (steps) => {
                this.setupNavBar(steps);
                this._wizardContext._steps = steps;
            };
            this.wizardService.initialize(modalConfig);
            this.setupModal(modalConfig);
        });
    }
    setupNavBar(steps) {
        this._wizardContext.navActions = steps.map((step, index) => {
            const action = this.wizardActions.navBarAction({
                id: 'NAV-' + step.id,
                stepIndex: index,
                wizardService: this.wizardService,
                destinationIndex: index,
                i18n: step.name,
                isCurrentStep: () => action.stepIndex === this.wizardService.getCurrentStepIndex()
            });
            return action;
        });
    }
    setupModal(setupConfig) {
        this._wizardContext.templateOverride = setupConfig.templateOverride;
        if (setupConfig.cancelAction) {
            this.modalManager.setDismissCallback(() => this.wizardService.executeAction(setupConfig.cancelAction));
        }
        this.setupNavBar(setupConfig.steps);
    }
    convertActionToButtonConf(action) {
        return {
            id: action.id,
            style: action.isMainAction ? utils.ModalButtonStyle.Primary : utils.ModalButtonStyle.Default,
            label: action.i18n,
            action: utils.ModalButtonAction.None,
            disabledFn: () => !action.enableIfCondition(),
            callback: () => {
                this.wizardService.executeAction(action);
                return rxjs.of(null);
            }
        };
    }
    createWizardApiInjector(config) {
        const injector = core.Injector.create({
            providers: [{ provide: WIZARD_MANAGER, useValue: this.wizardService }],
            parent: this.injector
        });
        const factory = this.componentFactoryResolver.resolveComponentFactory(config.component);
        const createdComponent = factory.create(injector).instance;
        lodash__namespace.assign(this, createdComponent);
        this.wizardInjector = core.Injector.create({
            providers: [{ provide: WIZARD_API, useValue: createdComponent }],
            parent: this.injector
        });
    }
};
exports.ModalWizardTemplateComponent = __decorate([
    core.Component({
        selector: 'se-modal-wizard-template',
        template: `
        <div id="yModalWizard">
            <div class="se-modal-wizard-template">
                <se-modal-wizard-nav-bar
                    [navActions]="_wizardContext?.navActions"
                    (executeAction)="executeAction($event)"></se-modal-wizard-nav-bar>
                <se-modal-wizard-step-outlet
                    [steps]="_wizardContext?._steps"
                    [wizardService]="wizardService"
                    [wizardApiInjector]="wizardInjector"></se-modal-wizard-step-outlet>
            </div>
        </div>
    `
    }),
    __metadata("design:paramtypes", [utils.ModalManagerService,
        WizardActions,
        DefaultWizardActionStrategy,
        core.ComponentFactoryResolver,
        core.Injector])
], exports.ModalWizardTemplateComponent);

/**
 * Used to create wizards that are embedded into the {@link ModalService}.
 */
/* @ngInject */ exports.ModalWizard = class /* @ngInject */ ModalWizard {
    constructor(modalService) {
        this.modalService = modalService;
    }
    /**
     * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you
     * such as look, feel and wizard navigation.
     *
     * @returns Promise that will either be resolved (wizard finished) or
     * rejected (wizard cancelled).
     */
    open(config) {
        this.validateConfig(config);
        return new Promise((resolve, reject) => {
            const ref = this.modalService.open({
                component: exports.ModalWizardTemplateComponent,
                templateConfig: { isDismissButtonVisible: true },
                data: config,
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'se-wizard-dialog'
                }
            });
            ref.afterClosed.subscribe(resolve, reject);
        });
    }
    validateConfig(config) {
        if (!config.controller && !config.component) {
            throw new Error('WizardService - initialization exception. No controller nor component provided');
        }
        if (config.controller && config.component) {
            throw new Error('WizardService - initialization exception. Provide either controller or component');
        }
    }
};
/* @ngInject */ exports.ModalWizard = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.IModalService])
], /* @ngInject */ exports.ModalWizard);

window.__smartedit__.addDecoratorPayload("Component", "ModalWizardNavBarComponent", {
    selector: 'se-modal-wizard-nav-bar',
    template: `
        <div class="se-modal-wizard__steps-container" *ngIf="navActions">
            <fd-wizard [appendToWizard]="false" [displaySummaryStep]="true">
                <fd-wizard-navigation>
                    <ul fd-wizard-progress-bar size="md">
                        <li
                            [attr.id]="action.id"
                            fd-wizard-step
                            *ngFor="let action of navActions; let i = index"
                            [status]="
                                action.isCurrentStep()
                                    ? 'current'
                                    : action.enableIfCondition()
                                    ? 'completed'
                                    : 'upcoming'
                            "
                            [ngClass]="{ 'se-none-cursor': !action.enableIfCondition() }"
                            [label]="action.i18n | translate"
                            (click)="onClickAction(action)">
                            <fd-wizard-step-indicator>{{ i + 1 }}</fd-wizard-step-indicator>
                            <fd-wizard-content size="md"> </fd-wizard-content>
                        </li>
                    </ul>
                </fd-wizard-navigation>
            </fd-wizard>
        </div>
    `
});
exports.ModalWizardNavBarComponent = class ModalWizardNavBarComponent {
    constructor() {
        this.executeAction = new core.EventEmitter();
    }
    onClickAction(action) {
        this.executeAction.emit(action);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.ModalWizardNavBarComponent.prototype, "navActions", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.ModalWizardNavBarComponent.prototype, "executeAction", void 0);
exports.ModalWizardNavBarComponent = __decorate([
    core.Component({
        selector: 'se-modal-wizard-nav-bar',
        template: `
        <div class="se-modal-wizard__steps-container" *ngIf="navActions">
            <fd-wizard [appendToWizard]="false" [displaySummaryStep]="true">
                <fd-wizard-navigation>
                    <ul fd-wizard-progress-bar size="md">
                        <li
                            [attr.id]="action.id"
                            fd-wizard-step
                            *ngFor="let action of navActions; let i = index"
                            [status]="
                                action.isCurrentStep()
                                    ? 'current'
                                    : action.enableIfCondition()
                                    ? 'completed'
                                    : 'upcoming'
                            "
                            [ngClass]="{ 'se-none-cursor': !action.enableIfCondition() }"
                            [label]="action.i18n | translate"
                            (click)="onClickAction(action)">
                            <fd-wizard-step-indicator>{{ i + 1 }}</fd-wizard-step-indicator>
                            <fd-wizard-content size="md"> </fd-wizard-content>
                        </li>
                    </ul>
                </fd-wizard-navigation>
            </fd-wizard>
        </div>
    `
    })
], exports.ModalWizardNavBarComponent);

window.__smartedit__.addDecoratorPayload("Component", "ModalWizardStepOutletComponent", {
    selector: 'se-modal-wizard-step-outlet',
    template: `
        <ng-container *ngIf="steps && steps.length > 0">
            <ng-container *ngFor="let step of steps">
                <div
                    [ngClass]="{
                        'se-modal-wizard__content--visible': isActive(step)
                    }"
                    class="se-modal-wizard__content">
                    <div *ngIf="step.component">
                        <ng-container
                            *ngComponentOutlet="
                                step.component;
                                injector: wizardApiInjector
                            "></ng-container>
                    </div>
                </div>
            </ng-container>
        </ng-container>
    `
});
exports.ModalWizardStepOutletComponent = class ModalWizardStepOutletComponent {
    isActive(step) {
        return this.wizardService.getCurrentStepId() === step.id;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.ModalWizardStepOutletComponent.prototype, "steps", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", WizardService)
], exports.ModalWizardStepOutletComponent.prototype, "wizardService", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Injector)
], exports.ModalWizardStepOutletComponent.prototype, "wizardApiInjector", void 0);
exports.ModalWizardStepOutletComponent = __decorate([
    core.Component({
        selector: 'se-modal-wizard-step-outlet',
        template: `
        <ng-container *ngIf="steps && steps.length > 0">
            <ng-container *ngFor="let step of steps">
                <div
                    [ngClass]="{
                        'se-modal-wizard__content--visible': isActive(step)
                    }"
                    class="se-modal-wizard__content">
                    <div *ngIf="step.component">
                        <ng-container
                            *ngComponentOutlet="
                                step.component;
                                injector: wizardApiInjector
                            "></ng-container>
                    </div>
                </div>
            </ng-container>
        </ng-container>
    `
    })
], exports.ModalWizardStepOutletComponent);

/**
 * Module containing all wizard related services
 * # Creating a modal wizard in a few simple steps
 *
 * Usage
 * 1. Import WizardModule from "smarteditcommons" to your module imports
 * 2. Inject {@link ModalWizard} using ModalWizard constructor from "smarteditcommons".
 * 3. Create a new component controller for your wizard. This component will be used for all steps of the wizard.
 * 4. Create step components to be rendered inside the wizard. If access to component controller is needed, inject the parent reference
 * 5. Implement a method in your new controller called getWizardConfig that returns a {@link ModalWizardConfig}
 * 6. Use [open]{@link ModalWizard#open} method, passing in your new controller
 *
 * ### Example
 *
 *      export class MyAngularWizardService {
 * 		    constructor(private modalWizard: ModalWizard) {}
 * 		    open() {
 * 			    this.modalWizard.open({
 *                  component: MyWizardControllerComponent,
 * 			    });
 * 		    }
 *      }
 *
 */
exports.WizardModule = class WizardModule {
};
exports.WizardModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$1.TranslateModule, wizard.WizardModule],
        providers: [exports.ModalWizard, WizardActions, DefaultWizardActionStrategy],
        declarations: [
            exports.ModalWizardTemplateComponent,
            exports.ModalWizardNavBarComponent,
            exports.ModalWizardStepOutletComponent
        ],
        entryComponents: [
            exports.ModalWizardTemplateComponent,
            exports.ModalWizardNavBarComponent,
            exports.ModalWizardStepOutletComponent
        ]
    })
], exports.WizardModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const TOOLBAR_ITEM = new core.InjectionToken('TOOLBAR_ITEM');
exports.ToolbarItemType = void 0;
(function (ToolbarItemType) {
    ToolbarItemType["TEMPLATE"] = "TEMPLATE";
    ToolbarItemType["ACTION"] = "ACTION";
    ToolbarItemType["HYBRID_ACTION"] = "HYBRID_ACTION";
})(exports.ToolbarItemType || (exports.ToolbarItemType = {}));
exports.ToolbarSection = void 0;
(function (ToolbarSection) {
    ToolbarSection["left"] = "left";
    ToolbarSection["middle"] = "middle";
    ToolbarSection["right"] = "right";
})(exports.ToolbarSection || (exports.ToolbarSection = {}));
exports.ToolbarDropDownPosition = void 0;
(function (ToolbarDropDownPosition) {
    ToolbarDropDownPosition["left"] = "left";
    ToolbarDropDownPosition["center"] = "center";
    ToolbarDropDownPosition["right"] = "right";
})(exports.ToolbarDropDownPosition || (exports.ToolbarDropDownPosition = {}));
/**
 * Provides an abstract extensible toolbar service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
class IToolbarService {
    constructor(logService, permissionService) {
        this.logService = logService;
        this.permissionService = permissionService;
        this.aliases = [];
        this.actions = {};
    }
    getActions() {
        return this.actions;
    }
    getAliases() {
        return this.aliases;
    }
    /**
     * Takes an array of items and maps them internally for display and trigger through an internal callback key.
     * The action's properties are made available through the included template by a variable named 'item'.
     *
     * This context can be shown or hidden by calling the events `seConstantsModule.SHOW_TOOLBAR_ITEM_CONTEXT` and
     * `seConstantsModule.HIDE_TOOLBAR_ITEM_CONTEXT` respectively. Both the events need the key of the toolbar item as data.
     *
     * ### Example
     *
     *      crossFrameEventService.publish(SHOW_TOOLBAR_ITEM_CONTEXT, 'toolbar.item.key');
     *
     */
    addItems(_items) {
        const promisesToResolve = _items
            .filter((item) => {
            // Validate provided actions -> The filter will return only valid items.
            let includeAction = true;
            if (!item.key) {
                this.logService.error('addItems() - Cannot add item without key.');
                includeAction = false;
            }
            return includeAction;
        })
            .map((item) => {
            const key = item.key;
            this.actions[key] = item.callback;
            const toolbarItem = {
                key,
                name: item.nameI18nKey,
                iconClassName: item.iconClassName,
                description: item.descriptionI18nKey,
                icons: item.icons,
                type: item.type,
                priority: item.priority || 500,
                section: item.section || 'left',
                isOpen: false,
                component: item.component,
                actionButtonFormat: item.actionButtonFormat,
                keepAliveOnClose: item.keepAliveOnClose || false,
                contextComponent: item.contextComponent,
                dropdownPosition: item.dropdownPosition,
                permissions: item.permissions
            };
            return this._populateIsPermissionGranted(toolbarItem);
        });
        return Promise.all(promisesToResolve).then((items) => {
            if (items.length > 0) {
                this.addAliases(items);
            }
        });
    }
    _populateIsPermissionGranted(toolbarItem) {
        if (toolbarItem.permissions) {
            return Promise.resolve(this.permissionService
                .isPermitted([
                {
                    names: toolbarItem.permissions
                }
            ])
                .then((isGranted) => {
                toolbarItem.isPermissionGranted = isGranted;
                return toolbarItem;
            }));
        }
        else {
            toolbarItem.isPermissionGranted = true;
            return Promise.resolve(toolbarItem);
        }
    }
    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    addAliases(items) {
        'proxyFunction';
    }
    removeItemByKey(key) {
        'proxyFunction';
    }
    _removeItemOnInner(itemKey) {
        'proxyFunction';
    }
    removeAliasByKey(itemKey) {
        'proxyFunction';
    }
    triggerActionOnInner(action) {
        'proxyFunction';
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Service containing truncate string functions.
 * @internal
 */
class TextTruncateService {
    /**
     * Truncates text to the nearest word depending on character length.
     * Truncates below character length.
     *
     * @param limit index in text to truncate to
     */
    truncateToNearestWord(limit, text, ellipsis = '') {
        if (lodash__namespace.isNil(text) || limit > text.length) {
            return new TruncatedText(text, text, false);
        }
        const regexp = /(\s)/g;
        const truncatedGroups = text.match(regexp);
        let truncateIndex = 0;
        if (!truncatedGroups) {
            truncateIndex = limit;
        }
        else {
            for (let i = 0; i < truncatedGroups.length; i++) {
                const nextPosition = this.getPositionOfCharacters(text, truncatedGroups[i], i + 1);
                if (nextPosition > limit) {
                    break;
                }
                truncateIndex = nextPosition;
            }
        }
        const truncated = text.substr(0, truncateIndex);
        return new TruncatedText(text, truncated, true, ellipsis);
    }
    getPositionOfCharacters(searchString, characters, index) {
        return searchString.split(characters, index).join(characters).length;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const FrequentlyChangingContentName = 'FrequentlyChangingContent';
const frequentlyChangingContent = new utils.CacheAction(FrequentlyChangingContentName);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @internal
 * @ignore
 */
const RarelyChangingContentName = 'RarelyChangingContent';
const rarelyChangingContent = new utils.CacheAction(RarelyChangingContentName);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// TODO : merge the EVENT strings and the tag ones
const authorizationEvictionTag = new utils.EvictionTag({ event: 'AUTHORIZATION_SUCCESS' });

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// TODO : merge the EVENT strings and the tag ones
const userEvictionTag = new utils.EvictionTag({ event: 'USER_HAS_CHANGED' });

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const catalogSyncedEvictionTag = new utils.EvictionTag({ event: 'CATALOG_SYNCHRONIZED_EVENT' });
const catalogEvictionTag = new utils.EvictionTag({
    event: 'CATALOG_EVENT',
    relatedTags: [catalogSyncedEvictionTag, userEvictionTag]
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const pageCreationEvictionTag = new utils.EvictionTag({ event: 'PAGE_CREATED_EVENT' });
const pageDeletionEvictionTag = new utils.EvictionTag({ event: 'PAGE_DELETED_EVENT' });
const pageUpdateEvictionTag = new utils.EvictionTag({ event: 'PAGE_UPDATED_EVENT' });
const pageRestoredEvictionTag = new utils.EvictionTag({ event: 'PAGE_RESTORED_EVENT' });
const pageChangeEvictionTag = new utils.EvictionTag({ event: 'PAGE_CHANGE' });
const pageEvictionTag = new utils.EvictionTag({
    event: 'pageEvictionTag',
    relatedTags: [pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag]
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const contentCatalogUpdateEvictionTag = new utils.EvictionTag({
    event: 'EVENT_CONTENT_CATALOG_UPDATE'
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const perspectiveChangedEvictionTag = new utils.EvictionTag({
    event: 'EVENT_PERSPECTIVE_CHANGED'
});

const CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API = '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs';
/* @ngInject */ exports.ContentCatalogRestService = class /* @ngInject */ ContentCatalogRestService extends utils.AbstractCachedRestService {
    constructor(restServiceFactory) {
        super(restServiceFactory, CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API);
    }
};
/* @ngInject */ exports.ContentCatalogRestService = __decorate([
    utils.CacheConfig({
        actions: [rarelyChangingContent],
        tags: [userEvictionTag, pageEvictionTag, contentCatalogUpdateEvictionTag]
    }),
    utils.OperationContextRegistered(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API, ['CMS', 'INTERACTIVE']),
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.ContentCatalogRestService);

/* @ngInject */ exports.ProductCatalogRestService = class /* @ngInject */ ProductCatalogRestService extends utils.AbstractCachedRestService {
    constructor(restServiceFactory) {
        super(restServiceFactory, '/cmssmarteditwebservices/v1/sites/:siteUID/productcatalogs');
    }
};
/* @ngInject */ exports.ProductCatalogRestService = __decorate([
    utils.CacheConfig({ actions: [rarelyChangingContent], tags: [userEvictionTag] }),
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.ProductCatalogRestService);

/* @ngInject */ exports.PermissionsRestService = class /* @ngInject */ PermissionsRestService {
    constructor(restServiceFactory) {
        this.URI = '/permissionswebservices/v1/permissions/global/search';
        this.resource = restServiceFactory.get(this.URI);
    }
    get(queryData) {
        return this.resource
            .queryByPost({ principalUid: queryData.user }, { permissionNames: queryData.permissionNames })
            .then((data) => ({
            permissions: data.permissions
        }));
    }
};
/* @ngInject */ exports.PermissionsRestService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.PermissionsRestService);

/**
 * This service makes calls to the Global Permissions REST API to check if the current user was
 * granted certain permissions.
 */
/* @ngInject */ exports.AuthorizationService = class /* @ngInject */ AuthorizationService {
    constructor(logService, sessionService, permissionsRestService) {
        this.logService = logService;
        this.sessionService = sessionService;
        this.permissionsRestService = permissionsRestService;
    }
    /**
     * This method checks if the current user is granted the given global permissions.
     *
     * @param permissionNames The list of global permissions to check.
     *
     * @returns True if the user is granted all of the given permissions, false otherwise
     *
     * ### Throws
     *
     * - Will throw an error if the permissionNames array is empty.
     */
    hasGlobalPermissions(permissionNames) {
        if (!permissionNames.length || permissionNames.length < 1) {
            throw new Error('permissionNames must be a non-empty array');
        }
        const onSuccess = (permissions) => this.mergePermissionResults(permissions, permissionNames);
        const onError = () => {
            this.logService.error('AuthorizationService - Failed to determine authorization for the following permissions: ' +
                permissionNames.toString());
            return false;
        };
        return this.getPermissions(permissionNames).then(onSuccess, onError);
    }
    /*
     * This method will look for the result for the given permission name. If found, it is
     * verified that it has been granted. Otherwise, the method will return false.
     */
    getPermissionResult(permissionResults, permissionName) {
        const permission = permissionResults.permissions.find((result) => result.key.toLowerCase() === permissionName.toLowerCase());
        return !!permission && permission.value === 'true';
    }
    /*
     * This method merges permission results. It iterates through the list of permission names that
     * were checked and evaluates if the permission is granted. It immediately returns false when
     * it encounters a permission that is denied.
     */
    mergePermissionResults(permissionResults, permissionNames) {
        let hasPermission = !!permissionNames && permissionNames.length > 0;
        let index = 0;
        while (hasPermission && index < permissionNames.length) {
            hasPermission =
                hasPermission &&
                    this.getPermissionResult(permissionResults, permissionNames[index++]);
        }
        return hasPermission;
    }
    /*
     * This method makes a call to the Global Permissions API with the given permission names
     * and returns the list of results.
     */
    getPermissions(permissionNames) {
        return this.sessionService.getCurrentUsername().then((user) => {
            if (!user) {
                return { permissions: [] };
            }
            return this.permissionsRestService.get({
                user,
                permissionNames: permissionNames.join(',')
            });
        });
    }
};
/* @ngInject */ exports.AuthorizationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService,
        utils.ISessionService,
        exports.PermissionsRestService])
], /* @ngInject */ exports.AuthorizationService);

/**
 * @internal
 * @ignore
 */
/* @ngInject */ exports.CrossFrameEventServiceGateway = class /* @ngInject */ CrossFrameEventServiceGateway {
    constructor(gatewayFactory) {
        return gatewayFactory.createGateway('CROSS_FRAME_EVENT');
    }
};
/* @ngInject */ exports.CrossFrameEventServiceGateway.crossFrameEventServiceGatewayToken = new core.InjectionToken('crossFrameEventServiceGatewayToken');
/* @ngInject */ exports.CrossFrameEventServiceGateway = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.GatewayFactory])
], /* @ngInject */ exports.CrossFrameEventServiceGateway);

window.__smartedit__.addDecoratorPayload("Injectable", "CrossFrameEventService", { providedIn: 'root' });
/**
 * The Cross Frame Event Service is responsible for publishing and subscribing events within and between frames.
 *
 * It uses {@link GatewayFactory} and {@link SystemEventService} to transmit events.
 */
/* @ngInject */ exports.CrossFrameEventService = class /* @ngInject */ CrossFrameEventService {
    constructor(systemEventService, crossFrameEventServiceGateway, windowUtils) {
        this.systemEventService = systemEventService;
        this.crossFrameEventServiceGateway = crossFrameEventServiceGateway;
        this.windowUtils = windowUtils;
    }
    /**
     * Publishes an event within and across the gateway.
     *
     * The publish method is used to send events using [publishAsync]{@link SystemEventService#publishAsync}
     * and as well send the message across the gateway by using [publish]{@link MessageGateway#publish} of the {@link GatewayFactory}.
     */
    publish(eventId, data) {
        const promises = [this.systemEventService.publishAsync(eventId, data)];
        if (this.windowUtils.getGatewayTargetFrame()) {
            promises.push(this.crossFrameEventServiceGateway.publish(eventId, data));
        }
        return Promise.all(promises);
    }
    /**
     * Subscribe to an event across both frames.
     *
     * The subscribe method is used to register for listening to events using subscribe method of
     * {@link SystemEventService} and as well send the registration message across the gateway by using
     * [subscribe]{@link MessageGateway#subscribe} of the {@link GatewayFactory}.
     *
     * @param handler Callback function to be invoked
     * @returns The function to call in order to unsubscribe the event listening.
     * This will unsubscribe both from the systemEventService and the crossFrameEventServiceGatway.
     */
    subscribe(eventId, handler) {
        const systemEventServiceUnsubscribeFn = this.systemEventService.subscribe(eventId, handler);
        const crossFrameEventServiceGatewayUnsubscribeFn = this.crossFrameEventServiceGateway.subscribe(eventId, handler);
        const unsubscribeFn = function () {
            systemEventServiceUnsubscribeFn();
            crossFrameEventServiceGatewayUnsubscribeFn();
        };
        return unsubscribeFn;
    }
    isIframe() {
        return this.windowUtils.isIframe();
    }
};
/* @ngInject */ exports.CrossFrameEventService = __decorate([
    core.Injectable({ providedIn: 'root' }),
    __param(1, core.Inject(exports.CrossFrameEventServiceGateway.crossFrameEventServiceGatewayToken)),
    __metadata("design:paramtypes", [exports.SystemEventService,
        MessageGateway,
        exports.WindowUtils])
], /* @ngInject */ exports.CrossFrameEventService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const GatewayProxiedName = 'GatewayProxied';
const GatewayProxied = utils.annotationService.getClassAnnotationFactory(GatewayProxiedName);
function GatewayProxiedAnnotationFactory(gatewayProxy) {
    'ngInject';
    return utils.annotationService.setClassAnnotationFactory(GatewayProxiedName, function (factoryArguments) {
        return function (instance, originalConstructor, invocationArguments) {
            instance = new (originalConstructor.bind(instance))(...invocationArguments);
            instance.gatewayId = diNameUtils.buildServiceName(originalConstructor);
            gatewayProxy.initForService(instance, factoryArguments.length > 0 ? factoryArguments : null);
            return instance;
        };
    });
}

/* @ngInject */ exports.LanguageService = class /* @ngInject */ LanguageService extends utils.LanguageService {
    constructor(logService, translateService, promiseUtils, eventService, browserService, storageService, injector, languageServiceConstants) {
        super(logService, translateService, promiseUtils, eventService, browserService, storageService, injector, languageServiceConstants);
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
     * Fetches a list of language descriptors for the specified storefront site UID.
     * The object containing the list of sites is fetched using REST calls to the cmswebservices languages API.
     */
    getLanguagesForSite(siteUID) {
        return this.languageRestService
            .get({
            siteUID
        })
            .then((languagesList) => languagesList.languages, (error) => {
            this.logService.error('LanguageService.getLanguagesForSite() - Error loading languages');
            return Promise.reject(error);
        });
    }
    get languageRestService() {
        return this.injector
            .get(utils.RestServiceFactory)
            .get(this.languageServiceConstants.LANGUAGE_RESOURCE_URI);
    }
};
__decorate([
    utils.Cached({ actions: [utils.rarelyChangingContent] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.LanguageService.prototype, "getLanguagesForSite", null);
/* @ngInject */ exports.LanguageService = __decorate([
    core.Injectable(),
    __param(3, core.Inject(utils.EVENT_SERVICE)),
    __param(7, core.Inject(utils.LANGUAGE_SERVICE_CONSTANTS)),
    __metadata("design:paramtypes", [utils.LogService,
        core$1.TranslateService,
        utils.PromiseUtils, Object, utils.BrowserService,
        utils.IStorageService,
        core.Injector, Object])
], /* @ngInject */ exports.LanguageService);

var /* @ngInject */ LanguageServiceGateway_1;
/**
 * @internal
 * @ignore
 */
/* @ngInject */ exports.LanguageServiceGateway = /* @ngInject */ LanguageServiceGateway_1 = class /* @ngInject */ LanguageServiceGateway {
    constructor(gatewayFactory) {
        /* @ngInject */ LanguageServiceGateway_1.instance =
            /* @ngInject */ LanguageServiceGateway_1.instance || gatewayFactory.createGateway('languageSwitch');
    }
    getInstance() {
        return /* @ngInject */ LanguageServiceGateway_1.instance;
    }
};
/* @ngInject */ exports.LanguageServiceGateway = /* @ngInject */ LanguageServiceGateway_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.GatewayFactory])
], /* @ngInject */ exports.LanguageServiceGateway);

class IPerspectiveService {
    /**
     * This method registers a perspective.
     * When an end user selects a perspective in the SmartEdit web application,
     * all features bound to the perspective will be enabled when their respective enablingCallback functions are invoked
     * and all features not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     */
    register(configuration) {
        'proxyFunction';
        return null;
    }
    /**
     * This method activates a perspective identified by its key and deactivates the currently active perspective.
     * Activating a perspective consists in activating any feature that is bound to the perspective
     * or any feature that is bound to the perspective's referenced perspectives and deactivating any features
     * that are not bound to the perspective or to its referenced perspectives.
     * After the perspective is changed, the `seConstantsModule.EVENT_PERSPECTIVE_CHANGED`
     * event is published on the {@link CrossFrameEventService}, with no data.
     *
     * @param key The key that uniquely identifies the perspective to be activated. This is the same key as the key used in the [register]{@link IPerspectiveService#register} method.
     */
    switchTo(key) {
        'proxyFunction';
        return null;
    }
    /**
     * This method returns true if a perspective is selected.
     */
    hasActivePerspective() {
        'proxyFunction';
        return null;
    }
    /**
     * This method switches the currently-selected perspective to the default perspective.
     * It will also disable all features for the default perspective before enabling them all back.
     * If no value has been stored in the smartedit-perspectives cookie, the value of the default perspective is se.none.
     * If a value is stored in the cookie, that value is used as the default perspective.
     */
    selectDefault() {
        'proxyFunction';
        return null;
    }
    /**
     * This method returns true if the current active perspective is the Preview mode (No active overlay).
     *
     * @returns A promise with the boolean flag that indicates if the current perspective is the Preview mode.
     */
    isEmptyPerspectiveActive() {
        'proxyFunction';
        return null;
    }
    /**
     * This method is used to refresh the prespective.
     * If there is an exising perspective set then it is refreshed by replaying all the features associated to the current perspective.
     * If there is no perspective set or if the perspective is not permitted then we set the default perspective.
     */
    refreshPerspective() {
        'proxyFunction';
        return null;
    }
    /**
     * This method returns the key of the perspective that is currently loaded.
     *
     * @returns A promise that resolves to the key of the current perspective loaded in the storefront, null otherwise.
     */
    getActivePerspectiveKey() {
        'proxyFunction';
        return null;
    }
    /**
     * This method returns true if the active perspective has the hotkey enabled
     */
    isHotkeyEnabledForActivePerspective() {
        'proxyFunction';
        return null;
    }
    getActivePerspective() {
        'proxyFunction';
        return null;
    }
    getPerspectives() {
        'proxyFunction';
        return null;
    }
}

var /* @ngInject */ GenericEditorStackService_1;
/* @ngInject */ exports.GenericEditorStackService = /* @ngInject */ GenericEditorStackService_1 = class /* @ngInject */ GenericEditorStackService {
    constructor(systemEventService, logService) {
        this.systemEventService = systemEventService;
        this.logService = logService;
        this._editorsStacks = {};
        this.systemEventService.subscribe(/* @ngInject */ GenericEditorStackService_1.EDITOR_PUSH_TO_STACK_EVENT, this.pushEditorEventHandler.bind(this));
        this.systemEventService.subscribe(/* @ngInject */ GenericEditorStackService_1.EDITOR_POP_FROM_STACK_EVENT, this.popEditorEventHandler.bind(this));
    }
    // --------------------------------------------------------------------------------------
    // API
    // --------------------------------------------------------------------------------------
    isAnyGenericEditorOpened() {
        return lodash__namespace.size(this._editorsStacks) >= 1;
    }
    areMultipleGenericEditorsOpened() {
        return (lodash__namespace.size(this._editorsStacks) > 1 ||
            lodash__namespace.some(this._editorsStacks, (stack) => stack.length > 1));
    }
    getEditorsStack(editorStackId) {
        return this._editorsStacks[editorStackId] || null;
    }
    isTopEditorInStack(editorStackId, editorId) {
        let result = false;
        const stack = this._editorsStacks[editorStackId];
        if (stack) {
            const topEditor = stack[stack.length - 1];
            result = topEditor && topEditor.editorId === editorId;
        }
        return result;
    }
    // --------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------
    pushEditorEventHandler(eventId, editorToPushInfo) {
        this.validateId(editorToPushInfo);
        const stackId = editorToPushInfo.editorStackId;
        if (!this._editorsStacks[stackId]) {
            this._editorsStacks[stackId] = [];
        }
        this._editorsStacks[stackId].push({
            component: editorToPushInfo.component,
            componentType: editorToPushInfo.componentType,
            editorId: editorToPushInfo.editorId
        });
    }
    popEditorEventHandler(eventId, editorToPopInfo) {
        this.validateId(editorToPopInfo);
        const stackId = editorToPopInfo.editorStackId;
        const stack = this._editorsStacks[stackId];
        if (!stack) {
            this.logService.warn('genericEditorStackService - Stack of editors not found. Cannot pop editor.');
            return;
        }
        stack.pop();
        if (stack.length === 0) {
            delete this._editorsStacks[stackId];
        }
    }
    validateId(editorInfo) {
        if (!editorInfo.editorStackId) {
            throw new Error('genericEditorStackService - Must provide a stack id.');
        }
    }
};
/* @ngInject */ exports.GenericEditorStackService.EDITOR_PUSH_TO_STACK_EVENT = 'EDITOR_PUSH_TO_STACK_EVENT';
/* @ngInject */ exports.GenericEditorStackService.EDITOR_POP_FROM_STACK_EVENT = 'EDITOR_POP_FROM_STACK_EVENT';
/* @ngInject */ exports.GenericEditorStackService = /* @ngInject */ GenericEditorStackService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.SystemEventService, utils.LogService])
], /* @ngInject */ exports.GenericEditorStackService);

/**
 * Used for HTTP error code 400. It removes all errors of type 'ValidationError' and displays alert messages for non-validation errors.
 */
/* @ngInject */ exports.NonValidationErrorInterceptor = class /* @ngInject */ NonValidationErrorInterceptor {
    constructor(alertService, genericEditorStackService) {
        this.alertService = alertService;
        this.genericEditorStackService = genericEditorStackService;
    }
    predicate(request, response) {
        return response.status === 400;
    }
    responseError(request, response) {
        if (response.error && response.error.errors) {
            response.error.errors
                .filter((error) => {
                const isValidationError = error.type === 'ValidationError';
                return (!isValidationError ||
                    (isValidationError &&
                        !this.genericEditorStackService.isAnyGenericEditorOpened()));
            })
                .forEach((error) => {
                this.alertService.showDanger({
                    message: error.message || 'se.unknown.request.error',
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
/* @ngInject */ exports.NonValidationErrorInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.IAlertService,
        exports.GenericEditorStackService])
], /* @ngInject */ exports.NonValidationErrorInterceptor);

/**
 * Used for HTTP error code 400 from the Preview API when the pageId is not found in the context. The request will
 * be replayed without the pageId.
 *
 * This can happen in a few different scenarios. For instance, you are on electronics catalog, on some custom page called XYZ.
 * If you use the experience selector and switch to apparel catalog, it will try to create a new preview ticket
 * with apparel catalog and pageId of XYZ. Since XYZ doesn't exist in apparel, it will fail. So we remove the page ID
 * and create a preview for homepage as a default/fallback.
 */
/* @ngInject */ exports.PreviewErrorInterceptor = class /* @ngInject */ PreviewErrorInterceptor {
    constructor(injector, logService, sharedDataService) {
        this.injector = injector;
        this.logService = logService;
        this.sharedDataService = sharedDataService;
    }
    predicate(request, response) {
        return (response.status === 400 &&
            request.url.indexOf(PREVIEW_RESOURCE_URI) > -1 &&
            !utils.stringUtils.isBlank(request.body.pageId) &&
            this._hasUnknownIdentifierError(response.error.errors));
    }
    responseError(request, response) {
        this.logService.info('The error 400 above on preview is expected in some scenarios, typically when switching catalogs from experience selector.');
        this.logService.info('Removing the pageId [' +
            request.body.pageId +
            '] and creating a preview for homepage');
        delete request.body.pageId;
        this.sharedDataService.update(EXPERIENCE_STORAGE_KEY, function (experience) {
            delete experience.pageId;
            return experience;
        });
        this.injector.get('iframeManagerService').setCurrentLocation(null);
        return this.injector.get(http.HttpClient).request(request).toPromise();
    }
    _hasUnknownIdentifierError(errors) {
        const unknownIdentifierErrors = errors.filter(function (error) {
            return error.type === 'UnknownIdentifierError';
        });
        return !!unknownIdentifierErrors.length;
    }
};
/* @ngInject */ exports.PreviewErrorInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core.Injector,
        utils.LogService,
        utils.ISharedDataService])
], /* @ngInject */ exports.PreviewErrorInterceptor);

/**
 * Used for HTTP error code 404 (Not Found) except for an HTML or a language resource. It will display the response.message in an alert message.
 */
/* @ngInject */ exports.ResourceNotFoundErrorInterceptor = class /* @ngInject */ ResourceNotFoundErrorInterceptor {
    constructor(alertService, httpUtils) {
        this.alertService = alertService;
        this.httpUtils = httpUtils;
    }
    predicate(request, response) {
        return (response.status === 404 &&
            !this.httpUtils.isHTMLRequest(request, response) &&
            !this._isLanguageResourceRequest(request.url));
    }
    responseError(request, response) {
        this.alertService.showDanger({
            message: response.statusText === utils.StatusText.UNKNOW_ERROR
                ? 'se.unknown.request.error'
                : response.message,
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        return Promise.reject(response);
    }
    _isLanguageResourceRequest(url) {
        const languageResourceRegex = new RegExp(LANGUAGE_RESOURCE_URI.replace(/\:.*\//g, '.*/'));
        return languageResourceRegex.test(url);
    }
};
/* @ngInject */ exports.ResourceNotFoundErrorInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.IAlertService,
        utils.HttpUtils])
], /* @ngInject */ exports.ResourceNotFoundErrorInterceptor);

function operationContextInteractivePredicate(response, operationContext) {
    return operationContext === OPERATION_CONTEXT.INTERACTIVE;
}
function operationContextNonInteractivePredicate(response, operationContext) {
    return lodash__namespace.includes([
        OPERATION_CONTEXT.BACKGROUND_TASKS,
        OPERATION_CONTEXT.NON_INTERACTIVE,
        OPERATION_CONTEXT.BATCH_OPERATIONS
    ], operationContext);
}
function operationContextCMSPredicate(response, operationContext) {
    return operationContext === OPERATION_CONTEXT.CMS;
}
function operationContextToolingPredicate(response, operationContext) {
    return operationContext === OPERATION_CONTEXT.TOOLING;
}

/* @ngInject */ exports.InterceptorHelper = class /* @ngInject */ InterceptorHelper {
    constructor(logService) {
        this.logService = logService;
    }
    handleRequest(config, callback) {
        return this._handle(config, config, callback, false);
    }
    handleResponse(response, callback) {
        return this._handle(response, response.config, callback, false);
    }
    handleResponseError(response, callback) {
        return this._handle(response, response.config, callback, true);
    }
    _isEligibleForInterceptors(config) {
        return config && config.url && !/.+\.html$/.test(config.url);
    }
    _handle(chain, config, callback, isError) {
        try {
            if (this._isEligibleForInterceptors(config)) {
                return callback();
            }
            else {
                if (isError) {
                    return Promise.reject(chain);
                }
                else {
                    return chain;
                }
            }
        }
        catch (e) {
            this.logService.error('caught error in one of the interceptors', e);
            if (isError) {
                return Promise.reject(chain);
            }
            else {
                return chain;
            }
        }
    }
};
/* @ngInject */ exports.InterceptorHelper = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService])
], /* @ngInject */ exports.InterceptorHelper);

/* @internal */
/* @ngInject */ exports.PolyfillService = class /* @ngInject */ PolyfillService {
    constructor(browserService) {
        this.browserService = browserService;
    }
    isEligibleForEconomyMode() {
        return this.browserService.isIE();
    }
    isEligibleForExtendedView() {
        return this.browserService.isIE() || this.browserService.isFF();
    }
    isEligibleForThrottledScrolling() {
        return this.browserService.isIE();
    }
};
/* @ngInject */ exports.PolyfillService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.BrowserService])
], /* @ngInject */ exports.PolyfillService);

/**
 * The PriorityService handles arrays of {@link IPrioritized} elements.
 */
/* @ngInject */ exports.PriorityService = class /* @ngInject */ PriorityService {
    constructor(stringUtils) {
        this.stringUtils = stringUtils;
    }
    /**
     * Will sort the candidate array by ascendign or descending priority.
     * Even if the priority is not defined for a number of elements, the sorting will still be consistent over invocations
     * @param candidate Elements to be sorted
     * @param ascending If true, candidate will be sorted by ascending priority.
     * @returns The sorted candidate array.
     */
    sort(candidate, ascending = true) {
        return candidate.sort((item1, item2) => {
            let output = item1.priority - item2.priority;
            if (output === 0) {
                output = this.stringUtils
                    .encode(item1)
                    .localeCompare(this.stringUtils.encode(item2));
            }
            return output;
        });
    }
};
/* @ngInject */ exports.PriorityService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.StringUtils])
], /* @ngInject */ exports.PriorityService);

/* @ngInject */ exports.SmarteditBootstrapGateway = class /* @ngInject */ SmarteditBootstrapGateway {
    constructor(gatewayFactory) {
        this.instance = this.instance || gatewayFactory.createGateway('smartEditBootstrap');
    }
    getInstance() {
        return this.instance;
    }
    subscribe(eventId, callback) {
        return this.getInstance().subscribe(eventId, callback);
    }
    publish(eventId, _data, retries = 0, pk) {
        return this.getInstance().publish(eventId, _data, retries, pk);
    }
    processEvent(event) {
        return this.getInstance().processEvent(event);
    }
};
/* @ngInject */ exports.SmarteditBootstrapGateway = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.GatewayFactory])
], /* @ngInject */ exports.SmarteditBootstrapGateway);

exports.NavigationEventSource = void 0;
(function (NavigationEventSource) {
    NavigationEventSource["SRC_URL"] = "/";
})(exports.NavigationEventSource || (exports.NavigationEventSource = {}));

/**
 * A service that provides navigation and URL manipulation capabilities.
 * It is a reliant source of information on routing state in Smartedit.
 */
exports.SmarteditRoutingService = class SmarteditRoutingService {
    constructor(router, document, logService) {
        this.router = router;
        this.document = document;
        this.logService = logService;
        this.listenersInitialized = false;
        this.routeChangeError$ = new rxjs.ReplaySubject();
        this.routeChangeStart$ = new rxjs.ReplaySubject();
        this.routeChangeSuccess$ = new rxjs.ReplaySubject();
        this.previousRouterUrl = '';
    }
    /**
     *  Initializes listeners for navigation events.
     */
    init() {
        if (!this.listenersInitialized) {
            this.notifyOnAngularRouteEvents();
            this.listenersInitialized = true;
        }
    }
    /** Navigates based on the provided URL (absolute). */
    go(url) {
        return this.router.navigateByUrl(url);
    }
    /** Returns the current router URL. */
    path() {
        return this.router.url;
    }
    /** Returns absolute URL. */
    absUrl() {
        return this.document.location.href;
    }
    /** Notifies when the route change has started. */
    routeChangeStart() {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeStart$.pipe(operators.filter((event) => !!event.url && event.url !== this.previousRouterUrl), operators.map((event) => event.routeData));
    }
    /** Notifies when the route change has ended. */
    routeChangeSuccess() {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeSuccess$.pipe(operators.filter((event) => !!event.url && event.url !== this.previousRouterUrl), operators.map((event) => event.routeData));
    }
    /** Notifies when the route change has failed. */
    routeChangeError() {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeError$.pipe(operators.map((event) => event.routeData));
    }
    /** Reloads the given URL. If not provided, it will reload the current URL.
     * Add a fake route '/nulldummy' to improve the reload speed
     */
    reload(url = this.router.url) {
        return this.router
            .navigateByUrl('/nulldummy', { skipLocationChange: true })
            .then(() => this.router.navigateByUrl(url));
    }
    /**
     * Extracts `url` from Angular router event
     */
    getCurrentUrlFromEvent(event) {
        if (event instanceof router.NavigationStart ||
            event instanceof router.NavigationEnd ||
            event instanceof router.NavigationError) {
            return event.url;
        }
        return null;
    }
    /**
     * @internal
     * @ignore
     */
    warnAboutListenersNotInitialized() {
        if (!this.listenersInitialized) {
            this.logService.warn('Listeners not initialized, run `init()` first.');
        }
    }
    /**
     * @internal
     * @ignore
     */
    notifyOnAngularRouteEvents() {
        this.router.events.subscribe((event) => {
            // if the event url is equal to null
            if (!event.url) {
                return;
            }
            switch (true) {
                case event instanceof router.NavigationStart: {
                    this.routeChangeStart$.next({
                        from: exports.NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event
                    });
                    break;
                }
                case event instanceof router.NavigationError: {
                    this.routeChangeError$.next({
                        from: exports.NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event
                    });
                    break;
                }
                case event instanceof router.NavigationEnd: {
                    this.routeChangeSuccess$.next({
                        from: exports.NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event
                    });
                    this.previousRouterUrl = event && event.url;
                    break;
                }
            }
        });
    }
};
exports.SmarteditRoutingService = __decorate([
    __param(0, core.Optional()),
    __param(1, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [router.Router,
        Document,
        utils.LogService])
], exports.SmarteditRoutingService);

/* @ngInject */ exports.AuthenticationManager = class /* @ngInject */ AuthenticationManager extends utils.IAuthenticationManagerService {
    constructor(routing) {
        super();
        this.routing = routing;
    }
    onLogout() {
        const currentLocation = this.routing.path();
        if (utils.stringUtils.isBlank(currentLocation) || currentLocation === '/') {
            this.routing.reload();
        }
        else {
            this.routing.go('/');
        }
    }
    onUserHasChanged() {
        this.routing.reload();
    }
};
/* @ngInject */ exports.AuthenticationManager = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.SmarteditRoutingService])
], /* @ngInject */ exports.AuthenticationManager);

/*
 * Meant to be a non-protected API
 */
/* @ngInject */ exports.SettingsService = class /* @ngInject */ SettingsService {
    constructor(restServicefactory) {
        this.restService = restServicefactory.get(SETTINGS_URI);
    }
    load() {
        return this.restService.get();
    }
    get(key) {
        return this.load().then((map) => map[key]);
    }
    getBoolean(key) {
        return this.load().then((map) => map[key] === true || map[key] === 'true');
    }
    getStringList(key) {
        return this.load().then((map) => map[key]);
    }
};
__decorate([
    utils.Cached({ actions: [rarelyChangingContent] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.SettingsService.prototype, "load", null);
/* @ngInject */ exports.SettingsService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.SettingsService);

/**
 * The SeRouteService is Angular service that allows to add Angular routes to the application.
 * It also collects information about each route to build route related shortcut links.
 */
class SeRouteService {
    /**
     * Adds new Angular route to the application. For more information please see documentation for RouterModule.forRoot.
     * @returns A wrapper around an NgModule that associates it with the providers.
     */
    static provideNgRoute(routes, config) {
        this.provideRouteShortcutConfigs(routes);
        return router.RouterModule.forRoot(routes, config);
    }
    /**
     * Returns a list of all shortcut configs.
     */
    static get routeShortcutConfigs() {
        return this.routeShortcuts;
    }
    /**
     * Populates the route shortcut list. It filters route shortcuts that cannot be used
     * as shortcuts.
     */
    static provideRouteShortcutConfigs(routes) {
        const routeShortcutConfigs = [];
        this.generateRouteShortcutConfig(routeShortcutConfigs, routes);
        routeShortcutConfigs
            .filter((routeShortcutConfig) => this.canRegisterRouteShortcutConfig(routeShortcutConfig))
            .forEach((routeShortcutConfig) => {
            this.routeShortcuts.push(routeShortcutConfig);
        });
    }
    /**
     * Recursively reads the list of routes and calculates the full path for each route.
     * Then populates the shortcut configs array with calculated data.
     */
    static generateRouteShortcutConfig(configs, routes, parent = '') {
        routes.forEach((route) => {
            if (route.path !== undefined) {
                const fullPath = this.getFullPath(parent, route);
                configs.push({
                    fullPath,
                    titleI18nKey: route.titleI18nKey,
                    priority: route.priority,
                    shortcutComponent: route.shortcutComponent
                });
            }
            if (route.children) {
                const fullPath = this.getFullPath(parent, route);
                const currentPath = route.path ? fullPath : parent;
                this.generateRouteShortcutConfig(configs, route.children, currentPath);
            }
        });
    }
    /**
     * Returns the full path concatenating parent route with current one.
     */
    static getFullPath(parent, route) {
        return parent ? parent + '/' + route.path : route.path;
    }
    /**
     * Validates whether the route shortcut config can be registered.
     * It's not registered if:
     * - the fullPath is not provided
     * - the fullPath contains placeholders that are not in CATALOG_AWARE_PATH_PLACEHOLDERS list.
     */
    static canRegisterRouteShortcutConfig(routeShortcutConfig) {
        if (routeShortcutConfig.fullPath === undefined) {
            return false;
        }
        let fullPath = routeShortcutConfig.fullPath;
        this.CATALOG_AWARE_PATH_PLACEHOLDERS.forEach((placeholder) => {
            fullPath = fullPath.replace(placeholder, '');
        });
        if (fullPath.indexOf(':') > -1) {
            return false;
        }
        return true;
    }
}
SeRouteService.CATALOG_AWARE_PATH_PLACEHOLDERS = [':siteId', ':catalogId', ':catalogVersion'];
SeRouteService.routeShortcuts = [];

const YJQUERY_TOKEN = 'yjQuery';
/**
 * Return a jQuery wrapping factory while preserving potentially pre-existing jQuery in storefront and SmartEditContainer
 */
function yjQueryServiceFactory() {
    /* forbiddenNameSpaces (window as any):false */
    const namespace = 'smarteditJQuery';
    if (!window[namespace]) {
        if (window.$ && window.$.noConflict) {
            window[namespace] = window.$.noConflict(true);
        }
        else {
            window[namespace] = window.$;
        }
    }
    return window[namespace];
}
/**
 * This module manages the use of the jQuery library.
 * It enables to work with a "noConflict" version of jQuery in a storefront that may contain another version.
 */
exports.YjqueryModule = class YjqueryModule {
};
exports.YjqueryModule = __decorate([
    core.NgModule({
        providers: [
            {
                provide: YJQUERY_TOKEN,
                useFactory: yjQueryServiceFactory
            },
            moduleUtils.initialize((yjQuery) => {
                yjQuery.fn.extend({
                    getCssPath() {
                        let path;
                        let node = this;
                        while (node.length) {
                            const realNode = node[0];
                            const name = realNode.className;
                            if (realNode.tagName === 'BODY') {
                                break;
                            }
                            node = node.parent();
                            path = name + (path ? '>' + path : '');
                        }
                        return path;
                    }
                });
            }, [YJQUERY_TOKEN])
        ]
    })
], exports.YjqueryModule);

const IN_VIEW_ELEMENTS_INTERSECTION_OBSERVER_OPTIONS = {
    // The root to use for intersection.
    // If not provided, use the top-level document’s viewport.
    root: null,
    // Threshold(s) at which to trigger callback, specified as a ratio, or list of
    // ratios, of (visible area / total area) of the observed element (hence all
    // entries must be in the range [0, 1]). Callback will be invoked when the visible
    // ratio of the observed element crosses a threshold in the list.
    threshold: 0
};
/*
 * This is the configuration passed to the MutationObserver instance
 */
const IN_VIEW_ELEMENTS_MUTATION_OBSERVER_OPTIONS = {
    /*
     * enables observation of attribute mutations precisely for class="smartEditComponent" that may be added dynamically
     * turned to true dynamically if at least of the selectors is class sensitive
     */
    attributes: true,
    /*
     * instruct the observer not to keep in store the former values of the mutated attributes
     */
    attributeOldValue: false,
    /*
     * enables observation of addition and removal of nodes
     */
    childList: true,
    characterData: false,
    /*
     * enables recursive lookup without which only addition and removal of DIRECT children of the observed DOM root would be collected
     */
    subtree: true
};
/**
 * InViewElementObserver maintains a collection of eligible DOM elements considered "in view".
 * An element is considered eligible if matches at least one of the selectors passed to the service.
 * An eligible element is in view when and only when it intersects with the view port of the window frame.
 * This services provides as well convenience methods around "in view" components:
 */
/* @ngInject */ exports.InViewElementObserver = class /* @ngInject */ InViewElementObserver {
    constructor(logService, document, yjQuery) {
        this.logService = logService;
        this.document = document;
        this.yjQuery = yjQuery;
        /*
         * Queue used to process components when intersecting the viewport
         * {Array.<{isIntersecting: Boolean, parent: DOMElement, processed: COMPONENT_STATE}>}
         */
        this.componentsQueue = [];
        this.selectors = [];
        this.hasClassBasedSelectors = false;
    }
    /**
     * Retrieves the element targeted by the given mousePosition.
     * On some browsers, the native Javascript API will not work when targeting
     * an element inside an iframe from the container if a container overlay blocks it.
     * In such case we resort to returning the targeted element amongst the list of "in view" elements
     */
    elementFromPoint(mousePosition) {
        const elementFromPointThroughNativeAPI = this.document.elementFromPoint(mousePosition.x, mousePosition.y);
        // we might potentially have an issue here if a browser has an intersection observer and document.elementFromPoint returns null.
        // Chrome version 66 when running in isE2EMode has the issue. But this is not likely to happen in reality because Chrome has an intersectionObserver
        // and hence it should just use the result from document.elementFromPoint.
        return (elementFromPointThroughNativeAPI ||
            this.getInViewElements().find((component) => nodeUtils.isPointOverElement(mousePosition, component)));
    }
    /**
     * Declares a new yjQuery selector in order to observe more elements.
     */
    addSelector(selector, callback) {
        if (!stringUtils.isBlank(selector) &&
            this.selectors.map((el) => el.selector).indexOf(selector) === -1) {
            if (/\.[\w]+/.test(selector)) {
                this.hasClassBasedSelectors = true;
            }
            this.selectors.push({ selector, callback });
            this.restart();
        }
        return () => {
            const index = this.selectors.map((el) => el.selector).indexOf(selector);
            this.selectors.splice(index, 1);
            this.restart();
        };
    }
    /**
     * Retrieves the full list of eligible DOM elements even if they are not "in view".
     */
    getAllElements() {
        return this.componentsQueue.map((element) => element.component);
    }
    /**
     * Retrieves the list of currently "in view" DOM elements.
     */
    getInViewElements() {
        return this.componentsQueue
            .filter((element) => element.isIntersecting)
            .map((element) => element.component);
    }
    restart() {
        this.stopListener();
        this.initListener();
    }
    /*
     * stops and clean up all listeners
     */
    stopListener() {
        // Stop listening for DOM mutations
        if (this.mutationObserver) {
            this.mutationObserver.disconnect();
            delete this.mutationObserver;
        }
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
            delete this.intersectionObserver;
        }
        this.componentsQueue = [];
    }
    /*
     * initializes and starts all Intersection/DOM listeners:
     * - Intersection of eligible components with the viewport
     * - DOM mutations on eligible components (by Means of native MutationObserver)
     */
    initListener() {
        if (!this.mutationObserver) {
            this.mutationObserver = this._newMutationObserver(this._mutationObserverCallback.bind(this));
            if (!this.intersectionObserver) {
                // Intersection Observer is used to observe intersection of components with the viewport.
                // each time the 'isIntersecting' property of an entry changes, the Intersection Callback is called.
                // we are using the componentsQueue to hold the components references and their isIntersecting value.
                this.intersectionObserver = this._newIntersectionObserver((entries) => {
                    const eligibleEntries = entries.filter((entry) => this._isEligibleComponent(entry.target));
                    eligibleEntries.forEach((entry) => {
                        this._updateQueue(entry);
                    });
                    /*
                     * for each added selector, if at least one of the entries is a match, we call the assocated callback
                     *
                     */
                    this.selectors
                        .filter((element) => !!element.callback)
                        .forEach((element) => {
                        if (eligibleEntries.find((entry) => this.yjQuery(entry.target).is(element.selector))) {
                            element.callback();
                        }
                    });
                });
            }
            // Observing all eligible components that are already in the page.
            // Note that when an element visible in the viewport is removed, the Intersection Callback is called so we don't need to use the Mutation Observe to oberser removal of Nodes.
            this._getEligibleElements().forEach((component) => {
                this.intersectionObserver.observe(component);
            });
        }
    }
    /*
     * Method used in mutationObserverCallback that extracts from mutations the list of added and removed nodes
     */
    _aggregateAddedOrRemovedNodes(mutations, addedOnes) {
        const entries = lodash__namespace.flatten(mutations
            .filter((mutation) => 
        // only keep mutations of type childList and addedNodes
        mutation.type === MUTATION_TYPES.CHILD_LIST.NAME &&
            ((!!addedOnes && mutation.addedNodes && mutation.addedNodes.length) ||
                (!addedOnes && mutation.removedNodes && mutation.removedNodes.length)))
            .map((mutation) => {
            const children = lodash__namespace
                .flatten(Array.prototype.slice
                .call(addedOnes ? mutation.addedNodes : mutation.removedNodes)
                .filter((node) => node.nodeType === Node.ELEMENT_NODE)
                .map((child) => {
                const eligibleChildren = this._getAllEligibleChildren(child);
                return this._isEligibleComponent(child)
                    ? [child].concat(eligibleChildren)
                    : eligibleChildren;
            }))
                .sort(nodeUtils.compareHTMLElementsPosition())
                // so that in case of nested eligible components the deeper element is picked
                .reverse();
            return children;
        }));
        /*
         * Despite MutationObserver specifications it so happens that sometimes,
         * depending on the very way a parent node is added with its children,
         * parent AND children will appear in a same mutation. We then must only keep the parent
         * Since the parent will appear first, the filtering lodash.uniqWith will always return the parent as opposed to the child which is what we need
         */
        return lodash__namespace.uniqWith(entries, (entry1, entry2) => entry1.contains(entry2) || entry2.contains(entry1));
    }
    /*
     * Method used in mutationObserverCallback that extracts from mutations the list of nodes that have a mutation in the class attribute value
     */
    _aggregateMutationsOnClass(mutations, addedOnes) {
        return lodash__namespace.compact(mutations
            .filter((mutation) => mutation.target &&
            mutation.target.nodeType === Node.ELEMENT_NODE &&
            mutation.type === MUTATION_TYPES.ATTRIBUTES.NAME &&
            mutation.attributeName === 'class')
            .map((mutation) => {
            if (addedOnes && this._isEligibleComponent(mutation.target)) {
                return mutation.target;
            }
            else if (!addedOnes &&
                this._isEligibleComponent(mutation.target)) {
                return mutation.target;
            }
            return null;
        }));
    }
    /*
     * callback executed by the mutation observer every time mutations occur.
     * repositioning and resizing are not part of this except that every time a eligible component is added,
     * it is registered within the positionRegistry and the resizeListener
     */
    _mutationObserverCallback(mutations) {
        this.logService.debug(mutations);
        this._aggregateAddedOrRemovedNodes(mutations, true).forEach((node) => {
            this.intersectionObserver.observe(node);
        });
        if (this.hasClassBasedSelectors) {
            this._aggregateMutationsOnClass(mutations, true).forEach((node) => {
                this.intersectionObserver.observe(node);
            });
        }
        this._aggregateAddedOrRemovedNodes(mutations, false).forEach((node) => {
            const componentIndex = this._getComponentIndexInQueue(node);
            if (componentIndex !== -1) {
                this.componentsQueue.splice(componentIndex, 1);
            }
        });
        if (this.hasClassBasedSelectors) {
            this._aggregateMutationsOnClass(mutations, false).forEach((node) => {
                const componentIndex = this._getComponentIndexInQueue(node);
                if (componentIndex !== -1) {
                    this.componentsQueue.splice(componentIndex, 1);
                }
            });
        }
    }
    /*
     * Add the given entry to the componentsQueue
     * The components in the queue are sorted according to their position in the DOM
     * so that the adding of components is done to have parents before children
     */
    _updateQueue(entry) {
        const componentIndex = this._getComponentIndexInQueue(entry.target);
        if (componentIndex !== -1) {
            if (!entry.intersectionRatio && !this._isInDOM(entry.target)) {
                this.componentsQueue.splice(componentIndex, 1);
            }
            else {
                this.componentsQueue[componentIndex].isIntersecting = !!entry.intersectionRatio;
            }
        }
        else if (this._isInDOM(entry.target)) {
            // may have been removed by competing MutationObserver hence showign here but not intersecting
            this.componentsQueue.push({
                component: entry.target,
                isIntersecting: !!entry.intersectionRatio
            });
        }
    }
    /// ///////////////////////////////////////////////////////////////////////////////////
    /// ///////////////////////////// HELPER METHODS //////////////////////////////////////
    /// ///////////////////////////////////////////////////////////////////////////////////
    /*
     * wrapping for test purposes
     */
    _newMutationObserver(callback) {
        const mutationObserver = new MutationObserver(callback);
        mutationObserver.observe(this.document.body, lodash__namespace.merge(lodash__namespace.cloneDeep(IN_VIEW_ELEMENTS_MUTATION_OBSERVER_OPTIONS), {
            attributes: this.hasClassBasedSelectors
        }));
        return mutationObserver;
    }
    /*
     * wrapping for test purposes
     */
    _newIntersectionObserver(callback) {
        return new IntersectionObserver(callback, IN_VIEW_ELEMENTS_INTERSECTION_OBSERVER_OPTIONS);
    }
    _getJQuerySelector() {
        return this.selectors.map((el) => el.selector).join(',');
    }
    _isEligibleComponent(component) {
        return this.yjQuery(component).is(this._getJQuerySelector());
    }
    _getEligibleElements() {
        return Array.prototype.slice.call(this.yjQuery(this._getJQuerySelector()));
    }
    _getAllEligibleChildren(component) {
        return Array.prototype.slice.call(component.querySelectorAll(this._getJQuerySelector()));
    }
    _getComponentIndexInQueue(component) {
        return this.componentsQueue.findIndex(function (obj) {
            return component === obj.component;
        });
    }
    _isInDOM(component) {
        return this.yjQuery.contains(this.document.body, component);
    }
};
/* @ngInject */ exports.InViewElementObserver = __decorate([
    core.Injectable(),
    __param(1, core.Inject(common.DOCUMENT)),
    __param(2, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [utils.LogService,
        Document, Function])
], /* @ngInject */ exports.InViewElementObserver);

var /* @ngInject */ DragAndDropScrollingService_1;
/**
 * @internal
 * @ignore
 */
/* @ngInject */ exports.DragAndDropScrollingService = /* @ngInject */ DragAndDropScrollingService_1 = class /* @ngInject */ DragAndDropScrollingService {
    constructor(windowUtils, translate, inViewElementObserver, yjQuery) {
        this.windowUtils = windowUtils;
        this.translate = translate;
        this.inViewElementObserver = inViewElementObserver;
        this.yjQuery = yjQuery;
        this.SCROLLING_AREA_HEIGHT = 50;
        this.FAST_SCROLLING_AREA_HEIGHT = 25;
        this.SCROLLING_STEP = 5;
        this.FAST_SCROLLING_STEP = 15;
        this.topScrollArea = null;
        this.bottomScrollArea = null;
        this.throttleScrollingEnabled = false;
        this.inViewElementObserver.addSelector('#' + /* @ngInject */ DragAndDropScrollingService_1.TOP_SCROLL_AREA_ID);
        this.inViewElementObserver.addSelector('#' + /* @ngInject */ DragAndDropScrollingService_1.BOTTOM_SCROLL_AREA_ID);
        this.throttledScrollPage = lodash__namespace.throttle(this.scrollPage.bind(this), THROTTLE_SCROLLING_DELAY);
    }
    initialize() {
        this.scrollable = this.getFromElementSelector(this.windowUtils.getWindow().document.scrollingElement);
        this.addScrollAreas();
        this.addEventListeners();
        this.scrollDelta = 0;
        this.initialized = true;
    }
    deactivate() {
        this.removeEventListeners();
        this.scrollDelta = 0;
        this.initialized = false;
    }
    enable() {
        if (this.initialized) {
            // Calculate limits based on current state.
            this.scrollLimitY =
                this.scrollable.get(0).scrollHeight - this.windowUtils.getWindow().innerHeight;
            this.showScrollAreas();
        }
    }
    disable() {
        if (this.initialized) {
            const scrollAreas = this.getScrollAreas();
            // following trigger necessary to remove scrollable areas when loosing track of the mouse from the outer layer
            scrollAreas.trigger('dragleave');
            scrollAreas.hide();
        }
    }
    toggleThrottling(isEnabled) {
        this.throttleScrollingEnabled = isEnabled;
    }
    addScrollAreas() {
        this.topScrollArea = this.getFromSelector('<div id="' +
            /* @ngInject */ DragAndDropScrollingService_1.TOP_SCROLL_AREA_ID +
            '" class="' +
            SCROLL_AREA_CLASS +
            '"></div>').appendTo('body');
        this.bottomScrollArea = this.getFromSelector('<div id="' +
            /* @ngInject */ DragAndDropScrollingService_1.BOTTOM_SCROLL_AREA_ID +
            '" class="' +
            SCROLL_AREA_CLASS +
            '"></div>').appendTo('body');
        const scrollAreas = this.getScrollAreas();
        scrollAreas.height(this.SCROLLING_AREA_HEIGHT);
        this.topScrollArea.css({
            top: 0
        });
        this.bottomScrollArea.css({
            bottom: 0
        });
        scrollAreas.hide();
        let topMessage;
        let bottomMessage;
        return this.translate
            .get('se.draganddrop.uihint.top')
            .toPromise()
            .then((localizedTopMessage) => {
            topMessage = localizedTopMessage;
            return this.translate.get('se.draganddrop.uihint.bottom').toPromise();
        })
            .then((localizedBottomMsg) => {
            bottomMessage = localizedBottomMsg;
            this.topScrollArea.text(topMessage);
            this.bottomScrollArea.text(bottomMessage);
        });
    }
    addEventListeners() {
        const scrollAreas = this.getScrollAreas();
        scrollAreas.on('dragenter', this.onDragEnter.bind(this));
        scrollAreas.on('dragover', this.onDragOver.bind(this));
        scrollAreas.on('dragleave', this.onDragLeave.bind(this));
    }
    removeEventListeners() {
        const scrollAreas = this.getScrollAreas();
        scrollAreas.off('dragenter');
        scrollAreas.off('dragover');
        scrollAreas.off('dragleave');
        scrollAreas.remove();
    }
    // Event Listeners
    onDragEnter(event) {
        let scrollDelta = this.SCROLLING_STEP;
        const scrollArea = this.getFromSelector(event.target);
        const scrollAreaId = scrollArea.attr('id');
        if (scrollAreaId === /* @ngInject */ DragAndDropScrollingService_1.TOP_SCROLL_AREA_ID) {
            scrollDelta *= -1;
        }
        this.scrollDelta = scrollDelta;
        this.animationFrameId = this.windowUtils
            .getWindow()
            .requestAnimationFrame(this.scrollPage.bind(this));
    }
    onDragOver(evt) {
        const event = evt.originalEvent;
        const scrollArea = this.getFromSelector(event.target);
        const scrollAreaId = scrollArea.attr('id');
        if (scrollAreaId === /* @ngInject */ DragAndDropScrollingService_1.TOP_SCROLL_AREA_ID) {
            if (event.clientY <= this.FAST_SCROLLING_AREA_HEIGHT) {
                this.scrollDelta = -this.FAST_SCROLLING_STEP;
            }
            else {
                this.scrollDelta = -this.SCROLLING_STEP;
            }
        }
        else {
            const windowHeight = this.windowUtils.getWindow().innerHeight;
            if (event.clientY >= windowHeight - this.FAST_SCROLLING_AREA_HEIGHT) {
                this.scrollDelta = this.FAST_SCROLLING_STEP;
            }
            else {
                this.scrollDelta = this.SCROLLING_STEP;
            }
        }
    }
    onDragLeave() {
        this.scrollDelta = 0;
        this.windowUtils.getWindow().cancelAnimationFrame(this.animationFrameId);
    }
    scrollPage() {
        if (this.scrollDelta) {
            const scrollTop = this.scrollable.scrollTop();
            let continueScrolling = false;
            if (this.scrollDelta > 0 && scrollTop < this.scrollLimitY) {
                continueScrolling = true;
            }
            else if (this.scrollDelta < 0 && scrollTop > 0) {
                continueScrolling = true;
            }
            if (continueScrolling) {
                const current = this.scrollable.scrollTop();
                const next = current + this.scrollDelta;
                this.scrollable.scrollTop(next);
                this.animationFrameId = this.windowUtils
                    .getWindow()
                    .requestAnimationFrame(this.throttleScrollingEnabled
                    ? this.throttledScrollPage
                    : this.scrollPage.bind(this));
            }
            this.showScrollAreas();
        }
    }
    // Since implementation class for JQueryStatic does not support constructor with multiple dynamic parameter well
    // Here we split into different sub methods to avoid build warning
    getFromSelector(selector) {
        if (typeof selector === 'string') {
            return this.yjQuery(selector);
        }
        else {
            return this.getFromHTMLSelector(selector);
        }
    }
    getFromHTMLSelector(selector) {
        return this.yjQuery(selector);
    }
    getFromElementSelector(selector) {
        return this.yjQuery(selector);
    }
    getScrollAreas() {
        return this.yjQuery([
            '#' + /* @ngInject */ DragAndDropScrollingService_1.TOP_SCROLL_AREA_ID,
            '#' + /* @ngInject */ DragAndDropScrollingService_1.BOTTOM_SCROLL_AREA_ID
        ].join(','));
    }
    showScrollAreas() {
        const scrollTop = this.scrollable.scrollTop();
        if (scrollTop === 0) {
            this.topScrollArea.hide();
        }
        else {
            this.topScrollArea.show();
        }
        if (scrollTop >= this.scrollLimitY) {
            this.bottomScrollArea.hide();
        }
        else {
            this.bottomScrollArea.show();
        }
    }
};
/* @ngInject */ exports.DragAndDropScrollingService.TOP_SCROLL_AREA_ID = 'top_scroll_page';
/* @ngInject */ exports.DragAndDropScrollingService.BOTTOM_SCROLL_AREA_ID = 'bottom_scroll_page';
/* @ngInject */ exports.DragAndDropScrollingService = /* @ngInject */ DragAndDropScrollingService_1 = __decorate([
    core.Injectable(),
    __param(3, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [utils.WindowUtils,
        core$1.TranslateService,
        exports.InViewElementObserver, Function])
], /* @ngInject */ exports.DragAndDropScrollingService);

var /* @ngInject */ DragAndDropService_1;
/* @ngInject */ exports.DragAndDropService = /* @ngInject */ DragAndDropService_1 = class /* @ngInject */ DragAndDropService {
    constructor(yjQuery, dragAndDropScrollingService, inViewElementObserver, systemEventService, dragAndDropCrossOrigin) {
        this.yjQuery = yjQuery;
        this.dragAndDropScrollingService = dragAndDropScrollingService;
        this.inViewElementObserver = inViewElementObserver;
        this.systemEventService = systemEventService;
        this.dragAndDropCrossOrigin = dragAndDropCrossOrigin;
        this.configurations = {};
        this.isDragAndDropExecuting = false;
        this.dragAndDropCrossOrigin.initialize();
    }
    /**
     * This method registers a new instance of the drag and drop service.
     * Note: Registering doesn't start the service. It just provides the configuration, which later must be applied with the apply method.
     *
     */
    register(configuration) {
        // Validate
        if (!configuration || !configuration.id) {
            throw new Error('dragAndDropService - register(): Configuration needs an ID.');
        }
        this.configurations[configuration.id] = configuration;
        if (!utils.stringUtils.isBlank(configuration.targetSelector)) {
            this.inViewElementObserver.addSelector(configuration.targetSelector);
        }
    }
    /**
     * This method removes the drag and drop instances specified by the provided IDs.
     *
     */
    unregister(configurationsIDList) {
        configurationsIDList.forEach((configurationID) => {
            const configuration = this.configurations[configurationID];
            if (configuration) {
                this.deactivateConfiguration(configuration);
                this.deactivateScrolling(configuration);
                delete this.configurations[configurationID];
            }
        });
    }
    /**
     * This method applies all drag and drop configurations registered.
     *
     */
    applyAll() {
        lodash__namespace.forEach(this.configurations, (currentConfig) => {
            this.apply(currentConfig.id);
        });
    }
    /**
     * This method apply the configuration specified by the provided ID in the current page. After this method is executed drag and drop can be started by the user.
     *
     */
    apply(configurationID) {
        const configuration = this.configurations[configurationID];
        if (configuration) {
            this.update(configuration.id);
            this.cacheDragImages(configuration);
            this.initializeScrolling(configuration);
        }
    }
    /**
     * This method updates the drag and drop instance specified by the provided ID in the current page. It is important to execute this method every time a draggable or droppable element
     * is added or removed from the page DOM.
     *
     */
    update(configurationID) {
        const configuration = this.configurations[configurationID];
        if (configuration) {
            this.deactivateConfiguration(configuration);
            this._update(configuration);
        }
    }
    /**
     * This method forces the page to prepare for a drag and drop operation. This method is necessary when the drag and drop operation is started somewhere else,
     * like on a different iFrame.
     *
     */
    markDragStarted() {
        this.setDragAndDropExecutionStatus(true);
        this.dragAndDropScrollingService.enable();
    }
    // Method used to stop drag and drop from another frame.
    /**
     * This method forces the page to clean after a drag and drop operation. This method is necessary when the drag and drop operation is stopped somewhere else,
     * like on a different iFrame.
     *
     */
    markDragStopped() {
        this.setDragAndDropExecutionStatus(false);
        this.dragAndDropScrollingService.disable();
    }
    _update(configuration) {
        const sourceSelectors = lodash__namespace.isArray(configuration.sourceSelector)
            ? configuration.sourceSelector
            : [configuration.sourceSelector];
        sourceSelectors.forEach((sourceSelector) => {
            const draggableElements = this.getSelector(sourceSelector + ':not([draggable])');
            draggableElements.attr(/* @ngInject */ DragAndDropService_1.DRAGGABLE_ATTR, 'true');
            draggableElements.on('dragstart', this.onDragStart.bind(this, configuration));
            draggableElements.on('dragend', this.onDragEnd.bind(this, configuration));
        });
        const droppableElements = this.getSelector(configuration.targetSelector + ':not([draggable])');
        droppableElements.attr(/* @ngInject */ DragAndDropService_1.DROPPABLE_ATTR, 'true'); // Not needed by HTML5. It's to mark element as processed.
        droppableElements.on('dragenter', this.onDragEnter.bind(this, configuration));
        droppableElements.on('dragover', this.onDragOver.bind(this, configuration));
        droppableElements.on('drop', this.onDrop.bind(this, configuration));
        droppableElements.on('dragleave', this.onDragLeave.bind(this, configuration));
    }
    deactivateConfiguration(configuration) {
        const sourceSelectors = Array.isArray(configuration.sourceSelector)
            ? configuration.sourceSelector.join(',')
            : configuration.sourceSelector;
        const draggableElements = this.getSelector(sourceSelectors);
        const droppableElements = this.getSelector(configuration.targetSelector);
        draggableElements.removeAttr(/* @ngInject */ DragAndDropService_1.DRAGGABLE_ATTR);
        droppableElements.removeAttr(/* @ngInject */ DragAndDropService_1.DROPPABLE_ATTR);
        draggableElements.off('dragstart');
        draggableElements.off('dragend');
        droppableElements.off('dragenter');
        droppableElements.off('dragover');
        droppableElements.off('dragleave');
        droppableElements.off('drop');
    }
    // Draggable Listeners
    onDragStart(configuration, yjQueryEvent) {
        // The native transferData object is modified outside the setTimeout since it can only be modified
        // inside the dragStart event handler (otherwise an exception is thrown by the browser).
        const evt = yjQueryEvent.originalEvent;
        this.setDragTransferData(configuration, evt);
        // Necessary because there's a bug in Chrome (and probably Safari) where dragEnd is triggered right after
        // dragStart whenever DOM is modified in the event handler. The problem can be circumvented by using setTimeout.
        setTimeout(() => {
            const component = this.yjQuery(yjQueryEvent.target).closest('.' + OVERLAY_COMPONENT_CLASS);
            this.setDragAndDropExecutionStatus(true, component);
            this.dragAndDropScrollingService.enable();
            if (configuration.startCallback) {
                configuration.startCallback(evt);
            }
        }, 0);
    }
    onDragEnd(configuration, yjQueryEvent) {
        const evt = yjQueryEvent.originalEvent;
        this.dragAndDropScrollingService.disable();
        if (this.isDragAndDropExecuting && configuration.stopCallback) {
            configuration.stopCallback(evt);
        }
        this.setDragAndDropExecutionStatus(false);
    }
    // Droppable Listeners
    onDragEnter(configuration, yjQueryEvent) {
        const evt = yjQueryEvent.originalEvent;
        evt.preventDefault();
        if (this.isDragAndDropExecuting && configuration.dragEnterCallback) {
            configuration.dragEnterCallback(evt);
        }
    }
    onDragOver(configuration, yjQueryEvent) {
        const evt = yjQueryEvent.originalEvent;
        evt.preventDefault();
        if (this.isDragAndDropExecuting && configuration.dragOverCallback) {
            configuration.dragOverCallback(evt);
        }
    }
    onDrop(configuration, yjQueryEvent) {
        const evt = yjQueryEvent.originalEvent;
        evt.preventDefault(); // Necessary to receive the on drop event. Otherwise, other handlers are executed.
        evt.stopPropagation();
        if (evt.relatedTarget && evt.relatedTarget.nodeType === 3) {
            return true;
        }
        if (evt.target === evt.relatedTarget) {
            return true;
        }
        if (this.isDragAndDropExecuting && configuration.dropCallback) {
            configuration.dropCallback(evt);
        }
        return false;
    }
    onDragLeave(configuration, yjQueryEvent) {
        const evt = yjQueryEvent.originalEvent;
        evt.preventDefault();
        if (this.isDragAndDropExecuting && configuration.outCallback) {
            configuration.outCallback(evt);
        }
    }
    // Helper Functions
    cacheDragImages(configuration) {
        let helperImg;
        if (configuration.helper) {
            helperImg = configuration.helper();
        }
        if (!helperImg) {
            return;
        }
        if (typeof helperImg === 'string') {
            configuration._cachedDragImage = new Image();
            configuration._cachedDragImage.src = helperImg;
        }
        else {
            configuration._cachedDragImage = helperImg;
        }
    }
    setDragTransferData(configuration, evt) {
        // Note: Firefox recently added some restrictions to their drag and drop functionality; it only
        // allows starting drag and drop operations if there's data present in the dataTransfer object.
        // Otherwise, the whole operation fails silently. Thus, some data needs to be added.
        evt.dataTransfer.setData('Text', configuration.id);
        if (configuration._cachedDragImage && evt.dataTransfer.setDragImage) {
            evt.dataTransfer.setDragImage(configuration._cachedDragImage, 0, 0);
        }
    }
    getSelector(selector) {
        return this.yjQuery(selector);
    }
    setDragAndDropExecutionStatus(isExecuting, element) {
        this.isDragAndDropExecuting = isExecuting;
        this.systemEventService.publish(isExecuting
            ? SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START
            : SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, element);
    }
    initializeScrolling(configuration) {
        if (configuration.enableScrolling && this.browserRequiresCustomScrolling()) {
            this.dragAndDropScrollingService.initialize();
        }
    }
    deactivateScrolling(configuration) {
        if (configuration.enableScrolling && this.browserRequiresCustomScrolling()) {
            this.dragAndDropScrollingService.deactivate();
        }
    }
    browserRequiresCustomScrolling() {
        // NOTE: It'd be better to identify if native scrolling while dragging is enabled in the browser, but
        // currently there's no way to know. Thus, browser fixing is necessary.
        return true;
    }
};
/* @ngInject */ exports.DragAndDropService.DRAGGABLE_ATTR = 'draggable';
/* @ngInject */ exports.DragAndDropService.DROPPABLE_ATTR = 'data-droppable';
/* @ngInject */ exports.DragAndDropService = /* @ngInject */ DragAndDropService_1 = __decorate([
    core.Injectable(),
    __param(0, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function, exports.DragAndDropScrollingService,
        exports.InViewElementObserver,
        exports.SystemEventService,
        IDragAndDropCrossOrigin])
], /* @ngInject */ exports.DragAndDropService);

/**
 * Contains a service that provides a rich drag and drop experience tailored for CMS operations.
 */
exports.DragAndDropServiceModule = class DragAndDropServiceModule {
};
exports.DragAndDropServiceModule = __decorate([
    core.NgModule({
        providers: [exports.InViewElementObserver, exports.DragAndDropScrollingService, exports.DragAndDropService]
    })
], exports.DragAndDropServiceModule);

// eslint-disable-next-line no-redeclare
const IDragEventType = {
    DROP: 'drop',
    DRAG_ENTER: 'dragenter',
    DRAG_OVER: 'dragover',
    DRAG_LEAVE: 'dragleave'
};

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @internal
 * @ignore
 */
class StorageNamespaceConverter {
    static ERR_INVALID_NAMESPACED_ID(id) {
        return new Error(`StorageNamespaceConverter - Invalid namespaced id [${id}]`);
    }
    static getNamespacedStorageId(namespace, storageId) {
        return `${namespace}${this.separator}${storageId}`;
    }
    static getStorageIdFromNamespacedId(namespacedId) {
        const matches = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
        if (matches && matches[2].length > 0) {
            return matches[2];
        }
        throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
    }
    static getNamespaceFromNamespacedId(namespacedId) {
        const matches = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
        if (matches && matches[1].length > 0) {
            return matches[1];
        }
        throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
    }
}
/**
 * Given:
 *  namespace = nmsp
 *  storageId = stoid
 *
 * Produces:
 *  newStorageId = nmsp<ns:id>stoid
 *
 * Fastest implementation I could think of that (most likely) will not clash with weird storageIds
 *
 * This algorithm is a bit overly simple, and assumes that neither storageId nor namespace contains "<ns:id>"
 * I think this is a fairly safe assumption, but if we have time in the future, we should escape any existing
 * matches of the string.
 */
StorageNamespaceConverter.separator = '<ns:id>';
StorageNamespaceConverter.namespaceDecoderRegexStr = '(.*)' + StorageNamespaceConverter.separator + '(.*)';

/**
 * @internal
 * @ignore
 */
class NamespacedStorageManager {
    constructor(storageManager, namespace) {
        this.storageManager = storageManager;
        this.namespace = namespace;
    }
    getStorage(storageConfiguration) {
        storageConfiguration.storageId = this.getNamespaceStorageId(storageConfiguration.storageId);
        return this.storageManager.getStorage(storageConfiguration);
    }
    deleteStorage(storageId, force = false) {
        return this.storageManager.deleteStorage(this.getNamespaceStorageId(storageId), force);
    }
    deleteExpiredStorages(force = false) {
        return this.storageManager.deleteExpiredStorages(force);
    }
    hasStorage(storageId) {
        return this.storageManager.hasStorage(this.getNamespaceStorageId(storageId));
    }
    registerStorageController(controller) {
        return this.storageManager.registerStorageController(controller);
    }
    getNamespaceStorageId(storageId) {
        return StorageNamespaceConverter.getNamespacedStorageId(this.namespace, storageId);
    }
    getStorageManager() {
        return this.storageManager;
    }
}

/**
 * @internal
 * @ignore
 */
class StorageManagerFactory {
    constructor(theOneAndOnlyStorageManager) {
        this.theOneAndOnlyStorageManager = theOneAndOnlyStorageManager;
    }
    static ERR_INVALID_NAMESPACE(namespace) {
        return new Error(`StorageManagerFactory Error: invalid namespace [${namespace}]. Namespace must be a non-empty string`);
    }
    getStorageManager(namespace) {
        this.validateNamespace(namespace);
        return new NamespacedStorageManager(this.theOneAndOnlyStorageManager, namespace);
    }
    validateNamespace(namespace) {
        if (typeof namespace !== 'string' || namespace.length <= 0) {
            throw StorageManagerFactory.ERR_INVALID_NAMESPACE(namespace);
        }
    }
}

class IStorageGateway {
    handleStorageRequest(storageConfiguration, method, args) {
        'proxyFunction';
        return Promise.resolve();
    }
}

/**
 * Represents a manager of multiple {@link IStorage}(s).
 *
 * Typically there is 1 StorageManager in the system, and it is responsible accessing, creating and deleting storages,
 * usually by delegating to {@link IStorageController}(s).
 *
 */
class IStorageManager {
    registerStorageController(controller) {
        'proxyFunction';
    }
    /**
     * Check if a storage has been created.
     */
    hasStorage(storageId) {
        'proxyFunction';
        return null;
    }
    /**
     * Get an existing or new storage
     */
    getStorage(storageConfiguration) {
        'proxyFunction';
        return null;
    }
    /**
     * Permanently delete a storage and all its data
     *
     * @param force If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
    deleteStorage(storageId, force) {
        'proxyFunction';
        return Promise.resolve(true);
    }
    /**
     * Delete all storages that have exceeded their idle timeout time.
     * See {@link IStorageOptions} for more details
     * @param force If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
    deleteExpiredStorages(force) {
        'proxyFunction';
        return Promise.resolve(true);
    }
}

/**
 * Represents a typical factory of {@link IStorageManager}(s).
 * There should typically only be 1 StorageManager in the system, which make this factory seem redundant, but it's used
 * to create wrapper around the single real StorageManager.
 *
 * The main use-case is for namespacing. A namespaced storagemanager will take care to prevent storageID clashes
 * between extensions or teams.
 */
class IStorageManagerFactory {
    /**
     * @param namespace A unique namespace for all your storage ids
     */
    getStorageManager(namespace) {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IStorageManagerGateway extends IStorageManager {
    getStorageSanitityCheck(storageConfiguration) {
        'proxyFunction';
        return Promise.resolve(true);
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Interface provider that allows you to mutate the default
 * storage properties before the storage system is initialized.
 */
class IStoragePropertiesService {
    getProperty(propertyName) {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const STORAGE_PROPERTIES_TOKEN = new core.InjectionToken('storageProperties');
const DO_NOT_USE_STORAGE_MANAGER_TOKEN = new core.InjectionToken('doNotUseStorageManager');

/**
 * Provides a Timer object that can invoke a callback after a certain period of time.
 *
 * A `Timer` must be instanciated calling **`timerService.createTimer()`**.
 * This `Timer` service uses native setInterval function and adds additional functions to it.
 */
class Timer {
    /**
     * @param _callback Callback function that will be invoked upon timeout.
     * @param _duration The number of milliseconds to wait before the callback is invoked.
     */
    constructor(zone, _callback, _duration = 1000) {
        this.zone = zone;
        this._callback = _callback;
        this._duration = _duration;
        /**
         * Keeps the interval reference. This will only be non-null when the
         * timer is actively counting down to callback invocation
         */
        this._timer = null;
    }
    /**
     * Returns true if the timer is active (counting down).
     */
    isActive() {
        return !!this._timer;
    }
    /**
     * Stops the timer, and then starts it again. If a new duration is given, the timer's duration will be set to that new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    restart(duration) {
        this._duration = duration || this._duration;
        this.stop();
        this.start();
    }
    /**
     * Start the timer, which will invoke the callback upon timeout.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    start(duration) {
        this._duration = duration || this._duration;
        this.zone.runOutsideAngular(() => {
            this._timer = setInterval(() => {
                try {
                    if (this._callback) {
                        this._callback();
                    }
                    else {
                        this.stop();
                    }
                }
                catch (e) {
                    this.stop();
                }
            }, this._duration);
        });
    }
    /**
     * Stop the current timer, if it is running, which will prevent the callback from being invoked.
     */
    stop() {
        clearInterval(this._timer);
        this._timer = null;
    }
    /**
     * Sets the duration to a new value.
     *
     * @param duration The new number of milliseconds to wait before the callback is invoked.
     * If not provided, the previously set duration is used.
     */
    resetDuration(duration) {
        this._duration = duration || this._duration;
    }
    /**
     * Clean up the internal object references
     */
    teardown() {
        this.stop();
        this._callback = null;
        this._duration = null;
        this._timer = null;
    }
}

/* @ngInject */ exports.TimerService = class /* @ngInject */ TimerService {
    constructor(ngZone) {
        this.ngZone = ngZone;
    }
    createTimer(callback, duration) {
        return new Timer(this.ngZone, callback, duration);
    }
};
/* @ngInject */ exports.TimerService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core.NgZone])
], /* @ngInject */ exports.TimerService);

/**
 * Angular utility service for JQuery operations
 */
/* @ngInject */ exports.JQueryUtilsService = class /* @ngInject */ JQueryUtilsService {
    constructor(yjQuery, document, EXTENDED_VIEW_PORT_MARGIN, windowUtils) {
        this.yjQuery = yjQuery;
        this.document = document;
        this.EXTENDED_VIEW_PORT_MARGIN = EXTENDED_VIEW_PORT_MARGIN;
        this.windowUtils = windowUtils;
    }
    /**
     * Parses a string HTML into a queriable DOM object
     * @param parent the DOM element from which we want to extract matching selectors
     * @param extractionSelector the yjQuery selector identifying the elements to be extracted
     */
    extractFromElement(parent, extractionSelector) {
        const element = this.yjQuery(parent);
        return element.filter(extractionSelector).add(element.find(extractionSelector));
    }
    /**
     * Parses a string HTML into a queriable DOM object, preserving any JavaScript present in the HTML.
     * Note - as this preserves the JavaScript present it must only be used on HTML strings originating
     * from a known safe location. Failure to do so may result in an XSS vulnerability.
     *
     */
    unsafeParseHTML(stringHTML) {
        return this.yjQuery.parseHTML(stringHTML, null, true);
    }
    /**
     * Checks whether passed HTMLElement is a part of the DOM
     */
    isInDOM(element) {
        return this.document.documentElement.contains(element);
    }
    /**
     *
     * Determines whether a DOM element is partially or totally intersecting with the "extended" viewPort
     * the "extended" viewPort is the real viewPort that extends up and down by a margin, in pixels, given by the overridable constant EXTENDED_VIEW_PORT_MARGIN
     */
    isInExtendedViewPort(element) {
        if (!this.document.documentElement.contains(element)) {
            return false;
        }
        const elem = this.yjQuery(element);
        const bounds = Object.assign(Object.assign({}, elem.offset()), { width: elem.outerWidth(), height: elem.outerHeight() });
        const doc = this.document.scrollingElement || this.document.documentElement;
        return nodeUtils.areIntersecting({
            left: -this.EXTENDED_VIEW_PORT_MARGIN + doc.scrollLeft,
            width: this.windowUtils.getWindow().innerWidth + 2 * this.EXTENDED_VIEW_PORT_MARGIN,
            top: -this.EXTENDED_VIEW_PORT_MARGIN + doc.scrollTop,
            height: this.windowUtils.getWindow().innerHeight + 2 * this.EXTENDED_VIEW_PORT_MARGIN
        }, bounds);
    }
};
/* @ngInject */ exports.JQueryUtilsService = __decorate([
    core.Injectable(),
    __param(0, core.Inject(YJQUERY_TOKEN)),
    __param(1, core.Inject(common.DOCUMENT)),
    __param(2, core.Inject(EXTENDED_VIEW_PORT_MARGIN_TOKEN)),
    __metadata("design:paramtypes", [Function, Document, Number, exports.WindowUtils])
], /* @ngInject */ exports.JQueryUtilsService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const CATALOG_DETAILS_ITEM_DATA = new core.InjectionToken('CATALOG_DETAILS_ITEM_DATA');

window.__smartedit__.addDecoratorPayload("Injectable", "L10nService", {
    providedIn: 'root'
});
/**
 * Service which exposes a subscription of the current language.
 */
/* @ngInject */ exports.L10nService = class /* @ngInject */ L10nService {
    constructor(languageService, crossFrameEventService) {
        this.languageService = languageService;
        this.languageSwitchSubject = new rxjs.BehaviorSubject(null);
        this.languageSwitch$ = this.languageSwitchSubject.asObservable();
        this.unRegSwitchLanguageEvent = crossFrameEventService.subscribe(utils.SWITCH_LANGUAGE_EVENT, () => this.resolveLanguage());
    }
    ngOnDestroy() {
        this.unRegSwitchLanguageEvent();
        this.languageSwitchSubject.unsubscribe();
    }
    resolveLanguage() {
        return __awaiter(this, void 0, void 0, function* () {
            const resolvedLanguage = yield this.languageService.getResolveLocaleIsoCode();
            this.languageSwitchSubject.next(resolvedLanguage);
        });
    }
};
/* @ngInject */ exports.L10nService = __decorate([
    core.Injectable({
        providedIn: 'root'
    }),
    __metadata("design:paramtypes", [exports.LanguageService,
        exports.CrossFrameEventService])
], /* @ngInject */ exports.L10nService);

// DO NOT DELETE this as we need this for angular routing service
class CustomHandlingStrategy {
    // we shall process all types of path to keep consistence with old routing strategy
    shouldProcessUrl(url) {
        return true;
    }
    extract(url) {
        return url;
    }
    merge(newUrlPart, rawUrl) {
        return newUrlPart;
    }
}

var /* @ngInject */ CMSModesService_1;
/* @ngInject */ exports.CMSModesService = /* @ngInject */ CMSModesService_1 = class /* @ngInject */ CMSModesService {
    constructor(perspectiveService) {
        this.perspectiveService = perspectiveService;
    }
    /**
     * Returns a promise that resolves to a boolean which is true if the current perspective loaded is versioning, false otherwise.
     */
    isVersioningPerspectiveActive() {
        return __awaiter(this, void 0, void 0, function* () {
            const activePerspectiveKey = yield this.perspectiveService.getActivePerspectiveKey();
            return activePerspectiveKey === /* @ngInject */ CMSModesService_1.VERSIONING_PERSPECTIVE_KEY;
        });
    }
};
/* @ngInject */ exports.CMSModesService.BASIC_PERSPECTIVE_KEY = 'se.cms.perspective.basic';
/* @ngInject */ exports.CMSModesService.ADVANCED_PERSPECTIVE_KEY = 'se.cms.perspective.advanced';
/* @ngInject */ exports.CMSModesService.VERSIONING_PERSPECTIVE_KEY = 'se.cms.perspective.versioning';
/* @ngInject */ exports.CMSModesService = /* @ngInject */ CMSModesService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IPerspectiveService])
], /* @ngInject */ exports.CMSModesService);

var _a;
const cmsitemsUri = `/cmswebservices/v1/sites/${CONTEXT_SITE_ID}/cmsitems`;
const cmsitemsEvictionTag = new utils.EvictionTag({ event: CMSITEMS_UPDATE_EVENT });
/**
 * @ngdoc service
 * @name cmsitemsRestService.cmsitemsRestService
 *
 * @description
 * Service to deal with CMS Items related CRUD operations.
 */
/* @ngInject */ exports.CmsitemsRestService = class /* @ngInject */ CmsitemsRestService {
    constructor(restServiceFactory, catalogService) {
        this.restServiceFactory = restServiceFactory;
        this.catalogService = catalogService;
        this.cmsitemsUuidsUri = `/cmswebservices/v1/sites/${CONTEXT_SITE_ID}/cmsitems/uuids`;
        this.resource = restServiceFactory.get(cmsitemsUri);
        this.versionedResource = restServiceFactory.get(cmsitemsUri + '/:itemUuid');
        this.uuidsResource = restServiceFactory.get(this.cmsitemsUuidsUri);
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getByIdAndVersion
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Get the CMS Item that matches the given item uuid (Universally Unique Identifier) for a given version.
     *
     * @param {String} cmsitemUuid The CMS Item uuid
     * @param {String} versionId The uid of the version to be retrieved.
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    getByIdAndVersion(itemUuid, versionId) {
        return this.versionedResource.get({
            itemUuid,
            versionId
        });
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#get
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Fetch CMS Items search result by making a REST call to the CMS Items API.
     * A search can be performed by a typeCode (optionnaly in combination of a mask parameter), or by providing a list of cms items uuid.
     *
     * @param {Object} queryParams The object representing the query params
     * @param {String} queryParams.pageSize number of items in the page
     * @param {String} queryParams.currentPage current page number
     * @param {String =} queryParams.typeCode for filtering on the cms item typeCode
     * @param {String =} queryParams.mask for filtering the search
     * @param {String =} queryParams.itemSearchParams search on additional fields using a comma separated list of field name and value
     * pairs which are separated by a colon. Exact matches only.
     * @param {String =} queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param {String =} queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the paged search result. If the
     * request fails, it resolves with errors from the backend.
     */
    get(queryParams) {
        return this.catalogService.retrieveUriContext().then((uriContext) => {
            const catalogDetailsParams = {
                catalogId: queryParams.catalogId || uriContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion: queryParams.catalogVersion || uriContext.CURRENT_CONTEXT_CATALOG_VERSION
            };
            queryParams = lodash__namespace.merge(catalogDetailsParams, queryParams);
            return this.restServiceFactory.get(cmsitemsUri).get(queryParams);
        });
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getByIds
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Fetch CMS Items by uuids, making a POST call to the CMS Items API.
     * A search can be performed by providing a list of cms items uuid.
     *
     * @param {string[] =} uuids list of cms item uuids
     *
     * @returns {Promise<CMSItem[]>} If request is successful, it returns a promise that resolves to the result. If the
     * request fails, it resolves with errors from the backend. Be mindful that the response payload size could
     * increase dramatically depending on the number of uuids that you send on the request.
     */
    getByIds(uuids, fields) {
        return this.getByIdsNoCache(uuids, fields);
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#update
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Update a CMS Item.
     *
     * @param {Object} cmsitem The object representing the CMS Item to update
     * @param {String} cmsitem.identifier The cms item identifier (uuid)
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    update(cmsitem, options) {
        return this.resource.update(cmsitem, options);
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#delete
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Remove a CMS Item.
     *
     * @param {Number} cmsitemUuid The CMS Item uuid
     */
    delete(identifier) {
        return this.resource.remove({
            identifier
        });
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#create
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Create a new CMS Item.
     *
     * @param {Object} cmsitem The object representing the CMS Item to create
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    create(cmsitem) {
        return this.catalogService.getCatalogVersionUUid().then((catalogVersionUUid) => {
            cmsitem.catalogVersion = cmsitem.catalogVersion || catalogVersionUUid;
            if (cmsitem.onlyOneRestrictionMustApply === undefined) {
                cmsitem.onlyOneRestrictionMustApply = false;
            }
            return this.resource.save(cmsitem);
        });
    }
    /**
     * The function is same as getByIds but it doesn't use caching, it will request data from backend every time.
     *
     * If request is successful, it returns a promise that resolves to the result. If the
     * request fails, it resolves with errors from the backend. Be mindful that the response payload size could
     * increase dramatically depending on the number of uuids that you send on the request.
     */
    getByIdsNoCache(uuids, fields) {
        uuids = Array.from(new Set(uuids)); // removing duplicates
        return this.catalogService.getCatalogVersionUUid().then((catalogVersion) => {
            const payload = {
                catalogVersion,
                uuids,
                fields
            };
            return this.uuidsResource.save(payload);
        });
    }
    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getById
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Get the CMS Item that matches the given item uuid (Universally Unique Identifier).
     *
     * @param {Number} cmsitemUuid The CMS Item uuid
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    getById(cmsitemUuid) {
        return this.resource.getById(cmsitemUuid);
    }
};
__decorate([
    utils.Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.CmsitemsRestService.prototype, "getByIdAndVersion", null);
__decorate([
    utils.Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.CmsitemsRestService.prototype, "get", null);
__decorate([
    utils.Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Array, String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.CmsitemsRestService.prototype, "getByIds", null);
__decorate([
    utils.InvalidateCache(cmsitemsEvictionTag),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [typeof (_a = typeof T !== "undefined" && T) === "function" ? _a : Object, Object]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.CmsitemsRestService.prototype, "update", null);
__decorate([
    utils.InvalidateCache(cmsitemsEvictionTag),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.CmsitemsRestService.prototype, "delete", null);
/* @ngInject */ exports.CmsitemsRestService = __decorate([
    utils.OperationContextRegistered(cmsitemsUri.replace(/CONTEXT_SITE_ID/, ':CONTEXT_SITE_ID'), 'CMS'),
    core.Injectable(),
    __metadata("design:paramtypes", [IRestServiceFactory,
        ICatalogService])
], /* @ngInject */ exports.CmsitemsRestService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Represents possible workflow action statuses.
 */
exports.WorkflowActionStatus = void 0;
(function (WorkflowActionStatus) {
    WorkflowActionStatus["PENDING"] = "pending";
    WorkflowActionStatus["IN_PROGRESS"] = "in_progress";
    WorkflowActionStatus["PAUSED"] = "paused";
    WorkflowActionStatus["COMPLETED"] = "completed";
    WorkflowActionStatus["DISABLED"] = "disabled";
    WorkflowActionStatus["ENDED_THROUGH_END_OF_WORKFLOW"] = "ended_through_end_of_workflow";
    WorkflowActionStatus["TERMINATED"] = "terminated";
})(exports.WorkflowActionStatus || (exports.WorkflowActionStatus = {}));

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Represents workflow operations.
 */
exports.WorkflowOperations = void 0;
(function (WorkflowOperations) {
    WorkflowOperations["CANCEL"] = "CANCEL_WORKFLOW";
    WorkflowOperations["MAKE_DECISION"] = "MAKE_DECISION";
})(exports.WorkflowOperations || (exports.WorkflowOperations = {}));

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Represents workflow statuses.
 */
exports.WorkflowStatus = void 0;
(function (WorkflowStatus) {
    WorkflowStatus["CONTINUED"] = "continued";
    WorkflowStatus["RUNNING"] = "running";
    WorkflowStatus["PAUSED"] = "paused";
    WorkflowStatus["FINISHED"] = "finished";
    WorkflowStatus["ABORTED"] = "aborted";
    WorkflowStatus["NEW"] = "new";
})(exports.WorkflowStatus || (exports.WorkflowStatus = {}));

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const WORKFLOW_CREATED_EVENT = 'WORKFLOW_CREATED_EVENT';
const WORKFLOW_FINISHED_EVENT = 'WORKFLOW_FINISHED_EVENT';
const workflowCreatedEvictionTag = new utils.EvictionTag({ event: WORKFLOW_CREATED_EVENT });
const workflowCompletedEvictionTag = new utils.EvictionTag({ event: WORKFLOW_FINISHED_EVENT });
const workflowTasksMenuOpenedEvictionTag = new utils.EvictionTag({
    event: 'WORKFLOW_TASKS_MENU_OPENED_EVENT'
});

const INBOX_POLLING_PARAMS = {
    INBOX_POLLING_TIMEOUT: 20000,
    INBOX_POLLING_PAGESIZE: 10,
    INBOX_POLLING_CURRENTPAGE: 0
};
/**
 * Used to retrieve inbox tasks.
 */
/* @ngInject */ exports.WorkflowTasksPollingService = class /* @ngInject */ WorkflowTasksPollingService {
    constructor(timerService, restServiceFactory, crossFrameEventService) {
        this.timerService = timerService;
        this.restServiceFactory = restServiceFactory;
        this.crossFrameEventService = crossFrameEventService;
        this.resourceInboxURI = '/cmssmarteditwebservices/v1/inbox/workflowtasks';
        this.subscribers = [];
        this.syncPollingTimer = null;
        this.savedHashedTasks = [];
        this.inboxRESTService = this.restServiceFactory.get(this.resourceInboxURI);
        this.crossFrameEventService.subscribe(utils.EVENTS.AUTHORIZATION_SUCCESS, () => this.initPolling());
        this.crossFrameEventService.subscribe(utils.EVENTS.LOGOUT, () => this.stopPolling());
        this.crossFrameEventService.subscribe(utils.EVENTS.REAUTH_STARTED, () => this.stopPolling());
        this.initPolling();
    }
    /**
     * Stops a polling timer.
     */
    stopPolling() {
        if (this.syncPollingTimer.isActive()) {
            this.syncPollingTimer.stop();
        }
    }
    /**
     * Starts a polling timer.
     */
    startPolling() {
        if (!this.syncPollingTimer.isActive()) {
            this.syncPollingTimer.restart(INBOX_POLLING_PARAMS.INBOX_POLLING_TIMEOUT);
        }
    }
    /**
     * Adds a new subscriber to the polling service. The subscriber is called with a list of new tasks and a pagination information.
     *
     * @param subscriber The subscriber.
     * @param callOnInit Default is true, when set to false, will not call the subscriber on initialization of the polling.
     *
     * @returns The method that can be used to unsubscribe.
     */
    addSubscriber(subscriber, callOnInit) {
        this.subscribers.push({
            subscriber,
            callOnInit
        });
        return () => this.unsubscribe(subscriber);
    }
    /**
     * Initializes a polling process.
     */
    initPolling() {
        this.syncPollingTimer = this.timerService.createTimer(() => this.fetchInboxTasks(false), INBOX_POLLING_PARAMS.INBOX_POLLING_TIMEOUT);
        this.fetchInboxTasks(true);
        this.startPolling();
    }
    /**
     * Unsubscribes a subscriber.
     * @param subscriber The subscriber that will be unsubscribed.
     */
    unsubscribe(subscriber) {
        const index = this.subscribers.findIndex((subs) => subs.subscriber === subscriber);
        if (index > -1) {
            this.subscribers.splice(index, 1);
        }
    }
    /**
     * Returns tasks that have not been yet delivered to subscribers.
     * @param tasks The list of retrieved tasks from the backend.
     * @return The list of new tasks.
     */
    getNewTasks(tasks) {
        return tasks.filter((task) => !this.savedHashedTasks.includes(this.encodeTask(task)));
    }
    /**
     * New tasks are added at the end of the array. If the array is bigger than INBOX_POLLING_PAGESIZE
     * it shrinks from the beginning to the INBOX_POLLING_PAGESIZE size.
     * @param newTasks The list of new tasks that will be stored in cache. Each task is encoded as base-64 string.
     */
    saveNewHashedTasks(newTasks) {
        newTasks.forEach((task) => this.savedHashedTasks.push(this.encodeTask(task)));
        const sizeDiff = this.savedHashedTasks.length - INBOX_POLLING_PARAMS.INBOX_POLLING_PAGESIZE;
        if (sizeDiff > 0) {
            for (let i = 0; i < sizeDiff; i++) {
                this.savedHashedTasks.shift();
            }
        }
    }
    /**
     * Encodes a task.
     * @param task the task that will be encoded to a base-64 string.
     * @return The encoded string
     */
    encodeTask(task) {
        const taskClone = lodash.cloneDeep(task);
        if (taskClone.action) {
            delete taskClone.action.startedAgoInMillis;
        }
        return stringUtils.getEncodedString(taskClone);
    }
    /**
     * Retrieves the list if tasks from the backend in paginated view.
     * It calls each subscriber with a list of new tasks and pagination information.
     */
    fetchInboxTasks(isInit) {
        return __awaiter(this, void 0, void 0, function* () {
            const response = yield this.fetchTaskList();
            if (!response) {
                this.stopPolling();
                return;
            }
            const newTasks = this.getNewTasks(response.tasks);
            this.saveNewHashedTasks(newTasks);
            this.subscribers.forEach((subscriber) => {
                if (!isInit || (isInit && !!subscriber.callOnInit)) {
                    subscriber.subscriber(newTasks, response.pagination);
                }
            });
        });
    }
    fetchTaskList() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                return yield this.inboxRESTService.get({
                    pageSize: INBOX_POLLING_PARAMS.INBOX_POLLING_PAGESIZE,
                    currentPage: INBOX_POLLING_PARAMS.INBOX_POLLING_CURRENTPAGE
                });
            }
            catch (error) {
                return Promise.reject();
            }
        });
    }
};
/* @ngInject */ exports.WorkflowTasksPollingService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.TimerService,
        IRestServiceFactory,
        exports.CrossFrameEventService])
], /* @ngInject */ exports.WorkflowTasksPollingService);

exports.OpenPageWorkflowMenu = void 0;
(function (OpenPageWorkflowMenu) {
    OpenPageWorkflowMenu["Default"] = "Default";
    OpenPageWorkflowMenu["SwitchPerspective"] = "SwitchPerspective";
})(exports.OpenPageWorkflowMenu || (exports.OpenPageWorkflowMenu = {}));
/**
 * This service is used to manage workflows.
 */
/* @ngInject */ exports.WorkflowService = class /* @ngInject */ WorkflowService {
    constructor(restServiceFactory, crossFrameEventService, systemEventService, sharedDataService, perspectiveService, catalogService, experienceService, workflowTasksPollingService, pageService, syncPollingService, cmsitemsRestService, translateService, storageService) {
        this.restServiceFactory = restServiceFactory;
        this.crossFrameEventService = crossFrameEventService;
        this.systemEventService = systemEventService;
        this.sharedDataService = sharedDataService;
        this.perspectiveService = perspectiveService;
        this.catalogService = catalogService;
        this.experienceService = experienceService;
        this.workflowTasksPollingService = workflowTasksPollingService;
        this.pageService = pageService;
        this.syncPollingService = syncPollingService;
        this.cmsitemsRestService = cmsitemsRestService;
        this.translateService = translateService;
        this.storageService = storageService;
        this.workflowTasksCountSubject = new rxjs.BehaviorSubject(0);
        this.resourceWorkflowURI = `/cmswebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workflows`;
        this.resourceWorkflowActionsURI = `/cmswebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workflows/:workflowCode/actions`;
        this.resourceWorkflowTemplateURI = `/cmswebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workflowtemplates`;
        this.resourceWorkflowOperationsURI = `/cmswebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workflows/:workflowCode/operations`;
        this.resourceWorkflowActionCommentsURI = `/cmswebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workflows/:workflowCode/actions/:actionCode/comments`;
        this.resourceWorkflowEditableItemsURI = `/cmssmarteditwebservices/v1/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/workfloweditableitems`;
        this.resourceWorkflowInboxTasksURI = `/cmssmarteditwebservices/v1/inbox/workflowtasks`;
        this.workflowRESTService = this.restServiceFactory.get(this.resourceWorkflowURI);
        this.workflowTemplateRESTService = this.restServiceFactory.get(this.resourceWorkflowTemplateURI);
        this.workflowActionsRESTService = this.restServiceFactory.get(this.resourceWorkflowActionsURI);
        this.workflowInboxTasksRESTService = this.restServiceFactory.get(this.resourceWorkflowInboxTasksURI);
        this.workflowEditableItemsRESTService = this.restServiceFactory.get(this.resourceWorkflowEditableItemsURI);
        this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, () => this.openPageWorkflowMenu());
        this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, () => this.openPageWorkflowMenu());
        this.workflowTasksPollingService.addSubscriber((tasks, pagination) => {
            const totalNumberOfTasks = pagination.totalCount || 0;
            this.updateWorkflowTasksCount(totalNumberOfTasks);
        }, true);
    }
    /**
     * Fetch workflow search result by making a REST call to the workflow API.
     *
     * @param queryParams The object representing the query params
     * @param queryParams.pageSize number of items in the page
     * @param queryParams.currentPage current page number
     * @param queryParams.attachments comma separated list of attachment id
     * @param queryParams.status comma separated list of workflow status
     * @param queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns If request is successful, it returns a promise that resolves with the workflow search result. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflows(queryParams) {
        return __awaiter(this, void 0, void 0, function* () {
            const workflowList = yield this.workflowRESTService.get(queryParams);
            return workflowList.workflows;
        });
    }
    /**
     * Fetch workflow templates search result by making a REST call to the workflow API.
     *
     * @param queryParams The object representing the query params.
     * @param queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns If request is successful, it returns a promise that resolves with the workflow template search result. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowTemplates(queryParams) {
        return __awaiter(this, void 0, void 0, function* () {
            const workflowTemplateList = yield this.workflowTemplateRESTService.get(queryParams);
            return workflowTemplateList.templates;
        });
    }
    /**
     * Fetch all actions for a given workflow code.
     *
     * @returns If request is successful, it returns a promise that resolves to list of available actions. If the
     * request fails, it resolves with errors from the backend.
     */
    getAllActionsForWorkflowCode(workflowCode) {
        return __awaiter(this, void 0, void 0, function* () {
            const workflow = yield this.workflowActionsRESTService.get({
                workflowCode
            });
            return workflow.actions;
        });
    }
    /**
     * Cancels the workflow. Shows the confirmation message before sending the request.
     */
    cancelWorflow(workflow) {
        this.workflowOperationsRESTService = this.restServiceFactory.get(this.resourceWorkflowOperationsURI.replace(':workflowCode', workflow.workflowCode));
        return this.workflowOperationsRESTService.save({
            operation: exports.WorkflowOperations.CANCEL
        });
    }
    /**
     * Returns a workflow template using its code.
     *
     * @returns A promise that resolves with the workflow template result, if the request is successful. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowTemplateByCode(code) {
        return __awaiter(this, void 0, void 0, function* () {
            const workflowTemplates = yield this.getWorkflowTemplates({});
            const workflow = workflowTemplates.find((wf) => wf.code === code);
            return workflow;
        });
    }
    /**
     * This method determines whether the current catalog version (the one in the current experience) has workflows
     * enabled. A catalog version has workflows enabled if it has at least one workflow template assigned to it.
     *
     * @returns A promise that resolves to a boolean. It will be true, if the workflow is
     * enabled for the current catalog version. False, otherwise.
     */
    areWorkflowsEnabledOnCurrentCatalogVersion() {
        return __awaiter(this, void 0, void 0, function* () {
            const workflowTemplates = yield this.getWorkflowTemplates({});
            return workflowTemplates && workflowTemplates.length > 0;
        });
    }
    /**
     * Fetch an active workflow for a page uuid.
     *
     * @returns A promise that resolves with the workflow object
     * or null, if the request is sucessful and there is no active workflow for provided page uuid.
     * If the request fails, it resolves with errors from the backend.
     */
    getActiveWorkflowForPageUuid(pageUuid) {
        return __awaiter(this, void 0, void 0, function* () {
            const workflows = yield this.getWorkflows({
                pageSize: 1,
                currentPage: 0,
                attachment: pageUuid,
                statuses: exports.WorkflowStatus.RUNNING + ',' + exports.WorkflowStatus.PAUSED
            });
            return workflows[0] === undefined ? null : workflows[0];
        });
    }
    /**
     * Verifies whether the page is in a workflow or not.
     *
     * @returns If request is successful, it returns a promise that resolves with boolean value.
     * If the request fails, it resolves with errors from the backend.
     */
    isPageInWorkflow(page) {
        return __awaiter(this, void 0, void 0, function* () {
            return !!(yield this.getActiveWorkflowForPageUuid(page.uuid));
        });
    }
    /**
     * Verifies whether the use is a participant of a current active action.
     *
     * @returns If request is successful, it returns a promise that resolves to a boolean. If the
     * request fails, it resolves with errors from the backend.
     */
    isUserParticipanInActiveAction(workflowCode) {
        return __awaiter(this, void 0, void 0, function* () {
            const activeActions = yield this.getActiveActionsForWorkflowCode(workflowCode);
            return activeActions.length > 0;
        });
    }
    /**
     * Fetch all active actions for a given workflow code and for the current user.
     *
     * @returns If request is successful, it returns a promise that resolves to list of active actions. If the
     * request fails, it resolves with errors from the backend.
     */
    getActiveActionsForWorkflowCode(workflowCode) {
        return __awaiter(this, void 0, void 0, function* () {
            const actions = yield this.getAllActionsForWorkflowCode(workflowCode);
            return actions.filter((action) => action.isCurrentUserParticipant &&
                (exports.WorkflowActionStatus.IN_PROGRESS === action.status.toLowerCase() ||
                    exports.WorkflowActionStatus.PAUSED === action.status.toLowerCase()));
        });
    }
    /**
     * Fetch a page of comments for a given workflow action and some pageable data.
     *
     * @returns If request is successful, it returns a promise that resolves to list of available comments for a given workflow and workflow action. If the
     * request fails, it resolves with errors from the backend.
     */
    getCommentsForWorkflowAction(workflowCode, workflowActionCode, payload) {
        this.workflowActionCommentsRESTService = this.restServiceFactory.get(this.resourceWorkflowActionCommentsURI
            .replace(':workflowCode', workflowCode)
            .replace(':actionCode', workflowActionCode));
        return this.workflowActionCommentsRESTService.page(payload);
    }
    /**
     * Fetches a page of workflow inbox tasks active for a given user.
     *
     * @returns If request is successful, it returns a promise that resolves to a page of workflow inbox tasks for a given user. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowInboxTasks(payload) {
        return this.workflowInboxTasksRESTService.page(payload);
    }
    /** The total number of active workflow tasks. */
    getTotalNumberOfActiveWorkflowTasks() {
        return this.workflowTasksCountSubject.asObservable();
    }
    updateWorkflowTasksCount(count) {
        this.workflowTasksCountSubject.next(count);
    }
    /**
     * Returns information about whether each item is editable or not. It also returns a workflow code where item is editable.
     *
     * @returns If request is successful, it returns a promise that resolves to a list of objects where each object
     * contains information about whether each item is editable or not. If the request fails, it resolves with errors from the backend.
     */
    getWorkflowEditableItems(itemUids) {
        return __awaiter(this, void 0, void 0, function* () {
            const data = yield this.workflowEditableItemsRESTService.get({
                itemUids
            });
            return data.editableItems;
        });
    }
    /**
     * Returns a resource uri for workflows.
     */
    getResourceWorkflowURI() {
        return this.resourceWorkflowURI;
    }
    /**
     * Returns a resource uri for workflow operations.
     */
    getResourceWorkflowOperationsURI() {
        return this.resourceWorkflowOperationsURI;
    }
    /**
     * Opens the page workflow menu. If the current perspective is not basic or advanced, it will switch to advanced perspective and then opens the menu.
     */
    openPageWorkflowMenu() {
        return __awaiter(this, void 0, void 0, function* () {
            const data = yield this.sharedDataService.get(OPEN_PAGE_WORKFLOW_MENU);
            if (data === exports.OpenPageWorkflowMenu.Default) {
                const activePerspective = yield this.perspectiveService.getActivePerspectiveKey();
                if (activePerspective === exports.CMSModesService.BASIC_PERSPECTIVE_KEY ||
                    activePerspective === exports.CMSModesService.ADVANCED_PERSPECTIVE_KEY) {
                    this.systemEventService.publish(CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU, true);
                    this.sharedDataService.remove(OPEN_PAGE_WORKFLOW_MENU);
                }
                else {
                    yield this.sharedDataService.set(OPEN_PAGE_WORKFLOW_MENU, exports.OpenPageWorkflowMenu.SwitchPerspective);
                    this.perspectiveService.switchTo(exports.CMSModesService.ADVANCED_PERSPECTIVE_KEY);
                }
            }
            else if (data === exports.OpenPageWorkflowMenu.SwitchPerspective) {
                this.systemEventService.publish(CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU, true);
                this.sharedDataService.remove(OPEN_PAGE_WORKFLOW_MENU);
            }
        });
    }
    /**
     * Loads the experience by building experience params from the given Workflow Task and then opens the page workflow menu.
     * If the current experience is same as the experience params from the given workflow task, it just opens the page workflow menu.
     * Otherwise, it loads the experience and then opens the page workflow menu.
     */
    loadExperienceAndOpenPageWorkflowMenu(task) {
        return __awaiter(this, void 0, void 0, function* () {
            if (task) {
                const defaultSite = yield this.catalogService.getDefaultSiteForContentCatalog(task.attachments[0].catalogId);
                const experienceParams = {
                    siteId: defaultSite.uid,
                    catalogId: task.attachments[0].catalogId,
                    catalogVersion: task.attachments[0].catalogVersion,
                    pageId: task.attachments[0].pageUid
                };
                /**
                 * First check if you are in storefront view or not,
                 * - If in storefront view, then check if same as current experience or not.
                 * 		- If requested experience is same as current experience then just open the workflow task menu.
                 * 	      to fix https://jira.tools.sap/browse/CXEC-20979 add page status when comparing
                 * 		- If requested experience is not same as current experience then load the provided experience.
                 * - If not in storefront view, then load the provided experience.
                 */
                if (!!windowUtils.getGatewayTargetFrame()) {
                    const isEqual = yield this.experienceService.compareWithCurrentExperience(experienceParams);
                    const pageInfo = yield this.pageService.getCurrentPageInfo();
                    if (isEqual && pageInfo && pageInfo.displayStatus === task.action.status) {
                        yield this.sharedDataService.set(OPEN_PAGE_WORKFLOW_MENU, exports.OpenPageWorkflowMenu.Default);
                        this.openPageWorkflowMenu();
                    }
                    else {
                        this._loadExperience(experienceParams);
                    }
                }
                else {
                    this._loadExperience(experienceParams);
                }
            }
        });
    }
    pageHasUnavailableDependencies() {
        return __awaiter(this, void 0, void 0, function* () {
            const syncStatus = yield this.getPageSyncStatus();
            return (!!syncStatus.unavailableDependencies && syncStatus.unavailableDependencies.length > 0);
        });
    }
    fetchPageTranslatedApprovalInfo() {
        return __awaiter(this, void 0, void 0, function* () {
            const syncStatus = yield this.getPageSyncStatus();
            const itemIds = syncStatus.unavailableDependencies.map(({ itemId }) => itemId);
            const itemNames = yield this.cmsitemsRestService
                .getByIds(itemIds)
                .then(({ response }) => response.map(({ name }) => name).join(', '));
            return this.translateService.instant('se.cms.workflow.approval.info', {
                itemNames
            });
        });
    }
    _loadExperience(experience) {
        this.storageService.setValueInLocalStorage('seselectedsite', experience.siteId, false);
        this.experienceService.loadExperience(experience).then(() => {
            this.sharedDataService.set(OPEN_PAGE_WORKFLOW_MENU, exports.OpenPageWorkflowMenu.Default);
        });
    }
    getPageSyncStatus() {
        return __awaiter(this, void 0, void 0, function* () {
            const { uuid } = yield this.pageService.getCurrentPageInfo();
            const uriContext = yield this.catalogService.retrieveUriContext();
            return this.syncPollingService.getSyncStatus(uuid, uriContext);
        });
    }
};
__decorate([
    utils.Cached({
        actions: [rarelyChangingContent],
        tags: [
            pageChangeEvictionTag,
            perspectiveChangedEvictionTag,
            workflowTasksMenuOpenedEvictionTag,
            workflowCompletedEvictionTag,
            workflowCreatedEvictionTag
        ]
    }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.WorkflowService.prototype, "getWorkflows", null);
__decorate([
    utils.Cached({ actions: [rarelyChangingContent], tags: [pageChangeEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.WorkflowService.prototype, "getWorkflowTemplates", null);
__decorate([
    utils.Cached({
        actions: [rarelyChangingContent],
        tags: [
            pageChangeEvictionTag,
            workflowTasksMenuOpenedEvictionTag,
            workflowCompletedEvictionTag,
            workflowCreatedEvictionTag
        ]
    }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.WorkflowService.prototype, "getAllActionsForWorkflowCode", null);
/* @ngInject */ exports.WorkflowService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IRestServiceFactory,
        exports.CrossFrameEventService,
        exports.SystemEventService,
        utils.ISharedDataService,
        IPerspectiveService,
        ICatalogService,
        IExperienceService,
        exports.WorkflowTasksPollingService,
        IPageService,
        ISyncPollingService,
        exports.CmsitemsRestService,
        core$1.TranslateService,
        utils.IStorageService])
], /* @ngInject */ exports.WorkflowService);

exports.SyncPollingService = class SyncPollingService extends ISyncPollingService {
    constructor() {
        super();
    }
};
exports.SyncPollingService = __decorate([
    GatewayProxied('getSyncStatus', 'fetchSyncStatus', 'changePollingSpeed', 'registerSyncPollingEvents', 'performSync'),
    __metadata("design:paramtypes", [])
], exports.SyncPollingService);

/**
 * Used to manage versions in a page.
 */
/* @ngInject */ exports.PageVersioningService = class /* @ngInject */ PageVersioningService {
    constructor(restServiceFactory) {
        this.restServiceFactory = restServiceFactory;
        this.pageVersionsServiceResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions`;
        this.pageVersionsRollbackServiceResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions/:versionId/rollbacks`;
        this.pageVersionRESTService = this.restServiceFactory.get(this.pageVersionsServiceResourceURI);
        this.pageVersionsRollbackRESTService = this.restServiceFactory.get(this.pageVersionsRollbackServiceResourceURI);
    }
    /**
     * Retrieves the list of versions found for the page identified by the provided id. This method is paged.
     *
     * @param payload The payload containing search query params, including the pageable information.
     * @returns A promise that resolves to a paged list of versions.
     */
    findPageVersions(payload) {
        return this.pageVersionRESTService.page(payload);
    }
    /**
     * Retrieves the page version information for the provided versionId.
     */
    getPageVersionForId(pageUuid, versionId) {
        return this.pageVersionRESTService.get({
            pageUuid,
            identifier: versionId
        });
    }
    /**
     * Retrieves the resource URI to manage page versions.
     */
    getResourceURI() {
        return this.pageVersionsServiceResourceURI;
    }
    deletePageVersion(pageUuid, versionId) {
        return this.pageVersionRESTService.remove({
            pageUuid,
            identifier: versionId
        });
    }
    /**
     * Rollbacks the page to the provided version. This process will automatically create a version of the current page.
     */
    rollbackPageVersion(pageUuid, versionId) {
        return this.pageVersionsRollbackRESTService.save({ pageUuid, versionId });
    }
};
/* @ngInject */ exports.PageVersioningService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IRestServiceFactory])
], /* @ngInject */ exports.PageVersioningService);

/**
 * This service is meant to be used internally by the page versions menu.
 * It allows selecting and deselecting a page version to be rendered in the Versioning Mode.
 */
/* @ngInject */ exports.PageVersionSelectionService = class /* @ngInject */ PageVersionSelectionService {
    constructor(crossFrameEventService, alertService, experienceService, cMSModesService, pageInfoService, pageVersioningService, translateService) {
        this.crossFrameEventService = crossFrameEventService;
        this.alertService = alertService;
        this.experienceService = experienceService;
        this.cMSModesService = cMSModesService;
        this.pageInfoService = pageInfoService;
        this.pageVersioningService = pageVersioningService;
        this.translateService = translateService;
        this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY = 'se.cms.pageVersionsMenu';
        this.PAGE_VERSION_UNSELECTED_MSG_KEY = 'se.cms.versions.unselect.version';
        this.selectedPageVersionSubject = new rxjs.BehaviorSubject(null);
        this.unSubEventPerspectiveChanged = this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, () => this.removePageVersionOnPerspectiveChange());
        this.unSubEventPerspectiveRefreshed = this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, () => this.resetPageVersionContext());
        this.unSubEventPageChange = this.crossFrameEventService.subscribe(utils.EVENTS.PAGE_CHANGE, (_eventId, experience) => this.initOnPageChange(experience));
        this.unSubSelectedPageVersion = this.selectedPageVersionSubject.subscribe((value) => (this.selectedPageVersion = value));
    }
    ngOnDestroy() {
        this.unSubEventPerspectiveChanged();
        this.unSubEventPerspectiveRefreshed();
        this.unSubEventPageChange();
        this.unSubSelectedPageVersion.unsubscribe();
    }
    getSelectedPageVersion() {
        return this.selectedPageVersion;
    }
    getSelectedPageVersion$() {
        return this.selectedPageVersionSubject.asObservable();
    }
    hideToolbarContextIfNotNeeded() {
        if (!this.selectedPageVersion) {
            this.crossFrameEventService.publish(HIDE_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
        }
    }
    showToolbarContextIfNeeded() {
        if (this.selectedPageVersion) {
            this.crossFrameEventService.publish(SHOW_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
        }
    }
    selectPageVersion(version) {
        if (!this.isSameVersion(this.selectedPageVersion, version)) {
            this.selectedPageVersionSubject.next(version);
            this.experienceService.updateExperience({
                versionId: version.uid
            });
            this.showToolbarContextIfNeeded();
            this.crossFrameEventService.publish(utils.EVENTS.PAGE_SELECTED);
        }
    }
    deselectPageVersion(showAlert = true) {
        if (this.selectedPageVersion && showAlert) {
            const msgTranslated = this.translateService.instant(this.PAGE_VERSION_UNSELECTED_MSG_KEY);
            this.alertService.showInfo(msgTranslated);
        }
        this.selectedPageVersionSubject.next(null);
        this.experienceService.updateExperience({
            versionId: null
        });
        this.crossFrameEventService.publish(HIDE_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
    }
    updatePageVersionDetails(version) {
        this.selectedPageVersionSubject.next(version);
    }
    /**
     * Required especially when a version is selected and you refresh the browser.
     */
    initOnPageChange(experience) {
        return __awaiter(this, void 0, void 0, function* () {
            if (experience.versionId && !this.selectedPageVersion) {
                const pageUuid = yield this.pageInfoService.getPageUUID();
                const version = yield this.pageVersioningService.getPageVersionForId(pageUuid, String(experience.versionId));
                this.selectedPageVersionSubject.next(version);
                this.showToolbarContextIfNeeded();
                this.crossFrameEventService.publish(utils.EVENTS.PAGE_SELECTED);
            }
        });
    }
    isSameVersion(selectedPageVersion, newVersion) {
        return selectedPageVersion !== null && newVersion !== null
            ? this.selectedPageVersion.uid === newVersion.uid
            : false;
    }
    removePageVersionOnPerspectiveChange() {
        return __awaiter(this, void 0, void 0, function* () {
            const isVersioningModeActive = yield this.cMSModesService.isVersioningPerspectiveActive();
            if (this.selectedPageVersion) {
                const pageUuid = yield this.pageInfoService.getPageUUID();
                if (!isVersioningModeActive || this.selectedPageVersion.itemUUID !== pageUuid) {
                    this.deselectPageVersion();
                }
            }
        });
    }
    resetPageVersionContext() {
        return __awaiter(this, void 0, void 0, function* () {
            const experience = yield this.experienceService.getCurrentExperience();
            if (!experience.versionId && this.selectedPageVersion) {
                this.selectedPageVersionSubject.next(null);
                this.hideToolbarContextIfNotNeeded();
            }
            else {
                this.showToolbarContextIfNeeded();
            }
        });
    }
};
/* @ngInject */ exports.PageVersionSelectionService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.CrossFrameEventService,
        utils.IAlertService,
        IExperienceService,
        exports.CMSModesService,
        IPageInfoService,
        exports.PageVersioningService,
        core$1.TranslateService])
], /* @ngInject */ exports.PageVersionSelectionService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const THEME_SWITCHER_ROUTER_MISSING_ERROR = `You need to import RouterModule to enable theme settings via the url.`;

/* @ngInject */ exports.ThemesService = 
/**
 * @deprecated
 * Service providing theme switcher functionality.
 * Deprecated since 0.35.0 in favor of ThemingService from ThemingModule
 */
class /* @ngInject */ ThemesService {
    constructor(_activatedRoute, _sanitizer, restServiceFactory, languageService) {
        this._activatedRoute = _activatedRoute;
        this._sanitizer = _sanitizer;
        this.restServiceFactory = restServiceFactory;
        this.languageService = languageService;
        /** Subject triggered, when the theming is changed by URL parameter */
        this.onThemeQueryParamChange = new rxjs.Subject();
        /** @hidden **/
        this._onDestroy$ = new rxjs.Subject();
        this.initThemes();
    }
    /**
     * get All themes by current language
     */
    initThemes() {
        return __awaiter(this, void 0, void 0, function* () {
            const locale = yield this.languageService.getResolveLocale();
            this.themeRestService = this.restServiceFactory.get(`${ALL_ACTIVE_THEMES_URI}?langIsoCode=${locale}`);
            this.themes = (yield this.themeRestService.get());
        });
    }
    getThemes() {
        return this.themes;
    }
    /**
     * Set theme according to additional URL parameter.
     * This parameter can be changed in function argument.
     * By default it's `theme`.
     **/
    setThemeByRoute(themeParamName) {
        const paramName = themeParamName || 'theme';
        if (!this._activatedRoute) {
            throw new Error(THEME_SWITCHER_ROUTER_MISSING_ERROR);
        }
        this._activatedRoute.queryParams
            .pipe(operators.takeUntil(this._onDestroy$), operators.filter((param) => param && param[paramName]))
            .subscribe((param) => this.propagateThemes(param[paramName]));
        const nativeTheme = this.getNativeParameterByName(paramName);
        if (nativeTheme) {
            this.propagateThemes(nativeTheme);
        }
    }
    /** Method to get once theme object directly from url. */
    getThemesFromURL(param) {
        const paramName = param || 'theme';
        const nativeTheme = this.getNativeParameterByName(paramName);
        if (!nativeTheme || core.isDevMode()) {
            return;
        }
        return {
            themeUrl: this.setTheme(nativeTheme),
            customThemeUrl: this.setCustomTheme(nativeTheme)
        };
    }
    /** Assign css file corresponding to chosen theme from @sap-theming **/
    setTheme(theme) {
        return this._sanitizer.bypassSecurityTrustResourceUrl(`static-resources/dist/smartedit-container-new/${theme}/css_variables.css`);
    }
    /** Assign css file corresponding to chosen theme fundamental-styles **/
    setCustomTheme(theme) {
        return this._sanitizer.bypassSecurityTrustResourceUrl(`static-resources/dist/smartedit-container-new/${theme}/${theme}.css`);
    }
    /** @hidden */
    getNativeParameterByName(paramName) {
        paramName = paramName.replace(/[[\]]/g, '\\$&');
        const regex = new RegExp(`[?&]${paramName}(=([^&#]*)|&|#|$)`);
        const index = 2;
        const results = regex.exec(window.location.href);
        if (!results || !results[index]) {
            return '';
        }
        return decodeURIComponent(results[index].replace(/\+/g, ' '));
    }
    /** @hidden */
    propagateThemes(theme) {
        this.onThemeQueryParamChange.next({
            themeUrl: this.setTheme(theme),
            customThemeUrl: this.setCustomTheme(theme)
        });
    }
};
/* @ngInject */ exports.ThemesService = __decorate([
    core.Injectable()
    /**
     * @deprecated
     * Service providing theme switcher functionality.
     * Deprecated since 0.35.0 in favor of ThemingService from ThemingModule
     */
    ,
    __param(0, core.Optional()),
    __metadata("design:paramtypes", [router.ActivatedRoute,
        platformBrowser.DomSanitizer,
        IRestServiceFactory,
        exports.LanguageService])
], /* @ngInject */ exports.ThemesService);

exports.SlotRestrictionsService = class SlotRestrictionsService extends ISlotRestrictionsService {
    getAllComponentTypesSupportedOnPage() {
        return null;
    }
    getSlotRestrictions(slotId) {
        return null;
    }
    isSlotEditable(slotId) {
        return null;
    }
    isComponentAllowedInSlot(slot, dragInfo) {
        return null;
    }
};
exports.SlotRestrictionsService = __decorate([
    GatewayProxied('getAllComponentTypesSupportedOnPage', 'getSlotRestrictions', 'isSlotEditable', 'isComponentAllowedInSlot')
], exports.SlotRestrictionsService);

exports.EditorModalService = class EditorModalService extends IEditorModalService {
};
exports.EditorModalService = __decorate([
    GatewayProxied('open', 'openAndRerenderSlot', 'openGenericEditor')
], exports.EditorModalService);

/**
 * PageContentSlotsServiceModule provides methods to load and act on the contentSlots for the page loaded in the storefront.
 */
/* @ngInject */ exports.PageContentSlotsService = class /* @ngInject */ PageContentSlotsService {
    constructor(restServiceFactory, crossFrameEventService, pageInfoService) {
        this.crossFrameEventService = crossFrameEventService;
        this.pageInfoService = pageInfoService;
        this.resource = restServiceFactory.get(PAGES_CONTENT_SLOT_RESOURCE_URI);
        this.crossFrameEventService.subscribe(utils.EVENTS.PAGE_CHANGE, () => this.reloadPageContentSlots());
    }
    /**
     * This function fetches all the slots of the loaded page.
     *
     * @returns promise that resolves to a collection of content slots information for the loaded page.
     */
    getPageContentSlots() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.pageContentSlots) {
                yield this.reloadPageContentSlots();
            }
            return this.pageContentSlots;
        });
    }
    /**
     * Retrieves the slot status of the proved slotId. It can be one of TEMPLATE, PAGE and OVERRIDE.
     *
     * @param slotId of the slot
     *
     * @returns promise that resolves to the status of the slot.
     */
    getSlotStatus(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.getPageContentSlots();
            const matchedSlotData = lodash.first(this.pageContentSlots.filter((pageContentSlot) => pageContentSlot.slotId === slotId));
            return matchedSlotData ? matchedSlotData.slotStatus : null;
        });
    }
    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     *
     * @param slotId of the slot
     *
     * @returns promise that resolves to true if the slot is shared; Otherwise false.
     */
    isSlotShared(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.getPageContentSlots();
            const matchedSlotData = lodash.first(this.pageContentSlots.filter((pageContentSlot) => pageContentSlot.slotId === slotId));
            return matchedSlotData && matchedSlotData.slotShared;
        });
    }
    /**
     * Fetches content slot list from API
     */
    reloadPageContentSlots() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageId = yield this.pageInfoService.getPageUID();
            const pageContent = yield this.resource.get({ pageId });
            this.pageContentSlots = lodash.uniq(pageContent.pageContentSlotList || []);
        });
    }
};
/* @ngInject */ exports.PageContentSlotsService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IRestServiceFactory,
        exports.CrossFrameEventService,
        IPageInfoService])
], /* @ngInject */ exports.PageContentSlotsService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class PageInfoService extends IPageInfoService {
    getPageUID() {
        return null;
    }
    getPageUUID() {
        return null;
    }
    getCatalogVersionUUIDFromPage() {
        return null;
    }
}
PageInfoService.PATTERN_SMARTEDIT_CATALOG_VERSION_UUID = /smartedit-catalog-version-uuid\-(\S+)/;

/**
 * Handles all get/set component related operations
 */
exports.IComponentHandlerService = class IComponentHandlerService {
    constructor(yjQuery) {
        this.yjQuery = yjQuery;
    }
    /*
     * Reload inner page
     * */
    reloadInner() {
        'proxyFunction';
        return;
    }
    /**
     * Determines whether the component identified by the provided smarteditComponentId and smarteditComponentType
     * resides in a different catalog version to the one of the current page.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns flag that evaluates to true if the component resides in a catalog version different to
     * the one of the current page.  False otherwise.
     */
    isExternalComponent(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    smarteditComponentId, 
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    smarteditComponentType) {
        'proxyFunction';
        return Promise.resolve(true);
    }
    /**
     * Retrieves a handler on the smartEdit overlay div
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @returns The #smarteditoverlay JQuery Element
     */
    getOverlay() {
        return this.yjQuery('#' + OVERLAY_ID);
    }
    /**
     * determines whether the overlay is visible
     * This method can only be invoked from the smartEdit application and not the smartEdit iframe.
     *
     * @returns  true if the overlay is visible
     */
    isOverlayOn() {
        return this.getOverlay().length && this.getOverlay()[0].style.display !== 'none';
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param smarteditSlotId the slot id of the slot containing the component as per the smartEdit contract with the storefront
     * @param cssClass the css Class to further restrict the search on. This parameter is optional.
     *
     * @returns  a yjQuery object wrapping the searched component
     */
    getComponentUnderSlot(smarteditComponentId, smarteditComponentType, smarteditSlotId, cssClass) {
        const slotQuery = this.buildComponentQuery(smarteditSlotId, CONTENT_SLOT_TYPE);
        const componentQuery = this.buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass);
        const selector = `${slotQuery} ${componentQuery}`;
        return this.yjQuery(selector);
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param cssClass the css Class to further restrict the search on. This parameter is optional.
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getComponent(smarteditComponentId, smarteditComponentType, cssClass) {
        return this.yjQuery(this.buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass));
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type and slot ID
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOriginalComponentWithinSlot(smarteditComponentId, smarteditComponentType, slotId) {
        return this.getComponentUnderSlot(smarteditComponentId, smarteditComponentType, slotId, COMPONENT_CLASS);
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOriginalComponent(smarteditComponentId, smarteditComponentType) {
        return this.getComponent(smarteditComponentId, smarteditComponentType, COMPONENT_CLASS);
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the overlay layer identified by its smartEdit id, smartEdit type and slot ID
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOverlayComponentWithinSlot(smarteditComponentId, smarteditComponentType, slotId) {
        return this.getComponentUnderSlot(smarteditComponentId, smarteditComponentType, slotId, OVERLAY_COMPONENT_CLASS);
    }
    /**
     * Retrieves the yjQuery wrapper around the smartEdit component of the overlay layer corresponding to the storefront layer component passed as argument
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param originalComponent the DOM element in the storefront layer
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOverlayComponent(originalComponent) {
        const slotId = this.getParentSlotForComponent(originalComponent.parent());
        if (slotId) {
            return this.getComponentUnderSlot(originalComponent.attr(ID_ATTRIBUTE), originalComponent.attr(TYPE_ATTRIBUTE), slotId, OVERLAY_COMPONENT_CLASS);
        }
        else {
            return this.getComponent(originalComponent.attr(ID_ATTRIBUTE), originalComponent.attr(TYPE_ATTRIBUTE), OVERLAY_COMPONENT_CLASS);
        }
    }
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the overlay div identified by its smartEdit id, smartEdit type
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns a yjQuery object wrapping the searched component
     *
     */
    getComponentInOverlay(smarteditComponentId, smarteditComponentType) {
        return this.getComponent(smarteditComponentId, smarteditComponentType, OVERLAY_COMPONENT_CLASS);
    }
    /**
     * Retrieves the the slot ID for a given element
     *
     * @param component the yjQuery component for which to search the parent
     *
     * @returns the slot ID for that particular component
     */
    getParentSlotForComponent(component) {
        const parent = this.yjQuery(component).closest(`[${TYPE_ATTRIBUTE}=${CONTENT_SLOT_TYPE}]`);
        return parent.attr(ID_ATTRIBUTE);
    }
    /**
     * Retrieves the position of a component within a slot based on visible components in the given slotId.
     *
     * @param slotId the slot id as per the smartEdit contract with the storefront
     * @param componentId the component id as per the smartEdit contract with the storefront
     *
     * @returns the position of the component within a slot
     */
    getComponentPositionInSlot(slotId, componentId) {
        const components = this.getOriginalComponentsWithinSlot(slotId);
        return lodash__namespace.findIndex(components, (component) => this.getId(component) === componentId);
    }
    /**
     * Retrieves the yjQuery wrapper around a list of smartEdit components contained in the slot identified by the given slotId.
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns The list of searched components yjQuery objects
     */
    getOriginalComponentsWithinSlot(slotId) {
        return this.yjQuery(this.buildComponentsInSlotQuery(slotId));
    }
    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_ID_ATTRIBUTE when applicable and defaults to ID_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the slot operations related id
     */
    getSlotOperationRelatedId(component) {
        component = this.yjQuery(component);
        const containerId = component.attr(CONTAINER_ID_ATTRIBUTE);
        return containerId && component.attr(CONTAINER_TYPE_ATTRIBUTE)
            ? containerId
            : component.attr(ID_ATTRIBUTE);
    }
    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is {@link seConstantsModule.CONTAINER_ID_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.ID_ATTRIBUTE}
     *
     * @param component the yjQuery component for which to get the Uuid
     *
     * @returns the slot operations related Uuid
     */
    getSlotOperationRelatedUuid(component) {
        const containerId = this.yjQuery(component).attr(CONTAINER_ID_ATTRIBUTE);
        return containerId && this.yjQuery(component).attr(CONTAINER_TYPE_ATTRIBUTE)
            ? containerId
            : this.yjQuery(component).attr(UUID_ATTRIBUTE);
    }
    /**
     * Retrieves the direct smartEdit component parent of a given component.
     * The parent is fetched in the same layer (original storefront or smartEdit overlay) as the child
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param component the yjQuery component for which to search a parent
     *
     * @returns a yjQuery object wrapping the smae-layer parent component
     */
    getParent(component) {
        component = this.yjQuery(component);
        let parentClassToLookFor = null;
        if (component.hasClass(COMPONENT_CLASS)) {
            parentClassToLookFor = COMPONENT_CLASS;
        }
        else if (component.hasClass(OVERLAY_COMPONENT_CLASS)) {
            parentClassToLookFor = OVERLAY_COMPONENT_CLASS;
        }
        if (utils.stringUtils.isBlank(parentClassToLookFor)) {
            throw new Error('componentHandlerService.getparent.error.component.from.unknown.layer');
        }
        return component.closest(`.${parentClassToLookFor}[${ID_ATTRIBUTE}]` +
            `[${ID_ATTRIBUTE}!='${component.attr(ID_ATTRIBUTE)}']`);
    }
    /**
     * Returns the closest parent (or self) being a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to search a parent
     *
     * @returns The closest closest parent (or self) being a smartEdit component
     */
    getClosestSmartEditComponent(component) {
        return this.yjQuery(component).closest('.' + COMPONENT_CLASS);
    }
    /**
     * Determines whether a DOM/yjQuery element is a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to check if it's a SmartEdit component
     *
     * @returns true if DOM/yjQuery element is a smartEdit component
     */
    isSmartEditComponent(component) {
        return this.yjQuery(component).hasClass(COMPONENT_CLASS);
    }
    /**
     * Sets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to set the id
     * @param id the id to be set
     *
     * @returns component the yjQuery component
     */
    setId(component, id) {
        return this.yjQuery(component).attr(ID_ATTRIBUTE, id);
    }
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getId(component) {
        return this.yjQuery(component).attr(ID_ATTRIBUTE);
    }
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getUuid(component) {
        return this.yjQuery(component).attr(UUID_ATTRIBUTE);
    }
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getCatalogVersionUuid(component) {
        return this.yjQuery(component).attr(CATALOG_VERSION_UUID_ATTRIBUTE);
    }
    /**
     * Sets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to set the type
     * @param type the type to be set
     *
     * @returns component the yjQuery component
     */
    setType(component, type) {
        return this.yjQuery(component).attr(TYPE_ATTRIBUTE, type);
    }
    /**
     * Gets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the component type
     */
    getType(component) {
        return this.yjQuery(component).attr(TYPE_ATTRIBUTE);
    }
    /**
     * Gets the type that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_TYPE_ATTRIBUTE when applicable and defaults to TYPE_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the slot operations related type
     */
    getSlotOperationRelatedType(component) {
        const containerType = this.yjQuery(component).attr(CONTAINER_TYPE_ATTRIBUTE);
        return containerType && this.yjQuery(component).attr(CONTAINER_ID_ATTRIBUTE)
            ? containerType
            : this.yjQuery(component).attr(TYPE_ATTRIBUTE);
    }
    /**
     * Retrieves the DOM selector matching all smartEdit components that are not of type ContentSlot
     *
     * @returns components selector
     */
    getAllComponentsSelector() {
        return `.${COMPONENT_CLASS}[${TYPE_ATTRIBUTE}!='ContentSlot']`;
    }
    /**
     * Retrieves the DOM selector matching all smartEdit components that are of type ContentSlot
     *
     * @returns the slots selector
     */
    getAllSlotsSelector() {
        return `.${COMPONENT_CLASS}[${TYPE_ATTRIBUTE}='ContentSlot']`;
    }
    /**
     * Retrieves the the slot Uuid for a given element
     *
     * @param the DOM element which represents the component
     *
     * @returns the slot Uuid for that particular component
     */
    getParentSlotUuidForComponent(component) {
        return this.yjQuery(component)
            .closest(`[${TYPE_ATTRIBUTE}=${CONTENT_SLOT_TYPE}]`)
            .attr(UUID_ATTRIBUTE);
    }
    /**
     * @param pattern Pattern of class names to search for
     *
     * @returns Class attributes from the body element of the storefront
     */
    getBodyClassAttributeByRegEx(pattern) {
        try {
            const bodyClass = this.yjQuery('body').attr('class');
            return pattern.exec(bodyClass)[1];
        }
        catch (err) {
            throw new Error(JSON.stringify({
                name: 'InvalidStorefrontPageError',
                message: 'Error: the page is not a valid storefront page.'
            }));
        }
    }
    /**
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     * Get first level smartEdit component children for a given node, regardless how deep they are found.
     * The returned children may have different depths relatively to the parent:
     *
     * ### Example
     *
     * 	    <body>
     * 		    <div>
     * 			    <component smartedit-component-id="1">
     * 				    <component smartedit-component-id="1_1"></component>
     * 			    </component>
     * 			    <component smartedit-component-id="2">
     * 			    	<component smartedit-component-id="2_1"></component>
     * 		    	</component>
     * 		    </div>
     * 		    <component smartedit-component-id="3">
     * 			    <component smartedit-component-id="3_1"></component>
     * 		    </component>
     * 		    <div>
     * 			    <div>
     * 				    <component smartedit-component-id="4">
     * 					    <component smartedit-component-id="4_1"></component>
     * 				    </component>
     * 			    </div>
     * 		    </div>
     * 	    </body>
     *
     *
     * @param node any HTML/yjQuery Element
     *
     * @returns The list of first level smartEdit component children for a given node, regardless how deep they are found.
     */
    getFirstSmartEditComponentChildren(htmlElement) {
        const node = this.yjQuery(htmlElement);
        const root = node[0];
        if (!root) {
            return [];
        }
        const collection = Array.from(root.getElementsByClassName(COMPONENT_CLASS)).filter((element) => {
            let current = element.parentElement;
            /**
             * The filter goes up the tree to see if any of the parents
             * have the component selector. If it does, it's not a first child.
             *
             * If the parent is the htmlElement, the search stops there.
             */
            while (current !== root) {
                if (current.classList.contains(COMPONENT_CLASS)) {
                    return false;
                }
                current = current.parentElement;
            }
            return true;
        });
        return this.yjQuery(collection);
    }
    /**
     * Get component clone in overlay
     *
     * @param the DOM element which represents the component
     *
     * @returns The component clone in overlay
     */
    getComponentCloneInOverlay(component) {
        const elementUuid = component.attr(ELEMENT_UUID_ATTRIBUTE);
        return this.yjQuery(`.${OVERLAY_COMPONENT_CLASS}[${ELEMENT_UUID_ATTRIBUTE}='${elementUuid}']`);
    }
    /**
     * Get all the slot uids from the DOM
     *
     * @returns An array of slot ids in the DOM
     */
    getAllSlotUids() {
        const slots = this.yjQuery(this.getAllSlotsSelector());
        return Array.prototype.slice.call(slots.map((_index, slot) => this.getId(slot)));
    }
    buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass) {
        let query = '';
        query += cssClass ? '.' + cssClass : '';
        query += `[${ID_ATTRIBUTE}='${smarteditComponentId}']`;
        query += `[${TYPE_ATTRIBUTE}='${smarteditComponentType}']`;
        return query;
    }
    buildComponentsInSlotQuery(slotId) {
        let query = '';
        query += '.' + COMPONENT_CLASS;
        query += `[${ID_ATTRIBUTE}='${slotId}']`;
        query += `[${TYPE_ATTRIBUTE}='${CONTENT_SLOT_TYPE}']`;
        query += ' > ';
        query += `[${ID_ATTRIBUTE}]`;
        return query;
    }
};
exports.IComponentHandlerService = __decorate([
    __param(0, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function])
], exports.IComponentHandlerService);

/**
 * Provides methods to interact with the backend for shared slot information.
 */
/* @ngInject */ exports.SlotSharedService = class /* @ngInject */ SlotSharedService {
    constructor(pageContentSlotsService, pageInfoService, translateService, editorModalService, componentHandlerService, catalogService, sharedDataService) {
        this.pageContentSlotsService = pageContentSlotsService;
        this.pageInfoService = pageInfoService;
        this.translateService = translateService;
        this.editorModalService = editorModalService;
        this.componentHandlerService = componentHandlerService;
        this.catalogService = catalogService;
        this.sharedDataService = sharedDataService;
    }
    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     */
    isSlotShared(slotId) {
        return this.pageContentSlotsService.isSlotShared(slotId);
    }
    /**
     * Checks whether the given slot is global icon slot or not
     * Returns true if either of the below conditions are true.
     * If the current catalog is multicountry related and if the slot is external slot.
     * If the current catalog is multicountry related and the slot is not external slot but the current page is from parent catalog.
     */
    isGlobalSlot(slotId, slotType) {
        return __awaiter(this, void 0, void 0, function* () {
            const isExternalSlot = yield this.componentHandlerService.isExternalComponent(slotId, slotType);
            const [isCurrentCatalogMultiCountry, experience] = yield Promise.all([
                this.catalogService.isCurrentCatalogMultiCountry(),
                this.sharedDataService.get(EXPERIENCE_STORAGE_KEY)
            ]);
            // isMultiCountry -> Has the current site with current catalog have any parent catalog ?
            const isMultiCountry = isCurrentCatalogMultiCountry || false;
            // isCurrentPageFromParent -> Is the current page from the parent catalog ?
            const isCurrentPageFromParent = this.isCurrentPageFromParentCatalog(experience);
            return isMultiCountry && (isExternalSlot || (isCurrentPageFromParent && !isExternalSlot));
        });
    }
    /**
     * Sets the status of the disableSharedSlot feature
     */
    setSharedSlotEnablementStatus(status) {
        this.disableShareSlotStatus = status;
    }
    /**
     * Checks the status of the disableSharedSlot feature
     *
     */
    areSharedSlotsDisabled() {
        return this.disableShareSlotStatus;
    }
    /**
     * Replaces the global slot (multicountry related) based on the options selected in the "Replace Slot" generic editor.
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    replaceGlobalSlot(componentAttributes) {
        return __awaiter(this, void 0, void 0, function* () {
            this.validateComponentAttributes(componentAttributes);
            const cmsItem = yield this.constructCmsItemParameter(componentAttributes);
            const componentData = {
                title: 'se.cms.slot.shared.replace.editor.title',
                structure: {
                    attributes: [
                        {
                            cmsStructureType: 'SlotSharedSlotTypeField',
                            qualifier: 'isSlotCustom',
                            required: true
                        },
                        {
                            cmsStructureType: 'SlotSharedCloneActionField',
                            qualifier: 'cloneAction',
                            required: true
                        }
                    ]
                },
                contentApi: cmsitemsUri,
                saveLabel: 'se.cms.slot.shared.replace.editor.save',
                content: cmsItem,
                initialDirty: true
            };
            return this.editorModalService.openGenericEditor(componentData);
        });
    }
    /**
     * Replaces the shared slot (non-multicountry related) based on the options selected in the "Replace Slot" generic editor
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    replaceSharedSlot(componentAttributes) {
        return __awaiter(this, void 0, void 0, function* () {
            this.validateComponentAttributes(componentAttributes);
            const cmsItem = yield this.constructCmsItemParameter(componentAttributes);
            cmsItem.isSlotCustom = true;
            const componentData = {
                title: 'se.cms.slot.shared.replace.editor.title',
                structure: {
                    attributes: [
                        {
                            cmsStructureType: 'SlotSharedCloneActionField',
                            qualifier: 'cloneAction',
                            required: true
                        }
                    ]
                },
                contentApi: cmsitemsUri,
                saveLabel: 'se.cms.slot.shared.replace.editor.save',
                content: cmsItem,
                initialDirty: true
            };
            return this.editorModalService.openGenericEditor(componentData);
        });
    }
    constructCmsItemParameter(componentAttributes) {
        return __awaiter(this, void 0, void 0, function* () {
            const cloneText = this.translateService.instant('se.cms.slot.shared.clone');
            const pageUid = yield this.pageInfoService.getPageUID();
            const targetCatalogVersionUuid = yield this.pageInfoService.getCatalogVersionUUIDFromPage();
            const componentName = `${pageUid}-${componentAttributes.smarteditComponentId}-${cloneText}`;
            const cmsItem = {
                name: componentName,
                smarteditComponentId: componentAttributes.smarteditComponentId,
                contentSlotUuid: componentAttributes.smarteditComponentUuid,
                itemtype: componentAttributes.smarteditComponentType,
                catalogVersion: targetCatalogVersionUuid,
                pageUuid: pageUid,
                onlyOneRestrictionMustApply: false
            };
            return cmsItem;
        });
    }
    validateComponentAttributes(componentAttributes) {
        if (!componentAttributes) {
            throw new Error('Parameter: componentAttributes needs to be supplied!');
        }
        const validationAttributes = [
            'smarteditComponentId',
            'smarteditComponentUuid',
            'smarteditComponentType'
        ];
        const invalidAttr = validationAttributes.find((attr) => !componentAttributes[attr]);
        if (!!invalidAttr) {
            throw new Error(`Parameter: componentAttributes.${invalidAttr} needs to be supplied!`);
        }
    }
    isCurrentPageFromParentCatalog(experience) {
        var _a, _b;
        const pageContextCatalogVersionUuid = ((_a = experience === null || experience === void 0 ? void 0 : experience.pageContext) === null || _a === void 0 ? void 0 : _a.catalogVersionUuid) || '';
        const catalogDescriptorCatalogVersionUuid = ((_b = experience === null || experience === void 0 ? void 0 : experience.catalogDescriptor) === null || _b === void 0 ? void 0 : _b.catalogVersionUuid) || '';
        const isCurrentPageFromParent = catalogDescriptorCatalogVersionUuid !== pageContextCatalogVersionUuid;
        return isCurrentPageFromParent;
    }
};
/* @ngInject */ exports.SlotSharedService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PageContentSlotsService,
        IPageInfoService,
        core$1.TranslateService,
        IEditorModalService,
        exports.IComponentHandlerService,
        ICatalogService,
        utils.ISharedDataService])
], /* @ngInject */ exports.SlotSharedService);

/**
 * Provides functionality for Component Menu displayed from toolbar on Storefront.
 * For example it allows to determine Content Catalog Version based on which component within the menu are fetched.
 */
/* @ngInject */ exports.ComponentMenuService = class /* @ngInject */ ComponentMenuService {
    constructor(catalogService, experienceService, storageService) {
        this.catalogService = catalogService;
        this.experienceService = experienceService;
        this.storageService = storageService;
        this.SELECTED_CATALOG_VERSION_COOKIE_NAME = 'se_catalogmenu_catalogversion_cookie';
    }
    hasMultipleContentCatalogs() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageContext = yield this.getPageContext();
            const contentCatalogs = yield this.getContentCatalogs();
            const contentCatalog = contentCatalogs.find((catalog) => catalog.catalogId === pageContext.catalogId);
            return !!contentCatalog.parents && contentCatalog.parents.length > 0;
        });
    }
    /**
     * This method is used to retrieve the content catalogs of the site in the page context.
     */
    getContentCatalogs() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageContext = yield this.getPageContext();
            return pageContext ? this.catalogService.getContentCatalogsForSite(pageContext.siteId) : [];
        });
    }
    /**
     * Gets the list of catalog/catalog versions where components can be retrieved from for this page.
     */
    getValidContentCatalogVersions() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageContext = yield this.getPageContext();
            const contentCatalogs = yield this.getContentCatalogs();
            // Return 'active' catalog versions for content catalogs, except for the
            // catalog in the current experience.
            let catalogVersions = [];
            const contentCatalog = contentCatalogs.find((catalog) => catalog.catalogId === pageContext.catalogId);
            catalogVersions.push(this.getActiveOrCurrentVersionForCatalog(pageContext, contentCatalog));
            // You can add components from the online version of a parent content catalog to the staged version of a child content catalog
            const parentCatalogVersions = contentCatalog.parents.map((catalog) => this.getActiveOrCurrentVersionForCatalog(pageContext, catalog));
            catalogVersions = [...parentCatalogVersions, ...catalogVersions];
            return catalogVersions;
        });
    }
    // --------------------------------------------------------------------------------------------------
    // Cookie Management Methods
    // --------------------------------------------------------------------------------------------------
    getInitialCatalogVersion(catalogVersions) {
        return __awaiter(this, void 0, void 0, function* () {
            const rawValue = yield this.storageService.getValueFromLocalStorage(this.SELECTED_CATALOG_VERSION_COOKIE_NAME, false);
            const selectedCatalogVersionId = typeof rawValue === 'string' ? rawValue : null;
            const selectedCatalogVersion = catalogVersions.find((catalogVersion) => catalogVersion.id === selectedCatalogVersionId);
            return selectedCatalogVersion
                ? selectedCatalogVersion
                : catalogVersions[catalogVersions.length - 1];
        });
    }
    persistCatalogVersion(catalogVersionId) {
        return this.storageService.setValueInLocalStorage(this.SELECTED_CATALOG_VERSION_COOKIE_NAME, catalogVersionId, false);
    }
    /**
     * Gets the list of catalog/catalog versions where components can be retrieved from for this page.
     */
    getActiveOrCurrentVersionForCatalog(pageContext, catalog) {
        const catalogVersion = catalog.versions.find((version) => {
            if (pageContext.catalogId === catalog.catalogId) {
                return pageContext.catalogVersion === version.version;
            }
            return version.active;
        });
        return {
            isCurrentCatalog: pageContext.catalogVersion === catalogVersion.version,
            catalogName: catalog.name ? catalog.name : catalog.catalogName,
            catalogId: catalog.catalogId,
            catalogVersionId: catalogVersion.version,
            id: catalogVersion.uuid
        };
    }
    getPageContext() {
        return __awaiter(this, void 0, void 0, function* () {
            const experience = yield this.experienceService.getCurrentExperience();
            return experience.pageContext;
        });
    }
};
/* @ngInject */ exports.ComponentMenuService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [ICatalogService,
        IExperienceService,
        utils.IStorageService])
], /* @ngInject */ exports.ComponentMenuService);

const BYTE = 1024;
const FILE_VALIDATION_CONFIG = {
    /** A list of file types supported by the platform. */
    ACCEPTED_FILE_TYPES: {
        VIDEO: ['mp4', 'avi', 'x-msvideo', 'wmv', 'mpg', 'mpeg', 'flv'],
        IMAGE: ['jpeg', 'jpg', 'gif', 'bmp', 'tiff', 'tif', 'png', 'svg'],
        PDF_DOCUMENT: ['pdf'],
        DEFAULT: ['jpeg', 'jpg', 'gif', 'bmp', 'tiff', 'tif', 'png', 'svg']
    },
    /** default max of upload file size is 20MB, unit is MB. */
    DEFAULT_MAX_UPLOAD_FILE_SIZE: 20,
    /** A map of all the internationalization keys used by the file validation service. */
    I18N_KEYS: {
        FILE_TYPE_INVALID: 'se.upload.file.type.invalid',
        FILE_SIZE_INVALID: 'se.upload.file.size.invalid'
    }
};
/**
 * Validates if a specified file meets the required file type and file size constraints of SAP Hybris Commerce.
 */
/* @ngInject */ exports.FileValidationService = class /* @ngInject */ FileValidationService extends IFileValidation {
    constructor(fileMimeTypeService, fileValidatorFactory) {
        super();
        this.fileMimeTypeService = fileMimeTypeService;
        this.fileValidatorFactory = fileValidatorFactory;
        this.validators = [
            {
                subject: 'size',
                message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_SIZE_INVALID,
                validate: (size, maxSize) => size <= maxSize * BYTE * BYTE
            }
        ];
    }
    /**
     * Validates the specified file object against custom validator and its mimetype.
     * It appends the errors to the error context array provided or it creates a new error context array.
     *
     * @param file The web API file object to be validated.
     * @param context The contextual error array to append the errors to. It is an output parameter.
     * @returns A promise that resolves if the file is valid otherwise it rejects with a list of errors.
     */
    validate(file, maxUploadFileSize, errorsContext) {
        return __awaiter(this, void 0, void 0, function* () {
            this.fileValidatorFactory
                .build(this.validators)
                .validate(file, maxUploadFileSize, errorsContext);
            try {
                yield this.fileMimeTypeService.isFileMimeTypeValid(file);
                if (errorsContext.length > 0) {
                    return Promise.reject(errorsContext);
                }
                return Promise.resolve();
            }
            catch (_a) {
                errorsContext.push({
                    subject: 'type',
                    message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_TYPE_INVALID
                });
                return Promise.reject(errorsContext);
            }
        });
    }
};
/* @ngInject */ exports.FileValidationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.FileMimeTypeService,
        utils.FileValidatorFactory])
], /* @ngInject */ exports.FileValidationService);

/**
 * This service provides functionality to upload images and to fetch images by code for a specific catalog-catalog version combination.
 */
/* @ngInject */ exports.MediaUploaderService = class /* @ngInject */ MediaUploaderService extends IMediaToUpload {
    constructor(restServiceFactory) {
        super();
        this.restServiceFactory = restServiceFactory;
        this.mediaRestService = this.restServiceFactory.get(MEDIA_RESOURCE_URI);
    }
    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media) {
        var _a;
        const formData = new FormData();
        lodash__namespace.forEach(media, (value, key) => {
            formData.append(key, value);
        });
        return this.mediaRestService.save(formData, {
            headers: { enctype: 'multipart/form-data', fileSize: '' + ((_a = media === null || media === void 0 ? void 0 : media.file) === null || _a === void 0 ? void 0 : _a.size) }
        });
    }
};
/* @ngInject */ exports.MediaUploaderService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.MediaUploaderService);

/** Service to deal with media related CRUD operations. */
/* @ngInject */ exports.MediaService = class /* @ngInject */ MediaService extends IMediaService {
    constructor(restServiceFactory) {
        super();
        this.restServiceFactory = restServiceFactory;
    }
    /**
     * Fetches paged search results by making a REST call to the appropriate item endpoint.
     *
     * @param mask for filtering the search.
     * @param pageSize number of items in the page.
     * @param currentPage current page number.
     */
    getPage(mask, pageSize, currentPage, mimeType) {
        return __awaiter(this, void 0, void 0, function* () {
            const contextParams = {
                catalogId: CONTEXT_CATALOG,
                catalogVersion: CONTEXT_CATALOG_VERSION
            };
            const subParams = this.contextParamsToCommaSeparated(contextParams);
            const payload = {
                params: subParams,
                mask,
                pageSize,
                currentPage,
                mimeType
            };
            const response = yield this.restServiceFactory.get(MEDIAS_PATH).get(payload);
            const page = {
                results: [],
                pagination: response.pagination
            };
            page.results = response.media.map((media) => this.mediaDTOtoMedia(media));
            return page;
        });
    }
    /**
     * This method fetches a Media by its UUID.
     * @param uuid uuid of a media (contains catalog information).
     */
    getMedia(uuid) {
        return __awaiter(this, void 0, void 0, function* () {
            // identifier is added to URI and not getByid argument because it contains slashes
            const url = `${MEDIA_PATH}/${uuid}`;
            const media = yield this.restServiceFactory.get(url).get();
            return this.mediaDTOtoMedia(media);
        });
    }
    /**
     * Returns comma separated params that will be attached to payload.
     *
     * E.g. "catalogId:CURRENT_CONTEXT_CATALOG,catalogVersion:CURRENT_CONTEXT_CATALOG_VERSION"
     */
    contextParamsToCommaSeparated(params) {
        return Object.keys(params)
            .reduce((accumulator, next) => {
            accumulator += `,${next}:${params[next]}`;
            return accumulator;
        }, '')
            .substring(1);
    }
    mediaDTOtoMedia(mediaDTO) {
        const needlessPropertiesOfMediaDTO = ['catalogId', 'catalogVersion', 'uuid'];
        const media = {};
        Object.getOwnPropertyNames(mediaDTO)
            .filter((propertyName) => needlessPropertiesOfMediaDTO.indexOf(propertyName) === -1)
            .forEach((propertyName) => (media[propertyName] = mediaDTO[propertyName]));
        media.id = mediaDTO.uuid;
        return media;
    }
};
/* @ngInject */ exports.MediaService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.MediaService);

const DROPDOWN_MENU_ITEM_DATA = new core.InjectionToken('DROPDOWN_MENU_ITEM_DATA');
window.__smartedit__.addDecoratorPayload("Component", "DropdownMenuItemDefaultComponent", {
    selector: 'se-dropdown-menu-item-default',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<a fd-list-link class="se-dropdown-item" [ngClass]="data.dropdownItem.customCss" (click)="data.dropdownItem.callback(data.selectedItem, data.dropdownItem)"><span *ngIf="data.dropdownItem.icon" [ngClass]="data.dropdownItem.icon"></span> <span fd-list-title>{{ data.dropdownItem.key | translate }}</span></a>`
});
exports.DropdownMenuItemDefaultComponent = class DropdownMenuItemDefaultComponent {
    constructor(data) {
        this.data = data;
    }
};
exports.DropdownMenuItemDefaultComponent = __decorate([
    core.Component({
        selector: 'se-dropdown-menu-item-default',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<a fd-list-link class="se-dropdown-item" [ngClass]="data.dropdownItem.customCss" (click)="data.dropdownItem.callback(data.selectedItem, data.dropdownItem)"><span *ngIf="data.dropdownItem.icon" [ngClass]="data.dropdownItem.icon"></span> <span fd-list-title>{{ data.dropdownItem.key | translate }}</span></a>`
    }),
    __param(0, core.Inject(DROPDOWN_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [Object])
], exports.DropdownMenuItemDefaultComponent);

const COLLAPSIBLE_DEFAULT_CONFIGURATION = {
    expandedByDefault: false,
    iconAlignment: 'right',
    iconVisible: true
};

window.__smartedit__.addDecoratorPayload("Component", "CollapsibleContainerComponent", {
    selector: 'se-collapsible-container',
    template: `<div role="tab" class="collapsible-container__header" [ngClass]="{
        'collapsible-container__header--icon-right': isIconRight(),
        'collapsible-container__header--icon-left': isIconLeft()
    }" [attr.id]="headingId" [attr.aria-selected]="isOpen" (keypress)="handleKeypress($event)"><button type="button" tabindex="0" class="collapsible-container__header__title" (click)="toggle()" [attr.aria-expanded]="isOpen" [attr.aria-controls]="panelId"><ng-content select="se-collapsible-container-header"></ng-content></button> <a class="collapsible-container__header__button btn btn-link" *ngIf="configuration.iconVisible" [ngClass]="{
            'collapsible-container__header__button--expanded': isOpen
        }" [title]="isOpen
            ? ('se.ycollapsible.action.collapse' | translate)
            : ('se.ycollapsible.action.expand' | translate)
        " [attr.aria-expanded]="isOpen" (click)="toggle()"><span class="sap-icon--navigation-down-arrow collapsible-container__header__icon"></span></a></div><div #container class="collapsible-container__content panel" [ngStyle]="{ 'max-height.px': isOpen ? containerHeight : 0 }" [attr.id]="panelId" [attr.aria-labelledby]="headingId" [attr.aria-hidden]="!isOpen" role="tabpanel"><ng-content select="se-collapsible-container-content"></ng-content></div>`,
    styles: [`.collapsible-container__content{overflow:hidden;display:block;transition:max-height .4s ease;width:100%;background-color:#fff}.collapsible-container__header{max-width:100%;background-color:#fff;display:flex;align-items:center;min-height:50px;box-shadow:0 1px 1px rgba(0,0,0,.05)}.collapsible-container__header__title{text-align:left;background:0 0;border:0;outline:0;flex:1;display:flex;align-items:center;min-height:50px;text-decoration:none;user-select:none}.collapsible-container__header__title:hover{text-decoration:none}.collapsible-container__header__button{flex:0;transition:all .4s ease;font-size:20px}.collapsible-container__header__button--expanded{transform:rotate(180deg)}.collapsible-container__header--icon-right .collapsible-container__header__title{order:1}.collapsible-container__header--icon-right .collapsible-container__header__button{order:2}.collapsible-container__header--icon-left .collapsible-container__header__title{order:2}.collapsible-container__header--icon-left .collapsible-container__header__button{order:1}:host(.se-collapsible-container--noshadow) .collapsible-container__content.panel{box-shadow:none}`],
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
exports.CollapsibleContainerComponent = class CollapsibleContainerComponent {
    constructor(cdr) {
        this.cdr = cdr;
        this.getApi = new core.EventEmitter();
        this.containerHeight = 0;
        this.headingId = stringUtils.generateIdentifier();
        this.panelId = stringUtils.generateIdentifier();
        this.api = {
            isExpanded: () => this.isOpen
        };
    }
    set _container(container) {
        this.container = container;
        this.containerHeight = container.nativeElement.scrollHeight;
        if (!this.mutationObserver && this.container && this.container.nativeElement) {
            this.mutationObserver = new MutationObserver(() => {
                this.containerHeight = this.container.nativeElement.scrollHeight;
                if (!this.cdr.destroyed) {
                    this.cdr.detectChanges();
                }
            });
            this.mutationObserver.observe(this.container.nativeElement, {
                childList: true,
                subtree: true,
                attributes: true
            });
        }
    }
    ngOnDestroy() {
        this.mutationObserver.disconnect();
    }
    ngOnChanges(changes) {
        if (changes.configuration) {
            this.configure();
        }
    }
    ngOnInit() {
        this.configure();
        this.isOpen = this.configuration.expandedByDefault;
        this.getApi.emit(this.api);
    }
    toggle() {
        this.isOpen = !this.isOpen;
    }
    handleKeypress(event) {
        if (event.code === 'Enter') {
            this.toggle();
        }
    }
    isIconRight() {
        return this.configuration.iconAlignment === 'right';
    }
    isIconLeft() {
        return this.configuration.iconAlignment === 'left';
    }
    configure() {
        this.configuration = Object.assign(Object.assign({}, COLLAPSIBLE_DEFAULT_CONFIGURATION), (this.configuration || {}));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.CollapsibleContainerComponent.prototype, "configuration", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.CollapsibleContainerComponent.prototype, "getApi", void 0);
__decorate([
    core.ViewChild('container', { static: true }),
    __metadata("design:type", core.ElementRef),
    __metadata("design:paramtypes", [core.ElementRef])
], exports.CollapsibleContainerComponent.prototype, "_container", null);
exports.CollapsibleContainerComponent = __decorate([
    core.Component({
        selector: 'se-collapsible-container',
        template: `<div role="tab" class="collapsible-container__header" [ngClass]="{
        'collapsible-container__header--icon-right': isIconRight(),
        'collapsible-container__header--icon-left': isIconLeft()
    }" [attr.id]="headingId" [attr.aria-selected]="isOpen" (keypress)="handleKeypress($event)"><button type="button" tabindex="0" class="collapsible-container__header__title" (click)="toggle()" [attr.aria-expanded]="isOpen" [attr.aria-controls]="panelId"><ng-content select="se-collapsible-container-header"></ng-content></button> <a class="collapsible-container__header__button btn btn-link" *ngIf="configuration.iconVisible" [ngClass]="{
            'collapsible-container__header__button--expanded': isOpen
        }" [title]="isOpen
            ? ('se.ycollapsible.action.collapse' | translate)
            : ('se.ycollapsible.action.expand' | translate)
        " [attr.aria-expanded]="isOpen" (click)="toggle()"><span class="sap-icon--navigation-down-arrow collapsible-container__header__icon"></span></a></div><div #container class="collapsible-container__content panel" [ngStyle]="{ 'max-height.px': isOpen ? containerHeight : 0 }" [attr.id]="panelId" [attr.aria-labelledby]="headingId" [attr.aria-hidden]="!isOpen" role="tabpanel"><ng-content select="se-collapsible-container-content"></ng-content></div>`,
        styles: [`.collapsible-container__content{overflow:hidden;display:block;transition:max-height .4s ease;width:100%;background-color:#fff}.collapsible-container__header{max-width:100%;background-color:#fff;display:flex;align-items:center;min-height:50px;box-shadow:0 1px 1px rgba(0,0,0,.05)}.collapsible-container__header__title{text-align:left;background:0 0;border:0;outline:0;flex:1;display:flex;align-items:center;min-height:50px;text-decoration:none;user-select:none}.collapsible-container__header__title:hover{text-decoration:none}.collapsible-container__header__button{flex:0;transition:all .4s ease;font-size:20px}.collapsible-container__header__button--expanded{transform:rotate(180deg)}.collapsible-container__header--icon-right .collapsible-container__header__title{order:1}.collapsible-container__header--icon-right .collapsible-container__header__button{order:2}.collapsible-container__header--icon-left .collapsible-container__header__title{order:2}.collapsible-container__header--icon-left .collapsible-container__header__button{order:1}:host(.se-collapsible-container--noshadow) .collapsible-container__content.panel{box-shadow:none}`],
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [core.ChangeDetectorRef])
], exports.CollapsibleContainerComponent);

window.__smartedit__.addDecoratorPayload("Component", "CollapsibleContainerContentComponent", {
    selector: 'se-collapsible-container-content',
    template: ` <ng-content></ng-content> `
});
exports.CollapsibleContainerContentComponent = class CollapsibleContainerContentComponent {
};
exports.CollapsibleContainerContentComponent = __decorate([
    core.Component({
        selector: 'se-collapsible-container-content',
        template: ` <ng-content></ng-content> `
    })
], exports.CollapsibleContainerContentComponent);

window.__smartedit__.addDecoratorPayload("Component", "CollapsibleContainerHeaderComponent", {
    selector: 'se-collapsible-container-header',
    template: ` <ng-content></ng-content> `
});
exports.CollapsibleContainerHeaderComponent = class CollapsibleContainerHeaderComponent {
};
exports.CollapsibleContainerHeaderComponent = __decorate([
    core.Component({
        selector: 'se-collapsible-container-header',
        template: ` <ng-content></ng-content> `
    })
], exports.CollapsibleContainerHeaderComponent);

exports.CollapsibleContainerModule = class CollapsibleContainerModule {
};
exports.CollapsibleContainerModule = __decorate([
    core.NgModule({
        imports: [core$1.TranslateModule.forChild(), common.CommonModule],
        declarations: [
            exports.CollapsibleContainerComponent,
            exports.CollapsibleContainerContentComponent,
            exports.CollapsibleContainerHeaderComponent
        ],
        entryComponents: [
            exports.CollapsibleContainerComponent,
            exports.CollapsibleContainerContentComponent,
            exports.CollapsibleContainerHeaderComponent
        ],
        exports: [
            exports.CollapsibleContainerComponent,
            exports.CollapsibleContainerContentComponent,
            exports.CollapsibleContainerHeaderComponent
        ]
    })
], exports.CollapsibleContainerModule);

window.__smartedit__.addDecoratorPayload("Component", "WaitDialogComponent", {
    template: `
        <fd-dialog id="se-busy-indicator-dialog">
            <fd-dialog-body>
                <div class="panel panel-default ySEPanelSpinner">
                    <div class="panel-body">
                        <div class="spinner ySESpinner">
                            <div class="fd-busy-indicator-extended">
                                <fd-busy-indicator
                                    [loading]="true"
                                    size="l"
                                    [label]="
                                        modalRef.data.customLoadingMessageLocalizedKey ||
                                            'se.wait.dialog.message' | translate
                                    "></fd-busy-indicator>
                            </div>
                        </div>
                    </div>
                </div>
            </fd-dialog-body>
        </fd-dialog>
    `
    // selector: 'wait-dialog'
});
exports.WaitDialogComponent = class WaitDialogComponent {
    constructor(modalRef) {
        this.modalRef = modalRef;
    }
};
exports.WaitDialogComponent = __decorate([
    core.Component({
        template: `
        <fd-dialog id="se-busy-indicator-dialog">
            <fd-dialog-body>
                <div class="panel panel-default ySEPanelSpinner">
                    <div class="panel-body">
                        <div class="spinner ySESpinner">
                            <div class="fd-busy-indicator-extended">
                                <fd-busy-indicator
                                    [loading]="true"
                                    size="l"
                                    [label]="
                                        modalRef.data.customLoadingMessageLocalizedKey ||
                                            'se.wait.dialog.message' | translate
                                    "></fd-busy-indicator>
                            </div>
                        </div>
                    </div>
                </div>
            </fd-dialog-body>
        </fd-dialog>
    `
        // selector: 'wait-dialog'
    }),
    __metadata("design:paramtypes", [core$2.DialogRef])
], exports.WaitDialogComponent);

class TabsSelectAdapter {
    static transform(item, id) {
        return {
            id,
            label: item.title,
            value: item,
            listItemClassName: item.hasErrors && 'sm-tab-error'
        };
    }
}

window.__smartedit__.addDecoratorPayload("Component", "TabsComponent", {
    selector: 'se-tabs',
    template: `<div class="se-tabset-wrapper"><nav fd-tab-nav class="nav nav-tabs se-tabset" role="tablist" *ngIf="isInitialized"><ng-container *ngIf="tabsList.length !== numTabsDisplayed"><ng-container *ngFor="let tab of (getVisibleTabs() | async); trackBy: trackTabById" [ngTemplateOutlet]="tabsetLink" [ngTemplateOutletContext]="{ $implicit: tab }"></ng-container></ng-container><ng-container *ngIf="tabsList && tabsList.length == numTabsDisplayed"><ng-container *ngFor="let tab of tabsList; trackBy: trackTabById" [ngTemplateOutlet]="tabsetLink" [ngTemplateOutletContext]="{ $implicit: tab }"></ng-container></ng-container><su-select class="se-tabset__select" *ngIf="tabsList.length > numTabsDisplayed && (getDropdownTabs() | async)" [items]="dropdownTabs" [isKeyboardControlEnabled]="false" [hasCustomTrigger]="true" (onItemSelected)="selectTab($event.value)"><li fd-list-item [ngClass]="{'active': isActiveInMoreTab()}" su-select-custom-trigger><a fd-list-link [ngClass]="{'sm-tab-error': dropDownHasErrors()}" class="dropdown-toggle"><span fd-list-title *ngIf="!isActiveInMoreTab()" class="multi-tabs__more-span">{{ 'se.ytabset.tabs.more' | translate }} </span><span fd-list-title *ngIf="isActiveInMoreTab()" class="multi-tabs__more-span">{{selectedTab.title | translate}} </span><span class="caret"></span></a></li></su-select><ng-template #tabsetLink let-tab><div fd-tab-item [ngClass]="{
                    'active': tab.id === selectedTab.id,
                    'se-tabset__tab--disabled': tab.disabled,
                    'sm-tab-error': tab.hasErrors
                }" [attr.tab-id]="tab.id" class="se-tabset__tab"><a fd-tab-link [ngClass]="{'sm-tab-error': tab.hasErrors}" class="se-tabset__link mycustomtabset" (click)="selectTab(tab)" *ngIf="!tab.message">{{tab.title | translate}}</a><se-tooltip [triggers]="['mouseover']" *ngIf="tab.message"><a fd-list-title se-tooltip-trigger [ngClass]="{'sm-tab-error': tab.hasErrors}" class="se-tabset__link" (click)="selectTab(tab)">{{tab.title | translate}} </a><span fd-list-title se-tooltip-body>{{ tab.message }}</span></se-tooltip></div></ng-template></nav><div class="se-tab-set__content-wrapper" *ngIf="selectedTab"><ng-container *ngFor="let tab of tabsList; trackBy: trackTabById"><se-tab [hidden]="tab.id !== selectedTab.id" [attr.tab-id]="tab.id" [tab]="tab" [model]="model"></se-tab></ng-container></div></div>`
});
exports.TabsComponent = class TabsComponent {
    constructor(userTrackingService) {
        this.userTrackingService = userTrackingService;
        this.tabsList = [];
        this.onTabSelected = new core.EventEmitter();
        this.tabChangedStream = new rxjs.BehaviorSubject(null);
    }
    get isInitialized() {
        return !!this.tabsList && this.tabsList.length > 1 && !!this.selectedTab;
    }
    isActiveInMoreTab() {
        return (this.tabsList.findIndex((tab) => tab.id === this.selectedTab.id) >=
            this.numTabsDisplayed - 1);
    }
    ngOnChanges(changes) {
        const hasTabsInitialized = (changes.tabsList &&
            changes.tabsList.currentValue &&
            !changes.tabsList.previousValue) ||
            (changes.tabsList && changes.tabsList.firstChange && !!changes.tabsList.currentValue);
        const hasTabsChanged = changes.tabsList && !!changes.tabsList.previousValue && !!changes.tabsList.currentValue;
        if (this.tabsList) {
            const active = this.tabsList.find((tab) => tab.active) || this.tabsList[0];
            if (!this.selectedTab || (this.selectedTab && this.selectedTab.id !== active.id)) {
                this.selectedTab = active;
                this.selectedTab.active = true;
            }
        }
        if (hasTabsInitialized) {
            this.tabsList = this.tabsList.map((tab) => (Object.assign(Object.assign({}, tab), { active: false, hasErrors: false })));
            this.tabChangedStream.next();
        }
        if (hasTabsChanged) {
            this.tabChangedStream.next();
        }
        this.getDropdownTabs().subscribe((tabs) => {
            this.dropdownTabs = tabs;
        });
    }
    selectTab(tabToSelect) {
        if (tabToSelect && tabToSelect.id !== this.selectedTab.id) {
            this.userTrackingService.trackingUserAction(USER_TRACKING_FUNCTIONALITY.ADD_COMPONENT, tabToSelect.title);
            if (!this.selectedTab.active) {
                this.findSelectedTab();
            }
            this.selectedTab.active = false;
            this.selectedTab = tabToSelect;
            this.selectedTab.active = true;
            this.onTabSelected.emit(this.selectedTab.id);
        }
    }
    dropDownHasErrors() {
        const tabsInDropDown = this.tabsList.slice(this.numTabsDisplayed - 1);
        return tabsInDropDown.some((tab) => tab.hasErrors);
    }
    findSelectedTab() {
        const selectedTab = this.tabsList.find((tab) => tab.active);
        if (selectedTab) {
            this.selectedTab = selectedTab;
        }
    }
    getDropdownTabs() {
        return this.tabChangedStream.pipe(operators.map(() => (this.tabsList || [])
            .slice(this.numTabsDisplayed - 1)
            .map(TabsSelectAdapter.transform)), operators.distinctUntilChanged((a, b) => lodash.isEqual(a, b)));
    }
    getVisibleTabs() {
        return this.tabChangedStream.pipe(operators.map(() => (this.tabsList || []).slice(0, this.numTabsDisplayed - 1)), operators.distinctUntilChanged((a, b) => lodash.isEqual(a, b)));
    }
    trackTabById(index) {
        return index;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TabsComponent.prototype, "model", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.TabsComponent.prototype, "tabsList", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.TabsComponent.prototype, "numTabsDisplayed", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.TabsComponent.prototype, "onTabSelected", void 0);
exports.TabsComponent = __decorate([
    core.Component({
        selector: 'se-tabs',
        template: `<div class="se-tabset-wrapper"><nav fd-tab-nav class="nav nav-tabs se-tabset" role="tablist" *ngIf="isInitialized"><ng-container *ngIf="tabsList.length !== numTabsDisplayed"><ng-container *ngFor="let tab of (getVisibleTabs() | async); trackBy: trackTabById" [ngTemplateOutlet]="tabsetLink" [ngTemplateOutletContext]="{ $implicit: tab }"></ng-container></ng-container><ng-container *ngIf="tabsList && tabsList.length == numTabsDisplayed"><ng-container *ngFor="let tab of tabsList; trackBy: trackTabById" [ngTemplateOutlet]="tabsetLink" [ngTemplateOutletContext]="{ $implicit: tab }"></ng-container></ng-container><su-select class="se-tabset__select" *ngIf="tabsList.length > numTabsDisplayed && (getDropdownTabs() | async)" [items]="dropdownTabs" [isKeyboardControlEnabled]="false" [hasCustomTrigger]="true" (onItemSelected)="selectTab($event.value)"><li fd-list-item [ngClass]="{'active': isActiveInMoreTab()}" su-select-custom-trigger><a fd-list-link [ngClass]="{'sm-tab-error': dropDownHasErrors()}" class="dropdown-toggle"><span fd-list-title *ngIf="!isActiveInMoreTab()" class="multi-tabs__more-span">{{ 'se.ytabset.tabs.more' | translate }} </span><span fd-list-title *ngIf="isActiveInMoreTab()" class="multi-tabs__more-span">{{selectedTab.title | translate}} </span><span class="caret"></span></a></li></su-select><ng-template #tabsetLink let-tab><div fd-tab-item [ngClass]="{
                    'active': tab.id === selectedTab.id,
                    'se-tabset__tab--disabled': tab.disabled,
                    'sm-tab-error': tab.hasErrors
                }" [attr.tab-id]="tab.id" class="se-tabset__tab"><a fd-tab-link [ngClass]="{'sm-tab-error': tab.hasErrors}" class="se-tabset__link mycustomtabset" (click)="selectTab(tab)" *ngIf="!tab.message">{{tab.title | translate}}</a><se-tooltip [triggers]="['mouseover']" *ngIf="tab.message"><a fd-list-title se-tooltip-trigger [ngClass]="{'sm-tab-error': tab.hasErrors}" class="se-tabset__link" (click)="selectTab(tab)">{{tab.title | translate}} </a><span fd-list-title se-tooltip-body>{{ tab.message }}</span></se-tooltip></div></ng-template></nav><div class="se-tab-set__content-wrapper" *ngIf="selectedTab"><ng-container *ngFor="let tab of tabsList; trackBy: trackTabById"><se-tab [hidden]="tab.id !== selectedTab.id" [attr.tab-id]="tab.id" [tab]="tab" [model]="model"></se-tab></ng-container></div></div>`
    }),
    __metadata("design:paramtypes", [IUserTrackingService])
], exports.TabsComponent);

const TAB_DATA = new core.InjectionToken('tab-data');
window.__smartedit__.addDecoratorPayload("Component", "TabComponent", {
    selector: 'se-tab',
    template: `<ng-container *ngComponentOutlet="tab.component; injector: tabInjector"></ng-container>`
});
exports.TabComponent = class TabComponent {
    constructor(injector) {
        this.injector = injector;
        this.scopeStream = new rxjs.BehaviorSubject(null);
    }
    ngOnChanges(changes) {
        const modelChanged = changes.model && !lodash.isEqual(changes.model.previousValue, changes.model.currentValue);
        const tabChanged = changes.tab && !lodash.isEqual(changes.tab.previousValue, changes.tab.currentValue);
        if (tabChanged || modelChanged) {
            this.scopeStream.next({ model: this.model, tabId: this.tab.id, tab: this.tab });
        }
    }
    ngOnInit() {
        this.tabInjector = core.Injector.create({
            providers: [
                {
                    provide: TAB_DATA,
                    useValue: { model: this.model, tabId: this.tab.id, tab: this.tab }
                }
            ],
            parent: this.injector
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TabComponent.prototype, "tab", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TabComponent.prototype, "model", void 0);
exports.TabComponent = __decorate([
    core.Component({
        selector: 'se-tab',
        template: `<ng-container *ngComponentOutlet="tab.component; injector: tabInjector"></ng-container>`
    }),
    __metadata("design:paramtypes", [core.Injector])
], exports.TabComponent);

exports.FundamentalsModule = class FundamentalsModule {
};
exports.FundamentalsModule = __decorate([
    core.NgModule({
        imports: [
            core$2.DialogModule,
            core$2.ButtonModule,
            animations.BrowserAnimationsModule,
            forms.FormsModule,
            core$2.PopoverModule,
            core$2.MenuModule,
            core$2.MessageStripModule,
            core$2.PaginationModule,
            core$2.BusyIndicatorModule
        ],
        exports: [
            core$2.DialogModule,
            core$2.ButtonModule,
            core$2.FormModule,
            core$2.PopoverModule,
            core$2.MenuModule,
            core$2.MessageStripModule,
            core$2.PaginationModule,
            core$2.BusyIndicatorModule
        ]
    })
], exports.FundamentalsModule);

window.__smartedit__.addDecoratorPayload("Component", "TooltipComponent", {
    selector: 'se-tooltip',
    template: `
        <fd-popover
            [triggers]="triggers"
            [placement]="placement"
            [appendTo]="appendTo"
            [noArrow]="!isChevronVisible"
            [additionalBodyClass]="'se-tooltip-container'"
            [ngClass]="additionalClasses"
            class="se-tooltip">
            <fd-popover-control>
                <ng-content select="[se-tooltip-trigger]"></ng-content>
            </fd-popover-control>
            <fd-popover-body>
                <div class="popover se-popover">
                    <h3 class="se-popover__title" *ngIf="title">{{ title | translate }}</h3>

                    <div class="se-popover__content">
                        <ng-content select="[se-tooltip-body]"></ng-content>
                    </div>
                </div>
            </fd-popover-body>
        </fd-popover>
    `
});
/**
 * Used to display content in a popover after trigger is applied
 *
 * ### Example
 *
 *      <se-tooltip [triggers]="mouseover">
 *          <span se-tooltip-trigger>Hover me</span>
 *          <p se-tooltip-body>Content</p>
 *      </se-tooltip>
 */
let TooltipComponent = class TooltipComponent {
    ngAfterViewInit() {
        // Ensures the tooltip position is set properly.
        //
        // For some components (such as PageDisplayStatusComponent), there is an issue with Popper.js (used by fundamental-ngx) which causes incorrect calculation of the position.
        // This is a temporary workaround which should be rechallenged when upgrading fundamental-ngx to the newer versions.
        this.popoverIsOpenChangeSubscription = this.popover.isOpenChange.subscribe((isOpen) => {
            if (isOpen) {
                setTimeout(() => this.popover.refreshPosition());
            }
        });
    }
    ngOnDestroy() {
        this.popoverIsOpenChangeSubscription.unsubscribe();
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], TooltipComponent.prototype, "triggers", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], TooltipComponent.prototype, "placement", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], TooltipComponent.prototype, "title", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.ElementRef)
], TooltipComponent.prototype, "appendTo", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], TooltipComponent.prototype, "isChevronVisible", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], TooltipComponent.prototype, "additionalClasses", void 0);
__decorate([
    core.ViewChild(core$2.PopoverComponent, { static: false }),
    __metadata("design:type", core$2.PopoverComponent)
], TooltipComponent.prototype, "popover", void 0);
TooltipComponent = __decorate([
    core.Component({
        selector: 'se-tooltip',
        template: `
        <fd-popover
            [triggers]="triggers"
            [placement]="placement"
            [appendTo]="appendTo"
            [noArrow]="!isChevronVisible"
            [additionalBodyClass]="'se-tooltip-container'"
            [ngClass]="additionalClasses"
            class="se-tooltip">
            <fd-popover-control>
                <ng-content select="[se-tooltip-trigger]"></ng-content>
            </fd-popover-control>
            <fd-popover-body>
                <div class="popover se-popover">
                    <h3 class="se-popover__title" *ngIf="title">{{ title | translate }}</h3>

                    <div class="se-popover__content">
                        <ng-content select="[se-tooltip-body]"></ng-content>
                    </div>
                </div>
            </fd-popover-body>
        </fd-popover>
    `
    })
], TooltipComponent);

exports.TooltipModule = class TooltipModule {
};
exports.TooltipModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.FundamentalsModule, utils.TranslationModule.forChild()],
        declarations: [TooltipComponent],
        entryComponents: [TooltipComponent],
        exports: [TooltipComponent]
    })
], exports.TooltipModule);

exports.TabsModule = class TabsModule {
};
exports.TabsModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            utils.SelectModule,
            exports.TooltipModule,
            tabs.TabsModule,
            list.ListModule,
            utils.TranslationModule.forChild()
        ],
        declarations: [exports.TabsComponent, exports.TabComponent],
        entryComponents: [exports.TabsComponent, exports.TabComponent],
        exports: [exports.TabsComponent, exports.TabComponent]
    })
], exports.TabsModule);

const DATA_TABLE_COMPONENT_DATA = new core.InjectionToken('DATA_TABLE_COMPONENT_DATA');
window.__smartedit__.addDecoratorPayload("Component", "DataTableComponent", {
    selector: 'se-data-table',
    styles: [`.se-paged-list-table.fd-table th{cursor:pointer}`],
    template: `<table class="se-paged-list-table fd-table"><thead><tr><th *ngFor="let column of columns" (click)="sortColumn(column)" [ngStyle]="{'width.': columnWidth + '%'}" [ngClass]="'se-paged-list__header-'+column.property" class="se-paged-list__header">{{ column.i18n | translate }} <span class="se-data-table__sort" *ngIf="visibleSortingHeader === column.property" [ngClass]="{ 
                        'sap-icon--sort-descending': headersSortingState[column.property],
                        'sap-icon--sort-ascending': !headersSortingState[column.property] }"></span></th></tr></thead><tbody class="se-paged-list__table-body"><tr *ngFor="let item of items" class="se-paged-list-item"><td *ngFor="let column of columns" [ngClass]="'se-paged-list-item-'+column.property"><se-data-table-renderer [column]="column" [item]="item"></se-data-table-renderer></td></tr></tbody></table>`
});
exports.DataTableComponent = class DataTableComponent {
    constructor() {
        this.onSortColumn = new core.EventEmitter();
        this.headersSortingState = {};
    }
    ngOnInit() {
        this._configure();
    }
    sortColumn(columnKey) {
        if (columnKey.sortable) {
            this.columnToggleReversed = !this.columnToggleReversed;
            this.headersSortingState[columnKey.property] = this.columnToggleReversed;
            this.visibleSortingHeader = columnKey.property;
            this.currentPage = 1;
            this.internalSortBy = columnKey.property;
            this.columnSortMode = this.columnToggleReversed
                ? exports.SortDirections.Descending
                : exports.SortDirections.Ascending;
            this.onSortColumn.emit({
                $columnKey: columnKey,
                $columnSortMode: this.columnSortMode
            });
        }
    }
    _configure() {
        const numberOfWidth = 100;
        this.columnWidth = numberOfWidth / this.columns.length;
        this.columnToggleReversed = this.sortStatus.reversed;
        this.columnSortMode = this.sortStatus.reversed
            ? exports.SortDirections.Descending
            : exports.SortDirections.Ascending;
        this.headersSortingState[this.sortStatus.internalSortBy] = this.config.reversed;
        this.visibleSortingHeader = this.sortStatus.internalSortBy;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.DataTableComponent.prototype, "columns", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DataTableComponent.prototype, "config", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.DataTableComponent.prototype, "items", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DataTableComponent.prototype, "sortStatus", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.DataTableComponent.prototype, "onSortColumn", void 0);
exports.DataTableComponent = __decorate([
    core.Component({
        selector: 'se-data-table',
        styles: [`.se-paged-list-table.fd-table th{cursor:pointer}`],
        template: `<table class="se-paged-list-table fd-table"><thead><tr><th *ngFor="let column of columns" (click)="sortColumn(column)" [ngStyle]="{'width.': columnWidth + '%'}" [ngClass]="'se-paged-list__header-'+column.property" class="se-paged-list__header">{{ column.i18n | translate }} <span class="se-data-table__sort" *ngIf="visibleSortingHeader === column.property" [ngClass]="{ 
                        'sap-icon--sort-descending': headersSortingState[column.property],
                        'sap-icon--sort-ascending': !headersSortingState[column.property] }"></span></th></tr></thead><tbody class="se-paged-list__table-body"><tr *ngFor="let item of items" class="se-paged-list-item"><td *ngFor="let column of columns" [ngClass]="'se-paged-list-item-'+column.property"><se-data-table-renderer [column]="column" [item]="item"></se-data-table-renderer></td></tr></tbody></table>`
    })
], exports.DataTableComponent);

window.__smartedit__.addDecoratorPayload("Component", "DataTableRendererComponent", {
    selector: 'se-data-table-renderer',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `
        <div *ngIf="column.component; else cellText">
            <ng-container
                *ngComponentOutlet="column.component; injector: componentInjector"></ng-container>
        </div>
        <ng-template #cellText
            ><span>{{ item[column.property] }}</span></ng-template
        >
    `
});
let DataTableRendererComponent = class DataTableRendererComponent {
    constructor(injector) {
        this.injector = injector;
    }
    ngOnChanges(changes) {
        const columnChanges = changes.column;
        if (columnChanges === null || columnChanges === void 0 ? void 0 : columnChanges.currentValue.component) {
            this.createInjector();
        }
    }
    createInjector() {
        const { column, item } = this;
        this.componentInjector = core.Injector.create({
            providers: [
                {
                    provide: DATA_TABLE_COMPONENT_DATA,
                    useValue: { column, item }
                }
            ],
            parent: this.injector
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], DataTableRendererComponent.prototype, "column", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], DataTableRendererComponent.prototype, "item", void 0);
DataTableRendererComponent = __decorate([
    core.Component({
        selector: 'se-data-table-renderer',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `
        <div *ngIf="column.component; else cellText">
            <ng-container
                *ngComponentOutlet="column.component; injector: componentInjector"></ng-container>
        </div>
        <ng-template #cellText
            ><span>{{ item[column.property] }}</span></ng-template
        >
    `
    }),
    __metadata("design:paramtypes", [core.Injector])
], DataTableRendererComponent);

let DataTableModule = class DataTableModule {
};
DataTableModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, utils.TranslationModule.forChild()],
        declarations: [exports.DataTableComponent, DataTableRendererComponent],
        entryComponents: [exports.DataTableComponent, DataTableRendererComponent],
        exports: [exports.DataTableComponent, DataTableRendererComponent]
    })
], DataTableModule);

window.__smartedit__.addDecoratorPayload("Component", "DropdownMenuComponent", {
    selector: 'se-dropdown-menu',
    template: `<fd-popover [triggers]="[]" [isOpen]="isOpen" [placement]="placement || 'bottom-end'" class="se-dropdown-more-menu"><fd-popover-control><div #toggleMenu><div *ngIf="!useProjectedAnchor; else projectedAnchor;" class="sap-icon--overflow se-dropdown-more-menu__toggle" title="{{ 'se.contextmenu.title.more' | translate }}"></div><ng-template #projectedAnchor><ng-content></ng-content></ng-template></div></fd-popover-control><fd-popover-body><ul fd-list class="se-dropdown-menu__list" [ngClass]="additionalClasses"><se-dropdown-menu-item *ngFor="let dropdownItem of clonedDropdownItems" [dropdownItem]="dropdownItem" [selectedItem]="selectedItem" (selectedItemChange)="selectedItemChange.emit($event)" class="fd-list__byline-left"></se-dropdown-menu-item></ul><div *ngIf="message" class="se-information">{{ message | translate }}</div></fd-popover-body></fd-popover>`,
    styles: [`se-dropdown-menu .fd-popover__popper{border:0}.se-information{padding:12px}.se-dropdown-more-menu__toggle{color:var(--sapInformativeColor)!important;cursor:pointer}.se-dropdown-more-menu__toggle.sap-icon--overflow{position:relative;bottom:3px;height:20px;line-height:36px}se-dropdown-menu-item li>*{width:100%!important}.se-dropdown-menu__list{display:flex;flex-direction:column;min-width:100px;border-radius:var(--sapElement_BorderCornerRadius)!important}.se-dropdown-menu__list se-dropdown-menu-item:first-child .se-dropdown-item,.se-dropdown-menu__list se-dropdown-menu-item:first-child .se-dropdown-item:hover{border-top-left-radius:var(--sapElement_BorderCornerRadius);border-top-right-radius:var(--sapElement_BorderCornerRadius)}.se-dropdown-menu__list se-dropdown-menu-item:last-child .se-dropdown-item,.se-dropdown-menu__list se-dropdown-menu-item:last-child .se-dropdown-item:hover{border-bottom-left-radius:var(--sapElement_BorderCornerRadius);border-bottom-right-radius:var(--sapElement_BorderCornerRadius);border-bottom:none!important}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item{border-bottom:none;padding:unset}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item{text-decoration:none!important;white-space:nowrap;color:var(--sapTextColor);width:100%;padding:10px 24px;border-bottom:var(--sapList_BorderWidth) solid var(--sapList_BorderColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item.fd-has-color-status-3{color:var(--sapNegativeColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a{order:99}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a:hover,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete:hover{color:var(--sapNegativeColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a.se-dropdown-item--disabled,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a.se-dropdown-item--disabled:hover,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete.se-dropdown-item--disabled,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete.se-dropdown-item--disabled:hover{opacity:.4;padding-right:8px!important;background:var(--sapBaseColor)!important}.se-dropdown-item__delete-wrapper{display:flex;flex-direction:row;align-items:center;width:100%}.se-dropdown-item__delete-page-popover{margin-right:16px}.se-dropdown-item__delete-page-popover .sap-icon--message-error{color:var(--sapIndicationColor_5)}.se-dropdown-item__delete-link-wrapper{width:100%}.se-dropdown-item__delete-link-wrapper--disabled{cursor:not-allowed}`],
    encapsulation: core.ViewEncapsulation.None
});
exports.DropdownMenuComponent = class DropdownMenuComponent {
    constructor(cd, yjQuery) {
        this.cd = cd;
        this.yjQuery = yjQuery;
        this.selectedItemChange = new core.EventEmitter();
        this.useProjectedAnchor = false;
        this.isOpen = false;
        this.additionalClasses = [];
        this.isOpenChange = new core.EventEmitter();
        this.dropdownListSelector = '.se-dropdown-menu__list';
        this.isSubscription = false;
    }
    clickHandler(event) {
        if (this.toggleMenuElement.nativeElement.contains(event.target)) {
            event.stopPropagation();
            this.isOpen = !this.isOpen;
            this.cd.detectChanges();
            this.emitIsOpenChange();
            return;
        }
        if (this.isOpen) {
            this.isOpen = false;
            this.cd.detectChanges();
            this.emitIsOpenChange();
        }
    }
    ngOnChanges(changes) {
        const dropdownItemsChange = changes.dropdownItems;
        if (dropdownItemsChange) {
            this.clonedDropdownItems = dropdownItemsChange.currentValue
                .map((item) => (Object.assign(Object.assign({}, item), { condition: item.condition || (() => true) })))
                .map((item) => this.setDefaultComponentIfNeeded(item));
        }
    }
    ngDoCheck() {
        if (this.isOpen && !this.isSubscription) {
            const dropdownList = this.yjQuery(this.dropdownListSelector);
            if (dropdownList.length && !dropdownList.height()) {
                this.message = 'se.actions.without.permission';
            }
            this.isSubscription = true;
        }
    }
    emitIsOpenChange() {
        this.isOpenChange.emit(this.isOpen);
    }
    setDefaultComponentIfNeeded(dropdownItem) {
        if (this.validateDropdownItem(dropdownItem) === 'callback') {
            return Object.assign(Object.assign({}, dropdownItem), { component: exports.DropdownMenuItemDefaultComponent });
        }
        else {
            return dropdownItem;
        }
    }
    validateDropdownItem(dropdownItem) {
        const expectedAttributes = ['callback', 'component'];
        const passedAttributes = Object.keys(dropdownItem);
        const validatedAttributes = passedAttributes.filter((attribute) => expectedAttributes.includes(attribute));
        if (validatedAttributes.length !== 1) {
            throw new Error('DropdownMenuComponent.validateDropdownItem - Dropdown Item must contain "callback" or "component"');
        }
        return validatedAttributes[0];
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.DropdownMenuComponent.prototype, "dropdownItems", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DropdownMenuComponent.prototype, "selectedItem", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.DropdownMenuComponent.prototype, "selectedItemChange", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.DropdownMenuComponent.prototype, "placement", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DropdownMenuComponent.prototype, "useProjectedAnchor", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DropdownMenuComponent.prototype, "isOpen", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.DropdownMenuComponent.prototype, "additionalClasses", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.DropdownMenuComponent.prototype, "isOpenChange", void 0);
__decorate([
    core.ViewChild('toggleMenu', { static: true }),
    __metadata("design:type", core.ElementRef)
], exports.DropdownMenuComponent.prototype, "toggleMenuElement", void 0);
__decorate([
    core.HostListener('document:click', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [MouseEvent]),
    __metadata("design:returntype", void 0)
], exports.DropdownMenuComponent.prototype, "clickHandler", null);
exports.DropdownMenuComponent = __decorate([
    core.Component({
        selector: 'se-dropdown-menu',
        template: `<fd-popover [triggers]="[]" [isOpen]="isOpen" [placement]="placement || 'bottom-end'" class="se-dropdown-more-menu"><fd-popover-control><div #toggleMenu><div *ngIf="!useProjectedAnchor; else projectedAnchor;" class="sap-icon--overflow se-dropdown-more-menu__toggle" title="{{ 'se.contextmenu.title.more' | translate }}"></div><ng-template #projectedAnchor><ng-content></ng-content></ng-template></div></fd-popover-control><fd-popover-body><ul fd-list class="se-dropdown-menu__list" [ngClass]="additionalClasses"><se-dropdown-menu-item *ngFor="let dropdownItem of clonedDropdownItems" [dropdownItem]="dropdownItem" [selectedItem]="selectedItem" (selectedItemChange)="selectedItemChange.emit($event)" class="fd-list__byline-left"></se-dropdown-menu-item></ul><div *ngIf="message" class="se-information">{{ message | translate }}</div></fd-popover-body></fd-popover>`,
        styles: [`se-dropdown-menu .fd-popover__popper{border:0}.se-information{padding:12px}.se-dropdown-more-menu__toggle{color:var(--sapInformativeColor)!important;cursor:pointer}.se-dropdown-more-menu__toggle.sap-icon--overflow{position:relative;bottom:3px;height:20px;line-height:36px}se-dropdown-menu-item li>*{width:100%!important}.se-dropdown-menu__list{display:flex;flex-direction:column;min-width:100px;border-radius:var(--sapElement_BorderCornerRadius)!important}.se-dropdown-menu__list se-dropdown-menu-item:first-child .se-dropdown-item,.se-dropdown-menu__list se-dropdown-menu-item:first-child .se-dropdown-item:hover{border-top-left-radius:var(--sapElement_BorderCornerRadius);border-top-right-radius:var(--sapElement_BorderCornerRadius)}.se-dropdown-menu__list se-dropdown-menu-item:last-child .se-dropdown-item,.se-dropdown-menu__list se-dropdown-menu-item:last-child .se-dropdown-item:hover{border-bottom-left-radius:var(--sapElement_BorderCornerRadius);border-bottom-right-radius:var(--sapElement_BorderCornerRadius);border-bottom:none!important}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item{border-bottom:none;padding:unset}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item{text-decoration:none!important;white-space:nowrap;color:var(--sapTextColor);width:100%;padding:10px 24px;border-bottom:var(--sapList_BorderWidth) solid var(--sapList_BorderColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item.fd-has-color-status-3{color:var(--sapNegativeColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a{order:99}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a:hover,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete:hover{color:var(--sapNegativeColor)}.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a.se-dropdown-item--disabled,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete a.se-dropdown-item--disabled:hover,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete.se-dropdown-item--disabled,.se-dropdown-menu__list se-dropdown-menu-item .fd-list__item .se-dropdown-item__delete.se-dropdown-item--disabled:hover{opacity:.4;padding-right:8px!important;background:var(--sapBaseColor)!important}.se-dropdown-item__delete-wrapper{display:flex;flex-direction:row;align-items:center;width:100%}.se-dropdown-item__delete-page-popover{margin-right:16px}.se-dropdown-item__delete-page-popover .sap-icon--message-error{color:var(--sapIndicationColor_5)}.se-dropdown-item__delete-link-wrapper{width:100%}.se-dropdown-item__delete-link-wrapper--disabled{cursor:not-allowed}`],
        encapsulation: core.ViewEncapsulation.None
    }),
    __param(1, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [core.ChangeDetectorRef, Function])
], exports.DropdownMenuComponent);

window.__smartedit__.addDecoratorPayload("Component", "DropdownMenuItemComponent", {
    selector: 'se-dropdown-menu-item',
    template: `<li fd-list-item *ngIf="dropdownItem.condition(selectedItem)" class="fd-list__byline-left" [ngClass]="dropdownItem.customCss" (click)="onClick()"><ng-container *ngComponentOutlet="dropdownItem.component; injector: dropdownItemInjector;"></ng-container></li>`
});
let DropdownMenuItemComponent = class DropdownMenuItemComponent {
    constructor(injector) {
        this.injector = injector;
        this.selectedItemChange = new core.EventEmitter();
    }
    ngOnInit() {
        this.createDropdownItemInjector();
    }
    onClick() {
        this.selectedItemChange.emit(this.dropdownItem);
    }
    createDropdownItemInjector() {
        const { selectedItem } = this;
        this.dropdownItemInjector = core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: DROPDOWN_MENU_ITEM_DATA,
                    useValue: {
                        dropdownItem: this.dropdownItem,
                        selectedItem
                    }
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], DropdownMenuItemComponent.prototype, "dropdownItem", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], DropdownMenuItemComponent.prototype, "selectedItem", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], DropdownMenuItemComponent.prototype, "selectedItemChange", void 0);
DropdownMenuItemComponent = __decorate([
    core.Component({
        selector: 'se-dropdown-menu-item',
        template: `<li fd-list-item *ngIf="dropdownItem.condition(selectedItem)" class="fd-list__byline-left" [ngClass]="dropdownItem.customCss" (click)="onClick()"><ng-container *ngComponentOutlet="dropdownItem.component; injector: dropdownItemInjector;"></ng-container></li>`
    }),
    __metadata("design:paramtypes", [core.Injector])
], DropdownMenuItemComponent);

exports.DropdownMenuModule = class DropdownMenuModule {
};
exports.DropdownMenuModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.FundamentalsModule, utils.TranslationModule.forChild(), list.ListModule],
        declarations: [
            exports.DropdownMenuComponent,
            DropdownMenuItemComponent,
            exports.DropdownMenuItemDefaultComponent
        ],
        entryComponents: [exports.DropdownMenuComponent, exports.DropdownMenuItemDefaultComponent],
        exports: [exports.DropdownMenuComponent, DropdownMenuItemComponent]
    })
], exports.DropdownMenuModule);

window.__smartedit__.addDecoratorPayload("Component", "DynamicPagedListComponent", {
    selector: 'se-dynamic-paged-list',
    template: `<div *ngIf="ready" class="se-dynamic-paged-list"><div class="fd-panel__body fd-panel__body--bleed"><se-data-table [columns]="columns" [config]="config" [items]="items" [sortStatus]="sortStatus" (onSortColumn)="orderByColumn($event)"></se-data-table></div><div class="fd-panel__footer"><se-pagination *ngIf="!!items.length" class="se-dynamic-list__pagination" [totalItems]="totalItems" [itemsPerPage]="config.itemsPerPage" [currentPage]="currentPage" (onChange)="onCurrentPageChange($event)"></se-pagination><span class="se-paged-list__page-count-wrapper se-paged-list__page-count-wrapper--footer"><span class="span-page-list__page-count-text">{{'se.pagelist.countsearchresult' | translate}} </span><span class="se-page-list__page-count">({{ totalItems }})</span></span></div></div><se-spinner [isSpinning]="!ready"></se-spinner>`
});
exports.DynamicPagedListComponent = class DynamicPagedListComponent {
    constructor(logService, restServiceFactory, cdr) {
        this.logService = logService;
        this.restServiceFactory = restServiceFactory;
        this.cdr = cdr;
        this.getApi = new core.EventEmitter();
        this.onItemsUpdate = new core.EventEmitter();
        this.api = {
            reloadItems: () => this.loadItems()
        };
    }
    ngOnInit() {
        this._validateInput();
        this.ready = false;
        this.totalItems = 0;
        this.currentPage = 1;
        this.columnSortMode = this.config.reversed
            ? exports.SortDirections.Descending
            : exports.SortDirections.Ascending;
        this.internalSortBy = lodash.cloneDeep(this.config.sortBy);
        this.oldMask = this.mask;
        this.columns = [];
        this.sortStatus = {
            internalSortBy: this.internalSortBy,
            reversed: this.config.reversed,
            currentPage: this.currentPage
        };
        this._buildColumnData();
        this.loadItems();
        this.getApi.emit(this.api);
    }
    ngOnChanges(changes) {
        const maskChange = changes.mask;
        if (!!maskChange && !maskChange.firstChange && this.oldMask !== this.mask) {
            this.oldMask = this.mask;
            this.currentPage = 1;
            this.loadItems();
        }
    }
    orderByColumn(event) {
        this.internalSortBy = event.$columnKey.property;
        this.columnSortMode = event.$columnSortMode;
        this.config.reversed = this.columnSortMode === exports.SortDirections.Descending;
        this.sortStatus.internalSortBy = this.internalSortBy;
        this.sortStatus.reversed = this.config.reversed;
        if (event.$columnKey.sortable) {
            this.currentPage = 1;
            this.sortStatus.currentPage = 1;
            this.loadItems();
        }
    }
    loadItems() {
        this.ready = false;
        const params = Object.assign(Object.assign({}, (this.config.queryParams || {})), { currentPage: this.currentPage - 1, mask: this.mask, pageSize: this.config.itemsPerPage, sort: `${this.internalSortBy}:${this.columnSortMode}` });
        return this.restServiceFactory
            .get(this.config.uri)
            .page(params)
            .then((result) => {
            this.items = result.results;
            if (this.items.length === 0) {
                this.logService.warn('PagedList: No items returned to display');
            }
            this.totalItems = result.pagination.totalCount;
            this.currentPage = parseInt(String(result.pagination.page), 10) + 1;
            this.ready = true;
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
            this.onItemsUpdate.emit(result);
            return result;
        });
    }
    onCurrentPageChange(newCurrentPage) {
        if (newCurrentPage === this.currentPage) {
            return;
        }
        this.currentPage = newCurrentPage;
        this.loadItems();
    }
    _validateInput() {
        if (!this.config) {
            throw new Error('config object is required');
        }
        if (!(this.config.keys instanceof Array)) {
            throw new Error('keys must be an array');
        }
        if (this.config.keys.length < 1) {
            throw new Error('dynamic paged list requires at least one key');
        }
        if (!this.config.uri) {
            throw new Error('dynamic paged list requires a uri to fetch the list of items');
        }
    }
    _buildColumnData() {
        this.columns = this.config.keys.map((key) => (Object.assign({}, key)));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.DynamicPagedListComponent.prototype, "config", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.DynamicPagedListComponent.prototype, "mask", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.DynamicPagedListComponent.prototype, "getApi", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.DynamicPagedListComponent.prototype, "onItemsUpdate", void 0);
exports.DynamicPagedListComponent = __decorate([
    core.Component({
        selector: 'se-dynamic-paged-list',
        template: `<div *ngIf="ready" class="se-dynamic-paged-list"><div class="fd-panel__body fd-panel__body--bleed"><se-data-table [columns]="columns" [config]="config" [items]="items" [sortStatus]="sortStatus" (onSortColumn)="orderByColumn($event)"></se-data-table></div><div class="fd-panel__footer"><se-pagination *ngIf="!!items.length" class="se-dynamic-list__pagination" [totalItems]="totalItems" [itemsPerPage]="config.itemsPerPage" [currentPage]="currentPage" (onChange)="onCurrentPageChange($event)"></se-pagination><span class="se-paged-list__page-count-wrapper se-paged-list__page-count-wrapper--footer"><span class="span-page-list__page-count-text">{{'se.pagelist.countsearchresult' | translate}} </span><span class="se-page-list__page-count">({{ totalItems }})</span></span></div></div><se-spinner [isSpinning]="!ready"></se-spinner>`
    }),
    __metadata("design:paramtypes", [utils.LogService,
        utils.RestServiceFactory,
        core.ChangeDetectorRef])
], exports.DynamicPagedListComponent);

window.__smartedit__.addDecoratorPayload("Component", "PaginationComponent", {
    selector: 'se-pagination',
    template: `<fd-pagination class="se-pagination" [displayTotalItems]="displayTotalItems" [totalItems]="totalItems" [itemsPerPage]="totalItems === 1 ? 1 : itemsPerPage" [currentPage]="currentPage" (pageChangeStart)="onPageChanged($event)" nextLabel="{{ 'se.btn.nextpage.title' | translate }}" previousLabel="{{ 'se.btn.previouspage.title' | translate }}"></fd-pagination>`
});
exports.PaginationComponent = class PaginationComponent {
    constructor() {
        this.displayTotalItems = false;
        this.onChange = new core.EventEmitter();
    }
    onPageChanged(page) {
        this.currentPage = page;
        this.onChange.emit(page);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.PaginationComponent.prototype, "totalItems", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PaginationComponent.prototype, "displayTotalItems", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.PaginationComponent.prototype, "itemsPerPage", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.PaginationComponent.prototype, "currentPage", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.PaginationComponent.prototype, "onChange", void 0);
exports.PaginationComponent = __decorate([
    core.Component({
        selector: 'se-pagination',
        template: `<fd-pagination class="se-pagination" [displayTotalItems]="displayTotalItems" [totalItems]="totalItems" [itemsPerPage]="totalItems === 1 ? 1 : itemsPerPage" [currentPage]="currentPage" (pageChangeStart)="onPageChanged($event)" nextLabel="{{ 'se.btn.nextpage.title' | translate }}" previousLabel="{{ 'se.btn.previouspage.title' | translate }}"></fd-pagination>`
    })
], exports.PaginationComponent);

exports.PaginationModule = class PaginationModule {
};
exports.PaginationModule = __decorate([
    core.NgModule({
        imports: [exports.FundamentalsModule, core$1.TranslateModule.forChild()],
        declarations: [exports.PaginationComponent],
        exports: [exports.PaginationComponent]
    })
], exports.PaginationModule);

window.__smartedit__.addDecoratorPayload("Component", "SpinnerComponent", {
    selector: 'se-spinner',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `
        <div
            *ngIf="isSpinning"
            class="se-spinner fd-busy-indicator-extended panel-body"
            [ngClass]="{ 'se-spinner--fluid': isFluid }">
            <div class="spinner">
                <fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator>
            </div>
        </div>
    `
});
exports.SpinnerComponent = class SpinnerComponent {
    constructor() {
        this.isFluid = true;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.SpinnerComponent.prototype, "isSpinning", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SpinnerComponent.prototype, "isFluid", void 0);
exports.SpinnerComponent = __decorate([
    core.Component({
        selector: 'se-spinner',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `
        <div
            *ngIf="isSpinning"
            class="se-spinner fd-busy-indicator-extended panel-body"
            [ngClass]="{ 'se-spinner--fluid': isFluid }">
            <div class="spinner">
                <fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator>
            </div>
        </div>
    `
    })
], exports.SpinnerComponent);

exports.SpinnerModule = class SpinnerModule {
};
exports.SpinnerModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, busyIndicator.BusyIndicatorModule],
        declarations: [exports.SpinnerComponent],
        entryComponents: [exports.SpinnerComponent],
        exports: [exports.SpinnerComponent]
    })
], exports.SpinnerModule);

exports.DynamicPagedListModule = class DynamicPagedListModule {
};
exports.DynamicPagedListModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            exports.PaginationModule,
            DataTableModule,
            utils.TranslationModule.forChild(),
            exports.SpinnerModule
        ],
        declarations: [exports.DynamicPagedListComponent],
        entryComponents: [exports.DynamicPagedListComponent],
        exports: [exports.DynamicPagedListComponent]
    })
], exports.DynamicPagedListModule);

window.__smartedit__.addDecoratorPayload("Component", "InfiniteScrollingComponent", {
    selector: 'se-infinite-scrolling',
    template: `<div #container class="se-infinite-scrolling__container" [ngClass]="dropDownContainerClass" fdInfiniteScroll (onScrollAction)="nextPage()" [scrollPercent]="distance" *ngIf="initiated"><div class="se-infinite-scrolling__holder" [ngClass]="dropDownClass"><div #content><ng-content></ng-content></div><div class="spinner-container"><se-spinner [isSpinning]="isLoading" [isFluid]="false"></se-spinner></div></div></div>`,
    styles: [`se-infinite-scrolling.se-infinite-scrolling--narrowed .se-infinite-scrolling__holder{padding:0 20px}se-infinite-scrolling .se-infinite-scrolling__container{overflow-y:auto;max-height:200px}se-infinite-scrolling .se-infinite-scrolling__holder{overflow-y:hidden}`],
    encapsulation: core.ViewEncapsulation.None
});
exports.InfiniteScrollingComponent = class InfiniteScrollingComponent {
    constructor(discardablePromiseUtils, cdr) {
        this.discardablePromiseUtils = discardablePromiseUtils;
        this.cdr = cdr;
        this.distance = 80;
        this.itemsChange = new core.EventEmitter();
        this.containerId = stringUtils.generateIdentifier();
        this.initiated = false;
        this.isLoading = false;
        this.internalItemsSelected = [];
    }
    ngOnInit() {
        this.init();
    }
    ngOnChanges() {
        this.context = this.context || this;
        this.init();
    }
    ngAfterViewInit() {
        this.initContentResizeObserver();
    }
    ngOnDestroy() {
        this.discardablePromiseUtils.clear(this.containerId);
        this.contentResizeObserver.disconnect();
    }
    nextPage() {
        if (this.pagingDisabled) {
            return;
        }
        this.pagingDisabled = true;
        this.currentPage = this.currentPage + 1;
        this.mask = this.mask || '';
        this.isLoading = true;
        this.internalItemsSelected = this.context.selected || [];
        this.discardablePromiseUtils.apply(this.containerId, this.fetchPage(this.mask, this.pageSize, this.currentPage, this.internalItemsSelected), (page) => {
            page.results = page.results || [];
            page.results.forEach((element) => {
                element.technicalUniqueId = stringUtils.encode(element);
            });
            const uniqueResults = lodash.differenceBy(page.results, this.context.items, 'technicalUniqueId');
            if (uniqueResults.length > 0) {
                this.context.items = [...this.context.items, ...(uniqueResults || [])];
            }
            this.itemsChange.emit(this.context.items);
            this.pagingDisabled = this.isPagingDisabled(page);
            if (this.pagingDisabled) {
                this.isLoading = false;
                if (!this.cdr.destroyed) {
                    this.cdr.detectChanges();
                }
            }
            this.tryToLoadNextPage(page);
        });
    }
    scrollToTop() {
        if (this.containerElement) {
            this.containerElement.nativeElement.scrollTop = 0;
        }
    }
    scrollToBottom() {
        if (this.containerElement) {
            this.containerElement.nativeElement.scrollTop =
                this.containerElement.nativeElement.scrollHeight;
        }
    }
    init() {
        const wasInitiated = this.initiated;
        this.context.items = [];
        this.currentPage = -1;
        this.pagingDisabled = false;
        this.initiated = true;
        if (wasInitiated) {
            this.scrollToTop();
            this.nextPage();
        }
    }
    initContentResizeObserver() {
        this.contentResizeObserver = new resizeObserver.ResizeObserver((entries) => {
            const { contentRect } = entries[0];
            const isInitialChange = contentRect.height === 0;
            if (!this.pagingDisabled && this.isLoading && !isInitialChange) {
                if (this.shouldLoadNextPage()) {
                    this.nextPage();
                }
                else {
                    this.isLoading = false;
                    if (!this.cdr.destroyed) {
                        this.cdr.detectChanges();
                    }
                }
                this.contentResizeObserver.disconnect();
            }
        });
        this.contentResizeObserver.observe(this.contentElement.nativeElement);
    }
    shouldLoadNextPage() {
        const contentHeight = this.contentElement.nativeElement.offsetHeight;
        const containerHeight = this.containerElement.nativeElement.offsetHeight;
        return contentHeight <= containerHeight;
    }
    isPagingDisabled(page) {
        return (page.pagination === undefined ||
            (page.pagination && this.context.items.length === page.pagination.totalCount) ||
            (page.pagination && page.pagination.totalPages === page.pagination.page + 1));
    }
    tryToLoadNextPage(page) {
        if (page.pagination === undefined || this.pagingDisabled) {
            return;
        }
        if (this.context.items.length === 0 ||
            (page.pagination.count < this.pageSize &&
                this.containerElement.nativeElement.offsetHeight !== 0 &&
                this.shouldLoadNextPage())) {
            this.nextPage();
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.InfiniteScrollingComponent.prototype, "pageSize", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.InfiniteScrollingComponent.prototype, "mask", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.InfiniteScrollingComponent.prototype, "dropDownContainerClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.InfiniteScrollingComponent.prototype, "dropDownClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.InfiniteScrollingComponent.prototype, "distance", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.InfiniteScrollingComponent.prototype, "context", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.InfiniteScrollingComponent.prototype, "fetchPage", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.InfiniteScrollingComponent.prototype, "itemsChange", void 0);
__decorate([
    core.ViewChild('container', { static: false }),
    __metadata("design:type", core.ElementRef)
], exports.InfiniteScrollingComponent.prototype, "containerElement", void 0);
__decorate([
    core.ViewChild('content', { static: false }),
    __metadata("design:type", core.ElementRef)
], exports.InfiniteScrollingComponent.prototype, "contentElement", void 0);
exports.InfiniteScrollingComponent = __decorate([
    core.Component({
        selector: 'se-infinite-scrolling',
        template: `<div #container class="se-infinite-scrolling__container" [ngClass]="dropDownContainerClass" fdInfiniteScroll (onScrollAction)="nextPage()" [scrollPercent]="distance" *ngIf="initiated"><div class="se-infinite-scrolling__holder" [ngClass]="dropDownClass"><div #content><ng-content></ng-content></div><div class="spinner-container"><se-spinner [isSpinning]="isLoading" [isFluid]="false"></se-spinner></div></div></div>`,
        styles: [`se-infinite-scrolling.se-infinite-scrolling--narrowed .se-infinite-scrolling__holder{padding:0 20px}se-infinite-scrolling .se-infinite-scrolling__container{overflow-y:auto;max-height:200px}se-infinite-scrolling .se-infinite-scrolling__holder{overflow-y:hidden}`],
        encapsulation: core.ViewEncapsulation.None
    }),
    __metadata("design:paramtypes", [exports.DiscardablePromiseUtils,
        core.ChangeDetectorRef])
], exports.InfiniteScrollingComponent);

exports.InfiniteScrollingModule = class InfiniteScrollingModule {
};
exports.InfiniteScrollingModule = __decorate([
    core.NgModule({
        imports: [core$2.InfiniteScrollModule, exports.SpinnerModule, common.CommonModule],
        declarations: [exports.InfiniteScrollingComponent],
        entryComponents: [exports.InfiniteScrollingComponent],
        exports: [exports.InfiniteScrollingComponent]
    })
], exports.InfiniteScrollingModule);

/** @ignore */
function getLocalizedFilterFn(language) {
    return (localizedMap) => {
        if (typeof localizedMap === 'string') {
            return localizedMap;
        }
        else if (localizedMap) {
            return localizedMap[language] || localizedMap[Object.keys(localizedMap)[0]];
        }
        else {
            return undefined;
        }
    };
}
/**
 * Pipe for translating localized maps for the current language.
 *
 * ### Example
 *
 *      localizedMap = {
 *        en: 'dummyText in english',
 *        fr: 'dummyText in french'
 *      };
 *
 *      {{ localizedMap | seL10n | async }}
 *
 */
exports.L10nPipe = class L10nPipe {
    constructor(l10nService) {
        this.l10nService = l10nService;
    }
    transform(localizedMap) {
        return this.l10nService.languageSwitch$.pipe(operators.startWith(null), operators.pairwise(), operators.tap(([prev, curr]) => {
            if (prev !== curr) {
                this.filterFn = getLocalizedFilterFn(curr);
            }
        }), operators.map(() => this.filterFn(localizedMap)));
    }
};
exports.L10nPipe = __decorate([
    core.Pipe({
        name: 'seL10n'
    }),
    __metadata("design:paramtypes", [exports.L10nService])
], exports.L10nPipe);

exports.L10nPipeModule = class L10nPipeModule {
};
exports.L10nPipeModule = __decorate([
    core.NgModule({
        declarations: [exports.L10nPipe],
        exports: [exports.L10nPipe],
        providers: [
            exports.L10nPipe,
            moduleUtils.initialize((l10nService) => l10nService.resolveLanguage(), [exports.L10nService])
        ]
    })
], exports.L10nPipeModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class TreeNestedDataSource {
    constructor() {
        this._data = new rxjs.BehaviorSubject([]);
    }
    get() {
        return this._data.asObservable();
    }
    set(data) {
        this._data.next(data);
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
class TreeNodeItem {
    constructor(config) {
        this.position = 0;
        this.nodes = [];
        this.hasChildren = config.hasChildren;
        this.name = config.name;
        this.title = config.title;
        this.parentUid = config.parentUid;
        this.position = config.position;
        this.itemType = config.itemType;
        this.uid = config.uid;
        this.uuid = config.uuid;
        this.level = config.level;
        this.nodes = [];
    }
    setPosition(position) {
        this.position = position;
        return this;
    }
    setMouseHovered(isHovered) {
        this.mouseHovered = isHovered;
        return this;
    }
    setInitiated(isInitiated) {
        this.initiated = isInitiated;
        return this;
    }
    setLevel(level) {
        this.level = level;
        return this;
    }
    addNode(node) {
        this.nodes = [...this.nodes, node.setParent(this)].map((item, idx) => item.setPosition(idx).setLevel(this.level + 1));
        this.updateHasChildren();
        return this;
    }
    addNodes(nodes) {
        this.nodes = [
            ...this.nodes,
            ...nodes.map((node) => node.setParent(this))
        ].map((item, idx) => item.setPosition(idx).setLevel(this.level + 1));
        this.updateHasChildren();
        return this;
    }
    removeAllNodes() {
        this.nodes = [];
        this.updateHasChildren();
        return this;
    }
    removeNode(node) {
        this.nodes = this.nodes
            .filter((_node) => _node.uid !== node.uid)
            .map((item, idx) => item.setPosition(idx));
        this.updateHasChildren();
        return this;
    }
    setParent(node) {
        this.parent = node;
        this.parentUid = node.uid;
        return this;
    }
    toggle() {
        this.isExpanded = !this.isExpanded;
        return this;
    }
    collapse() {
        this.isExpanded = false;
        return this;
    }
    expand() {
        this.isExpanded = true;
        return this;
    }
    collapseAll() {
        this.nodes.forEach((node) => this.collapseRecursively(node));
    }
    expandAll() {
        this.nodes.forEach((node) => this.expandRecursively(node));
    }
    collapseRecursively(_node) {
        _node.collapse().nodes.forEach((node) => this.collapseRecursively(node));
    }
    expandRecursively(_node) {
        _node.expand().nodes.forEach((node) => this.expandRecursively(node));
    }
    updateHasChildren() {
        this.hasChildren = !!this.nodes.length;
    }
}
class NavigationNodeItem extends TreeNodeItem {
    constructor(config) {
        super(config);
        this.entries = config.entries;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
var TreeNodeItemType;
(function (TreeNodeItemType) {
    TreeNodeItemType["CMSNavigationNode"] = "CMSNavigationNode";
})(TreeNodeItemType || (TreeNodeItemType = {}));
/**
 * A service used to generate instance of node item to be consumed by {@link TreeComponent}.
 */
class TreeNodeItemFactory {
    /**
     * @param dto DTO based on which the class is returned.
     *
     * Returns a class depending on itemtype.
     */
    get(dto) {
        switch (dto.itemType) {
            case TreeNodeItemType.CMSNavigationNode:
                return new NavigationNodeItem(dto);
            default:
                return new TreeNodeItem(dto);
        }
    }
}

/**
 * A service to manage tree nodes through a REST API.
 */
/* @ngInject */ exports.TreeService = class /* @ngInject */ TreeService {
    constructor(restServiceFactory, treeNodeItemFactory) {
        this.restServiceFactory = restServiceFactory;
        this.treeNodeItemFactory = treeNodeItemFactory;
        this.dataSource = new TreeNestedDataSource();
        this.$onTreeUpdated = new rxjs.BehaviorSubject(false);
    }
    onTreeUpdated() {
        return this.$onTreeUpdated.pipe(operators.filter((value) => !!value));
    }
    /**
     * Initializes the REST service and sets root node.
     *
     * @param nodeUri URI passed to {@link TreeComponent}.
     * @param rootNodeUid root uid passed to {@link TreeComponent}.
     */
    init(nodeUri, rootNodeUid) {
        if (nodeUri) {
            this.nodesRestService = this.restServiceFactory.get(nodeUri);
        }
        this.setRoot(rootNodeUid);
    }
    /**
     * Updates the position of the node within the tree.
     *
     * @param node Node to be rearranged.
     * @param position New position of node.
     */
    rearrange(node, parent, position) {
        const siblings = parent.nodes.filter((_node) => _node.uid !== node.uid);
        const rearranged = [
            ...siblings.slice(0, position),
            node,
            ...siblings.slice(position, siblings.length)
        ];
        node.parent.removeNode(node);
        parent.removeAllNodes().addNodes(rearranged);
        this.update();
    }
    /**
     * Fetches the node children and updates the tree.
     */
    fetchChildren(_parent) {
        return __awaiter(this, void 0, void 0, function* () {
            const parent = _parent || this.root;
            if (parent.initiated) {
                this.update();
                return Promise.resolve(parent.nodes);
            }
            else {
                const response = yield this.nodesRestService.get({ parentUid: parent.uid });
                const children = (apiUtils.getDataFromResponse(response) || []).map((dto) => this.treeNodeItemFactory.get(dto));
                parent.removeAllNodes().addNodes(children).setInitiated(true);
                this.update();
                return children;
            }
        });
    }
    /**
     * Toggles the passed node and fetches children
     *
     * @param node Node to be rearranged.
     */
    toggle(node) {
        return __awaiter(this, void 0, void 0, function* () {
            node.toggle();
            return this.fetchChildren(node);
        });
    }
    /**
     * Adds a new child to passed node.
     *
     * @param node Node to be rearranged.
     */
    newChild(node) {
        return __awaiter(this, void 0, void 0, function* () {
            const nodeData = node || this.root;
            const response = yield this.saveNode(nodeData);
            if (!nodeData.isExpanded) {
                this.toggle(nodeData);
            }
            else {
                const elm = nodeData.nodes.find((_node) => _node.uid === response.uid);
                if (!elm) {
                    nodeData.addNode(this.treeNodeItemFactory.get(response));
                    this.update();
                }
            }
        });
    }
    /**
     * Adds new sibling to passed node.
     *
     * @param node Node to be rearranged.
     */
    newSibling(node) {
        return __awaiter(this, void 0, void 0, function* () {
            const response = yield this.saveNode(node.parent);
            node.parent.addNode(this.treeNodeItemFactory.get(response));
            this.update();
        });
    }
    /**
     * Removes passed node.
     *
     * @param node Node to be rearranged.
     */
    removeNode(node) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.nodesRestService.remove({ identifier: node.uid });
            node.parent.removeNode(node);
            this.update();
        });
    }
    /**
     * Updates the data source from where the nodes are retrieved
     */
    update() {
        this.dataSource.set(null);
        this.dataSource.set(this.root.nodes);
        this.$onTreeUpdated.next(true);
    }
    /**
     * Expands all nodes from the root node
     */
    expandAll() {
        this.root.expandAll();
        this.update();
    }
    /**
     * Collapses all nodes from the root node
     */
    collapseAll() {
        this.root.collapseAll();
        this.update();
    }
    getNodePositionById(nodeUid) {
        return this.getNodeById(nodeUid).position;
    }
    getNodeById(nodeUid, nodeArray) {
        if (nodeUid === this.root.uid) {
            return this.root;
        }
        const nodes = nodeArray || this.root.nodes;
        // eslint-disable-next-line @typescript-eslint/no-for-in-array
        for (const node of nodes) {
            if (node.uid === nodeUid) {
                return node;
            }
            if (node.hasChildren) {
                node.nodes = node.nodes || [];
                return this.getNodeById(nodeUid, node.nodes);
            }
        }
        return null;
    }
    saveNode(_parent) {
        return __awaiter(this, void 0, void 0, function* () {
            const response = yield this.nodesRestService.save({
                parentUid: _parent.uid,
                name: (_parent.name ? _parent.name : _parent.uid) + _parent.nodes.length
            });
            return response;
        });
    }
    setRoot(uid) {
        this.root = this.treeNodeItemFactory.get({ uid, name: 'root', level: 0 });
    }
};
/* @ngInject */ exports.TreeService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory,
        TreeNodeItemFactory])
], /* @ngInject */ exports.TreeService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Represents the event triggered when dragging and dropping nodes in the {@link TreeComponent}.
 */
class TreeDragAndDropEvent {
    /**
     * @param sourceNode `ITreeNodeItem` that is being dragged.
     * @param destinationNodes The set of the destination's parent's children `ITreeNodeItem`.
     * @param sourceParentNode Parent `ITreeNodeItem` of the dragged node.
     * @param destinationParentNode Parent `ITreeNodeItem` of the destination node.
     * @param position Index at which the `ITreeNodeItem` was dropped.
     */
    constructor(sourceNode, destinationNodes, sourceParentNode, destinationParentNode, position) {
        this.sourceNode = sourceNode;
        this.destinationNodes = destinationNodes;
        this.sourceParentNode = sourceParentNode;
        this.destinationParentNode = destinationParentNode;
        this.position = position;
    }
}
const TREE_NODE = new core.InjectionToken('TREE_NODE');

let /* @ngInject */ TreeDragAndDropService = class /* @ngInject */ TreeDragAndDropService {
    constructor(confirmationModalService, alertService, treeService) {
        this.confirmationModalService = confirmationModalService;
        this.alertService = alertService;
        this.treeService = treeService;
    }
    init(options) {
        this.config = options;
        this.isDragEnabled =
            !!options &&
                (!!options.onDropCallback ||
                    !!options.beforeDropCallback ||
                    !!options.allowDropCallback);
    }
    handleDrop(event) {
        if (!this.config || !event.isPointerOverContainer) {
            return Promise.resolve();
        }
        if (!this.allowDrop(event)) {
            return Promise.resolve();
        }
        return this.beforeDrop(event).then((result) => {
            if (result === false) {
                return;
            }
            this.onDrop(event);
            this.rearrangeNodes(event);
        });
    }
    rearrangeNodes(event) {
        this.treeService.rearrange(event.item.data, event.container.data[0].parent, event.currentIndex);
    }
    onDrop(event) {
        if (!this.config.onDropCallback) {
            return;
        }
        const dndEvent = new TreeDragAndDropEvent(event.item.data, event.container.data, event.item.data.parent, event.container.data[0].parent, event.currentIndex);
        return this.config.onDropCallback(dndEvent);
    }
    allowDrop(event) {
        if (!this.config.allowDropCallback) {
            return true;
        }
        return this.config.allowDropCallback(new TreeDragAndDropEvent(event.item.data, event.container.data, event.item.data.parent, event.container.data[0].parent, event.currentIndex));
    }
    beforeDrop(event) {
        if (!this.config.beforeDropCallback) {
            return Promise.resolve();
        }
        const dndEvent = new TreeDragAndDropEvent(event.item.data, event.container.data, event.item.data.parent, event.container.data[0].parent, event.currentIndex);
        return this.config
            .beforeDropCallback(dndEvent)
            .then((result) => {
            if (typeof result === 'object') {
                if (result.confirmDropI18nKey) {
                    const message = {
                        description: result.confirmDropI18nKey
                    };
                    return this.confirmationModalService.confirm(message);
                }
                if (result.rejectDropI18nKey) {
                    this.alertService.showDanger({
                        message: result.rejectDropI18nKey,
                        minWidth: '',
                        mousePersist: true,
                        duration: 1000,
                        dismissible: true,
                        width: '300px'
                    });
                    return false;
                }
                throw new Error('Unexpected return value for beforeDropCallback does not contain confirmDropI18nKey nor rejectDropI18nKey: ' +
                    result);
            }
            return result;
        });
    }
};
/* @ngInject */ TreeDragAndDropService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [IConfirmationModalService,
        utils.IAlertService,
        exports.TreeService])
], /* @ngInject */ TreeDragAndDropService);

window.__smartedit__.addDecoratorPayload("Component", "TreeComponent", {
    selector: 'se-tree',
    providers: [exports.TreeService, TreeDragAndDropService, TreeNodeItemFactory],
    host: {
        class: 'se-tree'
    },
    template: `<div class="categoryTable se-tree"><div class="se-tree__body"><div class="desktopLayout"><ol class="se-tree__nodes"><li *ngIf="(this.treeService.dataSource.get() | async).length === 0" class="se-tree__nodes--nodata">{{ 'se.ytree.no.nodes.to.display' | translate }}</li><li class="se-tree__nodes--item"><se-tree-node [source]="this.treeService.dataSource.get() | async"></se-tree-node></li></ol></div></div></div>`
});
exports.TreeComponent = class TreeComponent {
    constructor(treeService, treeDragAndDropService) {
        this.treeService = treeService;
        this.treeDragAndDropService = treeDragAndDropService;
        this.onTreeUpdated = new core.EventEmitter();
    }
    ngOnInit() {
        this.setNodeActions();
        this.treeService.init(this.nodeUri, this.rootNodeUid);
        this.treeDragAndDropService.init(this.dragOptions);
        this.fetchData(this.treeService.root);
        this.treeService
            .onTreeUpdated()
            .subscribe(() => this.onTreeUpdated.emit(this.treeService.root.nodes));
    }
    fetchData(nodeData) {
        return this.treeService.fetchChildren(nodeData);
    }
    hasChildren(node) {
        return node.hasChildren;
    }
    collapseAll() {
        this.treeService.collapseAll();
    }
    expandAll() {
        this.treeService.expandAll();
    }
    remove(node) {
        this.treeService.removeNode(node);
    }
    newSibling(node) {
        this.treeService.newSibling(node);
    }
    refresh(node) {
        node.setInitiated(false);
        return this.treeService.fetchChildren(node);
    }
    refreshParent(node) {
        this.refresh(node.parent);
    }
    newChild(node) {
        return __awaiter(this, void 0, void 0, function* () {
            this.treeService.newChild(node);
        });
    }
    getNodeById(nodeUid, nodeArray) {
        return this.treeService.getNodeById(nodeUid, nodeArray);
    }
    get dragEnabled() {
        return this.treeDragAndDropService.isDragEnabled;
    }
    setNodeActions() {
        Object.keys(this.nodeActions).forEach((functionName) => {
            this[functionName] = this.nodeActions[functionName].bind(this, this.treeService);
            this.nodeActions[functionName] = this.nodeActions[functionName].bind(this, this.treeService);
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.TreeComponent.prototype, "nodeTemplateUrl", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.TreeComponent.prototype, "nodeComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.TreeComponent.prototype, "nodeUri", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TreeComponent.prototype, "nodeActions", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.TreeComponent.prototype, "rootNodeUid", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TreeComponent.prototype, "dragOptions", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.TreeComponent.prototype, "removeDefaultTemplate", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.TreeComponent.prototype, "showAsList", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.TreeComponent.prototype, "onTreeUpdated", void 0);
exports.TreeComponent = __decorate([
    core.Component({
        selector: 'se-tree',
        providers: [exports.TreeService, TreeDragAndDropService, TreeNodeItemFactory],
        host: {
            class: 'se-tree'
        },
        template: `<div class="categoryTable se-tree"><div class="se-tree__body"><div class="desktopLayout"><ol class="se-tree__nodes"><li *ngIf="(this.treeService.dataSource.get() | async).length === 0" class="se-tree__nodes--nodata">{{ 'se.ytree.no.nodes.to.display' | translate }}</li><li class="se-tree__nodes--item"><se-tree-node [source]="this.treeService.dataSource.get() | async"></se-tree-node></li></ol></div></div></div>`
    }),
    __metadata("design:paramtypes", [exports.TreeService,
        TreeDragAndDropService])
], exports.TreeComponent);

window.__smartedit__.addDecoratorPayload("Component", "TreeNodeComponent", {
    selector: 'se-tree-node',
    template: `<ol class="se-tree-node__ol angular-ui-tree-nodes" cdkDropList (cdkDropListDropped)="onDrop($event)" [cdkDropListData]="source" [cdkDropListDisabled]="isDisabled"><li *ngFor="let node of source" cdkDrag [cdkDragData]="node" class="angular-ui-tree-node se-tree-node__li"><se-tree-node-renderer [node]="node"></se-tree-node-renderer></li></ol>`
});
let TreeNodeComponent = class TreeNodeComponent {
    constructor(treeDragAndDropService, logService) {
        this.treeDragAndDropService = treeDragAndDropService;
        this.logService = logService;
    }
    get isDisabled() {
        return !this.treeDragAndDropService.isDragEnabled;
    }
    onDrop(event) {
        this.treeDragAndDropService.handleDrop(event).catch((reason) => {
            this.logService.debug(`TreeNodeComponent - onDrop: error: ${reason}`);
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], TreeNodeComponent.prototype, "source", void 0);
TreeNodeComponent = __decorate([
    core.Component({
        selector: 'se-tree-node',
        template: `<ol class="se-tree-node__ol angular-ui-tree-nodes" cdkDropList (cdkDropListDropped)="onDrop($event)" [cdkDropListData]="source" [cdkDropListDisabled]="isDisabled"><li *ngFor="let node of source" cdkDrag [cdkDragData]="node" class="angular-ui-tree-node se-tree-node__li"><se-tree-node-renderer [node]="node"></se-tree-node-renderer></li></ol>`
    }),
    __metadata("design:paramtypes", [TreeDragAndDropService,
        utils.LogService])
], TreeNodeComponent);

const DEFAULT_PADDING_LEFT_MODIFIER_PX = 10;
window.__smartedit__.addDecoratorPayload("Component", "TreeNodeRendererComponent", {
    selector: 'se-tree-node-renderer',
    template: `<div class="tree-node tree-node-content se-tree-node angular-ui-tree-handle" [ngClass]="{
        'hovered': node.mouseHovered,
        'se-tree-node--first-level': isRootNodeDescendant,
        'se-tree-node--selected': !collapsed
    }" [ngStyle]="{
        'padding-left': getPaddingLeft(node.level)
    }" [attr.level]="node.level" (mouseover)="onMouseOver()" (mouseout)="onMouseOut()"><div *ngIf="!showAsList" class="se-tree-node__expander--wrapper col-xs-1 text-left"><a class="se-tree-node__expander btn" *ngIf="node.hasChildren" [attr.disabled]="isDisabled || null" (click)="toggle($event)"><span [ngClass]="{
                    'sap-icon--slim-arrow-right': collapsed,
                    'sap-icon--slim-arrow-down': !collapsed
                }" title="{{ getNavigationTreeIconIsCollapse() | translate }}"></span></a></div><div *ngIf="displayDefaultTemplate" class="se-tree-node__name col-xs-5" [attr.title]="node.title | seL10n | async "><span>{{node.name}}</span><h6 *ngIf="node.title">{{node.title | seL10n | async }}</h6></div><ng-container *ngIf="tree.nodeComponent"><ng-container *ngComponentOutlet="tree.nodeComponent; injector: nodeComponentInjector"></ng-container></ng-container></div><se-tree-node [source]="node.nodes" *ngIf="node.nodes.length && node.isExpanded"></se-tree-node>`
});
exports.TreeNodeRendererComponent = class TreeNodeRendererComponent {
    constructor(tree, treeService, injector) {
        this.tree = tree;
        this.treeService = treeService;
        this.injector = injector;
    }
    ngOnInit() {
        this.createNodeComponentInjector();
    }
    ngOnChanges(changes) {
        if (changes.node) {
            this.createNodeComponentInjector();
        }
    }
    toggle($event) {
        $event.stopPropagation();
        $event.preventDefault();
        this.tree.isDropDisabled = true;
        this.treeService.toggle(this.node).then(() => {
            this.tree.isDropDisabled = false;
        });
    }
    onMouseOver() {
        this.node.setMouseHovered(true);
        this.treeService.update();
    }
    onMouseOut() {
        this.node.setMouseHovered(false);
        this.treeService.update();
    }
    getPaddingLeft(level) {
        return `${(level - 1) * DEFAULT_PADDING_LEFT_MODIFIER_PX}px`;
    }
    get showAsList() {
        return this.tree.showAsList;
    }
    get isDisabled() {
        return this.tree.isDropDisabled;
    }
    get collapsed() {
        return !this.node.isExpanded;
    }
    get displayDefaultTemplate() {
        return !this.tree.removeDefaultTemplate;
    }
    get isRootNodeDescendant() {
        return this.node.parentUid === 'root';
    }
    getNavigationTreeIconIsCollapse() {
        return this.node.isExpanded ? 'se.arrowicon.collapse.title' : 'se.arrowicon.expand.title';
    }
    createNodeComponentInjector() {
        this.nodeComponentInjector = core.Injector.create({
            providers: [
                {
                    provide: TREE_NODE,
                    useValue: this.node
                }
            ],
            parent: this.injector
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.TreeNodeRendererComponent.prototype, "node", void 0);
exports.TreeNodeRendererComponent = __decorate([
    core.Component({
        selector: 'se-tree-node-renderer',
        template: `<div class="tree-node tree-node-content se-tree-node angular-ui-tree-handle" [ngClass]="{
        'hovered': node.mouseHovered,
        'se-tree-node--first-level': isRootNodeDescendant,
        'se-tree-node--selected': !collapsed
    }" [ngStyle]="{
        'padding-left': getPaddingLeft(node.level)
    }" [attr.level]="node.level" (mouseover)="onMouseOver()" (mouseout)="onMouseOut()"><div *ngIf="!showAsList" class="se-tree-node__expander--wrapper col-xs-1 text-left"><a class="se-tree-node__expander btn" *ngIf="node.hasChildren" [attr.disabled]="isDisabled || null" (click)="toggle($event)"><span [ngClass]="{
                    'sap-icon--slim-arrow-right': collapsed,
                    'sap-icon--slim-arrow-down': !collapsed
                }" title="{{ getNavigationTreeIconIsCollapse() | translate }}"></span></a></div><div *ngIf="displayDefaultTemplate" class="se-tree-node__name col-xs-5" [attr.title]="node.title | seL10n | async "><span>{{node.name}}</span><h6 *ngIf="node.title">{{node.title | seL10n | async }}</h6></div><ng-container *ngIf="tree.nodeComponent"><ng-container *ngComponentOutlet="tree.nodeComponent; injector: nodeComponentInjector"></ng-container></ng-container></div><se-tree-node [source]="node.nodes" *ngIf="node.nodes.length && node.isExpanded"></se-tree-node>`
    }),
    __param(0, core.Inject(core.forwardRef(() => exports.TreeComponent))),
    __metadata("design:paramtypes", [exports.TreeComponent,
        exports.TreeService,
        core.Injector])
], exports.TreeNodeRendererComponent);

exports.NgTreeModule = class NgTreeModule {
};
exports.NgTreeModule = __decorate([
    core.NgModule({
        imports: [dragDrop.DragDropModule, common.CommonModule, exports.L10nPipeModule, utils.TranslationModule.forChild()],
        declarations: [exports.TreeComponent, exports.TreeNodeRendererComponent, TreeNodeComponent],
        exports: [exports.TreeComponent, TreeNodeComponent],
        entryComponents: [exports.TreeComponent, exports.TreeNodeRendererComponent]
    })
], exports.NgTreeModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class EditableListNodeItem extends TreeNodeItem {
    constructor(config) {
        super(config);
        this.thumbnail = config.thumbnail || {};
        this.id = config.id;
        this.code = config.code;
        this.catalogId = config.catalogId;
        this.catalogVersion = config.catalogVersion;
    }
}

window.__smartedit__.addDecoratorPayload("Component", "EditableListComponent", {
    selector: 'se-editable-list',
    template: `<div [ngClass]="{ 'y-editable-list-disabled': !editable, 'se-editable-list-disabled': !editable }"><se-tree (onTreeUpdated)="handleTreeUpdated($event)" [removeDefaultTemplate]="true" [rootNodeUid]="rootId" [nodeTemplateUrl]="itemTemplateUrl" [nodeComponent]="itemComponent" [nodeActions]="actions" [dragOptions]="dragOptions" [showAsList]="true"></se-tree></div>`
});
exports.EditableListComponent = class EditableListComponent {
    constructor() {
        this.refreshChange = new core.EventEmitter();
        this.itemsChange = new core.EventEmitter();
        this.dragOptions = {};
        this.itemsOld = [];
    }
    ngOnInit() {
        this._enableDragAndDrop = () => {
            this.dragOptions.allowDropCallback = (event) => event.sourceNode.parentUid === this.rootId;
        };
        this.actions = this.getTreeActions();
        this.refreshChange.emit(() => this.actions.refreshList());
        if (!this.itemTemplateUrl && !this.itemComponent) {
            this.itemComponent = exports.EditableListDefaultItem;
        }
        this.rootId = 'root' + this.id;
        if (this.editable === undefined) {
            this.editable = true;
        }
        if (this.editable === true) {
            this._enableDragAndDrop();
        }
    }
    handleTreeUpdated(items) {
        if (this.hasItemsChanged(this.itemsOld, items)) {
            this.itemsOld = items;
            this.itemsChange.emit(items);
            this.actions.performUpdate();
        }
    }
    getDropdownItems() {
        return [
            {
                key: 'se.ydropdownmenu.remove',
                callback: (handle) => {
                    this.actions.removeItem(handle);
                }
            },
            {
                key: 'se.ydropdownmenu.move.up',
                condition: (handle) => this.actions.isMoveUpAllowed(handle),
                callback: (handle) => {
                    this.actions.moveUp(handle);
                }
            },
            {
                key: 'se.ydropdownmenu.move.down',
                condition: (handle) => this.actions.isMoveDownAllowed(handle),
                callback: (handle) => {
                    this.actions.moveDown(handle);
                }
            }
        ];
    }
    hasItemsChanged(oldItems, newItems) {
        const oldUids = oldItems.map(({ uid }) => uid);
        const newUids = newItems.map(({ uid }) => uid);
        return !lodash.isEqual(oldUids, newUids);
    }
    getTreeActions() {
        const items = this.getDropdownItems();
        return {
            fetchData: (treeService, nodeData) => {
                const nodeItems = this.items.map((dto) => new EditableListNodeItem(dto));
                nodeItems.forEach((item) => {
                    if (item.id && !item.uid) {
                        item.uid = item.id;
                    }
                    item.setParent(nodeData);
                });
                nodeData.removeAllNodes().addNodes(nodeItems);
                treeService.update();
                return Promise.resolve(nodeData);
            },
            getDropdownItems: () => items,
            removeItem(treeService, nodeData) {
                nodeData.parent.removeNode(nodeData);
                treeService.update();
            },
            moveUp(treeService, nodeData) {
                treeService.rearrange(nodeData, treeService.root, nodeData.position - 1);
            },
            moveDown(treeService, nodeData) {
                treeService.rearrange(nodeData, treeService.root, nodeData.position + 1);
            },
            isMoveUpAllowed(treeService, nodeData) {
                return nodeData.position > 0;
            },
            isMoveDownAllowed(treeService, nodeData) {
                return treeService.root.nodes.length !== nodeData.position + 1;
            },
            performUpdate: (treeService) => {
                if (this.onChange) {
                    this.onChange();
                }
            },
            refreshList(treeService) {
                this.fetchData(treeService.root);
            }
        };
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.EditableListComponent.prototype, "refresh", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.EditableListComponent.prototype, "items", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.EditableListComponent.prototype, "onChange", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EditableListComponent.prototype, "itemTemplateUrl", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.EditableListComponent.prototype, "itemComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.EditableListComponent.prototype, "editable", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EditableListComponent.prototype, "id", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.EditableListComponent.prototype, "refreshChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.EditableListComponent.prototype, "itemsChange", void 0);
exports.EditableListComponent = __decorate([
    core.Component({
        selector: 'se-editable-list',
        template: `<div [ngClass]="{ 'y-editable-list-disabled': !editable, 'se-editable-list-disabled': !editable }"><se-tree (onTreeUpdated)="handleTreeUpdated($event)" [removeDefaultTemplate]="true" [rootNodeUid]="rootId" [nodeTemplateUrl]="itemTemplateUrl" [nodeComponent]="itemComponent" [nodeActions]="actions" [dragOptions]="dragOptions" [showAsList]="true"></se-tree></div>`
    })
], exports.EditableListComponent);
window.__smartedit__.addDecoratorPayload("Component", "EditableListDefaultItem", {
    selector: 'se-editable-list-default-item',
    template: `
        <div>
            <span>{{ node.uid }}</span>
            <se-dropdown-menu
                *ngIf="parent.editable"
                [dropdownItems]="parent.getDropdownItems()"
                [selectedItem]="node"
                class="pull-right se-tree-node__actions--more-menu"
                title="{{ 'se.contextmenu.title.more' | translate }}"></se-dropdown-menu>
        </div>
    `
});
exports.EditableListDefaultItem = class EditableListDefaultItem {
    constructor(parent, node) {
        this.parent = parent;
        this.node = node;
    }
};
exports.EditableListDefaultItem = __decorate([
    core.Component({
        selector: 'se-editable-list-default-item',
        template: `
        <div>
            <span>{{ node.uid }}</span>
            <se-dropdown-menu
                *ngIf="parent.editable"
                [dropdownItems]="parent.getDropdownItems()"
                [selectedItem]="node"
                class="pull-right se-tree-node__actions--more-menu"
                title="{{ 'se.contextmenu.title.more' | translate }}"></se-dropdown-menu>
        </div>
    `
    }),
    __param(1, core.Inject(TREE_NODE)),
    __metadata("design:paramtypes", [exports.EditableListComponent, Object])
], exports.EditableListDefaultItem);

exports.EditableListModule = class EditableListModule {
};
exports.EditableListModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.NgTreeModule, exports.DropdownMenuModule, utils.TranslationModule.forChild()],
        declarations: [exports.EditableListComponent, exports.EditableListDefaultItem],
        entryComponents: [exports.EditableListComponent, exports.EditableListDefaultItem],
        exports: [exports.EditableListComponent, exports.EditableListDefaultItem]
    })
], exports.EditableListModule);

exports.MessageType = void 0;
(function (MessageType) {
    MessageType["danger"] = "danger";
    MessageType["info"] = "info";
    MessageType["success"] = "success";
    MessageType["warning"] = "warning";
})(exports.MessageType || (exports.MessageType = {}));
window.__smartedit__.addDecoratorPayload("Component", "MessageComponent", {
    selector: 'se-message',
    template: `
        <div
            [attr.message-id]="messageId"
            class="fd-message-strip se-y-message"
            role="alert"
            [ngClass]="classes">
            <div class="y-message-text">
                <div class="y-message-info-title">
                    <ng-content select="[se-message-title]"></ng-content>
                </div>
                <div class="y-message-info-description">
                    <ng-content select="[se-message-description]"></ng-content>
                </div>
            </div>
        </div>
    `
});
/**
 *  This component provides contextual feedback messages for the user actions. To provide title and description for the se-essage
 *  use transcluded elements: se-message-title and se-message-description.
 *
 *  ### Example
 *
 *      <se-message>
 *          <div se-message-title>Title</div>
 *          <div se-message-description>Description</div>
 *      </se-message>
 */
exports.MessageComponent = class MessageComponent {
    ngOnInit() {
        this.messageId = this.messageId || 'y-message-default-id';
        switch (this.type) {
            case exports.MessageType.danger:
                this.classes = 'fd-message-strip--error';
                break;
            case exports.MessageType.info:
                this.classes = 'fd-message-strip--information';
                break;
            case exports.MessageType.success:
                this.classes = 'fd-message-strip--success';
                break;
            case exports.MessageType.warning:
                this.classes = 'fd-message-strip--warning';
                break;
            default:
                this.classes = 'fd-message-strip--information';
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MessageComponent.prototype, "messageId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MessageComponent.prototype, "type", void 0);
exports.MessageComponent = __decorate([
    core.Component({
        selector: 'se-message',
        template: `
        <div
            [attr.message-id]="messageId"
            class="fd-message-strip se-y-message"
            role="alert"
            [ngClass]="classes">
            <div class="y-message-text">
                <div class="y-message-info-title">
                    <ng-content select="[se-message-title]"></ng-content>
                </div>
                <div class="y-message-info-description">
                    <ng-content select="[se-message-description]"></ng-content>
                </div>
            </div>
        </div>
    `
    })
], exports.MessageComponent);

window.__smartedit__.addDecoratorPayload("Component", "EventMessageComponent", {
    selector: 'se-event-message',
    template: `
        <div>
            <se-message [type]="type" *ngIf="show">
                <ng-container *ngIf="showTitle()" se-message-title>
                    {{ title | translate }}
                </ng-container>
                <ng-container *ngIf="showDescription()" se-message-description>
                    {{ description | translate }}
                </ng-container>
            </se-message>
        </div>
    `
});
/**
 * Wrapper around se-message, used to display or hide the message based on events sent through the {@link SystemEventService}.
 */
exports.EventMessageComponent = class EventMessageComponent {
    constructor(systemEventService) {
        this.systemEventService = systemEventService;
        this.show = false;
    }
    ngOnInit() {
        this.show = this.showToStart === 'true' || this.showToStart === true;
        this.watchShowEventChange();
        this.watchHideEventChange();
    }
    ngOnDestroy() {
        this.removeShowEventHandler();
        this.removeHideEventHandler();
    }
    ngOnChanges(changes) {
        if (changes.showEvent) {
            this.watchShowEventChange();
        }
        if (changes.hideEvent) {
            this.watchHideEventChange();
        }
    }
    showDescription() {
        return typeof this.description === 'string' && this.description.length > 0;
    }
    showTitle() {
        return typeof this.title === 'string' && this.title.length > 0;
    }
    showEventHandler(eventId, eventData) {
        if (eventData.description && eventData.description.length) {
            this.description = eventData.description;
        }
        if (eventData.title && eventData.title.length) {
            this.title = eventData.title;
        }
        this.show = true;
    }
    removeHideEventHandler() {
        if (this.unregisterHideEventHandler) {
            this.unregisterHideEventHandler();
        }
    }
    removeShowEventHandler() {
        if (this.unregisterShowEventHandler) {
            this.unregisterShowEventHandler();
        }
    }
    watchShowEventChange() {
        this.removeShowEventHandler();
        this.unregisterShowEventHandler = this.systemEventService.subscribe(this.showEvent, (eventId, eventData) => this.showEventHandler(eventId, eventData));
    }
    watchHideEventChange() {
        this.removeHideEventHandler();
        this.unregisterHideEventHandler = this.systemEventService.subscribe(this.hideEvent, () => (this.show = false));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EventMessageComponent.prototype, "type", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EventMessageComponent.prototype, "title", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EventMessageComponent.prototype, "description", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EventMessageComponent.prototype, "showEvent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.EventMessageComponent.prototype, "hideEvent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.EventMessageComponent.prototype, "showToStart", void 0);
exports.EventMessageComponent = __decorate([
    core.Component({
        selector: 'se-event-message',
        template: `
        <div>
            <se-message [type]="type" *ngIf="show">
                <ng-container *ngIf="showTitle()" se-message-title>
                    {{ title | translate }}
                </ng-container>
                <ng-container *ngIf="showDescription()" se-message-description>
                    {{ description | translate }}
                </ng-container>
            </se-message>
        </div>
    `
    }),
    __metadata("design:paramtypes", [exports.SystemEventService])
], exports.EventMessageComponent);

/**
 * This module provides the se-message component, which is responsible for rendering contextual
 * feedback messages for the user actions.
 */
exports.MessageModule = class MessageModule {
};
exports.MessageModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$1.TranslateModule.forChild()],
        declarations: [exports.MessageComponent, exports.EventMessageComponent],
        entryComponents: [exports.MessageComponent, exports.EventMessageComponent],
        exports: [exports.MessageComponent, exports.EventMessageComponent]
    })
], exports.MessageModule);

window.__smartedit__.addDecoratorPayload("Component", "MoreTextComponent", {
    selector: 'se-more-text',
    template: `
        <span id="y-more-text-payload" class="se-more-text__payload">{{ text }}</span>
        <span
            id="y-more-text-toggle"
            class="se-more-text__toggle"
            [ngClass]="{ 'se-more-text__toggle--capitalize': capitalizeLabel }"
            (click)="showHideMoreText()"
            *ngIf="isTruncated"
            >{{ linkLabel }}</span
        >
    `,
    styles: [`.se-more-text__toggle--capitalize{text-transform:capitalize;color:var(--sapInformativeColor);cursor:pointer}`],
    providers: [TextTruncateService]
});
exports.MoreTextComponent = class MoreTextComponent {
    constructor(textTruncateService, translate) {
        this.textTruncateService = textTruncateService;
        this.translate = translate;
        this.capitalizeLabel = false;
        this.isTruncated = false;
        this.showingMore = false;
    }
    ngOnInit() {
        this.limit = this.limit || 100;
        this.moreLabelI18nKey = this.moreLabelI18nKey || 'se.moretext.more.link';
        this.lessLabelI18nKey = this.lessLabelI18nKey || 'se.moretext.less.link';
        this.truncatedText = this.textTruncateService.truncateToNearestWord(this.limit, this.text, this.ellipsis);
        this.isTruncated = this.truncatedText.isTruncated();
        this.translateLabels().subscribe(() => {
            this.showHideMoreText();
        });
    }
    showHideMoreText() {
        if (!this.isTruncated) {
            return;
        }
        this.text = this.showingMore
            ? this.truncatedText.getUntruncatedText()
            : this.truncatedText.getTruncatedText();
        this.linkLabel = this.showingMore ? this.lessLabel : this.moreLabel;
        this.showingMore = !this.showingMore;
    }
    translateLabels() {
        return rxjs.zip(this.translate.get(this.moreLabelI18nKey), this.translate.get(this.lessLabelI18nKey)).pipe(operators.tap(([more, less]) => {
            this.moreLabel = more;
            this.lessLabel = less;
        }));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MoreTextComponent.prototype, "text", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], exports.MoreTextComponent.prototype, "limit", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MoreTextComponent.prototype, "moreLabelI18nKey", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MoreTextComponent.prototype, "lessLabelI18nKey", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.MoreTextComponent.prototype, "ellipsis", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.MoreTextComponent.prototype, "capitalizeLabel", void 0);
exports.MoreTextComponent = __decorate([
    core.Component({
        selector: 'se-more-text',
        template: `
        <span id="y-more-text-payload" class="se-more-text__payload">{{ text }}</span>
        <span
            id="y-more-text-toggle"
            class="se-more-text__toggle"
            [ngClass]="{ 'se-more-text__toggle--capitalize': capitalizeLabel }"
            (click)="showHideMoreText()"
            *ngIf="isTruncated"
            >{{ linkLabel }}</span
        >
    `,
        styles: [`.se-more-text__toggle--capitalize{text-transform:capitalize;color:var(--sapInformativeColor);cursor:pointer}`],
        providers: [TextTruncateService]
    }),
    __metadata("design:paramtypes", [TextTruncateService,
        core$1.TranslateService])
], exports.MoreTextComponent);

exports.MoreTextModule = class MoreTextModule {
};
exports.MoreTextModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule],
        declarations: [exports.MoreTextComponent],
        entryComponents: [exports.MoreTextComponent],
        exports: [exports.MoreTextComponent]
    })
], exports.MoreTextModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const POPUP_OVERLAY_DATA = new core.InjectionToken('POPUP_OVERLAY_DATA');

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
exports.PopoverTrigger = void 0;
(function (PopoverTrigger) {
    PopoverTrigger["Hover"] = "hover";
    PopoverTrigger["Click"] = "click";
})(exports.PopoverTrigger || (exports.PopoverTrigger = {}));

window.__smartedit__.addDecoratorPayload("Component", "PopupOverlayComponent", {
    selector: 'se-popup-overlay',
    template: `<fd-popover [placement]="getPlacement()" [noArrow]="true" [triggers]="trigger && trigger.length ? [] : trigger" (isOpenChange)="handleOpenChange($event)" [additionalBodyClass]="'se-popup-overlay-container'" class="popup-overlay popover-outer" [ngClass]="popupOverlay.additionalClasses"><fd-popover-control><ng-content></ng-content></fd-popover-control><fd-popover-body #popoverBody class="popover-inner popup-overlay__inner"><ng-container *ngIf="isOpen"><ng-container *ngIf="popupOverlay.component"><ng-container *ngComponentOutlet="
                        popupOverlay.component;
                        injector: popupOverlayInjector
                    "></ng-container></ng-container><ng-content select="[se-popup-overlay-body]"></ng-content></ng-container></fd-popover-body></fd-popover>`,
    styles: [`.se-popup-overlay-container{z-index:1060!important}`],
    encapsulation: core.ViewEncapsulation.None
});
exports.PopupOverlayComponent = class PopupOverlayComponent {
    constructor(injector) {
        this.injector = injector;
        this.popupOverlayOnShow = new core.EventEmitter();
        this.popupOverlayOnHide = new core.EventEmitter();
        this.trigger = [];
    }
    ngOnInit() {
        this.createInjector();
        this.setTrigger();
    }
    ngOnChanges(changes) {
        if (changes.popupOverlayTrigger) {
            this.setTrigger();
        }
        if (changes.popupOverlayData) {
            this.createInjector();
        }
    }
    handleOpenChange(isOpen) {
        return isOpen ? this.handleOpen() : this.handleClose();
    }
    handleOpen() {
        this.isOpen = true;
        this.popupOverlayOnShow.emit();
    }
    handleClose() {
        this.isOpen = false;
        this.popupOverlayOnHide.emit();
    }
    getPlacement() {
        return `${this.popupOverlay.valign || 'bottom'}-${this.getHorizontalAlign()}`;
    }
    updatePosition() {
        this.popover.refreshPosition();
    }
    setTrigger() {
        if (this.popupOverlayTrigger === exports.PopoverTrigger.Click) {
            this.trigger = [this.popupOverlayTrigger];
        }
        if (this.popupOverlayTrigger === exports.PopoverTrigger.Hover) {
            this.trigger = ['mouseenter', 'mouseleave'];
        }
        if (!this.popover) {
            return;
        }
        if (this.popupOverlayTrigger === 'true' || this.popupOverlayTrigger === true) {
            this.popover.open();
        }
        else {
            this.popover.close();
        }
    }
    getHorizontalAlign() {
        if (!this.popupOverlay.halign) {
            return 'start';
        }
        return this.popupOverlay.halign === 'right' ? 'start' : 'end';
    }
    createInjector() {
        this.popupOverlayInjector = core.Injector.create({
            providers: [
                {
                    provide: POPUP_OVERLAY_DATA,
                    useValue: this.popupOverlayData || {}
                }
            ],
            parent: this.injector
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PopupOverlayComponent.prototype, "popupOverlay", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PopupOverlayComponent.prototype, "popupOverlayTrigger", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PopupOverlayComponent.prototype, "popupOverlayData", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.PopupOverlayComponent.prototype, "popupOverlayOnShow", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.PopupOverlayComponent.prototype, "popupOverlayOnHide", void 0);
__decorate([
    core.ViewChild(core$2.PopoverComponent, { static: false }),
    __metadata("design:type", core$2.PopoverComponent)
], exports.PopupOverlayComponent.prototype, "popover", void 0);
exports.PopupOverlayComponent = __decorate([
    core.Component({
        selector: 'se-popup-overlay',
        template: `<fd-popover [placement]="getPlacement()" [noArrow]="true" [triggers]="trigger && trigger.length ? [] : trigger" (isOpenChange)="handleOpenChange($event)" [additionalBodyClass]="'se-popup-overlay-container'" class="popup-overlay popover-outer" [ngClass]="popupOverlay.additionalClasses"><fd-popover-control><ng-content></ng-content></fd-popover-control><fd-popover-body #popoverBody class="popover-inner popup-overlay__inner"><ng-container *ngIf="isOpen"><ng-container *ngIf="popupOverlay.component"><ng-container *ngComponentOutlet="
                        popupOverlay.component;
                        injector: popupOverlayInjector
                    "></ng-container></ng-container><ng-content select="[se-popup-overlay-body]"></ng-content></ng-container></fd-popover-body></fd-popover>`,
        styles: [`.se-popup-overlay-container{z-index:1060!important}`],
        encapsulation: core.ViewEncapsulation.None
    }),
    __metadata("design:paramtypes", [core.Injector])
], exports.PopupOverlayComponent);

exports.PopupOverlayModule = class PopupOverlayModule {
};
exports.PopupOverlayModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$2.PopoverModule],
        declarations: [exports.PopupOverlayComponent],
        entryComponents: [exports.PopupOverlayComponent],
        exports: [exports.PopupOverlayComponent]
    })
], exports.PopupOverlayModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const STYLE_MAX_HEIGHT_PX = 'max-height.px';

window.__smartedit__.addDecoratorPayload("Component", "PreventVerticalOverflowComponent", {
    selector: 'se-prevent-vertical-overflow',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `
        <div #container [ngStyle]="containerStyle">
            <ng-content></ng-content>
        </div>
    `
});
/**
 * Component used to detect whether it's children are vertically overflowing the document.
 *
 * In case of overflow it sets it's max-height to the difference between top position and document bottom, and enables scrolling.
 */
exports.PreventVerticalOverflowComponent = class PreventVerticalOverflowComponent {
    constructor(document, cdr) {
        this.document = document;
        this.cdr = cdr;
        this.containerStyle = {
            'max-height.px': null,
            overflow: null
        };
    }
    onWindowResize() {
        if (this.containerElement) {
            this.onResize();
        }
    }
    ngAfterViewInit() {
        // when user opens popover, it will be called once to check for overflow
        this.observer = new IntersectionObserver(([event]) => {
            if (event.isIntersecting) {
                this.onResize();
            }
        }, { root: this.document, threshold: 0 });
        this.observer.observe(this.containerElement.nativeElement);
    }
    ngOnDestroy() {
        this.observer.unobserve(this.containerElement.nativeElement);
    }
    onResize() {
        const { top, height } = this.getContainerBoundingClientRect();
        // popover is not opened
        if (top === 0 && height === 0) {
            return;
        }
        this.preventOverflow(top, height);
    }
    preventOverflow(containerTop, containerHeight) {
        const didPrevent = this.preventIfOverflowing(containerTop, containerHeight);
        // If I zoom out, "didPrevent" may return false because it has set overflow and max-height.
        // In such a case, I need either to stop preventing overflow or stretch the container height for better UX.
        // To achieve that, I display it without max-height being set and then I check again whether it is overflowing.
        // If it is, I stretch the container. If it doesn't, remove max-height and overflow styles so it sets the height based on its content.
        if (!didPrevent && this.containerStyle[STYLE_MAX_HEIGHT_PX]) {
            this.reset();
            const { top, height } = this.getContainerBoundingClientRect();
            this.preventIfOverflowing(top, height);
        }
    }
    preventIfOverflowing(top, height) {
        if (this.isOverflowing(top, height)) {
            this.prevent(top);
            return true;
        }
        return false;
    }
    isOverflowing(top, height) {
        const documentHeight = this.getDocumentHeight();
        return documentHeight < top + height;
    }
    prevent(elementTop) {
        const documentHeight = this.getDocumentHeight();
        const maxHeight = documentHeight - elementTop;
        this.containerStyle[STYLE_MAX_HEIGHT_PX] = maxHeight;
        this.containerStyle.overflow = 'auto';
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    reset() {
        this.containerStyle[STYLE_MAX_HEIGHT_PX] = null;
        this.containerStyle.overflow = null;
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    getContainerBoundingClientRect() {
        return this.containerElement.nativeElement.getBoundingClientRect();
    }
    getDocumentHeight() {
        const { height } = this.document.documentElement.getBoundingClientRect();
        return height;
    }
};
__decorate([
    core.ViewChild('container', { static: false }),
    __metadata("design:type", core.ElementRef)
], exports.PreventVerticalOverflowComponent.prototype, "containerElement", void 0);
__decorate([
    core.HostListener('window:resize'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", void 0)
], exports.PreventVerticalOverflowComponent.prototype, "onWindowResize", null);
exports.PreventVerticalOverflowComponent = __decorate([
    core.Component({
        selector: 'se-prevent-vertical-overflow',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `
        <div #container [ngStyle]="containerStyle">
            <ng-content></ng-content>
        </div>
    `
    }),
    __param(0, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Document,
        core.ChangeDetectorRef])
], exports.PreventVerticalOverflowComponent);

exports.PreventVerticalOverflowModule = class PreventVerticalOverflowModule {
};
exports.PreventVerticalOverflowModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule],
        declarations: [exports.PreventVerticalOverflowComponent],
        entryComponents: [exports.PreventVerticalOverflowComponent],
        exports: [exports.PreventVerticalOverflowComponent]
    })
], exports.PreventVerticalOverflowModule);

window.__smartedit__.addDecoratorPayload("Component", "HelpComponent", {
    selector: 'se-help',
    template: `<se-tooltip [triggers]="['mouseenter', 'mouseleave']" placement="right" [isChevronVisible]="true" [title]="title" class="se-help"><span se-tooltip-trigger class="sap-icon--sys-help se-y-help-icon"></span><ng-content se-tooltip-body></ng-content></se-tooltip>`
});
exports.HelpComponent = class HelpComponent {
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.HelpComponent.prototype, "title", void 0);
exports.HelpComponent = __decorate([
    core.Component({
        selector: 'se-help',
        template: `<se-tooltip [triggers]="['mouseenter', 'mouseleave']" placement="right" [isChevronVisible]="true" [title]="title" class="se-help"><span se-tooltip-trigger class="sap-icon--sys-help se-y-help-icon"></span><ng-content se-tooltip-body></ng-content></se-tooltip>`
    })
], exports.HelpComponent);

exports.HelpModule = class HelpModule {
};
exports.HelpModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.TooltipModule],
        declarations: [exports.HelpComponent],
        entryComponents: [exports.HelpComponent],
        exports: [exports.HelpComponent]
    })
], exports.HelpModule);

window.__smartedit__.addDecoratorPayload("Component", "ShortcutLinkComponent", {
    selector: 'shortcut-link',
    template: `<ng-container #container><ng-template #defaultTemplate let-shortcutLink><div class="se-shortcut-link"><a class="se-shortcut-link__item" [ngClass]="{'se-shortcut-link__item--active': shortcutLink.active}" (click)="onClick(shortcutLink)">{{shortcutLink.titleI18nKey | translate}}</a></div></ng-template></ng-container>`
});
exports.ShortcutLinkComponent = class ShortcutLinkComponent {
    constructor(router, location, resolver, experienceService, priorityService, systemEventService, userTrackingService) {
        this.router = router;
        this.location = location;
        this.resolver = resolver;
        this.experienceService = experienceService;
        this.priorityService = priorityService;
        this.systemEventService = systemEventService;
        this.userTrackingService = userTrackingService;
    }
    ngOnInit() {
        this.unregFn = this.systemEventService.subscribe(utils.EVENTS.EXPERIENCE_UPDATE, () => this._createShortcutLink());
    }
    ngOnDestroy() {
        this.unregFn();
    }
    ngAfterViewInit() {
        this._createShortcutLink();
    }
    onClick(shortcutLink) {
        this.userTrackingService.trackingUserAction(USER_TRACKING_FUNCTIONALITY.NAVIGATION, shortcutLink.titleI18nKey);
        this.location.go(shortcutLink.fullPath);
        this.router.navigateByUrl(shortcutLink.fullPath);
    }
    _createShortcutLink() {
        const currentExperience = this.experienceService.getCurrentExperience();
        currentExperience.then((experience) => {
            const shortcutLinks = this._getShortcutLinks(experience);
            const orderedShortcutLinks = this._orderByPriority(shortcutLinks);
            this._createShortcutLinkDynamicComponents(orderedShortcutLinks);
        });
    }
    _createShortcutLinkDynamicComponents(shortcutLinks) {
        this.containerEntry.clear();
        shortcutLinks.forEach((shortcutLink) => {
            if (shortcutLink && shortcutLink.titleI18nKey) {
                this.containerEntry.createEmbeddedView(this.defaultTemplate, {
                    $implicit: shortcutLink
                });
            }
            else if (shortcutLink && shortcutLink.shortcutComponent) {
                const factory = this.resolver.resolveComponentFactory(shortcutLink.shortcutComponent);
                const component = this.containerEntry.createComponent(factory);
                component.instance.shortcutLink = shortcutLink;
            }
        });
    }
    _getShortcutLinks(experience) {
        const url = this.location.path();
        return SeRouteService.routeShortcutConfigs.map((routeShortcut) => {
            let active;
            const path = routeShortcut.fullPath
                .replace(':siteId', experience.catalogDescriptor.siteId)
                .replace(':catalogId', experience.catalogDescriptor.catalogId)
                .replace(':catalogVersion', experience.catalogDescriptor.catalogVersion);
            if (path.startsWith('/')) {
                active = path === url;
            }
            else {
                active = `/${path}` === url;
            }
            const shortcutLink = Object.assign({}, routeShortcut);
            shortcutLink.fullPath = path;
            shortcutLink.active = active;
            return shortcutLink;
        });
    }
    _orderByPriority(shortcutLinks) {
        shortcutLinks.forEach((shortcutLink) => (shortcutLink.priority = shortcutLink.priority || 500));
        return this.priorityService.sort(shortcutLinks);
    }
};
__decorate([
    core.ViewChild('container', { read: core.ViewContainerRef, static: false }),
    __metadata("design:type", core.ViewContainerRef)
], exports.ShortcutLinkComponent.prototype, "containerEntry", void 0);
__decorate([
    core.ViewChild('defaultTemplate', { read: core.TemplateRef, static: false }),
    __metadata("design:type", core.TemplateRef)
], exports.ShortcutLinkComponent.prototype, "defaultTemplate", void 0);
exports.ShortcutLinkComponent = __decorate([
    core.Component({
        selector: 'shortcut-link',
        template: `<ng-container #container><ng-template #defaultTemplate let-shortcutLink><div class="se-shortcut-link"><a class="se-shortcut-link__item" [ngClass]="{'se-shortcut-link__item--active': shortcutLink.active}" (click)="onClick(shortcutLink)">{{shortcutLink.titleI18nKey | translate}}</a></div></ng-template></ng-container>`
    }),
    __metadata("design:paramtypes", [router.Router,
        common.Location,
        core.ComponentFactoryResolver,
        IExperienceService,
        exports.PriorityService,
        exports.SystemEventService,
        IUserTrackingService])
], exports.ShortcutLinkComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class SliderPanelZIndexHelper {
    /** Retrieve a highest value from array of zIndex integers. */
    getHighestZIndex(node) {
        return Math.max(...this.getChildrenNodesFromTreeOrLeaf(node[0])
            .filter((elem) => this.filterBlacklistedNodes(elem))
            .map((elem) => this.mapToZIndexIntegers(elem)));
    }
    filterBlacklistedNodes(elem) {
        return !SliderPanelZIndexHelper.BLACKLISTED_NODE_NAMES.has(elem.nodeName);
    }
    /** Retrieve zIndex integer value from node, fallback with 0 value in case of NaN. */
    mapToZIndexIntegers(elem) {
        return (parseInt(window.getComputedStyle(elem).getPropertyValue('z-index'), 10) ||
            parseInt(elem.style.zIndex, 10) ||
            0);
    }
    /** Return recurring flat array of node and it's children. */
    getChildrenNodesFromTreeOrLeaf(node) {
        return [
            node,
            ...lodash.flatten(Array.from(node.children).map((child) => this.getChildrenNodesFromTreeOrLeaf(child)))
        ];
    }
}
SliderPanelZIndexHelper.BLACKLISTED_NODE_NAMES = new Set(['SCRIPT', 'LINK', 'BASE']);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * The SliderPanelService handles the initialization and the rendering of the se-slider-panel Angular component.
 */
class SliderPanelService {
    constructor(element, window, configuration, yjQuery) {
        this.element = element;
        this.window = window;
        this.configuration = configuration;
        this.yjQuery = yjQuery;
        this.inlineStyling = { container: {}, content: {} };
        this.sliderPanelDefaultConfiguration = {
            slideFrom: 'right',
            overlayDimension: '80%'
        };
        this.init();
    }
    /**
     * This method sets the inline styling applied to the slider panel container according to the dimension and position values
     * of the parent element.
     */
    updateContainerInlineStyling(screenResized) {
        const parentClientRect = this.parent[0].getBoundingClientRect();
        const borders = {
            left: this.parent.css('border-left-width')
                ? parseInt(this.parent.css('border-left-width').replace('px', ''), 10)
                : 0,
            top: this.parent.css('border-top-width')
                ? parseInt(this.parent.css('border-top-width').replace('px', ''), 10)
                : 0
        };
        this.inlineStyling.container.height = this.parent[0].clientHeight + 'px';
        this.inlineStyling.container.width = this.parent[0].clientWidth + 'px';
        this.inlineStyling.container.left =
            (this.appendChildTarget.nodeName === 'BODY'
                ? Math.round(parentClientRect.left + this.window.pageXOffset + borders.left)
                : 0) + 'px';
        this.inlineStyling.container.top =
            (this.appendChildTarget.nodeName === 'BODY'
                ? Math.round(parentClientRect.top + this.window.pageYOffset + borders.top)
                : 0) + 'px';
        // z-index value is not set during screen resize
        if (!screenResized) {
            this.inlineStyling.container.zIndex = this.sliderPanelConfiguration.zIndex
                ? this.sliderPanelConfiguration.zIndex.toString()
                : (this.returningHigherZIndex() + 1).toString();
        }
    }
    returningHigherZIndex() {
        return new SliderPanelZIndexHelper().getHighestZIndex(this.yjQuery('body'));
    }
    initializeParentRawElement() {
        // instantiating "parent" local variable
        const parentRawElement = this.sliderPanelConfiguration.cssSelector
            ? document.querySelector(this.sliderPanelConfiguration.cssSelector)
            : null;
        this.parent = this.yjQuery(parentRawElement || this.element.parent());
    }
    initializePanelConfiguration() {
        // defining the configuration set on the processed slider panel by merging the JSON object provided as parameter
        // with the default configuration
        this.sliderPanelConfiguration = lodash.defaultsDeep(this.configuration, this.sliderPanelDefaultConfiguration);
    }
    initializeInlineStyles() {
        // setting the inline styling applied on the slider panel content according to its configuration.
        const key = ['top', 'bottom'].indexOf(this.sliderPanelConfiguration.slideFrom) === -1
            ? 'width'
            : 'height';
        this.inlineStyling.content[key] = this.sliderPanelConfiguration.overlayDimension;
    }
    initializeChildTarget() {
        // instantiating "appendChildTarget" local variable
        let testedElement = this.parent;
        let modalFound = false;
        let i = 0;
        for (i; testedElement[0] && testedElement[0].nodeName !== 'BODY'; testedElement = this.yjQuery(testedElement.parent()), i++) {
            const isFundamentalModal = (testedElement[0].getAttribute('class') || '').includes('fd-dialog__content');
            const isLegacyModal = testedElement[0].getAttribute('id') === 'y-modal-dialog';
            if (isFundamentalModal || isLegacyModal) {
                modalFound = true;
                break;
            }
        }
        this.appendChildTarget = modalFound ? testedElement[0] : document.body;
    }
    append() {
        // appending the slider panel HTML tag as last child of the HTML body tag.
        this.yjQuery(this.appendChildTarget).append(this.element[0]);
    }
    init() {
        this.initializePanelConfiguration();
        this.initializeParentRawElement();
        this.initializeInlineStyles();
        this.initializeChildTarget();
        this.append();
    }
}

exports.SliderPanelServiceFactory = class SliderPanelServiceFactory {
    constructor(yjQuery) {
        this.yjQuery = yjQuery;
    }
    /**
     * Set and returns a new instance of the slider panel.
     */
    getNewServiceInstance(element, window, configuration) {
        return new SliderPanelService(element, window, configuration, this.yjQuery);
    }
};
exports.SliderPanelServiceFactory = __decorate([
    __param(0, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function])
], exports.SliderPanelServiceFactory);

const CSS_CLASSNAMES = {
    SLIDERPANEL_ANIMATED: 'sliderpanel--animated',
    SLIDERPANEL_SLIDEPREFIX: 'sliderpanel--slidefrom'
};
window.__smartedit__.addDecoratorPayload("Component", "SliderPanelComponent", {
    selector: 'se-slider-panel',
    template: `<div class="se-slider-panel-wrapper" *ngIf="isShown"><div class="se-slider-panel-container slide-in"><div class="se-slider-panel__header" *ngIf="sliderPanelConfiguration.modal"><span class="se-slider-panel__title" *ngIf="sliderPanelConfiguration.modal.title">{{ sliderPanelConfiguration.modal.title | translate }}</span> <button type="button" class="se-slider-panel__close-btn fd-button fd-button--transparent fd-dialog__close" aria-label="close" *ngIf="sliderPanelConfiguration.modal.showDismissButton" (click)="sliderPanelDismissAction()" title="{{ 'se.btn.close.title' | translate }}"><span class="sap-icon--decline"></span></button></div><div class="se-slider-panel__body"><ng-content></ng-content></div><div class="se-slider-panel__footer" *ngIf="sliderPanelConfiguration.modal && (sliderPanelConfiguration.modal.cancel || sliderPanelConfiguration.modal.save)"><button type="button" class="fd-button fd-button--emphasized se-slider-panel__footer-btn se-slider-panel__footer-btn--save" *ngIf="sliderPanelConfiguration.modal.save" (click)="sliderPanelConfiguration.modal.save.onClick()" [disabled]="isSaveDisabled()">{{ sliderPanelConfiguration.modal.save.label | translate }}</button> <button type="button" class="fd-button fd-button--transparent se-slider-panel__footer-btn se-slider-panel__footer-btn--cancel" *ngIf="sliderPanelConfiguration.modal.cancel" (click)="sliderPanelConfiguration.modal.cancel.onClick()">{{ sliderPanelConfiguration.modal.cancel.label | translate }}</button></div></div></div>`,
    styles: [`.se-slider-panel-wrapper{position:fixed;display:flex;justify-content:center;overflow:hidden;width:100%;top:0;left:0;z-index:999}.se-slider-panel-container{display:flex;flex-direction:column;transform:translateX(100%);-webkit-transform:translateX(100%);background-color:var(--sapBaseColor);border-radius:4px;width:650px}.se-slider-panel__header{padding:20px;display:flex;flex-direction:row;align-items:center;justify-content:space-between;border-bottom:1px solid var(--sapGroup_ContentBorderColor);height:53px}.se-slider-panel__header:first-child{font-size:1rem!important;line-height:1.25;font-weight:400;color:var(--sapTextColor)}.se-slider-panel__body{padding:20px;overflow-y:auto;max-height:70vh;height:70vh;min-height:500px;flex:1 1 auto}.se-slider-panel__footer{padding:20px;display:flex;flex-direction:row;align-items:center;justify-content:flex-end;border-top:1px solid var(--sapGroup_ContentBorderColor);height:69px}.se-slider-panel__footer button{text-transform:capitalize;margin-left:12px}.slide-in{animation:slide-in .4s forwards;-webkit-animation:slide-in .4s forwards}@keyframes slide-in{100%{transform:translateX(0)}}@-webkit-keyframes slide-in{100%{-webkit-transform:translateX(0)}}:host-context(fd-dialog-container).sliderpanel--animated{position:fixed;left:0;top:0;width:100%;height:100%;background-color:rgba(0,0,0,.5);align-items:center;display:flex;z-index:1000}:host-context(fd-dialog-container).sliderpanel--animated .se-slider-panel-wrapper{top:auto}:host-context(fd-dialog-container).sliderpanel--animated .se-slider-panel-wrapper .se-slider-panel__body{max-height:calc(100vh - 128px);min-height:auto}`]
});
exports.SliderPanelComponent = class SliderPanelComponent {
    constructor(renderer, element, windowUtils, yjQuery, sliderPanelServiceFactory) {
        this.renderer = renderer;
        this.element = element;
        this.windowUtils = windowUtils;
        this.yjQuery = yjQuery;
        this.sliderPanelServiceFactory = sliderPanelServiceFactory;
        this.sliderPanelHideChange = new core.EventEmitter();
        this.sliderPanelShowChange = new core.EventEmitter();
        this.isShownChange = new core.EventEmitter();
        this.inlineStyling = { container: {}, content: {} };
    }
    ngOnInit() {
        this.isShown = false;
        this.uniqueId = stringUtils.generateIdentifier();
        this.sliderPanelService = this.sliderPanelServiceFactory.getNewServiceInstance(this.yjQuery(this.element.nativeElement), this.windowUtils.getWindow(), this.sliderPanelConfiguration);
        this.sliderPanelConfiguration = this.sliderPanelService.sliderPanelConfiguration;
        this.slideClassName =
            CSS_CLASSNAMES.SLIDERPANEL_SLIDEPREFIX + this.sliderPanelConfiguration.slideFrom;
        this.inlineStyling = {
            container: this.sliderPanelService.inlineStyling.container,
            content: this.sliderPanelService.inlineStyling.content
        };
        setTimeout(() => {
            this.sliderPanelShowChange.emit(() => this.showSlider());
            this.sliderPanelHideChange.emit(() => this.hideSlider());
        });
        this.sliderPanelDismissAction =
            this.sliderPanelConfiguration.modal &&
                this.sliderPanelConfiguration.modal.dismiss &&
                this.sliderPanelConfiguration.modal.dismiss.onClick
                ? this.sliderPanelConfiguration.modal.dismiss.onClick
                : this.hideSlider;
        this.addScreenResizeEventHandler();
        if (this.sliderPanelConfiguration.displayedByDefault) {
            this.showSlider();
        }
    }
    ngOnDestroy() {
        this.yjQuery(this.windowUtils.getWindow()).off('resize.doResize');
    }
    hideSlider() {
        return new Promise((resolve) => {
            this.renderer.removeClass(this.element.nativeElement, CSS_CLASSNAMES.SLIDERPANEL_ANIMATED);
            this.isShown = false;
            this.isShownChange.emit(this.isShown);
            resolve();
        });
    }
    showSlider() {
        return new Promise((resolve) => {
            this.sliderPanelService.updateContainerInlineStyling(false);
            this.inlineStyling.container = this.sliderPanelService.inlineStyling.container;
            let isSecondarySliderPanel = false;
            this.yjQuery('se-slider-panel.sliderpanel--animated .se-slider-panel-container')
                .toArray()
                .forEach((sliderPanelContainer) => {
                const container = this.yjQuery(sliderPanelContainer);
                if (!isSecondarySliderPanel) {
                    if (container.css('height') === this.inlineStyling.container.height &&
                        container.css('width') === this.inlineStyling.container.width &&
                        container.css('left') === this.inlineStyling.container.left &&
                        container.css('top') === this.inlineStyling.container.top) {
                        isSecondarySliderPanel = true;
                    }
                }
            });
            this.sliderPanelConfiguration.noGreyedOutOverlay =
                typeof this.sliderPanelConfiguration.noGreyedOutOverlay === 'boolean'
                    ? this.sliderPanelConfiguration.noGreyedOutOverlay
                    : isSecondarySliderPanel;
            this.isShown = true;
            this.isShownChange.emit(this.isShown);
            this.renderer.addClass(this.element.nativeElement, CSS_CLASSNAMES.SLIDERPANEL_ANIMATED);
            resolve();
        });
    }
    isSaveDisabled() {
        if (this.sliderPanelConfiguration.modal &&
            this.sliderPanelConfiguration.modal.save &&
            this.sliderPanelConfiguration.modal.save.isDisabledFn) {
            return this.sliderPanelConfiguration.modal.save.isDisabledFn();
        }
        return false;
    }
    addScreenResizeEventHandler() {
        this.yjQuery(this.windowUtils.getWindow()).on('resize.sliderPanelRedraw_' + this.uniqueId, () => {
            if (this.isShown) {
                setTimeout(() => {
                    this.sliderPanelService.updateContainerInlineStyling(true);
                    this.inlineStyling.container =
                        this.sliderPanelService.inlineStyling.container;
                }, 0);
            }
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SliderPanelComponent.prototype, "sliderPanelConfiguration", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SliderPanelComponent.prototype, "sliderPanelHide", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SliderPanelComponent.prototype, "sliderPanelShow", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.SliderPanelComponent.prototype, "sliderPanelHideChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.SliderPanelComponent.prototype, "sliderPanelShowChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.SliderPanelComponent.prototype, "isShownChange", void 0);
__decorate([
    core.ContentChild(core.TemplateRef, { static: false }),
    __metadata("design:type", core.TemplateRef)
], exports.SliderPanelComponent.prototype, "content", void 0);
exports.SliderPanelComponent = __decorate([
    core.Component({
        selector: 'se-slider-panel',
        template: `<div class="se-slider-panel-wrapper" *ngIf="isShown"><div class="se-slider-panel-container slide-in"><div class="se-slider-panel__header" *ngIf="sliderPanelConfiguration.modal"><span class="se-slider-panel__title" *ngIf="sliderPanelConfiguration.modal.title">{{ sliderPanelConfiguration.modal.title | translate }}</span> <button type="button" class="se-slider-panel__close-btn fd-button fd-button--transparent fd-dialog__close" aria-label="close" *ngIf="sliderPanelConfiguration.modal.showDismissButton" (click)="sliderPanelDismissAction()" title="{{ 'se.btn.close.title' | translate }}"><span class="sap-icon--decline"></span></button></div><div class="se-slider-panel__body"><ng-content></ng-content></div><div class="se-slider-panel__footer" *ngIf="sliderPanelConfiguration.modal && (sliderPanelConfiguration.modal.cancel || sliderPanelConfiguration.modal.save)"><button type="button" class="fd-button fd-button--emphasized se-slider-panel__footer-btn se-slider-panel__footer-btn--save" *ngIf="sliderPanelConfiguration.modal.save" (click)="sliderPanelConfiguration.modal.save.onClick()" [disabled]="isSaveDisabled()">{{ sliderPanelConfiguration.modal.save.label | translate }}</button> <button type="button" class="fd-button fd-button--transparent se-slider-panel__footer-btn se-slider-panel__footer-btn--cancel" *ngIf="sliderPanelConfiguration.modal.cancel" (click)="sliderPanelConfiguration.modal.cancel.onClick()">{{ sliderPanelConfiguration.modal.cancel.label | translate }}</button></div></div></div>`,
        styles: [`.se-slider-panel-wrapper{position:fixed;display:flex;justify-content:center;overflow:hidden;width:100%;top:0;left:0;z-index:999}.se-slider-panel-container{display:flex;flex-direction:column;transform:translateX(100%);-webkit-transform:translateX(100%);background-color:var(--sapBaseColor);border-radius:4px;width:650px}.se-slider-panel__header{padding:20px;display:flex;flex-direction:row;align-items:center;justify-content:space-between;border-bottom:1px solid var(--sapGroup_ContentBorderColor);height:53px}.se-slider-panel__header:first-child{font-size:1rem!important;line-height:1.25;font-weight:400;color:var(--sapTextColor)}.se-slider-panel__body{padding:20px;overflow-y:auto;max-height:70vh;height:70vh;min-height:500px;flex:1 1 auto}.se-slider-panel__footer{padding:20px;display:flex;flex-direction:row;align-items:center;justify-content:flex-end;border-top:1px solid var(--sapGroup_ContentBorderColor);height:69px}.se-slider-panel__footer button{text-transform:capitalize;margin-left:12px}.slide-in{animation:slide-in .4s forwards;-webkit-animation:slide-in .4s forwards}@keyframes slide-in{100%{transform:translateX(0)}}@-webkit-keyframes slide-in{100%{-webkit-transform:translateX(0)}}:host-context(fd-dialog-container).sliderpanel--animated{position:fixed;left:0;top:0;width:100%;height:100%;background-color:rgba(0,0,0,.5);align-items:center;display:flex;z-index:1000}:host-context(fd-dialog-container).sliderpanel--animated .se-slider-panel-wrapper{top:auto}:host-context(fd-dialog-container).sliderpanel--animated .se-slider-panel-wrapper .se-slider-panel__body{max-height:calc(100vh - 128px);min-height:auto}`]
    }),
    __param(3, core.Inject(YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [core.Renderer2,
        core.ElementRef,
        exports.WindowUtils, Function, exports.SliderPanelServiceFactory])
], exports.SliderPanelComponent);

exports.SliderPanelModule = class SliderPanelModule {
};
exports.SliderPanelModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, utils.TranslationModule.forChild()],
        declarations: [exports.SliderPanelComponent],
        entryComponents: [exports.SliderPanelComponent],
        providers: [exports.SliderPanelServiceFactory],
        exports: [exports.SliderPanelComponent]
    })
], exports.SliderPanelModule);

/**
 * Used to listen to ElementRef resize event.
 *
 * It emits an event once the {@link https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver ResizeObserver}
 * detects the change.
 *
 * ### Example
 *
 *      <my-custom-component seResizeObserver (onResize)="handleResize()"></my-custom-component>
 */
exports.ResizeObserverDirective = class ResizeObserverDirective {
    constructor(elementRef) {
        this.elementRef = elementRef;
        this.onResize = new core.EventEmitter();
    }
    ngOnInit() {
        this.startWatching();
    }
    ngOnDestroy() {
        this.observer.disconnect();
    }
    startWatching() {
        this.observer = new resizeObserver.ResizeObserver((entries) => this.internalOnResize(entries));
        this.observer.observe(this.elementRef.nativeElement);
    }
    internalOnResize(entries) {
        this.onResize.emit();
    }
};
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.ResizeObserverDirective.prototype, "onResize", void 0);
exports.ResizeObserverDirective = __decorate([
    core.Directive({
        selector: '[seResizeObserver]'
    }),
    __metadata("design:paramtypes", [core.ElementRef])
], exports.ResizeObserverDirective);

exports.ResizeObserverModule = class ResizeObserverModule {
};
exports.ResizeObserverModule = __decorate([
    core.NgModule({
        declarations: [exports.ResizeObserverDirective],
        exports: [exports.ResizeObserverDirective]
    })
], exports.ResizeObserverModule);

/** @ignore */
class HasOperationPermissionBaseDirective {
    constructor(systemEventService, permissionService, logService) {
        this.systemEventService = systemEventService;
        this.permissionService = permissionService;
        this.logService = logService;
    }
    ngOnInit() {
        // NOTE: Refreshing permission checking should only be done after permissions have been cleaned
        // (PERMISSION_CACHE_CLEANED). If this is done as soon after user is changed (USER_CHANGED) then there's a race
        // condition between when the cache is cleaned and when this permission checking is executed.
        this.unregisterHandler = this.systemEventService.subscribe(utils.EVENTS.PERMISSION_CACHE_CLEANED, this.refreshIsPermissionGranted.bind(this));
    }
    ngOnDestroy() {
        this.unregisterHandler();
    }
    ngOnChanges(changes) {
        if (changes.hasOperationPermission && changes.hasOperationPermission.currentValue) {
            this.permission = changes.hasOperationPermission.currentValue;
            this.refreshIsPermissionGranted();
        }
    }
    set isPermissionGrantedHandler(handler) {
        this._isPermissionGrantedHandler = handler;
    }
    refreshIsPermissionGranted() {
        this.isPermissionGranted(this.permission).then((isPermissionGranted) => {
            this._isPermissionGrantedHandler(isPermissionGranted);
        });
    }
    isPermissionGranted(permission) {
        return this.permissionService
            .isPermitted(this.validateAndPreparePermissions(permission))
            .then((isPermissionGranted) => isPermissionGranted, (error) => {
            this.logService.error('Failed to retrieve authorization', error);
            return false;
        })
            .then((isPermissionGranted) => isPermissionGranted);
    }
    validateAndPreparePermissions(permissions) {
        if (typeof permissions !== 'string' && !Array.isArray(permissions)) {
            throw new Error('Permission should be string or an array of objects');
        }
        return typeof permissions === 'string' ? this.toPermissions(permissions) : permissions;
    }
    toPermissions(permissions) {
        return [{ names: permissions.split(',') }];
    }
}

/**
 * An Authorization structural directive that conditionally will remove elements from the DOM if the user does not have authorization defined
 * by the input parameter permission keys.
 *
 * This directive makes use of the {@link IPermissionService} service to validate
 * if the current user has access to the given permission set.
 *
 * It takes a comma-separated list of permission names or an array of permission name objects structured as follows:
 *
 * ### Example
 *
 * 1. String
 * 'se-edit-page'
 *
 * 2. Object
 *
 *          {
 *              names: ["permission1", "permission2"],
 *              context: {
 *                  data: "with the context property, extra data can be included to check a permission when the Rule.verify function is called"
 *              }
 *          }
 */
let HasOperationPermissionDirective = class HasOperationPermissionDirective extends HasOperationPermissionBaseDirective {
    constructor(templateRef, viewContainerRef, systemEventService, permissionService, logService, cdr) {
        super(systemEventService, permissionService, logService);
        this.templateRef = templateRef;
        this.viewContainerRef = viewContainerRef;
        this.cdr = cdr;
        this.hasView = false;
        this.isPermissionGrantedHandler = this.getIsPermissionGrantedHandler();
    }
    ngOnInit() {
        super.ngOnInit();
    }
    ngOnChanges(changes) {
        super.ngOnChanges({
            hasOperationPermission: new core.SimpleChange(changes.seHasOperationPermission.previousValue, changes.seHasOperationPermission.currentValue, changes.seHasOperationPermission.firstChange)
        });
    }
    ngOnDestroy() {
        super.ngOnDestroy();
    }
    getIsPermissionGrantedHandler() {
        return (isPermissionGranted) => {
            this.updateView(isPermissionGranted);
        };
    }
    updateView(isPermissionGranted) {
        if (isPermissionGranted && !this.hasView) {
            this.viewContainerRef.createEmbeddedView(this.templateRef);
            this.hasView = true;
        }
        else if (!isPermissionGranted && this.hasView) {
            this.viewContainerRef.clear();
        }
        this.cdr.markForCheck();
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], HasOperationPermissionDirective.prototype, "seHasOperationPermission", void 0);
HasOperationPermissionDirective = __decorate([
    core.Directive({ selector: '[seHasOperationPermission]' }),
    __param(3, core.Inject(IPermissionService)),
    __metadata("design:paramtypes", [core.TemplateRef,
        core.ViewContainerRef,
        exports.SystemEventService,
        IPermissionService,
        utils.LogService,
        core.ChangeDetectorRef])
], HasOperationPermissionDirective);

exports.HasOperationPermissionDirectiveModule = class HasOperationPermissionDirectiveModule {
};
exports.HasOperationPermissionDirectiveModule = __decorate([
    core.NgModule({
        declarations: [HasOperationPermissionDirective],
        exports: [HasOperationPermissionDirective]
    })
], exports.HasOperationPermissionDirectiveModule);

exports.ClickOutsideDirective = class ClickOutsideDirective {
    constructor(document, host, iframeClickDetectionService) {
        this.document = document;
        this.host = host;
        this.iframeClickDetectionService = iframeClickDetectionService;
        this.clickOutside = new core.EventEmitter();
        this.id = `clickOutsideIframeClick${stringUtils.generateIdentifier()}`;
    }
    onDocumentClick(target) {
        if (target === this.host.nativeElement ||
            this.host.nativeElement.contains(target) ||
            (this.popBody &&
                (this.popBody._elementRef.nativeElement.contains(target) ||
                    target === this.popBody._elementRef.nativeElement)) ||
            this.ignoreNotExistingTarget(target)) {
            return;
        }
        this.clickOutside.emit();
    }
    ngOnInit() {
        this.iframeClickDetectionService.registerCallback(this.id, () => this.onOutsideClick());
    }
    ngOnDestroy() {
        this.iframeClickDetectionService.removeCallback(this.id);
    }
    ignoreNotExistingTarget(target) {
        return !this.document.contains(target);
    }
    onOutsideClick() {
        this.clickOutside.emit();
    }
};
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.ClickOutsideDirective.prototype, "clickOutside", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core$2.PopoverBodyComponent)
], exports.ClickOutsideDirective.prototype, "popBody", void 0);
__decorate([
    core.HostListener('document:click', ['$event.target']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [HTMLElement]),
    __metadata("design:returntype", void 0)
], exports.ClickOutsideDirective.prototype, "onDocumentClick", null);
exports.ClickOutsideDirective = __decorate([
    core.Directive({
        selector: '[seClickOutside]'
    }),
    __param(0, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Document,
        core.ElementRef,
        IIframeClickDetectionService])
], exports.ClickOutsideDirective);

exports.ClickOutsideModule = class ClickOutsideModule {
};
exports.ClickOutsideModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule],
        declarations: [exports.ClickOutsideDirective],
        exports: [exports.ClickOutsideDirective]
    })
], exports.ClickOutsideModule);

/**
 * @ignore
 *
 * Used for rendering dynamic components decorated with {@link SeCustomComponent}.
 * It is meant for configurations that requires component to be sent through {@link MessageGateway} in postMessage payload.
 * Instead `component` use `componentName` as a configuration parameter.
 *
 * Due to {@link https://developer.mozilla.org/en-US/docs/Web/API/Window/postMessage postMessageApi},
 * component class / constructor function, cannot be sent through postMessage (it is removed).
 *
 * Note: Component must be also registered in @NgModule entryComponents array.
 *
 * ### Example
 *
 *
 *      \@SeCustomComponent()
 *      \@Component({
 *          selector: 'se-my-custom-component',
 *          templateUrl: './SeMyComponent.html'
 *      })
 *      export class MyCustomComponent {}
 *
 *      \@Component({
 *          selector: 'se-my-container',
 *          template: `<div [seCustomComponentOutlet]="'MyCustomComponent'"></div>`
 *      })
 *      export class MyContainer {}
 *
 *      \@NgModule({
 *          imports: [CustomComponentOutletDirectiveModule]
 *          declarations: [MyContainer, MyCustomComponent],
 *          entryComponents: [MyContainer, MyCustomComponent]
 *      })
 *      export class MyModule {}
 *
 */
exports.CustomComponentOutletDirective = class CustomComponentOutletDirective {
    constructor(elementRef, renderer) {
        this.elementRef = elementRef;
        this.renderer = renderer;
        this.hasView = false;
    }
    ngOnChanges() {
        this.updateView(this.componentName);
    }
    updateView(componentName) {
        if (componentName && !this.hasView) {
            this.createView();
        }
        else if (!componentName && this.hasView) {
            this.removeView();
        }
        else if (componentName && this.hasView) {
            this.removeView();
            this.createView();
        }
    }
    createView() {
        const componentMetadata = window.__smartedit__.getComponentDecoratorPayload(this.componentName);
        this.component = this.renderer.createElement(componentMetadata.selector);
        this.renderer.appendChild(this.elementRef.nativeElement, this.component);
        this.hasView = true;
    }
    removeView() {
        this.renderer.removeChild(this.elementRef.nativeElement, this.component);
        this.hasView = false;
    }
};
__decorate([
    core.Input('seCustomComponentOutlet'),
    __metadata("design:type", String)
], exports.CustomComponentOutletDirective.prototype, "componentName", void 0);
exports.CustomComponentOutletDirective = __decorate([
    core.Directive({
        selector: '[seCustomComponentOutlet]'
    }),
    __metadata("design:paramtypes", [core.ElementRef, core.Renderer2])
], exports.CustomComponentOutletDirective);

/** @ignore */
exports.CustomComponentOutletDirectiveModule = class CustomComponentOutletDirectiveModule {
};
exports.CustomComponentOutletDirectiveModule = __decorate([
    core.NgModule({
        declarations: [exports.CustomComponentOutletDirective],
        exports: [exports.CustomComponentOutletDirective]
    })
], exports.CustomComponentOutletDirectiveModule);

/**
 * Returns an array containing the items from the specified collection in reverse order.
 */
exports.ReversePipe = class ReversePipe {
    transform(value) {
        if (!value) {
            return undefined;
        }
        return value.slice().reverse();
    }
};
exports.ReversePipe = __decorate([
    core.Pipe({ name: 'seReverse' })
], exports.ReversePipe);

exports.ReversePipeModule = class ReversePipeModule {
};
exports.ReversePipeModule = __decorate([
    core.NgModule({
        declarations: [exports.ReversePipe],
        exports: [exports.ReversePipe]
    })
], exports.ReversePipeModule);

/**
 * Pipe used to filter array of objects by object passed as an argument.
 *
 * The pipe will return array of objects that contains the exact keys and values of passed object.
 *
 * ### Example
 *
 *      <div *ngFor='let item of items | seProperty:{ isEnabled: true }'></div>
 */
exports.PropertyPipe = class PropertyPipe {
    transform(array, propObject) {
        if (!array) {
            return undefined;
        }
        if (!propObject) {
            return [...array];
        }
        return array.filter((item) => Object.keys(propObject).every((key) => item[key] === propObject[key]));
    }
};
exports.PropertyPipe = __decorate([
    core.Pipe({ name: 'seProperty' })
], exports.PropertyPipe);

exports.PropertyPipeModule = class PropertyPipeModule {
};
exports.PropertyPipeModule = __decorate([
    core.NgModule({
        declarations: [exports.PropertyPipe],
        exports: [exports.PropertyPipe]
    })
], exports.PropertyPipeModule);

var StartFromPipe_1;
/**
 * Used to slice the array of items starting from index passed as an argument.
 */
exports.StartFromPipe = StartFromPipe_1 = class StartFromPipe {
    static transform(input, start) {
        return input ? input.slice(Number(start)) : [];
    }
    transform(input, start) {
        return StartFromPipe_1.transform(input, start);
    }
};
exports.StartFromPipe = StartFromPipe_1 = __decorate([
    core.Pipe({
        name: 'seStartFrom'
    })
], exports.StartFromPipe);

exports.StartFromPipeModule = class StartFromPipeModule {
};
exports.StartFromPipeModule = __decorate([
    core.NgModule({
        declarations: [exports.StartFromPipe],
        exports: [exports.StartFromPipe]
    })
], exports.StartFromPipeModule);

var FilterByFieldPipe_1;
/**
 * A pipe for an array of objects, that will search all the first level fields of an object,
 * or optionally allows you to specify which fields to include in the search.
 *
 * Only fields that correspond to string
 * values will be considered in the filtering. The filter implements the AND strategy, thus the filter will return search results
 * regardless of the search string order. IE search string "Add Mobile" will return strings such "Mobile Address" and "Address Mobile".
 */
exports.FilterByFieldPipe = FilterByFieldPipe_1 = class FilterByFieldPipe {
    /** @ignore */
    static transform(items, query, keys, callbackFn) {
        const callback = callbackFn || lodash.noop;
        const filterResult = [];
        if (!query) {
            callback(items);
            return items;
        }
        const queryList = query.toLowerCase().split(' ');
        (items || []).forEach((item) => {
            keys = keys || Object.keys(item);
            const terms = keys
                .map((key) => item[key])
                .filter((value) => 
            // eslint-disable-next-line @typescript-eslint/ban-types
            typeof value === 'string' || value instanceof String)
                .map((value) => value.toLowerCase());
            const matchList = queryList
                .map((queryItem) => !!terms.find((term) => term.indexOf(queryItem) >= 0))
                .filter((exists) => !exists);
            if (matchList.length === 0) {
                filterResult.push(item);
            }
        });
        callback(filterResult);
        return filterResult;
    }
    /**
     * @param query The search string in which the values will be filtered by. If no search string is given
     * the original array of objects is be returned.
     * @param keys An array of object fields which determines which key values that the filter will parse through.
     * If no array is specified the filter will check each field value in the array of objects.
     * @param callbackFn A function that will be executed after each iteration of the filter.
     */
    transform(items, query, keys, callbackFn) {
        return FilterByFieldPipe_1.transform(items, query, keys, callbackFn);
    }
};
exports.FilterByFieldPipe = FilterByFieldPipe_1 = __decorate([
    core.Pipe({ name: 'seFilterByField' })
], exports.FilterByFieldPipe);

exports.FilterByFieldPipeModule = class FilterByFieldPipeModule {
};
exports.FilterByFieldPipeModule = __decorate([
    core.NgModule({
        declarations: [exports.FilterByFieldPipe],
        exports: [exports.FilterByFieldPipe]
    })
], exports.FilterByFieldPipeModule);

exports.SharedComponentsModule = class SharedComponentsModule {
};
exports.SharedComponentsModule = __decorate([
    core.NgModule({
        imports: [
            utils.TranslationModule.forChild(),
            exports.FundamentalsModule,
            common.CommonModule,
            forms.FormsModule,
            utils.SelectModule,
            utils.LanguageDropdownModule,
            forms.ReactiveFormsModule,
            exports.CollapsibleContainerModule,
            exports.MessageModule,
            exports.NgTreeModule,
            exports.HasOperationPermissionDirectiveModule,
            exports.DropdownMenuModule,
            exports.ResizeObserverModule,
            exports.InfiniteScrollingModule,
            exports.SpinnerModule,
            exports.PopupOverlayModule,
            exports.StartFromPipeModule,
            exports.PaginationModule,
            exports.TooltipModule,
            exports.ReversePipeModule,
            exports.EditableListModule,
            exports.SliderPanelModule,
            utils.LoginDialogModule,
            utils.FundamentalModalTemplateModule,
            exports.DynamicPagedListModule,
            DataTableModule,
            exports.HelpModule,
            exports.TabsModule,
            exports.MoreTextModule,
            exports.PreventVerticalOverflowModule,
            dialog.DialogModule
        ],
        declarations: [exports.WaitDialogComponent, exports.ShortcutLinkComponent],
        entryComponents: [exports.WaitDialogComponent, exports.ShortcutLinkComponent],
        exports: [
            exports.WaitDialogComponent,
            exports.ShortcutLinkComponent,
            utils.SelectModule,
            utils.LanguageDropdownModule,
            exports.MessageModule,
            exports.NgTreeModule,
            exports.CollapsibleContainerModule,
            exports.HasOperationPermissionDirectiveModule,
            exports.DropdownMenuModule,
            exports.PopupOverlayModule,
            exports.ResizeObserverModule,
            exports.InfiniteScrollingModule,
            exports.StartFromPipeModule,
            exports.PaginationModule,
            exports.TooltipModule,
            exports.ReversePipeModule,
            exports.EditableListModule,
            exports.SpinnerModule,
            exports.SliderPanelModule,
            utils.LoginDialogModule,
            utils.FundamentalModalTemplateModule,
            exports.DynamicPagedListModule,
            DataTableModule,
            exports.HelpModule,
            exports.TabsModule,
            exports.MoreTextModule,
            exports.PreventVerticalOverflowModule
        ]
    })
], exports.SharedComponentsModule);

window.__smartedit__.addDecoratorPayload("Component", "ActionableSearchItemComponent", {
    selector: 'se-actionable-search-item',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-actionable-search-item]': 'true'
    },
    template: `<div *ngIf="search" class="se-actionable-search-item"><div class="se-actionable-search-item__name">{{ search }}</div><button type="button" class="fd-button fd-button--transparent se-actionable-search-item__action-btn" (click)="onButtonClick()">{{ getActionText() | translate }}</button></div>`
});
exports.ActionableSearchItemComponent = class ActionableSearchItemComponent {
    constructor(systemEventService) {
        this.systemEventService = systemEventService;
        this.actionButtonClick = new core.EventEmitter();
        this.defaultEventId = 'yActionableSearchItem_ACTION_CREATE';
        this.defaultActionText = 'se.yationablesearchitem.action.create';
    }
    getActionText() {
        return this.actionText || this.defaultActionText;
    }
    onButtonClick() {
        const evtId = this.eventId || this.defaultEventId;
        this.systemEventService.publishAsync(evtId, this.search || '');
        this.actionButtonClick.emit();
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.ActionableSearchItemComponent.prototype, "search", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.ActionableSearchItemComponent.prototype, "eventId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.ActionableSearchItemComponent.prototype, "actionText", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.ActionableSearchItemComponent.prototype, "actionButtonClick", void 0);
exports.ActionableSearchItemComponent = __decorate([
    core.Component({
        selector: 'se-actionable-search-item',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-actionable-search-item]': 'true'
        },
        template: `<div *ngIf="search" class="se-actionable-search-item"><div class="se-actionable-search-item__name">{{ search }}</div><button type="button" class="fd-button fd-button--transparent se-actionable-search-item__action-btn" (click)="onButtonClick()">{{ getActionText() | translate }}</button></div>`
    }),
    __metadata("design:paramtypes", [exports.SystemEventService])
], exports.ActionableSearchItemComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ITEM_COMPONENT_DATA_TOKEN = new core.InjectionToken('ITEM_COMPONENT_DATA');

window.__smartedit__.addDecoratorPayload("Component", "DefaultItemPrinterComponent", {
    selector: 'se-default-item-printer',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    styles: [`.se-item-printer-text{vertical-align:middle;position:relative;max-width:100%;font-family:"72";color:var(--sapNeutralColor);display:inline-block}`],
    template: `<span fd-menu-title [attr.title]="((data.item.label || data.item.name) | seL10n | async) | translate" class="se-item-printer-text se-nowrap-ellipsis">{{ ((data.item.label || data.item.name) | seL10n | async) | translate }}</span>`
});
let DefaultItemPrinterComponent = class DefaultItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
DefaultItemPrinterComponent = __decorate([
    core.Component({
        selector: 'se-default-item-printer',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        styles: [`.se-item-printer-text{vertical-align:middle;position:relative;max-width:100%;font-family:"72";color:var(--sapNeutralColor);display:inline-block}`],
        template: `<span fd-menu-title [attr.title]="((data.item.label || data.item.name) | seL10n | async) | translate" class="se-item-printer-text se-nowrap-ellipsis">{{ ((data.item.label || data.item.name) | seL10n | async) | translate }}</span>`
    }),
    __param(0, core.Inject(ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], DefaultItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "SearchInputComponent", {
    selector: 'se-select-search-input',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select-search-input]': 'true'
    },
    template: `<div class="search"><input #searchInput type="search" class="search__input fd-input" [disabled]="isDisabled" [readOnly]="isReadOnly" [placeholder]="placeholder" [ngModel]="search" (ngModelChange)="onChange($event)" (keyup)="onKeyup($event)"/></div>`
});
let SearchInputComponent = class SearchInputComponent {
    constructor(elementRef, sharedDataService) {
        this.elementRef = elementRef;
        this.sharedDataService = sharedDataService;
        this.searchKeyup = new core.EventEmitter();
        this.searchChange = new core.EventEmitter();
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.configurations = (yield this.sharedDataService.get('configuration'));
            this.initSearchInputFilter();
        });
    }
    ngOnDestroy() {
        this.searchTermSubscription.unsubscribe();
    }
    get nativeElement() {
        return this.elementRef.nativeElement;
    }
    get inputElement() {
        return this.searchInput.nativeElement;
    }
    focus() {
        this.inputElement.focus();
    }
    onChange(value) {
        this.isTypeAheadEnabled
            ? this.searchTermSubject.next(value)
            : this.searchChange.emit(value);
    }
    onKeyup(event) {
        this.keyUpEvent = event;
        const value = event.target.value;
        this.isTypeAheadEnabled
            ? this.searchTermSubject.next(value)
            : this.searchKeyup.emit({ event: this.keyUpEvent, value });
    }
    initSearchInputFilter() {
        this.searchTermSubject = new rxjs.Subject();
        this.searchTerm$ = this.searchTermSubject.asObservable().pipe(operators.filter((text) => this.configurations && this.configurations.typeAheadMiniSearchTermLength
            ? text.length > 0 &&
                text.length > this.configurations.typeAheadMiniSearchTermLength
            : true), operators.debounceTime((this.configurations && this.configurations.typeAheadDebounce) || 500), operators.distinctUntilChanged());
        this.searchTermSubscription = this.searchTerm$.subscribe((value) => {
            this.searchKeyup.emit({ event: this.keyUpEvent, value });
            this.searchChange.emit(value);
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], SearchInputComponent.prototype, "isDisabled", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], SearchInputComponent.prototype, "isReadOnly", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], SearchInputComponent.prototype, "isTypeAheadEnabled", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SearchInputComponent.prototype, "placeholder", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SearchInputComponent.prototype, "search", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], SearchInputComponent.prototype, "searchKeyup", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], SearchInputComponent.prototype, "searchChange", void 0);
__decorate([
    core.ViewChild('searchInput', { static: false }),
    __metadata("design:type", core.ElementRef)
], SearchInputComponent.prototype, "searchInput", void 0);
SearchInputComponent = __decorate([
    core.Component({
        selector: 'se-select-search-input',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-select-search-input]': 'true'
        },
        template: `<div class="search"><input #searchInput type="search" class="search__input fd-input" [disabled]="isDisabled" [readOnly]="isReadOnly" [placeholder]="placeholder" [ngModel]="search" (ngModelChange)="onChange($event)" (keyup)="onKeyup($event)"/></div>`
    }),
    __metadata("design:paramtypes", [core.ElementRef, utils.ISharedDataService])
], SearchInputComponent);

window.__smartedit__.addDecoratorPayload("Component", "SelectComponent", {
    selector: 'se-select',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select]': 'true'
    },
    template: `<fd-select [style.display]="'none'"></fd-select><div [attr.id]="id + '-selector'" class="select-container" [ngClass]="{
        'has-warning': hasWarning(),
        'has-error': hasError()
    }"><ng-container *ngIf="multiSelect; then multi; else singleSelect"></ng-container><ng-template #singleSelect><fd-popover [triggers]="['click']" [(isOpen)]="isOpen" [placement]="'bottom'" (isOpenChange)="onSingleSelectIsOpenChange($event)" fillControlMode="equal" [focusTrapped]="true" [focusAutoCapture]="true" [additionalBodyClass]="fullAdditionalBodyClass"><fd-popover-control><div class="selected-container fd-select-custom" [attr.has-selected-option]="!!selected || null"><button fd-button class="fd-select-button-custom toggle-button" [disabled]="isReadOnly" [fdMenu]="!isReadOnly"><span *ngIf="!selected; else selectedItem" class="selected-placeholder selected-item">{{ placeholder | translate }}</span><ng-template #selectedItem><div class="selected-item"><span *ngIf="controls" class="sap-icon--search">{{ placeholder | translate }}</span><se-item-printer *ngIf="selected" [attr.id]="id + '-selected'" [item]="selected" [component]="itemComponent" [selectComponentCtx]="this"></se-item-printer><span *ngIf="controls || showRemoveButton" class="sap-icon--sys-cancel selected-item__remove-button" (click)="removeSelectedOption($event, selected)"></span></div></ng-template></button></div></fd-popover-control><fd-popover-body><ul fd-list class="se-select-list-container"><ng-container *ngIf="searchEnabled && isOpen"><ng-container *ngTemplateOutlet="searchInputTemplate"></ng-container></ng-container><ng-container *ngTemplateOutlet="resultsHeader"></ng-container><ng-container *ngIf="isOpen"><ng-container *ngTemplateOutlet="selectListTemplate"></ng-container></ng-container></ul></fd-popover-body></fd-popover></ng-template><ng-template #multi><div [ngClass]="{'se-multi-select': selected && selected.length > 0}"><div class="selected-container se-selected-list"><ng-container *ngIf="selected && selected.length > 0"><div class="selected-list" cdkDropList (cdkDropListDropped)="onDrop($event)"><div *ngFor="let item of selected; trackBy: itemTrackBy" class="selected-list__item selected-item"><div cdkDrag><se-item-printer [item]="item" [component]="itemComponent" [selectComponentCtx]="this"></se-item-printer><div class="selected-list__placeholder" *cdkDragPlaceholder></div></div><span class="sap-icon--sys-cancel selected-item__remove-button selected-item-remove" (click)="removeSelectedOption($event, item)"></span></div></div></ng-container></div></div><fd-popover class="se-multi-select" [triggers]="[]" [closeOnOutsideClick]="false" [isOpen]="isOpen" [placement]="'bottom'" fillControlMode="equal" [focusTrapped]="true" [focusAutoCapture]="true" [additionalBodyClass]="fullAdditionalBodyClass"><fd-popover-control><ng-container *ngTemplateOutlet="searchInputTemplate"></ng-container></fd-popover-control><fd-popover-body><ul fd-list class="se-select-list-container"><ng-container *ngTemplateOutlet="resultsHeader"></ng-container><ng-container *ngIf="isOpen"><ng-container *ngTemplateOutlet="selectListTemplate"></ng-container></ng-container></ul></fd-popover-body></fd-popover></ng-template><ng-template #resultsHeader><se-select-results-header [search]="search" [resultsHeaderComponent]="resultsHeaderComponent" [resultsHeaderLabel]="resultsHeaderLabel" [displayResultsHeaderLabel]="showResultsHeaderLabel()" [actionableSearchItem]="actionableSearchItem" (actionButtonClick)="closeAndReset()"></se-select-results-header></ng-template><ng-template #searchInputTemplate><se-select-search-input [isDisabled]="isReadOnly" [isReadOnly]="multiSelect && !searchEnabled" [isTypeAheadEnabled]="!multiSelect" [placeholder]="showPlaceholder() ? (placeholder | translate) : ''" [search]="search" (searchKeyup)="onSearchInputKeyup($event.event, $event.value)" (searchChange)="onSearchInputChange($event)"></se-select-search-input></ng-template><ng-template #selectListTemplate><se-select-list [id]="id" [isPagedDropdown]="isPagedDropdown()" [fetchPage]="fetchStrategy.fetchPage" [search]="search" [items]="items" [selected]="selected" [excludeSelected]="multiSelect" [disableChoiceFn]="disableChoiceFn" [itemComponent]="itemComponent" [selectComponentCtx]="this" (optionClick)="onOptionClick($event)" (infiniteScrollItemsChange)="onInfiniteScrollItemsChange()"></se-select-list></ng-template></div>`,
    styles: [`.se-select:not(.se-select--paged) .se-select-list-container{overflow-y:auto;max-height:200px;width:auto}.se-select .select-container.has-warning .fd-select-button-custom,.se-select .select-container.has-warning .fd-select-button-custom:hover,.se-select .select-container.has-warning .selected-container{border-color:var(--sapWarningColor)}.se-select .select-container.has-error .fd-select-button-custom,.se-select .select-container.has-error .fd-select-button-custom:hover,.se-select .select-container.has-error .selected-container{border-color:var(--sapErrorColor)}.se-select .se-item-printer{width:100%}.se-select--single .selected-item{display:flex;width:100%;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;z-index:1}.se-select.is-disabled fd-popover,.se-select:disabled fd-popover,.se-select[aria-disabled=true] fd-popover{pointer-events:none;cursor:not-allowed;box-shadow:none;opacity:.65}.se-select.is-disabled .selected-item__remove-button,.se-select:disabled .selected-item__remove-button,.se-select[aria-disabled=true] .selected-item__remove-button{color:var(--sapNeutralColor)}.se-select .fd-popover-custom{display:block;position:relative;width:100%}.se-select .fd-popover-custom .fd-select-button-custom{text-align:unset;width:100%;color:var(--sapUiFieldTextColor)}.se-select .fd-popover-custom .fd-select-button-custom:hover{border-color:inherit}.se-select .fd-popover-custom .fd-select-button-custom:focus{box-shadow:none}.se-select .fd-popover-custom .fd-select-button-custom.toggle-button .fd-button.fd-select__button{position:absolute;right:0;border:none;height:100%;border-top-right-radius:.25rem}.se-select .fd-list{padding:0;max-width:unset}.se-select .sap-icon--search{align-self:center;margin-right:5px}.se-select .sap-icon--sys-cancel{display:flex;justify-content:flex-end;color:var(--sapContent_IconColor);padding-right:30px;line-height:1.875rem;z-index:1000}.se-select .se-multi-select .selected-container{border-radius:.125rem;border:1px solid var(--sapUiFieldBorderColor,#89919a)}.se-select .se-multi-select .selected-container.se-selected-list{border-bottom:none}.se-select .se-multi-select .selected-list{padding:0;margin:0;list-style:none;border-bottom:1px solid var(--sapUiFieldBorderColor,#89919a)}.se-select .se-multi-select .selected-list__item{display:grid;grid-template-columns:16fr 1fr;padding:8px 16px;align-items:center;min-height:36px}.se-select .se-multi-select .selected-list__placeholder{opacity:0}.se-select .se-multi-select .selected-item-remove{cursor:pointer}`]
});
exports.SelectComponent = class SelectComponent {
    constructor(l10nService, logService, cdr) {
        this.l10nService = l10nService;
        this.logService = logService;
        this.cdr = cdr;
        this.modelChange = new core.EventEmitter();
        this.controls = false;
        this.multiSelect = false;
        this.keepModelOnReset = false;
        this.isReadOnly = false;
        this.searchEnabled = true;
        this.resetSearchInput = true;
        this.showRemoveButton = false;
        this.getApi = new core.EventEmitter();
        this.resetChange = new core.EventEmitter();
        this.isOpen = false;
        this.items = [];
        this.search = '';
        this.api = {
            setValidationState: this.setValidationState.bind(this),
            resetValidationState: this.resetValidationState.bind(this)
        };
    }
    get isSingleCss() {
        return !this.multiSelect;
    }
    get isMultiCss() {
        return this.multiSelect;
    }
    get isPagedCss() {
        return this.isPagedDropdown();
    }
    clickHandler(event) {
        if (!this.multiSelect) {
            return;
        }
        if (event.target === this.searchInputCmp.inputElement) {
            event.stopPropagation();
            if (!this.isOpen) {
                this.onMultiSelectIsOpenChange(true);
            }
        }
        else if (this.isOpen) {
            this.onMultiSelectIsOpenChange(false);
        }
    }
    ngOnInit() {
        if (!this.placeholder) {
            this.placeholder = 'se.genericeditor.sedropdown.placeholder';
        }
        if (this.fetchStrategy.fetchAll) {
            this.initSearchInputFilter();
        }
        this.fullAdditionalBodyClass = 'se-select';
        if (this.additionalBodyClass) {
            this.fullAdditionalBodyClass = `${this.fullAdditionalBodyClass} ${this.additionalBodyClass}`;
        }
        this.fetchData();
        setTimeout(() => {
            this.resetChange.emit(this.internalReset.bind(this));
        });
        this.getApi.emit(this.api);
    }
    ngOnChanges(changes) {
        if (!this.itemComponent) {
            this.itemComponent = DefaultItemPrinterComponent;
        }
        this.isValidConfiguration();
        const modelChange = changes.model;
        const didModelChange = modelChange &&
            modelChange.currentValue !== this.modelChangeOld &&
            !modelChange.firstChange;
        if (didModelChange) {
            if (this.multiSelect && this.isModelEmpty()) {
                this.model = [];
            }
            this.fetchData();
        }
    }
    ngOnDestroy() {
        if (this.languageSwitchSubscription) {
            this.languageSwitchSubscription.unsubscribe();
        }
        if (this.searchInputChangeSubject) {
            this.searchInputChangeSubject.unsubscribe();
        }
    }
    onSingleSelectIsOpenChange(isOpen) {
        if (isOpen) {
            setTimeout(() => {
                if (this.searchInputCmp) {
                    this.searchInputCmp.focus();
                }
                if (this.fetchStrategy.fetchAll) {
                    this.refreshOptions(this.search);
                }
            });
        }
        else {
            this.resetOnClose();
        }
    }
    onSearchInputKeyup(event, value) {
        this.internalKeyup(event, value);
    }
    onSearchInputChange(value) {
        this.onSearchChange(value);
        if (this.searchInputChangeSubject) {
            this.searchInputChangeSubject.next(value);
        }
    }
    onOptionClick(item) {
        let selectedHasChanged = false;
        if (this.multiSelect) {
            const selected = [...(this.selected || []), item];
            this.setSelected(selected);
            selectedHasChanged = true;
        }
        else if (!this.isItemSelected(item)) {
            this.setSelected(item);
            selectedHasChanged = true;
        }
        this.internalOnSelect(item, item.id);
        if (selectedHasChanged) {
            this.internalOnChange();
        }
        this.closeAndReset();
    }
    onSearchChange(value) {
        this.search = value;
    }
    onDrop(event) {
        dragDrop.moveItemInArray(this.selected, event.previousIndex, event.currentIndex);
        this.setSelected(this.selected);
        const orderHasChanged = event.previousIndex !== event.currentIndex;
        if (orderHasChanged) {
            this.internalOnChange();
        }
    }
    removeSelectedOption(_, item) {
        _.stopPropagation();
        const selectedNew = this.multiSelect
            ? this.selected.filter((selectedItem) => selectedItem !== item)
            : undefined;
        if (this.multiSelect) {
            if (!this.searchInputCmp.isDisabled) {
                this.setSelected(selectedNew);
                this.internalOnRemove(item, item.id);
            }
        }
        else {
            this.setSelected(selectedNew);
            this.internalOnRemove(item, item.id);
        }
        if (this.isOpen) {
            this.closeAndReset();
        }
    }
    closeAndReset() {
        this.close();
        this.resetOnClose();
    }
    showResultsHeaderLabel() {
        return this.items.length > 0 && !!this.resultsHeaderLabel;
    }
    onInfiniteScrollItemsChange() {
    }
    showPlaceholder() {
        return this.multiSelect || (!this.multiSelect && !!this.selected);
    }
    itemTrackBy(_, item) {
        return item.id;
    }
    setValidationState(validationState) {
        this.validationState = validationState;
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    resetValidationState() {
        this.validationState = undefined;
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    hasError() {
        return VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR === this.validationState;
    }
    hasWarning() {
        return VALIDATION_MESSAGE_TYPES.WARNING === this.validationState;
    }
    fetchEntity(modelId) {
        return this.fetchStrategy.fetchEntity(modelId).then((item) => {
            if (!item) {
                this.logService.debug('fetchEntity was used to fetch the option identified by ' +
                    item +
                    ' but failed to find a match');
            }
            return item;
        });
    }
    updateModelIfNotFoundInItems(items) {
        if (!this.keepModelOnReset) {
            if (this.multiSelect) {
                if (this.isModelEmpty()) {
                    this.setSelected([], false);
                    return;
                }
                const modelFromNewItems = this.model.filter((id) => this.getItemByModel(items, id));
                const multiSelectMatch = lodash.isEqual(this.model, modelFromNewItems);
                if (!multiSelectMatch) {
                    this.updateModel(modelFromNewItems);
                    this.resolveAndSetSelected(items);
                }
                else {
                    this.resolveAndSetSelected(items);
                }
                return;
            }
            if (this.isModelEmpty()) {
                this.setSelected(undefined, false);
                return;
            }
            const singleSelectMatch = this.getItemByModel(items);
            if (!singleSelectMatch) {
                this.setSelected(undefined);
            }
            else {
                this.resolveAndSetSelected(items);
            }
        }
    }
    refreshOptions(mask) {
        this.internalFetchAll(mask).then((items) => {
            this.items = items;
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    isValidConfiguration() {
        if (this.resultsHeaderComponent && this.resultsHeaderLabel) {
            throw new Error(`Only one of "resultsHeaderComponent" or "resultsHeaderLabel" must be specified`);
        }
        if (!this.fetchStrategy.fetchAll && !this.fetchStrategy.fetchPage) {
            throw new Error('Neither fetchAll nor fetchPage have been specified in fetchStrategy');
        }
        if (this.fetchStrategy.fetchAll && this.fetchStrategy.fetchPage) {
            throw new Error('Only one of either fetchAll or fetchPage must be specified in fetchStrategy');
        }
        if (this.fetchStrategy.fetchPage &&
            this.model &&
            !this.fetchStrategy.fetchEntity &&
            !this.fetchStrategy.fetchEntities) {
            throw new Error(`fetchPage has been specified in fetchStrategy but neither fetchEntity nor fetchEntities are available to load item identified by ${this.model}`);
        }
        if (this.isPagedDropdown() && !this.keepModelOnReset) {
            this.logService.debug('current Select is paged, so keepModelOnReset flag is ignored (it will always keep the model on reset).');
        }
    }
    internalKeyup(event, search) {
        if (this.keyup) {
            this.keyup(event, search);
        }
    }
    internalOnRemove(item, model) {
        if (this.onRemove) {
            this.onRemove(item, model);
        }
        this.internalOnChange();
    }
    internalOnChange() {
        if (this.onChange) {
            this.onChange();
        }
    }
    internalOnSelect(item, model) {
        if (this.onSelect) {
            this.onSelect(item, model);
        }
    }
    internalFetchAll(mask = '') {
        return this.fetchStrategy.fetchAll(mask).then((items) => {
            this.fetchAllItems = items;
            return [...items];
        });
    }
    internalFetchEntities() {
        let promise;
        if (!this.multiSelect) {
            promise = this.fetchEntity(this.model).then((item) => [item]);
        }
        else {
            if (this.fetchStrategy.fetchEntities) {
                promise = this.fetchStrategy
                    .fetchEntities(this.model)
                    .then((items) => {
                    if (items.length !== this.model.length) {
                        this.logService.debug(`!fetchEntities was used to fetch the options identified by ${JSON.stringify(this.model)} but failed to find all matches`);
                    }
                    return items;
                });
            }
            else {
                const promiseArray = this.model.map((id) => this.fetchEntity(id));
                promise = Promise.all(promiseArray);
            }
        }
        return promise.then((result) => {
            const items = result
                .filter((item) => item !== null)
                .map((item) => {
                delete item.$promise;
                delete item.$resolved;
                item.technicalUniqueId = stringUtils.encode(item);
                return item;
            });
            this.updateModelIfNotFoundInItems(items);
            this.internalOnChange();
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    onMultiSelectIsOpenChange(isOpen) {
        this.isOpen = isOpen;
        if (isOpen) {
            if (this.isPagedDropdown()) {
                if (!this.cdr.destroyed) {
                    this.cdr.detectChanges();
                }
                return;
            }
            this.refreshOptions(this.search);
        }
        else {
            this.resetOnClose();
        }
    }
    initSearchInputFilter() {
        this.searchInputChangeSubject = new rxjs.Subject();
        this.searchInputChange$ = this.searchInputChangeSubject.asObservable();
        this.languageSwitchSubscription = this.l10nService.languageSwitch$.subscribe((lang) => {
            this.filterFn = getLocalizedFilterFn(lang);
        });
        this.searchInputChange$
            .pipe(operators.filter(() => typeof this.fetchStrategy.fetchAll !== 'undefined'), operators.tap((value) => {
            this.items = this.filterItemsBySearch(this.fetchAllItems, value);
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        }), operators.debounceTime(500), operators.switchMap((value) => this.internalFetchAll(value)))
            .subscribe((items) => {
            this.items = this.filterItemsBySearch(items, this.search);
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    filterItemsBySearch(fetchedItems, search) {
        return fetchedItems.filter((item) => this.filterFn(item.label || item.name)
            .trim()
            .toUpperCase()
            .includes(search.trim().toUpperCase()));
    }
    close() {
        this.isOpen = false;
    }
    resetOnClose() {
        if (this.search && this.resetSearchInput) {
            this.search = '';
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
            if (this.fetchStrategy.fetchAll) {
                this.refreshOptions(this.search);
            }
        }
    }
    fetchData() {
        if (!this.isPagedDropdown()) {
            this.internalFetchAllAndCheckModel();
        }
        else if (!this.isModelEmpty()) {
            this.internalFetchEntities();
        }
    }
    internalFetchAllAndCheckModel() {
        return this.internalFetchAll().then((items) => {
            this.updateModelIfNotFoundInItems(items);
            this.internalOnChange();
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    getItemByModel(items, model = this.model) {
        return items.find((item) => item.id === model);
    }
    getItemsByModel(items, model = this.model) {
        return model.map((id) => this.getItemByModel(items, id)).filter((item) => !!item);
    }
    mapSelectedToModel() {
        return this.multiSelect
            ? this.selected.map((item) => item.id)
            : this.selected.id;
    }
    setSelected(items, updateModel = true) {
        this.selected = items;
        if (updateModel) {
            let model;
            if (this.selected) {
                model = this.mapSelectedToModel();
            }
            this.updateModel(model);
        }
    }
    isItemSelected(item) {
        if (!this.selected) {
            return false;
        }
        return item.id === this.selected.id;
    }
    internalReset(forceReset = false) {
        this.items.length = 0;
        if (forceReset) {
            this.selected = undefined;
            this.resetModel();
            return;
        }
        if (!this.keepModelOnReset) {
            this.fetchData();
        }
    }
    resetModel() {
        const model = this.multiSelect ? [] : undefined;
        this.updateModel(model);
    }
    updateModel(model) {
        this.model = model;
        this.modelChangeOld = this.model;
        this.modelChange.emit(model);
    }
    isPagedDropdown() {
        return !!this.fetchStrategy.fetchPage;
    }
    isModelEmpty() {
        if (this.multiSelect) {
            return !this.model || (this.model && this.model.length === 0);
        }
        else {
            return !this.model;
        }
    }
    resolveAndSetSelected(items) {
        const selected = this.multiSelect
            ? this.getItemsByModel(items)
            : this.getItemByModel(items);
        this.setSelected(selected, false);
    }
};
__decorate([
    core.HostBinding('class.se-select--single'),
    __metadata("design:type", Boolean),
    __metadata("design:paramtypes", [])
], exports.SelectComponent.prototype, "isSingleCss", null);
__decorate([
    core.HostBinding('class.se-select--multi'),
    __metadata("design:type", Boolean),
    __metadata("design:paramtypes", [])
], exports.SelectComponent.prototype, "isMultiCss", null);
__decorate([
    core.HostBinding('class.se-select--paged'),
    __metadata("design:type", Boolean),
    __metadata("design:paramtypes", [])
], exports.SelectComponent.prototype, "isPagedCss", null);
__decorate([
    core.Input(),
    core.HostBinding('attr.id'),
    __metadata("design:type", String)
], exports.SelectComponent.prototype, "id", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "model", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "modelChange", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "fetchStrategy", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "controls", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "multiSelect", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "keepModelOnReset", void 0);
__decorate([
    core.Input(),
    core.HostBinding('class.is-disabled'),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "isReadOnly", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "actionableSearchItem", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.SelectComponent.prototype, "resultsHeaderComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.SelectComponent.prototype, "resultsHeaderLabel", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "disableChoiceFn", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.SelectComponent.prototype, "placeholder", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.SelectComponent.prototype, "additionalBodyClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.SelectComponent.prototype, "itemComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "searchEnabled", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "resetSearchInput", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "onChange", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "onRemove", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "onSelect", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "keyup", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "showRemoveButton", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "getApi", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.SelectComponent.prototype, "reset", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.SelectComponent.prototype, "resetChange", void 0);
__decorate([
    core.ViewChild(SearchInputComponent, { static: false }),
    __metadata("design:type", SearchInputComponent)
], exports.SelectComponent.prototype, "searchInputCmp", void 0);
__decorate([
    core.HostListener('document:click', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [MouseEvent]),
    __metadata("design:returntype", void 0)
], exports.SelectComponent.prototype, "clickHandler", null);
exports.SelectComponent = __decorate([
    core.Component({
        selector: 'se-select',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-select]': 'true'
        },
        template: `<fd-select [style.display]="'none'"></fd-select><div [attr.id]="id + '-selector'" class="select-container" [ngClass]="{
        'has-warning': hasWarning(),
        'has-error': hasError()
    }"><ng-container *ngIf="multiSelect; then multi; else singleSelect"></ng-container><ng-template #singleSelect><fd-popover [triggers]="['click']" [(isOpen)]="isOpen" [placement]="'bottom'" (isOpenChange)="onSingleSelectIsOpenChange($event)" fillControlMode="equal" [focusTrapped]="true" [focusAutoCapture]="true" [additionalBodyClass]="fullAdditionalBodyClass"><fd-popover-control><div class="selected-container fd-select-custom" [attr.has-selected-option]="!!selected || null"><button fd-button class="fd-select-button-custom toggle-button" [disabled]="isReadOnly" [fdMenu]="!isReadOnly"><span *ngIf="!selected; else selectedItem" class="selected-placeholder selected-item">{{ placeholder | translate }}</span><ng-template #selectedItem><div class="selected-item"><span *ngIf="controls" class="sap-icon--search">{{ placeholder | translate }}</span><se-item-printer *ngIf="selected" [attr.id]="id + '-selected'" [item]="selected" [component]="itemComponent" [selectComponentCtx]="this"></se-item-printer><span *ngIf="controls || showRemoveButton" class="sap-icon--sys-cancel selected-item__remove-button" (click)="removeSelectedOption($event, selected)"></span></div></ng-template></button></div></fd-popover-control><fd-popover-body><ul fd-list class="se-select-list-container"><ng-container *ngIf="searchEnabled && isOpen"><ng-container *ngTemplateOutlet="searchInputTemplate"></ng-container></ng-container><ng-container *ngTemplateOutlet="resultsHeader"></ng-container><ng-container *ngIf="isOpen"><ng-container *ngTemplateOutlet="selectListTemplate"></ng-container></ng-container></ul></fd-popover-body></fd-popover></ng-template><ng-template #multi><div [ngClass]="{'se-multi-select': selected && selected.length > 0}"><div class="selected-container se-selected-list"><ng-container *ngIf="selected && selected.length > 0"><div class="selected-list" cdkDropList (cdkDropListDropped)="onDrop($event)"><div *ngFor="let item of selected; trackBy: itemTrackBy" class="selected-list__item selected-item"><div cdkDrag><se-item-printer [item]="item" [component]="itemComponent" [selectComponentCtx]="this"></se-item-printer><div class="selected-list__placeholder" *cdkDragPlaceholder></div></div><span class="sap-icon--sys-cancel selected-item__remove-button selected-item-remove" (click)="removeSelectedOption($event, item)"></span></div></div></ng-container></div></div><fd-popover class="se-multi-select" [triggers]="[]" [closeOnOutsideClick]="false" [isOpen]="isOpen" [placement]="'bottom'" fillControlMode="equal" [focusTrapped]="true" [focusAutoCapture]="true" [additionalBodyClass]="fullAdditionalBodyClass"><fd-popover-control><ng-container *ngTemplateOutlet="searchInputTemplate"></ng-container></fd-popover-control><fd-popover-body><ul fd-list class="se-select-list-container"><ng-container *ngTemplateOutlet="resultsHeader"></ng-container><ng-container *ngIf="isOpen"><ng-container *ngTemplateOutlet="selectListTemplate"></ng-container></ng-container></ul></fd-popover-body></fd-popover></ng-template><ng-template #resultsHeader><se-select-results-header [search]="search" [resultsHeaderComponent]="resultsHeaderComponent" [resultsHeaderLabel]="resultsHeaderLabel" [displayResultsHeaderLabel]="showResultsHeaderLabel()" [actionableSearchItem]="actionableSearchItem" (actionButtonClick)="closeAndReset()"></se-select-results-header></ng-template><ng-template #searchInputTemplate><se-select-search-input [isDisabled]="isReadOnly" [isReadOnly]="multiSelect && !searchEnabled" [isTypeAheadEnabled]="!multiSelect" [placeholder]="showPlaceholder() ? (placeholder | translate) : ''" [search]="search" (searchKeyup)="onSearchInputKeyup($event.event, $event.value)" (searchChange)="onSearchInputChange($event)"></se-select-search-input></ng-template><ng-template #selectListTemplate><se-select-list [id]="id" [isPagedDropdown]="isPagedDropdown()" [fetchPage]="fetchStrategy.fetchPage" [search]="search" [items]="items" [selected]="selected" [excludeSelected]="multiSelect" [disableChoiceFn]="disableChoiceFn" [itemComponent]="itemComponent" [selectComponentCtx]="this" (optionClick)="onOptionClick($event)" (infiniteScrollItemsChange)="onInfiniteScrollItemsChange()"></se-select-list></ng-template></div>`,
        styles: [`.se-select:not(.se-select--paged) .se-select-list-container{overflow-y:auto;max-height:200px;width:auto}.se-select .select-container.has-warning .fd-select-button-custom,.se-select .select-container.has-warning .fd-select-button-custom:hover,.se-select .select-container.has-warning .selected-container{border-color:var(--sapWarningColor)}.se-select .select-container.has-error .fd-select-button-custom,.se-select .select-container.has-error .fd-select-button-custom:hover,.se-select .select-container.has-error .selected-container{border-color:var(--sapErrorColor)}.se-select .se-item-printer{width:100%}.se-select--single .selected-item{display:flex;width:100%;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;z-index:1}.se-select.is-disabled fd-popover,.se-select:disabled fd-popover,.se-select[aria-disabled=true] fd-popover{pointer-events:none;cursor:not-allowed;box-shadow:none;opacity:.65}.se-select.is-disabled .selected-item__remove-button,.se-select:disabled .selected-item__remove-button,.se-select[aria-disabled=true] .selected-item__remove-button{color:var(--sapNeutralColor)}.se-select .fd-popover-custom{display:block;position:relative;width:100%}.se-select .fd-popover-custom .fd-select-button-custom{text-align:unset;width:100%;color:var(--sapUiFieldTextColor)}.se-select .fd-popover-custom .fd-select-button-custom:hover{border-color:inherit}.se-select .fd-popover-custom .fd-select-button-custom:focus{box-shadow:none}.se-select .fd-popover-custom .fd-select-button-custom.toggle-button .fd-button.fd-select__button{position:absolute;right:0;border:none;height:100%;border-top-right-radius:.25rem}.se-select .fd-list{padding:0;max-width:unset}.se-select .sap-icon--search{align-self:center;margin-right:5px}.se-select .sap-icon--sys-cancel{display:flex;justify-content:flex-end;color:var(--sapContent_IconColor);padding-right:30px;line-height:1.875rem;z-index:1000}.se-select .se-multi-select .selected-container{border-radius:.125rem;border:1px solid var(--sapUiFieldBorderColor,#89919a)}.se-select .se-multi-select .selected-container.se-selected-list{border-bottom:none}.se-select .se-multi-select .selected-list{padding:0;margin:0;list-style:none;border-bottom:1px solid var(--sapUiFieldBorderColor,#89919a)}.se-select .se-multi-select .selected-list__item{display:grid;grid-template-columns:16fr 1fr;padding:8px 16px;align-items:center;min-height:36px}.se-select .se-multi-select .selected-list__placeholder{opacity:0}.se-select .se-multi-select .selected-item-remove{cursor:pointer}`]
    }),
    __metadata("design:paramtypes", [exports.L10nService,
        utils.LogService,
        core.ChangeDetectorRef])
], exports.SelectComponent);

window.__smartedit__.addDecoratorPayload("Component", "ItemPrinterComponent", {
    selector: 'se-item-printer',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-item-printer]': 'true'
    },
    template: `<ng-container *ngComponentOutlet="component; injector: componentInjector"></ng-container>`
});
let ItemPrinterComponent = class ItemPrinterComponent {
    constructor(injector) {
        this.injector = injector;
        this.isSelected = true;
    }
    ngOnChanges(changes) {
        if (changes.item) {
            this.itemComponentData = this.createItemComponentData();
            if (this.component) {
                this.componentInjector = this.createItemComponentInjector();
            }
        }
    }
    createItemComponentData() {
        return {
            item: this.item,
            selected: this.isSelected,
            select: this.selectComponentCtx
        };
    }
    createItemComponentInjector() {
        return core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: ITEM_COMPONENT_DATA_TOKEN,
                    useValue: this.itemComponentData
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ItemPrinterComponent.prototype, "item", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], ItemPrinterComponent.prototype, "component", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", exports.SelectComponent)
], ItemPrinterComponent.prototype, "selectComponentCtx", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ItemPrinterComponent.prototype, "isSelected", void 0);
ItemPrinterComponent = __decorate([
    core.Component({
        selector: 'se-item-printer',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-item-printer]': 'true'
        },
        template: `<ng-container *ngComponentOutlet="component; injector: componentInjector"></ng-container>`
    }),
    __metadata("design:paramtypes", [core.Injector])
], ItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "ResultsHeaderComponent", {
    selector: 'se-select-results-header',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select-results-header]': 'true'
    },
    template: `<div *ngIf="showResultsHeaderItem() || resultsHeaderLabel || actionableSearchItem" class="results-header"><div *ngIf="showResultsHeaderItem()"><ng-container *ngComponentOutlet="resultsHeaderComponent"></ng-container></div><se-actionable-search-item *ngIf="actionableSearchItem" [search]="search" [eventId]="actionableSearchItem.eventId" [actionText]="actionableSearchItem.actionText" (actionButtonClick)="onActionButtonClick()"></se-actionable-search-item><li class="fd-menu__list-header" *ngIf="displayResultsHeaderLabel">{{ resultsHeaderLabel | translate }}</li></div>`,
    styles: [`.results-header .fd-menu__list-header{font-size:1rem!important;line-height:1.25;font-weight:400;padding:8px 12px;border-bottom:1px solid var(--sapGroup_ContentBorderColor);color:var(--sapNeutralColor);display:block;padding-left:24px}`]
});
let ResultsHeaderComponent = class ResultsHeaderComponent {
    constructor() {
        this.actionButtonClick = new core.EventEmitter();
    }
    onActionButtonClick() {
        this.actionButtonClick.emit();
    }
    showResultsHeaderItem() {
        return typeof this.resultsHeaderComponent !== 'undefined';
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ResultsHeaderComponent.prototype, "search", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], ResultsHeaderComponent.prototype, "resultsHeaderComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ResultsHeaderComponent.prototype, "resultsHeaderLabel", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], ResultsHeaderComponent.prototype, "displayResultsHeaderLabel", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ResultsHeaderComponent.prototype, "actionableSearchItem", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], ResultsHeaderComponent.prototype, "actionButtonClick", void 0);
ResultsHeaderComponent = __decorate([
    core.Component({
        selector: 'se-select-results-header',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-select-results-header]': 'true'
        },
        template: `<div *ngIf="showResultsHeaderItem() || resultsHeaderLabel || actionableSearchItem" class="results-header"><div *ngIf="showResultsHeaderItem()"><ng-container *ngComponentOutlet="resultsHeaderComponent"></ng-container></div><se-actionable-search-item *ngIf="actionableSearchItem" [search]="search" [eventId]="actionableSearchItem.eventId" [actionText]="actionableSearchItem.actionText" (actionButtonClick)="onActionButtonClick()"></se-actionable-search-item><li class="fd-menu__list-header" *ngIf="displayResultsHeaderLabel">{{ resultsHeaderLabel | translate }}</li></div>`,
        styles: [`.results-header .fd-menu__list-header{font-size:1rem!important;line-height:1.25;font-weight:400;padding:8px 12px;border-bottom:1px solid var(--sapGroup_ContentBorderColor);color:var(--sapNeutralColor);display:block;padding-left:24px}`]
    })
], ResultsHeaderComponent);

window.__smartedit__.addDecoratorPayload("Component", "SelectListComponent", {
    selector: 'se-select-list',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select-list]': 'true'
    },
    template: `<se-infinite-scrolling *ngIf="isPagedDropdown; else selectList" [pageSize]="infiniteScrollingPageSize" [mask]="search" [fetchPage]="fetchPage" [context]="selectComponentCtx" (itemsChange)="onInfiniteScrollItemsChange()"><ng-container *ngTemplateOutlet="selectList"></ng-container></se-infinite-scrolling><ng-template #selectList><div [attr.id]="id +'-list'" suListKeyboardControl [suListKeyboardControlDisabledPredicate]="keyboardControlDisabledPredicate" (suListKeyboardControlEnterKeydown)="onEnterKeydown($event)" class="fd-list fd-list--selection" [ngClass]="{ 'fd-menu__list--basic': !isPagedDropdown }"><ng-container *ngFor="let item of items; trackBy: itemTrackBy"><li *ngIf="(excludeSelected && !isItemSelected(item)) || !excludeSelected" suListItemKeyboardControl class="se-select-list__item fd-list__item" [ngClass]="{'is-selected': !excludeSelected && isItemSelected(item), 'is-disabled': isItemDisabled(item)}"><a fd-list-title (click)="onOptionClick($event, item)" class="menu-option"><se-item-printer [item]="item" [isSelected]="false" [component]="itemComponent" [selectComponentCtx]="selectComponentCtx"></se-item-printer></a></li></ng-container></div></ng-template>`,
    styles: [`.se-select-list .fd-menu__list{position:relative;outline:0}.se-select-list .fd-menu__list--basic{max-height:200px;overflow-x:hidden;overflow-y:auto}.se-select-list__item{cursor:pointer}.se-select-list__item.fd-list__item{padding-left:0;padding-right:0}.se-select-list__item.is-selected{color:var(--sapSelectedColor)}.se-select-list__item.is-disabled .menu-option{cursor:not-allowed;opacity:.65}.se-select-list__item.is-disabled .menu-option:hover{background:0 0}.se-select-list__item .menu-option.fd-list__title{color:inherit;background:inherit;padding:.5rem 1.5rem}`]
});
let SelectListComponent = class SelectListComponent {
    constructor() {
        this.isPagedDropdown = false;
        this.excludeSelected = false;
        this.optionClick = new core.EventEmitter();
        this.infiniteScrollItemsChange = new core.EventEmitter();
        this.infiniteScrollingPageSize = 10;
    }
    keyboardControlDisabledPredicate(item) {
        return item.getElement().classList.contains('is-disabled');
    }
    itemTrackBy(_, item) {
        return item.id;
    }
    isItemSelected(item) {
        const isMultiSelect = Array.isArray(this.selected);
        if (!this.selected || (isMultiSelect && this.selected.length === 0)) {
            return false;
        }
        return isMultiSelect
            ? !!this.selected.find((selectedItem) => selectedItem.id === item.id)
            : item.id === this.selected.id;
    }
    onOptionClick(event, item) {
        if (this.isItemDisabled(item)) {
            event.stopPropagation();
            return;
        }
        this.optionClick.emit(item);
    }
    onEnterKeydown(itemIndex) {
        const items = this.getItems();
        const item = items[itemIndex];
        this.optionClick.emit(item);
    }
    isItemDisabled(item) {
        return this.disableChoiceFn ? this.disableChoiceFn(item) : false;
    }
    onInfiniteScrollItemsChange() {
        this.infiniteScrollItemsChange.emit();
    }
    getItems() {
        if (this.excludeSelected) {
            return this.items.filter((item) => !this.isItemSelected(item));
        }
        else {
            return this.items;
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SelectListComponent.prototype, "id", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SelectListComponent.prototype, "isPagedDropdown", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], SelectListComponent.prototype, "fetchPage", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SelectListComponent.prototype, "search", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], SelectListComponent.prototype, "items", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SelectListComponent.prototype, "selected", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SelectListComponent.prototype, "excludeSelected", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], SelectListComponent.prototype, "disableChoiceFn", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], SelectListComponent.prototype, "itemComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", exports.SelectComponent)
], SelectListComponent.prototype, "selectComponentCtx", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], SelectListComponent.prototype, "optionClick", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], SelectListComponent.prototype, "infiniteScrollItemsChange", void 0);
SelectListComponent = __decorate([
    core.Component({
        selector: 'se-select-list',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-select-list]': 'true'
        },
        template: `<se-infinite-scrolling *ngIf="isPagedDropdown; else selectList" [pageSize]="infiniteScrollingPageSize" [mask]="search" [fetchPage]="fetchPage" [context]="selectComponentCtx" (itemsChange)="onInfiniteScrollItemsChange()"><ng-container *ngTemplateOutlet="selectList"></ng-container></se-infinite-scrolling><ng-template #selectList><div [attr.id]="id +'-list'" suListKeyboardControl [suListKeyboardControlDisabledPredicate]="keyboardControlDisabledPredicate" (suListKeyboardControlEnterKeydown)="onEnterKeydown($event)" class="fd-list fd-list--selection" [ngClass]="{ 'fd-menu__list--basic': !isPagedDropdown }"><ng-container *ngFor="let item of items; trackBy: itemTrackBy"><li *ngIf="(excludeSelected && !isItemSelected(item)) || !excludeSelected" suListItemKeyboardControl class="se-select-list__item fd-list__item" [ngClass]="{'is-selected': !excludeSelected && isItemSelected(item), 'is-disabled': isItemDisabled(item)}"><a fd-list-title (click)="onOptionClick($event, item)" class="menu-option"><se-item-printer [item]="item" [isSelected]="false" [component]="itemComponent" [selectComponentCtx]="selectComponentCtx"></se-item-printer></a></li></ng-container></div></ng-template>`,
        styles: [`.se-select-list .fd-menu__list{position:relative;outline:0}.se-select-list .fd-menu__list--basic{max-height:200px;overflow-x:hidden;overflow-y:auto}.se-select-list__item{cursor:pointer}.se-select-list__item.fd-list__item{padding-left:0;padding-right:0}.se-select-list__item.is-selected{color:var(--sapSelectedColor)}.se-select-list__item.is-disabled .menu-option{cursor:not-allowed;opacity:.65}.se-select-list__item.is-disabled .menu-option:hover{background:0 0}.se-select-list__item .menu-option.fd-list__title{color:inherit;background:inherit;padding:.5rem 1.5rem}`]
    })
], SelectListComponent);

exports.SelectModule = class SelectModule {
};
exports.SelectModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            dragDrop.DragDropModule,
            core$2.PopoverModule,
            core$2.ButtonModule,
            core$2.MenuModule,
            core$2.SelectModule,
            core$2.FormModule,
            exports.InfiniteScrollingModule,
            exports.L10nPipeModule,
            utils.ListKeyboardControlModule,
            utils.TranslationModule.forChild(),
            list.ListModule
        ],
        declarations: [
            exports.SelectComponent,
            DefaultItemPrinterComponent,
            ItemPrinterComponent,
            exports.ActionableSearchItemComponent,
            SelectListComponent,
            SearchInputComponent,
            ResultsHeaderComponent
        ],
        entryComponents: [
            exports.SelectComponent,
            DefaultItemPrinterComponent,
            ItemPrinterComponent,
            exports.ActionableSearchItemComponent,
            SelectListComponent,
            SearchInputComponent,
            ResultsHeaderComponent
        ],
        exports: [exports.SelectComponent]
    })
], exports.SelectModule);

/**
 * Directive solely responsible for handling the submitting of its current data state to
 * an onSave input method and notifying of success and failure.
 *
 * ### Example
 *
 *      <form
 *          [contentManager]="{onSave: editor.submit$}"
 *          (onSuccess)="editor.onSuccess($event)"
 *          (onError)="editor.onFailure($event)"
 *      >
 *      </form>
 *
 * @param option object containing the onSave method of type (data: T) => Observable<T>
 * @param onSuccess outputs the successful result of onSave invocation
 * @param onError outputs the failing result of onSave invocation
 */
let ContentManager = class ContentManager {
    constructor() {
        /**
         * Called when a saving is a success.
         */
        this.onSuccess = new core.EventEmitter();
        /**
         * Called when there is an error after saving.
         */
        this.onError = new core.EventEmitter();
        /**
         * Submitting state of the manager.
         *
         * @type {boolean}
         */
        this.submitting = false;
    }
    set option(option) {
        this._onSave = option.onSave;
    }
    save() {
        this.submitting = true;
        return this._onSave().pipe(operators.finalize(() => (this.submitting = false)), operators.tap((content) => {
            this.onSuccess.emit(content);
        }), operators.catchError((err) => {
            this.onError.emit(err);
            return rxjs.throwError(err);
        }));
    }
};
__decorate([
    core.Input('contentManager'),
    __metadata("design:type", Object),
    __metadata("design:paramtypes", [Object])
], ContentManager.prototype, "option", null);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], ContentManager.prototype, "onSuccess", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], ContentManager.prototype, "onError", void 0);
ContentManager = __decorate([
    core.Directive({
        selector: '[contentManager]'
    })
], ContentManager);

const createApi = (editor) => 
/**
 * The generic editor's api object exposing public functionality
 */
({
    /**
     * Overrides the i18n key used bfor the submit button.
     */
    setSubmitButtonText: (_submitButtonText) => {
        editor.submitButtonText = _submitButtonText;
    },
    /**
     * Overrides the i18n key used bfor the submit button.
     */
    setCancelButtonText: (_cancelButtonText) => {
        editor.cancelButtonText = _cancelButtonText;
    },
    /**
     * If set to true, will always show the submit button.
     */
    setAlwaysShowSubmit: (_alwaysShowSubmit) => {
        editor.alwaysShowSubmit = _alwaysShowSubmit;
    },
    /**
     * If set to true, will always show the reset button.
     */
    setAlwaysShowReset: (_alwaysShowReset) => {
        editor.alwaysShowReset = _alwaysShowReset;
    },
    /**
     * To be executed after reset.
     */
    setOnReset: (_onReset) => {
        editor.onReset = _onReset;
    },
    /**
     * Function that passes a preparePayload function to the editor in order to transform the payload prior to submitting (see `GenericEditorFactoryService#preparePayload`)
     * @param preparePayload The function that takes the original payload as argument
     */
    setPreparePayload: (_preparePayload) => {
        editor.preparePayload = _preparePayload;
    },
    /**
     * Function that passes an updateCallback function to the editor in order to perform an action upon successful submit. It is invoked with two arguments: the pristine object and the response from the server.
     * @param updateCallback the callback invoked upon successful submit.
     */
    setUpdateCallback: (_updateCallback) => {
        editor.updateCallback = _updateCallback;
    },
    /**
     * Function that updates the content of the generic editor without having to reinitialize
     *
     * @param component The component to replace the current model for the generic editor
     */
    updateContent: (component) => {
        editor.form && editor.form.patchComponent(component);
    },
    /**
     * Copies of the current model
     * @returns a copy
     */
    getContent: () => editor.form ? objectUtils.copy(editor.form.component) : undefined,
    /**
     * **Deprecated since 1905 - use {@link addContentChangeEvent} instead.**
     *
     * Function triggered everytime the current model changes
     *
     * @deprecated
     */
    onContentChange() {
        return;
    },
    /**
     * Method adds a new function to the list of functions triggered everytime the current model changes
     *
     * @param {Function} The function triggered everytime the current model changes
     *
     * @returns The function to unregister the event;
     */
    addContentChangeEvent: (event) => {
        editor.onChangeEvents.push(event);
        return () => {
            const index = editor.onChangeEvents.findIndex((e) => e === event);
            if (index > -1) {
                editor.onChangeEvents.splice(index, 1);
            }
        };
    },
    /**
     * Triggers all functions that were added with addContentChangeEvent api method. It provides current content as parameter to every function call.
     */
    triggerContentChangeEvents: () => {
        editor.onChangeEvents.forEach((event) => {
            event(objectUtils.copy(editor.form.component));
        });
    },
    /**
     * Function that clears all validation messages in the editor
     */
    clearMessages: () => {
        editor.form.removeValidationMessages();
    },
    /**
     * Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     * @param qualifier the qualifier contained in the tab we want to switch to.
     */
    switchToTabContainingQualifier: (qualifier) => {
        editor.targetedQualifier = qualifier;
    },
    /** Currently used by clone components to open editor in dirty mode. */
    considerFormDirty: () => {
        editor.initialDirty = true;
    },
    setInProgress: (isInProgress) => {
        editor.inProgress = isInProgress;
    },
    /**
     * Returns true to inform that the submit button delegated to the invoker should be disabled.
     * @returns true if submit is disabled
     */
    isSubmitDisabled: () => editor.isSubmitDisabled(),
    /**
     * Function that returns a promise resolving to language descriptors. If defined, will be resolved
     * when the generic editor is initialized to override what languages are used for localized elements
     * within the editor.
     * @returns a promise resolving to language descriptors. Each descriptor provides the following
     * language properties: isocode, nativeName, name, active, and required.
     */
    getLanguages: () => null,
    /**
     * If set to true, will always enable the submit button.
     */
    setAlwaysEnableSubmit: (alwaysEnableSubmit) => {
        editor.alwaysEnableSubmit = alwaysEnableSubmit;
    }
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const GENERIC_EDITOR_WIDGET_DATA = new core.InjectionToken('GENERIC_EDITOR_WIDGET_DATA');

window.__smartedit__.addDecoratorPayload("Component", "BooleanComponent", {
    selector: 'se-boolean',
    template: `<div class="se-boolean fd-form__item fd-form__item--check"><span class="fd-toggle fd-toggle--xs fd-form__control"><label class="fd-form__label fd-switch fd-switch--compact" for="{{qualifierId}}-checkbox"><input type="checkbox" id="{{qualifierId}}-checkbox" class="se-boolean__input fd-switch__input" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.qualifier" [disabled]="!widget.field.editable" [(ngModel)]="widget.model[widget.qualifier]" (click)="checkboxOnClick($event.target)"/><div class="fd-switch__slider"><div class="fd-switch__track"><span class="fd-switch__handle" role="presentation" style="border-style: solid; border-width: 1px"></span></div></div></label></span><p *ngIf="widget.field.labelText && !widget.model[widget.qualifier]" class="se-boolean__text">{{widget.field.labelText| translate}}</p></div>`
});
exports.BooleanComponent = class BooleanComponent {
    constructor(systemEventService, widget) {
        this.systemEventService = systemEventService;
        this.widget = widget;
    }
    ngOnInit() {
        this.eventId = `${this.widget.id}${this.widget.qualifier}${CLICK_DROPDOWN}`;
        this.qualifierId = this.widget.qualifier + stringUtils.generateIdentifier();
        if (this.widget.model[this.widget.qualifier] === undefined) {
            const defaultValue = this.widget.field.defaultValue !== undefined
                ? this.widget.field.defaultValue
                : false;
            this.widget.model[this.widget.qualifier] = defaultValue;
            this.widget.editor.pristine[this.widget.qualifier] = defaultValue;
        }
        this.systemEventService.publishAsync(this.eventId, `${this.widget.model[this.widget.qualifier]}`);
    }
    checkboxOnClick(event) {
        this.systemEventService.publishAsync(this.eventId, `${event.checked}`);
    }
};
exports.BooleanComponent = __decorate([
    core.Component({
        selector: 'se-boolean',
        template: `<div class="se-boolean fd-form__item fd-form__item--check"><span class="fd-toggle fd-toggle--xs fd-form__control"><label class="fd-form__label fd-switch fd-switch--compact" for="{{qualifierId}}-checkbox"><input type="checkbox" id="{{qualifierId}}-checkbox" class="se-boolean__input fd-switch__input" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.qualifier" [disabled]="!widget.field.editable" [(ngModel)]="widget.model[widget.qualifier]" (click)="checkboxOnClick($event.target)"/><div class="fd-switch__slider"><div class="fd-switch__track"><span class="fd-switch__handle" role="presentation" style="border-style: solid; border-width: 1px"></span></div></div></label></span><p *ngIf="widget.field.labelText && !widget.model[widget.qualifier]" class="se-boolean__text">{{widget.field.labelText| translate}}</p></div>`
    }),
    __param(1, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [exports.SystemEventService, Object])
], exports.BooleanComponent);

exports.BooleanModule = class BooleanModule {
};
exports.BooleanModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, forms.FormsModule, core$1.TranslateModule.forChild()],
        declarations: [exports.BooleanComponent],
        entryComponents: [exports.BooleanComponent],
        exports: [exports.BooleanComponent]
    })
], exports.BooleanModule);

/**
 * The DateTimePickerLocalizationService is responsible for both localizing the date time picker as well as the tooltips
 */
let /* @ngInject */ DateTimePickerLocalizationService = class /* @ngInject */ DateTimePickerLocalizationService {
    constructor(languageService) {
        this.languageService = languageService;
    }
    setLocale(datetimeAdapter) {
        this.languageService.getResolveLocale().then((language) => {
            const momentLocale = this.convertToDateTimePickerLocale(language);
            datetimeAdapter.setLocale(momentLocale);
        });
    }
    convertToDateTimePickerLocale(resolvedLocale) {
        // for fundamental datetimepicker, it accept 'en-US' rather than 'en_US', it's different from smartedit.
        return resolvedLocale.replace('_', '-');
    }
};
/* @ngInject */ DateTimePickerLocalizationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.LanguageService])
], /* @ngInject */ DateTimePickerLocalizationService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP_VALUE = {
    in: 'id',
    zh: 'zh-cn'
};

window.__smartedit__.addDecoratorPayload("Component", "DateTimePickerComponent", {
    selector: 'se-date-time-picker',
    template: `<fd-datetime-picker [state]="isInvalid() ? 'error' : 'success'" name="se-datetime-picker" [placeholder]="placeholderText | translate" [disabled]="isDisabled" [(ngModel)]="date" (ngModelChange)="handleDatePickerChange()" [allowNull]="true"></fd-datetime-picker>`,
    providers: [
        {
            provide: datetime.DatetimeAdapter,
            useClass: datetime.FdDatetimeAdapter
        },
        {
            provide: datetime.DATE_TIME_FORMATS,
            useValue: datetime.FD_DATETIME_FORMATS
        }
    ]
});
let DateTimePickerComponent = class DateTimePickerComponent {
    constructor(widget, localizationService, datetimeAdapter) {
        this.widget = widget;
        this.localizationService = localizationService;
        this.datetimeAdapter = datetimeAdapter;
        this.placeholderText = 'se.componentform.select.date';
        this.date = datetime.FdDate.getToday();
        if (!!this.widget.model[this.widget.qualifier]) {
            this.date = datetime.FdDate.getFdDateByDate(new Date(this.widget.model[this.widget.qualifier]));
        }
        else {
            this.handleDatePickerChange();
        }
    }
    ngOnInit() {
        this.localizationService.setLocale(this.datetimeAdapter);
    }
    handleDatePickerChange() {
        this.widget.model[this.widget.qualifier] = this.date
            ? dateUtils.formatDateAsUtc(this.date.toString())
            : undefined;
    }
    get isDisabled() {
        return this.widget.isFieldDisabled();
    }
    isInvalid() {
        return !this.date ? false : !this.date.isDateValid();
    }
};
DateTimePickerComponent = __decorate([
    core.Component({
        selector: 'se-date-time-picker',
        template: `<fd-datetime-picker [state]="isInvalid() ? 'error' : 'success'" name="se-datetime-picker" [placeholder]="placeholderText | translate" [disabled]="isDisabled" [(ngModel)]="date" (ngModelChange)="handleDatePickerChange()" [allowNull]="true"></fd-datetime-picker>`,
        providers: [
            {
                provide: datetime.DatetimeAdapter,
                useClass: datetime.FdDatetimeAdapter
            },
            {
                provide: datetime.DATE_TIME_FORMATS,
                useValue: datetime.FD_DATETIME_FORMATS
            }
        ]
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object, DateTimePickerLocalizationService,
        datetime.DatetimeAdapter])
], DateTimePickerComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Contains a map of all inconsistent locales ISOs between SmartEdit and MomentJS
 */
const RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP = new core.InjectionToken('resolvedLocaleToMomentLocaleMap');
/**
 * Contains a map of all tooltips to be localized in the date time picker
 */
new core.InjectionToken('tooltipsMap');

/**
 * The date time picker service module is a module used for displaying a date time picker
 *
 * Use the se-date-time-picker to open the date time picker.
 *
 * Once the se-date-time-picker is opened, its DateTimePickerLocalizationService is used to localize the tooling.
 */
let DateTimePickerModule = class DateTimePickerModule {
};
DateTimePickerModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            datetimePicker.DatetimePickerModule,
            momentAdapter.MomentDatetimeModule,
            core$1.TranslateModule.forChild()
        ],
        providers: [
            {
                provide: RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP,
                useValue: RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP_VALUE
            },
            {
                provide: datetime.DatetimeAdapter,
                useClass: datetime.FdDatetimeAdapter
            },
            {
                provide: datetime.DATE_TIME_FORMATS,
                useValue: datetime.FD_DATETIME_FORMATS
            },
            exports.LanguageService,
            DateTimePickerLocalizationService
        ],
        declarations: [DateTimePickerComponent],
        entryComponents: [DateTimePickerComponent],
        exports: [DateTimePickerComponent]
    })
], DateTimePickerModule);

window.__smartedit__.addDecoratorPayload("Component", "DropdownItemPrinterComponent", {
    selector: 'se-dropdown-item-printer',
    template: ` <span>{{ data.item.label }}</span> `
});
let DropdownItemPrinterComponent = class DropdownItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
DropdownItemPrinterComponent = __decorate([
    core.Component({
        selector: 'se-dropdown-item-printer',
        template: ` <span>{{ data.item.label }}</span> `
    }),
    __param(0, core.Inject(ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], DropdownItemPrinterComponent);
window.__smartedit__.addDecoratorPayload("Component", "DropdownComponent", {
    selector: 'se-dropdown-wrapper',
    template: `
        <se-select
            [id]="data.field.qualifier"
            class="se-generic-editor-dropdown"
            [(model)]="data.model[data.qualifier]"
            [searchEnabled]="false"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [placeholder]="'se.genericeditor.sedropdown.placeholder'"></se-select>
    `
});
let DropdownComponent = class DropdownComponent {
    constructor(data) {
        this.data = data;
        this.itemComponent = DropdownItemPrinterComponent;
        const { field: { options } } = data;
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(options)
        };
    }
};
DropdownComponent = __decorate([
    core.Component({
        selector: 'se-dropdown-wrapper',
        template: `
        <se-select
            [id]="data.field.qualifier"
            class="se-generic-editor-dropdown"
            [(model)]="data.model[data.qualifier]"
            [searchEnabled]="false"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [placeholder]="'se.genericeditor.sedropdown.placeholder'"></se-select>
    `
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], DropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "EditableDropdownComponent", {
    selector: 'se-editable-dropdown',
    template: `<se-generic-editor-dropdown [id]="data.id" [field]="data.field" [qualifier]="data.qualifier" [model]="data.model"></se-generic-editor-dropdown>`
});
let EditableDropdownComponent = class EditableDropdownComponent {
    constructor(data) {
        this.data = data;
    }
};
EditableDropdownComponent = __decorate([
    core.Component({
        selector: 'se-editable-dropdown',
        template: `<se-generic-editor-dropdown [id]="data.id" [field]="data.field" [qualifier]="data.qualifier" [model]="data.model"></se-generic-editor-dropdown>`
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], EditableDropdownComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const LINKED_DROPDOWN_TOKEN = new core.InjectionToken(LINKED_DROPDOWN);
const DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN = new core.InjectionToken('DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN');
const CLICK_DROPDOWN_TOKEN = new core.InjectionToken(CLICK_DROPDOWN);
/**
 * @internal
 * @ignore
 */
class IGenericEditorDropdownServiceConstructor {
    constructor(conf) {
        //
    }
}

function genericEditorDropdownComponentOnInit() {
    this.field.params = this.field.params || {};
    this.field.params.catalogId = this.field.params.catalogId || CONTEXT_CATALOG;
    this.field.params.catalogVersion = this.field.params.catalogVersion || CONTEXT_CATALOG_VERSION;
    const dropdownServiceConstructor = this.GenericEditorDropdownService || this.SEDropdownService;
    this.dropdown = new dropdownServiceConstructor({
        field: this.field,
        qualifier: this.qualifier,
        model: this.model,
        id: this.id,
        onClickOtherDropdown: this.onClickOtherDropdown.bind(this),
        getApi: this.getApi
    });
    this.dropdown.init();
}
window.__smartedit__.addDecoratorPayload("Component", "GenericEditorDropdownComponent", {
    selector: 'se-generic-editor-dropdown',
    host: {
        '[class.se-generic-editor-dropdown]': 'true'
    },
    template: `<se-select *ngIf="dropdown.initialized" [id]="dropdown.qualifier" (click)="dropdown.onClick()" [placeholder]="field.placeholder" [(model)]="model[qualifier]" [onChange]="dropdown.triggerAction" [fetchStrategy]="dropdown.fetchStrategy" [(reset)]="dropdown.reset" [multiSelect]="dropdown.isMultiDropdown" [controls]="dropdown.isMultiDropdown" [showRemoveButton]="showRemoveButton" [isReadOnly]="!field.editable" [itemComponent]="itemComponent" [resultsHeaderComponent]="resultsHeaderComponent" [actionableSearchItem]="actionableSearchItem" (resetChange)="onResetChange($event)"></se-select>`
});
exports.GenericEditorDropdownComponent = class GenericEditorDropdownComponent {
    constructor(GenericEditorDropdownService) {
        this.GenericEditorDropdownService = GenericEditorDropdownService;
        this.resetChange = new core.EventEmitter();
    }
    ngOnChanges() {
        genericEditorDropdownComponentOnInit.call(this);
    }
    ngOnInit() {
        genericEditorDropdownComponentOnInit.call(this);
    }
    onClickOtherDropdown() {
        this.selectComponent.closeAndReset();
    }
    onResetChange(reset) {
        this.resetChange.emit(reset);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorDropdownComponent.prototype, "id", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorDropdownComponent.prototype, "field", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorDropdownComponent.prototype, "qualifier", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorDropdownComponent.prototype, "model", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.GenericEditorDropdownComponent.prototype, "showRemoveButton", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.GenericEditorDropdownComponent.prototype, "itemComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], exports.GenericEditorDropdownComponent.prototype, "resultsHeaderComponent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorDropdownComponent.prototype, "actionableSearchItem", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorDropdownComponent.prototype, "reset", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.GenericEditorDropdownComponent.prototype, "resetChange", void 0);
__decorate([
    core.ViewChild(exports.SelectComponent, { static: false }),
    __metadata("design:type", exports.SelectComponent)
], exports.GenericEditorDropdownComponent.prototype, "selectComponent", void 0);
exports.GenericEditorDropdownComponent = __decorate([
    core.Component({
        selector: 'se-generic-editor-dropdown',
        host: {
            '[class.se-generic-editor-dropdown]': 'true'
        },
        template: `<se-select *ngIf="dropdown.initialized" [id]="dropdown.qualifier" (click)="dropdown.onClick()" [placeholder]="field.placeholder" [(model)]="model[qualifier]" [onChange]="dropdown.triggerAction" [fetchStrategy]="dropdown.fetchStrategy" [(reset)]="dropdown.reset" [multiSelect]="dropdown.isMultiDropdown" [controls]="dropdown.isMultiDropdown" [showRemoveButton]="showRemoveButton" [isReadOnly]="!field.editable" [itemComponent]="itemComponent" [resultsHeaderComponent]="resultsHeaderComponent" [actionableSearchItem]="actionableSearchItem" (resetChange)="onResetChange($event)"></se-select>`
    }),
    __metadata("design:paramtypes", [IGenericEditorDropdownServiceConstructor])
], exports.GenericEditorDropdownComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * The SEDropdownService handles the initialization and the rendering of the {@link SeDropdownComponent}.
 * - Angular - `CustomDropdownPopulatorsToken` Injection Token
 */
const GenericEditorDropdownServiceFactory = (genericEditorUtil, logService, LINKED_DROPDOWN, CLICK_DROPDOWN, DROPDOWN_IMPLEMENTATION_SUFFIX, systemEventService, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators) => class {
    constructor(conf) {
        this.field = conf.field;
        this.qualifier = conf.qualifier;
        this.model = conf.model;
        this.id = conf.id;
        this.onClickOtherDropdown = conf.onClickOtherDropdown;
        this.items = [];
    }
    /**
     * Initializes the GenericEditorDropdownComponent with a Dropdown Populator instance,
     * based on the "field" attribute given in constructor.
     */
    init() {
        this.populatorName = {
            options: optionsDropdownPopulator.constructor.name,
            uri: uriDropdownPopulator.constructor.name,
            propertyType: this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX,
            cmsStructureType: this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX,
            smarteditComponentType: {
                withQualifier: this.field.smarteditComponentType +
                    this.field.qualifier +
                    DROPDOWN_IMPLEMENTATION_SUFFIX,
                withQualifierForDowngradedService: lodash.camelCase(this.field.smarteditComponentType +
                    this.field.qualifier +
                    DROPDOWN_IMPLEMENTATION_SUFFIX),
                withoutQualifier: this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX
            }
        };
        this.isMultiDropdown = this.field.collection ? this.field.collection : false;
        this.triggerAction = this.triggerAction.bind(this);
        this.eventId = (this.id || '') + LINKED_DROPDOWN;
        this.clickEventKey = (this.id || '') + CLICK_DROPDOWN;
        if (this.field.dependsOn) {
            systemEventService.subscribe(this.eventId, this._respondToChange.bind(this));
        }
        systemEventService.subscribe(this.clickEventKey, this._respondToOtherClicks.bind(this));
        const populator = genericEditorUtil.resolvePopulator(this.field, this.populatorName, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
        if (!populator) {
            logService.error('No dropdown populator found');
        }
        ({ instance: this.populator, isPaged: this.isPaged } = populator);
        this.fetchStrategy = {
            fetchEntity: this.fetchEntity.bind(this)
        };
        if (this.isPaged) {
            this.fetchStrategy.fetchPage = this.fetchPage.bind(this);
        }
        else {
            this.fetchStrategy.fetchAll = this.fetchAll.bind(this);
        }
        this.initialized = true;
    }
    /**
     * Publishes an asynchronous event for the currently selected option.
     */
    triggerAction() {
        const selectedObj = this.items.filter((option) => option.id === this.model[this.qualifier])[0];
        const handle = {
            qualifier: this.qualifier,
            optionObject: selectedObj
        };
        systemEventService.publishAsync(this.eventId, handle);
    }
    onClick() {
        systemEventService.publishAsync(this.clickEventKey, this.field.qualifier);
    }
    /**
     * Uses the configured implementation of {@link DropdownPopulatorInterface}
     * to populate the GenericEditorDropdownComponent items using [fetchAll]{@link DropdownPopulatorInterface#fetchAll}
     *
     * @returns A promise that resolves to a list of options to be populated.
     */
    fetchAll(search) {
        return this.populator
            .fetchAll({
            field: this.field,
            model: this.model,
            selection: this.selection,
            search
        })
            .then((options) => {
            this.items = options;
            return this.items;
        });
    }
    /**
     * Uses the configured implementation of {@link DropdownPopulatorInterface}
     * to populate a single item [getItem]{@link DropdownPopulatorInterface#getItem}
     *
     * @returns A promise that resolves to the option that was fetched
     */
    fetchEntity(id) {
        return this.populator.getItem({
            field: this.field,
            id,
            model: this.model
        });
    }
    /**
     * @param search The search to filter options by
     * @param pageSize The number of items to be returned
     * @param currentPage The page to be returned
     *
     * Uses the configured implementation of {@link DropdownPopulatorInterface}
     * to populate the seDropdown items using [fetchPage]{@link DropdownPopulatorInterface#fetchPage}
     *
     * @returns A promise that resolves to an object containing the array of items and paging information
     */
    fetchPage(search, pageSize, currentPage, selectedItems) {
        return this.populator
            .fetchPage({
            field: this.field,
            model: this.model,
            selection: this.selection,
            search,
            pageSize,
            currentPage
        })
            .then((page) => {
            const holderProperty = apiUtils.getKeyHoldingDataFromResponse(page);
            page.results = page[holderProperty];
            delete page[holderProperty];
            this.items = [...this.items, ...page.results];
            if (this.isMultiDropdown && selectedItems) {
                return this.limitToNonSelectedItems(page, selectedItems);
            }
            else {
                return page;
            }
        })
            .catch((error) => {
            logService.error(`Failed to fetch items and paging information. ${error}`);
        });
    }
    // /**
    //  * CEXC-2588: remove the items already selected to trigger the nextPage method of InifiteScrollingComponent
    //  */
    isInclude(element, arr) {
        let result = false;
        arr.forEach((item) => {
            if (item.uid === element.uid) {
                result = true;
            }
        });
        return result;
    }
    limitToNonSelectedItems(page, selectedItems) {
        const itemIndex = page.results.length;
        for (let i = itemIndex - 1; i >= 0; i--) {
            const item = page.results[i];
            if (this.isInclude(item, selectedItems)) {
                page.results.splice(i, 1);
                page.pagination.count--;
            }
        }
        return page;
    }
    /**
     * CEXC-2588 end
     */
    _respondToChange(_key, handle) {
        if (this.field.dependsOn &&
            this.field.dependsOn.split(',').indexOf(handle.qualifier) > -1) {
            this.selection = handle.optionObject;
            if (this.reset) {
                this.reset();
            }
        }
    }
    /** Responds to other dropdowns clicks */
    _respondToOtherClicks(key, qualifier) {
        if (this.field.qualifier !== qualifier &&
            typeof this.onClickOtherDropdown === 'function') {
            this.onClickOtherDropdown(key, qualifier);
        }
    }
};

// This util class is derived from GenericEditorDropdownServiceFactory.ts to shorten file size
class GenericEditorUtil {
    isFieldPaged(paged = undefined) {
        return paged ? paged : false;
    }
    /**
     * Lookup for Populator with given name and returns its instance.
     *
     * it will look for Angular service in `customDropdownPopulators`.
     */
    resolvePopulatorByName(name, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators) {
        if (name === optionsDropdownPopulator.constructor.name) {
            return optionsDropdownPopulator;
        }
        if (name === uriDropdownPopulator.constructor.name) {
            return uriDropdownPopulator;
        }
        if (customDropdownPopulators && customDropdownPopulators.length > 0) {
            return customDropdownPopulators.find((populator) => populator.constructor.name.toUpperCase() === name.toUpperCase());
        }
        return undefined;
    }
    isPopulatorPaged(populator) {
        return populator.isPaged && populator.isPaged();
    }
    resolvePopulator(field, populatorName, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators) {
        if (field.options && field.uri) {
            throw new Error('se.dropdown.contains.both.uri.and.options');
        }
        // OptionsDropdownPopulator e.g. EditableDropdown
        if (field.options) {
            return {
                instance: this.resolvePopulatorByName(populatorName.options, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators),
                isPaged: false
            };
        }
        // UriDropdownPopulator
        if (field.uri) {
            return {
                instance: this.resolvePopulatorByName(populatorName.uri, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators),
                isPaged: this.isFieldPaged(field.paged)
            };
        }
        // e.g. productDropdownPopulator, categoryDropdownPopulator
        if (field.propertyType) {
            const populator = this.resolvePopulatorByName(populatorName.propertyType, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
            return {
                instance: populator,
                isPaged: this.isPopulatorPaged(populator)
            };
        }
        // e.g. CMSItemDropdownDropdownPopulator
        const cmsStructureTypePopulator = this.resolvePopulatorByName(populatorName.cmsStructureType, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
        if (cmsStructureTypePopulator) {
            return {
                instance: cmsStructureTypePopulator,
                isPaged: this.isFieldPaged(field.paged)
            };
        }
        // For downstream teams
        // e.g. SmarteditComponentType + qualifier + DropdownPopulator
        const smarteditComponentTypeWithQualifierPopulator = this.resolvePopulatorByName(populatorName.smarteditComponentType.withQualifier, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
        if (smarteditComponentTypeWithQualifierPopulator) {
            return {
                instance: smarteditComponentTypeWithQualifierPopulator,
                isPaged: this.isPopulatorPaged(smarteditComponentTypeWithQualifierPopulator)
            };
        }
        // For downstream teams
        // TODO: PreviewDatapreviewCatalogDropdownPopulator provide with the token
        // e.g. smarteditComponentType + qualifier + DropdownPopulator
        const smarteditComponentTypeWithQualifierForDowngradedServicePopulator = this.resolvePopulatorByName(populatorName.smarteditComponentType.withQualifierForDowngradedService, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
        if (smarteditComponentTypeWithQualifierForDowngradedServicePopulator) {
            return {
                instance: smarteditComponentTypeWithQualifierForDowngradedServicePopulator,
                isPaged: this.isPopulatorPaged(smarteditComponentTypeWithQualifierForDowngradedServicePopulator)
            };
        }
        // For downstream teams
        // e.g. SmarteditComponentType + DropdownPopulator
        const smarteditComponentTypeWithoutQualifierPopulator = this.resolvePopulatorByName(populatorName.smarteditComponentType.withoutQualifier, optionsDropdownPopulator, uriDropdownPopulator, customDropdownPopulators);
        if (smarteditComponentTypeWithoutQualifierPopulator) {
            return {
                instance: smarteditComponentTypeWithoutQualifierPopulator,
                isPaged: this.isPopulatorPaged(smarteditComponentTypeWithoutQualifierPopulator)
            };
        }
        return undefined;
    }
}

/**
 * Interface describing the contract of a DropdownPopulator resolved by
 * {@link GenericEditorFactoryService} to populate the dropdowns of {@link GenericEditorDropdownComponent}.
 */
class DropdownPopulatorInterface {
    constructor(lodash, languageService, translateService) {
        this.lodash = lodash;
        this.languageService = languageService;
        this.translateService = translateService;
    }
    getItem(payload) {
        return null;
    }
    /**
     * Returns a promise resolving to a list of items.
     * The items must all contain a property `id`.
     */
    fetchAll(payload) {
        'proxyFunction';
        return null;
    }
    /**
     * Returns a promise resolving to a page of items.
     * The items must all contain a property `id`.
     */
    fetchPage(payload) {
        'proxyFunction';
        return null;
    }
    /**
     * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false.
     */
    isPaged() {
        return false;
    }
    /**
     * Populates the id and label property for each item in the list. If the label property is not already set,
     * then we use an ordered list of attributes to use when determining the label for each item.
     * @param items The array of items to set the id and label attributes on
     * @param idAttribute The name of the id attribute
     * @param orderedLabelAttributes The ordered list of label attributes
     * @returns The modified list of items
     */
    populateAttributes(items, idAttribute, orderedLabelAttributes) {
        return this.lodash.map(items, (item) => {
            if (idAttribute && this.lodash.isEmpty(item.id)) {
                item.id = item[idAttribute];
            }
            if (orderedLabelAttributes && this.lodash.isEmpty(item.label)) {
                // Find the first attribute that the item object contains
                const labelAttribute = this.lodash.find(orderedLabelAttributes, (attr) => !this.lodash.isEmpty(item[attr]));
                // If we found an attribute, set the label
                if (labelAttribute) {
                    item.label = item[labelAttribute];
                }
            }
            return item;
        });
    }
    /**
     * Searches a list and returns a promise resolving to only items with a label attribute that matches the search term.
     * @param items The list of items to search
     * @param searchTerm The search term to filter items by
     * @returns The filtered list of items
     */
    search(items, searchTerm) {
        return this.languageService.getResolveLocale().then((isocode) => this.lodash.filter(items, (item) => {
            let labelValue;
            if (this.lodash.isObject(item.label)) {
                isocode = item.label[isocode] ? isocode : Object.keys(item.label)[0];
                labelValue = item.label[isocode];
            }
            else {
                labelValue = this.translateService
                    ? this.translateService.instant(item.label)
                    : item.label;
            }
            return (labelValue && labelValue.toUpperCase().indexOf(searchTerm.toUpperCase()) > -1);
        }));
    }
}

/**
 * Implementation of {@link DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
 * containing options attribute.
 */
/* @ngInject */ exports.OptionsDropdownPopulator = class /* @ngInject */ OptionsDropdownPopulator extends DropdownPopulatorInterface {
    constructor(languageService, translateService) {
        super(lodash__namespace, languageService, translateService);
        this.languageService = languageService;
        this.translateService = translateService;
    }
    /**
     * Implementation of the [fetchAll]{@link DropdownPopulatorInterface#fetchAll} method.
     */
    fetchAll(payload) {
        const options = this.populateAttributes(payload.field.options, payload.field.idAttribute, payload.field.labelAttributes);
        if (payload.search) {
            return this.search(options, payload.search);
        }
        return Promise.resolve(options);
    }
};
/* @ngInject */ exports.OptionsDropdownPopulator = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.LanguageService,
        core$1.TranslateService])
], /* @ngInject */ exports.OptionsDropdownPopulator);

/**
 * Implementation of {@link DropdownPopulatorInterface} for "EditableDropdown" `cmsStructureType` containing `uri` attribute.
 */
/* @ngInject */ exports.UriDropdownPopulator = class /* @ngInject */ UriDropdownPopulator extends DropdownPopulatorInterface {
    constructor(restServiceFactory, languageService, translateService) {
        super(lodash__namespace, languageService, translateService);
        this.restServiceFactory = restServiceFactory;
        this.languageService = languageService;
        this.translateService = translateService;
    }
    /**
     * Implementation of the [fetchAll]{@link DropdownPopulatorInterface#fetchAll} method.
     */
    fetchAll(payload) {
        let params;
        if (payload.field.dependsOn) {
            params = this._buildQueryParams(payload.field.dependsOn, payload.model);
        }
        return this.restServiceFactory
            .get(payload.field.uri)
            .get(params)
            .then((response) => {
            const dataFromResponse = apiUtils.getDataFromResponse(response);
            const options = this.populateAttributes(dataFromResponse, payload.field.idAttribute, payload.field.labelAttributes);
            if (payload.search) {
                return this.search(options, payload.search);
            }
            return Promise.resolve(options);
        });
    }
    /**
     * Implementation of the [fetchPage]{@link DropdownPopulatorInterface#fetchPage} method.
     */
    fetchPage(payload) {
        let params = {};
        if (payload.field.dependsOn) {
            params = this._buildQueryParams(payload.field.dependsOn, payload.model);
        }
        params.pageSize = payload.pageSize;
        params.currentPage = payload.currentPage;
        params.mask = payload.search;
        if (payload.field.params) {
            this.lodash.extend(params, payload.field.params);
        }
        return this.restServiceFactory
            .get(payload.field.uri)
            .get(params)
            .then((response) => {
            const key = apiUtils.getKeyHoldingDataFromResponse(response);
            response[key] = this.populateAttributes(response[key], payload.field.idAttribute, payload.field.labelAttributes);
            return Promise.resolve(response);
        });
    }
    /**
     * Implementation of the [getItem]{@link DropdownPopulatorInterface#getItem} method.
     * @returns A promise that resolves to the option that was fetched
     */
    getItem(payload) {
        return this.restServiceFactory
            .get(payload.field.uri)
            .getById(payload.id)
            .then((item) => {
            item = this.populateAttributes([item], payload.field.idAttribute, payload.field.labelAttributes)[0];
            return Promise.resolve(item);
        });
    }
    _buildQueryParams(dependsOn, model) {
        const queryParams = dependsOn.split(',').reduce((obj, current) => {
            obj[current] = model[current];
            return obj;
        }, {});
        return queryParams;
    }
};
/* @ngInject */ exports.UriDropdownPopulator = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory,
        exports.LanguageService,
        core$1.TranslateService])
], /* @ngInject */ exports.UriDropdownPopulator);

/**
 * so here we return constructor function with pre-set dependencies.
 */
exports.DropdownPopulatorModule = class DropdownPopulatorModule {
};
exports.DropdownPopulatorModule = __decorate([
    core.NgModule({
        providers: [exports.OptionsDropdownPopulator, exports.UriDropdownPopulator]
    })
], exports.DropdownPopulatorModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Used to register Custom Populators that will be available for `GenericEditorDropdownServiceFactory`.
 *
 * A custom populator can be registered by providing the name of that populator without "DropdownPopulator" suffix
 * in the following properties of {@link GenericEditorField}.
 *
 * - propertType - e.g. `MyCustomDropdownPopulator` -> { propertyType: 'myCustom' }
 *
 * - cmsStrutureType - e.g. `MyCustomDropdownPopulator` -> { cmsStructureType: 'myCustom' }
 *
 * - smarteditComponentType - e.g. `MyCustomDropdownPopulator` -> { smarteditComponentType: 'myCustom' }
 *
 * - smarteditComponentType + qualifier - e.g. `MyCustomProductDropdownPopulator` { smarteditComponentType: 'myCustom', qualifier: 'product' }
 *
 * Note: The value of those properties is case insensitive.
 *
 * ### Example
 *
 *      \@NgModule({
 *          imports: [],
 *          providers: [
 *              {
 *                  provide: CustomDropdownPopulatorsToken,
 *                  useClass: MyCustomDropdownPopulator,
 *                  multi: true
 *               }
 *          ]
 *      })
 *      export class ExtensionModule {};
 */
const CustomDropdownPopulatorsToken = new core.InjectionToken('CustomDropdownPopulatorsToken');

exports.GenericEditorDropdownModule = class GenericEditorDropdownModule {
};
exports.GenericEditorDropdownModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.SelectModule, exports.DropdownPopulatorModule],
        declarations: [exports.GenericEditorDropdownComponent],
        entryComponents: [exports.GenericEditorDropdownComponent],
        exports: [exports.GenericEditorDropdownComponent],
        providers: [
            {
                provide: DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN,
                useValue: DROPDOWN_POPULATOR
            },
            {
                provide: LINKED_DROPDOWN_TOKEN,
                useValue: LINKED_DROPDOWN
            },
            {
                provide: CLICK_DROPDOWN_TOKEN,
                useValue: CLICK_DROPDOWN
            },
            GenericEditorUtil,
            // Injected by <se-generic-editor-dropdown>. It doesn't create a new instance (as it supposed to do because it's a factory function).
            // Instead, it returns a constructor function that is instantiated in `GenericEditorDropdownComponent#ngOnInit`.
            {
                provide: IGenericEditorDropdownServiceConstructor,
                useFactory: GenericEditorDropdownServiceFactory,
                deps: [
                    GenericEditorUtil,
                    utils.LogService,
                    LINKED_DROPDOWN_TOKEN,
                    CLICK_DROPDOWN_TOKEN,
                    DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN,
                    exports.SystemEventService,
                    exports.OptionsDropdownPopulator,
                    exports.UriDropdownPopulator,
                    [new core.Optional(), CustomDropdownPopulatorsToken] // Only available when Custom Populator has been provided.
                ]
            }
        ]
    })
], exports.GenericEditorDropdownModule);

let EditableDropdownModule = class EditableDropdownModule {
};
EditableDropdownModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.GenericEditorDropdownModule],
        declarations: [EditableDropdownComponent],
        entryComponents: [EditableDropdownComponent],
        exports: [EditableDropdownComponent]
    })
], EditableDropdownModule);

window.__smartedit__.addDecoratorPayload("Component", "EmailComponent", {
    selector: 'se-email',
    template: `<input fd-form-control [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.field.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" [max]="255" type="email" id="{{widget.field.qualifier}}-shortstring"/>`
});
let EmailComponent = class EmailComponent {
    constructor(widget) {
        this.widget = widget;
    }
};
EmailComponent = __decorate([
    core.Component({
        selector: 'se-email',
        template: `<input fd-form-control [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.field.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" [max]="255" type="email" id="{{widget.field.qualifier}}-shortstring"/>`
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], EmailComponent);

let EmailModule = class EmailModule {
};
EmailModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$1.TranslateModule.forChild(), forms.FormsModule, form.FormModule],
        declarations: [EmailComponent],
        entryComponents: [EmailComponent],
        exports: [EmailComponent]
    })
], EmailModule);

window.__smartedit__.addDecoratorPayload("Component", "EnumItemPrinterComponent", {
    selector: 'se-enum-item-printer',
    template: ` <span id="enum-{{ data.select.id }}">{{ data.item.label }}</span> `
});
let EnumItemPrinterComponent = class EnumItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
EnumItemPrinterComponent = __decorate([
    core.Component({
        selector: 'se-enum-item-printer',
        template: ` <span id="enum-{{ data.select.id }}">{{ data.item.label }}</span> `
    }),
    __param(0, core.Inject(ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], EnumItemPrinterComponent);
window.__smartedit__.addDecoratorPayload("Component", "EnumComponent", {
    selector: 'se-enum',
    template: `
        <se-select
            [id]="data.field.qualifier"
            class="se-generic-editor-dropdown"
            [(model)]="data.model[data.qualifier]"
            [isReadOnly]="data.isFieldDisabled()"
            [resetSearchInput]="false"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [placeholder]="'se.genericeditor.sedropdown.placeholder'"
            [showRemoveButton]="true"></se-select>
    `
});
let EnumComponent = class EnumComponent {
    constructor(data) {
        this.data = data;
        this.itemComponent = EnumItemPrinterComponent;
        const { editor, field, qualifier } = data;
        this.fetchStrategy = {
            fetchAll: (search) => __awaiter(this, void 0, void 0, function* () {
                // the refereshOptions does not resolve with items, it populates the "field.options" object with the fetched data
                yield editor.refreshOptions(field, qualifier, search);
                const options = field.options[qualifier];
                // Map code to id for SelectComponent that requires item to have an id property
                const selectItems = options.map((option) => (Object.assign(Object.assign({}, option), { id: option.code })));
                return selectItems;
            })
        };
    }
};
EnumComponent = __decorate([
    core.Component({
        selector: 'se-enum',
        template: `
        <se-select
            [id]="data.field.qualifier"
            class="se-generic-editor-dropdown"
            [(model)]="data.model[data.qualifier]"
            [isReadOnly]="data.isFieldDisabled()"
            [resetSearchInput]="false"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [placeholder]="'se.genericeditor.sedropdown.placeholder'"
            [showRemoveButton]="true"></se-select>
    `
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], EnumComponent);

const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION$1 = '0.01';
window.__smartedit__.addDecoratorPayload("Component", "FloatComponent", {
    selector: 'se-float',
    template: `<input pattern="^[-]?([0-9]*)(\.[0-9]{1,5})?$" type="number" id="{{widget.field.qualifier}}-float" class="fd-form-control" [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.field.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" [step]="precision"/>`
});
let FloatComponent = class FloatComponent {
    constructor(widget) {
        this.widget = widget;
        this.precision = DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION$1;
    }
};
FloatComponent = __decorate([
    core.Component({
        selector: 'se-float',
        template: `<input pattern="^[-]?([0-9]*)(\.[0-9]{1,5})?$" type="number" id="{{widget.field.qualifier}}-float" class="fd-form-control" [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.field.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" [step]="precision"/>`
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], FloatComponent);

let FloatModule = class FloatModule {
};
FloatModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$1.TranslateModule.forChild(), forms.FormsModule],
        declarations: [FloatComponent],
        entryComponents: [FloatComponent],
        exports: [FloatComponent]
    })
], FloatModule);

window.__smartedit__.addDecoratorPayload("Component", "LongStringComponent", {
    template: `
        <textarea
            class="fd-form__control"
            style="width: 100%;"
            [ngClass]="{ 'is-invalid': data.field.hasErrors, 'is-warning': data.field.hasWarnings }"
            [placeholder]="data.field.tooltip || '' | translate"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"></textarea>
    `,
    selector: 'se-long-string'
});
let LongStringComponent = class LongStringComponent {
    constructor(data) {
        this.data = data;
    }
};
LongStringComponent = __decorate([
    core.Component({
        template: `
        <textarea
            class="fd-form__control"
            style="width: 100%;"
            [ngClass]="{ 'is-invalid': data.field.hasErrors, 'is-warning': data.field.hasWarnings }"
            [placeholder]="data.field.tooltip || '' | translate"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"></textarea>
    `,
        selector: 'se-long-string'
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], LongStringComponent);

window.__smartedit__.addDecoratorPayload("Component", "NumberComponent", {
    selector: 'se-number',
    template: `<input [min]="1" [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" id="{{widget.qualifier}}-number" class="fd-form-control fd-input" type="number"/>`
});
exports.NumberComponent = class NumberComponent {
    constructor(systemEventService, widget) {
        this.systemEventService = systemEventService;
        this.widget = widget;
        const onClickEventName = `${this.widget.id}${this.widget.field.dependsOnField}${CLICK_DROPDOWN}`;
        this.unRegClickValueChanged = this.systemEventService.subscribe(onClickEventName, (_eventId, data) => this.onClickEvent(data));
    }
    onClickEvent(data) {
        this.widget.field.hideFieldWidget = data !== this.widget.field.dependsOnValue;
        if (this.widget.field.hideFieldWidget) {
            this.widget.model[this.widget.qualifier] = null;
        }
    }
};
exports.NumberComponent = __decorate([
    core.Component({
        selector: 'se-number',
        template: `<input [min]="1" [ngClass]="{ 'is-invalid': widget.field.hasErrors, 'is-warning': widget.field.hasWarnings }" [placeholder]="(widget.field.tooltip || '') | translate" [attr.name]="widget.qualifier" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" id="{{widget.qualifier}}-number" class="fd-form-control fd-input" type="number"/>`
    }),
    __param(1, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [exports.SystemEventService, Object])
], exports.NumberComponent);

exports.NumberModule = class NumberModule {
};
exports.NumberModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, core$1.TranslateModule.forChild(), forms.FormsModule],
        declarations: [exports.NumberComponent],
        entryComponents: [exports.NumberComponent],
        exports: [exports.NumberComponent]
    })
], exports.NumberModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP = new core.InjectionToken('resolvedLocaleToCKEDITORLocaleMap');
const RICH_TEXT_CONFIGURATION = new core.InjectionToken('richTextConfiguration');

/* @ngInject */ exports.GenericEditorSanitizationService = class /* @ngInject */ GenericEditorSanitizationService {
    constructor(domSanitizer) {
        this.domSanitizer = domSanitizer;
    }
    isSanitized(content) {
        const tempDiv = document.createElement('div');
        const tempComparedDiv = document.createElement('div');
        const sanitizedContent = this.domSanitizer
            .sanitize(core.SecurityContext.HTML, content)
            .replace(/&#10;/g, '\n')
            .replace(/&#160;/g, '\u00a0')
            .replace(/<br>/g, '<br />');
        tempDiv.innerHTML = sanitizedContent;
        const originalContent = content
            .replace(/&#10;/g, '\n')
            .replace(/&#160;/g, '\u00a0')
            .replace(/<br>/g, '<br />');
        tempComparedDiv.innerHTML = originalContent;
        return tempDiv.innerHTML === tempComparedDiv.innerHTML;
    }
};
/* @ngInject */ exports.GenericEditorSanitizationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [platformBrowser.DomSanitizer])
], /* @ngInject */ exports.GenericEditorSanitizationService);

exports.RichTextFieldLocalizationService = class RichTextFieldLocalizationService {
    constructor(languageService, resolvedLocaleToCKEDITORLocaleMap) {
        this.languageService = languageService;
        this.resolvedLocaleToCKEDITORLocaleMap = resolvedLocaleToCKEDITORLocaleMap;
    }
    localizeCKEditor() {
        return this.languageService.getResolveLocale().then((locale) => {
            CKEDITOR.config.language = this.convertResolvedToCKEditorLocale(locale);
        });
    }
    convertResolvedToCKEditorLocale(resolvedLocale) {
        const conversion = this.resolvedLocaleToCKEDITORLocaleMap[resolvedLocale];
        if (conversion) {
            return conversion;
        }
        return resolvedLocale;
    }
};
exports.RichTextFieldLocalizationService = __decorate([
    __param(1, core.Inject(RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP)),
    __metadata("design:paramtypes", [exports.LanguageService, Object])
], exports.RichTextFieldLocalizationService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class RichTextLoaderService {
    constructor() {
        this.loadPromise = new Promise((resolve) => {
            this.checkLoadedInterval = setInterval(() => {
                if (CKEDITOR.status === 'loaded') {
                    resolve();
                    clearInterval(this.checkLoadedInterval);
                    this.checkLoadedInterval = null;
                }
            }, 100);
        });
    }
    load() {
        return this.loadPromise;
    }
}

window.__smartedit__.addDecoratorPayload("Component", "RichTextFieldComponent", {
    selector: 'se-rich-text-field',
    template: `<textarea #textarea class="fd-form__control" [ngClass]="{'has-error': widget.field.hasErrors}" name="{{widget.field.qualifier}}-{{widget.qualifier}}" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" (change)="reassignUserCheck()"></textarea><div *ngIf="requiresUserCheck()"><div id="richTextWarningMessage" [hidden]="widget.field.isUserChecked"><span fd-object-status [status]="'critical'" [glyph]="'message-warning'" class="se-generic-editor__warning" label="{{'se.editor.richtext.message.warning' | translate}}"></span></div><input id="richTextWarningCheckBox" type="checkbox" [(ngModel)]="widget.field.isUserChecked" (click)="checkboxOnClick($event.target)"/> <span [ngClass]="{'warning-check-msg': true, 'not-checked': widget.editor.hasFrontEndValidationErrors && !widget.field.isUserChecked}">{{'se.editor.richtext.check' | translate}}</span></div>`
});
exports.RichTextFieldComponent = class RichTextFieldComponent {
    constructor(widget, seRichTextLoaderService, seRichTextConfiguration, genericEditorSanitizationService, seRichTextFieldLocalizationService, settingsService) {
        this.widget = widget;
        this.seRichTextLoaderService = seRichTextLoaderService;
        this.seRichTextConfiguration = seRichTextConfiguration;
        this.genericEditorSanitizationService = genericEditorSanitizationService;
        this.seRichTextFieldLocalizationService = seRichTextFieldLocalizationService;
        this.settingsService = settingsService;
    }
    ngAfterViewInit() {
        return this.settingsService
            .get('cms.components.allowUnsafeJavaScript')
            .then((allowUnsafeJavaScript) => this.seRichTextLoaderService.load().then(() => {
            this.seRichTextConfiguration.allowedContent = allowUnsafeJavaScript === 'true';
            this.editorInstance = CKEDITOR.replace(this.textarea.nativeElement, this.seRichTextConfiguration);
            this.seRichTextFieldLocalizationService.localizeCKEditor();
            if (this.editorInstance) {
                this.editorInstance.on('change', () => this.onChange());
                this.editorInstance.on('mode', () => this.onMode());
                CKEDITOR.on('instanceReady', (ev) => this.onInstanceReady(ev));
            }
        }));
    }
    ngOnDestroy() {
        if (this.editorInstance && CKEDITOR.instances[this.editorInstance.name]) {
            CKEDITOR.instances[this.editorInstance.name].destroy();
        }
    }
    onChange() {
        setTimeout(() => {
            this.widget.model[this.widget.qualifier] = this.editorInstance.getData();
            this.reassignUserCheck();
            this.toggleSubmitButton(this.requiresUserCheck() && !this.widget.field.isUserChecked);
        });
    }
    onMode() {
        if (this.editorInstance.mode === 'source') {
            const editable = this.editorInstance.editable();
            editable.attachListener(editable, 'input', () => {
                this.editorInstance.fire('change');
            });
        }
    }
    onInstanceReady(ev) {
        const tags = CKEDITOR.dtd;
        const elements = Object.assign(Object.assign(Object.assign(Object.assign(Object.assign({}, tags.$nonBodyContent), tags.$block), tags.$listItem), tags.$tableContent), { br: 1 });
        for (const element of Object.keys(elements)) {
            ev.editor.dataProcessor.writer.setRules(element, {
                indent: false,
                breakBeforeOpen: false,
                breakAfterOpen: false,
                breakBeforeClose: false,
                breakAfterClose: false
            });
        }
        ev.editor.dataProcessor.writer.lineBreakChars = '';
        ev.editor.dataProcessor.writer.indentationChars = '';
    }
    requiresUserCheck() {
        let requiresUserCheck = false;
        for (const qualifier in this.widget.field.requiresUserCheck) {
            if (this.widget.field.requiresUserCheck.hasOwnProperty(qualifier)) {
                requiresUserCheck =
                    requiresUserCheck || this.widget.field.requiresUserCheck[qualifier];
            }
        }
        return requiresUserCheck;
    }
    reassignUserCheck() {
        if (this.widget.model &&
            this.widget.qualifier &&
            this.widget.model[this.widget.qualifier]) {
            const sanitizedContentMatchesContent = this.genericEditorSanitizationService.isSanitized(this.widget.model[this.widget.qualifier]);
            this.widget.field.requiresUserCheck = this.widget.field.requiresUserCheck || {};
            this.widget.field.requiresUserCheck[this.widget.qualifier] =
                !sanitizedContentMatchesContent;
        }
        else {
            this.widget.field.requiresUserCheck = this.widget.field.requiresUserCheck || {};
            this.widget.field.requiresUserCheck[this.widget.qualifier] = false;
        }
    }
    checkboxOnClick(event) {
        this.toggleSubmitButton(!event.checked);
    }
    toggleSubmitButton(state) {
        this.widget.editor.api.setInProgress(state);
    }
};
__decorate([
    core.ViewChild('textarea', { static: false }),
    __metadata("design:type", core.ElementRef)
], exports.RichTextFieldComponent.prototype, "textarea", void 0);
exports.RichTextFieldComponent = __decorate([
    core.Component({
        selector: 'se-rich-text-field',
        template: `<textarea #textarea class="fd-form__control" [ngClass]="{'has-error': widget.field.hasErrors}" name="{{widget.field.qualifier}}-{{widget.qualifier}}" [disabled]="widget.isFieldDisabled()" [(ngModel)]="widget.model[widget.qualifier]" (change)="reassignUserCheck()"></textarea><div *ngIf="requiresUserCheck()"><div id="richTextWarningMessage" [hidden]="widget.field.isUserChecked"><span fd-object-status [status]="'critical'" [glyph]="'message-warning'" class="se-generic-editor__warning" label="{{'se.editor.richtext.message.warning' | translate}}"></span></div><input id="richTextWarningCheckBox" type="checkbox" [(ngModel)]="widget.field.isUserChecked" (click)="checkboxOnClick($event.target)"/> <span [ngClass]="{'warning-check-msg': true, 'not-checked': widget.editor.hasFrontEndValidationErrors && !widget.field.isUserChecked}">{{'se.editor.richtext.check' | translate}}</span></div>`
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __param(2, core.Inject(RICH_TEXT_CONFIGURATION)),
    __metadata("design:paramtypes", [Object, RichTextLoaderService, Object, exports.GenericEditorSanitizationService,
        exports.RichTextFieldLocalizationService,
        exports.SettingsService])
], exports.RichTextFieldComponent);

exports.RichTextFieldModule = class RichTextFieldModule {
};
exports.RichTextFieldModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, forms.FormsModule, core$1.TranslateModule.forChild(), objectStatus.ObjectStatusModule],
        providers: [
            exports.GenericEditorSanitizationService,
            exports.RichTextFieldLocalizationService,
            RichTextLoaderService,
            exports.SettingsService,
            {
                provide: RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP,
                useValue: {
                    in: 'id',
                    es_CO: 'es'
                }
            },
            {
                provide: RICH_TEXT_CONFIGURATION,
                useValue: {
                    toolbar: 'full',
                    toolbar_full: [
                        {
                            name: 'basicstyles',
                            items: ['Bold', 'Italic', 'Strike', 'Underline']
                        },
                        {
                            name: 'paragraph',
                            items: ['BulletedList', 'NumberedList', 'Blockquote']
                        },
                        {
                            name: 'editing',
                            items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
                        },
                        {
                            name: 'links',
                            items: ['Link', 'Unlink', 'Anchor']
                        },
                        {
                            name: 'tools',
                            items: ['SpellChecker', 'Maximize']
                        },
                        '/',
                        {
                            name: 'styles',
                            items: [
                                'Format',
                                'Font',
                                'FontSize',
                                'TextColor',
                                'PasteText',
                                'PasteFromWord',
                                'RemoveFormat'
                            ]
                        },
                        {
                            name: 'insert',
                            items: ['Image', 'Table', 'SpecialChar']
                        },
                        {
                            name: 'forms',
                            items: ['Outdent', 'Indent']
                        },
                        '/',
                        {
                            name: 'clipboard',
                            items: ['Undo', 'Redo']
                        },
                        {
                            name: 'document',
                            items: ['PageBreak', 'Source']
                        }
                    ],
                    disableNativeSpellChecker: false,
                    height: '100px',
                    width: '100%',
                    autoParagraph: false,
                    basicEntities: false,
                    fillEmptyBlocks: false,
                    extraAllowedContent: 'div(*)[role]',
                    extraPlugins: 'colorbutton, colordialog, font',
                    font_names: 'Arial/Arial, Helvetica, sans-serif;Times New Roman/Times New Roman, Times, serif;Verdana',
                    fontSize_sizes: '10/10px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;36/36px;48/48px;',
                    protectedSource: [/\r|\n|\r\n|\t/g]
                }
            }
        ],
        declarations: [exports.RichTextFieldComponent],
        entryComponents: [exports.RichTextFieldComponent],
        exports: [exports.RichTextFieldComponent]
    })
], exports.RichTextFieldModule);

window.__smartedit__.addDecoratorPayload("Component", "ShortStringComponent", {
    template: `
        <input
            type="text"
            fd-form-control
            [state]="data.field.hasErrors ? 'error' : ''"
            id="{{ data.field.qualifier }}-shortstring"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"
            [ngClass]="{
                'is-warning': data.field.hasWarnings
            }" />
    `,
    selector: 'se-short-string'
});
exports.ShortStringComponent = class ShortStringComponent {
    constructor(data) {
        this.data = data;
    }
};
exports.ShortStringComponent = __decorate([
    core.Component({
        template: `
        <input
            type="text"
            fd-form-control
            [state]="data.field.hasErrors ? 'error' : ''"
            id="{{ data.field.qualifier }}-shortstring"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"
            [ngClass]="{
                'is-warning': data.field.hasWarnings
            }" />
    `,
        selector: 'se-short-string'
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], exports.ShortStringComponent);

window.__smartedit__.addDecoratorPayload("Component", "TextComponent", {
    template: ` <p [id]="data.field.qualifier + '-text'">{{ data.model[data.qualifier] }}</p> `,
    selector: 'se-component-text'
});
let TextComponent = class TextComponent {
    constructor(data) {
        this.data = data;
    }
};
TextComponent = __decorate([
    core.Component({
        template: ` <p [id]="data.field.qualifier + '-text'">{{ data.model[data.qualifier] }}</p> `,
        selector: 'se-component-text'
    }),
    __param(0, core.Inject(GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object])
], TextComponent);

var /* @ngInject */ EditorFieldMappingService_1;
/**
 * FLOAT PRECISION
 */
/* @internal  */
const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = '0.01';
/**
 * The editorFieldMappingService contains the strategies that the `GenericEditorComponent` component
 * uses to control the rendering of a field. When the genericEditor directive is about to display a field, it queries the
 * editorFieldMappingService service to retrieve the right strategies to use. Internally, this service makes this selection based on three
 * matchers:
 * <ul>
 * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
 * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
 * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
 * </ul>
 * These three matchers are used together to provide finer-grained control on the selection of strategies. A matcher itself is either a
 * string or a function that will be used to determine whether a value is considered a match or not. The following list describes
 * how a matcher behaves depending on its type:
 * <ul>
 *  <li><b>String</b>:                      Matches only the parameters that equal the provided string. </li>
 *  <li><b>Null</b>:                        Matches any parameter provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
 *  <li><b>Function</b>:                    Matches only the parameters for which the provided callback returns true. This option allows more
 *                                          control over the parameters to match.
 * </li>
 * </ul>
 * <br/>
 *
 * Currently, there are two types of strategies that the genericEditor uses to control the way a field is rendered:
 * <ul>
 *  <li><b>editor field mapping</b>:        This strategy is used to select and customize which property editor is to be used for this field.</li>
 *  <li><b>tab field mapping</b>:           This strategy is used to select the tab in the genericEditor where a field will be positioned.</li>
 * </ul>
 *
 */
/* @ngInject */ exports.EditorFieldMappingService = /* @ngInject */ EditorFieldMappingService_1 = class /* @ngInject */ EditorFieldMappingService {
    constructor(logService) {
        this.logService = logService;
        // --------------------------------------------------------------------------------------
        // Variables
        // --------------------------------------------------------------------------------------
        this._editorsFieldMapping = [];
        this._fieldsTabsMapping = [];
    }
    // --------------------------------------------------------------------------------------
    // Public API
    // --------------------------------------------------------------------------------------
    /**
     * This method overrides the default strategy of the `GenericEditorComponent` directive.
     * used to choose the property editor for a given field. Internally, this selection is based on three matchers:
     * <ul>
     * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
     * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
     * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
     * </ul>
     * Only the fields that match all three matchers will be overriden.
     *
     * The following example shows how some sample fields would match some mappings:
     * <pre>
     * const field1 = {
     * 		cmsStructureType: 'ShortString',
     * 		smarteditComponentType: 'CmsParagraphComponent',
     * 		qualifier: 'name'
     * };
     *
     *  const field2 = {
     *      cmsStructureType: 'Boolean',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'visible'
     *  };
     * </pre>
     *
     * <pre>
     * // This mapping won't match any of the fields. They don't match all three matchers.
     * editorFieldMappingService.addFieldMapping(
     *  'ShortString', 'CmsParagraphComponent', 'visible', configuration);
     *
     * // This mapping will match field2. It matches all three matchers perfectly.
     * editorFieldMappingService.addFieldMapping(
     *  'Boolean', 'CmsParagraphComponent', 'visible', configuration);
     *
     * // This mapping will match both fields. They match all three matchers.
     * // Note that both null and '*' represent a wildcard that accepts any value.
     * editorFieldMappingService.addFieldMapping(
     *  null, 'CmsParagraphComponent', '*', configuration);
     * </pre>
     *
     * <b>Note:</b> <br/>
     * The genericEditor has some predefined editors for the following cmsStructureTypes:
     * <ul>
     * 		<li><b>ShortString</b>:			Displays a text input field.</li>
     * 		<li><b>LongString</b>:  		Displays a text area.</li>
     * 		<li><b>RichText</b>:    		Displays an HTML/rich text editor.</li>
     * 		<li><b>Boolean</b>:     		Displays a check box.</li>
     * 		<li><b>DateTime</b>:        	Displays an input field with a date-time picker.</li>
     * 		<li><b>Media</b>:       		Displays a filterable dropdown list of media</li>
     * 		<li><b>Enum</b>:		 		Displays a filterable dropdown list of the enum class values identified by cmsStructureEnumType property.
     * 		<li><b>EditableDropdown</b>: 	Displays a configurable dropdown list that is enabled by `GenericEditorDropdownComponent`.
     * </ul>
     * <br />
     * You can program the `GenericEditorComponent` to use other property editors for these
     * cmsStructureTypes. You can also add custom cmsStructureTypes.
     * All default and custom property editors are HTML templates. These templates must adhere to the PropertyEditorTemplate {@link PropertyEditorTemplate contract}.
     *
     * @param structureTypeName The matcher used to identify the cmsStructureTypes for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
     *  <li><b>field:</b>                      The field to evaluate.</li>
     *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param componentTypeName The matcher used to identify the smarteditComponentType for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
     *  <li><b>field:</b>                     The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param discriminator The matcher used to identify the discriminator for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
     *  <li><b>field:</b>                       The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * @param configuration The holder that contains the override instructions. Cannot be null.
     * @param configuration.template The path to the HTML template used in the override. Cannot be null.
     * @param  configuration.customSanitize Custom sanitize function for a custom property editor. It's provided with a payload.
     */
    addFieldMapping(structureTypeName, componentTypeName, discriminator, configuration) {
        this._addMapping(structureTypeName, componentTypeName, discriminator, configuration, this._editorsFieldMapping);
    }
    /**
     * This method is used by the genericEditor to retrieve the property editor to be rendered in a generic editor, along with its configuration.
     * If more than one property editor could be applied to the provided field, the one with the most accurate match will be used.
     *
     * Note:
     * Currently, all templates in SmartEdit use the short form. Before returning a response, this method ensures that
     * the template provided to the generic editor is in short form. For example:
     * - A template 'genericEditor/templates/shortStringTemplate.html' will be transformed to 'shortStringTemplate.html'
     *
     * @param field The object that represents the field that the property editor is retrieved for.
     * @param field.cmsStructureType The cmsStructureType that the property editor is retrieved for.
     * @param field.smarteditComponentType The smarteditComponentType that the property editor is retrieved for.
     * @param field.qualifier The field name of the smarteditComponentType that the property editor is retrieved for.
     * @param componentTypeStructure The smarteditComponentType structure of the componenent that contains the field that the property editor
     * is retrieved for.
     * @returns The configuration of the property editor to be used for this field. Can be null if no adequate match is found.
     *
     */
    getEditorFieldMapping(field, componentTypeStructure) {
        let fieldMapping = this._getMapping(field, componentTypeStructure, this._editorsFieldMapping);
        if (!fieldMapping) {
            this.logService.warn('editorFieldMappingService - Cannot find suitable field mapping for type ', field.cmsStructureType);
            fieldMapping = null;
        }
        else if (fieldMapping && fieldMapping.template) {
            fieldMapping.template = this._cleanTemplate(fieldMapping.template);
        }
        return fieldMapping;
    }
    /**
     * This method overrides the default strategy of the `GenericEditorComponent` directive
     * used to choose the tab where to render a field in the generic editor. Internally, this selection is based on three elements:
     * <ul>
     * 	<li><b>structureTypeName</b>:			The cmsStructureType of a field.</li>
     * 	<li><b>componentTypeName</b>:			The smarteditComponentType of the component containing the field.</li>
     * <li><b>discriminator</b>:			    The qualifier used by the genericEditor to identify the field.</li>
     * </ul>
     * Only the fields that match all three elements will be overriden.
     *
     * The following example shows how sample fields would match some mappings:
     * <pre>
     *  const field1 = {
     *      cmsStructureType: 'ShortString',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'name'
     *  };
     *
     *  const field2 = {
     *      cmsStructureType: 'Boolean',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'visible'
     *  };
     * </pre>
     *
     * <pre>
     * // This mapping won't match any of the fields. They don't match all three matchers.
     * editorFieldMappingService.addFieldTabMapping(
     *  'ShortString', 'CmsParagraphComponent', 'visible', tabId);
     *
     * // This mapping will match field2. It matches all three matchers perfectly.
     * editorFieldMappingService.addFieldTabMapping(
     *  'Boolean', 'CmsParagraphComponent', 'visible', tabId);
     *
     * // This mapping will match both fields. They match all three matchers.
     * // Note that both null and '*' represent a wildcard that accepts any value.
     * editorFieldMappingService.addFieldTabMapping(
     *  null, 'CmsParagraphComponent', '*', tabId);
     * </pre>
     *
     * @param structureTypeNameMatcher The matcher used to identify the cmsStructureTypes for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
     *  <li><b>field:</b>                      The field to evaluate.</li>
     *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param {String | Function} componentTypeName The matcher used to identify the smarteditComponentType for which for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
     *  <li><b>field:</b>                     The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param discriminator The matcher used to identify the discriminator for which for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
     *  <li><b>field:</b>                       The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * @param tabId The ID of the tab where the field must be rendered in the generic editor.
     */
    addFieldTabMapping(structureTypeName, componentTypeName, discriminator, tabId) {
        this._addMapping(structureTypeName, componentTypeName, discriminator, tabId, this._fieldsTabsMapping);
    }
    /**
     * This method is used by the genericEditor to retrieve the tab where the field will be rendered in the generic editor.
     * If more than one tab matches the field provided, then the tab with the most accurate match will be used.
     *
     * @param field The object that represents the field that the tab is retrieved for.
     * @param field.cmsStructureType The cmsStructureType that the tab is retrieved for.
     * @param field.smarteditComponentType The smarteditComponentType that the tab is retrieved for.
     * @param field.qualifier The field name of the smarteditComponentType that the tab is retrieved for.
     * @param componentTypeStructure The smarteditComponentType structure of the component that contains the field that the tab is retrieved for.
     * @returns The ID of the tab where this field must reside. Can be null if no adequate match is found.
     *
     */
    getFieldTabMapping(field, componentTypeStructure) {
        return this._getMapping(field, componentTypeStructure, this._fieldsTabsMapping);
    }
    // --------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------
    _addMapping(structureTypeMatcher, componentTypeMatcher, discriminatorMatcher, mappedValue, collection) {
        structureTypeMatcher = this._validateField(structureTypeMatcher);
        componentTypeMatcher = this._validateField(componentTypeMatcher);
        discriminatorMatcher = this._validateField(discriminatorMatcher);
        const newMapping = {
            structureTypeMatcher,
            componentTypeMatcher,
            discriminatorMatcher,
            value: mappedValue
        };
        lodash.remove(collection, (element) => element.structureTypeMatcher === structureTypeMatcher &&
            element.componentTypeMatcher === componentTypeMatcher &&
            element.discriminatorMatcher === discriminatorMatcher);
        collection.push(newMapping);
    }
    _validateField(field) {
        if (field === null) {
            field = /* @ngInject */ EditorFieldMappingService_1.WILDCARD;
        }
        else if (typeof field !== 'string' && typeof field !== 'function') {
            throw new Error('editorFieldMappingService: Mapping matcher must be of type string or function.');
        }
        return field;
    }
    _getMapping(field, componentTypeStructure, collection) {
        let result = null;
        let maxValue = 0;
        collection.forEach((mappingEntry) => {
            const mappingValue = this._evaluateMapping(mappingEntry, field, componentTypeStructure);
            if (mappingValue > maxValue) {
                result = mappingEntry.value;
                maxValue = mappingValue;
            }
        });
        return result;
    }
    _evaluateMapping(mappingEntry, field, componentTypeStructure) {
        let componentTypeMatch;
        let discriminatorMatch;
        let mappingMatch = /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE;
        const structureTypeMatch = this._evaluateMatcher(mappingEntry.structureTypeMatcher, field.cmsStructureType, field, componentTypeStructure);
        if (structureTypeMatch !== /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE) {
            componentTypeMatch = this._evaluateMatcher(mappingEntry.componentTypeMatcher, field.smarteditComponentType, field, componentTypeStructure);
            if (componentTypeMatch !== /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE) {
                discriminatorMatch = this._evaluateMatcher(mappingEntry.discriminatorMatcher, field.qualifier, field, componentTypeStructure);
            }
        }
        if (structureTypeMatch !== /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE &&
            componentTypeMatch !== /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE &&
            discriminatorMatch !== /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE) {
            mappingMatch = structureTypeMatch + componentTypeMatch + discriminatorMatch;
        }
        return mappingMatch;
    }
    _evaluateMatcher(matcher, actualValue, field, componentTypeStructure) {
        if (typeof matcher === 'string') {
            if (matcher === /* @ngInject */ EditorFieldMappingService_1.WILDCARD) {
                return /* @ngInject */ EditorFieldMappingService_1.MATCH.PARTIAL;
            }
            else {
                return this._exactValueMatchPredicate(matcher, actualValue)
                    ? /* @ngInject */ EditorFieldMappingService_1.MATCH.EXACT
                    : /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE;
            }
        }
        else {
            return matcher(actualValue, field, componentTypeStructure)
                ? /* @ngInject */ EditorFieldMappingService_1.MATCH.EXACT
                : /* @ngInject */ EditorFieldMappingService_1.MATCH.NONE;
        }
    }
    _registerDefaultFieldMappings() {
        if (!this._initialized) {
            this._initialized = true;
            this.addFieldMapping('Boolean', null, null, {
                component: exports.BooleanComponent
            });
            this.addFieldMapping('ShortString', null, null, {
                component: exports.ShortStringComponent,
                validators: {
                    notBlank: (id, structure, required, component) => (!!required ? !!required : undefined)
                }
            });
            this.addFieldMapping('Text', null, null, {
                component: TextComponent
            });
            this.addFieldMapping('LongString', null, null, {
                component: LongStringComponent
            });
            this.addFieldMapping('RichText', null, null, {
                component: exports.RichTextFieldComponent
            });
            this.addFieldMapping('Number', null, null, {
                component: exports.NumberComponent
            });
            this.addFieldMapping('Float', null, null, {
                component: FloatComponent
            });
            this.addFieldMapping('Dropdown', null, null, {
                component: DropdownComponent
            });
            this.addFieldMapping('EditableDropdown', null, null, {
                component: EditableDropdownComponent
            });
            this.addFieldMapping('DateTime', null, null, {
                component: DateTimePickerComponent
            });
            this.addFieldMapping('Enum', null, null, {
                component: EnumComponent
            });
            this.addFieldMapping('Email', null, null, {
                component: EmailComponent,
                validators: {
                    email: (id, structure, required, component) => true
                }
            });
        }
    }
    _cleanTemplate(template) {
        const index = template ? template.lastIndexOf('/') : -1;
        if (index !== -1) {
            template = template.substring(index + 1);
        }
        return template;
    }
    // --------------------------------------------------------------------------------------
    // Predicates
    // --------------------------------------------------------------------------------------
    _exactValueMatchPredicate(expectedValue, actualValue) {
        return expectedValue === actualValue;
    }
};
// --------------------------------------------------------------------------------------
// Constants
// --------------------------------------------------------------------------------------
/* @ngInject */ exports.EditorFieldMappingService.WILDCARD = '*';
/* @ngInject */ exports.EditorFieldMappingService.MATCH = {
    NONE: 0,
    PARTIAL: 1,
    EXACT: 4 // An exact match is always better than a partial.
};
/* @ngInject */ exports.EditorFieldMappingService = /* @ngInject */ EditorFieldMappingService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.LogService])
], /* @ngInject */ exports.EditorFieldMappingService);

var /* @ngInject */ FetchEnumDataHandler_1;
/* @internal  */
/* @ngInject */ exports.FetchEnumDataHandler = /* @ngInject */ FetchEnumDataHandler_1 = class /* @ngInject */ FetchEnumDataHandler {
    constructor(restServiceFactory) {
        this.restServiceFactory = restServiceFactory;
        this.restServiceForEnum = this.restServiceFactory.get(ENUM_RESOURCE_URI);
    }
    static resetForTests() {
        /* @ngInject */ FetchEnumDataHandler_1.cache = {};
    }
    findByMask(field, search) {
        return (/* @ngInject */ FetchEnumDataHandler_1.cache[field.cmsStructureEnumType]
            ? Promise.resolve(/* @ngInject */ FetchEnumDataHandler_1.cache[field.cmsStructureEnumType])
            : Promise.resolve(this.restServiceForEnum.get({
                enumClass: field.cmsStructureEnumType
            }))).then((response) => {
            /* @ngInject */ FetchEnumDataHandler_1.cache[field.cmsStructureEnumType] = response;
            return /* @ngInject */ FetchEnumDataHandler_1.cache[field.cmsStructureEnumType].enums.filter((element) => utils.stringUtils.isBlank(search) ||
                element.label.toUpperCase().indexOf(search.toUpperCase()) > -1);
        });
    }
    getById(field, identifier) {
        return null;
    }
};
/* @ngInject */ exports.FetchEnumDataHandler.cache = {};
/* @ngInject */ exports.FetchEnumDataHandler = /* @ngInject */ FetchEnumDataHandler_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory])
], /* @ngInject */ exports.FetchEnumDataHandler);

/**
 * The Generic Editor is a class that makes it possible for SmartEdit users (CMS managers, editors, etc.) to edit components in the SmartEdit interface.
 * The Generic Editor class is used by the `GenericEditorComponent` component.
 * The genericEditor directive makes a call either to a Structure API or, if the Structure API is not available, it reads the data from a local structure to request the information that it needs to build an HTML form.
 * It then requests the component by its type and ID from the Content API. The genericEditor directive populates the form with the data that is has received.
 * The form can now be used to edit the component. The modified data is saved using the Content API if it is provided else it would return the form data itself.
 *
 *
 * **The structure and the REST structure API</strong>.**
 *
 * The constructor of the `GenericEditorFactoryService` must be provided with the pattern of a REST Structure API, which must contain the string  ":smarteditComponentType", or with a local data structure.
 * If the pattern, Structure API, or the local structure is not provided, the Generic Editor will fail. If the Structure API is used, it must return a JSON payload that holds an array within the attributes property.
 * If the actual structure is used, it must return an array. Each entry in the array provides details about a component property to be displayed and edited. The following details are provided for each property:
 *
 * <ul>
 * <li><strong>qualifier:</strong> Name of the property.
 * <li><strong>i18nKey:</strong> Key of the property label to be translated into the requested language.
 * <li><strong>editable:</strong> Boolean that indicates if a property is editable or not. The default value is true.
 * <li><strong>localized:</strong> Boolean that indicates if a property is localized or not. The default value is false.
 * <li><strong>required:</strong> Boolean that indicates if a property is mandatory or not. The default value is false.
 * <li><strong>cmsStructureType:</strong> Value that is used to determine which form widget (property editor) to display for a specified property.
 * The selection is based on an extensible strategy mechanism owned by {@link EditorFieldMappingService}.
 * <li><strong>cmsStructureEnumType:</strong> The qualified name of the Enum class when cmsStructureType is "Enum"
 * </li>
 * <ul><br/>
 *
 * <b>Note:</b><br/>
 * The generic editor has a tabset within. This allows it to display complex types in an organized and clear way. By default, all fields are stored
 * in the default tab, and if there is only one tab the header is hidden. The selection and configuration of where each field resides is
 * controlled by the  {@link EditorFieldMappingService}. Similarly, the rendering
 * of tabs can be customized with the `GenericEditorTabService`.
 *
 *
 *
 * There are two options when you use the Structure API. The first option is to use an API resource that returns the structure object.
 * The following is an example of the JSON payload that is returned by the Structure API in this case:
 *
 *      {
 *          attributes: [{
 *              cmsStructureType: "ShortString",
 *              qualifier: "someQualifier1",
 *              i18nKey: 'i18nkeyForsomeQualifier1',
 *              localized: false
 *          }, {
 *              cmsStructureType: "LongString",
 *              qualifier: "someQualifier2",
 *              i18nKey: 'i18nkeyForsomeQualifier2',
 *              localized: false
 *          }, {
 *              cmsStructureType: "RichText",
 *              qualifier: "someQualifier3",
 *              i18nKey: 'i18nkeyForsomeQualifier3',
 *              localized: true,
 *              required: true
 *          }, {
 *              cmsStructureType: "Boolean",
 *              qualifier: "someQualifier4",
 *              i18nKey: 'i18nkeyForsomeQualifier4',
 *              localized: false
 *          }, {
 *              cmsStructureType: "DateTime",
 *              qualifier: "someQualifier5",
 *              i18nKey: 'i18nkeyForsomeQualifier5',
 *              localized: false
 *          }, {
 *              cmsStructureType: "Media",
 *              qualifier: "someQualifier6",
 *              i18nKey: 'i18nkeyForsomeQualifier6',
 *              localized: true,
 *              required: true
 *          }, {
 *              cmsStructureType: "Enum",
 *              cmsStructureEnumType:'de.mypackage.Orientation'
 *              qualifier: "someQualifier7",
 *              i18nKey: 'i18nkeyForsomeQualifier7',
 *              localized: true,
 *              required: true
 *          }]
 *      }
 *
 *
 * The second option is to use an API resource that returns a list of structures. In this case, the generic editor will select the first element from the list and use it to display its attributes.
 * The generic editor expects the structures to be in one of the two fields below.
 *
 *      {
 *          structures: [{}, {}]
 *      }
 *
 *
 * or
 *
 *      {
 *          componentTypes: [{}, {}]
 *      }
 *
 *
 * If the list has more than one element, the Generic Editor will throw an exception, otherwise it will get the first element on the list.
 * The following is an example of the JSON payload that is returned by the Structure API in this case:
 *
 *      {
 *          structures: [
 *              {
 *                  attributes: [{
 *                 	    cmsStructureType: "ShortString",
 *                 		qualifier: "someQualifier1",
 *                 		i18nKey: 'i18nkeyForsomeQualifier1',
 *                 		localized: false
 *             		}, {
 *                 		cmsStructureType: "LongString",
 *                 		qualifier: "someQualifier2",
 *                 		i18nKey: 'i18nkeyForsomeQualifier2',
 *                 		localized: false
 *         	   		}]
 *              }
 *          ]
 *      }
 *
 *
 *      {
 *          componentTypes: [
 *              {
 *                  attributes: [{
 *                 	    cmsStructureType: "ShortString",
 *                 		qualifier: "someQualifier1",
 *                 		i18nKey: 'i18nkeyForsomeQualifier1',
 *                 		localized: false
 *             		}, {
 *                 		cmsStructureType: "LongString",
 *                 		qualifier: "someQualifier2",
 *                 		i18nKey: 'i18nkeyForsomeQualifier2',
 *                 		localized: false
 *         	   		}]
 *              }
 *          ]
 *      }
 *
 *
 * The following is an example of the expected format of a structure:
 *
 *
 *      [{
 *          cmsStructureType: "ShortString",
 *          qualifier: "someQualifier1",
 *          i18nKey: 'i18nkeyForsomeQualifier1',
 *          localized: false
 *      }, {
 *          cmsStructureType: "LongString",
 *          qualifier: "someQualifier2",
 *          i18nKey: 'i18nkeyForsomeQualifier2',
 *          editable: false,
 *          localized: false
 *      }, {
 *          cmsStructureType: "RichText",
 *          qualifier: "someQualifier3",
 *          i18nKey: 'i18nkeyForsomeQualifier3',
 *          localized: true,
 *          required: true
 *      }, {
 *          cmsStructureType: "Boolean",
 *          qualifier: "someQualifier4",
 *          i18nKey: 'i18nkeyForsomeQualifier4',
 *          localized: false
 *      }, {
 *          cmsStructureType: "DateTime",
 *          qualifier: "someQualifier5",
 *          i18nKey: 'i18nkeyForsomeQualifier5',
 *          editable: false,
 *          localized: false
 *      }, {
 *          cmsStructureType: "Media",
 *          qualifier: "someQualifier6",
 *          i18nKey: 'i18nkeyForsomeQualifier6',
 *          localized: true,
 *          required: true
 *      }, {
 *          cmsStructureType: "Enum",
 *          cmsStructureEnumType:'de.mypackage.Orientation'
 *          qualifier: "someQualifier7",
 *          i18nKey: 'i18nkeyForsomeQualifier7',
 *          localized: true,
 *          required: true
 *      }]
 *
 * <strong>The REST CRUD API</strong>, is given to the constructor of `GenericEditorFactoryService`.
 * The CRUD API must support GET and PUT of JSON payloads.
 * The PUT method must return the updated payload in its response. Specific to the GET and PUT, the payload must fulfill the following requirements:
 * <ul>
 * 	<li>DateTime types: Must be serialized as long timestamps.</li>
 * 	<li>Media types: Must be serialized as identifier strings.</li>
 * 	<li>If a cmsStructureType is localized, then we expect that the CRUD API returns a map containing the type (string or map) and the map of values, where the key is the language and the value is the content that the type returns.</li>
 * </ul>
 *
 * The following is an example of a localized payload:
 *
 *      {
 *          content: {
 * 		        'en': 'content in english',
 * 		        'fr': 'content in french',
 * 		        'hi': 'content in hindi'
 * 	    }
 *
 *
 *
 *
 * If a validation warning or error occurs, the PUT method of the REST CRUD API will return a validation warning/error object that contains an array of validation messages. The information returned for each validation message is as follows:
 * <ul>
 * 	<li><strong>subject:</strong> The qualifier that has the error</li>
 * 	<li><strong>message:</strong> The error message to be displayed</li>
 * 	<li><strong>type:</strong> The type of message returned. This is of the type ValidationError or Warning.</li>
 * 	<li><strong>language:</strong> The language the error needs to be associated with. If no language property is provided, a match with regular expression /(Language: \[)[a-z]{2}\]/g is attempted from the message property. As a fallback, it implies that the field is not localized.</li>
 * </ul>
 *
 * The following code is an example of an error response object:
 *
 *      {
 *          errors: [{
 *              subject: 'qualifier1',
 *              message: 'error message for qualifier',
 *              type: 'ValidationError'
 *          }, {
 *              subject: 'qualifier2',
 *              message: 'error message for qualifier2 language: [fr]',
 *              type: 'ValidationError'
 *          }, {
 *              subject: 'qualifier3',
 *              message: 'error message for qualifier2',
 *              type: 'ValidationError'
 *          }, {
 *              subject: 'qualifier4',
 *              message: 'warning message for qualifier4',
 *              type: 'Warning'
 *          }]
 *      }
 *
 *
 * Whenever any sort of dropdown is used in one of the cmsStructureType widgets, it is advised using {@link GenericEditorFactoryService#refreshOptions}. See this method documentation to learn more.
 *
 */
/* @internal */
/* @ngInject */ exports.GenericEditorFactoryService = class /* @ngInject */ GenericEditorFactoryService {
    constructor(restServiceFactory, languageService, sharedDataService, systemEventService, logService, editorFieldMappingService, fetchEnumDataHandler) {
        editorFieldMappingService._registerDefaultFieldMappings();
        this.genericEditorConstructor = class extends DefaultGenericEditor {
            constructor(conf) {
                super(conf, restServiceFactory, languageService, sharedDataService, systemEventService, logService, fetchEnumDataHandler);
            }
        };
    }
    getGenericEditorConstructor() {
        return this.genericEditorConstructor;
    }
};
/* @ngInject */ exports.GenericEditorFactoryService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.RestServiceFactory,
        exports.LanguageService,
        utils.ISharedDataService,
        exports.SystemEventService,
        utils.LogService,
        exports.EditorFieldMappingService,
        exports.FetchEnumDataHandler])
], /* @ngInject */ exports.GenericEditorFactoryService);
class DefaultGenericEditor {
    constructor(conf, restServiceFactory, languageService, sharedDataService, systemEventService, logService, fetchEnumDataHandler) {
        this.restServiceFactory = restServiceFactory;
        this.languageService = languageService;
        this.sharedDataService = sharedDataService;
        this.systemEventService = systemEventService;
        this.logService = logService;
        this.fetchEnumDataHandler = fetchEnumDataHandler;
        this.schema$ = new rxjs.BehaviorSubject(null);
        this.data$ = new rxjs.BehaviorSubject(null);
        this.submitButtonText = 'se.componentform.actions.submit';
        this.cancelButtonText = 'se.componentform.actions.cancel';
        this.api = createApi(this);
        this.onSubmit = (payload) => {
            if (this.smarteditComponentId) {
                payload.identifier = this.smarteditComponentId;
            }
            if (this.editorCRUDService &&
                !this.smarteditComponentId && // if POST mode
                this.structure &&
                this.structure.type && // if we have a type field in the structure, use it for the type in the POST payload
                !payload.type // if the user already provided a type field, lets be nice
            ) {
                payload.type = this.structure.type;
            }
            return this.preparePayload(payload).then((preparedPayload) => {
                const updateOrSaveEditor = () => this.smarteditComponentId
                    ? this.editorCRUDService.update(preparedPayload)
                    : this.editorCRUDService.save(preparedPayload);
                const promise = this.editorCRUDService
                    ? updateOrSaveEditor()
                    : Promise.resolve(preparedPayload);
                return promise.then(function (response) {
                    return {
                        payload,
                        response
                    };
                });
            });
        };
        this.submit$ = () => rxjs.from(this.submit(this.form.component));
        this.onSuccess = (submitResult) => {
            // If we're doing a POST or PUT and the request returns non empty response, then this response is returned.
            // Otherwise the payload for the request is returned.
            let pristine;
            if (submitResult.response) {
                pristine = objectUtils.copy(submitResult.response);
            }
            else {
                pristine = objectUtils.copy(submitResult.payload);
            }
            delete pristine.identifier;
            if (!this.smarteditComponentId && submitResult.response) {
                this.smarteditComponentId = submitResult.response.uuid;
            }
            this.form && this.form.removeValidationMessages();
            this.inProgress = false;
            if (this.updateCallback) {
                // should onSuccess = updateCallback ?
                this.updateCallback(pristine, submitResult.response);
            }
        };
        this.onFailure = (failure) => {
            this.form.removeValidationMessages();
            const errors = failure.error ? failure.error.errors : [];
            this.form.displayValidationMessages(errors, true);
            // send unrelated validation messages to any other listening genericEditor when no other errors
            const unrelatedValidationMessages = this.form.collectUnrelatedValidationMessages(errors);
            if (unrelatedValidationMessages.length > 0) {
                // send tab id in errors for the legacy event.
                this.systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                    messages: unrelatedValidationMessages,
                    sourceGenericEditorId: this.id
                });
            }
            this.inProgress = false;
        };
        this.isSubmitDisabled = () => {
            if (this.inProgress) {
                return true;
            }
            else if (!this.isDirty() || !this.isValid()) {
                return !this.alwaysEnableSubmit;
            }
            else {
                return false;
            }
        };
        this._validate(conf);
        this.id = conf.id;
        this.inProgress = false;
        this.smarteditComponentType = conf.smarteditComponentType;
        this.smarteditComponentId = conf.smarteditComponentId;
        this.editorStackId = conf.editorStackId || this.id;
        this.updateCallback = conf.updateCallback;
        this.structure = conf.structure;
        this.onChangeEvents = [];
        if (conf.structureApi) {
            this.editorStructureService = this.restServiceFactory.get(conf.structureApi);
        }
        this.uriContext = conf.uriContext;
        if (conf.contentApi) {
            this.editorCRUDService = this.restServiceFactory.get(conf.contentApi);
        }
        this.initialContent = lodash__namespace.cloneDeep(conf.content);
        this.initialDirty = false;
        if (conf.customOnSubmit) {
            this.onSubmit = conf.customOnSubmit;
        }
        this._unregisterUnrelatedMessagesEvent = this.systemEventService.subscribe(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, this._handleUnrelatedValidationMessages.bind(this));
        this.element = conf.element;
    }
    get pristine() {
        return this.form ? this.form.pristine : undefined;
    }
    get nativeForm() {
        return this.element.querySelector('form');
    }
    setForm(state) {
        this.form = state;
        this.form.group.valueChanges.subscribe(() => {
            this.api.triggerContentChangeEvents();
        });
    }
    _finalize() {
        this._unregisterUnrelatedMessagesEvent();
        this.popEditorFromStack();
    }
    popEditorFromStack() {
        if (!this.editorStackId) {
            return;
        }
        this.systemEventService.publishAsync(exports.GenericEditorStackService.EDITOR_POP_FROM_STACK_EVENT, {
            editorStackId: this.editorStackId
        });
    }
    /**
     * Sets the content within the editor to its original state.
     */
    reset(pristine) {
        this.form && this.form.removeValidationMessages();
        const structurePromise = this.editorStructureService
            ? this.editorStructureService.get({
                smarteditComponentType: this.smarteditComponentType
            })
            : Promise.resolve(this.structure);
        return this._getUriContext()
            .then((uriContext) => Promise.all([
            this.api.getLanguages() ||
                this.languageService.getLanguagesForSite(uriContext[CONTEXT_SITE_ID]),
            structurePromise.then((structure) => this._convertStructureArray(structure)),
            Promise.resolve(uriContext)
        ]))
            .then(([languages, structure, uriContext]) => {
            const schema = {
                languages,
                structure,
                uriContext,
                id: this.id,
                smarteditComponentType: this.smarteditComponentType,
                targetedQualifier: this.targetedQualifier
            };
            this.schema$.next(schema);
            this.data$.next(pristine ? pristine : this.pristine);
        });
    }
    /**
     *  fetch will:
     *  - return data if initialContent is provided
     *  - make a call to the CRUD API to return the payload if initialContent is not provided
     *
     *  (In initialDirty is set to true, it is populated after loading and setting the content which will make the
     *   pristine and component states out of sync thus making the editor dirty)
     */
    fetch() {
        const fetchPayload = () => this.smarteditComponentId
            ? this.editorCRUDService
                .getById(this.smarteditComponentId)
                .then((response) => response || {})
            : Promise.resolve({});
        if (!this.initialDirty) {
            return this.initialContent ? Promise.resolve(this.initialContent) : fetchPayload();
        }
        return Promise.resolve({});
    }
    load() {
        return this.fetch().then((pristine) => this.reset(pristine), (failure) => {
            this.logService.error('GenericEditor.load failed');
            this.logService.error(failure);
        });
    }
    getComponent() {
        return (this.form && this.form.component) || null;
    }
    /**
     * Transforms the payload before POST/PUT to server
     *
     * @param originalPayload the transformed payload
     */
    preparePayload(originalPayload) {
        return Promise.resolve(this.form.sanitizedPayload(originalPayload));
    }
    /**
     * Saves the content within the form for a specified component. If there are any validation errors returned by the CRUD API after saving the content, it will display the errors.
     */
    submit(newContent) {
        // It's necessary to remove validation errors even if the form is not dirty. This might be because of unrelated validation errors
        // triggered in other tab.
        this.form.removeValidationMessages();
        this.hasFrontEndValidationErrors = false;
        if (!this.form.fieldsAreUserChecked()) {
            this.hasFrontEndValidationErrors = true;
            // Mark this tab as "in error" due to front-end validation.
            return Promise.reject(true);
        }
        else if (this.isValid(true)) {
            this.inProgress = true;
            /*
             * upon submitting, server side may have been updated,
             * since we PUT and not PATCH, we need to take latest of the fields not presented and send them back with the editable ones
             */
            return this.onSubmit(newContent);
        }
        else {
            this.logService.warn('GenericEditor.submit() - unable to submit form. Form is unexpectedly invalid.');
            return Promise.reject({});
        }
    }
    /**
     * Is invoked by HTML field templates that update and manage dropdowns.
     * It updates the dropdown list upon initialization (creates a list of one option) and when performing a search (returns a filtered list).
     * To do this, the GenericEditor fetches an implementation of the `FetchDataHandlerInterface` using the following naming convention:
     * <pre>"fetch" + cmsStructureType + "DataHandler"</pre>
     * @param field The field in the structure that requires a dropdown to be built.
     * @param qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
     * @param search The value of the mask to filter the dropdown entries on.
     */
    refreshOptions(field, qualifier, search) {
        return new Promise((resolve) => {
            let theIdentifier;
            let optionsIdentifier;
            if (field.localized) {
                theIdentifier = this.form.component[field.qualifier][qualifier];
                optionsIdentifier = qualifier;
            }
            else {
                theIdentifier = this.form.component[field.qualifier];
                optionsIdentifier = field.qualifier;
            }
            field.initiated = field.initiated || [];
            field.options = field.options || {};
            if (field.cmsStructureType === 'Enum') {
                field.initiated.push(optionsIdentifier);
            }
            if (field.initiated.indexOf(optionsIdentifier) > -1) {
                if (search.length > 2 || field.cmsStructureType === 'Enum') {
                    return this.fetchEnumDataHandler
                        .findByMask(field, search)
                        .then((entities) => {
                        field.options[optionsIdentifier] = entities;
                        resolve();
                    });
                }
            }
            else if (theIdentifier) {
                return this.fetchEnumDataHandler
                    .getById(field, theIdentifier)
                    .then((entity) => {
                    field.options[optionsIdentifier] = [entity];
                    field.initiated.push(optionsIdentifier);
                    resolve();
                });
            }
            else {
                field.initiated.push(optionsIdentifier);
            }
            return resolve();
        });
    }
    /**
     * A predicate function that returns true if the editor is in dirty state or false if it not.
     * The state of the editor is determined by comparing the current state of the component with the state of the component when it was pristine.
     *
     * @returns An indicator if the editor is in dirty state or not.
     */
    isDirty(qualifier, language) {
        return this.initialDirty || (this.form ? this.form.isDirty(qualifier, language) : false);
    }
    /**
     * Check for html validation errors on all the form fields.
     * If so, assign an error to a field that is not pristine.
     * The seGenericEditorFieldError will render these errors, just like
     * errors we receive from the backend.
     * It also validates error states for tabs.
     */
    isValid(comesFromSubmit = false) {
        if (comesFromSubmit) {
            const validationErrors = this.form.collectFrontEndValidationErrors(comesFromSubmit, this.nativeForm);
            this.form.removeFrontEndValidationMessages();
            this.form.displayValidationMessages(validationErrors, comesFromSubmit);
        }
        return !!this.form && this.form.group.valid;
    }
    watchFormErrors(el) {
        this.form.watchErrors(el);
    }
    init() {
        return this.load().then(() => {
            // If initialDirty is set to true and if any initial content is provided, it is populated here which will make the
            // pristine and component states out of sync thus making the editor dirty
            if (this.initialDirty) {
                this.form.patchComponent(this.initialContent || {});
                this.data$.next(this.initialContent || {});
            }
            this._pushEditorToStack();
            this.systemEventService.publishAsync(GENERIC_EDITOR_LOADED_EVENT, this.id);
        });
    }
    /* @internal */
    _validate(conf) {
        if (stringUtils.isBlank(conf.structureApi) && !conf.structure) {
            throw new Error('genericEditor.configuration.error.no.structure');
        }
        else if (!stringUtils.isBlank(conf.structureApi) && conf.structure) {
            throw new Error('genericEditor.configuration.error.2.structures');
        }
    }
    /* @internal */
    _getUriContext() {
        return Promise.resolve(this.uriContext
            ? this.uriContext
            : this.sharedDataService.get(EXPERIENCE_STORAGE_KEY).then((experience) => ({
                [CONTEXT_SITE_ID]: experience.siteDescriptor.uid,
                [CONTEXT_CATALOG]: experience.catalogDescriptor.catalogId,
                [CONTEXT_CATALOG_VERSION]: experience.catalogDescriptor.catalogVersion
            })));
    }
    /**
     * @internal
     * Conversion function in case the first attribute of the response is an array of type structures.
     */
    _convertStructureArray(structure) {
        const structureArray = structure.structures || structure.componentTypes;
        if (lodash__namespace.isArray(structureArray)) {
            if (structureArray.length > 1) {
                throw new Error('init: Invalid structure, multiple structures returned');
            }
            structure = structureArray[0];
        }
        return structure;
    }
    /* @internal */
    _handleUnrelatedValidationMessages(key, validationData) {
        if (validationData.targetGenericEditorId &&
            validationData.targetGenericEditorId !== this.id) {
            return;
        }
        if (validationData.sourceGenericEditorId &&
            validationData.sourceGenericEditorId === this.id) {
            return;
        }
        this.form.removeValidationMessages();
        this.form.displayValidationMessages(validationData.messages, true);
    }
    /* @internal */
    _pushEditorToStack() {
        this.systemEventService.publishAsync(exports.GenericEditorStackService.EDITOR_PUSH_TO_STACK_EVENT, {
            editorId: this.id,
            editorStackId: this.editorStackId,
            component: this.form.component,
            componentType: this.smarteditComponentType
        });
    }
}

const changeTriggeringInputs = new Set([
    'id',
    'smarteditComponentId',
    'smarteditComponentType',
    'structureApi',
    'structure',
    'contentApi',
    'content',
    'uriContext',
    'updateCallback',
    'customOnSubmit',
    'editorStackId',
    'modalHeaderTitle'
]);
window.__smartedit__.addDecoratorPayload("Component", "GenericEditorComponent", {
    selector: 'se-generic-editor',
    template: `<ng-template let-state [formBuilder]="{schema$: editor.schema$, data$: editor.data$}" (stateCreated)="setFormState($event)"><div class="se-generic-editor"><se-generic-editor-breadcrumb></se-generic-editor-breadcrumb><ng-container *ngIf="state; else noSupport"><form novalidate #nativeForm class="no-enter-submit se-generic-editor__form" [contentManager]="{onSave: editor.submit$}" (onSuccess)="editor.onSuccess($event)" (onError)="editor.onFailure($event)"><div class="modal-header se-generic-editor__header" *ngIf="modalHeaderTitle"><h4 class="modal-title">{{modalHeaderTitle| translate}}</h4></div><div class="se-generic-editor__body"><ng-container [formRenderer]="state.group"></ng-container></div><div class="se-generic-editor__footer dialog-footer" *ngIf="editor && showCommands()"><button fd-button id="submit" name="submit" options="emphasized" *ngIf="showSubmit()" [seSubmitBtn]="editor.isSubmitDisabled" [label]="editor.submitButtonText | translate"></button> <button fd-button id="cancel" type="button" options="light" *ngIf="showCancel()" (click)="_reset()">{{editor.cancelButtonText | translate}}</button></div></form></ng-container><ng-template #noSupport><div class="se-generic-editor__body"><se-message type="info" id="GenericEditor.NoEditingSupportDisclaimer"><ng-container se-message-description>{{ 'se.editor.notification.editing.not.supported' | translate }}</ng-container></se-message></div></ng-template></div></ng-template>`
});
exports.GenericEditorComponent = class GenericEditorComponent {
    constructor(elementRef, genericEditorFactoryService, waitDialogService) {
        this.elementRef = elementRef;
        this.genericEditorFactoryService = genericEditorFactoryService;
        this.waitDialogService = waitDialogService;
        this.submit = null;
        this.reset = null;
        this.isDirty = null;
        this.isValid = null;
        this.getApi = new core.EventEmitter();
        this.submitChange = new core.EventEmitter();
        this.resetChange = new core.EventEmitter();
        this.isValidChange = new core.EventEmitter();
        this.isDirtyChange = new core.EventEmitter();
        this.editor = null;
        this.componentForm = new forms.FormGroup({});
        this.formInitialized$ = new rxjs.BehaviorSubject(null);
        this.formSet$ = new rxjs.BehaviorSubject(false);
        this.isSubmitDisabled = () => this.editor.isSubmitDisabled();
    }
    set nativeForm(element) {
        if (!element) {
            return;
        }
        this.formInitialized$.next(element.nativeElement);
    }
    ngOnChanges(changes) {
        const hasRelevantChange = !!Object.keys(changes).find((key) => changeTriggeringInputs.has(key));
        if (!hasRelevantChange) {
            return;
        }
        if (this.editor) {
            this.editor._finalize();
        }
        const genericEditorConstructor = this.genericEditorFactoryService.getGenericEditorConstructor();
        this.editor = new genericEditorConstructor({
            id: this.id || stringUtils.generateIdentifier(),
            smarteditComponentType: this.smarteditComponentType,
            smarteditComponentId: this.smarteditComponentId,
            editorStackId: this.editorStackId,
            structureApi: this.structureApi,
            structure: this.structure,
            contentApi: this.contentApi,
            updateCallback: this.updateCallback,
            content: this.content,
            uriContext: this.uriContext,
            customOnSubmit: this.customOnSubmit,
            element: this.elementRef.nativeElement
        });
        this.getApi.emit(this.editor.api);
        this.editor.init();
    }
    ngOnInit() {
        this.waitDialogService.hideWaitModal();
        this.showResetButton = lodash.isNull(this.reset);
        this.showSubmitButton = lodash.isNull(this.submit);
        setTimeout(() => {
            this.submitChange.emit(() => this.contentManager
                .save()
                .toPromise()
                .then((result) => result.response));
            this.isValidChange.emit(() => this._isValid());
            this.isDirtyChange.emit(() => this._isDirty());
            this.resetChange.emit(() => this._reset());
        });
        rxjs.combineLatest([this.formSet$, this.formInitialized$])
            .pipe(operators.filter((values) => values.every((value) => !!value)), operators.take(1))
            .subscribe(([_, el]) => this.editor.watchFormErrors(el));
    }
    ngOnDestroy() {
        this.editor._finalize();
        this.waitDialogService.hideWaitModal();
    }
    setFormState(state) {
        this.editor.setForm(state);
        this.formSet$.next(true);
    }
    showCommands() {
        return this.showCancel() || this.showSubmit();
    }
    showCancel() {
        return (this.editor.alwaysShowReset ||
            (this.showResetButton === true && this.editor.isDirty() && this.editor.isValid()));
    }
    showSubmit() {
        return (this.editor.alwaysShowSubmit ||
            (this.showSubmitButton === true && this.editor.isDirty() && this.editor.isValid()));
    }
    _reset() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.editor.onReset && this.editor.onReset() === false) {
                return null;
            }
            return this.editor.reset();
        });
    }
    _isValid() {
        return this.editor.isValid();
    }
    _isDirty() {
        return this.editor ? this.editor.isDirty() : false;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "id", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "smarteditComponentId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "smarteditComponentType", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "structureApi", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorComponent.prototype, "structure", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "contentApi", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorComponent.prototype, "content", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Promise)
], exports.GenericEditorComponent.prototype, "uriContext", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "submit", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "reset", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "isDirty", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "isValid", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "updateCallback", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.GenericEditorComponent.prototype, "customOnSubmit", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "editorStackId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorComponent.prototype, "modalHeaderTitle", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.GenericEditorComponent.prototype, "getApi", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.GenericEditorComponent.prototype, "submitChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.GenericEditorComponent.prototype, "resetChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.GenericEditorComponent.prototype, "isValidChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.GenericEditorComponent.prototype, "isDirtyChange", void 0);
__decorate([
    core.ViewChild('nativeForm', { static: false, read: core.ElementRef }),
    __metadata("design:type", core.ElementRef),
    __metadata("design:paramtypes", [core.ElementRef])
], exports.GenericEditorComponent.prototype, "nativeForm", null);
__decorate([
    core.ViewChild(ContentManager, { static: false }),
    __metadata("design:type", ContentManager)
], exports.GenericEditorComponent.prototype, "contentManager", void 0);
exports.GenericEditorComponent = __decorate([
    core.Component({
        selector: 'se-generic-editor',
        template: `<ng-template let-state [formBuilder]="{schema$: editor.schema$, data$: editor.data$}" (stateCreated)="setFormState($event)"><div class="se-generic-editor"><se-generic-editor-breadcrumb></se-generic-editor-breadcrumb><ng-container *ngIf="state; else noSupport"><form novalidate #nativeForm class="no-enter-submit se-generic-editor__form" [contentManager]="{onSave: editor.submit$}" (onSuccess)="editor.onSuccess($event)" (onError)="editor.onFailure($event)"><div class="modal-header se-generic-editor__header" *ngIf="modalHeaderTitle"><h4 class="modal-title">{{modalHeaderTitle| translate}}</h4></div><div class="se-generic-editor__body"><ng-container [formRenderer]="state.group"></ng-container></div><div class="se-generic-editor__footer dialog-footer" *ngIf="editor && showCommands()"><button fd-button id="submit" name="submit" options="emphasized" *ngIf="showSubmit()" [seSubmitBtn]="editor.isSubmitDisabled" [label]="editor.submitButtonText | translate"></button> <button fd-button id="cancel" type="button" options="light" *ngIf="showCancel()" (click)="_reset()">{{editor.cancelButtonText | translate}}</button></div></form></ng-container><ng-template #noSupport><div class="se-generic-editor__body"><se-message type="info" id="GenericEditor.NoEditingSupportDisclaimer"><ng-container se-message-description>{{ 'se.editor.notification.editing.not.supported' | translate }}</ng-container></se-message></div></ng-template></div></ng-template>`
    }),
    __metadata("design:paramtypes", [core.ElementRef,
        exports.GenericEditorFactoryService,
        IWaitDialogService])
], exports.GenericEditorComponent);

var /* @ngInject */ GenericEditorTabService_1;
/**
 * The genericEditorTabService is used to configure the way in which the tabs in the
 * {@link GenericEditorComponent} component are rendered.
 */
/* @ngInject */ exports.GenericEditorTabService = /* @ngInject */ GenericEditorTabService_1 = class /* @ngInject */ GenericEditorTabService {
    constructor() {
        // --------------------------------------------------------------------------------------
        // Variables
        // --------------------------------------------------------------------------------------
        this._tabsConfiguration = {};
        this._defaultTabPredicates = [];
    }
    // --------------------------------------------------------------------------------------
    // Public Methods
    // --------------------------------------------------------------------------------------
    /**
     * This method stores the configuration of the tab identified by the provided ID.
     *
     * @param tabId The ID of the tab to configure.
     * @param tabConfiguration The object containing the configuration of the tab.
     * @param tabConfiguration.priority The priority of the tab. Higher numbers represent higher priority. This property is used to
     * sort tabs.
     *
     */
    configureTab(tabId, tabConfiguration) {
        this._tabsConfiguration[tabId] = tabConfiguration;
    }
    /**
     * This method retrieves the configuration of a tab.
     *
     * @param tabId The ID of the tab for which to retrieve its configuration.
     * @returns The configuration of the tab. Can be null if no tab with the provided ID has been configured.
     *
     */
    getTabConfiguration(tabId) {
        const result = this._tabsConfiguration[tabId];
        return result ? result : null;
    }
    /**
     * This method sorts in place the list of tabs provided. Sorting starts with tab priority. If two or more tabs have the same priority they
     * will be sorted alphabetically by ID.
     *
     * @param tabsToSort The list of tabs to sort.
     */
    sortTabs(tabsToSort) {
        return tabsToSort.sort((tab1, tab2) => {
            const tab1Priority = this.getTabPriority(tab1);
            const tab2Priority = this.getTabPriority(tab2);
            if (tab2Priority - tab1Priority !== 0) {
                return tab2Priority - tab1Priority; // Sort descending priority
            }
            else {
                // Sort alphabetically
                if (tab1.id < tab2.id) {
                    return -1;
                }
                else if (tab1.id > tab2.id) {
                    return 1;
                }
                return 0;
            }
        });
    }
    // Meant to be used internally. No ng-doc.
    getComponentTypeDefaultTab(componentTypeStructure) {
        let result = null;
        this._defaultTabPredicates.some((predicate) => {
            result = predicate(componentTypeStructure);
            return result !== null;
        });
        return result !== null ? result : /* @ngInject */ GenericEditorTabService_1.DEFAULT_TAB_ID;
    }
    // Meant to be used internally. No ng-doc.
    addComponentTypeDefaultTabPredicate(predicate) {
        if (!predicate || typeof predicate !== 'function') {
            throw new Error('genericEditorTabService - provided predicate must be a function.');
        }
        this._defaultTabPredicates.push(predicate);
    }
    // --------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------
    getTabPriority(tab) {
        const tabId = tab.id;
        if (!tabId) {
            throw new Error('genericEditorTabService - Every tab must have an id.');
        }
        let tabPriority = /* @ngInject */ GenericEditorTabService_1.MIN_PRIORITY;
        if (this._tabsConfiguration[tabId] && this._tabsConfiguration[tabId].priority) {
            tabPriority = this._tabsConfiguration[tabId].priority;
        }
        return tabPriority;
    }
};
// --------------------------------------------------------------------------------------
// Constants
// --------------------------------------------------------------------------------------
/* @ngInject */ exports.GenericEditorTabService.MIN_PRIORITY = 0;
/* @ngInject */ exports.GenericEditorTabService.DEFAULT_TAB_ID = 'default';
/* @ngInject */ exports.GenericEditorTabService = /* @ngInject */ GenericEditorTabService_1 = __decorate([
    core.Injectable()
], /* @ngInject */ exports.GenericEditorTabService);

const parseValidationMessage = (message) => {
    const expression = new RegExp('[a-zA-Z]+: ([|{)([a-zA-Z0-9]+)(]|}).?', 'g');
    const matches = message.match(expression) || [];
    return matches.reduce((messages, match) => {
        messages.message = messages.message.replace(match, '').trim();
        const key = match.split(':')[0].trim().toLowerCase();
        const value = match.split(':')[1].match(/[a-zA-Z0-9]+/g)[0];
        messages[key] = value;
        return messages;
    }, {
        message
    });
};
/**
 * This service provides the functionality to parse validation messages (errors, warnings) received from the backend.
 * This service is used to parse validation messages (errors, warnings) for parameters such as language and format,
 * which are sent as part of the message itself.
 */
/* @ngInject */ exports.SeValidationMessageParser = class /* @ngInject */ SeValidationMessageParser {
    /**
     * Parses extra details, such as language and format, from a validation message (error, warning). These details are also
     * stripped out of the final message. This function expects the message to be in the following format:
     *
     * <pre>
     * const message = "Some validation message occurred. Language: [en]. Format: [widescreen]. SomeKey: [SomeVal]."
     * </pre>
     *
     * The resulting message object is as follows:
     * <pre>
     * {
     *     message: "Some validation message occurred."
     *     language: "en",
     *     format: "widescreen",
     *     somekey: "someval"
     * }
     * </pre>
     */
    parse(message) {
        return parseValidationMessage(message);
    }
};
/* @ngInject */ exports.SeValidationMessageParser = __decorate([
    core.Injectable()
], /* @ngInject */ exports.SeValidationMessageParser);

function isBlankValidator() {
    return (control) => {
        const isBlank = control.value && control.value.length > 0 && '' === lodash__namespace.trim(control.value);
        return isBlank ? { isBlank: { value: control.value } } : null;
    };
}

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorBreadcrumbComponent", {
    selector: 'se-generic-editor-breadcrumb',
    template: `
        <div class="se-ge-breadcrumb">
            <ng-container *ngIf="showBreadcrumb()">
                <div
                    *ngFor="let breadcrumbItem of getEditorsStack(); let i = index"
                    class="se-ge-breadcrumb__item">
                    <div class="se-ge-breadcrumb__data">
                        <span class="se-ge-breadcrumb__title">
                            {{ getComponentName(breadcrumbItem) | translate }}
                        </span>
                        <span class="se-ge-breadcrumb__info">
                            {{ breadcrumbItem.componentType | translate }}
                        </span>
                    </div>

                    <div class="se-ge-breadcrumb__divider" *ngIf="i < getEditorsStack().length - 1">
                        <i class="sap-icon--navigation-right-arrow"></i>
                    </div>
                </div>
            </ng-container>
        </div>
    `
});
/**
 * Component responsible for rendering a breadcrumb on top of the generic editor
 * when there is more than one editor opened on top of each other.
 * This will happen when editing nested components.
 *
 * @param editorStackId The string that identifies the stack of editors being edited together.
 */
exports.GenericEditorBreadcrumbComponent = class GenericEditorBreadcrumbComponent {
    constructor(genericEditorStackService, ge) {
        this.genericEditorStackService = genericEditorStackService;
        this.ge = ge;
    }
    getEditorsStack() {
        if (!this.editorsStack) {
            this.editorsStack = this.genericEditorStackService.getEditorsStack(this.ge.editorStackId);
        }
        return this.editorsStack;
    }
    showBreadcrumb() {
        return this.getEditorsStack() && this.getEditorsStack().length > 1;
    }
    getComponentName(breadcrumbItem) {
        if (!breadcrumbItem.component.name) {
            return 'se.breadcrumb.name.empty';
        }
        return breadcrumbItem.component.name;
    }
};
exports.GenericEditorBreadcrumbComponent = __decorate([
    core.Component({
        selector: 'se-generic-editor-breadcrumb',
        template: `
        <div class="se-ge-breadcrumb">
            <ng-container *ngIf="showBreadcrumb()">
                <div
                    *ngFor="let breadcrumbItem of getEditorsStack(); let i = index"
                    class="se-ge-breadcrumb__item">
                    <div class="se-ge-breadcrumb__data">
                        <span class="se-ge-breadcrumb__title">
                            {{ getComponentName(breadcrumbItem) | translate }}
                        </span>
                        <span class="se-ge-breadcrumb__info">
                            {{ breadcrumbItem.componentType | translate }}
                        </span>
                    </div>

                    <div class="se-ge-breadcrumb__divider" *ngIf="i < getEditorsStack().length - 1">
                        <i class="sap-icon--navigation-right-arrow"></i>
                    </div>
                </div>
            </ng-container>
        </div>
    `
    }),
    __param(1, core.Inject(core.forwardRef(() => exports.GenericEditorComponent))),
    __metadata("design:paramtypes", [exports.GenericEditorStackService,
        exports.GenericEditorComponent])
], exports.GenericEditorBreadcrumbComponent);

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorDynamicFieldComponent", {
    selector: 'se-ge-dynamic-field',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    template: `
        <se-generic-editor-field
            [formControl]="form"
            [field]="field"
            [model]="component"
            [qualifier]="qualifier"
            [id]="id"></se-generic-editor-field>
    `
});
/**
 * TODO: Some parts of the generic editor field can be moved up to this component.
 * and then we could dynamic inject which form we should put the control in.
 */
let GenericEditorDynamicFieldComponent = class GenericEditorDynamicFieldComponent {
    constructor(cdr) {
        this.cdr = cdr;
    }
    ngAfterViewChecked() {
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
};
__decorate([
    utils.DynamicForm(),
    __metadata("design:type", utils.FormField)
], GenericEditorDynamicFieldComponent.prototype, "form", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Object)
], GenericEditorDynamicFieldComponent.prototype, "component", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Object)
], GenericEditorDynamicFieldComponent.prototype, "field", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", String)
], GenericEditorDynamicFieldComponent.prototype, "qualifier", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", String)
], GenericEditorDynamicFieldComponent.prototype, "id", void 0);
GenericEditorDynamicFieldComponent = __decorate([
    core.Component({
        selector: 'se-ge-dynamic-field',
        styles: [
            `
            :host {
                display: block;
            }
        `
        ],
        template: `
        <se-generic-editor-field
            [formControl]="form"
            [field]="field"
            [model]="component"
            [qualifier]="qualifier"
            [id]="id"></se-generic-editor-field>
    `
    }),
    __metadata("design:paramtypes", [core.ChangeDetectorRef])
], GenericEditorDynamicFieldComponent);

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorTabComponent", {
    selector: 'se-ge-tab',
    template: `<div class="se-generic-editor__row ySErow" *ngFor="let field of fields" [ngClass]="'se-generic-editor__row--' + field.qualifier"><label [id]="field.qualifier + '-label'" class="se-control-label" *ngIf="!field.hideFieldWidget && !field.hidePrefixLabel && field.i18nKey" [hidden]="field.hideFieldWidget && field.hidePrefixLabel" [ngClass]="{ required: field.required }">{{ field.i18nKey | lowercase | translate }}</label><se-help class="se-help-tooltip" *ngIf="field.tooltip"><span>{{ field.tooltip | translate }}</span></se-help><div class="ySEGenericEditorFieldStructure" [hidden]="field.hideFieldWidget" [id]="field.qualifier" [attr.data-cms-field-qualifier]="field.qualifier" [attr.data-cms-structure-type]="field.cmsStructureType"><ng-template [formRenderer]="getForm(field.qualifier)"></ng-template></div></div>`
});
let GenericEditorTabComponent = class GenericEditorTabComponent {
    constructor(cdr, ge, data) {
        this.cdr = cdr;
        this.ge = ge;
        this.data = data;
    }
    ngOnInit() {
        const { model: master, tabId } = this.data;
        this.fields = master.getInput('fieldsMap')[tabId];
        const group = master.controls[tabId];
        this.forms = group.controls;
        this._subscription = group.statusChanges.subscribe(() => {
            const hasErrorMessages = lodash.values(this.forms).some((form) => {
                const field = form.getInput('field');
                return field.messages && field.messages.length > 0;
            });
            this.data.tab.hasErrors = hasErrorMessages;
        });
        Object.keys(this.forms).forEach((key) => {
            this.forms[key].setInput('id', this.ge.editor.id);
        });
    }
    ngAfterViewChecked() {
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    ngOnDestroy() {
        this._subscription.unsubscribe();
    }
    getForm(id) {
        return this.forms[id];
    }
};
GenericEditorTabComponent = __decorate([
    core.Component({
        selector: 'se-ge-tab',
        template: `<div class="se-generic-editor__row ySErow" *ngFor="let field of fields" [ngClass]="'se-generic-editor__row--' + field.qualifier"><label [id]="field.qualifier + '-label'" class="se-control-label" *ngIf="!field.hideFieldWidget && !field.hidePrefixLabel && field.i18nKey" [hidden]="field.hideFieldWidget && field.hidePrefixLabel" [ngClass]="{ required: field.required }">{{ field.i18nKey | lowercase | translate }}</label><se-help class="se-help-tooltip" *ngIf="field.tooltip"><span>{{ field.tooltip | translate }}</span></se-help><div class="ySEGenericEditorFieldStructure" [hidden]="field.hideFieldWidget" [id]="field.qualifier" [attr.data-cms-field-qualifier]="field.qualifier" [attr.data-cms-structure-type]="field.cmsStructureType"><ng-template [formRenderer]="getForm(field.qualifier)"></ng-template></div></div>`
    }),
    __param(2, core.Inject(TAB_DATA)),
    __metadata("design:paramtypes", [core.ChangeDetectorRef,
        exports.GenericEditorComponent, Object])
], GenericEditorTabComponent);

const createValidatorMap = (validators, id, structure, required, component) => Object.keys(validators || {}).reduce((acc, item) => (Object.assign(Object.assign({}, acc), { [item]: validators[item](id, structure, required, component) })), {});
/**
 * A schema and data mapper for the GenericEditorRootTabsComponent.
 */
class RootSchemaDataMapper {
    constructor(mappers, tabs, fieldsMap) {
        this.mappers = mappers;
        this.tabs = tabs;
        this.fieldsMap = fieldsMap;
    }
    toValue() {
        return this.mappers.reduce((acc, mapper) => {
            acc[mapper.id] = mapper.toValue();
            return acc;
        }, {});
    }
    toSchema() {
        return {
            type: 'group',
            component: 'tabs',
            schemas: this.mappers.reduce((acc, mapper) => {
                acc[mapper.id] = mapper.toSchema();
                return acc;
            }, {}),
            inputs: {
                tabs: this.tabs,
                fieldsMap: this.fieldsMap
            }
        };
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @internal
 */
const INTERNAL_PROP_NAME = '$$internal';
/**
 * @internal
 * Adds an internal property on the component model for
 * watching property changes on an object.
 */
class InternalProperty {
    constructor() {
        this._map = new Map();
    }
    /**
     * Watch property changes.
     */
    watch(property, fn) {
        this._map.set(property, fn);
        return () => {
            this._map.delete(property);
        };
    }
    /**
     * Trigger prop change.
     *
     * @param property
     * @param value New value
     */
    trigger(property, value) {
        if (this._map.has(property)) {
            this._map.get(property)(value);
        }
    }
}
/**
 * @internal
 * Creates a proxied object to listen on property changes
 * for backwards compatibility with object mutations made by
 * widgets. This is used to proxy the model data called component in the
 * generic editor. The component data is the model that is
 * used for submitting to the backend. Old widgets mutate the
 * properties of component object, thus there is not way to
 * listen on properties changes except for the use of the ES6 Proxy
 * API. Some properties that are watched inside of the GenericEditorField
 * update the value of the AbstractForm of Angular used for validation.
 *
 * NOTE:
 * This function uses Proxy which is not supported in IE.
 */
const proxifyDataObject = (obj) => {
    const internal = new InternalProperty();
    Object.defineProperty(obj, INTERNAL_PROP_NAME, {
        get: () => internal
    });
    return new Proxy(obj, {
        set(target, prop, value) {
            target[prop] = value;
            target[INTERNAL_PROP_NAME].trigger(prop, value);
            return true;
        }
    });
};

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const VALIDATION_MESSAGE_TYPES_SET = new Set(lodash__namespace.values(VALIDATION_MESSAGE_TYPES));
const CMS_STRUCTURE_TYPE = {
    SHORT_STRING: 'ShortString',
    LONG_STRING: 'LongString'
};
/**
 * @internal
 * Holds the entire state of the generic editor.
 * Provides method to query and mutate the generic editor state.
 * The GenericEditorState is created by the GenericEditorStateBuilderService.
 */
class GenericEditorState {
    constructor(id, group, component, proxiedComponent, pristine, tabs, fields, languages, parameters) {
        this.id = id;
        this.group = group;
        this.component = component;
        this.proxiedComponent = proxiedComponent;
        this.pristine = pristine;
        this.tabs = tabs;
        this.fields = fields;
        this.languages = languages;
        this.parameters = parameters;
        /**
         * Removes all validation (local, outside or server) errors from fieds and tabs.
         */
        this.removeValidationMessages = () => {
            this.fields.forEach((field) => {
                field.messages = undefined;
                field.hasErrors = false;
                field.hasWarnings = false;
            });
        };
        this._qualifierFieldMap = this.fields.reduce((acc, field) => {
            acc.set(field.qualifier, field);
            return acc;
        }, new Map());
        this._formFields = this._buildFormFieldsArray(this.group);
    }
    /**
     * Removes validation errors generated in frontend, not the ones sent by outside or server.
     * Removes errors only from fields, not tabs.
     */
    removeFrontEndValidationMessages() {
        this.fields.forEach((field) => {
            if (!Array.isArray(field.messages)) {
                return;
            }
            const messages = (field.messages || []).filter((message) => message.fromSubmit === undefined ? true : message.fromSubmit);
            field.messages = messages.length ? messages : undefined;
            field.hasErrors = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
            field.hasWarnings = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.WARNING);
        });
    }
    /**
     * Checks if this validation message belongs to the current generic editor by seeing if the generic editor
     * has the qualifier.
     *
     * TODO: It assumes that the qualifier is unique in every genericeditor.
     *
     * @param {GenericEditorFieldMessage} validationMessage
     * @return {boolean}
     */
    validationMessageBelongsToCurrentInstance(validationMessage) {
        return this._qualifierFieldMap.has(validationMessage.subject);
    }
    /**
     * @param {GenericEditorFieldMessage[]} messages
     * @return {GenericEditorFieldMessage[]}
     */
    collectUnrelatedValidationMessages(messages) {
        return messages.filter((message) => this._isValidationMessageType(message.type) &&
            !this.validationMessageBelongsToCurrentInstance(message));
    }
    /**
     * Collects validation errors on all the form fields.
     * Returns the list of errors or empty list.
     * Each error contains the following properties:
     * type - VALIDATION_MESSAGE_TYPES
     * subject - the field qualifier.
     * message - error message.
     * fromSubmit - contains true if the error is related to submit operation, false otherwise.
     * isNonPristine - contains true if the field was modified (at least once) by the user, false otherwise.
     * language - optional language iso code.
     */
    watchErrors(formElement) {
        const formChangeStream = this._formFields.map((form) => form.statusChanges.pipe(operators.startWith(null)));
        rxjs.combineLatest([...formChangeStream, this.group.statusChanges.pipe(operators.startWith(null))])
            .pipe(operators.distinctUntilChanged((prev, curr) => lodash__namespace.isEqual(prev, curr)), operators.map(() => this.collectFrontEndValidationErrors(false, formElement)))
            .subscribe((messages) => {
            this.removeFrontEndValidationMessages();
            this.displayValidationMessages(messages, false);
        });
    }
    collectFrontEndValidationErrors(comesFromSubmit, formElement) {
        comesFromSubmit = comesFromSubmit || false;
        return this._formFields.reduce((acc, form) => {
            const field = form.getInput('field');
            if ((() => {
                const fieldNativeElement = Array.from(formElement || []).find((elem) => elem.getAttribute('name') === field.qualifier);
                return !!fieldNativeElement && !fieldNativeElement.checkValidity();
            })()) {
                acc.push({
                    type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                    subject: field.qualifier,
                    message: 'se.editor.htmlo.validation.error',
                    fromSubmit: comesFromSubmit,
                    isNonPristine: this.isDirty(field.qualifier)
                });
            }
            // Could get more specific errors.
            if ((form.getError('required') && form.touched) ||
                (form.getError('isBlank') && form.touched)) {
                if (field.localized) {
                    const id = form.getInput('id');
                    acc.push({
                        type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                        subject: field.qualifier,
                        message: 'se.componentform.required.field',
                        language: id,
                        fromSubmit: comesFromSubmit,
                        isNonPristine: this.isDirty(field.qualifier, id // Isocode
                        )
                    });
                }
                else {
                    acc.push({
                        type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                        subject: field.qualifier,
                        message: 'se.componentform.required.field',
                        fromSubmit: comesFromSubmit,
                        isNonPristine: this.isDirty(field.qualifier)
                    });
                }
            }
            return acc;
        }, []);
    }
    /**
     * Displays validation errors for fields and changes error states for all tabs.
     * TODO: move validation to fields.
     */
    displayValidationMessages(validationMessages, keepAllErrors) {
        validationMessages
            .filter((message) => this._isValidationMessageType(message.type) &&
            (keepAllErrors || message.isNonPristine))
            .forEach((validation) => {
            validation.type = validation.type || VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR;
            const field = this._qualifierFieldMap.get(validation.subject);
            if (!field) {
                return;
            }
            if (!field.messages) {
                field.messages = [];
            }
            const message = lodash__namespace.merge(validation, this._getParseValidationMessage(validation.message));
            message.marker = field.localized ? message.language : field.qualifier;
            message.type = validation.type;
            message.uniqId = stringUtils.encode(message);
            const existing = field.messages.find((msg) => msg.uniqId === message.uniqId);
            if (!existing) {
                field.messages.push(message);
                if (message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR) {
                    field.hasErrors = true;
                }
                else if (message.type === VALIDATION_MESSAGE_TYPES.WARNING) {
                    field.hasWarnings = true;
                }
            }
            else {
                // Update existing message.
                lodash__namespace.merge(existing, message);
            }
        });
        /**
         * Need to trigger onStatusChanges for each tab because these messages are added after
         * validation is triggered inside of AbstractFormControls. Messages will dictate for now
         * if a field is invalid.
         */
        lodash__namespace.values(this.group.controls).forEach((tab) => {
            tab.updateValueAndValidity({ emitEvent: true });
        });
        return Promise.resolve();
    }
    isDirty(qualifier, language) {
        this._bcPristine = this._buildComparable(this.fields, this.pristine);
        const bcComponent = this._buildComparable(this.fields, this.component);
        const subPristine = qualifier
            ? language
                ? this._bcPristine[qualifier][language]
                : this._bcPristine[qualifier]
            : this._bcPristine;
        const subComponent = qualifier
            ? language
                ? bcComponent[qualifier][language]
                : bcComponent[qualifier]
            : bcComponent;
        return !lodash__namespace.isEqual(subPristine, subComponent);
    }
    fieldsAreUserChecked() {
        return this.fields.every((field) => {
            let requiresUserCheck = false;
            for (const qualifier in field.requiresUserCheck) {
                if (field.requiresUserCheck.hasOwnProperty(qualifier)) {
                    requiresUserCheck = requiresUserCheck || field.requiresUserCheck[qualifier];
                }
            }
            return !requiresUserCheck || field.isUserChecked;
        });
    }
    /**
     * Updates the component with the patching component.
     */
    patchComponent(value) {
        // Proxify the localized objects.
        this._qualifierFieldMap.forEach(({ localized, qualifier }) => {
            if (localized && value[qualifier]) {
                value[qualifier] = proxifyDataObject(value[qualifier]);
            }
        });
        Object.assign(this.proxiedComponent, value);
    }
    /**
     * Get sanitized payload to be sent to the backend.
     *
     * **Deprecated since 2105. It will be removed in next release.**
     *
     * @deprecated
     */
    sanitizedPayload(payload = this.component) {
        this.fields
            .filter((field) => field.cmsStructureType === CMS_STRUCTURE_TYPE.LONG_STRING ||
            field.cmsStructureType === CMS_STRUCTURE_TYPE.SHORT_STRING ||
            typeof field.customSanitize === 'function')
            .forEach(({ qualifier, localized, customSanitize }) => {
            if (payload[qualifier] !== undefined && qualifier in payload) {
                if (customSanitize) {
                    payload[qualifier] = customSanitize(payload[qualifier], stringUtils.sanitize);
                    return;
                }
            }
        });
        return payload;
    }
    /*
     * Switches to tab with qualifier.
     * Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     */
    switchToTabContainingQualifier(targetedQualifier) {
        if (!targetedQualifier) {
            return;
        }
        this.tabs.forEach((tab) => {
            tab.active = !!this.group.get([tab.id, targetedQualifier]);
        });
    }
    _getParseValidationMessage(message) {
        return parseValidationMessage(message);
    }
    /**
     * @internal
     * Sees if it contains validation message type.
     */
    _containsValidationMessageType(validationMessages, messageType) {
        if (!Array.isArray(validationMessages)) {
            return false;
        }
        return validationMessages.some((message) => message.type === messageType &&
            this.validationMessageBelongsToCurrentInstance(message));
    }
    /**
     * @internal
     * Checks if validation message type is of type ValidationError or Warning.
     */
    _isValidationMessageType(messageType) {
        return VALIDATION_MESSAGE_TYPES_SET.has(messageType);
    }
    /**
     * @internal
     * Builds a comparable data object.
     */
    _buildComparable(fields, source) {
        if (!source) {
            return source;
        }
        const comparable = {};
        fields.forEach((field) => {
            let fieldValue = source[field.qualifier];
            if (field.localized) {
                fieldValue = fieldValue;
                const sub = {};
                lodash__namespace.forEach(fieldValue, (langValue, lang) => {
                    if (!lodash__namespace.isUndefined(langValue)) {
                        sub[lang] = this._buildFieldComparable(langValue, field);
                    }
                });
                comparable[field.qualifier] = sub;
            }
            else {
                fieldValue = source[field.qualifier];
                comparable[field.qualifier] = this._buildFieldComparable(fieldValue, field);
            }
        });
        // sometimes, such as in navigationNodeEntryEditor, we update properties not part of the fields and still want the editor to turn dirty
        lodash__namespace.forEach(source, (value, key) => {
            const notDisplayed = !fields.some((field) => field.qualifier === key);
            if (notDisplayed) {
                comparable[key] = value;
            }
        });
        return lodash__namespace.omitBy(comparable, lodash__namespace.isUndefined);
    }
    /**
     * @internal
     */
    _buildFieldComparable(fieldValue, field) {
        switch (field.cmsStructureType) {
            case 'RichText':
                return fieldValue !== undefined ? stringUtils.sanitizeHTML(fieldValue) : null;
            case 'Boolean':
                return fieldValue !== undefined ? fieldValue : false;
            default:
                return fieldValue;
        }
    }
    /**
     * @internal
     * Get all leaf nodes of the form.
     */
    _buildFormFieldsArray(form, array = []) {
        if (form instanceof utils.FormField) {
            array.push(form);
            return array;
        }
        if (form instanceof utils.FormGrouping) {
            Object.keys(form.controls).forEach((key) => {
                const field = form.controls[key];
                this._buildFormFieldsArray(field, array);
            });
            return array;
        }
        return array;
    }
}

/**
 * A schema and data mapper for the GenericEditorDynamicFieldComponent.
 */
class FieldSchemaDataMapper {
    constructor(id, structure, required, component) {
        this.id = id;
        this.structure = structure;
        this.required = required;
        this.component = component;
    }
    toValue() {
        return this.component[this.id];
    }
    toSchema() {
        return {
            type: 'field',
            component: 'field',
            validators: Object.assign({ required: (this.required && this.structure.editable) || undefined }, createValidatorMap(this.structure.validators, this.id, this.structure, this.required, this.component)),
            inputs: {
                id: this.id,
                field: this.structure,
                qualifier: this.id,
                component: this.component
            }
        };
    }
}

/**
 * A schema and data mapper for the LocalizedElementComponent.
 */
class LocalizedSchemaDataMapper {
    constructor(id, mappers, structure, languages, component) {
        this.id = id;
        this.mappers = mappers;
        this.structure = structure;
        this.languages = languages;
        this.component = component;
    }
    toValue() {
        return this.mappers.reduce((acc, mapper) => {
            acc[mapper.id] = mapper.toValue();
            return acc;
        }, {});
    }
    toSchema() {
        return {
            type: 'group',
            component: 'localized',
            schemas: this.mappers.reduce((acc, mapper) => {
                acc[mapper.id] = mapper.toSchema();
                return acc;
            }, {}),
            inputs: {
                field: this.structure,
                languages: this.languages,
                component: this.component
            }
        };
    }
}

/**
 * A schema and data mapper for the GenericEditorTabComponent.
 */
class TabSchemaDataMapper {
    constructor(id, mappers) {
        this.id = id;
        this.mappers = mappers;
    }
    toValue() {
        return this.mappers.reduce((acc, field) => {
            acc[field.id] = field.toValue();
            return acc;
        }, {});
    }
    toSchema() {
        return {
            type: 'group',
            persist: false,
            schemas: this.mappers.reduce((acc, field) => {
                acc[field.id] = field.toSchema();
                return acc;
            }, {})
        };
    }
}

const createFieldMapper = (qualifier, field, required, component) => new FieldSchemaDataMapper(qualifier, field, required, component);
const createLocalizedMapper = (field, languages, component) => {
    if (component[field.qualifier] === undefined) {
        component[field.qualifier] = {};
    }
    component[field.qualifier] = proxifyDataObject(component[field.qualifier]);
    const localMappers = languages.map(({ isocode, required }) => createFieldMapper(isocode, field, field.required && required, component[field.qualifier]));
    return new LocalizedSchemaDataMapper(field.qualifier, localMappers, field, languages, component);
};
const createTabMapper = (id, fields, languages, component) => {
    const fieldMappers = fields.map((field) => {
        if (field.localized) {
            return createLocalizedMapper(field, languages, component);
        }
        return createFieldMapper(field.qualifier, field, field.required, component);
    });
    return new TabSchemaDataMapper(id, fieldMappers);
};
/**
 * @internal
 * The createRootMapper is an entry factory to creating the RootSchemaDataMapper and
 * the subsequent the nested mappers for tabs, localized fields, and dynamic fields components.
 * The returning instance is of type RootSchemaDataMapper which contains
 * two methods for building the data and schema object that will be passed
 * to the form builder's schema compiler service to build the FormGrouping.
 *
 * @param {GenericEditorFieldsMap} fieldsMap
 * @param {Payload} component
 * @param {ILanguage[]} languages
 * @param {GenericEditorTab[]} tabs
 * @return {RootSchemaDataMapper} A mapper for building data and schema for it to be
 * consumed by the SchemaCompilerService in the FormBuilder module.
 */
const createRootMapper = (fieldsMap, component, languages, tabs) => {
    const rootMappers = lodash.toPairs(fieldsMap).map(([id, fields]) => createTabMapper(id, fields, languages, component));
    return new RootSchemaDataMapper(rootMappers, tabs, fieldsMap);
};

/**
 * @internal
 * GenericEditorStateBuilderService generates a GenericEditorState.
 */
let /* @ngInject */ GenericEditorStateBuilderService = class /* @ngInject */ GenericEditorStateBuilderService {
    constructor(
    /** @internal */
    editorFieldMappingService, 
    /** @internal */
    genericEditorTabService, 
    /** @internal */
    translateService, 
    /** @internal */
    schemaCompiler) {
        this.editorFieldMappingService = editorFieldMappingService;
        this.genericEditorTabService = genericEditorTabService;
        this.translateService = translateService;
        this.schemaCompiler = schemaCompiler;
    }
    /**
     * Compiles a GenericEditorState from schema and data. Whenever a new state
     * is provided the entire form is recompiled.
     */
    buildState(data, schema) {
        const fields = this._fieldAdaptor(schema);
        const tabs = [];
        const fieldsMap = fields.reduce((seed, field) => {
            let tab = this.editorFieldMappingService.getFieldTabMapping(field, schema.structure);
            if (!tab) {
                tab = this.genericEditorTabService.getComponentTypeDefaultTab(schema.structure);
            }
            if (!seed[tab]) {
                seed[tab] = [];
                tabs.push({
                    id: tab,
                    title: 'se.genericeditor.tab.' + tab + '.title',
                    component: GenericEditorTabComponent
                });
            }
            seed[tab].push(field);
            return seed;
        }, {});
        this.genericEditorTabService.sortTabs(tabs);
        // for setting uri params into custom widgets
        const parameters = {
            siteId: schema.uriContext[CONTEXT_SITE_ID],
            catalogId: schema.uriContext[CONTEXT_CATALOG],
            catalogVersion: schema.uriContext[CONTEXT_CATALOG_VERSION]
        };
        const component = objectUtils.copy(data);
        const proxied = proxifyDataObject(component);
        const mapper = this._createMapper(fieldsMap, proxied, schema.languages, tabs);
        const form = this.schemaCompiler.compileGroup(mapper.toValue(), mapper.toSchema());
        // Create central state handler.
        const state = new GenericEditorState(schema.id, form, component, proxied, objectUtils.copy(data), // Pristine form.
        tabs, fields, schema.languages, parameters);
        state.switchToTabContainingQualifier(schema.targetedQualifier);
        return state;
    }
    /** @internal */
    _createMapper(fieldsMap, component, languages, tabs) {
        return createRootMapper(fieldsMap, component, languages, tabs);
    }
    /** @internal */
    _fieldAdaptor(schema) {
        const structure = schema.structure;
        return structure.attributes.map((field) => {
            const fieldMapping = this.editorFieldMappingService.getEditorFieldMapping(field, structure);
            const genericField = Object.assign(field, fieldMapping);
            if (genericField.editable === undefined) {
                genericField.editable = true;
            }
            if (!genericField.postfixText) {
                const key = (structure.type ? structure.type.toLowerCase() : '') +
                    '.' +
                    field.qualifier.toLowerCase() +
                    '.postfix.text';
                const translated = this.translateService.instant(key);
                genericField.postfixText = translated !== key ? translated : '';
            }
            genericField.smarteditComponentType = schema.smarteditComponentType;
            return genericField;
        });
    }
};
/* @ngInject */ GenericEditorStateBuilderService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.EditorFieldMappingService,
        exports.GenericEditorTabService,
        core$1.TranslateService,
        utils.SchemaCompilerService])
], /* @ngInject */ GenericEditorStateBuilderService);

let FormBuilderDirective = class FormBuilderDirective {
    constructor(templateRef, viewContainer, stateBuilderService) {
        this.templateRef = templateRef;
        this.viewContainer = viewContainer;
        this.stateBuilderService = stateBuilderService;
        this.stateCreated = new core.EventEmitter();
    }
    set formBuilder(input) {
        this._dispose();
        this._subscription = rxjs.combineLatest([input.data$, input.schema$]).subscribe(this._onDataStream.bind(this));
    }
    ngOnDestroy() {
        this._dispose();
    }
    _onDataStream([data, schema]) {
        if (!data || !schema) {
            return;
        }
        /**
         * Destroys all views and recreate the embeddedview.
         */
        this.viewContainer.clear();
        /**
         * No form supported.
         */
        if (!schema.structure) {
            this.viewContainer.createEmbeddedView(this.templateRef, {
                $implicit: null
            });
            return;
        }
        /**
         * Build state, and emit state.
         */
        const state = this.stateBuilderService.buildState(data, schema);
        this.stateCreated.emit(state);
        this.viewContainer.createEmbeddedView(this.templateRef, {
            $implicit: state
        });
    }
    /**
     * @internal
     * Removes subscription and destroyes all views.
     */
    _dispose() {
        this._subscription && this._subscription.unsubscribe();
        this.viewContainer.clear();
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object),
    __metadata("design:paramtypes", [Object])
], FormBuilderDirective.prototype, "formBuilder", null);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], FormBuilderDirective.prototype, "stateCreated", void 0);
FormBuilderDirective = __decorate([
    core.Directive({ selector: '[formBuilder]' }),
    __metadata("design:paramtypes", [core.TemplateRef,
        core.ViewContainerRef,
        GenericEditorStateBuilderService])
], FormBuilderDirective);

var GenericEditorFieldComponent_1;
window.__smartedit__.addDecoratorPayload("Component", "GenericEditorFieldComponent", {
    selector: 'se-generic-editor-field',
    providers: [
        {
            provide: forms.NG_VALUE_ACCESSOR,
            useExisting: core.forwardRef(() => GenericEditorFieldComponent_1),
            multi: true
        }
    ],
    template: `<div [attr.validation-id]="field.qualifier" class="ySEField"><ng-container *ngIf="field.component"><ng-container *ngComponentOutlet="field.component; injector: widgetInjector"></ng-container></ng-container><se-generic-editor-field-messages [field]="field" [qualifier]="qualifier"></se-generic-editor-field-messages><div *ngIf="field.postfix" class="ySEText ySEFieldPostfix">{{ field.postfix | translate }}</div></div>`
});
exports.GenericEditorFieldComponent = GenericEditorFieldComponent_1 = class GenericEditorFieldComponent {
    constructor(elementRef, injector, ge) {
        this.elementRef = elementRef;
        this.injector = injector;
        this.ge = ge;
    }
    writeValue(value) {
        this.model[this.qualifier] = value;
    }
    registerOnChange(fn) {
        this._onChange = fn;
    }
    registerOnTouched(fn) {
        this._onTouched = fn;
    }
    ngOnInit() {
        this.createInjector();
        this._unWatch = this.model[INTERNAL_PROP_NAME].watch(this.qualifier, (value) => {
            if (this._onChange && this._onTouched) {
                this._onTouched();
                this._onChange(value);
            }
        });
    }
    ngOnDestroy() {
        this._unWatch();
    }
    ngAfterViewInit() {
        if (!this.field.template) {
            return;
        }
        const element = document.createElement('se-template-ge-widget');
        this.elementRef.nativeElement.editor = this.ge.editor;
        this.elementRef.nativeElement.model = this.model;
        this.elementRef.nativeElement.field = this.field;
        this.elementRef.nativeElement.qualifier = this.qualifier;
        this.elementRef.nativeElement.id = this.id;
        this.elementRef.nativeElement.editorStackId = this.ge.editor.editorStackId;
        this.elementRef.nativeElement.isFieldDisabled = () => this.isFieldDisabled();
        this.elementRef.nativeElement.$ctrl = this;
        this.geWidget.nativeElement.appendChild(element);
    }
    isFieldDisabled() {
        let isEnabled = this.field.editable;
        if (this.field.localized) {
            isEnabled = this.field.editable && this.field.isLanguageEnabledMap[this.qualifier];
        }
        return !isEnabled;
    }
    createInjector() {
        this.widgetInjector = core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: GENERIC_EDITOR_WIDGET_DATA,
                    useValue: {
                        id: this.id,
                        field: this.field,
                        model: this.model,
                        editor: this.ge.editor,
                        qualifier: this.qualifier,
                        isFieldDisabled: () => this.isFieldDisabled()
                    }
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorFieldComponent.prototype, "field", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.GenericEditorFieldComponent.prototype, "model", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorFieldComponent.prototype, "qualifier", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.GenericEditorFieldComponent.prototype, "id", void 0);
__decorate([
    core.ViewChild('widget', { read: core.ElementRef, static: false }),
    __metadata("design:type", core.ElementRef)
], exports.GenericEditorFieldComponent.prototype, "geWidget", void 0);
exports.GenericEditorFieldComponent = GenericEditorFieldComponent_1 = __decorate([
    core.Component({
        selector: 'se-generic-editor-field',
        providers: [
            {
                provide: forms.NG_VALUE_ACCESSOR,
                useExisting: core.forwardRef(() => GenericEditorFieldComponent_1),
                multi: true
            }
        ],
        template: `<div [attr.validation-id]="field.qualifier" class="ySEField"><ng-container *ngIf="field.component"><ng-container *ngComponentOutlet="field.component; injector: widgetInjector"></ng-container></ng-container><se-generic-editor-field-messages [field]="field" [qualifier]="qualifier"></se-generic-editor-field-messages><div *ngIf="field.postfix" class="ySEText ySEFieldPostfix">{{ field.postfix | translate }}</div></div>`
    }),
    __param(2, core.Inject(core.forwardRef(() => exports.GenericEditorComponent))),
    __metadata("design:paramtypes", [core.ElementRef,
        core.Injector,
        exports.GenericEditorComponent])
], exports.GenericEditorFieldComponent);

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorFieldMessagesComponent", {
    selector: 'se-generic-editor-field-messages',
    template: `<div *ngIf="errors?.length > 0" style="display: grid"><span *ngFor="let error of errors" class="se-generic-editor__error se-help-block--has-error help-block fd-form__message fd-form__message--error">{{ error | translate}}</span></div><div *ngIf="warnings?.length > 0" style="display: grid"><span *ngFor="let warning of warnings" class="se-generic-editor__warning se-help-block--has-warning help-block fd-form__message fd-form__message--warning">{{ warning | translate }}</span></div>`
});
let GenericEditorFieldMessagesComponent = class GenericEditorFieldMessagesComponent {
    constructor() {
        this.previousMessages = null;
    }
    ngDoCheck() {
        if (this.field) {
            const currentMessages = JSON.stringify(this.field.messages);
            if (this.previousMessages !== currentMessages) {
                this.previousMessages = currentMessages;
                this.errors = this.getFilteredMessagesByType(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
                this.warnings = this.getFilteredMessagesByType(VALIDATION_MESSAGE_TYPES.WARNING);
            }
        }
    }
    getFilteredMessagesByType(messageType) {
        return (this.field.messages || [])
            .filter((validationMessage) => validationMessage.marker === this.qualifier &&
            !validationMessage.format &&
            validationMessage.type === messageType)
            .map((validationMessage) => validationMessage.message);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], GenericEditorFieldMessagesComponent.prototype, "field", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], GenericEditorFieldMessagesComponent.prototype, "qualifier", void 0);
GenericEditorFieldMessagesComponent = __decorate([
    core.Component({
        selector: 'se-generic-editor-field-messages',
        template: `<div *ngIf="errors?.length > 0" style="display: grid"><span *ngFor="let error of errors" class="se-generic-editor__error se-help-block--has-error help-block fd-form__message fd-form__message--error">{{ error | translate}}</span></div><div *ngIf="warnings?.length > 0" style="display: grid"><span *ngFor="let warning of warnings" class="se-generic-editor__warning se-help-block--has-warning help-block fd-form__message fd-form__message--warning">{{ warning | translate }}</span></div>`
    })
], GenericEditorFieldMessagesComponent);

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorFieldWrapperComponent", {
    selector: 'se-generic-editor-field-wrapper',
    template: ` <ng-template [formRenderer]="form"></ng-template> `
});
let GenericEditorFieldWrapperComponent = class GenericEditorFieldWrapperComponent {
    constructor({ model: form, tabId, tab }) {
        this.form = form.controls[tabId];
        this._subscription = this.form.statusChanges.subscribe((status) => {
            tab.hasErrors = status === 'INVALID';
        });
    }
    ngOnDestroy() {
        this._subscription.unsubscribe();
    }
};
GenericEditorFieldWrapperComponent = __decorate([
    core.Component({
        selector: 'se-generic-editor-field-wrapper',
        template: ` <ng-template [formRenderer]="form"></ng-template> `
    }),
    __param(0, core.Inject(TAB_DATA)),
    __metadata("design:paramtypes", [Object])
], GenericEditorFieldWrapperComponent);

window.__smartedit__.addDecoratorPayload("Component", "LocalizedElementComponent", {
    selector: 'se-localized-element',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    template: `<se-tabs *ngIf="tabs" class="multi-tabs-editor" [model]="form" [tabsList]="tabs" [numTabsDisplayed]="6"></se-tabs>`
});
let LocalizedElementComponent = class LocalizedElementComponent {
    constructor(sessionService) {
        this.sessionService = sessionService;
    }
    onDynamicInputChange() {
        this._createLocalizedTabs();
    }
    ngDoCheck() {
        if (this.tabs && this.field.messages !== this._previousMessages) {
            this._previousMessages = this.field.messages;
            const messageMap = this.field.messages
                ? this.field.messages
                    .filter((message) => message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR)
                    .reduce((holder, next) => {
                    holder[next.language] = true;
                    return holder;
                }, {})
                : {};
            this.tabs = this.tabs.map(function (tab) {
                const message = messageMap[tab.id];
                tab.hasErrors = message !== undefined ? message : false;
                return tab;
            });
        }
    }
    _createLocalizedTabs() {
        return __awaiter(this, void 0, void 0, function* () {
            this.field.isLanguageEnabledMap = {};
            const { readableLanguages, writeableLanguages } = yield this.sessionService.getCurrentUser();
            const readableSet = new Set(readableLanguages);
            const writeable = new Set(writeableLanguages);
            this.tabs = this.languages
                .filter((language) => readableSet.has(language.isocode))
                .map(({ isocode, required }) => {
                this.field.isLanguageEnabledMap[isocode] = writeable.has(isocode);
                const title = `${isocode.toUpperCase()}${this.field.editable && this.field.required && required ? '*' : ''}`;
                return {
                    title,
                    id: isocode,
                    component: GenericEditorFieldWrapperComponent
                };
            });
        });
    }
};
__decorate([
    utils.DynamicForm(),
    __metadata("design:type", utils.FormGrouping)
], LocalizedElementComponent.prototype, "form", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Object)
], LocalizedElementComponent.prototype, "field", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Object)
], LocalizedElementComponent.prototype, "component", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Array)
], LocalizedElementComponent.prototype, "languages", void 0);
LocalizedElementComponent = __decorate([
    core.Component({
        selector: 'se-localized-element',
        styles: [
            `
            :host {
                display: block;
            }
        `
        ],
        template: `<se-tabs *ngIf="tabs" class="multi-tabs-editor" [model]="form" [tabsList]="tabs" [numTabsDisplayed]="6"></se-tabs>`
    }),
    __metadata("design:paramtypes", [utils.ISessionService])
], LocalizedElementComponent);

window.__smartedit__.addDecoratorPayload("Component", "GenericEditorRootTabsComponent", {
    selector: 'se-ge-root-tabs',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    template: `
        <se-tabs
            class="se-generic-editor__tabs"
            [model]="form"
            [tabsList]="tabs"
            [numTabsDisplayed]="3"></se-tabs>
    `
});
let GenericEditorRootTabsComponent = class GenericEditorRootTabsComponent {
};
__decorate([
    utils.DynamicForm(),
    __metadata("design:type", utils.FormGrouping)
], GenericEditorRootTabsComponent.prototype, "form", void 0);
__decorate([
    utils.DynamicInput(),
    __metadata("design:type", Array)
], GenericEditorRootTabsComponent.prototype, "tabs", void 0);
GenericEditorRootTabsComponent = __decorate([
    core.Component({
        selector: 'se-ge-root-tabs',
        styles: [
            `
            :host {
                display: block;
            }
        `
        ],
        template: `
        <se-tabs
            class="se-generic-editor__tabs"
            [model]="form"
            [tabsList]="tabs"
            [numTabsDisplayed]="3"></se-tabs>
    `
    })
], GenericEditorRootTabsComponent);

/**
 * Applied on a DOM element, this Directive will trigger a submit of the data stored in
 * the parent {@link ContentManager} upon cliking.
 *
 * ### Example
 *
 *      <form [contentManager]="{onSave: editor.someSubmit}">
 *          <button [seSubmitBtn]="editor.isSubmitDisabled">Submit </button>
 *      </form>
 *
 * @param seSubmitBtn The optional callback returning a boolean to add more cases for disablement
 */
let SubmitBtnDirective = class SubmitBtnDirective {
    constructor(cm) {
        this.cm = cm;
    }
    /**
     * Modifies the disabled attribute to be disabled when saving.
     */
    get disabled() {
        return this.cm.submitting || (this.isDisabled && this.isDisabled());
    }
    /**
     * When the element is clicked the save operation is called in the content manager direcitve.
     */
    save($event) {
        $event.preventDefault();
        this.cm.save().subscribe();
    }
};
__decorate([
    core.Input('seSubmitBtn'),
    __metadata("design:type", Function)
], SubmitBtnDirective.prototype, "isDisabled", void 0);
__decorate([
    core.HostBinding('disabled'),
    __metadata("design:type", Boolean),
    __metadata("design:paramtypes", [])
], SubmitBtnDirective.prototype, "disabled", null);
__decorate([
    core.HostListener('click', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Event]),
    __metadata("design:returntype", void 0)
], SubmitBtnDirective.prototype, "save", null);
SubmitBtnDirective = __decorate([
    core.Directive({
        selector: '[seSubmitBtn]'
    }),
    __metadata("design:paramtypes", [ContentManager])
], SubmitBtnDirective);

exports.GenericEditorWidgetModule = class GenericEditorWidgetModule {
};
exports.GenericEditorWidgetModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            DateTimePickerModule,
            exports.BooleanModule,
            utils.TranslationModule.forChild(),
            forms.FormsModule,
            exports.RichTextFieldModule,
            exports.SelectModule,
            FloatModule,
            exports.NumberModule,
            EmailModule,
            EditableDropdownModule,
            form.FormModule
        ],
        declarations: [
            exports.ShortStringComponent,
            LongStringComponent,
            EnumComponent,
            EnumItemPrinterComponent,
            DropdownComponent,
            DropdownItemPrinterComponent,
            TextComponent
        ],
        entryComponents: [
            exports.ShortStringComponent,
            LongStringComponent,
            EnumComponent,
            EnumItemPrinterComponent,
            DropdownComponent,
            DropdownItemPrinterComponent,
            TextComponent
        ]
    })
], exports.GenericEditorWidgetModule);

/**
 * Form Builder Setup
 */
exports.SeGenericEditorModule = class SeGenericEditorModule {
};
exports.SeGenericEditorModule = __decorate([
    core.NgModule({
        schemas: [core.CUSTOM_ELEMENTS_SCHEMA],
        imports: [
            common.CommonModule,
            exports.YjqueryModule,
            forms.FormsModule,
            forms.ReactiveFormsModule,
            exports.FundamentalsModule,
            exports.SharedComponentsModule,
            exports.GenericEditorWidgetModule,
            exports.SelectModule,
            exports.GenericEditorDropdownModule,
            utils.TranslationModule.forChild(),
            utils.FormBuilderModule.forRoot({
                validators: {
                    required: () => forms.Validators.required,
                    email: () => forms.Validators.email,
                    notBlank: () => isBlankValidator()
                },
                types: {
                    tabs: GenericEditorRootTabsComponent,
                    localized: LocalizedElementComponent,
                    field: GenericEditorDynamicFieldComponent
                }
            })
        ],
        providers: [
            exports.GenericEditorFactoryService,
            exports.EditorFieldMappingService,
            exports.FetchEnumDataHandler,
            exports.GenericEditorStackService,
            exports.GenericEditorTabService,
            exports.SeValidationMessageParser,
            GenericEditorStateBuilderService
        ],
        declarations: [
            GenericEditorDynamicFieldComponent,
            exports.GenericEditorComponent,
            GenericEditorTabComponent,
            LocalizedElementComponent,
            exports.GenericEditorFieldComponent,
            GenericEditorFieldMessagesComponent,
            GenericEditorFieldWrapperComponent,
            exports.GenericEditorBreadcrumbComponent,
            FormBuilderDirective,
            ContentManager,
            GenericEditorRootTabsComponent,
            SubmitBtnDirective
        ],
        entryComponents: [
            exports.GenericEditorComponent,
            GenericEditorTabComponent,
            LocalizedElementComponent,
            exports.GenericEditorFieldComponent,
            GenericEditorFieldMessagesComponent,
            GenericEditorFieldWrapperComponent,
            exports.GenericEditorBreadcrumbComponent
        ],
        exports: [exports.GenericEditorComponent, exports.GenericEditorWidgetModule, exports.GenericEditorDropdownModule]
    })
], exports.SeGenericEditorModule);

exports.GenericEditorMediaType = void 0;
(function (GenericEditorMediaType) {
    GenericEditorMediaType["VIDEO"] = "video";
    GenericEditorMediaType["IMAGE"] = "image";
    GenericEditorMediaType["PDF_DOCUMENT"] = "pdf";
    GenericEditorMediaType["DEFAULT"] = "";
})(exports.GenericEditorMediaType || (exports.GenericEditorMediaType = {}));

window.__smartedit__.addDecoratorPayload("Component", "HeaderLanguageDropdownComponent", {
    selector: 'header-language-dropdown',
    template: `
        <ul role="menu" class="fd-menu__list se-language-selector">
            <li *ngIf="selectedLanguage" class="fd-menu__item">
                <a
                    class="yToolbarActions__dropdown-element se-language-selector__element--selected fd-menu__link">
                    <span class="fd-menu__title"> {{ selectedLanguage.name }} </span>
                </a>
            </li>
            <ng-container *ngFor="let language of items">
                <li
                    *ngIf="selectedLanguage.isoCode !== language.value.isoCode"
                    class="fd-menu__item">
                    <a
                        class="yToolbarActions__dropdown-element se-language-selector__element fd-menu__link"
                        (click)="onSelectedLanguage(language)">
                        <span class="fd-menu__title"> {{ language.value.name }} </span>
                    </a>
                </li>
            </ng-container>
        </ul>
    `
});
exports.HeaderLanguageDropdownComponent = class HeaderLanguageDropdownComponent extends utils.LanguageDropdown {
    constructor(languageService, crossFrameEventService, logService) {
        super(languageService, crossFrameEventService, logService);
        this.languageService = languageService;
        this.crossFrameEventService = crossFrameEventService;
        this.logService = logService;
        this.languageSortStrategy = 'none';
    }
};
exports.HeaderLanguageDropdownComponent = __decorate([
    core.Component({
        selector: 'header-language-dropdown',
        template: `
        <ul role="menu" class="fd-menu__list se-language-selector">
            <li *ngIf="selectedLanguage" class="fd-menu__item">
                <a
                    class="yToolbarActions__dropdown-element se-language-selector__element--selected fd-menu__link">
                    <span class="fd-menu__title"> {{ selectedLanguage.name }} </span>
                </a>
            </li>
            <ng-container *ngFor="let language of items">
                <li
                    *ngIf="selectedLanguage.isoCode !== language.value.isoCode"
                    class="fd-menu__item">
                    <a
                        class="yToolbarActions__dropdown-element se-language-selector__element fd-menu__link"
                        (click)="onSelectedLanguage(language)">
                        <span class="fd-menu__title"> {{ language.value.name }} </span>
                    </a>
                </li>
            </ng-container>
        </ul>
    `
    }),
    __metadata("design:paramtypes", [exports.LanguageService,
        exports.CrossFrameEventService,
        utils.LogService])
], exports.HeaderLanguageDropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "ConfirmDialogComponent", {
    selector: 'se-confirm-dialog',
    template: `<ng-container *ngIf="config"><se-message *ngIf="config.message" [type]="config.message.type" [messageId]="config.message.id"><ng-container se-message-description><span [translate]="config.message.text"></span></ng-container></se-message><div id="confirmationModalDescription" [ngClass]="{ 'fd-has-margin-top-small' : config.message }" [translate]="config.description" [translateParams]="config.descriptionPlaceholders"></div></ng-container>`
});
exports.ConfirmDialogComponent = class ConfirmDialogComponent {
    constructor(modalManager) {
        this.modalManager = modalManager;
    }
    ngOnInit() {
        this.modalManager
            .getModalData()
            .pipe(operators.take(1))
            .subscribe((config) => (this.config = config));
    }
};
exports.ConfirmDialogComponent = __decorate([
    core.Component({
        selector: 'se-confirm-dialog',
        template: `<ng-container *ngIf="config"><se-message *ngIf="config.message" [type]="config.message.type" [messageId]="config.message.id"><ng-container se-message-description><span [translate]="config.message.text"></span></ng-container></se-message><div id="confirmationModalDescription" [ngClass]="{ 'fd-has-margin-top-small' : config.message }" [translate]="config.description" [translateParams]="config.descriptionPlaceholders"></div></ng-container>`
    }),
    __metadata("design:paramtypes", [utils.ModalManagerService])
], exports.ConfirmDialogComponent);

exports.ConfirmDialogModule = class ConfirmDialogModule {
};
exports.ConfirmDialogModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, utils.TranslationModule.forChild(), exports.MessageModule],
        declarations: [exports.ConfirmDialogComponent],
        entryComponents: [exports.ConfirmDialogComponent]
    })
], exports.ConfirmDialogModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * An Injection Token used to retrieve information about "item" and "key" from within rendered component.
 */
const CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN = new core.InjectionToken('CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN');

window.__smartedit__.addDecoratorPayload("Component", "ClientPagedListCellComponent", {
    selector: 'se-client-paged-list-cell',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<ng-container *ngIf="key.component; else cellText"><ng-container *ngComponentOutlet="key.component; injector: componentInjector"></ng-container></ng-container><ng-template #cellText><span>{{ item[key.property] }}</span></ng-template>`
});
let ClientPagedListCellComponent = class ClientPagedListCellComponent {
    constructor(injector) {
        this.injector = injector;
    }
    ngOnInit() {
        this.createComponentInjector();
    }
    createComponentInjector() {
        this.componentInjector = core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN,
                    useValue: {
                        item: this.item,
                        key: this.key
                    }
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ClientPagedListCellComponent.prototype, "item", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ClientPagedListCellComponent.prototype, "key", void 0);
ClientPagedListCellComponent = __decorate([
    core.Component({
        selector: 'se-client-paged-list-cell',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<ng-container *ngIf="key.component; else cellText"><ng-container *ngComponentOutlet="key.component; injector: componentInjector"></ng-container></ng-container><ng-template #cellText><span>{{ item[key.property] }}</span></ng-template>`
    }),
    __metadata("design:paramtypes", [core.Injector])
], ClientPagedListCellComponent);

window.__smartedit__.addDecoratorPayload("Component", "ClientPagedListComponent", {
    selector: 'se-client-paged-list',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    providers: [exports.FilterByFieldPipe, exports.StartFromPipe, common.SlicePipe],
    template: `<div class="fluid-container se-paged-list-result"><p class="se-page-list__page-count" *ngIf="displayCount"><span>({{ totalItems }} {{ 'se.pagelist.countsearchresult' | translate }})</span></p><table class="se-paged-list-table table table-striped table-hover fd-table"><thead><tr><th *ngFor="let key of keys; trackBy: keysTrackBy" (click)="onOrderByColumn(key.property)" [ngStyle]="{ 'width': columnWidth + '%' }" class="se-paged-list__header" [ngClass]="'se-paged-list__header-' + key.property"><ng-container *ngIf="key.i18n">{{ key.i18n | translate }} <span class="header-icon" [hidden]="visibleSortingHeader !== key.property" [ngClass]="{
                          'sap-icon--navigation-down-arrow': headersSortingState[key.property],
                          'sap-icon--navigation-up-arrow': !headersSortingState[key.property]
                      }"></span></ng-container></th><th class="se-paged-list__header"></th><th class="se-paged-list__header" *ngIf="dropdownItems"></th></tr></thead><tbody class="se-paged-list__table-body"><tr *ngFor="let item of filteredItems" class="se-paged-list-item"><td *ngFor="let key of keys" [ngClass]="'se-paged-list-item-' + key.property"><se-client-paged-list-cell [item]="item" [key]="key"></se-client-paged-list-cell></td><td><se-tooltip *ngIf="item.icon" [triggers]="['mouseenter', 'mouseleave']" [placement]="'bottom'" [title]="'se.icon.tooltip.visibility' | translate: { numberOfRestrictions: item.icon.numberOfRestrictions }" [isChevronVisible]="true"><img [src]="item.icon.src" se-tooltip-trigger/></se-tooltip></td><ng-container *ngIf="dropdownItems"><td *seHasOperationPermission="'se.edit.page'" class="paged-list-table__body__td paged-list-table__body__td-menu"><se-dropdown-menu [dropdownItems]="dropdownItems" [selectedItem]="item" class="pull-right"></se-dropdown-menu></td></ng-container></tr></tbody></table><div class="pagination-container"><se-pagination [totalItems]="totalItems < itemsPerPage ? itemsPerPage : totalItems" [itemsPerPage]="itemsPerPage" (onChange)="onCurrentPageChange($event)" [currentPage]="currentPage"></se-pagination></div></div>`
});
let ClientPagedListComponent = class ClientPagedListComponent {
    constructor(cdr, filterByFieldPipe, startFromPipe, slicePipe) {
        this.cdr = cdr;
        this.filterByFieldPipe = filterByFieldPipe;
        this.startFromPipe = startFromPipe;
        this.slicePipe = slicePipe;
        this.reversed = false;
        this.displayCount = false;
        this.totalItems = 0;
        this.currentPage = 1;
        this.headersSortingState = {};
    }
    ngOnChanges(changes) {
        if (changes.items || changes.query || changes.itemFilterKeys || changes.itemsPerPage) {
            if (changes.query) {
                this.currentPage = 1;
            }
            this.filteredItems = this.filterItems();
            this.totalItems =
                changes.query && changes.query.currentValue
                    ? this.filteredItems.length
                    : this.items.length;
        }
        if (changes.keys) {
            this.columnWidth = 100 / (this.keys.length || 1);
        }
        if (changes.reversed) {
            this.columnToggleReversed = this.reversed;
        }
        if (changes.sortBy) {
            this.headersSortingState = Object.assign(Object.assign({}, this.headersSortingState), { [this.sortBy]: this.columnToggleReversed });
            this.visibleSortingHeader = this.sortBy;
            this.items = objectUtils.sortBy(this.items, this.sortBy, this.columnToggleReversed);
            this.filteredItems = this.filterItems();
        }
    }
    keysTrackBy(_index, key) {
        return key.property;
    }
    onOrderByColumn(columnKeyProp) {
        this.columnToggleReversed = !this.columnToggleReversed;
        this.headersSortingState[columnKeyProp] = this.columnToggleReversed;
        this.visibleSortingHeader = columnKeyProp;
        this.items = objectUtils.sortBy(this.items, columnKeyProp, this.columnToggleReversed);
        this.filteredItems = this.filterItems();
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    onCurrentPageChange(page) {
        this.currentPage = page;
        this.filteredItems = this.filterItems();
        if (!this.cdr.destroyed) {
            this.cdr.detectChanges();
        }
    }
    filterItems() {
        const filterKeys = this.itemFilterKeys || [];
        const filteredItems = this.filterByFieldPipe.transform(this.items, this.query, filterKeys);
        const startFromIndex = (this.currentPage - 1) * this.itemsPerPage;
        const startFromItems = this.startFromPipe.transform(filteredItems, startFromIndex);
        const slicedItems = this.slicePipe.transform(startFromItems, 0, this.itemsPerPage);
        return slicedItems;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ClientPagedListComponent.prototype, "items", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ClientPagedListComponent.prototype, "itemFilterKeys", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ClientPagedListComponent.prototype, "keys", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], ClientPagedListComponent.prototype, "itemsPerPage", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ClientPagedListComponent.prototype, "sortBy", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ClientPagedListComponent.prototype, "reversed", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ClientPagedListComponent.prototype, "query", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ClientPagedListComponent.prototype, "displayCount", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ClientPagedListComponent.prototype, "dropdownItems", void 0);
ClientPagedListComponent = __decorate([
    core.Component({
        selector: 'se-client-paged-list',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        providers: [exports.FilterByFieldPipe, exports.StartFromPipe, common.SlicePipe],
        template: `<div class="fluid-container se-paged-list-result"><p class="se-page-list__page-count" *ngIf="displayCount"><span>({{ totalItems }} {{ 'se.pagelist.countsearchresult' | translate }})</span></p><table class="se-paged-list-table table table-striped table-hover fd-table"><thead><tr><th *ngFor="let key of keys; trackBy: keysTrackBy" (click)="onOrderByColumn(key.property)" [ngStyle]="{ 'width': columnWidth + '%' }" class="se-paged-list__header" [ngClass]="'se-paged-list__header-' + key.property"><ng-container *ngIf="key.i18n">{{ key.i18n | translate }} <span class="header-icon" [hidden]="visibleSortingHeader !== key.property" [ngClass]="{
                          'sap-icon--navigation-down-arrow': headersSortingState[key.property],
                          'sap-icon--navigation-up-arrow': !headersSortingState[key.property]
                      }"></span></ng-container></th><th class="se-paged-list__header"></th><th class="se-paged-list__header" *ngIf="dropdownItems"></th></tr></thead><tbody class="se-paged-list__table-body"><tr *ngFor="let item of filteredItems" class="se-paged-list-item"><td *ngFor="let key of keys" [ngClass]="'se-paged-list-item-' + key.property"><se-client-paged-list-cell [item]="item" [key]="key"></se-client-paged-list-cell></td><td><se-tooltip *ngIf="item.icon" [triggers]="['mouseenter', 'mouseleave']" [placement]="'bottom'" [title]="'se.icon.tooltip.visibility' | translate: { numberOfRestrictions: item.icon.numberOfRestrictions }" [isChevronVisible]="true"><img [src]="item.icon.src" se-tooltip-trigger/></se-tooltip></td><ng-container *ngIf="dropdownItems"><td *seHasOperationPermission="'se.edit.page'" class="paged-list-table__body__td paged-list-table__body__td-menu"><se-dropdown-menu [dropdownItems]="dropdownItems" [selectedItem]="item" class="pull-right"></se-dropdown-menu></td></ng-container></tr></tbody></table><div class="pagination-container"><se-pagination [totalItems]="totalItems < itemsPerPage ? itemsPerPage : totalItems" [itemsPerPage]="itemsPerPage" (onChange)="onCurrentPageChange($event)" [currentPage]="currentPage"></se-pagination></div></div>`
    }),
    __metadata("design:paramtypes", [core.ChangeDetectorRef,
        exports.FilterByFieldPipe,
        exports.StartFromPipe,
        common.SlicePipe])
], ClientPagedListComponent);

/**
 * Provides a component to display a paginated list of items with custom renderers.
 *
 * Allows the user to search and sort the list.
 */
exports.ClientPagedListModule = class ClientPagedListModule {
};
exports.ClientPagedListModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            exports.PaginationModule,
            exports.FundamentalsModule,
            exports.TooltipModule,
            exports.DropdownMenuModule,
            exports.HasOperationPermissionDirectiveModule,
            exports.FilterByFieldPipeModule,
            exports.StartFromPipeModule,
            utils.TranslationModule.forChild()
        ],
        entryComponents: [ClientPagedListComponent],
        declarations: [ClientPagedListComponent, ClientPagedListCellComponent],
        exports: [ClientPagedListComponent]
    })
], exports.ClientPagedListModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const MEDIA_FILE_SELECTOR_CUSTOM_TOKEN = new core.InjectionToken('MEDIA_FILE_SELECTOR_CUSTOM_TOKEN');
const MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN = new core.InjectionToken('MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN');
const MEDIA_SELECTOR_I18N_KEY = {
    UPLOAD: 'se.media.format.upload',
    REPLACE: 'se.media.format.replace',
    UNDER_EDIT: 'se.media.format.under.edit',
    REMOVE: 'se.media.format.remove'
};
const MEDIA_SELECTOR_I18N_KEY_TOKEN = new core.InjectionToken('MEDIA_SELECTOR_I18N_KEY_TOKEN');
const MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN = new core.InjectionToken('MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN');
const MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN = new core.InjectionToken('MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN');

/**
 * A HTTP request interceptor which intercepts all 'cmswebservices/catalogs' requests and adds the current catalog and version
 * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
 */
let /* @ngInject */ ExperienceInterceptor = class /* @ngInject */ ExperienceInterceptor {
    constructor(sharedDataService, stringUtils, httpUtils) {
        this.sharedDataService = sharedDataService;
        this.stringUtils = stringUtils;
        this.httpUtils = httpUtils;
    }
    /**
     * Interceptor method which gets called with a http config object, intercepts any 'cmswebservices/catalogs' requests and adds
     * the current catalog and version
     * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
     * If the request URI contains any of 'PAGE_CONTEXT_SITE_ID', 'PAGE_CONTEXT_CATALOG' or 'PAGE_CONTEXT_CATALOG_VERSION',
     * then it is replaced by the siteId/catalogId/catalogVersion of the current page in context.
     *
     * The catalog name and catalog versions of the current experience and the page loaded are stored in the shared data service object called 'experience' during preview initialization
     * and here we retrieve those details and set it to headers.
     */
    intercept(request, next) {
        if (!CMSWEBSERVICES_PATH.test(request.url)) {
            return next.handle(request);
        }
        return rxjs.from(this.sharedDataService.get(EXPERIENCE_STORAGE_KEY)).pipe(operators.switchMap((data) => {
            if (!data) {
                return next.handle(request);
            }
            const keys = {};
            keys.CONTEXT_SITE_ID_WITH_COLON = data.siteDescriptor.uid;
            keys.CONTEXT_CATALOG_VERSION_WITH_COLON = data.catalogDescriptor.catalogVersion;
            keys.CONTEXT_CATALOG_WITH_COLON = data.catalogDescriptor.catalogId;
            keys[CONTEXT_SITE_ID] = data.siteDescriptor.uid;
            keys[CONTEXT_CATALOG_VERSION] = data.catalogDescriptor.catalogVersion;
            keys[CONTEXT_CATALOG] = data.catalogDescriptor.catalogId;
            keys[PAGE_CONTEXT_SITE_ID] = data.pageContext
                ? data.pageContext.siteId
                : data.siteDescriptor.uid;
            keys[PAGE_CONTEXT_CATALOG_VERSION] = data.pageContext
                ? data.pageContext.catalogVersion
                : data.catalogDescriptor.catalogVersion;
            keys[PAGE_CONTEXT_CATALOG] = data.pageContext
                ? data.pageContext.catalogId
                : data.catalogDescriptor.catalogId;
            const newRequest = request.clone({
                url: this.stringUtils.replaceAll(request.url, keys),
                params: request.params && typeof request.params === 'object'
                    ? this.httpUtils.transformHttpParams(request.params, keys)
                    : request.params
            });
            return next.handle(newRequest);
        }));
    }
};
/* @ngInject */ ExperienceInterceptor = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [utils.ISharedDataService,
        utils.StringUtils,
        utils.HttpUtils])
], /* @ngInject */ ExperienceInterceptor);

exports.SmarteditConstantsModule = class SmarteditConstantsModule {
};
exports.SmarteditConstantsModule = __decorate([
    core.NgModule({
        providers: [
            {
                provide: EXTENDED_VIEW_PORT_MARGIN_TOKEN,
                useValue: EXTENDED_VIEW_PORT_MARGIN
            },
            {
                provide: HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN,
                useValue: HEART_BEAT_TIMEOUT_THRESHOLD_MS
            }
        ]
    })
], exports.SmarteditConstantsModule);

const gatewayProxiedAnnotationFactoryToken = new core.InjectionToken('gatewayProxiedAnnotationFactoryToKen');
const cachedAnnotationFactoryToken = new core.InjectionToken('cachedAnnotationFactoryToken');
const cacheConfigAnnotationFactoryToken = new core.InjectionToken('cacheConfigAnnotationFactoryToken');
const invalidateCacheAnnotationFactoryToken = new core.InjectionToken('invalidateCacheAnnotationFactoryToken');
const operationContextAnnotationFactoryToken = new core.InjectionToken('operationContextAnnotationFactoryToken');
/**
 * Module containing all the services shared within the smartedit commons.
 */
exports.SmarteditCommonsModule = class SmarteditCommonsModule {
};
exports.SmarteditCommonsModule = __decorate([
    core.NgModule({
        imports: [
            exports.FundamentalsModule,
            upgrade.LocationUpgradeModule.config(),
            exports.YjqueryModule,
            exports.SeGenericEditorModule,
            exports.WizardModule,
            exports.SmarteditConstantsModule
        ],
        providers: [
            exports.SmarteditBootstrapGateway,
            exports.LanguageServiceGateway,
            exports.LanguageService,
            {
                provide: utils.LANGUAGE_SERVICE,
                useClass: exports.LanguageService
            },
            {
                provide: ISlotRestrictionsService,
                useClass: exports.SlotRestrictionsService
            },
            {
                provide: IEditorModalService,
                useClass: exports.EditorModalService
            },
            {
                provide: IPageInfoService,
                useClass: PageInfoService
            },
            {
                provide: IFileValidation,
                useClass: exports.FileValidationService
            },
            {
                provide: IMediaToUpload,
                useClass: exports.MediaUploaderService
            },
            {
                provide: IMediaService,
                useClass: exports.MediaService
            },
            exports.TimerService,
            exports.DiscardablePromiseUtils,
            moduleUtils.provideValues({
                SSO_LOGOUT_ENTRY_POINT,
                SSO_AUTHENTICATION_ENTRY_POINT,
                SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT,
                SMARTEDIT_RESOURCE_URI_REGEXP,
                SMARTEDIT_ROOT,
                SMARTEDIT_INNER_FILES,
                SMARTEDIT_INNER_FILES_POST,
                [utils.OPERATION_CONTEXT_TOKEN]: OPERATION_CONTEXT,
                [utils.WHO_AM_I_RESOURCE_URI_TOKEN]: WHO_AM_I_RESOURCE_URI,
                [utils.I18N_RESOURCE_URI_TOKEN]: I18N_RESOURCE_URI
            }),
            {
                provide: http.HTTP_INTERCEPTORS,
                useClass: ExperienceInterceptor,
                multi: true
            },
            {
                provide: ISyncPollingService,
                useClass: exports.SyncPollingService
            },
            { provide: utils.IModalService, useClass: utils.ModalService },
            { provide: utils.ISettingsService, useClass: exports.SettingsService },
            exports.SliderPanelServiceFactory,
            utils.LogService,
            utils.BrowserService,
            utils.FingerPrintingService,
            utils.CacheEngine,
            utils.CacheService,
            utils.CloneableUtils,
            exports.CrossFrameEventService,
            /* forbiddenNameSpaces:false */
            {
                provide: utils.LoginDialogResourceProvider,
                useValue: SMARTEDIT_LOGIN_DIALOG_RESOURCES
            },
            {
                provide: utils.LANGUAGE_SERVICE_CONSTANTS,
                useValue: {
                    LANGUAGE_RESOURCE_URI,
                    I18N_LANGUAGES_RESOURCE_URI
                }
            },
            {
                provide: utils.EVENT_SERVICE,
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                useFactory: (crossFrameEventService) => crossFrameEventService,
                deps: [exports.CrossFrameEventService]
            },
            {
                provide: exports.CrossFrameEventServiceGateway.crossFrameEventServiceGatewayToken,
                useClass: exports.CrossFrameEventServiceGateway
            },
            utils.OperationContextService,
            utils.BooleanUtils,
            utils.CryptographicUtils,
            utils.FunctionsUtils,
            utils.HttpUtils,
            NodeUtils,
            utils.PromiseUtils,
            exports.JQueryUtilsService,
            {
                provide: utils.StringUtils,
                useClass: StringUtils
            },
            StringUtils,
            utils.UrlUtils,
            {
                provide: utils.WindowUtils,
                useClass: exports.WindowUtils
            },
            exports.WindowUtils,
            exports.SystemEventService,
            exports.PriorityService,
            exports.GatewayFactory,
            exports.GatewayProxy,
            exports.InterceptorHelper,
            {
                provide: gatewayProxiedAnnotationFactoryToken,
                useFactory: GatewayProxiedAnnotationFactory,
                deps: [exports.GatewayProxy]
            },
            {
                provide: cachedAnnotationFactoryToken,
                useFactory: utils.CachedAnnotationFactory,
                deps: [utils.CacheService]
            },
            {
                provide: cacheConfigAnnotationFactoryToken,
                useFactory: utils.CacheConfigAnnotationFactory,
                deps: [utils.LogService]
            },
            {
                provide: invalidateCacheAnnotationFactoryToken,
                useFactory: utils.InvalidateCacheAnnotationFactory,
                deps: [utils.CacheService]
            },
            {
                provide: operationContextAnnotationFactoryToken,
                useFactory: utils.OperationContextAnnotationFactory,
                deps: [core.Injector, utils.OperationContextService, utils.OPERATION_CONTEXT_TOKEN]
            },
            utils.RestServiceFactory,
            exports.PermissionsRestService,
            exports.AuthorizationService,
            utils.FileMimeTypeService,
            utils.FileReaderService,
            utils.FileValidatorFactory,
            utils.ModalService,
            exports.WorkflowService,
            exports.WorkflowTasksPollingService,
            exports.CmsitemsRestService,
            exports.PageVersioningService,
            exports.PageVersionSelectionService,
            exports.CMSModesService,
            exports.PageContentSlotsService,
            exports.SlotSharedService,
            exports.ComponentMenuService,
            // TODO: remove when all consumers in cmssmartedit has replaced DI with import
            {
                provide: 'seFileValidationServiceConstants',
                useValue: FILE_VALIDATION_CONFIG
            },
            moduleUtils.initialize((gatewayProxiedAnnotationFactory, cachedAnnotationFactory, cacheConfigAnnotationFactory, invalidateCacheAnnotationFactory, operationContextAnnotationFactory) => {
                // disable sonar issue
            }, [
                gatewayProxiedAnnotationFactoryToken,
                cachedAnnotationFactoryToken,
                cacheConfigAnnotationFactoryToken,
                invalidateCacheAnnotationFactoryToken,
                operationContextAnnotationFactoryToken
            ]),
            moduleUtils.bootstrap((retryInterceptor, defaultRetryStrategy, exponentialRetryStrategy, linearRetryStrategy, operationContextService) => {
                retryInterceptor
                    .register(utils.booleanUtils.areAllTruthy(operationContextInteractivePredicate, utils.retriableErrorPredicate), defaultRetryStrategy)
                    .register(utils.booleanUtils.areAllTruthy(operationContextNonInteractivePredicate, utils.retriableErrorPredicate), exponentialRetryStrategy)
                    .register(utils.booleanUtils.areAllTruthy(operationContextCMSPredicate, utils.timeoutErrorPredicate, utils.updatePredicate), exponentialRetryStrategy)
                    .register(utils.booleanUtils.areAllTruthy(operationContextToolingPredicate, utils.timeoutErrorPredicate, utils.updatePredicate), linearRetryStrategy);
                operationContextService.register(LANGUAGE_RESOURCE_URI, OPERATION_CONTEXT.TOOLING);
            }, [
                utils.RetryInterceptor,
                utils.DefaultRetryStrategy,
                utils.ExponentialRetryStrategy,
                utils.LinearRetryStrategy,
                utils.OperationContextService,
                utils.LogService
            ])
        ]
    })
], exports.SmarteditCommonsModule);

Object.defineProperty(exports, 'ALERT_CONFIG_DEFAULTS', {
    enumerable: true,
    get: function () { return utils.ALERT_CONFIG_DEFAULTS; }
});
Object.defineProperty(exports, 'ALERT_CONFIG_DEFAULTS_TOKEN', {
    enumerable: true,
    get: function () { return utils.ALERT_CONFIG_DEFAULTS_TOKEN; }
});
Object.defineProperty(exports, 'AbstractCachedRestService', {
    enumerable: true,
    get: function () { return utils.AbstractCachedRestService; }
});
Object.defineProperty(exports, 'Alert', {
    enumerable: true,
    get: function () { return utils.Alert; }
});
Object.defineProperty(exports, 'AlertModule', {
    enumerable: true,
    get: function () { return utils.AlertModule; }
});
Object.defineProperty(exports, 'AuthenticationService', {
    enumerable: true,
    get: function () { return utils.AuthenticationService; }
});
Object.defineProperty(exports, 'BaseAlertFactory', {
    enumerable: true,
    get: function () { return utils.AlertFactory; }
});
Object.defineProperty(exports, 'BaseAlertService', {
    enumerable: true,
    get: function () { return utils.AlertService; }
});
Object.defineProperty(exports, 'BrowserService', {
    enumerable: true,
    get: function () { return utils.BrowserService; }
});
Object.defineProperty(exports, 'CacheAction', {
    enumerable: true,
    get: function () { return utils.CacheAction; }
});
Object.defineProperty(exports, 'CacheConfig', {
    enumerable: true,
    get: function () { return utils.CacheConfig; }
});
Object.defineProperty(exports, 'CacheConfigAnnotationFactory', {
    enumerable: true,
    get: function () { return utils.CacheConfigAnnotationFactory; }
});
Object.defineProperty(exports, 'CacheService', {
    enumerable: true,
    get: function () { return utils.CacheService; }
});
Object.defineProperty(exports, 'Cached', {
    enumerable: true,
    get: function () { return utils.Cached; }
});
Object.defineProperty(exports, 'CachedAnnotationFactory', {
    enumerable: true,
    get: function () { return utils.CachedAnnotationFactory; }
});
Object.defineProperty(exports, 'CloneableUtils', {
    enumerable: true,
    get: function () { return utils.CloneableUtils; }
});
Object.defineProperty(exports, 'CryptographicUtils', {
    enumerable: true,
    get: function () { return utils.CryptographicUtils; }
});
Object.defineProperty(exports, 'DEFAULT_AUTHENTICATION_ENTRY_POINT', {
    enumerable: true,
    get: function () { return utils.DEFAULT_AUTHENTICATION_ENTRY_POINT; }
});
Object.defineProperty(exports, 'DEFAULT_AUTH_MAP', {
    enumerable: true,
    get: function () { return utils.DEFAULT_AUTH_MAP; }
});
Object.defineProperty(exports, 'DEFAULT_CREDENTIALS_MAP', {
    enumerable: true,
    get: function () { return utils.DEFAULT_CREDENTIALS_MAP; }
});
Object.defineProperty(exports, 'DEFAULT_LANGUAGE_ISO', {
    enumerable: true,
    get: function () { return utils.DEFAULT_LANGUAGE_ISO; }
});
Object.defineProperty(exports, 'EVENTS', {
    enumerable: true,
    get: function () { return utils.EVENTS; }
});
Object.defineProperty(exports, 'EVENT_SERVICE', {
    enumerable: true,
    get: function () { return utils.EVENT_SERVICE; }
});
Object.defineProperty(exports, 'EvictionTag', {
    enumerable: true,
    get: function () { return utils.EvictionTag; }
});
Object.defineProperty(exports, 'FileMimeTypeService', {
    enumerable: true,
    get: function () { return utils.FileMimeTypeService; }
});
Object.defineProperty(exports, 'FileReaderService', {
    enumerable: true,
    get: function () { return utils.FileReaderService; }
});
Object.defineProperty(exports, 'FileValidatorFactory', {
    enumerable: true,
    get: function () { return utils.FileValidatorFactory; }
});
Object.defineProperty(exports, 'FingerPrintingService', {
    enumerable: true,
    get: function () { return utils.FingerPrintingService; }
});
Object.defineProperty(exports, 'FunctionsUtils', {
    enumerable: true,
    get: function () { return utils.FunctionsUtils; }
});
Object.defineProperty(exports, 'FundamentalModalTemplateModule', {
    enumerable: true,
    get: function () { return utils.FundamentalModalTemplateModule; }
});
Object.defineProperty(exports, 'HttpBackendService', {
    enumerable: true,
    get: function () { return utils.HttpBackendService; }
});
Object.defineProperty(exports, 'HttpErrorInterceptorService', {
    enumerable: true,
    get: function () { return utils.HttpErrorInterceptorService; }
});
Object.defineProperty(exports, 'HttpInterceptorModule', {
    enumerable: true,
    get: function () { return utils.HttpInterceptorModule; }
});
Object.defineProperty(exports, 'HttpUtils', {
    enumerable: true,
    get: function () { return utils.HttpUtils; }
});
Object.defineProperty(exports, 'I18N_RESOURCE_URI_TOKEN', {
    enumerable: true,
    get: function () { return utils.I18N_RESOURCE_URI_TOKEN; }
});
Object.defineProperty(exports, 'I18N_ROOT_RESOURCE_URI', {
    enumerable: true,
    get: function () { return utils.I18N_ROOT_RESOURCE_URI; }
});
Object.defineProperty(exports, 'IAlertService', {
    enumerable: true,
    get: function () { return utils.IAlertService; }
});
Object.defineProperty(exports, 'IAlertServiceType', {
    enumerable: true,
    get: function () { return utils.IAlertServiceType; }
});
Object.defineProperty(exports, 'IAuthenticationManagerService', {
    enumerable: true,
    get: function () { return utils.IAuthenticationManagerService; }
});
Object.defineProperty(exports, 'IAuthenticationService', {
    enumerable: true,
    get: function () { return utils.IAuthenticationService; }
});
Object.defineProperty(exports, 'IBrowserService', {
    enumerable: true,
    get: function () { return utils.BrowserService; }
});
Object.defineProperty(exports, 'IModalService', {
    enumerable: true,
    get: function () { return utils.IModalService; }
});
Object.defineProperty(exports, 'ISessionService', {
    enumerable: true,
    get: function () { return utils.ISessionService; }
});
Object.defineProperty(exports, 'ISettingsService', {
    enumerable: true,
    get: function () { return utils.ISettingsService; }
});
Object.defineProperty(exports, 'ISharedDataService', {
    enumerable: true,
    get: function () { return utils.ISharedDataService; }
});
Object.defineProperty(exports, 'IStorageService', {
    enumerable: true,
    get: function () { return utils.IStorageService; }
});
Object.defineProperty(exports, 'ITranslationsFetchService', {
    enumerable: true,
    get: function () { return utils.ITranslationsFetchService; }
});
Object.defineProperty(exports, 'InvalidateCache', {
    enumerable: true,
    get: function () { return utils.InvalidateCache; }
});
Object.defineProperty(exports, 'InvalidateCacheAnnotationFactory', {
    enumerable: true,
    get: function () { return utils.InvalidateCacheAnnotationFactory; }
});
Object.defineProperty(exports, 'LANDING_PAGE_PATH', {
    enumerable: true,
    get: function () { return utils.LANDING_PAGE_PATH; }
});
Object.defineProperty(exports, 'LogService', {
    enumerable: true,
    get: function () { return utils.LogService; }
});
Object.defineProperty(exports, 'ModalButtonAction', {
    enumerable: true,
    get: function () { return utils.ModalButtonAction; }
});
Object.defineProperty(exports, 'ModalButtonStyle', {
    enumerable: true,
    get: function () { return utils.ModalButtonStyle; }
});
Object.defineProperty(exports, 'ModalManagerService', {
    enumerable: true,
    get: function () { return utils.ModalManagerService; }
});
Object.defineProperty(exports, 'ModalService', {
    enumerable: true,
    get: function () { return utils.ModalService; }
});
Object.defineProperty(exports, 'OPERATION_CONTEXT_TOKEN', {
    enumerable: true,
    get: function () { return utils.OPERATION_CONTEXT_TOKEN; }
});
Object.defineProperty(exports, 'OperationContextAnnotationFactory', {
    enumerable: true,
    get: function () { return utils.OperationContextAnnotationFactory; }
});
Object.defineProperty(exports, 'OperationContextRegistered', {
    enumerable: true,
    get: function () { return utils.OperationContextRegistered; }
});
Object.defineProperty(exports, 'OperationContextService', {
    enumerable: true,
    get: function () { return utils.OperationContextService; }
});
Object.defineProperty(exports, 'ParentWindowUtils', {
    enumerable: true,
    get: function () { return utils.WindowUtils; }
});
Object.defineProperty(exports, 'PermissionErrorInterceptor', {
    enumerable: true,
    get: function () { return utils.PermissionErrorInterceptor; }
});
Object.defineProperty(exports, 'PromiseUtils', {
    enumerable: true,
    get: function () { return utils.PromiseUtils; }
});
Object.defineProperty(exports, 'ResponseAdapterInterceptor', {
    enumerable: true,
    get: function () { return utils.ResponseAdapterInterceptor; }
});
Object.defineProperty(exports, 'RestServiceFactory', {
    enumerable: true,
    get: function () { return utils.RestServiceFactory; }
});
Object.defineProperty(exports, 'RetryInterceptor', {
    enumerable: true,
    get: function () { return utils.RetryInterceptor; }
});
Object.defineProperty(exports, 'SELECTED_LANGUAGE', {
    enumerable: true,
    get: function () { return utils.SELECTED_LANGUAGE; }
});
Object.defineProperty(exports, 'SSOAuthenticationHelper', {
    enumerable: true,
    get: function () { return utils.SSOAuthenticationHelper; }
});
Object.defineProperty(exports, 'SWITCH_LANGUAGE_EVENT', {
    enumerable: true,
    get: function () { return utils.SWITCH_LANGUAGE_EVENT; }
});
Object.defineProperty(exports, 'SeTranslationModule', {
    enumerable: true,
    get: function () { return utils.TranslationModule; }
});
Object.defineProperty(exports, 'TranslationModule', {
    enumerable: true,
    get: function () { return utils.TranslationModule; }
});
Object.defineProperty(exports, 'URIBuilder', {
    enumerable: true,
    get: function () { return utils.URIBuilder; }
});
Object.defineProperty(exports, 'UnauthorizedErrorInterceptor', {
    enumerable: true,
    get: function () { return utils.UnauthorizedErrorInterceptor; }
});
Object.defineProperty(exports, 'UrlUtils', {
    enumerable: true,
    get: function () { return utils.UrlUtils; }
});
Object.defineProperty(exports, 'WHO_AM_I_RESOURCE_URI_TOKEN', {
    enumerable: true,
    get: function () { return utils.WHO_AM_I_RESOURCE_URI_TOKEN; }
});
Object.defineProperty(exports, 'annotationService', {
    enumerable: true,
    get: function () { return utils.annotationService; }
});
Object.defineProperty(exports, 'booleanUtils', {
    enumerable: true,
    get: function () { return utils.booleanUtils; }
});
Object.defineProperty(exports, 'commonNgZone', {
    enumerable: true,
    get: function () { return utils.commonNgZone; }
});
Object.defineProperty(exports, 'functionsUtils', {
    enumerable: true,
    get: function () { return utils.functionsUtils; }
});
Object.defineProperty(exports, 'httpUtils', {
    enumerable: true,
    get: function () { return utils.httpUtils; }
});
Object.defineProperty(exports, 'promiseUtils', {
    enumerable: true,
    get: function () { return utils.promiseUtils; }
});
Object.defineProperty(exports, 'urlUtils', {
    enumerable: true,
    get: function () { return utils.urlUtils; }
});
exports.ALL_ACTIVE_THEMES_URI = ALL_ACTIVE_THEMES_URI;
exports.ALL_PERSPECTIVE = ALL_PERSPECTIVE;
exports.ANNOUNCEMENT_DATA = ANNOUNCEMENT_DATA;
exports.AbstractDecorator = AbstractDecorator;
exports.ApiUtils = ApiUtils;
exports.CATALOG_DETAILS_COLUMNS = CATALOG_DETAILS_COLUMNS;
exports.CATALOG_DETAILS_ITEM_DATA = CATALOG_DETAILS_ITEM_DATA;
exports.CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT = CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT;
exports.CATALOG_VERSION_UUID_ATTRIBUTE = CATALOG_VERSION_UUID_ATTRIBUTE;
exports.CLICK_DROPDOWN = CLICK_DROPDOWN;
exports.CLICK_DROPDOWN_TOKEN = CLICK_DROPDOWN_TOKEN;
exports.CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN = CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN;
exports.CLOSE_CTX_MENU = CLOSE_CTX_MENU;
exports.CMSITEMS_UPDATE_EVENT = CMSITEMS_UPDATE_EVENT;
exports.CMSWEBSERVICES_PATH = CMSWEBSERVICES_PATH;
exports.CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU = CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU;
exports.COLLAPSIBLE_DEFAULT_CONFIGURATION = COLLAPSIBLE_DEFAULT_CONFIGURATION;
exports.COMPONENT_CLASS = COMPONENT_CLASS;
exports.CONFIGURATION_URI = CONFIGURATION_URI;
exports.CONTAINER_ID_ATTRIBUTE = CONTAINER_ID_ATTRIBUTE;
exports.CONTAINER_TYPE_ATTRIBUTE = CONTAINER_TYPE_ATTRIBUTE;
exports.CONTENT_SLOT_TYPE = CONTENT_SLOT_TYPE;
exports.CONTEXTUAL_MENU_ITEM_DATA = CONTEXTUAL_MENU_ITEM_DATA;
exports.CONTEXT_CATALOG = CONTEXT_CATALOG;
exports.CONTEXT_CATALOG_VERSION = CONTEXT_CATALOG_VERSION;
exports.CONTEXT_SITE_ID = CONTEXT_SITE_ID;
exports.CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS = CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS;
exports.CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS;
exports.CSS_CLASSNAMES = CSS_CLASSNAMES;
exports.CTX_MENU_DROPDOWN_IS_OPEN = CTX_MENU_DROPDOWN_IS_OPEN;
exports.CURRENT_USER_THEME_URI = CURRENT_USER_THEME_URI;
exports.CustomDropdownPopulatorsToken = CustomDropdownPopulatorsToken;
exports.CustomHandlingStrategy = CustomHandlingStrategy;
exports.DATA_TABLE_COMPONENT_DATA = DATA_TABLE_COMPONENT_DATA;
exports.DATE_CONSTANTS = DATE_CONSTANTS;
exports.DEFAULT_AUTHENTICATION_CLIENT_ID = DEFAULT_AUTHENTICATION_CLIENT_ID;
exports.DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION;
exports.DEFAULT_LANGUAGE = DEFAULT_LANGUAGE;
exports.DINameUtils = DINameUtils;
exports.DOMAIN_TOKEN = DOMAIN_TOKEN;
exports.DO_NOT_USE_STORAGE_MANAGER_TOKEN = DO_NOT_USE_STORAGE_MANAGER_TOKEN;
exports.DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME = DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME;
exports.DROPDOWN_IMPLEMENTATION_SUFFIX = DROPDOWN_IMPLEMENTATION_SUFFIX;
exports.DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN = DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN;
exports.DROPDOWN_MENU_ITEM_DATA = DROPDOWN_MENU_ITEM_DATA;
exports.DROPDOWN_POPULATOR = DROPDOWN_POPULATOR;
exports.DateUtils = DateUtils;
exports.DropdownPopulatorInterface = DropdownPopulatorInterface;
exports.ELEMENT_UUID_ATTRIBUTE = ELEMENT_UUID_ATTRIBUTE;
exports.ENUM_RESOURCE_URI = ENUM_RESOURCE_URI;
exports.EVENT_CONTENT_CATALOG_UPDATE = EVENT_CONTENT_CATALOG_UPDATE;
exports.EVENT_INNER_FRAME_CLICKED = EVENT_INNER_FRAME_CLICKED;
exports.EVENT_NOTIFICATION_CHANGED = EVENT_NOTIFICATION_CHANGED;
exports.EVENT_OPEN_IN_PAGE_TREE = EVENT_OPEN_IN_PAGE_TREE;
exports.EVENT_OUTER_FRAME_CLICKED = EVENT_OUTER_FRAME_CLICKED;
exports.EVENT_OVERALL_REFRESH_TREE_NODE = EVENT_OVERALL_REFRESH_TREE_NODE;
exports.EVENT_PAGE_TREE_COMPONENT_SELECTED = EVENT_PAGE_TREE_COMPONENT_SELECTED;
exports.EVENT_PAGE_TREE_PANEL_SWITCH = EVENT_PAGE_TREE_PANEL_SWITCH;
exports.EVENT_PAGE_TREE_SLOT_NEED_UPDATE = EVENT_PAGE_TREE_SLOT_NEED_UPDATE;
exports.EVENT_PAGE_TREE_SLOT_SELECTED = EVENT_PAGE_TREE_SLOT_SELECTED;
exports.EVENT_PART_REFRESH_TREE_NODE = EVENT_PART_REFRESH_TREE_NODE;
exports.EVENT_PERSPECTIVE_ADDED = EVENT_PERSPECTIVE_ADDED;
exports.EVENT_PERSPECTIVE_CHANGED = EVENT_PERSPECTIVE_CHANGED;
exports.EVENT_PERSPECTIVE_REFRESHED = EVENT_PERSPECTIVE_REFRESHED;
exports.EVENT_PERSPECTIVE_UNLOADING = EVENT_PERSPECTIVE_UNLOADING;
exports.EVENT_PERSPECTIVE_UPDATED = EVENT_PERSPECTIVE_UPDATED;
exports.EVENT_SMARTEDIT_COMPONENT_UPDATED = EVENT_SMARTEDIT_COMPONENT_UPDATED;
exports.EVENT_STRICT_PREVIEW_MODE_REQUESTED = EVENT_STRICT_PREVIEW_MODE_REQUESTED;
exports.EVENT_THEME_CHANGED = EVENT_THEME_CHANGED;
exports.EVENT_THEME_SAVED = EVENT_THEME_SAVED;
exports.EXPERIENCE_STORAGE_KEY = EXPERIENCE_STORAGE_KEY;
exports.EXTENDED_VIEW_PORT_MARGIN = EXTENDED_VIEW_PORT_MARGIN;
exports.EXTENDED_VIEW_PORT_MARGIN_TOKEN = EXTENDED_VIEW_PORT_MARGIN_TOKEN;
exports.EditableListNodeItem = EditableListNodeItem;
exports.FILE_VALIDATION_CONFIG = FILE_VALIDATION_CONFIG;
exports.FrequentlyChangingContentName = FrequentlyChangingContentName;
exports.GENERIC_EDITOR_LOADED_EVENT = GENERIC_EDITOR_LOADED_EVENT;
exports.GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT;
exports.GENERIC_EDITOR_WIDGET_DATA = GENERIC_EDITOR_WIDGET_DATA;
exports.GatewayProxied = GatewayProxied;
exports.GatewayProxiedAnnotationFactory = GatewayProxiedAnnotationFactory;
exports.GenericEditorDropdownServiceFactory = GenericEditorDropdownServiceFactory;
exports.GenericEditorUtil = GenericEditorUtil;
exports.HEART_BEAT_TIMEOUT_THRESHOLD_MS = HEART_BEAT_TIMEOUT_THRESHOLD_MS;
exports.HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN = HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN;
exports.HIDE_SLOT_MENU = HIDE_SLOT_MENU;
exports.HIDE_TOOLBAR_ITEM_CONTEXT = HIDE_TOOLBAR_ITEM_CONTEXT;
exports.HasOperationPermissionBaseDirective = HasOperationPermissionBaseDirective;
exports.I18N_LANGUAGES_RESOURCE_URI = I18N_LANGUAGES_RESOURCE_URI;
exports.I18N_RESOURCE_URI = I18N_RESOURCE_URI;
exports.IAnnouncementService = IAnnouncementService;
exports.ICatalogDetailsService = ICatalogDetailsService;
exports.ICatalogService = ICatalogService;
exports.ICatalogVersionPermissionService = ICatalogVersionPermissionService;
exports.IConfirmationModalService = IConfirmationModalService;
exports.IContextualMenuService = IContextualMenuService;
exports.ID_ATTRIBUTE = ID_ATTRIBUTE;
exports.IDecoratorService = IDecoratorService;
exports.IDragAndDropCrossOrigin = IDragAndDropCrossOrigin;
exports.IDragEventType = IDragEventType;
exports.IEditorModalService = IEditorModalService;
exports.IExperienceService = IExperienceService;
exports.IFeatureService = IFeatureService;
exports.IFileValidation = IFileValidation;
exports.IGenericEditorDropdownServiceConstructor = IGenericEditorDropdownServiceConstructor;
exports.IIframeClickDetectionService = IIframeClickDetectionService;
exports.IMediaService = IMediaService;
exports.IMediaToUpload = IMediaToUpload;
exports.INotificationMouseLeaveDetectionService = INotificationMouseLeaveDetectionService;
exports.INotificationService = INotificationService;
exports.IPageInfoService = IPageInfoService;
exports.IPageService = IPageService;
exports.IPageTreeNodeService = IPageTreeNodeService;
exports.IPageTreeService = IPageTreeService;
exports.IPermissionService = IPermissionService;
exports.IPerspectiveService = IPerspectiveService;
exports.IPositionRegistry = IPositionRegistry;
exports.IPreviewService = IPreviewService;
exports.IRenderService = IRenderService;
exports.IResizeListener = IResizeListener;
exports.IRestServiceFactory = IRestServiceFactory;
exports.ISlotRestrictionsService = ISlotRestrictionsService;
exports.ISmartEditContractChangeListener = ISmartEditContractChangeListener;
exports.IStorageGateway = IStorageGateway;
exports.IStorageManager = IStorageManager;
exports.IStorageManagerFactory = IStorageManagerFactory;
exports.IStorageManagerGateway = IStorageManagerGateway;
exports.IStoragePropertiesService = IStoragePropertiesService;
exports.ISyncPollingService = ISyncPollingService;
exports.ITEM_COMPONENT_DATA_TOKEN = ITEM_COMPONENT_DATA_TOKEN;
exports.IToolbarService = IToolbarService;
exports.IToolbarServiceFactory = IToolbarServiceFactory;
exports.IUrlService = IUrlService;
exports.IUserTrackingService = IUserTrackingService;
exports.IWaitDialogService = IWaitDialogService;
exports.LANGUAGE_RESOURCE_URI = LANGUAGE_RESOURCE_URI;
exports.LINKED_DROPDOWN = LINKED_DROPDOWN;
exports.LINKED_DROPDOWN_TOKEN = LINKED_DROPDOWN_TOKEN;
exports.LOGOUT = LOGOUT;
exports.MEDIAS_PATH = MEDIAS_PATH;
exports.MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN = MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN;
exports.MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN = MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN;
exports.MEDIA_FILE_SELECTOR_CUSTOM_TOKEN = MEDIA_FILE_SELECTOR_CUSTOM_TOKEN;
exports.MEDIA_FOLDER_PATH = MEDIA_FOLDER_PATH;
exports.MEDIA_PATH = MEDIA_PATH;
exports.MEDIA_RESOURCE_URI = MEDIA_RESOURCE_URI;
exports.MEDIA_SELECTOR_I18N_KEY = MEDIA_SELECTOR_I18N_KEY;
exports.MEDIA_SELECTOR_I18N_KEY_TOKEN = MEDIA_SELECTOR_I18N_KEY_TOKEN;
exports.MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN = MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN;
exports.MUTATION_TYPES = MUTATION_TYPES;
exports.MessageGateway = MessageGateway;
exports.NG_ROUTE_PREFIX = NG_ROUTE_PREFIX;
exports.NG_ROUTE_WILDCARD = NG_ROUTE_WILDCARD;
exports.NONE_PERSPECTIVE = NONE_PERSPECTIVE;
exports.NamespacedStorageManager = NamespacedStorageManager;
exports.NavigationNodeItem = NavigationNodeItem;
exports.NodeUtils = NodeUtils;
exports.OPEN_PAGE_WORKFLOW_MENU = OPEN_PAGE_WORKFLOW_MENU;
exports.OPERATION_CONTEXT = OPERATION_CONTEXT;
exports.OVERLAY_COMPONENT_CLASS = OVERLAY_COMPONENT_CLASS;
exports.OVERLAY_DISABLED_EVENT = OVERLAY_DISABLED_EVENT;
exports.OVERLAY_ID = OVERLAY_ID;
exports.OVERLAY_RERENDERED_EVENT = OVERLAY_RERENDERED_EVENT;
exports.ObjectUtils = ObjectUtils;
exports.PAGES_CONTENT_SLOT_RESOURCE_URI = PAGES_CONTENT_SLOT_RESOURCE_URI;
exports.PAGE_CONTEXT_CATALOG = PAGE_CONTEXT_CATALOG;
exports.PAGE_CONTEXT_CATALOG_VERSION = PAGE_CONTEXT_CATALOG_VERSION;
exports.PAGE_CONTEXT_SITE_ID = PAGE_CONTEXT_SITE_ID;
exports.PAGE_TREE_NODE_CLASS = PAGE_TREE_NODE_CLASS;
exports.PAGE_TREE_PANEL_WIDTH_COOKIE_NAME = PAGE_TREE_PANEL_WIDTH_COOKIE_NAME;
exports.PAGE_TREE_SLOT_EXPANDED_EVENT = PAGE_TREE_SLOT_EXPANDED_EVENT;
exports.PERSPECTIVE_SELECTOR_WIDGET_KEY = PERSPECTIVE_SELECTOR_WIDGET_KEY;
exports.POPUP_OVERLAY_DATA = POPUP_OVERLAY_DATA;
exports.PREVIEW_RESOURCE_URI = PREVIEW_RESOURCE_URI;
exports.PREVIOUS_USERNAME_HASH = PREVIOUS_USERNAME_HASH;
exports.PRODUCT_LIST_RESOURCE_API = PRODUCT_LIST_RESOURCE_API;
exports.PRODUCT_RESOURCE_API = PRODUCT_RESOURCE_API;
exports.PageInfoService = PageInfoService;
exports.REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT = REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT;
exports.RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP = RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP;
exports.RICH_TEXT_CONFIGURATION = RICH_TEXT_CONFIGURATION;
exports.RarelyChangingContentName = RarelyChangingContentName;
exports.RichTextLoaderService = RichTextLoaderService;
exports.SCROLL_AREA_CLASS = SCROLL_AREA_CLASS;
exports.SEND_MOUSE_POSITION_THROTTLE = SEND_MOUSE_POSITION_THROTTLE;
exports.SETTINGS_URI = SETTINGS_URI;
exports.SHOW_SLOT_MENU = SHOW_SLOT_MENU;
exports.SHOW_TOOLBAR_ITEM_CONTEXT = SHOW_TOOLBAR_ITEM_CONTEXT;
exports.SITES_RESOURCE_URI = SITES_RESOURCE_URI;
exports.SMARTEDITCONTAINER_COMPONENT_NAME = SMARTEDITCONTAINER_COMPONENT_NAME;
exports.SMARTEDITLOADER_COMPONENT_NAME = SMARTEDITLOADER_COMPONENT_NAME;
exports.SMARTEDIT_ATTRIBUTE_PREFIX = SMARTEDIT_ATTRIBUTE_PREFIX;
exports.SMARTEDIT_COMPONENT_NAME = SMARTEDIT_COMPONENT_NAME;
exports.SMARTEDIT_COMPONENT_PROCESS_STATUS = SMARTEDIT_COMPONENT_PROCESS_STATUS;
exports.SMARTEDIT_DRAG_AND_DROP_EVENTS = SMARTEDIT_DRAG_AND_DROP_EVENTS;
exports.SMARTEDIT_ELEMENT_HOVERED = SMARTEDIT_ELEMENT_HOVERED;
exports.SMARTEDIT_IFRAME_DRAG_AREA = SMARTEDIT_IFRAME_DRAG_AREA;
exports.SMARTEDIT_IFRAME_WRAPPER_ID = SMARTEDIT_IFRAME_WRAPPER_ID;
exports.SMARTEDIT_INNER_FILES = SMARTEDIT_INNER_FILES;
exports.SMARTEDIT_INNER_FILES_POST = SMARTEDIT_INNER_FILES_POST;
exports.SMARTEDIT_LOGIN_DIALOG_RESOURCES = SMARTEDIT_LOGIN_DIALOG_RESOURCES;
exports.SMARTEDIT_RESOURCE_URI_REGEXP = SMARTEDIT_RESOURCE_URI_REGEXP;
exports.SMARTEDIT_ROOT = SMARTEDIT_ROOT;
exports.SSO_AUTHENTICATION_ENTRY_POINT = SSO_AUTHENTICATION_ENTRY_POINT;
exports.SSO_LOGOUT_ENTRY_POINT = SSO_LOGOUT_ENTRY_POINT;
exports.SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT;
exports.STORAGE_PROPERTIES_TOKEN = STORAGE_PROPERTIES_TOKEN;
exports.STORE_FRONT_CONTEXT = STORE_FRONT_CONTEXT;
exports.ScriptUtils = ScriptUtils;
exports.SeCustomComponent = SeCustomComponent;
exports.SeEntryModule = SeEntryModule;
exports.SeRouteService = SeRouteService;
exports.SmarteditErrorHandler = SmarteditErrorHandler;
exports.StorageManagerFactory = StorageManagerFactory;
exports.StorageNamespaceConverter = StorageNamespaceConverter;
exports.StringUtils = StringUtils;
exports.TAB_DATA = TAB_DATA;
exports.THROTTLE_SCROLLING_DELAY = THROTTLE_SCROLLING_DELAY;
exports.TOOLBAR_ITEM = TOOLBAR_ITEM;
exports.TREE_NODE = TREE_NODE;
exports.TYPES_RESOURCE_URI = TYPES_RESOURCE_URI;
exports.TYPE_ATTRIBUTE = TYPE_ATTRIBUTE;
exports.TextTruncateService = TextTruncateService;
exports.Timer = Timer;
exports.TreeDragAndDropEvent = TreeDragAndDropEvent;
exports.TreeNestedDataSource = TreeNestedDataSource;
exports.TreeNodeItem = TreeNodeItem;
exports.TruncatedText = TruncatedText;
exports.USER_TRACKING_FUNCTIONALITY = USER_TRACKING_FUNCTIONALITY;
exports.USER_TRACKING_KEY_MAP = USER_TRACKING_KEY_MAP;
exports.UUID_ATTRIBUTE = UUID_ATTRIBUTE;
exports.VALIDATION_MESSAGE_TYPES = VALIDATION_MESSAGE_TYPES;
exports.WHO_AM_I_RESOURCE_URI = WHO_AM_I_RESOURCE_URI;
exports.WIZARD_API = WIZARD_API;
exports.WIZARD_MANAGER = WIZARD_MANAGER;
exports.WizardService = WizardService;
exports.YJQUERY_TOKEN = YJQUERY_TOKEN;
exports.apiUtils = apiUtils;
exports.authorizationEvictionTag = authorizationEvictionTag;
exports.catalogEvictionTag = catalogEvictionTag;
exports.catalogSyncedEvictionTag = catalogSyncedEvictionTag;
exports.cmsitemsEvictionTag = cmsitemsEvictionTag;
exports.cmsitemsUri = cmsitemsUri;
exports.contentCatalogUpdateEvictionTag = contentCatalogUpdateEvictionTag;
exports.dateUtils = dateUtils;
exports.diNameUtils = diNameUtils;
exports.frequentlyChangingContent = frequentlyChangingContent;
exports.genericEditorDropdownComponentOnInit = genericEditorDropdownComponentOnInit;
exports.getLocalizedFilterFn = getLocalizedFilterFn;
exports.isBlankValidator = isBlankValidator;
exports.moduleUtils = moduleUtils;
exports.nodeUtils = nodeUtils;
exports.objectToArray = objectToArray;
exports.objectUtils = objectUtils;
exports.operationContextCMSPredicate = operationContextCMSPredicate;
exports.operationContextInteractivePredicate = operationContextInteractivePredicate;
exports.operationContextNonInteractivePredicate = operationContextNonInteractivePredicate;
exports.operationContextToolingPredicate = operationContextToolingPredicate;
exports.pageChangeEvictionTag = pageChangeEvictionTag;
exports.pageCreationEvictionTag = pageCreationEvictionTag;
exports.pageDeletionEvictionTag = pageDeletionEvictionTag;
exports.pageEvictionTag = pageEvictionTag;
exports.pageRestoredEvictionTag = pageRestoredEvictionTag;
exports.pageUpdateEvictionTag = pageUpdateEvictionTag;
exports.parseComponentSelector = parseComponentSelector;
exports.parseValidationMessage = parseValidationMessage;
exports.perspectiveChangedEvictionTag = perspectiveChangedEvictionTag;
exports.rarelyChangingContent = rarelyChangingContent;
exports.registerCustomComponents = registerCustomComponents;
exports.scriptUtils = scriptUtils;
exports.seCustomComponents = seCustomComponents;
exports.stringUtils = stringUtils;
exports.userEvictionTag = userEvictionTag;
exports.windowUtils = windowUtils;
exports.yjQueryServiceFactory = yjQueryServiceFactory;
