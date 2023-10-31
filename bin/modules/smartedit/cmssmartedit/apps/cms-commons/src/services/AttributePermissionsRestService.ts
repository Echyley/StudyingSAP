/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PermissionListRestServiceResponse } from 'cmscommons/dtos';
import { difference } from 'lodash';
import {
    authorizationEvictionTag,
    rarelyChangingContent,
    Cached,
    IPermissionsRestServicePair,
    IPermissionsRestServiceResult,
    IRestService,
    IRestServiceFactory,
    ISessionService,
    TypedMap,
    LogService
} from 'smarteditcommons';

/**
 * An enum type representing available attribute permission names for a given item
 */
export enum AttributePermissionNames {
    READ = 'read',
    CHANGE = 'change'
}

/**
 * Pair to keep track of an attribute and its enclosing type.
 */
interface TypeAttributePair {
    type: string;
    attribute: string;
}

/**
 * Rest Service to retrieve attribute permissions.
 */
@Injectable()
export class AttributePermissionsRestService {
    private readonly ATTRIBUTE_PERMISSIONS_URI =
        '/permissionswebservices/v1/permissions/attributes/search';
    private readonly permissionRestService: IRestService<PermissionListRestServiceResponse>;

    constructor(
        restServiceFactory: IRestServiceFactory,
        private sessionService: ISessionService,
        private logService: LogService
    ) {
        this.permissionRestService = restServiceFactory.get<PermissionListRestServiceResponse>(
            this.ATTRIBUTE_PERMISSIONS_URI
        );
    }

    /**
     * Determines if the current user has READ access to the given attributes in the given type.
     *
     * @param type The type enclosing the attributes for which to evaluate their read permissions.
     * @param attributeNames The names of the attributes for which to evaluate their read permissions.
     * @returns A promise that resolves to a TypedMap object with key (the attribute name) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    public hasReadPermissionOnAttributesInType(
        type: string,
        attributeNames: string[]
    ): Promise<TypedMap<boolean>> {
        return this.getPermissionsForAttributesAndNameByType(
            type,
            attributeNames,
            AttributePermissionNames.READ
        );
    }

    /**
     * Determines if the current user has CHANGE access to the given attributes in the given type.
     *
     * @param type The type enclosing the attributes for which to evaluate their change permissions.
     * @param attributeNames The names of the attributes for which to evaluate their change permissions.
     * @returns A promise that resolves to a TypedMap object with key (the attribute name) and
     * value (true if the user has READ access to the type or false otherwise).
     */
    public hasChangePermissionOnAttributesInType(
        type: string,
        attributeNames: string[]
    ): Promise<TypedMap<boolean>> {
        return this.getPermissionsForAttributesAndNameByType(
            type,
            attributeNames,
            AttributePermissionNames.CHANGE
        );
    }

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
    @Cached({ actions: [rarelyChangingContent], tags: [authorizationEvictionTag] })
    private async getAllPermissionsForAttributes(
        attributes: string[]
    ): Promise<IPermissionsRestServiceResult[]> {
        if (attributes.length <= 0) {
            return [];
        }

        const user = await this.sessionService.getCurrentUsername();
        if (!user) {
            return [];
        }

        try {
            const result = await this.permissionRestService.queryByPost(
                { principalUid: user },
                {
                    attributes: attributes.join(','),
                    permissionNames:
                        AttributePermissionNames.CHANGE + ',' + AttributePermissionNames.READ
                }
            );
            return result.permissionsList || [];
        } catch (error) {
            if (error) {
                this.logService.error(
                    `AttributePermissionsRestService - couldn't retrieve attribute permissions ${attributes}`
                );
            }
            return [];
        }
    }

    private getPermissionsForAttributesAndNameByType(
        type: string,
        attributes: string[],
        permissionName: string
    ): Promise<TypedMap<boolean>> {
        const convertedAttributeNames = attributes.map((attr: string) => type + '.' + attr);
        return this.getPermissionsForAttributesAndName(
            convertedAttributeNames,
            permissionName
        ).then(
            (attributePermissionsByTypeMap: TypedMap<TypedMap<boolean>>) =>
                attributePermissionsByTypeMap[type]
        );
    }

    private async getPermissionsForAttributesAndName(
        attributes: string[],
        permissionName: string
    ): Promise<TypedMap<TypedMap<boolean>>> {
        const result = await this.getAllPermissionsForAttributes(attributes);
        const allPermissions = this.concatPermissionsNotFound(attributes, result);
        return allPermissions.reduce(
            (
                attributePermissionsByTypeMap: TypedMap<TypedMap<boolean>>,
                permissionsResult: IPermissionsRestServiceResult
            ) => {
                if (permissionsResult.permissions) {
                    const typeAttributePair = this.parsePermissionsResultId(permissionsResult.id);
                    if (!attributePermissionsByTypeMap[typeAttributePair.type]) {
                        attributePermissionsByTypeMap[typeAttributePair.type] = {};
                    }

                    attributePermissionsByTypeMap[typeAttributePair.type][
                        typeAttributePair.attribute
                    ] = this.getPermissionByNameFromResult(permissionsResult, permissionName);
                }

                return attributePermissionsByTypeMap;
            },
            {}
        );
    }

    private parsePermissionsResultId(id: string): TypeAttributePair {
        const tokens = id.split('.');
        if (tokens.length !== 2) {
            throw new Error(
                'AttributePermissionsRestService - Received invalid attribute permissions'
            );
        }

        return {
            type: tokens[0],
            attribute: tokens[1]
        };
    }

    private getPermissionByNameFromResult(
        permissionsResult: IPermissionsRestServiceResult,
        permissionName: string
    ): boolean {
        const foundPermission = permissionsResult.permissions.find(
            (permission: IPermissionsRestServicePair) => permission.key === permissionName
        );
        return JSON.parse(foundPermission.value) as boolean;
    }

    private concatPermissionsNotFound(
        attributes: string[],
        permissionsFound: IPermissionsRestServiceResult[]
    ): IPermissionsRestServiceResult[] {
        const permissionKeysFound = permissionsFound.map(
            (permission: IPermissionsRestServiceResult) => permission.id
        );
        const permissionKeysNotFound = difference(attributes, permissionKeysFound);

        return permissionsFound.concat(
            permissionKeysNotFound.map((key: string) => ({
                id: key,
                permissions: [
                    {
                        key: AttributePermissionNames.READ,
                        value: 'false'
                    },
                    {
                        key: AttributePermissionNames.CHANGE,
                        value: 'false'
                    }
                ]
            }))
        );
    }
}
