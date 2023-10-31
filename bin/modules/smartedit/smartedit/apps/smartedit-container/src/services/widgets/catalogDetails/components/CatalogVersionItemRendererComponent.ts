/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */

import { Component, Injector, Input, OnChanges, OnInit } from '@angular/core';

import {
    CatalogDetailsItem,
    CATALOG_DETAILS_ITEM_DATA,
    ICatalog,
    ICatalogVersion
} from 'smarteditcommons';

@Component({
    selector: 'se-catalog-version-item-renderer',
    template: `
        <ng-container>
            <div *ngIf="item.component">
                <ng-container
                    *ngComponentOutlet="item.component; injector: itemInjector"></ng-container>
            </div>
        </ng-container>
    `
})
export class CatalogVersionItemRendererComponent implements OnInit, OnChanges {
    @Input() item: CatalogDetailsItem;
    @Input() catalog: ICatalog;
    @Input() catalogVersion: ICatalogVersion;
    @Input() activeCatalogVersion: ICatalogVersion;
    @Input() siteId: string;

    public itemInjector: Injector;

    constructor(private injector: Injector) {}

    ngOnInit(): void {
        this.createInjector();
    }

    ngOnChanges(): void {
        this.createInjector();
    }

    private createInjector(): void {
        if (!this.item.component) {
            return;
        }

        this.itemInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: CATALOG_DETAILS_ITEM_DATA,
                    useValue: {
                        siteId: this.siteId,
                        catalog: this.catalog,
                        catalogVersion: this.catalogVersion,
                        activeCatalogVersion: this.activeCatalogVersion
                    }
                }
            ]
        });
    }
}
