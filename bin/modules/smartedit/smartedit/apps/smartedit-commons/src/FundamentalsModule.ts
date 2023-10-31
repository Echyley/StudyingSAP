/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
    MessageStripModule,
    ButtonModule,
    FormModule,
    MenuModule,
    DialogModule,
    PaginationModule,
    PopoverModule,
    BusyIndicatorModule
} from '@fundamental-ngx/core';

@NgModule({
    imports: [
        DialogModule,
        ButtonModule,
        BrowserAnimationsModule,
        FormsModule,
        PopoverModule,
        MenuModule,
        MessageStripModule,
        PaginationModule,
        BusyIndicatorModule
    ],
    exports: [
        DialogModule,
        ButtonModule,
        FormModule,
        PopoverModule,
        MenuModule,
        MessageStripModule,
        PaginationModule,
        BusyIndicatorModule
    ]
})
export class FundamentalsModule {}
