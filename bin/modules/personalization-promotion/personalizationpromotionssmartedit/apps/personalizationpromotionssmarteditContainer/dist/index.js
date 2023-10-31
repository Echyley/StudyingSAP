'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var core$1 = require('@ngx-translate/core');
var personalizationcommons = require('personalizationcommons');
var personalizationpromotionssmarteditcommons = require('personalizationpromotionssmarteditcommons');
var smarteditcommons = require('smarteditcommons');
var lodash = require('lodash');

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
const PROMOTION_ACTION_TYPE = 'cxPromotionActionData';

window.__smartedit__.addDecoratorPayload("Component", "PromotionsSmarteditPromotionComponent", {
    selector: 'promotions-smartedit-promotion',
    template: `<div *ngIf="getSelectedTypeCode() === getPromotionCode()"><label for="promotion-selector-1" class="fd-form-label" translate="personalization.modal.commercecustomization.promotion.label"></label><se-select [id]="promotion-selector-1" class="fd-form__control" [placeholder]="'personalization.modal.commercecustomization.promotion.search.placeholder'" [fetchStrategy]="fetchStrategy" [(model)]="selectedPromotion" [onSelect]="promotionSelectedEvent" [searchEnabled]="true"></se-select></div>`
});
exports.PromotionsSmarteditPromotionComponent = class PromotionsSmarteditPromotionComponent {
    constructor(personlizationPromotionExtensionInjector, translateService, experienceService, promotionssmarteditRestService, personalizationsmarteditMessageHandler) {
        this.personlizationPromotionExtensionInjector = personlizationPromotionExtensionInjector;
        this.translateService = translateService;
        this.experienceService = experienceService;
        this.promotionssmarteditRestService = promotionssmarteditRestService;
        this.personalizationsmarteditMessageHandler = personalizationsmarteditMessageHandler;
        this.selectedPromotion = null;
        this.allPromotions = [];
        this.availablePromotions = [];
        this.fetchStrategy = {
            fetchAll: () => {
                this.availablePromotions = [];
                this.allPromotions.forEach((promotion) => {
                    const action = this.buildAction(promotion);
                    if (this.isActionInSelectDisabled(action, this.comparer) === undefined) {
                        this.availablePromotions.push(lodash.cloneDeep(promotion));
                    }
                });
                return Promise.resolve(this.availablePromotions);
            }
        };
        this.comparer = (a1, a2) => a1.type === a2.type && a1.promotionId === a2.promotionId;
        this.promotionSelectedEvent = (promotion) => {
            const action = this.buildAction(promotion);
            this.addAction(action, this.comparer);
        };
        this.addAction = personlizationPromotionExtensionInjector.addAction;
        this.isActionInSelectDisabled =
            personlizationPromotionExtensionInjector.isActionInSelectDisabled;
        this.getSelectedTypeCode = personlizationPromotionExtensionInjector.getSelectedTypeCode;
    }
    ngOnInit() {
        this.getAvailablePromotions();
    }
    getAvailablePromotions() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const response = yield this.getPromotions();
                this.allPromotions = response.promotions;
                this.allPromotions.forEach((item) => {
                    item.id = item.code;
                    item.label = item.code + '(' + item.promotionGroup + ')';
                });
            }
            catch (e) {
                this.personalizationsmarteditMessageHandler.sendError(this.translateService.instant('personalization.error.gettingpromotions'));
            }
        });
    }
    getPromotionCode() {
        return PROMOTION_ACTION_TYPE;
    }
    getPromotions() {
        return __awaiter(this, void 0, void 0, function* () {
            const catalogs = yield this.getCatalogs();
            const promotion = yield this.promotionssmarteditRestService.getPromotions(catalogs);
            return Promise.resolve(promotion);
        });
    }
    getCatalogs() {
        return __awaiter(this, void 0, void 0, function* () {
            const catalogs = [];
            const experience = yield this.experienceService.getCurrentExperience();
            catalogs.push({
                catalog: experience.catalogDescriptor.catalogId,
                catalogVersion: experience.catalogDescriptor.catalogVersion
            });
            experience.productCatalogVersions.forEach((item) => {
                catalogs.push({
                    catalog: item.catalog,
                    catalogVersion: item.catalogVersion
                });
            });
            return Promise.resolve(catalogs);
        });
    }
    buildAction(promotion) {
        return {
            type: 'cxPromotionActionData',
            promotionId: promotion.code
        };
    }
};
exports.PromotionsSmarteditPromotionComponent = __decorate([
    core.Component({
        selector: 'promotions-smartedit-promotion',
        template: `<div *ngIf="getSelectedTypeCode() === getPromotionCode()"><label for="promotion-selector-1" class="fd-form-label" translate="personalization.modal.commercecustomization.promotion.label"></label><se-select [id]="promotion-selector-1" class="fd-form__control" [placeholder]="'personalization.modal.commercecustomization.promotion.search.placeholder'" [fetchStrategy]="fetchStrategy" [(model)]="selectedPromotion" [onSelect]="promotionSelectedEvent" [searchEnabled]="true"></se-select></div>`
    }),
    __param(0, core.Inject(personalizationcommons.PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN)),
    __metadata("design:paramtypes", [Object, core$1.TranslateService,
        smarteditcommons.IExperienceService,
        personalizationpromotionssmarteditcommons.PromotionsSmarteditRestService,
        personalizationcommons.PersonalizationsmarteditMessageHandler])
], exports.PromotionsSmarteditPromotionComponent);

exports.PersonalizationPromotionsSmarteditContainer = class PersonalizationPromotionsSmarteditContainer {
};
exports.PersonalizationPromotionsSmarteditContainer = __decorate([
    smarteditcommons.SeEntryModule('personalizationpromotionssmarteditContainer'),
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            smarteditcommons.SelectModule,
            personalizationpromotionssmarteditcommons.PersonalizationPromotionsSmarteditCommonsModule,
            platformBrowser.BrowserModule
        ],
        declarations: [exports.PromotionsSmarteditPromotionComponent],
        entryComponents: [exports.PromotionsSmarteditPromotionComponent],
        providers: [
            {
                provide: personalizationcommons.PERSONLIZATION_PROMOTION_EXTENSION_TOKEN,
                useValue: {
                    component: exports.PromotionsSmarteditPromotionComponent
                }
            },
            smarteditcommons.moduleUtils.bootstrap((translateService, personalizationsmarteditCommerceCustomizationService) => {
                personalizationsmarteditCommerceCustomizationService.registerType({
                    type: PROMOTION_ACTION_TYPE,
                    text: 'personalization.modal.commercecustomization.action.type.promotion',
                    confProperty: 'personalizationsmartedit.commercecustomization.promotions.enabled',
                    getName: (action) => translateService.instant('personalization.modal.commercecustomization.promotion.display.name') +
                        ' - ' +
                        action.promotionId
                });
            }, [core$1.TranslateService, personalizationcommons.PersonalizationsmarteditCommerceCustomizationService])
        ]
    })
], exports.PersonalizationPromotionsSmarteditContainer);

exports.PROMOTION_ACTION_TYPE = PROMOTION_ACTION_TYPE;
