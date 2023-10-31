/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { HasOperationPermissionDirectiveModule } from '../../directives';
import { FundamentalsModule } from '../../FundamentalsModule';
import { SeTranslationModule } from '../../modules';
import { FilterByFieldPipeModule, StartFromPipeModule } from '../../pipes';
import { DropdownMenuModule } from '../dropdown/dropdownMenu';
import { PaginationModule } from '../pagination';
import { TooltipModule } from '../tooltip';
import { ClientPagedListCellComponent } from './ClientPagedListCellComponent';
import { ClientPagedListComponent } from './ClientPagedListComponent';

/**
 * Provides a component to display a paginated list of items with custom renderers.
 *
 * Allows the user to search and sort the list.
 */
@NgModule({
    imports: [
        CommonModule,
        PaginationModule,
        FundamentalsModule,
        TooltipModule,
        DropdownMenuModule,
        HasOperationPermissionDirectiveModule,
        FilterByFieldPipeModule,
        StartFromPipeModule,
        SeTranslationModule.forChild()
    ],
    entryComponents: [ClientPagedListComponent],
    declarations: [ClientPagedListComponent, ClientPagedListCellComponent],
    exports: [ClientPagedListComponent]
})
export class ClientPagedListModule {}
