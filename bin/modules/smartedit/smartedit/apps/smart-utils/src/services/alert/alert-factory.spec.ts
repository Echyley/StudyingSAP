/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { Component, Type } from '@angular/core';
import {
    AlertConfig,
    AlertRef,
    AlertService as FundamentalAlertService
} from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { IAlertServiceType } from '../../interfaces';
import { AlertFactory } from './alert-factory';
import { ALERT_CONFIG_DEFAULTS } from './alert-token';

@Component({
    selector: 'mock-alert-template',
    template: ''
})
class MockAlertTemplateComponent {}
describe('alertFactory', () => {
    const ALERT_MSG = 'alert message';
    let alertFactory: AlertFactory;
    let fundamentalAlertService: jasmine.SpyObj<FundamentalAlertService>;
    const translateService: jasmine.SpyObj<TranslateService> =
        jasmine.createSpyObj<TranslateService>('translateService', ['get']);
    translateService.get.and.callFake((content: any) => of(`${content}_translated`));

    const MOCK_ALERT_CONFIG_DEFAULTS: AlertConfig = { ...ALERT_CONFIG_DEFAULTS };

    beforeEach(() => {
        jasmine.clock().uninstall();
        jasmine.clock().install();

        fundamentalAlertService = jasmine.createSpyObj<FundamentalAlertService>(
            'fundamentalAlertService',
            ['open']
        );
        fundamentalAlertService.open.and.callFake((content: Type<any>, config: AlertConfig) => {
            const alertRef = new AlertRef();
            const timeoutId = setTimeout(() => alertRef.dismiss(), config.duration);
            alertRef.afterDismissed.subscribe(() => {
                clearTimeout(timeoutId);
            });
            return alertRef;
        });

        alertFactory = new AlertFactory(
            fundamentalAlertService,
            translateService,
            MOCK_ALERT_CONFIG_DEFAULTS
        );
    });

    afterEach(() => {
        jasmine.clock().uninstall();
    });

    it('Opens Alert with Component', () => {
        const alert = alertFactory.createAlert({
            component: MockAlertTemplateComponent,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        alert.show();
        const args = fundamentalAlertService.open.calls.mostRecent().args;
        expect(args[0]).toBe(MockAlertTemplateComponent);
    });

    it('Opens Alert with Message', async () => {
        const message1 = ALERT_MSG;
        const alert1 = alertFactory.createAlert({
            message: message1,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        await alert1.show();
        const args1 = fundamentalAlertService.open.calls.mostRecent().args;
        expect(args1[0]).toBe(`${message1}_translated`);

        const message2 = ALERT_MSG;
        const alert2 = alertFactory.createAlert(message1);
        await alert2.show();
        const args2 = fundamentalAlertService.open.calls.mostRecent().args;
        expect(args2[0]).toBe(`${message2}_translated`);
    });

    it('When both, Message and Component are provided, Alert is displayed with the Message', async () => {
        const message = ALERT_MSG;
        const alert = alertFactory.createAlert({
            message,
            component: MockAlertTemplateComponent,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        await alert.show();
        const args = fundamentalAlertService.open.calls.mostRecent().args;
        expect(args[0]).toBe(`${message}_translated`);
    });

    it('Opens Alert with no Message when empty Config is provided', async () => {
        const alert = alertFactory.createAlert({
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        await alert.show();
        expect(alert.message as undefined).toBe(undefined);
    });

    it('Alerts are created with the expected default values', () => {
        const alert = alertFactory.createAlert({
            message: ALERT_MSG,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        expect(alert.alertConf).toEqual(jasmine.objectContaining(MOCK_ALERT_CONFIG_DEFAULTS));
    });

    it('isDisplayed() properly returns displayed state for show() and hide() functions', async () => {
        const alert = alertFactory.createAlert(ALERT_MSG);
        expect(alert.isDisplayed()).toBe(false);

        await alert.show();
        expect(alert.isDisplayed()).toBe(true);

        alert.hide();
        expect(alert.isDisplayed()).toBe(false);
    });

    it('Alert is dismissed automatically for given timeout', async () => {
        const alert = alertFactory.createAlert({
            message: ALERT_MSG,
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        await alert.show();

        expect(alert.isDisplayed()).toBe(true);
        jasmine.clock().tick(1000);
        expect(alert.isDisplayed()).toBe(false);
    });

    it('Alert is dismissed by manually calling "hide()" function', async () => {
        const alert = alertFactory.createAlert({
            message: ALERT_MSG,
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        await alert.show();
        alert.hide();

        expect(alert.isDisplayed()).toBe(false);
    });

    it('Factory properly assigns Alert Type', () => {
        const alertDefault = alertFactory.createAlert(ALERT_MSG);
        const alertDefaultWithType = alertFactory.createAlert({
            message: ALERT_MSG,
            type: 'error',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        const info = alertFactory.createInfo(ALERT_MSG);
        const danger = alertFactory.createDanger(ALERT_MSG);
        const warning = alertFactory.createWarning(ALERT_MSG);
        const success = alertFactory.createSuccess(ALERT_MSG);

        expect(alertDefault.type).toBe(IAlertServiceType.INFO);
        expect(alertDefaultWithType.type).toBe(IAlertServiceType.DANGER);
        expect(info.type).toBe(IAlertServiceType.INFO);
        expect(info.type).toBe(IAlertServiceType.INFO);
        expect(danger.type).toBe(IAlertServiceType.DANGER);
        expect(warning.type).toBe(IAlertServiceType.WARNING);
        expect(success.type).toBe(IAlertServiceType.SUCCESS);
    });
});
