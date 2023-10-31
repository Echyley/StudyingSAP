/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorInterface,
    DropdownPopulatorItemPayload,
    DropdownPopulatorPagePayload,
    GenericEditorOption,
    LanguageService,
    UriDropdownPopulator
} from 'smarteditcommons';
import { ContextAwareCatalogService } from '../../../services';
import { PopulatorItem } from './types';

@Injectable()
export class CategoryDropdownPopulator extends DropdownPopulatorInterface {
    constructor(
        private contextAwareCatalogService: ContextAwareCatalogService,
        languageService: LanguageService,
        translateService: TranslateService,
        private uriDropdownPopulator: UriDropdownPopulator
    ) {
        super(lodash, languageService, translateService);
    }

    public async fetchPage(
        payload: DropdownPopulatorPagePayload
    ): Promise<DropdownPopulatorFetchPageResponse<PopulatorItem>> {
        if (payload.model.productCatalog === undefined) {
            throw new Error('"productCatalog" is required but it was not provided.');
        }

        const uri = await this.contextAwareCatalogService.getProductCategorySearchUri(
            payload.model.productCatalog as string
        );
        payload.field.uri = uri;

        const langIsoCode = await this.languageService.getResolveLocale();
        payload.field.params.langIsoCode = langIsoCode;

        return this.uriDropdownPopulator.fetchPage(payload);
    }

    public async getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        const uri = await this.contextAwareCatalogService.getProductCategoryItemUri();
        payload.field.uri = uri;

        return this.uriDropdownPopulator.getItem(payload);
    }

    public isPaged(): boolean {
        return true;
    }
}
