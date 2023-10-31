/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    IRestService,
    ISessionService,
    RestServiceFactory,
    functionsUtils
} from 'smarteditcommons';
import { CatalogVersionPermissionRestService } from 'smarteditcontainer/services';

describe('catalogVersionPermissionRestService', () => {
    const catalogId = 'apparel-deContentCatalog';
    const catalogVersion = 'Staged';

    const SELECTD_CATALOG_PERMISSIONS = {
        catalogId: 'apparel-deContentCatalog',
        catalogVersion: 'Staged',
        permissions: [
            {
                key: 'read',
                value: 'true'
            },
            {
                key: 'write',
                value: 'false'
            }
        ],
        syncPermissions: [
            {
                canSynchronize: true,
                targetCatalogVersion: 'Online'
            }
        ]
    };

    const PREMISSIONS_API_RESPONSE = {
        permissionsList: [SELECTD_CATALOG_PERMISSIONS]
    };

    const PRINCIPAL_IDENTIFIER = 'cmsmanager';

    let restService: jasmine.SpyObj<IRestService<any>>;
    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let sessionService: jasmine.SpyObj<ISessionService>;
    let catalogVersionPermissionRestService: CatalogVersionPermissionRestService;
    /*
     * In this section, a mock REST service is created to make it possible to spy on its post
     * method and return a mocked response.
     *
     * Then, the REST service factory is mocked to return the REST service previously described.
     *
     * The session service is mocked to return a user identifier, which is required to make a call
     * to the catalog version permission API.
     */
    beforeEach(() => {
        restService = jasmine.createSpyObj('restService', ['queryByPost']);
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restService);

        sessionService = jasmine.createSpyObj('sessionService', ['getCurrentUsername']);

        catalogVersionPermissionRestService = new CatalogVersionPermissionRestService(
            restServiceFactory,
            sessionService
        );
    });

    describe('CatalogVersionPermissionService.getCatalogVersionPermissions ', () => {
        it('GIVEN the REST call suceeds WHEN getCatalogVersionPermissions is called THEN REST factory and the REST endpoint are called with the right parameters', async () => {
            // Given
            sessionService.getCurrentUsername.and.returnValue(
                Promise.resolve(PRINCIPAL_IDENTIFIER)
            );
            restService.queryByPost.and.returnValue(Promise.resolve(PREMISSIONS_API_RESPONSE));

            // When
            await catalogVersionPermissionRestService.getCatalogVersionPermissions(
                catalogId,
                catalogVersion
            );

            // Then
            const updatedResourceUri = '/permissionswebservices/v1/permissions/catalogs/search';
            expect(restServiceFactory.get).toHaveBeenCalledWith(updatedResourceUri);
            expect(restService.queryByPost).toHaveBeenCalledWith(
                { principalUid: 'cmsmanager' },
                {
                    catalogId,
                    catalogVersion
                }
            );
        });

        it('GIVEN the REST call suceeds WHEN getCatalogVersionPermissions is called THEN the promise is resolved to a object containing list of permissions', async () => {
            // Given
            sessionService.getCurrentUsername.and.returnValue(
                Promise.resolve(PRINCIPAL_IDENTIFIER)
            );
            restService.queryByPost.and.returnValue(Promise.resolve(PREMISSIONS_API_RESPONSE));

            // When
            const res = await catalogVersionPermissionRestService.getCatalogVersionPermissions(
                catalogId,
                catalogVersion
            );

            // Then
            expect(res).toEqual(SELECTD_CATALOG_PERMISSIONS);
        });

        it('GIVEN the REST call fails WHEN getCatalogVersionPermissions is aclled THEN the promise is rejected', async () => {
            // Given
            sessionService.getCurrentUsername.and.returnValue(
                Promise.resolve(PRINCIPAL_IDENTIFIER)
            );
            restService.queryByPost.and.returnValue(Promise.reject('rejected'));

            // When
            try {
                await catalogVersionPermissionRestService.getCatalogVersionPermissions(
                    catalogId,
                    catalogVersion
                );

                functionsUtils.assertFail();
            } catch (e) {
                // Then
                expect(e).toBe('rejected');
            }
        });
    });
});
