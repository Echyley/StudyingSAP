import { TranslateLoader } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { TypedMap } from '../../dtos';
import { ITranslationsFetchService } from './i-translations-fetch.service';
export declare class TranslateHttpLoader implements TranslateLoader {
    private translationsFetchService;
    constructor(translationsFetchService: ITranslationsFetchService);
    /**
     * Gets the translations from the server
     */
    getTranslation(lang: string): Observable<TypedMap<string>>;
}
