import { OnInit } from '@angular/core';
import { GenericEditorWidgetData } from 'smarteditcommons';
import { CatalogInformationService, ProductCatalog, ProductsFetchStrategy } from '../../services';
import { ProductNodeComponent } from './productNode/ProductNodeComponent';
import { ProductSelectorItemComponent } from './productSelectorItem';
interface ProductModel {
    products: string[] | undefined;
}
export declare class MultiProductSelectorComponent implements OnInit {
    private catalogInformationService;
    id: string;
    editable: boolean;
    selectedItemIds: string[];
    productNodeComponent: typeof ProductNodeComponent;
    productSelectorItemComponent: typeof ProductSelectorItemComponent;
    getCatalogs: () => Promise<ProductCatalog[]>;
    itemsFetchStrategy: ProductsFetchStrategy;
    private model;
    private qualifier;
    constructor(catalogInformationService: CatalogInformationService, data: GenericEditorWidgetData<ProductModel>);
    ngOnInit(): void;
    onSelectedItemIdsChange(ids: string[]): void;
}
export {};
