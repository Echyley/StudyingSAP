import { HttpClient } from '@angular/common/http';
import { ITranslationsFetchService, PromiseUtils, TranslationMap } from 'smarteditcommons';
export declare class TranslationsFetchService extends ITranslationsFetchService {
    private httpClient;
    private promiseUtils;
    private ready;
    constructor(httpClient: HttpClient, promiseUtils: PromiseUtils);
    get(lang: string): Promise<TranslationMap>;
    isReady(): Promise<boolean>;
    waitToBeReady(): Promise<void>;
}
