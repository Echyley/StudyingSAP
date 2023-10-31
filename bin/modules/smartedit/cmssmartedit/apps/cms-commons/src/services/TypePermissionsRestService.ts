/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PermissionListRestServiceResponse } from 'cmscommons/dtos';
import {
    authorizationEvictionTag,
    rarelyChangingContent,
    Cached,
    IPermissionsRestServicePair,
    IPermissionsRestServiceResult,
    IRestService,
    ISessionService,
    TypedMap,
    LogService,
    IRestServiceFactory
} from 'smarteditcommons';

/**
 * An enum type representing available type permission names for a given item
 */
export enum TypePermissionNames {
    CREATE = 'create',
    READ = 'read',
    CHANGE = 'change',
    REMOVE = 'remove'
}

/**
 * Rest Service to retrieve the type permissions.
 */
@Injectable()
export class TypePermissionsRestService {
    private readonly URI = '/permissionswebservices/v1/permissions/types/search';

    private readonly resource: IRestService<PermissionListRestServiceResponse>;

    constructor(
        private logService: LogService,
        private sessionService: ISessionService,
        restServiceFactory: IRestServiceFactory
    ) {
        this.resource = restServiceFactory.get<PermissionListRestServiceResponse>(this.URI);
    }

    /**
     * Determines if the current user has CREATE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has CREATE access to the type or false otherwise).
     */
    public hasCreatePermissionForTypes(types: string[]): Promise<TypedMap<boolean>> {
        return this.getPermissionsForTypesAndName(types, TypePermissionNames.CREATE);
    }

    /**
     * Determines if the current user has READ access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    public hasReadPermissionForTypes(types: string[]): Promise<TypedMap<boolean>> {
        return this.getPermissionsForTypesAndName(types, TypePermissionNames.READ);
    }

    /**
     * Determines if the current user has CHANGE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has CHANGE access to the type or false otherwise).
     */
    public hasUpdatePermissionForTypes(types: string[]): Promise<TypedMap<boolean>> {
        return this.getPermissionsForTypesAndName(types, TypePermissionNames.CHANGE);
    }

    /**
     * Determines if the current user has REMOVE access to the given types.
     *
     * @param types The codes of all types.
     * @returns A promise that resolves to a TypedMap object with key (the code) and
     * value (true if the user has REMOVE access to the type or false otherwise).
     */
    public hasDeletePermissionForTypes(types: string[]): Promise<TypedMap<boolean>> {
        return this.getPermissionsForTypesAndName(types, TypePermissionNames.REMOVE);
    }

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
    public async hasAllPermissionsForTypes(types: string[]): Promise<TypedMap<TypedMap<boolean>>> {
        const initialMap: TypedMap<TypedMap<boolean>> = {};

        const permissionsForTypes = await this.getAllPermissionsForTypes(types);
        return permissionsForTypes.reduce(
            (
                map: TypedMap<TypedMap<boolean>>,
                permissionsResult: IPermissionsRestServiceResult
            ) => {
                if (permissionsResult.permissions) {
                    map[permissionsResult.id] = {};
                    map[permissionsResult.id][TypePermissionNames.READ] =
                        this.getPermissionByNameAndResult(
                            permissionsResult,
                            TypePermissionNames.READ
                        );
                    map[permissionsResult.id][TypePermissionNames.CHANGE] =
                        this.getPermissionByNameAndResult(
                            permissionsResult,
                            TypePermissionNames.CHANGE
                        );
                    map[permissionsResult.id][TypePermissionNames.CREATE] =
                        this.getPermissionByNameAndResult(
                            permissionsResult,
                            TypePermissionNames.CREATE
                        );
                    map[permissionsResult.id][TypePermissionNames.REMOVE] =
                        this.getPermissionByNameAndResult(
                            permissionsResult,
                            TypePermissionNames.REMOVE
                        );
                }
                return map;
            },
            initialMap
        );
    }

    @Cached({ actions: [rarelyChangingContent], tags: [authorizationEvictionTag] })
    private async getAllPermissionsForTypes(
        types: string[]
    ): Promise<IPermissionsRestServiceResult[]> {
        if (types.length === 0) {
            return [];
        }

        const user = await this.sessionService.getCurrentUsername();
        if (!user) {
            return [];
        }

        try {
            const permissionNames = [
                TypePermissionNames.CREATE,
                TypePermissionNames.CHANGE,
                TypePermissionNames.READ,
                TypePermissionNames.REMOVE
            ].join(',');

            const result = await this.resource.queryByPost(
                { principalUid: user },
                { types: types.join(','), permissionNames }
            );

            return result.permissionsList || [];
        } catch (error) {
            if (error) {
                this.logService.error(
                    `TypePermissionsRestService - no composed types ${types} exist`
                );
            }
            return Promise.reject(error);
        }
    }

    private getPermissionByNameAndResult(
        permissionsResult: IPermissionsRestServiceResult,
        permissionName: string
    ): boolean {
        const foundPermission = permissionsResult.permissions.find(
            (permission: IPermissionsRestServicePair) => permission.key === permissionName
        );
        return JSON.parse(foundPermission.value) as boolean;
    }

    private async getPermissionsForTypesAndName(
        types: string[],
        permissionName: string
    ): Promise<TypedMap<boolean>> {
        const permissionsForTypes = await this.getAllPermissionsForTypes(types);
        return permissionsForTypes.reduce(
            (map: TypedMap<boolean>, permissionsResult: IPermissionsRestServiceResult) => {
                if (permissionsResult.permissions) {
                    map[permissionsResult.id] = this.getPermissionByNameAndResult(
                        permissionsResult,
                        permissionName
                    );
                }
                return map;
            },
            {}
        );
    }
}
