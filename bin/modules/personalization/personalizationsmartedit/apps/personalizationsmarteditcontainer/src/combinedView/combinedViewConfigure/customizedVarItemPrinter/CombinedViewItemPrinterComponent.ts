/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
    CombinedViewSelectItem,
    CustomizationVariation,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import { Subscription } from 'rxjs';
import { ITEM_COMPONENT_DATA_TOKEN, ItemComponentData, SelectComponent } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../../../service';
import { CombinedViewConfigureService } from '../CombinedViewConfigureService';

/**
 * Component represents the Item of Select Component of CombinedViewConfigureComponent.
 * Displayed by Item Printer Component.
 */
@Component({
    selector: 'combined-view-item-printer',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './CombinedViewItemPrinterComponent.html'
})
export class CombinedViewItemPrinterComponent implements OnInit, OnDestroy {
    public catalogFilter: string;
    public combinedViewSelectItem: CombinedViewSelectItem;
    public isSelected: boolean;
    public select: SelectComponent<CombinedViewSelectItem>;
    private catalogFilterSubscription: Subscription;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData,
        private contextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        private combinedViewConfigureService: CombinedViewConfigureService
    ) {
        ({
            item: this.combinedViewSelectItem,
            selected: this.isSelected,
            select: this.select
        } = data);
        this.catalogFilterSubscription = this.combinedViewConfigureService
            .getCatalogFilter$()
            .subscribe((catalogFilter: string) => {
                this.catalogFilter = catalogFilter;
            });
    }

    async ngOnInit(): Promise<void> {
        if (this.catalogFilter !== 'current') {
            await this.personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(
                this.data.item.variation
            );
        }
    }

    ngOnDestroy(): void {
        this.catalogFilterSubscription.unsubscribe();
    }

    public isItemFromCurrentCatalog = (itemVariation: CustomizationVariation): boolean =>
        this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
            itemVariation,
            this.contextService.getSeData()
        );

    public isItemSelected = (item: CombinedViewSelectItem): boolean =>
        this.combinedViewConfigureService.isItemSelected(item);
}
