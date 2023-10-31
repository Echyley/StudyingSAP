import { IPermissionService, LogService, PermissionContext } from 'smarteditcommons';
export declare class PermissionService extends IPermissionService {
    private logService;
    constructor(logService: LogService);
    _remoteCallRuleVerify(ruleKey: string, permissionNameObjs: PermissionContext[]): Promise<boolean>;
}
