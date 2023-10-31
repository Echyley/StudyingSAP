/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import { PersonalizationsmarteditUtils, Customization } from 'personalizationcommons';
import { PersonalizationsmarteditRestService } from '../service';

@Injectable()
export class CustomizationDataFactory {
    public defaultSuccessCallbackFunction: (repsonse: any) => void;
    public defaultErrorCallbackFunction: (response: any) => void;

    public items: Customization[] = [];
    private defaultFilter = {};
    private defaultDataArrayName = 'customizations';

    constructor(
        protected personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils
    ) {}

    public async getCustomizations(filter: any): Promise<void> {
        try {
            const customizations = await this.personalizationsmarteditRestService.getCustomizations(
                filter
            );
            this.personalizationsmarteditUtils.uniqueArray(
                this.items,
                customizations[this.defaultDataArrayName] || []
            );
            this.defaultSuccessCallbackFunction(customizations);
        } catch (e) {
            this.defaultErrorCallbackFunction(e);
        }
    }

    public updateData(
        params: any,
        successCallbackFunction: (response: any) => void,
        errorCallbackFunction: (response: any) => void
    ): any {
        params = params || {};
        this.defaultFilter = params.filter || this.defaultFilter;
        this.defaultDataArrayName = params.dataArrayName || this.defaultDataArrayName;
        if (successCallbackFunction && typeof successCallbackFunction === 'function') {
            this.defaultSuccessCallbackFunction = successCallbackFunction;
        }
        if (errorCallbackFunction && typeof errorCallbackFunction === 'function') {
            this.defaultErrorCallbackFunction = errorCallbackFunction;
        }

        this.getCustomizations(this.defaultFilter);
    }

    public refreshData(): void {
        if (this.defaultFilter === {}) {
            return;
        }
        const tempFilter: any = lodash.cloneDeep(this.defaultFilter);
        tempFilter.currentSize = this.items.length;
        tempFilter.currentPage = 0;
        this.resetData();
        this.getCustomizations(tempFilter);
    }

    public resetData(): void {
        this.items.length = 0;
    }
}
