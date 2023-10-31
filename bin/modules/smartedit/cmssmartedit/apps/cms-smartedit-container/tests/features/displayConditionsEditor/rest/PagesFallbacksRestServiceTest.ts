/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PagesFallbacksRestService } from 'cmssmarteditcontainer/dao/PagesFallbacksRestService';
import {
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID,
    IRestService,
    RestServiceFactory
} from 'smarteditcommons';

describe('pagesFallbacksRestService', () => {
    const mockResponse: any = {
        uids: ['someFallbackUid', 'someOtherFallbackUid']
    };

    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let restService: jasmine.SpyObj<IRestService<typeof mockResponse>>;
    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restService = jasmine.createSpyObj<IRestService<typeof mockResponse>>('restService', [
            'get'
        ]);
        restServiceFactory.get.and.returnValue(restService);
    });

    let service: PagesFallbacksRestService;
    beforeEach(() => {
        service = new PagesFallbacksRestService(restServiceFactory);
    });

    it('GIVEN pageId WHEN getFallbacksForPageId is called THEN it will resolve with an array of fallback uids', async () => {
        restService.get.and.callFake(() => Promise.resolve(mockResponse));
        const pageId = 'somePageId';
        const fallbacks = await service.getFallbacksForPageId('somePageId');

        const restServiceGetParams = restService.get.calls.argsFor(0)[0];
        expect(restServiceGetParams).toEqual({ pageId });
        expect(fallbacks).toBe(mockResponse.uids);
    });

    it('GIVEN pageId AND uriContext WHEN getFallbacksForPageIdAndContext is called THEN it will resolve with an array of fallback uids', async () => {
        restService.get.and.callFake(() => Promise.resolve(mockResponse));
        const uriContext = {
            [CONTEXT_SITE_ID]: 'siteId',
            [CONTEXT_CATALOG]: 'catalogId',
            [CONTEXT_CATALOG_VERSION]: 'catalogVersionId'
        };

        const fallbacks = await service.getFallbacksForPageIdAndContext('somePageId', uriContext);

        const restServiceFactoryGetUri = restServiceFactory.get.calls.argsFor(1)[0];
        expect(restServiceFactoryGetUri).toMatch(
            /sites\/siteId\/catalogs\/catalogId\/versions\/catalogVersionId/
        );
        expect(fallbacks).toBe(mockResponse.uids);
    });
});
