/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    CrossFrameEventService,
    EVENT_PERSPECTIVE_CHANGED,
    EVENTS,
    GatewayProxied,
    IPermissionService,
    LogService,
    MultiNamePermissionContext,
    Permission,
    PermissionContext,
    Rule,
    RuleNames,
    SystemEventService,
    TypedMap
} from 'smarteditcommons';

/**
 * The name used to register the default rule.
 */
export const DEFAULT_DEFAULT_RULE_NAME = 'se.permission.service.default.rule';
export type RulePermissionNames = TypedMap<PermissionContext[]>;
@GatewayProxied(
    'isPermitted',
    'clearCache',
    'registerPermission',
    'unregisterDefaultRule',
    'registerDefaultRule',
    'registerRule',
    '_registerRule',
    '_remoteCallRuleVerify',
    '_registerDefaultRule'
)
export class PermissionService extends IPermissionService {
    private static rules: RuleNames[] = [];
    private static permissionsRegistry: Permission[] = [];
    private static cachedResults: TypedMap<TypedMap<boolean>> = {};

    public static resetForTests(): void {
        PermissionService.rules = [];
        PermissionService.permissionsRegistry = [];
        PermissionService.cachedResults = {};
    }

    private static hasCacheRegion(ruleName: string): boolean {
        return PermissionService.cachedResults.hasOwnProperty(ruleName);
    }

    private static getCacheRegion(ruleName: string): TypedMap<boolean> {
        return PermissionService.cachedResults[ruleName];
    }

    constructor(
        private logService: LogService,
        private systemEventService: SystemEventService,
        private crossFrameEventService: CrossFrameEventService
    ) {
        super();
        this._registerEventHandlers();
    }

    getPermission(permissionName: string): Permission {
        return PermissionService.permissionsRegistry.find(
            (permission: Permission) => permission.aliases.indexOf(permissionName) > -1
        );
    }

    unregisterDefaultRule(): void {
        const defaultRule = this._getRule(DEFAULT_DEFAULT_RULE_NAME);

        if (defaultRule) {
            PermissionService.rules.splice(PermissionService.rules.indexOf(defaultRule), 1);
        }
    }

    registerPermission(permission: Permission): void {
        this._validatePermission(permission);

        PermissionService.permissionsRegistry.push({
            aliases: permission.aliases,
            rules: permission.rules
        });
    }

    hasCachedResult(ruleName: string, key: string): boolean {
        return (
            PermissionService.hasCacheRegion(ruleName) &&
            PermissionService.getCacheRegion(ruleName).hasOwnProperty(key)
        );
    }

    clearCache(): void {
        PermissionService.cachedResults = {};
        this.crossFrameEventService.publish(EVENTS.PERMISSION_CACHE_CLEANED).catch((reason) => {
            this.logService.debug(`PermissionService - clearCache: ${reason}`);
        });
    }

    isPermitted(permissions: MultiNamePermissionContext[]): Promise<boolean> {
        const rulePermissionNames = this._mapRuleNameToPermissionNames(permissions);
        const rulePromises = this._getRulePromises(rulePermissionNames);

        const names = Object.keys(rulePromises);
        const promises: Promise<boolean>[] = names.map((key: string) => rulePromises[key]);

        const onSuccess = (permissionResults: boolean[]): boolean => {
            const result = names.reduce((acc: TypedMap<boolean>, name, index) => {
                acc[name] = permissionResults[index];
                return acc;
            }, {});

            this._updateCache(rulePermissionNames, result);
            return true;
        };

        const onError = (result: any): any => {
            if (result === false) {
                return result;
            }
            this.logService.error(result);
            return result === undefined ? false : result;
        };

        return Promise.all(promises).then(onSuccess, onError);
    }

