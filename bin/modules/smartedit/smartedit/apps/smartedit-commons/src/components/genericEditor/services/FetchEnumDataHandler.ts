/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { stringUtils, IRestService, Payload, RestServiceFactory, TypedMap } from '@smart/utils';
import { ENUM_RESOURCE_URI } from '../../../utils';
import { GenericEditorField } from '../types';
import { IFetchDataHandler } from './IFetchDataHandler';

/* @internal  */
@Injectable()
export class FetchEnumDataHandler implements IFetchDataHandler {
    private static cache: TypedMap<any> = {};

    private restServiceForEnum: IRestService<Payload>;

    public static resetForTests(): void {
        FetchEnumDataHandler.cache = {};
    }

    constructor(private restServiceFactory: RestServiceFactory) {
        this.restServiceForEnum = this.restServiceFactory.get<Payload>(ENUM_RESOURCE_URI);
    }

    findByMask(field: GenericEditorField, search?: string): Promise<string[]> {
        return (
            FetchEnumDataHandler.cache[field.cmsStructureEnumType]
                ? Promise.resolve(FetchEnumDataHandler.cache[field.cmsStructureEnumType])
                : Promise.resolve(
                      this.restServiceForEnum.get({
                          enumClass: field.cmsStructureEnumType
                      })
                  )
        ).then((response) => {
            FetchEnumDataHandler.cache[field.cmsStructureEnumType] = response;
            return FetchEnumDataHandler.cache[field.cmsStructureEnumType].enums.filter(
                (element: Payload) =>
                    stringUtils.isBlank(search) ||
                    (element.label as string).toUpperCase().indexOf(search.toUpperCase()) > -1
            );
        });
    }

    getById(field: GenericEditorField, identifier: string): Promise<string> {
        return null;
    }
}
