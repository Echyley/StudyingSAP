'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var common = require('@angular/common');
var core = require('@angular/core');
var platformBrowser = require('@angular/platform-browser');
var personalizationcommons = require('personalizationcommons');
var smarteditcommons = require('smarteditcommons');

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

function __metadata(metadataKey, metadataValue) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
}

var /* @ngInject */ PromotionsSmarteditRestService_1;
/* @ngInject */ exports.PromotionsSmarteditRestService = /* @ngInject */ PromotionsSmarteditRestService_1 = class /* @ngInject */ PromotionsSmarteditRestService {
    constructor(restServiceFactory, smarteditUtils) {
        this.restServiceFactory = restServiceFactory;
        this.smarteditUtils = smarteditUtils;
    }
    getPromotions(catalogVersions) {
        const restService = this.restServiceFactory.get(/* @ngInject */ PromotionsSmarteditRestService_1.AVAILABLE_PROMOTIONS);
        const entries = [];
        catalogVersions = catalogVersions || [];
        catalogVersions.forEach((element, i) => {
            this.smarteditUtils.pushToArrayIfValueExists(entries, 'catalog' + i, element.catalog);
            this.smarteditUtils.pushToArrayIfValueExists(entries, 'version' + i, element.catalogVersion);
        });
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }
};
/* @ngInject */ exports.PromotionsSmarteditRestService.AVAILABLE_PROMOTIONS = '/personalizationwebservices/v1/query/cxpromotionsforcatalog';
/* @ngInject */ exports.PromotionsSmarteditRestService = /* @ngInject */ PromotionsSmarteditRestService_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IRestServiceFactory,
        personalizationcommons.PersonalizationsmarteditUtils])
], /* @ngInject */ exports.PromotionsSmarteditRestService);

exports.PersonalizationPromotionsSmarteditCommonsModule = class PersonalizationPromotionsSmarteditCommonsModule {
};
exports.PersonalizationPromotionsSmarteditCommonsModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, platformBrowser.BrowserModule],
        providers: [exports.PromotionsSmarteditRestService]
    })
], exports.PersonalizationPromotionsSmarteditCommonsModule);
