import { AlertConfig, AlertService as FundamentalAlertService } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { IAlertConfig } from '../../interfaces';
import { Alert } from './alert';
/**
 * @ngdoc service
 * @name @smartutils.services:AlertFactory
 *
 * @description
 * The alertFactory allows you to create an instances of type Alert.<br />
 * When possible, it is better to use {@link @smartutils.services:AlertService AlertService} to show alerts.<br />
 * This factory is useful when one of the Alert class methods is needed, like
 * hide() or isDisplayed(), or if you want to create a single instance and hide/show when necessary.
 */
export declare class AlertFactory {
    private fundamentalAlertService;
    private translateService;
    private ALERT_CONFIG_DEFAULTS;
    constructor(fundamentalAlertService: FundamentalAlertService, translateService: TranslateService, ALERT_CONFIG_DEFAULTS: AlertConfig);
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createAlert
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance
     */
    createAlert(alertConf: string | IAlertConfig): Alert;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createInfo
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to INFO
     */
    createInfo(alertConf: string | IAlertConfig): Alert;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createDanger
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to DANGER
     */
    createDanger(alertConf: string | IAlertConfig): Alert;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createWarning
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to WARNING
     */
    createWarning(alertConf: string | IAlertConfig): Alert;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createSuccess
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to SUCCESS
     */
    createSuccess(alertConf: string | IAlertConfig): Alert;
    private getAlertConfig;
    private createAlertObject;
}
