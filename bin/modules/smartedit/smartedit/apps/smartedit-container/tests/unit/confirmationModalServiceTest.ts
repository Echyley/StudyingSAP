/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ConfirmDialogComponent,
    ModalButtonAction,
    ModalButtonStyle,
    IModalService
} from 'smarteditcommons';
import { ConfirmationModalService } from 'smarteditcontainer/services';

describe('ConfirmationModalService', () => {
    // Service Under Test
    let confirmationModalService: ConfirmationModalService;
    let modalService: jasmine.SpyObj<IModalService>;
    const MY_CONFIGURATION_TITLE = 'my.confirmation.title';
    // Set-up Service Under Test

    beforeEach(() => {
        modalService = jasmine.createSpyObj('modalService', ['open']);
        modalService.open.and.returnValue({
            afterClosed: {
                subscribe: () => {
                    // This is intentional for test
                }
            }
        } as any);

        confirmationModalService = new ConfirmationModalService(modalService);
    });

    it('GIVEN showOkButtonOnly WHEN modalService is called THEN it will display the modal with only the OK button', () => {
        // Arrange

        // Act
        const config = {
            title: MY_CONFIGURATION_TITLE,
            description: 'my.confirmation.message',
            showOkButtonOnly: true
        };
        confirmationModalService.confirm(config);

        // Assert
        expect(modalService.open).toHaveBeenCalledWith({
            component: ConfirmDialogComponent,
            data: config,
            config: {
                dialogPanelClass: 'se-confirmation-dialog',
                focusTrapped: false,
                container: 'body'
            },
            templateConfig: {
                isDismissButtonVisible: true,
                title: MY_CONFIGURATION_TITLE,
                buttons: [
                    {
                        id: 'confirmOk',
                        label: 'se.confirmation.modal.ok',
                        action: ModalButtonAction.Close,
                        style: ModalButtonStyle.Primary,
                        callback: jasmine.any(Function) as any
                    }
                ]
            }
        });
    });

    describe('with description', () => {
        it(
            'confirm will call open on the modalService with the given description and title when provided with a description' +
                ' and title',
            () => {
                // Arrange

                // Act
                const config = {
                    title: MY_CONFIGURATION_TITLE,
                    description: 'my.confirmation.message'
                };
                confirmationModalService.confirm(config);

                // Assert
                expect(modalService.open).toHaveBeenCalledWith({
                    component: ConfirmDialogComponent,
                    data: config,
                    config: {
                        dialogPanelClass: 'se-confirmation-dialog',
                        container: 'body',
                        focusTrapped: false
                    },
                    templateConfig: {
                        title: MY_CONFIGURATION_TITLE,
                        isDismissButtonVisible: true,
                        buttons: [
                            {
                                id: 'confirmOk',
                                label: 'se.confirmation.modal.ok',
                                style: ModalButtonStyle.Primary,
                                action: ModalButtonAction.Close,
                                callback: jasmine.any(Function) as any
                            },
                            {
                                id: 'confirmCancel',
                                label: 'se.confirmation.modal.cancel',
                                style: ModalButtonStyle.Default,
                                action: ModalButtonAction.Dismiss,
                                callback: jasmine.any(Function) as any
                            }
                        ]
                    }
                });
            }
        );
    });
});
