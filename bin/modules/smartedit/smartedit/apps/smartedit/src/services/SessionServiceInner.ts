/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, ISessionService } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
@Injectable()
export class SessionService extends ISessionService {
    constructor() {
        super();
    }
}
