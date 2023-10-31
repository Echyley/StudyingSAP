import { ISessionService, LogService } from '@smart/utils';
import { PermissionsRestService } from './rest';
/**
 * This service makes calls to the Global Permissions REST API to check if the current user was
 * granted certain permissions.
 */
export declare class AuthorizationService {
    private readonly logService;
    private readonly sessionService;
    private readonly permissionsRestService;
    constructor(logService: LogService, sessionService: ISessionService, permissionsRestService: PermissionsRestService);
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
    hasGlobalPermissions(permissionNames: string[]): Promise<boolean>;
    private getPermissionResult;
    private mergePermissionResults;
    private getPermissions;
}
