/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import {
    CONTEXT_SITE_ID,
    DropdownPopulatorInterface,
    DropdownPopulatorPayload,
    GenericEditorOption,
    IBaseCatalog,
    ICatalogService,
    LanguageService,
    OptionsDropdownPopulator
} from 'smarteditcommons';

@Injectable()
export class ProductCatalogDropdownPopulator extends DropdownPopulatorInterface {
    constructor(
        private catalogService: ICatalogService,
        languageService: LanguageService,
        private optionsDropdownPopulator: OptionsDropdownPopulator,
        translateService: TranslateService
    ) {
        super(lodash, languageService, translateService);
    }

    public async fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]> {
        const catalogs = await this.catalogService.getProductCatalogsBySiteKey(CONTEXT_SITE_ID);

        payload.field.options = catalogs.filter((catalog) =>
            this.hasCatalogOneActiveVersion(catalog)
        );
        return this.optionsDropdownPopulator.fetchAll(payload);
    }

    public isPaged(): boolean {
        return false;
    }

    private hasCatalogOneActiveVersion(catalog: IBaseCatalog): boolean {
        const activeVersions = catalog.versions.filter((version) => version.active === true);
        return activeVersions.length === 1;
    }
}
