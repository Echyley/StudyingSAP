/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { OperationContextService, OPERATION_CONTEXT, TYPES_RESOURCE_URI } from 'smarteditcommons';

@Injectable()
export class StructuresRestService {
    private readonly TYPE_PLACEHOLDER = ':smarteditComponentType';
    private readonly URI = `${TYPES_RESOURCE_URI}/`;

    constructor(operationContextService: OperationContextService) {
        operationContextService.register(this.URI, OPERATION_CONTEXT.CMS);
    }

    /**
     * Returns Types resource URI for given mode. If no type is given, it will use a placeholder for Item Type Code.
     *
     * E.g.
     * cmswebservices/v1/types?code=CMSCategoryRestriction&mode=ADD
     */
    public getUriForContext(mode: string, type?: string): string {
        const typePlaceholder = type || this.TYPE_PLACEHOLDER;

        const uri = `${TYPES_RESOURCE_URI}?code=${typePlaceholder}&mode=${mode.toUpperCase()}`;
        return uri;
    }
}
