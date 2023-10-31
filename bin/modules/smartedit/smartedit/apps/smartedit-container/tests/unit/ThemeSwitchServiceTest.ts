/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import {
    ThemesService,
    IAlertService,
    RestServiceFactory,
    IRestService,
    CrossFrameEventService
} from 'smarteditcommons';
import { ThemeSwitchService } from 'smarteditcontainer/services/theme/ThemeSwitchService';

describe('ThemeSwitchService', () => {
    let themeSwitchService: ThemeSwitchService;
    let themesService: jasmine.SpyObj<ThemesService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<
        RestServiceFactory
    >('restServiceFactory', ['get']);
    const themeRestService: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<
        IRestService<any>
    >('themeRestService', ['get']);
    beforeEach(() => {
        themesService = jasmine.createSpyObj('themesService', ['setTheme', 'setCustomTheme']);
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showDanger']);
        themeRestService.get.and.returnValue(Promise.resolve('sap_fiori_3'));
        restServiceFactory.get.and.callFake((uri: string, identifier: string) => {
            return themeRestService;
        });
        themesService.setTheme.and.returnValue({
            changingThisBreaksApplicationSecurity: '123'
        });
        themesService.setCustomTheme.and.returnValue({
            changingThisBreaksApplicationSecurity: '456'
        });
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', [
            'subscribe',
            'publish'
        ]);
        themeSwitchService = new ThemeSwitchService(
            alertService,
            themesService,
            restServiceFactory,
            crossFrameEventService
        );
        spyOn(themeSwitchService, 'setThemeSession').and.returnValue();
    });
    it('When the user login the smartedit without a theme on the backend for the first time, request the theme from backend', async () => {
        await themeSwitchService.selectTheme(null);
        expect(themeSwitchService.setThemeSession).toHaveBeenCalled();
        expect(themesService.setTheme).toHaveBeenCalled();
        expect(themesService.setCustomTheme).toHaveBeenCalled();
    });

    it('GIVEN the fiori light theme is selected, remove the appended links on container and inner', async () => {
        await themeSwitchService.selectTheme('sap_fiori_3');
        expect(themeSwitchService.setThemeSession).not.toHaveBeenCalled();
        expect(themeSwitchService.selectedTheme).toBe('sap_fiori_3');
        expect(themesService.setTheme).toHaveBeenCalled();
        expect(themesService.setCustomTheme).toHaveBeenCalled();
    });

    it('GIVEN the fiori dark theme is selected, remove the original appended links and add new links about theme both on container and inner', () => {
        themeSwitchService.selectTheme('sap_fiori_3_dark');
        expect(themeSwitchService.setThemeSession).not.toHaveBeenCalled();
        expect(themeSwitchService.selectedTheme).toBe('sap_fiori_3_dark');
        expect(themesService.setTheme).toHaveBeenCalled();
        expect(themesService.setCustomTheme).toHaveBeenCalled();
    });

    it('When horizon theme is selected, remove the original appended links and add new links about theme both on container and inner', () => {
        themeSwitchService.selectTheme('sap_horizon');
        expect(themeSwitchService.setThemeSession).not.toHaveBeenCalled();
        expect(themeSwitchService.selectedTheme).toBe('sap_horizon');
        expect(themesService.setTheme).toHaveBeenCalled();
        expect(themesService.setCustomTheme).toHaveBeenCalled();
    });
});
