/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import { ICatalog, ICatalogVersion, IExperienceService } from 'smarteditcommons';

/**
 * Component responsible for displaying a thumbnail of the provided catalog. When clicked,
 * it redirects to the storefront page for the catalog's active catalog version.
 *
 */
@Component({
    selector: 'se-catalog-versions-thumbnail-carousel',
    templateUrl: './CatalogVersionsThumbnailCarouselComponent.html'
})
export class CatalogVersionsThumbnailCarouselComponent {
    @Input() catalog: ICatalog;
    @Input() catalogVersion: ICatalogVersion;
    @Input() siteId: string;

    public selectedVersion: ICatalogVersion;

    constructor(private experienceService: IExperienceService) {}

    public onClick(): void {
        this.experienceService.loadExperience({
            siteId: this.siteId,
            catalogId: this.catalog.catalogId,
            catalogVersion: this.catalogVersion.version
        });
    }
}
