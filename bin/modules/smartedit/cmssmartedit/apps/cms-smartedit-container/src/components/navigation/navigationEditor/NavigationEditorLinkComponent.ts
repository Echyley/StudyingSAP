/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import {
    CatalogDetailsItemData,
    CATALOG_DETAILS_ITEM_DATA,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

@Component({
    selector: 'se-navigation-editor-link',
    template: `
        <div class="nav-management-link-container">
            <a
                class="fd-link nav-management-link-item__link se-catalog-version__link"
                [href]="getLink()"
                translate="se.cms.cataloginfo.navigationmanagement"
                (click)="onClick()"></a>
        </div>
    `
})
export class NavigationEditorLinkComponent {
    constructor(
        @Inject(CATALOG_DETAILS_ITEM_DATA) public catalogDetails: CatalogDetailsItemData,
        private userTrackingService: IUserTrackingService
    ) {}

    public getLink(): string {
        const {
            siteId,
            catalog: { catalogId },
            catalogVersion: { version }
        } = this.catalogDetails;

        return `#/navigations/${siteId}/${catalogId}/${version}`;
    }

    public onClick(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION,
            this.catalogDetails.catalogVersion + ' - Navigation'
        );
    }
}
