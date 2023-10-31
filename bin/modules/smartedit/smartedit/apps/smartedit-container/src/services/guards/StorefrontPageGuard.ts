/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';

import { SmarteditRoutingService } from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from '../resolvers/CatalogAwareRouteResolverHelper';

/**
 * Guard that prevents unauthorized user from navigating to Storefront Page.
 *
 * @internal
 * @ignore
 */
@Injectable()
export class StorefrontPageGuard implements CanActivate {
    constructor(
        private catalogAwareResolverHelper: CatalogAwareRouteResolverHelper,
        private routing: SmarteditRoutingService
    ) {}

    /**
     * It will redirect current user to the Landing Page if the user doesn't have a read permission to the current catalog version.
     */
    canActivate(): Promise<boolean> {
        return this.catalogAwareResolverHelper.storefrontResolve().catch(() => {
            this.routing.go('/');
            return false;
        });
    }
}
