import { EventEmitter } from '@angular/core';
import { ISyncStatusItem } from '../../types';
export declare class SynchronizationPanelItemComponent {
    index: number;
    item: ISyncStatusItem;
    rootItem: ISyncStatusItem;
    selectAllLabel: string;
    disableList: boolean;
    disableItem?: (item: ISyncStatusItem) => void;
    selectionChange: EventEmitter<number>;
    getSelectAllLabel(): string;
    isItemDisabled(): boolean;
    showPopoverOverSyncIcon(): boolean;
    getInfoTitle(): string;
    isInSync(): boolean;
    onSelectionChange(): void;
}
