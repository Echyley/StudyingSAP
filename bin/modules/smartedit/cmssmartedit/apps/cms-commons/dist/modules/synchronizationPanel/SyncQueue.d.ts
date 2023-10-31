import { ISyncStatusItem } from './types';
/**
 * Queue of items to be synchronized.
 */
export declare class SyncQueue {
    private items;
    getItems(): string[];
    addItems(items: string[]): void;
    removeItem(item: ISyncStatusItem): void;
    itemExists(item: ISyncStatusItem): boolean;
    isEmpty(): boolean;
    hasAtLeastOneItem(): boolean;
    emptyItems(): void;
}
