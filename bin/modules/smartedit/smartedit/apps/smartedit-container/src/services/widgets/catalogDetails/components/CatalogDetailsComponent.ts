/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, OnInit } from '@angular/core';
import { ICatalog, ICatalogVersion, ModalService } from 'smarteditcommons';
import { CatalogHierarchyModalComponent } from './CatalogHierarchyModalComponent';

import './CatalogDetailsComponent.scss';

/**
 * Component responsible for displaying a catalog details. It contains a thumbnail representing the whole
 * catalog and the list of catalog versions available to the current user.
 *
 * This component is currently used in the landing page.
 * @ignore
 */
@Component({
    selector: 'se-catalog-details',
    templateUrl: './CatalogDetailsComponent.html'
})
export class CatalogDetailsComponent implements OnInit {
    /** The catalog that needs to be displayed. */
    @Input() catalog: ICatalog;
    /** A flag that specifies if the provided catalog is associated with the selected site in the landing page. */
    @Input() isCatalogForCurrentSite: boolean;
    @Input() siteId: string;

    public activeCatalogVersion: ICatalogVersion;
    public sortedCatalogVersions: ICatalogVersion[];
    public collapsibleConfiguration: { expandedByDefault: boolean };
    public catalogDividerImage = 'static-resources/images/icon_catalog_arrow.png';

    constructor(private modalService: ModalService) {}

    ngOnInit(): void {
        this.activeCatalogVersion = this.catalog.versions.find(
            (catalogVersion) => catalogVersion.active
        );

        this.sortedCatalogVersions = this.getSortedCatalogVersions();
        this.collapsibleConfiguration = {
            expandedByDefault: this.isCatalogForCurrentSite
        };
    }

    onOpenCatalogHierarchy(): void {
        this.modalService.open({
            component: CatalogHierarchyModalComponent,
            data: {
                catalog: this.catalog
            },
            templateConfig: {
                title: 'se.catalog.hierarchy.modal.title',
                isDismissButtonVisible: true
            }
        });
    }

    private getSortedCatalogVersions(): ICatalogVersion[] {
        return [
            this.activeCatalogVersion,
            ...this.catalog.versions.filter((catalogVersion) => !catalogVersion.active)
        ];
    }
}
