'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var http = require('@angular/common/http');
var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var link = require('@fundamental-ngx/core/link');
var lodash = require('lodash');
var personalizationcommons = require('personalizationcommons');
var smarteditcommons = require('smarteditcommons');
var smarteditcontainer = require('smarteditcontainer');
var common = require('@angular/common');
var core$1 = require('@ngx-translate/core');
var rxjs = require('rxjs');
var button = require('@fundamental-ngx/core/button');
var operators = require('rxjs/operators');
var form = require('@fundamental-ngx/core/form');
var inlineHelp = require('@fundamental-ngx/core/inline-help');
var forms = require('@angular/forms');
var checkbox = require('@fundamental-ngx/core/checkbox');
var _switch = require('@fundamental-ngx/core/switch');
var core$2 = require('@fundamental-ngx/core');
var dragDrop = require('@angular/cdk/drag-drop');
var busyIndicator = require('@fundamental-ngx/core/busy-indicator');
var menu = require('@fundamental-ngx/core/menu');

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

/* @ngInject */ exports.PersonalizationsmarteditContextService = class /* @ngInject */ PersonalizationsmarteditContextService {
    constructor(sharedDataService, loadConfigManagerService, personalizationsmarteditContextServiceProxy, personalizationsmarteditContextUtils) {
        this.sharedDataService = sharedDataService;
        this.loadConfigManagerService = loadConfigManagerService;
        this.personalizationsmarteditContextServiceProxy = personalizationsmarteditContextServiceProxy;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        const context = personalizationsmarteditContextUtils.getContextObject();
        this.setPersonalization(context.personalization);
        this.setCustomize(context.customize);
        this.setCombinedView(context.combinedView);
        this.setSeData(context.seData);
        this.customizeFiltersState = {};
    }
    getPersonalization() {
        return this.personalization;
    }
    setPersonalization(personalization) {
        this.personalization = personalization;
        this.personalizationsmarteditContextServiceProxy.setPersonalization(personalization);
    }
    getCustomize() {
        return this.customize;
    }
    setCustomize(customize) {
        this.customize = customize;
        this.personalizationsmarteditContextServiceProxy.setCustomize(customize);
        window.sessionStorage.setItem('Customize', JSON.stringify(customize));
    }
    getCombinedView() {
        return this.combinedView;
    }
    setCombinedView(combinedView) {
        this.combinedView = combinedView;
        this.personalizationsmarteditContextServiceProxy.setCombinedView(combinedView);
        window.sessionStorage.setItem('CombinedView', JSON.stringify(combinedView));
    }
    getSeData() {
        return this.seData;
    }
    setSeData(seData) {
        this.seData = seData;
        this.personalizationsmarteditContextServiceProxy.setSeData(seData);
    }
    refreshExperienceData() {
        return this.sharedDataService.get('experience').then((data) => {
            const seData = this.getSeData();
            seData.seExperienceData = data;
            seData.pageId = data.pageId;
            this.setSeData(seData);
            return Promise.resolve('ok');
        });
    }
    refreshConfigurationData() {
        this.loadConfigManagerService.loadAsObject().then((configurations) => {
            const seData = this.getSeData();
            seData.seConfigurationData = configurations;
            this.setSeData(seData);
        });
    }
    applySynchronization() {
        this.personalizationsmarteditContextServiceProxy.setPersonalization(this.personalization);
        this.personalizationsmarteditContextServiceProxy.setCustomize(this.customize);
        this.personalizationsmarteditContextServiceProxy.setCombinedView(this.combinedView);
        this.personalizationsmarteditContextServiceProxy.setSeData(this.seData);
        this.refreshExperienceData();
        this.refreshConfigurationData();
    }
    getContexServiceProxy() {
        return this.personalizationsmarteditContextServiceProxy;
    }
    getCustomizeFiltersState() {
        return this.customizeFiltersState;
    }
    setCustomizeFiltersState(filters) {
        this.customizeFiltersState = filters;
    }
};
/* @ngInject */ exports.PersonalizationsmarteditContextService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.ISharedDataService,
        smarteditcontainer.LoadConfigManagerService,
        personalizationcommons.IPersonalizationsmarteditContextServiceProxy,
        personalizationcommons.PersonalizationsmarteditContextUtils])
], /* @ngInject */ exports.PersonalizationsmarteditContextService);

var /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy_1;
/* @ngInject */ exports.PersonalizationsmarteditContextServiceReverseProxy = /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy_1 = class /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy {
    constructor(personalizationsmarteditContextService, workflowService, pageInfoService) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.workflowService = workflowService;
        this.pageInfoService = pageInfoService;
    }
    applySynchronization() {
        this.personalizationsmarteditContextService.applySynchronization();
    }
    isCurrentPageActiveWorkflowRunning() {
        return this.pageInfoService.getPageUUID().then((pageUuid) => this.workflowService.getActiveWorkflowForPageUuid(pageUuid).then((workflow) => {
            if (workflow == null) {
                return false;
            }
            return (workflow.status ===
                /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy_1.WORKFLOW_RUNNING_STATUS);
        }));
    }
};
/* @ngInject */ exports.PersonalizationsmarteditContextServiceReverseProxy.WORKFLOW_RUNNING_STATUS = 'RUNNING';
/* @ngInject */ exports.PersonalizationsmarteditContextServiceReverseProxy = /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy_1 = __decorate([
    smarteditcommons.GatewayProxied('applySynchronization', 'isCurrentPageActiveWorkflowRunning'),
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditContextService,
        smarteditcommons.WorkflowService,
        smarteditcommons.IPageInfoService])
], /* @ngInject */ exports.PersonalizationsmarteditContextServiceReverseProxy);

/* @ngInject */ exports.PersonalizationsmarteditPreviewService = class /* @ngInject */ PersonalizationsmarteditPreviewService {
    constructor(experienceService) {
        this.experienceService = experienceService;
    }
    removePersonalizationDataFromPreview() {
        return this.updatePreviewTicketWithVariations([]);
    }
    updatePreviewTicketWithVariations(variations) {
        return __awaiter(this, void 0, void 0, function* () {
            const experience = yield this.experienceService.getCurrentExperience();
            if (!experience) {
                return undefined;
            }
            if (JSON.stringify(experience.variations) === JSON.stringify(variations)) {
                return undefined;
            }
            experience.variations = variations;
            yield this.experienceService.setCurrentExperience(experience);
            return this.experienceService.updateExperience();
        });
    }
};
/* @ngInject */ exports.PersonalizationsmarteditPreviewService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IExperienceService])
], /* @ngInject */ exports.PersonalizationsmarteditPreviewService);

/* @ngInject */ exports.PersonalizationsmarteditContextServiceProxy = class /* @ngInject */ PersonalizationsmarteditContextServiceProxy extends personalizationcommons.IPersonalizationsmarteditContextServiceProxy {
    constructor() {
        super();
    }
};
/* @ngInject */ exports.PersonalizationsmarteditContextServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData'),
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ exports.PersonalizationsmarteditContextServiceProxy);

var /* @ngInject */ PersonalizationsmarteditRestService_1;
/* @ngInject */ exports.PersonalizationsmarteditRestService = /* @ngInject */ PersonalizationsmarteditRestService_1 = class /* @ngInject */ PersonalizationsmarteditRestService {
    constructor(restServiceFactory, personalizationsmarteditUtils, httpClient, yjQuery, personalizationsmarteditContextService) {
        this.restServiceFactory = restServiceFactory;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.httpClient = httpClient;
        this.yjQuery = yjQuery;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.actionHeaders = new http.HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' });
    }
    extendRequestParamObjWithCatalogAwarePathVariables(requestParam, catalogAware) {
        catalogAware = catalogAware || {};
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const catalogAwareParams = {
            catalogId: catalogAware.catalog || experienceData.catalogDescriptor.catalogId,
            catalogVersion: catalogAware.catalogVersion || experienceData.catalogDescriptor.catalogVersion
        };
        requestParam = lodash__namespace.assign(requestParam, catalogAwareParams);
        return requestParam;
    }
    extendRequestParamObjWithCustomizatonCode(requestParam, customizatiodCode) {
        const customizationCodeParam = {
            customizationCode: customizatiodCode
        };
        requestParam = lodash__namespace.assign(requestParam, customizationCodeParam);
        return requestParam;
    }
    extendRequestParamObjWithVariationCode(requestParam, variationCode) {
        const param = {
            variationCode
        };
        requestParam = lodash__namespace.assign(requestParam, param);
        return requestParam;
    }
    getParamsAction(oldComponentId, newComponentId, slotId, containerId, customizationId, variationId) {
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'oldComponentId', oldComponentId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'newComponentId', newComponentId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'slotId', slotId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'containerId', containerId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'variationId', variationId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'customizationId', customizationId);
        return {
            params: {
                entry: entries
            }
        };
    }
    getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        filter = filter || {};
        return {
            customizationCode: customizationId,
            variationCode: variationId,
            actionId,
            catalogId: filter.catalog || experienceData.catalogDescriptor.catalogId,
            catalogVersion: filter.catalogVersion || experienceData.catalogDescriptor.catalogVersion
        };
    }
    prepareURI(uri, pathVariables) {
        return uri.replace(/((?:\:)(\w*)(?:\/))/g, (match, p1, p2) => pathVariables[p2] + '/');
    }
    getParamsForCustomizations(filter) {
        return {
            code: filter.code,
            pageId: filter.pageId,
            pageCatalogId: filter.pageCatalogId,
            name: filter.name,
            negatePageId: filter.negatePageId,
            catalogs: filter.catalogs,
            statuses: lodash__namespace.isUndefined(filter.statuses) ? undefined : filter.statuses.join(',')
        };
    }
    getActionsDetails(filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS_DETAILS);
        filter = this.extendRequestParamObjWithCatalogAwarePathVariables(filter);
        return restService.get(filter);
    }
    getCustomizations(filter) {
        filter = filter || {};
        let requestParams = {};
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATIONS);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);
        requestParams.pageSize = filter.currentSize || 10;
        requestParams.currentPage = filter.currentPage || 0;
        this.yjQuery.extend(requestParams, this.getParamsForCustomizations(filter));
        return restService.get(requestParams);
    }
    getComponentsIdsForVariation(customizationId, variationId, catalog, catalogVersion) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CXCMSC_ACTIONS_FROM_VARIATIONS);
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'customization', customizationId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'variations', variationId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalog', catalog || experienceData.catalogDescriptor.catalogId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalogVersion', catalogVersion || experienceData.catalogDescriptor.catalogVersion);
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }
    getCxCmsActionsOnPageForCustomization(customization, currentPage) {
        const filter = {
            type: 'CXCMSACTION',
            catalogs: 'ALL',
            fields: 'FULL',
            pageId: this.personalizationsmarteditContextService.getSeData().pageId,
            pageCatalogId: this.personalizationsmarteditContextService.getSeData().seExperienceData.pageContext
                .catalogId,
            customizationCode: customization.code || '',
            currentPage: currentPage || 0
        };
        return this.getActionsDetails(filter);
    }
    getSegments(filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.SEGMENTS);
        return restService.get(filter);
    }
    getCustomization(filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION, 'customizationCode');
        let requestParams = this.extendRequestParamObjWithCustomizatonCode({}, filter.code);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);
        return restService.get(requestParams);
    }
    createCustomization(customization) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION_PACKAGES);
        return restService.save(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
    }
    updateCustomization(customization) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION, 'customizationCode');
        customization.customizationCode = customization.code;
        return restService.update(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
    }
    updateCustomizationPackage(customization) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION_PACKAGE, 'customizationCode');
        customization.customizationCode = customization.code;
        return restService.update(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
    }
    deleteCustomization(customizationCode) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION, 'customizationCode');
        const requestParams = {
            customizationCode
        };
        return restService.remove(this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams));
    }
    getVariation(customizationCode, variationCode) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.VARIATION, 'variationCode');
        let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
        requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);
        return restService.get(requestParams);
    }
    editVariation(customizationCode, variation) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.VARIATION, 'variationCode');
        variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
        variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);
        variation.variationCode = variation.code;
        return restService.update(variation);
    }
    deleteVariation(customizationCode, variationCode) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.VARIATION, 'variationCode');
        let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
        requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);
        return restService.remove(requestParams);
    }
    createVariationForCustomization(customizationCode, variation) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.VARIATIONS);
        variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
        variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);
        return restService.save(variation);
    }
    getVariationsForCustomization(customizationCode, filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.VARIATIONS);
        let requestParams = {};
        const varForCustFilter = filter || {};
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, varForCustFilter);
        requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);
        requestParams.fields =
            /* @ngInject */ PersonalizationsmarteditRestService_1.VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS;
        const includeFullFields = typeof varForCustFilter.includeFullFields === 'undefined'
            ? false
            : varForCustFilter.includeFullFields;
        if (includeFullFields) {
            requestParams.fields = /* @ngInject */ PersonalizationsmarteditRestService_1.FULL_FIELDS;
        }
        return restService.get(requestParams);
    }
    replaceComponentWithContainer(componentId, slotId, filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ADD_CONTAINER);
        const catalogParams = this.extendRequestParamObjWithCatalogAwarePathVariables({}, filter);
        const requestParams = this.getParamsAction(componentId, null, slotId, null, null, null);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, 'catalog', catalogParams.catalogId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, 'catalogVersion', catalogParams.catalogVersion);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, 'slotCatalog', filter.slotCatalog);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, 'oldComponentCatalog', filter.oldComponentCatalog);
        return restService.save(requestParams);
    }
    getActions(customizationId, variationId, filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS);
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
        let requestParams = {
            fields: /* @ngInject */ PersonalizationsmarteditRestService_1.FULL_FIELDS
        };
        requestParams = lodash__namespace.assign(requestParams, pathVariables);
        return restService.get(requestParams);
    }
    createActions(customizationId, variationId, data, filter) {
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
        const url = this.prepareURI(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS, pathVariables);
        const httpOptions = {
            headers: this.actionHeaders
        };
        return this.httpClient.patch(url, data, httpOptions).toPromise();
    }
    addActionToContainer(componentId, catalogId, containerId, customizationId, variationId, filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS);
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
        let requestParams = {
            type: 'cxCmsActionData',
            containerId,
            componentId,
            componentCatalog: catalogId
        };
        requestParams = lodash__namespace.assign(requestParams, pathVariables);
        return restService.save(requestParams);
    }
    editAction(customizationId, variationId, actionId, newComponentId, newComponentCatalog, filter) {
        return __awaiter(this, void 0, void 0, function* () {
            const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTION, 'actionId');
            const requestParams = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);
            let actionInfo = yield restService.get(requestParams);
            actionInfo = lodash__namespace.assign(actionInfo, requestParams);
            actionInfo.componentId = newComponentId;
            actionInfo.componentCatalog = newComponentCatalog;
            return restService.update(actionInfo);
        });
    }
    deleteAction(customizationId, variationId, actionId, filter) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTION, 'actionId');
        const requestParams = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);
        return restService.remove(requestParams);
    }
    deleteActions(customizationId, variationId, actionIds, filter) {
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
        const url = this.prepareURI(/* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS, pathVariables);
        const httpOptions = {
            headers: this.actionHeaders,
            body: actionIds
        };
        return this.httpClient.delete(url, httpOptions).toPromise();
    }
    getComponents(filter) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CATALOGS);
        let requestParams = {
            siteId: experienceData.siteDescriptor.uid
        };
        requestParams = lodash__namespace.assign(requestParams, filter);
        return restService.get(this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter));
    }
    getComponent(itemUuid) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CATALOG, 'itemUuid');
        const requestParams = {
            itemUuid,
            siteId: experienceData.siteDescriptor.uid
        };
        return restService.get(requestParams);
    }
    getNewComponentTypes() {
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.COMPONENT_TYPES);
        return restService.get();
    }
    updateCustomizationRank(customizationId, icreaseValue) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.UPDATE_CUSTOMIZATION_RANK);
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'customization', customizationId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'increaseValue', icreaseValue);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalog', experienceData.catalogDescriptor.catalogId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalogVersion', experienceData.catalogDescriptor.catalogVersion);
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }
    checkVersionConflict(versionId) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(/* @ngInject */ PersonalizationsmarteditRestService_1.CHECK_VERSION);
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'versionId', versionId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalog', experienceData.catalogDescriptor.catalogId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'catalogVersion', experienceData.catalogDescriptor.catalogVersion);
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }
};
/* @ngInject */ exports.PersonalizationsmarteditRestService.CUSTOMIZATIONS = '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizations';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CUSTOMIZATION = /* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATIONS + '/:customizationCode';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGES = '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizationpackages';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGE = /* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION_PACKAGES + '/:customizationCode';
/* @ngInject */ exports.PersonalizationsmarteditRestService.ACTIONS_DETAILS = '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions';
/* @ngInject */ exports.PersonalizationsmarteditRestService.VARIATIONS = /* @ngInject */ PersonalizationsmarteditRestService_1.CUSTOMIZATION + '/variations';
/* @ngInject */ exports.PersonalizationsmarteditRestService.VARIATION = /* @ngInject */ PersonalizationsmarteditRestService_1.VARIATIONS + '/:variationCode';
/* @ngInject */ exports.PersonalizationsmarteditRestService.ACTIONS = /* @ngInject */ PersonalizationsmarteditRestService_1.VARIATION + '/actions';
/* @ngInject */ exports.PersonalizationsmarteditRestService.ACTION = /* @ngInject */ PersonalizationsmarteditRestService_1.ACTIONS + '/:actionId';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CXCMSC_ACTIONS_FROM_VARIATIONS = '/personalizationwebservices/v1/query/cxcmscomponentsfromvariations';
/* @ngInject */ exports.PersonalizationsmarteditRestService.SEGMENTS = '/personalizationwebservices/v1/segments';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CATALOGS = '/cmswebservices/v1/sites/:siteId/cmsitems';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CATALOG = /* @ngInject */ PersonalizationsmarteditRestService_1.CATALOGS + '/:itemUuid';
/* @ngInject */ exports.PersonalizationsmarteditRestService.ADD_CONTAINER = '/personalizationwebservices/v1/query/cxReplaceComponentWithContainer';
/* @ngInject */ exports.PersonalizationsmarteditRestService.COMPONENT_TYPES = '/cmswebservices/v1/types?category=COMPONENT';
/* @ngInject */ exports.PersonalizationsmarteditRestService.UPDATE_CUSTOMIZATION_RANK = '/personalizationwebservices/v1/query/cxUpdateCustomizationRank';
/* @ngInject */ exports.PersonalizationsmarteditRestService.CHECK_VERSION = '/personalizationwebservices/v1/query/cxCmsPageVersionCheck';
/* @ngInject */ exports.PersonalizationsmarteditRestService.VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS = 'variations(active,actions,enabled,code,name,rank,status,catalog,catalogVersion)';
/* @ngInject */ exports.PersonalizationsmarteditRestService.FULL_FIELDS = 'FULL';
/* @ngInject */ exports.PersonalizationsmarteditRestService = /* @ngInject */ PersonalizationsmarteditRestService_1 = __decorate([
    core.Injectable(),
    __param(3, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.RestServiceFactory,
        personalizationcommons.PersonalizationsmarteditUtils,
        http.HttpClient, Function, exports.PersonalizationsmarteditContextService])
], /* @ngInject */ exports.PersonalizationsmarteditRestService);

/* @ngInject */ exports.PersonalizationsmarteditRulesAndPermissionsRegistrationService = class /* @ngInject */ PersonalizationsmarteditRulesAndPermissionsRegistrationService {
    constructor(personalizationsmarteditContextService, personalizationsmarteditRestService, permissionService, catalogVersionPermissionService, pageService) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.permissionService = permissionService;
        this.catalogVersionPermissionService = catalogVersionPermissionService;
        this.pageService = pageService;
    }
    registerRules() {
        this.permissionService.registerRule({
            names: ['se.access.personalization'],
            verify: () => __awaiter(this, void 0, void 0, function* () {
                yield this.catalogVersionPermissionService.hasReadPermissionOnCurrent();
                yield this.personalizationsmarteditContextService.refreshExperienceData();
                try {
                    yield this.personalizationsmarteditRestService.getCustomizations(this.getCustomizationFilter());
                    return true;
                }
                catch (errorResp) {
                    if (errorResp.status === 403) {
                        // Forbidden status on GET /customizations - user doesn't have permission to personalization perspective
                        return false;
                    }
                    else {
                        // other errors will be handled with personalization perspective turned on
                        return true;
                    }
                }
            })
        });
        this.permissionService.registerRule({
            names: ['se.access.personalization.page'],
            verify: () => __awaiter(this, void 0, void 0, function* () {
                const info = yield this.pageService.getCurrentPageInfo();
                return info.typeCode !== 'EmailPage';
            })
        });
        // Permissions
        this.permissionService.registerPermission({
            aliases: ['se.personalization.open'],
            rules: ['se.access.personalization']
        });
        this.permissionService.registerPermission({
            aliases: ['se.personalization.page'],
            rules: ['se.access.personalization.page']
        });
    }
    getCustomizationFilter() {
        return {
            currentPage: 0,
            currentSize: 1
        };
    }
};
/* @ngInject */ exports.PersonalizationsmarteditRulesAndPermissionsRegistrationService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditContextService,
        exports.PersonalizationsmarteditRestService,
        smarteditcommons.IPermissionService,
        smarteditcommons.ICatalogVersionPermissionService,
        smarteditcommons.IPageService])
], /* @ngInject */ exports.PersonalizationsmarteditRulesAndPermissionsRegistrationService);

/* @ngInject */ exports.PersonalizationsmarteditVersionCheckerService = class /* @ngInject */ PersonalizationsmarteditVersionCheckerService {
    constructor(restService, pageVersionSelectionService) {
        this.restService = restService;
        this.pageVersionSelectionService = pageVersionSelectionService;
    }
    setVersion(version) {
        this.version = version;
    }
    provideTranlationKey(key) {
        const TRANSLATE_NS = 'personalization.se.cms.actionitem.page.version.rollback.confirmation';
        this.version = this.version || this.pageVersionSelectionService.getSelectedPageVersion();
        if (!!this.version) {
            return this.restService.checkVersionConflict(this.version.uid).then((response) => (response.result ? key : TRANSLATE_NS), () => key);
        }
        else {
            return Promise.resolve(key);
        }
    }
};
/* @ngInject */ exports.PersonalizationsmarteditVersionCheckerService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditRestService,
        smarteditcommons.PageVersionSelectionService])
], /* @ngInject */ exports.PersonalizationsmarteditVersionCheckerService);

exports.SePersonalizationsmarteditServicesModule = class SePersonalizationsmarteditServicesModule {
};
exports.SePersonalizationsmarteditServicesModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, personalizationcommons.PersonalizationsmarteditCommonsComponentsModule],
        providers: [
            {
                provide: personalizationcommons.IPersonalizationsmarteditContextServiceProxy,
                useClass: exports.PersonalizationsmarteditContextServiceProxy
            },
            exports.PersonalizationsmarteditContextService,
            exports.PersonalizationsmarteditRestService,
            exports.PersonalizationsmarteditPreviewService,
            exports.PersonalizationsmarteditRulesAndPermissionsRegistrationService,
            exports.PersonalizationsmarteditContextServiceReverseProxy,
            exports.PersonalizationsmarteditVersionCheckerService,
            smarteditcommons.moduleUtils.bootstrap((personalizationsmarteditRulesAndPermissionsRegistrationService) => {
                personalizationsmarteditRulesAndPermissionsRegistrationService.registerRules();
            }, [exports.PersonalizationsmarteditRulesAndPermissionsRegistrationService])
        ]
    })
], exports.SePersonalizationsmarteditServicesModule);

let /* @ngInject */ CombinedViewConfigureService = class /* @ngInject */ CombinedViewConfigureService {
    constructor() {
        this.catalogFilterSubject = new rxjs.Subject();
        this.selectedItemsSubject = new rxjs.BehaviorSubject([]);
        this.selectedItems = [];
        this.isItemSelected = (item) => !!this.selectedItems.find((currentItem) => currentItem.customization.code === item.customization.code &&
            currentItem.variation.code === item.variation.code);
        this.selectedItemsSubscription = this.selectedItemsSubject
            .asObservable()
            .subscribe((selectedItems) => {
            this.selectedItems = selectedItems;
        });
    }
    ngOnDestroy() {
        this.selectedItemsSubscription.unsubscribe();
    }
    setCatalogFilter(catalogFilter) {
        this.catalogFilterSubject.next(catalogFilter);
    }
    getCatalogFilter$() {
        return this.catalogFilterSubject.asObservable();
    }
    setSelectedItems(items) {
        this.selectedItemsSubject.next(items);
    }
};
/* @ngInject */ CombinedViewConfigureService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ CombinedViewConfigureService);

window.__smartedit__.addDecoratorPayload("Component", "CombinedViewItemPrinterComponent", {
    selector: 'combined-view-item-printer',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `<div [ngClass]="isItemSelected(combinedViewSelectItem) ? 'pe-combinedview-config__ui-select-item--selected': ''" class="pe-combinedview-config__ui-select-choices-layout"><div class="pe-combinedview-config__ui-select-choices-col1"><div [title]="combinedViewSelectItem.customization.name + ' > ' + combinedViewSelectItem.variation.name" [textContent]="combinedViewSelectItem.customization.name + ' > ' + combinedViewSelectItem.variation.name" class="perso-wrap-ellipsis"></div></div></div>`
});
let CombinedViewItemPrinterComponent = class CombinedViewItemPrinterComponent {
    constructor(data, contextService, personalizationsmarteditUtils, combinedViewConfigureService) {
        this.data = data;
        this.contextService = contextService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.combinedViewConfigureService = combinedViewConfigureService;
        this.isItemFromCurrentCatalog = (itemVariation) => this.personalizationsmarteditUtils.isItemFromCurrentCatalog(itemVariation, this.contextService.getSeData());
        this.isItemSelected = (item) => this.combinedViewConfigureService.isItemSelected(item);
        ({
            item: this.combinedViewSelectItem,
            selected: this.isSelected,
            select: this.select
        } = data);
        this.catalogFilterSubscription = this.combinedViewConfigureService
            .getCatalogFilter$()
            .subscribe((catalogFilter) => {
            this.catalogFilter = catalogFilter;
        });
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.catalogFilter !== 'current') {
                yield this.personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(this.data.item.variation);
            }
        });
    }
    ngOnDestroy() {
        this.catalogFilterSubscription.unsubscribe();
    }
};
CombinedViewItemPrinterComponent = __decorate([
    core.Component({
        selector: 'combined-view-item-printer',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `<div [ngClass]="isItemSelected(combinedViewSelectItem) ? 'pe-combinedview-config__ui-select-item--selected': ''" class="pe-combinedview-config__ui-select-choices-layout"><div class="pe-combinedview-config__ui-select-choices-col1"><div [title]="combinedViewSelectItem.customization.name + ' > ' + combinedViewSelectItem.variation.name" [textContent]="combinedViewSelectItem.customization.name + ' > ' + combinedViewSelectItem.variation.name" class="perso-wrap-ellipsis"></div></div></div>`
    }),
    __param(0, core.Inject(smarteditcommons.ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object, exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditUtils,
        CombinedViewConfigureService])
], CombinedViewItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "CombinedViewConfigureComponent", {
    selector: 'combined-view-configure',
    template: `<div class="pe-combinedview-config__form"><div class="form-group"><label for="PageFilterDropdownField001" class="fd-form-label" translate="personalization.commons.filter.label"></label><div class="pe-combinedview-config__filter-layout"><page-filter-dropdown id="PageFilterDropdownField001" (onSelectCallback)="pageFilterChange($event)" class="fd-has-margin-right-small"></page-filter-dropdown><has-multicatalog class="perso-filter__wrapper perso-filter__wrapper--catalog"><catalog-filter-dropdown (onSelectCallback)="catalogFilterChange($event)"></catalog-filter-dropdown></has-multicatalog></div><div class="pe-combinedview-config__select-group-layout"><div class="pe-combinedview-config__select-group-label-layout"><label for="CombinedViewSearchField1" class="fd-form-label se-control-label required pe-combinedview-config__label" translate="personalization.modal.combinedview.search.label"></label></div><se-select id="CombinedViewSearchField1" class="fd-form__control" [(model)]="selectedCombinedViewItemId" [placeholder]="'personalization.modal.combinedview.search.placeholder'" [fetchStrategy]="combinedViewItemsFetchStrategy" [onSelect]="selectElement" [itemComponent]="combinedViewItemPrinter" [disableChoiceFn]="isItemInSelectDisabled" [(reset)]="resetSelectedItems" [additionalBodyClass]="'se-select--paged'" [resetSearchInput]="false"></se-select></div></div><div class="form-group"><p *ngIf="selectedCombinedViewItems.length === 0" translate="personalization.toolbar.combinedview.openconfigure.empty"></p><div id="CombinedViewSelectedField1" class="pe-combinedview-config__list-layout" *ngFor="let item of selectedCombinedViewItems; index as i; first as isFirst" [ngClass]="isFirst ? 'pe-combinedview-config__divider' : ''"><div class="pe-combinedview-config__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combinedview-config__names-layout"><div class="perso-wrap-ellipsis pe-combinedview-config__cname" [textContent]="item.customization.name" [title]="item.customization.name"></div><span>></span><div class="perso-wrap-ellipsis pe-combinedview-config__vname" [textContent]="item.variation.name" [title]="item.variation.name"></div></div><div class="pe-combinedview-config__hyicon-globe"><se-tooltip *ngIf="!isItemFromCurrentCatalog(item.variation)" [placement]="'top-end'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" [appendTo]="'body'"><span class="perso__globe-icon sap-icon--globe" se-tooltip-trigger></span><div se-tooltip-body [translate]="item.variation.catalogVersionNameL10N"></div></se-tooltip></div><div class="pe-combinedview-config__hyicon-remove"><span class="sap-icon--decline" (click)="removeSelectedItem(item)" [title]="'personalization.modal.combinedview.icon.remove.title' | translate"></span></div></div></div></div>`
});
let CombinedViewConfigureComponent = class CombinedViewConfigureComponent {
    constructor(translateService, contextService, messageHandler, personalizationsmarteditUtils, componentMenuService, restService, modalManager, combinedViewConfigureService) {
        this.translateService = translateService;
        this.contextService = contextService;
        this.messageHandler = messageHandler;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.componentMenuService = componentMenuService;
        this.restService = restService;
        this.modalManager = modalManager;
        this.combinedViewConfigureService = combinedViewConfigureService;
        this.selectElement = (item, model) => __awaiter(this, void 0, void 0, function* () {
            if (!item) {
                return;
            }
            this.selectedCombinedViewItems.push(item);
            const catalogVersions = yield this.componentMenuService.getValidContentCatalogVersions();
            const catalogsUuids = catalogVersions.map((elem) => elem.id);
            this.selectedCombinedViewItems.sort((a, b) => {
                const aCatalogUuid = a.customization.catalog + '/' + a.customization.catalogVersion;
                const bCatalogUuid = b.customization.catalog + '/' + b.customization.catalogVersion;
                if (aCatalogUuid === bCatalogUuid) {
                    return a.customization.rank - b.customization.rank;
                }
                return catalogsUuids.indexOf(bCatalogUuid) - catalogsUuids.indexOf(aCatalogUuid);
            });
            this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);
            this.reset();
        });
        this.removeSelectedItem = (item) => {
            this.selectedCombinedViewItems.splice(this.selectedCombinedViewItems.indexOf(item), 1);
            this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);
            this.reset();
        };
        this.getClassForElement = (index) => this.personalizationsmarteditUtils.getClassForElement(index);
        this.getLetterForElement = (index) => this.personalizationsmarteditUtils.getLetterForElement(index);
        this.isItemInSelectDisabled = (item) => !!this.selectedCombinedViewItems.find((currentItem) => currentItem.customization.code === item.customization.code);
        this.pageFilterChange = (itemId) => {
            this.customizationPageFilter = itemId;
        };
        this.catalogFilterChange = (itemId) => {
            this.catalogFilter = itemId;
            this.combinedViewConfigureService.setCatalogFilter(this.catalogFilter);
        };
        this.isItemFromCurrentCatalog = (item) => this.personalizationsmarteditUtils.isItemFromCurrentCatalog(item, this.contextService.getSeData());
        this.constructCombinedViewSelectItem = (customization, variation) => ({
            id: customization.code + '-' + variation.code,
            label: customization.name + ' > ' + variation.name,
            customization: {
                code: customization.code,
                name: customization.name,
                rank: customization.rank,
                catalog: customization.catalog,
                catalogVersion: customization.catalogVersion
            },
            variation: {
                code: variation.code,
                name: variation.name,
                catalog: variation.catalog,
                catalogVersion: variation.catalogVersion
            }
        });
    }
    ngOnInit() {
        this.combinedView = this.contextService.getCombinedView();
        this.selectedCombinedViewItems = [];
        this.selectedCombinedViewItems = lodash__namespace.cloneDeep(this.combinedView.selectedItems || []);
        this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);
        this.selectedCombinedViewItemId = null;
        this.moreCustomizationsRequestProcessing = false;
        this.combinedViewItemPrinter = CombinedViewItemPrinterComponent;
        this.combinedViewItemsFetchStrategy = {
            fetchPage: (search, pageSize, currentPage) => __awaiter(this, void 0, void 0, function* () { return this.customizedVarItemsFetchPage(search, pageSize, currentPage); }),
            fetchEntity: (id) => __awaiter(this, void 0, void 0, function* () { return this.customizedVarItemsFetchEntity(id); })
        };
        this.modalManager.addButtons([
            {
                id: 'confirmOk',
                label: 'personalization.modal.combinedview.button.ok',
                style: smarteditcommons.ModalButtonStyle.Primary,
                action: smarteditcommons.ModalButtonAction.Close,
                callback: () => rxjs.from(this.onSave()),
                disabledFn: () => {
                    const combinedView = this.contextService.getCombinedView();
                    let arrayEquals = (combinedView.selectedItems || []).length === 0 &&
                        this.selectedCombinedViewItems.length === 0;
                    arrayEquals =
                        arrayEquals ||
                            lodash__namespace.isEqual(combinedView.selectedItems, this.selectedCombinedViewItems);
                    return arrayEquals;
                }
            },
            {
                id: 'confirmCancel',
                label: 'personalization.modal.combinedview.button.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss,
                callback: () => rxjs.from(this.onCancel())
            }
        ]);
    }
    reset() {
        if (this.resetSelectedItems) {
            this.resetSelectedItems(true);
        }
    }
    customizedVarItemsFetchPage(search, pageSize, currentPage) {
        return __awaiter(this, void 0, void 0, function* () {
            const filter = this.getCustomizationsCategoryFilter(search, pageSize, currentPage);
            try {
                const response = yield this.restService.getCustomizations(filter);
                const customizedVarItems = [];
                response.customizations.forEach((customization) => {
                    customization.variations
                        .filter((variation) => this.personalizationsmarteditUtils.isItemVisible(variation))
                        .forEach((variation) => customizedVarItems.push(this.constructCombinedViewSelectItem(customization, variation)));
                });
                return {
                    pagination: response.pagination,
                    results: customizedVarItems
                };
            }
            catch (e) {
                this.messageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomizations'));
            }
        });
    }
    customizedVarItemsFetchEntity(customizedVarItemId) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!customizedVarItemId) {
                return;
            }
            const [customizationCode, variationCode] = customizedVarItemId.split('-');
            const customization = yield this.restService.getCustomization({
                code: customizationCode
            });
            const variation = customization.variations.filter((element) => element.code === variationCode)[0];
            return this.constructCombinedViewSelectItem(customization, variation);
        });
    }
    getDefaultStatus() {
        const statusesMapping = this.personalizationsmarteditUtils.getStatusesMapping();
        return statusesMapping.find((elem) => elem.code === personalizationcommons.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL);
    }
    isCombinedViewContextPersRemoved(combinedView) {
        const items = combinedView.selectedItems;
        return !items.find((item) => item.customization.code === combinedView.customize.selectedCustomization.code &&
            item.variation.code ===
                combinedView.customize.selectedVariations.code);
    }
    getCustomizationsCategoryFilter(name = '', currentSize, currentPage) {
        const categoryFilter = {
            currentSize,
            currentPage,
            name,
            statuses: this.getDefaultStatus().modelStatuses,
            catalogs: this.catalogFilter,
            pageId: undefined,
            pageCatalogId: undefined
        };
        if (this.customizationPageFilter ===
            personalizationcommons.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE) {
            categoryFilter.pageId = this.contextService.getSeData().pageId;
            categoryFilter.pageCatalogId =
                this.contextService.getSeData().seExperienceData.pageContext.catalogId;
        }
        return categoryFilter;
    }
    onCancel() {
        this.modalManager.close(null);
        return Promise.resolve();
    }
    onSave() {
        const combinedView = this.contextService.getCombinedView();
        combinedView.selectedItems = this.selectedCombinedViewItems;
        if (combinedView.enabled &&
            combinedView.customize.selectedVariations !== null &&
            this.isCombinedViewContextPersRemoved(combinedView)) {
            combinedView.customize.selectedCustomization = null;
            combinedView.customize.selectedVariations = null;
            combinedView.customize.selectedComponents = null;
        }
        this.contextService.setCombinedView(combinedView);
        this.modalManager.close(null);
        return Promise.resolve();
    }
};
CombinedViewConfigureComponent = __decorate([
    core.Component({
        selector: 'combined-view-configure',
        template: `<div class="pe-combinedview-config__form"><div class="form-group"><label for="PageFilterDropdownField001" class="fd-form-label" translate="personalization.commons.filter.label"></label><div class="pe-combinedview-config__filter-layout"><page-filter-dropdown id="PageFilterDropdownField001" (onSelectCallback)="pageFilterChange($event)" class="fd-has-margin-right-small"></page-filter-dropdown><has-multicatalog class="perso-filter__wrapper perso-filter__wrapper--catalog"><catalog-filter-dropdown (onSelectCallback)="catalogFilterChange($event)"></catalog-filter-dropdown></has-multicatalog></div><div class="pe-combinedview-config__select-group-layout"><div class="pe-combinedview-config__select-group-label-layout"><label for="CombinedViewSearchField1" class="fd-form-label se-control-label required pe-combinedview-config__label" translate="personalization.modal.combinedview.search.label"></label></div><se-select id="CombinedViewSearchField1" class="fd-form__control" [(model)]="selectedCombinedViewItemId" [placeholder]="'personalization.modal.combinedview.search.placeholder'" [fetchStrategy]="combinedViewItemsFetchStrategy" [onSelect]="selectElement" [itemComponent]="combinedViewItemPrinter" [disableChoiceFn]="isItemInSelectDisabled" [(reset)]="resetSelectedItems" [additionalBodyClass]="'se-select--paged'" [resetSearchInput]="false"></se-select></div></div><div class="form-group"><p *ngIf="selectedCombinedViewItems.length === 0" translate="personalization.toolbar.combinedview.openconfigure.empty"></p><div id="CombinedViewSelectedField1" class="pe-combinedview-config__list-layout" *ngFor="let item of selectedCombinedViewItems; index as i; first as isFirst" [ngClass]="isFirst ? 'pe-combinedview-config__divider' : ''"><div class="pe-combinedview-config__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combinedview-config__names-layout"><div class="perso-wrap-ellipsis pe-combinedview-config__cname" [textContent]="item.customization.name" [title]="item.customization.name"></div><span>></span><div class="perso-wrap-ellipsis pe-combinedview-config__vname" [textContent]="item.variation.name" [title]="item.variation.name"></div></div><div class="pe-combinedview-config__hyicon-globe"><se-tooltip *ngIf="!isItemFromCurrentCatalog(item.variation)" [placement]="'top-end'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" [appendTo]="'body'"><span class="perso__globe-icon sap-icon--globe" se-tooltip-trigger></span><div se-tooltip-body [translate]="item.variation.catalogVersionNameL10N"></div></se-tooltip></div><div class="pe-combinedview-config__hyicon-remove"><span class="sap-icon--decline" (click)="removeSelectedItem(item)" [title]="'personalization.modal.combinedview.icon.remove.title' | translate"></span></div></div></div></div>`
    }),
    __metadata("design:paramtypes", [core$1.TranslateService,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        personalizationcommons.PersonalizationsmarteditUtils,
        smarteditcommons.ComponentMenuService,
        exports.PersonalizationsmarteditRestService,
        smarteditcommons.ModalManagerService,
        CombinedViewConfigureService])
], CombinedViewConfigureComponent);

