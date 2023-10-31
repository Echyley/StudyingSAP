/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { BaseWrapperComponent } from 'cmssmarteditcontainer/components/pages/pageListComponentWrappers/BaseWrapperComponent';
import { DataTableComponentData } from 'smarteditcommons';

describe('BaseWrapperComponent', () => {
    let component: BaseWrapperComponent;

    const dropdownData: DataTableComponentData = {
        column: {
            sortable: true,
            i18n: 'se.key',
            property: 'prop'
        },
        item: {
            uid: 'uid'
        }
    };

    beforeEach(() => {
        component = new BaseWrapperComponent(dropdownData);

        component.ngOnInit();
    });

    it('GIVEN component is initialized THEN it should have item property', () => {
        expect((component as any).item).toEqual({ uid: 'uid' });
    });
});
