/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSItemStructure } from 'cmscommons';
import {
    ModeManager,
    StructureModeManagerFactory
} from 'cmssmarteditcontainer/dao/StructureModeManagerFactory';
import { StructuresRestService } from 'cmssmarteditcontainer/dao/StructuresRestService';
import { TypeStructureRestService } from 'cmssmarteditcontainer/dao/TypeStructureRestService';
import { RestrictionsService } from 'cmssmarteditcontainer/services/RestrictionsService';

describe('RestrictionsService - ', () => {
    const MOCK_STRUCTURE_URI = '/cmswebservices/v1/types?code=:smarteditComponentType&mode=EDIT';
    const MOCK_RESTRICTION_STRUCTURES: CMSItemStructure[] = [
        {
            category: 'RESTRICTION',
            code: 'RESTRICTIONTYPE1',
            attributes: null,
            i18nKey: null,
            name: null,
            type: null
        },
        {
            category: 'RESTRICTION',
            code: 'RESTRICTIONTYPE2',
            attributes: null,
            i18nKey: null,
            name: null,
            type: null
        },
        {
            category: 'RESTRICTION',
            code: 'RESTRICTIONTYPE3',
            attributes: null,
            i18nKey: null,
            name: null,
            type: null
        }
    ];

    let structuresRestService: jasmine.SpyObj<StructuresRestService>;
    let typeStructureRestService: jasmine.SpyObj<TypeStructureRestService>;
    let structureModeManagerFactory: jasmine.SpyObj<StructureModeManagerFactory>;

    let service: RestrictionsService;
    beforeEach(() => {
        structuresRestService = jasmine.createSpyObj<StructuresRestService>(
            'structuresRestService',
            ['getUriForContext']
        );

        typeStructureRestService = jasmine.createSpyObj<TypeStructureRestService>(
            'typeStructureRestService',
            ['getStructuresByCategory']
        );

        structureModeManagerFactory = jasmine.createSpyObj<StructureModeManagerFactory>(
            'structureModeManagerFactory',
            ['createModeManager']
        );
        structureModeManagerFactory.createModeManager.and.callFake(
            (modes: string[]) => new ModeManager(modes)
        );

        service = new RestrictionsService(
            structuresRestService,
            typeStructureRestService,
            structureModeManagerFactory
        );
    });

    describe('getStructureApiUri', () => {
        it('should get the structure API URI for a valid mode', () => {
            const mode = 'edit';
            structuresRestService.getUriForContext.and.returnValue(MOCK_STRUCTURE_URI);

            const uri = service.getStructureApiUri(mode);

            expect(structuresRestService.getUriForContext).toHaveBeenCalledWith(mode);
            expect(uri).toBe(MOCK_STRUCTURE_URI);
        });

        it('GIVEN not supported mode THEN it will throw an error', () => {
            expect(() => {
                service.getStructureApiUri('notsupported');
            }).toThrow();
        });
    });

    it('getSupportedRestrictionTypeCodes - THEN it will return an array of codes', async () => {
        typeStructureRestService.getStructuresByCategory.and.returnValue(
            Promise.resolve(MOCK_RESTRICTION_STRUCTURES)
        );

        const codes = await service.getSupportedRestrictionTypeCodes();

        expect(codes).toEqual(['RESTRICTIONTYPE1', 'RESTRICTIONTYPE2', 'RESTRICTIONTYPE3']);
    });
});
