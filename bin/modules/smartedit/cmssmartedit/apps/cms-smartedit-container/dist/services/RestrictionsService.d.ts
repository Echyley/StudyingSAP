import { StructureModeManagerFactory } from '../dao/StructureModeManagerFactory';
import { StructuresRestService } from '../dao/StructuresRestService';
import { TypeStructureRestService } from '../dao/TypeStructureRestService';
/**
 * Used for fetching supported restrictions.
 */
export declare class RestrictionsService {
    private structuresRestService;
    private typeStructureRestService;
    private readonly modeManager;
    constructor(structuresRestService: StructuresRestService, typeStructureRestService: TypeStructureRestService, structureModeManagerFactory: StructureModeManagerFactory);
    /**
     * @returns An URI of the structure for a given mode.
     */
    getStructureApiUri(mode: string): string;
    /**
     * @returns An array of restriction TypeCodes that are supported by SmartEdit.
     */
    getSupportedRestrictionTypeCodes(): Promise<string[]>;
}
