/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AttributePermissionsRestService, PermissionListRestServiceResponse } from 'cmscommons';
import { IRestService, ISessionService, LogService, RestServiceFactory } from 'smarteditcommons';

describe('AttributePermissionsRestService', () => {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    const TYPE_1 = 'typeA';
    const ATTR_1 = 'attrA';
    const ATTR_2 = 'attrB';

    const DEFAULT_USER_NAME = 'some user name';

    const logService = jasmine.createSpyObj<LogService>('logService', ['error']);

    const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<
        RestServiceFactory
    >('restServiceFactory', ['get']);
    const restService: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>(
        'typePermissionsRestResource',
        ['queryByPost']
    );
    const sessionService: any = jasmine.createSpyObj<ISessionService>('sessionService', [
        'getCurrentUsername'
    ]);

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    let attributePermissionsRestService: AttributePermissionsRestService;
    let attributePermissionsList: PermissionListRestServiceResponse;

    beforeEach(() => {
        logService.error.calls.reset();

        attributePermissionsList = {
            permissionsList: [
                {
                    id: TYPE_1 + '.' + ATTR_1,
                    permissions: [
                        {
                            key: 'read',
                            value: 'false'
                        },
                        {
                            key: 'change',
                            value: 'true'
                        }
                    ]
                },
                {
                    id: TYPE_1 + '.' + ATTR_2,
                    permissions: [
                        {
                            key: 'read',
                            value: 'true'
                        },
                        {
                            key: 'change',
                            value: 'false'
                        }
                    ]
                }
            ]
        };

        restServiceFactory.get.and.returnValue(restService);
        restService.queryByPost.and.returnValue(Promise.resolve(attributePermissionsList));
        sessionService.getCurrentUsername.and.returnValue(Promise.resolve(DEFAULT_USER_NAME));

        attributePermissionsRestService = new AttributePermissionsRestService(
            restServiceFactory,
            sessionService,
            logService
        );
    });

    it('GIVEN attributes are found WHEN hasReadPermissionOnAttributesInType is called THEN the promise is resolved with a typed map with the right permissions', async () => {
        // WHEN
        const promise = await attributePermissionsRestService.hasReadPermissionOnAttributesInType(
            TYPE_1,
            [ATTR_1, ATTR_2]
        );

        // THEN
        expect(promise).toEqual(
            jasmine.objectContaining({
                attrA: false,
                attrB: true
            })
        );
    });

    it('GIVEN attribute is not found WHEN hasReadPermissionOnAttributesInType is called THEN logService.error is called and read permissions are falsy', async () => {
        // GIVEN
        restService.queryByPost.and.returnValue(Promise.reject('rejected'));

        // WHEN
        const promise = await attributePermissionsRestService.hasReadPermissionOnAttributesInType(
            TYPE_1,
            [ATTR_1, ATTR_2]
        );

        // THEN
        expect(promise).toEqual(
            jasmine.objectContaining({
                attrA: false,
                attrB: false
            })
        );
        expect(logService.error).toHaveBeenCalledTimes(1);
    });

    it('GIVEN attributes are found WHEN hasChangePermissionOnAttributesInType is called THEN the promise is rejected with the right permissions', async () => {
        // WHEN
        const promise = await attributePermissionsRestService.hasChangePermissionOnAttributesInType(
            TYPE_1,
            [ATTR_1, ATTR_2]
        );

        // THEN
        expect(promise).toEqual(
            jasmine.objectContaining({
                attrA: true,
                attrB: false
            })
        );
    });

    it('GIVEN attribute is not found WHEN hasChangePermissionOnAttributesInType is called THEN logService.error is called and change permissions are falsy', async () => {
        // GIVEN
        restService.queryByPost.and.returnValue(Promise.reject('rejected'));

        // WHEN
        const promise = await attributePermissionsRestService.hasChangePermissionOnAttributesInType(
            TYPE_1,
            [ATTR_1, ATTR_2]
        );

        // THEN
        expect(promise).toEqual(
            jasmine.objectContaining({
                attrA: false,
                attrB: false
            })
        );
        expect(logService.error).toHaveBeenCalledTimes(1);
    });
});
