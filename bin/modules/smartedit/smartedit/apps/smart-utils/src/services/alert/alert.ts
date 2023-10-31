/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    AlertConfig as FundamentalAlertConfig,
    AlertRef,
    AlertService as FundamentalAlertService
} from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import { IAlertConfig } from '../../interfaces';

export class Alert {
    private _displayed = false;
    private _alertRef!: AlertRef;

    constructor(
        private _alertConf: IAlertConfig,
        ALERT_CONFIG_DEFAULTS: FundamentalAlertConfig,
        private fundamentalAlertService: FundamentalAlertService,
        private translateService: TranslateService
    ) {
        lodash.defaultsDeep(this._alertConf, lodash.cloneDeep(ALERT_CONFIG_DEFAULTS));
    }

    get alertConf(): IAlertConfig {
        return this._alertConf;
    }

    get message(): string | undefined {
        return this._alertConf.message;
    }

    get type(): string {
        return this._alertConf.type;
    }

    /**
     * Displays the alert to the user.
     */
    public async show(): Promise<void> {
        if (this.isDisplayed()) {
            return;
        }

        if (this._alertConf.message) {
            this._alertConf.message = await this.translateService
                .get(this._alertConf.message, this._alertConf.messagePlaceholders)
                .toPromise<string>();
        }

        const content =
            typeof this._alertConf.message !== 'undefined'
                ? this._alertConf.message
                : this._alertConf.component || '';
        this._alertRef = this.fundamentalAlertService.open(content, this._alertConf);
        this._alertRef.afterDismissed.subscribe(() => (this._displayed = false));

        this._displayed = true;
    }

    /**
     * Hides the alert if it is currently being displayed to the user.
     */
    public hide(): void {
        if (!this.isDisplayed()) {
            return;
        }

        this._alertRef.dismiss();
    }

    public isDisplayed(): boolean {
        return this._displayed;
    }
}
