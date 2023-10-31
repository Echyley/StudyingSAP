import { TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../../../../../services/language/LanguageService';
import { GenericEditorOption } from '../../../../types';
import { DropdownPopulatorInterface } from '../DropdownPopulatorInterface';
import { DropdownPopulatorPayload } from '../types';
/**
 * Implementation of {@link DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
 * containing options attribute.
 */
export declare class OptionsDropdownPopulator extends DropdownPopulatorInterface {
    languageService: LanguageService;
    translateService: TranslateService;
    constructor(languageService: LanguageService, translateService: TranslateService);
    /**
     * Implementation of the [fetchAll]{@link DropdownPopulatorInterface#fetchAll} method.
     */
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
}
