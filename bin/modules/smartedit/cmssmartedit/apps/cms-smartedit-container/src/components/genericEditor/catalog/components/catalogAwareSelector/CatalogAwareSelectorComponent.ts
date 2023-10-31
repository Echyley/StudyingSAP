/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    HostBinding,
    Input,
    OnInit,
    Output,
    Type,
    ViewChild,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { EditableListNodeItemDTO, LogService } from 'smarteditcommons';
import { CatalogFetchStrategy, ProductCatalog } from '../../services';
import { ItemSelectorPanelComponent } from '../itemSelectorPanel';

/**
 * A component that allows users to select items from one or more catalogs.
 * This component is catalog aware; the list of items displayed is dependent on the catalog and catalog version selected by the user within the component.
 */
@Component({
    selector: 'se-catalog-aware-selector',
    host: {
        '[class.se-catalog-aware-selector]': 'true'
    },
    templateUrl: './CatalogAwareSelectorComponent.html',
    styleUrls: ['./CatalogAwareSelectorComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogAwareSelectorComponent implements OnInit {
    /** Identifier used to track the component in the page. */
    @Input()
    @HostBinding('attr.id')
    id: string;

    @Input() selectedItemIds: string[];
    /** Function called with no arguments by the component to retrieve the list of catalogs where the items to select reside. */
    @Input() getCatalogs: () => Promise<ProductCatalog[]>;
    @Input() itemsFetchStrategy: CatalogFetchStrategy<EditableListNodeItemDTO>;
    @Input() itemComponent: Type<any>;
    @Input() nodeComponent: Type<any>;
    /**
     * It is used to display the type of item (e.g. Product / Category) being selected in the component.
     */
    @Input() catalogItemTypeI18nKey: string;
    /**
     * Specifies whether the selector can be edited or not.
     * If this flag is false, then the selector is treated as read-only
     */
    @Input() editable: boolean;

    @Output() selectedItemIdsChange: EventEmitter<string[]>;

    @ViewChild(ItemSelectorPanelComponent, { static: false })
    itemSelectorPanel: ItemSelectorPanelComponent;

    /** Set with 2 way binding by Editable List. */
    public itemList: EditableListNodeItemDTO[];
    /** Called when there is a change in Editable List. */
    public onListChange: () => void;
    /**
     * Set with 2 way binding by Editable List.
     * Used to update items position when user changed position with Item Panel Selector.
     **/
    public refreshItemListWidget: () => void;
    public editableListId: string;

    constructor(private logService: LogService, private cdr: ChangeDetectorRef) {
        this.selectedItemIds = [];
        this.selectedItemIdsChange = new EventEmitter();

        this.itemList = [];
        this.onListChange = (): void => this.selectItemsFromItemList();
    }

    async ngOnInit(): Promise<void> {
        if (this.editable === undefined) {
            this.editable = true;
        }

        this.editableListId = `${this.id}_list`;

        if (this.selectedItemIds.length > 0) {
            return this.syncFromSelectedItemsToItemsList(this.selectedItemIds);
        } else {
            return Promise.reject();
        }
    }

    public selectedItemListIsEmpty(): boolean {
        return this.selectedItemIds.length === 0;
    }

    public openItemSelectorPanel(): void {
        this.itemSelectorPanel.initAndShowPanel([...this.selectedItemIds]);
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public onItemSelectorPanelSaveChanges(selectedItemIds: string[]): Promise<void> {
        this.selectedItemIdsChange.emit(selectedItemIds);

        return this.syncFromSelectedItemsToItemsList(selectedItemIds);
    }

    private selectItemsFromItemList(): void {
        const selectedItemIds = this.itemList.map((item) => item.id);

        this.selectedItemIdsChange.emit(selectedItemIds);
    }

    private async syncFromSelectedItemsToItemsList(selectedItemIds: string[]): Promise<void> {
        // Retrieve the found and the missing items for which we need to fetch the data from API later on.
        // We also select items that are already in list so later we can append fetched items to that list.
        const [idsFoundInList, idsMissingInList] = this.selectItemIdsFromItemList(
            selectedItemIds,
            this.itemList
        );

        const itemsFoundInList = new Map<string, EditableListNodeItemDTO>();
        this.itemList
            .filter(({ id }) => idsFoundInList.includes(id))
            .forEach((item) => {
                itemsFoundInList.set(item.id, item);
            });

        // Fetch the data for the missing items and append them to itemsFoundInList.
        // It can be empty when user opens Item Selector Panel, selects the same catalog and catalog version and clicks "Add" button.
        // In that case, we can skip the fetch part.
        if (idsMissingInList.length > 0) {
            const newItems = await this.fetchItems(idsMissingInList);
            newItems.forEach((item) => {
                itemsFoundInList.set(item.id, item);
            });
        }

        // Reappend itemList. Some items could be added / removed or position may have changed.
        this.itemList.length = 0;
        selectedItemIds.forEach((id) => {
            const item = itemsFoundInList.get(id);
            if (!item) {
                this.logService.warn(`[${this.constructor.name}] - Cannot find item with key `, id);
                return;
            }
            this.itemList.push(item);
        });

        if (this.itemList.length > 0) {
            this.updatePosition();
        }

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private updatePosition(): void {
        if (!!this.refreshItemListWidget) {
            this.refreshItemListWidget();
        }
    }

    private selectItemIdsFromItemList(
        selectedItemIds: string[],
        itemList: EditableListNodeItemDTO[]
    ): [string[], string[]] {
        const idsFound: string[] = [];
        const idsMissing: string[] = [];

        const itemIds = new Set(itemList.map(({ id }) => id));
        selectedItemIds.forEach((id) => {
            if (itemIds.has(id)) {
                idsFound.push(id);
            } else {
                idsMissing.push(id);
            }
        });
        return [idsFound, idsMissing];
    }

    private async fetchItems(itemsToRetrieve: string[]): Promise<EditableListNodeItemDTO[]> {
        const fetchEntityPromises = itemsToRetrieve.map((id) =>
            this.itemsFetchStrategy.fetchEntity(id)
        );
        return Promise.all(fetchEntityPromises);
    }
}
