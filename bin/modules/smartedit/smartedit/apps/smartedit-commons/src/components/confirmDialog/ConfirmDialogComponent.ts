/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { ConfirmationModalConfig, ModalManagerService } from '../../services';

@Component({
    selector: 'se-confirm-dialog',
    templateUrl: './ConfirmDialogComponent.html'
})
export class ConfirmDialogComponent implements OnInit {
    public config: ConfirmationModalConfig;

    constructor(private modalManager: ModalManagerService) {}

    ngOnInit(): void {
        this.modalManager
            .getModalData()
            .pipe(take(1))
            .subscribe((config: ConfirmationModalConfig) => (this.config = config));
    }
}
