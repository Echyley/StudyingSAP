/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { Injectable } from '@angular/core';
import { IPersonalizationsmarteditContextMenuServiceProxy } from 'personalizationcommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('openDeleteAction', 'openAddAction', 'openEditAction', 'openEditComponentAction')
@Injectable()
export class PersonalizationsmarteditContextMenuServiceProxy extends IPersonalizationsmarteditContextMenuServiceProxy {
    constructor() {
        super();
    }
}
