/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatetimePickerModule } from '@fundamental-ngx/core/datetime-picker';
import { FormModule as FundamentalFormModule } from '@fundamental-ngx/core/form';
import { LayoutGridModule } from '@fundamental-ngx/core/layout-grid';
import { SeTranslationModule, HelpModule } from 'smarteditcommons';
import { DatetimePickerRangeComponent } from './DatetimePickerRangeComponent';
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        FundamentalFormModule,
        SeTranslationModule.forChild(),
        HelpModule,
        DatetimePickerModule,
        LayoutGridModule
    ],
    declarations: [DatetimePickerRangeComponent],
    entryComponents: [DatetimePickerRangeComponent],
    exports: [DatetimePickerRangeComponent]
})
export class DatetimePickerRangeModule {}
