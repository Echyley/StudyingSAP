/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { RestrictionManagementEditComponent } from 'cmssmarteditcontainer/components/restrictions/components';
import { RestrictionsService } from 'cmssmarteditcontainer/services/RestrictionsService';
import { CMSItem } from 'smarteditcommons';

describe('RestrictionManagementEditComponent', () => {
    let component: RestrictionManagementEditComponent;
    let restrictionsService: jasmine.SpyObj<RestrictionsService>;

    beforeEach(() => {
        restrictionsService = jasmine.createSpyObj<RestrictionsService>('restrictionsService', [
            'getStructureApiUri'
        ]);

        restrictionsService.getStructureApiUri.and.returnValue('structure_api_string_value');

        component = new RestrictionManagementEditComponent(restrictionsService);
    });

    it('should have some of fields set when instance is created', () => {
        expect(component.ready).toEqual(false);
        expect(component.itemManagementMode).toEqual('edit');
        expect(component.structureApi).toEqual('structure_api_string_value');
    });

    it('GIVEN restriction input is null THEN it should fallback to empty object', async () => {
        await component.ngOnInit();

        expect(component.restriction).toEqual({} as unknown as CMSItem);
    });

    it('GIVEN method for getting supported restriction types is not defined THEN it should emit proper actions', async () => {
        const submitSpy = spyOn(component.submitFnChange, 'emit');
        const isDirtySpy = spyOn(component.isDirtyFnChange, 'emit');

        component.getSupportedRestrictionTypes = undefined;
        component.submitInternal = jasmine
            .createSpy()
            .and.returnValue(Promise.resolve({ foo: 'bar' }));
        component.isDirtyInternal = jasmine.createSpy().and.returnValue(true);

        await component.ngOnInit();

        const submitMock = submitSpy.calls.argsFor(0)[0];
        const isDirtyMock = isDirtySpy.calls.argsFor(0)[0];

        const submitResult = await submitMock();
        const isDirtyResult = isDirtyMock();

        expect(component.submitFnChange.emit).toHaveBeenCalled();
        expect(component.isDirtyFnChange.emit).toHaveBeenCalled();

        expect(submitResult).toEqual({ foo: 'bar' } as any);
        expect(isDirtyResult).toEqual(true);
    });

    it('GIVEN method for getting supported restriction types is defined AND restriction type is not in supported types THEN it should emit proper actions', async () => {
        component.getSupportedRestrictionTypes = jasmine
            .createSpy()
            .and.returnValue(Promise.resolve(['cmspage']));
        component.restriction = {
            itemtype: 'cmsitem'
        } as unknown as CMSItem;

        const submitSpy = spyOn(component.submitFnChange, 'emit');
        const isDirtySpy = spyOn(component.isDirtyFnChange, 'emit');

        await component.ngOnInit();

        const submitMock = submitSpy.calls.argsFor(0)[0];
        const isDirtyMock = isDirtySpy.calls.argsFor(0)[0];

        const submitResult = await submitMock();
        const isDirtyResult = isDirtyMock();

        expect(component.submitFnChange.emit).toHaveBeenCalled();
        expect(component.isDirtyFnChange.emit).toHaveBeenCalled();

        expect(submitResult).toEqual(null);
        expect(isDirtyResult).toEqual(false);
    });

    it('GIVEN method for getting supported restriction types is defined AND restriction type is in supported types THEN it should emit proper actions', async () => {
        component.getSupportedRestrictionTypes = jasmine
            .createSpy()
            .and.returnValue(Promise.resolve(['cmsitem']));
        component.restriction = {
            itemtype: 'cmsitem'
        } as unknown as CMSItem;
        component.submitInternal = jasmine
            .createSpy()
            .and.returnValue(Promise.resolve({ foo: 'bar' }));
        component.isDirtyInternal = jasmine.createSpy().and.returnValue(true);

        const submitSpy = spyOn(component.submitFnChange, 'emit');
        const isDirtySpy = spyOn(component.isDirtyFnChange, 'emit');

        await component.ngOnInit();

        const submitMock = submitSpy.calls.argsFor(0)[0];
        const isDirtyMock = isDirtySpy.calls.argsFor(0)[0];

        const submitResult = await submitMock();
        const isDirtyResult = isDirtyMock();

        expect(component.submitFnChange.emit).toHaveBeenCalled();
        expect(component.isDirtyFnChange.emit).toHaveBeenCalled();

        expect(submitResult).toEqual({ foo: 'bar' } as any);
        expect(isDirtyResult).toEqual(true);
    });

    it('GIVEN component initialization has finished THEN it should mark it as ready', async () => {
        await component.ngOnInit();

        expect(component.ready).toEqual(true);
    });
});
