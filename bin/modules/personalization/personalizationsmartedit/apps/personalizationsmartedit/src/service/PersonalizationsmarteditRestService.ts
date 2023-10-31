/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CustomizationsFilter } from 'personalizationcommons';
import { IRestServiceFactory, IExperienceCatalogDescriptor } from 'smarteditcommons';
import { ACTIONS_DETAILS } from '../constants';
import { PersonalizationsmarteditContextService } from './PersonalizationsmarteditContextServiceInner';
import { ActionsForContainerPage } from './types';

interface ActionDetailsRequestParam {
    type: string;
    customizationStatus: string;
    variationStatus: string;
    catalogs: string;
    needsTotal: boolean;
    containerId: string;
    pageSize: number;
    currentPage: number;
}

interface ActionDetailsCatalogAwareRequestParam extends ActionDetailsRequestParam {
    catalogId: IExperienceCatalogDescriptor['catalogId'];
    catalogVersion: IExperienceCatalogDescriptor['catalogVersion'];
}

@Injectable()
export class PersonalizationsmarteditRestService {
    constructor(
        protected restServiceFactory: IRestServiceFactory,
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService
    ) {}

    public getCxCmsAllActionsForContainer(
        containerId: string,
        filter: CustomizationsFilter
    ): Promise<ActionsForContainerPage> {
        const restService = this.restServiceFactory.get(ACTIONS_DETAILS);
        let requestParams = {
            type: 'CXCMSACTION',
            customizationStatus: 'ENABLED',
            variationStatus: 'ENABLED',
            catalogs: 'ALL',
            needsTotal: true,
            containerId,
            pageSize: filter?.currentSize || 25,
            currentPage: filter?.currentPage || 0
        };

        requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);

        return restService.get(requestParams);
    }

    private extendRequestParamObjWithCatalogAwarePathVariables(
        requestParam: ActionDetailsRequestParam
    ): ActionDetailsCatalogAwareRequestParam {
        const experienceData =
            this.personalizationsmarteditContextService.getSeData().seExperienceData;
        const catalogAwareParams = {
            catalogId: experienceData.catalogDescriptor.catalogId,
            catalogVersion: experienceData.catalogDescriptor.catalogVersion
        };
        const requestParamExtended = {
            ...requestParam,
            ...catalogAwareParams
        };
        return requestParamExtended;
    }
}
