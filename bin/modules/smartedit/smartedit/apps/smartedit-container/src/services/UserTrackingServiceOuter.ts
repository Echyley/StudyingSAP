/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    ISettingsService,
    LogService,
    GatewayProxied,
    WindowUtils,
    IUserTrackingService,
    USER_TRACKING_KEY_MAP
} from 'smarteditcommons';

/** @internal */
@GatewayProxied('initConfiguration', 'trackingUserAction')
@Injectable()
export class UserTrackingService extends IUserTrackingService {
    readonly CUSTOMER_KEY = 'modelt.customer.code';
    readonly PROJECT_KEY = 'modelt.project.code';
    readonly ENVIRONMENT_KEY = 'modelt.environment.code';
    readonly TRACKING_URL_KEY = 'smartedit.click.tracking.server.url';
    readonly DEFAULT_CUSTOMER = 'localCustomer';
    readonly DEFAULT_PROJECT = 'localProject';
    readonly DEFAULT_ENVIRONMENT = 'localEnvironment';
    readonly DEFAULT_TRACKING_URL = 'https://license.hybris.com/collect';
    isInitialized = false;

    private _paq: any;
    constructor(
        private readonly windowUtils: WindowUtils,
        private readonly settingService: ISettingsService,
        private readonly logService: LogService
    ) {
        super();
    }

    async initConfiguration(): Promise<void> {
        const isTrackingEnabled = await this.settingService.getBoolean(
            'smartedit.default.click.tracking.enabled'
        );

        if (!isTrackingEnabled) {
            this.isInitialized = false;
            return;
        }

        if (this.isEnvInitialized()) {
            const siteId = await this.getSiteId();
            let trackingUrl = await this.settingService.get(this.TRACKING_URL_KEY);
            trackingUrl = trackingUrl !== undefined ? trackingUrl : this.DEFAULT_TRACKING_URL;

            this._paq.push(['setTrackerUrl', trackingUrl]);
            this._paq.push(['setSiteId', siteId]);
            this.isInitialized = true;
        } else {
            this.isInitialized = false;
            this.logService.warn('User tracking is enabled and tracking tool is not initialized.');
        }
    }

    trackingUserAction(functionality: string, key: string): void {
        if (this.isInitialized) {
            if (USER_TRACKING_KEY_MAP.has(key)) {
                key = USER_TRACKING_KEY_MAP.get(key);
            }

            this._paq.push(['trackEvent', 'SmartEdit', functionality, key]);
        }
    }

    // Check if tracking library piwik is loaded
    private isEnvInitialized(): boolean {
        this._paq = this.windowUtils.getWindow()._paq;
        return this._paq === undefined ? false : this._paq.push !== undefined;
    }

    private async getSiteId(): Promise<string> {
        let customerCode = await this.settingService.get(this.CUSTOMER_KEY);
        customerCode = customerCode !== undefined ? customerCode : this.DEFAULT_CUSTOMER;
        let projectCode = await this.settingService.get(this.PROJECT_KEY);
        projectCode = projectCode !== undefined ? projectCode : this.DEFAULT_PROJECT;
        let environmentCode = await this.settingService.get(this.ENVIRONMENT_KEY);
        environmentCode =
            environmentCode !== undefined ? environmentCode : this.DEFAULT_ENVIRONMENT;
        return `${customerCode}-${projectCode}-${environmentCode}`;
    }
}
