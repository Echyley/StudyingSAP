/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatusItem, SynchronizationPanelItemComponent } from 'cmscommons';

describe('SynchronizationPanelItemComponent', () => {
    let mockItem: ISyncStatusItem;
    let mockRootItem: ISyncStatusItem;

    let component: SynchronizationPanelItemComponent;
    beforeEach(() => {
        mockItem = {} as ISyncStatusItem;
        mockRootItem = {} as ISyncStatusItem;

        component = new SynchronizationPanelItemComponent();
    });

    describe('isItemDisabled', () => {
        it('GIVEN disabledList is true THEN it returns true', () => {
            component.disableList = true;

            expect(component.isItemDisabled()).toBe(true);
        });

        it('GIVEN disabledList is false AND disableItem return true THEN it returns true', () => {
            component.disableList = true;
            component.disableItem = (): boolean => true;

            expect(component.isItemDisabled()).toBe(true);
        });

        it('GIVEN non Root Item WHEN Root Item is selected THEN it returns true', () => {
            component.item = mockItem;
            component.rootItem = { ...mockRootItem, selected: true };
        });

        it('GIVEN non Root Item AND Root Item is not selected AND item is not in sync THEN it returns false', () => {
            component.item = mockItem;
            component.rootItem = { ...mockRootItem, selected: false };

            expect(component.isItemDisabled()).toBe(false);
        });

        it('GIVEN non Root Item AND Root Item is not selected AND item is in sync THEN it returns true', () => {
            component.item = { ...mockItem, status: 'IN_SYNC' as any };
            component.rootItem = { ...mockRootItem, selected: false };

            expect(component.isItemDisabled()).toBe(true);
        });

        it('GIVEN Root Item AND item is in sync THEN it returns true', () => {
            const mockRootItemInSync = { ...mockRootItem, status: 'IN_SYNC' as any };
            component.item = mockRootItemInSync;
            component.rootItem = mockRootItemInSync;

            expect(component.isItemDisabled()).toBe(true);
        });

        it('GIVEN Root Item AND item is not in sync THEN it returns false', () => {
            const mockRootItemInSync = { ...mockRootItem, status: 'IN_PROGRESS' as any };
            component.item = mockRootItemInSync;
            component.rootItem = mockRootItemInSync;
        });
    });

    describe('showPopoverOverSyncIcon', () => {
        it('GIVEN item has dependent item types that are out of sync THEN it returns true', () => {
            component.item = { ...mockItem, dependentItemTypesOutOfSync: [{}] };

            expect(component.showPopoverOverSyncIcon()).toBe(true);
        });

        it('GIVEN external item AND the item has no dependent item types that are out of sync THEN it returns true', () => {
            component.item = { ...mockItem, isExternal: true };

            expect(component.showPopoverOverSyncIcon()).toBe(true);
        });

        it('GIVEN non external item AND has the item has no dependent item types that are out of sync THEN it returns false', () => {
            component.item = { ...mockItem, isExternal: false };

            expect(component.showPopoverOverSyncIcon()).toBe(false);
        });
    });
});
