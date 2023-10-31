/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    AfterContentInit,
    ContentChildren,
    Directive,
    EventEmitter,
    HostListener,
    Input,
    OnChanges,
    Output,
    QueryList,
    SimpleChanges
} from '@angular/core';

import { ListItemKeyboardControlDirective } from './list-item-keyboard-control.directive';

export enum KeyboardKey {
    ArrowDown = 'ArrowDown',
    ArrowUp = 'ArrowUp',
    Enter = 'Enter',
    Esc = 'Escape'
}

export type ListKeyboardControlDisabledPredicate = (
    item: ListItemKeyboardControlDirective,
    index: number
) => boolean;

/**
 * Directive that manages the active option in a list of items based on keyboard interaction.
 * For disabled options, a predicate must be passed with `suListKeyboardControlDisabledPredicate`
 * which will prevent that option from navigating with arrow up / down key.
 *
 * Note: It will include only direct children having the `suListItemKeyboardControl` directive.
 *
 * @example
 * ```
 * items = [
 *   {
 *     id: 1,
 *     label: 'item 1'
 *   },
 *   {
 *     id: 2,
 *     label: 'item 2'
 *   },
 *   {
 *     id: 3,
 *     label: 'item 3'
 *   }
 * ]
 * <ul suListKeyboardControl>
 *   <li suListItemKeyboardControl *ngFor="let item of items">
 *     {{ item.label }}
 *   </li>
 * </ul>
 * ```
 */
@Directive({
    selector: '[suListKeyboardControl]'
})
export class ListKeyboardControlDirective implements OnChanges, AfterContentInit {
    /** Whether the keyboard interaction is enabled */
    @Input() suListKeyboardControlEnabled = true;
    /** Predicate called for each item. If true, the item will be excluded from keyboard navigation */
    @Input() suListKeyboardControlDisabledPredicate?: ListKeyboardControlDisabledPredicate;
    @Output() suListKeyboardControlEnterKeydown = new EventEmitter<number | null>();
    @Output() suListKeyboardControlEscKeydown = new EventEmitter<void>();

    /** @internal */
    @ContentChildren(ListItemKeyboardControlDirective)
    items!: QueryList<ListItemKeyboardControlDirective>;

    /** @internal */
    private didNgAfterContentInit = false;
    /** @internal */
    private activeItem: ListItemKeyboardControlDirective | null = null;
    /** @internal */
    private activeItemIndex: number | null = null;

    /** @internal */
    @HostListener('document:keydown', ['$event']) onKeyDown(event: KeyboardEvent): void {
        if (!this.suListKeyboardControlEnabled || this.items.length === 0) {
            return;
        }

        // For ArrowDown and ArrowUp prevent from scrolling the container.
        // Focus event is called when setting an active item so it will also scroll if needed.
        switch (event.key) {
            case KeyboardKey.ArrowDown:
                event.preventDefault();
                this.handleArrowDown();
                return;
            case KeyboardKey.ArrowUp:
                event.preventDefault();
                this.handleArrowUp();
                return;
            case KeyboardKey.Enter:
                event.preventDefault();
                this.handleEnter();
                return;
            case KeyboardKey.Esc:
                this.handleEsc();
                return;
        }
    }

    /** @internal */
    ngAfterContentInit(): void {
        this.items.changes.subscribe(() => this.onItemsChange());
        // Items may be cached by wrapper component e.g. <fd-popover> so items.changes subscription will not receive an event.
        // Ensure that whenever a dropdown is opened, subscription will receive an event
        this.items.notifyOnChanges();

        this.didNgAfterContentInit = true;
    }

