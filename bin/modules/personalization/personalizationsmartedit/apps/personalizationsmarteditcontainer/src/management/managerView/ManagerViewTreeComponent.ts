/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CdkDragMove } from '@angular/cdk/drag-drop';
import { DOCUMENT } from '@angular/common';
import {
    Component,
    Inject,
    Input,
    ViewEncapsulation,
    OnInit,
    OnDestroy,
    DoCheck
} from '@angular/core';
import { cloneDeep } from 'lodash';
import {
    CustomizationTreeItem,
    CustomizationVariationTreeItem,
    PERSONALIZATION_MODEL_STATUS_CODES,
    PersonalizationsmarteditUtils,
    PersonalizationsmarteditDateUtils,
    PersonalizationsmarteditCommerceCustomizationService
} from 'personalizationcommons';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { IWaitDialogService } from 'smarteditcommons';
import { ManagerViewUtilsService } from '../../management/managerView/ManagerViewUtilsService';
import { PersonalizationsmarteditContextService } from '../../service';
import { PersonalizationsmarteditCommerceCustomizationView } from '../commerceCustomizationView';
import { ManageCustomizationViewManager } from '../manageCustomizationView/ManageCustomizationViewManager';

enum ItemType {
    customization = 'CUSTOMIZATION',
    variation = 'VARIATION'
}

