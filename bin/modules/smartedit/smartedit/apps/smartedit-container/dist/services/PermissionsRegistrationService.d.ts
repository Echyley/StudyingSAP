import { IPermissionService, ISharedDataService } from 'smarteditcommons';
/** @internal */
export declare class PermissionsRegistrationService {
    private permissionService;
    private sharedDataService;
    constructor(permissionService: IPermissionService, sharedDataService: ISharedDataService);
    /**
     * Method containing registrations of rules and permissions to be used in smartedit workspace
     */
    registerRulesAndPermissions(): void;
}
