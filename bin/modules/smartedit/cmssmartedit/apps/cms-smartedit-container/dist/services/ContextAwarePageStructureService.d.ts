import { GenericEditorStructure, IPageService } from 'smarteditcommons';
import { TypeStructureRestService } from '../dao/TypeStructureRestService';
export declare class ContextAwarePageStructureService {
    private typeStructureRestService;
    private pageService;
    constructor(typeStructureRestService: TypeStructureRestService, pageService: IPageService);
    /**
     * Return the CMS page structure with some modifications for the context of creating a new page.
     * The field order is modified, the created/modified time fields are removed, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the new page to be created
     * @param isPrimary Flag indicating if the new page will be a primary or variation page
     *
     * @returns A modified page structure
     */
    getPageStructureForNewPage(pageTypeCode: string, isPrimary: boolean): Promise<GenericEditorStructure>;
    /**
     * Return the CMS page structure with some modifications for the context of editing the info of an existing page.
     * The field order is modified, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the page to be edited
     * @param pageId The ID of the existing page to be modified
     *
     * @returns A modified page structure
     */
    getPageStructureForPageEditing(pageTypeCode: string, pageId: string): Promise<GenericEditorStructure>;
    /**
     * Return the CMS page structure with some modifications for the context of viewing the info of an existing page.
     * The field order is modified, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the existing page
     *
     * @returns A modified page structure
     */
    getPageStructureForViewing(pageTypeCode: string): Promise<GenericEditorStructure>;
    private moveElement;
    private getOrderedFields;
    private setLabelEditability;
    private getFields;
    private removeField;
}
