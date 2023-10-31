import { OnInit } from '@angular/core';
import { GenericEditorWidgetData, GenericEditorField, ICatalogService, IBaseCatalog, IGenericEditor, TypedMap, SelectReset } from 'smarteditcommons';
import { SingeActiveCatalogAwareItemSelectorItemRendererComponent } from './SingeActiveCatalogAwareItemSelectorItemRendererComponent';
import { CMSLinkItem } from './types';
export declare enum SINGLE_CATALOG_AWARE_ITEM_MAPPING {
    SingleOnlineProductSelector = "product",
    SingleOnlineCategorySelector = "category"
}
export declare class SingleActiveCatalogAwareItemSelectorComponent implements OnInit {
    private data;
    private catalogService;
    mainDropDownI18nKey: string;
    propertyType: SINGLE_CATALOG_AWARE_ITEM_MAPPING;
    qualifier: string;
    catalogName: TypedMap<string>;
    catalogs: IBaseCatalog[];
    editor: IGenericEditor;
    field: GenericEditorField;
    itemComponent: typeof SingeActiveCatalogAwareItemSelectorItemRendererComponent;
    model: CMSLinkItem;
    productCatalogField: GenericEditorField;
    reset: SelectReset;
    private initProductCatalog;
    constructor(data: GenericEditorWidgetData<CMSLinkItem>, catalogService: ICatalogService);
    ngOnInit(): Promise<void>;
    ngDoCheck(): void;
    private augmentDropdownAttributes;
    private initProductCatalogs;
    private getFieldPropertyType;
    private resetSelector;
}
