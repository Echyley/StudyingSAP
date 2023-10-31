'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var http = require('@angular/common/http');
var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var personalizationcommons = require('personalizationcommons');
var smarteditcommons = require('smarteditcommons');
var lodash = require('lodash');
var core$1 = require('@ngx-translate/core');
var common = require('@angular/common');

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
const PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
const PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
const ACTIONS_DETAILS = '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions';
const PAGES_CONTENT_SLOT_RESOURCE_URI = `/cmswebservices/v1/sites/${smarteditcommons.PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslots`;

let /* @ngInject */ PersonalizationsmarteditComponentHandlerService = class /* @ngInject */ PersonalizationsmarteditComponentHandlerService {
    constructor(restServiceFactory, crossFrameEventService, yjQuery, componentHandlerService, pageInfoService) {
        this.crossFrameEventService = crossFrameEventService;
        this.yjQuery = yjQuery;
        this.componentHandlerService = componentHandlerService;
        this.pageInfoService = pageInfoService;
        this.resource = restServiceFactory.get(PAGES_CONTENT_SLOT_RESOURCE_URI);
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, () => this.reloadPageContentSlots());
    }
    getParentContainerForComponent(component) {
        const parent = component.closest(`[${smarteditcommons.CONTAINER_TYPE_ATTRIBUTE}=${personalizationcommons.COMPONENT_CONTAINER_TYPE}]`);
        return parent;
    }
    getParentContainerIdForComponent(component) {
        const parent = component.closest(`[${smarteditcommons.CONTAINER_TYPE_ATTRIBUTE}=${personalizationcommons.COMPONENT_CONTAINER_TYPE}]`);
        return parent.attr(smarteditcommons.CONTAINER_ID_ATTRIBUTE);
    }
    getPageContentSlots() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.pageContentSlots) {
                yield this.reloadPageContentSlots();
            }
            return this.pageContentSlots;
        });
    }
    getParentContainerSourceIdForComponent(component) {
        const parent = component.closest(`[${smarteditcommons.CONTAINER_TYPE_ATTRIBUTE}=${personalizationcommons.COMPONENT_CONTAINER_TYPE}]`);
        return parent.attr(personalizationcommons.CONTAINER_SOURCE_ID_ATTR);
    }
    getParentSlotForComponent(component) {
        const parent = component.closest(`[${smarteditcommons.TYPE_ATTRIBUTE}=${smarteditcommons.CONTENT_SLOT_TYPE}]`);
        return parent;
    }
    getParentSlotIdForComponent(component) {
        return this.componentHandlerService.getParentSlotForComponent(component);
    }
    getOriginalComponent(componentId, componentType) {
        return this.componentHandlerService.getOriginalComponent(componentId, componentType);
    }
    isExternalComponent(componentId, componentType) {
        return this.componentHandlerService.isExternalComponent(componentId, componentType);
    }
    getCatalogVersionUuid(component) {
        return this.componentHandlerService.getCatalogVersionUuid(component);
    }
    getAllSlotsSelector() {
        return this.componentHandlerService.getAllSlotsSelector();
    }
    // Since implementation class for JQueryStatic does not support constructor with multiple dynamic parameter well
    // Here we split into different sub methods to avoid build warning
    getFromJQuerySelector(selector) {
        return this.yjQuery(selector);
    }
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
    getContainerSourceIdForContainerId(containerId) {
        let containerSelector = this.getAllSlotsSelector();
        containerSelector += ' [' + smarteditcommons.CONTAINER_ID_ATTRIBUTE + '="' + containerId + '"]'; // space at beginning is important
        const container = this.getFromSelector(containerSelector);
        return container[0] ? container[0].getAttribute(personalizationcommons.CONTAINER_SOURCE_ID_ATTR) : '';
    }
    isSlotShared(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.getPageContentSlots();
            const matchedSlotData = lodash.first(this.pageContentSlots.filter((pageContentSlot) => pageContentSlot.slotId === slotId));
            return matchedSlotData && matchedSlotData.slotShared;
        });
    }
    reloadPageContentSlots() {
        return __awaiter(this, void 0, void 0, function* () {
            const pageId = yield this.pageInfoService.getPageUID();
            const pageContent = yield this.resource.get({ pageId });
            this.pageContentSlots = lodash.uniq(pageContent.pageContentSlotList || []);
        });
    }
};
/* @ngInject */ PersonalizationsmarteditComponentHandlerService = __decorate([
    core.Injectable(),
    __param(2, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        smarteditcommons.CrossFrameEventService, Function, smarteditcommons.IComponentHandlerService,
        smarteditcommons.IPageInfoService])
], /* @ngInject */ PersonalizationsmarteditComponentHandlerService);

let /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy = class /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy {
    applySynchronization() {
        'proxyFunction';
        return undefined;
    }
    isCurrentPageActiveWorkflowRunning() {
        'proxyFunction';
        return undefined;
    }
};
/* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy = __decorate([
    smarteditcommons.GatewayProxied('applySynchronization', 'isCurrentPageActiveWorkflowRunning'),
    core.Injectable()
], /* @ngInject */ PersonalizationsmarteditContextServiceReverseProxy);

let /* @ngInject */ PersonalizationsmarteditContextService = class /* @ngInject */ PersonalizationsmarteditContextService {
    constructor(yjQuery, contextualMenuService, personalizationsmarteditContextServiceReverseProxy, personalizationsmarteditContextUtils) {
        this.yjQuery = yjQuery;
        this.contextualMenuService = contextualMenuService;
        this.personalizationsmarteditContextServiceReverseProxy = personalizationsmarteditContextServiceReverseProxy;
        this.personalizationsmarteditContextUtils = personalizationsmarteditContextUtils;
        const context = personalizationsmarteditContextUtils.getContextObject();
        this.setPersonalization(context.personalization);
        try {
            const combinedView = window.sessionStorage.getItem('CombinedView');
            const customize = window.sessionStorage.getItem('Customize');
            this.setCustomize(JSON.parse(customize) || context.customize);
            this.setCombinedView(JSON.parse(combinedView) || context.combinedView);
        }
        catch (e) {
            this.setCustomize(context.customize);
            this.setCombinedView(context.combinedView);
        }
        this.setSeData(context.seData);
    }
    getPersonalization() {
        return this.personalization;
    }
    setPersonalization(personalization) {
        this.personalization = personalization;
        this.contextualMenuService.refreshMenuItems();
    }
    getCustomize() {
        return this.customize;
    }
    setCustomize(customize) {
        this.customize = customize;
        this.contextualMenuService.refreshMenuItems();
    }
    getCombinedView() {
        return this.combinedView;
    }
    setCombinedView(combinedView) {
        this.combinedView = combinedView;
        this.contextualMenuService.refreshMenuItems();
    }
    getSeData() {
        return this.seData;
    }
    setSeData(seData) {
        this.seData = seData;
    }
    isCurrentPageActiveWorkflowRunning() {
        return this.personalizationsmarteditContextServiceReverseProxy.isCurrentPageActiveWorkflowRunning();
    }
};
/* @ngInject */ PersonalizationsmarteditContextService = __decorate([
    core.Injectable(),
    __param(0, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function, smarteditcommons.IContextualMenuService,
        PersonalizationsmarteditContextServiceReverseProxy,
        personalizationcommons.PersonalizationsmarteditContextUtils])
], /* @ngInject */ PersonalizationsmarteditContextService);

