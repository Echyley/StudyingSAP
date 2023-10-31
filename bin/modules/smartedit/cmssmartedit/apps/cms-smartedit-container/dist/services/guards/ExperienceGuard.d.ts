import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';
import { SmarteditRoutingService } from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from 'smarteditcontainer';
/**
 * Guard that prevents unauthorized user from navigating to Storefront Page.
 *
 * @internal
 * @ignore
 */
export declare class ExperienceGuard implements CanActivate {
    private catalogAwareRouteResolverHelper;
    private routing;
    constructor(catalogAwareRouteResolverHelper: CatalogAwareRouteResolverHelper, routing: SmarteditRoutingService);
    /**
     * It will redirect current user to the Landing Page if the user doesn't have a read permission to the current catalog version.
     */
    canActivate(route: ActivatedRouteSnapshot): Promise<boolean>;
}
