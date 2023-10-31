/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import {
    ClientPagedListItem,
    CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN,
    ClientPagedListCellComponentData
} from 'smarteditcommons';

@Component({
    selector: 'se-creation-date-renderer',
    template: `<span>{{ item.creationDate | date : 'short' }}</span>`
})
export class CreationDateRendererComponent {
    public item: ClientPagedListItem;

    constructor(
        @Inject(CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN)
        private cellData: ClientPagedListCellComponentData
    ) {
        this.item = this.cellData.item;
    }
}
