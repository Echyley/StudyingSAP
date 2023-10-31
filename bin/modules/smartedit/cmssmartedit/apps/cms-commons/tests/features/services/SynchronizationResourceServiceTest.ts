/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SynchronizationResourceService } from 'cmscommons';
import { IRestServiceFactory } from 'smarteditcommons';

describe('SynchronizationResourceService', () => {
    let service: SynchronizationResourceService;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;

    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);

        service = new SynchronizationResourceService(restServiceFactory);
    });

    describe('getPageSynchronizationGetRestService', () => {
        it('should call restServiceFactory with given url', () => {
            service.getPageSynchronizationGetRestService(null);

            const expectedUrl =
                '/cmssmarteditwebservices/v1/sites/CURRENT_PAGE_CONTEXT_SITE_ID/catalogs/CURRENT_PAGE_CONTEXT_CATALOG/versions/CURRENT_PAGE_CONTEXT_CATALOG_VERSION/synchronizations/versions/:target/pages/:pageUid';
            expect(restServiceFactory.get).toHaveBeenCalledWith(expectedUrl);
        });

        it('should call restServiceFactory with transformed url when uriContext is given', () => {
            service.getPageSynchronizationGetRestService({
                target: 'Online',
                pageUid: 'uid'
            });

            const expectedUrl =
                '/cmssmarteditwebservices/v1/sites/CURRENT_PAGE_CONTEXT_SITE_ID/catalogs/CURRENT_PAGE_CONTEXT_CATALOG/versions/CURRENT_PAGE_CONTEXT_CATALOG_VERSION/synchronizations/versions/Online/pages/uid';
            expect(restServiceFactory.get).toHaveBeenCalledWith(expectedUrl);
        });
    });

    describe('getPageSynchronizationPostRestService', () => {
        it('should call restServiceFactory with given url', () => {
            service.getPageSynchronizationPostRestService(null);

            const expectedUrl =
                '/cmssmarteditwebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/synchronizations/versions/:target';
            expect(restServiceFactory.get).toHaveBeenCalledWith(expectedUrl);
        });

        it('should call restServiceFactory with transformed url when uriContext is given', () => {
            service.getPageSynchronizationPostRestService({
                target: 'Online'
            });

            const expectedUrl =
                '/cmssmarteditwebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/synchronizations/versions/Online';
            expect(restServiceFactory.get).toHaveBeenCalledWith(expectedUrl);
        });
    });
});
