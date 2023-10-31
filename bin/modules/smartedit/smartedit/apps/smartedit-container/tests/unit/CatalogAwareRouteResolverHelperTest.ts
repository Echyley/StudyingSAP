/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ActivatedRoute } from '@angular/router';

import {
    ICatalogVersionPermissionService,
    IExperience,
    IExperienceParams,
    IExperienceService,
    ISharedDataService,
    LogService,
    SystemEventService,
    functionsUtils
} from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from 'smarteditcontainer/services/resolvers';

describe('CatalogAwareRouteResolverHelper', () => {
    const mockExperience = { pageId: 'pageid' } as IExperience;
    const mockNextExperience = {
        pageId: 'somePageId'
    } as IExperience;

    let logService: jasmine.SpyObj<LogService>;
    let route: jasmine.SpyObj<ActivatedRoute>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let catalogVersionPermissionService: jasmine.SpyObj<ICatalogVersionPermissionService>;

    let resolver: CatalogAwareRouteResolverHelper;
    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['info', 'error']);

        route = jasmine.createSpyObj<ActivatedRoute>('route', ['snapshot']);

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'getCurrentExperience',
            'hasCatalogVersionChanged',
            'buildAndSetExperience'
        ]);
        experienceService.getCurrentExperience.and.returnValue(Promise.resolve(mockExperience));

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['set']);

        catalogVersionPermissionService = jasmine.createSpyObj<ICatalogVersionPermissionService>(
            'catalogVersionPermissionService',
            ['hasReadPermissionOnCurrent']
        );

        resolver = new CatalogAwareRouteResolverHelper(
            logService,
            route,
            systemEventService,
            experienceService,
            sharedDataService,
            catalogVersionPermissionService
        );
    });

    describe('executeAndCheckCatalogPermissions', () => {
        it('GIVEN hasReadPermissionOnCurrent rejects THEN it should log that event', async () => {
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                Promise.reject()
            );

            try {
                // WHEN
                await resolver.storefrontResolve();

                functionsUtils.assertFail();
            } catch (error) {
                // THEN
                expect(logService.info).toHaveBeenCalled();
            }
        });
    });

    describe('storefrontResolve', () => {
        it('GIVEN there is no current experience THEN it should reject', async () => {
            // GIVEN
            experienceService.getCurrentExperience.and.returnValue(Promise.resolve(null));

            try {
                // WHEN
                await resolver.storefrontResolve();

                functionsUtils.assertFail();
            } catch (error) {
                // THEN
                expect(systemEventService.publishAsync).not.toHaveBeenCalled();
                expect(sharedDataService.set).not.toHaveBeenCalled();
            }
        });
    });

    describe('catalogVersion has changed', () => {
        beforeEach(() => {
            experienceService.getCurrentExperience.and.returnValue(
                Promise.resolve(mockNextExperience)
            );
            experienceService.hasCatalogVersionChanged.and.returnValue(Promise.resolve(true));
        });

        afterEach(() => {
            expect(sharedDataService.set).toHaveBeenCalledWith('experience', mockNextExperience);
        });

        it('GIVEN has read access to catalogVersion exists THEN experience update event is sent', async () => {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                Promise.resolve(true)
            );

            // WHEN
            const actual = await resolver.storefrontResolve();

            // THEN
            expect(actual).toBe(true);
            expect(systemEventService.publishAsync).toHaveBeenCalled();
        });

        it('GIVEN no read access to catalogVersion exists THEN it should reject AND experience update event is not sent', async () => {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                Promise.resolve(false)
            );

            try {
                // WHEN
                await resolver.storefrontResolve();

                functionsUtils.assertFail();
            } catch (error) {
                // THEN
                expect(systemEventService.publishAsync).not.toHaveBeenCalled();
            }
        });
    });

    describe('catalogVersion has not changed', () => {
        beforeEach(() => {
            experienceService.getCurrentExperience.and.returnValue(
                Promise.resolve(mockNextExperience)
            );
            experienceService.hasCatalogVersionChanged.and.returnValue(Promise.resolve(false));
        });

        it('GIVEN has read access to catalogVersion exists THEN experience update event is not sent', async () => {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                Promise.resolve(true)
            );

            // WHEN
            const actual = await resolver.storefrontResolve();

            // THEN
            expect(actual).toBe(true);
            expect(systemEventService.publishAsync).not.toHaveBeenCalled();
        });

        it('GIVEN no read access to catalogVersion exists THEN it should reject AND experience update event is not sent', async () => {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                Promise.resolve(false)
            );

            try {
                // WHEN
                await resolver.storefrontResolve();

                functionsUtils.assertFail();
            } catch (error) {
                // THEN
                expect(systemEventService.publishAsync).not.toHaveBeenCalled();
            }
        });
    });

    describe('experienceFromPathResolve', () => {
        it('GIVEN no experience could be built THEN it should reject', async () => {
            // GIVEN
            experienceService.buildAndSetExperience.and.returnValue(Promise.resolve(null));

            try {
                // WHEN
                await resolver.experienceFromPathResolve();

                functionsUtils.assertFail();
            } catch (error) {
                // THEN
                expect(error instanceof Error).toBe(true);
                expect(true).toBe(true);
            }
        });

        describe('catalogVersion has changed', () => {
            beforeEach(() => {
                experienceService.buildAndSetExperience.and.returnValue(
                    Promise.resolve(mockNextExperience)
                );
                experienceService.hasCatalogVersionChanged.and.returnValue(Promise.resolve(true));
            });

            it('GIVEN has read access to catalogVersion exists THEN experience update event is sent', async () => {
                // GIVEN
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(true)
                );

                // WHEN
                const actual = await resolver.experienceFromPathResolve();

                // THEN
                expect(actual).toBe(true);
                expect(systemEventService.publishAsync).toHaveBeenCalled();
            });

            it('GIVEN no read access to catalogVersion exists THEN it should reject AND and experience update event not sent', async () => {
                // GIVEN
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(false)
                );

                try {
                    // WHEN
                    await resolver.experienceFromPathResolve();

                    functionsUtils.assertFail();
                } catch (error) {
                    // THEN
                    expect(systemEventService.publishAsync).not.toHaveBeenCalled();
                }
            });
        });

        describe('catalogVersion has not changed', () => {
            beforeEach(() => {
                experienceService.buildAndSetExperience.and.returnValue(
                    Promise.resolve(mockNextExperience)
                );
                experienceService.hasCatalogVersionChanged.and.returnValue(Promise.resolve(false));
            });

            it('GIVEN has read access to catalogVersion exists THEN experience update event is not sent', async () => {
                // GIVEN
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(true)
                );

                // WHEN
                const actual = await resolver.experienceFromPathResolve();

                // THEN
                expect(actual).toBe(true);
                expect(systemEventService.publishAsync).not.toHaveBeenCalled();
            });

            it('GIVEN no read access to catalogVersion THEN it should reject and experience update event is not sent', async () => {
                // GIVEN
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(false)
                );

                try {
                    // WHEN
                    await resolver.experienceFromPathResolve();

                    functionsUtils.assertFail();
                } catch (error) {
                    // THEN
                    expect(systemEventService.publishAsync).not.toHaveBeenCalled();
                }
            });

            it('GIVEN no params provided THEN it should build experience from route snapshot', async () => {
                spyOn(CatalogAwareRouteResolverHelper, 'buildExperienceFromRoute').and.returnValue(
                    Promise.resolve() as any
                );
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(true)
                );
                route.snapshot.params = { foo: 'bar' };

                await resolver.experienceFromPathResolve();

                expect(
                    CatalogAwareRouteResolverHelper.buildExperienceFromRoute
                ).toHaveBeenCalledWith(jasmine.any(Object), { foo: 'bar' } as any);
            });

            it('GIVEN params are provided THEN it should build experience from given params', async () => {
                spyOn(CatalogAwareRouteResolverHelper, 'buildExperienceFromRoute').and.returnValue(
                    Promise.resolve() as any
                );
                catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue(
                    Promise.resolve(true)
                );
                route.snapshot.params = { foo: 'bar' };
                const params = ({ bar: 'baz', siteId: 'siteId' } as any) as IExperienceParams;

                await resolver.experienceFromPathResolve(params);

                expect(
                    CatalogAwareRouteResolverHelper.buildExperienceFromRoute
                ).toHaveBeenCalledWith(jasmine.any(Object), params);
            });
        });
    });
});
