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
export class ProductDropdownPopulator extends DropdownPopulatorInterface {
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
        const uri = await this.contextAwareCatalogService.getProductSearchUri(
            payload.model.productCatalog as string
        );

        payload.field.uri = uri;
        return this.uriDropdownPopulator.fetchPage(payload);
    }

    public isPaged(): boolean {
        return true;
    }

    public async getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        const uri = await this.contextAwareCatalogService.getProductItemUri();

        payload.field.uri = uri;
        return this.uriDropdownPopulator.getItem(payload);
    }
}
