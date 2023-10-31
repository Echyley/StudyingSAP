/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Inject, Injectable } from '@angular/core';
import { AlertConfig, AlertService as FundamentalAlertService } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { IAlertConfig, IAlertServiceType } from '../../interfaces';
import { Alert } from './alert';
import { ALERT_CONFIG_DEFAULTS_TOKEN } from './alert-token';

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
@Injectable()
export class AlertFactory {
    constructor(
        private fundamentalAlertService: FundamentalAlertService,
        private translateService: TranslateService,
        @Inject(ALERT_CONFIG_DEFAULTS_TOKEN) private ALERT_CONFIG_DEFAULTS: AlertConfig
    ) {}

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createAlert
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance
     */
    public createAlert(alertConf: string | IAlertConfig): Alert {
        const config = this.getAlertConfig(alertConf);
        return this.createAlertObject(config);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createInfo
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to INFO
     */
    public createInfo(alertConf: string | IAlertConfig): Alert {
        const config = this.getAlertConfig(alertConf, IAlertServiceType.INFO);
        return this.createAlertObject(config);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createDanger
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to DANGER
     */
    public createDanger(alertConf: string | IAlertConfig): Alert {
        const config = this.getAlertConfig(alertConf, IAlertServiceType.DANGER);
        return this.createAlertObject(config);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createWarning
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to WARNING
     */
    public createWarning(alertConf: string | IAlertConfig): Alert {
        const config = this.getAlertConfig(alertConf, IAlertServiceType.WARNING);
        return this.createAlertObject(config);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AlertFactory#createSuccess
     * @methodOf @smartutils.services:AlertFactory
     * @param {string | Object} alertConf The alert's configuration {@link @smartutils.interfaces:IAlertConfig IAlertConfig}
     * @returns {Alert} An {@link Alert Alert} instance with type set to SUCCESS
     */
    public createSuccess(alertConf: string | IAlertConfig): Alert {
        const config = this.getAlertConfig(alertConf, IAlertServiceType.SUCCESS);
        return this.createAlertObject(config);
    }

    private getAlertConfig(
        strOrConf: string | IAlertConfig,
        type?: IAlertServiceType
    ): IAlertConfig {
        if (typeof strOrConf === 'string') {
            return {
                message: strOrConf,
                type: type || IAlertServiceType.INFO,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            };
        }

        if (!strOrConf.type) {
            strOrConf.type = type || IAlertServiceType.INFO;
        }
        return strOrConf;
    }

    private createAlertObject(alertConf: IAlertConfig): Alert {
        return new Alert(
            alertConf,
            this.ALERT_CONFIG_DEFAULTS,
            this.fundamentalAlertService,
            this.translateService
        );
    }
}
