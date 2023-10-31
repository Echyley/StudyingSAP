/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewChild } from '@angular/core';
import { Page } from '@smart/utils';
import { InfiniteScrollingComponent, TechnicalUniqueIdAware } from './InfiniteScrollingComponent';

export const items = {
    items: [
        {
            id: 0,
            name: 'item0'
        },
        {
            id: 1,
            name: 'item1'
        },
        {
            id: 2,
            name: 'item2'
        },
        {
            id: 3,
            name: 'item3'
        },
        {
            id: 4,
            name: 'item4'
        },
        {
            id: 5,
            name: 'item5'
        },
        {
            id: 6,
            name: 'item6'
        },
        {
            id: 7,
            name: 'item7'
        },
        {
            id: 8,
            name: 'item8'
        },
        {
            id: 9,
            name: 'item9'
        },
        {
            id: 10,
            name: 'item10'
        },
        {
            id: 11,
            name: 'item11'
        },
        {
            id: 12,
            name: 'item12'
        },
        {
            id: 13,
            name: 'item13'
        },
        {
            id: 14,
            name: 'item14'
        },
        {
            id: 15,
            name: 'item15'
        },
        {
            id: 16,
            name: 'item16'
        },
        {
            id: 17,
            name: 'item17'
        },
        {
            id: 18,
            name: 'item18'
        },
        {
            id: 19,
            name: 'item19'
        }
    ]
};
export function fetchPage(
    mask: string,
    pageSize: number,
    currentPage: number,
    selectedItems?: any
): Promise<Page<any>> {
    return Promise.resolve({
        items: [
            {
                id: 10,
                name: 'item10'
            }
        ]
    } as any);
}
@Component({
    selector: 'immediate-check-container',
    template: ` <style>
            .item {
                padding: 15px 0;
            }
            .items {
                overflow-y: auto;
                max-height: 200px;
            }
        </style>
        <div id="immediate-check-container">
            <se-infinite-scrolling
                dropDownContainerClass="container-class"
                dropDownClass="holder-class"
                [pageSize]="pageSize"
                [mask]="mask"
                [fetchPage]="fetchPage"
                [context]="defaultItems">
                <div class="items">
                    <ng-container *ngFor="let item of context.items; let i = index">
                        <div *ngIf="i > showItemsAboveIndex" class="item" [attr.id]="item.name">
                            {{ item.id }} : {{ item.name }}
                        </div>
                    </ng-container>
                </div>
            </se-infinite-scrolling>
        </div>`
})
export class InfiniteScrollingFakeChildComponent {
    @ViewChild(InfiniteScrollingComponent)
    infiniteScrollingComponentRef: InfiniteScrollingComponent<TechnicalUniqueIdAware>;
    public pageSize = 10;
    public showItemsAboveIndex = 7;
    public mask = '';
    public context: { items: any[] } = { items: [] };
    public fetchPage: (mask: string, pageSize: number, currentPage: number) => Promise<any>;

    constructor() {
        this.fetchPage = fetchPage;
        this.context = items;
    }
}
