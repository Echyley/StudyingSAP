/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSItemStructure, StructureTypeCategory } from 'cmscommons';
import { TypeStructureRestService } from 'cmssmarteditcontainer/dao/TypeStructureRestService';
import { IRestService, RestServiceFactory } from 'smarteditcommons';

describe('TypeStructureRestService - ', () => {
    const mockStructure: CMSItemStructure = {
        attributes: [
            {
                cmsStructureType: 'ShortString',
                collection: false,
                editable: true,
                i18nKey: 'type.abstractpage.description.name',
                localized: true,
                paged: false,
                qualifier: 'description',
                required: false
            }
        ],
        category: 'PAGE',
        code: 'CategoryPage',
        i18nKey: 'type.categorypage.name',
        name: 'Category Page',
        type: 'categoryPageData'
    };

    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;

    let structureRestService: jasmine.SpyObj<
        IRestService<{ componentTypes: CMSItemStructure[] } | CMSItemStructure>
    >;

    let service: TypeStructureRestService;
    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        structureRestService = jasmine.createSpyObj<IRestService<any>>('restService', [
            'get',
            'getById'
        ]);
        restServiceFactory.get.and.returnValue(structureRestService);

        service = new TypeStructureRestService(restServiceFactory);
    });

    it('getStructureByType GIVEN typeCode THEN it will return an array of structure fields', async () => {
        const typeCode = 'CategoryPage';

        structureRestService.getById.and.returnValue(Promise.resolve(mockStructure));

        const structureFields = await service.getStructureByType(typeCode);

        expect(structureRestService.getById).toHaveBeenCalledWith(typeCode);
        expect(structureFields).toBe(mockStructure.attributes);
    });

    describe('getStructureByTypeAndMode', () => {
        const typeCode = 'CategoryPage';
        const mode = 'DEFAULT';
        const mockResponse: { componentTypes: CMSItemStructure[] } = {
            componentTypes: [mockStructure]
        };
        beforeEach(() => {
            structureRestService.get.and.returnValue(Promise.resolve(mockResponse));
        });

        it('GIVEN typeCode AND mode THEN it will return an array of structure fields', async () => {
            const structureFields = await service.getStructureByTypeAndMode(typeCode, mode);

            expect(structureRestService.get).toHaveBeenCalledWith({ code: typeCode, mode });
            expect(structureFields).toBe(mockResponse.componentTypes[0].attributes);
        });

        it('GIVEN typeCode AND mode AND getWholeStructure THEN it will return Item Structure', async () => {
            const itemStructures = await service.getStructureByTypeAndMode(typeCode, mode, true);

            expect(structureRestService.get).toHaveBeenCalledWith({ code: typeCode, mode });
            expect(itemStructures).toBe(mockResponse.componentTypes[0]);
        });
    });

    it('getStructuresByCategory GIVEN category THEN it will return an array of Item Structures', async () => {
        const category = StructureTypeCategory.COMPONENT;
        const mockResponse: { componentTypes: CMSItemStructure[] } = {
            componentTypes: [mockStructure]
        };
        structureRestService.get.and.returnValue(Promise.resolve(mockResponse));

        const itemStructures = await service.getStructuresByCategory(category);

        expect(structureRestService.get).toHaveBeenCalledWith({ category });
        expect(itemStructures).toBe(mockResponse.componentTypes);
    });
});
