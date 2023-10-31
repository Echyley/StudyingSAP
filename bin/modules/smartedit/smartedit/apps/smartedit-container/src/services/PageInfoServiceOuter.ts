/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IPageInfoService } from 'smarteditcommons';

/** @internal */
@GatewayProxied(
    'getPageUID',
    'getPageUUID',
    'getCatalogVersionUUIDFromPage',
    'isSameCatalogVersionOfPageAndPageTemplate'
)
export class PageInfoService extends IPageInfoService {
    constructor() {
        super();
    }
}
