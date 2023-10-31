/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { GenericEditorWidgetData, GENERIC_EDITOR_WIDGET_DATA } from 'smarteditcommons';
import { CatalogInformationService, CategoriesFetchStrategy, ProductCatalog } from '../../services';
import { CategoryNodeComponent } from './categoryNode';
import { CategorySelectorItemComponent } from './categorySelectorItem';

interface CategoryModel {
    categories: string[] | undefined;
}

@Component({
    selector: 'se-multi-category-selector',
    templateUrl: './MultiCategorySelectorComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiCategorySelectorComponent implements OnInit {
    /** An identifier used to track down the component in the page. */
    public id: string;
    public selectedItemIds: string[];
    /**
     * A flag that specifies whether the component can be modified or not.
     * If the component cannot be modified, then the categories selected are read-only.
     */
    public editable: boolean;

    public categoryNodeComponent: typeof CategoryNodeComponent;
    public categorySelectorItemComponent: typeof CategorySelectorItemComponent;
    public getCatalogs: () => Promise<ProductCatalog[]>;
    public itemsFetchStrategy: CategoriesFetchStrategy;
    /**
     * The object where the list of selected categories will be stored.
     * The model must contain a property with the same name as the qualifier.
     * That property must be of type array and is used to store the UIDs of the categories selected.
     */
    public model: CategoryModel;
    /** The key of the property in the model that will hold the list of categories selected. */
    public qualifier: string;

    constructor(
        private catalogInformationService: CatalogInformationService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<CategoryModel>
    ) {
        ({
            model: this.model,
            qualifier: this.qualifier,
            field: { qualifier: this.id, editable: this.editable }
        } = data);
        this.categoryNodeComponent = CategoryNodeComponent;
        this.categorySelectorItemComponent = CategorySelectorItemComponent;
    }

    ngOnInit(): void {
        this.getCatalogs = this.catalogInformationService.makeGetProductCatalogsInformation();
        this.itemsFetchStrategy = this.catalogInformationService.categoriesFetchStrategy;

        if (this.editable === undefined) {
            this.editable = true;
        }

        this.selectedItemIds = [...(this.model[this.qualifier] || [])];
    }

    public onSelectedItemIdsChange(ids: string[]): void {
        this.selectedItemIds = [...ids];

        this.model[this.qualifier] = this.selectedItemIds;
    }
}
