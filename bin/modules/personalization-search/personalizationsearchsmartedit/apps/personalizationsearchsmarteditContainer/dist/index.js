'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var dragDrop = require('@angular/cdk/drag-drop');
var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var core$2 = require('@fundamental-ngx/core');
var core$1 = require('@ngx-translate/core');
var personalizationcommons = require('personalizationcommons');
var common = require('@angular/common');
var lodash = require('lodash');
var smarteditcommons = require('smarteditcommons');
var rxjs = require('rxjs');
var operators = require('rxjs/operators');

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

/* @ngInject */ exports.SearchProfilesContextService = class /* @ngInject */ SearchProfilesContextService {
    constructor() {
        this.searchProfileActionComparer = (a1, a2) => a1.type === a2.type &&
            a1.searchProfileCode === a2.searchProfileCode &&
            a1.searchProfileCatalog === a2.searchProfileCatalog;
        this.updateSearchActionContext = (actions) => {
            actions.forEach((action) => {
                const searchProfileActions = this.searchProfileActionContext.searchProfilesOrder.filter((spAction) => this.searchProfileActionComparer(action, spAction));
                if (searchProfileActions.length > 0) {
                    searchProfileActions[0].code = action.code;
                }
            });
        };
        this.isDirty = () => false;
        this.searchProfileActionContext = {
            customizationCode: undefined,
            variationCode: undefined,
            initialOrder: [],
            searchProfilesOrder: [],
        };
    }
};
/* @ngInject */ exports.SearchProfilesContextService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [])
], /* @ngInject */ exports.SearchProfilesContextService);

/* @ngInject */ exports.SearchRestService = class /* @ngInject */ SearchRestService {
    constructor(personalizationsmarteditUtils, restServiceFactory) {
        this.personalizationsmarteditUtils = personalizationsmarteditUtils;
        this.restServiceFactory = restServiceFactory;
        this.SEARCH_PROFILES = "/adaptivesearchwebservices/v1/searchprofiles";
        this.UPDATE_CUSTOMIZATION_RANK = "/personalizationwebservices/v1/query/cxUpdateSearchProfileActionRank";
        this.GET_INDEX_TYPES_FOR_SITE = "/personalizationwebservices/v1/query/cxGetIndexTypesForSite";
    }
    initSeData(seData) {
        this.seData = seData;
    }
    getSeExperienceData() {
        return this.seData.seExperienceData;
    }
    getSearchProfiles(filter) {
        const experienceData = this.getSeExperienceData();
        const catalogVersionsStr = (experienceData.productCatalogVersions || [])
            .map(function (cv) {
            return cv.catalog + ":" + cv.catalogVersion;
        })
            .join(",");
        const restService = this.restServiceFactory.get(this.SEARCH_PROFILES);
        filter = Object.assign({ catalogVersions: catalogVersionsStr }, filter);
        return restService.get(filter);
    }
    updateSearchProfileActionRank(filter) {
        const experienceData = this.getSeExperienceData();
        const restService = this.restServiceFactory.get(this.UPDATE_CUSTOMIZATION_RANK);
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", filter.customizationCode);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variation", filter.variationCode);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "rankBeforeAction", filter.rankBeforeAction);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "rankAfterAction", filter.rankAfterAction);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "actions", filter.actions);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", experienceData.catalogDescriptor.catalogId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", experienceData.catalogDescriptor.catalogVersion);
        const requestParams = {
            params: {
                entry: entries,
            },
        };
        return restService.save(requestParams);
    }
    getIndexTypesForCatalogVersion(productCV) {
        const experienceData = this.getSeExperienceData();
        const restService = this.restServiceFactory.get(this.GET_INDEX_TYPES_FOR_SITE);
        const entries = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "baseSiteId", experienceData.catalogDescriptor.siteId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", productCV.catalog);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", productCV.catalogVersion);
        const requestParams = {
            params: {
                entry: entries,
            },
        };
        return restService.save(requestParams);
    }
};
/* @ngInject */ exports.SearchRestService = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [personalizationcommons.PersonalizationsmarteditUtils,
        smarteditcommons.IRestServiceFactory])
], /* @ngInject */ exports.SearchRestService);

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
const SEARCH_PROFILE_ACTION_TYPE = "cxSearchProfileActionData";

