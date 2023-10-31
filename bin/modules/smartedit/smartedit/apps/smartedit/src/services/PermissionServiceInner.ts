/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    GatewayProxied,
    IPermissionService,
    LogService,
    PermissionContext
} from 'smarteditcommons';

@GatewayProxied()
export class PermissionService extends IPermissionService {
    constructor(private logService: LogService) {
        super();
    }

    _remoteCallRuleVerify(
        ruleKey: string,
        permissionNameObjs: PermissionContext[]
    ): Promise<boolean> {
        if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
            return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
        }

        this.logService.warn(
            'could not call rule verify function for rule key: ' +
                ruleKey +
                ', it was not found in the iframe'
        );
        return null;
    }
}
