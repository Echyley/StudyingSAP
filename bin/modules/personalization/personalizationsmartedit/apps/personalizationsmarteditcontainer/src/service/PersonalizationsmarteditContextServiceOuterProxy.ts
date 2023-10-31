/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IPersonalizationsmarteditContextServiceProxy } from 'personalizationcommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData')
@Injectable()
export class PersonalizationsmarteditContextServiceProxy extends IPersonalizationsmarteditContextServiceProxy {
    constructor() {
        super();
    }
}
