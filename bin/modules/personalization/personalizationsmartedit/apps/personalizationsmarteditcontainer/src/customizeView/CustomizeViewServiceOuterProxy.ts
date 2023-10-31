/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Dictionary } from 'lodash';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('getSourceContainersInfo')
@Injectable()
export class CustomizeViewServiceProxy {
    getSourceContainersInfo(): Dictionary<number> {
        'proxyFunction';
        return null;
    }
}
