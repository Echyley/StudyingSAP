/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { IEventService } from '../../interfaces/i-event.service';
import { ISelectItem } from '../../interfaces/i-select-item';
import { IToolingLanguage, LanguageService } from '../../services/language.service';
import { LogService } from '../../services/log.service';
import { LanguageSortStrategy } from './language-dropdown-helper';
export declare class LanguageDropdown {
    languageService: LanguageService;
    eventService: IEventService;
    logService: LogService;
    languageSortStrategy: LanguageSortStrategy;
    selectedLanguage: IToolingLanguage;
    items: ISelectItem<IToolingLanguage>[];
    initialLanguage: ISelectItem<IToolingLanguage>;
    private languages;
    private unRegisterEventService;
    constructor(languageService: LanguageService, eventService: IEventService, logService: LogService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    /**
     * Triggered when an user selects a language.
     * @param {IToolingLanguage} language
     */
    onSelectedLanguage(item: ISelectItem<IToolingLanguage>): void;
    /**
     * Set initial language to be displayed in dropdown
     *
     * @param {string} isoCode
     */
    private setInitialLanguage;
    /**
     * Triggered onInit and when language service sets a new language.
     *
     * @param {IToolingLanguage[]} languages
     * @param {string} isoCode
     */
    private setSelectedLanguage;
    /**
     * Callback for setting the selected language.
     */
    private handleLanguageChange;
}