let /* @ngInject */ PersonalizationsmarteditContextServiceProxy = class /* @ngInject */ PersonalizationsmarteditContextServiceProxy extends personalizationcommons.IPersonalizationsmarteditContextServiceProxy {
    constructor(personalizationsmarteditContextService, crossFrameEventService) {
        super();
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.crossFrameEventService = crossFrameEventService;
    }
    setPersonalization(newPersonalization) {
        this.personalizationsmarteditContextService.setPersonalization(newPersonalization);
    }
    setCustomize(newCustomize) {
        this.personalizationsmarteditContextService.setCustomize(newCustomize);
        this.crossFrameEventService.publish('PERSONALIZATION_CUSTOMIZE_CONTEXT_SYNCHRONIZED');
    }
    setCombinedView(newCombinedView) {
        this.personalizationsmarteditContextService.setCombinedView(newCombinedView);
        this.crossFrameEventService.publish('PERSONALIZATION_COMBINEDVIEW_CONTEXT_SYNCHRONIZED');
    }
    setSeData(newSeData) {
        this.personalizationsmarteditContextService.setSeData(newSeData);
    }
};
/* @ngInject */ PersonalizationsmarteditContextServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData'),
    core.Injectable(),
    __metadata("design:paramtypes", [PersonalizationsmarteditContextService,
        smarteditcommons.CrossFrameEventService])
], /* @ngInject */ PersonalizationsmarteditContextServiceProxy);

var /* @ngInject */ PersonalizationsmarteditContextualMenuService_1;
let /* @ngInject */ PersonalizationsmarteditContextualMenuService = /* @ngInject */ PersonalizationsmarteditContextualMenuService_1 = class /* @ngInject */ PersonalizationsmarteditContextualMenuService {
    constructor(personalizationsmarteditContextService, personalizationsmarteditComponentHandlerService, personalizationsmarteditUtils, personalizationsmarteditContextMenuServiceProxy, crossFrameEventService) {
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditComponentHandlerService = personalizationsmarteditComponentHandlerService;
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.personalizationsmarteditContextMenuServiceProxy = personalizationsmarteditContextMenuServiceProxy;
        this.crossFrameEventService = crossFrameEventService;
        this.isWorkflowRunningBoolean = false;
        this.init();
    }
    updateWorkflowStatus() {
        return __awaiter(this, void 0, void 0, function* () {
            this.isWorkflowRunningBoolean =
                yield this.personalizationsmarteditContextService.isCurrentPageActiveWorkflowRunning();
        });
    }
    openDeleteAction(config) {
        const configProperties = lodash.isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {};
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode =
            configProperties.smarteditPersonalizationVariationId || null;
        configurationToPass.selectedCustomizationCode =
            configProperties.smarteditPersonalizationCustomizationId || null;
        const componentCatalog = configProperties.smarteditCatalogVersionUuid.split('/');
        configurationToPass.componentCatalog = componentCatalog[0];
        configurationToPass.componentCatalogVersion = componentCatalog[1];
        const contextCustomization = this.getSelectedCustomization(configurationToPass.selectedCustomizationCode);
        configurationToPass.catalog = contextCustomization.catalog;
        configurationToPass.catalogVersion = contextCustomization.catalogVersion;
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(configProperties.smarteditContainerSourceId);
        this.personalizationsmarteditContextMenuServiceProxy.openDeleteAction(configurationToPass);
    }
    openAddAction(config) {
        const configProperties = lodash.isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {};
        configurationToPass.componentType = config.componentType;
        configurationToPass.componentId = config.componentId;
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode = this.getSelectedVariationCode();
        const componentCatalog = configProperties.smarteditCatalogVersionUuid.split('/');
        configurationToPass.componentCatalog = componentCatalog[0];
        const contextCustomization = this.getSelectedCustomization();
        configurationToPass.catalog = contextCustomization.catalog;
        configurationToPass.selectedCustomizationCode = contextCustomization.code;
        const slot = this.personalizationsmarteditComponentHandlerService.getParentSlotForComponent(config.element);
        const slotCatalog = this.personalizationsmarteditComponentHandlerService
            .getCatalogVersionUuid(slot)
            .split('/');
        configurationToPass.slotCatalog = slotCatalog[0];
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(configProperties.smarteditContainerSourceId);
        configurationToPass.slotsToRefresh.push(config.slotId);
        return this.personalizationsmarteditContextMenuServiceProxy.openAddAction(configurationToPass);
    }
    openEditAction(config) {
        const configProperties = lodash.isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {};
        configurationToPass.componentType = config.componentType;
        configurationToPass.componentId = config.componentId;
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode =
            configProperties.smarteditPersonalizationVariationId || null;
        configurationToPass.selectedCustomizationCode =
            configProperties.smarteditPersonalizationCustomizationId || null;
        configurationToPass.componentUuid = configProperties.smarteditComponentUuid || null;
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(configProperties.smarteditContainerSourceId);
        return this.personalizationsmarteditContextMenuServiceProxy.openEditAction(configurationToPass);
    }
    openEditComponentAction(config) {
        const configProperties = lodash.isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {};
        configurationToPass.smarteditComponentType = configProperties.smarteditComponentType;
        configurationToPass.smarteditComponentUuid = configProperties.smarteditComponentUuid;
        configurationToPass.smarteditCatalogVersionUuid =
            configProperties.smarteditCatalogVersionUuid;
        return this.personalizationsmarteditContextMenuServiceProxy.openEditComponentAction(configurationToPass);
    }
    isCustomizationFromCurrentCatalog(config) {
        const items = this.contextCombinedView.selectedItems || [];
        const foundItems = items.filter((item) => item.customization.code ===
            config.componentAttributes.smarteditPersonalizationCustomizationId &&
            item.variation.code ===
                config.componentAttributes.smarteditPersonalizationVariationId);
        const foundItem = foundItems.shift();
        if (foundItem) {
            return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(foundItem.customization, this.personalizationsmarteditContextService.getSeData());
        }
        return false;
    }
    isPersonalizationAllowedInWorkflow() {
        this.refreshContext();
        return this.isEditPersonalizationInWorkflowAllowed(this.contextPersonalization.enabled);
    }
    isContextualMenuAddItemEnabled(config) {
        this.refreshContext();
        let isEnabled = this.isContextualMenuEnabled();
        isEnabled = isEnabled && !this.isElementHighlighted(config);
        isEnabled = isEnabled && this.isSlotInCurrentCatalog(config);
        isEnabled = isEnabled && this.isSelectedCustomizationFromCurrentCatalog();
        return this.isEditPersonalizationInWorkflowAllowed(isEnabled);
    }
    isContextualMenuEditItemEnabled(config) {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled =
            isEnabled && !lodash.isUndefined(config.componentAttributes.smarteditPersonalizationActionId);
        isEnabled = isEnabled && this.isSlotInCurrentCatalog(config);
        isEnabled =
            isEnabled &&
                (this.isSelectedCustomizationFromCurrentCatalog() ||
                    this.isCustomizationFromCurrentCatalog(config));
        return this.isEditPersonalizationInWorkflowAllowed(isEnabled);
    }
    isContextualMenuDeleteItemEnabled(config) {
        return this.isContextualMenuEditItemEnabled(config);
    }
    isContextualMenuShowActionListEnabled(config) {
        this.refreshContext();
        let isEnabled = !lodash.isUndefined(config.componentAttributes.smarteditPersonalizationActionId);
        isEnabled = isEnabled && this.contextCombinedView.enabled;
        isEnabled = isEnabled && !this.contextCombinedView.customize.selectedCustomization;
        return isEnabled;
    }
    isContextualMenuInfoEnabled() {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled = isEnabled && !lodash.isObject(this.contextCustomize.selectedVariations);
        isEnabled = isEnabled || lodash.isArray(this.contextCustomize.selectedVariations);
        isEnabled = isEnabled && !this.contextCombinedView.enabled;
        return isEnabled;
    }
    isContextualMenuInfoItemEnabled() {
        const isEnabled = this.isContextualMenuInfoEnabled();
        return (isEnabled ||
            !this.isEditPersonalizationInWorkflowAllowed(this.contextPersonalization.enabled));
    }
    isContextualMenuEditComponentItemEnabled(config) {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled =
            isEnabled &&
                !this.contextCombinedView.enabled &&
                this.isComponentInCurrentCatalog(config);
        return isEnabled;
    }
    isEditPersonalizationInWorkflowAllowed(condition) {
        const seConfigurationData = this.personalizationsmarteditContextService.getSeData().seConfigurationData || [];
        const isEditPersonalizationInWorkflowPropertyEnabled = seConfigurationData[/* @ngInject */ PersonalizationsmarteditContextualMenuService_1.EDIT_PERSONALIZATION_IN_WORKFLOW] === true;
        if (isEditPersonalizationInWorkflowPropertyEnabled) {
            return condition;
        }
        else {
            return condition && !this.isWorkflowRunningBoolean;
        }
    }
    isCustomizeObjectValid(customize) {
        return (lodash.isObject(customize.selectedCustomization) &&
            lodash.isObject(customize.selectedVariations) &&
            !lodash.isArray(customize.selectedVariations));
    }
    isContextualMenuEnabled() {
        return (this.isCustomizeObjectValid(this.contextCustomize) ||
            (this.contextCombinedView.enabled &&
                this.isCustomizeObjectValid(this.contextCombinedView.customize)));
    }
    isElementHighlighted(config) {
        if (this.contextCombinedView.enabled) {
            return this.contextCombinedView.customize.selectedComponents.includes(config.componentAttributes.smarteditContainerSourceId);
        }
        else {
            return this.contextCustomize.selectedComponents.includes(config.componentAttributes.smarteditContainerSourceId);
        }
    }
    isSlotInCurrentCatalog(config) {
        const slot = this.personalizationsmarteditComponentHandlerService.getParentSlotForComponent(config.element);
        const catalogUuid = this.personalizationsmarteditComponentHandlerService.getCatalogVersionUuid(slot);
        const experienceCV = this.contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
        return experienceCV === catalogUuid;
    }
    isComponentInCurrentCatalog(config) {
        const experienceCV = this.contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
        const componentCV = config.componentAttributes.smarteditCatalogVersionUuid;
        return experienceCV === componentCV;
    }
    isSelectedCustomizationFromCurrentCatalog() {
        const customization = this.contextCustomize.selectedCustomization ||
            this.contextCombinedView.customize.selectedCustomization;
        if (customization) {
            return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, this.personalizationsmarteditContextService.getSeData());
        }
        return false;
    }
    init() {
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, () => {
            this.updateWorkflowStatus();
        });
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_UPDATED, () => {
            this.updateWorkflowStatus();
        });
    }
    refreshContext() {
        this.contextPersonalization =
            this.personalizationsmarteditContextService.getPersonalization();
        this.contextCustomize = this.personalizationsmarteditContextService.getCustomize();
        this.contextCombinedView = this.personalizationsmarteditContextService.getCombinedView();
        this.contextSeData = this.personalizationsmarteditContextService.getSeData();
    }
    getSelectedVariationCode() {
        if (this.personalizationsmarteditContextService.getCombinedView().enabled) {
            return this.personalizationsmarteditContextService.getCombinedView().customize
                .selectedVariations.code;
        }
        return this.personalizationsmarteditContextService.getCustomize()
            .selectedVariations.code;
    }
    // return type should be Customization but it's in container
    getSelectedCustomization(customizationCode) {
        if (this.personalizationsmarteditContextService.getCombinedView().enabled) {
            let customization = this.personalizationsmarteditContextService.getCombinedView().customize
                .selectedCustomization;
            if (!customization && customizationCode) {
                customization = this.personalizationsmarteditContextService
                    .getCombinedView()
                    .selectedItems.filter((elem) => elem.customization.code === customizationCode)[0].customization;
            }
            return customization;
        }
        return this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
    }
    getSlotsToRefresh(containerSourceId) {
        let slotsSelector = this.personalizationsmarteditComponentHandlerService.getAllSlotsSelector();
        slotsSelector += ' [data-smartedit-container-source-id="' + containerSourceId + '"]'; // space at beginning is important
        const slots = this.personalizationsmarteditComponentHandlerService.getFromSelector(slotsSelector);
        const slotIds = Array.prototype.slice.call(lodash.map(slots, (el) => this.personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(this.personalizationsmarteditComponentHandlerService.getFromSelector(el))));
        return slotIds;
    }
};
/* @ngInject */ PersonalizationsmarteditContextualMenuService.EDIT_PERSONALIZATION_IN_WORKFLOW = 'personalizationsmartedit.editPersonalizationInWorkflow.enabled';
/* @ngInject */ PersonalizationsmarteditContextualMenuService = /* @ngInject */ PersonalizationsmarteditContextualMenuService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [PersonalizationsmarteditContextService,
        PersonalizationsmarteditComponentHandlerService,
        personalizationcommons.PersonalizationsmarteditUtils,
        personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy,
        smarteditcommons.CrossFrameEventService])
], /* @ngInject */ PersonalizationsmarteditContextualMenuService);

