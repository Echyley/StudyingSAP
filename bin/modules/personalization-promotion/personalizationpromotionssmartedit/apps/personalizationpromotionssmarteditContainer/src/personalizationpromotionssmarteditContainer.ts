/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import {
    PersonalizationsmarteditCommerceCustomizationService,
    PERSONLIZATION_PROMOTION_EXTENSION_TOKEN
} from 'personalizationcommons';
import {
    PersonalizationPromotionsSmarteditCommonsModule,
    PromotionsAction
} from 'personalizationpromotionssmarteditcommons';
import { TranslationModule, SelectModule, moduleUtils, SeEntryModule } from 'smarteditcommons';
import { PromotionsSmarteditPromotionComponent } from './promotionsComponent/PromotionsSmarteditPromotionComponent';
import { PROMOTION_ACTION_TYPE } from './utils/constants';
import '../../styling/style.less';

@SeEntryModule('personalizationpromotionssmarteditContainer')
@NgModule({
    imports: [
        TranslationModule.forChild(),
        SelectModule,
        PersonalizationPromotionsSmarteditCommonsModule,
        BrowserModule
    ],
    declarations: [PromotionsSmarteditPromotionComponent],
    entryComponents: [PromotionsSmarteditPromotionComponent],
    providers: [
        {
            provide: PERSONLIZATION_PROMOTION_EXTENSION_TOKEN,
            useValue: {
                component: PromotionsSmarteditPromotionComponent
            }
        },
        moduleUtils.bootstrap(
            (
                translateService: TranslateService,
                personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService
            ) => {
                personalizationsmarteditCommerceCustomizationService.registerType({
                    type: PROMOTION_ACTION_TYPE,
                    text: 'personalization.modal.commercecustomization.action.type.promotion',
                    confProperty:
                        'personalizationsmartedit.commercecustomization.promotions.enabled',
                    getName: (action: PromotionsAction) =>
                        translateService.instant(
                            'personalization.modal.commercecustomization.promotion.display.name'
                        ) +
                        ' - ' +
                        action.promotionId
                });
            },
            [TranslateService, PersonalizationsmarteditCommerceCustomizationService]
        )
    ]
})
export class PersonalizationPromotionsSmarteditContainer {}
