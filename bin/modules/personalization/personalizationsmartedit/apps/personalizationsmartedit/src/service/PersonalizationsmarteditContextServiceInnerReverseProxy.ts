/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('applySynchronization', 'isCurrentPageActiveWorkflowRunning')
@Injectable()
export class PersonalizationsmarteditContextServiceReverseProxy {
    public applySynchronization(): void {
        'proxyFunction';
        return undefined;
    }

    public isCurrentPageActiveWorkflowRunning(): Promise<boolean> {
        'proxyFunction';
        return undefined;
    }
}