let /* @ngInject */ PersonalizationsmarteditCustomizeViewHelper = class /* @ngInject */ PersonalizationsmarteditCustomizeViewHelper {
    constructor(personalizationsmarteditComponentHandlerService) {
        this.personalizationsmarteditComponentHandlerService = personalizationsmarteditComponentHandlerService;
    }
    getSourceContainersInfo() {
        let slotsSelector = this.personalizationsmarteditComponentHandlerService.getAllSlotsSelector();
        slotsSelector += ' [data-smartedit-container-source-id]'; // space at beginning is important
        const slots = this.personalizationsmarteditComponentHandlerService.getFromSelector(slotsSelector);
        const slotIds = slots.map((key, val) => {
            const component = this.personalizationsmarteditComponentHandlerService.getFromJQuerySelector(val);
            return {
                containerId: this.personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(component),
                containerSourceId: this.personalizationsmarteditComponentHandlerService.getParentContainerSourceIdForComponent(component)
            };
        });
        return lodash.countBy(slotIds, 'containerSourceId');
    }
};
/* @ngInject */ PersonalizationsmarteditCustomizeViewHelper = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [PersonalizationsmarteditComponentHandlerService])
], /* @ngInject */ PersonalizationsmarteditCustomizeViewHelper);

let /* @ngInject */ PersonalizationsmarteditRestService = class /* @ngInject */ PersonalizationsmarteditRestService {
    constructor(restServiceFactory, personalizationsmarteditContextService) {
        this.restServiceFactory = restServiceFactory;
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
    }
    getCxCmsAllActionsForContainer(containerId, filter) {
        const restService = this.restServiceFactory.get(ACTIONS_DETAILS);
        let requestParams = {
            type: 'CXCMSACTION',
            customizationStatus: 'ENABLED',
            variationStatus: 'ENABLED',
            catalogs: 'ALL',
            needsTotal: true,
            containerId,
            pageSize: (filter === null || filter === void 0 ? void 0 : filter.currentSize) || 25,
            currentPage: (filter === null || filter === void 0 ? void 0 : filter.currentPage) || 0
        };
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
        return restService.get(requestParams);
    }
    extendRequestParamObjWithCatalogAwarePathVariables(requestParam) {
        const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const catalogAwareParams = {
            catalogId: experienceData.catalogDescriptor.catalogId,
            catalogVersion: experienceData.catalogDescriptor.catalogVersion
        };
        const requestParamExtended = Object.assign(Object.assign({}, requestParam), catalogAwareParams);
        return requestParamExtended;
    }
};
/* @ngInject */ PersonalizationsmarteditRestService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        PersonalizationsmarteditContextService])
], /* @ngInject */ PersonalizationsmarteditRestService);

