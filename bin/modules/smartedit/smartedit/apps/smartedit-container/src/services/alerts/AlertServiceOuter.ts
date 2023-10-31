/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { BaseAlertService, GatewayProxied, IAlertConfig, IAlertService } from 'smarteditcommons';
import { AlertFactory } from './AlertFactory';

@GatewayProxied()
@Injectable()
export class AlertService extends BaseAlertService implements IAlertService {
    constructor(private _alertFactory: AlertFactory) {
        super(_alertFactory);
    }

    public showAlert(alertConf: string | IAlertConfig): void {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showAlert(alertConf);
    }

    public showInfo(alertConf: string | IAlertConfig): void {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showInfo(alertConf);
    }

    public showDanger(alertConf: string | IAlertConfig): void {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showDanger(alertConf);
    }

    public showWarning(alertConf: string | IAlertConfig): void {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showWarning(alertConf);
    }

    public showSuccess(alertConf: string | IAlertConfig): void {
        alertConf = this._alertFactory.getAlertConfigFromStringOrConfig(alertConf);
        super.showSuccess(alertConf);
    }
}
