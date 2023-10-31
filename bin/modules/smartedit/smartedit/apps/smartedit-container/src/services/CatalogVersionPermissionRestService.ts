/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    rarelyChangingContent,
    userEvictionTag,
    Cached,
    ISessionService,
    RestServiceFactory
} from 'smarteditcommons';

export interface CatalogVersionSyncPermission {
    canSynchronize: boolean;
    targetCatalogVersion: string;
}

export interface CatalogVersionPermissionMap {
    key: string;
    value: string;
}

export interface CatalogVersionPermission {
    catalogId: string;
    catalogVersion: string;
    permissions: CatalogVersionPermissionMap[];
    syncPermissions: CatalogVersionSyncPermission[];
}

/**
 * The catalog version permission service is used to check if the current user has been granted certain permissions
 * on a given catalog ID and catalog Version.
 */
@Injectable()
export class CatalogVersionPermissionRestService {
    private readonly URI = '/permissionswebservices/v1/permissions/catalogs/search';
    constructor(
        private restServiceFactory: RestServiceFactory,
        private sessionService: ISessionService
    ) {}

    /**
     * This method returns permissions from the Catalog Version Permissions Service API.
     *
     * Sample Request:
     * POST /permissionswebservices/v1/permissions/catalogs/search?catalogId=apparel-deContentCatalog&catalogVersion=Online
     *
     * Sample Response from API:
     * {
     * "permissionsList": [
     *     {
     *       "catalogId": "apparel-deContentCatalog",
     *       "catalogVersion": "Online",
     *       "permissions": [
     *         {
     *           "key": "read",
     *           "value": "true"
     *         },
     *         {
     *           "key": "write",
     *           "value": "false"
     *         }
     *       ],
     *      "syncPermissions": [
     *        {
     *          "canSynchronize": "true",
     *          "targetCatalogVersion": "Online"
     *        }
     *     }
     *    ]
     * }
     *
     * Sample Response returned by the service:
     * {
     *   "catalogId": "apparel-deContentCatalog",
     *   "catalogVersion": "Online",
     *   "permissions": [
     *      {
     *        "key": "read",
     *        "value": "true"
     *      },
     *      {
     *        "key": "write",
     *        "value": "false"
     *      }
     *     ],
     *    "syncPermissions": [
     *      {
     *        "canSynchronize": "true",
     *        "targetCatalogVersion": "Online"
     *      }
     *    ]
     *  }
     *
     * @param catalogId The Catalog ID
     * @param catalogVersion The Catalog Version name
     *
     * @returns A Promise which returns an object exposing a permissions array containing the catalog version permissions
     */
    @Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag] })
    getCatalogVersionPermissions(
        catalogId: string,
        catalogVersion: string
    ): Promise<CatalogVersionPermission | any> {
        this.validateParams(catalogId, catalogVersion);

        return this.sessionService.getCurrentUsername().then((principal) => {
            const restService = this.restServiceFactory.get<{
                permissionsList: CatalogVersionPermission[];
            }>(this.URI);

            return restService
                .queryByPost({ principalUid: principal }, { catalogId, catalogVersion })
                .then(({ permissionsList }) => permissionsList[0] || {});
        });
    }

    // TODO: When everything has been migrated to typescript it is sufficient enough to remove this validation.
    private validateParams(catalogId: string, catalogVersion: string): void {
        if (!catalogId) {
            throw new Error('catalog.version.permission.service.catalogid.is.required');
        }

        if (!catalogVersion) {
            throw new Error('catalog.version.permission.service.catalogversion.is.required');
        }
    }
}
