import { AlertConfig, AlertService as FundamentalAlertService } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { Alert, BaseAlertFactory, IAlertConfig } from 'smarteditcommons';
export declare class AlertFactory extends BaseAlertFactory {
    constructor(fundamentalAlertService: FundamentalAlertService, translateService: TranslateService, ALERT_CONFIG_DEFAULTS: AlertConfig);
    createAlert(alertConf: string | IAlertConfig): Alert;
    createInfo(alertConf: string | IAlertConfig): Alert;
    createDanger(alertConf: string | IAlertConfig): Alert;
    createWarning(alertConf: string | IAlertConfig): Alert;
    createSuccess(alertConf: string | IAlertConfig): Alert;
    /**
     * Accepts message string or config object
     * Will convert a str param to { message: str }
     */
    getAlertConfigFromStringOrConfig(strOrConf: string | IAlertConfig): IAlertConfig;
}
