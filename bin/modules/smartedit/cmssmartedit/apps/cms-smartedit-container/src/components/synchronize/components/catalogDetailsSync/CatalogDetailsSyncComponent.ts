/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { CatalogDetailsItemData, CATALOG_DETAILS_ITEM_DATA } from 'smarteditcommons';

@Component({
    selector: 'se-catalog-details-sync',
    template: `
        <se-synchronize-catalog
            [catalog]="catalogDetails.catalog"
            [catalogVersion]="catalogDetails.catalogVersion"
            [activeCatalogVersion]="catalogDetails.activeCatalogVersion">
        </se-synchronize-catalog>
    `
})
export class CatalogDetailsSyncComponent {
    constructor(@Inject(CATALOG_DETAILS_ITEM_DATA) public catalogDetails: CatalogDetailsItemData) {}
}
