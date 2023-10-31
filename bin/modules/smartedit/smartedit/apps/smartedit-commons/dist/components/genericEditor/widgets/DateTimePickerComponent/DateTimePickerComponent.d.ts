import { OnInit } from '@angular/core';
import { DatetimeAdapter, FdDate } from '@fundamental-ngx/core/datetime';
import { Moment } from 'moment';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { DateTimePickerLocalizationService } from './DateTimePickerLocalizationService';
export declare class DateTimePickerComponent implements OnInit {
    widget: GenericEditorWidgetData<any>;
    private localizationService;
    private datetimeAdapter;
    placeholderText: string;
    date: FdDate;
    constructor(widget: GenericEditorWidgetData<any>, localizationService: DateTimePickerLocalizationService, datetimeAdapter: DatetimeAdapter<Moment>);
    ngOnInit(): void;
    handleDatePickerChange(): void;
    get isDisabled(): boolean;
    isInvalid(): boolean;
}
