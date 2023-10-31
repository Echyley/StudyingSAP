/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PageRestrictionsRestService } from '../../dao';

/**
 * Service that concerns business logic tasks related to CMS restrictions for CMS pages.
 */
@Injectable()
export class PageRestrictionsService {
    constructor(private pageRestrictionsRestService: PageRestrictionsRestService) {}

    /**
     * @returns The number of restrictions applied to the page for the given page UID.
     */
    public async getPageRestrictionsCountForPageUID(pageUID: string): Promise<number> {
        const response = await this.pageRestrictionsRestService.getPagesRestrictionsForPageId(
            pageUID
        );

        return response.pageRestrictionList.length;
    }
}
