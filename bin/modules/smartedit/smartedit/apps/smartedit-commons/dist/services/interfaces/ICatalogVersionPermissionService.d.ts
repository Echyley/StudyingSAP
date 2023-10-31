/**
 * Provides a logic that allows to verify read and write permissions for a particular catalog version.
 */
export declare abstract class ICatalogVersionPermissionService {
    /**
     * Verifies whether current user has write permission for provided catalogId and catalogVersion.
     */
    hasWritePermission(catalogId: string, catalogVersion: string): Promise<boolean>;
    /**
     * Verifies whether current user has read permission for provided catalogId and catalogVersion.
     */
    hasReadPermission(catalogId: string, catalogVersion: string): Promise<boolean>;
    /**
     * Verifies whether current user has write permission for current catalog version.
     */
    hasWritePermissionOnCurrent(): Promise<boolean>;
    /**
     * Verifies whether current user has read permission for current catalog version.
     */
    hasReadPermissionOnCurrent(): Promise<boolean>;
    /**
     * Verifies whether current user has sync permission for provided catalogId, source and target catalog versions.
     */
    hasSyncPermission(catalogId: string, sourceCatalogVersion: string, targetCatalogVersion: string): Promise<boolean>;
    /**
     * Verifies whether current user has sync permission for current catalog version.
     */
    hasSyncPermissionFromCurrentToActiveCatalogVersion(): Promise<boolean>;
    /**
     * Verifies whether current user has sync permission for provided catalogId and catalog version.
     */
    hasSyncPermissionToActiveCatalogVersion(catalogId: string, catalogVersion: string): Promise<boolean>;
}
