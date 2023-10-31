import { OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SettingsService, IUserTrackingService } from 'smarteditcommons';
import './cx-qtx-survey-button.bundle.js';
export declare class QualtricsComponent implements OnInit, OnDestroy {
    private translateService;
    private readonly settingService;
    private readonly userTrackingService;
    interceptUrl: string;
    contextParamsString: string;
    private contextParams;
    constructor(translateService: TranslateService, settingService: SettingsService, userTrackingService: IUserTrackingService);
    ngOnDestroy(): void;
    ngOnInit(): void;
    onClick(): void;
    private _getFixedContextParams;
}
