/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    DialogConfig as FundamentalModalConfig,
    DialogRef as FundamentalModalRef,
    DialogService as FundamentalModalService
} from '@fundamental-ngx/core';
import { ModalTemplateComponent } from '../../components';
import { IModalService } from '../../interfaces';
import { ModalOpenConfig } from './i-modal.service';

@Injectable()
export class ModalService implements IModalService {
    constructor(protected fundamentalModalService: FundamentalModalService) {}

    public open<T>(options: ModalOpenConfig<T>): FundamentalModalRef {
        const { templateConfig } = options;

        return !!templateConfig
            ? this.openWithTemplate<T>(options)
            : this.openStandalone<T>(options);
    }

    public hasOpenModals(): boolean {
        return this.fundamentalModalService.hasOpenDialogs();
    }

    public dismissAll(): void {
        this.fundamentalModalService.dismissAll();
    }

    private openStandalone<T>(options: ModalOpenConfig<T>): FundamentalModalRef {
        const { config = {}, component, data } = options;

        return this.fundamentalModalService.open(component, { ...config, data });
    }

    private openWithTemplate<T>(options: ModalOpenConfig<T>): FundamentalModalRef {
        const { config = {}, templateConfig = {}, component, data } = options;

        const settings: FundamentalModalConfig = {
            ...config,
            data: {
                templateConfig,
                component,
                modalData: data
            }
        };

        return this.fundamentalModalService.open(ModalTemplateComponent, settings);
    }
}