/* @ngInject */ exports.CombinedViewCommonsService = class /* @ngInject */ CombinedViewCommonsService {
    constructor(personalizationsmarteditContextUtils, personalizationsmarteditContextService, personalizationsmarteditPreviewService, personalizationsmarteditUtils, personalizationsmarteditRestService, modalService, logService) {
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditPreviewService = personalizationsmarteditPreviewService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.modalService = modalService;
        this.logService = logService;
    }
    openManagerAction() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.modalService
                    .open({
                    component: CombinedViewConfigureComponent,
                    templateConfig: {
                        title: 'personalization.modal.combinedview.title',
                        isDismissButtonVisible: true
                    },
                    config: {
                        focusTrapped: false,
                        backdropClickCloseable: false
                    }
                })
                    .afterClosed.toPromise();
                this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(this.personalizationsmarteditContextService);
                const combinedView = this.personalizationsmarteditContextService.getCombinedView();
                combinedView.enabled =
                    combinedView.selectedItems && combinedView.selectedItems.length > 0;
                this.personalizationsmarteditContextService.setCombinedView(combinedView);
                this.updatePreview(this.getVariationsForPreviewTicket());
            }
            catch (e) {
                this.logService.debug('Combined View Items Select Modal dismissed', e);
            }
        });
    }
    updatePreview(previewTicketVariations) {
        this.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicketVariations);
        this.updateActionsOnSelectedVariations();
    }
    getVariationsForPreviewTicket() {
        const previewTicketVariations = [];
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        (combinedView.selectedItems || []).forEach((item) => {
            previewTicketVariations.push({
                customizationCode: item.customization.code,
                variationCode: item.variation.code,
                catalog: item.variation.catalog,
                catalogVersion: item.variation.catalogVersion
            });
        });
        return previewTicketVariations;
    }
    combinedViewEnabledEvent(isEnabled) {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        combinedView.enabled = isEnabled;
        this.personalizationsmarteditContextService.setCombinedView(combinedView);
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = null;
        customize.selectedVariations = null;
        customize.selectedComponents = null;
        this.personalizationsmarteditContextService.setCustomize(customize);
        if (isEnabled) {
            this.updatePreview(this.getVariationsForPreviewTicket());
        }
        else {
            this.updatePreview([]);
        }
    }
    isItemFromCurrentCatalog(item) {
        return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(item, this.personalizationsmarteditContextService.getSeData());
    }
    updateActionsOnSelectedVariations() {
        return __awaiter(this, void 0, void 0, function* () {
            const combinedView = this.personalizationsmarteditContextService.getCombinedView();
            const promisesArray = (combinedView.selectedItems || []).map((item) => __awaiter(this, void 0, void 0, function* () {
                const response = yield this.personalizationsmarteditRestService.getActions(item.customization.code, item.variation.code, item.variation);
                item.variation.actions = response.actions;
            }));
            yield Promise.all(promisesArray);
            this.personalizationsmarteditContextService.setCombinedView(combinedView);
        });
    }
};
/* @ngInject */ exports.CombinedViewCommonsService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditContextUtils,
        exports.PersonalizationsmarteditContextService,
        exports.PersonalizationsmarteditPreviewService,
        personalizationcommons.PersonalizationsmarteditUtils,
        exports.PersonalizationsmarteditRestService,
        smarteditcommons.IModalService,
        smarteditcommons.LogService])
], /* @ngInject */ exports.CombinedViewCommonsService);

window.__smartedit__.addDecoratorPayload("Component", "CombinedViewMenuComponent", {
    selector: 'combined-view-menu',
    template: `<div class="btn-block pe-toolbar-action--include"><div class="pe-toolbar-menu-content se-toolbar-menu-content--pe-customized se-toolbar-menu-content--pe-combine" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2" translate="personalization.toolbar.combinedview.header.title"></h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.combinedview.header.description"></span></se-help></div><div role="menuitem"><div *ngIf="!isCombinedViewConfigured"><div class="pe-combined-view-panel__wrapper pe-combined-view-panel__wrapper--empty"><img src="static-resources/images/emptyVersions.svg" alt="no Configurationss" class="pe-combined-view-panel--empty-img"> <span class="pe-combined-view-panel--empty-text" translate="personalization.toolbar.combinedview.openconfigure.empty"></span> <a class="fd-link pe-combined-view-panel--empty-link" (click)="combinedViewClick()" translate="personalization.toolbar.combinedview.openconfigure.link" [title]="'personalization.toolbar.combinedview.openconfigure.link' | translate"></a></div></div><div *ngIf="isCombinedViewConfigured"><div class="pe-combined-view-panel__wrapper"><div class="pe-combined-view-panel__configure-layout"><button fd-button [label]="'personalization.toolbar.combinedview.openconfigure.button' | translate" [title]="'personalization.toolbar.combinedview.openconfigure.button' | translate" class="pe-combined-view-panel__configure-btn perso-wrap-ellipsis" (click)="combinedViewClick()"></button> <button fd-button label="Transparent Button" class="pe-combined-view-panel__configure-btn perso-wrap-ellipsis" (click)="clearAllCombinedViewClick()" [label]="'personalization.toolbar.combinedview.clearall.button' | translate" [title]="'personalization.toolbar.combinedview.clearall.button' | translate" [fdType]="'transparent'"></button></div><div [ngClass]="combinedView.enabled ? null : 'pe-combined-view-panel--disabled'"><div class="pe-combined-view-panel__list-layout" *ngFor="let item of selectedItems; index as i" (click)="itemClick(item)" [ngClass]="{'pe-combined-view-panel-list__item--highlighted': item.highlighted}"><div class="pe-combined-view-panel-list__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combined-view-panel-list__names-layout"><div class="perso-wrap-ellipsis" [textContent]="item.customization.name" [title]="item.customization.name"></div><div class="perso-wrap-ellipsis perso-tree__primary-data" [textContent]="item.variation.name" [title]="item.variation.name"></div></div><div class="pe-combined-view-panel-list__icon"><se-tooltip *ngIf="!isItemFromCurrentCatalog(item.variation) && combinedView.enabled" [placement]="'top-end'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" [appendTo]="'body'"><span class="perso__globe-icon sap-icon--globe" se-tooltip-trigger></span><div se-tooltip-body [translate]="item.variation.catalogVersionNameL10N"></div></se-tooltip></div></div></div></div></div></div></div></div>`
});
exports.CombinedViewMenuComponent = class CombinedViewMenuComponent {
    constructor(translateService, contextService, messageHandler, restService, contextUtils, personalizationsmarteditUtils, previewService, combinedViewCommonsService, crossFrameEventService, permissionService, toolbarItem) {
        this.translateService = translateService;
        this.contextService = contextService;
        this.messageHandler = messageHandler;
        this.restService = restService;
        this.contextUtils = contextUtils;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.previewService = previewService;
        this.combinedViewCommonsService = combinedViewCommonsService;
        this.crossFrameEventService = crossFrameEventService;
        this.permissionService = permissionService;
        this.toolbarItem = toolbarItem;
    }
    ngOnInit() {
        this.combinedView = this.contextService.getCombinedView();
        this.selectedItems = this.combinedView.selectedItems || [];
        this.isCombinedViewConfigured = this.selectedItems.length !== 0;
    }
    combinedViewClick() {
        this.contextUtils.clearCustomizeContextAndReloadPreview(this.previewService, this.contextService);
        this.combinedViewCommonsService.openManagerAction();
        this.toolbarItem.isOpen = false;
    }
    getAndSetComponentsForElement(customizationId, variationId, catalog, catalogVersion) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.restService.getComponentsIdsForVariation(customizationId, variationId, catalog, catalogVersion);
                const combinedView = this.contextService.getCombinedView();
                combinedView.customize.selectedComponents = response.components;
                this.contextService.setCombinedView(combinedView);
            }
            catch (e) {
                this.messageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponentsforvariation'));
            }
        });
    }
    itemClick(item) {
        return __awaiter(this, void 0, void 0, function* () {
            const combinedView = this.contextService.getCombinedView();
            if (!combinedView.enabled) {
                return;
            }
            this.selectedItems.forEach((elem) => {
                elem.highlighted = false;
            });
            item.highlighted = true;
            combinedView.customize.selectedCustomization = item.customization;
            combinedView.customize.selectedVariations = item.variation;
            this.contextService.setCombinedView(combinedView);
            const roleGranted = yield this.permissionService.isPermitted([
                {
                    names: ['se.edit.page']
                }
            ]);
            if (roleGranted) {
                this.getAndSetComponentsForElement(item.customization.code, item.variation.code, item.customization.catalog, item.customization.catalogVersion);
            }
            this.combinedViewCommonsService.updatePreview(this.personalizationsmarteditUtils.getVariationKey(item.customization.code, [
                item.variation
            ]));
            this.crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY);
        });
    }
    getClassForElement(index) {
        return this.personalizationsmarteditUtils.getClassForElement(index);
    }
    getLetterForElement(index) {
        return this.personalizationsmarteditUtils.getLetterForElement(index);
    }
    isItemFromCurrentCatalog(itemVariation) {
        return this.combinedViewCommonsService.isItemFromCurrentCatalog(itemVariation);
    }
    clearAllCombinedViewClick() {
        this.selectedItems = [];
        this.combinedView.selectedItems = [];
        this.combinedView.enabled = false;
        this.contextService.setCombinedView(this.combinedView);
        this.combinedViewCommonsService.combinedViewEnabledEvent(this.combinedView.enabled);
    }
};
exports.CombinedViewMenuComponent = __decorate([
    core.Component({
        selector: 'combined-view-menu',
        template: `<div class="btn-block pe-toolbar-action--include"><div class="pe-toolbar-menu-content se-toolbar-menu-content--pe-customized se-toolbar-menu-content--pe-combine" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2" translate="personalization.toolbar.combinedview.header.title"></h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.combinedview.header.description"></span></se-help></div><div role="menuitem"><div *ngIf="!isCombinedViewConfigured"><div class="pe-combined-view-panel__wrapper pe-combined-view-panel__wrapper--empty"><img src="static-resources/images/emptyVersions.svg" alt="no Configurationss" class="pe-combined-view-panel--empty-img"> <span class="pe-combined-view-panel--empty-text" translate="personalization.toolbar.combinedview.openconfigure.empty"></span> <a class="fd-link pe-combined-view-panel--empty-link" (click)="combinedViewClick()" translate="personalization.toolbar.combinedview.openconfigure.link" [title]="'personalization.toolbar.combinedview.openconfigure.link' | translate"></a></div></div><div *ngIf="isCombinedViewConfigured"><div class="pe-combined-view-panel__wrapper"><div class="pe-combined-view-panel__configure-layout"><button fd-button [label]="'personalization.toolbar.combinedview.openconfigure.button' | translate" [title]="'personalization.toolbar.combinedview.openconfigure.button' | translate" class="pe-combined-view-panel__configure-btn perso-wrap-ellipsis" (click)="combinedViewClick()"></button> <button fd-button label="Transparent Button" class="pe-combined-view-panel__configure-btn perso-wrap-ellipsis" (click)="clearAllCombinedViewClick()" [label]="'personalization.toolbar.combinedview.clearall.button' | translate" [title]="'personalization.toolbar.combinedview.clearall.button' | translate" [fdType]="'transparent'"></button></div><div [ngClass]="combinedView.enabled ? null : 'pe-combined-view-panel--disabled'"><div class="pe-combined-view-panel__list-layout" *ngFor="let item of selectedItems; index as i" (click)="itemClick(item)" [ngClass]="{'pe-combined-view-panel-list__item--highlighted': item.highlighted}"><div class="pe-combined-view-panel-list__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combined-view-panel-list__names-layout"><div class="perso-wrap-ellipsis" [textContent]="item.customization.name" [title]="item.customization.name"></div><div class="perso-wrap-ellipsis perso-tree__primary-data" [textContent]="item.variation.name" [title]="item.variation.name"></div></div><div class="pe-combined-view-panel-list__icon"><se-tooltip *ngIf="!isItemFromCurrentCatalog(item.variation) && combinedView.enabled" [placement]="'top-end'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true" [appendTo]="'body'"><span class="perso__globe-icon sap-icon--globe" se-tooltip-trigger></span><div se-tooltip-body [translate]="item.variation.catalogVersionNameL10N"></div></se-tooltip></div></div></div></div></div></div></div></div>`
    }),
    __param(10, core.Inject(smarteditcommons.TOOLBAR_ITEM)),
    __metadata("design:paramtypes", [core$1.TranslateService,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditContextUtils,
        personalizationcommons.PersonalizationsmarteditUtils,
        exports.PersonalizationsmarteditPreviewService,
        exports.CombinedViewCommonsService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.IPermissionService, Object])
], exports.CombinedViewMenuComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogFilterDropdownComponent", {
    selector: 'catalog-filter-dropdown',
    template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
});
let CatalogFilterDropdownComponent = class CatalogFilterDropdownComponent {
    constructor() {
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(this.items)
        };
        this.onChange = this.onChange.bind(this);
        this.onSelectCallback = new core.EventEmitter();
    }
    ngOnInit() {
        this.items = [
            {
                id: personalizationcommons.PERSONALIZATION_CATALOG_FILTER.ALL,
                label: 'personalization.filter.catalog.all'
            },
            {
                id: personalizationcommons.PERSONALIZATION_CATALOG_FILTER.CURRENT,
                label: 'personalization.filter.catalog.current'
            },
            {
                id: personalizationcommons.PERSONALIZATION_CATALOG_FILTER.PARENTS,
                label: 'personalization.filter.catalog.parents'
            }
        ];
        this.selectedId = this.initialValue || this.items[1].id;
    }
    onChange() {
        this.onSelectCallback.emit(this.selectedId);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogFilterDropdownComponent.prototype, "initialValue", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], CatalogFilterDropdownComponent.prototype, "onSelectCallback", void 0);
CatalogFilterDropdownComponent = __decorate([
    core.Component({
        selector: 'catalog-filter-dropdown',
        template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
    }),
    __metadata("design:paramtypes", [])
], CatalogFilterDropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "PageFilterDropdownComponent", {
    selector: 'page-filter-dropdown',
    template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
});
let PageFilterDropdownComponent = class PageFilterDropdownComponent {
    constructor() {
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(this.items)
        };
        this.onChange = this.onChange.bind(this);
        this.onSelectCallback = new core.EventEmitter();
    }
    ngOnInit() {
        this.items = [
            {
                id: personalizationcommons.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE,
                label: 'personalization.filter.page.onlythispage'
            },
            {
                id: personalizationcommons.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ALL,
                label: 'personalization.filter.page.all'
            }
        ];
        this.selectedId = this.initialValue || this.items[0].id;
    }
    onChange() {
        this.onSelectCallback.emit(this.selectedId);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], PageFilterDropdownComponent.prototype, "initialValue", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], PageFilterDropdownComponent.prototype, "onSelectCallback", void 0);
PageFilterDropdownComponent = __decorate([
    core.Component({
        selector: 'page-filter-dropdown',
        template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
    }),
    __metadata("design:paramtypes", [])
], PageFilterDropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "StatusFilterDropdownComponent", {
    selector: 'status-filter-dropdown',
    template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
});
let StatusFilterDropdownComponent = class StatusFilterDropdownComponent {
    constructor(personalizationsmarteditUtils) {
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(this.items)
        };
        this.onChange = this.onChange.bind(this);
        this.onSelectCallback = new core.EventEmitter();
    }
    ngOnInit() {
        this.items = this.personalizationsmarteditUtils.getStatusesMapping().map((elem) => ({
            id: elem.code,
            label: elem.text,
            modelStatuses: elem.modelStatuses
        }));
        this.selectedId = this.initialValue || this.items[0].id;
    }
    onChange() {
        this.onSelectCallback.emit(this.selectedId);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], StatusFilterDropdownComponent.prototype, "initialValue", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], StatusFilterDropdownComponent.prototype, "onSelectCallback", void 0);
StatusFilterDropdownComponent = __decorate([
    core.Component({
        selector: 'status-filter-dropdown',
        template: `<se-select class="perso-filter" (click)="$event.stopPropagation()" [(model)]="selectedId" [onChange]="onChange" [fetchStrategy]="fetchStrategy" [searchEnabled]="false"></se-select>`
    }),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditUtils])
], StatusFilterDropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogVersionFilterItemPrinterComponent", {
    selector: 'catalog-version-filter-item-printer',
    template: `
        <div>
            <span
                class="perso__globe-icon sap-icon--globe"
                *ngIf="data.item.isCurrentCatalog === false"
            >
            </span>
            <span *ngIf="data.item.isCurrentCatalog === false">
                {{ data.item.catalogName }} - {{ data.item.catalogVersionId }}
            </span>
            <span
                *ngIf="data.item.isCurrentCatalog === true"
                [translate]="'personalization.filter.catalog.current'"
            ></span>
        </div>
    `
});
let CatalogVersionFilterItemPrinterComponent = class CatalogVersionFilterItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
CatalogVersionFilterItemPrinterComponent = __decorate([
    core.Component({
        selector: 'catalog-version-filter-item-printer',
        template: `
        <div>
            <span
                class="perso__globe-icon sap-icon--globe"
                *ngIf="data.item.isCurrentCatalog === false"
            >
            </span>
            <span *ngIf="data.item.isCurrentCatalog === false">
                {{ data.item.catalogName }} - {{ data.item.catalogVersionId }}
            </span>
            <span
                *ngIf="data.item.isCurrentCatalog === true"
                [translate]="'personalization.filter.catalog.current'"
            ></span>
        </div>
    `
    }),
    __param(0, core.Inject(smarteditcommons.ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], CatalogVersionFilterItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "CatalogVersionFilterDropdownComponent", {
    selector: 'catalog-version-filter-dropdown',
    template: `
        <se-select
            class="perso-filter"
            (click)="$event.stopPropagation()"
            [(model)]="selectedId"
            [onChange]="onChange"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [searchEnabled]="false"
        ></se-select>
    `,
    providers: [smarteditcommons.L10nPipe]
});
let CatalogVersionFilterDropdownComponent = class CatalogVersionFilterDropdownComponent {
    constructor(componentMenuService, personalizationsmarteditContextService, l10nPipe) {
        this.componentMenuService = componentMenuService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.l10nPipe = l10nPipe;
        this.onSelectCallback = new core.EventEmitter();
        this.itemComponent = CatalogVersionFilterItemPrinterComponent;
        this.onChange = this.onChange.bind(this);
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(this.items)
        };
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.items = yield this.componentMenuService.getValidContentCatalogVersions();
            const experience = this.personalizationsmarteditContextService.getSeData().seExperienceData;
            this.items.forEach((item) => __awaiter(this, void 0, void 0, function* () {
                item.isCurrentCatalog = item.id === experience.catalogDescriptor.catalogVersionUuid;
                item.catalogName = yield this.l10nPipe
                    .transform(item.catalogName)
                    .pipe(operators.take(1))
                    .toPromise();
            }));
            const selectedCatalogVersion = yield this.componentMenuService.getInitialCatalogVersion(this.items);
            this.selectedId = this.initialValue || selectedCatalogVersion.id;
        });
    }
    onChange(_changes) {
        this.onSelectCallback.emit(this.selectedId);
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], CatalogVersionFilterDropdownComponent.prototype, "initialValue", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], CatalogVersionFilterDropdownComponent.prototype, "onSelectCallback", void 0);
CatalogVersionFilterDropdownComponent = __decorate([
    core.Component({
        selector: 'catalog-version-filter-dropdown',
        template: `
        <se-select
            class="perso-filter"
            (click)="$event.stopPropagation()"
            [(model)]="selectedId"
            [onChange]="onChange"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [searchEnabled]="false"
        ></se-select>
    `,
        providers: [smarteditcommons.L10nPipe]
    }),
    __param(1, core.Inject(exports.PersonalizationsmarteditContextService)),
    __metadata("design:paramtypes", [smarteditcommons.ComponentMenuService,
        exports.PersonalizationsmarteditContextService,
        smarteditcommons.L10nPipe])
], CatalogVersionFilterDropdownComponent);

window.__smartedit__.addDecoratorPayload("Component", "HasMulticatalogComponent", {
    selector: 'has-multicatalog',
    template: `
        <div *ngIf="hasMulticatalog">
            <ng-content></ng-content>
        </div>
    `
});
let HasMulticatalogComponent = class HasMulticatalogComponent {
    constructor(personalizationsmarteditContextService) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
    }
    ngOnInit() {
        this.hasMulticatalog = this.getSeExperienceData().siteDescriptor.contentCatalogs.length > 1;
    }
    getSeExperienceData() {
        return this.personalizationsmarteditContextService.getSeData().seExperienceData;
    }
};
HasMulticatalogComponent = __decorate([
    core.Component({
        selector: 'has-multicatalog',
        template: `
        <div *ngIf="hasMulticatalog">
            <ng-content></ng-content>
        </div>
    `
    }),
    __param(0, core.Inject(exports.PersonalizationsmarteditContextService)),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditContextService])
], HasMulticatalogComponent);

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditSharedComponentsModule
 */
let PersonalizationsmarteditSharedComponentsModule = class PersonalizationsmarteditSharedComponentsModule {
};
PersonalizationsmarteditSharedComponentsModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, smarteditcommons.TranslationModule.forChild(), smarteditcommons.SelectModule],
        declarations: [
            HasMulticatalogComponent,
            CatalogVersionFilterDropdownComponent,
            CatalogVersionFilterItemPrinterComponent,
            StatusFilterDropdownComponent,
            PageFilterDropdownComponent,
            CatalogFilterDropdownComponent
        ],
        entryComponents: [
            HasMulticatalogComponent,
            CatalogVersionFilterDropdownComponent,
            CatalogVersionFilterItemPrinterComponent,
            StatusFilterDropdownComponent,
            PageFilterDropdownComponent,
            CatalogFilterDropdownComponent
        ],
        exports: [
            HasMulticatalogComponent,
            CatalogVersionFilterDropdownComponent,
            CatalogVersionFilterItemPrinterComponent,
            StatusFilterDropdownComponent,
            PageFilterDropdownComponent,
            CatalogFilterDropdownComponent
        ],
        providers: []
    })
], PersonalizationsmarteditSharedComponentsModule);

let /* @ngInject */ CustomizationDataFactory = class /* @ngInject */ CustomizationDataFactory {
    constructor(personalizationsmarteditRestService, personalizationsmarteditUtils) {
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.items = [];
        this.defaultFilter = {};
        this.defaultDataArrayName = 'customizations';
    }
    getCustomizations(filter) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const customizations = yield this.personalizationsmarteditRestService.getCustomizations(filter);
                this.personalizationsmarteditUtils.uniqueArray(this.items, customizations[this.defaultDataArrayName] || []);
                this.defaultSuccessCallbackFunction(customizations);
            }
            catch (e) {
                this.defaultErrorCallbackFunction(e);
            }
        });
    }
    updateData(params, successCallbackFunction, errorCallbackFunction) {
        params = params || {};
        this.defaultFilter = params.filter || this.defaultFilter;
        this.defaultDataArrayName = params.dataArrayName || this.defaultDataArrayName;
        if (successCallbackFunction && typeof successCallbackFunction === 'function') {
            this.defaultSuccessCallbackFunction = successCallbackFunction;
        }
        if (errorCallbackFunction && typeof errorCallbackFunction === 'function') {
            this.defaultErrorCallbackFunction = errorCallbackFunction;
        }
        this.getCustomizations(this.defaultFilter);
    }
    refreshData() {
        if (this.defaultFilter === {}) {
            return;
        }
        const tempFilter = lodash__namespace.cloneDeep(this.defaultFilter);
        tempFilter.currentSize = this.items.length;
        tempFilter.currentPage = 0;
        this.resetData();
        this.getCustomizations(tempFilter);
    }
    resetData() {
        this.items.length = 0;
    }
};
/* @ngInject */ CustomizationDataFactory = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditUtils])
], /* @ngInject */ CustomizationDataFactory);

let PersonalizationsmarteditDataFactoryModule = class PersonalizationsmarteditDataFactoryModule {
};
PersonalizationsmarteditDataFactoryModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, exports.SePersonalizationsmarteditServicesModule],
        providers: [CustomizationDataFactory]
    })
], PersonalizationsmarteditDataFactoryModule);

exports.CombinedViewModule = class CombinedViewModule {
};
exports.CombinedViewModule = __decorate([
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            common.CommonModule,
            button.ButtonModule,
            smarteditcommons.SelectModule,
            smarteditcommons.TooltipModule,
            smarteditcommons.HelpModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            PersonalizationsmarteditDataFactoryModule,
            exports.SePersonalizationsmarteditServicesModule,
            PersonalizationsmarteditSharedComponentsModule
        ],
        providers: [exports.CombinedViewCommonsService, CombinedViewConfigureService],
        declarations: [
            CombinedViewConfigureComponent,
            CombinedViewItemPrinterComponent,
            exports.CombinedViewMenuComponent
        ],
        entryComponents: [
            CombinedViewConfigureComponent,
            CombinedViewItemPrinterComponent,
            exports.CombinedViewMenuComponent
        ]
    })
], exports.CombinedViewModule);

window.__smartedit__.addDecoratorPayload("Component", "ContextMenuDeleteActionComponent", {
    selector: 'context-menu-delete-action',
    template: `<div id="confirmationModalDescription">
        {{ 'personalization.modal.deleteaction.content' | translate }}
    </div>`
});
let ContextMenuDeleteActionComponent = class ContextMenuDeleteActionComponent {
};
ContextMenuDeleteActionComponent = __decorate([
    core.Component({
        selector: 'context-menu-delete-action',
        template: `<div id="confirmationModalDescription">
        {{ 'personalization.modal.deleteaction.content' | translate }}
    </div>`
    })
], ContextMenuDeleteActionComponent);

