/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    functionsUtils,
    IPermissionsRestServiceResult,
    IRestService,
    PermissionsRestService,
    RestServiceFactory
} from 'smarteditcommons';

describe('PermisionsRestService', () => {
    // Service Under Test
    let permissionsRestService: PermissionsRestService;

    // MOCKS
    const mockResource = jasmine.createSpyObj<IRestService<IPermissionsRestServiceResult>>(
        'mockResource',
        ['queryByPost']
    );
    const mockRestServiceFactory = jasmine.createSpyObj<RestServiceFactory>(
        'mockRestServiceFactory',
        ['get']
    );

    beforeEach(() => {
        mockRestServiceFactory.get.and.returnValue(mockResource);
        permissionsRestService = new PermissionsRestService(mockRestServiceFactory);
    });

    it('Successfully returns permissions', async () => {
        const results: IPermissionsRestServiceResult = {
            permissions: [
                {
                    key: 'k1',
                    value: 'v1'
                },
                {
                    key: 'k2',
                    value: 'v2'
                }
            ]
        };
        mockResource.queryByPost.and.returnValue(Promise.resolve(results));

        const result = await permissionsRestService.get({
            user: '',
            permissionNames: ''
        });

        expect(result).toEqual(results);
    });

    it('Failed resource to be rejected with reason', async () => {
        const failureReason = '42';
        mockResource.queryByPost.and.returnValue(Promise.reject(failureReason));

        try {
            await permissionsRestService.get({
                user: '',
                permissionNames: ''
            });

            functionsUtils.assertFail();
        } catch (e) {
            expect(e).toEqual(failureReason);
        }
    });
});
