import { TranslateService } from '@ngx-translate/core';
import * as lo from 'lodash';
import { LanguageService } from '../../../../../services/language/LanguageService';
import { GenericEditorOption } from '../../../types';
import { DropdownPopulatorFetchPageResponse, DropdownPopulatorItemPayload, DropdownPopulatorPagePayload, DropdownPopulatorPayload, IDropdownPopulator } from './types';
/**
 * Interface describing the contract of a DropdownPopulator resolved by
 * {@link GenericEditorFactoryService} to populate the dropdowns of {@link GenericEditorDropdownComponent}.
 */
export declare class DropdownPopulatorInterface implements IDropdownPopulator {
    lodash: lo.LoDashStatic;
    languageService: LanguageService;
    translateService?: TranslateService;
    constructor(lodash: lo.LoDashStatic, languageService: LanguageService, translateService?: TranslateService);
    getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption>;
    /**
     * Returns a promise resolving to a list of items.
     * The items must all contain a property `id`.
     */
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    /**
     * Returns a promise resolving to a page of items.
     * The items must all contain a property `id`.
     */
    fetchPage(payload: DropdownPopulatorPagePayload): Promise<DropdownPopulatorFetchPageResponse>;
    /**
     * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false.
     */
    isPaged(): boolean;
    /**
     * Populates the id and label property for each item in the list. If the label property is not already set,
     * then we use an ordered list of attributes to use when determining the label for each item.
     * @param items The array of items to set the id and label attributes on
     * @param idAttribute The name of the id attribute
     * @param orderedLabelAttributes The ordered list of label attributes
     * @returns The modified list of items
     */
    populateAttributes(items: GenericEditorOption[], idAttribute: string, orderedLabelAttributes: string[]): GenericEditorOption[];
    /**
     * Searches a list and returns a promise resolving to only items with a label attribute that matches the search term.
     * @param items The list of items to search
     * @param searchTerm The search term to filter items by
     * @returns The filtered list of items
     */
    search(items: GenericEditorOption[], searchTerm: string): Promise<GenericEditorOption[]>;
}
