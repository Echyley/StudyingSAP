/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageListLinkComponent } from 'cmssmarteditcontainer/components/pages/pageListLink/PageListLinkComponent';
import { CatalogDetailsItemData, IUserTrackingService } from 'smarteditcommons';

describe('PageListLinkComponent', () => {
    let component: PageListLinkComponent;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const mockCatalogDetails = {
        catalog: { catalogId: 'catalogId' },
        catalogVersion: { version: 'Online' },
        siteId: 'siteId'
    } as CatalogDetailsItemData;

    beforeEach(() => {
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new PageListLinkComponent(mockCatalogDetails, userTrackingService);
    });

    it('should return proper link to pages', () => {
        const actual = component.getLink();

        expect(actual).toEqual('#/pages/siteId/catalogId/Online');
    });

    it('should track user action when click this link', () => {
        component.onClick();

        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });
});
