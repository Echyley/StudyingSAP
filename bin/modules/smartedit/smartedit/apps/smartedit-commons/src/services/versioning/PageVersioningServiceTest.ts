/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IRestService } from '@smart/utils';
import { IPageVersion, IRestServiceFactory } from '../interfaces';
import { PageVersioningService } from './PageVersioningService';

describe('PageVersioningService', () => {
    const mockPageUuid = 'pageuuid-1';
    const mockVersionId = 'versionid-1';

    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let pageVersionService: jasmine.SpyObj<IRestService<IPageVersion>>;
    let pageVersionRollbackService: jasmine.SpyObj<IRestService<void>>;

    let service: PageVersioningService;
    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        pageVersionService = jasmine.createSpyObj<IRestService<IPageVersion>>(
            'pageVersionService',
            ['get', 'page', 'remove', 'save']
        );
        pageVersionService.get.and.returnValue(Promise.resolve<any>({}));
        pageVersionService.page.and.returnValue(Promise.resolve<any>({}));

        pageVersionRollbackService = jasmine.createSpyObj<IRestService<void>>(
            'pageVersionRollbackService',
            ['save']
        );
        pageVersionRollbackService.save.and.returnValue(Promise.resolve());

        restServiceFactory.get.and.returnValues(pageVersionService, pageVersionRollbackService);
        service = new PageVersioningService(restServiceFactory);
    });

    it('WHEN findPageVersions is called THEN it calls the service with the right parameters', async () => {
        const mockPayload = {
            pageUuid: 'somePageUuid',
            currentPage: 123
        };

        await service.findPageVersions(mockPayload);

        expect(pageVersionService.page).toHaveBeenCalledWith(mockPayload);
    });

    it('WHEN getPageVersionForId is called THEN it calls the service with the right parameters', async () => {
        await service.getPageVersionForId(mockPageUuid, mockVersionId);

        expect(pageVersionService.get).toHaveBeenCalledWith(
            jasmine.objectContaining({
                pageUuid: mockPageUuid,
                identifier: mockVersionId
            })
        );
    });

    it('WHEN deletePageVersion is called THEN it calls the service with the right parameters', async () => {
        await service.deletePageVersion(mockPageUuid, mockVersionId);

        expect(pageVersionService.remove).toHaveBeenCalledWith(
            jasmine.objectContaining({
                pageUuid: mockPageUuid,
                identifier: mockVersionId
            })
        );
    });

    it('WHEN rollbackPageVersion is called THEN it calls the service with the right parameters', async () => {
        await service.rollbackPageVersion(mockPageUuid, mockVersionId);

        expect(pageVersionRollbackService.save).toHaveBeenCalledWith(
            jasmine.objectContaining({
                pageUuid: mockPageUuid,
                versionId: mockVersionId
            })
        );
    });
});
