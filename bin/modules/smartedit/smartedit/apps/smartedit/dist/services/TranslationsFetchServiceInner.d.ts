import { ITranslationsFetchService, TypedMap } from 'smarteditcommons';
export declare class TranslationsFetchService extends ITranslationsFetchService {
    get(lang: string): Promise<TypedMap<string>>;
    isReady(): Promise<boolean>;
    waitToBeReady(): Promise<void>;
}
