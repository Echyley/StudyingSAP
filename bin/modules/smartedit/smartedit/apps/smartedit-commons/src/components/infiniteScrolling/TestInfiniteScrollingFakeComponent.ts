/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewChild } from '@angular/core';
import { InfiniteScrollingComponent, TechnicalUniqueIdAware } from './InfiniteScrollingComponent';
import { fetchPage } from './InfiniteScrollingFakeChildComponent';

const defaultItems = {
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
        }
    ]
};

@Component({
    template: ` <style>
            .item {
                padding: 15px 0;
            }
            .items {
                overflow-y: auto;
                max-height: 200px;
            }
        </style>
        <div>
            <div id="default-container">
                <input id="mask" name="mask" placeholder="Enter mask" [(ngModel)]="mask" />
                <button id="scrollToBottom" (click)="scrollToBottom()">Scroll to bottom</button>
                <se-infinite-scrolling
                    dropDownContainerClass="container-class"
                    dropDownClass="holder-class"
                    [fetchPage]="fetchPage"
                    [pageSize]="pageSize"
                    [context]="defaultItems">
                    <div class="items">
                        <div *ngFor="let item of context.items" class="item" [attr.id]="item.name">
                            {{ item.id }} : {{ item.name }}
                        </div>
                    </div>
                </se-infinite-scrolling>
            </div>
            <immediate-check-container></immediate-check-container>
        </div>`
})
export class TestInfiniteScrollingFakeComponent {
    @ViewChild(InfiniteScrollingComponent)
    infiniteScrollingComponentRef: InfiniteScrollingComponent<TechnicalUniqueIdAware>;
    public pageSize = 10;
    public mask = '';
    public context: { items: any[] } = { items: [] };
    public fetchPage: (mask: string, pageSize: number, currentPage: number) => Promise<any>;

    constructor() {
        this.fetchPage = fetchPage;
        this.context = defaultItems;
    }

    public setItems(newItems: any): void {
        this.context = newItems;
    }
}
