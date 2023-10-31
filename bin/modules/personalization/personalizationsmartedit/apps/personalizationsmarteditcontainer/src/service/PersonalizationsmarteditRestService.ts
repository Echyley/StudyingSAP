/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import * as lodash from 'lodash';
import {
    PersonalizationsmarteditUtils,
    CustomizationVariationFullDto,
    CustomizationVariationComponents,
    Customization
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { RestServiceFactory, YJQUERY_TOKEN } from 'smarteditcommons';

@Injectable()
export class PersonalizationsmarteditRestService {
    public static readonly CUSTOMIZATIONS =
        '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizations';
    public static readonly CUSTOMIZATION =
        PersonalizationsmarteditRestService.CUSTOMIZATIONS + '/:customizationCode';

    public static readonly CUSTOMIZATION_PACKAGES =
        '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizationpackages';
    public static readonly CUSTOMIZATION_PACKAGE =
        PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGES + '/:customizationCode';

    public static readonly ACTIONS_DETAILS =
        '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions';

    public static readonly VARIATIONS =
        PersonalizationsmarteditRestService.CUSTOMIZATION + '/variations';
    public static readonly VARIATION =
        PersonalizationsmarteditRestService.VARIATIONS + '/:variationCode';

    public static readonly ACTIONS = PersonalizationsmarteditRestService.VARIATION + '/actions';
    public static readonly ACTION = PersonalizationsmarteditRestService.ACTIONS + '/:actionId';

    public static readonly CXCMSC_ACTIONS_FROM_VARIATIONS =
        '/personalizationwebservices/v1/query/cxcmscomponentsfromvariations';

    public static readonly SEGMENTS = '/personalizationwebservices/v1/segments';

    public static readonly CATALOGS = '/cmswebservices/v1/sites/:siteId/cmsitems';
    public static readonly CATALOG = PersonalizationsmarteditRestService.CATALOGS + '/:itemUuid';

    public static readonly ADD_CONTAINER =
        '/personalizationwebservices/v1/query/cxReplaceComponentWithContainer';

    public static readonly COMPONENT_TYPES = '/cmswebservices/v1/types?category=COMPONENT';

    public static readonly UPDATE_CUSTOMIZATION_RANK =
        '/personalizationwebservices/v1/query/cxUpdateCustomizationRank';
    public static readonly CHECK_VERSION =
        '/personalizationwebservices/v1/query/cxCmsPageVersionCheck';

    public static readonly VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS =
        'variations(active,actions,enabled,code,name,rank,status,catalog,catalogVersion)';

    public static readonly FULL_FIELDS = 'FULL';

    private actionHeaders = new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' });

    constructor(
        protected restServiceFactory: RestServiceFactory,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        protected httpClient: HttpClient,
        @Inject(YJQUERY_TOKEN) protected yjQuery: JQueryStatic,
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService
    ) {}

    public extendRequestParamObjWithCatalogAwarePathVariables(
        requestParam: any,
        catalogAware?: any
    ): any {
        catalogAware = catalogAware || {};
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const catalogAwareParams = {
            catalogId: catalogAware.catalog || experienceData.catalogDescriptor.catalogId,
            catalogVersion:
                catalogAware.catalogVersion || experienceData.catalogDescriptor.catalogVersion
        };
        requestParam = lodash.assign(requestParam, catalogAwareParams);
        return requestParam;
    }

    public extendRequestParamObjWithCustomizatonCode(
        requestParam: any,
        customizatiodCode: string
    ): any {
        const customizationCodeParam = {
            customizationCode: customizatiodCode
        };
        requestParam = lodash.assign(requestParam, customizationCodeParam);
        return requestParam;
    }

    public extendRequestParamObjWithVariationCode(requestParam: any, variationCode: string): any {
        const param = {
            variationCode
        };
        requestParam = lodash.assign(requestParam, param);
        return requestParam;
    }

    public getParamsAction(
        oldComponentId: string,
        newComponentId: string,
        slotId: string,
        containerId: string,
        customizationId: string,
        variationId: string
    ): any {
        const entries: any[] = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'oldComponentId',
            oldComponentId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'newComponentId',
            newComponentId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, 'slotId', slotId);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'containerId',
            containerId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'variationId',
            variationId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'customizationId',
            customizationId
        );
        return {
            params: {
                entry: entries
            }
        };
    }

    public getPathVariablesObjForModifyingActionURI(
        customizationId: string,
        variationId: string,
        actionId: string,
        filter: any
    ): any {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        filter = filter || {};
        return {
            customizationCode: customizationId,
            variationCode: variationId,
            actionId,
            catalogId: filter.catalog || experienceData.catalogDescriptor.catalogId,
            catalogVersion: filter.catalogVersion || experienceData.catalogDescriptor.catalogVersion
        };
    }

    public prepareURI(uri: string, pathVariables: any): any {
        return uri.replace(/((?:\:)(\w*)(?:\/))/g, (match, p1, p2) => pathVariables[p2] + '/');
    }

    public getParamsForCustomizations(filter: any): any {
        return {
            code: filter.code,
            pageId: filter.pageId,
            pageCatalogId: filter.pageCatalogId,
            name: filter.name,
            negatePageId: filter.negatePageId,
            catalogs: filter.catalogs,
            statuses: lodash.isUndefined(filter.statuses) ? undefined : filter.statuses.join(',')
        };
    }

    public getActionsDetails(filter: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ACTIONS_DETAILS
        );
        filter = this.extendRequestParamObjWithCatalogAwarePathVariables(filter);
        return restService.get(filter);
    }

    public getCustomizations(filter: any): any {
        filter = filter || {};
        let requestParams: any = {};

        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATIONS
        );

        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(
            requestParams,
            filter
        );

        requestParams.pageSize = filter.currentSize || 10;
        requestParams.currentPage = filter.currentPage || 0;

        this.yjQuery.extend(requestParams, this.getParamsForCustomizations(filter));

        return restService.get(requestParams);
    }

    public getComponentsIdsForVariation(
        customizationId: string,
        variationId: string,
        catalog: string,
        catalogVersion: string
    ): Promise<CustomizationVariationComponents> {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;

        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CXCMSC_ACTIONS_FROM_VARIATIONS
        );
        const entries: any[] = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'customization',
            customizationId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'variations',
            variationId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalog',
            catalog || experienceData.catalogDescriptor.catalogId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalogVersion',
            catalogVersion || experienceData.catalogDescriptor.catalogVersion
        );
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }

    public getCxCmsActionsOnPageForCustomization(customization: any, currentPage: number): any {
        const filter = {
            type: 'CXCMSACTION',
            catalogs: 'ALL',
            fields: 'FULL',
            pageId: this.personalizationsmarteditContextService.getSeData().pageId,
            pageCatalogId:
                this.personalizationsmarteditContextService.getSeData().seExperienceData.pageContext
                    .catalogId,
            customizationCode: customization.code || '',
            currentPage: currentPage || 0
        };

        return this.getActionsDetails(filter);
    }

    public getSegments(filter: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.SEGMENTS
        );
        return restService.get(filter);
    }

    public getCustomization(filter: any): Promise<Customization> {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATION,
            'customizationCode'
        );

        let requestParams = this.extendRequestParamObjWithCustomizatonCode({}, filter.code);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(
            requestParams,
            filter
        );

        return restService.get(requestParams);
    }

    public createCustomization(customization: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGES
        );

        return restService.save(
            this.extendRequestParamObjWithCatalogAwarePathVariables(customization)
        );
    }

    public updateCustomization(customization: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATION,
            'customizationCode'
        );
        customization.customizationCode = customization.code;
        return restService.update(
            this.extendRequestParamObjWithCatalogAwarePathVariables(customization)
        );
    }

    public updateCustomizationPackage(customization: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGE,
            'customizationCode'
        );
        customization.customizationCode = customization.code;
        return restService.update(
            this.extendRequestParamObjWithCatalogAwarePathVariables(customization)
        );
    }

    public deleteCustomization(customizationCode: string): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CUSTOMIZATION,
            'customizationCode'
        );

        const requestParams = {
            customizationCode
        };

        return restService.remove(
            this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams)
        );
    }

    public getVariation(customizationCode: string, variationCode: string): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.VARIATION,
            'variationCode'
        );

        let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
        requestParams = this.extendRequestParamObjWithCustomizatonCode(
            requestParams,
            customizationCode
        );

        return restService.get(requestParams);
    }

    public editVariation(customizationCode: string, variation: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.VARIATION,
            'variationCode'
        );

        variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
        variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);
        variation.variationCode = variation.code;
        return restService.update(variation);
    }

    public deleteVariation(customizationCode: string, variationCode: string): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.VARIATION,
            'variationCode'
        );

        let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
        requestParams = this.extendRequestParamObjWithCustomizatonCode(
            requestParams,
            customizationCode
        );

        return restService.remove(requestParams);
    }

    public createVariationForCustomization(customizationCode: string, variation: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.VARIATIONS
        );

        variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
        variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);

        return restService.save(variation);
    }

    public getVariationsForCustomization(
        customizationCode: string,
        filter: any
    ): Promise<CustomizationVariationFullDto> {
        const restService = this.restServiceFactory.get<CustomizationVariationFullDto>(
            PersonalizationsmarteditRestService.VARIATIONS
        );

        let requestParams: any = {};
        const varForCustFilter = filter || {};

        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(
            requestParams,
            varForCustFilter
        );
        requestParams = this.extendRequestParamObjWithCustomizatonCode(
            requestParams,
            customizationCode
        );

        requestParams.fields =
            PersonalizationsmarteditRestService.VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS;

        const includeFullFields =
            typeof varForCustFilter.includeFullFields === 'undefined'
                ? false
                : varForCustFilter.includeFullFields;

        if (includeFullFields) {
            requestParams.fields = PersonalizationsmarteditRestService.FULL_FIELDS;
        }

        return restService.get(requestParams);
    }

    public replaceComponentWithContainer(componentId: string, slotId: string, filter: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ADD_CONTAINER
        );
        const catalogParams = this.extendRequestParamObjWithCatalogAwarePathVariables({}, filter);
        const requestParams = this.getParamsAction(componentId, null, slotId, null, null, null);
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            requestParams.params.entry,
            'catalog',
            catalogParams.catalogId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            requestParams.params.entry,
            'catalogVersion',
            catalogParams.catalogVersion
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            requestParams.params.entry,
            'slotCatalog',
            filter.slotCatalog
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            requestParams.params.entry,
            'oldComponentCatalog',
            filter.oldComponentCatalog
        );

        return restService.save(requestParams);
    }

    public getActions(customizationId: string, variationId: string, filter: any): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ACTIONS
        );
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            undefined,
            filter
        );

        let requestParams = {
            fields: PersonalizationsmarteditRestService.FULL_FIELDS
        };
        requestParams = lodash.assign(requestParams, pathVariables);

        return restService.get(requestParams);
    }

    public createActions(
        customizationId: string,
        variationId: string,
        data: any,
        filter: any
    ): Promise<any> {
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            undefined,
            filter
        );
        const url = this.prepareURI(PersonalizationsmarteditRestService.ACTIONS, pathVariables);

        const httpOptions = {
            headers: this.actionHeaders
        };

        return this.httpClient.patch(url, data, httpOptions).toPromise();
    }

    public addActionToContainer(
        componentId: string,
        catalogId: string,
        containerId: string,
        customizationId: string,
        variationId: string,
        filter: any
    ): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ACTIONS
        );
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            undefined,
            filter
        );
        let requestParams = {
            type: 'cxCmsActionData',
            containerId,
            componentId,
            componentCatalog: catalogId
        };
        requestParams = lodash.assign(requestParams, pathVariables);
        return restService.save(requestParams);
    }

    public async editAction(
        customizationId: string,
        variationId: string,
        actionId: string,
        newComponentId: string,
        newComponentCatalog: any,
        filter: any
    ): Promise<any> {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ACTION,
            'actionId'
        );

        const requestParams = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            actionId,
            filter
        );

        let actionInfo: any = await restService.get(requestParams);
        actionInfo = lodash.assign(actionInfo, requestParams);
        actionInfo.componentId = newComponentId;
        actionInfo.componentCatalog = newComponentCatalog;
        return restService.update(actionInfo);
    }

    public deleteAction(
        customizationId: string,
        variationId: string,
        actionId: string,
        filter: any
    ): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.ACTION,
            'actionId'
        );

        const requestParams = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            actionId,
            filter
        );

        return restService.remove(requestParams);
    }

    public deleteActions(
        customizationId: string,
        variationId: string,
        actionIds: string,
        filter: any
    ): any {
        const pathVariables = this.getPathVariablesObjForModifyingActionURI(
            customizationId,
            variationId,
            undefined,
            filter
        );
        const url = this.prepareURI(PersonalizationsmarteditRestService.ACTIONS, pathVariables);

        const httpOptions = {
            headers: this.actionHeaders,
            body: actionIds
        };

        return this.httpClient.delete(url, httpOptions).toPromise();
    }

    public getComponents(filter: any): any {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CATALOGS
        );
        let requestParams = {
            siteId: experienceData.siteDescriptor.uid
        };
        requestParams = lodash.assign(requestParams, filter);

        return restService.get(
            this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter)
        );
    }

    public getComponent(itemUuid: string): any {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CATALOG,
            'itemUuid'
        );
        const requestParams = {
            itemUuid,
            siteId: experienceData.siteDescriptor.uid
        };

        return restService.get(requestParams);
    }

    public getNewComponentTypes(): any {
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.COMPONENT_TYPES
        );
        return restService.get();
    }

    public updateCustomizationRank(customizationId: string, icreaseValue: any): any {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.UPDATE_CUSTOMIZATION_RANK
        );
        const entries: any[] = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'customization',
            customizationId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'increaseValue',
            icreaseValue
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalog',
            experienceData.catalogDescriptor.catalogId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalogVersion',
            experienceData.catalogDescriptor.catalogVersion
        );
        const requestParams = {
            params: {
                entry: entries
            }
        };
        return restService.save(requestParams);
    }

    public checkVersionConflict(versionId: string): any {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const restService = this.restServiceFactory.get(
            PersonalizationsmarteditRestService.CHECK_VERSION
        );
        const entries: any[] = [];
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'versionId',
            versionId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalog',
            experienceData.catalogDescriptor.catalogId
        );
        this.personalizationsmarteditUtils.pushToArrayIfValueExists(
            entries,
            'catalogVersion',
            experienceData.catalogDescriptor.catalogVersion
        );
        const requestParams = {
            params: {
                entry: entries
            }
        };

        return restService.save(requestParams);
    }
}
