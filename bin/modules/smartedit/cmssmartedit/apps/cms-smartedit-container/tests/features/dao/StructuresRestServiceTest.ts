/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { StructuresRestService } from 'cmssmarteditcontainer/dao/StructuresRestService';
import { OperationContextService } from 'smarteditcommons';

describe('StructuresRestService', () => {
    let operationContextService: jasmine.SpyObj<OperationContextService>;

    let service: StructuresRestService;
    beforeEach(() => {
        operationContextService = jasmine.createSpyObj<OperationContextService>(
            'operationContextService',
            ['register']
        );
        service = new StructuresRestService(operationContextService);
    });

    it('WHEN instantiated THEN it will register Types Resource URI within CMS operation context', () => {
        expect(operationContextService.register).toHaveBeenCalled();
    });

    describe('getUriForContext', () => {
        it('GIVEN mode THEN it will return URI with default type placeholder', () => {
            const uri = service.getUriForContext('add');

            expect(uri).toBe('/cmswebservices/v1/types?code=:smarteditComponentType&mode=ADD');
        });

        it('GIVEN mode AND type THEN it will return URI with type inserted into placeholder', () => {
            const uri = service.getUriForContext('add', 'CMSCategoryRestriction');

            expect(uri).toBe('/cmswebservices/v1/types?code=CMSCategoryRestriction&mode=ADD');
        });
    });
});
