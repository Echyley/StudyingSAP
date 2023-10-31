import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { Theme } from '../../dtos/themes';
import { IRestServiceFactory } from '../interfaces';
import { LanguageService } from '../language/LanguageService';
export interface ThemeServiceOutput {
    themeUrl: SafeResourceUrl;
    customThemeUrl: SafeResourceUrl;
}
export declare class ThemesService {
    private readonly _activatedRoute;
    private readonly _sanitizer;
    private readonly restServiceFactory;
    private readonly languageService;
    /** Subject triggered, when the theming is changed by URL parameter */
    private readonly onThemeQueryParamChange;
    private themes;
    private themeRestService;
    /** @hidden **/
    private readonly _onDestroy$;
    constructor(_activatedRoute: ActivatedRoute, _sanitizer: DomSanitizer, restServiceFactory: IRestServiceFactory, languageService: LanguageService);
    /**
     * get All themes by current language
     */
    initThemes(): Promise<void>;
    getThemes(): Theme[];
    /**
     * Set theme according to additional URL parameter.
     * This parameter can be changed in function argument.
     * By default it's `theme`.
     **/
    setThemeByRoute(themeParamName?: string): void;
    /** Method to get once theme object directly from url. */
    getThemesFromURL(param?: string): ThemeServiceOutput | undefined;
    /** Assign css file corresponding to chosen theme from @sap-theming **/
    setTheme(theme: string): SafeResourceUrl;
    /** Assign css file corresponding to chosen theme fundamental-styles **/
    setCustomTheme(theme: string): SafeResourceUrl;
    /** @hidden */
    private getNativeParameterByName;
    /** @hidden */
    private propagateThemes;
}
