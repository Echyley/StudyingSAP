import { TranslateService } from '@ngx-translate/core';
import { DropdownPopulatorFetchPageResponse, DropdownPopulatorInterface, DropdownPopulatorItemPayload, DropdownPopulatorPagePayload, DropdownPopulatorPayload, GenericEditorOption, GenericEditorStackService, LanguageService, UriDropdownPopulator } from 'smarteditcommons';
/**
 * Implementation of Dropdown Populator for "CMSItemDropdown" Structure Type.
 * Methods of this service will be used to populate the dropdown rendered by GenericEditorDropdownComponent.
 */
export declare class CMSItemDropdownDropdownPopulator extends DropdownPopulatorInterface {
    private genericEditorStackService;
    private uriDropdownPopulator;
    private CMS_ITEMS_URI;
    constructor(genericEditorStackService: GenericEditorStackService, languageService: LanguageService, translateService: TranslateService, uriDropdownPopulator: UriDropdownPopulator);
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    fetchPage(payload: DropdownPopulatorPagePayload): Promise<DropdownPopulatorFetchPageResponse>;
    getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption>;
    private preparePayload;
    private getNonNestedComponents;
}
