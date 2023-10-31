import { CrossFrameEventService, IPermissionService, LogService, MultiNamePermissionContext, Permission, PermissionContext, Rule, RuleNames, SystemEventService, TypedMap } from 'smarteditcommons';
/**
 * The name used to register the default rule.
 */
export declare const DEFAULT_DEFAULT_RULE_NAME = "se.permission.service.default.rule";
export declare type RulePermissionNames = TypedMap<PermissionContext[]>;
export declare class PermissionService extends IPermissionService {
    private logService;
    private systemEventService;
    private crossFrameEventService;
    private static rules;
    private static permissionsRegistry;
    private static cachedResults;
    static resetForTests(): void;
    private static hasCacheRegion;
    private static getCacheRegion;
    constructor(logService: LogService, systemEventService: SystemEventService, crossFrameEventService: CrossFrameEventService);
    getPermission(permissionName: string): Permission;
    unregisterDefaultRule(): void;
    registerPermission(permission: Permission): void;
    hasCachedResult(ruleName: string, key: string): boolean;
    clearCache(): void;
    isPermitted(permissions: MultiNamePermissionContext[]): Promise<boolean>;
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
    protected _addRulePromise(rulePromises: TypedMap<Promise<boolean>>, rulePermissionNames: RulePermissionNames, ruleName: string): void;
    /**
     * This method validates a permission name. Permission names need to be prefixed by at least one
     * namespace followed by a "." character to be valid.
     *
     * Example: se.mynamespace is valid.
     * Example: mynamespace is not valid.
     */
    protected _isPermissionNameValid(permissionName: string): boolean;
    /**
     * This method returns an object that maps rule names to promises.
     */
    protected _getRulePromises(rulePermissionNames: RulePermissionNames): TypedMap<Promise<boolean>>;
    /**
     * This method returns true if a default rule is already registered.
     *
     * @returns true if the default rule has been registered, false otherwise.
     */
    protected _hasDefaultRule(): boolean;
    /**
     * This method returns the rule's cached result for the given key.
     *
     * @param ruleName The name of the rule for which to lookup the cached result.
     * @param key The cached key to lookup..
     *
     * @returns The cached result, if it exists, null otherwise.
     */
    protected _getCachedResult(ruleName: string, key: string): boolean | null;
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
    protected _generateCacheKey(permissions: PermissionContext[]): string;
    /**
     * This method goes through the permission name arrays associated to rule names to remove any duplicate
     * permission names.
     *
     * If one or more permission names with the same context are found in a rule name's permission name array,
     * only one entry is kept.
     */
    protected _removeDuplicatePermissionNames(rulePermissionNames: RulePermissionNames): void;
    /**
     * This method returns an object mapping rule name to permission name arrays.
     *
     * It will iterate through the given permission name object array to extract the permission names and contexts,
     * populate the map and clean it up by removing duplicate permission name and context pairs.
     */
    protected _mapRuleNameToPermissionNames(permissions: MultiNamePermissionContext[]): TypedMap<PermissionContext[]>;
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
    protected _populateRulePermissionNames(rulePermissionNames: RulePermissionNames, permissionName: string, permissionContext: TypedMap<string>): void;
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
    protected _addPermissionName(rulePermissionNames: RulePermissionNames, ruleName: string, permissionName: string, permissionContext: TypedMap<any>): void;
    /**
     * This method returns the rule registered with the given name.
     *
     * @param ruleName The name of the rule to lookup.
     *
     * @returns rule The rule with the given name, undefined otherwise.
     */
    protected _getRule(ruleName: string): RuleNames;
    protected _validationRule(ruleConfiguration: Rule): void;
    protected _validatePermission(permissionConfiguration: Permission): void;
    protected _updateCache(rulePermissionNames: RulePermissionNames, permissionResults: TypedMap<boolean>): void;
    protected _addCachedResult(ruleName: string, key: string, result: boolean): void;
    protected _registerRule(ruleConfiguration: Rule): void;
    protected _registerDefaultRule(ruleConfiguration: Rule): void;
    protected _callRuleVerify(ruleKey: string, permissionNameObjs: PermissionContext[]): Promise<boolean>;
    protected _registerEventHandlers(): void;
    protected _remoteCallRuleVerify(name: string, permissionNameObjs: PermissionContext[]): Promise<boolean>;
}
