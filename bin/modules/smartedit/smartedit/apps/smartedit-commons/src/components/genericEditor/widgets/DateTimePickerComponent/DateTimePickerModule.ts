/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
    DatetimeAdapter,
    DATE_TIME_FORMATS,
    FdDatetimeAdapter,
    FD_DATETIME_FORMATS
} from '@fundamental-ngx/core/datetime';
import { DatetimePickerModule as FdDatetimePickerModule } from '@fundamental-ngx/core/datetime-picker';
import { MomentDatetimeModule } from '@fundamental-ngx/moment-adapter';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../../../services/language/LanguageService';
import { RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP_VALUE } from './constants';
import { DateTimePickerComponent } from './DateTimePickerComponent';
import { DateTimePickerLocalizationService } from './DateTimePickerLocalizationService';
import { RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP } from './tokens';

/**
 * The date time picker service module is a module used for displaying a date time picker
 *
 * Use the se-date-time-picker to open the date time picker.
 *
 * Once the se-date-time-picker is opened, its DateTimePickerLocalizationService is used to localize the tooling.
 */
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        FdDatetimePickerModule,
        MomentDatetimeModule,
        TranslateModule.forChild()
    ],
    providers: [
        {
            provide: RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP,
            useValue: RESOLVED_LOCALE_TO_MOMENT_LOCALE_MAP_VALUE
        },
        {
            provide: DatetimeAdapter,
            useClass: FdDatetimeAdapter
        },
        {
            provide: DATE_TIME_FORMATS,
            useValue: FD_DATETIME_FORMATS
        },
        LanguageService,
        DateTimePickerLocalizationService
    ],
    declarations: [DateTimePickerComponent],
    entryComponents: [DateTimePickerComponent],
    exports: [DateTimePickerComponent]
})
export class DateTimePickerModule {}