window.__smartedit__.addDecoratorPayload("Component", "ComponentDropdownItemPrinterComponent", {
    selector: 'component-item-printer',
    template: `
        <div class="pe-customize-component__se-select-choices-layout">
            <div class="pe-customize-component__se-select-choices-col1">
                <div class="perso-wrap-ellipsis" title="{{ data.item.name }}">
                    {{ data.item.name }}
                </div>
            </div>
            <div class="pe-customize-component__se-select-choices-col2">
                <div class="perso-wrap-ellipsis" title="{{ data.item.typeCode }}">
                    {{ data.item.typeCode }}
                </div>
            </div>
        </div>
    `
});
let ComponentDropdownItemPrinterComponent = class ComponentDropdownItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
ComponentDropdownItemPrinterComponent = __decorate([
    core.Component({
        selector: 'component-item-printer',
        template: `
        <div class="pe-customize-component__se-select-choices-layout">
            <div class="pe-customize-component__se-select-choices-col1">
                <div class="perso-wrap-ellipsis" title="{{ data.item.name }}">
                    {{ data.item.name }}
                </div>
            </div>
            <div class="pe-customize-component__se-select-choices-col2">
                <div class="perso-wrap-ellipsis" title="{{ data.item.typeCode }}">
                    {{ data.item.typeCode }}
                </div>
            </div>
        </div>
    `
    }),
    __param(0, core.Inject(smarteditcommons.ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], ComponentDropdownItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "PersonalizationsmarteditContextMenuAddEditActionComponent", {
    template: `<div class="perso-customize-component"><div class="perso-customize-component__title-layout"><div *ngIf="letterIndicatorForElement" class="perso-customize-component__title-layout__letter-block"><span [ngClass]="colorIndicatorForElement">{{letterIndicatorForElement}}</span></div><div class="perso-customize-component__title-layout__cust-name perso-wrap-ellipsis" title="{{selectedCustomization.name}}">{{selectedCustomization.name}}</div><div class="perso-customize-component__title-layout__target-group-name perso-wrap-ellipsis" title="{{selectedVariation.name}}">{{'> '+ selectedVariation.name}}</div></div><dl class="perso-customize-component__data-list"><label fd-form-label for="perso-customize-component-type-desc">{{ "personalization.modal.addeditaction.selected.mastercomponent.title" | translate }}</label><dd id="perso-customize-component-type-desc">{{componentType}}</dd></dl><label fd-form-label class="se-control-label" [required]="true" for="perso-customize-component-action-select">{{ "personalization.modal.addeditaction.selected.actions.title" | translate }}</label><se-tooltip [placement]="'top-start'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true"><span class="sap-icon--question-mark sap-icon" style="margin-left: 10px; padding-top: 1px" se-tooltip-trigger></span><div se-tooltip-body [translate]="'personalization.modal.addeditaction.selected.actions.help.label'"></div></se-tooltip><se-select id="perso-customize-component-action-select" class="perso-customize-component__select2-container" [placeholder]="'personalization.modal.addeditaction.dropdown.placeholder'" [(model)]="actionSelected" [searchEnabled]="false" [showRemoveButton]="false" [fetchStrategy]="actionFetchStrategy"></se-select><div class="perso-customize-component__select-group-label-layout"><div *ngIf="actionSelected == 'use'"><label fd-form-label class="se-control-label" [required]="true" for="perso-customize-component-saved-component-select">{{ "personalization.modal.addeditaction.selected.component.title" | translate }}</label></div><has-multicatalog *ngIf="actionSelected == 'use'"><div class="perso-customize-component__filter-layout"><label fd-form-label class="perso-customize-component__filter-label" for="perso-customize-component-catalog-version-filter-dropdown">{{ 'personalization.commons.filter.label' | translate }}</label><catalog-version-filter-dropdown id="perso-customize-component-catalog-version-filter-dropdown" class="pe-customize-component__catalog-version-filter-dropdown" (onSelectCallback)="catalogVersionFilterChange($event)"></catalog-version-filter-dropdown></div></has-multicatalog></div><se-select id="perso-customize-component-saved-component-select" class="perso-customize-component__select2-container" *ngIf="actionSelected == 'use'" [placeholder]="'personalization.modal.addeditaction.dropdown.componentlist.placeholder'" [(model)]="idComponentSelected" [onSelect]="componentSelectedEvent" [searchEnabled]="true" [showRemoveButton]="false" [fetchStrategy]="componentsFetchStrategy" [itemComponent]="itemComponent"></se-select><se-select class="perso-customize-component__select2-container" *ngIf="actionSelected == 'create'" [placeholder]="'personalization.modal.addeditaction.dropdown.componenttype.placeholder'" [(model)]="newComponentSelected" [onSelect]="newComponentTypeSelectedEvent" [searchEnabled]="false" [fetchStrategy]="componentTypesFetchStrategy"></se-select></div>`
});
let PersonalizationsmarteditContextMenuAddEditActionComponent = class PersonalizationsmarteditContextMenuAddEditActionComponent {
    constructor(modalManager, translateService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditContextService, slotRestrictionsService, editorModalService) {
        this.modalManager = modalManager;
        this.translateService = translateService;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.slotRestrictionsService = slotRestrictionsService;
        this.editorModalService = editorModalService;
        this.actionSelected = '';
        this.idComponentSelected = '';
        this.selectedCustomization = {};
        this.selectedVariation = {};
        this.newComponentSelected = '';
        this.componentSelected = {};
        this.actionCreated = new core.EventEmitter();
        this.itemComponent = ComponentDropdownItemPrinterComponent;
        this.modalButtons = [
            {
                id: 'submit',
                style: smarteditcommons.ModalButtonStyle.Primary,
                label: 'personalization.modal.addeditaction.button.submit',
                action: smarteditcommons.ModalButtonAction.Close,
                disabledFn: () => !Boolean(this.idComponentSelected) ||
                    (Boolean(this.componentUuid) && this.componentUuid === this.idComponentSelected),
                callback: () => {
                    this.idComponentSelected = undefined;
                    const componentCatalogId = this.componentSelected.catalogVersion.substring(0, this.componentSelected.catalogVersion.indexOf('/'));
                    const filter = {
                        catalog: this.selectedCustomization.catalog,
                        catalogVersion: this.selectedCustomization.catalogVersion
                    };
                    const extraCatalogFilter = {
                        slotCatalog: this.slotCatalog,
                        oldComponentCatalog: this.componentCatalog
                    };
                    Object.assign(extraCatalogFilter, filter);
                    if (this.editEnabled) {
                        this.editAction(this.selectedCustomization.code, this.selectedVariation.code, this.actionId, this.componentSelected.uid, componentCatalogId, filter);
                    }
                    else {
                        this.personalizationsmarteditRestService
                            .replaceComponentWithContainer(this.defaultComponentId, this.slotId, extraCatalogFilter)
                            .then((result) => {
                            this.addActionToContainer(this.componentSelected.uid, componentCatalogId, result.sourceId, this.selectedCustomization.code, this.selectedVariation.code, filter);
                        }, () => {
                            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.replacingcomponent'));
                        });
                    }
                    return this.actionCreated;
                }
            },
            {
                id: 'cancel',
                label: 'personalization.modal.addeditaction.button.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Dismiss
            }
        ];
        this.initNewComponentTypes = () => this.slotRestrictionsService.getSlotRestrictions(this.slotId).then((restrictions) => this.personalizationsmarteditRestService.getNewComponentTypes().then((resp) => {
            this.newComponentTypes = resp.componentTypes
                .filter((elem) => restrictions.indexOf(elem.code) > -1)
                .map((elem) => {
                elem.id = elem.code;
                return elem;
            });
            return this.newComponentTypes;
        }, () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponentstypes'));
        }), () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingslotrestrictions'));
        });
        this.getAndSetComponentById = (componentUuid) => {
            this.personalizationsmarteditRestService.getComponent(componentUuid).then((resp) => {
                this.idComponentSelected = resp.uuid;
            }, () => {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponents'));
            });
        };
        this.getAndSetColorAndLetter = () => {
            const combinedView = this.personalizationsmarteditContextService.getCombinedView();
            if (combinedView.enabled) {
                (combinedView.selectedItems || []).forEach((element, index) => {
                    let state = this.selectedCustomizationCode === element.customization.code;
                    state = state && this.selectedVariationCode === element.variation.code;
                    const wrappedIndex = index % Object.keys(personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
                    if (state) {
                        this.letterIndicatorForElement = String.fromCharCode('a'.charCodeAt(0) + wrappedIndex).toUpperCase();
                        this.colorIndicatorForElement =
                            personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
                    }
                });
            }
        };
        this.componentSelectedEvent = (item) => {
            if (!item) {
                return;
            }
            this.componentSelected = item;
            this.idComponentSelected = item.uuid;
        };
        this.newComponentTypeSelectedEvent = (item) => {
            if (!item) {
                return;
            }
            const componentAttributes = {
                smarteditComponentType: item.code,
                catalogVersionUuid: this.personalizationsmarteditContextService.getSeData().seExperienceData.pageContext
                    .catalogVersionUuid
            };
            this.editorModalService.open(componentAttributes).then((response) => {
                this.actionSelected = this.actions.filter((action) => action.id === 'use')[0].id;
                this.idComponentSelected = response.uuid;
                this.componentSelected = response;
            }, () => {
                this.newComponentSelected = '';
            });
        };
        this.editAction = (customizationId, variationId, actionId, componentId, componentCatalog, filter) => {
            this.personalizationsmarteditRestService
                .editAction(customizationId, variationId, actionId, componentId, componentCatalog, filter)
                .then(() => {
                this.personalizationsmarteditMessageHandler.sendSuccess(this.translateService.instant('personalization.info.updatingaction'));
                this.actionCreated.emit();
            }, () => {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.updatingaction'));
                this.actionCreated.emit();
            });
        };
        this.addActionToContainer = (componentId, catalogId, containerSourceId, customizationId, variationId, filter) => {
            this.personalizationsmarteditRestService
                .addActionToContainer(componentId, catalogId, containerSourceId, customizationId, variationId, filter)
                .then(() => {
                this.personalizationsmarteditMessageHandler.sendSuccess(this.translateService.instant('personalization.info.creatingaction'));
                this.actionCreated.emit(containerSourceId);
            }, () => {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.creatingaction'));
                this.actionCreated.emit();
            });
        };
        this.catalogVersionFilterChange = (value) => {
            if (!value) {
                return;
            }
            const arr = value.split('/');
            this.catalogFilter = arr[0];
            this.catalogVersionFilter = arr[1];
        };
        this.components = [];
        this.modalManager.addButton(this.modalButtons[0]);
        this.modalManager.addButton(this.modalButtons[1]);
        this.actionFetchStrategy = {
            fetchAll: () => Promise.resolve(this.actions)
        };
        this.componentsFetchStrategy = {
            fetchPage: (mask, pageSize, currentPage) => this.componentTypesFetchStrategy.fetchAll().then((componentTypes) => {
                const typeCodes = componentTypes.map((elem) => elem.code).join(',');
                const filter = {
                    currentPage,
                    mask,
                    typeCodes,
                    pageSize: 30,
                    sort: 'name',
                    catalog: this.catalogFilter,
                    catalogVersion: this.catalogVersionFilter
                };
                return this.personalizationsmarteditRestService.getComponents(filter).then((resp) => {
                    const filteredComponents = resp.response.filter((elem) => !elem.restricted);
                    return Promise.resolve({
                        results: filteredComponents,
                        pagination: resp.pagination
                    });
                }, () => {
                    this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponents'));
                    return Promise.reject();
                });
            }),
            fetchEntity: (uuid) => this.personalizationsmarteditRestService.getComponent(uuid).then((resp) => Promise.resolve({
                id: resp.uuid,
                name: resp.name,
                typeCode: resp.typeCode
            }))
        };
        this.componentTypesFetchStrategy = {
            fetchAll: () => {
                if (this.newComponentTypes) {
                    return Promise.resolve(this.newComponentTypes);
                }
                else {
                    return this.initNewComponentTypes();
                }
            }
        };
    }
    get modalData() {
        return this.modalManager.getModalData();
    }
    ngOnInit() {
        this.init();
    }
    init() {
        this.actions = [
            {
                id: 'create',
                name: this.translateService.instant('personalization.modal.addeditaction.createnewcomponent')
            },
            {
                id: 'use',
                name: this.translateService.instant('personalization.modal.addeditaction.usecomponent')
            }
        ];
        this.modalData.subscribe((config) => {
            this.colorIndicatorForElement = config.colorIndicatorForElement;
            this.slotId = config.slotId;
            this.actionId = config.actionId;
            this.componentUuid = config.componentUuid;
            this.defaultComponentId = config.componentId;
            this.editEnabled = config.editEnabled;
            this.slotCatalog = config.slotCatalog;
            this.componentCatalog = config.componentCatalog;
            this.selectedCustomizationCode = config.selectedCustomizationCode;
            this.selectedVariationCode = config.selectedVariationCode;
            this.componentType = config.componentType;
            this.personalizationsmarteditRestService
                .getCustomization({
                code: this.selectedCustomizationCode
            })
                .then((response) => {
                this.selectedCustomization = response;
                this.selectedVariation = response.variations.filter((elem) => elem.code === this.selectedVariationCode)[0];
            }, () => {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomization'));
            });
            if (this.editEnabled) {
                this.getAndSetComponentById(this.componentUuid);
                this.actionSelected = this.actions.filter((item) => item.id === 'use')[0].id;
            }
            else {
                this.actionSelected = '';
            }
        });
        this.initNewComponentTypes();
        this.getAndSetColorAndLetter();
    }
};
PersonalizationsmarteditContextMenuAddEditActionComponent = __decorate([
    core.Component({
        template: `<div class="perso-customize-component"><div class="perso-customize-component__title-layout"><div *ngIf="letterIndicatorForElement" class="perso-customize-component__title-layout__letter-block"><span [ngClass]="colorIndicatorForElement">{{letterIndicatorForElement}}</span></div><div class="perso-customize-component__title-layout__cust-name perso-wrap-ellipsis" title="{{selectedCustomization.name}}">{{selectedCustomization.name}}</div><div class="perso-customize-component__title-layout__target-group-name perso-wrap-ellipsis" title="{{selectedVariation.name}}">{{'> '+ selectedVariation.name}}</div></div><dl class="perso-customize-component__data-list"><label fd-form-label for="perso-customize-component-type-desc">{{ "personalization.modal.addeditaction.selected.mastercomponent.title" | translate }}</label><dd id="perso-customize-component-type-desc">{{componentType}}</dd></dl><label fd-form-label class="se-control-label" [required]="true" for="perso-customize-component-action-select">{{ "personalization.modal.addeditaction.selected.actions.title" | translate }}</label><se-tooltip [placement]="'top-start'" [triggers]="['mouseenter', 'mouseleave']" [isChevronVisible]="true"><span class="sap-icon--question-mark sap-icon" style="margin-left: 10px; padding-top: 1px" se-tooltip-trigger></span><div se-tooltip-body [translate]="'personalization.modal.addeditaction.selected.actions.help.label'"></div></se-tooltip><se-select id="perso-customize-component-action-select" class="perso-customize-component__select2-container" [placeholder]="'personalization.modal.addeditaction.dropdown.placeholder'" [(model)]="actionSelected" [searchEnabled]="false" [showRemoveButton]="false" [fetchStrategy]="actionFetchStrategy"></se-select><div class="perso-customize-component__select-group-label-layout"><div *ngIf="actionSelected == 'use'"><label fd-form-label class="se-control-label" [required]="true" for="perso-customize-component-saved-component-select">{{ "personalization.modal.addeditaction.selected.component.title" | translate }}</label></div><has-multicatalog *ngIf="actionSelected == 'use'"><div class="perso-customize-component__filter-layout"><label fd-form-label class="perso-customize-component__filter-label" for="perso-customize-component-catalog-version-filter-dropdown">{{ 'personalization.commons.filter.label' | translate }}</label><catalog-version-filter-dropdown id="perso-customize-component-catalog-version-filter-dropdown" class="pe-customize-component__catalog-version-filter-dropdown" (onSelectCallback)="catalogVersionFilterChange($event)"></catalog-version-filter-dropdown></div></has-multicatalog></div><se-select id="perso-customize-component-saved-component-select" class="perso-customize-component__select2-container" *ngIf="actionSelected == 'use'" [placeholder]="'personalization.modal.addeditaction.dropdown.componentlist.placeholder'" [(model)]="idComponentSelected" [onSelect]="componentSelectedEvent" [searchEnabled]="true" [showRemoveButton]="false" [fetchStrategy]="componentsFetchStrategy" [itemComponent]="itemComponent"></se-select><se-select class="perso-customize-component__select2-container" *ngIf="actionSelected == 'create'" [placeholder]="'personalization.modal.addeditaction.dropdown.componenttype.placeholder'" [(model)]="newComponentSelected" [onSelect]="newComponentTypeSelectedEvent" [searchEnabled]="false" [fetchStrategy]="componentTypesFetchStrategy"></se-select></div>`
    }),
    __param(0, core.Inject(smarteditcommons.ModalManagerService)),
    __param(1, core.Inject(core$1.TranslateService)),
    __param(2, core.Inject(exports.PersonalizationsmarteditRestService)),
    __param(3, core.Inject(personalizationcommons.PersonalizationsmarteditMessageHandler)),
    __param(4, core.Inject(exports.PersonalizationsmarteditContextService)),
    __metadata("design:paramtypes", [smarteditcommons.ModalManagerService,
        core$1.TranslateService,
        exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        exports.PersonalizationsmarteditContextService,
        smarteditcommons.ISlotRestrictionsService,
        smarteditcommons.IEditorModalService])
], PersonalizationsmarteditContextMenuAddEditActionComponent);

const MODAL_BUTTONS = [
    {
        id: 'confirmOk',
        label: 'personalization.modal.deleteaction.button.ok',
        style: smarteditcommons.ModalButtonStyle.Primary,
        action: smarteditcommons.ModalButtonAction.Close
    },
    {
        id: 'confirmCancel',
        label: 'personalization.modal.deleteaction.button.cancel',
        style: smarteditcommons.ModalButtonStyle.Default,
        action: smarteditcommons.ModalButtonAction.Dismiss
    }
];
/* @ngInject */ exports.PersonalizationsmarteditContextMenuServiceProxy = class /* @ngInject */ PersonalizationsmarteditContextMenuServiceProxy extends personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy {
    constructor(modalService, renderService, editorModalService, personalizationsmarteditContextService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, translateService, logService) {
        super();
        this.modalService = modalService;
        this.renderService = renderService;
        this.editorModalService = editorModalService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.translateService = translateService;
        this.logService = logService;
    }
    openDeleteAction(config) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!(yield this.confirmDelete())) {
                return;
            }
            yield this.deleteContext(config);
            const combinedView = this.personalizationsmarteditContextService.getCombinedView();
            if (combinedView.enabled) {
                try {
                    const response = yield this.personalizationsmarteditRestService.getActions(config.selectedCustomizationCode, config.selectedVariationCode, config);
                    if (combinedView.customize.selectedComponents) {
                        combinedView.customize.selectedComponents = this.removeItemFromCollection(combinedView.customize.selectedComponents, config.containerSourceId);
                    }
                    combinedView.selectedItems = this.addActionsToVariation(combinedView.selectedItems, config, response.actions);
                    this.personalizationsmarteditContextService.setCombinedView(combinedView);
                }
                catch (_a) {
                    this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingactions'));
                }
            }
            else {
                const customize = this.personalizationsmarteditContextService.getCustomize();
                customize.selectedComponents = this.removeItemFromCollection(customize.selectedComponents, config.containerSourceId);
                this.personalizationsmarteditContextService.setCustomize(customize);
            }
            this.renderService.renderSlots(config.slotsToRefresh);
        });
    }
    openAddAction(config) {
        return __awaiter(this, void 0, void 0, function* () {
            const resultContainer = yield this.modalService
                .open({
                component: PersonalizationsmarteditContextMenuAddEditActionComponent,
                data: config,
                templateConfig: {
                    title: 'personalization.modal.addaction.title',
                    isDismissButtonVisible: true
                },
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'yPersonalizationContextModal'
                }
            })
                .afterClosed.toPromise();
            const combinedView = this.personalizationsmarteditContextService.getCombinedView();
            if (combinedView.enabled) {
                combinedView.customize.selectedComponents.push(resultContainer);
                this.personalizationsmarteditContextService.setCombinedView(combinedView);
            }
            else {
                const customize = this.personalizationsmarteditContextService.getCustomize();
                customize.selectedComponents.push(resultContainer);
                this.personalizationsmarteditContextService.setCustomize(customize);
            }
            this.renderService.renderSlots(config.slotsToRefresh);
        });
    }
    openEditAction(config) {
        return __awaiter(this, void 0, void 0, function* () {
            config.editEnabled = true;
            yield this.modalService
                .open({
                component: PersonalizationsmarteditContextMenuAddEditActionComponent,
                data: config,
                templateConfig: {
                    title: 'personalization.modal.editaction.title',
                    isDismissButtonVisible: true
                },
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'yPersonalizationContextModal'
                }
            })
                .afterClosed.toPromise();
            this.renderService.renderSlots(config.slotsToRefresh);
        });
    }
    openEditComponentAction(config) {
        this.editorModalService.open(config);
    }
    deleteContext(config) {
        return __awaiter(this, void 0, void 0, function* () {
            const filter = {
                catalog: config.catalog,
                catalogVersion: config.catalogVersion
            };
            try {
                yield this.personalizationsmarteditRestService.deleteAction(config.selectedCustomizationCode, config.selectedVariationCode, config.actionId, filter);
            }
            catch (error) {
                this.logService.error('An error occurred while deleting context', error);
            }
        });
    }
    removeItemFromCollection(collection, itemToRemove) {
        return collection.filter((item) => item !== itemToRemove);
    }
    addActionsToVariation(items, config, actions) {
        return items.map((item) => {
            if (item.customization.code === config.selectedCustomizationCode &&
                item.variation.code === config.selectedVariationCode) {
                item.variation.actions = actions;
            }
            return item;
        });
    }
    confirmDelete() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.modalService
                    .open({
                    component: ContextMenuDeleteActionComponent,
                    config: {
                        dialogPanelClass: 'yFrontModal modal-md'
                    },
                    templateConfig: {
                        buttons: MODAL_BUTTONS,
                        title: 'personalization.modal.deleteaction.title'
                    }
                })
                    .afterClosed.toPromise();
                return true;
            }
            catch (_a) {
                return false;
            }
        });
    }
};
/* @ngInject */ exports.PersonalizationsmarteditContextMenuServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('openDeleteAction', 'openAddAction', 'openEditAction', 'openEditComponentAction'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IModalService,
        smarteditcommons.IRenderService,
        smarteditcommons.IEditorModalService,
        exports.PersonalizationsmarteditContextService,
        exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        core$1.TranslateService,
        smarteditcommons.LogService])
], /* @ngInject */ exports.PersonalizationsmarteditContextMenuServiceProxy);

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditContextMenuModule
 */
let PersonalizationsmarteditContextMenuModule = class PersonalizationsmarteditContextMenuModule {
};
PersonalizationsmarteditContextMenuModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.SharedComponentsModule,
            form.FormModule,
            inlineHelp.InlineHelpModule,
            smarteditcommons.SelectModule,
            smarteditcommons.TooltipModule,
            PersonalizationsmarteditSharedComponentsModule
        ],
        declarations: [
            PersonalizationsmarteditContextMenuAddEditActionComponent,
            ComponentDropdownItemPrinterComponent,
            ContextMenuDeleteActionComponent
        ],
        entryComponents: [
            PersonalizationsmarteditContextMenuAddEditActionComponent,
            ComponentDropdownItemPrinterComponent,
            ContextMenuDeleteActionComponent
        ],
        exports: [
            PersonalizationsmarteditContextMenuAddEditActionComponent,
            ComponentDropdownItemPrinterComponent
        ]
    })
], PersonalizationsmarteditContextMenuModule);

/* @ngInject */ exports.CustomizeViewServiceProxy = class /* @ngInject */ CustomizeViewServiceProxy {
    getSourceContainersInfo() {
        'proxyFunction';
        return null;
    }
};
/* @ngInject */ exports.CustomizeViewServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('getSourceContainersInfo'),
    core.Injectable()
], /* @ngInject */ exports.CustomizeViewServiceProxy);

const defaultCustomization = {
    name: '',
    enabledStartDate: '',
    enabledEndDate: '',
    status: personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
    statusBoolean: true,
    variations: [],
    catalog: '',
    catalogVersion: '',
    code: '',
    rank: null
};
const defaultCustomizationViewState = {
    customization: defaultCustomization,
    visibleVariations: []
};
/* @ngInject */ exports.CustomizationViewService = class /* @ngInject */ CustomizationViewService {
    constructor(persoUtils) {
        this.persoUtils = persoUtils;
        this.editVariationActionSubject = new rxjs.BehaviorSubject(undefined);
        this.stateSubject = new rxjs.BehaviorSubject(defaultCustomizationViewState);
    }
    getState$() {
        return this.stateSubject.asObservable();
    }
    getState() {
        return this.stateSubject.getValue();
    }
    setState(state) {
        this.stateSubject.next(state);
    }
    addVariation(variation) {
        const customization = this.selectCustomization();
        const variations = [...customization.variations, variation];
        this.setVariations(variations);
    }
    setVariations(variations) {
        const customization = this.selectCustomization();
        customization.variations = variations.map((variation, idx) => {
            variation.rank = idx;
            return variation;
        });
        this.setState(this.prepareState(customization));
    }
    setCustomization(customization) {
        this.setState(this.prepareState(customization));
    }
    getCustomization$() {
        return this.getState$().pipe(operators.map((state) => state.customization));
    }
    // Actions
    editVariationAction(variation) {
        this.editVariationActionSubject.next(variation);
    }
    editVariationAction$() {
        return this.editVariationActionSubject.asObservable();
    }
    // Selectors
    selectCustomization() {
        return this.getState().customization;
    }
    selectVisibleVariations() {
        return this.getState().visibleVariations;
    }
    selectVariationByCode(code) {
        return this.getState().customization.variations.find((variation) => variation.code === code);
    }
    /** Calculate visible items */
    prepareState(customization) {
        return {
            customization,
            visibleVariations: this.persoUtils.getVisibleItems(customization.variations)
        };
    }
};
/* @ngInject */ exports.CustomizationViewService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditUtils])
], /* @ngInject */ exports.CustomizationViewService);

window.__smartedit__.addDecoratorPayload("Component", "BasicInfoTabComponent", {
    selector: 'basic-info-tab',
    template: `<div class="pe-customization-modal" *ngIf="(customization$ | async) as customization"><form><div fd-form-item><label fd-form-label [required]="true" for="customization-name" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.name' | translate }}</label> <input fd-form-control type="text" [placeholder]="'personalization.modal.customizationvariationmanagement.basicinformationtab.name.placeholder' | translate" [name]="customization.name + '_key'" [ngModel]="customization.name" (ngModelChange)="changeName($event)" id="customization-name"/></div><div fd-form-item><label fd-form-label for="customization-description" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details' | translate }}</label> <textarea fd-form-control rows="2" class="fd-form__control pe-customization-modal__textarea" [placeholder]="'personalization.modal.customizationvariationmanagement.basicinformationtab.details.placeholder' | translate" [name]="customization.description + '_key'" [ngModel]="customization.description" (ngModelChange)="changeDescription($event)" id="customization-description"></textarea></div><div fd-form-item class="fd-has-padding-bottom-tiny"><label fd-form-label for="customization-status" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.status' | translate }}</label><fd-switch [compact]="true" [ngModel]="customization.statusBoolean" id="test-checkbox" (ngModelChange)="toggleCustomizationStatus($event)"></fd-switch></div><div fd-form-item><div><button *ngIf="!datetimeConfigurationEnabled" class="se-button--text se-button--link fd-link" (click)="toggleDatetimeConfiguration()" type="button" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.showdateconfigdata"></button> <button *ngIf="datetimeConfigurationEnabled" class="se-button--text se-button--link fd-link" (click)="toggleDatetimeConfiguration(true)" type="button" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.hidedateconfigdata"></button></div><div translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.statusfortimeframe.description" class="fd-has-padding-top-tiny"></div></div><div fd-form-item><datetime-picker-range *ngIf="datetimeConfigurationEnabled" [dateFrom]="customization.enabledStartDate" [dateTo]="customization.enabledEndDate" [isEditable]="true" (dateFromChange)="onEnabledStartDateChange($event)" (dateToChange)="onEnabledEndDateChange($event)"></datetime-picker-range></div></form></div>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
exports.BasicInfoTabComponent = class BasicInfoTabComponent {
    constructor(customizationViewService, cdr) {
        this.customizationViewService = customizationViewService;
        this.cdr = cdr;
        this.datetimeConfigurationEnabled = false;
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.customization$ = this.customizationViewService.getCustomization$();
            const customization = yield this.customizationViewService
                .getCustomization$()
                .pipe(operators.take(1))
                .toPromise();
            this.datetimeConfigurationEnabled =
                !!customization.enabledStartDate || !!customization.enabledEndDate;
            this.cdr.detectChanges();
        });
    }
    changeName(name) {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { name }));
    }
    changeDescription(description) {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { description }));
    }
    toggleDatetimeConfiguration(reset = false) {
        this.datetimeConfigurationEnabled = !this.datetimeConfigurationEnabled;
        if (reset) {
            this.resetDatetimeConfiguration();
        }
    }
    toggleCustomizationStatus(statusBoolean) {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { statusBoolean, status: statusBoolean
                ? personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
                : personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED }));
    }
    onEnabledStartDateChange(date) {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { enabledStartDate: date }));
    }
    onEnabledEndDateChange(date) {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { enabledEndDate: date }));
    }
    resetDatetimeConfiguration() {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization(Object.assign(Object.assign({}, customization), { enabledStartDate: undefined, enabledEndDate: undefined }));
    }
};
exports.BasicInfoTabComponent = __decorate([
    core.Component({
        selector: 'basic-info-tab',
        template: `<div class="pe-customization-modal" *ngIf="(customization$ | async) as customization"><form><div fd-form-item><label fd-form-label [required]="true" for="customization-name" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.name' | translate }}</label> <input fd-form-control type="text" [placeholder]="'personalization.modal.customizationvariationmanagement.basicinformationtab.name.placeholder' | translate" [name]="customization.name + '_key'" [ngModel]="customization.name" (ngModelChange)="changeName($event)" id="customization-name"/></div><div fd-form-item><label fd-form-label for="customization-description" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details' | translate }}</label> <textarea fd-form-control rows="2" class="fd-form__control pe-customization-modal__textarea" [placeholder]="'personalization.modal.customizationvariationmanagement.basicinformationtab.details.placeholder' | translate" [name]="customization.description + '_key'" [ngModel]="customization.description" (ngModelChange)="changeDescription($event)" id="customization-description"></textarea></div><div fd-form-item class="fd-has-padding-bottom-tiny"><label fd-form-label for="customization-status" class="se-control-label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.status' | translate }}</label><fd-switch [compact]="true" [ngModel]="customization.statusBoolean" id="test-checkbox" (ngModelChange)="toggleCustomizationStatus($event)"></fd-switch></div><div fd-form-item><div><button *ngIf="!datetimeConfigurationEnabled" class="se-button--text se-button--link fd-link" (click)="toggleDatetimeConfiguration()" type="button" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.showdateconfigdata"></button> <button *ngIf="datetimeConfigurationEnabled" class="se-button--text se-button--link fd-link" (click)="toggleDatetimeConfiguration(true)" type="button" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.hidedateconfigdata"></button></div><div translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.statusfortimeframe.description" class="fd-has-padding-top-tiny"></div></div><div fd-form-item><datetime-picker-range *ngIf="datetimeConfigurationEnabled" [dateFrom]="customization.enabledStartDate" [dateTo]="customization.enabledEndDate" [isEditable]="true" (dateFromChange)="onEnabledStartDateChange($event)" (dateToChange)="onEnabledEndDateChange($event)"></datetime-picker-range></div></form></div>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [exports.CustomizationViewService,
        core.ChangeDetectorRef])
], exports.BasicInfoTabComponent);

let /* @ngInject */ ModalFullScreenButtonService = class /* @ngInject */ ModalFullScreenButtonService {
    constructor(rendererFactory) {
        this.className = 'sliderPanelParentModal';
        this.renderer = rendererFactory.createRenderer(null, null);
    }
    toggleClassOnCustomizationModal(childElement, classNameToToggle, toggleState) {
        const modal = this.findModal(childElement);
        if (!modal) {
            return;
        }
        if (toggleState) {
            this.renderer.addClass(modal, classNameToToggle);
        }
        else {
            this.renderer.removeClass(modal, classNameToToggle);
        }
    }
    findModal(element) {
        return element.closest(`.${this.className}`);
    }
};
/* @ngInject */ ModalFullScreenButtonService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core.RendererFactory2])
], /* @ngInject */ ModalFullScreenButtonService);

window.__smartedit__.addDecoratorPayload("Component", "ModalFullScreenButtonComponent", {
    selector: 'perso-modal-full-screen-button',
    template: `<button class="fd-button fd-button--light pe-customization-modal__sliderpanel__btn-link" (click)="toggle()"><fd-icon class="icon_padding_end" *ngIf="!isFullscreen" glyph="full-screen"></fd-icon><fd-icon class="icon_padding_end" *ngIf="isFullscreen" glyph="exitfullscreen"></fd-icon>{{(isFullscreen ? 'personalization.modal.customizationvariationmanagement.targetgrouptab.fullscreen.close' : 'personalization.modal.customizationvariationmanagement.targetgrouptab.fullscreen.open') | translate}}</button>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    providers: [ModalFullScreenButtonService]
});
let ModalFullScreenButtonComponent = class ModalFullScreenButtonComponent {
    constructor(element, modalFullScreenButtonService) {
        this.element = element;
        this.modalFullScreenButtonService = modalFullScreenButtonService;
        this.isFullscreen = false;
    }
    toggle(state) {
        this.isFullscreen = typeof state === 'undefined' ? !this.isFullscreen : state;
        this.modalFullScreenButtonService.toggleClassOnCustomizationModal(this.element.nativeElement, 'modal-fullscreen', this.isFullscreen);
    }
};
ModalFullScreenButtonComponent = __decorate([
    core.Component({
        selector: 'perso-modal-full-screen-button',
        template: `<button class="fd-button fd-button--light pe-customization-modal__sliderpanel__btn-link" (click)="toggle()"><fd-icon class="icon_padding_end" *ngIf="!isFullscreen" glyph="full-screen"></fd-icon><fd-icon class="icon_padding_end" *ngIf="isFullscreen" glyph="exitfullscreen"></fd-icon>{{(isFullscreen ? 'personalization.modal.customizationvariationmanagement.targetgrouptab.fullscreen.close' : 'personalization.modal.customizationvariationmanagement.targetgrouptab.fullscreen.open') | translate}}</button>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        providers: [ModalFullScreenButtonService]
    }),
    __metadata("design:paramtypes", [core.ElementRef,
        ModalFullScreenButtonService])
], ModalFullScreenButtonComponent);

let ModalFullScreenButtonModule = class ModalFullScreenButtonModule {
};
ModalFullScreenButtonModule = __decorate([
    core.NgModule({
        imports: [core$2.FundamentalNgxCoreModule, common.CommonModule, smarteditcommons.TranslationModule.forChild()],
        providers: [ModalFullScreenButtonService],
        declarations: [ModalFullScreenButtonComponent],
        exports: [ModalFullScreenButtonComponent]
    })
], ModalFullScreenButtonModule);

