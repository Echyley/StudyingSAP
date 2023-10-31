/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { RestrictionManagementComponent } from 'cmssmarteditcontainer/components/restrictions/components';
import { RestrictionPickerConfigService } from 'cmssmarteditcontainer/components/restrictions/services';

describe('RestrictionManagementComponent', () => {
    let component: RestrictionManagementComponent;
    let restrictionPickerConfigService: jasmine.SpyObj<RestrictionPickerConfigService>;

    beforeEach(() => {
        restrictionPickerConfigService = jasmine.createSpyObj<RestrictionPickerConfigService>(
            'restrictionPickerConfigService',
            ['isValidConfig', 'isEditingMode']
        );

        component = new RestrictionManagementComponent(restrictionPickerConfigService);
    });

    it('WHEN initialized THEN it should emit submit and is dirty function', () => {
        jasmine.clock().install();
        spyOn(component.isDirtyFnChange, 'emit');
        spyOn(component.submitFnChange, 'emit');

        component.ngOnInit();

        // simulating setTimeout
        jasmine.clock().tick(1);

        expect(component.isDirtyFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
        expect(component.submitFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
        jasmine.clock().uninstall();
    });

    describe('On Change', () => {
        it('WHEN there is no config provided it should do nothing', () => {
            component.config = null;

            component.ngOnChanges();

            expect(restrictionPickerConfigService.isEditingMode).not.toHaveBeenCalled();
            expect(restrictionPickerConfigService.isValidConfig).not.toHaveBeenCalled();
            expect(component.isEditMode).toEqual(false);
            expect(component.isReady).toEqual(false);
        });

        it('GIVEN config is valid WHEN change takes place THEN it should mark component as initialized and determine whether is edit mode', () => {
            restrictionPickerConfigService.isValidConfig.and.returnValue(true);
            restrictionPickerConfigService.isEditingMode.and.returnValue(true);
            component.config = { existingRestrictions: [], mode: 'edit', restriction: null };

            component.ngOnChanges();

            expect(restrictionPickerConfigService.isEditingMode).toHaveBeenCalled();
            expect(restrictionPickerConfigService.isValidConfig).toHaveBeenCalled();
            expect(component.isEditMode).toEqual(true);
            expect(component.isReady).toEqual(true);
        });

        it('GIVEN config is invalid WHEN change takes place THEN it should throw an error', () => {
            restrictionPickerConfigService.isValidConfig.and.returnValue(false);
            restrictionPickerConfigService.isEditingMode.and.returnValue(true);
            component.config = { existingRestrictions: [], mode: 'edit', restriction: null };

            expect(() => component.ngOnChanges()).toThrow(
                'RestrictionManagementComponent - invalid restrictionPickerConfig'
            );

            expect(restrictionPickerConfigService.isValidConfig).toHaveBeenCalled();
            expect(restrictionPickerConfigService.isEditingMode).not.toHaveBeenCalled();
            expect(component.isEditMode).toEqual(false);
            expect(component.isReady).toEqual(false);
        });
    });
});
