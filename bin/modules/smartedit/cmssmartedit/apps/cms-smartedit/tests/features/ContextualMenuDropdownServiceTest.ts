/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DRAG_AND_DROP_EVENTS } from 'cmscommons';
import { ContextualMenuDropdownService } from 'cmssmartedit/services/ContextualMenuDropdownService';
import { CLOSE_CTX_MENU, CTX_MENU_DROPDOWN_IS_OPEN, SystemEventService } from 'smarteditcommons';

describe('ContextualMenuDropdownService', () => {
    let service: ContextualMenuDropdownService;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    beforeEach(() => {
        systemEventService = jasmine.createSpyObj('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);

        service = new ContextualMenuDropdownService(systemEventService);
    });

    describe('GIVEN registerIsOpenEvent WHEN is called', () => {
        it('THEN systemEventService subscribes to CTX_MENU_DROPDOWN_IS_OPEN', () => {
            service.registerIsOpenEvent();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                CTX_MENU_DROPDOWN_IS_OPEN,
                jasmine.any(Function)
            );
        });

        it('THEN systemEventService subscribes to DRAG_STARTED after subscribing to CTX_MENU_DROPDOWN_IS_OPEN', () => {
            service.registerIsOpenEvent();

            const callbackFn = systemEventService.subscribe.calls.argsFor(0)[1] as () => void;

            callbackFn();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                DRAG_AND_DROP_EVENTS.DRAG_STARTED,
                jasmine.any(Function)
            );
        });

        it('THEN systemEventService after subscribing to CTX_MENU_DROPDOWN_IS_OPEN and DRAG_STARTED calls pubslishAnyc CLOSE_CTX_MENU', () => {
            service.registerIsOpenEvent();

            const callbackFn = systemEventService.subscribe.calls.argsFor(0)[1] as () => void;

            callbackFn();

            const secondCallbackFn = systemEventService.subscribe.calls.argsFor(1)[1] as () => void;

            secondCallbackFn();

            expect(systemEventService.publishAsync).toHaveBeenCalledWith(CLOSE_CTX_MENU);
        });
    });
});
