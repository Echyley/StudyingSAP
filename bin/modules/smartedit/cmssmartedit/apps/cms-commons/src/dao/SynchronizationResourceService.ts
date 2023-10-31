/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, IRestServiceFactory, IUriContext, URIBuilder } from 'smarteditcommons';
import { ISyncStatus, ISyncJob } from '../dtos';
import {
    GET_PAGE_SYNCHRONIZATION_RESOURCE_URI,
    POST_PAGE_SYNCHRONIZATION_RESOURCE_URI
} from './resourceLocationsConstants';

@Injectable()
export class SynchronizationResourceService {
    constructor(private restServiceFactory: IRestServiceFactory) {}

    public getPageSynchronizationGetRestService(
        uriContext: IUriContext
    ): IRestService<ISyncStatus> {
        const getURI = new URIBuilder(GET_PAGE_SYNCHRONIZATION_RESOURCE_URI)
            .replaceParams(uriContext)
            .build();
        return this.restServiceFactory.get(getURI);
    }

    public getPageSynchronizationPostRestService(uriContext: IUriContext): IRestService<ISyncJob> {
        const postURI = new URIBuilder(POST_PAGE_SYNCHRONIZATION_RESOURCE_URI)
            .replaceParams(uriContext)
            .build();
        return this.restServiceFactory.get(postURI);
    }
}
