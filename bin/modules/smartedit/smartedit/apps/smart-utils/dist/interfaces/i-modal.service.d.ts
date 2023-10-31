/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { DialogRef } from '@fundamental-ngx/core';
import { ModalOpenConfig } from '../services/modal';
export declare abstract class IModalService {
    dismissAll(): void;
    open<T>(conf: ModalOpenConfig<T>): DialogRef;
    hasOpenModals(): boolean;
}
