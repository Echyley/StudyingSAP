'use strict';

var _static = require('@angular/upgrade/static');
var core = require('@angular/core');
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

/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @class */ ((function () {
    function MerchandisingSmartEditModule(contextualMenuService, sharedDataService) {
        this.contextualMenuService = contextualMenuService;
        this.contextualMenuService.addItems({
            MerchandisingCarouselComponent: [
                {
                    key: "MerchandisingCarouselComponent",
                    i18nKey: "Edit Strategy",
                    action: {
                        callback: function (configuration, event) {
                            sharedDataService.get("merchandisingUrl").then(function (url) {
                                var appUrl = "https://" + url;
                                window.open(appUrl);
                            }.bind(this));
                        },
                    },
                    displayClass: 'sap-icon sap-icon--activate',
                },
            ],
        });
    }
    MerchandisingSmartEditModule = __decorate([
        smarteditcommons.SeEntryModule("merchandisingsmartedit"),
        core.NgModule({
            imports: [_static.UpgradeModule],
            declarations: [],
            entryComponents: [],
            providers: [],
        }),
        __metadata("design:paramtypes", [smarteditcommons.IContextualMenuService,
            smarteditcommons.ISharedDataService])
    ], MerchandisingSmartEditModule);
    return MerchandisingSmartEditModule;
})());
