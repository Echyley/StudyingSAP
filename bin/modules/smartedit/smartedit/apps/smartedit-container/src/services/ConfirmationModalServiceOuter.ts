/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
    ConfirmationModalConfig,
    ConfirmDialogComponent,
    ModalButtonAction,
    ModalButtonStyle,
    GatewayProxied,
    IConfirmationModalService,
    ModalButtonOptions,
    IModalService
} from 'smarteditcommons';

/**
 * Service used to open a confirmation modal in which an end-user can confirm or cancel an action.
 * A confirmation modal consists of a title, content, and an OK and cancel button. This modal may be used in any context in which a
 * confirmation is required.
 */
@GatewayProxied('confirm')
export class ConfirmationModalService extends IConfirmationModalService {
    constructor(private modalService: IModalService) {
        super();
    }

    /**
     * Uses the [ModalService]{@link IModalService} to open a confirmation modal.
     *
     * The confirmation modal is initialized by a default i18N key as a title or by an override title passed in configuration.
     *
     * @returns A promise that is resolved when the OK button is actioned or is rejected when the Cancel
     * button is actioned.
     */
    public confirm(configuration: ConfirmationModalConfig): Promise<any> {
        const ref = this.modalService.open<ConfirmationModalConfig>({
            component: ConfirmDialogComponent as Type<any>,
            data: configuration,
            config: {
                focusTrapped: false,
                dialogPanelClass: 'se-confirmation-dialog',
                // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
                container: (document.querySelector('[uib-modal-window]') as HTMLElement) || 'body'
            },
            templateConfig: {
                title: configuration.title || 'se.confirmation.modal.title',
                buttons: this.getButtons(configuration),
                isDismissButtonVisible: true
            }
        });
        // it always rejects with undefined, no matter what value you pass (due to handling rejection in MessageGateway)
        return new Promise((resolve, reject) => ref.afterClosed.subscribe(resolve, reject));
    }

    private getButtons(configuration: ConfirmationModalConfig): ModalButtonOptions[] {
        return [
            {
                id: 'confirmOk',
                label: 'se.confirmation.modal.ok',
                style: ModalButtonStyle.Primary,
                action: ModalButtonAction.Close,
                callback: (): Observable<boolean> => of(true)
            },
            !configuration.showOkButtonOnly && {
                id: 'confirmCancel',
                label: 'se.confirmation.modal.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss,
                callback: (): Observable<boolean> => of(false)
            }
        ].filter((x) => !!x);
    }
}
