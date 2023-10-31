/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PagesVariationsRestService } from 'cmssmarteditcontainer/dao/PagesVariationsRestService';
import { IRestService, RestServiceFactory } from 'smarteditcommons';

describe('PagesVariationsRestService', () => {
    const mockResponse = {
        uids: ['BrandsCategoryPage', 'category', 'productList']
    };

    const restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
        'get'
    ]);
    const restService = jasmine.createSpyObj<IRestService<typeof mockResponse>>('restService', [
        'get'
    ]);
    restServiceFactory.get.and.returnValue(restService);

    let service: PagesVariationsRestService;
    beforeEach(() => {
        service = new PagesVariationsRestService(restServiceFactory);
    });

    it('GIVEN pageId WHEN getVariationsForPrimaryPageId THEN it will resolve with an array of page variations', async () => {
        restService.get.and.callFake(() => Promise.resolve<any>(mockResponse));

        const variations = await service.getVariationsForPrimaryPageId('dummyId');

        expect(variations).toBe(mockResponse.uids);
    });
});
