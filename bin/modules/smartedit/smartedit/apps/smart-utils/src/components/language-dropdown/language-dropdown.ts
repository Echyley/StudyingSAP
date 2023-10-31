/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { Input } from '@angular/core';
import { DEFAULT_LANGUAGE_ISO, SWITCH_LANGUAGE_EVENT } from '../../constants';
import { IEventService } from '../../interfaces/i-event.service';
import { ISelectItem } from '../../interfaces/i-select-item';
import { IToolingLanguage, LanguageService } from '../../services/language.service';
import { LogService } from '../../services/log.service';
import { LanguageDropdownAdapter } from './language-dropdown-adapter';
import { LanguageDropdownHelper, LanguageSortStrategy } from './language-dropdown-helper';

export class LanguageDropdown {
    @Input() languageSortStrategy: LanguageSortStrategy = LanguageSortStrategy.Default;

    public selectedLanguage: IToolingLanguage = null as unknown as IToolingLanguage;
    public items: ISelectItem<IToolingLanguage>[] = [];
    public initialLanguage: ISelectItem<IToolingLanguage> =
        null as unknown as ISelectItem<IToolingLanguage>;

    private languages: IToolingLanguage[] = [];
    private unRegisterEventService: () => void = null as unknown as () => void;

    constructor(
        public languageService: LanguageService,
        public eventService: IEventService,
        public logService: LogService
    ) {}

    ngOnInit(): void {
        Promise.all([
            this.languageService.getResolveLocale(),
            this.languageService.getToolingLanguages()
        ]).then(([isoCode, languages]: [string, IToolingLanguage[]]) => {
            this.items = languages.map(LanguageDropdownAdapter.transform);
            this.languages = languages;

            this.setSelectedLanguage(isoCode);
            this.setInitialLanguage(isoCode);
        });

        this.unRegisterEventService = this.eventService.subscribe(SWITCH_LANGUAGE_EVENT, () =>
            this.handleLanguageChange()
        );
    }

    ngOnDestroy(): void {
        this.unRegisterEventService();
    }

    /**
     * Triggered when an user selects a language.
     * @param {IToolingLanguage} language
     */
    onSelectedLanguage(item: ISelectItem<IToolingLanguage>): void {
        this.languageService.setSelectedToolingLanguage(item.value);
    }

    /**
     * Set initial language to be displayed in dropdown
     *
     * @param {string} isoCode
     */

    private setInitialLanguage(isoCode: string): void {
        this.initialLanguage =
            this.items.find(
                LanguageDropdownHelper.findSelectLanguageWithIsoCodePredicate(isoCode)
            ) ||
            this.items.find(
                LanguageDropdownHelper.findSelectLanguageWithIsoCodePredicate(DEFAULT_LANGUAGE_ISO)
            );
    }
    /**
     * Triggered onInit and when language service sets a new language.
     *
     * @param {IToolingLanguage[]} languages
     * @param {string} isoCode
     */
    private setSelectedLanguage(isoCode: string): void {
        this.selectedLanguage = LanguageDropdownHelper.findLanguageWithIsoCode(
            isoCode,
            this.languages
        );

        if (this.selectedLanguage) {
            const sortedLanguages = LanguageDropdownHelper.order(
                this.selectedLanguage,
                this.languages,
                { strategy: this.languageSortStrategy }
            );

            this.items = sortedLanguages.map(LanguageDropdownAdapter.transform);
            return;
        }

        // In case the iso code is too specific, it will use the more generic iso code to set the language.
        this.languageService
            .getResolveLocaleIsoCode()
            .then((code: string) => {
                this.selectedLanguage = LanguageDropdownHelper.findLanguageWithIsoCode(
                    code,
                    this.languages
                );
                const sortedLanguages = LanguageDropdownHelper.order(
                    this.selectedLanguage,
                    this.languages,
                    { strategy: this.languageSortStrategy }
                );
                this.items = sortedLanguages.map(LanguageDropdownAdapter.transform);
            })
            .catch((error) => {
                this.logService.debug(`LanguageDropdown - can't set the language: ${error}`);
            });
    }

    /**
     * Callback for setting the selected language.
     */
    private handleLanguageChange(): void {
        this.languageService.getResolveLocale().then((isoCode: string) => {
            this.setSelectedLanguage(isoCode);
        });
    }
}
