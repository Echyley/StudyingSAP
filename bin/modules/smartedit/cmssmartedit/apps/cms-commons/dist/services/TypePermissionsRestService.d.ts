import { ISessionService, TypedMap, LogService, IRestServiceFactory } from 'smarteditcommons';
/**
 * An enum type representing available type permission names for a given item
 */
export declare enum TypePermissionNames {
    CREATE = "create",
    READ = "read",
    CHANGE = "change",
    REMOVE = "remove"
}
/**
 * Rest Service to retrieve the type permissions.
 */
export declare class TypePermissionsRestService {
    private logService;
    private sessionService;
    private readonly URI;
    private readonly resource;
    constructor(logService: LogService, sessionService: ISessionService, restServiceFactory: IRestServiceFactory);
    /**
     * Determines if the current user has CREATE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has CREATE access to the type or false otherwise).
     */
    hasCreatePermissionForTypes(types: string[]): Promise<TypedMap<boolean>>;
    /**
     * Determines if the current user has READ access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    hasReadPermissionForTypes(types: string[]): Promise<TypedMap<boolean>>;
    /**
     * Determines if the current user has CHANGE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has CHANGE access to the type or false otherwise).
     */
    hasUpdatePermissionForTypes(types: string[]): Promise<TypedMap<boolean>>;
    /**
     * Determines if the current user has REMOVE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has REMOVE access to the type or false otherwise).
     */
    hasDeletePermissionForTypes(types: string[]): Promise<TypedMap<boolean>>;
    /**
     * Determines if the current user has READ, CREATE, CHANGE, REMOVE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap of TypedMap object with key (the code) and
     * value (true if the user has corresponding access to the type or false otherwise).
     * {
     *  "typeA": {"read": true, "change": false, "create": true, "remove": true},
     *  "typeB": {"read": true, "change": false, "create": true, "remove": false}
     * }
     */
    hasAllPermissionsForTypes(types: string[]): Promise<TypedMap<TypedMap<boolean>>>;
    private getAllPermissionsForTypes;
    private getPermissionByNameAndResult;
    private getPermissionsForTypesAndName;
}