    /**
     * This method adds a promise obtained by calling the pre-configured rule.verify function to the rulePromises
     * map if the result does not exist in the rule's cache. Otherwise, a promise that contains the cached result
     * is added.
     *
     * The promise obtained from the rule.verify function is chained to allow short-circuiting the permission
     * verification process. If a rule resolves with a false result or with an error, the chained promise is
     * rejected to stop the verification process without waiting for all other rules to resolve.
     *
     * @param rulePromises An object that maps rule names to promises.
     * @param rulePermissionNames An object that maps rule names to permission name arrays.
     * @param ruleName The name of the rule to verify.
     */
    protected _addRulePromise(
        rulePromises: TypedMap<Promise<boolean>>,
        rulePermissionNames: RulePermissionNames,
        ruleName: string
    ): void {
        const rule = this._getRule(ruleName);
        const permissionNameObjs = rulePermissionNames[ruleName];
        const cacheKey = this._generateCacheKey(permissionNameObjs);

        let rulePromise;

        if (this.hasCachedResult(ruleName, cacheKey)) {
            rulePromise = Promise.resolve(this._getCachedResult(ruleName, cacheKey));
        } else {
            rulePromise = this._callRuleVerify(rule.names.join('-'), permissionNameObjs).then(
                (isPermitted: boolean) =>
                    isPermitted ? Promise.resolve(true) : Promise.reject(false)
            );
        }

        rulePromises[ruleName] = rulePromise;
    }

    /**
     * This method validates a permission name. Permission names need to be prefixed by at least one
     * namespace followed by a "." character to be valid.
     *
     * Example: se.mynamespace is valid.
     * Example: mynamespace is not valid.
     */
    protected _isPermissionNameValid(permissionName: string): boolean {
        const checkNameSpace = /^[A-Za-z0-9_\-]+\.[A-Za-z0-9_\-\.]+/;
        return checkNameSpace.test(permissionName);
    }

    /**
     * This method returns an object that maps rule names to promises.
     */
    protected _getRulePromises(
        rulePermissionNames: RulePermissionNames
    ): TypedMap<Promise<boolean>> {
        const rulePromises = {};

        Object.keys(rulePermissionNames).forEach((ruleName: string) => {
            this._addRulePromise.call(this, rulePromises, rulePermissionNames, ruleName);
        });

        return rulePromises;
    }

    /**
     * This method returns true if a default rule is already registered.
     *
     * @returns true if the default rule has been registered, false otherwise.
     */
    protected _hasDefaultRule(): boolean {
        return !!this._getRule(DEFAULT_DEFAULT_RULE_NAME);
    }

    /**
     * This method returns the rule's cached result for the given key.
     *
     * @param ruleName The name of the rule for which to lookup the cached result.
     * @param key The cached key to lookup..
     *
     * @returns The cached result, if it exists, null otherwise.
     */
    protected _getCachedResult(ruleName: string, key: string): boolean | null {
        return PermissionService.hasCacheRegion(ruleName)
            ? PermissionService.getCacheRegion(ruleName)[key]
            : null;
    }

    /**
     * This method generates a key to store a rule's result for a given combination of
     * permissions in its cache. It is done by sorting the list of permissions by name
     * and serializing it.
     *
     * @param permissions A list of permissions with a name and context.
     *
     * [{
     *     name: "permission.name"
     *     context: {
     *         key: "value"
     *     }
     * }]
     *
     * @returns The serialized sorted list of permissions.
     */
    protected _generateCacheKey(permissions: PermissionContext[]): string {
        return JSON.stringify(
            permissions.sort((permissionA, permissionB) => {
                const nameA = permissionA.name;
                const nameB = permissionB.name;

                return nameA === nameB ? 0 : nameA < nameB ? -1 : 1;
            })
        );
    }

