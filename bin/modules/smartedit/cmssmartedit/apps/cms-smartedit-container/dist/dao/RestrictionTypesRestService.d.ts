import { RestServiceFactory, TypedMap } from 'smarteditcommons';
export interface IRestrictionType {
    id?: number;
    code: string;
    name: TypedMap<string>;
}
export interface IRestrictionTypeList {
    restrictionTypes: IRestrictionType[];
}
export declare class RestrictionTypesRestService {
    private restServiceFactory;
    private readonly restrictionTypesRestService;
    constructor(restServiceFactory: RestServiceFactory);
    getRestrictionTypes(): Promise<IRestrictionTypeList>;
}
