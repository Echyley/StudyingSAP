import { ICatalogService, IUriContext, Page, CmsitemsRestService, CMSItem } from 'smarteditcommons';
import { IRestrictionType } from '../../../dao/RestrictionTypesRestService';
interface SelectedIdsModel {
    restriction?: string;
    restrictionType?: number;
}
export declare class RestrictionManagementSelectModel {
    private cmsitemsRestService;
    private catalogService;
    private fetchRestrictionTypes;
    private getSupportedRestrictionTypes;
    selectedIds: SelectedIdsModel;
    private model;
    private restrictions;
    private selectedRestriction;
    private supportedRestrictionTypes;
    constructor(cmsitemsRestService: CmsitemsRestService, catalogService: ICatalogService, fetchRestrictionTypes: () => Promise<IRestrictionType[]>, getSupportedRestrictionTypes: () => Promise<string[]>);
    initialize(): Promise<void>;
    getRestrictionsPaged(mask: string, pageSize: number, currentPage: number): Promise<Page<CMSItem>>;
    getRestrictionFromBackend(): Promise<CMSItem>;
    getRestrictionTypes(): Promise<IRestrictionType[]>;
    restrictionSelected(): boolean;
    restrictionTypeSelected(): boolean;
    createRestrictionSelected(name: string, uriContext: IUriContext): Promise<void>;
    getRestrictionTypeCode(): string;
    getRestriction(): Partial<CMSItem>;
    isTypeSupported(): boolean;
}
export declare class RestrictionManagementSelectModelFactory {
    private cmsitemsRestService;
    private catalogService;
    constructor(cmsitemsRestService: CmsitemsRestService, catalogService: ICatalogService);
    createRestrictionManagementSelectModel(fetchRestrictionTypes: () => Promise<IRestrictionType[]>, getSupportedRestrictionTypes: () => Promise<string[]>): RestrictionManagementSelectModel;
}
export {};
