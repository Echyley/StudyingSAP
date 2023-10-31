/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    EditingRestrictionConfig,
    RestrictionPickerConfigService,
    SelectingRestrictionConfig
} from 'cmssmarteditcontainer/components/restrictions/services';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';

describe('RestrictionPickerConfigService', () => {
    let service: RestrictionPickerConfigService;

    const mockRestriction = {
        description: 'description',
        name: 'name',
        type: {
            en: 'en'
        },
        typeCode: 'code'
    } as unknown as RestrictionCMSItem;
    const mockSupportedRestrictionTypesFn = jasmine.createSpy();
    const mockRestrictionTypesFn = jasmine.createSpy();

    beforeEach(() => {
        service = new RestrictionPickerConfigService();
    });

    it('should return config for editing', () => {
        const actual = service.getConfigForEditing(
            mockRestriction,
            mockSupportedRestrictionTypesFn
        );

        expect(actual).toEqual({
            mode: 'editing',
            restriction: mockRestriction,
            getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
        });
    });

    it('should return config for selecting', () => {
        const actual = service.getConfigForSelecting(
            [mockRestriction],
            mockRestrictionTypesFn,
            mockSupportedRestrictionTypesFn
        );

        expect(actual).toEqual({
            mode: 'select',
            existingRestrictions: [mockRestriction],
            getRestrictionTypesFn: mockRestrictionTypesFn,
            getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
        });
    });

    describe('isEditingMode', () => {
        it('WHEN config mode is set to editing THEN it should return true', () => {
            const actual = service.isEditingMode({
                mode: 'editing',
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn,
                restriction: mockRestriction,
                getRestrictionTypesFn: mockRestrictionTypesFn
            });

            expect(actual).toEqual(true);
        });

        it('WHEN config mode is set to selecting THEN it should return false', () => {
            const actual = service.isEditingMode({
                mode: 'select',
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn,
                restriction: mockRestriction,
                getRestrictionTypesFn: mockRestrictionTypesFn
            });

            expect(actual).toEqual(false);
        });
    });

    describe('isSelectMode', () => {
        it('WHEN config mode is set to selecting THEN it should return true', () => {
            const actual = service.isSelectMode({
                mode: 'select',
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn,
                existingRestrictions: [mockRestriction],
                getRestrictionTypesFn: mockRestrictionTypesFn
            });

            expect(actual).toEqual(true);
        });

        it('WHEN config mode is set to editing THEN it should return false', () => {
            const actual = service.isSelectMode({
                mode: 'editing',
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn,
                existingRestrictions: [mockRestriction],
                getRestrictionTypesFn: mockRestrictionTypesFn
            });

            expect(actual).toEqual(false);
        });
    });

    describe('isValidConfig', () => {
        it('WHEN given mode is editing AND restriction is an object THEN it should return true', () => {
            const config: EditingRestrictionConfig = {
                mode: 'editing',
                restriction: mockRestriction,
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(true);
        });

        it('WHEN given mode is editing AND restriction is not an object THEN it should return false', () => {
            const config = {
                mode: 'editing',
                restriction: 'string',
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
            } as unknown as EditingRestrictionConfig;

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(false);
        });

        it('WHEN given mode is select AND getRestrictionTypesFn is a function THEN it should return true', () => {
            const config: SelectingRestrictionConfig = {
                mode: 'select',
                existingRestrictions: [mockRestriction],
                getRestrictionTypesFn: mockRestrictionTypesFn
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(true);
        });

        it('WHEN given mode is select AND getRestrictionTypesFn is not provided THEN it should return false', () => {
            const config: SelectingRestrictionConfig = {
                mode: 'select',
                existingRestrictions: [mockRestriction]
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(false);
        });

        it('GIVEN mode is select AND getSupportedRestrictionTypesFn is provided AND both getRestrictionTypesFn and getSupportedRestrictionTypesFn are functions THEN it should return true', () => {
            const config: SelectingRestrictionConfig = {
                mode: 'select',
                existingRestrictions: [mockRestriction],
                getRestrictionTypesFn: mockRestrictionTypesFn,
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(true);
        });

        it('WHEN given mode is select, getSupportedRestrictionTypesFn is provided AND getRestrictionTypesFn provided and getSupportedRestrictionTypesFn is not THEN it should return true', () => {
            const config: SelectingRestrictionConfig = {
                mode: 'select',
                existingRestrictions: [mockRestriction],
                getRestrictionTypesFn: mockRestrictionTypesFn
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(true);
        });

        it('WHEN given mode is select, getSupportedRestrictionTypesFn is provided AND  getSupportedRestrictionTypesFn provided and getRestrictionTypesFn is not THEN it should return false', () => {
            const config: SelectingRestrictionConfig = {
                mode: 'select',
                existingRestrictions: [mockRestriction],
                getSupportedRestrictionTypesFn: mockSupportedRestrictionTypesFn
            };

            const actual = service.isValidConfig(config);

            expect(actual).toEqual(false);
        });
    });
});
