/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ISelectItem } from '../../interfaces';
import { IToolingLanguage } from '../../services';
export declare enum LanguageSortStrategy {
    Default = "default",
    None = "none"
}
export interface ILanguageSortConfig {
    strategy: LanguageSortStrategy;
}
export declare class LanguageDropdownHelper {
    static findSelectLanguageWithIsoCodePredicate(isoCode: string): (item: ISelectItem<IToolingLanguage>) => boolean;
    /**
     * Finds the language with a specified isoCode.
     *
     * @param {string} isoCode
     * @param {IToolingLanguage[]} languages
     * @returns {IToolingLanguage}
     */
    static findLanguageWithIsoCode(isoCode: string, languages: IToolingLanguage[]): IToolingLanguage;
    /**
     * Returns an ordered language array by name and sets the selected language at the beginning.
     *
     * @param {IToolingLanguage} selectedLanguage
     * @param {IToolingLanguage[]} languages
     * @param {ILanguageSortConfig} config
     * @returns {IToolingLanguage[]}
     */
    static order(selectedLanguage: IToolingLanguage, languages: IToolingLanguage[], config?: ILanguageSortConfig): IToolingLanguage[];
}
