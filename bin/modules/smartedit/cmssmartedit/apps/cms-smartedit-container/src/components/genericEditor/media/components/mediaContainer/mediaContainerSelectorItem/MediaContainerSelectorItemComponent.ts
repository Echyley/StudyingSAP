/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { IMAGES_URL } from 'cmscommons';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN, SelectItem } from 'smarteditcommons';

interface MediaContainerSelectItem extends SelectItem {
    thumbnailUrl: string;
    qualifier: string;
}

const ImagePlaceholderUrl = `${IMAGES_URL}/image_placeholder.png`;

@Component({
    selector: 'se-media-container-selector-item',
    templateUrl: './MediaContainerSelectorItemComponent.html',
    styleUrls: ['./MediaContainerSelectorItemComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaContainerSelectorItemComponent {
    public item: MediaContainerSelectItem;
    public isSelected: boolean;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) data: ItemComponentData<MediaContainerSelectItem>
    ) {
        ({ item: this.item, selected: this.isSelected } = data);
    }

    public getThumbnailUrl(thumbnailUrl: string | undefined): string {
        return thumbnailUrl || ImagePlaceholderUrl;
    }
}
