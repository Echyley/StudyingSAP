/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    GatewayProxy,
    IPermissionService,
    IToolbarServiceFactory,
    LogService,
    TypedMap
} from 'smarteditcommons';
import { ToolbarService } from './ToolbarService';

@Injectable()
export class ToolbarServiceFactory implements IToolbarServiceFactory {
    private toolbarServicesByGatewayId: TypedMap<ToolbarService> = {};

    constructor(
        private gatewayProxy: GatewayProxy,
        private logService: LogService,
        private permissionService: IPermissionService
    ) {}

    getToolbarService(gatewayId: string): ToolbarService {
        if (!this.toolbarServicesByGatewayId[gatewayId]) {
            this.toolbarServicesByGatewayId[gatewayId] = new ToolbarService(
                gatewayId,
                this.gatewayProxy,
                this.logService,
                this.permissionService
            );
        }
        return this.toolbarServicesByGatewayId[gatewayId];
    }
}
