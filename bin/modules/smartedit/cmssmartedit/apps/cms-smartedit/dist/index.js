'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var cmscommons = require('cmscommons');
var smarteditcommons = require('smarteditcommons');
var operators = require('rxjs/operators');
var lodash = require('lodash');
var core$1 = require('@ngx-translate/core');
var common = require('@angular/common');
var core$2 = require('@fundamental-ngx/core');

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

window.__smartedit__.addDecoratorPayload("Component", "ExternalComponentDecoratorComponent", {
    selector: 'external-component-decorator',
    template: `<div [ngClass]="{
        'cms-external-component-decorator': !isExternalSlot,
        'cms-external-component-decorator--versioning': isVersioningPerspective,
        'disabled-shared-slot-hovered': isActive && !isExternalSlot
    }"><div class="se-ctx-menu__overlay" *ngIf="!isExternalSlot && isReady"><se-tooltip *ngIf="!isActive || isVersioningPerspective" [placement]="'bottom'" [isChevronVisible]="true" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger class="se-ctx-menu-element__btn sap-icon--globe"></span><div se-tooltip-body translate="se.cms.from.external.catalog.version" [translateParams]="{ catalogVersion: catalogVersionText }"></div></se-tooltip></div><div class="se-wrapper-data" [ngClass]="{ 'disabled-shared-slot-versioning-mode': isVersioningPerspective && !isExternalSlot }"><ng-content></ng-content></div></div>`,
    styles: [`.cms-external-component-decorator{border:1px solid #dbe3eb;height:100%}.cms-external-component-decorator>.se-ctx-menu__overlay{left:1px!important;top:1px!important}`],
    providers: [smarteditcommons.L10nPipe]
});
let ExternalComponentDecoratorComponent = class ExternalComponentDecoratorComponent {
    constructor(catalogService, cMSModesService, componentHandlerService, l10nPipe, logService, element) {
        this.catalogService = catalogService;
        this.cMSModesService = cMSModesService;
        this.componentHandlerService = componentHandlerService;
        this.l10nPipe = l10nPipe;
        this.logService = logService;
        this.element = element;
        this.isActive = false;
        this.isExternalSlot = false;
        this.isReady = false;
        this.isVersioningPerspective = false;
    }
    set active(val) {
        this.isActive = val === 'true';
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            const parentSlotIdForComponent = this.componentHandlerService.getParentSlotForComponent(this.element.nativeElement);
            this.isExternalSlot = yield this.componentHandlerService.isExternalComponent(parentSlotIdForComponent, smarteditcommons.CONTENT_SLOT_TYPE);
            this.isReady = false;
            this.isVersioningPerspective = yield this.cMSModesService.isVersioningPerspectiveActive();
            try {
                this.catalogVersionText = yield this.getCatalogVersionText(this.smarteditCatalogVersionUuid);
                this.isReady = true;
            }
            catch (_a) {
                this.logService.error(`${this.constructor.name} - cannot find catalog version for uuid`, this.smarteditCatalogVersionUuid);
            }
        });
    }
    getCatalogVersionText(catalogVersionUuid) {
        return __awaiter(this, void 0, void 0, function* () {
            const catalogVersion = yield this.catalogService.getCatalogVersionByUuid(catalogVersionUuid);
            const catalogName = yield this.l10nPipe
                .transform(catalogVersion.catalogName)
                .pipe(operators.take(1))
                .toPromise();
            return `${catalogName} (${catalogVersion.version})`;
        });
    }
};
__decorate([
    core.Input('data-smartedit-catalog-version-uuid'),
    __metadata("design:type", String)
], ExternalComponentDecoratorComponent.prototype, "smarteditCatalogVersionUuid", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String),
    __metadata("design:paramtypes", [String])
], ExternalComponentDecoratorComponent.prototype, "active", null);
ExternalComponentDecoratorComponent = __decorate([
    smarteditcommons.SeCustomComponent(),
    core.Component({
        selector: 'external-component-decorator',
        template: `<div [ngClass]="{
        'cms-external-component-decorator': !isExternalSlot,
        'cms-external-component-decorator--versioning': isVersioningPerspective,
        'disabled-shared-slot-hovered': isActive && !isExternalSlot
    }"><div class="se-ctx-menu__overlay" *ngIf="!isExternalSlot && isReady"><se-tooltip *ngIf="!isActive || isVersioningPerspective" [placement]="'bottom'" [isChevronVisible]="true" [triggers]="['mouseenter', 'mouseleave']"><span se-tooltip-trigger class="se-ctx-menu-element__btn sap-icon--globe"></span><div se-tooltip-body translate="se.cms.from.external.catalog.version" [translateParams]="{ catalogVersion: catalogVersionText }"></div></se-tooltip></div><div class="se-wrapper-data" [ngClass]="{ 'disabled-shared-slot-versioning-mode': isVersioningPerspective && !isExternalSlot }"><ng-content></ng-content></div></div>`,
        styles: [`.cms-external-component-decorator{border:1px solid #dbe3eb;height:100%}.cms-external-component-decorator>.se-ctx-menu__overlay{left:1px!important;top:1px!important}`],
        providers: [smarteditcommons.L10nPipe]
    }),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogService,
        smarteditcommons.CMSModesService,
        smarteditcommons.IComponentHandlerService,
        smarteditcommons.L10nPipe,
        smarteditcommons.LogService,
        core.ElementRef])
], ExternalComponentDecoratorComponent);

window.__smartedit__.addDecoratorPayload("Component", "SyncIndicatorDecorator", {
    selector: 'sync-indicator',
    template: `<div class="sync-indicator-decorator" [ngClass]="syncStatus.status" [attr.sync-status]="syncStatus.status"><ng-content class="se-wrapper-data"></ng-content></div>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let SyncIndicatorDecorator = class SyncIndicatorDecorator extends smarteditcommons.AbstractDecorator {
    constructor(catalogService, slotSynchronizationService, crossFrameEventService, pageInfoService, cdr) {
        super();
        this.catalogService = catalogService;
        this.slotSynchronizationService = slotSynchronizationService;
        this.crossFrameEventService = crossFrameEventService;
        this.pageInfoService = pageInfoService;
        this.cdr = cdr;
        this.isVersionNonActive = false;
        this.syncStatus = {
            status: cmscommons.DEFAULT_SYNCHRONIZATION_STATUSES.UNAVAILABLE
        };
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            yield Promise.all([this.getPageUuid(), this.getIsVersionNonActive()]);
            if (this.isVersionNonActive) {
                yield this.fetchSyncStatus();
            }
            this.unRegisterSyncPolling = this.crossFrameEventService.subscribe(cmscommons.DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH, () => __awaiter(this, void 0, void 0, function* () {
                yield this.fetchSyncStatus();
                if (!this.cdr.destroyed) {
                    this.cdr.detectChanges();
                }
            }));
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    ngOnDestroy() {
        if (this.unRegisterSyncPolling) {
            this.unRegisterSyncPolling();
        }
    }
    getPageUuid() {
        return __awaiter(this, void 0, void 0, function* () {
            this.pageUUID = yield this.pageInfoService.getPageUUID();
        });
    }
    getIsVersionNonActive() {
        return __awaiter(this, void 0, void 0, function* () {
            this.isVersionNonActive = yield this.catalogService.isContentCatalogVersionNonActive();
        });
    }
    fetchSyncStatus() {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.isVersionNonActive) {
                return;
            }
            try {
                const syncStatus = yield this.slotSynchronizationService.getSyncStatus(this.pageUUID, this.componentAttributes.smarteditComponentId);
                if (this.slotSynchronizationService.syncStatusExists(syncStatus)) {
                    this.syncStatus = syncStatus;
                }
            }
            catch (_a) {
                this.syncStatus.status = cmscommons.DEFAULT_SYNCHRONIZATION_STATUSES.UNAVAILABLE;
            }
        });
    }
};
SyncIndicatorDecorator = __decorate([
    core.Component({
        selector: 'sync-indicator',
        template: `<div class="sync-indicator-decorator" [ngClass]="syncStatus.status" [attr.sync-status]="syncStatus.status"><ng-content class="se-wrapper-data"></ng-content></div>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogService,
        cmscommons.SlotSynchronizationService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.IPageInfoService,
        core.ChangeDetectorRef])
], SyncIndicatorDecorator);

/**
 * This service allows retrieving information about the containers found in a given page.
 */
