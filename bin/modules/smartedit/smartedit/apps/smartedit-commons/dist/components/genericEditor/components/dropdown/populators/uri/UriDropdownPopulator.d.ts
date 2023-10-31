import { TranslateService } from '@ngx-translate/core';
import { RestServiceFactory } from '@smart/utils';
import { LanguageService } from '../../../../../../services/language/LanguageService';
import { GenericEditorOption } from '../../../../types';
import { DropdownPopulatorInterface } from '../DropdownPopulatorInterface';
import { DropdownPopulatorFetchPageResponse, DropdownPopulatorItemPayload, DropdownPopulatorPagePayload, DropdownPopulatorPayload } from '../types';
/**
 * Implementation of {@link DropdownPopulatorInterface} for "EditableDropdown" `cmsStructureType` containing `uri` attribute.
 */
export declare class UriDropdownPopulator extends DropdownPopulatorInterface {
    private restServiceFactory;
    languageService: LanguageService;
    translateService: TranslateService;
    constructor(restServiceFactory: RestServiceFactory, languageService: LanguageService, translateService: TranslateService);
    /**
     * Implementation of the [fetchAll]{@link DropdownPopulatorInterface#fetchAll} method.
     */
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    /**
     * Implementation of the [fetchPage]{@link DropdownPopulatorInterface#fetchPage} method.
     */
    fetchPage<T>(payload: DropdownPopulatorPagePayload): Promise<DropdownPopulatorFetchPageResponse<T>>;
    /**
     * Implementation of the [getItem]{@link DropdownPopulatorInterface#getItem} method.
     * @returns A promise that resolves to the option that was fetched
     */
    getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption>;
    private _buildQueryParams;
}