window.__smartedit__.addDecoratorPayload("Component", "SearchProfilesSmarteditComponent", {
    selector: "search-profiles-smartedit",
    template: `<div *ngIf="getSelectedTypeCode() === getSearchProfileCode()" class="form-group"><label for="search-profile-selector-1" class="fd-form-label" translate="personalizationsearchsmartedit.commercecustomization.search.label"></label><personalization-infinite-scrolling [fetchPage]="getPage"><se-select [id]="search-profile-selector-1" class="fd-form__control" [placeholder]="'personalizationsearchsmartedit.commercecustomization.search.placeholder'" [searchEnabled]="true" [fetchStrategy]="fetchStrategy" [(model)]="selectedSearchProfile" [onSelect]="searchProfileSelectedEvent"></se-select><div class="pe-spinner--outer" [hidden]="!requestProcessing"><div class="spinner-md spinner-light"></div></div></personalization-infinite-scrolling><div *ngIf="searchProfileActions.length !== 0" class="ps-search-profile__selection"><label class="control-label ps-control-label"><span translate="personalizationsearchsmartedit.commercecustomization.search.selection.label"></span></label><se-help><span translate="personalizationsearchsmartedit.commercecustomization.search.helpmsg"></span></se-help><div cdkDropList [cdkDropListData]="searchProfileActions" [cdkDropListConnectedTo]="dragDroppedTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true" class="ps-search-profile__sel-tree"><div class="ps-search-profile__sel-tbody"><div *ngFor="let searchProfileAction of searchProfileActions" cdkDrag [cdkDragData]="searchProfileAction.searchProfileCode" (cdkDragMoved)="dragMoved($event)" class="ps-search-profile__sel-tree-row"><div class="node-item" [attr.data-id]="searchProfileAction.searchProfileCode" [attr.id]=" 'node-' + searchProfileAction.searchProfileCode"><div class="ps-search-profile__sel-tree-row-wrapper"><div class="ps-search-profile__sel-profile ps-search-profile-height"><span class="ps-search-profile__sel-profile-code" [textContent]="searchProfileAction.searchProfileCode"></span></div></div></div></div></div></div></div><label class="control-label ps-control-label__cust"><span translate="personalizationsearchsmartedit.commercecustomization.search.label.cust"></span></label></div>`,
    styles: [`.cdk-drag-placeholder{display:none}.cdk-drag-preview{z-index:2000!important}.node-item{z-index:2100!important;cursor:move}.cdk-drag-row-layout{display:flex}.drop-before{border-top:1px solid #000;z-index:2000!important}.drop-after{border-bottom:1px solid #000;z-index:2000!important}.ps-search-profile-height{height:40px}`],
    encapsulation: core.ViewEncapsulation.None,
});
exports.SearchProfilesSmarteditComponent = class SearchProfilesSmarteditComponent {
    constructor(personlizationSearchExtensionInjector, document, translateService, searchRestService, searchProfilesContextService, personalizationsmarteditMessageHandler) {
        this.personlizationSearchExtensionInjector = personlizationSearchExtensionInjector;
        this.document = document;
        this.translateService = translateService;
        this.searchRestService = searchRestService;
        this.searchProfilesContextService = searchProfilesContextService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.selectedSearchProfile = null;
        this.allSearchProfiles = [];
        this.availableSearchProfiles = [];
        this.searchProfileActions = [];
        this.fetchStrategy = {
            fetchAll: () => {
                this.availableSearchProfiles = [];
                this.allSearchProfiles.forEach((searchProfile) => {
                    if (this.isItemInSelectDisabled(searchProfile)) {
                        this.availableSearchProfiles.push(this.formateSearchProfileItem(searchProfile));
                    }
                });
                return Promise.resolve(this.availableSearchProfiles);
            },
        };
        this.actionsPreCount = 0;
        this.draggedItemCss = "node-item";
        this.dropTargetIds = [];
        this.nodeLookup = {};
        this.dropAction = null;
        this.dragMovedSubject = new rxjs.Subject();
        this.getPage = () => this.addMoreSearchProfilesItems();
        this.searchProfileSelectedEvent = (selectedSearchProfile) => {
            this.selectedSearchProfile = selectedSearchProfile;
            const searchProfileAction = this.buildAction(selectedSearchProfile);
            this.addAction(searchProfileAction, this.searchProfilesContextService.searchProfileActionComparer);
        };
        this.seData = personlizationSearchExtensionInjector.seData;
        this.getCustomization =
            personlizationSearchExtensionInjector.getCustomization;
        this.getVariation = personlizationSearchExtensionInjector.getVariation;
        this.actions = personlizationSearchExtensionInjector.actions;
        this.addAction = personlizationSearchExtensionInjector.addAction;
        this.isActionInSelectDisabled =
            personlizationSearchExtensionInjector.isActionInSelectDisabled;
        this.getSelectedTypeCode =
            personlizationSearchExtensionInjector.getSelectedTypeCode;
    }
    ngOnInit() {
        this.searchRestService.initSeData(this.seData);
        this.moreSearchProfilestRequestProcessing = false;
        this.paginationHelper = new personalizationcommons.PaginationHelper({});
        this.paginationHelper.reset();
        this.searchProfileFilter = {
            code: "",
            pageSize: 0,
            currentPage: 0,
        };
        this.searchProfileContext = this.searchProfilesContextService.searchProfileActionContext;
        this.dragMovedSubscription = this.dragMovedSubject
            .pipe(operators.debounceTime(50))
            .subscribe((event) => {
            this.performDragMoved(event);
        });
    }
    ngDoCheck() {
        if (this.actionsPreCount !== this.actions.length) {
            const actionsArray = this.actions || [];
            this.searchProfileActions = actionsArray
                .filter(function (item) {
                return item.action.type === SEARCH_PROFILE_ACTION_TYPE;
            })
                .map(function (item) {
                return Object.assign(Object.assign({}, item.action), { baseIndex: actionsArray.indexOf(item) });
            });
            this.searchProfileContext.searchProfilesOrder = this.searchProfileActions;
            this.resetSearchProfileActionsDragDrop();
            this.updateSearchProfileActionsDragDrop();
            this.actionsPreCount = this.actions.length;
        }
        if (this.customizationPreCode !== this.getCustomization().code) {
            if (this.getCustomization() && this.getCustomization().code) {
                this.searchProfileContext.customizationCode = this.getCustomization().code;
            }
            else {
                this.searchProfileContext.customizationCode = undefined;
            }
            this.customizationPreCode = this.getCustomization().code;
        }
        if (this.variationPreCode !== this.getVariation().code) {
            if (this.getVariation() && this.getVariation().code) {
                this.searchProfileContext.variationCode = this.getVariation().code;
            }
            else {
                this.searchProfileContext.variationCode = undefined;
            }
            this.variationPreCode = this.getVariation().code;
        }
    }
    ngOnDestroy() {
        var _a;
        (_a = this.dragMovedSubscription) === null || _a === void 0 ? void 0 : _a.unsubscribe();
    }
    getSearchProfileCode() {
        return SEARCH_PROFILE_ACTION_TYPE;
    }
    dragMoved(event) {
        this.dragMovedSubject.next(event);
    }
    dragDropped(event) {
        if (this.checkDragDrop(event.item.data)) {
            const draggedItemId = event.item.data;
            const dropItemId = this.dropAction.targetId;
            const draggedItem = this.nodeLookup[draggedItemId];
            const droppedItem = this.nodeLookup[dropItemId];
            if (this.dropAction.action === "before") {
                this.actions.splice(droppedItem.baseIndex, 0, this.actions.splice(draggedItem.baseIndex, 1)[0]);
            }
            else {
                this.actions.splice(droppedItem.baseIndex + 1, 0, this.actions.splice(draggedItem.baseIndex, 1)[0]);
            }
            this.searchProfileActions.splice(this.searchProfileActions.findIndex((item) => item.searchProfileCode === draggedItemId), 1);
            const droppedIndex = this.searchProfileActions.findIndex((item) => item.searchProfileCode === this.dropAction.targetId);
            if (this.dropAction.action === "before") {
                this.searchProfileActions.splice(droppedIndex, 0, draggedItem);
            }
            else {
                this.searchProfileActions.splice(droppedIndex + 1, 0, draggedItem);
            }
            this.updateSearchProfileActionsBaseIndex();
            const startIdx = event.previousIndex < event.currentIndex
                ? event.previousIndex
                : event.currentIndex;
            const increaseValue = Math.abs(event.currentIndex - event.previousIndex) + 1;
            const modifiedActions = this.searchProfileActions.slice(startIdx, increaseValue);
            const modifiedWrappedActions = [];
            modifiedActions.forEach((item) => {
                modifiedWrappedActions.push(this.getWrapperActionForAction(item));
            });
            this.setStatusForUpdatedActions(modifiedWrappedActions);
        }
    }
    getWrapperActionForAction(action) {
        const matchedActions = [];
        this.actions.forEach((comparedAction) => {
            if (this.searchProfilesContextService.searchProfileActionComparer(action, comparedAction.action)) {
                matchedActions.push(comparedAction);
            }
        });
        return matchedActions[0];
    }
    checkDragDrop(draggedItemId) {
        return (this.dropAction !== null &&
            this.dropAction.action !== null &&
            this.dropAction.targetId !== null &&
            draggedItemId !== this.dropAction.targetId &&
            this.checkDragDropIndex(draggedItemId));
    }
    checkDragDropIndex(draggedItemId) {
        const draggedIndex = this.searchProfileActions.findIndex((item) => item.searchProfileCode === draggedItemId);
        let droppedIndex = this.searchProfileActions.findIndex((item) => item.searchProfileCode === this.dropAction.targetId);
        if (this.dropAction.action === "before") {
            droppedIndex -= 1;
        }
        else {
            droppedIndex += 1;
        }
        return draggedIndex !== droppedIndex;
    }
    updateSearchProfileActionsBaseIndex() {
        this.actions.forEach((actionItem, index) => {
            if (actionItem.action.type === SEARCH_PROFILE_ACTION_TYPE) {
                this.nodeLookup[actionItem.action.searchProfileCode].baseIndex = index;
            }
        });
    }
    setStatusForUpdatedActions(wrapperActions) {
        wrapperActions.forEach((action) => {
            if (action.status !==
                personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW) {
                action.status =
                    personalizationcommons.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
            }
        });
    }
    updateSearchProfileActionsDragDrop() {
        this.searchProfileActions.forEach((node) => {
            this.dropTargetIds.push(node.searchProfileCode);
            this.nodeLookup[node.searchProfileCode] = node;
        });
    }
    resetSearchProfileActionsDragDrop() {
        this.dropTargetIds.length = 0;
        this.nodeLookup = {};
    }
    performDragMoved(event) {
        this.dropAction = {
            targetId: null,
            action: null,
        };
        const ele = this.document.elementFromPoint(event.pointerPosition.x, event.pointerPosition.y);
        if (!ele) {
            this.clearDragInfo();
            return;
        }
        const container = ele.classList.contains(this.draggedItemCss)
            ? ele
            : ele.closest(".node-item");
        if (!container) {
            this.clearDragInfo();
            return;
        }
        this.dropAction = {
            targetId: container.getAttribute("data-id"),
        };
        const targetRect = container.getBoundingClientRect();
        const oneHalf = targetRect.height / 2;
        if (event.pointerPosition.y - targetRect.top < oneHalf) {
            this.dropAction.action = "before";
        }
        else if (event.pointerPosition.y - targetRect.top > oneHalf) {
            this.dropAction.action = "after";
        }
        this.showDragInfo();
    }
    showDragInfo() {
        this.clearDragInfo();
        if (this.dropAction) {
            this.document
                .getElementById("node-" + this.dropAction.targetId)
                .classList.add("drop-" + this.dropAction.action);
        }
    }
    clearDragInfo(dropped = false) {
        if (dropped) {
            this.dropAction = null;
        }
        this.document
            .querySelectorAll(".drop-before")
            .forEach((element) => element.classList.remove("drop-before"));
        this.document
            .querySelectorAll(".drop-after")
            .forEach((element) => element.classList.remove("drop-after"));
    }
    addMoreSearchProfilesItems() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                if (this.paginationHelper.getPage() <
                    this.paginationHelper.getTotalPages() - 1 &&
                    !this.moreSearchProfilestRequestProcessing) {
                    this.moreSearchProfilestRequestProcessing = true;
                    const indexTypeResponse = yield this.getIndexTypes();
                    let filter = this.getSearchProfileFilterObject();
                    const param = {
                        indexTypes: indexTypeResponse.indexTypeIds || [],
                    };
                    filter = Object.assign({ indexTypes: param.indexTypes }, filter);
                    const searchProfilesResponse = yield this.searchRestService.getSearchProfiles(filter);
                    this.allSearchProfiles = lodash.cloneDeep(searchProfilesResponse.searchProfiles);
                    this.paginationHelper = new personalizationcommons.PaginationHelper(searchProfilesResponse.pagination);
                    this.moreSearchProfilestRequestProcessing = false;
                }
            }
            catch (err) {
                this.moreSearchProfilestRequestProcessing = false;
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant("personalizationsearchsmartedit.commercecustomization.search.error.gettingsearchprofiles"));
            }
        });
    }
    getIndexTypes() {
        const deferred = smarteditcommons.promiseUtils.defer();
        const experienceDataPromises = [];
        const experienceDataResponse = this.searchRestService.getSeExperienceData();
        experienceDataResponse.productCatalogVersions.forEach((productCatalogVersion) => {
            experienceDataPromises.push(this.searchRestService.getIndexTypesForCatalogVersion(productCatalogVersion));
        });
        Promise.all(experienceDataPromises).then(function successCallback(experienceDataPromisesResponse) {
            const mergedResponse = {
                indexTypeIds: [],
            };
            experienceDataPromisesResponse.forEach((singleResponse) => {
                mergedResponse.indexTypeIds = mergedResponse.indexTypeIds.concat(singleResponse.indexTypeIds.filter(function (item) {
                    return mergedResponse.indexTypeIds.indexOf(item) < 0;
                }));
            });
            deferred.resolve(mergedResponse);
        }, function errorCallback(errorResponse) {
            deferred.reject(errorResponse);
        });
        return deferred.promise;
    }
    isItemInSelectDisabled(searchProfileItem) {
        const searchProfileAction = this.buildAction(searchProfileItem);
        return this.isActionInSelectDisabled(searchProfileAction, this.searchProfilesContextService.searchProfileActionComparer) === undefined
            ? true
            : false;
    }
    getSearchProfileFilterObject() {
        return {
            code: this.searchProfileFilter.code,
            pageSize: this.paginationHelper.getCount(),
            currentPage: this.paginationHelper.getPage() + 1,
        };
    }
    buildAction(searchProfileItem) {
        return {
            type: SEARCH_PROFILE_ACTION_TYPE,
            searchProfileCode: searchProfileItem.code,
            searchProfileCatalog: searchProfileItem.catalogVersion.split(":")[0],
        };
    }
    formateSearchProfileItem(searchProfileItem) {
        const availableSearchProfileItem = lodash.cloneDeep(searchProfileItem);
        availableSearchProfileItem.id = availableSearchProfileItem.code;
        availableSearchProfileItem.label = availableSearchProfileItem.name;
        return availableSearchProfileItem;
    }
};
exports.SearchProfilesSmarteditComponent = __decorate([
    core.Component({
        selector: "search-profiles-smartedit",
        template: `<div *ngIf="getSelectedTypeCode() === getSearchProfileCode()" class="form-group"><label for="search-profile-selector-1" class="fd-form-label" translate="personalizationsearchsmartedit.commercecustomization.search.label"></label><personalization-infinite-scrolling [fetchPage]="getPage"><se-select [id]="search-profile-selector-1" class="fd-form__control" [placeholder]="'personalizationsearchsmartedit.commercecustomization.search.placeholder'" [searchEnabled]="true" [fetchStrategy]="fetchStrategy" [(model)]="selectedSearchProfile" [onSelect]="searchProfileSelectedEvent"></se-select><div class="pe-spinner--outer" [hidden]="!requestProcessing"><div class="spinner-md spinner-light"></div></div></personalization-infinite-scrolling><div *ngIf="searchProfileActions.length !== 0" class="ps-search-profile__selection"><label class="control-label ps-control-label"><span translate="personalizationsearchsmartedit.commercecustomization.search.selection.label"></span></label><se-help><span translate="personalizationsearchsmartedit.commercecustomization.search.helpmsg"></span></se-help><div cdkDropList [cdkDropListData]="searchProfileActions" [cdkDropListConnectedTo]="dragDroppedTargetIds" (cdkDropListDropped)="dragDropped($event)" [cdkDropListSortingDisabled]="true" class="ps-search-profile__sel-tree"><div class="ps-search-profile__sel-tbody"><div *ngFor="let searchProfileAction of searchProfileActions" cdkDrag [cdkDragData]="searchProfileAction.searchProfileCode" (cdkDragMoved)="dragMoved($event)" class="ps-search-profile__sel-tree-row"><div class="node-item" [attr.data-id]="searchProfileAction.searchProfileCode" [attr.id]=" 'node-' + searchProfileAction.searchProfileCode"><div class="ps-search-profile__sel-tree-row-wrapper"><div class="ps-search-profile__sel-profile ps-search-profile-height"><span class="ps-search-profile__sel-profile-code" [textContent]="searchProfileAction.searchProfileCode"></span></div></div></div></div></div></div></div><label class="control-label ps-control-label__cust"><span translate="personalizationsearchsmartedit.commercecustomization.search.label.cust"></span></label></div>`,
        styles: [`.cdk-drag-placeholder{display:none}.cdk-drag-preview{z-index:2000!important}.node-item{z-index:2100!important;cursor:move}.cdk-drag-row-layout{display:flex}.drop-before{border-top:1px solid #000;z-index:2000!important}.drop-after{border-bottom:1px solid #000;z-index:2000!important}.ps-search-profile-height{height:40px}`],
        encapsulation: core.ViewEncapsulation.None,
    }),
    __param(0, core.Inject(personalizationcommons.PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN)),
    __param(1, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [Object, Document,
        core$1.TranslateService,
        exports.SearchRestService,
        exports.SearchProfilesContextService,
        personalizationcommons.PersonalizationsmarteditMessageHandler])
], exports.SearchProfilesSmarteditComponent);

