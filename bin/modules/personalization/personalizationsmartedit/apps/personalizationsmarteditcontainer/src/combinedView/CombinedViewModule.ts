/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonModule } from '@fundamental-ngx/core/button';
import { PersonalizationsmarteditCommonsComponentsModule } from 'personalizationcommons';
import { SelectModule, TooltipModule, HelpModule, TranslationModule } from 'smarteditcommons';
import { PersonalizationsmarteditSharedComponentsModule } from '../commonComponents/PersonalizationsmarteditSharedComponentsModule';
import { PersonalizationsmarteditDataFactoryModule } from '../dataFactory';
import { SePersonalizationsmarteditServicesModule } from '../service/';
import { CombinedViewCommonsService } from './CombinedViewCommonsService';
import {
    CombinedViewConfigureComponent,
    CombinedViewConfigureService,
    CombinedViewItemPrinterComponent
} from './combinedViewConfigure';
import { CombinedViewMenuComponent } from './CombinedViewMenuComponent';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        ButtonModule,
        SelectModule,
        TooltipModule,
        HelpModule,
        PersonalizationsmarteditCommonsComponentsModule,
        PersonalizationsmarteditDataFactoryModule,
        SePersonalizationsmarteditServicesModule,
        PersonalizationsmarteditSharedComponentsModule
    ],
    providers: [CombinedViewCommonsService, CombinedViewConfigureService],
    declarations: [
        CombinedViewConfigureComponent,
        CombinedViewItemPrinterComponent,
        CombinedViewMenuComponent
    ],
    entryComponents: [
        CombinedViewConfigureComponent,
        CombinedViewItemPrinterComponent,
        CombinedViewMenuComponent
    ]
})
export class CombinedViewModule {}