window.__smartedit__.addDecoratorPayload("Component", "EditVariationItemComponent", {
    selector: 'perso-edit-variation-item',
    template: `
        <a
            (click)="editVariation()"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.edit"
        ></a>
    `,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let EditVariationItemComponent = class EditVariationItemComponent {
    constructor(dropdownMenuData, customizationViewService) {
        this.customizationViewService = customizationViewService;
        ({ selectedItem: this.selectedItem } = dropdownMenuData);
    }
    editVariation() {
        this.customizationViewService.editVariationAction(this.selectedItem.variation);
    }
};
EditVariationItemComponent = __decorate([
    core.Component({
        selector: 'perso-edit-variation-item',
        template: `
        <a
            (click)="editVariation()"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.edit"
        ></a>
    `,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [Object, exports.CustomizationViewService])
], EditVariationItemComponent);

/* @ngInject */ exports.TriggerService = class /* @ngInject */ TriggerService {
    constructor() {
        this.actions = [
            {
                id: personalizationcommons.TriggerActionId.AND,
                name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
            },
            {
                id: personalizationcommons.TriggerActionId.OR,
                name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
            },
            {
                id: personalizationcommons.TriggerActionId.NOT,
                name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
            }
        ];
        this.supportedTypes = [
            personalizationcommons.TriggerType.DEFAULT_TRIGGER,
            personalizationcommons.TriggerType.SEGMENT_TRIGGER,
            personalizationcommons.TriggerType.EXPRESSION_TRIGGER
        ];
    }
    isContainer(element) {
        return this.isElementOfType(element, personalizationcommons.TriggerType.CONTAINER_TYPE);
    }
    isEmptyContainer(element) {
        return this.isContainer(element) && element.nodes.length === 0;
    }
    isNotEmptyContainer(element) {
        return this.isContainer(element) && element.nodes.length > 0;
    }
    isDropzone(element) {
        return this.isElementOfType(element, personalizationcommons.TriggerType.DROPZONE_TYPE);
    }
    isItem(element) {
        return this.isElementOfType(element, personalizationcommons.TriggerType.ITEM_TYPE);
    }
    isValidExpression(element) {
        if (!element) {
            return false;
        }
        if (this.isContainer(element)) {
            return (element.nodes &&
                element.nodes.length > 0 &&
                element.nodes.every((node) => this.isValidExpression(node)));
        }
        else {
            return element.selectedSegment !== undefined;
        }
    }
    buildTriggers(form, existingTriggers) {
        let trigger = {};
        if (this.isDefaultData(form)) {
            trigger = this.buildDefaultTrigger();
        }
        else if (!!(form === null || form === void 0 ? void 0 : form.expression) && form.expression.length > 0) {
            const element = form.expression[0];
            if (this.isEmptyContainer(element)) {
                trigger = {};
            }
            else if (this.isExpressionData(element)) {
                trigger = this.buildExpressionTrigger(element);
            }
            else {
                trigger = this.buildSegmentTrigger(element);
            }
        }
        return this.mergeTriggers(existingTriggers, trigger);
    }
    buildData(triggers) {
        let trigger = {};
        let data = this.getBaseData();
        if (triggers && Array.isArray(triggers) && triggers.length > 0) {
            trigger = triggers.filter((elem) => this.isSupportedTrigger(elem))[0];
        }
        if (this.isDefaultTrigger(trigger)) ;
        else if (this.isExpressionTrigger(trigger)) {
            data = this.buildExpressionTriggerData(trigger);
        }
        else if (this.isSegmentTrigger(trigger)) {
            data = this.buildSegmentTriggerData(trigger);
        }
        return data;
    }
    isDefault(triggers) {
        const defaultTrigger = (triggers || []).filter((elem) => this.isDefaultTrigger(elem))[0];
        return triggers && defaultTrigger ? true : false;
    }
    getExpressionAsString(expressionContainer) {
        let retStr = '';
        if (expressionContainer === undefined) {
            return retStr;
        }
        const currOperator = this.getCurrOperator(expressionContainer);
        retStr = this.getRetStr(retStr, expressionContainer) + '(';
        if (this.isEmptyContainer(expressionContainer)) {
            retStr += ' [] ';
        }
        else {
            expressionContainer.nodes.forEach((element, index) => {
                if (this.isEmptyContainer(element)) {
                    retStr = this.getEmptyEleContainerRetStr(retStr, index, currOperator);
                }
                else {
                    retStr = this.getNonEmptyEleContainerRetStr(retStr, index, currOperator);
                    retStr += this.isItem(element)
                        ? element.selectedSegment.code
                        : this.getExpressionAsString(element);
                }
            });
        }
        retStr += ')';
        return retStr;
    }
    getCurrOperator(expressionContainer) {
        return this.isNegation(expressionContainer) ? 'AND' : expressionContainer.operation.id;
    }
    getRetStr(retStr, expressionContainer) {
        retStr += this.isNegation(expressionContainer) ? ' NOT ' : '';
        return retStr;
    }
    getEmptyEleContainerRetStr(retStr, index, currOperator) {
        const emptyStr = ' ';
        const emptyBracketsStr = ' ( [] )';
        const bracketsStr = '( [] )';
        retStr += index > 0 ? emptyStr + currOperator + emptyBracketsStr : bracketsStr;
        return retStr;
    }
    getNonEmptyEleContainerRetStr(retStr, index, currOperator) {
        const emptyStr = ' ';
        retStr += index > 0 ? emptyStr + currOperator + emptyStr : '';
        return retStr;
    }
    isElementOfType(element, myType) {
        return typeof element !== 'undefined' ? element.type === myType : false;
    }
    isNegation(element) {
        return this.isContainer(element) && element.operation.id === 'NOT';
    }
    isDefaultData(form) {
        return !!(form === null || form === void 0 ? void 0 : form.isDefault);
    }
    isExpressionData(element) {
        return (element.operation.id === personalizationcommons.TriggerActionId.NOT ||
            element.nodes.some((item) => !this.isItem(item)));
    }
    isSupportedTrigger(trigger) {
        return this.supportedTypes.indexOf(trigger.type) >= 0;
    }
    isDefaultTrigger(trigger) {
        return this.isElementOfType(trigger, personalizationcommons.TriggerType.DEFAULT_TRIGGER);
    }
    isSegmentTrigger(trigger) {
        return this.isElementOfType(trigger, personalizationcommons.TriggerType.SEGMENT_TRIGGER);
    }
    isExpressionTrigger(trigger) {
        return this.isElementOfType(trigger, personalizationcommons.TriggerType.EXPRESSION_TRIGGER);
    }
    isGroupExpressionData(expression) {
        return this.isElementOfType(expression, personalizationcommons.TriggerType.GROUP_EXPRESSION);
    }
    isSegmentExpressionData(expression) {
        return this.isElementOfType(expression, personalizationcommons.TriggerType.SEGMENT_EXPRESSION);
    }
    isNegationExpressionData(expression) {
        return this.isElementOfType(expression, personalizationcommons.TriggerType.NEGATION_EXPRESSION);
    }
    // ------------------------ FORM DATA -> TRIGGER ---------------------------
    buildSegmentsForTrigger(element) {
        return element.nodes
            .filter((node) => this.isItem(node))
            .map((node) => node.selectedSegment);
    }
    buildExpressionForTrigger(element) {
        if (this.isNegation(element)) {
            const negationElements = [];
            element.nodes.forEach((node) => {
                negationElements.push(this.buildExpressionForTrigger(node));
            });
            return {
                type: personalizationcommons.TriggerType.NEGATION_EXPRESSION,
                element: {
                    type: personalizationcommons.TriggerType.GROUP_EXPRESSION,
                    operator: personalizationcommons.TriggerActionId.AND,
                    elements: negationElements
                }
            };
        }
        else if (this.isContainer(element)) {
            const groupElements = [];
            element.nodes.forEach((node) => {
                groupElements.push(this.buildExpressionForTrigger(node));
            });
            return {
                type: personalizationcommons.TriggerType.GROUP_EXPRESSION,
                operator: element.operation.id,
                elements: groupElements
            };
        }
        else {
            return {
                type: personalizationcommons.TriggerType.SEGMENT_EXPRESSION,
                code: element.selectedSegment.code
            };
        }
    }
    buildDefaultTrigger() {
        return {
            type: personalizationcommons.TriggerType.DEFAULT_TRIGGER
        };
    }
    buildExpressionTrigger(element) {
        return {
            type: personalizationcommons.TriggerType.EXPRESSION_TRIGGER,
            expression: this.buildExpressionForTrigger(element)
        };
    }
    buildSegmentTrigger(element) {
        return {
            type: personalizationcommons.TriggerType.SEGMENT_TRIGGER,
            groupBy: element.operation.id,
            segments: this.buildSegmentsForTrigger(element)
        };
    }
    mergeTriggers(triggers, target) {
        if (typeof triggers === 'undefined') {
            return [target];
        }
        const index = triggers.findIndex((t) => t.type === target.type);
        if (index >= 0) {
            target.code = triggers[index].code;
        }
        // remove other instanced of supported types (there can be only one) but maintain unsupported types
        const result = triggers.filter((trigger) => !this.isSupportedTrigger(trigger));
        result.push(target);
        return result;
    }
    // ------------------------ TRIGGER -> FORM DATA ---------------------------
    buildContainer(actionId) {
        const action = this.actions.filter((a) => a.id === actionId)[0];
        return {
            type: personalizationcommons.TriggerType.CONTAINER_TYPE,
            operation: action,
            nodes: [],
            uid: smarteditcommons.stringUtils.generateIdentifier()
        };
    }
    buildItem(value) {
        return {
            type: personalizationcommons.TriggerType.ITEM_TYPE,
            operation: null,
            selectedSegment: {
                code: value
            },
            nodes: [],
            uid: smarteditcommons.stringUtils.generateIdentifier()
        };
    }
    getBaseData() {
        const data = this.buildContainer(personalizationcommons.TriggerActionId.AND);
        return [data];
    }
    buildExpressionFromTrigger(expression) {
        let data;
        if (this.isGroupExpressionData(expression)) {
            data = this.buildContainer(expression.operator);
            data.nodes = expression.elements.map((item) => this.buildExpressionFromTrigger(item));
        }
        else if (this.isNegationExpressionData(expression)) {
            data = this.buildContainer(personalizationcommons.TriggerActionId.NOT);
            const element = this.buildExpressionFromTrigger(expression.element);
            if (this.isGroupExpressionData(expression.element) &&
                expression.element.operator === 'AND') {
                data.nodes = element.nodes;
            }
            else {
                data.nodes.push(element);
            }
        }
        else if (this.isSegmentExpressionData(expression)) {
            data = this.buildItem(expression.code);
        }
        return data;
    }
    buildSegmentTriggerData(trigger) {
        const data = this.buildContainer(trigger.groupBy);
        trigger.segments.forEach((segment) => {
            data.nodes.push(this.buildItem(segment.code));
        });
        return [data];
    }
    buildExpressionTriggerData(trigger) {
        const data = this.buildExpressionFromTrigger(trigger.expression);
        return [data];
    }
};
/* @ngInject */ exports.TriggerService = __decorate([
    core.Injectable()
], /* @ngInject */ exports.TriggerService);

window.__smartedit__.addDecoratorPayload("Component", "TargetGroupTabComponent", {
    selector: 'perso-target-group-tab',
    template: `<div class="pe-customization-modal" *ngIf="customization"><div class="pe-customization-modal__title"><div class="pe-customization-modal__title-header"><div class="pe-customization-modal__title-header-name perso-wrap-ellipsis" [attr.title]="customization.name">{{ customization.name }}</div><div><span class="pe-customization-modal__title-header-badge badge badge-success" [ngClass]="[getActivityStateForCustomization(customization)]">{{'personalization.modal.customizationvariationmanagement.targetgrouptab.customization.' + customization.status | lowercase | translate}}</span></div></div><div class="pe-customization-modal__title-subarea"><div *ngIf="isCustomizationEnabled(customization)" class="pe-customization-modal__title-dates"><span *ngIf="!customization.enabledStartDate && customization.enabledEndDate">...</span> <span [ngClass]="{'perso__datetimepicker--error-text': !persoDateUtils.isDateValidOrEmpty(customization.enabledStartDate)}">{{ customization.enabledStartDate }}</span> <span *ngIf="customization.enabledStartDate || customization.enabledEndDate">- </span><span [ngClass]="{'perso__datetimepicker--error-text':!persoDateUtils.isDateValidOrEmpty(customization.enabledEndDate)}">{{ customization.enabledEndDate }}</span> <span *ngIf="persoDateUtils.isDateInThePast(customization.enabledEndDate)" class="section-help help-inline help-inline--section help-inline--tooltip"><span class="pe-datetime__warning-icon"></span> <span class="pe-help-block--inline help-block-inline help-block-inline--text" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.datetooltip"></span> </span><span *ngIf="customization.enabledStartDate && !customization.enabledEndDate">...</span></div></div></div><div class="pe-customization-modal__y-add-btn"><button fd-button label="{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.addtargetgroup.button' | translate }}" (click)="showAddVariationPanel()"></button></div><perso-target-group-variation-list *ngIf="visibleVariations.length > 0" [customization]="customization" [variations]="visibleVariations"></perso-target-group-variation-list><se-slider-panel [sliderPanelConfiguration]="sliderPanelConfiguration" [(sliderPanelHide)]="sliderPanelHide" [(sliderPanelShow)]="sliderPanelShow" class="pe-customization-modal__sliderpanel"><div *ngIf="isVariationLoaded"><div class="pe-customization-modal__sliderpanel__btn-layout"><perso-modal-full-screen-button></perso-modal-full-screen-button></div><div fd-form-item><label fd-form-label for="targetgroup-name" [required]="true">{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname' | translate }}</label> <input fd-form-control type="text" name="variationname_key" id="targetgroup-name" class="fd-form__control" [placeholder]="'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname.placeholder' | translate" [(ngModel)]="edit.name"/></div><fd-checkbox label="{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.default' | translate }}" [(ngModel)]="edit.isDefault" (ngModelChange)="showConfirmForDefaultTrigger($event)" id="targetgroup-isDefault-001"></fd-checkbox><div *ngIf="edit.showExpression"><segment-view [targetGroupState]="edit" (expressionChange)="onSegmentViewExpressionChange($event)"></segment-view></div></div></se-slider-panel></div>`
});
let TargetGroupTabComponent = class TargetGroupTabComponent {
    constructor(persoDateUtils, persoUtils, persoTriggerService, confirmationModalService, customizationViewService, logService, cdr) {
        this.persoDateUtils = persoDateUtils;
        this.persoUtils = persoUtils;
        this.persoTriggerService = persoTriggerService;
        this.confirmationModalService = confirmationModalService;
        this.customizationViewService = customizationViewService;
        this.logService = logService;
        this.cdr = cdr;
        this.visibleVariations = [];
        this.isVariationLoaded = false;
        this.isFullscreen = false;
        this.sliderPanelConfiguration = {
            modal: {
                showDismissButton: true,
                title: 'personalization.modal.customizationvariationmanagement.targetgrouptab.slidingpanel.title',
                cancel: {
                    label: 'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelchanges',
                    onClick: () => {
                        this.cancelChangesClick();
                    },
                    isDisabledFn: () => false
                },
                dismiss: {
                    onClick: () => {
                        this.cancelChangesClick();
                    },
                    label: '',
                    isDisabledFn: () => false
                },
                save: {
                    onClick: () => { },
                    label: '',
                    isDisabledFn: () => false
                }
            },
            cssSelector: '#y-modal-dialog'
        };
        this.edit = {
            code: '',
            name: '',
            expression: [],
            isDefault: false,
            showExpression: true,
            selectedVariation: undefined
        };
    }
    ngOnInit() {
        this.customizationViewStateSubscription = this.customizationViewService
            .getState$()
            .subscribe(({ customization, visibleVariations }) => {
            this.customization = customization;
            this.visibleVariations = visibleVariations;
            this.cdr.detectChanges();
        });
        this.editVariationSubscription = this.customizationViewService
            .editVariationAction$()
            .pipe(operators.filter((variation) => !!variation))
            .subscribe((variation) => {
            setTimeout(() => {
                this.showEditVariationPanel(variation);
            }, 100);
        });
    }
    ngOnDestroy() {
        var _a, _b;
        (_a = this.editVariationSubscription) === null || _a === void 0 ? void 0 : _a.unsubscribe();
        (_b = this.customizationViewStateSubscription) === null || _b === void 0 ? void 0 : _b.unsubscribe();
    }
    isCustomizationEnabled(customization) {
        return customization.status === personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }
    getActivityStateForCustomization(customization) {
        return this.persoUtils.getActivityStateForCustomization(customization);
    }
    toggleSliderFullscreen(enableFullscreen) {
        this.modalFullScreenButtonCmp.toggle(enableFullscreen);
    }
    onSegmentViewExpressionChange(expression) {
        this.edit.expression = expression;
    }
    setVariationRank(variationListItem, increaseValue, $event, firstOrLast) {
        if (firstOrLast) {
            $event.stopPropagation();
        }
        else {
            let fromIndex;
            if (variationListItem.isNew) {
                fromIndex = this.customization.variations.findIndex(({ tempcode }) => tempcode === variationListItem.tempcode);
            }
            else {
                fromIndex = this.customization.variations.findIndex(({ code }) => code === variationListItem.code);
            }
            const variation = this.customization.variations[fromIndex];
            const to = this.persoUtils.getValidRank(this.customization.variations, variation, increaseValue);
            const variations = [...this.customization.variations];
            if (to >= 0 && to < variations.length) {
                variations.splice(to, 0, variations.splice(fromIndex, 1)[0]);
                this.setVariations(variations);
            }
        }
    }
    showConfirmForDefaultTrigger(isDefault) {
        return __awaiter(this, void 0, void 0, function* () {
            if (isDefault && this.persoTriggerService.isValidExpression(this.edit.expression[0])) {
                try {
                    yield this.confirmationModalService.confirm({
                        description: 'personalization.modal.manager.targetgrouptab.defaulttrigger.content'
                    });
                    this.edit.showExpression = false;
                }
                catch (_a) {
                    this.edit.isDefault = false;
                }
            }
            else {
                this.edit.showExpression = !isDefault;
            }
        });
    }
    removeVariation(variation) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.confirmationModalService.confirm({
                    description: 'personalization.modal.manager.targetgrouptab.deletevariation.content'
                });
                if (variation.isNew) {
                    const newVariations = this.customization.variations.filter((_variation) => _variation.tempcode !== variation.tempcode);
                    this.setVariations(newVariations);
                }
                else {
                    variation.status = personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
                    this.updateVariation(variation);
                }
            }
            catch (_a) {
                this.logService.log('Remove Variation canceled');
            }
        });
    }
    updateVariation(variationToUpdate) {
        const newVariations = this.customization.variations.map((variation) => (variationToUpdate.isNew && variation.tempcode === variationToUpdate.tempcode) ||
            (!variationToUpdate.isNew && variation.code === variationToUpdate.code)
            ? variationToUpdate
            : variation);
        this.setVariations(newVariations);
    }
    showAddVariationPanel() {
        this.clearEditedVariationDetails();
        this.setSliderConfigForAdd();
        this.sliderPanelShow();
        this.edit.selectedVariation = { triggers: [] };
        setTimeout(() => {
            this.isVariationLoaded = true;
        }, 0);
    }
    showEditVariationPanel(variation) {
        this.setSliderConfigForEditing();
        this.edit.selectedVariation = variation;
        this.edit.code = variation.code;
        this.edit.name = variation.name;
        this.edit.isDefault = this.persoTriggerService.isDefault(variation.triggers);
        this.edit.showExpression = !this.edit.isDefault;
        this.isVariationLoaded = true;
        this.sliderPanelShow();
    }
    setSliderConfigForAdd() {
        this.sliderPanelConfiguration.modal.save.label =
            'personalization.modal.customizationvariationmanagement.targetgrouptab.addvariation';
        this.sliderPanelConfiguration.modal.save.isDisabledFn = () => !this.canSaveVariation();
        this.sliderPanelConfiguration.modal.save.onClick = () => {
            this.onAddVariation();
        };
    }
    setSliderConfigForEditing() {
        this.sliderPanelConfiguration.modal.save.label =
            'personalization.modal.customizationvariationmanagement.targetgrouptab.savechanges';
        this.sliderPanelConfiguration.modal.save.isDisabledFn = () => !this.canSaveVariation();
        this.sliderPanelConfiguration.modal.save.onClick = () => {
            this.onSaveEditedVariation();
        };
    }
    canSaveVariation() {
        const isValidOrEmpty = this.isSavedVariationValidOrEmpty();
        const isTriggerValid = this.persoTriggerService.isValidExpression(this.edit.expression[0]);
        const isNameValid = !smarteditcommons.stringUtils.isBlank(this.edit.name);
        const canSaveVariation = isNameValid && (this.edit.isDefault || (isTriggerValid && isValidOrEmpty));
        return canSaveVariation;
    }
    isSavedVariationValidOrEmpty() {
        this.edit.expression = this.edit.expression || [{ nodes: [] }];
        return (this.persoTriggerService.isValidExpression(this.edit.expression[0]) ||
            this.edit.expression.length === 0 ||
            this.edit.expression[0].nodes.length === 0);
    }
    setVariations(variations) {
        this.customizationViewService.setVariations(variations);
    }
    onAddVariation() {
        const random = 10000;
        this.customizationViewService.addVariation({
            tempcode: 'Temp' + (Math.random() * random).toFixed(0),
            code: this.edit.code,
            name: this.edit.name,
            enabled: true,
            status: personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
            triggers: this.persoTriggerService.buildTriggers(this.edit, this.edit.selectedVariation.triggers || []),
            rank: this.customization.variations.length,
            isNew: true,
            catalog: null,
            catalogVersion: null
        });
        this.clearEditedVariationDetails();
        this.toggleSliderFullscreen(false);
        this.sliderPanelHide();
        this.isVariationLoaded = false;
    }
    onSaveEditedVariation() {
        const triggers = this.persoTriggerService.buildTriggers(this.edit, this.edit.selectedVariation.triggers || []);
        const newVariations = this.customization.variations.map((variation) => variation.code === this.edit.selectedVariation.code
            ? Object.assign(Object.assign({}, variation), { triggers, name: this.edit.name }) : variation);
        this.setVariations(newVariations);
        this.toggleSliderFullscreen(false);
        this.sliderPanelHide();
        this.isVariationLoaded = false;
    }
    cancelChangesClick() {
        if (this.isVariationSelected()) {
            this.edit.selectedVariation = undefined;
        }
        else {
            this.clearEditedVariationDetails();
        }
        this.toggleSliderFullscreen(false);
        this.sliderPanelHide();
        this.isVariationLoaded = false;
    }
    isVariationSelected() {
        return typeof this.edit.selectedVariation !== 'undefined';
    }
    clearEditedVariationDetails() {
        this.edit.code = '';
        this.edit.name = '';
        this.edit.expression = [];
        this.edit.isDefault = false;
        this.edit.showExpression = true;
    }
};
__decorate([
    core.ViewChild(ModalFullScreenButtonComponent, { static: false }),
    __metadata("design:type", ModalFullScreenButtonComponent)
], TargetGroupTabComponent.prototype, "modalFullScreenButtonCmp", void 0);
TargetGroupTabComponent = __decorate([
    core.Component({
        selector: 'perso-target-group-tab',
        template: `<div class="pe-customization-modal" *ngIf="customization"><div class="pe-customization-modal__title"><div class="pe-customization-modal__title-header"><div class="pe-customization-modal__title-header-name perso-wrap-ellipsis" [attr.title]="customization.name">{{ customization.name }}</div><div><span class="pe-customization-modal__title-header-badge badge badge-success" [ngClass]="[getActivityStateForCustomization(customization)]">{{'personalization.modal.customizationvariationmanagement.targetgrouptab.customization.' + customization.status | lowercase | translate}}</span></div></div><div class="pe-customization-modal__title-subarea"><div *ngIf="isCustomizationEnabled(customization)" class="pe-customization-modal__title-dates"><span *ngIf="!customization.enabledStartDate && customization.enabledEndDate">...</span> <span [ngClass]="{'perso__datetimepicker--error-text': !persoDateUtils.isDateValidOrEmpty(customization.enabledStartDate)}">{{ customization.enabledStartDate }}</span> <span *ngIf="customization.enabledStartDate || customization.enabledEndDate">- </span><span [ngClass]="{'perso__datetimepicker--error-text':!persoDateUtils.isDateValidOrEmpty(customization.enabledEndDate)}">{{ customization.enabledEndDate }}</span> <span *ngIf="persoDateUtils.isDateInThePast(customization.enabledEndDate)" class="section-help help-inline help-inline--section help-inline--tooltip"><span class="pe-datetime__warning-icon"></span> <span class="pe-help-block--inline help-block-inline help-block-inline--text" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.datetooltip"></span> </span><span *ngIf="customization.enabledStartDate && !customization.enabledEndDate">...</span></div></div></div><div class="pe-customization-modal__y-add-btn"><button fd-button label="{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.addtargetgroup.button' | translate }}" (click)="showAddVariationPanel()"></button></div><perso-target-group-variation-list *ngIf="visibleVariations.length > 0" [customization]="customization" [variations]="visibleVariations"></perso-target-group-variation-list><se-slider-panel [sliderPanelConfiguration]="sliderPanelConfiguration" [(sliderPanelHide)]="sliderPanelHide" [(sliderPanelShow)]="sliderPanelShow" class="pe-customization-modal__sliderpanel"><div *ngIf="isVariationLoaded"><div class="pe-customization-modal__sliderpanel__btn-layout"><perso-modal-full-screen-button></perso-modal-full-screen-button></div><div fd-form-item><label fd-form-label for="targetgroup-name" [required]="true">{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname' | translate }}</label> <input fd-form-control type="text" name="variationname_key" id="targetgroup-name" class="fd-form__control" [placeholder]="'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname.placeholder' | translate" [(ngModel)]="edit.name"/></div><fd-checkbox label="{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.default' | translate }}" [(ngModel)]="edit.isDefault" (ngModelChange)="showConfirmForDefaultTrigger($event)" id="targetgroup-isDefault-001"></fd-checkbox><div *ngIf="edit.showExpression"><segment-view [targetGroupState]="edit" (expressionChange)="onSegmentViewExpressionChange($event)"></segment-view></div></div></se-slider-panel></div>`
    }),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditDateUtils,
        personalizationcommons.PersonalizationsmarteditUtils,
        exports.TriggerService,
        smarteditcommons.IConfirmationModalService,
        exports.CustomizationViewService,
        smarteditcommons.LogService,
        core.ChangeDetectorRef])
], TargetGroupTabComponent);

window.__smartedit__.addDecoratorPayload("Component", "ToggleVariationActiveItemComponent", {
    selector: 'perso-toggle-variation-active-item',
    template: `
        <a (click)="toggle()" class="se-dropdown-item fd-list__link" [translate]="label"></a>
    `,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let ToggleVariationActiveItemComponent = class ToggleVariationActiveItemComponent {
    constructor(dropdownMenuItemData, targetGroupTabComponent) {
        this.targetGroupTabComponent = targetGroupTabComponent;
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }
    ngOnInit() {
        this.label = this.selectedItem.variation.enabled
            ? 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable'
            : 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable';
    }
    toggle() {
        this.toggleVariationActive(this.selectedItem.variation);
    }
    toggleVariationActive(variation) {
        const enabled = !variation.enabled;
        const status = enabled
            ? personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            : personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        const newVariation = Object.assign(Object.assign({}, variation), { enabled,
            status });
        this.targetGroupTabComponent.updateVariation(newVariation);
    }
};
ToggleVariationActiveItemComponent = __decorate([
    core.Component({
        selector: 'perso-toggle-variation-active-item',
        template: `
        <a (click)="toggle()" class="se-dropdown-item fd-list__link" [translate]="label"></a>
    `,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __param(1, core.Inject(core.forwardRef(() => TargetGroupTabComponent))),
    __metadata("design:paramtypes", [Object, TargetGroupTabComponent])
], ToggleVariationActiveItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "RemoveVariationItemComponent", {
    selector: 'perso-remove-variation-item',
    template: `
        <a
            (click)="remove()"
            class="se-dropdown-item fd-list__link fd-has-color-status-3"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.remove"
        ></a>
    `,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let RemoveVariationItemComponent = class RemoveVariationItemComponent {
    constructor(dropdownMenuItemData, targetGroupTabComponent) {
        this.targetGroupTabComponent = targetGroupTabComponent;
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }
    remove() {
        this.targetGroupTabComponent.removeVariation(this.selectedItem.variation);
    }
};
RemoveVariationItemComponent = __decorate([
    core.Component({
        selector: 'perso-remove-variation-item',
        template: `
        <a
            (click)="remove()"
            class="se-dropdown-item fd-list__link fd-has-color-status-3"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.remove"
        ></a>
    `,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __param(1, core.Inject(core.forwardRef(() => TargetGroupTabComponent))),
    __metadata("design:paramtypes", [Object, TargetGroupTabComponent])
], RemoveVariationItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "MoveVariationUpItemComponent", {
    selector: 'perso-set-variation-rank-up-item',
    template: `
        <a
            (click)="moveUp($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isFirst }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.moveup"
        ></a>
    `,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let MoveVariationUpItemComponent = class MoveVariationUpItemComponent {
    constructor(dropdownMenuItemData, targetGroupTabComponent) {
        this.targetGroupTabComponent = targetGroupTabComponent;
        this.isFirst = false;
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }
    ngOnInit() {
        this.isFirst = this.selectedItem.variation.rank === 0;
    }
    moveUp(event) {
        if (this.isFirst) {
            event.stopPropagation(); // do not close the dropdown when disabled button is clicked
            return;
        }
        this.targetGroupTabComponent.setVariationRank(this.selectedItem.variation, -1, event, this.isFirst);
    }
};
MoveVariationUpItemComponent = __decorate([
    core.Component({
        selector: 'perso-set-variation-rank-up-item',
        template: `
        <a
            (click)="moveUp($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isFirst }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.moveup"
        ></a>
    `,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __param(1, core.Inject(core.forwardRef(() => TargetGroupTabComponent))),
    __metadata("design:paramtypes", [Object, TargetGroupTabComponent])
], MoveVariationUpItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "MoveVariationDownItemComponent", {
    selector: 'perso-set-variation-rank-down-item',
    template: `
        <a
            (click)="moveDown($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isLast }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.movedown"
        ></a>
    `,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let MoveVariationDownItemComponent = class MoveVariationDownItemComponent {
    constructor(dropdownMenuItemData, targetGroupTabComponent) {
        this.targetGroupTabComponent = targetGroupTabComponent;
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
        this.isLast = false;
    }
    ngOnInit() {
        this.isLast =
            this.selectedItem.variation.rank ===
                this.targetGroupTabComponent.visibleVariations.length - 1;
    }
    moveDown(event) {
        if (this.isLast) {
            event.stopPropagation(); // do not close the dropdown when disabled button is clicked
            return;
        }
        this.targetGroupTabComponent.setVariationRank(this.selectedItem.variation, 1, event, this.isLast);
    }
};
MoveVariationDownItemComponent = __decorate([
    core.Component({
        selector: 'perso-set-variation-rank-down-item',
        template: `
        <a
            (click)="moveDown($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isLast }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.movedown"
        ></a>
    `,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.DROPDOWN_MENU_ITEM_DATA)),
    __param(1, core.Inject(core.forwardRef(() => TargetGroupTabComponent))),
    __metadata("design:paramtypes", [Object, TargetGroupTabComponent])
], MoveVariationDownItemComponent);

window.__smartedit__.addDecoratorPayload("Component", "TargetGroupVariationListComponent", {
    selector: 'perso-target-group-variation-list',
    template: `<ul class="pe-customization-modal__list-group"><li class="pe-customization-modal__list-group__item" *ngFor="let variation of variationsForView"><div class="pe-customization-modal__list-group__item-col1"><a class="pe-customization-modal__list-group__item-link perso-wrap-ellipsis" (click)="editVariation(variation)">{{ variation.name }}</a><span [ngClass]="variation.activityState">{{ variation.enablementText }}</span><div *ngIf="variation.isDefault"><span class="pe-customization-modal__title-label-segments" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.colon"></span> <span translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.default"></span></div><div *ngIf="!variation.isDefault"><div><span class="pe-customization-modal__title-label-segments" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.colon"></span><segment-expression-as-html [segmentExpression]="variation.triggers"></segment-expression-as-html></div></div></div><div><se-dropdown-menu [dropdownItems]="dropdownItems" [selectedItem]="{ variation: variation }" class="pull-right"></se-dropdown-menu></div></li></ul>`
});
let TargetGroupVariationListComponent = class TargetGroupVariationListComponent {
    constructor(customizationViewService, triggerService, persoUtils) {
        this.customizationViewService = customizationViewService;
        this.triggerService = triggerService;
        this.persoUtils = persoUtils;
        this.dropdownItems = [
            { component: EditVariationItemComponent },
            { component: ToggleVariationActiveItemComponent },
            { component: MoveVariationUpItemComponent },
            { component: MoveVariationDownItemComponent },
            { component: RemoveVariationItemComponent }
        ];
        this.variationsForView = [];
    }
    ngOnChanges(changes) {
        const variationsChange = changes.variations;
        if (variationsChange) {
            const variations = variationsChange.currentValue;
            this.variationsForView = variations.map((variation) => {
                const enablementText = this.getEnablementTextForVariation(variation);
                const activityState = this.getActivityStateForVariation(this.customization, variation);
                const isDefault = this.isDefaultTrigger(variation.triggers);
                return Object.assign(Object.assign({}, variation), { enablementText,
                    activityState,
                    isDefault });
            });
        }
    }
    editVariation(variation) {
        this.customizationViewService.editVariationAction(variation);
    }
    isDefaultTrigger(triggers) {
        return this.triggerService.isDefault(triggers);
    }
    getActivityStateForVariation(customization, variation) {
        return this.persoUtils.getActivityStateForVariation(customization, variation);
    }
    getEnablementTextForVariation(variation) {
        const enablementText = this.persoUtils.getEnablementTextForVariation(variation, 'personalization.modal.customizationvariationmanagement.targetgrouptab');
        return `(${enablementText})`;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], TargetGroupVariationListComponent.prototype, "variations", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], TargetGroupVariationListComponent.prototype, "customization", void 0);
TargetGroupVariationListComponent = __decorate([
    core.Component({
        selector: 'perso-target-group-variation-list',
        template: `<ul class="pe-customization-modal__list-group"><li class="pe-customization-modal__list-group__item" *ngFor="let variation of variationsForView"><div class="pe-customization-modal__list-group__item-col1"><a class="pe-customization-modal__list-group__item-link perso-wrap-ellipsis" (click)="editVariation(variation)">{{ variation.name }}</a><span [ngClass]="variation.activityState">{{ variation.enablementText }}</span><div *ngIf="variation.isDefault"><span class="pe-customization-modal__title-label-segments" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.colon"></span> <span translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.default"></span></div><div *ngIf="!variation.isDefault"><div><span class="pe-customization-modal__title-label-segments" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.colon"></span><segment-expression-as-html [segmentExpression]="variation.triggers"></segment-expression-as-html></div></div></div><div><se-dropdown-menu [dropdownItems]="dropdownItems" [selectedItem]="{ variation: variation }" class="pull-right"></se-dropdown-menu></div></li></ul>`
    }),
    __metadata("design:paramtypes", [exports.CustomizationViewService,
        exports.TriggerService,
        personalizationcommons.PersonalizationsmarteditUtils])
], TargetGroupVariationListComponent);

window.__smartedit__.addDecoratorPayload("Component", "ManageCustomizationViewComponent", {
    selector: 'perso-manage-customization-view',
    template: `<div class="pe-customization-modal__tabs"><se-tabs *ngIf="isReady" [tabsList]="tabs" [numTabsDisplayed]="2" (onTabSelected)="onTabSelected($event)"></se-tabs></div>`,
    providers: [exports.CustomizationViewService]
});
let ManageCustomizationViewComponent = class ManageCustomizationViewComponent {
    constructor(persoDateUtils, persoCommerceCustomizationService, persoMessageHandler, persoCustomizationViewService, persoRestService, confirmationModalService, systemEventService, modalManager, translateService, logService, cdr, renderer, document) {
        this.persoDateUtils = persoDateUtils;
        this.persoCommerceCustomizationService = persoCommerceCustomizationService;
        this.persoMessageHandler = persoMessageHandler;
        this.persoCustomizationViewService = persoCustomizationViewService;
        this.persoRestService = persoRestService;
        this.confirmationModalService = confirmationModalService;
        this.systemEventService = systemEventService;
        this.modalManager = modalManager;
        this.translateService = translateService;
        this.logService = logService;
        this.cdr = cdr;
        this.renderer = renderer;
        this.document = document;
        this.isReady = false;
        this.savingCustomizationView = false;
        this.tabs = [];
        this.isReady = false;
        this.isEditMode = false;
        this.tabs = [
            {
                id: personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_FORM_NAME,
                title: 'personalization.modal.customizationvariationmanagement.basicinformationtab',
                component: exports.BasicInfoTabComponent,
                hasErrors: false,
                active: true
            },
            {
                id: personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME,
                title: 'personalization.modal.customizationvariationmanagement.targetgrouptab',
                component: TargetGroupTabComponent,
                hasErrors: false,
                active: false
            }
        ];
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            const modalData = yield this.modalManager.getModalData().pipe(operators.take(1)).toPromise();
            if (modalData) {
                ({ customizationCode: this.customizationCode, variationCode: this.variationCode } =
                    modalData);
            }
            this.initModalButtons();
            yield this.initCustomization();
            if (this.customizationCode) {
                this.isEditMode = true;
                if (this.variationCode) {
                    this.persoCustomizationViewService.editVariationAction(this.persoCustomizationViewService.selectVariationByCode(this.variationCode));
                }
            }
            if (this.variationCode) {
                this.tabs[0].active = false;
                this.tabs[1].active = true;
                this.modalManager.addButtons(this.modalButtons.targetGroupTabComponent);
            }
            else {
                this.modalManager.addButtons(this.modalButtons.basicInfoTabComponent);
            }
            this.isReady = true;
            this.cdr.detectChanges();
        });
    }
    onTabSelected(tabId) {
        this.modalManager.removeAllButtons();
        if (tabId === personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_FORM_NAME) {
            this.modalManager.addButtons([...this.modalButtons.basicInfoTabComponent]);
        }
        else if (tabId === personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME) {
            this.modalManager.addButtons([...this.modalButtons.targetGroupTabComponent]);
        }
    }
    initModalButtons() {
        const modalButtonCancel = {
            id: 'cancel',
            label: 'se.cms.component.confirmation.modal.cancel',
            style: smarteditcommons.ModalButtonStyle.Default,
            action: smarteditcommons.ModalButtonAction.None,
            callback: () => rxjs.from(this.onCancel())
        };
        this.modalButtons = {
            basicInfoTabComponent: [
                {
                    id: personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT,
                    label: 'personalization.modal.customizationvariationmanagement.basicinformationtab.button.next',
                    style: smarteditcommons.ModalButtonStyle.Primary,
                    action: smarteditcommons.ModalButtonAction.None,
                    callback: () => rxjs.from(this.selectTargetGroupTab()),
                    disabledFn: () => {
                        const isBasicInfoTabValid = this.isBasicInfoTabValid();
                        this.fixDisabledTabStyles(isBasicInfoTabValid);
                        return !isBasicInfoTabValid;
                    }
                },
                modalButtonCancel
            ],
            targetGroupTabComponent: [
                {
                    id: personalizationcommons.CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK,
                    label: 'personalization.modal.customizationvariationmanagement.targetgrouptab.button.submit',
                    style: smarteditcommons.ModalButtonStyle.Primary,
                    action: smarteditcommons.ModalButtonAction.None,
                    callback: () => rxjs.from(this.save()),
                    disabledFn: () => {
                        const visibleVariations = this.persoCustomizationViewService.selectVisibleVariations();
                        return visibleVariations.length === 0 || this.savingCustomizationView;
                    }
                },
                modalButtonCancel
            ]
        };
    }
    selectTargetGroupTab() {
        return new Promise(() => {
            this.tabsCmp.selectTab(this.tabs[1]);
        });
    }
    fixDisabledTabStyles(isTabValid) {
        const tab = this.document.querySelector(`[tab-id="targetgrptab"]`);
        if (!tab) {
            return;
        }
        if (isTabValid) {
            this.renderer.setStyle(tab, 'pointer-events', 'unset');
            this.renderer.setStyle(tab, 'opacity', 'unset');
        }
        else {
            this.renderer.setStyle(tab, 'pointer-events', 'none');
            this.renderer.setStyle(tab, 'opacity', 0.4);
        }
    }
    isBasicInfoTabValid() {
        const { name } = this.persoCustomizationViewService.selectCustomization();
        return !!name;
    }
    initCustomization() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.customizationCode) {
                this.initialCustomization = lodash.cloneDeep(defaultCustomization);
                return;
            }
            const customization = yield this.getCustomization();
            if (customization) {
                this.persoCustomizationViewService.setCustomization(customization);
                this.initialCustomization = lodash.cloneDeep(customization);
            }
        });
    }
    fetchCustomizationAndSetDates() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const customization = yield this.persoRestService.getCustomization({
                    code: this.customizationCode
                });
                customization.enabledStartDate = this.persoDateUtils.formatDate(customization.enabledStartDate, undefined);
                customization.enabledEndDate = this.persoDateUtils.formatDate(customization.enabledEndDate, undefined);
                customization.statusBoolean =
                    customization.status === personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
                return customization;
            }
            catch (_a) {
                this.persoMessageHandler.sendError(this.translateService.instant('personalization.error.gettingsegments'));
            }
        });
    }
    getCustomization() {
        return __awaiter(this, void 0, void 0, function* () {
            const customization = yield this.fetchCustomizationAndSetDates();
            if (!customization) {
                return;
            }
            const variations = yield this.fetchVariations(this.customizationCode);
            if (!variations) {
                return;
            }
            customization.variations = variations;
            return customization;
        });
    }
    fetchVariations(customizationCode) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const filter = {
                    includeFullFields: true
                };
                const response = yield this.persoRestService.getVariationsForCustomization(customizationCode, filter);
                response.variations.forEach((variation) => {
                    variation.commerceCustomizations =
                        this.persoCommerceCustomizationService.getCommerceActionsCountMap(variation);
                    variation.numberOfCommerceActions =
                        this.persoCommerceCustomizationService.getCommerceActionsCount(variation);
                    delete variation.actions;
                });
                return response.variations;
            }
            catch (_a) {
                this.persoMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponents'));
            }
        });
    }
    onCancel() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.isModalDirty()) {
                this.modalManager.dismiss();
                return;
            }
            try {
                yield this.confirmationModalService.confirm({
                    description: 'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelconfirmation'
                });
                this.modalManager.dismiss();
            }
            catch (_a) {
                this.logService.log('Confirmation Modal cancelled');
            }
        });
    }
    isModalDirty() {
        const customization = this.persoCustomizationViewService.selectCustomization();
        return !lodash.isEqual(this.initialCustomization, customization);
    }
    save() {
        return __awaiter(this, void 0, void 0, function* () {
            this.savingCustomizationView = true;
            const customization = Object.assign({}, this.persoCustomizationViewService.selectCustomization());
            const payload = this.prepareCustomizationPayload(customization, this.isEditMode);
            if (this.isEditMode) {
                try {
                    yield this.persoRestService.updateCustomizationPackage(payload);
                    this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
                    this.persoMessageHandler.sendSuccess(this.translateService.instant('personalization.info.updatingcustomization'));
                    this.modalManager.dismiss();
                }
                catch (_a) {
                    this.savingCustomizationView = false;
                    this.persoMessageHandler.sendError(this.translateService.instant('personalization.error.updatingcustomization'));
                }
            }
            else {
                try {
                    yield this.persoRestService.createCustomization(payload);
                    this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
                    this.persoMessageHandler.sendSuccess(this.translateService.instant('personalization.info.creatingcustomization'));
                    this.modalManager.dismiss();
                }
                catch (_b) {
                    this.savingCustomizationView = false;
                    this.persoMessageHandler.sendError(this.translateService.instant('personalization.error.creatingcustomization'));
                }
            }
        });
    }
    prepareCustomizationPayload(customization, isEditMode) {
        customization.enabledStartDate = customization.enabledStartDate
            ? this.persoDateUtils.formatDate(customization.enabledStartDate, personalizationcommons.PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT)
            : undefined;
        customization.enabledEndDate = customization.enabledEndDate
            ? this.persoDateUtils.formatDate(customization.enabledEndDate, personalizationcommons.PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT)
            : undefined;
        if (!isEditMode) {
            delete customization.catalog;
            delete customization.code;
            delete customization.rank;
        }
        return customization;
    }
};
__decorate([
    core.ViewChild(smarteditcommons.TabsComponent, { static: false }),
    __metadata("design:type", smarteditcommons.TabsComponent)
], ManageCustomizationViewComponent.prototype, "tabsCmp", void 0);
ManageCustomizationViewComponent = __decorate([
    core.Component({
        selector: 'perso-manage-customization-view',
        template: `<div class="pe-customization-modal__tabs"><se-tabs *ngIf="isReady" [tabsList]="tabs" [numTabsDisplayed]="2" (onTabSelected)="onTabSelected($event)"></se-tabs></div>`,
        providers: [exports.CustomizationViewService]
    }),
    __param(12, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditDateUtils,
        personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        exports.CustomizationViewService,
        exports.PersonalizationsmarteditRestService,
        smarteditcommons.IConfirmationModalService,
        smarteditcommons.SystemEventService,
        smarteditcommons.ModalManagerService,
        core$1.TranslateService,
        smarteditcommons.LogService,
        core.ChangeDetectorRef,
        core.Renderer2,
        Document])
], ManageCustomizationViewComponent);

