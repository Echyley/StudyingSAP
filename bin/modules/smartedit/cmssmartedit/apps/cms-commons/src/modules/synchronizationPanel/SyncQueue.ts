/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatusItem } from './types';

/**
 * Queue of items to be synchronized.
 */
export class SyncQueue {
    private items: string[] = [];

    public getItems(): string[] {
        return this.items;
    }

    public addItems(items: string[]): void {
        this.items = this.items.concat(items);
    }

    public removeItem(item: ISyncStatusItem): void {
        this.items = this.items.filter((itemId) => itemId !== item.itemId);
    }

    public itemExists(item: ISyncStatusItem): boolean {
        return this.getItems().includes(item.itemId);
    }

    public isEmpty(): boolean {
        return this.getItems().length === 0;
    }

    public hasAtLeastOneItem(): boolean {
        return this.getItems().length > 0;
    }

    public emptyItems(): void {
        this.items = [];
    }
}
