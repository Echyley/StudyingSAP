/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { synchronizationUtils } from '../../SynchronizationUtils';
import { ISyncStatusItem } from '../../types';

@Component({
    selector: 'se-synchronization-panel-item',
    templateUrl: './SynchronizationPanelItemComponent.html',
    styleUrls: ['./SynchronizationPanelItemComponent.scss']
})
export class SynchronizationPanelItemComponent {
    @Input() index: number;
    @Input() item: ISyncStatusItem;
    @Input() rootItem: ISyncStatusItem;
    @Input() selectAllLabel: string;
    @Input() disableList: boolean;
    @Input() disableItem?: (item: ISyncStatusItem) => void;

    @Output() selectionChange = new EventEmitter<number>();

    public getSelectAllLabel(): string {
        return this.selectAllLabel || 'se.cms.synchronization.page.select.all.slots';
    }

    /**
     * Verifies whether the individual item is disabled on synchronization panel or not.
     */
    public isItemDisabled(): boolean {
        if (this.disableList || (this.disableItem && this.disableItem(this.item))) {
            return true;
        }

        return (
            (this.item !== this.rootItem && !!this.rootItem?.selected) ||
            synchronizationUtils.isInSync(this.item)
        );
    }

    /**
     * Verifies whether the information popover must be displayed on top of sync icon.
     * - It returns true if dependent item types out of sync exist or the item is external.
     */
    public showPopoverOverSyncIcon(): boolean {
        return (
            this.item.dependentItemTypesOutOfSync?.length > 0 ||
            synchronizationUtils.isExternalItem(this.item)
        );
    }

    /** Returns title for information popover. */
    public getInfoTitle(): string {
        return !synchronizationUtils.isExternalItem(this.item)
            ? 'se.cms.synchronization.panel.update.title'
            : '';
    }

    public isInSync(): boolean {
        return synchronizationUtils.isInSync(this.item);
    }

    public onSelectionChange(): void {
        this.selectionChange.emit(this.index);
    }
}
