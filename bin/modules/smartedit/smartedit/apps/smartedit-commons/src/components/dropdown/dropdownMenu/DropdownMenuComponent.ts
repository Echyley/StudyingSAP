/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import {
    ChangeDetectorRef,
    Component,
    ElementRef,
    EventEmitter,
    HostListener,
    Injector,
    Input,
    Output,
    OnChanges,
    DoCheck,
    SimpleChanges,
    ViewChild,
    ViewEncapsulation,
    Inject
} from '@angular/core';
import { YJQUERY_TOKEN } from '../../../services/vendors/YjqueryModule';
import { Placement } from '../../popupOverlay';
import { DropdownMenuItemDefaultComponent } from './DropdownMenuItemDefaultComponent';
import { IDropdownMenuItem } from './IDropdownMenuItem';
/**
 * Renders a Dropdown Menu.
 * It has two Inputs `dropdownItems` and `selectedItem`.
 *
 * The dropdownItems is an array of objects
 * which must contain either an i18n key associated to a callback function,
 * a static HTML template or a templateUrl leading to an external HTML file.
 * An optional condition can be added to define whether the item is to get
 * rendered.
 *
 * The selectedItem is the object associated to the dropdown. It is passed
 * as argument for the callback of dropdownItems.
 * For a given item, if a condition callback is defined, the item will show
 * only if this callback returns true
 *
 * ### Example
 *
 * By providing the callback method, Dropdown Item component will be used.
 *
 *      this.dropdownItems = [
 *          {
 *              key: 'my.translated.key',
 *              callback: () => {
 *                  doSomething();
 *              }
 *          }
 *      ];
 *
 * Providing a custom Dropdown Item component. `DROPDOWN_MENU_ITEM_DATA` provider can be injected.
 *
 *      this.dropdownItems = [
 *          {
 *              key: 'my.translated.key',
 *              component: ExampleDropdownItemComponent
 *          }
 *      ];
 *
 */
@Component({
    selector: 'se-dropdown-menu',
    templateUrl: './DropdownMenuComponent.html',
    styleUrls: ['./DropdownMenuComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class DropdownMenuComponent implements OnChanges, DoCheck {
    /**
     * An array of objects containing parameters allowing for the selection of a cached HTML template used to render the dropdown menu item.
     */
    @Input() dropdownItems: IDropdownMenuItem[];
    /**
     * An object defining the context of the page associated to the dropdown item.
     */
    @Input() selectedItem: any;
    @Output() selectedItemChange = new EventEmitter<any>();

    @Input() placement: Placement;
    /**
     * Uses the projected anchor instead of a the default contextual menu icon.
     */
    @Input() useProjectedAnchor = false;
    @Input() isOpen = false;
    /**
     * List of additional classes that will be added to menu list element classes.
     */
    @Input() additionalClasses: string[] = [];
    @Output() isOpenChange = new EventEmitter<boolean>();

    @ViewChild('toggleMenu', { static: true }) toggleMenuElement: ElementRef<HTMLDivElement>;

    public clonedDropdownItems: IDropdownMenuItem[];
    public dropdownMenuItemDefaultInjector: Injector;
    public isSubscription: boolean;
    public message: string;
    public dropdownListSelector = '.se-dropdown-menu__list';

    constructor(
        private cd: ChangeDetectorRef,
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic
    ) {
        this.isSubscription = false;
    }

    /** @internal */
    @HostListener('document:click', ['$event'])
    clickHandler(event: MouseEvent): void {
        // toggle menu
        if (this.toggleMenuElement.nativeElement.contains(event.target as Node)) {
            event.stopPropagation();
            this.isOpen = !this.isOpen;
            this.cd.detectChanges();

            this.emitIsOpenChange();
            return;
        }

        // close when any element except toggleMenuElement has been clicked
        if (this.isOpen) {
            this.isOpen = false;
            this.cd.detectChanges();

            this.emitIsOpenChange();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const dropdownItemsChange = changes.dropdownItems;
        if (dropdownItemsChange) {
            this.clonedDropdownItems = (dropdownItemsChange.currentValue as this['dropdownItems'])
                .map((item) => ({
                    ...item,
                    condition: item.condition || ((): boolean => true)
                }))
                .map((item) => this.setDefaultComponentIfNeeded(item));
        }
    }
    // if the content of dropdown list is empty, hide the popover of the dropdown
    ngDoCheck(): void {
        if (this.isOpen && !this.isSubscription) {
            const dropdownList = this.yjQuery(this.dropdownListSelector);
            if (dropdownList.length && !dropdownList.height()) {
                this.message = 'se.actions.without.permission';
            }
            this.isSubscription = true;
        }
    }

    private emitIsOpenChange(): void {
        this.isOpenChange.emit(this.isOpen);
    }

    /** @internal */
    private setDefaultComponentIfNeeded(dropdownItem: IDropdownMenuItem): IDropdownMenuItem {
        if (this.validateDropdownItem(dropdownItem) === 'callback') {
            return {
                ...dropdownItem,
                component: DropdownMenuItemDefaultComponent
            };
        } else {
            return dropdownItem;
        }
    }

    /** @internal */
    private validateDropdownItem(dropdownItem: IDropdownMenuItem): string {
        const expectedAttributes: Readonly<string[]> = ['callback', 'component'];
        const passedAttributes = Object.keys(dropdownItem);
        const validatedAttributes = passedAttributes.filter((attribute) =>
            expectedAttributes.includes(attribute)
        );

        if (validatedAttributes.length !== 1) {
            throw new Error(
                'DropdownMenuComponent.validateDropdownItem - Dropdown Item must contain "callback" or "component"'
            );
        }
        return validatedAttributes[0];
    }
}
