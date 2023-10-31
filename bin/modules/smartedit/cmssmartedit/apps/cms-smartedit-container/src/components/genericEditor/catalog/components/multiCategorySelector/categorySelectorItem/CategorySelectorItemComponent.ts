/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import {
    ItemComponentData,
    ITEM_COMPONENT_DATA_TOKEN,
    LocalizedMap,
    SelectItem
} from 'smarteditcommons';

interface CategorySelectorItem extends SelectItem {
    name: LocalizedMap;
    code: string;
    catalogId: string;
    catalogVersion: string;
}

@Component({
    selector: 'se-category-selector-item',
    templateUrl: './CategorySelectorItemComponent.html',
    styleUrls: ['./CategorySelectorItemComponent.scss', '../../ProductRow.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CategorySelectorItemComponent {
    public item: CategorySelectorItem;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData<CategorySelectorItem>
    ) {
        ({ item: this.item } = data);
    }
}
