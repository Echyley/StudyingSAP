import { OperationContextService } from 'smarteditcommons';
export declare class StructuresRestService {
    private readonly TYPE_PLACEHOLDER;
    private readonly URI;
    constructor(operationContextService: OperationContextService);
    /**
     * Returns Types resource URI for given mode. If no type is given, it will use a placeholder for Item Type Code.
     *
     * E.g.
     * cmswebservices/v1/types?code=CMSCategoryRestriction&mode=ADD
     */
    getUriForContext(mode: string, type?: string): string;
}
