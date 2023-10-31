/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import { PermissionListRestServiceResponse, TypePermissionsRestService } from 'cmscommons';
import {
    IRestService,
    RestServiceFactory,
    ISessionService,
    LogService,
    functionsUtils
} from 'smarteditcommons';

describe('TypePermissionsRestService', () => {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<
        RestServiceFactory
    >('restServiceFactory', ['get']);
    const typePermissionsRestResource: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<
        IRestService<any>
    >('typePermissionsRestResource', ['queryByPost']);

    const sessionService: any = jasmine.createSpyObj<ISessionService>('sessionService', [
        'getCurrentUsername'
    ]);

    const logService = jasmine.createSpyObj<LogService>('logService', ['error']);

    const typeCodeA = 'typeA';
    const typeCodeB = 'typeB';

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    let typePermissionsRestService: TypePermissionsRestService;
    let typeABPermissionResult: PermissionListRestServiceResponse;

    beforeEach(() => {
        logService.error.calls.reset();
        typeABPermissionResult = {
            permissionsList: [
                {
                    id: typeCodeA,
                    permissions: [
                        {
                            key: 'read',
                            value: 'true'
                        },
                        {
                            key: 'change',
                            value: 'false'
                        },
                        {
                            key: 'create',
                            value: 'false'
                        },
                        {
                            key: 'remove',
                            value: 'true'
                        }
                    ]
                },
                {
                    id: typeCodeB,
                    permissions: [
                        {
                            key: 'read',
                            value: 'true'
                        },
                        {
                            key: 'change',
                            value: 'true'
                        },
                        {
                            key: 'create',
                            value: 'false'
                        },
                        {
                            key: 'remove',
                            value: 'false'
                        }
                    ]
                }
            ]
        };

        restServiceFactory.get.and.returnValue(typePermissionsRestResource);
        typePermissionsRestResource.queryByPost.and.returnValue(
            Promise.resolve(typeABPermissionResult)
        );
        sessionService.getCurrentUsername.and.returnValue(Promise.resolve('someUser'));

        // call service
        typePermissionsRestService = new TypePermissionsRestService(
            logService,
            sessionService,
            restServiceFactory
        );
    });

    it(`GIVEN types exist
        WHEN hasCreatePermissionForTypes is called
        THEN should return TypedMap object`, async () => {
        const value = await typePermissionsRestService.hasCreatePermissionForTypes([
            typeCodeA,
            typeCodeB
        ]);

        expect(value).toEqual(
            jasmine.objectContaining({
                typeA: false,
                typeB: false
            })
        );
    });

    it(`GIVEN types exist
        WHEN hasReadPermissionForTypes is called
        THEN should return TypedMap object`, async () => {
        const value = await typePermissionsRestService.hasReadPermissionForTypes([
            typeCodeA,
            typeCodeB
        ]);

        expect(value).toEqual(
            jasmine.objectContaining({
                typeA: true,
                typeB: true
            })
        );
    });

    it(`GIVEN types exist
        WHEN hasUpdatePermissionForTypes is called
        THEN should return TypedMap object`, async () => {
        const value = await typePermissionsRestService.hasUpdatePermissionForTypes([
            typeCodeA,
            typeCodeB
        ]);

        expect(value).toEqual(
            jasmine.objectContaining({
                typeA: false,
                typeB: true
            })
        );
    });

    it(`GIVEN types exist
        WHEN hasDeletePermissionForTypes is called
        THEN should return TypedMap object`, async () => {
        const value = await typePermissionsRestService.hasDeletePermissionForTypes([
            typeCodeA,
            typeCodeB
        ]);

        expect(value).toEqual(
            jasmine.objectContaining({
                typeA: true,
                typeB: false
            })
        );
    });

    it(`GIVEN types doesnot exist
        WHEN hasDeletePermissionForTypes is called
        THEN promise should be rejected`, async () => {
        typePermissionsRestResource.queryByPost.and.returnValue(Promise.reject('rejected'));

        try {
            await typePermissionsRestService.hasDeletePermissionForTypes([typeCodeA, typeCodeB]);

            functionsUtils.assertFail();
        } catch (e) {
            expect(e).toEqual('rejected');
        }
    });

    it(`GIVEN types exist
        WHEN hasAllPermissionsForTypes is called
        THEN should return TypedMap object`, async () => {
        const value = await typePermissionsRestService.hasAllPermissionsForTypes([
            typeCodeA,
            typeCodeB
        ]);

        expect(value).toEqual(
            jasmine.objectContaining({
                typeA: {
                    read: true,
                    change: false,
                    create: false,
                    remove: true
                },
                typeB: {
                    read: true,
                    change: true,
                    create: false,
                    remove: false
                }
            })
        );
    });
});
