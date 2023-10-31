/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ISessionService, LogService } from '@smart/utils';
import { IPermissionsRestServicePair, IPermissionsRestServiceResult } from 'smarteditcommons/dtos';
import { PermissionsRestService } from './rest';

/**
 * This service makes calls to the Global Permissions REST API to check if the current user was
 * granted certain permissions.
 */
@Injectable()
export class AuthorizationService {
    constructor(
        private readonly logService: LogService,
        private readonly sessionService: ISessionService,
        private readonly permissionsRestService: PermissionsRestService
    ) {}

    /**
     * This method checks if the current user is granted the given global permissions.
     *
     * @param permissionNames The list of global permissions to check.
     *
     * @returns True if the user is granted all of the given permissions, false otherwise
     *
     * ### Throws
     *
     * - Will throw an error if the permissionNames array is empty.
     */
    public hasGlobalPermissions(permissionNames: string[]): Promise<boolean> {
        if (!permissionNames.length || permissionNames.length < 1) {
            throw new Error('permissionNames must be a non-empty array');
        }

        const onSuccess = (permissions: IPermissionsRestServiceResult): boolean =>
            this.mergePermissionResults(permissions, permissionNames);

        const onError = (): boolean => {
            this.logService.error(
                'AuthorizationService - Failed to determine authorization for the following permissions: ' +
                    permissionNames.toString()
            );
            return false;
        };

        return this.getPermissions(permissionNames).then(onSuccess, onError);
    }

    /*
     * This method will look for the result for the given permission name. If found, it is
     * verified that it has been granted. Otherwise, the method will return false.
     */
    private getPermissionResult(
        permissionResults: IPermissionsRestServiceResult,
        permissionName: string
    ): boolean {
        const permission: IPermissionsRestServicePair = permissionResults.permissions.find(
            (result: IPermissionsRestServicePair) =>
                result.key.toLowerCase() === permissionName.toLowerCase()
        );

        return !!permission && permission.value === 'true';
    }

    /*
     * This method merges permission results. It iterates through the list of permission names that
     * were checked and evaluates if the permission is granted. It immediately returns false when
     * it encounters a permission that is denied.
     */
    private mergePermissionResults(
        permissionResults: IPermissionsRestServiceResult,
        permissionNames: string[]
    ): boolean {
        let hasPermission = !!permissionNames && permissionNames.length > 0;
        let index = 0;

        while (hasPermission && index < permissionNames.length) {
            hasPermission =
                hasPermission &&
                this.getPermissionResult(permissionResults, permissionNames[index++]);
        }

        return hasPermission;
    }

    /*
     * This method makes a call to the Global Permissions API with the given permission names
     * and returns the list of results.
     */
    private getPermissions(permissionNames: string[]): Promise<IPermissionsRestServiceResult> {
        return this.sessionService.getCurrentUsername().then((user: string) => {
            if (!user) {
                return { permissions: [] };
            }
            return this.permissionsRestService.get({
                user,
                permissionNames: permissionNames.join(',')
            });
        });
    }
}
