/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { CONTEXT_SITE_ID } from 'cmscommons';
import {
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    GenericEditorField,
    ICatalogService,
    IBaseCatalog,
    IGenericEditor,
    TypedMap,
    SelectReset
} from 'smarteditcommons';
import { SingeActiveCatalogAwareItemSelectorItemRendererComponent } from './SingeActiveCatalogAwareItemSelectorItemRendererComponent';
import { CMSLinkItem } from './types';

export enum SINGLE_CATALOG_AWARE_ITEM_MAPPING {
    SingleOnlineProductSelector = 'product',
    SingleOnlineCategorySelector = 'category'
}

@Component({
    selector: 'se-single-active-catalog-aware-item-selector',
    templateUrl: './SingleActiveCatalogAwareItemSelectorComponent.html',
    styleUrls: ['./SingleActiveCatalogAwareItemSelectorComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class SingleActiveCatalogAwareItemSelectorComponent implements OnInit {
    public mainDropDownI18nKey: string;
    public propertyType: SINGLE_CATALOG_AWARE_ITEM_MAPPING;
    public qualifier: string;
    public catalogName: TypedMap<string>;
    public catalogs: IBaseCatalog[];
    public editor: IGenericEditor;
    public field: GenericEditorField;
    public itemComponent: typeof SingeActiveCatalogAwareItemSelectorItemRendererComponent;
    public model: CMSLinkItem;
    public productCatalogField: GenericEditorField;
    /**
     * Resets the second se-generic-editor-dropdown model when parent model changes
     */
    public reset: SelectReset;
    private initProductCatalog: string;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) private data: GenericEditorWidgetData<CMSLinkItem>,
        private catalogService: ICatalogService
    ) {
        ({
            editor: this.editor,
            field: this.field,
            model: this.model,
            qualifier: this.qualifier
        } = this.data);

        this.catalogName = {};
        this.catalogs = [];
        this.itemComponent = SingeActiveCatalogAwareItemSelectorItemRendererComponent;
        this.initProductCatalog = this.model.productCatalog;
    }

    async ngOnInit(): Promise<void> {
        this.augmentDropdownAttributes();
        return this.initProductCatalogs();
    }

    ngDoCheck(): void {
        if (this.catalogs.length > 1 && this.initProductCatalog !== this.model.productCatalog) {
            this.initProductCatalog = this.model.productCatalog;
            this.resetSelector();
        }
    }

    /**
     * Augment the seDropdown attributes bindings to init the seDropdown with proper settings
     * The 'propertyType' value enable the usage of custom populators in seDropdown
     */
    private augmentDropdownAttributes(): void {
        this.propertyType = this.getFieldPropertyType(this.field);
        if (this.propertyType) {
            this.productCatalogField = {
                idAttribute: 'catalogId',
                labelAttributes: ['name'],
                editable: this.field.editable,
                propertyType: 'productCatalog'
            } as GenericEditorField;

            this.mainDropDownI18nKey = this.field.i18nKey;
            delete this.field.i18nKey;
            this.field.paged = true;
            this.field.idAttribute = 'uid';
            this.field.labelAttributes = ['name'];
            this.field.dependsOn = 'productCatalog';
            this.field.propertyType = this.propertyType;
        }
    }

    /**
     * Filter on active product catalogs:
     * - If there is only one product catalog, will hide the product catalog seDropDown and show the product catalog name
     * - If there is more than one product catalog, will show the product catalog seDropDown
     */
    private async initProductCatalogs(): Promise<void> {
        const catalogs = await this.catalogService.getProductCatalogsBySiteKey(CONTEXT_SITE_ID);
        this.catalogs = catalogs;
        if (this.catalogs.length === 1) {
            this.model.productCatalog = this.catalogs[0].catalogId;
            this.editor.form.pristine.productCatalog = this.catalogs[0].catalogId;
            this.catalogName = this.catalogs[0].name;
        }
    }

    private getFieldPropertyType(field: GenericEditorField): SINGLE_CATALOG_AWARE_ITEM_MAPPING {
        const type = field.cmsStructureType as keyof typeof SINGLE_CATALOG_AWARE_ITEM_MAPPING;

        return SINGLE_CATALOG_AWARE_ITEM_MAPPING[type];
    }

    private resetSelector(): void {
        if (typeof this.reset === 'function') {
            this.reset(true);
        }
    }
}
