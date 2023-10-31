/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Dictionary } from 'lodash';
import { PersonalizationsmarteditCustomizeViewHelper } from 'personalizationsmartedit/service/PersonalizationsmarteditCustomizeViewHelper';
import { GatewayProxied } from 'smarteditcommons';
@GatewayProxied('getSourceContainersInfo')
@Injectable()
export class CustomizeViewServiceProxy {
    constructor(
        protected personalizationsmarteditCustomizeViewHelper: PersonalizationsmarteditCustomizeViewHelper
    ) {}

    public getSourceContainersInfo(): Dictionary<number> {
        return this.personalizationsmarteditCustomizeViewHelper.getSourceContainersInfo();
    }
}
