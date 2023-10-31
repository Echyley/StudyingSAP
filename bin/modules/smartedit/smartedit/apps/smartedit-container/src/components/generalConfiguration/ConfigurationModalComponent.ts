/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './ConfigurationModalComponent.scss';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { from as rxFrom, Observable } from 'rxjs';

import {
    ConfirmationModalConfig,
    ModalButtonAction,
    ModalButtonStyle,
    ModalManagerService,
    IConfirmationModalService
} from 'smarteditcommons';
import { ConfigurationItem } from '../../services/bootstrap/Configuration';
import { ConfigurationService } from '../../services/ConfigurationService';

@Component({
    selector: 'se-configuration-modal',
    templateUrl: './ConfigurationModalComponent.html'
})
export class ConfigurationModalComponent implements OnInit {
    @ViewChild('form', { static: true }) public form: NgForm;

    constructor(
        public editor: ConfigurationService,
        public modalManager: ModalManagerService,
        private confirmationModalService: IConfirmationModalService
    ) {}

    ngOnInit(): void {
        this.editor.init();

        this.form.statusChanges.subscribe(() => {
            if (this.form.valid && this.form.dirty) {
                this.modalManager.enableButton('save');
            }

            if (this.form.invalid || !this.form.dirty) {
                this.modalManager.disableButton('save');
            }
        });

        this.modalManager.addButtons([
            {
                id: 'save',
                style: ModalButtonStyle.Primary,
                label: 'se.cms.component.confirmation.modal.save',
                callback: (): Observable<void> => rxFrom(this.onSave()),
                disabled: true
            },
            {
                id: 'cancel',
                label: 'se.cms.component.confirmation.modal.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss,
                callback: (): Observable<void> => rxFrom(this.onCancel())
            }
        ]);
    }

    public trackByFn(_: number, item: ConfigurationItem): string {
        return item.uuid;
    }

    private onCancel(): Promise<void> {
        const { dirty } = this.form;
        const confirmationData: ConfirmationModalConfig = {
            description: 'se.editor.cancel.confirm'
        };

        if (!dirty) {
            return Promise.resolve();
        }

        return this.confirmationModalService
            .confirm(confirmationData)
            .then(() => this.modalManager.close(null));
    }

    private onSave(): Promise<void> {
        return this.editor.submit(this.form).then(() => {
            this.modalManager.close(null);
        });
    }
}
