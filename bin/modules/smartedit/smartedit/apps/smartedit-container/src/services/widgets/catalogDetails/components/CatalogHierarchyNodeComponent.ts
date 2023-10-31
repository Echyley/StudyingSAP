/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output
} from '@angular/core';
import { IBaseCatalog, IDropdownMenuItem, IParentCatalog } from 'smarteditcommons';
import { CatalogNavigateToSite } from '../services/CatalogNavigateToSite';
import { CatalogHierarchyNodeMenuItemComponent } from './CatalogHierarchyNodeMenuItemComponent';

import './CatalogHierarchyNodeComponent.scss';

/**
 * @ignore
 */
@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'se-catalog-hierarchy-node',
    templateUrl: './CatalogHierarchyNodeComponent.html'
})
export class CatalogHierarchyNodeComponent implements OnChanges {
    @Input()
    index: number;

    @Input()
    catalog: IBaseCatalog & IParentCatalog;

    @Input()
    siteId: string;

    /**
     * Is the last node in the tree which is the catalog
     * itself.
     */
    @Input()
    isLast: boolean;

    @Output()
    siteSelect = new EventEmitter<void>();

    dropdownItems: IDropdownMenuItem[];

    constructor(private navigateToSite: CatalogNavigateToSite) {}

    ngOnChanges(): void {
        this.dropdownItems = this.getDropdownItems();
    }

    onNavigateToSite(siteUid: string): void {
        this.navigateToSite.navigate(siteUid);
        this.siteSelect.emit();
    }

    get hasOneSite(): boolean {
        return this.catalog.sites.length === 1;
    }

    getDropdownItems(): IDropdownMenuItem[] {
        return this.catalog.sites.map(
            (site) =>
                ({
                    ...site,
                    component: CatalogHierarchyNodeMenuItemComponent
                } as IDropdownMenuItem)
        );
    }
}