/* @ngInject */ exports.SlotContainerService = class /* @ngInject */ SlotContainerService {
    constructor(restServiceFactory, experienceService) {
        this.experienceService = experienceService;
        const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${smarteditcommons.PAGE_CONTEXT_SITE_ID}/catalogs/${smarteditcommons.PAGE_CONTEXT_CATALOG}/versions/${smarteditcommons.PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscontainers?pageId=:pageId`;
        this.containersRestService = restServiceFactory.get(contentSlotContainerResourceURI);
    }
    /**
     * This method is used to retrieve the information about the container holding the provided component.
     * If the component is not inside a container, the method returns null.
     *
     * @param slotId The SmartEdit id of the slot where the component in question is located.
     * @param componentUuid The UUID of the component as defined in the database.
     *
     * @returns A promise that resolves to the information of the container of the component provided.
     * Will be null if the component is not inside a container.
     */
    getComponentContainer(slotId, componentUuid) {
        return __awaiter(this, void 0, void 0, function* () {
            const containersInPage = yield this.loadContainersInPageInfo();
            const containers = containersInPage.filter((container) => container.slotId === slotId && lodash.includes(container.components, componentUuid));
            return containers.length > 0 ? containers[0] : null;
        });
    }
    loadContainersInPageInfo() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.containersInPage) {
                return this.containersInPage;
            }
            const experience = yield this.experienceService.getCurrentExperience();
            const result = yield this.containersRestService.get({ pageId: experience.pageId });
            this.containersInPage = result.pageContentSlotContainerList;
            return this.containersInPage;
        });
    }
};
/* @ngInject */ exports.SlotContainerService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        smarteditcommons.IExperienceService])
], /* @ngInject */ exports.SlotContainerService);

/**
 * This service is used to retrieve menu items that are available to be used with hidden components.
//  */
/* @ngInject */ exports.HiddenComponentMenuService = class /* @ngInject */ HiddenComponentMenuService {
    constructor(contextualMenuService, slotContainerService) {
        this.contextualMenuService = contextualMenuService;
        this.slotContainerService = slotContainerService;
        this.MENU_ITEM_EXTERNAL = 'externalcomponentbutton';
        this.MENU_ITEM_CLONE = 'clonecomponentbutton';
        this.MENU_ITEM_REMOVE = 'se.cms.remove';
        this.allowedItems = {};
        this.setDefaultItemsAllowed();
    }
    /**
     * This method is used to set the list of items that can be used with hidden components.
     *
     * @param itemsToAllow The ID of the menu items that can be used with hidden components.
     *
     */
    allowItemsInHiddenComponentMenu(itemsToAllow) {
        itemsToAllow.forEach((item) => {
            this.allowedItems[item] = true;
        });
    }
    /**
     * This method removes a provided set of allowed menu items if previously allowed.
     *
     * @param itemsToDisallow An array containing the ID's of the menu items that cannot be used any longer with hidden
     * components.
     *
     */
    removeAllowedItemsInHiddenComponentMenu(itemsToDisallow) {
        itemsToDisallow.forEach((item) => {
            delete this.allowedItems[item];
        });
    }
    /**
     * This method retrieves the list of IDs of the menu items that can be used with hidden components.
     *
     * @returns The list of IDs of the menu items that can be used with hidden components.
     *
     */
    getAllowedItemsInHiddenComponentMenu() {
        return Object.keys(this.allowedItems);
    }
    /**
     * This method is used to retrieve the menu items available to be used in the provided component. To do so,
     * this method retrieves contextual menu items available for the provided component and filters out the ones that cannot
     * be used in hidden components. For example, assuming that a visible component has 'drag and drop' and 'remove'
     * contextual menu items, if the component is hidden it should only have the remove button available, since the
     * drag and drop operation is meaningless if the component is hidden. Hence, this service will retrieve only
     * the remove item.
     *
     * @param component The hidden component for which to retrieve its menu items.
     * @param slotId The SmartEdit id of the slot where the component is located.
     *
     * @returns Promise that resolves to an array of contextual menu items available for the component
     * provided.
     */
    getItemsForHiddenComponent(component, slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            this.validateComponent(component);
            const configuration = yield this.buildComponentInfo(slotId, component);
            return this.getAllowedItemsForComponent(component, configuration);
        });
    }
    validateComponent(component) {
        if (!component) {
            throw new Error('HiddenComponentMenuService - Component cannot be null.');
        }
        if (!component.uid) {
            throw new Error('HiddenComponentMenuService - Component needs a uid.');
        }
        if (!component.typeCode) {
            throw new Error('HiddenComponentMenuService - Component needs a type code.');
        }
        if (!component.uuid) {
            throw new Error('HiddenComponentMenuService - Component needs a uuid.');
        }
    }
    setDefaultItemsAllowed() {
        this.allowItemsInHiddenComponentMenu([
            this.MENU_ITEM_EXTERNAL,
            this.MENU_ITEM_CLONE,
            this.MENU_ITEM_REMOVE
        ]);
    }
    buildComponentInfo(slotId, component) {
        return __awaiter(this, void 0, void 0, function* () {
            const componentContainer = yield this.slotContainerService.getComponentContainer(slotId, component.uuid);
            return {
                componentType: component.typeCode,
                componentId: component.uid,
                componentAttributes: {
                    smarteditCatalogVersionUuid: component.catalogVersion,
                    smarteditComponentId: component.uid,
                    smarteditComponentType: component.componentType,
                    smarteditComponentUuid: component.uuid,
                    smarteditElementUuid: null
                },
                containerType: componentContainer ? componentContainer.containerType : null,
                containerId: componentContainer ? componentContainer.containerId : null,
                element: null,
                isComponentHidden: true,
                slotId,
                iLeftBtns: 0
            };
        });
    }
    getAllowedItemsForComponent(component, configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            const menuItems = this.contextualMenuService.getContextualMenuByType(component.typeCode);
            const allowedActionsPromises = menuItems
                .filter((item) => this.allowedItems[item.key] && !!item.condition)
                .map((item) => __awaiter(this, void 0, void 0, function* () {
                let isEnabled;
                try {
                    isEnabled = yield item.condition(configuration);
                }
                catch (_a) {
                    isEnabled = false;
                }
                return { isEnabled, item };
            }));
            const allowedActions = (yield Promise.all(allowedActionsPromises))
                .filter(({ isEnabled }) => isEnabled)
                .map(({ item }) => item);
            return { buttons: allowedActions, configuration };
        });
    }
};
/* @ngInject */ exports.HiddenComponentMenuService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IContextualMenuService,
        exports.SlotContainerService])
], /* @ngInject */ exports.HiddenComponentMenuService);

/**
 * This service is used to fetch and cache components information.
 * This service keeps track of components added, edited and removed. It also automatically fetches and caches components when they are visible in the viewport (and invalidates them).
 *
 * This service is intended to be used to improve the performance of the application by reducing the number of xhr calls to the cmsitems api.
 * Example:
 * - a component in the overlay that is doing a fetch to the cmsitems api should use this service instead of using cmsitemsRestService.
 *   When a lot of components are rendered in the overlay we want to avoid one xhr call per component, but instead use this service that is listening
 *   to the 'OVERLAY_RERENDERED_EVENT' and fetch components information in batch (POST to cmsitems endpoint with an Array of uuids).
 */
/* @ngInject */ exports.ComponentInfoService = class /* @ngInject */ ComponentInfoService {
    constructor(yjQuery, logService, crossFrameEventService, cmsitemsRestService, promiseUtils, cMSModesService) {
        this.yjQuery = yjQuery;
        this.logService = logService;
        this.crossFrameEventService = crossFrameEventService;
        this.cmsitemsRestService = cmsitemsRestService;
        this.promiseUtils = promiseUtils;
        this.cMSModesService = cMSModesService;
        this.cachedComponents = {};
        this.promisesQueue = {};
        this.crossFrameEventService.subscribe(smarteditcommons.OVERLAY_RERENDERED_EVENT, (eventId, data) => {
            this.onOverlayReRendered(data);
        });
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_CREATED_EVENT, (eventId, data) => {
            this.onComponentAdded(data);
        });
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_UPDATED_EVENT, (eventId, data) => {
            this.onComponentAdded(data);
        });
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_REMOVED_EVENT, (eventId, data) => {
            this.onComponentRemoved(data);
        });
        // clear cache
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, () => this.clearCache());
        this.crossFrameEventService.subscribe(smarteditcommons.EVENTS.USER_HAS_CHANGED, () => this.clearCache());
    }
    /**
     * @internal
     * Returns a Promise that will be resolved with the component identified by the given uuid.
     * When called this method works like this:
     * - If the component is in the cache, the promise resolves right away.
     * - If the component is not in the cache, and the forceRetrieval flag is not set, this method won't call the cmsItem backend API right away.
     *   Instead, it waits until the component is cached (e.g., it is added to the overlay).
     * - If the forceRetrieval flag is set, then the method will call the cmsItem backend API right away.
     *
     * @param uuid The uuid of the item to retrieve
     * @param forceRetrieval Boolean flag. It specifies whether to retrieve the cmsItem right away.
     * @returns Promise that will be resolved only if the component was added previously in the overlay and if not will resolve only when the component is added to the overlay.
     *
     */
    getById(uuid, forceRetrieval) {
        return __awaiter(this, void 0, void 0, function* () {
            const uuidSelector = `[${smarteditcommons.UUID_ATTRIBUTE}='${uuid}']`;
            if (!forceRetrieval &&
                !this.cachedComponents[uuid] &&
                !document.querySelectorAll(uuidSelector).length) {
                // For hidden components that are not present in the DOM
                forceRetrieval = true;
            }
            if (this.isComponentCached(uuid)) {
                return this.cachedComponents[uuid];
            }
            else if (forceRetrieval) {
                return this.getComponentDataByUUID(uuid);
            }
            else {
                const deferred = this.promisesQueue[uuid] || this.promiseUtils.defer();
                if (!this.promisesQueue[uuid]) {
                    this.promisesQueue[uuid] = deferred;
                }
                return deferred.promise;
            }
        });
    }
    resolvePromises(data) {
        (data.response ? data.response : [data]).forEach((component) => {
            this.cachedComponents[component.uuid] = component;
            if (this.promisesQueue[component.uuid]) {
                this.promisesQueue[component.uuid].resolve(component);
                delete this.promisesQueue[component.uuid];
            }
        });
    }
    rejectPromises(uuids, error) {
        this.logService.error('componentInfoService:: getById error:', error.message);
        uuids.forEach((uuid) => {
            if (this.promisesQueue[uuid]) {
                this.promisesQueue[uuid].reject(error);
                delete this.promisesQueue[uuid];
            }
        });
    }
    getComponentDataByUUID(uuid) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.cmsitemsRestService.getById(uuid);
                this.resolvePromises(response);
                return this.cachedComponents[uuid];
            }
            catch (error) {
                this.rejectPromises([uuid], error);
                throw error;
            }
        });
    }
    getComponentsDataByUUIDs(uuids) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const components = yield this.cmsitemsRestService.getByIds(uuids, 'DEFAULT');
                this.resolvePromises(components);
            }
            catch (e) {
                this.rejectPromises(uuids, e);
            }
        });
    }
    onComponentsAddedToOverlay(addedComponentsDomElements) {
        const uuids = addedComponentsDomElements
            .map((component) => this.yjQuery(component).attr(smarteditcommons.UUID_ATTRIBUTE))
            .filter((uuid) => !Object.keys(this.cachedComponents).includes(uuid));
        if (uuids.length) {
            this.getComponentsDataByUUIDs(uuids);
        }
    }
    // delete from the cache the components that were removed from the DOM
    // note: components that are still in the DOM were only removed from the overlay
    onComponentsRemovedFromOverlay(removedComponentsDomElements) {
        removedComponentsDomElements
            .filter((component) => {
            const uuidSelector = `[${smarteditcommons.UUID_ATTRIBUTE}='${this.yjQuery(component).attr(smarteditcommons.UUID_ATTRIBUTE)}']`;
            return !this.yjQuery(uuidSelector).length;
        })
            .filter((component) => Object.keys(this.cachedComponents).includes(this.yjQuery(component).attr(smarteditcommons.UUID_ATTRIBUTE)))
            .map((component) => this.yjQuery(component).attr(smarteditcommons.UUID_ATTRIBUTE))
            .forEach((uuid) => {
            delete this.cachedComponents[uuid];
        });
    }
    forceAddComponent(cmsComponentToAdd) {
        this.resolvePromises({
            response: [cmsComponentToAdd]
        });
    }
    forceRemoveComponent(componentToRemove) {
        delete this.cachedComponents[componentToRemove.uuid];
    }
    isComponentCached(componentUuid) {
        return !!this.cachedComponents[componentUuid];
    }
    clearCache() {
        this.cachedComponents = {};
        this.promisesQueue = {};
    }
    //  CMSX-10058: In versioning perspective, edit page is not allowed, do not need to update the component cache
    onOverlayReRendered(data) {
        return __awaiter(this, void 0, void 0, function* () {
            const isVersioningPerspectiveActive = yield this.cMSModesService.isVersioningPerspectiveActive();
            if (data && !isVersioningPerspectiveActive) {
                if (data.addedComponents && data.addedComponents.length) {
                    this.onComponentsAddedToOverlay(data.addedComponents);
                }
                if (data.removedComponents && data.removedComponents.length) {
                    this.onComponentsRemovedFromOverlay(data.removedComponents);
                }
            }
        });
    }
    // Components added & removed from storefront page.
    onComponentAdded(data) {
        this.forceAddComponent(data);
    }
    onComponentRemoved(data) {
        this.forceRemoveComponent(data);
    }
};
/* @ngInject */ exports.ComponentInfoService = __decorate([
    core.Injectable(),
    __param(0, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [Function, smarteditcommons.LogService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.CmsitemsRestService,
        smarteditcommons.PromiseUtils,
        smarteditcommons.CMSModesService])
], /* @ngInject */ exports.ComponentInfoService);

/* @ngInject */ exports.ComponentSharedService = class /* @ngInject */ ComponentSharedService extends cmscommons.IComponentSharedService {
    constructor(componentInfoService) {
        super();
        this.componentInfoService = componentInfoService;
    }
    isComponentShared(componentParam) {
        return __awaiter(this, void 0, void 0, function* () {
            const component = yield this.determineComponent(componentParam);
            if (!component.slots) {
                throw new Error('ComponentSharedService::isComponentShared - Component must have slots property.');
            }
            return component.slots.length > 1;
        });
    }
    determineComponent(componentParam) {
        if (typeof componentParam === 'string') {
            return this.componentInfoService.getById(componentParam, true);
        }
        else if (componentParam.uuid) {
            return this.componentInfoService.getById(componentParam.uuid, true);
        }
        return Promise.resolve(componentParam);
    }
};
/* @ngInject */ exports.ComponentSharedService = __decorate([
    smarteditcommons.GatewayProxied(),
    core.Injectable(),
    __metadata("design:paramtypes", [exports.ComponentInfoService])
], /* @ngInject */ exports.ComponentSharedService);

exports.ComponentVisibilityAlertService = class ComponentVisibilityAlertService extends cmscommons.IComponentVisibilityAlertService {
};
exports.ComponentVisibilityAlertService = __decorate([
    smarteditcommons.GatewayProxied('checkAndAlertOnComponentVisibility')
], exports.ComponentVisibilityAlertService);

/* @ngInject */ exports.SlotRestrictionsService = class /* @ngInject */ SlotRestrictionsService extends smarteditcommons.ISlotRestrictionsService {
    constructor(componentHandlerService, logService, pageContentSlotsComponentsRestService, pageInfoService, restServiceFactory, slotSharedService, typePermissionsRestService, yjQuery, crossFrameEventService, cMSModesService) {
        super();
        this.componentHandlerService = componentHandlerService;
        this.logService = logService;
        this.pageContentSlotsComponentsRestService = pageContentSlotsComponentsRestService;
        this.pageInfoService = pageInfoService;
        this.restServiceFactory = restServiceFactory;
        this.slotSharedService = slotSharedService;
        this.typePermissionsRestService = typePermissionsRestService;
        this.yjQuery = yjQuery;
        this.cMSModesService = cMSModesService;
        this.currentPageId = null;
        this.slotRestrictions = {};
        this.CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT = 100;
        crossFrameEventService.subscribe(smarteditcommons.EVENTS.PAGE_CHANGE, () => __awaiter(this, void 0, void 0, function* () {
            //  CMSX-10058: In versioning perspective, edit page is not allowed, do not need to update the slots restrictions cache
            const isVersioningPerspectiveActive = yield this.cMSModesService.isVersioningPerspectiveActive();
            if (!isVersioningPerspectiveActive) {
                this.emptyCache();
                this.cacheSlotsRestrictions();
            }
        }));
    }
    /**
     * @deprecated since 2005
     */
    getAllComponentTypesSupportedOnPage() {
        return __awaiter(this, void 0, void 0, function* () {
            const slots = this.yjQuery(this.componentHandlerService.getAllSlotsSelector());
            const slotIds = slots
                .get()
                .map((slot) => this.componentHandlerService.getId(slot));
            const slotRestrictionPromises = slotIds.map((slotId) => this.getSlotRestrictions(slotId));
            try {
                const arrayOfSlotRestrictions = yield Promise.all(slotRestrictionPromises);
                return lodash.flatten(arrayOfSlotRestrictions);
            }
            catch (error) {
                this.logService.info(error);
                return Promise.reject();
            }
        });
    }
    getSlotRestrictions(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            const pageId = yield this.getPageUID(this.currentPageId);
            const isExternalSlot = yield this.isExternalSlot(slotId);
            this.currentPageId = pageId;
            const restrictionId = this.getEntryId(this.currentPageId, slotId);
            if (this.slotRestrictions[restrictionId]) {
                return this.slotRestrictions[restrictionId];
            }
            else if (isExternalSlot) {
                this.slotRestrictions[restrictionId] = [];
            }
            return [];
        });
    }
    /**
     * This methods determines whether a component of the provided type is allowed in the slot.
     *
     * @param slot The slot for which to verify if it allows a component of the provided type.
     * @returns Promise containing COMPONENT_IN_SLOT_STATUS (ALLOWED, DISALLOWED, MAYBEALLOWED) string that determines whether a component of the provided type is allowed in the slot.
     *
     * TODO: The name is misleading. To not introduce breaking change in 2105, consider changing the name in the next release (after 2105).
     * Candidates: "getComponentStatusInSlot", "determineComponentStatusInSlot"
     */
    isComponentAllowedInSlot(slot, dragInfo) {
        return __awaiter(this, void 0, void 0, function* () {
            return this.determineComponentStatusInSlot(slot.id, dragInfo);
        });
    }
    determineComponentStatusInSlot(slotId, dragInfo) {
        return __awaiter(this, void 0, void 0, function* () {
            const currentSlotRestrictions = yield this.getSlotRestrictions(slotId);
            const componentsForSlot = yield this.pageContentSlotsComponentsRestService.getComponentsForSlot(slotId);
            const isComponentIdAllowed = slotId === dragInfo.slotId ||
                !componentsForSlot.some((component) => component.uid === dragInfo.componentId);
            if (isComponentIdAllowed) {
                if (currentSlotRestrictions) {
                    return lodash.includes(currentSlotRestrictions, dragInfo.componentType)
                        ? smarteditcommons.COMPONENT_IN_SLOT_STATUS.ALLOWED
                        : smarteditcommons.COMPONENT_IN_SLOT_STATUS.DISALLOWED;
                }
                return smarteditcommons.COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED;
            }
            return smarteditcommons.COMPONENT_IN_SLOT_STATUS.DISALLOWED;
        });
    }
    isSlotEditable(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            // This method can get called with slotId as "undefined", which means that the slot has not been rendered yet.
            if (!slotId) {
                return Promise.resolve(false);
            }
            const slotPermissions = yield this.typePermissionsRestService.hasUpdatePermissionForTypes([
                smarteditcommons.CONTENT_SLOT_TYPE
            ]);
            const isShared = yield this.slotSharedService.isSlotShared(slotId);
            let result = slotPermissions[smarteditcommons.CONTENT_SLOT_TYPE];
            if (isShared) {
                const isExternalSlot = yield this.isExternalSlot(slotId);
                result = result && !isExternalSlot && !this.slotSharedService.areSharedSlotsDisabled();
            }
            return result;
        });
    }
    emptyCache() {
        this.slotRestrictions = {};
        this.currentPageId = null;
    }
    cacheSlotsRestrictions() {
        return __awaiter(this, void 0, void 0, function* () {
            const originalSlotIds = this.componentHandlerService.getAllSlotUids() || [];
            const nonExternalOriginalSlotIds = [];
            yield Promise.all(originalSlotIds.map((slotId) => __awaiter(this, void 0, void 0, function* () {
                const isExternalSlot = yield this.isExternalSlot(slotId);
                if (!isExternalSlot) {
                    nonExternalOriginalSlotIds.push(slotId);
                }
            })));
            const uniqueSlotIds = lodash.uniq(nonExternalOriginalSlotIds);
            const chunks = lodash.chunk(uniqueSlotIds, this.CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT);
            return this.recursiveFetchSlotsRestrictions(chunks, 0);
        });
    }
    // Recursively fetch slots restrictions by the number of chunks of slotIds split by fetch limit
    recursiveFetchSlotsRestrictions(slotIdsByChunks, chunkIndex) {
        return __awaiter(this, void 0, void 0, function* () {
            if (chunkIndex === slotIdsByChunks.length) {
                return;
            }
            yield this.fetchSlotsRestrictions(slotIdsByChunks[chunkIndex]);
            this.recursiveFetchSlotsRestrictions(slotIdsByChunks, chunkIndex + 1);
        });
    }
    // Fetch slot restriction and cache them in-memory
    fetchSlotsRestrictions(slotIds) {
        return __awaiter(this, void 0, void 0, function* () {
            const pageId = yield this.getPageUID(this.currentPageId);
            this.currentPageId = pageId;
            this.slotsRestrictionsRestService =
                this.slotsRestrictionsRestService ||
                    this.restServiceFactory.get(cmscommons.CONTENT_SLOTS_TYPE_RESTRICTION_RESOURCE_URI, this.currentPageId);
            try {
                const contentSlotsResponse = yield this.slotsRestrictionsRestService.save({
                    slotIds,
                    pageUid: this.currentPageId
                });
                const contentSlots = contentSlotsResponse || [];
                contentSlots.forEach((slot) => {
                    const restrictionId = this.getEntryId(this.currentPageId, slot.contentSlotUid);
                    this.slotRestrictions[restrictionId] = slot.validComponentTypes;
                });
            }
            catch (error) {
                this.logService.info(error);
                throw new Error(error);
            }
        });
    }
    getPageUID(pageUID) {
        return !smarteditcommons.stringUtils.isBlank(pageUID)
            ? Promise.resolve(pageUID)
            : this.pageInfoService.getPageUID();
    }
    getEntryId(pageId, slotId) {
        return `${pageId}_${slotId}`;
    }
    isExternalSlot(slotId) {
        return this.componentHandlerService.isExternalComponent(slotId, smarteditcommons.CONTENT_SLOT_TYPE);
    }
};
/* @ngInject */ exports.SlotRestrictionsService = __decorate([
    smarteditcommons.GatewayProxied('getAllComponentTypesSupportedOnPage', 'getSlotRestrictions', 'determineComponentStatusInSlot', 'isSlotEditable'),
    core.Injectable(),
    __param(7, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IComponentHandlerService,
        smarteditcommons.LogService,
        cmscommons.IPageContentSlotsComponentsRestService,
        smarteditcommons.IPageInfoService,
        smarteditcommons.IRestServiceFactory,
        smarteditcommons.SlotSharedService,
        cmscommons.TypePermissionsRestService, Function, smarteditcommons.CrossFrameEventService,
        smarteditcommons.CMSModesService])
], /* @ngInject */ exports.SlotRestrictionsService);

/**
 * Allows enabling the Edit Component contextual menu item,
 * providing a SmartEdit CMS admin the ability to edit various properties of the given component.
 *
 * Convenience service to attach the open Editor Modal action to the contextual menu of a given component type, or
 * given regex corresponding to a selection of component types.
 *
 * Example: The Edit button is added to the contextual menu of the CMSParagraphComponent, and all types postfixed
 * with BannerComponent.
 */
/* @ngInject */ exports.EditorEnablerService = class /* @ngInject */ EditorEnablerService extends cmscommons.IEditorEnablerService {
    constructor(componentHandlerService, componentVisibilityAlertService, editorModalService, featureService, slotRestrictionsService, crossFrameEventService, systemEventService) {
        super();
        this.componentHandlerService = componentHandlerService;
        this.componentVisibilityAlertService = componentVisibilityAlertService;
        this.editorModalService = editorModalService;
        this.featureService = featureService;
        this.slotRestrictionsService = slotRestrictionsService;
        this.crossFrameEventService = crossFrameEventService;
        this.systemEventService = systemEventService;
        this.contextualMenuButton = {
            key: 'se.cms.edit',
            nameI18nKey: 'se.cms.contextmenu.nameI18nKey.edit',
            i18nKey: 'se.cms.contextmenu.title.edit',
            descriptionI18nKey: 'se.cms.contextmenu.descriptionI18n.edit',
            displayClass: 'editbutton',
            displayIconClass: 'sap-icon--edit',
            displaySmallIconClass: 'sap-icon--edit',
            priority: 400,
            permissions: ['se.context.menu.edit.component'],
            action: {
                callback: (config) => this.onClickEditButton(config)
            },
            condition: (config) => this.isSlotEditableForNonExternalComponent(config)
        };
    }
    /**
     * Enables the Edit contextual menu item for the given component types.
     *
     * @param componentTypes The list of component types, as defined in the platform, for which to enable the Edit contextual menu.
     */
    enableForComponents(componentTypes) {
        const contextualMenuConfig = Object.assign(Object.assign({}, this.contextualMenuButton), { regexpKeys: componentTypes });
        this.featureService.addContextualMenuButton(contextualMenuConfig);
    }
    onClickEditButton({ slotId, componentAttributes, isComponentHidden, componentType, componentUuid }) {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.isEditorModalOpen) {
                return;
            }
            this.isEditorModalOpen = true;
            //  Add class to panel to make the width equals to 650px defined in apps/smartedit-commons/src/services/modal/modal.scss
            const config = { dialogPanelClass: 'modal-dialog' };
            try {
                let item;
                if (isComponentHidden) {
                    item = yield this.editorModalService.openAndRerenderSlot(componentType, componentUuid, 'visible', config);
                }
                else {
                    item = yield this.editorModalService.open(componentAttributes, null, null, null, null, null, config);
                }
                this.isEditorModalOpen = false;
                this.systemEventService.publish(cmscommons.COMPONENT_UPDATED_EVENT, item);
                yield this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                    itemId: item.uuid,
                    itemType: item.itemtype,
                    catalogVersion: item.catalogVersion,
                    restricted: item.restricted,
                    visible: item.visible,
                    slotId
                });
            }
            catch (_a) {
                this.isEditorModalOpen = false;
            }
        });
    }
    isSlotEditableForNonExternalComponent(config) {
        return __awaiter(this, void 0, void 0, function* () {
            let slotId = null;
            if (!!config.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(config.element);
            }
            else {
                slotId = config.slotId;
            }
            const isEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
            const isExternalComponent = yield this.componentHandlerService.isExternalComponent(config.componentId, config.componentType);
            return isEditable && !isExternalComponent;
        });
    }
};
/* @ngInject */ exports.EditorEnablerService = __decorate([
    smarteditcommons.GatewayProxied('onClickEditButton', 'isSlotEditableForNonExternalComponent'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IComponentHandlerService,
        cmscommons.IComponentVisibilityAlertService,
        smarteditcommons.IEditorModalService,
        smarteditcommons.IFeatureService,
        smarteditcommons.ISlotRestrictionsService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.SystemEventService])
], /* @ngInject */ exports.EditorEnablerService);

var /* @ngInject */ CmsDragAndDropService_1;
const DATA_COMPONENT_ID = 'data-component-id';
/**
 * This service provides a rich drag and drop experience tailored for CMS operations.
 */
/* @ngInject */ exports.CmsDragAndDropService = /* @ngInject */ CmsDragAndDropService_1 = class /* @ngInject */ CmsDragAndDropService {
    constructor(alertService, assetsService, browserService, componentEditingFacade, componentHandlerService, dragAndDropService, gatewayFactory, translateService, pageContentSlotsComponentsRestService, slotRestrictionsService, systemEventService, waitDialogService, yjQuery, domain, crossFrameEventService) {
        this.alertService = alertService;
        this.assetsService = assetsService;
        this.browserService = browserService;
        this.componentEditingFacade = componentEditingFacade;
        this.componentHandlerService = componentHandlerService;
        this.dragAndDropService = dragAndDropService;
        this.gatewayFactory = gatewayFactory;
        this.translateService = translateService;
        this.pageContentSlotsComponentsRestService = pageContentSlotsComponentsRestService;
        this.slotRestrictionsService = slotRestrictionsService;
        this.systemEventService = systemEventService;
        this.waitDialogService = waitDialogService;
        this.yjQuery = yjQuery;
        this.domain = domain;
        this.crossFrameEventService = crossFrameEventService;
        this.cachedSlots = {};
        this.highlightedSlot = null;
        this.highlightedComponent = null;
        this.highlightedHint = null;
        this.dragInfo = null;
        this.overlayRenderedUnSubscribeFn = null;
        this.componentRemovedUnSubscribeFn = null;
        this._window = smarteditcommons.windowUtils.getWindow();
        this.gateway = this.gatewayFactory.createGateway('cmsDragAndDrop');
    }
    /**
     * This method registers this drag and drop instance in SmartEdit.
     */
    register() {
        this.dragAndDropService.register({
            id: /* @ngInject */ CmsDragAndDropService_1.CMS_DRAG_AND_DROP_ID,
            sourceSelector: [
                /* @ngInject */ CmsDragAndDropService_1.SOURCE_SELECTOR,
                /* @ngInject */ CmsDragAndDropService_1.MORE_MENU_SOURCE_SELECTOR
            ],
            targetSelector: /* @ngInject */ CmsDragAndDropService_1.TARGET_SELECTOR,
            startCallback: (event) => this.onStart(event),
            dragEnterCallback: (event) => this.onDragEnter(event),
            dragOverCallback: (event) => this.onDragOver(event),
            dropCallback: (event) => this.onDrop(event),
            outCallback: (event) => this.onDragLeave(event),
            stopCallback: (event) => this.onStop(event),
            enableScrolling: true
        });
    }
    /**
     * This method unregisters this drag and drop instance from SmartEdit.
     */
    unregister() {
        this.dragAndDropService.unregister([/* @ngInject */ CmsDragAndDropService_1.CMS_DRAG_AND_DROP_ID]);
        if (this.overlayRenderedUnSubscribeFn) {
            this.overlayRenderedUnSubscribeFn();
        }
        if (this.componentRemovedUnSubscribeFn) {
            this.componentRemovedUnSubscribeFn();
        }
    }
    /**
     * This method applies this drag and drop instance in the current page. After this method is executed,
     * the user can start a drag and drop operation.
     */
    apply() {
        this.dragAndDropService.apply(/* @ngInject */ CmsDragAndDropService_1.CMS_DRAG_AND_DROP_ID);
        this.addUIHelpers();
        // Register a listener for every time the overlay is updated.
        this.overlayRenderedUnSubscribeFn = this.systemEventService.subscribe(smarteditcommons.OVERLAY_RERENDERED_EVENT, () => this.onOverlayUpdate());
        this.componentRemovedUnSubscribeFn = this.systemEventService.subscribe(cmscommons.COMPONENT_REMOVED_EVENT, () => this.onOverlayUpdate());
        this.gateway.subscribe(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_STARTED, (eventId, data) => {
            this.dragAndDropService.markDragStarted();
            this.initializeDragOperation(data);
        });
        this.gateway.subscribe(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_STOPPED, () => {
            this.dragAndDropService.markDragStopped();
            this.cleanDragOperation();
        });
        this.gateway.subscribe(cmscommons.DRAG_AND_DROP_EVENTS.SCROLL_TO_MODIFIED_SLOT, (eventId, targetSlotId) => {
            const component = this.componentHandlerService.getComponent(targetSlotId, smarteditcommons.CONTENT_SLOT_TYPE);
            if (component && component.length > 0) {
                component[0].scrollIntoView();
            }
        });
    }
    /**
     * This method updates this drag and drop instance in the current page. It is important to execute
     * this method every time a draggable or droppable element is added or removed from the page DOM.
     */
    update() {
        this.dragAndDropService.update(/* @ngInject */ CmsDragAndDropService_1.CMS_DRAG_AND_DROP_ID);
        // Add UI helpers -> They identify the places where you can drop components.
        this.addUIHelpers();
        // Update cache elements AFTER adding UI Helpers
        this.cacheElements();
    }
    // Other Event Handlers
    onOverlayUpdate() {
        this.update();
        return Promise.resolve();
    }
    // Drag and Drop Event Handlers
    onStart(event) {
        // Find element
        let targetElm = this.getSelector(event.target);
        // when the DnD icon is in the more option dropdown, the targetElm is a span and has no data-component-id. Here we get the closest element (i.e. <contextual-menu-item>)
        if (!targetElm.attr(DATA_COMPONENT_ID)) {
            targetElm = this.yjQuery(targetElm).closest('[data-component-id]');
        }
        const component = targetElm.closest(/* @ngInject */ CmsDragAndDropService_1.COMPONENT_SELECTOR);
        const slot = component.closest(/* @ngInject */ CmsDragAndDropService_1.SLOT_SELECTOR);
        // Here if the component evaluated above exits that means the component has been located and we can fetch its attributes
        // else it is not located as the DnD option is hidden inside the more option of the contextual menu in which case
        // we find the component/slot info by accessing attributes of the DnD icon.
        const componentId = component.length > 0
            ? this.componentHandlerService.getId(component)
            : targetElm.attr(DATA_COMPONENT_ID);
        const componentUuid = component.length > 0
            ? this.componentHandlerService.getSlotOperationRelatedUuid(component)
            : targetElm.attr('data-component-uuid');
        const componentType = component.length > 0
            ? this.componentHandlerService.getType(component)
            : targetElm.attr('data-component-type');
        const slotOperationRelatedId = component.length > 0
            ? this.componentHandlerService.getSlotOperationRelatedId(component)
            : targetElm.attr(DATA_COMPONENT_ID);
        const slotOperationRelatedType = component.length > 0
            ? this.componentHandlerService.getSlotOperationRelatedType(component)
            : targetElm.attr('data-component-type');
        const slotId = component.length > 0
            ? this.componentHandlerService.getId(slot)
            : targetElm.attr('data-slot-id');
        const slotUuid = component.length > 0
            ? this.componentHandlerService.getId(slot)
            : targetElm.attr('data-slot-uuid');
        const dragInfo = {
            componentId,
            componentUuid,
            componentType,
            slotUuid,
            slotId,
            slotOperationRelatedId,
            slotOperationRelatedType
        };
        component.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.COMPONENT_DRAGGED);
        this.initializeDragOperation(dragInfo);
        this.toggleKeepVisibleComponentAndSlot(true);
    }
    onDragEnter(event) {
        return this.highlightSlot(event);
    }
    onDragOver(event) {
        return __awaiter(this, void 0, void 0, function* () {
            yield this.highlightSlot(event);
            if (!this.highlightedSlot || !this.highlightedSlot.isAllowed) {
                return;
            }
            // Check which component is highlighted
            if (this.highlightedHint) {
                if (this.isMouseInRegion(event, this.highlightedHint)) {
                    // If right hint is already highlighted don't do anything.
                    return;
                }
                else {
                    // Hint is not longer hovered.
                    this.clearHighlightedHint();
                }
            }
            this.highlightHoveredComponentsAndTheirHints(event);
            if (this.highlightedComponent &&
                this.highlightedComponent.id === this.dragInfo.slotOperationRelatedId) {
                this.highlightedComponent.original.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
            }
            else if (this.highlightedHint && this.highlightedSlot.isAllowed) {
                this.highlightedHint.original.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_HOVERED);
                if (this.highlightedSlot.mayBeAllowed) {
                    this.highlightedHint.original.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_MAY_BE_ALLOWED);
                }
            }
        });
    }
    highlightHoveredComponentsAndTheirHints(event) {
        var _a;
        const cachedSlot = this.cachedSlots[this.componentHandlerService.getId(this.highlightedSlot.original)];
        if (cachedSlot.components.length > 0) {
            // Find the hovered component.
            if (!this.highlightedComponent ||
                !this.isMouseInRegion(event, this.highlightedComponent)) {
                this.clearHighlightedComponent();
                // Find the component, if any, to higlight.
                const componentToHighlight = this.selectMouseOverElement(event, cachedSlot.components);
                if (componentToHighlight) {
                    this.highlightedComponent = componentToHighlight;
                }
            }
            // Find the hint, if any, to highlight.
            if ((_a = this.highlightedComponent) === null || _a === void 0 ? void 0 : _a.hints) {
                const hintToHighlight = this.selectMouseOverElement(event, this.highlightedComponent.hints);
                if (hintToHighlight) {
                    this.highlightedHint = hintToHighlight;
                }
            }
        }
    }
    selectMouseOverElement(event, elements) {
        return elements.find((element) => this.isMouseInRegion(event, element));
    }
    onDrop(event) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!this.highlightedSlot) {
                return;
            }
            const sourceSlotId = this.dragInfo.slotId;
            const targetSlotId = this.componentHandlerService.getId(this.highlightedSlot.original);
            const targetSlotUUId = this.componentHandlerService.getUuid(this.highlightedSlot.original);
            const sourceComponentId = this.dragInfo.componentId;
            // if component is dragged from component-menu, there is no slotOperationRelated(Id/Type) available.
            const sourceSlotOperationRelatedId = this.getSlotOperationRelatedIdOrComponentId();
            const componentType = this.getSlotOperationRelatedTypeOrComponentType();
            if (!this.highlightedSlot.isAllowed) {
                this.showNotValidComponentAlert(targetSlotId, sourceSlotOperationRelatedId);
                return;
            }
            if (this.highlightedHint || this.highlightedSlot.components.length === 0) {
                const position = this.getHighlightedHintPosition();
                this.waitDialogService.showWaitModal();
                if (sourceSlotId &&
                    !this.shouldPerformActionWithSourceSlotId(sourceSlotId, targetSlotId, sourceComponentId, position)) {
                    // Do not perform update if position and slot has not changed.
                    this.waitDialogService.hideWaitModal();
                    return;
                }
                try {
                    yield this.performActionToSlotAndHint(sourceSlotId, sourceComponentId, targetSlotId, targetSlotUUId, componentType, position, sourceSlotOperationRelatedId);
                    this.scrollToModifiedSlot(targetSlotId);
                    yield this.crossFrameEventService.publish(cmscommons.DRAG_AND_DROP_EVENTS.SCROLL_TO_MODIFIED_SLOT, targetSlotId);
                }
                catch (_a) {
                    this.onStop(event);
                }
                finally {
                    this.waitDialogService.hideWaitModal();
                }
            }
        });
    }
    performActionToSlotAndHint(sourceSlotId, sourceComponentId, targetSlotId, targetSlotUUId, componentType, position, sourceSlotOperationRelatedId) {
        if (!sourceSlotId) {
            if (!sourceComponentId) {
                return this.getPerformActionWithoutSourceSlotIdWithoutSourceComponentId(targetSlotId, targetSlotUUId, componentType, position);
            }
            else {
                return this.getPerformActionWithoutSourceSlotIdWithSourceComponentId(sourceComponentId, componentType, targetSlotId, position);
            }
        }
        else {
            return this.getPerformActionWithSourceSlotId(sourceSlotId, targetSlotId, sourceComponentId, position, sourceSlotOperationRelatedId);
        }
    }
    getHighlightedHintPosition() {
        return this.highlightedHint ? this.highlightedHint.position : 0;
    }
    getSlotOperationRelatedTypeOrComponentType() {
        return this.dragInfo.slotOperationRelatedType || this.dragInfo.componentType;
    }
    getSlotOperationRelatedIdOrComponentId() {
        return this.dragInfo.slotOperationRelatedId || this.dragInfo.componentId;
    }
    getPerformActionWithSourceSlotId(sourceSlotId, targetSlotId, sourceComponentId, position, sourceSlotOperationRelatedId) {
        if (sourceSlotId === targetSlotId &&
            this.getComponentPositionFromCachedSlot(sourceSlotId, sourceComponentId) < position) {
            // The current component will be removed from its current position, thus the target
            // position needs to take this into account.
            position--;
        }
        return this.componentEditingFacade.moveComponent(sourceSlotId, targetSlotId, sourceSlotOperationRelatedId, position);
    }
    shouldPerformActionWithSourceSlotId(sourceSlotId, targetSlotId, sourceComponentId, position) {
        return (sourceSlotId !== targetSlotId ||
            this.getComponentPositionFromCachedSlot(sourceSlotId, sourceComponentId) !== position);
    }
    getPerformActionWithoutSourceSlotIdWithSourceComponentId(sourceComponentId, componentType, targetSlotId, position) {
        const dragInfo = {
            componentId: sourceComponentId,
            componentUuid: this.dragInfo.componentUuid,
            componentType
        };
        const componentProperties = {
            targetSlotId,
            dragInfo,
            position
        };
        return this.dragInfo.cloneOnDrop
            ? this.componentEditingFacade.cloneExistingComponentToSlot(componentProperties)
            : this.componentEditingFacade.addExistingComponentToSlot(targetSlotId, dragInfo, position);
    }
    getPerformActionWithoutSourceSlotIdWithoutSourceComponentId(targetSlotId, targetSlotUUId, componentType, position) {
        const slotInfo = {
            targetSlotId,
            targetSlotUUId
        };
        const catalogVersionUuid = this.componentHandlerService.getCatalogVersionUuid(this.highlightedSlot.original);
        return this.componentEditingFacade.addNewComponentToSlot(slotInfo, catalogVersionUuid, componentType, position);
    }
    showNotValidComponentAlert(targetSlotId, sourceSlotOperationRelatedId) {
        const translation = this.translateService.instant('se.drag.and.drop.not.valid.component.type', {
            slotUID: targetSlotId,
            componentUID: sourceSlotOperationRelatedId
        });
        this.alertService.showDanger({
            message: translation,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }
    getComponentPositionFromCachedSlot(slotId, componentId) {
        const cachedSlot = this.cachedSlots[slotId];
        const componentsInCachedSlot = (cachedSlot === null || cachedSlot === void 0 ? void 0 : cachedSlot.components) ? cachedSlot.components : [];
        const cachedComponent = componentsInCachedSlot.find((component) => this.componentHandlerService.getId(component.original) === componentId);
        return cachedComponent
            ? cachedComponent.position
            : this.componentHandlerService.getComponentPositionInSlot(slotId, componentId);
    }
    onDragLeave(event) {
        if (this.highlightedSlot) {
            const slotId = this.componentHandlerService.getId(this.highlightedSlot.original);
            const cachedSlot = this.cachedSlots[slotId];
            if (!this.isMouseInRegion(event, cachedSlot)) {
                this.clearHighlightedSlot();
            }
        }
    }
    onStop(event) {
        const component = this.getSelector(event.target).closest(/* @ngInject */ CmsDragAndDropService_1.COMPONENT_SELECTOR);
        this.toggleKeepVisibleComponentAndSlot(false);
        this.cleanDragOperation(component);
        this.systemEventService.publish(smarteditcommons.CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS);
    }
    initializeDragOperation(dragInfo) {
        this.dragInfo = dragInfo;
        this.cacheElements();
        // Prepare UI
        const overlay = this.componentHandlerService.getOverlay();
        overlay.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.OVERLAY_IN_DRAG_DROP);
        // Send an event to signal that the drag operation is started. Other pieces of SE, like contextual menus
        // need to be aware.
        this.systemEventService.publishAsync(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_STARTED);
    }
    cleanDragOperation(draggedComponent) {
        this.clearHighlightedSlot();
        if (draggedComponent) {
            draggedComponent.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.COMPONENT_DRAGGED);
        }
        const overlay = this.componentHandlerService.getOverlay();
        overlay.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.OVERLAY_IN_DRAG_DROP);
        this.systemEventService.publishAsync(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_STOPPED);
        this.dragInfo = null;
        this.cachedSlots = {};
        this.highlightedSlot = null;
    }
    highlightSlot(event) {
        return __awaiter(this, void 0, void 0, function* () {
            const slot = this.yjQuery(event.target).closest(/* @ngInject */ CmsDragAndDropService_1.SLOT_SELECTOR);
            const slotId = this.componentHandlerService.getId(slot);
            const oldSlotId = this.getOldSlotId(slotId);
            if (this.highlightedSlot && this.highlightedSlot.isAllowed !== undefined) {
                return;
            }
            this.highlightedSlot = this.cachedSlots[slotId];
            const dragInfo = Object.assign({}, this.dragInfo);
            // if component is dragged from component-menu, there is no slotOperationRelated(Id/Type) available.
            dragInfo.componentId = this.getSlotOperationRelatedIdOrComponentId();
            dragInfo.componentType = this.getSlotOperationRelatedTypeOrComponentType();
            if (dragInfo.cloneOnDrop) {
                delete dragInfo.componentId;
            }
            const isComponentAllowed = yield this.slotRestrictionsService.isComponentAllowedInSlot(this.highlightedSlot, dragInfo);
            const isSlotEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
            // The highlighted slot might have changed while waiting for the promise to be resolved.
            if (!this.highlightedSlot || this.highlightedSlot.id !== slotId) {
                return;
            }
            this.updateSlotStyle(isComponentAllowed, isSlotEditable, slot);
            if (this.isDragOverNewSlot(event, oldSlotId, slotId)) {
                this.systemEventService.publish(slotId + '_SHOW_SLOT_MENU');
                this.systemEventService.publish(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_OVER, slotId); // can be used to perform any actions on encountering a drag over event.
            }
        });
    }
    isDragOverNewSlot(event, oldSlotId, slotId) {
        return (event.type === 'dragenter' &&
            (!oldSlotId || oldSlotId !== slotId) &&
            this.highlightedSlot &&
            this.highlightedSlot.id === slotId);
    }
    getOldSlotId(slotId) {
        let oldSlotId;
        if (this.highlightedSlot) {
            oldSlotId = this.componentHandlerService.getId(this.highlightedSlot.original);
            if (oldSlotId !== slotId) {
                this.clearHighlightedSlot();
            }
        }
        return oldSlotId;
    }
    updateSlotStyle(isComponentAllowed, isSlotEditable, slot) {
        const isAllowed = isComponentAllowed === smarteditcommons.COMPONENT_IN_SLOT_STATUS.ALLOWED && isSlotEditable;
        const mayBeAllowed = isComponentAllowed === smarteditcommons.COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED && isSlotEditable;
        /**
         * Basically the component could be allowed to drop in the slot if the isComponentAllowed status is either ALLOWED or MAYBEALLOWED.
         * But in order to differentiate between ALLOWED and MAYBEALLOWED, we store it in highlightedSlot.isAllowed and highlightedSlot.mayBeAllowed respectively.
         */
        this.highlightedSlot.isAllowed = isAllowed || mayBeAllowed;
        this.highlightedSlot.mayBeAllowed = mayBeAllowed;
        slot.addClass(this.highlightedSlot.isAllowed
            ? /* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_ALLOWED
            : /* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_NOT_ALLOWED);
        if (mayBeAllowed) {
            slot.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_MAY_BE_ALLOWED);
        }
    }
    addUIHelpers() {
        const overlay = this.componentHandlerService.getOverlay();
        // First remove all dropzones.
        overlay.find('.' + /* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.UI_HELPER_OVERLAY).remove();
        const vm = this;
        overlay.find(/* @ngInject */ CmsDragAndDropService_1.SLOT_SELECTOR).each((i, overlayElement) => {
            const slot = vm.yjQuery(overlayElement);
            const slotHeight = slot[0].offsetHeight;
            const slotWidth = slot[0].offsetWidth;
            const components = slot.find(/* @ngInject */ CmsDragAndDropService_1.COMPONENT_SELECTOR);
            const divElementHtmlStr = '<div></div>';
            if (components.length === 0) {
                const uiHelperOverlay = vm.yjQuery(divElementHtmlStr);
                uiHelperOverlay.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.UI_HELPER_OVERLAY);
                const uiHelper = vm.yjQuery(divElementHtmlStr);
                uiHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE);
                uiHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_FULL);
                uiHelperOverlay.height(slotHeight);
                uiHelperOverlay.width(slotWidth);
                uiHelperOverlay.append(uiHelper);
                slot.append(uiHelperOverlay);
            }
            else {
                components.each((j, componentElement) => {
                    const component = vm.yjQuery(componentElement);
                    const componentHeight = component[0].offsetHeight;
                    const componentWidth = component[0].offsetWidth;
                    const uiHelperOverlay = vm.yjQuery(divElementHtmlStr);
                    uiHelperOverlay.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.UI_HELPER_OVERLAY);
                    uiHelperOverlay.height(componentHeight);
                    uiHelperOverlay.width(componentWidth);
                    const firstHelper = vm.yjQuery(divElementHtmlStr);
                    const secondHelper = vm.yjQuery(divElementHtmlStr);
                    firstHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE);
                    secondHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE);
                    if (componentWidth === slotWidth) {
                        firstHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_TOP);
                        secondHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_BOTTOM);
                    }
                    else {
                        firstHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_LEFT);
                        secondHelper.addClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_RIGHT);
                    }
                    uiHelperOverlay.append(firstHelper);
                    uiHelperOverlay.append(secondHelper);
                    component.append(uiHelperOverlay);
                });
            }
        });
    }
    cacheElements() {
        const overlay = this.componentHandlerService.getOverlay();
        if (!overlay) {
            return;
        }
        const scrollY = this.getWindowScrolling();
        overlay.find(/* @ngInject */ CmsDragAndDropService_1.SLOT_SELECTOR).each((si, slotElement) => {
            const slot = this.yjQuery(slotElement);
            const slotId = this.componentHandlerService.getId(slot);
            const slotUuid = this.componentHandlerService.getUuid(slot);
            // Fetch all components (visible or not) in each slot to get proper position values.
            // The componentHandlerService.getComponentPositionInSlot method is not used here, because it's only based on visible components in the DOM.
            this.pageContentSlotsComponentsRestService
                .getComponentsForSlot(slotId)
                .then((componentsForSlot) => {
                const cachedSlot = {
                    id: slotId,
                    uuid: slotUuid,
                    original: slot,
                    components: [],
                    rect: this.getElementRects(slot, scrollY),
                    hint: null
                };
                const components = slot.children(/* @ngInject */ CmsDragAndDropService_1.COMPONENT_SELECTOR);
                if (components.length === 0) {
                    const hint = slot.find(/* @ngInject */ CmsDragAndDropService_1.HINT_SELECTOR);
                    cachedSlot.hint =
                        hint.length > 0
                            ? {
                                original: hint,
                                rect: this.getElementRects(hint, scrollY)
                            }
                            : null;
                }
                else {
                    components.each((ci, componentElement) => {
                        const component = this.yjQuery(componentElement);
                        let positionInSlot = componentsForSlot.findIndex((componentInSlot) => componentInSlot.uuid ===
                            this.componentHandlerService.getUuid(component));
                        if (positionInSlot === -1) {
                            positionInSlot = ci;
                        }
                        const cachedComponent = {
                            id: this.componentHandlerService.getSlotOperationRelatedId(component),
                            type: this.componentHandlerService.getSlotOperationRelatedType(component),
                            original: component,
                            position: positionInSlot,
                            hints: [],
                            rect: this.getElementRects(component, scrollY)
                        };
                        let positionInComponent = positionInSlot++;
                        component
                            .find(/* @ngInject */ CmsDragAndDropService_1.HINT_SELECTOR)
                            .each((hi, hintElement) => {
                            const hint = this.yjQuery(hintElement);
                            const cachedHint = {
                                original: hint,
                                position: positionInComponent++,
                                rect: this.getElementRects(hint, scrollY)
                            };
                            cachedComponent.hints.push(cachedHint);
                        });
                        cachedSlot.components.push(cachedComponent);
                    });
                }
                this.cachedSlots[cachedSlot.id] = cachedSlot;
            });
        });
    }
    clearHighlightedHint() {
        if (this.highlightedHint) {
            this.highlightedHint.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_HOVERED);
            this.highlightedHint.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.DROPZONE_MAY_BE_ALLOWED);
            this.highlightedHint = null;
        }
    }
    clearHighlightedComponent() {
        this.clearHighlightedHint();
        if (this.highlightedComponent) {
            this.highlightedComponent.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
            this.highlightedComponent = null;
        }
    }
    clearHighlightedSlot() {
        this.clearHighlightedComponent();
        if (this.highlightedSlot) {
            this.highlightedSlot.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_ALLOWED);
            this.highlightedSlot.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_NOT_ALLOWED);
            this.highlightedSlot.original.removeClass(/* @ngInject */ CmsDragAndDropService_1.CSS_CLASSES.SLOT_MAY_BE_ALLOWED);
            this.systemEventService.publish('HIDE_SLOT_MENU');
            this.systemEventService.publish(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_LEAVE); // can be used to perform any actions on encountering a drag leave event.
        }
        this.highlightedSlot = null;
    }
    isMouseInRegion(event, element) {
        const boundingRect = element.rect;
        return (event.pageX >= boundingRect.left &&
            event.pageX <= boundingRect.right &&
            event.pageY >= boundingRect.top &&
            event.pageY <= boundingRect.bottom);
    }
    getElementRects(element, scrollY) {
        const baseRect = element[0].getBoundingClientRect();
        return {
            left: baseRect.left,
            right: baseRect.right,
            bottom: baseRect.bottom + scrollY,
            top: baseRect.top + scrollY
        };
    }
    getWindowScrolling() {
        return this._window.pageYOffset;
    }
    scrollToModifiedSlot(componentId) {
        const component = this.componentHandlerService.getComponentInOverlay(componentId, smarteditcommons.CONTENT_SLOT_TYPE);
        if (component && component.length > 0) {
            component[0].scrollIntoView();
        }
    }
    getSelector(selector) {
        return this.yjQuery(selector);
    }
    /**
     * When a PROCESS_COMPONENTS is occuring, it could remove the currently dragged component if this one is not in the viewport.
     * To avoid having the dragged component and it's slot removed we mark then as "KEEP_VISIBLE" when the drag and drop start.
     * On drag end, an event is sent to call a RESTART_PROCESS to add or remove the components according to their viewport visibility and the component and slot are marked as "PROCESS".
     * Using yjQuery.each() here because of MiniCart component (among other slots/compoents) that have multiple occurences in DOM.
     */
    toggleKeepVisibleComponentAndSlot(keepVisible) {
        if (this.dragInfo) {
            const status = keepVisible
                ? smarteditcommons.CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.KEEP_VISIBLE
                : smarteditcommons.CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS;
            this.yjQuery.each(this.componentHandlerService.getComponentUnderSlot(this.dragInfo.componentId, this.dragInfo.componentType, this.dragInfo.slotId), (i, element) => {
                element.dataset[smarteditcommons.SMARTEDIT_COMPONENT_PROCESS_STATUS] = status;
            });
            this.yjQuery.each(this.componentHandlerService.getComponent(this.dragInfo.slotId, smarteditcommons.CONTENT_SLOT_TYPE), (i, element) => {
                element.dataset[smarteditcommons.SMARTEDIT_COMPONENT_PROCESS_STATUS] = status;
            });
        }
    }
};
/* @ngInject */ exports.CmsDragAndDropService.CMS_DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';
/* @ngInject */ exports.CmsDragAndDropService.TARGET_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type='ContentSlot']";
/* @ngInject */ exports.CmsDragAndDropService.SOURCE_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type!='ContentSlot'] .movebutton";
/* @ngInject */ exports.CmsDragAndDropService.MORE_MENU_SOURCE_SELECTOR = '.movebutton';
/* @ngInject */ exports.CmsDragAndDropService.SLOT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type='ContentSlot']";
/* @ngInject */ exports.CmsDragAndDropService.COMPONENT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type!='ContentSlot']";
/* @ngInject */ exports.CmsDragAndDropService.HINT_SELECTOR = '.overlayDropzone';
/* @ngInject */ exports.CmsDragAndDropService.CSS_CLASSES = {
    UI_HELPER_OVERLAY: 'overlayDnd',
    DROPZONE: 'overlayDropzone',
    DROPZONE_FULL: 'overlayDropzone--full',
    DROPZONE_TOP: 'overlayDropzone--top',
    DROPZONE_BOTTOM: 'overlayDropzone--bottom',
    DROPZONE_LEFT: 'overlayDropzone--left',
    DROPZONE_RIGHT: 'overlayDropzone--right',
    DROPZONE_HOVERED: 'overlayDropzone--hovered',
    DROPZONE_MAY_BE_ALLOWED: 'overlayDropzone--mayBeAllowed',
    OVERLAY_IN_DRAG_DROP: 'smarteditoverlay_dndRendering',
    COMPONENT_DRAGGED: 'component_dragged',
    COMPONENT_DRAGGED_HOVERED: 'component_dragged_hovered',
    SLOTS_MARKED: 'slot-marked',
    SLOT_ALLOWED: 'over-slot-enabled',
    SLOT_NOT_ALLOWED: 'over-slot-disabled',
    SLOT_MAY_BE_ALLOWED: 'over-slot-maybeenabled'
};
/* @ngInject */ exports.CmsDragAndDropService = /* @ngInject */ CmsDragAndDropService_1 = __decorate([
    core.Injectable(),
    __param(12, core.Inject(smarteditcommons.YJQUERY_TOKEN)),
    __param(13, core.Inject(smarteditcommons.DOMAIN_TOKEN)),
    __metadata("design:paramtypes", [smarteditcommons.IAlertService,
        cmscommons.AssetsService,
        smarteditcommons.IBrowserService,
        cmscommons.IComponentEditingFacade,
        smarteditcommons.IComponentHandlerService,
        smarteditcommons.DragAndDropService,
        smarteditcommons.GatewayFactory,
        core$1.TranslateService,
        cmscommons.IPageContentSlotsComponentsRestService,
        smarteditcommons.ISlotRestrictionsService,
        smarteditcommons.SystemEventService,
        smarteditcommons.IWaitDialogService, Function, String, smarteditcommons.CrossFrameEventService])
], /* @ngInject */ exports.CmsDragAndDropService);

const contentSlotComponentsResourceLocation = `${cmscommons.PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI}/pages/:pageId/contentslots/:currentSlotId/components/:componentId`;
/**
 * This service provides methods that allow adding or removing components in the page.
 */
/* @ngInject */ exports.ComponentEditingFacade = class /* @ngInject */ ComponentEditingFacade extends cmscommons.IComponentEditingFacade {
    constructor(alertService, componentService, componentVisibilityAlertService, crossFrameEventService, editorModalService, logService, pageInfoService, renderService, restServiceFactory, slotVisibilityService, sharedDataService, systemEventService, translateService) {
        super();
        this.alertService = alertService;
        this.componentService = componentService;
        this.componentVisibilityAlertService = componentVisibilityAlertService;
        this.crossFrameEventService = crossFrameEventService;
        this.editorModalService = editorModalService;
        this.logService = logService;
        this.pageInfoService = pageInfoService;
        this.renderService = renderService;
        this.restServiceFactory = restServiceFactory;
        this.slotVisibilityService = slotVisibilityService;
        this.sharedDataService = sharedDataService;
        this.systemEventService = systemEventService;
        this.translateService = translateService;
    }
    /**
     * Adds a new component to the slot and opens a component modal to edit its properties.
     *
     * @param slotInfo The target slot for the new component.
     * @param catalogVersionUuid The catalog version on which to create the new component
     * @param componentType The type of the new component to add.
     * @param position The position in the slot where to add the new component.
     *
     */
    addNewComponentToSlot(slotInfo, catalogVersionUuid, componentType, position) {
        return __awaiter(this, void 0, void 0, function* () {
            const componentAttributes = {
                smarteditComponentType: componentType,
                catalogVersionUuid
            };
            const editedComponent = yield this.editorModalService.open(componentAttributes, slotInfo.targetSlotUUId, position);
            this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                itemId: editedComponent.uuid,
                itemType: editedComponent.itemtype,
                catalogVersion: editedComponent.catalogVersion,
                restricted: editedComponent.restricted,
                slotId: slotInfo.targetSlotId,
                visible: editedComponent.visible
            });
            this.crossFrameEventService.publish(cmscommons.COMPONENT_CREATED_EVENT, editedComponent);
            return this.renderSlots([slotInfo.targetSlotId], editedComponent.uid, slotInfo.targetSlotId, true);
        });
    }
    /**
     * Adds an existing component to the slot and display an Alert whenever the component is either hidden or restricted.
     *
     * @param targetSlotId The ID of the slot where to drop the component.
     * @param dragInfo The dragInfo object containing the componentId, componentUuid and componentType.
     * @param position The position in the slot where to add the component.
     */
    addExistingComponentToSlot(targetSlotId, dragInfo, position) {
        return __awaiter(this, void 0, void 0, function* () {
            const pageId = yield this.pageInfoService.getPageUID();
            let item;
            try {
                yield this.componentService.addExistingComponent(pageId, dragInfo.componentId, targetSlotId, position);
                item = yield this.componentService.loadComponentItem(dragInfo.componentUuid);
            }
            catch (error) {
                this.generateAndAlertErrorMessage(dragInfo.componentId, targetSlotId, error);
                return Promise.reject();
            }
            this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                itemId: dragInfo.componentUuid,
                itemType: dragInfo.componentType,
                catalogVersion: item.catalogVersion,
                restricted: item.restricted,
                slotId: targetSlotId,
                visible: item.visible
            });
            // 1. First update the cache.
            this.systemEventService.publish(cmscommons.COMPONENT_UPDATED_EVENT, item);
            // 2. Then replay decorators (via EVENT_SMARTEDIT_COMPONENT_UPDATED).
            // This is important because there might be existing instances of the component in the page that need to
            // be updated. For example, if the component was not shared, it would not show the SharedComponent contextual button.
            // However, if a user adds another instance into the page then the component becomes shared. Both instances of the
            // component must show that the component is shared now. Thus, the first instance needs to be updated too.
            this.crossFrameEventService.publish(smarteditcommons.EVENT_SMARTEDIT_COMPONENT_UPDATED, {
                componentId: dragInfo.componentId,
                componentType: dragInfo.componentType,
                componentUuid: dragInfo.componentUuid,
                requiresReplayingDecorators: true
            });
            return this.renderSlots(targetSlotId, dragInfo.componentId, targetSlotId, true);
        });
    }
    /**
     * This methods clones an existing component to the slot by opening a component modal to edit its properties.
     */
    cloneExistingComponentToSlot(componentInfo) {
        return __awaiter(this, void 0, void 0, function* () {
            const componentItem = yield this.componentService
                .loadComponentItem(componentInfo.dragInfo.componentUuid)
                .catch((error) => {
                this.generateAndAlertErrorMessage(componentInfo.dragInfo.componentId, componentInfo.targetSlotId, error);
                return Promise.reject();
            });
            const experience = (yield this.sharedDataService.get(smarteditcommons.EXPERIENCE_STORAGE_KEY));
            const component = smarteditcommons.objectUtils.copy(componentItem);
            // While cloning an existing components, remove some parameters, reset catalogVersion to the version of the page.
            // If cloning an existing component, prefix name and drop restrictions - doing this here will make generic editor dirty and enable save by default.
            component.componentUuid = component.uuid;
            component.cloneComponent = true;
            component.catalogVersion = experience.pageContext.catalogVersionUuid;
            component.name = `${this.translateService.instant('se.cms.component.name.clone.of.prefix')} ${component.name}`;
            delete component.uuid;
            delete component.uid;
            delete component.slots;
            delete component.restrictions;
            delete component.creationtime;
            delete component.modifiedtime;
            const componentAttributes = {
                smarteditComponentType: componentInfo.dragInfo.componentType,
                catalogVersionUuid: experience.pageContext.catalogVersionUuid,
                content: smarteditcommons.objectUtils.copy(component),
                initialDirty: true
            };
            const updatedComponent = yield this.editorModalService.open(componentAttributes, componentInfo.targetSlotId, componentInfo.position);
            this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                itemId: updatedComponent.uuid,
                itemType: updatedComponent.itemtype,
                catalogVersion: updatedComponent.catalogVersion,
                restricted: updatedComponent.restricted,
                slotId: componentInfo.targetSlotId,
                visible: updatedComponent.visible
            });
            this.crossFrameEventService.publish(cmscommons.COMPONENT_CREATED_EVENT, updatedComponent);
            return this.renderSlots(componentInfo.targetSlotId, updatedComponent.uid, componentInfo.targetSlotId, true);
        });
    }
    /**
     * This methods moves a component from two slots in a page.
     *
     * @param sourceSlotId The ID of the slot where the component is initially located.
     * @param targetSlotId The ID of the slot where to drop the component.
     * @param componentId The ID of the component to add into the slot.
     * @param position The position in the slot where to add the component.
     */
    moveComponent(sourceSlotId, targetSlotId, componentId, position) {
        return __awaiter(this, void 0, void 0, function* () {
            this.contentSlotComponentsRestService =
                this.contentSlotComponentsRestService ||
                    this.restServiceFactory.get(contentSlotComponentsResourceLocation, 'componentId');
            const pageId = yield this.pageInfoService.getPageUID();
            try {
                yield this.contentSlotComponentsRestService.update({
                    pageId,
                    currentSlotId: sourceSlotId,
                    componentId,
                    slotId: targetSlotId,
                    position
                });
            }
            catch (error) {
                this.generateAndAlertErrorMessage(componentId, targetSlotId, error, {
                    message: 'se.cms.draganddrop.move.failed',
                    messagePlaceholders: {
                        slotID: targetSlotId,
                        componentID: componentId
                    },
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
                return Promise.reject();
            }
            return this.renderSlots([sourceSlotId, targetSlotId], componentId, targetSlotId);
        });
    }
    generateAndAlertSuccessMessage(sourceComponentId, targetSlotId) {
        this.alertService.showSuccess({
            message: 'se.cms.draganddrop.success',
            messagePlaceholders: {
                sourceComponentId,
                targetSlotId
            },
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }
    generateAndAlertErrorMessage(sourceComponentId, targetSlotId, requestResponse, alertConf) {
        if (this.hasErrorResponseErrors(requestResponse)) {
            this.alertService.showDanger({
                message: 'se.cms.draganddrop.error',
                messagePlaceholders: {
                    sourceComponentId,
                    targetSlotId,
                    detailedError: requestResponse.error.errors[0].message
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        }
        else if (alertConf) {
            this.alertService.showDanger(alertConf);
        }
    }
    hasErrorResponseErrors(response) {
        var _a, _b;
        return !!(((_b = (_a = response === null || response === void 0 ? void 0 : response.error) === null || _a === void 0 ? void 0 : _a.errors) === null || _b === void 0 ? void 0 : _b.length) > 0);
    }
    renderSlots(slots, sourceComponentId, targetSlotId, showSuccess) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.renderService.renderSlots(slots);
            }
            catch (error) {
                this.logService.error(`${this.constructor.name}.renderSlots::renderService.renderSlots - targetSlotId:`, targetSlotId);
                this.logService.error(error);
                this.generateAndAlertErrorMessage(sourceComponentId, targetSlotId, error);
                return Promise.reject(error);
            }
            try {
                yield this.slotVisibilityService.reloadSlotsInfo();
                if (showSuccess) {
                    this.generateAndAlertSuccessMessage(sourceComponentId, targetSlotId);
                }
            }
            catch (error) {
                this.logService.error(`${this.constructor.name}.renderSlots::slotVisibilityService.reloadSlotsInfo`);
                this.logService.error(error);
                return Promise.reject(error);
            }
            return Promise.resolve();
        });
    }
};
/* @ngInject */ exports.ComponentEditingFacade = __decorate([
    smarteditcommons.GatewayProxied('addNewComponentToSlot', 'addExistingComponentToSlot', 'cloneExistingComponentToSlot', 'moveComponent'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IAlertService,
        cmscommons.ComponentService,
        cmscommons.IComponentVisibilityAlertService,
        smarteditcommons.CrossFrameEventService,
        smarteditcommons.IEditorModalService,
        smarteditcommons.LogService,
        smarteditcommons.IPageInfoService,
        smarteditcommons.IRenderService,
        smarteditcommons.IRestServiceFactory,
        cmscommons.ISlotVisibilityService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.SystemEventService,
        core$1.TranslateService])
], /* @ngInject */ exports.ComponentEditingFacade);

exports.ContextAwareEditableItemService = class ContextAwareEditableItemService extends cmscommons.IContextAwareEditableItemService {
    constructor() {
        super();
    }
};
exports.ContextAwareEditableItemService = __decorate([
    smarteditcommons.GatewayProxied(),
    __metadata("design:paramtypes", [])
], exports.ContextAwareEditableItemService);

/**
 * ContextualMenuDropdownService is an internal service that provides methods for interaction between
 * Drag and Drop Service and the Contextual Menu.
 *
 * Note: The contextualMenuDropdownService functions are as a glue between the Drag and Drop Service and the Contextual Menu.
 *  The service was created to solve the issue of closing any contextual menu that is open whenever a drag operation is started.
 *  It does so while keeping the DnD and Contextual Menu services decoupled.
 */
/* @ngInject */ exports.ContextualMenuDropdownService = class /* @ngInject */ ContextualMenuDropdownService {
    constructor(systemEventService) {
        this.systemEventService = systemEventService;
    }
    registerIsOpenEvent() {
        this.systemEventService.subscribe(smarteditcommons.CTX_MENU_DROPDOWN_IS_OPEN, () => {
            this.registerDragStarted();
        });
    }
    registerDragStarted() {
        this.unsubscribeFn = this.systemEventService.subscribe(cmscommons.DRAG_AND_DROP_EVENTS.DRAG_STARTED, () => {
            this.triggerCloseOperation();
        });
    }
    triggerCloseOperation() {
        this.systemEventService.publishAsync(smarteditcommons.CLOSE_CTX_MENU);
        if (this.unsubscribeFn) {
            this.unsubscribeFn();
        }
    }
};
/* @ngInject */ exports.ContextualMenuDropdownService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService])
], /* @ngInject */ exports.ContextualMenuDropdownService);

/* @ngInject */ exports.EditorModalService = class /* @ngInject */ EditorModalService extends smarteditcommons.IEditorModalService {
};
/* @ngInject */ exports.EditorModalService = __decorate([
    smarteditcommons.GatewayProxied('open', 'openAndRerenderSlot', 'openGenericEditor'),
    core.Injectable()
], /* @ngInject */ exports.EditorModalService);

/* @ngInject */ exports.RemoveComponentService = class /* @ngInject */ RemoveComponentService {
    constructor(restServiceFactory, alertService, componentInfoService, renderService, systemEventService) {
        this.alertService = alertService;
        this.componentInfoService = componentInfoService;
        this.renderService = renderService;
        this.systemEventService = systemEventService;
        this.resource = restServiceFactory.get(`${cmscommons.PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI}/contentslots/:slotId/components/:componentId`, 'componentId');
    }
    removeComponent(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                yield this.resource.remove({
                    slotId: configuration.slotId,
                    componentId: configuration.slotOperationRelatedId
                });
            }
            catch (_a) {
                this.alertService.showDanger({
                    message: 'se.cms.remove.component.failed',
                    messagePlaceholders: {
                        slotID: configuration.slotId,
                        componentID: configuration.slotOperationRelatedId
                    },
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
                return Promise.reject();
            }
            // This call will come from the cache.
            const component = yield this.componentInfoService.getById(configuration.componentUuid);
            this.systemEventService.publish(cmscommons.COMPONENT_REMOVED_EVENT, component);
            this.renderService.renderSlots(configuration.slotId);
            return this.componentInfoService.getById(configuration.componentUuid, true);
        });
    }
};
/* @ngInject */ exports.RemoveComponentService = __decorate([
    smarteditcommons.GatewayProxied('removeComponent'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        smarteditcommons.IAlertService,
        exports.ComponentInfoService,
        smarteditcommons.IRenderService,
        smarteditcommons.SystemEventService])
], /* @ngInject */ exports.RemoveComponentService);

/**
 * The slot visibility service provides methods to manage all backend calls and loads an internal
 * structure that provides the necessary data to the slot visibility button and slot visibility component.
 */
/* @ngInject */ exports.SlotVisibilityService = class /* @ngInject */ SlotVisibilityService extends cmscommons.ISlotVisibilityService {
    constructor(crossFrameEventService, componentHandlerService, logService, pageInfoService, pageContentSlotsComponentsRestService) {
        super();
        this.crossFrameEventService = crossFrameEventService;
        this.componentHandlerService = componentHandlerService;
        this.logService = logService;
        this.pageInfoService = pageInfoService;
        this.pageContentSlotsComponentsRestService = pageContentSlotsComponentsRestService;
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_CREATED_EVENT, () => this.clearComponentsCache());
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_UPDATED_EVENT, () => this.clearComponentsCache());
        this.crossFrameEventService.subscribe(cmscommons.COMPONENT_REMOVED_EVENT, () => this.clearComponentsCache());
    }
    /**
     * Returns the list of hidden components for a given slotId
     *
     * @param slotId the slot id
     *
     * @returns A promise that resolves to a list of hidden components for the slotId
     */
    getHiddenComponents(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            const slots = yield this.getSlotToComponentsMap();
            const filteredSlots = this.filterVisibleComponents(slots);
            return filteredSlots[slotId] || [];
        });
    }
    /**
     * Reloads and cache's the pagesContentSlotsComponents for the current page in context.
     * this method can be called when ever a component is added or modified to the slot so that the pagesContentSlotsComponents is re-evaluated.
     *
     * @returns A promise that resolves to the contentSlot - Components [] map for the page in context.
     */
    reloadSlotsInfo() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const pageUid = yield this.pageInfoService.getPageUID();
                this.pageContentSlotsComponentsRestService.clearCache();
                return this.pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(pageUid);
            }
            catch (exception) {
                this.logService.error('SlotVisibilityService::reloadSlotsInfo - failed call to pageInfoService.getPageUID');
                throw exception;
            }
        });
    }
    /**
     * Function that filters the given SlotsToComponentsMap to return only those components that are hidden in the storefront.
     * @param allSlotsToComponentsMap object containing slotId - components list.
     *
     * @return allSlotsToComponentsMap object containing slotId - components list.
     */
    filterVisibleComponents(allSlotsToComponentsMap) {
        Object.keys(allSlotsToComponentsMap).forEach((slotId) => {
            const jQueryComponents = this.componentHandlerService.getOriginalComponentsWithinSlot(slotId);
            const componentsOnDOM = jQueryComponents
                .get()
                .map((component) => this.componentHandlerService.getId(component));
            const hiddenComponents = allSlotsToComponentsMap[slotId].filter((component) => !componentsOnDOM.includes(component.uid));
            allSlotsToComponentsMap[slotId] = hiddenComponents;
        });
        return allSlotsToComponentsMap;
    }
    /**
     * Function to load slot to component map for the current page in context
     */
    getSlotToComponentsMap() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const pageUid = yield this.pageInfoService.getPageUID();
                return this.pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(pageUid);
            }
            catch (exception) {
                this.logService.error('SlotVisibilityService::getSlotToComponentsMap - failed call to pageInfoService.getPageUID');
                throw exception;
            }
        });
    }
    clearComponentsCache() {
        this.pageContentSlotsComponentsRestService.clearCache();
    }
};
/* @ngInject */ exports.SlotVisibilityService = __decorate([
    smarteditcommons.GatewayProxied('getHiddenComponents'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.CrossFrameEventService,
        smarteditcommons.IComponentHandlerService,
        smarteditcommons.LogService,
        smarteditcommons.IPageInfoService,
        cmscommons.IPageContentSlotsComponentsRestService])
], /* @ngInject */ exports.SlotVisibilityService);

/* @ngInject */ exports.SyncPollingService = class /* @ngInject */ SyncPollingService extends smarteditcommons.ISyncPollingService {
    constructor(systemEventService) {
        super();
        this.systemEventService = systemEventService;
        this.registerSyncPollingEvents();
    }
    registerSyncPollingEvents() {
        this.systemEventService.subscribe(cmscommons.DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP, this.changePollingSpeed.bind(this));
        this.systemEventService.subscribe(cmscommons.DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN, this.changePollingSpeed.bind(this));
    }
};
/* @ngInject */ exports.SyncPollingService = __decorate([
    smarteditcommons.GatewayProxied('getSyncStatus', 'fetchSyncStatus', 'changePollingSpeed', 'registerSyncPollingEvents', 'performSync'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.SystemEventService])
], /* @ngInject */ exports.SyncPollingService);

/* @ngInject */ exports.PageContentSlotsComponentsRestService = class /* @ngInject */ PageContentSlotsComponentsRestService extends cmscommons.IPageContentSlotsComponentsRestService {
    constructor(restServiceFactory, pageInfoService, cmsitemsRestService) {
        super();
        this.pageInfoService = pageInfoService;
        this.cmsitemsRestService = cmsitemsRestService;
        const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${smarteditcommons.PAGE_CONTEXT_SITE_ID}/catalogs/${cmscommons.PAGE_CONTEXT_CATALOG}/versions/${cmscommons.PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscomponents?pageId=:pageId`;
        this.pagesContentSlotsComponentsRestService =
            restServiceFactory.get(contentSlotContainerResourceURI);
    }
    clearCache() {
        return;
    }
    getComponentsForSlot(slotId) {
        return __awaiter(this, void 0, void 0, function* () {
            const pageUID = yield this.pageInfoService.getPageUID();
            const slotsToComponentsMap = yield this.getSlotsToComponentsMapForPageUid(pageUID);
            return slotsToComponentsMap[slotId] || [];
        });
    }
    /**
     * Returns a list of Page Content Slots Components associated to a page.
     *
     * @param pageUid The uid of the page to retrieve the content slots to components map.
     */
    getSlotsToComponentsMapForPageUid(pageUid) {
        return __awaiter(this, void 0, void 0, function* () {
            const response = yield this.getCachedSlotsToComponentsMapForPageUid(pageUid);
            return lodash.cloneDeep(response);
        });
    }
    getCachedSlotsToComponentsMapForPageUid(pageUid) {
        return __awaiter(this, void 0, void 0, function* () {
            const { pageContentSlotComponentList } = yield this.pagesContentSlotsComponentsRestService.get({
                pageId: pageUid
            });
            const componentUuids = this.mapPageContentSlotComponentListToComponentUuids(pageContentSlotComponentList);
            const { response: components } = yield this.cmsitemsRestService.getByIds(componentUuids, 'DEFAULT');
            const uuidToComponentMap = this.createUuidToComponentMap(components);
            // load all components as SlotUuid-Component[] map
            const allSlotsToComponentsMap = this.createSlotUuidToComponentMap(pageContentSlotComponentList, uuidToComponentMap);
            return allSlotsToComponentsMap;
        });
    }
    createSlotUuidToComponentMap(componentList, uuidToComponentMap) {
        return componentList.reduce((map, component) => {
            map[component.slotId] = map[component.slotId] || [];
            if (uuidToComponentMap[component.componentUuid]) {
                map[component.slotId].push(uuidToComponentMap[component.componentUuid]);
            }
            return map;
        }, {});
    }
    mapPageContentSlotComponentListToComponentUuids(componentList) {
        let componentUuids = componentList.map((pageContentSlotComponent) => pageContentSlotComponent.componentUuid);
        componentUuids = Array.from(new Set(componentUuids)); // remove duplicates
        return componentUuids;
    }
    createUuidToComponentMap(components) {
        return (components || []).reduce((map, component) => {
            map[component.uuid] = component;
            return map;
        }, {});
    }
};
__decorate([
    smarteditcommons.InvalidateCache(cmscommons.slotEvictionTag),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", void 0)
], /* @ngInject */ exports.PageContentSlotsComponentsRestService.prototype, "clearCache", null);
__decorate([
    smarteditcommons.Cached({
        actions: [smarteditcommons.rarelyChangingContent],
        tags: [smarteditcommons.cmsitemsEvictionTag, smarteditcommons.pageChangeEvictionTag, cmscommons.slotEvictionTag]
    }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], /* @ngInject */ exports.PageContentSlotsComponentsRestService.prototype, "getCachedSlotsToComponentsMapForPageUid", null);
/* @ngInject */ exports.PageContentSlotsComponentsRestService = __decorate([
    smarteditcommons.GatewayProxied('clearCache', 'getSlotsToComponentsMapForPageUid', 'getComponentsForSlot'),
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        smarteditcommons.IPageInfoService,
        smarteditcommons.CmsitemsRestService])
], /* @ngInject */ exports.PageContentSlotsComponentsRestService);

