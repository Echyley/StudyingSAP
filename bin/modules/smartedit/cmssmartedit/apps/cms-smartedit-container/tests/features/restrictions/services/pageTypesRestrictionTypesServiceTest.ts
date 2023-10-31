/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageTypesRestrictionTypesRestService } from 'cmssmarteditcontainer/dao';
import { PageTypesRestrictionTypesService } from 'cmssmarteditcontainer/services/pageRestrictions/PageTypesRestrictionTypesService';

describe('pageTypesRestrictionTypesService', () => {
    let pageTypesRestrictionTypesService: PageTypesRestrictionTypesService;
    let pageTypesRestrictionTypesRestService: jasmine.SpyObj<PageTypesRestrictionTypesRestService>;

    const MOCK_PAGES_RESTRICTIONS = {
        pageTypeRestrictionTypeList: [
            {
                pageType: 'CatalogPage',
                restrictionType: 'CMSCatalogRestriction'
            },
            {
                pageType: 'CatalogPage',
                restrictionType: 'CMSTimeRestriction'
            },
            {
                pageType: 'CatalogPage',
                restrictionType: 'CMSUserRestriction'
            },
            {
                pageType: 'ContentPage',
                restrictionType: 'CMSTimeRestriction'
            },
            {
                pageType: 'ContentPage',
                restrictionType: 'CMSUserRestriction'
            },
            {
                pageType: 'ContentPage',
                restrictionType: 'CMSUserGroupRestriction'
            },
            {
                pageType: 'ContentPage',
                restrictionType: 'CMSUiExperienceRestriction'
            }
        ]
    };

    beforeEach(() => {
        pageTypesRestrictionTypesRestService = jasmine.createSpyObj(
            'pageTypesRestrictionTypesRestService',
            ['getRestrictionTypeCodesForPageType', 'getPageTypesRestrictionTypes']
        );

        pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes.and.callFake(() => {
            return Promise.resolve(MOCK_PAGES_RESTRICTIONS);
        });

        pageTypesRestrictionTypesService = new PageTypesRestrictionTypesService(
            pageTypesRestrictionTypesRestService
        );
    });
    // ------------------------------------------------------------------------------------------

    it('should return all pageTypesRestrictionTypes', async () => {
        const result = await pageTypesRestrictionTypesService.getPageTypesRestrictionTypes();
        expect(result).toEqual(MOCK_PAGES_RESTRICTIONS.pageTypeRestrictionTypeList);
    });

    it('should cache the results and return cache if it exists', async () => {
        const orig = await pageTypesRestrictionTypesService.getPageTypesRestrictionTypes();
        const second = await pageTypesRestrictionTypesService.getPageTypesRestrictionTypes();

        expect(
            pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes.calls.count()
        ).toBe(1);

        expect(orig).toEqual(second);
    });

    it('should return page types restriction types for specific page type', async () => {
        const result = await pageTypesRestrictionTypesService.getRestrictionTypeCodesForPageType(
            'ContentPage'
        );
        expect(result).toEqual([
            'CMSTimeRestriction',
            'CMSUserRestriction',
            'CMSUserGroupRestriction',
            'CMSUiExperienceRestriction'
        ]);
    });
});
