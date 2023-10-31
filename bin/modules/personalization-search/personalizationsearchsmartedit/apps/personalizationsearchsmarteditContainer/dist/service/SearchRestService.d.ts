import { PersonalizationsmarteditUtils, SeData } from 'personalizationcommons';
import { IRestServiceFactory } from 'smarteditcommons';
export declare class SearchRestService {
    private personalizationsmarteditUtils;
    private restServiceFactory;
    private readonly SEARCH_PROFILES;
    private readonly UPDATE_CUSTOMIZATION_RANK;
    private readonly GET_INDEX_TYPES_FOR_SITE;
    private seData;
    constructor(personalizationsmarteditUtils: PersonalizationsmarteditUtils, restServiceFactory: IRestServiceFactory);
    initSeData(seData: SeData): void;
    getSeExperienceData(): any;
    getSearchProfiles(filter: any): any;
    updateSearchProfileActionRank(filter: any): any;
    getIndexTypesForCatalogVersion(productCV: any): any;
}
