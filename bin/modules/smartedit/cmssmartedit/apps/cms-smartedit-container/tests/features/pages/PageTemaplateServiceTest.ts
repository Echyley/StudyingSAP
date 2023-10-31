/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageTemplateService } from 'cmssmarteditcontainer/services/PageTemplateService';
import {
    IRestService,
    IRestServiceFactory,
    CMSPageTypes,
    PageTemplateType
} from 'smarteditcommons'; // CMSX-10265: Same Name but UT passed

describe('PageTemplateService', () => {
    let service: PageTemplateService;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let restService: jasmine.SpyObj<IRestService<PageTemplateType>>;

    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restService = jasmine.createSpyObj<IRestService<PageTemplateType>>('restService', ['get']);

        restServiceFactory.get.and.returnValue(restService);

        service = new PageTemplateService(restServiceFactory);
    });

    describe('getPageTemplatesForType', () => {
        const returnedTemplates: PageTemplateType[] = [
            {
                frontEndName: 'layout/landingLayout2Page',
                name: 'Landing Page 2 Template',
                uid: 'LandingPage2Template',
                uuid: 'uuidLandingPage2',
                catalogVersion: 'Catalog version online'
            },
            {
                frontEndName: 'category/productListPage',
                name: 'Product List Page Template',
                uid: 'ProductListPageTemplate',
                uuid: 'uuidproductListPage',
                catalogVersion: 'Catalog version online'
            },
            {
                frontEndName: 'account/accountRegisterPage',
                name: 'account reigster page',
                uid: 'acountregisterpage',
                uuid: 'uuidacountregisterpage',
                catalogVersion: 'Catalog version online'
            },
            {
                frontEndName: 'checkout/checkoutRegisterPage',
                name: 'checkout register page',
                uid: 'checkoutresiterpage',
                uuid: 'uuidcheckoutresiterpage',
                catalogVersion: 'Catalog version online'
            }
        ];

        it('WHEN called THEN it should return templates without non supported templates', async () => {
            restService.get.and.returnValue({ templates: returnedTemplates } as any);

            const actual = await service.getPageTemplatesForType(
                { context: 'context' },
                CMSPageTypes.ContentPage
            );

            expect(restService.get).toHaveBeenCalledWith({
                pageTypeCode: CMSPageTypes.ContentPage,
                active: true,
                context: 'context'
            });
            expect(actual).toEqual({
                templates: [returnedTemplates[0], returnedTemplates[1]]
            });
        });
    });
});
