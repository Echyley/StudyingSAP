/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import {
    EVENTS,
    ICatalogVersionPermissionService,
    IExperience,
    IExperienceParams,
    IExperienceService,
    ISharedDataService,
    LogService,
    SystemEventService,
    EXPERIENCE_STORAGE_KEY
} from 'smarteditcommons';

/**
 * Validates if user has permission on current catalog.
 *
 * Implemented by Route Guards.
 */
@Injectable()
export class CatalogAwareRouteResolverHelper {
    /**
     * @internal
     * @ignore
     *
     * Convert to instance method after 2105 deprecation period has been exceeded.
     */
    public static executeAndCheckCatalogPermissions(
        catalogVersionPermissionService: ICatalogVersionPermissionService,
        logService: LogService,
        experienceService: IExperienceService,
        systemEventService: SystemEventService,
        operation: () => Promise<any>
    ): Promise<boolean> {
        return operation().then(
            () =>
                catalogVersionPermissionService.hasReadPermissionOnCurrent().then(
                    (hasReadPermission: boolean) => {
                        if (!hasReadPermission) {
                            logService.info(
                                'no permission to access the storefront view with this experience'
                            );
                            return Promise.reject();
                        }

                        return experienceService
                            .hasCatalogVersionChanged()
                            .then((hasCatalogVersionChanged) => {
                                if (hasCatalogVersionChanged) {
                                    systemEventService.publishAsync(EVENTS.EXPERIENCE_UPDATE);
                                }
                                return true;
                            });
                    },
                    () => {
                        logService.info(
                            'failed to evaluate permissions to access the storefront view with this experience'
                        );
                        return Promise.reject();
                    }
                ),
            (error: any) => {
                logService.error(
                    'could not retrieve experience from storage or route params',
                    error
                );
                throw new Error(error);
            }
        );
    }

    /**
     * @internal
     * @ignore
     */
    public static checkExperienceIsSet(
        experienceService: IExperienceService,
        sharedDataService: ISharedDataService
    ): Promise<IExperience> {
        return new Promise<IExperience>((resolve, reject) => {
            experienceService.getCurrentExperience().then((experience: IExperience) => {
                if (!experience) {
                    return reject();
                }

                // next line to preserve in-memory features throughout the app
                sharedDataService.set(EXPERIENCE_STORAGE_KEY, experience);

                return resolve(experience);
            });
        });
    }

    /**
     * @internal
     * @ignore
     */
    public static buildExperienceFromRoute(
        experienceService: IExperienceService,
        params: IExperienceParams
    ): Promise<IExperience> {
        return experienceService.buildAndSetExperience(params).then((experience) => {
            if (!experience) {
                return Promise.reject();
            }
            return experience;
        });
    }

    constructor(
        private logService: LogService,
        private route: ActivatedRoute,
        private systemEventService: SystemEventService,
        private experienceService: IExperienceService,
        private sharedDataService: ISharedDataService,
        private catalogVersionPermissionService: ICatalogVersionPermissionService
    ) {}

    /**
     * Checks presence of a stored experience.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    public storefrontResolve(): Promise<boolean> {
        return this.executeAndCheckCatalogPermissions(() => this.checkExperienceIsSet());
    }

    /**
     * Initializes new experience based on route params.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    public experienceFromPathResolve(params?: IExperienceParams): Promise<boolean> {
        return this.executeAndCheckCatalogPermissions(() => this.buildExperienceFromRoute(params));
    }

    /**
     * Runs operation that sets the experience and then resolves to true if the user has read permissions on current catalog.
     *
     * @internal
     * @ignore
     */
    private executeAndCheckCatalogPermissions(operation: () => Promise<any>): Promise<boolean> {
        return CatalogAwareRouteResolverHelper.executeAndCheckCatalogPermissions(
            this.catalogVersionPermissionService,
            this.logService,
            this.experienceService,
            this.systemEventService,
            operation
        );
    }

    /**
     * Resolves with the existing experience if it is set, otherwise rejects.
     *
     * @internal
     * @ignore
     */
    private checkExperienceIsSet(): Promise<IExperience> {
        return CatalogAwareRouteResolverHelper.checkExperienceIsSet(
            this.experienceService,
            this.sharedDataService
        );
    }

    /**
     * Creates and sets an experience based on active route params.
     *
     * @internal
     * @ignore
     */
    private buildExperienceFromRoute(params?: IExperienceParams): Promise<IExperience> {
        return CatalogAwareRouteResolverHelper.buildExperienceFromRoute(
            this.experienceService,
            params || (this.route.snapshot.params as IExperienceParams)
        );
    }
}
