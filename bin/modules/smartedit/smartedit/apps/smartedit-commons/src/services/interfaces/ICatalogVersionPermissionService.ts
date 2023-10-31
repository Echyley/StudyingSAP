/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides a logic that allows to verify read and write permissions for a particular catalog version.
 */
export abstract class ICatalogVersionPermissionService {
    /**
     * Verifies whether current user has write permission for provided catalogId and catalogVersion.
     */
    hasWritePermission(catalogId: string, catalogVersion: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has read permission for provided catalogId and catalogVersion.
     */
    hasReadPermission(catalogId: string, catalogVersion: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has write permission for current catalog version.
     */
    hasWritePermissionOnCurrent(): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has read permission for current catalog version.
     */
    hasReadPermissionOnCurrent(): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has sync permission for provided catalogId, source and target catalog versions.
     */
    hasSyncPermission(
        catalogId: string,
        sourceCatalogVersion: string,
        targetCatalogVersion: string
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has sync permission for current catalog version.
     */
    hasSyncPermissionFromCurrentToActiveCatalogVersion(): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Verifies whether current user has sync permission for provided catalogId and catalog version.
     */
    hasSyncPermissionToActiveCatalogVersion(
        catalogId: string,
        catalogVersion: string
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}
