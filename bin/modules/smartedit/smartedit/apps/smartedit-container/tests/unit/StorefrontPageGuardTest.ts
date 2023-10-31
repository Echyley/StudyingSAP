/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SmarteditRoutingService } from 'smarteditcommons';
import { StorefrontPageGuard } from 'smarteditcontainer/services/guards';
import { CatalogAwareRouteResolverHelper } from 'smarteditcontainer/services/resolvers';

describe('StorefrontPageGuard', () => {
    let catalogAwareRouteResolverHelper: jasmine.SpyObj<CatalogAwareRouteResolverHelper>;
    let routing: jasmine.SpyObj<SmarteditRoutingService>;

    let storefrontPageGuard: StorefrontPageGuard;

    beforeEach(() => {
        catalogAwareRouteResolverHelper = jasmine.createSpyObj<CatalogAwareRouteResolverHelper>(
            'catalogAwareRouteResolverHelper',
            ['storefrontResolve']
        );
        routing = jasmine.createSpyObj<SmarteditRoutingService>('routing', ['go']);

        storefrontPageGuard = new StorefrontPageGuard(catalogAwareRouteResolverHelper, routing);
    });

    it('WHEN storeFrontResolve succeed THEN it should return true', async () => {
        catalogAwareRouteResolverHelper.storefrontResolve.and.returnValue(Promise.resolve(true));

        const actual = await storefrontPageGuard.canActivate();

        expect(actual).toBe(true);
    });

    it('WHEN storefrontResolve fails THEN it should redirect to Landing Page', async () => {
        catalogAwareRouteResolverHelper.storefrontResolve.and.returnValue(Promise.reject());

        const actual = await storefrontPageGuard.canActivate();

        expect(routing.go).toHaveBeenCalled();
        expect(actual).toBe(false);
    });
});
