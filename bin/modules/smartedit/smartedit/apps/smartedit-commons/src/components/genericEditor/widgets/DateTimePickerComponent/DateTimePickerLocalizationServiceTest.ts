/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { fakeAsync, tick } from '@angular/core/testing';
import { DatetimeAdapter } from '@fundamental-ngx/core';
import { Moment } from 'moment';
import { LanguageService } from '../../../../services';
import { DateTimePickerLocalizationService } from './DateTimePickerLocalizationService';

describe('dateTimePickerLocalizationService', () => {
    let dateTimePickerLocalizationService: DateTimePickerLocalizationService;
    let languageService: jasmine.SpyObj<LanguageService>;

    beforeEach(() => {
        languageService = jasmine.createSpyObj('languageService', ['getResolveLocale']);
        dateTimePickerLocalizationService = new DateTimePickerLocalizationService(languageService);
    });

    describe('localizeDateTimePicker', () => {
        // use 'zz' language to avoid sonar issues
        it('should set locale to zz', fakeAsync((): void => {
            const enLanguage = 'zz';
            languageService.getResolveLocale.and.returnValue(Promise.resolve(enLanguage));
            const datetimeAdapter: jasmine.SpyObj<DatetimeAdapter<Moment>> = jasmine.createSpyObj(
                'datetimeAdapter',
                ['setLocale']
            );
            dateTimePickerLocalizationService.setLocale(datetimeAdapter);
            tick();
            expect(datetimeAdapter.setLocale).toHaveBeenCalledWith(enLanguage);
        }));

        it('should set locale to zz-US', fakeAsync((): void => {
            languageService.getResolveLocale.and.returnValue(Promise.resolve('zz_US'));
            const datetimeAdapter: jasmine.SpyObj<DatetimeAdapter<Moment>> = jasmine.createSpyObj(
                'datetimeAdapter',
                ['setLocale']
            );
            dateTimePickerLocalizationService.setLocale(datetimeAdapter);
            tick();
            expect(datetimeAdapter.setLocale).toHaveBeenCalledWith('zz-US');
        }));
    });
});
