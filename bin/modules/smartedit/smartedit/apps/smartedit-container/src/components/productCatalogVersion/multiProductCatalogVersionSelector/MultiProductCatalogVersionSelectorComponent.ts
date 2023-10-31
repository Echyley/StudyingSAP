/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output
} from '@angular/core';
import { combineLatest, BehaviorSubject, Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import {
    IBaseCatalog,
    IBaseCatalogVersion,
    IModalService,
    L10nPipe,
    LocalizedMap,
    SystemEventService
} from 'smarteditcommons';
import { MultiProductCatalogVersionConfigurationComponent } from '../multiProductCatalogVersionConfiguration';
import { MultiProductCatalogVersionSelectorData, MULTI_PRODUCT_CATALOGS_UPDATED } from '../types';

type CatalogNameCatalogVersionLabelMap = Map<string, { name: LocalizedMap; version: string }>;

import './MultiProductCatalogVersionSelectorComponent.scss';

/**
 * Represents a selector of Product Catalogs versions.
 * When clicked, it will open a [version configuration modal]{@link MultiProductCatalogVersionSelectorComponent}.
 */
@Component({
    selector: 'se-multi-product-catalog-version-selector',
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-multi-product-catalog-version-selector]': 'true'
    },
    templateUrl: './MultiProductCatalogVersionSelectorComponent.html'
})
export class MultiProductCatalogVersionSelectorComponent implements OnInit, OnDestroy, OnChanges {
    @Input() productCatalogs: IBaseCatalog[];
    @Input() selectedProductCatalogVersions: string[];
    @Output() selectedProductCatalogVersionsChange = new EventEmitter<string[]>();

    /**
     * Emits a value for selected options input
     */
    public multiProductCatalogVersionsSelectedOptions$ = new BehaviorSubject<string>('');

    /**
     * Stores catalog name and catalog version identified by catalog id
     * that is updated when `productCatalogs` changes.
     */
    private catalogNameCatalogVersionLabelMap: CatalogNameCatalogVersionLabelMap;
    private $unRegEventForMultiProducts: () => void;

    constructor(
        private l10nPipe: L10nPipe,
        private modalService: IModalService,
        private systemEventService: SystemEventService
    ) {}

    ngOnInit(): void {
        // Published by MultiProductCatalogVersionSelectorComponent when save button is clicked.
        this.$unRegEventForMultiProducts = this.systemEventService.subscribe(
            MULTI_PRODUCT_CATALOGS_UPDATED,
            (eventId: string, catalogVersions: string[]) =>
                this.updateProductCatalogVersionsModel(eventId, catalogVersions)
        );
    }

    ngOnDestroy(): void {
        if (this.$unRegEventForMultiProducts) {
            this.$unRegEventForMultiProducts();
        }
    }

    ngOnChanges(): void {
        this.setMultiVersionSelectorTexts(this.productCatalogs);
    }

    /** Selector click handler.  */
    public onClickSelector(): void {
        this.modalService.open<MultiProductCatalogVersionSelectorData>({
            component: MultiProductCatalogVersionConfigurationComponent,
            data: {
                productCatalogs: this.productCatalogs,
                selectedCatalogVersions: this.selectedProductCatalogVersions
            },
            templateConfig: {
                title: 'se.modal.product.catalog.configuration'
            },
            config: {
                dialogPanelClass: 'modal-md modal-stretched'
            }
        });
    }

    /**
     * Returns a concatenated string of translated Product Catalog Name and Product Catalog Version.
     *
     * E.g. "productCatalogName (Online)", "productCatalogName (Staged)"
     */
    public getCatalogNameCatalogVersionLabel(catalogId: string): Observable<string> {
        const catalogNameCatalogVersionLabel =
            this.catalogNameCatalogVersionLabelMap.get(catalogId);
        return this.l10nPipe
            .transform(catalogNameCatalogVersionLabel.name)
            .pipe(map((name) => `${name} (${catalogNameCatalogVersionLabel.version})`));
    }

    /** Sets texts of Multi Selector Tooltip and Input.  */
    private setMultiVersionSelectorTexts(productCatalogs: IBaseCatalog[]): void {
        this.catalogNameCatalogVersionLabelMap = this.buildCatalogNameCatalogVersionLabelMap(
            productCatalogs,
            this.selectedProductCatalogVersions
        );

        this.setMultiProductCatalogVersionsSelectedOptions(productCatalogs);
    }

    /**
     * Returns a map of Catalog Name and Catalog Version with a catalogId as a key.
     * Used for creating strings that are displayed in Multi Selector and Tooltip .
     * It populates a map for given Product Catalog only if the catalog has a version that also exists in the model.
     */
    private buildCatalogNameCatalogVersionLabelMap(
        productCatalogs: IBaseCatalog[],
        versionsFromModel: string[]
    ): CatalogNameCatalogVersionLabelMap {
        const catalogsMap = new Map<string, { name: LocalizedMap; version: string }>();

        productCatalogs.forEach((productCatalog) => {
            const productCatalogVersion = productCatalog.versions.find(
                (version: IBaseCatalogVersion) =>
                    versionsFromModel && versionsFromModel.includes(version.uuid)
            );

            if (productCatalogVersion) {
                catalogsMap.set(productCatalog.catalogId, {
                    name: productCatalog.name,
                    version: productCatalogVersion.version
                });
            }
        });
        return catalogsMap;
    }

    /**
     * Creates and emits a string into the stream used for displaying the value of Multi Selector.
     *
     * E.g. "Clothing Product Catalog (Online), Shoes Product Catalog (Online)"
     */
    private setMultiProductCatalogVersionsSelectedOptions(productCatalogs: IBaseCatalog[]): void {
        if (productCatalogs) {
            const labels$ = Array.from(this.catalogNameCatalogVersionLabelMap).map((key) =>
                this.getCatalogNameCatalogVersionLabel(key[0])
            );
            combineLatest(labels$)
                .pipe(
                    take(1),
                    map((results) => results.join(', '))
                )
                .subscribe((selectedOptions) =>
                    this.multiProductCatalogVersionsSelectedOptions$.next(selectedOptions)
                );
        } else {
            this.multiProductCatalogVersionsSelectedOptions$.next('');
        }
    }

    /** Called when Multi Product Catalogs have been updated. */
    private updateProductCatalogVersionsModel(_eventId: string, catalogVersions: string[]): void {
        this.selectedProductCatalogVersionsChange.emit(catalogVersions);
    }
}
