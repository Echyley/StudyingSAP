import { ChangeDetectorRef } from '@angular/core';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import { SharedSlotComponent } from 'personalizationsmartedit/sharedSlotDecorator/SharedSlotComponent';
import { ContextualMenuItemData, CrossFrameEventService } from 'smarteditcommons';

describe('SharedSlotComponent', () => {
    let sharedSlotComponent: SharedSlotComponent;
    let persoComponentHandlerService: jasmine.SpyObj<PersonalizationsmarteditComponentHandlerService>;
    let contextualMenuItem: ContextualMenuItemData;
    let cfEventService: jasmine.SpyObj<CrossFrameEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let componentAny: any;
    beforeEach(() => {
        contextualMenuItem = {
            componentAttributes: {
                smarteditComponentId: 'ApparelUKHomepageFreeDelBannerComponent',
                smarteditCatalogVersionUuid: undefined,
                smarteditComponentType: undefined,
                smarteditComponentUuid: undefined,
                smarteditElementUuid: undefined
            },
            setRemainOpen: jasmine.createSpy('setRemainOpen')
        };
        sharedSlotComponent = new SharedSlotComponent(
            contextualMenuItem,
            persoComponentHandlerService,
            cfEventService,
            cdr
        );

        componentAny = sharedSlotComponent;
        persoComponentHandlerService = jasmine.createSpyObj(
            'personalizationsmarteditComponentHandlerService',
            ['isSlotShared']
        );
        cfEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', [
            'subscribe'
        ]);
    });

    beforeEach(() => {
        persoComponentHandlerService.isSlotShared.and.returnValue(Promise.resolve(true));
    });

    describe('initialize', () => {
        it('should have proper api when initialized', () => {
            expect(sharedSlotComponent.onButtonClick).toBeDefined();
            expect(sharedSlotComponent.hidePopup).toBeDefined();
            expect(sharedSlotComponent.ngOnInit).toBeDefined();
        });
    });

    it('isPopupOpened is initialized to false', () => {
        expect(sharedSlotComponent.isPopupOpened).toEqual(false);
    });

    it('GIVEN isPopupOpened is set to true WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to true', () => {
        // GIVEN
        componentAny.isPopupOpenedPreviousValue = false;
        sharedSlotComponent.isPopupOpened = true;

        // WHEN
        sharedSlotComponent.ngDoCheck();

        // THEN
        expect(componentAny.isPopupOpenedPreviousValue).toEqual(true);
    });

    it('GIVEN isPopupOpened is set to false WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to false', () => {
        // GIVEN
        componentAny.isPopupOpenedPreviousValue = true;
        sharedSlotComponent.isPopupOpened = false;

        // WHEN
        sharedSlotComponent.ngDoCheck();

        // THEN
        expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
    });

    it('GIVEN isPopupOpenedPreviousValue and isPopupOpened are both true WHEN ngDoCheck is called THEN isPopupOpenedPreviousValue will not change', () => {
        // GIVEN
        componentAny.isPopupOpenedPreviousValue = true;
        sharedSlotComponent.isPopupOpened = true;

        // WHEN
        sharedSlotComponent.ngDoCheck();

        // THEN
        expect(componentAny.isPopupOpenedPreviousValue).toEqual(true);
    });

    it('GIVEN isPopupOpenedPreviousValue and isPopupOpened are both false WHEN ngDoCheck is called THEN isPopupOpenedPreviousValue will not change', () => {
        // GIVEN
        componentAny.isPopupOpenedPreviousValue = false;
        sharedSlotComponent.isPopupOpened = false;

        // WHEN
        sharedSlotComponent.ngDoCheck();

        // THEN
        expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
    });
});
