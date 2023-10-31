/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IPerspectiveService } from 'smarteditcommons';
import { CMSModesService } from './CMSModesService';

describe('CMS Modes Service', () => {
    let service: CMSModesService;
    let perspectiveServiceMock: jasmine.SpyObj<IPerspectiveService>;

    beforeEach(() => {
        perspectiveServiceMock = jasmine.createSpyObj<IPerspectiveService>('perspectiveService', [
            'getActivePerspectiveKey'
        ]);
        service = new CMSModesService(perspectiveServiceMock);
    });

    it('it should get active perspective key and return true if the key is versioning key', async () => {
        perspectiveServiceMock.getActivePerspectiveKey.and.returnValue(
            Promise.resolve('se.cms.perspective.versioning')
        );

        const promiseResult = await service.isVersioningPerspectiveActive();

        expect(promiseResult).toBe(true);
    });

    it('it should get active perspective key and return false  if the key is not versioning key', async () => {
        perspectiveServiceMock.getActivePerspectiveKey.and.returnValue(
            Promise.resolve('se.cms.perspective.advanced')
        );

        const promiseResult = await service.isVersioningPerspectiveActive();

        expect(promiseResult).toBe(false);
    });
});