interface DropAction {
    targetId: string;
    action?: 'before' | 'after';
    itemType?: string;
}
const PZ_MODAL_MANAGER = 'personalization.modal.manager';
@Component({
    selector: 'manager-view-tree',
    templateUrl: './ManagerViewTreeComponent.html',
    styleUrls: ['./ManagerViewTreeComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ManagerViewTreeComponent implements OnInit, OnDestroy, DoCheck {
    @Input() public customizations: CustomizationTreeItem[];
    public treeRoot = 'tree-root';

    private uncollapsedCustomizations = [];
    private customizationsPreCount = 0;

    // data for drag & drop
    private dropTargetIds = [];
    private nodeLookup = {};
    private dropAction: DropAction = null;
    private dragMovedSubscription: Subscription;
    private dragMovedSubject = new Subject<CdkDragMove<any> | undefined>();

    constructor(
        @Inject(DOCUMENT) private document: Document,
        private managerViewUtilsService: ManagerViewUtilsService,
        private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        private personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils,
        private manageCustomizationViewManager: ManageCustomizationViewManager,
        private personalizationsmarteditCommerceCustomizationView: PersonalizationsmarteditCommerceCustomizationView,
        private personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService,
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private waitDialogService: IWaitDialogService
    ) {}

    ngOnDestroy(): void {
        this.dragMovedSubscription?.unsubscribe();
    }

    ngOnInit(): void {
        this.dragMovedSubscription = this.dragMovedSubject
            .pipe(debounceTime(50))
            .subscribe((event) => {
                this.performDragMoved(event);
            });
    }

    ngDoCheck(): void {
        if (this.customizationsPreCount !== this.customizations.length) {
            this.updateCustomizations();
            this.resetCustomizationsDragDrop();
            this.updateCustomizationsDragDrop(this.customizations, 0);
            this.customizationsPreCount = this.customizations.length;
        }
    }

    public dragMoved(event: CdkDragMove<any>): void {
        this.dragMovedSubject.next(event);
    }

    public dragDropped(event: any): void {
        const draggedItemId = event.item.data;
        const dropItemId = this.dropAction.targetId;
        const draggedItemRootId = event.previousContainer.id;
        const dropItemRootId = this.getParentNodeId(dropItemId, this.customizations, this.treeRoot);

        // can't drag variation to other customization
        // can't drag variation to customization level
        if (
            !this.dropAction.action ||
            this.nodeLookup[draggedItemId].itemLevel !== this.nodeLookup[dropItemId].itemLevel ||
            draggedItemRootId !== dropItemRootId
        ) {
            return;
        }

        const dropItem = this.getDropItem(
            this.dropAction.itemType,
            draggedItemId,
            dropItemRootId,
            this.customizations
        );

        const draggedItem = this.nodeLookup[draggedItemId];

        const oldItemContainer =
            draggedItemRootId !== this.treeRoot
                ? this.nodeLookup[draggedItemRootId].variations
                : this.customizations;
        const newContainer =
            dropItemRootId !== this.treeRoot
                ? this.nodeLookup[dropItemRootId].variations
                : this.customizations;

        const i = oldItemContainer.findIndex((c) => c.code === draggedItemId);
        oldItemContainer.splice(i, 1);

        const targetIndex = newContainer.findIndex((c) => c.code === this.dropAction.targetId);
        if (this.dropAction.action === 'before') {
            newContainer.splice(targetIndex, 0, draggedItem);
        } else {
            newContainer.splice(targetIndex + 1, 0, draggedItem);
        }

        newContainer.forEach((item, index) => (item.rank = index));
        if (this.dropAction.itemType === ItemType.customization) {
            this.updateChangedCustomization(dropItem, newContainer);
        } else if (this.dropAction.itemType === ItemType.variation) {
            const parentCustomization = cloneDeep(this.nodeLookup[dropItemRootId]);
            this.updateChangedVariation(parentCustomization, dropItem, newContainer);
        }

        this.clearDragInfo(true);
    }

    public customizationCollapseAction(customization: CustomizationTreeItem): void {
        this.clearAllCustomizationSubMenu();
        this.clearAllVariationSubMenu();
        customization.collapsed = !customization.collapsed;
        if (customization.collapsed === false) {
            this.uncollapsedCustomizations.push(customization.code);
        } else {
            this.uncollapsedCustomizations.splice(
                this.uncollapsedCustomizations.indexOf(customization.code),
                1
            );
        }
        this.updateCustomizationVariations(customization);
        this.resetCustomizationsDragDrop();
        this.updateCustomizationsDragDrop(this.customizations, 0);
    }

    public allCustomizationsCollapsed(): boolean {
        return this.customizations
            .map((elem: CustomizationTreeItem) => elem.collapsed)
            .reduce(
                (previousValue: boolean, currentValue: boolean) => previousValue && currentValue,
                true
            );
    }

    public getStatusNotDeleted(customization: CustomizationTreeItem): number {
        return customization.variations.filter((variation: CustomizationVariationTreeItem) =>
            this.personalizationsmarteditUtils.isItemVisible(variation)
        ).length;
    }

    public getActivityStateForCustomization(customization: CustomizationTreeItem): string {
        return this.personalizationsmarteditUtils.getActivityStateForCustomization(customization);
    }

    public getEnablementTextForCustomization(customization: CustomizationTreeItem): string {
        return this.personalizationsmarteditUtils.getEnablementTextForCustomization(
            customization,
            PZ_MODAL_MANAGER
        );
    }

    public getFormattedDate(myDate: string): string {
        return myDate ? this.personalizationsmarteditDateUtils.formatDate(myDate, null) : '';
    }

    public clearAllCustomizationSubMenuAndEditCustomizationAction(
        customization: CustomizationTreeItem
    ): void {
        this.clearAllCustomizationSubMenu();
        this.editCustomizationAction(customization);
    }

    public clearAllVariationSubMenuAndEditVariationAction(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): void {
        this.clearAllVariationSubMenu();
        this.editVariationAction(customization, variation);
    }

    public clearAllVariationSubMenuAndmanageCommerceCustomization(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): void {
        this.clearAllVariationSubMenu();
        this.manageCommerceCustomization(customization, variation);
    }

    public editCustomizationAction(customization: CustomizationTreeItem): void {
        this.manageCustomizationViewManager.openEditCustomizationModal(customization.code, null);
    }

    public isNotEnabled(customization: CustomizationTreeItem): boolean {
        return customization.status !== PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }

    public deleteCustomizationAction(customization: CustomizationTreeItem): void {
        this.managerViewUtilsService.deleteCustomizationAction(customization, this.customizations);
    }

    public updateSubMenuAction(node: CustomizationTreeItem | CustomizationVariationTreeItem): void {
        if (!node.subMenu) {
            this.clearAllCustomizationSubMenu();
            this.clearAllVariationSubMenu();
        }
        node.subMenu = !node.subMenu;
    }

    public clearAllCustomizationSubMenu(): void {
        for (const customization of this.customizations) {
            customization.subMenu = false;
        }
    }

    public clearAllVariationSubMenu(): void {
        for (const customization of this.customizations) {
            for (const variation of customization.variations) {
                variation.subMenu = false;
            }
        }
    }

    public hasCommerceActions(variation: CustomizationVariationTreeItem): boolean {
        return this.personalizationsmarteditUtils.hasCommerceActions(variation);
    }

    public getCommerceCustomizationTooltip(variation: CustomizationVariationTreeItem): string {
        return this.personalizationsmarteditUtils.getCommerceCustomizationTooltip(
            variation,
            '',
            ''
        );
    }

    public getActivityStateForVariation(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): string {
        return this.personalizationsmarteditUtils.getActivityStateForVariation(
            customization,
            variation
        );
    }

    public getEnablementTextForVariation(variation: CustomizationVariationTreeItem): string {
        return this.personalizationsmarteditUtils.getEnablementTextForVariation(
            variation,
            PZ_MODAL_MANAGER
        );
    }

    public editVariationAction(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): void {
        this.manageCustomizationViewManager.openEditCustomizationModal(
            customization.code,
            variation.code
        );
    }

    public toogleVariationActive(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): void {
        this.managerViewUtilsService.toggleVariationActive(customization, variation);
    }

    public getEnablementActionTextForVariation(variation: CustomizationVariationTreeItem): string {
        return this.personalizationsmarteditUtils.getEnablementActionTextForVariation(
            variation,
            PZ_MODAL_MANAGER
        );
    }

    public manageCommerceCustomization(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem
    ): void {
        this.personalizationsmarteditCommerceCustomizationView.openCommerceCustomizationAction(
            customization,
            variation
        );
    }

    public isDeleteVariationEnabled(customization: CustomizationTreeItem): boolean {
        return (
            this.personalizationsmarteditUtils.getVisibleItems(customization.variations).length > 1
        );
    }

    public deleteVariationAction(
        customization: CustomizationTreeItem,
        variation: CustomizationVariationTreeItem,
        $event: any
    ): void {
        if (this.isDeleteVariationEnabled(customization)) {
            this.clearAllVariationSubMenu();
            this.managerViewUtilsService.deleteVariationAction(customization, variation);
        } else {
            $event.stopPropagation();
        }
    }

    public statusNotDeleted(variation: CustomizationVariationTreeItem): boolean {
        return this.personalizationsmarteditUtils.isItemVisible(variation);
    }

    private performDragMoved(event: CdkDragMove<any>): void {
        // close all sub-menus
        this.clearAllCustomizationSubMenu();
        this.clearAllVariationSubMenu();

        const ele = this.document.elementFromPoint(
            event.pointerPosition.x,
            event.pointerPosition.y
        );
        if (!ele) {
            this.clearDragInfo();
            return;
        }

        const container: Element = ele.classList.contains('node-item')
            ? ele
            : ele.closest('.node-item');
        if (!container) {
            this.clearDragInfo();
            return;
        }
        this.dropAction = {
            targetId: container.getAttribute('data-id')
        };
        this.dropAction.itemType = container.getAttribute('type');
        const targetRect = container.getBoundingClientRect();
        const oneHalf = targetRect.height / 2;

        // memorize moved location
        if (event.pointerPosition.y - targetRect.top < oneHalf) {
            this.dropAction.action = 'before';
        } else if (event.pointerPosition.y - targetRect.top > oneHalf) {
            this.dropAction.action = 'after';
        }
        this.showDragInfo();
    }

    private resetCustomizationsDragDrop(): void {
        this.dropTargetIds.length = 0;
        this.nodeLookup = {};
    }

    private updateCustomizationsDragDrop(
        nodes: CustomizationTreeItem[] | CustomizationVariationTreeItem[],
        itemLevel: number
    ): void {
        nodes.forEach((node) => {
            this.dropTargetIds.push(node.code);
            this.nodeLookup[node.code] = node;
            this.nodeLookup[node.code].itemLevel = itemLevel;
            if (node.variations) {
                this.updateCustomizationsDragDrop(node.variations, itemLevel + 1);
            }
        });
    }

    private getParentNodeId(id: string, nodesToSearch: any, parentId: string): string {
        for (const node of nodesToSearch) {
            if (node.code === id) {
                return parentId;
            }
            if (node.variations) {
                const ret = this.getParentNodeId(id, node.variations, node.code);
                if (ret) {
                    return ret;
                }
            }
        }
        return null;
    }

    private updateCustomizations(): void {
        this.customizations.forEach((customization) => {
            if (this.uncollapsedCustomizations.includes(customization.code)) {
                customization.collapsed = false;
                this.updateCustomizationVariations(customization);
            } else {
                customization.collapsed = true;
            }
        });
    }

    private showDragInfo(): void {
        this.clearDragInfo();
        if (this.dropAction) {
            this.document
                .getElementById('node-' + this.dropAction.targetId)
                .classList.add('drop-' + this.dropAction.action);
        }
    }

    private clearDragInfo(dropped = false): void {
        if (dropped) {
            this.dropAction = null;
        }
        this.document
            .querySelectorAll('.drop-before')
            .forEach((element) => element.classList.remove('drop-before'));
        this.document
            .querySelectorAll('.drop-after')
            .forEach((element) => element.classList.remove('drop-after'));
    }

    private isCommerceCustomizationEnabled(): boolean {
        return this.personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(
            this.personalizationsmarteditContextService.getSeData().seConfigurationData
        );
    }

    private updateCustomizationVariations(customization: CustomizationTreeItem): void {
        this.managerViewUtilsService.customizationClickAction(customization);
        const isCommerceCustomizationEnabled = this.isCommerceCustomizationEnabled();
        customization.variations.forEach((variation) => {
            variation.statusNotDeleted = this.statusNotDeleted(variation);
            variation.isCommerceCustomizationEnabled = isCommerceCustomizationEnabled;
        });
    }

    private async updateChangedVariation(
        customization: CustomizationTreeItem,
        dropItem: CustomizationTreeItem | CustomizationVariationTreeItem,
        updatedVariations: CustomizationVariationTreeItem[]
    ): Promise<void> {
        const droppedItemDest = updatedVariations.filter(
            (elem: CustomizationVariationTreeItem) => elem.code === dropItem.code
        )[0].rank;
        const increasedIndex = droppedItemDest - dropItem.rank;

        if (increasedIndex !== 0) {
            this.waitDialogService.showWaitModal();
            await this.updateVariationRank(customization, dropItem.code, increasedIndex);
            this.waitDialogService.hideWaitModal();
        }
    }

    private async updateChangedCustomization(
        dropItem: CustomizationTreeItem | CustomizationVariationTreeItem,
        updatedCustomizations: CustomizationTreeItem[]
    ): Promise<void> {
        const droppedItemDest = updatedCustomizations.filter(
            (elem: CustomizationVariationTreeItem) => elem.code === dropItem.code
        )[0].rank;
        const increasedIndex = droppedItemDest - dropItem.rank;

        if (increasedIndex !== 0) {
            this.waitDialogService.showWaitModal();
            await this.updateCustomizationRank(dropItem.code, increasedIndex);
            this.waitDialogService.hideWaitModal();
        }
    }

    private updateCustomizationRank(
        customizationCode: string,
        increaseValue: number
    ): Promise<void> {
        return this.managerViewUtilsService.updateCustomizationRank(
            customizationCode,
            increaseValue
        );
    }

    private updateVariationRank(
        customization: CustomizationTreeItem,
        variationCode: string,
        increaseValue: number
    ): Promise<void> {
        return this.managerViewUtilsService.updateVariationRank(
            customization,
            variationCode,
            increaseValue
        );
    }

    private getDropItem(
        itemType: string,
        draggedItemId: string,
        targetListId: string,
        itemList: CustomizationTreeItem[]
    ): CustomizationTreeItem | CustomizationVariationTreeItem {
        if (itemType === ItemType.customization) {
            return cloneDeep(
                itemList.filter((elem: CustomizationTreeItem) => elem.code === draggedItemId)[0]
            );
        } else if (itemType === ItemType.variation) {
            const customization = itemList.filter(
                (elem: CustomizationTreeItem) => elem.code === targetListId
            )[0];
            return cloneDeep(
                customization.variations.filter(
                    (elem: CustomizationTreeItem) => elem.code === draggedItemId
                )[0]
            );
        }
    }
}
