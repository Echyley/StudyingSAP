import { DropdownPopulatorInterface, DropdownPopulatorPayload, GenericEditorOption, ICatalogService, LanguageService, ISharedDataService, L10nPipe } from 'smarteditcommons';
/**
 * Implementation of DropdownPopulatorInterface for catalog dropdown in
 * experience selector to populate the list of catalogs by making a REST call to retrieve the sites and then the catalogs based on the site.
 */
export declare class PreviewDatapreviewCatalogDropdownPopulator extends DropdownPopulatorInterface {
    private catalogService;
    private sharedDataService;
    private l10nPipe;
    constructor(catalogService: ICatalogService, sharedDataService: ISharedDataService, l10nPipe: L10nPipe, languageService: LanguageService);
    /**
     *  Returns a promise resolving to a list of site - catalogs to be displayed in the experience selector.
     *
     */
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    /** @internal */
    private initCatalogVersionDropdownChoices;
    /** @internal */
    private getDropdownChoices;
    private getTranslatedCatalogVersionsOptions;
}
