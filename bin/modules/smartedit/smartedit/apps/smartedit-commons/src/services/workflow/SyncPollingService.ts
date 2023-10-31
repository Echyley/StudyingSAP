/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied } from '../gatewayProxiedAnnotation';
import { ISyncPollingService } from '../interfaces';

@GatewayProxied(
    'getSyncStatus',
    'fetchSyncStatus',
    'changePollingSpeed',
    'registerSyncPollingEvents',
    'performSync'
)
export class SyncPollingService extends ISyncPollingService {
    constructor() {
        super();
    }
}
