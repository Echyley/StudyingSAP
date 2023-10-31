/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './HeaderLanguageDropdownComponent.scss';

import { Component } from '@angular/core';
import { LanguageDropdown, LogService } from '@smart/utils';

import { CrossFrameEventService } from '../../services/crossFrame/CrossFrameEventService';
import { LanguageService } from '../../services/language/LanguageService';

@Component({
    selector: 'header-language-dropdown',
    template: `
        <ul role="menu" class="fd-menu__list se-language-selector">
            <li *ngIf="selectedLanguage" class="fd-menu__item">
                <a
                    class="yToolbarActions__dropdown-element se-language-selector__element--selected fd-menu__link">
                    <span class="fd-menu__title"> {{ selectedLanguage.name }} </span>
                </a>
            </li>
            <ng-container *ngFor="let language of items">
                <li
                    *ngIf="selectedLanguage.isoCode !== language.value.isoCode"
                    class="fd-menu__item">
                    <a
                        class="yToolbarActions__dropdown-element se-language-selector__element fd-menu__link"
                        (click)="onSelectedLanguage(language)">
                        <span class="fd-menu__title"> {{ language.value.name }} </span>
                    </a>
                </li>
            </ng-container>
        </ul>
    `
})
export class HeaderLanguageDropdownComponent extends LanguageDropdown {
    constructor(
        public languageService: LanguageService,
        public crossFrameEventService: CrossFrameEventService,
        public logService: LogService
    ) {
        super(languageService, crossFrameEventService, logService);

        this.languageSortStrategy = 'none' as any;
    }
}
