import { CMSItemStructure, CMSItemStructureField, StructureTypeCategory } from 'cmscommons';
import { RestServiceFactory } from 'smarteditcommons';
/**
 * Used for fetching CMS Item Structures.
 */
export declare class TypeStructureRestService {
    private readonly structureRestService;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Fetches the type structure (fields) for CMS pages for a given Page Type.
     */
    getStructureByType(typeCode: string): Promise<CMSItemStructureField[]>;
    /**
     * Fetches the type structure (fields) for CMS pages for a given Page Type and mode.
     */
    getStructureByTypeAndMode(typeCode: string, mode: string, getWholeStructure?: boolean): Promise<CMSItemStructure | CMSItemStructureField[]>;
    /**
     * Fetches structures supported in the given category.
     */
    getStructuresByCategory(category: StructureTypeCategory): Promise<CMSItemStructure[]>;
}
