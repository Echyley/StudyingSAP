import { ISessionService, RestServiceFactory } from 'smarteditcommons';
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
export declare class CatalogVersionPermissionRestService {
    private restServiceFactory;
    private sessionService;
    private readonly URI;
    constructor(restServiceFactory: RestServiceFactory, sessionService: ISessionService);
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
    getCatalogVersionPermissions(catalogId: string, catalogVersion: string): Promise<CatalogVersionPermission | any>;
    private validateParams;
}
