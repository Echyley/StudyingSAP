/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentSearchComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/components';
import { SystemEventService } from 'smarteditcommons';

describe('ComponentSearchComponent', () => {
    let component: ComponentSearchComponent;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    let unsubscribe: jasmine.Spy;

    beforeEach(() => {
        unsubscribe = jasmine.createSpy();
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        systemEventService.subscribe.and.returnValue(unsubscribe);

        component = new ComponentSearchComponent(systemEventService);

        component.placeholderI18nKey = 'i18nKey';
    });

    it('WHEN initialized THEN should set default values, reset search query and subscribe to event', () => {
        component.ngOnInit();

        expect(component.placeholderI18nKey).toEqual('i18nKey');
        expect(component.searchQuery).toEqual('');
        expect(component.showResetButton).toEqual(false);
    });

    it('GIVEN component was initialized WHEN it gets destroyed THEN is should unsubscribe from events', () => {
        spyOn((component as any).searchQuerySubscription, 'unsubscribe');

        component.ngOnInit();

        component.ngOnDestroy();

        expect((component as any).searchQuerySubscription.unsubscribe).toHaveBeenCalled();
        expect(unsubscribe).toHaveBeenCalled();
    });

    it('WHEN search query is reset THEN it should clear search query, emit empty value and hide reset button', () => {
        spyOn(component.onChange, 'emit');
        component.searchQuery = 'search';
        component.showResetButton = true;

        const mockEvent = {
            stopPropagation: jasmine.createSpy()
        } as unknown as Event;

        component.resetSearch(mockEvent);

        expect(mockEvent.stopPropagation).toHaveBeenCalled();
        expect(component.showResetButton).toEqual(false);
        expect(component.searchQuery).toEqual('');
        expect(component.onChange.emit).toHaveBeenCalledWith('');
    });

    describe('WHEN onSearchQueryChange is called THEN', () => {
        const clock = jasmine.clock();
        beforeEach(() => {
            spyOn(component.onChange, 'emit');
            clock.install();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('should update query value, emit it AND show reset button value', () => {
            component.onSearchQueryChange('new val');

            clock.tick(501);

            expect(component.searchQuery).toEqual('new val');
            expect(component.onChange.emit).toHaveBeenCalledWith('new val');
            expect(component.showResetButton).toEqual(true);
        });

        it('should update only recent value but set show reset button value immediately', () => {
            component.onSearchQueryChange('val1');
            clock.tick(499);

            expect(component.searchQuery).toEqual('');
            expect(component.onChange.emit).not.toHaveBeenCalled();
            expect(component.showResetButton).toEqual(true);

            component.onSearchQueryChange('val2');
            clock.tick(2); // so we are at 501 (499 + 2);
            expect(component.searchQuery).toEqual('');
            expect(component.onChange.emit).not.toHaveBeenCalled();
            expect(component.showResetButton).toEqual(true);

            clock.tick(500);
            expect(component.searchQuery).toEqual('val2');
            expect(component.onChange.emit).toHaveBeenCalledWith('val2');
            expect(component.showResetButton).toEqual(true);
        });
    });
});
