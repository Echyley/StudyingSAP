/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { SharedComponentButtonComponent, IContextAwareEditableItemService } from 'cmscommons';
import { ComponentAttributes } from 'smarteditcommons';

describe('SharedComponentButtonComponent', () => {
    let item: ComponentAttributes;
    let contextAwareEditableItemService: jasmine.SpyObj<IContextAwareEditableItemService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: SharedComponentButtonComponent;
    beforeEach(() => {
        item = {
            smarteditComponentId: 'ApparelUKHomepageFreeDelBannerComponent',
            smarteditCatalogVersionUuid: undefined,
            smarteditComponentType: undefined,
            smarteditComponentUuid: undefined,
            smarteditElementUuid: undefined
        };
        contextAwareEditableItemService = jasmine.createSpyObj<IContextAwareEditableItemService>(
            'contextAwareEditableItemService',
            ['isItemEditable']
        );
        component = new SharedComponentButtonComponent(
            { componentAttributes: item, setRemainOpen: undefined },
            contextAwareEditableItemService,
            cdr
        );
    });

    describe('initialize', () => {
        it('GIVEN component is editable THEN it sets the message properly and marks state as ready', async () => {
            contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(true));

            await component.ngOnInit();

            expect(component.isReady).toBe(true);
            expect(component.message).toBe('se.cms.contextmenu.shared.component.info.msg.editable');
        });

        it('GIVEN component is not editable THEN it sets the message properly and marks state as ready', async () => {
            contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.isReady).toBe(true);
            expect(component.message).toBe('se.cms.contextmenu.shared.component.info.msg');
        });
    });
});
