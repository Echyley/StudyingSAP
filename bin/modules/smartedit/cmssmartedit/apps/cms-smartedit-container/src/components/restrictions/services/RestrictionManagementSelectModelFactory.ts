/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { Injectable } from '@angular/core';
import {
    ICatalogService,
    IUriContext,
    Page,
    CmsitemsRestService,
    CMSItemSearch,
    CMSItem
} from 'smarteditcommons';
import { IRestrictionType } from '../../../dao/RestrictionTypesRestService';

interface RestrictionModel {
    restrictionTypes: IRestrictionType[];
    selectedRestrictionType: IRestrictionType;
}

interface SelectedIdsModel {
    restriction?: string;
    restrictionType?: number;
}

export class RestrictionManagementSelectModel {
    public selectedIds: SelectedIdsModel;

    private model: RestrictionModel;
    private restrictions: CMSItem[];
    private selectedRestriction: Partial<CMSItem>;
    private supportedRestrictionTypes: string[];

    constructor(
        private cmsitemsRestService: CmsitemsRestService,
        private catalogService: ICatalogService,
        private fetchRestrictionTypes: () => Promise<IRestrictionType[]>,
        private getSupportedRestrictionTypes: () => Promise<string[]>
    ) {
        this.selectedIds = {};
        this.model = {
            restrictionTypes: [],
            selectedRestrictionType: null
        };
        this.restrictions = [];
        this.supportedRestrictionTypes = [];
    }

    public async initialize(): Promise<void> {
        const restrictionTypesResponse = await this.fetchRestrictionTypes();
        this.model.restrictionTypes = restrictionTypesResponse.map((type, index) => ({
            ...type,
            id: index
        }));

        if (typeof this.getSupportedRestrictionTypes === 'function') {
            this.supportedRestrictionTypes = await this.getSupportedRestrictionTypes();
        } else {
            this.supportedRestrictionTypes = this.model.restrictionTypes.map((type) => type.code);
        }
    }

    public async getRestrictionsPaged(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<CMSItem>> {
        const requestParams: CMSItemSearch = {
            pageSize,
            currentPage,
            typeCode: this.model.selectedRestrictionType.code,
            mask,
            itemSearchParams: ''
        };

        if (currentPage === 0) {
            this.restrictions = [];
        }

        const restrictionsResponse = await this.cmsitemsRestService.get(requestParams);
        const restrictionResult = restrictionsResponse.response.map((restriction) => ({
            ...restriction,
            id: restriction.uid
        }));
        this.restrictions = this.restrictions.concat(restrictionResult);

        return {
            pagination: restrictionsResponse.pagination,
            results: restrictionResult
        };
    }

    public getRestrictionFromBackend(): Promise<CMSItem> {
        return Promise.resolve({} as CMSItem);
    }

    public getRestrictionTypes(): Promise<IRestrictionType[]> {
        return Promise.resolve(this.model.restrictionTypes);
    }

    public restrictionSelected(): boolean {
        if (this.selectedIds.restriction) {
            this.selectedRestriction = this.restrictions.find(
                (restriction) => restriction.id === this.selectedIds.restriction
            );

            return true;
        }

        return false;
    }

    public restrictionTypeSelected(): boolean {
        this.selectedIds.restriction = null;
        this.model.selectedRestrictionType =
            (this.model.restrictionTypes || []).find(
                (restrictionType) => restrictionType.id === this.selectedIds.restrictionType
            ) || null;
        if (this.model.selectedRestrictionType) {
            this.selectedRestriction = {
                typeCode: this.model.selectedRestrictionType.code
            };

            return true;
        }

        return false;
    }

    public async createRestrictionSelected(name: string, uriContext: IUriContext): Promise<void> {
        this.selectedRestriction = {
            itemtype: this.model.selectedRestrictionType.code,
            name
        };

        const catalogVersionUuid = await this.catalogService.getCatalogVersionUUid(uriContext);
        this.selectedRestriction.catalogVersion = catalogVersionUuid;
    }

    public getRestrictionTypeCode(): string {
        return this.model.selectedRestrictionType?.code;
    }

    public getRestriction(): Partial<CMSItem> {
        return this.selectedRestriction;
    }

    public isTypeSupported(): boolean {
        if (this.getRestrictionTypeCode()) {
            return this.supportedRestrictionTypes.includes(this.model.selectedRestrictionType.code);
        }

        return false;
    }
}

@Injectable()
export class RestrictionManagementSelectModelFactory {
    constructor(
        private cmsitemsRestService: CmsitemsRestService,
        private catalogService: ICatalogService
    ) {}

    public createRestrictionManagementSelectModel(
        fetchRestrictionTypes: () => Promise<IRestrictionType[]>,
        getSupportedRestrictionTypes: () => Promise<string[]>
    ): RestrictionManagementSelectModel {
        const instance = new RestrictionManagementSelectModel(
            this.cmsitemsRestService,
            this.catalogService,
            fetchRestrictionTypes,
            getSupportedRestrictionTypes
        );
        instance.initialize();
        return instance;
    }
}
