/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
    PersonalizationsmarteditCommonsComponentsModule,
    ScrollZoneModule
} from 'personalizationcommons';
import { PersonalizationsmarteditCommerceCustomizationModule } from 'personalizationsmarteditcontainer/management/commerceCustomizationView/PersonalizationsmarteditCommerceCustomizationModule';
import { NgTreeModule, SelectModule, TranslationModule } from 'smarteditcommons';

import { SePersonalizationsmarteditServicesModule } from '../../../service';
import { SegmentExpressionAsHtmlModule } from '../segmentExpressionAsHtml';
import { SegmentDragAndDropService } from './SegmentDragAndDropService';
import { SegmentItemPrinterComponent } from './SegmentItemPrinterComponent';
import { SegmentNodeComponent } from './SegmentNodeComponent';
import { SegmentViewComponent } from './SegmentViewComponent';

@NgModule({
    imports: [
        CommonModule,
        PersonalizationsmarteditCommonsComponentsModule,
        SePersonalizationsmarteditServicesModule,
        PersonalizationsmarteditCommerceCustomizationModule,
        SegmentExpressionAsHtmlModule,
        ScrollZoneModule,
        SelectModule,
        NgTreeModule,
        TranslationModule.forChild(),
        DragDropModule
    ],
    providers: [SegmentDragAndDropService],
    declarations: [SegmentViewComponent, SegmentNodeComponent, SegmentItemPrinterComponent],
    entryComponents: [SegmentViewComponent, SegmentNodeComponent, SegmentItemPrinterComponent],
    exports: [SegmentViewComponent]
})
export class PersonalizationsmarteditSegmentViewModule {}
