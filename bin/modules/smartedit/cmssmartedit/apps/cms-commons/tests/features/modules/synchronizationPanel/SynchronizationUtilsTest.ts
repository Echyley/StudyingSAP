/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatusItem, synchronizationUtils } from 'cmscommons';

describe('SynchronizationUtils', () => {
    it('isInSync returns true when the item is synchronized', () => {
        const item = { status: 'IN_SYNC' } as ISyncStatusItem;

        expect(synchronizationUtils.isInSync(item)).toBe(true);
    });

    it('isInNotSync returns true when the item has failed synchronization status', () => {
        const item = { status: 'NOT_SYNC' } as ISyncStatusItem;

        expect(synchronizationUtils.isInNotSync(item)).toBe(true);
    });

    it('isSyncInFailed returns true when the item has failed synchronization status', () => {
        const item = { status: 'SYNC_FAILED' } as ISyncStatusItem;

        expect(synchronizationUtils.isSyncInFailed(item)).toBe(true);
    });

    it('isSyncInProgress returns true when synchronization is in progress for the item', () => {
        const item = { status: 'IN_PROGRESS' } as ISyncStatusItem;

        expect(synchronizationUtils.isSyncInProgress(item)).toBe(true);
    });

    it('isExternalItem returns true when the item is external', () => {
        const externalItem = { isExternal: true } as ISyncStatusItem;

        expect(synchronizationUtils.isExternalItem(externalItem)).toBe(true);
    });

    it('isExternalItem returns flase when the item is non external', () => {
        const nonExternalItem = { isExternal: false } as ISyncStatusItem;

        expect(synchronizationUtils.isExternalItem(nonExternalItem)).toBe(false);
    });
});
