import { SafeResourceUrl } from '@angular/platform-browser';
import { IAlertService, RestServiceFactory, CrossFrameEventService, ThemesService } from 'smarteditcommons';
export declare class ThemeSwitchService {
    private readonly alertService;
    private readonly _themesService;
    private readonly restServiceFactory;
    private readonly crossFrameEventService;
    static readonly lightTheme = "sap_fiori_3";
    static cssStringUrl: string;
    static cssCustomStringUrl: string;
    selectedTheme: string;
    cssUrl: SafeResourceUrl;
    cssCustomUrl: SafeResourceUrl;
    private readonly themeRestService;
    private readonly sessionStorage;
    constructor(alertService: IAlertService, _themesService: ThemesService, restServiceFactory: RestServiceFactory, crossFrameEventService: CrossFrameEventService);
    getCurrentUserTheme(): Promise<string>;
    selectTheme(selectedTheme: string): Promise<void>;
    setThemeSession(): void;
    saveTheme(): Promise<void>;
    getThemeSession(): string;
    private generateAndAlertErrorMessage;
}
