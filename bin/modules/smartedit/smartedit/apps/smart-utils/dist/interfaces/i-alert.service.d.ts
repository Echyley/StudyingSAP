/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Type } from '@angular/core';
import { AlertConfig as FundamentalAlertConfig } from '@fundamental-ngx/core';
import { TypedMap } from '../dtos';
export interface IAlertConfig extends FundamentalAlertConfig {
    component?: Type<any>;
    message?: string;
    messagePlaceholders?: TypedMap<string>;
}
export declare enum IAlertServiceType {
    INFO = "information",
    SUCCESS = "success",
    WARNING = "warning",
    DANGER = "error"
}
export declare abstract class IAlertService {
    showAlert(alertConf: IAlertConfig | string): void;
    showInfo(alertConf: IAlertConfig | string): void;
    showDanger(alertConf: IAlertConfig | string): void;
    showWarning(alertConf: IAlertConfig | string): void;
    showSuccess(alertConf: IAlertConfig | string): void;
}
