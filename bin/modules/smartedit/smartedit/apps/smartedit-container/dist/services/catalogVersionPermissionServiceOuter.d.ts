import { ICatalogService, ICatalogVersionPermissionService } from 'smarteditcommons';
import { CatalogVersionPermissionRestService } from './CatalogVersionPermissionRestService';
declare enum PERMISSION_TYPES {
    READ = "read",
    WRITE = "write"
}
export declare class CatalogVersionPermissionService extends ICatalogVersionPermissionService {
    private catalogVersionPermissionRestService;
    private catalogService;
    constructor(catalogVersionPermissionRestService: CatalogVersionPermissionRestService, catalogService: ICatalogService);
    hasPermission(accessType: PERMISSION_TYPES, catalogId: string, catalogVersion: string, siteId?: string): Promise<boolean>;
    hasSyncPermissionFromCurrentToActiveCatalogVersion(): Promise<boolean>;
    hasSyncPermissionToActiveCatalogVersion(catalogId: string, catalogVersion: string): Promise<boolean>;
    hasSyncPermission(catalogId: string, sourceCatalogVersion: string, targetCatalogVersion: string): Promise<boolean>;
    hasWritePermissionOnCurrent(): Promise<boolean>;
    hasReadPermissionOnCurrent(): Promise<boolean>;
    hasWritePermission(catalogId: string, catalogVersion: string): Promise<boolean>;
    hasReadPermission(catalogId: string, catalogVersion: string): Promise<boolean>;
    /**
     * if in the context of an experience AND the catalogVersion is the active one, then permissions should be ignored in read mode
     */
    private shouldIgnoreCatalogPermissions;
    /**
     * Verifies whether current user has write or read permission for current catalog version.
     * @param {String} accessType
     */
    private hasCurrentCatalogPermission;
    private isCatalogVersionPermissionResponse;
}
export {};
