/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { Component, Inject } from '@angular/core';
import { EVENT_SERVICE, LANGUAGE_SERVICE, YJQUERY_TOKEN } from '../../constants';
import { IEventService } from '../../interfaces';
import { LanguageService } from '../../services/language.service';
import { LogService } from '../../services/log.service';
import { LanguageDropdown } from './language-dropdown';

/**
 * @ngdoc component
 * @name  @smartutils.components:LanguageDropdownComponent
 *
 * @description
 * A component responsible for displaying and selecting application language. Uses {@link @smartutils.components:SelectComponent SelectComponent} to show language items
 *
 */

@Component({
    selector: 'su-language-dropdown',
    template: `
        <su-select
            class="su-language-selector"
            [items]="items"
            [initialValue]="initialLanguage"
            (onItemSelected)="onSelectedLanguage($event)">
        </su-select>
    `
})
export class LanguageDropdownComponent extends LanguageDropdown {
    private readonly CDKOverlayContainer = '.cdk-overlay-container';
    constructor(
        @Inject(LANGUAGE_SERVICE) languageService: LanguageService,
        @Inject(EVENT_SERVICE) public eventService: IEventService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        public readonly logService: LogService
    ) {
        super(languageService, eventService, logService);
    }

    ngOnInit(): void {
        super.ngOnInit();

        // workaround, to make popover z-index higher than dialog, so that the language dropdown can be selected
        this.removeAllPopovers();
    }

    ngAfterViewChecked(): void {
        // workaround, to make popover z-index higher than dialog, so that the language dropdown can be selected
        this.yjQuery(this.CDKOverlayContainer).addClass('cdk-overlay-container-language');
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.yjQuery(this.CDKOverlayContainer).removeClass('cdk-overlay-container-language');
    }

    private removeAllPopovers(): void {
        const parentCDK = this.yjQuery(this.CDKOverlayContainer).toArray();
        if (parentCDK.length === 0) {
            return;
        }

        this.yjQuery('.cdk-overlay-connected-position-bounding-box')
            .toArray()
            .forEach((child) => {
                parentCDK[0].removeChild(child);
            });
    }
}
