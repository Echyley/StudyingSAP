import { CustomizationsFilter } from 'personalizationcommons';
import { IRestServiceFactory } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from './PersonalizationsmarteditContextServiceInner';
import { ActionsForContainerPage } from './types';
export declare class PersonalizationsmarteditRestService {
    protected restServiceFactory: IRestServiceFactory;
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    constructor(restServiceFactory: IRestServiceFactory, personalizationsmarteditContextService: PersonalizationsmarteditContextService);
    getCxCmsAllActionsForContainer(containerId: string, filter: CustomizationsFilter): Promise<ActionsForContainerPage>;
    private extendRequestParamObjWithCatalogAwarePathVariables;
}