let servicesModule = class servicesModule {
};
servicesModule = __decorate([
    core.NgModule({
        providers: [
            PersonalizationsmarteditContextService,
            PersonalizationsmarteditRestService,
            PersonalizationsmarteditCustomizeViewHelper,
            PersonalizationsmarteditContextualMenuService,
            PersonalizationsmarteditComponentHandlerService,
            PersonalizationsmarteditContextServiceReverseProxy,
            {
                provide: personalizationcommons.IPersonalizationsmarteditContextServiceProxy,
                useClass: PersonalizationsmarteditContextServiceProxy
            },
            smarteditcommons.moduleUtils.initialize((contextualMenuService) => contextualMenuService.updateWorkflowStatus(), [PersonalizationsmarteditContextualMenuService])
        ]
    })
], servicesModule);

window.__smartedit__.addDecoratorPayload("Component", "PersonalizationsmarteditCombinedViewComponentLightUpDecorator", {
    selector: 'personalizationsmartedit-combined-view-component-light-up',
    template: `<div [ngClass]="classForElement" class="pe-combined-view-component-letter">{{ letterForElement }}</div><div><ng-content></ng-content></div>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let PersonalizationsmarteditCombinedViewComponentLightUpDecorator = class PersonalizationsmarteditCombinedViewComponentLightUpDecorator extends smarteditcommons.AbstractDecorator {
    constructor(personalizationsmarteditContextService, crossFrameEventService, cdr, yjQuery, element) {
        super();
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.crossFrameEventService = crossFrameEventService;
        this.cdr = cdr;
        this.$element = yjQuery(element.nativeElement);
        this.letterForElement = '';
        this.classForElement = '';
    }
    ngOnInit() {
        this.allBorderClassess = Object.values(personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING)
            .map(({ borderClass }) => borderClass)
            .join(' ');
        this.unSubscribeRegOverlayRerender = this.crossFrameEventService.subscribe(smarteditcommons.OVERLAY_RERENDERED_EVENT, () => {
            this.calculate();
        });
        this.calculate();
    }
    ngOnDestroy() {
        this.unSubscribeRegOverlayRerender();
    }
    calculate() {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        if (!combinedView.enabled) {
            return;
        }
        const container = this.$element
            .parent()
            .closest('[class~="smartEditComponentX"][data-smartedit-container-source-id][data-smartedit-container-type="CxCmsComponentContainer"][data-smartedit-personalization-customization-id][data-smartedit-personalization-variation-id]');
        if (container.length === 0) {
            return;
        }
        container.removeClass(this.allBorderClassess);
        (combinedView.selectedItems || []).forEach((element, index) => {
            let state = container.data().smarteditPersonalizationCustomizationId ===
                element.customization.code;
            state =
                state &&
                    container.data().smarteditPersonalizationVariationId === element.variation.code;
            if (state) {
                const wrappedIndex = index % Object.keys(personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
                container.addClass(personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].borderClass);
                this.letterForElement = String.fromCharCode('a'.charCodeAt(0) + wrappedIndex).toUpperCase();
                this.classForElement =
                    personalizationcommons.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
                this.cdr.detectChanges();
            }
        });
    }
};
PersonalizationsmarteditCombinedViewComponentLightUpDecorator = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-combined-view-component-light-up',
        template: `<div [ngClass]="classForElement" class="pe-combined-view-component-letter">{{ letterForElement }}</div><div><ng-content></ng-content></div>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(3, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [PersonalizationsmarteditContextService,
        smarteditcommons.CrossFrameEventService,
        core.ChangeDetectorRef, Function, core.ElementRef])
], PersonalizationsmarteditCombinedViewComponentLightUpDecorator);

window.__smartedit__.addDecoratorPayload("Component", "PersonalizationsmarteditComponentLightUpDecorator", {
    selector: 'personalizationsmartedit-component-light-up',
    template: `<div><ng-content></ng-content></div>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let PersonalizationsmarteditComponentLightUpDecorator = class PersonalizationsmarteditComponentLightUpDecorator extends smarteditcommons.AbstractDecorator {
    constructor(personalizationsmarteditContextService, personalizationsmarteditComponentHandlerService, crossFrameEventService, element, yjQuery) {
        super();
        this.personalizationsmarteditContextService = personalizationsmarteditContextService;
        this.personalizationsmarteditComponentHandlerService = personalizationsmarteditComponentHandlerService;
        this.crossFrameEventService = crossFrameEventService;
        this.CONTAINER_TYPE = 'CxCmsComponentContainer';
        this.ACTION_ID_ATTR = 'data-smartedit-personalization-action-id';
        this.$element = yjQuery(element.nativeElement);
        this.PARENT_CONTAINER_SELECTOR = `[class~="${smarteditcommons.OVERLAY_COMPONENT_CLASS}"][${personalizationcommons.CONTAINER_SOURCE_ID_ATTR}][${smarteditcommons.CONTAINER_TYPE_ATTRIBUTE}="${this.CONTAINER_TYPE}"]`;
        this.PARENT_CONTAINER_WITH_ACTION_SELECTOR = `[class~="${smarteditcommons.OVERLAY_COMPONENT_CLASS}"][${smarteditcommons.CONTAINER_TYPE_ATTRIBUTE}="${this.CONTAINER_TYPE}"][${this.ACTION_ID_ATTR}]`;
        this.COMPONENT_SELECTOR = `[${smarteditcommons.ID_ATTRIBUTE}][${smarteditcommons.CATALOG_VERSION_UUID_ATTRIBUTE}][${smarteditcommons.TYPE_ATTRIBUTE}]`;
    }
    ngAfterViewInit() {
        this.unRegisterCustomizeContextSynchronized = this.crossFrameEventService.subscribe('PERSONALIZATION_CUSTOMIZE_CONTEXT_SYNCHRONIZED', () => this.delayToggleCssClasses());
        this.delayToggleCssClasses();
    }
    ngOnDestroy() {
        this.unRegisterCustomizeContextSynchronized();
    }
    /**
     * There is an issue that the DOM instance of $element has been created but it has not been appended to the DOM yet and hence it is not possible to access its parent element.
     * By using timeout we ensure that the element has been already appended to DOM.
     */
    delayToggleCssClasses() {
        setTimeout(() => {
            this.toggleCssClasses();
        }, 500);
    }
    toggleCssClasses() {
        const component = this.$element.parent().closest(this.COMPONENT_SELECTOR); // personalizationsmartedit-combined-view-component-light-up
        const container = component.closest(this.PARENT_CONTAINER_SELECTOR); // smartedit-element
        const isVariationComponentSelected = this.isVariationComponentSelected(component);
        // when Customization has been selected, a border displays on customized components
        container.toggleClass('perso__component-decorator', isVariationComponentSelected);
        // when Customization has been selected, checked icon is displayed in top left corner of the decorator
        container.toggleClass('hyicon hyicon-checkedlg perso__component-decorator-icon', isVariationComponentSelected);
        // when Customization is clicked in, a border displays on customized components
        container.toggleClass('personalizationsmarteditComponentSelected', this.isComponentSelected());
    }
    isVariationComponentSelected(component) {
        let isSelected = false;
        const customize = this.personalizationsmarteditContextService.getCustomize();
        if (customize.selectedCustomization && customize.selectedVariations) {
            const container = component.closest(this.PARENT_CONTAINER_WITH_ACTION_SELECTOR);
            isSelected = container.length > 0;
        }
        return isSelected;
    }
    isComponentSelected() {
        if (!Array.isArray(this.personalizationsmarteditContextService.getCustomize().selectedVariations)) {
            return false;
        }
        const containerId = this.personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(this.$element);
        const selectedComponents = this.personalizationsmarteditContextService.getCustomize().selectedComponents;
        return !!(selectedComponents === null || selectedComponents === void 0 ? void 0 : selectedComponents.includes(containerId));
    }
};
PersonalizationsmarteditComponentLightUpDecorator = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-component-light-up',
        template: `<div><ng-content></ng-content></div>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(4, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [PersonalizationsmarteditContextService,
        PersonalizationsmarteditComponentHandlerService,
        smarteditcommons.CrossFrameEventService,
        core.ElementRef, Function])
], PersonalizationsmarteditComponentLightUpDecorator);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
let /* @ngInject */ PersonalizationsmarteditContextMenuServiceProxy = class /* @ngInject */ PersonalizationsmarteditContextMenuServiceProxy extends personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy {
    constructor() {
        super();
    }
};
/* @ngInject */ PersonalizationsmarteditContextMenuServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('openDeleteAction', 'openAddAction', 'openEditAction', 'openEditComponentAction'),
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ PersonalizationsmarteditContextMenuServiceProxy);

