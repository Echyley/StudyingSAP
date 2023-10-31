/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ConfirmationModalConfig } from './IConfirmationModal';

export abstract class IConfirmationModalService {
    confirm(conf: ConfirmationModalConfig): Promise<boolean> {
        'proxyFunction';
        return Promise.resolve(true);
    }
}
