/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { BaseAlertService, IAlertConfig } from 'smarteditcommons';
import { AlertFactory, AlertService } from 'smarteditcontainer/services/alerts';

describe('alertService', () => {
    let alertService: AlertService;
    let alertFactory: jasmine.SpyObj<AlertFactory>;
    const alertConf: IAlertConfig = {
        message: 'alert message',
        minWidth: '',
        mousePersist: true,
        duration: 1000,
        dismissible: true,
        width: '300px'
    };
    beforeEach(() => {
        alertFactory = jasmine.createSpyObj<AlertFactory>('alertFactory', [
            'createInfo',
            'createAlert',
            'createWarning',
            'createSuccess',
            'createDanger',
            'getAlertConfigFromStringOrConfig'
        ]);

        alertService = new AlertService(alertFactory);
    });

    describe('displaying Alert variations', () => {
        it('"showAlert" function delegates call to Base Class', () => {
            spyOn(alertService, 'showAlert').and.callThrough();
            alertFactory.getAlertConfigFromStringOrConfig.and.returnValue(alertConf);
            const baseAlertSpyShow = spyOn(BaseAlertService.prototype, 'showAlert');

            alertService.showAlert(alertConf);

            expect(baseAlertSpyShow).toHaveBeenCalledWith(alertConf);
        });

        it('"showInfo" function delegates call to Base Class', () => {
            spyOn(alertService, 'showInfo').and.callThrough();
            alertFactory.getAlertConfigFromStringOrConfig.and.returnValue(alertConf);
            const baseAlertSpyShow = spyOn(BaseAlertService.prototype, 'showInfo');

            alertService.showInfo(alertConf);

            expect(baseAlertSpyShow).toHaveBeenCalledWith(alertConf);
        });

        it('"showDanger" function delegates call to Base Class', () => {
            spyOn(alertService, 'showDanger').and.callThrough();
            alertFactory.getAlertConfigFromStringOrConfig.and.returnValue(alertConf);
            const baseAlertSpyShow = spyOn(BaseAlertService.prototype, 'showDanger');

            alertService.showDanger(alertConf);

            expect(baseAlertSpyShow).toHaveBeenCalledWith(alertConf);
        });

        it('"showWarning" function delegates call to Base Class', () => {
            spyOn(alertService, 'showWarning').and.callThrough();
            alertFactory.getAlertConfigFromStringOrConfig.and.returnValue(alertConf);
            const baseAlertSpyShow = spyOn(BaseAlertService.prototype, 'showWarning');

            alertService.showWarning(alertConf);

            expect(baseAlertSpyShow).toHaveBeenCalledWith(alertConf);
        });

        it('"showSuccess" function delegates call to Base Class', () => {
            spyOn(alertService, 'showSuccess').and.callThrough();
            alertFactory.getAlertConfigFromStringOrConfig.and.returnValue(alertConf);
            const baseAlertSpyShow = spyOn(BaseAlertService.prototype, 'showSuccess');

            alertService.showSuccess(alertConf);

            expect(baseAlertSpyShow).toHaveBeenCalledWith(alertConf);
        });
    });
});
