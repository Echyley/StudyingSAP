/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { AbstractCachedRestService, CacheConfig, RestServiceFactory } from '@smart/utils';
import { IBaseCatalogs } from 'smarteditcommons/dtos/ICatalog';
import { rarelyChangingContent, userEvictionTag } from 'smarteditcommons/services/cache';

@CacheConfig({ actions: [rarelyChangingContent], tags: [userEvictionTag] })
@Injectable()
export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
    constructor(restServiceFactory: RestServiceFactory) {
        super(restServiceFactory, '/cmssmarteditwebservices/v1/sites/:siteUID/productcatalogs');
    }
}
