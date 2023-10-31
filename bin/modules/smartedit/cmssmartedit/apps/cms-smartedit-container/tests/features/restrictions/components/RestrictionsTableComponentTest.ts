/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { RestrictionsTableComponent } from 'cmssmarteditcontainer/components/restrictions/components';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';
import { PageRestrictionsCriteriaService } from 'cmssmarteditcontainer/services/pageRestrictions/PageRestrictionsCriteriaService';

describe('RestrictionTableComponent', () => {
    let component: RestrictionsTableComponent;
    let pageRestrictionsCriteriaService: jasmine.SpyObj<PageRestrictionsCriteriaService>;

    const mockRestrictions = [
        {
            uuid: 'uuid1',
            name: 'name1',
            typeCode: 'typeCode1',
            description: 'description1',
            canBeEdited: true
        },
        {
            uuid: 'uuid2',
            name: 'name2',
            typeCode: 'typeCode2',
            description: 'description2',
            canBeEdited: true
        },
        {
            uuid: 'uuid3',
            name: 'name3',
            typeCode: 'typeCode3',
            description: 'description3',
            canBeEdited: false
        }
    ] as unknown as RestrictionCMSItem[];

    const mockCriteria = [
        { id: '1', editLabel: '1', label: '1', value: true },
        { id: '2', editLabel: '2', label: '2', value: true },
        { id: '3', editLabel: '3', label: '3', value: true }
    ];

    beforeEach(() => {
        pageRestrictionsCriteriaService = jasmine.createSpyObj<PageRestrictionsCriteriaService>(
            'pageRestrictionsCriteriaService',
            ['getRestrictionCriteriaOptions']
        );

        pageRestrictionsCriteriaService.getRestrictionCriteriaOptions.and.returnValue(mockCriteria);

        component = new RestrictionsTableComponent(pageRestrictionsCriteriaService);
    });

    describe('initialize', () => {
        it('should set previous restrictions editability, actions and criteria options', () => {
            component.restrictions = mockRestrictions;
            component.ngOnInit();

            expect((component as any).oldRestrictionsEditability).toEqual([true, true, false]);
            expect(component.defaultActions).toEqual([
                {
                    key: 'se.cms.restrictions.item.edit',
                    callback: jasmine.any(Function)
                },
                {
                    key: 'se.cms.restrictions.item.remove',
                    callback: jasmine.any(Function),
                    customCss: 'se-dropdown-item__delete'
                }
            ]);
            const restrictionActions = component.restrictions.map((r) => r.actions);
            expect(restrictionActions).toEqual([
                [
                    {
                        key: 'se.cms.restrictions.item.edit',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ],
                [
                    {
                        key: 'se.cms.restrictions.item.edit',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ],
                [
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ]
            ]);
            expect(component.criteriaOptions).toEqual(mockCriteria);
        });
    });

    describe('on change', () => {
        it('GIVEN restriction editability changed THEN should update restriction actions AND update old restrictions editability', () => {
            component.restrictions = mockRestrictions;
            (component as any).oldRestrictionsEditability = [true, true, true];

            component.ngOnChanges();

            expect((component as any).oldRestrictionsEditability).toEqual([true, true, false]);
            const restrictionActions = component.restrictions.map((r) => r.actions);
            expect(restrictionActions).toEqual([
                [
                    {
                        key: 'se.cms.restrictions.item.edit',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ],
                [
                    {
                        key: 'se.cms.restrictions.item.edit',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ],
                [
                    {
                        key: 'se.cms.restrictions.item.remove',
                        callback: jasmine.any(Function),
                        customCss: 'se-dropdown-item__delete'
                    }
                ]
            ]);
        });
    });

    describe('resetRestrictionCriteria', () => {
        it('GIVEN there are no restrictions THEN it should set default restriction criteria', () => {
            component.restrictions = null;
            component.ngOnInit();

            expect(component.restrictionCriteria).toEqual(mockCriteria[0]);
        });

        it('GIVEN there less than 2 restrictions THEN it should set default restriction criteria', () => {
            component.restrictions = [mockRestrictions[0]];
            component.ngOnInit();

            expect(component.restrictionCriteria).toEqual(mockCriteria[0]);
        });
    });

    describe('removeRestriction', () => {
        it('should emit index of restriction to be removed, clear unnecessary errors and fix errors positions', () => {
            spyOn(component.onRemove, 'emit');
            component.restrictions = mockRestrictions;
            component.errors = [
                { message: 'error0', position: 0 },
                { message: 'error1', position: 1 },
                { message: 'error2', position: 2 }
            ];
            component.ngOnInit();

            component.removeRestriction(mockRestrictions[1]);

            expect(component.onRemove.emit).toHaveBeenCalledWith(1);
            expect(component.errors).toEqual([
                { message: 'error0', position: 0 },
                { message: 'error2', position: 1 }
            ]);
        });
    });

    describe('editRestriction', () => {
        it('should emit restriction to be edited', () => {
            spyOn(component.onEdit, 'emit');
            component.restrictions = mockRestrictions;
            component.ngOnInit();

            component.editRestriction(mockRestrictions[1]);

            expect(component.onEdit.emit).toHaveBeenCalledWith(mockRestrictions[1]);
        });
    });

    describe('isInError', () => {
        it('should return true when there is an error on given position', () => {
            component.errors = [
                { message: 'err0', position: 0 },
                { message: 'err1', position: 1 },
                { message: 'err2', position: 2 }
            ];

            const actual = component.isInError(1);

            expect(actual).toEqual(true);
        });
    });

    describe('getRestrictionsEditability', () => {
        it('should return array of boolean values', () => {
            component.restrictions = mockRestrictions;

            const actual = component.getRestrictionsEditability(component.restrictions);

            expect(actual).toEqual([true, true, false]);
        });
    });
});
