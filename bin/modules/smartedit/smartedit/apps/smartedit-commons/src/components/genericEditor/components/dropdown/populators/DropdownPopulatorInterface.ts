/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import * as lo from 'lodash';

import { LanguageService } from '../../../../../services/language/LanguageService';
import { GenericEditorOption } from '../../../types';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorItemPayload,
    DropdownPopulatorPagePayload,
    DropdownPopulatorPayload,
    IDropdownPopulator
} from './types';

/**
 * Interface describing the contract of a DropdownPopulator resolved by
 * {@link GenericEditorFactoryService} to populate the dropdowns of {@link GenericEditorDropdownComponent}.
 */
export class DropdownPopulatorInterface implements IDropdownPopulator {
    constructor(
        public lodash: lo.LoDashStatic,
        public languageService: LanguageService,
        public translateService?: TranslateService
    ) {}

    public getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        return null;
    }

    /**
     * Returns a promise resolving to a list of items.
     * The items must all contain a property `id`.
     */
    public fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]> {
        'proxyFunction';
        return null;
    }

    /**
     * Returns a promise resolving to a page of items.
     * The items must all contain a property `id`.
     */
    public fetchPage(
        payload: DropdownPopulatorPagePayload
    ): Promise<DropdownPopulatorFetchPageResponse> {
        'proxyFunction';
        return null;
    }

    /**
     * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false.
     */
    public isPaged(): boolean {
        return false;
    }

    /**
     * Populates the id and label property for each item in the list. If the label property is not already set,
     * then we use an ordered list of attributes to use when determining the label for each item.
     * @param items The array of items to set the id and label attributes on
     * @param idAttribute The name of the id attribute
     * @param orderedLabelAttributes The ordered list of label attributes
     * @returns The modified list of items
     */
    public populateAttributes(
        items: GenericEditorOption[],
        idAttribute: string,
        orderedLabelAttributes: string[]
    ): GenericEditorOption[] {
        return this.lodash.map(items, (item: GenericEditorOption) => {
            if (idAttribute && this.lodash.isEmpty(item.id)) {
                item.id = item[idAttribute];
            }

            if (orderedLabelAttributes && this.lodash.isEmpty(item.label)) {
                // Find the first attribute that the item object contains
                const labelAttribute = this.lodash.find(
                    orderedLabelAttributes,
                    (attr: string) => !this.lodash.isEmpty(item[attr])
                );

                // If we found an attribute, set the label
                if (labelAttribute) {
                    item.label = item[labelAttribute];
                }
            }

            return item;
        });
    }

    /**
     * Searches a list and returns a promise resolving to only items with a label attribute that matches the search term.
     * @param items The list of items to search
     * @param searchTerm The search term to filter items by
     * @returns The filtered list of items
     */
    public search(
        items: GenericEditorOption[],
        searchTerm: string
    ): Promise<GenericEditorOption[]> {
        return this.languageService.getResolveLocale().then((isocode: string) =>
            this.lodash.filter(items, (item: GenericEditorOption) => {
                let labelValue: string;
                if (this.lodash.isObject(item.label)) {
                    isocode = item.label[isocode] ? isocode : Object.keys(item.label)[0];
                    labelValue = item.label[isocode];
                } else {
                    labelValue = this.translateService
                        ? this.translateService.instant(item.label)
                        : item.label;
                }
                return (
                    labelValue && labelValue.toUpperCase().indexOf(searchTerm.toUpperCase()) > -1
                );
            })
        );
    }
}
