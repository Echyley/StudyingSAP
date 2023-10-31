/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CmsDropdownItemComponent, CMSLinkDropdownItem } from 'cmssmarteditcontainer';
import { SystemEventService, ItemComponentData } from 'smarteditcommons';

describe('CmsDropdownItemComponent', () => {
    let component: CmsDropdownItemComponent;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let stopPropagation: jasmine.Spy;
    let event: Event;

    const mockItem = {
        id: 'id',
        linkTo: 'something',
        slotId: 'slotId'
    } as CMSLinkDropdownItem;

    beforeEach(() => {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);
        stopPropagation = jasmine.createSpy();
        event = { stopPropagation } as unknown as Event;

        component = new CmsDropdownItemComponent(
            {
                item: mockItem,
                select: {
                    id: 'name'
                },
                selected: true
            } as ItemComponentData<any>,
            systemEventService
        );
    });

    describe('onClick', () => {
        it('WHEN element is clicked AND is selected THEN it should publish an event', () => {
            component.isSelected = true;
            component.item = mockItem;
            component.qualifier = 'name';

            component.onClick(event);

            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                'ON_EDIT_NESTED_COMPONENT',
                {
                    qualifier: 'name',
                    item: mockItem
                }
            );
        });

        it('WHEN element is clicked AND is NOT selected THEN it should not publish an event', () => {
            component.isSelected = false;
            component.item = mockItem;
            component.qualifier = 'name';

            component.onClick(event);

            expect(systemEventService.publishAsync).not.toHaveBeenCalled();
        });
    });
});
