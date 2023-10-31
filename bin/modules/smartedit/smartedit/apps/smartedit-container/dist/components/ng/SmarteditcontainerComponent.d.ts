import { ElementRef, Injector } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { CrossFrameEventService } from 'smarteditcommons';
import { ThemeSwitchService } from 'smarteditcontainer/services/theme/ThemeSwitchService';
export declare class SmarteditcontainerComponent {
    private translateService;
    private elementRef;
    private readonly themeSwitchService;
    private readonly crossFrameEventService;
    legacyAppName: string;
    cssUrl: SafeResourceUrl;
    cssCustomUrl: SafeResourceUrl;
    defaultTheme: boolean;
    constructor(translateService: TranslateService, injector: Injector, elementRef: ElementRef, themeSwitchService: ThemeSwitchService, crossFrameEventService: CrossFrameEventService);
    ngOnInit(): Promise<void>;
    private setApplicationTitle;
    private get legacyAppNode();
    private isAppComponent;
    private isSmarteditLoader;
    private getThemeCss;
}