/* @ngInject */ exports.ManageCustomizationViewManager = class /* @ngInject */ ManageCustomizationViewManager {
    constructor(modalService) {
        this.modalService = modalService;
        this.className = 'sliderPanelParentModal';
        this.modalTitle = 'personalization.modal.customizationvariationmanagement.title';
    }
    openCreateCustomizationModal() {
        this.modalService.open({
            component: ManageCustomizationViewComponent,
            config: {
                dialogPanelClass: `lg ${this.className}`,
                width: '650px',
                backdropClickCloseable: false,
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            templateConfig: {
                title: this.modalTitle,
                isDismissButtonVisible: true
            }
        });
    }
    openEditCustomizationModal(customizationCode, variationCode) {
        this.modalService.open({
            component: ManageCustomizationViewComponent,
            data: {
                customizationCode,
                variationCode
            },
            config: {
                dialogPanelClass: `lg ${this.className}`,
                width: '650px',
                backdropClickCloseable: false,
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            templateConfig: {
                title: this.modalTitle,
                isDismissButtonVisible: true
            }
        });
    }
};
/* @ngInject */ exports.ManageCustomizationViewManager = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IModalService])
], /* @ngInject */ exports.ManageCustomizationViewManager);

window.__smartedit__.addDecoratorPayload("Component", "SegmentExpressionAsHtmlComponent", {
    selector: 'segment-expression-as-html',
    template: `<span *ngFor="let word of expression"><span *ngIf="operators.includes(word)" class="pe-customization-modal__expression-text">&nbsp;{{ segmentActionI18n[word] | translate }}&nbsp;</span><se-tooltip *ngIf="word === emptyGroup" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger class="pe-customization-modal__expression-alert-icon sap-icon--alert"></span> <span se-tooltip-body translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.group.tooltip"></span></se-tooltip><span *ngIf="!emptyGroupAndOperators.includes(word)">{{ word }}</span></span>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let SegmentExpressionAsHtmlComponent = class SegmentExpressionAsHtmlComponent {
    constructor(triggerService) {
        this.triggerService = triggerService;
        this.expression = [];
        this.segmentActionI18n = {
            [personalizationcommons.TriggerActionId.AND]: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and',
            [personalizationcommons.TriggerActionId.OR]: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or',
            [personalizationcommons.TriggerActionId.NOT]: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
        };
        this.operators = ['AND', 'OR', 'NOT'];
        this.emptyGroup = '[]';
        this.emptyGroupAndOperators = this.operators.concat(this.emptyGroup);
    }
    ngOnInit() {
        if (!this.segmentExpression) {
            return;
        }
        this.update();
    }
    ngOnChanges() {
        this.update();
    }
    update() {
        this.expression = this.isSegmentExpression(this.segmentExpression)
            ? this.buildExpression(this.segmentExpression)
            : this.mapExpressionToString(this.segmentExpression);
    }
    buildExpression(segmentExpression) {
        const expression = this.triggerService.buildData(segmentExpression)[0];
        return this.mapExpressionToString(expression);
    }
    mapExpressionToString(expression) {
        return this.triggerService.getExpressionAsString(expression).split(' ');
    }
    isSegmentExpression(expression) {
        return typeof expression.operation === 'undefined';
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SegmentExpressionAsHtmlComponent.prototype, "segmentExpression", void 0);
SegmentExpressionAsHtmlComponent = __decorate([
    core.Component({
        selector: 'segment-expression-as-html',
        template: `<span *ngFor="let word of expression"><span *ngIf="operators.includes(word)" class="pe-customization-modal__expression-text">&nbsp;{{ segmentActionI18n[word] | translate }}&nbsp;</span><se-tooltip *ngIf="word === emptyGroup" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger class="pe-customization-modal__expression-alert-icon sap-icon--alert"></span> <span se-tooltip-body translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.group.tooltip"></span></se-tooltip><span *ngIf="!emptyGroupAndOperators.includes(word)">{{ word }}</span></span>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [exports.TriggerService])
], SegmentExpressionAsHtmlComponent);

let SegmentExpressionAsHtmlModule = class SegmentExpressionAsHtmlModule {
};
SegmentExpressionAsHtmlModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, smarteditcommons.TooltipModule, smarteditcommons.TranslationModule.forChild()],
        declarations: [SegmentExpressionAsHtmlComponent],
        exports: [SegmentExpressionAsHtmlComponent]
    })
], SegmentExpressionAsHtmlModule);

let /* @ngInject */ ActionsDataFactory = class /* @ngInject */ ActionsDataFactory {
    constructor() {
        this.actions = [];
        this.removedActions = [];
    }
    getActions() {
        return this.actions;
    }
    getRemovedActions() {
        return this.removedActions;
    }
    resetActions() {
        this.actions.length = 0;
    }
    resetRemovedActions() {
        this.removedActions.length = 0;
    }
    addAction(action, comparer) {
        let exist = false;
        this.actions.forEach((wrapper) => {
            exist = exist || comparer(action, wrapper.action);
        });
        if (!exist) {
            let status = personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW;
            let removedIndex = -1;
            this.removedActions.forEach((wrapper, index) => {
                if (comparer(action, wrapper.action)) {
                    removedIndex = index;
                }
            });
            if (removedIndex >= 0) {
                // we found or action in delete queue
                status = personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD;
                this.removedActions.splice(removedIndex, 1);
            }
            this.actions.push({
                action,
                status
            });
        }
    }
    isItemInSelectedActions(action, comparer) {
        return this.actions.find((wrapper) => comparer(action, wrapper.action));
    }
};
/* @ngInject */ ActionsDataFactory = __decorate([
    core.Injectable()
], /* @ngInject */ ActionsDataFactory);

window.__smartedit__.addDecoratorPayload("Component", "CommerceCustomizationViewComponent", {
    selector: 'commerce-customization-view',
    template: `<div id="commerceCustomizationBody-002" class="perso-cc-modal"><div class="perso-cc-modal__title-layout"><div class="perso-wrap-ellipsis perso-cc-modal__title-cname" [textContent]="customization.name" title="{{customization.name}}"></div><div class="perso-cc-modal__title-status" [ngClass]="customizationStatus" [textContent]="' (' + customizationStatusText + ') '"></div><span>></span><div class="perso-wrap-ellipsis perso-cc-modal__title-vname" [textContent]="variation.name" title="{{variation.name}}"></div><div class="perso-cc-modal__title-status" [ngClass]="variationStatus" [textContent]="' (' + variationStatusText + ')'"></div></div><div class="form-group perso-cc-modal__content-layout"><label for="commerce-customization-type-1" class="fd-form-label" translate="personalization.modal.commercecustomization.action.type"></label><se-select [id]="commerce-customization-type-1" class="fd-form__control" [fetchStrategy]="customizationTypeFetchStrategy" [searchEnabled]="false" [(model)]="select.typeId" [onChange]="onActionTypeChange"></se-select></div><ng-container><ng-container *ngComponentOutlet="promotionComponent.component; injector: promotionComponent.injector"></ng-container></ng-container><ng-container><ng-container *ngComponentOutlet="searchProfileComponent.component; injector: searchProfileComponent.injector"></ng-container></ng-container><div class="select2-choices"><div class="ui-select-match-item select2-search-choice" *ngFor="let action of getActionsToDisplay()"><span [textContent]="displayAction(action)"></span> <span class="ui-select-match-close select2-search-choice-close sap-icon--decline" (click)="removeSelectedAction(action)"></span></div></div></div>`,
    encapsulation: core.ViewEncapsulation.None
});
exports.CommerceCustomizationViewComponent = class CommerceCustomizationViewComponent {
    constructor(injector, personlizationSearchExtensionComponent, personlizationPromotionExtensionComponent, translateService, actionsDataFactory, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, systemEventService, personalizationsmarteditCommerceCustomizationService, personalizationsmarteditContextService, personalizationsmarteditUtils, modalManager, logService) {
        this.injector = injector;
        this.translateService = translateService;
        this.actionsDataFactory = actionsDataFactory;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.systemEventService = systemEventService;
        this.personalizationsmarteditCommerceCustomizationService = personalizationsmarteditCommerceCustomizationService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.modalManager = modalManager;
        this.logService = logService;
        this.availableTypes = [];
        this.select = {};
        this.customizationTypeFetchStrategy = {
            fetchAll: () => {
                this.availableTypes = [];
                this.availableTypes =
                    this.personalizationsmarteditCommerceCustomizationService.getAvailableTypes(this.personalizationsmarteditContextService.getSeData().seConfigurationData);
                this.availableTypes.forEach((type) => {
                    type.id = type.type;
                    type.label = type.text;
                });
                return Promise.resolve(this.availableTypes);
            }
        };
        this.isItemInSelectedActions = (action, comparer) => this.actionsDataFactory.isItemInSelectedActions(action, comparer);
        this.addAction = (action, comparer) => {
            this.actionsDataFactory.addAction(action, comparer);
        };
        this.availableTypes =
            this.personalizationsmarteditCommerceCustomizationService.getAvailableTypes(this.personalizationsmarteditContextService.getSeData().seConfigurationData);
        this.availableTypes.forEach((type) => {
            type.id = type.type;
            type.label = type.text;
        });
        this.select = {
            typeId: this.availableTypes[0].id,
            template: this.availableTypes[0].template
        };
        this.promotionCode = 'cxPromotionActionData';
        this.searchProfilesCode = 'cxSearchProfileActionData';
        this.actionsDataFactory.resetActions();
        this.actionsDataFactory.resetRemovedActions();
        this.actions = this.actionsDataFactory.getActions();
        this.removedActions = this.actionsDataFactory.getRemovedActions();
        this.onActionTypeChange = this.onActionTypeChange.bind(this);
        if (personlizationSearchExtensionComponent) {
            this.initPersonlizationSearchExtensionComponent(personlizationSearchExtensionComponent);
        }
        if (personlizationPromotionExtensionComponent) {
            this.initPersonlizationPromotionExtensionComponent(personlizationPromotionExtensionComponent);
        }
    }
    ngOnInit() {
        this.modalManager
            .getModalData()
            .pipe(operators.take(1))
            .subscribe((data) => {
            this.customization = data.customization;
            this.variation = data.variation;
        });
        this.customizationStatusText =
            this.personalizationsmarteditUtils.getEnablementTextForCustomization(this.customization, 'personalization.modal.commercecustomization');
        this.variationStatusText = this.personalizationsmarteditUtils.getEnablementTextForVariation(this.variation, 'personalization.modal.commercecustomization');
        this.customizationStatus =
            this.personalizationsmarteditUtils.getActivityStateForCustomization(this.customization);
        this.variationStatus = this.personalizationsmarteditUtils.getActivityStateForVariation(this.customization, this.variation);
        this.modalManager.addButtons([
            {
                id: 'confirmOk',
                label: 'personalization.modal.commercecustomization.button.submit',
                style: smarteditcommons.ModalButtonStyle.Primary,
                action: smarteditcommons.ModalButtonAction.Close,
                callback: () => rxjs.from(this.onSave()),
                disabledFn: () => !this.isDirty()
            },
            {
                id: 'confirmCancel',
                label: 'personalization.modal.commercecustomization.button.cancel',
                style: smarteditcommons.ModalButtonStyle.Default,
                action: smarteditcommons.ModalButtonAction.Close,
                callback: () => rxjs.from(this.onCancel())
            }
        ]);
        this.populateActions();
    }
    onActionTypeChange() {
        this.availableTypes.forEach((type) => {
            if (type.id === this.select.typeId) {
                this.select.template = type.template;
            }
        });
    }
    getSeData() {
        return this.personalizationsmarteditContextService.getSeData();
    }
    removeSelectedAction(actionWrapper) {
        const index = this.actions.indexOf(actionWrapper);
        if (index < 0) {
            return;
        }
        const removed = this.actions.splice(index, 1);
        if (removed[0].status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD ||
            removed[0].status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE) {
            removed[0].status = personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE;
            this.removedActions.push(removed[0]);
        }
    }
    displayAction(actionWrapper) {
        const action = actionWrapper.action;
        const type = this.getType(action.type);
        if (type.getName) {
            return type.getName(action);
        }
        else {
            return action.code;
        }
    }
    getActionsToDisplay() {
        return this.actionsDataFactory.getActions();
    }
    isDirty() {
        let dirty = false;
        this.actions.forEach((wrapper) => {
            dirty =
                dirty ||
                    wrapper.status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW ||
                    wrapper.status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
        });
        dirty = dirty || this.removedActions.length > 0;
        return dirty;
    }
    getSelectedTypeCode() {
        return this.select.typeId;
    }
    getCustomization() {
        return this.customization;
    }
    getVariation() {
        return this.variation;
    }
    initPersonlizationSearchExtensionComponent({ component }) {
        this.searchProfileComponent = {
            component,
            injector: core.Injector.create({
                parent: this.injector,
                providers: [
                    {
                        provide: personalizationcommons.PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN,
                        useValue: {
                            seData: this.getSeData(),
                            actions: this.actions,
                            addAction: (action, comparer) => this.addAction(action, comparer),
                            isActionInSelectDisabled: (action, comparer) => this.isItemInSelectedActions(action, comparer),
                            getSelectedTypeCode: () => this.getSelectedTypeCode(),
                            getCustomization: () => this.getCustomization(),
                            getVariation: () => this.getVariation()
                        }
                    }
                ]
            })
        };
    }
    initPersonlizationPromotionExtensionComponent({ component }) {
        this.promotionComponent = {
            component,
            injector: core.Injector.create({
                parent: this.injector,
                providers: [
                    {
                        provide: personalizationcommons.PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN,
                        useValue: {
                            addAction: (action, comparer) => this.addAction(action, comparer),
                            isActionInSelectDisabled: (action, comparer) => this.isItemInSelectedActions(action, comparer),
                            getSelectedTypeCode: () => this.getSelectedTypeCode()
                        }
                    }
                ]
            })
        };
    }
    populateActions() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.personalizationsmarteditRestService.getActions(this.customization.code, this.variation.code, {});
                const actions = response.actions
                    .filter((elem) => elem.type !== 'cxCmsActionData')
                    .map((item) => ({
                    code: item.code,
                    action: item,
                    status: personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD
                }));
                this.actionsDataFactory.resetActions();
                this.personalizationsmarteditUtils.uniqueArray(this.actionsDataFactory.actions, actions || []);
            }
            catch (e) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingactions'));
            }
        });
    }
    getType(type) {
        for (const item of this.availableTypes) {
            if (item.type === type) {
                return item;
            }
        }
        return {};
    }
    sendRefreshEvent() {
        this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
    }
    onCancel() {
        this.modalManager.close(null);
        return Promise.resolve();
    }
    onSave() {
        const createData = {
            actions: this.actions
                .filter((item) => item.status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW)
                .map((item) => item.action)
        };
        const deleteData = this.removedActions
            .filter((item) => item.status === personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE)
            .map((item) => item.action.code);
        const updateData = {
            actions: this.actions
                .filter((item) => item.status ===
                personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE)
                .map((item) => item.action)
        };
        const shouldCreate = createData.actions.length > 0;
        const shouldDelete = deleteData.length > 0;
        const shouldUpdate = updateData.actions.length > 0;
        (() => {
            if (shouldCreate) {
                return this.createActions(this.customization.code, this.variation.code, createData);
            }
            else {
                return Promise.resolve();
            }
        })().then((respCreate) => {
            (() => {
                if (shouldDelete) {
                    return this.deleteActions(this.customization.code, this.variation.code, deleteData);
                }
                else {
                    return Promise.resolve();
                }
            })().then((respDelete) => {
                if (shouldUpdate) {
                    this.updateActions(this.customization.code, this.variation.code, updateData, respCreate, respDelete);
                }
                else {
                    return Promise.resolve();
                }
            });
        });
        this.modalManager.close(null);
        return Promise.resolve();
    }
    getActionTypesForActions(actions) {
        return actions
            .map((action) => action.type)
            .filter((item, index, arr) => arr.indexOf(item) === index)
            .map((typeCode) => this.availableTypes.filter((availableType) => availableType.type === typeCode)[0]);
    }
    createActions(customizationCode, variationCode, createData) {
        return __awaiter(this, void 0, void 0, function* () {
            const deferred = smarteditcommons.promiseUtils.defer();
            try {
                const response = yield this.personalizationsmarteditRestService.createActions(customizationCode, variationCode, createData, {});
                this.personalizationsmarteditMessageHandler.sendSuccess(this.translateService.instant('personalization.info.creatingaction'));
                this.sendRefreshEvent();
                deferred.resolve(response);
            }
            catch (e) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.creatingaction'));
                deferred.reject();
            }
            return deferred.promise;
        });
    }
    deleteActions(customizationCode, variationCode, deleteData) {
        return __awaiter(this, void 0, void 0, function* () {
            const deferred = smarteditcommons.promiseUtils.defer();
            try {
                const response = yield this.personalizationsmarteditRestService.deleteActions(customizationCode, variationCode, deleteData, {});
                this.personalizationsmarteditMessageHandler.sendSuccess(this.translateService.instant('personalization.info.removingaction'));
                this.sendRefreshEvent();
                deferred.resolve(response);
            }
            catch (e) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.removingaction'));
                deferred.resolve();
            }
            return deferred.promise;
        });
    }
    updateActions(customizationCode, variationCode, updateData, respCreate, respDelete) {
        const updateTypes = this.getActionTypesForActions(updateData.actions);
        updateTypes.forEach((type) => {
            if (type.updateActions) {
                const actionsForType = updateData.actions.filter((a) => this.getType(a.type) === type);
                type.updateActions(customizationCode, variationCode, actionsForType, respCreate, respDelete).then(() => {
                    this.personalizationsmarteditMessageHandler.sendSuccess(this.translateService.instant('personalization.info.updatingactions'));
                    this.sendRefreshEvent();
                }, () => {
                    this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.updatingactions'));
                });
            }
            else {
                this.logService.debug(this.translateService.instant('personalization.error.noupdatingactions'));
            }
        });
    }
};
exports.CommerceCustomizationViewComponent = __decorate([
    core.Component({
        selector: 'commerce-customization-view',
        template: `<div id="commerceCustomizationBody-002" class="perso-cc-modal"><div class="perso-cc-modal__title-layout"><div class="perso-wrap-ellipsis perso-cc-modal__title-cname" [textContent]="customization.name" title="{{customization.name}}"></div><div class="perso-cc-modal__title-status" [ngClass]="customizationStatus" [textContent]="' (' + customizationStatusText + ') '"></div><span>></span><div class="perso-wrap-ellipsis perso-cc-modal__title-vname" [textContent]="variation.name" title="{{variation.name}}"></div><div class="perso-cc-modal__title-status" [ngClass]="variationStatus" [textContent]="' (' + variationStatusText + ')'"></div></div><div class="form-group perso-cc-modal__content-layout"><label for="commerce-customization-type-1" class="fd-form-label" translate="personalization.modal.commercecustomization.action.type"></label><se-select [id]="commerce-customization-type-1" class="fd-form__control" [fetchStrategy]="customizationTypeFetchStrategy" [searchEnabled]="false" [(model)]="select.typeId" [onChange]="onActionTypeChange"></se-select></div><ng-container><ng-container *ngComponentOutlet="promotionComponent.component; injector: promotionComponent.injector"></ng-container></ng-container><ng-container><ng-container *ngComponentOutlet="searchProfileComponent.component; injector: searchProfileComponent.injector"></ng-container></ng-container><div class="select2-choices"><div class="ui-select-match-item select2-search-choice" *ngFor="let action of getActionsToDisplay()"><span [textContent]="displayAction(action)"></span> <span class="ui-select-match-close select2-search-choice-close sap-icon--decline" (click)="removeSelectedAction(action)"></span></div></div></div>`,
        encapsulation: core.ViewEncapsulation.None
    }),
    __param(1, core.Optional()),
    __param(1, core.Inject(personalizationcommons.PERSONLIZATION_SEARCH_EXTENSION_TOKEN)),
    __param(2, core.Inject(personalizationcommons.PERSONLIZATION_PROMOTION_EXTENSION_TOKEN)),
    __metadata("design:paramtypes", [core.Injector, Object, Object, core$1.TranslateService,
        ActionsDataFactory,
        exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        smarteditcommons.SystemEventService,
        personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditUtils,
        smarteditcommons.ModalManagerService,
        smarteditcommons.LogService])
], exports.CommerceCustomizationViewComponent);

/* @ngInject */ exports.PersonalizationsmarteditCommerceCustomizationView = class /* @ngInject */ PersonalizationsmarteditCommerceCustomizationView {
    constructor(modalService) {
        this.modalService = modalService;
        this.openCommerceCustomizationAction = (customization, variation) => {
            this.modalService.open({
                component: exports.CommerceCustomizationViewComponent,
                data: {
                    customization,
                    variation
                },
                templateConfig: {
                    title: 'personalization.modal.commercecustomization.title',
                    isDismissButtonVisible: true
                },
                config: {
                    width: '700px',
                    focusTrapped: false,
                    backdropClickCloseable: false
                }
            });
        };
    }
};
/* @ngInject */ exports.PersonalizationsmarteditCommerceCustomizationView = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IModalService])
], /* @ngInject */ exports.PersonalizationsmarteditCommerceCustomizationView);

let PersonalizationsmarteditCommerceCustomizationModule = class PersonalizationsmarteditCommerceCustomizationModule {
};
PersonalizationsmarteditCommerceCustomizationModule = __decorate([
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            common.CommonModule,
            forms.FormsModule,
            smarteditcommons.SelectModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule
        ],
        declarations: [exports.CommerceCustomizationViewComponent],
        entryComponents: [exports.CommerceCustomizationViewComponent],
        providers: [exports.PersonalizationsmarteditCommerceCustomizationView, ActionsDataFactory]
    })
], PersonalizationsmarteditCommerceCustomizationModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// Detailed explanation of this solution is here: https://stackoverflow.com/questions/67337934/angular-nested-drag-and-drop-cdk-material-cdkdroplistgroup-cdkdroplist-nested
let /* @ngInject */ SegmentDragAndDropService = class /* @ngInject */ SegmentDragAndDropService {
    constructor(document) {
        this.document = document;
    }
    dragMoved(event) {
        const elementFromPoint = this.document.elementFromPoint(event.pointerPosition.x, event.pointerPosition.y);
        if (!elementFromPoint) {
            this.currentHoverDropListId = null;
            return;
        }
        const dropList = elementFromPoint.classList.contains('cdk-drop-list')
            ? elementFromPoint
            : elementFromPoint.closest('.cdk-drop-list');
        if (!dropList) {
            this.currentHoverDropListId = null;
            return;
        }
        this.currentHoverDropListId = dropList.id;
    }
    dragReleased() {
        this.currentHoverDropListId = null;
    }
};
/* @ngInject */ SegmentDragAndDropService = __decorate([
    core.Injectable(),
    __param(0, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Document])
], /* @ngInject */ SegmentDragAndDropService);

window.__smartedit__.addDecoratorPayload("Component", "SegmentItemPrinterComponent", {
    selector: 'segment-item-printer',
    changeDetection: core.ChangeDetectionStrategy.OnPush,
    template: `
        <div>
            <div class="perso__item-name">{{ data.item.code }}</div>
            <div
                class="perso-wrap-ellipsis perso__wrapper-description"
                [attr.title]="data.item.description"
            >
                {{ data.item.description }}
            </div>
        </div>
    `
});
/**
 * Component represents the default Item of Select Component.
 * Displayed by Item Printer Component.
 */
let SegmentItemPrinterComponent = class SegmentItemPrinterComponent {
    constructor(data) {
        this.data = data;
    }
};
SegmentItemPrinterComponent = __decorate([
    core.Component({
        selector: 'segment-item-printer',
        changeDetection: core.ChangeDetectionStrategy.OnPush,
        template: `
        <div>
            <div class="perso__item-name">{{ data.item.code }}</div>
            <div
                class="perso-wrap-ellipsis perso__wrapper-description"
                [attr.title]="data.item.description"
            >
                {{ data.item.description }}
            </div>
        </div>
    `
    }),
    __param(0, core.Inject(smarteditcommons.ITEM_COMPONENT_DATA_TOKEN)),
    __metadata("design:paramtypes", [Object])
], SegmentItemPrinterComponent);

window.__smartedit__.addDecoratorPayload("Component", "SegmentNodeComponent", {
    selector: 'segment-node',
    template: `<div cdkDropList [id]="node.uid" [cdkDropListData]="node" [cdkDropListConnectedTo]="connectedDropListsIds" (cdkDropListDropped)="onDragDrop($event)" [cdkDropListSortingDisabled]="false" [cdkDropListEnterPredicate]="canBeDropped" [ngClass]="{'perso-segments-tree__layout': isContainer(node), 'perso-segments-tree__empty-container': isEmptyContainer(node), 'perso-segments-tree__collapsed-container': collapsed }"><div><div class="perso-segments-tree" [ngClass]="{'perso-segments-tree__node': isItem(node), 'perso-segments-tree__container': isContainer(node)}"><div *ngIf="isContainer(node)"><div class="perso-segments-tree__toggle" (click)="toggle()" title="{{collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse' | translate}}" [ngClass]="collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'"></div><div class="perso-segments-tree__dropdown"><label class="fd-form-label perso-segments-tree__creategroupselect__label" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.select.label"></label><se-select class="se-select-creategroup" [model]="node.operation.id" (modelChange)="operationChange($event)" [fetchStrategy]="fetchStrategy" [resetSearchInput]="false" [searchEnabled]="false"></se-select></div></div><div *ngIf="isItem(node)" class="perso-segments-tree__node-content">{{ node.selectedSegment.code }}</div><div *ngIf="isContainer(node)" class="perso-segments-tree__angular-ui-tree-handle--empty"></div><div><span class="perso-segments-tree__pull-right"><a *ngIf="isItem(node)" class="fd-link perso-segments-tree__actions perso-segments-tree__node-icon" (click)="duplicateItem(node)" title="{{'personalization.commons.icon.title.duplicate' | translate}}"><span class="sap-icon--duplicate"></span> </a><a *ngIf="!isTopContainer()" class="fd-link perso-segments-tree__actions" (click)="removeItem(node?.uid)"><div *ngIf="isContainer(node)" class="btn btn-link perso-segments-tree__container-btn-icon fd-has-margin-left-tiny" title="{{'personalization.commons.icon.title.remove' | translate}}"><span class="btn sap-icon--decline"></span></div><div *ngIf="isItem(node)" class="perso-segments-tree__node-icon" title="{{'personalization.commons.icon.title.remove' | translate}}"><span class="sap-icon--decline"></span></div></a></span><button *ngIf="isContainer(node)" class="fd-button fd-button--light perso-segments-tree__btn" (click)="newSubItem('container')"><span translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.group.button"></span></button></div></div><div *ngIf="isEmptyContainer(node) && !collapsed" class="perso-segments-tree__empty-container-node"><div class="perso-segments-tree__empty-container-node-text" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.dropzone"></div></div><ol *ngIf="!collapsed"><li cdkDrag [id]="innerNode.uid" [cdkDragData]="innerNode" (cdkDragStarted)="onDragStarted($event)" (cdkDragMoved)="onDragMoved($event)" (cdkDragReleased)="onDragReleased()" *ngFor="let innerNode of node.nodes"><segment-node [node]="innerNode" [expression]="expression" [connectedDropListsIds]="connectedDropListsIds" (expressionChange)="handleUpdate($event)" (onDrop)="onDragDrop($event)" (onDragStart)="onDragStarted($event)"></segment-node></li></ol></div></div>`,
    styles: [`ol{position:relative;margin:0;padding:0;list-style:none}.cdk-drag{list-style-type:none}.cdk-drag-preview{z-index:2000!important}.cdk-drag-placeholder{opacity:0!important}.cdk-drop-list-dragging{border-top:2px dashed rgba(0,0,0,.4);border-right:2px dashed rgba(0,0,0,.4);border-bottom:2px dashed rgba(0,0,0,.4)}.se-select-creategroup{display:inline-block;width:5rem}`]
});
let SegmentNodeComponent = class SegmentNodeComponent {
    constructor(triggerService, confirmationModalService, translateService, dragDropService) {
        this.triggerService = triggerService;
        this.confirmationModalService = confirmationModalService;
        this.translateService = translateService;
        this.dragDropService = dragDropService;
        this.canBeDropped = (drag, drop) => {
            const isNotItem = !this.isItem(this.node);
            if (this.dragDropService.currentHoverDropListId === null) {
                return isNotItem;
            }
            return drop.id === this.dragDropService.currentHoverDropListId && isNotItem;
        };
        this.connectedDropListsIds = [];
        this.expressionChange = new core.EventEmitter();
        this.onDrop = new core.EventEmitter();
        this.onDragStart = new core.EventEmitter();
        this.collapsed = false;
        this.elementToDuplicate = null;
    }
    ngOnInit() {
        this.fetchStrategy = {
            fetchAll: () => Promise.resolve(this.triggerService.actions)
        };
    }
    onDragDrop(event) {
        this.onDrop.emit(event);
    }
    onDragStarted(event) {
        event.source.element.nativeElement.style.zIndex = '2000';
        this.onDragStart.emit(event);
    }
    onDragMoved(event) {
        this.dragDropService.dragMoved(event);
    }
    onDragReleased() {
        this.dragDropService.dragReleased();
    }
    newSubItem(type) {
        this.node.nodes.unshift({
            uid: smarteditcommons.stringUtils.generateIdentifier(),
            type,
            operation: type === personalizationcommons.TriggerType.ITEM_TYPE
                ? {}
                : lodash.cloneDeep(this.triggerService.actions[0]),
            nodes: []
        });
        this.collapsed = false;
        this.expressionChange.emit(this.expression);
    }
    removeItem(uid) {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.triggerService.isNotEmptyContainer(this.node)) {
                yield this.confirmationModalService.confirm({
                    description: this.translateService.instant('personalization.modal.customizationvariationmanagement.targetgrouptab.segments.removecontainerconfirmation')
                });
                this.removeFromNode(uid);
                this.expressionChange.emit(this.expression);
            }
            else {
                this.removeFromNode(uid);
                this.expressionChange.emit(this.expression);
            }
        });
    }
    operationChange(operation) {
        this.node.operation = this.triggerService.actions.find((action) => action.id === operation);
        this.expressionChange.emit(this.expression);
    }
    toggle() {
        this.collapsed = !this.collapsed;
    }
    duplicateItem(elementToDuplicate) {
        this.elementToDuplicate = elementToDuplicate;
        this.expression[0].nodes.some(this.findElementAndDuplicate, this);
        this.expressionChange.emit(this.expression);
    }
    handleUpdate(exp) {
        this.expressionChange.emit(exp);
    }
    isContainerWithDropzone(element) {
        return this.isContainer(element) && element.nodes.length === 1;
    }
    isItem(element) {
        return this.triggerService.isItem(element);
    }
    isContainer(element) {
        return this.triggerService.isContainer(element);
    }
    isTopContainer() {
        return lodash.isEqual(this.expression[0], this.node);
    }
    isEmptyContainer(node) {
        return this.isContainer(node) && node.nodes.length === 0;
    }
    findElementAndDuplicate(element, index, array) {
        if (this.elementToDuplicate === element) {
            array.splice(index, 0, lodash.cloneDeep(Object.assign(Object.assign({}, this.elementToDuplicate), { uid: smarteditcommons.stringUtils.generateIdentifier() })));
            return true;
        }
        if (this.isContainer(element)) {
            element.nodes.some(this.findElementAndDuplicate, this);
        }
        return false;
    }
    removeFromNode(uid) {
        const target = this.findParentByUid(uid);
        target.nodes = target.nodes.filter((node) => node.uid !== uid);
    }
    findParentByUid(uid) {
        let target = null;
        const findParent = (parent) => {
            const hasChild = (parent.nodes || []).find((n) => n.uid === uid);
            if (hasChild) {
                target = parent;
                return;
            }
            for (let i = 0; i < parent.nodes.length; i++) {
                if (parent.nodes[i].uid) {
                    findParent(parent.nodes[i]);
                }
            }
        };
        findParent(this.expression[0]);
        return target;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], SegmentNodeComponent.prototype, "expression", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SegmentNodeComponent.prototype, "node", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], SegmentNodeComponent.prototype, "connectedDropListsIds", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], SegmentNodeComponent.prototype, "expressionChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], SegmentNodeComponent.prototype, "onDrop", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], SegmentNodeComponent.prototype, "onDragStart", void 0);
SegmentNodeComponent = __decorate([
    core.Component({
        selector: 'segment-node',
        template: `<div cdkDropList [id]="node.uid" [cdkDropListData]="node" [cdkDropListConnectedTo]="connectedDropListsIds" (cdkDropListDropped)="onDragDrop($event)" [cdkDropListSortingDisabled]="false" [cdkDropListEnterPredicate]="canBeDropped" [ngClass]="{'perso-segments-tree__layout': isContainer(node), 'perso-segments-tree__empty-container': isEmptyContainer(node), 'perso-segments-tree__collapsed-container': collapsed }"><div><div class="perso-segments-tree" [ngClass]="{'perso-segments-tree__node': isItem(node), 'perso-segments-tree__container': isContainer(node)}"><div *ngIf="isContainer(node)"><div class="perso-segments-tree__toggle" (click)="toggle()" title="{{collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse' | translate}}" [ngClass]="collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'"></div><div class="perso-segments-tree__dropdown"><label class="fd-form-label perso-segments-tree__creategroupselect__label" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.select.label"></label><se-select class="se-select-creategroup" [model]="node.operation.id" (modelChange)="operationChange($event)" [fetchStrategy]="fetchStrategy" [resetSearchInput]="false" [searchEnabled]="false"></se-select></div></div><div *ngIf="isItem(node)" class="perso-segments-tree__node-content">{{ node.selectedSegment.code }}</div><div *ngIf="isContainer(node)" class="perso-segments-tree__angular-ui-tree-handle--empty"></div><div><span class="perso-segments-tree__pull-right"><a *ngIf="isItem(node)" class="fd-link perso-segments-tree__actions perso-segments-tree__node-icon" (click)="duplicateItem(node)" title="{{'personalization.commons.icon.title.duplicate' | translate}}"><span class="sap-icon--duplicate"></span> </a><a *ngIf="!isTopContainer()" class="fd-link perso-segments-tree__actions" (click)="removeItem(node?.uid)"><div *ngIf="isContainer(node)" class="btn btn-link perso-segments-tree__container-btn-icon fd-has-margin-left-tiny" title="{{'personalization.commons.icon.title.remove' | translate}}"><span class="btn sap-icon--decline"></span></div><div *ngIf="isItem(node)" class="perso-segments-tree__node-icon" title="{{'personalization.commons.icon.title.remove' | translate}}"><span class="sap-icon--decline"></span></div></a></span><button *ngIf="isContainer(node)" class="fd-button fd-button--light perso-segments-tree__btn" (click)="newSubItem('container')"><span translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.group.button"></span></button></div></div><div *ngIf="isEmptyContainer(node) && !collapsed" class="perso-segments-tree__empty-container-node"><div class="perso-segments-tree__empty-container-node-text" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.segments.dropzone"></div></div><ol *ngIf="!collapsed"><li cdkDrag [id]="innerNode.uid" [cdkDragData]="innerNode" (cdkDragStarted)="onDragStarted($event)" (cdkDragMoved)="onDragMoved($event)" (cdkDragReleased)="onDragReleased()" *ngFor="let innerNode of node.nodes"><segment-node [node]="innerNode" [expression]="expression" [connectedDropListsIds]="connectedDropListsIds" (expressionChange)="handleUpdate($event)" (onDrop)="onDragDrop($event)" (onDragStart)="onDragStarted($event)"></segment-node></li></ol></div></div>`,
        styles: [`ol{position:relative;margin:0;padding:0;list-style:none}.cdk-drag{list-style-type:none}.cdk-drag-preview{z-index:2000!important}.cdk-drag-placeholder{opacity:0!important}.cdk-drop-list-dragging{border-top:2px dashed rgba(0,0,0,.4);border-right:2px dashed rgba(0,0,0,.4);border-bottom:2px dashed rgba(0,0,0,.4)}.se-select-creategroup{display:inline-block;width:5rem}`]
    }),
    __metadata("design:paramtypes", [exports.TriggerService,
        smarteditcommons.IConfirmationModalService,
        core$1.TranslateService,
        SegmentDragAndDropService])
], SegmentNodeComponent);

