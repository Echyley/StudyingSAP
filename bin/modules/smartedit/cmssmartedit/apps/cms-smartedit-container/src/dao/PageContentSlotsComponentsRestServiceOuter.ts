/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IPageContentSlotsComponentsRestService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('clearCache', 'getSlotsToComponentsMapForPageUid', 'getComponentsForSlot')
@Injectable()
export class PageContentSlotsComponentsRestService extends IPageContentSlotsComponentsRestService {
    constructor() {
        super();
    }
}
