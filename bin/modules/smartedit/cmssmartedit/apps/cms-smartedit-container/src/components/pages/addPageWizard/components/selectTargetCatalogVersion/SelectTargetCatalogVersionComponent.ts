/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    Input,
    OnInit,
    Output,
    EventEmitter,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { isEmpty } from 'lodash';
import {
    FetchStrategy,
    ICatalogService,
    ICatalogVersion,
    IUriContext,
    SelectApi,
    SelectItem,
    VALIDATION_MESSAGE_TYPES,
    IPageService
} from 'smarteditcommons';
import { CatalogVersionRestService } from '../../../../../dao';
import { PageFacade } from '../../../../../facades';

@Component({
    selector: 'se-select-target-catalog-version',
    templateUrl: './SelectTargetCatalogVersionComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
/**
 * Displays a list of catalog versions used for selecting the target catalog version that will be applied to the cloned page
 */
export class SelectTargetCatalogVersionComponent implements OnInit {
    /**
     * The uri context containing site/catalog information
     */
    @Input() uriContext: IUriContext;
    /**
     * The page typeCode of a potential new page
     */
    @Input() pageTypeCode: string;
    /**
     * The page label of a potential new page
     */
    @Input() pageLabel: string;
    /**
     * An optional output function binding. Every time there is a change to the catalogVersion selection,
     * or resulting target catalog version, this function (if it exists) will get executed
     */
    @Output() onTargetCatalogVersionSelected: EventEmitter<any>;

    public catalogVersions: ICatalogVersion[];
    public selectedCatalogVersion: string;
    public catalogVersionContainsPageWithSameLabel: boolean;
    public catalogVersionSelectorFetchStrategy: FetchStrategy;

    public onSelectionChange: () => Promise<void>;
    public selectApi: SelectApi;

    constructor(
        private readonly pageFacade: PageFacade,
        private readonly catalogVersionRestService: CatalogVersionRestService,
        private readonly catalogService: ICatalogService,
        private readonly pageService: IPageService,
        private readonly cdr: ChangeDetectorRef
    ) {
        this.catalogVersions = [];
        this.selectedCatalogVersion = null;
        this.catalogVersionContainsPageWithSameLabel = false;
        this.onTargetCatalogVersionSelected = new EventEmitter();
    }

    async ngOnInit(): Promise<void> {
        this.catalogVersionSelectorFetchStrategy = {
            fetchAll: (): Promise<SelectItem[]> =>
                Promise.resolve(
                    (this.catalogVersions || []).map((catalogVersion) => ({
                        id: catalogVersion.uuid,
                        label: catalogVersion.name
                    }))
                )
        };

        this.onSelectionChange = (): Promise<void> => this.selectionChangeHandler();

        await this.setupCatalogVersions();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public setSelectApi(api: SelectApi): void {
        this.selectApi = api;
    }

    private async setupCatalogVersions(): Promise<void> {
        const targets = await this.catalogVersionRestService.getCloneableTargets(this.uriContext);
        const catalogVersions = targets.versions;

        if (!isEmpty(catalogVersions)) {
            const uuid = await this.catalogService.getCatalogVersionUUid(this.uriContext);
            const currentCatalogVersion = catalogVersions.find(
                (catalogVersion) => catalogVersion.uuid === uuid
            );

            if (currentCatalogVersion) {
                this.selectedCatalogVersion = currentCatalogVersion.uuid;
            } else {
                this.selectedCatalogVersion = catalogVersions[0].uuid;
            }

            this.catalogVersions = catalogVersions;
        }
    }

    private async selectionChangeHandler(): Promise<void> {
        if (!this.selectedCatalogVersion) {
            return;
        }

        const catalogVersion = await this.catalogService.getCatalogVersionByUuid(
            this.selectedCatalogVersion
        );

        this.onTargetCatalogVersionSelected.emit(catalogVersion);

        if (this.pageTypeCode === 'ContentPage') {
            this.catalogVersionContainsPageWithSameLabel =
                await this.determineContentPageWithLabelExists(catalogVersion);
        }
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }

        this.updateSelectValidationState();
    }

    private determineContentPageWithLabelExists(catalogVersion: ICatalogVersion): Promise<boolean> {
        return this.pageFacade.contentPageWithLabelExists(
            this.pageLabel,
            catalogVersion.catalogId,
            catalogVersion.version
        );
    }

    private updateSelectValidationState(): void {
        if (!this.selectApi) {
            return;
        }

        if (this.catalogVersionContainsPageWithSameLabel) {
            this.selectApi.setValidationState(VALIDATION_MESSAGE_TYPES.WARNING);
        } else {
            this.selectApi.resetValidationState();
        }
    }
}
