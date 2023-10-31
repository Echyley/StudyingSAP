/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import {
    DropdownMenuModule,
    L10nPipeModule,
    MessageModule,
    SeGenericEditorModule,
    SelectModule,
    SliderPanelModule
} from 'smarteditcommons';
import { ItemManagementComponent } from '../itemManagement/ItemManagerComponent';
import {
    RestrictionEditorCriteriaSelectItemComponent,
    RestrictionManagementComponent,
    RestrictionManagementEditComponent,
    RestrictionManagementSelectComponent,
    RestrictionManagementSelectItemComponent,
    RestrictionsEditorComponent,
    RestrictionsTableComponent
} from './components';
import {
    RestrictionManagementSelectModelFactory,
    RestrictionPickerConfigService
} from './services';

@NgModule({
    imports: [
        CommonModule,
        TranslateModule.forChild(),
        SeGenericEditorModule,
        MessageModule,
        SelectModule,
        L10nPipeModule,
        DropdownMenuModule,
        SliderPanelModule,
        ButtonModule
    ],
    providers: [RestrictionPickerConfigService, RestrictionManagementSelectModelFactory],
    declarations: [
        RestrictionManagementEditComponent,
        ItemManagementComponent,
        RestrictionManagementSelectComponent,
        RestrictionManagementSelectItemComponent,
        RestrictionManagementComponent,
        RestrictionsTableComponent,
        RestrictionsEditorComponent,
        RestrictionEditorCriteriaSelectItemComponent
    ],
    entryComponents: [
        RestrictionManagementEditComponent,
        ItemManagementComponent,
        RestrictionManagementSelectComponent,
        RestrictionManagementSelectItemComponent,
        RestrictionManagementComponent,
        RestrictionsTableComponent,
        RestrictionsEditorComponent,
        RestrictionEditorCriteriaSelectItemComponent
    ],
    exports: [RestrictionsEditorComponent]
})
export class RestrictionsModule {}
