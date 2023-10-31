/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BusyIndicatorModule } from '@fundamental-ngx/core/busy-indicator';
import { SpinnerComponent } from './SpinnerComponent';

@NgModule({
    imports: [CommonModule, BusyIndicatorModule],
    declarations: [SpinnerComponent],
    entryComponents: [SpinnerComponent],
    exports: [SpinnerComponent]
})
export class SpinnerModule {}