    /** @internal */
    ngOnChanges(changes: SimpleChanges): void {
        if (!this.didNgAfterContentInit) {
            return;
        }

        const enabledChange = changes.suListKeyboardControlEnabled;
        const predicateChange = changes.suListKeyboardControlDisabledPredicate;

        const shouldSetActive =
            enabledChange && enabledChange.currentValue && !enabledChange.previousValue;

        const shouldUnsetActive =
            enabledChange && !enabledChange.currentValue && enabledChange.previousValue;

        const shouldSetActiveForPredicate =
            predicateChange && predicateChange.currentValue && !!this.suListKeyboardControlEnabled;
        const shouldUnsetActiveForPredicate =
            predicateChange &&
            !predicateChange.currentValue &&
            predicateChange.previousValue &&
            !!this.suListKeyboardControlEnabled;

        if (this.items.length === 0) {
            return;
        }

        if (shouldSetActive || shouldSetActiveForPredicate) {
            this.setFirstItemActive();
        }

        if (shouldUnsetActive || shouldUnsetActiveForPredicate) {
            this.unsetActiveItem();
        }
    }

    /**
     * Handler for dynamic content change.
     * Sets or unsets active item.
     * @internal
     */
    private onItemsChange(): void {
        if (this.items.length === 0) {
            this.clearActiveItem();
            return;
        }

        if (this.suListKeyboardControlEnabled) {
            if (!this.isActiveItemSet()) {
                // if no active item then set the first one as active
                this.setActiveItemByIndex(0, 1);
            } else if (!this.itemExistsByIndex(this.activeItemIndex)) {
                // clear active item if the active item no longer exists
                this.clearActiveItem();
            }
        } else {
            // keyboard control is not enabled
            this.clearActiveItem();
        }
    }

    /** @internal */
    private handleArrowUp(): void {
        this.setPreviousItemActive();
    }

    /** @internal */
    private handleArrowDown(): void {
        this.setNextItemActive();
    }

    /** @internal */
    private handleEnter(): void {
        this.suListKeyboardControlEnterKeydown.emit(this.activeItemIndex);
    }

    /** @internal */
    private handleEsc(): void {
        this.suListKeyboardControlEscKeydown.emit();
    }

    /** @internal */
    private getItemByIndex(index: number): ListItemKeyboardControlDirective {
        const items = this.getItemsArray();
        return items[index];
    }

    /** @internal */
    private getItemsArray(): ListItemKeyboardControlDirective[] {
        return this.items.toArray();
    }

    /** @internal */
    private setActiveItemByIndex(index: number, delta: -1 | 1): void {
        const items = this.getItemsArray();

        if (this.suListKeyboardControlDisabledPredicate) {
            while (this.suListKeyboardControlDisabledPredicate(items[index], index)) {
                index += delta;

                if (!items[index]) {
                    return;
                }
            }
        }

        const item = this.getItemByIndex(index);
        this.setActiveItem(item);
    }

    /** @internal */
    private setActiveItem(item: ListItemKeyboardControlDirective): void {
        const items = this.getItemsArray();
        const index = items.indexOf(item);

        if (this.activeItem) {
            this.activeItem.setInactive();
        }

        if (items.length > 0) {
            this.activeItem = items[index];
            this.activeItem.setActive();
            this.activeItemIndex = index;
        }
    }

    /** @internal */
    private unsetActiveItem(): void {
        if (!this.activeItem) {
            return;
        }

        this.activeItem.setInactive();
        this.activeItemIndex = null;
    }

    /** @internal */
    private setFirstItemActive(): void {
        this.setActiveItemByIndex(0, 1);
    }

    /** @internal */
    private setNextItemActive(): void {
        if (this.activeItemIndex === null) {
            this.setFirstItemActive();
            return;
        }

        if (this.activeItemIndex < this.items.length - 1) {
            this.setActiveItemByIndex(this.activeItemIndex + 1, 1);
        }
    }

    /** @internal */
    private setPreviousItemActive(): void {
        if (this.activeItemIndex === null) {
            this.setFirstItemActive();
            return;
        }

        if (this.activeItemIndex > 0) {
            this.setActiveItemByIndex(this.activeItemIndex - 1, -1);
        }
    }

    /** @internal */
    private itemExistsByIndex(index: number | null): boolean {
        if (index === null) {
            return false;
        }
        return !!this.getItemByIndex(index);
    }

    /** @internal */
    private clearActiveItem(): void {
        this.activeItem = null;
        this.activeItemIndex = null;
    }

    /** @internal */
    private isActiveItemSet(): boolean {
        return !!this.activeItem;
    }
}
