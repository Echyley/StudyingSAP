import { TranslateService } from '@ngx-translate/core';
import { DropdownPopulatorInterface, DropdownPopulatorPayload, GenericEditorOption, ICatalogService, LanguageService, OptionsDropdownPopulator } from 'smarteditcommons';
export declare class ProductCatalogDropdownPopulator extends DropdownPopulatorInterface {
    private catalogService;
    private optionsDropdownPopulator;
    constructor(catalogService: ICatalogService, languageService: LanguageService, optionsDropdownPopulator: OptionsDropdownPopulator, translateService: TranslateService);
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    isPaged(): boolean;
    private hasCatalogOneActiveVersion;
}
