/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
    ICatalogService,
    IPermissionService,
    IUriContext,
    IUrlService,
    TypedMap
} from 'smarteditcommons';

@Component({
    selector: 'se-navigation-management-page',
    templateUrl: './NavigationManagementPageComponent.html',
    styleUrls: ['./NavigationManagementPageComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavigationManagementPageComponent implements OnInit {
    public catalogName: TypedMap<string>;
    public catalogVersion: string;
    public uriContext: IUriContext;
    public readOnly: boolean;

    constructor(
        private activatedRoute: ActivatedRoute,
        private urlService: IUrlService,
        private permissionService: IPermissionService,
        private catalogService: ICatalogService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        const { siteId, catalogId, catalogVersion } = this.activatedRoute.snapshot.params;
        this.catalogVersion = catalogVersion;
        this.uriContext = this.urlService.buildUriContext(siteId, catalogId, catalogVersion);

        await Promise.all([this.setCatalogName(siteId, catalogId), this.setReadOnly()]);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private async setCatalogName(siteId: string, catalogId: string): Promise<void> {
        const catalogs = await this.catalogService.getContentCatalogsForSite(siteId);
        const foundCatalog = catalogs.find((catalog) => catalog.catalogId === catalogId);
        this.catalogName = foundCatalog?.name || {};
    }

    private async setReadOnly(): Promise<void> {
        const isPermissionGranted = await this.permissionService.isPermitted([
            {
                names: ['se.edit.navigation']
            }
        ]);
        this.readOnly = !isPermissionGranted;
    }
}
