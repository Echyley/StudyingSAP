/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, OnInit } from '@angular/core';

import {
    CatalogDetailsItem,
    ICatalog,
    ICatalogDetailsService,
    ICatalogVersion
} from 'smarteditcommons';

/**
 * Component responsible for displaying a catalog version details. Contains a link, called homepage, that
 * redirects to the default page with the right experience (site, catalog, and catalog version).
 *
 * Can be extended with custom items to provide new links and functionality.
 */
@Component({
    selector: 'se-catalog-version-details',
    templateUrl: './CatalogVersionDetailsComponent.html'
})
export class CatalogVersionDetailsComponent implements OnInit {
    /**
     * Object representing the parent catalog of the catalog version to display.
     */
    @Input() catalog: ICatalog;
    /** Object representing the catalog version to display. */
    @Input() catalogVersion: ICatalogVersion;
    /** Object representing the active catalog version of the parent catalog. */
    @Input() activeCatalogVersion: ICatalogVersion;
    /** The site associated with the provided catalog. */
    @Input() siteId: string;

    public leftItems: CatalogDetailsItem[];
    public rightItems: CatalogDetailsItem[];

    constructor(private catalogDetailsService: ICatalogDetailsService) {}

    ngOnInit(): void {
        const { left, right } = this.catalogDetailsService.getItems();

        this.leftItems = left;
        this.rightItems = right;
    }
}
