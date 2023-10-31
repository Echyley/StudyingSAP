/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, IPageService } from 'smarteditcommons';

@GatewayProxied()
@Injectable()
export class PageService extends IPageService {
    constructor() {
        super();
    }
}
