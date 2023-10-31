/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { DatetimeAdapter } from '@fundamental-ngx/core';
import { Moment } from 'moment';
import { LanguageService } from '../../../../services/language/LanguageService';

/**
 * The DateTimePickerLocalizationService is responsible for both localizing the date time picker as well as the tooltips
 */
@Injectable()
export class DateTimePickerLocalizationService {
    constructor(private languageService: LanguageService) {}

    public setLocale(datetimeAdapter: DatetimeAdapter<Moment>): void {
        this.languageService.getResolveLocale().then((language: string) => {
            const momentLocale = this.convertToDateTimePickerLocale(language);
            datetimeAdapter.setLocale(momentLocale);
        });
    }

    private convertToDateTimePickerLocale(resolvedLocale: string): string {
        // for fundamental datetimepicker, it accept 'en-US' rather than 'en_US', it's different from smartedit.
        return resolvedLocale.replace('_', '-');
    }
}
