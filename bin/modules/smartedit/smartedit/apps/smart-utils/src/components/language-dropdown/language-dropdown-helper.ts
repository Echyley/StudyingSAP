/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { ISelectItem } from '../../interfaces';
import { IToolingLanguage } from '../../services';

export enum LanguageSortStrategy {
    Default = 'default',
    None = 'none'
}
export interface ILanguageSortConfig {
    strategy: LanguageSortStrategy;
}

// @dynamic
export class LanguageDropdownHelper {
    static findSelectLanguageWithIsoCodePredicate(
        isoCode: string
    ): (item: ISelectItem<IToolingLanguage>) => boolean {
        return (item: ISelectItem<IToolingLanguage>): boolean => item.value.isoCode === isoCode;
    }

    /**
     * Finds the language with a specified isoCode.
     *
     * @param {string} isoCode
     * @param {IToolingLanguage[]} languages
     * @returns {IToolingLanguage}
     */
    static findLanguageWithIsoCode(
        isoCode: string,
        languages: IToolingLanguage[]
    ): IToolingLanguage {
        return languages.find((language) => language.isoCode === isoCode);
    }

    /**
     * Returns an ordered language array by name and sets the selected language at the beginning.
     *
     * @param {IToolingLanguage} selectedLanguage
     * @param {IToolingLanguage[]} languages
     * @param {ILanguageSortConfig} config
     * @returns {IToolingLanguage[]}
     */

    static order(
        selectedLanguage: IToolingLanguage,
        languages: IToolingLanguage[],
        config: ILanguageSortConfig = { strategy: LanguageSortStrategy.Default }
    ): IToolingLanguage[] {
        switch (config.strategy) {
            case LanguageSortStrategy.Default:
                const orderedLanguages = languages
                    .filter((language) => language.isoCode !== selectedLanguage.isoCode)
                    .sort((a, b) => a.isoCode.localeCompare(b.isoCode));

                return [selectedLanguage, ...orderedLanguages];

            case LanguageSortStrategy.None:
            default:
                return languages;
        }
    }
}
