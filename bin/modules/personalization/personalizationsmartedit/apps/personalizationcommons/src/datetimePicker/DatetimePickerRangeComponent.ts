/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    Input,
    Output,
    EventEmitter,
    ChangeDetectionStrategy,
    AfterViewInit,
    ChangeDetectorRef
} from '@angular/core';
import { FdDate } from '@fundamental-ngx/core';
import { PersonalizationsmarteditDateUtils } from '../utils/PersonalizationsmarteditDateUtils';
@Component({
    selector: 'datetime-picker-range',
    templateUrl: './DatetimePickerRangeComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatetimePickerRangeComponent implements AfterViewInit {
    @Input() dateFrom: string;
    @Input() dateTo: string;
    @Input() isEditable: boolean;

    @Output() dateFromChange: EventEmitter<string>;
    @Output() dateToChange: EventEmitter<string>;

    public readonly placeholderText: string;
    public isFromDateValid: boolean;
    public isToDateValid: boolean;
    public isEndDateInThePast: boolean;
    public fdDateFrom: FdDate;
    public fdDateTo: FdDate;

    constructor(
        private readonly dateUtil: PersonalizationsmarteditDateUtils,
        private readonly cdr: ChangeDetectorRef
    ) {
        this.placeholderText = 'personalization.commons.datetimepicker.placeholder';
        this.dateFromChange = new EventEmitter();
        this.dateToChange = new EventEmitter();
        this.isFromDateValid = true;
        this.isToDateValid = true;
        this.isEndDateInThePast = false;
    }

    ngAfterViewInit(): void {
        this._initDateTime();
    }

    public dateFromOnChange(fdDateFrom: FdDate): void {
        if (!!fdDateFrom) {
            this.isFromDateValid = fdDateFrom.isDateValid();
            this.dateFrom = this.dateUtil.formatDate(fdDateFrom.toString(), undefined);
        } else {
            // if fdDateFrom is empty, it should be valid
            this.isFromDateValid = true;
            this.dateFrom = '';
        }
        this.dateFromChange.emit(this.dateFrom);
    }

    public dateToOnChange(fdDateTo: FdDate): void {
        if (!!fdDateTo) {
            this.isToDateValid = fdDateTo.isDateValid();
            this.dateTo = this.dateUtil.formatDate(fdDateTo.toString(), undefined);
        } else {
            // if fdDateTo is empty, it should be valid
            this.isToDateValid = true;
            this.dateTo = '';
        }

        this.isEndDateInThePast = this.dateUtil.isDateInThePast(this.dateTo);
        this.dateToChange.emit(this.dateTo);
    }

    private _initDateTime(): void {
        if (!!this.dateFrom) {
            this.fdDateFrom = FdDate.getFdDateByDate(new Date(this.dateFrom));
        } else {
            this.fdDateFrom = FdDate.getToday();
            this.dateFromOnChange(this.fdDateFrom);
        }

        if (!!this.dateTo) {
            this.fdDateTo = FdDate.getFdDateByDate(new Date(this.dateTo));
        } else {
            this.fdDateTo = FdDate.getToday();
            this.dateToOnChange(this.fdDateTo);
        }
        this.cdr.detectChanges();
    }
}