window.__smartedit__.addDecoratorPayload("Component", "ShowComponentInfoListComponent", {
    selector: 'perso-show-component-info-list',
    template: `<div class="pe-component-info"><div class="pe-component-info__info-layout"><div *ngIf="!isPageBlocked"><div *ngIf="!isPersonalizationAllowedInWorkflow"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow"></div></div><div *ngIf="isContextualMenuInfoEnabled"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.title"></div><div class="perso__page-level-help-message" *ngIf="!isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow.componentinfo"></div><div class="perso__page-level-help-message" *ngIf="pagination.totalCount > 0 && isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.label"></div><div class="perso__page-level-help-message" *ngIf="pagination.totalCount === 0 && isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.nocustomizations"></div></div></div><div *ngIf="isPageBlocked"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.blocked.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showcomponentinfolist.blocked.label"></div></div></div><div class="perso__page-level-help-message pe-component-info__info-layout" *ngIf="pagination.totalCount > 0 && isContextualMenuInfoEnabled"><span translate="personalization.modal.showcomponentinfolist.help.numberofenabledcustomizations"></span> <span>{{ pagination.totalCount }}</span></div><div [hidden]="pagination.totalCount === 0 || !isContextualMenuInfoEnabled"><personalization-infinite-scrolling [fetchPage]="fetchActionsPage" [dropDownContainerClass]="'pe-component-info__wrapper'"><personalization-prevent-parent-scroll><div [ngClass]="isCustomizationVisible ? 'pe-component-info__list-item' : '' " *ngFor="let action of actions"><div class="pe-component-info__names-layout"><div class="perso-wrap-ellipsis" [title]="action.customizationName">{{ action.customizationName }}</div><div class="perso-wrap-ellipsis perso-tree__primary-data" [title]="action.variationName">{{ action.variationName }}</div></div><div [ngClass]="isCustomizationVisible ? 'pe-component-info__icon' : '' "><se-tooltip *ngIf="!action.customization.isFromCurrentCatalog" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger [ngClass]="isCustomizationVisible ? 'perso__globe-icon sap-icon--globe' : '' "></span> <span se-tooltip-body>{{ action.customization.catalogVersionNameL10N | seL10n | async }}</span></se-tooltip></div></div><div class="pe-spinner--inner" *ngIf="moreCustomizationsRequestProcessing"><div class="spinner-md spinner-light"></div></div></personalization-prevent-parent-scroll></personalization-infinite-scrolling></div></div>`
});
let ShowComponentInfoListComponent = class ShowComponentInfoListComponent {
    constructor(item, persoComponentHandlerService, persoContextService, persoContextualMenuService, persoMessageHandler, persoRestService, persoUtils, permissionService, translateService, cdr) {
        this.persoComponentHandlerService = persoComponentHandlerService;
        this.persoContextService = persoContextService;
        this.persoContextualMenuService = persoContextualMenuService;
        this.persoMessageHandler = persoMessageHandler;
        this.persoRestService = persoRestService;
        this.persoUtils = persoUtils;
        this.permissionService = permissionService;
        this.translateService = translateService;
        this.cdr = cdr;
        ({
            componentAttributes: { smarteditContainerId: this.containerId }
        } = item);
        this.pagination = new personalizationcommons.PaginationHelper({});
        this.pagination.reset();
        this.isPageBlocked = false;
        this.isContextualMenuInfoEnabled = false;
        this.isPersonalizationAllowedInWorkflow = false;
        this.moreCustomizationsRequestProcessing = false;
        const pageSize = 25;
        this.customizationsFilter = {
            currentSize: pageSize,
            currentPage: this.pagination.getPage() + 1
        };
        this.actions = [];
        this.isCustomizationVisible = false;
        this.fetchActionsPage = () => this.fetchMoreActions();
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.containerIdExists = !!this.containerId;
            this.containerSourceId = this.containerIdExists
                ? this.persoComponentHandlerService.getContainerSourceIdForContainerId(this.containerId)
                : '';
            this.setCustomizationVisible();
            this.isPageBlocked = yield this.isPersonalizationBlockedOnPage();
            this.isPersonalizationAllowedInWorkflow =
                this.persoContextualMenuService.isPersonalizationAllowedInWorkflow();
            this.isContextualMenuInfoEnabled =
                this.persoContextualMenuService.isContextualMenuInfoEnabled();
            this.cdr.detectChanges();
        });
    }
    fetchMoreActions() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.pagination.isLastPage() &&
                !this.moreCustomizationsRequestProcessing &&
                this.containerIdExists) {
                this.moreCustomizationsRequestProcessing = true;
                const customizationsFilter = this.setCustomizationsFilterForNextPage();
                yield this.fetchAllActionsForContainerId(this.containerSourceId, customizationsFilter);
            }
        });
    }
    isPersonalizationBlockedOnPage() {
        return __awaiter(this, void 0, void 0, function* () {
            const isPermitted = yield this.permissionService.isPermitted([
                { names: ['se.personalization.page'] }
            ]);
            return !isPermitted;
        });
    }
    setCustomizationsFilterForNextPage() {
        const nextPage = this.pagination.getPage() + 1;
        this.customizationsFilter.currentPage = nextPage;
        return this.customizationsFilter;
    }
    fetchAllActionsForContainerId(containerId, filter) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.persoRestService.getCxCmsAllActionsForContainer(containerId, filter);
                const actionsWithCustomizationsPromises = (response.actions || []).map((action) => this.getActionsWithCustomizations(action));
                const actionsWithCustomizations = yield Promise.all(actionsWithCustomizationsPromises);
                this.persoUtils.uniqueArray(this.actions, actionsWithCustomizations, 'variationCode');
                this.setCustomizationVisible();
                this.pagination = new personalizationcommons.PaginationHelper(response.pagination);
            }
            catch (_a) {
                this.persoMessageHandler.sendError(this.translateService.instant('personalization.error.gettingactions'));
            }
            finally {
                this.moreCustomizationsRequestProcessing = false;
            }
        });
    }
    getActionsWithCustomizations(action) {
        return __awaiter(this, void 0, void 0, function* () {
            const customization = {
                catalog: action.actionCatalog,
                catalogVersion: action.actionCatalogVersion,
                catalogVersionNameL10N: '',
                isFromCurrentCatalog: false
            };
            customization.isFromCurrentCatalog = this.persoUtils.isItemFromCurrentCatalog(customization, this.persoContextService.getSeData());
            yield this.persoUtils.getAndSetCatalogVersionNameL10N(customization);
            return Object.assign(Object.assign({}, action), { customization });
        });
    }
    setCustomizationVisible() {
        this.isCustomizationVisible = this.containerIdExists && this.actions.length > 0;
    }
};
ShowComponentInfoListComponent = __decorate([
    core.Component({
        selector: 'perso-show-component-info-list',
        template: `<div class="pe-component-info"><div class="pe-component-info__info-layout"><div *ngIf="!isPageBlocked"><div *ngIf="!isPersonalizationAllowedInWorkflow"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow"></div></div><div *ngIf="isContextualMenuInfoEnabled"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.title"></div><div class="perso__page-level-help-message" *ngIf="!isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.noactionsinworkflow.componentinfo"></div><div class="perso__page-level-help-message" *ngIf="pagination.totalCount > 0 && isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.label"></div><div class="perso__page-level-help-message" *ngIf="pagination.totalCount === 0 && isPersonalizationAllowedInWorkflow" translate="personalization.modal.showcomponentinfolist.help.nocustomizations"></div></div></div><div *ngIf="isPageBlocked"><div class="pe-component-info__title" translate="personalization.modal.showcomponentinfolist.blocked.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showcomponentinfolist.blocked.label"></div></div></div><div class="perso__page-level-help-message pe-component-info__info-layout" *ngIf="pagination.totalCount > 0 && isContextualMenuInfoEnabled"><span translate="personalization.modal.showcomponentinfolist.help.numberofenabledcustomizations"></span> <span>{{ pagination.totalCount }}</span></div><div [hidden]="pagination.totalCount === 0 || !isContextualMenuInfoEnabled"><personalization-infinite-scrolling [fetchPage]="fetchActionsPage" [dropDownContainerClass]="'pe-component-info__wrapper'"><personalization-prevent-parent-scroll><div [ngClass]="isCustomizationVisible ? 'pe-component-info__list-item' : '' " *ngFor="let action of actions"><div class="pe-component-info__names-layout"><div class="perso-wrap-ellipsis" [title]="action.customizationName">{{ action.customizationName }}</div><div class="perso-wrap-ellipsis perso-tree__primary-data" [title]="action.variationName">{{ action.variationName }}</div></div><div [ngClass]="isCustomizationVisible ? 'pe-component-info__icon' : '' "><se-tooltip *ngIf="!action.customization.isFromCurrentCatalog" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger [ngClass]="isCustomizationVisible ? 'perso__globe-icon sap-icon--globe' : '' "></span> <span se-tooltip-body>{{ action.customization.catalogVersionNameL10N | seL10n | async }}</span></se-tooltip></div></div><div class="pe-spinner--inner" *ngIf="moreCustomizationsRequestProcessing"><div class="spinner-md spinner-light"></div></div></personalization-prevent-parent-scroll></personalization-infinite-scrolling></div></div>`
    }),
    __param(0, core.Inject(smarteditcommons.CONTEXTUAL_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [Object, PersonalizationsmarteditComponentHandlerService,
        PersonalizationsmarteditContextService,
        PersonalizationsmarteditContextualMenuService,
        personalizationcommons.PersonalizationsmarteditMessageHandler,
        PersonalizationsmarteditRestService,
        personalizationcommons.PersonalizationsmarteditUtils,
        smarteditcommons.IPermissionService,
        core$1.TranslateService,
        core.ChangeDetectorRef])
], ShowComponentInfoListComponent);

let ShowComponentInfoListModule = class ShowComponentInfoListModule {
};
ShowComponentInfoListModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.SeTranslationModule.forChild(),
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            smarteditcommons.TooltipModule,
            smarteditcommons.L10nPipeModule
        ],
        declarations: [ShowComponentInfoListComponent],
        entryComponents: [ShowComponentInfoListComponent],
        exports: [ShowComponentInfoListComponent]
    })
], ShowComponentInfoListModule);

