/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AlertConfig as FundamentalAlertConfig, AlertService as FundamentalAlertService } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { IAlertConfig } from '../../interfaces';
export declare class Alert {
    private _alertConf;
    private fundamentalAlertService;
    private translateService;
    private _displayed;
    private _alertRef;
    constructor(_alertConf: IAlertConfig, ALERT_CONFIG_DEFAULTS: FundamentalAlertConfig, fundamentalAlertService: FundamentalAlertService, translateService: TranslateService);
    get alertConf(): IAlertConfig;
    get message(): string | undefined;
    get type(): string;
    /**
     * Displays the alert to the user.
     */
    show(): Promise<void>;
    /**
     * Hides the alert if it is currently being displayed to the user.
     */
    hide(): void;
    isDisplayed(): boolean;
}
