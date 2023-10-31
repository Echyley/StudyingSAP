/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    RestrictionManagementSelectModel,
    RestrictionManagementSelectModelFactory
} from 'cmssmarteditcontainer/components/restrictions/services';

import { IRestrictionType } from 'cmssmarteditcontainer/dao/RestrictionTypesRestService';
import { ICatalogService, CmsitemsRestService, CMSItem } from 'smarteditcommons';

describe('RestrictionManagementSelectModelFactory', () => {
    let factory: RestrictionManagementSelectModelFactory;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    let fetchRestrictionTypesMock: jasmine.Spy;
    let getSupportedRestrictionTypesMock: jasmine.Spy;

    const cmsRestrictionsMock: CMSItem[] = [
        {
            uid: 'restriction1',
            name: 'r1',
            uuid: 'uuid1',
            typeCode: 'code1',
            catalogVersion: 'Staged'
        },
        {
            uid: 'restriction2',
            name: 'r2',
            uuid: 'uuid2',
            typeCode: 'code2',
            catalogVersion: 'Staged'
        },
        {
            uid: 'restriction3',
            name: 'r3',
            uuid: 'uuid3',
            typeCode: 'code3',
            catalogVersion: 'Staged'
        }
    ];
    const cmsPaginationMock = {
        count: 3,
        page: 1,
        totalCount: 3,
        totalPages: 1
    };

    const restrictionTypes: IRestrictionType[] = [
        {
            code: 'type1',
            name: {
                en: 'name1'
            }
        },
        {
            code: 'type2',
            name: {
                en: 'name2'
            }
        },
        {
            code: 'type3',
            name: {
                en: 'name3'
            }
        }
    ];

    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get'
        ]);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionUUid'
        ]);

        fetchRestrictionTypesMock = jasmine.createSpy();
        getSupportedRestrictionTypesMock = jasmine.createSpy();
        cmsitemsRestService.get.and.returnValue(
            Promise.resolve({
                response: cmsRestrictionsMock,
                pagination: cmsPaginationMock
            })
        );
        catalogService.getCatalogVersionUUid.and.returnValue(Promise.resolve('staged'));

        factory = new RestrictionManagementSelectModelFactory(cmsitemsRestService, catalogService);
    });

    it('should create instance', () => {
        fetchRestrictionTypesMock.and.returnValue(Promise.resolve([]));
        const instance = factory.createRestrictionManagementSelectModel(
            fetchRestrictionTypesMock,
            getSupportedRestrictionTypesMock
        );

        expect(instance instanceof RestrictionManagementSelectModel).toEqual(true);
    });

    describe('RestrictionManagementSelectModel', () => {
        let factoryClass: RestrictionManagementSelectModel;

        beforeEach(async () => {
            fetchRestrictionTypesMock.and.returnValue(Promise.resolve(restrictionTypes));
            getSupportedRestrictionTypesMock.and.returnValue(
                Promise.resolve(['type1', 'type2', 'type3', 'type4'])
            );

            factoryClass = factory.createRestrictionManagementSelectModel(
                fetchRestrictionTypesMock,
                getSupportedRestrictionTypesMock
            );
            await factoryClass.initialize();
        });

        describe('initialize', () => {
            it('WHEN supported restriction types function is not defined THEN it should set all restriction types as supported once and give them unique id', async () => {
                factoryClass = factory.createRestrictionManagementSelectModel(
                    fetchRestrictionTypesMock,
                    null
                );
                await factoryClass.initialize();

                expect((factoryClass as any).model.restrictionTypes).toEqual(
                    restrictionTypes.map((r, i) => ({ ...r, id: i }))
                );
                expect((factoryClass as any).supportedRestrictionTypes).toEqual([
                    'type1',
                    'type2',
                    'type3'
                ]);
            });

            it('WHEN supported restriction types function is defined THEN it should set them as supported restrictions', () => {
                expect((factoryClass as any).model.restrictionTypes).toEqual(
                    restrictionTypes.map((r, i) => ({ ...r, id: i }))
                );
                expect((factoryClass as any).supportedRestrictionTypes).toEqual([
                    'type1',
                    'type2',
                    'type3',
                    'type4'
                ]);
            });
        });

        describe('getRestrictionsPaged', () => {
            beforeEach(() => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: 'code'
                };
            });

            it('WHEN current page is greater than 0 THEN it should set restrictions and return object with pagination and mapped results', async () => {
                const actual = await factoryClass.getRestrictionsPaged('', 4, 1);

                expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                    pageSize: 4,
                    currentPage: 1,
                    typeCode: 'code',
                    mask: '',
                    itemSearchParams: ''
                });
                expect(actual.pagination).toEqual(cmsPaginationMock);
                expect(actual.results).toEqual(
                    cmsRestrictionsMock.map((r) => ({ ...r, id: r.uid }))
                );
            });

            it('GIVEN there are restrictions already WHEN current page is greater than 0 THEN it should set restrictions and return object with pagination and latest mapped results', async () => {
                // setting up previous values, like simulating page change
                await factoryClass.getRestrictionsPaged('', 4, 1);

                const actual = await factoryClass.getRestrictionsPaged('', 4, 1);

                const expectedResults = cmsRestrictionsMock
                    .concat(cmsRestrictionsMock)
                    .map((r) => ({ ...r, id: r.uid }));

                expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                    pageSize: 4,
                    currentPage: 1,
                    typeCode: 'code',
                    mask: '',
                    itemSearchParams: ''
                });
                expect(actual.pagination).toEqual(cmsPaginationMock);
                expect(actual.results).toEqual(
                    cmsRestrictionsMock.map((r) => ({ ...r, id: r.uid }))
                );
                expect((factoryClass as any).restrictions).toEqual(expectedResults);
            });

            it('GIVEN there are restrictions already WHEN current page is 0 THEN it should clear restrictions, set restrictions and return object with pagination and mapped results', async () => {
                // setting up previous values
                await factoryClass.getRestrictionsPaged('', 4, 1);

                expect((factoryClass as any).restrictions).toEqual(
                    cmsRestrictionsMock.map((r) => ({ ...r, id: r.uid }))
                );

                const actual = await factoryClass.getRestrictionsPaged('', 4, 0);

                expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                    pageSize: 4,
                    currentPage: 1,
                    typeCode: 'code',
                    mask: '',
                    itemSearchParams: ''
                });
                expect(actual.pagination).toEqual(cmsPaginationMock);
                expect(actual.results).toEqual(
                    cmsRestrictionsMock.map((r) => ({ ...r, id: r.uid }))
                );
                // no extra values as it was cleared
                expect((factoryClass as any).restrictions).toEqual(
                    cmsRestrictionsMock.map((r) => ({ ...r, id: r.uid }))
                );
            });
        });

        describe('getRestrictionTypes', () => {
            it('WHEN called THEN it should return restriction types', async () => {
                const actual = await factoryClass.getRestrictionTypes();

                expect(actual).toEqual(restrictionTypes.map((r, i) => ({ ...r, id: i })));
            });
        });

        describe('restrictionSelected', () => {
            beforeEach(async () => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: 'code'
                };
                await factoryClass.getRestrictionsPaged('', 4, 1);
            });

            it('WHEN the restriction in selectedIds is not defined THEN it should return false', () => {
                factoryClass.selectedIds.restriction = null;

                const actual = factoryClass.restrictionSelected();

                expect(actual).toEqual(false);
            });

            it('WHEN the restriction in selectedIds is defined AND this restriction exists in restrictions list THEN it should be selected and return true', () => {
                factoryClass.selectedIds.restriction = cmsRestrictionsMock[0].uid;

                const actual = factoryClass.restrictionSelected();

                expect(actual).toEqual(true);
                expect(factoryClass.getRestriction()).toEqual({
                    ...cmsRestrictionsMock[0],
                    id: cmsRestrictionsMock[0].uid
                });
            });

            it('WHEN the restriction in selectedIds is defined AND this restriction exists in restrictions list THEN is should be NOT selected and return true', () => {
                factoryClass.selectedIds.restriction = 'restriction5';

                const actual = factoryClass.restrictionSelected();

                expect(actual).toEqual(true);
                expect(factoryClass.getRestriction()).toEqual(undefined);
            });
        });

        describe('restrictionTypeSelected', () => {
            it('WHEN restriction type is found by selectedIds restriction type THEN it should reset selectedIds restriction, set selected restriction and return true', () => {
                factoryClass.selectedIds.restrictionType = 1;

                const actual = factoryClass.restrictionTypeSelected();

                expect(actual).toEqual(true);
                expect(factoryClass.getRestrictionTypeCode()).toEqual(restrictionTypes[1].code);
            });

            it('WHEN restriction type is not found by selectedIds restriction type THEN it should reset selectedIds restriction, set return false', () => {
                factoryClass.selectedIds.restrictionType = 5;

                const actual = factoryClass.restrictionTypeSelected();

                expect(actual).toEqual(false);
                expect(factoryClass.getRestrictionTypeCode()).toEqual(undefined);
            });
        });

        describe('createRestrictionSelected', () => {
            it('WHEN called THEN it should set selected restriction values', async () => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: 'code'
                };

                await factoryClass.createRestrictionSelected('name', { context: 'context' });

                expect(factoryClass.getRestriction()).toEqual({
                    itemtype: 'code',
                    name: 'name',
                    catalogVersion: 'staged'
                });
                expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith({
                    context: 'context'
                });
            });
        });

        describe('isTypeSupported', () => {
            it('WHEN restriction type code is set AND it is in supported restriction types THEN it should return true', () => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: 'type1'
                };

                const actual = factoryClass.isTypeSupported();

                expect(actual).toEqual(true);
            });

            it('WHEN restriction type code is set AND it is NOT in supported restriction types THEN it should return false', () => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: 'type18'
                };

                const actual = factoryClass.isTypeSupported();

                expect(actual).toEqual(false);
            });

            it('WHEN restriction type code is not set THEN it should return false', () => {
                (factoryClass as any).model.selectedRestrictionType = {
                    code: null
                };

                const actual = factoryClass.isTypeSupported();

                expect(actual).toEqual(false);
            });
        });
    });
});
