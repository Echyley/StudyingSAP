/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { IExperienceParams, SmarteditRoutingService } from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from 'smarteditcontainer';

/**
 * Guard that prevents unauthorized user from navigating to Storefront Page.
 *
 * @internal
 * @ignore
 */
@Injectable()
export class ExperienceGuard implements CanActivate {
    constructor(
        private catalogAwareRouteResolverHelper: CatalogAwareRouteResolverHelper,
        private routing: SmarteditRoutingService
    ) {}

    /**
     * It will redirect current user to the Landing Page if the user doesn't have a read permission to the current catalog version.
     */
    canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
        return this.catalogAwareRouteResolverHelper
            .experienceFromPathResolve(route.params as IExperienceParams)
            .catch(() => {
                this.routing.go('/');
                return false;
            });
    }
}
