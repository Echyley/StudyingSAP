/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { DialogRef } from '@fundamental-ngx/core';

@Component({
    template: `
        <fd-dialog id="se-busy-indicator-dialog">
            <fd-dialog-body>
                <div class="panel panel-default ySEPanelSpinner">
                    <div class="panel-body">
                        <div class="spinner ySESpinner">
                            <div class="fd-busy-indicator-extended">
                                <fd-busy-indicator
                                    [loading]="true"
                                    size="l"
                                    [label]="
                                        modalRef.data.customLoadingMessageLocalizedKey ||
                                            'se.wait.dialog.message' | translate
                                    "></fd-busy-indicator>
                            </div>
                        </div>
                    </div>
                </div>
            </fd-dialog-body>
        </fd-dialog>
    `
    // selector: 'wait-dialog'
})
export class WaitDialogComponent {
    constructor(public modalRef: DialogRef) {}
}
