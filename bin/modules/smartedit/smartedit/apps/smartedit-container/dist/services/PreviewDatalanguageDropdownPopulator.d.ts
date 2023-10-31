import { DropdownPopulatorInterface, DropdownPopulatorPayload, GenericEditorOption, LanguageService } from 'smarteditcommons';
/**
 * Implementation of smarteditcommons' DropdownPopulatorInterface for language dropdown in
 * experience selector to populate the list of languages by making a REST call to retrieve the list of langauges for a given site.
 *
 */
export declare class PreviewDatalanguageDropdownPopulator extends DropdownPopulatorInterface {
    constructor(languageService: LanguageService);
    /**
     * Returns a promise resolving to a list of languages for a given Site ID (based on the selected catalog). The site Id is generated from the
     * selected catalog in the 'catalog' dropdown.
     */
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    /** @internal */
    private getLanguageDropdownChoices;
}
