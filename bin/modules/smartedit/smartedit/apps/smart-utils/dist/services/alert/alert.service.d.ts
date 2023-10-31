import { IAlertConfig } from '../../interfaces';
import { AlertFactory } from './alert-factory';
/**
 * @ngdoc service
 * @name @smartutils.services:AlertService
 */
export declare class AlertService {
    private alertFactory;
    constructor(alertFactory: AlertFactory);
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showAlert
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showAlert(alertConf: string | IAlertConfig): void;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showInfo
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showInfo(alertConf: string | IAlertConfig): void;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showDanger
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showDanger(alertConf: string | IAlertConfig): void;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showWarning
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showWarning(alertConf: string | IAlertConfig): void;
    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showSuccess
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    showSuccess(alertConf: string | IAlertConfig): void;
}
