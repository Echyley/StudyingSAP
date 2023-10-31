/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injectable } from '@angular/core';
import { IAlertConfig } from '../../interfaces';
import { AlertFactory } from './alert-factory';

/**
 * @ngdoc service
 * @name @smartutils.services:AlertService
 */
@Injectable()
export class AlertService {
    constructor(private alertFactory: AlertFactory) {}

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showAlert
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    public showAlert(alertConf: string | IAlertConfig): void {
        const alert = this.alertFactory.createAlert(alertConf);
        alert.show();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showInfo
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    public showInfo(alertConf: string | IAlertConfig): void {
        const alert = this.alertFactory.createInfo(alertConf);
        alert.show();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showDanger
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    public showDanger(alertConf: string | IAlertConfig): void {
        const alert = this.alertFactory.createDanger(alertConf);
        alert.show();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showWarning
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    public showWarning(alertConf: string | IAlertConfig): void {
        const alert = this.alertFactory.createWarning(alertConf);
        alert.show();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertService#showSuccess
     * @methodOf @smartutils.services:AlertService
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @description
     * Displays an alert to the user. <br />
     * Convenience method to create an alert and call.show() on it immediately.
     */
    public showSuccess(alertConf: string | IAlertConfig): void {
        const alert = this.alertFactory.createSuccess(alertConf);
        alert.show();
    }
}
