import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { FetchStrategy, GenericEditorWidgetData, IBaseCatalog, ICatalogService, SelectReset, SystemEventService, TypedMap } from 'smarteditcommons';
import './ProductCatalogVersionsSelectorComponent.scss';
export declare class ProductCatalogVersionsSelectorComponent implements OnInit, OnDestroy {
    geData: GenericEditorWidgetData<TypedMap<any>>;
    private catalogService;
    private systemEventService;
    private cdr;
    contentCatalogVersionId: string;
    isReady: boolean;
    isSingleVersionSelector: boolean;
    isMultiVersionSelector: boolean;
    fetchStrategy: FetchStrategy;
    reset: SelectReset;
    productCatalogs: IBaseCatalog[];
    private $unRegSiteChangeEvent;
    constructor(geData: GenericEditorWidgetData<TypedMap<any>>, catalogService: ICatalogService, systemEventService: SystemEventService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    private resetSelector;
    private setContent;
    private getSiteUIDFromContentCatalogVersionId;
    private parseSingleCatalogVersion;
}
