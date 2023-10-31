/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PagesRestService } from 'cmssmarteditcontainer/dao/PagesRestService';
import { IRestService, RestServiceFactory, ICMSPage } from 'smarteditcommons';

describe('PagesRestService', () => {
    const restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
        'get'
    ]);
    const restService = jasmine.createSpyObj<IRestService<any>>('restService', ['get', 'update']);
    restServiceFactory.get.and.returnValue(restService);

    let service: PagesRestService;
    beforeEach(() => {
        service = new PagesRestService(restServiceFactory);
    });

    it('WHEN get is called THEN it will resolve with an array of Page objects', async () => {
        const mockResponse: { pages: ICMSPage[] } = {
            pages: [
                mockCmsPage('BrandsCategoryPage'),
                mockCmsPage('category'),
                mockCmsPage('productList')
            ]
        };
        restService.get.and.callFake(() => Promise.resolve<any>(mockResponse));

        const pages = await service.get([]);

        expect(pages).toBe(mockResponse.pages);
    });

    it('WHEN getById is called THEN it will resolve with Page object for given Uid', async () => {
        const mockResponse = mockCmsPage('BrandsCategoryPage');
        restService.get.and.callFake(() => Promise.resolve<any>(mockResponse));

        const page = await service.getById('BrandsCategoryPage');

        expect(page).toBe(mockResponse);
    });

    it('WHEN update is called THEN it will resolve with updated Page object', async () => {
        const mockPayload = mockCmsPage('BrandsCategoryPage');
        const mockResponse: ICMSPage = { ...mockPayload, title: { en: 'updated title' } };
        restService.update.and.callFake(() => Promise.resolve<any>(mockResponse));

        const page = await service.update('BrandsCategoryPage', mockPayload);

        expect(restService.update.calls.argsFor(0)[0]).toEqual({
            pageUid: 'BrandsCategoryPage',
            ...mockPayload
        });
        expect(page).toBe(mockResponse);
    });

    function mockCmsPage(uid: string): ICMSPage {
        return {
            uid,
            uuid: null,
            pageStatus: null,
            approvalStatus: null,
            displayStatus: null,
            masterTemplate: null,
            masterTemplateId: null,
            title: null,
            defaultPage: null,
            restrictions: null,
            homepage: null,
            name: null,
            typeCode: null,
            catalogVersion: null
        };
    }
});
