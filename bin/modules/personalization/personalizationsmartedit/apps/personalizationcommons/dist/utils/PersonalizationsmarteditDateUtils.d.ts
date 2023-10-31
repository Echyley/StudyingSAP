import { TranslateService } from '@ngx-translate/core';
export declare class PersonalizationsmarteditDateUtils {
    private readonly translateService;
    constructor(translateService: TranslateService);
    formatDate(dateStr: string, format: string): string;
    formatDateWithMessage(dateStr: string, format?: string): string;
    isDateInThePast(modelValue: any): boolean;
    isDateValidOrEmpty(modelValue: any): boolean;
    isDateValid(modelValue: any): boolean;
    isDateRangeValid(startDate: any, endDate: any): boolean;
    isDateStrFormatValid(dateStr: string, format: string): boolean;
}
