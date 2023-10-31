/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PersonalizationsmarteditCommonsComponentsModule } from 'personalizationcommons';
import { ManagerViewService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewService';
import { SePersonalizationsmarteditServicesModule } from 'personalizationsmarteditcontainer/service/SePersonalizationsmarteditServicesModule';
import { HelpModule, TranslationModule } from 'smarteditcommons';
import { ToolbarModule } from 'smarteditcontainer';
import { CombinedViewToolbarContextComponent } from './CombinedViewToolbarContextComponent';
import { CustomizeToolbarContextComponent } from './CustomizeToolbarContextComponent';
import { ManageCustomizationViewMenuComponent } from './ManageCustomizationViewMenuComponent';

@NgModule({
    imports: [
        CommonModule,
        HelpModule,
        PersonalizationsmarteditCommonsComponentsModule,
        SePersonalizationsmarteditServicesModule,
        TranslationModule.forChild(),
        ToolbarModule
    ],
    declarations: [
        CustomizeToolbarContextComponent,
        ManageCustomizationViewMenuComponent,
        CombinedViewToolbarContextComponent
    ],
    entryComponents: [
        CustomizeToolbarContextComponent,
        ManageCustomizationViewMenuComponent,
        CombinedViewToolbarContextComponent
    ],
    providers: [ManagerViewService]
})
export class SePersonalizationsmarteditToolbarContextModule {}
