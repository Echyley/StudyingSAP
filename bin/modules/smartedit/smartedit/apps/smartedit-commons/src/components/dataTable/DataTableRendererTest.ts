/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injector, SimpleChange } from '@angular/core';
import { createSimulateNgOnChanges } from 'testhelpers';
import { DataTableRendererComponent } from './DataTableRenderer';

describe('DataTableRenderer', () => {
    class MockComponent {}
    let injector: jasmine.SpyObj<Injector>;
    let component: DataTableRendererComponent;

    type Input = Partial<Pick<typeof component, 'column' | 'item'>>;
    let simulateNgOnChanges: ReturnType<typeof createSimulateNgOnChanges>;
    beforeEach(() => {
        injector = jasmine.createSpyObj<Injector>('injector', ['get']);

        component = new DataTableRendererComponent(injector);
        simulateNgOnChanges = createSimulateNgOnChanges<Input>(component);
    });

    it('GIVEN component is provided THEN it will create an injector with proper data', () => {
        const column = {
            component: MockComponent,
            property: 'itemtype',
            i18n: 'se.cms.pagelist.headerpagetype',
            sortable: true
        };

        expect(component.componentInjector).toBeUndefined();

        simulateNgOnChanges({
            column: new SimpleChange(undefined, column, false),
            item: new SimpleChange(undefined, {}, false)
        });

        expect(component.componentInjector).toBeDefined();
    });
});
