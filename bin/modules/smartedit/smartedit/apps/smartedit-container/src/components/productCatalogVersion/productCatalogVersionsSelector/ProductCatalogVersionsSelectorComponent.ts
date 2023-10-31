/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import { cloneDeep } from 'lodash';

import {
    FetchStrategy,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IBaseCatalog,
    IBaseCatalogVersion,
    ICatalogService,
    IGenericEditorDropdownSelectedOptionEventData,
    LINKED_DROPDOWN,
    SelectItem,
    SelectReset,
    SystemEventService,
    TypedMap
} from 'smarteditcommons';

import './ProductCatalogVersionsSelectorComponent.scss';

type SiteChangeEventData = IGenericEditorDropdownSelectedOptionEventData<SelectItem>;

/**
 * Represents a selector of a Product Catalog Version for selected Content Catalog Version.
 * It can be either Single Selector or Multi Selector (if it has multiple product catalogs).
 */
@Component({
    selector: 'se-product-catalog-versions-selector',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-product-catalog-versions-selector]': 'true'
    },
    templateUrl: './ProductCatalogVersionsSelectorComponent.html'
})
export class ProductCatalogVersionsSelectorComponent implements OnInit, OnDestroy {
    public contentCatalogVersionId: string;
    public isReady: boolean;
    public isSingleVersionSelector: boolean;
    public isMultiVersionSelector: boolean;
    public fetchStrategy: FetchStrategy;
    public reset: SelectReset;
    public productCatalogs: IBaseCatalog[];

    private $unRegSiteChangeEvent: () => void;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public geData: GenericEditorWidgetData<TypedMap<any>>,
        private catalogService: ICatalogService,
        private systemEventService: SystemEventService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        this.contentCatalogVersionId = cloneDeep(this.geData.model.previewCatalog);
        if (this.contentCatalogVersionId) {
            this.isReady = false;
            this.isSingleVersionSelector = false;
            this.isMultiVersionSelector = false;

            // subscribe to event emitted from GenericEditorDropdownService, when Content Catalog Version is changed
            const eventId = (this.geData.id || '') + LINKED_DROPDOWN;
            this.$unRegSiteChangeEvent = this.systemEventService.subscribe(
                eventId,
                (id: string, data: SiteChangeEventData) => this.resetSelector(id, data)
            );

            await this.setContent();
        }
        return Promise.resolve();
    }

    ngOnDestroy(): void {
        if (this.$unRegSiteChangeEvent) {
            this.$unRegSiteChangeEvent();
        }
    }

    /** Called when Content Catalog Version is changed. */
    private async resetSelector(_eventId: string, data: SiteChangeEventData): Promise<void> {
        if (
            data.qualifier === 'previewCatalog' &&
            data.optionObject &&
            this.contentCatalogVersionId !== data.optionObject.id
        ) {
            this.contentCatalogVersionId = data.optionObject.id;

            const siteUID = this.getSiteUIDFromContentCatalogVersionId(
                this.contentCatalogVersionId
            );

            const productCatalogs = await this.catalogService.getProductCatalogsForSite(siteUID);
            // eslint-disable-next-line @typescript-eslint/await-thenable
            const activeProductCatalogVersions = await Promise.resolve(
                this.catalogService.returnActiveCatalogVersionUIDs(productCatalogs)
            );

            // sets selected Product Catalog Versions
            this.geData.model[this.geData.qualifier] = activeProductCatalogVersions;

            if (this.isSingleVersionSelector) {
                this.reset();
            }

            this.setContent();
        }
    }

    /**
     * Sets Product Catalogs and flags.
     *
     * For Multi Selector it registers a listener for Multi Product Catalogs updates.
     */
    private async setContent(): Promise<void> {
        const setContent = async (): Promise<void> => {
            this.productCatalogs = await this.catalogService.getProductCatalogsForSite(
                this.getSiteUIDFromContentCatalogVersionId(this.contentCatalogVersionId)
            );

            if (this.productCatalogs.length === 0) {
                return;
            }

            if (this.productCatalogs.length === 1) {
                // Single Selector
                this.fetchStrategy = {
                    fetchAll: (): Promise<SelectItem[]> => {
                        const parsedVersions = this.parseSingleCatalogVersion(
                            this.productCatalogs[0].versions
                        );
                        return Promise.resolve(parsedVersions);
                    }
                };
                this.isSingleVersionSelector = true;
                this.isMultiVersionSelector = false;
                this.isReady = true;
                return;
            }
            // Multi Selector
            this.isSingleVersionSelector = false;
            this.isMultiVersionSelector = true;
            this.isReady = true;
        };

        await setContent();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private getSiteUIDFromContentCatalogVersionId(id: string): string {
        return id.split('|')[0];
    }

    /** Maps catalogs to the items that can be displayed in the dropdown.  */
    private parseSingleCatalogVersion(versions: IBaseCatalogVersion[]): SelectItem[] {
        return versions.map((version) => ({
            id: version.uuid,
            label: version.version
        }));
    }
}
