/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import {
    IRestService,
    IAlertService,
    RestServiceFactory,
    CrossFrameEventService,
    CURRENT_USER_THEME_URI,
    ThemesService,
    Theme,
    EVENTS,
    EVENT_THEME_CHANGED,
    EVENT_THEME_SAVED
} from 'smarteditcommons';

/* @internal */
@Injectable()
export class ThemeSwitchService {
    static readonly lightTheme = 'sap_fiori_3';
    static cssStringUrl: string;
    static cssCustomStringUrl: string;
    public selectedTheme: string;
    public cssUrl: SafeResourceUrl;
    public cssCustomUrl: SafeResourceUrl;
    private readonly themeRestService: IRestService<Theme>;
    private readonly sessionStorage: Storage = window.sessionStorage;
    constructor(
        private readonly alertService: IAlertService,
        private readonly _themesService: ThemesService,
        private readonly restServiceFactory: RestServiceFactory,
        private readonly crossFrameEventService: CrossFrameEventService
    ) {
        this.themeRestService = this.restServiceFactory.get(CURRENT_USER_THEME_URI);
        this.crossFrameEventService.subscribe(EVENTS.REAUTH_STARTED, () => {
            this.selectedTheme = ThemeSwitchService.lightTheme;
            this.crossFrameEventService.publish(EVENT_THEME_CHANGED);
        });
        this.crossFrameEventService.subscribe(EVENTS.LOGOUT, () => {
            this.selectedTheme = ThemeSwitchService.lightTheme;
            this.crossFrameEventService.publish(EVENT_THEME_CHANGED);
        });
    }

    public async getCurrentUserTheme(): Promise<string> {
        return (await this.themeRestService.get()).code;
    }

    public async selectTheme(selectedTheme: string): Promise<void> {
        if (!selectedTheme) {
            this.selectedTheme = await this.getCurrentUserTheme();
            this.setThemeSession();
        }
        this.crossFrameEventService.publish(EVENT_THEME_CHANGED);
        selectedTheme = selectedTheme ? selectedTheme : this.getThemeSession();
        this.selectedTheme = selectedTheme;
        this.cssUrl = this._themesService.setTheme(selectedTheme);
        this.cssCustomUrl = this._themesService.setCustomTheme(selectedTheme);
        ThemeSwitchService.cssStringUrl = `/${
            (this.cssUrl as any).changingThisBreaksApplicationSecurity
        }`;
        ThemeSwitchService.cssCustomStringUrl = `/${
            (this.cssCustomUrl as any).changingThisBreaksApplicationSecurity
        }`;
    }

    public setThemeSession(): void {
        if (this.selectedTheme) {
            this.sessionStorage.setItem('theme', this.selectedTheme);
        }
    }

    public async saveTheme(): Promise<void> {
        try {
            await this.restServiceFactory.get(CURRENT_USER_THEME_URI, 'code').update({
                code: this.selectedTheme
            });
            this.setThemeSession();
            this.crossFrameEventService.publish(EVENT_THEME_SAVED);
            return Promise.resolve();
        } catch (error) {
            this.selectTheme(this.getThemeSession());
            this.generateAndAlertErrorMessage();
            return Promise.reject();
        }
    }

    public getThemeSession(): string {
        if (
            this.sessionStorage.getItem('theme') &&
            this.sessionStorage.getItem('theme') !== 'undefined'
        ) {
            return this.sessionStorage.getItem('theme');
        } else {
            return null;
        }
    }

    private generateAndAlertErrorMessage(): void {
        this.alertService.showDanger({
            message: 'se.cms.theme.save.error',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }
}
