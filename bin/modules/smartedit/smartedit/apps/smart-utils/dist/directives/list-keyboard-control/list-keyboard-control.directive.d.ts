import { AfterContentInit, EventEmitter, OnChanges, QueryList, SimpleChanges } from '@angular/core';
import { ListItemKeyboardControlDirective } from './list-item-keyboard-control.directive';
export declare enum KeyboardKey {
    ArrowDown = "ArrowDown",
    ArrowUp = "ArrowUp",
    Enter = "Enter",
    Esc = "Escape"
}
export declare type ListKeyboardControlDisabledPredicate = (item: ListItemKeyboardControlDirective, index: number) => boolean;
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
export declare class ListKeyboardControlDirective implements OnChanges, AfterContentInit {
    /** Whether the keyboard interaction is enabled */
    suListKeyboardControlEnabled: boolean;
    /** Predicate called for each item. If true, the item will be excluded from keyboard navigation */
    suListKeyboardControlDisabledPredicate?: ListKeyboardControlDisabledPredicate;
    suListKeyboardControlEnterKeydown: EventEmitter<number>;
    suListKeyboardControlEscKeydown: EventEmitter<void>;
    /** @internal */
    items: QueryList<ListItemKeyboardControlDirective>;
    /** @internal */
    private didNgAfterContentInit;
    /** @internal */
    private activeItem;
    /** @internal */
    private activeItemIndex;
    /** @internal */
    onKeyDown(event: KeyboardEvent): void;
    /** @internal */
    ngAfterContentInit(): void;
    /** @internal */
    ngOnChanges(changes: SimpleChanges): void;
    /**
     * Handler for dynamic content change.
     * Sets or unsets active item.
     * @internal
     */
    private onItemsChange;
    /** @internal */
    private handleArrowUp;
    /** @internal */
    private handleArrowDown;
    /** @internal */
    private handleEnter;
    /** @internal */
    private handleEsc;
    /** @internal */
    private getItemByIndex;
    /** @internal */
    private getItemsArray;
    /** @internal */
    private setActiveItemByIndex;
    /** @internal */
    private setActiveItem;
    /** @internal */
    private unsetActiveItem;
    /** @internal */
    private setFirstItemActive;
    /** @internal */
    private setNextItemActive;
    /** @internal */
    private setPreviousItemActive;
    /** @internal */
    private itemExistsByIndex;
    /** @internal */
    private clearActiveItem;
    /** @internal */
    private isActiveItemSet;
}
