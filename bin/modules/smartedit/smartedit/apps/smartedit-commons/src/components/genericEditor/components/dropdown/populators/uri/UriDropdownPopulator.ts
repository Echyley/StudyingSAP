/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { RestServiceFactory, TypedMap } from '@smart/utils';
import * as lodash from 'lodash';

import { LanguageService } from '../../../../../../services/language/LanguageService';
import { apiUtils } from '../../../../../../utils';
import { GenericEditorOption } from '../../../../types';
import { DropdownPopulatorInterface } from '../DropdownPopulatorInterface';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorItemPayload,
    DropdownPopulatorPagePayload,
    DropdownPopulatorPayload
} from '../types';

/**
 * Implementation of {@link DropdownPopulatorInterface} for "EditableDropdown" `cmsStructureType` containing `uri` attribute.
 */
@Injectable()
export class UriDropdownPopulator extends DropdownPopulatorInterface {
    constructor(
        private restServiceFactory: RestServiceFactory,
        public languageService: LanguageService,
        public translateService: TranslateService
    ) {
        super(lodash, languageService, translateService);
    }

    /**
     * Implementation of the [fetchAll]{@link DropdownPopulatorInterface#fetchAll} method.
     */
    public fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]> {
        let params;

        if (payload.field.dependsOn) {
            params = this._buildQueryParams(payload.field.dependsOn, payload.model);
        }

        return this.restServiceFactory
            .get<DropdownPopulatorPayload>(payload.field.uri)
            .get(params)
            .then((response: DropdownPopulatorPayload) => {
                const dataFromResponse = apiUtils.getDataFromResponse(response);
                const options = this.populateAttributes(
                    dataFromResponse,
                    payload.field.idAttribute,
                    payload.field.labelAttributes
                );

                if (payload.search) {
                    return this.search(options, payload.search);
                }

                return Promise.resolve(options) as PromiseLike<GenericEditorOption[]>;
            });
    }

    /**
     * Implementation of the [fetchPage]{@link DropdownPopulatorInterface#fetchPage} method.
     */
    public fetchPage<T>(
        payload: DropdownPopulatorPagePayload
    ): Promise<DropdownPopulatorFetchPageResponse<T>> {
        let params: TypedMap<any> = {};

        if (payload.field.dependsOn) {
            params = this._buildQueryParams(payload.field.dependsOn, payload.model);
        }

        params.pageSize = payload.pageSize;
        params.currentPage = payload.currentPage;
        params.mask = payload.search;

        if (payload.field.params) {
            this.lodash.extend(params, payload.field.params);
        }

        return this.restServiceFactory
            .get<DropdownPopulatorFetchPageResponse>(payload.field.uri)
            .get(params)
            .then((response: DropdownPopulatorFetchPageResponse) => {
                const key = apiUtils.getKeyHoldingDataFromResponse(response);
                response[key] = this.populateAttributes(
                    response[key],
                    payload.field.idAttribute,
                    payload.field.labelAttributes
                );

                return Promise.resolve(response) as PromiseLike<DropdownPopulatorFetchPageResponse>;
            });
    }

    /**
     * Implementation of the [getItem]{@link DropdownPopulatorInterface#getItem} method.
     * @returns A promise that resolves to the option that was fetched
     */
    public getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        return this.restServiceFactory
            .get<GenericEditorOption>(payload.field.uri)
            .getById(payload.id)
            .then((item) => {
                item = this.populateAttributes(
                    [item],
                    payload.field.idAttribute,
                    payload.field.labelAttributes
                )[0];

                return Promise.resolve(item);
            });
    }

    private _buildQueryParams(dependsOn: string, model: any): TypedMap<any> {
        const queryParams = dependsOn.split(',').reduce((obj: TypedMap<any>, current: string) => {
            obj[current] = model[current];
            return obj;
        }, {});

        return queryParams;
    }
}
