/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageRestrictionsRestService } from 'cmssmarteditcontainer/dao';
import { PageRestrictionsService } from 'cmssmarteditcontainer/services/pageRestrictions/PageRestrictionsService';

describe('PageRestrictionsService - ', () => {
    const MOCK_PAGES_RESTRICTIONS = {
        pageRestrictionList: [
            {
                pageId: 'homepage',
                restrictionId: 'timeRestrictionIdA'
            },
            {
                pageId: 'homepage',
                restrictionId: 'timeRestrictionIdB'
            }
        ]
    };

    let pageRestrictionsRestService: jasmine.SpyObj<PageRestrictionsRestService>;
    let service: PageRestrictionsService;
    beforeEach(() => {
        pageRestrictionsRestService = jasmine.createSpyObj('pageRestrictionsRestService', [
            'getPagesRestrictionsForPageId'
        ]);

        service = new PageRestrictionsService(pageRestrictionsRestService);
    });

    beforeEach(() => {
        pageRestrictionsRestService.getPagesRestrictionsForPageId.and.returnValue(
            Promise.resolve<any>(MOCK_PAGES_RESTRICTIONS)
        );
    });

    describe('getPageRestrictionsCountForPageUID', () => {
        it('should return the page to number of restrictions for a given page UID', async () => {
            const result = await service.getPageRestrictionsCountForPageUID('irrelevant');

            expect(result).toEqual(2);
        });
    });
});
