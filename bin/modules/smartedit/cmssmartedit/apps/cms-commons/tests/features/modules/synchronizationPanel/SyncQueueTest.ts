/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatusItem } from 'cmscommons';
import { SyncQueue } from 'cmscommons/modules/synchronizationPanel/SyncQueue';

describe('SyncQueue', () => {
    let syncQueue: SyncQueue;
    beforeEach(() => {
        syncQueue = new SyncQueue();
    });

    it('gets items properly', () => {
        expect(syncQueue.getItems()).toEqual([]);

        syncQueue.addItems(['1']);
        expect(syncQueue.getItems()).toEqual(['1']);
    });

    it('adds items properly', () => {
        syncQueue.addItems(['1']);
        syncQueue.addItems(['2']);

        expect(syncQueue.getItems()).toEqual(['1', '2']);
    });

    it('removes item properly', () => {
        syncQueue.addItems(['1', '2']);

        const mockItem = { itemId: '1' } as ISyncStatusItem;
        syncQueue.removeItem(mockItem);

        expect(syncQueue.getItems()).toEqual(['2']);
    });

    it('itemExists return true when the given item exists in the queue', () => {
        syncQueue.addItems(['1', '2']);

        const mockItem1 = { itemId: '1' } as ISyncStatusItem;
        expect(syncQueue.itemExists(mockItem1)).toBe(true);

        const mockItem3 = { itemId: '3' } as ISyncStatusItem;
        expect(syncQueue.itemExists(mockItem3)).toBe(false);
    });

    it('isEmpty returns true when the queue is empty', () => {
        expect(syncQueue.isEmpty()).toBe(true);

        syncQueue.addItems(['1']);
        expect(syncQueue.isEmpty()).toBe(false);
    });

    it('hasAtLeasOneItem returns true when queue has at least one item', () => {
        expect(syncQueue.hasAtLeastOneItem()).toBe(false);

        syncQueue.addItems(['1']);
        expect(syncQueue.hasAtLeastOneItem()).toBe(true);
    });
});
