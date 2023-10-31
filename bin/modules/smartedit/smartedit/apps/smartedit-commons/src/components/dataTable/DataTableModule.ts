/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SeTranslationModule } from '../../modules';
import { DataTableComponent } from './DataTableComponent';
import { DataTableRendererComponent } from './DataTableRenderer';

@NgModule({
    imports: [CommonModule, SeTranslationModule.forChild()],
    declarations: [DataTableComponent, DataTableRendererComponent],
    entryComponents: [DataTableComponent, DataTableRendererComponent],
    exports: [DataTableComponent, DataTableRendererComponent]
})
export class DataTableModule {}