window.__smartedit__.addDecoratorPayload("Component", "ShowActionListComponent", {
    selector: 'perso-show-action-list',
    template: `<div class="pe-combinedview-ranking"><div class="pe-combinedview-ranking__info-layout"><div class="pe-combinedview-ranking__title" translate="personalization.modal.showactionlist.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showactionlist.help.label"></div></div><div class="pe-combinedview-ranking__list-item" *ngFor="let item of selectedItems; index as i" [hidden]="!item.visible"><div class="pe-combinedview-ranking__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combinedview-ranking__names-layout"><div class="perso-wrap-ellipsis" [textContent]="item.customization.name" title="{{item.customization.name}}"></div><div class="perso-wrap-ellipsis perso-tree__primary-data" [textContent]="item.variation.name" title="{{item.variation.name}}"></div></div><div class="pe-combinedview-ranking__icon"><se-tooltip *ngIf="!isCustomizationFromCurrentCatalog(item.customization)" [triggers]="['mouseenter', 'mouseleave']" [placement]="'top right'"><span se-tooltip-trigger class="perso__globe-icon sap-icon--globe"></span> <span se-tooltip-body>{{ item.variation.catalogVersionNameL10N | seL10n | async }}</span></se-tooltip></div></div></div>`
});
let ShowActionListComponent = class ShowActionListComponent {
    constructor(contextualMenuItem, persoContextService, persoUtils, persoComponentHandlerService, persoContextualMenuService, yjQuery) {
        this.contextualMenuItem = contextualMenuItem;
        this.persoContextService = persoContextService;
        this.persoUtils = persoUtils;
        this.persoComponentHandlerService = persoComponentHandlerService;
        this.persoContextualMenuService = persoContextualMenuService;
        this.COMPONENT_SELECTOR = 'showactionlistbutton';
        this.isContextualMenuShowActionListEnabled = false;
        this.$element = yjQuery(`[class~="${this.COMPONENT_SELECTOR}"]`);
    }
    ngOnInit() {
        this.selectedItems = this.persoContextService.getCombinedView().selectedItems;
        this.containerId = this.persoComponentHandlerService.getParentContainerIdForComponent(this.$element);
        this.containerIdExists = !!this.containerId;
        this.containerSourceId = this.containerIdExists
            ? this.persoComponentHandlerService.getContainerSourceIdForContainerId(this.containerId)
            : '';
        this.isContextualMenuShowActionListEnabled =
            this.persoContextualMenuService.isContextualMenuShowActionListEnabled(this.contextualMenuItem);
        (this.selectedItems || []).forEach((element, index) => {
            this.initItem(element);
        });
    }
    getLetterForElement(index) {
        return this.persoUtils.getLetterForElement(index);
    }
    getClassForElement(index) {
        return this.persoUtils.getClassForElement(index);
    }
    initItem(item) {
        item.visible = false;
        (item.variation.actions || []).forEach((elem) => {
            if (elem.containerId && elem.containerId === this.containerSourceId) {
                item.visible = true;
            }
        });
        this.persoUtils.getAndSetCatalogVersionNameL10N(item.variation);
    }
    isCustomizationFromCurrentCatalog(customization) {
        return this.persoUtils.isItemFromCurrentCatalog(customization, this.persoContextService.getSeData());
    }
};
ShowActionListComponent = __decorate([
    core.Component({
        selector: 'perso-show-action-list',
        template: `<div class="pe-combinedview-ranking"><div class="pe-combinedview-ranking__info-layout"><div class="pe-combinedview-ranking__title" translate="personalization.modal.showactionlist.title"></div><div class="perso__page-level-help-message" translate="personalization.modal.showactionlist.help.label"></div></div><div class="pe-combinedview-ranking__list-item" *ngFor="let item of selectedItems; index as i" [hidden]="!item.visible"><div class="pe-combinedview-ranking__letter-layout"><div [ngClass]="getClassForElement(i)" [textContent]="getLetterForElement(i)"></div></div><div class="pe-combinedview-ranking__names-layout"><div class="perso-wrap-ellipsis" [textContent]="item.customization.name" title="{{item.customization.name}}"></div><div class="perso-wrap-ellipsis perso-tree__primary-data" [textContent]="item.variation.name" title="{{item.variation.name}}"></div></div><div class="pe-combinedview-ranking__icon"><se-tooltip *ngIf="!isCustomizationFromCurrentCatalog(item.customization)" [triggers]="['mouseenter', 'mouseleave']" [placement]="'top right'"><span se-tooltip-trigger class="perso__globe-icon sap-icon--globe"></span> <span se-tooltip-body>{{ item.variation.catalogVersionNameL10N | seL10n | async }}</span></se-tooltip></div></div></div>`
    }),
    __param(0, core.Inject(smarteditcommons.CONTEXTUAL_MENU_ITEM_DATA)),
    __param(5, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Object, PersonalizationsmarteditContextService,
        personalizationcommons.PersonalizationsmarteditUtils,
        PersonalizationsmarteditComponentHandlerService,
        PersonalizationsmarteditContextualMenuService, Function])
], ShowActionListComponent);

