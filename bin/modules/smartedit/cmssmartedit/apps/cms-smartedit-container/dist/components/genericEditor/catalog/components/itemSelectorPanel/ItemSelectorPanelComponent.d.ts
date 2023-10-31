import { EventEmitter, OnInit, Type } from '@angular/core';
import { FetchStrategy, SelectReset, SliderPanelConfiguration } from 'smarteditcommons';
import { PopulatorItem } from '../../../dropdownPopulators/types';
import { CatalogFetchStrategy, ProductCatalog } from '../../services';
interface CatalogInfo {
    catalogId: string;
    catalogVersion: string;
}
export declare class ItemSelectorPanelComponent implements OnInit {
    itemComponent: Type<any>;
    getCatalogs: () => Promise<ProductCatalog[]>;
    itemsFetchStrategy: CatalogFetchStrategy<PopulatorItem>;
    catalogItemTypeI18nKey: string;
    onSaveChanges: EventEmitter<string[]>;
    catalogs: ProductCatalog[];
    catalogInfo: CatalogInfo;
    panelConfig: SliderPanelConfiguration;
    saveButtonDisabled: boolean;
    internalItemsSelected: string[];
    resetCatalogVersionSelector: SelectReset;
    resetItemsListSelector: SelectReset;
    catalogSelectorFetchStrategy: FetchStrategy;
    catalogVersionSelectorFetchStrategy: FetchStrategy;
    itemsSelectorFetchStrategy: FetchStrategy;
    onCatalogSelectorChange: () => void;
    onCatalogVersionSelectorChange: () => void;
    onItemsSelectorChange: () => void;
    hidePanel: () => Promise<void>;
    showPanel: () => Promise<void>;
    constructor();
    ngOnInit(): Promise<void>;
    initAndShowPanel(selectedItems: string[]): void;
    isItemSelectorEnabled(): boolean;
    private initOnCatalogSelectorChange;
    private initOnCatalogVersionSelectorChange;
    private initOnItemsSelectorChange;
    private initCatalogs;
    private initCatalogSelector;
    private initCatalogVersionSelector;
    private initItemsSelector;
    private itemsFetchPageAndFilter;
    private limitToNonSelectedItems;
    private isSaveButtonDisabled;
    private cancel;
    private saveChanges;
}
export {};
