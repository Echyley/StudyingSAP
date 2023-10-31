/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { GenericEditorWidgetData, GENERIC_EDITOR_WIDGET_DATA } from 'smarteditcommons';
import { CatalogInformationService, ProductCatalog, ProductsFetchStrategy } from '../../services';
import { ProductNodeComponent } from './productNode/ProductNodeComponent';
import { ProductSelectorItemComponent } from './productSelectorItem';

interface ProductModel {
    products: string[] | undefined;
}

/**
 *  A component that allows users to select products from one or more catalogs.
 *  This component is catalog aware; the list of products displayed is dependent on
 *  the product catalog and catalog version selected by the user within the component.
 */
@Component({
    selector: 'se-multi-product-selector',
    templateUrl: './MultiProductSelectorComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiProductSelectorComponent implements OnInit {
    /** An identifier used to track down the component in the page. */
    public id: string;

    /**
     * This property specifies whether the selector can be edited or not. If this flag is false,
     * then the selector is treated as read-only; the selection cannot be modified in any way. Optional, default value is true.
     */
    public editable: boolean;
    public selectedItemIds: string[];
    public productNodeComponent: typeof ProductNodeComponent;
    public productSelectorItemComponent: typeof ProductSelectorItemComponent;

    public getCatalogs: () => Promise<ProductCatalog[]>;
    public itemsFetchStrategy: ProductsFetchStrategy;

    /**
     * The object where the list of selected products will be stored. The model must contain a property with the same name as the qualifier. That property must be
     * of type array and is used to store the UIDs of the products selected.
     */
    private model: ProductModel;
    /** The key of the property in the model that will hold the list of products selected. */
    private qualifier: string;

    constructor(
        private catalogInformationService: CatalogInformationService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<ProductModel>
    ) {
        ({
            field: { qualifier: this.id },
            model: this.model,
            qualifier: this.qualifier,
            field: { editable: this.editable }
        } = data);
        this.productNodeComponent = ProductNodeComponent;
        this.productSelectorItemComponent = ProductSelectorItemComponent;
    }

    ngOnInit(): void {
        this.getCatalogs = this.catalogInformationService.makeGetProductCatalogsInformation();
        this.itemsFetchStrategy = this.catalogInformationService.productsFetchStrategy;

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
