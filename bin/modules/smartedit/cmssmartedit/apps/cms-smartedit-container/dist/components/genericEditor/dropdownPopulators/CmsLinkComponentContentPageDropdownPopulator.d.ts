import { TranslateService } from '@ngx-translate/core';
import { DropdownPopulatorFetchPageResponse, DropdownPopulatorInterface, DropdownPopulatorItemPayload, DropdownPopulatorPagePayload, GenericEditorOption, LanguageService, UriDropdownPopulator } from 'smarteditcommons';
import { ContextAwareCatalogService } from '../../../services';
/**
 * TODO: It might be deleted because it seems that it has been replaced by CMSItemDropdownDropdownPopulator.
 */
export declare class CmsLinkComponentContentPageDropdownPopulator extends DropdownPopulatorInterface {
    private contextAwareCatalogService;
    private uriDropdownPopulator;
    constructor(contextAwareCatalogService: ContextAwareCatalogService, languageService: LanguageService, translateService: TranslateService, uriDropdownPopulator: UriDropdownPopulator);
    fetchPage(payload: DropdownPopulatorPagePayload): Promise<DropdownPopulatorFetchPageResponse>;
    getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption>;
    isPaged(): boolean;
}
