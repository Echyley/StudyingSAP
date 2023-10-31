/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import {
    DatetimeAdapter,
    DATE_TIME_FORMATS,
    FdDate,
    FdDatetimeAdapter,
    FD_DATETIME_FORMATS
} from '@fundamental-ngx/core/datetime';
import { Moment } from 'moment';
import { dateUtils } from '../../../../utils';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';
import { DateTimePickerLocalizationService } from './DateTimePickerLocalizationService';

@Component({
    selector: 'se-date-time-picker',
    templateUrl: './DateTimePickerComponent.html',
    providers: [
        {
            provide: DatetimeAdapter,
            useClass: FdDatetimeAdapter
        },
        {
            provide: DATE_TIME_FORMATS,
            useValue: FD_DATETIME_FORMATS
        }
    ]
})
export class DateTimePickerComponent implements OnInit {
    public placeholderText = 'se.componentform.select.date';
    public date: FdDate = FdDate.getToday();

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>,
        private localizationService: DateTimePickerLocalizationService,
        private datetimeAdapter: DatetimeAdapter<Moment>
    ) {
        if (!!this.widget.model[this.widget.qualifier]) {
            this.date = FdDate.getFdDateByDate(new Date(this.widget.model[this.widget.qualifier]));
        } else {
            this.handleDatePickerChange();
        }
    }

    public ngOnInit(): void {
        this.localizationService.setLocale(this.datetimeAdapter);
    }

    public handleDatePickerChange(): void {
        this.widget.model[this.widget.qualifier] = this.date
            ? dateUtils.formatDateAsUtc(this.date.toString())
            : undefined;
    }

    public get isDisabled(): boolean {
        return this.widget.isFieldDisabled();
    }

    public isInvalid(): boolean {
        return !this.date ? false : !this.date.isDateValid();
    }
}
