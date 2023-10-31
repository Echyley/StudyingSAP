/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CMSItemStructure, CMSItemStructureField, StructureTypeCategory } from 'cmscommons';
import { IRestService, RestServiceFactory, TYPES_RESOURCE_URI } from 'smarteditcommons';

/**
 * Used for fetching CMS Item Structures.
 */
@Injectable()
export class TypeStructureRestService {
    private readonly structureRestService: IRestService<
        { componentTypes: CMSItemStructure[] } | CMSItemStructure
    >;

    constructor(restServiceFactory: RestServiceFactory) {
        this.structureRestService = restServiceFactory.get(TYPES_RESOURCE_URI);
    }

    /**
     * Fetches the type structure (fields) for CMS pages for a given Page Type.
     */
    public getStructureByType(typeCode: string): Promise<CMSItemStructureField[]> {
        return this.structureRestService
            .getById<CMSItemStructure>(typeCode)
            .then((structure) => structure.attributes);
    }

    /**
     * Fetches the type structure (fields) for CMS pages for a given Page Type and mode.
     */
    public getStructureByTypeAndMode(
        typeCode: string,
        mode: string,
        getWholeStructure = false
    ): Promise<CMSItemStructure | CMSItemStructureField[]> {
        return this.structureRestService
            .get<{ componentTypes: CMSItemStructure[] }>({
                code: typeCode,
                mode
            })
            .then((result) => {
                const structure = result.componentTypes[0];
                return !structure || getWholeStructure ? structure : structure.attributes;
            });
    }

    /**
     * Fetches structures supported in the given category.
     */
    public getStructuresByCategory(category: StructureTypeCategory): Promise<CMSItemStructure[]> {
        return this.structureRestService
            .get<{ componentTypes: CMSItemStructure[] }>({
                category
            })
            .then((result) => result.componentTypes);
    }
}
