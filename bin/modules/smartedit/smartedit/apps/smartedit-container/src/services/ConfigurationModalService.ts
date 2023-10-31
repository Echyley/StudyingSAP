/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IModalService } from 'smarteditcommons';
import { ConfigurationModalComponent } from '../components/generalConfiguration/ConfigurationModalComponent';

@Injectable()
export class ConfigurationModalService {
    constructor(private modalService: IModalService) {}
    /*
     *The edit configuration method opens the modal for the configurations.
     */

    public editConfiguration(): void {
        this.modalService.open({
            templateConfig: {
                title: 'se.modal.administration.configuration.edit.title'
            },
            component: ConfigurationModalComponent,
            config: {
                focusTrapped: false,
                backdropClickCloseable: false
            }
        });
    }
}
