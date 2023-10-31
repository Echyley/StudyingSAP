import { TypedMap } from '@smart/utils';
export interface Rule {
    /** The list of names associated to the rule. */
    names: string[];
    /** The verification function of the rule. */
    verify: (permissionObjects?: PermissionContext[]) => Promise<boolean>;
}
export interface Permission {
    /**
     * The list of aliases associated to the permission. A permission alias must be prefixed by at least one
     * namespace followed by a "." character to be valid. i.e. "se.fake.permission"
     */
    aliases: string[];
    /**
     * The list of the names of the rules used to verify.
     */
    rules: string[];
}
export interface MultiNamePermissionContext {
    names: string[];
    context?: TypedMap<any>;
}
export interface PermissionContext {
    name: string;
    context?: TypedMap<any>;
}
export interface RuleNames {
    names: string[];
}
/**
 * The permission service is used to check if a user has been granted certain permissions.
 *
 * It is configured with rules and permissions. A rule is used to execute some logic to determine whether or not
 * the permission should be granted. A permission references a list of rules. In order for a permission to be
 * granted, each rule must be executed successfully and return true.
 */
export declare abstract class IPermissionService {
    ruleVerifyFunctions: TypedMap<{
        verify: (obj: any) => Promise<boolean>;
    }>;
    /**
     * This method clears all cached results in the rules' caches.
     */
    clearCache(): void;
    /**
     * This method returns the registered permission that contains the given name in its
     * array of names.
     *
     * @returns The permission with the given name, undefined otherwise.
     */
    getPermission(permission: string): Permission;
    /**
     * This method checks if a user has been granted certain permissions.
     *
     * It takes an array of permission objects structured as follows:
     *
     * ### Example
     *
     *      {
     *          names: ["permission.aliases"],
     *          context: {
     *              data: "required to check a permission"
     *          }
     *      }
     *
     *
     * @returns A promise that resolves to true if permission is granted, rejects to false if it isn't and rejects on error.
     */
    isPermitted(permissions: MultiNamePermissionContext[]): Promise<boolean>;
    /**
     * This method registers a permission.
     *
     * A permission is defined by a set of aliases and rules. It is verified by its set of rules.
     * The set of aliases is there for convenience, as there may be different permissions
     * that use the same set of rules to be verified. The permission aliases property
     * will resolve if any one alias is in the aliases' array. Calling [isPermitted]{@link IPermissionService#isPermitted}
     * with any of these aliases will use the same permission object, therefore the same
     * combination of rules to check if the user has the appropriate clearance. This reduces the
     * number of permissions you need to register.
     *
     * ### Throws
     *
     * - Will throw an error if the permission has no aliases array
     * - Will throw an error if the permission's aliases array is empty
     * - Will throw an error if the permission has no rules array
     * - Will throw an error if the permission's rule aliases array is empty
     * - Will throw an error if a permission is already registered with a common entry in its array of aliases
     * - Will throw an error if one of the permission's aliases is not name spaced
     * - Will throw an error if no rule is registered with on of the permission's rule names
     */
    registerPermission(permission: Permission): void;
    /**
     * This method registers a rule. These rules can be used by registering permissions that
     * use them to verify if a user has the appropriate clearance.
     *
     * To avoid accidentally overriding the default rule, an error is thrown when attempting
     * to register a rule with the {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     *
     * To register the default rule, see [registerDefaultRule]{@link IPermissionService#registerDefaultRule}.
     *
     * It must return a promise that responds with true, false, or an error.
     *
     * ### Throws
     *
     * - Will throw an error if the list of rule names contains the reserved {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     * - Will throw an error if the rule has no names array.
     * - Will throw an error if the rule's names array is empty.
     * - Will throw an error if the rule has no verify function.
     * - Will throw an error if the rule's verify parameter is not a function.
     * - Will throw an error if a rule is already registered with a common entry in its names array
     */
    registerRule(ruleConfiguration: Rule): void;
    /**
     * This method registers the default rule.
     *
     * The default rule is used when no permission is found for a given permission name when
     * [isPermitted]{@link IPermissionService#isPermitted} is called.
     *
     * ### Throws
     *
     * - Will throw an error if the default rule's names does not contain {@link /smarteditcontainer/miscellaneous/variables.html#DEFAULT_DEFAULT_RULE_NAME default rule name}.
     * - Will throw an error if the default rule has no names array.
     * - Will throw an error if the default rule's names array is empty.
     * - Will throw an error if the default rule has no verify function.
     * - Will throw an error if the default rule's verify parameter is not a function.
     * - Will throw an error if a rule is already registered with a common entry in its names array
     */
    registerDefaultRule(ruleConfiguration: Rule): void;
    unregisterDefaultRule(): void;
    protected _registerRule(ruleConfiguration: Rule): void;
    protected _registerDefaultRule(ruleConfiguration: Rule): void;
}