window.__smartedit__.addDecoratorPayload("Component", "SegmentViewComponent", {
    selector: 'segment-view',
    template: `<label class="fd-form-label" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupexpression"></label><div><div class="form-group"><segment-expression-as-html [segmentExpression]="expression[0]"></segment-expression-as-html></div><div class="form-group"><label fd-form-label for="segment-select">{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.segments' | translate }}</label><se-select id="segment-select" [model]="singleSegment.code" (modelChange)="segmentSelectedEvent($event)" [fetchStrategy]="segmentFetchStrategy" [itemComponent]="segmentPrinterComponent" [(reset)]="resetSelect" [placeholder]="'personalization.modal.customizationvariationmanagement.targetgrouptab.segments.placeholder'"></se-select></div><segment-node *ngFor="let node of expression" [node]="node" [expression]="expression" [connectedDropListsIds]="connectedDropListsIds" (expressionChange)="handleTreeUpdated($event)" (onDrop)="onDropHandler($event)" (onDragStart)="onDragStart($event)"></segment-node></div>`
});
let SegmentViewComponent = class SegmentViewComponent {
    constructor(personalizationsmarteditRestService, personalizationsmarteditMessageHandler, triggerService, personalizationsmarteditUtils, translateService, yjQuery) {
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.triggerService = triggerService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.translateService = translateService;
        this.yjQuery = yjQuery;
        this.expressionChange = new core.EventEmitter();
        this.segments = [];
        this.singleSegment = { code: null };
        this.triggerLookupMap = {};
        this.actions = this.triggerService.actions;
        this.segmentPrinterComponent = SegmentItemPrinterComponent;
        this.nodeTemplate = SegmentNodeComponent;
        this.connectedDropListsIds = [];
    }
    ngOnInit() {
        this.triggers = this.triggers || (this.targetGroupState.selectedVariation || {}).triggers;
        this.expression = this.expression || this.targetGroupState.expression;
        if (this.triggers && this.triggers.length > 0) {
            this.expression = this.triggerService.buildData(this.triggers);
        }
        else {
            this.expression = [
                {
                    uid: smarteditcommons.stringUtils.generateIdentifier(),
                    type: personalizationcommons.TriggerType.CONTAINER_TYPE,
                    operation: this.actions[0],
                    nodes: []
                }
            ];
        }
        this.syncExpressionOnTriggerData();
        this.elementToScroll = this.yjQuery('.se-slider-panel__body');
        this.segmentFetchStrategy = {
            fetchPage: (search, pageSize, currentPage) => this.loadSegmentItems(search, pageSize, currentPage),
            fetchEntity: (id) => Promise.resolve(this.segments.find((segment) => segment.code === id))
        };
        this.getIdsRecursive(this.rootExpression);
    }
    get rootExpression() {
        var _a;
        return ((_a = this.expression) === null || _a === void 0 ? void 0 : _a.length) ? this.expression[0] : null;
    }
    segmentSelectedEvent(itemCode) {
        if (!itemCode) {
            this.singleSegment = { code: null };
            return;
        }
        const item = this.segments.find((segment) => segment.code === itemCode);
        this.expression = [
            Object.assign(Object.assign({}, this.rootExpression), { nodes: [
                    {
                        type: personalizationcommons.TriggerType.ITEM_TYPE,
                        operation: null,
                        selectedSegment: item,
                        nodes: [],
                        uid: smarteditcommons.stringUtils.generateIdentifier()
                    }
                ].concat(this.rootExpression.nodes) })
        ];
        this.getIdsRecursive(this.rootExpression);
        setTimeout(() => {
            this.resetSelect();
        });
        this.syncExpressionOnTriggerData();
    }
    handleTreeUpdated(expression) {
        this.triggerLookupMap = {};
        this.expression = lodash.cloneDeep(expression);
        this.getIdsRecursive(this.rootExpression);
        this.syncExpressionOnTriggerData();
    }
    onDropHandler(event) {
        this.scrollZoneVisible = false;
        if (event.container.id === event.previousContainer.id) {
            const container = this.triggerLookupMap[event.container.id];
            dragDrop.moveItemInArray(container.nodes, event.previousIndex, event.currentIndex);
        }
        else {
            const target = this.triggerLookupMap[event.container.id];
            target.nodes.splice(event.currentIndex, 0, event.item.data);
            const previous = this.triggerLookupMap[event.previousContainer.id];
            previous.nodes.splice(previous.nodes.findIndex((n) => n.uid === event.item.data.uid), 1);
        }
        this.handleTreeUpdated(this.expression);
    }
    onDragStart() {
        this.scrollZoneVisible = this.isScrollZoneVisible();
    }
    loadSegmentItems(search, pageSize, currentPage) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.personalizationsmarteditRestService.getSegments({
                    code: search,
                    pageSize,
                    currentPage
                });
                this.personalizationsmarteditUtils.uniqueArray(this.segments, response.segments || []);
                return Object.assign(Object.assign({}, response), { results: response.results.map((item) => (Object.assign(Object.assign({}, item), { id: item.code }))) });
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingsegments'));
            }
        });
    }
    syncExpressionOnTriggerData() {
        this.expressionChange.emit(this.expression);
    }
    getIdsRecursive(item) {
        this.triggerLookupMap[item.uid] = item;
        (item.nodes || []).forEach((childItem) => {
            if (!this.triggerService.isItem(childItem)) {
                this.getIdsRecursive(childItem);
            }
        });
        this.connectedDropListsIds = Object.keys(this.triggerLookupMap).reverse();
    }
    isScrollZoneVisible() {
        const element = this.yjQuery('.se-slider-panel__body').get(0);
        if (element) {
            return element.scrollHeight > element.clientHeight;
        }
        return false;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SegmentViewComponent.prototype, "targetGroupState", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], SegmentViewComponent.prototype, "expressionChange", void 0);
SegmentViewComponent = __decorate([
    core.Component({
        selector: 'segment-view',
        template: `<label class="fd-form-label" translate="personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupexpression"></label><div><div class="form-group"><segment-expression-as-html [segmentExpression]="expression[0]"></segment-expression-as-html></div><div class="form-group"><label fd-form-label for="segment-select">{{ 'personalization.modal.customizationvariationmanagement.targetgrouptab.segments' | translate }}</label><se-select id="segment-select" [model]="singleSegment.code" (modelChange)="segmentSelectedEvent($event)" [fetchStrategy]="segmentFetchStrategy" [itemComponent]="segmentPrinterComponent" [(reset)]="resetSelect" [placeholder]="'personalization.modal.customizationvariationmanagement.targetgrouptab.segments.placeholder'"></se-select></div><segment-node *ngFor="let node of expression" [node]="node" [expression]="expression" [connectedDropListsIds]="connectedDropListsIds" (expressionChange)="handleTreeUpdated($event)" (onDrop)="onDropHandler($event)" (onDragStart)="onDragStart($event)"></segment-node></div>`
    }),
    __param(5, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        exports.TriggerService,
        personalizationcommons.PersonalizationsmarteditUtils,
        core$1.TranslateService, Function])
], SegmentViewComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
let PersonalizationsmarteditSegmentViewModule = class PersonalizationsmarteditSegmentViewModule {
};
PersonalizationsmarteditSegmentViewModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            exports.SePersonalizationsmarteditServicesModule,
            PersonalizationsmarteditCommerceCustomizationModule,
            SegmentExpressionAsHtmlModule,
            personalizationcommons.ScrollZoneModule,
            smarteditcommons.SelectModule,
            smarteditcommons.NgTreeModule,
            smarteditcommons.TranslationModule.forChild(),
            dragDrop.DragDropModule
        ],
        providers: [SegmentDragAndDropService],
        declarations: [SegmentViewComponent, SegmentNodeComponent, SegmentItemPrinterComponent],
        entryComponents: [SegmentViewComponent, SegmentNodeComponent, SegmentItemPrinterComponent],
        exports: [SegmentViewComponent]
    })
], PersonalizationsmarteditSegmentViewModule);

exports.ManageCustomizationViewModule = class ManageCustomizationViewModule {
};
exports.ManageCustomizationViewModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            smarteditcommons.TranslationModule.forChild(),
            personalizationcommons.DatetimePickerRangeModule,
            smarteditcommons.TabsModule,
            smarteditcommons.TooltipModule,
            smarteditcommons.SliderPanelModule,
            smarteditcommons.DropdownMenuModule,
            SegmentExpressionAsHtmlModule,
            PersonalizationsmarteditSegmentViewModule,
            ModalFullScreenButtonModule,
            button.ButtonModule,
            form.FormModule,
            _switch.SwitchModule,
            checkbox.CheckboxModule
        ],
        providers: [exports.ManageCustomizationViewManager, exports.CustomizationViewService, exports.TriggerService],
        declarations: [
            exports.BasicInfoTabComponent,
            TargetGroupTabComponent,
            TargetGroupVariationListComponent,
            EditVariationItemComponent,
            ToggleVariationActiveItemComponent,
            MoveVariationUpItemComponent,
            MoveVariationDownItemComponent,
            RemoveVariationItemComponent,
            ManageCustomizationViewComponent
        ],
        entryComponents: [
            exports.BasicInfoTabComponent,
            TargetGroupTabComponent,
            EditVariationItemComponent,
            ToggleVariationActiveItemComponent,
            MoveVariationUpItemComponent,
            MoveVariationDownItemComponent,
            RemoveVariationItemComponent,
            ManageCustomizationViewComponent
        ],
        exports: [exports.BasicInfoTabComponent, TargetGroupTabComponent]
    })
], exports.ManageCustomizationViewModule);

window.__smartedit__.addDecoratorPayload("Component", "CustomizationsListComponent", {
    selector: 'customizations-list',
    template: `<div class="pe-customize-panel-list"><div class="pe-customize-panel-list__header"><span class="pe-customize-panel-list__header--name" translate="personalization.toolbar.pagecustomizations.list.title"></span> <span class="pe-customize-panel-list__header--status" translate="personalization.toolbar.pagecustomizations.list.status"></span></div><div *ngFor="let customization of customizationsList; last as isLast; trackBy: customizationsListTrackBy" [ngClass]="isLast && customization.collapsed && isCustomizationFromCurrentCatalog(customization, isLast) ? 'pe-customize-panel-list__item-last':''"><div class="pe-customize-panel-list__row-layout" [ngClass]="getSelectedCustomizationClass(customization)"><div class="pe-customize-panel-list__icon-layout pe-customize-panel-list__icon-divider" (click)="customizationRowClick(customization)"><a class="pe-customize-panel-list__btn-link btn btn-link" title="{{(customization.collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse') | translate}}"><span [ngClass]="customization.collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'"></span></a></div><div class="pe-customize-panel-list__row" (click)="customizationRowClick(customization,true)"><div class="pe-customize-panel-list__col-lg"><div class="perso-wrap-ellipsis pe-customize-panel-list__parent-layout perso-tree__primary-data" [textContent]="customization.name" title="{{customization.name}}"></div></div><div class="pe-customize-panel-list__col-md"></div><div class="pe-customize-panel-list__col-md"><div class="perso-tree__status" [ngClass]="getActivityStateForCustomization(customization)" [textContent]="getEnablementTextForCustomization(customization)"></div><div class="perso-tree__dates-layout" *ngIf="isEnabled(customization)" [textContent]="getDatesForCustomization(customization)"></div></div><div class="pe-customize-panel-list__col-sm"><span *ngIf="!isCustomizationFromCurrentCatalog(customization)" class="perso__globe-icon sap-icon--globe"></span></div></div><div class="pe-customize-panel-list__col-xs pe-customize-panel-list__dropdown"><div *ngIf="isCustomizationFromCurrentCatalog(customization)" class="y-dropdown-more-menu dropdown open"><button type="button" class="pe-customize-panel-list__btn-link fd-button fd-button--light customization-rank-{{customization.rank}}-dropdown-toggle" (click)="customizationSubMenuAction(customization)"><span class="perso__more-icon sap-icon--overflow"></span></button><ul *ngIf="customization.subMenu" class="se-y-dropdown-menu__list fd-menu__list dropdown-menu" role="menu"><li><a class="se-dropdown-item fd-menu__item cutomization-rank-{{customization.rank}}-edit-button" (click)="clearAllSubMenu();editCustomizationAction(customization)" translate="personalization.toolbar.pagecustomizations.customization.options.edit"></a></li></ul></div></div></div><div *ngIf="!customization.collapsed"><div class="pe-customize-panel-list__row-layout" *ngFor="let variation of customization.variations" [ngClass]="getSelectedVariationClass(variation)" (click)="clearAllSubMenu();variationClick(customization, variation)"><div class="pe-customize-panel-list__icon-layout"><div class="pe-customize-panel-list__btn-link btn btn-link"></div></div><div class="pe-customize-panel-list__row"><div class="pe-customize-panel-list__col-lg"><div class="perso-wrap-ellipsis pe-customize-panel-list__child-layout" [textContent]="variation.name" title="{{variation.name}}"></div></div><div class="pe-customize-panel-list__col-md"><div class="pe-customize-panel-list__components-layout"><div [hidden]="variation.numberOfAffectedComponents &lt; 0" class="pe-customize-panel-list__number-layout">{{variation.numberOfAffectedComponents}}</div><div [hidden]="variation.numberOfAffectedComponents >= 0" class="pe-customize-panel-list__number-layout">#</div><div class="perso-wrap-ellipsis" translate="personalization.toolbar.pagecustomizations.variation.numberofaffectedcomponents.label" title="{{'personalization.toolbar.pagecustomizations.variation.numberofaffectedcomponents.label' | translate}}"></div></div></div><div class="pe-customize-panel-list__col-md perso-tree__status" [ngClass]="getActivityStateForVariation(customization, variation)" [textContent]="getEnablementTextForVariation(variation)"></div><div class="pe-customize-panel-list__col-sm"><span class="perso__cc-icon sap-icon--tag" [ngClass]="{'perso__cc-icon--hidden': !hasCommerceActions(variation)}" [title]="getCommerceCustomizationTooltip(variation)"></span></div></div><div class="pe-customize-panel-list__col-xs pe-customize-panel-list__dropdown"></div></div></div></div></div><div *ngIf="requestProcessing" class="se-spinner fd-busy-indicator-extended"><div class="spinner"><fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator></div></div>`
});
exports.CustomizationsListComponent = class CustomizationsListComponent {
    constructor(translateService, personalizationsmarteditContextService, personalizationsmarteditRestService, personalizationsmarteditCommerceCustomizationService, personalizationsmarteditMessageHandler, personalizationsmarteditUtils, personalizationsmarteditDateUtils, personalizationsmarteditContextUtils, personalizationsmarteditPreviewService, personalizationsmarteditManager, customizeViewServiceProxy, systemEventService, crossFrameEventService, toolbarItem) {
        this.translateService = translateService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditCommerceCustomizationService = personalizationsmarteditCommerceCustomizationService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.personalizationsmarteditDateUtils = personalizationsmarteditDateUtils;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        this.personalizationsmarteditPreviewService = personalizationsmarteditPreviewService;
        this.personalizationsmarteditManager = personalizationsmarteditManager;
        this.customizeViewServiceProxy = customizeViewServiceProxy;
        this.systemEventService = systemEventService;
        this.crossFrameEventService = crossFrameEventService;
        this.toolbarItem = toolbarItem;
        this.sourceContainersComponentsInfo = {};
        this.customizationsListPreCount = 0;
    }
    ngOnDestroy() {
        if (this.customizationsModifiedUnsubscribe) {
            this.customizationsModifiedUnsubscribe();
        }
    }
    ngOnInit() {
        this.sourceContainersComponentsInfo = {};
        this.customizationsModifiedUnsubscribe = this.systemEventService.subscribe('CUSTOMIZATIONS_MODIFIED', () => {
            this.refreshCustomizeContext();
        });
    }
    ngDoCheck() {
        if (this.customizationsListPreCount !== this.customizationsList.length) {
            this.updateCustomizationList();
            this.customizationsListPreCount = this.customizationsList.length;
        }
    }
    updateCustomizationList() {
        this.customizationsList.forEach((customization) => {
            customization.collapsed = true;
            if ((this.personalizationsmarteditContextService.getCustomize()
                .selectedCustomization || {}).code === customization.code) {
                customization.collapsed = false;
                this.updateCustomizationData(customization);
            }
            this.personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(customization);
        });
    }
    editCustomizationAction(customization) {
        this.personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(this.personalizationsmarteditPreviewService, this.personalizationsmarteditContextService);
        this.personalizationsmarteditManager.openEditCustomizationModal(customization.code, '');
        this.toolbarItem.isOpen = false;
    }
    customizationRowClick(customization, select) {
        this.clearAllSubMenu();
        customization.collapsed = !customization.collapsed;
        if (!customization.collapsed) {
            this.updateCustomizationData(customization);
        }
        if (select) {
            this.customizationClick(customization);
        }
        this.customizationsList
            .filter((cust) => customization.code !== cust.code)
            .forEach((cust) => {
            cust.collapsed = true;
        });
    }
    customizationClick(customization) {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const currentVariations = this.personalizationsmarteditContextService.getCustomize().selectedVariations;
        const visibleVariations = this.getVisibleVariations(customization);
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = customization;
        customize.selectedVariations = visibleVariations;
        this.personalizationsmarteditContextService.setCustomize(customize);
        if (visibleVariations.length > 0) {
            const allVariations = this.personalizationsmarteditUtils
                .getVariationCodes(visibleVariations)
                .join(',');
            this.getAndSetComponentsForVariation(customization.code, allVariations, customization.catalog, customization.catalogVersion);
        }
        if ((lodash__namespace.isObjectLike(currentVariations) &&
            !lodash__namespace.isArray(currentVariations)) ||
            combinedView.enabled) {
            this.updatePreviewTicket();
        }
        this.personalizationsmarteditContextUtils.clearCombinedViewContext(this.personalizationsmarteditContextService);
        this.crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
    }
    getSelectedVariationClass(variation) {
        if (lodash__namespace.isEqual(variation.code, (this.personalizationsmarteditContextService.getCustomize()
            .selectedVariations || {}).code)) {
            return 'selectedVariation';
        }
        else {
            return '';
        }
    }
    getSelectedCustomizationClass(customization) {
        if (lodash__namespace.isEqual(customization.code, (this.personalizationsmarteditContextService.getCustomize()
            .selectedCustomization || {}).code) &&
            lodash__namespace.isArray(this.personalizationsmarteditContextService.getCustomize().selectedVariations)) {
            return 'selectedCustomization';
        }
        else {
            return '';
        }
    }
    variationClick(customization, variation) {
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = customization;
        customize.selectedVariations = variation;
        this.personalizationsmarteditContextService.setCustomize(customize);
        this.personalizationsmarteditContextUtils.clearCombinedViewContext(this.personalizationsmarteditContextService);
        this.getAndSetComponentsForVariation(customization.code, variation.code, customization.catalog, customization.catalogVersion);
        this.updatePreviewTicket(customization.code, [variation]);
        this.crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
    }
    hasCommerceActions(variation) {
        return this.personalizationsmarteditUtils.hasCommerceActions(variation);
    }
    getCommerceCustomizationTooltip(variation) {
        return this.personalizationsmarteditUtils.getCommerceCustomizationTooltip(variation, '', '');
    }
    getActivityStateForCustomization(customization) {
        return this.personalizationsmarteditUtils.getActivityStateForCustomization(customization);
    }
    customizationsListTrackBy(index, _customizationsList) {
        return index;
    }
    getActivityStateForVariation(customization, variation) {
        return this.personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
    }
    clearAllSubMenu() {
        for (const customization of this.customizationsList) {
            customization.subMenu = false;
        }
    }
    getEnablementTextForCustomization(customization) {
        return this.personalizationsmarteditUtils.getEnablementTextForCustomization(customization, 'personalization.toolbar.pagecustomizations');
    }
    getEnablementTextForVariation(variation) {
        return this.personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.toolbar.pagecustomizations');
    }
    isEnabled(item) {
        return this.personalizationsmarteditUtils.isPersonalizationItemEnabled(item);
    }
    getDatesForCustomization(customization) {
        let activityStr = '';
        let startDateStr = '';
        let endDateStr = '';
        if (customization.enabledStartDate || customization.enabledEndDate) {
            startDateStr = this.personalizationsmarteditDateUtils.formatDateWithMessage(customization.enabledStartDate);
            endDateStr = this.personalizationsmarteditDateUtils.formatDateWithMessage(customization.enabledEndDate);
            if (!customization.enabledStartDate) {
                startDateStr = ' ...';
            }
            if (!customization.enabledEndDate) {
                endDateStr = '... ';
            }
            activityStr += ' (' + startDateStr + ' - ' + endDateStr + ') ';
        }
        return activityStr;
    }
    customizationSubMenuAction(customization) {
        if (!customization.subMenu) {
            this.clearAllSubMenu();
        }
        customization.subMenu = !customization.subMenu;
    }
    isCustomizationFromCurrentCatalog(customization) {
        return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, this.personalizationsmarteditContextService.getSeData());
    }
    statusNotDeleted(variation) {
        return this.personalizationsmarteditUtils.isItemVisible(variation);
    }
    matchActionForVariation(action, variation) {
        return (action.variationCode === variation.code &&
            action.actionCatalog === variation.catalog &&
            action.actionCatalogVersion === variation.catalogVersion);
    }
    numberOfAffectedComponentsForActions(actionsForVariation) {
        let result = 0;
        actionsForVariation.forEach((action) => {
            result += parseInt(this.sourceContainersComponentsInfo[action.containerId], 10) || 0;
        });
        return result;
    }
    initSourceContainersComponentsInfo() {
        const deferred = smarteditcommons.promiseUtils.defer();
        this.customizeViewServiceProxy.getSourceContainersInfo().then((response) => {
            this.sourceContainersComponentsInfo = response;
            deferred.resolve();
        }, () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingnumberofaffectedcomponentsforvariation'));
            deferred.reject();
        });
        return deferred.promise;
    }
    paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, currentPage) {
        this.personalizationsmarteditRestService
            .getCxCmsActionsOnPageForCustomization(customization, currentPage)
            .then((response) => {
            customization.variations.forEach((variation) => {
                const actionsForVariation = response.actions.filter((action) => this.matchActionForVariation(action, variation));
                variation.numberOfAffectedComponents =
                    currentPage === 0 ? 0 : variation.numberOfAffectedComponents;
                variation.numberOfAffectedComponents +=
                    this.numberOfAffectedComponentsForActions(actionsForVariation);
            });
            const nextPage = currentPage + 1;
            if (nextPage < response.pagination.totalPages) {
                this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, nextPage);
            }
        }, () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingnumberofaffectedcomponentsforvariation'));
        });
    }
    getAndSetNumberOfAffectedComponentsForVariations(customization) {
        const customize = this.personalizationsmarteditContextService.getCustomize();
        const isUpToDate = (customize.selectedComponents || []).every((componentId) => this.sourceContainersComponentsInfo[componentId] !== undefined);
        if (!isUpToDate ||
            customize.selectedComponents === null ||
            lodash__namespace.isEqual(this.sourceContainersComponentsInfo, {})) {
            this.initSourceContainersComponentsInfo().finally(() => {
                this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
            });
        }
        else if (isUpToDate) {
            this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
        }
    }
    getNumberOfAffectedComponentsForCorrespondingVariation(variationsArray, variationCode) {
        const foundVariation = variationsArray.filter((elem) => elem.code === variationCode);
        return (foundVariation[0] || {}).numberOfAffectedComponents;
    }
    updateCustomizationData(customization) {
        this.personalizationsmarteditRestService
            .getVariationsForCustomization(customization.code, customization)
            .then((response) => {
            const variations = response.variations
                ? lodash__namespace.cloneDeep(response.variations)
                : [];
            variations.forEach((variation, index) => {
                if (!this.statusNotDeleted(variation)) {
                    variations.splice(index, 1);
                }
                else {
                    variation.numberOfAffectedComponents =
                        this.getNumberOfAffectedComponentsForCorrespondingVariation(customization.variations, variation.code);
                    variation.numberOfCommerceActions =
                        this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                    variation.commerceCustomizations =
                        this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                }
            });
            customization.variations = variations || [];
            this.getAndSetNumberOfAffectedComponentsForVariations(customization);
        }, () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomization'));
        });
    }
    getVisibleVariations(customization) {
        return this.personalizationsmarteditUtils.getVisibleItems(customization.variations);
    }
    getAndSetComponentsForVariation(customizationId, variationId, catalog, catalogVersion) {
        this.personalizationsmarteditRestService
            .getComponentsIdsForVariation(customizationId, variationId, catalog, catalogVersion)
            .then((response) => {
            const customize = this.personalizationsmarteditContextService.getCustomize();
            customize.selectedComponents = response.components;
            this.personalizationsmarteditContextService.setCustomize(customize);
        }, () => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcomponentsforvariation'));
        });
    }
    updatePreviewTicket(customizationId, variationArray) {
        const variationKeys = this.personalizationsmarteditUtils.getVariationKey(customizationId, variationArray);
        this.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(variationKeys);
    }
    refreshCustomizeContext() {
        const customize = lodash__namespace.cloneDeep(this.personalizationsmarteditContextService.getCustomize());
        if (customize.selectedCustomization) {
            this.personalizationsmarteditRestService
                .getCustomization(customize.selectedCustomization)
                .then((response) => {
                customize.selectedCustomization = response;
                if (customize.selectedVariations &&
                    !lodash__namespace.isArray(customize.selectedVariations)) {
                    response.variations
                        .filter((item) => customize.selectedVariations
                        .code === item.code)
                        .forEach((variation) => {
                        customize.selectedVariations = variation;
                        if (!this.personalizationsmarteditUtils.isItemVisible(variation)) {
                            customize.selectedCustomization = null;
                            customize.selectedVariations = null;
                            this.personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
                        }
                    });
                }
                this.personalizationsmarteditContextService.setCustomize(customize);
            }, () => {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomization'));
            });
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], exports.CustomizationsListComponent.prototype, "customizationsList", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.CustomizationsListComponent.prototype, "requestProcessing", void 0);
exports.CustomizationsListComponent = __decorate([
    core.Component({
        selector: 'customizations-list',
        template: `<div class="pe-customize-panel-list"><div class="pe-customize-panel-list__header"><span class="pe-customize-panel-list__header--name" translate="personalization.toolbar.pagecustomizations.list.title"></span> <span class="pe-customize-panel-list__header--status" translate="personalization.toolbar.pagecustomizations.list.status"></span></div><div *ngFor="let customization of customizationsList; last as isLast; trackBy: customizationsListTrackBy" [ngClass]="isLast && customization.collapsed && isCustomizationFromCurrentCatalog(customization, isLast) ? 'pe-customize-panel-list__item-last':''"><div class="pe-customize-panel-list__row-layout" [ngClass]="getSelectedCustomizationClass(customization)"><div class="pe-customize-panel-list__icon-layout pe-customize-panel-list__icon-divider" (click)="customizationRowClick(customization)"><a class="pe-customize-panel-list__btn-link btn btn-link" title="{{(customization.collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse') | translate}}"><span [ngClass]="customization.collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'"></span></a></div><div class="pe-customize-panel-list__row" (click)="customizationRowClick(customization,true)"><div class="pe-customize-panel-list__col-lg"><div class="perso-wrap-ellipsis pe-customize-panel-list__parent-layout perso-tree__primary-data" [textContent]="customization.name" title="{{customization.name}}"></div></div><div class="pe-customize-panel-list__col-md"></div><div class="pe-customize-panel-list__col-md"><div class="perso-tree__status" [ngClass]="getActivityStateForCustomization(customization)" [textContent]="getEnablementTextForCustomization(customization)"></div><div class="perso-tree__dates-layout" *ngIf="isEnabled(customization)" [textContent]="getDatesForCustomization(customization)"></div></div><div class="pe-customize-panel-list__col-sm"><span *ngIf="!isCustomizationFromCurrentCatalog(customization)" class="perso__globe-icon sap-icon--globe"></span></div></div><div class="pe-customize-panel-list__col-xs pe-customize-panel-list__dropdown"><div *ngIf="isCustomizationFromCurrentCatalog(customization)" class="y-dropdown-more-menu dropdown open"><button type="button" class="pe-customize-panel-list__btn-link fd-button fd-button--light customization-rank-{{customization.rank}}-dropdown-toggle" (click)="customizationSubMenuAction(customization)"><span class="perso__more-icon sap-icon--overflow"></span></button><ul *ngIf="customization.subMenu" class="se-y-dropdown-menu__list fd-menu__list dropdown-menu" role="menu"><li><a class="se-dropdown-item fd-menu__item cutomization-rank-{{customization.rank}}-edit-button" (click)="clearAllSubMenu();editCustomizationAction(customization)" translate="personalization.toolbar.pagecustomizations.customization.options.edit"></a></li></ul></div></div></div><div *ngIf="!customization.collapsed"><div class="pe-customize-panel-list__row-layout" *ngFor="let variation of customization.variations" [ngClass]="getSelectedVariationClass(variation)" (click)="clearAllSubMenu();variationClick(customization, variation)"><div class="pe-customize-panel-list__icon-layout"><div class="pe-customize-panel-list__btn-link btn btn-link"></div></div><div class="pe-customize-panel-list__row"><div class="pe-customize-panel-list__col-lg"><div class="perso-wrap-ellipsis pe-customize-panel-list__child-layout" [textContent]="variation.name" title="{{variation.name}}"></div></div><div class="pe-customize-panel-list__col-md"><div class="pe-customize-panel-list__components-layout"><div [hidden]="variation.numberOfAffectedComponents &lt; 0" class="pe-customize-panel-list__number-layout">{{variation.numberOfAffectedComponents}}</div><div [hidden]="variation.numberOfAffectedComponents >= 0" class="pe-customize-panel-list__number-layout">#</div><div class="perso-wrap-ellipsis" translate="personalization.toolbar.pagecustomizations.variation.numberofaffectedcomponents.label" title="{{'personalization.toolbar.pagecustomizations.variation.numberofaffectedcomponents.label' | translate}}"></div></div></div><div class="pe-customize-panel-list__col-md perso-tree__status" [ngClass]="getActivityStateForVariation(customization, variation)" [textContent]="getEnablementTextForVariation(variation)"></div><div class="pe-customize-panel-list__col-sm"><span class="perso__cc-icon sap-icon--tag" [ngClass]="{'perso__cc-icon--hidden': !hasCommerceActions(variation)}" [title]="getCommerceCustomizationTooltip(variation)"></span></div></div><div class="pe-customize-panel-list__col-xs pe-customize-panel-list__dropdown"></div></div></div></div></div><div *ngIf="requestProcessing" class="se-spinner fd-busy-indicator-extended"><div class="spinner"><fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator></div></div>`
    }),
    __param(13, core.Inject(smarteditcommons.TOOLBAR_ITEM)),
    __metadata("design:paramtypes", [core$1.TranslateService,
        exports.PersonalizationsmarteditContextService,
        exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        personalizationcommons.PersonalizationsmarteditUtils,
        personalizationcommons.PersonalizationsmarteditDateUtils,
        personalizationcommons.PersonalizationsmarteditContextUtils,
        exports.PersonalizationsmarteditPreviewService,
        exports.ManageCustomizationViewManager,
        exports.CustomizeViewServiceProxy,
        smarteditcommons.SystemEventService,
        smarteditcommons.CrossFrameEventService, Object])
], exports.CustomizationsListComponent);

window.__smartedit__.addDecoratorPayload("Component", "CustomizeViewComponent", {
    selector: 'customize-view',
    template: `<div class="btn-block pe-toolbar-action--include"><div class="pe-toolbar-menu-content se-toolbar-menu-content--pe-customized-customizations-panel" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><div class="se-toolbar-menu-content--pe-customized__headers--wrapper"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2"><span translate="personalization.toolbar.pagecustomizations.header.title"></span> ({{pagination.totalCount}})</h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.pagecustomizations.header.description"></span></se-help></div><div class="se-input-group se-component-search"><input type="text" id="customization-search-name" class="se-input-group--input se-component-search--input fd-input" [(ngModel)]="nameFilter" [placeholder]="'personalization.toolbar.pagecustomizations.search.placeholder' | translate" (ngModelChange)="nameInputKeypress()"/> <span class="sap-icon--search se-input-group__addon"></span><span *ngIf="showResetButton" class="se-input-group__addon se-input-group__clear-btn sap-icon--decline" (click)="resetSearch($event)"></span></div></div><div role="menuitem"><div id="personalizationsmartedit-right-toolbar-item-template"><div class="pe-customize-panel__filter-label-layout"><div class="pe-customize-panel__filter-layout"><page-filter-dropdown (onSelectCallback)="pageFilterChange($event)" [initialValue]="pageFilter" class="perso-filter__wrapper perso-filter__wrapper--page"></page-filter-dropdown><has-multicatalog class="perso-filter__wrapper perso-filter__wrapper--catalog"><catalog-filter-dropdown (onSelectCallback)="catalogFilterChange($event)" [initialValue]="catalogFilter"></catalog-filter-dropdown></has-multicatalog><status-filter-dropdown (onSelectCallback)="statusFilterChange($event)" [initialValue]="statusFilter" class="perso-filter__wrapper perso-filter__wrapper--status"></status-filter-dropdown></div></div><personalization-infinite-scrolling [dropDownContainerClass]="'pe-customize-panel__wrapper'" [fetchPage]="getPage"><customizations-list [customizationsList]="customizationsList" [requestProcessing]="moreCustomizationsRequestProcessing"></customizations-list></personalization-infinite-scrolling></div></div></div></div>`
});
exports.CustomizeViewComponent = class CustomizeViewComponent {
    constructor(toolbarItem, translateService, customizationDataFactory, personalizationsmarteditContextService, personalizationsmarteditMessageHandler, personalizationsmarteditUtils) {
        this.toolbarItem = toolbarItem;
        this.translateService = translateService;
        this.customizationDataFactory = customizationDataFactory;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.getPage = () => this.addMoreCustomizationItems();
    }
    ngOnInit() {
        this.personalizationsmarteditContextService.refreshExperienceData();
        this.moreCustomizationsRequestProcessing = false;
        this.customizationsList = this.customizationDataFactory.items;
        this.customizationDataFactory.resetData();
        this.pagination = new personalizationcommons.PaginationHelper({});
        this.pagination.reset();
        this.filters = this.personalizationsmarteditContextService.getCustomizeFiltersState();
        this.catalogFilter = this.filters.catalogFilter;
        this.pageFilter = this.filters.pageFilter;
        this.statusFilter = this.filters.statusFilter;
        this.nameFilter = this.filters.nameFilter;
        this.showResetButton = false;
        this.oldIsOpen = this.toolbarItem.isOpen;
    }
    ngOnDestroy() {
        const filters = this.personalizationsmarteditContextService.getCustomizeFiltersState();
        filters.catalogFilter = this.catalogFilter;
        filters.pageFilter = this.pageFilter;
        filters.statusFilter = this.statusFilter;
        filters.nameFilter = this.nameFilter;
        this.personalizationsmarteditContextService.setCustomizeFiltersState(filters);
    }
    ngDoCheck() {
        if (this.toolbarItem.isOpen !== this.oldIsOpen) {
            if (this.toolbarItem.isOpen) {
                this.refreshList();
            }
            this.oldIsOpen = this.toolbarItem.isOpen;
        }
    }
    catalogFilterChange(itemId) {
        this.catalogFilter = itemId;
        this.refreshList();
    }
    pageFilterChange(itemId) {
        this.pageFilter = itemId;
        this.refreshList();
    }
    statusFilterChange(itemId) {
        this.statusFilter = itemId;
        this.refreshList();
    }
    nameInputKeypress() {
        if (this.nameFilter.length > 0) {
            this.showResetButton = true;
        }
        if (this.nameFilter.length > 2 || this.nameFilter.length === 0) {
            this.refreshList();
        }
    }
    resetSearch(event) {
        if (event) {
            event.stopPropagation();
        }
        this.nameFilter = '';
        this.showResetButton = false;
        this.refreshList();
    }
    addMoreCustomizationItems() {
        if (this.pagination.getPage() < this.pagination.getTotalPages() - 1 &&
            !this.moreCustomizationsRequestProcessing) {
            this.moreCustomizationsRequestProcessing = true;
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }
    errorCallback() {
        this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomizations'));
        this.moreCustomizationsRequestProcessing = false;
    }
    successCallback(response) {
        this.pagination = new personalizationcommons.PaginationHelper(response.pagination);
        this.moreCustomizationsRequestProcessing = false;
    }
    getStatus() {
        if (this.statusFilter === undefined) {
            return this.personalizationsmarteditUtils.getStatusesMapping()[0];
        }
        return this.personalizationsmarteditUtils
            .getStatusesMapping()
            .filter((elem) => elem.code === this.statusFilter)[0];
    }
    getCustomizations(categoryFilter) {
        const params = {
            filter: categoryFilter,
            dataArrayName: 'customizations'
        };
        this.customizationDataFactory.updateData(params, this.successCallback.bind(this), this.errorCallback.bind(this));
    }
    getCustomizationsFilterObject() {
        const ret = {
            currentSize: this.pagination.getCount(),
            currentPage: this.pagination.getPage() + 1,
            name: this.nameFilter,
            statuses: this.getStatus().modelStatuses,
            catalogs: this.catalogFilter
        };
        if (this.pageFilter === personalizationcommons.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE) {
            ret.pageId = this.personalizationsmarteditContextService.getSeData().pageId;
            ret.pageCatalogId = (this.personalizationsmarteditContextService.getSeData().seExperienceData
                .pageContext || {}).catalogId;
        }
        return ret;
    }
    refreshList() {
        if (this.moreCustomizationsRequestProcessing === false) {
            this.moreCustomizationsRequestProcessing = true;
            this.pagination.reset();
            this.customizationDataFactory.resetData();
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }
};
exports.CustomizeViewComponent = __decorate([
    core.Component({
        selector: 'customize-view',
        template: `<div class="btn-block pe-toolbar-action--include"><div class="pe-toolbar-menu-content se-toolbar-menu-content--pe-customized-customizations-panel" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><div class="se-toolbar-menu-content--pe-customized__headers--wrapper"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2"><span translate="personalization.toolbar.pagecustomizations.header.title"></span> ({{pagination.totalCount}})</h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.pagecustomizations.header.description"></span></se-help></div><div class="se-input-group se-component-search"><input type="text" id="customization-search-name" class="se-input-group--input se-component-search--input fd-input" [(ngModel)]="nameFilter" [placeholder]="'personalization.toolbar.pagecustomizations.search.placeholder' | translate" (ngModelChange)="nameInputKeypress()"/> <span class="sap-icon--search se-input-group__addon"></span><span *ngIf="showResetButton" class="se-input-group__addon se-input-group__clear-btn sap-icon--decline" (click)="resetSearch($event)"></span></div></div><div role="menuitem"><div id="personalizationsmartedit-right-toolbar-item-template"><div class="pe-customize-panel__filter-label-layout"><div class="pe-customize-panel__filter-layout"><page-filter-dropdown (onSelectCallback)="pageFilterChange($event)" [initialValue]="pageFilter" class="perso-filter__wrapper perso-filter__wrapper--page"></page-filter-dropdown><has-multicatalog class="perso-filter__wrapper perso-filter__wrapper--catalog"><catalog-filter-dropdown (onSelectCallback)="catalogFilterChange($event)" [initialValue]="catalogFilter"></catalog-filter-dropdown></has-multicatalog><status-filter-dropdown (onSelectCallback)="statusFilterChange($event)" [initialValue]="statusFilter" class="perso-filter__wrapper perso-filter__wrapper--status"></status-filter-dropdown></div></div><personalization-infinite-scrolling [dropDownContainerClass]="'pe-customize-panel__wrapper'" [fetchPage]="getPage"><customizations-list [customizationsList]="customizationsList" [requestProcessing]="moreCustomizationsRequestProcessing"></customizations-list></personalization-infinite-scrolling></div></div></div></div>`
    }),
    __param(0, core.Inject(smarteditcommons.TOOLBAR_ITEM)),
    __metadata("design:paramtypes", [Object, core$1.TranslateService,
        CustomizationDataFactory,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        personalizationcommons.PersonalizationsmarteditUtils])
], exports.CustomizeViewComponent);

exports.SePersonalizationsmarteditCustomizeViewModule = class SePersonalizationsmarteditCustomizeViewModule {
};
exports.SePersonalizationsmarteditCustomizeViewModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.SharedComponentsModule,
            busyIndicator.BusyIndicatorModule,
            exports.SePersonalizationsmarteditServicesModule,
            PersonalizationsmarteditDataFactoryModule,
            PersonalizationsmarteditSharedComponentsModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            menu.MenuModule
        ],
        declarations: [exports.CustomizationsListComponent, exports.CustomizeViewComponent],
        entryComponents: [exports.CustomizationsListComponent, exports.CustomizeViewComponent],
        providers: [exports.CustomizeViewServiceProxy],
        exports: []
    })
], exports.SePersonalizationsmarteditCustomizeViewModule);

