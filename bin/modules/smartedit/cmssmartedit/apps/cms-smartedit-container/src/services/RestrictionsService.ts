/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { StructureTypeCategory } from 'cmscommons';
import { ModeManager, StructureModeManagerFactory } from '../dao/StructureModeManagerFactory';
import { StructuresRestService } from '../dao/StructuresRestService';
import { TypeStructureRestService } from '../dao/TypeStructureRestService';

/**
 * Used for fetching supported restrictions.
 */
@Injectable()
export class RestrictionsService {
    private readonly modeManager: ModeManager;

    constructor(
        private structuresRestService: StructuresRestService,
        private typeStructureRestService: TypeStructureRestService,
        structureModeManagerFactory: StructureModeManagerFactory
    ) {
        this.modeManager = structureModeManagerFactory.createModeManager(['add', 'edit', 'create']);
    }

    /**
     * @returns An URI of the structure for a given mode.
     */
    public getStructureApiUri(mode: string): string {
        this.modeManager.validateMode(mode);
        return this.structuresRestService.getUriForContext(mode);
    }

    /**
     * @returns An array of restriction TypeCodes that are supported by SmartEdit.
     */
    public async getSupportedRestrictionTypeCodes(): Promise<string[]> {
        const structures = await this.typeStructureRestService.getStructuresByCategory(
            StructureTypeCategory.RESTRICTION
        );
        return structures.map((structure) => structure.code);
    }
}
