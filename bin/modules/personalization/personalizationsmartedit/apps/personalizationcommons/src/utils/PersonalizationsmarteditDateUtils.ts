/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { stringUtils } from 'smarteditcommons';
import { PERSONALIZATION_DATE_FORMATS, DATE_CONSTANTS } from './PersonalizationsmarteditConstants';

@Injectable()
export class PersonalizationsmarteditDateUtils {
    constructor(private readonly translateService: TranslateService) {}

    public formatDate(dateStr: string, format: string): string {
        format = format || DATE_CONSTANTS.MOMENT_FORMAT;
        if (dateStr) {
            if (
                dateStr.match &&
                dateStr.match(/^(\d{4})\-(\d{2})\-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\+|\-)(\d{4})$/)
            ) {
                dateStr = `${dateStr.slice(0, -2)}:${dateStr.slice(-2)}`;
            }
            return moment(new Date(dateStr)).format(format);
        } else {
            return '';
        }
    }

    public formatDateWithMessage(dateStr: string, format?: string): string {
        format = format || PERSONALIZATION_DATE_FORMATS.SHORT_DATE_FORMAT;
        if (dateStr) {
            return this.formatDate(dateStr, format);
        } else {
            return this.translateService.instant(
                'personalization.toolbar.pagecustomizations.nodatespecified'
            ) as string;
        }
    }

    public isDateInThePast(modelValue: any): boolean {
        if (stringUtils.isBlank(modelValue)) {
            return false;
        } else {
            return moment(modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isBefore();
        }
    }

    public isDateValidOrEmpty(modelValue: any): boolean {
        return stringUtils.isBlank(modelValue) || this.isDateValid(modelValue);
    }

    public isDateValid(modelValue: any): boolean {
        return moment(modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isValid();
    }

    public isDateRangeValid(startDate: any, endDate: any): boolean {
        if (stringUtils.isBlank(startDate) || stringUtils.isBlank(endDate)) {
            return true;
        } else {
            return moment(new Date(startDate)).isSameOrBefore(moment(new Date(endDate)));
        }
    }

    public isDateStrFormatValid(dateStr: string, format: string): boolean {
        format = format || DATE_CONSTANTS.MOMENT_FORMAT;
        if (stringUtils.isBlank(dateStr)) {
            return false;
        } else {
            return moment(dateStr, format, true).isValid();
        }
    }
}
