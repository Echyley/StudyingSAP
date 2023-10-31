import { IRestrictionType, RestrictionTypesRestService } from '../../dao/RestrictionTypesRestService';
import { PageTypesRestrictionTypesService } from './PageTypesRestrictionTypesService';
export declare class RestrictionTypesService {
    private pageTypesRestrictionTypesService;
    private restrictionTypesRestService;
    private cache;
    constructor(pageTypesRestrictionTypesService: PageTypesRestrictionTypesService, restrictionTypesRestService: RestrictionTypesRestService);
    getRestrictionTypesByPageType(pageType: string): Promise<IRestrictionType[]>;
    getRestrictionTypeForTypeCode(typeCode: string): Promise<IRestrictionType>;
    getRestrictionTypes(): Promise<IRestrictionType[]>;
}
