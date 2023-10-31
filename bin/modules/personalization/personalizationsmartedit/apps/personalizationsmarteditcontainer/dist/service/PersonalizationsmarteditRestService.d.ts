/// <reference types="jquery" />
import { HttpClient } from '@angular/common/http';
import { PersonalizationsmarteditUtils, CustomizationVariationFullDto, CustomizationVariationComponents, Customization } from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { RestServiceFactory } from 'smarteditcommons';
export declare class PersonalizationsmarteditRestService {
    protected restServiceFactory: RestServiceFactory;
    protected personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    protected httpClient: HttpClient;
    protected yjQuery: JQueryStatic;
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    static readonly CUSTOMIZATIONS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizations";
    static readonly CUSTOMIZATION: string;
    static readonly CUSTOMIZATION_PACKAGES = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizationpackages";
    static readonly CUSTOMIZATION_PACKAGE: string;
    static readonly ACTIONS_DETAILS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions";
    static readonly VARIATIONS: string;
    static readonly VARIATION: string;
    static readonly ACTIONS: string;
    static readonly ACTION: string;
    static readonly CXCMSC_ACTIONS_FROM_VARIATIONS = "/personalizationwebservices/v1/query/cxcmscomponentsfromvariations";
    static readonly SEGMENTS = "/personalizationwebservices/v1/segments";
    static readonly CATALOGS = "/cmswebservices/v1/sites/:siteId/cmsitems";
    static readonly CATALOG: string;
    static readonly ADD_CONTAINER = "/personalizationwebservices/v1/query/cxReplaceComponentWithContainer";
    static readonly COMPONENT_TYPES = "/cmswebservices/v1/types?category=COMPONENT";
    static readonly UPDATE_CUSTOMIZATION_RANK = "/personalizationwebservices/v1/query/cxUpdateCustomizationRank";
    static readonly CHECK_VERSION = "/personalizationwebservices/v1/query/cxCmsPageVersionCheck";
    static readonly VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS = "variations(active,actions,enabled,code,name,rank,status,catalog,catalogVersion)";
    static readonly FULL_FIELDS = "FULL";
    private actionHeaders;
    constructor(restServiceFactory: RestServiceFactory, personalizationsmarteditUtils: PersonalizationsmarteditUtils, httpClient: HttpClient, yjQuery: JQueryStatic, personalizationsmarteditContextService: PersonalizationsmarteditContextService);
    extendRequestParamObjWithCatalogAwarePathVariables(requestParam: any, catalogAware?: any): any;
    extendRequestParamObjWithCustomizatonCode(requestParam: any, customizatiodCode: string): any;
    extendRequestParamObjWithVariationCode(requestParam: any, variationCode: string): any;
    getParamsAction(oldComponentId: string, newComponentId: string, slotId: string, containerId: string, customizationId: string, variationId: string): any;
    getPathVariablesObjForModifyingActionURI(customizationId: string, variationId: string, actionId: string, filter: any): any;
    prepareURI(uri: string, pathVariables: any): any;
    getParamsForCustomizations(filter: any): any;
    getActionsDetails(filter: any): any;
    getCustomizations(filter: any): any;
    getComponentsIdsForVariation(customizationId: string, variationId: string, catalog: string, catalogVersion: string): Promise<CustomizationVariationComponents>;
    getCxCmsActionsOnPageForCustomization(customization: any, currentPage: number): any;
    getSegments(filter: any): any;
    getCustomization(filter: any): Promise<Customization>;
    createCustomization(customization: any): any;
    updateCustomization(customization: any): any;
    updateCustomizationPackage(customization: any): any;
    deleteCustomization(customizationCode: string): any;
    getVariation(customizationCode: string, variationCode: string): any;
    editVariation(customizationCode: string, variation: any): any;
    deleteVariation(customizationCode: string, variationCode: string): any;
    createVariationForCustomization(customizationCode: string, variation: any): any;
    getVariationsForCustomization(customizationCode: string, filter: any): Promise<CustomizationVariationFullDto>;
    replaceComponentWithContainer(componentId: string, slotId: string, filter: any): any;
    getActions(customizationId: string, variationId: string, filter: any): any;
    createActions(customizationId: string, variationId: string, data: any, filter: any): Promise<any>;
    addActionToContainer(componentId: string, catalogId: string, containerId: string, customizationId: string, variationId: string, filter: any): any;
    editAction(customizationId: string, variationId: string, actionId: string, newComponentId: string, newComponentCatalog: any, filter: any): Promise<any>;
    deleteAction(customizationId: string, variationId: string, actionId: string, filter: any): any;
    deleteActions(customizationId: string, variationId: string, actionIds: string, filter: any): any;
    getComponents(filter: any): any;
    getComponent(itemUuid: string): any;
    getNewComponentTypes(): any;
    updateCustomizationRank(customizationId: string, icreaseValue: any): any;
    checkVersionConflict(versionId: string): any;
}
