/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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

export enum IAlertServiceType {
    INFO = 'information',
    SUCCESS = 'success',
    WARNING = 'warning',
    DANGER = 'error'
}

export abstract class IAlertService {
    showAlert(alertConf: IAlertConfig | string): void {
        'proxyFunction';
        return;
    }

    showInfo(alertConf: IAlertConfig | string): void {
        'proxyFunction';
        return;
    }

    showDanger(alertConf: IAlertConfig | string): void {
        'proxyFunction';
        return;
    }

    showWarning(alertConf: IAlertConfig | string): void {
        'proxyFunction';
        return;
    }

    showSuccess(alertConf: IAlertConfig | string): void {
        'proxyFunction';
        return;
    }
}
