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

/**
 * TODO: It might be deleted because it seems that it has been replaced by CMSItemDropdownDropdownPopulator.
 */
@Injectable()
export class CmsLinkComponentContentPageDropdownPopulator extends DropdownPopulatorInterface {
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
    ): Promise<DropdownPopulatorFetchPageResponse> {
        const uri = await this.contextAwareCatalogService.getContentPageSearchUri();
        payload.field.uri = uri;

        return this.uriDropdownPopulator.fetchPage(payload);
    }

    public async getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        const uri = await this.contextAwareCatalogService.getContentPageItemUri();
        payload.field.uri = uri;

        return this.uriDropdownPopulator.getItem(payload);
    }

    public isPaged(): boolean {
        return true;
    }
}
