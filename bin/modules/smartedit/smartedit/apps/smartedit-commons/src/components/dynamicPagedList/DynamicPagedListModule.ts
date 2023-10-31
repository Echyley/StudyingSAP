/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SeTranslationModule } from '../../modules';
import { DataTableModule } from '../dataTable/DataTableModule';
import { PaginationModule } from '../pagination/PaginationModule';
import { SpinnerModule } from '../spinner';
import { DynamicPagedListComponent } from './DynamicPagedListComponent';

@NgModule({
    imports: [
        CommonModule,
        PaginationModule,
        DataTableModule,
        SeTranslationModule.forChild(),
        SpinnerModule
    ],
    declarations: [DynamicPagedListComponent],
    entryComponents: [DynamicPagedListComponent],
    exports: [DynamicPagedListComponent]
})
export class DynamicPagedListModule {}
