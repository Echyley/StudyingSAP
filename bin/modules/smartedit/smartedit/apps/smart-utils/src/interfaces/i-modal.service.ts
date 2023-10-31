/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { DialogRef } from '@fundamental-ngx/core';
import { ModalOpenConfig } from '../services/modal';

export abstract class IModalService {
    public dismissAll(): void {
        'proxyFunction';
    }

    public open<T>(conf: ModalOpenConfig<T>): DialogRef {
        'proxyFunction';
        return {} as DialogRef;
    }

    public hasOpenModals(): boolean {
        'proxyFunction';
        return null;
    }
}
