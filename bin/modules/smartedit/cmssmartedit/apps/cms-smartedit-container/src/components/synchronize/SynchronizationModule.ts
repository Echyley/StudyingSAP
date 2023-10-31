/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PopoverModule, ButtonModule } from '@fundamental-ngx/core';
import { SynchronizationPanelModule } from 'cmscommons';
import {
    HasOperationPermissionDirectiveModule,
    HelpModule,
    L10nPipeModule,
    TranslationModule,
    PreventVerticalOverflowModule
} from 'smarteditcommons';
import {
    CatalogDetailsSyncComponent,
    PageSynchronizationHeaderComponent,
    PageSynchronizationPanelComponent,
    PageSynchronizationPanelModalComponent,
    PageSyncMenuToolbarItemComponent,
    SynchronizeCatalogComponent
} from './components';

@NgModule({
    imports: [
        CommonModule,
        TranslationModule.forChild(),
        PopoverModule,
        L10nPipeModule,
        HasOperationPermissionDirectiveModule,
        SynchronizationPanelModule,
        PreventVerticalOverflowModule,
        HelpModule,
        ButtonModule
    ],
    declarations: [
        SynchronizeCatalogComponent,
        PageSynchronizationHeaderComponent,
        PageSynchronizationPanelComponent,
        PageSynchronizationPanelModalComponent,
        PageSyncMenuToolbarItemComponent,
        CatalogDetailsSyncComponent
    ],
    entryComponents: [
        SynchronizeCatalogComponent,
        PageSynchronizationHeaderComponent,
        PageSynchronizationPanelComponent,
        PageSynchronizationPanelModalComponent,
        PageSyncMenuToolbarItemComponent,
        CatalogDetailsSyncComponent
    ]
})
export class SynchronizationModule {}