    /**
     * This method goes through the permission name arrays associated to rule names to remove any duplicate
     * permission names.
     *
     * If one or more permission names with the same context are found in a rule name's permission name array,
     * only one entry is kept.
     */
    protected _removeDuplicatePermissionNames(rulePermissionNames: RulePermissionNames): void {
        Object.keys(rulePermissionNames).forEach((ruleName: string) => {
            rulePermissionNames[ruleName] = rulePermissionNames[ruleName].filter(
                (currentPermission) => {
                    const existingPermission = rulePermissionNames[ruleName].find(
                        (permission) => permission.name === currentPermission.name
                    );

                    if (existingPermission === currentPermission) {
                        return true;
                    } else {
                        const existingPermissionContext = existingPermission.context;
                        const currentPermissionContext = currentPermission.context;

                        return (
                            JSON.stringify(existingPermissionContext) !==
                            JSON.stringify(currentPermissionContext)
                        );
                    }
                }
            );
        });
    }

    /**
     * This method returns an object mapping rule name to permission name arrays.
     *
     * It will iterate through the given permission name object array to extract the permission names and contexts,
     * populate the map and clean it up by removing duplicate permission name and context pairs.
     */
    protected _mapRuleNameToPermissionNames(
        permissions: MultiNamePermissionContext[]
    ): TypedMap<PermissionContext[]> {
        const rulePermissionNames = {};

        permissions.forEach((permission: MultiNamePermissionContext) => {
            if (!permission.names) {
                throw Error('Requested Permission requires at least one name');
            }

            const permissionNames = permission.names;
            const permissionContext = permission.context;

            permissionNames.forEach((permissionName: string) => {
                this._populateRulePermissionNames(
                    rulePermissionNames,
                    permissionName,
                    permissionContext
                );
            });
        });

        this._removeDuplicatePermissionNames(rulePermissionNames);

        return rulePermissionNames;
    }

    /**
     * This method will populate rulePermissionNames with the rules associated to the permission with the given
     * permissionName.
     *
     * If no permission is registered with the given permissionName and a default rule is registered, the default
     * rule is added to rulePermissionNames.
     *
     * If no permission is registered with the given permissionName and no default rule is registered, an error
     * is thrown.
     */
    protected _populateRulePermissionNames(
        rulePermissionNames: RulePermissionNames,
        permissionName: string,
        permissionContext: TypedMap<string>
    ): void {
        const permission = this.getPermission(permissionName);
        const permissionHasRules =
            !!permission && !!permission.rules && permission.rules.length > 0;

        if (permissionHasRules) {
            permission.rules.forEach((ruleName: string) => {
                this._addPermissionName(
                    rulePermissionNames,
                    ruleName,
                    permissionName,
                    permissionContext
                );
            });
        } else if (this._hasDefaultRule()) {
            this._addPermissionName(
                rulePermissionNames,
                DEFAULT_DEFAULT_RULE_NAME,
                permissionName,
                permissionContext
            );
        } else {
            throw Error('Permission has no rules');
        }
    }

    /**
     * This method will add an object with the permissionName and permissionContext to rulePermissionNames.
     *
     * Since rules can have multiple names, the map will use the first name in the rule's name list as its key.
     * This way, each rule will be called only once for every permission name and context.
     *
     * If the rule associated to a given rule name is already in rulePermissionNames, the permission will be
     * appended to the associated array. Otherwise, the rule name is added to the map and its permission name array
     * is created.
     */
    protected _addPermissionName(
        rulePermissionNames: RulePermissionNames,
        ruleName: string,
        permissionName: string,
        permissionContext: TypedMap<any>
    ): void {
        const rule = this._getRule(ruleName);

        if (!rule) {
            throw Error('Permission found but no rule found named: ' + ruleName);
        }

        ruleName = rule.names[0];

        if (!rulePermissionNames.hasOwnProperty(ruleName)) {
            rulePermissionNames[ruleName] = [];
        }

        rulePermissionNames[ruleName].push({
            name: permissionName,
            context: permissionContext
        });
    }

    /**
     * This method returns the rule registered with the given name.
     *
     * @param ruleName The name of the rule to lookup.
     *
     * @returns rule The rule with the given name, undefined otherwise.
     */
    protected _getRule(ruleName: string): RuleNames {
        return PermissionService.rules.find((rule) => rule.names.indexOf(ruleName) > -1);
    }

