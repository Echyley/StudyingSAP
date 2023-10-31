import { EventEmitter, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { IBaseCatalog, IModalService, L10nPipe, SystemEventService } from 'smarteditcommons';
import './MultiProductCatalogVersionSelectorComponent.scss';
export declare class MultiProductCatalogVersionSelectorComponent implements OnInit, OnDestroy, OnChanges {
    private l10nPipe;
    private modalService;
    private systemEventService;
    productCatalogs: IBaseCatalog[];
    selectedProductCatalogVersions: string[];
    selectedProductCatalogVersionsChange: EventEmitter<string[]>;
    multiProductCatalogVersionsSelectedOptions$: BehaviorSubject<string>;
    private catalogNameCatalogVersionLabelMap;
    private $unRegEventForMultiProducts;
    constructor(l10nPipe: L10nPipe, modalService: IModalService, systemEventService: SystemEventService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    ngOnChanges(): void;
    onClickSelector(): void;
    getCatalogNameCatalogVersionLabel(catalogId: string): Observable<string>;
    private setMultiVersionSelectorTexts;
    private buildCatalogNameCatalogVersionLabelMap;
    private setMultiProductCatalogVersionsSelectedOptions;
    private updateProductCatalogVersionsModel;
}
