'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@angular/core');
var smarteditcommons = require('smarteditcommons');
var http = require('@angular/common/http');
var lo = require('lodash');
var rxjs = require('rxjs');
var operators = require('rxjs/operators');
var common = require('@angular/common');
var router = require('@angular/router');
var core$2 = require('@ngx-translate/core');
var core$1 = require('@fundamental-ngx/core');
var forms = require('@angular/forms');
var moment = require('moment');
var platformBrowser = require('@angular/platform-browser');
var animations$1 = require('@angular/platform-browser/animations');
var gridList = require('@fundamental-ngx/core/grid-list');
var animations = require('@angular/animations');
var form = require('@fundamental-ngx/core/form');
var link = require('@fundamental-ngx/core/link');
var list = require('@fundamental-ngx/core/list');

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

var lo__namespace = /*#__PURE__*/_interopNamespace(lo);
var moment__default = /*#__PURE__*/_interopDefaultLegacy(moment);

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

var /* @ngInject */ ThemeSwitchService_1;
/* @internal */
/* @ngInject */ exports.ThemeSwitchService = /* @ngInject */ ThemeSwitchService_1 = class /* @ngInject */ ThemeSwitchService {
    constructor(alertService, _themesService, restServiceFactory, crossFrameEventService) {
        this.alertService = alertService;
        this._themesService = _themesService;
        this.restServiceFactory = restServiceFactory;
        this.crossFrameEventService = crossFrameEventService;
        this.sessionStorage = window.sessionStorage;
        this.themeRestService = this.restServiceFactory.get(smarteditcommons.CURRENT_USER_THEME_URI);
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.REAUTH_STARTED, () => {
            this.selectedTheme = /* @ngInject */ ThemeSwitchService_1.lightTheme;
            this.crossFrameEventService.publish(smarteditcommons.EVENT_THEME_CHANGED);
        });
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.LOGOUT, () => {
            this.selectedTheme = /* @ngInject */ ThemeSwitchService_1.lightTheme;
            this.crossFrameEventService.publish(smarteditcommons.EVENT_THEME_CHANGED);
        });
    }
    getCurrentUserTheme() {
        return __awaiter(this, void 0, void 0, function* () {
            return (yield this.themeRestService.get()).code;
        });
    }
    selectTheme(selectedTheme) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!selectedTheme) {
                this.selectedTheme = yield this.getCurrentUserTheme();
                this.setThemeSession();
            }
            this.crossFrameEventService.publish(smarteditcommons.EVENT_THEME_CHANGED);
            selectedTheme = selectedTheme ? selectedTheme : this.getThemeSession();
            this.selectedTheme = selectedTheme;
            this.cssUrl = this._themesService.setTheme(selectedTheme);
            this.cssCustomUrl = this._themesService.setCustomTheme(selectedTheme);
            /* @ngInject */ ThemeSwitchService_1.cssStringUrl = `/${this.cssUrl.changingThisBreaksApplicationSecurity}`;
            /* @ngInject */ ThemeSwitchService_1.cssCustomStringUrl = `/${this.cssCustomUrl.changingThisBreaksApplicationSecurity}`;
        });
    }
    setThemeSession() {
        if (this.selectedTheme) {
            this.sessionStorage.setItem('theme', this.selectedTheme);
        }
    }
    saveTheme() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.restServiceFactory.get(smarteditcommons.CURRENT_USER_THEME_URI, 'code').update({
                    code: this.selectedTheme
                });
                this.setThemeSession();
                this.crossFrameEventService.publish(smarteditcommons.EVENT_THEME_SAVED);
                return Promise.resolve();
            }
            catch (error) {
                this.selectTheme(this.getThemeSession());
                this.generateAndAlertErrorMessage();
                return Promise.reject();
            }
        });
    }
    getThemeSession() {
        if (this.sessionStorage.getItem('theme') &&
            this.sessionStorage.getItem('theme') !== 'undefined') {
            return this.sessionStorage.getItem('theme');
        }
        else {
            return null;
        }
    }
    generateAndAlertErrorMessage() {
        this.alertService.showDanger({
            message: 'se.cms.theme.save.error',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }
};
/* @ngInject */ exports.ThemeSwitchService.lightTheme = 'sap_fiori_3';
/* @ngInject */ exports.ThemeSwitchService = /* @ngInject */ ThemeSwitchService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IAlertService,
        smarteditcommons.ThemesService,
        smarteditcommons.RestServiceFactory,
        smarteditcommons.CrossFrameEventService])
], /* @ngInject */ exports.ThemeSwitchService);

var ApplicationLayer;
(function (ApplicationLayer) {
    ApplicationLayer[ApplicationLayer["SMARTEDITCONTAINER"] = 0] = "SMARTEDITCONTAINER";
    ApplicationLayer[ApplicationLayer["SMARTEDIT"] = 1] = "SMARTEDIT";
})(ApplicationLayer || (ApplicationLayer = {}));
/** @internal */
/* @ngInject */ exports.ConfigurationExtractorService = class /* @ngInject */ ConfigurationExtractorService {
    constructor(logService) {
        this.logService = logService;
    }
    extractSEContainerModules(configurations) {
        return this._getAppsAndLocations(configurations, ApplicationLayer.SMARTEDITCONTAINER);
    }
    extractSEModules(configurations) {
        return this._getAppsAndLocations(configurations, ApplicationLayer.SMARTEDIT);
    }
    orderApplications(applications) {
        const simpleApps = applications.filter((item) => !item.extends);
        const extendingApps = applications
            .filter((item) => !!item.extends)
            /*
             * filer out extendingApps thata do extend an unknown app
             * other recursive _addExtendingAppsInOrder will never end
             */
            .filter((extendingApp) => {
            const index = applications.findIndex((item) => item.name === extendingApp.extends);
            if (index === -1) {
                this.logService.error(`Application ${extendingApp.name} located at ${extendingApp.location} is ignored because it extends an unknown application '${extendingApp.extends}'; SmartEdit functionality may be compromised.`);
            }
            return index > -1;
        });
        return this._addExtendingAppsInOrder(simpleApps, extendingApps);
    }
    _addExtendingAppsInOrder(simpleApps, extendingApps, pass) {
        pass = pass || 1;
        const remainingApps = [];
        extendingApps.forEach((extendingApp) => {
            const index = simpleApps.findIndex((item) => item.name === extendingApp.extends);
            if (index > -1) {
                this.logService.debug(`pass ${pass}, ${extendingApp.name} requiring ${extendingApp.extends} found it at index ${index} (${simpleApps.map((app) => app.name)})`);
                simpleApps.splice(index + 1, 0, extendingApp);
            }
            else {
                this.logService.debug(`pass ${pass}, ${extendingApp.name} requiring ${extendingApp.extends} did not find it  (${simpleApps.map((app) => app.name)})`);
                remainingApps.push(extendingApp);
            }
        });
        if (remainingApps.length) {
            return this._addExtendingAppsInOrder(simpleApps, remainingApps, ++pass);
        }
        else {
            return simpleApps;
        }
    }
    _getAppsAndLocations(configurations, applicationLayer) {
        let locationName;
        switch (applicationLayer) {
            case ApplicationLayer.SMARTEDITCONTAINER:
                locationName = 'smartEditContainerLocation';
                break;
            case ApplicationLayer.SMARTEDIT:
                locationName = 'smartEditLocation';
                break;
        }
        const appsAndLocations = lo__namespace
            .map(configurations, (value, prop) => ({ key: prop, value }))
            .reduce((holder, current) => {
            if (current.key.indexOf('applications') === 0 &&
                typeof current.value[locationName] === 'string') {
                const app = {};
                app.name = current.key.split('.')[1];
                const location = current.value[locationName];
                if (/^https?\:\/\//.test(location)) {
                    app.location = location;
                }
                else {
                    app.location = configurations.domain + location;
                }
                const _extends = current.value.extends;
                if (_extends) {
                    app.extends = _extends;
                }
                holder.applications.push(app);
                // authenticationMaps from smartedit modules
                holder.authenticationMap = lo__namespace.merge(holder.authenticationMap, current.value.authenticationMap);
            }
            else if (current.key === 'authenticationMap') {
                // authenticationMap from smartedit
                holder.authenticationMap = lo__namespace.merge(holder.authenticationMap, current.value);
            }
            return holder;
        }, {
            applications: [],
            authenticationMap: {}
        });
        return appsAndLocations;
    }
};
/* @ngInject */ exports.ConfigurationExtractorService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService])
], /* @ngInject */ exports.ConfigurationExtractorService);

exports.BootstrapService = class BootstrapService {
    constructor(configurationExtractorService, sharedDataService, logService, httpClient, promiseUtils, smarteditBootstrapGateway, moduleUtils, SMARTEDIT_INNER_FILES, SMARTEDIT_INNER_FILES_POST) {
        this.configurationExtractorService = configurationExtractorService;
        this.sharedDataService = sharedDataService;
        this.logService = logService;
        this.httpClient = httpClient;
        this.promiseUtils = promiseUtils;
        this.smarteditBootstrapGateway = smarteditBootstrapGateway;
        this.moduleUtils = moduleUtils;
        this.SMARTEDIT_INNER_FILES = SMARTEDIT_INNER_FILES;
        this.SMARTEDIT_INNER_FILES_POST = SMARTEDIT_INNER_FILES_POST;
    }
    bootstrapContainerModules(configurations) {
        const deferred = this.promiseUtils.defer();
        const seContainerModules = this.configurationExtractorService.extractSEContainerModules(configurations);
        const orderedApplications = this.configurationExtractorService.orderApplications(seContainerModules.applications);
        this.logService.debug('outerAppLocations are:', orderedApplications);
        this.sharedDataService.set('authenticationMap', seContainerModules.authenticationMap);
        this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);
        const constants = this._getConstants(configurations);
        this._getValidApplications(orderedApplications).then((validApplications) => {
            smarteditcommons.scriptUtils.injectJS().execute({
                srcs: validApplications.map((app) => app.location),
                callback: () => {
                    const modules = [...window.__smartedit__.pushedModules];
                    // The original validApplications contains the list of modules that must be loaded dynamically as determined by
                    // the SmartEdit Configuration service; this is the legacy way of loading extensions.
                    // However, now extensions can also be loaded at compilation time. The code of those extensions is not required to be
                    // loaded dynamically, but it's still necessary to load their Angular top-level module. Those modules are defined in
                    // the smartEditContainerAngularApps global variable.
                    window.__smartedit__.smartEditContainerAngularApps.forEach((appName) => {
                        validApplications.push({
                            name: appName,
                            location: ''
                        });
                    });
                    validApplications.forEach((app) => {
                        const esModule = this.moduleUtils.getNgModule(app.name);
                        if (esModule) {
                            modules.push(esModule);
                        }
                    });
                    deferred.resolve({
                        modules,
                        constants
                    });
                }
            });
        });
        return deferred.promise;
    }
    /**
     * Retrieve SmartEdit inner application configuration and dispatch 'bundle' event with list of resources.
     * @param configurations
     */
    bootstrapSEApp(configurations) {
        const seModules = this.configurationExtractorService.extractSEModules(configurations);
        const orderedApplications = this.configurationExtractorService.orderApplications(seModules.applications);
        this.sharedDataService.set('authenticationMap', seModules.authenticationMap);
        this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);
        const resources = {
            properties: this._getConstants(configurations),
            js: [
                {
                    src: configurations.smarteditroot +
                        '/static-resources/thirdparties/blockumd/blockumd.js'
                },
                {
                    src: configurations.smarteditroot +
                        '/static-resources/dist/smartedit-new/vendors.js'
                },
                {
                    src: configurations.smarteditroot +
                        '/static-resources/thirdparties/ckeditor/ckeditor.js'
                },
                {
                    src: configurations.smarteditroot +
                        '/static-resources/dist/smartedit-new/smartedit.js'
                }
            ],
            css: [
                configurations.smarteditroot + exports.ThemeSwitchService.cssStringUrl,
                configurations.smarteditroot + exports.ThemeSwitchService.cssCustomStringUrl,
                configurations.smarteditroot + '/static-resources/dist/smartedit-new/main.css'
            ]
        };
        return this._getValidApplications(orderedApplications).then((validApplications) => {
            // The original validApplications contains the list of modules that must be loaded dynamically as determined by
            // the SmartEdit Configuration service; this is the legacy way of loading extensions.
            // However, now extensions can also be loaded at compilation time. The code of those extensions is not required to be
            // loaded dynamically, but it's still necessary to load their Angular top-level module. Those modules are defined in
            // the smartEditInnerAngularApps global variable.
            window.__smartedit__.smartEditInnerAngularApps.forEach((appName) => {
                validApplications.push({
                    name: appName,
                    location: ''
                });
            });
            if (E2E_ENVIRONMENT && this.SMARTEDIT_INNER_FILES) {
                // Note: This is only enabled on e2e tests. In production, this is removed by webpack.
                resources.js = this.SMARTEDIT_INNER_FILES.map((filePath) => ({
                    src: configurations.domain + filePath
                }));
            }
            resources.js = resources.js.concat(validApplications.map((app) => {
                const source = { src: app.location };
                return source;
            }));
            if (E2E_ENVIRONMENT && this.SMARTEDIT_INNER_FILES_POST) {
                // Note: This is only enabled on e2e tests. In production, this is removed by webpack.
                resources.js = resources.js.concat(this.SMARTEDIT_INNER_FILES_POST.map((filePath) => ({
                    src: configurations.domain + filePath
                })));
            }
            resources.properties.applications = validApplications.map((app) => app.name);
            this.smarteditBootstrapGateway
                .getInstance()
                .publish('bundle', { resources });
        });
    }
    _getConstants(configurations) {
        return {
            domain: configurations.domain,
            smarteditroot: configurations.smarteditroot
        };
    }
    /**
     * Applications are considered valid if they can be retrieved over the wire
     */
    _getValidApplications(applications) {
        return Promise.all(applications.map((application) => {
            const deferred = this.promiseUtils.defer();
            this.httpClient.get(application.location, { responseType: 'text' }).subscribe(() => {
                deferred.resolve(application);
            }, (e) => {
                this.logService.error(`Failed to load application '${application.name}' from location ${application.location}; SmartEdit functionality may be compromised.`);
                deferred.resolve();
            });
            return deferred.promise;
        })).then((validApplications) => lo__namespace.merge(lo__namespace.compact(validApplications)));
    }
};
exports.BootstrapService = __decorate([
    __param(7, core.Inject('SMARTEDIT_INNER_FILES')),
    __param(8, core.Inject('SMARTEDIT_INNER_FILES_POST')),
    __metadata("design:paramtypes", [exports.ConfigurationExtractorService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.LogService,
        http.HttpClient,
        smarteditcommons.PromiseUtils,
        smarteditcommons.SmarteditBootstrapGateway,
        smarteditcommons.ModuleUtils, Array, Array])
], exports.BootstrapService);

/**
 * LoadConfigManagerService is used to retrieve configurations stored in configuration API.
 */
exports.LoadConfigManagerService = class LoadConfigManagerService {
    constructor(restServicefactory, sharedDataService, logService, promiseUtils, SMARTEDIT_RESOURCE_URI_REGEXP, SMARTEDIT_ROOT) {
        this.sharedDataService = sharedDataService;
        this.logService = logService;
        this.promiseUtils = promiseUtils;
        this.SMARTEDIT_RESOURCE_URI_REGEXP = SMARTEDIT_RESOURCE_URI_REGEXP;
        this.SMARTEDIT_ROOT = SMARTEDIT_ROOT;
        this.restService = restServicefactory.get(smarteditcommons.CONFIGURATION_URI);
    }
    /**
     * Retrieves configuration from an API and returns as an array of mapped key/value pairs.
     *
     * ### Example:
     *
     *      loadConfigManagerService.loadAsArray().then(
     *          (response: ConfigurationItem[]) => {
     *              this._prettify(response);
     *          }));
     *
     *
     *
     * @returns  a promise of configuration values as an array of mapped configuration key/value pairs
     */
    loadAsArray() {
        const deferred = this.promiseUtils.defer();
        this.restService.query().then((response) => {
            deferred.resolve(this._parse(response));
        }, (error) => {
            this.logService.log('Fail to load the configurations.', error);
            deferred.reject();
        });
        return deferred.promise;
    }
    /**
     * Retrieves a configuration from the API and converts it to an object.
     *
     * ### Example:
     *
     *
     *      loadConfigManagerService.loadAsObject().then((conf: ConfigurationObject) => {
     *          sharedDataService.set('defaultToolingLanguage', conf.defaultToolingLanguage);
     *      });
     *
     * @returns a promise of configuration values as an object of mapped configuration key/value pairs
     */
    loadAsObject() {
        const deferred = this.promiseUtils.defer();
        this.loadAsArray().then((response) => {
            try {
                const conf = this._convertToObject(response);
                this.sharedDataService.set('configuration', conf);
                deferred.resolve(conf);
            }
            catch (e) {
                this.logService.error('LoadConfigManager.loadAsObject - _convertToObject failed to load configuration:', response);
                this.logService.error(e);
                deferred.reject();
            }
        });
        return deferred.promise;
    }
    _convertToObject(configuration) {
        const configurations = configuration.reduce((previous, current) => {
            try {
                if (typeof previous[current.key] !== 'undefined') {
                    this.logService.error('LoadConfigManager._convertToObject() - duplicate configuration keys found: ' +
                        current.key);
                }
                previous[current.key] = JSON.parse(current.value);
            }
            catch (parseError) {
                this.logService.error('item _key_ from configuration contains unparsable JSON data _value_ and was ignored'
                    .replace('_key_', current.key)
                    .replace('_value_', current.value));
            }
            return previous;
        }, {});
        try {
            configurations.domain = this.SMARTEDIT_RESOURCE_URI_REGEXP.exec(this._getLocation())[1];
        }
        catch (e) {
            throw new Error(`location ${this._getLocation()} doesn't match the expected pattern ${this.SMARTEDIT_RESOURCE_URI_REGEXP}`);
        }
        configurations.smarteditroot = configurations.domain + '/' + this.SMARTEDIT_ROOT;
        return configurations;
    }
    _getLocation() {
        return document.location.href;
    }
    // FIXME: weird on an array and useless
    _parse(configuration) {
        const conf = lo__namespace.cloneDeep(configuration);
        Object.keys(conf).forEach((key) => {
            try {
                conf[key] = JSON.parse(conf[key]);
            }
            catch (e) {
                //
            }
        });
        return conf;
    }
};
exports.LoadConfigManagerService = __decorate([
    smarteditcommons.OperationContextRegistered(smarteditcommons.CONFIGURATION_URI, 'TOOLING'),
    __param(4, core.Inject('SMARTEDIT_RESOURCE_URI_REGEXP')),
    __param(5, core.Inject('SMARTEDIT_ROOT')),
    __metadata("design:paramtypes", [smarteditcommons.RestServiceFactory,
        smarteditcommons.ISharedDataService,
        smarteditcommons.LogService,
        smarteditcommons.PromiseUtils,
        RegExp, String])
], exports.LoadConfigManagerService);

const ANNOUNCEMENT_DEFAULTS = {
    timeout: 20000,
    closeable: true
};
let /* @ngInject */ AnnouncementService = class /* @ngInject */ AnnouncementService extends smarteditcommons.IAnnouncementService {
    constructor(logService) {
        super();
        this.logService = logService;
        this.announcements$ = new rxjs.BehaviorSubject([]);
    }
    showAnnouncement(announcementConfig) {
        this.validateAnnouncementConfig(announcementConfig);
        const announcement = lo.clone(announcementConfig);
        announcement.id = smarteditcommons.stringUtils.encode(announcementConfig);
        announcement.timeout =
            !!announcement.timeout && announcement.timeout > 0
                ? announcement.timeout
                : ANNOUNCEMENT_DEFAULTS.timeout;
        if (announcement.timeout > 0) {
            announcement.timer = setTimeout(() => {
                this._closeAnnouncement(announcement);
            }, announcement.timeout);
        }
        this.announcements$.next([...this.announcements$.getValue(), announcement]);
        return Promise.resolve(announcement.id);
    }
    getAnnouncements() {
        return this.announcements$.asObservable();
    }
    closeAnnouncement(announcementId) {
        const announcement = this.announcements$
            .getValue()
            .find((announcementObj) => announcementObj.id === announcementId);
        if (announcement) {
            this._closeAnnouncement(announcement);
        }
        return Promise.resolve();
    }
    _closeAnnouncement(announcement) {
        if (announcement.timer) {
            clearTimeout(announcement.timer);
        }
        const announcements = this.announcements$.getValue();
        const newAnnouncements = announcements.filter((announcementObj) => announcementObj.id !== announcement.id);
        this.announcements$.next(newAnnouncements);
    }
    validateAnnouncementConfig(announcementConfig) {
        const { message, component } = announcementConfig;
        if (!message && !component) {
            this.logService.warn('AnnouncementService.validateAnnouncementConfig - announcement must contain at least a message or component property', announcementConfig);
        }
        if (message && component) {
            throw new Error('AnnouncementService.validateAnnouncementConfig - either message or component must be provided');
        }
    }
};
/* @ngInject */ AnnouncementService = __decorate([
    smarteditcommons.GatewayProxied('showAnnouncement', 'closeAnnouncement'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService])
], /* @ngInject */ AnnouncementService);

/**
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
let NotificationService = class NotificationService {
    constructor(systemEventService, logService) {
        this.systemEventService = systemEventService;
        this.logService = logService;
        this.notificationsChangeAction = new rxjs.BehaviorSubject(undefined);
        this.notifications = new rxjs.BehaviorSubject([]);
        this.initNotificationsChangeAction();
    }
    ngOnDestroy() {
        this.notifications.unsubscribe();
        this.notificationsChangeAction.unsubscribe();
    }
    pushNotification(configuration) {
        const action = {
            type: "PUSH" /* PUSH */,
            payload: configuration
        };
        this.notificationsChangeAction.next(action);
        return Promise.resolve();
    }
    removeNotification(notificationId) {
        const action = {
            type: "REMOVE" /* REMOVE */,
            payload: {
                id: notificationId
            }
        };
        this.notificationsChangeAction.next(action);
        return Promise.resolve();
    }
    removeAllNotifications() {
        const action = {
            type: "REMOVE_ALL" /* REMOVE_ALL */
        };
        this.notificationsChangeAction.next(action);
        return Promise.resolve();
    }
    isNotificationDisplayed(notificationId) {
        return !!this.getNotification(notificationId);
    }
    getNotification(notificationId) {
        return this.notifications
            .getValue()
            .find((notification) => notification.id === notificationId);
    }
    getNotifications() {
        return this.notifications.asObservable();
    }
    initNotificationsChangeAction() {
        this.notificationsChangeAction
            .pipe(operators.distinctUntilChanged((_, action) => this.emitWhenActionIsAvailable(action)), 
        // Skip first emission with "undefined" value.
        // First "undefined" is needed for invoking distinctUntilChanged (which requires at least 2 values emited) when first notification is added.
        operators.skip(1), operators.map((action) => this.resolveNotifications(action)))
            .subscribe((notifications) => {
            this.notifications.next(notifications);
            this.systemEventService.publishAsync(smarteditcommons.EVENT_NOTIFICATION_CHANGED);
        });
    }
    /**
     * Meant for case when a user has quickly pressed ESC key multiple times.
     * There might be some delay when adding / removing a notification because these methods are called in async context.
     * This may lead to the situation where notification has not yet been removed, but ESC key has called the pushNotification.
     *
     * @returns false (emit), true (do not emit)
     */
    emitWhenActionIsAvailable(action) {
        const { payload: newNotification } = action;
        const notification = (action.type === "PUSH" /* PUSH */ ||
            action.type === "REMOVE" /* REMOVE */) &&
            this.getNotification(newNotification.id);
        switch (action.type) {
            case "PUSH" /* PUSH */:
                if (notification) {
                    this.logService.debug(`Notification already exists for id:"${newNotification.id}"`);
                    return true;
                }
                return false;
            case "REMOVE" /* REMOVE */:
                if (!notification) {
                    this.logService.debug(`Attempt to remove a non existing notification for id:"${newNotification.id}"`);
                    return true;
                }
                return false;
            case "REMOVE_ALL" /* REMOVE_ALL */:
            default:
                return false;
        }
    }
    resolveNotifications(action) {
        const { payload: newNotification } = action;
        switch (action.type) {
            case "PUSH" /* PUSH */:
                return [...this.notifications.getValue(), newNotification];
            case "REMOVE" /* REMOVE */:
                return this.notifications
                    .getValue()
                    .filter((notification) => notification.id !== newNotification.id);
            case "REMOVE_ALL" /* REMOVE_ALL */:
            default:
                return [];
        }
    }
};
NotificationService = __decorate([
    smarteditcommons.GatewayProxied('pushNotification', 'removeNotification', 'removeAllNotifications'),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService, smarteditcommons.LogService])
], NotificationService);

/**
 * This service makes it possible to track the mouse position to detect when it leaves the notification panel.
 * It is solely meant to be used with the notificationService.
 */
/** @internal */
let /* @ngInject */ NotificationMouseLeaveDetectionService = class /* @ngInject */ NotificationMouseLeaveDetectionService extends smarteditcommons.INotificationMouseLeaveDetectionService {
    constructor(document, windowUtils, logService) {
        super();
        this.document = document;
        this.windowUtils = windowUtils;
        this.logService = logService;
        this.notificationPanelBounds = null;
        this.mouseLeaveCallback = null;
        /*
         * We need to bind the function in order for it to execute within the service's
         * scope and store it to be able to un-register the listener.
         */
        this._onMouseMove = this._onMouseMove.bind(this);
    }
    startDetection(outerBounds, innerBounds, callback) {
        this.validateBounds(outerBounds);
        if (!callback) {
            throw new Error('Callback function is required');
        }
        this.notificationPanelBounds = outerBounds;
        this.mouseLeaveCallback = callback;
        this.document.addEventListener('mousemove', this._onMouseMove);
        if (innerBounds) {
            this.validateBounds(innerBounds);
            this._remoteStartDetection(innerBounds);
        }
        return Promise.resolve();
    }
    stopDetection() {
        this.document.removeEventListener('mousemove', this._onMouseMove);
        this.notificationPanelBounds = null;
        this.mouseLeaveCallback = null;
        if (this.windowUtils.getGatewayTargetFrame()) {
            this._remoteStopDetection().catch((reason) => {
                this.logService.debug(`NotificationMouseLeaveDetectionService - stopDetection: ${JSON.stringify(reason)}`);
            });
        }
        return Promise.resolve();
    }
    _callCallback() {
        this._getCallback().then((callback) => {
            if (callback) {
                callback();
            }
        });
        return Promise.resolve();
    }
    _getBounds() {
        return Promise.resolve(this.notificationPanelBounds);
    }
    _getCallback() {
        return Promise.resolve(this.mouseLeaveCallback);
    }
    validateBounds(bounds) {
        if (!bounds) {
            throw new Error('Bounds are required for mouse leave detection');
        }
        if (!bounds.hasOwnProperty('x')) {
            throw new Error('Bounds must contain the x coordinate');
        }
        if (!bounds.hasOwnProperty('y')) {
            throw new Error('Bounds must contain the y coordinate');
        }
        if (!bounds.hasOwnProperty('width')) {
            throw new Error('Bounds must contain the width dimension');
        }
        if (!bounds.hasOwnProperty('height')) {
            throw new Error('Bounds must contain the height dimension');
        }
    }
};
/* @ngInject */ NotificationMouseLeaveDetectionService = __decorate([
    smarteditcommons.GatewayProxied('stopDetection', '_remoteStartDetection', '_remoteStopDetection', '_callCallback'),
    core.Injectable(),
    __param(0, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Document,
        smarteditcommons.WindowUtils,
        smarteditcommons.LogService])
], /* @ngInject */ NotificationMouseLeaveDetectionService);

/*
 * internal service to proxy calls from inner RESTService to the outer restServiceFactory and the 'real' IRestService
 */
/** @internal */
exports.DelegateRestService = class DelegateRestService {
    constructor(restServiceFactory) {
        this.restServiceFactory = restServiceFactory;
    }
    delegateForSingleInstance(methodName, params, uri, identifier, metadataActivated, options) {
        const restService = this.restServiceFactory.get(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.getMethodForSingleInstance(methodName)(params, options);
    }
    delegateForArray(methodName, params, uri, identifier, metadataActivated, options) {
        const restService = this.restServiceFactory.get(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.getMethodForArray(methodName)(params, options);
    }
    delegateForPage(pageable, uri, identifier, metadataActivated, options) {
        const restService = this.restServiceFactory.get(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.page(pageable, options);
    }
    delegateForQueryByPost(payload, params, uri, identifier, metadataActivated, options) {
        const restService = this.restServiceFactory.get(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.queryByPost(payload, params, options);
    }
};
exports.DelegateRestService = __decorate([
    smarteditcommons.GatewayProxied(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory])
], exports.DelegateRestService);

/** @internal */
const DEVICE_ORIENTATIONS = [
    {
        orientation: 'vertical',
        key: 'se.deviceorientation.vertical.label',
        default: true
    },
    {
        orientation: 'horizontal',
        key: 'se.deviceorientation.horizontal.label'
    }
];

/** @internal */
const DEVICE_SUPPORTS = [
    {
        iconClass: 'sap-icon--iphone',
        type: 'phone',
        width: 480,
        name: 'Mobile Portrait'
    },
    {
        iconClass: 'sap-icon--iphone-2',
        type: 'wide-phone',
        width: 600,
        name: 'Mobile Landscape'
    },
    {
        iconClass: 'sap-icon--ipad',
        type: 'tablet',
        width: 700,
        name: 'Tablet Portrait'
    },
    {
        iconClass: 'sap-icon--ipad-2',
        type: 'wide-tablet',
        width: 1024,
        name: 'Tablet Landscape'
    },
    {
        iconClass: 'sap-icon--sys-monitor',
        type: 'desktop',
        width: 1200,
        name: 'Desktop'
    },
    {
        iconClass: 'hyicon hyicon-wide-screen',
        type: 'wide-desktop',
        width: '100%',
        default: true,
        name: 'Widescreen'
    }
];

/**
 * The iFrame Manager service provides methods to load the storefront into an iframe. The preview of the storefront can be loaded for a specified input homepage and a specified preview ticket. The iframe src attribute is updated with that information in order to display the storefront in SmartEdit.
 */
let IframeManagerService = class IframeManagerService {
    constructor(logService, httpClient, yjQuery, windowUtils, sharedDataService) {
        this.logService = logService;
        this.httpClient = httpClient;
        this.yjQuery = yjQuery;
        this.windowUtils = windowUtils;
        this.sharedDataService = sharedDataService;
    }
    /**
     * This method sets the current page location and stores it in the service. The storefront will be loaded with this location.
     *
     * @param URL Location to be stored
     */
    setCurrentLocation(location) {
        this.currentLocation = location;
    }
    getIframe() {
        return this.yjQuery(this.windowUtils.getSmarteditIframe());
    }
    isCrossOrigin() {
        return this.windowUtils.isCrossOrigin(this.currentLocation);
    }
    /**
     * This method loads the storefront within an iframe by setting the src attribute to the specified input URL.
     * If this method is called within the context of a new or updated experience, prior to the loading, it will check if the page exists.
     * If the pages does not exist (the server returns a 404 and a content-type:text/html), the user will be redirected to the homepage of the storefront. Otherwise,
     * the user will be redirected to the requested page for the experience.
     *
     * @param URL The URL of the storefront.
     * @param checkIfFailingHTML Boolean indicating if we need to check if the page call returns a 404
     * @param homepageInPreviewMode URL of the storefront homepage in preview mode if it's a new experience
     *
     */
    load(url, checkIfFailingHTML, pageInPreviewMode) {
        if (checkIfFailingHTML) {
            return this._getPageAsync(url).then(() => {
                this.getIframe().attr('src', url);
            }, (error) => {
                if (error.status === 404) {
                    this.getIframe().attr('src', pageInPreviewMode);
                    return;
                }
                this.logService.error(`IFrameManagerService.load - _getPageAsync failed with error ${error}`);
            });
        }
        else {
            this.logService.debug('iframeManagerService::load - loading storefront url:', url);
            this.getIframe().attr('src', url);
            return Promise.resolve();
        }
    }
    /**
     * This method loads the preview of the storefront for a specified input homepage URL or a page from the page list, and for a specified preview ticket.
     * This method will add '/cx-preview' as specified in configuration.storefrontPreviewRoute to the URI and append the preview ticket in the query string.
     * <br/>If it is an initial load, [load]{@link IframeManagerService#load} will be called with this modified homepage or page from page list.
     * <br/>If it is a subsequent call, the modified homepage will be called through Ajax to initialize the preview (storefront constraint) and then
     * [load]{@link IframeManagerService#load} will be called with the current location.
     *
     * @param homePageOrPageFromPageList The URL of the storefront homepage or a page from the page list for a given experience context.
     * @param  previewTicket The preview ticket.
     */
    loadPreview(homePageOrPageFromPageList, previewTicket) {
        this.windowUtils.setTrustedIframeDomain(homePageOrPageFromPageList);
        this.logService.debug('loading storefront iframe with preview ticket:', previewTicket);
        let promiseToResolve;
        if (!/.+\.html/.test(homePageOrPageFromPageList)) {
            // for testing purposes
            promiseToResolve = this._appendURISuffix(homePageOrPageFromPageList);
        }
        else {
            promiseToResolve = Promise.resolve(homePageOrPageFromPageList);
        }
        return promiseToResolve.then((previewURL) => {
            const pageInPreviewMode = previewURL +
                (previewURL.indexOf('?') === -1 ? '?' : '&') +
                'cmsTicketId=' +
                previewTicket;
            // If we don't have a current location, or the current location is the homePage or a page from page list, or the current location has a cmsTicketID
            if (this._mustLoadAsSuch(homePageOrPageFromPageList)) {
                return this.load(pageInPreviewMode);
            }
            else {
                const isCrossOrigin = this.isCrossOrigin();
                /*
                 * check failing HTML only if same origin to prevent CORS errors.
                 * if location to reload in new experience context is different from homepage, one will have to
                 * first load the home page in preview mode and then access the location without preview mode
                 */
                return (isCrossOrigin ? Promise.resolve({}) : this._getPageAsync(pageInPreviewMode)).then(() => 
                // FIXME: use gatewayProxy to load url from the inner
                this.load(this.currentLocation, !isCrossOrigin, pageInPreviewMode), (error) => this.logService.error('failed to load preview', error));
            }
        });
    }
    apply(deviceSupport, deviceOrientation) {
        let width;
        let height;
        let isVertical = true;
        if (deviceOrientation && deviceOrientation.orientation) {
            isVertical = deviceOrientation.orientation === 'vertical';
        }
        if (deviceSupport) {
            width = isVertical ? deviceSupport.width : deviceSupport.height;
            height = isVertical ? deviceSupport.height : deviceSupport.width;
            // hardcoded the name to default to remove the device skin
            this.getIframe()
                .removeClass()
                .addClass('device-' + (isVertical ? 'vertical' : 'horizontal') + ' device-default');
        }
        else {
            this.getIframe().removeClass();
        }
        this.getIframe().css({
            width: width || '100%',
            height: height || '100%',
            display: 'block',
            margin: 'auto'
        });
    }
    applyDefault() {
        const defaultDeviceSupport = DEVICE_SUPPORTS.find((deviceSupport) => deviceSupport.default);
        const defaultDeviceOrientation = DEVICE_ORIENTATIONS.find((deviceOrientation) => deviceOrientation.default);
        this.apply(defaultDeviceSupport, defaultDeviceOrientation);
    }
    /*
     * if currentLocation is not set yet, it means that this is a first loading and we are trying to load the homepage,
     * or if the page has a ticket ID but is not the homepage, it means that we try to load a page from the page list.
     * For those scenarios, we want to load the page as such in preview mode.
     */
    _mustLoadAsSuch(homePageOrPageFromPageList) {
        return (!this.currentLocation ||
            smarteditcommons.urlUtils.getURI(homePageOrPageFromPageList) === smarteditcommons.urlUtils.getURI(this.currentLocation) ||
            'cmsTicketId' in smarteditcommons.urlUtils.parseQuery(this.currentLocation));
    }
    _getPageAsync(url) {
        return this.httpClient.get(url, { observe: 'body', responseType: 'text' }).toPromise();
    }
    _appendURISuffix(url) {
        const pair = url.split('?');
        return this.sharedDataService
            .get('configuration')
            .then((configuration) => {
            if (!configuration || !configuration.storefrontPreviewRoute) {
                this.logService.debug("SmartEdit configuration for 'storefrontPreviewRoute' is not found. Fallback to default value: '" +
                    IframeManagerService.DEFAULT_PREVIEW_ROUTE +
                    "'");
                return IframeManagerService.DEFAULT_PREVIEW_ROUTE;
            }
            return configuration.storefrontPreviewRoute;
        })
            .then((previewRoute) => pair[0]
            .replace(/(.+)([^\/])$/g, '$1$2/' + previewRoute)
            .replace(/(.+)\/$/g, '$1/' + previewRoute) +
            (pair.length === 2 ? '?' + pair[1] : ''));
    }
};
IframeManagerService.DEFAULT_PREVIEW_ROUTE = 'cx-preview';
IframeManagerService = __decorate([
    __param(2, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        http.HttpClient, Function, smarteditcommons.WindowUtils,
        smarteditcommons.ISharedDataService])
], IframeManagerService);

/**
 * Polyfill for HTML5 Drag and Drop in a cross-origin setup.
 * Most browsers (except Firefox) do not allow on-page drag-and-drop from non-same-origin frames.
 * This service is a polyfill to allow it, by listening the 'dragover' event over a sibling <div> of the iframe and sending the mouse position to the inner frame.
 * The inner frame 'DragAndDropCrossOriginInner' will use document.elementFromPoint (or isPointOverElement helper function for IE only) to determine the current hovered element and then dispatch drag events onto elligible droppable elements.
 *
 * More information about security restrictions:
 * https://bugs.chromium.org/p/chromium/issues/detail?id=251718
 * https://bugs.chromium.org/p/chromium/issues/detail?id=59081
 * https://www.infosecurity-magazine.com/news/new-google-chrome-clickjacking-vulnerability/
 * https://bugzilla.mozilla.org/show_bug.cgi?id=605991
 */
/** @internal */
let DragAndDropCrossOrigin = class DragAndDropCrossOrigin extends smarteditcommons.IDragAndDropCrossOrigin {
    constructor(yjQuery, crossFrameEventService, iframeManagerService) {
        super();
        this.yjQuery = yjQuery;
        this.crossFrameEventService = crossFrameEventService;
        this.iframeManagerService = iframeManagerService;
        this.onDragStart = () => {
            if (!this.isEnabled()) {
                return;
            }
            this.crossFrameEventService.publish(smarteditcommons.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_CROSS_ORIGIN_START);
            this.syncIframeDragArea()
                .show()
                .off('dragover') // `off()` is necessary since dragEnd event is not always fired.
                .on('dragover', (e) => {
                e.preventDefault(); // `preventDefault()` is necessary for the 'drop' event callback to be fired.
                const mousePosition = this.getPositionRelativeToIframe(e.pageX, e.pageY);
                this.throttledSendMousePosition(mousePosition);
                return false;
            })
                .off('drop')
                .on('drop', (e) => {
                e.preventDefault();
                e.stopPropagation();
                const mousePosition = this.getPositionRelativeToIframe(e.pageX, e.pageY);
                this.crossFrameEventService.publish(smarteditcommons.SMARTEDIT_DRAG_AND_DROP_EVENTS.DROP_ELEMENT, mousePosition);
                return false;
            });
        };
        this.onDragEnd = () => {
            if (!this.isEnabled()) {
                return;
            }
            this.getIframeDragArea().off('dragover').off('drop').hide();
        };
        this.sendMousePosition = (mousePosition) => {
            this.crossFrameEventService.publish(smarteditcommons.SMARTEDIT_DRAG_AND_DROP_EVENTS.TRACK_MOUSE_POSITION, mousePosition);
        };
    }
    initialize() {
        this.throttledSendMousePosition = lo__namespace.throttle(this.sendMousePosition, smarteditcommons.SEND_MOUSE_POSITION_THROTTLE);
        this.crossFrameEventService.subscribe(smarteditcommons.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, this.onDragStart);
        this.crossFrameEventService.subscribe(smarteditcommons.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, this.onDragEnd);
        this.crossFrameEventService.subscribe(smarteditcommons.DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.START, () => {
            if (this.isEnabled()) {
                this.iframeManagerService.getIframe().css('pointer-events', 'none');
            }
        });
        this.crossFrameEventService.subscribe(smarteditcommons.DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.END, () => {
            if (this.isEnabled()) {
                this.iframeManagerService.getIframe().css('pointer-events', 'auto');
            }
        });
    }
    isEnabled() {
        return this.iframeManagerService.isCrossOrigin();
    }
    getIframeDragArea() {
        return this.yjQuery('#' + smarteditcommons.SMARTEDIT_IFRAME_DRAG_AREA);
    }
    getPositionRelativeToIframe(posX, posY) {
        const iframeOffset = this.getIframeDragArea().offset();
        return {
            x: posX - iframeOffset.left,
            y: posY - iframeOffset.top
        };
    }
    syncIframeDragArea() {
        this.getIframeDragArea().width(this.iframeManagerService.getIframe().width());
        this.getIframeDragArea().height(this.iframeManagerService.getIframe().height());
        const iframeOffset = this.iframeManagerService.getIframe().offset();
        this.getIframeDragArea().css({
            top: iframeOffset.top,
            left: iframeOffset.left
        });
        return this.getIframeDragArea();
    }
};
DragAndDropCrossOrigin = __decorate([
    __param(0, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function, smarteditcommons.CrossFrameEventService,
        IframeManagerService])
], DragAndDropCrossOrigin);

/**
 * The Site Service fetches all sites configured on the hybris platform using REST calls to the cmswebservices sites API.
 */
let SiteService = class SiteService {
    constructor(restServiceFactory) {
        this.SITES_FOR_CATALOGS_URI = '/cmswebservices/v1/sites/catalogs';
        this.cache = null;
        this.siteRestService = restServiceFactory.get(smarteditcommons.SITES_RESOURCE_URI);
        this.sitesForCatalogsRestService = restServiceFactory.get(this.SITES_FOR_CATALOGS_URI);
    }
    /**
     * Fetches a list of sites for which user has at-least read access to one of the non-active catalog versions.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    getAccessibleSites() {
        return this.siteRestService.get({}).then((sitesDTO) => sitesDTO.sites);
    }
    /**
     * Fetches a list of sites configured for accessible sites. The list of sites fetched using REST calls through
     * the cmswebservices sites API.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    getSites() {
        //  Uses two REST API calls because of multicountry. The first call gives all the sites for which the user has permissions to.
        return this.getAccessibleSites().then((sites) => {
            const catalogIds = sites.reduce((catalogs, site) => [...(catalogs || []), ...site.contentCatalogs], []);
            // The call with catalogIds gives all the corresponding sites in the hierarchy.
            return this.sitesForCatalogsRestService
                .save({
                catalogIds
            })
                .then((allSites) => {
                this.cache = allSites.sites;
                return this.cache;
            });
        });
    }
    /**
     * Fetches a site, configured on the hybris platform, by its uid. The sites fetched using REST calls through
     * cmswebservices sites API.
     *
     * @param uid unique site ID
     * @returns A promise of [ISite]{@link ISite}.
     */
    getSiteById(uid) {
        return this.getSites().then((sites) => sites.find((site) => site.uid === uid));
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.authorizationEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], SiteService.prototype, "getAccessibleSites", null);
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.authorizationEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], SiteService.prototype, "getSites", null);
SiteService = __decorate([
    smarteditcommons.OperationContextRegistered('SITES_RESOURCE_URI', 'CMS'),
    __metadata("design:paramtypes", [smarteditcommons.RestServiceFactory])
], SiteService);

/** @internal */
let ExperienceService = class ExperienceService extends smarteditcommons.IExperienceService {
    constructor(seStorageManager, storagePropertiesService, logService, crossFrameEventService, siteService, previewService, catalogService, languageService, sharedDataService, iframeManagerService, routingService) {
        super();
        this.seStorageManager = seStorageManager;
        this.storagePropertiesService = storagePropertiesService;
        this.logService = logService;
        this.crossFrameEventService = crossFrameEventService;
        this.siteService = siteService;
        this.previewService = previewService;
        this.catalogService = catalogService;
        this.languageService = languageService;
        this.sharedDataService = sharedDataService;
        this.iframeManagerService = iframeManagerService;
        this.routingService = routingService;
        this.storageLoaded$ = new rxjs.BehaviorSubject(false);
        seStorageManager
            .getStorage({
            storageId: smarteditcommons.EXPERIENCE_STORAGE_KEY,
            storageType: storagePropertiesService.getProperty('STORAGE_TYPE_SESSION_STORAGE')
        })
            .then((_storage) => {
            this.experienceStorage = _storage;
            this.storageLoaded$.next(true);
        });
    }
    /**
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     */
    buildAndSetExperience(params) {
        const siteId = params.siteId;
        const catalogId = params.catalogId;
        const catalogVersion = params.catalogVersion;
        const productCatalogVersions = params.productCatalogVersions;
        return Promise.all([
            this.siteService.getSiteById(siteId),
            this.catalogService.getContentCatalogsForSite(siteId),
            this.catalogService.getProductCatalogsForSite(siteId),
            this.languageService.getLanguagesForSite(siteId)
        ]).then(([siteDescriptor, catalogs, productCatalogs, languages]) => {
            const currentCatalog = catalogs.find((catalog) => catalog.catalogId === catalogId);
            const currentCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((result) => result.version === catalogVersion)
                : null;
            if (!currentCatalogVersion) {
                return Promise.reject(`no catalogVersionDescriptor found for ${catalogId} catalogId and ${catalogVersion} catalogVersion`);
            }
            const currentExperienceProductCatalogVersions = [];
            productCatalogs.forEach((productCatalog) => {
                // for each product catalog either choose the version already present in the params or choose the active version.
                const currentProductCatalogVersion = productCatalog.versions.find((version) => productCatalogVersions
                    ? productCatalogVersions.indexOf(version.uuid) > -1
                    : version.active === true);
                currentExperienceProductCatalogVersions.push({
                    catalog: productCatalog.catalogId,
                    catalogName: productCatalog.name,
                    catalogVersion: currentProductCatalogVersion.version,
                    active: currentProductCatalogVersion.active,
                    uuid: currentProductCatalogVersion.uuid
                });
            });
            const languageDescriptor = params.language
                ? languages.find((lang) => lang.isocode === params.language)
                : languages[0];
            const defaultExperience = lo__namespace.cloneDeep(params);
            delete defaultExperience.siteId;
            delete defaultExperience.catalogId;
            delete defaultExperience.catalogVersion;
            defaultExperience.siteDescriptor = siteDescriptor;
            defaultExperience.catalogDescriptor = {
                catalogId,
                catalogVersion: currentCatalogVersion.version,
                catalogVersionUuid: currentCatalogVersion.uuid,
                name: currentCatalog.name,
                siteId,
                active: currentCatalogVersion.active
            };
            defaultExperience.languageDescriptor = languageDescriptor;
            defaultExperience.time = defaultExperience.time || null;
            defaultExperience.productCatalogVersions = currentExperienceProductCatalogVersions;
            return this.setCurrentExperience(defaultExperience);
        });
    }
    /**
     * Used to update the page ID stored in the current experience and reloads the page to make the changes visible.
     *
     * @param newPageID the ID of the page that must be stored in the current experience.
     *
     */
    updateExperiencePageId(newPageID) {
        return this.getCurrentExperience().then((currentExperience) => {
            if (!currentExperience) {
                // Experience haven't been set. Thus, the experience hasn't been loaded.
                // No need to update the experience then.
                return null;
            }
            currentExperience.pageId = newPageID;
            return this.setCurrentExperience(currentExperience).then(() => this.reloadPage());
        });
    }
    /**
     * Used to update the experience with the parameters provided and reloads the page to make the changes visible.
     *
     * @param params The object containing the paratements for the experience to be loaded.
     * @param params.siteId the ID of the site that must be stored in the current experience.
     * @param params.catalogId the ID of the catalog that must be stored in the current experience.
     * @param params.catalogVersion the version of the catalog that must be stored in the current experience.
     * @param params.pageId the ID of the page that must be stored in the current experience.
     *
     */
    loadExperience(params) {
        return this.buildAndSetExperience(params).then(() => this.reloadPage());
    }
    reloadPage() {
        this.routingService.reload(smarteditcommons.STORE_FRONT_CONTEXT);
    }
    updateExperiencePageContext(pageCatalogVersionUuid, pageId) {
        return this.getCurrentExperience()
            .then((currentExperience) => this.catalogService
            .getContentCatalogsForSite(currentExperience.catalogDescriptor.siteId)
            .then((catalogs) => {
            if (!currentExperience) {
                // Experience haven't been set. Thus, the experience hasn't been loaded. No need to update the
                // experience then.
                return null;
            }
            const allCatalogs = catalogs.reduce((acc, catalog) => {
                if (catalog.parents && catalog.parents.length) {
                    catalog.parents.forEach((parent) => {
                        acc.push(parent);
                    });
                }
                return acc;
            }, [...catalogs]);
            const pageCatalogVersion = lo__namespace
                .flatten(allCatalogs.map((catalog) => catalog.versions.map((version) => {
                version.catalogName =
                    catalog.name ||
                        catalog.catalogName;
                version.catalogId = catalog.catalogId;
                return version;
            })))
                .find((version) => version.uuid === pageCatalogVersionUuid);
            return this.catalogService.getCurrentSiteID().then((siteID) => {
                currentExperience.pageId = pageId;
                currentExperience.pageContext = {
                    catalogId: pageCatalogVersion.catalogId,
                    catalogName: pageCatalogVersion.catalogName,
                    catalogVersion: pageCatalogVersion.version,
                    catalogVersionUuid: pageCatalogVersion.uuid,
                    siteId: siteID,
                    active: pageCatalogVersion.active
                };
                return this.setCurrentExperience(currentExperience);
            });
        }))
            .then((experience) => {
            this.crossFrameEventService.publish(smarteditcommons.EVENTS.PAGE_CHANGE, experience);
            return experience;
        });
    }
    getCurrentExperience() {
        // After Angular porting of StorageModule the experienceStorage load promise seems to be resolved after execution of getCurrentExperience.
        // To avoid errors the method is triggered once experienceStorage is present.
        return this.storageLoaded$
            .pipe(operators.filter((value) => value), operators.take(1), operators.mergeMap(() => this.experienceStorage.get(smarteditcommons.EXPERIENCE_STORAGE_KEY)))
            .toPromise();
    }
    setCurrentExperience(experience) {
        return this.getCurrentExperience().then((previousExperience) => {
            this.previousExperience = previousExperience;
            return this.experienceStorage
                .put(experience, smarteditcommons.EXPERIENCE_STORAGE_KEY)
                .then(() => this.sharedDataService
                .set(smarteditcommons.EXPERIENCE_STORAGE_KEY, experience)
                .then(() => experience));
        });
    }
    hasCatalogVersionChanged() {
        return this.getCurrentExperience().then((currentExperience) => this.previousExperience === undefined ||
            currentExperience.catalogDescriptor.catalogId !==
                this.previousExperience.catalogDescriptor.catalogId ||
            currentExperience.catalogDescriptor.catalogVersion !==
                this.previousExperience.catalogDescriptor.catalogVersion);
    }
    initializeExperience() {
        this.iframeManagerService.setCurrentLocation(null);
        return this.getCurrentExperience().then((experience) => {
            if (!experience) {
                this.routingService.go('/');
                return null;
            }
            return this.updateExperience();
        }, (err) => {
            this.logService.error('ExperienceService.initializeExperience() - failed to retrieve experience');
            return Promise.reject(err);
        });
    }
    updateExperience(newExperience) {
        return this.getCurrentExperience().then((experience) => {
            // create a deep copy of the current experience
            experience = lo__namespace.cloneDeep(experience);
            // merge the new experience into the copy of the current experience
            lo__namespace.merge(experience, newExperience);
            return this.previewService
                .getResourcePathFromPreviewUrl(experience.siteDescriptor.previewUrl)
                .then((resourcePath) => {
                const previewData = this._convertExperienceToPreviewData(experience, resourcePath);
                return this.previewService.createPreview(previewData).then((previewResponse) => {
                    /* forbiddenNameSpaces window._:false */
                    window.__smartedit__.smartEditBootstrapped = {};
                    this.iframeManagerService.loadPreview(previewResponse.resourcePath, previewResponse.ticketId);
                    return this.setCurrentExperience(experience);
                }, (err) => {
                    this.logService.error('iframeManagerService.updateExperience() - failed to update experience');
                    return Promise.reject(err);
                });
            }, (err) => {
                this.logService.error('ExperienceService.updateExperience() - failed to retrieve resource path');
                return Promise.reject(err);
            });
        }, (err) => {
            this.logService.error('ExperienceService.updateExperience() - failed to retrieve current experience');
            return Promise.reject(err);
        });
    }
    compareWithCurrentExperience(experience) {
        if (!experience) {
            return Promise.resolve(false);
        }
        return this.getCurrentExperience().then((currentExperience) => {
            if (!currentExperience) {
                return Promise.resolve(false);
            }
            if (currentExperience.pageId === experience.pageId &&
                currentExperience.siteDescriptor.uid === experience.siteId &&
                currentExperience.catalogDescriptor.catalogId === experience.catalogId &&
                currentExperience.catalogDescriptor.catalogVersion === experience.catalogVersion) {
                return Promise.resolve(true);
            }
            return Promise.resolve(false);
        });
    }
};
ExperienceService = __decorate([
    smarteditcommons.GatewayProxied('loadExperience', 'updateExperiencePageContext', 'getCurrentExperience', 'hasCatalogVersionChanged', 'buildRefreshedPreviewUrl', 'compareWithCurrentExperience'),
    __param(3, core.Inject(smarteditcommons.EVENT_SERVICE)),
    __metadata("design:paramtypes", [smarteditcommons.IStorageManager,
        smarteditcommons.IStoragePropertiesService,
        smarteditcommons.LogService,
        smarteditcommons.CrossFrameEventService,
        SiteService,
        smarteditcommons.IPreviewService,
        smarteditcommons.ICatalogService,
        smarteditcommons.LanguageService,
        smarteditcommons.ISharedDataService,
        IframeManagerService,
        smarteditcommons.SmarteditRoutingService])
], ExperienceService);

/** @internal */
let FeatureService = class FeatureService extends smarteditcommons.IFeatureService {
    constructor(toolbarServiceFactory, cloneableUtils, logService) {
        super(cloneableUtils, logService);
        this.toolbarServiceFactory = toolbarServiceFactory;
        this.logService = logService;
        this.features = [];
    }
    getFeatureProperty(featureKey, propertyName) {
        const feature = this._getFeatureByKey(featureKey);
        return Promise.resolve(feature ? feature[propertyName] : null);
    }
    getFeatureKeys() {
        return this.features.map((feature) => feature.key);
    }
    addToolbarItem(configuration) {
        const toolbar = this.toolbarServiceFactory.getToolbarService(configuration.toolbarId);
        configuration.enablingCallback = function () {
            this.addItems([configuration]);
        }.bind(toolbar);
        configuration.disablingCallback = function () {
            this.removeItemByKey(configuration.key);
        }.bind(toolbar);
        return this.register(configuration);
    }
    _registerAliases(configuration) {
        const feature = this._getFeatureByKey(configuration.key);
        if (!feature) {
            configuration.id = Buffer.from(configuration.key).toString('base64');
            this.features.push(configuration);
        }
        return Promise.resolve();
    }
    _getFeatureByKey(key) {
        return this.features.find((feature) => feature.key === key);
    }
};
FeatureService = __decorate([
    smarteditcommons.GatewayProxied('_registerAliases', 'addToolbarItem', 'register', 'enable', 'disable', '_remoteEnablingFromInner', '_remoteDisablingFromInner', 'addDecorator', 'getFeatureProperty', 'addContextualMenuButton'),
    __metadata("design:paramtypes", [smarteditcommons.IToolbarServiceFactory,
        smarteditcommons.CloneableUtils,
        smarteditcommons.LogService])
], FeatureService);

/** @internal */
let PageInfoService = class PageInfoService extends smarteditcommons.IPageInfoService {
    constructor() {
        super();
    }
};
PageInfoService = __decorate([
    smarteditcommons.GatewayProxied('getPageUID', 'getPageUUID', 'getCatalogVersionUUIDFromPage', 'isSameCatalogVersionOfPageAndPageTemplate'),
    __metadata("design:paramtypes", [])
], PageInfoService);

/** @internal */
let PreviewService = class PreviewService extends smarteditcommons.IPreviewService {
    constructor(log, loadConfigManagerService, restServiceFactory, urlUtils) {
        super(urlUtils);
        this.log = log;
        this.loadConfigManagerService = loadConfigManagerService;
        this.restServiceFactory = restServiceFactory;
        this.ticketIdIdentifier = 'ticketId';
    }
    createPreview(previewData) {
        return __awaiter(this, void 0, void 0, function* () {
            /**
             * We don't know about any fields coming from other extensions, but throw error for any of the fields
             * that we do know about, namely the IPreviewData interface fields
             */
            const requiredFields = ['catalogVersions', 'resourcePath'];
            this.validatePreviewDataAttributes(previewData, requiredFields);
            yield this.prepareRestService();
            try {
                const response = yield this.previewRestService.save(previewData);
                /**
                 * The response object being stringified, when using copy method, has a method named toJSON()
                 * because it is originally of type angular.resource.IResource<IPreviewData> and
                 * that IResource.toJSON() method is responsible to remove $promise, $resolved properties from the response object.
                 */
                return smarteditcommons.objectUtils.copy(response);
            }
            catch (err) {
                this.log.error('PreviewService.createPreview() - Error creating preview');
                return Promise.reject(err);
            }
        });
    }
    updatePreview(previewData) {
        return __awaiter(this, void 0, void 0, function* () {
            const requiredFields = ['catalogVersions', 'resourcePath', 'ticketId'];
            this.validatePreviewDataAttributes(previewData, requiredFields);
            yield this.prepareRestService();
            try {
                return yield this.previewByticketRestService.update(previewData);
            }
            catch (err) {
                this.log.error('PreviewService.updatePreview() - Error updating preview');
                return Promise.reject(err);
            }
        });
    }
    getResourcePathFromPreviewUrl(previewUrl) {
        return this.prepareRestService().then(() => this.urlUtils.getAbsoluteURL(this.domain, previewUrl));
    }
    prepareRestService() {
        if (!this.previewRestService || !this.previewByticketRestService) {
            return this.loadConfigManagerService.loadAsObject().then((configurations) => {
                const RESOURCE_URI = (configurations.previewTicketURI ||
                    smarteditcommons.PREVIEW_RESOURCE_URI);
                this.previewRestService = this.restServiceFactory.get(RESOURCE_URI);
                this.previewByticketRestService = this.restServiceFactory.get(RESOURCE_URI, this.ticketIdIdentifier);
                this.domain = configurations.domain;
            }, (err) => {
                this.log.error('PreviewService.getRestService() - Error loading configuration');
                return Promise.reject(err);
            });
        }
        return Promise.resolve();
    }
    validatePreviewDataAttributes(previewData, requiredFields) {
        if (requiredFields) {
            requiredFields.forEach((elem) => {
                if (lo__namespace.isEmpty(previewData[elem])) {
                    throw new Error(`ValidatePreviewDataAttributes - ${elem} is empty`);
                }
            });
        }
    }
};
PreviewService = __decorate([
    smarteditcommons.GatewayProxied(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        exports.LoadConfigManagerService,
        smarteditcommons.RestServiceFactory,
        smarteditcommons.UrlUtils])
], PreviewService);

/** @internal */
let PerspectiveService = class PerspectiveService extends smarteditcommons.IPerspectiveService {
    constructor(routingService, logService, systemEventService, featureService, waitDialogService, storageService, crossFrameEventService, permissionService) {
        super();
        this.routingService = routingService;
        this.logService = logService;
        this.systemEventService = systemEventService;
        this.featureService = featureService;
        this.waitDialogService = waitDialogService;
        this.storageService = storageService;
        this.crossFrameEventService = crossFrameEventService;
        this.permissionService = permissionService;
        this.PERSPECTIVE_COOKIE_NAME = 'smartedit-perspectives';
        this.INITIAL_SWITCHTO_ARG = 'INITIAL_SWITCHTO_ARG';
        this.data = {
            activePerspective: undefined,
            previousPerspective: undefined,
            previousSwitchToArg: this.INITIAL_SWITCHTO_ARG
        };
        this.immutablePerspectives = []; // once a perspective is registered it will always exists in this variable
        this.perspectives = [];
        this._addDefaultPerspectives();
        this._registerEventHandlers();
        this.NON_PERSPECTIVE_OBJECT = { key: smarteditcommons.NONE_PERSPECTIVE, nameI18nKey: '', features: [] };
    }
    register(configuration) {
        this._validate(configuration);
        let perspective = this._findByKey(configuration.key);
        if (!perspective) {
            this._addPerspectiveSelectorWidget(configuration);
            perspective = configuration;
            perspective.isHotkeyDisabled = !!configuration.isHotkeyDisabled;
            this.immutablePerspectives.push(perspective);
            this.perspectives.push(perspective);
            this.systemEventService.publishAsync(smarteditcommons.EVENT_PERSPECTIVE_ADDED);
        }
        else {
            perspective.features = smarteditcommons.objectUtils.uniqueArray(perspective.features || [], configuration.features || []);
            perspective.perspectives = smarteditcommons.objectUtils.uniqueArray(perspective.perspectives || [], configuration.perspectives || []);
            perspective.permissions = smarteditcommons.objectUtils.uniqueArray(perspective.permissions || [], configuration.permissions || []);
            this.systemEventService.publishAsync(smarteditcommons.EVENT_PERSPECTIVE_UPDATED);
        }
        return Promise.resolve();
    }
    // Filters immutablePerspectives to determine which perspectives are available, taking into account security
    getPerspectives() {
        const promises = [];
        this.immutablePerspectives.forEach((perspective) => {
            let promise;
            if (perspective.permissions && perspective.permissions.length > 0) {
                promise = this.permissionService.isPermitted([
                    {
                        names: perspective.permissions
                    }
                ]);
            }
            else {
                promise = Promise.resolve(true);
            }
            promises.push(promise);
        });
        return Promise.all(promises).then((results) => this.immutablePerspectives.filter((perspective, index) => results[index]));
    }
    hasActivePerspective() {
        return Promise.resolve(Boolean(this.data.activePerspective));
    }
    getActivePerspective() {
        return this.data.activePerspective
            ? Object.assign({}, this._findByKey(this.data.activePerspective.key)) : null;
    }
    isEmptyPerspectiveActive() {
        return Promise.resolve(!!this.data.activePerspective && this.data.activePerspective.key === smarteditcommons.NONE_PERSPECTIVE);
    }
    switchTo(key) {
        if (!this._changeActivePerspective(key)) {
            this.waitDialogService.hideWaitModal();
            return Promise.resolve();
        }
        this._handleUnloadEvent(key);
        this.waitDialogService.showWaitModal();
        const featuresFromPreviousPerspective = [];
        if (this.data.previousPerspective) {
            this._fetchAllFeatures(this.data.previousPerspective, featuresFromPreviousPerspective);
        }
        const featuresFromNewPerspective = [];
        this._fetchAllFeatures(this.data.activePerspective, featuresFromNewPerspective);
        // deactivating any active feature not belonging to either the perspective or one of its nested perspectives
        featuresFromPreviousPerspective
            .filter((featureKey) => !featuresFromNewPerspective.some((f) => featureKey === f))
            .forEach((featureKey) => {
            this.featureService.disable(featureKey);
        });
        // activating any feature belonging to either the perspective or one of its nested perspectives
        const permissionPromises = [];
        featuresFromNewPerspective
            .filter((feature) => !featuresFromPreviousPerspective.some((f) => feature === f))
            .forEach((featureKey) => {
            permissionPromises.push(this._enableOrDisableFeature(featureKey));
        });
        return Promise.all(permissionPromises).then(() => {
            if (this.data.activePerspective.key === smarteditcommons.NONE_PERSPECTIVE) {
                this.waitDialogService.hideWaitModal();
            }
            this.crossFrameEventService.publish(smarteditcommons.EVENT_PERSPECTIVE_CHANGED, this.data.activePerspective.key !== smarteditcommons.NONE_PERSPECTIVE);
        });
    }
    selectDefault() {
        return this.getPerspectives().then((perspectives) => this.storageService
            .getValueFromLocalStorage(this.PERSPECTIVE_COOKIE_NAME, true)
            .then((cookieValue) => {
            //  restricted by permission
            const perspectiveAvailable = perspectives.find((p) => p.key === cookieValue);
            let defaultPerspective;
            let perspective;
            if (!perspectiveAvailable) {
                this.logService.warn('Cannot select mode "' +
                    cookieValue +
                    '" It might not exist or is restricted.');
                // from full list of perspectives, regardless of permissions
                const perspectiveFromCookie = this._findByKey(cookieValue);
                if (!!perspectiveFromCookie) {
                    this._disableAllFeaturesForPerspective(perspectiveFromCookie);
                }
                defaultPerspective = this.NON_PERSPECTIVE_OBJECT;
                perspective = this.NON_PERSPECTIVE_OBJECT;
            }
            else {
                const perspectiveFromCookie = this._findByKey(cookieValue);
                defaultPerspective = perspectiveFromCookie
                    ? perspectiveFromCookie
                    : this.NON_PERSPECTIVE_OBJECT;
                perspective = this.data.previousPerspective
                    ? this.data.previousPerspective
                    : defaultPerspective;
            }
            if (defaultPerspective.key !== this.NON_PERSPECTIVE_OBJECT.key) {
                this._disableAllFeaturesForPerspective(defaultPerspective);
            }
            return this.switchTo(perspective.key);
        }));
    }
    refreshPerspective() {
        return this.getPerspectives().then((result) => {
            const activePerspective = this.getActivePerspective();
            if (!activePerspective) {
                return this.selectDefault();
            }
            else {
                this.perspectives = result;
                if (!this._findByKey(activePerspective.key)) {
                    return this.switchTo(smarteditcommons.NONE_PERSPECTIVE);
                }
                else {
                    const features = [];
                    const permissionPromises = [];
                    this._fetchAllFeatures(activePerspective, features);
                    features.forEach((featureKey) => {
                        permissionPromises.push(this._enableOrDisableFeature(featureKey));
                    });
                    return Promise.all(permissionPromises).then(() => {
                        this.waitDialogService.hideWaitModal();
                        this.crossFrameEventService.publish(smarteditcommons.EVENT_PERSPECTIVE_REFRESHED, activePerspective.key !== smarteditcommons.NONE_PERSPECTIVE);
                    });
                }
            }
        });
    }
    getActivePerspectiveKey() {
        const activePerspective = this.getActivePerspective();
        return Promise.resolve(activePerspective ? activePerspective.key : null);
    }
    isHotkeyEnabledForActivePerspective() {
        const activePerspective = this.getActivePerspective();
        return Promise.resolve(activePerspective && !activePerspective.isHotkeyDisabled);
    }
    _addPerspectiveSelectorWidget(configuration) {
        configuration.features = configuration.features || [];
        if (configuration.features.indexOf(smarteditcommons.PERSPECTIVE_SELECTOR_WIDGET_KEY) === -1) {
            configuration.features.unshift(smarteditcommons.PERSPECTIVE_SELECTOR_WIDGET_KEY);
        }
    }
    _addDefaultPerspectives() {
        this.register({
            key: smarteditcommons.NONE_PERSPECTIVE,
            nameI18nKey: 'se.perspective.none.name',
            isHotkeyDisabled: true,
            descriptionI18nKey: 'se.perspective.none.description.disabled'
        });
        this.register({
            key: smarteditcommons.ALL_PERSPECTIVE,
            nameI18nKey: 'se.perspective.all.name',
            descriptionI18nKey: 'se.perspective.all.description'
        });
    }
    _registerEventHandlers() {
        this.systemEventService.subscribe(smarteditcommons.EVENTS.LOGOUT, this._clearPerspectiveFeatures.bind(this));
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, this._clearPerspectiveFeatures.bind(this));
        // clear the features when navigating to another page than the storefront. this is preventing a flickering of toolbar icons when going back to storefront on another page.
        this.routingService.routeChangeSuccess().subscribe((event) => {
            const url = this.routingService.getCurrentUrlFromEvent(event);
            if ((url || '').includes(smarteditcommons.STORE_FRONT_CONTEXT)) {
                this._clearPerspectiveFeatures();
            }
        });
    }
    _validate(configuration) {
        if (smarteditcommons.stringUtils.isBlank(configuration.key)) {
            throw new Error('perspectiveService.configuration.key.error.required');
        }
        if (smarteditcommons.stringUtils.isBlank(configuration.nameI18nKey)) {
            throw new Error('perspectiveService.configuration.nameI18nKey.error.required');
        }
        if ([smarteditcommons.NONE_PERSPECTIVE, smarteditcommons.ALL_PERSPECTIVE].indexOf(configuration.key) === -1 &&
            (smarteditcommons.stringUtils.isBlank(configuration.features) || configuration.features.length === 0)) {
            throw new Error('perspectiveService.configuration.features.error.required');
        }
    }
    _findByKey(key) {
        return this.perspectives.find((perspective) => perspective.key === key);
    }
    _fetchAllFeatures(perspective, holder) {
        if (!holder) {
            holder = [];
        }
        if (perspective.key === smarteditcommons.ALL_PERSPECTIVE) {
            smarteditcommons.objectUtils.uniqueArray(holder, this.featureService.getFeatureKeys() || []);
        }
        else {
            smarteditcommons.objectUtils.uniqueArray(holder, perspective.features || []);
            (perspective.perspectives || []).forEach((perspectiveKey) => {
                const nestedPerspective = this._findByKey(perspectiveKey);
                if (nestedPerspective) {
                    this._fetchAllFeatures(nestedPerspective, holder);
                }
                else {
                    this.logService.debug('nested perspective ' + perspectiveKey + ' was not found in the registry');
                }
            });
        }
    }
    _enableOrDisableFeature(featureKey) {
        return this.featureService
            .getFeatureProperty(featureKey, 'permissions')
            .then((permissionNames) => {
            if (!Array.isArray(permissionNames)) {
                permissionNames = [];
            }
            return this.permissionService
                .isPermitted([
                {
                    names: permissionNames
                }
            ])
                .then((allowCallback) => {
                if (allowCallback) {
                    this.featureService.enable(featureKey);
                }
                else {
                    this.featureService.disable(featureKey);
                }
            });
        });
    }
    /**
     * Takes care of sending EVENT_PERSPECTIVE_UNLOADING when perspectives change.
     *
     * This function tracks the "key" argument in calls to switchTo(..) function in order to detect when a
     * perspective is being switched.
     */
    _handleUnloadEvent(nextPerspectiveKey) {
        if (nextPerspectiveKey !== this.data.previousSwitchToArg &&
            this.data.previousSwitchToArg !== this.INITIAL_SWITCHTO_ARG) {
            this.crossFrameEventService.publish(smarteditcommons.EVENT_PERSPECTIVE_UNLOADING, this.data.previousSwitchToArg);
        }
        this.data.previousSwitchToArg = nextPerspectiveKey;
    }
    _retrievePerspective(key) {
        // Validation
        // Change the perspective only if it makes sense.
        if (this.data.activePerspective && this.data.activePerspective.key === key) {
            return null;
        }
        const newPerspective = this._findByKey(key);
        if (!newPerspective) {
            throw new Error("switchTo() - Couldn't find perspective with key " + key);
        }
        return newPerspective;
    }
    _changeActivePerspective(newPerspectiveKey) {
        const newPerspective = this._retrievePerspective(newPerspectiveKey);
        if (newPerspective) {
            this.data.previousPerspective = this.data.activePerspective;
            this.data.activePerspective = newPerspective;
            this.storageService.setValueInLocalStorage(this.PERSPECTIVE_COOKIE_NAME, newPerspective.key, true);
        }
        return newPerspective;
    }
    _disableAllFeaturesForPerspective(perspective) {
        const features = [];
        this._fetchAllFeatures(perspective, features);
        features.forEach((featureKey) => {
            this.featureService.disable(featureKey);
        });
    }
    _clearPerspectiveFeatures() {
        // De-activates all current perspective's features (Still leaves the cookie in the system).
        const perspectiveFeatures = [];
        if (this.data && this.data.activePerspective) {
            this._fetchAllFeatures(this.data.activePerspective, perspectiveFeatures);
        }
        perspectiveFeatures.forEach((feature) => {
            this.featureService.disable(feature);
        });
        return Promise.resolve();
    }
};
PerspectiveService = __decorate([
    smarteditcommons.GatewayProxied('register', 'switchTo', 'hasActivePerspective', 'isEmptyPerspectiveActive', 'selectDefault', 'refreshPerspective', 'getActivePerspectiveKey', 'isHotkeyEnabledForActivePerspective'),
    __metadata("design:paramtypes", [smarteditcommons.SmarteditRoutingService,
        smarteditcommons.LogService,
        smarteditcommons.SystemEventService,
        smarteditcommons.IFeatureService,
        smarteditcommons.IWaitDialogService,
        smarteditcommons.IStorageService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.IPermissionService])
], PerspectiveService);

/** @internal */
exports.SessionService = class SessionService extends smarteditcommons.ISessionService {
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    constructor($log, restServiceFactory, storageService, cryptographicUtils) {
        super();
        this.$log = $log;
        this.storageService = storageService;
        this.cryptographicUtils = cryptographicUtils;
        // ------------------------------------------------------------------------
        // Constants
        // ------------------------------------------------------------------------
        this.USER_DATA_URI = '/cmswebservices/v1/users/:userUid';
        this.whoAmIService = restServiceFactory.get(smarteditcommons.WHO_AM_I_RESOURCE_URI);
        this.userRestService = restServiceFactory.get(this.USER_DATA_URI);
    }
    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------
    getCurrentUserDisplayName() {
        return this.getCurrentUserData().then((currentUserData) => currentUserData.displayName);
    }
    getCurrentUsername() {
        return this.getCurrentUserData().then((currentUserData) => currentUserData.uid);
    }
    getCurrentUser() {
        return this.getCurrentUserData();
    }
    hasUserChanged() {
        const prevHashPromise = Promise.resolve(this.cachedUserHash
            ? this.cachedUserHash
            : this.storageService.getItem(smarteditcommons.PREVIOUS_USERNAME_HASH));
        return prevHashPromise.then((prevHash) => this.whoAmIService
            .get({})
            .then((currentUserData) => !!prevHash &&
            prevHash !== this.cryptographicUtils.sha1Hash(currentUserData.uid)));
    }
    setCurrentUsername() {
        return this.whoAmIService.get({}).then((currentUserData) => {
            // NOTE: For most of SmartEdit operation, it is enough to store the previous user hash in the cache.
            // However, if the page is refreshed the cache is cleaned. Therefore, it's necessary to also store it in
            // a cookie through the storageService.
            this.cachedUserHash = this.cryptographicUtils.sha1Hash(currentUserData.uid);
            this.storageService.setItem(smarteditcommons.PREVIOUS_USERNAME_HASH, this.cachedUserHash);
        });
    }
    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------
    getCurrentUserData() {
        return this.whoAmIService
            .get({})
            .then((whoAmIData) => this.userRestService
            .get({
            userUid: whoAmIData.uid
        })
            .then((userData) => ({
            uid: userData.uid,
            displayName: whoAmIData.displayName,
            readableLanguages: userData.readableLanguages,
            writeableLanguages: userData.writeableLanguages
        })))
            .catch((reason) => {
            this.$log.warn("[SessionService]: Can't load session information", reason);
            return null;
        });
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.userEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], exports.SessionService.prototype, "getCurrentUserData", null);
exports.SessionService = __decorate([
    smarteditcommons.GatewayProxied('getCurrentUsername', 'getCurrentUserDisplayName', 'hasUserChanged', 'setCurrentUsername', 'getCurrentUser'),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        smarteditcommons.RestServiceFactory,
        smarteditcommons.IStorageService,
        smarteditcommons.CryptographicUtils])
], exports.SessionService);

/** @internal */
/* @ngInject */ exports.SharedDataService = class /* @ngInject */ SharedDataService extends smarteditcommons.ISharedDataService {
    constructor() {
        super();
        this.storage = {};
    }
    get(key) {
        return Promise.resolve(this.storage[key]);
    }
    set(key, value) {
        this.storage[key] = value;
        return Promise.resolve();
    }
    update(key, modifyingCallback) {
        return this.get(key).then((oldValue) => modifyingCallback(oldValue).then((newValue) => this.set(key, newValue)));
    }
    remove(key) {
        const value = this.storage[key];
        delete this.storage[key];
        return Promise.resolve(value);
    }
    containsKey(key) {
        return Promise.resolve(lo__namespace.has(this.storage, key));
    }
};
/* @ngInject */ exports.SharedDataService = __decorate([
    smarteditcommons.GatewayProxied(),
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ exports.SharedDataService);

/** @internal */
/* @ngInject */ exports.StorageService = class /* @ngInject */ StorageService extends smarteditcommons.IStorageService {
    constructor(logService, windowUtils, cryptographicUtils, fingerPrintingService) {
        super();
        this.logService = logService;
        this.windowUtils = windowUtils;
        this.cryptographicUtils = cryptographicUtils;
        this.fingerPrintingService = fingerPrintingService;
        this.SMARTEDIT_SESSIONS = 'smartedit-sessions';
        this.CUSTOM_PROPERTIES = 'custom_properties';
    }
    isInitialized() {
        const sessions = this.getAuthTokens();
        return Promise.resolve(lo__namespace.values(lo__namespace.omit(sessions, [this.CUSTOM_PROPERTIES])).length > 0);
    }
    storeAuthToken(authURI, auth) {
        const sessions = this.getAuthTokens();
        sessions[authURI] = auth;
        this._setSmarteditSessions(sessions);
        return Promise.resolve();
    }
    getAuthToken(authURI) {
        const sessions = this.getAuthTokens();
        return Promise.resolve(sessions[authURI]);
    }
    removeAuthToken(authURI) {
        const sessions = this.getAuthTokens();
        delete sessions[authURI];
        this._setSmarteditSessions(sessions);
        return Promise.resolve();
    }
    removeAllAuthTokens() {
        this._removeAllAuthTokens();
        return Promise.resolve();
    }
    getValueFromCookie(cookieName, isEncoded) {
        throw new Error('getValueFromCookie deprecated since 1905, use getValueFromLocalStorage');
    }
    getValueFromLocalStorage(cookieName, isEncoded) {
        return Promise.resolve(this._getValueFromLocalStorage(cookieName, isEncoded));
    }
    getAuthTokens() {
        const smarteditSessions = this.windowUtils
            .getWindow()
            .localStorage.getItem(this.SMARTEDIT_SESSIONS);
        let authTokens;
        if (smarteditSessions) {
            try {
                const decrypted = this.cryptographicUtils.aesDecrypt(smarteditSessions, this.fingerPrintingService.getFingerprint());
                authTokens = JSON.parse(decodeURIComponent(escape(decrypted)));
            }
            catch (_a) {
                // failed to decrypt token. may occur if fingerprint changed.
                this.logService.info('Failed to read authentication token. Forcing a re-authentication.');
            }
        }
        return authTokens || {};
    }
    putValueInCookie(cookieName, value, encode) {
        throw new Error('putValueInCookie deprecated since 1905, use setValueInLocalStorage');
    }
    setValueInLocalStorage(cookieName, value, encode) {
        return Promise.resolve(this._setValueInLocalStorage(cookieName, value, encode));
    }
    setItem(key, value) {
        const sessions = this.getAuthTokens();
        sessions[this.CUSTOM_PROPERTIES] = sessions[this.CUSTOM_PROPERTIES] || {};
        sessions[this.CUSTOM_PROPERTIES][key] = value;
        this._setSmarteditSessions(sessions);
        return Promise.resolve();
    }
    getItem(key) {
        const sessions = this.getAuthTokens();
        sessions[this.CUSTOM_PROPERTIES] = sessions[this.CUSTOM_PROPERTIES] || {};
        return Promise.resolve(sessions[this.CUSTOM_PROPERTIES][key]);
    }
    _removeAllAuthTokens() {
        const sessions = this.getAuthTokens();
        const newSessions = lo__namespace.pick(sessions, [this.CUSTOM_PROPERTIES]);
        this._setSmarteditSessions(newSessions);
    }
    _getValueFromLocalStorage(cookieName, isEncoded) {
        const rawValue = this.windowUtils.getWindow().localStorage.getItem(cookieName);
        let value = null;
        if (rawValue) {
            try {
                value = JSON.parse(isEncoded ? decodeURIComponent(escape(window.atob(rawValue))) : rawValue);
            }
            catch (e) {
                // protecting against deserialization issue
                this.logService.error('Failed during deserialization ', e);
            }
        }
        return value;
    }
    _setSmarteditSessions(sessions) {
        const sessionsJSONString = Buffer.from(decodeURIComponent(encodeURIComponent(JSON.stringify(sessions)))).toString('base64');
        const sessionsEncrypted = this.cryptographicUtils.aesBase64Encrypt(sessionsJSONString, this.fingerPrintingService.getFingerprint());
        this.windowUtils
            .getWindow()
            .localStorage.setItem(this.SMARTEDIT_SESSIONS, sessionsEncrypted);
    }
    _setValueInLocalStorage(cookieName, value, encode) {
        let processedValue = JSON.stringify(value);
        processedValue = encode
            ? Buffer.from(decodeURIComponent(encodeURIComponent(processedValue))).toString('base64')
            : processedValue;
        this.windowUtils.getWindow().localStorage.setItem(cookieName, processedValue);
    }
};
/* @ngInject */ exports.StorageService = __decorate([
    smarteditcommons.GatewayProxied('isInitialized', 'storeAuthToken', 'getAuthToken', 'removeAuthToken', 'removeAllAuthTokens', 'storePrincipalIdentifier', 'getPrincipalIdentifier', 'removePrincipalIdentifier', 'getValueFromCookie', 'getValueFromLocalStorage'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        smarteditcommons.WindowUtils,
        smarteditcommons.CryptographicUtils,
        smarteditcommons.FingerPrintingService])
], /* @ngInject */ exports.StorageService);

/** @internal */
let /* @ngInject */ PermissionsRegistrationService = class /* @ngInject */ PermissionsRegistrationService {
    constructor(permissionService, sharedDataService) {
        this.permissionService = permissionService;
        this.sharedDataService = sharedDataService;
    }
    /**
     * Method containing registrations of rules and permissions to be used in smartedit workspace
     */
    registerRulesAndPermissions() {
        // Rules
        this.permissionService.registerRule({
            names: ['se.slot.belongs.to.page'],
            verify: (permissionObjects) => this.sharedDataService
                .get(smarteditcommons.EXPERIENCE_STORAGE_KEY)
                .then((experience) => experience.pageContext &&
                experience.pageContext.catalogVersionUuid ===
                    permissionObjects[0].context.slotCatalogVersionUuid)
        });
        // Permissions
        this.permissionService.registerPermission({
            aliases: ['se.slot.not.external'],
            rules: ['se.slot.belongs.to.page']
        });
    }
};
/* @ngInject */ PermissionsRegistrationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IPermissionService,
        smarteditcommons.ISharedDataService])
], /* @ngInject */ PermissionsRegistrationService);

/** @internal */
let /* @ngInject */ CatalogService = class /* @ngInject */ CatalogService extends smarteditcommons.ICatalogService {
    constructor(logService, sharedDataService, siteService, urlService, contentCatalogRestService, productCatalogRestService, storageService) {
        super();
        this.logService = logService;
        this.sharedDataService = sharedDataService;
        this.siteService = siteService;
        this.urlService = urlService;
        this.contentCatalogRestService = contentCatalogRestService;
        this.productCatalogRestService = productCatalogRestService;
        this.storageService = storageService;
        this.SELECTED_SITE_COOKIE_NAME = 'seselectedsite';
    }
    getContentCatalogsForSite(siteUID) {
        return this.contentCatalogRestService
            .get({
            siteUID
        })
            .then((catalogs) => catalogs.catalogs);
    }
    getCatalogByVersion(siteUID, catalogVersionName) {
        return this.getContentCatalogsForSite(siteUID).then((catalogs) => catalogs.filter((catalog) => catalog.versions.some((currentCatalogVersion) => currentCatalogVersion.version === catalogVersionName)));
    }
    isContentCatalogVersionNonActive(_uriContext) {
        return this._getContext(_uriContext).then((uriContext) => this.getContentCatalogsForSite(uriContext[smarteditcommons.CONTEXT_SITE_ID]).then((catalogs) => {
            const currentCatalog = catalogs.find((catalog) => catalog.catalogId === uriContext[smarteditcommons.CONTEXT_CATALOG]);
            const currentCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((catalogVersion) => catalogVersion.version === uriContext[smarteditcommons.CONTEXT_CATALOG_VERSION])
                : null;
            if (!currentCatalogVersion) {
                throw new Error(`Invalid uriContext ${uriContext}, cannot find catalog version.`);
            }
            return !currentCatalogVersion.active;
        }));
    }
    getContentCatalogActiveVersion(_uriContext) {
        return this._getContext(_uriContext).then((uriContext) => this.getContentCatalogsForSite(uriContext[smarteditcommons.CONTEXT_SITE_ID]).then((catalogs) => {
            const currentCatalog = catalogs.find((catalog) => catalog.catalogId === uriContext[smarteditcommons.CONTEXT_CATALOG]);
            const activeCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                : null;
            if (!activeCatalogVersion) {
                throw new Error(`Invalid uriContext ${uriContext}, cannot find catalog version.`);
            }
            return activeCatalogVersion.version;
        }));
    }
    getActiveContentCatalogVersionByCatalogId(contentCatalogId) {
        return this._getContext().then((uriContext) => this.getContentCatalogsForSite(uriContext[smarteditcommons.CONTEXT_SITE_ID]).then((catalogs) => {
            const currentCatalog = catalogs.find((catalog) => catalog.catalogId === contentCatalogId);
            const currentCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                : null;
            if (!currentCatalogVersion) {
                throw new Error(`Invalid content catalog ${contentCatalogId}, cannot find any active catalog version.`);
            }
            return currentCatalogVersion.version;
        }));
    }
    getContentCatalogVersion(_uriContext) {
        return this._getContext(_uriContext).then((uriContext) => this.getContentCatalogsForSite(uriContext[smarteditcommons.CONTEXT_SITE_ID]).then((catalogs) => {
            const catalog = catalogs.find((c) => c.catalogId === uriContext[smarteditcommons.CONTEXT_CATALOG]);
            if (!catalog) {
                throw new Error('no catalog ' +
                    uriContext[smarteditcommons.CONTEXT_CATALOG] +
                    ' found for site ' +
                    uriContext[smarteditcommons.CONTEXT_SITE_ID]);
            }
            const catalogVersion = catalog.versions.find((version) => version.version === uriContext[smarteditcommons.CONTEXT_CATALOG_VERSION]);
            if (!catalogVersion) {
                throw new Error(`no catalogVersion ${uriContext[smarteditcommons.CONTEXT_CATALOG_VERSION]} for catalog ${uriContext[smarteditcommons.CONTEXT_CATALOG]} and site ${uriContext[smarteditcommons.CONTEXT_SITE_ID]}`);
            }
            catalogVersion.catalogName = catalog.name;
            catalogVersion.catalogId = catalog.catalogId;
            return catalogVersion;
        }));
    }
    getCurrentSiteID() {
        return this.storageService.getValueFromLocalStorage(this.SELECTED_SITE_COOKIE_NAME, false);
    }
    /**
     * Finds the ID of the default site configured for the provided content catalog.
     * @param contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns The ID of the default site found.
     */
    getDefaultSiteForContentCatalog(contentCatalogId) {
        return this.siteService.getSites().then((sites) => {
            const defaultSitesForCatalog = sites.filter((site) => {
                // ContentCatalogs in the site object are sorted. The last one is considered
                // the default one for a given site.
                const siteDefaultContentCatalog = lo__namespace.last(site.contentCatalogs);
                return siteDefaultContentCatalog && siteDefaultContentCatalog === contentCatalogId;
            });
            if (defaultSitesForCatalog.length === 0) {
                this.logService.warn(`[catalogService] - No default site found for content catalog ${contentCatalogId}`);
            }
            else if (defaultSitesForCatalog.length > 1) {
                this.logService.warn(`[catalogService] - Many default sites found for content catalog ${contentCatalogId}`);
            }
            return defaultSitesForCatalog[0];
        });
    }
    /*
     *  Only the '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs' API can get data about the catalogVersions.
     * For performance, start with the current site. If current site does not have catalogVersionUuid, loop through all
     * sites.
     * */
    getCatalogVersionByUuid(catalogVersionUuid, siteId) {
        return __awaiter(this, void 0, void 0, function* () {
            const contentCatalogsGrouped = yield this.getContentCatalogsGrouped(catalogVersionUuid, siteId);
            const catalogs = lo__namespace.reduce(contentCatalogsGrouped, (allCatalogs, siteCatalogs) => allCatalogs.concat(siteCatalogs), []);
            const catalogVersionFound = lo__namespace
                .flatten(catalogs.map((catalog) => lo__namespace.cloneDeep(catalog.versions).map((version) => {
                version.catalogName = catalog.name;
                version.catalogId = catalog.catalogId;
                return version;
            })))
                .filter((version) => catalogVersionUuid === version.uuid &&
                (!siteId || siteId === version.siteDescriptor.uid))[0];
            if (!catalogVersionFound) {
                const errorMessage = 'Cannot find catalog version with UUID ' +
                    catalogVersionUuid +
                    (siteId ? ' in site ' + siteId : '');
                throw new Error(errorMessage);
            }
            return this.getCurrentSiteID().then((defaultSiteID) => {
                catalogVersionFound.siteId = defaultSiteID;
                return catalogVersionFound;
            });
        });
    }
    getAllContentCatalogsGroupedById() {
        return this.siteService.getSites().then((sites) => {
            const promisesToResolve = sites.map((site) => this.getContentCatalogsForSite(site.uid).then((catalogs) => {
                catalogs.forEach((catalog) => {
                    catalog.versions = catalog.versions.map((catalogVersion) => {
                        catalogVersion.siteDescriptor = site;
                        return catalogVersion;
                    });
                });
                return catalogs;
            }));
            return Promise.all(promisesToResolve);
        });
    }
    getProductCatalogsBySiteKey(siteUIDKey) {
        return this._getContext().then((uriContext) => this.getProductCatalogsForSite(uriContext[siteUIDKey]));
    }
    /* =====================================================================================================================
      * `getProductCatalogsBySite` is to get product catalogs by site value
      * `siteUIDValue` - is the site value rather than site key
      * if you want to get product catalogs by site key, please refer to function `getProductCatalogsBySiteKey`
       =====================================================================================================================
    */
    // eslint-disable-next-line @typescript-eslint/member-ordering
    getProductCatalogsForSite(siteUIDValue) {
        return this.productCatalogRestService
            .get({
            siteUID: siteUIDValue
        })
            .then((catalogs) => catalogs.catalogs);
    }
    getActiveProductCatalogVersionByCatalogId(productCatalogId) {
        return this.getProductCatalogsBySiteKey(smarteditcommons.CONTEXT_SITE_ID).then((catalogs) => {
            const currentCatalog = catalogs.find((catalog) => catalog.catalogId === productCatalogId);
            const currentCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                : null;
            if (!currentCatalogVersion) {
                throw new Error(`Invalid product catalog ${productCatalogId}, cannot find any active catalog version.`);
            }
            return currentCatalogVersion.version;
        });
    }
    // =====================================================================================================================
    //  Helper Methods
    // =====================================================================================================================
    getCatalogVersionUUid(_uriContext) {
        return this.getContentCatalogVersion(_uriContext).then((catalogVersion) => catalogVersion.uuid);
    }
    retrieveUriContext(_uriContext) {
        return this._getContext(_uriContext);
    }
    returnActiveCatalogVersionUIDs(catalogs) {
        return catalogs.reduce((accumulator, catalog) => {
            accumulator.push(catalog.versions.find((version) => version.active).uuid);
            return accumulator;
        }, []);
    }
    // eslint-disable-next-line @typescript-eslint/member-ordering
    isCurrentCatalogMultiCountry() {
        return this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY).then((experience) => {
            if (experience && experience.siteDescriptor && experience.catalogDescriptor) {
                const siteId = experience.siteDescriptor.uid;
                const catalogId = experience.catalogDescriptor.catalogId;
                return this.getContentCatalogsForSite(siteId).then((catalogs) => {
                    const catalog = catalogs.find((obj) => obj.catalogId === catalogId);
                    return Promise.resolve(catalog && catalog.parents && catalog.parents.length ? true : false);
                });
            }
            return false;
        });
    }
    _getContext(_uriContext) {
        // TODO: once refactored by Nick, use definition of experience
        return _uriContext
            ? Promise.resolve(_uriContext)
            : this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY).then((experience) => {
                if (!experience) {
                    throw new Error('catalogService was not provided with a uriContext and could not retrive an experience from sharedDataService');
                }
                return this.urlService.buildUriContext(experience.siteDescriptor.uid, experience.catalogDescriptor.catalogId, experience.catalogDescriptor.catalogVersion);
            });
    }
    getContentCatalogsGrouped(catalogVersionUuid, siteId) {
        return __awaiter(this, void 0, void 0, function* () {
            let contentCatalogsGrouped = [];
            const currentSiteId = yield this.getCurrentSiteID();
            if (siteId !== undefined || currentSiteId !== undefined) {
                const siteCatalogs = yield this.getContentCatalogsForSite(siteId !== undefined ? siteId : currentSiteId);
                const site = yield this.siteService.getSiteById(siteId !== undefined ? siteId : currentSiteId);
                let existed = false;
                siteCatalogs.forEach((catalog) => {
                    catalog.versions.forEach((version) => {
                        if (version.uuid === catalogVersionUuid) {
                            version.siteDescriptor = site;
                            existed = true;
                        }
                    });
                });
                if (existed) {
                    contentCatalogsGrouped.push(siteCatalogs);
                }
            }
            if (contentCatalogsGrouped.length === 0) {
                contentCatalogsGrouped = yield this.getAllContentCatalogsGroupedById();
            }
            return contentCatalogsGrouped;
        });
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.catalogEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ CatalogService.prototype, "getProductCatalogsForSite", null);
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.pageChangeEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], /* @ngInject */ CatalogService.prototype, "isCurrentCatalogMultiCountry", null);
/* @ngInject */ CatalogService = __decorate([
    smarteditcommons.GatewayProxied(),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        smarteditcommons.ISharedDataService,
        SiteService,
        smarteditcommons.IUrlService,
        smarteditcommons.ContentCatalogRestService,
        smarteditcommons.ProductCatalogRestService,
        smarteditcommons.IStorageService])
], /* @ngInject */ CatalogService);

/** @internal */
let /* @ngInject */ UrlService = class /* @ngInject */ UrlService extends smarteditcommons.IUrlService {
    constructor(router, location, windowUtils) {
        super();
        this.router = router;
        this.location = location;
        this.windowUtils = windowUtils;
    }
    openUrlInPopup(url) {
        const win = this.windowUtils
            .getWindow()
            .open(url, '_blank', 'toolbar=no, scrollbars=yes, resizable=yes');
        win.focus();
    }
    path(url) {
        /**
         * Angular route has been used to navigate to currently previewed page.
         */
        this.location.go(url);
        this.router.navigateByUrl(url);
    }
};
/* @ngInject */ UrlService = __decorate([
    smarteditcommons.GatewayProxied('openUrlInPopup', 'path'),
    core.Injectable(),
    __metadata("design:paramtypes", [router.Router,
        common.Location,
        smarteditcommons.WindowUtils])
], /* @ngInject */ UrlService);

/** @internal */
let WaitDialogService = class WaitDialogService extends smarteditcommons.IWaitDialogService {
    constructor(modalService) {
        super();
        this.modalService = modalService;
        this.modalRef = null;
    }
    showWaitModal(customLoadingMessageLocalizedKey) {
        const config = {
            component: smarteditcommons.WaitDialogComponent,
            data: { customLoadingMessageLocalizedKey },
            config: {
                backdropClickCloseable: false,
                dialogPanelClass: 'se-wait-spinner-dialog',
                focusTrapped: false
            }
        };
        if (this.modalRef === null) {
            this.modalRef = this.modalService.open(config);
        }
    }
    hideWaitModal() {
        if (this.modalRef != null) {
            this.modalRef.close();
            this.modalRef = null;
        }
    }
};
WaitDialogService = __decorate([
    smarteditcommons.GatewayProxied(),
    __metadata("design:paramtypes", [smarteditcommons.ModalService])
], WaitDialogService);

/** @internal */
class MemoryStorage {
    constructor() {
        this.data = {};
    }
    clear() {
        this.data = {};
        return Promise.resolve(true);
    }
    dispose() {
        return Promise.resolve(true);
    }
    find(queryObject) {
        return this.get(queryObject).then((result) => [result]);
    }
    get(queryObject) {
        return Promise.resolve(this.data[this.getKey(queryObject)]);
    }
    getLength() {
        return Promise.resolve(Object.keys(this.data).length);
    }
    put(obj, queryObject) {
        this.data[this.getKey(queryObject)] = obj;
        return Promise.resolve(true);
    }
    remove(queryObject) {
        const originalData = this.data[this.getKey(queryObject)];
        delete this.data[this.getKey(queryObject)];
        return Promise.resolve(originalData);
    }
    entries() {
        const entries = [];
        Object.keys(this.data).forEach((key) => {
            entries.push([JSON.parse(key), this.data[key]]);
        });
        return Promise.resolve(entries);
    }
    getKey(queryObject) {
        return JSON.stringify(queryObject);
    }
}

/** @internal */
class MemoryStorageController {
    constructor(storagePropertiesService) {
        this.storages = {};
        this.storageType = storagePropertiesService.getProperty('STORAGE_TYPE_IN_MEMORY');
    }
    getStorage(options) {
        let storage = this.storages[options.storageId];
        if (!storage) {
            storage = new MemoryStorage();
        }
        this.storages[options.storageId] = storage;
        return Promise.resolve(storage);
    }
    deleteStorage(storageId) {
        delete this.storages[storageId];
        return Promise.resolve(true);
    }
    getStorageIds() {
        return Promise.resolve(Object.keys(this.storages));
    }
}

/** @internal */
class WebStorage {
    constructor(controller, storageConfiguration) {
        this.controller = controller;
        this.storageConfiguration = storageConfiguration;
    }
    static ERR_INVALID_QUERY_OBJECT(queryObjec, storageId) {
        return new Error(`WebStorage exception for storage [${storageId}]. Invalid key [${queryObjec}]`);
    }
    clear() {
        this.controller.saveStorageData({});
        return Promise.resolve(true);
    }
    find(queryObject) {
        if (queryObject === undefined) {
            throw WebStorage.ERR_INVALID_QUERY_OBJECT(queryObject, this.storageConfiguration.storageId);
        }
        return this.get(queryObject).then((result) => [result]);
    }
    get(queryObject) {
        return this.controller.getStorageData().then((data) => {
            const key = this.getKeyFromQueryObj(queryObject);
            return Promise.resolve(data[key]);
        });
    }
    put(obj, queryObject) {
        return this.controller.getStorageData().then((data) => {
            data[this.getKeyFromQueryObj(queryObject)] = obj;
            this.controller.saveStorageData(data);
            return Promise.resolve(true);
        });
    }
    remove(queryObject) {
        if (queryObject === undefined) {
            throw WebStorage.ERR_INVALID_QUERY_OBJECT(queryObject, this.storageConfiguration.storageId);
        }
        const getPromise = this.get(queryObject);
        return this.controller.getStorageData().then((data) => {
            delete data[this.getKeyFromQueryObj(queryObject)];
            this.controller.saveStorageData(data);
            return getPromise;
        });
    }
    getLength() {
        return this.controller
            .getStorageData()
            .then((data) => Promise.resolve(Object.keys(data).length));
    }
    dispose() {
        return Promise.resolve(true);
    }
    entries() {
        const entries = [];
        return new Promise((resolve) => {
            this.controller.getStorageData().then((data) => {
                Object.keys(data).forEach((key) => {
                    entries.push([JSON.parse(key), data[key]]);
                });
                resolve(entries);
            });
        });
    }
    getKeyFromQueryObj(queryObj) {
        return JSON.stringify(queryObj);
    }
}

/** @internal */
class WebStorageBridge {
    constructor(controller, configuration) {
        this.controller = controller;
        this.configuration = configuration;
    }
    saveStorageData(data) {
        return this.controller.saveStorageData(this.configuration.storageId, data);
    }
    getStorageData() {
        return this.controller.getStorageData(this.configuration.storageId);
    }
}

/** @internal */
class AbstractWebStorageController {
    getStorage(configuration) {
        const bridge = new WebStorageBridge(this, configuration);
        const store = new WebStorage(bridge, configuration);
        const oldDispose = store.dispose;
        store.dispose = () => this.deleteStorage(configuration.storageId).then(() => oldDispose());
        return Promise.resolve(store);
    }
    deleteStorage(storageId) {
        const container = this.getWebStorageContainer();
        delete container[storageId];
        this.setWebStorageContainer(container);
        return Promise.resolve(true);
    }
    getStorageIds() {
        const keys = Object.keys(this.getWebStorageContainer());
        return Promise.resolve(keys);
    }
    saveStorageData(storageId, data) {
        const root = this.getWebStorageContainer();
        root[storageId] = data;
        this.setWebStorageContainer(root);
        return Promise.resolve(true);
    }
    getStorageData(storageId) {
        const root = this.getWebStorageContainer();
        if (root[storageId]) {
            return Promise.resolve(root[storageId]);
        }
        return Promise.resolve({});
    }
    setWebStorageContainer(data) {
        this.getStorageApi().setItem(this.getStorageRootKey(), JSON.stringify(data));
    }
    getWebStorageContainer() {
        const container = this.getStorageApi().getItem(this.getStorageRootKey());
        if (!container) {
            return {};
        }
        return JSON.parse(container);
    }
}

/** @internal */
class LocalStorageController extends AbstractWebStorageController {
    constructor(storagePropertiesService) {
        super();
        this.storagePropertiesService = storagePropertiesService;
        this.storageType = this.storagePropertiesService.getProperty('STORAGE_TYPE_LOCAL_STORAGE');
    }
    getStorageApi() {
        return window.localStorage;
    }
    getStorageRootKey() {
        return this.storagePropertiesService.getProperty('LOCAL_STORAGE_ROOT_KEY');
    }
}

/** @internal */
class SessionStorageController extends AbstractWebStorageController {
    constructor(storagePropertiesService) {
        super();
        this.storagePropertiesService = storagePropertiesService;
        this.storageType = this.storagePropertiesService.getProperty('STORAGE_TYPE_SESSION_STORAGE');
    }
    getStorageApi() {
        return window.sessionStorage;
    }
    getStorageRootKey() {
        return this.storagePropertiesService.getProperty('SESSION_STORAGE_ROOT_KEY');
    }
}

/** @internal */
let StorageManagerGateway = class StorageManagerGateway {
    constructor(storageManager) {
        this.storageManager = storageManager;
    }
    getStorageSanitityCheck(storageConfiguration) {
        return this.storageManager.getStorage(storageConfiguration).then(() => true, () => false);
    }
    deleteExpiredStorages(force) {
        return this.storageManager.deleteExpiredStorages(force);
    }
    deleteStorage(storageId, force) {
        return this.storageManager.deleteStorage(storageId, force);
    }
    hasStorage(storageId) {
        return this.storageManager.hasStorage(storageId);
    }
    getStorage(storageConfiguration) {
        throw new Error(`getStorage() is not supported from the StorageManagerGateway, please use the storage manager directly`);
    }
    registerStorageController(controller) {
        throw new Error(`registerStorageController() is not supported from the StorageManagerGateway, please use the storage manager directly`);
    }
};
StorageManagerGateway = __decorate([
    smarteditcommons.GatewayProxied('getStorageSanitityCheck', 'deleteExpiredStorages', 'deleteStorage', 'hasStorage'),
    __param(0, core.Inject(smarteditcommons.DO_NOT_USE_STORAGE_MANAGER_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IStorageManager])
], StorageManagerGateway);

/** @internal */
let StorageGateway = class StorageGateway {
    constructor(storageManager) {
        this.storageManager = storageManager;
    }
    handleStorageRequest(storageConfiguration, method, args) {
        return new Promise((resolve, reject) => {
            this.storageManager.getStorage(storageConfiguration).then((storage) => resolve(storage[method](...args)), (reason) => reject(reason));
        });
    }
};
StorageGateway = __decorate([
    smarteditcommons.GatewayProxied(),
    __param(0, core.Inject(smarteditcommons.DO_NOT_USE_STORAGE_MANAGER_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IStorageManager])
], StorageGateway);

/** @internal */
class MetaDataMapStorage {
    constructor(storageKey) {
        this.storageKey = storageKey;
    }
    getAll() {
        const allMetaData = [];
        const data = this.getDataFromStore();
        Object.keys(data).forEach((key) => {
            allMetaData.push(data[key]);
        });
        return allMetaData;
    }
    get(storageId) {
        return this.getDataFromStore()[storageId];
    }
    put(storageId, value) {
        const data = this.getDataFromStore();
        data[storageId] = value;
        this.setDataInStore(data);
    }
    remove(storageId) {
        const data = this.getDataFromStore();
        delete data[storageId];
        this.setDataInStore(data);
    }
    removeAll() {
        window.localStorage.removeItem(this.storageKey);
    }
    getDataFromStore() {
        try {
            const store = window.localStorage.getItem(this.storageKey);
            if (store === null) {
                return {};
            }
            return JSON.parse(store);
        }
        catch (e) {
            return {};
        }
    }
    setDataInStore(data) {
        window.localStorage.setItem(this.storageKey, JSON.stringify(data));
    }
}

var /* @ngInject */ StorageManager_1;
/** @internal */
let /* @ngInject */ StorageManager = /* @ngInject */ StorageManager_1 = class /* @ngInject */ StorageManager {
    constructor(logService, storagePropertiesService) {
        this.logService = logService;
        this.storagePropertiesService = storagePropertiesService;
        this.storageControllers = {};
        this.storages = {};
        this.storageMetaDataMap = new MetaDataMapStorage(this.storagePropertiesService.getProperty('LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA'));
    }
    static ERR_NO_STORAGE_TYPE_CONTROLLER(storageType) {
        return new Error(`StorageManager Error: Cannot create storage. No Controller available to handle type [${storageType}]`);
    }
    registerStorageController(controller) {
        this.storageControllers[controller.storageType] = controller;
    }
    getStorage(storageConfiguration) {
        this.setDefaultStorageOptions(storageConfiguration);
        const loadExistingStorage = this.hasStorage(storageConfiguration.storageId);
        let pendingValidation = Promise.resolve(true);
        if (loadExistingStorage) {
            const metadata = this.storageMetaDataMap.get(storageConfiguration.storageId);
            pendingValidation = this.verifyMetaData(metadata, storageConfiguration);
        }
        return new Promise((resolve, reject) => {
            pendingValidation
                .then(() => {
                if (this.storages[storageConfiguration.storageId]) {
                    this.updateStorageMetaData(storageConfiguration);
                    resolve(this.storages[storageConfiguration.storageId]);
                }
                else {
                    this.getStorageController(storageConfiguration.storageType)
                        .getStorage(storageConfiguration)
                        .then((newStorage) => {
                        this.applyDisposeDecorator(storageConfiguration.storageId, newStorage);
                        this.updateStorageMetaData(storageConfiguration);
                        this.storages[storageConfiguration.storageId] = newStorage;
                        resolve(newStorage);
                    });
                }
            })
                .catch((e) => reject(e));
        });
    }
    hasStorage(storageId) {
        // true if we have metadata for it
        return !!this.storageMetaDataMap.get(storageId);
    }
    deleteStorage(storageId, force = false) {
        delete this.storages[storageId];
        if (!this.hasStorage(storageId)) {
            return Promise.resolve(true);
        }
        const metaData = this.storageMetaDataMap.get(storageId);
        if (metaData) {
            let ctrl;
            try {
                ctrl = this.getStorageController(metaData.storageType);
            }
            catch (e) {
                // silently fail on no storage type handler
                if (force) {
                    this.storageMetaDataMap.remove(storageId);
                }
                return Promise.resolve(true);
            }
            return ctrl.deleteStorage(storageId).then(() => {
                this.storageMetaDataMap.remove(storageId);
                return Promise.resolve(true);
            });
        }
        else {
            return Promise.resolve(true);
        }
    }
    deleteExpiredStorages(force = false) {
        const deletePromises = [];
        const storageMetaDatas = this.storageMetaDataMap.getAll();
        storageMetaDatas.forEach((metaData) => {
            if (this.isStorageExpired(metaData)) {
                deletePromises.push(this.deleteStorage(metaData.storageId, force));
            }
        });
        return Promise.all(deletePromises).then(() => true, () => false);
    }
    updateStorageMetaData(storageConfiguration) {
        this.storageMetaDataMap.put(storageConfiguration.storageId, {
            storageId: storageConfiguration.storageId,
            storageType: storageConfiguration.storageType,
            storageVersion: storageConfiguration.storageVersion,
            lastAccess: Date.now()
        });
    }
    isStorageExpired(metaData) {
        const timeSinceLastAccess = Date.now() - metaData.lastAccess;
        let idleExpiryTime = metaData.expiresAfterIdle;
        if (idleExpiryTime === undefined) {
            idleExpiryTime = this.storagePropertiesService.getProperty('STORAGE_IDLE_EXPIRY');
        }
        return timeSinceLastAccess >= idleExpiryTime;
    }
    applyDisposeDecorator(storageId, storage) {
        const originalDispose = storage.dispose;
        storage.dispose = () => this.deleteStorage(storageId).then(() => originalDispose());
    }
    getStorageController(storageType) {
        const controller = this.storageControllers[storageType];
        if (!controller) {
            throw /* @ngInject */ StorageManager_1.ERR_NO_STORAGE_TYPE_CONTROLLER(storageType);
        }
        return controller;
    }
    verifyMetaData(metadata, configuration) {
        if (metadata.storageVersion !== configuration.storageVersion) {
            this.logService.warn(`StorageManager - Removing old storage version for storage ${metadata.storageId}`);
            return this.deleteStorage(metadata.storageId);
        }
        if (metadata.storageType !== configuration.storageType) {
            this.logService.warn(`StorageManager - Detected a change in storage type for existing storage. Removing old storage with id ${configuration.storageId}`);
            return this.deleteStorage(metadata.storageId);
        }
        return Promise.resolve(true);
    }
    setDefaultStorageOptions(options) {
        if (!options.storageVersion || options.storageVersion.length <= 0) {
            options.storageVersion = '0';
        }
    }
};
/* @ngInject */ StorageManager = /* @ngInject */ StorageManager_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        smarteditcommons.IStoragePropertiesService])
], /* @ngInject */ StorageManager);

/**
 *
 * defaultStorageProperties are the default [IStorageProperties]{@link IStorageProperties} of the
 * storage system. These values should not be reference directly at build/compile time, but rather through the
 * angular provider that exposes them. See [IStoragePropertiesService]{@link IStoragePropertiesService}
 * for more details.
 *
 * ```
 * {
 *     STORAGE_IDLE_EXPIRY: 1000 * 60 * 60 * 24 * 30, // 30 days
 *     STORAGE_TYPE_LOCAL_STORAGE: "se.storage.type.localstorage",
 *     STORAGE_TYPE_SESSION_STORAGE: "se.storage.type.sessionstorage",
 *     STORAGE_TYPE_IN_MEMORY: "se.storage.type.inmemory",
 *     LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: "se.storage.storagemanager.metadata",
 *     LOCAL_STORAGE_ROOT_KEY: "se.storage.root",
 *     SESSION_STORAGE_ROOT_KEY: "se.storage.root"
 * }
 * ```
 */
/** @internal */
const defaultStorageProperties = {
    STORAGE_IDLE_EXPIRY: 1000 * 60 * 60 * 24 * 30,
    // STORAGE TYPES
    STORAGE_TYPE_LOCAL_STORAGE: 'se.storage.type.localstorage',
    STORAGE_TYPE_SESSION_STORAGE: 'se.storage.type.sessionstorage',
    STORAGE_TYPE_IN_MEMORY: 'se.storage.type.inmemory',
    // LOCAL STORAGE KEYS
    LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: 'se.storage.storagemanager.metadata',
    LOCAL_STORAGE_ROOT_KEY: 'se.storage.root',
    SESSION_STORAGE_ROOT_KEY: 'se.storage.root'
};

/**
 * The storagePropertiesService is a provider that implements the IStoragePropertiesService
 * interface and exposes the default storage properties. These properties are used to bootstrap various
 * pieces of the storage system.
 * By Means of StorageModule.configure() you would might change the default localStorage key names, or storage types.
 */
/** @internal */
let /* @ngInject */ StoragePropertiesService = class /* @ngInject */ StoragePropertiesService {
    constructor(storageProperties) {
        this.properties = lo__namespace.cloneDeep(defaultStorageProperties);
        storageProperties.forEach((properties) => {
            lo__namespace.merge(this.properties, properties);
        });
    }
    getProperty(propertyName) {
        return this.properties[propertyName];
    }
};
/* @ngInject */ StoragePropertiesService = __decorate([
    core.Injectable(),
    __param(0, core.Inject(smarteditcommons.STORAGE_PROPERTIES_TOKEN)),
    __metadata("design:paramtypes", [Array])
], /* @ngInject */ StoragePropertiesService);

var StorageModule_1;
let StorageModule = StorageModule_1 = class StorageModule {
    static forRoot(properties = {}) {
        return {
            ngModule: StorageModule_1,
            providers: [
                {
                    provide: smarteditcommons.STORAGE_PROPERTIES_TOKEN,
                    multi: true,
                    useValue: properties
                }
            ]
        };
    }
};
StorageModule = StorageModule_1 = __decorate([
    core.NgModule({
        providers: [
            /**
             * The StorageManagerFactory implements the IStorageManagerFactory interface, and produces
             * StorageManager instances. Typically you would only create one StorageManager instance, and expose it through a
             * service for the rest of your application. StorageManagers produced from this factory will take care of
             * name-spacing storage ids, preventing clashes between extensions, or other storages with the same ID.
             * All StorageManagers produced by the storageManagerFactory delegate to the same single root StorageManager.
             *
             */
            {
                provide: smarteditcommons.IStoragePropertiesService,
                useClass: StoragePropertiesService
            },
            {
                provide: smarteditcommons.DO_NOT_USE_STORAGE_MANAGER_TOKEN,
                useClass: StorageManager
            },
            {
                provide: smarteditcommons.IStorageGateway,
                useClass: StorageGateway
            },
            {
                provide: smarteditcommons.IStorageManagerGateway,
                useClass: StorageManagerGateway
            },
            {
                provide: smarteditcommons.IStorageManagerFactory,
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                useFactory: (logService, doNotUseStorageManager) => new smarteditcommons.StorageManagerFactory(doNotUseStorageManager),
                deps: [smarteditcommons.LogService, smarteditcommons.DO_NOT_USE_STORAGE_MANAGER_TOKEN]
            },
            {
                provide: smarteditcommons.IStorageManager,
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                useFactory: (storageManagerFactory) => storageManagerFactory.getStorageManager('se.nsp'),
                deps: [smarteditcommons.IStorageManagerFactory]
            },
            smarteditcommons.moduleUtils.initialize((storagePropertiesService, seStorageManager) => {
                seStorageManager.registerStorageController(new LocalStorageController(storagePropertiesService));
                seStorageManager.registerStorageController(new SessionStorageController(storagePropertiesService));
                seStorageManager.registerStorageController(new MemoryStorageController(storagePropertiesService));
            }, [smarteditcommons.IStoragePropertiesService, smarteditcommons.IStorageManager])
        ]
    })
], StorageModule);

var PermissionService_1;
/**
 * The name used to register the default rule.
 */
const DEFAULT_DEFAULT_RULE_NAME = 'se.permission.service.default.rule';
let PermissionService = PermissionService_1 = class PermissionService extends smarteditcommons.IPermissionService {
    constructor(logService, systemEventService, crossFrameEventService) {
        super();
        this.logService = logService;
        this.systemEventService = systemEventService;
        this.crossFrameEventService = crossFrameEventService;
        this._registerEventHandlers();
    }
    static resetForTests() {
        PermissionService_1.rules = [];
        PermissionService_1.permissionsRegistry = [];
        PermissionService_1.cachedResults = {};
    }
    static hasCacheRegion(ruleName) {
        return PermissionService_1.cachedResults.hasOwnProperty(ruleName);
    }
    static getCacheRegion(ruleName) {
        return PermissionService_1.cachedResults[ruleName];
    }
    getPermission(permissionName) {
        return PermissionService_1.permissionsRegistry.find((permission) => permission.aliases.indexOf(permissionName) > -1);
    }
    unregisterDefaultRule() {
        const defaultRule = this._getRule(DEFAULT_DEFAULT_RULE_NAME);
        if (defaultRule) {
            PermissionService_1.rules.splice(PermissionService_1.rules.indexOf(defaultRule), 1);
        }
    }
    registerPermission(permission) {
        this._validatePermission(permission);
        PermissionService_1.permissionsRegistry.push({
            aliases: permission.aliases,
            rules: permission.rules
        });
    }
    hasCachedResult(ruleName, key) {
        return (PermissionService_1.hasCacheRegion(ruleName) &&
            PermissionService_1.getCacheRegion(ruleName).hasOwnProperty(key));
    }
    clearCache() {
        PermissionService_1.cachedResults = {};
        this.crossFrameEventService.publish(smarteditcommons.EVENTS.PERMISSION_CACHE_CLEANED).catch((reason) => {
            this.logService.debug(`PermissionService - clearCache: ${reason}`);
        });
    }
    isPermitted(permissions) {
        const rulePermissionNames = this._mapRuleNameToPermissionNames(permissions);
        const rulePromises = this._getRulePromises(rulePermissionNames);
        const names = Object.keys(rulePromises);
        const promises = names.map((key) => rulePromises[key]);
        const onSuccess = (permissionResults) => {
            const result = names.reduce((acc, name, index) => {
                acc[name] = permissionResults[index];
                return acc;
            }, {});
            this._updateCache(rulePermissionNames, result);
            return true;
        };
        const onError = (result) => {
            if (result === false) {
                return result;
            }
            this.logService.error(result);
            return result === undefined ? false : result;
        };
        return Promise.all(promises).then(onSuccess, onError);
    }
    /**
     * This method adds a promise obtained by calling the pre-configured rule.verify function to the rulePromises
     * map if the result does not exist in the rule's cache. Otherwise, a promise that contains the cached result
     * is added.
     *
     * The promise obtained from the rule.verify function is chained to allow short-circuiting the permission
     * verification process. If a rule resolves with a false result or with an error, the chained promise is
     * rejected to stop the verification process without waiting for all other rules to resolve.
     *
     * @param rulePromises An object that maps rule names to promises.
     * @param rulePermissionNames An object that maps rule names to permission name arrays.
     * @param ruleName The name of the rule to verify.
     */
    _addRulePromise(rulePromises, rulePermissionNames, ruleName) {
        const rule = this._getRule(ruleName);
        const permissionNameObjs = rulePermissionNames[ruleName];
        const cacheKey = this._generateCacheKey(permissionNameObjs);
        let rulePromise;
        if (this.hasCachedResult(ruleName, cacheKey)) {
            rulePromise = Promise.resolve(this._getCachedResult(ruleName, cacheKey));
        }
        else {
            rulePromise = this._callRuleVerify(rule.names.join('-'), permissionNameObjs).then((isPermitted) => isPermitted ? Promise.resolve(true) : Promise.reject(false));
        }
        rulePromises[ruleName] = rulePromise;
    }
    /**
     * This method validates a permission name. Permission names need to be prefixed by at least one
     * namespace followed by a "." character to be valid.
     *
     * Example: se.mynamespace is valid.
     * Example: mynamespace is not valid.
     */
    _isPermissionNameValid(permissionName) {
        const checkNameSpace = /^[A-Za-z0-9_\-]+\.[A-Za-z0-9_\-\.]+/;
        return checkNameSpace.test(permissionName);
    }
    /**
     * This method returns an object that maps rule names to promises.
     */
    _getRulePromises(rulePermissionNames) {
        const rulePromises = {};
        Object.keys(rulePermissionNames).forEach((ruleName) => {
            this._addRulePromise.call(this, rulePromises, rulePermissionNames, ruleName);
        });
        return rulePromises;
    }
    /**
     * This method returns true if a default rule is already registered.
     *
     * @returns true if the default rule has been registered, false otherwise.
     */
    _hasDefaultRule() {
        return !!this._getRule(DEFAULT_DEFAULT_RULE_NAME);
    }
    /**
     * This method returns the rule's cached result for the given key.
     *
     * @param ruleName The name of the rule for which to lookup the cached result.
     * @param key The cached key to lookup..
     *
     * @returns The cached result, if it exists, null otherwise.
     */
    _getCachedResult(ruleName, key) {
        return PermissionService_1.hasCacheRegion(ruleName)
            ? PermissionService_1.getCacheRegion(ruleName)[key]
            : null;
    }
    /**
     * This method generates a key to store a rule's result for a given combination of
     * permissions in its cache. It is done by sorting the list of permissions by name
     * and serializing it.
     *
     * @param permissions A list of permissions with a name and context.
     *
     * [{
     *     name: "permission.name"
     *     context: {
     *         key: "value"
     *     }
     * }]
     *
     * @returns The serialized sorted list of permissions.
     */
    _generateCacheKey(permissions) {
        return JSON.stringify(permissions.sort((permissionA, permissionB) => {
            const nameA = permissionA.name;
            const nameB = permissionB.name;
            return nameA === nameB ? 0 : nameA < nameB ? -1 : 1;
        }));
    }
    /**
     * This method goes through the permission name arrays associated to rule names to remove any duplicate
     * permission names.
     *
     * If one or more permission names with the same context are found in a rule name's permission name array,
     * only one entry is kept.
     */
    _removeDuplicatePermissionNames(rulePermissionNames) {
        Object.keys(rulePermissionNames).forEach((ruleName) => {
            rulePermissionNames[ruleName] = rulePermissionNames[ruleName].filter((currentPermission) => {
                const existingPermission = rulePermissionNames[ruleName].find((permission) => permission.name === currentPermission.name);
                if (existingPermission === currentPermission) {
                    return true;
                }
                else {
                    const existingPermissionContext = existingPermission.context;
                    const currentPermissionContext = currentPermission.context;
                    return (JSON.stringify(existingPermissionContext) !==
                        JSON.stringify(currentPermissionContext));
                }
            });
        });
    }
    /**
     * This method returns an object mapping rule name to permission name arrays.
     *
     * It will iterate through the given permission name object array to extract the permission names and contexts,
     * populate the map and clean it up by removing duplicate permission name and context pairs.
     */
    _mapRuleNameToPermissionNames(permissions) {
        const rulePermissionNames = {};
        permissions.forEach((permission) => {
            if (!permission.names) {
                throw Error('Requested Permission requires at least one name');
            }
            const permissionNames = permission.names;
            const permissionContext = permission.context;
            permissionNames.forEach((permissionName) => {
                this._populateRulePermissionNames(rulePermissionNames, permissionName, permissionContext);
            });
        });
        this._removeDuplicatePermissionNames(rulePermissionNames);
        return rulePermissionNames;
    }
    /**
     * This method will populate rulePermissionNames with the rules associated to the permission with the given
     * permissionName.
     *
     * If no permission is registered with the given permissionName and a default rule is registered, the default
     * rule is added to rulePermissionNames.
     *
     * If no permission is registered with the given permissionName and no default rule is registered, an error
     * is thrown.
     */
    _populateRulePermissionNames(rulePermissionNames, permissionName, permissionContext) {
        const permission = this.getPermission(permissionName);
        const permissionHasRules = !!permission && !!permission.rules && permission.rules.length > 0;
        if (permissionHasRules) {
            permission.rules.forEach((ruleName) => {
                this._addPermissionName(rulePermissionNames, ruleName, permissionName, permissionContext);
            });
        }
        else if (this._hasDefaultRule()) {
            this._addPermissionName(rulePermissionNames, DEFAULT_DEFAULT_RULE_NAME, permissionName, permissionContext);
        }
        else {
            throw Error('Permission has no rules');
        }
    }
    /**
     * This method will add an object with the permissionName and permissionContext to rulePermissionNames.
     *
     * Since rules can have multiple names, the map will use the first name in the rule's name list as its key.
     * This way, each rule will be called only once for every permission name and context.
     *
     * If the rule associated to a given rule name is already in rulePermissionNames, the permission will be
     * appended to the associated array. Otherwise, the rule name is added to the map and its permission name array
     * is created.
     */
    _addPermissionName(rulePermissionNames, ruleName, permissionName, permissionContext) {
        const rule = this._getRule(ruleName);
        if (!rule) {
            throw Error('Permission found but no rule found named: ' + ruleName);
        }
        ruleName = rule.names[0];
        if (!rulePermissionNames.hasOwnProperty(ruleName)) {
            rulePermissionNames[ruleName] = [];
        }
        rulePermissionNames[ruleName].push({
            name: permissionName,
            context: permissionContext
        });
    }
    /**
     * This method returns the rule registered with the given name.
     *
     * @param ruleName The name of the rule to lookup.
     *
     * @returns rule The rule with the given name, undefined otherwise.
     */
    _getRule(ruleName) {
        return PermissionService_1.rules.find((rule) => rule.names.indexOf(ruleName) > -1);
    }
    _validationRule(ruleConfiguration) {
        ruleConfiguration.names.forEach((ruleName) => {
            if (this._getRule(ruleName)) {
                throw Error('Rule already exists: ' + ruleName);
            }
        });
    }
    _validatePermission(permissionConfiguration) {
        if (!(permissionConfiguration.aliases instanceof Array)) {
            throw Error('Permission aliases must be an array');
        }
        if (permissionConfiguration.aliases.length < 1) {
            throw Error('Permission requires at least one alias');
        }
        if (!(permissionConfiguration.rules instanceof Array)) {
            throw Error('Permission rules must be an array');
        }
        if (permissionConfiguration.rules.length < 1) {
            throw Error('Permission requires at least one rule');
        }
        permissionConfiguration.aliases.forEach((permissionName) => {
            if (this.getPermission(permissionName)) {
                throw Error('Permission already exists: ' + permissionName);
            }
            if (!this._isPermissionNameValid(permissionName)) {
                throw Error('Permission aliases must be prefixed with namespace and a full stop');
            }
        });
        permissionConfiguration.rules.forEach((ruleName) => {
            if (!this._getRule(ruleName)) {
                throw Error('Permission found but no rule found named: ' + ruleName);
            }
        });
    }
    _updateCache(rulePermissionNames, permissionResults) {
        Object.keys(permissionResults).forEach((ruleName) => {
            const cacheKey = this._generateCacheKey(rulePermissionNames[ruleName]);
            const cacheValue = permissionResults[ruleName];
            this._addCachedResult(ruleName, cacheKey, cacheValue);
        });
    }
    _addCachedResult(ruleName, key, result) {
        if (!PermissionService_1.hasCacheRegion(ruleName)) {
            PermissionService_1.cachedResults[ruleName] = {};
        }
        PermissionService_1.cachedResults[ruleName][key] = result;
    }
    _registerRule(ruleConfiguration) {
        this._validationRule(ruleConfiguration);
        if (ruleConfiguration.names &&
            ruleConfiguration.names.length &&
            ruleConfiguration.names.indexOf(DEFAULT_DEFAULT_RULE_NAME) > -1) {
            throw Error('Register default rule using permissionService.registerDefaultRule()');
        }
        PermissionService_1.rules.push({
            names: ruleConfiguration.names
        });
    }
    _registerDefaultRule(ruleConfiguration) {
        this._validationRule(ruleConfiguration);
        if (ruleConfiguration.names &&
            ruleConfiguration.names.length &&
            ruleConfiguration.names.indexOf(DEFAULT_DEFAULT_RULE_NAME) === -1) {
            throw Error('Default rule name must be DEFAULT_RULE_NAME');
        }
        PermissionService_1.rules.push({
            names: ruleConfiguration.names
        });
    }
    _callRuleVerify(ruleKey, permissionNameObjs) {
        if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
            return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
        }
        // ask inner application for verify function.
        return this._remoteCallRuleVerify(ruleKey, permissionNameObjs);
    }
    _registerEventHandlers() {
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, this.clearCache.bind(this));
        this.systemEventService.subscribe(smarteditcommons.EVENTS.EXPERIENCE_UPDATE, this.clearCache.bind(this));
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, this.clearCache.bind(this));
        this.crossFrameEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_CHANGED, this.clearCache.bind(this));
    }
    _remoteCallRuleVerify(name, permissionNameObjs) {
        'proxyFunction';
        return null;
    }
};
PermissionService.rules = [];
PermissionService.permissionsRegistry = [];
PermissionService.cachedResults = {};
PermissionService = PermissionService_1 = __decorate([
    smarteditcommons.GatewayProxied('isPermitted', 'clearCache', 'registerPermission', 'unregisterDefaultRule', 'registerDefaultRule', 'registerRule', '_registerRule', '_remoteCallRuleVerify', '_registerDefaultRule'),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        smarteditcommons.SystemEventService,
        smarteditcommons.CrossFrameEventService])
], PermissionService);

/**
 * The catalog version permission service is used to check if the current user has been granted certain permissions
 * on a given catalog ID and catalog Version.
 */
let /* @ngInject */ CatalogVersionPermissionRestService = class /* @ngInject */ CatalogVersionPermissionRestService {
    constructor(restServiceFactory, sessionService) {
        this.restServiceFactory = restServiceFactory;
        this.sessionService = sessionService;
        this.URI = '/permissionswebservices/v1/permissions/catalogs/search';
    }
    /**
     * This method returns permissions from the Catalog Version Permissions Service API.
     *
     * Sample Request:
     * POST /permissionswebservices/v1/permissions/catalogs/search?catalogId=apparel-deContentCatalog&catalogVersion=Online
     *
     * Sample Response from API:
     * {
     * "permissionsList": [
     *     {
     *       "catalogId": "apparel-deContentCatalog",
     *       "catalogVersion": "Online",
     *       "permissions": [
     *         {
     *           "key": "read",
     *           "value": "true"
     *         },
     *         {
     *           "key": "write",
     *           "value": "false"
     *         }
     *       ],
     *      "syncPermissions": [
     *        {
     *          "canSynchronize": "true",
     *          "targetCatalogVersion": "Online"
     *        }
     *     }
     *    ]
     * }
     *
     * Sample Response returned by the service:
     * {
     *   "catalogId": "apparel-deContentCatalog",
     *   "catalogVersion": "Online",
     *   "permissions": [
     *      {
     *        "key": "read",
     *        "value": "true"
     *      },
     *      {
     *        "key": "write",
     *        "value": "false"
     *      }
     *     ],
     *    "syncPermissions": [
     *      {
     *        "canSynchronize": "true",
     *        "targetCatalogVersion": "Online"
     *      }
     *    ]
     *  }
     *
     * @param catalogId The Catalog ID
     * @param catalogVersion The Catalog Version name
     *
     * @returns A Promise which returns an object exposing a permissions array containing the catalog version permissions
     */
    getCatalogVersionPermissions(catalogId, catalogVersion) {
        this.validateParams(catalogId, catalogVersion);
        return this.sessionService.getCurrentUsername().then((principal) => {
            const restService = this.restServiceFactory.get(this.URI);
            return restService
                .queryByPost({ principalUid: principal }, { catalogId, catalogVersion })
                .then(({ permissionsList }) => permissionsList[0] || {});
        });
    }
    // TODO: When everything has been migrated to typescript it is sufficient enough to remove this validation.
    validateParams(catalogId, catalogVersion) {
        if (!catalogId) {
            throw new Error('catalog.version.permission.service.catalogid.is.required');
        }
        if (!catalogVersion) {
            throw new Error('catalog.version.permission.service.catalogversion.is.required');
        }
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.userEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ CatalogVersionPermissionRestService.prototype, "getCatalogVersionPermissions", null);
/* @ngInject */ CatalogVersionPermissionRestService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.RestServiceFactory,
        smarteditcommons.ISessionService])
], /* @ngInject */ CatalogVersionPermissionRestService);

window.__smartedit__.addDecoratorPayload("Component", "HeartBeatAlertComponent", {
    selector: 'se-heartbeat-alert',
    template: `<div><span>{{ 'se.heartbeat.failure1' | translate }}</span> <span><a style="cursor:pointer" (click)="switchToPreviewMode()">{{ 'se.heartbeat.failure2' | translate }}</a></span></div>`
});
let HeartBeatAlertComponent = class HeartBeatAlertComponent {
    constructor(alertRef, perspectiveService, crossFrameEventService) {
        this.alertRef = alertRef;
        this.perspectiveService = perspectiveService;
        this.crossFrameEventService = crossFrameEventService;
    }
    switchToPreviewMode() {
        this.alertRef.dismiss();
        this.perspectiveService.switchTo(smarteditcommons.NONE_PERSPECTIVE);
        this.crossFrameEventService.publish(smarteditcommons.EVENT_STRICT_PREVIEW_MODE_REQUESTED, true);
    }
};
HeartBeatAlertComponent = __decorate([
    core.Component({
        selector: 'se-heartbeat-alert',
        template: `<div><span>{{ 'se.heartbeat.failure1' | translate }}</span> <span><a style="cursor:pointer" (click)="switchToPreviewMode()">{{ 'se.heartbeat.failure2' | translate }}</a></span></div>`
    }),
    __param(2, core.Inject(smarteditcommons.EVENT_SERVICE)),
    __metadata("design:paramtypes", [core$1.AlertRef,
        smarteditcommons.IPerspectiveService,
        smarteditcommons.CrossFrameEventService])
], HeartBeatAlertComponent);

let /* @ngInject */ AlertFactory = class /* @ngInject */ AlertFactory extends smarteditcommons.BaseAlertFactory {
    constructor(fundamentalAlertService, translateService, ALERT_CONFIG_DEFAULTS) {
        super(fundamentalAlertService, translateService, ALERT_CONFIG_DEFAULTS);
    }
    createAlert(alertConf) {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        return super.createAlert(alertConf);
    }
    createInfo(alertConf) {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = smarteditcommons.IAlertServiceType.INFO;
        return super.createInfo(alertConf);
    }
    createDanger(alertConf) {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = smarteditcommons.IAlertServiceType.DANGER;
        return super.createDanger(alertConf);
    }
    createWarning(alertConf) {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = smarteditcommons.IAlertServiceType.WARNING;
        return super.createWarning(alertConf);
    }
    createSuccess(alertConf) {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = smarteditcommons.IAlertServiceType.SUCCESS;
        return super.createSuccess(alertConf);
    }
    /**
     * Accepts message string or config object
     * Will convert a str param to { message: str }
     */
    getAlertConfigFromStringOrConfig(strOrConf) {
        if (typeof strOrConf === 'string') {
            return {
                message: strOrConf,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            };
        }
        const config = strOrConf;
        return Object.assign({}, config);
    }
};
/* @ngInject */ AlertFactory = __decorate([
    core.Injectable(),
    __param(2, core.Inject(smarteditcommons.ALERT_CONFIG_DEFAULTS_TOKEN)),
    __metadata("design:paramtypes", [core$1.AlertService,
        core$2.TranslateService,
        core$1.AlertConfig])
], /* @ngInject */ AlertFactory);

let /* @ngInject */ AlertService = class /* @ngInject */ AlertService extends smarteditcommons.BaseAlertService {
    constructor(_alertFactory) {
        super(_alertFactory);
        this._alertFactory = _alertFactory;
    }
    showAlert(alertConf) {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showAlert(alertConf);
    }
    showInfo(alertConf) {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showInfo(alertConf);
    }
    showDanger(alertConf) {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showDanger(alertConf);
    }
    showWarning(alertConf) {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showWarning(alertConf);
    }
    showSuccess(alertConf) {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showSuccess(alertConf);
    }
};
/* @ngInject */ AlertService = __decorate([
    smarteditcommons.GatewayProxied(),
    core.Injectable(),
    __metadata("design:paramtypes", [AlertFactory])
], /* @ngInject */ AlertService);

exports.AlertServiceModule = class AlertServiceModule {
};
exports.AlertServiceModule = __decorate([
    core.NgModule({
        imports: [smarteditcommons.AlertModule],
        providers: [
            AlertFactory,
            {
                provide: smarteditcommons.IAlertService,
                useClass: AlertService
            }
        ]
    })
], exports.AlertServiceModule);

var /* @ngInject */ HeartBeatService_1;
/* @internal */
let /* @ngInject */ HeartBeatService = /* @ngInject */ HeartBeatService_1 = class /* @ngInject */ HeartBeatService {
    constructor(HEART_BEAT_TIMEOUT_THRESHOLD_MS, translate, routingService, windowUtils, alertFactory, crossFrameEventService, gatewayFactory, sharedDataService) {
        this.HEART_BEAT_TIMEOUT_THRESHOLD_MS = HEART_BEAT_TIMEOUT_THRESHOLD_MS;
        this.routingService = routingService;
        this.windowUtils = windowUtils;
        this.crossFrameEventService = crossFrameEventService;
        this.sharedDataService = sharedDataService;
        this.reconnectingInProgress = false;
        /**
         * @internal
         * Hide all alerts and cancel all pending actions and timers.
         */
        this.resetAndStop = () => {
            this.reconnectingInProgress = false;
            if (this.cancellableTimeoutTimer) {
                clearTimeout(this.cancellableTimeoutTimer);
                this.cancellableTimeoutTimer = null;
            }
            this.reconnectingAlert.hide();
            this.reconnectedAlert.hide();
        };
        /**
         * Connection to iframe has been lost, show reconnected alert to user
         */
        this.connectionLost = () => {
            this.resetAndStop();
            if (!!this.windowUtils.getGatewayTargetFrame()) {
                this.reconnectingAlert.show();
            }
            this.reconnectingInProgress = true;
        };
        this.reconnectingAlert = alertFactory.createInfo({
            component: HeartBeatAlertComponent,
            duration: -1,
            dismissible: false,
            minWidth: '',
            mousePersist: true,
            width: '300px'
        });
        this.reconnectedAlert = alertFactory.createInfo({
            message: translate.instant('se.heartbeat.reconnection'),
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        const heartBeatGateway = gatewayFactory.createGateway(/* @ngInject */ HeartBeatService_1.HEART_BEAT_GATEWAY_ID);
        heartBeatGateway.subscribe(/* @ngInject */ HeartBeatService_1.HEART_BEAT_MSG_ID, () => this.heartBeatReceived());
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, () => {
            this.resetAndStop();
            // assume every page is smarteditable ¯\_(ツ)_/¯
            return this.heartBeatReceived();
        });
        this.routingService.routeChangeSuccess().subscribe((event) => {
            const url = this.routingService.getCurrentUrlFromEvent(event);
            if (url === smarteditcommons.STORE_FRONT_CONTEXT) {
                return this.heartBeatReceived();
            }
            return Promise.resolve();
        });
        this.routingService.routeChangeStart().subscribe(() => {
            this.resetAndStop();
        });
    }
    /**
     * @internal
     * Heartbeat received from iframe, show reconnected if connection was previously
     * lost, and restart the timer to wait for iframe heartbeat
     */
    heartBeatReceived() {
        const reconnecting = this.reconnectingInProgress;
        this.resetAndStop();
        if (reconnecting) {
            if (!!this.windowUtils.getGatewayTargetFrame()) {
                this.reconnectedAlert.show();
            }
            this.reconnectingInProgress = false;
            // Publish an event to enable the perspective selector in case if it is disabled
            this.crossFrameEventService.publish(smarteditcommons.EVENT_STRICT_PREVIEW_MODE_REQUESTED, false);
        }
        return this.sharedDataService
            .get('configuration')
            .then(({ heartBeatTimeoutThreshold }) => {
            if (!heartBeatTimeoutThreshold) {
                heartBeatTimeoutThreshold = this.HEART_BEAT_TIMEOUT_THRESHOLD_MS;
            }
            this.cancellableTimeoutTimer = this.windowUtils.runTimeoutOutsideAngular(this.connectionLost, +heartBeatTimeoutThreshold);
        });
    }
};
/* @ngInject */ HeartBeatService.HEART_BEAT_GATEWAY_ID = 'heartBeatGateway';
/* @ngInject */ HeartBeatService.HEART_BEAT_MSG_ID = 'heartBeat';
/* @ngInject */ HeartBeatService = /* @ngInject */ HeartBeatService_1 = __decorate([
    core.Injectable(),
    __param(0, core.Inject(smarteditcommons.HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN)),
    __metadata("design:paramtypes", [Number, core$2.TranslateService,
        smarteditcommons.SmarteditRoutingService,
        smarteditcommons.WindowUtils,
        AlertFactory,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.GatewayFactory,
        smarteditcommons.ISharedDataService])
], /* @ngInject */ HeartBeatService);

/** @internal */
let /* @ngInject */ ConfigurationService = class /* @ngInject */ ConfigurationService {
    constructor(logService, loadConfigManagerService, restServiceFactory) {
        this.logService = logService;
        this.loadConfigManagerService = loadConfigManagerService;
        this.restServiceFactory = restServiceFactory;
        // Constants
        this.ABSOLUTE_URI_NOT_APPROVED = 'URI_EXCEPTION';
        this.ABSOLUTE_URI_REGEX = /(\"[A-Za-z]+:\/|\/\/)/;
        this.configuration = [];
        this.editorCRUDService = this.restServiceFactory.get(smarteditcommons.CONFIGURATION_URI, 'key');
    }
    /*
     * The Add Entry method adds an entry to the list of configurations.
     *
     */
    addEntry() {
        const item = { key: '', value: '', isNew: true, uuid: lo__namespace.uniqueId() };
        this.configuration = [item, ...(this.configuration || [])];
    }
    /*
     * The Remove Entry method deletes the specified entry from the list of configurations. The method does not delete the actual configuration, but just removes it from the array of configurations.
     * The entry will be deleted when a user clicks the Submit button but if the entry is new we can are removing it from the configuration
     *
     * @param {Object} entry The object to be deleted
     * @param {Object} configurationForm The form object which is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}
     * that provides methods to monitor and control the state of the form.
     */
    removeEntry(entry, configurationForm) {
        if (entry.isNew) {
            this.configuration = this.configuration.filter((confEntry) => !confEntry.isNew || confEntry.key !== entry.key);
        }
        else {
            configurationForm.form.markAsDirty();
            entry.toDelete = true;
        }
    }
    /*
     * Method that returns a list of configurations by filtering out only those configurations whose 'toDelete' parameter is set to false.
     *
     * @returns {Object} A list of filtered configurations.
     */
    filterConfiguration() {
        return (this.configuration || []).filter((instance) => !instance.toDelete);
    }
    filterErrorConfiguration() {
        return (this.configuration || []).filter((instance) => instance.hasErrors);
    }
    validateUserInput(entry) {
        if (!entry.value) {
            return;
        }
        entry.requiresUserCheck = !!entry.value.match(this.ABSOLUTE_URI_REGEX);
    }
    /*
     * The Submit method saves the list of available configurations by making a REST call to a web service.
     * The method is called when a user clicks the Submit button in the configuration editor.
     *
     * @param {Object} configurationForm The form object that is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}.
     * It provides methods to monitor and control the state of the form.
     */
    submit(configurationForm) {
        if (!configurationForm.dirty || !this.isValid(configurationForm)) {
            return Promise.reject([]);
        }
        configurationForm.form.markAsPristine();
        return Promise.all(this.configuration.map((entry, i) => this.reconstructConfigurationEntry(entry, i)));
    }
    /*
     * The init method initializes the configuration editor and loads all the configurations so they can be edited.
     *
     * @param {Function} loadCallback The callback to be executed after loading the configurations.
     */
    init(_loadCallback) {
        this.loadCallback = _loadCallback || lo__namespace.noop;
        return this.loadAndPresent();
    }
    reconstructConfigurationEntry(entry, i) {
        const payload = smarteditcommons.objectUtils.copy(entry);
        let method;
        payload.secured = false; // needed for yaas configuration service
        delete payload.toDelete;
        delete payload.errors;
        delete payload.uuid;
        try {
            if (entry.toDelete) {
                method = 'remove';
            }
            else if (payload.isNew) {
                method = 'save';
                payload.value = this.validate(payload);
            }
            else {
                method = 'update';
                payload.value = this.validate(payload);
            }
        }
        catch (error) {
            entry.hasErrors = true;
            if (error instanceof smarteditcommons.Errors.ParseError) {
                this.addValueError(entry, 'se.configurationform.json.parse.error');
            }
            return Promise.reject({});
        }
        delete payload.isNew;
        entry.hasErrors = false;
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return this.editorCRUDService[method](payload).then(() => {
            switch (method) {
                case 'save':
                    delete entry.isNew;
                    break;
                case 'remove':
                    this.configuration.splice(i, 1);
                    break;
            }
            return Promise.resolve({});
        }, () => {
            this.addValueError(entry, 'configurationform.save.error');
            return Promise.reject({});
        });
    }
    reset(configurationForm) {
        this.configuration = smarteditcommons.objectUtils.copy(this.pristine);
        if (configurationForm) {
            configurationForm.form.markAsPristine();
        }
        if (this.loadCallback) {
            this.loadCallback();
        }
    }
    addError(entry, type, message) {
        entry.hasErrors = true;
        entry.errors = entry.errors || {};
        entry.errors[type] = entry.errors[type] || [];
        entry.errors[type].push({
            message
        });
    }
    addKeyError(entry, message) {
        this.addError(entry, 'keys', message);
    }
    addValueError(entry, message) {
        this.addError(entry, 'values', message);
    }
    prettify(array) {
        const configuration = smarteditcommons.objectUtils.copy(array);
        configuration.forEach((entry) => {
            try {
                entry.value = JSON.stringify(JSON.parse(entry.value), null, 2);
            }
            catch (parseError) {
                this.addValueError(entry, 'se.configurationform.json.parse.error');
            }
        });
        return configuration;
    }
    /**
     * for editing purposes
     */
    loadAndPresent() {
        return new Promise((resolve, reject) => this.loadConfigManagerService.loadAsArray().then((response) => {
            this.pristine = this.prettify(response.map((item) => (Object.assign(Object.assign({}, item), { uuid: lo__namespace.uniqueId() }))));
            this.reset();
            resolve();
        }, () => {
            this.logService.log('load failed');
            reject();
        }));
    }
    isValid(configurationForm) {
        let hasError = false;
        this.configuration.forEach((entry) => {
            delete entry.errors;
        });
        this.configuration.forEach((entry) => {
            if (smarteditcommons.stringUtils.isBlank(entry.key)) {
                this.addKeyError(entry, 'se.configurationform.required.entry.error');
                entry.hasErrors = true;
                hasError = true;
            }
            if (smarteditcommons.stringUtils.isBlank(entry.value)) {
                this.addValueError(entry, 'se.configurationform.required.entry.error');
                entry.hasErrors = true;
                hasError = true;
            }
        });
        return (!hasError &&
            configurationForm.valid &&
            !this.configuration.reduce((confHolder, nextConfiguration) => {
                if (confHolder.keys.indexOf(nextConfiguration.key) > -1) {
                    this.addKeyError(nextConfiguration, 'se.configurationform.duplicate.entry.error');
                    confHolder.errors = true;
                }
                else {
                    confHolder.keys.push(nextConfiguration.key);
                }
                return confHolder;
            }, {
                keys: [],
                errors: false
            }).errors);
    }
    validate(entry) {
        if (entry.requiresUserCheck && !entry.isCheckedByUser) {
            throw new Error(this.ABSOLUTE_URI_NOT_APPROVED);
        }
        else {
            try {
                return JSON.stringify(JSON.parse(entry.value));
            }
            catch (parseError) {
                throw new smarteditcommons.Errors.ParseError(entry.value);
            }
        }
    }
};
/* @ngInject */ ConfigurationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        exports.LoadConfigManagerService,
        smarteditcommons.RestServiceFactory])
], /* @ngInject */ ConfigurationService);

let /* @ngInject */ IframeClickDetectionService = class /* @ngInject */ IframeClickDetectionService extends smarteditcommons.IIframeClickDetectionService {
    constructor() {
        super();
        this.callbacks = {};
    }
    registerCallback(id, callback) {
        this.callbacks[id] = callback;
        return this.removeCallback.bind(this, id);
    }
    removeCallback(id) {
        if (this.callbacks[id]) {
            delete this.callbacks[id];
            return true;
        }
        return false;
    }
    /**
     * Triggers all callbacks currently registered to the service. This function is registered as a listener through
     * the GatewayProxy
     */
    onIframeClick() {
        for (const ref in this.callbacks) {
            if (this.callbacks.hasOwnProperty(ref)) {
                this.callbacks[ref]();
            }
        }
    }
};
/* @ngInject */ IframeClickDetectionService = __decorate([
    smarteditcommons.GatewayProxied('onIframeClick'),
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ IframeClickDetectionService);

/** @internal */
let RenderService = class RenderService extends smarteditcommons.IRenderService {
    constructor(yjQuery, crossFrameEventService, systemEventService, notificationService, pageInfoService, perspectiveService, windowUtils, modalService, logService) {
        super(yjQuery, systemEventService, notificationService, pageInfoService, perspectiveService, crossFrameEventService, windowUtils, modalService, logService);
        this.yjQuery = yjQuery;
        this.crossFrameEventService = crossFrameEventService;
        this.systemEventService = systemEventService;
        this.logService = logService;
    }
    blockRendering(block) {
        this.renderingBlocked = block;
    }
    isRenderingBlocked() {
        return Promise.resolve(this.renderingBlocked || false);
    }
};
RenderService = __decorate([
    smarteditcommons.GatewayProxied('blockRendering', 'isRenderingBlocked', 'renderSlots', 'renderComponent', 'renderRemoval', 'toggleOverlay', 'refreshOverlayDimensions', 'renderPage'),
    __param(0, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function, smarteditcommons.CrossFrameEventService,
        smarteditcommons.SystemEventService,
        smarteditcommons.INotificationService,
        smarteditcommons.IPageInfoService,
        smarteditcommons.IPerspectiveService,
        smarteditcommons.WindowUtils,
        smarteditcommons.ModalService,
        smarteditcommons.LogService])
], RenderService);

window.__smartedit__.addDecoratorPayload("Injectable", "TranslationsFetchService", { providedIn: 'root' });
/* @ngInject */ exports.TranslationsFetchService = class /* @ngInject */ TranslationsFetchService extends smarteditcommons.ITranslationsFetchService {
    constructor(httpClient, promiseUtils) {
        super();
        this.httpClient = httpClient;
        this.promiseUtils = promiseUtils;
        this.ready = false;
    }
    get(lang) {
        return this.httpClient
            .get(`${smarteditcommons.RestServiceFactory.getGlobalBasePath()}${smarteditcommons.I18N_RESOURCE_URI}/${lang}`, {
            responseType: 'json'
        })
            .pipe(operators.map((result) => result.value
            ? result.value
            : result))
            .toPromise()
            .then((result) => {
            this.ready = true;
            return result;
        });
    }
    isReady() {
        return Promise.resolve(this.ready);
    }
    waitToBeReady() {
        return this.promiseUtils.resolveToCallbackWhenCondition(() => this.ready, () => {
            //
        });
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.TranslationsFetchService.prototype, "get", null);
/* @ngInject */ exports.TranslationsFetchService = __decorate([
    smarteditcommons.GatewayProxied()
    /*
     * this service, provider in SeTranslationModule, needs be accessible to root
     * so that when downgrading it to legacy usage it will be found in DI
     */
    ,
    core.Injectable({ providedIn: 'root' }),
    __metadata("design:paramtypes", [http.HttpClient, smarteditcommons.PromiseUtils])
], /* @ngInject */ exports.TranslationsFetchService);

var PERMISSION_TYPES;
(function (PERMISSION_TYPES) {
    PERMISSION_TYPES["READ"] = "read";
    PERMISSION_TYPES["WRITE"] = "write";
})(PERMISSION_TYPES || (PERMISSION_TYPES = {}));
let CatalogVersionPermissionService = class CatalogVersionPermissionService extends smarteditcommons.ICatalogVersionPermissionService {
    constructor(catalogVersionPermissionRestService, catalogService) {
        super();
        this.catalogVersionPermissionRestService = catalogVersionPermissionRestService;
        this.catalogService = catalogService;
    }
    hasPermission(accessType, catalogId, catalogVersion, siteId) {
        return __awaiter(this, void 0, void 0, function* () {
            const [shouldOverride, response] = yield Promise.all([
                this.shouldIgnoreCatalogPermissions(accessType, catalogId, catalogVersion, siteId),
                this.catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId, catalogVersion)
            ]);
            if (this.isCatalogVersionPermissionResponse(response)) {
                const targetPermission = response.permissions.find((permission) => permission.key === accessType);
                const value = targetPermission ? targetPermission.value : 'false';
                return value === 'true' || shouldOverride;
            }
            return false;
        });
    }
    hasSyncPermissionFromCurrentToActiveCatalogVersion() {
        return __awaiter(this, void 0, void 0, function* () {
            const data = yield this.catalogService.retrieveUriContext();
            return yield this.hasSyncPermissionToActiveCatalogVersion(data[smarteditcommons.CONTEXT_CATALOG], data[smarteditcommons.CONTEXT_CATALOG_VERSION]);
        });
    }
    hasSyncPermissionToActiveCatalogVersion(catalogId, catalogVersion) {
        return __awaiter(this, void 0, void 0, function* () {
            const targetCatalogVersion = yield this.catalogService.getActiveContentCatalogVersionByCatalogId(catalogId);
            return yield this.hasSyncPermission(catalogId, catalogVersion, targetCatalogVersion);
        });
    }
    hasSyncPermission(catalogId, sourceCatalogVersion, targetCatalogVersion) {
        return __awaiter(this, void 0, void 0, function* () {
            const response = yield this.catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId, sourceCatalogVersion);
            if (this.isCatalogVersionPermissionResponse(response) &&
                response.syncPermissions &&
                response.syncPermissions.length > 0) {
                const permission = response.syncPermissions.some((syncPermission) => syncPermission
                    ? syncPermission.canSynchronize === true &&
                        syncPermission.targetCatalogVersion === targetCatalogVersion
                    : false);
                return permission;
            }
            return false;
        });
    }
    hasWritePermissionOnCurrent() {
        return this.hasCurrentCatalogPermission(PERMISSION_TYPES.WRITE);
    }
    hasReadPermissionOnCurrent() {
        return this.hasCurrentCatalogPermission(PERMISSION_TYPES.READ);
    }
    hasWritePermission(catalogId, catalogVersion) {
        return this.hasPermission(PERMISSION_TYPES.WRITE, catalogId, catalogVersion);
    }
    hasReadPermission(catalogId, catalogVersion) {
        return this.hasPermission(PERMISSION_TYPES.READ, catalogId, catalogVersion);
    }
    /**
     * if in the context of an experience AND the catalogVersion is the active one, then permissions should be ignored in read mode
     */
    shouldIgnoreCatalogPermissions(accessType, catalogId, catalogVersion, siteId) {
        return __awaiter(this, void 0, void 0, function* () {
            const promise = siteId && accessType === PERMISSION_TYPES.READ
                ? this.catalogService.getActiveContentCatalogVersionByCatalogId(catalogId)
                : Promise.resolve();
            const versionCheckedAgainst = yield promise;
            return versionCheckedAgainst === catalogVersion;
        });
    }
    /**
     * Verifies whether current user has write or read permission for current catalog version.
     * @param {String} accessType
     */
    hasCurrentCatalogPermission(accessType) {
        return __awaiter(this, void 0, void 0, function* () {
            const data = yield this.catalogService.retrieveUriContext();
            return yield this.hasPermission(accessType, data[smarteditcommons.CONTEXT_CATALOG], data[smarteditcommons.CONTEXT_CATALOG_VERSION], data[smarteditcommons.CONTEXT_SITE_ID]);
        });
    }
    isCatalogVersionPermissionResponse(response) {
        return !lo__namespace.isEmpty(response);
    }
};
CatalogVersionPermissionService = __decorate([
    smarteditcommons.GatewayProxied('hasWritePermission', 'hasReadPermission', 'hasWritePermissionOnCurrent', 'hasReadPermissionOnCurrent'),
    __metadata("design:paramtypes", [CatalogVersionPermissionRestService,
        smarteditcommons.ICatalogService])
], CatalogVersionPermissionService);

/**
 * Implementation of smarteditcommons' DropdownPopulatorInterface for language dropdown in
 * experience selector to populate the list of languages by making a REST call to retrieve the list of langauges for a given site.
 *
 */
let /* @ngInject */ PreviewDatalanguageDropdownPopulator = class /* @ngInject */ PreviewDatalanguageDropdownPopulator extends smarteditcommons.DropdownPopulatorInterface {
    constructor(languageService) {
        super(lo__namespace, languageService);
    }
    /**
     * Returns a promise resolving to a list of languages for a given Site ID (based on the selected catalog). The site Id is generated from the
     * selected catalog in the 'catalog' dropdown.
     */
    fetchAll(payload) {
        if (payload.model[payload.field.dependsOn]) {
            const siteUid = payload.model[payload.field.dependsOn].split('|')[0];
            return this.getLanguageDropdownChoices(siteUid, payload.search);
        }
        return Promise.resolve([]);
    }
    /** @internal */
    getLanguageDropdownChoices(siteUid, search) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const languages = yield this.languageService.getLanguagesForSite(siteUid);
                let languagesDropdownChoices = languages.map(({ isocode, nativeName }) => {
                    const dropdownChoices = {};
                    dropdownChoices.id = isocode;
                    dropdownChoices.label = nativeName;
                    return dropdownChoices;
                });
                if (search) {
                    languagesDropdownChoices = languagesDropdownChoices.filter((language) => language.label.toUpperCase().indexOf(search.toUpperCase()) > -1);
                }
                return languagesDropdownChoices;
            }
            catch (e) {
                throw new Error(e);
            }
        });
    }
};
/* @ngInject */ PreviewDatalanguageDropdownPopulator = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LanguageService])
], /* @ngInject */ PreviewDatalanguageDropdownPopulator);

/**
 * Implementation of DropdownPopulatorInterface for catalog dropdown in
 * experience selector to populate the list of catalogs by making a REST call to retrieve the sites and then the catalogs based on the site.
 */
let /* @ngInject */ PreviewDatapreviewCatalogDropdownPopulator = class /* @ngInject */ PreviewDatapreviewCatalogDropdownPopulator extends smarteditcommons.DropdownPopulatorInterface {
    constructor(catalogService, sharedDataService, l10nPipe, languageService) {
        super(lo__namespace, languageService);
        this.catalogService = catalogService;
        this.sharedDataService = sharedDataService;
        this.l10nPipe = l10nPipe;
    }
    /**
     *  Returns a promise resolving to a list of site - catalogs to be displayed in the experience selector.
     *
     */
    fetchAll(payload) {
        return this.initCatalogVersionDropdownChoices(payload.search);
    }
    /** @internal */
    initCatalogVersionDropdownChoices(search) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const experience = (yield this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY));
                const siteDescriptor = experience.siteDescriptor;
                const dropdownChoices = yield this.getDropdownChoices(siteDescriptor, search);
                const ascDropdownChoices = lo__namespace
                    .flatten(dropdownChoices)
                    .sort((e1, e2) => e1.label.localeCompare(e2.label));
                return ascDropdownChoices;
            }
            catch (e) {
                throw new Error(e);
            }
        });
    }
    /** @internal */
    getDropdownChoices(siteDescriptor, search) {
        return __awaiter(this, void 0, void 0, function* () {
            const catalogs = yield this.catalogService.getContentCatalogsForSite(siteDescriptor.uid);
            const optionPromises = lo__namespace
                .flatten(catalogs)
                .map((catalog) => this.getTranslatedCatalogVersionsOptions(siteDescriptor, catalog));
            const options = yield Promise.all(lo__namespace.flatten(optionPromises));
            return options.filter((option) => search ? option.label.toUpperCase().includes(search.toUpperCase()) : true);
        });
    }
    getTranslatedCatalogVersionsOptions(siteDescriptor, catalog) {
        return catalog.versions.map((catalogVersion) => __awaiter(this, void 0, void 0, function* () {
            const catalogName = yield this.l10nPipe
                .transform(catalog.name)
                .pipe(operators.take(1))
                .toPromise();
            return {
                id: `${siteDescriptor.uid}|${catalog.catalogId}|${catalogVersion.version}`,
                label: `${catalogName} - ${catalogVersion.version}`
            };
        }));
    }
};
/* @ngInject */ PreviewDatapreviewCatalogDropdownPopulator = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.L10nPipe,
        smarteditcommons.LanguageService])
], /* @ngInject */ PreviewDatapreviewCatalogDropdownPopulator);

var /* @ngInject */ CatalogAwareRouteResolverHelper_1;
/**
 * Validates if user has permission on current catalog.
 *
 * Implemented by Route Guards.
 */
/* @ngInject */ exports.CatalogAwareRouteResolverHelper = /* @ngInject */ CatalogAwareRouteResolverHelper_1 = class /* @ngInject */ CatalogAwareRouteResolverHelper {
    constructor(logService, route, systemEventService, experienceService, sharedDataService, catalogVersionPermissionService) {
        this.logService = logService;
        this.route = route;
        this.systemEventService = systemEventService;
        this.experienceService = experienceService;
        this.sharedDataService = sharedDataService;
        this.catalogVersionPermissionService = catalogVersionPermissionService;
    }
    /**
     * @internal
     * @ignore
     *
     * Convert to instance method after 2105 deprecation period has been exceeded.
     */
    static executeAndCheckCatalogPermissions(catalogVersionPermissionService, logService, experienceService, systemEventService, operation) {
        return operation().then(() => catalogVersionPermissionService.hasReadPermissionOnCurrent().then((hasReadPermission) => {
            if (!hasReadPermission) {
                logService.info('no permission to access the storefront view with this experience');
                return Promise.reject();
            }
            return experienceService
                .hasCatalogVersionChanged()
                .then((hasCatalogVersionChanged) => {
                if (hasCatalogVersionChanged) {
                    systemEventService.publishAsync(smarteditcommons.EVENTS.EXPERIENCE_UPDATE);
                }
                return true;
            });
        }, () => {
            logService.info('failed to evaluate permissions to access the storefront view with this experience');
            return Promise.reject();
        }), (error) => {
            logService.error('could not retrieve experience from storage or route params', error);
            throw new Error(error);
        });
    }
    /**
     * @internal
     * @ignore
     */
    static checkExperienceIsSet(experienceService, sharedDataService) {
        return new Promise((resolve, reject) => {
            experienceService.getCurrentExperience().then((experience) => {
                if (!experience) {
                    return reject();
                }
                // next line to preserve in-memory features throughout the app
                sharedDataService.set(smarteditcommons.EXPERIENCE_STORAGE_KEY, experience);
                return resolve(experience);
            });
        });
    }
    /**
     * @internal
     * @ignore
     */
    static buildExperienceFromRoute(experienceService, params) {
        return experienceService.buildAndSetExperience(params).then((experience) => {
            if (!experience) {
                return Promise.reject();
            }
            return experience;
        });
    }
    /**
     * Checks presence of a stored experience.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    storefrontResolve() {
        return this.executeAndCheckCatalogPermissions(() => this.checkExperienceIsSet());
    }
    /**
     * Initializes new experience based on route params.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    experienceFromPathResolve(params) {
        return this.executeAndCheckCatalogPermissions(() => this.buildExperienceFromRoute(params));
    }
    /**
     * Runs operation that sets the experience and then resolves to true if the user has read permissions on current catalog.
     *
     * @internal
     * @ignore
     */
    executeAndCheckCatalogPermissions(operation) {
        return /* @ngInject */ CatalogAwareRouteResolverHelper_1.executeAndCheckCatalogPermissions(this.catalogVersionPermissionService, this.logService, this.experienceService, this.systemEventService, operation);
    }
    /**
     * Resolves with the existing experience if it is set, otherwise rejects.
     *
     * @internal
     * @ignore
     */
    checkExperienceIsSet() {
        return /* @ngInject */ CatalogAwareRouteResolverHelper_1.checkExperienceIsSet(this.experienceService, this.sharedDataService);
    }
    /**
     * Creates and sets an experience based on active route params.
     *
     * @internal
     * @ignore
     */
    buildExperienceFromRoute(params) {
        return /* @ngInject */ CatalogAwareRouteResolverHelper_1.buildExperienceFromRoute(this.experienceService, params || this.route.snapshot.params);
    }
};
/* @ngInject */ exports.CatalogAwareRouteResolverHelper = /* @ngInject */ CatalogAwareRouteResolverHelper_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.LogService,
        router.ActivatedRoute,
        smarteditcommons.SystemEventService,
        smarteditcommons.IExperienceService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.ICatalogVersionPermissionService])
], /* @ngInject */ exports.CatalogAwareRouteResolverHelper);

/**
 * Guard that prevents unauthorized user from navigating to Storefront Page.
 *
 * @internal
 * @ignore
 */
let /* @ngInject */ StorefrontPageGuard = class /* @ngInject */ StorefrontPageGuard {
    constructor(catalogAwareResolverHelper, routing) {
        this.catalogAwareResolverHelper = catalogAwareResolverHelper;
        this.routing = routing;
    }
    /**
     * It will redirect current user to the Landing Page if the user doesn't have a read permission to the current catalog version.
     */
    canActivate() {
        return this.catalogAwareResolverHelper.storefrontResolve().catch(() => {
            this.routing.go('/');
            return false;
        });
    }
};
/* @ngInject */ StorefrontPageGuard = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.CatalogAwareRouteResolverHelper,
        smarteditcommons.SmarteditRoutingService])
], /* @ngInject */ StorefrontPageGuard);

/**
 * The ProductService provides is used to access products from the product catalog
 */
/* @ngInject */ exports.ProductService = class /* @ngInject */ ProductService {
    constructor(restServiceFactory, languageService) {
        this.restServiceFactory = restServiceFactory;
        this.languageService = languageService;
        this.productService = this.restServiceFactory.get(smarteditcommons.PRODUCT_RESOURCE_API);
        this.productListService = this.restServiceFactory.get(smarteditcommons.PRODUCT_LIST_RESOURCE_API);
    }
    /**
     * Returns a list of Products from the catalog that match the given mask
     */
    findProducts(productSearch, pageable) {
        return __awaiter(this, void 0, void 0, function* () {
            this._validateProductCatalogInfo(productSearch);
            const langIsoCode = yield this.languageService.getResolveLocale();
            const list = yield this.productListService.get({
                catalogId: productSearch.catalogId,
                catalogVersion: productSearch.catalogVersion,
                text: pageable.mask,
                pageSize: pageable.pageSize,
                currentPage: pageable.currentPage,
                langIsoCode
            });
            return list;
        });
    }
    /**
     * Returns a Product that matches the given siteUID and productUID
     */
    getProductById(siteUID, productUID) {
        return this.productService.get({
            siteUID,
            productUID
        });
    }
    _validateProductCatalogInfo(productSearch) {
        if (!productSearch.catalogId) {
            throw new Error('[productService] - catalog ID missing.');
        }
        if (!productSearch.catalogVersion) {
            throw new Error('[productService] - catalog version missing.');
        }
    }
};
__decorate([
    smarteditcommons.Cached({ actions: [smarteditcommons.rarelyChangingContent], tags: [smarteditcommons.userEvictionTag] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.ProductService.prototype, "findProducts", null);
/* @ngInject */ exports.ProductService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.RestServiceFactory,
        smarteditcommons.LanguageService])
], /* @ngInject */ exports.ProductService);

let ComponentHandlerService = class ComponentHandlerService extends smarteditcommons.IComponentHandlerService {
    constructor(yjQuery) {
        super(yjQuery);
    }
};
ComponentHandlerService = __decorate([
    smarteditcommons.GatewayProxied('isExternalComponent', 'reloadInner'),
    __param(0, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function])
], ComponentHandlerService);

window.__smartedit__.addDecoratorPayload("Component", "ConfigurationModalComponent", {
    selector: 'se-configuration-modal',
    template: `<div id="editConfigurationsBody" class="se-config"><form #form="ngForm" novalidate><div class="se-config__sub-header"><span class="se-config__sub-title">{{'se.configurationform.label.keyvalue' | translate}}</span> <button class="fd-button fd-button--compact" type="button" (click)="editor.addEntry()">{{ "se.general.configuration.add.button" | translate }}</button></div><div class="se-config__error" *ngIf="editor.filterErrorConfiguration().length > 0"><span class="error-input help-block">{{'se.configurationform.contain.error'|translate}}</span></div><div class="se-config__entry" *ngFor="let entry of editor.filterConfiguration(); let $index = index"><div class="se-config__entry-data"><div class="se-config__entry-key"><input type="text" [ngClass]="{
                            'is-error': entry.errors &&  entry.errors.keys && entry.errors.keys.length > 0,
                            'se-config__entry-key--disabled': !entry.isNew }" name="{{entry.key}}_{{entry.uuid}}_key" placeholder="{{'se.configurationform.header.key.name' | translate}}" [(ngModel)]="entry.key" [required]="true" [disabled]="!entry.isNew" class="se-config__entry-key-input fd-input fd-form__control" [title]="entry.key"/><ng-container *ngIf="entry.errors && entry.errors.keys"><span id="{{entry.key}}_error_{{$index}}" *ngFor="let error of entry.errors.keys; let $index = index" class="error-input help-block">{{error.message|translate}}</span></ng-container></div><div class="se-config__entry-value"><textarea [ngClass]="{'is-error': entry.errors && entry.errors.values && entry.errors.values.length>0}" name="{{entry.key}}_{{entry.uuid}}_value" placeholder="{{'se.configurationform.header.value.name' | translate}}" [(ngModel)]="entry.value" [required]="true" class="se-config__entry-text-area fd-input fd-form__control" (change)="editor.validateUserInput(entry)"></textarea><div *ngIf="entry.requiresUserCheck"><input id="{{entry.key}}_absoluteUrl_check_{{$index}}" type="checkbox" name="{{entry.key}}_{{entry.uuid}}_isCheckedByUser" [(ngModel)]="entry.isCheckedByUser" class="fd-input se-config__entry-value-checkbox"/> <span id="{{entry.key}}_absoluteUrl_msg_{{$index}}" [ngClass]="{
                                'warning-check-msg': true,
                                'not-checked': entry.hasErrors && !entry.isCheckedByUser
                            }">{{'se.configurationform.absoluteurl.check' | translate}}</span></div><ng-container *ngIf="entry.errors && entry.errors.values && entry.errors.values"><span id="{{entry.key}}_error_{{$index}}" *ngFor="let error of entry.errors.values; let $index = index" class="error-input help-block">{{error.message|translate}}</span></ng-container></div></div><button type="button" id="{{entry.key}}_removeButton_{{$index}}" class="se-config__entry-remove-btn fd-button fd-button--transparent sap-icon--delete" (click)="editor.removeEntry(entry, form);"></button></div></form></div>`
});
let ConfigurationModalComponent = class ConfigurationModalComponent {
    constructor(editor, modalManager, confirmationModalService) {
        this.editor = editor;
        this.modalManager = modalManager;
        this.confirmationModalService = confirmationModalService;
    }
    ngOnInit() {
        this.editor.init();
        this.form.statusChanges.subscribe(() => {
            if (this.form.valid && this.form.dirty) {
                this.modalManager.enableButton('save');
            }
            if (this.form.invalid || !this.form.dirty) {
                this.modalManager.disableButton('save');
            }
        });
        this.modalManager.addButtons([
            {
                id: 'save',
                style: smarteditcommons.ModalButtonStyle.Primary,
                label: 'se.cms.component.confirmation.modal.save',
                callback: () => rxjs.from(this.onSave()),
                disabled: true
            },
            {
                id: 'cancel',
                label: 'se.cms.component.confirmation.modal.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss,
                callback: () => rxjs.from(this.onCancel())
            }
        ]);
    }
    trackByFn(_, item) {
        return item.uuid;
    }
    onCancel() {
        const { dirty } = this.form;
        const confirmationData = {
            description: 'se.editor.cancel.confirm'
        };
        if (!dirty) {
            return Promise.resolve();
        }
        return this.confirmationModalService
            .confirm(confirmationData)
            .then(() => this.modalManager.close(null));
    }
    onSave() {
        return this.editor.submit(this.form).then(() => {
            this.modalManager.close(null);
        });
    }
};
__decorate([
    core.ViewChild('form', { static: true }),
    __metadata("design:type", forms.NgForm)
], ConfigurationModalComponent.prototype, "form", void 0);
ConfigurationModalComponent = __decorate([
    core.Component({
        selector: 'se-configuration-modal',
        template: `<div id="editConfigurationsBody" class="se-config"><form #form="ngForm" novalidate><div class="se-config__sub-header"><span class="se-config__sub-title">{{'se.configurationform.label.keyvalue' | translate}}</span> <button class="fd-button fd-button--compact" type="button" (click)="editor.addEntry()">{{ "se.general.configuration.add.button" | translate }}</button></div><div class="se-config__error" *ngIf="editor.filterErrorConfiguration().length > 0"><span class="error-input help-block">{{'se.configurationform.contain.error'|translate}}</span></div><div class="se-config__entry" *ngFor="let entry of editor.filterConfiguration(); let $index = index"><div class="se-config__entry-data"><div class="se-config__entry-key"><input type="text" [ngClass]="{
                            'is-error': entry.errors &&  entry.errors.keys && entry.errors.keys.length > 0,
                            'se-config__entry-key--disabled': !entry.isNew }" name="{{entry.key}}_{{entry.uuid}}_key" placeholder="{{'se.configurationform.header.key.name' | translate}}" [(ngModel)]="entry.key" [required]="true" [disabled]="!entry.isNew" class="se-config__entry-key-input fd-input fd-form__control" [title]="entry.key"/><ng-container *ngIf="entry.errors && entry.errors.keys"><span id="{{entry.key}}_error_{{$index}}" *ngFor="let error of entry.errors.keys; let $index = index" class="error-input help-block">{{error.message|translate}}</span></ng-container></div><div class="se-config__entry-value"><textarea [ngClass]="{'is-error': entry.errors && entry.errors.values && entry.errors.values.length>0}" name="{{entry.key}}_{{entry.uuid}}_value" placeholder="{{'se.configurationform.header.value.name' | translate}}" [(ngModel)]="entry.value" [required]="true" class="se-config__entry-text-area fd-input fd-form__control" (change)="editor.validateUserInput(entry)"></textarea><div *ngIf="entry.requiresUserCheck"><input id="{{entry.key}}_absoluteUrl_check_{{$index}}" type="checkbox" name="{{entry.key}}_{{entry.uuid}}_isCheckedByUser" [(ngModel)]="entry.isCheckedByUser" class="fd-input se-config__entry-value-checkbox"/> <span id="{{entry.key}}_absoluteUrl_msg_{{$index}}" [ngClass]="{
                                'warning-check-msg': true,
                                'not-checked': entry.hasErrors && !entry.isCheckedByUser
                            }">{{'se.configurationform.absoluteurl.check' | translate}}</span></div><ng-container *ngIf="entry.errors && entry.errors.values && entry.errors.values"><span id="{{entry.key}}_error_{{$index}}" *ngFor="let error of entry.errors.values; let $index = index" class="error-input help-block">{{error.message|translate}}</span></ng-container></div></div><button type="button" id="{{entry.key}}_removeButton_{{$index}}" class="se-config__entry-remove-btn fd-button fd-button--transparent sap-icon--delete" (click)="editor.removeEntry(entry, form);"></button></div></form></div>`
    }),
    __metadata("design:paramtypes", [ConfigurationService,
        smarteditcommons.ModalManagerService,
        smarteditcommons.IConfirmationModalService])
], ConfigurationModalComponent);

let /* @ngInject */ ConfigurationModalService = class /* @ngInject */ ConfigurationModalService {
    constructor(modalService) {
        this.modalService = modalService;
    }
    /*
     *The edit configuration method opens the modal for the configurations.
     */
    editConfiguration() {
        this.modalService.open({
            templateConfig: {
                title: 'se.modal.administration.configuration.edit.title'
            },
            component: ConfigurationModalComponent,
            config: {
                focusTrapped: false,
                backdropClickCloseable: false
            }
        });
    }
};
/* @ngInject */ ConfigurationModalService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IModalService])
], /* @ngInject */ ConfigurationModalService);

let PageTreeNodeService = class PageTreeNodeService extends smarteditcommons.IPageTreeNodeService {
    constructor() {
        super();
    }
};
PageTreeNodeService = __decorate([
    smarteditcommons.GatewayProxied('getSlotNodes', 'scrollToElement', 'existedSmartEditElement'),
    __metadata("design:paramtypes", [])
], PageTreeNodeService);

let PageTreeService = class PageTreeService extends smarteditcommons.IPageTreeService {
    constructor() {
        super();
    }
    registerTreeComponent(item) {
        this.item = item;
        return Promise.resolve();
    }
    getTreeComponent() {
        return Promise.resolve(this.item);
    }
};
PageTreeService = __decorate([
    smarteditcommons.GatewayProxied('registerTreeComponent', 'getTreeComponent'),
    __metadata("design:paramtypes", [])
], PageTreeService);

let SmarteditServicesModule = class SmarteditServicesModule {
};
SmarteditServicesModule = __decorate([
    core.NgModule({
        imports: [smarteditcommons.DragAndDropServiceModule, smarteditcommons.SmarteditCommonsModule, exports.AlertServiceModule],
        providers: [
            HeartBeatService,
            exports.BootstrapService,
            exports.ConfigurationExtractorService,
            exports.DelegateRestService,
            smarteditcommons.RestServiceFactory,
            ConfigurationService,
            ConfigurationModalService,
            PreviewDatalanguageDropdownPopulator,
            PreviewDatapreviewCatalogDropdownPopulator,
            CatalogVersionPermissionRestService,
            CatalogVersionPermissionService,
            exports.CatalogAwareRouteResolverHelper,
            StorefrontPageGuard,
            smarteditcommons.SmarteditRoutingService,
            {
                provide: smarteditcommons.IComponentHandlerService,
                useClass: ComponentHandlerService
            },
            {
                provide: smarteditcommons.ICatalogVersionPermissionService,
                useClass: CatalogVersionPermissionService
            },
            { provide: smarteditcommons.IPermissionService, useClass: PermissionService },
            { provide: smarteditcommons.IPageInfoService, useClass: PageInfoService },
            {
                provide: smarteditcommons.IRestServiceFactory,
                useClass: smarteditcommons.RestServiceFactory
            },
            {
                provide: smarteditcommons.IRenderService,
                useClass: RenderService
            },
            {
                provide: smarteditcommons.IAnnouncementService,
                useClass: AnnouncementService
            },
            {
                provide: smarteditcommons.IPerspectiveService,
                useClass: PerspectiveService
            },
            {
                provide: smarteditcommons.IFeatureService,
                useClass: FeatureService
            },
            {
                provide: smarteditcommons.INotificationMouseLeaveDetectionService,
                useClass: NotificationMouseLeaveDetectionService
            },
            {
                provide: smarteditcommons.IWaitDialogService,
                useClass: WaitDialogService
            },
            {
                provide: smarteditcommons.IPreviewService,
                useClass: PreviewService
            },
            {
                provide: smarteditcommons.IDragAndDropCrossOrigin,
                useClass: DragAndDropCrossOrigin
            },
            PermissionsRegistrationService,
            {
                provide: smarteditcommons.INotificationService,
                useClass: NotificationService
            },
            {
                provide: smarteditcommons.IPageTreeNodeService,
                useClass: PageTreeNodeService
            },
            {
                provide: smarteditcommons.IPageTreeService,
                useClass: PageTreeService
            },
            {
                provide: smarteditcommons.CustomDropdownPopulatorsToken,
                useClass: PreviewDatapreviewCatalogDropdownPopulator,
                multi: true
            },
            {
                provide: smarteditcommons.CustomDropdownPopulatorsToken,
                useClass: PreviewDatalanguageDropdownPopulator,
                multi: true
            },
            exports.ProductService,
            smarteditcommons.moduleUtils.initialize((previewService) => {
                //
            }, [smarteditcommons.IPreviewService])
        ]
    })
], SmarteditServicesModule);

/**
 * Service used to open a confirmation modal in which an end-user can confirm or cancel an action.
 * A confirmation modal consists of a title, content, and an OK and cancel button. This modal may be used in any context in which a
 * confirmation is required.
 */
let ConfirmationModalService = class ConfirmationModalService extends smarteditcommons.IConfirmationModalService {
    constructor(modalService) {
        super();
        this.modalService = modalService;
    }
    /**
     * Uses the [ModalService]{@link IModalService} to open a confirmation modal.
     *
     * The confirmation modal is initialized by a default i18N key as a title or by an override title passed in configuration.
     *
     * @returns A promise that is resolved when the OK button is actioned or is rejected when the Cancel
     * button is actioned.
     */
    confirm(configuration) {
        const ref = this.modalService.open({
            component: smarteditcommons.ConfirmDialogComponent,
            data: configuration,
            config: {
                focusTrapped: false,
                dialogPanelClass: 'se-confirmation-dialog',
                // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
                container: document.querySelector('[uib-modal-window]') || 'body'
            },
            templateConfig: {
                title: configuration.title || 'se.confirmation.modal.title',
                buttons: this.getButtons(configuration),
                isDismissButtonVisible: true
            }
        });
        // it always rejects with undefined, no matter what value you pass (due to handling rejection in MessageGateway)
        return new Promise((resolve, reject) => ref.afterClosed.subscribe(resolve, reject));
    }
    getButtons(configuration) {
        return [
            {
                id: 'confirmOk',
                label: 'se.confirmation.modal.ok',
                style: smarteditcommons.ModalButtonStyle.Primary,
                action: smarteditcommons.ModalButtonAction.Close,
                callback: () => rxjs.of(true)
            },
            !configuration.showOkButtonOnly && {
                id: 'confirmCancel',
                label: 'se.confirmation.modal.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss,
                callback: () => rxjs.of(false)
            }
        ].filter((x) => !!x);
    }
};
ConfirmationModalService = __decorate([
    smarteditcommons.GatewayProxied('confirm'),
    __metadata("design:paramtypes", [smarteditcommons.IModalService])
], ConfirmationModalService);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class ToolbarService extends smarteditcommons.IToolbarService {
    constructor(gatewayId, gatewayProxy, logService, permissionService) {
        super(logService, permissionService);
        this.gatewayId = gatewayId;
        this.onAliasesChange = null;
        gatewayProxy.initForService(this, [
            'addAliases',
            'removeItemByKey',
            'removeAliasByKey',
            '_removeItemOnInner',
            'triggerActionOnInner'
        ]);
    }
    addAliases(aliases) {
        this.aliases = this.aliases.map((alias) => this.get(aliases, alias) || alias);
        this.aliases = [
            ...(this.aliases || []),
            ...(aliases || []).filter((alias) => !this.get(this.aliases, alias))
        ];
        this.aliases = this.sortAliases(this.aliases);
        if (this.onAliasesChange) {
            this.onAliasesChange(this.aliases);
        }
    }
    /**
     * This method removes the action and the aliases of the toolbar item identified by
     * the provided key.
     *
     * @param itemKey - Identifier of the toolbar item to remove.
     */
    removeItemByKey(itemKey) {
        if (itemKey in this.actions) {
            delete this.actions[itemKey];
        }
        else {
            this._removeItemOnInner(itemKey);
        }
        this.removeAliasByKey(itemKey);
    }
    removeAliasByKey(itemKey) {
        let aliasIndex = 0;
        for (; aliasIndex < this.aliases.length; aliasIndex++) {
            if (this.aliases[aliasIndex].key === itemKey) {
                break;
            }
        }
        if (aliasIndex < this.aliases.length) {
            this.aliases.splice(aliasIndex, 1);
        }
        if (this.onAliasesChange) {
            this.onAliasesChange(this.aliases);
        }
    }
    setOnAliasesChange(onAliasesChange) {
        this.onAliasesChange = onAliasesChange;
    }
    triggerAction(action) {
        if (action && this.actions[action.key]) {
            this.actions[action.key].call(action);
            return;
        }
        this.triggerActionOnInner(action);
    }
    get(aliases, alias) {
        return aliases.find(({ key }) => key === alias.key);
    }
    sortAliases(aliases) {
        let samePriority = false;
        let warning = `In ${this.gatewayId} the items `;
        let _section = null;
        const result = [...(aliases || [])].sort((a, b) => {
            if (a.priority === b.priority && a.section === b.section) {
                _section = a.section;
                warning += `${a.key} and ${b.key} `;
                samePriority = true;
                return a.key > b.key ? 1 : -1; // or the opposite ?
            }
            return a.priority - b.priority;
        });
        if (samePriority) {
            this.logService.warn(`WARNING: ${warning}have the same priority withing section:${_section}`);
        }
        return result;
    }
}

let /* @ngInject */ ToolbarServiceFactory = class /* @ngInject */ ToolbarServiceFactory {
    constructor(gatewayProxy, logService, permissionService) {
        this.gatewayProxy = gatewayProxy;
        this.logService = logService;
        this.permissionService = permissionService;
        this.toolbarServicesByGatewayId = {};
    }
    getToolbarService(gatewayId) {
        if (!this.toolbarServicesByGatewayId[gatewayId]) {
            this.toolbarServicesByGatewayId[gatewayId] = new ToolbarService(gatewayId, this.gatewayProxy, this.logService, this.permissionService);
        }
        return this.toolbarServicesByGatewayId[gatewayId];
    }
};
/* @ngInject */ ToolbarServiceFactory = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.GatewayProxy,
        smarteditcommons.LogService,
        smarteditcommons.IPermissionService])
], /* @ngInject */ ToolbarServiceFactory);

var ToolbarComponent_1;
window.__smartedit__.addDecoratorPayload("Component", "ToolbarComponent", {
    selector: 'se-toolbar',
    template: `<div class="se-toolbar" [ngClass]="cssClass"><div class="se-toolbar__left"><div *ngFor="let item of aliases | seProperty:{section:'left'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__left {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div><div class="se-toolbar__middle"><div *ngFor="let item of aliases | seProperty:{section:'middle'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__middle {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div><div class="se-toolbar__right"><div *ngFor="let item of aliases | seProperty:{section:'right'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__right {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div></div>`
});
let ToolbarComponent = ToolbarComponent_1 = class ToolbarComponent {
    constructor(toolbarServiceFactory, iframeClickDetectionService, systemEventService, injector, cdr, routingService, userTrackingService) {
        this.toolbarServiceFactory = toolbarServiceFactory;
        this.iframeClickDetectionService = iframeClickDetectionService;
        this.systemEventService = systemEventService;
        this.injector = injector;
        this.cdr = cdr;
        this.routingService = routingService;
        this.userTrackingService = userTrackingService;
        this.imageRoot = '';
        this.aliases = [];
        this.unregCloseActions = null;
        this.unregCloseAll = null;
        this.unregRecalcPermissions = null;
    }
    ngOnInit() {
        this.setup();
    }
    ngOnDestroy() {
        var _a, _b, _c;
        this.closeAllActionItems();
        (_a = this.unregCloseActions) === null || _a === void 0 ? void 0 : _a.call(this);
        (_b = this.unregCloseAll) === null || _b === void 0 ? void 0 : _b.call(this);
        (_c = this.unregRecalcPermissions) === null || _c === void 0 ? void 0 : _c.call(this);
    }
    triggerAction(action, $event) {
        $event.preventDefault();
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.TOOL_BAR, action.key);
        this.toolbarService.triggerAction(action);
    }
    getItemVisibility(item) {
        return item.component && (item.isOpen || item.keepAliveOnClose);
    }
    isOnStorefront() {
        return this.routingService.absUrl().includes(smarteditcommons.STORE_FRONT_CONTEXT);
    }
    createInjector(item) {
        return core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: smarteditcommons.TOOLBAR_ITEM,
                    useValue: item
                }
            ]
        });
    }
    trackByFn(_, item) {
        return item.key;
    }
    closeAllActionItems() {
        this.aliases.forEach((alias) => {
            alias.isOpen = false;
        });
    }
    populatePermissions() {
        return __awaiter(this, void 0, void 0, function* () {
            const promises = this.aliases.map((alias) => this.toolbarService._populateIsPermissionGranted(alias));
            this.aliases = yield Promise.all(promises);
        });
    }
    setup() {
        this.buildAliases();
        this.populatePermissions();
        this.registerCallbacks();
    }
    buildAliases() {
        this.toolbarService = this.toolbarServiceFactory.getToolbarService(this.toolbarName);
        this.toolbarService.setOnAliasesChange((aliases) => {
            this.aliases = aliases;
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
        this.aliases = this.toolbarService.getAliases();
    }
    registerCallbacks() {
        this.unregCloseActions = this.iframeClickDetectionService.registerCallback(ToolbarComponent_1.CLOSE_ALL_ACTION_ITEMS + this.toolbarName, () => this.closeAllActionItems());
        this.unregCloseAll = this.systemEventService.subscribe(smarteditcommons.EVENTS.PAGE_SELECTED, () => this.closeAllActionItems());
        this.unregRecalcPermissions = this.systemEventService.subscribe(smarteditcommons.EVENTS.PERMISSION_CACHE_CLEANED, () => this.populatePermissions());
    }
};
ToolbarComponent.CLOSE_ALL_ACTION_ITEMS = 'closeAllActionItems';
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ToolbarComponent.prototype, "cssClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ToolbarComponent.prototype, "toolbarName", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ToolbarComponent.prototype, "imageRoot", void 0);
ToolbarComponent = ToolbarComponent_1 = __decorate([
    core.Component({
        selector: 'se-toolbar',
        template: `<div class="se-toolbar" [ngClass]="cssClass"><div class="se-toolbar__left"><div *ngFor="let item of aliases | seProperty:{section:'left'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__left {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div><div class="se-toolbar__middle"><div *ngFor="let item of aliases | seProperty:{section:'middle'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__middle {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div><div class="se-toolbar__right"><div *ngFor="let item of aliases | seProperty:{section:'right'}; trackBy: trackByFn" class="se-template-toolbar se-template-toolbar__right {{item.className}}"><se-toolbar-section-item [item]="item"></se-toolbar-section-item></div></div></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.IToolbarServiceFactory,
        smarteditcommons.IIframeClickDetectionService,
        smarteditcommons.SystemEventService,
        core.Injector,
        core.ChangeDetectorRef,
        smarteditcommons.SmarteditRoutingService,
        smarteditcommons.IUserTrackingService])
], ToolbarComponent);

window.__smartedit__.addDecoratorPayload("Component", "ToolbarActionComponent", {
    selector: 'se-toolbar-action',
    template: `<div *ngIf="item.isPermissionGranted"><div *ngIf="item.type == type.ACTION" class="toolbar-action"><button fd-button type="button" [ngClass]="{
                'toolbar-action--button--compact': isCompact,
                'toolbar-action--button': !isCompact
            }" class="btn fd-button" (click)="toolbar.triggerAction(item, $event)" attr.aria-label="{{ item.name | translate}}" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn" title="{{ item.name | translate}}"><span *ngIf="item.iconClassName" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn_iconclass" class="{{item.iconClassName}}" [ngClass]="{ 'se-toolbar-actions__icon': isCompact }"></span> <img *ngIf="!item.iconClassName && item.icons" id="{{toolbar.toolbarName}}_option_{{item.key}}" src="{{toolbar.imageRoot}}{{item.icons[0]}}" class="file" title="{{item.name | translate}}" alt="{{item.name | translate}}"/> <span *ngIf="!isCompact" class="toolbar-action-button__txt" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn_lbl">{{item.name | translate}}</span></button></div><fd-popover class="se-toolbar-action__wrapper toolbar-action toolbar-action--hybrid" *ngIf="item.type === type.HYBRID_ACTION" [attr.data-item-key]="item.key" [noArrow]="false" [placement]="placement" [(isOpen)]="item.isOpen" [fixedPosition]="true"><fd-popover-control><button fd-button *ngIf="item.iconClassName || item.icons" type="button" class="btn fd-button" [ngClass]="{
                    'toolbar-action--button--compact': isCompact,
                    'toolbar-action--button': !isCompact
                }" [disabled]="disabled" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn" attr.aria-label="{{ item.name | translate}}" [attr.aria-expanded]="item.isOpen" (click)="toolbar.triggerAction(item, $event)" title="{{ item.name | translate}}"><span *ngIf="item.iconClassName" class="{{item.iconClassName}}" [ngClass]="{ 'se-toolbar-actions__icon': isCompact }"></span> <img *ngIf="!item.iconClassName && item.icons" id="{{toolbar.toolbarName}}_option_{{item.key}}" src="{{toolbar.imageRoot}}{{item.icons[0]}}" class="file" title="{{item.name | translate}}" alt="{{item.name | translate}}"/> <span *ngIf="!isCompact" class="toolbar-action-button__txt">{{item.name | translate}}</span></button></fd-popover-control><fd-popover-body role="listbox" class="se-toolbar-action__body" [ngClass]="{
                'toolbar-action--include--compact': isCompact,
                'toolbar-action--include': !isCompact  
            }"><se-prevent-vertical-overflow><ng-container *ngComponentOutlet="item.component; injector: actionInjector"></ng-container></se-prevent-vertical-overflow></fd-popover-body></fd-popover></div>`
});
let ToolbarActionComponent = class ToolbarActionComponent {
    constructor(toolbar) {
        this.toolbar = toolbar;
        this.type = smarteditcommons.ToolbarItemType;
    }
    ngOnInit() {
        this.setup();
    }
    ngOnChanges(changes) {
        if (changes.item) {
            this.setup();
        }
    }
    get isCompact() {
        return this.item.actionButtonFormat === 'compact';
    }
    get placement() {
        if (this.item.section === smarteditcommons.ToolbarSection.left ||
            this.item.section === smarteditcommons.ToolbarSection.middle) {
            return 'bottom-start';
        }
        else if (this.item.section === smarteditcommons.ToolbarSection.right) {
            return 'bottom-end';
        }
        switch (this.item.dropdownPosition) {
            case smarteditcommons.ToolbarDropDownPosition.left:
                return 'bottom-start';
            case smarteditcommons.ToolbarDropDownPosition.right:
                return 'bottom-end';
            default:
                return 'bottom';
        }
    }
    setup() {
        this.actionInjector = this.toolbar.createInjector(this.item);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ToolbarActionComponent.prototype, "item", void 0);
ToolbarActionComponent = __decorate([
    core.Component({
        selector: 'se-toolbar-action',
        template: `<div *ngIf="item.isPermissionGranted"><div *ngIf="item.type == type.ACTION" class="toolbar-action"><button fd-button type="button" [ngClass]="{
                'toolbar-action--button--compact': isCompact,
                'toolbar-action--button': !isCompact
            }" class="btn fd-button" (click)="toolbar.triggerAction(item, $event)" attr.aria-label="{{ item.name | translate}}" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn" title="{{ item.name | translate}}"><span *ngIf="item.iconClassName" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn_iconclass" class="{{item.iconClassName}}" [ngClass]="{ 'se-toolbar-actions__icon': isCompact }"></span> <img *ngIf="!item.iconClassName && item.icons" id="{{toolbar.toolbarName}}_option_{{item.key}}" src="{{toolbar.imageRoot}}{{item.icons[0]}}" class="file" title="{{item.name | translate}}" alt="{{item.name | translate}}"/> <span *ngIf="!isCompact" class="toolbar-action-button__txt" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn_lbl">{{item.name | translate}}</span></button></div><fd-popover class="se-toolbar-action__wrapper toolbar-action toolbar-action--hybrid" *ngIf="item.type === type.HYBRID_ACTION" [attr.data-item-key]="item.key" [noArrow]="false" [placement]="placement" [(isOpen)]="item.isOpen" [fixedPosition]="true"><fd-popover-control><button fd-button *ngIf="item.iconClassName || item.icons" type="button" class="btn fd-button" [ngClass]="{
                    'toolbar-action--button--compact': isCompact,
                    'toolbar-action--button': !isCompact
                }" [disabled]="disabled" id="{{toolbar.toolbarName}}_option_{{item.key}}_btn" attr.aria-label="{{ item.name | translate}}" [attr.aria-expanded]="item.isOpen" (click)="toolbar.triggerAction(item, $event)" title="{{ item.name | translate}}"><span *ngIf="item.iconClassName" class="{{item.iconClassName}}" [ngClass]="{ 'se-toolbar-actions__icon': isCompact }"></span> <img *ngIf="!item.iconClassName && item.icons" id="{{toolbar.toolbarName}}_option_{{item.key}}" src="{{toolbar.imageRoot}}{{item.icons[0]}}" class="file" title="{{item.name | translate}}" alt="{{item.name | translate}}"/> <span *ngIf="!isCompact" class="toolbar-action-button__txt">{{item.name | translate}}</span></button></fd-popover-control><fd-popover-body role="listbox" class="se-toolbar-action__body" [ngClass]="{
                'toolbar-action--include--compact': isCompact,
                'toolbar-action--include': !isCompact  
            }"><se-prevent-vertical-overflow><ng-container *ngComponentOutlet="item.component; injector: actionInjector"></ng-container></se-prevent-vertical-overflow></fd-popover-body></fd-popover></div>`
    }),
    __param(0, core.Inject(core.forwardRef(() => ToolbarComponent))),
    __metadata("design:paramtypes", [ToolbarComponent])
], ToolbarActionComponent);

window.__smartedit__.addDecoratorPayload("Component", "ToolbarActionOutletComponent", {
    selector: 'se-toolbar-action-outlet',
    template: `
        <div
            class="se-template-toolbar__action-template"
            [ngClass]="{
                'se-toolbar-action': isSectionRight,
                'se-toolbar-action--active': isSectionRight && isPermitionGranted
            }">
            <ng-container *ngIf="item.component && item.type === type.TEMPLATE">
                <ng-container
                    *ngComponentOutlet="item.component; injector: actionInjector"></ng-container>
            </ng-container>

            <!--ACTION and HYBRID_ACTION types-->

            <div *ngIf="item.type !== type.TEMPLATE">
                <se-toolbar-action [item]="item"></se-toolbar-action>
            </div>
        </div>
    `
});
/** @internal  */
let ToolbarActionOutletComponent = class ToolbarActionOutletComponent {
    constructor(toolbar) {
        this.toolbar = toolbar;
        this.type = smarteditcommons.ToolbarItemType;
    }
    ngOnInit() {
        this.setup();
    }
    ngOnChanges(changes) {
        if (changes.item) {
            this.setup();
        }
    }
    get isSectionRight() {
        return this.item.section === smarteditcommons.ToolbarSection.right;
    }
    get isPermitionGranted() {
        return this.item.isPermissionGranted;
    }
    setup() {
        this.actionInjector = this.toolbar.createInjector(this.item);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ToolbarActionOutletComponent.prototype, "item", void 0);
ToolbarActionOutletComponent = __decorate([
    core.Component({
        selector: 'se-toolbar-action-outlet',
        template: `
        <div
            class="se-template-toolbar__action-template"
            [ngClass]="{
                'se-toolbar-action': isSectionRight,
                'se-toolbar-action--active': isSectionRight && isPermitionGranted
            }">
            <ng-container *ngIf="item.component && item.type === type.TEMPLATE">
                <ng-container
                    *ngComponentOutlet="item.component; injector: actionInjector"></ng-container>
            </ng-container>

            <!--ACTION and HYBRID_ACTION types-->

            <div *ngIf="item.type !== type.TEMPLATE">
                <se-toolbar-action [item]="item"></se-toolbar-action>
            </div>
        </div>
    `
    }),
    __param(0, core.Inject(core.forwardRef(() => ToolbarComponent))),
    __metadata("design:paramtypes", [ToolbarComponent])
], ToolbarActionOutletComponent);

window.__smartedit__.addDecoratorPayload("Component", "ToolbarItemContextComponent", {
    selector: 'se-toolbar-item-context',
    template: `
        <div
            *ngIf="displayContext"
            id="toolbar_item_context_{{ itemKey }}_btn"
            class="se-toolbar-actionable-item-context"
            [ngClass]="{ 'se-toolbar-actionable-item-context--open': isOpen }">
            <div *ngIf="contextComponent" class="se-toolbar-actionable-context__btn">
                <ng-container *ngComponentOutlet="contextComponent"></ng-container>
            </div>
        </div>
    `
});
/** @internal  */
let ToolbarItemContextComponent = class ToolbarItemContextComponent {
    constructor(crossFrameEventService) {
        this.crossFrameEventService = crossFrameEventService;
        this.displayContext = false;
    }
    ngOnInit() {
        this.registerCallbacks();
    }
    ngOnDestroy() {
        this.unregShowContext();
        this.unregHideContext();
    }
    showContext(show) {
        this.displayContext = show;
    }
    registerCallbacks() {
        this.unregShowContext = this.crossFrameEventService.subscribe(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, (eventId, itemKey) => {
            if (itemKey === this.itemKey) {
                this.showContext(true);
            }
        });
        this.unregHideContext = this.crossFrameEventService.subscribe(smarteditcommons.HIDE_TOOLBAR_ITEM_CONTEXT, (eventId, itemKey) => {
            if (itemKey === this.itemKey) {
                this.showContext(false);
            }
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ToolbarItemContextComponent.prototype, "itemKey", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], ToolbarItemContextComponent.prototype, "isOpen", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", core.Type)
], ToolbarItemContextComponent.prototype, "contextComponent", void 0);
ToolbarItemContextComponent = __decorate([
    core.Component({
        selector: 'se-toolbar-item-context',
        template: `
        <div
            *ngIf="displayContext"
            id="toolbar_item_context_{{ itemKey }}_btn"
            class="se-toolbar-actionable-item-context"
            [ngClass]="{ 'se-toolbar-actionable-item-context--open': isOpen }">
            <div *ngIf="contextComponent" class="se-toolbar-actionable-context__btn">
                <ng-container *ngComponentOutlet="contextComponent"></ng-container>
            </div>
        </div>
    `
    }),
    __param(0, core.Inject(smarteditcommons.EVENT_SERVICE)),
    __metadata("design:paramtypes", [smarteditcommons.CrossFrameEventService])
], ToolbarItemContextComponent);

window.__smartedit__.addDecoratorPayload("Component", "ToolbarSectionItemComponent", {
    selector: 'se-toolbar-section-item',
    host: {
        class: 'se-toolbar-section-item'
    },
    template: `
        <se-toolbar-action-outlet [item]="item"></se-toolbar-action-outlet>
        <se-toolbar-item-context
            *ngIf="item.contextComponent"
            class="se-template-toolbar__context-template"
            [attr.data-item-key]="item.key"
            [itemKey]="item.key"
            [isOpen]="item.isOpen"
            [contextComponent]="item.contextComponent"></se-toolbar-item-context>
    `
});
/** @internal  */
let ToolbarSectionItemComponent = class ToolbarSectionItemComponent {
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ToolbarSectionItemComponent.prototype, "item", void 0);
ToolbarSectionItemComponent = __decorate([
    core.Component({
        selector: 'se-toolbar-section-item',
        host: {
            class: 'se-toolbar-section-item'
        },
        template: `
        <se-toolbar-action-outlet [item]="item"></se-toolbar-action-outlet>
        <se-toolbar-item-context
            *ngIf="item.contextComponent"
            class="se-template-toolbar__context-template"
            [attr.data-item-key]="item.key"
            [itemKey]="item.key"
            [isOpen]="item.isOpen"
            [contextComponent]="item.contextComponent"></se-toolbar-item-context>
    `
    })
], ToolbarSectionItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "DeviceSupportWrapperComponent", {
    selector: 'se-device-support-wrapper',
    template: `
        <inflection-point-selector
            *ngIf="toolbar.isOnStorefront()"
            class="toolbar-action"></inflection-point-selector>
    `
});
let DeviceSupportWrapperComponent = class DeviceSupportWrapperComponent {
    constructor(toolbar) {
        this.toolbar = toolbar;
    }
};
DeviceSupportWrapperComponent = __decorate([
    core.Component({
        selector: 'se-device-support-wrapper',
        template: `
        <inflection-point-selector
            *ngIf="toolbar.isOnStorefront()"
            class="toolbar-action"></inflection-point-selector>
    `
    }),
    __param(0, core.Inject(core.forwardRef(() => ToolbarComponent))),
    __metadata("design:paramtypes", [ToolbarComponent])
], DeviceSupportWrapperComponent);

window.__smartedit__.addDecoratorPayload("Component", "ExperienceSelectorButtonComponent", {
    selector: 'se-experience-selector-button',
    template: `<fd-popover class="se-experience-selector" [(isOpen)]="status.isOpen" (isOpenChange)="resetExperienceSelector()" [closeOnOutsideClick]="false" [triggers]="['click']" [placement]="'bottom-end'"><fd-popover-control><div class="se-experience-selector__control"><se-tooltip [placement]="'bottom'" [triggers]="['mouseenter', 'mouseleave']" *ngIf="isCurrentPageFromParent" class="se-experience-selector__tooltip"><span se-tooltip-trigger class="se-experience-selector__btn--globe"><span class="sap-icon--globe se-experience-selector__btn--globe--icon"></span></span><div se-tooltip-body>{{ parentCatalogVersion }}</div></se-tooltip><button class="se-experience-selector__btn" id="experience-selector-btn"><span [attr.title]="experienceText" class="se-experience-selector__btn-text se-nowrap-ellipsis">{{ experienceText }} </span><span class="se-experience-selector__btn-arrow sap-icon--navigation-down-arrow" title="{{ getNavigationArrowIconIsCollapse() | translate }}"></span></button></div></fd-popover-control><fd-popover-body><div class="se-experience-selector__dropdown" role="menu"><se-experience-selector [experience]="experience" [dropdownStatus]="status" [(resetExperienceSelector)]="resetExperienceSelector"></se-experience-selector></div></fd-popover-body></fd-popover>`,
    providers: [smarteditcommons.L10nPipe]
});
let ExperienceSelectorButtonComponent = class ExperienceSelectorButtonComponent {
    constructor(systemEventService, crossFrameEventService, locale, sharedDataService, l10nPipe) {
        this.systemEventService = systemEventService;
        this.crossFrameEventService = crossFrameEventService;
        this.locale = locale;
        this.sharedDataService = sharedDataService;
        this.l10nPipe = l10nPipe;
        this.status = { isOpen: false };
        this.isCurrentPageFromParent = false;
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.updateExperience();
            yield this.setExperienceText();
            this.unregFn = this.systemEventService.subscribe(smarteditcommons.EVENTS.EXPERIENCE_UPDATE, () => __awaiter(this, void 0, void 0, function* () {
                yield this.updateExperience();
                yield this.setExperienceText();
            }));
            this.unRegNewPageContextEventFn = this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, (eventId, data) => {
                this.setPageFromParent(data);
            });
        });
    }
    ngOnDestroy() {
        this.unregFn();
        this.unRegNewPageContextEventFn();
    }
    updateExperience() {
        return __awaiter(this, void 0, void 0, function* () {
            this.experience = (yield this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY));
        });
    }
    setPageFromParent(data) {
        return __awaiter(this, void 0, void 0, function* () {
            const { pageContext: { catalogName, catalogVersion, catalogVersionUuid: pageContextCatalogVersionUuid }, catalogDescriptor: { catalogVersionUuid: catalogDescriptorCatalogVersionUuid } } = data;
            const translatedName = yield this.l10nPipe.transform(catalogName).pipe(operators.take(1)).toPromise();
            this.parentCatalogVersion = `${translatedName} (${catalogVersion})`;
            this.isCurrentPageFromParent =
                catalogDescriptorCatalogVersionUuid !== pageContextCatalogVersionUuid;
        });
    }
    getNavigationArrowIconIsCollapse() {
        return this.status.isOpen ? 'se.arrowicon.collapse.title' : 'se.arrowicon.expand.title';
    }
    setExperienceText() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.experience) {
                this.experienceText = '';
            }
            const { catalogDescriptor: { name, catalogVersion }, languageDescriptor: { nativeName }, time } = this.experience;
            const pipe = new common.DatePipe(this.locale);
            const transformedTime = time
                ? `  |  ${pipe.transform(moment__default["default"](time).isValid() ? time : moment__default["default"].now(), smarteditcommons.DATE_CONSTANTS.ANGULAR_SHORT)}`
                : '';
            const [translatedName, catalogVersions] = yield Promise.all([
                this.l10nPipe.transform(name).pipe(operators.take(1)).toPromise(),
                this.getProductCatalogVersionTextByUuids()
            ]);
            this.experienceText = `${translatedName} - ${catalogVersion}  |  ${nativeName}${transformedTime}${catalogVersions}`;
        });
    }
    getProductCatalogVersionTextByUuids() {
        return __awaiter(this, void 0, void 0, function* () {
            const { productCatalogVersions } = this.experience;
            const SEPARATOR = '';
            const versionPromises = productCatalogVersions.map(({ catalogName, catalogVersion }) => __awaiter(this, void 0, void 0, function* () {
                const translatedName = yield this.l10nPipe
                    .transform(catalogName)
                    .pipe(operators.take(1))
                    .toPromise();
                return `${translatedName} (${catalogVersion})`;
            }));
            const versions = yield Promise.all(versionPromises);
            return [SEPARATOR].concat(versions).join(' | ');
        });
    }
};
ExperienceSelectorButtonComponent = __decorate([
    core.Component({
        selector: 'se-experience-selector-button',
        template: `<fd-popover class="se-experience-selector" [(isOpen)]="status.isOpen" (isOpenChange)="resetExperienceSelector()" [closeOnOutsideClick]="false" [triggers]="['click']" [placement]="'bottom-end'"><fd-popover-control><div class="se-experience-selector__control"><se-tooltip [placement]="'bottom'" [triggers]="['mouseenter', 'mouseleave']" *ngIf="isCurrentPageFromParent" class="se-experience-selector__tooltip"><span se-tooltip-trigger class="se-experience-selector__btn--globe"><span class="sap-icon--globe se-experience-selector__btn--globe--icon"></span></span><div se-tooltip-body>{{ parentCatalogVersion }}</div></se-tooltip><button class="se-experience-selector__btn" id="experience-selector-btn"><span [attr.title]="experienceText" class="se-experience-selector__btn-text se-nowrap-ellipsis">{{ experienceText }} </span><span class="se-experience-selector__btn-arrow sap-icon--navigation-down-arrow" title="{{ getNavigationArrowIconIsCollapse() | translate }}"></span></button></div></fd-popover-control><fd-popover-body><div class="se-experience-selector__dropdown" role="menu"><se-experience-selector [experience]="experience" [dropdownStatus]="status" [(resetExperienceSelector)]="resetExperienceSelector"></se-experience-selector></div></fd-popover-body></fd-popover>`,
        providers: [smarteditcommons.L10nPipe]
    }),
    __param(2, core.Inject(core.LOCALE_ID)),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService,
        smarteditcommons.CrossFrameEventService, String, smarteditcommons.ISharedDataService,
        smarteditcommons.L10nPipe])
], ExperienceSelectorButtonComponent);

window.__smartedit__.addDecoratorPayload("Component", "InflectionPointSelectorComponent", {
    selector: 'inflection-point-selector',
    template: `<div class="se-inflection-point dropdown" [class.open]="isOpen" id="inflectionPtDropdown"><button type="button" class="se-inflection-point__toggle" (click)="toggleDropdown($event)" aria-pressed="false" [attr.aria-expanded]="isOpen" title="{{'se.device.support.selector.btn' | translate}}"><span [ngClass]="getIconClass()" class="se-inflection-point___selected"></span></button><div class="se-inflection-point-dropdown"><nav><ul class="fd-menu__list" role="menu"><li *ngFor="let choice of points" class="fd-menu__item inflection-point__device" [ngClass]="{selected: isSelected(choice)}" id="se-device-{{choice.type}}" (click)="selectPoint(choice)" title="{{choice.name | translate}}"><span [ngClass]="getIconClass(choice)"></span></li></ul></nav></div></div>`
});
let InflectionPointSelectorComponent = class InflectionPointSelectorComponent {
    constructor(systemEventService, iframeManagerService, iframeClickDetectionService, yjQuery, userTrackingService) {
        this.systemEventService = systemEventService;
        this.iframeManagerService = iframeManagerService;
        this.iframeClickDetectionService = iframeClickDetectionService;
        this.yjQuery = yjQuery;
        this.userTrackingService = userTrackingService;
        this.currentPointSelected = DEVICE_SUPPORTS.find((deviceSupport) => deviceSupport.default);
        this.points = DEVICE_SUPPORTS;
        this.isOpen = false;
    }
    ngOnInit() {
        this.iframeClickDetectionService.registerCallback('inflectionPointClose', () => (this.isOpen = false));
        const window = smarteditcommons.windowUtils.getWindow();
        this.documentClick$ = rxjs.fromEvent(window.document, 'click')
            .pipe(operators.filter((event) => this.yjQuery(event.target).parents('inflection-point-selector').length <=
            0 && this.isOpen))
            .subscribe((event) => (this.isOpen = false));
        this.unRegFn = this.systemEventService.subscribe(smarteditcommons.OVERLAY_DISABLED_EVENT, () => (this.isOpen = false));
    }
    ngOnDestroy() {
        this.unRegFn();
        this.documentClick$.unsubscribe();
    }
    selectPoint(choice) {
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.INFLECTION, choice.type);
        this.currentPointSelected = choice;
        this.isOpen = !this.isOpen;
        if (choice !== undefined) {
            this.iframeManagerService.apply(choice);
        }
    }
    toggleDropdown(event) {
        event.preventDefault();
        event.stopPropagation();
        this.isOpen = !this.isOpen;
    }
    getIconClass(choice) {
        if (choice !== undefined) {
            return choice.iconClass;
        }
        else {
            return this.currentPointSelected.iconClass;
        }
    }
    isSelected(choice) {
        if (choice !== undefined) {
            return choice.type === this.currentPointSelected.type;
        }
        return false;
    }
};
InflectionPointSelectorComponent = __decorate([
    core.Component({
        selector: 'inflection-point-selector',
        template: `<div class="se-inflection-point dropdown" [class.open]="isOpen" id="inflectionPtDropdown"><button type="button" class="se-inflection-point__toggle" (click)="toggleDropdown($event)" aria-pressed="false" [attr.aria-expanded]="isOpen" title="{{'se.device.support.selector.btn' | translate}}"><span [ngClass]="getIconClass()" class="se-inflection-point___selected"></span></button><div class="se-inflection-point-dropdown"><nav><ul class="fd-menu__list" role="menu"><li *ngFor="let choice of points" class="fd-menu__item inflection-point__device" [ngClass]="{selected: isSelected(choice)}" id="se-device-{{choice.type}}" (click)="selectPoint(choice)" title="{{choice.name | translate}}"><span [ngClass]="getIconClass(choice)"></span></li></ul></nav></div></div>`
    }),
    __param(2, core.Inject(smarteditcommons.IIframeClickDetectionService)),
    __param(3, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService,
        IframeManagerService,
        IframeClickDetectionService, Function, smarteditcommons.IUserTrackingService])
], InflectionPointSelectorComponent);

window.__smartedit__.addDecoratorPayload("Component", "ExperienceSelectorComponent", {
    selector: 'se-experience-selector',
    template: `<se-generic-editor *ngIf="isReady" [smarteditComponentType]="smarteditComponentType" [smarteditComponentId]="smarteditComponentId" [structureApi]="structureApi" [content]="content" [contentApi]="contentApi" (getApi)="getApi($event)"></se-generic-editor>`,
    providers: [smarteditcommons.L10nPipe]
});
let ExperienceSelectorComponent = class ExperienceSelectorComponent {
    constructor(systemEventService, siteService, sharedDataService, iframeClickDetectionService, iframeManagerService, experienceService, catalogService, l10nPipe, userTrackingService) {
        this.systemEventService = systemEventService;
        this.siteService = siteService;
        this.sharedDataService = sharedDataService;
        this.iframeClickDetectionService = iframeClickDetectionService;
        this.iframeManagerService = iframeManagerService;
        this.experienceService = experienceService;
        this.catalogService = catalogService;
        this.l10nPipe = l10nPipe;
        this.userTrackingService = userTrackingService;
        this.resetExperienceSelectorChange = new core.EventEmitter();
        this.modalHeaderTitle = 'se.experience.selector.header';
        this.siteCatalogs = {};
    }
    ngOnInit() {
        setTimeout(() => {
            this.resetExperienceSelectorChange.emit(() => this.resetExperienceSelectorFn());
        });
        this.unRegCloseExperienceFn = this.iframeClickDetectionService.registerCallback('closeExperienceSelector', () => {
            if (this.dropdownStatus && this.dropdownStatus.isOpen) {
                this.dropdownStatus.isOpen = false;
            }
        });
        this.unRegFn = this.systemEventService.subscribe('OVERLAY_DISABLED', () => {
            if (this.dropdownStatus && this.dropdownStatus.isOpen) {
                this.dropdownStatus.isOpen = false;
            }
        });
    }
    ngOnDestroy() {
        if (this.unRegFn) {
            this.unRegFn();
        }
        if (this.unRegCloseExperienceFn) {
            this.unRegCloseExperienceFn();
        }
    }
    preparePayload(experienceContent) {
        return __awaiter(this, void 0, void 0, function* () {
            [this.siteCatalogs.siteId, this.siteCatalogs.catalogId, this.siteCatalogs.catalogVersion] =
                experienceContent.previewCatalog.split('|');
            const productCatalogs = yield this.catalogService.getProductCatalogsForSite(this.siteCatalogs.siteId);
            const { domain } = (yield this.sharedDataService.get('configuration'));
            const { previewUrl, uid: siteId } = yield this.siteService.getSiteById(this.siteCatalogs.siteId);
            const { language, time, pageId, productCatalogVersions } = experienceContent;
            this.siteCatalogs.productCatalogs = productCatalogs;
            this.siteCatalogs.productCatalogVersions = productCatalogVersions;
            this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.TOOL_BAR, 'update Experience');
            return Object.assign(Object.assign({}, experienceContent), { resourcePath: smarteditcommons.urlUtils.getAbsoluteURL(domain, previewUrl), catalogVersions: [
                    ...this._getProductCatalogsByUuids(productCatalogVersions),
                    {
                        catalog: this.siteCatalogs.catalogId,
                        catalogVersion: this.siteCatalogs.catalogVersion
                    }
                ], siteId,
                language,
                time,
                pageId });
        });
    }
    updateCallback(payload, response) {
        return __awaiter(this, void 0, void 0, function* () {
            const { siteId, catalogId, catalogVersion, productCatalogVersions } = this.siteCatalogs;
            const { time } = payload;
            const { pageId, language, ticketId } = response;
            this.smarteditComponentId = null;
            this.dropdownStatus.isOpen = false;
            const experienceParams = Object.assign(Object.assign({}, response), { siteId,
                catalogId,
                catalogVersion,
                productCatalogVersions, time: smarteditcommons.dateUtils.formatDateAsUtc(time), pageId,
                language });
            const experience = yield this.experienceService.buildAndSetExperience(experienceParams);
            yield this.sharedDataService.set(smarteditcommons.EXPERIENCE_STORAGE_KEY, experience);
            this.systemEventService.publishAsync(smarteditcommons.EVENTS.EXPERIENCE_UPDATE);
            this.iframeManagerService.loadPreview(experience.siteDescriptor.previewUrl, ticketId);
        });
    }
    getApi($api) {
        $api.setPreparePayload(this.preparePayload.bind(this));
        $api.setUpdateCallback(this.updateCallback.bind(this));
        $api.setAlwaysShowSubmit(true);
        $api.setAlwaysShowReset(true);
        $api.setSubmitButtonText('se.componentform.actions.apply');
        $api.setCancelButtonText('se.componentform.actions.cancel');
        $api.setOnReset(() => {
            this.dropdownStatus.isOpen = false;
            return this.dropdownStatus.isOpen;
        });
    }
    _getProductCatalogsByUuids(versionUuids) {
        return this.siteCatalogs.productCatalogs.map(({ versions, catalogId }) => ({
            catalogVersion: versions.find(({ uuid }) => versionUuids.indexOf(uuid) > -1).version,
            catalog: catalogId
        }));
    }
    resetExperienceSelectorFn() {
        return __awaiter(this, void 0, void 0, function* () {
            const experience = (yield this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY));
            const configuration = (yield this.sharedDataService.get('configuration'));
            this.smarteditComponentType = 'PreviewData';
            this.smarteditComponentId = null;
            this.structureApi = smarteditcommons.TYPES_RESOURCE_URI + '?code=:smarteditComponentType&mode=DEFAULT';
            this.contentApi = (configuration && configuration.previewTicketURI) || smarteditcommons.PREVIEW_RESOURCE_URI;
            const activeSiteTranslated = yield this.l10nPipe
                .transform(experience.siteDescriptor.name)
                .pipe(operators.take(1))
                .toPromise();
            this.content = Object.assign(Object.assign({}, experience), { activeSite: activeSiteTranslated, time: experience.time, pageId: experience.pageId, productCatalogVersions: experience.productCatalogVersions.map((productCatalogVersion) => productCatalogVersion.uuid), language: experience.languageDescriptor.isocode, previewCatalog: `${experience.siteDescriptor.uid}|${experience.catalogDescriptor.catalogId}|${experience.catalogDescriptor.catalogVersion}` });
            if (!this.isReady) {
                this.isReady = true;
            }
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ExperienceSelectorComponent.prototype, "experience", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], ExperienceSelectorComponent.prototype, "dropdownStatus", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], ExperienceSelectorComponent.prototype, "resetExperienceSelector", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], ExperienceSelectorComponent.prototype, "resetExperienceSelectorChange", void 0);
ExperienceSelectorComponent = __decorate([
    core.Component({
        selector: 'se-experience-selector',
        template: `<se-generic-editor *ngIf="isReady" [smarteditComponentType]="smarteditComponentType" [smarteditComponentId]="smarteditComponentId" [structureApi]="structureApi" [content]="content" [contentApi]="contentApi" (getApi)="getApi($event)"></se-generic-editor>`,
        providers: [smarteditcommons.L10nPipe]
    }),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService,
        SiteService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.IIframeClickDetectionService,
        IframeManagerService,
        smarteditcommons.IExperienceService,
        smarteditcommons.ICatalogService,
        smarteditcommons.L10nPipe,
        smarteditcommons.IUserTrackingService])
], ExperienceSelectorComponent);

const imgUrl = 'static-resources/images/';
window.__smartedit__.addDecoratorPayload("Component", "ThemeSwitchComponent", {
    selector: 'fd-theme-switch',
    template: `<fd-grid-list [layoutPattern]="layoutPattern" selectionMode="singleSelect" (selectionChange)="onSelectionChange($event)"><fd-grid-list-item *ngFor="let item of list" [value]="item.id" [noPadding]="true" (press)="press($event)" [selected]="item.selected"><fd-grid-list-item-toolbar *ngIf="item.toolbarText">{{ item.toolbarText }}</fd-grid-list-item-toolbar><img [id]="item.id" [src]="item.url" [alt]="item.toolbarText"/></fd-grid-list-item></fd-grid-list>`,
    styles: [`fd-theme-switch .fd-grid-list{margin-top:30px}fd-theme-switch .fd-grid-list fd-grid-list-item{height:180px}fd-theme-switch .fd-grid-list fd-grid-list-item img{height:100%}`],
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    encapsulation: core.ViewEncapsulation.None
});
let ThemeSwitchComponent = class ThemeSwitchComponent {
    constructor(_themesService, _themeSwitchService, modalManager, confirmationModalService) {
        this._themesService = _themesService;
        this._themeSwitchService = _themeSwitchService;
        this.modalManager = modalManager;
        this.confirmationModalService = confirmationModalService;
        this.themes = this._themesService.getThemes();
        this.list = this.themes.themes.map((theme) => ({
            id: theme.code,
            toolbarText: theme.name,
            url: `${imgUrl}${theme.code}.png`,
            selected: this._themeSwitchService.selectedTheme === theme.code
        }));
        this.layoutPattern = 'XL3-L3-M2-S1';
    }
    ngOnInit() {
        this.modalManager.addButtons([
            {
                id: 'save',
                style: smarteditcommons.ModalButtonStyle.Primary,
                label: 'se.cms.component.confirmation.modal.save',
                callback: () => rxjs.from(this.onSave()),
                disabled: true
            },
            {
                id: 'cancel',
                label: 'se.cms.component.confirmation.modal.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss,
                callback: () => rxjs.from(this.onCancel())
            }
        ]);
    }
    onSelectionChange(event) {
        if (!event.removed.length) {
            return;
        }
        if (event.selection[0] !== this._themeSwitchService.getThemeSession()) {
            this.modalManager.enableButton('save');
        }
        else {
            this.modalManager.disableButton('save');
        }
        this._themeSwitchService.selectTheme(event.selection[0]);
    }
    onCancel() {
        const buttonsSource = this.modalManager.getButtons();
        const saveBtnIndex = 0;
        let dirty;
        buttonsSource.forEach((buttons) => {
            dirty = !buttons[saveBtnIndex].disabled;
        });
        const confirmationData = {
            description: 'se.editor.cancel.confirm'
        };
        if (!dirty) {
            this.modalManager.close(null);
            return Promise.resolve();
        }
        return this.confirmationModalService.confirm(confirmationData).then(() => {
            if (this._themeSwitchService.getThemeSession() !==
                this._themeSwitchService.selectedTheme) {
                this._themeSwitchService.selectTheme(this._themeSwitchService.getThemeSession());
            }
            this.modalManager.close(null);
        });
    }
    onSave() {
        if (this._themeSwitchService.getThemeSession() !== this._themeSwitchService.selectedTheme) {
            this._themeSwitchService.saveTheme();
        }
        this.modalManager.close(null);
        return Promise.resolve();
    }
};
ThemeSwitchComponent = __decorate([
    core.Component({
        selector: 'fd-theme-switch',
        template: `<fd-grid-list [layoutPattern]="layoutPattern" selectionMode="singleSelect" (selectionChange)="onSelectionChange($event)"><fd-grid-list-item *ngFor="let item of list" [value]="item.id" [noPadding]="true" (press)="press($event)" [selected]="item.selected"><fd-grid-list-item-toolbar *ngIf="item.toolbarText">{{ item.toolbarText }}</fd-grid-list-item-toolbar><img [id]="item.id" [src]="item.url" [alt]="item.toolbarText"/></fd-grid-list-item></fd-grid-list>`,
        styles: [`fd-theme-switch .fd-grid-list{margin-top:30px}fd-theme-switch .fd-grid-list fd-grid-list-item{height:180px}fd-theme-switch .fd-grid-list fd-grid-list-item img{height:100%}`],
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        encapsulation: core.ViewEncapsulation.None
    }),
    __metadata("design:paramtypes", [smarteditcommons.ThemesService,
        exports.ThemeSwitchService,
        smarteditcommons.ModalManagerService,
        smarteditcommons.IConfirmationModalService])
], ThemeSwitchComponent);

window.__smartedit__.addDecoratorPayload("Component", "UserAccountComponent", {
    selector: 'se-user-account',
    template: `<div class="se-user-account-dropdown"><div class="se-user-account-dropdown__role">{{ 'se.toolbar.useraccount.role' | translate }}</div><div class="user-account-dropdown__name">{{username}}</div><div class="divider"></div><a class="se-theme-switch__link fd-menu__item" (click)="themeSwitch()"><fd-icon role="presentation" class="sap-icon--palette ng-star-inserted"></fd-icon><span fd-menu-title="" class="fd-menu__title">{{'se.modal.administration.theme.switch.title' | translate}} </span></a><a class="se-sign-out__link fd-menu__item" (click)="signOut()"><fd-icon role="presentation" class="sap-icon--log ng-star-inserted"></fd-icon><span fd-menu-title="" class="fd-menu__title">{{'se.toolbar.useraccount.signout' | translate}}</span></a></div>`
});
let UserAccountComponent = class UserAccountComponent {
    constructor(authenticationService, iframeManagerService, crossFrameEventService, sessionService, modalService, themesService) {
        this.authenticationService = authenticationService;
        this.iframeManagerService = iframeManagerService;
        this.crossFrameEventService = crossFrameEventService;
        this.sessionService = sessionService;
        this.modalService = modalService;
        this.themesService = themesService;
    }
    ngOnInit() {
        this.unregUserChanged = this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, () => this.getUsername());
        this.getUsername();
        this.crossFrameEventService.subscribe(smarteditcommons.SWITCH_LANGUAGE_EVENT, () => this.themesService.initThemes());
    }
    ngOnDestroy() {
        this.unregUserChanged();
    }
    signOut() {
        this.authenticationService.logout();
        this.iframeManagerService.setCurrentLocation(null);
    }
    themeSwitch() {
        this.modalService.open({
            templateConfig: {
                title: 'se.modal.administration.theme.switch.title'
            },
            component: ThemeSwitchComponent,
            config: {
                focusTrapped: false,
                backdropClickCloseable: false,
                width: '650px',
                height: '380px'
            }
        });
    }
    getUsername() {
        this.sessionService.getCurrentUserDisplayName().then((displayName) => {
            this.username = displayName;
        });
    }
};
UserAccountComponent = __decorate([
    core.Component({
        selector: 'se-user-account',
        template: `<div class="se-user-account-dropdown"><div class="se-user-account-dropdown__role">{{ 'se.toolbar.useraccount.role' | translate }}</div><div class="user-account-dropdown__name">{{username}}</div><div class="divider"></div><a class="se-theme-switch__link fd-menu__item" (click)="themeSwitch()"><fd-icon role="presentation" class="sap-icon--palette ng-star-inserted"></fd-icon><span fd-menu-title="" class="fd-menu__title">{{'se.modal.administration.theme.switch.title' | translate}} </span></a><a class="se-sign-out__link fd-menu__item" (click)="signOut()"><fd-icon role="presentation" class="sap-icon--log ng-star-inserted"></fd-icon><span fd-menu-title="" class="fd-menu__title">{{'se.toolbar.useraccount.signout' | translate}}</span></a></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.IAuthenticationService,
        IframeManagerService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.ISessionService,
        smarteditcommons.IModalService,
        smarteditcommons.ThemesService])
], UserAccountComponent);

window.__smartedit__.addDecoratorPayload("Component", "ExperienceSelectorWrapperComponent", {
    selector: 'se-experience-selector-wrapper',
    template: `
        <se-experience-selector-button
            *ngIf="toolbar.isOnStorefront()"></se-experience-selector-button>
    `
});
let ExperienceSelectorWrapperComponent = class ExperienceSelectorWrapperComponent {
    constructor(toolbar) {
        this.toolbar = toolbar;
    }
};
ExperienceSelectorWrapperComponent = __decorate([
    core.Component({
        selector: 'se-experience-selector-wrapper',
        template: `
        <se-experience-selector-button
            *ngIf="toolbar.isOnStorefront()"></se-experience-selector-button>
    `
    }),
    __param(0, core.Inject(core.forwardRef(() => ToolbarComponent))),
    __metadata("design:paramtypes", [ToolbarComponent])
], ExperienceSelectorWrapperComponent);

window.__smartedit__.addDecoratorPayload("Component", "LogoComponent", {
    selector: 'se-logo',
    template: `
        <div class="se-app-logo">
            <img
                src="static-resources/images/SAP_scrn_R.png"
                class="se-logo-image"
                width="48"
                height="24"
                title="{{ 'se.logo.title' | translate }}" />
            <div class="se-logo-text">{{ 'se.application.name' | translate }}</div>
        </div>
    `
});
let LogoComponent = class LogoComponent {
};
LogoComponent = __decorate([
    core.Component({
        selector: 'se-logo',
        template: `
        <div class="se-app-logo">
            <img
                src="static-resources/images/SAP_scrn_R.png"
                class="se-logo-image"
                width="48"
                height="24"
                title="{{ 'se.logo.title' | translate }}" />
            <div class="se-logo-text">{{ 'se.application.name' | translate }}</div>
        </div>
    `
    })
], LogoComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// upgrade qualtrix from 1.0 to 2.0.1
var Kt = Object.defineProperty,
    Xt = Object.defineProperties;
var te = Object.getOwnPropertyDescriptors;
var xt = Object.getOwnPropertySymbols;
var ee = Object.prototype.hasOwnProperty,
    se = Object.prototype.propertyIsEnumerable;
var X = (v, u, d) =>
        u in v
            ? Kt(v, u, { enumerable: !0, configurable: !0, writable: !0, value: d })
            : (v[u] = d),
    F = (v, u) => {
        for (var d in u || (u = {})) ee.call(u, d) && X(v, d, u[d]);
        if (xt) for (var d of xt(u)) se.call(u, d) && X(v, d, u[d]);
        return v;
    },
    z = (v, u) => Xt(v, te(u));
var At = (v, u, d) => (X(v, typeof u != 'symbol' ? u + '' : u, d), d);
var St = (v, u, d) =>
    new Promise((L, C) => {
        var V = (g) => {
                try {
                    I(d.next(g));
                } catch (y) {
                    C(y);
                }
            },
            D = (g) => {
                try {
                    I(d.throw(g));
                } catch (y) {
                    C(y);
                }
            },
            I = (g) => (g.done ? L(g.value) : Promise.resolve(g.value).then(V, D));
        I((d = d.apply(v, u)).next());
    });
(function () {
    /**
     * @license
     * Copyright 2019 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ const v = window,
        u =
            v.ShadowRoot &&
            (v.ShadyCSS === void 0 || v.ShadyCSS.nativeShadow) &&
            'adoptedStyleSheets' in Document.prototype &&
            'replace' in CSSStyleSheet.prototype,
        d = Symbol(),
        L = new WeakMap();
    class C {
        constructor(t, e, s) {
            if (((this._$cssResult$ = !0), s !== d))
                throw Error('CSSResult is not constructable. Use `unsafeCSS` or `css` instead.');
            (this.cssText = t), (this.t = e);
        }
        get styleSheet() {
            let t = this.o;
            const e = this.t;
            if (u && t === void 0) {
                const s = e !== void 0 && e.length === 1;
                s && (t = L.get(e)),
                    t === void 0 &&
                        ((this.o = t = new CSSStyleSheet()).replaceSync(this.cssText),
                        s && L.set(e, t));
            }
            return t;
        }
        toString() {
            return this.cssText;
        }
    }
    const V = (r) => new C(typeof r == 'string' ? r : r + '', void 0, d),
        D = (r, ...t) => {
            const e =
                r.length === 1
                    ? r[0]
                    : t.reduce(
                          (s, o, i) =>
                              s +
                              ((n) => {
                                  if (n._$cssResult$ === !0) return n.cssText;
                                  if (typeof n == 'number') return n;
                                  throw Error(
                                      "Value passed to 'css' function must be a 'css' function result: " +
                                          n +
                                          ". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security."
                                  );
                              })(o) +
                              r[i + 1],
                          r[0]
                      );
            return new C(e, r, d);
        },
        I = (r, t) => {
            u
                ? (r.adoptedStyleSheets = t.map((e) =>
                      e instanceof CSSStyleSheet ? e : e.styleSheet
                  ))
                : t.forEach((e) => {
                      const s = document.createElement('style'),
                          o = v.litNonce;
                      o !== void 0 && s.setAttribute('nonce', o),
                          (s.textContent = e.cssText),
                          r.appendChild(s);
                  });
        },
        g = u
            ? (r) => r
            : (r) =>
                  r instanceof CSSStyleSheet
                      ? ((t) => {
                            let e = '';
                            for (const s of t.cssRules) e += s.cssText;
                            return V(e);
                        })(r)
                      : r;
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var y;
    const N = window,
        tt = N.trustedTypes,
        wt = tt ? tt.emptyScript : '',
        et = N.reactiveElementPolyfillSupport,
        G = {
            toAttribute(r, t) {
                switch (t) {
                    case Boolean:
                        r = r ? wt : null;
                        break;
                    case Object:
                    case Array:
                        r = r == null ? r : JSON.stringify(r);
                }
                return r;
            },
            fromAttribute(r, t) {
                let e = r;
                switch (t) {
                    case Boolean:
                        e = r !== null;
                        break;
                    case Number:
                        e = r === null ? null : Number(r);
                        break;
                    case Object:
                    case Array:
                        try {
                            e = JSON.parse(r);
                        } catch (s) {
                            e = null;
                        }
                }
                return e;
            }
        },
        st = (r, t) => t !== r && (t == t || r == r),
        Z = { attribute: !0, type: String, converter: G, reflect: !1, hasChanged: st };
    class x extends HTMLElement {
        constructor() {
            super(),
                (this._$Ei = new Map()),
                (this.isUpdatePending = !1),
                (this.hasUpdated = !1),
                (this._$El = null),
                this.u();
        }
        static addInitializer(t) {
            var e;
            ((e = this.h) !== null && e !== void 0) || (this.h = []), this.h.push(t);
        }
        static get observedAttributes() {
            this.finalize();
            const t = [];
            return (
                this.elementProperties.forEach((e, s) => {
                    const o = this._$Ep(s, e);
                    o !== void 0 && (this._$Ev.set(o, s), t.push(o));
                }),
                t
            );
        }
        static createProperty(t, e = Z) {
            if (
                (e.state && (e.attribute = !1),
                this.finalize(),
                this.elementProperties.set(t, e),
                !e.noAccessor && !this.prototype.hasOwnProperty(t))
            ) {
                const s = typeof t == 'symbol' ? Symbol() : '__' + t,
                    o = this.getPropertyDescriptor(t, s, e);
                o !== void 0 && Object.defineProperty(this.prototype, t, o);
            }
        }
        static getPropertyDescriptor(t, e, s) {
            return {
                get() {
                    return this[e];
                },
                set(o) {
                    const i = this[t];
                    (this[e] = o), this.requestUpdate(t, i, s);
                },
                configurable: !0,
                enumerable: !0
            };
        }
        static getPropertyOptions(t) {
            return this.elementProperties.get(t) || Z;
        }
        static finalize() {
            if (this.hasOwnProperty('finalized')) return !1;
            this.finalized = !0;
            const t = Object.getPrototypeOf(this);
            if (
                (t.finalize(),
                (this.elementProperties = new Map(t.elementProperties)),
                (this._$Ev = new Map()),
                this.hasOwnProperty('properties'))
            ) {
                const e = this.properties,
                    s = [...Object.getOwnPropertyNames(e), ...Object.getOwnPropertySymbols(e)];
                for (const o of s) this.createProperty(o, e[o]);
            }
            return (this.elementStyles = this.finalizeStyles(this.styles)), !0;
        }
        static finalizeStyles(t) {
            const e = [];
            if (Array.isArray(t)) {
                const s = new Set(t.flat(1 / 0).reverse());
                for (const o of s) e.unshift(g(o));
            } else t !== void 0 && e.push(g(t));
            return e;
        }
        static _$Ep(t, e) {
            const s = e.attribute;
            return s === !1
                ? void 0
                : typeof s == 'string'
                ? s
                : typeof t == 'string'
                ? t.toLowerCase()
                : void 0;
        }
        u() {
            var t;
            (this._$E_ = new Promise((e) => (this.enableUpdating = e))),
                (this._$AL = new Map()),
                this._$Eg(),
                this.requestUpdate(),
                (t = this.constructor.h) === null || t === void 0 || t.forEach((e) => e(this));
        }
        addController(t) {
            var e, s;
            ((e = this._$ES) !== null && e !== void 0 ? e : (this._$ES = [])).push(t),
                this.renderRoot !== void 0 &&
                    this.isConnected &&
                    ((s = t.hostConnected) === null || s === void 0 || s.call(t));
        }
        removeController(t) {
            var e;
            (e = this._$ES) === null || e === void 0 || e.splice(this._$ES.indexOf(t) >>> 0, 1);
        }
        _$Eg() {
            this.constructor.elementProperties.forEach((t, e) => {
                this.hasOwnProperty(e) && (this._$Ei.set(e, this[e]), delete this[e]);
            });
        }
        createRenderRoot() {
            var t;
            const e =
                (t = this.shadowRoot) !== null && t !== void 0
                    ? t
                    : this.attachShadow(this.constructor.shadowRootOptions);
            return I(e, this.constructor.elementStyles), e;
        }
        connectedCallback() {
            var t;
            this.renderRoot === void 0 && (this.renderRoot = this.createRenderRoot()),
                this.enableUpdating(!0),
                (t = this._$ES) === null ||
                    t === void 0 ||
                    t.forEach((e) => {
                        var s;
                        return (s = e.hostConnected) === null || s === void 0 ? void 0 : s.call(e);
                    });
        }
        enableUpdating(t) {}
        disconnectedCallback() {
            var t;
            (t = this._$ES) === null ||
                t === void 0 ||
                t.forEach((e) => {
                    var s;
                    return (s = e.hostDisconnected) === null || s === void 0 ? void 0 : s.call(e);
                });
        }
        attributeChangedCallback(t, e, s) {
            this._$AK(t, s);
        }
        _$EO(t, e, s = Z) {
            var o;
            const i = this.constructor._$Ep(t, s);
            if (i !== void 0 && s.reflect === !0) {
                const n = (((o = s.converter) === null || o === void 0 ? void 0 : o.toAttribute) !==
                void 0
                    ? s.converter
                    : G
                ).toAttribute(e, s.type);
                (this._$El = t),
                    n == null ? this.removeAttribute(i) : this.setAttribute(i, n),
                    (this._$El = null);
            }
        }
        _$AK(t, e) {
            var s;
            const o = this.constructor,
                i = o._$Ev.get(t);
            if (i !== void 0 && this._$El !== i) {
                const n = o.getPropertyOptions(i),
                    c =
                        typeof n.converter == 'function'
                            ? { fromAttribute: n.converter }
                            : ((s = n.converter) === null || s === void 0
                                  ? void 0
                                  : s.fromAttribute) !== void 0
                            ? n.converter
                            : G;
                (this._$El = i), (this[i] = c.fromAttribute(e, n.type)), (this._$El = null);
            }
        }
        requestUpdate(t, e, s) {
            let o = !0;
            t !== void 0 &&
                (((s = s || this.constructor.getPropertyOptions(t)).hasChanged || st)(this[t], e)
                    ? (this._$AL.has(t) || this._$AL.set(t, e),
                      s.reflect === !0 &&
                          this._$El !== t &&
                          (this._$EC === void 0 && (this._$EC = new Map()), this._$EC.set(t, s)))
                    : (o = !1)),
                !this.isUpdatePending && o && (this._$E_ = this._$Ej());
        }
        _$Ej() {
            return St(this, null, function* () {
                this.isUpdatePending = !0;
                try {
                    yield this._$E_;
                } catch (e) {
                    Promise.reject(e);
                }
                const t = this.scheduleUpdate();
                return t != null && (yield t), !this.isUpdatePending;
            });
        }
        scheduleUpdate() {
            return this.performUpdate();
        }
        performUpdate() {
            var t;
            if (!this.isUpdatePending) return;
            this.hasUpdated,
                this._$Ei && (this._$Ei.forEach((o, i) => (this[i] = o)), (this._$Ei = void 0));
            let e = !1;
            const s = this._$AL;
            try {
                (e = this.shouldUpdate(s)),
                    e
                        ? (this.willUpdate(s),
                          (t = this._$ES) === null ||
                              t === void 0 ||
                              t.forEach((o) => {
                                  var i;
                                  return (i = o.hostUpdate) === null || i === void 0
                                      ? void 0
                                      : i.call(o);
                              }),
                          this.update(s))
                        : this._$Ek();
            } catch (o) {
                throw ((e = !1), this._$Ek(), o);
            }
            e && this._$AE(s);
        }
        willUpdate(t) {}
        _$AE(t) {
            var e;
            (e = this._$ES) === null ||
                e === void 0 ||
                e.forEach((s) => {
                    var o;
                    return (o = s.hostUpdated) === null || o === void 0 ? void 0 : o.call(s);
                }),
                this.hasUpdated || ((this.hasUpdated = !0), this.firstUpdated(t)),
                this.updated(t);
        }
        _$Ek() {
            (this._$AL = new Map()), (this.isUpdatePending = !1);
        }
        get updateComplete() {
            return this.getUpdateComplete();
        }
        getUpdateComplete() {
            return this._$E_;
        }
        shouldUpdate(t) {
            return !0;
        }
        update(t) {
            this._$EC !== void 0 &&
                (this._$EC.forEach((e, s) => this._$EO(s, this[s], e)), (this._$EC = void 0)),
                this._$Ek();
        }
        updated(t) {}
        firstUpdated(t) {}
    }
    (x.finalized = !0),
        (x.elementProperties = new Map()),
        (x.elementStyles = []),
        (x.shadowRootOptions = { mode: 'open' }),
        et == null || et({ ReactiveElement: x }),
        ((y = N.reactiveElementVersions) !== null && y !== void 0
            ? y
            : (N.reactiveElementVersions = [])
        ).push('1.4.1');
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var W;
    const R = window,
        A = R.trustedTypes,
        rt = A ? A.createPolicy('lit-html', { createHTML: (r) => r }) : void 0,
        _ = `lit$${(Math.random() + '').slice(9)}$`,
        ot = '?' + _,
        Et = `<${ot}>`,
        S = document,
        O = (r = '') => S.createComment(r),
        T = (r) => r === null || (typeof r != 'object' && typeof r != 'function'),
        nt = Array.isArray,
        Pt = (r) => nt(r) || typeof (r == null ? void 0 : r[Symbol.iterator]) == 'function',
        B = /<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g,
        it = /-->/g,
        at = />/g,
        $ = RegExp(
            `>|[ 	
\f\r](?:([^\\s"'>=/]+)([ 	
\f\r]*=[ 	
\f\r]*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`,
            'g'
        ),
        lt = /'/g,
        ht = /"/g,
        ct = /^(?:script|style|textarea|title)$/i,
        kt = (r) => (t, ...e) => ({ _$litType$: r, strings: t, values: e }),
        J = kt(1),
        w = Symbol.for('lit-noChange'),
        b = Symbol.for('lit-nothing'),
        ut = new WeakMap(),
        Ct = (r, t, e) => {
            var s, o;
            const i = (s = e == null ? void 0 : e.renderBefore) !== null && s !== void 0 ? s : t;
            let n = i._$litPart$;
            if (n === void 0) {
                const c =
                    (o = e == null ? void 0 : e.renderBefore) !== null && o !== void 0 ? o : null;
                i._$litPart$ = n = new q(t.insertBefore(O(), c), c, void 0, e != null ? e : {});
            }
            return n._$AI(r), n;
        },
        E = S.createTreeWalker(S, 129, null, !1),
        It = (r, t) => {
            const e = r.length - 1,
                s = [];
            let o,
                i = t === 2 ? '<svg>' : '',
                n = B;
            for (let a = 0; a < e; a++) {
                const l = r[a];
                let f,
                    h,
                    p = -1,
                    m = 0;
                for (; m < l.length && ((n.lastIndex = m), (h = n.exec(l)), h !== null); )
                    (m = n.lastIndex),
                        n === B
                            ? h[1] === '!--'
                                ? (n = it)
                                : h[1] !== void 0
                                ? (n = at)
                                : h[2] !== void 0
                                ? (ct.test(h[2]) && (o = RegExp('</' + h[2], 'g')), (n = $))
                                : h[3] !== void 0 && (n = $)
                            : n === $
                            ? h[0] === '>'
                                ? ((n = o != null ? o : B), (p = -1))
                                : h[1] === void 0
                                ? (p = -2)
                                : ((p = n.lastIndex - h[2].length),
                                  (f = h[1]),
                                  (n = h[3] === void 0 ? $ : h[3] === '"' ? ht : lt))
                            : n === ht || n === lt
                            ? (n = $)
                            : n === it || n === at
                            ? (n = B)
                            : ((n = $), (o = void 0));
                const j = n === $ && r[a + 1].startsWith('/>') ? ' ' : '';
                i +=
                    n === B
                        ? l + Et
                        : p >= 0
                        ? (s.push(f), l.slice(0, p) + '$lit$' + l.slice(p) + _ + j)
                        : l + _ + (p === -2 ? (s.push(void 0), a) : j);
            }
            const c = i + (r[e] || '<?>') + (t === 2 ? '</svg>' : '');
            if (!Array.isArray(r) || !r.hasOwnProperty('raw'))
                throw Error('invalid template strings array');
            return [rt !== void 0 ? rt.createHTML(c) : c, s];
        };
    class U {
        constructor({ strings: t, _$litType$: e }, s) {
            let o;
            this.parts = [];
            let i = 0,
                n = 0;
            const c = t.length - 1,
                a = this.parts,
                [l, f] = It(t, e);
            if (((this.el = U.createElement(l, s)), (E.currentNode = this.el.content), e === 2)) {
                const h = this.el.content,
                    p = h.firstChild;
                p.remove(), h.append(...p.childNodes);
            }
            for (; (o = E.nextNode()) !== null && a.length < c; ) {
                if (o.nodeType === 1) {
                    if (o.hasAttributes()) {
                        const h = [];
                        for (const p of o.getAttributeNames())
                            if (p.endsWith('$lit$') || p.startsWith(_)) {
                                const m = f[n++];
                                if ((h.push(p), m !== void 0)) {
                                    const j = o.getAttribute(m.toLowerCase() + '$lit$').split(_),
                                        Q = /([.?@])?(.*)/.exec(m);
                                    a.push({
                                        type: 1,
                                        index: i,
                                        name: Q[2],
                                        strings: j,
                                        ctor:
                                            Q[1] === '.'
                                                ? Tt
                                                : Q[1] === '?'
                                                ? Ut
                                                : Q[1] === '@'
                                                ? qt
                                                : M
                                    });
                                } else a.push({ type: 6, index: i });
                            }
                        for (const p of h) o.removeAttribute(p);
                    }
                    if (ct.test(o.tagName)) {
                        const h = o.textContent.split(_),
                            p = h.length - 1;
                        if (p > 0) {
                            o.textContent = A ? A.emptyScript : '';
                            for (let m = 0; m < p; m++)
                                o.append(h[m], O()), E.nextNode(), a.push({ type: 2, index: ++i });
                            o.append(h[p], O());
                        }
                    }
                } else if (o.nodeType === 8)
                    if (o.data === ot) a.push({ type: 2, index: i });
                    else {
                        let h = -1;
                        for (; (h = o.data.indexOf(_, h + 1)) !== -1; )
                            a.push({ type: 7, index: i }), (h += _.length - 1);
                    }
                i++;
            }
        }
        static createElement(t, e) {
            const s = S.createElement('template');
            return (s.innerHTML = t), s;
        }
    }
    function P(r, t, e = r, s) {
        var o, i, n, c;
        if (t === w) return t;
        let a = s !== void 0 ? ((o = e._$Cl) === null || o === void 0 ? void 0 : o[s]) : e._$Cu;
        const l = T(t) ? void 0 : t._$litDirective$;
        return (
            (a == null ? void 0 : a.constructor) !== l &&
                ((i = a == null ? void 0 : a._$AO) === null || i === void 0 || i.call(a, !1),
                l === void 0 ? (a = void 0) : ((a = new l(r)), a._$AT(r, e, s)),
                s !== void 0
                    ? (((n = (c = e)._$Cl) !== null && n !== void 0 ? n : (c._$Cl = []))[s] = a)
                    : (e._$Cu = a)),
            a !== void 0 && (t = P(r, a._$AS(r, t.values), a, s)),
            t
        );
    }
    class Ot {
        constructor(t, e) {
            (this.v = []), (this._$AN = void 0), (this._$AD = t), (this._$AM = e);
        }
        get parentNode() {
            return this._$AM.parentNode;
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        p(t) {
            var e;
            const {
                    el: { content: s },
                    parts: o
                } = this._$AD,
                i = ((e = t == null ? void 0 : t.creationScope) !== null && e !== void 0
                    ? e
                    : S
                ).importNode(s, !0);
            E.currentNode = i;
            let n = E.nextNode(),
                c = 0,
                a = 0,
                l = o[0];
            for (; l !== void 0; ) {
                if (c === l.index) {
                    let f;
                    l.type === 2
                        ? (f = new q(n, n.nextSibling, this, t))
                        : l.type === 1
                        ? (f = new l.ctor(n, l.name, l.strings, this, t))
                        : l.type === 6 && (f = new Lt(n, this, t)),
                        this.v.push(f),
                        (l = o[++a]);
                }
                c !== (l == null ? void 0 : l.index) && ((n = E.nextNode()), c++);
            }
            return i;
        }
        m(t) {
            let e = 0;
            for (const s of this.v)
                s !== void 0 &&
                    (s.strings !== void 0
                        ? (s._$AI(t, s, e), (e += s.strings.length - 2))
                        : s._$AI(t[e])),
                    e++;
        }
    }
    class q {
        constructor(t, e, s, o) {
            var i;
            (this.type = 2),
                (this._$AH = b),
                (this._$AN = void 0),
                (this._$AA = t),
                (this._$AB = e),
                (this._$AM = s),
                (this.options = o),
                (this._$C_ =
                    (i = o == null ? void 0 : o.isConnected) === null || i === void 0 || i);
        }
        get _$AU() {
            var t, e;
            return (e = (t = this._$AM) === null || t === void 0 ? void 0 : t._$AU) !== null &&
                e !== void 0
                ? e
                : this._$C_;
        }
        get parentNode() {
            let t = this._$AA.parentNode;
            const e = this._$AM;
            return e !== void 0 && t.nodeType === 11 && (t = e.parentNode), t;
        }
        get startNode() {
            return this._$AA;
        }
        get endNode() {
            return this._$AB;
        }
        _$AI(t, e = this) {
            (t = P(this, t, e)),
                T(t)
                    ? t === b || t == null || t === ''
                        ? (this._$AH !== b && this._$AR(), (this._$AH = b))
                        : t !== this._$AH && t !== w && this.$(t)
                    : t._$litType$ !== void 0
                    ? this.T(t)
                    : t.nodeType !== void 0
                    ? this.k(t)
                    : Pt(t)
                    ? this.O(t)
                    : this.$(t);
        }
        S(t, e = this._$AB) {
            return this._$AA.parentNode.insertBefore(t, e);
        }
        k(t) {
            this._$AH !== t && (this._$AR(), (this._$AH = this.S(t)));
        }
        $(t) {
            this._$AH !== b && T(this._$AH)
                ? (this._$AA.nextSibling.data = t)
                : this.k(S.createTextNode(t)),
                (this._$AH = t);
        }
        T(t) {
            var e;
            const { values: s, _$litType$: o } = t,
                i =
                    typeof o == 'number'
                        ? this._$AC(t)
                        : (o.el === void 0 && (o.el = U.createElement(o.h, this.options)), o);
            if (((e = this._$AH) === null || e === void 0 ? void 0 : e._$AD) === i) this._$AH.m(s);
            else {
                const n = new Ot(i, this),
                    c = n.p(this.options);
                n.m(s), this.k(c), (this._$AH = n);
            }
        }
        _$AC(t) {
            let e = ut.get(t.strings);
            return e === void 0 && ut.set(t.strings, (e = new U(t))), e;
        }
        O(t) {
            nt(this._$AH) || ((this._$AH = []), this._$AR());
            const e = this._$AH;
            let s,
                o = 0;
            for (const i of t)
                o === e.length
                    ? e.push((s = new q(this.S(O()), this.S(O()), this, this.options)))
                    : (s = e[o]),
                    s._$AI(i),
                    o++;
            o < e.length && (this._$AR(s && s._$AB.nextSibling, o), (e.length = o));
        }
        _$AR(t = this._$AA.nextSibling, e) {
            var s;
            for (
                (s = this._$AP) === null || s === void 0 || s.call(this, !1, !0, e);
                t && t !== this._$AB;

            ) {
                const o = t.nextSibling;
                t.remove(), (t = o);
            }
        }
        setConnected(t) {
            var e;
            this._$AM === void 0 &&
                ((this._$C_ = t), (e = this._$AP) === null || e === void 0 || e.call(this, t));
        }
    }
    class M {
        constructor(t, e, s, o, i) {
            (this.type = 1),
                (this._$AH = b),
                (this._$AN = void 0),
                (this.element = t),
                (this.name = e),
                (this._$AM = o),
                (this.options = i),
                s.length > 2 || s[0] !== '' || s[1] !== ''
                    ? ((this._$AH = Array(s.length - 1).fill(new String())), (this.strings = s))
                    : (this._$AH = b);
        }
        get tagName() {
            return this.element.tagName;
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        _$AI(t, e = this, s, o) {
            const i = this.strings;
            let n = !1;
            if (i === void 0)
                (t = P(this, t, e, 0)),
                    (n = !T(t) || (t !== this._$AH && t !== w)),
                    n && (this._$AH = t);
            else {
                const c = t;
                let a, l;
                for (t = i[0], a = 0; a < i.length - 1; a++)
                    (l = P(this, c[s + a], e, a)),
                        l === w && (l = this._$AH[a]),
                        n || (n = !T(l) || l !== this._$AH[a]),
                        l === b ? (t = b) : t !== b && (t += (l != null ? l : '') + i[a + 1]),
                        (this._$AH[a] = l);
            }
            n && !o && this.P(t);
        }
        P(t) {
            t === b
                ? this.element.removeAttribute(this.name)
                : this.element.setAttribute(this.name, t != null ? t : '');
        }
    }
    class Tt extends M {
        constructor() {
            super(...arguments), (this.type = 3);
        }
        P(t) {
            this.element[this.name] = t === b ? void 0 : t;
        }
    }
    const Bt = A ? A.emptyScript : '';
    class Ut extends M {
        constructor() {
            super(...arguments), (this.type = 4);
        }
        P(t) {
            t && t !== b
                ? this.element.setAttribute(this.name, Bt)
                : this.element.removeAttribute(this.name);
        }
    }
    class qt extends M {
        constructor(t, e, s, o, i) {
            super(t, e, s, o, i), (this.type = 5);
        }
        _$AI(t, e = this) {
            var s;
            if ((t = (s = P(this, t, e, 0)) !== null && s !== void 0 ? s : b) === w) return;
            const o = this._$AH,
                i =
                    (t === b && o !== b) ||
                    t.capture !== o.capture ||
                    t.once !== o.once ||
                    t.passive !== o.passive,
                n = t !== b && (o === b || i);
            i && this.element.removeEventListener(this.name, this, o),
                n && this.element.addEventListener(this.name, this, t),
                (this._$AH = t);
        }
        handleEvent(t) {
            var e, s;
            typeof this._$AH == 'function'
                ? this._$AH.call(
                      (s = (e = this.options) === null || e === void 0 ? void 0 : e.host) !==
                          null && s !== void 0
                          ? s
                          : this.element,
                      t
                  )
                : this._$AH.handleEvent(t);
        }
    }
    class Lt {
        constructor(t, e, s) {
            (this.element = t),
                (this.type = 6),
                (this._$AN = void 0),
                (this._$AM = e),
                (this.options = s);
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        _$AI(t) {
            P(this, t);
        }
    }
    const dt = R.litHtmlPolyfillSupport;
    dt == null || dt(U, q),
        ((W = R.litHtmlVersions) !== null && W !== void 0 ? W : (R.litHtmlVersions = [])).push(
            '2.3.1'
        );
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var Y, K;
    class k extends x {
        constructor() {
            super(...arguments), (this.renderOptions = { host: this }), (this._$Do = void 0);
        }
        createRenderRoot() {
            var t, e;
            const s = super.createRenderRoot();
            return (
                ((t = (e = this.renderOptions).renderBefore) !== null && t !== void 0) ||
                    (e.renderBefore = s.firstChild),
                s
            );
        }
        update(t) {
            const e = this.render();
            this.hasUpdated || (this.renderOptions.isConnected = this.isConnected),
                super.update(t),
                (this._$Do = Ct(e, this.renderRoot, this.renderOptions));
        }
        connectedCallback() {
            var t;
            super.connectedCallback(),
                (t = this._$Do) === null || t === void 0 || t.setConnected(!0);
        }
        disconnectedCallback() {
            var t;
            super.disconnectedCallback(),
                (t = this._$Do) === null || t === void 0 || t.setConnected(!1);
        }
        render() {
            return w;
        }
    }
    (k.finalized = !0),
        (k._$litElement$ = !0),
        (Y = globalThis.litElementHydrateSupport) === null ||
            Y === void 0 ||
            Y.call(globalThis, { LitElement: k });
    const pt = globalThis.litElementPolyfillSupport;
    pt == null || pt({ LitElement: k }),
        ((K = globalThis.litElementVersions) !== null && K !== void 0
            ? K
            : (globalThis.litElementVersions = [])
        ).push('3.2.2');
    function Nt(r, t, e) {
        arguments.length === 2 && (e = !0);
        let s = r;
        const o = (t || '').split('/');
        for (const i of o) {
            if (!Object.prototype.hasOwnProperty.call(s, i)) {
                if (!e) return;
                s[i] = {};
            }
            s = s[i];
        }
        return s;
    }
    function Rt(r, t, e) {
        let s = r;
        if (t === '') throw new Error('Path cannot be empty');
        const o = (t || '').split('/'),
            i = o.length;
        return (
            o.forEach((n, c) => {
                Object.prototype.hasOwnProperty.call(s, n) || (s[n] = {}),
                    c === i - 1 && (s[n] = e),
                    (s = s[n]);
            }),
            s
        );
    }
    function vt(r, t) {
        let e = r;
        const s = (t || '').split('/'),
            o = s.length;
        s.forEach((i, n) => {
            !Object.prototype.hasOwnProperty.call(e, i) || (n === o - 1 ? delete e[i] : (e = e[i]));
        });
    }
    function Mt(r, t) {
        if (typeof t != 'string' || t.length === 0) return null;
        const e = t.split('/');
        let s = '',
            o = r;
        for (let i = 0; i < e.length; i++) {
            const n = e[i];
            if (!Object.prototype.hasOwnProperty.call(o, n))
                return s + (s.length === 0 ? '' : '/') + n;
            (o = o[n]), (s += (i === 0 ? '' : '/') + n);
        }
        return t === s ? null : s;
    }
    const bt = 'surveyTriggerButton';
    function Ht(r) {
        const { interceptUrl: t, globalObj: e, surveyLaunchMethod: s } = r;
        return new Promise((o, i) => {
            if (!t) {
                i(new Error('No intercept URL was provided. This is a mandatory parameter.'));
                return;
            }
            try {
                if (!e.document || !e.document.body)
                    throw new Error(
                        'Cannot inject elements in the document: document.body is not available.'
                    );
                let n = null;
                switch (s) {
                    case 'invisibleButton':
                        if (document.getElementById(bt))
                            throw new Error(
                                `An element with id ${bt} already exists in this page. The survey won't be launched unless the existing button is removed or its id is renamed.`
                            );
                        n = Qt();
                        break;
                    case 'qsiApi':
                        break;
                    default:
                        throw new Error(`Unsupported launch method: ${s}.`);
                }
                if (mt(e))
                    e.QSI.API.load().then(() => {
                        const c = ft(r, n, null);
                        o(c);
                    });
                else {
                    Ft(t, e);
                    const c = () => {
                        const a = ft(r, n, c);
                        o(a);
                    };
                    e.addEventListener('qsi_js_loaded', c, !1);
                }
            } catch (n) {
                i(n);
            }
        });
    }
    function ft(r, t, e) {
        const s = r.globalObj;
        if (!s.QSI.API)
            throw new Error(
                'Cannot create api context: QSI.API is not available in the global scope'
            );
        return {
            QSI: s.QSI,
            setParameters: new Set([]),
            globalObj: s,
            destroyed: !1,
            firstNonExistingRoot: Mt(s, r.contextRootPath),
            contextRootPath: r.contextRootPath,
            contextParamPaths: jt(r.contextParamPaths, r.customContextParamPaths),
            surveyLaunchMethod: r.surveyLaunchMethod,
            apiLoadedListener: e,
            invisibleButtonElement: t
        };
    }
    function mt(r) {
        return !!(r.QSI && r.QSI.API && typeof r.QSI.API.unload == 'function');
    }
    function jt(r, t) {
        return Object.keys(r || {}).reduce(
            (e, s) => ((e[s] = r[s]), e),
            JSON.parse(JSON.stringify(t || {}))
        );
    }
    function Qt() {
        const r = document.createElement('button');
        return (
            r.setAttribute('id', 'surveyTriggerButton'),
            (r.style.display = 'none'),
            document.body.appendChild(r),
            r
        );
    }
    function Ft(r, t) {
        const e = t.document.createElement('script');
        (e.type = 'text/javascript'), (e.src = r), t.document.body.appendChild(e);
    }
    function zt(r, t) {
        const e = Nt(r.globalObj, r.contextRootPath);
        Object.keys(t).forEach((s) => {
            const o = r.contextParamPaths[s] || s;
            Rt(e, o, t[s]), r.setParameters.add(s);
        });
    }
    function Vt(r) {
        if (r.setParameters.size === 0)
            throw new Error(
                'Cannot start a survey without context parameters. Call setContextParameters first.'
            );
        if (r.surveyLaunchMethod === 'invisibleButton') {
            r.invisibleButtonElement && r.invisibleButtonElement.dispatchEvent(new Event('click'));
            return;
        }
        r.QSI.API.unload(), r.QSI.API.load().then(r.QSI.API.run.bind(r.QSI.API));
    }
    function Dt(r) {
        mt(r) && r.QSI.API.unload(),
            r.apiLoadedListener &&
                r.globalObj.removeEventListener('qsi_js_loaded', r.apiLoadedListener);
        let t = r.contextRootPath;
        r.firstNonExistingRoot
            ? vt(r.globalObj, r.firstNonExistingRoot)
            : r.setParameters.forEach((e) => {
                  const s = r.contextParamPaths[e];
                  s && (t = `${t}/${s}`), vt(r.globalObj, t);
              }),
            r.invisibleButtonElement && r.invisibleButtonElement.remove(),
            (r.destroyed = !0);
    }
    const H = { init: Ht, destroy: Dt, setContextParameters: zt, startSurvey: Vt },
        gt = 'cx-qtx-sbtn-timestamp',
        _t = 'cx-qtx-sbtn-localstorage-test';
    function Gt(r) {
        if (!!r.enabled)
            try {
                r.globalObj.localStorage.setItem(gt, +new Date());
            } catch (t) {
                return;
            }
    }
    function Zt(r) {
        try {
            return r.globalObj.localStorage.getItem(gt);
        } catch (t) {
            return null;
        }
    }
    function Wt(r) {
        var s;
        if (typeof r._IS_LOCAL_STORAGE_AVAILABLE_CACHE == 'boolean')
            return r._IS_LOCAL_STORAGE_AVAILABLE_CACHE;
        const t = (s = r == null ? void 0 : r.globalObj) == null ? void 0 : s.localStorage;
        let e;
        try {
            t.setItem(_t, 'true'), t.removeItem(_t), (e = !0);
        } catch (o) {
            e = !1;
        }
        return (r._IS_LOCAL_STORAGE_AVAILABLE_CACHE = e), e;
    }
    function Jt(r) {
        return new Promise((t, e) => {
            var h;
            if (!r.enabled) {
                t(!1);
                return;
            }
            if (!Wt(r)) {
                t(!1);
                return;
            }
            const s = (h = r == null ? void 0 : r.range) == null ? void 0 : h.type;
            if (s !== 'months') {
                e(new Error(`Invalid range type configured: ${s}`));
                return;
            }
            const o = new Date().getMonth() + 1,
                n = Yt(r).indexOf(o) >= 0,
                c = Zt(r);
            if (!c) {
                t(n);
                return;
            }
            const a = parseInt(c, 10),
                f = new Date(a).getMonth() + 1;
            t(n && o !== f);
        });
    }
    function Yt(r) {
        var t, e;
        return ((t = r == null ? void 0 : r.range) == null ? void 0 : t.type) === 'months'
            ? (e = r == null ? void 0 : r.range) == null
                ? void 0
                : e.value.map((s) => window.parseInt(s, 10))
            : [];
    }
    const yt = { saveUserInteractionTime: Gt, determineNotificationStatus: Jt };
    class $t extends k {
        constructor() {
            super(),
                (this._version = '2.0.1'),
                (this.apiContext = null),
                (this.notifyUser = null),
                (this.surveyLaunchMethod = 'invisibleButton'),
                (this.contextParams = {}),
                (this.fiori3Compatible = !1),
                (this.interceptUrl = ''),
                (this.contextRootPath = 'sap/qtx'),
                (this.notificationConfig = {
                    enabled: !0,
                    range: { type: 'months', value: [2, 5, 8, 11] }
                }),
                (this.title = 'Give Feedback'),
                (this.ariaLabel = 'Give Feedback'),
                (this.contextParamPaths = {
                    Q_Language: 'appcontext/languageTag',
                    language: 'appcontext/languageTag',
                    appFrameworkVersion: 'appcontext/appFrameworkVersion',
                    theme: 'appcontext/theme',
                    appId: 'appcontext/appId',
                    appVersion: 'appcontext/appVersion',
                    technicalAppComponentId: 'appcontext/technicalAppComponentId',
                    appTitle: 'appcontext/appTitle',
                    appSupportInfo: 'appcontext/appSupportInfo',
                    tenantId: 'session/tenantId',
                    tenantRole: 'session/tenantRole',
                    appFrameworkId: 'appcontext/appFrameworkId',
                    productName: 'session/productName',
                    platformType: 'session/platformType',
                    pushSrcType: 'push/pushSrcType',
                    pushSrcAppId: 'push/pushSrcAppId',
                    pushSrcTrigger: 'push/pushSrcTrigger',
                    clientAction: 'appcontext/clientAction',
                    previousTheme: 'appcontext/previousTheme',
                    followUpCount: 'appcontext/followUpCount',
                    deviceType: 'device/type',
                    orientation: 'device/orientation',
                    osName: 'device/osName',
                    browserName: 'device/browserName'
                }),
                (this.customContextParamPaths = {});
        }
        static get properties() {
            return {
                title: { type: String, attribute: 'title', reflect: !1 },
                ariaLabel: { type: String, attribute: 'aria-label', reflect: !1 },
                surveyLaunchMethod: {
                    type: String,
                    attribute: 'survey-launch-method',
                    reflect: !1
                },
                notifyUser: { type: Boolean, attribute: 'notify-user', reflect: !0 },
                notificationConfig: { type: Object, attribute: 'notification-config', reflect: !0 },
                interceptUrl: { type: String, attribute: 'intercept-url', reflect: !0 },
                customContextParamPaths: {
                    type: Object,
                    attribute: 'custom-context-param-paths',
                    reflect: !0
                },
                contextParamPaths: { type: Object, attribute: 'context-param-paths', reflect: !0 },
                contextRootPath: { type: String, attribute: 'context-root-path', reflect: !0 },
                contextParams: { type: Object, attribute: 'context-params', reflect: !0 },
                fiori3Compatible: { type: Boolean, attribute: 'fiori3-compatible', reflect: !0 }
            };
        }
        _onClick() {
            this._turnOffNotification(),
                yt.saveUserInteractionTime(
                    z(F({}, this.notificationConfig), { globalObj: window })
                ),
                this._runSurvey();
        }
        _turnOffNotification() {
            (this.notifyUser = !1), this.requestUpdate();
        }
        _runSurvey() {
            this._getAPIContext().then(
                (t) => {
                    const e = this.contextParams;
                    (this.apiContext = t),
                        H.setContextParameters(this.apiContext, e),
                        H.startSurvey(this.apiContext);
                },
                (t) => {
                    console.log(t);
                }
            );
        }
        _getAPIContext() {
            return this.apiContext
                ? Promise.resolve(this.apiContext)
                : H.init({
                      interceptUrl: this.interceptUrl,
                      globalObj: window,
                      contextRootPath: this.contextRootPath,
                      contextParamPaths: this.contextParamPaths,
                      customContextParamPaths: this.customContextParamPaths,
                      surveyLaunchMethod: this.surveyLaunchMethod
                  });
        }
        setContextParams(t) {
            this.contextParams = JSON.parse(JSON.stringify(t));
        }
        updateContextParam(t, e) {
            this.contextParams[t] = e;
        }
        connectedCallback() {
            super.connectedCallback(),
                this.addEventListener('click', this._onClick),
                this.setAttribute('aria-hidden', 'true'),
                yt
                    .determineNotificationStatus(
                        z(F({}, this.notificationConfig), { globalObj: window })
                    )
                    .then(
                        (t) => {
                            this.notifyUser === null &&
                                ((this.notifyUser = t), this.requestUpdate());
                        },
                        (t) => {
                            console.log(t);
                        }
                    );
        }
        disconnectedCallback() {
            super.disconnectedCallback(),
                this.removeEventListener('click', this._onClick),
                this.apiContext && (H.destroy(this.apiContext), (this.apiContext = null));
        }
        render() {
            return J`
            <slot>
                <button
                    class="qtxSurveyFallbackButton"
                    title="${this.title}"
                    aria-label="${this.ariaLabel}"
                >
                </button>
            </slot>
        `;
        }
        static get styles() {
            return D`/*
 * Prevent focus effect on the host, the button only should be focussed.
 */
:host(:focus) {
    outline: none;
    box-shadow: none;
}
:host(:focus) .qtxSurveyFallbackButton {
    background-color: initial;
}
.qtxSurveyFallbackButton {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    width: var(--qtxSurveyButton_Size, 2rem);
    height: var(--qtxSurveyButton_Size, 2rem);
    outline: none;
    shape-rendering: geometricprecision;
    /* Fiori Next buttons have transitions */
    -webkit-transition: 0.3s ease-in-out;
    transition: 0.3s ease-in-out;
    font-weight: 400;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin: 0;
    border: 0;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
    position: relative;
    padding: 6px;
    -webkit-box-shadow: none;
    box-shadow: none;
    border-radius: var(--sapButton_BorderCornerRadius, 0.5rem);
    background: 0 0;
}
.qtxSurveyFallbackButton.is-focus,
.qtxSurveyFallbackButton:focus {
    z-index: 5;
    background: var(--sapShell_Hover_Background, transparent);
    outline: 0;
    border-color: #fff;
    -webkit-box-shadow: 0 0 2px rgba(27, 144, 255, 0.16),
        0 8px 16px rgba(27, 144, 255, 0.16), 0 0 0 0.125rem #1b90ff,
        inset 0 0 0 0.125rem #fff;
    box-shadow: 0 0 2px rgba(27, 144, 255, 0.16),
        0 8px 16px rgba(27, 144, 255, 0.16), 0 0 0 0.125rem #1b90ff,
        inset 0 0 0 0.125rem #fff;
}
:host([fiori3-compatible]) .qtxSurveyFallbackButton {
    /*
     * Fiori 3: use correct sizing
     */
    width: var(--qtxSurveyButton_Size, 2.25rem);
    height: var(--qtxSurveyButton_Size, 2.25rem);
    /*
     * Fiori 3: disable Fiori Next transitions + box shadow
     */
    -webkit-transition: none;
    transition: none;
    box-shadow: none;
    -webkit-box-shadow: none;
}
:host([fiori3-compatible]):host(:hover) .qtxSurveyFallbackButton {
    box-shadow: none;
    -webkit-box-shadow: none;
}
:host([fiori3-compatible]):host(:active) .qtxSurveyFallbackButton {
    box-shadow: none;
    -webkit-box-shadow: none;
}
/*
 * Fiori3: create visible focus area
 */
:host([fiori3-compatible]):host(:focus) .qtxSurveyFallbackButton:after {
    content: "";
    position: absolute;
    width: auto;
    height: auto;
    top: 0.0625rem;
    left: 0.0625rem;
    right: 0.0625rem;
    bottom: 0.0625rem;
    border: var(--sapContent_FocusWidth, 0.0625rem) dotted
        var(--sapContent_ContrastFocusColor, #fff);
    border-radius: 4px;
}
:host(:hover) .qtxSurveyFallbackButton {
    background-color: var(--sapShell_Hover_Background, transparent);
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 8px 16px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 8px 16px rgba(131, 150, 168, 0.16);
}
:host(:hover) path {
    stroke: var(--sapShell_Active_TextColor, #475e75);
}
:host(:hover) .eye,
:host(:hover) .foregroundIcon {
    fill: var(--sapShell_Active_TextColor, #475e75);
    stroke: transparent;
}
:host(:active) .qtxSurveyFallbackButton {
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    background: var(--sapShell_Active_Background, #fff);
}
:host([notify-user]) .smiley,
:host([notify-user]) .backgroundCircle {
    fill: var(
        --qtxSurveyButton_NotificationColor,
        #64edd2
    ); /* Teal 2 */
}
:host([notify-user][fiori3-compatible]) .smiley,
:host([notify-user][fiori3-compatible]) .backgroundCircle {
    fill: var(
        --qtxSurveyButton_NotificationColor,
        var(--sapIndicationColor_6, #0f828f)
    );
}
:host([fiori3-compatible]) .pathGroup {
    transform: scale(var(--qtxSurveyButton_IconScale, 1))
        translate(0, var(--qtxSurveyButton_IconOffsetY, 0));
}
.pathGroup {
    transform: scale(var(--qtxSurveyButton_IconScale, 1))
        translate(0, var(--qtxSurveyButton_IconOffsetY, 0));
    transform-origin: center;
    fill: none;
}
path {
    stroke: var(--sapShell_InteractiveTextColor, #5b738b);
    stroke-width: 2px;
    vector-effect: non-scaling-stroke;
}
.eye,
.foregroundIcon {
    fill: var(--sapShell_InteractiveTextColor, #5b738b);
    stroke: transparent;
}
.qtxSurveyFallbackButton::after,
.qtxSurveyFallbackButton::before {
    -webkit-box-sizing: inherit;
    box-sizing: inherit;
    font-size: inherit;
}
.qtxSurveyFallbackButton.is-pressed,
.qtxSurveyFallbackButton[aria-pressed="true"] {
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    background: var(--sapShell_Active_Background, #fff);
}
`;
        }
    }
    At(
        $t,
        'shadowRootOptions',
        z(F({}, k.shadowRootOptions), { mode: 'closed', delegatesFocus: 'true' })
    ),
        window.customElements.define('cx-qtx-survey-button', $t);
})();

window.__smartedit__.addDecoratorPayload("Component", "QualtricsComponent", {
    selector: 'se-qualtrics',
    template: `
        <cx-qtx-survey-button
            fiori3-compatible="true"
            [attr.aria-hidden]="false"
            attr.aria-label="{{ 'se.toolbar.qualtrics.button.arialabel' | translate }}"
            style="--qtxSurveyButton_Size:40px; --sapContent_ContrastFocusColor: transparent;"
            title="{{ 'se.toolbar.qualtrics.title' | translate }}"
            [attr.intercept-url]="interceptUrl"
            [attr.context-params]="contextParamsString"
            (click)="onClick()">
            <button type="button" class="fd-button">
                <span class="sap-icon--feedback"></span>
            </button>
        </cx-qtx-survey-button>
    `
});
let QualtricsComponent = class QualtricsComponent {
    constructor(translateService, settingService, userTrackingService) {
        this.translateService = translateService;
        this.settingService = settingService;
        this.userTrackingService = userTrackingService;
        this.interceptUrl = '';
        this.contextParamsString = '';
        this.contextParams = {};
    }
    ngOnDestroy() {
        document.querySelector('cx-qtx-survey-button').remove();
    }
    ngOnInit() {
        this.contextParams = this._getFixedContextParams();
        this.settingService.load().then((settings) => {
            const tenantId = `${settings['modelt.customer.code']}-${settings['modelt.project.code']}-${settings['modelt.environment.code']}`;
            const tenantRole = settings['modelt.environment.type'];
            const appVersion = settings['build.version.api'];
            this.contextParamsString = JSON.stringify(Object.assign(Object.assign({}, this.contextParams), { tenantId,
                tenantRole,
                appVersion }));
            this.interceptUrl = settings['smartedit.qualtrics.interceptUrl'];
        });
        // change language when customer switch language
        this.translateService.onLangChange.subscribe((result) => {
            const language = result.lang;
            const productName = result.translations['se.application.name'];
            this.contextParamsString = JSON.stringify(Object.assign(Object.assign({}, this.contextParams), { language,
                productName }));
        });
    }
    onClick() {
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.HEADER_TOOL, 'Give Feedback');
    }
    _getFixedContextParams() {
        return {
            appFrameworkId: 4,
            appFrameworkVersion: core.VERSION.full,
            appId: 'SmartEdit',
            appTitle: 'SmartEdit',
            appSupportInfo: 'CEC-COM-ADM-SEDIT',
            pushSrcType: 2
        };
    }
};
QualtricsComponent = __decorate([
    core.Component({
        selector: 'se-qualtrics',
        template: `
        <cx-qtx-survey-button
            fiori3-compatible="true"
            [attr.aria-hidden]="false"
            attr.aria-label="{{ 'se.toolbar.qualtrics.button.arialabel' | translate }}"
            style="--qtxSurveyButton_Size:40px; --sapContent_ContrastFocusColor: transparent;"
            title="{{ 'se.toolbar.qualtrics.title' | translate }}"
            [attr.intercept-url]="interceptUrl"
            [attr.context-params]="contextParamsString"
            (click)="onClick()">
            <button type="button" class="fd-button">
                <span class="sap-icon--feedback"></span>
            </button>
        </cx-qtx-survey-button>
    `
    }),
    __metadata("design:paramtypes", [core$2.TranslateService,
        smarteditcommons.SettingsService,
        smarteditcommons.IUserTrackingService])
], QualtricsComponent);

let TopToolbarsModule = class TopToolbarsModule {
};
TopToolbarsModule = __decorate([
    core.NgModule({
        schemas: [core.CUSTOM_ELEMENTS_SCHEMA],
        imports: [
            core$1.PopoverModule,
            common.CommonModule,
            smarteditcommons.SeGenericEditorModule,
            core$2.TranslateModule.forChild(),
            smarteditcommons.TooltipModule
        ],
        declarations: [
            smarteditcommons.HeaderLanguageDropdownComponent,
            UserAccountComponent,
            InflectionPointSelectorComponent,
            ExperienceSelectorButtonComponent,
            ExperienceSelectorComponent,
            DeviceSupportWrapperComponent,
            ExperienceSelectorWrapperComponent,
            LogoComponent,
            QualtricsComponent
        ],
        entryComponents: [
            smarteditcommons.HeaderLanguageDropdownComponent,
            UserAccountComponent,
            InflectionPointSelectorComponent,
            ExperienceSelectorButtonComponent,
            ExperienceSelectorComponent,
            DeviceSupportWrapperComponent,
            ExperienceSelectorWrapperComponent,
            LogoComponent,
            QualtricsComponent
        ],
        exports: [
            smarteditcommons.HeaderLanguageDropdownComponent,
            UserAccountComponent,
            InflectionPointSelectorComponent,
            ExperienceSelectorButtonComponent,
            ExperienceSelectorComponent,
            DeviceSupportWrapperComponent,
            ExperienceSelectorWrapperComponent,
            LogoComponent,
            QualtricsComponent
        ]
    })
], TopToolbarsModule);

exports.ToolbarModule = class ToolbarModule {
};
exports.ToolbarModule = __decorate([
    core.NgModule({
        imports: [
            TopToolbarsModule,
            common.CommonModule,
            core$2.TranslateModule.forChild(),
            smarteditcommons.PropertyPipeModule,
            smarteditcommons.ResizeObserverModule,
            core$1.PopoverModule,
            smarteditcommons.ClickOutsideModule,
            smarteditcommons.PreventVerticalOverflowModule
        ],
        providers: [
            {
                provide: smarteditcommons.IToolbarServiceFactory,
                useClass: ToolbarServiceFactory
            }
        ],
        declarations: [
            ToolbarActionComponent,
            ToolbarActionOutletComponent,
            ToolbarComponent,
            ToolbarItemContextComponent,
            ToolbarSectionItemComponent
        ],
        entryComponents: [
            ToolbarActionComponent,
            ToolbarActionOutletComponent,
            ToolbarComponent,
            ToolbarItemContextComponent,
            ToolbarSectionItemComponent
        ],
        exports: [
            ToolbarActionComponent,
            ToolbarActionOutletComponent,
            ToolbarComponent,
            ToolbarItemContextComponent,
            ToolbarSectionItemComponent
        ]
    })
], exports.ToolbarModule);

/** @internal */
let /* @ngInject */ UserTrackingService = class /* @ngInject */ UserTrackingService extends smarteditcommons.IUserTrackingService {
    constructor(windowUtils, settingService, logService) {
        super();
        this.windowUtils = windowUtils;
        this.settingService = settingService;
        this.logService = logService;
        this.CUSTOMER_KEY = 'modelt.customer.code';
        this.PROJECT_KEY = 'modelt.project.code';
        this.ENVIRONMENT_KEY = 'modelt.environment.code';
        this.TRACKING_URL_KEY = 'smartedit.click.tracking.server.url';
        this.DEFAULT_CUSTOMER = 'localCustomer';
        this.DEFAULT_PROJECT = 'localProject';
        this.DEFAULT_ENVIRONMENT = 'localEnvironment';
        this.DEFAULT_TRACKING_URL = 'https://license.hybris.com/collect';
        this.isInitialized = false;
    }
    initConfiguration() {
        return __awaiter(this, void 0, void 0, function* () {
            const isTrackingEnabled = yield this.settingService.getBoolean('smartedit.default.click.tracking.enabled');
            if (!isTrackingEnabled) {
                this.isInitialized = false;
                return;
            }
            if (this.isEnvInitialized()) {
                const siteId = yield this.getSiteId();
                let trackingUrl = yield this.settingService.get(this.TRACKING_URL_KEY);
                trackingUrl = trackingUrl !== undefined ? trackingUrl : this.DEFAULT_TRACKING_URL;
                this._paq.push(['setTrackerUrl', trackingUrl]);
                this._paq.push(['setSiteId', siteId]);
                this.isInitialized = true;
            }
            else {
                this.isInitialized = false;
                this.logService.warn('User tracking is enabled and tracking tool is not initialized.');
            }
        });
    }
    trackingUserAction(functionality, key) {
        if (this.isInitialized) {
            if (smarteditcommons.USER_TRACKING_KEY_MAP.has(key)) {
                key = smarteditcommons.USER_TRACKING_KEY_MAP.get(key);
            }
            this._paq.push(['trackEvent', 'SmartEdit', functionality, key]);
        }
    }
    // Check if tracking library piwik is loaded
    isEnvInitialized() {
        this._paq = this.windowUtils.getWindow()._paq;
        return this._paq === undefined ? false : this._paq.push !== undefined;
    }
    getSiteId() {
        return __awaiter(this, void 0, void 0, function* () {
            let customerCode = yield this.settingService.get(this.CUSTOMER_KEY);
            customerCode = customerCode !== undefined ? customerCode : this.DEFAULT_CUSTOMER;
            let projectCode = yield this.settingService.get(this.PROJECT_KEY);
            projectCode = projectCode !== undefined ? projectCode : this.DEFAULT_PROJECT;
            let environmentCode = yield this.settingService.get(this.ENVIRONMENT_KEY);
            environmentCode =
                environmentCode !== undefined ? environmentCode : this.DEFAULT_ENVIRONMENT;
            return `${customerCode}-${projectCode}-${environmentCode}`;
        });
    }
};
/* @ngInject */ UserTrackingService = __decorate([
    smarteditcommons.GatewayProxied('initConfiguration', 'trackingUserAction'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.WindowUtils,
        smarteditcommons.ISettingsService,
        smarteditcommons.LogService])
], /* @ngInject */ UserTrackingService);

window.__smartedit__.addDecoratorPayload("Component", "AnnouncementBoardComponent", {
    selector: 'se-announcement-board',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<ng-container *ngIf="(announcements$ | async) as announcements"><div class="se-announcement-board"><se-announcement *ngFor="
                let announcement of (announcements | seReverse);
                trackBy: announcementTrackBy;
                let i = index
            " id="se-announcement-{{ i }}" [announcement]="announcement"></se-announcement></div></ng-container>`
});
let AnnouncementBoardComponent = class AnnouncementBoardComponent {
    constructor(announcementService) {
        this.announcementService = announcementService;
    }
    ngOnInit() {
        this.announcements$ = this.announcementService.getAnnouncements();
    }
    announcementTrackBy(index, item) {
        return item.id;
    }
};
AnnouncementBoardComponent = __decorate([
    core.Component({
        selector: 'se-announcement-board',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<ng-container *ngIf="(announcements$ | async) as announcements"><div class="se-announcement-board"><se-announcement *ngFor="
                let announcement of (announcements | seReverse);
                trackBy: announcementTrackBy;
                let i = index
            " id="se-announcement-{{ i }}" [announcement]="announcement"></se-announcement></div></ng-container>`
    }),
    __param(0, core.Inject(smarteditcommons.IAnnouncementService)),
    __metadata("design:paramtypes", [AnnouncementService])
], AnnouncementBoardComponent);

window.__smartedit__.addDecoratorPayload("Component", "AnnouncementComponent", {
    selector: 'se-announcement',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    animations: [
        animations.trigger('fadeAnimation', [
            animations.transition(':enter', [
                animations.style({
                    opacity: 0,
                    transform: 'rotateY(90deg)'
                }),
                animations.animate('0.5s'),
                animations.style({
                    opacity: 1,
                    transform: 'translateX(0px)'
                })
            ]),
            animations.transition(':leave', [
                animations.animate('0.25s'),
                animations.style({
                    opacity: '0',
                    transform: 'translateX(100%)'
                })
            ])
        ])
    ],
    template: `<div class="se-announcement-content"><span *ngIf="isCloseable()" class="sap-icon--decline se-announcement-close" (click)="closeAnnouncement()"></span><div *ngIf="hasMessage()"><h4 *ngIf="hasMessageTitle()">{{ announcement.messageTitle | translate }}</h4><span>{{ announcement.message | translate }}</span></div><ng-container *ngIf="hasComponent()"><ng-container *ngComponentOutlet="announcement.component; injector: announcementInjector"></ng-container></ng-container></div>`
});
let AnnouncementComponent = class AnnouncementComponent {
    constructor(announcementService, injector) {
        this.announcementService = announcementService;
        this.injector = injector;
    }
    get fadeAnimation() {
        return true;
    }
    ngOnChanges() {
        this.createAnnouncementInjector();
    }
    hasComponent() {
        return this.announcement.hasOwnProperty('component');
    }
    hasMessage() {
        return this.announcement.hasOwnProperty('message');
    }
    hasMessageTitle() {
        return this.announcement.hasOwnProperty('messageTitle');
    }
    isCloseable() {
        return this.announcement.hasOwnProperty('closeable')
            ? this.announcement.closeable
            : ANNOUNCEMENT_DEFAULTS.closeable;
    }
    closeAnnouncement() {
        this.announcementService.closeAnnouncement(this.announcement.id);
    }
    createAnnouncementInjector() {
        this.announcementInjector = core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: smarteditcommons.ANNOUNCEMENT_DATA,
                    useValue: Object.assign({ id: this.announcement.id }, this.announcement.data)
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], AnnouncementComponent.prototype, "announcement", void 0);
__decorate([
    core.HostBinding('@fadeAnimation'),
    __metadata("design:type", Boolean),
    __metadata("design:paramtypes", [])
], AnnouncementComponent.prototype, "fadeAnimation", null);
AnnouncementComponent = __decorate([
    core.Component({
        selector: 'se-announcement',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        animations: [
            animations.trigger('fadeAnimation', [
                animations.transition(':enter', [
                    animations.style({
                        opacity: 0,
                        transform: 'rotateY(90deg)'
                    }),
                    animations.animate('0.5s'),
                    animations.style({
                        opacity: 1,
                        transform: 'translateX(0px)'
                    })
                ]),
                animations.transition(':leave', [
                    animations.animate('0.25s'),
                    animations.style({
                        opacity: '0',
                        transform: 'translateX(100%)'
                    })
                ])
            ])
        ],
        template: `<div class="se-announcement-content"><span *ngIf="isCloseable()" class="sap-icon--decline se-announcement-close" (click)="closeAnnouncement()"></span><div *ngIf="hasMessage()"><h4 *ngIf="hasMessageTitle()">{{ announcement.messageTitle | translate }}</h4><span>{{ announcement.message | translate }}</span></div><ng-container *ngIf="hasComponent()"><ng-container *ngComponentOutlet="announcement.component; injector: announcementInjector"></ng-container></ng-container></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.IAnnouncementService, core.Injector])
], AnnouncementComponent);

window.__smartedit__.addDecoratorPayload("Component", "HotkeyNotificationComponent", {
    selector: 'se-hotkey-notification',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<div class="se-notification__hotkey"><div *ngFor="let key of hotkeyNames; let last = last"><div class="se-notification__hotkey--key"><span>{{ key }}</span></div><span *ngIf="!last" class="se-notification__hotkey__icon--add">+</span></div><div class="se-notification__hotkey--text"><div class="se-notification__hotkey--title">{{ title }}</div><div class="se-notification__hotkey--message">{{ message }}</div></div></div>`
});
let HotkeyNotificationComponent = class HotkeyNotificationComponent {
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], HotkeyNotificationComponent.prototype, "hotkeyNames", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], HotkeyNotificationComponent.prototype, "title", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], HotkeyNotificationComponent.prototype, "message", void 0);
HotkeyNotificationComponent = __decorate([
    core.Component({
        selector: 'se-hotkey-notification',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<div class="se-notification__hotkey"><div *ngFor="let key of hotkeyNames; let last = last"><div class="se-notification__hotkey--key"><span>{{ key }}</span></div><span *ngIf="!last" class="se-notification__hotkey__icon--add">+</span></div><div class="se-notification__hotkey--text"><div class="se-notification__hotkey--title">{{ title }}</div><div class="se-notification__hotkey--message">{{ message }}</div></div></div>`
    })
], HotkeyNotificationComponent);

window.__smartedit__.addDecoratorPayload("Component", "PerspectiveSelectorHotkeyNotificationComponent", {
    selector: 'se-perspective-selector-hotkey-notification',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<se-hotkey-notification [hotkeyNames]="['esc']" [title]="'se.application.status.hotkey.title' | translate" [message]="'se.application.status.hotkey.message' | translate"></se-hotkey-notification>`
});
let PerspectiveSelectorHotkeyNotificationComponent = class PerspectiveSelectorHotkeyNotificationComponent {
};
PerspectiveSelectorHotkeyNotificationComponent = __decorate([
    smarteditcommons.SeCustomComponent(),
    core.Component({
        selector: 'se-perspective-selector-hotkey-notification',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<se-hotkey-notification [hotkeyNames]="['esc']" [title]="'se.application.status.hotkey.title' | translate" [message]="'se.application.status.hotkey.message' | translate"></se-hotkey-notification>`
    })
], PerspectiveSelectorHotkeyNotificationComponent);

/** @internal */
let HotkeyNotificationModule = class HotkeyNotificationModule {
};
HotkeyNotificationModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, smarteditcommons.TranslationModule.forChild()],
        declarations: [HotkeyNotificationComponent, PerspectiveSelectorHotkeyNotificationComponent],
        entryComponents: [HotkeyNotificationComponent, PerspectiveSelectorHotkeyNotificationComponent]
    })
], HotkeyNotificationModule);

window.__smartedit__.addDecoratorPayload("Component", "InvalidRouteComponent", { selector: 'empty', template: "<div>This page doesn't exist</div>" });
/**
 * Component that is displayed when Angular route has not been found
 */
let InvalidRouteComponent = class InvalidRouteComponent {
};
InvalidRouteComponent = __decorate([
    core.Component({ selector: 'empty', template: "<div>This page doesn't exist</div>" })
], InvalidRouteComponent);

const LEGACY_APP_NAME = 'legacyApp';
const LIGHT_THEME = 'sap_fiori_3';
window.__smartedit__.addDecoratorPayload("Component", "SmarteditcontainerComponent", {
    selector: smarteditcommons.SMARTEDITCONTAINER_COMPONENT_NAME,
    template: `
        <link rel="stylesheet" *ngIf="cssUrl && !defaultTheme" [href]="cssUrl" />
        <link rel="stylesheet" *ngIf="cssCustomUrl && !defaultTheme" [href]="cssCustomUrl" />
        <router-outlet></router-outlet>
        <div ng-attr-id="${LEGACY_APP_NAME}">
            <se-announcement-board></se-announcement-board>
            <se-notification-panel></se-notification-panel>
            <div ng-view></div>
        </div>
    `
});
let SmarteditcontainerComponent = class SmarteditcontainerComponent {
    constructor(translateService, injector, elementRef, themeSwitchService, crossFrameEventService) {
        this.translateService = translateService;
        this.elementRef = elementRef;
        this.themeSwitchService = themeSwitchService;
        this.crossFrameEventService = crossFrameEventService;
        this.legacyAppName = LEGACY_APP_NAME;
        this.legacyAppName = LEGACY_APP_NAME;
        this.setApplicationTitle();
        smarteditcommons.registerCustomComponents(injector);
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.TOKEN_STORED, () => {
            themeSwitchService.selectTheme(null);
        });
        this.crossFrameEventService.subscribe(smarteditcommons.EVENT_THEME_CHANGED, () => {
            this.getThemeCss();
        });
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            /*
             * for e2e purposes:
             * in e2e, we sometimes add some test code in the parent frame to be added to the runtime
             * since we only bootstrap within smarteditcontainer-component node,
             * this code will be ignored unless added into the component before legacy AnguylarJS bootstrapping
             */
            Array.prototype.slice
                .call(document.body.childNodes)
                .filter((childNode) => !this.isAppComponent(childNode) && !this.isSmarteditLoader(childNode))
                .forEach((childNode) => {
                this.legacyAppNode.appendChild(childNode);
            });
            yield this.themeSwitchService.selectTheme(null);
            this.getThemeCss();
        });
    }
    setApplicationTitle() {
        this.translateService.get('se.application.name').subscribe((pageTitle) => {
            document.title = pageTitle;
        });
    }
    get legacyAppNode() {
        return this.elementRef.nativeElement.querySelector(`div[ng-attr-id="${this.legacyAppName}"]`);
    }
    isAppComponent(childNode) {
        return (childNode.nodeType === Node.ELEMENT_NODE &&
            childNode.tagName === smarteditcommons.SMARTEDITCONTAINER_COMPONENT_NAME.toUpperCase());
    }
    isSmarteditLoader(childNode) {
        return (childNode.nodeType === Node.ELEMENT_NODE &&
            (childNode.id === 'smarteditloader' ||
                childNode.tagName === smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME.toUpperCase()));
    }
    getThemeCss() {
        this.cssUrl = this.themeSwitchService.cssUrl;
        this.cssCustomUrl = this.themeSwitchService.cssCustomUrl;
        this.defaultTheme = this.themeSwitchService.selectedTheme === LIGHT_THEME ? true : false;
    }
};
SmarteditcontainerComponent = __decorate([
    core.Component({
        selector: smarteditcommons.SMARTEDITCONTAINER_COMPONENT_NAME,
        template: `
        <link rel="stylesheet" *ngIf="cssUrl && !defaultTheme" [href]="cssUrl" />
        <link rel="stylesheet" *ngIf="cssCustomUrl && !defaultTheme" [href]="cssCustomUrl" />
        <router-outlet></router-outlet>
        <div ng-attr-id="${LEGACY_APP_NAME}">
            <se-announcement-board></se-announcement-board>
            <se-notification-panel></se-notification-panel>
            <div ng-view></div>
        </div>
    `
    }),
    __metadata("design:paramtypes", [core$2.TranslateService,
        core.Injector,
        core.ElementRef,
        exports.ThemeSwitchService,
        smarteditcommons.CrossFrameEventService])
], SmarteditcontainerComponent);

window.__smartedit__.addDecoratorPayload("Component", "NotificationComponent", {
    selector: 'se-notification',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<div class="se-notification" id="{{ id }}" *ngIf="notification"><div [seCustomComponentOutlet]="notification.componentName"></div></div>`
});
let NotificationComponent = class NotificationComponent {
    ngOnInit() {
        this.id =
            this.notification && this.notification.id
                ? 'se-notification-' + this.notification.id
                : '';
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], NotificationComponent.prototype, "notification", void 0);
NotificationComponent = __decorate([
    core.Component({
        selector: 'se-notification',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<div class="se-notification" id="{{ id }}" *ngIf="notification"><div [seCustomComponentOutlet]="notification.componentName"></div></div>`
    })
], NotificationComponent);

window.__smartedit__.addDecoratorPayload("Component", "NotificationPanelComponent", {
    selector: 'se-notification-panel',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<ng-container *ngIf="(notifications$ | async) as notifications"><div class="se-notification-panel" *ngIf="notifications.length > 0" [ngClass]="{'is-mouseover': isMouseOver}" (mouseenter)="onMouseEnter()"><div><se-notification *ngFor="let notification of (notifications | seReverse)" [notification]="notification"></se-notification></div></div></ng-container>`
});
let NotificationPanelComponent = class NotificationPanelComponent {
    constructor(notificationService, notificationMouseLeaveDetectionService, systemEventService, iframeManagerService, windowUtils, element, yjQuery, cd) {
        this.notificationService = notificationService;
        this.notificationMouseLeaveDetectionService = notificationMouseLeaveDetectionService;
        this.systemEventService = systemEventService;
        this.iframeManagerService = iframeManagerService;
        this.windowUtils = windowUtils;
        this.element = element;
        this.yjQuery = yjQuery;
        this.cd = cd;
        this.notificationPanelBounds = null;
        this.iFrameNotificationPanelBounds = null;
        this.addMouseMoveEventListenerTimeout = null;
    }
    ngOnInit() {
        this.notifications$ = this.notificationService.getNotifications();
        this.isMouseOver = false;
        this.windowUtils.getWindow().addEventListener('resize', () => this.onResize());
        this.unRegisterNotificationChangedEventHandler = this.systemEventService.subscribe(smarteditcommons.EVENT_NOTIFICATION_CHANGED, () => this.onNotificationChanged());
    }
    ngAfterViewInit() {
        this.$element = this.yjQuery(this.element.nativeElement);
    }
    ngOnDestroy() {
        this.windowUtils.getWindow().removeEventListener('resize', () => this.onResize());
        this.notificationMouseLeaveDetectionService.stopDetection();
        this.unRegisterNotificationChangedEventHandler();
    }
    onMouseEnter() {
        this.isMouseOver = true;
        this.cd.detectChanges();
        if (!this.hasBounds()) {
            this.calculateBounds();
        }
        this.addMouseMoveEventListenerTimeout =
            this.addMouseMoveEventListenerTimeout ||
                setTimeout(() => this.addMouseMoveEventListener(), 10);
    }
    onMouseLeave() {
        this.isMouseOver = false;
        this.cd.detectChanges();
    }
    getIFrame() {
        return this.iframeManagerService.getIframe()[0];
    }
    getNotificationPanel() {
        return this.$element.find('.se-notification-panel');
    }
    calculateNotificationPanelBounds() {
        const notificationPanel = this.getNotificationPanel();
        const notificationPanelPosition = notificationPanel.position();
        this.notificationPanelBounds = {
            x: Math.floor(notificationPanelPosition.left),
            y: Math.floor(notificationPanelPosition.top),
            width: Math.floor(notificationPanel.width()),
            height: Math.floor(notificationPanel.height())
        };
    }
    calculateIFrameNotificationPanelBounds() {
        const iFrame = this.getIFrame();
        if (iFrame) {
            this.iFrameNotificationPanelBounds = {
                x: this.notificationPanelBounds.x - iFrame.offsetLeft,
                y: this.notificationPanelBounds.y - iFrame.offsetTop,
                width: this.notificationPanelBounds.width,
                height: this.notificationPanelBounds.height
            };
        }
    }
    calculateBounds() {
        this.calculateNotificationPanelBounds();
        this.calculateIFrameNotificationPanelBounds();
    }
    invalidateBounds() {
        this.notificationPanelBounds = null;
        this.iFrameNotificationPanelBounds = null;
    }
    hasBounds() {
        const hasNotificationPanelBounds = !!this.notificationPanelBounds;
        const hasIFrameBounds = this.getIFrame()
            ? !!this.iFrameNotificationPanelBounds
            : true;
        return hasNotificationPanelBounds && hasIFrameBounds;
    }
    cancelDetection() {
        this.invalidateBounds();
        this.notificationMouseLeaveDetectionService.stopDetection();
        if (this.isMouseOver) {
            this.onMouseLeave();
        }
    }
    onResize() {
        this.cancelDetection();
    }
    onNotificationChanged() {
        if (!this.isMouseOver) {
            this.cancelDetection();
        }
    }
    addMouseMoveEventListener() {
        this.addMouseMoveEventListenerTimeout = null;
        this.notificationMouseLeaveDetectionService.startDetection(this.notificationPanelBounds, this.iFrameNotificationPanelBounds, () => this.onMouseLeave());
    }
};
NotificationPanelComponent = __decorate([
    core.Component({
        selector: 'se-notification-panel',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<ng-container *ngIf="(notifications$ | async) as notifications"><div class="se-notification-panel" *ngIf="notifications.length > 0" [ngClass]="{'is-mouseover': isMouseOver}" (mouseenter)="onMouseEnter()"><div><se-notification *ngFor="let notification of (notifications | seReverse)" [notification]="notification"></se-notification></div></div></ng-container>`
    }),
    __param(0, core.Inject(smarteditcommons.INotificationService)),
    __param(1, core.Inject(smarteditcommons.INotificationMouseLeaveDetectionService)),
    __param(6, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [NotificationService,
        NotificationMouseLeaveDetectionService,
        smarteditcommons.SystemEventService,
        IframeManagerService,
        smarteditcommons.WindowUtils,
        core.ElementRef, Function, core.ChangeDetectorRef])
], NotificationPanelComponent);

let NotificationPanelModule = class NotificationPanelModule {
};
NotificationPanelModule = __decorate([
    core.NgModule({
        imports: [
            SmarteditServicesModule,
            smarteditcommons.SharedComponentsModule,
            common.CommonModule,
            smarteditcommons.CustomComponentOutletDirectiveModule
        ],
        declarations: [NotificationPanelComponent, NotificationComponent],
        exports: [NotificationPanelComponent]
    })
], NotificationPanelModule);

const SITES_ID = 'sites-id';

window.__smartedit__.addDecoratorPayload("Component", "LandingPageComponent", {
    selector: 'se-landing-page',
    template: `<div class="se-toolbar-group"><se-toolbar cssClass="se-toolbar--shell" imageRoot="imageRoot" toolbarName="smartEditHeaderToolbar"></se-toolbar></div><div class="se-landing-page"><div class="se-landing-page-actions"><div class="se-landing-page-container"><h1 class="se-landing-page-title">{{ 'se.landingpage.title' | translate }}</h1><div class="se-landing-page-site-selection" *ngIf="model"><se-generic-editor-dropdown [field]="field" [qualifier]="qualifier" [model]="model" [id]="sitesId"></se-generic-editor-dropdown></div><p class="se-landing-page-label">{{ 'se.landingpage.label' | translate }}</p></div></div><p class="se-landing-page-container se-landing-page-sub-header">Content Catalogs</p><div class="se-landing-page-container se-landing-page-catalogs"><div class="se-landing-page-catalog" *ngFor="let catalog of catalogs; let isLast = last"><se-catalog-details [catalog]="catalog" [siteId]="model.site" [isCatalogForCurrentSite]="isLast"></se-catalog-details></div></div><img src="static-resources/images/best-run-sap-logo.svg" class="se-landing-page-footer-logo" title="{{ 'se.logo.title' | translate }}"/></div>`
});
let LandingPageComponent = class LandingPageComponent {
    constructor(siteService, catalogService, systemEventService, storageService, alertService, route, location, yjQuery) {
        this.siteService = siteService;
        this.catalogService = catalogService;
        this.systemEventService = systemEventService;
        this.storageService = storageService;
        this.alertService = alertService;
        this.route = route;
        this.location = location;
        this.yjQuery = yjQuery;
        this.sitesId = SITES_ID;
        this.qualifier = 'site';
        this.catalogs = [];
        this.SELECTED_SITE_COOKIE_NAME = 'seselectedsite';
        this.unregisterUserChangesEventHandler = this.systemEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, () => {
            this.init();
        });
    }
    ngOnInit() {
        this.init();
    }
    ngOnDestroy() {
        var _a, _b;
        (_a = this.unregisterSitesDropdownEventHandler) === null || _a === void 0 ? void 0 : _a.call(this);
        (_b = this.unregisterUserChangesEventHandler) === null || _b === void 0 ? void 0 : _b.call(this);
    }
    init() {
        var _a;
        if (this.paramMapSubscription) {
            this.paramMapSubscription.unsubscribe();
        }
        else {
            this.removeStorefrontCssClass();
        }
        this.paramMapSubscription = this.route.paramMap.subscribe((params) => {
            this.getCurrentSiteId(params.get('siteId')).then((siteId) => {
                this.setModel(siteId);
            });
        });
        this.siteService.getAccessibleSites().then((sites) => {
            this.field = {
                idAttribute: 'uid',
                labelAttributes: ['name'],
                editable: true,
                paged: false,
                options: sites
            };
        });
        (_a = this.unregisterSitesDropdownEventHandler) === null || _a === void 0 ? void 0 : _a.call(this);
        this.unregisterSitesDropdownEventHandler = this.systemEventService.subscribe(this.sitesId + smarteditcommons.LINKED_DROPDOWN, this.selectedSiteDropdownEventHandler.bind(this));
    }
    getCurrentSiteId(siteIdFromUrl) {
        return this.storageService
            .getValueFromLocalStorage(this.SELECTED_SITE_COOKIE_NAME, false)
            .then((siteIdFromCookie) => this.siteService.getAccessibleSites().then((sites) => {
            const isSiteAvailableFromUrl = sites.some((site) => site.uid === siteIdFromUrl);
            if (isSiteAvailableFromUrl) {
                this.setSelectedSite(siteIdFromUrl);
                this.updateRouteToRemoveSite();
                return siteIdFromUrl;
            }
            else {
                if (siteIdFromUrl) {
                    this.alertService.showInfo('se.landingpage.site.url.error');
                    this.updateRouteToRemoveSite();
                }
                const isSelectedSiteAvailableFromCookie = sites.some((site) => site.uid === siteIdFromCookie);
                if (!isSelectedSiteAvailableFromCookie) {
                    const firstSiteId = sites.length > 0 ? sites[0].uid : null;
                    return firstSiteId;
                }
                else {
                    return siteIdFromCookie;
                }
            }
        }));
    }
    updateRouteToRemoveSite() {
        this.location.replaceState('');
    }
    removeStorefrontCssClass() {
        const bodyTag = this.yjQuery(document.querySelector('body'));
        if (bodyTag.hasClass('is-storefront')) {
            bodyTag.removeClass('is-storefront');
        }
        if (bodyTag.hasClass('is-safari')) {
            bodyTag.removeClass('is-safari');
        }
    }
    selectedSiteDropdownEventHandler(_eventId, data) {
        if (data.optionObject) {
            const siteId = data.optionObject.id;
            this.setSelectedSite(siteId);
            this.loadCatalogsBySite(siteId);
            this.setModel(siteId);
        }
        else {
            this.catalogs = [];
        }
    }
    setSelectedSite(siteId) {
        this.storageService.setValueInLocalStorage(this.SELECTED_SITE_COOKIE_NAME, siteId, false);
    }
    loadCatalogsBySite(siteId) {
        this.catalogService
            .getContentCatalogsForSite(siteId)
            .then((catalogs) => (this.catalogs = catalogs));
    }
    setModel(siteId) {
        if (this.model) {
            this.model[this.qualifier] = siteId;
        }
        else {
            this.model = {
                [this.qualifier]: siteId
            };
        }
    }
};
LandingPageComponent = __decorate([
    core.Component({
        selector: 'se-landing-page',
        template: `<div class="se-toolbar-group"><se-toolbar cssClass="se-toolbar--shell" imageRoot="imageRoot" toolbarName="smartEditHeaderToolbar"></se-toolbar></div><div class="se-landing-page"><div class="se-landing-page-actions"><div class="se-landing-page-container"><h1 class="se-landing-page-title">{{ 'se.landingpage.title' | translate }}</h1><div class="se-landing-page-site-selection" *ngIf="model"><se-generic-editor-dropdown [field]="field" [qualifier]="qualifier" [model]="model" [id]="sitesId"></se-generic-editor-dropdown></div><p class="se-landing-page-label">{{ 'se.landingpage.label' | translate }}</p></div></div><p class="se-landing-page-container se-landing-page-sub-header">Content Catalogs</p><div class="se-landing-page-container se-landing-page-catalogs"><div class="se-landing-page-catalog" *ngFor="let catalog of catalogs; let isLast = last"><se-catalog-details [catalog]="catalog" [siteId]="model.site" [isCatalogForCurrentSite]="isLast"></se-catalog-details></div></div><img src="static-resources/images/best-run-sap-logo.svg" class="se-landing-page-footer-logo" title="{{ 'se.logo.title' | translate }}"/></div>`
    }),
    __param(7, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [SiteService,
        smarteditcommons.ICatalogService,
        smarteditcommons.SystemEventService,
        smarteditcommons.IStorageService,
        smarteditcommons.IAlertService,
        router.ActivatedRoute,
        common.Location, Function])
], LandingPageComponent);

window.__smartedit__.addDecoratorPayload("Component", "StorefrontPageComponent", {
    selector: 'se-storefront-page',
    template: `<div class="se-toolbar-group"><se-toolbar cssClass="se-toolbar--shell" imageRoot="imageRoot" toolbarName="smartEditHeaderToolbar"></se-toolbar><se-toolbar cssClass="se-toolbar--experience" imageRoot="imageRoot" toolbarName="smartEditExperienceToolbar"></se-toolbar><se-toolbar [ngClass]="{'se-toolbar--perspective-hidden': !isReady}" cssClass="se-toolbar--perspective" imageRoot="imageRoot" toolbarName="smartEditPerspectiveToolbar"></se-toolbar></div><div id="js_iFrameWrapper" class="iframeWrapper"><div class="wrap"><div *ngIf="showPageTreeList()" id="page-tree-container" [ngStyle]="{'width': width}" class="page-tree__list"><se-page-tree-container></se-page-tree-container></div><div *ngIf="showPageTreeList()" class="splitter-tree-panel" resizer="#page-tree-container"></div><div class="iframe-panel"><iframe id="ySmartEditFrame" src=""></iframe><div id="ySmartEditFrameDragArea"></div></div></div></div>`,
    styles: [`.wrap{display:flex;justify-content:flex-start;flex-wrap:nowrap;height:100%}.page-tree__list{height:100%;padding:16px 0 16px 8px;min-width:20%;max-width:40%;flex:0 0 auto}.iframe-panel{width:100%;height:100%;flex:1 1 auto}.splitter-tree-panel{flex:0 0 auto;width:8px;cursor:col-resize}.se-toolbar--perspective-hidden{visibility:hidden}`]
});
let StorefrontPageComponent = class StorefrontPageComponent {
    constructor(browserService, iframeManagerService, experienceService, yjQuery, crossFrameEventService, storageService, perspectiveService, smarteditBootstrapGateway) {
        this.browserService = browserService;
        this.iframeManagerService = iframeManagerService;
        this.experienceService = experienceService;
        this.yjQuery = yjQuery;
        this.crossFrameEventService = crossFrameEventService;
        this.storageService = storageService;
        this.perspectiveService = perspectiveService;
        this.smarteditBootstrapGateway = smarteditBootstrapGateway;
        this.PAGE_TREE_PANEL_OPEN_COOKIE_NAME = 'smartedit-page-tree-panel-open';
        this.isPageTreePanelOpen = false;
        this.isReady = false;
        this.width = '20%';
    }
    ngOnDestroy() {
        if (this.unregisterPageTreePanelSwitch) {
            this.unregisterPageTreePanelSwitch();
        }
        if (this.unregisterOpenInPageTree) {
            this.unregisterOpenInPageTree();
        }
        if (this.unregisterPerspectiveChanged) {
            this.unregisterPerspectiveChanged();
        }
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.iframeManagerService.applyDefault();
            yield this.experienceService.initializeExperience();
            this.yjQuery(document.body).addClass('is-storefront');
            if (this.browserService.isSafari()) {
                this.yjQuery(document.body).addClass('is-safari');
            }
            const activePerspective = this.perspectiveService.getActivePerspective();
            if (activePerspective &&
                activePerspective.key === smarteditcommons.CMSModesService.ADVANCED_PERSPECTIVE_KEY) {
                yield this.getPageTreePanelStatus();
                yield this.getPageTreePanelWidth();
            }
            this.unregisterPageTreePanelSwitch = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_PAGE_TREE_PANEL_SWITCH, this.handlePageTreePanelSwitch.bind(this));
            this.unregisterOpenInPageTree = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_OPEN_IN_PAGE_TREE, this.handleOpenInPageTree.bind(this));
            this.unregisterPerspectiveChanged = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_CHANGED, this.handlePerspectiveChanged.bind(this));
            this.smarteditBootstrapGateway
                .getInstance()
                .subscribe('smartEditReady', this.setPerspectiveToolbarVisible.bind(this));
        });
    }
    showPageTreeList() {
        return this.isPageTreePanelOpen;
    }
    updateStorage() {
        this.storageService.setValueInLocalStorage(this.PAGE_TREE_PANEL_OPEN_COOKIE_NAME, this.isPageTreePanelOpen, true);
    }
    setPerspectiveToolbarVisible() {
        this.isReady = true;
    }
    handlePageTreePanelSwitch() {
        return __awaiter(this, void 0, void 0, function* () {
            this.isPageTreePanelOpen = !this.isPageTreePanelOpen;
            if (this.isPageTreePanelOpen) {
                yield this.getPageTreePanelWidth();
            }
            this.updateStorage();
        });
    }
    handleOpenInPageTree() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.isPageTreePanelOpen) {
                this.isPageTreePanelOpen = true;
                yield this.getPageTreePanelWidth();
                this.updateStorage();
            }
        });
    }
    handlePerspectiveChanged() {
        return __awaiter(this, void 0, void 0, function* () {
            const activePerspective = this.perspectiveService.getActivePerspective();
            if (activePerspective &&
                activePerspective.key === smarteditcommons.CMSModesService.ADVANCED_PERSPECTIVE_KEY) {
                yield this.getPageTreePanelStatus();
                yield this.getPageTreePanelWidth();
            }
            else {
                this.isPageTreePanelOpen = false;
            }
        });
    }
    getPageTreePanelWidth() {
        return __awaiter(this, void 0, void 0, function* () {
            const width = yield this.storageService.getValueFromLocalStorage(smarteditcommons.PAGE_TREE_PANEL_WIDTH_COOKIE_NAME, true);
            if (!!width) {
                this.width = width + 'px';
            }
        });
    }
    getPageTreePanelStatus() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageTreePanelStatus = yield this.storageService.getValueFromLocalStorage(this.PAGE_TREE_PANEL_OPEN_COOKIE_NAME, true);
            this.isPageTreePanelOpen = pageTreePanelStatus;
        });
    }
};
StorefrontPageComponent = __decorate([
    core.Component({
        selector: 'se-storefront-page',
        template: `<div class="se-toolbar-group"><se-toolbar cssClass="se-toolbar--shell" imageRoot="imageRoot" toolbarName="smartEditHeaderToolbar"></se-toolbar><se-toolbar cssClass="se-toolbar--experience" imageRoot="imageRoot" toolbarName="smartEditExperienceToolbar"></se-toolbar><se-toolbar [ngClass]="{'se-toolbar--perspective-hidden': !isReady}" cssClass="se-toolbar--perspective" imageRoot="imageRoot" toolbarName="smartEditPerspectiveToolbar"></se-toolbar></div><div id="js_iFrameWrapper" class="iframeWrapper"><div class="wrap"><div *ngIf="showPageTreeList()" id="page-tree-container" [ngStyle]="{'width': width}" class="page-tree__list"><se-page-tree-container></se-page-tree-container></div><div *ngIf="showPageTreeList()" class="splitter-tree-panel" resizer="#page-tree-container"></div><div class="iframe-panel"><iframe id="ySmartEditFrame" src=""></iframe><div id="ySmartEditFrameDragArea"></div></div></div></div>`,
        styles: [`.wrap{display:flex;justify-content:flex-start;flex-wrap:nowrap;height:100%}.page-tree__list{height:100%;padding:16px 0 16px 8px;min-width:20%;max-width:40%;flex:0 0 auto}.iframe-panel{width:100%;height:100%;flex:1 1 auto}.splitter-tree-panel{flex:0 0 auto;width:8px;cursor:col-resize}.se-toolbar--perspective-hidden{visibility:hidden}`]
    }),
    __param(3, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.BrowserService,
        IframeManagerService,
        smarteditcommons.IExperienceService, Function, smarteditcommons.CrossFrameEventService,
        smarteditcommons.IStorageService,
        smarteditcommons.IPerspectiveService,
        smarteditcommons.SmarteditBootstrapGateway])
], StorefrontPageComponent);

window.__smartedit__.addDecoratorPayload("Component", "PageTreeContainerComponent", {
    selector: 'se-page-tree-container',
    template: `<div *ngIf="component" class="page-tree-container"><ng-container *ngComponentOutlet="component"></ng-container></div>`,
    styles: [`.page-tree-container{height:100%}`]
});
let PageTreeContainerComponent = class PageTreeContainerComponent {
    constructor(pageTreeService) {
        this.pageTreeService = pageTreeService;
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            const config = yield this.pageTreeService.getTreeComponent();
            this.component = config.component;
        });
    }
};
PageTreeContainerComponent = __decorate([
    core.Component({
        selector: 'se-page-tree-container',
        template: `<div *ngIf="component" class="page-tree-container"><ng-container *ngComponentOutlet="component"></ng-container></div>`,
        styles: [`.page-tree-container{height:100%}`]
    }),
    __metadata("design:paramtypes", [smarteditcommons.IPageTreeService])
], PageTreeContainerComponent);

const MULTI_PRODUCT_CATALOGS_UPDATED = 'MULTI_PRODUCT_CATALOGS_UPDATED';

window.__smartedit__.addDecoratorPayload("Component", "MultiProductCatalogVersionConfigurationComponent", {
    selector: 'se-multi-product-catalog-version-configuration',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-multi-product-catalog-version-configuration]': 'true'
    },
    template: `<div class="form-group se-multi-product-catalog-version-selector__label">{{'se.product.catalogs.multiple.list.header' | translate}}</div><div class="se-multi-product-catalog-version-selector__catalog form-group" *ngFor="let productCatalog of productCatalogs"><label class="se-control-label se-multi-product-catalog-version-selector__catalog-name" id="{{ productCatalog.catalogId }}-label">{{ productCatalog.name | seL10n | async }}</label><div class="se-multi-product-catalog-version-selector__catalog-version"><se-select [id]="productCatalog.catalogId" [(model)]="productCatalog.selectedItem" [onChange]="updateModel()" [fetchStrategy]="productCatalog.fetchStrategy"></se-select></div></div>`
});
let MultiProductCatalogVersionConfigurationComponent = class MultiProductCatalogVersionConfigurationComponent {
    constructor(modalManager, systemEventService) {
        this.modalManager = modalManager;
        this.systemEventService = systemEventService;
        this.doneButtonId = 'done';
    }
    ngOnInit() {
        this.modalManager
            .getModalData()
            .pipe(operators.take(1))
            .subscribe((data) => {
            this.selectedCatalogVersions = data.selectedCatalogVersions;
            this.productCatalogs = data.productCatalogs.map((productCatalog) => {
                const versions = productCatalog.versions.map((version) => (Object.assign(Object.assign({}, version), { id: version.uuid, label: version.version })));
                return Object.assign(Object.assign({}, productCatalog), { versions, fetchStrategy: {
                        fetchAll: () => Promise.resolve(versions)
                    }, selectedItem: productCatalog.versions.find((version) => this.selectedCatalogVersions.includes(version.uuid)).uuid });
            });
        });
        this.modalManager.setDismissCallback(() => this.onCancel());
        this.modalManager.addButtons([
            {
                id: this.doneButtonId,
                label: 'se.confirmation.modal.done',
                style: smarteditcommons.ModalButtonStyle.Primary,
                action: smarteditcommons.ModalButtonAction.None,
                disabled: true,
                callback: () => rxjs.from(this.onSave())
            },
            {
                id: 'cancel',
                label: 'se.confirmation.modal.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss,
                callback: () => rxjs.from(this.onCancel())
            }
        ]);
    }
    updateModel() {
        return () => {
            const selectedVersions = this.productCatalogs.map((productCatalog) => productCatalog.selectedItem);
            this.updateSelection(selectedVersions);
        };
    }
    updateSelection(updatedSelectedVersions) {
        if (!lo.isEqual(updatedSelectedVersions, this.selectedCatalogVersions)) {
            this.updatedCatalogVersions = updatedSelectedVersions;
            this.modalManager.enableButton(this.doneButtonId);
        }
        else {
            this.modalManager.disableButton(this.doneButtonId);
        }
    }
    onCancel() {
        this.modalManager.close(null);
        return Promise.resolve();
    }
    onSave() {
        this.systemEventService.publishAsync(MULTI_PRODUCT_CATALOGS_UPDATED, this.updatedCatalogVersions);
        this.modalManager.close(null);
        return Promise.resolve();
    }
};
MultiProductCatalogVersionConfigurationComponent = __decorate([
    core.Component({
        selector: 'se-multi-product-catalog-version-configuration',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-multi-product-catalog-version-configuration]': 'true'
        },
        template: `<div class="form-group se-multi-product-catalog-version-selector__label">{{'se.product.catalogs.multiple.list.header' | translate}}</div><div class="se-multi-product-catalog-version-selector__catalog form-group" *ngFor="let productCatalog of productCatalogs"><label class="se-control-label se-multi-product-catalog-version-selector__catalog-name" id="{{ productCatalog.catalogId }}-label">{{ productCatalog.name | seL10n | async }}</label><div class="se-multi-product-catalog-version-selector__catalog-version"><se-select [id]="productCatalog.catalogId" [(model)]="productCatalog.selectedItem" [onChange]="updateModel()" [fetchStrategy]="productCatalog.fetchStrategy"></se-select></div></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.ModalManagerService,
        smarteditcommons.SystemEventService])
], MultiProductCatalogVersionConfigurationComponent);

window.__smartedit__.addDecoratorPayload("Component", "MultiProductCatalogVersionSelectorComponent", {
    selector: 'se-multi-product-catalog-version-selector',
    providers: [smarteditcommons.L10nPipe],
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-multi-product-catalog-version-selector]': 'true'
    },
    template: `<se-tooltip [placement]="'bottom'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" class="se-products-catalog-select-multiple__tooltip"><div id="multi-product-catalog-versions-selector" se-tooltip-trigger (click)="onClickSelector()" class="se-products-catalog-select-multiple"><input type="text" [value]="multiProductCatalogVersionsSelectedOptions$ | async" class="fd-input se-products-catalog-select-multiple__catalogs se-nowrap-ellipsis" [name]="'productCatalogVersions'" readonly="readonly"/> <span class="hyicon hyicon-optionssm se-products-catalog-select-multiple__icon"></span></div><div class="se-product-catalogs-tooltip" se-tooltip-body><div class="se-product-catalogs-tooltip__h">{{ ('se.product.catalogs.selector.headline.tooltip' || '') | translate }}</div><div class="se-product-catalog-info" *ngFor="let productCatalog of productCatalogs">{{ getCatalogNameCatalogVersionLabel(productCatalog.catalogId) | async }}</div></div></se-tooltip>`
});
let MultiProductCatalogVersionSelectorComponent = class MultiProductCatalogVersionSelectorComponent {
    constructor(l10nPipe, modalService, systemEventService) {
        this.l10nPipe = l10nPipe;
        this.modalService = modalService;
        this.systemEventService = systemEventService;
        this.selectedProductCatalogVersionsChange = new core.EventEmitter();
        this.multiProductCatalogVersionsSelectedOptions$ = new rxjs.BehaviorSubject('');
    }
    ngOnInit() {
        this.$unRegEventForMultiProducts = this.systemEventService.subscribe(MULTI_PRODUCT_CATALOGS_UPDATED, (eventId, catalogVersions) => this.updateProductCatalogVersionsModel(eventId, catalogVersions));
    }
    ngOnDestroy() {
        if (this.$unRegEventForMultiProducts) {
            this.$unRegEventForMultiProducts();
        }
    }
    ngOnChanges() {
        this.setMultiVersionSelectorTexts(this.productCatalogs);
    }
    onClickSelector() {
        this.modalService.open({
            component: MultiProductCatalogVersionConfigurationComponent,
            data: {
                productCatalogs: this.productCatalogs,
                selectedCatalogVersions: this.selectedProductCatalogVersions
            },
            templateConfig: {
                title: 'se.modal.product.catalog.configuration'
            },
            config: {
                dialogPanelClass: 'modal-md modal-stretched'
            }
        });
    }
    getCatalogNameCatalogVersionLabel(catalogId) {
        const catalogNameCatalogVersionLabel = this.catalogNameCatalogVersionLabelMap.get(catalogId);
        return this.l10nPipe
            .transform(catalogNameCatalogVersionLabel.name)
            .pipe(operators.map((name) => `${name} (${catalogNameCatalogVersionLabel.version})`));
    }
    setMultiVersionSelectorTexts(productCatalogs) {
        this.catalogNameCatalogVersionLabelMap = this.buildCatalogNameCatalogVersionLabelMap(productCatalogs, this.selectedProductCatalogVersions);
        this.setMultiProductCatalogVersionsSelectedOptions(productCatalogs);
    }
    buildCatalogNameCatalogVersionLabelMap(productCatalogs, versionsFromModel) {
        const catalogsMap = new Map();
        productCatalogs.forEach((productCatalog) => {
            const productCatalogVersion = productCatalog.versions.find((version) => versionsFromModel && versionsFromModel.includes(version.uuid));
            if (productCatalogVersion) {
                catalogsMap.set(productCatalog.catalogId, {
                    name: productCatalog.name,
                    version: productCatalogVersion.version
                });
            }
        });
        return catalogsMap;
    }
    setMultiProductCatalogVersionsSelectedOptions(productCatalogs) {
        if (productCatalogs) {
            const labels$ = Array.from(this.catalogNameCatalogVersionLabelMap).map((key) => this.getCatalogNameCatalogVersionLabel(key[0]));
            rxjs.combineLatest(labels$)
                .pipe(operators.take(1), operators.map((results) => results.join(', ')))
                .subscribe((selectedOptions) => this.multiProductCatalogVersionsSelectedOptions$.next(selectedOptions));
        }
        else {
            this.multiProductCatalogVersionsSelectedOptions$.next('');
        }
    }
    updateProductCatalogVersionsModel(_eventId, catalogVersions) {
        this.selectedProductCatalogVersionsChange.emit(catalogVersions);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], MultiProductCatalogVersionSelectorComponent.prototype, "productCatalogs", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], MultiProductCatalogVersionSelectorComponent.prototype, "selectedProductCatalogVersions", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], MultiProductCatalogVersionSelectorComponent.prototype, "selectedProductCatalogVersionsChange", void 0);
MultiProductCatalogVersionSelectorComponent = __decorate([
    core.Component({
        selector: 'se-multi-product-catalog-version-selector',
        providers: [smarteditcommons.L10nPipe],
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-multi-product-catalog-version-selector]': 'true'
        },
        template: `<se-tooltip [placement]="'bottom'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" class="se-products-catalog-select-multiple__tooltip"><div id="multi-product-catalog-versions-selector" se-tooltip-trigger (click)="onClickSelector()" class="se-products-catalog-select-multiple"><input type="text" [value]="multiProductCatalogVersionsSelectedOptions$ | async" class="fd-input se-products-catalog-select-multiple__catalogs se-nowrap-ellipsis" [name]="'productCatalogVersions'" readonly="readonly"/> <span class="hyicon hyicon-optionssm se-products-catalog-select-multiple__icon"></span></div><div class="se-product-catalogs-tooltip" se-tooltip-body><div class="se-product-catalogs-tooltip__h">{{ ('se.product.catalogs.selector.headline.tooltip' || '') | translate }}</div><div class="se-product-catalog-info" *ngFor="let productCatalog of productCatalogs">{{ getCatalogNameCatalogVersionLabel(productCatalog.catalogId) | async }}</div></div></se-tooltip>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.L10nPipe,
        smarteditcommons.IModalService,
        smarteditcommons.SystemEventService])
], MultiProductCatalogVersionSelectorComponent);

window.__smartedit__.addDecoratorPayload("Component", "ProductCatalogVersionsSelectorComponent", {
    selector: 'se-product-catalog-versions-selector',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-product-catalog-versions-selector]': 'true'
    },
    template: `<ng-container *ngIf="isReady"><se-select *ngIf="isSingleVersionSelector" [id]="geData.qualifier" [(model)]="geData.model.productCatalogVersions[0]" [(reset)]="reset" [fetchStrategy]="fetchStrategy"></se-select><se-multi-product-catalog-version-selector *ngIf="isMultiVersionSelector" [productCatalogs]="productCatalogs" [(selectedProductCatalogVersions)]="geData.model[geData.qualifier]"></se-multi-product-catalog-version-selector></ng-container>`
});
let ProductCatalogVersionsSelectorComponent = class ProductCatalogVersionsSelectorComponent {
    constructor(geData, catalogService, systemEventService, cdr) {
        this.geData = geData;
        this.catalogService = catalogService;
        this.systemEventService = systemEventService;
        this.cdr = cdr;
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.contentCatalogVersionId = lo.cloneDeep(this.geData.model.previewCatalog);
            if (this.contentCatalogVersionId) {
                this.isReady = false;
                this.isSingleVersionSelector = false;
                this.isMultiVersionSelector = false;
                const eventId = (this.geData.id || '') + smarteditcommons.LINKED_DROPDOWN;
                this.$unRegSiteChangeEvent = this.systemEventService.subscribe(eventId, (id, data) => this.resetSelector(id, data));
                yield this.setContent();
            }
            return Promise.resolve();
        });
    }
    ngOnDestroy() {
        if (this.$unRegSiteChangeEvent) {
            this.$unRegSiteChangeEvent();
        }
    }
    resetSelector(_eventId, data) {
        return __awaiter(this, void 0, void 0, function* () {
            if (data.qualifier === 'previewCatalog' &&
                data.optionObject &&
                this.contentCatalogVersionId !== data.optionObject.id) {
                this.contentCatalogVersionId = data.optionObject.id;
                const siteUID = this.getSiteUIDFromContentCatalogVersionId(this.contentCatalogVersionId);
                const productCatalogs = yield this.catalogService.getProductCatalogsForSite(siteUID);
                const activeProductCatalogVersions = yield Promise.resolve(this.catalogService.returnActiveCatalogVersionUIDs(productCatalogs));
                this.geData.model[this.geData.qualifier] = activeProductCatalogVersions;
                if (this.isSingleVersionSelector) {
                    this.reset();
                }
                this.setContent();
            }
        });
    }
    setContent() {
        return __awaiter(this, void 0, void 0, function* () {
            const setContent = () => __awaiter(this, void 0, void 0, function* () {
                this.productCatalogs = yield this.catalogService.getProductCatalogsForSite(this.getSiteUIDFromContentCatalogVersionId(this.contentCatalogVersionId));
                if (this.productCatalogs.length === 0) {
                    return;
                }
                if (this.productCatalogs.length === 1) {
                    this.fetchStrategy = {
                        fetchAll: () => {
                            const parsedVersions = this.parseSingleCatalogVersion(this.productCatalogs[0].versions);
                            return Promise.resolve(parsedVersions);
                        }
                    };
                    this.isSingleVersionSelector = true;
                    this.isMultiVersionSelector = false;
                    this.isReady = true;
                    return;
                }
                this.isSingleVersionSelector = false;
                this.isMultiVersionSelector = true;
                this.isReady = true;
            });
            yield setContent();
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    getSiteUIDFromContentCatalogVersionId(id) {
        return id.split('|')[0];
    }
    parseSingleCatalogVersion(versions) {
        return versions.map((version) => ({
            id: version.uuid,
            label: version.version
        }));
    }
};
ProductCatalogVersionsSelectorComponent = __decorate([
    core.Component({
        selector: 'se-product-catalog-versions-selector',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        host: {
            '[class.se-product-catalog-versions-selector]': 'true'
        },
        template: `<ng-container *ngIf="isReady"><se-select *ngIf="isSingleVersionSelector" [id]="geData.qualifier" [(model)]="geData.model.productCatalogVersions[0]" [(reset)]="reset" [fetchStrategy]="fetchStrategy"></se-select><se-multi-product-catalog-version-selector *ngIf="isMultiVersionSelector" [productCatalogs]="productCatalogs" [(selectedProductCatalogVersions)]="geData.model[geData.qualifier]"></se-multi-product-catalog-version-selector></ng-container>`
    }),
    __param(0, core.Inject(smarteditcommons.GENERIC_EDITOR_WIDGET_DATA)),
    __metadata("design:paramtypes", [Object, smarteditcommons.ICatalogService,
        smarteditcommons.SystemEventService,
        core.ChangeDetectorRef])
], ProductCatalogVersionsSelectorComponent);

let ProductCatalogVersionModule = class ProductCatalogVersionModule {
};
ProductCatalogVersionModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            form.FormModule,
            smarteditcommons.L10nPipeModule,
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.SelectModule,
            smarteditcommons.TooltipModule
        ],
        declarations: [
            ProductCatalogVersionsSelectorComponent,
            MultiProductCatalogVersionConfigurationComponent,
            MultiProductCatalogVersionSelectorComponent
        ],
        entryComponents: [
            ProductCatalogVersionsSelectorComponent,
            MultiProductCatalogVersionConfigurationComponent,
            MultiProductCatalogVersionSelectorComponent
        ],
        providers: [
            smarteditcommons.moduleUtils.initialize((editorFieldMappingService) => {
                editorFieldMappingService.addFieldMapping('ProductCatalogVersionsSelector', null, null, {
                    component: ProductCatalogVersionsSelectorComponent
                });
            }, [smarteditcommons.EditorFieldMappingService])
        ]
    })
], ProductCatalogVersionModule);

window.__smartedit__.addDecoratorPayload("Component", "SitesLinkComponent", {
    selector: 'sites-link',
    template: `<div (click)="goToSites()" class="se-sites-link {{cssClass}}"><a class="se-sites-link__text">{{'se.toolbar.sites' | translate}}</a></div>`
});
let SitesLinkComponent = class SitesLinkComponent {
    constructor(routingService, iframeManagerService, userTrackingService) {
        this.routingService = routingService;
        this.iframeManagerService = iframeManagerService;
        this.userTrackingService = userTrackingService;
        this.iconCssClass = 'sap-icon--navigation-right-arrow';
    }
    goToSites() {
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.NAVIGATION, 'Sites');
        this.iframeManagerService.setCurrentLocation(null);
        this.routingService.go('/');
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SitesLinkComponent.prototype, "cssClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SitesLinkComponent.prototype, "iconCssClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SitesLinkComponent.prototype, "shortcutLink", void 0);
SitesLinkComponent = __decorate([
    core.Component({
        selector: 'sites-link',
        template: `<div (click)="goToSites()" class="se-sites-link {{cssClass}}"><a class="se-sites-link__text">{{'se.toolbar.sites' | translate}}</a></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.SmarteditRoutingService,
        IframeManagerService,
        smarteditcommons.IUserTrackingService])
], SitesLinkComponent);

/**
 * Used to adjust the width of page tree continer in storefrontPage
 */
let ResizerDirective = class ResizerDirective {
    constructor(storageService, yjQuery) {
        this.storageService = storageService;
        this.yjQuery = yjQuery;
        this.instanceId = 'rsz_' + new Date().getTime();
    }
    onMouseDown($event) {
        $event.preventDefault();
        const resizeElement = this.yjQuery(this.resizer);
        const startTransition = resizeElement.css('transition');
        const startPos = {
            x: $event.clientX,
            width: resizeElement.width()
        };
        resizeElement.css('transition', 'none');
        const mousemove = `mousemove.${this.instanceId}`;
        const mouseup = `mouseup.${this.instanceId}`;
        const selectstart = `selectstart.${this.instanceId}`;
        this.yjQuery(document).on(mousemove, (event) => {
            const e = event;
            const newWidth = startPos.width + e.clientX - startPos.x;
            resizeElement.width(newWidth);
        });
        this.yjQuery(document).on(mouseup, (event) => {
            event.stopPropagation();
            event.preventDefault();
            this.yjQuery(document).off(mousemove);
            this.yjQuery(document).off(mouseup);
            this.yjQuery(document).off(selectstart);
            resizeElement.css('transition', startTransition);
            this.yjQuery('iframe').css('pointer-events', 'auto');
            this.storageService.setValueInLocalStorage(smarteditcommons.PAGE_TREE_PANEL_WIDTH_COOKIE_NAME, resizeElement.outerWidth(), true);
        });
        this.yjQuery(document).on(selectstart, (event) => {
            event.stopPropagation();
            event.preventDefault();
        });
        this.yjQuery('iframe').css('pointer-events', 'none');
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], ResizerDirective.prototype, "resizer", void 0);
__decorate([
    core.HostListener('mousedown', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [MouseEvent]),
    __metadata("design:returntype", void 0)
], ResizerDirective.prototype, "onMouseDown", null);
ResizerDirective = __decorate([
    core.Directive({
        selector: '[resizer]'
    }),
    __param(1, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IStorageService, Function])
], ResizerDirective);

window.__smartedit__.addDecoratorPayload("Component", "PerspectiveSelectorComponent", {
    selector: 'se-perspective-selector',
    template: `<fd-popover [(isOpen)]="isOpen" [closeOnOutsideClick]="false" [triggers]="['click']" *ngIf="hasActivePerspective()" class="se-perspective-selector" [placement]="'bottom-end'" [disabled]="isDisabled" [options]="popperOptions"><fd-popover-control><div class="se-perspective-selector__trigger"><se-tooltip [isChevronVisible]="true" [triggers]="['mouseenter', 'mouseleave']" *ngIf="isTooltipVisible()"><span se-tooltip-trigger id="perspectiveTooltip" class="sap-icon--hint se-perspective-selector__hotkey-tooltip--icon"></span><div se-tooltip-body>{{ activePerspective.descriptionI18nKey | translate }}</div></se-tooltip><button class="se-perspective-selector__btn" [disabled]="isDisabled">{{getActivePerspectiveName() | translate}} <span class="se-perspective-selector__btn-arrow sap-icon--navigation-down-arrow" title="{{ getNavigationArrowIconIsCollapse() | translate }}"></span></button></div></fd-popover-control><fd-popover-body><ul fd-list class="se-perspective__list" [ngClass]="{'se-perspective__list--tooltip': isTooltipVisible()}" role="menu"><li fd-list-item *ngFor="let choice of getDisplayedPerspectives()" class="se-perspective__list-item fd-has-padding-none"><a fd-list-link class="fd-menu__link se-perspective__list-item-text" (click)="selectPerspective($event, choice.key);">{{choice.nameI18nKey | translate}}</a></li></ul></fd-popover-body></fd-popover>`
});
let PerspectiveSelectorComponent = class PerspectiveSelectorComponent {
    constructor(logService, yjQuery, perspectiveService, iframeClickDetectionService, systemEventService, crossFrameEventService, userTrackingService) {
        this.logService = logService;
        this.yjQuery = yjQuery;
        this.perspectiveService = perspectiveService;
        this.iframeClickDetectionService = iframeClickDetectionService;
        this.systemEventService = systemEventService;
        this.crossFrameEventService = crossFrameEventService;
        this.userTrackingService = userTrackingService;
        this.isOpen = false;
        this.popperOptions = {
            placement: 'bottom-start',
            modifiers: {
                preventOverflow: {
                    enabled: true,
                    escapeWithReference: true,
                    boundariesElement: 'viewport'
                },
                applyStyle: {
                    gpuAcceleration: false
                }
            }
        };
        this.isDisabled = false;
        this.perspectives = [];
        this.displayedPerspectives = [];
        this.activePerspective = null;
    }
    onDocumentClick(event) {
        this.onClickHandler(event);
    }
    ngOnInit() {
        this.activePerspective = null;
        this.iframeClickDetectionService.registerCallback('perspectiveSelectorClose', () => this.closeDropdown());
        this.unRegOverlayDisabledFn = this.systemEventService.subscribe('OVERLAY_DISABLED', () => this.closeDropdown());
        this.unRegPerspectiveAddedFn = this.systemEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_ADDED, () => this.onPerspectiveAdded());
        this.unRegPerspectiveChgFn = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_CHANGED, () => this.refreshPerspectives());
        this.unRegPerspectiveRefreshFn = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_REFRESHED, () => this.refreshPerspectives());
        this.unRegUserHasChanged = this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, () => this.onPerspectiveAdded());
        this.unRegStrictPreviewModeToggleFn = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_STRICT_PREVIEW_MODE_REQUESTED, (eventId, isDisabled) => this.togglePerspectiveSelector(isDisabled));
        this.onPerspectiveAdded();
    }
    ngOnDestroy() {
        this.unRegOverlayDisabledFn();
        this.unRegPerspectiveAddedFn();
        this.unRegPerspectiveChgFn();
        this.unRegPerspectiveRefreshFn();
        this.unRegUserHasChanged();
        this.unRegStrictPreviewModeToggleFn();
    }
    selectPerspective(event, choice) {
        event.preventDefault();
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.SELECT_PERSPECTIVE, choice);
        try {
            this.perspectiveService.switchTo(choice);
            this.closeDropdown();
        }
        catch (e) {
            this.logService.error('selectPerspective() - Cannot select perspective.', e);
        }
    }
    getDisplayedPerspectives() {
        return this.displayedPerspectives;
    }
    getActivePerspectiveName() {
        return this.activePerspective ? this.activePerspective.nameI18nKey : '';
    }
    hasActivePerspective() {
        return this.activePerspective !== null;
    }
    isTooltipVisible() {
        return (!!this.activePerspective &&
            !!this.activePerspective.descriptionI18nKey &&
            this.checkTooltipVisibilityCondition());
    }
    getNavigationArrowIconIsCollapse() {
        return this.isOpen ? 'se.arrowicon.collapse.title' : 'se.arrowicon.expand.title';
    }
    checkTooltipVisibilityCondition() {
        if (this.activePerspective.key !== smarteditcommons.NONE_PERSPECTIVE ||
            (this.activePerspective.key === smarteditcommons.NONE_PERSPECTIVE && this.isDisabled)) {
            return true;
        }
        return false;
    }
    filterPerspectives(perspectives) {
        return perspectives.filter((perspective) => {
            const isActivePerspective = this.activePerspective && perspective.key === this.activePerspective.key;
            const isAllPerspective = perspective.key === smarteditcommons.ALL_PERSPECTIVE;
            return !isActivePerspective && !isAllPerspective;
        });
    }
    closeDropdown() {
        this.isOpen = false;
    }
    onPerspectiveAdded() {
        this.perspectiveService.getPerspectives().then((result) => {
            this.perspectives = result;
            this.displayedPerspectives = this.filterPerspectives(this.perspectives);
        });
    }
    refreshPerspectives() {
        this.perspectiveService.getPerspectives().then((result) => {
            this.perspectives = result;
            this._refreshActivePerspective();
            this.displayedPerspectives = this.filterPerspectives(this.perspectives);
        });
    }
    _refreshActivePerspective() {
        this.activePerspective = this.perspectiveService.getActivePerspective();
    }
    onClickHandler(event) {
        if (this.yjQuery(event.target).parents('.se-perspective-selector').length <= 0 &&
            this.isOpen) {
            this.closeDropdown();
        }
    }
    togglePerspectiveSelector(value) {
        this.isDisabled = value;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], PerspectiveSelectorComponent.prototype, "isOpen", void 0);
__decorate([
    core.HostListener('document:click', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Event]),
    __metadata("design:returntype", void 0)
], PerspectiveSelectorComponent.prototype, "onDocumentClick", null);
PerspectiveSelectorComponent = __decorate([
    core.Component({
        selector: 'se-perspective-selector',
        template: `<fd-popover [(isOpen)]="isOpen" [closeOnOutsideClick]="false" [triggers]="['click']" *ngIf="hasActivePerspective()" class="se-perspective-selector" [placement]="'bottom-end'" [disabled]="isDisabled" [options]="popperOptions"><fd-popover-control><div class="se-perspective-selector__trigger"><se-tooltip [isChevronVisible]="true" [triggers]="['mouseenter', 'mouseleave']" *ngIf="isTooltipVisible()"><span se-tooltip-trigger id="perspectiveTooltip" class="sap-icon--hint se-perspective-selector__hotkey-tooltip--icon"></span><div se-tooltip-body>{{ activePerspective.descriptionI18nKey | translate }}</div></se-tooltip><button class="se-perspective-selector__btn" [disabled]="isDisabled">{{getActivePerspectiveName() | translate}} <span class="se-perspective-selector__btn-arrow sap-icon--navigation-down-arrow" title="{{ getNavigationArrowIconIsCollapse() | translate }}"></span></button></div></fd-popover-control><fd-popover-body><ul fd-list class="se-perspective__list" [ngClass]="{'se-perspective__list--tooltip': isTooltipVisible()}" role="menu"><li fd-list-item *ngFor="let choice of getDisplayedPerspectives()" class="se-perspective__list-item fd-has-padding-none"><a fd-list-link class="fd-menu__link se-perspective__list-item-text" (click)="selectPerspective($event, choice.key);">{{choice.nameI18nKey | translate}}</a></li></ul></fd-popover-body></fd-popover>`
    }),
    __param(1, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __param(5, core.Inject(smarteditcommons.EVENT_SERVICE)),
    __metadata("design:paramtypes", [smarteditcommons.LogService, Function, smarteditcommons.IPerspectiveService,
        smarteditcommons.IIframeClickDetectionService,
        smarteditcommons.SystemEventService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.IUserTrackingService])
], PerspectiveSelectorComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogHierarchyModalComponent", {
    selector: 'se-catalog-hierarchy-modal',
    template: `<div class="se-catalog-hierarchy-header"><span>{{ 'se.catalog.hierarchy.modal.tree.header' | translate }}</span></div><div *ngIf="this.catalogs$ | async as catalogs" class="se-catalog-hierarchy-body"><se-catalog-hierarchy-node *ngFor="let catalog of catalogs; let i = index" [catalog]="catalog" [index]="i" [isLast]="i === catalogs.length - 1" [siteId]="siteId" (siteSelect)="onSiteSelected()"></se-catalog-hierarchy-node></div>`
});
let CatalogHierarchyModalComponent = class CatalogHierarchyModalComponent {
    constructor(modalService) {
        this.modalService = modalService;
    }
    ngOnInit() {
        this.catalogs$ = this.modalService
            .getModalData()
            .pipe(operators.take(1))
            .toPromise()
            .then(({ catalog }) => [...catalog.parents, catalog]);
    }
    onSiteSelected() {
        this.modalService.close();
    }
};
CatalogHierarchyModalComponent = __decorate([
    core.Component({
        selector: 'se-catalog-hierarchy-modal',
        template: `<div class="se-catalog-hierarchy-header"><span>{{ 'se.catalog.hierarchy.modal.tree.header' | translate }}</span></div><div *ngIf="this.catalogs$ | async as catalogs" class="se-catalog-hierarchy-body"><se-catalog-hierarchy-node *ngFor="let catalog of catalogs; let i = index" [catalog]="catalog" [index]="i" [isLast]="i === catalogs.length - 1" [siteId]="siteId" (siteSelect)="onSiteSelected()"></se-catalog-hierarchy-node></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.ModalManagerService])
], CatalogHierarchyModalComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogDetailsComponent", {
    selector: 'se-catalog-details',
    template: `<div class="se-catalog-details" [attr.data-catalog]="catalog.name | seL10n | async"><div class="se-catalog-header"><div class="se-catalog-header-flex"><div class="se-catalog-details__header">{{ catalog.name | seL10n | async }}</div><div *ngIf="catalog.parents?.length"><a fd-link href="javascript:void(0)" (click)="onOpenCatalogHierarchy()">{{ 'se.landingpage.catalog.hierarchy' | translate }}</a></div></div></div><div class="se-catalog-details__content"><se-catalog-version-details *ngFor="let catalogVersion of sortedCatalogVersions" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion" [siteId]="siteId"></se-catalog-version-details></div></div>`
});
let CatalogDetailsComponent = class CatalogDetailsComponent {
    constructor(modalService) {
        this.modalService = modalService;
        this.catalogDividerImage = 'static-resources/images/icon_catalog_arrow.png';
    }
    ngOnInit() {
        this.activeCatalogVersion = this.catalog.versions.find((catalogVersion) => catalogVersion.active);
        this.sortedCatalogVersions = this.getSortedCatalogVersions();
        this.collapsibleConfiguration = {
            expandedByDefault: this.isCatalogForCurrentSite
        };
    }
    onOpenCatalogHierarchy() {
        this.modalService.open({
            component: CatalogHierarchyModalComponent,
            data: {
                catalog: this.catalog
            },
            templateConfig: {
                title: 'se.catalog.hierarchy.modal.title',
                isDismissButtonVisible: true
            }
        });
    }
    getSortedCatalogVersions() {
        return [
            this.activeCatalogVersion,
            ...this.catalog.versions.filter((catalogVersion) => !catalogVersion.active)
        ];
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogDetailsComponent.prototype, "catalog", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], CatalogDetailsComponent.prototype, "isCatalogForCurrentSite", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogDetailsComponent.prototype, "siteId", void 0);
CatalogDetailsComponent = __decorate([
    core.Component({
        selector: 'se-catalog-details',
        template: `<div class="se-catalog-details" [attr.data-catalog]="catalog.name | seL10n | async"><div class="se-catalog-header"><div class="se-catalog-header-flex"><div class="se-catalog-details__header">{{ catalog.name | seL10n | async }}</div><div *ngIf="catalog.parents?.length"><a fd-link href="javascript:void(0)" (click)="onOpenCatalogHierarchy()">{{ 'se.landingpage.catalog.hierarchy' | translate }}</a></div></div></div><div class="se-catalog-details__content"><se-catalog-version-details *ngFor="let catalogVersion of sortedCatalogVersions" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion" [siteId]="siteId"></se-catalog-version-details></div></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.ModalService])
], CatalogDetailsComponent);

/**
 * @ignore
 * @internal
 *
 * Navigates to a site with the given site id.
 */
let /* @ngInject */ CatalogNavigateToSite = class /* @ngInject */ CatalogNavigateToSite {
    constructor(systemEvent) {
        this.systemEvent = systemEvent;
    }
    navigate(siteId) {
        this.systemEvent.publishAsync(SITES_ID + smarteditcommons.LINKED_DROPDOWN, {
            qualifier: 'site',
            optionObject: {
                contentCatalogs: [],
                uid: siteId,
                id: siteId,
                label: {},
                name: {},
                previewUrl: ''
            }
        });
    }
};
/* @ngInject */ CatalogNavigateToSite = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService])
], /* @ngInject */ CatalogNavigateToSite);

window.__smartedit__.addDecoratorPayload("Component", "CatalogHierarchyNodeMenuItemComponent", {
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    selector: 'se-catalog-hierarchy-node-menu-item',
    template: `
        <a fd-list-link class="se-dropdown-item" (click)="onSiteSelect()">
            {{ name | seL10n | async }}
        </a>
    `
});
let CatalogHierarchyNodeMenuItemComponent = class CatalogHierarchyNodeMenuItemComponent {
    constructor(activateSite, data) {
        this.activateSite = activateSite;
        const { name, uid } = data.dropdownItem;
        this.name = name;
        this.uid = uid;
    }
    onSiteSelect() {
        this.activateSite.navigate(this.uid);
    }
};
CatalogHierarchyNodeMenuItemComponent = __decorate([
    core.Component({
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        selector: 'se-catalog-hierarchy-node-menu-item',
        template: `
        <a fd-list-link class="se-dropdown-item" (click)="onSiteSelect()">
            {{ name | seL10n | async }}
        </a>
    `
    }),
    __param(1, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [CatalogNavigateToSite, Object])
], CatalogHierarchyNodeMenuItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogHierarchyNodeComponent", {
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    selector: 'se-catalog-hierarchy-node',
    template: `<div class="se-cth-node-name" [style.padding-left.px]="15 * index" [style.padding-right.px]="15 * index" [class.se-cth-node-name__last]="isLast"><fd-icon glyph="navigation-down-arrow"></fd-icon>{{ (isLast ? catalog.name : catalog.catalogName) | seL10n | async }}&nbsp; <span *ngIf="isLast">({{ 'se.catalog.hierarchy.modal.tree.this.catalog' | translate}})</span></div><div class="se-cth-node-sites"><ng-container><ng-container *ngIf="hasOneSite; else hasManySites"><a class="se-cth-node-anchor" href="" (click)="onNavigateToSite(catalog.sites[0].uid)">{{ catalog.sites[0].name | seL10n | async }}<fd-icon glyph="navigation-right-arrow"></fd-icon></a></ng-container><ng-template #hasManySites><se-dropdown-menu [dropdownItems]="dropdownItems" useProjectedAnchor="1" (selectedItemChange)="siteSelect.emit()"><span class="se-cth-node-anchor">{{ this.catalog.sites.length }} Sites<fd-icon glyph="navigation-down-arrow"></fd-icon></span></se-dropdown-menu></ng-template></ng-container></div>`
});
let CatalogHierarchyNodeComponent = class CatalogHierarchyNodeComponent {
    constructor(navigateToSite) {
        this.navigateToSite = navigateToSite;
        this.siteSelect = new core.EventEmitter();
    }
    ngOnChanges() {
        this.dropdownItems = this.getDropdownItems();
    }
    onNavigateToSite(siteUid) {
        this.navigateToSite.navigate(siteUid);
        this.siteSelect.emit();
    }
    get hasOneSite() {
        return this.catalog.sites.length === 1;
    }
    getDropdownItems() {
        return this.catalog.sites.map((site) => (Object.assign(Object.assign({}, site), { component: CatalogHierarchyNodeMenuItemComponent })));
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Number)
], CatalogHierarchyNodeComponent.prototype, "index", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogHierarchyNodeComponent.prototype, "catalog", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogHierarchyNodeComponent.prototype, "siteId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], CatalogHierarchyNodeComponent.prototype, "isLast", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], CatalogHierarchyNodeComponent.prototype, "siteSelect", void 0);
CatalogHierarchyNodeComponent = __decorate([
    core.Component({
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        selector: 'se-catalog-hierarchy-node',
        template: `<div class="se-cth-node-name" [style.padding-left.px]="15 * index" [style.padding-right.px]="15 * index" [class.se-cth-node-name__last]="isLast"><fd-icon glyph="navigation-down-arrow"></fd-icon>{{ (isLast ? catalog.name : catalog.catalogName) | seL10n | async }}&nbsp; <span *ngIf="isLast">({{ 'se.catalog.hierarchy.modal.tree.this.catalog' | translate}})</span></div><div class="se-cth-node-sites"><ng-container><ng-container *ngIf="hasOneSite; else hasManySites"><a class="se-cth-node-anchor" href="" (click)="onNavigateToSite(catalog.sites[0].uid)">{{ catalog.sites[0].name | seL10n | async }}<fd-icon glyph="navigation-right-arrow"></fd-icon></a></ng-container><ng-template #hasManySites><se-dropdown-menu [dropdownItems]="dropdownItems" useProjectedAnchor="1" (selectedItemChange)="siteSelect.emit()"><span class="se-cth-node-anchor">{{ this.catalog.sites.length }} Sites<fd-icon glyph="navigation-down-arrow"></fd-icon></span></se-dropdown-menu></ng-template></ng-container></div>`
    }),
    __metadata("design:paramtypes", [CatalogNavigateToSite])
], CatalogHierarchyNodeComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogVersionDetailsComponent", {
    selector: 'se-catalog-version-details',
    template: `<div class="se-catalog-version-container" [attr.data-catalog-version]="catalogVersion.version"><div class="se-catalog-version-container__left"><se-catalog-versions-thumbnail-carousel [catalogVersion]="catalogVersion" [catalog]="catalog" [siteId]="siteId"></se-catalog-versions-thumbnail-carousel><div><div class="se-catalog-version-container__name">{{catalogVersion.version}}</div><div class="se-catalog-version-container__left__templates"><div class="se-catalog-version-container__left__template" *ngFor="let item of leftItems; let isLast = last"><se-catalog-version-item-renderer [item]="item" [siteId]="siteId" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion"></se-catalog-version-item-renderer><div class="se-catalog-version-container__divider" *ngIf="!isLast"></div></div></div></div></div><div class="se-catalog-version-container__right"><div class="se-catalog-version-container__right__template" *ngFor="let item of rightItems"><se-catalog-version-item-renderer [item]="item" [siteId]="siteId" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion"></se-catalog-version-item-renderer></div></div></div>`
});
let CatalogVersionDetailsComponent = class CatalogVersionDetailsComponent {
    constructor(catalogDetailsService) {
        this.catalogDetailsService = catalogDetailsService;
    }
    ngOnInit() {
        const { left, right } = this.catalogDetailsService.getItems();
        this.leftItems = left;
        this.rightItems = right;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionDetailsComponent.prototype, "catalog", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionDetailsComponent.prototype, "catalogVersion", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionDetailsComponent.prototype, "activeCatalogVersion", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogVersionDetailsComponent.prototype, "siteId", void 0);
CatalogVersionDetailsComponent = __decorate([
    core.Component({
        selector: 'se-catalog-version-details',
        template: `<div class="se-catalog-version-container" [attr.data-catalog-version]="catalogVersion.version"><div class="se-catalog-version-container__left"><se-catalog-versions-thumbnail-carousel [catalogVersion]="catalogVersion" [catalog]="catalog" [siteId]="siteId"></se-catalog-versions-thumbnail-carousel><div><div class="se-catalog-version-container__name">{{catalogVersion.version}}</div><div class="se-catalog-version-container__left__templates"><div class="se-catalog-version-container__left__template" *ngFor="let item of leftItems; let isLast = last"><se-catalog-version-item-renderer [item]="item" [siteId]="siteId" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion"></se-catalog-version-item-renderer><div class="se-catalog-version-container__divider" *ngIf="!isLast"></div></div></div></div></div><div class="se-catalog-version-container__right"><div class="se-catalog-version-container__right__template" *ngFor="let item of rightItems"><se-catalog-version-item-renderer [item]="item" [siteId]="siteId" [catalog]="catalog" [catalogVersion]="catalogVersion" [activeCatalogVersion]="activeCatalogVersion"></se-catalog-version-item-renderer></div></div></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogDetailsService])
], CatalogVersionDetailsComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
window.__smartedit__.addDecoratorPayload("Component", "CatalogVersionItemRendererComponent", {
    selector: 'se-catalog-version-item-renderer',
    template: `
        <ng-container>
            <div *ngIf="item.component">
                <ng-container
                    *ngComponentOutlet="item.component; injector: itemInjector"></ng-container>
            </div>
        </ng-container>
    `
});
let CatalogVersionItemRendererComponent = class CatalogVersionItemRendererComponent {
    constructor(injector) {
        this.injector = injector;
    }
    ngOnInit() {
        this.createInjector();
    }
    ngOnChanges() {
        this.createInjector();
    }
    createInjector() {
        if (!this.item.component) {
            return;
        }
        this.itemInjector = core.Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: smarteditcommons.CATALOG_DETAILS_ITEM_DATA,
                    useValue: {
                        siteId: this.siteId,
                        catalog: this.catalog,
                        catalogVersion: this.catalogVersion,
                        activeCatalogVersion: this.activeCatalogVersion
                    }
                }
            ]
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionItemRendererComponent.prototype, "item", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionItemRendererComponent.prototype, "catalog", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionItemRendererComponent.prototype, "catalogVersion", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionItemRendererComponent.prototype, "activeCatalogVersion", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogVersionItemRendererComponent.prototype, "siteId", void 0);
CatalogVersionItemRendererComponent = __decorate([
    core.Component({
        selector: 'se-catalog-version-item-renderer',
        template: `
        <ng-container>
            <div *ngIf="item.component">
                <ng-container
                    *ngComponentOutlet="item.component; injector: itemInjector"></ng-container>
            </div>
        </ng-container>
    `
    }),
    __metadata("design:paramtypes", [core.Injector])
], CatalogVersionItemRendererComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogVersionsThumbnailCarouselComponent", {
    selector: 'se-catalog-versions-thumbnail-carousel',
    template: `<div class="se-active-catalog-thumbnail"><div class="se-active-catalog-version-container__thumbnail" (click)="onClick()"><div class="se-active-catalog-version-container__thumbnail__default-img"><div class="se-active-catalog-version-container__thumbnail__img" [ngStyle]="{'background-image': 'url(' + catalogVersion.thumbnailUrl + ')'}"></div></div></div></div>`
});
let CatalogVersionsThumbnailCarouselComponent = class CatalogVersionsThumbnailCarouselComponent {
    constructor(experienceService) {
        this.experienceService = experienceService;
    }
    onClick() {
        this.experienceService.loadExperience({
            siteId: this.siteId,
            catalogId: this.catalog.catalogId,
            catalogVersion: this.catalogVersion.version
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionsThumbnailCarouselComponent.prototype, "catalog", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], CatalogVersionsThumbnailCarouselComponent.prototype, "catalogVersion", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogVersionsThumbnailCarouselComponent.prototype, "siteId", void 0);
CatalogVersionsThumbnailCarouselComponent = __decorate([
    core.Component({
        selector: 'se-catalog-versions-thumbnail-carousel',
        template: `<div class="se-active-catalog-thumbnail"><div class="se-active-catalog-version-container__thumbnail" (click)="onClick()"><div class="se-active-catalog-version-container__thumbnail__default-img"><div class="se-active-catalog-version-container__thumbnail__img" [ngStyle]="{'background-image': 'url(' + catalogVersion.thumbnailUrl + ')'}"></div></div></div></div>`
    }),
    __metadata("design:paramtypes", [smarteditcommons.IExperienceService])
], CatalogVersionsThumbnailCarouselComponent);

window.__smartedit__.addDecoratorPayload("Component", "HomePageLinkComponent", {
    selector: 'se-home-page-link',
    template: `<div class="home-link-container"><a href="javascript:void(0)" class="fd-link home-link-item__link se-catalog-version__link" (click)="onClick()">{{ 'se.landingpage.homepage' | translate }}</a></div>`
});
let HomePageLinkComponent = class HomePageLinkComponent {
    constructor(experienceService, userTrackingService, data) {
        this.experienceService = experienceService;
        this.userTrackingService = userTrackingService;
        this.data = data;
    }
    onClick() {
        const { siteId, catalog: { catalogId }, catalogVersion: { version: catalogVersion } } = this.data;
        this.experienceService.loadExperience({
            siteId,
            catalogId,
            catalogVersion
        });
        this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.NAVIGATION, catalogVersion + ' - HomePage');
    }
};
HomePageLinkComponent = __decorate([
    core.Component({
        selector: 'se-home-page-link',
        template: `<div class="home-link-container"><a href="javascript:void(0)" class="fd-link home-link-item__link se-catalog-version__link" (click)="onClick()">{{ 'se.landingpage.homepage' | translate }}</a></div>`
    }),
    __param(2, core.Inject(smarteditcommons.CATALOG_DETAILS_ITEM_DATA)),
    __metadata("design:paramtypes", [smarteditcommons.IExperienceService,
        smarteditcommons.IUserTrackingService, Object])
], HomePageLinkComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * The catalog details Service makes it possible to add items in form of directive
 * to the catalog details directive
 *
 */
class CatalogDetailsService {
    constructor() {
        this._customItems = {
            left: [
                {
                    component: HomePageLinkComponent
                }
            ],
            right: []
        };
    }
    /**
     * This method allows to add a new item/items to the template array.
     *
     * @param  items An array that hold a list of items.
     * @param  column The place where the template will be added to. If this value is empty
     * the template will be added to the left side by default. The available places are defined in the
     * constant CATALOG_DETAILS_COLUMNS
     */
    addItems(items, column) {
        if (column === smarteditcommons.CATALOG_DETAILS_COLUMNS.RIGHT) {
            this._customItems.right = this._customItems.right.concat(items);
        }
        else {
            this._customItems.left = this._customItems.left.splice(0);
            this._customItems.left.splice(this._customItems.left.length - 1, 0, ...items);
        }
    }
    /**
     * This retrieves the list of items currently extending catalog version details components.
     */
    getItems() {
        return this._customItems;
    }
}

/**
 * This module contains the {@link CatalogDetailsModule.component:catalogVersionDetails} component.
 */
let CatalogDetailsModule = class CatalogDetailsModule {
};
CatalogDetailsModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.CollapsibleContainerModule,
            smarteditcommons.L10nPipeModule,
            link.LinkModule,
            smarteditcommons.SharedComponentsModule,
            core$1.IconModule,
            core$2.TranslateModule.forChild(),
            list.ListModule
        ],
        providers: [
            { provide: smarteditcommons.ICatalogDetailsService, useClass: CatalogDetailsService },
            CatalogNavigateToSite
        ],
        declarations: [
            HomePageLinkComponent,
            CatalogDetailsComponent,
            CatalogVersionDetailsComponent,
            CatalogVersionsThumbnailCarouselComponent,
            CatalogVersionItemRendererComponent,
            CatalogHierarchyModalComponent,
            CatalogHierarchyNodeComponent,
            CatalogHierarchyNodeMenuItemComponent
        ],
        entryComponents: [
            CatalogVersionsThumbnailCarouselComponent,
            CatalogVersionDetailsComponent,
            HomePageLinkComponent,
            CatalogDetailsComponent,
            CatalogVersionItemRendererComponent,
            CatalogHierarchyModalComponent,
            CatalogHierarchyNodeComponent,
            CatalogHierarchyNodeMenuItemComponent
        ],
        exports: [CatalogDetailsComponent]
    })
], CatalogDetailsModule);

const smarteditContainerRoutes = [
    {
        path: '',
        shortcutComponent: SitesLinkComponent,
        component: LandingPageComponent
    },
    {
        path: 'sites/:siteId',
        component: LandingPageComponent
    },
    {
        path: 'storefront',
        canActivate: [StorefrontPageGuard],
        priority: 30,
        titleI18nKey: 'se.route.storefront.title',
        component: StorefrontPageComponent
    },
    {
        path: smarteditcommons.NG_ROUTE_WILDCARD,
        component: InvalidRouteComponent
    }
];
// https://stackoverflow.com/questions/38888008/how-can-i-use-create-dynamic-template-to-compile-dynamic-component-with-angular
const smarteditContainerFactory = (bootstrapPayload) => {
    let Smarteditcontainer = class Smarteditcontainer {
    };
    Smarteditcontainer = __decorate([
        core.NgModule({
            schemas: [core.CUSTOM_ELEMENTS_SCHEMA],
            imports: [
                platformBrowser.BrowserModule,
                animations$1.BrowserAnimationsModule,
                forms.FormsModule,
                forms.ReactiveFormsModule,
                http.HttpClientModule,
                smarteditcommons.FundamentalsModule,
                smarteditcommons.SeGenericEditorModule,
                smarteditcommons.SharedComponentsModule,
                smarteditcommons.ClientPagedListModule,
                smarteditcommons.FilterByFieldPipeModule,
                NotificationPanelModule,
                StorageModule.forRoot(),
                exports.ToolbarModule,
                CatalogDetailsModule,
                smarteditcommons.SelectModule,
                HotkeyNotificationModule,
                ProductCatalogVersionModule,
                smarteditcommons.ConfirmDialogModule,
                gridList.GridListModule,
                smarteditcommons.HttpInterceptorModule.forRoot(smarteditcommons.UnauthorizedErrorInterceptor, smarteditcommons.ResourceNotFoundErrorInterceptor, smarteditcommons.RetryInterceptor, smarteditcommons.NonValidationErrorInterceptor, smarteditcommons.PreviewErrorInterceptor, smarteditcommons.PermissionErrorInterceptor),
                smarteditcommons.SeTranslationModule.forRoot(exports.TranslationsFetchService),
                SmarteditServicesModule,
                ...bootstrapPayload.modules,
                smarteditcommons.SeRouteService.provideNgRoute(smarteditContainerRoutes, {
                    useHash: true,
                    initialNavigation: 'enabledNonBlocking',
                    onSameUrlNavigation: 'reload'
                })
            ],
            declarations: [
                SmarteditcontainerComponent,
                InvalidRouteComponent,
                SitesLinkComponent,
                ThemeSwitchComponent,
                AnnouncementComponent,
                AnnouncementBoardComponent,
                ConfigurationModalComponent,
                PerspectiveSelectorComponent,
                HeartBeatAlertComponent,
                LandingPageComponent,
                StorefrontPageComponent,
                PageTreeContainerComponent,
                ResizerDirective
            ],
            entryComponents: [
                SmarteditcontainerComponent,
                SitesLinkComponent,
                ThemeSwitchComponent,
                AnnouncementComponent,
                ConfigurationModalComponent,
                PerspectiveSelectorComponent,
                HeartBeatAlertComponent,
                LandingPageComponent,
                StorefrontPageComponent,
                PageTreeContainerComponent
            ],
            exports: [ResizerDirective],
            providers: [
                {
                    provide: core.ErrorHandler,
                    useClass: smarteditcommons.SmarteditErrorHandler
                },
                SiteService,
                smarteditcommons.SSOAuthenticationHelper,
                smarteditcommons.moduleUtils.provideValues(bootstrapPayload.constants),
                { provide: router.UrlHandlingStrategy, useClass: smarteditcommons.CustomHandlingStrategy },
                { provide: common.APP_BASE_HREF, useValue: '' },
                IframeManagerService,
                {
                    provide: smarteditcommons.IAuthenticationManagerService,
                    useClass: smarteditcommons.AuthenticationManager
                },
                {
                    provide: smarteditcommons.IAuthenticationService,
                    useClass: smarteditcommons.AuthenticationService
                },
                {
                    provide: smarteditcommons.IConfirmationModalService,
                    useClass: ConfirmationModalService
                },
                {
                    provide: smarteditcommons.ISharedDataService,
                    useClass: exports.SharedDataService
                },
                smarteditcommons.SeRouteService,
                {
                    provide: smarteditcommons.ISessionService,
                    useClass: exports.SessionService
                },
                {
                    provide: smarteditcommons.IStorageService,
                    useClass: exports.StorageService
                },
                {
                    provide: smarteditcommons.IUrlService,
                    useClass: UrlService
                },
                {
                    provide: smarteditcommons.IIframeClickDetectionService,
                    useClass: IframeClickDetectionService
                },
                {
                    provide: smarteditcommons.ICatalogService,
                    useClass: CatalogService
                },
                {
                    provide: smarteditcommons.IExperienceService,
                    useClass: ExperienceService
                },
                {
                    provide: smarteditcommons.IUserTrackingService,
                    useClass: UserTrackingService
                },
                smarteditcommons.SmarteditRoutingService,
                smarteditcommons.ContentCatalogRestService,
                smarteditcommons.ProductCatalogRestService,
                exports.LoadConfigManagerService,
                smarteditcommons.ThemesService,
                exports.ThemeSwitchService,
                smarteditcommons.moduleUtils.bootstrap(smarteditContainerBootstrap, [
                    smarteditcommons.IAuthenticationService,
                    smarteditcommons.GatewayProxy,
                    smarteditcommons.IFeatureService,
                    ConfigurationModalService,
                    smarteditcommons.IToolbarServiceFactory,
                    exports.LoadConfigManagerService,
                    smarteditcommons.GatewayFactory,
                    smarteditcommons.SmarteditBootstrapGateway,
                    smarteditcommons.YJQUERY_TOKEN,
                    IframeManagerService,
                    smarteditcommons.IWaitDialogService,
                    exports.ThemeSwitchService,
                    smarteditcommons.PromiseUtils,
                    exports.BootstrapService,
                    smarteditcommons.ISharedDataService,
                    smarteditcommons.IModalService,
                    smarteditcommons.IRenderService,
                    HeartBeatService,
                    smarteditcommons.SmarteditRoutingService,
                    smarteditcommons.IUserTrackingService
                ]),
                smarteditcommons.moduleUtils.initialize(smarteditContainerInitialize, [
                    exports.DelegateRestService,
                    http.HttpClient,
                    smarteditcommons.RestServiceFactory,
                    PermissionsRegistrationService,
                    smarteditcommons.IPermissionService,
                    smarteditcommons.AuthorizationService
                ])
            ],
            bootstrap: [SmarteditcontainerComponent]
        })
    ], Smarteditcontainer);
    return Smarteditcontainer;
};
function smarteditContainerBootstrap(auth, gatewayProxy, featureService, configurationModalService, toolbarServiceFactory, loadConfigManagerService, gatewayFactory, smarteditBootstrapGateway, yjQuery, iframeManagerService, waitDialogService, themeSwitchService, promiseUtils, bootstrapService, sharedDataService, modalService, renderService, heartBeatService, routingService, userTrackingService) {
    gatewayProxy.initForService(auth, [
        'filterEntryPoints',
        'isAuthEntryPoint',
        'authenticate',
        'logout',
        'isReAuthInProgress',
        'setReAuthInProgress',
        'isAuthenticated'
    ], smarteditcommons.diNameUtils.buildServiceName(smarteditcommons.AuthenticationService));
    gatewayFactory.initListener();
    routingService.init();
    routingService.routeChangeSuccess().subscribe((event) => {
        modalService.dismissAll();
    });
    loadConfigManagerService.loadAsObject().then((configurations) => {
        sharedDataService.set('defaultToolingLanguage', configurations.defaultToolingLanguage);
        sharedDataService.set('maxUploadFileSize', configurations.maxUploadFileSize);
        if (!!configurations.typeAheadDebounce) {
            sharedDataService.set('typeAheadDebounce', configurations.typeAheadDebounce);
        }
        if (!!configurations.typeAheadMiniSearchTermLength) {
            sharedDataService.set('typeAheadMiniSearchTermLength', configurations.typeAheadMiniSearchTermLength);
        }
    });
    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: smarteditcommons.PERSPECTIVE_SELECTOR_WIDGET_KEY,
        nameI18nKey: 'se.perspective.selector.widget',
        type: 'TEMPLATE',
        priority: 1,
        section: 'left',
        component: PerspectiveSelectorComponent
    });
    const smartEditHeaderToolbarService = toolbarServiceFactory.getToolbarService('smartEditHeaderToolbar');
    smartEditHeaderToolbarService.addItems([
        {
            key: 'headerToolbar.logoTemplate',
            type: smarteditcommons.ToolbarItemType.TEMPLATE,
            component: LogoComponent,
            priority: 1,
            section: smarteditcommons.ToolbarSection.left
        },
        {
            key: 'headerToolbar.userAccountTemplate',
            type: smarteditcommons.ToolbarItemType.HYBRID_ACTION,
            iconClassName: 'sap-icon--customer',
            component: UserAccountComponent,
            priority: 1,
            actionButtonFormat: 'compact',
            section: smarteditcommons.ToolbarSection.right,
            dropdownPosition: smarteditcommons.ToolbarDropDownPosition.right,
            nameI18nKey: 'se.toolbar.useraccount.button.arialabel'
        },
        {
            key: 'headerToolbar.languageSelectorTemplate',
            type: smarteditcommons.ToolbarItemType.HYBRID_ACTION,
            iconClassName: 'sap-icon--world',
            component: smarteditcommons.HeaderLanguageDropdownComponent,
            priority: 3,
            actionButtonFormat: 'compact',
            section: smarteditcommons.ToolbarSection.right,
            dropdownPosition: smarteditcommons.ToolbarDropDownPosition.center,
            nameI18nKey: 'se.languageselector.dropdown.button.arialabel'
        },
        {
            key: 'headerToolbar.configurationTemplate',
            type: smarteditcommons.ToolbarItemType.ACTION,
            actionButtonFormat: 'compact',
            iconClassName: 'sap-icon--action-settings',
            callback: () => {
                configurationModalService.editConfiguration();
            },
            priority: 4,
            section: smarteditcommons.ToolbarSection.right,
            permissions: ['smartedit.configurationcenter.read'],
            nameI18nKey: 'se.toolbar.configuration.button.arialabel'
        },
        {
            key: 'headerToolbar.qualtricTemplate',
            type: smarteditcommons.ToolbarItemType.TEMPLATE,
            priority: 2,
            section: smarteditcommons.ToolbarSection.right,
            component: QualtricsComponent
        }
    ]);
    const smartEditExperienceToolbarService = toolbarServiceFactory.getToolbarService('smartEditExperienceToolbar');
    smartEditExperienceToolbarService.addItems([
        {
            key: 'se.cms.shortcut',
            type: smarteditcommons.ToolbarItemType.TEMPLATE,
            component: smarteditcommons.ShortcutLinkComponent,
            priority: 1,
            section: smarteditcommons.ToolbarSection.left
        },
        {
            key: 'experienceToolbar.deviceSupportTemplate',
            type: smarteditcommons.ToolbarItemType.TEMPLATE,
            component: DeviceSupportWrapperComponent,
            priority: 1,
            section: smarteditcommons.ToolbarSection.right
        },
        {
            type: smarteditcommons.ToolbarItemType.TEMPLATE,
            key: 'experienceToolbar.experienceSelectorTemplate',
            // className: 'se-experience-selector',
            component: ExperienceSelectorWrapperComponent,
            priority: 1,
            section: smarteditcommons.ToolbarSection.middle
        }
    ]);
    function offSetStorefront() {
        // Set the storefront offset
        yjQuery(smarteditcommons.SMARTEDIT_IFRAME_WRAPPER_ID).css('padding-top', yjQuery('.se-toolbar-group').height() + 'px');
    }
    // storefront actually loads twice all the JS files, including webApplicationInjector.js, smartEdit must be protected against receiving twice a smartEditBootstrap event
    function getBootstrapNamespace() {
        /* forbiddenNameSpaces window._:false */
        if (window.__smartedit__.smartEditBootstrapped) {
            window.__smartedit__.smartEditBootstrapped = {};
        }
        return window.__smartedit__.smartEditBootstrapped;
    }
    smarteditBootstrapGateway.getInstance().subscribe('loading', (eventId, data) => {
        const deferred = promiseUtils.defer();
        iframeManagerService.setCurrentLocation(data.location);
        waitDialogService.showWaitModal();
        const smartEditBootstrapped = getBootstrapNamespace();
        delete smartEditBootstrapped[data.location];
        return deferred.promise;
    });
    smarteditBootstrapGateway.getInstance().subscribe('unloading', (eventId, data) => {
        const deferred = promiseUtils.defer();
        waitDialogService.showWaitModal();
        return deferred.promise;
    });
    smarteditBootstrapGateway
        .getInstance()
        .subscribe('bootstrapSmartEdit', (eventId, data) => {
        offSetStorefront();
        const deferred = promiseUtils.defer();
        const smartEditBootstrapped = getBootstrapNamespace();
        if (!smartEditBootstrapped[data.location]) {
            smartEditBootstrapped[data.location] = true;
            loadConfigManagerService.loadAsObject().then((configurations) => {
                bootstrapService.bootstrapSEApp(configurations);
                deferred.resolve();
            });
        }
        else {
            deferred.resolve();
        }
        return deferred.promise;
    });
    smarteditBootstrapGateway.getInstance().subscribe('smartEditReady', function () {
        const deferred = promiseUtils.defer();
        deferred.resolve();
        waitDialogService.hideWaitModal();
        return deferred.promise;
    });
    userTrackingService.initConfiguration();
}
function smarteditContainerInitialize(delegateRestService, httpClient, restServiceFactory, permissionsRegistrationService, permissionService, authorizationService) {
    permissionService.registerDefaultRule({
        names: [DEFAULT_DEFAULT_RULE_NAME],
        verify: (permissionNameObjs) => {
            const permissionNames = permissionNameObjs.map((permissionName) => permissionName.name);
            return authorizationService.hasGlobalPermissions(permissionNames);
        }
    });
    permissionsRegistrationService.registerRulesAndPermissions();
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
if (process.env.NODE_ENV === 'production') {
    core.enableProdMode();
}

exports.smarteditContainerFactory = smarteditContainerFactory;