/* @ngInject */ exports.ManagerViewUtilsService = class /* @ngInject */ ManagerViewUtilsService {
    constructor(personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditCommerceCustomizationService, waitDialogService, confirmationModalService, translateService) {
        this.personalizationsmarteditRestService = personalizationsmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.personalizationsmarteditCommerceCustomizationService = personalizationsmarteditCommerceCustomizationService;
        this.waitDialogService = waitDialogService;
        this.confirmationModalService = confirmationModalService;
        this.translateService = translateService;
    }
    deleteCustomizationAction(customization, customizations) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.confirmationModalService.confirm({
                description: 'personalization.modal.manager.deletecustomization.content'
            });
            try {
                const responseCustomization = yield this.personalizationsmarteditRestService.getCustomization(customization);
                yield this.markCustomizationAsDeleted(responseCustomization, customization, customizations);
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.deletingcustomization'));
            }
        });
    }
    deleteVariationAction(customization, variation) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.confirmationModalService.confirm({
                description: 'personalization.modal.manager.deletevariation.content'
            });
            try {
                const responseVariation = yield this.personalizationsmarteditRestService.getVariation(customization.code, variation.code);
                responseVariation.status = personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
                yield this.updateVariationStatus(responseVariation, variation, customization);
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.deletingvariation'));
            }
        });
    }
    toggleVariationActive(customization, variation) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const responseVariation = yield this.personalizationsmarteditRestService.getVariation(customization.code, variation.code);
                responseVariation.enabled = !responseVariation.enabled;
                responseVariation.status = responseVariation.enabled
                    ? personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
                    : personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
                yield this.updateVariationStatusAndEnablement(responseVariation, variation, customization);
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingvariation'));
            }
        });
    }
    customizationClickAction(customization) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.personalizationsmarteditRestService.getVariationsForCustomization(customization.code, customization);
                customization.variations.forEach((variation) => {
                    variation.actions = this.getActionsForVariation(variation.code, response.variations);
                    variation.numberOfComponents =
                        this.personalizationsmarteditCommerceCustomizationService.getNonCommerceActionsCount(variation);
                    variation.commerceCustomizations =
                        this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                    variation.numberOfCommerceActions =
                        this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                });
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomization'));
            }
        });
    }
    getCustomizations(filter) {
        return this.personalizationsmarteditRestService.getCustomizations(filter);
    }
    updateCustomizationRank(customizationCode, increaseValue) {
        return this.personalizationsmarteditRestService
            .updateCustomizationRank(customizationCode, increaseValue)
            .catch(() => {
            this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.updatingcustomization'));
        });
    }
    updateVariationRank(customization, variationCode, increaseValue) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const responseVariation = yield this.personalizationsmarteditRestService.getVariation(customization.code, variationCode);
                responseVariation.rank = responseVariation.rank + increaseValue;
                return this.personalizationsmarteditRestService
                    .editVariation(customization.code, responseVariation)
                    .catch(() => {
                    this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.editingvariation'));
                });
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingvariation'));
            }
        });
    }
    setCustomizationRank(customization, increaseValue, customizations) {
        return __awaiter(this, void 0, void 0, function* () {
            const nextItem = customizations[customizations.indexOf(customization) + increaseValue];
            this.waitDialogService.showWaitModal();
            try {
                yield this.updateCustomizationRank(customization.code, nextItem.rank - customization.rank);
                customization.rank += increaseValue;
                nextItem.rank -= increaseValue;
                const index = customizations.indexOf(customization);
                const tempItem = customizations.splice(index, 1);
                customizations.splice(index + increaseValue, 0, tempItem[0]);
                this.waitDialogService.hideWaitModal();
            }
            catch (_a) {
                this.waitDialogService.hideWaitModal();
            }
        });
    }
    setVariationRank(customization, variation, increaseValue) {
        return __awaiter(this, void 0, void 0, function* () {
            const nextItem = customization.variations[customization.variations.indexOf(variation) + increaseValue];
            this.waitDialogService.showWaitModal();
            try {
                yield this.updateVariationRank(customization, variation.code, increaseValue);
                variation.rank += increaseValue;
                nextItem.rank -= increaseValue;
                const index = customization.variations.indexOf(variation);
                const tempItem = customization.variations.splice(index, 1);
                customization.variations.splice(index + increaseValue, 0, tempItem[0]);
            }
            catch (error) {
                console.log('Unexpected error occurred while updating variation rank', error);
            }
            finally {
                this.waitDialogService.hideWaitModal();
            }
        });
    }
    getActionsForVariation(variationCode, variationsArray) {
        variationsArray = variationsArray || [];
        const variation = variationsArray.filter((elem) => variationCode === elem.code)[0];
        return variation ? variation.actions : [];
    }
    markCustomizationAsDeleted(responseCustomization, originalCustomization, customizations) {
        return __awaiter(this, void 0, void 0, function* () {
            responseCustomization.status = personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
            try {
                yield this.personalizationsmarteditRestService.updateCustomization(responseCustomization);
                customizations.splice(customizations.indexOf(originalCustomization), 1);
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.deletingcustomization'));
            }
        });
    }
    updateVariationStatus(responseVariation, variation, customization) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.personalizationsmarteditRestService.editVariation(customization.code, responseVariation);
                variation.status = response.status;
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.deletingvariation'));
            }
        });
    }
    updateVariationStatusAndEnablement(responseVariation, variation, customization) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.personalizationsmarteditRestService.editVariation(customization.code, responseVariation);
                variation.enabled = response.enabled;
                variation.status = response.status;
            }
            catch (_a) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.editingvariation'));
            }
        });
    }
};
/* @ngInject */ exports.ManagerViewUtilsService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
        smarteditcommons.IWaitDialogService,
        smarteditcommons.IConfirmationModalService,
        core$1.TranslateService])
], /* @ngInject */ exports.ManagerViewUtilsService);

window.__smartedit__.addDecoratorPayload("Component", "ManagerViewComponent", {
    selector: 'customizations-list',
    template: `<div id="editConfigurationsBody-001" class="perso-library"><personalization-infinite-scrolling [fetchPage]="getPage"><div #managerScrollZone id="managerScrollZone" class="perso-library__scroll-zone perso__scrollbar--hidden"><div class="perso-library__header"><div class="perso-library__title"><span [textContent]="catalogName"></span> | {{'personalization.toolbar.pagecustomizations.header.title' | translate}} ({{pagination.totalCount}})</div><div class="se-input-group"><div class="perso-input-group"><input fd-form-control id="customizationSearchInput" name="customizationSearchInput" type="text" class="se-input-group--input se-component-search--input" [placeholder]="'personalization.modal.manager.search.placeholder' | translate" [(ngModel)]="customizationSearch.name" (ngModelChange)="searchInputKeypress()"><span class="sap-icon--search se-input-group__addon"></span><span *ngIf="showResetButton" (click)="resetSearch($event)" class="se-input-group__addon se-input-group__clear-btn sap-icon--decline"></span></div></div><status-filter-dropdown (onSelectCallback)="statusFilterChange($event)" [initialValue]="customizationSearch.status" class="perso-library__header-space"></status-filter-dropdown><div class="perso-library__header-space"><button class="fd-button" type="button" id="ps_manager_add_btn" (click)="openNewModal()"><span translate="personalization.modal.manager.add.button"></span></button></div></div><div class="y-tree perso-library__y-tree"><div class="y-tree-header"><manager-view-grid-header></manager-view-grid-header></div><manager-view-tree [customizations]="customizations"></manager-view-tree><div *ngIf="moreCustomizationsRequestProcessing" class="se-spinner fd-busy-indicator-extended"><div class="spinner"><fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator></div></div></div></div></personalization-infinite-scrolling><personalizationsmartedit-scroll-zone *ngIf="scrollZoneElement != null && elementToScroll" [isTransparent]="true" [scrollZoneVisible]="scrollZoneVisible" [elementToScroll]="elementToScroll"></personalizationsmartedit-scroll-zone><a class="perso-library__back-to-top fd-link" translate="personalization.commons.button.title.backtotop" [hidden]="!isReturnToTopButtonVisible()" (click)="scrollZoneReturnToTop()"><span class="sap-icon--back-to-top"></span></a></div>`,
    styles: [`.se-component-search--input{margin:0}.perso-library__y-tree .fd-busy-indicator-extended{margin-top:0;margin-bottom:0}`]
});
exports.ManagerViewComponent = class ManagerViewComponent {
    constructor(translateService, managerViewUtilsService, messageHandler, contextService, utils, manageCustomizationViewManager, systemEventService) {
        this.translateService = translateService;
        this.managerViewUtilsService = managerViewUtilsService;
        this.messageHandler = messageHandler;
        this.contextService = contextService;
        this.utils = utils;
        this.manageCustomizationViewManager = manageCustomizationViewManager;
        this.systemEventService = systemEventService;
        this.getPage = () => this.addMoreItems();
        const seExperienceData = this.contextService.getSeData().seExperienceData;
        const currentLanguageIsocode = seExperienceData.languageDescriptor.isocode;
        this.catalogName = `${seExperienceData.catalogDescriptor.name[currentLanguageIsocode]} - ${seExperienceData.catalogDescriptor.catalogVersion}`;
        this.customizations = [];
        this.showResetButton = false;
        this.customizationSearch = {
            name: '',
            status: ''
        };
        this.moreCustomizationsRequestProcessing = false;
        this.pagination = new personalizationcommons.PaginationHelper();
        this.pagination.reset();
        this.scrollZoneObject = {
            scrollZoneElement: undefined,
            filteredCustomizationsCount: 0
        };
    }
    ngOnDestroy() {
        if (this.customizationsModifiedUnsubscribe) {
            this.customizationsModifiedUnsubscribe();
        }
    }
    ngOnInit() {
        this.customizationsModifiedUnsubscribe = this.systemEventService.subscribe('CUSTOMIZATIONS_MODIFIED', () => {
            this.refreshGrid();
        });
    }
    ngAfterViewInit() {
        this.scrollZoneObject.scrollZoneElement = this.managerScrollZone.nativeElement;
    }
    searchInputKeypress() {
        if (this.customizationSearch.name.length > 0) {
            this.showResetButton = true;
        }
        if (this.customizationSearch.name.length > 2 ||
            this.customizationSearch.name.length === 0) {
            this.refreshGrid();
        }
    }
    resetSearch(event) {
        if (event) {
            event.stopPropagation();
        }
        this.customizationSearch.name = '';
        this.showResetButton = false;
        this.refreshGrid();
    }
    statusFilterChange(itemId) {
        this.customizationSearch.status = itemId;
        this.refreshGrid();
    }
    addMoreItems() {
        if (this.pagination.getPage() < this.pagination.getTotalPages() - 1 &&
            !this.moreCustomizationsRequestProcessing) {
            this.moreCustomizationsRequestProcessing = true;
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }
    openNewModal() {
        this.manageCustomizationViewManager.openCreateCustomizationModal();
    }
    isSearchGridHeaderHidden() {
        if (!this.scrollZoneObject.scrollZoneElement) {
            return false;
        }
        return this.scrollZoneObject.scrollZoneElement.scrollTop >= 120;
    }
    scrollZoneReturnToTop() {
        setTimeout(() => {
            this.scrollZoneObject.scrollZoneElement.scrollTop = 0;
        }, 500);
    }
    isReturnToTopButtonVisible() {
        if (!this.scrollZoneObject.scrollZoneElement) {
            return false;
        }
        else {
            return this.scrollZoneObject.scrollZoneElement.scrollTop > 50;
        }
    }
    refreshGrid() {
        this.pagination.reset();
        this.customizations = [];
        this.addMoreItems();
    }
    getCustomizations(filter) {
        return __awaiter(this, void 0, void 0, function* () {
            const customizationResponse = yield this.managerViewUtilsService.getCustomizations(filter);
            try {
                this.utils.uniqueArray(this.customizations, customizationResponse.customizations || []);
                this.scrollZoneObject.filteredCustomizationsCount =
                    customizationResponse.pagination.totalCount;
                this.pagination = new personalizationcommons.PaginationHelper(customizationResponse.pagination);
                this.moreCustomizationsRequestProcessing = false;
            }
            catch (err) {
                this.messageHandler.sendError(this.translateService.instant('personalization.error.gettingcustomizations'));
                this.moreCustomizationsRequestProcessing = false;
            }
        });
    }
    getCustomizationsFilterObject() {
        return {
            active: 'all',
            name: this.customizationSearch.name,
            currentSize: this.pagination.getCount(),
            currentPage: this.pagination.getPage() + 1,
            statuses: this.getStatus().modelStatuses
        };
    }
    getStatus() {
        if (this.customizationSearch.status === '') {
            return this.utils.getStatusesMapping()[0];
        }
        return this.utils
            .getStatusesMapping()
            .filter((elem) => elem.code === this.customizationSearch.status)[0];
    }
};
__decorate([
    core.ViewChild('managerScrollZone', { static: false }),
    __metadata("design:type", core.ElementRef)
], exports.ManagerViewComponent.prototype, "managerScrollZone", void 0);
exports.ManagerViewComponent = __decorate([
    core.Component({
        selector: 'customizations-list',
        template: `<div id="editConfigurationsBody-001" class="perso-library"><personalization-infinite-scrolling [fetchPage]="getPage"><div #managerScrollZone id="managerScrollZone" class="perso-library__scroll-zone perso__scrollbar--hidden"><div class="perso-library__header"><div class="perso-library__title"><span [textContent]="catalogName"></span> | {{'personalization.toolbar.pagecustomizations.header.title' | translate}} ({{pagination.totalCount}})</div><div class="se-input-group"><div class="perso-input-group"><input fd-form-control id="customizationSearchInput" name="customizationSearchInput" type="text" class="se-input-group--input se-component-search--input" [placeholder]="'personalization.modal.manager.search.placeholder' | translate" [(ngModel)]="customizationSearch.name" (ngModelChange)="searchInputKeypress()"><span class="sap-icon--search se-input-group__addon"></span><span *ngIf="showResetButton" (click)="resetSearch($event)" class="se-input-group__addon se-input-group__clear-btn sap-icon--decline"></span></div></div><status-filter-dropdown (onSelectCallback)="statusFilterChange($event)" [initialValue]="customizationSearch.status" class="perso-library__header-space"></status-filter-dropdown><div class="perso-library__header-space"><button class="fd-button" type="button" id="ps_manager_add_btn" (click)="openNewModal()"><span translate="personalization.modal.manager.add.button"></span></button></div></div><div class="y-tree perso-library__y-tree"><div class="y-tree-header"><manager-view-grid-header></manager-view-grid-header></div><manager-view-tree [customizations]="customizations"></manager-view-tree><div *ngIf="moreCustomizationsRequestProcessing" class="se-spinner fd-busy-indicator-extended"><div class="spinner"><fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator></div></div></div></div></personalization-infinite-scrolling><personalizationsmartedit-scroll-zone *ngIf="scrollZoneElement != null && elementToScroll" [isTransparent]="true" [scrollZoneVisible]="scrollZoneVisible" [elementToScroll]="elementToScroll"></personalizationsmartedit-scroll-zone><a class="perso-library__back-to-top fd-link" translate="personalization.commons.button.title.backtotop" [hidden]="!isReturnToTopButtonVisible()" (click)="scrollZoneReturnToTop()"><span class="sap-icon--back-to-top"></span></a></div>`,
        styles: [`.se-component-search--input{margin:0}.perso-library__y-tree .fd-busy-indicator-extended{margin-top:0;margin-bottom:0}`]
    }),
    __metadata("design:paramtypes", [core$1.TranslateService,
        exports.ManagerViewUtilsService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditUtils,
        exports.ManageCustomizationViewManager,
        smarteditcommons.SystemEventService])
], exports.ManagerViewComponent);

window.__smartedit__.addDecoratorPayload("Component", "ManagerViewGridHeader", {
    selector: 'manager-view-grid-header',
    template: `
        <div class="tree-head hidden-sm hidden-xs">
            <div
                class="tree-head__col--lg perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.customization"
                title="{{ 'personalization.modal.manager.grid.header.customization' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs"
                translate="personalization.modal.manager.grid.header.variations"
                title="{{ 'personalization.modal.manager.grid.header.variations' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.components"
                title="{{ 'personalization.modal.manager.grid.header.components' | translate }}"
            ></div>
            <div
                class="tree-head__col--md"
                translate="personalization.modal.manager.grid.header.status"
                title="{{ 'personalization.modal.manager.grid.header.status' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.startdate"
                title="{{ 'personalization.modal.manager.grid.header.startdate' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.enddate"
                title="{{ 'personalization.modal.manager.grid.header.enddate' | translate }}"
            ></div>
        </div>
    `
});
let ManagerViewGridHeader = class ManagerViewGridHeader {
};
ManagerViewGridHeader = __decorate([
    core.Component({
        selector: 'manager-view-grid-header',
        template: `
        <div class="tree-head hidden-sm hidden-xs">
            <div
                class="tree-head__col--lg perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.customization"
                title="{{ 'personalization.modal.manager.grid.header.customization' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs"
                translate="personalization.modal.manager.grid.header.variations"
                title="{{ 'personalization.modal.manager.grid.header.variations' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.components"
                title="{{ 'personalization.modal.manager.grid.header.components' | translate }}"
            ></div>
            <div
                class="tree-head__col--md"
                translate="personalization.modal.manager.grid.header.status"
                title="{{ 'personalization.modal.manager.grid.header.status' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.startdate"
                title="{{ 'personalization.modal.manager.grid.header.startdate' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.enddate"
                title="{{ 'personalization.modal.manager.grid.header.enddate' | translate }}"
            ></div>
        </div>
    `
    })
], ManagerViewGridHeader);

let /* @ngInject */ ManagerViewService = class /* @ngInject */ ManagerViewService {
    constructor(modalService) {
        this.modalService = modalService;
    }
    openManagerAction() {
        this.modalService.open({
            templateConfig: {
                title: 'personalization.modal.manager.title',
                isDismissButtonVisible: true
            },
            config: {
                height: '100%',
                width: '100%',
                dialogPanelClass: 'perso-library',
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            component: exports.ManagerViewComponent
        });
    }
};
/* @ngInject */ ManagerViewService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IModalService])
], /* @ngInject */ ManagerViewService);

