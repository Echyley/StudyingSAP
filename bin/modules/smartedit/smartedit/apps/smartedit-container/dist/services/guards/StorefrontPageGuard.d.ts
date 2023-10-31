import { CanActivate } from '@angular/router';
import { SmarteditRoutingService } from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from '../resolvers/CatalogAwareRouteResolverHelper';
/**
 * Guard that prevents unauthorized user from navigating to Storefront Page.
 *
 * @internal
 * @ignore
 */
export declare class StorefrontPageGuard implements CanActivate {
    private catalogAwareResolverHelper;
    private routing;
    constructor(catalogAwareResolverHelper: CatalogAwareRouteResolverHelper, routing: SmarteditRoutingService);
    /**
     * It will redirect current user to the Landing Page if the user doesn't have a read permission to the current catalog version.
     */
    canActivate(): Promise<boolean>;
}
