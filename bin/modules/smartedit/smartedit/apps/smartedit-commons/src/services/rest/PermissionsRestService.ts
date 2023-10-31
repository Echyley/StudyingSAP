/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, RestServiceFactory } from '@smart/utils';
import {
    IPermissionsRestServiceQueryData,
    IPermissionsRestServiceResult
} from 'smarteditcommons/dtos';

@Injectable()
export class PermissionsRestService {
    private readonly URI = '/permissionswebservices/v1/permissions/global/search';
    private readonly resource: IRestService<IPermissionsRestServiceResult>;

    constructor(restServiceFactory: RestServiceFactory) {
        this.resource = restServiceFactory.get<IPermissionsRestServiceResult>(this.URI);
    }

    get(queryData: IPermissionsRestServiceQueryData): Promise<IPermissionsRestServiceResult> {
        return this.resource
            .queryByPost(
                { principalUid: queryData.user },
                { permissionNames: queryData.permissionNames }
            )
            .then((data: IPermissionsRestServiceResult) => ({
                permissions: data.permissions
            }));
    }
}