/* @ngInject */ exports.PageService = class /* @ngInject */ PageService extends smarteditcommons.IPageService {
    constructor() {
        super();
    }
};
/* @ngInject */ exports.PageService = __decorate([
    smarteditcommons.GatewayProxied(),
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ exports.PageService);

/* @ngInject */ exports.OpenNodeInPageTreeService = class /* @ngInject */ OpenNodeInPageTreeService {
    constructor(crossFrameEventService) {
        this.crossFrameEventService = crossFrameEventService;
        this.timeout = null;
    }
    /*
     * When user click the 'Open In Page Tree' button, it will publish the event and the page tree
     * will open the component node on tree.
     * But if the pagetree is not open firstly, EVENT_OPEN_IN_PAGE_TREE will
     * inform storefrontpage to open the page tree. And it should publish the event second time
     * to open the component node on tree.
     * */
    publishOpenEvent(elementUuid) {
        this.timeout && clearTimeout(this.timeout);
        this.crossFrameEventService.publish(smarteditcommons.EVENT_OPEN_IN_PAGE_TREE, elementUuid);
        this.timeout = setTimeout(() => {
            this.crossFrameEventService.publish(smarteditcommons.EVENT_OPEN_IN_PAGE_TREE, elementUuid);
        }, 1000);
    }
};
/* @ngInject */ exports.OpenNodeInPageTreeService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.CrossFrameEventService])
], /* @ngInject */ exports.OpenNodeInPageTreeService);

