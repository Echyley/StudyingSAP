/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { LogService, ModalService, TypedMap } from 'smarteditcommons';
import {
    SelectComponentTypeModalComponent,
    SelectComponentTypeModalComponentData
} from '../components/SelectComponentTypeModalComponent';

@Injectable()
export class SelectComponentTypeModalService {
    constructor(private logService: LogService, private modalService: ModalService) {}

    public async open(subTypes: TypedMap<string>): Promise<string | void> {
        return await this.modalService
            .open<SelectComponentTypeModalComponentData>({
                component: SelectComponentTypeModalComponent,
                data: {
                    subTypes
                },
                config: {
                    dialogPanelClass: 'modal-lg'
                },
                templateConfig: {
                    title: 'se.cms.nestedcomponenteditor.select.type'
                }
            })
            .afterClosed.toPromise<string>()
            .catch((error) => {
                this.logService.debug('Select Component Type Modal dismissed', error);
            });
    }
}
