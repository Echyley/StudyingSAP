/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { IRestServiceFactory } from 'smarteditcommons';
import { PromotionsPage, PromotionsCatalog } from '../types';

@Injectable()
export class PromotionsSmarteditRestService {
    public static readonly AVAILABLE_PROMOTIONS: string =
        '/personalizationwebservices/v1/query/cxpromotionsforcatalog';

    constructor(
        protected restServiceFactory: IRestServiceFactory,
        protected smarteditUtils: PersonalizationsmarteditUtils
    ) {}

    public getPromotions(catalogVersions: PromotionsCatalog[]): Promise<PromotionsPage> {
        const restService = this.restServiceFactory.get(
            PromotionsSmarteditRestService.AVAILABLE_PROMOTIONS
        );
        const entries = [];

        catalogVersions = catalogVersions || [];

        catalogVersions.forEach((element: any, i: number) => {
            this.smarteditUtils.pushToArrayIfValueExists(entries, 'catalog' + i, element.catalog);
            this.smarteditUtils.pushToArrayIfValueExists(
                entries,
                'version' + i,
                element.catalogVersion
            );
        });

        const requestParams = {
            params: {
                entry: entries
            }
        };

        return restService.save(requestParams);
    }
}
