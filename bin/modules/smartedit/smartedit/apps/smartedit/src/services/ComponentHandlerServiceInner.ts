/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import {
    GatewayProxied,
    IComponentHandlerService,
    YJQUERY_TOKEN,
    PageInfoService,
    windowUtils
} from 'smarteditcommons';
@GatewayProxied('isExternalComponent', 'reloadInner')
export class ComponentHandlerService extends IComponentHandlerService {
    constructor(@Inject(YJQUERY_TOKEN) yjQuery: JQueryStatic) {
        super(yjQuery);
    }

    isExternalComponent(
        smarteditComponentId: string,
        smarteditComponentType: string
    ): Promise<boolean> {
        const component: JQuery = this.getOriginalComponent(
            smarteditComponentId,
            smarteditComponentType
        );
        const componentCatalogVersionUuid: string = this.getCatalogVersionUuid(component);
        return Promise.resolve(
            componentCatalogVersionUuid !==
                this.getBodyClassAttributeByRegEx(
                    PageInfoService.PATTERN_SMARTEDIT_CATALOG_VERSION_UUID
                )
        );
    }

    reloadInner(): Promise<void> {
        windowUtils.getWindow().location.reload();
        return Promise.resolve();
    }
}