exports.ComponentMenuConditionAndCallbackService = class ComponentMenuConditionAndCallbackService extends cmscommons.IComponentMenuConditionAndCallbackService {
    constructor(componentHandlerService, typePermissionsRestService, componentVisibilityAlertService, editorModalService, featureService, slotRestrictionsService, confirmationModalService, logService, alertService, removeComponentService, slotVisibilityService, crossFrameEventService, translateService, componentInfoService, cmsitemsRestService, componentEditingFacade, componentSharedService, pageInfoService, catalogService, catalogVersionPermissionService) {
        super();
        this.componentHandlerService = componentHandlerService;
        this.typePermissionsRestService = typePermissionsRestService;
        this.componentVisibilityAlertService = componentVisibilityAlertService;
        this.editorModalService = editorModalService;
        this.featureService = featureService;
        this.slotRestrictionsService = slotRestrictionsService;
        this.confirmationModalService = confirmationModalService;
        this.logService = logService;
        this.alertService = alertService;
        this.removeComponentService = removeComponentService;
        this.slotVisibilityService = slotVisibilityService;
        this.crossFrameEventService = crossFrameEventService;
        this.translateService = translateService;
        this.componentInfoService = componentInfoService;
        this.cmsitemsRestService = cmsitemsRestService;
        this.componentEditingFacade = componentEditingFacade;
        this.componentSharedService = componentSharedService;
        this.pageInfoService = pageInfoService;
        this.catalogService = catalogService;
        this.catalogVersionPermissionService = catalogVersionPermissionService;
    }
    editConditionForHiddenComponent(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            const isSlotEditable = yield this.slotRestrictionsService.isSlotEditable(configuration.slotId);
            if (!isSlotEditable) {
                return false;
            }
            const smarteditCatalogVersionUuid = configuration.componentAttributes &&
                configuration.componentAttributes.smarteditCatalogVersionUuid;
            if (smarteditCatalogVersionUuid) {
                const uuid = yield this.pageInfoService.getCatalogVersionUUIDFromPage();
                const isExternal = smarteditCatalogVersionUuid !== uuid;
                const { catalogId, version } = yield this.catalogService.getCatalogVersionByUuid(smarteditCatalogVersionUuid);
                const isWritable = yield this.catalogVersionPermissionService.hasWritePermission(catalogId, version);
                return isWritable && !isExternal;
            }
            return false;
        });
    }
    sharedCondition(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            let slotId = null;
            if (configuration.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
            }
            else {
                slotId = configuration.slotId;
            }
            if (configuration.isComponentHidden) {
                const pageUuid = yield this.pageInfoService.getCatalogVersionUUIDFromPage();
                if (configuration.componentAttributes.smarteditCatalogVersionUuid !== pageUuid) {
                    return false;
                }
            }
            else {
                const [isExternalComponent, isSlotEditable] = yield Promise.all([
                    this.componentHandlerService.isExternalComponent(configuration.componentId, configuration.componentType),
                    this.slotRestrictionsService.isSlotEditable(slotId)
                ]);
                if (isExternalComponent || !isSlotEditable) {
                    return false;
                }
            }
            return this.componentSharedService.isComponentShared(configuration.componentAttributes.smarteditComponentUuid);
        });
    }
    externalCondition(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            let slotId = null;
            if (!!configuration.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
            }
            else {
                slotId = configuration.slotId;
            }
            const isSlotEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
            if (!isSlotEditable) {
                return false;
            }
            const smarteditCatalogVersionUuid = configuration.componentAttributes &&
                configuration.componentAttributes.smarteditCatalogVersionUuid;
            if (smarteditCatalogVersionUuid) {
                const uuid = yield this.pageInfoService.getCatalogVersionUUIDFromPage();
                return smarteditCatalogVersionUuid !== uuid;
            }
            return this.componentHandlerService.isExternalComponent(configuration.componentId, configuration.componentType);
        });
    }
    removeCondition(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!configuration.isComponentHidden) {
                let slotId = null;
                if (!!configuration.element) {
                    slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
                }
                else {
                    slotId = configuration.slotId;
                }
                const slotEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
                if (slotEditable) {
                    const hasDeletePermission = yield this.typePermissionsRestService.hasDeletePermissionForTypes([
                        configuration.componentType
                    ]);
                    return hasDeletePermission[configuration.componentType];
                }
                return false;
            }
            const hasDeletePermissionOfHidden = yield this.typePermissionsRestService.hasDeletePermissionForTypes([
                configuration.componentType
            ]);
            return hasDeletePermissionOfHidden[configuration.componentType];
        });
    }
    removeCallback(configuration, $event) {
        return __awaiter(this, void 0, void 0, function* () {
            let slotOperationRelatedId;
            let slotOperationRelatedType;
            if (configuration.element) {
                slotOperationRelatedId = this.componentHandlerService.getSlotOperationRelatedId(configuration.element);
                slotOperationRelatedType = this.componentHandlerService.getSlotOperationRelatedType(configuration.element);
            }
            else {
                slotOperationRelatedId = configuration.containerId
                    ? configuration.containerId
                    : configuration.componentId;
                slotOperationRelatedType =
                    configuration.containerId && configuration.containerType
                        ? configuration.containerType
                        : configuration.componentType;
            }
            const confirmed = yield this.confirmationModalService
                .confirm({
                description: 'se.cms.contextmenu.removecomponent.confirmation.message',
                title: 'se.cms.contextmenu.removecomponent.confirmation.title'
            })
                .catch(() => this.logService.log('Confirmation cancelled'));
            if (!confirmed) {
                return;
            }
            yield this.removeComponentService.removeComponent({
                slotId: configuration.slotId,
                slotUuid: configuration.slotUuid,
                componentId: configuration.componentId,
                componentType: configuration.componentType,
                componentUuid: configuration.componentAttributes.smarteditComponentUuid,
                slotOperationRelatedId,
                slotOperationRelatedType
            });
            this.slotVisibilityService.reloadSlotsInfo();
            // This is necessary in case the component was used more than once in the page. If so, those instances need to be updated.
            this.crossFrameEventService.publish(smarteditcommons.EVENT_SMARTEDIT_COMPONENT_UPDATED, {
                componentId: configuration.componentId,
                componentType: configuration.componentType,
                requiresReplayingDecorators: true
            });
            const translation = this.translateService.instant('se.cms.alert.component.removed.from.slot', {
                componentID: slotOperationRelatedId,
                slotID: configuration.slotId
            });
            this.alertService.showSuccess({
                message: translation,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            if (configuration.isComponentHidden) {
                yield this.crossFrameEventService.publish(smarteditcommons.EVENT_PAGE_TREE_SLOT_NEED_UPDATE, configuration.slotId);
            }
            $event.preventDefault();
            $event.stopPropagation();
        });
    }
    cloneCondition(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            const componentUuid = configuration.componentAttributes.smarteditComponentUuid;
            if (!configuration.isComponentHidden) {
                let slotId = null;
                if (!!configuration.element) {
                    slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
                }
                else {
                    slotId = configuration.slotId;
                }
                const isSlotEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
                if (isSlotEditable) {
                    const hasCreatePermission = yield this.typePermissionsRestService.hasCreatePermissionForTypes([
                        configuration.componentType
                    ]);
                    if (hasCreatePermission[configuration.componentType]) {
                        const component = yield this.componentInfoService.getById(componentUuid, true);
                        return component.cloneable;
                    }
                    else {
                        return false;
                    }
                }
                return false;
            }
            const hiddenComponent = yield this.cmsitemsRestService.getById(componentUuid);
            return hiddenComponent.cloneable;
        });
    }
    cloneCallback(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            const sourcePosition = this.componentHandlerService.getComponentPositionInSlot(configuration.slotId, configuration.componentAttributes.smarteditComponentId);
            yield this.componentEditingFacade.cloneExistingComponentToSlot({
                targetSlotId: configuration.slotId,
                dragInfo: {
                    componentId: configuration.componentAttributes.smarteditComponentId,
                    componentType: configuration.componentType,
                    componentUuid: configuration.componentAttributes.smarteditComponentUuid
                },
                position: sourcePosition + 1
            });
            return null;
        });
    }
    dragCondition(configuration) {
        return __awaiter(this, void 0, void 0, function* () {
            let slotId = null;
            if (!!configuration.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
            }
            else {
                slotId = configuration.slotId;
            }
            const slotEditable = yield this.slotRestrictionsService.isSlotEditable(slotId);
            if (slotEditable) {
                const hasUpdatePermission = yield this.typePermissionsRestService.hasUpdatePermissionForTypes([
                    configuration.componentType
                ]);
                return hasUpdatePermission[configuration.componentType];
            }
            return false;
        });
    }
};
exports.ComponentMenuConditionAndCallbackService = __decorate([
    smarteditcommons.GatewayProxied('externalCondition', 'removeCondition', 'removeCallback', 'cloneCondition', 'cloneCallback', 'sharedCondition', 'editConditionForHiddenComponent', 'dragCondition'),
    __metadata("design:paramtypes", [smarteditcommons.IComponentHandlerService,
        cmscommons.TypePermissionsRestService,
        cmscommons.IComponentVisibilityAlertService,
        smarteditcommons.IEditorModalService,
        smarteditcommons.IFeatureService,
        smarteditcommons.ISlotRestrictionsService,
        smarteditcommons.IConfirmationModalService,
        smarteditcommons.LogService,
        smarteditcommons.IAlertService,
        cmscommons.IRemoveComponentService,
        cmscommons.ISlotVisibilityService,
        smarteditcommons.CrossFrameEventService,
        core$1.TranslateService,
        exports.ComponentInfoService,
        smarteditcommons.CmsitemsRestService,
        cmscommons.IComponentEditingFacade,
        cmscommons.IComponentSharedService,
        smarteditcommons.IPageInfoService,
        smarteditcommons.ICatalogService,
        smarteditcommons.ICatalogVersionPermissionService])
], exports.ComponentMenuConditionAndCallbackService);

