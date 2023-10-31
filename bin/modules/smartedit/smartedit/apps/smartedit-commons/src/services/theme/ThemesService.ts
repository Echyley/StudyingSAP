/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable, isDevMode, Optional } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { IRestService } from '@smart/utils';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { Theme } from '../../dtos/themes';
import { ALL_ACTIVE_THEMES_URI } from '../../utils/smarteditconstants';
import { IRestServiceFactory } from '../interfaces';
import { LanguageService } from '../language/LanguageService';

import { THEME_SWITCHER_ROUTER_MISSING_ERROR } from './constants';

export interface ThemeServiceOutput {
    themeUrl: SafeResourceUrl;
    customThemeUrl: SafeResourceUrl;
}

@Injectable()
/**
 * @deprecated
 * Service providing theme switcher functionality.
 * Deprecated since 0.35.0 in favor of ThemingService from ThemingModule
 */
export class ThemesService {
    /** Subject triggered, when the theming is changed by URL parameter */
    private readonly onThemeQueryParamChange: Subject<ThemeServiceOutput> =
        new Subject<ThemeServiceOutput>();
    private themes: Theme[];
    private themeRestService: IRestService<Theme>;

    /** @hidden **/
    private readonly _onDestroy$: Subject<void> = new Subject<void>();

    constructor(
        @Optional() private readonly _activatedRoute: ActivatedRoute,
        private readonly _sanitizer: DomSanitizer,
        private readonly restServiceFactory: IRestServiceFactory,
        private readonly languageService: LanguageService
    ) {
        this.initThemes();
    }

    /**
     * get All themes by current language
     */

    async initThemes(): Promise<void> {
        const locale = await this.languageService.getResolveLocale();
        this.themeRestService = this.restServiceFactory.get(
            `${ALL_ACTIVE_THEMES_URI}?langIsoCode=${locale}`
        );
        this.themes = (await this.themeRestService.get()) as any;
    }

    getThemes(): Theme[] {
        return this.themes;
    }

    /**
     * Set theme according to additional URL parameter.
     * This parameter can be changed in function argument.
     * By default it's `theme`.
     **/
    setThemeByRoute(themeParamName?: string): void {
        const paramName = themeParamName || 'theme';

        if (!this._activatedRoute) {
            throw new Error(THEME_SWITCHER_ROUTER_MISSING_ERROR);
        }

        this._activatedRoute.queryParams
            .pipe(
                takeUntil(this._onDestroy$),
                filter((param) => param && param[paramName])
            )
            .subscribe((param) => this.propagateThemes(param[paramName]));

        const nativeTheme = this.getNativeParameterByName(paramName);
        if (nativeTheme) {
            this.propagateThemes(nativeTheme);
        }
    }

    /** Method to get once theme object directly from url. */
    getThemesFromURL(param?: string): ThemeServiceOutput | undefined {
        const paramName = param || 'theme';

        const nativeTheme = this.getNativeParameterByName(paramName);
        if (!nativeTheme || isDevMode()) {
            return;
        }

        return {
            themeUrl: this.setTheme(nativeTheme),
            customThemeUrl: this.setCustomTheme(nativeTheme)
        };
    }

    /** Assign css file corresponding to chosen theme from @sap-theming **/
    setTheme(theme: string): SafeResourceUrl {
        return this._sanitizer.bypassSecurityTrustResourceUrl(
            `static-resources/dist/smartedit-container-new/${theme}/css_variables.css`
        );
    }

    /** Assign css file corresponding to chosen theme fundamental-styles **/
    setCustomTheme(theme: string): SafeResourceUrl {
        return this._sanitizer.bypassSecurityTrustResourceUrl(
            `static-resources/dist/smartedit-container-new/${theme}/${theme}.css`
        );
    }

    /** @hidden */
    private getNativeParameterByName(paramName: string): string {
        paramName = paramName.replace(/[[\]]/g, '\\$&');
        const regex = new RegExp(`[?&]${paramName}(=([^&#]*)|&|#|$)`);
        const index = 2;
        const results = regex.exec(window.location.href);
        if (!results || !results[index]) {
            return '';
        }
        return decodeURIComponent(results[index].replace(/\+/g, ' '));
    }

    /** @hidden */
    private propagateThemes(theme: string): void {
        this.onThemeQueryParamChange.next({
            themeUrl: this.setTheme(theme),
            customThemeUrl: this.setCustomTheme(theme)
        });
    }
}
