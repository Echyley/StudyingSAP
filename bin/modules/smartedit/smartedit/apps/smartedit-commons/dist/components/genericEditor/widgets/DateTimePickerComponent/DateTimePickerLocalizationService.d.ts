import { DatetimeAdapter } from '@fundamental-ngx/core';
import { Moment } from 'moment';
import { LanguageService } from '../../../../services/language/LanguageService';
/**
 * The DateTimePickerLocalizationService is responsible for both localizing the date time picker as well as the tooltips
 */
export declare class DateTimePickerLocalizationService {
    private languageService;
    constructor(languageService: LanguageService);
    setLocale(datetimeAdapter: DatetimeAdapter<Moment>): void;
    private convertToDateTimePickerLocale;
}
