/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PAGE_TYPES_URI } from 'cmscommons';
import { IRestService, RestServiceFactory, TypedMap, CMSPageTypes } from 'smarteditcommons';

export interface PageType {
    code: CMSPageTypes;
    description: TypedMap<string>;
    name: TypedMap<string>;
    type: string;
}

interface PageTypesResponse {
    pageTypes: PageType[];
}

/**
 * A service used to retrive all supported page types configured on the platform, and caches them for the duration of the session.
 */
@Injectable()
export class PageTypeService {
    private pageTypeRestService: IRestService<PageTypesResponse>;
    private pageTypesResponse: Promise<PageTypesResponse>;

    constructor(restServiceFactory: RestServiceFactory) {
        this.pageTypeRestService = restServiceFactory.get(PAGE_TYPES_URI);
    }

    /**
     * Returns a list of page type descriptor objects.
     */
    public getPageTypes(): Promise<PageType[]> {
        this.pageTypesResponse = this.pageTypesResponse || this.pageTypeRestService.get();
        return this.pageTypesResponse.then((response) => response.pageTypes);
    }
}
