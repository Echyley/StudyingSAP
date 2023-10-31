/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    AlertConfig,
    AlertRef,
    AlertService as FundamentalAlertService
} from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { Alert, ALERT_CONFIG_DEFAULTS, IAlertServiceType } from 'smarteditcommons';
import { AlertFactory } from 'smarteditcontainer/services/alerts';

describe('alertFactory', () => {
    let alertFactory: AlertFactory;
    const MOCK_ALERT_CONFIG_DEFAULTS: AlertConfig = { ...ALERT_CONFIG_DEFAULTS };

    let fundamentalAlertService: jasmine.SpyObj<FundamentalAlertService>;
    const translateService: jasmine.SpyObj<TranslateService> = jasmine.createSpyObj<
        TranslateService
    >('translateService', ['get']);
    translateService.get.and.callFake((content: any) => of(`${content}_translated`));

    beforeEach(() => {
        fundamentalAlertService = jasmine.createSpyObj<FundamentalAlertService>(
            'fundamentalAlertService',
            ['open']
        );
        fundamentalAlertService.open.and.returnValue(new AlertRef());

        alertFactory = new AlertFactory(
            fundamentalAlertService,
            translateService,
            MOCK_ALERT_CONFIG_DEFAULTS
        );
    });

    it('Will convert a string param into an AlertConfig with message', async () => {
        const message = 'my alert message';
        const alert = alertFactory.createAlert(message);
        await alert.show();
        expect(alert.alertConf.message).toBe(`${message}_translated`);
    });

    it('Alert "show()" function delegates call to Base Class', async () => {
        const baseAlertSpyShow = spyOn(Alert.prototype, 'show');
        baseAlertSpyShow.and.callThrough();
        const alert = alertFactory.createAlert({
            message: 'Alert message',
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        await alert.show();
        expect(baseAlertSpyShow).toHaveBeenCalled();
    });

    it('Alert "hide()" function delegates call to Base Class', async () => {
        const baseAlertSpyHide = spyOn(Alert.prototype, 'hide');
        baseAlertSpyHide.and.callThrough();
        const alert = alertFactory.createAlert({
            message: 'Alert message',
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });
        await alert.show();
        alert.hide();
        expect(baseAlertSpyHide).toHaveBeenCalled();
    });

    it('Alert is of the Template type that was given', () => {
        const alert1 = alertFactory.createAlert({
            message: 'A string message',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });

        expect(alert1.message).toBeDefined();
    });

    it('Factory properly assigns the Alert Type', () => {
        const info = alertFactory.createInfo({
            message: 'info',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        const danger = alertFactory.createDanger({
            message: 'danger',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        const warning = alertFactory.createWarning({
            message: 'warning',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
        const success = alertFactory.createSuccess({
            message: 'success',
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });

        expect(info.type).toBe(IAlertServiceType.INFO);
        expect(danger.type).toBe(IAlertServiceType.DANGER);
        expect(warning.type).toBe(IAlertServiceType.WARNING);
        expect(success.type).toBe(IAlertServiceType.SUCCESS);
    });
});
