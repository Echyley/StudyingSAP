/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ActionableAlertService } from 'cmssmarteditcontainer/services/actionableAlert';
import { IAlertConfig, IAlertService, IAlertServiceType } from 'smarteditcommons';

describe('ActionableAlertService', () => {
    let actionableAlertService: ActionableAlertService;
    let alertService: jasmine.SpyObj<IAlertService>;

    class mockComponent {}

    const MOCK_NEW_PAYLOAD = {
        component: mockComponent,
        data: { message: 'mocked message' },
        minWidth: '',
        mousePersist: true,
        duration: 1000,
        dismissible: true,
        width: '300px'
    } as IAlertConfig;

    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', [
            'showInfo',
            'showSuccess'
        ]);

        actionableAlertService = new ActionableAlertService(alertService);
    });

    describe('displayActionableAlert', () => {
        it("use given component in an 'info' alert by default", () => {
            // Act
            actionableAlertService.displayActionableAlert(MOCK_NEW_PAYLOAD);

            // Assert
            expect(alertService.showInfo).toHaveBeenCalledWith({
                component: mockComponent,
                data: { message: 'mocked message' },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it('WHEN displayActionableAlert is called with an invalid alert type THEN the alert is displayed as info', () => {
            // Given
            const invalidAlertType = 'Something invalid';

            // Act
            actionableAlertService.displayActionableAlert(
                MOCK_NEW_PAYLOAD,
                // cast to any for test purpose
                invalidAlertType as any
            );

            // Assert
            expect(alertService.showInfo).toHaveBeenCalledWith({
                component: mockComponent,
                data: { message: 'mocked message' },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it('WHEN displayActionableAlert is called with a valid alert type (success) THEN the alert is displayed as Success', () => {
            // Act
            actionableAlertService.displayActionableAlert(
                MOCK_NEW_PAYLOAD,
                IAlertServiceType.SUCCESS
            );

            // Assert
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                component: mockComponent,
                data: { message: 'mocked message' },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });
    });
});
