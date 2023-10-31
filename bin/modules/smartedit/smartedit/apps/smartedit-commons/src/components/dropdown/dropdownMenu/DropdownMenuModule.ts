/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ListModule } from '@fundamental-ngx/core/list';
import { TranslationModule } from '@smart/utils';
import { FundamentalsModule } from '../../../FundamentalsModule';
import { DropdownMenuComponent } from './DropdownMenuComponent';
import { DropdownMenuItemComponent } from './DropdownMenuItemComponent';
import { DropdownMenuItemDefaultComponent } from './DropdownMenuItemDefaultComponent';

@NgModule({
    imports: [CommonModule, FundamentalsModule, TranslationModule.forChild(), ListModule],
    declarations: [
        DropdownMenuComponent,
        DropdownMenuItemComponent,
        DropdownMenuItemDefaultComponent
    ],
    entryComponents: [DropdownMenuComponent, DropdownMenuItemDefaultComponent],
    exports: [DropdownMenuComponent, DropdownMenuItemComponent]
})
export class DropdownMenuModule {}
