import { IRestServiceFactory, ISessionService, TypedMap, LogService } from 'smarteditcommons';
/**
 * An enum type representing available attribute permission names for a given item
 */
export declare enum AttributePermissionNames {
    READ = "read",
    CHANGE = "change"
}
/**
 * Rest Service to retrieve attribute permissions.
 */
export declare class AttributePermissionsRestService {
    private sessionService;
    private logService;
    private readonly ATTRIBUTE_PERMISSIONS_URI;
    private readonly permissionRestService;
    constructor(restServiceFactory: IRestServiceFactory, sessionService: ISessionService, logService: LogService);
    /**
     * Determines if the current user has READ access to the given attributes in the given type.
     *
     * @param type The type enclosing the attributes for which to evaluate their read permissions.
     * @param attributeNames The names of the attributes for which to evaluate their read permissions.
     * @returns A promise that resolves to a TypedMap object with key (the attribute name) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    hasReadPermissionOnAttributesInType(type: string, attributeNames: string[]): Promise<TypedMap<boolean>>;
    /**
     * Determines if the current user has CHANGE access to the given attributes in the given type.
     *
     * @param type The type enclosing the attributes for which to evaluate their change permissions.
     * @param attributeNames The names of the attributes for which to evaluate their change permissions.
     * @returns A promise that resolves to a TypedMap object with key (the attribute name) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    hasChangePermissionOnAttributesInType(type: string, attributeNames: string[]): Promise<TypedMap<boolean>>;
    /**
     * @internal
     *
     * This method retrieves ALL the permissions the current user has on the given attributes. Attributes are expected with the following format:
     * - type.attribute name
     * For example, for an attribute called approvalStatus within the type ContentPage, the given attribute must be:
     * - ContentPage.approvalStatus
     *
     * Note: This method is cached.
     *
     * @param attributes The list of attributes for which to retrieve permissions
     * @returns A promise that resolves to a list of IPermissionsRestServiceResult, each of which
     * represents the permissions of one of the given attributes.
     */
    private getAllPermissionsForAttributes;
    private getPermissionsForAttributesAndNameByType;
    private getPermissionsForAttributesAndName;
    private parsePermissionsResultId;
    private getPermissionByNameFromResult;
    private concatPermissionsNotFound;
}