exports.PersonalizationsearchSmarteditContainerModule = class PersonalizationsearchSmarteditContainerModule {
};
exports.PersonalizationsearchSmarteditContainerModule = __decorate([
    smarteditcommons.SeEntryModule("personalizationsearchsmarteditContainer"),
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.SelectModule,
            core$2.PopoverModule,
            platformBrowser.BrowserModule,
            smarteditcommons.SharedComponentsModule,
            dragDrop.DragDropModule,
            personalizationcommons.PersonalizationsmarteditCommonsComponentsModule,
        ],
        declarations: [exports.SearchProfilesSmarteditComponent],
        entryComponents: [exports.SearchProfilesSmarteditComponent],
        exports: [],
        providers: [
            exports.SearchProfilesContextService,
            exports.SearchRestService,
            {
                provide: personalizationcommons.PERSONLIZATION_SEARCH_EXTENSION_TOKEN,
                useValue: {
                    component: exports.SearchProfilesSmarteditComponent,
                },
            },
            smarteditcommons.moduleUtils.bootstrap((translateService, searchProfilesContextService, searchRestService, personalizationsmarteditMessageHandler, personalizationsmarteditCommerceCustomizationService) => {
                personalizationsmarteditCommerceCustomizationService.registerType({
                    type: SEARCH_PROFILE_ACTION_TYPE,
                    text: "personalizationsearchsmartedit.commercecustomization.action.type.search",
                    confProperty: "personalizationsearch.commercecustomization.search.profile.enabled",
                    getName: (action) => translateService.instant("personalizationsearchsmartedit.commercecustomization.search.display.name") +
                        " - " +
                        action.searchProfileCode,
                    updateActions(customizationCode, variationCode, actions, respCreate) {
                        const deferred = smarteditcommons.promiseUtils.defer();
                        if (respCreate !== undefined) {
                            searchProfilesContextService.updateSearchActionContext(respCreate.actions);
                        }
                        const searchProfilesCtx = searchProfilesContextService.searchProfileActionContext;
                        const rankAfterAction = searchProfilesCtx.searchProfilesOrder.splice(0, 1)[0];
                        const spActionCodes = searchProfilesCtx.searchProfilesOrder
                            .map(function (sp) {
                            return sp.code;
                        })
                            .join(",");
                        const filter = {
                            customizationCode: searchProfilesCtx.customizationCode,
                            variationCode: searchProfilesCtx.variationCode,
                            rankAfterAction: rankAfterAction.code,
                            actions: spActionCodes,
                        };
                        searchRestService.updateSearchProfileActionRank(filter).then(function successCallback() {
                            deferred.resolve();
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError(this.translateService.instant("personalization.error.updatingcustomization"));
                            deferred.reject();
                        });
                        return deferred.promise;
                    },
                });
            }, [
                core$1.TranslateService,
                exports.SearchProfilesContextService,
                exports.SearchRestService,
                personalizationcommons.PersonalizationsmarteditMessageHandler,
                personalizationcommons.PersonalizationsmarteditCommerceCustomizationService,
            ]),
        ],
    })
], exports.PersonalizationsearchSmarteditContainerModule);
