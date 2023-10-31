/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { L10nPipe, L10nPipeModule, TranslationModule } from 'smarteditcommons';
import { DatetimePickerRangeModule } from './datetimePicker';
import {
    PersonalizationInfiniteScrollDirective,
    PersonalizationsmarteditInfiniteScrollingComponent
} from './personalizationInfiniteScroll';
import {
    PersonalizationPreventParentScrollComponent,
    PersonalizationPreventParentScrollDirective
} from './personalizationPreventParentScroll';
import { ScrollZoneModule } from './scrollZone';
import {
    PaginationHelper,
    PersonalizationsmarteditCommerceCustomizationService,
    PersonalizationsmarteditMessageHandler
} from './services';
import {
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditDateUtils,
    PersonalizationsmarteditUtils
} from './utils';

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditCommonsComponentsModule
 */
@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        L10nPipeModule,
        DatetimePickerRangeModule,
        ScrollZoneModule
    ],
    providers: [
        {
            provide: 'PaginationHelper',
            useFactory:
                () =>
                (initialData: any): PaginationHelper =>
                    new PaginationHelper(initialData)
        },
        L10nPipe,
        PersonalizationsmarteditUtils,
        PersonalizationsmarteditDateUtils,
        PersonalizationsmarteditContextUtils,
        PersonalizationsmarteditMessageHandler,
        PersonalizationsmarteditCommerceCustomizationService
    ],
    declarations: [
        PersonalizationInfiniteScrollDirective,
        PersonalizationPreventParentScrollDirective,
        PersonalizationsmarteditInfiniteScrollingComponent,
        PersonalizationPreventParentScrollComponent
    ],
    entryComponents: [
        PersonalizationsmarteditInfiniteScrollingComponent,
        PersonalizationPreventParentScrollComponent
    ],
    exports: [
        PersonalizationInfiniteScrollDirective,
        PersonalizationPreventParentScrollDirective,
        PersonalizationsmarteditInfiniteScrollingComponent,
        PersonalizationPreventParentScrollComponent
    ]
})
export class PersonalizationsmarteditCommonsComponentsModule {}
