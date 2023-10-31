/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LinkModule } from '@fundamental-ngx/core/link';
import { TranslateModule } from '@ngx-translate/core';
import {
    SpinnerModule,
    TooltipModule,
    PopupOverlayModule,
    HasOperationPermissionDirectiveModule
} from 'smarteditcommons';
import {
    ActionableAlertComponent,
    SharedComponentButtonComponent,
    ExternalComponentButtonComponent,
    SlotSyncButtonComponent,
    SlotSynchronizationPanelWrapperComponent,
    SlotSharedButtonComponent,
    SlotUnsharedButtonComponent
} from './components';
import { SynchronizationResourceService } from './dao';
import { ComponentVisibilityAlertModule, SynchronizationPanelModule } from './modules';
import { AssetsService } from './services/AssetsService';
import { AttributePermissionsRestService } from './services/AttributePermissionsRestService';
import { CMSTimeService } from './services/CMSTimeService';
import { ComponentService } from './services/ComponentService';
import { SynchronizationService } from './services/synchronizationService';
import { TypePermissionsRestService } from './services/TypePermissionsRestService';

@NgModule({
    imports: [
        CommonModule,
        SpinnerModule,
        LinkModule,
        TranslateModule.forChild(),
        SynchronizationPanelModule,
        ComponentVisibilityAlertModule,
        TooltipModule,
        PopupOverlayModule,
        HasOperationPermissionDirectiveModule
    ],
    providers: [
        AttributePermissionsRestService,
        CMSTimeService,
        SynchronizationService,
        TypePermissionsRestService,
        ComponentService,
        SynchronizationResourceService,
        AssetsService
    ],
    declarations: [
        ActionableAlertComponent,
        SharedComponentButtonComponent,
        ExternalComponentButtonComponent,
        SlotSyncButtonComponent,
        SlotSynchronizationPanelWrapperComponent,
        SlotSharedButtonComponent,
        SlotUnsharedButtonComponent
    ],
    entryComponents: [
        SharedComponentButtonComponent,
        ExternalComponentButtonComponent,
        SlotSyncButtonComponent,
        SlotSynchronizationPanelWrapperComponent,
        SlotSharedButtonComponent,
        SlotUnsharedButtonComponent
    ],
    exports: [
        ActionableAlertComponent,
        SharedComponentButtonComponent,
        ExternalComponentButtonComponent,
        SlotSyncButtonComponent,
        SlotSharedButtonComponent,
        SlotUnsharedButtonComponent,
        SynchronizationPanelModule,
        ComponentVisibilityAlertModule
    ]
})
export class CmsCommonsModule {}