let ShowActionListModule = class ShowActionListModule {
};
ShowActionListModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.TooltipModule,
            smarteditcommons.L10nPipeModule,
            smarteditcommons.SeTranslationModule.forChild(),
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule
        ],
        providers: [],
        declarations: [ShowActionListComponent],
        entryComponents: [ShowActionListComponent],
        exports: [ShowActionListComponent]
    })
], ShowActionListModule);

let /* @ngInject */ CustomizeViewServiceProxy = class /* @ngInject */ CustomizeViewServiceProxy {
    constructor(personalizationsmarteditCustomizeViewHelper) {
        this.personalizationsmarteditCustomizeViewHelper = personalizationsmarteditCustomizeViewHelper;
    }
    getSourceContainersInfo() {
        return this.personalizationsmarteditCustomizeViewHelper.getSourceContainersInfo();
    }
};
/* @ngInject */ CustomizeViewServiceProxy = __decorate([
    smarteditcommons.GatewayProxied('getSourceContainersInfo'),
    core.Injectable(),
    __metadata("design:paramtypes", [PersonalizationsmarteditCustomizeViewHelper])
], /* @ngInject */ CustomizeViewServiceProxy);

let CustomizeViewModule = class CustomizeViewModule {
};
CustomizeViewModule = __decorate([
    core.NgModule({
        imports: [servicesModule],
        providers: [CustomizeViewServiceProxy]
    })
], CustomizeViewModule);

