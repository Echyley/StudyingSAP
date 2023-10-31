/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, SimpleChange } from '@angular/core';
import { IComponentSharedService, ICMSComponent } from 'cmscommons';
import { ComponentItemComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/components/ComponentItemComponent';

describe('ComponentItemComponent', () => {
    let component: ComponentItemComponent;
    let componentSharedService: jasmine.SpyObj<IComponentSharedService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    beforeEach(() => {
        componentSharedService = jasmine.createSpyObj<IComponentSharedService>(
            'componentSharedService',
            ['isComponentShared']
        );
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new ComponentItemComponent(componentSharedService, cdr);
    });

    it('WHEN initialized THEN it should get info whether component is shared and set received values', async () => {
        componentSharedService.isComponentShared.and.returnValue(Promise.resolve(true));

        await component.ngOnInit();

        expect(component.isSharedComponent).toEqual(true);
    });

    it('GIVEN inputs have changed WHEN clone on drop is true and component is not clonable THEN it should disable that component', () => {
        const changes = {
            cloneOnDrop: {
                currentValue: true,
                firstChange: true,
                previousValue: null
            } as SimpleChange
        };
        component.cloneOnDrop = true;
        component.componentInfo = {
            cloneable: false
        } as ICMSComponent;

        component.ngOnChanges(changes);

        expect(component.isComponentDisabled).toEqual(true);
    });
});
