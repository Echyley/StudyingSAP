import { OnInit } from '@angular/core';
import { GenericEditorWidgetData } from 'smarteditcommons';
import { CatalogInformationService, CategoriesFetchStrategy, ProductCatalog } from '../../services';
import { CategoryNodeComponent } from './categoryNode';
import { CategorySelectorItemComponent } from './categorySelectorItem';
interface CategoryModel {
    categories: string[] | undefined;
}
export declare class MultiCategorySelectorComponent implements OnInit {
    private catalogInformationService;
    id: string;
    selectedItemIds: string[];
    editable: boolean;
    categoryNodeComponent: typeof CategoryNodeComponent;
    categorySelectorItemComponent: typeof CategorySelectorItemComponent;
    getCatalogs: () => Promise<ProductCatalog[]>;
    itemsFetchStrategy: CategoriesFetchStrategy;
    model: CategoryModel;
    qualifier: string;
    constructor(catalogInformationService: CatalogInformationService, data: GenericEditorWidgetData<CategoryModel>);
    ngOnInit(): void;
    onSelectedItemIdsChange(ids: string[]): void;
}
export {};