window.__smartedit__.addDecoratorPayload("Component", "HiddenComponentMenuComponent", {
    selector: 'se-hidden-component-menu',
    template: `<se-popup-overlay class="se-hidden-component-menu__popup-anchor" [popupOverlay]="popupConfig" [popupOverlayTrigger]="isMenuOpen" (popupOverlayOnHide)="hideMenu()"><span *ngIf="menuItems.length > 0" class="sap-icon--overflow se-hidden-component-menu__toggle" (click)="toggle($event)"></span><div se-popup-overlay-body class="se-hidden-component-menu fd-menu__list"><div class="se-hidden-component-menu__item fd-menu__item" *ngFor="let item of menuItems" (click)="executeMenuItemCallback(item, $event)"><span class="se-hidden-component-menu__item-icon" [ngClass]="item.displayIconClass"></span> <span class="se-hidden-component-menu__item-link" [translate]="item.i18nKey"></span></div></div></se-popup-overlay>`,
    styles: [`.se-hidden-component-menu{box-shadow:0 6px 12px rgba(0,0,0,.175);background-color:var(--sapBaseColor);border-radius:var(--sapElement_BorderCornerRadius);min-width:100px;width:fit-content;position:absolute;border:1px solid var(--sapGroup_ContentBorderColor);z-index:2000;top:96%;left:initial!important;right:0}.se-hidden-component-menu::before{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapGroup_ContentBorderColor);border-bottom-color:var(var(--sapField_BorderColor));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-8px}.se-hidden-component-menu::after{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapBaseColor);border-bottom-color:var(var(--sapButton_Attention_Hover_Background));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-7px}.se-hidden-component-menu::after,.se-hidden-component-menu::before{right:10px}.se-hidden-component-menu::after,.se-hidden-component-menu::before{display:none}.se-hidden-component-menu__toggle{cursor:pointer;color:var(--sapIndicationColor_5)}.se-hidden-component-menu__item{display:flex;flex-direction:row;align-items:center;display:block;padding:10px 24px}.se-hidden-component-menu__item-icon{font-size:.75rem!important;line-height:1.3333333333;font-weight:400;margin-right:8px}`],
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let HiddenComponentMenuComponent = class HiddenComponentMenuComponent {
    constructor(componentHandlerService, hiddenComponentMenuService, cdr) {
        this.componentHandlerService = componentHandlerService;
        this.hiddenComponentMenuService = hiddenComponentMenuService;
        this.cdr = cdr;
        this.isMenuOpen = false;
        this.menuItems = [];
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom'
        };
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            const slot = this.componentHandlerService.getOriginalComponent(this.slotId, smarteditcommons.CONTENT_SLOT_TYPE);
            this.slotUuid = this.componentHandlerService.getUuid(slot);
            const hiddenComponentMenu = yield this.hiddenComponentMenuService.getItemsForHiddenComponent(this.component, this.slotId);
            this.configuration = lodash.cloneDeep(hiddenComponentMenu.configuration);
            this.configuration.slotUuid = this.slotUuid;
            this.menuItems = lodash.cloneDeep(hiddenComponentMenu.buttons);
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    toggle(event) {
        event.stopPropagation();
        this.isMenuOpen = !this.isMenuOpen;
    }
    hideMenu() {
        this.isMenuOpen = false;
    }
    executeMenuItemCallback(item, event) {
        item.action.callback(this.configuration, event);
        this.isMenuOpen = false;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], HiddenComponentMenuComponent.prototype, "component", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], HiddenComponentMenuComponent.prototype, "slotId", void 0);
HiddenComponentMenuComponent = __decorate([
    core.Component({
        selector: 'se-hidden-component-menu',
        template: `<se-popup-overlay class="se-hidden-component-menu__popup-anchor" [popupOverlay]="popupConfig" [popupOverlayTrigger]="isMenuOpen" (popupOverlayOnHide)="hideMenu()"><span *ngIf="menuItems.length > 0" class="sap-icon--overflow se-hidden-component-menu__toggle" (click)="toggle($event)"></span><div se-popup-overlay-body class="se-hidden-component-menu fd-menu__list"><div class="se-hidden-component-menu__item fd-menu__item" *ngFor="let item of menuItems" (click)="executeMenuItemCallback(item, $event)"><span class="se-hidden-component-menu__item-icon" [ngClass]="item.displayIconClass"></span> <span class="se-hidden-component-menu__item-link" [translate]="item.i18nKey"></span></div></div></se-popup-overlay>`,
        styles: [`.se-hidden-component-menu{box-shadow:0 6px 12px rgba(0,0,0,.175);background-color:var(--sapBaseColor);border-radius:var(--sapElement_BorderCornerRadius);min-width:100px;width:fit-content;position:absolute;border:1px solid var(--sapGroup_ContentBorderColor);z-index:2000;top:96%;left:initial!important;right:0}.se-hidden-component-menu::before{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapGroup_ContentBorderColor);border-bottom-color:var(var(--sapField_BorderColor));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-8px}.se-hidden-component-menu::after{height:0;width:0;border-style:solid;border-width:0 8px 8px 8px;border-bottom-color:var(--sapBaseColor);border-bottom-color:var(var(--sapButton_Attention_Hover_Background));border-left-color:transparent;border-right-color:transparent;content:"";position:absolute;top:-7px}.se-hidden-component-menu::after,.se-hidden-component-menu::before{right:10px}.se-hidden-component-menu::after,.se-hidden-component-menu::before{display:none}.se-hidden-component-menu__toggle{cursor:pointer;color:var(--sapIndicationColor_5)}.se-hidden-component-menu__item{display:flex;flex-direction:row;align-items:center;display:block;padding:10px 24px}.se-hidden-component-menu__item-icon{font-size:.75rem!important;line-height:1.3333333333;font-weight:400;margin-right:8px}`],
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [smarteditcommons.IComponentHandlerService,
        exports.HiddenComponentMenuService,
        core.ChangeDetectorRef])
], HiddenComponentMenuComponent);

window.__smartedit__.addDecoratorPayload("Component", "SlotVisibilityComponent", {
    selector: 'se-slot-visibility-component',
    template: `<div class="se-slot-visibility-wrapper"><div *ngIf="isReady" class="se-slot-visiblity-component__content"><div class="se-slot-visibility__icon-wrapper"><div class="se-slot-visibility__icon sap-icon--home-share"></div><div *ngIf="isSharedComponent" class="se-slot-visibility__icon--shared sap-icon--chain-link"></div></div><div class="se-slot-visiblity-component__description"><div *ngIf="readOnly" class="se-slot-visiblity-component__name">{{ component.name }}</div><div *ngIf="!readOnly" class="se-slot-visiblity-component__name--link" (click)="openEditorModal()">{{ component.name }}</div><div class="se-slot-visiblity-component__type">{{ component.typeCode }}</div><div class="se-slot-visiblity-component__visibility">{{ 'se.genericeditor.tab.visibility.title' | translate }} {{ componentVisibilitySwitch | translate }} / {{ componentRestrictionsCount }} {{ 'se.cms.restrictions.editor.tab' | translate }}</div><div *ngIf="component.isExternal" class="sap-icon--globe se-slot-visiblity-component--globe-icon"></div></div></div><se-hidden-component-menu [slotId]="slotId" [component]="component"></se-hidden-component-menu></div>`,
    styles: [`.se-slot-visiblity-component--globe-icon{margin-left:4px}.se-slot-visiblity-component__content{display:flex;flex-direction:row;align-items:center;flex-grow:1}.se-slot-visiblity-component__description{margin-right:16px}.se-slot-visiblity-component__name,.se-slot-visiblity-component__visibility{color:var(--sapTextColor)}.se-slot-visiblity-component__name--link{cursor:pointer;color:var(--sapLinkColor)}.se-slot-visiblity-component__type{color:var(--sapNeutralColor)}`],
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let SlotVisibilityComponent = class SlotVisibilityComponent {
    constructor(catalogService, componentSharedService, editorModalService, componentVisibilityAlertService, catalogVersionPermissionService, logService, cdr, userTrackingService) {
        this.catalogService = catalogService;
        this.componentSharedService = componentSharedService;
        this.editorModalService = editorModalService;
        this.componentVisibilityAlertService = componentVisibilityAlertService;
        this.catalogVersionPermissionService = catalogVersionPermissionService;
        this.logService = logService;
        this.cdr = cdr;
        this.userTrackingService = userTrackingService;
        this.isReady = false;
        this.isSharedComponent = false;
        this.readOnly = false;
    }
    ngOnInit() {
        var _a;
        return __awaiter(this, void 0, void 0, function* () {
            this.isSharedComponent = yield this.componentSharedService.isComponentShared(this.component);
            const { catalogId, version } = yield this.catalogService.getCatalogVersionByUuid(this.component.catalogVersion);
            const isWritable = yield this.catalogVersionPermissionService.hasWritePermission(catalogId, version);
            this.readOnly = !isWritable || this.component.isExternal;
            this.componentVisibilitySwitch = this.component.visible
                ? 'se.cms.component.visibility.status.on'
                : 'se.cms.component.visibility.status.off';
            const count = ((_a = this.component.restrictions) === null || _a === void 0 ? void 0 : _a.length) || 0;
            this.componentRestrictionsCount = `(${count})`;
            this.isReady = true;
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    ngOnChanges(changes) {
        const componentChange = changes.component;
        if (componentChange) {
            this.componentId = this.component.uid;
        }
    }
    openEditorModal() {
        return __awaiter(this, void 0, void 0, function* () {
            this.userTrackingService.trackingUserAction(smarteditcommons.USER_TRACKING_FUNCTIONALITY.CONTEXT_MENU, 'Slot Visibility');
            try {
                const item = yield this.editorModalService.openAndRerenderSlot(this.component.typeCode, this.component.uuid, 'visible');
                this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                    itemId: item.uuid,
                    itemType: item.itemtype,
                    catalogVersion: item.catalogVersion,
                    restricted: item.restricted,
                    slotId: this.slotId,
                    visible: item.visible
                });
            }
            catch (error) {
                if (error !== undefined) {
                    this.logService.warn('Something went wrong with openAndRerenderSlot method.', error);
                }
            }
        });
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SlotVisibilityComponent.prototype, "component", void 0);
__decorate([
    core.HostBinding('attr.data-component-id'),
    __metadata("design:type", String)
], SlotVisibilityComponent.prototype, "componentId", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], SlotVisibilityComponent.prototype, "slotId", void 0);
SlotVisibilityComponent = __decorate([
    core.Component({
        selector: 'se-slot-visibility-component',
        template: `<div class="se-slot-visibility-wrapper"><div *ngIf="isReady" class="se-slot-visiblity-component__content"><div class="se-slot-visibility__icon-wrapper"><div class="se-slot-visibility__icon sap-icon--home-share"></div><div *ngIf="isSharedComponent" class="se-slot-visibility__icon--shared sap-icon--chain-link"></div></div><div class="se-slot-visiblity-component__description"><div *ngIf="readOnly" class="se-slot-visiblity-component__name">{{ component.name }}</div><div *ngIf="!readOnly" class="se-slot-visiblity-component__name--link" (click)="openEditorModal()">{{ component.name }}</div><div class="se-slot-visiblity-component__type">{{ component.typeCode }}</div><div class="se-slot-visiblity-component__visibility">{{ 'se.genericeditor.tab.visibility.title' | translate }} {{ componentVisibilitySwitch | translate }} / {{ componentRestrictionsCount }} {{ 'se.cms.restrictions.editor.tab' | translate }}</div><div *ngIf="component.isExternal" class="sap-icon--globe se-slot-visiblity-component--globe-icon"></div></div></div><se-hidden-component-menu [slotId]="slotId" [component]="component"></se-hidden-component-menu></div>`,
        styles: [`.se-slot-visiblity-component--globe-icon{margin-left:4px}.se-slot-visiblity-component__content{display:flex;flex-direction:row;align-items:center;flex-grow:1}.se-slot-visiblity-component__description{margin-right:16px}.se-slot-visiblity-component__name,.se-slot-visiblity-component__visibility{color:var(--sapTextColor)}.se-slot-visiblity-component__name--link{cursor:pointer;color:var(--sapLinkColor)}.se-slot-visiblity-component__type{color:var(--sapNeutralColor)}`],
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogService,
        cmscommons.IComponentSharedService,
        smarteditcommons.IEditorModalService,
        cmscommons.IComponentVisibilityAlertService,
        smarteditcommons.ICatalogVersionPermissionService,
        smarteditcommons.LogService,
        core.ChangeDetectorRef,
        smarteditcommons.IUserTrackingService])
], SlotVisibilityComponent);

window.__smartedit__.addDecoratorPayload("Component", "SlotVisibilityButtonComponent", {
    selector: 'se-slot-visibility-button',
    template: `<se-popup-overlay *ngIf="buttonVisible" class="se-slot-visibility" [popupOverlay]="popupConfig" [popupOverlayTrigger]="isPopupOpened" (popupOverlayOnHide)="hideMenu()"><button type="button" id="slot-visibility-button-{{ slotId }}" class="se-slot-ctx-menu__dropdown-toggle se-slot-ctx-menu__dropdown-toggle--slot-visibility" [ngClass]="{
            'se-slot-ctx-menu__dropdown-toggle--open': isPopupOpened
        }" (click)="toggleMenu()" title="{{ getSlotVisibilityButtonTitle() | translate }}"><span class="se-slot-ctx-menu__dropdown-toggle-icon sap-icon--hide"></span> <span class="se-slot-ctx-menu__dropdown-toggle-add-on">{{ hiddenComponentCount }}</span></button><div se-popup-overlay-body id="slot-visibility-list-{{ slotId }}" class="se-slot__dropdown-menu" (click)="onInsideClick($event)"><div class="se-slot-contextual-menu__header" translate="se.cms.slotvisibility.list.title"></div><ul class="se-slot-visibility__component-list"><li *ngFor="let component of hiddenComponents; trackBy: hiddenComponentTrackBy" class="se-slot-visibility__component-list-item"><se-tooltip *ngIf="component.isExternal" [placement]="'top'" [triggers]="['mouseenter', 'mouseleave']"><se-slot-visibility-component se-tooltip-trigger [component]="component" [slotId]="slotId"></se-slot-visibility-component><span se-tooltip-body translate="se.cms.slotvisibility.external.component"></span></se-tooltip><se-slot-visibility-component *ngIf="!component.isExternal" [component]="component" [slotId]="slotId"></se-slot-visibility-component></li></ul></div></se-popup-overlay>`,
    styles: [`#smarteditoverlay .se-slot-visibility__component-list,.cdk-overlay-container .se-slot-visibility__component-list{padding:16px 20px;list-style:none}#smarteditoverlay .se-slot-visibility__component-list-item,.cdk-overlay-container .se-slot-visibility__component-list-item{margin-bottom:16px}#smarteditoverlay .se-slot-visibility__component-list-item:last-child,.cdk-overlay-container .se-slot-visibility__component-list-item:last-child{margin-bottom:0}`],
    encapsulation: core.ViewEncapsulation.None,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let SlotVisibilityButtonComponent = class SlotVisibilityButtonComponent {
    constructor(contextualMenuItem, slotVisibilityService, sharedDataService, crossFrameEventService, cdr) {
        this.contextualMenuItem = contextualMenuItem;
        this.slotVisibilityService = slotVisibilityService;
        this.sharedDataService = sharedDataService;
        this.crossFrameEventService = crossFrameEventService;
        this.cdr = cdr;
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = this.isPopupOpened;
        this.buttonVisible = false;
        this.hiddenComponents = [];
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__dropdown-toggle-wrapper',
                'se-slot-ctx-menu__dropdown-toggle-wrapper--slot-visibility',
                'se-slot-ctx-menu__divider'
            ]
        };
        this.buttonName = 'slotVisibilityButton';
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(smarteditcommons.EVENT_OUTER_FRAME_CLICKED, () => {
                this.isPopupOpened = false;
            });
            const hiddenComponents = yield this.slotVisibilityService.getHiddenComponents(this.slotId);
            const experience = (yield this.sharedDataService.get('experience'));
            this.hiddenComponents = this.markExternalComponents(experience, hiddenComponents);
            this.hiddenComponentCount = hiddenComponents.length;
            if (this.hiddenComponentCount > 0) {
                this.buttonVisible = true;
            }
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    ngOnDestroy() {
        if (this.unRegOuterFrameClicked) {
            this.unRegOuterFrameClicked();
        }
    }
    ngDoCheck() {
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
        }
    }
    get slotId() {
        return this.contextualMenuItem.componentAttributes.smarteditComponentId;
    }
    hiddenComponentTrackBy(index, _hiddenComponent) {
        return index;
    }
    toggleMenu() {
        this.isPopupOpened = !this.isPopupOpened;
    }
    hideMenu() {
        this.isPopupOpened = false;
    }
    onInsideClick(event) {
        event.stopPropagation();
    }
    getSlotVisibilityButtonTitle() {
        return this.buttonVisible
            ? 'se.cms.pagetree.component.menu.icon.hide.title'
            : 'se.cms.pagetree.component.menu.icon.visible.title';
    }
    markExternalComponents(experience, hiddenComponents) {
        return hiddenComponents.map((component) => (Object.assign(Object.assign({}, component), { isExternal: component.catalogVersion !== experience.pageContext.catalogVersionUuid })));
    }
};
SlotVisibilityButtonComponent = __decorate([
    core.Component({
        selector: 'se-slot-visibility-button',
        template: `<se-popup-overlay *ngIf="buttonVisible" class="se-slot-visibility" [popupOverlay]="popupConfig" [popupOverlayTrigger]="isPopupOpened" (popupOverlayOnHide)="hideMenu()"><button type="button" id="slot-visibility-button-{{ slotId }}" class="se-slot-ctx-menu__dropdown-toggle se-slot-ctx-menu__dropdown-toggle--slot-visibility" [ngClass]="{
            'se-slot-ctx-menu__dropdown-toggle--open': isPopupOpened
        }" (click)="toggleMenu()" title="{{ getSlotVisibilityButtonTitle() | translate }}"><span class="se-slot-ctx-menu__dropdown-toggle-icon sap-icon--hide"></span> <span class="se-slot-ctx-menu__dropdown-toggle-add-on">{{ hiddenComponentCount }}</span></button><div se-popup-overlay-body id="slot-visibility-list-{{ slotId }}" class="se-slot__dropdown-menu" (click)="onInsideClick($event)"><div class="se-slot-contextual-menu__header" translate="se.cms.slotvisibility.list.title"></div><ul class="se-slot-visibility__component-list"><li *ngFor="let component of hiddenComponents; trackBy: hiddenComponentTrackBy" class="se-slot-visibility__component-list-item"><se-tooltip *ngIf="component.isExternal" [placement]="'top'" [triggers]="['mouseenter', 'mouseleave']"><se-slot-visibility-component se-tooltip-trigger [component]="component" [slotId]="slotId"></se-slot-visibility-component><span se-tooltip-body translate="se.cms.slotvisibility.external.component"></span></se-tooltip><se-slot-visibility-component *ngIf="!component.isExternal" [component]="component" [slotId]="slotId"></se-slot-visibility-component></li></ul></div></se-popup-overlay>`,
        styles: [`#smarteditoverlay .se-slot-visibility__component-list,.cdk-overlay-container .se-slot-visibility__component-list{padding:16px 20px;list-style:none}#smarteditoverlay .se-slot-visibility__component-list-item,.cdk-overlay-container .se-slot-visibility__component-list-item{margin-bottom:16px}#smarteditoverlay .se-slot-visibility__component-list-item:last-child,.cdk-overlay-container .se-slot-visibility__component-list-item:last-child{margin-bottom:0}`],
        encapsulation: core.ViewEncapsulation.None,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __param(0, core.Inject(smarteditcommons.CONTEXTUAL_MENU_ITEM_DATA)),
    __metadata("design:paramtypes", [Object, cmscommons.ISlotVisibilityService,
        smarteditcommons.ISharedDataService,
        smarteditcommons.CrossFrameEventService,
        core.ChangeDetectorRef])
], SlotVisibilityButtonComponent);

window.__smartedit__.addDecoratorPayload("Component", "ExternalSlotDisabledDecoratorComponent", {
    selector: 'external-slot-disabled-decorator',
    template: `<div class="se-decorator-wrap"><slot-disabled-component [active]="active" [componentAttributes]="componentAttributes"></slot-disabled-component></div>`
});
let ExternalSlotDisabledDecoratorComponent = class ExternalSlotDisabledDecoratorComponent extends smarteditcommons.AbstractDecorator {
    constructor() {
        super();
    }
};
ExternalSlotDisabledDecoratorComponent = __decorate([
    smarteditcommons.SeCustomComponent(),
    core.Component({
        selector: 'external-slot-disabled-decorator',
        template: `<div class="se-decorator-wrap"><slot-disabled-component [active]="active" [componentAttributes]="componentAttributes"></slot-disabled-component></div>`
    }),
    __metadata("design:paramtypes", [])
], ExternalSlotDisabledDecoratorComponent);

window.__smartedit__.addDecoratorPayload("Component", "SharedSlotDisabledDecoratorComponent", {
    selector: 'shared-slot-disabled-decorator',
    template: `<div class="se-decorator-wrap"><slot-disabled-component [active]="active" [componentAttributes]="componentAttributes"></slot-disabled-component><div><ng-content></ng-content></div></div>`
});
let SharedSlotDisabledDecoratorComponent = class SharedSlotDisabledDecoratorComponent extends smarteditcommons.AbstractDecorator {
    constructor() {
        super();
    }
};
SharedSlotDisabledDecoratorComponent = __decorate([
    smarteditcommons.SeCustomComponent(),
    core.Component({
        selector: 'shared-slot-disabled-decorator',
        template: `<div class="se-decorator-wrap"><slot-disabled-component [active]="active" [componentAttributes]="componentAttributes"></slot-disabled-component><div><ng-content></ng-content></div></div>`
    }),
    __metadata("design:paramtypes", [])
], SharedSlotDisabledDecoratorComponent);

var IconClass;
(function (IconClass) {
    IconClass["GLOBE"] = "sap-icon--globe";
    IconClass["HOVERED"] = "sap-icon--chain-link";
})(IconClass || (IconClass = {}));
window.__smartedit__.addDecoratorPayload("Component", "SlotDisabledComponent", {
    selector: 'slot-disabled-component',
    template: `<div class="se-slot-popover" [ngClass]="{ 
        'disabled-shared-slot': !isVersioningPerspective,
        'disabled-shared-slot-hovered': active, 
        'disabled-shared-slot-versioning-mode': isVersioningPerspective, 
        'external-shared-slot': isGlobalSlot
    }" [attr.data-popover-content]="popoverMessage?.translate | translate: popoverMessage?.translateParams"><div class="se-slot-popover__arrow"><div class="se-slot-popover__arrow--fill"></div></div><div class="disabled-shared-slot__icon--outer disabled-shared-slot__icon--outer-linked" [ngClass]="outerSlotClass"><span class="shared_slot_disabled_icon disabled-shared-slot__icon--inner" [ngClass]="iconClass"></span></div></div>`,
    styles: [`#smarteditoverlay .se-slot-popover{align-items:center;display:flex;flex-direction:column-reverse;justify-content:center}#smarteditoverlay .se-slot-popover__arrow{opacity:0;margin-top:15px;position:relative;width:0;border-bottom:12px solid var(--sapGroup_ContentBorderColor);border-right:12px solid transparent;border-left:12px solid transparent;z-index:9999}#smarteditoverlay .se-slot-popover__arrow--fill{position:absolute;top:1px;left:-11px;width:0;border-bottom:11px solid var(--sapBaseColor);border-right:11px solid transparent;border-left:11px solid transparent}#smarteditoverlay .se-slot-popover:before{opacity:0;box-shadow:0 0 4px 0 var(--sapGroup_ContentBorderColor);z-index:9999;background-color:var(--sapBaseColor);color:var(--sapTextColor);border:1px solid var(--sapGroup_ContentBorderColor);border-radius:4px;content:attr(data-popover-content);margin-bottom:-78px;padding:10px;white-space:nowrap}#smarteditoverlay [data-smartedit-component-type=ContentSlot]:hover .se-slot-popover:before{opacity:1;margin-top:-1px}#smarteditoverlay [data-smartedit-component-type=ContentSlot]:hover .se-slot-popover__arrow{opacity:1}`],
    encapsulation: core.ViewEncapsulation.None,
    providers: [smarteditcommons.L10nPipe],
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let SlotDisabledComponent = class SlotDisabledComponent {
    constructor(catalogService, cMSModesService, l10nPipe, slotSharedService, cdr) {
        this.catalogService = catalogService;
        this.cMSModesService = cMSModesService;
        this.l10nPipe = l10nPipe;
        this.slotSharedService = slotSharedService;
        this.cdr = cdr;
        this.DEFAULT_DECORATOR_MSG = 'se.cms.sharedslot.decorator.label';
        this.GLOBAL_SLOT_DECORATOR_MSG = 'se.cms.parentsharedslot.decorator.label';
        this.DEFAULT_DECORATOR_MSG_VERSIONING_MODE = 'se.cms.versioning.shared.slot.from.label';
        this.GLOBAL_SLOT_DECORATOR_MSG_VERSIONING_MODE = 'se.cms.versioning.parent.shared.slot.from.label';
        this.isGlobalSlot = false;
        this.isVersioningPerspective = false;
    }
    ngOnInit() {
        return __awaiter(this, void 0, void 0, function* () {
            const [catalogVersion, isVersioningPerspective] = yield Promise.all([
                this.catalogService.getCatalogVersionByUuid(this.componentAttributes.smarteditCatalogVersionUuid),
                this.cMSModesService.isVersioningPerspectiveActive()
            ]);
            this.isVersioningPerspective = isVersioningPerspective;
            this.isGlobalSlot = yield this.slotSharedService.isGlobalSlot(this.componentAttributes.smarteditComponentId, this.componentAttributes.smarteditComponentType);
            this.popoverMessage = yield this.getPopoverMessage(catalogVersion.catalogName);
            this.setIconAndOuterSlotClassName();
            if (!this.cdr.destroyed) {
                this.cdr.detectChanges();
            }
        });
    }
    ngOnChanges(changes) {
        const activeChange = changes.active;
        if (activeChange && !activeChange.firstChange) {
            this.setIconAndOuterSlotClassName();
        }
    }
    getPopoverMessage(catalogNameL10n) {
        return __awaiter(this, void 0, void 0, function* () {
            let msgToLocalize;
            if (this.isVersioningPerspective) {
                msgToLocalize = this.isGlobalSlot
                    ? this.GLOBAL_SLOT_DECORATOR_MSG_VERSIONING_MODE
                    : this.DEFAULT_DECORATOR_MSG_VERSIONING_MODE;
            }
            else {
                msgToLocalize = this.isGlobalSlot
                    ? this.GLOBAL_SLOT_DECORATOR_MSG
                    : this.DEFAULT_DECORATOR_MSG;
            }
            const catalogName = yield this.l10nPipe
                .transform(catalogNameL10n)
                .pipe(operators.take(1))
                .toPromise();
            const msgParams = {
                slotId: this.componentAttributes.smarteditComponentId,
                catalogName
            };
            return { translate: msgToLocalize, translateParams: msgParams };
        });
    }
    setIconAndOuterSlotClassName() {
        this.iconClass = this.isGlobalSlot ? IconClass.GLOBE : IconClass.HOVERED;
        this.outerSlotClass = this.getOuterSlotClass(this.active, this.iconClass);
    }
    getOuterSlotClass(active, iconClass) {
        return {
            'disabled-shared-slot__icon--outer-hovered': active && !this.isVersioningPerspective,
            'disabled-shared-slot__icon--outer-globe': iconClass === IconClass.GLOBE
        };
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], SlotDisabledComponent.prototype, "componentAttributes", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], SlotDisabledComponent.prototype, "active", void 0);
SlotDisabledComponent = __decorate([
    core.Component({
        selector: 'slot-disabled-component',
        template: `<div class="se-slot-popover" [ngClass]="{ 
        'disabled-shared-slot': !isVersioningPerspective,
        'disabled-shared-slot-hovered': active, 
        'disabled-shared-slot-versioning-mode': isVersioningPerspective, 
        'external-shared-slot': isGlobalSlot
    }" [attr.data-popover-content]="popoverMessage?.translate | translate: popoverMessage?.translateParams"><div class="se-slot-popover__arrow"><div class="se-slot-popover__arrow--fill"></div></div><div class="disabled-shared-slot__icon--outer disabled-shared-slot__icon--outer-linked" [ngClass]="outerSlotClass"><span class="shared_slot_disabled_icon disabled-shared-slot__icon--inner" [ngClass]="iconClass"></span></div></div>`,
        styles: [`#smarteditoverlay .se-slot-popover{align-items:center;display:flex;flex-direction:column-reverse;justify-content:center}#smarteditoverlay .se-slot-popover__arrow{opacity:0;margin-top:15px;position:relative;width:0;border-bottom:12px solid var(--sapGroup_ContentBorderColor);border-right:12px solid transparent;border-left:12px solid transparent;z-index:9999}#smarteditoverlay .se-slot-popover__arrow--fill{position:absolute;top:1px;left:-11px;width:0;border-bottom:11px solid var(--sapBaseColor);border-right:11px solid transparent;border-left:11px solid transparent}#smarteditoverlay .se-slot-popover:before{opacity:0;box-shadow:0 0 4px 0 var(--sapGroup_ContentBorderColor);z-index:9999;background-color:var(--sapBaseColor);color:var(--sapTextColor);border:1px solid var(--sapGroup_ContentBorderColor);border-radius:4px;content:attr(data-popover-content);margin-bottom:-78px;padding:10px;white-space:nowrap}#smarteditoverlay [data-smartedit-component-type=ContentSlot]:hover .se-slot-popover:before{opacity:1;margin-top:-1px}#smarteditoverlay [data-smartedit-component-type=ContentSlot]:hover .se-slot-popover__arrow{opacity:1}`],
        encapsulation: core.ViewEncapsulation.None,
        providers: [smarteditcommons.L10nPipe],
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [smarteditcommons.ICatalogService,
        smarteditcommons.CMSModesService,
        smarteditcommons.L10nPipe,
        smarteditcommons.SlotSharedService,
        core.ChangeDetectorRef])
], SlotDisabledComponent);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
let CmsComponentsModule = class CmsComponentsModule {
};
CmsComponentsModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            smarteditcommons.TooltipModule,
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.PopupOverlayModule,
            core$2.ButtonModule
        ],
        declarations: [
            ExternalComponentDecoratorComponent,
            SlotDisabledComponent,
            ExternalSlotDisabledDecoratorComponent,
            SharedSlotDisabledDecoratorComponent,
            HiddenComponentMenuComponent,
            SlotVisibilityComponent,
            SlotVisibilityButtonComponent,
            SyncIndicatorDecorator
        ],
        entryComponents: [
            ExternalComponentDecoratorComponent,
            SlotDisabledComponent,
            ExternalSlotDisabledDecoratorComponent,
            SharedSlotDisabledDecoratorComponent,
            HiddenComponentMenuComponent,
            SlotVisibilityComponent,
            SlotVisibilityButtonComponent,
            SyncIndicatorDecorator
        ]
    })
], CmsComponentsModule);

const REGEXPKEYS = '^((?!Slot).)*$';
exports.CmssmarteditModule = class CmssmarteditModule {
};
exports.CmssmarteditModule = __decorate([
    smarteditcommons.SeEntryModule('cmssmartedit'),
    core.NgModule({
        imports: [platformBrowser.BrowserModule, smarteditcommons.SeTranslationModule.forChild(), CmsComponentsModule, cmscommons.CmsCommonsModule],
        providers: [
            exports.ContextualMenuDropdownService,
            smarteditcommons.PageContentSlotsService,
            cmscommons.SlotUnsharedService,
            exports.SlotContainerService,
            exports.HiddenComponentMenuService,
            exports.ComponentInfoService,
            cmscommons.SlotSynchronizationService,
            smarteditcommons.SlotSharedService,
            {
                provide: cmscommons.IEditorEnablerService,
                useClass: exports.EditorEnablerService
            },
            {
                provide: cmscommons.IComponentEditingFacade,
                useClass: exports.ComponentEditingFacade
            },
            exports.CmsDragAndDropService,
            exports.OpenNodeInPageTreeService,
            {
                provide: cmscommons.IPageContentSlotsComponentsRestService,
                useClass: exports.PageContentSlotsComponentsRestService
            },
            {
                provide: smarteditcommons.ISyncPollingService,
                useClass: exports.SyncPollingService
            },
            {
                provide: cmscommons.IRemoveComponentService,
                useClass: exports.RemoveComponentService
            },
            {
                provide: smarteditcommons.IPageService,
                useClass: exports.PageService
            },
            {
                provide: cmscommons.IContextAwareEditableItemService,
                useClass: exports.ContextAwareEditableItemService
            },
            {
                provide: smarteditcommons.IEditorModalService,
                useClass: exports.EditorModalService
            },
            {
                provide: cmscommons.IComponentVisibilityAlertService,
                useClass: exports.ComponentVisibilityAlertService
            },
            {
                provide: cmscommons.IComponentSharedService,
                useClass: exports.ComponentSharedService
            },
            {
                provide: smarteditcommons.ISlotRestrictionsService,
                useClass: exports.SlotRestrictionsService
            },
            {
                provide: cmscommons.ISlotVisibilityService,
                useClass: exports.SlotVisibilityService
            },
            {
                provide: cmscommons.IComponentMenuConditionAndCallbackService,
                useClass: exports.ComponentMenuConditionAndCallbackService
            },
            smarteditcommons.moduleUtils.bootstrap((contextualMenuDropdownService, editorEnablerService, decoratorService, featureService, componentHandlerService, slotRestrictionsService, pageInfoService, typePermissionsRestService, cmsDragAndDropService, slotSharedService, settingsService, openNodeInPageTreeService, componentMenuConditionAndCallbackService) => {
                contextualMenuDropdownService.registerIsOpenEvent();
                editorEnablerService.enableForComponents([REGEXPKEYS]);
                decoratorService.addMappings({
                    '^((?!Slot).)*$': ['se.contextualMenu', 'externalComponentDecorator'],
                    '^.*Slot$': [
                        'se.slotContextualMenu',
                        'se.basicSlotContextualMenu',
                        'syncIndicator',
                        'sharedSlotDisabledDecorator',
                        'externalSlotDisabledDecorator'
                    ]
                });
                addContextualMenuButtonForFeature(featureService, {
                    componentMenuConditionAndCallbackService,
                    cmsDragAndDropService,
                    openNodeInPageTreeService
                });
                registerForFeature(featureService, { cmsDragAndDropService, slotSharedService });
                addDecoratorForFeature(featureService, {
                    slotRestrictionsService,
                    componentHandlerService,
                    slotSharedService
                });
            }, [
                exports.ContextualMenuDropdownService,
                cmscommons.IEditorEnablerService,
                smarteditcommons.IDecoratorService,
                smarteditcommons.IFeatureService,
                smarteditcommons.IComponentHandlerService,
                smarteditcommons.ISlotRestrictionsService,
                smarteditcommons.IPageInfoService,
                cmscommons.TypePermissionsRestService,
                exports.CmsDragAndDropService,
                smarteditcommons.SlotSharedService,
                smarteditcommons.ISettingsService,
                exports.OpenNodeInPageTreeService,
                cmscommons.IComponentMenuConditionAndCallbackService
            ])
        ]
    })
], exports.CmssmarteditModule);
/**
 * @param featureService
 * @param deps Set property to any type is because that it will need new dependency
 * when adding new feature.
 */
function addDecoratorForFeature(featureService, deps) {
    const { slotRestrictionsService, componentHandlerService, slotSharedService } = deps;
    featureService.addDecorator({
        key: 'syncIndicator',
        nameI18nKey: 'syncIndicator',
        permissions: ['se.sync.slot.indicator']
    });
    featureService.addDecorator({
        key: 'sharedSlotDisabledDecorator',
        nameI18nKey: 'se.cms.shared.slot.disabled.decorator',
        // only show that the slot is shared if it is not already external
        displayCondition: (componentType, componentId) => __awaiter(this, void 0, void 0, function* () {
            const [isSlotEditable, isExternalComponent, isSlotShared] = yield Promise.all([
                slotRestrictionsService.isSlotEditable(componentId),
                componentHandlerService.isExternalComponent(componentId, componentType),
                slotSharedService.isSlotShared(componentId)
            ]);
            return !isSlotEditable && !isExternalComponent && isSlotShared;
        })
    });
    featureService.addDecorator({
        key: 'externalSlotDisabledDecorator',
        nameI18nKey: 'se.cms.external.slot.disabled.decorator',
        displayCondition: (componentType, componentId) => Promise.resolve(slotSharedService.isGlobalSlot(componentId, componentType))
    });
    featureService.addDecorator({
        key: 'externalComponentDecorator',
        nameI18nKey: 'se.cms.external.component.decorator',
        displayCondition: (componentType, componentId) => componentHandlerService.isExternalComponent(componentId, componentType)
    });
}
/**
 * @param featureService
 * @param deps Set property to any type is because that it will need new dependency
 * when adding new feature.
 */
function registerForFeature(featureService, deps) {
    const { cmsDragAndDropService, slotSharedService } = deps;
    featureService.register({
        key: 'se.cms.html5DragAndDrop',
        nameI18nKey: 'se.cms.dragAndDrop.name',
        descriptionI18nKey: 'se.cms.dragAndDrop.description',
        enablingCallback: () => {
            cmsDragAndDropService.register();
            cmsDragAndDropService.apply();
        },
        disablingCallback: () => {
            cmsDragAndDropService.unregister();
        }
    });
    featureService.register({
        key: 'disableSharedSlotEditing',
        nameI18nKey: 'se.cms.disableSharedSlotEditing',
        descriptionI18nKey: 'se.cms.disableSharedSlotEditing.description',
        enablingCallback: () => {
            slotSharedService.setSharedSlotEnablementStatus(true);
        },
        disablingCallback: () => {
            slotSharedService.setSharedSlotEnablementStatus(false);
        }
    });
}
/**
 * @param featureService
 * @param deps Set property to any type is because that it will need new dependency
 * when adding new feature.
 */
function addContextualMenuButtonForFeature(featureService, deps) {
    const { componentMenuConditionAndCallbackService, cmsDragAndDropService, openNodeInPageTreeService } = deps;
    featureService.addContextualMenuButton({
        key: 'externalcomponentbutton',
        priority: 100,
        nameI18nKey: 'se.cms.contextmenu.title.externalcomponent',
        i18nKey: 'se.cms.contextmenu.title.externalcomponentbutton',
        regexpKeys: [REGEXPKEYS],
        condition: componentMenuConditionAndCallbackService.externalCondition,
        action: {
            component: cmscommons.ExternalComponentButtonComponent
        },
        displayClass: 'externalcomponentbutton',
        displayIconClass: 'sap-icon--globe',
        displaySmallIconClass: 'sap-icon--globe'
    });
    featureService.addContextualMenuButton({
        key: 'se.cms.dragandropbutton',
        priority: 200,
        nameI18nKey: 'se.cms.contextmenu.title.dragndrop',
        i18nKey: 'se.cms.contextmenu.title.dragndrop',
        regexpKeys: [REGEXPKEYS],
        condition: componentMenuConditionAndCallbackService.dragCondition,
        action: {
            callbacks: {
                mousedown: () => {
                    cmsDragAndDropService.update();
                }
            }
        },
        displayClass: 'movebutton',
        displayIconClass: 'sap-icon--move',
        displaySmallIconClass: 'sap-icon--move',
        permissions: ['se.context.menu.drag.and.drop.component']
    });
    featureService.addContextualMenuButton({
        key: 'se.cms.sharedcomponentbutton',
        priority: 300,
        nameI18nKey: 'se.cms.contextmenu.title.shared.component',
        i18nKey: 'se.cms.contextmenu.title.shared.component',
        regexpKeys: [REGEXPKEYS],
        condition: componentMenuConditionAndCallbackService.sharedCondition,
        action: {
            component: cmscommons.SharedComponentButtonComponent
        },
        displayClass: 'shared-component-button',
        displayIconClass: 'sap-icon--chain-link',
        displaySmallIconClass: 'sap-icon--chain-link',
        permissions: []
    });
    featureService.addContextualMenuButton({
        key: 'se.cms.remove',
        priority: 500,
        customCss: 'se-contextual-more-menu__item--delete',
        i18nKey: 'se.cms.contextmenu.title.remove',
        nameI18nKey: 'se.cms.contextmenu.title.remove',
        regexpKeys: [REGEXPKEYS],
        condition: componentMenuConditionAndCallbackService.removeCondition,
        action: {
            callback: componentMenuConditionAndCallbackService.removeCallback
        },
        displayClass: 'removebutton',
        displayIconClass: 'sap-icon--decline',
        displaySmallIconClass: 'sap-icon--decline',
        permissions: ['se.context.menu.remove.component']
    });
    featureService.addContextualMenuButton({
        key: 'se.slotContextualMenuVisibility',
        nameI18nKey: 'slotcontextmenu.title.visibility',
        regexpKeys: ['^.*ContentSlot$'],
        action: { component: SlotVisibilityButtonComponent },
        permissions: ['se.slot.context.menu.visibility']
    });
    featureService.addContextualMenuButton({
        key: 'se.slotSharedButton',
        nameI18nKey: 'slotcontextmenu.title.shared.button',
        regexpKeys: ['^.*Slot$'],
        action: {
            component: cmscommons.SlotSharedButtonComponent
        },
        permissions: ['se.slot.context.menu.shared.icon']
    });
    featureService.addContextualMenuButton({
        key: 'slotUnsharedButton',
        nameI18nKey: 'slotcontextmenu.title.unshared.button',
        regexpKeys: ['^.*Slot$'],
        action: { component: cmscommons.SlotUnsharedButtonComponent },
        permissions: ['se.slot.context.menu.unshared.icon']
    });
    featureService.addContextualMenuButton({
        key: 'se.slotSyncButton',
        nameI18nKey: 'slotcontextmenu.title.sync.button',
        regexpKeys: ['^.*Slot$'],
        action: { component: cmscommons.SlotSyncButtonComponent },
        permissions: ['se.sync.slot.context.menu']
    });
    featureService.addContextualMenuButton({
        key: 'clonecomponentbutton',
        priority: 600,
        nameI18nKey: 'se.cms.contextmenu.title.clone.component',
        i18nKey: 'se.cms.contextmenu.title.clone.component',
        regexpKeys: [REGEXPKEYS],
        condition: componentMenuConditionAndCallbackService.cloneCondition,
        action: {
            callback: componentMenuConditionAndCallbackService.cloneCallback
        },
        displayClass: 'clonebutton',
        displayIconClass: 'sap-icon--duplicate',
        displaySmallIconClass: 'sap-icon--duplicate',
        permissions: ['se.clone.component']
    });
    featureService.addContextualMenuButton({
        key: 'se.cms.openInPageTreeButton',
        priority: 700,
        nameI18nKey: 'se.cms.contextmenu.title.open.in.page.tree',
        i18nKey: 'se.cms.contextmenu.title.open.in.page.tree',
        regexpKeys: [REGEXPKEYS],
        action: {
            callback: (configuration) => {
                const elementUuid = configuration.componentAttributes.smarteditElementUuid;
                openNodeInPageTreeService.publishOpenEvent(elementUuid);
            }
        },
        displayIconClass: 'sap-icon--tree',
        displaySmallIconClass: 'sap-icon--tree'
    });
}
