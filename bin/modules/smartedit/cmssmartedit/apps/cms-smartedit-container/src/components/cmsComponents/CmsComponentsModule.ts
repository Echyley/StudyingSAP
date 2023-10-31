/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
    CustomDropdownPopulatorsToken,
    TranslationModule,
    GenericEditorDropdownModule
} from 'smarteditcommons';
import {
    SelectComponentTypeModalComponent,
    SubTypeSelectorComponent,
    CmsDropdownItemComponent,
    CmsComponentDropdownComponent
} from './cmsItemDropdown';
import {
    NestedComponentManagementService,
    CMSItemDropdownDropdownPopulator,
    SelectComponentTypeModalService
} from './cmsItemDropdown/services';
import { ComponentMenuModule } from './componentMenu';
import { ComponentRestrictionsEditorComponent, PageRestrictionsModule } from './restrictionEditor';

@NgModule({
    imports: [
        CommonModule,
        TranslationModule.forChild(),
        ComponentMenuModule,
        PageRestrictionsModule,
        GenericEditorDropdownModule
    ],
    providers: [
        SelectComponentTypeModalService,
        NestedComponentManagementService,
        {
            provide: CustomDropdownPopulatorsToken,
            useClass: CMSItemDropdownDropdownPopulator,
            multi: true
        }
    ],
    declarations: [
        SubTypeSelectorComponent,
        SelectComponentTypeModalComponent,
        ComponentRestrictionsEditorComponent,
        CmsDropdownItemComponent,
        CmsComponentDropdownComponent
    ],
    entryComponents: [
        SubTypeSelectorComponent,
        SelectComponentTypeModalComponent,
        ComponentRestrictionsEditorComponent,
        CmsDropdownItemComponent,
        CmsComponentDropdownComponent
    ]
})
export class CmsComponentsModule {}
