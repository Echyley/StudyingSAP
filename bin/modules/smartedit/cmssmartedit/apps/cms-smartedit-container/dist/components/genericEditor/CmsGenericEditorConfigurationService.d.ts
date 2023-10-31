import { EditorFieldMappingService, GenericEditorTabService } from 'smarteditcommons';
export declare class CmsGenericEditorConfigurationService {
    private editorFieldMappingService;
    private genericEditorTabService;
    private readonly DEFAULT_PAGE_TAB_ID;
    private readonly CATEGORIES;
    constructor(editorFieldMappingService: EditorFieldMappingService, genericEditorTabService: GenericEditorTabService);
    setDefaultEditorFieldMappings(): void;
    setDefaultTabsConfiguration(): void;
    setDefaultTabFieldMappings(): void;
    private defaultTabPredicate;
    private isPagePredicate;
    private isComponentPredicate;
}
