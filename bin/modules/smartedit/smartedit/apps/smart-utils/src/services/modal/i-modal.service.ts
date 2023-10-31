/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TemplateRef, Type } from '@angular/core';
import { DialogConfig } from '@fundamental-ngx/core';
import { ModalButtonOptions } from '../../interfaces';

export interface ModalTemplateConfig {
    buttons?: ModalButtonOptions[];
    title?: string;
    titleSuffix?: string;
    isDismissButtonVisible?: boolean;
}

/** Configuration to open a modal via ModalService. */
export interface ModalOpenConfig<T> {
    /**
     * Component to be displayed as a modal.
     */
    component: Type<any> | TemplateRef<any>;
    data?: T;
    /**
     * Opens a Modal with the given configuration.
     * `ModalManagerService` can be injected into the component constructor.
     *
     * **Note**: If no templateConfig is provided, the component is resolved as a standalone and cannot access `ModalManagerService`.
     */
    templateConfig?: ModalTemplateConfig;
    config?: DialogConfig;
}
