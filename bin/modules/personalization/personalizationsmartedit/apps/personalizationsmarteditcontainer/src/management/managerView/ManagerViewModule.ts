/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PopoverModule } from '@fundamental-ngx/core';
import { BusyIndicatorModule } from '@fundamental-ngx/core/busy-indicator';
import { FormModule as FundamentalFormModule } from '@fundamental-ngx/core/form';
import {
    PersonalizationsmarteditCommonsComponentsModule,
    ScrollZoneModule
} from 'personalizationcommons';
import { PersonalizationsmarteditSharedComponentsModule } from 'personalizationsmarteditcontainer/commonComponents/PersonalizationsmarteditSharedComponentsModule';
import { ManagerViewComponent } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewComponent';
import { ManagerViewGridHeader } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewGridHeader';
import { ManagerViewService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewService';
import { ManagerViewTreeComponent } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewTreeComponent';
import { ManagerViewUtilsService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewUtilsService';
import { TranslationModule, SelectModule, TooltipModule } from 'smarteditcommons';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        SelectModule,
        TooltipModule,
        ScrollZoneModule,
        FormsModule,
        FundamentalFormModule,
        DragDropModule,
        PopoverModule,
        BusyIndicatorModule,
        PersonalizationsmarteditCommonsComponentsModule,
        PersonalizationsmarteditSharedComponentsModule
    ],
    declarations: [ManagerViewComponent, ManagerViewGridHeader, ManagerViewTreeComponent],
    entryComponents: [ManagerViewComponent, ManagerViewGridHeader, ManagerViewTreeComponent],
    providers: [ManagerViewUtilsService, ManagerViewService]
})
export class ManagerViewModule {}
