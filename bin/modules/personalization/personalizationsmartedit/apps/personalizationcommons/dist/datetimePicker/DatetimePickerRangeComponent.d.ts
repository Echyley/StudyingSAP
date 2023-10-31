import { EventEmitter, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { FdDate } from '@fundamental-ngx/core';
import { PersonalizationsmarteditDateUtils } from '../utils/PersonalizationsmarteditDateUtils';
export declare class DatetimePickerRangeComponent implements AfterViewInit {
    private readonly dateUtil;
    private readonly cdr;
    dateFrom: string;
    dateTo: string;
    isEditable: boolean;
    dateFromChange: EventEmitter<string>;
    dateToChange: EventEmitter<string>;
    readonly placeholderText: string;
    isFromDateValid: boolean;
    isToDateValid: boolean;
    isEndDateInThePast: boolean;
    fdDateFrom: FdDate;
    fdDateTo: FdDate;
    constructor(dateUtil: PersonalizationsmarteditDateUtils, cdr: ChangeDetectorRef);
    ngAfterViewInit(): void;
    dateFromOnChange(fdDateFrom: FdDate): void;
    dateToOnChange(fdDateTo: FdDate): void;
    private _initDateTime;
}
