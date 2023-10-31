import { IPageService, IPermissionService, ICatalogVersionPermissionService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from './PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from './PersonalizationsmarteditRestService';
export declare class PersonalizationsmarteditRulesAndPermissionsRegistrationService {
    private personalizationsmarteditContextService;
    private personalizationsmarteditRestService;
    private permissionService;
    private catalogVersionPermissionService;
    private pageService;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditRestService: PersonalizationsmarteditRestService, permissionService: IPermissionService, catalogVersionPermissionService: ICatalogVersionPermissionService, pageService: IPageService);
    registerRules(): void;
    private getCustomizationFilter;
}
