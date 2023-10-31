/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/// <reference types="jquery" />
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
export declare class LanguageDropdownComponent extends LanguageDropdown {
    eventService: IEventService;
    private readonly yjQuery;
    readonly logService: LogService;
    private readonly CDKOverlayContainer;
    constructor(languageService: LanguageService, eventService: IEventService, yjQuery: JQueryStatic, logService: LogService);
    ngOnInit(): void;
    ngAfterViewChecked(): void;
    ngOnDestroy(): void;
    private removeAllPopovers;
}