var ItemType;
(function (ItemType) {
    ItemType["customization"] = "CUSTOMIZATION";
    ItemType["variation"] = "VARIATION";
})(ItemType || (ItemType = {}));
const PZ_MODAL_MANAGER = 'personalization.modal.manager';
window.__smartedit__.addDecoratorPayload("Component", "ManagerViewTreeComponent", {
    selector: 'manager-view-tree',
    template: `<div cdkDropList [cdkDropListData]="customizations" [id]="treeRoot" [cdkDropListConnectedTo]="dragDroppedTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true"><div *ngFor="let customization of customizations" cdkDrag [cdkDragData]="customization.code" (cdkDragMoved)="dragMoved($event)"><div class="node-item" [attr.data-id]="customization.code" [attr.type]="'CUSTOMIZATION'" [attr.id]=" 'node-' + customization.code"><div class="y-tree-row" [ngClass]="allCustomizationsCollapsed()? 'active-level' : 'inactive-level'"><div class="cdk-drag-row-layout desktop-layout hidden-sm hidden-xs customization-rank-{{customization.rank}}-row"><div (click)="customizationCollapseAction(customization)" class="expand-arrow"><a fd-link title="{{(customization.collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse') | translate}}"><span [ngClass]="customization.collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'" class="perso__toggle-icon"></span></a></div><div class="y-tree-row__angular-ui-tree-handle"><div class="y-tree__col--lg"><div class="perso-wrap-ellipsis perso-tree__primary-data" title="{{customization.name}}" [textContent]="customization.name"></div></div><div class="y-tree__col--xs" [textContent]="getStatusNotDeleted(customization) || 0"></div><div class="y-tree__col--xs"></div><div class="y-tree__col--md perso-tree__status" [ngClass]="getActivityStateForCustomization(customization)"><span [textContent]="getEnablementTextForCustomization(customization)"></span></div><div class="y-tree__col--sm"><div [hidden]="isNotEnabled(customization)" class="perso-tree__dates-layout"><span [textContent]="getFormattedDate(customization.enabledStartDate)" class="perso-library__status-layout"></span></div></div><div class="y-tree__col--sm"><div [hidden]="isNotEnabled(customization)" class="perso-tree__dates-layout"><span [textContent]="getFormattedDate(customization.enabledEndDate)"></span></div></div></div><fd-popover [isOpen]="customization.subMenu" [closeOnOutsideClick]="true" [triggers]="[]"><fd-popover-control><button type="button" class="fd-button--light fd-button" (click)="updateSubMenuAction(customization)" title="{{'personalization.modal.manager.customization.moremenu' | translate}}"><span class="perso__more-icon sap-icon--overflow"></span></button></fd-popover-control><fd-popover-body><div *ngIf="customization.subMenu" class="toolbar-action--include pe-manager-view-tree-more-list__dropdown"><div class="pe-manager-view-tree-more-list__item" (click)="clearAllCustomizationSubMenuAndEditCustomizationAction(customization)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.customization.options.edit"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="deleteCustomizationAction(customization)"><span class="pe-manager-view-tree-more-list__item-deletion" translate="personalization.modal.manager.customization.options.delete"></span></div></div></fd-popover-body></fd-popover></div></div></div><div *ngIf="!customization.collapsed" cdkDropList [cdkDropListData]="customization.variations" [id]="customization.code" [cdkDropListConnectedTo]="dropTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true"><div *ngFor="let variation of customization.variations" cdkDrag [cdkDragData]="variation.code" (cdkDragMoved)="dragMoved($event)" class="y-tree-row child-row active-level"><div class="node-item" [attr.data-id]="variation.code" [attr.type]="'VARIATION'" [attr.id]=" 'node-' + variation.code"><div *ngIf="statusNotDeleted(variation)" class="cdk-drag-row-layout desktop-layout variation-rank-{{variation.rank}}-row hidden-sm hidden-xs"><div class="y-tree-row__angular-ui-tree-handle"><div class="y-tree__col--xl perso-wrap-ellipsis" title="{{variation.name}}"><div><se-tooltip *ngIf="hasCommerceActions(variation)" [triggers]="['mouseenter','mouseleave']"><span class="perso__cc-icon sap-icon--tag perso__cc-icon-padding" [ngClass]="{'perso__cc-icon--hidden': !hasCommerceActions(variation)}" se-tooltip-trigger></span> <span se-tooltip-body [textContent]="getCommerceCustomizationTooltip(variation)"></span></se-tooltip></div><span [textContent]="variation.name"></span></div><div class="y-tree__col--xs" [textContent]="variation.numberOfComponents"></div><div class="y-tree__col--md perso-tree__status" [ngClass]="getActivityStateForVariation(customization,variation)"><span [textContent]="getEnablementTextForVariation(variation)" class="perso-library__status-layout"></span></div><div class="y-tree__col--lg"></div></div><fd-popover [isOpen]="variation.subMenu" [closeOnOutsideClick]="true" [triggers]="[]"><fd-popover-control><button type="button" class="fd-button--light fd-button" (click)="updateSubMenuAction(variation)" title="{{'personalization.modal.manager.customization.moremenu' | translate}}"><span class="perso__more-icon sap-icon--overflow"></span></button></fd-popover-control><fd-popover-body><div *ngIf="variation.subMenu" class="toolbar-action--include pe-manager-view-tree-more-list__dropdown"><div class="pe-manager-view-tree-more-list__item" (click)="clearAllVariationSubMenuAndEditVariationAction(customization, variation)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.variation.options.edit"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="toogleVariationActive(customization,variation)"><span class="pe-manager-view-tree-more-list__item-active" [textContent]="getEnablementActionTextForVariation(variation)"></span></div><div [hidden]="!variation.isCommerceCustomizationEnabled" class="pe-manager-view-tree-more-list__item" (click)="clearAllVariationSubMenuAndmanageCommerceCustomization(customization, variation)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.variation.options.commercecustomization"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="deleteVariationAction(customization, variation, $event)"><span [ngClass]="isDeleteVariationEnabled(customization) ? 'pe-manager-view-tree-more-list__item-deletion' : 'pe-manager-view-tree-more-list__item-inactive' " translate="personalization.modal.manager.variation.options.delete"></span></div></div></fd-popover-body></fd-popover></div></div></div></div></div></div>`,
    styles: [`.cdk-drag-preview{z-index:2000!important}.cdk-drag-row-layout{display:flex}.pe-manager-view-tree-more-list__item{padding:10px 10px;cursor:pointer}.pe-manager-view-tree-more-list__item:first-child:hover{border-top-left-radius:var(--sapElement_BorderCornerRadius);border-top-right-radius:var(--sapElement_BorderCornerRadius)}.pe-manager-view-tree-more-list__item:last-child:hover{border-bottom-left-radius:var(--sapElement_BorderCornerRadius);border-bottom-right-radius:var(--sapElement_BorderCornerRadius)}.pe-manager-view-tree-more-list__item:hover{background-color:var(--sapList_Hover_Background);cursor:pointer}.pe-manager-view-tree-more-list__item-active{color:fd-color("text", 1);cursor:pointer}.pe-manager-view-tree-more-list__item-inactive{color:fd-color("text", 4);cursor:not-allowed}.pe-manager-view-tree-more-list__item-deletion{color:fd-color("status", 3);cursor:pointer}.pe-manager-view-tree-more-list__dropdown{box-shadow:0 6px 12px rgba(0,0,0,.175);background-color:var(--sapBaseColor);border-radius:var(--sapElement_BorderCornerRadius);min-width:100px;width:fit-content;position:absolute;border:1px solid var(--sapGroup_ContentBorderColor);z-index:2000;top:96%;left:initial!important;right:0}.pe-manager-view-tree-more-list__dropdown::before{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapGroup_ContentBorderColor);border-bottom-color:var(var(--sapField_BorderColor));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-8px}.pe-manager-view-tree-more-list__dropdown::after{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapBaseColor);border-bottom-color:var(var(--sapButton_Attention_Hover_Background));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-7px}.pe-manager-view-tree-more-list__dropdown::after,.pe-manager-view-tree-more-list__dropdown::before{right:10px}.pe-manager-view-tree-more-list__dropdown::after,.pe-manager-view-tree-more-list__dropdown::before{display:none}.drop-before{border-top:1px solid #000;z-index:2000!important}.drop-after{border-bottom:1px solid #000;z-index:2000!important}.perso__cc-icon-padding{padding-right:15px}`],
    encapsulation: core.ViewEncapsulation.None
});
let ManagerViewTreeComponent = class ManagerViewTreeComponent {
    constructor(document, managerViewUtilsService, personalizationsmarteditUtils, personalizationsmarteditDateUtils, manageCustomizationViewManager, personalizationsmarteditCommerceCustomizationView, personalizationsmarteditCommerceCustomizationService, personalizationsmarteditContextService, waitDialogService) {
        this.document = document;
        this.managerViewUtilsService = managerViewUtilsService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.personalizationsmarteditDateUtils = personalizationsmarteditDateUtils;
        this.manageCustomizationViewManager = manageCustomizationViewManager;
        this.personalizationsmarteditCommerceCustomizationView = personalizationsmarteditCommerceCustomizationView;
        this.personalizationsmarteditCommerceCustomizationService = personalizationsmarteditCommerceCustomizationService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.waitDialogService = waitDialogService;
        this.treeRoot = 'tree-root';
        this.uncollapsedCustomizations = [];
        this.customizationsPreCount = 0;
        this.dropTargetIds = [];
        this.nodeLookup = {};
        this.dropAction = null;
        this.dragMovedSubject = new rxjs.Subject();
    }
    ngOnDestroy() {
        var _a;
        (_a = this.dragMovedSubscription) === null || _a === void 0 ? void 0 : _a.unsubscribe();
    }
    ngOnInit() {
        this.dragMovedSubscription = this.dragMovedSubject
            .pipe(operators.debounceTime(50))
            .subscribe((event) => {
            this.performDragMoved(event);
        });
    }
    ngDoCheck() {
        if (this.customizationsPreCount !== this.customizations.length) {
            this.updateCustomizations();
            this.resetCustomizationsDragDrop();
            this.updateCustomizationsDragDrop(this.customizations, 0);
            this.customizationsPreCount = this.customizations.length;
        }
    }
    dragMoved(event) {
        this.dragMovedSubject.next(event);
    }
    dragDropped(event) {
        const draggedItemId = event.item.data;
        const dropItemId = this.dropAction.targetId;
        const draggedItemRootId = event.previousContainer.id;
        const dropItemRootId = this.getParentNodeId(dropItemId, this.customizations, this.treeRoot);
        if (!this.dropAction.action ||
            this.nodeLookup[draggedItemId].itemLevel !== this.nodeLookup[dropItemId].itemLevel ||
            draggedItemRootId !== dropItemRootId) {
            return;
        }
        const dropItem = this.getDropItem(this.dropAction.itemType, draggedItemId, dropItemRootId, this.customizations);
        const draggedItem = this.nodeLookup[draggedItemId];
        const oldItemContainer = draggedItemRootId !== this.treeRoot
            ? this.nodeLookup[draggedItemRootId].variations
            : this.customizations;
        const newContainer = dropItemRootId !== this.treeRoot
            ? this.nodeLookup[dropItemRootId].variations
            : this.customizations;
        const i = oldItemContainer.findIndex((c) => c.code === draggedItemId);
        oldItemContainer.splice(i, 1);
        const targetIndex = newContainer.findIndex((c) => c.code === this.dropAction.targetId);
        if (this.dropAction.action === 'before') {
            newContainer.splice(targetIndex, 0, draggedItem);
        }
        else {
            newContainer.splice(targetIndex + 1, 0, draggedItem);
        }
        newContainer.forEach((item, index) => (item.rank = index));
        if (this.dropAction.itemType === ItemType.customization) {
            this.updateChangedCustomization(dropItem, newContainer);
        }
        else if (this.dropAction.itemType === ItemType.variation) {
            const parentCustomization = lodash.cloneDeep(this.nodeLookup[dropItemRootId]);
            this.updateChangedVariation(parentCustomization, dropItem, newContainer);
        }
        this.clearDragInfo(true);
    }
    customizationCollapseAction(customization) {
        this.clearAllCustomizationSubMenu();
        this.clearAllVariationSubMenu();
        customization.collapsed = !customization.collapsed;
        if (customization.collapsed === false) {
            this.uncollapsedCustomizations.push(customization.code);
        }
        else {
            this.uncollapsedCustomizations.splice(this.uncollapsedCustomizations.indexOf(customization.code), 1);
        }
        this.updateCustomizationVariations(customization);
        this.resetCustomizationsDragDrop();
        this.updateCustomizationsDragDrop(this.customizations, 0);
    }
    allCustomizationsCollapsed() {
        return this.customizations
            .map((elem) => elem.collapsed)
            .reduce((previousValue, currentValue) => previousValue && currentValue, true);
    }
    getStatusNotDeleted(customization) {
        return customization.variations.filter((variation) => this.personalizationsmarteditUtils.isItemVisible(variation)).length;
    }
    getActivityStateForCustomization(customization) {
        return this.personalizationsmarteditUtils.getActivityStateForCustomization(customization);
    }
    getEnablementTextForCustomization(customization) {
        return this.personalizationsmarteditUtils.getEnablementTextForCustomization(customization, PZ_MODAL_MANAGER);
    }
    getFormattedDate(myDate) {
        return myDate ? this.personalizationsmarteditDateUtils.formatDate(myDate, null) : '';
    }
    clearAllCustomizationSubMenuAndEditCustomizationAction(customization) {
        this.clearAllCustomizationSubMenu();
        this.editCustomizationAction(customization);
    }
    clearAllVariationSubMenuAndEditVariationAction(customization, variation) {
        this.clearAllVariationSubMenu();
        this.editVariationAction(customization, variation);
    }
    clearAllVariationSubMenuAndmanageCommerceCustomization(customization, variation) {
        this.clearAllVariationSubMenu();
        this.manageCommerceCustomization(customization, variation);
    }
    editCustomizationAction(customization) {
        this.manageCustomizationViewManager.openEditCustomizationModal(customization.code, null);
    }
    isNotEnabled(customization) {
        return customization.status !== personalizationcommons.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }
    deleteCustomizationAction(customization) {
        this.managerViewUtilsService.deleteCustomizationAction(customization, this.customizations);
    }
    updateSubMenuAction(node) {
        if (!node.subMenu) {
            this.clearAllCustomizationSubMenu();
            this.clearAllVariationSubMenu();
        }
        node.subMenu = !node.subMenu;
    }
    clearAllCustomizationSubMenu() {
        for (const customization of this.customizations) {
            customization.subMenu = false;
        }
    }
    clearAllVariationSubMenu() {
        for (const customization of this.customizations) {
            for (const variation of customization.variations) {
                variation.subMenu = false;
            }
        }
    }
    hasCommerceActions(variation) {
        return this.personalizationsmarteditUtils.hasCommerceActions(variation);
    }
    getCommerceCustomizationTooltip(variation) {
        return this.personalizationsmarteditUtils.getCommerceCustomizationTooltip(variation, '', '');
    }
    getActivityStateForVariation(customization, variation) {
        return this.personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
    }
    getEnablementTextForVariation(variation) {
        return this.personalizationsmarteditUtils.getEnablementTextForVariation(variation, PZ_MODAL_MANAGER);
    }
    editVariationAction(customization, variation) {
        this.manageCustomizationViewManager.openEditCustomizationModal(customization.code, variation.code);
    }
    toogleVariationActive(customization, variation) {
        this.managerViewUtilsService.toggleVariationActive(customization, variation);
    }
    getEnablementActionTextForVariation(variation) {
        return this.personalizationsmarteditUtils.getEnablementActionTextForVariation(variation, PZ_MODAL_MANAGER);
    }
    manageCommerceCustomization(customization, variation) {
        this.personalizationsmarteditCommerceCustomizationView.openCommerceCustomizationAction(customization, variation);
    }
    isDeleteVariationEnabled(customization) {
        return (this.personalizationsmarteditUtils.getVisibleItems(customization.variations).length > 1);
    }
    deleteVariationAction(customization, variation, $event) {
        if (this.isDeleteVariationEnabled(customization)) {
            this.clearAllVariationSubMenu();
            this.managerViewUtilsService.deleteVariationAction(customization, variation);
        }
        else {
            $event.stopPropagation();
        }
    }
    statusNotDeleted(variation) {
        return this.personalizationsmarteditUtils.isItemVisible(variation);
    }
    performDragMoved(event) {
        this.clearAllCustomizationSubMenu();
        this.clearAllVariationSubMenu();
        const ele = this.document.elementFromPoint(event.pointerPosition.x, event.pointerPosition.y);
        if (!ele) {
            this.clearDragInfo();
            return;
        }
        const container = ele.classList.contains('node-item')
            ? ele
            : ele.closest('.node-item');
        if (!container) {
            this.clearDragInfo();
            return;
        }
        this.dropAction = {
            targetId: container.getAttribute('data-id')
        };
        this.dropAction.itemType = container.getAttribute('type');
        const targetRect = container.getBoundingClientRect();
        const oneHalf = targetRect.height / 2;
        if (event.pointerPosition.y - targetRect.top < oneHalf) {
            this.dropAction.action = 'before';
        }
        else if (event.pointerPosition.y - targetRect.top > oneHalf) {
            this.dropAction.action = 'after';
        }
        this.showDragInfo();
    }
    resetCustomizationsDragDrop() {
        this.dropTargetIds.length = 0;
        this.nodeLookup = {};
    }
    updateCustomizationsDragDrop(nodes, itemLevel) {
        nodes.forEach((node) => {
            this.dropTargetIds.push(node.code);
            this.nodeLookup[node.code] = node;
            this.nodeLookup[node.code].itemLevel = itemLevel;
            if (node.variations) {
                this.updateCustomizationsDragDrop(node.variations, itemLevel + 1);
            }
        });
    }
    getParentNodeId(id, nodesToSearch, parentId) {
        for (const node of nodesToSearch) {
            if (node.code === id) {
                return parentId;
            }
            if (node.variations) {
                const ret = this.getParentNodeId(id, node.variations, node.code);
                if (ret) {
                    return ret;
                }
            }
        }
        return null;
    }
    updateCustomizations() {
        this.customizations.forEach((customization) => {
            if (this.uncollapsedCustomizations.includes(customization.code)) {
                customization.collapsed = false;
                this.updateCustomizationVariations(customization);
            }
            else {
                customization.collapsed = true;
            }
        });
    }
    showDragInfo() {
        this.clearDragInfo();
        if (this.dropAction) {
            this.document
                .getElementById('node-' + this.dropAction.targetId)
                .classList.add('drop-' + this.dropAction.action);
        }
    }
    clearDragInfo(dropped = false) {
        if (dropped) {
            this.dropAction = null;
        }
        this.document
            .querySelectorAll('.drop-before')
            .forEach((element) => element.classList.remove('drop-before'));
        this.document
            .querySelectorAll('.drop-after')
            .forEach((element) => element.classList.remove('drop-after'));
    }
    isCommerceCustomizationEnabled() {
        return this.personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(this.personalizationsmarteditContextService.getSeData().seConfigurationData);
    }
    updateCustomizationVariations(customization) {
        this.managerViewUtilsService.customizationClickAction(customization);
        const isCommerceCustomizationEnabled = this.isCommerceCustomizationEnabled();
        customization.variations.forEach((variation) => {
            variation.statusNotDeleted = this.statusNotDeleted(variation);
            variation.isCommerceCustomizationEnabled = isCommerceCustomizationEnabled;
        });
    }
    updateChangedVariation(customization, dropItem, updatedVariations) {
        return __awaiter(this, void 0, void 0, function* () {
            const droppedItemDest = updatedVariations.filter((elem) => elem.code === dropItem.code)[0].rank;
            const increasedIndex = droppedItemDest - dropItem.rank;
            if (increasedIndex !== 0) {
                this.waitDialogService.showWaitModal();
                yield this.updateVariationRank(customization, dropItem.code, increasedIndex);
                this.waitDialogService.hideWaitModal();
            }
        });
    }
    updateChangedCustomization(dropItem, updatedCustomizations) {
        return __awaiter(this, void 0, void 0, function* () {
            const droppedItemDest = updatedCustomizations.filter((elem) => elem.code === dropItem.code)[0].rank;
            const increasedIndex = droppedItemDest - dropItem.rank;
            if (increasedIndex !== 0) {
                this.waitDialogService.showWaitModal();
                yield this.updateCustomizationRank(dropItem.code, increasedIndex);
                this.waitDialogService.hideWaitModal();
            }
        });
    }
    updateCustomizationRank(customizationCode, increaseValue) {
        return this.managerViewUtilsService.updateCustomizationRank(customizationCode, increaseValue);
    }
    updateVariationRank(customization, variationCode, increaseValue) {
        return this.managerViewUtilsService.updateVariationRank(customization, variationCode, increaseValue);
    }
    getDropItem(itemType, draggedItemId, targetListId, itemList) {
        if (itemType === ItemType.customization) {
            return lodash.cloneDeep(itemList.filter((elem) => elem.code === draggedItemId)[0]);
        }
        else if (itemType === ItemType.variation) {
            const customization = itemList.filter((elem) => elem.code === targetListId)[0];
            return lodash.cloneDeep(customization.variations.filter((elem) => elem.code === draggedItemId)[0]);
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ManagerViewTreeComponent.prototype, "customizations", void 0);
ManagerViewTreeComponent = __decorate([
    core.Component({
        selector: 'manager-view-tree',
        template: `<div cdkDropList [cdkDropListData]="customizations" [id]="treeRoot" [cdkDropListConnectedTo]="dragDroppedTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true"><div *ngFor="let customization of customizations" cdkDrag [cdkDragData]="customization.code" (cdkDragMoved)="dragMoved($event)"><div class="node-item" [attr.data-id]="customization.code" [attr.type]="'CUSTOMIZATION'" [attr.id]=" 'node-' + customization.code"><div class="y-tree-row" [ngClass]="allCustomizationsCollapsed()? 'active-level' : 'inactive-level'"><div class="cdk-drag-row-layout desktop-layout hidden-sm hidden-xs customization-rank-{{customization.rank}}-row"><div (click)="customizationCollapseAction(customization)" class="expand-arrow"><a fd-link title="{{(customization.collapsed ? 'personalization.commons.icon.title.expand' : 'personalization.commons.icon.title.collapse') | translate}}"><span [ngClass]="customization.collapsed ? 'sap-icon--navigation-right-arrow' : 'sap-icon--navigation-down-arrow'" class="perso__toggle-icon"></span></a></div><div class="y-tree-row__angular-ui-tree-handle"><div class="y-tree__col--lg"><div class="perso-wrap-ellipsis perso-tree__primary-data" title="{{customization.name}}" [textContent]="customization.name"></div></div><div class="y-tree__col--xs" [textContent]="getStatusNotDeleted(customization) || 0"></div><div class="y-tree__col--xs"></div><div class="y-tree__col--md perso-tree__status" [ngClass]="getActivityStateForCustomization(customization)"><span [textContent]="getEnablementTextForCustomization(customization)"></span></div><div class="y-tree__col--sm"><div [hidden]="isNotEnabled(customization)" class="perso-tree__dates-layout"><span [textContent]="getFormattedDate(customization.enabledStartDate)" class="perso-library__status-layout"></span></div></div><div class="y-tree__col--sm"><div [hidden]="isNotEnabled(customization)" class="perso-tree__dates-layout"><span [textContent]="getFormattedDate(customization.enabledEndDate)"></span></div></div></div><fd-popover [isOpen]="customization.subMenu" [closeOnOutsideClick]="true" [triggers]="[]"><fd-popover-control><button type="button" class="fd-button--light fd-button" (click)="updateSubMenuAction(customization)" title="{{'personalization.modal.manager.customization.moremenu' | translate}}"><span class="perso__more-icon sap-icon--overflow"></span></button></fd-popover-control><fd-popover-body><div *ngIf="customization.subMenu" class="toolbar-action--include pe-manager-view-tree-more-list__dropdown"><div class="pe-manager-view-tree-more-list__item" (click)="clearAllCustomizationSubMenuAndEditCustomizationAction(customization)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.customization.options.edit"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="deleteCustomizationAction(customization)"><span class="pe-manager-view-tree-more-list__item-deletion" translate="personalization.modal.manager.customization.options.delete"></span></div></div></fd-popover-body></fd-popover></div></div></div><div *ngIf="!customization.collapsed" cdkDropList [cdkDropListData]="customization.variations" [id]="customization.code" [cdkDropListConnectedTo]="dropTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true"><div *ngFor="let variation of customization.variations" cdkDrag [cdkDragData]="variation.code" (cdkDragMoved)="dragMoved($event)" class="y-tree-row child-row active-level"><div class="node-item" [attr.data-id]="variation.code" [attr.type]="'VARIATION'" [attr.id]=" 'node-' + variation.code"><div *ngIf="statusNotDeleted(variation)" class="cdk-drag-row-layout desktop-layout variation-rank-{{variation.rank}}-row hidden-sm hidden-xs"><div class="y-tree-row__angular-ui-tree-handle"><div class="y-tree__col--xl perso-wrap-ellipsis" title="{{variation.name}}"><div><se-tooltip *ngIf="hasCommerceActions(variation)" [triggers]="['mouseenter','mouseleave']"><span class="perso__cc-icon sap-icon--tag perso__cc-icon-padding" [ngClass]="{'perso__cc-icon--hidden': !hasCommerceActions(variation)}" se-tooltip-trigger></span> <span se-tooltip-body [textContent]="getCommerceCustomizationTooltip(variation)"></span></se-tooltip></div><span [textContent]="variation.name"></span></div><div class="y-tree__col--xs" [textContent]="variation.numberOfComponents"></div><div class="y-tree__col--md perso-tree__status" [ngClass]="getActivityStateForVariation(customization,variation)"><span [textContent]="getEnablementTextForVariation(variation)" class="perso-library__status-layout"></span></div><div class="y-tree__col--lg"></div></div><fd-popover [isOpen]="variation.subMenu" [closeOnOutsideClick]="true" [triggers]="[]"><fd-popover-control><button type="button" class="fd-button--light fd-button" (click)="updateSubMenuAction(variation)" title="{{'personalization.modal.manager.customization.moremenu' | translate}}"><span class="perso__more-icon sap-icon--overflow"></span></button></fd-popover-control><fd-popover-body><div *ngIf="variation.subMenu" class="toolbar-action--include pe-manager-view-tree-more-list__dropdown"><div class="pe-manager-view-tree-more-list__item" (click)="clearAllVariationSubMenuAndEditVariationAction(customization, variation)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.variation.options.edit"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="toogleVariationActive(customization,variation)"><span class="pe-manager-view-tree-more-list__item-active" [textContent]="getEnablementActionTextForVariation(variation)"></span></div><div [hidden]="!variation.isCommerceCustomizationEnabled" class="pe-manager-view-tree-more-list__item" (click)="clearAllVariationSubMenuAndmanageCommerceCustomization(customization, variation)"><span class="pe-manager-view-tree-more-list__item-active" translate="personalization.modal.manager.variation.options.commercecustomization"></span></div><div class="pe-manager-view-tree-more-list__item" (click)="deleteVariationAction(customization, variation, $event)"><span [ngClass]="isDeleteVariationEnabled(customization) ? 'pe-manager-view-tree-more-list__item-deletion' : 'pe-manager-view-tree-more-list__item-inactive' " translate="personalization.modal.manager.variation.options.delete"></span></div></div></fd-popover-body></fd-popover></div></div></div></div></div></div>`,
        styles: [`.cdk-drag-preview{z-index:2000!important}.cdk-drag-row-layout{display:flex}.pe-manager-view-tree-more-list__item{padding:10px 10px;cursor:pointer}.pe-manager-view-tree-more-list__item:first-child:hover{border-top-left-radius:var(--sapElement_BorderCornerRadius);border-top-right-radius:var(--sapElement_BorderCornerRadius)}.pe-manager-view-tree-more-list__item:last-child:hover{border-bottom-left-radius:var(--sapElement_BorderCornerRadius);border-bottom-right-radius:var(--sapElement_BorderCornerRadius)}.pe-manager-view-tree-more-list__item:hover{background-color:var(--sapList_Hover_Background);cursor:pointer}.pe-manager-view-tree-more-list__item-active{color:fd-color("text", 1);cursor:pointer}.pe-manager-view-tree-more-list__item-inactive{color:fd-color("text", 4);cursor:not-allowed}.pe-manager-view-tree-more-list__item-deletion{color:fd-color("status", 3);cursor:pointer}.pe-manager-view-tree-more-list__dropdown{box-shadow:0 6px 12px rgba(0,0,0,.175);background-color:var(--sapBaseColor);border-radius:var(--sapElement_BorderCornerRadius);min-width:100px;width:fit-content;position:absolute;border:1px solid var(--sapGroup_ContentBorderColor);z-index:2000;top:96%;left:initial!important;right:0}.pe-manager-view-tree-more-list__dropdown::before{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapGroup_ContentBorderColor);border-bottom-color:var(var(--sapField_BorderColor));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-8px}.pe-manager-view-tree-more-list__dropdown::after{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapBaseColor);border-bottom-color:var(var(--sapButton_Attention_Hover_Background));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-7px}.pe-manager-view-tree-more-list__dropdown::after,.pe-manager-view-tree-more-list__dropdown::before{right:10px}.pe-manager-view-tree-more-list__dropdown::after,.pe-manager-view-tree-more-list__dropdown::before{display:none}.drop-before{border-top:1px solid #000;z-index:2000!important}.drop-after{border-bottom:1px solid #000;z-index:2000!important}.perso__cc-icon-padding{padding-right:15px}`],
        encapsulation: core.ViewEncapsulation.None
    }),
    __param(0, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Document,
        exports.ManagerViewUtilsService,
        personalizationcommons.PersonalizationsmarteditUtils,
        personalizationcommons.PersonalizationsmarteditDateUtils,
        exports.ManageCustomizationViewManager,
        exports.PersonalizationsmarteditCommerceCustomizationView,
        personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
        exports.PersonalizationsmarteditContextService,
        smarteditcommons.IWaitDialogService])
], ManagerViewTreeComponent);

let ManagerViewModule = class ManagerViewModule {
};
ManagerViewModule = __decorate([
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            common.CommonModule,
            smarteditcommons.SelectModule,
            smarteditcommons.TooltipModule,
            personalizationcommons.ScrollZoneModule,
            forms.FormsModule,
            form.FormModule,
            dragDrop.DragDropModule,
            core$2.PopoverModule,
            busyIndicator.BusyIndicatorModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            PersonalizationsmarteditSharedComponentsModule
        ],
        declarations: [exports.ManagerViewComponent, ManagerViewGridHeader, ManagerViewTreeComponent],
        entryComponents: [exports.ManagerViewComponent, ManagerViewGridHeader, ManagerViewTreeComponent],
        providers: [exports.ManagerViewUtilsService, ManagerViewService]
    })
], ManagerViewModule);

window.__smartedit__.addDecoratorPayload("Component", "CombinedViewToolbarContextComponent", {
    selector: 'combined-view-toolbar-context',
    template: `<div *ngIf="visible" class="pe-toolbar-item-context"><span class="sap-icon--slim-arrow-right pe-toolbar-item-context__icon"></span><div class="pe-toolbar-item-context__btn"><div class="pe-toolbar-item-context__btn-txt"><div title="{{title}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-title">{{title}}</div><div title="{{subtitle}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-subtitle">{{subtitle}}</div></div></div><div class="pe-toolbar-item-context__btn-hyicon" (click)="clear()"><span class="sap-icon--decline"></span></div></div>`
});
let CombinedViewToolbarContextComponent = class CombinedViewToolbarContextComponent {
    constructor(combinedViewCommonsService, personalizationsmarteditContextService, personalizationsmarteditContextUtils, crossFrameEventService) {
        this.combinedViewCommonsService = combinedViewCommonsService;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        this.crossFrameEventService = crossFrameEventService;
    }
    ngOnInit() {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        this.visible = false;
        this.selectedCustomization = lodash__namespace.cloneDeep(combinedView.customize.selectedCustomization);
        if (this.selectedCustomization) {
            this.title = combinedView.customize.selectedCustomization.name;
            this.subtitle = combinedView.customize.selectedVariations.name;
            this.visible = true;
        }
        this.oldSelectedCustomization = combinedView.customize.selectedCustomization;
        this.oldSelectedCustomizationEnabled = combinedView.enabled;
    }
    ngDoCheck() {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const selectedCustomization = combinedView.customize.selectedCustomization;
        if (selectedCustomization && selectedCustomization !== this.oldSelectedCustomization) {
            this.oldSelectedCustomization = selectedCustomization;
            this.title = selectedCustomization.name;
            this.subtitle = combinedView.customize.selectedVariations.name;
            this.visible = true;
            this.crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY);
        }
        else if (!selectedCustomization) {
            this.visible = false;
            this.crossFrameEventService.publish(smarteditcommons.HIDE_TOOLBAR_ITEM_CONTEXT, personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY);
        }
        const selectedCustomizationEnabled = combinedView.enabled;
        if (selectedCustomizationEnabled &&
            selectedCustomizationEnabled !== this.oldSelectedCustomizationEnabled) {
            this.oldSelectedCustomizationEnabled = selectedCustomizationEnabled;
            this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(this.personalizationsmarteditContextService);
        }
    }
    clear() {
        this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(this.personalizationsmarteditContextService);
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const variations = [];
        (combinedView.selectedItems || []).forEach((item) => {
            variations.push({
                customizationCode: item.customization.code,
                variationCode: item.variation.code,
                catalog: item.variation.catalog,
                catalogVersion: item.variation.catalogVersion
            });
        });
        this.combinedViewCommonsService.updatePreview(variations);
        this.crossFrameEventService.publish(smarteditcommons.HIDE_TOOLBAR_ITEM_CONTEXT, personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY);
    }
};
CombinedViewToolbarContextComponent = __decorate([
    core.Component({
        selector: 'combined-view-toolbar-context',
        template: `<div *ngIf="visible" class="pe-toolbar-item-context"><span class="sap-icon--slim-arrow-right pe-toolbar-item-context__icon"></span><div class="pe-toolbar-item-context__btn"><div class="pe-toolbar-item-context__btn-txt"><div title="{{title}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-title">{{title}}</div><div title="{{subtitle}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-subtitle">{{subtitle}}</div></div></div><div class="pe-toolbar-item-context__btn-hyicon" (click)="clear()"><span class="sap-icon--decline"></span></div></div>`
    }),
    __metadata("design:paramtypes", [exports.CombinedViewCommonsService,
        exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditContextUtils,
        smarteditcommons.CrossFrameEventService])
], CombinedViewToolbarContextComponent);

window.__smartedit__.addDecoratorPayload("Component", "CustomizeToolbarContextComponent", {
    selector: 'personalizationsmartedit-customize-toolbar-context',
    template: `<div *ngIf="visible" class="pe-toolbar-item-context"><span class="sap-icon--slim-arrow-right pe-toolbar-item-context__icon"></span><div class="pe-toolbar-item-context__btn"><div class="pe-toolbar-item-context__btn-txt"><div title="{{title}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-title">{{title}}</div><div title="{{subtitle}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-subtitle">{{subtitle}}</div></div></div><div class="pe-toolbar-item-context__btn-hyicon" (click)="clear()"><span class="sap-icon--decline"></span></div></div>`
});
let CustomizeToolbarContextComponent = class CustomizeToolbarContextComponent {
    constructor(personalizationsmarteditContextService, personalizationsmarteditContextUtils, personalizationsmarteditPreviewService, crossFrameEventService) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        this.personalizationsmarteditPreviewService = personalizationsmarteditPreviewService;
        this.crossFrameEventService = crossFrameEventService;
    }
    ngOnInit() {
        this.selectedCustomization = lodash__namespace.cloneDeep(this.personalizationsmarteditContextService.getCustomize().selectedCustomization);
        this.selectedVariations = lodash__namespace.cloneDeep(this.personalizationsmarteditContextService.getCustomize().selectedVariations);
        this.visible = false;
        if (this.selectedCustomization) {
            this.title =
                this.personalizationsmarteditContextService.getCustomize().selectedCustomization.name;
            this.visible = true;
            if (!lodash__namespace.isArray(this.selectedVariations)) {
                this.subtitle = this.selectedVariations.name;
            }
        }
        this.oldSelectedCustomization =
            this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
        this.oldSelectedVariations =
            this.personalizationsmarteditContextService.getCustomize().selectedVariations;
    }
    ngDoCheck() {
        const selectedCustomization = this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
        if (selectedCustomization && selectedCustomization !== this.oldSelectedCustomization) {
            this.oldSelectedCustomization = selectedCustomization;
            this.title = selectedCustomization.name;
            this.visible = true;
            this.crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
        }
        else if (!selectedCustomization) {
            this.visible = false;
            this.crossFrameEventService.publish(smarteditcommons.HIDE_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
        }
        const selectedVariations = this.personalizationsmarteditContextService.getCustomize().selectedVariations;
        if (selectedVariations && selectedVariations !== this.oldSelectedVariations) {
            this.oldSelectedVariations = selectedVariations;
            if (!lodash__namespace.isArray(selectedVariations)) {
                this.subtitle = selectedVariations.name;
            }
        }
    }
    clear() {
        this.personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(this.personalizationsmarteditPreviewService, this.personalizationsmarteditContextService);
        this.crossFrameEventService.publish(smarteditcommons.HIDE_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
    }
};
CustomizeToolbarContextComponent = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-customize-toolbar-context',
        template: `<div *ngIf="visible" class="pe-toolbar-item-context"><span class="sap-icon--slim-arrow-right pe-toolbar-item-context__icon"></span><div class="pe-toolbar-item-context__btn"><div class="pe-toolbar-item-context__btn-txt"><div title="{{title}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-title">{{title}}</div><div title="{{subtitle}}" class="perso-wrap-ellipsis pe-toolbar-item-context__btn-subtitle">{{subtitle}}</div></div></div><div class="pe-toolbar-item-context__btn-hyicon" (click)="clear()"><span class="sap-icon--decline"></span></div></div>`
    }),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditContextUtils,
        exports.PersonalizationsmarteditPreviewService,
        smarteditcommons.CrossFrameEventService])
], CustomizeToolbarContextComponent);

window.__smartedit__.addDecoratorPayload("Component", "ManageCustomizationViewMenuComponent", {
    selector: 'manage-customization-view-menu',
    template: `<div class="btn-block pe-toolbar-action--include"><div class="se-toolbar-menu-content se-toolbar-menu-content--pe-customized" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2" translate="personalization.toolbar.library.header.title"></h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.library.header.description"></span></se-help></div><div class="se-toolbar-menu-content--pe-customized__item"><a class="se-toolbar-menu-content--pe-customized__item__link fd-link" id="personalizationsmartedit-pagecustomizations-toolbar-customization-anchor" translate="personalization.toolbar.library.manager.name" (click)="managerViewClick()"></a></div><div class="se-toolbar-menu-content--pe-customized__item se-toolbar-menu-content--pe-customized__item--last"><a class="se-toolbar-menu-content--pe-customized__item__link fd-link" id="personalizationsmartedit-pagecustomizations-toolbar-customization-anchor" translate="personalization.toolbar.library.customizationvariationmanagement.name" (click)="createCustomizationClick()"></a></div></div></div>`,
    styles: [
        `
            .se-toolbar-menu-content--pe-customized__item__link {
                color: var(--sapLinkColor);
                cursor: pointer;
            }
        `
    ],
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let ManageCustomizationViewMenuComponent = class ManageCustomizationViewMenuComponent {
    constructor(personalizationsmarteditContextService, personalizationsmarteditContextUtils, personalizationsmarteditPreviewService, manageCustomizationViewManager, personalizationsmarteditManagerView, toolbarItem) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        this.personalizationsmarteditPreviewService = personalizationsmarteditPreviewService;
        this.manageCustomizationViewManager = manageCustomizationViewManager;
        this.personalizationsmarteditManagerView = personalizationsmarteditManagerView;
        this.toolbarItem = toolbarItem;
    }
    createCustomizationClick() {
        this.manageCustomizationViewManager.openCreateCustomizationModal();
        this.toolbarItem.isOpen = false;
    }
    managerViewClick() {
        this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(this.personalizationsmarteditContextService);
        this.personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(this.personalizationsmarteditPreviewService, this.personalizationsmarteditContextService);
        this.personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(this.personalizationsmarteditPreviewService, this.personalizationsmarteditContextService);
        this.personalizationsmarteditManagerView.openManagerAction();
        this.toolbarItem.isOpen = false;
    }
};
ManageCustomizationViewMenuComponent = __decorate([
    core.Component({
        selector: 'manage-customization-view-menu',
        template: `<div class="btn-block pe-toolbar-action--include"><div class="se-toolbar-menu-content se-toolbar-menu-content--pe-customized" role="menu"><div class="se-toolbar-menu-content--pe-customized__headers"><h2 class="se-toolbar-menu-content--pe-customized__headers--h2" translate="personalization.toolbar.library.header.title"></h2><se-help class="se-toolbar-menu-content__y-help"><span translate="personalization.toolbar.library.header.description"></span></se-help></div><div class="se-toolbar-menu-content--pe-customized__item"><a class="se-toolbar-menu-content--pe-customized__item__link fd-link" id="personalizationsmartedit-pagecustomizations-toolbar-customization-anchor" translate="personalization.toolbar.library.manager.name" (click)="managerViewClick()"></a></div><div class="se-toolbar-menu-content--pe-customized__item se-toolbar-menu-content--pe-customized__item--last"><a class="se-toolbar-menu-content--pe-customized__item__link fd-link" id="personalizationsmartedit-pagecustomizations-toolbar-customization-anchor" translate="personalization.toolbar.library.customizationvariationmanagement.name" (click)="createCustomizationClick()"></a></div></div></div>`,
        styles: [
            `
            .se-toolbar-menu-content--pe-customized__item__link {
                color: var(--sapLinkColor);
                cursor: pointer;
            }
        `
        ],
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(5, core.Inject(smarteditcommons.TOOLBAR_ITEM)),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditContextUtils,
        exports.PersonalizationsmarteditPreviewService,
        exports.ManageCustomizationViewManager,
        ManagerViewService, Object])
], ManageCustomizationViewMenuComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
let SePersonalizationsmarteditToolbarContextModule = class SePersonalizationsmarteditToolbarContextModule {
};
SePersonalizationsmarteditToolbarContextModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.HelpModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            exports.SePersonalizationsmarteditServicesModule,
            smarteditcommons.TranslationModule.forChild(),
            smarteditcontainer.ToolbarModule
        ],
        declarations: [
            CustomizeToolbarContextComponent,
            ManageCustomizationViewMenuComponent,
            CombinedViewToolbarContextComponent
        ],
        entryComponents: [
            CustomizeToolbarContextComponent,
            ManageCustomizationViewMenuComponent,
            CombinedViewToolbarContextComponent
        ],
        providers: [ManagerViewService]
    })
], SePersonalizationsmarteditToolbarContextModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const SE_EDIT_PAGE = 'se.edit.page';
exports.PersonalizationsmarteditContainerModule = class PersonalizationsmarteditContainerModule {
};
exports.PersonalizationsmarteditContainerModule = __decorate([
    smarteditcommons.SeEntryModule('personalizationsmarteditcontainer'),
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            platformBrowser.BrowserModule,
            personalizationcommons.ScrollZoneModule,
            link.LinkModule,
            exports.ManageCustomizationViewModule,
            exports.CombinedViewModule,
            PersonalizationsmarteditDataFactoryModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            PersonalizationsmarteditContextMenuModule,
            PersonalizationsmarteditSharedComponentsModule,
            exports.SePersonalizationsmarteditServicesModule,
            SePersonalizationsmarteditToolbarContextModule,
            exports.SePersonalizationsmarteditCustomizeViewModule,
            PersonalizationsmarteditCommerceCustomizationModule,
            ManagerViewModule,
            PersonalizationsmarteditSegmentViewModule
        ],
        providers: [
            {
                provide: http.HTTP_INTERCEPTORS,
                useClass: personalizationcommons.BaseSiteHeaderInterceptor,
                multi: true,
                deps: [smarteditcommons.ISharedDataService]
            },
            {
                provide: personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy,
                useClass: exports.PersonalizationsmarteditContextMenuServiceProxy
            },
            smarteditcommons.moduleUtils.bootstrap((experienceService, crossFrameEventService, smarteditBootstrapGateway, perspectiveService, featureService, systemEventService, personalizationsmarteditUtils, personalizationsmarteditContextService, personalizationsmarteditMessageHandler, personalizationsmarteditContextUtils, personalizationsmarteditPreviewService, 
            // needs to be initialized here because it's required to work but it's not imported by any component/service in personalizationsmartedit container app
            personalizationsmarteditContextMenuServiceProxy, personalizationsmarteditContextServiceReverseProxy) => {
                const PERSONALIZATION_PERSPECTIVE_KEY = 'personalizationsmartedit.perspective';
                const clearAllContextsAndReloadPreview = () => {
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
                    personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
                    personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
                };
                const clearAllContexts = () => {
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
                    personalizationsmarteditContextUtils.clearCustomizeContext(personalizationsmarteditContextService);
                    personalizationsmarteditContextUtils.clearCombinedViewContext(personalizationsmarteditContextService);
                };
                crossFrameEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_UNLOADING, (eventId, unloadingPerspective) => {
                    if (unloadingPerspective === PERSONALIZATION_PERSPECTIVE_KEY) {
                        clearAllContextsAndReloadPreview();
                    }
                });
                systemEventService.subscribe(smarteditcommons.EVENTS.EXPERIENCE_UPDATE, () => {
                    clearAllContexts();
                    personalizationsmarteditContextService.setCustomizeFiltersState({
                        catalogFilter: personalizationcommons.PERSONALIZATION_CATALOG_FILTER.CURRENT,
                        pageFilter: personalizationcommons.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE,
                        statusFilter: personalizationcommons.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
                        nameFilter: ''
                    });
                    return smarteditcommons.promiseUtils.promise;
                });
                systemEventService.subscribe(smarteditcommons.EVENT_PERSPECTIVE_ADDED, () => {
                    personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
                    return smarteditcommons.promiseUtils.promise;
                });
                systemEventService.subscribe(smarteditcommons.SWITCH_LANGUAGE_EVENT, () => {
                    const combinedView = personalizationsmarteditContextService.getCombinedView();
                    lodash.forEach(combinedView.selectedItems, function (item) {
                        personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(item.variation);
                    });
                    personalizationsmarteditContextService.setCombinedView(combinedView);
                    return smarteditcommons.promiseUtils.promise;
                });
                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.pagecustomizations',
                    priority: 4,
                    section: 'left',
                    component: exports.CustomizeViewComponent,
                    contextComponent: CustomizeToolbarContextComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--palette se-toolbar-menu-ddlb--button__icon',
                    permissions: [SE_EDIT_PAGE, 'se.personalization.page']
                });
                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: 'personalizationsmartedit.container.manager.toolbar',
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.library.name',
                    priority: 8,
                    section: 'left',
                    component: ManageCustomizationViewMenuComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--bbyd-active-sales se-toolbar-menu-ddlb--button__icon',
                    permissions: [SE_EDIT_PAGE]
                });
                featureService.register({
                    key: 'personalizationsmartedit.context.service',
                    nameI18nKey: 'personalization.context.service.name',
                    descriptionI18nKey: 'personalization.context.service.description',
                    enablingCallback: () => {
                        const personalization = personalizationsmarteditContextService.getPersonalization();
                        personalization.enabled = true;
                        personalizationsmarteditContextService.setPersonalization(personalization);
                    },
                    disablingCallback: () => {
                        const personalization = personalizationsmarteditContextService.getPersonalization();
                        personalization.enabled = false;
                        personalizationsmarteditContextService.setPersonalization(personalization);
                    },
                    permissions: [SE_EDIT_PAGE]
                });
                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY,
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.combinedview.name',
                    priority: 6,
                    section: 'left',
                    component: exports.CombinedViewMenuComponent,
                    contextComponent: CombinedViewToolbarContextComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--switch-views se-toolbar-menu-ddlb--button__icon',
                    permissions: ['se.read.page', 'se.personalization.page']
                });
                perspectiveService.register({
                    key: PERSONALIZATION_PERSPECTIVE_KEY,
                    nameI18nKey: 'personalization.perspective.name',
                    features: [
                        'personalizationsmartedit.context.service',
                        personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
                        'personalizationsmartedit.container.manager.toolbar',
                        personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY,
                        'personalizationsmarteditSharedSlot',
                        'personalizationsmarteditComponentLightUp',
                        'personalizationsmarteditCombinedViewComponentLightUp',
                        'personalizationsmartedit.context.add.action',
                        'personalizationsmartedit.context.edit.action',
                        'personalizationsmartedit.context.delete.action',
                        'personalizationsmartedit.context.info.action',
                        'personalizationsmartedit.context.component.edit.action',
                        'personalizationsmartedit.context.show.action.list',
                        'externalcomponentbutton',
                        'se.contextualMenu',
                        'se.slotContextualMenu',
                        'se.emptySlotFix',
                        'se.cms.pageDisplayStatus'
                    ],
                    perspectives: [],
                    permissions: ['se.personalization.open']
                });
                smarteditBootstrapGateway.subscribe('smartEditReady', (eventId, data) => {
                    crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY);
                    crossFrameEventService.publish(smarteditcommons.SHOW_TOOLBAR_ITEM_CONTEXT, personalizationcommons.COMBINED_VIEW_TOOLBAR_ITEM_KEY);
                    const customize = personalizationsmarteditContextService.getCustomize().selectedCustomization;
                    const combinedView = personalizationsmarteditContextService.getCombinedView().customize
                        .selectedCustomization;
                    const combinedViewCustomize = personalizationsmarteditContextService.getCombinedView().selectedItems;
                    experienceService.getCurrentExperience().then((experience) => {
                        if (!experience.variations &&
                            (customize || combinedView || combinedViewCustomize)) {
                            clearAllContexts();
                        }
                    });
                    personalizationsmarteditContextService
                        .refreshExperienceData()
                        .then(() => {
                        const experience = personalizationsmarteditContextService.getSeData().seExperienceData;
                        const activePerspective = perspectiveService.getActivePerspective() || {
                            key: '',
                            nameI18nKey: '',
                            features: []
                        };
                        if (activePerspective.key === PERSONALIZATION_PERSPECTIVE_KEY &&
                            experience.pageContext.catalogVersionUuid !==
                                experience.catalogDescriptor.catalogVersionUuid) {
                            const warningConf = {
                                message: 'personalization.warning.pagefromparent',
                                timeout: -1
                            };
                            personalizationsmarteditMessageHandler.sendWarning(warningConf);
                        }
                    })
                        .finally(() => {
                        personalizationsmarteditContextService.applySynchronization();
                    });
                });
            }, [
                smarteditcommons.IExperienceService,
                smarteditcommons.CrossFrameEventService,
                smarteditcommons.SmarteditBootstrapGateway,
                smarteditcommons.IPerspectiveService,
                smarteditcommons.IFeatureService,
                smarteditcommons.SystemEventService,
                personalizationcommons.PersonalizationsmarteditUtils,
                exports.PersonalizationsmarteditContextService,
                personalizationcommons.PersonalizationsmarteditMessageHandler,
                personalizationcommons.PersonalizationsmarteditContextUtils,
                exports.PersonalizationsmarteditPreviewService,
                personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy,
                exports.PersonalizationsmarteditContextServiceReverseProxy
            ])
        ]
    })
], exports.PersonalizationsmarteditContainerModule);

exports.defaultCustomization = defaultCustomization;
