/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ActivatedRouteSnapshot } from '@angular/router';
import { ExperienceGuard } from 'cmssmarteditcontainer/services/guards/ExperienceGuard';
import { SmarteditRoutingService } from 'smarteditcommons';
import { CatalogAwareRouteResolverHelper } from 'smarteditcontainer';

describe('ExperienceGuard', () => {
    let guard: ExperienceGuard;
    let catalogAwareRouteResolverHelper: jasmine.SpyObj<CatalogAwareRouteResolverHelper>;
    let routing: jasmine.SpyObj<SmarteditRoutingService>;

    const mockedActivatedRouteSnapshot = {
        params: {
            foo: 'bar'
        }
    } as any as ActivatedRouteSnapshot;

    beforeEach(() => {
        catalogAwareRouteResolverHelper = jasmine.createSpyObj<CatalogAwareRouteResolverHelper>(
            'catalogAwareRouteResolverHelper',
            ['experienceFromPathResolve']
        );
        routing = jasmine.createSpyObj<SmarteditRoutingService>('routing', ['go']);

        guard = new ExperienceGuard(catalogAwareRouteResolverHelper, routing);
    });

    it('WHEN canActivate is called AND resolver throws error THEN it should redirect and return false', async () => {
        catalogAwareRouteResolverHelper.experienceFromPathResolve.and.returnValue(Promise.reject());

        const actual = await guard.canActivate(mockedActivatedRouteSnapshot);

        expect(routing.go).toHaveBeenCalledWith('/');
        expect(catalogAwareRouteResolverHelper.experienceFromPathResolve).toHaveBeenCalledWith({
            foo: 'bar'
        } as any);
        expect(actual).toEqual(false);
    });

    it('WHEN canActivete is called AND resolver resolves then it should return true without', async () => {
        catalogAwareRouteResolverHelper.experienceFromPathResolve.and.returnValue(
            Promise.resolve(true)
        );

        const actual = await guard.canActivate(mockedActivatedRouteSnapshot);

        expect(routing.go).not.toHaveBeenCalled();
        expect(catalogAwareRouteResolverHelper.experienceFromPathResolve).toHaveBeenCalledWith({
            foo: 'bar'
        } as any);
        expect(actual).toEqual(true);
    });
});
