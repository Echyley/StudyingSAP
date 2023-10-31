/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import { AlertConfig, AlertService as FundamentalAlertService } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import {
    Alert,
    ALERT_CONFIG_DEFAULTS_TOKEN,
    BaseAlertFactory,
    IAlertConfig,
    IAlertServiceType
} from 'smarteditcommons';

@Injectable()
export class AlertFactory extends BaseAlertFactory {
    constructor(
        fundamentalAlertService: FundamentalAlertService,
        translateService: TranslateService,
        @Inject(ALERT_CONFIG_DEFAULTS_TOKEN) ALERT_CONFIG_DEFAULTS: AlertConfig
    ) {
        super(fundamentalAlertService, translateService, ALERT_CONFIG_DEFAULTS);
    }

    public createAlert(alertConf: string | IAlertConfig): Alert {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        return super.createAlert(alertConf);
    }

    public createInfo(alertConf: string | IAlertConfig): Alert {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = IAlertServiceType.INFO;
        return super.createInfo(alertConf);
    }

    public createDanger(alertConf: string | IAlertConfig): Alert {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = IAlertServiceType.DANGER;
        return super.createDanger(alertConf);
    }

    public createWarning(alertConf: string | IAlertConfig): Alert {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = IAlertServiceType.WARNING;
        return super.createWarning(alertConf);
    }

    public createSuccess(alertConf: string | IAlertConfig): Alert {
        alertConf = this.getAlertConfigFromStringOrConfig(alertConf);
        alertConf.type = IAlertServiceType.SUCCESS;
        return super.createSuccess(alertConf);
    }

    /**
     * Accepts message string or config object
     * Will convert a str param to { message: str }
     */
    public getAlertConfigFromStringOrConfig(strOrConf: string | IAlertConfig): IAlertConfig {
        if (typeof strOrConf === 'string') {
            return {
                message: strOrConf,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            };
        }
        const config = strOrConf;
        return {
            ...config
        };
    }
}