    protected _validationRule(ruleConfiguration: Rule): void {
        ruleConfiguration.names.forEach((ruleName: string) => {
            if (this._getRule(ruleName)) {
                throw Error('Rule already exists: ' + ruleName);
            }
        });
    }

    protected _validatePermission(permissionConfiguration: Permission): void {
        if (!(permissionConfiguration.aliases instanceof Array)) {
            throw Error('Permission aliases must be an array');
        }

        if (permissionConfiguration.aliases.length < 1) {
            throw Error('Permission requires at least one alias');
        }

        if (!(permissionConfiguration.rules instanceof Array)) {
            throw Error('Permission rules must be an array');
        }

        if (permissionConfiguration.rules.length < 1) {
            throw Error('Permission requires at least one rule');
        }

        permissionConfiguration.aliases.forEach((permissionName: string) => {
            if (this.getPermission(permissionName)) {
                throw Error('Permission already exists: ' + permissionName);
            }

            if (!this._isPermissionNameValid(permissionName)) {
                throw Error('Permission aliases must be prefixed with namespace and a full stop');
            }
        });

        permissionConfiguration.rules.forEach((ruleName: string) => {
            if (!this._getRule(ruleName)) {
                throw Error('Permission found but no rule found named: ' + ruleName);
            }
        });
    }

    protected _updateCache(
        rulePermissionNames: RulePermissionNames,
        permissionResults: TypedMap<boolean>
    ): void {
        Object.keys(permissionResults).forEach((ruleName: string) => {
            const cacheKey = this._generateCacheKey(rulePermissionNames[ruleName]);
            const cacheValue = permissionResults[ruleName];

            this._addCachedResult(ruleName, cacheKey, cacheValue);
        });
    }

    protected _addCachedResult(ruleName: string, key: string, result: boolean): void {
        if (!PermissionService.hasCacheRegion(ruleName)) {
            PermissionService.cachedResults[ruleName] = {};
        }

        PermissionService.cachedResults[ruleName][key] = result;
    }

    protected _registerRule(ruleConfiguration: Rule): void {
        this._validationRule(ruleConfiguration);

        if (
            ruleConfiguration.names &&
            ruleConfiguration.names.length &&
            ruleConfiguration.names.indexOf(DEFAULT_DEFAULT_RULE_NAME) > -1
        ) {
            throw Error('Register default rule using permissionService.registerDefaultRule()');
        }

        PermissionService.rules.push({
            names: ruleConfiguration.names
        });
    }

    protected _registerDefaultRule(ruleConfiguration: Rule): void {
        this._validationRule(ruleConfiguration);

        if (
            ruleConfiguration.names &&
            ruleConfiguration.names.length &&
            ruleConfiguration.names.indexOf(DEFAULT_DEFAULT_RULE_NAME) === -1
        ) {
            throw Error('Default rule name must be DEFAULT_RULE_NAME');
        }

        PermissionService.rules.push({
            names: ruleConfiguration.names
        });
    }

    protected _callRuleVerify(
        ruleKey: string,
        permissionNameObjs: PermissionContext[]
    ): Promise<boolean> {
        if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
            return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
        }

        // ask inner application for verify function.
        return this._remoteCallRuleVerify(ruleKey, permissionNameObjs);
    }

    protected _registerEventHandlers(): void {
        this.crossFrameEventService.subscribe(EVENTS.USER_HAS_CHANGED, this.clearCache.bind(this));
        this.systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, this.clearCache.bind(this));
        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, this.clearCache.bind(this));
        this.crossFrameEventService.subscribe(
            EVENT_PERSPECTIVE_CHANGED,
            this.clearCache.bind(this)
        );
    }

    protected _remoteCallRuleVerify(
        name: string,
        permissionNameObjs: PermissionContext[]
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}
