/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DialogRef } from '@fundamental-ngx/core';
import {
    GatewayProxied,
    ModalOpenConfig,
    IWaitDialogService,
    ModalService,
    WaitDialogComponent
} from 'smarteditcommons';

/** @internal */
@GatewayProxied()
export class WaitDialogService extends IWaitDialogService {
    private modalRef: DialogRef;

    constructor(private modalService: ModalService) {
        super();
        this.modalRef = null;
    }

    showWaitModal(customLoadingMessageLocalizedKey?: string): void {
        const config: ModalOpenConfig<{ customLoadingMessageLocalizedKey: string }> = {
            component: WaitDialogComponent,
            data: { customLoadingMessageLocalizedKey },
            config: {
                backdropClickCloseable: false,
                dialogPanelClass: 'se-wait-spinner-dialog',
                focusTrapped: false
            }
        };

        if (this.modalRef === null) {
            this.modalRef = this.modalService.open(config);
        }
    }

    hideWaitModal(): void {
        if (this.modalRef != null) {
            this.modalRef.close();
            this.modalRef = null;
        }
    }
}
