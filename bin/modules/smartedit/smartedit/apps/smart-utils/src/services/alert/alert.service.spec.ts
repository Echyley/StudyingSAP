/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { Alert } from './alert';
import { AlertFactory } from './alert-factory';
import { AlertService } from './alert.service';

describe('alertService', () => {
    let alertService: AlertService;
    let alertFactory: jasmine.SpyObj<AlertFactory>;
    beforeEach(() => {
        alertFactory = jasmine.createSpyObj<AlertFactory>('alertFactory', [
            'createInfo',
            'createAlert',
            'createWarning',
            'createSuccess',
            'createDanger'
        ]);

        alertService = new AlertService(alertFactory);
    });

    describe('all alertService.showXZY() functions', () => {
        // spies
        const message = 'Alert Test';
        const COMMON_DESCR =
            'showAlert creates an alert and calls alert.show() before returning the alert';
        let mockAlert: jasmine.SpyObj<Alert>;

        function testShowXYZFunction(
            alertServiceFn: keyof AlertService,
            alertFactoryFn: keyof jasmine.SpyObj<AlertFactory>
        ) {
            // given
            mockAlert = jasmine.createSpyObj('mockAlert', ['show']);
            alertFactory[alertFactoryFn].and.returnValue(mockAlert);

            // when
            alertService[alertServiceFn](message);

            // then
            expect(alertFactory[alertFactoryFn]).toHaveBeenCalledWith(message);
            expect(mockAlert.show).toHaveBeenCalled();
        }

        it(COMMON_DESCR, () => {
            testShowXYZFunction('showAlert', 'createAlert');
        });

        it(COMMON_DESCR, () => {
            testShowXYZFunction('showInfo', 'createInfo');
        });

        it(COMMON_DESCR, () => {
            testShowXYZFunction('showWarning', 'createWarning');
        });

        it(COMMON_DESCR, () => {
            testShowXYZFunction('showSuccess', 'createSuccess');
        });

        it(COMMON_DESCR, () => {
            testShowXYZFunction('showDanger', 'createDanger');
        });
    });
});
