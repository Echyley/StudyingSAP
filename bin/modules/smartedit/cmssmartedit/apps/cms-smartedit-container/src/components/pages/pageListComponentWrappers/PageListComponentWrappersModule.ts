/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
    HasOperationPermissionDirectiveModule,
    SharedComponentsModule,
    SmarteditCommonsModule,
    TranslationModule
} from 'smarteditcommons';
import { ModifiedTimeWrapperComponent } from './ModifiedTimeWrapperComponent';
import { NumberOfRestrictionsWrapperComponent } from './NumberOfRestrictionsWrapperComponent';
import { PageStatusWrapperComponent } from './PageStatusWrapperComponent';
import { TrashListDropdownItemsWrapperComponent } from './TrashListDropdownItemsWrapperComponent';

/**
 * Module containing all the components and services necessary to manage a page.
 */
@NgModule({
    imports: [
        CommonModule,
        SmarteditCommonsModule,
        SharedComponentsModule,
        TranslationModule.forChild(),
        HasOperationPermissionDirectiveModule
    ],
    declarations: [
        TrashListDropdownItemsWrapperComponent,
        ModifiedTimeWrapperComponent,
        NumberOfRestrictionsWrapperComponent,
        PageStatusWrapperComponent
    ],
    entryComponents: [
        TrashListDropdownItemsWrapperComponent,
        ModifiedTimeWrapperComponent,
        NumberOfRestrictionsWrapperComponent,
        PageStatusWrapperComponent
    ]
})
export class PageListComponentWrappersModule {}
