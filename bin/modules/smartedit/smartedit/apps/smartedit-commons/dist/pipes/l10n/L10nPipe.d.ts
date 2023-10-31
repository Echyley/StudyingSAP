import { PipeTransform } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { Observable } from 'rxjs';
import { L10nService } from '../../services/L10nService';
export declare type LocalizedMap = TypedMap<string>;
export declare type L10nPipeFilterFn = (localizedMap: LocalizedMap | string) => string;
/** @ignore */
export declare function getLocalizedFilterFn(language: string): L10nPipeFilterFn;
/**
 * Pipe for translating localized maps for the current language.
 *
 * ### Example
 *
 *      localizedMap = {
 *        en: 'dummyText in english',
 *        fr: 'dummyText in french'
 *      };
 *
 *      {{ localizedMap | seL10n | async }}
 *
 */
export declare class L10nPipe implements PipeTransform {
    private l10nService;
    private filterFn;
    constructor(l10nService: L10nService);
    transform(localizedMap: string | TypedMap<string>): Observable<string>;
}
