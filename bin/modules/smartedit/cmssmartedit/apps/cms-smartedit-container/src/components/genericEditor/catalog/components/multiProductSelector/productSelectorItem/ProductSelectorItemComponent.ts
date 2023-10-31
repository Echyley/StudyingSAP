/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { IMAGES_URL } from 'cmscommons';
import {
    ItemComponentData,
    ITEM_COMPONENT_DATA_TOKEN,
    LocalizedMap,
    SelectItem
} from 'smarteditcommons';
import { SelectorItemThumbnail } from '../../../../../genericEditor';

interface ProductSelectorItem extends SelectItem {
    name: LocalizedMap;
    code: string;
    catalogId: string;
    catalogVersion: string;
    thumbnail?: SelectorItemThumbnail;
}

@Component({
    selector: 'se-product-selector-item',
    templateUrl: './ProductSelectorItemComponent.html',
    styleUrls: ['../ProductRowContainer.scss', '../../ProductRow.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSelectorItemComponent {
    public item: ProductSelectorItem;

    private defaultThumbnail = `${IMAGES_URL}/product_thumbnail_default.png`;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData<ProductSelectorItem>
    ) {
        ({ item: this.item } = data);
    }

    public getThumbnailUrl(url: string | undefined): string {
        return url || this.defaultThumbnail;
    }
}
