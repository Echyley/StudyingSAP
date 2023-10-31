import { TypedMap } from '@smart/utils';
import { LanguageService } from '../../../../../services/language/LanguageService';
export declare class RichTextFieldLocalizationService {
    private languageService;
    private resolvedLocaleToCKEDITORLocaleMap;
    constructor(languageService: LanguageService, resolvedLocaleToCKEDITORLocaleMap: TypedMap<string>);
    localizeCKEditor(): Promise<void>;
    private convertResolvedToCKEditorLocale;
}