window.__smartedit__.addDecoratorPayload("Component", "SharedSlotComponent", {
    selector: 'perso-shared-slot',
    template: `<div class="se-decorative-panel-wrapper"><div class="cmsx-ctx-wrapper1 se-slot-contextual-menu-level1"><div class="cmsx-ctx-wrapper2 se-slot-contextual-menu-level2"><div *ngIf="slotSharedFlag"><div class="se-decorator-panel-padding-center"></div><div class="se-decorative-panel-slot-contextual-menu"><div class="se-shared-slot-button-template" *ngIf="slotSharedFlag"><se-popup-overlay [popupOverlay]="popupConfig" [popupOverlayTrigger]="isPopupOpened" (popupOverlayOnHide)="hidePopup()"><button type="button" class="se-slot-ctx-menu__dropdown-toggle sap-icon--chain-link se-slot-ctx-menu__dropdown-toggle-icon" [ngClass]="{'se-slot-ctx-menu__dropdown-toggle--open': isPopupOpened}" id="sharedSlotButton-{{ slotId }}" (click)="onButtonClick()"></button><div class="se-slot__dropdown-menu" se-popup-overlay-body><div class="se-shared-slot__body"><div class="se-shared-slot__description" translate="personalization.slot.shared.popover.message"></div></div></div></se-popup-overlay></div></div></div></div></div></div>`
});
let SharedSlotComponent = class SharedSlotComponent {
    constructor(contextualMenuItem, persoComponentHandlerService, crossFrameEventService, cdr) {
        this.contextualMenuItem = contextualMenuItem;
        this.persoComponentHandlerService = persoComponentHandlerService;
        this.crossFrameEventService = crossFrameEventService;
        this.cdr = cdr;
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__divider',
                'se-slot-ctx-menu__dropdown-toggle-wrapper'
            ]
        };
        this.buttonName = 'SharedSlotButton';
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = false;
    }
    ngOnInit() {
        this.slotSharedFlag = false;
        this.persoComponentHandlerService.isSlotShared(this.slotId).then((result) => {
            this.slotSharedFlag = result;
        });
        this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_OUTER_FRAME_CLICKED, () => {
            this.isPopupOpened = false;
        });
        this.cdr.detectChanges();
    }
    ngOnChanges(changes) {
        if (changes.active && changes.active.currentValue) {
            this.isPopupOpened = false;
        }
    }
    ngOnDestroy() {
        if (this.unRegOuterFrameClicked) {
            this.unRegOuterFrameClicked();
        }
    }
    ngDoCheck() {
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
        }
    }
    onButtonClick() {
        this.isPopupOpened = !this.isPopupOpened;
    }
    hidePopup() {
        this.isPopupOpened = false;
    }
    get componentAttributes() {
        return this.contextualMenuItem.componentAttributes;
    }
    get slotId() {
        return this.componentAttributes.smarteditComponentId;
    }
};
SharedSlotComponent = __decorate([
    core.Component({
        selector: 'perso-shared-slot',
        template: `<div class="se-decorative-panel-wrapper"><div class="cmsx-ctx-wrapper1 se-slot-contextual-menu-level1"><div class="cmsx-ctx-wrapper2 se-slot-contextual-menu-level2"><div *ngIf="slotSharedFlag"><div class="se-decorator-panel-padding-center"></div><div class="se-decorative-panel-slot-contextual-menu"><div class="se-shared-slot-button-template" *ngIf="slotSharedFlag"><se-popup-overlay [popupOverlay]="popupConfig" [popupOverlayTrigger]="isPopupOpened" (popupOverlayOnHide)="hidePopup()"><button type="button" class="se-slot-ctx-menu__dropdown-toggle sap-icon--chain-link se-slot-ctx-menu__dropdown-toggle-icon" [ngClass]="{'se-slot-ctx-menu__dropdown-toggle--open': isPopupOpened}" id="sharedSlotButton-{{ slotId }}" (click)="onButtonClick()"></button><div class="se-slot__dropdown-menu" se-popup-overlay-body><div class="se-shared-slot__body"><div class="se-shared-slot__description" translate="personalization.slot.shared.popover.message"></div></div></div></se-popup-overlay></div></div></div></div></div></div>`
    }),
    __param(0, core.Inject(smarteditcommons.CONTEXTUAL_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [Object, PersonalizationsmarteditComponentHandlerService,
        smarteditcommons.CrossFrameEventService,
        core.ChangeDetectorRef])
], SharedSlotComponent);

const COMP_REGEX_PKEYS = '^.*Component$';
const SE_EDIT_PAGE = 'se.edit.page';
exports.PersonalizationsmarteditModule = class PersonalizationsmarteditModule {
};
exports.PersonalizationsmarteditModule = __decorate([
    smarteditcommons.SeEntryModule('personalizationsmartedit'),
    core.NgModule({
        imports: [
            smarteditcommons.SeTranslationModule.forChild(),
            platformBrowser.BrowserModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
            ShowComponentInfoListModule,
            CustomizeViewModule,
            ShowActionListModule,
            smarteditcommons.PopupOverlayModule,
            servicesModule
        ],
        providers: [
            PersonalizationsmarteditContextualMenuService,
            PersonalizationsmarteditComponentHandlerService,
            {
                provide: http.HTTP_INTERCEPTORS,
                useClass: personalizationcommons.BaseSiteHeaderInterceptor,
                multi: true,
                deps: [smarteditcommons.ISharedDataService]
            },
            {
                provide: personalizationcommons.IPersonalizationsmarteditContextMenuServiceProxy,
                useClass: PersonalizationsmarteditContextMenuServiceProxy
            },
            smarteditcommons.moduleUtils.bootstrap((decoratorService, featureService, personalizationsmarteditContextualMenuService) => {
                decoratorService.addMappings({
                    '^.*Slot$': ['personalizationsmarteditSharedSlot', 'se.slotContextualMenu']
                });
                decoratorService.addMappings({
                    COMP_REGEX_PKEYS: [
                        'personalizationsmarteditComponentLightUp',
                        'personalizationsmarteditCombinedViewComponentLightUp'
                    ]
                });
                featureService.addDecorator({
                    key: 'personalizationsmarteditComponentLightUp',
                    nameI18nKey: 'personalizationsmarteditComponentLightUp'
                });
                featureService.addDecorator({
                    key: 'personalizationsmarteditCombinedViewComponentLightUp',
                    nameI18nKey: 'personalizationsmarteditCombinedViewComponentLightUp'
                });
                featureService.addDecorator({
                    key: 'personalizationsmarteditSharedSlot',
                    nameI18nKey: 'personalizationsmarteditSharedSlot'
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.show.action.list',
                    i18nKey: 'personalization.context.action.list.show',
                    nameI18nKey: 'personalization.context.action.list.show',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: (config) => personalizationsmarteditContextualMenuService.isContextualMenuShowActionListEnabled(config),
                    action: {
                        component: ShowActionListComponent
                    },
                    displayClass: 'showactionlistbutton',
                    displayIconClass: 'hyicon hyicon-combinedview cmsx-ctx__icon personalization-ctx__icon',
                    displaySmallIconClass: 'hyicon hyicon-combinedview cmsx-ctx__icon--small',
                    permissions: ['se.read.page'],
                    priority: 500
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.info.action',
                    i18nKey: 'personalization.context.action.info',
                    nameI18nKey: 'personalization.context.action.info',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: () => personalizationsmarteditContextualMenuService.isContextualMenuInfoItemEnabled(),
                    action: {
                        component: ShowComponentInfoListComponent
                    },
                    displayClass: 'infoactionbutton',
                    displayIconClass: 'hyicon hyicon-msginfo cmsx-ctx__icon personalization-ctx__icon',
                    displaySmallIconClass: 'hyicon hyicon-msginfo cmsx-ctx__icon--small',
                    permissions: [SE_EDIT_PAGE],
                    priority: 510
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.add.action',
                    i18nKey: 'personalization.context.action.add',
                    nameI18nKey: 'personalization.context.action.add',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: (config) => personalizationsmarteditContextualMenuService.isContextualMenuAddItemEnabled(config),
                    action: {
                        callback: (config) => {
                            personalizationsmarteditContextualMenuService.openAddAction(config);
                        }
                    },
                    displayClass: 'addactionbutton',
                    displayIconClass: 'hyicon hyicon-addlg cmsx-ctx__icon personalization-ctx__icon',
                    displaySmallIconClass: 'hyicon hyicon-addlg cmsx-ctx__icon--small',
                    permissions: [SE_EDIT_PAGE],
                    priority: 520
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.component.edit.action',
                    i18nKey: 'personalization.context.component.action.edit',
                    nameI18nKey: 'personalization.context.component.action.edit',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: (config) => personalizationsmarteditContextualMenuService.isContextualMenuEditComponentItemEnabled(config),
                    action: {
                        callback: (config) => {
                            personalizationsmarteditContextualMenuService.openEditComponentAction(config);
                        }
                    },
                    displayClass: 'editbutton',
                    displayIconClass: 'sap-icon--edit cmsx-ctx__icon',
                    displaySmallIconClass: 'sap-icon--edit cmsx-ctx__icon--small',
                    permissions: [SE_EDIT_PAGE],
                    priority: 530
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.edit.action',
                    i18nKey: 'personalization.context.action.edit',
                    nameI18nKey: 'personalization.context.action.edit',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: (config) => personalizationsmarteditContextualMenuService.isContextualMenuEditItemEnabled(config),
                    action: {
                        callback: (config) => {
                            personalizationsmarteditContextualMenuService.openEditAction(config);
                        }
                    },
                    displayClass: 'replaceactionbutton',
                    displayIconClass: 'hyicon hyicon-change cmsx-ctx__icon personalization-ctx__icon',
                    displaySmallIconClass: 'hyicon hyicon-change cmsx-ctx__icon--small',
                    permissions: [SE_EDIT_PAGE],
                    priority: 540
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmartedit.context.delete.action',
                    i18nKey: 'personalization.context.action.delete',
                    nameI18nKey: 'personalization.context.action.delete',
                    regexpKeys: [COMP_REGEX_PKEYS],
                    condition: (config) => personalizationsmarteditContextualMenuService.isContextualMenuDeleteItemEnabled(config),
                    action: {
                        callback: (config) => {
                            personalizationsmarteditContextualMenuService.openDeleteAction(config);
                        }
                    },
                    displayClass: 'removeactionbutton',
                    displayIconClass: 'hyicon hyicon-removelg cmsx-ctx__icon personalization-ctx__icon',
                    displaySmallIconClass: 'hyicon hyicon-removelg cmsx-ctx__icon--small',
                    permissions: [SE_EDIT_PAGE],
                    priority: 550
                });
                featureService.addContextualMenuButton({
                    key: 'personalizationsmarteditSharedSlot',
                    nameI18nKey: 'slotcontextmenu.title.shared.button',
                    regexpKeys: ['^.*Slot$'],
                    action: {
                        component: SharedSlotComponent
                    },
                    permissions: ['se.read.page']
                });
            }, [
                smarteditcommons.IDecoratorService,
                smarteditcommons.IFeatureService,
                PersonalizationsmarteditContextualMenuService,
                PersonalizationsmarteditComponentHandlerService,
                personalizationcommons.IPersonalizationsmarteditContextServiceProxy,
                CustomizeViewServiceProxy
            ])
        ],
        declarations: [
            PersonalizationsmarteditComponentLightUpDecorator,
            PersonalizationsmarteditCombinedViewComponentLightUpDecorator,
            SharedSlotComponent
        ],
        entryComponents: [
            PersonalizationsmarteditComponentLightUpDecorator,
            PersonalizationsmarteditCombinedViewComponentLightUpDecorator,
            SharedSlotComponent
        ]
    })
], exports.PersonalizationsmarteditModule);
